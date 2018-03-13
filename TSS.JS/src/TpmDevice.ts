/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */


let ffi = require('ffi');
let ref = require('ref');
let Struct = require('ref-struct');
let ArrayType = require('ref-array')

let byte = ref.types.byte;
let int = ref.types.int;

export var ByteArray = ArrayType(byte);


export class TpmError extends Error
{
    constructor(
        public responseCode: number,
        public tpmCommand: string,
        errorMessage: string = null)
    {
        super(errorMessage);
    }
}


let TbsContext = Struct({
    version: ref.types.int,
    params: ref.types.int
});
let PTbsContext = ref.refType(TbsContext);

export enum TSS_TPM_INFO {
    // Flags corresponding to the TpmEndPointInfo values used by the TPM simulator
    TSS_TpmPlatformAvailable = 0x01,
    TSS_TpmUsesTbs = 0x02,
    TSS_TpmInRawMode = 0x04,
    TSS_TpmSupportsPP = 0x08,

    // TPM connection type. Flags are mutually exclusive for better error checking
    TSS_SocketConn = 0x1000,
    TSS_TbsConn = 0x2000
};

export interface TpmDevice {
    connect(continuation: (err: TpmError) => void): void;
    dispatchCommand(command: TpmBuffer, continuation: (err: TpmError, response?: Buffer) => void): void;
    close(): void;
}

export class TpmLinuxDevice implements TpmDevice
{
    private static readonly InvalidHandle: number = -1;
    private static fs = null;

    public constructor(
        private devTpmHandle: number = TpmLinuxDevice.InvalidHandle
    ) {}

    public connect(continuation: (err: TpmError) => void)
    {
        if (this.devTpmHandle == TpmLinuxDevice.InvalidHandle)
        {
            if (TpmLinuxDevice.fs == null)
                TpmLinuxDevice.fs = require('fs');

            this.devTpmHandle = TpmLinuxDevice.fs.openSync('/dev/tpm0', 'rs+');
        }
        setImmediate(continuation, null);
        return null;
    }

    public dispatchCommand(command: TpmBuffer, continuation: (err: TpmError, resp?: Buffer) => void): void
    {
        let err: TpmError = null;
        let respBuf: Buffer = null;
        let numWritten: number = TpmLinuxDevice.fs.writeSync(this.devTpmHandle, command, 0, command.length, null);
        if (numWritten != command.length)
        {
            let errMsg = 'Only ' + numWritten + ' bytes written to the TPM device instead of ' + command.length;
            console.log(errMsg);
            err = new TpmError(TPM_RC.TSS_SEND_OP_FAILED, 'TpmSend', errMsg);
        }
        else
        {
            respBuf = new Buffer(4096);
            let numRead: number = TpmLinuxDevice.fs.readSync(this.devTpmHandle, respBuf, 0, respBuf.length, null);
            respBuf = respBuf.slice(0, numRead);
        }
        setImmediate(continuation, err, respBuf);
    }

    public close(): void
    {
        if (this.devTpmHandle != TpmLinuxDevice.InvalidHandle)
        {
            TpmLinuxDevice.fs.closeSync(this.devTpmHandle);
            this.devTpmHandle = TpmLinuxDevice.InvalidHandle;
        }
    }
}; // class TpmLinuxDevice

export class TpmTbsDevice implements TpmDevice
{
    public constructor(
        private tbsHandle: number = 0,
        private tbsDll = null
    ) {}

    public connect(continuation: (err: TpmError) => void)
    {
        this.tbsDll = ffi.Library('Tbs', {
                'Tbsi_Context_Create': ['int', ['pointer', 'pointer']],
                'Tbsip_Context_Close': ['int', ['int']],
                'Tbsip_Submit_Command': ['int', ['int' /*handle*/, 'int' /*locality*/, 'int' /*priority*/,
                    ByteArray /*inBuf*/, 'int' /*inBufLen*/,
                    ByteArray /*outBuf*/, 'pointer' /*outBufLen*/]]
            });

        let tbsCtx = new TbsContext();
        tbsCtx.version = 2;
        tbsCtx.params = 1 << 2;
        //var tbsHandle = ref.NULL;
        var handleOut = ref.alloc('long', 0);

        let err: TpmError = null;
        let res = this.tbsDll.Tbsi_Context_Create(tbsCtx.ref(), handleOut);
        if (res == 0)
            this.tbsHandle = handleOut.deref();
        else
            err = new TpmError(res, 'Tbsi_Context_Create', 'TBS context cretaion failed');

        setImmediate(continuation, err);
        return null;
    }

    public dispatchCommand(command: TpmBuffer, continuation: (err: TpmError, resp?: Buffer) => void): void
    {
        let respBuf = new ByteArray(4096);
        let respSizePtr = ref.alloc('int', respBuf.length);
        let cmd = new ByteArray(command);

        let err: TpmError = null;
        let resp: Buffer = null;
        let res = this.tbsDll.Tbsip_Submit_Command(this.tbsHandle, 0, 0, cmd, command.length, respBuf, respSizePtr);

        if (res == 0)
            resp = new Buffer(respBuf.toArray().slice(0, respSizePtr.deref()));
        else
            err = new TpmError(res, 'Tbsip_Submit_Command', 'Sending TPM command to TBS failed');

        setImmediate(continuation, err, resp);
    }

    public close(): void
    {
        if (this.tbsHandle != 0)
            this.tbsDll.Tbsip_Context_Close(this.tbsHandle);
    }
}; // class TpmTbsDevice


import * as Net from 'net';
//import * as TPM from "./TpmTypes.js";
import { TPM_RC } from "./TpmTypes.js";
import { TpmBuffer } from "./TpmMarshaller.js";

const ClientVer : number = 1; 


enum TPM_TCP_PROTOCOL {
    SignalPowerOn = 1,
    //SignalPowerOff = 2,
    SendCommand = 8,
    SignalNvOn = 11,
    //SignalNvOff = 12,
    HandShake = 15,
    SessionEnd = 20,
    Stop = 21,
};

export class TpmTcpDevice implements TpmDevice
{
    public tpmInfo: TSS_TPM_INFO = null;

    public constructor(host: string = '127.0.0.1', port: number = 2321)
    {
        this.host = host;
        this.port = port;
    }

    public connect(continuation: (err: TpmError) => void)
    {
        this.connectCont = continuation;
        this.tpmSocket = new Net.Socket();
        this.tpmSocket.connect(this.port, this.host, this.onConnect.bind(this));
    }

    public dispatchCommand(command: TpmBuffer, continuation: (err: TpmError, resp?: Buffer) => void): void
    {
        let cmdBuf = new TpmBuffer(command.length + 9);
        cmdBuf.toTpm(TPM_TCP_PROTOCOL.SendCommand, 4);
        cmdBuf.toTpm(0, 1);   // locality
        cmdBuf.toTpm(command.length, 4);
        command.copy(cmdBuf, cmdBuf.curPos);

        this.dispatchCont = continuation;
        this.tcpResp = new TpmBuffer(0);
        this.tpmSocket.removeAllListeners('data');
        this.tpmSocket.on('data', this.onDispatch.bind(this));
        this.tpmSocket.write(cmdBuf.buffer);
    }

    public close(): void
    {
        if (this.tpmSocket != null) {
            this.tpmSocket.end();
            this.tpmSocket.unref();
            this.tpmSocket = null;
        }
    }

    //
    // Private members
    //

    private tpmSocket: Net.Socket;

    // Scratch members
    private host: string;
    private port: number;
    private tpmPlatSocket: Net.Socket;
    private tcpResp: TpmBuffer;
    private connectCont: (err: TpmError) => void = null;
    private dispatchCont: (err: TpmError, resp?: Buffer) => void = null;

    private onConnect()
    {
        //console.log('Socket connection to the simulator established');
        this.tcpResp = new TpmBuffer(0);
        this.tpmSocket.on('data', this.onHandShake.bind(this));
        let req = new Buffer([0, 0, 0, TPM_TCP_PROTOCOL.HandShake,
                              0, 0, 0, ClientVer]);
        this.tpmSocket.write(req);
    }

    private onHandShake(lastRespFrag: Buffer)
    {
        this.tcpResp = new TpmBuffer(Buffer.concat([this.tcpResp.buffer, lastRespFrag]));
        if (this.tcpResp.length < 12)
        {
            //console.log('Incomplete response received: ' + this.tcpResp.length + ' out of 12 bytes. Continue reading...');
            return;
        }

        this.tpmSocket.removeAllListeners('data');
        if (this.tcpResp.length != 12)
        {
            setImmediate(this.connectCont, new TpmError(TPM_RC.TSS_TCP_BAD_HANDSHAKE_RESP, 'SimConnect',
                            'Wrong length of the handshake response ' + this.tcpResp.length + ' bytes instead of 12'));
            return;
        }

        let simVer: number = this.tcpResp.fromTpm(4);
        if (ClientVer != simVer)
        {
            setImmediate(this.connectCont, new TpmError(TPM_RC.TSS_TCP_SERVER_TOO_OLD, 'SimConnect',
                            'Too old TCP server version: ' + simVer));
            return;
        }

        this.tpmInfo = this.tcpResp.fromTpm(4);
        //console.log('Simulator props: ' + this.tpmInfo);

        let ack: number;
        ack = this.tcpResp.fromTpm(4);
        if (ack != 0)
        {
            setImmediate(this.connectCont, new TpmError(TPM_RC.TSS_TCP_BAD_ACK, 'SimConnect',
                            'Bad ack for the handshake sequence'));
            return;
        }

        this.tpmInfo |= TSS_TPM_INFO.TSS_SocketConn;

        this.tpmPlatSocket = new Net.Socket();
        this.tpmPlatSocket.connect(this.port + 1, this.host, this.onPlatConnect.bind(this));
    }

    private onPlatConnect(resp: Buffer)
    {
        //console.log('Connected to TPM platform port. Response ' +  resp);
        this.tpmPlatSocket.on('data', this.onPowerOnAck.bind(this));
        let req = new Buffer([0, 0, 0, TPM_TCP_PROTOCOL.SignalPowerOn]);
        this.tpmPlatSocket.write(req);
    }

    private onPowerOnAck(resp: Buffer)
    {
        this.tpmPlatSocket.removeAllListeners('data');

        if (resp.length != 4 || resp.readInt32BE(0) != 0)
        {
            setImmediate(this.connectCont, new TpmError(TPM_RC.TSS_TCP_BAD_ACK, 'SimPowerOn',
                            'Bad ack for the Simulator Power ON command'));
            return;
        }

        this.tpmPlatSocket.on('data', this.onNvOnAck.bind(this));
        let req = new Buffer([0, 0, 0, TPM_TCP_PROTOCOL.SignalNvOn]);
        this.tpmPlatSocket.write(req);
    }

    private onNvOnAck(resp: Buffer)
    {
        if (resp.length != 4 || resp.readInt32BE(0) != 0)
        {
            setImmediate(this.connectCont, new TpmError(TPM_RC.TSS_TCP_BAD_ACK, 'SimNvOn',
                            'Bad ack for the Simulator NV ON command'));
            return;
        }

        let req = new Buffer([0, 0, 0, TPM_TCP_PROTOCOL.SignalNvOn]);
        this.tpmPlatSocket.end();
        this.tpmPlatSocket.unref();
        this.tpmPlatSocket = null;

        this.dispatchCommand(new TpmBuffer([0x80,0x01,0x00,0x00,0x00,0x0C,0x00,0x00,0x01,0x44,0x00,0x00]),
                             this.onTpmStartup.bind(this));
    }

    private onTpmStartup(err: TpmError, resp: Buffer)
    {
        if (!err)
        {
            this.tpmSocket.removeAllListeners('data');

            if (resp.length == 10)
            {
                let rc: TPM_RC = resp.readInt32BE(6);
                if (rc != TPM_RC.SUCCESS && rc != TPM_RC.INITIALIZE)
                {
                    err = new TpmError(TPM_RC.TSS_TCP_UNEXPECTED_STARTUP_RESP, 'SimStartup',
                                         'Unexpected TPM2_Startup() response code ' + TPM_RC[rc]);
                }
            }
            else
                err = new TpmError(TPM_RC.TSS_TCP_BAD_RESP_LEN, 'SimStartup', 'Wrong length of TPM2_Startup response');
        }
        setImmediate(this.connectCont, err);
    }

    private onDispatch(lastRespFrag: Buffer)
    {
        this.tcpResp = new TpmBuffer(Buffer.concat([this.tcpResp.buffer, lastRespFrag]));
        let respLen: number = this.tcpResp.fromTpm(4);
        if (this.tcpResp.length < respLen + 8)
        {
            //console.log('Incomplete response received: ' + this.tcpResp.length + ' out of ' + (respLen + 8) + '. Continue reading...');
            return;
        }

        let err: TpmError = null;
        let resp: Buffer = null;
        if (respLen == this.tcpResp.length - 8)
        {
            this.tcpResp.setCurPos(respLen + 4);
            let ack: number = this.tcpResp.fromTpm(4);
            if (ack == 0)
                resp = this.tcpResp.buffer.slice(4, respLen + 4);
            else
                err = new TpmError(TPM_RC.TSS_TCP_BAD_ACK, 'Dispatch', 'Bad ack during regular command dispatch');
        }
        else
        {
            err = new TpmError(TPM_RC.TSS_TCP_INVALID_SIZE_TAG, 'Dispatch', 'Invalid size tag in the TPM response TCP packet: '
                                    + respLen + ' instead of ' + this.tcpResp.length + ' bytes');
        }

        setImmediate(this.dispatchCont, err, resp);
    }
}; // class TpmTcpDevice

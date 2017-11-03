
let ffi = require('ffi');
let ref = require('ref');
let Struct = require('ref-struct');
let ArrayType = require('ref-array')

let byte = ref.types.byte;
let int = ref.types.int;

export var ByteArray = ArrayType(byte);

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
    connect(continuation: () => void): void;
    dispatchCommand(command: Buffer, continuation: (Buffer) => void): void;
    close(): void;
}

export class TpmTbsDevice implements TpmDevice
{
    private tbsHandle: number = 0;

    private tbsDll;

    public connect(continuation: () => void)
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

        let res = this.tbsDll.Tbsi_Context_Create(tbsCtx.ref(), handleOut);
        if (res != 0)
            throw (new Error('TBS context cretaion failed. Error '));
        this.tbsHandle = handleOut.deref();
        //console.log("tbsHandle: " + this.tbsHandle)

        setImmediate(continuation);
        return null;
    }

    public dispatchCommand(command: Buffer, continuation: (Buffer) => void): void
    {
        let respBuf = new ByteArray(4096);
        let respSizePtr = ref.alloc('int', respBuf.length);
        let cmd = new ByteArray(command);

        let res = this.tbsDll.Tbsip_Submit_Command(this.tbsHandle, 0, 0, cmd, command.length, respBuf, respSizePtr);

        let respSize = respSizePtr.deref();
        let response: number[] = respBuf.toArray().slice(0, respSize);

        setImmediate(continuation, res == 0 ? new Buffer(response) : null);
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
import { toTpm, fromTpm } from "./TpmMarshaller.js";

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

    public connect(continuation: () => void)
    {
        this.connectCont = continuation;
        this.tpmSocket = new Net.Socket();
        this.tpmSocket.connect(this.port, this.host, this.onConnect.bind(this));
    }

    public dispatchCommand(command: Buffer, continuation: (Buffer) => void): void
    {
        let curPos = 0;
        let cmdBuf = new Buffer(command.length + 9);
        curPos = toTpm(TPM_TCP_PROTOCOL.SendCommand, cmdBuf, 4, curPos);
        curPos = toTpm(0, cmdBuf, 1, curPos);   // locality
        curPos = toTpm(command.length, cmdBuf, 4, curPos);
        command.copy(cmdBuf, curPos);

        this.dispatchCont = continuation;
        this.tcpResp = new Buffer(0);
        this.tpmSocket.removeAllListeners('data');
        this.tpmSocket.on('data', this.onDispatch.bind(this));
        this.tpmSocket.write(cmdBuf);
    }

    public close(): void
    {
        if (this.tpmSocket != null) {
            //console.log('Closing the simulator connection...');
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
    private tcpResp: Buffer;
    private connectCont: () => void = null;
    private dispatchCont: (Buffer) => void = null;


    private onConnect()
    {
        //console.log('Socket connection to the simulator established');
        this.tcpResp = new Buffer(0);
        this.tpmSocket.on('data', this.onHandShake.bind(this));
        let req = new Buffer([0, 0, 0, TPM_TCP_PROTOCOL.HandShake,
                              0, 0, 0, ClientVer]);
        this.tpmSocket.write(req);
    }

    private onHandShake(lastRespFrag: Buffer) {
        this.tcpResp = Buffer.concat([this.tcpResp, lastRespFrag]);
        if (this.tcpResp.length < 12)
        {
            console.log('Incomplete response received: ' + this.tcpResp.length + ' out of 12 bytes. Continue reading...');
            return;
        }

        this.tpmSocket.removeAllListeners('data');
        if (this.tcpResp.length != 12)
            throw new Error('Wrong length of the hand shake response ' + this.tcpResp.length + ' bytes instead of 12');

        let [simVer, curPos]: number[] = fromTpm(this.tcpResp, 4, 0);
        if (ClientVer != simVer)
            throw (new Error('Too old server version'));

        [this.tpmInfo, curPos] = fromTpm(this.tcpResp, 4, curPos);
        //console.log('Simulator props: ' + this.tpmInfo);

        let ack: number;
        [ack, curPos] = fromTpm(this.tcpResp, 4, curPos);
        if (ack != 0)
            throw (new Error('Bad ack from the TPM end point'));

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

        if (resp.length != 4 || fromTpm(resp, 4)[0] != 0)
            throw (new Error('Bad PowerOn ack from TPM platform end point'));
        //console.log('PowerOn confirmed');

        this.tpmPlatSocket.on('data', this.onNvOnAck.bind(this));
        let req = new Buffer([0, 0, 0, TPM_TCP_PROTOCOL.SignalNvOn]);
        this.tpmPlatSocket.write(req);
    }

    private onNvOnAck(resp: Buffer)
    {
        if (resp.length != 4 || fromTpm(resp, 4)[0] != 0)
            throw (new Error('Bad NvOn ack from TPM platform end point'));
        //console.log('NvOn confirmed');

        let req = new Buffer([0, 0, 0, TPM_TCP_PROTOCOL.SignalNvOn]);
        this.tpmPlatSocket.end();
        this.tpmPlatSocket.unref();
        this.tpmPlatSocket = null;

        this.dispatchCommand(new Buffer([0x80,0x01,0x00,0x00,0x00,0x0C,0x00,0x00,0x01,0x44,0x00,0x00]),
                                        this.onTpmStartup.bind(this));
    }

    private onTpmStartup(resp: Buffer)
    {
        this.tpmSocket.removeAllListeners('data');

        if (resp.length != 10)
            throw (new Error('Wrong length of TPM2_Startup() response'));

        let rc: TPM_RC = fromTpm(resp, 4, 6)[0];
        if (rc != TPM_RC.SUCCESS && rc != TPM_RC.INITIALIZE)
            throw (new Error('Unexpected TPM2_Startup() response ' + TPM_RC[rc]));

        setImmediate(this.connectCont);
    }

    private onDispatch(lastRespFrag: Buffer) {
        this.tcpResp = Buffer.concat([this.tcpResp, lastRespFrag]);
        let respLen: number = fromTpm(this.tcpResp, 4)[0];
        if (this.tcpResp.length < respLen + 8)
        {
            //console.log('Incomplete response received: ' + this.tcpResp.length + ' out of ' + (respLen + 8) + '. Continue reading...');
            return;
        }

        if (respLen != this.tcpResp.length - 8)
        {
            throw new Error('Invalid size tag in the TPM response TCP packet: '
                            + respLen + ' instead of ' + this.tcpResp.length + ' bytes');
        }
        //console.log('TPM returned response of ' + respLen + ' bytes');

        let ack: number = fromTpm(this.tcpResp, 4, respLen + 4)[0];
        if (ack != 0)
            throw (new Error('Bad ACK in the TPM response TCP packet: ' + ack + ' instead of 0'));

        let respBuf = new Buffer(this.tcpResp.slice(4, respLen + 4));
        setImmediate(this.dispatchCont, respBuf);
    }
}; // class TpmTcpDevice

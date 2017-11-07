import { TPM_CC, TPM_RC, TPM_RH, TPM_ST, TPM_HANDLE } from "./TpmTypes.js";
import { TpmDevice, TpmTcpDevice, TpmTbsDevice, TpmLinuxDevice } from "./TpmDevice.js";
import { toTpm, fromTpm, toTpm2B, fromTpm2B, createFromTpm, TpmMarshaller } from "./TpmMarshaller.js";
import * as tss from "./Tss.js";
import { Tpm } from "./Tpm.js";


export class TpmBase
{
    //
    // TPM object state
    //
    private Device: TpmDevice;

    private LastResponseCode: TPM_RC = TPM_RC.NOT_USED;

    //
    // Per-command state
    //

    private Sessions: tss.Session[] = null;

    /**
	 *  Suppresses exceptions in response to the next command failure
     */
	private AllowErrors: boolean;

    //
    // Scratch members
    //
    private CmdTag: TPM_ST;


    constructor(useSimulator: boolean = false,
                host: string = '127.0.0.1', port: number = 2321)
    {
        this.Device = useSimulator ? new TpmTcpDevice(host, port)
                    : process.platform == 'win32' ? new TpmTbsDevice()
                                                  : new TpmLinuxDevice();
    }
    
    public connect(continuation: () => void)
    {
        this.Device.connect(continuation);
    }

    public close(): void
    {
        this.Device.close();
        this.Device = null;

        if (this.Device == undefined)
            console.log("this._device is undefined");
        if (this.Device == null)
            console.log("this._device is null");
        console.log("this._device = ", this.Device);
    }


    private static isCommMediumError(code: TPM_RC): boolean
    {
        // TBS or TPMSim protocol error
        return (code & 0xFFFF0000) == 0x80280000;
    }

    private static cleanResponseCode(rawResponse: TPM_RC): TPM_RC
    {
        if (this.isCommMediumError(rawResponse))
            return rawResponse;

        let mask: number = (rawResponse & TPM_RC.RC_FMT1) != 0
                         ? TPM_RC.RC_FMT1 | 0x3F : TPM_RC.RC_WARN | TPM_RC.RC_VER1 | 0x7F;
        return rawResponse & mask;
    }

    public getLastResponseCode(): TPM_RC
    {
        return this.LastResponseCode;
    }

	/**
	 * For the next TPM command invocation, errors will not cause an exception to be thrown
	 * (use _lastCommandSucceeded or _getLastResponseCode() to check for an error)
	 * 
	 * @return The same object (to allow modifier chaining)
	 */
	public allowErrors(): Tpm
	{
		this.AllowErrors = true;
		return <Tpm><Object>this;
	}
	
	/**
	 * Specifies a single session handle to use with the next command 
	 * 
	 * @param hh List of up to 3 session handles 
	 * @return This TPM object
	 */
    public withSession(sess: tss.Session): Tpm
	{
		this.Sessions = new Array<tss.Session>(sess);
		return <Tpm><Object>this;
	}

	/**
	 * Specifies the session handles to use with the next command 
	 * 
	 * @param hh List of up to 3 session handles 
	 * @return This TPM object
	 */
    public withSessions(...sess: tss.Session[]): Tpm
	{
		this.Sessions = new Array<tss.Session>(...sess);
		return <Tpm><Object>this;
	}

    protected prepareCmdBuf(
        cmdCode: TPM_CC,
        handles: TPM_HANDLE[]
    ): [Buffer, number]
    {
        let cmdBuf: Buffer = new Buffer(4096);
        let curPos: number = 0;

        this.CmdTag = this.Sessions != null && this.Sessions.length > 0 ? TPM_ST.SESSIONS : TPM_ST.NO_SESSIONS;
        curPos = toTpm(this.CmdTag, cmdBuf, 2, curPos);
        curPos = toTpm(0, cmdBuf, 4, curPos); // to be filled in later
        curPos = toTpm(cmdCode, cmdBuf, 4, curPos);

        if (handles != null)
            for (let h of handles)
            {
                curPos = h == null ? toTpm(TPM_RH.NULL, cmdBuf, 4, curPos)
                                   : h.toTpm(cmdBuf, curPos);
            }

        if (this.CmdTag == TPM_ST.SESSIONS)
        {
            // We do not know the size of the authorization area yet.
            // Remember the place to marshal it, ...
            let authSizePos = curPos;
            // ... and marshal a placeholder value for now.
            //curPos = toTpm(0, cmdBuf, 4, curPos);
            curPos += 4;

            for (let sess of this.Sessions)
            {
                curPos = sess.SessIn.toTpm(cmdBuf, curPos);
            }
            toTpm(curPos - authSizePos - 4, cmdBuf, 4, authSizePos);
        }
        this.Sessions = null;

        return [cmdBuf, curPos];
    }

    private ResponseHandler: (Buffer) => void;
    private CmdBuf: Buffer;

    private InterimResponseHandler (respBuf: Buffer)
    {
        let rc: TPM_RC = fromTpm(respBuf, 4, 6)[0];
        if (rc == TPM_RC.RETRY)
            this.Device.dispatchCommand(this.CmdBuf, this.ResponseHandler.bind(this));
        else
            setImmediate(this.ResponseHandler.bind(this), respBuf);
    }

    protected dispatchCommand(cmdBuf: Buffer, responseHandler: (Buffer) => void)
    {
        // Fill in command buffer size in the command header
        toTpm(cmdBuf.length, cmdBuf, 4, 2);
        this.ResponseHandler = responseHandler;
        this.CmdBuf = cmdBuf;
        this.Device.dispatchCommand(cmdBuf, this.InterimResponseHandler.bind(this));
    }

    // Returns pair [response parameters size, read position in response buffer]
    protected processResponse(cmdCode: TPM_CC, respBuf: Buffer): [number, number]
    {
        let allowErrors = this.AllowErrors;
        this.AllowErrors = false;

        if (respBuf.length < 10)
            throw(new Error('Response buffer is too short: ' + respBuf.length));

        let tag: TPM_ST = fromTpm(respBuf, 2, 0)[0];
        let respSize: number = fromTpm(respBuf, 4, 2)[0];
        let rc: TPM_RC = fromTpm(respBuf, 4, 6)[0];
        let curPos: number = 10;

        this.LastResponseCode = TpmBase.cleanResponseCode(rc);

        if (rc == TPM_RC.SUCCESS && tag != this.CmdTag ||
            rc != TPM_RC.SUCCESS && tag != TPM_ST.NO_SESSIONS)
        {
            throw(new Error('Invalid tag in the response buffer: ' + TPM_ST[tag] +
                            ' for command {' + TPM_CC[cmdCode] +
                            '} with response code {' + TPM_RC[this.LastResponseCode] + '}'));
        }

        if (this.LastResponseCode != TPM_RC.SUCCESS)
        {
            if (!allowErrors)
            {
                throw(new Error('Command {' + TPM_CC[cmdCode] +
                                '} failed with error {' + TPM_RC[this.LastResponseCode] + '}'));
            }
            return [0, 10];
        }

        let retHandle: TPM_HANDLE = null;
        if (cmdCode == TPM_CC.CreatePrimary
            || cmdCode == TPM_CC.Load
            || cmdCode == TPM_CC.HMAC_Start
            || cmdCode == TPM_CC.ContextLoad
            || cmdCode == TPM_CC.LoadExternal
            || cmdCode == TPM_CC.StartAuthSession
            || cmdCode == TPM_CC.HashSequenceStart
            || cmdCode == TPM_CC.CreateLoaded)
        {
            // Response buffer contains a handle returned by the TPM
            [retHandle, curPos] = createFromTpm(TPM_HANDLE, respBuf, curPos);
            //assert(retHandle.handle != 0 && retHandle.handle != TPM_RH.UNASSIGNED);
        }

        // If a response session is present, response buffer contains a field specifying the size of response parameters
        let respParamsSize: number = respBuf.length - curPos;
        if (tag == TPM_ST.SESSIONS)
            [respParamsSize, curPos] = fromTpm(respBuf, 4, curPos);

        if (retHandle != null)
        {
            // A hack to simplify code gen for returned handles handling
            curPos -= 4;
            respParamsSize += 4;
            retHandle.toTpm(respBuf, curPos);
        }

        return [respParamsSize, curPos];
     } // processResponse()

}; // class TpmBase

package tss;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;

import tss.tpm.*;


/**
 * TpmBase is the base class for Tpm (Tpm is auto-generated)
 *
 */
public abstract class TpmBase implements Closeable 
{
    /**
     * Admin handles (and associated auth values) can be associated with a TPM object
     */
    public TPM_HANDLE _OwnerHandle = TPM_HANDLE.from(TPM_RH.OWNER);
    /**
     * Admin handles (and associated auth values) can be associated with a TPM object
     */
    public TPM_HANDLE _EndorsementHandle = TPM_HANDLE.from(TPM_RH.ENDORSEMENT);
    /**
     * Admin handles (and associated auth values) can be associated with a TPM object
     */
    public TPM_HANDLE _PlatformHandle = TPM_HANDLE.from(TPM_RH.PLATFORM);
    /**
     * Admin handles (and associated auth values) can be associated with a TPM object
     */
    public TPM_HANDLE _LockoutHandle = TPM_HANDLE.from(TPM_RH.LOCKOUT);

    /**
     * Tpm objects can interact with TPMs over a variety of interfaces.  This attaches a "TPM transport" interface
     * to the TPM so that commands can be sent and received
     * 
     * @param theDevice A transport connection to a TPM device
     */
    public void _setDevice(TpmDevice theDevice)
    {
        device = theDevice;
        lastResponseCode = TPM_RC.SUCCESS;
    }
    /**
     * Tpm objects can interact with TPMs over a variety of interfaces called "devices."  This returns
     * the current attached device
     *  
     * @return The current TPM device
     */
    public TpmDevice _getDevice()
    {
        return device;
    }

    
    /**
     * For the next TPM command invocation, errors will not cause an exception to be thrown
     * (use _lastCommandSucceeded or _getLastResponseCode() to check for an error)
     * 
     * @return The same object (to allow modifier chaining)
     */
    public Tpm _allowErrors()
    {
        AllowErrors = true;
        return (Tpm)this;
    }
    
    /**
     * @return The list of response codes allowed to be returned by the next executed command.
     */
    public TPM_RC[] _GetExpectedResponses()
    {
        return ExpectedResponses;
    }

    /**
     * For the next TPM command invocation, an exception will be throw if the command 
     * returns a response code different from expectedResponse.
     * @param expectedResponse Expected response code. May be null or TPM_RC.SUCCESS.
     * @return This Tpm object (to allow modifier chaining)
     */
    public Tpm _expectError(TPM_RC expectedResponse)
    {
        return _expectResponses(expectedResponse);
    }
    
    /**
     * The next executed command should return one of the response codes contained
     * in the given list. Otherwise a run-time warning is issued.
     * If the only expected response code is TPM_RC.SUCCESS, and the command returns
     * an error code, a TssException is thrown.
     * 
     * @param expectedResponses One or more allowed response codes. May be null.
     * @return This Tpm object (to allow modifier chaining)
     */
    public Tpm _expectResponses(TPM_RC... expectedResponses)
    {
        // Empty responses list indicates success
        ExpectedResponses = null;

        if (expectedResponses.length == 0 ||
            (expectedResponses.length == 1 &&
             Helpers.isOneOf(expectedResponses[0], TPM_RC.SUCCESS, null)))
        {
            return (Tpm)this;
        }

        ExpectedResponses = new TPM_RC[0];
        return _expectMoreResponses(expectedResponses);
    }
    
    /**
     * Adds more response codes allowed to be returned by the next executed command.
     * If no expected response codes have been specified with the _ExpectResponses()
     * method before this call, TpmRc.Success is implicitly added to the list.
     * @param expectedResponses Additional allowed response codes. May not be null.
     * @return this TPM object 
     */
    public Tpm _expectMoreResponses(TPM_RC... expectedResponses)
    {
        if (ExpectedResponses == null)
        {
            ExpectedResponses = new TPM_RC[] {TPM_RC.SUCCESS};
        }
        TPM_RC[] old = ExpectedResponses;
        ExpectedResponses = new TPM_RC[expectedResponses.length + old.length];
        for (int i = 0; i < old.length; ++i)
        {
            ExpectedResponses[i] = old[i];  
        }

        for (int i = 0; i < expectedResponses.length; ++i)
        {
            TPM_RC rc =expectedResponses[i];
            int curPos = old.length + i;
            if (rc == TPM_RC.SUCCESS && curPos != 0)
            {
                if (ExpectedResponses[0] == TPM_RC.SUCCESS)
                    continue;
                rc = ExpectedResponses[0];
                ExpectedResponses[0] = TPM_RC.SUCCESS;
            }
            ExpectedResponses[curPos] = rc;
        }
        return (Tpm)this;
    }

    private boolean _isSuccessExpected()
    {
        return ExpectedResponses == null || ExpectedResponses[0] == TPM_RC.SUCCESS;
    }
    
    /**
     * Did the last TPM command return RC_SUCCESS?
     * 
     * @return Success?
     */
    public Boolean _lastCommandSucceeded()
    {
        return (lastResponseCode == TPM_RC.SUCCESS);
    }

    /**
     * Get the last TPM response code
     * 
     * @return The response code
     */
    public TPM_RC _getLastResponseCode()
    {
        return lastResponseCode;
    }

    /**
     * Specifies a single session handle to use with the next command 
     * 
     * @param h Session handle
     * @return this TPM object
     */
    public Tpm _withSession(TPM_HANDLE h)
    {
        Sessions = new TPM_HANDLE[] { h };
        return (Tpm)this;
    }

    /**
     * Specifies the session handles to use with the next command 
     * 
     * @param hh List of up to 3 session handles 
     * @return this TPM object
     */
    public Tpm _withSessions(TPM_HANDLE ... hh)
    {
        Sessions = hh;
        return (Tpm)this;
    }

    /**
     * Get last response code returned from the TPM (e.g. TPM_RC.SUCCESS)
     * @return The response code
     */
    public TPM_RC getLastResponseCode()
    {
        return lastResponseCode;
    }

    static void WriteSession (TpmBuffer buf, TPM_HANDLE sessHandle, byte[] nonceCaller,
                                              TPMA_SESSION sessAttrs, byte[] authVal)
    {
        sessHandle.toTpm(buf);
        buf.writeSizedByteBuf(nonceCaller);
        sessAttrs.toTpm(buf);
        buf.writeSizedByteBuf(authVal);
    }

    /**
     * Send a command to the underlying TPM
     * @param cmdCode The command code
     * @param req The input parameter structure
     * @param resp The output parameter structure
     */
    protected void DispatchCommand(TPM_CC cmdCode, ReqStructure req, RespStructure resp)
    { try {
        TPM_HANDLE[] inHandles = req.getHandles();
        int numAuthHandles = req.numAuthHandles();

        boolean hasSessions = numAuthHandles != 0 || Sessions != null;
        int sessTag = hasSessions ? TPM_ST.SESSIONS.toInt() : TPM_ST.NO_SESSIONS.toInt();
        
        TpmBuffer cmdBuf = new TpmBuffer();

        // Standard TPM command header {tag, length, commandCode}
        cmdBuf.writeShort(sessTag);
        cmdBuf.writeInt(0);        // to be filled in later
        cmdBuf.writeInt(cmdCode.toInt());

        // Handles
        int numHandles = inHandles == null ? 0 : inHandles.length;
        for (int i=0; i < numHandles; i++)
            inHandles[i].toTpm(cmdBuf);
        
        // Marshal command params (without handles) to paramBuf
        TpmBuffer paramBuf = new TpmBuffer();
        req.toTpm(paramBuf);
        paramBuf.trim();

        byte[] cpHashData = null;
        
        //
        // Authorization sessions
        //
        if (hasSessions)
        {
            // We do not know the size of the authorization area yet.
            // Remember the place to marshal it, ...
            int authSizePos = cmdBuf.curPos();
            // ... and marshal a placeholder 0 value for now.
            cmdBuf.writeInt(0);

            // todo: Make Sessions type Session[]
            // If not all required sessions were provided explicitly, TSS.Java will create the necessary
            // number of password sessions with auth values (if any) from the corresponding TPM_HANDLE objects.
            int numExplicitSessions = 0;
            if (Sessions == null)
                Sessions = new TPM_HANDLE[numAuthHandles];
            else
            {
                numExplicitSessions = Sessions.length;
                if (numExplicitSessions < numAuthHandles)
                    Sessions = Arrays.copyOf(Sessions, numAuthHandles);
            }
            for (int i = numExplicitSessions; i < numAuthHandles; ++i)
                Sessions[i] = TPM_HANDLE.PW;

            TPMA_SESSION sessAttrs = TPMA_SESSION.continueSession;
            for (int i=0; i < Sessions.length; i++)
            {
                // todo: Add support for policyc sessions with HMAC
                boolean needAuth = i < numHandles && Sessions[i].getType() != TPM_HT.POLICY_SESSION;
                WriteSession (cmdBuf, Sessions[i], null, sessAttrs,
                              needAuth ? inHandles[i].AuthValue : null);
            }
            Sessions = null;

            cmdBuf.writeNumAtPos(cmdBuf.curPos() - authSizePos - 4, authSizePos);
        }
        
        // Write marshaled command params to the command buffer
        cmdBuf.writeByteBuf(paramBuf.buffer());
        
        // Finally, set the command buffer size
        cmdBuf.writeNumAtPos(cmdBuf.curPos(), 2);
        

        if (CpHash != null || AuditCommand)
        {
            if (cpHashData == null)
                cpHashData = GetCpHashData(cmdCode, paramBuf.buffer());
            if (CpHash != null)
            {
                CpHash.digest = Crypto.hash(CpHash.hashAlg, cpHashData);
                clearInvocationState();
                Sessions = null;
                CpHash = null;
                return;
            }
            AuditCpHash.digest = Crypto.hash(CommandAuditHash.hashAlg, cpHashData);
        }



        byte[] rawCmdBuf = cmdBuf.trim();
        int nvRateRecoveryCount = 4;    
        TpmBuffer respBuf = null;
        TPM_ST respTag = TPM_ST.NULL; 
        int respSize = 0;
        int rawResponseCode = 0;

        while (true)
        {
            device.dispatchCommand(rawCmdBuf);
            
            byte[] rawRespBuf = device.getResponse();
            respBuf = new TpmBuffer(rawRespBuf);
            
            // get the standard header
            respTag = TPM_ST.fromTpm(respBuf);
            respSize = respBuf.readInt();
            rawResponseCode = respBuf.readInt();
    
            int actRespSize = respBuf.size();
            if (respSize != actRespSize)
            {
                throw new TpmException(String.format(
                            "Inconsistent TPM response buffer: %d B reported, %d B received", respSize, actRespSize));
            }

            lastResponseCode = TpmHelpers.fromRawResponse(rawResponseCode);
            if (callbackObject != null)
                callbackObject.commandCompleteCallback(cmdCode, lastResponseCode, rawCmdBuf, rawRespBuf);

            if (lastResponseCode == TPM_RC.RETRY)
                continue;

            if (lastResponseCode != TPM_RC.NV_RATE || ++nvRateRecoveryCount > 4)
                break;

            // todo: Enable TPM property retrieval and sleep below, and remove the following break
            break;
            //System.out.println(">>>> NV_RATE: Retrying... Attempt " + nvRateRecoveryCount.toString());
            //Thread.Sleep((int)Tpm2.GetProperty(this, Pt.NvWriteRecovery) + 100);
        }

        // Interpretation of the response code depends on whether the programmer
        // has indicated that an error is expected or allowed.
        if (lastResponseCode != TPM_RC.SUCCESS)
        {
            // error - decode it
            if (AllowErrors)
                return; // Any error is allowed

            if (Helpers.isOneOf(lastResponseCode, ExpectedResponses))
                return; // The given error is expected

            if (_isSuccessExpected())
            {
                System.out.println("TPM ERROR: " + lastResponseCode);
                throw new TpmException(lastResponseCode, rawResponseCode);
            }

            String expected = ExpectedResponses.length > 1 ? Arrays.toString(ExpectedResponses)
                                                           : ExpectedResponses[0].toString();
            throw new TpmException("Unexpected response {" + lastResponseCode + "} instead of {" + expected + "}",
                                   lastResponseCode);
        }
        else if (ExpectedResponses != null)
        {
            String expected = ExpectedResponses.length > 1 ? "s " + Arrays.toString(ExpectedResponses) + " were"
                                                           : " " + ExpectedResponses.toString() + " was";
            throw new TpmException("Error" + expected + " expected, " +
                                   "but the TPM command " + cmdCode + " succeeded"); 
        }

        // Keep a copy of this before we clean out invocation state
        boolean auditCommand = AuditCommand;

        clearInvocationState();

        // A check for the session tag consistency across the command invocation
        // only makes sense when the command succeeds.
        if (respTag.toInt() != sessTag)
            throw new TpmException("Unexpected response tag " + respTag);

        if (resp == null)
            resp = new RespStructure();  // use a placeholder to avoid null checks

        if (resp.numHandles() > 0)
        {
            assert(resp.numHandles() == 1);
            resp.setHandle(TPM_HANDLE.fromTpm(respBuf));
        }

        boolean rpReady = false;
        int     respParamsPos = 0,
                respParamsSize = 0;

        // If there are no sessions then response parameters take up the remaining part
        // of the response buffer. Otherwise the response parameters area is preceded with
        // its size, and followed by the session area.
        if (respTag == TPM_ST.SESSIONS)
        {
            respParamsSize = respBuf.readInt();
            respParamsPos = respBuf.curPos();
            rpReady = processRespSessions(respBuf, cmdCode, respParamsPos, respParamsSize);
        }
        else
        {
            respParamsPos = respBuf.curPos();
            respParamsSize = respBuf.size() - respParamsPos;
        }

        if (auditCommand)
        {
            byte[] rpHash = getRpHash(CommandAuditHash.hashAlg, respBuf,
                                      cmdCode, respParamsPos, respParamsSize, rpReady);
            CommandAuditHash.extend(Helpers.concatenate(AuditCpHash.digest, rpHash));
        }

        // Now we can decrypt (if necessary) the first response parameter
        doParmEncryption(resp, respBuf, respParamsPos, false);

        // ... and unmarshall the whole response parameters area
        respBuf.curPos(respParamsPos);
        resp.initFromTpm(respBuf);
        if (respBuf.curPos() != respParamsPos + respParamsSize)
            throw new TpmException("Bad response parameters area");

        // If there is a returned handle get a pointer to it. It is always the 
        // first element in the structure.
        updateRespHandle(cmdCode, resp);

        Sessions = null;

    } finally {
        clearInvocationState();
    }} // DispatchCommand()
    
    void clearInvocationState()
    {
        AllowErrors = false;
        ExpectedResponses = null;
        AuditCommand = false;
    }

    byte[] GetCpHashData(TPM_CC cmdCode, byte[] cmdParams)
    {
        return null;
    }

    byte[] getRpHash(TPM_ALG_ID hashAlg, TpmBuffer respBuf, TPM_CC cmdCode,
                     int respParamsPos, int respParamsSize, boolean rpReady)
    {
        return null;
    }

    void doParmEncryption(CmdStructure cmd, TpmBuffer paramBuf, int startPos, boolean request)
    {
    }

    boolean processRespSessions(TpmBuffer b, TPM_CC cmdCode, int respParamsPos, int respParamsSize)
    {    
        return false;
    }
    
    void updateRespHandle(TPM_CC cc, RespStructure resp)
    {
    }

    /**
     * Clients can register for callbacks, e.g. after each TPM command is executed.
     * @param callback Reference to a TpmCallbackInterface implementation
     */
    public void _setCallback(TpmCallbackInterface callback)
    {
        callbackObject = callback;
    }

    @Override
    public void close() throws IOException {
        device.close();
        device = null;
    }



    //
    // State persistent across commands
    //

    TpmDevice device;
    TpmCallbackInterface callbackObject;
    
    TPM_RC lastResponseCode;
    
    TPMT_HA     CommandAuditHash;
    TPMT_HA     AuditCpHash;

    //
    // Per-invocation state
    //

    /** Suppress exceptions in response to the next command failure */
    boolean     AllowErrors = false;
    boolean     AuditCommand = false;
    
    /** List of allowed response codes for the next command invocation.
     * 
     *  If it contains TPM_RC.SUCCESS value, it is always the first item of the list.
     */
    TPM_RC[]    ExpectedResponses;

    TPM_HANDLE[] Sessions;
    //TPM_RC ErrorToExpect;

    TPMT_HA     CpHash = null;

}

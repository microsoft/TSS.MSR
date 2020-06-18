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

    static void WriteSession (OutByteBuf buf, TPM_HANDLE sessHandle, byte[] nonceCaller,
                                              TPMA_SESSION sessAttrs, byte[] authVal)
    {
        buf.writeObj(sessHandle);
        buf.writeSizedByteBuf(nonceCaller);
        buf.writeObj(sessAttrs);
        buf.writeSizedByteBuf(authVal);
    }

    /**
     * Send a command to the underlying TPM
     * @param cmdCode The command code
     * @param inHandles Input handles
     * @param numAuthHandles Number of handles that need authorization
     * @param numRespHandles count
     * @param inParms The input parameter structure
     * @param outParms The output parameter structure
     */
    protected void DispatchCommand(TPM_CC cmdCode,
            TPM_HANDLE[] inHandles,
            int numAuthHandles,
            int numRespHandles,
            TpmStructure inParms, 
            TpmStructure outParms)
    { try {
        boolean hasSessions = numAuthHandles != 0 || Sessions != null;
        int sessTag = hasSessions ? TPM_ST.SESSIONS.toInt() : TPM_ST.NO_SESSIONS.toInt();
        
        OutByteBuf cmdBuf = new OutByteBuf();
        // Standard TPM command header {tag, length, commandCode}
        cmdBuf.writeShort(sessTag);
        cmdBuf.writeInt(0);        // to be filled in later
        cmdBuf.writeInt(cmdCode.toInt());

        // Handles
        int numHandles = inHandles == null ? 0 : inHandles.length;
        for (int i=0; i < numHandles; i++)
            cmdBuf.writeObj(inHandles[i]);
        
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
        
        // Marshal command params (minus the handles) to the outBuf
        cmdBuf.writeObj(inParms);
        
        // And, finally, set the command buffer size
        cmdBuf.writeNumAtPos(cmdBuf.curPos(), 2);
        
        byte[] req = cmdBuf.buffer();
        int nvRateRecoveryCount = 4;    
        InByteBuf respBuf = null;
        TPM_ST respTag = TPM_ST.NULL; 
        int respSize = 0;
        int rawResponseCode = 0;

        while (true)
        {
            device.dispatchCommand(req);
            
            byte[] resp = device.getResponse();
            respBuf = new InByteBuf(resp);
            
            // get the standard header
            respTag = TPM_ST.fromTpm(respBuf);
            respSize = respBuf.readInt();
            rawResponseCode = respBuf.readInt();
    
            lastResponseCode = TpmHelpers.fromRawResponse(rawResponseCode);
            if(callbackObject!=null)
            {
                callbackObject.commandCompleteCallback(cmdCode, lastResponseCode, req, resp);
            }
            if (lastResponseCode == TPM_RC.RETRY)
            {
                continue;
            }
            if (lastResponseCode != TPM_RC.NV_RATE || ++nvRateRecoveryCount > 4)
            {
                break;
            }
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
        
        // This should be fine, but just to check
        if (respTag.toInt() != sessTag)
            throw new TpmException("Unexpected response tag " + respTag);

        // first the handle, if there are any
        // note that the response structure is fragmented, so we need to reconstruct it
        // in respParmBuf if there are handles
        OutByteBuf respParmBuf = new OutByteBuf();
        
        TPM_HANDLE outHandles[] = new TPM_HANDLE[numRespHandles];
        for (int j=0; j < numRespHandles; j++)
        {
            outHandles[j] = TPM_HANDLE.fromTpm(respBuf);
            outHandles[j].toTpm(respParmBuf);
        }
        
        byte[] responseWithoutHandles = null;
        if (hasSessions)
        {
            int restOfParmSize = respBuf.readInt(4);
            responseWithoutHandles = respBuf.readByteArray(restOfParmSize);
            respParmBuf.writeByteBuf(responseWithoutHandles);
        }
        else
        {
            responseWithoutHandles = respBuf.readByteArray(respSize - respBuf.curPos());
            respParmBuf.writeByteBuf(responseWithoutHandles);
        }
        
        if(hasSessions)
        {
            processResponseSessions(respBuf);
        }
        
        if(outParms!=null)
        {
            // the handles may've been fragmented in the TPM response, but we 
            // put them back together
            InByteBuf responseData = new InByteBuf(respParmBuf.buffer());
            outParms.initFromTpm(responseData);
        }
    } finally {
        AllowErrors = false;
        ExpectedResponses = null;
    }} // DispatchCommand()
    
    
    void processResponseSessions(InByteBuf b)
    {    
        return;
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

    
    
    TpmDevice device;
    TpmCallbackInterface callbackObject;
    
    TPM_RC lastResponseCode;
    
    /**
     * Suppresses exceptions in response to the next command failure
     */
    boolean AllowErrors;
    
    /**
     * List of expected errors for the next command invocation.
     * If it contains TPM_RC.SUCCESS value, it is always the first item of the list.
     */
    TPM_RC[] ExpectedResponses;
    

    TPM_HANDLE[] Sessions;
    //TPM_RC ErrorToExpect;

    
    
}

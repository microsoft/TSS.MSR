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
	public void _setDevice(TpmDeviceBase theDevice)
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
	public TpmDeviceBase _getDevice()
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
		ExplicitSessionHandles = new TPM_HANDLE[] { h };
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
		ExplicitSessionHandles = hh;
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
	
	/**
	 * Send a command to the underlying TPM
	 * @param command The command code
	 * @param inHandles Input handles
	 * @param authHandleCount Number of handles that need authorization
	 * @param outHandleCount count
	 * @param inParms The input parameter structure
	 * @param outParms The output parameter structure
	 */
	protected void DispatchCommand(TPM_CC command,
			TPM_HANDLE[] inHandles,
			int authHandleCount,
			int outHandleCount,
			TpmStructure inParms, 
			TpmStructure outParms)
	{
		try {
		int numExplicitSessions = ExplicitSessionHandles==null? 0:ExplicitSessionHandles.length;
		boolean haveSessions = (authHandleCount!=0) || (numExplicitSessions!=0);
		OutByteBuf outBuf = new OutByteBuf();
		int tag = haveSessions ? TPM_ST.SESSIONS.toInt() : TPM_ST.NO_SESSIONS.toInt();
		// standard header {tag, length, commandCode}
		outBuf.writeInt(tag,  2);
		outBuf.writeInt(0, 4);		// to be filled in later
		outBuf.writeInt(command.toInt(),4);
		// handles
		for(int j=0;j<inHandles.length;j++)
		{
			outBuf.writeInt(inHandles[j].handle,4);
		}
		
		// Sessions.  
		// If sessions are provided explicitly, they will be used (and enough explicit sessions 
		// must be provided.)
		// Otherwise the dispatcher does PWAP and fetches the authValue from the 
		// TPM_HANDLE.
		OutByteBuf sessionBuf = new OutByteBuf();
		if(haveSessions)
		{
			if(numExplicitSessions==0)
			{			
				// No explicit sessions were provided.  tss.Java uses the authorization value
				// in the TPM_HANDLE in PWAP sessions as needed.  Note: if no authorization 
				// value has been set in the handle, then byte[0] is assumed.

				// Sessions are {authHandle, nonceCallerSize, nonceCaller, sessionAttributes, hmacSize, hmac}
				// When marshaled, the Session[] is length-prepended
				TPM_RH pwapHandle = TPM_RH.RS_PW;
				TPMA_SESSION sessionAttributes = TPMA_SESSION.continueSession;
				for(int j=0;j<authHandleCount;j++)
				{
					// translate missing auth to byte[0]
					boolean authMissing = inHandles[j].AuthValue==null;
					int authValLen = authMissing? 0: inHandles[j].AuthValue.length;
					byte[] authVal = authMissing?new byte[0]:inHandles[j].AuthValue;
					sessionBuf.write(pwapHandle);			// handle
					sessionBuf.writeInt(0, 2);				// zero length nonce (nonce is missing)
					sessionBuf.write(sessionAttributes);	// attributes
					sessionBuf.writeInt(authValLen, 2);		// authLen
					sessionBuf.write(authVal);				// authVal
				}
			}
			else 
			{
				// we have explicit sessions.  The caller MUST provide enough sessions for everything that needs
				// authorizing, but may provide more (e.g. encrypting or auditing sessions)
				if(ExplicitSessionHandles.length < authHandleCount)
				{
					ExplicitSessionHandles = null;
					throw new TpmException("Needed at least " + String.valueOf(authHandleCount) + " session handles, but only " + 
							String.valueOf(ExplicitSessionHandles.length) + " were provided");
				}
				for(int j=0;j<ExplicitSessionHandles.length;j++)
				{
					TPM_HANDLE h = ExplicitSessionHandles[j];
					TPMA_SESSION sessionAttributes = TPMA_SESSION.continueSession;
					TPM_RH pwapHandle = TPM_RH.RS_PW;

					if(h.handle == TPM_RH.RS_PW.toInt())
					{
						boolean authMissing = inHandles[j].AuthValue==null;
						int authValLen = authMissing? 0: inHandles[j].AuthValue.length;
						byte[] authVal = authMissing?new byte[0]:inHandles[j].AuthValue;
						sessionBuf.write(pwapHandle);			// handle
						sessionBuf.writeInt(0, 2);				// zero length nonce (nonce itself is missing)
						sessionBuf.write(sessionAttributes);	// attributes
						sessionBuf.writeInt(authValLen, 2);		// authLen
						sessionBuf.write(authVal);				// authVal
						continue;
					}

					switch(h.getType().asEnum())
					{
					case POLICY_SESSION:
						sessionBuf.write(h.handle);			// handle
						sessionBuf.writeInt(0, 2);				// zero length nonce (nonce is missing)
						sessionBuf.write(sessionAttributes);	// attributes
						sessionBuf.writeInt(0, 2);		// authLen = 0 (auth itself is missing)
						break;
						
						default:
							throw new RuntimeException("Unsupported handle type in session");
					}
				}
				ExplicitSessionHandles = null;
			}
			outBuf.writeInt(sessionBuf.size(),4);
			outBuf.write(sessionBuf.getBuf());
		}
		
		
		// prepare the parms buf (we will skip the handles)
		OutByteBuf parmsBuf = new OutByteBuf();
		inParms.toTpm(parmsBuf);
		
		// copy the parms (minus the handles) to the outBuf
		outBuf.writeArrayFragment(parmsBuf.getBuf(), inHandles.length*4,parmsBuf.size() );
		
		// fill in the length by making a new buf and copying stuff over (plus the length)
		OutByteBuf finalBuf = new OutByteBuf();
		finalBuf.writeArrayFragment(outBuf.getBuf(), 0, 2);
		finalBuf.writeInt(outBuf.size(), 4);
		finalBuf.writeArrayFragment(outBuf.getBuf(), 6, outBuf.size());
		
		byte[] cBuf = finalBuf.getBuf();
		byte[] rBuf = null;
        int nvRateRecoveryCount = 4;    
        InByteBuf respBuf = null;
        TPM_ST respTag = TPM_ST.NULL; 
        int rawResponseCode = 0;

        while(true)
        {
			device.dispatchCommand(cBuf);
			rBuf = device.getResponse();
			
			respBuf = new InByteBuf(rBuf);
			
			// get the standard header
			int respTagInt = respBuf.readInt(2);
			respTag = TPM_ST.fromInt(respTagInt);
			/*int respSize =*/ respBuf.readInt(4);
			rawResponseCode = respBuf.readInt(4);
	
			lastResponseCode = TpmHelpers.fromRawResponse(rawResponseCode);
			if(callbackObject!=null)
			{
				callbackObject.commandCompleteCallback(command, lastResponseCode, cBuf, rBuf);
			}
            if (lastResponseCode == TPM_RC.RETRY)
            {
                continue;
            }
            if (lastResponseCode != TPM_RC.NV_RATE || ++nvRateRecoveryCount > 4)
            {
                break;
            }
            // TODO: Enable TPM property retrieval and sleep below, and remove the following break
            break;
            //System.out.println(">>>> NV_RATE: Retrying... Attempt " + nvRateRecoveryCount.toString());
            //Thread.Sleep((int)Tpm2.GetProperty(this, Pt.NvWriteRecovery) + 100);
        }
		// Interpretation of the response code depends on whether the programmer
		// has indicated that an error is expected or allowed.
//		if(rawResponseCode != 0)
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
			String expected = ExpectedResponses.length > 1 ? Arrays.toString(ExpectedResponses) : ExpectedResponses[0].toString();
			throw new TpmException("TPM returned unexpected error " + lastResponseCode + " instead of " + expected,
								   lastResponseCode);
		}
        else if (ExpectedResponses != null)
		{
			String expected = ExpectedResponses.length > 1 ? "s " + Arrays.toString(ExpectedResponses) + " were"
														   : " " + ExpectedResponses.toString() + " was";
			throw new TpmException("Error" + expected + " expected, " +
								   "but the TPM command " + command + " succeeded"); 
		}
		
		// This should be fine, but just to check
		if(respTag.toInt() != tag)
		{
			throw new TpmException("Unexpected response tag " + respTag);
		}

		// first the handle, if there are any
		// note that the response structure is fragmented, so we need to reconstruct it
		// in respParmBuf if there are handles
		OutByteBuf respParmBuf = new OutByteBuf();
		
		TPM_HANDLE outHandles[] = new TPM_HANDLE[outHandleCount];
		for(int j=0;j<outHandleCount;j++)
		{
			outHandles[j] = new TPM_HANDLE();
			outHandles[j].initFromTpm(respBuf);
			outHandles[j].toTpm(respParmBuf);
		}
		
		byte[] responseWithoutHandles = null;
		if(haveSessions)
		{
			int restOfParmSize = respBuf.readInt(4);
			responseWithoutHandles = respBuf.readByteArray(restOfParmSize);
			respParmBuf.writeArray(responseWithoutHandles);
		}
		else
		{
			responseWithoutHandles = respBuf.getRemaining();
			respParmBuf.writeArray(responseWithoutHandles);
		}
		
		if(haveSessions)
		{
			processResponseSessions(respBuf);
		}
		
		if(outParms!=null)
		{
			// the handles may've been fragmented in the TPM response, but we 
			// put them back together
			InByteBuf responseData = new InByteBuf(respParmBuf.getBuf());
			outParms.initFromTpm(responseData);
		}
		} finally {
			AllowErrors = false;
			ExpectedResponses = null;
		}
	} // DispatchCommand()
	
	
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

	
	
	TpmDeviceBase device;
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
    

	TPM_HANDLE[] ExplicitSessionHandles;
	//TPM_RC ErrorToExpect;

	
	
}

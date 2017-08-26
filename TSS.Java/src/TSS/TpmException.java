package tss;

import tss.tpm.TPM_RC;

public class TpmException extends RuntimeException
{
	public int EncodedError;
	public TPM_RC ResponseCode;
	public Exception NestedException;
	/**
	 * 
	 */
	private static final long serialVersionUID = 774530215342949864L;

	public TpmException(String message)
	{
		super(message);
	}
	public TpmException(String message, TPM_RC code)
	{
		super(message);
		ResponseCode = code;
	}
	public TpmException(String message, Exception nestedException)
	{
		super(message);
		NestedException = nestedException;
	}
	public TpmException(TPM_RC code)
	{
		super("TPM Exception: " + code.toString());
		ResponseCode = code;
	}
	public TpmException(TPM_RC code, int encodedError)
	{
		super("TPM Exception: " + code.toString());
		ResponseCode = code;
		EncodedError = encodedError;
	}
	
}

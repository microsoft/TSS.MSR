package tss;

import tss.tpm.TPM_CC;
import tss.tpm.TPM_RC;

/**
 * Classes that require callbacks from the tss.Java library should implement this interface
 * @author pengland
 *
 */
public interface TpmCallbackInterface 
{
	public void commandCompleteCallback(
			TPM_CC commandCode, TPM_RC responseCode, 
			byte[] inCommand, byte[] outResponse);
	
}

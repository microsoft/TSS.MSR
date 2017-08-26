package TSS;

import TSS.TpmTypes.TPM_CC;
import TSS.TpmTypes.TPM_RC;

/**
 * Classes that require callbacks from the TSS.Java library should implement this interface
 * @author pengland
 *
 */
public interface TpmCallbackInterface 
{
	public void commandCompleteCallback(
			TPM_CC commandCode, TPM_RC responseCode, 
			byte[] inCommand, byte[] outResponse);
	
}

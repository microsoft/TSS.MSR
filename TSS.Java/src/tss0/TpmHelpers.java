/**
 * 
 */
package tss;

import tss.tpm.*;

/**
 * @author andreyma
 *
 */
public class TpmHelpers
{
	public static int getTpmProperty(Tpm tpm, TPM_PT prop)
	{
	    GetCapabilityResponse caps = tpm.GetCapability(TPM_CAP.TPM_PROPERTIES, prop.toInt(), 1);
	    TPML_TAGGED_TPM_PROPERTY props = (TPML_TAGGED_TPM_PROPERTY)caps.capabilityData;
	    if (props.tpmProperty.length != 1 || props.tpmProperty[0].property != prop)
	    	throw new RuntimeException("Unexpected result of TPM2_GetCapability(TPM_PT.INPUT_BUFFER)");
	    return props.tpmProperty[0].value;
	}

	public static boolean isFmt1(int responseCode)
	{
	    return (responseCode & 0x80) != 0;
	}

	public static TPM_RC fromRawResponse(int responseCode)
	{
		int decodedVal = responseCode & (isFmt1(responseCode) ? 0xBF : 0x97F);
	    return TPM_RC.fromInt(decodedVal);
	}	
}


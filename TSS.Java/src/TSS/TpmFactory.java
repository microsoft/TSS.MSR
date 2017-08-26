package TSS;

import TSS.TpmTypes.TPM_HANDLE;
import TSS.TpmTypes.TPM_RH;
import TSS.TpmTypes.TPM_SU;
/**
 * Contains methods for instantiating TPM instances on top of various TPM-transport connections
 * @author pengland
 *
 */
public class TpmFactory 
{
	/**
	 * Connect to a simulator running on localhost using the default ports.  Note that 
	 * only one Tpm device instance at a time can connect to a local simulator.
	 * 
	 * @return The new Tpm instance with an connected TpmDeviceTcp
	 */
	public static Tpm localTpmSimulator()
	{		
		Tpm tpm = new Tpm();
		TpmDeviceBase device = new TpmDeviceTcp("localhost", 2321);
		device.powerCycle();
		tpm = new Tpm();
		tpm._setDevice(device);
		tpm.Startup(TPM_SU.CLEAR);
		tpm.DictionaryAttackLockReset(TPM_HANDLE.from(TPM_RH.LOCKOUT));
		return tpm;
	}

	/**
	 * Connect to a simulator running on localhost using the default ports.  Note that 
	 * only one Tpm device instance at a time can connect to a local simulator.
	 * 
	 * @param hostName The remote host (dotted IP address or DNS host name)	
	 * @return The new Tpm instance with an connected TpmDeviceTcp
	 */
	public static Tpm remoteTpmSimulator(String hostName)
	{		
		Tpm tpm = new Tpm();
		TpmDeviceBase device = new TpmDeviceTcp(hostName, 2321);
		device.powerCycle();
		tpm = new Tpm();
		tpm._setDevice(device);
		tpm.Startup(TPM_SU.CLEAR);
		tpm.DictionaryAttackLockReset(TPM_HANDLE.from(TPM_RH.LOCKOUT));
		return tpm;
	}

	
	
	/**
	 * Connect to the platform TPM device.  On Windows this will connect via TPM Base Services (TBS).
	 * On Linux this will connect to /dev/tpm.
	 * 
	 * @return The new Tpm instance with an connected TpmDeviceTbs
	 */
	public static Tpm platformTpm()
	{		
		Tpm tpm = new Tpm();
		TpmDeviceTbs device = new TpmDeviceTbs();
		tpm = new Tpm();
		tpm._setDevice(device);
		return tpm;
	}
	

	/**
	 * Connect to a TPM via /dev/tpm (Linux only )

	 * @return The new Tpm instance with an connected TpmDeviceTbs
	 
	
	public static Tpm linux()
	{		
		Tpm tpm = new Tpm();
		TpmDeviceTbs device = new TpmDeviceTbs();
		tpm = new Tpm();
		tpm._setDevice(device);
		return tpm;
	}
*/
	
}

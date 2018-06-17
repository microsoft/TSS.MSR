package tss;

//import java.io.Console;

import tss.tpm.TPM_HANDLE;
import tss.tpm.TPM_RH;
import tss.tpm.TPM_SU;
/**
 * Contains methods for instantiating TPM instances on top of various TPM-transport connections
 * @author pengland
 *
 */
public class TpmFactory 
{
	/**
	 * Connect to a simulator running on localhost using the default ports.
	 * Upon successful connection this method power-cycles the simulator and executes TPM2_Statup(SU_CLEAR) command.
	 * Note that only one Tpm device instance at a time can connect to a local simulator.
	 * 
	 * @return New Tpm instance with an connected TpmDeviceTcp
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
	 * Connect either to a TPM simulator or to a proxy server implementing the TPM simulator protocol. The latter can be
	 * used to access platform TPM on a remote host.
	 * 
	 * @param hostName Remote host (dotted IP address or DNS host name)	
	 * @param port Port number of the TPM command socket. Note that the protocol also uses port+1 to accept control signals.   	
	 * @return New Tpm instance with an connected TpmDeviceTcp
	 */
	public static Tpm remoteTpm(String hostName, int port)
	{		
		Tpm tpm = new Tpm();
		TpmDeviceBase device = new TpmDeviceTcp(hostName, port);
		tpm._setDevice(device);
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
		String osName = System.getProperty("os.name");
		TpmDeviceBase device = null;
		if (osName.contains("Windows"))
			device = new TpmDeviceTbs();
		else
		{
			// First, try to connect to the kernel mode TRM (TPM resource manager) or system TPM
			try {
				device = new TpmDeviceLinux();
			} catch (Exception e) {
				// Now try to connect to the user mode TRM (TPM resource manager) 
				device = new TpmDeviceTcp("localhost", 2323, true);
				//System.out.println("Connected to the user mode TPM Resource Manager");
			}
		}
		tpm._setDevice(device);
		return tpm;
	}
	
}

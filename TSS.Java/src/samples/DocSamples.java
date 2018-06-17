package samples;

import java.io.IOException;

import tss.*;
import tss.tpm.*;

/**
 * The DocSamples class contains the example code described in the tss.Java documentation
 * @author pengland
 *
 */
public class DocSamples 
{
	Tpm tpm;

	public DocSamples()
	{
		// By default, the samples interact with a local TPM simulator.  Change this to
		// use an actual TPM device (not all samples will work.)
		boolean useSimulator = true;
		if(useSimulator)
		{
			tpm = TpmFactory.localTpmSimulator();
		} 
		else
		{
			tpm = TpmFactory.platformTpm();
		}
	}
	public void doAll()
	{
		arrays();
		enumerations();
		pwapAuth();
		errors();

		try 
		{
			// Only shutdown the TPM simulator
			if (tpm._getDevice() instanceof TpmDeviceTcp)
				tpm.Shutdown(TPM_SU.CLEAR);
			tpm.close();
		} catch (IOException e) 
		{
			// don't care
		}
	}


	/**
	 * Demonstrates TPM functions that return or require length-prepended arrays 
	 * (e.g. TPM2B_DIGEST) are translated to use java arrays
	 */
	void arrays() 
	{
		// get 20 bytes of random data
		byte[] r = tpm.GetRandom(20);
		System.out.println("GetRandom: " + Helpers.toHex(r));

		// seed the TPM RNG with some system-provided entropy
		byte[] seedData = new byte[] {2,7,1,8,2,8};
		tpm.StirRandom(seedData);
	}
	
	/**
	 * Demonstrates how TPM constant collections are translated into Java enumerated types
	 */
	void enumerations()
	{
		byte[] dataToHash = new byte[] {3,1,4,1,5,9};
		TPM_ALG_ID hashAlg = TPM_ALG_ID.SHA256;
		HashResponse digestValue = tpm.Hash(dataToHash, hashAlg, TPM_HANDLE.NULL);
		System.out.println(
				"Hash of: "+ Helpers.toHex(dataToHash) +
				"\nWith algorithm: " + hashAlg.toString() + 
				"\nis: "+ Helpers.toHex(digestValue.outHash));
	}

	/**
	 * Demonstrates the simplest form of TPM authorization: Password Authorization, or PWAP 
	 */
	void pwapAuth()
	{
		// Most TPM entities are referenced by handle
		TPM_HANDLE platformHandle = TPM_HANDLE.from(TPM_RH.PLATFORM);
		// The tss.Java TPM_HANDLE class also includes an authValue to be used
		// whenever this handle is used.  When a new handle is created, the authValue
		// is empty (byte[])

		// If we issue a command that needs authorization tss.C++ automatically
		// uses the authValue contained in the handle, in this case the TPM_HANDLE 
		// authValue is byte[0], and the simulator is initialized with a null-authValue 
		// for the platform administrator value, so the following operation will succeed
		tpm.Clear(platformHandle);

		// We can use the "old" platform-auth to install a new value
		byte[] newAuth = new byte[] {1,2,3,4};
		tpm.HierarchyChangeAuth(platformHandle, newAuth);

		// If we want to do further TPM administration as platform administrator, we must 
		// use the new authValue.  This can be set explicitly in the handle  

		platformHandle.AuthValue = newAuth;
		// with the changed authValue, this command will succeed 
		tpm.Clear(platformHandle);

		// And put things back the way they were so that subsequent tests will work
		tpm.HierarchyChangeAuth(platformHandle, new byte[0]);
	}

	void errors()
	{
		// Construct an illegal handle value
		TPM_HANDLE invalidHandle = new TPM_HANDLE(-1);

		// Try to read the associated information.  This will fail
		try 
		{
			tpm.ReadPublic(invalidHandle);
		}
		catch (TpmException e) 
		{
			System.out.println("As expected, the TPM returned an error: " + e.toString());
		}

		// We can also suppress the exception and do an explicit error check
		tpm._allowErrors().ReadPublic(invalidHandle);

		if (tpm._getLastResponseCode() != TPM_RC.SUCCESS) 
		{
			System.out.println("Command failed but no exception was thrown, as expected");
		}

		// If we WANT an error we can turn things around so that an exception is
		// thrown if a specific error is _not_ seen.
		tpm._expectError(TPM_RC.VALUE).ReadPublic(invalidHandle);
	}
}

	


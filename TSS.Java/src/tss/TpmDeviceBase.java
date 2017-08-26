package tss;

import java.io.Closeable;

/**
 * Base class for "transport layers" that communicate with TPM device (e.g. TCP-communication to 
 * a simulator, the TBS interface (on Windows) or /dev/tpm (on linux)  
 * @author pengland
 *
 */
public abstract class TpmDeviceBase implements Closeable
{
	/**
	 * Send the TPM-formatted command byte-array to the TPM
	 * @param command The command bytes
	 */
	public abstract void dispatchCommand(byte[] command);
	/**
	 * Get the response from the TPM.  Will block if the TPM is still busy
	 * @return The TPM-encoded response bytes
	 */
	public abstract byte[] getResponse();
	/**
	 * Is the TPM response ready?
	 * @return Response is ready
	 */
	abstract boolean responseReady();
	
	/**
	 * Power-on the TPM (typically only implemented for simulator).
	 */
    public void powerOn() {throw new UnsupportedOperationException("Not implemented on this TPM device");}

    /**
     * Power-off the TPM (typically only implemented for simulator).
     */
    public void powerOff() {throw new UnsupportedOperationException("Not implemented on this TPM device");}

    /**
     * Power-cycle the TPM (typically only implemented for simulator).
     */
    public void powerCycle() {throw new UnsupportedOperationException("Not implemented on this TPM device");}
    
    
    /**
     * Assert physical presence until PPOff (typically only implemented for simulator).
     */
    public void pPOn() {throw new UnsupportedOperationException("Not implemented on this TPM device");}

    /**
     * Disable physical presence (typically only implemented for simulator).
     */
    public void pPOff() {throw new UnsupportedOperationException("Not implemented on this TPM device");}

    /**
     * Send the commands that follow at the specified locality
     * @param locality Locality (zero to 4)
     */
    public void setLocality(int locality)  {throw new UnsupportedOperationException("Not implemented on this TPM device");}
    
    
}

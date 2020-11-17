package tss;

import java.io.Closeable;

/**
 * Partially abstract base class for classes implementing communication 
 * interface with TPM devices of different kinds (e.g. a simulator, 
 * TBS interface on Windows or /dev/tpm0 on linux).
 * @author pengland
 */
public abstract class TpmDevice implements Closeable
{
    // A set of TSS_TPM_CONN_INFO flags (defined below)
    protected int  TpmInfo = 0;

    // TSS_TPM_CONN_INFO flags. Specify the type and capabilities of the given TPM device
    protected final int
        // Platform hierarchy is enabled, and hardware platform functionality (such
        // as SignalHashStart/Data/End) is available.
        TpmPlatformAvailable = 0x01,

        // The connection represents a TPM Resource Manager (TRM), rather than TPM device.
        // This means context management commands are unavailable, and the handle values
        // returned to the client are virtualized.
        TpmUsesTrm = 0x02,

        // The TRM is in raw mode (i.e. no actual resourse virtualization is performed).
        TpmInRawMode = 0x04,

        // Phisical presence signals (SignalPPOn/Off) are supported.
        TpmSupportsPP = 0x08,

        // Valid only with TpmPlatformAvailable set.
        // System and TPM power control signals (SignalPowerOn/Off) are not supported.
        TpmNoPowerCtl = 0x10,

        // Valid only with TpmPlatformAvailable set.
        // TPM locality cannot be changed.
        TpmNoLocalityCtl = 0x20,

        //
        // Endpoint type descriptors
        //

        // Connection medium is socket.
        // Mutually exclusive with TSS_TbsConn for better error checking
        TpmSocketConn = 0x1000,

        // Connection medium is an OS specific handle.
        // Mutually exclusive with TSS_SocketConn for better error checking
        TpmTbsConn = 0x2000,

        // Valid with TSS_SocketConn only. This is a socket connection to an old
        // version of the Intel's user mode TRM implementation on Linux
        TpmLinuxOldUserModeTrm = 0x4000,

        // Connection via a context representing TCG compliant TCTI connection interface
        TpmTctiConn = 0x8000;


    static void throwUnsupported(String meth)
    {
        throw new UnsupportedOperationException("TpmDevice." + meth + "() is not implemented on this TPM device");
    }

    /** Establishes a connection with the TPM device.
     *  @return  Whether the connection was established
     */
    abstract public boolean connect();

    /** Closes the established connection with the TPM device. */
    @Override
    abstract public void close();

    /** Sends the TPM command buffer byte array to the TPM
     *  @param cmdBuf  TPM command buffer
     */
    public abstract void dispatchCommand(byte[] cmdBuf);

    /** @return  TPM response buffer received from the TPM device */
    public abstract byte[] getResponse();

    /** @return  Whether the TPM response to the previously issued command is ready */
    public abstract boolean responseReady();
    
    /** Powers on/off the TPM.
     * 
     *  Only implemented for TPM simulators and TPM vendors test harness.
     *  @param on New on/off state (true/false correspondingly)
     */
    public void powerCtl(boolean on) { throwUnsupported("powerCtl"); }

    /** Asserts or stops asserting Physical Presence.
     * 
     *  Only implemented for TPM simulators and TPM vendors test harness.
     *  @param on New on/off state (true/false correspondingly)
     */
    public void assertPhysicalPresence(boolean on) { throwUnsupported("assertPhysicalPresence"); }

    /** Sets the locality for subsequent commands.
     * 
     *  Only implemented for TPM simulators and TPM vendors test harness.
     *  @param locality New locality value
     */
    public void setLocality(int locality) { throwUnsupported("setLocality"); }


    /** @return true if the TPM device supports sending/emulation of platform signals,
     *          and if the platform hierarchy is enabled.*/
    public boolean platformAvailable()
    {
        return (TpmInfo & TpmPlatformAvailable) != 0;
    }

    /** @return true if the TPM device can be power cycled programmatically */
    public boolean powerCtlAvailable()
    {
        return (TpmInfo & TpmNoPowerCtl) == 0;
    }

    /** @return true if the TPM device allows changing locality programmatically */
    public boolean localityCtlAvailable()
    {
        return (TpmInfo & TpmNoLocalityCtl) == 0;
    }

    /** @return true if physical presence can be asserted */
    public boolean implementsPhysicalPresence()
    {
        return (TpmInfo & TpmSupportsPP) != 0;
    }

    /** Convenience wrapper for {@link #powerCtl(boolean) powerCtl(true)} */
    public void powerOn()
    {
        powerCtl(true);
    }

    /** Convenience wrapper for {@link #powerCtl(boolean) powerCtl(false)} */
    public void powerOff()
    {
        powerCtl(false);
    }

    /** Convenience wrapper for {@link #powerCtl(boolean) powerCtl(false); powerCtl(true)}.
     *  Power-cycles the TPM device, i.e. turns it off and then back on.
     */
    public void powerCycle()
    {
        powerCtl(false);
        powerCtl(true);
    }
    
    /** Convenience wrapper for {@link #assertPhysicalPresence(boolean) assertPhysicalPresence(true)} */
    public void ppOn()
    {
        assertPhysicalPresence(true);
    }

    /** Convenience wrapper for {@link #assertPhysicalPresence(boolean) assertPhysicalPresence(false)} */
    public void ppOff()
    {
        assertPhysicalPresence(false);
    }
}

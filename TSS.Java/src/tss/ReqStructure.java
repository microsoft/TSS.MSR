package tss;

import tss.tpm.TPM_HANDLE;

/**
 *  Base class for custom (not TPM 2.0 spec defined) auto-generated data
 *  structures representing a TPM command parameters and handles, if any.
 */
public class ReqStructure extends CmdStructure
{
    /** @return An array of TPM handles contained in this TPM request data structure */
    public TPM_HANDLE[] getHandles() { return null; }

    /** @return Number of authorization TPM handles contained in this data structure */
    public int numAuthHandles() { return 0; }

    /** ISerializable method */
    public String typeName () { return "ReqStructure"; }
}

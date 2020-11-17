package tss;

import tss.tpm.TPM_HANDLE;

/**
 *  Base class for custom (not TPM 2.0 spec defined) auto-generated data
 *  structures representing a TPM response parameters and handles, if any.
 */
public class RespStructure extends CmdStructure
{
    /** @return The TPM handle contained in this TPM response data structure */
    public TPM_HANDLE getHandle() { return null; }

    /** Sets this structure's handle field (TPM_HANDLE) if it is present
     * @param h The handle value
     */
    public void setHandle(TPM_HANDLE h) {}

    /** ISerializable method
     * @return Human readable type name for the purposes of text serialization and pretty-printing
     */
    public String typeName () { return "RespStructure"; }
}

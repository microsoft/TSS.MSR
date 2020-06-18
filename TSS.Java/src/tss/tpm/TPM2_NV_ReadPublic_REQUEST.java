package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to read the public area and Name of an NV Index. The public area
 *  of an Index is not privacy-sensitive and no authorization is required to read this data.
 */
public class TPM2_NV_ReadPublic_REQUEST extends TpmStructure
{
    /** The NV Index
     *  Auth Index: None
     */
    public TPM_HANDLE nvIndex;
    
    public TPM2_NV_ReadPublic_REQUEST() { nvIndex = new TPM_HANDLE(); }
    
    /** @param _nvIndex The NV Index
     *         Auth Index: None
     */
    public TPM2_NV_ReadPublic_REQUEST(TPM_HANDLE _nvIndex) { nvIndex = _nvIndex; }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_ReadPublic_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
    }
}

//<<<

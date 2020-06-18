package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows access to the public area of a loaded object.  */
public class TPM2_ReadPublic_REQUEST extends TpmStructure
{
    /** TPM handle of an object
     *  Auth Index: None
     */
    public TPM_HANDLE objectHandle;
    
    public TPM2_ReadPublic_REQUEST() { objectHandle = new TPM_HANDLE(); }
    
    /** @param _objectHandle TPM handle of an object
     *         Auth Index: None
     */
    public TPM2_ReadPublic_REQUEST(TPM_HANDLE _objectHandle) { objectHandle = _objectHandle; }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ReadPublic_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "objectHandle", objectHandle);
    }
}

//<<<

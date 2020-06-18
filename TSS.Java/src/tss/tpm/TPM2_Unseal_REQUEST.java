package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns the data in a loaded Sealed Data Object.  */
public class TPM2_Unseal_REQUEST extends TpmStructure
{
    /** Handle of a loaded data object
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE itemHandle;
    
    public TPM2_Unseal_REQUEST() { itemHandle = new TPM_HANDLE(); }
    
    /** @param _itemHandle Handle of a loaded data object
     *         Auth Index: 1
     *         Auth Role: USER
     */
    public TPM2_Unseal_REQUEST(TPM_HANDLE _itemHandle) { itemHandle = _itemHandle; }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Unseal_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "itemHandle", itemHandle);
    }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command saves a session context, object context, or sequence object
 *  context outside the TPM.
 */
public class TPM2_ContextSave_REQUEST extends TpmStructure
{
    /**
     *  handle of the resource to save
     *  Auth Index: None
     */
    public TPM_HANDLE saveHandle;
    
    public TPM2_ContextSave_REQUEST() { saveHandle = new TPM_HANDLE(); }
    
    /**
     *  @param _saveHandle handle of the resource to save
     *         Auth Index: None
     */
    public TPM2_ContextSave_REQUEST(TPM_HANDLE _saveHandle) { saveHandle = _saveHandle; }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ContextSave_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "saveHandle", saveHandle);
    }
}

//<<<

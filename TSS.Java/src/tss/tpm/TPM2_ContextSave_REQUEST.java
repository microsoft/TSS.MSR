package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command saves a session context, object context, or sequence object context outside the TPM.
*/
public class TPM2_ContextSave_REQUEST extends TpmStructure
{
    /**
     * This command saves a session context, object context, or sequence object context outside the TPM.
     * 
     * @param _saveHandle handle of the resource to save Auth Index: None
     */
    public TPM2_ContextSave_REQUEST(TPM_HANDLE _saveHandle)
    {
        saveHandle = _saveHandle;
    }
    /**
    * This command saves a session context, object context, or sequence object context outside the TPM.
    */
    public TPM2_ContextSave_REQUEST() {};
    /**
    * handle of the resource to save Auth Index: None
    */
    public TPM_HANDLE saveHandle;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        saveHandle.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        saveHandle = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_ContextSave_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ContextSave_REQUEST ret = new TPM2_ContextSave_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_ContextSave_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ContextSave_REQUEST ret = new TPM2_ContextSave_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
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
    };
    
    
};

//<<<


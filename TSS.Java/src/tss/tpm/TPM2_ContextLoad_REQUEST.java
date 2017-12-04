package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to reload a context that has been saved by TPM2_ContextSave().
*/
public class TPM2_ContextLoad_REQUEST extends TpmStructure
{
    /**
     * This command is used to reload a context that has been saved by TPM2_ContextSave().
     * 
     * @param _context the context blob
     */
    public TPM2_ContextLoad_REQUEST(TPMS_CONTEXT _context)
    {
        context = _context;
    }
    /**
    * This command is used to reload a context that has been saved by TPM2_ContextSave().
    */
    public TPM2_ContextLoad_REQUEST() {};
    /**
    * the context blob
    */
    public TPMS_CONTEXT context;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        context.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        context = TPMS_CONTEXT.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_ContextLoad_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ContextLoad_REQUEST ret = new TPM2_ContextLoad_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_ContextLoad_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ContextLoad_REQUEST ret = new TPM2_ContextLoad_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ContextLoad_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_CONTEXT", "context", context);
    };
    
    
};

//<<<


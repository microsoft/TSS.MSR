package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command saves a session context, object context, or sequence object context outside the TPM.
*/
public class ContextSaveResponse extends TpmStructure
{
    /**
     * This command saves a session context, object context, or sequence object context outside the TPM.
     * 
     * @param _context -
     */
    public ContextSaveResponse(TPMS_CONTEXT _context)
    {
        context = _context;
    }
    /**
    * This command saves a session context, object context, or sequence object context outside the TPM.
    */
    public ContextSaveResponse() {};
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
    public static ContextSaveResponse fromTpm (byte[] x) 
    {
        ContextSaveResponse ret = new ContextSaveResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ContextSaveResponse fromTpm (InByteBuf buf) 
    {
        ContextSaveResponse ret = new ContextSaveResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ContextSave_RESPONSE");
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


package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to reload a context that has been saved by TPM2_ContextSave().
*/
public class ContextLoadResponse extends TpmStructure
{
    /**
     * This command is used to reload a context that has been saved by TPM2_ContextSave().
     * 
     * @param _handle the handle assigned to the resource after it has been successfully loaded
     */
    public ContextLoadResponse(TPM_HANDLE _handle)
    {
        handle = _handle;
    }
    /**
    * This command is used to reload a context that has been saved by TPM2_ContextSave().
    */
    public ContextLoadResponse() {};
    /**
    * the handle assigned to the resource after it has been successfully loaded
    */
    public TPM_HANDLE handle;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        handle.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static ContextLoadResponse fromTpm (byte[] x) 
    {
        ContextLoadResponse ret = new ContextLoadResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ContextLoadResponse fromTpm (InByteBuf buf) 
    {
        ContextLoadResponse ret = new ContextLoadResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ContextLoad_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
    };
    
    
};

//<<<


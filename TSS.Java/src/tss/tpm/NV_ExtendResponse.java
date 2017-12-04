package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command extends a value to an area in NV memory that was previously defined by TPM2_NV_DefineSpace.
*/
public class NV_ExtendResponse extends TpmStructure
{
    /**
     * This command extends a value to an area in NV memory that was previously defined by TPM2_NV_DefineSpace.
     */
    public NV_ExtendResponse()
    {
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static NV_ExtendResponse fromTpm (byte[] x) 
    {
        NV_ExtendResponse ret = new NV_ExtendResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static NV_ExtendResponse fromTpm (InByteBuf buf) 
    {
        NV_ExtendResponse ret = new NV_ExtendResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_Extend_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
    };
    
    
};

//<<<


package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command removes an Index from the TPM.
*/
public class NV_UndefineSpaceResponse extends TpmStructure
{
    /**
     * This command removes an Index from the TPM.
     */
    public NV_UndefineSpaceResponse()
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
    public static NV_UndefineSpaceResponse fromTpm (byte[] x) 
    {
        NV_UndefineSpaceResponse ret = new NV_UndefineSpaceResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static NV_UndefineSpaceResponse fromTpm (InByteBuf buf) 
    {
        NV_UndefineSpaceResponse ret = new NV_UndefineSpaceResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_UndefineSpace_RESPONSE");
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


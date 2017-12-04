package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows removal of a platform-created NV Index that has TPMA_NV_POLICY_DELETE SET.
*/
public class NV_UndefineSpaceSpecialResponse extends TpmStructure
{
    /**
     * This command allows removal of a platform-created NV Index that has TPMA_NV_POLICY_DELETE SET.
     */
    public NV_UndefineSpaceSpecialResponse()
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
    public static NV_UndefineSpaceSpecialResponse fromTpm (byte[] x) 
    {
        NV_UndefineSpaceSpecialResponse ret = new NV_UndefineSpaceSpecialResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static NV_UndefineSpaceSpecialResponse fromTpm (InByteBuf buf) 
    {
        NV_UndefineSpaceSpecialResponse ret = new NV_UndefineSpaceSpecialResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_UndefineSpaceSpecial_RESPONSE");
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


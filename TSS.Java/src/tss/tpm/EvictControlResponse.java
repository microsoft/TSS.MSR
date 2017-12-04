package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows certain Transient Objects to be made persistent or a persistent object to be evicted.
*/
public class EvictControlResponse extends TpmStructure
{
    /**
     * This command allows certain Transient Objects to be made persistent or a persistent object to be evicted.
     */
    public EvictControlResponse()
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
    public static EvictControlResponse fromTpm (byte[] x) 
    {
        EvictControlResponse ret = new EvictControlResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static EvictControlResponse fromTpm (InByteBuf buf) 
    {
        EvictControlResponse ret = new EvictControlResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_EvictControl_RESPONSE");
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


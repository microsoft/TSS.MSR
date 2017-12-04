package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows qualification of duplication to allow duplication to a selected new parent.
*/
public class PolicyDuplicationSelectResponse extends TpmStructure
{
    /**
     * This command allows qualification of duplication to allow duplication to a selected new parent.
     */
    public PolicyDuplicationSelectResponse()
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
    public static PolicyDuplicationSelectResponse fromTpm (byte[] x) 
    {
        PolicyDuplicationSelectResponse ret = new PolicyDuplicationSelectResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static PolicyDuplicationSelectResponse fromTpm (InByteBuf buf) 
    {
        PolicyDuplicationSelectResponse ret = new PolicyDuplicationSelectResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyDuplicationSelect_RESPONSE");
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


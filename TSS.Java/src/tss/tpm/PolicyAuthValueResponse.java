package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows a policy to be bound to the authorization value of the authorized entity.
*/
public class PolicyAuthValueResponse extends TpmStructure
{
    /**
     * This command allows a policy to be bound to the authorization value of the authorized entity.
     */
    public PolicyAuthValueResponse()
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
    public static PolicyAuthValueResponse fromTpm (byte[] x) 
    {
        PolicyAuthValueResponse ret = new PolicyAuthValueResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static PolicyAuthValueResponse fromTpm (InByteBuf buf) 
    {
        PolicyAuthValueResponse ret = new PolicyAuthValueResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyAuthValue_RESPONSE");
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


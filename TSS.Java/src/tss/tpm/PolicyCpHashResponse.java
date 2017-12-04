package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to allow a policy to be bound to a specific command and command parameters.
*/
public class PolicyCpHashResponse extends TpmStructure
{
    /**
     * This command is used to allow a policy to be bound to a specific command and command parameters.
     */
    public PolicyCpHashResponse()
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
    public static PolicyCpHashResponse fromTpm (byte[] x) 
    {
        PolicyCpHashResponse ret = new PolicyCpHashResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static PolicyCpHashResponse fromTpm (InByteBuf buf) 
    {
        PolicyCpHashResponse ret = new PolicyCpHashResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyCpHash_RESPONSE");
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


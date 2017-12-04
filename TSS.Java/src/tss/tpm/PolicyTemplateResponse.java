package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows a policy to be bound to a specific creation template. This is most useful for an object creation command such as TPM2_Create(), TPM2_CreatePrimary(), or TPM2_CreateLoaded().
*/
public class PolicyTemplateResponse extends TpmStructure
{
    /**
     * This command allows a policy to be bound to a specific creation template. This is most useful for an object creation command such as TPM2_Create(), TPM2_CreatePrimary(), or TPM2_CreateLoaded().
     */
    public PolicyTemplateResponse()
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
    public static PolicyTemplateResponse fromTpm (byte[] x) 
    {
        PolicyTemplateResponse ret = new PolicyTemplateResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static PolicyTemplateResponse fromTpm (InByteBuf buf) 
    {
        PolicyTemplateResponse ret = new PolicyTemplateResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyTemplate_RESPONSE");
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


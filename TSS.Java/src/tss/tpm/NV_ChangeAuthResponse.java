package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command allows the authorization secret for an NV Index to be changed.
*/
public class NV_ChangeAuthResponse extends TpmStructure
{
    /**
     * This command allows the authorization secret for an NV Index to be changed.
     */
    public NV_ChangeAuthResponse()
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
    public static NV_ChangeAuthResponse fromTpm (byte[] x) 
    {
        NV_ChangeAuthResponse ret = new NV_ChangeAuthResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static NV_ChangeAuthResponse fromTpm (InByteBuf buf) 
    {
        NV_ChangeAuthResponse ret = new NV_ChangeAuthResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_ChangeAuth_RESPONSE");
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


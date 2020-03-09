package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to set the time remaining before an Authenticated Countdown Timer (ACT) expires.
*/
public class ACT_SetTimeoutResponse extends TpmStructure
{
    /**
     * This command is used to set the time remaining before an Authenticated Countdown Timer (ACT) expires.
     */
    public ACT_SetTimeoutResponse()
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
    public static ACT_SetTimeoutResponse fromTpm (byte[] x) 
    {
        ACT_SetTimeoutResponse ret = new ACT_SetTimeoutResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ACT_SetTimeoutResponse fromTpm (InByteBuf buf) 
    {
        ACT_SetTimeoutResponse ret = new ACT_SetTimeoutResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ACT_SetTimeout_RESPONSE");
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


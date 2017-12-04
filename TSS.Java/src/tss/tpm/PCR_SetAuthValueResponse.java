package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command changes the authValue of a PCR or group of PCR.
*/
public class PCR_SetAuthValueResponse extends TpmStructure
{
    /**
     * This command changes the authValue of a PCR or group of PCR.
     */
    public PCR_SetAuthValueResponse()
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
    public static PCR_SetAuthValueResponse fromTpm (byte[] x) 
    {
        PCR_SetAuthValueResponse ret = new PCR_SetAuthValueResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static PCR_SetAuthValueResponse fromTpm (InByteBuf buf) 
    {
        PCR_SetAuthValueResponse ret = new PCR_SetAuthValueResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_SetAuthValue_RESPONSE");
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


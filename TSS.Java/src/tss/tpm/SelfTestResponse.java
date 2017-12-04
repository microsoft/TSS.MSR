package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command causes the TPM to perform a test of its capabilities. If the fullTest is YES, the TPM will test all functions. If fullTest = NO, the TPM will only test those functions that have not previously been tested.
*/
public class SelfTestResponse extends TpmStructure
{
    /**
     * This command causes the TPM to perform a test of its capabilities. If the fullTest is YES, the TPM will test all functions. If fullTest = NO, the TPM will only test those functions that have not previously been tested.
     */
    public SelfTestResponse()
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
    public static SelfTestResponse fromTpm (byte[] x) 
    {
        SelfTestResponse ret = new SelfTestResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static SelfTestResponse fromTpm (InByteBuf buf) 
    {
        SelfTestResponse ret = new SelfTestResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_SelfTest_RESPONSE");
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


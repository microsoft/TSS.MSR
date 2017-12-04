package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command adjusts the rate of advance of Clock and Time to provide a better approximation to real time.
*/
public class ClockRateAdjustResponse extends TpmStructure
{
    /**
     * This command adjusts the rate of advance of Clock and Time to provide a better approximation to real time.
     */
    public ClockRateAdjustResponse()
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
    public static ClockRateAdjustResponse fromTpm (byte[] x) 
    {
        ClockRateAdjustResponse ret = new ClockRateAdjustResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ClockRateAdjustResponse fromTpm (InByteBuf buf) 
    {
        ClockRateAdjustResponse ret = new ClockRateAdjustResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ClockRateAdjust_RESPONSE");
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


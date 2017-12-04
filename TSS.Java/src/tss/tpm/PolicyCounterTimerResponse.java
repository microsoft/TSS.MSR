package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to cause conditional gating of a policy based on the contents of the TPMS_TIME_INFO structure.
*/
public class PolicyCounterTimerResponse extends TpmStructure
{
    /**
     * This command is used to cause conditional gating of a policy based on the contents of the TPMS_TIME_INFO structure.
     */
    public PolicyCounterTimerResponse()
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
    public static PolicyCounterTimerResponse fromTpm (byte[] x) 
    {
        PolicyCounterTimerResponse ret = new PolicyCounterTimerResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static PolicyCounterTimerResponse fromTpm (InByteBuf buf) 
    {
        PolicyCounterTimerResponse ret = new PolicyCounterTimerResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyCounterTimer_RESPONSE");
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


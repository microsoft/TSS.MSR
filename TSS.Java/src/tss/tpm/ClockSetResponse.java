package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to advance the value of the TPMs Clock. The command will fail if newTime is less than the current value of Clock or if the new time is greater than FFFF00000000000016. If both of these checks succeed, Clock is set to newTime. If either of these checks fails, the TPM shall return TPM_RC_VALUE and make no change to Clock.
*/
public class ClockSetResponse extends TpmStructure
{
    /**
     * This command is used to advance the value of the TPMs Clock. The command will fail if newTime is less than the current value of Clock or if the new time is greater than FFFF00000000000016. If both of these checks succeed, Clock is set to newTime. If either of these checks fails, the TPM shall return TPM_RC_VALUE and make no change to Clock.
     */
    public ClockSetResponse()
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
    public static ClockSetResponse fromTpm (byte[] x) 
    {
        ClockSetResponse ret = new ClockSetResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ClockSetResponse fromTpm (InByteBuf buf) 
    {
        ClockSetResponse ret = new ClockSetResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ClockSet_RESPONSE");
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


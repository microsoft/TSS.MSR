package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command reads the current TPMS_TIME_INFO structure that contains the current setting of Time, Clock, resetCount, and restartCount.
*/
public class ReadClockResponse extends TpmStructure
{
    /**
     * This command reads the current TPMS_TIME_INFO structure that contains the current setting of Time, Clock, resetCount, and restartCount.
     * 
     * @param _currentTime -
     */
    public ReadClockResponse(TPMS_TIME_INFO _currentTime)
    {
        currentTime = _currentTime;
    }
    /**
    * This command reads the current TPMS_TIME_INFO structure that contains the current setting of Time, Clock, resetCount, and restartCount.
    */
    public ReadClockResponse() {};
    public TPMS_TIME_INFO currentTime;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        currentTime.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        currentTime = TPMS_TIME_INFO.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static ReadClockResponse fromTpm (byte[] x) 
    {
        ReadClockResponse ret = new ReadClockResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static ReadClockResponse fromTpm (InByteBuf buf) 
    {
        ReadClockResponse ret = new ReadClockResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ReadClock_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_TIME_INFO", "currentTime", currentTime);
    };
    
    
};

//<<<


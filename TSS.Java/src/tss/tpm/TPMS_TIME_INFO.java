package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used in, e.g., the TPM2_GetTime() attestation and TPM2_ReadClock(). */
public class TPMS_TIME_INFO extends TpmStructure
{
    /**
     *  time in milliseconds since the TIme circuit was last reset
     *  This structure element is used to report on the TPM's Time value.
     */
    public long time;
    
    /** a structure containing the clock information */
    public TPMS_CLOCK_INFO clockInfo;
    
    public TPMS_TIME_INFO() {}
    
    /**
     *  @param _time time in milliseconds since the TIme circuit was last reset
     *         This structure element is used to report on the TPM's Time value.
     *  @param _clockInfo a structure containing the clock information
     */
    public TPMS_TIME_INFO(long _time, TPMS_CLOCK_INFO _clockInfo)
    {
        time = _time;
        clockInfo = _clockInfo;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt64(time);
        clockInfo.toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        time = buf.readInt64();
        clockInfo = TPMS_CLOCK_INFO.fromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static TPMS_TIME_INFO fromTpm (byte[] x) 
    {
        TPMS_TIME_INFO ret = new TPMS_TIME_INFO();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMS_TIME_INFO fromTpm (InByteBuf buf) 
    {
        TPMS_TIME_INFO ret = new TPMS_TIME_INFO();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_TIME_INFO");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "long", "time", time);
        _p.add(d, "TPMS_CLOCK_INFO", "clockInfo", clockInfo);
    }
}

//<<<

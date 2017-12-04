package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is used in the TPM2_GetTime() attestation.
*/
public class TPMS_TIME_INFO extends TpmStructure
{
    /**
     * This structure is used in the TPM2_GetTime() attestation.
     * 
     * @param _time time in milliseconds since the last _TPM_Init() or TPM2_Startup() This structure element is used to report on the TPM's Time value. 
     * @param _clockInfo a structure containing the clock information
     */
    public TPMS_TIME_INFO(long _time,TPMS_CLOCK_INFO _clockInfo)
    {
        time = _time;
        clockInfo = _clockInfo;
    }
    /**
    * This structure is used in the TPM2_GetTime() attestation.
    */
    public TPMS_TIME_INFO() {};
    /**
    * time in milliseconds since the last _TPM_Init() or TPM2_Startup() This structure element is used to report on the TPM's Time value.
    */
    public long time;
    /**
    * a structure containing the clock information
    */
    public TPMS_CLOCK_INFO clockInfo;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.write(time);
        clockInfo.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        time = buf.readLong();
        clockInfo = TPMS_CLOCK_INFO.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
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
        _p.add(d, "ulong", "time", time);
        _p.add(d, "TPMS_CLOCK_INFO", "clockInfo", clockInfo);
    };
    
    
};

//<<<


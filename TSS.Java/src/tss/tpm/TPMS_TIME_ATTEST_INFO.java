package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is used when the TPM performs TPM2_GetTime.
*/
public class TPMS_TIME_ATTEST_INFO extends TpmStructure implements TPMU_ATTEST 
{
    /**
     * This structure is used when the TPM performs TPM2_GetTime.
     * 
     * @param _time the Time, Clock, resetCount, restartCount, and Safe indicator 
     * @param _firmwareVersion a TPM vendor-specific value indicating the version number of the firmware
     */
    public TPMS_TIME_ATTEST_INFO(TPMS_TIME_INFO _time,long _firmwareVersion)
    {
        time = _time;
        firmwareVersion = _firmwareVersion;
    }
    /**
    * This structure is used when the TPM performs TPM2_GetTime.
    */
    public TPMS_TIME_ATTEST_INFO() {};
    /**
    * the Time, Clock, resetCount, restartCount, and Safe indicator
    */
    public TPMS_TIME_INFO time;
    /**
    * a TPM vendor-specific value indicating the version number of the firmware
    */
    public long firmwareVersion;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        time.toTpm(buf);
        buf.write(firmwareVersion);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        time = TPMS_TIME_INFO.fromTpm(buf);
        firmwareVersion = buf.readLong();
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_TIME_ATTEST_INFO fromTpm (byte[] x) 
    {
        TPMS_TIME_ATTEST_INFO ret = new TPMS_TIME_ATTEST_INFO();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_TIME_ATTEST_INFO fromTpm (InByteBuf buf) 
    {
        TPMS_TIME_ATTEST_INFO ret = new TPMS_TIME_ATTEST_INFO();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_TIME_ATTEST_INFO");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_TIME_INFO", "time", time);
        _p.add(d, "ulong", "firmwareVersion", firmwareVersion);
    };
    
    
};

//<<<


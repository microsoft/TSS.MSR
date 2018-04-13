package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is used in each of the attestation commands.
*/
public class TPMS_CLOCK_INFO extends TpmStructure
{
    /**
     * This structure is used in each of the attestation commands.
     * 
     * @param _clock time value in milliseconds that advances while the TPM is powered NOTE The interpretation of the time-origin (clock=0) is out of the scope of this specification, although Coordinated Universal Time (UTC) is expected to be a common convention. This structure element is used to report on the TPM's Clock value. This value is reset to zero when the Storage Primary Seed is changed (TPM2_Clear()). This value may be advanced by TPM2_ClockSet(). 
     * @param _resetCount number of occurrences of TPM Reset since the last TPM2_Clear() 
     * @param _restartCount number of times that TPM2_Shutdown() or _TPM_Hash_Start have occurred since the last TPM Reset or TPM2_Clear(). 
     * @param _safe no value of Clock greater than the current value of Clock has been previously reported by the TPM. Set to YES on TPM2_Clear().
     */
    public TPMS_CLOCK_INFO(long _clock,int _resetCount,int _restartCount,byte _safe)
    {
        clock = _clock;
        resetCount = _resetCount;
        restartCount = _restartCount;
        safe = _safe;
    }
    /**
    * This structure is used in each of the attestation commands.
    */
    public TPMS_CLOCK_INFO() {};
    /**
    * time value in milliseconds that advances while the TPM is powered NOTE The interpretation of the time-origin (clock=0) is out of the scope of this specification, although Coordinated Universal Time (UTC) is expected to be a common convention. This structure element is used to report on the TPM's Clock value. This value is reset to zero when the Storage Primary Seed is changed (TPM2_Clear()). This value may be advanced by TPM2_ClockSet().
    */
    public long clock;
    /**
    * number of occurrences of TPM Reset since the last TPM2_Clear()
    */
    public int resetCount;
    /**
    * number of times that TPM2_Shutdown() or _TPM_Hash_Start have occurred since the last TPM Reset or TPM2_Clear().
    */
    public int restartCount;
    /**
    * no value of Clock greater than the current value of Clock has been previously reported by the TPM. Set to YES on TPM2_Clear().
    */
    public byte safe;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.write(clock);
        buf.write(resetCount);
        buf.write(restartCount);
        buf.write(safe);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        clock = buf.readLong();
        resetCount =  buf.readInt(4);
        restartCount =  buf.readInt(4);
        safe = (byte) buf.readInt(1);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_CLOCK_INFO fromTpm (byte[] x) 
    {
        TPMS_CLOCK_INFO ret = new TPMS_CLOCK_INFO();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_CLOCK_INFO fromTpm (InByteBuf buf) 
    {
        TPMS_CLOCK_INFO ret = new TPMS_CLOCK_INFO();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_CLOCK_INFO");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "ulong", "clock", clock);
        _p.add(d, "uint", "resetCount", resetCount);
        _p.add(d, "uint", "restartCount", restartCount);
        _p.add(d, "BYTE", "safe", safe);
    };
    
    
};

//<<<


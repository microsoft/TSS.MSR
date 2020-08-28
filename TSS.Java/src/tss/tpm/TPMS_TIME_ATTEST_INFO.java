package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used when the TPM performs TPM2_GetTime.  */
public class TPMS_TIME_ATTEST_INFO extends TpmStructure implements TPMU_ATTEST
{
    /** The Time, Clock, resetCount, restartCount, and Safe indicator  */
    public TPMS_TIME_INFO time;

    /** A TPM vendor-specific value indicating the version number of the firmware  */
    public long firmwareVersion;

    public TPMS_TIME_ATTEST_INFO() {}

    /** @param _time The Time, Clock, resetCount, restartCount, and Safe indicator
     *  @param _firmwareVersion A TPM vendor-specific value indicating the version number of
     *  the firmware
     */
    public TPMS_TIME_ATTEST_INFO(TPMS_TIME_INFO _time, long _firmwareVersion)
    {
        time = _time;
        firmwareVersion = _firmwareVersion;
    }

    /** TpmUnion method  */
    public TPM_ST GetUnionSelector() { return TPM_ST.ATTEST_TIME; }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        time.toTpm(buf);
        buf.writeInt64(firmwareVersion);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        time = TPMS_TIME_INFO.fromTpm(buf);
        firmwareVersion = buf.readInt64();
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPMS_TIME_ATTEST_INFO fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_TIME_ATTEST_INFO.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_TIME_ATTEST_INFO fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPMS_TIME_ATTEST_INFO fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_TIME_ATTEST_INFO.class);
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
        _p.add(d, "long", "firmwareVersion", firmwareVersion);
    }
}

//<<<

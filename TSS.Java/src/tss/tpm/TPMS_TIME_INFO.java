package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used in, e.g., the TPM2_GetTime() attestation and TPM2_ReadClock().  */
public class TPMS_TIME_INFO extends TpmStructure
{
    /** Time in milliseconds since the TIme circuit was last reset
     *  This structure element is used to report on the TPM's Time value.
     */
    public long time;
    
    /** A structure containing the clock information  */
    public TPMS_CLOCK_INFO clockInfo;
    
    public TPMS_TIME_INFO() {}
    
    /** @param _time Time in milliseconds since the TIme circuit was last reset
     *         This structure element is used to report on the TPM's Time value.
     *  @param _clockInfo A structure containing the clock information
     */
    public TPMS_TIME_INFO(long _time, TPMS_CLOCK_INFO _clockInfo)
    {
        time = _time;
        clockInfo = _clockInfo;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeInt64(time);
        clockInfo.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        time = buf.readInt64();
        clockInfo = TPMS_CLOCK_INFO.fromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_TIME_INFO fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_TIME_INFO.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_TIME_INFO fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_TIME_INFO fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_TIME_INFO.class);
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

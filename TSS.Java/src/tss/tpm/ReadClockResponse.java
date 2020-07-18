package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command reads the current TPMS_TIME_INFO structure that contains the current
 *  setting of Time, Clock, resetCount, and restartCount.
 */
public class ReadClockResponse extends RespStructure
{
    public TPMS_TIME_INFO currentTime;
    
    public ReadClockResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { currentTime.toTpm(buf); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { currentTime = TPMS_TIME_INFO.fromTpm(buf); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static ReadClockResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(ReadClockResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static ReadClockResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static ReadClockResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(ReadClockResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("ReadClockResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_TIME_INFO", "currentTime", currentTime);
    }
}

//<<<

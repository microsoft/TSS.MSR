package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command adds the last part of data, if any, to an Event Sequence and returns the
 *  result in a digest list. If pcrHandle references a PCR and not TPM_RH_NULL, then the
 *  returned digest list is processed in the same manner as the digest list input
 *  parameter to TPM2_PCR_Extend(). That is, if a bank contains a PCR associated with
 *  pcrHandle, it is extended with the associated digest value from the list.
 */
public class EventSequenceCompleteResponse extends RespStructure
{
    /** List of digests computed for the PCR  */
    public TPMT_HA[] results;
    
    public EventSequenceCompleteResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(results); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { results = buf.readObjArr(TPMT_HA.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static EventSequenceCompleteResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(EventSequenceCompleteResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static EventSequenceCompleteResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static EventSequenceCompleteResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(EventSequenceCompleteResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("EventSequenceCompleteResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_HA", "results", results);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(4, 66); }
}

//<<<

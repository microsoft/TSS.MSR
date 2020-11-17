package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to cause an update to the indicated PCR. */
public class PCR_EventResponse extends RespStructure
{
    public TPMT_HA[] digests;

    public PCR_EventResponse() {}

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(digests); }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf) { digests = buf.readObjArr(TPMT_HA.class); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static PCR_EventResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(PCR_EventResponse.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static PCR_EventResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static PCR_EventResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(PCR_EventResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("PCR_EventResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_HA[]", "digests", digests);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(4, 66); }
}

//<<<

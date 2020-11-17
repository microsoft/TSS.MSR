package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command adds the last part of data, if any, to a hash/HMAC sequence and returns
 *  the result.
 */
public class SequenceCompleteResponse extends RespStructure
{
    /** The returned HMAC or digest in a sized buffer */
    public byte[] result;

    /** Ticket indicating that the sequence of octets used to compute outDigest did not start
     *  with TPM_GENERATED_VALUE
     *  This is a NULL Ticket when the sequence is HMAC.
     */
    public TPMT_TK_HASHCHECK validation;

    public SequenceCompleteResponse() {}

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(result);
        validation.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        result = buf.readSizedByteBuf();
        validation = TPMT_TK_HASHCHECK.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static SequenceCompleteResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(SequenceCompleteResponse.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static SequenceCompleteResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static SequenceCompleteResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(SequenceCompleteResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("SequenceCompleteResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "result", result);
        _p.add(d, "TPMT_TK_HASHCHECK", "validation", validation);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

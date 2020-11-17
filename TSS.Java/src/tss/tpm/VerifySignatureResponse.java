package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command uses loaded keys to validate a signature on a message with the message
 *  digest passed to the TPM.
 */
public class VerifySignatureResponse extends RespStructure
{
    public TPMT_TK_VERIFIED validation;

    public VerifySignatureResponse() {}

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf) { validation.toTpm(buf); }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf) { validation = TPMT_TK_VERIFIED.fromTpm(buf); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static VerifySignatureResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(VerifySignatureResponse.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static VerifySignatureResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static VerifySignatureResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(VerifySignatureResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("VerifySignatureResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_TK_VERIFIED", "validation", validation);
    }
}

//<<<

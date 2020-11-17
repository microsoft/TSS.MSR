package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This list is used to convey a list of digest values. This type is used in
 *  TPM2_PolicyOR() and in TPM2_PCR_Read().
 */
public class TPML_DIGEST extends TpmStructure
{
    /** A list of digests
     *  For TPM2_PolicyOR(), all digests will have been computed using the digest of the
     *  policy session. For TPM2_PCR_Read(), each digest will be the size of the digest for
     *  the bank containing the PCR.
     */
    public TPM2B_DIGEST[] digests;

    public TPML_DIGEST() {}

    /** @param _digests A list of digests
     *         For TPM2_PolicyOR(), all digests will have been computed using the digest of the
     *         policy session. For TPM2_PCR_Read(), each digest will be the size of the digest
     *  for
     *         the bank containing the PCR.
     */
    public TPML_DIGEST(TPM2B_DIGEST[] _digests) { digests = _digests; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(digests); }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf) { digests = buf.readObjArr(TPM2B_DIGEST.class); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPML_DIGEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPML_DIGEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPML_DIGEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPML_DIGEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPML_DIGEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_DIGEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM2B_DIGEST[]", "digests", digests);
    }
}

//<<<

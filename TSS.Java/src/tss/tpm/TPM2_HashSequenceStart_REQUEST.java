package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command starts a hash or an Event Sequence. If hashAlg is an implemented hash,
 *  then a hash sequence is started. If hashAlg is TPM_ALG_NULL, then an Event Sequence is
 *  started. If hashAlg is neither an implemented algorithm nor TPM_ALG_NULL, then the TPM
 *  shall return TPM_RC_HASH.
 */
public class TPM2_HashSequenceStart_REQUEST extends ReqStructure
{
    /** Authorization value for subsequent use of the sequence  */
    public byte[] auth;

    /** The hash algorithm to use for the hash sequence
     *  An Event Sequence starts if this is TPM_ALG_NULL.
     */
    public TPM_ALG_ID hashAlg;

    public TPM2_HashSequenceStart_REQUEST() { hashAlg = TPM_ALG_ID.NULL; }

    /** @param _auth Authorization value for subsequent use of the sequence
     *  @param _hashAlg The hash algorithm to use for the hash sequence
     *         An Event Sequence starts if this is TPM_ALG_NULL.
     */
    public TPM2_HashSequenceStart_REQUEST(byte[] _auth, TPM_ALG_ID _hashAlg)
    {
        auth = _auth;
        hashAlg = _hashAlg;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(auth);
        hashAlg.toTpm(buf);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        auth = buf.readSizedByteBuf();
        hashAlg = TPM_ALG_ID.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_HashSequenceStart_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_HashSequenceStart_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_HashSequenceStart_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_HashSequenceStart_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_HashSequenceStart_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_HashSequenceStart_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "auth", auth);
        _p.add(d, "TPM_ALG_ID", "hashAlg", hashAlg);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

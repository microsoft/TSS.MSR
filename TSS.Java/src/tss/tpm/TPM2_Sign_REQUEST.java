package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command causes the TPM to sign an externally provided hash with the specified
 *  symmetric or asymmetric signing key.
 */
public class TPM2_Sign_REQUEST extends ReqStructure
{
    /** Handle of key that will perform signing
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE keyHandle;

    /** Digest to be signed */
    public byte[] digest;

    /** Scheme selector */
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }

    /** Signing scheme to use if the scheme for keyHandle is TPM_ALG_NULL
     *  One of: TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *  TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR,
     *  TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME.
     */
    public TPMU_SIG_SCHEME inScheme;

    /** Proof that digest was created by the TPM
     *  If keyHandle is not a restricted signing key, then this may be a NULL Ticket with tag
     *  = TPM_ST_CHECKHASH.
     */
    public TPMT_TK_HASHCHECK validation;

    public TPM2_Sign_REQUEST() { keyHandle = new TPM_HANDLE(); }

    /** @param _keyHandle Handle of key that will perform signing
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _digest Digest to be signed
     *  @param _inScheme Signing scheme to use if the scheme for keyHandle is TPM_ALG_NULL
     *         One of: TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *         TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR,
     *         TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME.
     *  @param _validation Proof that digest was created by the TPM
     *         If keyHandle is not a restricted signing key, then this may be a NULL Ticket with
     *         tag = TPM_ST_CHECKHASH.
     */
    public TPM2_Sign_REQUEST(TPM_HANDLE _keyHandle, byte[] _digest, TPMU_SIG_SCHEME _inScheme, TPMT_TK_HASHCHECK _validation)
    {
        keyHandle = _keyHandle;
        digest = _digest;
        inScheme = _inScheme;
        validation = _validation;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(digest);
        buf.writeShort(inScheme.GetUnionSelector());
        inScheme.toTpm(buf);
        validation.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        digest = buf.readSizedByteBuf();
        TPM_ALG_ID inSchemeScheme = TPM_ALG_ID.fromTpm(buf);
        inScheme = UnionFactory.create("TPMU_SIG_SCHEME", inSchemeScheme);
        inScheme.initFromTpm(buf);
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
    public static TPM2_Sign_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_Sign_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_Sign_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_Sign_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_Sign_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Sign_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
        _p.add(d, "byte[]", "digest", digest);
        _p.add(d, "TPMU_SIG_SCHEME", "inScheme", inScheme);
        _p.add(d, "TPMT_TK_HASHCHECK", "validation", validation);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {keyHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

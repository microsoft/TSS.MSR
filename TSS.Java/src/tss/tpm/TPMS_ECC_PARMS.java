package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure contains the parameters for prime modulus ECC. */
public class TPMS_ECC_PARMS extends TpmStructure implements TPMU_PUBLIC_PARMS
{
    /** For a restricted decryption key, shall be set to a supported symmetric algorithm, key
     *  size. and mode.
     *  if the key is not a restricted decryption key, this field shall be set to TPM_ALG_NULL.
     */
    public TPMT_SYM_DEF_OBJECT symmetric;

    /** Scheme selector */
    public TPM_ALG_ID schemeScheme() { return scheme != null ? scheme.GetUnionSelector() : TPM_ALG_ID.NULL; }

    /** If the sign attribute of the key is SET, then this shall be a valid signing scheme.
     *  NOTE If the sign parameter in curveID indicates a mandatory scheme, then this field
     *  shall have the same value.
     *  If the decrypt attribute of the key is SET, then this shall be a valid key exchange
     *  scheme or TPM_ALG_NULL.
     *  If the key is a Storage Key, then this field shall be TPM_ALG_NULL.
     *  One of: TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
     *  TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA,
     *  TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES,
     *  TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME.
     */
    public TPMU_ASYM_SCHEME scheme;

    /** ECC curve ID */
    public TPM_ECC_CURVE curveID;

    /** Scheme selector */
    public TPM_ALG_ID kdfScheme() { return kdf != null ? kdf.GetUnionSelector() : TPM_ALG_ID.NULL; }

    /** An optional key derivation scheme for generating a symmetric key from a Z value
     *  If the kdf parameter associated with curveID is not TPM_ALG_NULL then this is required
     *  to be NULL.
     *  NOTE There are currently no commands where this parameter has effect and, in the
     *  reference code, this field needs to be set to TPM_ALG_NULL.
     *  One of: TPMS_KDF_SCHEME_MGF1, TPMS_KDF_SCHEME_KDF1_SP800_56A, TPMS_KDF_SCHEME_KDF2,
     *  TPMS_KDF_SCHEME_KDF1_SP800_108, TPMS_SCHEME_HASH, TPMS_NULL_KDF_SCHEME.
     */
    public TPMU_KDF_SCHEME kdf;

    public TPMS_ECC_PARMS() {}

    /** @param _symmetric For a restricted decryption key, shall be set to a supported symmetric
     *         algorithm, key size. and mode.
     *         if the key is not a restricted decryption key, this field shall be set to TPM_ALG_NULL.
     *  @param _scheme If the sign attribute of the key is SET, then this shall be a valid signing
     *         scheme.
     *         NOTE If the sign parameter in curveID indicates a mandatory scheme, then this field
     *         shall have the same value.
     *         If the decrypt attribute of the key is SET, then this shall be a valid key exchange
     *         scheme or TPM_ALG_NULL.
     *         If the key is a Storage Key, then this field shall be TPM_ALG_NULL.
     *         One of: TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
     *         TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA,
     *         TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES,
     *         TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME.
     *  @param _curveID ECC curve ID
     *  @param _kdf An optional key derivation scheme for generating a symmetric key from a Z value
     *         If the kdf parameter associated with curveID is not TPM_ALG_NULL then this is
     *         required to be NULL.
     *         NOTE There are currently no commands where this parameter has effect and, in the
     *         reference code, this field needs to be set to TPM_ALG_NULL.
     *         One of: TPMS_KDF_SCHEME_MGF1, TPMS_KDF_SCHEME_KDF1_SP800_56A, TPMS_KDF_SCHEME_KDF2,
     *         TPMS_KDF_SCHEME_KDF1_SP800_108, TPMS_SCHEME_HASH, TPMS_NULL_KDF_SCHEME.
     */
    public TPMS_ECC_PARMS(TPMT_SYM_DEF_OBJECT _symmetric, TPMU_ASYM_SCHEME _scheme, TPM_ECC_CURVE _curveID, TPMU_KDF_SCHEME _kdf)
    {
        symmetric = _symmetric;
        scheme = _scheme;
        curveID = _curveID;
        kdf = _kdf;
    }

    /** TpmUnion method */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.ECC; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        symmetric.toTpm(buf);
        buf.writeShort(scheme.GetUnionSelector());
        scheme.toTpm(buf);
        curveID.toTpm(buf);
        buf.writeShort(kdf.GetUnionSelector());
        kdf.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        symmetric = TPMT_SYM_DEF_OBJECT.fromTpm(buf);
        TPM_ALG_ID schemeScheme = TPM_ALG_ID.fromTpm(buf);
        scheme = UnionFactory.create("TPMU_ASYM_SCHEME", schemeScheme);
        scheme.initFromTpm(buf);
        curveID = TPM_ECC_CURVE.fromTpm(buf);
        TPM_ALG_ID kdfScheme = TPM_ALG_ID.fromTpm(buf);
        kdf = UnionFactory.create("TPMU_KDF_SCHEME", kdfScheme);
        kdf.initFromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_ECC_PARMS fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_ECC_PARMS.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_ECC_PARMS fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_ECC_PARMS fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_ECC_PARMS.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_ECC_PARMS");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_SYM_DEF_OBJECT", "symmetric", symmetric);
        _p.add(d, "TPMU_ASYM_SCHEME", "scheme", scheme);
        _p.add(d, "TPM_ECC_CURVE", "curveID", curveID);
        _p.add(d, "TPMU_KDF_SCHEME", "kdf", kdf);
    }
}

//<<<

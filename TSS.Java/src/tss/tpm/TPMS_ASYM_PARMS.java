package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure contains the common public area parameters for an asymmetric key. The
 *  first two parameters of the parameter definition structures of an asymmetric key shall
 *  have the same two first components.
 */
public class TPMS_ASYM_PARMS extends TpmStructure implements TPMU_PUBLIC_PARMS
{
    /** The companion symmetric algorithm for a restricted decryption key and shall be set to
     *  a supported symmetric algorithm
     *  This field is optional for keys that are not decryption keys and shall be set to
     *  TPM_ALG_NULL if not used.
     */
    public TPMT_SYM_DEF_OBJECT symmetric;
    
    /** Scheme selector  */
    public TPM_ALG_ID schemeScheme() { return scheme != null ? scheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** For a key with the sign attribute SET, a valid signing scheme for the key type
     *  for a key with the decrypt attribute SET, a valid key exchange protocol
     *  for a key with sign and decrypt attributes, shall be TPM_ALG_NULL
     *  One of: TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
     *  TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA,
     *  TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES,
     *  TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME.
     */
    public TPMU_ASYM_SCHEME scheme;
    
    public TPMS_ASYM_PARMS() {}
    
    /** @param _symmetric The companion symmetric algorithm for a restricted decryption key and
     *         shall be set to a supported symmetric algorithm
     *         This field is optional for keys that are not decryption keys and shall be set to
     *         TPM_ALG_NULL if not used.
     *  @param _scheme For a key with the sign attribute SET, a valid signing scheme for the
     *  key type
     *         for a key with the decrypt attribute SET, a valid key exchange protocol
     *         for a key with sign and decrypt attributes, shall be TPM_ALG_NULL
     *         One of: TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
     *         TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA,
     *         TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES,
     *         TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME.
     */
    public TPMS_ASYM_PARMS(TPMT_SYM_DEF_OBJECT _symmetric, TPMU_ASYM_SCHEME _scheme)
    {
        symmetric = _symmetric;
        scheme = _scheme;
    }
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.ANY; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        symmetric.toTpm(buf);
        buf.writeShort(scheme.GetUnionSelector());
        scheme.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        symmetric = TPMT_SYM_DEF_OBJECT.fromTpm(buf);
        TPM_ALG_ID schemeScheme = TPM_ALG_ID.fromTpm(buf);
        scheme = UnionFactory.create("TPMU_ASYM_SCHEME", schemeScheme);
        scheme.initFromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_ASYM_PARMS fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_ASYM_PARMS.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_ASYM_PARMS fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_ASYM_PARMS fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_ASYM_PARMS.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_ASYM_PARMS");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_SYM_DEF_OBJECT", "symmetric", symmetric);
        _p.add(d, "TPMU_ASYM_SCHEME", "scheme", scheme);
    }
}

//<<<

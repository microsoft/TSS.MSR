package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure contains the parameters for prime modulus ECC. */
public class TPMS_ECC_PARMS extends TpmStructure implements TPMU_PUBLIC_PARMS
{
    /**
     *  for a restricted decryption key, shall be set to a supported symmetric algorithm, key
     *  size. and mode.
     *  if the key is not a restricted decryption key, this field shall be set to
     *  TPM_ALG_NULL.
     */
    public TPMT_SYM_DEF_OBJECT symmetric;
    
    /** scheme selector */
    public TPM_ALG_ID schemeScheme() { return scheme != null ? scheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /**
     *  If the sign attribute of the key is SET, then this shall be a valid signing scheme.
     *  NOTE If the sign parameter in curveID indicates a mandatory scheme, then this field shall
     *  have the same value.
     *  If the decrypt attribute of the key is SET, then this shall be a valid key exchange scheme
     *  or TPM_ALG_NULL.
     *  If the key is a Storage Key, then this field shall be TPM_ALG_NULL.
     */
    public TPMU_ASYM_SCHEME scheme;
    
    /** ECC curve ID */
    public TPM_ECC_CURVE curveID;
    
    /** scheme selector */
    public TPM_ALG_ID kdfScheme() { return kdf != null ? kdf.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /**
     *  an optional key derivation scheme for generating a symmetric key from a Z value
     *  If the kdf parameter associated with curveID is not TPM_ALG_NULL then this is required to
     *  be NULL.
     *  NOTE There are currently no commands where this parameter has effect and, in the reference
     *  code, this field needs to be set to TPM_ALG_NULL.
     */
    public TPMU_KDF_SCHEME kdf;
    
    public TPMS_ECC_PARMS() {}
    
    /**
     *  @param _symmetric for a restricted decryption key, shall be set to a supported symmetric algorithm, key
     *         size. and mode.
     *         if the key is not a restricted decryption key, this field shall be set to
     *         TPM_ALG_NULL.
     *  @param _scheme If the sign attribute of the key is SET, then this shall be a valid signing scheme.
     *         NOTE If the sign parameter in curveID indicates a mandatory scheme, then this field shall
     *         have the same value.
     *         If the decrypt attribute of the key is SET, then this shall be a valid key exchange scheme
     *         or TPM_ALG_NULL.
     *         If the key is a Storage Key, then this field shall be TPM_ALG_NULL.
     *         (One of [TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
     *         TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2,
     *         TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES, TPMS_ENC_SCHEME_OAEP,
     *         TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME])
     *  @param _curveID ECC curve ID
     *  @param _kdf an optional key derivation scheme for generating a symmetric key from a Z value
     *         If the kdf parameter associated with curveID is not TPM_ALG_NULL then this is required to
     *         be NULL.
     *         NOTE There are currently no commands where this parameter has effect and, in the reference
     *         code, this field needs to be set to TPM_ALG_NULL.
     *         (One of [TPMS_KDF_SCHEME_MGF1, TPMS_KDF_SCHEME_KDF1_SP800_56A, TPMS_KDF_SCHEME_KDF2,
     *         TPMS_KDF_SCHEME_KDF1_SP800_108, TPMS_SCHEME_HASH, TPMS_NULL_KDF_SCHEME])
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
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        symmetric.toTpm(buf);
        scheme.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)scheme).toTpm(buf);
        curveID.toTpm(buf);
        kdf.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)kdf).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        symmetric = TPMT_SYM_DEF_OBJECT.fromTpm(buf);
        int _schemeScheme = buf.readShort() & 0xFFFF;
        scheme = UnionFactory.create("TPMU_ASYM_SCHEME", new TPM_ALG_ID(_schemeScheme));
        scheme.initFromTpm(buf);
        curveID = TPM_ECC_CURVE.fromTpm(buf);
        int _kdfScheme = buf.readShort() & 0xFFFF;
        kdf = UnionFactory.create("TPMU_KDF_SCHEME", new TPM_ALG_ID(_kdfScheme));
        kdf.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static TPMS_ECC_PARMS fromTpm (byte[] x) 
    {
        TPMS_ECC_PARMS ret = new TPMS_ECC_PARMS();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMS_ECC_PARMS fromTpm (InByteBuf buf) 
    {
        TPMS_ECC_PARMS ret = new TPMS_ECC_PARMS();
        ret.initFromTpm(buf);
        return ret;
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

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  A TPM compatible with this specification and supporting RSA shall support two primes and
 *  an exponent of zero. An exponent of zero indicates that the exponent is the default of 216
 *  + 1. Support for other values is optional. Use of other exponents in duplicated keys is
 *  not recommended because the resulting keys would not be interoperable with other TPMs.
 */
public class TPMS_RSA_PARMS extends TpmStructure implements TPMU_PUBLIC_PARMS
{
    /**
     *  for a restricted decryption key, shall be set to a supported symmetric algorithm, key
     *  size, and mode.
     *  if the key is not a restricted decryption key, this field shall be set to
     *  TPM_ALG_NULL.
     */
    public TPMT_SYM_DEF_OBJECT symmetric;
    public TPM_ALG_ID schemeScheme() { return scheme != null ? scheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /**
     *  scheme.scheme shall be:
     *  for an unrestricted signing key, either TPM_ALG_RSAPSS TPM_ALG_RSASSA or TPM_ALG_NULL
     *  for a restricted signing key, either TPM_ALG_RSAPSS or TPM_ALG_RSASSA
     *  for an unrestricted decryption key, TPM_ALG_RSAES, TPM_ALG_OAEP, or TPM_ALG_NULL unless
     *  the object also has the sign attribute
     *  for a restricted decryption key, TPM_ALG_NULL
     *  NOTE When both sign and decrypt are SET, restricted shall be CLEAR and
     *  scheme shall be TPM_ALG_NULL.
     */
    public TPMU_ASYM_SCHEME scheme;
    
    /** number of bits in the public modulus */
    public short keyBits;
    
    /**
     *  the public exponent
     *  A prime number greater than 2.
     */
    public int exponent;
    
    public TPMS_RSA_PARMS() {}
    
    /**
     *  @param _symmetric for a restricted decryption key, shall be set to a supported symmetric algorithm, key
     *         size, and mode.
     *         if the key is not a restricted decryption key, this field shall be set to
     *         TPM_ALG_NULL.
     *  @param _scheme scheme.scheme shall be:
     *         for an unrestricted signing key, either TPM_ALG_RSAPSS TPM_ALG_RSASSA or TPM_ALG_NULL
     *         for a restricted signing key, either TPM_ALG_RSAPSS or TPM_ALG_RSASSA
     *         for an unrestricted decryption key, TPM_ALG_RSAES, TPM_ALG_OAEP, or TPM_ALG_NULL unless
     *         the object also has the sign attribute
     *         for a restricted decryption key, TPM_ALG_NULL
     *         NOTE When both sign and decrypt are SET, restricted shall be CLEAR and
     *         scheme shall be TPM_ALG_NULL.
     *         (One of [TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
     *         TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2,
     *         TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES, TPMS_ENC_SCHEME_OAEP,
     *         TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME])
     *  @param _keyBits number of bits in the public modulus
     *  @param _exponent the public exponent
     *         A prime number greater than 2.
     */
    public TPMS_RSA_PARMS(TPMT_SYM_DEF_OBJECT _symmetric, TPMU_ASYM_SCHEME _scheme, int _keyBits, int _exponent)
    {
        symmetric = _symmetric;
        scheme = _scheme;
        keyBits = (short)_keyBits;
        exponent = _exponent;
    }

    /** TpmUnion method */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.RSA; }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        symmetric.toTpm(buf);
        scheme.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)scheme).toTpm(buf);
        buf.writeShort(keyBits);
        buf.writeInt(exponent);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        symmetric = TPMT_SYM_DEF_OBJECT.fromTpm(buf);
        int _schemeScheme = buf.readShort() & 0xFFFF;
        scheme = UnionFactory.create("TPMU_ASYM_SCHEME", new TPM_ALG_ID(_schemeScheme));
        scheme.initFromTpm(buf);
        keyBits = buf.readShort();
        exponent = buf.readInt();
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPMS_RSA_PARMS fromTpm (byte[] x) 
    {
        TPMS_RSA_PARMS ret = new TPMS_RSA_PARMS();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMS_RSA_PARMS fromTpm (InByteBuf buf) 
    {
        TPMS_RSA_PARMS ret = new TPMS_RSA_PARMS();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_RSA_PARMS");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_SYM_DEF_OBJECT", "symmetric", symmetric);
        _p.add(d, "TPMU_ASYM_SCHEME", "scheme", scheme);
        _p.add(d, "short", "keyBits", keyBits);
        _p.add(d, "int", "exponent", exponent);
    }
}

//<<<

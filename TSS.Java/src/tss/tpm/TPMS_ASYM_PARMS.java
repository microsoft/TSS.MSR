package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This structure contains the common public area parameters for an asymmetric key. The first
 *  two parameters of the parameter definition structures of an asymmetric key shall have
 *  the same two first components.
 */
public class TPMS_ASYM_PARMS extends TpmStructure implements TPMU_PUBLIC_PARMS
{
    /**
     *  the companion symmetric algorithm for a restricted decryption key and shall be set to a
     *  supported symmetric algorithm
     *  This field is optional for keys that are not decryption keys and shall be set
     *  to TPM_ALG_NULL if not used.
     */
    public TPMT_SYM_DEF_OBJECT symmetric;
    
    /** scheme selector */
    public TPM_ALG_ID schemeScheme() { return scheme != null ? scheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /**
     *  for a key with the sign attribute SET, a valid signing scheme for the key type
     *  for a key with the decrypt attribute SET, a valid key exchange protocol
     *  for a key with sign and decrypt attributes, shall be TPM_ALG_NULL
     */
    public TPMU_ASYM_SCHEME scheme;
    
    public TPMS_ASYM_PARMS() {}
    
    /**
     *  @param _symmetric the companion symmetric algorithm for a restricted decryption key and shall be set to a
     *         supported symmetric algorithm
     *         This field is optional for keys that are not decryption keys and shall be set
     *         to TPM_ALG_NULL if not used.
     *  @param _scheme for a key with the sign attribute SET, a valid signing scheme for the key type
     *         for a key with the decrypt attribute SET, a valid key exchange protocol
     *         for a key with sign and decrypt attributes, shall be TPM_ALG_NULL
     *         (One of [TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
     *         TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2,
     *         TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES, TPMS_ENC_SCHEME_OAEP,
     *         TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME])
     */
    public TPMS_ASYM_PARMS(TPMT_SYM_DEF_OBJECT _symmetric, TPMU_ASYM_SCHEME _scheme)
    {
        symmetric = _symmetric;
        scheme = _scheme;
    }

    /** TpmUnion method */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.ANY; }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        symmetric.toTpm(buf);
        scheme.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)scheme).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        symmetric = TPMT_SYM_DEF_OBJECT.fromTpm(buf);
        int _schemeScheme = buf.readShort() & 0xFFFF;
        scheme = UnionFactory.create("TPMU_ASYM_SCHEME", new TPM_ALG_ID(_schemeScheme));
        scheme.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static TPMS_ASYM_PARMS fromTpm (byte[] x) 
    {
        TPMS_ASYM_PARMS ret = new TPMS_ASYM_PARMS();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPMS_ASYM_PARMS fromTpm (InByteBuf buf) 
    {
        TPMS_ASYM_PARMS ret = new TPMS_ASYM_PARMS();
        ret.initFromTpm(buf);
        return ret;
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

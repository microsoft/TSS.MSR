package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure contains the parameters for prime modulus ECC.
*/
public class TPMS_ECC_PARMS extends TpmStructure implements TPMU_PUBLIC_PARMS 
{
    /**
     * This structure contains the parameters for prime modulus ECC.
     * 
     * @param _symmetric for a restricted decryption key, shall be set to a supported symmetric algorithm, key size. and mode. if the key is not a restricted decryption key, this field shall be set to TPM_ALG_NULL. 
     * @param _scheme If the sign attribute of the key is SET, then this shall be a valid signing scheme. NOTE If the sign parameter in curveID indicates a mandatory scheme, then this field shall have the same value. If the decrypt attribute of the key is SET, then this shall be a valid key exchange scheme or TPM_ALG_NULL. If the key is a Storage Key, then this field shall be TPM_ALG_NULL. (One of TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES, TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME) 
     * @param _curveID ECC curve ID 
     * @param _kdf an optional key derivation scheme for generating a symmetric key from a Z value If the kdf parameter associated with curveID is not TPM_ALG_NULL then this is required to be NULL. NOTE There are currently no commands where this parameter has effect and, in the reference code, this field needs to be set to TPM_ALG_NULL. (One of TPMS_SCHEME_MGF1, TPMS_SCHEME_KDF1_SP800_56A, TPMS_SCHEME_KDF2, TPMS_SCHEME_KDF1_SP800_108, TPMS_NULL_KDF_SCHEME)
     */
    public TPMS_ECC_PARMS(TPMT_SYM_DEF_OBJECT _symmetric,TPMU_ASYM_SCHEME _scheme,TPM_ECC_CURVE _curveID,TPMU_KDF_SCHEME _kdf)
    {
        symmetric = _symmetric;
        scheme = _scheme;
        curveID = _curveID;
        kdf = _kdf;
    }
    /**
    * This structure contains the parameters for prime modulus ECC.
    */
    public TPMS_ECC_PARMS() {};
    /**
    * for a restricted decryption key, shall be set to a supported symmetric algorithm, key size. and mode. if the key is not a restricted decryption key, this field shall be set to TPM_ALG_NULL.
    */
    public TPMT_SYM_DEF_OBJECT symmetric;
    /**
    * scheme selector
    */
    // private TPM_ALG_ID schemeScheme;
    /**
    * If the sign attribute of the key is SET, then this shall be a valid signing scheme. NOTE If the sign parameter in curveID indicates a mandatory scheme, then this field shall have the same value. If the decrypt attribute of the key is SET, then this shall be a valid key exchange scheme or TPM_ALG_NULL. If the key is a Storage Key, then this field shall be TPM_ALG_NULL.
    */
    public TPMU_ASYM_SCHEME scheme;
    /**
    * ECC curve ID
    */
    public TPM_ECC_CURVE curveID;
    /**
    * scheme selector
    */
    // private TPM_ALG_ID kdfScheme;
    /**
    * an optional key derivation scheme for generating a symmetric key from a Z value If the kdf parameter associated with curveID is not TPM_ALG_NULL then this is required to be NULL. NOTE There are currently no commands where this parameter has effect and, in the reference code, this field needs to be set to TPM_ALG_NULL.
    */
    public TPMU_KDF_SCHEME kdf;
    public int GetUnionSelector_scheme()
    {
        if(scheme instanceof TPMS_KEY_SCHEME_ECDH){return 0x0019; }
        if(scheme instanceof TPMS_KEY_SCHEME_ECMQV){return 0x001D; }
        if(scheme instanceof TPMS_SIG_SCHEME_RSASSA){return 0x0014; }
        if(scheme instanceof TPMS_SIG_SCHEME_RSAPSS){return 0x0016; }
        if(scheme instanceof TPMS_SIG_SCHEME_ECDSA){return 0x0018; }
        if(scheme instanceof TPMS_SIG_SCHEME_ECDAA){return 0x001A; }
        if(scheme instanceof TPMS_SIG_SCHEME_SM2){return 0x001B; }
        if(scheme instanceof TPMS_SIG_SCHEME_ECSCHNORR){return 0x001C; }
        if(scheme instanceof TPMS_ENC_SCHEME_RSAES){return 0x0015; }
        if(scheme instanceof TPMS_ENC_SCHEME_OAEP){return 0x0017; }
        if(scheme instanceof TPMS_SCHEME_HASH){return 0x7FFF; }
        if(scheme instanceof TPMS_NULL_ASYM_SCHEME){return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }
    public int GetUnionSelector_kdf()
    {
        if(kdf instanceof TPMS_SCHEME_MGF1){return 0x0007; }
        if(kdf instanceof TPMS_SCHEME_KDF1_SP800_56A){return 0x0020; }
        if(kdf instanceof TPMS_SCHEME_KDF2){return 0x0021; }
        if(kdf instanceof TPMS_SCHEME_KDF1_SP800_108){return 0x0022; }
        if(kdf instanceof TPMS_NULL_KDF_SCHEME){return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        symmetric.toTpm(buf);
        buf.writeInt(GetUnionSelector_scheme(), 2);
        ((TpmMarshaller)scheme).toTpm(buf);
        curveID.toTpm(buf);
        buf.writeInt(GetUnionSelector_kdf(), 2);
        ((TpmMarshaller)kdf).toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        symmetric = TPMT_SYM_DEF_OBJECT.fromTpm(buf);
        int _schemeScheme = buf.readInt(2);
        scheme=null;
        if(_schemeScheme==TPM_ALG_ID.ECDH.toInt()) {scheme = new TPMS_KEY_SCHEME_ECDH();}
        else if(_schemeScheme==TPM_ALG_ID.ECMQV.toInt()) {scheme = new TPMS_KEY_SCHEME_ECMQV();}
        else if(_schemeScheme==TPM_ALG_ID.RSASSA.toInt()) {scheme = new TPMS_SIG_SCHEME_RSASSA();}
        else if(_schemeScheme==TPM_ALG_ID.RSAPSS.toInt()) {scheme = new TPMS_SIG_SCHEME_RSAPSS();}
        else if(_schemeScheme==TPM_ALG_ID.ECDSA.toInt()) {scheme = new TPMS_SIG_SCHEME_ECDSA();}
        else if(_schemeScheme==TPM_ALG_ID.ECDAA.toInt()) {scheme = new TPMS_SIG_SCHEME_ECDAA();}
        // code generator workaround BUGBUG >> (probChild)else if(_schemeScheme==TPM_ALG_ID.SM2.toInt()) {scheme = new TPMS_SIG_SCHEME_SM2();}
        // code generator workaround BUGBUG >> (probChild)else if(_schemeScheme==TPM_ALG_ID.ECSCHNORR.toInt()) {scheme = new TPMS_SIG_SCHEME_ECSCHNORR();}
        else if(_schemeScheme==TPM_ALG_ID.RSAES.toInt()) {scheme = new TPMS_ENC_SCHEME_RSAES();}
        else if(_schemeScheme==TPM_ALG_ID.OAEP.toInt()) {scheme = new TPMS_ENC_SCHEME_OAEP();}
        else if(_schemeScheme==TPM_ALG_ID.ANY.toInt()) {scheme = new TPMS_SCHEME_HASH();}
        else if(_schemeScheme==TPM_ALG_ID.NULL.toInt()) {scheme = new TPMS_NULL_ASYM_SCHEME();}
        if(scheme==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_schemeScheme).name());
        scheme.initFromTpm(buf);
        curveID = TPM_ECC_CURVE.fromTpm(buf);
        int _kdfScheme = buf.readInt(2);
        kdf=null;
        if(_kdfScheme==TPM_ALG_ID.MGF1.toInt()) {kdf = new TPMS_SCHEME_MGF1();}
        else if(_kdfScheme==TPM_ALG_ID.KDF1_SP800_56A.toInt()) {kdf = new TPMS_SCHEME_KDF1_SP800_56A();}
        else if(_kdfScheme==TPM_ALG_ID.KDF2.toInt()) {kdf = new TPMS_SCHEME_KDF2();}
        else if(_kdfScheme==TPM_ALG_ID.KDF1_SP800_108.toInt()) {kdf = new TPMS_SCHEME_KDF1_SP800_108();}
        else if(_kdfScheme==TPM_ALG_ID.NULL.toInt()) {kdf = new TPMS_NULL_KDF_SCHEME();}
        if(kdf==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_kdfScheme).name());
        kdf.initFromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
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
    };
    
    
};

//<<<


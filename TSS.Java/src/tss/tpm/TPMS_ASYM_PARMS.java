package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure contains the common public area parameters for an asymmetric key. The first two parameters of the parameter definition structures of an asymmetric key shall have the same two first components.
*/
public class TPMS_ASYM_PARMS extends TpmStructure implements TPMU_PUBLIC_PARMS 
{
    /**
     * This structure contains the common public area parameters for an asymmetric key. The first two parameters of the parameter definition structures of an asymmetric key shall have the same two first components.
     * 
     * @param _symmetric the companion symmetric algorithm for a restricted decryption key and shall be set to a supported symmetric algorithm This field is optional for keys that are not decryption keys and shall be set to TPM_ALG_NULL if not used. 
     * @param _scheme for a key with the sign attribute SET, a valid signing scheme for the key type for a key with the decrypt attribute SET, a valid key exchange protocol for a key with sign and decrypt attributes, shall be TPM_ALG_NULL (One of TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES, TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME)
     */
    public TPMS_ASYM_PARMS(TPMT_SYM_DEF_OBJECT _symmetric,TPMU_ASYM_SCHEME _scheme)
    {
        symmetric = _symmetric;
        scheme = _scheme;
    }
    /**
    * This structure contains the common public area parameters for an asymmetric key. The first two parameters of the parameter definition structures of an asymmetric key shall have the same two first components.
    */
    public TPMS_ASYM_PARMS() {};
    /**
    * the companion symmetric algorithm for a restricted decryption key and shall be set to a supported symmetric algorithm This field is optional for keys that are not decryption keys and shall be set to TPM_ALG_NULL if not used.
    */
    public TPMT_SYM_DEF_OBJECT symmetric;
    /**
    * scheme selector
    */
    // private TPM_ALG_ID schemeScheme;
    /**
    * for a key with the sign attribute SET, a valid signing scheme for the key type for a key with the decrypt attribute SET, a valid key exchange protocol for a key with sign and decrypt attributes, shall be TPM_ALG_NULL
    */
    public TPMU_ASYM_SCHEME scheme;
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
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        symmetric.toTpm(buf);
        buf.writeInt(GetUnionSelector_scheme(), 2);
        ((TpmMarshaller)scheme).toTpm(buf);
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
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
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
    };
    
    
};

//<<<


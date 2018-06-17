package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Table 173 Definition of (TPMT_SIG_SCHEME) {ECC} TPMT_ECC_SCHEME Structure
*/
public class TPMT_ECC_SCHEME extends TpmStructure
{
    /**
     * Table 173 Definition of (TPMT_SIG_SCHEME) {ECC} TPMT_ECC_SCHEME Structure
     * 
     * @param _details scheme parameters (One of TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES, TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME)
     */
    public TPMT_ECC_SCHEME(TPMU_ASYM_SCHEME _details)
    {
        details = _details;
    }
    /**
    * Table 173 Definition of (TPMT_SIG_SCHEME) {ECC} TPMT_ECC_SCHEME Structure
    */
    public TPMT_ECC_SCHEME() {};
    /**
    * scheme selector
    */
    // private TPM_ALG_ID scheme;
    /**
    * scheme parameters
    */
    public TPMU_ASYM_SCHEME details;
    public int GetUnionSelector_details()
    {
        if(details instanceof TPMS_KEY_SCHEME_ECDH){return 0x0019; }
        if(details instanceof TPMS_KEY_SCHEME_ECMQV){return 0x001D; }
        if(details instanceof TPMS_SIG_SCHEME_RSASSA){return 0x0014; }
        if(details instanceof TPMS_SIG_SCHEME_RSAPSS){return 0x0016; }
        if(details instanceof TPMS_SIG_SCHEME_ECDSA){return 0x0018; }
        if(details instanceof TPMS_SIG_SCHEME_ECDAA){return 0x001A; }
        if(details instanceof TPMS_SIG_SCHEME_SM2){return 0x001B; }
        if(details instanceof TPMS_SIG_SCHEME_ECSCHNORR){return 0x001C; }
        if(details instanceof TPMS_ENC_SCHEME_RSAES){return 0x0015; }
        if(details instanceof TPMS_ENC_SCHEME_OAEP){return 0x0017; }
        if(details instanceof TPMS_SCHEME_HASH){return 0x7FFF; }
        if(details instanceof TPMS_NULL_ASYM_SCHEME){return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt(GetUnionSelector_details(), 2);
        ((TpmMarshaller)details).toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _scheme = buf.readInt(2);
        details=null;
        if(_scheme==TPM_ALG_ID.ECDH.toInt()) {details = new TPMS_KEY_SCHEME_ECDH();}
        else if(_scheme==TPM_ALG_ID.ECMQV.toInt()) {details = new TPMS_KEY_SCHEME_ECMQV();}
        else if(_scheme==TPM_ALG_ID.RSASSA.toInt()) {details = new TPMS_SIG_SCHEME_RSASSA();}
        else if(_scheme==TPM_ALG_ID.RSAPSS.toInt()) {details = new TPMS_SIG_SCHEME_RSAPSS();}
        else if(_scheme==TPM_ALG_ID.ECDSA.toInt()) {details = new TPMS_SIG_SCHEME_ECDSA();}
        else if(_scheme==TPM_ALG_ID.ECDAA.toInt()) {details = new TPMS_SIG_SCHEME_ECDAA();}
        // code generator workaround BUGBUG >> (probChild)else if(_scheme==TPM_ALG_ID.SM2.toInt()) {details = new TPMS_SIG_SCHEME_SM2();}
        // code generator workaround BUGBUG >> (probChild)else if(_scheme==TPM_ALG_ID.ECSCHNORR.toInt()) {details = new TPMS_SIG_SCHEME_ECSCHNORR();}
        else if(_scheme==TPM_ALG_ID.RSAES.toInt()) {details = new TPMS_ENC_SCHEME_RSAES();}
        else if(_scheme==TPM_ALG_ID.OAEP.toInt()) {details = new TPMS_ENC_SCHEME_OAEP();}
        else if(_scheme==TPM_ALG_ID.ANY.toInt()) {details = new TPMS_SCHEME_HASH();}
        else if(_scheme==TPM_ALG_ID.NULL.toInt()) {details = new TPMS_NULL_ASYM_SCHEME();}
        if(details==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_scheme).name());
        details.initFromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMT_ECC_SCHEME fromTpm (byte[] x) 
    {
        TPMT_ECC_SCHEME ret = new TPMT_ECC_SCHEME();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMT_ECC_SCHEME fromTpm (InByteBuf buf) 
    {
        TPMT_ECC_SCHEME ret = new TPMT_ECC_SCHEME();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_ECC_SCHEME");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMU_ASYM_SCHEME", "details", details);
    };
    
    
};

//<<<


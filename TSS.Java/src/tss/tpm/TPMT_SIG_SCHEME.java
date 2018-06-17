package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Table 152 Definition of TPMT_SIG_SCHEME Structure
*/
public class TPMT_SIG_SCHEME extends TpmStructure
{
    /**
     * Table 152 Definition of TPMT_SIG_SCHEME Structure
     * 
     * @param _details scheme parameters (One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)
     */
    public TPMT_SIG_SCHEME(TPMU_SIG_SCHEME _details)
    {
        details = _details;
    }
    /**
    * Table 152 Definition of TPMT_SIG_SCHEME Structure
    */
    public TPMT_SIG_SCHEME() {};
    /**
    * scheme selector
    */
    // private TPM_ALG_ID scheme;
    /**
    * scheme parameters
    */
    public TPMU_SIG_SCHEME details;
    public int GetUnionSelector_details()
    {
        if(details instanceof TPMS_SIG_SCHEME_RSASSA){return 0x0014; }
        if(details instanceof TPMS_SIG_SCHEME_RSAPSS){return 0x0016; }
        if(details instanceof TPMS_SIG_SCHEME_ECDSA){return 0x0018; }
        if(details instanceof TPMS_SIG_SCHEME_ECDAA){return 0x001A; }
        if(details instanceof TPMS_SIG_SCHEME_SM2){return 0x001B; }
        if(details instanceof TPMS_SIG_SCHEME_ECSCHNORR){return 0x001C; }
        if(details instanceof TPMS_SCHEME_HMAC){return 0x0005; }
        if(details instanceof TPMS_SCHEME_HASH){return 0x7FFF; }
        if(details instanceof TPMS_NULL_SIG_SCHEME){return 0x0010; }
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
        if(_scheme==TPM_ALG_ID.RSASSA.toInt()) {details = new TPMS_SIG_SCHEME_RSASSA();}
        else if(_scheme==TPM_ALG_ID.RSAPSS.toInt()) {details = new TPMS_SIG_SCHEME_RSAPSS();}
        else if(_scheme==TPM_ALG_ID.ECDSA.toInt()) {details = new TPMS_SIG_SCHEME_ECDSA();}
        else if(_scheme==TPM_ALG_ID.ECDAA.toInt()) {details = new TPMS_SIG_SCHEME_ECDAA();}
        // code generator workaround BUGBUG >> (probChild)else if(_scheme==TPM_ALG_ID.SM2.toInt()) {details = new TPMS_SIG_SCHEME_SM2();}
        // code generator workaround BUGBUG >> (probChild)else if(_scheme==TPM_ALG_ID.ECSCHNORR.toInt()) {details = new TPMS_SIG_SCHEME_ECSCHNORR();}
        else if(_scheme==TPM_ALG_ID.HMAC.toInt()) {details = new TPMS_SCHEME_HMAC();}
        else if(_scheme==TPM_ALG_ID.ANY.toInt()) {details = new TPMS_SCHEME_HASH();}
        else if(_scheme==TPM_ALG_ID.NULL.toInt()) {details = new TPMS_NULL_SIG_SCHEME();}
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
    public static TPMT_SIG_SCHEME fromTpm (byte[] x) 
    {
        TPMT_SIG_SCHEME ret = new TPMT_SIG_SCHEME();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMT_SIG_SCHEME fromTpm (InByteBuf buf) 
    {
        TPMT_SIG_SCHEME ret = new TPMT_SIG_SCHEME();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_SIG_SCHEME");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMU_SIG_SCHEME", "details", details);
    };
    
    
};

//<<<


package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command returns a digital signature of the audit session digest.
*/
public class TPM2_GetSessionAuditDigest_REQUEST extends TpmStructure
{
    /**
     * This command returns a digital signature of the audit session digest.
     * 
     * @param _privacyAdminHandle handle of the privacy administrator (TPM_RH_ENDORSEMENT) Auth Index: 1 Auth Role: USER 
     * @param _signHandle handle of the signing key Auth Index: 2 Auth Role: USER 
     * @param _sessionHandle handle of the audit session Auth Index: None 
     * @param _qualifyingData user-provided qualifying data may be zero-length 
     * @param _inScheme signing scheme to use if the scheme for signHandle is TPM_ALG_NULL (One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)
     */
    public TPM2_GetSessionAuditDigest_REQUEST(TPM_HANDLE _privacyAdminHandle,TPM_HANDLE _signHandle,TPM_HANDLE _sessionHandle,byte[] _qualifyingData,TPMU_SIG_SCHEME _inScheme)
    {
        privacyAdminHandle = _privacyAdminHandle;
        signHandle = _signHandle;
        sessionHandle = _sessionHandle;
        qualifyingData = _qualifyingData;
        inScheme = _inScheme;
    }
    /**
    * This command returns a digital signature of the audit session digest.
    */
    public TPM2_GetSessionAuditDigest_REQUEST() {};
    /**
    * handle of the privacy administrator (TPM_RH_ENDORSEMENT) Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE privacyAdminHandle;
    /**
    * handle of the signing key Auth Index: 2 Auth Role: USER
    */
    public TPM_HANDLE signHandle;
    /**
    * handle of the audit session Auth Index: None
    */
    public TPM_HANDLE sessionHandle;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short qualifyingDataSize;
    /**
    * user-provided qualifying data may be zero-length
    */
    public byte[] qualifyingData;
    /**
    * scheme selector
    */
    // private TPM_ALG_ID inSchemeScheme;
    /**
    * signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
    */
    public TPMU_SIG_SCHEME inScheme;
    public int GetUnionSelector_inScheme()
    {
        if(inScheme instanceof TPMS_SIG_SCHEME_RSASSA){return 0x0014; }
        if(inScheme instanceof TPMS_SIG_SCHEME_RSAPSS){return 0x0016; }
        if(inScheme instanceof TPMS_SIG_SCHEME_ECDSA){return 0x0018; }
        if(inScheme instanceof TPMS_SIG_SCHEME_ECDAA){return 0x001A; }
        if(inScheme instanceof TPMS_SIG_SCHEME_SM2){return 0x001B; }
        if(inScheme instanceof TPMS_SIG_SCHEME_ECSCHNORR){return 0x001C; }
        if(inScheme instanceof TPMS_SCHEME_HMAC){return 0x0005; }
        if(inScheme instanceof TPMS_SCHEME_HASH){return 0x7FFF; }
        if(inScheme instanceof TPMS_NULL_SIG_SCHEME){return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        privacyAdminHandle.toTpm(buf);
        signHandle.toTpm(buf);
        sessionHandle.toTpm(buf);
        buf.writeInt((qualifyingData!=null)?qualifyingData.length:0, 2);
        if(qualifyingData!=null)
            buf.write(qualifyingData);
        buf.writeInt(GetUnionSelector_inScheme(), 2);
        ((TpmMarshaller)inScheme).toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        privacyAdminHandle = TPM_HANDLE.fromTpm(buf);
        signHandle = TPM_HANDLE.fromTpm(buf);
        sessionHandle = TPM_HANDLE.fromTpm(buf);
        int _qualifyingDataSize = buf.readInt(2);
        qualifyingData = new byte[_qualifyingDataSize];
        buf.readArrayOfInts(qualifyingData, 1, _qualifyingDataSize);
        int _inSchemeScheme = buf.readInt(2);
        inScheme=null;
        if(_inSchemeScheme==TPM_ALG_ID.RSASSA.toInt()) {inScheme = new TPMS_SIG_SCHEME_RSASSA();}
        else if(_inSchemeScheme==TPM_ALG_ID.RSAPSS.toInt()) {inScheme = new TPMS_SIG_SCHEME_RSAPSS();}
        else if(_inSchemeScheme==TPM_ALG_ID.ECDSA.toInt()) {inScheme = new TPMS_SIG_SCHEME_ECDSA();}
        else if(_inSchemeScheme==TPM_ALG_ID.ECDAA.toInt()) {inScheme = new TPMS_SIG_SCHEME_ECDAA();}
        // code generator workaround BUGBUG >> (probChild)else if(_inSchemeScheme==TPM_ALG_ID.SM2.toInt()) {inScheme = new TPMS_SIG_SCHEME_SM2();}
        // code generator workaround BUGBUG >> (probChild)else if(_inSchemeScheme==TPM_ALG_ID.ECSCHNORR.toInt()) {inScheme = new TPMS_SIG_SCHEME_ECSCHNORR();}
        else if(_inSchemeScheme==TPM_ALG_ID.HMAC.toInt()) {inScheme = new TPMS_SCHEME_HMAC();}
        else if(_inSchemeScheme==TPM_ALG_ID.ANY.toInt()) {inScheme = new TPMS_SCHEME_HASH();}
        else if(_inSchemeScheme==TPM_ALG_ID.NULL.toInt()) {inScheme = new TPMS_NULL_SIG_SCHEME();}
        if(inScheme==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_inSchemeScheme).name());
        inScheme.initFromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_GetSessionAuditDigest_REQUEST fromTpm (byte[] x) 
    {
        TPM2_GetSessionAuditDigest_REQUEST ret = new TPM2_GetSessionAuditDigest_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_GetSessionAuditDigest_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_GetSessionAuditDigest_REQUEST ret = new TPM2_GetSessionAuditDigest_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_GetSessionAuditDigest_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "privacyAdminHandle", privacyAdminHandle);
        _p.add(d, "TPM_HANDLE", "signHandle", signHandle);
        _p.add(d, "TPM_HANDLE", "sessionHandle", sessionHandle);
        _p.add(d, "byte", "qualifyingData", qualifyingData);
        _p.add(d, "TPMU_SIG_SCHEME", "inScheme", inScheme);
    };
    
    
};

//<<<


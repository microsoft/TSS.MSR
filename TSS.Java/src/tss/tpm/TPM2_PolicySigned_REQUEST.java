package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command includes a signed authorization in a policy. The command ties the policy to a signing key by including the Name of the signing key in the policyDigest
*/
public class TPM2_PolicySigned_REQUEST extends TpmStructure
{
    /**
     * This command includes a signed authorization in a policy. The command ties the policy to a signing key by including the Name of the signing key in the policyDigest
     * 
     * @param _authObject handle for a key that will validate the signature Auth Index: None 
     * @param _policySession handle for the policy session being extended Auth Index: None 
     * @param _nonceTPM the policy nonce for the session This can be the Empty Buffer. 
     * @param _cpHashA digest of the command parameters to which this authorization is limited This is not the cpHash for this command but the cpHash for the command to which this policy session will be applied. If it is not limited, the parameter will be the Empty Buffer. 
     * @param _policyRef a reference to a policy relating to the authorization may be the Empty Buffer Size is limited to be no larger than the nonce size supported on the TPM. 
     * @param _expiration time when authorization will expire, measured in seconds from the time that nonceTPM was generated If expiration is non-negative, a NULL Ticket is returned. See 23.2.5. 
     * @param _auth signed authorization (not optional) (One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)
     */
    public TPM2_PolicySigned_REQUEST(TPM_HANDLE _authObject,TPM_HANDLE _policySession,byte[] _nonceTPM,byte[] _cpHashA,byte[] _policyRef,int _expiration,TPMU_SIGNATURE _auth)
    {
        authObject = _authObject;
        policySession = _policySession;
        nonceTPM = _nonceTPM;
        cpHashA = _cpHashA;
        policyRef = _policyRef;
        expiration = _expiration;
        auth = _auth;
    }
    /**
    * This command includes a signed authorization in a policy. The command ties the policy to a signing key by including the Name of the signing key in the policyDigest
    */
    public TPM2_PolicySigned_REQUEST() {};
    /**
    * handle for a key that will validate the signature Auth Index: None
    */
    public TPM_HANDLE authObject;
    /**
    * handle for the policy session being extended Auth Index: None
    */
    public TPM_HANDLE policySession;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short nonceTPMSize;
    /**
    * the policy nonce for the session This can be the Empty Buffer.
    */
    public byte[] nonceTPM;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short cpHashASize;
    /**
    * digest of the command parameters to which this authorization is limited This is not the cpHash for this command but the cpHash for the command to which this policy session will be applied. If it is not limited, the parameter will be the Empty Buffer.
    */
    public byte[] cpHashA;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short policyRefSize;
    /**
    * a reference to a policy relating to the authorization may be the Empty Buffer Size is limited to be no larger than the nonce size supported on the TPM.
    */
    public byte[] policyRef;
    /**
    * time when authorization will expire, measured in seconds from the time that nonceTPM was generated If expiration is non-negative, a NULL Ticket is returned. See 23.2.5.
    */
    public int expiration;
    /**
    * selector of the algorithm used to construct the signature
    */
    // private TPM_ALG_ID authSigAlg;
    /**
    * signed authorization (not optional)
    */
    public TPMU_SIGNATURE auth;
    public int GetUnionSelector_auth()
    {
        if(auth instanceof TPMS_SIGNATURE_RSASSA){return 0x0014; }
        if(auth instanceof TPMS_SIGNATURE_RSAPSS){return 0x0016; }
        if(auth instanceof TPMS_SIGNATURE_ECDSA){return 0x0018; }
        if(auth instanceof TPMS_SIGNATURE_ECDAA){return 0x001A; }
        if(auth instanceof TPMS_SIGNATURE_SM2){return 0x001B; }
        if(auth instanceof TPMS_SIGNATURE_ECSCHNORR){return 0x001C; }
        if(auth instanceof TPMT_HA){return 0x0005; }
        if(auth instanceof TPMS_SCHEME_HASH){return 0x7FFF; }
        if(auth instanceof TPMS_NULL_SIGNATURE){return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        authObject.toTpm(buf);
        policySession.toTpm(buf);
        buf.writeInt((nonceTPM!=null)?nonceTPM.length:0, 2);
        if(nonceTPM!=null)
            buf.write(nonceTPM);
        buf.writeInt((cpHashA!=null)?cpHashA.length:0, 2);
        if(cpHashA!=null)
            buf.write(cpHashA);
        buf.writeInt((policyRef!=null)?policyRef.length:0, 2);
        if(policyRef!=null)
            buf.write(policyRef);
        buf.write(expiration);
        buf.writeInt(GetUnionSelector_auth(), 2);
        ((TpmMarshaller)auth).toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authObject = TPM_HANDLE.fromTpm(buf);
        policySession = TPM_HANDLE.fromTpm(buf);
        int _nonceTPMSize = buf.readInt(2);
        nonceTPM = new byte[_nonceTPMSize];
        buf.readArrayOfInts(nonceTPM, 1, _nonceTPMSize);
        int _cpHashASize = buf.readInt(2);
        cpHashA = new byte[_cpHashASize];
        buf.readArrayOfInts(cpHashA, 1, _cpHashASize);
        int _policyRefSize = buf.readInt(2);
        policyRef = new byte[_policyRefSize];
        buf.readArrayOfInts(policyRef, 1, _policyRefSize);
        expiration =  buf.readInt(4);
        int _authSigAlg = buf.readInt(2);
        auth=null;
        if(_authSigAlg==TPM_ALG_ID.RSASSA.toInt()) {auth = new TPMS_SIGNATURE_RSASSA();}
        else if(_authSigAlg==TPM_ALG_ID.RSAPSS.toInt()) {auth = new TPMS_SIGNATURE_RSAPSS();}
        else if(_authSigAlg==TPM_ALG_ID.ECDSA.toInt()) {auth = new TPMS_SIGNATURE_ECDSA();}
        else if(_authSigAlg==TPM_ALG_ID.ECDAA.toInt()) {auth = new TPMS_SIGNATURE_ECDAA();}
        // code generator workaround BUGBUG >> (probChild)else if(_authSigAlg==TPM_ALG_ID.SM2.toInt()) {auth = new TPMS_SIGNATURE_SM2();}
        // code generator workaround BUGBUG >> (probChild)else if(_authSigAlg==TPM_ALG_ID.ECSCHNORR.toInt()) {auth = new TPMS_SIGNATURE_ECSCHNORR();}
        // code generator workaround BUGBUG >> (probChild)else if(_authSigAlg==TPM_ALG_ID.HMAC.toInt()) {auth = new TPMT_HA();}
        else if(_authSigAlg==TPM_ALG_ID.ANY.toInt()) {auth = new TPMS_SCHEME_HASH();}
        else if(_authSigAlg==TPM_ALG_ID.NULL.toInt()) {auth = new TPMS_NULL_SIGNATURE();}
        if(auth==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_authSigAlg).name());
        auth.initFromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PolicySigned_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicySigned_REQUEST ret = new TPM2_PolicySigned_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PolicySigned_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicySigned_REQUEST ret = new TPM2_PolicySigned_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicySigned_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authObject", authObject);
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "byte", "nonceTPM", nonceTPM);
        _p.add(d, "byte", "cpHashA", cpHashA);
        _p.add(d, "byte", "policyRef", policyRef);
        _p.add(d, "int", "expiration", expiration);
        _p.add(d, "TPMU_SIGNATURE", "auth", auth);
    };
    
    
};

//<<<


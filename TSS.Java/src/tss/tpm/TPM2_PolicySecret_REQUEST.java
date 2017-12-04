package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command includes a secret-based authorization to a policy. The caller proves knowledge of the secret value using an authorization session using the authValue associated with authHandle. A password session, an HMAC session, or a policy session containing TPM2_PolicyAuthValue() or TPM2_PolicyPassword() will satisfy this requirement.
*/
public class TPM2_PolicySecret_REQUEST extends TpmStructure
{
    /**
     * This command includes a secret-based authorization to a policy. The caller proves knowledge of the secret value using an authorization session using the authValue associated with authHandle. A password session, an HMAC session, or a policy session containing TPM2_PolicyAuthValue() or TPM2_PolicyPassword() will satisfy this requirement.
     * 
     * @param _authHandle handle for an entity providing the authorization Auth Index: 1 Auth Role: USER 
     * @param _policySession handle for the policy session being extended Auth Index: None 
     * @param _nonceTPM the policy nonce for the session This can be the Empty Buffer. 
     * @param _cpHashA digest of the command parameters to which this authorization is limited This not the cpHash for this command but the cpHash for the command to which this policy session will be applied. If it is not limited, the parameter will be the Empty Buffer. 
     * @param _policyRef a reference to a policy relating to the authorization may be the Empty Buffer Size is limited to be no larger than the nonce size supported on the TPM. 
     * @param _expiration time when authorization will expire, measured in seconds from the time that nonceTPM was generated If expiration is non-negative, a NULL Ticket is returned. See 23.2.5.
     */
    public TPM2_PolicySecret_REQUEST(TPM_HANDLE _authHandle,TPM_HANDLE _policySession,byte[] _nonceTPM,byte[] _cpHashA,byte[] _policyRef,int _expiration)
    {
        authHandle = _authHandle;
        policySession = _policySession;
        nonceTPM = _nonceTPM;
        cpHashA = _cpHashA;
        policyRef = _policyRef;
        expiration = _expiration;
    }
    /**
    * This command includes a secret-based authorization to a policy. The caller proves knowledge of the secret value using an authorization session using the authValue associated with authHandle. A password session, an HMAC session, or a policy session containing TPM2_PolicyAuthValue() or TPM2_PolicyPassword() will satisfy this requirement.
    */
    public TPM2_PolicySecret_REQUEST() {};
    /**
    * handle for an entity providing the authorization Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE authHandle;
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
    * digest of the command parameters to which this authorization is limited This not the cpHash for this command but the cpHash for the command to which this policy session will be applied. If it is not limited, the parameter will be the Empty Buffer.
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
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        authHandle.toTpm(buf);
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
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authHandle = TPM_HANDLE.fromTpm(buf);
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
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_PolicySecret_REQUEST fromTpm (byte[] x) 
    {
        TPM2_PolicySecret_REQUEST ret = new TPM2_PolicySecret_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_PolicySecret_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_PolicySecret_REQUEST ret = new TPM2_PolicySecret_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicySecret_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "byte", "nonceTPM", nonceTPM);
        _p.add(d, "byte", "cpHashA", cpHashA);
        _p.add(d, "byte", "policyRef", policyRef);
        _p.add(d, "int", "expiration", expiration);
    };
    
    
};

//<<<


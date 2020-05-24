package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command includes a signed authorization in a policy. The command ties the policy to a
 *  signing key by including the Name of the signing key in the policyDigest
 */
public class TPM2_PolicySigned_REQUEST extends TpmStructure
{
    /**
     *  handle for a key that will validate the signature
     *  Auth Index: None
     */
    public TPM_HANDLE authObject;
    
    /**
     *  handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;
    
    /**
     *  the policy nonce for the session
     *  This can be the Empty Buffer.
     */
    public byte[] nonceTPM;
    
    /**
     *  digest of the command parameters to which this authorization is limited
     *  This is not the cpHash for this command but the cpHash for the command to which this
     *  policy session will be applied. If it is not limited, the parameter
     *  will be the Empty Buffer.
     */
    public byte[] cpHashA;
    
    /**
     *  a reference to a policy relating to the authorization may be the Empty Buffer
     *  Size is limited to be no larger than the nonce size supported on the TPM.
     */
    public byte[] policyRef;
    
    /**
     *  time when authorization will expire, measured in seconds from the time that nonceTPM was
     *  generated
     *  If expiration is non-negative, a NULL Ticket is returned. See 23.2.5.
     */
    public int expiration;
    
    /** selector of the algorithm used to construct the signature */
    public TPM_ALG_ID authSigAlg() { return auth != null ? auth.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** signed authorization (not optional) */
    public TPMU_SIGNATURE auth;
    
    public TPM2_PolicySigned_REQUEST()
    {
        authObject = new TPM_HANDLE();
        policySession = new TPM_HANDLE();
    }

    /**
     *  @param _authObject handle for a key that will validate the signature
     *         Auth Index: None
     *  @param _policySession handle for the policy session being extended
     *         Auth Index: None
     *  @param _nonceTPM the policy nonce for the session
     *         This can be the Empty Buffer.
     *  @param _cpHashA digest of the command parameters to which this authorization is limited
     *         This is not the cpHash for this command but the cpHash for the command to which this
     *         policy session will be applied. If it is not limited, the parameter
     *         will be the Empty Buffer.
     *  @param _policyRef a reference to a policy relating to the authorization may be the Empty Buffer
     *         Size is limited to be no larger than the nonce size supported on the TPM.
     *  @param _expiration time when authorization will expire, measured in seconds from the time that nonceTPM was
     *         generated
     *         If expiration is non-negative, a NULL Ticket is returned. See 23.2.5.
     *  @param _auth signed authorization (not optional)
     *         (One of [TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA,
     *         TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TPMT_HA,
     *         TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE])
     */
    public TPM2_PolicySigned_REQUEST(TPM_HANDLE _authObject, TPM_HANDLE _policySession, byte[] _nonceTPM, byte[] _cpHashA, byte[] _policyRef, int _expiration, TPMU_SIGNATURE _auth)
    {
        authObject = _authObject;
        policySession = _policySession;
        nonceTPM = _nonceTPM;
        cpHashA = _cpHashA;
        policyRef = _policyRef;
        expiration = _expiration;
        auth = _auth;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(nonceTPM);
        buf.writeSizedByteBuf(cpHashA);
        buf.writeSizedByteBuf(policyRef);
        buf.writeInt(expiration);
        auth.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)auth).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _nonceTPMSize = buf.readShort() & 0xFFFF;
        nonceTPM = new byte[_nonceTPMSize];
        buf.readArrayOfInts(nonceTPM, 1, _nonceTPMSize);
        int _cpHashASize = buf.readShort() & 0xFFFF;
        cpHashA = new byte[_cpHashASize];
        buf.readArrayOfInts(cpHashA, 1, _cpHashASize);
        int _policyRefSize = buf.readShort() & 0xFFFF;
        policyRef = new byte[_policyRefSize];
        buf.readArrayOfInts(policyRef, 1, _policyRefSize);
        expiration = buf.readInt();
        int _authSigAlg = buf.readShort() & 0xFFFF;
        auth = UnionFactory.create("TPMU_SIGNATURE", new TPM_ALG_ID(_authSigAlg));
        auth.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
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
    }
}

//<<<

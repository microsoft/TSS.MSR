package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command includes a signed authorization in a policy. The command ties the policy
 *  to a signing key by including the Name of the signing key in the policyDigest
 */
public class TPM2_PolicySigned_REQUEST extends ReqStructure
{
    /** Handle for a key that will validate the signature
     *  Auth Index: None
     */
    public TPM_HANDLE authObject;

    /** Handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;

    /** The policy nonce for the session
     *  This can be the Empty Buffer.
     */
    public byte[] nonceTPM;

    /** Digest of the command parameters to which this authorization is limited
     *  This is not the cpHash for this command but the cpHash for the command to which this
     *  policy session will be applied. If it is not limited, the parameter will be the Empty Buffer.
     */
    public byte[] cpHashA;

    /** A reference to a policy relating to the authorization may be the Empty Buffer
     *  Size is limited to be no larger than the nonce size supported on the TPM.
     */
    public byte[] policyRef;

    /** Time when authorization will expire, measured in seconds from the time that nonceTPM
     *  was generated
     *  If expiration is non-negative, a NULL Ticket is returned. See 23.2.5.
     */
    public int expiration;

    /** Selector of the algorithm used to construct the signature */
    public TPM_ALG_ID authSigAlg() { return auth != null ? auth.GetUnionSelector() : TPM_ALG_ID.NULL; }

    /** Signed authorization (not optional)
     *  One of: TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA,
     *  TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TPMT_HA,
     *  TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE.
     */
    public TPMU_SIGNATURE auth;

    public TPM2_PolicySigned_REQUEST()
    {
        authObject = new TPM_HANDLE();
        policySession = new TPM_HANDLE();
    }

    /** @param _authObject Handle for a key that will validate the signature
     *         Auth Index: None
     *  @param _policySession Handle for the policy session being extended
     *         Auth Index: None
     *  @param _nonceTPM The policy nonce for the session
     *         This can be the Empty Buffer.
     *  @param _cpHashA Digest of the command parameters to which this authorization is limited
     *         This is not the cpHash for this command but the cpHash for the command to which
     *         this policy session will be applied. If it is not limited, the parameter will be
     *         the Empty Buffer.
     *  @param _policyRef A reference to a policy relating to the authorization may be the
     *  Empty Buffer
     *         Size is limited to be no larger than the nonce size supported on the TPM.
     *  @param _expiration Time when authorization will expire, measured in seconds from the time
     *         that nonceTPM was generated
     *         If expiration is non-negative, a NULL Ticket is returned. See 23.2.5.
     *  @param _auth Signed authorization (not optional)
     *         One of: TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA,
     *         TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TPMT_HA,
     *         TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE.
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

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(nonceTPM);
        buf.writeSizedByteBuf(cpHashA);
        buf.writeSizedByteBuf(policyRef);
        buf.writeInt(expiration);
        buf.writeShort(auth.GetUnionSelector());
        auth.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        nonceTPM = buf.readSizedByteBuf();
        cpHashA = buf.readSizedByteBuf();
        policyRef = buf.readSizedByteBuf();
        expiration = buf.readInt();
        TPM_ALG_ID authSigAlg = TPM_ALG_ID.fromTpm(buf);
        auth = UnionFactory.create("TPMU_SIGNATURE", authSigAlg);
        auth.initFromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_PolicySigned_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PolicySigned_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_PolicySigned_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_PolicySigned_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PolicySigned_REQUEST.class);
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
        _p.add(d, "byte[]", "nonceTPM", nonceTPM);
        _p.add(d, "byte[]", "cpHashA", cpHashA);
        _p.add(d, "byte[]", "policyRef", policyRef);
        _p.add(d, "int", "expiration", expiration);
        _p.add(d, "TPMU_SIGNATURE", "auth", auth);
    }

    @Override
    public int numHandles() { return 2; }

    @Override
    public int numAuthHandles() { return 0; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {authObject, policySession}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

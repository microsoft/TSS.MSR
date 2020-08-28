package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns a digital signature of the audit session digest.  */
public class TPM2_GetSessionAuditDigest_REQUEST extends ReqStructure
{
    /** Handle of the privacy administrator (TPM_RH_ENDORSEMENT)
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE privacyAdminHandle;

    /** Handle of the signing key
     *  Auth Index: 2
     *  Auth Role: USER
     */
    public TPM_HANDLE signHandle;

    /** Handle of the audit session
     *  Auth Index: None
     */
    public TPM_HANDLE sessionHandle;

    /** User-provided qualifying data may be zero-length  */
    public byte[] qualifyingData;

    /** Scheme selector  */
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }

    /** Signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
     *  One of: TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *  TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR,
     *  TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME.
     */
    public TPMU_SIG_SCHEME inScheme;

    public TPM2_GetSessionAuditDigest_REQUEST()
    {
        privacyAdminHandle = new TPM_HANDLE();
        signHandle = new TPM_HANDLE();
        sessionHandle = new TPM_HANDLE();
    }

    /** @param _privacyAdminHandle Handle of the privacy administrator (TPM_RH_ENDORSEMENT)
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _signHandle Handle of the signing key
     *         Auth Index: 2
     *         Auth Role: USER
     *  @param _sessionHandle Handle of the audit session
     *         Auth Index: None
     *  @param _qualifyingData User-provided qualifying data may be zero-length
     *  @param _inScheme Signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
     *         One of: TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *         TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR,
     *         TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME.
     */
    public TPM2_GetSessionAuditDigest_REQUEST(TPM_HANDLE _privacyAdminHandle, TPM_HANDLE _signHandle, TPM_HANDLE _sessionHandle, byte[] _qualifyingData, TPMU_SIG_SCHEME _inScheme)
    {
        privacyAdminHandle = _privacyAdminHandle;
        signHandle = _signHandle;
        sessionHandle = _sessionHandle;
        qualifyingData = _qualifyingData;
        inScheme = _inScheme;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(qualifyingData);
        buf.writeShort(inScheme.GetUnionSelector());
        inScheme.toTpm(buf);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        qualifyingData = buf.readSizedByteBuf();
        TPM_ALG_ID inSchemeScheme = TPM_ALG_ID.fromTpm(buf);
        inScheme = UnionFactory.create("TPMU_SIG_SCHEME", inSchemeScheme);
        inScheme.initFromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_GetSessionAuditDigest_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_GetSessionAuditDigest_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_GetSessionAuditDigest_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_GetSessionAuditDigest_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_GetSessionAuditDigest_REQUEST.class);
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
        _p.add(d, "byte[]", "qualifyingData", qualifyingData);
        _p.add(d, "TPMU_SIG_SCHEME", "inScheme", inScheme);
    }

    @Override
    public int numHandles() { return 3; }

    @Override
    public int numAuthHandles() { return 2; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {privacyAdminHandle, signHandle, sessionHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

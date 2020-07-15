package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns the current value of the command audit digest, a digest of the
 *  commands being audited, and the audit hash algorithm. These values are placed in an
 *  attestation structure and signed with the key referenced by signHandle.
 */
public class TPM2_GetCommandAuditDigest_REQUEST extends ReqStructure
{
    /** Handle of the privacy administrator (TPM_RH_ENDORSEMENT)
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE privacyHandle;
    
    /** The handle of the signing key
     *  Auth Index: 2
     *  Auth Role: USER
     */
    public TPM_HANDLE signHandle;
    
    /** Other data to associate with this audit digest  */
    public byte[] qualifyingData;
    
    /** Scheme selector  */
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** Signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
     *  One of: TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *  TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR,
     *  TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME.
     */
    public TPMU_SIG_SCHEME inScheme;
    
    public TPM2_GetCommandAuditDigest_REQUEST()
    {
        privacyHandle = new TPM_HANDLE();
        signHandle = new TPM_HANDLE();
    }
    
    /** @param _privacyHandle Handle of the privacy administrator (TPM_RH_ENDORSEMENT)
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _signHandle The handle of the signing key
     *         Auth Index: 2
     *         Auth Role: USER
     *  @param _qualifyingData Other data to associate with this audit digest
     *  @param _inScheme Signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
     *         One of: TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *         TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR,
     *         TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME.
     */
    public TPM2_GetCommandAuditDigest_REQUEST(TPM_HANDLE _privacyHandle, TPM_HANDLE _signHandle, byte[] _qualifyingData, TPMU_SIG_SCHEME _inScheme)
    {
        privacyHandle = _privacyHandle;
        signHandle = _signHandle;
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
    public static TPM2_GetCommandAuditDigest_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_GetCommandAuditDigest_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_GetCommandAuditDigest_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_GetCommandAuditDigest_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_GetCommandAuditDigest_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_GetCommandAuditDigest_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "privacyHandle", privacyHandle);
        _p.add(d, "TPM_HANDLE", "signHandle", signHandle);
        _p.add(d, "byte", "qualifyingData", qualifyingData);
        _p.add(d, "TPMU_SIG_SCHEME", "inScheme", inScheme);
    }

    @Override
    public int numHandles() { return 2; }

    @Override
    public int numAuthHandles() { return 2; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {privacyHandle, signHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

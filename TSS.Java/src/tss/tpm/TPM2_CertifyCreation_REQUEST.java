package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to prove the association between an object and its creation data.
 *  The TPM will validate that the ticket was produced by the TPM and that the ticket
 *  validates the association between a loaded public area and the provided hash of the
 *  creation data (creationHash).
 */
public class TPM2_CertifyCreation_REQUEST extends ReqStructure
{
    /** Handle of the key that will sign the attestation block
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE signHandle;
    
    /** The object associated with the creation data
     *  Auth Index: None
     */
    public TPM_HANDLE objectHandle;
    
    /** User-provided qualifying data  */
    public byte[] qualifyingData;
    
    /** Hash of the creation data produced by TPM2_Create() or TPM2_CreatePrimary()  */
    public byte[] creationHash;
    
    /** Scheme selector  */
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** Signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
     *  One of: TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *  TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR,
     *  TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME.
     */
    public TPMU_SIG_SCHEME inScheme;
    
    /** Ticket produced by TPM2_Create() or TPM2_CreatePrimary()  */
    public TPMT_TK_CREATION creationTicket;
    
    public TPM2_CertifyCreation_REQUEST()
    {
        signHandle = new TPM_HANDLE();
        objectHandle = new TPM_HANDLE();
    }
    
    /** @param _signHandle Handle of the key that will sign the attestation block
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _objectHandle The object associated with the creation data
     *         Auth Index: None
     *  @param _qualifyingData User-provided qualifying data
     *  @param _creationHash Hash of the creation data produced by TPM2_Create() or TPM2_CreatePrimary()
     *  @param _inScheme Signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
     *         One of: TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *         TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR,
     *         TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME.
     *  @param _creationTicket Ticket produced by TPM2_Create() or TPM2_CreatePrimary()
     */
    public TPM2_CertifyCreation_REQUEST(TPM_HANDLE _signHandle, TPM_HANDLE _objectHandle, byte[] _qualifyingData, byte[] _creationHash, TPMU_SIG_SCHEME _inScheme, TPMT_TK_CREATION _creationTicket)
    {
        signHandle = _signHandle;
        objectHandle = _objectHandle;
        qualifyingData = _qualifyingData;
        creationHash = _creationHash;
        inScheme = _inScheme;
        creationTicket = _creationTicket;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(qualifyingData);
        buf.writeSizedByteBuf(creationHash);
        buf.writeShort(inScheme.GetUnionSelector());
        inScheme.toTpm(buf);
        creationTicket.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        qualifyingData = buf.readSizedByteBuf();
        creationHash = buf.readSizedByteBuf();
        TPM_ALG_ID inSchemeScheme = TPM_ALG_ID.fromTpm(buf);
        inScheme = UnionFactory.create("TPMU_SIG_SCHEME", inSchemeScheme);
        inScheme.initFromTpm(buf);
        creationTicket = TPMT_TK_CREATION.fromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_CertifyCreation_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_CertifyCreation_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_CertifyCreation_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_CertifyCreation_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_CertifyCreation_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_CertifyCreation_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "signHandle", signHandle);
        _p.add(d, "TPM_HANDLE", "objectHandle", objectHandle);
        _p.add(d, "byte", "qualifyingData", qualifyingData);
        _p.add(d, "byte", "creationHash", creationHash);
        _p.add(d, "TPMU_SIG_SCHEME", "inScheme", inScheme);
        _p.add(d, "TPMT_TK_CREATION", "creationTicket", creationTicket);
    }

    @Override
    public int numHandles() { return 2; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {signHandle, objectHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

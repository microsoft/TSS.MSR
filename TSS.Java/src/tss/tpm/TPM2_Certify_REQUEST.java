package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** The purpose of this command is to prove that an object with a specific Name is loaded
 *  in the TPM. By certifying that the object is loaded, the TPM warrants that a public
 *  area with a given Name is self-consistent and associated with a valid sensitive area.
 *  If a relying party has a public area that has the same Name as a Name certified with
 *  this command, then the values in that public area are correct.
 */
public class TPM2_Certify_REQUEST extends ReqStructure
{
    /** Handle of the object to be certified
     *  Auth Index: 1
     *  Auth Role: ADMIN
     */
    public TPM_HANDLE objectHandle;

    /** Handle of the key used to sign the attestation structure
     *  Auth Index: 2
     *  Auth Role: USER
     */
    public TPM_HANDLE signHandle;

    /** User provided qualifying data */
    public byte[] qualifyingData;

    /** Scheme selector */
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }

    /** Signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
     *  One of: TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *  TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR,
     *  TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME.
     */
    public TPMU_SIG_SCHEME inScheme;

    public TPM2_Certify_REQUEST()
    {
        objectHandle = new TPM_HANDLE();
        signHandle = new TPM_HANDLE();
    }

    /** @param _objectHandle Handle of the object to be certified
     *         Auth Index: 1
     *         Auth Role: ADMIN
     *  @param _signHandle Handle of the key used to sign the attestation structure
     *         Auth Index: 2
     *         Auth Role: USER
     *  @param _qualifyingData User provided qualifying data
     *  @param _inScheme Signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
     *         One of: TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *         TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR,
     *         TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME.
     */
    public TPM2_Certify_REQUEST(TPM_HANDLE _objectHandle, TPM_HANDLE _signHandle, byte[] _qualifyingData, TPMU_SIG_SCHEME _inScheme)
    {
        objectHandle = _objectHandle;
        signHandle = _signHandle;
        qualifyingData = _qualifyingData;
        inScheme = _inScheme;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(qualifyingData);
        buf.writeShort(inScheme.GetUnionSelector());
        inScheme.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        qualifyingData = buf.readSizedByteBuf();
        TPM_ALG_ID inSchemeScheme = TPM_ALG_ID.fromTpm(buf);
        inScheme = UnionFactory.create("TPMU_SIG_SCHEME", inSchemeScheme);
        inScheme.initFromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_Certify_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_Certify_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_Certify_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_Certify_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_Certify_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Certify_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "objectHandle", objectHandle);
        _p.add(d, "TPM_HANDLE", "signHandle", signHandle);
        _p.add(d, "byte[]", "qualifyingData", qualifyingData);
        _p.add(d, "TPMU_SIG_SCHEME", "inScheme", inScheme);
    }

    @Override
    public int numHandles() { return 2; }

    @Override
    public int numAuthHandles() { return 2; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {objectHandle, signHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

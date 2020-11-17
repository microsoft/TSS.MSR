package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command uses platformPolicy and a TPM Vendor Authorization Key to authorize a
 *  Field Upgrade Manifest.
 */
public class TPM2_FieldUpgradeStart_REQUEST extends ReqStructure
{
    /** TPM_RH_PLATFORM+{PP}
     *  Auth Index:1
     *  Auth Role: ADMIN
     */
    public TPM_HANDLE authorization;

    /** Handle of a public area that contains the TPM Vendor Authorization Key that will be
     *  used to validate manifestSignature
     *  Auth Index: None
     */
    public TPM_HANDLE keyHandle;

    /** Digest of the first block in the field upgrade sequence */
    public byte[] fuDigest;

    /** Selector of the algorithm used to construct the signature */
    public TPM_ALG_ID manifestSignatureSigAlg() { return manifestSignature != null ? manifestSignature.GetUnionSelector() : TPM_ALG_ID.NULL; }

    /** Signature over fuDigest using the key associated with keyHandle (not optional)
     *  One of: TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA,
     *  TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TPMT_HA,
     *  TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE.
     */
    public TPMU_SIGNATURE manifestSignature;

    public TPM2_FieldUpgradeStart_REQUEST()
    {
        authorization = new TPM_HANDLE();
        keyHandle = new TPM_HANDLE();
    }

    /** @param _authorization TPM_RH_PLATFORM+{PP}
     *         Auth Index:1
     *         Auth Role: ADMIN
     *  @param _keyHandle Handle of a public area that contains the TPM Vendor Authorization Key
     *         that will be used to validate manifestSignature
     *         Auth Index: None
     *  @param _fuDigest Digest of the first block in the field upgrade sequence
     *  @param _manifestSignature Signature over fuDigest using the key associated with keyHandle
     *         (not optional)
     *         One of: TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA,
     *         TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TPMT_HA,
     *         TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE.
     */
    public TPM2_FieldUpgradeStart_REQUEST(TPM_HANDLE _authorization, TPM_HANDLE _keyHandle, byte[] _fuDigest, TPMU_SIGNATURE _manifestSignature)
    {
        authorization = _authorization;
        keyHandle = _keyHandle;
        fuDigest = _fuDigest;
        manifestSignature = _manifestSignature;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(fuDigest);
        buf.writeShort(manifestSignature.GetUnionSelector());
        manifestSignature.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        fuDigest = buf.readSizedByteBuf();
        TPM_ALG_ID manifestSignatureSigAlg = TPM_ALG_ID.fromTpm(buf);
        manifestSignature = UnionFactory.create("TPMU_SIGNATURE", manifestSignatureSigAlg);
        manifestSignature.initFromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_FieldUpgradeStart_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_FieldUpgradeStart_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_FieldUpgradeStart_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_FieldUpgradeStart_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_FieldUpgradeStart_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_FieldUpgradeStart_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authorization", authorization);
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
        _p.add(d, "byte[]", "fuDigest", fuDigest);
        _p.add(d, "TPMU_SIGNATURE", "manifestSignature", manifestSignature);
    }

    @Override
    public int numHandles() { return 2; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {authorization, keyHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 190 shows the basic algorithm-agile structure when a symmetric or asymmetric
 *  signature is indicated. The sigAlg parameter indicates the algorithm used for the
 *  signature. This structure is output from commands such as the attestation commands and
 *  TPM2_Sign, and is an input to commands such as TPM2_VerifySignature(),
 *  TPM2_PolicySigned(), and TPM2_FieldUpgradeStart().
 */
public class TPMT_SIGNATURE extends TpmStructure
{
    /** Selector of the algorithm used to construct the signature */
    public TPM_ALG_ID sigAlg() { return signature != null ? signature.GetUnionSelector() : TPM_ALG_ID.NULL; }

    /** This shall be the actual signature information.
     *  One of: TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA,
     *  TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TPMT_HA,
     *  TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE.
     */
    public TPMU_SIGNATURE signature;

    public TPMT_SIGNATURE() {}

    /** @param _signature This shall be the actual signature information.
     *         One of: TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA,
     *         TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TPMT_HA,
     *         TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE.
     */
    public TPMT_SIGNATURE(TPMU_SIGNATURE _signature) { signature = _signature; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        if (signature == null) return;
        buf.writeShort(signature.GetUnionSelector());
        signature.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        TPM_ALG_ID sigAlg = TPM_ALG_ID.fromTpm(buf);
        signature = UnionFactory.create("TPMU_SIGNATURE", sigAlg);
        signature.initFromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMT_SIGNATURE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMT_SIGNATURE.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMT_SIGNATURE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMT_SIGNATURE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMT_SIGNATURE.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_SIGNATURE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMU_SIGNATURE", "signature", signature);
    }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to prove the association between an object and its creation data.
 *  The TPM will validate that the ticket was produced by the TPM and that the ticket
 *  validates the association between a loaded public area and the provided hash of the
 *  creation data (creationHash).
 */
public class CertifyCreationResponse extends RespStructure
{
    /** The structure that was signed */
    public TPMS_ATTEST certifyInfo;

    /** Selector of the algorithm used to construct the signature */
    public TPM_ALG_ID signatureSigAlg() { return signature != null ? signature.GetUnionSelector() : TPM_ALG_ID.NULL; }

    /** The signature over certifyInfo
     *  One of: TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA,
     *  TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TPMT_HA,
     *  TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE.
     */
    public TPMU_SIGNATURE signature;

    public CertifyCreationResponse() {}

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedObj(certifyInfo);
        buf.writeShort(signature.GetUnionSelector());
        signature.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        certifyInfo = buf.createSizedObj(TPMS_ATTEST.class);
        TPM_ALG_ID signatureSigAlg = TPM_ALG_ID.fromTpm(buf);
        signature = UnionFactory.create("TPMU_SIGNATURE", signatureSigAlg);
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
    public static CertifyCreationResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(CertifyCreationResponse.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static CertifyCreationResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static CertifyCreationResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(CertifyCreationResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("CertifyCreationResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ATTEST", "certifyInfo", certifyInfo);
        _p.add(d, "TPMU_SIGNATURE", "signature", signature);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

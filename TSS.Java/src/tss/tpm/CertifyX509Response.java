package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** The purpose of this command is to generate an X.509 certificate that proves an object
 *  with a specific public key and attributes is loaded in the TPM. In contrast to
 *  TPM2_Certify, which uses a TCG-defined data structure to convey attestation
 *  information, TPM2_CertifyX509 encodes the attestation information in a DER-encoded
 *  X.509 certificate that is compliant with RFC5280 Internet X.509 Public Key
 *  Infrastructure Certificate and Certificate Revocation List (CRL) Profile.
 */
public class CertifyX509Response extends RespStructure
{
    /** A DER encoded SEQUENCE containing the DER encoded fields added to partialCertificate
     *  to make it a complete RFC5280 TBSCertificate.
     */
    public byte[] addedToCertificate;

    /** The digest that was signed  */
    public byte[] tbsDigest;

    /** Selector of the algorithm used to construct the signature  */
    public TPM_ALG_ID signatureSigAlg() { return signature != null ? signature.GetUnionSelector() : TPM_ALG_ID.NULL; }

    /** The signature over tbsDigest
     *  One of: TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA,
     *  TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TPMT_HA,
     *  TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE.
     */
    public TPMU_SIGNATURE signature;

    public CertifyX509Response() {}

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(addedToCertificate);
        buf.writeSizedByteBuf(tbsDigest);
        buf.writeShort(signature.GetUnionSelector());
        signature.toTpm(buf);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        addedToCertificate = buf.readSizedByteBuf();
        tbsDigest = buf.readSizedByteBuf();
        TPM_ALG_ID signatureSigAlg = TPM_ALG_ID.fromTpm(buf);
        signature = UnionFactory.create("TPMU_SIGNATURE", signatureSigAlg);
        signature.initFromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static CertifyX509Response fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(CertifyX509Response.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static CertifyX509Response fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static CertifyX509Response fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(CertifyX509Response.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("CertifyX509Response");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "addedToCertificate", addedToCertificate);
        _p.add(d, "byte[]", "tbsDigest", tbsDigest);
        _p.add(d, "TPMU_SIGNATURE", "signature", signature);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

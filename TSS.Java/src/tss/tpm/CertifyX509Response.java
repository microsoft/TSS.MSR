package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  The purpose of this command is to generate an X.509 certificate that proves an object with
 *  a specific public key and attributes is loaded in the TPM. In contrast to TPM2_Certify,
 *  which uses a TCG-defined data structure to convey attestation information,
 *  TPM2_CertifyX509 encodes the attestation information in a DER-encoded X.509 certificate
 *  that is compliant with RFC5280 Internet X.509 Public Key Infrastructure Certificate and
 *  Certificate Revocation List (CRL) Profile.
 */
public class CertifyX509Response extends TpmStructure
{
    /**
     *  a DER encoded SEQUENCE containing the DER encoded fields added to partialCertificate to make it a
     *  complete RFC5280 TBSCertificate.
     */
    public byte[] addedToCertificate;
    
    /** the digest that was signed */
    public byte[] tbsDigest;
    public TPM_ALG_ID signatureSigAlg() { return signature != null ? signature.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** The signature over tbsDigest */
    public TPMU_SIGNATURE signature;
    
    public CertifyX509Response() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(addedToCertificate);
        buf.writeSizedByteBuf(tbsDigest);
        signature.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)signature).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _addedToCertificateSize = buf.readShort() & 0xFFFF;
        addedToCertificate = new byte[_addedToCertificateSize];
        buf.readArrayOfInts(addedToCertificate, 1, _addedToCertificateSize);
        int _tbsDigestSize = buf.readShort() & 0xFFFF;
        tbsDigest = new byte[_tbsDigestSize];
        buf.readArrayOfInts(tbsDigest, 1, _tbsDigestSize);
        int _signatureSigAlg = buf.readShort() & 0xFFFF;
        signature = UnionFactory.create("TPMU_SIGNATURE", new TPM_ALG_ID(_signatureSigAlg));
        signature.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static CertifyX509Response fromTpm (byte[] x) 
    {
        CertifyX509Response ret = new CertifyX509Response();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static CertifyX509Response fromTpm (InByteBuf buf) 
    {
        CertifyX509Response ret = new CertifyX509Response();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_CertifyX509_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "addedToCertificate", addedToCertificate);
        _p.add(d, "byte", "tbsDigest", tbsDigest);
        _p.add(d, "TPMU_SIGNATURE", "signature", signature);
    }
}

//<<<

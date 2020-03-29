package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* The purpose of this command is to generate an X.509 certificate that proves an object with a specific public key and attributes is loaded in the TPM. In contrast to TPM2_Certify, which uses a TCG-defined data structure to convey attestation information, TPM2_CertifyX509 encodes the attestation information in a DER-encoded X.509 certificate that is compliant with RFC5280 Internet X.509 Public Key Infrastructure Certificate and Certificate Revocation List (CRL) Profile.
*/
public class CertifyX509Response extends TpmStructure
{
    /**
     * @param _addedToCertificate a DER encoded SEQUENCE containing the DER encoded fields added to partialCertificate to make it a complete RFC5280 TBSCertificate. 
     * @param _tbsDigest the digest that was signed 
     * @param _signature The signature over tbsDigest (One of [TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TPMT_HA, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE])
     */
    public CertifyX509Response(byte[] _addedToCertificate,byte[] _tbsDigest,TPMU_SIGNATURE _signature)
    {
        addedToCertificate = _addedToCertificate;
        tbsDigest = _tbsDigest;
        signature = _signature;
    }
    /**
    * The purpose of this command is to generate an X.509 certificate that proves an object with a specific public key and attributes is loaded in the TPM. In contrast to TPM2_Certify, which uses a TCG-defined data structure to convey attestation information, TPM2_CertifyX509 encodes the attestation information in a DER-encoded X.509 certificate that is compliant with RFC5280 Internet X.509 Public Key Infrastructure Certificate and Certificate Revocation List (CRL) Profile.
    */
    public CertifyX509Response() {};
    /**
    * size of the buffer
    */
    // private short addedToCertificateSize;
    /**
    * a DER encoded SEQUENCE containing the DER encoded fields added to partialCertificate to make it a complete RFC5280 TBSCertificate.
    */
    public byte[] addedToCertificate;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short tbsDigestSize;
    /**
    * the digest that was signed
    */
    public byte[] tbsDigest;
    /**
    * selector of the algorithm used to construct the signature
    */
    // private TPM_ALG_ID signatureSigAlg;
    /**
    * The signature over tbsDigest
    */
    public TPMU_SIGNATURE signature;
    public int GetUnionSelector_signature()
    {
        if(signature instanceof TPMS_SIGNATURE_RSASSA){return 0x0014; }
        if(signature instanceof TPMS_SIGNATURE_RSAPSS){return 0x0016; }
        if(signature instanceof TPMS_SIGNATURE_ECDSA){return 0x0018; }
        if(signature instanceof TPMS_SIGNATURE_ECDAA){return 0x001A; }
        if(signature instanceof TPMS_SIGNATURE_SM2){return 0x001B; }
        if(signature instanceof TPMS_SIGNATURE_ECSCHNORR){return 0x001C; }
        if(signature instanceof TPMT_HA){return 0x0005; }
        if(signature instanceof TPMS_SCHEME_HASH){return 0x7FFF; }
        if(signature instanceof TPMS_NULL_SIGNATURE){return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((addedToCertificate!=null)?addedToCertificate.length:0, 2);
        if(addedToCertificate!=null)
            buf.write(addedToCertificate);
        buf.writeInt((tbsDigest!=null)?tbsDigest.length:0, 2);
        if(tbsDigest!=null)
            buf.write(tbsDigest);
        buf.writeInt(GetUnionSelector_signature(), 2);
        ((TpmMarshaller)signature).toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _addedToCertificateSize = buf.readInt(2);
        addedToCertificate = new byte[_addedToCertificateSize];
        buf.readArrayOfInts(addedToCertificate, 1, _addedToCertificateSize);
        int _tbsDigestSize = buf.readInt(2);
        tbsDigest = new byte[_tbsDigestSize];
        buf.readArrayOfInts(tbsDigest, 1, _tbsDigestSize);
        int _signatureSigAlg = buf.readInt(2);
        signature=null;
        if(_signatureSigAlg==TPM_ALG_ID.RSASSA.toInt()) {signature = new TPMS_SIGNATURE_RSASSA();}
        else if(_signatureSigAlg==TPM_ALG_ID.RSAPSS.toInt()) {signature = new TPMS_SIGNATURE_RSAPSS();}
        else if(_signatureSigAlg==TPM_ALG_ID.ECDSA.toInt()) {signature = new TPMS_SIGNATURE_ECDSA();}
        else if(_signatureSigAlg==TPM_ALG_ID.ECDAA.toInt()) {signature = new TPMS_SIGNATURE_ECDAA();}
        // code generator workaround BUGBUG >> (probChild)else if(_signatureSigAlg==TPM_ALG_ID.SM2.toInt()) {signature = new TPMS_SIGNATURE_SM2();}
        // code generator workaround BUGBUG >> (probChild)else if(_signatureSigAlg==TPM_ALG_ID.ECSCHNORR.toInt()) {signature = new TPMS_SIGNATURE_ECSCHNORR();}
        else if(_signatureSigAlg==TPM_ALG_ID.HMAC.toInt()) {signature = new TPMT_HA();}
        else if(_signatureSigAlg==TPM_ALG_ID.ANY.toInt()) {signature = new TPMS_SCHEME_HASH();}
        else if(_signatureSigAlg==TPM_ALG_ID.NULL.toInt()) {signature = new TPMS_NULL_SIGNATURE();}
        if(signature==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_signatureSigAlg).name());
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
    };
    
    
};

//<<<


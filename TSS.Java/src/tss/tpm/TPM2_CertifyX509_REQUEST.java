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
public class TPM2_CertifyX509_REQUEST extends TpmStructure
{
    /**
     *  handle of the object to be certified
     *  Auth Index: 1
     *  Auth Role: ADMIN
     */
    public TPM_HANDLE objectHandle;
    
    /**
     *  handle of the key used to sign the attestation structure
     *  Auth Index: 2
     *  Auth Role: USER
     */
    public TPM_HANDLE signHandle;
    
    /** shall be an Empty Buffer */
    public byte[] reserved;
    
    /** signing scheme to use if the scheme for signHandle is TPM_ALG_NULL */
    public TPMU_SIG_SCHEME inScheme;
    
    /** a DER encoded partial certificate */
    public byte[] partialCertificate;
    
    public TPM2_CertifyX509_REQUEST() {}
    
    /**
     *  @param _objectHandle handle of the object to be certified
     *         Auth Index: 1
     *         Auth Role: ADMIN
     *  @param _signHandle handle of the key used to sign the attestation structure
     *         Auth Index: 2
     *         Auth Role: USER
     *  @param _reserved shall be an Empty Buffer
     *  @param _inScheme signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
     *         (One of [TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *         TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC,
     *         TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME])
     *  @param _partialCertificate a DER encoded partial certificate
     */
    public TPM2_CertifyX509_REQUEST(TPM_HANDLE _objectHandle, TPM_HANDLE _signHandle, byte[] _reserved, TPMU_SIG_SCHEME _inScheme, byte[] _partialCertificate)
    {
        objectHandle = _objectHandle;
        signHandle = _signHandle;
        reserved = _reserved;
        inScheme = _inScheme;
        partialCertificate = _partialCertificate;
    }

    public int GetUnionSelector_inScheme()
    {
        if (inScheme instanceof TPMS_SIG_SCHEME_RSASSA) { return 0x0014; }
        if (inScheme instanceof TPMS_SIG_SCHEME_RSAPSS) { return 0x0016; }
        if (inScheme instanceof TPMS_SIG_SCHEME_ECDSA) { return 0x0018; }
        if (inScheme instanceof TPMS_SIG_SCHEME_ECDAA) { return 0x001A; }
        if (inScheme instanceof TPMS_SIG_SCHEME_SM2) { return 0x001B; }
        if (inScheme instanceof TPMS_SIG_SCHEME_ECSCHNORR) { return 0x001C; }
        if (inScheme instanceof TPMS_SCHEME_HMAC) { return 0x0005; }
        if (inScheme instanceof TPMS_SCHEME_HASH) { return 0x7FFF; }
        if (inScheme instanceof TPMS_NULL_SIG_SCHEME) { return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        objectHandle.toTpm(buf);
        signHandle.toTpm(buf);
        buf.writeInt(reserved != null ? reserved.length : 0, 2);
        if (reserved != null)
            buf.write(reserved);
        buf.writeInt(GetUnionSelector_inScheme(), 2);
        ((TpmMarshaller)inScheme).toTpm(buf);
        buf.writeInt(partialCertificate != null ? partialCertificate.length : 0, 2);
        if (partialCertificate != null)
            buf.write(partialCertificate);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        objectHandle = TPM_HANDLE.fromTpm(buf);
        signHandle = TPM_HANDLE.fromTpm(buf);
        int _reservedSize = buf.readInt(2);
        reserved = new byte[_reservedSize];
        buf.readArrayOfInts(reserved, 1, _reservedSize);
        int _inSchemeScheme = buf.readInt(2);
        inScheme=null;
        if(_inSchemeScheme==TPM_ALG_ID.RSASSA.toInt()) {inScheme = new TPMS_SIG_SCHEME_RSASSA();}
        else if(_inSchemeScheme==TPM_ALG_ID.RSAPSS.toInt()) {inScheme = new TPMS_SIG_SCHEME_RSAPSS();}
        else if(_inSchemeScheme==TPM_ALG_ID.ECDSA.toInt()) {inScheme = new TPMS_SIG_SCHEME_ECDSA();}
        else if(_inSchemeScheme==TPM_ALG_ID.ECDAA.toInt()) {inScheme = new TPMS_SIG_SCHEME_ECDAA();}
        // code generator workaround BUGBUG >> (probChild)else if(_inSchemeScheme==TPM_ALG_ID.SM2.toInt()) {inScheme = new TPMS_SIG_SCHEME_SM2();}
        // code generator workaround BUGBUG >> (probChild)else if(_inSchemeScheme==TPM_ALG_ID.ECSCHNORR.toInt()) {inScheme = new TPMS_SIG_SCHEME_ECSCHNORR();}
        else if(_inSchemeScheme==TPM_ALG_ID.HMAC.toInt()) {inScheme = new TPMS_SCHEME_HMAC();}
        else if(_inSchemeScheme==TPM_ALG_ID.ANY.toInt()) {inScheme = new TPMS_SCHEME_HASH();}
        else if(_inSchemeScheme==TPM_ALG_ID.NULL.toInt()) {inScheme = new TPMS_NULL_SIG_SCHEME();}
        if (inScheme == null) throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_inSchemeScheme).name());
        inScheme.initFromTpm(buf);
        int _partialCertificateSize = buf.readInt(2);
        partialCertificate = new byte[_partialCertificateSize];
        buf.readArrayOfInts(partialCertificate, 1, _partialCertificateSize);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPM2_CertifyX509_REQUEST fromTpm (byte[] x) 
    {
        TPM2_CertifyX509_REQUEST ret = new TPM2_CertifyX509_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_CertifyX509_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_CertifyX509_REQUEST ret = new TPM2_CertifyX509_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_CertifyX509_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "objectHandle", objectHandle);
        _p.add(d, "TPM_HANDLE", "signHandle", signHandle);
        _p.add(d, "byte", "reserved", reserved);
        _p.add(d, "TPMU_SIG_SCHEME", "inScheme", inScheme);
        _p.add(d, "byte", "partialCertificate", partialCertificate);
    }
}

//<<<


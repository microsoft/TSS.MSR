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
public class TPM2_CertifyX509_REQUEST extends ReqStructure
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
    
    /** Shall be an Empty Buffer  */
    public byte[] reserved;
    
    /** Scheme selector  */
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** Signing scheme to use if the scheme for signHandle is TPM_ALG_NULL  */
    public TPMU_SIG_SCHEME inScheme;
    
    /** A DER encoded partial certificate  */
    public byte[] partialCertificate;
    
    public TPM2_CertifyX509_REQUEST()
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
     *  @param _reserved Shall be an Empty Buffer
     *  @param _inScheme Signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
     *         (One of [TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *         TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR,
     *         TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME])
     *  @param _partialCertificate A DER encoded partial certificate
     */
    public TPM2_CertifyX509_REQUEST(TPM_HANDLE _objectHandle, TPM_HANDLE _signHandle, byte[] _reserved, TPMU_SIG_SCHEME _inScheme, byte[] _partialCertificate)
    {
        objectHandle = _objectHandle;
        signHandle = _signHandle;
        reserved = _reserved;
        inScheme = _inScheme;
        partialCertificate = _partialCertificate;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(reserved);
        buf.writeShort(inScheme.GetUnionSelector());
        inScheme.toTpm(buf);
        buf.writeSizedByteBuf(partialCertificate);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        reserved = buf.readSizedByteBuf();
        TPM_ALG_ID inSchemeScheme = TPM_ALG_ID.fromTpm(buf);
        inScheme = UnionFactory.create("TPMU_SIG_SCHEME", inSchemeScheme);
        inScheme.initFromTpm(buf);
        partialCertificate = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_CertifyX509_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_CertifyX509_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_CertifyX509_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_CertifyX509_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_CertifyX509_REQUEST.class);
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

    @Override
    public int numHandles() { return 2; }
    
    public int numAuthHandles() { return 2; }
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {objectHandle, signHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs RSA encryption using the indicated padding scheme according to
 *  IETF RFC 8017. If the scheme of keyHandle is TPM_ALG_NULL, then the caller may use
 *  inScheme to specify the padding scheme. If scheme of keyHandle is not TPM_ALG_NULL,
 *  then inScheme shall either be TPM_ALG_NULL or be the same as scheme (TPM_RC_SCHEME).
 */
public class TPM2_RSA_Encrypt_REQUEST extends ReqStructure
{
    /** Reference to public portion of RSA key to use for encryption
     *  Auth Index: None
     */
    public TPM_HANDLE keyHandle;

    /** Message to be encrypted
     *  NOTE 1 The data type was chosen because it limits the overall size of the input to no
     *  greater than the size of the largest RSA public key. This may be larger than allowed
     *  for keyHandle.
     */
    public byte[] message;

    /** Scheme selector  */
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }

    /** The padding scheme to use if scheme associated with keyHandle is TPM_ALG_NULL
     *  One of: TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
     *  TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA,
     *  TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES,
     *  TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME.
     */
    public TPMU_ASYM_SCHEME inScheme;

    /** Optional label L to be associated with the message
     *  Size of the buffer is zero if no label is present
     *  NOTE 2 See description of label above.
     */
    public byte[] label;

    public TPM2_RSA_Encrypt_REQUEST() { keyHandle = new TPM_HANDLE(); }

    /** @param _keyHandle Reference to public portion of RSA key to use for encryption
     *         Auth Index: None
     *  @param _message Message to be encrypted
     *         NOTE 1 The data type was chosen because it limits the overall size of the input
     *  to
     *         no greater than the size of the largest RSA public key. This may be larger than
     *         allowed for keyHandle.
     *  @param _inScheme The padding scheme to use if scheme associated with keyHandle is TPM_ALG_NULL
     *         One of: TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
     *         TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA,
     *         TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES,
     *         TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME.
     *  @param _label Optional label L to be associated with the message
     *         Size of the buffer is zero if no label is present
     *         NOTE 2 See description of label above.
     */
    public TPM2_RSA_Encrypt_REQUEST(TPM_HANDLE _keyHandle, byte[] _message, TPMU_ASYM_SCHEME _inScheme, byte[] _label)
    {
        keyHandle = _keyHandle;
        message = _message;
        inScheme = _inScheme;
        label = _label;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(message);
        buf.writeShort(inScheme.GetUnionSelector());
        inScheme.toTpm(buf);
        buf.writeSizedByteBuf(label);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        message = buf.readSizedByteBuf();
        TPM_ALG_ID inSchemeScheme = TPM_ALG_ID.fromTpm(buf);
        inScheme = UnionFactory.create("TPMU_ASYM_SCHEME", inSchemeScheme);
        inScheme.initFromTpm(buf);
        label = buf.readSizedByteBuf();
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_RSA_Encrypt_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_RSA_Encrypt_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_RSA_Encrypt_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_RSA_Encrypt_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_RSA_Encrypt_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_RSA_Encrypt_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
        _p.add(d, "byte[]", "message", message);
        _p.add(d, "TPMU_ASYM_SCHEME", "inScheme", inScheme);
        _p.add(d, "byte[]", "label", label);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 0; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {keyHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

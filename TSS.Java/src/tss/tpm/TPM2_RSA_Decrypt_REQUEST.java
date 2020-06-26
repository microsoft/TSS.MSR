package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs RSA decryption using the indicated padding scheme according to
 *  IETF RFC 8017 ((PKCS#1).
 */
public class TPM2_RSA_Decrypt_REQUEST extends TpmStructure
{
    /** RSA key to use for decryption
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE keyHandle;
    
    /** Cipher text to be decrypted
     *  NOTE An encrypted RSA data block is the size of the public modulus.
     */
    public byte[] cipherText;
    
    /** Scheme selector  */
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** The padding scheme to use if scheme associated with keyHandle is TPM_ALG_NULL  */
    public TPMU_ASYM_SCHEME inScheme;
    
    /** Label whose association with the message is to be verified  */
    public byte[] label;
    
    public TPM2_RSA_Decrypt_REQUEST() { keyHandle = new TPM_HANDLE(); }
    
    /** @param _keyHandle RSA key to use for decryption
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _cipherText Cipher text to be decrypted
     *         NOTE An encrypted RSA data block is the size of the public modulus.
     *  @param _inScheme The padding scheme to use if scheme associated with keyHandle is TPM_ALG_NULL
     *         (One of [TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
     *         TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA,
     *         TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES,
     *         TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME])
     *  @param _label Label whose association with the message is to be verified
     */
    public TPM2_RSA_Decrypt_REQUEST(TPM_HANDLE _keyHandle, byte[] _cipherText, TPMU_ASYM_SCHEME _inScheme, byte[] _label)
    {
        keyHandle = _keyHandle;
        cipherText = _cipherText;
        inScheme = _inScheme;
        label = _label;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(cipherText);
        buf.writeShort(inScheme.GetUnionSelector());
        inScheme.toTpm(buf);
        buf.writeSizedByteBuf(label);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        cipherText = buf.readSizedByteBuf();
        TPM_ALG_ID inSchemeScheme = TPM_ALG_ID.fromTpm(buf);
        inScheme = UnionFactory.create("TPMU_ASYM_SCHEME", inSchemeScheme);
        inScheme.initFromTpm(buf);
        label = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_RSA_Decrypt_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_RSA_Decrypt_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_RSA_Decrypt_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_RSA_Decrypt_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_RSA_Decrypt_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_RSA_Decrypt_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
        _p.add(d, "byte", "cipherText", cipherText);
        _p.add(d, "TPMU_ASYM_SCHEME", "inScheme", inScheme);
        _p.add(d, "byte", "label", label);
    }
}

//<<<

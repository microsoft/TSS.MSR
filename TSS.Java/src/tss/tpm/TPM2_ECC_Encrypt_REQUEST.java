package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs ECC encryption as described in Part 1, Annex D.  */
public class TPM2_ECC_Encrypt_REQUEST extends ReqStructure
{
    /** Reference to public portion of ECC key to use for encryption
     *  Auth Index: None
     */
    public TPM_HANDLE keyHandle;
    
    /** Plaintext to be encrypted  */
    public byte[] plainText;
    
    /** Scheme selector  */
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** The KDF to use if scheme associated with keyHandle is TPM_ALG_NULL  */
    public TPMU_KDF_SCHEME inScheme;
    
    public TPM2_ECC_Encrypt_REQUEST() { keyHandle = new TPM_HANDLE(); }
    
    /** @param _keyHandle Reference to public portion of ECC key to use for encryption
     *         Auth Index: None
     *  @param _plainText Plaintext to be encrypted
     *  @param _inScheme The KDF to use if scheme associated with keyHandle is TPM_ALG_NULL
     *         (One of [TPMS_KDF_SCHEME_MGF1, TPMS_KDF_SCHEME_KDF1_SP800_56A,
     *         TPMS_KDF_SCHEME_KDF2, TPMS_KDF_SCHEME_KDF1_SP800_108, TPMS_SCHEME_HASH,
     *  TPMS_NULL_KDF_SCHEME])
     */
    public TPM2_ECC_Encrypt_REQUEST(TPM_HANDLE _keyHandle, byte[] _plainText, TPMU_KDF_SCHEME _inScheme)
    {
        keyHandle = _keyHandle;
        plainText = _plainText;
        inScheme = _inScheme;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(plainText);
        buf.writeShort(inScheme.GetUnionSelector());
        inScheme.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        plainText = buf.readSizedByteBuf();
        TPM_ALG_ID inSchemeScheme = TPM_ALG_ID.fromTpm(buf);
        inScheme = UnionFactory.create("TPMU_KDF_SCHEME", inSchemeScheme);
        inScheme.initFromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_ECC_Encrypt_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_ECC_Encrypt_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_ECC_Encrypt_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_ECC_Encrypt_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_ECC_Encrypt_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ECC_Encrypt_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
        _p.add(d, "byte", "plainText", plainText);
        _p.add(d, "TPMU_KDF_SCHEME", "inScheme", inScheme);
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

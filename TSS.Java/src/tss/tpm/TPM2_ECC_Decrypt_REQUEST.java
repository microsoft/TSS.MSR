package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs ECC decryption.  */
public class TPM2_ECC_Decrypt_REQUEST extends ReqStructure
{
    /** ECC key to use for decryption
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE keyHandle;
    
    /** The public ephemeral key used for ECDH  */
    public TPMS_ECC_POINT C1;
    
    /** The data block produced by the XOR process  */
    public byte[] C2;
    
    /** The integrity value  */
    public byte[] C3;
    
    /** Scheme selector  */
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** The KDF to use if scheme associated with keyHandle is TPM_ALG_NULL
     *  One of: TPMS_KDF_SCHEME_MGF1, TPMS_KDF_SCHEME_KDF1_SP800_56A, TPMS_KDF_SCHEME_KDF2,
     *  TPMS_KDF_SCHEME_KDF1_SP800_108, TPMS_SCHEME_HASH, TPMS_NULL_KDF_SCHEME.
     */
    public TPMU_KDF_SCHEME inScheme;
    
    public TPM2_ECC_Decrypt_REQUEST() { keyHandle = new TPM_HANDLE(); }
    
    /** @param _keyHandle ECC key to use for decryption
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _C1 The public ephemeral key used for ECDH
     *  @param _C2 The data block produced by the XOR process
     *  @param _C3 The integrity value
     *  @param _inScheme The KDF to use if scheme associated with keyHandle is TPM_ALG_NULL
     *         One of: TPMS_KDF_SCHEME_MGF1, TPMS_KDF_SCHEME_KDF1_SP800_56A, TPMS_KDF_SCHEME_KDF2,
     *         TPMS_KDF_SCHEME_KDF1_SP800_108, TPMS_SCHEME_HASH, TPMS_NULL_KDF_SCHEME.
     */
    public TPM2_ECC_Decrypt_REQUEST(TPM_HANDLE _keyHandle, TPMS_ECC_POINT _C1, byte[] _C2, byte[] _C3, TPMU_KDF_SCHEME _inScheme)
    {
        keyHandle = _keyHandle;
        C1 = _C1;
        C2 = _C2;
        C3 = _C3;
        inScheme = _inScheme;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedObj(C1);
        buf.writeSizedByteBuf(C2);
        buf.writeSizedByteBuf(C3);
        buf.writeShort(inScheme.GetUnionSelector());
        inScheme.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        C1 = buf.createSizedObj(TPMS_ECC_POINT.class);
        C2 = buf.readSizedByteBuf();
        C3 = buf.readSizedByteBuf();
        TPM_ALG_ID inSchemeScheme = TPM_ALG_ID.fromTpm(buf);
        inScheme = UnionFactory.create("TPMU_KDF_SCHEME", inSchemeScheme);
        inScheme.initFromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_ECC_Decrypt_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_ECC_Decrypt_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_ECC_Decrypt_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_ECC_Decrypt_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_ECC_Decrypt_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ECC_Decrypt_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
        _p.add(d, "TPMS_ECC_POINT", "C1", C1);
        _p.add(d, "byte[]", "C2", C2);
        _p.add(d, "byte[]", "C3", C3);
        _p.add(d, "TPMU_KDF_SCHEME", "inScheme", inScheme);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {keyHandle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

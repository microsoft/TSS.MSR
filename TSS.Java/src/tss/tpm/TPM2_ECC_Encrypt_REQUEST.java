package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs ECC encryption as described in Part 1, Annex D. */
public class TPM2_ECC_Encrypt_REQUEST extends TpmStructure
{
    /**
     *  reference to public portion of ECC key to use for encryption
     *  Auth Index: None
     */
    public TPM_HANDLE keyHandle;
    
    /** Plaintext to be encrypted */
    public byte[] plainText;
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** the KDF to use if scheme associated with keyHandle is TPM_ALG_NULL */
    public TPMU_KDF_SCHEME inScheme;
    
    public TPM2_ECC_Encrypt_REQUEST() { keyHandle = new TPM_HANDLE(); }
    
    /**
     *  @param _keyHandle reference to public portion of ECC key to use for encryption
     *         Auth Index: None
     *  @param _plainText Plaintext to be encrypted
     *  @param _inScheme the KDF to use if scheme associated with keyHandle is TPM_ALG_NULL
     *         (One of [TPMS_KDF_SCHEME_MGF1, TPMS_KDF_SCHEME_KDF1_SP800_56A, TPMS_KDF_SCHEME_KDF2,
     *         TPMS_KDF_SCHEME_KDF1_SP800_108, TPMS_SCHEME_HASH, TPMS_NULL_KDF_SCHEME])
     */
    public TPM2_ECC_Encrypt_REQUEST(TPM_HANDLE _keyHandle, byte[] _plainText, TPMU_KDF_SCHEME _inScheme)
    {
        keyHandle = _keyHandle;
        plainText = _plainText;
        inScheme = _inScheme;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        keyHandle.toTpm(buf);
        buf.writeSizedByteBuf(plainText);
        inScheme.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)inScheme).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        keyHandle = TPM_HANDLE.fromTpm(buf);
        int _plainTextSize = buf.readShort() & 0xFFFF;
        plainText = new byte[_plainTextSize];
        buf.readArrayOfInts(plainText, 1, _plainTextSize);
        int _inSchemeScheme = buf.readShort() & 0xFFFF;
        inScheme = UnionFactory.create("TPMU_KDF_SCHEME", new TPM_ALG_ID(_inSchemeScheme));
        inScheme.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPM2_ECC_Encrypt_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ECC_Encrypt_REQUEST ret = new TPM2_ECC_Encrypt_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_ECC_Encrypt_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ECC_Encrypt_REQUEST ret = new TPM2_ECC_Encrypt_REQUEST();
        ret.initFromTpm(buf);
        return ret;
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
}

//<<<

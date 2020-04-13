package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs ECC decryption. */
public class TPM2_ECC_Decrypt_REQUEST extends TpmStructure
{
    /**
     *  ECC key to use for decryption
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE keyHandle;
    
    /** the public ephemeral key used for ECDH */
    public TPMS_ECC_POINT C1;
    
    /** the data block produced by the XOR process */
    public byte[] C2;
    
    /** the integrity value */
    public byte[] C3;
    
    /** the KDF to use if scheme associated with keyHandle is TPM_ALG_NULL */
    public TPMU_KDF_SCHEME inScheme;
    
    public TPM2_ECC_Decrypt_REQUEST() {}
    
    /**
     *  @param _keyHandle ECC key to use for decryption
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _C1 the public ephemeral key used for ECDH
     *  @param _C2 the data block produced by the XOR process
     *  @param _C3 the integrity value
     *  @param _inScheme the KDF to use if scheme associated with keyHandle is TPM_ALG_NULL
     *         (One of [TPMS_KDF_SCHEME_MGF1, TPMS_KDF_SCHEME_KDF1_SP800_56A, TPMS_KDF_SCHEME_KDF2,
     *         TPMS_KDF_SCHEME_KDF1_SP800_108, TPMS_SCHEME_HASH, TPMS_NULL_KDF_SCHEME])
     */
    public TPM2_ECC_Decrypt_REQUEST(TPM_HANDLE _keyHandle, TPMS_ECC_POINT _C1, byte[] _C2, byte[] _C3, TPMU_KDF_SCHEME _inScheme)
    {
        keyHandle = _keyHandle;
        C1 = _C1;
        C2 = _C2;
        C3 = _C3;
        inScheme = _inScheme;
    }

    public int GetUnionSelector_inScheme()
    {
        if (inScheme instanceof TPMS_KDF_SCHEME_MGF1) { return 0x0007; }
        if (inScheme instanceof TPMS_KDF_SCHEME_KDF1_SP800_56A) { return 0x0020; }
        if (inScheme instanceof TPMS_KDF_SCHEME_KDF2) { return 0x0021; }
        if (inScheme instanceof TPMS_KDF_SCHEME_KDF1_SP800_108) { return 0x0022; }
        if (inScheme instanceof TPMS_SCHEME_HASH) { return 0x7FFF; }
        if (inScheme instanceof TPMS_NULL_KDF_SCHEME) { return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        keyHandle.toTpm(buf);
        buf.writeInt(C1 != null ? C1.toTpm().length : 0, 2);
        if (C1 != null)
            C1.toTpm(buf);
        buf.writeInt(C2 != null ? C2.length : 0, 2);
        if (C2 != null)
            buf.write(C2);
        buf.writeInt(C3 != null ? C3.length : 0, 2);
        if (C3 != null)
            buf.write(C3);
        buf.writeInt(GetUnionSelector_inScheme(), 2);
        ((TpmMarshaller)inScheme).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        keyHandle = TPM_HANDLE.fromTpm(buf);
        int _C1Size = buf.readInt(2);
        buf.structSize.push(buf.new SizedStructInfo(buf.curPos(), _C1Size));
        C1 = TPMS_ECC_POINT.fromTpm(buf);
        buf.structSize.pop();
        int _C2Size = buf.readInt(2);
        C2 = new byte[_C2Size];
        buf.readArrayOfInts(C2, 1, _C2Size);
        int _C3Size = buf.readInt(2);
        C3 = new byte[_C3Size];
        buf.readArrayOfInts(C3, 1, _C3Size);
        int _inSchemeScheme = buf.readInt(2);
        inScheme=null;
        if(_inSchemeScheme==TPM_ALG_ID.MGF1.toInt()) {inScheme = new TPMS_KDF_SCHEME_MGF1();}
        else if(_inSchemeScheme==TPM_ALG_ID.KDF1_SP800_56A.toInt()) {inScheme = new TPMS_KDF_SCHEME_KDF1_SP800_56A();}
        else if(_inSchemeScheme==TPM_ALG_ID.KDF2.toInt()) {inScheme = new TPMS_KDF_SCHEME_KDF2();}
        else if(_inSchemeScheme==TPM_ALG_ID.KDF1_SP800_108.toInt()) {inScheme = new TPMS_KDF_SCHEME_KDF1_SP800_108();}
        else if(_inSchemeScheme==TPM_ALG_ID.ANY.toInt()) {inScheme = new TPMS_SCHEME_HASH();}
        else if(_inSchemeScheme==TPM_ALG_ID.NULL.toInt()) {inScheme = new TPMS_NULL_KDF_SCHEME();}
        if (inScheme == null) throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_inSchemeScheme).name());
        inScheme.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPM2_ECC_Decrypt_REQUEST fromTpm (byte[] x) 
    {
        TPM2_ECC_Decrypt_REQUEST ret = new TPM2_ECC_Decrypt_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_ECC_Decrypt_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_ECC_Decrypt_REQUEST ret = new TPM2_ECC_Decrypt_REQUEST();
        ret.initFromTpm(buf);
        return ret;
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
        _p.add(d, "byte", "C2", C2);
        _p.add(d, "byte", "C3", C3);
        _p.add(d, "TPMU_KDF_SCHEME", "inScheme", inScheme);
    }
}

//<<<


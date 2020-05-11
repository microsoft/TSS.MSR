package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command causes the TPM to sign an externally provided hash with the specified
 *  symmetric or asymmetric signing key.
 */
public class TPM2_Sign_REQUEST extends TpmStructure
{
    /**
     *  Handle of key that will perform signing
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE keyHandle;
    
    /** digest to be signed */
    public byte[] digest;
    public TPM_ALG_ID inSchemeScheme() { return inScheme != null ? inScheme.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** signing scheme to use if the scheme for keyHandle is TPM_ALG_NULL */
    public TPMU_SIG_SCHEME inScheme;
    
    /**
     *  proof that digest was created by the TPM
     *  If keyHandle is not a restricted signing key, then this may be a NULL Ticket
     *  with tag = TPM_ST_CHECKHASH.
     */
    public TPMT_TK_HASHCHECK validation;
    
    public TPM2_Sign_REQUEST() { keyHandle = new TPM_HANDLE(); }
    
    /**
     *  @param _keyHandle Handle of key that will perform signing
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _digest digest to be signed
     *  @param _inScheme signing scheme to use if the scheme for keyHandle is TPM_ALG_NULL
     *         (One of [TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
     *         TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC,
     *         TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME])
     *  @param _validation proof that digest was created by the TPM
     *         If keyHandle is not a restricted signing key, then this may be a NULL Ticket
     *         with tag = TPM_ST_CHECKHASH.
     */
    public TPM2_Sign_REQUEST(TPM_HANDLE _keyHandle, byte[] _digest, TPMU_SIG_SCHEME _inScheme, TPMT_TK_HASHCHECK _validation)
    {
        keyHandle = _keyHandle;
        digest = _digest;
        inScheme = _inScheme;
        validation = _validation;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        keyHandle.toTpm(buf);
        buf.writeSizedByteBuf(digest);
        inScheme.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)inScheme).toTpm(buf);
        validation.toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        keyHandle = TPM_HANDLE.fromTpm(buf);
        int _digestSize = buf.readShort() & 0xFFFF;
        digest = new byte[_digestSize];
        buf.readArrayOfInts(digest, 1, _digestSize);
        int _inSchemeScheme = buf.readShort() & 0xFFFF;
        inScheme = UnionFactory.create("TPMU_SIG_SCHEME", new TPM_ALG_ID(_inSchemeScheme));
        inScheme.initFromTpm(buf);
        validation = TPMT_TK_HASHCHECK.fromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }

    public static TPM2_Sign_REQUEST fromTpm (byte[] x) 
    {
        TPM2_Sign_REQUEST ret = new TPM2_Sign_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_Sign_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_Sign_REQUEST ret = new TPM2_Sign_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Sign_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
        _p.add(d, "byte", "digest", digest);
        _p.add(d, "TPMU_SIG_SCHEME", "inScheme", inScheme);
        _p.add(d, "TPMT_TK_HASHCHECK", "validation", validation);
    }
}

//<<<

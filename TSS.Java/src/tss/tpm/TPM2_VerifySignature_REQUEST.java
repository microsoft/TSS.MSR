package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command uses loaded keys to validate a signature on a message with the
 *  message digest passed to the TPM.
 */
public class TPM2_VerifySignature_REQUEST extends TpmStructure
{
    /**
     *  handle of public key that will be used in the validation
     *  Auth Index: None
     */
    public TPM_HANDLE keyHandle;
    
    /** digest of the signed message */
    public byte[] digest;
    
    /** selector of the algorithm used to construct the signature */
    public TPM_ALG_ID signatureSigAlg() { return signature != null ? signature.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** signature to be tested */
    public TPMU_SIGNATURE signature;
    
    public TPM2_VerifySignature_REQUEST() { keyHandle = new TPM_HANDLE(); }
    
    /**
     *  @param _keyHandle handle of public key that will be used in the validation
     *         Auth Index: None
     *  @param _digest digest of the signed message
     *  @param _signature signature to be tested
     *         (One of [TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA,
     *         TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TPMT_HA,
     *         TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE])
     */
    public TPM2_VerifySignature_REQUEST(TPM_HANDLE _keyHandle, byte[] _digest, TPMU_SIGNATURE _signature)
    {
        keyHandle = _keyHandle;
        digest = _digest;
        signature = _signature;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(digest);
        signature.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)signature).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _digestSize = buf.readShort() & 0xFFFF;
        digest = new byte[_digestSize];
        buf.readArrayOfInts(digest, 1, _digestSize);
        int _signatureSigAlg = buf.readShort() & 0xFFFF;
        signature = UnionFactory.create("TPMU_SIGNATURE", new TPM_ALG_ID(_signatureSigAlg));
        signature.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static TPM2_VerifySignature_REQUEST fromTpm (byte[] x) 
    {
        TPM2_VerifySignature_REQUEST ret = new TPM2_VerifySignature_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2_VerifySignature_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_VerifySignature_REQUEST ret = new TPM2_VerifySignature_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_VerifySignature_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
        _p.add(d, "byte", "digest", digest);
        _p.add(d, "TPMU_SIGNATURE", "signature", signature);
    }
}

//<<<

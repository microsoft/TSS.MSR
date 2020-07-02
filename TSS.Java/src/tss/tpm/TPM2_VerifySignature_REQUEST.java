package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command uses loaded keys to validate a signature on a message with the message
 *  digest passed to the TPM.
 */
public class TPM2_VerifySignature_REQUEST extends ReqStructure
{
    /** Handle of public key that will be used in the validation
     *  Auth Index: None
     */
    public TPM_HANDLE keyHandle;
    
    /** Digest of the signed message  */
    public byte[] digest;
    
    /** Selector of the algorithm used to construct the signature  */
    public TPM_ALG_ID signatureSigAlg() { return signature != null ? signature.GetUnionSelector() : TPM_ALG_ID.NULL; }
    
    /** Signature to be tested  */
    public TPMU_SIGNATURE signature;
    
    public TPM2_VerifySignature_REQUEST() { keyHandle = new TPM_HANDLE(); }
    
    /** @param _keyHandle Handle of public key that will be used in the validation
     *         Auth Index: None
     *  @param _digest Digest of the signed message
     *  @param _signature Signature to be tested
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
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(digest);
        buf.writeShort(signature.GetUnionSelector());
        signature.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        digest = buf.readSizedByteBuf();
        TPM_ALG_ID signatureSigAlg = TPM_ALG_ID.fromTpm(buf);
        signature = UnionFactory.create("TPMU_SIGNATURE", signatureSigAlg);
        signature.initFromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_VerifySignature_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_VerifySignature_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_VerifySignature_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_VerifySignature_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_VerifySignature_REQUEST.class);
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

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command uses loaded keys to validate a signature on a message with the message digest passed to the TPM.
*/
public class TPM2_VerifySignature_REQUEST extends TpmStructure
{
    /**
     * This command uses loaded keys to validate a signature on a message with the message digest passed to the TPM.
     * 
     * @param _keyHandle handle of public key that will be used in the validation Auth Index: None 
     * @param _digest digest of the signed message 
     * @param _signature signature to be tested (One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)
     */
    public TPM2_VerifySignature_REQUEST(TPM_HANDLE _keyHandle,byte[] _digest,TPMU_SIGNATURE _signature)
    {
        keyHandle = _keyHandle;
        digest = _digest;
        signature = _signature;
    }
    /**
    * This command uses loaded keys to validate a signature on a message with the message digest passed to the TPM.
    */
    public TPM2_VerifySignature_REQUEST() {};
    /**
    * handle of public key that will be used in the validation Auth Index: None
    */
    public TPM_HANDLE keyHandle;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short digestSize;
    /**
    * digest of the signed message
    */
    public byte[] digest;
    /**
    * selector of the algorithm used to construct the signature
    */
    // private TPM_ALG_ID signatureSigAlg;
    /**
    * signature to be tested
    */
    public TPMU_SIGNATURE signature;
    public int GetUnionSelector_signature()
    {
        if(signature instanceof TPMS_SIGNATURE_RSASSA){return 0x0014; }
        if(signature instanceof TPMS_SIGNATURE_RSAPSS){return 0x0016; }
        if(signature instanceof TPMS_SIGNATURE_ECDSA){return 0x0018; }
        if(signature instanceof TPMS_SIGNATURE_ECDAA){return 0x001A; }
        if(signature instanceof TPMS_SIGNATURE_SM2){return 0x001B; }
        if(signature instanceof TPMS_SIGNATURE_ECSCHNORR){return 0x001C; }
        if(signature instanceof TPMT_HA){return 0x0005; }
        if(signature instanceof TPMS_SCHEME_HASH){return 0x7FFF; }
        if(signature instanceof TPMS_NULL_SIGNATURE){return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        keyHandle.toTpm(buf);
        buf.writeInt((digest!=null)?digest.length:0, 2);
        if(digest!=null)
            buf.write(digest);
        buf.writeInt(GetUnionSelector_signature(), 2);
        ((TpmMarshaller)signature).toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        keyHandle = TPM_HANDLE.fromTpm(buf);
        int _digestSize = buf.readInt(2);
        digest = new byte[_digestSize];
        buf.readArrayOfInts(digest, 1, _digestSize);
        int _signatureSigAlg = buf.readInt(2);
        signature=null;
        if(_signatureSigAlg==TPM_ALG_ID.RSASSA.toInt()) {signature = new TPMS_SIGNATURE_RSASSA();}
        else if(_signatureSigAlg==TPM_ALG_ID.RSAPSS.toInt()) {signature = new TPMS_SIGNATURE_RSAPSS();}
        else if(_signatureSigAlg==TPM_ALG_ID.ECDSA.toInt()) {signature = new TPMS_SIGNATURE_ECDSA();}
        else if(_signatureSigAlg==TPM_ALG_ID.ECDAA.toInt()) {signature = new TPMS_SIGNATURE_ECDAA();}
        // code generator workaround BUGBUG >> (probChild)else if(_signatureSigAlg==TPM_ALG_ID.SM2.toInt()) {signature = new TPMS_SIGNATURE_SM2();}
        // code generator workaround BUGBUG >> (probChild)else if(_signatureSigAlg==TPM_ALG_ID.ECSCHNORR.toInt()) {signature = new TPMS_SIGNATURE_ECSCHNORR();}
        // code generator workaround BUGBUG >> (probChild)else if(_signatureSigAlg==TPM_ALG_ID.HMAC.toInt()) {signature = new TPMT_HA();}
        else if(_signatureSigAlg==TPM_ALG_ID.ANY.toInt()) {signature = new TPMS_SCHEME_HASH();}
        else if(_signatureSigAlg==TPM_ALG_ID.NULL.toInt()) {signature = new TPMS_NULL_SIGNATURE();}
        if(signature==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_signatureSigAlg).name());
        signature.initFromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
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
    };
    
    
};

//<<<


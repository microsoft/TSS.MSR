package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Table 180 shows the basic algorithm-agile structure when a symmetric or asymmetric signature is indicated. The sigAlg parameter indicates the algorithm used for the signature. This structure is output from commands such as the attestation commands and TPM2_Sign, and is an input to commands such as TPM2_VerifySignature(), TPM2_PolicySigned(), and TPM2_FieldUpgradeStart().
*/
public class TPMT_SIGNATURE extends TpmStructure
{
    /**
     * Table 180 shows the basic algorithm-agile structure when a symmetric or asymmetric signature is indicated. The sigAlg parameter indicates the algorithm used for the signature. This structure is output from commands such as the attestation commands and TPM2_Sign, and is an input to commands such as TPM2_VerifySignature(), TPM2_PolicySigned(), and TPM2_FieldUpgradeStart().
     * 
     * @param _signature This shall be the actual signature information. (One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)
     */
    public TPMT_SIGNATURE(TPMU_SIGNATURE _signature)
    {
        signature = _signature;
    }
    /**
    * Table 180 shows the basic algorithm-agile structure when a symmetric or asymmetric signature is indicated. The sigAlg parameter indicates the algorithm used for the signature. This structure is output from commands such as the attestation commands and TPM2_Sign, and is an input to commands such as TPM2_VerifySignature(), TPM2_PolicySigned(), and TPM2_FieldUpgradeStart().
    */
    public TPMT_SIGNATURE() {};
    /**
    * selector of the algorithm used to construct the signature
    */
    // private TPM_ALG_ID sigAlg;
    /**
    * This shall be the actual signature information.
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
        buf.writeInt(GetUnionSelector_signature(), 2);
        ((TpmMarshaller)signature).toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _sigAlg = buf.readInt(2);
        signature=null;
        if(_sigAlg==TPM_ALG_ID.RSASSA.toInt()) {signature = new TPMS_SIGNATURE_RSASSA();}
        else if(_sigAlg==TPM_ALG_ID.RSAPSS.toInt()) {signature = new TPMS_SIGNATURE_RSAPSS();}
        else if(_sigAlg==TPM_ALG_ID.ECDSA.toInt()) {signature = new TPMS_SIGNATURE_ECDSA();}
        else if(_sigAlg==TPM_ALG_ID.ECDAA.toInt()) {signature = new TPMS_SIGNATURE_ECDAA();}
        // code generator workaround BUGBUG >> (probChild)else if(_sigAlg==TPM_ALG_ID.SM2.toInt()) {signature = new TPMS_SIGNATURE_SM2();}
        // code generator workaround BUGBUG >> (probChild)else if(_sigAlg==TPM_ALG_ID.ECSCHNORR.toInt()) {signature = new TPMS_SIGNATURE_ECSCHNORR();}
        // code generator workaround BUGBUG >> (probChild)else if(_sigAlg==TPM_ALG_ID.HMAC.toInt()) {signature = new TPMT_HA();}
        else if(_sigAlg==TPM_ALG_ID.ANY.toInt()) {signature = new TPMS_SCHEME_HASH();}
        else if(_sigAlg==TPM_ALG_ID.NULL.toInt()) {signature = new TPMS_NULL_SIGNATURE();}
        if(signature==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_sigAlg).name());
        signature.initFromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMT_SIGNATURE fromTpm (byte[] x) 
    {
        TPMT_SIGNATURE ret = new TPMT_SIGNATURE();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMT_SIGNATURE fromTpm (InByteBuf buf) 
    {
        TPMT_SIGNATURE ret = new TPMT_SIGNATURE();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_SIGNATURE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMU_SIGNATURE", "signature", signature);
    };
    
    
};

//<<<


package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command uses platformPolicy and a TPM Vendor Authorization Key to authorize a Field Upgrade Manifest.
*/
public class TPM2_FieldUpgradeStart_REQUEST extends TpmStructure
{
    /**
     * This command uses platformPolicy and a TPM Vendor Authorization Key to authorize a Field Upgrade Manifest.
     * 
     * @param _authorization TPM_RH_PLATFORM+{PP} Auth Index:1 Auth Role: ADMIN 
     * @param _keyHandle handle of a public area that contains the TPM Vendor Authorization Key that will be used to validate manifestSignature Auth Index: None 
     * @param _fuDigest digest of the first block in the field upgrade sequence 
     * @param _manifestSignature signature over fuDigest using the key associated with keyHandle (not optional) (One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)
     */
    public TPM2_FieldUpgradeStart_REQUEST(TPM_HANDLE _authorization,TPM_HANDLE _keyHandle,byte[] _fuDigest,TPMU_SIGNATURE _manifestSignature)
    {
        authorization = _authorization;
        keyHandle = _keyHandle;
        fuDigest = _fuDigest;
        manifestSignature = _manifestSignature;
    }
    /**
    * This command uses platformPolicy and a TPM Vendor Authorization Key to authorize a Field Upgrade Manifest.
    */
    public TPM2_FieldUpgradeStart_REQUEST() {};
    /**
    * TPM_RH_PLATFORM+{PP} Auth Index:1 Auth Role: ADMIN
    */
    public TPM_HANDLE authorization;
    /**
    * handle of a public area that contains the TPM Vendor Authorization Key that will be used to validate manifestSignature Auth Index: None
    */
    public TPM_HANDLE keyHandle;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short fuDigestSize;
    /**
    * digest of the first block in the field upgrade sequence
    */
    public byte[] fuDigest;
    /**
    * selector of the algorithm used to construct the signature
    */
    // private TPM_ALG_ID manifestSignatureSigAlg;
    /**
    * signature over fuDigest using the key associated with keyHandle (not optional)
    */
    public TPMU_SIGNATURE manifestSignature;
    public int GetUnionSelector_manifestSignature()
    {
        if(manifestSignature instanceof TPMS_SIGNATURE_RSASSA){return 0x0014; }
        if(manifestSignature instanceof TPMS_SIGNATURE_RSAPSS){return 0x0016; }
        if(manifestSignature instanceof TPMS_SIGNATURE_ECDSA){return 0x0018; }
        if(manifestSignature instanceof TPMS_SIGNATURE_ECDAA){return 0x001A; }
        if(manifestSignature instanceof TPMS_SIGNATURE_SM2){return 0x001B; }
        if(manifestSignature instanceof TPMS_SIGNATURE_ECSCHNORR){return 0x001C; }
        if(manifestSignature instanceof TPMT_HA){return 0x0005; }
        if(manifestSignature instanceof TPMS_SCHEME_HASH){return 0x7FFF; }
        if(manifestSignature instanceof TPMS_NULL_SIGNATURE){return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        authorization.toTpm(buf);
        keyHandle.toTpm(buf);
        buf.writeInt((fuDigest!=null)?fuDigest.length:0, 2);
        if(fuDigest!=null)
            buf.write(fuDigest);
        buf.writeInt(GetUnionSelector_manifestSignature(), 2);
        ((TpmMarshaller)manifestSignature).toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authorization = TPM_HANDLE.fromTpm(buf);
        keyHandle = TPM_HANDLE.fromTpm(buf);
        int _fuDigestSize = buf.readInt(2);
        fuDigest = new byte[_fuDigestSize];
        buf.readArrayOfInts(fuDigest, 1, _fuDigestSize);
        int _manifestSignatureSigAlg = buf.readInt(2);
        manifestSignature=null;
        if(_manifestSignatureSigAlg==TPM_ALG_ID.RSASSA.toInt()) {manifestSignature = new TPMS_SIGNATURE_RSASSA();}
        else if(_manifestSignatureSigAlg==TPM_ALG_ID.RSAPSS.toInt()) {manifestSignature = new TPMS_SIGNATURE_RSAPSS();}
        else if(_manifestSignatureSigAlg==TPM_ALG_ID.ECDSA.toInt()) {manifestSignature = new TPMS_SIGNATURE_ECDSA();}
        else if(_manifestSignatureSigAlg==TPM_ALG_ID.ECDAA.toInt()) {manifestSignature = new TPMS_SIGNATURE_ECDAA();}
        // code generator workaround BUGBUG >> (probChild)else if(_manifestSignatureSigAlg==TPM_ALG_ID.SM2.toInt()) {manifestSignature = new TPMS_SIGNATURE_SM2();}
        // code generator workaround BUGBUG >> (probChild)else if(_manifestSignatureSigAlg==TPM_ALG_ID.ECSCHNORR.toInt()) {manifestSignature = new TPMS_SIGNATURE_ECSCHNORR();}
        // code generator workaround BUGBUG >> (probChild)else if(_manifestSignatureSigAlg==TPM_ALG_ID.HMAC.toInt()) {manifestSignature = new TPMT_HA();}
        else if(_manifestSignatureSigAlg==TPM_ALG_ID.ANY.toInt()) {manifestSignature = new TPMS_SCHEME_HASH();}
        else if(_manifestSignatureSigAlg==TPM_ALG_ID.NULL.toInt()) {manifestSignature = new TPMS_NULL_SIGNATURE();}
        if(manifestSignature==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_manifestSignatureSigAlg).name());
        manifestSignature.initFromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_FieldUpgradeStart_REQUEST fromTpm (byte[] x) 
    {
        TPM2_FieldUpgradeStart_REQUEST ret = new TPM2_FieldUpgradeStart_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_FieldUpgradeStart_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_FieldUpgradeStart_REQUEST ret = new TPM2_FieldUpgradeStart_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_FieldUpgradeStart_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authorization", authorization);
        _p.add(d, "TPM_HANDLE", "keyHandle", keyHandle);
        _p.add(d, "byte", "fuDigest", fuDigest);
        _p.add(d, "TPMU_SIGNATURE", "manifestSignature", manifestSignature);
    };
    
    
};

//<<<


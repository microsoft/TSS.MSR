package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command causes the TPM to sign an externally provided hash with the specified symmetric or asymmetric signing key.
*/
public class TPM2_Sign_REQUEST extends TpmStructure
{
    /**
     * This command causes the TPM to sign an externally provided hash with the specified symmetric or asymmetric signing key.
     * 
     * @param _keyHandle Handle of key that will perform signing Auth Index: 1 Auth Role: USER 
     * @param _digest digest to be signed 
     * @param _inScheme signing scheme to use if the scheme for keyHandle is TPM_ALG_NULL (One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME) 
     * @param _validation proof that digest was created by the TPM If keyHandle is not a restricted signing key, then this may be a NULL Ticket with tag = TPM_ST_CHECKHASH.
     */
    public TPM2_Sign_REQUEST(TPM_HANDLE _keyHandle,byte[] _digest,TPMU_SIG_SCHEME _inScheme,TPMT_TK_HASHCHECK _validation)
    {
        keyHandle = _keyHandle;
        digest = _digest;
        inScheme = _inScheme;
        validation = _validation;
    }
    /**
    * This command causes the TPM to sign an externally provided hash with the specified symmetric or asymmetric signing key.
    */
    public TPM2_Sign_REQUEST() {};
    /**
    * Handle of key that will perform signing Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE keyHandle;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short digestSize;
    /**
    * digest to be signed
    */
    public byte[] digest;
    /**
    * scheme selector
    */
    // private TPM_ALG_ID inSchemeScheme;
    /**
    * signing scheme to use if the scheme for keyHandle is TPM_ALG_NULL
    */
    public TPMU_SIG_SCHEME inScheme;
    /**
    * proof that digest was created by the TPM If keyHandle is not a restricted signing key, then this may be a NULL Ticket with tag = TPM_ST_CHECKHASH.
    */
    public TPMT_TK_HASHCHECK validation;
    public int GetUnionSelector_inScheme()
    {
        if(inScheme instanceof TPMS_SIG_SCHEME_RSASSA){return 0x0014; }
        if(inScheme instanceof TPMS_SIG_SCHEME_RSAPSS){return 0x0016; }
        if(inScheme instanceof TPMS_SIG_SCHEME_ECDSA){return 0x0018; }
        if(inScheme instanceof TPMS_SIG_SCHEME_ECDAA){return 0x001A; }
        if(inScheme instanceof TPMS_SIG_SCHEME_SM2){return 0x001B; }
        if(inScheme instanceof TPMS_SIG_SCHEME_ECSCHNORR){return 0x001C; }
        if(inScheme instanceof TPMS_SCHEME_HMAC){return 0x0005; }
        if(inScheme instanceof TPMS_SCHEME_HASH){return 0x7FFF; }
        if(inScheme instanceof TPMS_NULL_SIG_SCHEME){return 0x0010; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        keyHandle.toTpm(buf);
        buf.writeInt((digest!=null)?digest.length:0, 2);
        if(digest!=null)
            buf.write(digest);
        buf.writeInt(GetUnionSelector_inScheme(), 2);
        ((TpmMarshaller)inScheme).toTpm(buf);
        validation.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        keyHandle = TPM_HANDLE.fromTpm(buf);
        int _digestSize = buf.readInt(2);
        digest = new byte[_digestSize];
        buf.readArrayOfInts(digest, 1, _digestSize);
        int _inSchemeScheme = buf.readInt(2);
        inScheme=null;
        if(_inSchemeScheme==TPM_ALG_ID.RSASSA.toInt()) {inScheme = new TPMS_SIG_SCHEME_RSASSA();}
        else if(_inSchemeScheme==TPM_ALG_ID.RSAPSS.toInt()) {inScheme = new TPMS_SIG_SCHEME_RSAPSS();}
        else if(_inSchemeScheme==TPM_ALG_ID.ECDSA.toInt()) {inScheme = new TPMS_SIG_SCHEME_ECDSA();}
        else if(_inSchemeScheme==TPM_ALG_ID.ECDAA.toInt()) {inScheme = new TPMS_SIG_SCHEME_ECDAA();}
        // code generator workaround BUGBUG >> (probChild)else if(_inSchemeScheme==TPM_ALG_ID.SM2.toInt()) {inScheme = new TPMS_SIG_SCHEME_SM2();}
        // code generator workaround BUGBUG >> (probChild)else if(_inSchemeScheme==TPM_ALG_ID.ECSCHNORR.toInt()) {inScheme = new TPMS_SIG_SCHEME_ECSCHNORR();}
        else if(_inSchemeScheme==TPM_ALG_ID.HMAC.toInt()) {inScheme = new TPMS_SCHEME_HMAC();}
        else if(_inSchemeScheme==TPM_ALG_ID.ANY.toInt()) {inScheme = new TPMS_SCHEME_HASH();}
        else if(_inSchemeScheme==TPM_ALG_ID.NULL.toInt()) {inScheme = new TPMS_NULL_SIG_SCHEME();}
        if(inScheme==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_inSchemeScheme).name());
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
    };
    
    
};

//<<<


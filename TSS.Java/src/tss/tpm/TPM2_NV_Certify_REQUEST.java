package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* The purpose of this command is to certify the contents of an NV Index or portion of an NV Index.
*/
public class TPM2_NV_Certify_REQUEST extends TpmStructure
{
    /**
     * The purpose of this command is to certify the contents of an NV Index or portion of an NV Index.
     * 
     * @param _signHandle handle of the key used to sign the attestation structure Auth Index: 1 Auth Role: USER 
     * @param _authHandle handle indicating the source of the authorization value for the NV Index Auth Index: 2 Auth Role: USER 
     * @param _nvIndex Index for the area to be certified Auth Index: None 
     * @param _qualifyingData user-provided qualifying data 
     * @param _inScheme signing scheme to use if the scheme for signHandle is TPM_ALG_NULL (One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME) 
     * @param _size number of octets to certify 
     * @param _offset octet offset into the NV area This value shall be less than or equal to the size of the nvIndex data.
     */
    public TPM2_NV_Certify_REQUEST(TPM_HANDLE _signHandle,TPM_HANDLE _authHandle,TPM_HANDLE _nvIndex,byte[] _qualifyingData,TPMU_SIG_SCHEME _inScheme,int _size,int _offset)
    {
        signHandle = _signHandle;
        authHandle = _authHandle;
        nvIndex = _nvIndex;
        qualifyingData = _qualifyingData;
        inScheme = _inScheme;
        size = (short)_size;
        offset = (short)_offset;
    }
    /**
    * The purpose of this command is to certify the contents of an NV Index or portion of an NV Index.
    */
    public TPM2_NV_Certify_REQUEST() {};
    /**
    * handle of the key used to sign the attestation structure Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE signHandle;
    /**
    * handle indicating the source of the authorization value for the NV Index Auth Index: 2 Auth Role: USER
    */
    public TPM_HANDLE authHandle;
    /**
    * Index for the area to be certified Auth Index: None
    */
    public TPM_HANDLE nvIndex;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short qualifyingDataSize;
    /**
    * user-provided qualifying data
    */
    public byte[] qualifyingData;
    /**
    * scheme selector
    */
    // private TPM_ALG_ID inSchemeScheme;
    /**
    * signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
    */
    public TPMU_SIG_SCHEME inScheme;
    /**
    * number of octets to certify
    */
    public short size;
    /**
    * octet offset into the NV area This value shall be less than or equal to the size of the nvIndex data.
    */
    public short offset;
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
        signHandle.toTpm(buf);
        authHandle.toTpm(buf);
        nvIndex.toTpm(buf);
        buf.writeInt((qualifyingData!=null)?qualifyingData.length:0, 2);
        if(qualifyingData!=null)
            buf.write(qualifyingData);
        buf.writeInt(GetUnionSelector_inScheme(), 2);
        ((TpmMarshaller)inScheme).toTpm(buf);
        buf.write(size);
        buf.write(offset);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        signHandle = TPM_HANDLE.fromTpm(buf);
        authHandle = TPM_HANDLE.fromTpm(buf);
        nvIndex = TPM_HANDLE.fromTpm(buf);
        int _qualifyingDataSize = buf.readInt(2);
        qualifyingData = new byte[_qualifyingDataSize];
        buf.readArrayOfInts(qualifyingData, 1, _qualifyingDataSize);
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
        size = (short) buf.readInt(2);
        offset = (short) buf.readInt(2);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_NV_Certify_REQUEST fromTpm (byte[] x) 
    {
        TPM2_NV_Certify_REQUEST ret = new TPM2_NV_Certify_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_NV_Certify_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_NV_Certify_REQUEST ret = new TPM2_NV_Certify_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_Certify_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "signHandle", signHandle);
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
        _p.add(d, "byte", "qualifyingData", qualifyingData);
        _p.add(d, "TPMU_SIG_SCHEME", "inScheme", inScheme);
        _p.add(d, "ushort", "size", size);
        _p.add(d, "ushort", "offset", offset);
    };
    
    
};

//<<<


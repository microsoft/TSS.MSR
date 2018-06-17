package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command is used to prove the association between an object and its creation data. The TPM will validate that the ticket was produced by the TPM and that the ticket validates the association between a loaded public area and the provided hash of the creation data (creationHash).
*/
public class TPM2_CertifyCreation_REQUEST extends TpmStructure
{
    /**
     * This command is used to prove the association between an object and its creation data. The TPM will validate that the ticket was produced by the TPM and that the ticket validates the association between a loaded public area and the provided hash of the creation data (creationHash).
     * 
     * @param _signHandle handle of the key that will sign the attestation block Auth Index: 1 Auth Role: USER 
     * @param _objectHandle the object associated with the creation data Auth Index: None 
     * @param _qualifyingData user-provided qualifying data 
     * @param _creationHash hash of the creation data produced by TPM2_Create() or TPM2_CreatePrimary() 
     * @param _inScheme signing scheme to use if the scheme for signHandle is TPM_ALG_NULL (One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME) 
     * @param _creationTicket ticket produced by TPM2_Create() or TPM2_CreatePrimary()
     */
    public TPM2_CertifyCreation_REQUEST(TPM_HANDLE _signHandle,TPM_HANDLE _objectHandle,byte[] _qualifyingData,byte[] _creationHash,TPMU_SIG_SCHEME _inScheme,TPMT_TK_CREATION _creationTicket)
    {
        signHandle = _signHandle;
        objectHandle = _objectHandle;
        qualifyingData = _qualifyingData;
        creationHash = _creationHash;
        inScheme = _inScheme;
        creationTicket = _creationTicket;
    }
    /**
    * This command is used to prove the association between an object and its creation data. The TPM will validate that the ticket was produced by the TPM and that the ticket validates the association between a loaded public area and the provided hash of the creation data (creationHash).
    */
    public TPM2_CertifyCreation_REQUEST() {};
    /**
    * handle of the key that will sign the attestation block Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE signHandle;
    /**
    * the object associated with the creation data Auth Index: None
    */
    public TPM_HANDLE objectHandle;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short qualifyingDataSize;
    /**
    * user-provided qualifying data
    */
    public byte[] qualifyingData;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short creationHashSize;
    /**
    * hash of the creation data produced by TPM2_Create() or TPM2_CreatePrimary()
    */
    public byte[] creationHash;
    /**
    * scheme selector
    */
    // private TPM_ALG_ID inSchemeScheme;
    /**
    * signing scheme to use if the scheme for signHandle is TPM_ALG_NULL
    */
    public TPMU_SIG_SCHEME inScheme;
    /**
    * ticket produced by TPM2_Create() or TPM2_CreatePrimary()
    */
    public TPMT_TK_CREATION creationTicket;
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
        objectHandle.toTpm(buf);
        buf.writeInt((qualifyingData!=null)?qualifyingData.length:0, 2);
        if(qualifyingData!=null)
            buf.write(qualifyingData);
        buf.writeInt((creationHash!=null)?creationHash.length:0, 2);
        if(creationHash!=null)
            buf.write(creationHash);
        buf.writeInt(GetUnionSelector_inScheme(), 2);
        ((TpmMarshaller)inScheme).toTpm(buf);
        creationTicket.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        signHandle = TPM_HANDLE.fromTpm(buf);
        objectHandle = TPM_HANDLE.fromTpm(buf);
        int _qualifyingDataSize = buf.readInt(2);
        qualifyingData = new byte[_qualifyingDataSize];
        buf.readArrayOfInts(qualifyingData, 1, _qualifyingDataSize);
        int _creationHashSize = buf.readInt(2);
        creationHash = new byte[_creationHashSize];
        buf.readArrayOfInts(creationHash, 1, _creationHashSize);
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
        creationTicket = TPMT_TK_CREATION.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_CertifyCreation_REQUEST fromTpm (byte[] x) 
    {
        TPM2_CertifyCreation_REQUEST ret = new TPM2_CertifyCreation_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_CertifyCreation_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_CertifyCreation_REQUEST ret = new TPM2_CertifyCreation_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_CertifyCreation_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "signHandle", signHandle);
        _p.add(d, "TPM_HANDLE", "objectHandle", objectHandle);
        _p.add(d, "byte", "qualifyingData", qualifyingData);
        _p.add(d, "byte", "creationHash", creationHash);
        _p.add(d, "TPMU_SIG_SCHEME", "inScheme", inScheme);
        _p.add(d, "TPMT_TK_CREATION", "creationTicket", creationTicket);
    };
    
    
};

//<<<


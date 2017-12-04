package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure provides information relating to the creation environment for the object. The creation data includes the parent Name, parent Qualified Name, and the digest of selected PCR. These values represent the environment in which the object was created. Creation data allows a relying party to determine if an object was created when some appropriate protections were present.
*/
public class TPMS_CREATION_DATA extends TpmStructure
{
    /**
     * This structure provides information relating to the creation environment for the object. The creation data includes the parent Name, parent Qualified Name, and the digest of selected PCR. These values represent the environment in which the object was created. Creation data allows a relying party to determine if an object was created when some appropriate protections were present.
     * 
     * @param _pcrSelect list indicating the PCR included in pcrDigest 
     * @param _pcrDigest digest of the selected PCR using nameAlg of the object for which this structure is being created pcrDigest.size shall be zero if the pcrSelect list is empty. 
     * @param _locality the locality at which the object was created 
     * @param _parentNameAlg nameAlg of the parent 
     * @param _parentName Name of the parent at time of creation The size will match digest size associated with parentNameAlg unless it is TPM_ALG_NULL, in which case the size will be 4 and parentName will be the hierarchy handle. 
     * @param _parentQualifiedName Qualified Name of the parent at the time of creation Size is the same as parentName. 
     * @param _outsideInfo association with additional information added by the key creator This will be the contents of the outsideInfo parameter in TPM2_Create() or TPM2_CreatePrimary().
     */
    public TPMS_CREATION_DATA(TPMS_PCR_SELECTION[] _pcrSelect,byte[] _pcrDigest,TPMA_LOCALITY _locality,TPM_ALG_ID _parentNameAlg,byte[] _parentName,byte[] _parentQualifiedName,byte[] _outsideInfo)
    {
        pcrSelect = _pcrSelect;
        pcrDigest = _pcrDigest;
        locality = _locality;
        parentNameAlg = _parentNameAlg;
        parentName = _parentName;
        parentQualifiedName = _parentQualifiedName;
        outsideInfo = _outsideInfo;
    }
    /**
    * This structure provides information relating to the creation environment for the object. The creation data includes the parent Name, parent Qualified Name, and the digest of selected PCR. These values represent the environment in which the object was created. Creation data allows a relying party to determine if an object was created when some appropriate protections were present.
    */
    public TPMS_CREATION_DATA() {};
    /**
    * number of selection structures A value of zero is allowed.
    */
    // private int pcrSelectCount;
    /**
    * list indicating the PCR included in pcrDigest
    */
    public TPMS_PCR_SELECTION[] pcrSelect;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short pcrDigestSize;
    /**
    * digest of the selected PCR using nameAlg of the object for which this structure is being created pcrDigest.size shall be zero if the pcrSelect list is empty.
    */
    public byte[] pcrDigest;
    /**
    * the locality at which the object was created
    */
    public TPMA_LOCALITY locality;
    /**
    * nameAlg of the parent
    */
    public TPM_ALG_ID parentNameAlg;
    /**
    * size of the Name structure
    */
    // private short parentNameSize;
    /**
    * Name of the parent at time of creation The size will match digest size associated with parentNameAlg unless it is TPM_ALG_NULL, in which case the size will be 4 and parentName will be the hierarchy handle.
    */
    public byte[] parentName;
    /**
    * size of the Name structure
    */
    // private short parentQualifiedNameSize;
    /**
    * Qualified Name of the parent at the time of creation Size is the same as parentName.
    */
    public byte[] parentQualifiedName;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short outsideInfoSize;
    /**
    * association with additional information added by the key creator This will be the contents of the outsideInfo parameter in TPM2_Create() or TPM2_CreatePrimary().
    */
    public byte[] outsideInfo;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((pcrSelect!=null)?pcrSelect.length:0, 4);
        if(pcrSelect!=null)
            buf.writeArrayOfTpmObjects(pcrSelect);
        buf.writeInt((pcrDigest!=null)?pcrDigest.length:0, 2);
        if(pcrDigest!=null)
            buf.write(pcrDigest);
        locality.toTpm(buf);
        parentNameAlg.toTpm(buf);
        buf.writeInt((parentName!=null)?parentName.length:0, 2);
        if(parentName!=null)
            buf.write(parentName);
        buf.writeInt((parentQualifiedName!=null)?parentQualifiedName.length:0, 2);
        if(parentQualifiedName!=null)
            buf.write(parentQualifiedName);
        buf.writeInt((outsideInfo!=null)?outsideInfo.length:0, 2);
        if(outsideInfo!=null)
            buf.write(outsideInfo);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _pcrSelectCount = buf.readInt(4);
        pcrSelect = new TPMS_PCR_SELECTION[_pcrSelectCount];
        for(int j=0;j<_pcrSelectCount;j++)pcrSelect[j]=new TPMS_PCR_SELECTION();
        buf.readArrayOfTpmObjects(pcrSelect, _pcrSelectCount);
        int _pcrDigestSize = buf.readInt(2);
        pcrDigest = new byte[_pcrDigestSize];
        buf.readArrayOfInts(pcrDigest, 1, _pcrDigestSize);
        int _locality = buf.readInt(1);
        locality = TPMA_LOCALITY.fromInt(_locality);
        parentNameAlg = TPM_ALG_ID.fromTpm(buf);
        int _parentNameSize = buf.readInt(2);
        parentName = new byte[_parentNameSize];
        buf.readArrayOfInts(parentName, 1, _parentNameSize);
        int _parentQualifiedNameSize = buf.readInt(2);
        parentQualifiedName = new byte[_parentQualifiedNameSize];
        buf.readArrayOfInts(parentQualifiedName, 1, _parentQualifiedNameSize);
        int _outsideInfoSize = buf.readInt(2);
        outsideInfo = new byte[_outsideInfoSize];
        buf.readArrayOfInts(outsideInfo, 1, _outsideInfoSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_CREATION_DATA fromTpm (byte[] x) 
    {
        TPMS_CREATION_DATA ret = new TPMS_CREATION_DATA();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_CREATION_DATA fromTpm (InByteBuf buf) 
    {
        TPMS_CREATION_DATA ret = new TPMS_CREATION_DATA();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_CREATION_DATA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_PCR_SELECTION", "pcrSelect", pcrSelect);
        _p.add(d, "byte", "pcrDigest", pcrDigest);
        _p.add(d, "TPMA_LOCALITY", "locality", locality);
        _p.add(d, "TPM_ALG_ID", "parentNameAlg", parentNameAlg);
        _p.add(d, "byte", "parentName", parentName);
        _p.add(d, "byte", "parentQualifiedName", parentQualifiedName);
        _p.add(d, "byte", "outsideInfo", outsideInfo);
    };
    
    
};

//<<<


package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This structure provides information relating to the creation environment for the object.
 *  The creation data includes the parent Name, parent Qualified Name, and the digest of
 *  selected PCR. These values represent the environment in which the object was created.
 *  Creation data allows a relying party to determine if an object was created when some
 *  appropriate protections were present.
 */
public class TPMS_CREATION_DATA extends TpmStructure
{
    /** list indicating the PCR included in pcrDigest */
    public TPMS_PCR_SELECTION[] pcrSelect;
    
    /**
     *  digest of the selected PCR using nameAlg of the object for which this structure is being
     *  created
     *  pcrDigest.size shall be zero if the pcrSelect list is empty.
     */
    public byte[] pcrDigest;
    
    /** the locality at which the object was created */
    public TPMA_LOCALITY locality;
    
    /** nameAlg of the parent */
    public TPM_ALG_ID parentNameAlg;
    
    /**
     *  Name of the parent at time of creation
     *  The size will match digest size associated with parentNameAlg unless it is TPM_ALG_NULL,
     *  in which case the size will be 4 and parentName will be the hierarchy handle.
     */
    public byte[] parentName;
    
    /**
     *  Qualified Name of the parent at the time of creation
     *  Size is the same as parentName.
     */
    public byte[] parentQualifiedName;
    
    /**
     *  association with additional information added by the key creator
     *  This will be the contents of the outsideInfo parameter in TPM2_Create()
     *  or TPM2_CreatePrimary().
     */
    public byte[] outsideInfo;
    
    public TPMS_CREATION_DATA() { parentNameAlg = TPM_ALG_ID.NULL; }
    
    /**
     *  @param _pcrSelect list indicating the PCR included in pcrDigest
     *  @param _pcrDigest digest of the selected PCR using nameAlg of the object for which this structure is being
     *         created
     *         pcrDigest.size shall be zero if the pcrSelect list is empty.
     *  @param _locality the locality at which the object was created
     *  @param _parentNameAlg nameAlg of the parent
     *  @param _parentName Name of the parent at time of creation
     *         The size will match digest size associated with parentNameAlg unless it is TPM_ALG_NULL,
     *         in which case the size will be 4 and parentName will be the hierarchy handle.
     *  @param _parentQualifiedName Qualified Name of the parent at the time of creation
     *         Size is the same as parentName.
     *  @param _outsideInfo association with additional information added by the key creator
     *         This will be the contents of the outsideInfo parameter in TPM2_Create()
     *         or TPM2_CreatePrimary().
     */
    public TPMS_CREATION_DATA(TPMS_PCR_SELECTION[] _pcrSelect, byte[] _pcrDigest, TPMA_LOCALITY _locality, TPM_ALG_ID _parentNameAlg, byte[] _parentName, byte[] _parentQualifiedName, byte[] _outsideInfo)
    {
        pcrSelect = _pcrSelect;
        pcrDigest = _pcrDigest;
        locality = _locality;
        parentNameAlg = _parentNameAlg;
        parentName = _parentName;
        parentQualifiedName = _parentQualifiedName;
        outsideInfo = _outsideInfo;
    }

    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeObjArr(pcrSelect);
        buf.writeSizedByteBuf(pcrDigest);
        locality.toTpm(buf);
        parentNameAlg.toTpm(buf);
        buf.writeSizedByteBuf(parentName);
        buf.writeSizedByteBuf(parentQualifiedName);
        buf.writeSizedByteBuf(outsideInfo);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _pcrSelectCount = buf.readInt();
        pcrSelect = new TPMS_PCR_SELECTION[_pcrSelectCount];
        for (int j=0; j < _pcrSelectCount; j++) pcrSelect[j] = new TPMS_PCR_SELECTION();
        buf.readArrayOfTpmObjects(pcrSelect, _pcrSelectCount);
        int _pcrDigestSize = buf.readShort() & 0xFFFF;
        pcrDigest = new byte[_pcrDigestSize];
        buf.readArrayOfInts(pcrDigest, 1, _pcrDigestSize);
        int _locality = buf.readByte();
        locality = TPMA_LOCALITY.fromInt(_locality);
        parentNameAlg = TPM_ALG_ID.fromTpm(buf);
        int _parentNameSize = buf.readShort() & 0xFFFF;
        parentName = new byte[_parentNameSize];
        buf.readArrayOfInts(parentName, 1, _parentNameSize);
        int _parentQualifiedNameSize = buf.readShort() & 0xFFFF;
        parentQualifiedName = new byte[_parentQualifiedNameSize];
        buf.readArrayOfInts(parentQualifiedName, 1, _parentQualifiedNameSize);
        int _outsideInfoSize = buf.readShort() & 0xFFFF;
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
    }
}

//<<<

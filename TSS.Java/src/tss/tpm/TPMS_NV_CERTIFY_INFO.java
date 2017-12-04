package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure contains the Name and contents of the selected NV Index that is certified by TPM2_NV_Certify().
*/
public class TPMS_NV_CERTIFY_INFO extends TpmStructure implements TPMU_ATTEST 
{
    /**
     * This structure contains the Name and contents of the selected NV Index that is certified by TPM2_NV_Certify().
     * 
     * @param _indexName Name of the NV Index 
     * @param _offset the offset parameter of TPM2_NV_Certify() 
     * @param _nvContents contents of the NV Index
     */
    public TPMS_NV_CERTIFY_INFO(byte[] _indexName,int _offset,byte[] _nvContents)
    {
        indexName = _indexName;
        offset = (short)_offset;
        nvContents = _nvContents;
    }
    /**
    * This structure contains the Name and contents of the selected NV Index that is certified by TPM2_NV_Certify().
    */
    public TPMS_NV_CERTIFY_INFO() {};
    /**
    * size of the Name structure
    */
    // private short indexNameSize;
    /**
    * Name of the NV Index
    */
    public byte[] indexName;
    /**
    * the offset parameter of TPM2_NV_Certify()
    */
    public short offset;
    /**
    * size of the buffer
    */
    // private short nvContentsSize;
    /**
    * contents of the NV Index
    */
    public byte[] nvContents;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((indexName!=null)?indexName.length:0, 2);
        if(indexName!=null)
            buf.write(indexName);
        buf.write(offset);
        buf.writeInt((nvContents!=null)?nvContents.length:0, 2);
        if(nvContents!=null)
            buf.write(nvContents);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _indexNameSize = buf.readInt(2);
        indexName = new byte[_indexNameSize];
        buf.readArrayOfInts(indexName, 1, _indexNameSize);
        offset = (short) buf.readInt(2);
        int _nvContentsSize = buf.readInt(2);
        nvContents = new byte[_nvContentsSize];
        buf.readArrayOfInts(nvContents, 1, _nvContentsSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_NV_CERTIFY_INFO fromTpm (byte[] x) 
    {
        TPMS_NV_CERTIFY_INFO ret = new TPMS_NV_CERTIFY_INFO();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_NV_CERTIFY_INFO fromTpm (InByteBuf buf) 
    {
        TPMS_NV_CERTIFY_INFO ret = new TPMS_NV_CERTIFY_INFO();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_NV_CERTIFY_INFO");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "indexName", indexName);
        _p.add(d, "ushort", "offset", offset);
        _p.add(d, "byte", "nvContents", nvContents);
    };
    
    
};

//<<<


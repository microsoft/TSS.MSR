package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure contains the Name and hash of the contents of the selected NV Index
 *  that is certified by TPM2_NV_Certify(). The data is hashed using hash of the signing scheme.
 */
public class TPMS_NV_DIGEST_CERTIFY_INFO extends TpmStructure implements TPMU_ATTEST
{
    /** Name of the NV Index  */
    public byte[] indexName;
    
    /** Hash of the contents of the index  */
    public byte[] nvDigest;
    
    public TPMS_NV_DIGEST_CERTIFY_INFO() {}
    
    /** @param _indexName Name of the NV Index
     *  @param _nvDigest Hash of the contents of the index
     */
    public TPMS_NV_DIGEST_CERTIFY_INFO(byte[] _indexName, byte[] _nvDigest)
    {
        indexName = _indexName;
        nvDigest = _nvDigest;
    }
    
    /** TpmUnion method  */
    public TPM_ST GetUnionSelector() { return TPM_ST.ATTEST_NV_DIGEST; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(indexName);
        buf.writeSizedByteBuf(nvDigest);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        indexName = buf.readSizedByteBuf();
        nvDigest = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_NV_DIGEST_CERTIFY_INFO fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_NV_DIGEST_CERTIFY_INFO.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_NV_DIGEST_CERTIFY_INFO fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_NV_DIGEST_CERTIFY_INFO fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_NV_DIGEST_CERTIFY_INFO.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_NV_DIGEST_CERTIFY_INFO");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "indexName", indexName);
        _p.add(d, "byte", "nvDigest", nvDigest);
    }
}

//<<<

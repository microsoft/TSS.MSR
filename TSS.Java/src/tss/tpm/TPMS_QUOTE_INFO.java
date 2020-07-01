package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This is the attested data for TPM2_Quote().  */
public class TPMS_QUOTE_INFO extends TpmStructure implements TPMU_ATTEST
{
    /** Information on algID, PCR selected and digest  */
    public TPMS_PCR_SELECTION[] pcrSelect;
    
    /** Digest of the selected PCR using the hash of the signing key  */
    public byte[] pcrDigest;
    
    public TPMS_QUOTE_INFO() {}
    
    /** @param _pcrSelect Information on algID, PCR selected and digest
     *  @param _pcrDigest Digest of the selected PCR using the hash of the signing key
     */
    public TPMS_QUOTE_INFO(TPMS_PCR_SELECTION[] _pcrSelect, byte[] _pcrDigest)
    {
        pcrSelect = _pcrSelect;
        pcrDigest = _pcrDigest;
    }
    
    /** TpmUnion method  */
    public TPM_ST GetUnionSelector() { return TPM_ST.ATTEST_QUOTE; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeObjArr(pcrSelect);
        buf.writeSizedByteBuf(pcrDigest);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        pcrSelect = buf.readObjArr(TPMS_PCR_SELECTION.class);
        pcrDigest = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_QUOTE_INFO fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_QUOTE_INFO.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_QUOTE_INFO fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_QUOTE_INFO fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_QUOTE_INFO.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_QUOTE_INFO");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_PCR_SELECTION", "pcrSelect", pcrSelect);
        _p.add(d, "byte", "pcrDigest", pcrDigest);
    }
}

//<<<

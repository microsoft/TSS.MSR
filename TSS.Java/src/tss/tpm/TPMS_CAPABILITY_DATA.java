package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This data area is returned in response to a TPM2_GetCapability().  */
public class TPMS_CAPABILITY_DATA extends TpmStructure
{
    /** The capability  */
    public TPM_CAP capability() { return data.GetUnionSelector(); }
    
    /** The capability data  */
    public TPMU_CAPABILITIES data;
    
    public TPMS_CAPABILITY_DATA() {}
    
    /** @param _data The capability data
     *         (One of [TPML_ALG_PROPERTY, TPML_HANDLE, TPML_CCA, TPML_CC, TPML_PCR_SELECTION,
     *         TPML_TAGGED_TPM_PROPERTY, TPML_TAGGED_PCR_PROPERTY, TPML_ECC_CURVE,
     *         TPML_TAGGED_POLICY, TPML_ACT_DATA])
     */
    public TPMS_CAPABILITY_DATA(TPMU_CAPABILITIES _data) { data = _data; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        if (data == null) return;
        buf.writeInt(data.GetUnionSelector());
        data.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        TPM_CAP capability = TPM_CAP.fromTpm(buf);
        data = UnionFactory.create("TPMU_CAPABILITIES", capability);
        data.initFromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_CAPABILITY_DATA fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_CAPABILITY_DATA.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_CAPABILITY_DATA fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_CAPABILITY_DATA fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_CAPABILITY_DATA.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_CAPABILITY_DATA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMU_CAPABILITIES", "data", data);
    }
}

//<<<

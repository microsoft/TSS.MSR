package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns various information regarding the TPM and its current state.  */
public class GetCapabilityResponse extends RespStructure
{
    /** Flag to indicate if there are more values of this type  */
    public byte moreData;

    /** The capability  */
    public TPM_CAP capabilityDataCapability() { return capabilityData.GetUnionSelector(); }

    /** The capability data
     *  One of: TPML_ALG_PROPERTY, TPML_HANDLE, TPML_CCA, TPML_CC, TPML_PCR_SELECTION,
     *  TPML_TAGGED_TPM_PROPERTY, TPML_TAGGED_PCR_PROPERTY, TPML_ECC_CURVE,
     *  TPML_TAGGED_POLICY, TPML_ACT_DATA.
     */
    public TPMU_CAPABILITIES capabilityData;

    public GetCapabilityResponse() {}

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeByte(moreData);
        buf.writeInt(capabilityData.GetUnionSelector());
        capabilityData.toTpm(buf);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        moreData = buf.readByte();
        TPM_CAP capabilityDataCapability = TPM_CAP.fromTpm(buf);
        capabilityData = UnionFactory.create("TPMU_CAPABILITIES", capabilityDataCapability);
        capabilityData.initFromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static GetCapabilityResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(GetCapabilityResponse.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static GetCapabilityResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static GetCapabilityResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(GetCapabilityResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("GetCapabilityResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "moreData", moreData);
        _p.add(d, "TPMU_CAPABILITIES", "capabilityData", capabilityData);
    }
}

//<<<

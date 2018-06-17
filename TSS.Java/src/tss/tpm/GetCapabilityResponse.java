package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command returns various information regarding the TPM and its current state.
*/
public class GetCapabilityResponse extends TpmStructure
{
    /**
     * This command returns various information regarding the TPM and its current state.
     * 
     * @param _moreData flag to indicate if there are more values of this type 
     * @param _capabilityData the capability data (One of TPML_ALG_PROPERTY, TPML_HANDLE, TPML_CCA, TPML_CC, TPML_CC, TPML_PCR_SELECTION, TPML_TAGGED_TPM_PROPERTY, TPML_TAGGED_PCR_PROPERTY, TPML_ECC_CURVE, TPML_TAGGED_POLICY)
     */
    public GetCapabilityResponse(byte _moreData,TPMU_CAPABILITIES _capabilityData)
    {
        moreData = _moreData;
        capabilityData = _capabilityData;
    }
    /**
    * This command returns various information regarding the TPM and its current state.
    */
    public GetCapabilityResponse() {};
    /**
    * flag to indicate if there are more values of this type
    */
    public byte moreData;
    /**
    * the capability
    */
    // private TPM_CAP capabilityDataCapability;
    /**
    * the capability data
    */
    public TPMU_CAPABILITIES capabilityData;
    public int GetUnionSelector_capabilityData()
    {
        if(capabilityData instanceof TPML_ALG_PROPERTY){return 0x00000000; }
        if(capabilityData instanceof TPML_HANDLE){return 0x00000001; }
        if(capabilityData instanceof TPML_CCA){return 0x00000002; }
        if(capabilityData instanceof TPML_CC){return 0x00000003; }
        if(capabilityData instanceof TPML_CC){return 0x00000004; }
        if(capabilityData instanceof TPML_PCR_SELECTION){return 0x00000005; }
        if(capabilityData instanceof TPML_TAGGED_TPM_PROPERTY){return 0x00000006; }
        if(capabilityData instanceof TPML_TAGGED_PCR_PROPERTY){return 0x00000007; }
        if(capabilityData instanceof TPML_ECC_CURVE){return 0x00000008; }
        if(capabilityData instanceof TPML_TAGGED_POLICY){return 0x00000009; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.write(moreData);
        buf.writeInt(GetUnionSelector_capabilityData(), 4);
        ((TpmMarshaller)capabilityData).toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        moreData = (byte) buf.readInt(1);
        int _capabilityDataCapability = buf.readInt(4);
        capabilityData=null;
        if(_capabilityDataCapability==TPM_CAP.ALGS.toInt()) {capabilityData = new TPML_ALG_PROPERTY();}
        else if(_capabilityDataCapability==TPM_CAP.HANDLES.toInt()) {capabilityData = new TPML_HANDLE();}
        else if(_capabilityDataCapability==TPM_CAP.COMMANDS.toInt()) {capabilityData = new TPML_CCA();}
        else if(_capabilityDataCapability==TPM_CAP.PP_COMMANDS.toInt()) {capabilityData = new TPML_CC();}
        else if(_capabilityDataCapability==TPM_CAP.AUDIT_COMMANDS.toInt()) {capabilityData = new TPML_CC();}
        else if(_capabilityDataCapability==TPM_CAP.PCRS.toInt()) {capabilityData = new TPML_PCR_SELECTION();}
        else if(_capabilityDataCapability==TPM_CAP.TPM_PROPERTIES.toInt()) {capabilityData = new TPML_TAGGED_TPM_PROPERTY();}
        else if(_capabilityDataCapability==TPM_CAP.PCR_PROPERTIES.toInt()) {capabilityData = new TPML_TAGGED_PCR_PROPERTY();}
        else if(_capabilityDataCapability==TPM_CAP.ECC_CURVES.toInt()) {capabilityData = new TPML_ECC_CURVE();}
        else if(_capabilityDataCapability==TPM_CAP.AUTH_POLICIES.toInt()) {capabilityData = new TPML_TAGGED_POLICY();}
        if(capabilityData==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_capabilityDataCapability).name());
        capabilityData.initFromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static GetCapabilityResponse fromTpm (byte[] x) 
    {
        GetCapabilityResponse ret = new GetCapabilityResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static GetCapabilityResponse fromTpm (InByteBuf buf) 
    {
        GetCapabilityResponse ret = new GetCapabilityResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_GetCapability_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "BYTE", "moreData", moreData);
        _p.add(d, "TPMU_CAPABILITIES", "capabilityData", capabilityData);
    };
    
    
};

//<<<


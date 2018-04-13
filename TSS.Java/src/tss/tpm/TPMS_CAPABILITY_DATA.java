package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This data area is returned in response to a TPM2_GetCapability().
*/
public class TPMS_CAPABILITY_DATA extends TpmStructure
{
    /**
     * This data area is returned in response to a TPM2_GetCapability().
     * 
     * @param _data the capability data (One of TPML_ALG_PROPERTY, TPML_HANDLE, TPML_CCA, TPML_CC, TPML_CC, TPML_PCR_SELECTION, TPML_TAGGED_TPM_PROPERTY, TPML_TAGGED_PCR_PROPERTY, TPML_ECC_CURVE, TPML_TAGGED_POLICY)
     */
    public TPMS_CAPABILITY_DATA(TPMU_CAPABILITIES _data)
    {
        data = _data;
    }
    /**
    * This data area is returned in response to a TPM2_GetCapability().
    */
    public TPMS_CAPABILITY_DATA() {};
    /**
    * the capability
    */
    // private TPM_CAP capability;
    /**
    * the capability data
    */
    public TPMU_CAPABILITIES data;
    public int GetUnionSelector_data()
    {
        if(data instanceof TPML_ALG_PROPERTY){return 0x00000000; }
        if(data instanceof TPML_HANDLE){return 0x00000001; }
        if(data instanceof TPML_CCA){return 0x00000002; }
        if(data instanceof TPML_CC){return 0x00000003; }
        if(data instanceof TPML_CC){return 0x00000004; }
        if(data instanceof TPML_PCR_SELECTION){return 0x00000005; }
        if(data instanceof TPML_TAGGED_TPM_PROPERTY){return 0x00000006; }
        if(data instanceof TPML_TAGGED_PCR_PROPERTY){return 0x00000007; }
        if(data instanceof TPML_ECC_CURVE){return 0x00000008; }
        if(data instanceof TPML_TAGGED_POLICY){return 0x00000009; }
        throw new RuntimeException("Unrecognized type");
    }
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt(GetUnionSelector_data(), 4);
        ((TpmMarshaller)data).toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _capability = buf.readInt(4);
        data=null;
        if(_capability==TPM_CAP.ALGS.toInt()) {data = new TPML_ALG_PROPERTY();}
        else if(_capability==TPM_CAP.HANDLES.toInt()) {data = new TPML_HANDLE();}
        else if(_capability==TPM_CAP.COMMANDS.toInt()) {data = new TPML_CCA();}
        else if(_capability==TPM_CAP.PP_COMMANDS.toInt()) {data = new TPML_CC();}
        else if(_capability==TPM_CAP.AUDIT_COMMANDS.toInt()) {data = new TPML_CC();}
        else if(_capability==TPM_CAP.PCRS.toInt()) {data = new TPML_PCR_SELECTION();}
        else if(_capability==TPM_CAP.TPM_PROPERTIES.toInt()) {data = new TPML_TAGGED_TPM_PROPERTY();}
        else if(_capability==TPM_CAP.PCR_PROPERTIES.toInt()) {data = new TPML_TAGGED_PCR_PROPERTY();}
        else if(_capability==TPM_CAP.ECC_CURVES.toInt()) {data = new TPML_ECC_CURVE();}
        else if(_capability==TPM_CAP.AUTH_POLICIES.toInt()) {data = new TPML_TAGGED_POLICY();}
        if(data==null)throw new RuntimeException("Unexpected type selector " + TPM_ALG_ID.fromInt(_capability).name());
        data.initFromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_CAPABILITY_DATA fromTpm (byte[] x) 
    {
        TPMS_CAPABILITY_DATA ret = new TPMS_CAPABILITY_DATA();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_CAPABILITY_DATA fromTpm (InByteBuf buf) 
    {
        TPMS_CAPABILITY_DATA ret = new TPMS_CAPABILITY_DATA();
        ret.initFromTpm(buf);
        return ret;
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
    };
    
    
};

//<<<


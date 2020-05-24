package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This data area is returned in response to a TPM2_GetCapability(). */
public class TPMS_CAPABILITY_DATA extends TpmStructure
{
    /** the capability */
    public TPM_CAP capability() { return data.GetUnionSelector(); }
    
    /** the capability data */
    public TPMU_CAPABILITIES data;
    
    public TPMS_CAPABILITY_DATA() {}
    
    /**
     *  @param _data the capability data
     *         (One of [TPML_ALG_PROPERTY, TPML_HANDLE, TPML_CCA, TPML_CC, TPML_PCR_SELECTION,
     *         TPML_TAGGED_TPM_PROPERTY, TPML_TAGGED_PCR_PROPERTY, TPML_ECC_CURVE,
     *         TPML_TAGGED_POLICY, TPML_ACT_DATA])
     */
    public TPMS_CAPABILITY_DATA(TPMU_CAPABILITIES _data) { data = _data; }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        if (data == null) return;
        data.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)data).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _capability = buf.readInt();
        data = UnionFactory.create("TPMU_CAPABILITIES", new TPM_CAP(_capability));
        data.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
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
    }
}

//<<<

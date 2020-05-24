package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns various information regarding the TPM and its current state. */
public class GetCapabilityResponse extends TpmStructure
{
    /** flag to indicate if there are more values of this type */
    public byte moreData;
    
    /** the capability */
    public TPM_CAP capabilityDataCapability() { return capabilityData.GetUnionSelector(); }
    
    /** the capability data */
    public TPMU_CAPABILITIES capabilityData;
    
    public GetCapabilityResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeByte(moreData);
        capabilityData.GetUnionSelector().toTpm(buf);
        ((TpmMarshaller)capabilityData).toTpm(buf);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        moreData = buf.readByte();
        int _capabilityDataCapability = buf.readInt();
        capabilityData = UnionFactory.create("TPMU_CAPABILITIES", new TPM_CAP(_capabilityDataCapability));
        capabilityData.initFromTpm(buf);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
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
        _p.add(d, "byte", "moreData", moreData);
        _p.add(d, "TPMU_CAPABILITIES", "capabilityData", capabilityData);
    }
}

//<<<

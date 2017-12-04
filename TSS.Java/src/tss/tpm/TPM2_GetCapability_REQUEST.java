package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command returns various information regarding the TPM and its current state.
*/
public class TPM2_GetCapability_REQUEST extends TpmStructure
{
    /**
     * This command returns various information regarding the TPM and its current state.
     * 
     * @param _capability group selection; determines the format of the response 
     * @param _property further definition of information 
     * @param _propertyCount number of properties of the indicated type to return
     */
    public TPM2_GetCapability_REQUEST(TPM_CAP _capability,int _property,int _propertyCount)
    {
        capability = _capability;
        property = _property;
        propertyCount = _propertyCount;
    }
    /**
    * This command returns various information regarding the TPM and its current state.
    */
    public TPM2_GetCapability_REQUEST() {};
    /**
    * group selection; determines the format of the response
    */
    public TPM_CAP capability;
    /**
    * further definition of information
    */
    public int property;
    /**
    * number of properties of the indicated type to return
    */
    public int propertyCount;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        capability.toTpm(buf);
        buf.write(property);
        buf.write(propertyCount);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        capability = TPM_CAP.fromTpm(buf);
        property =  buf.readInt(4);
        propertyCount =  buf.readInt(4);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_GetCapability_REQUEST fromTpm (byte[] x) 
    {
        TPM2_GetCapability_REQUEST ret = new TPM2_GetCapability_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_GetCapability_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_GetCapability_REQUEST ret = new TPM2_GetCapability_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_GetCapability_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_CAP", "capability", capability);
        _p.add(d, "uint", "property", property);
        _p.add(d, "uint", "propertyCount", propertyCount);
    };
    
    
};

//<<<


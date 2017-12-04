package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is used to report the properties that are UINT32 values. It is returned in response to a TPM2_GetCapability().
*/
public class TPMS_TAGGED_PROPERTY extends TpmStructure
{
    /**
     * This structure is used to report the properties that are UINT32 values. It is returned in response to a TPM2_GetCapability().
     * 
     * @param _property a property identifier 
     * @param _value the value of the property
     */
    public TPMS_TAGGED_PROPERTY(TPM_PT _property,int _value)
    {
        property = _property;
        value = _value;
    }
    /**
    * This structure is used to report the properties that are UINT32 values. It is returned in response to a TPM2_GetCapability().
    */
    public TPMS_TAGGED_PROPERTY() {};
    /**
    * a property identifier
    */
    public TPM_PT property;
    /**
    * the value of the property
    */
    public int value;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        property.toTpm(buf);
        buf.write(value);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        property = TPM_PT.fromTpm(buf);
        value =  buf.readInt(4);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPMS_TAGGED_PROPERTY fromTpm (byte[] x) 
    {
        TPMS_TAGGED_PROPERTY ret = new TPMS_TAGGED_PROPERTY();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPMS_TAGGED_PROPERTY fromTpm (InByteBuf buf) 
    {
        TPMS_TAGGED_PROPERTY ret = new TPMS_TAGGED_PROPERTY();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_TAGGED_PROPERTY");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_PT", "property", property);
        _p.add(d, "uint", "value", value);
    };
    
    
};

//<<<


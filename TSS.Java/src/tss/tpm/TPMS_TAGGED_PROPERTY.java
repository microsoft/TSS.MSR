package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used to report the properties that are UINT32 values. It is returned
 *  in response to a TPM2_GetCapability().
 */
public class TPMS_TAGGED_PROPERTY extends TpmStructure
{
    /** A property identifier  */
    public TPM_PT property;
    
    /** The value of the property  */
    public int value;
    
    public TPMS_TAGGED_PROPERTY() {}
    
    /** @param _property A property identifier
     *  @param _value The value of the property
     */
    public TPMS_TAGGED_PROPERTY(TPM_PT _property, int _value)
    {
        property = _property;
        value = _value;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        property.toTpm(buf);
        buf.writeInt(value);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        property = TPM_PT.fromTpm(buf);
        value = buf.readInt();
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPMS_TAGGED_PROPERTY fromBytes (byte[] byteBuf) 
    {
        TPMS_TAGGED_PROPERTY ret = new TPMS_TAGGED_PROPERTY();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_TAGGED_PROPERTY fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
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
        _p.add(d, "int", "value", value);
    }
}

//<<<

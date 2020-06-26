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
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        property.toTpm(buf);
        buf.writeInt(value);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        property = TPM_PT.fromTpm(buf);
        value = buf.readInt();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_TAGGED_PROPERTY fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_TAGGED_PROPERTY.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_TAGGED_PROPERTY fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_TAGGED_PROPERTY fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_TAGGED_PROPERTY.class);
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

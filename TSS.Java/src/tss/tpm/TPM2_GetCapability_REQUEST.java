package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns various information regarding the TPM and its current state.  */
public class TPM2_GetCapability_REQUEST extends TpmStructure
{
    /** Group selection; determines the format of the response  */
    public TPM_CAP capability;
    
    /** Further definition of information  */
    public int property;
    
    /** Number of properties of the indicated type to return  */
    public int propertyCount;
    
    public TPM2_GetCapability_REQUEST() {}
    
    /** @param _capability Group selection; determines the format of the response
     *  @param _property Further definition of information
     *  @param _propertyCount Number of properties of the indicated type to return
     */
    public TPM2_GetCapability_REQUEST(TPM_CAP _capability, int _property, int _propertyCount)
    {
        capability = _capability;
        property = _property;
        propertyCount = _propertyCount;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        capability.toTpm(buf);
        buf.writeInt(property);
        buf.writeInt(propertyCount);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        capability = TPM_CAP.fromTpm(buf);
        property = buf.readInt();
        propertyCount = buf.readInt();
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_GetCapability_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_GetCapability_REQUEST ret = new TPM2_GetCapability_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_GetCapability_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
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
        _p.add(d, "int", "property", property);
        _p.add(d, "int", "propertyCount", propertyCount);
    }
}

//<<<

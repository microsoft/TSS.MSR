package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns various information regarding the TPM and its current state.  */
public class TPM2_GetCapability_REQUEST extends ReqStructure
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

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        capability.toTpm(buf);
        buf.writeInt(property);
        buf.writeInt(propertyCount);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        capability = TPM_CAP.fromTpm(buf);
        property = buf.readInt();
        propertyCount = buf.readInt();
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_GetCapability_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_GetCapability_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_GetCapability_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_GetCapability_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_GetCapability_REQUEST.class);
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

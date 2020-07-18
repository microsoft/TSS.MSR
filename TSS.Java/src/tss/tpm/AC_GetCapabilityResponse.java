package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** The purpose of this command is to obtain information about an Attached Component
 *  referenced by an AC handle.
 */
public class AC_GetCapabilityResponse extends RespStructure
{
    /** Flag to indicate whether there are more values  */
    public byte moreData;
    
    /** List of capabilities  */
    public TPMS_AC_OUTPUT[] capabilitiesData;
    
    public AC_GetCapabilityResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeByte(moreData);
        buf.writeObjArr(capabilitiesData);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        moreData = buf.readByte();
        capabilitiesData = buf.readObjArr(TPMS_AC_OUTPUT.class);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static AC_GetCapabilityResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(AC_GetCapabilityResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static AC_GetCapabilityResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static AC_GetCapabilityResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(AC_GetCapabilityResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("AC_GetCapabilityResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "moreData", moreData);
        _p.add(d, "TPMS_AC_OUTPUT", "capabilitiesData", capabilitiesData);
    }
}

//<<<

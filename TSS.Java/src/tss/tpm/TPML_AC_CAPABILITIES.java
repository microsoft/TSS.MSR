package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This list is only used in TPM2_AC_GetCapability().  */
public class TPML_AC_CAPABILITIES extends TpmStructure
{
    /** A list of AC values  */
    public TPMS_AC_OUTPUT[] acCapabilities;
    
    public TPML_AC_CAPABILITIES() {}
    
    /** @param _acCapabilities A list of AC values  */
    public TPML_AC_CAPABILITIES(TPMS_AC_OUTPUT[] _acCapabilities) { acCapabilities = _acCapabilities; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(acCapabilities); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { acCapabilities = buf.readObjArr(TPMS_AC_OUTPUT.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPML_AC_CAPABILITIES fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPML_AC_CAPABILITIES.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPML_AC_CAPABILITIES fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPML_AC_CAPABILITIES fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPML_AC_CAPABILITIES.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_AC_CAPABILITIES");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_AC_OUTPUT", "acCapabilities", acCapabilities);
    }
}

//<<<

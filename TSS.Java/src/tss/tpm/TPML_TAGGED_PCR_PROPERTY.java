package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This list is used to report on a list of properties that are TPMS_PCR_SELECT values.
 *  It is returned by a TPM2_GetCapability().
 */
public class TPML_TAGGED_PCR_PROPERTY extends TpmStructure implements TPMU_CAPABILITIES
{
    /** A tagged PCR selection  */
    public TPMS_TAGGED_PCR_SELECT[] pcrProperty;
    
    public TPML_TAGGED_PCR_PROPERTY() {}
    
    /** @param _pcrProperty A tagged PCR selection  */
    public TPML_TAGGED_PCR_PROPERTY(TPMS_TAGGED_PCR_SELECT[] _pcrProperty) { pcrProperty = _pcrProperty; }
    
    /** TpmUnion method  */
    public TPM_CAP GetUnionSelector() { return TPM_CAP.PCR_PROPERTIES; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(pcrProperty); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { pcrProperty = buf.readObjArr(TPMS_TAGGED_PCR_SELECT.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPML_TAGGED_PCR_PROPERTY fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPML_TAGGED_PCR_PROPERTY.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPML_TAGGED_PCR_PROPERTY fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPML_TAGGED_PCR_PROPERTY fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPML_TAGGED_PCR_PROPERTY.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_TAGGED_PCR_PROPERTY");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_TAGGED_PCR_SELECT[]", "pcrProperty", pcrProperty);
    }
}

//<<<

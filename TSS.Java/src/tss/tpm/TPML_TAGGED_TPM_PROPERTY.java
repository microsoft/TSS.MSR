package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This list is used to report on a list of properties that are TPMS_TAGGED_PROPERTY
 *  values. It is returned by a TPM2_GetCapability().
 */
public class TPML_TAGGED_TPM_PROPERTY extends TpmStructure implements TPMU_CAPABILITIES
{
    /** An array of tagged properties  */
    public TPMS_TAGGED_PROPERTY[] tpmProperty;
    
    public TPML_TAGGED_TPM_PROPERTY() {}
    
    /** @param _tpmProperty An array of tagged properties  */
    public TPML_TAGGED_TPM_PROPERTY(TPMS_TAGGED_PROPERTY[] _tpmProperty) { tpmProperty = _tpmProperty; }
    
    /** TpmUnion method  */
    public TPM_CAP GetUnionSelector() { return TPM_CAP.TPM_PROPERTIES; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(tpmProperty); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { tpmProperty = buf.readObjArr(TPMS_TAGGED_PROPERTY.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPML_TAGGED_TPM_PROPERTY fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPML_TAGGED_TPM_PROPERTY.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPML_TAGGED_TPM_PROPERTY fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPML_TAGGED_TPM_PROPERTY fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPML_TAGGED_TPM_PROPERTY.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_TAGGED_TPM_PROPERTY");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_TAGGED_PROPERTY", "tpmProperty", tpmProperty);
    }
}

//<<<

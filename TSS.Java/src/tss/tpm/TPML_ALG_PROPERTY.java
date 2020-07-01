package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This list is used to report on a list of algorithm attributes. It is returned in a
 *  TPM2_GetCapability().
 */
public class TPML_ALG_PROPERTY extends TpmStructure implements TPMU_CAPABILITIES
{
    /** List of properties  */
    public TPMS_ALG_PROPERTY[] algProperties;
    
    public TPML_ALG_PROPERTY() {}
    
    /** @param _algProperties List of properties  */
    public TPML_ALG_PROPERTY(TPMS_ALG_PROPERTY[] _algProperties) { algProperties = _algProperties; }
    
    /** TpmUnion method  */
    public TPM_CAP GetUnionSelector() { return TPM_CAP.ALGS; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(algProperties); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { algProperties = buf.readObjArr(TPMS_ALG_PROPERTY.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPML_ALG_PROPERTY fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPML_ALG_PROPERTY.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPML_ALG_PROPERTY fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPML_ALG_PROPERTY fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPML_ALG_PROPERTY.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_ALG_PROPERTY");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_ALG_PROPERTY", "algProperties", algProperties);
    }
}

//<<<

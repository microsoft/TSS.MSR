package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This list is used to report the ECC curve ID values supported by the TPM. It is
 *  returned by a TPM2_GetCapability().
 */
public class TPML_ECC_CURVE extends TpmStructure implements TPMU_CAPABILITIES
{
    /** Array of ECC curve identifiers  */
    public TPM_ECC_CURVE[] eccCurves;
    
    public TPML_ECC_CURVE() {}
    
    /** @param _eccCurves Array of ECC curve identifiers  */
    public TPML_ECC_CURVE(TPM_ECC_CURVE[] _eccCurves) { eccCurves = _eccCurves; }
    
    /** TpmUnion method  */
    public TPM_CAP GetUnionSelector() { return TPM_CAP.ECC_CURVES; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(eccCurves); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { eccCurves = buf.readObjArr(TPM_ECC_CURVE.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPML_ECC_CURVE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPML_ECC_CURVE.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPML_ECC_CURVE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPML_ECC_CURVE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPML_ECC_CURVE.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_ECC_CURVE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ECC_CURVE[]", "eccCurves", eccCurves);
    }
}

//<<<

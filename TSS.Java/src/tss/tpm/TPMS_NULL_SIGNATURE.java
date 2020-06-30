package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Custom data structure representing an empty element (i.e. the one with 
 *  no data to marshal) for selector algorithm TPM_ALG_NULL for the union TPMU_SIGNATURE
 */
public class TPMS_NULL_SIGNATURE extends TPMS_NULL_UNION
{
    public TPMS_NULL_SIGNATURE() {}
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.NULL; }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_NULL_SIGNATURE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_NULL_SIGNATURE.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_NULL_SIGNATURE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_NULL_SIGNATURE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_NULL_SIGNATURE.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_NULL_SIGNATURE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used as a placeholder. In some cases, a union will have a selector
 *  value with no data to unmarshal when that type is selected. Rather than leave the
 *  entry empty, TPMS_EMPTY may be selected.
 */
public class TPMS_EMPTY extends TpmStructure implements TPMU_ASYM_SCHEME
{
    public TPMS_EMPTY() {}
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.RSAES; }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_EMPTY fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_EMPTY.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_EMPTY fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_EMPTY fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_EMPTY.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_EMPTY");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

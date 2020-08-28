package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Custom data structure representing an empty element (i.e. the one with 
 *  no data to marshal) for selector algorithm TPM_ALG_NULL for the union TPMU_SIG_SCHEME
 */
public class TPMS_NULL_SIG_SCHEME extends TPMS_NULL_UNION
{
    public TPMS_NULL_SIG_SCHEME() {}

    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.NULL; }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPMS_NULL_SIG_SCHEME fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_NULL_SIG_SCHEME.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_NULL_SIG_SCHEME fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPMS_NULL_SIG_SCHEME fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_NULL_SIG_SCHEME.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_NULL_SIG_SCHEME");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

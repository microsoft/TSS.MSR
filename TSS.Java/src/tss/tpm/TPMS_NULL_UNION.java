package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Base class for empty union elements.
 *  An empty union element does not contain any data to marshal.
 *  This data structure can be used in place of any other union
 *  initialized with its own empty element.
 */
public class TPMS_NULL_UNION extends TpmStructure implements TPMU_SYM_DETAILS, TPMU_SCHEME_KEYEDHASH, TPMU_SIG_SCHEME, TPMU_KDF_SCHEME, TPMU_ASYM_SCHEME, TPMU_SIGNATURE
{
    public TPMS_NULL_UNION() {}

    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.NULL; }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPMS_NULL_UNION fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_NULL_UNION.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_NULL_UNION fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPMS_NULL_UNION fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_NULL_UNION.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_NULL_UNION");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

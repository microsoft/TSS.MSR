package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Custom data structure representing an empty element (i.e. the one with 
 *  no data to marshal) for selector algorithm TPM_ALG_NULL for the union TPMU_SCHEME_KEYEDHASH
 */
public class TPMS_NULL_SCHEME_KEYEDHASH extends TPMS_NULL_UNION
{
    public TPMS_NULL_SCHEME_KEYEDHASH() {}

    /** TpmUnion method */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.NULL; }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_NULL_SCHEME_KEYEDHASH fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_NULL_SCHEME_KEYEDHASH.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_NULL_SCHEME_KEYEDHASH fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_NULL_SCHEME_KEYEDHASH fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_NULL_SCHEME_KEYEDHASH.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_NULL_SCHEME_KEYEDHASH");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

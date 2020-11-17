package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Custom data structure representing an empty element (i.e. the one with 
 *  no data to marshal) for selector algorithm TPM_ALG_TDES for the union TPMU_SYM_DETAILS
 */
public class TPMS_TDES_SYM_DETAILS extends TPMS_NULL_UNION
{
    public TPMS_TDES_SYM_DETAILS() {}

    /** TpmUnion method */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.TDES; }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_TDES_SYM_DETAILS fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_TDES_SYM_DETAILS.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_TDES_SYM_DETAILS fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_TDES_SYM_DETAILS fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_TDES_SYM_DETAILS.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_TDES_SYM_DETAILS");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

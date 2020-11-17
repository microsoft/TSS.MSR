package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** TPMS_AC_OUTPUT is used to return information about an AC. The tag structure parameter
 *  indicates the type of the data value.
 */
public class TPMS_AC_OUTPUT extends TpmStructure
{
    /** Tag indicating the contents of data */
    public TPM_AT tag;

    /** The data returned from the AC */
    public int data;

    public TPMS_AC_OUTPUT() {}

    /** @param _tag Tag indicating the contents of data
     *  @param _data The data returned from the AC
     */
    public TPMS_AC_OUTPUT(TPM_AT _tag, int _data)
    {
        tag = _tag;
        data = _data;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        tag.toTpm(buf);
        buf.writeInt(data);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        tag = TPM_AT.fromTpm(buf);
        data = buf.readInt();
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_AC_OUTPUT fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_AC_OUTPUT.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_AC_OUTPUT fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_AC_OUTPUT fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_AC_OUTPUT.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_AC_OUTPUT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_AT", "tag", tag);
        _p.add(d, "int", "data", data);
    }
}

//<<<

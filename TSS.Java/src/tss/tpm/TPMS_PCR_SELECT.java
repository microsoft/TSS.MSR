package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure provides a standard method of specifying a list of PCR. */
public class TPMS_PCR_SELECT extends TpmStructure
{
    /** The bit map of selected PCR */
    public byte[] pcrSelect;

    public TPMS_PCR_SELECT() {}

    /** @param _pcrSelect The bit map of selected PCR */
    public TPMS_PCR_SELECT(byte[] _pcrSelect) { pcrSelect = _pcrSelect; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(pcrSelect, 1); }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf) { pcrSelect = buf.readSizedByteBuf(1); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_PCR_SELECT fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_PCR_SELECT.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_PCR_SELECT fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_PCR_SELECT fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_PCR_SELECT.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_PCR_SELECT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "pcrSelect", pcrSelect);
    }
}

//<<<

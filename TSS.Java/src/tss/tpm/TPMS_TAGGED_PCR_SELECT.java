package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used in TPM2_GetCapability() to return the attributes of the PCR. */
public class TPMS_TAGGED_PCR_SELECT extends TpmStructure
{
    /** The property identifier */
    public TPM_PT_PCR tag;

    /** The bit map of PCR with the identified property */
    public byte[] pcrSelect;

    public TPMS_TAGGED_PCR_SELECT() {}

    /** @param _tag The property identifier
     *  @param _pcrSelect The bit map of PCR with the identified property
     */
    public TPMS_TAGGED_PCR_SELECT(TPM_PT_PCR _tag, byte[] _pcrSelect)
    {
        tag = _tag;
        pcrSelect = _pcrSelect;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        tag.toTpm(buf);
        buf.writeSizedByteBuf(pcrSelect, 1);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        tag = TPM_PT_PCR.fromTpm(buf);
        pcrSelect = buf.readSizedByteBuf(1);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_TAGGED_PCR_SELECT fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_TAGGED_PCR_SELECT.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_TAGGED_PCR_SELECT fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_TAGGED_PCR_SELECT fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_TAGGED_PCR_SELECT.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_TAGGED_PCR_SELECT");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_PT_PCR", "tag", tag);
        _p.add(d, "byte[]", "pcrSelect", pcrSelect);
    }
}

//<<<

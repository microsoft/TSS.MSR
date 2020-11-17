package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is created by TPM2_Create() and TPM2_CreatePrimary(). It is never
 *  entered into the TPM and never has a size of zero.
 */
public class TPM2B_CREATION_DATA extends TpmStructure
{
    public TPMS_CREATION_DATA creationData;

    public TPM2B_CREATION_DATA() {}

    /** @param _creationData TBD */
    public TPM2B_CREATION_DATA(TPMS_CREATION_DATA _creationData) { creationData = _creationData; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedObj(creationData); }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf) { creationData = buf.createSizedObj(TPMS_CREATION_DATA.class); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_CREATION_DATA fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_CREATION_DATA.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_CREATION_DATA fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_CREATION_DATA fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_CREATION_DATA.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_CREATION_DATA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_CREATION_DATA", "creationData", creationData);
    }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** The TPM2B_PRIVATE structure is used as a parameter in multiple commands that create,
 *  load, and modify the sensitive area of an object.
 */
public class TPM2B_PRIVATE extends TpmStructure
{
    /** An encrypted private area */
    public byte[] buffer;

    public TPM2B_PRIVATE() {}

    /** @param _buffer An encrypted private area */
    public TPM2B_PRIVATE(byte[] _buffer) { buffer = _buffer; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(buffer); }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf) { buffer = buf.readSizedByteBuf(); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_PRIVATE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_PRIVATE.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_PRIVATE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_PRIVATE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_PRIVATE.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_PRIVATE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "buffer", buffer);
    }
}

//<<<

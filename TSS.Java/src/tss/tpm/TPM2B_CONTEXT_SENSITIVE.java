package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure holds the object or session context data. When saved, the full
 *  structure is encrypted.
 */
public class TPM2B_CONTEXT_SENSITIVE extends TpmStructure
{
    /** The sensitive data */
    public byte[] buffer;

    public TPM2B_CONTEXT_SENSITIVE() {}

    /** @param _buffer The sensitive data */
    public TPM2B_CONTEXT_SENSITIVE(byte[] _buffer) { buffer = _buffer; }

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
    public static TPM2B_CONTEXT_SENSITIVE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_CONTEXT_SENSITIVE.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_CONTEXT_SENSITIVE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_CONTEXT_SENSITIVE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_CONTEXT_SENSITIVE.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_CONTEXT_SENSITIVE");
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

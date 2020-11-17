package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This sized buffer holds the largest RSA public key supported by the TPM. */
public class TPM2B_PUBLIC_KEY_RSA extends TpmStructure implements TPMU_PUBLIC_ID
{
    /** Value */
    public byte[] buffer;

    public TPM2B_PUBLIC_KEY_RSA() {}

    /** @param _buffer Value */
    public TPM2B_PUBLIC_KEY_RSA(byte[] _buffer) { buffer = _buffer; }

    /** TpmUnion method */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.RSA; }

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
    public static TPM2B_PUBLIC_KEY_RSA fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_PUBLIC_KEY_RSA.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_PUBLIC_KEY_RSA fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2B_PUBLIC_KEY_RSA fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_PUBLIC_KEY_RSA.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_PUBLIC_KEY_RSA");
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

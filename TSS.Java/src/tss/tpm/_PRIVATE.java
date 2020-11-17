package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is defined to size the contents of a TPM2B_PRIVATE. This structure is
 *  not directly marshaled or unmarshaled.
 */
public class _PRIVATE extends TpmStructure
{
    public byte[] integrityOuter;

    /** Could also be a TPM2B_IV */
    public byte[] integrityInner;

    /** The sensitive area */
    public TPMT_SENSITIVE sensitive;

    public _PRIVATE() {}

    /** @param _integrityOuter TBD
     *  @param _integrityInner Could also be a TPM2B_IV
     *  @param _sensitive The sensitive area
     */
    public _PRIVATE(byte[] _integrityOuter, byte[] _integrityInner, TPMT_SENSITIVE _sensitive)
    {
        integrityOuter = _integrityOuter;
        integrityInner = _integrityInner;
        sensitive = _sensitive;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(integrityOuter);
        buf.writeSizedByteBuf(integrityInner);
        buf.writeSizedObj(sensitive);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        integrityOuter = buf.readSizedByteBuf();
        integrityInner = buf.readSizedByteBuf();
        sensitive = buf.createSizedObj(TPMT_SENSITIVE.class);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static _PRIVATE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(_PRIVATE.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static _PRIVATE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static _PRIVATE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(_PRIVATE.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("_PRIVATE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "integrityOuter", integrityOuter);
        _p.add(d, "byte[]", "integrityInner", integrityInner);
        _p.add(d, "TPMT_SENSITIVE", "sensitive", sensitive);
    }
}

//<<<

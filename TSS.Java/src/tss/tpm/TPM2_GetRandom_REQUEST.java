package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns the next bytesRequested octets from the random number generator (RNG). */
public class TPM2_GetRandom_REQUEST extends ReqStructure
{
    /** Number of octets to return */
    public int bytesRequested;

    public TPM2_GetRandom_REQUEST() {}

    /** @param _bytesRequested Number of octets to return */
    public TPM2_GetRandom_REQUEST(int _bytesRequested) { bytesRequested = _bytesRequested; }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeShort(bytesRequested); }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf) { bytesRequested = buf.readShort(); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_GetRandom_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_GetRandom_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_GetRandom_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_GetRandom_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_GetRandom_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_GetRandom_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "int", "bytesRequested", bytesRequested);
    }
}

//<<<

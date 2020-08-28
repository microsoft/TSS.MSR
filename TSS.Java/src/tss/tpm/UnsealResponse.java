package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns the data in a loaded Sealed Data Object.  */
public class UnsealResponse extends RespStructure
{
    /** Unsealed data
     *  Size of outData is limited to be no more than 128 octets.
     */
    public byte[] outData;

    public UnsealResponse() {}

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(outData); }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { outData = buf.readSizedByteBuf(); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static UnsealResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(UnsealResponse.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static UnsealResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static UnsealResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(UnsealResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("UnsealResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "outData", outData);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

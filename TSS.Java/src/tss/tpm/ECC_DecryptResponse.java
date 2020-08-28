package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs ECC decryption.  */
public class ECC_DecryptResponse extends RespStructure
{
    /** Decrypted output  */
    public byte[] plainText;

    public ECC_DecryptResponse() {}

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(plainText); }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { plainText = buf.readSizedByteBuf(); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static ECC_DecryptResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(ECC_DecryptResponse.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static ECC_DecryptResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static ECC_DecryptResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(ECC_DecryptResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("ECC_DecryptResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "plainText", plainText);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

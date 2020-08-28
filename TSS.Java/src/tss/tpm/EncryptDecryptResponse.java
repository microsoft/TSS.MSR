package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** NOTE 1 This command is deprecated, and TPM2_EncryptDecrypt2() is preferred. This
 *  should be reflected in platform-specific specifications.
 */
public class EncryptDecryptResponse extends RespStructure
{
    /** Encrypted or decrypted output  */
    public byte[] outData;

    /** Chaining value to use for IV in next round  */
    public byte[] ivOut;

    public EncryptDecryptResponse() {}

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(outData);
        buf.writeSizedByteBuf(ivOut);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        outData = buf.readSizedByteBuf();
        ivOut = buf.readSizedByteBuf();
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static EncryptDecryptResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(EncryptDecryptResponse.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static EncryptDecryptResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static EncryptDecryptResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(EncryptDecryptResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("EncryptDecryptResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "outData", outData);
        _p.add(d, "byte[]", "ivOut", ivOut);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

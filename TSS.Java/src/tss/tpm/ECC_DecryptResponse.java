package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs ECC decryption. */
public class ECC_DecryptResponse extends TpmStructure
{
    /** decrypted output */
    public byte[] plainText;
    
    public ECC_DecryptResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(plainText);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _plainTextSize = buf.readShort() & 0xFFFF;
        plainText = new byte[_plainTextSize];
        buf.readArrayOfInts(plainText, 1, _plainTextSize);
    }

    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }

    public static ECC_DecryptResponse fromTpm (byte[] x) 
    {
        ECC_DecryptResponse ret = new ECC_DecryptResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static ECC_DecryptResponse fromTpm (InByteBuf buf) 
    {
        ECC_DecryptResponse ret = new ECC_DecryptResponse();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ECC_Decrypt_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "plainText", plainText);
    }
}

//<<<

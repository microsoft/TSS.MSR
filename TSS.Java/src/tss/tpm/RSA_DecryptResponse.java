package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs RSA decryption using the indicated padding scheme according to
 *  IETF RFC 8017 ((PKCS#1).
 */
public class RSA_DecryptResponse extends TpmStructure
{
    /** Decrypted output  */
    public byte[] message;
    
    public RSA_DecryptResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(message);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _messageSize = buf.readShort() & 0xFFFF;
        message = new byte[_messageSize];
        buf.readArrayOfInts(message, 1, _messageSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static RSA_DecryptResponse fromBytes (byte[] byteBuf) 
    {
        RSA_DecryptResponse ret = new RSA_DecryptResponse();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static RSA_DecryptResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static RSA_DecryptResponse fromTpm (InByteBuf buf) 
    {
        RSA_DecryptResponse ret = new RSA_DecryptResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_RSA_Decrypt_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "message", message);
    }
}

//<<<

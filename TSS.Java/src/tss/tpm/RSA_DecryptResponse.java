package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command performs RSA decryption using the indicated padding scheme according to IETF RFC 8017 ((PKCS#1).
*/
public class RSA_DecryptResponse extends TpmStructure
{
    /**
     * This command performs RSA decryption using the indicated padding scheme according to IETF RFC 8017 ((PKCS#1).
     * 
     * @param _message decrypted output
     */
    public RSA_DecryptResponse(byte[] _message)
    {
        message = _message;
    }
    /**
    * This command performs RSA decryption using the indicated padding scheme according to IETF RFC 8017 ((PKCS#1).
    */
    public RSA_DecryptResponse() {};
    /**
    * size of the buffer The value of zero is only valid for create.
    */
    // private short messageSize;
    /**
    * decrypted output
    */
    public byte[] message;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((message!=null)?message.length:0, 2);
        if(message!=null)
            buf.write(message);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _messageSize = buf.readInt(2);
        message = new byte[_messageSize];
        buf.readArrayOfInts(message, 1, _messageSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static RSA_DecryptResponse fromTpm (byte[] x) 
    {
        RSA_DecryptResponse ret = new RSA_DecryptResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
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
    };
    
    
};

//<<<


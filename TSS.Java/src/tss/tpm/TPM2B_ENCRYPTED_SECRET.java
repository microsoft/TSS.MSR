package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Table 182 Definition of TPM2B_ENCRYPTED_SECRET Structure
*/
public class TPM2B_ENCRYPTED_SECRET extends TpmStructure
{
    /**
     * Table 182 Definition of TPM2B_ENCRYPTED_SECRET Structure
     * 
     * @param _secret secret
     */
    public TPM2B_ENCRYPTED_SECRET(byte[] _secret)
    {
        secret = _secret;
    }
    /**
    * Table 182 Definition of TPM2B_ENCRYPTED_SECRET Structure
    */
    public TPM2B_ENCRYPTED_SECRET() {};
    /**
    * size of the secret value
    */
    // private short size;
    /**
    * secret
    */
    public byte[] secret;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((secret!=null)?secret.length:0, 2);
        if(secret!=null)
            buf.write(secret);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _size = buf.readInt(2);
        secret = new byte[_size];
        buf.readArrayOfInts(secret, 1, _size);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2B_ENCRYPTED_SECRET fromTpm (byte[] x) 
    {
        TPM2B_ENCRYPTED_SECRET ret = new TPM2B_ENCRYPTED_SECRET();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_ENCRYPTED_SECRET fromTpm (InByteBuf buf) 
    {
        TPM2B_ENCRYPTED_SECRET ret = new TPM2B_ENCRYPTED_SECRET();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_ENCRYPTED_SECRET");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "secret", secret);
    };
    
    
};

//<<<


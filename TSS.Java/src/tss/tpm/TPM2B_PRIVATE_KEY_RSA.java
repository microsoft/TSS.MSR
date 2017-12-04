package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This sized buffer holds the largest RSA prime number supported by the TPM.
*/
public class TPM2B_PRIVATE_KEY_RSA extends TpmStructure implements TPMU_SENSITIVE_COMPOSITE 
{
    /**
     * This sized buffer holds the largest RSA prime number supported by the TPM.
     * 
     * @param _buffer -
     */
    public TPM2B_PRIVATE_KEY_RSA(byte[] _buffer)
    {
        buffer = _buffer;
    }
    /**
    * This sized buffer holds the largest RSA prime number supported by the TPM.
    */
    public TPM2B_PRIVATE_KEY_RSA() {};
    // private short size;
    public byte[] buffer;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((buffer!=null)?buffer.length:0, 2);
        if(buffer!=null)
            buf.write(buffer);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _size = buf.readInt(2);
        buffer = new byte[_size];
        buf.readArrayOfInts(buffer, 1, _size);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2B_PRIVATE_KEY_RSA fromTpm (byte[] x) 
    {
        TPM2B_PRIVATE_KEY_RSA ret = new TPM2B_PRIVATE_KEY_RSA();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_PRIVATE_KEY_RSA fromTpm (InByteBuf buf) 
    {
        TPM2B_PRIVATE_KEY_RSA ret = new TPM2B_PRIVATE_KEY_RSA();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_PRIVATE_KEY_RSA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "buffer", buffer);
    };
    
    
};

//<<<


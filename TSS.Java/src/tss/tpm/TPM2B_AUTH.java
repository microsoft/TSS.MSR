package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is used for an authorization value and limits an authValue to being no larger than the largest digest produced by a TPM. In order to ensure consistency within an object, the authValue may be no larger than the size of the digest produced by the objects nameAlg. This ensures that any TPM that can load the object will be able to handle the authValue of the object.
*/
public class TPM2B_AUTH extends TpmStructure implements TPMU_PUBLIC_ID 
{
    /**
     * This structure is used for an authorization value and limits an authValue to being no larger than the largest digest produced by a TPM. In order to ensure consistency within an object, the authValue may be no larger than the size of the digest produced by the objects nameAlg. This ensures that any TPM that can load the object will be able to handle the authValue of the object.
     * 
     * @param _buffer the buffer area that can be no larger than a digest
     */
    public TPM2B_AUTH(byte[] _buffer)
    {
        buffer = _buffer;
    }
    /**
    * This structure is used for an authorization value and limits an authValue to being no larger than the largest digest produced by a TPM. In order to ensure consistency within an object, the authValue may be no larger than the size of the digest produced by the objects nameAlg. This ensures that any TPM that can load the object will be able to handle the authValue of the object.
    */
    public TPM2B_AUTH() {};
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short size;
    /**
    * the buffer area that can be no larger than a digest
    */
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
    public static TPM2B_AUTH fromTpm (byte[] x) 
    {
        TPM2B_AUTH ret = new TPM2B_AUTH();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_AUTH fromTpm (InByteBuf buf) 
    {
        TPM2B_AUTH ret = new TPM2B_AUTH();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_AUTH");
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


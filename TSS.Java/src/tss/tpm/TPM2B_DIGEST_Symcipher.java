package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Auto-derived from TPM2B_DIGEST to provide unique GetUnionSelector() implementation
*/
public class TPM2B_DIGEST_Symcipher extends TpmStructure implements TPMU_PUBLIC_ID 
{
    /**
     * Auto-derived from TPM2B_DIGEST to provide unique GetUnionSelector() implementation
     * 
     * @param _buffer the buffer area that can be no larger than a digest
     */
    public TPM2B_DIGEST_Symcipher(byte[] _buffer)
    {
        buffer = _buffer;
    }
    /**
    * Auto-derived from TPM2B_DIGEST to provide unique GetUnionSelector() implementation
    */
    public TPM2B_DIGEST_Symcipher() {};
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
    public static TPM2B_DIGEST_Symcipher fromTpm (byte[] x) 
    {
        TPM2B_DIGEST_Symcipher ret = new TPM2B_DIGEST_Symcipher();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_DIGEST_Symcipher fromTpm (InByteBuf buf) 
    {
        TPM2B_DIGEST_Symcipher ret = new TPM2B_DIGEST_Symcipher();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_DIGEST_Symcipher");
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


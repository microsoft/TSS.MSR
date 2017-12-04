package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* The TPM2B_PRIVATE structure is used as a parameter in multiple commands that create, load, and modify the sensitive area of an object.
*/
public class TPM2B_PRIVATE extends TpmStructure
{
    /**
     * The TPM2B_PRIVATE structure is used as a parameter in multiple commands that create, load, and modify the sensitive area of an object.
     * 
     * @param _buffer an encrypted private area
     */
    public TPM2B_PRIVATE(byte[] _buffer)
    {
        buffer = _buffer;
    }
    /**
    * The TPM2B_PRIVATE structure is used as a parameter in multiple commands that create, load, and modify the sensitive area of an object.
    */
    public TPM2B_PRIVATE() {};
    /**
    * size of the private structure
    */
    // private short size;
    /**
    * an encrypted private area
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
    public static TPM2B_PRIVATE fromTpm (byte[] x) 
    {
        TPM2B_PRIVATE ret = new TPM2B_PRIVATE();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_PRIVATE fromTpm (InByteBuf buf) 
    {
        TPM2B_PRIVATE ret = new TPM2B_PRIVATE();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_PRIVATE");
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


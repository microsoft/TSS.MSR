package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This structure is used to hold a symmetric key in the sensitive area of an asymmetric object.
*/
public class TPM2B_SYM_KEY extends TpmStructure implements TPMU_SENSITIVE_COMPOSITE 
{
    /**
     * This structure is used to hold a symmetric key in the sensitive area of an asymmetric object.
     * 
     * @param _buffer the key
     */
    public TPM2B_SYM_KEY(byte[] _buffer)
    {
        buffer = _buffer;
    }
    /**
    * This structure is used to hold a symmetric key in the sensitive area of an asymmetric object.
    */
    public TPM2B_SYM_KEY() {};
    /**
    * size, in octets, of the buffer containing the key; may be zero
    */
    // private short size;
    /**
    * the key
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
    public static TPM2B_SYM_KEY fromTpm (byte[] x) 
    {
        TPM2B_SYM_KEY ret = new TPM2B_SYM_KEY();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_SYM_KEY fromTpm (InByteBuf buf) 
    {
        TPM2B_SYM_KEY ret = new TPM2B_SYM_KEY();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_SYM_KEY");
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


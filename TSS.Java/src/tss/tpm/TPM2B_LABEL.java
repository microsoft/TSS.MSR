package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This buffer holds a label or context value. For interoperability and backwards compatibility, LABEL_MAX_BUFFER is the minimum of the largest digest on the device and the largest ECC parameter (MAX_ECC_KEY_BYTES) but no more than 32 bytes.
*/
public class TPM2B_LABEL extends TpmStructure
{
    /**
     * This buffer holds a label or context value. For interoperability and backwards compatibility, LABEL_MAX_BUFFER is the minimum of the largest digest on the device and the largest ECC parameter (MAX_ECC_KEY_BYTES) but no more than 32 bytes.
     * 
     * @param _buffer symmetic data for a created object or the label and context for a derived object
     */
    public TPM2B_LABEL(byte[] _buffer)
    {
        buffer = _buffer;
    }
    /**
    * This buffer holds a label or context value. For interoperability and backwards compatibility, LABEL_MAX_BUFFER is the minimum of the largest digest on the device and the largest ECC parameter (MAX_ECC_KEY_BYTES) but no more than 32 bytes.
    */
    public TPM2B_LABEL() {};
    // private short size;
    /**
    * symmetic data for a created object or the label and context for a derived object
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
    public static TPM2B_LABEL fromTpm (byte[] x) 
    {
        TPM2B_LABEL ret = new TPM2B_LABEL();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_LABEL fromTpm (InByteBuf buf) 
    {
        TPM2B_LABEL ret = new TPM2B_LABEL();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_LABEL");
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


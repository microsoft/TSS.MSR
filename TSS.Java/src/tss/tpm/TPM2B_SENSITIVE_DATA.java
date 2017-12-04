package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This buffer wraps the TPMU_SENSITIVE_CREATE structure.
*/
public class TPM2B_SENSITIVE_DATA extends TpmStructure implements TPMU_SENSITIVE_COMPOSITE 
{
    /**
     * This buffer wraps the TPMU_SENSITIVE_CREATE structure.
     * 
     * @param _buffer symmetic data for a created object or the label and context for a derived object
     */
    public TPM2B_SENSITIVE_DATA(byte[] _buffer)
    {
        buffer = _buffer;
    }
    /**
    * This buffer wraps the TPMU_SENSITIVE_CREATE structure.
    */
    public TPM2B_SENSITIVE_DATA() {};
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
    public static TPM2B_SENSITIVE_DATA fromTpm (byte[] x) 
    {
        TPM2B_SENSITIVE_DATA ret = new TPM2B_SENSITIVE_DATA();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_SENSITIVE_DATA fromTpm (InByteBuf buf) 
    {
        TPM2B_SENSITIVE_DATA ret = new TPM2B_SENSITIVE_DATA();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_SENSITIVE_DATA");
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


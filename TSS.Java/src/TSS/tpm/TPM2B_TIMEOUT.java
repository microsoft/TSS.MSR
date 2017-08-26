package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This TPM-dependent structure is used to provide the timeout value for an authorization.
*/
public class TPM2B_TIMEOUT extends TpmStructure implements TPMU_PUBLIC_ID 
{
    /**
    * This TPM-dependent structure is used to provide the timeout value for an authorization.
    * 
    * @param _buffer the buffer area that can be no larger than a digest
    */
    public TPM2B_TIMEOUT(byte[] _buffer)
    {
        buffer = _buffer;
    }
    /**
    * This TPM-dependent structure is used to provide the timeout value for an authorization.
    */
    public TPM2B_TIMEOUT() {};
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
        buf.write(buffer);
        return;
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
    public static TPM2B_TIMEOUT fromTpm (byte[] x) 
    {
        TPM2B_TIMEOUT ret = new TPM2B_TIMEOUT();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2B_TIMEOUT fromTpm (InByteBuf buf) 
    {
        TPM2B_TIMEOUT ret = new TPM2B_TIMEOUT();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_TIMEOUT");
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


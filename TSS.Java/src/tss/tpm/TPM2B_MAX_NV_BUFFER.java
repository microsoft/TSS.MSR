package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This type is a sized buffer that can hold a maximally sized buffer for NV data commands
 *  such as TPM2_NV_Read(), TPM2_NV_Write(), and TPM2_NV_Certify().
 */
public class TPM2B_MAX_NV_BUFFER extends TpmStructure
{
    /**
     *  the operand
     *  NOTE MAX_NV_BUFFER_SIZE is TPM-dependent
     */
    public byte[] buffer;
    
    public TPM2B_MAX_NV_BUFFER() {}
    
    /**
     *  @param _buffer the operand
     *         NOTE MAX_NV_BUFFER_SIZE is TPM-dependent
     */
    public TPM2B_MAX_NV_BUFFER(byte[] _buffer) { buffer = _buffer; }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(buffer);
    }

    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _size = buf.readShort() & 0xFFFF;
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

    public static TPM2B_MAX_NV_BUFFER fromTpm (byte[] x) 
    {
        TPM2B_MAX_NV_BUFFER ret = new TPM2B_MAX_NV_BUFFER();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2B_MAX_NV_BUFFER fromTpm (InByteBuf buf) 
    {
        TPM2B_MAX_NV_BUFFER ret = new TPM2B_MAX_NV_BUFFER();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_MAX_NV_BUFFER");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "buffer", buffer);
    }
}

//<<<


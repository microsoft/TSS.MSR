package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This structure is used for a sized buffer that cannot be larger than the largest digest
 *  produced by any hash algorithm implemented on the TPM.
 */
public class TPM2B_DIGEST extends TpmStructure implements TPMU_PUBLIC_ID
{
    /** the buffer area that can be no larger than a digest */
    public byte[] buffer;
    
    public TPM2B_DIGEST() {}
    
    /** @param _buffer the buffer area that can be no larger than a digest */
    public TPM2B_DIGEST(byte[] _buffer) { buffer = _buffer; }
    
    /** TpmUnion method */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.KEYEDHASH; }
    
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
        return buf.buffer();
    }

    public static TPM2B_DIGEST fromTpm (byte[] x) 
    {
        TPM2B_DIGEST ret = new TPM2B_DIGEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }

    public static TPM2B_DIGEST fromTpm (InByteBuf buf) 
    {
        TPM2B_DIGEST ret = new TPM2B_DIGEST();
        ret.initFromTpm(buf);
        return ret;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_DIGEST");
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

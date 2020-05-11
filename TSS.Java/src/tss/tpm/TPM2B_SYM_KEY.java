package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This structure is used to hold a symmetric key in the sensitive area
 *  of an asymmetric object.
 */
public class TPM2B_SYM_KEY extends TpmStructure implements TPMU_SENSITIVE_COMPOSITE
{
    /** the key */
    public byte[] buffer;
    
    public TPM2B_SYM_KEY() {}
    
    /** @param _buffer the key */
    public TPM2B_SYM_KEY(byte[] _buffer) { buffer = _buffer; }
    
    /** TpmUnion method */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.SYMCIPHER; }
    
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
    }
}

//<<<

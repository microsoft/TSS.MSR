package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This buffer holds a Name for any entity type.  */
public class TPM2B_NAME extends TpmStructure
{
    /** The Name structure  */
    public byte[] name;
    
    public TPM2B_NAME() {}
    
    /** @param _name The Name structure  */
    public TPM2B_NAME(byte[] _name) { name = _name; }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(name);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _size = buf.readShort() & 0xFFFF;
        name = new byte[_size];
        buf.readArrayOfInts(name, 1, _size);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2B_NAME fromBytes (byte[] byteBuf) 
    {
        TPM2B_NAME ret = new TPM2B_NAME();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_NAME fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2B_NAME fromTpm (InByteBuf buf) 
    {
        TPM2B_NAME ret = new TPM2B_NAME();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_NAME");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "name", name);
    }
}

//<<<

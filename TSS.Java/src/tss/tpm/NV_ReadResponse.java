package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command reads a value from an area in NV memory previously defined by TPM2_NV_DefineSpace().  */
public class NV_ReadResponse extends TpmStructure
{
    /** The data read  */
    public byte[] data;
    
    public NV_ReadResponse() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(data);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _dataSize = buf.readShort() & 0xFFFF;
        data = new byte[_dataSize];
        buf.readArrayOfInts(data, 1, _dataSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static NV_ReadResponse fromBytes (byte[] byteBuf) 
    {
        NV_ReadResponse ret = new NV_ReadResponse();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static NV_ReadResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static NV_ReadResponse fromTpm (InByteBuf buf) 
    {
        NV_ReadResponse ret = new NV_ReadResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_Read_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "data", data);
    }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command reads a value from an area in NV memory previously defined by TPM2_NV_DefineSpace().
*/
public class NV_ReadResponse extends TpmStructure
{
    /**
     * This command reads a value from an area in NV memory previously defined by TPM2_NV_DefineSpace().
     * 
     * @param _data the data read
     */
    public NV_ReadResponse(byte[] _data)
    {
        data = _data;
    }
    /**
    * This command reads a value from an area in NV memory previously defined by TPM2_NV_DefineSpace().
    */
    public NV_ReadResponse() {};
    /**
    * size of the buffer
    */
    // private short dataSize;
    /**
    * the data read
    */
    public byte[] data;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((data!=null)?data.length:0, 2);
        if(data!=null)
            buf.write(data);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _dataSize = buf.readInt(2);
        data = new byte[_dataSize];
        buf.readArrayOfInts(data, 1, _dataSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static NV_ReadResponse fromTpm (byte[] x) 
    {
        NV_ReadResponse ret = new NV_ReadResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
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
    };
    
    
};

//<<<


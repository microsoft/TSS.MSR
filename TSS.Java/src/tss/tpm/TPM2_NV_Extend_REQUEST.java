package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command extends a value to an area in NV memory that was previously defined by TPM2_NV_DefineSpace.
*/
public class TPM2_NV_Extend_REQUEST extends TpmStructure
{
    /**
     * This command extends a value to an area in NV memory that was previously defined by TPM2_NV_DefineSpace.
     * 
     * @param _authHandle handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER 
     * @param _nvIndex the NV Index to extend Auth Index: None 
     * @param _data the data to extend
     */
    public TPM2_NV_Extend_REQUEST(TPM_HANDLE _authHandle,TPM_HANDLE _nvIndex,byte[] _data)
    {
        authHandle = _authHandle;
        nvIndex = _nvIndex;
        data = _data;
    }
    /**
    * This command extends a value to an area in NV memory that was previously defined by TPM2_NV_DefineSpace.
    */
    public TPM2_NV_Extend_REQUEST() {};
    /**
    * handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE authHandle;
    /**
    * the NV Index to extend Auth Index: None
    */
    public TPM_HANDLE nvIndex;
    /**
    * size of the buffer
    */
    // private short dataSize;
    /**
    * the data to extend
    */
    public byte[] data;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        authHandle.toTpm(buf);
        nvIndex.toTpm(buf);
        buf.writeInt((data!=null)?data.length:0, 2);
        if(data!=null)
            buf.write(data);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authHandle = TPM_HANDLE.fromTpm(buf);
        nvIndex = TPM_HANDLE.fromTpm(buf);
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
    public static TPM2_NV_Extend_REQUEST fromTpm (byte[] x) 
    {
        TPM2_NV_Extend_REQUEST ret = new TPM2_NV_Extend_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_NV_Extend_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_NV_Extend_REQUEST ret = new TPM2_NV_Extend_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_Extend_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
        _p.add(d, "byte", "data", data);
    };
    
    
};

//<<<


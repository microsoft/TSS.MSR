package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command reads a value from an area in NV memory previously defined by TPM2_NV_DefineSpace().
*/
public class TPM2_NV_Read_REQUEST extends TpmStructure
{
    /**
     * This command reads a value from an area in NV memory previously defined by TPM2_NV_DefineSpace().
     * 
     * @param _authHandle the handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER 
     * @param _nvIndex the NV Index to be read Auth Index: None 
     * @param _size number of octets to read 
     * @param _offset octet offset into the NV area This value shall be less than or equal to the size of the nvIndex data.
     */
    public TPM2_NV_Read_REQUEST(TPM_HANDLE _authHandle,TPM_HANDLE _nvIndex,int _size,int _offset)
    {
        authHandle = _authHandle;
        nvIndex = _nvIndex;
        size = (short)_size;
        offset = (short)_offset;
    }
    /**
    * This command reads a value from an area in NV memory previously defined by TPM2_NV_DefineSpace().
    */
    public TPM2_NV_Read_REQUEST() {};
    /**
    * the handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE authHandle;
    /**
    * the NV Index to be read Auth Index: None
    */
    public TPM_HANDLE nvIndex;
    /**
    * number of octets to read
    */
    public short size;
    /**
    * octet offset into the NV area This value shall be less than or equal to the size of the nvIndex data.
    */
    public short offset;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        authHandle.toTpm(buf);
        nvIndex.toTpm(buf);
        buf.write(size);
        buf.write(offset);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authHandle = TPM_HANDLE.fromTpm(buf);
        nvIndex = TPM_HANDLE.fromTpm(buf);
        size = (short) buf.readInt(2);
        offset = (short) buf.readInt(2);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_NV_Read_REQUEST fromTpm (byte[] x) 
    {
        TPM2_NV_Read_REQUEST ret = new TPM2_NV_Read_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_NV_Read_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_NV_Read_REQUEST ret = new TPM2_NV_Read_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_Read_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
        _p.add(d, "ushort", "size", size);
        _p.add(d, "ushort", "offset", offset);
    };
    
    
};

//<<<


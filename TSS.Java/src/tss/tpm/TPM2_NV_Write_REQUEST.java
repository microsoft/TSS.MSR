package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command writes a value to an area in NV memory that was previously defined by
 *  TPM2_NV_DefineSpace().
 */
public class TPM2_NV_Write_REQUEST extends TpmStructure
{
    /** Handle indicating the source of the authorization value
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    /** The NV Index of the area to write
     *  Auth Index: None
     */
    public TPM_HANDLE nvIndex;
    
    /** The data to write  */
    public byte[] data;
    
    /** The octet offset into the NV Area  */
    public short offset;
    
    public TPM2_NV_Write_REQUEST()
    {
        authHandle = new TPM_HANDLE();
        nvIndex = new TPM_HANDLE();
    }
    
    /** @param _authHandle Handle indicating the source of the authorization value
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _nvIndex The NV Index of the area to write
     *         Auth Index: None
     *  @param _data The data to write
     *  @param _offset The octet offset into the NV Area
     */
    public TPM2_NV_Write_REQUEST(TPM_HANDLE _authHandle, TPM_HANDLE _nvIndex, byte[] _data, int _offset)
    {
        authHandle = _authHandle;
        nvIndex = _nvIndex;
        data = _data;
        offset = (short)_offset;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(data);
        buf.writeShort(offset);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _dataSize = buf.readShort() & 0xFFFF;
        data = new byte[_dataSize];
        buf.readArrayOfInts(data, 1, _dataSize);
        offset = buf.readShort();
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_NV_Write_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_NV_Write_REQUEST ret = new TPM2_NV_Write_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_NV_Write_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_NV_Write_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_NV_Write_REQUEST ret = new TPM2_NV_Write_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_Write_REQUEST");
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
        _p.add(d, "short", "offset", offset);
    }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command reads a value from an area in NV memory previously defined by TPM2_NV_DefineSpace().  */
public class TPM2_NV_Read_REQUEST extends ReqStructure
{
    /** The handle indicating the source of the authorization value
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    /** The NV Index to be read
     *  Auth Index: None
     */
    public TPM_HANDLE nvIndex;
    
    /** Number of octets to read  */
    public int size;
    
    /** Octet offset into the NV area
     *  This value shall be less than or equal to the size of the nvIndex data.
     */
    public int offset;
    
    public TPM2_NV_Read_REQUEST()
    {
        authHandle = new TPM_HANDLE();
        nvIndex = new TPM_HANDLE();
    }
    
    /** @param _authHandle The handle indicating the source of the authorization value
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _nvIndex The NV Index to be read
     *         Auth Index: None
     *  @param _size Number of octets to read
     *  @param _offset Octet offset into the NV area
     *         This value shall be less than or equal to the size of the nvIndex data.
     */
    public TPM2_NV_Read_REQUEST(TPM_HANDLE _authHandle, TPM_HANDLE _nvIndex, int _size, int _offset)
    {
        authHandle = _authHandle;
        nvIndex = _nvIndex;
        size = (short)_size;
        offset = (short)_offset;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeShort(size);
        buf.writeShort(offset);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        size = buf.readShort();
        offset = buf.readShort();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_NV_Read_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_NV_Read_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_NV_Read_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_NV_Read_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_NV_Read_REQUEST.class);
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
        _p.add(d, "int", "size", size);
        _p.add(d, "int", "offset", offset);
    }

    @Override
    public int numHandles() { return 2; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {authHandle, nvIndex}; }
}

//<<<

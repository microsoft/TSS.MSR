package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command extends a value to an area in NV memory that was previously defined by
 *  TPM2_NV_DefineSpace.
 */
public class TPM2_NV_Extend_REQUEST extends ReqStructure
{
    /** Handle indicating the source of the authorization value
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    /** The NV Index to extend
     *  Auth Index: None
     */
    public TPM_HANDLE nvIndex;
    
    /** The data to extend  */
    public byte[] data;
    
    public TPM2_NV_Extend_REQUEST()
    {
        authHandle = new TPM_HANDLE();
        nvIndex = new TPM_HANDLE();
    }
    
    /** @param _authHandle Handle indicating the source of the authorization value
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _nvIndex The NV Index to extend
     *         Auth Index: None
     *  @param _data The data to extend
     */
    public TPM2_NV_Extend_REQUEST(TPM_HANDLE _authHandle, TPM_HANDLE _nvIndex, byte[] _data)
    {
        authHandle = _authHandle;
        nvIndex = _nvIndex;
        data = _data;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(data); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { data = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_NV_Extend_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_NV_Extend_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_NV_Extend_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_NV_Extend_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_NV_Extend_REQUEST.class);
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
    }

    @Override
    public int numHandles() { return 2; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {authHandle, nvIndex}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

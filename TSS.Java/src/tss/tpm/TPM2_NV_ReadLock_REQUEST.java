package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** If TPMA_NV_READ_STCLEAR is SET in an Index, then this command may be used to prevent
 *  further reads of the NV Index until the next TPM2_Startup (TPM_SU_CLEAR).
 */
public class TPM2_NV_ReadLock_REQUEST extends ReqStructure
{
    /** The handle indicating the source of the authorization value
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    /** The NV Index to be locked
     *  Auth Index: None
     */
    public TPM_HANDLE nvIndex;
    
    public TPM2_NV_ReadLock_REQUEST()
    {
        authHandle = new TPM_HANDLE();
        nvIndex = new TPM_HANDLE();
    }
    
    /** @param _authHandle The handle indicating the source of the authorization value
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _nvIndex The NV Index to be locked
     *         Auth Index: None
     */
    public TPM2_NV_ReadLock_REQUEST(TPM_HANDLE _authHandle, TPM_HANDLE _nvIndex)
    {
        authHandle = _authHandle;
        nvIndex = _nvIndex;
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_NV_ReadLock_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_NV_ReadLock_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_NV_ReadLock_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_NV_ReadLock_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_NV_ReadLock_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_ReadLock_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
    }

    @Override
    public int numHandles() { return 2; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {authHandle, nvIndex}; }
}

//<<<

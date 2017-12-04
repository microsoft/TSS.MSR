package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* If the TPMA_NV_WRITEDEFINE or TPMA_NV_WRITE_STCLEAR attributes of an NV location are SET, then this command may be used to inhibit further writes of the NV Index.
*/
public class TPM2_NV_WriteLock_REQUEST extends TpmStructure
{
    /**
     * If the TPMA_NV_WRITEDEFINE or TPMA_NV_WRITE_STCLEAR attributes of an NV location are SET, then this command may be used to inhibit further writes of the NV Index.
     * 
     * @param _authHandle handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER 
     * @param _nvIndex the NV Index of the area to lock Auth Index: None
     */
    public TPM2_NV_WriteLock_REQUEST(TPM_HANDLE _authHandle,TPM_HANDLE _nvIndex)
    {
        authHandle = _authHandle;
        nvIndex = _nvIndex;
    }
    /**
    * If the TPMA_NV_WRITEDEFINE or TPMA_NV_WRITE_STCLEAR attributes of an NV location are SET, then this command may be used to inhibit further writes of the NV Index.
    */
    public TPM2_NV_WriteLock_REQUEST() {};
    /**
    * handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE authHandle;
    /**
    * the NV Index of the area to lock Auth Index: None
    */
    public TPM_HANDLE nvIndex;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        authHandle.toTpm(buf);
        nvIndex.toTpm(buf);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        authHandle = TPM_HANDLE.fromTpm(buf);
        nvIndex = TPM_HANDLE.fromTpm(buf);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static TPM2_NV_WriteLock_REQUEST fromTpm (byte[] x) 
    {
        TPM2_NV_WriteLock_REQUEST ret = new TPM2_NV_WriteLock_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_NV_WriteLock_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_NV_WriteLock_REQUEST ret = new TPM2_NV_WriteLock_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_WriteLock_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
    };
    
    
};

//<<<


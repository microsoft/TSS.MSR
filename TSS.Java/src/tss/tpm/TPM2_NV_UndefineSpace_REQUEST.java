package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This command removes an Index from the TPM.
*/
public class TPM2_NV_UndefineSpace_REQUEST extends TpmStructure
{
    /**
     * This command removes an Index from the TPM.
     * 
     * @param _authHandle TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER 
     * @param _nvIndex the NV Index to remove from NV space Auth Index: None
     */
    public TPM2_NV_UndefineSpace_REQUEST(TPM_HANDLE _authHandle,TPM_HANDLE _nvIndex)
    {
        authHandle = _authHandle;
        nvIndex = _nvIndex;
    }
    /**
    * This command removes an Index from the TPM.
    */
    public TPM2_NV_UndefineSpace_REQUEST() {};
    /**
    * TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER
    */
    public TPM_HANDLE authHandle;
    /**
    * the NV Index to remove from NV space Auth Index: None
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
    public static TPM2_NV_UndefineSpace_REQUEST fromTpm (byte[] x) 
    {
        TPM2_NV_UndefineSpace_REQUEST ret = new TPM2_NV_UndefineSpace_REQUEST();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static TPM2_NV_UndefineSpace_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_NV_UndefineSpace_REQUEST ret = new TPM2_NV_UndefineSpace_REQUEST();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_UndefineSpace_REQUEST");
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


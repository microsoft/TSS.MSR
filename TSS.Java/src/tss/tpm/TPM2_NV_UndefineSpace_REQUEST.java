package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command removes an Index from the TPM. */
public class TPM2_NV_UndefineSpace_REQUEST extends TpmStructure
{
    /**
     *  TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    /**
     *  the NV Index to remove from NV space
     *  Auth Index: None
     */
    public TPM_HANDLE nvIndex;
    
    public TPM2_NV_UndefineSpace_REQUEST()
    {
        authHandle = new TPM_HANDLE();
        nvIndex = new TPM_HANDLE();
    }

    /**
     *  @param _authHandle TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _nvIndex the NV Index to remove from NV space
     *         Auth Index: None
     */
    public TPM2_NV_UndefineSpace_REQUEST(TPM_HANDLE _authHandle, TPM_HANDLE _nvIndex)
    {
        authHandle = _authHandle;
        nvIndex = _nvIndex;
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
    }
}

//<<<

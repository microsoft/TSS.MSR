package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command provides a capability that is the equivalent of a revocable policy. With
 *  TPM2_PolicyAuthorize(), the authorization ticket never expires, so the authorization may
 *  not be withdrawn. With this command, the approved policy is kept in an NV Index location
 *  so that the policy may be changed as needed to render the old policy unusable.
 */
public class TPM2_PolicyAuthorizeNV_REQUEST extends TpmStructure
{
    /**
     *  handle indicating the source of the authorization value
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    /**
     *  the NV Index of the area to read
     *  Auth Index: None
     */
    public TPM_HANDLE nvIndex;
    
    /**
     *  handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;
    
    public TPM2_PolicyAuthorizeNV_REQUEST()
    {
        authHandle = new TPM_HANDLE();
        nvIndex = new TPM_HANDLE();
        policySession = new TPM_HANDLE();
    }

    /**
     *  @param _authHandle handle indicating the source of the authorization value
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _nvIndex the NV Index of the area to read
     *         Auth Index: None
     *  @param _policySession handle for the policy session being extended
     *         Auth Index: None
     */
    public TPM2_PolicyAuthorizeNV_REQUEST(TPM_HANDLE _authHandle, TPM_HANDLE _nvIndex, TPM_HANDLE _policySession)
    {
        authHandle = _authHandle;
        nvIndex = _nvIndex;
        policySession = _policySession;
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyAuthorizeNV_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
        _p.add(d, "TPM_HANDLE", "nvIndex", nvIndex);
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
    }
}

//<<<

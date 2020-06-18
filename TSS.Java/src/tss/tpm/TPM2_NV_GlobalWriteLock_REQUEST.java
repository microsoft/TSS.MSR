package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** The command will SET TPMA_NV_WRITELOCKED for all indexes that have their
 *  TPMA_NV_GLOBALLOCK attribute SET.
 */
public class TPM2_NV_GlobalWriteLock_REQUEST extends TpmStructure
{
    /** TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    public TPM2_NV_GlobalWriteLock_REQUEST() { authHandle = new TPM_HANDLE(); }
    
    /** @param _authHandle TPM_RH_OWNER or TPM_RH_PLATFORM+{PP}
     *         Auth Index: 1
     *         Auth Role: USER
     */
    public TPM2_NV_GlobalWriteLock_REQUEST(TPM_HANDLE _authHandle) { authHandle = _authHandle; }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_NV_GlobalWriteLock_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
    }
}

//<<<

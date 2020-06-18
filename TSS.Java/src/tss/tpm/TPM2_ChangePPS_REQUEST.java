package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This replaces the current platform primary seed (PPS) with a value from the RNG and
 *  sets platformPolicy to the default initialization value (the Empty Buffer).
 */
public class TPM2_ChangePPS_REQUEST extends TpmStructure
{
    /** TPM_RH_PLATFORM+{PP}
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    public TPM2_ChangePPS_REQUEST() { authHandle = new TPM_HANDLE(); }
    
    /** @param _authHandle TPM_RH_PLATFORM+{PP}
     *         Auth Index: 1
     *         Auth Role: USER
     */
    public TPM2_ChangePPS_REQUEST(TPM_HANDLE _authHandle) { authHandle = _authHandle; }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ChangePPS_REQUEST");
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

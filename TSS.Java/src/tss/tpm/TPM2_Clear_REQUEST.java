package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command removes all TPM context associated with a specific Owner. */
public class TPM2_Clear_REQUEST extends TpmStructure
{
    /**
     *  TPM_RH_LOCKOUT or TPM_RH_PLATFORM+{PP}
     *  Auth Handle: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;
    
    public TPM2_Clear_REQUEST() { authHandle = new TPM_HANDLE(); }
    
    /**
     *  @param _authHandle TPM_RH_LOCKOUT or TPM_RH_PLATFORM+{PP}
     *         Auth Handle: 1
     *         Auth Role: USER
     */
    public TPM2_Clear_REQUEST(TPM_HANDLE _authHandle) { authHandle = _authHandle; }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_Clear_REQUEST");
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

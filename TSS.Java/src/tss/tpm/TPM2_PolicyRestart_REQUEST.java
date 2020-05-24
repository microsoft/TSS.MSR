package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command allows a policy authorization session to be returned to its initial state.
 *  This command is used after the TPM returns TPM_RC_PCR_CHANGED. That response code
 *  indicates that a policy will fail because the PCR have changed after TPM2_PolicyPCR() was
 *  executed. Restarting the session allows the authorizations to be replayed because the
 *  session restarts with the same nonceTPM. If the PCR are valid for the policy,
 *  the policy may then succeed.
 */
public class TPM2_PolicyRestart_REQUEST extends TpmStructure
{
    /** the handle for the policy session */
    public TPM_HANDLE sessionHandle;
    
    public TPM2_PolicyRestart_REQUEST() { sessionHandle = new TPM_HANDLE(); }
    
    /** @param _sessionHandle the handle for the policy session */
    public TPM2_PolicyRestart_REQUEST(TPM_HANDLE _sessionHandle) { sessionHandle = _sessionHandle; }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyRestart_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "sessionHandle", sessionHandle);
    }
}

//<<<

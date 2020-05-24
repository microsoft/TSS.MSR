package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  This command indicates that physical presence will need to be asserted at the time
 *  the authorization is performed.
 */
public class TPM2_PolicyPhysicalPresence_REQUEST extends TpmStructure
{
    /**
     *  handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;
    
    public TPM2_PolicyPhysicalPresence_REQUEST() { policySession = new TPM_HANDLE(); }
    
    /**
     *  @param _policySession handle for the policy session being extended
     *         Auth Index: None
     */
    public TPM2_PolicyPhysicalPresence_REQUEST(TPM_HANDLE _policySession) { policySession = _policySession; }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyPhysicalPresence_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
    }
}

//<<<

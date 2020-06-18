package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command cancels the effect of a TPM lockout due to a number of successive
 *  authorization failures. If this command is properly authorized, the lockout counter is
 *  set to zero.
 */
public class TPM2_DictionaryAttackLockReset_REQUEST extends TpmStructure
{
    /** TPM_RH_LOCKOUT
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE lockHandle;
    
    public TPM2_DictionaryAttackLockReset_REQUEST() { lockHandle = new TPM_HANDLE(); }
    
    /** @param _lockHandle TPM_RH_LOCKOUT
     *         Auth Index: 1
     *         Auth Role: USER
     */
    public TPM2_DictionaryAttackLockReset_REQUEST(TPM_HANDLE _lockHandle) { lockHandle = _lockHandle; }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_DictionaryAttackLockReset_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "lockHandle", lockHandle);
    }
}

//<<<

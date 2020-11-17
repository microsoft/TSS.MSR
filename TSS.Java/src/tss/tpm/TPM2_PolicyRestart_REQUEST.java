package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows a policy authorization session to be returned to its initial
 *  state. This command is used after the TPM returns TPM_RC_PCR_CHANGED. That response
 *  code indicates that a policy will fail because the PCR have changed after
 *  TPM2_PolicyPCR() was executed. Restarting the session allows the authorizations to be
 *  replayed because the session restarts with the same nonceTPM. If the PCR are valid for
 *  the policy, the policy may then succeed.
 */
public class TPM2_PolicyRestart_REQUEST extends ReqStructure
{
    /** The handle for the policy session */
    public TPM_HANDLE sessionHandle;

    public TPM2_PolicyRestart_REQUEST() { sessionHandle = new TPM_HANDLE(); }

    /** @param _sessionHandle The handle for the policy session */
    public TPM2_PolicyRestart_REQUEST(TPM_HANDLE _sessionHandle) { sessionHandle = _sessionHandle; }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_PolicyRestart_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PolicyRestart_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_PolicyRestart_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_PolicyRestart_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PolicyRestart_REQUEST.class);
    }

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

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 0; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {sessionHandle}; }
}

//<<<

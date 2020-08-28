package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This replaces the current endorsement primary seed (EPS) with a value from the RNG and
 *  sets the Endorsement hierarchy controls to their default initialization values:
 *  ehEnable is SET, endorsementAuth and endorsementPolicy are both set to the Empty
 *  Buffer. It will flush any resident objects (transient or persistent) in the
 *  Endorsement hierarchy and not allow objects in the hierarchy associated with the
 *  previous EPS to be loaded.
 */
public class TPM2_ChangeEPS_REQUEST extends ReqStructure
{
    /** TPM_RH_PLATFORM+{PP}
     *  Auth Handle: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE authHandle;

    public TPM2_ChangeEPS_REQUEST() { authHandle = new TPM_HANDLE(); }

    /** @param _authHandle TPM_RH_PLATFORM+{PP}
     *         Auth Handle: 1
     *         Auth Role: USER
     */
    public TPM2_ChangeEPS_REQUEST(TPM_HANDLE _authHandle) { authHandle = _authHandle; }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_ChangeEPS_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_ChangeEPS_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_ChangeEPS_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_ChangeEPS_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_ChangeEPS_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ChangeEPS_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "authHandle", authHandle);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {authHandle}; }
}

//<<<

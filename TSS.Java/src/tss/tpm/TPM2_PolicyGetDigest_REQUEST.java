package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command returns the current policyDigest of the session. This command allows the
 *  TPM to be used to perform the actions required to pre-compute the authPolicy for an object.
 */
public class TPM2_PolicyGetDigest_REQUEST extends ReqStructure
{
    /** Handle for the policy session
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;
    
    public TPM2_PolicyGetDigest_REQUEST() { policySession = new TPM_HANDLE(); }
    
    /** @param _policySession Handle for the policy session
     *         Auth Index: None
     */
    public TPM2_PolicyGetDigest_REQUEST(TPM_HANDLE _policySession) { policySession = _policySession; }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_PolicyGetDigest_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PolicyGetDigest_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PolicyGetDigest_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_PolicyGetDigest_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PolicyGetDigest_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyGetDigest_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 0; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {policySession}; }
}

//<<<

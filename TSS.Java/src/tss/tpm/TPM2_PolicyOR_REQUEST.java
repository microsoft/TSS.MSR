package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows options in authorizations without requiring that the TPM evaluate
 *  all of the options. If a policy may be satisfied by different sets of conditions, the
 *  TPM need only evaluate one set that satisfies the policy. This command will indicate
 *  that one of the required sets of conditions has been satisfied.
 */
public class TPM2_PolicyOR_REQUEST extends ReqStructure
{
    /** Handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;
    
    /** The list of hashes to check for a match  */
    public TPM2B_DIGEST[] pHashList;
    
    public TPM2_PolicyOR_REQUEST() { policySession = new TPM_HANDLE(); }
    
    /** @param _policySession Handle for the policy session being extended
     *         Auth Index: None
     *  @param _pHashList The list of hashes to check for a match
     */
    public TPM2_PolicyOR_REQUEST(TPM_HANDLE _policySession, TPM2B_DIGEST[] _pHashList)
    {
        policySession = _policySession;
        pHashList = _pHashList;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(pHashList); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { pHashList = buf.readObjArr(TPM2B_DIGEST.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_PolicyOR_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PolicyOR_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PolicyOR_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_PolicyOR_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PolicyOR_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyOR_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "TPM2B_DIGEST", "pHashList", pHashList);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 0; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {policySession}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(4, 2); }
}

//<<<

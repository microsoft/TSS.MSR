package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command indicates that physical presence will need to be asserted at the time the
 *  authorization is performed.
 */
public class TPM2_PolicyPhysicalPresence_REQUEST extends ReqStructure
{
    /** Handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;
    
    public TPM2_PolicyPhysicalPresence_REQUEST() { policySession = new TPM_HANDLE(); }
    
    /** @param _policySession Handle for the policy session being extended
     *         Auth Index: None
     */
    public TPM2_PolicyPhysicalPresence_REQUEST(TPM_HANDLE _policySession) { policySession = _policySession; }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_PolicyPhysicalPresence_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PolicyPhysicalPresence_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PolicyPhysicalPresence_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_PolicyPhysicalPresence_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PolicyPhysicalPresence_REQUEST.class);
    }
    
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

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 0; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {policySession}; }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command allows a policy to be bound to the TPMA_NV_WRITTEN attributes. This is a
 *  deferred assertion. Values are stored in the policy session context and checked when
 *  the policy is used for authorization.
 */
public class TPM2_PolicyNvWritten_REQUEST extends ReqStructure
{
    /** Handle for the policy session being extended
     *  Auth Index: None
     */
    public TPM_HANDLE policySession;
    
    /** YES if NV Index is required to have been written
     *  NO if NV Index is required not to have been written
     */
    public byte writtenSet;
    
    public TPM2_PolicyNvWritten_REQUEST() { policySession = new TPM_HANDLE(); }
    
    /** @param _policySession Handle for the policy session being extended
     *         Auth Index: None
     *  @param _writtenSet YES if NV Index is required to have been written
     *         NO if NV Index is required not to have been written
     */
    public TPM2_PolicyNvWritten_REQUEST(TPM_HANDLE _policySession, byte _writtenSet)
    {
        policySession = _policySession;
        writtenSet = _writtenSet;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeByte(writtenSet); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { writtenSet = buf.readByte(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_PolicyNvWritten_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PolicyNvWritten_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PolicyNvWritten_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_PolicyNvWritten_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PolicyNvWritten_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PolicyNvWritten_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "policySession", policySession);
        _p.add(d, "byte", "writtenSet", writtenSet);
    }

    @Override
    public int numHandles() { return 1; }
    
    public int numAuthHandles() { return 0; }
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {policySession}; }
}

//<<<

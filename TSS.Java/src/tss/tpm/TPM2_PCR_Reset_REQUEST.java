package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** If the attribute of a PCR allows the PCR to be reset and proper authorization is
 *  provided, then this command may be used to set the PCR in all banks to zero. The
 *  attributes of the PCR may restrict the locality that can perform the reset operation.
 */
public class TPM2_PCR_Reset_REQUEST extends ReqStructure
{
    /** The PCR to reset
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE pcrHandle;
    
    public TPM2_PCR_Reset_REQUEST() { pcrHandle = new TPM_HANDLE(); }
    
    /** @param _pcrHandle The PCR to reset
     *         Auth Index: 1
     *         Auth Role: USER
     */
    public TPM2_PCR_Reset_REQUEST(TPM_HANDLE _pcrHandle) { pcrHandle = _pcrHandle; }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_PCR_Reset_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PCR_Reset_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PCR_Reset_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_PCR_Reset_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PCR_Reset_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_Reset_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "pcrHandle", pcrHandle);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {pcrHandle}; }
}

//<<<

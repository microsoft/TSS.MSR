package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to cause an update to the indicated PCR. The digests parameter
 *  contains one or more tagged digest values identified by an algorithm ID. For each
 *  digest, the PCR associated with pcrHandle is Extended into the bank identified by the
 *  tag (hashAlg).
 */
public class TPM2_PCR_Extend_REQUEST extends TpmStructure
{
    /** Handle of the PCR
     *  Auth Handle: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE pcrHandle;
    
    /** List of tagged digest values to be extended  */
    public TPMT_HA[] digests;
    
    public TPM2_PCR_Extend_REQUEST() { pcrHandle = new TPM_HANDLE(); }
    
    /** @param _pcrHandle Handle of the PCR
     *         Auth Handle: 1
     *         Auth Role: USER
     *  @param _digests List of tagged digest values to be extended
     */
    public TPM2_PCR_Extend_REQUEST(TPM_HANDLE _pcrHandle, TPMT_HA[] _digests)
    {
        pcrHandle = _pcrHandle;
        digests = _digests;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(digests); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { digests = buf.readObjArr(TPMT_HA.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_PCR_Extend_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_PCR_Extend_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_PCR_Extend_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_PCR_Extend_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_PCR_Extend_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_PCR_Extend_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "pcrHandle", pcrHandle);
        _p.add(d, "TPMT_HA", "digests", digests);
    }
}

//<<<

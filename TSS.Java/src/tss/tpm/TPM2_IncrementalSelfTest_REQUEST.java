package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command causes the TPM to perform a test of the selected algorithms.  */
public class TPM2_IncrementalSelfTest_REQUEST extends ReqStructure
{
    /** List of algorithms that should be tested  */
    public TPM_ALG_ID[] toTest;
    
    public TPM2_IncrementalSelfTest_REQUEST() {}
    
    /** @param _toTest List of algorithms that should be tested  */
    public TPM2_IncrementalSelfTest_REQUEST(TPM_ALG_ID[] _toTest) { toTest = _toTest; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(toTest); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { toTest = buf.readObjArr(TPM_ALG_ID.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_IncrementalSelfTest_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_IncrementalSelfTest_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_IncrementalSelfTest_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_IncrementalSelfTest_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_IncrementalSelfTest_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_IncrementalSelfTest_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "toTest", toTest);
    }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(4, 2); }
}

//<<<

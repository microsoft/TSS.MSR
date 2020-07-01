package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command causes the TPM to perform a test of its capabilities. If the fullTest is
 *  YES, the TPM will test all functions. If fullTest = NO, the TPM will only test those
 *  functions that have not previously been tested.
 */
public class TPM2_SelfTest_REQUEST extends ReqStructure
{
    /** YES if full test to be performed
     *  NO if only test of untested functions required
     */
    public byte fullTest;
    
    public TPM2_SelfTest_REQUEST() {}
    
    /** @param _fullTest YES if full test to be performed
     *         NO if only test of untested functions required
     */
    public TPM2_SelfTest_REQUEST(byte _fullTest) { fullTest = _fullTest; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeByte(fullTest); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { fullTest = buf.readByte(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_SelfTest_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_SelfTest_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_SelfTest_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_SelfTest_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_SelfTest_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_SelfTest_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "fullTest", fullTest);
    }
}

//<<<

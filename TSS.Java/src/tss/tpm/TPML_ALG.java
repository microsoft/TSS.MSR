package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This list is returned by TPM2_IncrementalSelfTest().  */
public class TPML_ALG extends TpmStructure
{
    /** A list of algorithm IDs
     *  The maximum only applies to an algorithm list in a command. The response size is
     *  limited only by the size of the parameter buffer.
     */
    public TPM_ALG_ID[] algorithms;
    
    public TPML_ALG() {}
    
    /** @param _algorithms A list of algorithm IDs
     *         The maximum only applies to an algorithm list in a command. The response size is
     *         limited only by the size of the parameter buffer.
     */
    public TPML_ALG(TPM_ALG_ID[] _algorithms) { algorithms = _algorithms; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeObjArr(algorithms); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { algorithms = buf.readObjArr(TPM_ALG_ID.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPML_ALG fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPML_ALG.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPML_ALG fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPML_ALG fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPML_ALG.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPML_ALG");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ALG_ID", "algorithms", algorithms);
    }
}

//<<<

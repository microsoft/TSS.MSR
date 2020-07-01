package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure contains the parameters for a symmetric block cipher object.  */
public class TPMS_SYMCIPHER_PARMS extends TpmStructure implements TPMU_PUBLIC_PARMS
{
    /** A symmetric block cipher  */
    public TPMT_SYM_DEF_OBJECT sym;
    
    public TPMS_SYMCIPHER_PARMS() {}
    
    /** @param _sym A symmetric block cipher  */
    public TPMS_SYMCIPHER_PARMS(TPMT_SYM_DEF_OBJECT _sym) { sym = _sym; }
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.SYMCIPHER; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { sym.toTpm(buf); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { sym = TPMT_SYM_DEF_OBJECT.fromTpm(buf); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_SYMCIPHER_PARMS fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_SYMCIPHER_PARMS.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_SYMCIPHER_PARMS fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_SYMCIPHER_PARMS fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_SYMCIPHER_PARMS.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SYMCIPHER_PARMS");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMT_SYM_DEF_OBJECT", "sym", sym);
    }
}

//<<<

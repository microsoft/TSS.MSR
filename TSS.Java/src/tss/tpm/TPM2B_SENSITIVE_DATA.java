package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This buffer wraps the TPMU_SENSITIVE_CREATE structure.  */
public class TPM2B_SENSITIVE_DATA extends TpmStructure implements TPMU_SENSITIVE_COMPOSITE
{
    /** Symmetric data for a created object or the label and context for a derived object  */
    public byte[] buffer;
    
    public TPM2B_SENSITIVE_DATA() {}
    
    /** @param _buffer Symmetric data for a created object or the label and context for a
     *  derived object
     */
    public TPM2B_SENSITIVE_DATA(byte[] _buffer) { buffer = _buffer; }
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.KEYEDHASH; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(buffer); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { buffer = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_SENSITIVE_DATA fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_SENSITIVE_DATA.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_SENSITIVE_DATA fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_SENSITIVE_DATA fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_SENSITIVE_DATA.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_SENSITIVE_DATA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte[]", "buffer", buffer);
    }
}

//<<<

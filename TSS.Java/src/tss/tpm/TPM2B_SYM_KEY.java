package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used to hold a symmetric key in the sensitive area of an asymmetric object.  */
public class TPM2B_SYM_KEY extends TpmStructure implements TPMU_SENSITIVE_COMPOSITE
{
    /** The key  */
    public byte[] buffer;
    
    public TPM2B_SYM_KEY() {}
    
    /** @param _buffer The key  */
    public TPM2B_SYM_KEY(byte[] _buffer) { buffer = _buffer; }
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.SYMCIPHER; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(buffer); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { buffer = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_SYM_KEY fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_SYM_KEY.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_SYM_KEY fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_SYM_KEY fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_SYM_KEY.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_SYM_KEY");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "buffer", buffer);
    }
}

//<<<

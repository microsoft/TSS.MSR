package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This sized buffer is used to embed a TPMT_TEMPLATE for TPM2_CreateLoaded().  */
public class TPM2B_TEMPLATE extends TpmStructure
{
    /** The public area  */
    public byte[] buffer;
    
    public TPM2B_TEMPLATE() {}
    
    /** @param _buffer The public area  */
    public TPM2B_TEMPLATE(byte[] _buffer) { buffer = _buffer; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(buffer); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { buffer = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_TEMPLATE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_TEMPLATE.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_TEMPLATE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_TEMPLATE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_TEMPLATE.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_TEMPLATE");
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

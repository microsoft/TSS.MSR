package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This type is a sized buffer that can hold a maximally sized buffer for NV data
 *  commands such as TPM2_NV_Read(), TPM2_NV_Write(), and TPM2_NV_Certify().
 */
public class TPM2B_MAX_NV_BUFFER extends TpmStructure
{
    /** The operand
     *  NOTE MAX_NV_BUFFER_SIZE is TPM-dependent
     */
    public byte[] buffer;
    
    public TPM2B_MAX_NV_BUFFER() {}
    
    /** @param _buffer The operand
     *         NOTE MAX_NV_BUFFER_SIZE is TPM-dependent
     */
    public TPM2B_MAX_NV_BUFFER(byte[] _buffer) { buffer = _buffer; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(buffer); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { buffer = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_MAX_NV_BUFFER fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_MAX_NV_BUFFER.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_MAX_NV_BUFFER fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_MAX_NV_BUFFER fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_MAX_NV_BUFFER.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_MAX_NV_BUFFER");
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

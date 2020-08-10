package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used for passing an initial value for a symmetric block cipher to or
 *  from the TPM. The size is set to be the largest block size of any implemented
 *  symmetric cipher implemented on the TPM.
 */
public class TPM2B_IV extends TpmStructure
{
    /** The IV value  */
    public byte[] buffer;
    
    public TPM2B_IV() {}
    
    /** @param _buffer The IV value  */
    public TPM2B_IV(byte[] _buffer) { buffer = _buffer; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(buffer); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { buffer = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_IV fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_IV.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_IV fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_IV fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_IV.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_IV");
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

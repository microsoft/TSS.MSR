package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This TPM-dependent structure is used to provide the timeout value for an
 *  authorization. The size shall be 8 or less.
 */
public class TPM2B_TIMEOUT extends TpmStructure
{
    /** The timeout value  */
    public byte[] buffer;
    
    public TPM2B_TIMEOUT() {}
    
    /** @param _buffer The timeout value  */
    public TPM2B_TIMEOUT(byte[] _buffer) { buffer = _buffer; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedByteBuf(buffer); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { buffer = buf.readSizedByteBuf(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_TIMEOUT fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_TIMEOUT.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_TIMEOUT fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_TIMEOUT fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_TIMEOUT.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_TIMEOUT");
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

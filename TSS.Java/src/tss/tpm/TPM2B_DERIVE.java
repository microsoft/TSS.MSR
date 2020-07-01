package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 147 Definition of TPM2B_DERIVE Structure  */
public class TPM2B_DERIVE extends TpmStructure
{
    /** Symmetric data for a created object or the label and context for a derived object  */
    public TPMS_DERIVE buffer;
    
    public TPM2B_DERIVE() {}
    
    /** @param _buffer Symmetric data for a created object or the label and context for a
     *  derived object
     */
    public TPM2B_DERIVE(TPMS_DERIVE _buffer) { buffer = _buffer; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedObj(buffer); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { buffer = buf.createSizedObj(TPMS_DERIVE.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_DERIVE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_DERIVE.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_DERIVE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_DERIVE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_DERIVE.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_DERIVE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_DERIVE", "buffer", buffer);
    }
}

//<<<

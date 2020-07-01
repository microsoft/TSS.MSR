package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This structure is used in a TPMS_CONTEXT.  */
public class TPM2B_CONTEXT_DATA extends TpmStructure
{
    public TPMS_CONTEXT_DATA buffer;
    
    public TPM2B_CONTEXT_DATA() {}
    
    /** @param _buffer TBD  */
    public TPM2B_CONTEXT_DATA(TPMS_CONTEXT_DATA _buffer) { buffer = _buffer; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { buf.writeSizedObj(buffer); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { buffer = buf.createSizedObj(TPMS_CONTEXT_DATA.class); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2B_CONTEXT_DATA fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2B_CONTEXT_DATA.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2B_CONTEXT_DATA fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2B_CONTEXT_DATA fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2B_CONTEXT_DATA.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2B_CONTEXT_DATA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_CONTEXT_DATA", "buffer", buffer);
    }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command saves a session context, object context, or sequence object context
 *  outside the TPM.
 */
public class ContextSaveResponse extends TpmStructure
{
    public TPMS_CONTEXT context;
    
    public ContextSaveResponse() {}
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { context.toTpm(buf); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { context = TPMS_CONTEXT.fromTpm(buf); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static ContextSaveResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(ContextSaveResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static ContextSaveResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static ContextSaveResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(ContextSaveResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ContextSave_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPMS_CONTEXT", "context", context);
    }
}

//<<<

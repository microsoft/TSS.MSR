package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to reload a context that has been saved by TPM2_ContextSave().  */
public class TPM2_ContextLoad_REQUEST extends ReqStructure
{
    /** The context blob  */
    public TPMS_CONTEXT context;

    public TPM2_ContextLoad_REQUEST() {}

    /** @param _context The context blob  */
    public TPM2_ContextLoad_REQUEST(TPMS_CONTEXT _context) { context = _context; }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { context.toTpm(buf); }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { context = TPMS_CONTEXT.fromTpm(buf); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_ContextLoad_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_ContextLoad_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_ContextLoad_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_ContextLoad_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_ContextLoad_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ContextLoad_REQUEST");
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

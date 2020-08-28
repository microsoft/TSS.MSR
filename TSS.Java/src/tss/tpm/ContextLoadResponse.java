package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command is used to reload a context that has been saved by TPM2_ContextSave().  */
public class ContextLoadResponse extends RespStructure
{
    /** The handle assigned to the resource after it has been successfully loaded  */
    public TPM_HANDLE handle;

    public ContextLoadResponse() { handle = new TPM_HANDLE(); }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static ContextLoadResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(ContextLoadResponse.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static ContextLoadResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static ContextLoadResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(ContextLoadResponse.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("ContextLoadResponse");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public TPM_HANDLE getHandle() { return handle; }

    @Override
    public void setHandle(TPM_HANDLE h) { handle = h; }
}

//<<<

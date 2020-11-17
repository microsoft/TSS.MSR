package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command saves a session context, object context, or sequence object context
 *  outside the TPM.
 */
public class TPM2_ContextSave_REQUEST extends ReqStructure
{
    /** Handle of the resource to save
     *  Auth Index: None
     */
    public TPM_HANDLE saveHandle;

    public TPM2_ContextSave_REQUEST() { saveHandle = new TPM_HANDLE(); }

    /** @param _saveHandle Handle of the resource to save
     *         Auth Index: None
     */
    public TPM2_ContextSave_REQUEST(TPM_HANDLE _saveHandle) { saveHandle = _saveHandle; }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_ContextSave_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_ContextSave_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_ContextSave_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_ContextSave_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_ContextSave_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_ContextSave_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "saveHandle", saveHandle);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 0; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {saveHandle}; }
}

//<<<

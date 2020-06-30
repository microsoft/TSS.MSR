package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command saves a session context, object context, or sequence object context
 *  outside the TPM.
 */
public class TPM2_ContextSave_REQUEST extends TpmStructure
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
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_ContextSave_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_ContextSave_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_ContextSave_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
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
}

//<<<

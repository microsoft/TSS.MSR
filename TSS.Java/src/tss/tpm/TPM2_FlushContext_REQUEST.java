package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command causes all context associated with a loaded object, sequence object, or
 *  session to be removed from TPM memory.
 */
public class TPM2_FlushContext_REQUEST extends ReqStructure
{
    /** The handle of the item to flush
     *  NOTE This is a use of a handle as a parameter.
     */
    public TPM_HANDLE flushHandle;
    
    public TPM2_FlushContext_REQUEST() { flushHandle = new TPM_HANDLE(); }
    
    /** @param _flushHandle The handle of the item to flush
     *         NOTE This is a use of a handle as a parameter.
     */
    public TPM2_FlushContext_REQUEST(TPM_HANDLE _flushHandle) { flushHandle = _flushHandle; }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf) { flushHandle.toTpm(buf); }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf) { flushHandle = TPM_HANDLE.fromTpm(buf); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_FlushContext_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_FlushContext_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_FlushContext_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_FlushContext_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_FlushContext_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_FlushContext_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "flushHandle", flushHandle);
    }
}

//<<<

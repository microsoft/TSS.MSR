package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command causes all context associated with a loaded object, sequence object, or
 *  session to be removed from TPM memory.
 */
public class TPM2_FlushContext_REQUEST extends TpmStructure
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
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        flushHandle.toTpm(buf);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        flushHandle = TPM_HANDLE.fromTpm(buf);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_FlushContext_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_FlushContext_REQUEST ret = new TPM2_FlushContext_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_FlushContext_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_FlushContext_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_FlushContext_REQUEST ret = new TPM2_FlushContext_REQUEST();
        ret.initFromTpm(buf);
        return ret;
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

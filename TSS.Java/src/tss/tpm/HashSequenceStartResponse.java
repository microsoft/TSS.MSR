package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command starts a hash or an Event Sequence. If hashAlg is an implemented hash,
 *  then a hash sequence is started. If hashAlg is TPM_ALG_NULL, then an Event Sequence is
 *  started. If hashAlg is neither an implemented algorithm nor TPM_ALG_NULL, then the TPM
 *  shall return TPM_RC_HASH.
 */
public class HashSequenceStartResponse extends RespStructure
{
    /** A handle to reference the sequence  */
    public TPM_HANDLE handle;
    
    public HashSequenceStartResponse() { handle = new TPM_HANDLE(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static HashSequenceStartResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(HashSequenceStartResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static HashSequenceStartResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static HashSequenceStartResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(HashSequenceStartResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_HashSequenceStart_RESPONSE");
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
    
    public TPM_HANDLE getHandle() { return handle; }
    public void setHandle(TPM_HANDLE h) { handle = h; }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command starts an HMAC sequence. The TPM will create and initialize an HMAC
 *  sequence structure, assign a handle to the sequence, and set the authValue of the
 *  sequence object to the value in auth.
 */
public class HMAC_StartResponse extends RespStructure
{
    /** A handle to reference the sequence  */
    public TPM_HANDLE handle;
    
    public HMAC_StartResponse() { handle = new TPM_HANDLE(); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static HMAC_StartResponse fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(HMAC_StartResponse.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static HMAC_StartResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static HMAC_StartResponse fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(HMAC_StartResponse.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_HMAC_Start_RESPONSE");
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

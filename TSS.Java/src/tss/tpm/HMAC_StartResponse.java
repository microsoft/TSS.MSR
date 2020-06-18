package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command starts an HMAC sequence. The TPM will create and initialize an HMAC
 *  sequence structure, assign a handle to the sequence, and set the authValue of the
 *  sequence object to the value in auth.
 */
public class HMAC_StartResponse extends TpmStructure
{
    /** A handle to reference the sequence  */
    public TPM_HANDLE handle;
    
    public HMAC_StartResponse() { handle = new TPM_HANDLE(); }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        handle.toTpm(buf);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static HMAC_StartResponse fromBytes (byte[] byteBuf) 
    {
        HMAC_StartResponse ret = new HMAC_StartResponse();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static HMAC_StartResponse fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static HMAC_StartResponse fromTpm (InByteBuf buf) 
    {
        HMAC_StartResponse ret = new HMAC_StartResponse();
        ret.initFromTpm(buf);
        return ret;
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
}

//<<<

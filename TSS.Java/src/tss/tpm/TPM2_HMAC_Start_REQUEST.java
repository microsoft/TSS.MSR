package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command starts an HMAC sequence. The TPM will create and initialize an HMAC
 *  sequence structure, assign a handle to the sequence, and set the authValue of the
 *  sequence object to the value in auth.
 */
public class TPM2_HMAC_Start_REQUEST extends ReqStructure
{
    /** Handle of an HMAC key
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE handle;
    
    /** Authorization value for subsequent use of the sequence  */
    public byte[] auth;
    
    /** The hash algorithm to use for the HMAC  */
    public TPM_ALG_ID hashAlg;
    
    public TPM2_HMAC_Start_REQUEST()
    {
        handle = new TPM_HANDLE();
        hashAlg = TPM_ALG_ID.NULL;
    }
    
    /** @param _handle Handle of an HMAC key
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _auth Authorization value for subsequent use of the sequence
     *  @param _hashAlg The hash algorithm to use for the HMAC
     */
    public TPM2_HMAC_Start_REQUEST(TPM_HANDLE _handle, byte[] _auth, TPM_ALG_ID _hashAlg)
    {
        handle = _handle;
        auth = _auth;
        hashAlg = _hashAlg;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(auth);
        hashAlg.toTpm(buf);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        auth = buf.readSizedByteBuf();
        hashAlg = TPM_ALG_ID.fromTpm(buf);
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPM2_HMAC_Start_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_HMAC_Start_REQUEST.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_HMAC_Start_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPM2_HMAC_Start_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_HMAC_Start_REQUEST.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_HMAC_Start_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "byte", "auth", auth);
        _p.add(d, "TPM_ALG_ID", "hashAlg", hashAlg);
    }

    @Override
    public int numHandles() { return 1; }

    @Override
    public int numAuthHandles() { return 1; }

    @Override
    public TPM_HANDLE[] getHandles() { return new TPM_HANDLE[] {handle}; }

    @Override
    public SessEncInfo sessEncInfo() { return new SessEncInfo(2, 1); }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command starts an HMAC sequence. The TPM will create and initialize an HMAC
 *  sequence structure, assign a handle to the sequence, and set the authValue of the
 *  sequence object to the value in auth.
 */
public class TPM2_HMAC_Start_REQUEST extends TpmStructure
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
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(auth);
        hashAlg.toTpm(buf);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _authSize = buf.readShort() & 0xFFFF;
        auth = new byte[_authSize];
        buf.readArrayOfInts(auth, 1, _authSize);
        hashAlg = TPM_ALG_ID.fromTpm(buf);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPM2_HMAC_Start_REQUEST fromBytes (byte[] byteBuf) 
    {
        TPM2_HMAC_Start_REQUEST ret = new TPM2_HMAC_Start_REQUEST();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_HMAC_Start_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPM2_HMAC_Start_REQUEST fromTpm (InByteBuf buf) 
    {
        TPM2_HMAC_Start_REQUEST ret = new TPM2_HMAC_Start_REQUEST();
        ret.initFromTpm(buf);
        return ret;
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
}

//<<<

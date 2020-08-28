package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs an HMAC on the supplied data using the indicated hash algorithm.  */
public class TPM2_HMAC_REQUEST extends ReqStructure
{
    /** Handle for the symmetric signing key providing the HMAC key
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE handle;

    /** HMAC data  */
    public byte[] buffer;

    /** Algorithm to use for HMAC  */
    public TPM_ALG_ID hashAlg;

    public TPM2_HMAC_REQUEST()
    {
        handle = new TPM_HANDLE();
        hashAlg = TPM_ALG_ID.NULL;
    }

    /** @param _handle Handle for the symmetric signing key providing the HMAC key
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _buffer HMAC data
     *  @param _hashAlg Algorithm to use for HMAC
     */
    public TPM2_HMAC_REQUEST(TPM_HANDLE _handle, byte[] _buffer, TPM_ALG_ID _hashAlg)
    {
        handle = _handle;
        buffer = _buffer;
        hashAlg = _hashAlg;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(buffer);
        hashAlg.toTpm(buf);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        buffer = buf.readSizedByteBuf();
        hashAlg = TPM_ALG_ID.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_HMAC_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_HMAC_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_HMAC_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_HMAC_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_HMAC_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_HMAC_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "byte[]", "buffer", buffer);
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

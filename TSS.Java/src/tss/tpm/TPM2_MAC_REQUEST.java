package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command performs an HMAC or a block cipher MAC on the supplied data using the
 *  indicated algorithm.
 */
public class TPM2_MAC_REQUEST extends ReqStructure
{
    /** Handle for the symmetric signing key providing the MAC key
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE handle;

    /** MAC data  */
    public byte[] buffer;

    /** Algorithm to use for MAC  */
    public TPM_ALG_ID inScheme;

    public TPM2_MAC_REQUEST()
    {
        handle = new TPM_HANDLE();
        inScheme = TPM_ALG_ID.NULL;
    }

    /** @param _handle Handle for the symmetric signing key providing the MAC key
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _buffer MAC data
     *  @param _inScheme Algorithm to use for MAC
     */
    public TPM2_MAC_REQUEST(TPM_HANDLE _handle, byte[] _buffer, TPM_ALG_ID _inScheme)
    {
        handle = _handle;
        buffer = _buffer;
        inScheme = _inScheme;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(buffer);
        inScheme.toTpm(buf);
    }

    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        buffer = buf.readSizedByteBuf();
        inScheme = TPM_ALG_ID.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPM2_MAC_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_MAC_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPM2_MAC_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPM2_MAC_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_MAC_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_MAC_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "byte[]", "buffer", buffer);
        _p.add(d, "TPM_ALG_ID", "inScheme", inScheme);
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

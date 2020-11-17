package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This command starts a MAC sequence. The TPM will create and initialize a MAC sequence
 *  structure, assign a handle to the sequence, and set the authValue of the sequence
 *  object to the value in auth.
 */
public class TPM2_MAC_Start_REQUEST extends ReqStructure
{
    /** Handle of a MAC key
     *  Auth Index: 1
     *  Auth Role: USER
     */
    public TPM_HANDLE handle;

    /** Authorization value for subsequent use of the sequence */
    public byte[] auth;

    /** The algorithm to use for the MAC */
    public TPM_ALG_ID inScheme;

    public TPM2_MAC_Start_REQUEST()
    {
        handle = new TPM_HANDLE();
        inScheme = TPM_ALG_ID.NULL;
    }

    /** @param _handle Handle of a MAC key
     *         Auth Index: 1
     *         Auth Role: USER
     *  @param _auth Authorization value for subsequent use of the sequence
     *  @param _inScheme The algorithm to use for the MAC
     */
    public TPM2_MAC_Start_REQUEST(TPM_HANDLE _handle, byte[] _auth, TPM_ALG_ID _inScheme)
    {
        handle = _handle;
        auth = _auth;
        inScheme = _inScheme;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(auth);
        inScheme.toTpm(buf);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        auth = buf.readSizedByteBuf();
        inScheme = TPM_ALG_ID.fromTpm(buf);
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_MAC_Start_REQUEST fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPM2_MAC_Start_REQUEST.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_MAC_Start_REQUEST fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPM2_MAC_Start_REQUEST fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPM2_MAC_Start_REQUEST.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPM2_MAC_Start_REQUEST");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "byte[]", "auth", auth);
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

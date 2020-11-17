package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Structure representing a session block in a command buffer [TSS] */
public class SessionIn extends TpmStructure
{
    /** Session handle */
    public TPM_HANDLE handle;

    /** Caller nonce */
    public byte[] nonceCaller;

    /** Session attributes */
    public TPMA_SESSION attributes;

    /** AuthValue (or HMAC) */
    public byte[] auth;

    public SessionIn() { handle = new TPM_HANDLE(); }

    /** @param _handle Session handle
     *  @param _nonceCaller Caller nonce
     *  @param _attributes Session attributes
     *  @param _auth AuthValue (or HMAC)
     */
    public SessionIn(TPM_HANDLE _handle, byte[] _nonceCaller, TPMA_SESSION _attributes, byte[] _auth)
    {
        handle = _handle;
        nonceCaller = _nonceCaller;
        attributes = _attributes;
        auth = _auth;
    }

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        handle.toTpm(buf);
        buf.writeSizedByteBuf(nonceCaller);
        attributes.toTpm(buf);
        buf.writeSizedByteBuf(auth);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
        nonceCaller = buf.readSizedByteBuf();
        attributes = TPMA_SESSION.fromTpm(buf);
        auth = buf.readSizedByteBuf();
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static SessionIn fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(SessionIn.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static SessionIn fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static SessionIn fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(SessionIn.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("SessionIn");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }

    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "handle", handle);
        _p.add(d, "byte[]", "nonceCaller", nonceCaller);
        _p.add(d, "TPMA_SESSION", "attributes", attributes);
        _p.add(d, "byte[]", "auth", auth);
    }
}

//<<<

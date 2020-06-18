package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Structure representing a session block in a command buffer [TSS]  */
public class SessionIn extends TpmStructure
{
    /** Session handle  */
    public TPM_HANDLE handle;
    
    /** Caller nonce  */
    public byte[] nonceCaller;
    
    /** Session attributes  */
    public TPMA_SESSION attributes;
    
    /** AuthValue (or HMAC)  */
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
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        handle.toTpm(buf);
        buf.writeSizedByteBuf(nonceCaller);
        attributes.toTpm(buf);
        buf.writeSizedByteBuf(auth);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
        int _nonceCallerSize = buf.readShort() & 0xFFFF;
        nonceCaller = new byte[_nonceCallerSize];
        buf.readArrayOfInts(nonceCaller, 1, _nonceCallerSize);
        int _attributes = buf.readByte();
        attributes = TPMA_SESSION.fromInt(_attributes);
        int _authSize = buf.readShort() & 0xFFFF;
        auth = new byte[_authSize];
        buf.readArrayOfInts(auth, 1, _authSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static SessionIn fromBytes (byte[] byteBuf) 
    {
        SessionIn ret = new SessionIn();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static SessionIn fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static SessionIn fromTpm (InByteBuf buf) 
    {
        SessionIn ret = new SessionIn();
        ret.initFromTpm(buf);
        return ret;
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
        _p.add(d, "byte", "nonceCaller", nonceCaller);
        _p.add(d, "TPMA_SESSION", "attributes", attributes);
        _p.add(d, "byte", "auth", auth);
    }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Structure representing a session block in a command buffer [TSS]
*/
public class SessionIn extends TpmStructure
{
    /**
     * Structure representing a session block in a command buffer [TSS]
     * 
     * @param _handle Session handle 
     * @param _nonceCaller Caller nonce 
     * @param _attributes Session attributes 
     * @param _auth AuthValue (or HMAC)
     */
    public SessionIn(TPM_HANDLE _handle,byte[] _nonceCaller,TPMA_SESSION _attributes,byte[] _auth)
    {
        handle = _handle;
        nonceCaller = _nonceCaller;
        attributes = _attributes;
        auth = _auth;
    }
    /**
    * Structure representing a session block in a command buffer [TSS]
    */
    public SessionIn() {};
    /**
    * Session handle
    */
    public TPM_HANDLE handle;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short nonceCallerSize;
    /**
    * Caller nonce
    */
    public byte[] nonceCaller;
    /**
    * Session attributes
    */
    public TPMA_SESSION attributes;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short authSize;
    /**
    * AuthValue (or HMAC)
    */
    public byte[] auth;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        handle.toTpm(buf);
        buf.writeInt((nonceCaller!=null)?nonceCaller.length:0, 2);
        if(nonceCaller!=null)
            buf.write(nonceCaller);
        attributes.toTpm(buf);
        buf.writeInt((auth!=null)?auth.length:0, 2);
        if(auth!=null)
            buf.write(auth);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        handle = TPM_HANDLE.fromTpm(buf);
        int _nonceCallerSize = buf.readInt(2);
        nonceCaller = new byte[_nonceCallerSize];
        buf.readArrayOfInts(nonceCaller, 1, _nonceCallerSize);
        int _attributes = buf.readInt(1);
        attributes = TPMA_SESSION.fromInt(_attributes);
        int _authSize = buf.readInt(2);
        auth = new byte[_authSize];
        buf.readArrayOfInts(auth, 1, _authSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static SessionIn fromTpm (byte[] x) 
    {
        SessionIn ret = new SessionIn();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
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
    };
    
    
};

//<<<


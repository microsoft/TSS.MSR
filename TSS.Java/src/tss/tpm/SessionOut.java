package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* Structure representing a session block in a response buffer [TSS]
*/
public class SessionOut extends TpmStructure
{
    /**
     * Structure representing a session block in a response buffer [TSS]
     * 
     * @param _nonceTpm TPM nonce 
     * @param _attributes Session attributes 
     * @param _auth HMAC value
     */
    public SessionOut(byte[] _nonceTpm,TPMA_SESSION _attributes,byte[] _auth)
    {
        nonceTpm = _nonceTpm;
        attributes = _attributes;
        auth = _auth;
    }
    /**
    * Structure representing a session block in a response buffer [TSS]
    */
    public SessionOut() {};
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short nonceTpmSize;
    /**
    * TPM nonce
    */
    public byte[] nonceTpm;
    /**
    * Session attributes
    */
    public TPMA_SESSION attributes;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short authSize;
    /**
    * HMAC value
    */
    public byte[] auth;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((nonceTpm!=null)?nonceTpm.length:0, 2);
        if(nonceTpm!=null)
            buf.write(nonceTpm);
        attributes.toTpm(buf);
        buf.writeInt((auth!=null)?auth.length:0, 2);
        if(auth!=null)
            buf.write(auth);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _nonceTpmSize = buf.readInt(2);
        nonceTpm = new byte[_nonceTpmSize];
        buf.readArrayOfInts(nonceTpm, 1, _nonceTpmSize);
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
    public static SessionOut fromTpm (byte[] x) 
    {
        SessionOut ret = new SessionOut();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static SessionOut fromTpm (InByteBuf buf) 
    {
        SessionOut ret = new SessionOut();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("SessionOut");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "nonceTpm", nonceTpm);
        _p.add(d, "TPMA_SESSION", "attributes", attributes);
        _p.add(d, "byte", "auth", auth);
    };
    
    
};

//<<<


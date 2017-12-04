package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* This is the format for each of the authorizations in the session area of the response. If the TPM returns TPM_RC_SUCCESS, then the session area of the response contains the same number of authorizations as the command and the authorizations are in the same order.
*/
public class AUTHResponse extends TpmStructure
{
    /**
     * This is the format for each of the authorizations in the session area of the response. If the TPM returns TPM_RC_SUCCESS, then the session area of the response contains the same number of authorizations as the command and the authorizations are in the same order.
     * 
     * @param _nonce the session nonce, may be the Empty Buffer 
     * @param _sessionAttributes the session attributes 
     * @param _hmac either an HMAC or an EmptyAuth
     */
    public AUTHResponse(byte[] _nonce,TPMA_SESSION _sessionAttributes,byte[] _hmac)
    {
        nonce = _nonce;
        sessionAttributes = _sessionAttributes;
        hmac = _hmac;
    }
    /**
    * This is the format for each of the authorizations in the session area of the response. If the TPM returns TPM_RC_SUCCESS, then the session area of the response contains the same number of authorizations as the command and the authorizations are in the same order.
    */
    public AUTHResponse() {};
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short nonceSize;
    /**
    * the session nonce, may be the Empty Buffer
    */
    public byte[] nonce;
    /**
    * the session attributes
    */
    public TPMA_SESSION sessionAttributes;
    /**
    * size in octets of the buffer field; may be 0
    */
    // private short hmacSize;
    /**
    * either an HMAC or an EmptyAuth
    */
    public byte[] hmac;
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeInt((nonce!=null)?nonce.length:0, 2);
        if(nonce!=null)
            buf.write(nonce);
        sessionAttributes.toTpm(buf);
        buf.writeInt((hmac!=null)?hmac.length:0, 2);
        if(hmac!=null)
            buf.write(hmac);
    }
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _nonceSize = buf.readInt(2);
        nonce = new byte[_nonceSize];
        buf.readArrayOfInts(nonce, 1, _nonceSize);
        int _sessionAttributes = buf.readInt(1);
        sessionAttributes = TPMA_SESSION.fromInt(_sessionAttributes);
        int _hmacSize = buf.readInt(2);
        hmac = new byte[_hmacSize];
        buf.readArrayOfInts(hmac, 1, _hmacSize);
    }
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.getBuf();
    }
    public static AUTHResponse fromTpm (byte[] x) 
    {
        AUTHResponse ret = new AUTHResponse();
        InByteBuf buf = new InByteBuf(x);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    public static AUTHResponse fromTpm (InByteBuf buf) 
    {
        AUTHResponse ret = new AUTHResponse();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_AUTH_RESPONSE");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "byte", "nonce", nonce);
        _p.add(d, "TPMA_SESSION", "sessionAttributes", sessionAttributes);
        _p.add(d, "byte", "hmac", hmac);
    };
    
    
};

//<<<


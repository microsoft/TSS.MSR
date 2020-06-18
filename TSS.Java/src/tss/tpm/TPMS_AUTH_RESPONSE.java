package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This is the format for each of the authorizations in the session area of the response.
 *  If the TPM returns TPM_RC_SUCCESS, then the session area of the response contains the
 *  same number of authorizations as the command and the authorizations are in the same order.
 */
public class TPMS_AUTH_RESPONSE extends TpmStructure
{
    /** The session nonce, may be the Empty Buffer  */
    public byte[] nonce;
    
    /** The session attributes  */
    public TPMA_SESSION sessionAttributes;
    
    /** Either an HMAC or an EmptyAuth  */
    public byte[] hmac;
    
    public TPMS_AUTH_RESPONSE() {}
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        buf.writeSizedByteBuf(nonce);
        sessionAttributes.toTpm(buf);
        buf.writeSizedByteBuf(hmac);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _nonceSize = buf.readShort() & 0xFFFF;
        nonce = new byte[_nonceSize];
        buf.readArrayOfInts(nonce, 1, _nonceSize);
        int _sessionAttributes = buf.readByte();
        sessionAttributes = TPMA_SESSION.fromInt(_sessionAttributes);
        int _hmacSize = buf.readShort() & 0xFFFF;
        hmac = new byte[_hmacSize];
        buf.readArrayOfInts(hmac, 1, _hmacSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPMS_AUTH_RESPONSE fromBytes (byte[] byteBuf) 
    {
        TPMS_AUTH_RESPONSE ret = new TPMS_AUTH_RESPONSE();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_AUTH_RESPONSE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPMS_AUTH_RESPONSE fromTpm (InByteBuf buf) 
    {
        TPMS_AUTH_RESPONSE ret = new TPMS_AUTH_RESPONSE();
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
    }
}

//<<<

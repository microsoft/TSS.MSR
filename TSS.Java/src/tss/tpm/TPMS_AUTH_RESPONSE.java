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
    
    /** @param _nonce The session nonce, may be the Empty Buffer
     *  @param _sessionAttributes The session attributes
     *  @param _hmac Either an HMAC or an EmptyAuth
     */
    public TPMS_AUTH_RESPONSE(byte[] _nonce, TPMA_SESSION _sessionAttributes, byte[] _hmac)
    {
        nonce = _nonce;
        sessionAttributes = _sessionAttributes;
        hmac = _hmac;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(nonce);
        sessionAttributes.toTpm(buf);
        buf.writeSizedByteBuf(hmac);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        nonce = buf.readSizedByteBuf();
        sessionAttributes = TPMA_SESSION.fromTpm(buf);
        hmac = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_AUTH_RESPONSE fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_AUTH_RESPONSE.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_AUTH_RESPONSE fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_AUTH_RESPONSE fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_AUTH_RESPONSE.class);
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
        _p.add(d, "byte[]", "nonce", nonce);
        _p.add(d, "TPMA_SESSION", "sessionAttributes", sessionAttributes);
        _p.add(d, "byte[]", "hmac", hmac);
    }
}

//<<<

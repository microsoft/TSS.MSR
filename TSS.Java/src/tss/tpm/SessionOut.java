package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Structure representing a session block in a response buffer [TSS]  */
public class SessionOut extends TpmStructure
{
    /** TPM nonce  */
    public byte[] nonceTpm;
    
    /** Session attributes  */
    public TPMA_SESSION attributes;
    
    /** HMAC value  */
    public byte[] auth;
    
    public SessionOut() {}
    
    /** @param _nonceTpm TPM nonce
     *  @param _attributes Session attributes
     *  @param _auth HMAC value
     */
    public SessionOut(byte[] _nonceTpm, TPMA_SESSION _attributes, byte[] _auth)
    {
        nonceTpm = _nonceTpm;
        attributes = _attributes;
        auth = _auth;
    }
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeSizedByteBuf(nonceTpm);
        attributes.toTpm(buf);
        buf.writeSizedByteBuf(auth);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        nonceTpm = buf.readSizedByteBuf();
        attributes = TPMA_SESSION.fromTpm(buf);
        auth = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static SessionOut fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(SessionOut.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static SessionOut fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static SessionOut fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(SessionOut.class);
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
    }
}

//<<<

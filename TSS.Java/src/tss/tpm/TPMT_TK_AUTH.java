package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This ticket is produced by TPM2_PolicySigned() and TPM2_PolicySecret() when the
 *  authorization has an expiration time. If nonceTPM was provided in the policy command,
 *  the ticket is computed by
 */
public class TPMT_TK_AUTH extends TpmStructure
{
    /** Ticket structure tag  */
    public TPM_ST tag;
    
    /** The hierarchy of the object used to produce the ticket  */
    public TPM_HANDLE hierarchy;
    
    /** This shall be the HMAC produced using a proof value of hierarchy.  */
    public byte[] digest;
    
    public TPMT_TK_AUTH() { hierarchy = new TPM_HANDLE(); }
    
    /** @param _tag Ticket structure tag
     *  @param _hierarchy The hierarchy of the object used to produce the ticket
     *  @param _digest This shall be the HMAC produced using a proof value of hierarchy.
     */
    public TPMT_TK_AUTH(TPM_ST _tag, TPM_HANDLE _hierarchy, byte[] _digest)
    {
        tag = _tag;
        hierarchy = _hierarchy;
        digest = _digest;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        tag.toTpm(buf);
        hierarchy.toTpm(buf);
        buf.writeSizedByteBuf(digest);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        tag = TPM_ST.fromTpm(buf);
        hierarchy = TPM_HANDLE.fromTpm(buf);
        int _digestSize = buf.readShort() & 0xFFFF;
        digest = new byte[_digestSize];
        buf.readArrayOfInts(digest, 1, _digestSize);
    }
    
    @Override
    public byte[] toTpm() 
    {
        OutByteBuf buf = new OutByteBuf();
        toTpm(buf);
        return buf.buffer();
    }
    
    public static TPMT_TK_AUTH fromBytes (byte[] byteBuf) 
    {
        TPMT_TK_AUTH ret = new TPMT_TK_AUTH();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMT_TK_AUTH fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPMT_TK_AUTH fromTpm (InByteBuf buf) 
    {
        TPMT_TK_AUTH ret = new TPMT_TK_AUTH();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_TK_AUTH");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_ST", "tag", tag);
        _p.add(d, "TPM_HANDLE", "hierarchy", hierarchy);
        _p.add(d, "byte", "digest", digest);
    }
}

//<<<

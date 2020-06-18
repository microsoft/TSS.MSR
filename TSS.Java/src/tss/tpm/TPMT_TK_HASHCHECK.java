package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This ticket is produced by TPM2_SequenceComplete() or TPM2_Hash() when the message
 *  that was digested did not start with TPM_GENERATED_VALUE. The ticket is computed by
 */
public class TPMT_TK_HASHCHECK extends TpmStructure
{
    /** The hierarchy  */
    public TPM_HANDLE hierarchy;
    
    /** This shall be the HMAC produced using a proof value of hierarchy.  */
    public byte[] digest;
    
    public TPMT_TK_HASHCHECK() { hierarchy = new TPM_HANDLE(); }
    
    /** @param _hierarchy The hierarchy
     *  @param _digest This shall be the HMAC produced using a proof value of hierarchy.
     */
    public TPMT_TK_HASHCHECK(TPM_HANDLE _hierarchy, byte[] _digest)
    {
        hierarchy = _hierarchy;
        digest = _digest;
    }
    
    @Override
    public void toTpm(OutByteBuf buf) 
    {
        TPM_ST.HASHCHECK.toTpm(buf);
        hierarchy.toTpm(buf);
        buf.writeSizedByteBuf(digest);
    }
    
    @Override
    public void initFromTpm(InByteBuf buf)
    {
        int _tag = buf.readShort() & 0xFFFF;
        assert(_tag == TPM_ST.HASHCHECK.toInt());
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
    
    public static TPMT_TK_HASHCHECK fromBytes (byte[] byteBuf) 
    {
        TPMT_TK_HASHCHECK ret = new TPMT_TK_HASHCHECK();
        InByteBuf buf = new InByteBuf(byteBuf);
        ret.initFromTpm(buf);
        if (buf.bytesRemaining()!=0)
            throw new AssertionError("bytes remaining in buffer after object was de-serialized");
        return ret;
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMT_TK_HASHCHECK fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    public static TPMT_TK_HASHCHECK fromTpm (InByteBuf buf) 
    {
        TPMT_TK_HASHCHECK ret = new TPMT_TK_HASHCHECK();
        ret.initFromTpm(buf);
        return ret;
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_TK_HASHCHECK");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
    
    @Override
    public void toStringInternal(TpmStructurePrinter _p, int d)
    {
        _p.add(d, "TPM_HANDLE", "hierarchy", hierarchy);
        _p.add(d, "byte", "digest", digest);
    }
    
    /**
    * Create a NULL ticket (e.g. used for signing data with non-restricted keys)
    * 
    * @return The null ticket
    */
    public static TPMT_TK_HASHCHECK nullTicket()
    {
        TPMT_TK_HASHCHECK t = new TPMT_TK_HASHCHECK();
        t.hierarchy = TPM_HANDLE.from(TPM_RH.OWNER);
        return t;
    }
    
}

//<<<

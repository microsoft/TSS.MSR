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
    
    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeShort(TPM_ST.HASHCHECK);
        hierarchy.toTpm(buf);
        buf.writeSizedByteBuf(digest);
    }
    
    /** TpmMarshaller method  */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        buf.readShort();
        hierarchy = TPM_HANDLE.fromTpm(buf);
        digest = buf.readSizedByteBuf();
    }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMT_TK_HASHCHECK fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMT_TK_HASHCHECK.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMT_TK_HASHCHECK fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMT_TK_HASHCHECK fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMT_TK_HASHCHECK.class);
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
        _p.add(d, "byte[]", "digest", digest);
    }
    
    /** Create a NULL ticket (e.g. used for signing data with non-restricted keys)
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

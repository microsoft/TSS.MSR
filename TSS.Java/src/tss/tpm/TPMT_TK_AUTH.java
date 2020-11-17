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
    /** Ticket structure tag */
    public TPM_ST tag;

    /** The hierarchy of the object used to produce the ticket */
    public TPM_HANDLE hierarchy;

    /** This shall be the HMAC produced using a proof value of hierarchy. */
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

    /** TpmMarshaller method */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        tag.toTpm(buf);
        hierarchy.toTpm(buf);
        buf.writeSizedByteBuf(digest);
    }

    /** TpmMarshaller method */
    @Override
    public void initFromTpm(TpmBuffer buf)
    {
        tag = TPM_ST.fromTpm(buf);
        hierarchy = TPM_HANDLE.fromTpm(buf);
        digest = buf.readSizedByteBuf();
    }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMT_TK_AUTH fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMT_TK_AUTH.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMT_TK_AUTH fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMT_TK_AUTH fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMT_TK_AUTH.class);
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
        _p.add(d, "byte[]", "digest", digest);
    }
}

//<<<

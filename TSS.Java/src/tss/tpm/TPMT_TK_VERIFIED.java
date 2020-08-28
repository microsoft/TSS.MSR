package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This ticket is produced by TPM2_VerifySignature(). This formulation is used for
 *  multiple ticket uses. The ticket provides evidence that the TPM has validated that a
 *  digest was signed by a key with the Name of keyName. The ticket is computed by
 */
public class TPMT_TK_VERIFIED extends TpmStructure
{
    /** The hierarchy containing keyName  */
    public TPM_HANDLE hierarchy;

    /** This shall be the HMAC produced using a proof value of hierarchy.  */
    public byte[] digest;

    public TPMT_TK_VERIFIED() { hierarchy = new TPM_HANDLE(); }

    /** @param _hierarchy The hierarchy containing keyName
     *  @param _digest This shall be the HMAC produced using a proof value of hierarchy.
     */
    public TPMT_TK_VERIFIED(TPM_HANDLE _hierarchy, byte[] _digest)
    {
        hierarchy = _hierarchy;
        digest = _digest;
    }

    /** TpmMarshaller method  */
    @Override
    public void toTpm(TpmBuffer buf)
    {
        buf.writeShort(TPM_ST.VERIFIED);
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
    public static TPMT_TK_VERIFIED fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMT_TK_VERIFIED.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMT_TK_VERIFIED fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPMT_TK_VERIFIED fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMT_TK_VERIFIED.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMT_TK_VERIFIED");
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
}

//<<<

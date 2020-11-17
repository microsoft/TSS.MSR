package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** These structures are used to define the key derivation for symmetric secret sharing
 *  using asymmetric methods. A secret sharing scheme is required in any asymmetric key
 *  with the decrypt attribute SET.
 */
public class TPMS_SCHEME_MGF1 extends TPMS_KDF_SCHEME_MGF1
{
    public TPMS_SCHEME_MGF1() {}

    /** @param _hashAlg The hash algorithm used to digest the message */
    public TPMS_SCHEME_MGF1(TPM_ALG_ID _hashAlg) { super(_hashAlg); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_SCHEME_MGF1 fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_SCHEME_MGF1.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_SCHEME_MGF1 fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_SCHEME_MGF1 fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_SCHEME_MGF1.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SCHEME_MGF1");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** These structures are used to define the key derivation for symmetric secret sharing
 *  using asymmetric methods. A secret sharing scheme is required in any asymmetric key
 *  with the decrypt attribute SET.
 */
public class TPMS_KDF_SCHEME_KDF2 extends TPMS_SCHEME_HASH
{
    public TPMS_KDF_SCHEME_KDF2() {}

    /** @param _hashAlg The hash algorithm used to digest the message */
    public TPMS_KDF_SCHEME_KDF2(TPM_ALG_ID _hashAlg) { super(_hashAlg); }

    /** TpmUnion method */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.KDF2; }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_KDF_SCHEME_KDF2 fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_KDF_SCHEME_KDF2.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_KDF_SCHEME_KDF2 fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_KDF_SCHEME_KDF2 fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_KDF_SCHEME_KDF2.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_KDF_SCHEME_KDF2");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

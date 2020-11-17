package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** These are the ECC schemes that only need a hash algorithm as a controlling parameter. */
public class TPMS_KEY_SCHEME_ECDH extends TPMS_SCHEME_HASH
{
    public TPMS_KEY_SCHEME_ECDH() {}

    /** @param _hashAlg The hash algorithm used to digest the message */
    public TPMS_KEY_SCHEME_ECDH(TPM_ALG_ID _hashAlg) { super(_hashAlg); }

    /** TpmUnion method */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.ECDH; }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_KEY_SCHEME_ECDH fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_KEY_SCHEME_ECDH.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_KEY_SCHEME_ECDH fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_KEY_SCHEME_ECDH fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_KEY_SCHEME_ECDH.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_KEY_SCHEME_ECDH");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** These are the RSA encryption schemes that only need a hash algorithm as a controlling parameter. */
public class TPMS_SCHEME_OAEP extends TPMS_ENC_SCHEME_OAEP
{
    public TPMS_SCHEME_OAEP() {}

    /** @param _hashAlg The hash algorithm used to digest the message */
    public TPMS_SCHEME_OAEP(TPM_ALG_ID _hashAlg) { super(_hashAlg); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_SCHEME_OAEP fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_SCHEME_OAEP.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_SCHEME_OAEP fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_SCHEME_OAEP fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_SCHEME_OAEP.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SCHEME_OAEP");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** These are the RSA schemes that only need a hash algorithm as a scheme parameter. */
public class TPMS_SCHEME_RSAPSS extends TPMS_SIG_SCHEME_RSAPSS
{
    public TPMS_SCHEME_RSAPSS() {}

    /** @param _hashAlg The hash algorithm used to digest the message */
    public TPMS_SCHEME_RSAPSS(TPM_ALG_ID _hashAlg) { super(_hashAlg); }

    /** @deprecated Use {@link #toBytes()} instead
     *  @return Wire (marshaled) representation of this object
     */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_SCHEME_RSAPSS fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_SCHEME_RSAPSS.class);
    }

    /** @deprecated Use {@link #fromBytes(byte[])} instead
     *  @param byteBuf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_SCHEME_RSAPSS fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper
     *  @param buf Wire representation of the object
     *  @return New object constructed from its wire representation
     */
    public static TPMS_SCHEME_RSAPSS fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_SCHEME_RSAPSS.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SCHEME_RSAPSS");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** These structures are used to define the key derivation for symmetric secret sharing
 *  using asymmetric methods. A secret sharing scheme is required in any asymmetric key
 *  with the decrypt attribute SET.
 */
public class TPMS_KDF_SCHEME_MGF1 extends TPMS_SCHEME_HASH
{
    public TPMS_KDF_SCHEME_MGF1() {}

    /** @param _hashAlg The hash algorithm used to digest the message  */
    public TPMS_KDF_SCHEME_MGF1(TPM_ALG_ID _hashAlg) { super(_hashAlg); }

    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.MGF1; }

    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }

    /** Static marshaling helper  */
    public static TPMS_KDF_SCHEME_MGF1 fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_KDF_SCHEME_MGF1.class);
    }

    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_KDF_SCHEME_MGF1 fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }

    /** Static marshaling helper  */
    public static TPMS_KDF_SCHEME_MGF1 fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_KDF_SCHEME_MGF1.class);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_KDF_SCHEME_MGF1");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

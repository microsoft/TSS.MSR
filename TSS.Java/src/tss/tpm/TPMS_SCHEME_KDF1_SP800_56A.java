package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** These structures are used to define the key derivation for symmetric secret sharing
 *  using asymmetric methods. A secret sharing scheme is required in any asymmetric key
 *  with the decrypt attribute SET.
 */
public class TPMS_SCHEME_KDF1_SP800_56A extends TPMS_KDF_SCHEME_KDF1_SP800_56A
{
    public TPMS_SCHEME_KDF1_SP800_56A() {}
    
    /** @param _hashAlg The hash algorithm used to digest the message  */
    public TPMS_SCHEME_KDF1_SP800_56A(TPM_ALG_ID _hashAlg) { super(_hashAlg); }
    
    /** @deprecated Use {@link #toBytes()} instead  */
    public byte[] toTpm () { return toBytes(); }
    
    /** Static marshaling helper  */
    public static TPMS_SCHEME_KDF1_SP800_56A fromBytes (byte[] byteBuf) 
    {
        return new TpmBuffer(byteBuf).createObj(TPMS_SCHEME_KDF1_SP800_56A.class);
    }
    
    /** @deprecated Use {@link #fromBytes()} instead  */
    public static TPMS_SCHEME_KDF1_SP800_56A fromTpm (byte[] byteBuf)  { return fromBytes(byteBuf); }
    
    /** Static marshaling helper  */
    public static TPMS_SCHEME_KDF1_SP800_56A fromTpm (TpmBuffer buf) 
    {
        return buf.createObj(TPMS_SCHEME_KDF1_SP800_56A.class);
    }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SCHEME_KDF1_SP800_56A");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

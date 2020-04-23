package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  Underlying type comment: These structures are used to define the key derivation for symmetric secret sharing using
 *  asymmetric methods. A secret sharing scheme is required in any asymmetric key
 *  with the decrypt attribute SET.
 */
public class TPMS_SCHEME_KDF1_SP800_108 extends TPMS_KDF_SCHEME_KDF1_SP800_108
{
    public TPMS_SCHEME_KDF1_SP800_108() {}
    
    /** @param _hashAlg the hash algorithm used to digest the message */
    public TPMS_SCHEME_KDF1_SP800_108(TPM_ALG_ID _hashAlg) { super(_hashAlg); }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SCHEME_KDF1_SP800_108");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<


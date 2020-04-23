package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 155 Definition of Types for HMAC_SIG_SCHEME */
public class TPMS_SCHEME_HMAC extends TPMS_SCHEME_HASH
{
    public TPMS_SCHEME_HMAC() {}
    
    /** @param _hashAlg the hash algorithm used to digest the message */
    public TPMS_SCHEME_HMAC(TPM_ALG_ID _hashAlg) { super(_hashAlg); }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SCHEME_HMAC");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<


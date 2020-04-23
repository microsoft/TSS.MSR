package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** These are the ECC schemes that only need a hash algorithm as a controlling parameter. */
public class TPMS_KEY_SCHEME_ECDH extends TPMS_SCHEME_HASH
{
    public TPMS_KEY_SCHEME_ECDH() {}
    
    /** @param _hashAlg the hash algorithm used to digest the message */
    public TPMS_KEY_SCHEME_ECDH(TPM_ALG_ID _hashAlg) { super(_hashAlg); }
    
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


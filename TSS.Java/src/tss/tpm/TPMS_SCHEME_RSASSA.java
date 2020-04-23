package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Underlying type comment: These are the RSA schemes that only need a hash algorithm as a scheme parameter. */
public class TPMS_SCHEME_RSASSA extends TPMS_SIG_SCHEME_RSASSA
{
    public TPMS_SCHEME_RSASSA() {}
    
    /** @param _hashAlg the hash algorithm used to digest the message */
    public TPMS_SCHEME_RSASSA(TPM_ALG_ID _hashAlg) { super(_hashAlg); }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SCHEME_RSASSA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<


package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/**
 *  Most of the ECC signature schemes only require a hash algorithm to complete the definition
 *  and can be typed as TPMS_SCHEME_HASH. Anonymous algorithms also require a count value so they
 *  are typed to be TPMS_SCHEME_ECDAA.
 */
public class TPMS_SIG_SCHEME_ECDAA extends TPMS_SCHEME_ECDAA
{
    public TPMS_SIG_SCHEME_ECDAA() {}
    
    /**
     *  @param _hashAlg the hash algorithm used to digest the message
     *  @param _count the counter value that is used between TPM2_Commit() and the sign operation
     */
    public TPMS_SIG_SCHEME_ECDAA(TPM_ALG_ID _hashAlg, int _count)
    {
        super(_hashAlg, _count);
    }

    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SIG_SCHEME_ECDAA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<


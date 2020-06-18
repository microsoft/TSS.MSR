package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Most of the ECC signature schemes only require a hash algorithm to complete the
 *  definition and can be typed as TPMS_SCHEME_HASH. Anonymous algorithms also require a
 *  count value so they are typed to be TPMS_SCHEME_ECDAA.
 */
public class TPMS_SIG_SCHEME_ECDSA extends TPMS_SCHEME_HASH
{
    public TPMS_SIG_SCHEME_ECDSA() {}
    
    /** @param _hashAlg The hash algorithm used to digest the message  */
    public TPMS_SIG_SCHEME_ECDSA(TPM_ALG_ID _hashAlg) { super(_hashAlg); }
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.ECDSA; }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SIG_SCHEME_ECDSA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

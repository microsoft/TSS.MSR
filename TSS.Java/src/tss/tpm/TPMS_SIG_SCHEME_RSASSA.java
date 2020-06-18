package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** These are the RSA schemes that only need a hash algorithm as a scheme parameter.  */
public class TPMS_SIG_SCHEME_RSASSA extends TPMS_SCHEME_HASH
{
    public TPMS_SIG_SCHEME_RSASSA() {}
    
    /** @param _hashAlg The hash algorithm used to digest the message  */
    public TPMS_SIG_SCHEME_RSASSA(TPM_ALG_ID _hashAlg) { super(_hashAlg); }
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.RSASSA; }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_SIG_SCHEME_RSASSA");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

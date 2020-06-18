package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** These are the RSA encryption schemes that only need a hash algorithm as a controlling parameter.  */
public class TPMS_ENC_SCHEME_OAEP extends TPMS_SCHEME_HASH
{
    public TPMS_ENC_SCHEME_OAEP() {}
    
    /** @param _hashAlg The hash algorithm used to digest the message  */
    public TPMS_ENC_SCHEME_OAEP(TPM_ALG_ID _hashAlg) { super(_hashAlg); }
    
    /** TpmUnion method  */
    public TPM_ALG_ID GetUnionSelector() { return TPM_ALG_ID.OAEP; }
    
    @Override
    public String toString()
    {
        TpmStructurePrinter _p = new TpmStructurePrinter("TPMS_ENC_SCHEME_OAEP");
        toStringInternal(_p, 1);
        _p.endStruct();
        return _p.toString();
    }
}

//<<<

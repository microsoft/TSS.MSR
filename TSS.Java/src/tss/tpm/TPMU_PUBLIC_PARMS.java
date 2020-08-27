package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 199 defines the possible parameter definition structures that may be contained
 *  in the public portion of a key. If the Object can be a parent, the first field must be
 *  a TPMT_SYM_DEF_OBJECT. See 11.1.7.
 *  One of: TPMS_KEYEDHASH_PARMS, TPMS_SYMCIPHER_PARMS, TPMS_RSA_PARMS, TPMS_ECC_PARMS,
 *  TPMS_ASYM_PARMS.
 */
public interface TPMU_PUBLIC_PARMS extends TpmUnion
{
    public TPM_ALG_ID GetUnionSelector();
}

//<<<

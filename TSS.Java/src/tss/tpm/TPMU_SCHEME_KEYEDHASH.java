package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 157 Definition of TPMU_SCHEME_KEYEDHASH Union [IN/OUT]
 *  One of: TPMS_SCHEME_HMAC, TPMS_SCHEME_XOR, TPMS_NULL_SCHEME_KEYEDHASH.
 */
public interface TPMU_SCHEME_KEYEDHASH extends TpmUnion
{
    public TPM_ALG_ID GetUnionSelector();
}

//<<<

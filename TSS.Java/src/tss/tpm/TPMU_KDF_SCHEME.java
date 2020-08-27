package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 166 Definition of TPMU_KDF_SCHEME Union [IN/OUT]
 *  One of: TPMS_KDF_SCHEME_MGF1, TPMS_KDF_SCHEME_KDF1_SP800_56A, TPMS_KDF_SCHEME_KDF2,
 *  TPMS_KDF_SCHEME_KDF1_SP800_108, TPMS_SCHEME_HASH, TPMS_NULL_KDF_SCHEME.
 */
public interface TPMU_KDF_SCHEME extends TpmUnion
{
    public TPM_ALG_ID GetUnionSelector();
}

//<<<

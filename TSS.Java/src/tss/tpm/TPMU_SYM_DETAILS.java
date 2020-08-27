package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This union allows additional parameters to be added for a symmetric cipher. Currently,
 *  no additional parameters are required for any of the symmetric algorithms.
 *  One of: TPMS_TDES_SYM_DETAILS, TPMS_AES_SYM_DETAILS, TPMS_SM4_SYM_DETAILS,
 *  TPMS_CAMELLIA_SYM_DETAILS, TPMS_ANY_SYM_DETAILS, TPMS_XOR_SYM_DETAILS, TPMS_NULL_SYM_DETAILS.
 */
public interface TPMU_SYM_DETAILS extends TpmUnion
{
    public TPM_ALG_ID GetUnionSelector();
}

//<<<

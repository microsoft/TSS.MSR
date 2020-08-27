package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 205 Definition of TPMU_SENSITIVE_COMPOSITE Union [IN/OUT]
 *  One of: TPM2B_PRIVATE_KEY_RSA, TPM2B_ECC_PARAMETER, TPM2B_SENSITIVE_DATA,
 *  TPM2B_SYM_KEY, TPM2B_PRIVATE_VENDOR_SPECIFIC.
 */
public interface TPMU_SENSITIVE_COMPOSITE extends TpmUnion
{
    public TPM_ALG_ID GetUnionSelector();
}

//<<<

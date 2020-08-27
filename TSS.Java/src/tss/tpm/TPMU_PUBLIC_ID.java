package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This is the union of all values allowed in in the unique field of a TPMT_PUBLIC.
 *  One of: TPM2B_DIGEST_KEYEDHASH, TPM2B_DIGEST_SYMCIPHER, TPM2B_PUBLIC_KEY_RSA,
 *  TPMS_ECC_POINT, TPMS_DERIVE.
 */
public interface TPMU_PUBLIC_ID extends TpmUnion
{
    public TPM_ALG_ID GetUnionSelector();
}

//<<<

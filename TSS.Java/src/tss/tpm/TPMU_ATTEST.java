package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 132 Definition of TPMU_ATTEST Union [OUT]
 *  One of: TPMS_CERTIFY_INFO, TPMS_CREATION_INFO, TPMS_QUOTE_INFO,
 *  TPMS_COMMAND_AUDIT_INFO, TPMS_SESSION_AUDIT_INFO, TPMS_TIME_ATTEST_INFO,
 *  TPMS_NV_CERTIFY_INFO, TPMS_NV_DIGEST_CERTIFY_INFO.
 */
public interface TPMU_ATTEST extends TpmUnion
{
    public TPM_ST GetUnionSelector();
}

//<<<

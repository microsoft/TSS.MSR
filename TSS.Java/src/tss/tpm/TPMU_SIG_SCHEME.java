package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This is the union of all of the signature schemes.
 *  One of: TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA,
 *  TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR,
 *  TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME.
 */
public interface TPMU_SIG_SCHEME extends TpmUnion
{
    public TPM_ALG_ID GetUnionSelector();
}

//<<<

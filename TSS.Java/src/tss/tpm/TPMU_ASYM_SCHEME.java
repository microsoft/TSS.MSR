package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** This union of all asymmetric schemes is used in each of the asymmetric scheme
 *  structures. The actual scheme structure is defined by the interface type used for the
 *  selector (TPMI_ALG_ASYM_SCHEME).
 *  One of: TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA,
 *  TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA,
 *  TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES,
 *  TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME.
 */
public interface TPMU_ASYM_SCHEME extends TpmUnion
{
    public TPM_ALG_ID GetUnionSelector();
}

//<<<

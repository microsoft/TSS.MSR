package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** A TPMU_SIGNATURE_COMPOSITE is a union of the various signatures that are supported by
 *  a particular TPM implementation. The union allows substitution of any signature
 *  algorithm wherever a signature is required in a structure.
 *  One of: TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA,
 *  TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TPMT_HA,
 *  TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE.
 */
public interface TPMU_SIGNATURE extends TpmUnion
{
    public TPM_ALG_ID GetUnionSelector();
}

//<<<

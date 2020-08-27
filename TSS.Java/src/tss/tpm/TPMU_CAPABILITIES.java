package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Table 119 Definition of TPMU_CAPABILITIES Union [OUT]
 *  One of: TPML_ALG_PROPERTY, TPML_HANDLE, TPML_CCA, TPML_CC, TPML_PCR_SELECTION,
 *  TPML_TAGGED_TPM_PROPERTY, TPML_TAGGED_PCR_PROPERTY, TPML_ECC_CURVE,
 *  TPML_TAGGED_POLICY, TPML_ACT_DATA.
 */
public interface TPMU_CAPABILITIES extends TpmUnion
{
    public TPM_CAP GetUnionSelector();
}

//<<<

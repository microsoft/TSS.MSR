package tss.tpm;

import tss.*;


// -----------This is an auto-generated file: do not edit

//>>>

/** Holds static factory method for instantiating TPM unions.
 *  Note: A wrapper class is used instead of simply static function solely for the sake of
 *  uniformity with languages like C# and Java.
 */
class UnionFactory
{
    /** Creates specific TPM union member based on the union type and selector (tag) value */
    @SuppressWarnings("unchecked")
    public static <U extends TpmUnion, S extends TpmEnum<S>>
    U create(String unionType, S selector) // S = TPM_ALG_ID | TPM_CAP | TPM_ST
    {
        if (unionType == "TPMU_CAPABILITIES")
            switch (((TPM_CAP)selector).asEnum()) {
                case ALGS: return (U) new TPML_ALG_PROPERTY();
                case HANDLES: return (U) new TPML_HANDLE();
                case COMMANDS: return (U) new TPML_CCA();
                case PP_COMMANDS: return (U) new TPML_CC();
                case AUDIT_COMMANDS: return (U) new TPML_CC();
                case PCRS: return (U) new TPML_PCR_SELECTION();
                case TPM_PROPERTIES: return (U) new TPML_TAGGED_TPM_PROPERTY();
                case PCR_PROPERTIES: return (U) new TPML_TAGGED_PCR_PROPERTY();
                case ECC_CURVES: return (U) new TPML_ECC_CURVE();
                case AUTH_POLICIES: return (U) new TPML_TAGGED_POLICY();
                case ACT: return (U) new TPML_ACT_DATA();
                default:
            }
        else if (unionType == "TPMU_ATTEST")
            switch (((TPM_ST)selector).asEnum()) {
                case ATTEST_CERTIFY: return (U) new TPMS_CERTIFY_INFO();
                case ATTEST_CREATION: return (U) new TPMS_CREATION_INFO();
                case ATTEST_QUOTE: return (U) new TPMS_QUOTE_INFO();
                case ATTEST_COMMAND_AUDIT: return (U) new TPMS_COMMAND_AUDIT_INFO();
                case ATTEST_SESSION_AUDIT: return (U) new TPMS_SESSION_AUDIT_INFO();
                case ATTEST_TIME: return (U) new TPMS_TIME_ATTEST_INFO();
                case ATTEST_NV: return (U) new TPMS_NV_CERTIFY_INFO();
                case ATTEST_NV_DIGEST: return (U) new TPMS_NV_DIGEST_CERTIFY_INFO();
                default:
            }
        else if (unionType == "TPMU_SYM_DETAILS")
            switch (((TPM_ALG_ID)selector).asEnum()) {
                case TDES: return (U) new TPMS_TDES_SYM_DETAILS();
                case AES: return (U) new TPMS_AES_SYM_DETAILS();
                case SM4: return (U) new TPMS_SM4_SYM_DETAILS();
                case CAMELLIA: return (U) new TPMS_CAMELLIA_SYM_DETAILS();
                case ANY: return (U) new TPMS_ANY_SYM_DETAILS();
                case XOR: return (U) new TPMS_XOR_SYM_DETAILS();
                case NULL: return (U) new TPMS_NULL_SYM_DETAILS();
                default:
            }
        else if (unionType == "TPMU_SENSITIVE_CREATE")
            switch (((TPM_ALG_ID)selector).asEnum()) {
                case ANY: return (U) null;
                case ANY2: return (U) new TPMS_DERIVE();
                default:
            }
        else if (unionType == "TPMU_SCHEME_KEYEDHASH")
            switch (((TPM_ALG_ID)selector).asEnum()) {
                case HMAC: return (U) new TPMS_SCHEME_HMAC();
                case XOR: return (U) new TPMS_SCHEME_XOR();
                case NULL: return (U) new TPMS_NULL_SCHEME_KEYEDHASH();
                default:
            }
        else if (unionType == "TPMU_SIG_SCHEME")
            switch (((TPM_ALG_ID)selector).asEnum()) {
                case RSASSA: return (U) new TPMS_SIG_SCHEME_RSASSA();
                case RSAPSS: return (U) new TPMS_SIG_SCHEME_RSAPSS();
                case ECDSA: return (U) new TPMS_SIG_SCHEME_ECDSA();
                case ECDAA: return (U) new TPMS_SIG_SCHEME_ECDAA();
                case SM2: return (U) new TPMS_SIG_SCHEME_SM2();
                case ECSCHNORR: return (U) new TPMS_SIG_SCHEME_ECSCHNORR();
                case HMAC: return (U) new TPMS_SCHEME_HMAC();
                case ANY: return (U) new TPMS_SCHEME_HASH();
                case NULL: return (U) new TPMS_NULL_SIG_SCHEME();
                default:
            }
        else if (unionType == "TPMU_KDF_SCHEME")
            switch (((TPM_ALG_ID)selector).asEnum()) {
                case MGF1: return (U) new TPMS_KDF_SCHEME_MGF1();
                case KDF1_SP800_56A: return (U) new TPMS_KDF_SCHEME_KDF1_SP800_56A();
                case KDF2: return (U) new TPMS_KDF_SCHEME_KDF2();
                case KDF1_SP800_108: return (U) new TPMS_KDF_SCHEME_KDF1_SP800_108();
                case ANY: return (U) new TPMS_SCHEME_HASH();
                case NULL: return (U) new TPMS_NULL_KDF_SCHEME();
                default:
            }
        else if (unionType == "TPMU_ASYM_SCHEME")
            switch (((TPM_ALG_ID)selector).asEnum()) {
                case ECDH: return (U) new TPMS_KEY_SCHEME_ECDH();
                case ECMQV: return (U) new TPMS_KEY_SCHEME_ECMQV();
                case RSASSA: return (U) new TPMS_SIG_SCHEME_RSASSA();
                case RSAPSS: return (U) new TPMS_SIG_SCHEME_RSAPSS();
                case ECDSA: return (U) new TPMS_SIG_SCHEME_ECDSA();
                case ECDAA: return (U) new TPMS_SIG_SCHEME_ECDAA();
                case SM2: return (U) new TPMS_SIG_SCHEME_SM2();
                case ECSCHNORR: return (U) new TPMS_SIG_SCHEME_ECSCHNORR();
                case RSAES: return (U) new TPMS_ENC_SCHEME_RSAES();
                case OAEP: return (U) new TPMS_ENC_SCHEME_OAEP();
                case ANY: return (U) new TPMS_SCHEME_HASH();
                case NULL: return (U) new TPMS_NULL_ASYM_SCHEME();
                default:
            }
        else if (unionType == "TPMU_SIGNATURE")
            switch (((TPM_ALG_ID)selector).asEnum()) {
                case RSASSA: return (U) new TPMS_SIGNATURE_RSASSA();
                case RSAPSS: return (U) new TPMS_SIGNATURE_RSAPSS();
                case ECDSA: return (U) new TPMS_SIGNATURE_ECDSA();
                case ECDAA: return (U) new TPMS_SIGNATURE_ECDAA();
                case SM2: return (U) new TPMS_SIGNATURE_SM2();
                case ECSCHNORR: return (U) new TPMS_SIGNATURE_ECSCHNORR();
                case HMAC: return (U) new TPMT_HA();
                case ANY: return (U) new TPMS_SCHEME_HASH();
                case NULL: return (U) new TPMS_NULL_SIGNATURE();
                default:
            }
        else if (unionType == "TPMU_PUBLIC_ID")
            switch (((TPM_ALG_ID)selector).asEnum()) {
                case KEYEDHASH: return (U) new TPM2B_DIGEST_KEYEDHASH();
                case SYMCIPHER: return (U) new TPM2B_DIGEST_SYMCIPHER();
                case RSA: return (U) new TPM2B_PUBLIC_KEY_RSA();
                case ECC: return (U) new TPMS_ECC_POINT();
                case ANY: return (U) new TPMS_DERIVE();
                default:
            }
        else if (unionType == "TPMU_PUBLIC_PARMS")
            switch (((TPM_ALG_ID)selector).asEnum()) {
                case KEYEDHASH: return (U) new TPMS_KEYEDHASH_PARMS();
                case SYMCIPHER: return (U) new TPMS_SYMCIPHER_PARMS();
                case RSA: return (U) new TPMS_RSA_PARMS();
                case ECC: return (U) new TPMS_ECC_PARMS();
                case ANY: return (U) new TPMS_ASYM_PARMS();
                default:
            }
        else if (unionType == "TPMU_SENSITIVE_COMPOSITE")
            switch (((TPM_ALG_ID)selector).asEnum()) {
                case RSA: return (U) new TPM2B_PRIVATE_KEY_RSA();
                case ECC: return (U) new TPM2B_ECC_PARAMETER();
                case KEYEDHASH: return (U) new TPM2B_SENSITIVE_DATA();
                case SYMCIPHER: return (U) new TPM2B_SYM_KEY();
                case ANY: return (U) new TPM2B_PRIVATE_VENDOR_SPECIFIC();
                default:
            }
        else
            throw new RuntimeException("UnionFactory::Create(): Unknown union type " + unionType);
        throw new RuntimeException("Unknown selector value " + selector.toString() + " for union " + unionType);
    } // create()

}; // class UnionFactory


//<<<

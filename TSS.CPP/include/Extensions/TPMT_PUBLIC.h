/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

///<summary> Customized TPMT_PUBLIC implementation </summary>
class _DLLEXP_ TPMT_PUBLIC : public _TPMT_PUBLIC
{
public:
    TPMT_PUBLIC () {}
    TPMT_PUBLIC(TPM_ALG_ID nameAlg,
                TPMA_OBJECT objectAttributes,
                const ByteVec& authPolicy,
                const TPMU_PUBLIC_PARMS& parameters,
                const TPMU_PUBLIC_ID& unique )
        : _TPMT_PUBLIC(nameAlg, objectAttributes, authPolicy, parameters, unique)
    {}
    virtual ~TPMT_PUBLIC() {}

    ///<summary>Return the name of this TPMT_PUBLIC object (the hash-alg-prepended hash of the public area).</summary>
    ByteVec GetName() const;

    ///<summary>Validate a TPM-created signature.</summary>
    bool ValidateSignature(const ByteVec& signedData, const TPMU_SIGNATURE& sig);

    ///<summary>Validate a TPM-created quote-attestaion.</summary>
    bool ValidateQuote(const class PCR_ReadResponse& expectedPcrVals,
                       const ByteVec& Nonce, class QuoteResponse& quote) const;

    ///<summary>Validate a TPM-created key-certification.</summary>
    bool ValidateCertify(const TPMT_PUBLIC& certifiedKey, const ByteVec& Nonce,
                         class CertifyResponse& quote) const;

    ///<summary>Validate a TPM-created time-quote.</summary>
    bool ValidateGetTime(const ByteVec& Nonce, class GetTimeResponse& timeQuote) const;

    ///<summary>Validate a TPM-created key-certification.</summary>
    bool ValidateCommandAudit(const TPMT_HA& expectedHash, const ByteVec& Nonce,
                              class GetCommandAuditDigestResponse& quote) const;

    ///<summary>Validate a session-audit signature.</summary>
    bool ValidateSessionAudit(const TPMT_HA& expectedHash, const ByteVec& Nonce,
                              class GetSessionAuditDigestResponse& quote) const;

    ///<summary>Validate a key creation signature.</summary>
    bool ValidateCertifyCreation(const ByteVec& Nonce, const ByteVec& creationHash,
                                 class CertifyCreationResponse& quote) const;

    ///<summary>Validate a key creation signature.</summary>
    bool ValidateCertifyNV(const ByteVec& Nonce, const ByteVec& expectedContents,
                           UINT16 startOffset, class NV_CertifyResponse& quote) const;

    ///<summary>Encrypt: currently only RSA/OAEP.</summary>
    ByteVec Encrypt(const ByteVec& secret, const ByteVec& encodingParms) const;

    ///<summary>Creates an activation blob suitable for TPM2_ActivateCredential() on the TPM
    ///with the corresponding private key.</summary>
    class ActivationData CreateActivation(const ByteVec& secret, const ByteVec& activatedName) const;

    ///<summary>Encrypt session salt: currently only RSA/OAEP</summary>
    ByteVec EncryptSessionSalt(const ByteVec& _secret) const;

    ///<summary>Create an object that we can Import() to the storage key associated with this public key.</summary>
    class DuplicationBlob GetDuplicationBlob(Tpm2& tpm, const TPMT_PUBLIC& pub, const TPMT_SENSITIVE& sensitive,
                                             const TPMT_SYM_DEF_OBJECT& innerWrapper) const;

    [[deprecated("Use GetDuplicationBlob() instead")]]
    class DuplicationBlob CreateImportableObject(Tpm2& tpm, const TPMT_PUBLIC& pub, const TPMT_SENSITIVE& sensitive,
                                                 const TPMT_SYM_DEF_OBJECT& innerWrapper);

    ///<summary>Gets the algorithm of this key.</summary>
    TPM_ALG_ID GetAlg() const;

}; // class TPMT_PUBLIC


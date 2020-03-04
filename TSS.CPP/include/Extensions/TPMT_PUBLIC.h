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
    ByteVec GetName();

    ///<summary>Validate a TPM-created signature.</summary>
    bool ValidateSignature(ByteVec _dataThatWasSigned, TPMU_SIGNATURE& _sig);

    ///<summary>Validate a TPM-created quote-attestaion.</summary>
    bool ValidateQuote(const class PCR_ReadResponse& expectedPcrVals,
                       ByteVec Nonce,
                       class QuoteResponse& quote);

    ///<summary>Validate a TPM-created key-certification.</summary>
    bool ValidateCertify(class TPMT_PUBLIC& keyThatWasCertified,
                         ByteVec Nonce,
                         class CertifyResponse& quote);

    ///<summary>Validate a TPM-created time-quote.</summary>
    bool ValidateGetTime(ByteVec Nonce, class GetTimeResponse& _timeQuote);

    ///<summary>Validate a TPM-created key-certification.</summary>
    bool ValidateCommandAudit(TPMT_HA expectedHash,
                              ByteVec Nonce,
                              class GetCommandAuditDigestResponse& quote);

    ///<summary>Validate a session-audit signature.</summary>
    bool ValidateSessionAudit(TPMT_HA expectedHash,
                              ByteVec Nonce,
                              class GetSessionAuditDigestResponse& quote);

    ///<summary>Validate a key creation signature.</summary>
    bool ValidateCertifyCreation(ByteVec Nonce,
                                 ByteVec creationHash,
                                 class CertifyCreationResponse& quote);

    ///<summary>Validate a key creation signature.</summary>
    bool ValidateCertifyNV(const ByteVec& Nonce,
                           const ByteVec& expectedContents,
                           UINT16 startOffset, class NV_CertifyResponse& quote);

    ///<summary>Encrypt: currently only RSA/OAEP.</summary>
    ByteVec Encrypt(ByteVec _secret, ByteVec _encodingParms);

    ///<summary>Create activation blobs to create an object suitable for TPM2_Activate on the TPM
    ///with the corresponding private key.</summary>
    class ActivationData CreateActivation(ByteVec _secret,
                                          TPM_ALG_ID _nameAlg,
                                          ByteVec _nameOfKeyToBeActivated);

    ///<summary>Encrypt session salt: currently only RSA/OAEP</summary>
    ByteVec EncryptSessionSalt(ByteVec _secret);

    ///<summary>Create an object that we can Import() to the storage key associated with this public key.</summary>
    class DuplicationBlob CreateImportableObject(Tpm2& _tpm, 
                                                 TPMT_PUBLIC _publicPart,
                                                 TPMT_SENSITIVE _sensitivePart,
                                                 TPMT_SYM_DEF_OBJECT _innerWrapper);

    ///<summary>Gets the algorithm of this key.</summary>
    TPM_ALG_ID GetAlg();

}; // class TPMT_PUBLIC


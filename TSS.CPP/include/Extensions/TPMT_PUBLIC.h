/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/

/*
The definitions in this file are #included in TpmTypes.h

These are the custom additions for the TPMT_PUBLIC class

*/

#define TPMT_PUBLIC_CUSTOM_CLONE(l,r)

///<summary>Return the name of this TPMT_PUBLIC object (the hash-alg-prepended hash of the public area).</summary>
public:
ByteVec GetName();

///<summary>Validate a TPM-created signature.</summary>
public:
bool ValidateSignature(ByteVec _dataThatWasSigned, TPMU_SIGNATURE& _sig);

///<summary>Validate a TPM-created quote-attestaion.</summary>
public:
bool ValidateQuote(const class PCR_ReadResponse& expectedPcrVals,
                   ByteVec Nonce,
                   class QuoteResponse& quote);

///<summary>Validate a TPM-created key-certification.</summary>
public:
bool ValidateCertify(class TPMT_PUBLIC& keyThatWasCertified,
                     ByteVec Nonce,
                     class CertifyResponse& quote);

///<summary>Validate a TPM-created time-quote.</summary>
public:
bool ValidateGetTime(ByteVec Nonce, class GetTimeResponse& _timeQuote);

///<summary>Validate a TPM-created key-certification.</summary>
public:
bool ValidateCommandAudit(TPMT_HA expectedHash,
                          ByteVec Nonce,
                          class GetCommandAuditDigestResponse& quote);

///<summary>Validate a session-audit signature.</summary>
public:
bool ValidateSessionAudit(TPMT_HA expectedHash,
                          ByteVec Nonce,
                          class GetSessionAuditDigestResponse& quote);

///<summary>Validate a key creation signature.</summary>
public:
bool ValidateCertifyCreation(ByteVec Nonce,
                             ByteVec creationHash,
                             class CertifyCreationResponse& quote);

///<summary>Validate a key creation signature.</summary>
public:
bool ValidateCertifyNV(const ByteVec& Nonce,
                       const ByteVec& expectedContents,
                       UINT16 startOffset, class NV_CertifyResponse& quote);

///<summary>Encrypt: currently only RSA/OAEP.</summary>
public:
ByteVec Encrypt(ByteVec _secret, ByteVec _encodingParms);

///<summary>Create activation blobs to create an object suitable for TPM2_Activate on the TPM
///with the corresponding private key.</summary>
public:
class ActivationData CreateActivation(ByteVec _secret,
                                      TPM_ALG_ID _nameAlg,
                                      ByteVec _nameOfKeyToBeActivated);

///<summary>Encrypt session salt: currently only RSA/OAEP</summary>
public:
ByteVec EncryptSessionSalt(ByteVec _secret);

///<summary>Create an object that we can Import() to the storage key associated with this public key.</summary>
public:
class DuplicationBlob CreateImportableObject(Tpm2& _tpm, 
                                             TPMT_PUBLIC _publicPart,
                                             TPMT_SENSITIVE _sensitivePart,
                                             TPMT_SYM_DEF_OBJECT _innerWrapper);

///<summary>Gets the algorithm of this key.</summary>
public:
TPM_ALG_ID GetAlg();

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
std::vector<BYTE> GetName();

///<summary>Validate a TPM-created signature.</summary>
public:
bool ValidateSignature(std::vector<BYTE> _dataThatWasSigned, TPMU_SIGNATURE& _sig);

///<summary>Validate a TPM-created quote-attestaion.</summary>
public:
bool ValidateQuote(const class PCR_ReadResponse& expectedPcrVals,
                   std::vector<BYTE> Nonce,
                   class QuoteResponse& quote);

///<summary>Validate a TPM-created key-certification.</summary>
public:
bool ValidateCertify(class TPMT_PUBLIC& keyThatWasCertified,
                     std::vector<BYTE> Nonce,
                     class CertifyResponse& quote);

///<summary>Validate a TPM-created time-quote.</summary>
public:
bool ValidateGetTime(std::vector<BYTE> Nonce, class GetTimeResponse& _timeQuote);

///<summary>Validate a TPM-created key-certification.</summary>
public:
bool ValidateCommandAudit(TPMT_HA expectedHash,
                          std::vector<BYTE> Nonce,
                          class GetCommandAuditDigestResponse& quote);

///<summary>Validate a session-audit signature.</summary>
public:
bool ValidateSessionAudit(TPMT_HA expectedHash,
                          std::vector<BYTE> Nonce,
                          class GetSessionAuditDigestResponse& quote);

///<summary>Validate a key creation signature.</summary>
public:
bool ValidateCertifyCreation(std::vector<BYTE> Nonce,
                             std::vector<BYTE> creationHash,
                             class CertifyCreationResponse& quote);

///<summary>Validate a key creation signature.</summary>
public:
bool ValidateCertifyNV(const std::vector<BYTE>& Nonce,
                       const std::vector<BYTE>& expectedContents,
                       UINT16 startOffset, class NV_CertifyResponse& quote);

///<summary>Encrypt: currently only RSA/OAEP.</summary>
public:
std::vector<BYTE> Encrypt(std::vector<BYTE> _secret, std::vector<BYTE> _encodingParms);

///<summary>Create activation blobs to create an object suitable for TPM2_Activate on the TPM
///with the corresponding private key.</summary>
public:
class ActivationData CreateActivation(std::vector<BYTE> _secret,
                                      TPM_ALG_ID _nameAlg,
                                      std::vector<BYTE> _nameOfKeyToBeActivated);

///<summary>Encrypt session salt: currently only RSA/OAEP</summary>
public:
std::vector<BYTE> EncryptSessionSalt(std::vector<BYTE> _secret);

///<summary>Create an object that we can Import() to the storage key associated with this public key.</summary>
public:
class DuplicationBlob CreateImportableObject(Tpm2& _tpm, 
                                             TPMT_PUBLIC _publicPart,
                                             TPMT_SENSITIVE _sensitivePart,
                                             TPMT_SYM_DEF_OBJECT _innerWrapper);

///<summary>Gets the algorithm of this key.</summary>
public:
TPM_ALG_ID GetAlg();

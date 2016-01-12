/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/

// AuthSessions.cpp - Support for AUTH_SESSION and Tpm2 session-related functions.

#include "stdafx.h"
#include "Tpm2.h"

using namespace std;

_TPMCPP_BEGIN

AUTH_SESSION Tpm2::StartAuthSession(TPM_SE sessionType, 
                                    TPM_ALG_ID authHash)
{
    // Defaults
    std::vector<BYTE> nonceCaller(GetRandom(CryptoServices::HashLength(authHash)));
    TPM_HANDLE tpmKey = TPM_HANDLE::NullHandle();
    TPM_HANDLE bindHandle = TPM_HANDLE::NullHandle();
    TPMT_SYM_DEF symDef = TPMT_SYM_DEF(TPM_ALG_ID::_NULL, 0, TPM_ALG_ID::_NULL);
    TPMA_SESSION attr = TPMA_SESSION::continueSession;
    ByteVec Salt;

    auto resp = StartAuthSession(tpmKey,
                                 bindHandle,
                                 nonceCaller,
                                 std::vector<BYTE>(),
                                 sessionType, 
                                 symDef,
                                 authHash);

    AUTH_SESSION sess(resp.sessionHandle, 
                      sessionType, 
                      authHash,
                      nonceCaller,
                      resp.nonceTPM,
                      attr, 
                      symDef,
                      Salt,
                      bindHandle);
    return sess;
}

AUTH_SESSION Tpm2::StartAuthSession(TPM_SE sessionType,
                                    TPM_ALG_ID authHash,
                                    TPMA_SESSION attr,
                                    TPMT_SYM_DEF symDef)
{
    // Defaults
    std::vector<BYTE> nonceCaller(GetRandom(CryptoServices::HashLength(authHash)));
    TPM_HANDLE tpmKey = TPM_HANDLE::NullHandle();
    TPM_HANDLE bindHandle = TPM_HANDLE::NullHandle();

    auto resp = StartAuthSession(tpmKey,
                                 bindHandle,
                                 nonceCaller,
                                 std::vector<BYTE>(),
                                 sessionType,
                                 symDef,
                                 authHash);

    AUTH_SESSION sess(resp.sessionHandle,
                      sessionType,
                      authHash,
                      nonceCaller,
                      resp.nonceTPM,
                      attr, 
                      symDef, 
                      ByteVec{},
                      bindHandle);
    return sess;
}

AUTH_SESSION Tpm2::StartAuthSession(TPM_HANDLE saltKey, 
                                    TPM_HANDLE bindHandle,
                                    TPM_SE sessionType,
                                    TPM_ALG_ID authHash,
                                    TPMA_SESSION attr,
                                    TPMT_SYM_DEF symDef,
                                    vector<BYTE> salt,
                                    vector<BYTE> encryptedSalt)
{
    std::vector<BYTE> nonceCaller(GetRandom(CryptoServices::HashLength(authHash)));

    auto resp = StartAuthSession(saltKey,
                                 bindHandle, 
                                 nonceCaller,
                                 encryptedSalt,
                                 sessionType, 
                                 symDef,
                                 authHash);

    AUTH_SESSION sess(resp.sessionHandle, 
                      sessionType,
                      authHash,
                      nonceCaller,
                      resp.nonceTPM,
                      attr, 
                      symDef,
                      salt,
                      bindHandle);
    return sess;
}

AUTH_SESSION::AUTH_SESSION()
{

}

AUTH_SESSION::AUTH_SESSION(TPM_HANDLE _sessionHandle,
                           TPM_SE _type,
                           TPM_ALG_ID _hashAlg,
                           std::vector<BYTE> _nonceCaller,
                           std::vector<BYTE> _nonceTpm,
                           TPMA_SESSION _attributes,
                           TPMT_SYM_DEF _symDef,
                           std::vector<BYTE> _salt,
                           TPM_HANDLE _boundObject)
{
    handle = _sessionHandle;
    SessionType = _type;
    HashAlg = _hashAlg;
    NonceCaller = _nonceCaller;
    NonceTpm = _nonceTpm;
    Symmetric = _symDef;
    Salt = _salt;
    BindKey = _boundObject;
    SessionAttributes = _attributes;

    if (BindKey.handle != (UINT32)TPM_RH::_NULL) {
        AuthValue = BindKey.GetAuth();
    }

    Init();
}

void AUTH_SESSION::Init()
{
    CalcSessionKey();
    SessionInitted = true;
    return;
}

bool AUTH_SESSION::CanEncrypt()
{
    return HasSymmetricCipher();
}

std::vector<BYTE> AUTH_SESSION::GetAuthHmac(std::vector<BYTE>& parmHash,
                                            bool directionIn,
                                            std::vector<BYTE> nonceDec,
                                            std::vector<BYTE> nonceEnc,
                                            TPM_HANDLE* associatedHandle)
{
    _ASSERT(SessionInitted);

    // Special case: If this is a policy session and the session includes PolicyPassword the 
    // TPM expects and assumes that the HMAC field will have the plaintext entity field as in 
    // a PWAP session (the related PolicyAuthValue demands an HMAC as usual).
    if (IncludePlaintextPasswordInPolicySession) {
        return AuthValue;
    }

    std::vector<BYTE> nonceNewer, nonceOlder;
    if (directionIn) {
        nonceNewer = NonceCaller;
        nonceOlder = NonceTpm;
    } else {
        nonceNewer = NonceTpm;
        nonceOlder = NonceCaller;
    }

    std::vector<BYTE> sessionAttrs(1);
    sessionAttrs[0] = (BYTE)SessionAttributes;

    if (associatedHandle != NULL && associatedHandle->handle == BindKey.handle) {
        // If we are referencing the object to which the session is bound,
        // we do not inlude the authValue here.
        AuthValue.clear();
    }

    auto tempAuthValue = (SessionType == TPM_SE::POLICY && !SessionIncludesAuth) ? 
                          std::vector<BYTE>() : AuthValue;

    auto hmacKey = Helpers::Concatenate(SessionKey, tempAuthValue);

    std::vector<BYTE> bufToHmac = Helpers::Concatenate(std::vector<std::vector<BYTE>> {
        parmHash,
        nonceNewer,
        nonceOlder,
        nonceDec,
        nonceEnc,
        sessionAttrs 
    });

    auto hmac = CryptoServices::HMAC(HashAlg, hmacKey, bufToHmac);
    return hmac;
}

void AUTH_SESSION::CalcSessionKey()
{
    _ASSERT(SessionKey.size() == 0);

    // Compute Handle.Auth in accordance with Part 1, 19.6.8.
    if (Salt.size() == 0 && BindKey.handle == (UINT32)TPM_RH::_NULL) {
        SessionKey.resize(0);
        return;
    }

    auto hmacKey = Helpers::Concatenate(BindKey.GetAuth(), Salt);

    SessionKey = KDF::KDFa(HashAlg, hmacKey, "ATH", NonceTpm, NonceCaller,
                           CryptoServices::HashLength(HashAlg) * 8);
    return;
}

ByteVec AUTH_SESSION::ParmEncrypt(ByteVec& parm, bool directionCommand)
{
    ByteVec nonceNewer, nonceOlder;

    if (directionCommand) {
        nonceNewer = NonceCaller;
        nonceOlder = NonceTpm;
    } else {
        nonceNewer = NonceTpm;
        nonceOlder = NonceCaller;
    }

    if (Symmetric.algorithm != TPM_ALG_ID::AES) {
        throw domain_error("Only AES parm encryption is implemtented");
    }

    if (Symmetric.keyBits != 128) {
        throw domain_error("Only AES-128 parm encryption is implemtented");
    }

    if (Symmetric.mode != TPM_ALG_ID::CFB) {
        throw domain_error("Only AES-128-CFB parm encryption is implemtented");
    }

    int numKdfBits = 256;
    int numBits = 128;

    ByteVec encKey = SessionKey;
    ByteVec keyInfo = KDF::KDFa(HashAlg, encKey, "CFB", nonceNewer, nonceOlder, numKdfBits);

    size_t keySize = numBits / 8;

    ByteVec key(keyInfo.begin(), keyInfo.begin() + keySize);
    ByteVec iv(keyInfo.begin() + keySize, keyInfo.end());

    ByteVec xcrypted = CryptoServices::CFBXncrypt(directionCommand, TPM_ALG_ID::AES, key, iv, parm);

    return xcrypted;
}

_TPMCPP_END
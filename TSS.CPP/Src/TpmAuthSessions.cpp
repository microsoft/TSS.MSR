/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

// Provides TPM 2.0 auth session related functionality.

#include "stdafx.h"
#include "Tpm2.h"

_TPMCPP_BEGIN

using namespace std;

AUTH_SESSION Tpm2::StartAuthSession(TPM_SE sessionType, 
                                    TPM_ALG_ID authHash)
{
    // Defaults
    ByteVec nonceCaller(GetRandom(Crypto::HashLength(authHash)));
    TPM_HANDLE tpmKey;
    TPM_HANDLE bindHandle;
    TPMT_SYM_DEF symDef = TPMT_SYM_DEF(TPM_ALG_ID::_NULL, 0, TPM_ALG_ID::_NULL);
    TPMA_SESSION attr = TPMA_SESSION::continueSession;
    ByteVec salt;

    auto resp = StartAuthSession(tpmKey, bindHandle, nonceCaller, ByteVec(),
                                 sessionType, symDef, authHash);
    return AUTH_SESSION(resp.handle, sessionType, authHash, nonceCaller, resp.nonceTPM,
                        attr, symDef, salt, bindHandle);
}

AUTH_SESSION Tpm2::StartAuthSession(TPM_SE sessionType, TPM_ALG_ID authHash,
                                    TPMA_SESSION attr, const TPMT_SYM_DEF& symDef)
{
    // Defaults
    ByteVec nonceCaller(GetRandom(Crypto::HashLength(authHash)));
    TPM_HANDLE tpmKey;
    TPM_HANDLE bindHandle;

    auto resp = StartAuthSession(tpmKey, bindHandle, nonceCaller, ByteVec(),
                                 sessionType, symDef, authHash);
    return AUTH_SESSION(resp.handle, sessionType, authHash, nonceCaller, resp.nonceTPM,
                        attr,  symDef,  ByteVec{}, bindHandle);
}

AUTH_SESSION Tpm2::StartAuthSession(TPM_HANDLE saltKey, TPM_HANDLE bindHandle,
                                    TPM_SE sessionType, TPM_ALG_ID authHash,
                                    TPMA_SESSION attr, const TPMT_SYM_DEF& symDef,
                                    const ByteVec& salt, const ByteVec& encryptedSalt)
{
    ByteVec nonceCaller(GetRandom(Crypto::HashLength(authHash)));
    
    _SetRhAuthValue(bindHandle);

    auto resp = StartAuthSession(saltKey, bindHandle,  nonceCaller, encryptedSalt,
                                 sessionType, symDef, authHash);

    return AUTH_SESSION(resp.handle, sessionType, authHash, nonceCaller, resp.nonceTPM,
                        attr, symDef, salt, bindHandle);
}

AUTH_SESSION::AUTH_SESSION(const TPM_HANDLE& sessHandle, TPM_SE type, TPM_ALG_ID hashAlg,
                           const ByteVec& nonceCaller, const ByteVec& nonceTpm,
                           TPMA_SESSION attributes, const TPMT_SYM_DEF& symDef,
                           const ByteVec& salt, const TPM_HANDLE& boundObject)
  : handle(sessHandle), SessionType(type), NonceTpm(nonceTpm), NonceCaller(nonceCaller), 
    HashAlg(hashAlg), SessionAttributes(attributes), Symmetric(symDef), Salt(salt), BindObject(boundObject)
{
    if (BindObject != TPM_RH_NULL)
        AuthValue = BindObject.GetAuth();
    Init();
}

void AUTH_SESSION::Init()
{
    CalcSessionKey();
    SessionInitted = true;
}

bool AUTH_SESSION::CanEncrypt()
{
    return HasSymmetricCipher();
}

ByteVec AUTH_SESSION::GetAuthHmac(const ByteVec& parmHash, bool directionIn,
                                  const ByteVec& nonceDec, const ByteVec& nonceEnc,
                                  const TPM_HANDLE* authHandle)
{
    _ASSERT(SessionInitted);

    // Special case: If this is a policy session and the session includes PolicyPassword the 
    // TPM expects and assumes that the HMAC field will have the plaintext entity field as in 
    // a PWAP session (the related PolicyAuthValue demands an HMAC as usual).
    if (IncludePlaintextPasswordInPolicySession)
        return AuthValue;

    ByteVec nonceNewer, nonceOlder;
    if (directionIn) {
        nonceNewer = NonceCaller;
        nonceOlder = NonceTpm;
    } else {
        nonceNewer = NonceTpm;
        nonceOlder = NonceCaller;
    }

    ByteVec sessionAttrs(1);
    sessionAttrs[0] = (BYTE)SessionAttributes;

    // Sessions's own auth (may be used for overriding standard auth value source)
    ByteVec auth = handle.GetAuth();

    if (authHandle && *authHandle != TPM_RH::PW && auth.empty() &&
        ((SessionType != TPM_SE::POLICY && BindObject != *authHandle) ||
         (SessionType == TPM_SE::POLICY && SessionIncludesAuth)))
    {
        auth = Helpers::TrimTrailingZeros(authHandle->GetAuth());
    }

    auto hmacKey = Helpers::Concatenate(SessionKey, auth);

    ByteVec bufToHmac = Helpers::Concatenate(vector<ByteVec> {
        parmHash,
        nonceNewer,
        nonceOlder,
        nonceDec,
        nonceEnc,
        sessionAttrs 
    });
    return Crypto::HMAC(HashAlg, hmacKey, bufToHmac);
}

void AUTH_SESSION::CalcSessionKey()
{
    _ASSERT(SessionKey.empty());

    // Compute Handle.Auth in accordance with Part 1, 19.6.8.
    if (Salt.empty() && BindObject == TPM_RH_NULL)
        return;

    auto bindAuth = Helpers::TrimTrailingZeros(BindObject.GetAuth());
    auto hmacKey = Helpers::Concatenate(bindAuth, Salt);

    SessionKey = Crypto::KDFa(HashAlg, hmacKey, "ATH", NonceTpm, NonceCaller,
                              Crypto::HashLength(HashAlg) * 8);
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

    if (Symmetric.algorithm != TPM_ALG_ID::AES)
        throw domain_error("Only AES parm encryption is implemtented");

    if (Symmetric.keyBits != 128)
        throw domain_error("Only AES-128 parm encryption is implemtented");

    if (Symmetric.mode != TPM_ALG_ID::CFB)
        throw domain_error("Only AES-128-CFB parm encryption is implemtented");

    int numKdfBits = 256;
    int numBits = 128;

    const ByteVec& encKey = SessionKey;
    size_t keySize = numBits / 8;
    ByteVec keyInfo = Crypto::KDFa(HashAlg, encKey, "CFB", nonceNewer, nonceOlder, numKdfBits);
    ByteVec key(keyInfo.begin(), keyInfo.begin() + keySize);
    ByteVec iv(keyInfo.begin() + keySize, keyInfo.end());

    return Crypto::CFBXncrypt(directionCommand, TPM_ALG_ID::AES, key, iv, parm);
}

_TPMCPP_END

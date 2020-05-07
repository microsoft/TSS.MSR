/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "MarshalInternal.h"
#include "Crypto.h"

extern "C" {
#include <openssl/aes.h>
#include <openssl/evp.h>
#include <openssl/ossl_typ.h>
#include <openssl/sha.h>
#include <openssl/hmac.h>
#include <openssl/rand.h>
#include <openssl/evp.h>
#include <openssl/rsa.h>



#if !defined(OPENSSL_NO_SM3) && OPENSSL_VERSION_NUMBER > 0x1010100FL
#   define ALG_SM3_256  1
#   include <openssl/sm3.h>
#endif

#if OPENSSL_VERSION_NUMBER >= 0x10200000L
    // Check the rsa_st and RSA_PRIME_INFO definitions in crypto/rsa/rsa_lcl.h and
    // either update the version check or provide the new definition for this version.
#   error Untested OpenSSL version
#elif OPENSSL_VERSION_NUMBER >= 0x10100000L
    // from crypto/rsa/rsa_lcl.h
    typedef struct rsa_prime_info_st {
        BIGNUM *r;
        BIGNUM *d;
        BIGNUM *t;
        BIGNUM *pp;
        BN_MONT_CTX *m;
    } RSA_PRIME_INFO;

    DEFINE_STACK_OF(RSA_PRIME_INFO)

    struct rsa_st {
        int pad;
        int32_t version;
        const RSA_METHOD *meth;
        ENGINE *engine;
        BIGNUM *n;
        BIGNUM *e;
        BIGNUM *d;
        BIGNUM *p;
        BIGNUM *q;
        BIGNUM *dmp1;
        BIGNUM *dmq1;
        BIGNUM *iqmp;
        STACK_OF(RSA_PRIME_INFO) *prime_infos;
        RSA_PSS_PARAMS *pss;
        CRYPTO_EX_DATA ex_data;
        int references;
        int flags;
        /* Used to cache montgomery values */
        BN_MONT_CTX *_method_mod_n;
        BN_MONT_CTX *_method_mod_p;
        BN_MONT_CTX *_method_mod_q;
        char *bignum_data;
        BN_BLINDING *blinding;
        BN_BLINDING *mt_blinding;
        CRYPTO_RWLOCK *lock;
    };

#endif // OPENSSL_VERSION_NUMBER

}

typedef INT16     CRYPT_RESULT;

typedef struct {
    UINT16        size;
    BYTE          buffer[4096];
} TPM2B;

typedef struct {
    UINT32        exponent;      // The public exponent pointer
    TPM2B        *publicKey;     // Pointer to the public modulus
    TPM2B        *privateKey;    // The private exponent (not a prime)
} RSA_KEY;

#define CRYPT_FAIL          ((CRYPT_RESULT)  1)
#define CRYPT_SUCCESS       ((CRYPT_RESULT)  0)
#define CRYPT_NO_RESULT     ((CRYPT_RESULT) -1)
#define CRYPT_SCHEME        ((CRYPT_RESULT) -2)
#define CRYPT_PARAMETER     ((CRYPT_RESULT) -3)
#define CRYPT_UNDERFLOW     ((CRYPT_RESULT) -4)
#define CRYPT_POINT         ((CRYPT_RESULT) -5)
#define CRYPT_CANCEL        ((CRYPT_RESULT) -6)


_TPMCPP_BEGIN

using namespace std;


static CRYPT_RESULT
_cpri__ValidateSignatureRSA(const RSA_KEY   *key,       // IN: key to use
                            TPM_ALG_ID       scheme,    // IN: the scheme to use
                            TPM_ALG_ID       hashAlg,   // IN: hash algorithm
                            UINT32           hInSize,   // IN: size of digest to be checked
                            const BYTE      *hIn,       // IN: digest buffer
                            UINT32           sigInSize, // IN: size of signature
                            const BYTE      *sigIn,     // IN: signature
                            UINT16         /*saltSize*/); // IN: salt size for PSS

static int TpmAlgIdToNid(TPM_ALG_ID hashAlg)
{
    switch (hashAlg) {
        case TPM_ALG_NULL:  return 0;
        case TPM_ALG_ID::SHA1:   return NID_sha1;
        case TPM_ALG_ID::SHA256: return NID_sha256;
        case TPM_ALG_ID::SHA384: return NID_sha384;
        case TPM_ALG_ID::SHA512: return NID_sha512;
#if ALG_SM3_256
        case TPM_ALG_ID::SM3_256: return NID_sm3;
#endif
    }
    throw domain_error("TpmAlgIdToNid(): Unknown or not a hash algorithm");
}


bool Crypto::IsImplemented(TPM_ALG_ID hashAlg)
{
    switch (hashAlg) {
        case TPM_ALG_ID::SHA1:
        case TPM_ALG_ID::SHA256:
        case TPM_ALG_ID::SHA384:
        case TPM_ALG_ID::SHA512:
#if ALG_SM3_256
        case TPM_ALG_ID::SM3_256:
#endif
            return true;
    }
    return false;
}


UINT16 Crypto::HashLength(TPM_ALG_ID hashAlg)
{
    switch (hashAlg) {
        case TPM_ALG_NULL:  return 0;
        case TPM_ALG_ID::SHA1:   return 20;
        case TPM_ALG_ID::SHA256: return 32;
        case TPM_ALG_ID::SHA384: return 48;
        case TPM_ALG_ID::SHA512: return 64;
        case TPM_ALG_ID::SM3_256: return 32;
    }
    return 0;
}

ByteVec Crypto::Hash(TPM_ALG_ID hashAlg, const ByteVec& toHash)
{
    ByteVec digest(HashLength(hashAlg));
    size_t len = toHash.size();
    const BYTE *message = toHash.data();
    BYTE *digestBuf = &digest[0];

    switch (hashAlg) {
        case TPM_ALG_ID::SHA1:
            ::SHA1(message, len, digestBuf);
            break;

        case TPM_ALG_ID::SHA256:
			::SHA256(message, len, digestBuf);
            break;

        case TPM_ALG_ID::SHA384:
			::SHA384(message, len, digestBuf);
            break;

        case TPM_ALG_ID::SHA512:
			::SHA512(message, len, digestBuf);
            break;

#if ALG_SM3_256
        case TPM_ALG_ID::SM3_256:
        {
            SM3_CTX ctx;
            sm3_init(&ctx);
            sm3_update(&ctx, message, len);
            sm3_final(digestBuf, &ctx);
            break;
        }
#endif
        default:
            throw domain_error("Hash(): Unknown or not a hash algorithm");
    }

    _ASSERT(HashLength(hashAlg) == digest.size());
    return digest;
}

ByteVec Crypto::HMAC(TPM_ALG_ID hashAlg, const ByteVec& key, const ByteVec& toHash)
{
    size_t messageLen = toHash.size();
    const BYTE *message = toHash.data();

    const BYTE *pKey = key.data();
    int keyLen = (int)key.size();

    const EVP_MD *evp;
    switch (hashAlg) {
        case TPM_ALG_ID::SHA1: evp = EVP_sha1(); break;
        case TPM_ALG_ID::SHA256: evp = EVP_sha256(); break;
        case TPM_ALG_ID::SHA384: evp = EVP_sha384(); break;
        case TPM_ALG_ID::SHA512: evp = EVP_sha512(); break;
#if ALG_SM3_256
        case TPM_ALG_ID::SM3_256: evp = EVP_sm3(); break;
#endif
        default: throw domain_error("Not a hash algorithm");
    }

    // We will use the OpenSSL allocated buffer
    BYTE *digestBuf = ::HMAC(evp, pKey, keyLen, message, messageLen, NULL, NULL);

    return ByteVec(digestBuf, digestBuf + HashLength(hashAlg));
}

///<summary>Default source of random numbers is OpenSSL</summary>
ByteVec Crypto::GetRand(size_t numBytes)
{
    ByteVec resp(numBytes);
    RAND_bytes(&resp[0], (int)numBytes);
    return resp;
}

///<summary>TPM KDF function. Note, a zero is added to the end of label by this routine</summary>
ByteVec Crypto::KDFa(TPM_ALG_ID hmacHash, const ByteVec& hmacKey, const string& label, 
                     const ByteVec& contextU, const ByteVec& contextV, uint32_t numBitsRequired)
{
    uint32_t bitsPerLoop = Crypto::HashLength(hmacHash) * 8;
    uint32_t numLoops = (numBitsRequired + bitsPerLoop - 1) / bitsPerLoop;
    ByteVec kdfStream(numLoops * bitsPerLoop / 8);
    ByteVec labelBytes(label.length());

    for (size_t k = 0; k < label.size(); k++)
        labelBytes[k] = label[k];

    for (uint32_t j = 0; j < numLoops; j++)
    {
        auto toHmac = Helpers::Concatenate(vector<ByteVec> {
            ValueTypeToByteArray(j + 1),
            labelBytes,
            ValueTypeToByteArray((BYTE)0),
            contextU,
            contextV,
            ValueTypeToByteArray(numBitsRequired)
        });

        auto fragment = Crypto::HMAC(hmacHash, hmacKey, toHmac);

        for (size_t m = 0; m < fragment.size(); m++)
            kdfStream[j * bitsPerLoop / 8 + m] = fragment[m];
    }

    return Helpers::ShiftRight(kdfStream, bitsPerLoop * numLoops - numBitsRequired);
}

bool Crypto::ValidateSignature(const TPMT_PUBLIC& pubKey, const ByteVec& signedDigest,
                               const TPMU_SIGNATURE& sig)
{
#if !NEW_MARSHAL
    // Set the selectors in _pubKey.
    pubKey.ToBuf();
#endif
    TPMS_RSA_PARMS *rsaParms = dynamic_cast<TPMS_RSA_PARMS*>(&*pubKey.parameters);

    if (rsaParms == NULL)
        throw domain_error("Only RSA signature verificaion is supported");

    TPM2B_PUBLIC_KEY_RSA *rsaPubKey = dynamic_cast<TPM2B_PUBLIC_KEY_RSA*>(&*pubKey.unique);
    auto schemeTypeId = rsaParms->scheme->GetTypeId();

    if (schemeTypeId != TpmTypeId::TPMS_SCHEME_RSASSA_ID)
        throw domain_error("only RSASSA is supported");

    TPMS_SCHEME_RSASSA *scheme = dynamic_cast<TPMS_SCHEME_RSASSA*>(&*rsaParms->scheme);
    TPM_ALG_ID hashAlg = scheme->hashAlg;
    TPM_ALG_ID sigScheme = TPM_ALG_ID::RSASSA;

    const TPMS_SIGNATURE_RSASSA *rsaSig = dynamic_cast<const TPMS_SIGNATURE_RSASSA*>(&sig);

    if (rsaSig == NULL)
        throw logic_error("internal error");

    // Else this is an algorithm and scheme we support, so validate
    TPM2B rsaPubKeyBuf;
    rsaPubKeyBuf.size = (UINT16)rsaPubKey->buffer.size();
    memcpy(rsaPubKeyBuf.buffer, &rsaPubKey->buffer[0], rsaPubKey->buffer.size());
    RSA_KEY theKey;
    theKey.publicKey = &rsaPubKeyBuf;
    theKey.exponent = rsaParms->exponent;

    CRYPT_RESULT res = _cpri__ValidateSignatureRSA(&theKey, sigScheme, hashAlg,
                                                   (UINT32)signedDigest.size(), &signedDigest[0],
                                                   (UINT32)rsaSig->sig.size(), &rsaSig->sig[0], 0);
    return res == CRYPT_SUCCESS;
}

CRYPT_RESULT
_cpri__ValidateSignatureRSA(const RSA_KEY   *key,       // IN: key to use
                            TPM_ALG_ID     /*scheme*/,  // IN: the scheme to use
                            TPM_ALG_ID       hashAlg,   // IN: hash algorithm
                            UINT32           hInSize,   // IN: size of digest to be checked
                            const BYTE      *hIn,       // IN: digest buffer
                            UINT32           sigInSize, // IN: size of signature
                            const BYTE      *sigIn,     // IN: signature
                            UINT16         /*saltSize*/)  // IN: salt size for PSS
{
    _ASSERT(key != NULL && sigIn != NULL && hIn != NULL);

    // Errors that might be caused by calling parameters
    if (sigInSize != key->publicKey->size)
        return CRYPT_FAIL;

    RSA *keyX;
    BIGNUM *bn_mod = NULL;
    BIGNUM *bn_exp = NULL;
    BYTE exponent[] {1, 0, 1};
    bn_mod = BN_bin2bn(key->publicKey->buffer, key->publicKey->size, NULL);
    bn_exp = BN_bin2bn(exponent, 3, NULL);

    keyX = RSA_new();
    keyX->n = bn_mod;
    keyX->e = bn_exp;
    keyX->d = NULL;
    keyX->p = NULL;
    keyX->q = NULL;

    int res = RSA_verify(TpmAlgIdToNid(hashAlg), hIn, hInSize, const_cast<BYTE*>(sigIn), sigInSize, keyX);

    RSA_free(keyX);
    return res == 1 ? CRYPT_SUCCESS : CRYPT_FAIL;
}

size_t RsaEncrypt(const RSA_KEY *key,         // IN: key to use
                  TPM_ALG_ID /*scheme*/,      // IN: the scheme to use
                  TPM_ALG_ID /*hashAlg*/,     // IN: hash algorithm
                  UINT32       secretSize,    // IN: size of digest to be checked
                  const BYTE   *secret,       // IN: digest buffer
                  UINT32       paddingSize,   // IN: size of signature
                  const BYTE  *padding,       // IN: signature
                  UINT32      *outBufferSize, // IN: salt size for PSS
                  BYTE        *outBuffer
)
{

    BYTE encBuffer[4096];
    RSA *keyX;

    BIGNUM *bn_mod = NULL;
    BIGNUM *bn_exp = NULL;
    BYTE exponent[] {1, 0, 1};

    bn_mod = BN_bin2bn(key->publicKey->buffer, key->publicKey->size, NULL);
    bn_exp = BN_bin2bn(exponent, 3, NULL);

    keyX = RSA_new();
    keyX->n = bn_mod;
    keyX->e = bn_exp;
    keyX->d = NULL;
    keyX->p = NULL;
    keyX->q = NULL;

    int wasNumBytes = (int) * outBufferSize;
    int numBytes = 0;

    if (paddingSize == 0)
        numBytes = RSA_public_encrypt(secretSize, secret, outBuffer, keyX, RSA_PKCS1_OAEP_PADDING);
    else {
        int encLen = key->publicKey->size;
        RSA_padding_add_PKCS1_OAEP(encBuffer, encLen, secret, secretSize, padding, paddingSize);
        numBytes = RSA_public_encrypt(encLen, encBuffer, outBuffer, keyX, RSA_NO_PADDING);
    }

    // Note, we will already've written the buffer if this assert fails, but perhaps it will help.
    _ASSERT(wasNumBytes >= numBytes);

    *outBufferSize = numBytes;
    RSA_free(keyX);
    return numBytes;
}

void Crypto::CreateRsaKey(int bits, int exponent, ByteVec& outPublic, ByteVec& outPrivate)
{
    RSA *newKey = NULL;
    BIGNUM *e = NULL;

    newKey = RSA_new();
    e = BN_new();

    if (!newKey || !e)
        return;

    if (exponent == 0)
        exponent = 65537;

    BN_set_word(e, exponent);

    if (!RSA_generate_key_ex(newKey, bits, e, NULL))
        return;

    outPublic.resize(BN_num_bytes(newKey->n));
    outPrivate.resize(BN_num_bytes(newKey->p));

    BN_bn2bin(newKey->n, &outPublic[0]);
    BN_bn2bin(newKey->p, &outPrivate[0]);

    RSA_free(newKey);
    BN_free(e);

    return;
}

ByteVec Crypto::Encrypt(const TPMT_PUBLIC& pubKey,
                        const ByteVec& secret, const ByteVec& encodingParms)
{
#if !NEW_MARSHAL
    // Set the selectors
    pubKey.ToBuf();
#endif
    TPMS_RSA_PARMS *rsaParms = dynamic_cast<TPMS_RSA_PARMS*>(&*pubKey.parameters);

    if (rsaParms == NULL)
        throw domain_error("Only RSA encryption is supported");

    TPM2B_PUBLIC_KEY_RSA *rsaPubKey = dynamic_cast<TPM2B_PUBLIC_KEY_RSA*>(&*pubKey.unique);

    TPM_ALG_ID hashAlg = pubKey.nameAlg;
    TPM_ALG_ID encScheme = TPM_ALG_ID::OAEP;

    TPM2B rsaPubKeyBuf;
    rsaPubKeyBuf.size = (UINT16)rsaPubKey->buffer.size();
    memcpy(rsaPubKeyBuf.buffer, &rsaPubKey->buffer[0], rsaPubKey->buffer.size());
    RSA_KEY theKey;
    theKey.publicKey = &rsaPubKeyBuf;
    theKey.exponent = rsaParms->exponent;

    UINT32 bufferSize = 4096;
    BYTE encryptionBuffer[4096];
    BYTE null { 0 };
    const BYTE *encoding = &null;

    if (!encodingParms.empty())
        encoding = &encodingParms[0];

    size_t encBlobSize = RsaEncrypt(&theKey, encScheme, hashAlg,
                                    (UINT32)secret.size(), &secret[0],
                                    (UINT32)encodingParms.size(), encoding,
                                    &bufferSize, encryptionBuffer);

    if (encBlobSize < 0)
        throw logic_error("RSA encryption error");

    ByteVec res(encBlobSize);
    for (size_t j = 0; j < encBlobSize; j++)
        res[j] = encryptionBuffer[j];

    return res;
}

SignResponse Crypto::Sign(const TSS_KEY& key, const ByteVec& toSign,
                          const TPMU_SIG_SCHEME& explicitScheme)
{
    // Set the selectors
    TPMT_PUBLIC pubKey = key.publicPart;
#if !NEW_MARSHAL
    pubKey.ToBuf();
#endif

    TPMS_RSA_PARMS *rsaParms = dynamic_cast<TPMS_RSA_PARMS*>(&*pubKey.parameters);

    if (rsaParms == NULL)
        throw domain_error("Only RSA signing is supported");

    TPM2B_PUBLIC_KEY_RSA *rsaPubKey = dynamic_cast<TPM2B_PUBLIC_KEY_RSA*>(&*pubKey.unique);
    ByteVec priv = key.privatePart;

    auto *scheme = dynamic_cast<const TPMS_SCHEME_RSASSA*>(&*rsaParms->scheme),
         *explicitRsassa = dynamic_cast<const TPMS_SCHEME_RSASSA*>(&explicitScheme);
    auto *explicitNullScheme = dynamic_cast<const TPMS_NULL_SIG_SCHEME*>(&explicitScheme);

    if (!scheme)
    {
        scheme = explicitRsassa;
        if (!scheme) {
            if (!explicitNullScheme)
                throw domain_error("Crypto::Sign: No signing scheme specified");
            else
                throw domain_error("Crypto::Sign: Only RSASSA is supported");
        }
    }
    else if (!explicitNullScheme)
        throw domain_error("Non-default scheme can only be used for a key with no scheme of its own");

    RSA *keyX;
    BN_CTX *ctxt = BN_CTX_new();
    BIGNUM *bn_mod = NULL;
    BIGNUM *bn_exp = NULL;
    BIGNUM *bn_p = BN_new();
    BIGNUM *rem = BN_new();
    BIGNUM *bn_q = BN_new();
    BIGNUM *bn_d = BN_new();

    BIGNUM *bnPhi = BN_new();

    // TODO: Non-default exponent.
    BYTE exponent[] {1, 0, 1};

    bn_mod = BN_bin2bn(&rsaPubKey->buffer[0], (int)rsaPubKey->buffer.size(), NULL);
    bn_exp = BN_bin2bn(exponent, 3, NULL);
    bn_p = BN_bin2bn(&priv[0], (int)priv.size(), NULL);

    BN_div(bn_q, rem, bn_mod, bn_p, ctxt);

    keyX = RSA_new();
    keyX->n = bn_mod;
    keyX->e = bn_exp;
    keyX->d = NULL;
    keyX->q = bn_q;
    keyX->p = bn_p;

    // Get compute Phi = (p - 1)(q - 1) = pq - p - q + 1 = n - p - q + 1
    if (BN_copy(bnPhi, bn_mod) == NULL
        || !BN_sub(bnPhi, bnPhi, bn_p)
        || !BN_sub(bnPhi, bnPhi, bn_q)
        || !BN_add_word(bnPhi, 1))
    {
        _ASSERT(FALSE);
    }

    if (!BN_mod_inverse(bn_d, bn_exp, bnPhi, ctxt))
        _ASSERT(FALSE);

    keyX->d = bn_d;

    const int maxBuf = 4096;
    BYTE signature[maxBuf];
    UINT32 sigLen = 4096;
    int res = RSA_sign(TpmAlgIdToNid(scheme->hashAlg), &toSign[0], (unsigned)toSign.size(),
                       &signature[0], &sigLen, keyX);
    
    _ASSERT(res != 0 );

    // Note, we will already've written the buffer if this assert fails, but perhaps it will help.
    _ASSERT(sigLen <= maxBuf);

    ByteVec _sig(sigLen);

    for (size_t j = 0; j < sigLen; j++) {
        _sig[j] = signature[j];
    }

    BN_clear_free(bnPhi);
    BN_free(rem);

    RSA_free(keyX);
    BN_CTX_free(ctxt);
    SignResponse resp;
    resp.signature = make_shared<TPMS_SIGNATURE_RSASSA>(scheme->hashAlg, _sig);
    return resp;
}

ByteVec Crypto::CFBXncrypt(bool encrypt, TPM_ALG_ID algId,
                           const ByteVec& keyBytes, ByteVec& iv, const ByteVec& data)
{
    if (algId != TPM_ALG_ID::AES)
        throw domain_error("unsuppported SymmCipher");

    if (data.empty())
        return ByteVec();

    ByteVec res(data.size());

    AES_KEY key;
    BYTE nullVec[512] = {0};
    BYTE *pIv = iv.empty() ? nullVec : &iv[0];

    int num = 0;

    if (encrypt) {
        AES_set_encrypt_key(&keyBytes[0], (int)keyBytes.size() * 8, &key);
        AES_cfb128_encrypt(&data[0], &res[0], data.size(), &key, pIv, &num, AES_ENCRYPT);
    }
    else {
        AES_set_encrypt_key(&keyBytes[0], (int)keyBytes.size() * 8, &key);
        AES_cfb128_encrypt(&data[0], &res[0], data.size(), &key, pIv, &num, AES_DECRYPT);
    }

    return res;
}

ByteVec Crypto::StringToEncodingParms(const string& s)
{
    ByteVec parms(s.length() + 1);

    for (size_t k = 0; k < s.size(); k++) {
        parms[k] = s[k];
    }

    parms[s.length()] = 0;
    return parms;
}

_TPMCPP_END

/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"

#include "Tpm2.h"
#include "MarshallInternal.h"
#include "CryptoServices.h"

extern "C" {
#include <openssl/aes.h>
#include <openssl/evp.h>
#include <openssl/ossl_typ.h>
#include <openssl/sha.h>
#include <openssl/hmac.h>
#include <openssl/rand.h>
#include <openssl/evp.h>
#include <openssl/rsa.h>

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

static CRYPT_RESULT
_cpri__ValidateSignatureRSA(RSA_KEY         *key,       // IN: key to use
                            TPM_ALG_ID       scheme,    // IN: the scheme to use
                            TPM_ALG_ID       hashAlg,   // IN: hash algorithm
                            UINT32           hInSize,   // IN: size of digest to be checked
                            BYTE            *hIn,       // IN: digest buffer
                            UINT32           sigInSize, // IN: size of signature
                            BYTE            *sigIn,     // IN: signature
                            UINT16           saltSize); // IN: salt size for PSS

#define EVP_sm3_256 EVP_sha256

UINT16 CryptoServices::HashLength(TPM_ALG_ID hashAlg)
{
    UINT16 digestLen;

    switch (hashAlg) {
        case TPM_ALG_ID::SHA1:
            digestLen = 20;
            break;

        case TPM_ALG_ID::SHA256:
            digestLen = 32;
            break;

        case TPM_ALG_ID::SHA384:
            digestLen = 48;
            break;

        case TPM_ALG_ID::SHA512:
            digestLen = 64;
            break;

        default:
            throw domain_error("Not a supported hash algorithm");
    }

    return digestLen;
}

vector<byte> CryptoServices::Hash(TPM_ALG_ID hashAlg, std::vector<BYTE> toHash)
{
    ByteVec digest(HashLength(hashAlg));
    size_t len = toHash.size();
    const BYTE *message = toHash.data();
    BYTE *digestBuf = &digest[0];
    size_t digestLen;

    switch (hashAlg) {
        case TPM_ALG_ID::SHA1:
            ::SHA1(message, len, digestBuf);
            digestLen = 20;
            break;

        case TPM_ALG_ID::SHA256:
			::SHA256(message, len, digestBuf);
            digestLen = 32;
            break;

        case TPM_ALG_ID::SHA384:
			::SHA384(message, len, digestBuf);
            digestLen = 48;
            break;

        case TPM_ALG_ID::SHA512:
			::SHA512(message, len, digestBuf);
            digestLen = 64;
            break;

        default:
            throw domain_error("Not a supported hash algorithm");
    }

    _ASSERT(digestLen == digest.size());

    return digest;
}

vector<byte> CryptoServices::HMAC(TPM_ALG_ID hashAlg,
                                  std::vector<BYTE> _key,
                                  std::vector<BYTE> toHash)
{
    // We will use the OpenSSL allocated buffer
    BYTE *digestBuf;
    size_t messageLen = toHash.size();
    const BYTE *message = toHash.data();

    const BYTE *key = _key.data();
    int keyLen = (int)_key.size();

    // If IV-length or message len is zero then key or message is NULL,
    // and OpenSSL does not like this. Set the pointers to something inoccuous.
    BYTE temp = 0;

    if (key == NULL) {
        key = &temp;
    }

    if (message == NULL) {
        message = &temp;
    }

    size_t digestLen;

    switch (hashAlg) {
        case TPM_ALG_ID::SHA1:
            digestBuf = ::HMAC(EVP_sha1(), key, keyLen, message, messageLen, NULL, NULL);
            digestLen = 20;
            break;

        case TPM_ALG_ID::SHA256:
            digestBuf = ::HMAC(EVP_sha256(), key, keyLen, message, messageLen, NULL, NULL);
            digestLen = 32;
            break;

        case TPM_ALG_ID::SHA384:
            digestBuf = ::HMAC(EVP_sha384(), key, keyLen, message, messageLen, NULL, NULL);
            digestLen = 48;
            break;

        case TPM_ALG_ID::SHA512:
            digestBuf = ::HMAC(EVP_sha512(), key, keyLen, message, messageLen, NULL, NULL);
            digestLen = 64;
            break;

        default:
            throw domain_error("Not a hash algorithm");
    }

    vector<byte> digest(HashLength(hashAlg));

    for (size_t j = 0; j < digestLen; j++) {
        digest[j] = digestBuf[j];
    }

    return digest;
}

///<summary>Default source of random numbers is OpenSSL</summary>
vector<byte> CryptoServices::GetRand(size_t numBytes)
{
    vector<BYTE> resp(numBytes);
    RAND_bytes(&resp[0], (int)numBytes);
    return resp;
}

///<summary>Shift an array right by numBits</summary>
ByteVec ShiftRightInternal(ByteVec x, int numBits)
{
    if (numBits > 7) {
        throw domain_error("Can only shift up to 7 bits");
    }

    int numCarryBits = 8 - numBits;
    ByteVec y(x.size());

    for (int j = (int) x.size() - 1; j >= 0; j--) {
        y[j] = (byte)(x[j] >> numBits);

        if (j != 0) {
            y[j] |= (byte)(x[j - 1] << numCarryBits);
        }
    }

    return y;
}

///<summary>Shift an array right by numBits</summary>
ByteVec ShiftRight(ByteVec x, int numBits)
{
    ByteVec y(x.size() - numBits / 8);

    for (size_t j = 0; j < y.size(); j++) {
        y[j] = x[j];
    }

    return ShiftRightInternal(y, numBits % 8);
}

///<summary>TPM KDF function. Note, a zero is added to the end of label by this routine</summary>
ByteVec KDF::KDFa(TPM_ALG_ID hmacHash,
                  ByteVec hmacKey,
                  string label, 
                  ByteVec contextU,
                  ByteVec contextV,
                  UINT32 numBitsRequired)
{
    UINT32 bitsPerLoop = CryptoServices::HashLength(hmacHash) * 8;
    UINT32 numLoops = (numBitsRequired + bitsPerLoop - 1) / bitsPerLoop;
    ByteVec kdfStream(numLoops * bitsPerLoop / 8);

    ByteVec labelBytes(label.length());

    for (size_t k = 0; k < label.size(); k++) {
        labelBytes[k] = label[k];
    }

    for (UINT32 j = 0; j < numLoops; j++) {

        auto toHmac = Helpers::Concatenate(vector<ByteVec> {
            ValueTypeToByteArray(j + 1),
            labelBytes,
            ValueTypeToByteArray((BYTE)0),
            contextU,
            contextV,
            ValueTypeToByteArray(numBitsRequired)
        });

        auto fragment = CryptoServices::HMAC(hmacHash, hmacKey, toHmac);

        for (UINT32 m = 0; m < fragment.size(); m++) {
            kdfStream[j * bitsPerLoop / 8 + m] = fragment[m];
        }
    }

    return ShiftRight(kdfStream, (int)(bitsPerLoop * numLoops - numBitsRequired));
}

bool CryptoServices::ValidateSignature(TPMT_PUBLIC& _pubKey, 
                                       vector<BYTE>& _digestThatWasSigned,
                                       TPMU_SIGNATURE& _signature)
{
    // Set the selectors in _pubKey.
    _pubKey.ToBuf();

    TPMS_RSA_PARMS *rsaParms = dynamic_cast<TPMS_RSA_PARMS *> (_pubKey.parameters);

    if (rsaParms == NULL) {
        throw domain_error("Only RSA signature verificaion is supported");
    }

    TPM2B_PUBLIC_KEY_RSA *rsaPubKey = dynamic_cast<TPM2B_PUBLIC_KEY_RSA *> (_pubKey.unique);
    TpmStructureBase *schemeX = rsaParms->scheme;
    auto schemeTypeId = schemeX->GetTypeId();

    if (schemeTypeId != TpmTypeId::TPMS_SCHEME_RSASSA_ID) {
        throw domain_error("only RSASSA is supported");
    }

    TPMS_SCHEME_RSASSA *scheme = dynamic_cast<TPMS_SCHEME_RSASSA *>(rsaParms->scheme);
    TPM_ALG_ID hashAlg = scheme->hashAlg;
    TPM_ALG_ID sigScheme = TPM_ALG_ID::RSASSA;

    TPMS_SIGNATURE_RSASSA *theRsaSsaSig = dynamic_cast<TPMS_SIGNATURE_RSASSA *> (&_signature);

    if (theRsaSsaSig == NULL) {
        throw logic_error("internal error");
    }

    // Else this is an algorithm and scheme we support, so validate
    TPM2B rsaPubKeyBuf;
    rsaPubKeyBuf.size = (UINT16)rsaPubKey->buffer.size();
    memcpy(rsaPubKeyBuf.buffer, &rsaPubKey->buffer[0], rsaPubKey->buffer.size());
    RSA_KEY theKey;
    theKey.publicKey = &rsaPubKeyBuf;
    theKey.exponent = rsaParms->exponent;

    CRYPT_RESULT res = _cpri__ValidateSignatureRSA(&theKey, 
                                                   sigScheme,
                                                   hashAlg,
                                                   (UINT32)_digestThatWasSigned.size(),
                                                   &_digestThatWasSigned[0],
                                                   (UINT32)theRsaSsaSig->sig.size(),
                                                   &theRsaSsaSig->sig[0],
                                                   0);
    if (res == CRYPT_SUCCESS) {
        return true;
    }

    return false;
}

CRYPT_RESULT
_cpri__ValidateSignatureRSA(RSA_KEY         *key,       // IN: key to use
                            TPM_ALG_ID       scheme,    // IN: the scheme to use
                            TPM_ALG_ID       hashAlg,   // IN: hash algorithm
                            UINT32           hInSize,   // IN: size of digest to be checked
                            BYTE            *hIn,       // IN: digest buffer
                            UINT32           sigInSize, // IN: size of signature
                            BYTE            *sigIn,     // IN: signature
                            UINT16           saltSize)  // IN: salt size for PSS
{
    _ASSERT(key != NULL && sigIn != NULL && hIn != NULL);

    // Errors that might be caused by calling parameters
    if (sigInSize != key->publicKey->size) {
        return CRYPT_FAIL;
    }

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

    int res = RSA_verify(NID_sha1, hIn, hInSize, sigIn, sigInSize, keyX);

    RSA_free(keyX);

    if (res == 1) {
        return CRYPT_SUCCESS;
    }

    return CRYPT_FAIL;

}

CRYPT_RESULT RsaEncrypt(RSA_KEY     *key,           // IN: key to use
                        TPM_ALG_ID   scheme,        // IN: the scheme to use
                        TPM_ALG_ID   hashAlg,       // IN: hash algorithm
                        UINT32       secretSize,    // IN: size of digest to be checked
                        BYTE        *secret,        // IN: digest buffer
                        UINT32       paddingSize,   // IN: size of signature
                        BYTE        *padding,       // IN: signature
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

    if (paddingSize == 0) {
        numBytes = RSA_public_encrypt(secretSize, secret, outBuffer, keyX, RSA_PKCS1_OAEP_PADDING);
    } else {
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

void CryptoServices::CreateRsaKey(int bits,
                                  int exponent,
                                  std::vector<BYTE>& _outPublic,
                                  std::vector<BYTE>& _outPrivate)
{
    RSA *newKey = NULL;
    BIGNUM *e = NULL;

    newKey = RSA_new();
    e = BN_new();

    if (!newKey || !e) {
        return;
    }

    if (exponent == 0) {
        exponent = 65537;
    }

    BN_set_word(e, exponent);

    if (!RSA_generate_key_ex(newKey, bits, e, NULL)) {
        return;
    }

    _outPublic.resize(BN_num_bytes(newKey->n));
    _outPrivate.resize(BN_num_bytes(newKey->p));

    BN_bn2bin(newKey->n, &_outPublic[0]);
    BN_bn2bin(newKey->p, &_outPrivate[0]);

    RSA_free(newKey);
    BN_free(e);

    return;
}

std::vector<BYTE> CryptoServices::Encrypt(class TPMT_PUBLIC& _pubKey,
                                          vector<BYTE>& _secret,
                                          vector<BYTE>& _encodingParms)
{
    // Set the selectors
    _pubKey.ToBuf();
    TPMS_RSA_PARMS *rsaParms = dynamic_cast<TPMS_RSA_PARMS *> (_pubKey.parameters);

    if (rsaParms == NULL) {
        throw domain_error("Only RSA encryption is supported");
    }

    TPM2B_PUBLIC_KEY_RSA *rsaPubKey = dynamic_cast<TPM2B_PUBLIC_KEY_RSA *> (_pubKey.unique);

    TPM_ALG_ID hashAlg = _pubKey.nameAlg;
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
    BYTE *encoding = &null;

    if (_encodingParms.size() != 0) {
        encoding = &_encodingParms[0];
    }

    int encBlobSize = RsaEncrypt(&theKey,
                                 encScheme,
                                 hashAlg,
                                 (UINT32)_secret.size(),
                                 &_secret[0],
                                 (UINT32)_encodingParms.size(),
                                 encoding,
                                 &bufferSize,
                                 encryptionBuffer);

    if (encBlobSize < 0) {
        throw logic_error("RSA encryption error");
    }

    ByteVec res(encBlobSize);

    for (int j = 0; j < encBlobSize; j++) {
        res[j] = encryptionBuffer[j];
    }

    return res;
}


SignResponse CryptoServices::Sign(class TSS_KEY& key,
                                  vector<BYTE>& toSign,
                                  const TPMU_SIG_SCHEME& _scheme)
{
    // Set the selectors
    TPMT_PUBLIC pubKey = key.publicPart;
    pubKey.ToBuf();

    TPMS_RSA_PARMS *rsaParms = dynamic_cast<TPMS_RSA_PARMS *> (pubKey.parameters);

    if (rsaParms == NULL) {
        throw domain_error("Only RSA signing is supported");
    }

    TPM2B_PUBLIC_KEY_RSA *rsaPubKey = dynamic_cast<TPM2B_PUBLIC_KEY_RSA *> (pubKey.unique);
    ByteVec priv = key.privatePart;

    // REVISIT
    if (dynamic_cast<const TPMS_NULL_SIG_SCHEME *> (&_scheme) == NULL) {
        throw domain_error("non-default scheme not implemented");
    }

    TPMS_SCHEME_RSASSA *scheme = dynamic_cast<TPMS_SCHEME_RSASSA *>(rsaParms->scheme);

    if (scheme == NULL) {
        throw domain_error("only RSASSA is supported");
    }

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
        || !BN_add_word(bnPhi, 1)) {
        _ASSERT(FALSE);
    }

    if (BN_mod_inverse(bn_d, bn_exp, bnPhi, ctxt) == NULL) {
        _ASSERT(FALSE);
    }

    keyX->d = bn_d;

    const int maxBuf = 4096;
    BYTE signature[maxBuf];
    UINT32 sigLen = 4096;
    int res = RSA_sign(NID_sha1, &toSign[0], (unsigned int)toSign.size(), &signature[0], &sigLen, keyX);
    
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
    SignResponse resp(TPMS_SIGNATURE_RSASSA(TPM_ALG_ID::SHA1, _sig));

    return resp;
}

ByteVec CryptoServices::CFBXncrypt(bool _encrypt, 
                                   TPM_ALG_ID _algId,
                                   ByteVec _key,
                                   ByteVec _iv,
                                   ByteVec _x)
{
    if (_algId != TPM_ALG_ID::AES) {
        throw domain_error("unsuppported SymmCipher");
    }

    if (_x.size() == 0) {
        return ByteVec();
    }

    ByteVec res(_x.size());

    AES_KEY key;
    ByteVec nullVec(512);
    BYTE *iv = _iv.size() == 0 ? &nullVec[0] : &_iv[0];

    int num = 0;

    if (_encrypt) {
        AES_set_encrypt_key(&_key[0], (int)_key.size() * 8, &key);
        AES_cfb128_encrypt(&_x[0], &res[0], _x.size(), &key, iv, &num, AES_ENCRYPT);
    }
    else {
        AES_set_encrypt_key(&_key[0], (int)_key.size() * 8, &key);
        AES_cfb128_encrypt(&_x[0], &res[0], _x.size(), &key, iv, &num, AES_DECRYPT);
    }

    return res;
}

ByteVec CryptoServices::StringToEncodingParms(const string& s)
{
    ByteVec parms(s.length() + 1);

    for (size_t k = 0; k < s.size(); k++) {
        parms[k] = s[k];
    }

    parms[s.length()] = 0;
    return parms;
}

_TPMCPP_END

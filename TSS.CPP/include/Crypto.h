/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once

_TPMCPP_BEGIN

typedef ByteVec ByteVec;
class  TpmException;
class TPMT_PUBLIC;

/// <summary> Provides TSS with static methods implementing an interface to an underlying software 
/// crypto library (currently OpenSSL). </summary>
class _DLLEXP_ Crypto {
    public:
        /// <summary> Default RNG used by all TSS facilities. </summary>
        static ByteVec GetRand(size_t numBytes);

        /// <summary> Return true if the hash algorithm is implemented by the TSS software layer </summary>
        static bool IsImplemented(TPM_ALG_ID hashAlg);

        /// <summary>Return the length in bytes of the hash algorithm </summary>
        static UINT16 HashLength(TPM_ALG_ID hashAlg);

        /// <summary> Computes digest of the given data using the given hash algorithm </summary>
        /// <param name = "hashAlg"> Hash algorithm to use </param>
        /// <param name = "data"> Byte buffer with the data to digest </param>
        /// <param name = "startPos"> First byte of the fragment to digest </param>
        /// <param name = "len"> Length of the fragment to digest </param>
        static ByteVec Hash(TPM_ALG_ID hashAlg, const ByteVec& data,
                            size_t startPos = 0, size_t len = 0);

        /// <summary> Computes HMAC of the given data buffer using the given key and hash algorithm  </summary>
        /// <param name = "hashAlg"> Hash algorithm to use </param>
        /// <param name = "key"> Byte buffer with the HMAC key to use </param>
        /// <param name = "data"> Byte buffer with the data to digest </param>
        static ByteVec HMAC(TPM_ALG_ID hashAlg, const ByteVec& key, const ByteVec& data);

        // Public-key operations

        /// <summary> Validate the signature using the public key and scheme in pubKey </summary>
        static bool ValidateSignature(const TPMT_PUBLIC& pubKey, 
                                      const ByteVec& signedDigest,
                                      const TPMU_SIGNATURE& signature);

        /// <summary> Encrypt using the public key and scheme in pubKey </summary>
        static ByteVec Encrypt(const TPMT_PUBLIC& pubKey, 
                               const ByteVec& secret,
                               const ByteVec& encodingParms);

        // Private asym key operations

        /// <summary>Sign the dataToSign byte array using the given key. 
        /// If the key does not have a scheme of its own (i.e. was configuted with
        /// a NULL scheme), explicitScheme can be used to supply the signing scheme. </summary>
        static SignResponse Sign(const class TSS_KEY& key, 
                                       const ByteVec& dataToSign,
                                       const TPMU_SIG_SCHEME& explicitScheme);

        /// <summary>Sign the dataToSign byte array using the given key. </summary>
        static SignResponse Sign(class TSS_KEY& key, const ByteVec& dataToSign)
        {
            return Sign(key, dataToSign, TPMS_NULL_SIG_SCHEME());
        }

        /// <summary> CFB Encryption (encrypt = true) or Decryption (decrypt = false) of x </summary>
        static ByteVec CFBXcrypt(bool encrypt, TPM_ALG_ID algId,
                                  const ByteVec& key,
                                  ByteVec& iv,
                                  const ByteVec& data);

        static void CreateRsaKey(int bits,
                                 int exponent,
                                 ByteVec& outPublic,
                                 ByteVec& outPrivate);

        /// <summary> Converts to UTF8 and adds a terminating zero </summary>
        static ByteVec StringToEncodingParms(const string& s);

        /// <summary> TPM KDFa key-derivation function </summary>
        static ByteVec KDFa(TPM_ALG_ID hmacHash, 
                            const ByteVec& hmacKey,
                            const string& label, 
                            const ByteVec& contextU,
                            const ByteVec& contextV,
                            uint32_t numBitsRequired);
};

[[deprecated("Use Crypto instead")]]
typedef Crypto CryptoServices;

_TPMCPP_END
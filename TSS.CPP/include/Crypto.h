/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once

_TPMCPP_BEGIN

typedef ByteVec ByteVec;
class  TpmException;
class TPMT_PUBLIC;

///<summary>The static methods in Crypto are TSS.C++'s interface to
/// a software crypto-library. At the time of writing we only support OpenSSL.</summary>
class _DLLEXP_ Crypto {
    public:
        ///<summary>TSS.C++ default RNG. TSS.C++ pulls all random numbers through the 
        /// Tpm2 class. By default this method is called, but the programmer can register
        /// their own local RNG instead.</summary>
        static ByteVec GetRand(size_t numBytes);

        ///<summary>Return true if the hash algorithm is implemented by the TSS software layer</summary>
        static bool IsImplemented(TPM_ALG_ID hashAlg);

        ///<summary>Return the length in bytes of the hash algorithm</summary>
        static UINT16 HashLength(TPM_ALG_ID hashAlg);

        ///<summary>Hash</summary>
        static ByteVec Hash(TPM_ALG_ID hashAlg, 
                            const ByteVec& toHash);

        ///<summary>HMAC</summary>
        static ByteVec HMAC(TPM_ALG_ID hashAlg,
                            const ByteVec& key,
                            const ByteVec& toHash);

        // Public-key operations

        ///<summary>Validate the signature using the public key and scheme in pubKey </summary>
        static bool ValidateSignature(const TPMT_PUBLIC& pubKey, 
                                      const ByteVec& signedDigest,
                                      const TPMU_SIGNATURE& signature);

        ///<summary>Encrypt using the public key and scheme in pubKey </summary>
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

        ///<summary>CFB Encryption (encrypt = true) or Decryption (decrypt = false) of x</summary>
        static ByteVec CFBXncrypt(bool encrypt, TPM_ALG_ID algId,
                                  const ByteVec& key,
                                  ByteVec& iv,
                                  const ByteVec& data);

        static void CreateRsaKey(int bits,
                                 int exponent,
                                 ByteVec& outPublic,
                                 ByteVec& outPrivate);

        ///<summary>Converts to UTF8 and adds a terminating zero</summary>
        static ByteVec StringToEncodingParms(const string& s);

        ///<summary>TPM KDFa key-derivation function</summary>
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
/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once

_TPMCPP_BEGIN

typedef ByteVec ByteVec;
class  TpmException;

///<summary>The static methods in CryptoServices are TSS.C++'s interface to
/// a crypto-library. At the time of writing we only support OpenSSL.</summary>
class _DLLEXP_ CryptoServices {
    public:
        ///<summary>TSS.C++ default RNG. TSS.C++ pulls all random numbers through the 
        /// Tpm2 class. By default this method is called, but the programmer can register
        /// their own local RNG instead.</summary>
        static ByteVec GetRand(size_t numBytes);

        ///<summary>Hash</summary>
        static ByteVec Hash(TPM_ALG_ID hashAlg, 
                            const ByteVec& toHash);

        ///<summary>HMAC</summary>
        static ByteVec HMAC(TPM_ALG_ID hashAlg,
                            const ByteVec& key,
                            const ByteVec& toHash);

        ///<summary>Return the length in bytes of the hash algorithm</summary>
        static UINT16 HashLength(TPM_ALG_ID hashAlg);

        // Public-key operations

        ///<summary>Validate the signature using the public key and scheme in _pubKey</summary>
        static bool ValidateSignature(class TPMT_PUBLIC& _pubKey, 
                                      const ByteVec& _digestThatWasSigned,
                                      class TPMU_SIGNATURE& _signature);

        ///<summary>Encrypt using the public key and scheme in _pubKey</summary>
        static ByteVec Encrypt(class TPMT_PUBLIC& _pubKey, 
                                   const ByteVec& _secret,
                                   const ByteVec& _encodingParms);

        // Private asym key operations

        ///<summary>Sign</summary>
        static class SignResponse Sign(class TSS_KEY& key, 
                                       const ByteVec& toSign,
                                       const class TPMU_SIG_SCHEME& _nonDefaultScheme);

        static class SignResponse Sign(class TSS_KEY& key, 
                                       const ByteVec& toSign)
        {
            return Sign(key, toSign, TPMS_NULL_SIG_SCHEME());
        }

        ///<summary>CFB Encryption (encrypt = true) or Decryption (decrypt = false) of x</summary>
        static ByteVec CFBXncrypt(bool encrypt, TPM_ALG_ID algId,
                                  const ByteVec& key,
                                  ByteVec& iv,
                                  const ByteVec& x);

        static void CreateRsaKey(int bits,
                                 int exponent,
                                 ByteVec& _outPublic,
                                 ByteVec& _outPrivate);

        ///<summary>Converts to UTF8 and adds a terminating zero</summary>
        static ByteVec StringToEncodingParms(const string& s);
};

class KDF {
    public:
        ///<summary>TPM KDFa key-derivation function</summary>
        static ByteVec KDFa(TPM_ALG_ID hmacHash, 
                            const ByteVec& hmacKey,
                            const string& label, 
                            const ByteVec& contextU,
                            const ByteVec& contextV,
                            UINT32 numBitsRequired);
};

_TPMCPP_END
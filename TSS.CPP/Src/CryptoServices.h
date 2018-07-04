/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#pragma once

_TPMCPP_BEGIN

enum class TPM_ALG_ID : UINT16;
typedef std::vector<BYTE> ByteVec;
class  TpmException;

///<summary>The static methods in CryptoServices are TSS.C++'s interface to
/// a crypto-library. At the time of writing we only support OpenSSL.</summary>
class _DLLEXP_ CryptoServices {
    public:
        ///<summary>TSS.C++ default RNG. TSS.C++ pulls all random numbers through the 
        /// Tpm2 class. By default this method is called, but the programmer can register
        /// their own local RNG instead.</summary>
        static std::vector<byte> GetRand(size_t numBytes);

        ///<summary>Hash</summary>
        static std::vector<byte> Hash(TPM_ALG_ID hashAlg, std::vector<BYTE> toHash);

        ///<summary>HMAC</summary>
        static std::vector<byte> HMAC(TPM_ALG_ID hashAlg,
                                      std::vector<BYTE> key,
                                      std::vector<BYTE> toHash);

        ///<summary>Return the length in bytes of the hash algorithm</summary>
        static UINT16 HashLength(TPM_ALG_ID hashAlg);

        // Public-key operations

        ///<summary>Validate the signature using the public key and scheme in _pubKey</summary>
        static bool ValidateSignature(class TPMT_PUBLIC& _pubKey, 
                                      std::vector<BYTE>& _digestThatWasSigned,
                                      class TPMU_SIGNATURE& _signature);

        ///<summary>Encrypt using the public key and scheme in _pubKey</summary>
        static std::vector<BYTE> Encrypt(class TPMT_PUBLIC& _pubKey, 
                                         std::vector<BYTE>& _secret,
                                         std::vector<BYTE>& _encodingParms);

        // Private asym key operations

        ///<summary>Sign</summary>
        static class SignResponse Sign(class TSS_KEY& key, 
                                       vector<BYTE>& toSign,
                                       const class TPMU_SIG_SCHEME& _nonDefaultScheme);

        ///<summary>CFB Encryption (encrypt = true) or Decryption (decrypt = false) of x</summary>
        static ByteVec CFBXncrypt(bool encrypt, 
                                  TPM_ALG_ID algId,
                                  ByteVec key,
                                  ByteVec iv,
                                  ByteVec x);

        static void CreateRsaKey(int bits,
                                 int exponent,
                                 std::vector<BYTE>& _outPublic,
                                 std::vector<BYTE>& _outPrivate);

        ///<summary>Converts to UTF8 and adds a terminating zero</summary>
        static ByteVec StringToEncodingParms(const string& s);
};

class KDF {
    public:
        ///<summary>TPM KDFa key-derivation function</summary>
        static ByteVec KDFa(TPM_ALG_ID hmacHash, 
                            ByteVec hmacKey,
                            std::string label, 
                            ByteVec contextU,
                            ByteVec contextV,
                            UINT32 numBitsRequired);
};

_TPMCPP_END
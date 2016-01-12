/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#pragma once

// TODO: Better encapsulation and better constructors

_TPMCPP_BEGIN

///<summary>AUTH_SESSION encapsulates state + methods for TPM authorization sessions.</summary>
class _DLLEXP_ AUTH_SESSION {
        friend class Tpm2;
    public:
        AUTH_SESSION();
                     
        AUTH_SESSION(TPM_HANDLE _sessionHandle,     
                     TPM_SE _type,                  
                     TPM_ALG_ID _hashAlg,           
                     std::vector<BYTE> _nonceCaller,
                     std::vector<BYTE> _nonceTpm,   
                     TPMA_SESSION _attributes,
                     TPMT_SYM_DEF _symDef,          // Optional
                     std::vector<BYTE> _salt,       // Optional
                     TPM_HANDLE _boundObject);      // Optional

        static AUTH_SESSION PWAP() {
            AUTH_SESSION s;
            s.PWap = true;
            s.handle = TPM_HANDLE::FromReservedHandle(TPM_RH::RS_PW);
            return s;
        }

        ///<summary>Casting operator so that sessions can be used in place of handles</summary>
        operator TPM_HANDLE() {
            return handle;
        }

        bool IsPWAP() const {
            return PWap;
        }

        void IncludePlaintextPassword() {
            IncludePlaintextPasswordInPolicySession = true;
        }

        void SetSessionIncludesAuth() {
            SessionIncludesAuth = true;
        }

        void ForceHmac() {
            ForceHmacOnPolicySession = true;
        }

        TPM_ALG_ID GetHashAlg() {
            return HashAlg;
        }

        ByteVec GetNonceTpm() {
            return NonceTpm;
        }

    protected:
        void Init();
        void CalcSessionKey();

        ByteVec ParmEncrypt(ByteVec& parm, bool directionCommand /* false == response */);

        void PrepareParmEncryptionSessions();
        bool CanEncrypt();

        bool HasSymmetricCipher() {

            return (Symmetric.algorithm != TPM_ALG_ID::_NULL);
        }

        std::vector<BYTE> GetAuthHmac(std::vector<BYTE>& parmHash,
                                      bool directionIn,
                                      std::vector<BYTE> nonceDec,
                                      std::vector<BYTE> nonceEnc,
                                      TPM_HANDLE *associatedHandle);

        bool SessionInitted = false;

        ///<summary>Set the auth-value to be used for the next use of this session.</summary>
        void SetAuthValue(const std::vector<BYTE>& _authVal) {
            AuthValue = _authVal;
        }

        ///<summary>Session handle</summary>
        TPM_HANDLE handle;

        ///<summary>Type of session (HMAC, policly or trial-policy).</summary>
        TPM_SE SessionType;

        ///<summary>Most recent nonce returned by TPM.</summary>
        std::vector<BYTE> NonceTpm;

        ///<summary>TSS.C++ library nonce.</summary>
        std::vector<BYTE> NonceCaller;

        ///<summary>Session hash algorithm (HMAC or policy-hash).</summary>
        TPM_ALG_ID HashAlg;

        ///<summary>Attribues</summary>
        TPMA_SESSION SessionAttributes;

        ///<summary>Algorithm-info for encrypt/decrypt sessions.</summary>
        TPMT_SYM_DEF Symmetric;

        bool PWap = false;
        std::vector<BYTE> SessionKey;
        std::vector<BYTE> AuthValue;
        std::vector<BYTE> Salt;

        ///<summary>Object to which the session is bound (needed for AuthValue).</summary>
        TPM_HANDLE BindKey;

        bool SessionIncludesAuth = false;
        bool ForceHmacOnPolicySession = false;
        bool IncludePlaintextPasswordInPolicySession = false;

};
///<summary>This class encapsulates the data needed to call Activate().</summary>
class ActivationData {
    public:
        std::vector<BYTE> CredentialBlob;
    public:
        std::vector<BYTE> Secret;
};

///<summary>This class encapsulates the data you need to Import a key.</summary>
class DuplicationBlob {    
    public:
        ///<summary>The optional symmetric encryption key used as the inner wrapper for
        /// duplicate. If symmetricAlg is TPM_ALG_NULL, then this parameter shall be the
        /// Empty Buffer</summary>
        std::vector<BYTE> EncryptionKey;

        ///<summary>The symmetrically encrypted duplicate object that may contain an inner
        /// symmetric wrapper</summary>
        std::vector<BYTE> DuplicateObject;

        ///<summary>Symmetric key used to encrypt duplicate inSymSeed is
        /// encrypted / encoded using the algorithms of newParent.</summary>
        std::vector<BYTE> EncryptedSeed;

        ///<summary>Set to random key used for inner-wrapper (if an inner-wrapper is requested).</summary>
        std::vector<BYTE> InnerWrapperKey;
};

_TPMCPP_END
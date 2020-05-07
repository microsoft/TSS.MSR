/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */
#pragma once

using namespace TpmCpp;

#define null  {}

class Samples {
    public:
        Samples();
        ~Samples();

        Tpm2& GetTpm() {
            return tpm;
        }

        void InitTpmProps();

        // The following methods demonstrate how TSS.C++ is used to perform TPM functions.
        void RunAllSamples();
        void Rand();
        void PCR();
        void Locality();
        void Hash();
        void HMAC();
        void GetCapability();
        void NV();
        void NVX();
        void PrimaryKeys();
        void AuthSessions();
        void Async();
        void PolicySimplest();
        void PolicyLocalitySample();
        void PolicyPCRSample();
        void PolicyORSample();
        void ChildKeys();
        void CounterTimer();
        void Attestation();
        void Admin();
        void DictionaryAttack();
        void PolicyCpHash();
        void PolicyTimer();
        void PolicyWithPasswords();
        void Unseal();
        void Serializer();
        void SessionEncryption();
        void ImportDuplicate();
        void MiscAdmin();
        void RsaEncryptDecrypt();
        void Audit();
        void Activate();
        void SoftwareKeys();
        void PolicySigned();
        void PolicyAuthorizeSample();
        void PolicySecretSample();
        void EncryptDecryptSample();
        void SeededSession();
        void PolicyNVSample();
        void PolicyNameHashSample();
        void ReWrapSample();
        void BoundSession();

        void Callback1();
        void Callback2();

        void PresentationSnippets();

        ///<summary>Checks to see that there are no keys left in the TPM</summary>
        void AssertNoLoadedKeys();

        void TpmCallback(const ByteVec& command, const ByteVec& response);

        static void TpmCallbackStatic(const ByteVec& command, const ByteVec& response, void *context)
        {
            static_cast<Samples*>(context)->TpmCallback(command, response);
        }

    protected:
        void Announce(const char *testName);
        void RecoverFromLockout();
        void SetCol(UINT16 col);
        int GetSystemTime(bool reset = false);
        void Sleep(int numMillisecs);
        TPM_HANDLE MakeHmacPrimaryWithPolicy(const TPM_HASH& policy, const ByteVec& keyAuth);
        TPM_HANDLE MakeStoragePrimary();
        TPM_HANDLE MakeDuplicatableStoragePrimary(const ByteVec& policyDigest);
        TPM_HANDLE MakeChildSigningKey(TPM_HANDLE parent, bool restricted);
        TPM_HANDLE MakeEndorsementKey();

        _TPMCPP Tpm2 tpm;
        _TPMCPP TpmTcpDevice *device;

        std::map<_TPMCPP TPM_CC, int> commandsInvoked;
        std::map<_TPMCPP TPM_RC, int> responses;
        std::vector<_TPMCPP TPM_CC> commandsImplemented;
};
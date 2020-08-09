/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once

using namespace TpmCpp;

extern bool UseSimulator;

// Beginning of the TPM NV indices range used by the samples
constexpr int NvRangeBegin = 2101;
constexpr int NvRangeEnd = 3000;

// Beginning of the TPM persistent objects range used by the samples
constexpr int PersRangeBegin = 2101;
constexpr int PersRangeEnd = 3000;

inline TPM_HANDLE RandomNvHandle()
{
    return TPM_HANDLE::NV(Helpers::RandomInt(NvRangeBegin, NvRangeEnd));
}

inline TPM_HANDLE RandomPersHandle()
{
    return TPM_HANDLE::Persistent(Helpers::RandomInt(PersRangeBegin, PersRangeEnd));
}

#define null  {}

class Samples {
    public:
        Samples();
        ~Samples();

        // The following methods demonstrate how TSS.C++ is used to perform TPM functions.
        void RunAllSamples();
        void RunDocSamples();
        void ArrayParameters();
        void PWAPAuth();
        void Errors();
        void Structures();
        void HMACSessions();
        void SigningPrimary();
        void SimplePolicy();
        void ThreeElementPolicy();
        void PolicyOrSample();

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
        void PolicyCpHashSample();
        void PolicyCounterTimerSample();
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

        void StartCallbacks();
        void FinishCallbacks();

        void PresentationSnippets();

        /// <summary> Checks to see that there are no keys left in the TPM </summary>
        void AssertNoLoadedKeys();

        void TpmCallback(const ByteVec& command, const ByteVec& response);

        static void TpmCallbackStatic(const ByteVec& command, const ByteVec& response, void *context)
        {
            static_cast<Samples*>(context)->TpmCallback(command, response);
        }

    protected:
        void Announce(const char *testName);
        void RecoverTpm();
        void SetColor(UINT16 col);
        int GetSystemTime(bool reset = false);
        void Sleep(int numMillisecs);
        TPM_HANDLE MakeHmacPrimaryWithPolicy(const TPM_HASH& policy, const ByteVec& keyAuth);
        TPM_HANDLE MakeStoragePrimary(AUTH_SESSION* sess = nullptr);
        TPM_HANDLE MakeDuplicableStoragePrimary(const ByteVec& policyDigest);
        TPM_HANDLE MakeChildSigningKey(TPM_HANDLE parent, bool restricted);
        TPM_HANDLE MakeEndorsementKey();
        void TestAuthSession(AUTH_SESSION& sess);

        _TPMCPP Tpm2 tpm;
        _TPMCPP TpmDevice *device;

        std::map<_TPMCPP TPM_CC, int> commandsInvoked;
        std::map<_TPMCPP TPM_RC, int> responses;
        std::vector<_TPMCPP TPM_CC> commandsImplemented;
};
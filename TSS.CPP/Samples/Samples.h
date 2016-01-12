/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#pragma once

using namespace TpmCpp;

class Samples {
    public:
        Samples();
        ~Samples();

        Tpm2& GetTpm() {
            return tpm;
        }

        // The following methods demonstrate how TSS.C++ is used to perform TPM functions.
        void RunAllSamples();
        void Rand();
        void PCR();
        void Locality();
        void Hash();
        void HMAC();
        void GetCapability();
        void NV();
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

        void TpmCallback(std::vector<BYTE> command, std::vector<BYTE> response);
        static void TpmCallbackStatic(std::vector<BYTE> command, std::vector<BYTE> response, void *context);

        // The following standalone routine(s) demonstrate minimal TPM functions.
        static void GetRandom();

    protected:
        void Announce(const char *testName);
        void RecoverFromLockout();
        void SetCol(UINT16 col);
        int GetSystemTime(bool reset = false);
        void Sleep(int numMillisecs);
        TPM_HANDLE MakeHmacPrimaryWithPolicy(TPMT_HA policy, std::vector<BYTE> keyAuth);
        TPM_HANDLE MakeStoragePrimary();
        TPM_HANDLE MakeDuplicatableStoragePrimary(std::vector<BYTE> policy);
        TPM_HANDLE MakeChildSigningKey(TPM_HANDLE parent, bool restricted);
        TPM_HANDLE MakeEndorsementKey();

        vector<BYTE> NullVec;
        _TPMCPP Tpm2 tpm;
        _TPMCPP TpmTcpDevice *device;

        std::map<_TPMCPP TPM_CC, int> commandsInvoked;
        std::map<_TPMCPP TPM_RC, int> responses;
        std::vector<_TPMCPP TPM_CC> commandsImplemented;
};
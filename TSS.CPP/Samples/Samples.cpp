/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "Samples.h"
#include "TpmConfig.h"

using namespace std;

static const TPMT_SYM_DEF_OBJECT Aes128Cfb {TPM_ALG_ID::AES, 128, TPM_ALG_ID::CFB};

// Verify that the sample did not leave any dangling handles in the TPM.
#define _check AssertNoLoadedKeys()


Samples::Samples()
{
    device = UseSimulator ? new TpmTcpDevice("127.0.0.1", 2321)
                          : (TpmDevice*)new TpmTbsDevice();

    if (!device || !device->Connect())
    {
        device = nullptr;
        throw runtime_error("Could not connect to TPM device.");
    }

    tpm._SetDevice(*device);

    if (UseSimulator)
    {
        // This code is normally not needed for a system/platform TPM.
        assert(device->PlatformAvailable() && device->ImplementsPhysicalPresence() &&
               device->PowerCtlAvailable() && device->LocalityCtlAvailable());

        device->PowerCycle();

        // Startup the TPM
        tpm.Startup(TPM_SU::CLEAR);
    }

    // If the simulator was not shut down cleanly ("disorderly shutdown") or a TPM app
    // crashed midway or has bugs the TPM may go into lockout or have objects abandoned
    // in its (limited) internal memory. Try to clean up and recover the TPM.
    RecoverTpm();

    // Install callbacks to collect command execution statistics.
    StartCallbacks();

    TpmConfig::Init(tpm);
} // Samples::Samples()

Samples::~Samples()
{
    if (UseSimulator)
    {
        // A clean shutdown results in fewer lockout errors.
        tpm.Shutdown(TPM_SU::CLEAR);
        device->PowerOff();
    }

    // The following routine finalizes and prints the function stats.
    FinishCallbacks();

    delete device;
}

void Samples::RunAllSamples()
{
    _check;
    RunDocSamples();

    _check;
    Rand();
    _check;
    DictionaryAttack();  // Run early in the test set to avoid lockout
    _check;
    Hash();
    _check;
    HMAC();
    _check;
    PCR();
    _check;
    Locality();
    _check;
    GetCapability();
    _check;
    NV();
    _check;
    PrimaryKeys();
    _check;
    AuthSessions();
    _check;
    Async();
    _check;
    PolicySimplest();
    _check;
    PolicyLocalitySample();
    _check;
    PolicyPCRSample();
    _check;
    ChildKeys();
    _check;
    PolicyORSample();
    _check;
    CounterTimer();
    _check;
    Attestation();
    _check;
    Admin();
    _check;
    PolicyCpHashSample();
    _check;
    PolicyCounterTimerSample();
    _check;
    PolicyWithPasswords();
    _check;
    Unseal();
    _check;
    Serializer();
    _check;
    SessionEncryption();
    _check;
    ImportDuplicate();
    _check;
    MiscAdmin();
    _check;
    RsaEncryptDecrypt();
    _check;
    Audit();
    _check;
    Activate();
    _check;
    SoftwareKeys();
    _check;
    PolicySigned();
    _check;
    PolicyAuthorizeSample();
    _check;
    PolicySecretSample();
    _check;
    EncryptDecryptSample();
    _check;
    PolicyWithPasswords();
    _check;
    SeededSession();
    _check;
    PolicyNVSample();
    _check;
    PolicyNameHashSample();
    _check;
    ReWrapSample();
    _check;
    BoundSession();
    _check;
    NVX();
} // Samples::RunAllSamples()

void Samples::Announce(const char *testName)
{
    SetColor(0);
    cout << endl;
    cout << "================================================================================" << endl;
    cout << "        " << testName << endl;
    cout << "================================================================================" << endl;
    cout << endl << flush;
    SetColor(1);
}


/// <summary> This routine throws an exception if there is a key or session left in the TPM </summary>
void AssertNoHandlesOfType(Tpm2& tpm, TPM_HT handleType, UINT32 rangeBegin = 0, UINT32 rangeEnd = 0x00FFFFFF)
{
    UINT32  startHandle = (handleType << 24) + rangeBegin,
            rangeSize = rangeEnd - rangeBegin;
    auto resp = tpm.GetCapability(TPM_CAP::HANDLES, startHandle, rangeSize);
    auto handles = dynamic_cast<TPML_HANDLE*>(&*resp.capabilityData)->handle;
    if (!handles.empty() && handles[0].handle < startHandle + rangeSize)
    {
        string errMsg = "!!! " + to_string(handles.size()) + " dangling " + EnumToStr(handleType) + " handle"
                      + (handles.size() == 1 ? "" : "s") + " left";
        cerr << errMsg << endl;
        throw runtime_error(errMsg);
    }
}

void Samples::AssertNoLoadedKeys()
{
    AssertNoHandlesOfType(tpm, TPM_HT::LOADED_SESSION);
    AssertNoHandlesOfType(tpm, TPM_HT::TRANSIENT);
    AssertNoHandlesOfType(tpm, TPM_HT::PERSISTENT, PersRangeBegin, PersRangeEnd);
    AssertNoHandlesOfType(tpm, TPM_HT::NV_INDEX, NvRangeBegin, NvRangeEnd);
}

void CleanHandlesOfType(Tpm2& tpm, TPM_HT handleType, UINT32 rangeBegin = 0, UINT32 rangeEnd = 0x00FFFFFF)
{
    UINT32  startHandle = (handleType << 24) + rangeBegin,
            rangeSize = rangeEnd - rangeBegin;
    GetCapabilityResponse resp;
    size_t count = 0;
    for(;;)
    {
        resp = tpm.GetCapability(TPM_CAP::HANDLES, startHandle, rangeSize);
        auto handles = dynamic_cast<TPML_HANDLE*>(&*resp.capabilityData)->handle;

        for (auto& h: handles)
        {
            if ((h.handle & 0x00FFFFFF) >= rangeEnd)
                break;
            if (handleType == TPM_HT::NV_INDEX)
            {
                tpm._AllowErrors().NV_UndefineSpace(TPM_RH::OWNER, h);
                if (!tpm._LastCommandSucceeded())
                    fprintf(stderr, "Failed to clean NV index 0x%08X: error %s\n", h.handle, EnumToStr(tpm._GetLastResponseCode()).c_str());
            }
            else if (handleType == TPM_HT::PERSISTENT)
            {
                tpm._AllowErrors().EvictControl(TPM_RH::OWNER, h, h);
                if (!tpm._LastCommandSucceeded())
                    fprintf(stderr, "Failed to clean persistent object 0x%08X: error %s\n", h.handle, EnumToStr(tpm._GetLastResponseCode()).c_str());
            }
            else
                tpm._AllowErrors().FlushContext(h);
            ++count;
        }

        if (!resp.moreData)
            break;
        auto newStart = (UINT32)handles.back().handle + 1;
        rangeSize -= newStart - startHandle;
        startHandle = newStart;
    }

    if (count)
        cout << "Cleaned " << count << " dangling " << EnumToStr(handleType) << " handle" << (count == 1 ? "" : "s") << endl;
    else
        cout << "No dangling " << EnumToStr(handleType) << " handles" << endl;
}

void Samples::RecoverTpm()
{
    tpm._AllowErrors()
       .DictionaryAttackLockReset(TPM_RH::LOCKOUT);

    if (!tpm._LastCommandSucceeded() && UseSimulator)
    {
        tpm._AllowErrors()
           .Shutdown(TPM_SU::CLEAR);

        // If this is a simulator, power-cycle it and clear just to be sure...
        device->PowerCycle();
        tpm.Startup(TPM_SU::CLEAR);

        // Clearing the TPM:
        // - Deletes persistent and transient objects in the Storage and Endorsement hierarchies;
        // - Deletes non-platform NV indices;
        // - Generates new Storage Primary Seed;
        // - Re-enables disabled hierarchies;
        // - Resets Owner, Endorsement, and Lockout auth values and auth policies;
        // - Resets clock, reset and restart counters.
        tpm.Clear(TPM_RH::PLATFORM);
    }

    CleanHandlesOfType(tpm, TPM_HT::LOADED_SESSION);
    CleanHandlesOfType(tpm, TPM_HT::TRANSIENT);
    CleanHandlesOfType(tpm, TPM_HT::PERSISTENT, PersRangeBegin, PersRangeEnd);
    CleanHandlesOfType(tpm, TPM_HT::NV_INDEX, NvRangeBegin, NvRangeEnd);
}


TPM_HANDLE Samples::MakeStoragePrimary(AUTH_SESSION* sess)
{
    TPMT_PUBLIC storagePrimaryTemplate(TPM_ALG_ID::SHA1,
                    TPMA_OBJECT::decrypt | TPMA_OBJECT::restricted
                        | TPMA_OBJECT::fixedParent | TPMA_OBJECT::fixedTPM
                        | TPMA_OBJECT::sensitiveDataOrigin | TPMA_OBJECT::userWithAuth,
                    null,           // No policy
                    TPMS_RSA_PARMS(Aes128Cfb, TPMS_NULL_ASYM_SCHEME(), 2048, 65537),
                    TPM2B_PUBLIC_KEY_RSA());
    // Create the key
    if (sess)
        tpm[*sess];
    return tpm.CreatePrimary(TPM_RH::OWNER, null, storagePrimaryTemplate, null, null)
              .handle;
}

TPM_HANDLE Samples::MakeDuplicableStoragePrimary(const ByteVec& policyDigest)
{
    TPMT_PUBLIC storagePrimaryTemplate(TPM_ALG_ID::SHA1,
                    TPMA_OBJECT::decrypt | TPMA_OBJECT::restricted
                        | TPMA_OBJECT::sensitiveDataOrigin | TPMA_OBJECT::userWithAuth,
                    policyDigest,
                    TPMS_RSA_PARMS(Aes128Cfb, TPMS_NULL_ASYM_SCHEME(), 2048, 65537),
                    TPM2B_PUBLIC_KEY_RSA());
    // Create the key
    return tpm.CreatePrimary(TPM_RH::OWNER, null, storagePrimaryTemplate, null, null)
              .handle;
}

/// <summary> Helper function to make a primary key with usePolicy set as specified </summary>
TPM_HANDLE Samples::MakeHmacPrimaryWithPolicy(const TPM_HASH& policy, const ByteVec& useAuth)
{
    TPM_ALG_ID hashAlg = TPM_ALG_ID::SHA1;
    ByteVec key { 5, 4, 3, 2, 1, 0 };
    TPMA_OBJECT extraAttr = (TPMA_OBJECT)0;

    if (!useAuth.empty())
        extraAttr = TPMA_OBJECT::userWithAuth;

    // HMAC key with policy as specified
    TPMT_PUBLIC templ(policy.hashAlg,
                      TPMA_OBJECT::sign | TPMA_OBJECT::fixedParent | TPMA_OBJECT::fixedTPM | extraAttr,
                      policy,
                      TPMS_KEYEDHASH_PARMS(TPMS_SCHEME_HMAC(hashAlg)),
                      TPM2B_DIGEST_KEYEDHASH());

    TPMS_SENSITIVE_CREATE sensCreate(useAuth, key);
    return tpm.CreatePrimary(TPM_RH::OWNER, sensCreate, templ, null, null).handle;
}

TPM_HANDLE Samples::MakeEndorsementKey()
{
    TPMT_PUBLIC storagePrimaryTemplate(TPM_ALG_ID::SHA1,
                    TPMA_OBJECT::decrypt | TPMA_OBJECT::restricted
                        | TPMA_OBJECT::fixedParent | TPMA_OBJECT::fixedTPM
                        | TPMA_OBJECT::sensitiveDataOrigin | TPMA_OBJECT::userWithAuth,
                    null,           // No policy
                    TPMS_RSA_PARMS(Aes128Cfb, TPMS_NULL_ASYM_SCHEME(), 2048, 65537),
                    TPM2B_PUBLIC_KEY_RSA());
    // Create the key
    return tpm.CreatePrimary(TPM_RH::ENDORSEMENT, null, storagePrimaryTemplate, null, null)
              .handle;
}

TPM_HANDLE Samples::MakeChildSigningKey(TPM_HANDLE parent, bool restricted)
{
    TPMA_OBJECT restrictedAttribute = restricted ? TPMA_OBJECT::restricted : 0;

    TPMT_PUBLIC templ(TPM_ALG_ID::SHA1,
        TPMA_OBJECT::sign | TPMA_OBJECT::fixedParent | TPMA_OBJECT::fixedTPM
            | TPMA_OBJECT::sensitiveDataOrigin | TPMA_OBJECT::userWithAuth | restrictedAttribute,
        null,  // No policy
        TPMS_RSA_PARMS(null, TPMS_SCHEME_RSASSA(TPM_ALG_ID::SHA1), 2048, 65537), // PKCS1.5
        TPM2B_PUBLIC_KEY_RSA());

    auto newSigningKey = tpm.Create(parent, null, templ, null, null);

    return tpm.Load(parent, newSigningKey.outPrivate, newSigningKey.outPublic);
}


void Samples::StartCallbacks()
{
    Announce("Installing callback");

    // Install a callback that is invoked after the TPM command has been executed
    tpm._SetResponseCallback(&Samples::TpmCallbackStatic, this);
}

void Samples::FinishCallbacks()
{
    Announce("Processing callback data");

    cout << "Commands invoked:" << endl;
    for (auto it = commandsInvoked.begin(); it != commandsInvoked.end(); ++it)
        cout << dec << setfill(' ') << setw(32) << EnumToStr(it->first) << ": count = " << it->second << endl;

    cout << endl << "Responses received:" << endl;
    for (auto it = responses.begin(); it != responses.end(); ++it)
        cout << dec << setfill(' ') << setw(32) << EnumToStr(it->first) << ": count = " << it->second << endl;

    cout << endl << "Commands not exercised:" << endl;
    for (auto it = commandsImplemented.begin(); it != commandsImplemented.end(); ++it)
    {
        if (commandsInvoked.find(*it) == commandsInvoked.end())
            cout << dec << setfill(' ') << setw(1) << EnumToStr(*it) << " ";
    }
    cout << endl;
    tpm._SetResponseCallback(NULL, NULL);
}


void Samples::Rand()
{
    Announce("Rand");

    auto rand = tpm.GetRandom(20);
    cout << "random bytes:      " << rand << endl;

    tpm.StirRandom({1, 2, 3});

    rand = tpm.GetRandom(20);
    cout << "more random bytes: " << rand << endl;
}

void Samples::SetColor(UINT16 col)
{
#ifdef _WIN32
    UINT16 fColor;
    switch (col) {
        case 0:
            fColor = FOREGROUND_GREEN;
            break;
        case 1:
            fColor = FOREGROUND_GREEN | FOREGROUND_BLUE | FOREGROUND_RED;
            break;
    };
    SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), fColor);
#endif
}

void Samples::PCR()
{
    Announce("PCR");

    // Modify PCR0 via a PCR_Event, and print out the value
    ByteVec toEvent { 1, 2, 3 };
    auto afterEvent = tpm.PCR_Event(TPM_HANDLE::Pcr(0), toEvent);

    cout << "PCR after event:" << endl << afterEvent[0].ToString() << endl;

    vector<TPMS_PCR_SELECTION> toReadArray = { {TPM_ALG_ID::SHA1, 0},
                                                    {TPM_ALG_ID::SHA256, 1} };

    // Get the initial values of two PCRs: one SHA1, and one SHA256
    auto initVals = tpm.PCR_Read(toReadArray);
    cout << "Initial value:" << endl << initVals.ToString(false) << endl;

    // Used by PCR_Read to read PCR0 in the SHA1 bank
    vector<TPMS_PCR_SELECTION> toReadPcr0 = { {TPM_ALG_ID::SHA1, 0} };

    // Modify PCR0 via event
    auto newVals = tpm.PCR_Event(TPM_HANDLE::Pcr(0), toEvent);
    auto pcrVals = tpm.PCR_Read(toReadPcr0);
    cout << "SHA1 After Event:" << endl << pcrVals.pcrValues[0].ToString() << endl;

    // Now modify the SHA1 PCR0 via extend
    TPM_HASH toExtend = TPM_HASH::FromHashOfString(TPM_ALG_ID::SHA1, "abc");
    tpm.PCR_Extend(TPM_HANDLE::Pcr(0), {toExtend});

    // Now read SHA1:PCR0 again
    pcrVals = tpm.PCR_Read(toReadPcr0);
    cout << "SHA1 After Extend:" << endl << pcrVals.pcrValues[0].ToString() << endl;
    TPM_HASH pcrAtEnd(TPM_ALG_ID::SHA1, pcrVals.pcrValues[0]);

    //Check that this answer is what we expect
    TPM_HASH pcrSim(TPM_ALG_ID::SHA1, initVals.pcrValues[0]);
    pcrSim.Event(toEvent);
    pcrSim.Extend(toExtend);

    if (pcrSim == pcrAtEnd)
        cout << "PCR values correct" << endl;
    else {
        cout << "Error: PCR values NOT correct" << endl;
        _ASSERT(FALSE);
    }

    // Extend a resettable PCR
    UINT32 resettablePcr = 16;
    tpm.PCR_Event(TPM_HANDLE::Pcr(resettablePcr), { 1, 2, 3 });
    auto resettablePcrVal = tpm.PCR_Read({{TPM_ALG_ID::SHA1, resettablePcr}});
    cout << "Resettable PCR before reset: " << resettablePcrVal.pcrValues[0] << endl;

    tpm.PCR_Reset(TPM_HANDLE::Pcr(resettablePcr));
    resettablePcrVal = tpm.PCR_Read({{TPM_ALG_ID::SHA1, resettablePcr}});
    cout << "After reset:                 " << resettablePcrVal.pcrValues[0] << endl;

    // Check it really is all zeros
    _ASSERT(resettablePcrVal.pcrValues[0].buffer == ByteVec(20));
} // PCR()

void Samples::Locality()
{
    if (!tpm._GetDevice().LocalityCtlAvailable())
    {
        cout << endl << "~~~~ SAMPLE Locality() SKIPPED ~~~~" << endl << endl;
        return;
    }
    Announce("Locality");

    // Extend the resettable PCR
    UINT32 locTwoResettablePcr = 21;

    tpm._GetDevice().SetLocality(2);
    tpm.PCR_Event(TPM_HANDLE::Pcr(locTwoResettablePcr), { 1, 2, 3, 4 });
    tpm._GetDevice().SetLocality(0);

    vector<TPMS_PCR_SELECTION> pcrSel = {{TPM_ALG_ID::SHA1, locTwoResettablePcr}};
    auto resettablePcrVal = tpm.PCR_Read(pcrSel);
    cout << "PCR before reset at locality 2: " << resettablePcrVal.pcrValues[0].buffer << endl;

    // Should fail - tell Tpm2 not to generate an exception
    tpm._ExpectError(TPM_RC::LOCALITY)
       .PCR_Reset(TPM_HANDLE::Pcr(locTwoResettablePcr));

    // Should fail - tell Tpm2 not to generate an exception (second way)
    tpm._DemandError()
       .PCR_Reset(TPM_HANDLE::Pcr(locTwoResettablePcr));

    // Should succeed at locality 2
    tpm._GetDevice().SetLocality(2);
    tpm.PCR_Reset(TPM_HANDLE::Pcr(locTwoResettablePcr));

    // Return to locality zero
    tpm._GetDevice().SetLocality(0);
    resettablePcrVal = tpm.PCR_Read({{TPM_ALG_ID::SHA1, locTwoResettablePcr}});
    cout << "PCR After reset at locality 2:  " << resettablePcrVal.pcrValues[0].buffer << endl;
} // Locality()

void Samples::Hash()
{
    Announce("Hash");

    vector<TPM_ALG_ID> hashAlgs = { TPM_ALG_ID::SHA1, TPM_ALG_ID::SHA256 };
    ByteVec accumulator;
    ByteVec data1 { 1, 2, 3, 4, 5, 6 };

    cout << "Simple Hashing" << endl;

    for (auto it = hashAlgs.begin(); it != hashAlgs.end(); it++)
    {
        auto hashResponse = tpm.Hash(data1, *it, TPM_RH_NULL);
        auto expected = Crypto::Hash(*it, data1);

        _ASSERT(hashResponse.outHash == expected);
        cout << "Hash:: " << EnumToStr(*it) << endl;
        cout << "Expected:      " << expected << endl;
        cout << "TPM generated: " << hashResponse.outHash << endl;
    }

    cout << "Hash sequences" << endl;

    for (auto iterator = hashAlgs.begin(); iterator != hashAlgs.end(); iterator++) {
        auto hashHandle = tpm.HashSequenceStart(null, *iterator);
        accumulator.clear();

        for (int j = 0; j < 10; j++) {
            // Note the syntax below. If no explicit sessions are provided then the
            // library automatically uses PWAP with the authValue contained in the handle.
            // If you want to mix PWAP and other sessions then you can use the psuedo-PWAP
            // session as below.
            AUTH_SESSION mySession = AUTH_SESSION::PWAP();
            tpm[mySession].SequenceUpdate(hashHandle, data1);
            accumulator = Helpers::Concatenate(accumulator, data1);
        }

        accumulator = Helpers::Concatenate(accumulator, data1);

        // Note that the handle is flushed by the TPM when the sequence is completed
        auto hashVal = tpm.SequenceComplete(hashHandle, data1, TPM_RH_NULL);
        auto expected = Crypto::Hash(*iterator, accumulator);

        _ASSERT(hashVal.result == expected);
        cout << "Hash:: " << EnumToStr(*iterator) << endl;
        cout << "Expected:      " << expected << endl;
        cout << "TPM generated: " << hashVal.result << endl;
    }

    // We can also do an "event sequence"
    auto hashHandle = tpm.HashSequenceStart(null, TPM_ALG_NULL);
    accumulator.clear();

    for (int j = 0; j < 10; j++) {
        tpm.SequenceUpdate(hashHandle, data1);
        accumulator = Helpers::Concatenate(accumulator, data1);
    }

    accumulator = Helpers::Concatenate(accumulator, data1);

    // Note that the handle is flushed by the TPM when the sequence is completed
    auto initPcr = tpm.PCR_Read({{TPM_ALG_ID::SHA1, 0}});
    auto hashVal2 = tpm.EventSequenceComplete(TPM_HANDLE::Pcr(0), hashHandle, data1);
    auto expected = Crypto::Hash(TPM_ALG_ID::SHA1, accumulator);
    auto finalPcr = tpm.PCR_Read({{TPM_ALG_ID::SHA1, 0}});

    // Is this what we expect?
    TPM_HASH expectedPcr(TPM_ALG_ID::SHA1, initPcr.pcrValues[0]);
    expectedPcr.Extend(expected);

    if (expectedPcr == finalPcr.pcrValues[0])
        cout << "EventSequenceComplete gives expected answer:  " << endl << expectedPcr.ToString(false) << endl;
    _ASSERT(expectedPcr == finalPcr.pcrValues[0]);
} // Hash()

void Samples::HMAC()
{
    Announce("HMAC");

    // Key and data to be HMACd
    ByteVec key { 5, 4, 3, 2, 1, 0 };
    ByteVec data1 { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };
    auto hashAlg = TPM_ALG_ID::SHA1;

    // To do an HMAC we need to load a key into the TPM.  A primary key is easiest.
    // template for signing/symmetric HMAC key with data originating externally
    TPMT_PUBLIC templ(TPM_ALG_ID::SHA256, TPMA_OBJECT::sign | TPMA_OBJECT::fixedParent |
                      TPMA_OBJECT::fixedTPM |  TPMA_OBJECT::userWithAuth,
                      null,
                      TPMS_KEYEDHASH_PARMS(TPMS_SCHEME_HMAC(hashAlg)),
                      TPM2B_DIGEST_KEYEDHASH());

    // The key is passed in in the SENSITIVE_CREATE structure
    TPMS_SENSITIVE_CREATE sensCreate(null, key);

    // "Create" they key based on the externally provided keying data
    auto newPrimary = tpm.CreatePrimary(TPM_RH::OWNER, sensCreate, templ, null, null);
    TPM_HANDLE keyHandle = newPrimary.handle;
    TPM_HANDLE hmacHandle= tpm.HMAC_Start(keyHandle, null, TPM_ALG_ID::SHA1);

    tpm.SequenceUpdate(hmacHandle, data1);

    auto hmacDigest = tpm.SequenceComplete(hmacHandle, data1, TPM_RH_NULL);
    auto data = Helpers::Concatenate(data1, data1);
    auto expectedHmac = Crypto::HMAC(hashAlg, key, data);

    _ASSERT(expectedHmac == hmacDigest.result);

    cout <<  "HMAC[SHA1] of " << data << endl <<
             "with key      " << key << endl <<
             "           =  " << hmacDigest.result << endl;

    // We can also just TPM2_Sign() with an HMAC key
    auto sig = tpm.Sign(keyHandle, data, TPMS_NULL_SIG_SCHEME(), null);
    TPM_HASH *sigIs = dynamic_cast<TPM_HASH*>(&*sig);

    cout << "HMAC[SHA1] of " << data << endl <<
            "with key      " << key << endl <<
            "           =  " << sigIs->digest << endl;

    // Or use the HMAC signing command
    ByteVec sig3 = tpm.HMAC(keyHandle, data, TPM_ALG_ID::SHA1);
    cout << "HMAC[SHA1] of " << data << endl <<
            "with key      " << key << endl <<
            "           =  " << sig3 << endl;

    tpm.FlushContext(keyHandle);
} // HMAC()

void Samples::GetCapability()
{
    Announce("GetCapability");

    UINT32 startVal = 0;

    cout << "Algorithms:" << endl;

    // For the first example we show how to get a batch (8) properties at a time.
    // For simplicity, subsequent samples just get one at a time: avoiding the
    // nested loop.
    while (true)
    {
        auto caps = tpm.GetCapability(TPM_CAP::ALGS, startVal, 8);
        TPML_ALG_PROPERTY *props = dynamic_cast<TPML_ALG_PROPERTY*>(&*caps.capabilityData);

        // Print alg name and properties
        for (auto p = props->algProperties.begin(); p != props->algProperties.end(); p++)
            cout << setw(16) << EnumToStr(p->alg) << ": " << EnumToStr(p->algProperties) << endl;

        if (!caps.moreData)
            break;
        startVal = (props->algProperties[props->algProperties.size() - 1].alg) + 1;
    }

    cout << "Commands:" << endl;
    startVal = 0;

    while (true) {
        auto caps = tpm.GetCapability(TPM_CAP::COMMANDS, startVal, 32);
        auto comms = dynamic_cast<TPML_CCA*>(&*caps.capabilityData);

        for (auto it = comms->commandAttributes.begin(); it != comms->commandAttributes.end(); it++)
        {
            // Decode the packed structure
            TPM_CC cc = *it & 0xFFFF;
            TPMA_CC maskedAttr = *it & 0xFFff0000;

            cout << "Command:" << EnumToStr(cc) << ": ";
            cout << EnumToStr(maskedAttr) << endl;

            commandsImplemented.push_back(cc);
            startVal = cc;
        }
        cout << endl;

        if (!caps.moreData)
            break;
        startVal++;
    }

    startVal = 0;
    cout << "PCRS: " << endl;
    auto caps2 = tpm.GetCapability(TPM_CAP::PCRS, 0, 1);
    auto pcrs = dynamic_cast<TPML_PCR_SELECTION*>(&*caps2.capabilityData);

    for (auto it = pcrs->pcrSelections.begin(); it != pcrs->pcrSelections.end(); it++)
    {
        cout << EnumToStr(it->hash) << "\t";
        auto pcrsWithThisHash = it->ToArray();

        for (auto p = pcrsWithThisHash.begin(); p != pcrsWithThisHash.end(); p++)
            cout << *p << " ";
        cout << endl;
    }
} // GetCapability()

void Samples::NVX()
{
    if (!tpm._GetDevice().PlatformAvailable() || !tpm._GetDevice().ImplementsPhysicalPresence())
    {
        cout << endl << "~~~~ SAMPLE NVX() SKIPPED ~~~~" << endl << endl;
        return;
    }
    Announce("NVX");

    ByteVec nvAuth { 1, 5, 1, 1 };
    TPM_HANDLE nvHandle = RandomNvHandle();

    // Try to delete the slot if it exists
    tpm._AllowErrors()
       .NV_UndefineSpace(TPM_RH::OWNER, nvHandle);

    // The index (in the platform hierarchy) may exist as the result of this test failure
    bool exists = !tpm._LastCommandSucceeded() && tpm._GetLastResponseCode() != TPM_RC::HANDLE;

    // Demonstrating the use of NV_UndefineSpaceSpecial .  We must have a policy...
    PolicyTree p(PolicyCommandCode(TPM_CC::NV_UndefineSpaceSpecial, ""));
    auto policyHash = p.GetPolicyDigest(TPM_ALG_ID::SHA1);
    cout << "Prepared policy session" << endl;

    if (exists)
        cout << "! Dangling Platform NV slot found !" << endl;
    else
    {
        TPMS_NV_PUBLIC nvTemplate5(nvHandle,                // Index handle
                                   TPM_ALG_ID::SHA1,        // Name alg
                                   TPMA_NV::AUTHREAD | TPMA_NV::AUTHWRITE | TPMA_NV::EXTEND
                                    | TPMA_NV::POLICY_DELETE | TPMA_NV::PLATFORMCREATE,
                                   policyHash,
                                   20);                     // Size in bytes

        tpm.NV_DefineSpace(TPM_RH::PLATFORM, nvAuth, nvTemplate5);
        cout << "Created NV slot under Platform hierarchy" << endl;
    }

    auto nvPub = tpm.NV_ReadPublic(nvHandle);
    nvHandle.SetName(nvPub.nvName);
    cout << "Platform NV slot pub:" << nvPub.ToString() << endl;

    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    AUTH_SESSION pwapSession = AUTH_SESSION::PWAP();

    cout << "Trying deletion with empty policy first..." << endl;

    tpm._GetDevice().AssertPhysicalPresence(true);
    tpm._Sessions(s/*, pwapSession*/)._ExpectError(TPM_RC::POLICY_FAIL)
       .NV_UndefineSpaceSpecial(nvHandle, TPM_RH::PLATFORM);
    cout << "Failed to delete the Platform NV slot without the proper policy" << endl;

    p.Execute(tpm, s);

    // Empty PW session will be automatically added by the TSS.CPP
    tpm._Sessions(s/*, pwapSession*/)
       .NV_UndefineSpaceSpecial(nvHandle, TPM_RH::PLATFORM);
    cout << "Deleted the Platform NV slot." << endl;

    tpm._GetDevice().AssertPhysicalPresence(false);
    tpm.FlushContext(s);
    cout << "---- NVX successfully finished ----" << endl;
} // NVX()

void Samples::NV()
{
    Announce("NV");

    // Several types of NV-slot use are demonstrated here: simple, counter, bitfield, and extendable

    ByteVec nvAuth = Helpers::RandomBytes(20);
    TPM_HANDLE nvHandle = RandomNvHandle();

    // Try to delete the slot if it exists
    tpm._AllowErrors()
       .NV_UndefineSpace(TPM_RH::OWNER, nvHandle);

    // CASE 1 - Simple NV-slot: Make a new simple NV slot, 16 bytes, RW with auth
    TPMS_NV_PUBLIC nvTemplate(nvHandle,           // Index handle
                              TPM_ALG_ID::SHA256, // Name-alg
                              TPMA_NV::AUTHREAD | // Attributes
                              TPMA_NV::AUTHWRITE,
                              null,            // Policy
                              16);                // Size in bytes

    tpm.NV_DefineSpace(TPM_RH::OWNER, nvAuth, nvTemplate);

    // We have set the authVal to be nvAuth, so set it in the handle too.
    nvHandle.SetAuth(nvAuth);

    // Write some data
    ByteVec toWrite { 1, 2, 3, 4, 5, 4, 3, 2, 1 };
    tpm.NV_Write(nvHandle, nvHandle, toWrite, 0);

    // And read it back and see if it is good
    ByteVec dataRead = tpm.NV_Read(nvHandle, nvHandle, 16, 0);
    cout << "Data read from nv-slot:   " << dataRead << endl;

    // And make sure that it's good
    _ASSERT(equal(toWrite.begin(), toWrite.end(), dataRead.begin()));

    // We can also read the public area
    auto nvPub = tpm.NV_ReadPublic(nvHandle);
    cout << "NV Slot public area:" << endl << nvPub.ToString(false) << endl;

    // And then delete it
    tpm.NV_UndefineSpace(TPM_RH::OWNER, nvHandle);

    // CASE 2 - Counter NV-slot
    TPMS_NV_PUBLIC nvTemplate2(nvHandle,            // Index handle
                               TPM_ALG_ID::SHA256,  // Name-alg
                               TPMA_NV::AUTHREAD  | // Attributes
                               TPMA_NV::AUTHWRITE |
                               TPMA_NV::COUNTER,
                               null,             // Policy
                               8);                  // Size in bytes

    tpm.NV_DefineSpace(TPM_RH::OWNER, nvAuth, nvTemplate2);

    // We have set the authVal to be nvAuth, so set it in the handle too.
    nvHandle.SetAuth(nvAuth);

    // Should not be able to write (increment only)
    tpm._ExpectError(TPM_RC::ATTRIBUTES)
       .NV_Write(nvHandle, nvHandle, toWrite, 0);

    // Should not be able to read before the first increment
    tpm._ExpectError(TPM_RC::NV_UNINITIALIZED)
       .NV_Read(nvHandle, nvHandle, 8, 0);

    // First increment
    tpm.NV_Increment(nvHandle, nvHandle);

    // Should now be able to read
    ByteVec beforeIncrement = tpm.NV_Read(nvHandle, nvHandle, 8, 0);
    cout << "Initial counter data:     " << beforeIncrement << endl;

    // Should be able to increment
    for (int j = 0; j < 5; j++)
        tpm.NV_Increment(nvHandle, nvHandle);

    // And make sure that it's good
    ByteVec afterIncrement = tpm.NV_Read(nvHandle, nvHandle, 8, 0);
    cout << "After 5 increments:       " << afterIncrement << endl;

    // And then delete it
    tpm.NV_UndefineSpace(TPM_RH::OWNER, nvHandle);

    // CASE 3 - Bitfield
    TPMS_NV_PUBLIC nvTemplate3(nvHandle,            // Index handle
                               TPM_ALG_ID::SHA256,  // Name-alg
                               TPMA_NV::AUTHREAD  | // Attributes
                               TPMA_NV::AUTHWRITE |
                               TPMA_NV::BITS,      
                               null,             // Policy
                               8);                  // Size in bytes

    tpm.NV_DefineSpace(TPM_RH::OWNER, nvAuth, nvTemplate3);

    // We have set the authVal to be nvAuth, so set it in the handle too.
    nvHandle.SetAuth(nvAuth);

    // Should not be able to write (bitfield)
    tpm._ExpectError(TPM_RC::ATTRIBUTES)
       .NV_Write(nvHandle, nvHandle, toWrite, 0);

    // Should not be able to read before first written
    tpm._ExpectError(TPM_RC::NV_UNINITIALIZED)
       .NV_Read(nvHandle, nvHandle, 8, 0);

    // Should not be able to increment
    tpm._ExpectError(TPM_RC::ATTRIBUTES)
       .NV_Increment(nvHandle, nvHandle);

    // Should be able set bits
    cout << "Bit setting:" << endl;
    UINT64 bit = 1;
    for (int j = 0; j < 64; j++)
    {
        tpm.NV_SetBits(nvHandle, nvHandle, bit);
        ByteVec bits = tpm.NV_Read(nvHandle, nvHandle, 8, 0);
        cout << setfill(' ') << setw(4) << dec << j << " : " << hex << bits << endl;
        bit = bit << 1;
    }

    // And then delete it
    tpm.NV_UndefineSpace(TPM_RH::OWNER, nvHandle);

    // CASE 4 - Extendable
    TPMS_NV_PUBLIC nvTemplate4(nvHandle,            // Index handle
                               TPM_ALG_ID::SHA1,    // Name+extend-alg
                               TPMA_NV::AUTHREAD  | // Attributes
                               TPMA_NV::AUTHWRITE |
                               TPMA_NV::EXTEND,
                               null,             // Policy
                               20);                 // Size in bytes

    tpm.NV_DefineSpace(TPM_RH::OWNER, nvAuth, nvTemplate4);

    // We have set the authVal to be nvAuth, so set it in the handle too.
    nvHandle.SetAuth(nvAuth);

    // Should not be able to write (bitfield)
    tpm._ExpectError(TPM_RC::ATTRIBUTES)
       .NV_Write(nvHandle, nvHandle, toWrite, 0);

    // Should not be able to read before first written
    tpm._ExpectError(TPM_RC::NV_UNINITIALIZED)
       .NV_Read(nvHandle, nvHandle, 8, 0);

    // Should not be able to increment
    tpm._ExpectError(TPM_RC::ATTRIBUTES)
       .NV_Increment(nvHandle, nvHandle);

    // Should be able to extend
    TPM_HASH toExtend = TPM_HASH::FromHashOfString(TPM_ALG_ID::SHA256, "abc");
    tpm.NV_Extend(nvHandle, nvHandle, toExtend);

    // Read the extended value and print it
    ByteVec extendedData = tpm.NV_Read(nvHandle, nvHandle, 20, 0);
    cout << "Extended NV slot:" << extendedData << endl;

    // Check the result is correct
    _ASSERT(TPM_HASH(TPM_ALG_ID::SHA1).Extend(toExtend) == extendedData);

    // And then delete it
    tpm.NV_UndefineSpace(TPM_RH::OWNER, nvHandle);
    cout << "Deleted the NV slot" << endl;

    if (tpm._GetDevice().PlatformAvailable())
    {
        // Now demonstrate NV_WriteLock
        TPMS_NV_PUBLIC nvTemplate6(nvHandle,                // Index handle
                                   TPM_ALG_ID::SHA1,        // Name+extend-alg
                                   TPMA_NV::AUTHREAD |      // attributes
                                   TPMA_NV::AUTHWRITE |
                                   TPMA_NV::PLATFORMCREATE |
                                   TPMA_NV::WRITEDEFINE,
                                   null,                 // Policy
                                   20);                     // Size in bytes

        tpm.NV_DefineSpace(TPM_RH::PLATFORM, nvAuth, nvTemplate6);

        // We have set the authVal to be nvAuth, so set it in the handle too.
        nvHandle.SetAuth(nvAuth);
        tpm.NV_WriteLock(nvHandle, nvHandle);

        // And then delete it
        tpm.NV_UndefineSpace(TPM_RH::PLATFORM, nvHandle);
    }

    // Demonstrating NV_ChangeAuth. To issue this command you must use ADMIN auth
    // We must have a policy...
    PolicyTree p3(PolicyCommandCode(TPM_CC::NV_ChangeAuth, ""));
    TPM_HASH policyHash = p3.GetPolicyDigest(TPM_ALG_ID::SHA1);

    TPMS_NV_PUBLIC nvTemplate7(nvHandle,           // Index handle
                               TPM_ALG_ID::SHA1,   // Name+extend-alg
                               TPMA_NV::AUTHREAD | // Attributes
                               TPMA_NV::AUTHWRITE, 
                               policyHash,         // Policy digest
                               20);                // Size in bytes

    tpm.NV_DefineSpace(TPM_RH::OWNER, nvAuth, nvTemplate7);

    auto nvPub3 = tpm.NV_ReadPublic(nvHandle);
    nvHandle.SetName(nvPub3.nvName);

    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    p3.Execute(tpm, s);

    tpm.NV_Write(nvHandle, nvHandle, toWrite, 0);

    // Can change the authVal
    ByteVec newAuth { 3, 1, 4, 1 };
    tpm(s).NV_ChangeAuth(nvHandle, newAuth);
    tpm.FlushContext(s);

    // TODO: Does not work now
    // TSS.C++ tracks changes of auth-values and updates the relevant handle.
    //_ASSERT_EXPR(nvHandle.GetAuth() == newAuth, L"Auth value stored in NV handlewas was not updated");

    // Can no longer read with old password
    tpm._ExpectError(TPM_RC::AUTH_FAIL)
       .NV_Read(nvHandle, nvHandle, 16, 0);

    // But can read with the new one
    nvHandle.SetAuth(newAuth);
    tpm.NV_Read(nvHandle, nvHandle, 16, 0);

    // And then delete it
    tpm.NV_UndefineSpace(TPM_RH::OWNER, nvHandle);
} // NV()

void Samples::TpmCallback(const ByteVec& command, const ByteVec& response)
{
    // Extract the command and responses codes from the buffers.
    // Both are 4 bytes long starting at byte 6
    UINT32 *commandCodePtr = (UINT32*) &command[6];
    UINT32 *responseCodePtr = (UINT32*) &response[6];

    TPM_CC cmdCode = (TPM_CC)ntohl(*commandCodePtr);
    TPM_RC rcCode = (TPM_RC)ntohl(*responseCodePtr);

    // Strip any parameter decorations
    rcCode = Tpm2::ResponseCodeFromTpmError(rcCode);

    commandsInvoked[cmdCode]++;
    responses[rcCode]++;
}

void Samples::PrimaryKeys()
{
    Announce("PrimaryKeys");

    // To create a primary key the TPM must be provided with a template.
    // This is for an RSA1024 signing key.
    TPMT_PUBLIC templ(TPM_ALG_ID::SHA1,
                      TPMA_OBJECT::sign | TPMA_OBJECT::fixedParent | TPMA_OBJECT::fixedTPM
                        | TPMA_OBJECT::sensitiveDataOrigin | TPMA_OBJECT::userWithAuth,
                      null,  // No policy
                      TPMS_RSA_PARMS(null, TPMS_SCHEME_RSASSA(TPM_ALG_ID::SHA256), 1024, 65537),
                      TPM2B_PUBLIC_KEY_RSA());

    // Set the use-auth for the nex key. Note the second parameter is
    // NULL because we are asking the TPM to create a new key.
    ByteVec userAuth = { 1, 2, 3, 4 };
    TPMS_SENSITIVE_CREATE sensCreate(userAuth, null);

    // Create the key (no PCR-state captured)
    auto newPrimary = tpm._AllowErrors()
                         .CreatePrimary(TPM_RH::OWNER, sensCreate, templ, null, null);
    if (!tpm._LastCommandSucceeded())
    {
        // Some TPMs only allow primary keys of no lower than a particular strength.
        _ASSERT(tpm._GetLastResponseCode() == TPM_RC::VALUE);
        dynamic_cast<TPMS_RSA_PARMS*>(&*templ.parameters)->keyBits = 2048;
        newPrimary = tpm.CreatePrimary(TPM_RH::OWNER, sensCreate, templ, null, null);
    }

    // Print out the public data for the new key. Note the parameter to
    // ToString() "pretty-prints" the byte-arrays.
    cout << "New RSA primary key" << endl << newPrimary.outPublic.ToString(false) << endl;

    cout << "Name of new key:" << endl;
    cout << " Returned by TPM " << newPrimary.name << endl;
    cout << " Calculated      " << newPrimary.outPublic.GetName() << endl;
    cout << " Set in handle   " << newPrimary.handle.GetName() << endl;
    _ASSERT(newPrimary.name == newPrimary.outPublic.GetName());

    // Sign something with the new key.  First set the auth-value in the handle
    TPM_HANDLE& signKey = newPrimary.handle;
    signKey.SetAuth(userAuth);

    TPM_HASH dataToSign = TPM_HASH::FromHashOfString(TPM_ALG_ID::SHA256, "abc");

    auto sig = tpm.Sign(signKey, dataToSign, TPMS_NULL_SIG_SCHEME(), null);
    cout << "Data to be signed:" << dataToSign.digest << endl;
    cout << "Signature:" << endl << sig->ToString(false) << endl;

    // We can put the primary key into NV with EvictControl
    TPM_HANDLE persistentHandle = TPM_HANDLE::Persistent(1000);

    // First delete anything that might already be there
    tpm._AllowErrors().EvictControl(TPM_RH::OWNER, persistentHandle, persistentHandle);

    // Make our primary persistent
    tpm.EvictControl(TPM_RH::OWNER, newPrimary.handle, persistentHandle);

    // Flush the old one
    tpm.FlushContext(newPrimary.handle);

    // ReadPublic of the new persistent one
    auto persistentPub = tpm.ReadPublic(persistentHandle);
    cout << "Public part of persistent primary" << endl << persistentPub.ToString(false);

    // And delete it
    tpm.EvictControl(TPM_RH::OWNER, persistentHandle, persistentHandle);
} // PrimaryKeys()

void Samples::TestAuthSession(AUTH_SESSION& sess)
{
    TPM_HANDLE hPrim = MakeStoragePrimary(&sess);

    // Template for new data blob.
    TPMT_PUBLIC inPub(TPM_ALG_ID::SHA1,
                      TPMA_OBJECT::fixedParent | TPMA_OBJECT::fixedTPM,
                      null,
                      TPMS_KEYEDHASH_PARMS(TPMS_NULL_SCHEME_KEYEDHASH()),
                      TPM2B_DIGEST_KEYEDHASH());

    ByteVec dataToSeal = Helpers::RandomBytes(20);
    ByteVec authValue = Helpers::RandomBytes(20);
    TPMS_SENSITIVE_CREATE sensCreate(authValue, dataToSeal);

    // Ask the TPM to create the key. We don't care about the PCR at creation.
    auto sealed = tpm[sess].Create(hPrim, sensCreate, inPub, null, null);

    TPM_HANDLE hSealed = tpm[sess].Load(hPrim, sealed.outPrivate, sealed.outPublic);

    auto persSealed = TPM_HANDLE::Persistent(2);
    tpm[sess].EvictControl(TPM_RH::OWNER, hSealed, persSealed);
    tpm.FlushContext(hSealed);

    persSealed.SetAuth(authValue);
    persSealed.SetName(sealed.outPublic.GetName());
    TPM2B_PRIVATE newPriv = tpm[sess].ObjectChangeAuth(persSealed, hPrim, null);

    // And clean up using the same HMAC session
    tpm[sess].EvictControl(TPM_RH::OWNER, persSealed, persSealed);
    tpm.FlushContext(hPrim);

    if (UseSimulator)
    {
        // And just for the hell of it... (it's a simulator anyway)
        tpm[sess].Clear(TPM_RH::LOCKOUT);
    }

    tpm.FlushContext(sess);
} // TestAuthSession

void Samples::AuthSessions()
{
    Announce("AuthSessions");

    // Start a simple HMAC authorization session (no salt, no encryption, no bound-object)
    AUTH_SESSION sess = tpm.StartAuthSession(TPM_SE::HMAC, TPM_ALG_ID::SHA1);

    TestAuthSession(sess);
} // AuthSessions()

void Samples::Async()
{
    Announce("Async");

    // First do a fast operation
    cout << "Waiting for GetRandom()";
    tpm.Async.GetRandom(16);

    while (!tpm._GetDevice().ResponseIsReady())
    {
        cout << "." << flush;
        Sleep(30);
    }

    cout << endl << "Done" << endl;
    auto randData = tpm.Async.GetRandomComplete();
    cerr << "Async random data: " << randData << endl;

    // Now do a slow operation
    TPMT_PUBLIC templ(TPM_ALG_ID::SHA1,                   // Name alg
                      TPMA_OBJECT::sign | TPMA_OBJECT::fixedParent | TPMA_OBJECT::fixedTPM
                        | TPMA_OBJECT::sensitiveDataOrigin | TPMA_OBJECT::userWithAuth,
                      null,  // No policy
                      TPMS_RSA_PARMS(null, TPMS_SCHEME_RSASSA(TPM_ALG_ID::SHA256), 2048, 65537),
                      TPM2B_PUBLIC_KEY_RSA());

    ByteVec userAuth = { 1, 2, 3, 4 };
    TPMS_SENSITIVE_CREATE sensCreate(userAuth, null);

    // Start the slow key creation
    cout << "Waiting for CreatePrimary()";
    tpm.Async.CreatePrimary(TPM_RH::OWNER, sensCreate, templ, null, null);

    // Spew dots while we wait...
    while (!tpm._GetDevice().ResponseIsReady())
    {
        cout << "." << flush;
        Sleep(30);
    }

    cout << endl << "Done" << endl;

    auto newPrimary = tpm.Async.CreatePrimaryComplete();

    // And show we actually did something
    cout << "Asynchronously created primary key name: " << endl << newPrimary.name << endl;

    tpm.FlushContext(newPrimary.handle);
} // Async()

void Samples::PolicySimplest()
{
    Announce("PolicySimplest");

    // A TPM policy is a list or tree of Policy Assertions represented as a
    // vector<PABase*> in TSS.C++. The simplest policy tree is a single element.
    // The following policy indicates that the only operation that can be
    // performed is TPM2_HMAC_Start.
    PolicyTree p(TpmCpp::PolicyCommandCode(TPM_CC::HMAC_Start, ""));

    // Get the policy digest
    TPM_HASH policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);

    // Make an object with this policy hash
    TPM_HANDLE hmacKeyHandle = MakeHmacPrimaryWithPolicy(policyDigest, null);

    // Try to use the key using an authValue (not policy) - This should fail
    tpm._ExpectError(TPM_RC::AUTH_UNAVAILABLE)
       .HMAC_Start(hmacKeyHandle, null, TPM_ALG_ID::SHA1);

    // Now use policy
    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);

    // Execute the policy using the session. This issues a sequence of TPM
    // operations to "prove" to the TPM that the policy is satisfied. In this very
    // simple case Execute() will call tpm.PolicyCommandCode(s, TPM_CC:HMAC_Start).
    p.Execute(tpm, s);

    // Check that the policy-hash in the session is really what we calculated it to be.
    // If this is not the case then the attempt to use the policy-protected object below will fail.
    ByteVec digest = tpm.PolicyGetDigest(s);
    cout << "Calculated policy digest  : " << policyDigest.digest << endl;
    cout << "TPM reported policy digest: " << digest << endl;

    // Execute ReadPublic - This should succeed
    auto hmacSessionHandle = tpm[s].HMAC_Start(hmacKeyHandle, null, TPM_ALG_ID::SHA1);
    tpm.FlushContext(s);
    tpm.FlushContext(hmacSessionHandle);

    // But if we try to use the key in another way this should fail
    s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    p.Execute(tpm, s);

    // Note that this command would fail with a different error even if you knew the auth-value.
    tpm[s]._ExpectError(TPM_RC::POLICY_CC)
          .Unseal(hmacKeyHandle);

    // Clean up
    tpm.FlushContext(hmacKeyHandle);
    tpm.FlushContext(s);
} // PolicySimplest()


void Samples::PolicyLocalitySample()
{
    Announce("PolicyLocality");

    // A TPM policy is a list or tree of Policy Assertions represented as a vector<PABase*> in
    // TSS.C++. The simplest policy tree is a single element. The following policy indicates that
    // actions may only be performed at locality 1.
    PolicyTree p(TpmCpp::PolicyLocality(TPMA_LOCALITY::LOC_ONE, "" ));

    // Get the policy digest
    TPM_HASH policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);

    // Make an object with this policy hash
    TPM_HANDLE hmacKeyHandle = MakeHmacPrimaryWithPolicy(policyDigest, null);

    // Try to use the key using an authValue (not policy) - This should fail
    tpm._ExpectError(TPM_RC::AUTH_UNAVAILABLE)
       .HMAC_Start(hmacKeyHandle, null, TPM_ALG_ID::SHA1);

    // Now use policy and issue at locality 1
    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);

    // Execute the policy using the session. This issues a sequence of TPM operations to
    // "prove" to the TPM that the policy is satisfied. In this very simple case
    // Execute() will call tpm.PolicyLocality(s, TPMA_LOCALITY::LOC_ONE).
    p.Execute(tpm, s);

    // Check that the policy-hash in the session is really what we calculated it to be.
    // If this is not the case then the attempt to use the policy-protected object below will fail.
    ByteVec digest = tpm.PolicyGetDigest(s);
    cout << "Calculated policy digest  : " << policyDigest.digest << endl;
    cout << "TPM reported policy digest: " << digest << endl;

    if (tpm._GetDevice().LocalityCtlAvailable())
    {
        // Execute at locality 1 with the session should succeed
        tpm._GetDevice().SetLocality(1);
        auto hmacSessionHandle = tpm[s].HMAC_Start(hmacKeyHandle, null, TPM_ALG_ID::SHA1);
        tpm._GetDevice().SetLocality(0);
        tpm.FlushContext(hmacSessionHandle);
    }

    // Clean up
    tpm.FlushContext(hmacKeyHandle);
    tpm.FlushContext(s);
} // PolicyLocalitySample()

void Samples::PolicyPCRSample()
{
    Announce("PolicyPCR");

    // In this sample we show the use of PolicyPcr

    // First set a PCR to a value
    TPM_ALG_ID bank = TPM_ALG_ID::SHA1;
    UINT32 pcr = 15;   

    tpm.PCR_Event(TPM_HANDLE::Pcr(pcr), { 1, 2, 3, 4 });

    // Read the current value
    auto pcrSelection = TPMS_PCR_SELECTION::GetSelectionArray(bank, pcr);
    auto startPcrVal = tpm.PCR_Read(pcrSelection);
    auto currentValue = startPcrVal.pcrValues;

    // Create a policy naming this PCR and current PCR value
    PolicyTree p(PolicyPcr(currentValue, pcrSelection));

    // Get the policy digest
    TPM_HASH policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);

    // Make an object with this policy hash
    TPM_HANDLE hmacKeyHandle = MakeHmacPrimaryWithPolicy(policyDigest, null);

    // To prove to the TPM that the policy is satisfied we first create a session
    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);

    // Next we execute the policy using the session. This issues a sequence of TPM operations to
    // "prove" to the TPM that the policy is satisfied. In this very simple case
    // Execute() will call tpm.PolicyPcr(...).
    p.Execute(tpm, s);

    // Check that the policy-hash in the session is really what we calculated it to be.
    // If this is not the case then the attempt to use the policy-protected object below will fail.
    ByteVec digest = tpm.PolicyGetDigest(s);
    cout << "Calculated policy digest  : " << policyDigest.digest << endl;
    cout << "TPM reported policy digest: " << digest << endl;

    // Since we have not changed the PCR this should succeed
    TPM_HANDLE hmacSequenceHandle = tpm[s].HMAC_Start(hmacKeyHandle, null, TPM_ALG_ID::SHA1);
    tpm.FlushContext(s);

    // Next we change the PCR value, so the action should fail
    tpm.PCR_Event(TPM_HANDLE::Pcr(pcr), { 1, 2, 3, 4 });
    s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);

    try {
        p.Execute(tpm, s);
        cerr << "Should NOT get here, because the policy evaluation should fail";
        _ASSERT(FALSE);
    }
    catch (exception) {
        // Expected
    }

    // And the session should not be usable
    tpm[s]._ExpectError(TPM_RC::POLICY_FAIL)
          .HMAC_Start(hmacKeyHandle, null, TPM_ALG_ID::SHA1);

    // Clean up
    tpm.FlushContext(hmacKeyHandle);
    tpm.FlushContext(s);
    tpm.FlushContext(hmacSequenceHandle);
} // PolicyPCRSample()

void Samples::ChildKeys()
{
    Announce("Child Keys");

    // In this sample we demonstrate how a primary storage key can be used to protect a child key

    // To create the primary storage key the TPM must be provided with a template.
    // Storage keys must be protected decryption keys.
    TPMT_PUBLIC primTempl(TPM_ALG_ID::SHA1,          // Key nameAlg
                          TPMA_OBJECT::decrypt | TPMA_OBJECT::restricted | TPMA_OBJECT::userWithAuth
                            | TPMA_OBJECT::fixedParent | TPMA_OBJECT::fixedTPM | TPMA_OBJECT::sensitiveDataOrigin,
                          null,                   // No policy
                          // Key-parms: How child keys should be protected
                          TPMS_RSA_PARMS(Aes128Cfb, TPMS_NULL_ASYM_SCHEME(), 2048, 65537),
                          TPM2B_PUBLIC_KEY_RSA());

    // Set the use-auth for the next key. Note the second parameter is
    // NULL because we are asking the TPM to create a new key.
    ByteVec userAuth = { 1, 2, 3, 4 };
    TPMS_SENSITIVE_CREATE sensCreate(userAuth, null);

    // Create the key (no PCR-state captured)
    auto storagePrimary = tpm.CreatePrimary(TPM_RH::OWNER, sensCreate, primTempl, null, null);

    // Note that if we want to use the storage key handle we need the userAuth, as specified above.
    // TSS.C++ sets this when it can, but this is what you have to do if it has not been auto-set.
    storagePrimary.handle.SetAuth(userAuth);
    TPM_HANDLE& hPrim = storagePrimary.handle;

    // Print out the public data for the new key. Note the parameter to
    // ToString() "pretty-prints" the byte-arrays.
    cout << "New RSA primary storage key" << endl << storagePrimary.outPublic.ToString(false) << endl;

    // Now we have a primary we can ask the TPM to make child keys. As always, we start with
    // a template. Here we specify a 1024-bit signing key to create a primary key the TPM
    // must be provided with a template.  This is for an RSA1024 signing key.
    TPMT_PUBLIC templ(TPM_ALG_ID::SHA1, 
                      TPMA_OBJECT::sign | TPMA_OBJECT::fixedParent | TPMA_OBJECT::fixedTPM
                        | TPMA_OBJECT::sensitiveDataOrigin | TPMA_OBJECT::userWithAuth,
                      null,                                   // No policy
                          // PKCS1.5: How the signing will be performed
                      TPMS_RSA_PARMS(null, TPMS_SCHEME_RSASSA(TPM_ALG_ID::SHA1), 2048, 65537),
                      TPM2B_PUBLIC_KEY_RSA());

    // Ask the TPM to create the key.  For simplicity we will leave the other parameters
    // (apart from the template) the same as for the storage key
    auto newSigKey = tpm.Create(hPrim, sensCreate, templ, null, null);

    // Unlike primary keys, child keys must be "loaded" before they can be used. To load
    // a the parent has to also be loaded, and you must have the parents use-auth.
    TPM_HANDLE signKey = tpm.Load(hPrim, newSigKey.outPrivate, newSigKey.outPublic);
    
    // Set the auth so we can use it
    signKey.SetAuth(userAuth);

    // And once it is loaded you can use it. In this case we ask it to sign some data
    ByteVec data = {1, 2, 3};
    TPM_HASH dataToSign = TPM_HASH::FromHashOfData(TPM_ALG_ID::SHA1, data);

    auto sig = tpm.Sign(signKey, dataToSign, TPMS_NULL_SIG_SCHEME(), null);

    cout << "Data to be signed:" << dataToSign.digest << endl;
    cout << "Signature:" << endl << sig->ToString(false) << endl;

    // Non-primary TPM objects can be context-saved to make space for other keys to be loaded.
    TPMS_CONTEXT keyContext = tpm.ContextSave(signKey);
    tpm.FlushContext(signKey);

    // The key is no longer there...
    tpm._DemandError().ReadPublic(signKey);

    // But we can reload it
    TPM_HANDLE reloadedKey = tpm.ContextLoad(keyContext);
    reloadedKey.SetAuth(userAuth);

    // And now we can read it again
    auto pubInfo = tpm.ReadPublic(reloadedKey);

    // We can also "change" the authValue for a loaded object
    ByteVec newAuth { 2, 7, 1, 8, 2, 8 };
    auto newPrivate = tpm.ObjectChangeAuth(reloadedKey, hPrim, newAuth);

    // Check we can use it with the new Auth
    TPM_HANDLE changedAuthHandle = tpm.Load(hPrim, newPrivate, newSigKey.outPublic);
    changedAuthHandle.SetAuth(newAuth);
    auto sigx = tpm.Sign(changedAuthHandle, dataToSign, TPMS_NULL_SIG_SCHEME(), null);

    tpm.FlushContext(changedAuthHandle);

    // Clean up a bit
    tpm.FlushContext(hPrim);
    tpm.FlushContext(reloadedKey);

    // Use the TSS.C++ library to validate the signature
    newSigKey.outPublic.ValidateSignature(dataToSign, *sig);

    // The TPM can also validate signatures. 
    // To validate a signature, only the public part of a key need be loaded.

    // LoadExternal can also load a pub/priv key pair.
    TPM_HANDLE publicKeyHandle = tpm.LoadExternal(null, newSigKey.outPublic, TPM_RH_NULL);

    // Now use the loaded public key to validate the previously created signature
    auto sigVerify = tpm._AllowErrors().VerifySignature(publicKeyHandle, dataToSign, *sig);
    if (tpm._LastCommandSucceeded())
        cout << "Signature verification succeeded" << endl;

    // Mess up the signature by flipping a bit
    TPMS_SIGNATURE_RSASSA *rsaSig = dynamic_cast<TPMS_SIGNATURE_RSASSA*>(&*sig);
    rsaSig->sig[0] ^= 1;

    // This should fail
    sigVerify = tpm._AllowErrors().VerifySignature(publicKeyHandle, dataToSign, *sig);

    if (!tpm._LastCommandSucceeded())
        cout << "Signature verification of bad signature failed, as expected" << endl;

    _ASSERT(!tpm._LastCommandSucceeded());

    // And sofware verification should fail too
    _ASSERT(!newSigKey.outPublic.ValidateSignature(dataToSign, *sig));

    // Remove the primary key from the TPM
    tpm.FlushContext(publicKeyHandle);
} // ChildKeys()

void Samples::PolicyORSample()
{
    Announce("PolicyOR");

    // In this sample we show the use of PolicyOr. We make two policy-branches: one that needs
    // a specific PCR-value, and one that needs physical presence.

    // First set a PCR to a value
    TPM_ALG_ID bank = TPM_ALG_ID::SHA1;
    UINT32 pcr = 15;
    tpm.PCR_Event(TPM_HANDLE::Pcr(pcr), { 1, 2, 3, 4 });

    // Read the current value
    auto pcrSelection = TPMS_PCR_SELECTION::GetSelectionArray(bank, pcr);
    auto startPcrVal = tpm.PCR_Read(pcrSelection);
    auto currentValue = startPcrVal.pcrValues;

    // Create a policy naming the PCR and policy-locality in an OR current PCR value
    PolicyTree branch1(PolicyPcr(currentValue, pcrSelection, "pcr-branch"));
    PolicyTree branch2(PolicyPhysicalPresence("pp-branch"));

    PolicyTree p(PolicyOr(branch1.GetTree(), branch2.GetTree()));

    // Get the policy digest
    TPM_HASH policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);

    // Make an object with this policy hash
    TPM_HANDLE hmacKeyHandle = MakeHmacPrimaryWithPolicy(policyDigest, null);

    // Use the PCR-policy branch to authorize use of the key
    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA);
    p.Execute(tpm, s, "pcr-branch");
    tpm[s].HMAC(hmacKeyHandle, {1, 2, 3, 4}, TPM_ALG_ID::SHA1);
    tpm.FlushContext(s);

    // Now change the PCR so this no longer works
    tpm.PCR_Event(TPM_HANDLE::Pcr(pcr), { 1, 2, 3, 4 });
    s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA);

    try {
        p.Execute(tpm, s, "pcr-branch");

        // We should not hit this _ASSERT because the PCR-value is wrong
        _ASSERT(FALSE);
    }
    catch (exception) {
        cerr << "PolicyPcr failed, as expected" << endl;
    }

    tpm[s]._ExpectError(TPM_RC::POLICY_FAIL)
        .HMAC(hmacKeyHandle, {1, 2, 3, 4}, TPM_ALG_ID::SHA1);

    // But we can still use the physical-presence branch, as long as we can assert PP.
    if (tpm._GetDevice().ImplementsPhysicalPresence())
    {
        tpm.PolicyRestart(s);
        p.Execute(tpm, s, "pp-branch");
        tpm._GetDevice().AssertPhysicalPresence(true);
        tpm[s].HMAC(hmacKeyHandle, {1, 2, 3, 4}, TPM_ALG_ID::SHA1);
        tpm._GetDevice().AssertPhysicalPresence(false);
    }
    tpm.FlushContext(s);

    // And clean up
    tpm.FlushContext(hmacKeyHandle);
} // PolicyORSample()

void Samples::CounterTimer()
{
    Announce("CounterTimer");

    int runTime = 5;
    cout << "TPM-time (reading for ~" << runTime << " seconds)" << endl;
    //TPMS_TIME_INFO startTimeX = tpm.ReadClock();
    int systemTime = GetSystemTime(true),
        endTime = systemTime + runTime;

    while (systemTime < endTime)
    {
        TPMS_TIME_INFO time = tpm.ReadClock();
        cout << "(Sytem Time(s), TpmTime(ms)) = (" << dec << systemTime << ", " << time.time << ")" << endl;
        Sleep(1000);
        systemTime = GetSystemTime();
    }
} // CounterTimer()

static time_t startTimer = 0;

int Samples::GetSystemTime(bool reset)
{
    if (reset)
        startTimer = time(NULL);

    time_t timer = time(NULL);
    return (int)difftime(timer, startTimer);
}

void Samples::Sleep(int numMillisecs)
{
#ifdef WIN32
    ::Sleep(numMillisecs);
#elif defined(__linux__)
    usleep(numMillisecs * 1000);
#endif
}

void Samples::Attestation()
{
    Announce("Attestation");

    // Attestation is the TPM signing internal data structures. The TPM can perform
    // several-types of attestation: we demonstrate signing PCR, keys, and time.

    // To get attestation information we need a restricted signing key and privacy authorization.
    TPM_HANDLE primaryKey = MakeStoragePrimary();
    TPM_HANDLE sigKey = MakeChildSigningKey(primaryKey, true);

    // First PCR-signing (quoting). We will sign PCR-7.
    cout << ">> PCR Quoting" << endl;
    auto pcrsToQuote = TPMS_PCR_SELECTION::GetSelectionArray(TPM_ALG_ID::SHA1, 7);

    // Do an event to make sure the value is non-zero
    tpm.PCR_Event(TPM_HANDLE::Pcr(7), { 1, 2, 3 });

    // Then read the value so that we can validate the signature later
    auto pcrVals = tpm.PCR_Read(pcrsToQuote);

    // Do the quote.  Note that we provide a nonce.
    ByteVec Nonce = Crypto::GetRand(16);
    auto quote = tpm.Quote(sigKey, Nonce, TPMS_NULL_SIG_SCHEME(), pcrsToQuote);

    // Need to cast to the proper attestion type to validate
    TPMS_ATTEST qAttest = quote.quoted;
    TPMS_QUOTE_INFO *qInfo = dynamic_cast<TPMS_QUOTE_INFO*>(&*qAttest.attested);
    cout << "Quoted PCR: " << qInfo->pcrSelect[0].ToString() << endl;
    cout << "PCR-value digest: " << qInfo->pcrDigest << endl;

    // We can use the TSS.C++ library to verify the quote. First read the public key.
    // Nomrmally the verifier will have other ways of determinig the veractity
    // of the public key
    auto pubKey = tpm.ReadPublic(sigKey);
    bool sigOk = pubKey.outPublic.ValidateQuote(pcrVals, Nonce, quote);
    if (sigOk)
        cout << "The quote was verified correctly" << endl;
    _ASSERT(sigOk);

    // Now change the PCR and do a new quote
    tpm.PCR_Event(TPM_HANDLE::Pcr(7), { 1, 2, 3 });
    quote = tpm.Quote(sigKey, Nonce, TPMS_NULL_SIG_SCHEME(), pcrsToQuote);

    // And check against the values we read earlier
    sigOk = pubKey.outPublic.ValidateQuote(pcrVals, Nonce, quote);
    if (!sigOk)
        cout << "The changed quote did not match, as expected" << endl;
    _ASSERT(!sigOk);

    // Get a time-attestation
    cout << ">> Time Quoting" << endl;
    ByteVec timeNonce = { 0xa, 0x9, 0x8, 0x7 };
    auto timeQuote = tpm.GetTime(TPM_RH::ENDORSEMENT, sigKey, timeNonce, TPMS_NULL_SIG_SCHEME());

    // The TPM returns the siganture block that it signed: interpret it as an 
    // attestation structure then cast down into the nested members...
    TPMS_ATTEST& tm = timeQuote.timeInfo;
    auto tmx = dynamic_cast <TPMS_TIME_ATTEST_INFO*>(&*tm.attested);
    TPMS_CLOCK_INFO cInfo = tmx->time.clockInfo;

    cout << "Attested Time" << endl;
    cout << "   Firmware Version:" << tmx->firmwareVersion << endl <<
            "   Time:" << tmx->time.time << endl <<
            "   Clock:" << cInfo.clock << endl <<
            "   ResetCount:" << cInfo.resetCount << endl <<
            "   RestartCount:" << cInfo.restartCount << endl;

    sigOk = pubKey.outPublic.ValidateGetTime(timeNonce, timeQuote);
    if (sigOk)
        cout << "Time-quote validated" << endl;
    _ASSERT(sigOk);

    // Get a key attestation.  For simplicity we have the signingKey self-certify b
    cout << ">> Key Quoting" << endl;
    ByteVec nonce { 5, 6, 7 };
    auto keyInfo = tpm.Certify(sigKey, sigKey, nonce, TPMS_NULL_SIG_SCHEME());

    // The TPM returns the siganture block that it signed: interpret it as an
    // attestation structure then cast down into the nested members...
    TPMS_ATTEST& ky = keyInfo.certifyInfo;

    auto kyx = dynamic_cast <TPMS_CERTIFY_INFO*>(&*ky.attested);
    cout << "Name of certified key:" << endl << "  " << kyx->name << endl;
    cout << "Qualified name of certified key:" << endl << "  " << kyx->qualifiedName << endl;

    // Validate then cerify against the known name of the key
    sigOk = pubKey.outPublic.ValidateCertify(pubKey.outPublic, nonce, keyInfo);
    if (sigOk)
        cout << "Key certification validated" << endl;
    _ASSERT(sigOk);

    // CertifyCreation provides a "birth certificate" for a newly createed object
    TPMT_PUBLIC templ(TPM_ALG_ID::SHA1,
                      TPMA_OBJECT::sign | TPMA_OBJECT::fixedParent | TPMA_OBJECT::fixedTPM
                        | TPMA_OBJECT::sensitiveDataOrigin | TPMA_OBJECT::userWithAuth,
                      null,  // No policy
                      TPMS_RSA_PARMS(null, TPMS_SCHEME_RSASSA(TPM_ALG_ID::SHA1), 2048, 65537),
                      TPM2B_PUBLIC_KEY_RSA());

    // Ask the TPM to create the key. For simplicity we will leave the other parameters
    // (apart from the template) the same as for the storage key.
    auto newSigKey = tpm.Create(primaryKey, null, templ, null, null);

    TPM_HANDLE toCertify = tpm.Load(primaryKey, newSigKey.outPrivate, newSigKey.outPublic);

    auto createQuote = tpm.CertifyCreation(sigKey, toCertify, nonce, newSigKey.creationHash,
                                           TPMS_NULL_SIG_SCHEME(), newSigKey.creationTicket);
    tpm.FlushContext(toCertify);
    tpm.FlushContext(primaryKey);

    sigOk = pubKey.outPublic.ValidateCertifyCreation(nonce, newSigKey.creationHash, createQuote);
    if (sigOk)
        cout << "Key creation certification validated" << endl;
    _ASSERT(sigOk);

    // NV-index quoting.
    
    // First make an NV-slot and put some data in it.
    ByteVec nvAuth { 1, 5, 1, 1 };
    TPM_HANDLE nvHandle = RandomNvHandle();

    // Try to delete the slot if it exists
    tpm._AllowErrors().NV_UndefineSpace(TPM_RH::OWNER, nvHandle);

    // CASE 1 - Simple NV-slot: Make a new simple NV slot, 16 bytes, RW with auth
    TPMS_NV_PUBLIC nvTemplate(nvHandle,           // Index handle
                              TPM_ALG_ID::SHA256, // Name-alg
                              TPMA_NV::AUTHREAD | // Attributes
                              TPMA_NV::AUTHWRITE,
                              null,            // Policy
                              16);                // Size in bytes

    tpm.NV_DefineSpace(TPM_RH::OWNER, nvAuth, nvTemplate);

    // We have set the authVal to be nvAuth, so set it in the handle too.
    nvHandle.SetAuth(nvAuth);

    // Write some data
    ByteVec toWrite { 1, 2, 3, 4, 5, 4, 3, 2, 1 };
    tpm.NV_Write(nvHandle, nvHandle, toWrite, 0);

    auto nvQuote = tpm.NV_Certify(sigKey, nvHandle, nvHandle, nonce,
                                  TPMS_NULL_SIG_SCHEME(), (UINT16)toWrite.size(), 0);

    sigOk = pubKey.outPublic.ValidateCertifyNV(nonce, toWrite, 0, nvQuote);
    if (sigOk)
        cout << "Key creation certification validated" << endl;
    _ASSERT(sigOk);

    tpm.NV_UndefineSpace(TPM_RH::OWNER, nvHandle);
    tpm.FlushContext(sigKey);
} // Attestation()

void Samples::Admin()
{
    Announce("Administration");

    // This sample demonstrates some TPM administration functions.

    // Clearing the TPM.

    if (tpm._GetDevice().PlatformAvailable())
    {
        // "Clearing" the TPM changes the Storage Primary Seed (among other actions).
        // Since primary keys are deterministically generated from the seed, the primary
        // keys in the storage hierarchy will change.

        // Create two primary keys from the same template ...
        TPM_HANDLE h1 = MakeStoragePrimary();
        TPM_HANDLE h2 = MakeStoragePrimary();

        // ... and make sure that they are the same
        auto pub1 = tpm.ReadPublic(h1);
        auto pub2 = tpm.ReadPublic(h2);
        _ASSERT(pub1.name == pub2.name);

        // Clear the TPM
        tpm.Clear(TPM_RH::LOCKOUT);

        TPM_HANDLE h3 = MakeStoragePrimary();
        auto pub3 = tpm.ReadPublic(h3);

        cout << "Name before clear " << pub1.name << endl;
        cout << "Name after clear  " << pub3.name << endl;
        _ASSERT(pub1.name != pub3.name);

        tpm.FlushContext(h3);

        // We can do the same thing with the endorsement and platform hierarchies
        tpm.ChangePPS(TPM_RH::PLATFORM);
        tpm.ChangeEPS(TPM_RH::PLATFORM);
    }

    // We can change the authValue for the hierarchies.

    ByteVec newOwnerAuth = TPM_HASH::FromHashOfString(TPM_ALG_ID::SHA1, "passw0rd");
    tpm.HierarchyChangeAuth(TPM_RH::OWNER, newOwnerAuth);

    // TSS.C++ tracks changes of auth-values and updates the relevant handle.
    _ASSERT(tpm._AdminOwner.GetAuth() == newOwnerAuth);

    // Because we have the new auth-value we can continue managing the TPM
    tpm.HierarchyChangeAuth(TPM_RH::OWNER, null);

    // And set the value in the handle so that other tests will work
    //tpm._AdminOwner.SetAuth(null);


    if (tpm._GetDevice().PlatformAvailable())
    {
        // HierarchyControl enables and disables access to a hierarchy
        // First disable the storage hierarchy. This will flush any objects in this hierarchy.
        TPM_HANDLE ha = MakeStoragePrimary();
        tpm.HierarchyControl(TPM_RH::OWNER, TPM_RH::OWNER, 0);
        tpm._ExpectError(TPM_RC::REFERENCE_H0)
           .ReadPublic(ha);

        // Reenable it again
        tpm.HierarchyControl(TPM_RH::PLATFORM, TPM_RH::OWNER, 1);

        // Re-enabling the hierarchy does not resurrect its flushed objects
        tpm._ExpectError(TPM_RC::REFERENCE_H0)
           .ReadPublic(ha);
    }

    if (tpm._GetDevice().LocalityCtlAvailable())
    {
        // Hierarchies can be controlled by policy. Here we say any entity at locality 1 can
        // perform admin actions on the TPM.
        PolicyTree p(::PolicyLocality(TPMA_LOCALITY::LOC_ONE, ""));
        TPM_HASH policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);
        tpm.SetPrimaryPolicy(TPM_RH::OWNER, policyDigest, TPM_ALG_ID::SHA1);

        tpm._GetDevice().SetLocality(1);
        AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
        p.Execute(tpm, s);

        // Set the policy back to empty
        tpm.SetPrimaryPolicy(TPM_RH::OWNER, null, TPM_ALG_NULL);

        // Restore the original locality
        tpm._GetDevice().SetLocality(0);

        // Cleanup
        tpm.FlushContext(s);
    }
} // Admin()

void Samples::DictionaryAttack()
{
    Announce("Dictionary Attack");

    // The TPM maintains global dictionary attack remediation logic. A special
    // authValue is needed to control it. This is LockoutAuth.

    // Reset the lockout
    tpm.DictionaryAttackLockReset(TPM_RH::LOCKOUT);

    // And set the TPM to be fairly forgiving for running the tests
    UINT32 newMaxTries = 1000, newRecoverTime = 1, lockoutAuthFailRecoveryTime = 1;
    tpm.DictionaryAttackParameters(TPM_RH::LOCKOUT, newMaxTries, newRecoverTime,
                                   lockoutAuthFailRecoveryTime);
} // DictionaryAttack()

void Samples::PolicyCpHashSample()
{
    Announce("PolicyCpHashSample");

    // PolicyCpHash restricts the actions that can be performed on a secured object to
    // just a specific operation identified by the hash of the command paramters.
    // THis sample demonstrates how TSS.c++ can be used to obtain and use CpHashes.
    // We demonstrate a policy that (effectively) lets anyone do a TPM Clear() operation,
    // but no other admin tasks.

    // The Tpm2 method _CpHash() initiates all normal command processing, but rather
    // than dispatching the command to the TPM, the command-parameter hash is returned.
    TPM_HASH cpHash(TPM_ALG_ID::SHA1);

    // The policy will allow resetting the Endorsement hierarchy auth value to empty buffer
    tpm._GetCpHash(&cpHash)
       .HierarchyChangeAuth(TPM_RH::ENDORSEMENT, null);

    // We can now make a policy that authorizes this CpHash
    PolicyTree policyTree = PolicyCpHash(cpHash);

    // Get the policy digest
    TPM_HASH policyDigest = policyTree.GetPolicyDigest(TPM_ALG_ID::SHA1);

    // Set the endosement hierarchy new policy
    // (TSS uses its current auth value under cover to authorize this operation)
    tpm.SetPrimaryPolicy(TPM_RH::ENDORSEMENT, policyDigest, TPM_ALG_ID::SHA1);

    AUTH_SESSION sess = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    policyTree.Execute(tpm, sess);

    auto newAuth = Helpers::RandomBytes(20);

    // Cannot use this policy for a different hierarchy
    tpm[sess]._DemandError()
       .HierarchyChangeAuth(TPM_RH::OWNER, null);

    // And cannot use this policy to set a non-empty auth value even for the correct hierarchy...
    tpm[sess]._DemandError()
       .HierarchyChangeAuth(TPM_RH::ENDORSEMENT, newAuth);

    // ... but can use the current auth value for this purpose
    // (TSS uses it behind the scenes in an auto-generated non-policy session)
    tpm.HierarchyChangeAuth(TPM_RH::ENDORSEMENT, newAuth);

    // Now that we are resetting the auth value back to empty our cpHash policy must work
    tpm[sess].HierarchyChangeAuth(TPM_RH::ENDORSEMENT, null);

    // Reset the endorsement hierarchy policy
    tpm.SetPrimaryPolicy(TPM_RH::ENDORSEMENT, null, TPM_ALG_NULL);

    if (tpm._GetDevice().PlatformAvailable())
    {
        // One more usage of the same policy session with a different allowed cpHash 
        tpm.PolicyRestart(sess);

        tpm._GetCpHash(&cpHash)
           .Clear(TPM_RH::PLATFORM);
        ((PolicyCpHash*)policyTree.GetTree()[0])->CpHash = cpHash;

        policyTree.Execute(tpm, sess);

        // Set the platform policy to this value
        policyDigest = policyTree.GetPolicyDigest(TPM_ALG_ID::SHA1);
        tpm.SetPrimaryPolicy(TPM_RH::PLATFORM, policyDigest, TPM_ALG_ID::SHA1);

        tpm[sess].Clear(TPM_RH::PLATFORM);
        cout << "Command Clear() authorized by policy session with PolicyCpHash assertion" << endl;

        // Put things back the way they were
        tpm.SetPrimaryPolicy(TPM_RH::PLATFORM, null, TPM_ALG_NULL);
    }

    // And clean up
    tpm.FlushContext(sess);
} // PolicyCpHash()

void Samples::PolicyCounterTimerSample()
{
    Announce("PolicyCounterTimerSample");

    // PolicyCounterTimer allows actions to be gated on the TPMs clocks and timers.
    // Here we will demontrate giving a user owner-privileges for ~7 seconds

    TPMS_TIME_INFO startClock = tpm.ReadClock();
    UINT64 nowTime = startClock.time;
    UINT64 endTime = nowTime + 5 * 1000;

    // we can now make a policy that authorizes this CpHash
    PolicyTree p(::PolicyCounterTimer(endTime, 0, TPM_EO::UNSIGNED_LT));

    // Get the policy digest
    TPM_HASH policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);
    tpm.SetPrimaryPolicy(TPM_RH::OWNER, policyDigest, TPM_ALG_ID::SHA1);

    // We can now set the owner-admin policy to this value
    cout << "The TPM operations should start failing in about 5 seconds..." << endl;
    int startTime = GetSystemTime(true);

    while (true)
    {
        int nowTime = GetSystemTime();
        int nowDiff = nowTime - startTime;

        if (nowDiff > 8)
            break;

        AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
        try {
            // PolicyCounterTimer will start to fail after 10 seconds
            p.Execute(tpm, s);
        }
        catch (exception) {
            // Expected
        }

        tpm[s]._AllowErrors()
           .SetPrimaryPolicy(TPM_RH::OWNER, policyDigest, TPM_ALG_ID::SHA1);
        if (tpm._LastCommandSucceeded())
            cout << "Succeeded at " << dec << nowDiff << endl;
        else
            cout << "Failed at " << dec << nowDiff << endl;

        tpm.FlushContext(s);
        Sleep(1000);
    }

    // Put things back the way they were
    tpm.SetPrimaryPolicy(TPM_RH::OWNER, null, TPM_ALG_NULL);
} // PolicyTimer()

void Samples::PolicyWithPasswords()
{
    Announce("PolicyWithPasswords");

    // By default authorization uses *either* an auth-value or a policy. The
    // commands PolicyPassword and PolicyAuthValue indicate that both must be used,
    // as demonstrated here.

    // First PolicyPassword (plain-text)
    PolicyPassword pp;
    PolicyTree p(pp);
    TPM_HASH policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);
    ByteVec useAuth = TPM_HASH::FromHashOfString(TPM_ALG_ID::SHA1, "password");
    auto hmacHandle = MakeHmacPrimaryWithPolicy(policyDigest, useAuth);

    // First show it works if you know the password
    hmacHandle.SetAuth(useAuth);
    AUTH_SESSION sess = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    p.Execute(tpm, sess);
    auto hmacSig = tpm[sess].HMAC(hmacHandle, { 1, 2, 3, 4 }, TPM_ALG_ID::SHA1);
    tpm.FlushContext(sess);

    // Now show it fails if you do not know the password
    hmacHandle.SetAuth(null);
    sess = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    p.Execute(tpm, sess);
    hmacSig = tpm[sess]._ExpectError(TPM_RC::AUTH_FAIL)
                 .HMAC(hmacHandle, { 1, 2, 3, 4 }, TPM_ALG_ID::SHA1);

    // Do some cleanup
    tpm.FlushContext(sess);
    tpm.FlushContext(hmacHandle);

    // Now do the same thing with HMAC proof-of-possession
    PolicyTree p2(PolicyAuthValue(""));
    policyDigest = p2.GetPolicyDigest(TPM_ALG_ID::SHA1);
    useAuth = TPM_HASH::FromHashOfString(TPM_ALG_ID::SHA1, "password");
    hmacHandle = MakeHmacPrimaryWithPolicy(policyDigest, useAuth);

    // First show it works if you know the password
    hmacHandle.SetAuth(useAuth);
    sess = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    p2.Execute(tpm, sess);
    hmacSig = tpm[sess].HMAC(hmacHandle, { 1, 2, 3, 4 }, TPM_ALG_ID::SHA1);
    tpm.FlushContext(sess);

    // Now show it fails if you do not know the password
    hmacHandle.SetAuth(null);
    sess = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    p.Execute(tpm, sess);
    hmacSig = tpm[sess]._ExpectError(TPM_RC::AUTH_FAIL)
                 .HMAC(hmacHandle, { 1, 2, 3, 4 }, TPM_ALG_ID::SHA1);

    // And cleanup
    tpm.FlushContext(sess);
    tpm.FlushContext(hmacHandle);
} // PolicyWithPasswords()

void Samples::Unseal()
{
    Announce("Unseal");

    tpm.DictionaryAttackLockReset(tpm._AdminLockout);

    // Unsealing is reading the private data of an object when an authorization policy is
    // satisfied. The policy can be a simple password (the benefit being that the TPM
    // anti-hammering mechanisms can protect a strong password with a weaker one), or
    // any other policy. Here we demonstrate the classic Unseal() requiring PCR and a
    // auth-value.

    UINT32 pcr = 15;
    TPM_ALG_ID bank = TPM_ALG_ID::SHA1;

    // Set the PCR to something
    tpm.PCR_Event(TPM_HANDLE::Pcr(pcr), { 1, 2, 3, 4 });

    // Read the current value
    auto pcrSelection = TPMS_PCR_SELECTION::GetSelectionArray(bank, pcr);
    auto startPcrVal = tpm.PCR_Read(pcrSelection);
    auto currentValue = startPcrVal.pcrValues;

    // Create a policy naming this PCR and current PCR value
    PolicyTree p(PolicyPcr(currentValue, pcrSelection), PolicyPassword());

    // Get the policy digest
    TPM_HASH policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);

    // Now create an object with this policy to read

    // We will demonstrate sealed data that is the child of a storage key
    TPM_HANDLE storagePrimary = MakeStoragePrimary();

    // Template for new data blob.
    TPMT_PUBLIC templ(TPM_ALG_ID::SHA1,
                      TPMA_OBJECT::fixedParent | TPMA_OBJECT::fixedTPM ,
                      policyDigest,
                      TPMS_KEYEDHASH_PARMS(TPMS_NULL_SCHEME_KEYEDHASH()),
                      TPM2B_DIGEST_KEYEDHASH());

    ByteVec dataToSeal { 1, 2, 3, 4, 5, 0xf, 0xe, 0xd, 0xa, 9, 8 };
    ByteVec authValue { 9, 8, 7, 6, 5 };
    TPMS_SENSITIVE_CREATE sensCreate(authValue, dataToSeal);

    // Ask the TPM to create the key. We don't care about the PCR at creation.
    auto sealedObject = tpm.Create(storagePrimary, sensCreate, templ, null, null);

    TPM_HANDLE sealedKey = tpm.Load(storagePrimary, 
                                    sealedObject.outPrivate, sealedObject.outPublic);
    sealedKey.SetAuth(authValue);

    // Start an auth-session
    AUTH_SESSION sess = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    p.Execute(tpm, sess);

    // And try to read the value
    ByteVec unsealedData = tpm[sess].Unseal(sealedKey);
    tpm.FlushContext(sess);
    cout << "Unsealed data: " << unsealedData << endl;
    _ASSERT(unsealedData == dataToSeal);

    // Now show we can't read it without the auth-value
    sealedKey.SetAuth(null);
    sess = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    p.Execute(tpm, sess);

    // And try to read the value
    unsealedData = tpm[sess]._ExpectError(TPM_RC::AUTH_FAIL)
                      .Unseal(sealedKey);
    tpm.FlushContext(sess);

    // Finally show we can't read it if the PCR-value is wrong
    sealedKey.SetAuth(authValue);
    tpm.PCR_Event(TPM_HANDLE::Pcr(pcr), { 1, 2, 3, 4 });
    sess = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);

    try {
        p.Execute(tpm, sess);

        // An _ASSERT we shouldn't hit, see catch().
        _ASSERT(FALSE);
    }
    catch (exception) {
        // Error is expected because the PCR values are wrong
    }

    // And try to read the value
    unsealedData = tpm[sess]._ExpectError(TPM_RC::POLICY_FAIL)
                      .Unseal(sealedKey);
    tpm.FlushContext(sess);

    tpm.FlushContext(storagePrimary);
    tpm.FlushContext(sealedKey);
} // Unseal()

void Samples::Serializer()
{
    Announce("Serializer");

    // TSS.C++ provides support for all TPM-defined data structures to be serialized and
    // deserialized to binary or to a string form. Binary serialization is via the
    // ToBuf and FromBuf methods. These methods convert to and from the TPM canonical
    // structure representation.

    // String serialization is to JSON (XML coming soon...). The resulting strings can
    // be stored in files or across the network. Here we demonstrate serialization and
    // deserialization of some TPM structures.

    // Start by using the TPM to initialize some data structures, 
    // specifically, PCR-values and a key pair.
    vector<TPMS_PCR_SELECTION> toReadArray = { {TPM_ALG_ID::SHA1, 0}, {TPM_ALG_ID::SHA256, 1} };

    // Used by PCR_Read to read PCR-0 in the SHA1 bank
    vector<TPMS_PCR_SELECTION> toReadPcr0 = { {TPM_ALG_ID::SHA1, 0} };

    ByteVec toEvent { 1, 2, 3 };
    tpm.PCR_Event(TPM_HANDLE::Pcr(0), toEvent);
    tpm.PCR_Event(TPM_HANDLE::Pcr(1), toEvent);

    auto pcrVals = tpm.PCR_Read(toReadArray);

    // Make a storage primary
    TPM_HANDLE primHandle = MakeStoragePrimary();

    // Make a new child signing key
    TPMT_PUBLIC templ(TPM_ALG_ID::SHA1,
                      TPMA_OBJECT::sign | TPMA_OBJECT::fixedParent |  TPMA_OBJECT::fixedTPM
                        | TPMA_OBJECT::sensitiveDataOrigin | TPMA_OBJECT::userWithAuth,
                      null, // No policy
                      TPMS_RSA_PARMS(null, TPMS_SCHEME_RSAPSS(TPM_ALG_ID::SHA1), 1024, 65537),
                      TPM2B_PUBLIC_KEY_RSA());

    auto newSigningKey = tpm.Create(primHandle, null, templ, null, null);
    tpm.FlushContext(primHandle);

    // Now demonstrate binary serialization
    // PubKey objects
    TPMT_PUBLIC& pubKey = newSigningKey.outPublic;
    ByteVec pubKeyBinary = pubKey.toBytes();
    TPMT_PUBLIC reconstitutedPub;
    reconstitutedPub.initFromBytes(pubKeyBinary);

    if (reconstitutedPub.toBytes() == pubKey.toBytes())
        cout << "TPMT_PUBLIC Original and Original->Binary->Reconstituted are the same" << endl;
    _ASSERT(reconstitutedPub.toBytes() == pubKey.toBytes());

    // PCR-values
    ByteVec pcrValsBinary = pcrVals.toBytes();
    PCR_ReadResponse pcrValsRedux;
    pcrValsRedux.initFromBytes(pcrValsBinary);
    cout << "PcrVals:" << endl << pcrVals.ToString(true) << endl;
    cout << "Binary form:" << endl << pcrValsBinary << endl;

    // Check that they're the same:
    if (pcrValsRedux.toBytes() == pcrVals.toBytes())
        cout << "PCR Original and Original->Binary->Reconstituted are the same" << endl;
    _ASSERT(pcrValsRedux.toBytes() == pcrVals.toBytes());

    // Next demonstrate JSON serialization
    // First the PCR-values structure
    string pcrValsJson = pcrVals.Serialize(SerializationType::JSON);
    cout << "JSON-serialized PCR values:" << endl << pcrValsJson << endl;
    pcrValsRedux.Deserialize(SerializationType::JSON, pcrValsJson);

    if (pcrValsRedux == pcrVals)
        cout << "JSON serializer of PCR values OK" << endl;
    _ASSERT(pcrValsRedux == pcrVals);

    // Next a full key (pub + priv)
    string keyJson = newSigningKey.Serialize(SerializationType::JSON);
    cout << "JSON-serialized signing key info: " << keyJson << endl;
    CreateResponse keyRedux;
    keyRedux.Deserialize(SerializationType::JSON, keyJson);

    if (keyRedux == newSigningKey)
        cout << "JSON serializer of TPM key-container is OK" << endl;
    _ASSERT(keyRedux == newSigningKey);

    // Now plain text representation
    string keyText = newSigningKey.Serialize(SerializationType::Text);
    cout << "TEXT-serialized signing key info:" << endl << keyText << endl;

    string pcrValsText = pcrVals.Serialize(SerializationType::Text);
    cout << "TEXT-serialized PCR values:" << endl << pcrValsText << endl;

    pcrValsRedux.Deserialize(SerializationType::Text, pcrValsText);
    if (pcrValsRedux == pcrVals)
        cout << "TEXT serializer of PCR values OK" << endl;
    _ASSERT(pcrValsRedux == pcrVals);

    keyRedux.Deserialize(SerializationType::Text, keyText);
    if (keyRedux == newSigningKey)
        cout << "TEXT serializer of TPM key-container is OK" << endl;
    _ASSERT(keyRedux == newSigningKey);
} // Serializer()

void Samples::SessionEncryption()
{
    Announce("SessionEncryption");

    // Session encryption is essentially transparent to the application programmer.
    // All that is needed is to create a session with the necessary characteristics and
    // TSS.C++ adds all necessary parameter encryption and decryption.

    // At the time of writing only unseeded and unbound session enc and dec are supported.
    // First set up a session that encrypts communications TO the TPM. To do this
    // we tell the TPM to decrypt via TPMA_SESSION::decrypt.
    AUTH_SESSION sess = tpm.StartAuthSession(TPM_SE::HMAC, TPM_ALG_ID::SHA1,
                        TPMA_SESSION::continueSession | TPMA_SESSION::decrypt,
                        TPMT_SYM_DEF(TPM_ALG_ID::AES, 128, TPM_ALG_ID::CFB));

    ByteVec stirValue { 1, 1, 1, 1, 1, 1, 1, 1 };

    // Simplest use of parm encryption - the stirValue buffer will be encrypted
    // Note: because the nonces are transferred in plaintext and because this example
    // does not use a secret auth-value, a MiM could decrypt (but it shows how parm
    // encryption is enabled in TSS.C++.
    tpm[sess].StirRandom(stirValue);

    // A bit more complicated: here we set the ownerAuth using parm-encrytion
    ByteVec newOwnerAuth { 0, 1, 2, 3, 4, 5, 6 };
    tpm[sess].HierarchyChangeAuth(TPM_RH::OWNER, newOwnerAuth);
    //tpm._AdminOwner.SetAuth(newOwnerAuth);

    // But show we can change it back using the encrypting session
    tpm[sess].HierarchyChangeAuth(TPM_RH::OWNER, null);
    //tpm._AdminOwner.SetAuth(null);
    tpm.FlushContext(sess);

    // Now instruct the TPM to encrypt responses.
    // Create a primary key so we have something to read.
    TPM_HANDLE storagePrimary = MakeStoragePrimary();

    // Read some data unencrypted
    auto plaintextRead = tpm.ReadPublic(storagePrimary);

    // Make an encrypting session
    sess = tpm.StartAuthSession(TPM_SE::HMAC, TPM_ALG_ID::SHA1,
                                TPMA_SESSION::continueSession | TPMA_SESSION::encrypt,
                                TPMT_SYM_DEF(TPM_ALG_ID::AES, 128, TPM_ALG_ID::CFB));

    auto encryptedRead = tpm[sess].ReadPublic(storagePrimary);

    if (plaintextRead == encryptedRead)
        cout << "Return parameter encryption succeeded" << endl;
    _ASSERT(plaintextRead == encryptedRead);

    tpm.FlushContext(sess);
    tpm.FlushContext(storagePrimary);
} // SessionEncryption()

void Samples::PresentationSnippets()
{
    // Connect to a local TPM simulator
    TpmTcpDevice device;

    if (!device.Connect("127.0.0.1", 2321)) {
        cerr << "Could not connect to the TPM device";
        return;
    }

    // We talk to the TPM via a Tpm2 object
    Tpm2 tpm;

    // Associate Tpm2 object with a "device"
    tpm._SetDevice(device);

    // If we are talking to the simulator we need to do some of the
    // things that the BIOS normally does for us.
    device.PowerOn();
    tpm.Startup(TPM_SU::CLEAR);

    ByteVec rand = tpm.GetRandom(20);
    cout << "random bytes: " << rand << endl;
}

void Samples::ImportDuplicate()
{
    Announce("ImportDuplicate");

    // Make a storage primary
    auto hPrim = MakeStoragePrimary();

    // We will need the public area for import later
    auto primPub = tpm.ReadPublic(hPrim);

    // Make a duplicatable signing key as a child. Note that duplication
    // *requires* a policy session.
    PolicyTree p(PolicyCommandCode(TPM_CC::Duplicate, ""));
    TPM_HASH policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);

    TPMT_PUBLIC templ(TPM_ALG_ID::SHA1,
                      TPMA_OBJECT::sign | TPMA_OBJECT::userWithAuth | TPMA_OBJECT::adminWithPolicy
                         | TPMA_OBJECT::sensitiveDataOrigin,
                      policyDigest,
                      TPMS_RSA_PARMS(null, TPMS_SCHEME_RSASSA(TPM_ALG_ID::SHA1), 2048, 65537),
                      TPM2B_PUBLIC_KEY_RSA());

    auto newSigKey = tpm.Create(hPrim, null, templ, null, null);
    // Load the key
    TPM_HANDLE signKey = tpm.Load(hPrim, newSigKey.outPrivate, newSigKey.outPublic);

    // Start and then execute the session
    AUTH_SESSION session = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    p.Execute(tpm, session);

    // Keys can be duplicated in plaintext or with a symmetric wrapper, or with a symmetric
    // wrapper and encrypted to a loaded pubic key. The simplest: export (duplicate) it
    // specifying no encryption.
    auto duplicatedKey = tpm[session].Duplicate(signKey, TPM_RH_NULL, null, null);

    cout << "Duplicated private key:" << duplicatedKey.ToString(false);
    
    tpm.FlushContext(session);
    tpm.FlushContext(signKey);

    // Now try to import it (to the same parent)
    auto impPriv = tpm.Import(hPrim, null, newSigKey.outPublic, duplicatedKey.duplicate, null, null);

    // And now show that we can load and and use the imported blob
    TPM_HANDLE importedSigningKey = tpm.Load(hPrim, impPriv, newSigKey.outPublic);

    auto signature = tpm.Sign(importedSigningKey, TPM_HASH::FromHashOfString(TPM_ALG_ID::SHA1, "abc"),
                              TPMS_NULL_SIG_SCHEME(), null);

    cout << "Signature with imported key: " << signature->ToString(false) << endl;

    tpm.FlushContext(importedSigningKey);

    // Now create and import an externally created key. We will demonstrate
    // creation and import of an RSA signing key.
    TPMT_PUBLIC swKeyDef(TPM_ALG_ID::SHA1,
                         TPMA_OBJECT::sign | TPMA_OBJECT::userWithAuth | TPMA_OBJECT::adminWithPolicy
                            | TPMA_OBJECT::sensitiveDataOrigin,
                         policyDigest,
                         TPMS_RSA_PARMS(null, TPMS_SCHEME_RSASSA(TPM_ALG_ID::SHA1), 2048, 65537),
                         TPM2B_PUBLIC_KEY_RSA());

    TSS_KEY swKey;
    swKey.publicPart = swKeyDef;
    swKey.CreateKey();
    ByteVec swKeyAuthValue { 4, 5, 4, 5 };

    // We can use TSS.C++ to create an duplication blob that we can Import()
    TPMT_SENSITIVE sens(swKeyAuthValue, null, TPM2B_PRIVATE_KEY_RSA(swKey.privatePart));
    TPMT_SYM_DEF_OBJECT noInnerWrapper;
    DuplicationBlob dupBlob = primPub.outPublic.GetDuplicationBlob(tpm, swKey, sens, noInnerWrapper);

    auto newPrivate = tpm.Import(hPrim, null, swKey, dupBlob, dupBlob.EncryptedSeed, noInnerWrapper);

    // We can also import it with an inner wrapper
    TPMT_SYM_DEF_OBJECT innerWrapper = Aes128Cfb;
    dupBlob = primPub.outPublic.GetDuplicationBlob(tpm, swKey.publicPart, sens, innerWrapper);
    newPrivate = tpm.Import(hPrim, dupBlob.InnerWrapperKey, swKey, 
                            dupBlob, dupBlob.EncryptedSeed, innerWrapper);

    // Now load and use it.
    TPM_HANDLE importedSwKey = tpm.Load(hPrim,  newPrivate, swKey.publicPart);
    importedSwKey.SetAuth(swKeyAuthValue);
    TPM_HASH dataToSign = TPM_HASH::FromHashOfString(TPM_ALG_ID::SHA1, "abc");
    auto impKeySig = tpm.Sign(importedSwKey, dataToSign, TPMS_NULL_SIG_SCHEME(), null);
    // And verify
    bool swKeySig = swKey.publicPart.ValidateSignature(dataToSign, *impKeySig);
    _ASSERT(swKeySig);

    if (swKeySig)
        cout << "Imported SW-key works" << endl;

    tpm.FlushContext(hPrim);
    tpm.FlushContext(importedSwKey);
} // ImportDuplicate()

void Samples::MiscAdmin()
{
    Announce("MiscAdmin");

    // Demonstrate the use of miscellaneous admin commands

    //
    // Self Testing
    //

    // Note that the simulator does not implement selt-tests, so this sample does
    // nothing more than demonstrate how to use the TPM.
    tpm._AllowErrors().SelfTest(1);
    auto testResult = tpm.GetTestResult();
    auto toBeTested = tpm.IncrementalSelfTest({TPM_ALG_ID::SHA1, TPM_ALG_ID::AES});
    cout << "Algorithms to be tested: " << toBeTested.size() << endl;

    //
    // Clock Management
    //

    TPMS_TIME_INFO startClock = tpm.ReadClock();

    // We should be able to set time forward
    int dt = 10000000;
    UINT64 newClock = startClock.clockInfo.clock + dt;

    tpm.ClockSet(TPM_RH::OWNER, newClock);

    TPMS_TIME_INFO nowClock = tpm.ReadClock();

    int dtIs = (int)(nowClock.clockInfo.clock - startClock.clockInfo.clock);
    cout << setw(1) << dec <<
         "Tried to advance the clock by 10000000" << endl <<
         "actual =               " << dtIs << endl;

    // But not back...
    tpm._ExpectError(TPM_RC::VALUE)
       .ClockSet(TPM_RH::OWNER, startClock.clockInfo.clock);

    // Should be able to speed up and slow down the clock
    tpm.ClockRateAdjust(TPM_RH::OWNER, TPM_CLOCK_ADJUST::MEDIUM_SLOWER);
    tpm.ClockRateAdjust(TPM_RH::OWNER, TPM_CLOCK_ADJUST::MEDIUM_FASTER);

    //
    // Physical Presence
    //
    if (tpm._GetDevice().PlatformAvailable() && tpm._GetDevice().ImplementsPhysicalPresence())
    {
        // Set the commands that need physical presence. Add TPM2_Clear to the PP-list.
        // By default PP_Commands itself needs PP.
        tpm._GetDevice().AssertPhysicalPresence(true);
        tpm.PP_Commands(TPM_RH::PLATFORM, {TPM_CC::Clear}, null);
        tpm._GetDevice().AssertPhysicalPresence(false);

        // Should not be able to execute without PP
        tpm._ExpectError(TPM_RC::PP)
           .Clear(TPM_RH::PLATFORM);

        // But shold be able to execute with PP
        tpm._GetDevice().AssertPhysicalPresence(true);
        tpm.Clear(tpm._AdminLockout);
        cout << "PP-Clear - OK" << endl;
        tpm._GetDevice().AssertPhysicalPresence(false);

        // And now put things back the way they were
        tpm._GetDevice().AssertPhysicalPresence(true);
        tpm.PP_Commands(TPM_RH::PLATFORM, null, {TPM_CC::Clear});
        tpm._GetDevice().AssertPhysicalPresence(false);
        // check it works without PP
        tpm.Clear(tpm._AdminLockout);
    }

    //
    // PCR authorization
    //
    if (tpm._GetDevice().LocalityCtlAvailable())
    {
        // Set an auth value for a PCR. We will use pcr-20, generally extendable at Loc3.
        int pcrNum = 20;
        tpm._GetDevice().SetLocality(3);
        ByteVec newAuth = TPM_HASH::FromHashOfString(TPM_ALG_ID::SHA1, "password");
        tpm.PCR_SetAuthValue(TPM_HANDLE::Pcr(pcrNum), newAuth);

        // This won't work because we have not set the auth in the handle
        tpm._ExpectError(TPM_RC::BAD_AUTH)
           .PCR_Event(TPM_HANDLE::Pcr(pcrNum), { 1, 2, 3 });

        TPM_HANDLE pcrHandle = TPM_HANDLE::Pcr(pcrNum);
        pcrHandle.SetAuth(newAuth);

        // And now it will work
        tpm.PCR_Event(pcrHandle, { 1, 2, 3 });

        // Set things back the way they were
        tpm.PCR_SetAuthValue(pcrHandle, null);

        // Now set a policy for a PCR. Our policy will be that the PCR can only
        // be Evented (extend won't work) at the start show extend works.
        tpm.PCR_Extend(TPM_HANDLE::Pcr(pcrNum), {{TPM_ALG_ID::SHA1}});

        // Create a policy
        PolicyCommandCode XX(TPM_CC::PCR_Event);
        PolicyTree p(PolicyCommandCode(TPM_CC::PCR_Event, ""));
        ByteVec policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);
        tpm.PCR_SetAuthPolicy(TPM_RH::PLATFORM, policyDigest, 
                              TPM_ALG_ID::SHA1, TPM_HANDLE::Pcr(pcrNum));

        // Change the use-auth to a random value so that it can no longer be used
        ByteVec randomAuth = TPM_HASH::FromHashOfString(TPM_ALG_ID::SHA1, "secret");

        // And now show that we can do event with a policy session
        AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
        p.Execute(tpm, s);
        tpm[s].PCR_Event(TPM_HANDLE::Pcr(pcrNum), { 1, 2, 3 });
        tpm.FlushContext(s);

        // And set things back the way they were
        tpm.PCR_SetAuthPolicy(TPM_RH::PLATFORM, null, TPM_ALG_NULL, TPM_HANDLE::Pcr(pcrNum));

        tpm.PCR_SetAuthValue(TPM_HANDLE::Pcr(pcrNum), null);
        tpm._GetDevice().SetLocality(0);
    }

    //
    // PCR-bank allocations
    //
    if (tpm._GetDevice().PowerCtlAvailable())
    {
        // The TPM simulator starts off with SHA256 PCR. Let's delete them.
        // --- REVISIT: The GetCap shows this as not working
        auto resp = tpm.PCR_Allocate(TPM_RH::PLATFORM, {{TPM_ALG_ID::SHA1, vector<UINT32>{0, 1, 2, 3, 4}},
                                                        {TPM_ALG_ID::SHA256, vector<UINT32>{0, 23}} });
        _ASSERT(resp.allocationSuccess);

        // We have to restart the TPM for this to take effect
        tpm.Shutdown(TPM_SU::CLEAR);
        tpm._GetDevice().PowerCycle();
        tpm.Startup(TPM_SU::CLEAR);

        // Now read the PCR
        auto caps = tpm.GetCapability(TPM_CAP::PCRS, 0, 1);
        auto pcrs = dynamic_cast<TPML_PCR_SELECTION*>(&*caps.capabilityData);

        cout << "New PCR-set: " << EnumToStr(pcrs->pcrSelections[0].hash) << "\t";
        auto pcrsWithThisHash = pcrs->pcrSelections[0].ToArray();

        for (auto p = pcrsWithThisHash.begin(); p != pcrsWithThisHash.end(); p++)
            cout << *p << " ";

        cout << endl << "New PCR-set: " << EnumToStr(pcrs->pcrSelections[1].hash) << "\t";
        pcrsWithThisHash = pcrs->pcrSelections[1].ToArray();

        for (auto p = pcrsWithThisHash.begin(); p != pcrsWithThisHash.end(); p++)
            cout << *p << " ";

        cout << endl;

        // And put things back the way they were
        vector<UINT32> standardPcr(24);
        iota(standardPcr.begin(), standardPcr.end(), 0);
        resp = tpm.PCR_Allocate(TPM_RH::PLATFORM, { {TPM_ALG_ID::SHA1, standardPcr},
                                                    {TPM_ALG_ID::SHA256, standardPcr} });
        _ASSERT(resp.allocationSuccess);

        // We have to restart the TPM for this to take effect
        tpm.Shutdown(TPM_SU::CLEAR);
        tpm._GetDevice().PowerCycle();
        tpm.Startup(TPM_SU::CLEAR);
    }

    //
    // ClearControl
    //
    if (tpm._GetDevice().PlatformAvailable())
    {
        // ClearControl disables the use of Clear(). Show we can clear.
        tpm.Clear(TPM_RH::LOCKOUT);

        // Disable clear
        tpm.ClearControl(TPM_RH::LOCKOUT, 1);
        tpm._ExpectError(TPM_RC::DISABLED)
           .Clear(tpm._AdminLockout);
        tpm.ClearControl(TPM_RH::PLATFORM, 0);

        // And now it should work again
        tpm.Clear(TPM_RH::LOCKOUT);
    }
} // MiscAdmin()

void Samples::RsaEncryptDecrypt()
{
    Announce("RsaEncryptDecrypt");

    // This sample demostrates the use of the TPM for RSA operations.
    
    // We will make a key in the "null hierarchy".
    TPMT_PUBLIC primTempl(TPM_ALG_ID::SHA1,
                          TPMA_OBJECT::decrypt | TPMA_OBJECT::userWithAuth | TPMA_OBJECT::sensitiveDataOrigin,
                          null,  // No policy
                          TPMS_RSA_PARMS(null, TPMS_SCHEME_OAEP(TPM_ALG_ID::SHA1), 2048, 65537),
                          TPM2B_PUBLIC_KEY_RSA());

    // Create the key
    auto storagePrimary = tpm.CreatePrimary(TPM_RH_NULL, null, primTempl, null, null);

    TPM_HANDLE& keyHandle = storagePrimary.handle;

    ByteVec dataToEncrypt = TPM_HASH::FromHashOfString(TPM_ALG_ID::SHA1, "secret");
    cout << "Data to encrypt: " << dataToEncrypt << endl;

    auto enc = tpm.RSA_Encrypt(keyHandle, dataToEncrypt, TPMS_NULL_ASYM_SCHEME(), null);
    cout << "RSA-encrypted data: " << enc << endl;

    auto dec = tpm.RSA_Decrypt(keyHandle, enc, TPMS_NULL_ASYM_SCHEME(), null);
    cout << "decrypted data: " << dec << endl;
    if (dec == dataToEncrypt)
        cout << "Decryption worked" << endl;
    _ASSERT(dataToEncrypt == dec);

    // Now encrypt using TSS.C++ library functions
    ByteVec mySecret = Helpers::RandomBytes(20);
    enc = storagePrimary.outPublic.Encrypt(mySecret, null);
    dec = tpm.RSA_Decrypt(keyHandle, enc, TPMS_NULL_ASYM_SCHEME(), null);
    cout << "My           secret: " << mySecret << endl;
    cout << "My decrypted secret: " << dec << endl;
    _ASSERT(mySecret == dec);

    // Now with padding
    ByteVec pad { 1, 2, 3, 4, 5, 6, 0 };
    enc = storagePrimary.outPublic.Encrypt(mySecret, pad);
    dec = tpm.RSA_Decrypt(keyHandle, enc, TPMS_NULL_ASYM_SCHEME(), pad);
    cout << "My           secret: " << mySecret << endl;
    cout << "My decrypted secret: " << dec << endl;
    _ASSERT(mySecret == dec);

    tpm.FlushContext(keyHandle);
} // RsaEncryptDecrypt()

void Samples::Audit()
{
    Announce("Audit");

    // The TPM supports two kinds of audit. Command audit - demonstrated first -
    // instructs the TPM to keep a running hash of the commands and responses for
    // all commands in a list.

    // There are four parts to this: (1) instructing the Tpm2 object to keep an
    // audit so that it can later be checked, (2) instructing the TPM to keep
    // an audit, and (3) quoting the TPMaudit, and (4) checking it.

    TPM_ALG_ID auditAlg = TPM_ALG_ID::SHA1;
    vector<TPM_CC> emptyVec;
    tpm.SetCommandCodeAuditStatus(TPM_RH::OWNER, TPM_ALG_NULL, emptyVec, emptyVec);

    // Start the TPM auditing
    vector<TPM_CC> toAudit { TPM_CC::GetRandom, TPM_CC::StirRandom };
    tpm.SetCommandCodeAuditStatus(TPM_RH::OWNER, auditAlg, emptyVec, emptyVec);
    tpm.SetCommandCodeAuditStatus(TPM_RH::OWNER, TPM_ALG_NULL, toAudit, emptyVec);

    // Read the current audit-register value from the TPM and register this
    // as the "start point" with TSS.C++
    auto auditDigestAtStart = tpm.GetCommandAuditDigest(TPM_RH::ENDORSEMENT, TPM_RH_NULL,
                                                        null, TPMS_NULL_SIG_SCHEME());

    TPMS_ATTEST& atStart = auditDigestAtStart.auditInfo;
    auto atStartInf = dynamic_cast<TPMS_COMMAND_AUDIT_INFO*>(&*atStart.attested);
    tpm._StartAudit(TPM_HASH(auditAlg, atStartInf->auditDigest));

    // Audit some commands

    // TSS.C++ does not automatically maintain the list of commands that are audited.
    // You must use _Audit() to tell TSS.C++ to add the rpHash and cpHash to the accumulator.
    tpm._Audit().GetRandom(20);
    tpm._Audit().StirRandom({ 1, 2, 3, 4 });
    tpm._Audit().GetRandom(10);
    tpm._Audit().StirRandom({ 9, 8, 7, 6 });

    // And stop auditing
    tpm._Audit().SetCommandCodeAuditStatus(TPM_RH::OWNER, TPM_ALG_NULL, emptyVec, emptyVec);

    TPM_HASH expectedAuditHash = tpm._GetAuditHash();
    tpm._EndAudit();

    // We can read the audit.
    auto auditDigest = tpm.GetCommandAuditDigest(TPM_RH::ENDORSEMENT, TPM_RH_NULL,
                                                 null, TPMS_NULL_SIG_SCHEME());

    TPMS_ATTEST& attest = auditDigest.auditInfo;
    auto cmdAuditInfo = dynamic_cast<TPMS_COMMAND_AUDIT_INFO*>(&*attest.attested);

    // Compare this to the value we are maintaining in the TPM context
    cout << "TPM reported command digest:" << cmdAuditInfo->auditDigest << endl;
    cout << "TSS.C++ calculated         :" << expectedAuditHash.digest << endl;
    _ASSERT(expectedAuditHash == cmdAuditInfo->auditDigest);

    // And now we can quote the audit. Make a protected signing key.
    TPM_HANDLE primaryKey = MakeStoragePrimary();
    TPM_HANDLE signingKey = MakeChildSigningKey(primaryKey, true);
    tpm.FlushContext(primaryKey);
    auto pubKey = tpm.ReadPublic(signingKey);
    auto quote = tpm.GetCommandAuditDigest(TPM_RH::ENDORSEMENT, signingKey,
                                           null, TPMS_NULL_SIG_SCHEME());
    bool quoteOk = pubKey.outPublic.ValidateCommandAudit(expectedAuditHash, null, quote);
    cout << "Command audit quote " << (quoteOk ? "OK" : "FAILED") << endl;
    _ASSERT(quoteOk);

    // Session-audit cryptographically tracks commands issued in the context of the session
    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::HMAC,  TPM_ALG_ID::SHA1,
                                          TPMA_SESSION::audit | TPMA_SESSION::continueSession,
                                          TPMT_SYM_DEF());

    tpm._StartAudit(TPM_HASH(TPM_ALG_ID::SHA1));

    tpm[s]._Audit().GetRandom(20);
    tpm[s]._Audit().StirRandom({ 1, 2, 3, 4 });

    TPM_HASH expectedHash = tpm._GetAuditHash();
    tpm._EndAudit();

    auto sessionQuote = tpm.GetSessionAuditDigest(TPM_RH::ENDORSEMENT, signingKey, s,
                                                  null, TPMS_NULL_SIG_SCHEME());

    quoteOk = pubKey.outPublic.ValidateSessionAudit(expectedHash, null, sessionQuote);
    cout << "Session audit quote " << (quoteOk ? "OK" : "FAILED") << endl;
    _ASSERT(quoteOk);

    tpm.FlushContext(s);
    tpm.FlushContext(signingKey);
} // Audit()

void Samples::Activate()
{
    Announce("ActivateCredential");

    // Make a new EK and get the public key
    TPM_HANDLE ekHandle = MakeEndorsementKey();
    auto ekPubX = tpm.ReadPublic(ekHandle);
    TPMT_PUBLIC& ekPub = ekPubX.outPublic;

    // Make another key that we will "activate"
    TPM_HANDLE srk = MakeStoragePrimary();
    TPM_HANDLE keyToActivate = MakeChildSigningKey(srk, true);
    tpm.FlushContext(srk);

    // Make a secret using the TSS.C++ RNG
    auto secret = Helpers::RandomBytes(20);
    auto nameOfKeyToActivate = keyToActivate.GetName();

    // Use TSS.C++ to get an activation blob
    ActivationData cred = ekPub.CreateActivation(secret, nameOfKeyToActivate);

    ByteVec recoveredSecret = tpm.ActivateCredential(keyToActivate, ekHandle, 
                                                     cred.CredentialBlob, cred.Secret);

    cout << "Secret:                         " << secret << endl;
    cout << "Secret recovered from Activate: " << recoveredSecret << endl;

    _ASSERT(secret == recoveredSecret);

    // You can also use the TPM to make the activation credential
    auto tpmActivator = tpm.MakeCredential(ekHandle, secret, nameOfKeyToActivate);

    recoveredSecret = tpm.ActivateCredential(keyToActivate, ekHandle,
                                             tpmActivator.credentialBlob, tpmActivator.secret);

    cout << "TPM-created activation blob: Secret recovered from Activate: " << recoveredSecret << endl;
    
    _ASSERT(secret == recoveredSecret);

    tpm.FlushContext(ekHandle);
    tpm.FlushContext(keyToActivate);
} // Activate()

// This sample illustrates various forms of import of externally created keys, 
// and export of a TPM key to TSS.c++ where it can be used for cryptography.
void Samples::SoftwareKeys()
{
    Announce("SoftwareKeys");

    // Repeat for each hash algorithm implemented by the TPM:
    for (TPM_ALG_ID hashAlg: TpmConfig::HashAlgs)
    {
        // Skip hash algorithms not supported by the TSS SW crypto layer
        if (!Crypto::IsImplemented(hashAlg))
            continue;
        // First make a software key, and show how it can be imported into the TPM and used.
        TPMT_PUBLIC templ(hashAlg,
                          TPMA_OBJECT::sign | TPMA_OBJECT::userWithAuth,
                          null,  // No policy
                          TPMS_RSA_PARMS(null, TPMS_SCHEME_RSASSA(hashAlg), 1024, 65537),
                          TPM2B_PUBLIC_KEY_RSA());

        TSS_KEY k;
        k.publicPart = templ;
        k.CreateKey();

        TPMT_SENSITIVE s(null, null, TPM2B_PRIVATE_KEY_RSA(k.privatePart));
        TPM_HANDLE h2 = tpm.LoadExternal(s, k.publicPart, TPM_RH_NULL);

        ByteVec toSign = TPM_HASH::FromHashOfString(hashAlg, "hello");
        auto sig = tpm.Sign(h2, toSign, TPMS_NULL_SIG_SCHEME(), null);

        bool swValidatedSig = k.publicPart.ValidateSignature(toSign, *sig);

        if (swValidatedSig)
            cout << "External key imported into the TPM works for signing" << endl;

        _ASSERT(swValidatedSig);

        // Next make an exportable key in the TPM and export it to a SW-key

        auto primHandle = MakeStoragePrimary();

        // Make a duplicatable signing key as a child. Note that duplication *requires* a policy session.
        PolicyTree p(PolicyCommandCode(TPM_CC::Duplicate, ""));
        TPM_HASH policyDigest = p.GetPolicyDigest(hashAlg);

        // Change the attributes since we want the TPM to make the sensitve area
        templ.objectAttributes = TPMA_OBJECT::sign | TPMA_OBJECT::userWithAuth | TPMA_OBJECT::sensitiveDataOrigin;
        templ.authPolicy = policyDigest;
        auto keyData = tpm.Create(primHandle, null, templ, null, null);

        TPM_HANDLE h = tpm.Load(primHandle, keyData.outPrivate, keyData.outPublic);

        // Duplicate. Note we need a policy session.
        AUTH_SESSION sess = tpm.StartAuthSession(TPM_SE::POLICY, hashAlg);
        p.Execute(tpm, sess);
        auto dup = tpm[sess].Duplicate(h, TPM_RH_NULL, null, null);
        tpm.FlushContext(sess);

        // Import the key into a TSS_KEY. The privvate key is in a an encoded TPM2B_SENSITIVE.
        TPM2B_SENSITIVE sens;
        sens.initFromBytes(dup.duplicate.buffer);

        // And the sensitive area is an RSA key in this case
        TPM2B_PRIVATE_KEY_RSA *rsaPriv = dynamic_cast<TPM2B_PRIVATE_KEY_RSA*>(&*sens.sensitiveArea.sensitive);

        // Put this in a TSS.C++ defined structure for convenience
        TSS_KEY swKey(keyData.outPublic, rsaPriv->buffer);

        // Now show that we can sign with the exported SW-key and validate the
        // signature with the pubkey in the TPM.
        TPMS_NULL_SIG_SCHEME nullScheme;
        auto swSig2 = swKey.Sign(toSign, nullScheme);
        auto sigResp = tpm.VerifySignature(h, toSign, *swSig2.signature);

        // Sign with the TPM key
        sig = tpm.Sign(h2, toSign, TPMS_NULL_SIG_SCHEME(), null);

        // And validate with the SW-key (this only uses the public key, of course).
        swValidatedSig = k.publicPart.ValidateSignature(toSign, *sig);
        if (swValidatedSig)
            cout << "Key created in the TPM and then exported can sign (as expected)" << endl;
        _ASSERT(swValidatedSig);

        // Now sign with the duplicate key and check that we can validate the
        // sig with the public key still in the TPM.
        auto swSig = k.Sign(toSign, TPMS_NULL_SIG_SCHEME());

        // Check the SW generated sig is validated with the SW verifier
        bool sigOk = k.publicPart.ValidateSignature(toSign, *swSig.signature);
        _ASSERT(sigOk);

        // And finally check that the key still in the TPM can validate the duplicated key sig
        auto sigVerify = tpm.VerifySignature(h2, toSign, *swSig.signature);

        tpm.FlushContext(h);
        tpm.FlushContext(primHandle);
        tpm.FlushContext(h2);
    } // End of loop over hash algs
} // SoftwareKeys()

TSS_KEY *signingKey = NULL;
SignResponse MyPolicySignedCallback(const ByteVec& nonceTpm, UINT32 expiration, const ByteVec& cpHashA,
                                    const ByteVec& policyRef, const string& tag)
{
    // In normal operation, the calling program will check what
    // it is signing before it signs it.  We just sign...
    TpmBuffer toSign;
    toSign.writeByteBuf(nonceTpm);
    toSign.writeInt(expiration);
    toSign.writeByteBuf(cpHashA);
    toSign.writeByteBuf(policyRef);
    auto hashToSign = TPM_HASH::FromHashOfData(TPM_ALG_ID::SHA1, toSign.trim());
    auto sig = signingKey->Sign(hashToSign, TPMS_NULL_SIG_SCHEME());
    return sig;
}

void Samples::PolicySigned()
{
    Announce("PolicySigned");

    // This sample illustrates how TSS.C++ supports PolicySigned. Two versions
    // are presented. In the first, a "software key" is given to TSS.C++ so that
    // the signature can be obtained automatically. In the second case the library
    // "calls back" so that a signature can be obtained. This form might be
    // used to get a signature from a smartcard, for instance.

    // Note we do not actually "use" the policy in these samples: we just read
    // the policy digest and see if it is what we expect.

    // Make a "SW-key"
    TPMT_PUBLIC templ(TPM_ALG_ID::SHA1,
                      TPMA_OBJECT::sign | TPMA_OBJECT::userWithAuth,
                      null,
                      TPMS_RSA_PARMS(null, TPMS_SCHEME_RSASSA(TPM_ALG_ID::SHA1), 1024, 65537),
                      TPM2B_PUBLIC_KEY_RSA());
    TSS_KEY swKey;
    swKey.publicPart = templ;
    swKey.CreateKey();

    ::PolicySigned paSigned(false, null, null, 0, swKey.publicPart);

    // Tell TSS.C++ what the private key is so that it can do the sig
    paSigned.SetKey(swKey);

    PolicyTree p(paSigned);

    // In normal use the policyDigest would be used to set the policyHash of an object
    auto policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);

    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);

    // "Execute" the policy: this will cause the library to do the signature over the nonce, etc.
    p.Execute(tpm, s);

    // Read the policy hash and see if it is OK.
    ByteVec actualDigest = tpm.PolicyGetDigest(s);

    if (policyDigest != actualDigest)
        throw runtime_error("Bad policy digest");
    cout << "PolicySigned policy digest is correct" << endl;

    // We could use the session at this point, but here we just delete it
    tpm.FlushContext(s);

    // For the second sample we "call out" to the calling program
    ::PolicySigned paSigned2(true, null, null, 0, swKey.publicPart);

    PolicyTree p2(paSigned2);

    // In normal use the policyDigest would be used to set the policyHash of an object.
    policyDigest = p2.GetPolicyDigest(TPM_ALG_ID::SHA1);

    // Install the callback
    signingKey = &swKey;
    p2.SetPolicySignedCallback(&MyPolicySignedCallback);

    s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    // "Execute" the policy: this will cause the library to do the signature over the nonce, etc.
    p2.Execute(tpm, s);

    // Read the policy hash and see if it is OK
    actualDigest = tpm.PolicyGetDigest(s);

    if (actualDigest != policyDigest.digest) {
        throw runtime_error("Bad policy digest");
    }

    // We could use the session at this point...

    // But we will demo another function:
    tpm.PolicyRestart(s);
    auto digestIs = tpm.PolicyGetDigest(s);

    if (digestIs != TPM_HASH(TPM_ALG_ID::SHA1))
        throw runtime_error("did not reset");

    tpm.FlushContext(s);
} // PolicySigned()

void Samples::PolicyAuthorizeSample()
{
    Announce("PolicyAuthorize");

    // This sample illustrates how TSS.C++ supports PolicyAuthorize.
    // PolicyAuthorize lets a key holder tranform a policyHash into a new
    // policyHash derived from a public key if the corresponding private key
    // holder authorizes the pre-policy-hash with a signature.

    // Make a software signing key
    TPMT_PUBLIC templ(TPM_ALG_ID::SHA1,
                      TPMA_OBJECT::sign | TPMA_OBJECT::userWithAuth,
                      null,
                      TPMS_RSA_PARMS(null, TPMS_SCHEME_RSASSA(TPM_ALG_ID::SHA1), 1024, 65537),
                      TPM2B_PUBLIC_KEY_RSA());
    TSS_KEY swKey;
    swKey.publicPart = templ;
    swKey.CreateKey();

    // We will authorize the change from the policyDigest given by PolicyLocality(1)
    // to a value derived from the authorizing key above.

    // First get the policyHash we want to authorize
    PolicyLocality l(TPMA_LOCALITY::LOC_ONE);
    PolicyTree t1(l);
    auto preDigest = t1.GetPolicyDigest(TPM_ALG_ID::SHA1);

    // Next sign the policyHash as defined in the spec
    auto aHash = TPM_HASH::FromHashOfData(TPM_ALG_ID::SHA1,
                                         Helpers::Concatenate(preDigest, null));
    auto signature = swKey.Sign(aHash, TPMS_NULL_SIG_SCHEME());

    // Now make the second policy that contains the PolicyLocality AND the PolicyAuthorize
    PolicyTree p2(PolicyAuthorize(preDigest, null, swKey.publicPart, *signature.signature), l);

    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    p2.Execute(tpm, s);

    auto policyDigest = tpm.PolicyGetDigest(s);

    // Is it what we expect? This is the PolicyUpdate function from the spec.
    TpmBuffer buf;
    buf.writeInt(TPM_CC::PolicyAuthorize);
    buf.writeByteBuf(swKey.publicPart.GetName());

    TPM_HASH expectedPolicyDigest(TPM_ALG_ID::SHA1);
    expectedPolicyDigest.Extend(buf.trim());
    expectedPolicyDigest.Extend(null);

    if (expectedPolicyDigest != policyDigest)
        throw runtime_error("Incorrect policyHash");
    cout << "PolicyAuthorize digest is correct" << endl;

    // We could now use the policy session, but for the sample we will just clean up.
    tpm.FlushContext(s);
} // PolicyAuthorizeSample()

void Samples::PolicySecretSample()
{
    Announce("PolicySecretSample");

    // This sample illustrates how TSS.C++ supports PolicySecret and PolicyAuthValue

    // Make a policy that demands that proof-of-knowledge of the admin authVal
    PolicySecret sec(false, null, null, 0, tpm._AdminOwner.GetName());

    // To "execute" the policy, the policy-assertion node needs to know the
    // handle of the entity that will provide the authVal-check.
    sec.SetAuthorizingObjectHandle(TPM_RH::OWNER);

    PolicyTree p(sec);
    auto policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);
    cout << "PolicySecret session hash = " << policyDigest.digest << endl;

    // Make an object with this policy
    TPM_HANDLE h = MakeHmacPrimaryWithPolicy(policyDigest, null);

    // Now run the policy: this will use PWAP to prove knowledge of the admin-password
    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY , TPM_ALG_ID::SHA1);
    p.Execute(tpm, s);

    // So now we can use the session to authorize an action using handle h
    auto hmacSequenceHandle = tpm[s].HMAC_Start(h, null, TPM_ALG_ID::SHA1);

    tpm.FlushContext(s);
    tpm.FlushContext(h);
    tpm.FlushContext(hmacSequenceHandle);
} // PolicySecretSample()

void Samples::EncryptDecryptSample()
{
    Announce("EncryptDecryptSample");

    TPM_HANDLE prim = MakeStoragePrimary();

    // Make an AES key
    TPMT_PUBLIC inPublic(TPM_ALG_ID::SHA256,
						 TPMA_OBJECT::decrypt | TPMA_OBJECT::sign | TPMA_OBJECT::userWithAuth
                            | TPMA_OBJECT::sensitiveDataOrigin,
                         null,
                         TPMS_SYMCIPHER_PARMS(Aes128Cfb),
                         TPM2B_DIGEST_SYMCIPHER());

    auto aesKey = tpm.Create(prim, null, inPublic, null, null);

    TPM_HANDLE aesHandle = tpm.Load(prim, aesKey.outPrivate, aesKey.outPublic);

    ByteVec toEncrypt { 1, 2, 3, 4, 5, 4, 3, 2, 12, 3, 4, 5 };
    ByteVec iv(16);

    auto encrypted = tpm.EncryptDecrypt(aesHandle, (BYTE)0, TPM_ALG_ID::CFB, iv, toEncrypt);
    auto decrypted = tpm.EncryptDecrypt(aesHandle, (BYTE)1, TPM_ALG_ID::CFB, iv, encrypted.outData);

    cout << "AES encryption" << endl <<
            "in:  " << toEncrypt << endl <<
            "enc: " << encrypted.outData << endl <<
            "dec: " << decrypted.outData << endl;

    _ASSERT(decrypted.outData == toEncrypt);

    tpm.FlushContext(prim);
    tpm.FlushContext(aesHandle);
} // EncryptDecryptSample()

void Samples::SeededSession()
{
    Announce("SeededSession");

    // A seeded session is one in which a decryption key in the TPM is used
    // to decrypt a seed value that is folded into the session key.  THis
    // provides protection when authValues are known or can be inferred from
    // the protocol.

    // To start a seeded session we need a decryption key. Here we make a primary RSA key
    TPMT_PUBLIC storagePrimaryTemplate(TPM_ALG_ID::SHA1,
                                       TPMA_OBJECT::decrypt |  TPMA_OBJECT::restricted
                                        | TPMA_OBJECT::fixedParent | TPMA_OBJECT::fixedTPM
                                        | TPMA_OBJECT::sensitiveDataOrigin | TPMA_OBJECT::userWithAuth,
                                       null,
                                       TPMS_RSA_PARMS(Aes128Cfb, TPMS_NULL_ASYM_SCHEME(), 2048, 65537),
                                       TPM2B_PUBLIC_KEY_RSA());

    // Create the key
    auto saltKey = tpm.CreatePrimary(TPM_RH::OWNER, null, storagePrimaryTemplate, null, null);

    // Think up a salt value
    ByteVec salt { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    // Encrypt it using the new storage primary
    ByteVec encryptedSalt = saltKey.outPublic.EncryptSessionSalt(salt);

    // Start the session using the salt. The TPM needs the encrypted salt, and the
    // TSS.C++ library needs the plaintext salt.
    AUTH_SESSION sess = tpm.StartAuthSession(saltKey.handle, TPM_RH_NULL, TPM_SE::HMAC,
                                             TPM_ALG_ID::SHA1, TPMA_SESSION::continueSession,
                                             TPMT_SYM_DEF(TPM_ALG_ID::AES, 128, TPM_ALG_ID::CFB),
                                             salt, encryptedSalt);
    tpm.FlushContext(saltKey.handle);

    TestAuthSession(sess);
} // SeededSession()

PolicyNVCallbackData nvData;

PolicyNVCallbackData MyPolicyNVCallback(const string& _tag)
{
    return nvData;
}

void Samples::PolicyNVSample()
{
    Announce("PolicyNV");

    // PolicyNV allows actions to be gated on the contents of an NV-storage slot

    // First make an NV-slot and put some data in it
    ByteVec nvAuth { 1, 5, 1, 1 };
    TPM_HANDLE nvHandle = RandomNvHandle();

    // Try to delete the slot if it exists
    tpm._AllowErrors().NV_UndefineSpace(TPM_RH::OWNER, nvHandle);

    // CASE 1 - Simple NV-slot: Make a new simple NV slot, 16 bytes, RW with auth
    TPMS_NV_PUBLIC nvTemplate(nvHandle,           // Index handle
                              TPM_ALG_ID::SHA256, // Name-alg
                              TPMA_NV::AUTHREAD | // Attributes
                              TPMA_NV::AUTHWRITE, 
                              null,               // Policy
                              16);                // Size in bytes

    tpm.NV_DefineSpace(TPM_RH::OWNER, nvAuth, nvTemplate);
    nvHandle.SetAuth(nvAuth);

    // Write some data
    ByteVec toWrite { 1, 2, 3, 4, 5, 4, 3, 2, 1 };
    tpm.NV_Write(nvHandle, nvHandle, toWrite, 0);

    auto slotPublic = tpm.NV_ReadPublic(nvHandle);
    nvHandle.SetName(slotPublic.nvName);
    PolicyTree p(PolicyNV(toWrite, slotPublic.nvName, 0, TPM_EO::EQ));

    // Set up some data so that the NV-callback knows what to do
    nvData.AuthHandle = nvHandle;
    nvData.NvIndex = nvHandle;

    p.SetPolicyNvCallback(&MyPolicyNVCallback);

    // Get the policy digest
    TPM_HASH policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);

    // Set the primary policy based on whether the NV-slot has the correct value
    tpm.SetPrimaryPolicy(TPM_RH::OWNER, policyDigest, TPM_ALG_ID::SHA1);

    // Make sure that we can use the policy to authrorize an action
    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    p.Execute(tpm, s);

    ByteVec newAuth { 0xa, 0xb };
    tpm(s).HierarchyChangeAuth(TPM_RH::OWNER, newAuth);
    tpm.FlushContext(s);

    // Now change the NV-contents
    tpm.NV_Write(nvHandle, nvHandle, { 3, 1}, 0);

    // Now show that we can's use the policy any more
    s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    bool policyFailed = false;

    try {
        p.Execute(tpm, s);
    }
    catch (exception) {
        policyFailed = true;
    }

    _ASSERT(policyFailed);

    tpm.FlushContext(s);

    // Put things back the way they were
    tpm.SetPrimaryPolicy(TPM_RH::OWNER, null, TPM_ALG_NULL);
    tpm.HierarchyChangeAuth(TPM_RH::OWNER, null);
    tpm.NV_UndefineSpace(TPM_RH::OWNER, nvHandle);
} // PolicyNVSample()

void Samples::PolicyNameHashSample()
{
    Announce("PolicyNameHashSample");

    // PolicyNameHash restricts a policy to be authorized if and only if the
    // handles associated with the name command are as specified in the nameHash.
    // This is a contrived example showing showing a policy that lets anyone
    // perform a clear operation using the platform handle, even if they don't
    // know the associated auth-value.

    auto nameHash = TPM_HASH::FromHashOfData(TPM_ALG_ID::SHA1,
                                             TPM_HANDLE(TPM_RH::ENDORSEMENT).GetName());

    PolicyTree policyTree = PolicyNameHash(nameHash);
    TPM_HASH policyDigest = policyTree.GetPolicyDigest(TPM_ALG_ID::SHA1);

    // Set the endosement hierarchy new policy
    // (TSS uses its current auth value under cover to authorize this operation)
    tpm.SetPrimaryPolicy(TPM_RH::ENDORSEMENT, policyDigest, TPM_ALG_ID::SHA1);

    AUTH_SESSION sess = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    policyTree.Execute(tpm, sess);

    auto newAuth = Helpers::RandomBytes(20);

    // Cannot use the given policy for a different handle
    tpm[sess]._DemandError()
       .HierarchyChangeAuth(TPM_RH::OWNER, null);

    // Use the current auth value for this purpose
    tpm[sess].HierarchyChangeAuth(TPM_RH::ENDORSEMENT, newAuth);

    // Reset the auth value back to empty our using new authValue
    // (TSS uses it behind the scenes in an auto-generated non-policy session)
    tpm.HierarchyChangeAuth(TPM_RH::ENDORSEMENT, null);

    // Reset the endorsement hierarchy policy
    tpm.SetPrimaryPolicy(TPM_RH::ENDORSEMENT, null, TPM_ALG_NULL);

    if (tpm._GetDevice().PlatformAvailable())
    {
        // One more usage of the same policy session with a different allowed nameHash 
        tpm.PolicyRestart(sess);

        nameHash = TPM_HASH::FromHashOfData(TPM_ALG_ID::SHA1,
                                            TPM_HANDLE(TPM_RH::PLATFORM).GetName());
        ((PolicyNameHash*)policyTree.GetTree()[0])->NameHash = nameHash;

        policyTree.Execute(tpm, sess);

        // Set the platform policy to this value
        policyDigest = policyTree.GetPolicyDigest(TPM_ALG_ID::SHA1);
        tpm.SetPrimaryPolicy(TPM_RH::PLATFORM, policyDigest, TPM_ALG_ID::SHA1);

        tpm[sess].Clear(TPM_RH::PLATFORM);
        cout << "Command Clear() authorized by policy session with PolicyNameHash assertion" << endl;

        // Put things back the way they were
        tpm.SetPrimaryPolicy(TPM_RH::PLATFORM, null, TPM_ALG_NULL);
    }

    tpm.FlushContext(sess);
} // PolicyNameHashSample()

void Samples::ReWrapSample()
{
    Announce("RewrapSample");

    // Make an exportable key
    PolicyTree p(PolicyCommandCode(TPM_CC::Duplicate, ""));
    TPM_HASH policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);
    TPM_HANDLE duplicatableKey = MakeDuplicableStoragePrimary(policyDigest);

    // Make a new storage parent
    TPM_HANDLE newParent = MakeStoragePrimary();

    // Duplicate the key to the new parent
    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    p.Execute(tpm, s);
    auto dupResp = tpm(s).Duplicate(duplicatableKey, newParent, null, null);

    // And rewrap
    auto rewrap = tpm.Rewrap(TPM_RH_NULL, newParent, dupResp.duplicate, duplicatableKey.GetName(), null);

    tpm.FlushContext(duplicatableKey);
    tpm.FlushContext(newParent);
    tpm.FlushContext(s);
} // ReWrapSample()

void Samples::BoundSession()
{
    Announce("BoundSession");

    // A bound session is a session that is associated with a specific TPM
    // entity - a loaded key or other object. When the session is used in
    // conjunction with the entity to which the session is bound, the HMAC
    // calculation omits the associated auth-value (the auth-value only).
    // We will bind to the owner and show how the session is used to authorize
    // the bound (owner) action as well as privacy administration.

    // First set the owner-auth to a non-NULL value
    ByteVec ownerAuth { 0, 2, 1, 3, 5, 6 };
    tpm.HierarchyChangeAuth(TPM_RH::OWNER, ownerAuth);

    // Start a session bound to the owner-handle
    AUTH_SESSION s = tpm.StartAuthSession(TPM_RH_NULL, TPM_RH::OWNER, TPM_SE::HMAC,
                                          TPM_ALG_ID::SHA1, TPMA_SESSION::continueSession,
                                          TPMT_SYM_DEF(), null, null);

    // Create a slot using the owner handle
    TPM_HANDLE nvHandle = RandomNvHandle();
    ByteVec nvAuth = { 5, 4, 3, 2, 1, 0 };
    nvHandle.SetAuth(nvAuth);

    tpm._AllowErrors()
       .NV_UndefineSpace(TPM_RH::OWNER, nvHandle);

    TPMS_NV_PUBLIC nvTemplate(nvHandle,           // Index handle
                              TPM_ALG_ID::SHA256, // Name-alg
                              TPMA_NV::AUTHREAD | // Attributes
                              TPMA_NV::AUTHWRITE, 
                              null,            // Policy
                              16);                // Size in bytes

    // This is a degenerate bound-to-the-SAME-object use case
    tpm(s).NV_DefineSpace(TPM_RH::OWNER, nvAuth, nvTemplate);

    // Get the name
    auto nvInfo = tpm.NV_ReadPublic(nvHandle);
    nvHandle.SetName(nvInfo.nvName);

    // Now write something using the normal bound-to-a-DIFFERENT-object scenario
    tpm(s).NV_Write(nvHandle, nvHandle, { 0, 1, 2, 3 }, 0);

    tpm.NV_UndefineSpace(TPM_RH::OWNER, nvHandle);
    tpm.HierarchyChangeAuth(TPM_RH::OWNER, null);
    tpm.FlushContext(s);
} // BoundSession()

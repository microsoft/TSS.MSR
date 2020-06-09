/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"
#include "Tpm2.h"

#include "Samples.h"

using namespace TpmCpp;     // TSS.CPP declarations
using namespace std;


// Run the samples described in the TSS.C++ Intro paper in turn

void Samples::ArrayParameters()
{
    // Get 20 random bytes from the TPM
    ByteVec rand = tpm.GetRandom(20);
    cout << "Random bytes: " << rand << endl;

    // Get random data from the (default) OS random-number generator and
    // add it to the TPM entropy pool.
    ByteVec osRand = Helpers::RandomBytes(20);
    tpm.StirRandom(osRand);
}

void Samples::PWAPAuth()
{
    // Most TPM entities are referenced by handle
    TPM_HANDLE hierarchyHandle(TPM_RH::ENDORSEMENT);

    // On top of the fields defined by the TPM 2.0 spec TPM_HANDLE class also includes
    // an authValue fields to be used by the TSS behind the scenes.
    hierarchyHandle.SetAuth(null);

    // If we issue a command that needs authorization, the TSS automatically
    // uses the authValue contained in the handle in a password session.

    // We can use the current auth values to authorize its change to a new value
    ByteVec newAuth { 1, 2, 3, 4, 5 };
    tpm.HierarchyChangeAuth(hierarchyHandle, newAuth);

    // If we want to do further TPM administration we must associate the new
    // authValue with the handle.
    hierarchyHandle.SetAuth(newAuth);

    // And put things back the way they were
    tpm.HierarchyChangeAuth(hierarchyHandle, null);
}

void Samples::Errors()
{
    // Construct an ilegal handle value
    TPM_HANDLE invalidHandle((UINT32) - 1);

    // Try to read the associated information
    try {
        tpm.ReadPublic(invalidHandle);
    }
    catch (system_error e) {
        // Note that the following e.what() may produce a platform specific
        // result. For example, this error typically corresponds to the ERFKILL
        // errno on a linux platform.
        cout << "As expected, the TPM returned an error:" << e.what() << endl;
    }

    // We can also suppress the exception and do an explit error check
    ReadPublicResponse rpr = tpm._AllowErrors().ReadPublic(invalidHandle);

    cout << rpr.outPublic.parameters << endl;

    if (tpm._GetLastResponseCode() != TPM_RC::SUCCESS)
        cout << "Command failed, as expected." << endl;

    // If we WANT an error we can turn things around so that an exception is
    // thrown if a specific error is _not_ seen.
    tpm._ExpectError(TPM_RC::VALUE).ReadPublic(invalidHandle);

    // Or any error
    tpm._DemandError().ReadPublic(invalidHandle);
}

void Samples::Structures()
{
    UINT32 pcrIndex = 0;

    // "Event" PCR-0 with the binary data
    tpm.PCR_Event(pcrIndex, ByteVec { 0, 1, 2, 3, 4 });

    // Read PCR-0
    std::vector<TPMS_PCR_SELECTION> pcrToRead = { {TPM_ALG_ID::SHA1, pcrIndex} };
    PCR_ReadResponse pcrVal = tpm.PCR_Read(pcrToRead);

    // Now print it out in pretty-printed human-readable form
    cout << "Text form of pcrVal" << endl << pcrVal.ToString() << endl;

    // Now in JSON
    std::string pcrValInJSON = pcrVal.Serialize(SerializationType::JSON);
    cout << "JSON form" << endl << pcrValInJSON << endl;
    
    // Now in TPM-binary form
    ByteVec tpmBinaryForm = pcrVal.toBytes();
    cout << "TPM Binary form:" << endl << tpmBinaryForm << endl;
    
    // Now rehydrate the JSON and binary forms to new structures
    PCR_ReadResponse fromJSON, fromBinary;
    fromJSON.Deserialize(SerializationType::JSON, pcrValInJSON);
    fromBinary.initFromBytes(tpmBinaryForm);

    // And check that the reconstituted values are the same as the originals with
    // the built-in value-equality operators.

    _ASSERT(pcrVal == fromJSON);
    cout << "JSON Deserialization succeeded" << endl;

    _ASSERT(pcrVal == fromBinary);
    cout << "Binary serialization succeeded" << endl;
} // Structures()

void Samples::HMACSessions()
{
    // Create a basic HMAC authorization session: no salt, no encryption, no bound-object...
    AUTH_SESSION sess = tpm.StartAuthSession(TPM_SE::HMAC, TPM_ALG_ID::SHA1);

    // ... and use with all commands requiring authorization

    const size_t IndexSize = 50;
    ByteVec nvAuth = Helpers::RandomBytes(20);
    TPM_HANDLE nvHandle = RandomNvHandle();

    // Check if the slot exists
    auto nvPub = tpm._AllowErrors()
                    .NV_ReadPublic(nvHandle);
    if (tpm._LastCommandSucceeded())
    {
        // Try to delete the existing slot using the owner hierarchy to authorize the operation

        // Computing HMAC session requires TPM names of the handles passed to the command
        nvHandle.SetName(nvPub.nvName);

        tpm._Sessions(sess)
           ._AllowErrors()
           .NV_UndefineSpace(TPM_RH::OWNER, nvHandle);
        if (!tpm._LastCommandSucceeded())
        {
            cerr << "Failed to delete existing NV slot #" << nvHandle << endl;
            tpm.FlushContext(sess);
            return;
        }
    }

    TPMS_NV_PUBLIC nvTemplate(nvHandle,             // Index handle
                              TPM_ALG_ID::SHA1,     // Name-alg
                              // Attributes
                              TPMA_NV::AUTHREAD | TPMA_NV::AUTHWRITE | TPMA_NV::NO_DA,
                              null,                 // Policy
                              IndexSize);           // Index sata size in bytes

    // Use a terser form of session specification (multiple sessions will require
    // the matching number of index operators applied one after another).
    tpm[sess].NV_DefineSpace(TPM_RH::OWNER, nvAuth, nvTemplate);

    // Let TSS know the authVal of the entity associated with this handle ...
    nvHandle.SetAuth(nvAuth);
    // and its name
    nvPub = tpm.NV_ReadPublic(nvHandle);
    nvHandle.SetName(nvPub.nvName);

    // Write some data at some offset
    const size_t Offset = IndexSize / 4;
    ByteVec toWrite = Helpers::RandomBytes(IndexSize - Offset);
    tpm[sess].NV_Write(nvHandle, nvHandle, toWrite, Offset);

    nvPub = tpm.NV_ReadPublic(nvHandle);
    nvHandle.SetName(nvPub.nvName);

    // And read the whole index back
    ByteVec dataRead = tpm[sess].NV_Read(nvHandle, nvHandle, IndexSize, 0);
    cout << "Data read from nv-slot:   " << dataRead << endl;

    // And make sure that it's good
    _ASSERT(equal(toWrite.begin(), toWrite.end(), dataRead.begin() + Offset));
    _ASSERT(all_of(dataRead.begin(), dataRead.begin() + Offset, [](BYTE b){ return b == 0; }) ||
            all_of(dataRead.begin(), dataRead.begin() + Offset, [](BYTE b){ return b == 0xFF; }));

    // Clean up
    if (tpm._GetDevice().PlatformAvailable())
    {
        // Perform an operation authorizing with an HMAC
        tpm[sess].Clear(TPM_RH::PLATFORM);
    }
    else
        tpm[sess].NV_UndefineSpace(TPM_RH::OWNER, nvHandle);

    tpm.FlushContext(sess);
} // HMACSessions()

void Samples::SigningPrimary()
{
    // To create a primary key the TPM must be provided with a template.
    // This is for an RSA1024 signing key.
    TPMT_PUBLIC templ(TPM_ALG_ID::SHA1,
                      TPMA_OBJECT::sign |
                      TPMA_OBJECT::fixedParent |
                      TPMA_OBJECT::fixedTPM | 
                      TPMA_OBJECT::sensitiveDataOrigin |
                      TPMA_OBJECT::userWithAuth,
                      null,
                      TPMS_RSA_PARMS(
                          TPMT_SYM_DEF_OBJECT(),
                          TPMS_SCHEME_RSASSA(TPM_ALG_ID::SHA1), 1024, 65537),
                      TPM2B_PUBLIC_KEY_RSA());

    // Set the use-auth for the key. Note the second parameter is NULL
    // because we are asking the TPM to create a new key.
    ByteVec userAuth = ByteVec { 1, 2, 3, 4 };
    TPMS_SENSITIVE_CREATE sensCreate(userAuth, null);

    // We don't need to know the PCR-state with the key was created so set this
    // parameter to a null-vector.
    std::vector<TPMS_PCR_SELECTION> pcrSelect;

    // Ask the TPM to create the key
    auto newPrimary = tpm.CreatePrimary(TPM_RH::OWNER, sensCreate, templ, null, pcrSelect);

    // Print out the public data for the new key. Note the "false" parameter to
    // ToString() "pretty-prints" the byte-arrays.
    cout << "New RSA primary key" << endl << newPrimary.outPublic.ToString(false) << endl;

    // Sign something with the new key. First set the auth-value in the handle.
    TPM_HANDLE& signKey = newPrimary.handle;
    signKey.SetAuth(userAuth);

    TPM_HASH dataToSign = TPM_HASH::FromHashOfString(TPM_ALG_ID::SHA1, "abc");

    auto sig = tpm.Sign(signKey, dataToSign, TPMS_NULL_SIG_SCHEME(), TPMT_TK_HASHCHECK());

    cout << "Signature:" << endl << sig->ToString() << endl;

    // Use TSS.C++ to validate the signature
    bool sigOk = newPrimary.outPublic.ValidateSignature(dataToSign, *sig);
    cout << "Signature is " << (sigOk ? "OK" : "BAD") << endl;
    _ASSERT(sigOk);

    tpm.FlushContext(newPrimary.handle);
} // SigningPrimary()

void Samples::SimplePolicy()
{
    if (!tpm._GetDevice().PlatformAvailable())
    {
        cout << endl << "~~~~ SAMPLE SimplePolicy() SKIPPED ~~~~" << endl << endl;
        return;
    }

    // A TPM policy is a list or tree of Policy Assertions. We will create a
    // policy that authorizes actions when they are issued at locality 1.

    // Create the simple policy "tree"
    PolicyTree policyTree(PolicyLocality(TPMA_LOCALITY::LOC_ONE, ""));

    // Get the policy digest
    TPM_HASH policyDigest = policyTree.GetPolicyDigest(TPM_ALG_ID::SHA1);

    // Now configure the TPM so that storage-hierarchy actions can be performed
    // by any sofware that can issue commands at locality 1.  We do this using
    // the platform auth-value

    tpm.SetPrimaryPolicy(TPM_RH::PLATFORM, policyDigest, TPM_ALG_ID::SHA1);

    // Now execute the policy
    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);

    // Execute the policy using the session. This issues a sequence of TPM
    // operations to "prove" to the TPM that the policy is satisfied. In this
    // very simple case Execute() will call
    policyTree.Execute(tpm, s);

    // Execute a Clear operation at locality 1 with the policy session
    tpm._GetDevice().SetLocality(1);
    tpm(s).Clear(TPM_RH::PLATFORM);
    tpm._GetDevice().SetLocality(0);
    tpm.FlushContext(s);

    // But the command should fail at locality zero
    s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    policyTree.Execute(tpm, s);

    tpm(s)._ExpectError(TPM_RC::LOCALITY)
          .Clear(TPM_RH::PLATFORM);
    tpm.FlushContext(s);

    // Clear the hierarch policy
    tpm.SetPrimaryPolicy(TPM_RH::PLATFORM, ByteVec(), TPM_ALG_NULL);
} // SimplePolicy()

void Samples::ThreeElementPolicy()
{
    // We will construct a policy that needs pcr-15 to be set to a certain value
    // (a value that we will measure) and needs physical-presence to be asserted
    // and that the command be issued at locality 1.

    // First set PCR-15 to an "interesting" value and read it.
    UINT32 pcr = 15;
    TPM_ALG_ID bank = TPM_ALG_ID::SHA1;
    tpm.PCR_Event(TPM_HANDLE::Pcr(pcr), ByteVec { 1, 2, 3, 4 });

    // Read the current value
    auto pcrSelection = TPMS_PCR_SELECTION::GetSelectionArray(bank, pcr);
    auto startPcrVal = tpm.PCR_Read(pcrSelection);
    auto currentValue = startPcrVal.pcrValues;

    // Create a policy naming this PCR+value, PP, and locality - 1
    PolicyTree policyTree(PolicyPcr(currentValue, pcrSelection),
                                    PolicyPhysicalPresence(),
                                    PolicyLocality(TPMA_LOCALITY::LOC_TWO));

    // Get the policy digest
    TPM_HASH policyDigest = policyTree.GetPolicyDigest(TPM_ALG_ID::SHA1);

    if (!tpm._GetDevice().PlatformAvailable())
        tpm._AllowErrors();

    // Set the policy so that pcr-20 can only be extended with this policy
    TPM_HANDLE pcr2 = TPM_HANDLE::Pcr(20);
    tpm.PCR_SetAuthPolicy(TPM_RH::PLATFORM, policyDigest, TPM_ALG_ID::SHA1, pcr2);

    if (!tpm._LastCommandSucceeded())
        return;

    // Show that we can no longer extend.
    tpm._ExpectError(TPM_RC::AUTH_TYPE)
       .PCR_Event(pcr2, ByteVec {0, 1});

    // But we can perform the action with the appropriate policy + assertion of PP
    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    policyTree.Execute(tpm, s);

    if (tpm._GetDevice().LocalityCtlAvailable() && tpm._GetDevice().ImplementsPhysicalPresence())
    {
        // Use the session + PP to execute the command
        tpm._GetDevice().AssertPhysicalPresence(true);
        tpm._GetDevice().SetLocality(2);
        auto pcrAfterExtend = tpm(s).PCR_Event(pcr2, ByteVec {0, 1});
        tpm._GetDevice().SetLocality(0);
        tpm._GetDevice().AssertPhysicalPresence(false);

        cout << "PCR after policy-based extend: " << endl << pcrAfterExtend[0].ToString() << endl;
    }
    tpm.FlushContext(s);

    // Change the PCR and show that this no longer works
    tpm.PCR_Event(TPM_HANDLE::Pcr(pcr), ByteVec { 1, 2, 3, 4 });

    bool worked = true;
    s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    
    try {
        policyTree.Execute(tpm, s);
    }
    catch (exception) {
        worked = false;
    }

    if (!worked)
        cout << "Policy failed after PCR-extend, as expected." << endl;
    _ASSERT(!worked);

    tpm.FlushContext(s);

    // Reset the PCR-policy
    tpm.PCR_SetAuthPolicy(TPM_RH::PLATFORM, ByteVec(), TPM_ALG_NULL, pcr2);
} // ThreeElementPolicy()

void Samples::PolicyOrSample()
{
    // Create a policy demanding either locality-1 OR physical presence
    // In this sample we execute the policy and check the TPM-policy-digest
    // but do not attempt to use the policy session to authorize an action.

    PolicyTree branch1(PolicyLocality(TPMA_LOCALITY::LOC_ONE, "loc-branch"));
    PolicyTree branch2(PolicyPhysicalPresence("pp-branch"));
    PolicyTree policyTree(PolicyOr(branch1.GetTree(), branch2.GetTree()));

    // Get the policy-digest
    auto policyDigest = policyTree.GetPolicyDigest(TPM_ALG_ID::SHA1);

    // Execute one branch...
    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    policyTree.Execute(tpm, s, "loc-branch");
    auto policyDigest2 = tpm.PolicyGetDigest(s);

    _ASSERT(policyDigest == policyDigest2);
    if (policyDigest == policyDigest2)
        cout << "PolicyOR (branch1) digest is as expected:" << endl << policyDigest2 << endl;

    tpm.FlushContext(s);
    
    // And then the other branch
    s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    policyTree.Execute(tpm, s, "pp-branch");
    policyDigest2 = tpm.PolicyGetDigest(s);

    _ASSERT(policyDigest == policyDigest2);
    if (policyDigest == policyDigest2)
        cout << "PolicyOR (branch1) digest is as expected:" << endl << policyDigest2 << endl;

    tpm.FlushContext(s);
} // PolicyOrSample()

void Samples::RunDocSamples()
{
    ArrayParameters();
    PWAPAuth();
    Errors();
    Structures();
    HMACSessions();
    SigningPrimary();
    SimplePolicy();
    ThreeElementPolicy();
    PolicyOrSample();
}

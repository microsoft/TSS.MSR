/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"
#include "Tpm2.h"

// All TSS.C++ code is in the TpmCpp namespace
using namespace TpmCpp;

#include "Samples.h"

// Run the samples described in the TSS.C++ Intro paper in turn
void RunSamples();

Tpm2 tpm;
TpmTcpDevice device;

// Initialize the library and local TPM
void InitTpm()
{
    // Connect the Tpm2 device to a simulator running on the same machine
    if (!device.Connect("127.0.0.1", 2321)) {
        cerr << "Could not connect to the TPM device";
        return;
    }

    // Instruct the Tpm2 object to send commands to the local TPM simulator
    tpm._SetDevice(device);

    // Power-cycle the simulator
    device.PowerOff();
    device.PowerOn();

    // and startup the TPM
    tpm.Startup(TPM_SU::CLEAR);

    return;
}

void Shutdown()
{
    tpm.Shutdown(TPM_SU::CLEAR);
    device.PowerOff();
    device.~TpmTcpDevice();
    return;
}

void ArrayParameters()
{
    // Get 20 random bytes from the TPM
    std::vector<BYTE> rand = tpm.GetRandom(20);
    cout << "Random bytes: " << rand << endl;

    // Get random data from the (default) OS random-number generator and
    // add it to the TPM entropy pool.
    vector<BYTE> osRand = tpm._GetRandLocal(20);
    tpm.StirRandom(osRand);

    return;
}

void PWAPAuth()
{
    // Most TPM entities are referenced by handle
    TPM_HANDLE platformHandle = TPM_HANDLE::FromReservedHandle(TPM_RH::PLATFORM);

    // The TSS.C++ TPM_HANDLE class also includes an authValue to be used
    // whenever this handle is used.
    vector<BYTE> NullAuth {};
    platformHandle.SetAuth(NullAuth);

    // If we issue a command that needs authorization TSS.C++ automatically
    // uses the authValue contained in the handle.
    tpm.Clear(platformHandle);

    // We can use the "old" platform-auth to install a new value
    vector<BYTE> newAuth { 1, 2, 3, 4, 5 };
    tpm.HierarchyChangeAuth(platformHandle, newAuth);

    // If we want to do further TPM administration we must associate the new
    // authValue with the handle.
    platformHandle.SetAuth(newAuth);
    tpm.Clear(platformHandle);

    // And put things back the way they were
    tpm.HierarchyChangeAuth(platformHandle, NullAuth);

    return;
}

void Errors()
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
    tpm._AllowErrors().ReadPublic(invalidHandle);

    if (tpm._GetLastError() != TPM_RC::SUCCESS) {
        cout << "Command failed, as expected." << endl;
    }

    // If we WANT an error we can turn things around so that an exception is
    // thrown if a specific error is _not_ seen.
    tpm._ExpectError(TPM_RC::VALUE).ReadPublic(invalidHandle);

    // Or any error
    tpm._DemandError().ReadPublic(invalidHandle);

    return;
}

void Structures()
{
    UINT32 pcrIndex = 0;

    // "Event" PCR-0 with the binary data
    tpm.PCR_Event(pcrIndex, std::vector<BYTE> { 0, 1, 2, 3, 4 });

    // Read PCR-0
    vector<TPMS_PCR_SELECTION> pcrToRead { TPMS_PCR_SELECTION(TPM_ALG_ID::SHA1, pcrIndex) };
    PCR_ReadResponse pcrVal = tpm.PCR_Read(pcrToRead);

    // Now print it out in pretty-printed human-readable form
    cout << "Text form of pcrVal" << endl << pcrVal.ToString() << endl;

    // Now in JSON
    string pcrValInJSON = pcrVal.Serialize(SerializationType::JSON);
    cout << "JSON form" << endl << pcrValInJSON << endl;
    
    // Now in TPM-binary form
    vector<BYTE> tpmBinaryForm = pcrVal.ToBuf();
    cout << "TPM Binary form:" << endl << tpmBinaryForm << endl;
    
    // Now rehydrate the JSON and binary forms to new structures
    PCR_ReadResponse fromJSON, fromBinary;
    fromJSON.Deserialize(SerializationType::JSON, pcrValInJSON);
    fromBinary.FromBuf(tpmBinaryForm);

    // And check that the reconstituted values are the same as the originals with
    // the built-in value-equality operators.

    if (pcrVal != fromJSON) {
        cout << "JSON Deserialization failed" << endl;
    }

    if (pcrVal == fromBinary) {
        cout << "Binary serialization succeeded" << endl;
    }

    return;
}

void HMACSessions()
{
    // Start a simple HMAC authorization session: no salt, no encryption, no bound-object.
    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::HMAC, TPM_ALG_ID::SHA1);

    // Perform an operation authorizing with an HMAC
    tpm._Sessions(s).Clear(tpm._AdminPlatform);

    // A more terse way of associating an explicit session with a command
    tpm(s).Clear(tpm._AdminPlatform);

    // And clean up
    tpm.FlushContext(s);

    return;
}

void SigningPrimary()
{
    // To create a primary key the TPM must be provided with a template.
    // This is for an RSA1024 signing key.
    vector<BYTE> NullVec;
    TPMT_PUBLIC templ(TPM_ALG_ID::SHA1,
                      TPMA_OBJECT::sign |
                      TPMA_OBJECT::fixedParent |
                      TPMA_OBJECT::fixedTPM | 
                      TPMA_OBJECT::sensitiveDataOrigin |
                      TPMA_OBJECT::userWithAuth,
                      NullVec,
                      TPMS_RSA_PARMS(
                          TPMT_SYM_DEF_OBJECT::NullObject(),
                          TPMS_SCHEME_RSASSA(TPM_ALG_ID::SHA1), 1024, 65537),
                      TPM2B_PUBLIC_KEY_RSA(NullVec));

    // Set the use-auth for the key. Note the second parameter is NULL
    // because we are asking the TPM to create a new key.
    ByteVec userAuth = ByteVec { 1, 2, 3, 4 };
    TPMS_SENSITIVE_CREATE sensCreate(userAuth, NullVec);

    // We don't need to know the PCR-state with the key was created so set this
    // parameter to a null-vector.
    vector<TPMS_PCR_SELECTION> pcrSelect {};

    // Ask the TPM to create the key
    CreatePrimaryResponse newPrimary = tpm.CreatePrimary(tpm._AdminOwner,
                                                         sensCreate,
                                                         templ,
                                                         NullVec,
                                                         pcrSelect);

    // Print out the public data for the new key. Note the "false" parameter to
    // ToString() "pretty-prints" the byte-arrays.
    cout << "New RSA primary key" << endl << newPrimary.outPublic.ToString(false) << endl;

    // Sign something with the new key. First set the auth-value in the handle.
    TPM_HANDLE& signKey = newPrimary.handle;
    signKey.SetAuth(userAuth);

    TPMT_HA dataToSign = TPMT_HA::FromHashOfString(TPM_ALG_ID::SHA1, "abc");

    auto sig = tpm.Sign(signKey,
                        dataToSign.digest,
                        TPMS_NULL_SIG_SCHEME(),
                        TPMT_TK_HASHCHECK::NullTicket());

    cout << "Signature:" << endl << sig.ToString(false) << endl;

    // Use TSS.C++ to validate the signature
    bool sigOk = newPrimary.outPublic.ValidateSignature(dataToSign.digest,
                                                        *sig.signature);
    _ASSERT(sigOk);

    tpm.FlushContext(newPrimary.handle);

    return;
}

void SimplePolicy()
{
    // A TPM policy is a list or tree of Policy Assertions. We will create a
    // policy that authorizes actions when they are issued at locality 1.

    // Create the simple policy "tree"
    PolicyTree p(PolicyLocality(TPMA_LOCALITY::LOC_ONE, ""));

    // Get the policy digest
    TPMT_HA policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);

    // Now configure the TPM so that storage-hierarchy actions can be performed
    // by any sofware that can issue commands at locality 1.  We do this using
    // the platform auth-value

    tpm.SetPrimaryPolicy(tpm._AdminPlatform, policyDigest.digest, TPM_ALG_ID::SHA1);

    // Now execute the policy
    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);

    // Execute the policy using the session. This issues a sequence of TPM
    // operations to "prove" to the TPM that the policy is satisfied. In this
    // very simple case Execute() will call
    p.Execute(tpm, s);

    // Execute a Clear operation at locality 1 with the policy session
    tpm._GetDevice().SetLocality(1);
    tpm(s).Clear(tpm._AdminPlatform);
    tpm._GetDevice().SetLocality(0);
    tpm.FlushContext(s);

    // But the command should fail at locality zero
    s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    p.Execute(tpm, s);

    tpm(s)._ExpectError(TPM_RC::LOCALITY).Clear(tpm._AdminPlatform);
    tpm.FlushContext(s);

    // Clear the hierarch policy
    tpm.SetPrimaryPolicy(tpm._AdminPlatform, vector<BYTE>(), TPM_ALG_ID::_NULL);

    return;
}

void ThreeElementPolicy()
{
    // We will construct a policy that needs pcr-15 to be set to a certain value
    // (a value that we will measure) and needs physical-presence to be asserted
    // and that the command be issued at locality 1.

    // First set PCR-15 to an "interesting" value and read it.
    UINT32 pcr = 15;
    TPM_ALG_ID bank = TPM_ALG_ID::SHA1;
    tpm.PCR_Event(TPM_HANDLE::PcrHandle(pcr), ByteVec { 1, 2, 3, 4 });

    // Read the current value
    vector<TPMS_PCR_SELECTION> pcrSelection = TPMS_PCR_SELECTION::GetSelectionArray(bank, pcr);
    auto startPcrVal = tpm.PCR_Read(pcrSelection);
    auto currentValue = startPcrVal.pcrValues;

    // Create a policy naming this PCR+value, PP, and locality - 1
    PolicyTree p(PolicyPcr(currentValue, pcrSelection),
                 PolicyPhysicalPresence(),
                 PolicyLocality(TPMA_LOCALITY::LOC_TWO));

    // Get the policy digest
    TPMT_HA policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);

    // set the policy so that pcr-20 can only be extended with this policy
    TPM_HANDLE pcr2 = TPM_HANDLE::PcrHandle(20);
    tpm.PCR_SetAuthPolicy(tpm._AdminPlatform,
                          policyDigest.digest,
                          TPM_ALG_ID::SHA1, pcr2);

    // Show that we can no longer extend.
    tpm._ExpectError(TPM_RC::AUTH_TYPE).PCR_Event(pcr2, vector<BYTE> {0, 1});

    // But we can perform the action with the appropriate policy + assertion of PP
    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    p.Execute(tpm, s);

    // Use the session + PP to execute the command
    tpm._GetDevice().PPOn();
    tpm._GetDevice().SetLocality(2);
    auto pcrAfterExtend = tpm(s).PCR_Event(pcr2, vector<BYTE> {0, 1});
    tpm._GetDevice().SetLocality(0);
    tpm._GetDevice().PPOff();
    tpm.FlushContext(s);

    cout << "PCR after policy-based extend: " << endl << pcrAfterExtend[0].ToString() << endl;

    // Change the PCR and show that this no longer works
    tpm.PCR_Event(TPM_HANDLE::PcrHandle(pcr), ByteVec { 1, 2, 3, 4 });

    bool worked = true;
    s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    
    try {
        p.Execute(tpm, s);
    }
    catch (exception) {
        worked = false;
    }

    _ASSERT(!worked);

    if (!worked) {
        cout << "Policy failed after PCR-extend, as expected." << endl;
    }

    tpm.FlushContext(s);

    // Reset the PCR-policy
    tpm.PCR_SetAuthPolicy(tpm._AdminPlatform,
                          vector<BYTE>(),
                          TPM_ALG_ID::_NULL, 
                          pcr2);
    return;
}

void PolicyOrSample()
{

    // Create a policy demanding either locality-1 OR physical presence
    // In this sample we execute the policy and check the TPM-policy-digest
    // but do not attempt to use the policy session to authorize an action.

    PolicyTree branch1(PolicyLocality(TPMA_LOCALITY::LOC_ONE, "loc-branch"));
    PolicyTree branch2(PolicyPhysicalPresence("pp-branch"));

    PolicyTree p(PolicyOr(branch1.GetTree(), branch2.GetTree()));

    // Get the policy-digest
    auto policyDigest = p.GetPolicyDigest(TPM_ALG_ID::SHA1);

    // Execute one branch...
    AUTH_SESSION s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    p.Execute(tpm, s, "loc-branch");
    auto policyDigest2 = tpm.PolicyGetDigest(s);

    _ASSERT(policyDigest.digest == policyDigest2);

    if (policyDigest.digest == policyDigest2) {
        cout << "PolicyOR (branch1) digest is as expected:" << endl << policyDigest2 << endl;
    }

    tpm.FlushContext(s);
    
    // And then the other branch
    s = tpm.StartAuthSession(TPM_SE::POLICY, TPM_ALG_ID::SHA1);
    p.Execute(tpm, s, "pp-branch");
    policyDigest2 = tpm.PolicyGetDigest(s);

    _ASSERT(policyDigest.digest == policyDigest2);

    if (policyDigest.digest == policyDigest2) {
        cout << "PolicyOR (branch1) digest is as expected:" << endl << policyDigest2 << endl;
    }

    tpm.FlushContext(s);
}

void RunSamples()
{
    InitTpm();
    ArrayParameters();
    PWAPAuth();
    Errors();
    Structures();
    HMACSessions();
    SigningPrimary();
    SimplePolicy();
    ThreeElementPolicy();
    PolicyOrSample();
    Shutdown();

    return;
}

void GetRandomSim()
{
    // First, create a TpmDevice and try to connect to the TPM
    TpmTcpDevice device;

    if (!device.Connect("127.0.0.1", 2321)) {
        cerr << "Could not connect to the TPM device";
        return;
    }

    // The TPM is easiest to access via a Tpm2 object "on top" of the low-level TpmDevice object
    Tpm2 tpm(device);

    // When devloping against the simulator you must perform some of the
    // functions that would normally be managed by the BIOS and hardware.
    device.PowerOff();
    device.PowerOn();
    tpm.Startup(TPM_SU::CLEAR);

    // Tpm2 lets a developer call TPM functions directly. Here we get 20 bytes of random data.
    std::vector<BYTE> rand = tpm.GetRandom(20);

    // Which we can print out...
    cout << "Random bytes: " << rand << endl;

    return;
}

void GetRandomTbs()
{
#ifdef WIN32
    // Create a TpmDevice object and attach it to the TPM. Here we
    // use the Windows TPM Base Services OS interface.
    TpmTbsDevice device;

    if (!device.Connect()) {
        cerr << "Could not connect to the TPM device";
        return;
    }

    // Create a Tpm2 object "on top" of the device.
    Tpm2 tpm(device);

    // Get 20 bytes of random data from
    std::vector<BYTE> rand = tpm.GetRandom(20);

    // Print it out.
    cout << "Random bytes: " << rand << endl;
#endif

#ifdef __linux__
    // No TBS.
#endif
    return;
}

void GetRandomSimulator()
{
    // Create a TpmDevice object and attach it to the TPM. Here we
    // attach to a TPM simulator process running on the same host.
    TpmTcpDevice device;

    if (!device.Connect("127.0.0.1", 2321)) {
        cerr << "Could not connect to the TPM device";
        return;
    }

    // Create a Tpm2 object "on top" of the device.
    Tpm2 tpm(device);

    // When talking to the simulator you must perform some of the startup
    // functions that would normally happen automatically or be done by
    // the BIOS (note: PowerOff does nothing if the TPM is already powered
    // off, but lets this sample run whatever the state of the TPM.)
    device.PowerOff();
    device.PowerOn();
    tpm.Startup(TPM_SU::CLEAR);

    // Get 20 bytes of random data
    std::vector<BYTE> rand = tpm.GetRandom(20);

    // And print it out.
    cout << "Random bytes: " << rand << endl;

    // And shut down the TPM
    tpm.Shutdown(TPM_SU::CLEAR);
    device.PowerOff();

    return;
}

# Tpm2Tester

Tpm2Tester is a framework facilitating TPM 2.0 test suites creation. It is implemented as a .Net DLL that is linked to an application supplying the actual tests. The framework provides the following functionality and services:

- An extensive set of attributes to express the needs and the scope of a particular test case.
- Parsing command line (with dozens of options available), and configuring the test session correspondingly.
- Automatic pre- and post-test cleanup of the TPM device.
- Obtaining the actual TPM device configuration and exposing it to the test application in a structured form convenient for programmatic access.
- Automatic enumeration of the test cases in the client application, and determining the subset to execute based on command line options and available TPM functionality.
- Various execution modes for the test session: sequential, randomized repetitive, stress, fuzzing, reproduction.
- Computation and reporting detailed test session statistics:
  - Executed test cases;
  - Executed (and not executed) TPM 2.0 commands (how many successes and failures, which tests cases used a particular command);
  - Detailed information about test failures
    - location;
    - call stack;
    - parameters of the failed TPM 2.0 command (including complete dump of TPM data structures );
    - parameters in the failed assertion (assertions allow specifying arbitrary auxiliary information);
    - additional parameters stored in the test context for the reporting purposes (see ReportParams below);
    - seed value that can be used to reproduce the failure in a separate test run.
- Large number of helpers that allow to:
  - Write randomized (while still reproducible) tests that allow achiving better coverage for TPMs with arbitrary configuratiuons.
  - Hide the boilerplate code of TPM operations with keys and PCR banks, making the test logic more expressive and easier to understand.
  - Use persistent primary keys to avoid routinely repeated lengthy key creation operations across multiple test cases.
- Extended functionality for debugging TPM simulators.

## Implementing test cases

An application implementing a TPM 2.0 test suite needs to be linked to the Tpm2Tester and [TSS.Net](https://github.com/Microsoft/TSS.MSR/tree/master/TSS.NET) assemblies, and to instantiate a TestSubstrate object, passing the comamnd line and an instance of a class implementing test cases to its factory method. Created substrate object is normally made available to the test case methods, e.g. by storing a reference to it as a static member.

```C#
partial class MyTestCases
{
    static Tpm2Tester.TestSubstrate Substrate;

    static void Main(string[] args)
    {
        Substrate = Tpm2Tester.TestSubstrate.Create(args, new MyTestCases());
        if (Substrate == null)
        {
            // Failed to initialize the test framework (bad command line or no test
            // cases found in MyTestCases)
            return;
        }
        Substrate.RunTestSession();
    }
}
```

A test case is any method qualified with an attribute of type Tpm2Tester.TestAttribute and accepting two parameters of types Tpm2Lib.Tpm2 and Tpm2Tester.TestContext. Test cases should be implemented by the class, an instance of which is passed to the test substrate creation method.

Normally test cases set the category and special requirements in the attributes to reflect the actual TPM functionality required for the test. An example of a package with two test cases:

```C#
using Tpm2Lib;
using Tpm2Tester;

namespace MyTestSuite
{
    partial class MyTestCases
    {
        [Test(Profile.TPM20, Privileges.StandardUser, Category.Misc, Special.None)]
        void MyTestcase1(Tpm2 tpm, TestContext testCtx)
        {
        // . . .
        }
    
        [Test(Profile.TPM20, Privileges.Admin, Category.Startup | Category.Context, Special.PowerControl)]
        void MyTestcase2(Tpm2 tpm, TestContext testCtx)
        {
            // . . .
        }
    }
}
```

### Tpm2 parameter

The first parameter of a test case method is a Tpm2 object representing the TPM 2.0 device to be tested. It is initialized by the test infrastructure based on the command line options.

Besides TPM 2.0 commands proper, it provides several methods extremely useful in writing tests. First, methods for establishing assertions or reaction on expected command outcome:

```C#
Tpm2 _AllowErrors();
Tpm2 _ExpectError(TpmRc errorCode);
Tpm2 _ExpectResponses(params TpmRc[] expectedResponses);
Tpm2 _ExpectMoreResponses(params TpmRc[] expectedResponses);

TpmRc _GetLastResponseCode();
bool _LastCommandSucceeded();
```

Then, methods that emulate various flavors of TPM's hash computing functionality in software:

```C#
void _SetCommandAuditAlgorithm(TpmAlgId auditAlg);
void _SetCommandAuditAlgorithm(TpmAlgId auditAlg, byte[] currentDigest);
Tpm2 _Audit();
TpmHash _GetAuditHash();

Tpm2 _GetCpHash(TpmHash cpHash);
```

And several methods useful in testing TPM simulators or physical TPMs with special harness that allows/emulates access to TPM's platform and/or hardware functionality:

```C#
Tpm2 _AssertPhysicalPresence();
Tpm2 _SetLocality(LocalityAttr locality);
```

### TestContext parameter

The second parameter of a test case method is the test context object. It is mostly used for assertions:
```C#
void Assert(string label, bool success, params Object[] parms);
void AssertEqual<T>(string label, T val1, T val2, params Object[] parms);
void AssertNotEqual<T>(string label, T val1, T val2, params Object[] parms);
```

and to specify additional data that will be included in the report in case one of the TPM commands or assertions fail:
```C#
void ReportParams(params object[] parms);
```

Specialized forms of asssertion AssertEqual and AssertNotEqual are often convenient because in case of failure they will print in the report the guilty values without the necessity of repeating them in the additional parameters list or specifying in the ReportParams() call.


### TestSubstrate object

The test substrate object is usually made available to the test cases as a static member.

It exposes the following two public members: TestCfg and TpmCfg. The former contains hardcoded and configurable (via the command line) parameters that may be used by the individual test cases, and the latter represents the actual TPM device configuration (version info, various size limits, supported commands, algorithms, key sizes and curves, etc.).

It also provides the following groups of helpers:
- Randomness;
- Key creation and loading helpers (internally randomized);
- Persistent primary keys management.

TPM entities (keys, data objects, NV indices) are configured with a multitude of cryptographic (algorithms, schemes, curves, modes, key and hash sizes) and TPM specific (attributes, hierarchies) parameters. This makes writing a test that would check all their possible combinations a practically impossible task (such a test would take way too long to complete). Instead the test substrate provides a rich set of helpers that allow randomly (and safely wrt. to TPM limits and restrictions) select available parameters for TPM objects. When repeated multiple times (yet within available time budget) the same test is picking up different combinations of parameters (because each test run within the same test session is seeded differently).

In case of failure, the test framework includes the value for the '-seed' option that can be used to reproduce the run with the configuration causing the error. An important requirement for the tests to be reproducible is to use only random number generator of the test substrate.

This approach allows using the same test suite for both basic (and quick) integrity checking, and more comprehensive coverage (by running long test sessions periodically). Additionally, in case of using '-randSeed' option, the cumulative coverage of the TPM functionality may be growing across successive test sessions, increasing a chance that a TPM bug triggered by a particular combination of parameters will eventually be discovered.

To simplify the task of test cases randomization the substrate provides the following helpers.

Basic random helpers:
```C#
byte[] RandomBytes(int numBytes);
byte[] RandBytes(int minBytes, int maxBytes);
int RandomInt(int max);
ushort RandomSize(int max, ushort excludedSize = 0);
TpmHandle RandomNvHandle(TpmHandle exclude = null);
byte[] RandomAuth(TpmAlgId associatedHash = TpmAlgId.None, int minSize = 1);
public byte[] RandomNonce(TpmAlgId associatedHash = TpmAlgId.None);
public byte[] RandomBlob(TpmAlgId associatedAlg = TpmAlgId.None);
```

Generic random selection:
```C#
public E Random<E>(IEnumerable<E> coll);
public E Random<E>(IEnumerable<E> coll, E valueIfEmpty);
```

Random selection from what is implemented by the given TPM device:
```C#
EccCurve RandomCurve(TpmAlgId scheme = TpmAlgId.Null, bool swCompat = false);
ushort RandomRsaKeySize (TpmAlgId nameAlg);
TpmAlgId RandomHashAlg(int digestSize);
TpmAlgId[] TwoRandomHashAlgs(int digestSize = 0);
TpmAlgId[] RandomHashAlgs(int numAlgs);
TpmAlgId AltHashAlg(TpmAlgId baseAlg, bool onlyDiffSize = false);
PcrSelection RandomPcrBank();
PcrSelection RandomPcrSel(TpmAlgId hashAlg = TpmAlgId.None, int numPcrs = 0);
EccPoint RandomEccPoint(Tpm2 tpm, EccCurve curveID);
IPublicIdUnion RandomUnique(Tpm2 tpm, TpmPublic pub)
```

Another large group of helpers with multiple overloaded forms is intended for both test randomization, and test code expressiveness improvement. The latter is important because such a basic TPM operation as key or object creation involves a large amount of auxiliary definitions and code, and a few such operations being interwoven in the test logic make the code extremely difficult to read. Hiding the complexity of this boilerplate code behind concise helper prototypes greatly improves test code clarity.


```C#
TpmHandle CreatePrimary(Tpm2 tpm, ...);
TpmHandle CreateRsaPrimary(Tpm2 tpm, ...);
TpmHandle CreateEccPrimary(Tpm2 tpm, ...);
TpmPrivate Create(Tpm2 tpm, ...);
TpmHandle CreateAndLoad(Tpm2 tpm, ...);
TpmHandle CreateSignKey(Tpm2 tpm, ...);
TpmHandle CreateDataObject(Tpm2 tpm, ...);
TpmHandle CreateDataObjectWithPolicy(Tpm2 tpm, TpmHash policy, ...);
```

In a comprehensive test suite many tests will require a parent key as part of their logic. Repeated creation of esentially the same object would introduce a noticeable delay (as RSA key creation may be very slow on some devices), while not adding much to the coverage of the TPM functionality. Therefore the test substrate allows to create and use a persistent primary key for each hierarchy, preserving them during the TPM cleanup or recovery operations it does upon each test case completion. These keys are accessed by the following method:

```C#
TpmHandle LoadRsaPrimary(Tpm2 tpm, TpmRh hierarchy = TpmRh.Owner);
```

Test substrate also provides basic logging methods:

```C#
void WriteToLog(string msgFormat, params object[] msgParams);
void WriteErrorToLog(string msgFormat, params object[] msgParams);
```

and a few other helpers:

```C#
bool ResetTpm(Tpm2 tpm);
bool RestartTpm(Tpm2 tpm);
bool ResumeTpm(Tpm2 tpm);

void DisableKeyCache(Tpm2 tpm);
public void ReactivateKeyCache(Tpm2 tpm);

NvPublic SafeDefineRandomNvIndex (Tpm2 tpm, ushort dataSize = 0, NvAttr attr = TestConfig.DefaultNvAttrs, TpmAlgId nameAlg = TpmAlgId.Null, byte[] auth = null);
bool StressSafeEvictControl(Tpm2 tpm, TpmHandle hObj, TpmHandle hPers, TpmRh hierarchy = TpmRh.Owner)
```



### A complete test sample

Here is an example of an actual test (it does not require any special capabilities, so the Special component of its Test attribute is omitted):

```C#
[Test(Profile.TPM20, Privileges.Admin, Category.Sig | Category.Asym)]
void TestImportSoftwareKeyAndSign(Tpm2 tpm, TestContext testCtx)
{
    TpmAlgId nameAlg = Substrate.Random(TpmCfg.HashAlgs);
    var policy = new PolicyTree(nameAlg);
    policy.SetPolicyRoot(new TpmPolicyCommand(TpmCc.Duplicate));
    // Calculate the expected policy hash
    TpmHash policyDigest = policy.GetPolicyDigest();

    TpmAlgId sigHashAlg = Substrate.Random(TpmCfg.HashAlgs);
    var scheme = new SchemeRsassa(sigHashAlg);
    var inPub = new TpmPublic(nameAlg,
        ObjectAttr.UserWithAuth | ObjectAttr.Sign | ObjectAttr.AdminWithPolicy,
        policyDigest,
        new RsaParms(new SymDefObject(), scheme, Substrate.Random(TpmCfg.RsaKeySizes), 0),
        new Tpm2bPublicKeyRsa());
    AuthValue sigAuthVal = Substrate.RandomAuth(nameAlg);

    testCtx.ReportParams("Software key preparation", inPub, sigAuthVal);

    // Create a software key 
    TssObject swKey = TssObject.Create(inPub, sigAuthVal);

    // Check some expectations regarding the created software key parameters:
    testCtx.AssertEqual("SwKey.Params", swKey.Public.objectAttributes, inPub.objectAttributes);
    testCtx.AssertEqual("SwKey.Params", swKey.Public.parameters, inPub.parameters);
    testCtx.Assert("SwKey.Unique.Non-null", swKey.Public.unique != null);
    testCtx.AssertNotEqual("SwKey.Unique", swKey.Public.unique, inPub.unique);

    // Create unprotected duplication blob (no wrappers)
    TpmPrivate dupBlob = swKey.GetPlaintextDuplicationBlob();

    testCtx.ReportParams("Import to the TPM", swKey.Public);

    TpmHandle hPrim = Substrate.LoadRsaPrimary(tpm);

    // Import the duplication blob to create a TPM key blob bound to this TPM and the given parent key
    TpmPrivate importedKeyBlob = tpm.Import(hPrim, null, swKey.Public,
                                            dupBlob, null, new SymDefObject());

    // Load the new key blob into the TPM ...
    TpmHandle hKey = tpm.Load(hPrim, importedKeyBlob, swKey.Public)
                        .SetAuth(sigAuthVal);

    testCtx.ReportParams("Signing and verification");

    // ... and use it to sign something to check that the imported key is OK
    TpmHash toSign = TpmHash.FromRandom(sigHashAlg);
    var proofx = new TkHashcheck(TpmRh.Null, null);
    ISignatureUnion sig = tpm.Sign(hKey, toSign, scheme, proofx);
    bool sigOk = swKey.Public.VerifySignatureOverHash(toSign, sig);
    testCtx.Assert("PlainImportExport.Sign", sigOk);

    // Clean up
    tpm.FlushContext(hKey);

    // Verify that the public portion of a key won't sign
    hKey = tpm.LoadExternal(null, swKey.Public, TpmRh.Owner);
    sig = tpm._ExpectError(TpmRc.AuthUnavailable)
             .Sign(hKey, toSign, scheme, proofx);
    tpm.FlushContext(hKey);
} // TestSign

```


## Test session execution modes

After the framework defines a subset of test cases to run, it executes them in one of the three primary modes:
- Normal, or sequential: tests are executed one after another (default mode).
- Stress, or parallel: thread safe tests are executed in concurrent threads ('-stress' option).
- Fuzzing: command buffers generated by the normal test logic are modified using a variety of techniques that ensure deep penetration in the TPM parameter validation logic ('-fuzz' option).

Additionally it is possible to:
- Run the test session repetitively for the given time ('-mins' option). This allows achieve better coverage when test cases select parameters of TPM commands randomly.
- Randomly reorder test cases ('-shuffle' option).
- Randomly seed the test session ('-randSeed' option).
- For TPM simulators it is possible to introduce NV unavailability events ('debugNv') or inject random "sleep" events ('S3' option).
- Seed the test session with a specific seed ('-seed' option). This is useful for reproducing a failed test.

## Command line

```
TestSuiteApp [-opt1 [param1]] [-opt2 [param2]] [...] [TestProfileAndTestCaseList]
```

All components of the command line are case-insensitive, and may be specified in arbitrary order, except that if an option required a value, the value should immediately follow the option name and be separated from it with a space. Option names are prefixed with either "-" (dash) or "/" (slash), and the list of test profiles and test cases can be space or comma separated.

The list of test profiles and test cases defines the subset of defined test case methods to be included into the test session. This subset may be further filtered based on other command line options and TPM device type and capabilities.

Test profile names are case-insensitive names of enumerators from the Tpm2Lib.Category enumeration ("startup", "nv", "pcr", etc.). 

Test names are case-insensitive names ot test case methods (methods qualified with the Tpm2Lib.TestAttribute).

A test or test profile name may be prefixed with an exclusion modifier '!' or '~' (similar to C logical and bitwise negation operators) to exclude it from execution. Also, a dash before test or test profile name is ignored.

If no profiles or test names are specified for inclusion, then all test cases not marked with Category.Slow are selected (and then filtered in accordance with the command line options and exclusions, if any).


Help options:
- ?, help - Print the usage help
- help - Print the usage help
- tests - List all test and test profile names
- profiles - List all test and test profile names

Target device specification options:
  device - Target TPM device type (tcp, tbs, tbsraw, dll, rmsim)
  address - HostName:Port of simulator or TPM proxy (for 'tcp' TPM device type)
  tpm - Path to a TPM simulator executable to be started by the test suite (for 'tcp' TPM device type)
  restart - Restart TPM if it dies or becomes unresponsive (with -tpm and -fuzz)
  dllpath - Path to a TPM simulator DLL (for 'dll' TPM device type; also may be used by tests directly)
  noinit - Do not initialize TPM and do not clear NV memory
  stopTpm - Signal TPM simulator to stop upon test session completion

Test session mode options:
  mins - Set duration of the test session in minutes
  fast - Make TPM simulator use RSA key caching (speeds up tests)
  stress - Stress mode - tests are executed in parallel (see -threads)
  shuffle - Shuffle the order of individual test cases (default for stress mode)
  debugNv - For each command requiring NV attempt to execute it with NV disabled (TPM simulator only)
  S3 - Include random S3 events (only 'rmsim' device type)

Test and test session parameters:
  seed - Set seed for RNG used by the test substrate
  randSeed - Randomly seed the test session (otherwise the substrate uses the same seed every run)
  threads - Set number of threads (with '-stress')
  stdUser - Running as a standard user
  params - Arbitrary string w/o spaces to be passed to a single test specified with '-tests' option
  daTime - Overrides default DA recovery time (3 sec) used by tests
  validate - Validate that tests obey threading and other rules
  bleeding - Include tests that are known to cause problems with released TPM versions

Test selection options:
  exclude - Exclude the following (until the end) tests from the run (alternatively use '!' or '~' before test or profile name)
  continue      Restart the last test run at the non-completed test

Reporting options:
  nohtml - Do not show HTML results
  quiet - Minimal output
  silent - No output to console except for errors and warnings
  dumpConsole - Dump console output to console.txt
  dumpIo - Output TPM commands and responses to tpm_io.txt
  crashReports  A directory to receive fuzz crash reports
  checkin       Notify of tester startup/shutdown with a file in crashreports
  log - Log IO between TPM and tester to files in the given directory
  dbg - Enable debug output
  
Fuzzing options:
  fuzz - Fuzz the TPM
  fuzzCmd - Fuzz only command with this name (no 'TMP2_' prefix)
  fuzzCount - Debug break after this number of fuzzings. Used with -fuzzcmd or -breakcount.
  breakCmd - Command, to which -fuzzcount applies
  breakCount - Number of fuzzed commands to skip before -fuzzcount applies

Miscelaneous options:
  parseIn       Interpret byte-string as TPM command
  parseOut      Interpret byte-string as a TPM response
  tpmInfo       Dump TPM information and exit

## Sources structure

This folder contains three projects:
- TestSubstrate: the testing framework proper;
- TestSuite: an example of a TPM 2.0 test suite built on top of Tpm2Tester framework;
- TpmProxy: an application that works as a TCP/IP server that exposes access to a locally available TPM 2.0 device via the [TPM 2.0 simulator](https://github.com/Microsoft/ms-tpm-20-ref/tree/master/TPMCmd/Simulator) protocol.

TestSubstrate and TestSuite projects depend on the [TSS.Net](https://github.com/Microsoft/TSS.MSR/tree/master/TSS.NET) project from this repo.

## System Requirements
Tpm2Tester is a cross-platform .Net Standard library and requires Visual Studio 2015 Update 3 to build.

## Questions and Feedback
Please send questions and feedback to tssdotnet@microsoft.com.

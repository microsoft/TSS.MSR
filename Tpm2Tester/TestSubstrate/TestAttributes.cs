/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;

namespace Tpm2Tester
{
    // Device profile (TPM20 or MinTPM)
    [Flags]
    public enum Profile
    {
        None = 0,

        // MinTPM command subset
        MinTPM = 0x1, 
        // Full TPM2.0 command set
        TPM20 = 0x2,
        // Only available in debug / instrumented TPM builds
        Debug = 0x40,
        // Test is disabled
        Disabled = 0x80

    }

    // Privileges needed when running on Windows (StandardUser, Admin, Special).  This is only 
    // defined for MinTPM.  Special means that it is needed/implemented in MinTPM but is not accessible 
    // through TBS
    [Flags]
    public enum Privileges
    {
        None = 0,
        // Test can execute as standard user or admin
        StandardUser = 0x1,
        // Test can only execute as admin
        Admin = 0x2,
        // Blocked by TBS but defined by MinTPM
        Special = 0x4,
    }

    // Special Attributes affect how tests are run, and whether a test will run on a particular device
    // NotThreadSafe, TpmCfg.PowerControl
    // Which auth values are needed (OnwerAuth, PrivacyAuth, PlatformAuth, LocoutAuth)
    // The major subsystem tested (NV, Crypto, etc.)
    [Flags]
    public enum Special
    {
        None = 0, 

        // Command changes global TPM state and cannot be run in parallel with other tests
        NotThreadSafe = 0x01, 

        // Test needs to PowerCycle() the TPM
        PowerControl = 0x02,
        
        // Test needs to assert physical-presence
        PhysicalPresence = 0x04,

        // Test needs an underlying resource manager (for instance it uses lots of slots)
        NeedsTpmResourceMgr = 0x08,

        // Test needs TPM locality support
        Locality = 0x10,

        // The test does not work in fuzz-mode
        NotFuzzSafe = 0x20,

        // Can be run only in the absence of TRM (TPM Resource Management), i.e.
        // when OS' TBS is in raw mode
        NoTRM = 0x40,

        // Test does not work with -debugnv (because time does not advance when NV-unavail)
        NotDebugNvSafe = 0x80,

        // Run this test last
        RunAtEnd = 0x100,

        // Full access to platform functionality required (including platform hierarchy,
        // machine reset/restart/resume, locality and PPI).
        Platform = 0x200,

        // Lockout auth required.
        Lockout = 0x400,

        // Test needs to be able to turn TPM NV on/off
        NvControl = 0x800
    } // enum Special

    // A category reflects a TPM subsystem or functionality subset targeted by the test
    [Flags]
    public enum Category : uint
    {
        None = 0,

        Startup = 0x1, 
        NV = 0x2,
        PCR = 0x4,
        Clock = 0x8,

        Hierarchy = 0x10,
        Context = 0x20,
        Object = 0x40,
        // = 0x80,

        Hash = 0x100,
        Mac = 0x200,
        Hmac = Mac,
        Seq = 0x400,
        Sym = 0x800,
        Symmetric = Sym,
        
        Rsa = 0x1000,
        Ecc = 0x2000,
        Asym = 0x4000,
        Asymmetric = Asym,
        Sig = 0x8000,
        Signature = Sig,

        Attest = 0x10000,
        Attestation = Attest,
        Dup = 0x20000,
        Duplication = Dup,
        Ticket = 0x40000,
        // = 0x80000,

        Session = 0x100000,
        Policy = 0x200000,
        Audit = 0x400000,
        DA = 0x800000,

        Coverage = 0x01000000,      // tests using invalid input and corner case attributes combinations
        Regression = 0x02000000,    // tests that trigger bugs in un-patched TPM implementation
        CriticalPatch = 0x04000000, // tests verifying critical patches
        Misc = 0x08000000,          // TPM capabilities not fitting other defined categories

        Slow = 0x10000000,          // The test takes a long time (> 1 min) to complete
        Infra = 0x20000000,         // tests used to test Tpm2Tester infrastructure
        Hidden = 0x40000000,        // Test can only be run by explicitly specifying its name on the command line
        WLK = 0x80000000
    }

    public class TestAttribute : Attribute
    {
        public Profile CommProfile;
        public Privileges Privileges;
        public Special SpecialNeeds;
        public Category Category;

        public TestAttribute(Profile prof, Privileges priv, Category mainCategory, 
                               Special extraNeeds = Special.None)
        {
            CommProfile = prof;
            Privileges = priv;
            SpecialNeeds = extraNeeds;
            Category = mainCategory;
        }

    }
}

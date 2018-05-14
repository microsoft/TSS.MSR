/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System.Diagnostics;
using System.Collections.Generic;
using Tpm2Lib;

namespace Tpm2Tester
{
    // Validates test categorization (privileges, thread safety, MinTPM-profile,
    // and NeedsReboot from observing behavior and tables contained herein
    internal class TestCategorizer
    {
        static bool Initialized = false;
        // Indicates whether the command mutates global state.  The assumption is that
        // if an session, object NV slot, etc. is "owned" by a thread then that is safe.
        // Conversely global resources (PCR, the NV-array, owner auth, clock, etc. are
        // shared state. Modification of shared state is not thread safe. For the
        // purposes of the testCtx, use of shared state IS thread safe.
        static CommandThreadSafety[] ThreadInfo =
        {
            new CommandThreadSafety(TpmCc.EvictControl, false),
            new CommandThreadSafety(TpmCc.HierarchyControl, false),
            new CommandThreadSafety(TpmCc.NvUndefineSpace, false),
            new CommandThreadSafety(TpmCc.ChangeEPS, false),
            new CommandThreadSafety(TpmCc.ChangePPS, false),
            new CommandThreadSafety(TpmCc.Clear, false),
            new CommandThreadSafety(TpmCc.ClearControl, false),
            new CommandThreadSafety(TpmCc.ClockSet, false),
            new CommandThreadSafety(TpmCc.HierarchyChangeAuth, false),
            new CommandThreadSafety(TpmCc.NvDefineSpace, false),
            new CommandThreadSafety(TpmCc.PcrAllocate, false),
            new CommandThreadSafety(TpmCc.PcrSetAuthPolicy, false),
            new CommandThreadSafety(TpmCc.SetPrimaryPolicy, false),
            new CommandThreadSafety(TpmCc.FieldUpgradeStart, false),
            new CommandThreadSafety(TpmCc.ClockRateAdjust, false),
            new CommandThreadSafety(TpmCc.CreatePrimary, true),
            new CommandThreadSafety(TpmCc.NvGlobalWriteLock, false),
            new CommandThreadSafety(TpmCc.GetCommandAuditDigest, true),
            new CommandThreadSafety(TpmCc.NvIncrement, true),
            new CommandThreadSafety(TpmCc.NvSetBits, true),
            new CommandThreadSafety(TpmCc.NvExtend, true),
            new CommandThreadSafety(TpmCc.NvWrite, true),
            new CommandThreadSafety(TpmCc.NvWriteLock, true),
            new CommandThreadSafety(TpmCc.DictionaryAttackLockReset, false),
            new CommandThreadSafety(TpmCc.DictionaryAttackParameters, false),
            new CommandThreadSafety(TpmCc.NvChangeAuth, true),
            new CommandThreadSafety(TpmCc.PcrEvent, false),
            new CommandThreadSafety(TpmCc.PcrReset, false),
            new CommandThreadSafety(TpmCc.SequenceComplete, true),
            new CommandThreadSafety(TpmCc.SetAlgorithmSet , false),
            new CommandThreadSafety(TpmCc.SetCommandCodeAuditStatus, false),
            new CommandThreadSafety(TpmCc.FieldUpgradeData, false),
            new CommandThreadSafety(TpmCc.IncrementalSelfTest, false),
            new CommandThreadSafety(TpmCc.SelfTest, false),
            new CommandThreadSafety(TpmCc.Startup, false),
            new CommandThreadSafety(TpmCc.Shutdown, false),
            new CommandThreadSafety(TpmCc.StirRandom, true),
            new CommandThreadSafety(TpmCc.ActivateCredential, true),
            new CommandThreadSafety(TpmCc.Certify, true),
            new CommandThreadSafety(TpmCc.PolicyNV, true),
            new CommandThreadSafety(TpmCc.CertifyCreation, true),
            new CommandThreadSafety(TpmCc.Duplicate, true),
            new CommandThreadSafety(TpmCc.GetTime, true),
            new CommandThreadSafety(TpmCc.GetSessionAuditDigest, true),
            new CommandThreadSafety(TpmCc.NvRead, true),
            new CommandThreadSafety(TpmCc.NvReadLock, true),
            new CommandThreadSafety(TpmCc.ObjectChangeAuth, true),
            new CommandThreadSafety(TpmCc.PolicySecret, true),
            new CommandThreadSafety(TpmCc.Rewrap, true),
            new CommandThreadSafety(TpmCc.Create, true),
            new CommandThreadSafety(TpmCc.EcdhZGen, true),
            new CommandThreadSafety(TpmCc.Hmac, true),
            new CommandThreadSafety(TpmCc.Import, true),
            new CommandThreadSafety(TpmCc.Load, true),
            new CommandThreadSafety(TpmCc.Quote, true),
            new CommandThreadSafety(TpmCc.RsaDecrypt, true),
            new CommandThreadSafety(TpmCc.HmacStart, true),
            new CommandThreadSafety(TpmCc.SequenceUpdate, true),
            new CommandThreadSafety(TpmCc.Sign, true),
            new CommandThreadSafety(TpmCc.Unseal, true),
            new CommandThreadSafety(TpmCc.PolicySigned, true),
            new CommandThreadSafety(TpmCc.ContextLoad, false),   // because they won't run on an RM 
            new CommandThreadSafety(TpmCc.ContextSave, false),  // because they won't run on an RM
            new CommandThreadSafety(TpmCc.EcdhKeyGen, true),
            new CommandThreadSafety(TpmCc.EncryptDecrypt, true),
            new CommandThreadSafety(TpmCc.FlushContext, true),
            new CommandThreadSafety(TpmCc.LoadExternal, true),
            new CommandThreadSafety(TpmCc.MakeCredential, true),
            new CommandThreadSafety(TpmCc.NvReadPublic, true),
            new CommandThreadSafety(TpmCc.PolicyAuthorize, true),
            new CommandThreadSafety(TpmCc.PolicyAuthValue, true),
            new CommandThreadSafety(TpmCc.PolicyCommandCode, true),
            new CommandThreadSafety(TpmCc.PolicyCounterTimer, true),
            new CommandThreadSafety(TpmCc.PolicyCpHash, true),
            new CommandThreadSafety(TpmCc.PolicyLocality, true),
            new CommandThreadSafety(TpmCc.PolicyNameHash, true),
            new CommandThreadSafety(TpmCc.PolicyOR, true),
            new CommandThreadSafety(TpmCc.PolicyTicket, true),
            new CommandThreadSafety(TpmCc.ReadPublic, true),
            new CommandThreadSafety(TpmCc.RsaEncrypt, true),
            new CommandThreadSafety(TpmCc.StartAuthSession, true),
            new CommandThreadSafety(TpmCc.VerifySignature, true),
            new CommandThreadSafety(TpmCc.EccParameters, true),
            new CommandThreadSafety(TpmCc.FirmwareRead, false),
            new CommandThreadSafety(TpmCc.GetCapability, true),
            new CommandThreadSafety(TpmCc.GetRandom, true),
            new CommandThreadSafety(TpmCc.GetTestResult, true),
            new CommandThreadSafety(TpmCc.Hash, true),
            new CommandThreadSafety(TpmCc.PcrRead, true),
            new CommandThreadSafety(TpmCc.PolicyPCR, true),
            new CommandThreadSafety(TpmCc.PolicyRestart, true),
            new CommandThreadSafety(TpmCc.ReadClock, true),
            new CommandThreadSafety(TpmCc.PcrExtend, false),
            new CommandThreadSafety(TpmCc.PcrSetAuthValue, false),
            new CommandThreadSafety(TpmCc.NvCertify, true),
            new CommandThreadSafety(TpmCc.EventSequenceComplete, false),
            new CommandThreadSafety(TpmCc.HashSequenceStart, true),
            new CommandThreadSafety(TpmCc.PolicyPhysicalPresence, true),
            new CommandThreadSafety(TpmCc.PolicyDuplicationSelect, true),
            new CommandThreadSafety(TpmCc.PolicyGetDigest, true),
            new CommandThreadSafety(TpmCc.TestParms, true), 
//            new CommandThreadSafety(TpmCc.ec.EcdaaCertify, true), 
            new CommandThreadSafety(TpmCc.PolicyPassword, true)
        };

        // DB of Windows privileges necessary to execute command
        static MinTpmNecessaryPrivileges[] NecessaryPrivileges =
        {
            //                                      admin, standardUser, okInLockout
            new MinTpmNecessaryPrivileges(TpmCc.Startup, false, false, true),
            new MinTpmNecessaryPrivileges(TpmCc.Shutdown, false, false, true),
            new MinTpmNecessaryPrivileges(TpmCc.SelfTest, true, false, true),
            new MinTpmNecessaryPrivileges(TpmCc.GetTestResult, true, false, true),
            new MinTpmNecessaryPrivileges(TpmCc.StartAuthSession, true, true, true),
            new MinTpmNecessaryPrivileges(TpmCc.PolicyRestart, true, false, true),
            new MinTpmNecessaryPrivileges(TpmCc.Create, true, true, false),
            new MinTpmNecessaryPrivileges(TpmCc.Load, true, true, false),
            new MinTpmNecessaryPrivileges(TpmCc.ReadPublic, true, true, true),
            new MinTpmNecessaryPrivileges(TpmCc.ActivateCredential, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.Unseal, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.ObjectChangeAuth, true, true, false),
            new MinTpmNecessaryPrivileges(TpmCc.Duplicate, true, true, false),
            new MinTpmNecessaryPrivileges(TpmCc.Import, true, true, false),
            new MinTpmNecessaryPrivileges(TpmCc.Sign, true, true, false),
            new MinTpmNecessaryPrivileges(TpmCc.RsaDecrypt, true, true, false),
            new MinTpmNecessaryPrivileges(TpmCc.Hash, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.GetRandom, true, true, true),
            new MinTpmNecessaryPrivileges(TpmCc.StirRandom, true, true, true),
            new MinTpmNecessaryPrivileges(TpmCc.Certify, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.CertifyCreation, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.Quote, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.PcrEvent, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.PcrRead, true, false, true),
            new MinTpmNecessaryPrivileges(TpmCc.PcrReset, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.PcrExtend, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.PolicySecret, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.PolicyOR, true, false, true),
            new MinTpmNecessaryPrivileges(TpmCc.PolicyPCR, true, false, true),
            new MinTpmNecessaryPrivileges(TpmCc.PolicyCommandCode, true, false, true),
            new MinTpmNecessaryPrivileges(TpmCc.PolicyAuthValue, true, true, true),
            new MinTpmNecessaryPrivileges(TpmCc.CreatePrimary, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.HierarchyControl,true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.Clear, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.ClearControl, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.HierarchyChangeAuth, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.DictionaryAttackLockReset, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.DictionaryAttackParameters, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.ContextSave, false , false, true),
            new MinTpmNecessaryPrivileges(TpmCc.ContextLoad, false , false, true),
            new MinTpmNecessaryPrivileges(TpmCc.FlushContext, true, true, true),
            new MinTpmNecessaryPrivileges(TpmCc.EvictControl, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.ReadClock, true, false, true),
            new MinTpmNecessaryPrivileges(TpmCc.ClockSet, false, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.GetCapability, true, true, true),
            new MinTpmNecessaryPrivileges(TpmCc.NvDefineSpace, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.NvUndefineSpace, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.NvReadPublic, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.NvWrite, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.NvIncrement, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.NvRead, true, false, false),
            new MinTpmNecessaryPrivileges(TpmCc.NvChangeAuth, true, false, false                                )
        };

        internal static void Init()
        {
            //
            // Allow Init to be called multiple times for TAEF support
            if (TestCategorizer.Initialized == false)
            {
                Debug.Assert(Privileges.Count == 0);
                foreach (MinTpmNecessaryPrivileges priv in NecessaryPrivileges)
                {
                    Privileges.Add(priv.CommandCode, priv);
                }
                foreach (CommandThreadSafety safe in ThreadInfo)
                {
                    ThreadSafety.Add(safe.CommandCode, safe.ThreadSafe);
                }
                TestCategorizer.Initialized = true;
            }
        }

        // Determines whether the command is in the minimal (MinTPM) profile
        internal static bool InProfile0(TpmCc code)
        {
            return Privileges.ContainsKey(code);
        }
        internal static bool GetThreadSafety(TpmCc command)
        {
            return ThreadSafety[command];
        }

        // Does the command mutate global state?
        internal static bool CommandDefined(TpmCc command)
        {
            return ThreadSafety.ContainsKey(command);
        }

        // Return true if the command available for MinTpm
        internal static bool CommandDefinedForMinTpm(TpmCc command)
        {
            foreach (MinTpmNecessaryPrivileges c in NecessaryPrivileges)
            {
                if (c.CommandCode.Equals(command) == true)
                    return true;
            }
            return false;
        }

        // Get expected privileges to execute command
        internal static NecessaryPrivilege GetNecessaryPrivileges(TpmCc code)
        {
            MinTpmNecessaryPrivileges priv = Privileges[code];
            if (priv.StandardUserAccessible) return NecessaryPrivilege.User;
            if (priv.AdminAccessible) return NecessaryPrivilege.Admin;
            return NecessaryPrivilege.Special;
        }

        static Dictionary<TpmCc, MinTpmNecessaryPrivileges> Privileges =
                                new Dictionary<TpmCc, MinTpmNecessaryPrivileges>();
        static Dictionary<TpmCc, bool> ThreadSafety = new Dictionary<TpmCc, bool>();
    }

    // Indicates OS-level privileges necessary to execute the command.
    internal enum NecessaryPrivilege
    {
        User = 1,
        Admin = 2,

        // Command is not accessible when OS is running, but is needed for Profile0
        Special = 3,

        // Command is only accessible on instrumented TPMs
        Debug = 4
    }

    internal class CommandThreadSafety
    {
        internal CommandThreadSafety(TpmCc code, bool safe)
        {
            CommandCode = code;
            ThreadSafe = safe;
        }
        internal TpmCc CommandCode;
        internal bool ThreadSafe;
    }
    internal class MinTpmNecessaryPrivileges
    {
        internal MinTpmNecessaryPrivileges(TpmCc code,
                                        bool admin, bool stdUser, bool OkInLockout)
        {
            CommandCode = code;
            AdminAccessible = admin;
            StandardUserAccessible = stdUser;
            AllowedInLockout = OkInLockout;
        }
        internal TpmCc CommandCode;
        internal bool AdminAccessible;
        internal bool StandardUserAccessible;
        internal bool AllowedInLockout;
    }

}

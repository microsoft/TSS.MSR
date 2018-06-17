/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.Linq;
using System.IO;
using System.Collections;
using System.Collections.Generic;
using System.Reflection;
using System.Threading;
using System.Diagnostics;
using System.Text;
using Tpm2Lib;

namespace Tpm2Tester
{
    // We keep track of what we have fuzzed so that we have good
    // coverage (otherwise the tester might test FlushSpecific, etc.
    // more than is useful
    internal class FuzzStats
    {
        internal FuzzStats(CommandInfo info)
        {
            cmdInfo = info;
            cmdComplexity = 0;

            fuzzTime = 0;
            fuzzSeries = fuzzBypassed = tgtFuzzes = rawFuzzes = 0;
            succeeded = failed = authFailed = lockedOut =
                handlesFuzzed = enumsFuzzed = buffersFuzzed = valuesFuzzed =
                nullLeafsResurrected = nullStructsResurrected = 0;
        }

        internal void Combine(FuzzStats s)
        {
            fuzzTime += s.fuzzTime;
            fuzzSeries += s.fuzzSeries;
            tgtFuzzes += s.tgtFuzzes;
            rawFuzzes += s.rawFuzzes;
            fuzzBypassed += s.fuzzBypassed;
            succeeded += s.succeeded;
            failed += s.failed;
            authFailed += s.authFailed;
            lockedOut += s.lockedOut;
            handlesFuzzed += s.handlesFuzzed;
            enumsFuzzed += s.enumsFuzzed;
            buffersFuzzed += s.buffersFuzzed;
            valuesFuzzed += s.valuesFuzzed;
            nullLeafsResurrected += s.nullLeafsResurrected;
            nullStructsResurrected += s.nullStructsResurrected;
        }

        // Command's TSS.Net metadata
        internal CommandInfo cmdInfo;

        internal int cmdComplexity;

        // Time spent fuzzing this command in seconds.
        internal double fuzzTime;

        // Number of fuzzing series for this command
        internal uint fuzzSeries;

        // Number of times this command was executed in normal mode (without fuzzing)
        internal uint fuzzBypassed;

        // Number of targeted fuzzes (preserve formal integrity of the command parameters)
        internal uint tgtFuzzes;

        // Number of raw (whole command buffer) and blind (parameters buffer) fuzzes
        internal uint rawFuzzes;

        // Number of times the fuzzed command was successfully executed by TPM
        internal ulong succeeded;

        // Number of times the fuzzed command was failed by the TPM
        internal ulong failed;

        // Number of times the fuzzed command failed because of authorization failure
        internal ulong authFailed;

        // Number of times the fuzzed command failed because the TPM was in lockout
        internal ulong lockedOut;

        //
        // Statistics about fuzzed parameter types
        //
        internal ulong handlesFuzzed;
        internal ulong enumsFuzzed;
        internal ulong buffersFuzzed;
        internal ulong valuesFuzzed;
        internal ulong nullStructsResurrected;
        internal ulong nullLeafsResurrected;
    } // class FuzzStats

    internal class FuzzableMember : Tuple<MemberInfo, object, int>
    {
        internal FuzzableMember(MemberInfo i1, object i2, int i3) : base(i1, i2, i3) {}
    }

    internal partial class TestFramework
    {
        // When in fuzz mode, the tester runs tests as usual, but at some point
        // picks a command and starts a fuzzing series, repeatedly mangling the
        // original command buffer formed by the test and sending it to the TPM.
        //
        // Parameters MaxCommandFuzzTime and MaxFuzzCount determine the maximal
        // duration of single fuzzing series, after which the test continues its
        // execution (with the TPM being in the state resulted from the execution
        // of last fuzzed command instance).
        //
        // Note that the fuzzing series may be aborted earlier in accordance with
        // the fuzzer logic (e.g. it favours fuzzing commands with more complex
        // parameters).
        internal bool FuzzMode = false;

        // Maximal duration of a single fuzzing series.
        readonly TimeSpan MaxCommandFuzzTime = new TimeSpan(0, 0, 20);

        // Degree of the fuzzing series progress before it can be terminated early
        const double FuzzEarlyQuitLimit = 0.75;

        // Maximal number of iterations in a single fuzzing series.
        const uint MaxFuzzCount = 5000;

        // Probability of selecting a test for fuzzing.
        // During fuzz session some tests are allowed to execute unfuzzed with error
        // reporting enabled to verify that internal TPM state is still consistent.
        const double TestFuzzProbability = 0.85;

        //
        // Probability boundaries of selecting a command for fuzzing
        //
        const double MinFuzzProbability = 0.02;
        const double MaxFuzzProbability = 0.80;
        const double BaseFuzzProbability = 0.2;

        //
        // Probabilities of a basic fuzzing types
        //
        const double RawFuzzProbability = 0.12;
        const double RawParamFuzzProbability = 0.5; // relative to RawFuzzProbability
        const double SecondaryRawFuzzProbability = 0.03;

        //
        // Comamnd line options
        //

        // Command for exclusive fuzzing.
        // Specified by the '-fuzzCmd' option.
        internal string FuzzCmd = null;

        // Number of fuzzings of the current command, after which a debug break is
        // generated. This allows then to step through the TPM simulator command
        // processing using the fuzzed command buffer that triggers the undesirable
        // behavior.
        // This value is set by the '-fuzzCount' command line option. It should be
        // used together with either of BreakCmd or BreakCount values that select
        // the command, to which the FuzzCountToBreak value is applied.
        // Currently the only completely reproducible test run is the first one after
        // Tpm2Tester starts. This is when this option should be used.
        internal int FuzzCountToBreak = 0;

        // Defines the command, to which the FuzzCountToBreak setting is applied.
        // This value is set by the '-breakCmd' command line option.
        internal string BreakCmd = null;

        // Defines the command, to which the FuzzCountToBreak setting is applied.
        // This value is set by the '-breakCount' command line option.
        internal int BreakCount = 0;

        //
        // Fuzzer state
        //

        // Indicates that a fuzzing series is in progress
        bool IsFuzzing = false;

        // Command being currently fuzzed
        TpmCc CurFuzzCmd = TpmCc.None;

        // Current fuzzing run statistics
        FuzzStats CurFuzzStats = null;

        TpmCc LastFuzzedCommand = TpmCc.None;

        // Probability of starting a fuzzing series for the current command
        double Threshold = 1.0;

        DateTime CurFuzzEndTime;
        DateTime NextProgressReportTime;

        // Set when Tpm2Tester begins next test method execution
        //bool NewTest = false;

        //
        // Cumulative fuzzing statistics
        //

        Dictionary<TpmCc, FuzzStats> FuzzedCommands;
        Dictionary<Type, object> ValidStructs;
        double AverageCmdComplexity = 0;
        double AverageFuzzSeriesPerCmd = 0;
        ulong TotalFuzzSeries;
        ulong TotalFuzzBypasses;

        //
        // Scratch vars
        //

        internal byte[] LastFuzzedCommandBuffer;

        bool CurFuzzingSeriesHadSuccesses = false;
        bool CurFuzzingSeriesHadFailures = false;

        bool SkipRawFuzzer = false;


        internal void SimpleFuzzer(byte[] x)
        {
            // only fuzz a fraction of inputs
            double fuzzProb = 0.1;
            double coinFlip = Globs.GetRandomDouble();
            if (coinFlip > fuzzProb)
                return;
            
            int fuzzType = Globs.GetRandomInt(2);
            switch (fuzzType)
            {
                case 0:
                    // flip a bit
                    byte bitToFlip = (byte)(1 << Globs.GetRandomInt(8));
                    int byteToFlip = Globs.GetRandomInt(x.Length);
                    x[byteToFlip] ^= bitToFlip;
                    return;
                case 1:
                    // shorten or lengthen the command by adding random data at the
                    //  breakpoint or deleting bytes at the breakpoint 
                    int length = x.Length;
                    int newLength = Globs.GetRandomInt(length * 2);
                    int breakPoint = Globs.GetRandomInt(Math.Min(length, newLength));
                    int missing = length - newLength;
                    byte[] t = new byte[newLength];
                    // make a new buffer and copy in the old buf adding new random bytes
                    // or deleting bytes at the breakpoint
                    if (missing >= 0)
                    {
                        // shorter.  Leave out a bit of the original array at breakpoint
                        Array.Copy(x, t, breakPoint);
                        Array.Copy(x, breakPoint + missing, t, breakPoint,
                                   length - breakPoint - missing);
                    }
                    else
                    {
                        // longer.  Copy frags and fill gap with random
                        int extraSize = -missing;
                        Array.Copy(x, t, breakPoint);
                        Array.Copy(x, breakPoint, t, breakPoint + extraSize,
                                   length - breakPoint);
                        Array.Copy(Substrate.RandomBytes(extraSize), 0, t, breakPoint, extraSize);
                    }
                    x = t;
                    return;

                default:
                    throw new NotImplementedException();
            }
        }

        // Start a fuzz run on the current test set for the specified duration.
        // (Currently only supported on TCP TPM)
        // A stress run expects "working" tests.  The tester picks a test then runs
        // it up to a TPM command (which test and which command within the test will
        // be refined to give good coverage.)  This sets internal TPM state to be
        // something "useful."
        // At this point the tester enters a loop where it fuzzes the "good" command
        // input to try to drive the TPM into a failure.
        internal bool RunFuzz(string[] testNames, DateTime endTime)
        {
            bool runOnce = endTime == DateTime.MinValue;
            List<MethodInfo> testsToRun = MethodInfoFromNames(testNames.ToList());
            if (testsToRun.Count == 0)
                return false;
            
            // these stats help the tester do good coverage
            AverageCmdComplexity = AverageFuzzSeriesPerCmd = 0.0;
            TotalFuzzSeries = TotalFuzzBypasses = 0;
            FuzzedCommands = new Dictionary<TpmCc, FuzzStats>();
            ValidStructs = new Dictionary<Type, object>();

            bool origMode = Tpm2._TssBehavior.Passthrough;
            Tpm2._TssBehavior.Passthrough = true;

            do {
                // No fuzzing while we are trying to get the TPM ready to accept commands
                MainTpm._SetCommandCallbacks(null);
                
                if (!RecoverTpm(MainTpm))
                {
                    // do everything we can to reset the TPM to a pristine state
                    bool tpmReady = PlatformCleanTpm(MainTpm);
                    if (!tpmReady && !MainTestContext.TpmSeemsToBeDead)
                    {
                        WriteToLog("Failed to prepare the TPM for the next test phase. " +
                                   "Aborting");
                        Tpm2._TssBehavior.Passthrough = origMode;
                        return false;
                    }
                }

                MethodInfo test = Substrate.Random(testsToRun);
#if !TSS_NO_TCP
                TheTransportLogger.NotifyTestStart(test.Name);
#endif
                // We are not fuzzing until the callback decides to start.
                IsFuzzing = false;

                // Initial unadjusted command fuzzing probability
                Threshold = 1.0;

                CurFuzzCmd = TpmCc.None;

                // Report test failuers as usually until the first fuzzed command 
                MainTestContext.ReportErrors = true;

                if (Globs.GetRandomDouble() < TestFuzzProbability)
                {
                    // Install fuzz callback
                    MainTpm._SetCommandCallbacks(FuzzStatsCallback, TransformerCallback,
                                                 RawFuzzCallback);
                }

                // Run the test. It is likely that during execution of the test a
                // command will be selected to be fuzzed (by the logic in
                // TransformerCallback). The test might complete  without issues, or
                // it might result in a TPM crash.
                RunTest(test, MainTpm, MainTestContext);

                // Uninstall fuzzing callbacks
                MainTpm._SetCommandCallbacks(null);

                if (MainTestContext.TpmSeemsToBeDead)
                {
                    string functionName = "unknown", lineNumber = "unknown";
#if !TSS_NO_TCP
                    // If so instructed, restart the TPM
                    if (TestCfg.TpmAutoRestart)
                    {
                        KillTpmProcess();
                        Thread.Sleep(5000);

                        StartTpm();
                        Thread.Sleep(5000);

                        MainTestContext.TpmSeemsToBeDead = false;

                        TestCfg.TheTcpTpmDevice.Connect();
                        Debug.Write("");
                    }

                    if (TpmStderr != null)
                    {
                        string[] chunks = TpmStderr.Split(new char[] { ' ' });
                        if (chunks.Length >= 1)
                            functionName = chunks[0];
                        if (chunks.Length >= 2)
                            lineNumber = chunks[1];
                    }
#endif // !TSS_NO_TCP
                    GenerateCrashLog(functionName, lineNumber, test.Name);
                }
            } while (DateTime.Now < endTime);

            Tpm2._TssBehavior.Passthrough = origMode;

            // output some fuzz stats to tune testing
            var errsSeenList = FuzzResponsesReceived.ToList();
            errsSeenList.Sort((x, y) => -x.Value.CompareTo(y.Value));

            var commTimeList = FuzzedCommands.ToList();
            commTimeList.Sort((x, y) => -x.Value.fuzzTime.CompareTo(y.Value.fuzzTime));

            Console.WriteLine("ERRORS RECEIVED");
            foreach (var err in errsSeenList)
            {
                Console.WriteLine(String.Format("{0,16}  {1}", err.Key, err.Value));
            }

            Console.WriteLine("{0,29}Command Statistics", "");
            Console.WriteLine("{0,29}------------------\n", "");
            Console.WriteLine("{0,27}  Series Bypass avgT   totT \n", "Command Name"); 
            foreach (var cinfo in commTimeList)
            {
                FuzzStats fuzzStats = cinfo.Value;
                String s = String.Format("{0,27}: {1,4}, {2,4}, {3,6:0.000}, {4,5:0.00}", 
                    cinfo.Key, fuzzStats.fuzzSeries, fuzzStats.fuzzBypassed,
                    fuzzStats.fuzzTime/(fuzzStats.succeeded + fuzzStats.failed),
                    fuzzStats.fuzzTime);
                Console.Write("{0} | s:{1,5}, f:{2,6}, a:{3,6}; " +
                                    "h:{4,5}, e:{5,5}, b:{6,5}, v:{7,5}; c:{8,3}", s,
                              fuzzStats.succeeded, fuzzStats.failed, fuzzStats.authFailed,
                              fuzzStats.handlesFuzzed, fuzzStats.enumsFuzzed,
                              fuzzStats.buffersFuzzed, fuzzStats.valuesFuzzed,
                              fuzzStats.cmdComplexity);

                if (fuzzStats.lockedOut != 0)
                {
                    Console.Write(" | l:{0}", fuzzStats.lockedOut);
                }
                if (fuzzStats.nullStructsResurrected + fuzzStats.nullLeafsResurrected != 0)
                {
                    Console.Write(" | ns:{0}, nv:{0}", fuzzStats.nullStructsResurrected,
                                                       fuzzStats.nullLeafsResurrected);
                }
                Console.WriteLine("");
            }
            return true;
        }

        // Filter commands that can brick a TPM.  Note that this may be called with
        // encrypted parms so only the header and handles are valid
        private bool IsCommandSafe(TpmCc cmdCode, TpmHandle[] handles)
        {
            if (cmdCode != TpmCc.NvDefineSpace)
                return true;
            if (handles[0] == TpmRh.Owner)
                return true;
            // Else this is a platform define-space, and is risky 
            return false;
        }

        private bool IsCommandSafe(byte[] cmdBuf)
        {
            // Disallow NV_DefineSpace with TpmRh.Platform (these are the only parameters
            // that can be reliably checked on an encrypted session)
            if (cmdBuf.Length < 14)
                return true;
            var m = new Marshaller(cmdBuf);
            var hdr = m.Get<CommandHeader>();
            if (hdr.CommandCode != TpmCc.NvDefineSpace)
                return true;
            var authHandle = m.Get<TpmHandle>();
            if (authHandle == TpmRh.Owner)
                return true;
            // Else this is a platform define-space, and is risky 
            return false;
        }

        // There are two ways to fuzz a TPM command through callbacks on the Tpm2 context.
        // First, TransformerCallback (below). It allows a little bit of smarts -
        // for instance, we can just fuzz the command buffer.
        // Second, RawFuzzCallback - this command. It lets you fuzz everything just
        // before it is sent to the TPM - we only do it x% of the time.
        // TransformerCallback controls whether RawFuzzCallback does anything at all.
        internal bool RawFuzzCallback(ref byte[] cmdBuf)
        {
            if (!IsFuzzing)
            {
                return false;
            }

            if (!SkipRawFuzzer)
            {
                LastFuzzedCommandBuffer = Globs.CopyData(cmdBuf);
                FuzzBuffer(ref cmdBuf, Globs.GetRandomInt() % NumBufFuzzOpts);
            }

            // The command NV_DefineSpace(TpmRh.Platform, ... TPMA_NV_POLICY_DELETE...)
            // can create an NV slot that cannot be deleted.  Let's not do this...
            if (!IsCommandSafe(cmdBuf))
            {
                // Adjust statistics
                Debug.Assert(CurFuzzStats.rawFuzzes > 0);
                --CurFuzzStats.rawFuzzes;

                // Indicate tp the caller that the fuzzing attempt is aborted
                cmdBuf = null;
                return false;
            }
            return true;
        }

        private void ReportFuzzProgress()
        {
            if (CurFuzzingSeriesHadFailures )
            {
                Console.Write(CurFuzzingSeriesHadSuccesses ? "*" : "x");
            }
            else if (CurFuzzingSeriesHadSuccesses)
            {
                Console.Write("+");
            }
            CurFuzzingSeriesHadSuccesses = CurFuzzingSeriesHadFailures = false;
        }

        private void ReportStats(FuzzStats cmdStats)
        {
            if (!TestCfg.Verbose)
                return;

            Console.Write("{{s:{0}, f:{1}, a:{2}; h:{3}, e:{4}, b:{5}, v:{6}; " +
                          "f:{7}, b:{8}, FS:{9}, B:{10}, A:{11:F1}; c:{12}, C:{13}; t:{14:F2}",
                          CurFuzzStats.succeeded, CurFuzzStats.failed, CurFuzzStats.authFailed,
                          CurFuzzStats.handlesFuzzed, CurFuzzStats.enumsFuzzed,
                          CurFuzzStats.buffersFuzzed, CurFuzzStats.valuesFuzzed,
                          cmdStats.fuzzSeries, cmdStats.fuzzBypassed,
                          TotalFuzzSeries, TotalFuzzBypasses, AverageFuzzSeriesPerCmd,
                          cmdStats.cmdComplexity, (int)AverageCmdComplexity, Threshold);
            if (CurFuzzStats.lockedOut != 0)
            {
                Console.Write(" | l:{0}", CurFuzzStats.lockedOut);
            }
            if (CurFuzzStats.nullStructsResurrected + CurFuzzStats.nullLeafsResurrected != 0)
            {
                Console.Write(" | ns:{0}, nv:{0}", CurFuzzStats.nullStructsResurrected,
                                                   CurFuzzStats.nullLeafsResurrected);
            }
            Console.Write("}");
        }

        private void StopFuzzing(string reasonIndicator)
        {
            FuzzStats cmdStats = FuzzedCommands[CurFuzzCmd];
            uint fuzzSeries = cmdStats.fuzzSeries;
            Debug.Assert(cmdStats.cmdInfo.CommandCode == CurFuzzStats.cmdInfo.CommandCode);
            cmdStats.Combine(CurFuzzStats);
            Substrate.Assert(fuzzSeries == cmdStats.fuzzSeries);

            IsFuzzing = false;
            LastFuzzedCommand = CurFuzzCmd;
            CurFuzzCmd = TpmCc.None;
            ReportFuzzProgress();
            Console.Write("{0}", reasonIndicator);
            ReportStats(cmdStats);
            Console.WriteLine("");
        }

        enum FieldFilter
        {
            Any = 0,
            Buffer = 1,
            NonNull = 2
        }

        int NumBufFuzzOpts = 8;

        // Complexity of parameters of the command being traversed.
        int CmdComplexity = 0;


        bool DoFuzzCommand(FuzzStats cmdStat, ref byte[] parmsBuf, TpmHandle[] inHandles)
        {
            SkipRawFuzzer = false;

            int rnd = Globs.GetRandomInt();

            TpmStructureBase b;
            FuzzableMember[] fuzzableVals;

#if true
            byte[] inStructBytes = Globs.Concatenate(
                        Marshaller.GetTpmRepresentation(inHandles), parmsBuf);
            Marshaller mx = new Marshaller(inStructBytes);

            b = (TpmStructureBase)mx.Get(cmdStat.cmdInfo.InStructType, "");
            // TODO: Adjust complexity based on the handle types
            // CmdComplexity = (int)cmdStat.cmdInfo.HandleCountIn * 5;
            CmdComplexity = 0;
            fuzzableVals = TraverseTpmStructure(b.GetType(), b, FieldFilter.Any).ToArray();

            if (CmdComplexity > cmdStat.cmdComplexity)
            {
                AverageCmdComplexity = (AverageCmdComplexity * FuzzedCommands.Count
                                        - cmdStat.cmdComplexity + CmdComplexity) /
                                                                FuzzedCommands.Count;
                cmdStat.cmdComplexity = CmdComplexity;
                CmdComplexity = 0;
            }
#else
            // Reuse previously generated decomposition of the parameters data structure.

            // The approach does not work now because it is necessary to roll back
            // members modified by the previous fuzzing iteration

            if (rnd != 1 || InStructToFuzz == null)
            {
                byte[] inStructBytes = Globs.Concatenate(
                            Marshaller.GetTpmRepresentation(inHandles), parmsBuf);
                Marshaller mx = new Marshaller(inStructBytes);

                b = (TpmStructureBase) mx.Get(info.InStructType, "");
                fuzzableVals = TraverseTpmStructure(b.GetType(), b, FieldFilter.Any).ToArray();

                if (InStructToFuzz == null)
                {
                    InStructToFuzz = b;
                    FuzzableValues = fuzzableVals;
                }
            }
            if (rnd == 1)
            {
                b = InStructToFuzz;
                fuzzableVals = FuzzableValues;
            }
#endif
            if (fuzzableVals.Length == 0)
            {
                FuzzBuffer(ref parmsBuf, rnd);
                return false;
            }

            if (Globs.GetRandomDouble() < RawFuzzProbability)
            {
                // Apply only raw fuzzing this time
                if (parmsBuf.Length != 0 &&
                    Globs.GetRandomDouble() < RawParamFuzzProbability)
                {
                    // Fuzz only the marshaled command parameters buffer
                    FuzzBuffer(ref parmsBuf, rnd);
                    SkipRawFuzzer = true;
                }
                // else fuzz the whole command buffer
                return false;
            }

            // Targeted fuzzing of original command parameters before they are marshaled
            DoFuzzDecomposedStruct(fuzzableVals, cmdStat);

            TpmHandle[] inHandles2;
            byte[] parmsBuf2;
            CommandProcessor.Fragment(b, (uint)inHandles.Length, out inHandles2,
                                      out parmsBuf2);

            //if (!Globs.ArraysAreEqual(parmsBuf, parmsBuf2))
            //    Console.WriteLine("Reconstructed params buffer is " + 
            //      (Globs.ArraysAreEqual(parmsBuf, parmsBuf2) ? "the same" : "different"));
            //if (!Globs.ArraysAreEqual(inHandles, inHandles2))
            //    Console.WriteLine("Reconstructed handle buffer is " + 
            //      (Globs.ArraysAreEqual(inHandles, inHandles2) ? "the same" : "different"));

            parmsBuf = parmsBuf2;

            // Also apply raw fuzzing (over the final command buffer) in 3% of the cases
            SkipRawFuzzer = Globs.GetRandomDouble() > SecondaryRawFuzzProbability;

            return true;
        }

        void DoFuzzDecomposedStruct(FuzzableMember[] fuzzableVals, FuzzStats cmdStat)
        {
            if (fuzzableVals.Length == 0)
            {
                return;
            }

            // Pick up the number of fuzzable values in the decomposed request to
            // actually fuzz. Probability of fuzzing more values decreases.
            int numValsToFuzz = fuzzableVals.Length < 8 ? fuzzableVals.Length : 8;
            double d = Globs.GetRandomDouble();
            double threshold = 0.5;
            for (int i = 1; i < numValsToFuzz; ++i )
            {
                if (d > threshold)
                {
                    numValsToFuzz = i;
                    break;
                }
                threshold /= 2;
            }
            if (numValsToFuzz == 0)
            {
                numValsToFuzz = fuzzableVals.Length;
            }

            //int numValsToFuzz = fuzzableVals.Length;

            var valsToFuzz = new FuzzableMember[numValsToFuzz];

            if (fuzzableVals.Length == 1)
            {
                valsToFuzz[0] = fuzzableVals[0];
            }
            else
            {
                // Select numValsToFuzz out of fuzzableVals.Length values
                int selected = 0;
                for (int i = 0;
                     selected < numValsToFuzz  && selected < fuzzableVals.Length - i;
                     ++i )
                {
                    double p = 1.0 - Math.Pow(1.0 - (double)fuzzableVals[i].Item3 / 
                                                    cmdStat.cmdComplexity, numValsToFuzz);
                    Debug.Assert(p <= 1.0);

                    double r = Globs.GetRandomDouble();
                    if (r <= p)
                    {
                        var memInfo = fuzzableVals[i];
                        object member = Globs.GetMember(memInfo.Item1, memInfo.Item2);
                        Type memType = Globs.GetMemberType(memInfo.Item1);

                        if (member != null || ValidStructs.ContainsKey(memType))
                        {
                            valsToFuzz[selected++] = memInfo;
                        }
                    }
                }
                while (selected < numValsToFuzz)
                {
                    valsToFuzz[selected] = fuzzableVals[fuzzableVals.Length
                                           - (numValsToFuzz - selected)];
                    ++selected;
                }
            }

            foreach (var memInfo in valsToFuzz)
            {
                Type memType = Globs.GetMemberType(memInfo.Item1);
                var containingObject = memInfo.Item2;
                object member = Globs.GetMember(memInfo.Item1, containingObject);
                var ti = memType.GetTypeInfo();
                int rnd = Globs.GetRandomInt();

                if (member == null)
                {
                    //Console.Write("'Nil' node: {0} {1} > ", memType, memInfo.Item1.Name);
                    if (memType == typeof(byte[]))
                    {
                        member = new byte[0];
                    }
                    else if (!ti.IsEnum && !ti.IsValueType)
                    {
                        //Console.Write("'Nil' struct: {0} {1} > ", memType, memInfo.Item1.Name);

                        if (ValidStructs.ContainsKey(memType))
                        {
                            //Console.WriteLine("Replacing...");
                            member = ValidStructs[memType];
                            // Use actual object type in case of members of an interface type
                            DoFuzzDecomposedStruct(
                                    TraverseTpmStructure(member.GetType(), member,
                                                         FieldFilter.Any).ToArray(),
                                    cmdStat);
                            ++CurFuzzStats.nullStructsResurrected;
                        }
                        //else
                        //    Console.WriteLine("No replacement...");
                        return;
                    }
                    else
                    {
                        member = Activator.CreateInstance(memType);
                    }
                    ++CurFuzzStats.nullLeafsResurrected;
                }

                if (ti.IsEnum)
                {
                    object fuzzedEnum = FuzzEnum(memType, member);
                    Globs.SetMember(memInfo.Item1, containingObject, fuzzedEnum);
                    ++CurFuzzStats.enumsFuzzed;
                }
                else if (memType == typeof(byte[]))
                {
                    var buf = member as byte[];
                    if (buf.Length > 0)
                    {
                        if (rnd % 17 != 0)
                        {
                            FuzzBuffer(ref buf, rnd);
                        }
                        else
                        {
                            buf = null;
                        }
                    }
                    else
                    {
                        // Pick up a random size for the fuzzed empty buffer, making 
                        // sure that the values from various ranges are selected.
                        int bufSize = 0;
                        switch (rnd % 5)
                        {
                        case 0:
                            bufSize = Substrate.RandomInt(TpmCfg.MinDigestSize - 1) + 1;
                            break;
                        case 1:
                            bufSize = Substrate.RandomInt(TpmCfg.MaxDigestSize - TpmCfg.MinDigestSize)
                                    + TpmCfg.MinDigestSize;
                            break;
                        case 2:
                            bufSize = TpmHash.DigestSize(Substrate.Random(TpmCfg.HashAlgs));
                            break;
                        case 3:
                            bufSize = Substrate.RandomInt(TpmCfg.MaxNvOpSize - TpmCfg.MaxDigestSize)
                                    + TpmCfg.MaxDigestSize;
                            break;
                        case 4:
                            var keySizes = new int[] { 16, 16, 16, 24, 32, 32, 32, 48,
                                                       64, 1024, 1024, 2048, 2048 };
                            bufSize = keySizes[Substrate.RandomInt(keySizes.Length)];
                            break;
                        case 5:
                            bufSize = Substrate.RandomInt(4 * TpmCfg.MaxNvIndexSize - TpmCfg.MaxNvOpSize)
                                    + TpmCfg.MaxNvOpSize;
                            break;
                        }
                        rnd /= 5;
                        buf = rnd % 2 == 0 ? Substrate.RandomBytes(bufSize)
                                           : Globs.GetZeroBytes(bufSize);
                    }
                    Globs.SetMember(memInfo.Item1, containingObject, buf);
                    ++CurFuzzStats.buffersFuzzed;
                }
                else if (containingObject.GetType() == typeof(TpmHandle))
                {
                    if (rnd % 4 == 0)
                    {
                        // Fuzz as TPM_RH
                        object fuzzedEnum = FuzzEnum(typeof(TpmRh), member);
                        Globs.SetMember(memInfo.Item1, containingObject, fuzzedEnum);
                    }
                    else if (rnd % 8 == 0)
                    {
                        // Fuzz as random value
                        Globs.SetMember(memInfo.Item1, containingObject,
                                        Globs.GetRandomUInt());
                    }
                    else
                    {
                        // TODO: Fuzz to defined handle values in PCR, transient,
                        //       persistent, NV, and session ranges
                        byte[] mso = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x040, 0x80, 0x81 };
                        rnd /= 29;
                        uint fuzzedVal = ((uint)mso[Globs.GetRandomInt(7)] << 24)
                                | (Globs.GetRandomUInt() & 
                                  (rnd % 4 == 0 ? 0x00FFFFFFu : rnd % 4 == 1 ? 0x3Fu : 0x07u));

                    }
                    ++CurFuzzStats.handlesFuzzed;
                }
                else
                {
                    Substrate.Assert(memType.GetTypeInfo().IsValueType);

                    byte[] val = null;
                    val = Globs.HostToNet(member);
                    // Select fuzzing mode that does not change buffer size to preserve
                    // value type
                    FuzzBuffer(ref val, rnd, false);
                    member = Globs.NetToHostValue(memType, val);
                    Globs.SetMember(memInfo.Item1, containingObject, member);
                    ++CurFuzzStats.valuesFuzzed;
                }
            }
        }

        void FuzzBuffer(ref byte[] x, int fuzzType, bool mayRealloc = true)
        {
            // Figuring out what's best here is a bit delicate.  Generally we think
            // that the input parm validation is good - i.e. too short, too-long and
            // at least some bad-value inputs are summarily rejected. So perhaps more
            // important here is to introduce subtle input errors that sneak past input
            // validation and exercise new control-flow paths in the TPM itself.
            // In the following we flip 1, 2, or 3 bits, shorten the input, or add
            // random data in the middle of the input byte-array.
            // Intuition says that a single bit-flip is a subtle change that is most
            // likely to sneak into the TPM, so most of our fuzzes are a single bit-flip.
            // More options can be added here as we get sneakier.
            
            byte bitToFlip;
            int byteToFlip;
            switch (fuzzType % (NumBufFuzzOpts - (mayRealloc ? 0 : 1)))
            {
                // flip one bit
                case 0:
                case 1:
                case 2:
                case 3:
                    bitToFlip = (byte)(1 << Globs.GetRandomInt(8));
                    byteToFlip = Globs.GetRandomInt(x.Length);
                    x[byteToFlip] ^= bitToFlip;
                    break;
                // flip 2 bits
                case 4:
                case 5:
                    bitToFlip = (byte)(1 << Globs.GetRandomInt(8));
                    byteToFlip = Globs.GetRandomInt(x.Length);
                    x[byteToFlip] ^= bitToFlip;

                    bitToFlip = (byte)(1 << Globs.GetRandomInt(8));
                    byteToFlip = Globs.GetRandomInt(x.Length);
                    x[byteToFlip] ^= bitToFlip;
                    break;
                // flip 3 bits
                case 6:
                    bitToFlip = (byte)(1 << Globs.GetRandomInt(8));
                    byteToFlip = Globs.GetRandomInt(x.Length);
                    x[byteToFlip] ^= bitToFlip;

                    bitToFlip = (byte)(1 << Globs.GetRandomInt(8));
                    byteToFlip = Globs.GetRandomInt(x.Length);
                    x[byteToFlip] ^= bitToFlip;

                    bitToFlip = (byte)(1 << Globs.GetRandomInt(8));
                    byteToFlip = Globs.GetRandomInt(x.Length);
                    x[byteToFlip] ^= bitToFlip;
                    break;
                // shorten or lengthen the command by adding random data at the breakpoint
                // or deleting bytes at the breakpoint 
                case 7:
                    int length = x.Length;
                    int newLength = 1 + Globs.GetRandomInt(length * 2);
                    int breakPoint = Globs.GetRandomInt(Math.Min(length, newLength));
                    int missing = length - newLength;
                    byte[] t = new byte[newLength];
                    // make a new buffer and copy in the old buf adding new random
                    // bytes or deleting bytes at the breakpoint
                    if (missing >= 0)
                    {
                        // shorter.  Leave out a bit of the original array at breakpoint
                        Array.Copy(x, t, breakPoint);
                        Array.Copy(x, breakPoint + missing, t, breakPoint,
                                   length - breakPoint - missing);
                    }
                    else
                    {
                        // longer.  Copy frags and fill gap with random
                        int extraSize = -missing;
                        Array.Copy(x, t, breakPoint);
                        Array.Copy(x, breakPoint, t, breakPoint + extraSize,
                                   length - breakPoint);
                        Array.Copy(Substrate.RandomBytes(extraSize), 0, t, breakPoint, extraSize);
                    }
                    x = t;
                    break;
                default:
                    WriteToLog("FuzzBuffer: Logic error");
                    Debug.Assert("FuzzBuffer: Logic error: Check NumBufFuzzOpts value/usage" == "");
                    break;
            }
        } // FuzzBuffer
        
        object FuzzEnum (Type eType, object e)
        {
            Substrate.Assert(eType.GetTypeInfo().IsEnum);
            var enumValues = Enum.GetValues(eType);
            object[] values = new object[enumValues.Length];
            Type uType = Enum.GetUnderlyingType(eType);
            for (int i = 0; i < enumValues.Length; ++i)
            {
                values[i] = Convert.ChangeType(enumValues.GetValue(i), uType);
            }
#if false
            ulong firstEnumerator = values[values[0] == 0 && values.Length > 1 ? 1 : 0]; 
            ulong lastEnumerator = values[values.Length - 1];
            bool sparse = (ulong)values.Length <= lastEnumerator - firstEnumerator;
#endif
            // Assign fuzzed enumerator a random value.
            int r = Globs.GetRandomInt();
            bool fuzzWithValid = (r & 3) != 3;
            r >>= 2;
            if (fuzzWithValid)
            {
                object v = values[r % (values.Length - 1)];
                if (e.Equals(v))
                {
                    v = values[values.Length - 1];
                }
                return v;
            }
            byte[] buf = Globs.HostToNet(Convert.ChangeType(e, uType));
            FuzzBuffer (ref buf, r, false);
            return Enum.ToObject(eType, Globs.NetToHostValue(uType, buf));
        }

        // Adjusts the Threshold value (probability of initiating the fuzzing series
        // for the current command) based on the complexity of the command and how
        // often has it already been fuzzed.
        void AdjustFuzzingProbability(FuzzStats cmdStat)
        {
            // Maximal amounts, by which the fuzzing probability may be changed.
            double  upBase = MaxFuzzProbability - Threshold,
                    downBase = Threshold - MinFuzzProbability;

            // Adjustment is not applicable for a command that has not been fuzzed yet.
            //Debug.Assert(cmdStat.fuzzSeries != 0 && cmdStat.cmdComplexity != 0);

            // Reverse (complement to 1.0) coefficients defining how much the fuzzing
            // probability should change relative to the upward/downward limits.
            double  upCoef = 1.0, downCoef = 1.0;

            // Coefficient to favor lees frequently fuzzed commands
            if (cmdStat.fuzzSeries < AverageFuzzSeriesPerCmd)
            {
                upCoef *= cmdStat.fuzzSeries / AverageFuzzSeriesPerCmd;
            }
            else if (cmdStat.fuzzSeries > AverageFuzzSeriesPerCmd)
            {
                downCoef *= AverageFuzzSeriesPerCmd / cmdStat.fuzzSeries;
            }

            // Coefficient to favor commands with more complex parameters
            if (cmdStat.cmdComplexity > AverageCmdComplexity)
            {
                upCoef *= AverageCmdComplexity / cmdStat.cmdComplexity;
            }
            else if (cmdStat.cmdComplexity < AverageCmdComplexity)
            {
                downCoef *= cmdStat.cmdComplexity / AverageCmdComplexity;
            }

            if (downCoef == 1.0)
            {
                Threshold += upBase * (1.0 - upCoef);
            }
            else if (upCoef == 1.0)
            {
                Threshold += downBase * (downCoef - 1.0);
            }
            else
            {
                double  offs = (1.0 - upCoef) * (1.0 - downCoef);

                if (upCoef < downCoef)
                {
                    Threshold += upBase * (1.0 - (upCoef + offs));
                }
                else if (upCoef > downCoef)
                {
                    Threshold += downBase * (downCoef + offs - 1.0);
                }
            }

            if (Threshold < MinFuzzProbability)
            {
                // Make sure that this is the result of a rounding error
                Debug.Assert((MinFuzzProbability - Threshold) / MinFuzzProbability < 1e-6);
                Threshold = MinFuzzProbability;
            }
        }


        // Generally the fuzzer executes a few commands before fuzzing begins
        // (to get the TPM internal state correct for a complicated scenario.
        // Then the tester will fuzz for a while and then give up.
        internal bool TransformerCallback(CommandInfo info, ref byte[] parmsBuf,
                                        TpmHandle[] inHandles)
        {
            if (parmsBuf.Length == 0 && info.HandleCountIn == 0)
            {
                // There is no sense in fuzzing this command.
                Substrate.Assert(!IsFuzzing);
                SkipRawFuzzer = true;
                return false;
            }

            TpmCc cmdCode = info.CommandCode;

            TimeSpan progressPeriod = new TimeSpan(0, 0, 2);
            DateTime now = DateTime.Now;

            if (!FuzzedCommands.ContainsKey(cmdCode))
            {
                AverageCmdComplexity = AverageCmdComplexity * FuzzedCommands.Count
                                        / (FuzzedCommands.Count + 1);
                FuzzedCommands.Add(cmdCode, new FuzzStats(info));
            }

            FuzzStats cmdStat = FuzzedCommands[cmdCode];

            if (!IsFuzzing)
            {
                // We are not yet fuzzing. Start the next fuzzing series now?

                if (!IsCommandSafe(cmdCode, inHandles))
                {
                    // Fuzzing this command may get theTPM bricked
                    return false;
                }

                if (FuzzCmd == null)
                {
                    if (cmdStat.fuzzSeries == 0)
                    {
                        Threshold = BaseFuzzProbability + (1.0 - 1.0 / (cmdStat.fuzzBypassed + 1))
                                                         * (MaxFuzzProbability - BaseFuzzProbability);
                    }
                    else
                    {
                        if (LastFuzzedCommand != cmdCode)
                        {
                            Threshold = BaseFuzzProbability;
                        }
                        else
                        {
                            // Decrease the chance of fuzzing the same command twice in a row
                            Threshold = MinFuzzProbability
                                      + Threshold * (BaseFuzzProbability - MinFuzzProbability);
                        }

                        AdjustFuzzingProbability(cmdStat);
                    }
                }
                else if (cmdCode.ToString() != FuzzCmd)
                {
                    // Exclusive fuzzing mode: skip all commands but FuzzCmd
                    return false;
                }
                else
                {
                    // Exclusive fuzzing mode uses a random fuzzing probability
                    Threshold = MinFuzzProbability
                              + Globs.GetRandomDouble() * (MaxFuzzProbability - MinFuzzProbability);
                }

                Debug.Assert(Threshold >= MinFuzzProbability && Threshold <= MaxFuzzProbability);

                if (Globs.GetRandomDouble() > Threshold)
                {
                    ++cmdStat.fuzzBypassed;
                    ++TotalFuzzBypasses;
                    return false;
                }

                // Start fuzzing...
                ++TotalFuzzSeries;
                ++cmdStat.fuzzSeries;
                AverageFuzzSeriesPerCmd = (double)TotalFuzzSeries / FuzzedCommands.Count;
                IsFuzzing = true;
                // Ignore test failures from this point until the end of the test case
                MainTestContext.ReportErrors = false;
                CurFuzzCmd = cmdCode;
                CurFuzzingSeriesHadSuccesses = CurFuzzingSeriesHadFailures = false;
                CurFuzzStats = new FuzzStats(info);
                TimeSpan toGo = TestCfg.TestEndTime - now;
                if(toGo < new TimeSpan())
                {
                    toGo = new TimeSpan();
                }
                WriteToLog("Fuzzing {0} ({1} to go) ", cmdCode,
                           toGo.ToString(@"dd\d\ hh\:mm\:ss"), ConsoleColor.Magenta);
                NextProgressReportTime = now + progressPeriod;
                CurFuzzEndTime = now + MaxCommandFuzzTime;
            }

            Debug.Assert(CurFuzzCmd != TpmCc.None);
            Debug.Assert(CurFuzzCmd == cmdCode);

            try
            {
                if (   DoFuzzCommand(cmdStat, ref parmsBuf, inHandles)
                    && SkipRawFuzzer)
                {
                    ++CurFuzzStats.tgtFuzzes;
                }
                else
                {
                    ++CurFuzzStats.rawFuzzes;
                }
            }
            catch (Exception)
            {
                StopFuzzing("!");
                throw;
            }

            uint fuzzCount = CurFuzzStats.tgtFuzzes + CurFuzzStats.rawFuzzes;

            Debug.Assert(fuzzCount - 1 == CurFuzzStats.succeeded + CurFuzzStats.failed);

            // After a while it is better to fuzz something else
            if (now > CurFuzzEndTime || fuzzCount > MaxFuzzCount)
            {
                StopFuzzing("\\");
                throw new Exception("Escape from the test");
            }

            // Report progress
            if (now > NextProgressReportTime || fuzzCount % (MaxFuzzCount / 20) == 0)
            {
                ReportFuzzProgress();
                NextProgressReportTime = now + progressPeriod;
            }

            if (   FuzzCountToBreak == fuzzCount
                && (cmdCode.ToString() == BreakCmd || BreakCount == fuzzCount))
            {
                // Trigger debug break point
                Debug.Assert("Debug break" == "");
            }
            return true;
        }

        private readonly Dictionary<TpmRc, int> FuzzResponsesReceived = new Dictionary<TpmRc, int>();

        // Special processing for fuzz-loops similar to what is done for normal command
        // execution upon response from TPM. Basically what we are doing here is keeping
        // the sessions up-to-date so that the nonces are rolled if a command succeeds.
        // This makes fuzzing more effective (otherwise the TPM will be trivially
        // rejecting hmac session command with AuthFail.
        internal bool FuzzStatsCallback(TpmCc command, TpmRc maskedError, double executionTime)
        {
            if (!IsFuzzing)
            {
                return false;
            }
            //Console.WriteLine(maskedError.ToString(), ConsoleColor.Cyan);
            if (maskedError == TpmRc.Success)
            {
                CurFuzzingSeriesHadSuccesses = true;
                ++CurFuzzStats.succeeded;
            }
            else
            {
                CurFuzzingSeriesHadFailures = true;
                ++CurFuzzStats.failed;
                if (   maskedError == TpmRc.AuthFail || maskedError == TpmRc.BadAuth
                    || maskedError == TpmRc.PolicyFail)
                {
                    ++CurFuzzStats.authFailed;
                }
                else if (maskedError == TpmRc.Lockout)
                {
                    ++CurFuzzStats.lockedOut;
                }
            }
            // Collect stats on errors seen (to improve fuzzing)
            if (!FuzzResponsesReceived.ContainsKey(maskedError))
            {
                FuzzResponsesReceived.Add(maskedError, 0);
            }
            FuzzResponsesReceived[maskedError]++;
            CurFuzzStats.fuzzTime += executionTime;

            // If the fuzzed command succeeded, sometimes stop the current fuzzing
            // series, provided that a substantial part of the current fuzzing series
            // has already been executed. The condition below favors early termination
            // for commands that are less probable to succeed when fuzzed.
            //
            // Such early termination upon a successfully executed fuzzed command
            // allows the test to continue its execution using unexpected, though
            // technically consistent, results of the current command, which improves
            // chances of covering broader range of control flow paths in the TPM.
            if (maskedError == TpmRc.Success)
            {
                uint fuzzCount = CurFuzzStats.tgtFuzzes + CurFuzzStats.rawFuzzes;

                Debug.Assert(fuzzCount == CurFuzzStats.succeeded + CurFuzzStats.failed);

                double doneIters = (double)fuzzCount / MaxFuzzCount;
                double doneTime = 1.0 - (CurFuzzEndTime - DateTime.Now).TotalSeconds /
                                        MaxCommandFuzzTime.TotalSeconds;

                if (   (doneIters > FuzzEarlyQuitLimit || doneTime > FuzzEarlyQuitLimit)
                    && Globs.GetRandomDouble() <
                       doneIters * CurFuzzStats.failed / fuzzCount)
                {
                    StopFuzzing(">");
                    //String.Format(" [i:{0:F2}, t:{1:F2}, f:{2:F2}] ", doneIters, doneTime, (double)CurFuzzStats.failed / fuzzCount);
                    return false;
                }
            }
            return true;
        }

        private static void FlushHandles (Type responseType)
        {
            FieldInfo[] fields = responseType.GetFields(BindingFlags.Instance | BindingFlags.Public);
            for (int i = 0; i < fields.Length; ++i)
            {
                if (fields[i].FieldType != typeof(TpmHandle))
                    continue;
                // TODO: Flush'em here ...
            }
        }

        void RememberValidStruct(Type type, object o)
        {
            if (ValidStructs.ContainsKey(type))
            {
                ValidStructs[type] = o;
            }
            else
            {
                ValidStructs.Add(type, o);
            }
        }

        static Dbg FuzzDbg = new Dbg(true);

        List<FuzzableMember> TraverseTpmStructure(Type t, object o, FieldFilter filter)
        {
            var fuzzableVals = new List<FuzzableMember>();

            if (o == null)
            {
                return fuzzableVals;
            }

            // recurse
            var members = new List<MemberInfo>();
            foreach (var bf in new BindingFlags[] {BindingFlags.Public, BindingFlags.NonPublic})
            {
                var candidateMembers = t.GetMembers(BindingFlags.Instance | bf);
                foreach (var mi in candidateMembers)
                {
                    var memberAttrs = mi.CustomAttributes;
                    foreach (var a in memberAttrs)
                    {
                        if (a.AttributeType.Name != "MarshalAsAttribute")
                        {
                            continue;
                        }
                        var arg1 = a.ConstructorArguments[1];
                        if (arg1.ArgumentType == typeof(MarshalType))
                        {
                            var marshalType = (MarshalType)arg1.Value;
                            if (marshalType != MarshalType.ArrayCount &&
                                marshalType != MarshalType.LengthOfStruct &&
                                marshalType != MarshalType.UnionSelector)
                            {
                                members.Add(mi);
                            }
                        }
                        break;
                    }
                }
            }

            FuzzDbg.Trace(t.ToString());
            FuzzDbg.Indent();
            foreach (var memInfo in members)
            {
                Type memType = Globs.GetMemberType(memInfo);
                object member = Globs.GetMember(memInfo, o);

                if (member == null)
                {
                    if (!filter.HasFlag(FieldFilter.NonNull))
                    {
                        // TODO: Update CmdComplexity based on the type info
                        fuzzableVals.Add(new FuzzableMember(memInfo, o, 0));
                        if (memInfo.Name != "inPrivate")
                            FuzzDbg.Trace("Leaf 'Nil': {0} {1}", memType, memInfo.Name);
                    }
                    continue;
                }

                Type actualType = member.GetType();
                if (actualType.GetTypeInfo().IsSubclassOf(typeof(TpmStructureBase)))
                {
                    // TODO: Update CmdComplexity based on the type info
                    FuzzDbg.Trace("Tpm Structure: {0} {1} {2}", memType, actualType, memInfo.Name);
                    fuzzableVals.AddRange(TraverseTpmStructure(actualType, member, filter));
                    RememberValidStruct(memType, member);
                }
                else if (actualType.IsArray && actualType != typeof(byte[]))
                {
                    FuzzDbg.Trace("Array: {0} {1} {2}", memType, actualType, memInfo.Name);
                    var arr = (Array)member;
                    Type elementType = actualType.GetElementType();
                    foreach (object e in arr)
                    {
                        fuzzableVals.AddRange(TraverseTpmStructure(elementType, e, filter));
                    }
                    RememberValidStruct(memType, member);
                }
                else if (  !filter.HasFlag(FieldFilter.Buffer)
                         || actualType == typeof(byte[]))
                {
                    int complexity = 0;
                    if (actualType.GetTypeInfo().IsEnum)
                    {
                        complexity = Enum.GetValues(actualType).Length / 4 + 4;
                        if (memInfo is PropertyInfo)
                        {
                            Type uType = Enum.GetUnderlyingType(actualType);
                            object origVal = Convert.ChangeType(member, uType);
                            Globs.SetMember(memInfo, o, Enum.ToObject(actualType,
                                            Globs.IncrementValue(origVal, 1)));
                            object newVal = Convert.ChangeType(
                                                Globs.GetMember(memInfo, o), uType);
                            if (newVal.Equals(origVal))
                            {
                                // A property without setter
                                FuzzDbg.Trace("Property w/o setter: {0} {1}", actualType, memInfo.Name);
                                continue;
                            }
                        }
                    }
                    else if (actualType == typeof(byte[]))
                    {
                        complexity = 16;
                    }
                    else if (o.GetType() == typeof(TpmHandle))
                    {
                        // TPM_RH, PCR, transient, persistent, NV, session, arbitrary 
                        complexity = Enum.GetValues(typeof(TpmRh)).Length / 4 + 6 * 2;
                    }
                    else
                    {
                        // An integer value
                        complexity = 4;
                    }
                    FuzzDbg.Trace("Leaf '{0}': {0} {1}", filter.HasFlag(FieldFilter.Buffer) ? "Buf" : "Val", actualType, memInfo.Name);
                    CmdComplexity += complexity;
                    fuzzableVals.Add(new FuzzableMember(memInfo, o, complexity));
                }
                else
                {
                    Debug.Assert("Unidentified field type" == "");
                }
            }
            FuzzDbg.Unindent();
            return fuzzableVals;
        }

        List<MethodInfo> MethodInfoFromNames(List<string> testNames)
        {
            List<MethodInfo> testsToRun = new List<MethodInfo>();
            foreach (string s in testNames)
            {
                MethodInfo t = AllTests.Find(item => s == item.Name);
                if (t == null)
                {
                    WriteErrorToLog("Test " + s + " is not defined.\nAborting");
                    return null;
                }
                testsToRun.Add(t);
            }
            return testsToRun;
        }

        bool GenerateCrashLog(string assertFuncName, string assertlineNum, string testFunc)
        {
            string hostName = Process.GetCurrentProcess().MachineName;
            string guid = System.Guid.NewGuid().ToString();
 
            string fileName = "Fail_"+assertFuncName + "_" + assertlineNum + "_" +testFunc+ "_" +
                CurFuzzCmd.ToString() + "_" + hostName + "_" + guid + ".txt";
            string toWrite = "-fuzz -seed " + MainTestContext.CurRngSeed
                + testFunc + "\r\n\r\n";

            toWrite += Globs.HexFromByteArray(LastFuzzedCommandBuffer) + "\r\n\r\n";

            toWrite += MainTestContext.GetLastExceptionInfo();
            string sep = new string(new char[] {Path.DirectorySeparatorChar});
            string separator = sep;
            if (   TestCfg.FuzzCrashesDirectory!= ""
                && TestCfg.FuzzCrashesDirectory.EndsWith(sep))
            {
                separator = "";
            }
            string filePath = TestCfg.FuzzCrashesDirectory + separator + fileName;
            try
            {
                File.WriteAllText(filePath, toWrite, Encoding.ASCII);
            }
            catch (Exception)
            {
                WriteErrorToLog("Failed to write file " + filePath);
                return false;
            }
            return true;
        }

    } // partial class TestCases

} // namespace Tpm2TestSuite

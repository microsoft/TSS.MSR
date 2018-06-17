/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.IO;
using System.Diagnostics;
using System.Reflection;
using System.Collections.Generic;
using System.Linq;
using Tpm2Lib;

namespace Tpm2Tester
{
    // These flags are OR-able
    public enum TestStatus
    {
        None = 0,
        OK = 1,
        Failed = 2,
        Aborted = 3
    };

    // One of these per test thread.  Errors are reported to the owning tester
    // when these objects are disposed.
    public class TestContext : ICommandCallbacks
    {
        internal TestLogger Logger;
        public int Instance;
        bool ValidateTestAttributes = true;

        StreamWriter TpmIoWriter = null;

        // Error reporting needs to be suppressed during fuzzing.
        public bool ReportErrors = true;

        public bool TpmSeemsToBeDead = false;

        ICommandCallbacks ChainedCallbacks;

        internal List<TestCaseInfo> FailedTests = new List<TestCaseInfo>();
        internal List<TestCaseInfo> TestsWithWarnings = new List<TestCaseInfo>();
        internal Dictionary<string, int> FailedAssertions = new Dictionary<string, int>();

        // Parameters associated with the currently executed test phase.
        // In case of test failure, they are included in the error report.
        object[] CurTestPhaseParms;

        internal string CurRngSeed = null;

        internal int CurTestNum = 0;

        internal TestStatus CurTestStatus;
        internal string CurTestMethod;
        internal DateTime CurTestStartTime;

        internal Dictionary<TpmCc, CommandStats> CmdStats =
                                            new Dictionary<TpmCc, CommandStats>();

        // Number of TPM commands issued by the current test
        internal int NumCommands = 0;

        // Number of test assertions evaluated by the current test
        internal int NumAsserts = 0;

        DateTime LastReportTime = DateTime.MinValue;

        internal bool ClearWasExecuted = false;

        // Bit mask (set of bit flags) that allows a test method to track whether a
        // particular execution branch has been taken since the beginning of the
        // current test.
        long TestPaths = 0;

        // Current command execution info
        DateTime CurCmdStartTime;

        internal TestContext(TestLogger logger, int instance, 
                             bool validateTestAttributes,
                             ICommandCallbacks callbacks = null)
        {
            Logger = logger;
            Instance = instance;
            ValidateTestAttributes = validateTestAttributes;
            ChainedCallbacks = callbacks;
        }

        public bool IsTestPathTaken(byte pathID)
        {
            if (pathID >= sizeof(long))
                throw new Exception("Extra check number is too large");
            return (TestPaths & (1 << pathID)) != 0;
        }

        public void TestPathDone(byte pathID)
        {
            if (pathID >= sizeof(long))
                throw new Exception("Extra check number is too large");
            TestPaths |= (long)1 << pathID;
        }

        internal void TestStarted(string methodName, string rngSeed = null)
        {
            if (CurTestMethod == TestLogger.LibTesterInfraName)
            {
                Debug.Assert(methodName != TestLogger.LibTesterInfraName);
                TestCompleted();
            }

            ClearWasExecuted = false;
            CurRngSeed = rngSeed;

            Debug.Assert(CmdStats.Count == 0 && NumCommands == 0 && NumAsserts == 0
                         && TestPaths == 0 && CurTestPhaseParms == null);
            CurTestStatus = TestStatus.OK;
            CurTestMethod = methodName;
            CurTestStartTime = DateTime.Now;
            if (methodName == TestLogger.LibTesterInfraName)
            {
                // Report errors accumulated during the test run
                Logger.ReportErrors(this);
            }
            else
            {
                ++CurTestNum;
                Logger.ThreadSafePrint("[" + (Instance != 0 ? Instance + ":" : "")
                                       + CurTestMethod + "]");
                LastReportTime = DateTime.MinValue;
                ReportProgress();
            }
        }

        internal void TestCompleted()
        {
            bool transitionToInfra = CurTestMethod != TestLogger.LibTesterInfraName;

            // FailedTests will be reset
            Logger.ReportErrors(this);
            Logger.TestCompleted(this);

            CurRngSeed = null;
            CurTestStatus = TestStatus.None;
            CurTestMethod = null;
            CurTestPhaseParms = null;
            TestPaths = 0;
            NumCommands = NumAsserts = 0;
            CmdStats.Clear();

            if (transitionToInfra)
            {
                // Attribute the current TPM commands execution span to the Tpm2Tester
                // infrastructure
                TestStarted(TestLogger.LibTesterInfraName);
            }
        }

        private void ReportProgress()
        {
            if (Logger.TestCount == 0 ||
                (DateTime.Now - LastReportTime).TotalSeconds < 1)
            {
                return;
            }

            string toGo = Logger.TimeToGo();
            string progress = toGo == null
                    ? "Test " + CurTestNum + " of " + Logger.TestCount
                    : "Time to go " + toGo;

            progress += ": " + NumCommands + " commands; " + NumAsserts + " asserts";

            if (Logger.MaxStatsLineLen < progress.Length)
            {
                Logger.MaxStatsLineLen = progress.Length;
            }
            Console.Write(progress + "\r");
            LastReportTime = DateTime.Now;
        }

        void ICommandCallbacks.PreCallback(byte[] inBuf, out byte[] modifiedInBuf)
        {
            if (ChainedCallbacks == null)
            {
                modifiedInBuf = null;
            }
            else
            {
                ChainedCallbacks.PreCallback(inBuf, out modifiedInBuf);
            }
            CurCmdStartTime = DateTime.Now;
        }

        static string PrevLogName = "";
        static int PrevLogInstance = 1;

        // This is installed as the raw command callback handler on the underlying TPM.
        // It is used to generate low-level test statistics (number of commands executed,
        // etc.), dumps of the conversation with the TPM and to keep a record of all
        // command sequences seen that contain types that we haven't seen before.
        // In the case of a multi-context TPM this will be called on different threads,
        // but locking should be handled safely by MainTestLogger.
        void ICommandCallbacks.PostCallback(byte[] inBuf, byte[] outBuf)
        {
            TimeSpan cmdExecutionTime = DateTime.Now - CurCmdStartTime;

            if (inBuf.Length < 10)
            {
                return;
            }
            Marshaller m = new Marshaller(inBuf);
            TpmSt sessionTag = m.Get<TpmSt>();
            uint parmSize = m.Get<UInt32>();
            TpmCc commandCode = m.Get<TpmCc>();

            if (commandCode == TpmCc.Clear)
            {
                ClearWasExecuted = true; 
            }

            Marshaller mOut = new Marshaller(outBuf);
            TpmSt responseTag = mOut.Get<TpmSt>();
            uint responseParamSize = mOut.Get<uint>();
            TpmRc responseCode = mOut.Get<TpmRc>();

            if (ValidateTestAttributes)
            {
                // ValidateTestAttributes should not be set for a stress run
                LogTestAttributes(sessionTag, commandCode);
                try
                {
                    if (responseCode == TpmRc.Success)
                        ValidateHandleUsage(commandCode, inBuf);
                }
                catch (Exception)
                {
                    // Invalid command buffer can mess this up
                }
            }

            if (sessionTag.Equals(TpmSt.Null))
                return;

            // There are two encoding for errors - formats 0 and 1. Decode the error type
            uint resultCodeValue = (uint)responseCode;
            bool formatOneErrorType = ((resultCodeValue & 0x80) != 0);
            uint resultCodeMask = formatOneErrorType ? 0xBFU : 0x97FU;

            TpmRc maskedError = (TpmRc)((uint)responseCode & resultCodeMask);

            lock (this)
            {
                // log the command info to the test logger so that it can collect stats
                LogCommandExecution(commandCode, maskedError, cmdExecutionTime);
            }

#if false
            // Keep a copy of successfully executed commands that contain types we have
            // not seen so far. This is for tests that need good-command candidate strings,
            // like TestCommandDispatcherCoverage.
            // Code 0x80280400 is returned by TBS when the command is blocked by Windows.
            if (maskedError == TpmRc.Success && !Tpm2.IsTbsError(resultCodeValue))
            {
                // look at all types in command string.  If we have a new type we keep it
                CrackedCommand cc = CommandProcessor.CrackCommand(inBuf);
                CommandInfo info = CommandInformation.Info.First(x =>
                                            x.CommandCode == cc.Header.CommandCode);
                byte[] inStructBytes = Globs.Concatenate(
                    Globs.GetZeroBytes((int) info.HandleCountIn*4), 
                    cc.CommandParms);
                Marshaller mx = new Marshaller(inStructBytes);

                TpmStructureBase bb = (TpmStructureBase) mx.Get(info.InStructType, "");

                // If a new type is contained, save this command for testing in
                // TestDispatcherCoverage.
                if (HasNewTypes(bb))
                {
                    ExecutedCommandInfo.Add(inBuf);
                }
            }
            else
#else
            if (maskedError != TpmRc.Success)
#endif
            {
                // If a command failed, we can get here only if the corresponding
                // expected error assertion was specified.
                ++NumAsserts;
            }
            ReportProgress();

            // output TPM IO to a text file for later processing
            if (Logger.LogTpmIo)
            {
                while (TpmIoWriter == null) try
                {
                    string ioLogPath;
                    if (Logger.LogPath != null)
                    {
                        ioLogPath = System.IO.Path.Combine(Logger.LogPath, "tpm_io.txt");
                    }
                    else
                    {
                        string fileName;
                        lock (this)
                        {
                            fileName = "tpm_io-" + DateTime.Now.ToString("yyyy-MMM-dd-HH");
                            if (PrevLogName == fileName)
                            {
                                fileName += "(" + ++PrevLogInstance + ")";
                            }
                            else
                            {
                                PrevLogName = fileName;
                                PrevLogInstance = 1;
                            }
                        }
                        fileName += ".txt";

#if TSS_MIN_API
                        ioLogPath = fileName;
#else
                        string docsPath = Environment.GetFolderPath(
                                            Environment.SpecialFolder.MyDocuments);
                        ioLogPath = System.IO.Path.Combine(docsPath, fileName);
#endif
                    }

                    TpmIoWriter = new StreamWriter(new FileStream(ioLogPath,
                                                                  FileMode.Create));
                    Logger.WriteToLog("Dumping TPM I/O to " + ioLogPath);
                }
                catch (Exception e)
                {
                    string message = "Failed to open the tpm_io.txt file for writing.\n" +
                                     "Error: " + e.Message;
                    Logger.WriteErrorToLog(message);
                }

                // get the test source code line that initiated the command
                string caller = "unknown";
#if !TSS_NO_STACK
                StackTrace trace = new StackTrace(true);
                StackFrame[] frames = trace.GetFrames();
                int frameCount = frames.Length;
                StackFrame f = null;
                // start at 1 to not count the currently executing function
                for (int j = 1; j < frameCount; j++)
                {
                    f = frames[j];
                    if (f.GetMethod().DeclaringType.Assembly == Logger.TestAssembly)
                    {
                        caller = f.GetFileName() + ":" + f.GetFileLineNumber();
                        break;
                    }
                }
#endif
                string commandCodeString = Enum.GetName(typeof(TpmCc), commandCode);
                string inString = "{MALFORMED COMMAND BUFFER}";
                string outString = "{MALFORMED RESPONSE BUFFER}";

                try { inString = CommandProcessor.ParseCommand(inBuf); }
                catch (Exception) { }
                try { outString = CommandProcessor.ParseResponse(commandCodeString, outBuf); }
                catch (Exception) { }

                lock (this)
                {
                    TpmIoWriter.WriteLine(commandCode);
                    TpmIoWriter.WriteLine(caller);

                    TpmIoWriter.WriteLine(">>>> Raw input");
                    TpmIoWriter.WriteLine(Globs.HexFromByteArray(inBuf));
                    TpmIoWriter.WriteLine(">>>> Raw output");
                    TpmIoWriter.WriteLine(Globs.HexFromByteArray(outBuf));

                    TpmIoWriter.WriteLine(">>>> Parsed input");
                    TpmIoWriter.WriteLine(inString);
                    TpmIoWriter.WriteLine(">>>> Parsed output");
                    TpmIoWriter.WriteLine(outString);

                    TpmIoWriter.WriteLine("-----------------------------------------");
                    TpmIoWriter.Flush();
                }
            }

            if (ChainedCallbacks != null)
            {
                ChainedCallbacks.PostCallback(inBuf, outBuf);
            }
        } // ICommandCallbacks.PostCallback


        static string[] KnownCommandCodes = Enum.GetNames(typeof(TpmCc));

        private void LogCommandExecution(TpmCc cc, TpmRc rc, TimeSpan executionTime)
        {
            NumCommands++;

            if(!KnownCommandCodes.Contains(cc.ToString()))
            {
                cc = TpmCc.None;
            }
            if (!CmdStats.ContainsKey(cc))
            {
                CmdStats.Add(cc, new CommandStats());
            }

            CommandStats stat = CmdStats[cc];
            if (rc == TpmRc.Success)
            {
                stat.NumSuccess++;
                stat.SuccessExecutionTime += executionTime;
            }
            else
            {
                stat.NumFailures++;
                stat.FailureExecutionTime += executionTime;
                if (!stat.FailureResponses.Contains(rc))
                {
                    stat.FailureResponses.Add(rc);
                }
            }
            // No need to update stat.CallerTests here. Current test will be logged
            // in the command execution statistics in the TestCompleted() notification.
        }

        // Stores in the test context an arbitrary set of parameters associated with
        // the current test phase. In case of a failure the parameters are included
        // in the error report. Each parameter is printed on a separate line.
        public void ReportParams(params object[] parms)
        {
            CurTestPhaseParms = parms;
        }

        string LastExceptionInfo = "";

        internal string GetLastExceptionInfo()
        {
            return LastExceptionInfo;
        }

        internal string FullTestCaseName (string assertName)
        {
            return  CurTestMethod + 
                    (string.IsNullOrEmpty(assertName) ? "" : ":" + assertName);
        }

        public void ReportWarning(string s)
        {
            string location = null;
#if !TSS_NO_STACK
            StackTrace callerStack = new StackTrace(true);
            StackFrame[] locationFrames = TestContext.GetFramesToReport(callerStack);
            location = locationFrames.Length == 0
                            ? TestLogger.GetLocationAsText(callerStack.GetFrames(), "\n")
                            : TestLogger.GetLocationAsText(locationFrames, "\n");

            Logger.WriteErrorToLog("Warning: " + s, ConsoleColor.Cyan);
            Logger.WriteErrorToLog(location, ConsoleColor.Yellow);
#endif
            var t = new TestCaseInfo
            {
                TestCase = FullTestCaseName(null),
                Message = s,
                Parms = null,
                Location = location,
                StackTrace = null,
                RngSeed = CurRngSeed
            };
            TestsWithWarnings.Add(t);
        }

        object[] ConcatParams(object[] p1, object[] p2)
        {
            return    p1 == null || p1.Length == 0 ? p2
                    : p2 == null || p2.Length == 0 ? p1
                    : p1.Concat(p2).ToArray();
        }

        internal void ReportTestFailure(bool testException, string assertName, Object[] parms)
        {
            if (!ReportErrors)
                return;

            string fullMsg;
#if !TSS_NO_STACK
            StackTrace callerStack = null;
#endif
            string fullTestCase = FullTestCaseName(assertName);
            string fullStackTrace = "";

            if (!string.IsNullOrEmpty(assertName))
            {
                if (FailedAssertions.ContainsKey(fullTestCase))
                {
                    FailedAssertions[fullTestCase]++;
                    return;
                }
                FailedAssertions.Add(fullTestCase, 1);
            }

            if (testException)
            {
                Debug.Assert(parms.Length == 1);
                TestExceptionInfo ei = TestLogger.ParseException((Exception)parms[0]);
                fullMsg = ei.message;
#if !TSS_NO_STACK
                callerStack = ei.callerStack;
#endif
                fullStackTrace = ei.fullStack;
                parms = ConcatParams(CurTestPhaseParms,
                                     ei.cmdParms == null ? null
                                                         : new object[] {ei.cmdParms});
            }
            else
            {
                fullMsg = "Assertion " + assertName + " failed";
#if !TSS_NO_STACK
                callerStack = new StackTrace(true);
                fullStackTrace = TestLogger.GetLocationAsText(callerStack.GetFrames(), "\n");
                parms = ConcatParams(parms, CurTestPhaseParms);
#endif
            }
            string location = "";
#if !TSS_NO_STACK
            StackFrame[] locationFrames = GetFramesToReport(callerStack);
            location = locationFrames.Length == 0 ? fullStackTrace
                                  : TestLogger.GetLocationAsText(locationFrames, "\n");
#endif

            if (testException)
            {
                LastExceptionInfo = fullMsg + "\n\n" + location;
            }

            var t = new TestCaseInfo {
                            TestCase = fullTestCase,
                            Message = fullMsg,
                            Parms = parms,
                            Location = location,
                            StackTrace = fullStackTrace,
                            RngSeed = CurRngSeed,
                            RepeatedFailures = 1
                        };
            FailedTests.Add(t);

            int pos = fullMsg.IndexOf("Details:");
            string msg;
            string details = "";

            if (pos != -1)
            {
                msg = fullMsg.Substring(0, pos);
                details = fullMsg.Substring(pos) + "\n";
            }
            else
                msg = fullMsg + "\n";

            details += TestLogger.GetParamsList(parms, "\n");

            Logger.ThreadSafePrint(msg, ConsoleColor.Red);
            Logger.ThreadSafePrint("To reproduce use option: -seed "
                                   + CurRngSeed + "\n", ConsoleColor.Gray);
            if (details != "")
            {
                Logger.ThreadSafePrint(details, ConsoleColor.DarkYellow);
            }
            Logger.ThreadSafePrint(location, ConsoleColor.Yellow);
        } // ReportTestFailure

        public void ReportTestException(Exception e)
        {
            ReportTestFailure(true, null, new object[] {e});
        }

        public void Assert(string label, bool success, params Object[] parms)
        {
            ++NumAsserts;
            if (!success)
            {
                ReportTestFailure(false, label, parms);
                CurTestStatus = TestStatus.Failed;
                throw new TssAssertException();
            }
            ReportProgress();
        }

        static object[] Gather(object parm1, object parm2, object[] parms)
        {
            return new object[]{ parm1, parm2 }.Concat(parms).ToArray();
        }
    
        public void AssertEqual<T>(string label, T val1, T val2,
                                     params Object[] parms)
        {
            Assert(label, val1.Equals(val2), Gather(val1, val2, parms));
        }

        public void AssertNotEqual<T>(string label, T val1, T val2,
                                        params Object[] parms)
        {
            Assert(label, !val1.Equals(val2), Gather(val1, val2, parms));
        }

        public void AssertEqual(string label, byte[] arr1, byte[] arr2,
                                  params Object[] parms)
        {
            Assert(label, Globs.ArraysAreEqual(arr1, arr2), Gather(arr1, arr2, parms));
        }

        public void AssertNotEqual(string label, byte[] arr1, byte[] arr2,
                                     params Object[] parms)
        {
            Assert(label, !Globs.ArraysAreEqual(arr1, arr2), Gather(arr1, arr2, parms));
        }

        [Obsolete("Use methods Assert, AssertEqual, and AssertNotEqual instead", false)]
        public void Test(string testCase, bool success, params Object[] parms)
        {
            Assert(testCase, success, parms);
        }

#if !TSS_NO_STACK
        // Heuristics to return a suitable frame to report as part of an error report.
        // Returns the first entity in the stack with a tester-attribute.
        public static StackFrame[] GetFramesToReport(StackTrace s)
        {
            if (s == null)
            {
                return new StackFrame[0];
            }

            StackFrame[] stackFrames = s.GetFrames();
            if (stackFrames == null)
            {
                return new StackFrame[0];
            }
            var framesToReport = new List<StackFrame>();
            bool reportThisFrame = false;

            for (int j = 0; j < stackFrames.Length; j++)
            {
                StackFrame f = stackFrames[j];
                MethodBase b = f.GetMethod();
                MethodInfo m = b is MethodInfo ? (MethodInfo)b : null;
                if (m == null)
                {
                    Debug.Assert(!reportThisFrame);
                    continue;
                }
                if (reportThisFrame)
                {
                    framesToReport.Add(f);
                }
                if (Globs.GetAttr<TestAttribute>(f.GetMethod()) != null)
                {
                    if (!reportThisFrame)
                    {
                        framesToReport.Add(f);
                    }
                    break;
                }
                reportThisFrame = Globs.GetAttr<TpmCommandAttribute>(f.GetMethod()) != null
                                  || f.GetMethod().ToString().Contains("Void Test(");
            }
            return framesToReport.ToArray();
        }
#endif // TSS_NO_STACK

        //
        // Test attributes validation
        //
        bool TestIsThreadSafe = true;
        bool TestWithinMinTpmProfile = true;
        bool TestUsesPlatformAuth = false;
        NecessaryPrivilege MaximumPrivilege = NecessaryPrivilege.User;

        private void LogTestAttributes(TpmSt sessionTag, TpmCc command)
        {
            if (!TestCategorizer.CommandDefined(command))
                return;

            if (sessionTag.Equals(TpmSt.Null))
            {
                TestIsThreadSafe = false;
                TestWithinMinTpmProfile = false;
                return;
            }

            bool threadSafe = TestCategorizer.GetThreadSafety(command);
            if (!threadSafe) TestIsThreadSafe = false;
            if (!TestCategorizer.InProfile0(command))
            {
                TestWithinMinTpmProfile = false;
                return;
            }
            // else is P0 command.  What privileges are needed?
            NecessaryPrivilege priv = TestCategorizer.GetNecessaryPrivileges(command);
            if (priv > MaximumPrivilege)
                MaximumPrivilege = priv;
        }

        private void ValidateHandleUsage(TpmCc command, byte[] inBuf)
        {
            CommandInfo commInfo = Tpm2.CommandInfoFromCommandCode(command);
            if (commInfo.HandleCountIn == 0)
                return;
            // else see if any of the inHandles are TpmRh.Platform
            CrackedCommand cc = CommandProcessor.CrackCommand(inBuf);
            TpmHandle[] handles = cc.Handles;
            foreach (TpmHandle h in handles)
            {
                if (h == TpmRh.Platform) TestUsesPlatformAuth = true;
            }
        }
        
        internal void ResetAttributeCollection(Tpm2 tpm)
        {
            TestIsThreadSafe = true;
            TestWithinMinTpmProfile = true;
            MaximumPrivilege = NecessaryPrivilege.User;
            TestUsesPlatformAuth = false;
            TpmPassThroughDevice dev = (TpmPassThroughDevice)tpm._GetUnderlyingDevice();
            // reset the power-cycle dirty bit
            dev.GetPowerCycleDirtyBit();
        }

        private void SetColorCritical(bool isCritical)
        {
            if (isCritical)
            {
                Console.ForegroundColor = ConsoleColor.Red;
            }
            else
            {
                Console.ForegroundColor = ConsoleColor.Green;
            }
        }

        // This routine checks that the observed test behavior matches the attributes.
        // It is called during a -validate pass on a full TPM simulator
        internal void ValidateTestAttributeCollection(MethodInfo test, Tpm2 tpm)
        {
            var attr = Globs.GetAttr<TestAttribute>(test);
            // check that the cumulative attributes match the claimed attributes
            if (attr.SpecialNeeds.HasFlag(Special.NotThreadSafe) == TestIsThreadSafe)
            {
                string s = "<Benign>";
                if (!TestIsThreadSafe)
                {
                    // It is safe to say you are not thread safe if you are not (and
                    // there are commands that the test harness does not understand
                    // that might lead you to be not thread safe
                    s = "<Critical - must be fixed>";
                    SetColorCritical(true);
                }
                else
                {
                    SetColorCritical(false);
                }
                Logger.WriteErrorToLog("\nThread safety attribute does not match " +
                                       "observed behavior for {0} {1}", test.Name, s);
            }

            bool PowerCycleWasIssued = ((TpmPassThroughDevice)tpm._GetUnderlyingDevice())
                                                                .GetPowerCycleDirtyBit();
            if (attr.SpecialNeeds.HasFlag(Special.PowerControl) != PowerCycleWasIssued)
            {
                SetColorCritical(PowerCycleWasIssued);
                Logger.WriteErrorToLog("\nPower-cycle attribute does not match observed behavior");
            }
            if ((attr.CommProfile == Profile.MinTPM) != TestWithinMinTpmProfile)
            {
                SetColorCritical(attr.CommProfile == Profile.MinTPM);
                string profile = attr.CommProfile.ToString();
                Logger.WriteErrorToLog("\nClaimed profile {0} but this does not match " +
                                       "observed behavior for {1}", profile, test.Name);
            }
            // change privUsed and privStated into a number so that we can compare 
            // bigger numbers are more privileged
            int privUsed, privStated;
            switch (MaximumPrivilege)
            {
                case NecessaryPrivilege.User: privUsed = 0; break;
                case NecessaryPrivilege.Admin: privUsed = 1;break;
                case NecessaryPrivilege.Special: privUsed = 2; break;
                case NecessaryPrivilege.Debug: privUsed = 3; break;
                default: throw new Exception("tester fault");
            }
            switch (attr.Privileges)
            {
                case Privileges.StandardUser:  privStated = 0; break;
                case Privileges.Admin: privStated = 1; break;
                case Privileges.Special: privStated = 3; break;
                default: throw new Exception("badly attributed test.  Need a stated privilege");
            }

            bool maxPrivOk = (privStated >= privUsed);
            bool privilegesMatch = privUsed==privStated || (privStated==3 && privUsed==2);
            /*
            switch (MaximumPrivilege)
            {
                case NecessaryPrivilege.User:
                    if (attr.Privileges != PrivilegesNeeded.StandardUser) maxPrivOk = false;
                    break;
                case NecessaryPrivilege.Admin:
                    if (attr.Privileges != PrivilegesNeeded.Admin) maxPrivOk = false;
                    break;
                case NecessaryPrivilege.Special:
                    if (attr.Privileges != PrivilegesNeeded.Special) maxPrivOk = false;
                    break;
                default:
                    throw new Exception("");
            }
            // maxPriv is only defined for commands within the MinTPM profile
            */
            if (!privilegesMatch)
            {
                SetColorCritical(!maxPrivOk);
                Logger.WriteErrorToLog("\nPrivilege used is {0} but stated {1} for test {2}",
                                       MaximumPrivilege, attr.Privileges, test.Name);
                if (!maxPrivOk)
                    Logger.WriteErrorToLog("critical - must fix.");
            }
            if (TestUsesPlatformAuth)
            {
                // not allowed for 
                if (attr.CommProfile == Profile.MinTPM)
                {
                    SetColorCritical(true);
                    string profile = attr.CommProfile.ToString();
                    Logger.WriteErrorToLog("\nClaimed profile {0} but used PlatformAuth: {1}",
                                           profile, test.Name);
                }
            }
            Console.ResetColor();
        }
    } // class LibTesterContext

} // namespace Tpm2TestSuite

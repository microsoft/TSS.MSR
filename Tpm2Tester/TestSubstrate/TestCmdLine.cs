/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.Diagnostics;
using System.IO;
using System.Threading;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Tpm2Lib;

namespace Tpm2Tester
{

    class TesterCmdLine
    {
        // Target test framework
        private TestFramework Target;

        // Shortcut to the corresponding target framework member
        private TestConfig TestCfg;

        // Command line arguments.
        private string[] Args;

        // Index of an element in Args to process next.
        private int CurArg = 0;

        //
        // Intermediate lists of test names used to build the set of tests to run 
        //
        private List<string> TestsToExclude = new List<string>();
        private List<string> TestsToInclude = new List<string>();


        internal TesterCmdLine(TestFramework framework, string[] args)
        {
            Target = framework;
            TestCfg = Target.TestCfg;
            Args = args;
        }

        static SortedSet<Option> DefinedOptions = new SortedSet<Option> {
            new Option("help", "Help", "Print the usage help",
                (TesterCmdLine cl) =>
                {
                    cl.PrintHelp();
                    cl.TestCfg.TestsToRun = null;
                    cl.TestCfg.Verbose = false;
                    return true;
                }),
            new Option("?", "Help", "Print the usage help",
                (TesterCmdLine cl) =>
                {
                    cl.PrintHelp();
                    cl.TestCfg.TestsToRun = null;
                    cl.TestCfg.Verbose = false;
                    return true;
                }),
            new Option("tests", "Help", "List all test case names",
                (TesterCmdLine cl) =>
                {
                    cl.PrintHelp(HelpItem.Tests);
                    cl.TestCfg.TestsToRun = null;
                    cl.TestCfg.Verbose = false;
                    return true;
                }),
            new Option("profiles", "Help", "List all test profile names",
                (TesterCmdLine cl) =>
                {
                    cl.PrintHelp(HelpItem.Profiles);
                    cl.TestCfg.TestsToRun = null;
                    cl.TestCfg.Verbose = false;
                    return true;
                }),


            new Option("device", "Target device", "Target TPM device type (tcp, tbs, tbsraw, dll, rmsim)",
                (TesterCmdLine cl) =>
                {
                    string device = "";
                    if (cl.NextParam(ref device))
                    {
                        if (Enum.TryParse<TpmDeviceType>(device, false,
                                                          out cl.TestCfg.DeviceType))
                        {
                            if (   cl.TestCfg.DeviceType == TpmDeviceType.tbs
                                || cl.TestCfg.DeviceType == TpmDeviceType.tbsraw)
                            {
                                cl.TestCfg.DisabledTests.SpecialNeeds |= Special.PowerControl
                                                             | Special.PhysicalPresence;
                            }
                            return true;
                        }
                        cl.Target.WriteErrorToLog("Unrecognized TPM device.");
                    }
                    Console.WriteLine("Supported devices: " + GetSupportedDeviceNamesList());
                    return false;
                }),
#if !TSS_NO_TCP
            new Option("address", "Target device", "HostName:Port of simulator or TCP proxy"
                                                 + "(for 'tcp' TPM device type)",
                (TesterCmdLine cl) =>
                {
                    string addr = "";
                    if (!cl.NextParam(ref addr))
                        return false;
                    string[] hostAddr = addr.Split(new char[] { ':' });
                    int portNum = 0;
                    if (hostAddr.Length != 2 || !Int32.TryParse(hostAddr[1], out portNum))
                    {
                        cl.Target.WriteErrorToLog(
                            "TPM TCP/IP server should have format HostName:PortNumber");
                        return false;
                    }
                    cl.TestCfg.TcpHostName = hostAddr[0];
                    cl.TestCfg.TcpServerPort = portNum;
                    return true;
                }),
            new Option("tpm", "Target device", "Path to a TPM simulator executable to be started "
                                             + "by Tpm2Tester (for 'tcp' TPM device type)",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.DeviceType = TpmDeviceType.tcp;
                    return cl.NextParam(ref cl.TestCfg.TpmPath);
                }),
            new Option("restart", "Target device",
                       "Restart TPM simulator if it dies or becomes unresponsive (with -tpm and -fuzz)",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.TpmAutoRestart = true;
                    return true;
                }),
#endif //!TSS_NO_TCP
            new Option("dllpath", "Target device", "Path to a TPM simulator DLL (for 'dll' TPM device type;"
                                                 + "also may be used by tests directly)",
                (TesterCmdLine cl) =>
                {
                    return cl.NextParam(ref cl.TestCfg.TpmDllPath);
                }),
            new Option("noinit", "Target device", "Do not initialize TPM and do not clear its NV memory",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.DoInit = false;
                    cl.TestCfg.ClearNv = false;
                    return true;
                }),
            new Option("stopTpm", "Target device", "Signal TPM simulator to stop upon test session completion",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.StopTpm = true;
                    // If this is the only option, suppress default test set execution.
                    if (cl.Args.Length == 1)
                    {
                        cl.TestCfg.TestsToRun = null;
                    }
                    return true;
                }),


            new Option("mins", "Mode", "Set duration of the test session in minutes",
                (TesterCmdLine cl) =>
                {
                    int mins = 0;
                    if (!cl.NextIntParam(ref mins))
                        return false;
                    cl.TestCfg.TestEndTime = DateTime.Now + new TimeSpan(0, mins, 0);
                    return true;
                }),
            new Option("fast", "Mode", "Make TPM simulator use RSA key caching (speeds up tests)",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.UseKeyCache = true;
                    return true;
                }),
            new Option("stress", "Mode", "Stress mode - tests are executed in parallel (see -threads)",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.StressMode = true;
                    cl.TestCfg.DisabledTests.SpecialNeeds |= Special.NotThreadSafe
                        | Special.PowerControl | Special.PhysicalPresence | Special.Locality;
                    return true;
                }),
            new Option("shuffle", "Mode", "Shuffle the order of individual test cases (default for stress mode)",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.Shuffle = true;
                    return true;
                }),
            new Option("debugNv", "Mode", "For each command requiring NV attempt to execute it with NV disabled (TPM simulator only)",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.TestNvAvailable = true;
                    return true;
                }),
            new Option("S3", "Mode", "Include random S3 events (only 'rmsim' device type)",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.TestS3 = true;
                    return true;
                }),
            new Option("stdUser", "Mode", "Running as a standard user",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.RunAsStandardUser = true;
                    return true;
                }),

            new Option("seed", "Parameters", "Set seed for RNG used by the test substrate",
                (TesterCmdLine cl) =>
                {
                    return cl.NextParam(ref cl.TestCfg.RngSeed);
                }),
            new Option("randSeed", "Parameters",
                       "Randomly seed the test session (otherwise the substrate uses the same seed every run)",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.SeededRng = false;
                    return true;
                }),
            new Option("threads", "Parameters", "Set number of threads (with '-stress')",
                (TesterCmdLine cl) =>
                {
                    return cl.NextIntParam(ref cl.TestCfg.NumThreads);
                }),
            new Option("params", "Parameters", "Arbitrary string w/o spaces to be passed to a " +
                                               "single test specified with '-tests' option",
                (TesterCmdLine cl) =>
                {
                    return cl.NextParam(ref cl.TestCfg.TestParams);
                }),
            new Option("daTime", "Parameters", "Overrides default DA recovery time (3 sec) used by tests",
                (TesterCmdLine cl) =>
                {
                    string recoveryTime = "";
                    if (!cl.NextParam(ref recoveryTime))
                        return false;
                    if (!uint.TryParse(recoveryTime, out cl.TestCfg.DA_RecoveryTime) ||
                        cl.TestCfg.DA_RecoveryTime == 0)
                    {
                        cl.Target.WriteErrorToLog("Option '-daTime' needs a positive integer value");
                        return false;
                    }
                    return true;
                }),
            new Option("validate", "Parameters", "Validate that tests obey threading and other rules",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.TestValidationRun = true;
                    return true;
                }),
            new Option("bleeding", "Parameters",
                       "Include tests that are known to cause problems with released TPM versions",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.Bleeding = true;
                    return true;
                }),

            new Option("exclude", "Selectors", "Exclude the following (until the end) tests from the run" +
                                               "(alternatively use '!' or '~' before test or profile name)",
                (TesterCmdLine cl) =>
                {
                    cl.AddTestsInto(cl.TestsToExclude, true, true);
                    // Do not fail if any of the tests to exclude do not exist
                    return true;
                }),
            new Option("continue", "Selectors", "Restart the last test run at the non-completed test",
                (TesterCmdLine cl) =>  {
                    if(!File.Exists(cl.Target.RemainingTestsFile))
                    {
                        cl.Target.WriteErrorToLog("The file " + cl.Target.RemainingTestsFile + " does not exist.");
                        return false;
                    }
                    string testsLine = File.ReadAllText(cl.Target.RemainingTestsFile, Encoding.ASCII);
                    string[] tests = testsLine.Split(new char[] {' '});
                    foreach(string test in tests)
                    {
                        if (test == "") break;
                        if(!cl.AddTest(cl.TestsToInclude, test, false))
                        {
                            return false;
                        }
                    }
                    return true;
                }),

            new Option("nohtml", "Reporting", "Do not show HTML results",
                (TesterCmdLine cl) =>
                {
#if !WLK
                    cl.TestCfg.ShowHTML = false;
#endif
                    return true;
                }),
            new Option("quiet", "Reporting", "Minimal output",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.Verbose = false;
                    return true;
                }),
            new Option("silent", "Reporting", "No output to console except for errors and warnings",
                (TesterCmdLine cl) =>
                {
                    Console.SetOut(StreamWriter.Null);
                    return true;
                }),
            new Option("dumpConsole", "Reporting", "Dump console output to console.txt",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.DumpConsole = true;
                    return true;
                }),
            new Option("dumpIo", "Reporting", "Output TPM commands and responses to tpm_io.txt",
                (TesterCmdLine cl) =>
                {
                    cl.Target.MainTestLogger.LogTpmIo = true;
                    return true;
                }),
            new Option("crashReports", "Reporting", "A directory to receive fuzz crash reports",
                (TesterCmdLine cl) =>
                {
                    if (!cl.NextParam(ref cl.TestCfg.FuzzCrashesDirectory))
                    {
                        return false;
                    }
                    string sep = Path.DirectorySeparatorChar.ToString();
                    string separator = sep;
                    if ( cl.TestCfg.FuzzCrashesDirectory != "" &&
                        !cl.TestCfg.FuzzCrashesDirectory.EndsWith(sep))
                    {
                        cl.TestCfg.FuzzCrashesDirectory += separator;
                    }
                    return true;
                }),
            new Option("checkin", "Reporting",
                       "Notify of tester startup/shutdown with a file in crashreports",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.GenerateStartupCheckin = true;
                    return true;
                }),
            new Option("log", "Reporting", "Log IO between TPM and tester to files in the given directory",
                (TesterCmdLine cl) =>
                {
                    return cl.NextParam(ref cl.TestCfg.TransportLogsDirectory);
                }),
            new Option("dbg", "Reporting", "Enable debug output",
                (TesterCmdLine cl) =>
                {
                    Dbg.Enabled = true;
                    return true;
                }),

            new Option("fuzz", "Fuzzing", "Fuzz the TPM",
                (TesterCmdLine cl) =>
                {
                    cl.Target.FuzzMode = true;
                    return true;
                }),
            new Option("fuzzCmd", "Fuzzing", "Fuzz only command with this name (no 'TMP2_' prefix)",
                (TesterCmdLine cl) =>
                {
                    return cl.NextParam(ref cl.Target.FuzzCmd);
                }),
            new Option("fuzzCount", "Fuzzing", "Debug break after this number of fuzzings. " +
                                               "Used with -fuzzcmd or -breakcount.",
                (TesterCmdLine cl) =>
                {
                    return cl.NextIntParam(ref cl.Target.FuzzCountToBreak);
                }),
            new Option("breakCmd", "Fuzzing", "Command, to which -fuzzcount applies",
                (TesterCmdLine cl) =>
                {
                    return cl.NextParam(ref cl.Target.BreakCmd);
                }),
            new Option("breakCount", "Fuzzing", "Number of fuzzed commands to skip before " +
                                     "-fuzzcount applies",
                (TesterCmdLine cl) =>
                {
                    return cl.NextIntParam(ref cl.Target.BreakCount);
                }),

            new Option("tpmInfo", "Miscelaneous", "Dumps TPM information",
                (TesterCmdLine cl) =>
                {
                    cl.TestCfg.DumpTpmInfo = true;
                    cl.TestCfg.TestsToRun = null;
                    cl.TestCfg.Verbose = false;
                    return true;
                }),
            new Option("parseIn", "Miscelaneous", "Interpret byte-string as TPM command",
                (TesterCmdLine cl) =>
                {
                    // Accepts multi-line snapshots of the VS memory window with
                    // the first (address) and last (ASCII dump) columns removed.
                    Console.WriteLine("Paste one or more lines of hex values " +
                                      "(possibly separated by spaces) into console." +
                                      "Empty line indicates end of input:\n");
                    string hex = ReadMultilineInput();
                    string output = CommandProcessor.ParseCommand(hex);
                    Console.WriteLine(output);
                    cl.TestCfg.TestsToRun = null;
                    return true;
                }),
            new Option("parseOut", "Miscelaneous", "Interpret byte-string as a TPM response",
                (TesterCmdLine cl) =>
                {
                    string commandName = "";
                    if (!cl.NextParam(ref commandName))
                    {
                        cl.Target.WriteErrorToLog("Command name must be provided. " +
                                                    "E.g. CreatePrimary");
                        return false;
                    }
#if false
                    Console.WriteLine("Paste hex values into console as a single hex string");
                    string hex = Console.ReadLine();
#else
                    string hex = ReadMultilineInput();
#endif
                    string output = CommandProcessor.ParseResponse(commandName, hex);
                    Console.WriteLine(output);
                    cl.TestCfg.TestsToRun = null;
                    return true;
                })
        }; // static SortedSet<Option> DefinedOptions

        static TestProfile[] DefinedProfiles = new TestProfile[]
        {
            new TestProfile("All",
                (TesterCmdLine cl) => {
                    cl.TestCfg.DisabledTests.Category &= ~Category.Slow;
                    return Globs.ConvertAll(cl.Target.AllTests, item => item.Name);
                }),
            new TestProfile("TbsAdmin",
                (TesterCmdLine cl) =>
                    cl.GetTestListFromAttrs(attr => attr.Privileges.HasFlag(Privileges.Admin))
                ),
            new TestProfile("TbsStandardUser",
                (TesterCmdLine cl) => {
                    cl.TestCfg.RunAsStandardUser = true;
                    return cl.GetTestListFromAttrs(attr =>
                                            attr.Privileges.HasFlag(Privileges.StandardUser));
                }),
            new TestProfile("MinTpmTbsAdmin",
                (TesterCmdLine cl) => cl.GetTestListFromAttrs(attr =>
                                            attr.CommProfile == Profile.MinTPM &&
                                            attr.Privileges.HasFlag(Privileges.Admin) &&
                                           !attr.Category.HasFlag(Category.WLK))
                ),
            //
            // Get tests that use MinTpm, don't need power control, PPI and aren't
            // WLK specific tests (e.g. HW Interface Test).
            // If raw mode specified then get tests that don't require RM. Otherwise,
            // skip tests that require raw mode
            //
            new TestProfile("NoTRM",
                (TesterCmdLine cl) => cl.GetTestListFromAttrs(attr =>
                                            attr.SpecialNeeds.HasFlag(Special.NoTRM) &&
                                           !attr.Category.HasFlag(Category.WLK))
                ),
            //
            // Get HW Interface Tests
            //
            new TestProfile("WlkHWInterfaceTests",
                (TesterCmdLine cl) => cl.GetTestListFromAttrs(attr =>
                                            (attr.CommProfile == Profile.MinTPM ||
                                             attr.CommProfile == Profile.TPM20) &&
                                            !attr.Privileges.HasFlag(Privileges.Special) &&
                                             attr.Category.HasFlag(Category.WLK))
                )
        }; // static TestProfile[] DefinedProfiles


        static string ReadMultilineInput()
        {
            string s = "";
            do {
                string line = Console.ReadLine();
                if (line == null || line == "")
                    break;
                s += " " + line;
            } while (true);

            return s;
        }

        internal bool Parse()
        {
            while (CurArg < Args.Length)
            {
                bool exclude = false;
                string curOpt = Args[CurArg++];
                Option opt = null;

                if (curOpt[0] == '-' || curOpt[0] == '/')
                {
                    curOpt = curOpt.TrimStart('-', '/').ToLower();
                    var opts = DefinedOptions.Where(o => o.Tag.ToLower() == curOpt);
                    if (opts.Count() == 0)
                    {
                        opts = DefinedOptions.Where(o => o.Tag.ToLower().StartsWith(curOpt));
                    }
                    if (opts.Count() == 0)
                    {
                        Target.WriteErrorToLog("Unrecognized option '" + Args[CurArg - 1] + "'.");
                        PrintHelp(HelpItem.Options);
                        return false;
                    }
                    opt = opts.First();
                }
                else
                {
                    // This is not an option, try to interpret it as a test case or profile name
                    if (curOpt[0] == '!' || curOpt[0] == '~')
                    {
                        exclude = true;
                        curOpt = curOpt.Substring(1);
                    }
                    curOpt = curOpt.TrimEnd(',');

                    if (ProcessProfile(curOpt, exclude) ||
                        AddTest(exclude ? TestsToExclude : TestsToInclude, curOpt, exclude))
                    {
                        continue;
                    }
                    Target.WriteErrorToLog("Unrecognized test case or profile name '" + Args[CurArg - 1] + "'.");
                    PrintHelp(HelpItem.Tests | HelpItem.Profiles);
                    return false;
                }
                if (!opt.Processor(this))
                {
                    Target.WriteErrorToLog("Failed to parse command line");
                    return false;
                }
            }
            if (TestCfg.TestParams != null && TestCfg.TestsToRun.Count > 1)
            {
                Target.WriteErrorToLog("Option '-params' is ignored when multiple " +
                                          "tests are specified.");
                TestCfg.TestParams = null;
            }
            return true;
        }

        void ClearDefaultProfile ()
        {
            if (TestCfg.TestsToRun == Target.AllTestNames)
            {
                TestCfg.TestsToRun = new List<string>();
            }
        }

        bool AddTestsInto(List<string> testList, bool exclude,
                          bool profilesAllowed = false)
        {
            string errorInfo = null;
            if (CurArg == Args.Length)
            {
                errorInfo = "";
            }
            else
            {
                int origIdx = CurArg;
                while (CurArg < Args.Length &&
                       ((profilesAllowed && ProcessProfile(Args[CurArg], exclude)) ||
                        AddTest(testList, Args[CurArg], exclude)))
                {
                    ++CurArg;
                }
                if (origIdx < CurArg)
                {
                    // Some of the parameters have been recognized as test names.
                    return true;
                }
                errorInfo = "valid ";
            }
            Target.WriteErrorToLog("No {0}test names specified for option '{1}'",
                                      errorInfo, Args[CurArg - 1]);
            return false;
        }

        bool AddTest(List<string> testList, string testName, bool exclude)
        {
            testName = testName.ToLower();
            var tests = Target.AllTestNames.FindAll(t =>
                                                    t.ToLower() == testName ||
                                                    t.ToLower() == ("test" + testName));
            if (tests.Count() == 0)
            {
                tests = Target.AllTestNames.FindAll(t =>
                                            t.ToLower().StartsWith(testName) ||
                                            t.ToLower().StartsWith("test" + testName));
                if (tests.Count() == 0)
                {
                    if (!testName.StartsWith("test") && testName.Length > 3)
                    {
                        tests = Target.AllTestNames.FindAll(t =>
                                                        t.ToLower().Contains(testName));
                    }
                    if (tests.Count() == 0)
                    {
                        // TODO: Search using lexicographical distance
                        return false;
                    }
                }
            }
            foreach (var test in tests)
            {
                testList.Add(test);
            }
            if (!exclude)
            {
                // An explicitly specified test was added, so clear the default profile
                ClearDefaultProfile();
            }
            return true;
        }

        public static string ListToString(IEnumerable<string> list)
        {
            string s = "";
            foreach (string n in list)
            {
                s = s + n + ", ";
            }
            s = s.TrimEnd(',', ' ');
            return s;
        }

        bool NextIntParam(ref int n)
        {
            string value = "";
            if (!NextParam(ref value))
            {
                return false;
            }
            if (!Int32.TryParse(value, out n))
            {
                Target.WriteErrorToLog("Integer parameter expected for option '" + Args[CurArg - 2] + "'");
                return false;
            }
            return true;
        }

        bool NextParam(ref string par, bool optional = false)
        {
            if (CurArg < Args.Length && !Args[CurArg].StartsWith("-"))
            {
                par = Args[CurArg++];
            }
            else
            {
                if (!optional)
                    Target.WriteErrorToLog("Missing parameter for option '" + Args[CurArg - 1] + "'");
                return false;
            }
            return true;
        }

        delegate List<string> GetTestCasesFromProfile(TesterCmdLine cl);

        class TestProfile
        {
            public string ProfileName;
            public GetTestCasesFromProfile GetProfileTests;

            public TestProfile(string name, GetTestCasesFromProfile p)
            {
                ProfileName = name;
                GetProfileTests = p;
            }
        }

        bool ProcessProfile(string profileName = null, bool exclude = false)
        {
            bool softCheck = profileName != null;
            if (softCheck || NextParam(ref profileName))
            {
                profileName = profileName.ToLower();

                var profMatch = DefinedProfiles.Where(p => p.ProfileName.ToLower() == profileName);
                if (profMatch.Count() == 0)
                {
                    profMatch = DefinedProfiles.Where(p => p.ProfileName.ToLower().StartsWith(profileName));
                }
                if (profMatch.Count() != 0)
                {
                    var tests = profMatch.First().GetProfileTests(this);
                    if (exclude)
                    {
                        TestsToExclude.AddRange(tests);
                    }
                    else
                    {
                        // An explicitly specified profile was added, so clear the
                        // default profile if necessary
                        ClearDefaultProfile();
                        TestCfg.TestsToRun.AddRange(tests);
                    }
                    return true;
                }

                // Check if the requested profile is a test category name

                var categories = Enum.GetNames(typeof(Category));
                var catMatch = categories.Where(c => c.ToLower() == profileName);
                if (catMatch.Count() == 0)
                {
                    catMatch = categories.Where(c => c.ToLower().StartsWith(profileName));
                }

                foreach (var catName in catMatch)
                {
                    Category cat = Category.None;
                    if (!Enum.TryParse(catName, true, out cat))
                        Debug.Assert("Failed to parse test category name" == null);

                    var tests = Target.AllTestNames.Where(item => 
                                  Target.GetTestAttribute(item).Category.HasFlag(cat));
                    if (exclude)
                    {
                        TestsToExclude.AddRange(tests);
                        // Store info about disabled category
                        TestCfg.DisabledTests.Category |= cat;
                    }
                    else
                    {
                        // An explicitly specified profile was added, so clear the 
                        // default profile if necessary
                        ClearDefaultProfile();
                        TestCfg.TestsToRun.AddRange(tests);
                        // Prevent possible filtering out just included tests
                        TestCfg.DisabledTests.Category &= ~cat;
                    }
                    return true;
                }
                if (!softCheck)
                {
                    Target.WriteErrorToLog("Profile name is not recognized");
                }
            }
            if (!softCheck)
            {
                Console.WriteLine("Defined test profiles:\n" +
                                  GetDefinedProfileNamesList());
            }
            return false;
        }

        void FilterOutTests(List<string> testList, Predicate<string> hasAttr,
                            string msgIfDropped)
        {
            var droppedTests = new List<string>();
            TestCfg.TestsToRun = TestCfg.TestsToRun.Where(item => {
                bool drop = hasAttr(item);
                if (drop)
                    droppedTests.Add(item);
                return !drop;
            }).ToList();
            if (msgIfDropped != null && droppedTests.Count > 0 && TestCfg.Verbose)
            {
                Target.WriteErrorToLog("\nSome of the tests cannot be executed " +
                                          msgIfDropped, ConsoleColor.Cyan);
                Target.WriteToLog("These tests will be skipped: " +
                                     ListToString(droppedTests), ConsoleColor.DarkCyan);
            }
        }

        void FilterOutTests(List<string> testList, Category cat, string msgIfDropped)
        {
            FilterOutTests(testList, item =>
                    (Target.GetTestAttribute(item).Category & cat) != Category.None,
                    msgIfDropped);
            // Add the filtered category to the set of disabled test attributes
            TestCfg.DisabledTests.Category |= cat;
        }

        void FilterOutTests(List<string> testList, Special sp, string msgIfDropped)
        {
            FilterOutTests(testList, item =>
                    (Target.GetTestAttribute(item).SpecialNeeds & sp) != Special.None,
                    msgIfDropped);
            // Add the filtered category to the set of disabled test attributes
            TestCfg.DisabledTests.SpecialNeeds |= sp;
        }

        internal List<string> GenerateTestSet(Tpm2Device tpmDevice)
        {
            if (TestCfg.TestsToRun == Target.AllTestNames && TestCfg.Verbose)
            {
                Target.WriteToLog("\nNote: Slow tests will be skipped by default.\n" +
                                     "      To include them use profile 'All'.\n" +
                                     "      To run them separately use profile 'Slow'.");
            }
            if (!TestCfg.HasTRM)
            {
                // remove tests that need an TRM
                TestCfg.DisabledTests.SpecialNeeds |= Special.NeedsTpmResourceMgr;
                FilterOutTests(TestsToInclude, Special.NeedsTpmResourceMgr,
                               "without TPM Resource Manager");
            }
            if (TestCfg.TestNvAvailable)
            {
                // remove tests that are not debug-NV safe
                FilterOutTests(TestsToInclude, Special.NotDebugNvSafe,
                               "in NV debugging mode");
            }
            if (Target.FuzzMode)
            {
                // remove tests that are not fuzz-safe
                FilterOutTests(TestsToInclude, Special.NotFuzzSafe, "in fuzzing mode");
            }
#if !WLK
            // filter out interface tests.  
            // TODO: this will filter out interface tests running across TPMProxy,
            //  which might not be desired.
            FilterOutTests(TestsToInclude, Category.WLK, null);
#endif
            if (!Target.TpmCfg.IsImplemented(TpmAlgId.Ecc))
            {
                FilterOutTests(TestsToInclude, Category.Ecc,
                               "as the TPM does not support ECC");
            }

            if (!Target.MainTpm._GetUnderlyingDevice().ImplementsPhysicalPresence())
            {
                FilterOutTests(TestsToInclude, Special.PhysicalPresence,
                                "as the physical presence control is not available");
            }

            if (!Target.MainTpm._GetUnderlyingDevice().PlatformAvailable())
            {
                FilterOutTests(TestsToInclude, Special.PowerControl,
                               "as the power control is not available");
                FilterOutTests(TestsToInclude, Special.Locality,
                               "as the locality control is not available");
                FilterOutTests(TestsToInclude, Special.Platform,
                               "as the platform functionality is not available");
                if (TestCfg.DeviceType != TpmDeviceType.tbsraw)
                {
                    FilterOutTests(TestsToInclude, Special.NoTRM,
                                   "as the TBS is not in raw mode (device is not 'tbsraw')");
                    FilterOutTests(TestsToInclude, Category.Context,
                                   "as the context operations are not available");
                }
            }

            // Filter test set generated from a profile (if any)
            TestCfg.TestsToRun = TestCfg.TestsToRun.Where(item => {
                var attrs = Target.GetTestAttribute(item);
                return (attrs.CommProfile & TestCfg.DisabledTests.CommProfile) == Profile.None &&
                        (attrs.Privileges & TestCfg.DisabledTests.Privileges) == Privileges.None &&
                        (attrs.Category & TestCfg.DisabledTests.Category) == Category.None &&
                        (attrs.SpecialNeeds & TestCfg.DisabledTests.SpecialNeeds) == Special.None;
            }).ToList();

            TestCfg.TestsToRun.Sort();
            TestCfg.TestsToRun = TestCfg.TestsToRun.Concat(TestsToInclude).ToList();
#if false
            var GratuitousExcludeTests = TestsToExclude.Except(TestCfg.TestsToRun).ToList();
            if (GratuitousExcludeTests.Count > 0)
            {
                Target.WriteErrorToLog("The following tests are not on the run list and cannot be excluded: "
                                          + ListToString(GratuitousExcludeTests), ConsoleColor.Cyan);
            }
#endif
            TestCfg.TestsToRun = TestCfg.TestsToRun.Except(TestsToExclude).ToList();

            // sort so that RunAtEnd tests come last
            TestCfg.TestsToRun = TestCfg.TestsToRun.OrderBy(x =>
                    Target.GetTestAttribute(x).SpecialNeeds.HasFlag(Special.RunAtEnd))
                .ToList();

            if (TestCfg.Verbose)
            {
                Target.WriteToLog("\nTest Routines in current test run:\n" +
                                     ListToString(TestCfg.TestsToRun));
            }
            return TestCfg.TestsToRun;
        }

        public List<string> GetTestListFromAttrs(Func<TestAttribute, bool> fits)
        {
            return Globs.ConvertAll(Target.AllTests.Where(
                        item => fits(Globs.GetAttr<TestAttribute>(item))),
                        item => item.Name);
        }

        public static string GetSupportedDeviceNamesList()
        {
            return ListToString(Enum.GetNames(typeof(TpmDeviceType)));
        }

        string GetDefinedProfileNamesList()
        {
            return ListToString(Globs.ConvertAll(DefinedProfiles, item => item.ProfileName))
                   + ", " +
                   ListToString(Enum.GetNames(typeof(Category)));
        }

        enum HelpItem
        {
            Usage = 1,
            Options = 2,
            Tests = 4,
            Profiles = 8,
            All = Usage | Options | Tests | Profiles
        }

        void PrintHelp(HelpItem helpItems = HelpItem.All)
        {
            string LibTesterName =
#if TSS_MIN_API
                "TpmTest.exe";
#else
                Environment.GetCommandLineArgs()[0];
#endif
            if (helpItems.HasFlag(HelpItem.Usage))
            {
                Console.WriteLine("\nUsage:\n");
                Console.WriteLine(LibTesterName +
                " [-opt1 [param1] [-opt2 [param2] [...] [TestProfile | TestList]]]]\n\n" +
                "- Option qualifiers, test and test profile names are case-insensitive.\n" +
                "- Prefix 'Test' present in many test names may be dropped.\n" +
                "- A test or test profile name do not require a preceding option.\n" +
                "- A test or test profile name may be prepended with an exclusion\n" +
                "    modifier '!' or '~' (similar to C/C++ logical and bitwise negation\n" +
                "    operators correspondingly) to exclude the test or profile from\n" +
                "    execution, or with a dash (which is ignored).\n" +
                "- Any number of option qualifiers with their corresponding values,\n" +
                "    test and profile namess with or without exclusion modifier may\n" +
                "    be simultaneously specified on the same command line in any order.\n" +
                "\n");
            }

            if (helpItems.HasFlag(HelpItem.Options))
            {
                Console.WriteLine("Supported options:");
                foreach (Option opt in DefinedOptions)
                {
                    Console.WriteLine("  " + opt.Tag.PadRight(8) + "\t" + opt.Comment);
                }
                Console.WriteLine("\nSupported devices:\n" + GetSupportedDeviceNamesList());
            }

            if (helpItems == HelpItem.All)
                Console.WriteLine("\nTest profiles:");
            if (helpItems.HasFlag(HelpItem.Profiles))
                Console.WriteLine(GetDefinedProfileNamesList());

            if (helpItems == HelpItem.All)
                Console.WriteLine("\nTest cases:");
            if (helpItems.HasFlag(HelpItem.Tests))
                Console.WriteLine(ListToString(Target.AllTestNames.OrderBy(sel => sel)));
        }
    } // class CmdLine

    class Option : IComparable<Option>
    {
        public delegate bool ProcessParm(TesterCmdLine cl);
        public string Tag;
        public string Group;
        public string Comment;
        public ProcessParm Processor;

        public Option(string tag, string group, string comment, ProcessParm action)
        {
            Tag = tag;
            Group = group;
            Comment = comment;
            Processor = action;
        }

        int IComparable<Option>.CompareTo(Option rhs)
        {
            return string.Compare(Tag, rhs.Tag, true);
        }
    } // class CommandParms

} // namespace Tpm2TestSuite

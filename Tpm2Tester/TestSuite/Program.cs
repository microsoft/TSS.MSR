using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Tpm2Lib;
using Tpm2Tester;

namespace Tpm2TestSuite
{
    partial class Tpm2Tests
    {
        public static readonly AuthValue NullAuth = new AuthValue();

        // Primary object representing the test infrastructure
        internal static TestSubstrate Substrate;

        // Shortcut to the test configuration member of the Substrate object 
        internal static TestConfig TestCfg;

        // Shortcut to the TPM configuration member of the Substrate object
        internal static TpmConfig TpmCfg;

        static void Main(string[] args)
        {
            // Pass an instance of the calss implementing test methods
            Substrate = TestSubstrate.Create(args, new Tpm2Tests());
            if (Substrate == null)
            {
                Console.WriteLine("Failed to initialize Tpm2Tester framework (bad command line " +
                                  "or no test cases found in MyTestCases). Aborting...");
                return;
            }

            // Initialize the shortcuts to the Substrate members (just a convenience)
            TestCfg = Substrate.TestCfg;
            // Note that the members of TpmCfg are not initialized at this point yet.
            // But they will be when the test cases are invoked by the substrate later.
            TpmCfg = Substrate.TpmCfg;

            Substrate.RunTestSession();
        }
    }
}

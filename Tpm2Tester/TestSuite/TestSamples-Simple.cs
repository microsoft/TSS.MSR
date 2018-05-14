/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using System.Text;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Json;
using Tpm2Lib;
using Tpm2Tester;

//
// This file contains examples of TPM 2.0 test routines
//
// Note that the names of the namespace and class(es) containing the tests can be any.
//

namespace Tpm2TestSuite
{
    partial class Tpm2Tests
    {
        // A test case method must be marked with 
        [Test(Profile.TPM20, Privileges.StandardUser, Category.Misc, Special.None)]
        void TestRandom(Tpm2 tpm, TestContext testCtx)
        {
            // Check that the TPM returns the correct number of random bytes for various lengths
            testCtx.ReportParams("Test phase: GetRandom");
            for (int j = 0; j < TestConfig.NumIters; j++)
            {
                int numBytes = Substrate.RandomInt(TpmCfg.MaxDigestSize);
                byte[] rx = tpm.GetRandom((ushort)numBytes);
                testCtx.AssertEqual("CorrectNumBytes", rx.Length, numBytes);
            }

            // Check that the TPM can accept stir data up to test-defined maximum
            testCtx.ReportParams("Test phase: StirRandom");
            for (int j = 0; j < TestConfig.NumIters; j++)
            {
                byte[] toStir = Substrate.RandomBytes(Substrate.RandomInt(TpmCfg.MaxDigestSize));
                tpm.StirRandom(toStir);
                int numBytes = Substrate.RandomInt(TpmCfg.MaxDigestSize);
                byte[] rx = tpm.GetRandom((ushort)numBytes);
                testCtx.AssertEqual("CorrectNumBytes.AfterStir", rx.Length, numBytes);
            }
        } // TestRandom

        [Test(Profile.TPM20, Privileges.Admin, Category.Misc, Special.None)]
        void TestVendorSpecific(Tpm2 tpm, TestContext testCtx)
        {
            if (!TpmCfg.IsImplemented(TpmCc.VendorTcgTest))
            {
                Substrate.WriteToLog("TestVendorSpecific skipped", ConsoleColor.DarkCyan);
                return;
            }

            TpmHandle h = Substrate.CreateDataObject(tpm);
            byte[] inData = Substrate.RandomBytes(24);

            testCtx.ReportParams("Input data size: " + inData.Length);
            byte[] outData = tpm.VendorTcgTest(inData);
            testCtx.Assert("CertDataReceived", outData.Length > 0, outData.Length);
           
            tpm.FlushContext(h);
        } // TestVendorSpecific
    }
}


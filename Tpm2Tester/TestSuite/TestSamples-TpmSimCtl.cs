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
        [Test(Profile.TPM20, Privileges.Special, Category.Startup, Special.NotThreadSafe
                | Special.Platform | Special.PowerControl | Special.Locality | Special.PhysicalPresence)]
        void TestTpmPlatformControls(Tpm2 tpm, TestContext testCtx)
        {
            //
            // Locality control
            //

            tpm.Shutdown(Su.Clear);
            tpm._GetUnderlyingDevice().PowerCycle();

            foreach (var loc in new LocalityAttr[] { LocalityAttr.TpmLocOne,
                                        LocalityAttr.TpmLocFour, LocalityAttr.ExtendedBit0 })
            {
                tpm._SetLocality(loc);
                tpm._ExpectError(TpmRc.Locality)
                   .Startup(Su.Clear);
            }

            tpm._SetLocality(LocalityAttr.TpmLocThree);
            tpm.Startup(Su.Clear);

            tpm.Shutdown(Su.State);
            tpm._GetUnderlyingDevice().PowerCycle();
            tpm.Startup(Su.State);

            tpm.Shutdown(Su.Clear);
            tpm._GetUnderlyingDevice().PowerCycle();

            tpm._SetLocality(LocalityAttr.TpmLocZero);
            tpm.Startup(Su.Clear);

            //
            // Physical Presence control
            //

            // PpCommands() requires PP asserted
            tpm._ExpectError(TpmRc.Pp)
               .PpCommands(TpmRh.Platform, new TpmCc[0], new TpmCc[0]);

            tpm._AssertPhysicalPresence()
               .PpCommands(TpmRh.Platform, new TpmCc[0], new TpmCc[0]);
        } // TestTpmPlatformControls

        [Test(Profile.TPM20, Privileges.Admin, Category.Misc,
              Special.Platform | Special.NotThreadSafe)]
        void TestFailureMode(Tpm2 tpm, TestContext testCtx)
        {
            tpm._GetUnderlyingDevice().TestFailureMode();
            tpm._ExpectError(TpmRc.Failure)
               .SelfTest(1);

            TpmRc testResult = TpmRc.None;
            byte[] outData = tpm.GetTestResult(out testResult);
            testCtx.Assert("TestResult", testResult == TpmRc.Failure);
            testCtx.Assert("OutData", outData != null && outData.Length > 0);

            // Make sure that selected capabilities can be retrieved even when TPM is in failure mode
            Tpm2.GetProperty(tpm, Pt.Manufacturer);
            Tpm2.GetProperty(tpm, Pt.VendorString1);
            Tpm2.GetProperty(tpm, Pt.VendorTpmType);
            Tpm2.GetProperty(tpm, Pt.FirmwareVersion1);

            // Check if other commands fail as expected while in failure mode.
            tpm._ExpectError(TpmRc.Failure)
               .GetRandom(8);

            // Bring TPM back to normal.
            tpm._GetUnderlyingDevice().PowerCycle();
            tpm.Startup(Su.Clear);
        } // TestFailureMode
    }
}

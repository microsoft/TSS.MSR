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
        [Test(Profile.TPM20, Privileges.Admin, Category.Asym | Category.Ecc)]
        void EcdhSample(Tpm2 tpm, TestContext testCtx)
        {
            //
            // Peer A (e.g. local machine):
            //

            // Template for an ECC key with the ECDH scheme:
            var inPub = new TpmPublic(TpmAlgId.Sha256,
                ObjectAttr.Decrypt | ObjectAttr.UserWithAuth | ObjectAttr.SensitiveDataOrigin,
                null,
                new EccParms(new SymDefObject(), new SchemeEcdh(TpmAlgId.Sha256),
                                EccCurve.NistP256, new NullKdfScheme()),
                new EccPoint());

            // Boilerplate stuff
            var pcrSel = new PcrSelection[0];
            CreationData crData;
            byte[] crHash;
            TkCreation crTk;

            // Create a key for ECDH
            TpmPublic pubA;
            TpmHandle hKeyA = tpm.CreatePrimary(TpmRh.Owner, new SensitiveCreate(), inPub, null, new PcrSelection[0],
                                                out pubA, out crData, out crHash, out crTk);

            //
            // Peer B (e.g. remote machine):
            //

            // Receives 'pubA' from peer A

            // Load public key
            TpmHandle hPubKeyA = tpm.LoadExternal(null, pubA, TpmRh.Owner);

            // Create shared secret 'zB', and a public ECC point for exchange
            EccPoint ephPubPt;
            EccPoint zB = tpm.EcdhKeyGen(hPubKeyA, out ephPubPt);
            tpm.FlushContext(hPubKeyA);

            //
            // Peer A again:
            //

            // Receives 'ephPubPt' from peer B

            // A full key is required here
            EccPoint zA = tpm.EcdhZGen(hKeyA, ephPubPt);

            testCtx.AssertEqual("SharedSecret", zA, zB);

            tpm.FlushContext(hKeyA);
        } // EcdhSample
    }
}


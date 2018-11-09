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
using System.Diagnostics;

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
        void TestCertifyX509_1(Tpm2 tpm, TestContext testCtx)
        {
            testCtx.ReportParams("Test phase: TestCertifyX509_1");

            var policy = new PolicyTree(TpmAlgId.Sha256);
            policy.SetPolicyRoot(new TpmPolicyCommand(TpmCc.CertifyX509));

            var keyTemplate = new TpmPublic(
                TpmAlgId.Sha256,
                ObjectAttr.Restricted | ObjectAttr.Sign | ObjectAttr.FixedParent | ObjectAttr.FixedTPM | ObjectAttr.UserWithAuth | ObjectAttr.AdminWithPolicy | ObjectAttr.SensitiveDataOrigin,
                policy.GetPolicyDigest(),
                new RsaParms(new SymDefObject(), new SchemeRsassa(TpmAlgId.Sha256), 2048, 0),
                new Tpm2bPublicKeyRsa()
             );

            // Make two keys 
            TpmPublic certifyingKeyPub, keyToBeCertifiedPub;
            TpmHandle hCertifyingKey = Substrate.CreatePrimary(tpm, keyTemplate, out certifyingKeyPub);
            TpmHandle hKeyToBeCertified = Substrate.CreatePrimary(tpm, keyTemplate, out keyToBeCertifiedPub);

            var partialCert = CertifyX509Support.MakeExemplarPartialCert();
            var partialCertBytes = partialCert.GetDerEncoded();

            // If you want to paste in your own hex put it here and s
            //var partialCertBytes = Globs.ByteArrayFromHex("01020304");

            AuthSession sess = tpm.StartAuthSessionEx(TpmSe.Policy, TpmAlgId.Sha256);
            sess.RunPolicy(tpm, policy);

            // I used this to test that the auth is OK.  Of course you need to change the PolicyCommandCode above
            //var attestInfo = tpm[sess].Certify(hKeyToBeCertified, hCertifyingKey, new byte[0], new NullSigScheme(), out var legacySignature);

            byte[] tbsHash;
            ISignatureUnion signature;
            byte[] addedTo = tpm[sess].CertifyX509(hKeyToBeCertified, hCertifyingKey, new byte[0], new NullSigScheme(), partialCertBytes, out tbsHash, out signature);
            var addedToCert = AddedToCertificate.FromDerEncoding(addedTo);

            var returnedCert = CertifyX509Support.AssembleCertificate(partialCert, addedToCert, ((SignatureRsa)signature).GetTpmRepresentation());

            // Does the expected hash match the returned hash?
            var tbsBytes = returnedCert.GetTbsCertificate();
            var expectedTbsHash  = TpmHash.FromData(TpmAlgId.Sha256, tbsBytes);
            Debug.Assert(Globs.ArraysAreEqual(expectedTbsHash.HashData, tbsHash));

            // If you get this far we can check the cert is properly signed but we'll need to do a
            // bit of TPM<->Bouncy Castle data type conversion.


            tpm.FlushContext(hCertifyingKey);
            tpm.FlushContext(hKeyToBeCertified);


        } // TestCertifyX509_1





    }
}
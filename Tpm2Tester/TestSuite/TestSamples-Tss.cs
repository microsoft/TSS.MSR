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
        [Test(Profile.TPM20, Privileges.Admin, Category.Infra | Category.Session, Special.NotThreadSafe)]
        void TestAutomaticAuth(Tpm2 tpm, TestContext testCtx)
        {
            TpmHandle hPrim = Substrate.LoadRsaPrimary(tpm);

            // Make an RSA encryption key.
            var decScheme = new SchemeOaep(Substrate.Random(TpmCfg.HashAlgs));
            var sigScheme = new SchemeRsassa(Substrate.Random(TpmCfg.HashAlgs));
            ushort keyLength = Substrate.RandomRsaKeySize(decScheme.hashAlg);
            var inPub = new TpmPublic(Substrate.Random(TpmCfg.HashAlgs),
                ObjectAttr.Decrypt | ObjectAttr.Sign
                    | ObjectAttr.FixedParent | ObjectAttr.FixedTPM
                    | ObjectAttr.UserWithAuth | ObjectAttr.SensitiveDataOrigin,
                null,
                new RsaParms(new SymDefObject(), null, keyLength, 0),
                new Tpm2bPublicKeyRsa());

            TpmPublic keyPublic;
            TpmPrivate keyPrivate = Substrate.Create(tpm, hPrim, inPub, out keyPublic);

            TpmHandle keyHandle = null;

            tpm._Behavior.Strict = true;
            try
            {
                // No auth session is added automatically when TPM object is in strict mode.
                tpm._ExpectError(TpmRc.AuthMissing)
                   .Load(hPrim, keyPrivate, keyPublic);

                // Now explicitly request an auth session of appropriate type
                keyHandle = tpm[Auth.Default].Load(hPrim, keyPrivate, keyPublic);
            }
            finally
            {
                tpm._Behavior.Strict = false;
            }

            byte[] message = Substrate.RandBytes(1, TpmHelper.MaxOaepMsgSize(keyLength, decScheme.hashAlg));

            byte[] encrypted = tpm.RsaEncrypt(keyHandle, message, decScheme, null);

            // An auth session is added automatically when TPM object is not in strict mode.
            byte[] decrypted1 = tpm.RsaDecrypt(keyHandle, encrypted, decScheme, null);

            TpmAlgId auditHashAlg = Substrate.Random(TpmCfg.HashAlgs);
            byte[] nonceTpm;

            // AuthSession object can be built from session handle concatenated, if necessary,
            // with session flags and unencrypted salt value (not used in this example).
            AuthSession auditSess = tpm.StartAuthSession(
                                            TpmRh.Null,         // no salt
                                            TpmRh.Null,         // no bind object
                                            Substrate.RandomNonce(auditHashAlg),   // nonceCaller
                                            null,        // no salt
                                            TpmSe.Hmac,         // session type
                                            new SymDef(),       // no encryption/decryption
                                            auditHashAlg,       // authHash
                                            out nonceTpm)       
                                    + (SessionAttr.ContinueSession | SessionAttr.Audit);
            /*
             * Alternatively one of the StartAuthSessionEx helpers can be used)
             * AuthSession auditSess = tpm.StartAuthSessionEx(TpmSe.Hmac, auditHashAlg, null,
             *                                                SessionAttr.ContinueSession | SessionAttr.Audit);
             */

            // TSS-specific call to verify TPM auditing correctness.
            tpm._SetCommandAuditAlgorithm(auditHashAlg);

            // Appropriate auth value is added automatically into the provided session
            byte[] decrypted2 = tpm[auditSess]._Audit()
                                              .RsaDecrypt(keyHandle, encrypted, decScheme, null);

            ISignatureUnion sig;
            Attest attest;

            // A session is added automatically to authorize TpmRh.Endorsement usage.
            attest = tpm.GetSessionAuditDigest(TpmRh.Endorsement, TpmRh.Null, auditSess,
                                               null, new NullSigScheme(), out sig);

            // But if the corresponding auth value stored in the Tpm2 object is invalid, ...
            AuthValue endorsementAuth = tpm.EndorsementAuth;
            tpm.EndorsementAuth = Globs.ByteArray(16, 0xde);
            // ... the command will fail
            tpm._ExpectError(TpmRc.BadAuth)
               .GetSessionAuditDigest(TpmRh.Endorsement, TpmRh.Null, auditSess,
                                      null, new NullSigScheme(), out sig);
            // Restore correct auth value.
            tpm.EndorsementAuth = endorsementAuth;

            // Verify that both decryption and auditing worked correctly. 
            SessionAuditInfo info = (SessionAuditInfo)attest.attested;
            byte[] auditDigest = tpm._GetAuditHash();
            testCtx.AssertEqual("AuditSessionDigest", info.sessionDigest, auditDigest);
            testCtx.AssertEqual("Decryption", decrypted1, decrypted2);

            // Change auth value of the decryption key.
            TpmPrivate newKeyPrivate = tpm.ObjectChangeAuth(keyHandle, hPrim,
                                                            Substrate.RandomAuth(keyPublic.nameAlg));
            TpmHandle newKeyHandle = tpm.Load(hPrim, newKeyPrivate, keyPublic);

            auditSess.Attrs &= ~SessionAttr.AuditExclusive;
            // Correct auth value (corresponding to newKeyHandle, and different from
            // the one used for keyHandle) will be added to auditSess.
            decrypted1 = tpm[auditSess]._Audit().RsaDecrypt(newKeyHandle, encrypted, decScheme, null);

            // And now two sessions are auto-generated (for TpmRh.Endorsement and keyHandle).
            attest = tpm.GetSessionAuditDigest(TpmRh.Endorsement, keyHandle, auditSess,
                                               null, sigScheme, out sig);
            bool sigOk = keyPublic.VerifySignatureOverData(
                                        Marshaller.GetTpmRepresentation(attest), sig);
            testCtx.Assert("AuditSessionSignatute.1", sigOk);

            // Here the first session is generated based on session type indicator
            // (Auth.Pw), and the second one is added automatically.
            attest = tpm[Auth.Pw].GetSessionAuditDigest(TpmRh.Endorsement, keyHandle, auditSess, 
                                                        null, sigScheme, out sig);

            // Verify that auditing worked correctly.
            sigOk = keyPublic.VerifySignatureOverData(
                                        Marshaller.GetTpmRepresentation(attest), sig);
            testCtx.Assert("AuditSessionSignatute.2", sigOk);

            tpm.FlushContext(newKeyHandle);
            tpm.FlushContext(auditSess);

            if (!TestCfg.HasTRM)
            {
                // Deplete TPM's active session storage
                List<AuthSession> landfill = new List<AuthSession>();

                for (;;)
                {
                    tpm._AllowErrors();
                    AuthSession s = tpm.StartAuthSessionEx(TpmSe.Hmac, Substrate.Random(TpmCfg.HashAlgs),
                                                           SessionAttr.ContinueSession);
                    if (!tpm._LastCommandSucceeded())
                    {
                        break;
                    }
                    landfill.Add(s);
                }

                // Check if session type indicators are processed correctly
                tpm[Auth.Hmac]._ExpectError(TpmRc.SessionMemory)
                              .RsaDecrypt(keyHandle, encrypted, null, null);
                tpm[Auth.Pw].RsaDecrypt(keyHandle, encrypted, null, null);

                // Check if default session type defined by the TPM device is processed correctly
                bool needHmac = tpm._GetUnderlyingDevice().NeedsHMAC;

                tpm._GetUnderlyingDevice().NeedsHMAC = true;
                tpm._ExpectError(TpmRc.SessionMemory)
                   .RsaDecrypt(keyHandle, encrypted, null, null);
                tpm[Auth.Default]._ExpectError(TpmRc.SessionMemory)
                                 .RsaDecrypt(keyHandle, encrypted, null, null);

                tpm._GetUnderlyingDevice().NeedsHMAC = false;
                tpm.RsaDecrypt(keyHandle, encrypted, null, null);
                tpm[Auth.Default].RsaDecrypt(keyHandle, encrypted, null, null);

                tpm._GetUnderlyingDevice().NeedsHMAC = needHmac;

                landfill.ForEach(s => tpm.FlushContext(s));
            }
            tpm.FlushContext(keyHandle);
        } // TestAutomaticAuth

        [Test(Profile.TPM20, Privileges.Admin, Category.Infra)]
        void TestSerialization(Tpm2 tpm, TestContext testCtx)
        {
            // test library serialization (not a TPM test)
            TpmAlgId hashAlg = Substrate.Random(TpmCfg.HashAlgs);

            // make some moderately complicated TPM structures
            var inPub = new TpmPublic(hashAlg,
                ObjectAttr.Sign | ObjectAttr.FixedParent | ObjectAttr.FixedTPM
                    | ObjectAttr.UserWithAuth | ObjectAttr.SensitiveDataOrigin,
                null,
                new RsaParms(new SymDefObject(), new SchemeRsassa(hashAlg),
                             Substrate.Random(TpmCfg.RsaKeySizes), 0),
                new Tpm2bPublicKeyRsa());

            TpmPublic pub;
            TpmHandle hKey = Substrate.CreateAndLoad(tpm, inPub, out pub);

            TpmHash hashToSign = TpmHash.FromRandom(hashAlg);
            var proof = new TkHashcheck(TpmRh.Null, null);
            var sig = tpm.Sign(hKey, hashToSign, new SchemeRsassa(hashAlg), proof);
            tpm.FlushContext(hKey);

            // Simple TPM-hash to/from JSON
            TpmHash h = TpmHash.FromString(hashAlg, "hello");

            MemoryStream s2 = new MemoryStream();
            DataContractJsonSerializer ser2 = new DataContractJsonSerializer(typeof(TpmHash));
            ser2.WriteObject(s2, h);
            s2.Flush();
            string jsonString2 = Encoding.ASCII.GetString(s2.ToArray());

            TpmHash h2 = (TpmHash)ser2.ReadObject(new MemoryStream(s2.ToArray()));
            testCtx.AssertEqual("JSON.Simple", h, h2);

            // JSON more complex - 
            MemoryStream s = new MemoryStream();
            DataContractJsonSerializer ser = new DataContractJsonSerializer(typeof(TpmPublic));
            ser.WriteObject(s, pub);
            s.Flush();
            string jsonString = Encoding.ASCII.GetString(s.ToArray());
            TpmPublic reconstruct = (TpmPublic)ser.ReadObject(new MemoryStream(s.ToArray()));
            testCtx.AssertEqual("JSON.Complex", pub, reconstruct);

            // XML
            s = new MemoryStream();
            DataContractSerializer s4 = new DataContractSerializer(typeof(TpmPublic));
            s4.WriteObject(s, pub);
            s.Flush();
            string xmlString = Encoding.ASCII.GetString(s.ToArray());
            TpmPublic rec4 = (TpmPublic)s4.ReadObject(new MemoryStream(s.ToArray()));
            testCtx.AssertEqual("XML.Complex", pub, rec4, s4);
        } // TestSerialization

    }
}

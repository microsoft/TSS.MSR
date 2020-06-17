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
        [Test(Profile.TPM20, Privileges.Admin, Category.Asym | Category.Dup | Category.Rsa)]
        void DuplicateImportRsaSample(Tpm2 tpm, TestContext testCtx)
        {
            TpmAlgId nameAlg = Substrate.Random(TpmCfg.HashAlgs);
            var policy = new PolicyTree(nameAlg);
            policy.SetPolicyRoot(new TpmPolicyCommand(TpmCc.Duplicate));

            var inPub = new TpmPublic(nameAlg,
                    ObjectAttr.Sign | ObjectAttr.AdminWithPolicy | ObjectAttr.SensitiveDataOrigin,
                    policy.GetPolicyDigest(),
                    new RsaParms(new SymDefObject(),
                                 new SchemeRsassa(Substrate.Random(TpmCfg.HashAlgs)),
                                 Substrate.Random(TpmCfg.RsaKeySizes), 0),
                    new Tpm2bPublicKeyRsa());

            TpmHandle hKey = Substrate.CreateAndLoad(tpm, inPub, out TpmPublic pub);

            // Duplicate
            TpmPrivate priv = TpmHelper.GetPlaintextPrivate(tpm, hKey, policy);
            tpm.FlushContext(hKey);

            // Import
            TpmPrivate privImp = tpm.Import(Substrate.LoadRsaPrimary(tpm), null, pub, priv, null, new SymDefObject());
        } // SimpleDuplicateImportRsaSample

        TssObject ImportExternalRsaKey(Tpm2 tpm, TpmHandle hParent,
                                       int keySizeInBits, IAsymSchemeUnion scheme,
                                       byte[] publicPart, byte[] privatePart,
                                       ObjectAttr keyAttrs,
                                       byte[] authVal = null, byte[] policyDigest = null)
        {
            TpmAlgId    sigHashAlg = TpmHelper.GetSchemeHash(scheme),
                        nameAlg = sigHashAlg;

            // TPM signing key template with the actual public key bits
            var inPub = new TpmPublic(nameAlg,
                keyAttrs | ObjectAttr.AdminWithPolicy | ObjectAttr.SensitiveDataOrigin,
                policyDigest,
                new RsaParms(new SymDefObject(), scheme as IAsymSchemeUnion,
                             (ushort)keySizeInBits, 0),
                new Tpm2bPublicKeyRsa(publicPart));

            // Wrap the key in a TSS helper class
            TssObject swKey = TssObject.Create(inPub, authVal, privatePart);

            // Get a key duplication blob in TPM 2.0 compatibale format
            TpmPrivate dupBlob = swKey.GetPlaintextDuplicationBlob();

            // Importing a duplication blob creates a new TPM private key blob protected
            // with its new parent key.
            swKey.Private = tpm.Import(hParent, null, swKey.Public, dupBlob, null, new SymDefObject());

            return swKey;
        } // ImportExternalRsaKey

        [Test(Profile.TPM20, Privileges.Admin,
              Category.Asym | Category.Dup | Category.Rsa)]
        void ExternalKeyImportSample(Tpm2 tpm, TestContext testCtx)
        {
            // Create a software key (external to any TPM).
            int keySize = 2048;
            var externalRsaKey = new RawRsa(keySize);

            // When an external key comes from a cert, one would need to extract the key size and
            // byte buffers representing public and private parts of the key from the cert, an use
            // them directly in the call to ImportExternalRsaKey() below (i.e. no RawRsa object is
            // necessary).

            // Signing scheme to use (it may come from the key's cert)
            TpmAlgId sigHashAlg = Substrate.Random(TpmCfg.HashAlgs);
            var sigScheme = new SchemeRsassa(sigHashAlg);

            // An arbitrary external key would not have TPM key attributes associated with it.
            // Yet some of them may be inferred from the cert based on the declared key purpose
            // (ObjectAttr.Sign below). The others are defined by the intended TPM key usage
            // scenarios, e.g. ObjectAttr.UserWithAuth tells TPM to allow key usage authorization
            // using an auth value (random byte buffer) in a password or an HMAC session.
            ObjectAttr keyAttrs = ObjectAttr.Sign | ObjectAttr.UserWithAuth;

            // Generate an auth value for the imported matching in strength the signing scheme
            byte[] authVal = Substrate.RandomAuth(sigHashAlg);

            // We need a storage key to use as a parent of the imported key.
            // The following helper creates an RSA primary storage key.
            TpmHandle hParent = Substrate.CreateRsaPrimary(tpm);

            TssObject importedKey = ImportExternalRsaKey(tpm, hParent,
                                                         keySize, sigScheme,
                                                         externalRsaKey.Public, externalRsaKey.Private,
                                                         keyAttrs, authVal);

            // Now we can load the newly imported key into the TPM, ...
            TpmHandle hImportedKey = tpm.Load(hParent, importedKey.Private, importedKey.Public);

            // ... let the TSS know the auth value associated with this handle, ...
            hImportedKey.SetAuth(authVal);  

            // ... and use it to sign something to check if import was OK
            TpmHash toSign = TpmHash.FromRandom(sigHashAlg);
            ISignatureUnion sig = tpm.Sign(hImportedKey, toSign, null, new TkHashcheck());

            // Verify that the signature is correct using the public part of the imported key
            bool sigOk = importedKey.Public.VerifySignatureOverHash(toSign, sig);
            testCtx.Assert("Signature.OK", sigOk);

            // Cleanup
            tpm.FlushContext(hImportedKey);
            // The parent key handle can be flushed immediately after it was used in the Load() command
            tpm.FlushContext(hParent);

            // Imported private/public key pair (in the TssObject) can be stored on disk, in the cloud,
            // etc. (no additional protection is necessary), and loaded into the TPM as above whenever
            // the key is needed.

            // Alternatively the key can be persisted in the TPM using the EvictControl() command
        } // ExternalKeyImportSample
    }
}

/*++

Copyright (c) 2010-2015 Microsoft Corporation
Microsoft Confidential

*/
using System;
using System.Diagnostics;
using System.Text;

#if !TSS_USE_BCRYPT
using System.Security.Cryptography;
#endif


namespace Tpm2Lib
{
    public partial class NvPublic
    {
        /// <summary>
        /// Calculate and return the name of the entity.  The name is an alg-prepended hash in a byte[]
        /// </summary>
        /// <returns></returns>
        public byte[] GetName()
        {
            byte[] rawData = GetTpmRepresentation();
            TpmHash pubDigest = TpmHash.FromData(nameAlg, rawData);
            return Marshaller.GetTpmRepresentation(pubDigest);
        }
    }

    public partial class TpmPublic
    {
        /// <summary>
        /// Calculate and return the name of the entity.  The name is an alg-prepended hash in a byte[]
        /// </summary>
        /// <returns></returns>
        public byte[] GetName()
        {
            byte[] rawData = GetTpmRepresentation();
            TpmHash pubDigest = TpmHash.FromData(nameAlg, rawData);
            return Marshaller.GetTpmRepresentation(pubDigest);
        }

        public delegate void Transformer(byte[] x);

        internal Transformer TransformerCallback;

        /// <summary>
        /// Install a transformer callback (for debugging). Transformer is called on several
        /// code-paths in creating SW-generated keys, import blobs, and activation
        /// blobs. Transformer can arbitrarily manipulate the byte array parameter and the
        /// transformed value will be used (this allows a caller to transform parameters
        /// that are hard to affect in the raw TPM command because they are protected by
        /// crypto.
        /// Note that the transformer callback should only work on a fraction (say 10%)
        /// of the calls because the it is called several times during preparation of some
        /// data structures and if one always modifies the first then it is possible that
        /// the second is never processed by the TPM.
        /// </summary>
        /// <param name="transformer"></param>
        public void _SetTransformer(Transformer transformer)
        {
            TransformerCallback = transformer;
        }

        private void Transform(byte[] x)
        {
            if (TransformerCallback != null)
            {
                TransformerCallback(x);
            }
        }

        /// <summary>
        /// The TPM always signs hash-sized data.  This version of the VerifySignature performs the necessary
        /// hash operation over arbitrarily-length data and verifies that the hash is properly signed
        /// (i.e. the library performs the hash)
        /// </summary>
        /// <param name="signedData"></param>
        /// <param name="signature"></param>
        /// <returns></returns>
        public bool VerifySignatureOverData(byte[] signedData, ISignatureUnion signature, TpmAlgId sigHashAlg = TpmAlgId.Null)
        {
            using (AsymCryptoSystem verifier = AsymCryptoSystem.CreateFrom(this))
            {
                bool sigOk = verifier.VerifySignatureOverData(signedData, signature, sigHashAlg);
                return sigOk;
            }
        }

        /// <summary>
        /// Verify a TPM signature structure of the hash of some data (caller hashes the data that will be verified)
        /// </summary>
        /// <param name="signedHash"></param>
        /// <param name="signature"></param>
        /// <returns></returns>
        public bool VerifySignatureOverHash(TpmHash signedHash, ISignatureUnion signature)
        {
            using (AsymCryptoSystem verifier = AsymCryptoSystem.CreateFrom(this))
            {
                return verifier.VerifySignatureOverHash(signedHash, signature);
            }
        }

        /// <summary>
        /// Verify that a TPM quote matches an expect PCR selection, is well formed, and is properly signed
        /// by the private key corresponding to this public key.
        /// </summary>
        /// <param name="pcrDigestAlg"></param>
        /// <param name="expectedSelectedPcr"></param>
        /// <param name="expectedPcrValues"></param>
        /// <param name="nonce"></param>
        /// <param name="quotedInfo"></param>
        /// <param name="signature"></param>
        /// <param name="qualifiedNameOfSigner"></param>
        /// <returns></returns>
        public bool VerifyQuote(
            TpmAlgId pcrDigestAlg,
            PcrSelection[] expectedSelectedPcr,
            Tpm2bDigest[] expectedPcrValues,
            byte[] nonce,
            Attest quotedInfo,
            ISignatureUnion signature,
            byte[] qualifiedNameOfSigner = null)
        {
            if (!(quotedInfo.attested is QuoteInfo))
            {
                return false;
            }

            if (quotedInfo.magic != Generated.Value)
            {
                return false;
            }

            if (!quotedInfo.extraData.IsEqual(nonce))
            {
                return false;
            }

            // Check environment of signer (name) is expected
            if (qualifiedNameOfSigner != null)
            {
                if (!quotedInfo.qualifiedSigner.IsEqual(qualifiedNameOfSigner))
                {
                    return false;
                }
            }

            // Now check the quote-specific fields
            var quoted = (QuoteInfo)quotedInfo.attested;

            // Check values pcr indices are what we expect
            if (!Globs.ArraysAreEqual(quoted.pcrSelect, expectedSelectedPcr))
            {
                return false;
            }

            // Check that values in the indices above are what we expect
            // ReSharper disable once UnusedVariable
            var expected = new PcrValueCollection(expectedSelectedPcr, expectedPcrValues);
            var m = new Marshaller();

            foreach (Tpm2bDigest d in expectedPcrValues)
            {
                m.Put(d.buffer, "");
            }

            TpmHash expectedPcrHash = TpmHash.FromData(pcrDigestAlg, m.GetBytes());
            if (!Globs.ArraysAreEqual(expectedPcrHash, quoted.pcrDigest))
            {
                return false;
            }

            // And finally check the signature
            bool sigOk = VerifySignatureOverData(quotedInfo.GetTpmRepresentation(), signature);
            return sigOk;
        }

        /// <summary>
        /// Verify that quotedInfo is properly signed by an associated private key holder, and that the
        /// quotedInfo.type, .extraData and .magic are correct.  Also check that the certified name is what
        /// the caller expects.  The caller must check other fields (for instance the qualified name)
        /// </summary>
        /// <param name="name"></param>
        /// <param name="nonce"></param>
        /// <param name="quotedInfo"></param>
        /// <param name="expectedName"></param>
        /// <param name="signature"></param>
        /// <returns></returns>
        public bool VerifyCertify(TpmHash name, byte[] nonce, Attest quotedInfo, byte[] expectedName, ISignatureUnion signature)
        {
            // Check generic signature stuff
            if (quotedInfo.type != TpmSt.AttestCertify)
            {
                return false;
            }

            if (!Globs.ArraysAreEqual(quotedInfo.extraData, nonce))
            {
                return false;
            }

            if (quotedInfo.magic != Generated.Value)
            {
                return false;
            }

            // Check specific certify-signature stuff
            var certInfo = (CertifyInfo)quotedInfo.attested;
            if (!Globs.ArraysAreEqual(expectedName, certInfo.name))
            {
                return false;
            }
            // Check the actual signature
            TpmHash sigHash = TpmHash.FromData(TpmAlgId.Sha1, quotedInfo.GetTpmRepresentation());
            bool certifyOk = VerifySignatureOverHash(sigHash, signature);
            return certifyOk;
        }

        /// <summary>
        /// OEAP pad and encrypt the data using the specified encoding parameters (RSA only)
        /// </summary>
        /// <param name="dataToEncrypt"></param>
        /// <param name="encodingParms"></param>
        /// <returns></returns>
        public byte[] EncryptOaep(byte[] dataToEncrypt, byte[] encodingParms)
        {
            using (AsymCryptoSystem encryptor = AsymCryptoSystem.CreateFrom(this))
            {
                return encryptor.EncryptOaep(dataToEncrypt, encodingParms);
            }
        }

        /// <summary>
        /// Get an ECDH key exchange key (one pass ephemeral) and the public key of the ephemeral
        /// key using ECDH with encodingParms as input to the KDF (ECC only)
        /// </summary>
        /// <param name="encodingParms"></param>
        /// <param name="decryptKeyNameAlg"></param>
        /// <param name="pubEphem"></param>
        /// <returns></returns>
        public byte[] EcdhGetKeyExchangeKey(byte[] encodingParms, TpmAlgId decryptKeyNameAlg, out EccPoint pubEphem)
        {
            using (AsymCryptoSystem encryptor = AsymCryptoSystem.CreateFrom(this))
            {
                return encryptor.EcdhGetKeyExchangeKey(encodingParms, decryptKeyNameAlg, out pubEphem);
            }
        }

        /// <summary>
        /// Create activation blobs that can be passed to ActivateCredential.  Two blobs are returned -
        /// (a) - encryptedSecret - is the symmetric key cfb-symmetrically encrypted with an enveloping key
        /// (b) credentialBlob (the return value of this function) - is the enveloping key OEAP (RSA) encrypted
        ///         by the public part of this key.
        /// </summary>
        /// <param name="secret"></param>
        /// <param name="nameAlgId"></param>
        /// <param name="nameOfKeyToBeActivated"></param>
        /// <param name="encryptedSecret"></param>
        /// <returns>CredentialBlob (</returns>
        public byte[] CreateActivationCredentials(
            byte[] secret,
            TpmAlgId nameAlgId,
            byte[] nameOfKeyToBeActivated,
            out byte[] encryptedSecret)
        {
            byte[] seed, encSecret;

            switch (type)
            {
                case TpmAlgId.Rsa:
                    // The seed should be the same size as the symmKey
                    seed = Globs.GetRandomBytes((CryptoLib.DigestSize(nameAlg) + 7) / 8);
                    encSecret = EncryptOaep(seed, ActivateEncodingParms);
                    break;
                case TpmAlgId.Ecc:
                    EccPoint pubEphem;
                    seed = EcdhGetKeyExchangeKey(ActivateEncodingParms, nameAlg, out pubEphem);
                    encSecret = Marshaller.GetTpmRepresentation(pubEphem);
                    break;
                default:
                    throw new NotImplementedException("activate crypto scheme not implemented");
            }

            Transform(seed);
            Transform(encSecret);

            var cvx = new Tpm2bDigest(secret);
            byte[] cvTpm2B = Marshaller.GetTpmRepresentation(cvx);
            Transform(cvTpm2B);

            SymDefObject symDef = TssObject.GetSymDef(this);
            byte[] symKey = KDF.KDFa(nameAlg, seed, "STORAGE", nameOfKeyToBeActivated, new byte[0], symDef.KeyBits);
            Transform(symKey);

            byte[] encIdentity;
            using (SymmCipher symm2 = SymmCipher.Create(symDef, symKey))
            {
                encIdentity = symm2.CFBEncrypt(cvTpm2B);
            }
            Transform(encIdentity);

            var hmacKeyBits = (uint)CryptoLib.DigestSize(nameAlg);
            byte[] hmacKey = KDF.KDFa(nameAlg, seed, "INTEGRITY", new byte[0], new byte[0], hmacKeyBits * 8);
            Transform(hmacKey);
            byte[] outerHmac = CryptoLib.HmacData(nameAlg,
                                                  hmacKey,
                                                  Globs.Concatenate(encIdentity, nameOfKeyToBeActivated));
            Transform(outerHmac);

            byte[] activationBlob = Globs.Concatenate(
                                                      Marshaller.ToTpm2B(outerHmac),
                                                      encIdentity);

            Transform(activationBlob);

            encryptedSecret = encSecret;

            return activationBlob;
        }

        private static readonly byte[] ActivateEncodingParms = Encoding.UTF8.GetBytes("IDENTITY\0");
    }

    /// <summary>
    /// TssObject is a container wrapper for
    /// 1) A Public
    /// 2) A Public and a Sensitive
    /// 3) A public and a private
    /// 4) Optional use-authorization
    /// It (and methods on the contained public) provide support for many of the actions
    /// of the TPM (signing, quote verification, key creation, etc.)
    /// </summary>
    public partial class TssObject
    {
        public AuthValue UseAuth = new AuthValue();

        public TssObject(TpmPublic thePublicPart, TpmPrivate thePrivatePart)
        {
            publicPart = thePublicPart;
            privatePart = thePrivatePart;
        }

        public delegate void Transformer(byte[] x);

        private Transformer TransformerCallback;

        /// <summary>
        /// Install a transformer callback (for debugging). Transformer is called on several
        /// code-paths in creating SW-generated keys, import blobs, and activation
        /// blobs. Transformer can arbitrarily manipulate the byte array parameter and the
        /// transformed value will be used (this allows a caller to transform parameters
        /// that are hard to affect in the raw TPM command because they are protected by
        /// crypto.
        /// Note that the transformer callback should only work on a fraction (say 10%)
        /// of the calls because the it is called several times during preparation of some
        /// data structures and if one always modifies the first then it is possible that
        /// the second is never processed by the TPM.
        /// </summary>
        /// <param name="transformer"></param>
        public void _SetTransformer(Transformer transformer)
        {
            TransformerCallback = transformer;
        }

        private void Transform(byte[] x)
        {
            if (TransformerCallback != null)
            {
                TransformerCallback(x);
            }
        }

        /// <summary>
        /// Creates a *software* root key.  The key will be random (not created from a seed).  The key can be used
        /// as the root of a software hierarchy that can be translated into a duplication blob ready for import into
        /// a TPM.  Depending on the type of key, the software root key can be a parent for other root keys that can
        /// comprise a migration group.  The caller should specify necessary key parameters in Public.
        /// </summary>
        /// <returns></returns>
        public static TssObject CreateStorageParent(TpmPublic keyParameters, AuthValue authVal)
        {
            var newKey = new TssObject();
            // Create a new asymmetric key from the supplied parameters
            IPublicIdUnion publicId;
            ISensitiveCompositeUnion sensitiveData = CreateSensitiveComposite(keyParameters, out publicId);

            // fill in the public data
            newKey.publicPart = keyParameters.Copy();
            newKey.publicPart.unique = publicId;

            // Create the associated symmetric key -
            SymDefObject symDef = GetSymDef(keyParameters);
            byte[] symmKey;
            if (symDef.Algorithm != TpmAlgId.Null)
            {
                using (var symmCipher = SymmCipher.Create(symDef))
                {
                    symmKey = symmCipher.KeyData;
                }
            }
            else
            {
                symmKey = new byte[0];
            }
            // Fill in the fields for the symmetric private-part of the asymmetric key
            var sens = new Sensitive(authVal.AuthVal, symmKey, sensitiveData);
            newKey.sensitivePart = sens;

            // And return the new key
            return newKey;
        }

        /// <summary>
        /// Creates a Private area for this key that will be loadable on a TPM though TPM2_Load() if the target TPM already has the parent
        /// storage key "parent" loaded.  This function lets applications create key-hierarchies in software that can be loaded into
        /// a TPM once the parent has been "TPM2_Import'ed."
        /// TPM2_Import() supports plaintext import.  To get this sort of import blob set intendedParent
        /// to null
        /// </summary>
        /// <param name="intendedParent"></param>
        /// <returns></returns>
        public TpmPrivate GetPrivate(TssObject intendedParent)
        {
            SymDefObject symDef = GetSymDef(intendedParent.publicPart);

            // Figure out how many bits we will need from the KDF
            byte[] parentSymValue = intendedParent.sensitivePart.seedValue;
            Transform(parentSymValue);
            byte[] iv = Globs.GetRandomBytes(SymmCipher.GetBlockSize(symDef));

            // The encryption key is calculated with a KDF
            byte[] symKey = KDF.KDFa(intendedParent.publicPart.nameAlg,
                                     parentSymValue,
                                     "STORAGE",
                                     GetName(),
                                     new byte[0],
                                     symDef.KeyBits);

            Transform(symKey);

            byte[] newPrivate = KeyWrapper.CreatePrivateFromSensitive(symDef,
                                                                      symKey,
                                                                      iv,
                                                                      sensitivePart,
                                                                      publicPart.nameAlg,
                                                                      publicPart.GetName(),
                                                                      intendedParent.publicPart.nameAlg,
                                                                      intendedParent.sensitivePart.seedValue,
                                                                      TransformerCallback);
            Transform(newPrivate);
            return new TpmPrivate(newPrivate);
        }

        /// <summary>
        /// Create a plaintext duplication blob that can be imported into a TPM
        /// </summary>
        /// <returns></returns>
        public TpmPrivate GetPlaintextDuplicationBlob()
        {
            return new TpmPrivate(sensitivePart.GetTpm2BRepresentation());
        }

        /// <summary>
        /// Creates a duplication blob for the current key that can be Imported as a child
        /// of newParent. Three forms are possible. GetPlaintextDuplicationBlob() allows
        /// plaintext-import. This function enables duplication with and without an
        /// inner wrapper (depending on whether innerWrapper is null)
        /// </summary>
        /// <param name="newParent"></param>
        /// <param name="innerWrapper"></param>
        /// <param name="encryptedWrappingKey"></param>
        /// <returns></returns>
        public TpmPrivate GetDuplicationBlob(
            TpmPublic newParent,
            SymmCipher innerWrapper,
            out byte[] encryptedWrappingKey)
        {
            byte[] encSensitive;
            if (innerWrapper == null)
            {
                // No inner wrapper
                encSensitive = Marshaller.ToTpm2B(sensitivePart.GetTpmRepresentation());
                Transform(encSensitive);
            }
            else
            {
                byte[] sens = Marshaller.ToTpm2B(sensitivePart.GetTpmRepresentation());
                byte[] toHash = Globs.Concatenate(sens, GetName());
                Transform(toHash);
                byte[] innerIntegrity = Marshaller.ToTpm2B(CryptoLib.HashData(publicPart.nameAlg, toHash));
                byte[] innerData = Globs.Concatenate(innerIntegrity, sens);
                Transform(innerData);
                encSensitive = innerWrapper.CFBEncrypt(innerData);
                Transform(encSensitive);
            }

            byte[] seed, encSecret;
            SymDefObject symDef = GetSymDef(newParent);

            using (AsymCryptoSystem newParentPubKey = AsymCryptoSystem.CreateFrom(newParent))
            {
                switch (newParent.type)
                {
                    case TpmAlgId.Rsa:
                        // The seed should be the same size as the symmKey
                        seed = Globs.GetRandomBytes((symDef.KeyBits + 7) / 8);
                        encSecret = newParentPubKey.EncryptOaep(seed, DuplicateEncodingParms);
                        break;
                    case TpmAlgId.Ecc:
                        EccPoint pubEphem;
                        seed = newParentPubKey.EcdhGetKeyExchangeKey(DuplicateEncodingParms,
                                                                     newParent.nameAlg,
                                                                     out pubEphem);
                        encSecret = Marshaller.GetTpmRepresentation(pubEphem);
                        break;
                    default:
                        throw new NotImplementedException("activate crypto scheme not implemented");
                }
            }
            Transform(seed);
            Transform(encSecret);

            encryptedWrappingKey = encSecret;

            byte[] symKey = KDF.KDFa(newParent.nameAlg, seed, "STORAGE", publicPart.GetName(), new byte[0], symDef.KeyBits);
            Transform(symKey);

            byte[] dupSensitive;
            using (SymmCipher enc2 = SymmCipher.Create(symDef, symKey))
            {
                dupSensitive = enc2.CFBEncrypt(encSensitive);
            }
            Transform(dupSensitive);

            int npNameNumBits = CryptoLib.DigestSize(newParent.nameAlg) * 8;
            byte[] hmacKey = KDF.KDFa(newParent.nameAlg, seed, "INTEGRITY", new byte[0], new byte[0], (uint)npNameNumBits);

            byte[] outerDataToHmac = Globs.Concatenate(dupSensitive, publicPart.GetName());
            Transform(outerDataToHmac);

            byte[] outerHmac = Marshaller.ToTpm2B(CryptoLib.HmacData(newParent.nameAlg, hmacKey, outerDataToHmac));
            Transform(outerHmac);

            byte[] dupBlob = Globs.Concatenate(outerHmac, dupSensitive);
            Transform(dupBlob);

            return new TpmPrivate(dupBlob);
        }

        /// <summary>
        /// Create a new asymmetric key based on the parameters in keyParms. The resulting key data is returned in structures
        /// suitable for incorporation in a TPMT_PUBLIC and TPMS_SENSITIVE
        /// </summary>
        /// <param name="keyParms"></param>
        /// <param name="publicParms"></param>
        /// <returns></returns>
        internal static ISensitiveCompositeUnion CreateSensitiveComposite(TpmPublic keyParms, out IPublicIdUnion publicParms)
        {
            TpmAlgId keyAlgId = keyParms.type;
            ISensitiveCompositeUnion newSens;

            // Create the asymmetric key
            if (keyAlgId != TpmAlgId.Rsa)
            {
                throw new Exception("Algorithm not supported");
            }

            var newKeyPair = new RawRsa((keyParms.parameters as RsaParms).keyBits);

            // Put the key bits into the required structure envelopes
            newSens = new Tpm2bPrivateKeyRsa(newKeyPair.Private);
            publicParms = new Tpm2bPublicKeyRsa(newKeyPair.Public);
            return newSens;
        }

        /// <summary>
        /// Get the name of the associated public object
        /// </summary>
        /// <returns></returns>
        public byte[] GetName()
        {
            return publicPart.GetName();
        }

        /// <summary>
        /// Extract and return the SymDefObject that describes the associated symmetric algorithm that is used for key protection
        /// in storage keys.
        /// </summary>
        /// <param name="keyParms"></param>
        /// <returns></returns>
        internal static SymDefObject GetSymDef(TpmPublic keyParms)
        {
            TpmAlgId keyAlgId = keyParms.type;
            switch (keyAlgId)
            {
                case TpmAlgId.Rsa:
                    var rsaParms = (RsaParms)keyParms.parameters;
                    return rsaParms.symmetric;
                case TpmAlgId.Ecc:
                    var eccParms = (EccParms)keyParms.parameters;
                    return eccParms.symmetric;
                default:
                    throw new Exception("Unsupported algorithm");

            }
        }

        /// <summary>
        /// Encoding parameters for key duplication and import
        /// </summary>
        private static readonly byte[] DuplicateEncodingParms = Encoding.UTF8.GetBytes("DUPLICATE\0");

        /// <summary>
        /// Encoding parameters for objects in the storage hierarchy
        /// </summary>
        public static byte[] SecretEncodingParms = Encoding.UTF8.GetBytes("SECRET\0");
    }
}

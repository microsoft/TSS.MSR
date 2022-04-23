/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.Linq;
using System.Numerics;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text;
using System.Security.Cryptography;

namespace Tpm2Lib
{
    /// <summary>
    /// AsymCryptoSystem is a helper class for doing asymmetric cryptography using TPM data
    /// structures. It currently does ECC and RSA signing, decryption and ECDH key exchange.
    /// 
    /// NOTE: The methods of this class do not attempt to replicate parameters validation
    ///       performed by the TPM.
    /// </summary>
    public sealed class AsymCryptoSystem : IDisposable
    {
        private TpmPublic PublicParms;
        private ECDiffieHellman EcDhProvider;
        private ECDsa EcdsaProvider;
        private RSA RsaProvider;

        public AsymCryptoSystem()
        {
        }

        /// <summary>
        /// Create a new random software key (public and private) matching the parameters in keyParams.
        /// </summary>
        /// <param name="keyParams"></param>
        /// <returns></returns>
        public AsymCryptoSystem (TpmPublic keyParams)
        {
            TpmAlgId keyAlgId = keyParams.type;
            PublicParms = keyParams.Copy();

            switch (keyAlgId)
            {
                case TpmAlgId.Rsa:
                {
                    var rsaParams = keyParams.parameters as RsaParms;
                    RsaProvider = RSA.Create(rsaParams.keyBits);
                    var modulus = RsaProvider.ExportParameters(true).Modulus;
                    var pubId = new Tpm2bPublicKeyRsa(modulus);
                    PublicParms.unique = pubId;
                    break;
                }
                case TpmAlgId.Ecc:
                {
                    ECCurve curve = RawEccKey.GetEccCurve(keyParams);
                    ECPoint pub;
                    if (keyParams.objectAttributes.HasFlag(ObjectAttr.Sign))
                    {
                        EcdsaProvider = ECDsa.Create(curve);
                        pub = EcdsaProvider.ExportParameters(false).Q;
                    }
                    else
                    {
                        EcDhProvider = ECDiffieHellman.Create(curve);
                        pub = EcDhProvider.ExportParameters(false).Q;
                    }

                    PublicParms.unique = new EccPoint(pub.X, pub.Y);
                    break;
                }
                default:
                    Globs.Throw<ArgumentException>("Algorithm not supported");
                    break;
            }
        }

        public static bool IsCurveSupported(EccCurve curve)
        {
            return RawEccKey.IsCurveSupported(curve);
        }

        /// <summary>
        /// Create a new AsymCryptoSystem from TPM public parameter. This can then
        /// be used to validate TPM signatures or encrypt data destined for a TPM.  
        /// </summary>
        /// <param name="pubKey"></param>
        /// <param name="privKey"></param>
        /// <returns></returns>
        public static AsymCryptoSystem CreateFrom(TpmPublic pubKey, TpmPrivate privKey = null)
        {
            var cs = new AsymCryptoSystem();

            TpmAlgId keyAlgId = pubKey.type;
            cs.PublicParms = pubKey.Copy();

            // Create an algorithm provider from the provided PubKey
            switch (keyAlgId)
            {
                case TpmAlgId.Rsa:
                {
                    RawRsa rr = null;
                    byte[] prime1 = null,
                           prime2 = null;
                    var rsaParams = (RsaParms)pubKey.parameters;
                    if (privKey != null)
                    {
                        rr = new RawRsa(pubKey, privKey);
                        prime1 = RawRsa.ToBigEndian(rr.P, rsaParams.keyBits / 16);
                        prime2 = RawRsa.ToBigEndian(rr.Q, rsaParams.keyBits / 16);
                    }
                    var exponent = rsaParams.exponent != 0
                                            ? Globs.HostToNet(rsaParams.exponent)
                                            : RsaParms.DefaultExponent;
                    var modulus = (pubKey.unique as Tpm2bPublicKeyRsa).buffer;
                    var dotNetPubParms = new RSAParameters() {Exponent = exponent, Modulus = modulus};
                    if (privKey != null)
                    {
                        dotNetPubParms.P = prime1;
                        dotNetPubParms.Q = prime2;
                        dotNetPubParms.D = RawRsa.ToBigEndian(rr.D, rsaParams.keyBits / 8);
                        dotNetPubParms.InverseQ = RawRsa.ToBigEndian(rr.InverseQ, rsaParams.keyBits / 16);
                        dotNetPubParms.DP = RawRsa.ToBigEndian(rr.DP, rsaParams.keyBits / 16);
                        dotNetPubParms.DQ = RawRsa.ToBigEndian(rr.DQ, rsaParams.keyBits / 16);
                    }

                    cs.RsaProvider = RSA.Create();
                    cs.RsaProvider.ImportParameters(dotNetPubParms);
                    break;
                }
                case TpmAlgId.Ecc:
                {
                    var eccParms = (EccParms)pubKey.parameters;
                    var eccPub = (EccPoint)pubKey.unique;
                    bool isEcdsa = eccParms.scheme.GetUnionSelector() == TpmAlgId.Ecdsa;
                    ECParameters parms = RawEccKey.GetEccParameters(eccPub, eccParms.curveID);

                    if (isEcdsa)
                    {
                        cs.EcdsaProvider = ECDsa.Create(parms);
                    }
                    else
                    {
                        cs.EcDhProvider = ECDiffieHellman.Create(parms);
                    }
                    break;
                }
                default:
                    Globs.Throw<ArgumentException>("Algorithm not supported");
                    cs = null;
                    break;
            }
            return cs;
        }

        /// <summary>
        /// Retrieves key template (containing public key bits).
        /// </summary>
        /// <returns></returns>
        public TpmPublic GetPublicParms()
        {
            return PublicParms;
        }

        public TpmPrivate GetPrivate(out TpmPublic tpmPub,
                                            TpmAlgId nameAlg = TpmAlgId.Sha1,
                                            ObjectAttr keyAttrs = ObjectAttr.Decrypt | ObjectAttr.UserWithAuth,
                                            IAsymSchemeUnion scheme = null,
                                            SymDefObject symDef = null)
        {
            if (scheme == null)
            {
                scheme = new NullAsymScheme();
            }
            if (symDef == null)
            {
                symDef = new SymDefObject();
            }

            var sens = new Sensitive();
            if (RsaProvider != null)
            {
                RSAParameters parms = RsaProvider.ExportParameters(true);
                var rsaPriv = new Tpm2bPrivateKeyRsa(parms.P);
                sens = new Sensitive(new byte[0], new byte[0], rsaPriv);

                tpmPub = new TpmPublic(nameAlg, keyAttrs, new byte[0],
                                       new RsaParms(symDef,
                                                    scheme,
                                                    (ushort)(8 * parms.Modulus.Length),
                                                    (uint)(new BigInteger(parms.Exponent))),
                                       new Tpm2bPublicKeyRsa(parms.Modulus));
            }
            else if (EcdsaProvider != null || EcDhProvider != null)
            {
                Globs.Throw<NotSupportedException>("Blobs for ECC keys are not supported.");
                tpmPub = new TpmPublic();
            }
            else
            {
                Globs.Throw<NotSupportedException>("Blobs for non-RSA, non-ECC keys are not supported.");
                tpmPub = new TpmPublic();
            }

            return new TpmPrivate(sens.GetTpm2BRepresentation());
        }

        public Sensitive GetSensitive()
        {
            TpmPublic pub;
            TpmPrivate priv = GetPrivate(out pub);
            var m = new Marshaller(priv.buffer);
            ushort privSize = m.Get<UInt16>();
            if (priv.buffer.Length != privSize + 2)
            {
                Globs.Throw("Invalid key blob");
            }
            return m.Get<Sensitive>();
        }

        /// <summary>
        /// Sign using the hash algorithm specified during object instantiation. 
        /// </summary>
        /// <param name="data"></param>
        /// <returns></returns>
        public ISignatureUnion Sign(byte[] data)
        {
            return SignData(data, TpmAlgId.Null);
        }

        /// <summary>
        /// Sign using a non-default hash algorithm.
        /// </summary>
        /// <param name="data"></param>
        /// <param name="sigHash"></param>
        /// <returns></returns>
        public ISignatureUnion SignData(byte[] data, TpmAlgId sigHash)
        {
            var rsaParams = PublicParms.parameters as RsaParms;
            if (rsaParams != null)
            {
                Debug.Assert(RsaProvider != null);
                TpmAlgId sigScheme = rsaParams.scheme.GetUnionSelector();

                switch (sigScheme)
                {
                    case TpmAlgId.Rsassa:
                    {
                        if (sigHash == TpmAlgId.Null)
                        {
                            sigHash = (rsaParams.scheme as SigSchemeRsassa).hashAlg;
                        }
                        byte[] sig = RsaProvider.SignData(data, CryptoLib.GetHashAlgorithmName(sigHash), RSASignaturePadding.Pkcs1);
                        return new SignatureRsassa(sigHash, sig);
                    }
                    case TpmAlgId.Rsapss:
                    {
                        if (sigHash == TpmAlgId.Null)
                        {
                            sigHash = (rsaParams.scheme as SigSchemeRsapss).hashAlg;
                        }
                        byte[] sig = RsaProvider.SignData(data, CryptoLib.GetHashAlgorithmName(sigHash), RSASignaturePadding.Pss);
                        return new SignatureRsapss(sigHash, sig);
                    }
                }
                Globs.Throw<ArgumentException>("Unsupported signature scheme");
                return null;
            }

            var eccParms = PublicParms.parameters as EccParms;
            if (eccParms != null)
            {
                if (eccParms.scheme.GetUnionSelector() != TpmAlgId.Ecdsa)
                {
                    Globs.Throw<ArgumentException>("Unsupported ECC sig scheme");
                    return null;
                }
                if (sigHash == TpmAlgId.Null)
                {
                    sigHash = (eccParms.scheme as SigSchemeEcdsa).hashAlg;
                }
                byte[] digest = CryptoLib.HashData(sigHash, data);
                Debug.Assert(EcdsaProvider != null);
                byte[] sig = EcdsaProvider.SignData(data, CryptoLib.GetHashAlgorithmName(sigHash));

                int fragLen = sig.Length / 2;
                var r = Globs.CopyData(sig, 0, fragLen);
                var s = Globs.CopyData(sig, fragLen, fragLen);
                return new SignatureEcdsa(sigHash, r, s);
            }

            // Should never be here
            Globs.Throw("VerifySignature: Unrecognized asymmetric algorithm");
            return null;
        } // SignData()

        /// <summary>
        /// Verifies the signature over a digest.
        /// The signing scheme is retrieved from the signature. The verification key
        /// shall have either compatible or null scheme.
        /// </summary>
        /// <param name="digest">Digest to check against the signature</param>
        /// <param name="signature">The signature</param>
        /// <returns>True if the verification succeeds.</returns>
        public bool VerifySignatureOverHash(byte[] digest, ISignatureUnion signature)
        {
            return VerifySignature(digest ?? new byte[0], true, signature);
        }

        /// <summary>
        /// Verifies the signature over data.
        /// The data will be hashed internall by the method using hash algorithm from
        /// the signing scheme digest computed from the specified data buffer.
        /// The signing scheme is retrieved from the signature. The verification key
        /// shall have either compatible or null scheme.
        /// </summary>
        /// <param name="signedData">Data buffer to check against the signature</param>
        /// <param name="signature">The signature</param>
        /// <returns>True if the verification succeeds.</returns>
        public bool VerifySignatureOverData(byte[] signedData, ISignatureUnion signature)
        {
            return VerifySignature(signedData, false, signature);
        }

        /// <summary>
        /// Verifies the signature over data or a digest.
        /// The data will be hashed internall by the method using hash algorithm from
        /// the signing scheme digest computed from the specified data buffer.
        /// The signing scheme is retrieved from the signature. The verification key
        /// shall have either compatible or null scheme.
        /// </summary>
        /// <param name="data">Byte buffer containing either digest or data to check against the signature</param>
        /// <param name="dataIsDigest">Specifies the type of 'data' parameter contents</param>
        /// <param name="signature">The signature</param>
        /// <returns>True if the verification succeeds.</returns>
        private bool VerifySignature(byte[] data, bool dataIsDigest, ISignatureUnion sig)
        {
            TpmAlgId sigScheme = sig.GetUnionSelector();
            TpmAlgId sigHash = CryptoLib.SchemeHash(sig);

            var rsaParams = PublicParms.parameters as RsaParms;
            if (rsaParams != null)
            {
                Debug.Assert(RsaProvider != null);
                var s = sig as SignatureRsa;
                TpmAlgId keyScheme = rsaParams.scheme.GetUnionSelector();

                if (keyScheme != TpmAlgId.Null && keyScheme != sigScheme)
                {
                    Globs.Throw<ArgumentException>("Key scheme and signature scheme do not match");
                }

                var paddingScheme = RSASignaturePadding.Pkcs1;
                switch (sigScheme)
                {
                    case TpmAlgId.Rsassa:
                    {
                        paddingScheme = RSASignaturePadding.Pkcs1;
                        break;
                    }
                    case TpmAlgId.Rsapss:
                    {
                        paddingScheme = RSASignaturePadding.Pss;
                        break;
                    }
                    default:
                    {
                        Globs.Throw<ArgumentException>("VerifySignature(): Unrecognized scheme");
                        break;
                    }
                }

                var sRsa = sig as SignatureRsa;
                if (dataIsDigest)
                {
                    return RsaProvider.VerifyHash(data, sRsa.sig, CryptoLib.GetHashAlgorithmName(sigHash), paddingScheme);
                }
                return RsaProvider.VerifyData(data, sRsa.sig, CryptoLib.GetHashAlgorithmName(sigHash), paddingScheme);
            }

            var eccParams = PublicParms.parameters as EccParms;
            if (eccParams != null)
            {
                if (eccParams.scheme.GetUnionSelector() != TpmAlgId.Ecdsa)
                {
                    Globs.Throw<ArgumentException>("Unsupported ECC sig scheme");
                }
                TpmAlgId keyScheme = eccParams.scheme.GetUnionSelector();

                if (keyScheme != TpmAlgId.Null && keyScheme != sigScheme)
                {
                    Globs.Throw<ArgumentException>("Key scheme and signature scheme do not match");
                }

                var s = sig as SignatureEcdsa;
                byte[] sigBlob = Globs.Concatenate(s.signatureR, s.signatureS);
                Debug.Assert(EcdsaProvider != null);
                if (dataIsDigest)
                {
                    return EcdsaProvider.VerifyHash(data, sigBlob);
                }
                return EcdsaProvider.VerifyData(data, sigBlob, CryptoLib.GetHashAlgorithmName(sigHash));
            }

            // Should never be here
            Globs.Throw("VerifySignature: Unrecognized asymmetric algorithm");
            return false;
        } // VerifySignature()

        /// <summary>
        /// Generates the key exchange key and the public part of the ephemeral key
        /// using specified encoding parameters in the KDF (ECC only).
        /// </summary>
        /// <param name="encodingParms"></param>
        /// <param name="decryptKeyNameAlg"></param>
        /// <param name="ephemPub"></param>
        /// <returns>key exchange key blob</returns>
        public byte[] EcdhGetKeyExchangeKey(byte[] encodingParms, TpmAlgId decryptKeyNameAlg, out EccPoint ephemPub)
        {
            byte[] keyExchangeKey = null;
            ephemPub = null;
            var eccParms = (EccParms)PublicParms.parameters;

            // Make a new ephemeral key
            using (ECDiffieHellman eph = ECDiffieHellman.Create(RawEccKey.GetEccCurve(eccParms.curveID)))
            {
                HashAlgorithmName hash = CryptoLib.GetHashAlgorithmName(decryptKeyNameAlg);
                ECPoint ephPub = eph.PublicKey.ExportParameters().Q;
                ephemPub = new EccPoint(ephPub.X, ephPub.Y);
                byte[] otherInfo = Globs.Concatenate(new[] {encodingParms, ephPub.X, EcDhProvider.PublicKey.ExportParameters().Q.X});

                // The TPM uses the following number of bytes from the KDF
                int bytesNeeded = CryptoLib.DigestSize(decryptKeyNameAlg);
                keyExchangeKey = new byte[bytesNeeded];

                for (int pos = 0, count = 1, bytesToCopy = 0;
                     pos < bytesNeeded;
                     ++count, pos += bytesToCopy)
                {
                    byte[] secretPrepend = Marshaller.GetTpmRepresentation((UInt32)count);
                    byte[] fragment = eph.DeriveKeyFromHash(EcDhProvider.PublicKey, hash, secretPrepend, otherInfo);
                    bytesToCopy = Math.Min(bytesNeeded - pos, fragment.Length);
                    Array.Copy(fragment, 0, keyExchangeKey, pos, bytesToCopy);
                }
            }
            return keyExchangeKey;
        }

        internal TpmAlgId OaepHash
        {
            get
            {
                var rsaParams = (RsaParms)PublicParms.parameters;
                var hashAlg = PublicParms.nameAlg;
                if (rsaParams.scheme is SchemeOaep)
                    hashAlg = (rsaParams.scheme as SchemeOaep).hashAlg;
                else if (rsaParams.scheme is EncSchemeOaep)
                    hashAlg = (rsaParams.scheme as EncSchemeOaep).hashAlg;
                return hashAlg;
            }
        }

        /// <summary>
        /// Encrypt dataToEncrypt using the specified encodingParams (RSA only).
        /// </summary>
        /// <param name="plainText"></param>
        /// <param name="label"></param>
        /// <returns></returns>
        public byte[] EncryptOaep(byte[] plainText, byte[] label)
        {
            if (plainText == null)
                plainText = new byte[0];
            if (label == null)
                label = new byte[0];
            var rr = new RawRsa(RsaProvider.ExportParameters(false), RsaProvider.KeySize);
            byte[] cipherText = rr.OaepEncrypt(plainText, OaepHash, label);
            return cipherText;
        }

        public byte[] DecryptOaep(byte[] cipherText, byte[] label)
        {
            var rr = new RawRsa(RsaProvider.ExportParameters(true), RsaProvider.KeySize);
            byte[] plainText = rr.OaepDecrypt(cipherText, OaepHash, label);
            return plainText;
        }

        public void Dispose()
        {
            if (RsaProvider != null)
            {
                RsaProvider.Dispose();
            }
            if (EcdsaProvider != null)
            {
                EcdsaProvider.Dispose();
            }
            if (EcDhProvider != null)
            {
                EcDhProvider.Dispose();
            }
        }
    } // class AsymCryptoSystem

    public class RawRsa
    {
        internal int NumBits = 0;

        /// <summary>
        /// Modulus (internal key) = P * Q
        /// </summary>
        internal BigInteger N;

        /// <summary>
        /// Public (encryption) exponent (typically 65537)
        /// </summary>
        internal BigInteger E;

        /// <summary>
        ///  The first prime factor (private key)
        /// </summary>
        internal BigInteger P;

        /// <summary>
        ///  The second prime factor
        /// </summary>
        internal BigInteger Q;

        /// <summary>
        /// Private (decryption) exponent
        /// </summary>
        internal BigInteger D;

        internal BigInteger InverseQ;
        internal BigInteger DP;
        internal BigInteger DQ;

        internal int KeySize { get { return (NumBits + 7) / 8; } }

        /// <summary>
        /// Returns the public key in TPM-format
        /// </summary>
        /// <returns></returns>
        public byte[] Public { get { return ToBigEndian(N); } }

        /// <summary>
        /// Returns the RSA private key in TPM format (the first prime number)
        /// </summary>
        /// <returns></returns>
        public byte[] Private { get { return ToBigEndian(P); } }

        /// <summary>
        ///  Generates new key pair using OS CSP
        /// </summary>
        /// <param name="numBits"></param>
        /// <param name="publicExponent"></param>
        public RawRsa (int numBits, int publicExponent = 65537)
        {
            using (var prov = RSA.Create(numBits))
            {
                Init(prov.ExportParameters(true), numBits);
            }
        }

        /// <summary>
        /// Instantiates the object using a TPM generated key pair
        /// </summary>
        /// <param name="pub"></param>
        /// <param name="priv"></param>
        public RawRsa(TpmPublic pub, TpmPrivate priv)
        {
            var m = new Marshaller(priv.buffer);
            var privSize = m.Get<UInt16>();
            // Assert that the private key blob is in plain text 
            Debug.Assert(priv.buffer.Length == privSize + 2);
            var sens = m.Get<Sensitive>();
            Init(pub, sens.sensitive as Tpm2bPrivateKeyRsa);
        }

        public RawRsa(RSAParameters rsaParams, int numBits)
        {
            Init(rsaParams, numBits);
        }

        void Init(RSAParameters rsaParams, int numBits)
        {
            NumBits = numBits;
            E = FromBigEndian(rsaParams.Exponent);
            N = FromBigEndian(rsaParams.Modulus);
            Debug.Assert(Globs.ArraysAreEqual(ToBigEndian(N.ToByteArray()), rsaParams.Modulus));
            if (rsaParams.P != null)
            {
                D = FromBigEndian(rsaParams.D);
                P = FromBigEndian(rsaParams.P);
                Q = FromBigEndian(rsaParams.Q);
                InverseQ = FromBigEndian(rsaParams.InverseQ);
                DP = FromBigEndian(rsaParams.DP);
                DQ = FromBigEndian(rsaParams.DQ);
            }
        }

        void Init(TpmPublic pub, Tpm2bPrivateKeyRsa priv)
        {
            var parms = pub.parameters as RsaParms;

            NumBits = parms.keyBits;

            E = new BigInteger(parms.exponent == 0 ? RsaParms.DefaultExponent
                                                   : BitConverter.GetBytes(parms.exponent));
            N = FromBigEndian((pub.unique as Tpm2bPublicKeyRsa).buffer);
            P = FromBigEndian(priv.buffer);
            Q = N / P;
            Debug.Assert(N % P == BigInteger.Zero);

            BigInteger PHI = N - (P + Q - BigInteger.One);
            D = ModInverse(E, PHI);
            InverseQ = ModInverse(Q, P);
            DP = D % (P - BigInteger.One);
            DQ = D % (Q - BigInteger.One);
        }

        public static byte[] GetLabel(string label)
        {
            return GetLabel(Encoding.ASCII.GetBytes(label));
        }

        public static byte[] GetLabel(byte[] data)
        {
            if (data == null)
            {
                return new byte[0];
            }
            if (data.Length == 0)
            {
                return data;
            }
            int labelSize = 0;
            while (labelSize < data.Length && data[labelSize++] != 0)
            {
                continue;
            }
            var label = new byte[labelSize + (data[labelSize - 1] != 0 ? 1 : 0)];
            Array.Copy(data, label, labelSize);
            return label;
        }

        internal static BigInteger ModInverse(BigInteger a, BigInteger b)
        {
            BigInteger dividend = a % b;
            BigInteger divisor = b;

            BigInteger lastX = BigInteger.One;
            BigInteger currX = BigInteger.Zero;

            while (divisor.Sign > 0)
            {
                BigInteger quotient = dividend / divisor;
                BigInteger remainder = dividend % divisor;

                if (remainder.Sign <= 0)
                {
                    break;
                }

                BigInteger nextX = lastX - currX * quotient;

                lastX = currX;
                currX = nextX;

                dividend = divisor;
                divisor = remainder;
            }

            if (divisor != BigInteger.One)
            {
                throw new Exception("ModInverse(): Not coprime");
            }

            return (currX.Sign < 0 ? currX + b : currX);
        }

        /// <summary>
        /// Translate a byte array representing a big-endian (MSB first, possibly > 0x7F)
        /// TPM-style number to a BigInteger.
        /// </summary>
        /// <param name="b"></param>
        /// <returns></returns>
        public static BigInteger FromBigEndian(byte[] b)
        {
            return new BigInteger(ToLittleEndian(b));
        }

        /// <summary>
        /// Translates a BigInt into a TPM-style big-endian byte array.
        /// By default removes MSB-zeros.
        /// If sizeWanted is specified, pads with MSB-zeros to desired length.
        /// </summary>
        /// <param name="b"></param>
        /// <param name="sizeWanted"></param>
        /// <returns></returns>
        public static byte[] ToBigEndian(BigInteger b, int sizeWanted = -1)
        {
            return ToBigEndian(b.ToByteArray(), sizeWanted);
        }

        /// <summary>
        /// Translate a byte array representing a big-endian (MSB first, possibly > 0x7F)
        /// TPM-style number to the little endian representation.
        /// </summary>
        /// <param name="b"></param>
        /// <returns></returns>
        internal static byte[] ToLittleEndian(byte[] b)
        {
            int len = b.Length;
            var b2 = new byte[len + (b[0] > 0x7F ? 1 : 0)];

            for (int j = 0; j < len; j++)
            {
                b2[j] = b[len - 1 - j];
            }
            return b2;
        }

        /// <summary>
        /// Translates a little endian number represented as a byte array to TPM-style
        /// big-endian byte array. By default removes MSB-zeros.
        /// If sizeWanted is specified, pads with MSB-zeros to desired length.
        /// </summary>
        /// <param name="b"></param>
        /// <param name="sizeWanted"></param>
        /// <returns></returns>
        internal static byte[] ToBigEndian(byte[] b, int sizeWanted = -1)
        {
            int len = b.Length;

            // Count trailing zeros (MSB zeros to be removed)
            while (len > 0 && b[len - 1] == 0)
            {
                --len;
            }
            if (sizeWanted == -1)
            {
                sizeWanted = len;
            }

            int pad = sizeWanted - len;
            if (pad < 0)
            {
                Globs.Throw<ArgumentException>("ToBigEndian(): Too short size requested");
                return new byte[0];
            }

            var b2 = new byte[sizeWanted];

            for (int j = 0; j < len; j++)
            {
                b2[j + pad] = b[len - 1 - j];
            }
            return b2;
        }

        public byte[] RawEncrypt(byte[] plain)
        {
            BigInteger plainX = FromBigEndian(plain);
            BigInteger cipher = BigInteger.ModPow(plainX, E, N);
            byte[] cipherX = ToBigEndian(cipher, KeySize);
            return cipherX;
        }

        public byte[] RawDecrypt(byte[] cipher)
        {
            BigInteger cipherX = FromBigEndian(cipher);
            BigInteger plain = BigInteger.ModPow(cipherX, D, N);
            byte[] plainX = ToBigEndian(plain, KeySize);
            return plainX;
        }

        public byte[] OaepEncrypt(byte[] data, TpmAlgId hashAlg, byte[] encodingParms)
        {
            int encLen = NumBits / 8;
            byte[] zeroTermEncoding = GetLabel(encodingParms);
            byte[] encoded = CryptoEncoders.OaepEncode(data, zeroTermEncoding, hashAlg, encLen);
            BigInteger message = FromBigEndian(encoded);
            BigInteger cipher = BigInteger.ModPow(message, E, N);
            byte[] encMessageBigEnd = ToBigEndian(cipher, KeySize);
            if (encMessageBigEnd.Length < encLen)
                encMessageBigEnd = Globs.AddZeroToBeginning(encMessageBigEnd, encLen - encMessageBigEnd.Length);
            return encMessageBigEnd;
        }

        public byte[] OaepDecrypt(byte[] cipherText, TpmAlgId hashAlg, byte[] encodingParms)
        {
            byte[] zeroTermEncoding = GetLabel(encodingParms);
            BigInteger cipher = FromBigEndian(cipherText);
            BigInteger plain = BigInteger.ModPow(cipher, D, N);
            byte[] encMessage = ToBigEndian(plain, KeySize - 1);
            byte[] message;

            // Hack - be robust to leading zeros
            while (true)
            {
                bool decodeOk = CryptoEncoders.OaepDecode(encMessage, zeroTermEncoding, hashAlg, out message);
                if (decodeOk)
                {
                    break;
                }
                encMessage = Globs.AddZeroToBeginning(encMessage);
            }
            return message;
        }

        public byte[] PssSign(byte[] m, TpmAlgId hashAlg)
        {
            // The TPM uses the maximum salt length
            int defaultPssSaltLength = 0; // KeySize - CryptoLib.DigestSize(hashAlg) - 1 - 1;

            // Encode
            byte[] em = CryptoEncoders.PssEncode(m, hashAlg, defaultPssSaltLength, NumBits - 1);
            BigInteger message = FromBigEndian(em);

            // Sign
            BigInteger sig = BigInteger.ModPow(message, D, N);
            byte[] signature = ToBigEndian(sig, KeySize);
            return signature;
        }

        public bool PssVerify(byte[] m, byte[] signature, TpmAlgId hashAlg)
        {
            // The TPM uses the maximum salt length
            int defaultPssSaltLength = 0; //  KeySize - CryptoLib.DigestSize(hashAlg) - 1 - 1;
            BigInteger sig = FromBigEndian(signature);
            BigInteger emx = BigInteger.ModPow(sig, E, N);

            byte[] em = ToBigEndian(emx, KeySize);

            bool ok = CryptoEncoders.PssVerify(m, em, defaultPssSaltLength, NumBits - 1, hashAlg);
            return ok;
        }

        public byte[] PkcsSign(byte[] m, TpmAlgId hashAlg)
        {
            int k = KeySize;
            byte[] em = CryptoEncoders.Pkcs15Encode(m, k, hashAlg);
            BigInteger message = FromBigEndian(em);
            BigInteger sig = BigInteger.ModPow(message, D, N);
            byte[] signature = ToBigEndian(sig, KeySize);
            return signature;
        }

        public bool PkcsVerify(byte[] m, byte[] s, TpmAlgId hashAlg)
        {
            if (s.Length != KeySize)
            {
                Globs.Throw<ArgumentException>("PkcsVerify: Invalid signature");
                return false;
            }
            int k = KeySize;
            BigInteger sig = FromBigEndian(s);
            BigInteger emx = BigInteger.ModPow(sig, E, N);

            byte[] emDecrypted = ToBigEndian(emx, KeySize);

            byte[] emPrime = CryptoEncoders.Pkcs15Encode(m, k, hashAlg);
            if (!Globs.ArraysAreEqual(emPrime, emDecrypted))
            {
                return false;
            }
            return true;
        }
    }

    internal class RawEccKey
    {
        internal static ECParameters GetEccParameters(EccPoint pubId, EccCurve curveId)
        {
            var res = new ECParameters();
            res.Curve = GetEccCurve(curveId);
            res.Q = new ECPoint();
            res.Q.X = pubId.x;
            res.Q.Y = pubId.y;
            return res;
        }

        internal static bool IsCurveSupported(EccCurve curve)
        {
                return EccCurves.ContainsKey(curve);
        }

        static Dictionary<EccCurve, ECCurve> EccCurves = new Dictionary<EccCurve, ECCurve>() {
                            {EccCurve.NistP256, ECCurve.CreateFromFriendlyName("nistP256")},
                            {EccCurve.NistP384, ECCurve.CreateFromFriendlyName("nistP384")},
                            {EccCurve.NistP521, ECCurve.CreateFromFriendlyName("nistP521")},
                        };

        internal static ECCurve GetEccCurve(EccCurve curveID)
        {
            if (!IsCurveSupported(curveID))
            {
                Globs.Throw<ArgumentException>("Unsupported ECC curve");
                return ECCurve.CreateFromFriendlyName("nistP256");
            }
            ECCurve curve;
            EccCurves.TryGetValue(curveID, out curve);
            return curve;
        }

        internal static ECCurve GetEccCurve(TpmPublic pub)
        {
            if (pub.unique.GetUnionSelector() != TpmAlgId.Ecc)
            {
                Globs.Throw<ArgumentException>("Not an ECC key");
            }

            var eccParms = (EccParms)pub.parameters;

            bool signing = pub.objectAttributes.HasFlag(ObjectAttr.Sign);
            bool encrypting = pub.objectAttributes.HasFlag(ObjectAttr.Decrypt);
            if (!(signing ^ encrypting))
            {
                Globs.Throw<ArgumentException>("ECC Key must either sign or decrypt");
            }
            var scheme = eccParms.scheme.GetUnionSelector();
            if (signing && scheme != TpmAlgId.Ecdsa && scheme != TpmAlgId.Null)
            {
                Globs.Throw<ArgumentException>("Unsupported ECC signing scheme");
            }

            return GetEccCurve(eccParms.curveID);
        }
    }
}

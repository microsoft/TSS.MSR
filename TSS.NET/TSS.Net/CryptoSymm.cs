/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.Diagnostics;
using System.IO;

#if !TSS_USE_BCRYPT
using System.Security.Cryptography;
#endif

namespace Tpm2Lib
{
    /// <summary>
    /// A helper class for doing symmetric cryptography based on 
    /// TPM structure definitions.
    /// </summary>
    public sealed class SymCipher : IDisposable
    {
        public bool LimitedSupport = false;

#if TSS_USE_BCRYPT
        private BCryptKey Key;
        private byte[] KeyBuffer;
        private byte[] IV;

        private SymCipher(BCryptKey key, byte[] keyData, byte[] iv, int blockSize)
        {
            Key = key;
            KeyBuffer = keyData;
            IV = Globs.CopyData(iv) ?? new byte[BlockSize];
            BlockSize = blockSize;
        }

        public byte[] KeyData { get { return KeyBuffer; } }

        public int BlockSize = 0;

        public int IVSize = 16;

#else
        private readonly SymmetricAlgorithm Alg;

        public byte[] KeyData { get { return Alg.Key; } }

        /// <summary>
        /// Block size in bytes.
        /// </summary>
        public int BlockSize { get { return Alg.BlockSize / 8; } }

        /// <summary>
        /// Initialization vector size in bytes.
        /// </summary>
        public int IVSize { get { return Alg.IV.Length; } }

        private SymCipher(SymmetricAlgorithm alg)
        {
            Alg = alg;
        }
#endif

        /// <summary>
        /// Block size in bytes.
        /// </summary>
        public static implicit operator byte[] (SymCipher sym)
        {
            return sym == null ? null : sym.KeyData;
        }

        public static int GetBlockSize(SymDefObject symDef)
        {
            if (symDef.Algorithm == TpmAlgId.Tdes)
            {
                return 8;
            }
            if (symDef.Algorithm != TpmAlgId.Aes)
            {
                Globs.Throw<ArgumentException>("Unsupported algorithm " + symDef.Algorithm);
                return 0;
            }
            return 16;
        }

        /// <summary>
        /// Create a new SymCipher object with a random key based on the alg and mode supplied.
        /// </summary>
        /// <param name="symDef"></param>
        /// <param name="keyData"></param>
        /// <param name="iv"></param>
        /// <returns></returns>
        public static SymCipher Create(SymDefObject symDef = null,
                                       byte[] keyData = null, byte[] iv = null)
        {
            if (symDef == null)
            {
                symDef = new SymDefObject(TpmAlgId.Aes, 128, TpmAlgId.Cfb);
            }

#if TSS_USE_BCRYPT
            BCryptAlgorithm alg = null;

            switch (symDef.Algorithm)
            {
                case TpmAlgId.Aes:
                    alg = new BCryptAlgorithm(Native.BCRYPT_AES_ALGORITHM);
                    break;
                case TpmAlgId.Tdes:
                    alg = new BCryptAlgorithm(Native.BCRYPT_3DES_ALGORITHM);
                    break;
                default:
                    Globs.Throw<ArgumentException>("Unsupported symmetric algorithm "
                                                   + symDef.Algorithm);
                    return null;
            }

            if (keyData == null)
            {
                keyData = Globs.GetRandomBytes(symDef.KeyBits / 8);
            }
            var key = alg.GenerateSymKey(symDef, keyData, GetBlockSize(symDef));
            //key = BCryptInterface.ExportSymKey(keyHandle);
            //keyHandle = alg.LoadSymKey(key, symDef, GetBlockSize(symDef));
            alg.Close();
            return key == null ? null : new SymCipher(key, keyData, iv, GetBlockSize(symDef));
#else
            if (symDef.Mode == TpmAlgId.Ofb)
                return null;
            var mode = GetCipherMode(symDef.Mode);
            if (mode == CipherMode_None)
                return null;

            SymmetricAlgorithm alg = null; // = new RijndaelManaged();
            bool limitedSupport = false;
            // DES and __3DES are not supported in TPM 2.0 rev. 0.96 to 1.30
            switch (symDef.Algorithm) {
                case TpmAlgId.Aes:
                    alg = new RijndaelManaged();
                    break;
                case TpmAlgId.Tdes:
                    alg = new TripleDESCryptoServiceProvider();
                    limitedSupport = true;
                    break;
                default:
                    Globs.Throw<ArgumentException>("Unsupported symmetric algorithm " + symDef.Algorithm);
                    break;
            }

            int blockSize = GetBlockSize(symDef);
            alg.KeySize = symDef.KeyBits;
            alg.BlockSize = blockSize * 8;
            alg.Padding = PaddingMode.None;
            alg.Mode = mode;

            // REVISIT: Get this right for other modes
            if (symDef.Algorithm == TpmAlgId.Tdes && symDef.Mode == TpmAlgId.Cfb)
            {
                alg.FeedbackSize = 8;
            }
            else
            {
                alg.FeedbackSize = alg.BlockSize;
            }

            if (keyData == null)
            {
                // Generate random key
                alg.IV = Globs.GetZeroBytes(blockSize);
                try
                {
                    alg.GenerateKey();
                }
                catch (Exception)
                {
                    alg.Dispose();
                    throw;
                }
            }
            else
            {
                // Use supplied key bits
                alg.Key = keyData;
                if (iv == null)
                {
                    iv = Globs.GetZeroBytes(blockSize);
                }
                else if (iv.Length != blockSize)
                {
                    Array.Resize(ref iv, blockSize);
                }
                alg.IV = iv;
            }

            var symCipher = new SymCipher(alg);
            symCipher.LimitedSupport = limitedSupport;
            return symCipher;
#endif
        }

#if !TSS_USE_BCRYPT
        const CipherMode CipherMode_None = (CipherMode)0;

        public static CipherMode GetCipherMode(TpmAlgId cipherMode)
        {
            switch (cipherMode)
            {
                case TpmAlgId.Cfb:
                    return CipherMode.CFB;
                case TpmAlgId.Ofb:
                    return CipherMode.OFB;
                case TpmAlgId.Cbc:
                    return CipherMode.CBC;
                case TpmAlgId.Ecb:
                    return CipherMode.ECB;
                default:
                    Globs.Throw<ArgumentException>("GetCipherMode: Unsupported cipher mode");
                    return CipherMode_None;
            }
        }
#endif

        public static SymCipher CreateFromPublicParms(IPublicParmsUnion parms)
        {
            switch (parms.GetUnionSelector())
            {
                case TpmAlgId.Rsa:
                    return Create((parms as RsaParms).symmetric);
                case TpmAlgId.Ecc:
                    return Create((parms as EccParms).symmetric);
                default:
                    Globs.Throw<ArgumentException>("CreateFromPublicParms: Unsupported algorithm");
                    return null;
            }
        }

        public static byte[] Encrypt(SymDefObject symDef, byte[] key, byte[] iv,
                                     byte[] dataToEncrypt)
        {
            using (SymCipher cipher = Create(symDef, key, iv))
            {
                return cipher.Encrypt(dataToEncrypt);
            }
        }

        public static byte[] Decrypt(SymDefObject symDef, byte[] key, byte[] iv,
                                     byte[] dataToDecrypt)
        {
            using (SymCipher cipher = Create(symDef, key, iv))
            {
                return cipher.Decrypt(dataToDecrypt);
            }
        }

        /// <summary>
        /// Performs the TPM-defined CFB encrypt using the associated algorithm.
        /// This routine assumes that the integrity value has been prepended.
        /// </summary>
        /// <param name="data"></param>
        /// <param name="iv"></param>
        /// <returns></returns>
        public byte[] Encrypt(byte[] data, byte[] iv = null)
        {
            byte[] paddedData;
            int unpadded = data.Length % BlockSize;
            paddedData = unpadded == 0 ? data : Globs.AddZeroToEnd(data, BlockSize - unpadded);
#if TSS_USE_BCRYPT
            paddedData = Key.Encrypt(paddedData, null, iv ?? IV);
#else
            bool externalIV = iv != null && iv.Length > 0;
            if (externalIV)
                Alg.IV = iv;

            ICryptoTransform enc = Alg.CreateEncryptor();
            using (var outStream = new MemoryStream())
            {
                var s = new CryptoStream(outStream, enc, CryptoStreamMode.Write);
                s.Write(paddedData, 0, paddedData.Length);
                s.FlushFinalBlock();
                paddedData = outStream.ToArray();
            }

            if (externalIV)
            {
                var src = data;
                var res = paddedData;
                if (res.Length > iv.Length)
                {
                    src = Globs.CopyData(data, src.Length - iv.Length, iv.Length);
                    res = Globs.CopyData(paddedData, res.Length - iv.Length, iv.Length);
                }

                switch(Alg.Mode)
                {
                case CipherMode.CBC:
                case CipherMode.CFB:
                    res.CopyTo(iv, 0);
                    break;
                case CipherMode.OFB:
                    XorEngine.Xor(res, src).CopyTo(iv, 0);
                    break;
                case CipherMode.ECB:
                    break;
                case CipherMode.CTS:
                    Globs.Throw<ArgumentException>("Encrypt: Unsupported symmetric mode");
                    break;
                }
            }
#endif
            return unpadded == 0 ? paddedData : Globs.CopyData(paddedData, 0, data.Length);
        }

        public byte[] Decrypt(byte[] data, byte[] iv = null)
        {
            byte[] paddedData;
            int unpadded = data.Length % BlockSize;
            paddedData = unpadded == 0 ? data : Globs.AddZeroToEnd(data, BlockSize - unpadded);
#if TSS_USE_BCRYPT
            paddedData = Key.Decrypt(paddedData, null, iv ?? IV);
            return Globs.CopyData(paddedData, 0, data.Length);
#else
            bool externalIV = iv != null && iv.Length > 0;
            if (externalIV)
                Alg.IV = iv;

            var tempOut = new byte[data.Length];
            ICryptoTransform dec = Alg.CreateDecryptor();
            using (var outStream = new MemoryStream(paddedData))
            {
                var s = new CryptoStream(outStream, dec, CryptoStreamMode.Read);
                int numPlaintextBytes = s.Read(tempOut, 0, data.Length);
                Debug.Assert(numPlaintextBytes == data.Length);
            }

            if (externalIV)
            {
                var src = data;
                var res = tempOut;
                if (res.Length > iv.Length)
                {
                    src = Globs.CopyData(paddedData, src.Length - iv.Length, iv.Length);
                    res = Globs.CopyData(tempOut, res.Length - iv.Length, iv.Length);
                }

                switch(Alg.Mode)
                {
                case CipherMode.CBC:
                case CipherMode.CFB:
                    src.CopyTo(iv, 0);
                    break;
                case CipherMode.OFB:
                    XorEngine.Xor(res, src).CopyTo(iv, 0);
                    break;
                case CipherMode.ECB:
                    break;
                case CipherMode.CTS:
                    Globs.Throw<ArgumentException>("Decrypt: Unsupported symmetric mode");
                    break;
                }
            }
            return tempOut;
#endif
        }

        /// <summary>
        /// De-envelope inner-wrapped duplication blob.
        /// TODO: Move this to TpmPublic and make it fully general
        /// </summary>
        /// <param name="exportedPrivate"></param>
        /// <param name="encAlg"></param>
        /// <param name="encKey"></param>
        /// <param name="nameAlg"></param>
        /// <param name="name"></param>
        /// <returns></returns>
        public static Sensitive SensitiveFromDupBlob(TpmPrivate exportedPrivate,
                                                     SymDefObject encAlg, byte[] encKey,
                                                     TpmAlgId nameAlg, byte[] name)
        {
            byte[] dupBlob = exportedPrivate.buffer;
            byte[] sensNoLen = null;
            using (SymCipher c = Create(encAlg, encKey))
            {
                byte[] innerObject = null;
                if (c == null)
                {
                    if (encAlg.Algorithm != TpmAlgId.Null)
                        return null;
                    else
                        return Marshaller.FromTpmRepresentation<Sensitive>(Marshaller.Tpm2BToBuffer(dupBlob));
                }
                innerObject = c.Decrypt(dupBlob);

                byte[] innerIntegrity, sensitive;
                KDF.Split(innerObject,
                          16 + CryptoLib.DigestSize(nameAlg) * 8,
                          out innerIntegrity,
                          8 * (innerObject.Length - CryptoLib.DigestSize(nameAlg) - 2),
                          out sensitive);

                byte[] expectedInnerIntegrity = Marshaller.ToTpm2B(
                                        CryptoLib.HashData(nameAlg, sensitive, name));

                if (!Globs.ArraysAreEqual(expectedInnerIntegrity, innerIntegrity))
                {
                    Globs.Throw("SensitiveFromDupBlob: Bad inner integrity");
                }

                sensNoLen = Marshaller.Tpm2BToBuffer(sensitive);
            }
            var sens = Marshaller.FromTpmRepresentation<Sensitive>(sensNoLen);
            return sens;
        }

        public void Dispose()
        {
#if TSS_USE_BCRYPT
            Key.Dispose();
#else
            Alg.Dispose();
#endif
        }
    }
}

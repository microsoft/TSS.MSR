/*++

Copyright (c) 2010-2015 Microsoft Corporation
Microsoft Confidential

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
    public sealed class SymmCipher : IDisposable
    {
#if TSS_USE_BCRYPT
        private BCryptKey Key;
        private byte[] KeyBuffer;
        private byte[] IV;

        private SymmCipher(BCryptKey key, byte[] keyData, byte[] iv)
        {
            Key = key;
            KeyBuffer = keyData;
            IV = Globs.CopyData(iv) ?? new byte[BlockSize];
        }

        public byte[] KeyData { get { return KeyBuffer; } }

        public int BlockSize { get { return 16; } }

#else
        private readonly SymmetricAlgorithm Alg;

        public byte[] KeyData { get { return Alg.Key; } }

        /// <summary>
        /// Block size in bytes.
        /// </summary>
        public int BlockSize { get { return Alg.BlockSize / 8; } }

        private SymmCipher(SymmetricAlgorithm alg)
        {
            Alg = alg;
        }
#endif

        public static int GetBlockSize(SymDefObject symDef)
        {
            if (symDef.Algorithm != TpmAlgId.Aes)
            {
                Globs.Throw<ArgumentException>("Unsupported algorithm " + symDef.Algorithm);
                return 0;
            }
            return 16;
        }

        /// <summary>
        /// Create a new SymmCipher object with a random key based on the alg and mode supplied.
        /// </summary>
        /// <param name="algId"></param>
        /// <param name="numBits"></param>
        /// <param name="mode"></param>
        /// <returns></returns>
        public static SymmCipher Create(SymDefObject symDef = null, byte[] keyData = null, byte[] iv = null)
        {
            if (symDef == null)
            {
                symDef = new SymDefObject(TpmAlgId.Aes, 128, TpmAlgId.Cfb);
            }
            else if (symDef.Algorithm != TpmAlgId.Aes)
            {
                Globs.Throw<ArgumentException>("Unsupported symmetric algorithm " + symDef.Algorithm);
                return null;
            }

#if TSS_USE_BCRYPT
            var alg = new BCryptAlgorithm(Native.BCRYPT_AES_ALGORITHM);

            if (keyData == null)
            {
                keyData = Globs.GetRandomBytes(symDef.KeyBits / 8);
            }
            var key = alg.GenerateSymKey(symDef, keyData, GetBlockSize(symDef));
            //key = BCryptInterface.ExportSymKey(keyHandle);
            //keyHandle = alg.LoadSymKey(key, symDef, GetBlockSize(symDef));
            alg.Close();
            return new SymmCipher(key, keyData, iv);
#else
            SymmetricAlgorithm alg = new RijndaelManaged();
            // DES and __3DES are not supported in TPM 2.0 v 0.96 and above
            //switch (algId) {
            //    case TpmAlgId.Aes: alg = new RijndaelManaged(); break;
            //    case TpmAlgId.__3DES: alg = new TripleDESCryptoServiceProvider(); break;
            //    case TpmAlgId.Des: alg = new DESCryptoServiceProvider(); break;
            //}
            int blockSize = GetBlockSize(symDef);
            alg.KeySize = symDef.KeyBits;
            alg.BlockSize = blockSize * 8;
            alg.Padding = PaddingMode.None;
            alg.Mode = GetCipherMode(symDef.Mode);
            // REVISIT: Get this right for other modes
            alg.FeedbackSize = alg.BlockSize;
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
            return new SymmCipher(alg);
#endif
        }

#if !TSS_USE_BCRYPT
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
                    throw new Exception("unsupported cipher mode");
            }
        }
#endif

        public static SymmCipher CreateFromPublicParms(IPublicParmsUnion parms)
        {
            switch (parms.GetUnionSelector())
            {
                case TpmAlgId.Rsa:
                    return Create((parms as RsaParms).symmetric);
                default:
                    throw new Exception("Unsupported algorithm");
            }
        }

        public static byte[] Encrypt(SymDefObject symDef, byte[] key, byte[] iv, byte[] dataToEncrypt)
        {
            using (SymmCipher cipher = Create(symDef, key, iv))
            {
                return cipher.CFBEncrypt(dataToEncrypt);
            }
        }

        public static byte[] Decrypt(SymDefObject symDef, byte[] key, byte[] iv, byte[] dataToDecrypt)
        {
            using (SymmCipher cipher = Create(symDef, key, iv))
            {
                return cipher.CFBDecrypt(dataToDecrypt);
            }
        }

        /// <summary>
        /// Performs the TPM-defined CFB encrypt using the associated algorithm.  This routine assumes that 
        /// the integrity value has been prepended.
        /// </summary>
        /// <param name="x"></param>
        /// <param name="iv"></param>
        /// <returns></returns>
        public byte[] CFBEncrypt(byte[] data, byte[] iv = null)
        {
            byte[] paddedData;
            int unpadded = data.Length % BlockSize;
            paddedData = unpadded == 0 ? data : Globs.AddZeroToEnd(data, BlockSize - unpadded);
#if TSS_USE_BCRYPT
            paddedData = Key.Encrypt(paddedData, null, iv ?? IV);
#else
            if (iv != null)
            {
                Alg.IV = iv;
            }

            ICryptoTransform enc = Alg.CreateEncryptor();
            using (var outStream = new MemoryStream())
            {
                var s = new CryptoStream(outStream, enc, CryptoStreamMode.Write);
                s.Write(paddedData, 0, paddedData.Length);
                s.FlushFinalBlock();
                paddedData = outStream.ToArray();
            }
#endif
            return unpadded == 0 ? paddedData : Globs.CopyData(paddedData, 0, data.Length);
        }

        public byte[] CFBDecrypt(byte[] data, byte[] iv = null)
        {
            byte[] paddedData;
            int unpadded = data.Length % BlockSize;
            paddedData = unpadded == 0 ? data : Globs.AddZeroToEnd(data, BlockSize - unpadded);
#if TSS_USE_BCRYPT
            paddedData = Key.Decrypt(paddedData, null, iv ?? IV);
            return Globs.CopyData(paddedData, 0, data.Length);
#else
            ICryptoTransform dec = Alg.CreateDecryptor();
            using (var outStream = new MemoryStream(paddedData))
            {
                var s = new CryptoStream(outStream, dec, CryptoStreamMode.Read);
                var tempOut = new byte[data.Length];
                int numPlaintextBytes = s.Read(tempOut, 0, data.Length);
                Debug.Assert(numPlaintextBytes == data.Length);
                return tempOut;
            }
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
        public static Sensitive SensitiveFromDuplicateBlob(TpmPrivate exportedPrivate, SymDefObject encAlg, byte[] encKey, TpmAlgId nameAlg, byte[] name)
        {
            byte[] dupBlob = exportedPrivate.buffer;
            byte[] sensNoLen;
            using (SymmCipher c = Create(encAlg, encKey))
            {
                byte[] innerObject = c.CFBDecrypt(dupBlob);
                byte[] innerIntegrity, sensitive;

                KDF.Split(innerObject,
                          16 + CryptoLib.DigestSize(nameAlg) * 8,
                          out innerIntegrity,
                          8 * (innerObject.Length - CryptoLib.DigestSize(nameAlg) - 2),
                          out sensitive);

                byte[] expectedInnerIntegrity = Marshaller.ToTpm2B(CryptoLib.HashData(nameAlg, sensitive, name));

                if (!Globs.ArraysAreEqual(expectedInnerIntegrity, innerIntegrity))
                {
                    throw new Exception("Bad inner integrity");
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

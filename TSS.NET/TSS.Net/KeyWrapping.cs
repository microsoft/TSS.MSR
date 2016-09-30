/*++

Copyright (c) 2010-2015 Microsoft Corporation
Microsoft Confidential

*/
using System.Diagnostics;

namespace Tpm2Lib
{
    /// <summary>
    /// This class contains algorithms for wrapping and unwrapping TPM objects
    /// </summary>
    internal class KeyWrapper
    {
        private KeyWrapper()
        {
        }

        /// <summary>
        /// Create an enveloped (encrypted and integrity protected) private area from a provided sensitive.
        /// </summary>
        /// <param name="iv"></param>
        /// <param name="sens"></param>
        /// <param name="nameHash"></param>
        /// <param name="publicName"></param>
        /// <param name="symWrappingAlg"></param>
        /// <param name="symKey"></param>
        /// <param name="parentNameAlg"></param>
        /// <param name="parentSeed"></param>
        /// <param name="f"></param>
        /// <returns></returns>
        public static byte[] CreatePrivateFromSensitive(
            SymDefObject symWrappingAlg,
            byte[] symKey,
            byte[] iv,
            Sensitive sens,
            TpmAlgId nameHash,
            byte[] publicName,
            TpmAlgId parentNameAlg,
            byte[] parentSeed,
            TssObject.Transformer f = null)
        {
            // ReSharper disable once InconsistentNaming
            byte[] tpm2bIv = Marshaller.ToTpm2B(iv);
            Transform(tpm2bIv, f);

            byte[] sensitive = sens.GetTpmRepresentation();
            Transform(sensitive, f);

            // ReSharper disable once InconsistentNaming
            byte[] tpm2bSensitive = Marshaller.ToTpm2B(sensitive);
            Transform(tpm2bSensitive, f);

            byte[] encSensitive = SymmCipher.Encrypt(symWrappingAlg, symKey, iv, tpm2bSensitive);
            Transform(encSensitive, f);
            byte[] decSensitive = SymmCipher.Decrypt(symWrappingAlg, symKey, iv, encSensitive);
            Debug.Assert(f != null || Globs.ArraysAreEqual(decSensitive, tpm2bSensitive));

            var hmacKeyBits = CryptoLib.DigestSize(parentNameAlg) * 8;
            byte[] hmacKey = KDF.KDFa(parentNameAlg, parentSeed, "INTEGRITY", new byte[0], new byte[0], hmacKeyBits);
            Transform(hmacKey, f);

            byte[] dataToHmac = Marshaller.GetTpmRepresentation(tpm2bIv,
                                                                encSensitive,
                                                                publicName);
            Transform(dataToHmac, f);

            byte[] outerHmac = CryptoLib.HmacData(parentNameAlg, hmacKey, dataToHmac);
            Transform(outerHmac, f);

            byte[] priv = Marshaller.GetTpmRepresentation(Marshaller.ToTpm2B(outerHmac),
                                                          tpm2bIv,
                                                          encSensitive);
            Transform(priv, f);
            return priv;
        }

        private static void Transform(byte[] x, TssObject.Transformer f)
        {
            if (f == null)
            {
                return;
            }
            f(x);
        }
    }
}

/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.Linq;
using System.Collections.Generic;
using Tpm2Lib;

namespace Tpm2Tester
{
    public class TpmConfig
    {
        //
        // Parameters of the TPM device
        //

        public uint TpmVersion;
        public DateTime TpmSpecDate;


        //
        // TPM device config
        //

        public bool PlatformDisabled = true;
        public bool PowerControl = false;
        public bool LocalityControl = false;
        public bool NvControl = false;

        public bool PlatformAuthUnknown = false;
        public bool LockoutAuthUnknown = false;

        // TPM is compliant with FIPS 140-2 requirements
        public bool FipsMode = false;

        //
        // TPM limits
        //

        // Max size of the data TPM2_Event accept (see TPM2B_EVENT in the Part 2)
        public const int MaxEventSize = 1024;

        // Size of the monotonic NV counter index
        public const ushort NvCounterSize = 8;

        // Largest digest that can be produced by the TPM (bytes)
        public ushort MaxDigestSize = 0;
        // Shortest digest that can be produced by the TPM (bytes)
        public ushort MinDigestSize = 0;
        // Maximal buffer size accepted by hashing commands (bytes)
        // (MAX_DIGEST_BUFFER, TPM_PT_INPUT_BUFFER)
        public ushort MaxInputBuffer = 0;
        // Maximal size of qualifying data (bytes)
        public ushort MaxQualDataSize = 0;

        // Maximal size of data stored in an NV index
        public ushort MaxNvIndexSize = 0;
        // Maximal size of a data chunk transferred in a single NV command invocation
        public ushort MaxNvOpSize = 0;
        // Lesser of MaxNvIndexSize and MaxNvOpSize
        public ushort SafeNvIndexSize = 0;

        // PCR banks defined in the TPM. Compact form (does not contain empty banks)
        public List<PcrSelection> PcrBanks = new List<PcrSelection>();

        // Hash algorithm with the largest digest
        public TpmAlgId LargestHashAlg = TpmAlgId.None;

        // Hash algorithm with the shortest digest
        public TpmAlgId ShortestHashAlg = TpmAlgId.None;

        // Size in bytes of the largest ECC key implemented by TPM
        public int MaxEccKeySize = 0;

        // Maximal size in bytes of key derivation label and context values
        public int MaxLabelSize = 0;

        //
        // Commands/algorithms/curves/key sizes supported by the TPM
        //

        // All commands implemented by the TPM
        public TpmCc[] SupportedCommands;

        // All implemented algorithms
        public TpmAlgId[] ImplementedAlgs;

        // Implemented hash algorithms
        public TpmAlgId[] HashAlgs;

        // Implemented hash algorithms including Null algorithm
        public TpmAlgId[] HashAlgsEx;

        public TpmAlgId ContextHashAlg;

        // Implemented ECC curves with their point coordinate sizes
        public Dictionary<EccCurve, AlgorithmDetailEcc> EccCurves;

        public EccCurve[] PrimaryCurves;
        public EccCurve[] PrimaryEndorsmentCurves;

        // ECC curves supported by both TPM and Tpm2Tester software crypto.
        public EccCurve[] SwEccCurves;

        // Implemented block cipher modes
        public TpmAlgId[] SymModes;

        //
        // Supported key sizes
        //
        public ushort[] RsaKeySizes;   // In bits
        public ushort[] AesKeySizes;   // In bits
        public ushort[] DesKeySizes;   // In bits

        public ushort[] PrimaryRsaKeySizes;   // In bits
        public ushort[] PrimaryEndorsRsaKeySizes;   // In bits

        // Symmetric algorithm specifications supported by TPM
        public SymDefObject[] SymDefs;

        // Subset of SymDefs with CFB block mode
        public SymDefObject[] CfbSymDefs;

        // Subset of SymDefs supported by both Tpm2Tester's software crypto
        public SymDefObject[] SwSymDefs;

        // Subset of SwSymDefs with CFB block mode
        public SymDefObject[] SwCfbSymDefs;

        // Enabled hierarchies
        public TpmRh[] Hierarchies;

        //ushort DrtmPcr;
        //ushort CrtmPcr;

        // Resettable PCRs at locality 0
        public byte[] ResettablePcrs = null;

        // extendable PCRs at locality 0
        public byte[] ExtendablePcrs = null;

        //
        // Helpers
        //
        public bool RefactoredTpm()
        {
            return TpmVersion > 129;
        }

        public bool Tpm_138_Errata_1()
        {
            return TpmVersion >= 138 && TpmSpecDate > new DateTime(2017, 03, 01);
        }

        public bool Tpm_138_Errata_2()
        {
            return TpmVersion >= 138 && TpmSpecDate > new DateTime(2017, 04, 16);
        }

        public bool Tpm_115_Errata_12()
        {
            return TpmSpecDate > new DateTime(2015, 1, 14);
        }

        public bool Tpm_115_Errata_13()
        {
            return TpmSpecDate > new DateTime(2015, 06, 15);
        }

        public bool Tpm_115_Errata_14()
        {
            return TpmSpecDate > new DateTime(2016, 01, 14);
        }

        public bool Tpm_115_Errata_15()
        {
            return TpmSpecDate > new DateTime(2016, 09, 20);
        }

        public bool IsImplemented(TpmCc cmd)
        {
            return SupportedCommands.Contains(cmd);
        }

        public bool IsImplemented(TpmAlgId alg)
        {
            return ImplementedAlgs.Contains(alg);
        }

        public bool IsImplemented(EccCurve curve)
        {
            return EccCurves.ContainsKey(curve);
        }

        public bool IsEncryptAttributeSupported()
        {
            return TpmVersion > 119 || Tpm_115_Errata_12();
        }

        // returns PCR values after TPM Reset.
        // NOTE: PCR_Reset() comamnd always resets PCR contents to zeroes.
        // TODO: use TPM PCR properties
        public static TpmHash GetPcrResetValue(TpmAlgId hashAlg, int pcrNum)
        {
            return pcrNum >= 17 && pcrNum <= 22 ? TpmHash.AllOnesHash(hashAlg)
                                                : TpmHash.ZeroHash(hashAlg);
        }

        public bool IsResettablePcr(Tpm2 tpm, int pcr, int locality = 0)
        {
            byte[] resettablePcrs = ResettablePcrs;
            if (locality == 4)
            {
                // In accordance with PTP 3.7.1:
                // "Note that since the hardware that performs the DRTM sequence at
                // Locality 4 is not capable of doing TPM2_PCR_Reset(), the TPM_PT_PCR_RESET_L4
                // attribute is repurposed to indicate the initial state of the PCR(0 or - 1)
                // and to indicate which PCR are set to 0 by a successful DRTM Sequence.
                return false;
            }
            if (locality != 0)
            {
                var props = new PtPcr[] { PtPcr.ResetL1, PtPcr.ResetL2,
                                          PtPcr.ResetL3, PtPcr.ResetL4 };
                resettablePcrs = Tpm2.GetPcrProperty(tpm, props[locality - 1]);
            }
            return Globs.IsBitSet(resettablePcrs, (int)pcr);
        }

        public bool IsExtendablePcr(Tpm2 tpm, int pcr, int locality = 0)
        {
            byte[] extendablePcrs = ExtendablePcrs;
            if (locality != 0)
            {
                var props = new PtPcr[] { PtPcr.ExtendL1, PtPcr.ExtendL2,
                                          PtPcr.ExtendL3, PtPcr.ExtendL4 };
                extendablePcrs = Tpm2.GetPcrProperty(tpm, props[locality - 1]);
            }
            return Globs.IsBitSet(extendablePcrs, (int)pcr);
        }

        public bool CurveSupportsScheme(EccCurve curveId, TpmAlgId scheme)
        {
            if (!EccCurves.ContainsKey(curveId))
                return false;

            var curveParams = EccCurves[curveId];
            return curveParams.signScheme == TpmAlgId.Null ||
                   curveParams.signScheme == scheme;
        }

        public IEnumerable<EccCurve> CurvesForScheme(TpmAlgId scheme, bool swCompat = false)
        {
            var curves = !swCompat
                       ? EccCurves
                       : EccCurves.Where(curveEntry => SwEccCurves.Contains(curveEntry.Key));
            if (scheme != TpmAlgId.Null)
            {
                curves = curves.Where(curveEntry =>
                                      curveEntry.Value.signScheme == TpmAlgId.Null ||
                                      curveEntry.Value.signScheme == scheme);
            }
            return curves.Select(curveEntry => curveEntry.Key);
        }

        public PcrSelection PcrBank(TpmAlgId hashAlg)
        {
            return PcrBank(PcrBanks.ToArray(), hashAlg);
        }

        public PcrSelection PcrBank(PcrSelection[] pcrBanks, TpmAlgId hashAlg)
        {
            // The hash algorithm is not supported by Tpm2Tester
            if (!CryptoLib.IsSupported(hashAlg))
                return null;

            foreach (var pb in pcrBanks)
            {
                if (pb.hash == hashAlg)
                    return pb;
            }
            Globs.Throw("No PCR bank for hash algorithm " + hashAlg);
            return null;
        }

    } // class TpmConfig
}

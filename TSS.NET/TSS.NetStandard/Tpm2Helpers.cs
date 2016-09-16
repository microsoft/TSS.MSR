/*++

Copyright (c) 2010-2015 Microsoft Corporation
Microsoft Confidential

*/
using System;
using System.Linq;
using System.Threading.Tasks;
using System.Diagnostics;

namespace Tpm2Lib
{
    /// <summary>
    /// TpmHelpers is a set of routines accessible like
    ///     tpm.Helpers.Primaries.CreateRsaPrimary(...)
    /// that perform common operations.  Programming the TPM is simplified because the libraries 
    /// string together command sequences that are needed to get a job done, or because we set up 
    /// complex data structures with default (and commonly desired) settings.
    /// 
    /// </summary>
    public class TpmHelpers
    {
        // These are the areas that we provide helper-classes
        public PrimaryHelpers Primaries;
        public KeyHelpers Keys;

        // These are the default settings for TPM operations
        public TpmAlgId NameHash = TpmAlgId.Sha256;
        public IAsymSchemeUnion RsaSigScheme = new SchemeRsassa(TpmAlgId.Sha1);

        internal Tpm2 Tpm;

        /// <summary>
        /// Caches command codes implemented by this TPM instance.
        /// </summary>
        private TpmCc[] ImplementedCommands;

        /// <summary>
        /// Caches algorithm IDs implemented by this TPM instance.
        /// </summary>
        private TpmAlgId[] ImplementedAlgs;


        internal TpmHelpers(Tpm2 associatedTpm)
        {
            Tpm = associatedTpm;
            Primaries = new PrimaryHelpers(this);
            Keys = new KeyHelpers(this);
        }

        /// <summary>
        /// Check if this TPM implements the given command.
        /// The method sends the GetCapability command the first time it is called,
        /// and reuses its results during subsequent invocations.
        /// </summary>
        /// <param name="commandCode">The command code to check.</param>
        /// <returns>true if the given command is supported by this TPM instance.</returns>
        public bool IsImplemented(TpmCc commandCode)
        {
            if (ImplementedCommands == null || ImplementedCommands.Length == 0)
            {
                ICapabilitiesUnion caps;
                uint totalCommands = Tpm2.GetProperty(Tpm, Pt.TotalCommands);
                Tpm.GetCapability(Cap.Commands, (uint)TpmCc.First, totalCommands, out caps);
                ImplementedCommands = Globs.ConvertAll((caps as CcaArray).commandAttributes,
                                                       cmdAttr => (TpmCc)(cmdAttr & CcAttr.commandIndexBitMask))
                                           .ToArray();
                Debug.Assert(ImplementedCommands.Length != 0);
            }
            return ImplementedCommands.Contains(commandCode);
        }

        /// <summary>
        /// Check if this TPM implements the given algorithm.
        /// The method sends the GetCapability command the first time it is called,
        /// and reuses its results during subsequent invocations.
        /// </summary>
        /// <param name="commandCode">Algorithm ID to check.</param>
        /// <returns>true if the given algorithm is supported by this TPM instance.</returns>
        public bool IsImplemented(TpmAlgId algId)
        {
            if (ImplementedAlgs == null || ImplementedAlgs.Length == 0)
            {
                ICapabilitiesUnion caps;
                Tpm.GetCapability(Cap.Algs, (uint)TpmAlgId.First, (uint)TpmAlgId.Last, out caps);
                ImplementedAlgs = Globs.ConvertAll((caps as AlgPropertyArray).algProperties,
                                                   algProp => algProp.alg)
                                       .ToArray();
                Debug.Assert(ImplementedAlgs.Length != 0);
            }
            return ImplementedAlgs.Contains(algId);
        }

        /// <summary>
        /// Returns the value of an enumerator that was renamed in one of the TPM 2.0 spec revisions.
        /// </summary>
        public static E GetEnumerator<E>(string oldName, string newName) where E : struct
        {
            E val;
            if (Enum.TryParse<E>(newName, out val) || Enum.TryParse<E>(oldName, out val))
            {
                return val;
            }
            throw new Exception("Invalid enumerator names " + oldName + ", " + newName + " for enum " + typeof(E));
        }
    }

    public class TpmErrorHelpers
    {
        /// <summary>
        /// Checks if the given response code uses Format-One.
        /// </summary>
        public static bool IsFmt1 (TpmRc responseCode)
        {
            return ((uint)responseCode & 0x80) != 0;
        }

        /// <summary>
        /// Returns error number, i.e. what is left after masking out auxiliary data
        /// (such as format selector, version, and bad parameter index) from the
        /// response code returned by TPM.
        /// </summary>
        public static TpmRc ErrorNumber (TpmRc rawResponse)
        {
            const uint Fmt1 = (uint)TpmRc.RcFmt1;   // Format 1 code (TPM 2 only)
            const uint Ver1 = (uint)TpmRc.RcVer1;   // TPM 1 code (format 0 only)
            const uint Warn = (uint)TpmRc.RcWarn;   // Code is a warning (format 0 only)
            uint mask = IsFmt1(rawResponse) ? Fmt1 | 0x3F : Warn | Ver1 | 0x7F;
            return (TpmRc)((uint)rawResponse & mask);
        }
    }

    public class PrimaryHelpers
    {
        private readonly TpmHelpers H;

        internal PrimaryHelpers(TpmHelpers parentH)
        {
            H = parentH;
        }

        /// <summary>
        /// Create an RSA primary with the specified use-auth value and key size.
        /// </summary>
        /// <param name="keyLen"></param>
        /// <param name="useAuth"></param>
        /// <returns></returns>
        public async Task<Tpm2CreatePrimaryResponse> CreatePrimaryRsaAsync(int keyLen, AuthValue useAuth)
        {
            return await CreatePrimaryRsaAsyncInternal(keyLen, useAuth.AuthVal, null, null);
        }

        public async Task<Tpm2CreatePrimaryResponse> CreatePrimaryRsaAsync(int keyLen, AuthValue useAuth, TpmHash adminPolicy)
        {
            return await CreatePrimaryRsaAsyncInternal(keyLen, useAuth.AuthVal, adminPolicy, null);
        }

        public async Task<Tpm2CreatePrimaryResponse> CreatePrimaryRsaAsync(int keyLen, byte[] useAuth)
        {
            return await CreatePrimaryRsaAsyncInternal(keyLen, useAuth, null, null);
        }

        internal async Task<Tpm2CreatePrimaryResponse> CreatePrimaryRsaAsyncInternal(
            int keyLen,
            byte[] useAuth,
            byte[] policyAuth,
            PcrSelection[] pcrSel)
        {
            ObjectAttr attr = ObjectAttr.Restricted | ObjectAttr.Decrypt | ObjectAttr.FixedParent | ObjectAttr.FixedTPM |
                              ObjectAttr.SensitiveDataOrigin;

            var theUseAuth = new byte[0];
            if (useAuth != null)
            {
                theUseAuth = useAuth;
                attr |= ObjectAttr.UserWithAuth;
            }
            var thePolicyAuth = new byte[0];
            if (policyAuth != null)
            {
                thePolicyAuth = policyAuth;
                attr |= ObjectAttr.AdminWithPolicy;
            }
            var theSelection = new PcrSelection[0];
            if (pcrSel != null)
            {
                theSelection = pcrSel;
            }

            var sensCreate = new SensitiveCreate(theUseAuth, new byte[0]);
            var parms = new TpmPublic(H.NameHash,
                                      attr,
                                      thePolicyAuth,
                                      new RsaParms(new SymDefObject(TpmAlgId.Aes, 128, TpmAlgId.Cfb),
                                                   new NullAsymScheme(),
                                                   (ushort)keyLen,
                                                   0),
                                      new Tpm2bPublicKeyRsa());

            byte[] outsideInfo = Globs.GetRandomBytes(8);
            var newPrimary = await H.Tpm.CreatePrimaryAsync(TpmRh.Owner, sensCreate, parms, outsideInfo, theSelection);
            return newPrimary;
        }
    }

    public class KeyHelpers
    {
        private readonly TpmHelpers H;

        internal KeyHelpers(TpmHelpers parentH)
        {
            H = parentH;
        }

        /// <summary>
        /// Create a non-migratable RSA primary with the specified use-auth value and key size.
        /// </summary>
        /// <param name="parentAuth"></param>
        /// <param name="keyLen"></param>
        /// <param name="restricted"></param>
        /// <param name="useAuth"></param>
        /// <param name="parentHandle"></param>
        /// <param name="policy"></param>
        /// <returns></returns>
        public async Task<Tpm2CreateResponse> CreateRsaSigningAsync(
            TpmHandle parentHandle,
            AuthValue parentAuth,
            int keyLen,
            bool restricted,
            AuthValue useAuth,
            TpmHash policy = null)
        {
            ObjectAttr attr = ObjectAttr.Sign | ObjectAttr.FixedParent | ObjectAttr.FixedTPM | // Non-duplicatable
                              ObjectAttr.SensitiveDataOrigin | ObjectAttr.UserWithAuth; // Authorize with auth-data

            if (restricted)
            {
                attr |= ObjectAttr.Restricted;
            }

            var thePolicy = new byte[0];
            if ((Object)policy != null)
            {
                thePolicy = policy;
                attr |= ObjectAttr.AdminWithPolicy;
            }

            var signKeyPubTemplate = new TpmPublic(H.NameHash,
                                                   attr,
                                                   thePolicy,
                                                   new RsaParms(new SymDefObject(),
                                                                // Key type and sig scheme
                                                                H.RsaSigScheme,
                                                                (ushort)keyLen,
                                                                0),
                                                   new Tpm2bPublicKeyRsa());

            // Auth-data for new key
            var sensCreate = new SensitiveCreate(useAuth, new byte[0]);

            // Create the key
            var newKey = await H.Tpm[parentAuth].CreateAsync(parentHandle,
                                                             sensCreate,
                                                             signKeyPubTemplate,
                                                             new byte[0],
                                                             new PcrSelection[0]);
            return newKey;
        }
    }
}

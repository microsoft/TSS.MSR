/*++

Copyright (c) 2010-2015 Microsoft Corporation
Microsoft Confidential

*/
using System;
using System.Threading.Tasks;

namespace Tpm2Lib
{
    public partial class Tpm2
    {
        /// <summary>
        /// Create a simple unbound & unseeded session.
        /// </summary>
        public AuthSession StartAuthSessionEx(
            TpmSe sessionType,
            TpmAlgId authHash,
            int nonceCallerSize = 0)
        {
            return StartAuthSessionEx(sessionType, authHash,
                                      SessionAttr.ContinueSession, nonceCallerSize);
        }

        /// <summary>
        /// Create a simple unbound & unseeded session.
        /// </summary>
        public AuthSession StartAuthSessionEx(
            TpmSe sessionType,
            TpmAlgId authHash,
            SessionAttr initialialAttrs,
            int nonceCallerSize = 0)
        {
            return StartAuthSessionEx(sessionType, authHash, 
                                      initialialAttrs, new SymDef(), nonceCallerSize);
        }

        /// <summary>
        /// Create a simple unbound & unseeded session supporting session encryption.
        /// </summary>
        public AuthSession StartAuthSessionEx(
            TpmSe sessionType,
            TpmAlgId authHash,
            SessionAttr initialialAttrs,
            SymDef symDef,
            int nonceCallerSize = 0)
        {
            return StartAuthSessionEx(TpmRh.Null, sessionType, authHash, 
                                      initialialAttrs, symDef, nonceCallerSize);
        }

        /// <summary>
        /// Create a simple bound but unseeded session.
        /// </summary>
        public AuthSession StartAuthSessionEx(
            TpmHandle boundEntity,
            TpmSe sessionType,
            TpmAlgId authHash,
            SessionAttr initialialAttrs = SessionAttr.ContinueSession,
            SymDef symDef = null,
            int nonceCallerSize = 0)
        {
            byte[] nonceTpm;
            var EmptySalt = new byte[0];

            if (nonceCallerSize == 0)
            {
                nonceCallerSize = CryptoLib.DigestSize(authHash);
            }

            AuthSession sess = StartAuthSession(TpmRh.Null, boundEntity,
                                                GetRandomBytes(nonceCallerSize),
                                                EmptySalt, sessionType,
                                                symDef ?? new SymDef(),
                                                authHash, out nonceTpm)
                               + initialialAttrs;

            _InitializeSession(sess);
            return sess;
        }

        public uint[] GetFirmwareVersionEx()
        {
            ICapabilitiesUnion caps;

            // Get build string and Revision number
            GetCapability(Cap.TpmProperties, (uint)Pt.Revision, 256, out caps);

            var props = (TaggedTpmPropertyArray)caps;

            TaggedProperty[] arr = props.tpmProperty;
            uint fwV1 = 0, fwV2 = 0;
            uint revision = 0;
            foreach (TaggedProperty p in arr)
            {
                if (p.property == Pt.FirmwareVersion1)
                {
                    fwV1 = p.value;
                }
                if (p.property == Pt.FirmwareVersion2)
                {
                    fwV2 = p.value;
                }
                if (p.property == Pt.Revision)
                {
                    revision = p.value;
                }
            }

            var ret = new[] {fwV1, fwV2, revision};
            return ret;
        }

        /// <summary>
        /// Get the date of the specification from which the TPM was built.
        /// </summary>
        /// <param name="manufacturer"></param>
        /// <param name="year"></param>
        /// <param name="dayOfYear"></param>
        /// <param name="tpm"></param>
        public static void GetTpmInfo(Tpm2 tpm, out string manufacturer, out uint year, out uint dayOfYear)
        {
            // ReSharper disable once RedundantAssignment
            manufacturer = "";
            year = GetProperty(tpm, Pt.Year);
            dayOfYear = GetProperty(tpm, Pt.DayOfYear);

            uint manX = GetProperty(tpm, Pt.Manufacturer);
            var arr = Marshaller.GetTpmRepresentation(manX);
            manufacturer = (new System.Text.UTF8Encoding()).GetString(arr, 0, arr.Length);
        }

        public static uint GetProperty(Tpm2 tpm, Pt prop)
        {
            ICapabilitiesUnion caps;
            tpm.GetCapability(Cap.TpmProperties, (uint)prop, 1, out caps);
            var props = (TaggedTpmPropertyArray)caps;
            TaggedProperty[] arr = props.tpmProperty;
            if (arr.Length != 1)
            {
                Globs.Throw("Unexpected return from GetCapability");
                if (arr.Length == 0)
                    return 0;
            }

            uint val = arr[0].value;
            return val;
        }

        public static byte[] GetPcrProperty(Tpm2 tpm, PtPcr prop)
        {
            ICapabilitiesUnion caps;
            tpm.GetCapability(Cap.PcrProperties, (uint)prop, 1, out caps);
            TaggedPcrSelect[] props = (caps as TaggedPcrPropertyArray).pcrProperty;
            if (props.Length == 0)
            {
                return null;
            }
            if (props.Length != 1)
            {
                Globs.Throw("Unexpected return from GetCapability");
            }
            return props[0].pcrSelect;
        }

        [TpmCommand]
        public async Task<Tpm2CreatePrimaryResponse> CreatePrimaryAsync(
            TpmHandle primaryHandle,
            SensitiveCreate inSensitive,
            TpmPublic inPublic,
            byte[] outsideInfo,
            PcrSelection[] creationPCR)
        {
            var inS = new Tpm2CreatePrimaryRequest {
                primaryHandle = primaryHandle,
                inSensitive = inSensitive,
                inPublic = inPublic,
                outsideInfo = outsideInfo,
                creationPCR = creationPCR
            };
            TpmStructureBase outSBase = null;
            await Task.Run(() => 
                DispatchMethod(TpmCc.CreatePrimary, inS, typeof (Tpm2CreatePrimaryResponse), out outSBase, 1, 1));
            var outS = (Tpm2CreatePrimaryResponse)outSBase;
            return outS;
        }

        /// <summary>
        /// This command causes the TPM to sign an externally provided hash with the specified asymmetric signing key.
        /// 
        /// </summary>
        /// <param name = "keyHandle">Handle of key that will perform signing Auth Index: 1 Auth Role: USER</param>
        /// <param name = "digest">Digest to be signed</param>
        /// <param name = "inScheme">Signing scheme to use.</param>
        /// <param name = "validation">Proof that digest was created by the TPM.</param>
        [TpmCommand]
        public async Task<ISignatureUnion> SignAsync(
            TpmHandle keyHandle,
            byte[] digest,
            ISigSchemeUnion inScheme,
            TkHashcheck validation)
        {
            var inS = new Tpm2SignRequest {
                keyHandle = keyHandle,
                digest = digest,
                inScheme = inScheme,
                validation = validation
            };
            TpmStructureBase outSBase = null;
            await Task.Run(() => DispatchMethod(TpmCc.Sign, inS, typeof (Tpm2SignResponse), out outSBase, 1, 0));
            var outS = (Tpm2SignResponse)outSBase;
            return outS.signature;
        }

        /// <summary>
        /// This command is used to create an object that can be loaded into a TPM using TPM2_Load(). 
        /// If the command completes successfully, the TPM will create the new object and return the object's
        /// creation data (creationData), its public area (outPublic), and its encrypted sensitive area (outPrivate).
        /// Preservation of the returned data is the responsibility of the caller. The object will need to be loaded 
        /// (TPM2_Load()) before it may be used.
        ///  
        /// </summary>
        /// <param name = "parentHandle">Handle of parent for new object.</param>
        /// <param name = "inSensitive">The sensitive data</param>
        /// <param name = "inPublic">The public template</param>
        /// <param name = "outsideInfo">Data that will be included in the creation data for this object</param>
        /// <param name = "creationPCR">PCR that will be used in creation data</param>
        [TpmCommand]
        public async Task<Tpm2CreateResponse> CreateAsync(
            TpmHandle parentHandle,
            SensitiveCreate inSensitive,
            TpmPublic inPublic,
            byte[] outsideInfo,
            PcrSelection[] creationPCR)
        {
            var inS = new Tpm2CreateRequest {
                parentHandle = parentHandle,
                inSensitive = inSensitive,
                inPublic = inPublic,
                outsideInfo = outsideInfo,
                creationPCR = creationPCR
            };
            TpmStructureBase outSBase = null;
            await Task.Run(() => DispatchMethod(TpmCc.Create, inS, typeof (Tpm2CreateResponse), out outSBase, 1, 0));
            var outS = (Tpm2CreateResponse)outSBase;
            return outS;
        }
    }
}
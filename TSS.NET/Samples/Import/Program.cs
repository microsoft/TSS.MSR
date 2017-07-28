/*
 * Copyright (c) 2013  Microsoft Corporation
 */

using System;
using System.Collections.Generic;
using Tpm2Lib;

namespace GetRandom
{
    /// <summary>
    /// Main class containing the logic of this sample.
    /// </summary>
    class Program
    {
        /// <summary>
        /// Defines the argument to use to have this program use a TCP connection
        /// to communicate with a TPM 2.0 simulator.
        /// </summary>
        private const string DeviceSimulator = "-tcp";
        /// <summary>
        /// Defines the argument to use to have this program use the Windows TBS
        /// API to communicate with a TPM 2.0 device.
        /// </summary>
        private const string DeviceWinTbs = "-tbs";
        /// <summary>
        /// The default number of random bytes to query from the TPM.
        /// </summary>
        private const ushort DefaultNumberOfBytes = 20;
        /// <summary>
        /// The default connection to use for communication with the TPM.
        /// </summary>
        private const string DefaultDevice = DeviceSimulator;
        /// <summary>
        /// If using a TCP connection, the default DNS name/IP address for the
        /// simulator.
        /// </summary>
        private const string DefaultSimulatorName = "127.0.0.1";
        /// <summary>
        /// If using a TCP connection, the default TCP port of the simualtor.
        /// </summary>
        private const int DefaultSimulatorPort = 2321;

        /// <summary>
        /// Prints instructions for usage of this program.
        /// </summary>
        static void WriteUsage()
        {
            Console.WriteLine();
            Console.WriteLine("Usage: GetRandom [<device>] [<number of bytes>]");
            Console.WriteLine();
            Console.WriteLine("    <device> can be '{0}' or '{1}'. Defaults to '{2}'.", DeviceWinTbs, DeviceSimulator, DefaultDevice);
            Console.WriteLine("        If <device> is '{0}', the program will connect to a simulator\n" +
                              "        listening on a TCP port.", DeviceSimulator);
            Console.WriteLine("        If <device> is '{0}', the program will use the TBS interface to talk\n" +
                              "        to the TPM device.", DeviceWinTbs);
            Console.WriteLine();
            Console.WriteLine("    <number of bytes> defaults to {0}.", DefaultNumberOfBytes);
            Console.WriteLine("        The maximum number of bytes is defined by the size of the largest\n" +
                              "        digest that can be produced by the TPM.");
            Console.WriteLine("        For instance: SHA1 produces a 20 byte digest.");
            Console.WriteLine("        SHA256 produces a 32 byte digest.");
        }

        /// <summary>
        /// Parse the arguments of the program and return the selected values.
        /// </summary>
        /// <param name="args">The arguments of the program.</param>
        /// <param name="tpmDeviceName">The name of the selected TPM connection created.</param>
        /// <param name="bytesRequested">The number of random bytes to request from the TPM.</param>
        /// <returns>True if the arguments could be parsed. False if an unknown argument or malformed
        /// argument was present.</returns>
        static bool ParseArguments(IEnumerable<string> args, out string tpmDeviceName, out ushort bytesRequested)
        {
            bytesRequested = DefaultNumberOfBytes;
            tpmDeviceName = DefaultDevice;
            foreach (string arg in args)
            {
                ushort bytesArg;
                if (string.Compare(arg, DeviceSimulator, true) == 0)
                {
                    tpmDeviceName = DeviceSimulator;
                }
                else if (string.Compare(arg, DeviceWinTbs, true) == 0)
                {
                    tpmDeviceName = DeviceWinTbs;
                }
                else if (UInt16.TryParse(arg, out bytesArg))
                {
                    bytesRequested = bytesArg;
                }
                else
                {
                    return false;
                }
            }
            return true;
        }

        /// <summary>
        /// Program entry point
        /// </summary>
        /// <param name="args">Arguments to this program.</param>
        static void Main(string[] args)
        {
            //
            // Parse the program arguments. If the wrong arguments are given or
            // are malformed, then instructions for usage are displayed and 
            // the program terminates.
            // 
            string tpmDeviceName;
            ushort bytesRequested;
            if (!ParseArguments(args, out tpmDeviceName, out bytesRequested))
            {
                WriteUsage();
                return;
            }

            try
            {
                //
                // Create the device according to the selected connection.
                // 
                Tpm2Device tpmDevice;
                switch (tpmDeviceName)
                {
                    case DeviceSimulator:
                        tpmDevice = new TcpTpmDevice(DefaultSimulatorName, DefaultSimulatorPort);
                        break;

                    case DeviceWinTbs:
                        tpmDevice = new TbsDevice();
                        break;

                    default:
                        throw new Exception("Unknown device selected.");
                }

                //
                // Connect to the TPM device. This function actually establishes the
                // connection.
                // 
                tpmDevice.Connect();

                //
                // Pass the device object used for communication to the TPM 2.0 object
                // which provides the command interface.
                // 
                var tpm = new Tpm2(tpmDevice);
                if (tpmDevice is TcpTpmDevice)
                {
                    //
                    // If we are using the simulator, we have to do a few things the
                    // firmware would usually do. These actions have to occur after
                    // the connection has been established.
                    // 
                    tpmDevice.PowerCycle();
                    tpm.Startup(Su.Clear);
                }

                // Generate, import and use keys 
                ImportSample(tpm);

                //
                // Clean up.
                // 
                tpm.Dispose();
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception occurred: {0}", e.Message);
            }

            Console.WriteLine("Press Any Key to continue.");
            Console.ReadLine();
        }

        /// <summary>
        /// Creates a primary RSA storage key in the storage hierarchy and returns its
        /// handle. The caller can provide an auth value and additional entropy for
        /// the key derivation (primary keys are deterministically derived by the TPM
        /// from an internal primary seed value unique for each hierarchy).
        /// The caller is responsible for disposing of the returned key handle.
        /// </summary>
        /// <param name="auth">Optional auth value to be associated with the created key.</param>
        /// <param name="seed">Optional entropy that may be used to create different primary kyes with exactly the same template.</param>
        /// <returns></returns>
        static TpmHandle CreateRsaPrimaryStorageKey(Tpm2 tpm,
                                                    byte[] auth = null, byte[] seed = null)
        {
            TpmPublic newKeyPub;
            return CreateRsaPrimaryStorageKey(tpm, out newKeyPub, seed, auth);
        }

        /// <summary>
        /// Creates a primary RSA storage key in the storage hierarchy and returns its
        /// handle and public area. The caller can provide an auth value and additional
        /// entropy for the key derivation (primary keys are deterministically derived
        /// by the TPM from an internal primary seed value unique for each hierarchy).
        /// The caller is responsible for disposing of the returned key handle.
        /// </summary>
        /// <param name="newKeyPub">Public area of the the created key. Its 'unique' member contains the actual public key of the generated key pair.</param>
        /// <param name="auth">Optional auth value to be associated with the created key.</param>
        /// <param name="seed">Optional entropy that may be used to create different primary kyes with exactly the same template.</param>
        /// <returns></returns>
        static TpmHandle CreateRsaPrimaryStorageKey(Tpm2 tpm, out TpmPublic newKeyPub,
                                                    byte[] auth = null, byte[] seed = null)
        {
            //
            // Creation parameters (no external data for TPM-created objects)
            // 
            var sensCreate = new SensitiveCreate(auth,      // Auth-data provided by the caller
                                                 null);     // No private key bits for asymmetric keys

            // Typical storage key template
            var parms = new TpmPublic(TpmAlgId.Sha256,                                  // Name algorithm
                                      ObjectAttr.Restricted   | ObjectAttr.Decrypt  |   // Storage key
                                      ObjectAttr.FixedParent  | ObjectAttr.FixedTPM |   // Non-duplicable
                                      ObjectAttr.UserWithAuth | ObjectAttr.SensitiveDataOrigin,
                                      null,                                             // No policy
                                      // No signing or decryption scheme, and non-empty symmetric
                                      // specification (even when it is an asymmetric key)
                                      new RsaParms(new SymDefObject(TpmAlgId.Aes, 128, TpmAlgId.Cfb),
                                                   null, 2048, 0),
                                      new Tpm2bPublicKeyRsa(seed)     // Additional entropy for key derivation
                                    );

            //
            // The following are returned by the TPM in CreatePrimary (and Create)
            // they are not used in this sample.
            // 
            CreationData creationData;
            TkCreation creationTicket;
            byte[] creationHash;

            return  tpm.CreatePrimary(TpmRh.Owner,          // In storage hierarchy
                                      sensCreate,           // Auth value
                                      parms,                // Key template
                                      //
                                      // The following parameters influence the creation of the 
                                      // creation-ticket. They are not used in this sample
                                      //
                                      null,                 // Null outsideInfo
                                      new PcrSelection[0],  // Not PCR-bound
                                      out newKeyPub,        // Our outs
                                      out creationData, out creationHash, out creationTicket);
        } // CreateRsaPrimaryStorageKey()

        /// <summary>
        /// Main logic of the Import sample.
        /// The sample demonstrates how to create in software (using TSS.net helpers)
        /// keys of different kinds, then import them into TPM and make sure they work. 
        /// </summary>
        static void ImportSample(Tpm2 tpm)
        {
            // Templates of the keys to be generated and imported
            var inPubs = new TpmPublic[] {
                new TpmPublic(TpmAlgId.Sha256,
                            ObjectAttr.Decrypt | ObjectAttr.UserWithAuth,
                            null,
                            new SymDefObject(TpmAlgId.Aes, 128, TpmAlgId.Cfb),
                            new Tpm2bDigestSymcipher()),
                    new TpmPublic(TpmAlgId.Sha256,
                            ObjectAttr.Sign | ObjectAttr.UserWithAuth,
                            null,
                            new RsaParms(new SymDefObject(),
                                         new SchemeRsassa(TpmAlgId.Sha256),
                                         2048, 0),
                            new Tpm2bPublicKeyRsa()),
                    new TpmPublic(TpmAlgId.Sha256,
                            ObjectAttr.Sign | ObjectAttr.UserWithAuth,
                            null,
                            new KeyedhashParms(new SchemeHmac(TpmAlgId.Sha256)),
                            new Tpm2bDigestKeyedhash())
            };

            // Create a TPM based key that will serve as a parent for the imported keys.
            // This should be a storage key, i.e. it must have Restricted and Decrypt
            // attributes, no signing or decryption scheme, and non-empty symmetric
            // specification (even if it is an asymmetric key) as part of its parameters.
            // NOTE 1 - We use an empty auth value for the parent key. If you want one,
            // uncomment the optional second parameter.
            // NOTE 2 - The size of the auth value shall not exceed the size of the digest
            // produced by the name algorithm of the key. In this case name algorithm is
            // hardcoded inside the helper, so that we do not have control over it at
            // this point, and therefore we use the maximal number that is safe for any
            // modern hash algorithm (SHA-1 or larger), though for the given implementation
            // of CreateRsaPrimaryStorageKey() we could use 32 (for SHA-256).
            TpmHandle hPrim = CreateRsaPrimaryStorageKey(tpm/*, AuthValue.FromRandom(20)*/);

            foreach (var inPub in inPubs)
            {
                GenerateAndImport(tpm, inPub, hPrim);

                // Now do the same using an inner wrapper (additional layer of cryptographic
                // protection in the form of symmetric encryption for the duplication blob).
                GenerateAndImport(tpm, inPub, hPrim,
                                  new SymDefObject(TpmAlgId.Aes, 128, TpmAlgId.Cfb));
            }

            tpm.FlushContext(hPrim);
        } // ImportSample()

        static IAsymSchemeUnion GetScheme(TpmPublic pub)
        {
            return (pub.type == TpmAlgId.Keyedhash
                        ? (IAsymSchemeUnion)(pub.parameters as KeyedhashParms).scheme
                        : pub.type == TpmAlgId.Rsa
                                ? (IAsymSchemeUnion)(pub.parameters as RsaParms).scheme
                                : (IAsymSchemeUnion)(pub.parameters as EccParms).scheme);
        }

        static TpmAlgId GetSchemeHash(IAsymSchemeUnion scheme)
        {
            if (scheme == null || scheme is NullUnion || scheme is Empty)
                return TpmAlgId.Null;
            var daaScheme = scheme as SchemeEcdaa;
            return daaScheme != null ? daaScheme.hashAlg
                                     : (scheme as SchemeHash).hashAlg;
        }

        static TpmAlgId GetSchemeHash(TpmPublic pub)
        {
            TpmAlgId schemeAlg = GetSchemeHash(GetScheme(pub));
            return schemeAlg == TpmAlgId.Null ? pub.nameAlg : schemeAlg;
        }

        /// <summary>
        /// Performs the following operations:
        /// - Generates in software (using TSS.net helpers) a key with the given template,
        /// - Creates TPM-compatible dupliction blob for the given TPM based parent key,
        /// - Import the duplication blob into TPM
        /// - Loads the imported key into the TPM
        /// - Makes sure that the imported key works.
        /// </summary>
        /// <param name="tpm">TPM instance to use</param>
        /// <param name="keyPub">Template for the software generated key.</param>
        /// <param name="hParent">Intended TPM based parent key for the software generated key.</param>
        /// <param name="innerSymDef">Specification of the optional inner wrapper for the duplication blob.</param>
        static void GenerateAndImport(Tpm2 tpm, TpmPublic keyPub, TpmHandle hParent,
                                      SymDefObject innerSymDef = null)
        {
            //
            // Create a software key with the given template
            //

            // Generate a random auth value for the key to be created (though we could
            // use an empty buffer, too).
            var keyAuth = AuthValue.FromRandom(CryptoLib.DigestSize(keyPub.nameAlg));

            // Generate the key
            TssObject swKey = TssObject.Create(keyPub, keyAuth);

            //
            // Create duplication blob for the new key with the SRK as the new parent
            //

            // Create a symmetric software key if an inner wrapper is requested.
            var innerWrapKey = innerSymDef == null ? null : SymCipher.Create(innerSymDef);

            // Retrieve the public area of the intended parent key from the TPM 
            // We do not need the name (and qualified name) of the key here, but
            // the TPM command returns them anyway.
            // NOTE - Alternatively we could get the public area from the overloaded
            // form of the CreateRsaPrimaryStorageKey() helper used to create the parent
            // key, as all TPM key creation commands (TPM2_CreatePrimary(), TPM2_Create()
            // and TPM2_CreateLoaded()) return it.
            byte[] name, qname;
            TpmPublic pubParent = tpm.ReadPublic(hParent, out name, out qname);

            byte[] encSecret;
            TpmPrivate dupBlob = swKey.GetDuplicationBlob(pubParent, innerWrapKey, out encSecret);

            // Import the duplication blob into the TPM
            TpmPrivate privImp = tpm.Import(hParent, innerWrapKey, swKey.Public, dupBlob,
                                            encSecret, innerSymDef ?? new SymDefObject());

            // Load the imported key ...
            TpmHandle hKey = tpm.Load(hParent, privImp, swKey.Public)
                                .SetAuth(swKey.Sensitive.authValue);

            // ... and validate that it works
            byte[] message = Globs.GetRandomBytes(32);

            if (keyPub.objectAttributes.HasFlag(ObjectAttr.Decrypt))
            {
                // Encrypt something
                if (keyPub.type == TpmAlgId.Symcipher)
                {
                    // Only need software symcypher here to query IV size.
                    // Normally, when you use a fixed algorithm, you can hardcode it.
                    var swSym = SymCipher.Create(keyPub.parameters as SymDefObject);
                    byte[] ivIn = Globs.GetRandomBytes(swSym.IVSize),
                           ivOut = null;
                    byte[] cipher = swKey.Encrypt(message, ref ivIn, out ivOut);

                    // Not all TPMs implement TPM2_EncryptDecrypt() command
                    tpm._ExpectResponses(TpmRc.Success, TpmRc.TbsCommandBlocked);
                    byte[] decrypted = tpm.EncryptDecrypt(hKey, 1, TpmAlgId.Null, ivIn,
                                                    cipher, out ivOut);
                    if (tpm._LastCommandSucceeded())
                    {
                        bool decOk = Globs.ArraysAreEqual(message, decrypted);
                        Console.WriteLine("Imported symmetric key validation {0}",
                                          decOk ? "SUCCEEDED" : "FAILED");
                    }
                }
            }
            else
            {
                // Sign something (works for both asymmetric and MAC keys)
                string keyType = keyPub.type == TpmAlgId.Rsa ? "RSA"
                               : keyPub.type == TpmAlgId.Keyedhash ? "HMAC"
                               : "UNKNOWN"; // Should not happen in this sample
                TpmAlgId sigHashAlg = GetSchemeHash(keyPub);
                TpmHash toSign = TpmHash.FromData(sigHashAlg, message);
                var proofx = new TkHashcheck(TpmRh.Null, null);
                ISignatureUnion sig = tpm.Sign(hKey, toSign, null, proofx);
                bool sigOk =  swKey.VerifySignatureOverHash(toSign, sig);
                Console.WriteLine("Imported {0} key validation {1}", keyType,
                                  sigOk ? "SUCCEEDED" : "FAILED");
            }

            // Free TPM resources taken by the loaded imported key
            tpm.FlushContext(hKey);
        } // GenerateAndImport

    }
}

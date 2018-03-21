/*
 * Copyright (c) 2013  Microsoft Corporation
 */

using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using Tpm2Lib;

namespace PCRandKeys
{
    /// <summary>
    /// Main class to contain the program of this sample.
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
        /// Defines the argument to use to have this program use the Linux /dev/tpm0
        /// device file to communicate with a TPM 2.0 device.
        /// </summary>
        private const string DeviceLinux = "-tpm0";
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
        /// If using a TCP connection, the default TCP port of the simulator.
        /// </summary>
        private const int DefaultSimulatorPort = 2321;

        /// <summary>
        /// Prints instructions for usage of this program.
        /// </summary>
        static void WriteUsage()
        {
            Console.WriteLine();
            Console.WriteLine("Usage: PCRandKeys [<device>]");
            Console.WriteLine();
            Console.WriteLine($"    <device> can be '{DeviceWinTbs}' or '{DeviceLinux}' or '{DeviceSimulator}'. Defaults to '{DefaultDevice}'.");
            Console.WriteLine($"        If <device> is '{DeviceSimulator}', the program will connect to a simulator\n" +
                              $"        listening on a TCP port.");
            Console.WriteLine($"        If <device> is '{DeviceWinTbs}', the program will use the TBS interface to talk\n" +
                              $"        to the TPM device.");
            Console.WriteLine($"        If <device> is '{DeviceLinux}', the program will use the /dev/tpm0 interface to talk\n" +
                              $"        to the TPM device.");
        }

        /// <summary>
        /// Parse the arguments of the program and return the selected values.
        /// </summary>
        /// <param name="args">The arguments of the program.</param>
        /// <param name="tpmDeviceName">The name of the selected TPM connection created.</param>
        /// <returns>True if the arguments could be parsed. False if an unknown argument or malformed
        /// argument was present.</returns>
        static bool ParseArguments(IEnumerable<string> args, out string tpmDeviceName)
        {
            tpmDeviceName = DefaultDevice;
            foreach (string arg in args)
            {
                if (string.Compare(arg, DeviceSimulator, true) == 0)
                {
                    tpmDeviceName = DeviceSimulator;
                }
                else if (string.Compare(arg, DeviceWinTbs, true) == 0)
                {
                    tpmDeviceName = DeviceWinTbs;
                }
                else if (string.Compare(arg, DeviceLinux, true) == 0)
                {
                    tpmDeviceName = DeviceLinux;
                }
                else
                {
                    return false;
                }
            }
            return true;
        }

        /// <summary>
        /// Executes the hashing functionality. After parsing arguments, the 
        /// function connects to the selected TPM device and invokes the TPM
        /// commands on that connection.
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
            if (!ParseArguments(args, out tpmDeviceName))
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

                    case DeviceLinux:
                        tpmDevice = new LinuxTpmDevice();
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

                Pcrs(tpm);
                QuotePcrs(tpm);
                StorageRootKey(tpm);
                //
                // Need a synchronization event to avoid disposing TPM object before
                // asynchronous method completed.
                // 
                var sync = new AutoResetEvent(false);
                Console.WriteLine("Calling asynchronous method.");
                PrimarySigningKeyAsync(tpm, sync);

                Console.WriteLine("Waiting for asynchronous method to complete.");
                sync.WaitOne();

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
        /// This sample demonstrates the use of the TPM Platform Configuration 
        /// Registers (PCR). TSS.Net provides several features to model PCR
        /// semantics.
        /// </summary>
        /// <param name="tpm">Reference to the TPM object.</param>
        static void Pcrs(Tpm2 tpm)
        {
            Console.WriteLine("\nPCR sample started.");

            //
            // Read the value of the SHA1 PCR 1 and 2
            // 
            var valuesToRead = new PcrSelection[] 
                {
                    new PcrSelection(TpmAlgId.Sha1, new uint[] {1, 2})
                };

            PcrSelection[] valsRead;
            Tpm2bDigest[] values;

            tpm.PcrRead(valuesToRead, out valsRead, out values);

            //
            // Check that what we read is what we asked for (the TPM does not 
            // guarantee this)
            // 
            if (valsRead[0] != valuesToRead[0])
            {
                Console.WriteLine("Unexpected PCR-set");
            }

            //
            // Print out PCR-1
            // 
            var pcr1 = new TpmHash(TpmAlgId.Sha1, values[0].buffer);
            Console.WriteLine("PCR1: " + pcr1);

            //
            // Extend (event) PCR[1] in the TPM and in the external library and
            // see if they match
            //
            var dataToExtend = new byte[] { 0, 1, 2, 3, 4 };

            //
            // Note that most PCR must be authorized with "null" authorization
            // 
            tpm.PcrEvent(TpmHandle.Pcr(1), dataToExtend);

            //
            // And read the current value
            // 
            tpm.PcrRead(valuesToRead, out valsRead, out values);

            //
            // Update the "simulated" PCR
            // 
            pcr1.Event(dataToExtend);

            //
            // And see whether the PCR has the value we expect
            // 
            if (pcr1 != values[0].buffer)
            {
                throw new Exception("Event did not work");
            }

            //
            // Update a resettable PCR
            // 
            tpm.PcrEvent(TpmHandle.Pcr(16), new byte[] { 1, 2 });

            //
            // And reset it
            // 
            tpm.PcrReset(TpmHandle.Pcr(16));

            //
            // And check that it is indeed zero
            // 
            tpm.PcrRead(new PcrSelection[] {
                            new PcrSelection(TpmAlgId.Sha1, new uint[] {16})
                        }, 
                        out valsRead,
                        out values);

            //
            // Did it reset?
            // 
            if (TpmHash.ZeroHash(TpmAlgId.Sha1) != values[0].buffer)
            {
                throw new Exception("PCR did not reset");
            }

            Console.WriteLine("PCR sample finished.");
        } // Pcrs

        /// <summary>
        /// Creates a primary RSA storage key in the storage hierarchy and returns its
        /// handle. The caller can provide an auth value and additional entropy for
        /// the key derivation (primary keys are deterministically derived by the TPM
        /// from an internal primary seed value unique for each hierarchy).
        /// The caller is responsible for disposing of the returned key handle.
        /// </summary>
        /// <param name="tpm">TPM instance to use</param>
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
        /// <param name="tpm">TPM instance to use</param>
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
        /// This sample illustrates the creation and use of an RSA signing key to 
        /// "quote" PCR state
        /// </summary>
        /// <param name="tpm">Reference to the TPM object.</param>
        static void QuotePcrs(Tpm2 tpm)
        {
            Console.WriteLine("\nPCR Quote sample started.");

            //
            // First use a library routine to create an RSA/AES primary storage key
            // with null user-auth.
            // 
            TpmHandle primHandle = CreateRsaPrimaryStorageKey(tpm);

            //
            // Template for a signing key.  We will make the key restricted so that we 
            // can quote with it too.
            // 
            var signKeyPubTemplate = new TpmPublic(TpmAlgId.Sha1,
                                                   ObjectAttr.Sign | ObjectAttr.Restricted |      // A "quoting" key
                                                   ObjectAttr.FixedParent | ObjectAttr.FixedTPM | // Non-duplicable
                                                   ObjectAttr.UserWithAuth |                      // Authorize with auth-data
                                                   ObjectAttr.SensitiveDataOrigin,                // TPM will create a new key
                                                   null,
                                                   new RsaParms(new SymDefObject(), new SchemeRsassa(TpmAlgId.Sha1), 2048, 0),
                                                   new Tpm2bPublicKeyRsa());
            //
            // Auth-data for new key
            // 
            var userAuth = new byte[] { 1, 2, 3, 4 };
            var sensCreate = new SensitiveCreate(userAuth, null);

            //
            // Creation data (not used in this sample)
            // 
            CreationData childCreationData;
            TkCreation creationTicket;
            byte[] creationHash;

            //
            // Create the key
            // 
            TpmPublic keyPub;
            TpmPrivate keyPriv = tpm.Create(primHandle,         // Child of primary key created above
                                            sensCreate,         // Auth-data
                                            signKeyPubTemplate, // Template created above
                                            null,               // Other parms are not used here
                                            new PcrSelection[0],// Not bound to any PCRs
                                            out keyPub,
                                            out childCreationData, out creationHash, out creationTicket);

            Console.WriteLine("New public key\n" + keyPub.ToString());

            //
            // Load the key as a child of the primary that it 
            // was created under.
            // 
            TpmHandle signHandle = tpm.Load(primHandle, keyPriv, keyPub);

            //
            // Note that Load returns the "name" of the key and this is automatically
            // associated with the handle.
            // 
            Console.WriteLine("Name of key:" + BitConverter.ToString(signHandle.Name));

            //
            // Aome data to quote
            // 
            TpmHash hashToSign = TpmHash.FromData(TpmAlgId.Sha1, new byte[] { 4, 3, 2, 1 });

            //
            // PCRs to quote.  SHA-1 bank, PCR-indices 1, 2, and 3
            // 
            var pcrsToQuote = new PcrSelection[] 
            {
                new PcrSelection(TpmAlgId.Sha, new uint[] { 1, 2, 3 })
            };

            //
            // Ask the TPM to quote the PCR (and the nonce).  The TPM
            // returns the quote-signature and the data that was signed
            // 
            ISignatureUnion quoteSig;
            Attest quotedInfo = tpm.Quote(signHandle,
                                                    hashToSign,
                                                    new SchemeRsassa(TpmAlgId.Sha1),
                                                    pcrsToQuote,
                                                    out quoteSig);
            //
            // Print out what was quoted
            // 
            var info = (QuoteInfo)quotedInfo.attested;
            Console.WriteLine("PCRs that were quoted: "    +
                              info.pcrSelect[0].ToString() +
                              "\nHash of PCR-array: "      +
                              BitConverter.ToString(info.pcrDigest));

            //
            // Read the PCR to check the quoted value
            // 
            PcrSelection[] outSelection;
            Tpm2bDigest[] outValues;
            tpm.PcrRead(new PcrSelection[] {
                            new PcrSelection(TpmAlgId.Sha, new uint[] { 1, 2, 3 }) 
                        },
                        out outSelection,
                        out outValues);

            //
            // Use the TSS.Net library to validate the quote against the
            // values just read.
            // 
            bool quoteOk = keyPub.VerifyQuote(TpmAlgId.Sha1, outSelection, outValues,
                                              hashToSign, quotedInfo, quoteSig);
            if (!quoteOk)
            {
                throw new Exception("Quote did not validate");
            }

            Console.WriteLine("Quote correctly validated.");

            //
            // Test other uses of the signing key.  A restricted key can only
            // sign data that the TPM knows does not start with a magic
            // number (that identifies TPM internal data).  So this does not 
            // work
            //
            var nullProof = new TkHashcheck(TpmHandle.RhNull, null);
            tpm._ExpectError(TpmRc.Ticket)
               .Sign(signHandle, hashToSign, new SchemeRsassa(TpmAlgId.Sha1), nullProof);

            //
            // But if we ask the TPM to hash the same data and then sign it 
            // then the TPM can be sure that the data is safe, so it will 
            // sign it.
            // 
            TkHashcheck tkSafeHash;
            TpmHandle hashHandle = tpm.HashSequenceStart(null, TpmAlgId.Sha1);

            //
            // The ticket is only generated if the data is "safe."
            // 
            tpm.SequenceComplete(hashHandle, new byte[] { 4, 3, 2, 1 },
                                 TpmRh.Owner, out tkSafeHash);
            //
            // This will now work because the ticket proves to the 
            // TPM that the data that it is about to sign does not 
            // start with TPM_GENERATED
            // 
            ISignatureUnion sig = tpm.Sign(signHandle, hashToSign,
                                           new SchemeRsassa(TpmAlgId.Sha1), tkSafeHash);
            //
            // And we can verify the signature
            // 
            bool sigOk = keyPub.VerifySignatureOverData(new byte[] { 4, 3, 2, 1 }, sig);
            if (!sigOk)
            {
                throw new Exception("Signature did not verify");
            }

            Console.WriteLine("Signature verified.");

            //
            // Clean up
            // 
            tpm.FlushContext(primHandle);
            tpm.FlushContext(signHandle);

            Console.WriteLine("PCR Quote sample finished.");
        } // QuotePcrs()

        /// <summary>
        /// This sample demonstrates the creation and use of a storage root key that 
        /// behaves like the Storage Root Key (SRK) defined in TPM1.2.
        /// To do this we need to create a new primary, and then use EvictControl
        /// to make it NV-resident.
        /// </summary>
        /// <param name="tpm">Reference to TPM object</param>
        static void StorageRootKey(Tpm2 tpm)
        {
            Console.WriteLine("\nStorageRootKey sample started.");

            //
            // This template asks the TPM to create an 2048 bit RSA storage key 
            // with an associated AES key for symmetric protection of its child keys.
            // NOTE - The term SRK is not used in TPM 2.0 spec, but is widely used
            // in other documents.
            // 
            var srkTemplate = new TpmPublic(TpmAlgId.Sha1,                      // Name algorithm
                                            ObjectAttr.Restricted   |           // Storage keys must be restricted
                                            ObjectAttr.Decrypt      |           // Storage keys are Decrypt keys
                                            ObjectAttr.FixedParent  | ObjectAttr.FixedTPM | // Non-duplicable (like 1.2)
                                            ObjectAttr.UserWithAuth | ObjectAttr.SensitiveDataOrigin,
                                            null,                               // No policy
                                            new RsaParms(new SymDefObject(TpmAlgId.Aes, 128, TpmAlgId.Cfb),
                                                         new NullAsymScheme(),  // No signing or decryption scheme
                                                         2048, 0),              // 2048-bit RSA
                                            new Tpm2bPublicKeyRsa());

            AuthValue childAuthVal = AuthValue.FromRandom(8);
            TssObject swKey = TssObject.Create(srkTemplate, childAuthVal);

            TpmPublic srkPublic;
            CreationData srkCreationData;
            TkCreation srkCreationTicket;
            byte[] srkCreationHash;

            //
            // Ask the TPM to create a new primary RSA/AES primary storage key
            // 
            TpmHandle keyHandle = tpm.CreatePrimary(TpmRh.Owner,            // In the owner-hierarchy
                                                    new SensitiveCreate(null, null), // Empty auth-value
                                                    srkTemplate,            // Key template (params)
                                                    null,                   // For creation ticket
                                                    new PcrSelection[0],    // For creation ticket
                                                    out srkPublic,          // Out pubKey and attrs
                                                    out srkCreationData,    // Not used here
                                                    out srkCreationHash,    // Ibid
                                                    out srkCreationTicket); // Ibid
                                                                
            //
            // print out text-versions of the public key just created
            // 
            Console.WriteLine("New SRK public key\n" + srkPublic.ToString());

            //
            // The caller provides the handle for persistent keys
            // 
            TpmHandle srkHandle = TpmHandle.Persistent(0x5000);

            //
            // Ae will make the "SRK" persistent in an NV-slot, so clean up anything
            // that is already there
            // 
            tpm._AllowErrors()
               .EvictControl(TpmRh.Owner, srkHandle, srkHandle);
            if (tpm._LastCommandSucceeded())
            {
                Console.WriteLine("Removed previous persistent SRK.");
            }

            //
            // Make the SRK NV-resident
            // 
            tpm.EvictControl(TpmRh.Owner, keyHandle, srkHandle);
            Console.WriteLine("SRK is persistent now.");
            Console.WriteLine("\nStorageRootKey sample finished.");
        } // StorageRootKey()

        /// <summary>
        /// This sample demonstrates the async interface to the TPM for selected slow operations.
        /// await-async is preferred when calling slow TPM functions on a UI-thread.  Only a few TPM
        /// functions have an async-form.
        /// </summary>
        /// <param name="tpm">Reference to TPM object</param>
        /// <param name="Event">Synchronization object to signal calling function when we're done.</param>
        static async void PrimarySigningKeyAsync(Tpm2 tpm, AutoResetEvent Event)
        {
            //
            // The TPM needs a template that describes the parameters of the key
            // or other object to be created.  The template below instructs the TPM 
            // to create a new 2048-bit non-migratable signing key.
            // 
            var keyTemplate = new TpmPublic(TpmAlgId.Sha1,                                  // Name algorithm
                                            ObjectAttr.UserWithAuth | ObjectAttr.Sign     | // Signing key
                                            ObjectAttr.FixedParent  | ObjectAttr.FixedTPM | // Non-migratable 
                                            ObjectAttr.SensitiveDataOrigin,
                                            null,                                    // No policy
                                            new RsaParms(new SymDefObject(), 
                                                         new SchemeRsassa(TpmAlgId.Sha1), 2048, 0),
                                            new Tpm2bPublicKeyRsa());
            //
            // Authorization for the key we are about to create
            // 
            var keyAuth = new byte[] { 1, 2, 3 };

            //
            // Ask the TPM to create a new primary RSA signing key
            // 
            Tpm2CreatePrimaryResponse newPrimary = await tpm.CreatePrimaryAsync(
                                            TpmRh.Owner,                        // In the owner-hierarchy
                                            new SensitiveCreate(keyAuth, null), // With this auth-value
                                            keyTemplate,                        // Key params
                                            null,                               // For creation ticket
                                            new PcrSelection[0]);               // For creation ticket

            //
            // Print out text-versions of the public key just created
            // 
            Console.WriteLine("New public key\n" + newPrimary.outPublic.ToString());

            //
            // Use the key to sign some data
            // 
            byte[] message = Encoding.Unicode.GetBytes("ABC");
            TpmHash dataToSign = TpmHash.FromData(TpmAlgId.Sha1, message);
            var sig = await tpm.SignAsync(newPrimary.handle,          // Signing key handle
                                          dataToSign,                       // Data to sign
                                          new SchemeRsassa(TpmAlgId.Sha1),  // Default scheme
                                          TpmHashCheck.Null());
            //
            // Print the signature. A different structure is returned for each 
            // signing scheme, so cast the interface to our signature type.
            // 
            var actualSig = (SignatureRsassa)sig;
            Console.WriteLine("Signature: " + BitConverter.ToString(actualSig.sig));

            //
            // Clean up
            // 
            tpm.FlushContext(newPrimary.handle);

            //
            // Tell caller, we're done.
            // 
            Event.Set();
        }
    }
}

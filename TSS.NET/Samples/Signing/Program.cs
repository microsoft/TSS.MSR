/*
 * Copyright (c) 2013  Microsoft Corporation
 */

using System;
using System.Collections.Generic;
using System.Text;
using Tpm2Lib;

namespace Signing
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
            Console.WriteLine("Usage: Signing [<device>]");
            Console.WriteLine();
            Console.WriteLine("    <device> can be '{0}' or '{1}'. Defaults to '{2}'.", DeviceWinTbs, DeviceSimulator, DefaultDevice);
            Console.WriteLine("        If <device> is '{0}', the program will connect to a simulator\n" +
                              "        listening on a TCP port.", DeviceSimulator);
            Console.WriteLine("        If <device> is '{0}', the program will use the TBS interface to talk\n" +
                              "        to the TPM device.", DeviceWinTbs);
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
                else
                {
                    return false;
                }
            }
            return true;
        }

        /// <summary>
        /// This sample demonstrates the creation of a signing "primary" key and use of this
        /// key to sign data, and use of the TPM and TSS.Net to validate the signature.
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

                //
                // AuthValue encapsulates an authorization value: essentially a byte-array.
                // OwnerAuth is the owner authorization value of the TPM-under-test.  We
                // assume that it (and other) auths are set to the default (null) value.
                // If running on a real TPM, which has been provisioned by Windows, this
                // value will be different. An administrator can retrieve the owner
                // authorization value from the registry.
                //
                var ownerAuth = new AuthValue();

                // 
                // The TPM needs a template that describes the parameters of the key
                // or other object to be created.  The template below instructs the TPM 
                // to create a new 2048-bit non-migratable signing key.
                // 
                var keyTemplate = new TpmPublic(TpmAlgId.Sha1,                                  // Name algorithm
                                                ObjectAttr.UserWithAuth | ObjectAttr.Sign |     // Signing key
                                                ObjectAttr.FixedParent  | ObjectAttr.FixedTPM | // Non-migratable 
                                                ObjectAttr.SensitiveDataOrigin,
                                                null,                                    // No policy
                                                new RsaParms(new SymDefObject(), 
                                                             new SchemeRsassa(TpmAlgId.Sha1), 2048, 0),
                                                new Tpm2bPublicKeyRsa());

                // 
                // Authorization for the key we are about to create.
                // 
                var keyAuth = new byte[] { 1, 2, 3 };

                TpmPublic keyPublic;
                CreationData creationData;
                TkCreation creationTicket;
                byte[] creationHash;

                // 
                // Ask the TPM to create a new primary RSA signing key.
                // 
                TpmHandle keyHandle = tpm[ownerAuth].CreatePrimary(
                    TpmRh.Owner,                            // In the owner-hierarchy
                    new SensitiveCreate(keyAuth, null),     // With this auth-value
                    keyTemplate,                            // Describes key
                    null,                                   // Extra data for creation ticket
                    new PcrSelection[0],                    // Non-PCR-bound
                    out keyPublic,                          // PubKey and attributes
                    out creationData, out creationHash, out creationTicket);    // Not used here

                // 
                // Print out text-versions of the public key just created
                // 
                Console.WriteLine("New public key\n" + keyPublic.ToString());

                // 
                // Use the key to sign some data
                // 
                byte[] message = Encoding.Unicode.GetBytes("ABC");
                TpmHash digestToSign = TpmHash.FromData(TpmAlgId.Sha1, message);

                // 
                // A different structure is returned for each signing scheme, 
                // so cast the interface to our signature type (see third argument).
                // 
                // As an alternative, 'signature' can be of type ISignatureUnion and
                // cast to SignatureRssa whenever a signature specific type is needed.
                // 
                var signature = tpm[keyAuth].Sign(keyHandle,            // Handle of signing key
                                                  digestToSign,         // Data to sign
                                                  null,                 // Use key's scheme
                                                  TpmHashCheck.Null()) as SignatureRsassa;
                // 
                // Print the signature.
                // 
                Console.WriteLine("Signature: " + BitConverter.ToString(signature.sig));

                // 
                // Use the TPM library to validate the signature
                // 
                bool sigOk = keyPublic.VerifySignatureOverData(message, signature);
                if (!sigOk)
                {
                    throw new Exception("Signature did not validate.");
                }

                Console.WriteLine("Verified signature with TPM2lib (software implementation).");

                // 
                // Load the public key into another slot in the TPM and then 
                // use the TPM to validate the signature
                // 
                TpmHandle pubHandle = tpm.LoadExternal(null, keyPublic, TpmRh.Owner);
                tpm.VerifySignature(pubHandle, digestToSign, signature);
                Console.WriteLine("Verified signature with TPM.");

                // 
                // The default behavior of Tpm2Lib is to create an exception if the 
                // signature does not validate. If an error is expected the library can 
                // be notified of this, or the exception can be turned into a value that
                // can be later queried. The following are examples of this.
                // 
                signature.sig[0] ^= 1;
                tpm._ExpectError(TpmRc.Signature)
                   .VerifySignature(pubHandle, digestToSign, signature);

                if (tpm._GetLastResponseCode() != TpmRc.Signature)
                {
                    throw new Exception("TPM returned unexpected return code.");
                }

                Console.WriteLine("Verified that invalid signature causes TPM_RC_SIGNATURE return code.");

                // 
                // Clean up of used handles.
                // 
                tpm.FlushContext(keyHandle);
                tpm.FlushContext(pubHandle);

                // 
                // (Note that serialization is not supported on WinRT)
                // 
                // Demonstrate the use of XML persistence by saving keyPublic to 
                // a file and making a copy by reading it back into a new object
                // 
                // NOTE: 12-JAN-2016: May be removing support for policy
                //       serialization. We'd like to get feedback on whether
                //       this is a desirable feature and should be retained.
                //
                // {
                //     const string fileName = "sample.xml";
                //     string xmlVersionOfObject = keyPublic.GetXml();
                //     keyPublic.XmlSerializeToFile(fileName);
                //     var copyOfPublic = TpmStructureBase.XmlDeserializeFromFile<TpmPublic>(fileName);
                //     
                //     // 
                //     // Demonstrate Tpm2Lib support of TPM-structure equality operators
                //     // 
                //     if (copyOfPublic != keyPublic)
                //     {
                //         Console.WriteLine("Library bug persisting data.");
                //     }
                // }
                //

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
    }
}

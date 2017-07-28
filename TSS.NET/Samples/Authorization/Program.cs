/*
 * Copyright (c) 2013  Microsoft Corporation
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Diagnostics;
using Tpm2Lib;

namespace Authorization
{
    /// <summary>
    /// Main class to contain this sample program.
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
            Console.WriteLine("Usage: Policy [<device>]");
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
        /// <returns>True if the arguments could be parsed. False if an unknown argument or 
        /// malformed argument was present.</returns>
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
        /// Creates a primary RSA storage key.
        /// Illustrates automatic authorization of a permanent handle access.
        /// </summary>
        /// <returns>Handle of the created key.</returns>
        static TpmHandle CreateRsaPrimaryKey(Tpm2 tpm)
        {
            //
            // First member of SensitiveCreate contains auth value of the key
            //
            var sensCreate = new SensitiveCreate(new byte[] {0xa, 0xb, 0xc}, null);

            TpmPublic parms = new TpmPublic(
                TpmAlgId.Sha1,
                ObjectAttr.Restricted | ObjectAttr.Decrypt | ObjectAttr.FixedParent | ObjectAttr.FixedTPM
                    | ObjectAttr.UserWithAuth | ObjectAttr.SensitiveDataOrigin,
                null,
                new RsaParms(
                    new SymDefObject(TpmAlgId.Aes, 128, TpmAlgId.Cfb),
                    new NullAsymScheme(),
                    2048,
                    0),
                new Tpm2bPublicKeyRsa());

            byte[] outsideInfo = Globs.GetRandomBytes(8);
            var creationPcr = new PcrSelection(TpmAlgId.Sha1, new uint[] { 0, 1, 2 });

            TpmPublic pubCreated;
            CreationData creationData;
            TkCreation creationTicket;
            byte[] creationHash;

            Console.WriteLine("Automatic authorization of TpmRh.Owner.");

            //
            // An auth session is added automatically to authorize access to the permanent
            // handle TpmHandle.RhOwner.
            //
            // Note that if the TPM is not a simulator and not cleared, you need to
            // assign the corresponding auth value to the tpm.OwnerAuth property of
            // the given Tpm2 object.
            //
            TpmHandle h = tpm.CreatePrimary(TpmRh.Owner,
                                            sensCreate, 
                                            parms,
                                            outsideInfo,
                                            new PcrSelection[] { creationPcr },
                                            out pubCreated,
                                            out creationData,
                                            out creationHash,
                                            out creationTicket);

            Console.WriteLine("Primary RSA storage key created.");

            return h;
        }

        /// <summary>
        /// Creates a child of the given storage key, which can be used both for signing and decryption.
        /// Illustrates strict mode effect on automatic authorization handling.
        /// </summary>
        /// <returns>Handle of the created key.</returns>
        static TpmHandle CreateSigningDecryptionKey(Tpm2 tpm, TpmHandle primHandle, out TpmPublic keyPublic)
        {
            TpmPublic keyInPublic = new TpmPublic(
                TpmAlgId.Sha1,
                ObjectAttr.Decrypt | ObjectAttr.Sign | ObjectAttr.FixedParent | ObjectAttr.FixedTPM
                    | ObjectAttr.UserWithAuth | ObjectAttr.SensitiveDataOrigin,
                null,
                new RsaParms(
                    new SymDefObject(),
                    new NullAsymScheme(),
                    2048, 0),
               new Tpm2bPublicKeyRsa());

            SensitiveCreate sensCreate = new SensitiveCreate(new byte[] {1, 2, 3}, null);
            CreationData keyCreationData;
            TkCreation creationTicket;
            byte[] creationHash;

            Console.WriteLine("Automatic authorization of a primary storage key.");

            //
            // An auth session is added automatically to authorize access to primHandle.
            //
            TpmPrivate keyPrivate = tpm.Create(primHandle,
                                               sensCreate,
                                               keyInPublic,
                                               null,
                                               new PcrSelection[0],
                                               out keyPublic,
                                               out keyCreationData,
                                               out creationHash,
                                               out creationTicket);

            TpmHandle keyHandle = null;

            Console.WriteLine("Strict mode.");

            //
            // Switch TPM object to the strict mode. (Note that this is a TSS.Net
            // specific piece of functionality, not a part of TPM 2.0 specification).
            //
            tpm._Behavior.Strict = true;

            //
            // No auth session is added automatically when TPM object is in strict mode.
            //
            tpm._ExpectError(TpmRc.AuthMissing)
               .Load(primHandle, keyPrivate, keyPublic);

            //
            // Now explicitly request an auth session of a desired type.
            // The actual auth value will be supplied by TSS.Net implicitly.
            //
            keyHandle = tpm[Auth.Default].Load(primHandle, keyPrivate, keyPublic);

            Console.WriteLine("Signing decryption key created.");

            //
            // Switch TPM object back to the normal mode.
            //
            tpm._Behavior.Strict = false;

            return keyHandle;
        }

        /// <summary>
        /// Illustrates various cases of automatic authorization handling.
        /// </summary>
        static void AutomaticAuth(Tpm2 tpm)
        {
            TpmHandle primHandle = CreateRsaPrimaryKey(tpm);

            TpmPublic keyPublic;
            TpmHandle keyHandle = CreateSigningDecryptionKey(tpm, primHandle, out keyPublic);

            byte[] message = Globs.GetRandomBytes(32);

            IAsymSchemeUnion decScheme = new SchemeOaep(TpmAlgId.Sha1);
            ISigSchemeUnion sigScheme = new SchemeRsassa(TpmAlgId.Sha1);

            //
            // TSS.Net implicitly creates an auth session to authorize keyHandle.
            // It uses the auth value cached in the TpmHandle object.
            //
            byte[] encrypted = tpm.RsaEncrypt(keyHandle, message, decScheme, null);

            Console.WriteLine("Automatic authorization of a decryption key.");

            //
            // An auth session is added automatically when TPM object is not in strict mode.
            //
            byte[] decrypted1 = tpm.RsaDecrypt(keyHandle, encrypted, decScheme, null);

            byte[] nonceTpm;

            Console.WriteLine("Session object construction.");

            //
            // If a session with specific properties is required, an AuthSession object
            // can be built from the session handle returned by the TPM2_StartAuthSession
            // command concatenated, if necessary, with session flags and unencrypted salt
            // value (not used in this example).
            //
            AuthSession auditSess = tpm.StartAuthSession(
                                            TpmRh.Null,        // no salt
                                            TpmRh.Null,        // no bind object
                                            Globs.GetRandomBytes(16),   // nonceCaller
                                            null,       // no salt
                                            TpmSe.Hmac,        // session type
                                            new SymDef(),      // no encryption/decryption
                                            TpmAlgId.Sha256,   // authHash
                                            out nonceTpm)       
                                    + (SessionAttr.ContinueSession | SessionAttr.Audit);
            
            /*
             * Alternatively one of the StartAuthSessionEx helpers can be used). E.g.
             * 
             * AuthSession auditSess = tpm.StartAuthSessionEx(TpmSe.Hmac, TpmAlgId.Sha256,
             *                                  SessionAttr.ContinueSession | SessionAttr.Audit);
             */

            //
            // TSS.Net specific call to verify TPM auditing correctness.
            //
            tpm._SetCommandAuditAlgorithm(TpmAlgId.Sha256);

            Console.WriteLine("Automatic authorization using explicitly created session object.");

            //
            // Appropriate auth value is added automatically into the provided session.
            //
            // Note that the call to _Audit() is optional and is only used when one
            // needs the TSS.Net framework to compute the audit digest on its own (e.g.
            // when simulating the TPM functionality without access to an actual TPM).
            //
            byte[] decrypted2 = tpm[auditSess]._Audit()
                                              .RsaDecrypt(keyHandle, encrypted, decScheme, null);

            ISignatureUnion signature;
            Attest attest;

            //
            // A session is added automatically to authorize usage of the permanent
            // handle TpmRh.Endorsement.
            //
            // Note that if auth value of TpmRh.Endorsement is not empty, you need to
            // explicitly assign it to the tpm.EndorsementAuth property of the given
            // Tpm2 object.
            //
            attest = tpm.GetSessionAuditDigest(TpmRh.Endorsement, TpmRh.Null, auditSess,
                                               null, new NullSigScheme(), out signature);

            //
            // But if the corresponding auth value stored in the Tpm2 object is invalid, ...
            //
            AuthValue endorsementAuth = tpm.EndorsementAuth;
            tpm.EndorsementAuth = Globs.ByteArray(16, 0xde);

            //
            // ... the command will fail.
            //
            tpm._ExpectError(TpmRc.BadAuth)
               .GetSessionAuditDigest(TpmRh.Endorsement, TpmRh.Null, auditSess,
                                      null, new NullSigScheme(), out signature);
            //
            // Restore correct auth value.
            //
            tpm.EndorsementAuth = endorsementAuth;

            //
            // Verify that decryption worked correctly. 
            //
            Debug.Assert(Globs.ArraysAreEqual(decrypted1, decrypted2));

            //
            // Verify that auditing worked correctly. 
            //
            SessionAuditInfo info = (SessionAuditInfo)attest.attested;
            Debug.Assert(Globs.ArraysAreEqual(info.sessionDigest, tpm._GetAuditHash().HashData));

            Console.WriteLine("Auth value tracking by TSS.Net.");

            //
            // Change auth value of the decryption key.
            //
            TpmPrivate newKeyPrivate = tpm.ObjectChangeAuth(keyHandle, primHandle, AuthValue.FromRandom(16));
            TpmHandle newKeyHandle = tpm.Load(primHandle, newKeyPrivate, keyPublic);

            //
            // Allow non-exclusive usage of the audit session.
            //
            auditSess.Attrs &= ~SessionAttr.AuditExclusive;

            //
            // Correct auth value (corresponding to newKeyHandle, and different from
            // the one used for keyHandle) will be added to auditSess.
            //
            decrypted1 = tpm[auditSess]._Audit()
                                       .RsaDecrypt(newKeyHandle, encrypted, decScheme, null);

            Console.WriteLine("Automatic authorization with multiple sessions.");

            //
            // Now two sessions are auto-generated (for TpmRh.Endorsement and keyHandle).
            //
            attest = tpm.GetSessionAuditDigest(TpmRh.Endorsement, keyHandle, auditSess,
                                               null, sigScheme, out signature);

            //
            // Verify that the previous command worked correctly.
            //
            bool sigOk = keyPublic.VerifySignatureOverData(Marshaller.GetTpmRepresentation(attest),
                                                           signature);
            Debug.Assert(sigOk);

            //
            // In the following example the first session is generated based on session
            // type indicator (Auth.Pw), and the second one is added automatically.
            //
            attest = tpm[Auth.Pw].GetSessionAuditDigest(TpmRh.Endorsement, keyHandle, auditSess, 
                                                        null, sigScheme, out signature);

            //
            // Verify that the previous command worked correctly.
            //
            sigOk = keyPublic.VerifySignatureOverData(Marshaller.GetTpmRepresentation(attest),
                                                      signature);
            Debug.Assert(sigOk);

            //
            // Release TPM resources that we do not need anymore.
            //
            tpm.FlushContext(newKeyHandle);
            tpm.FlushContext(auditSess);

            //
            // The following example works correctly only when TPM resource management
            // is not enabled (e.g. with TPM simulator, or when actual TPM is in raw mode).
            //
            if (!tpm._GetUnderlyingDevice().HasRM())
            {
                Console.WriteLine("Using session type indicators.");

                //
                // Deplete TPM's active session storage
                //
                List<AuthSession> landfill = new List<AuthSession>();

                for (;;)
                {
                    tpm._AllowErrors();
                    AuthSession s = tpm.StartAuthSessionEx(TpmSe.Hmac, TpmAlgId.Sha256,
                                                           SessionAttr.ContinueSession);
                    if (!tpm._LastCommandSucceeded())
                    {
                        break;
                    }
                    landfill.Add(s);
                }

                //
                // Check if session type indicators are processed correctly
                //
                tpm[Auth.Hmac]._ExpectError(TpmRc.SessionMemory)
                              .RsaDecrypt(keyHandle, encrypted, new NullAsymScheme(), null);
                //
                // Password authorization protocol session uses a predefined handle value,
                // so it must work even when there are no free session slots in the TPM.
                //
                tpm[Auth.Pw].RsaDecrypt(keyHandle, encrypted, new NullAsymScheme(), null);

                //
                // Check if default session type defined by the TPM device is processed correctly.
                //
                bool needHmac = tpm._GetUnderlyingDevice().NeedsHMAC;

                tpm._GetUnderlyingDevice().NeedsHMAC = true;

                tpm._ExpectError(TpmRc.SessionMemory)
                   .RsaDecrypt(keyHandle, encrypted, new NullAsymScheme(), null);

                tpm[Auth.Default]._ExpectError(TpmRc.SessionMemory)
                                 .RsaDecrypt(keyHandle, encrypted, new NullAsymScheme(), null);

                tpm._GetUnderlyingDevice().NeedsHMAC = false;

                tpm.RsaDecrypt(keyHandle, encrypted, new NullAsymScheme(), null);
                tpm[Auth.Default].RsaDecrypt(keyHandle, encrypted, new NullAsymScheme(), null);

                tpm._GetUnderlyingDevice().NeedsHMAC = needHmac;

                landfill.ForEach(s => tpm.FlushContext(s));
            }

            //
            // Release TPM resources.
            //
            tpm.FlushContext(keyHandle);
            tpm.FlushContext(primHandle);

            Console.WriteLine("Done.");
        }


        /// <summary>
        /// This sample demonstrates the creation of a signing "primary" key and use of this
        /// key to sign data, and use of the TPM and TSS.Net to validate the signature.
        /// </summary>
        /// <param name="args">Arguments to this program.</param>
        static void Main(string[] args)
        {
            string tpmDeviceName;

            //
            // Parse the program arguments. If the wrong arguments are given or
            // are malformed, then instructions for usage are displayed and 
            // the program terminates.
            // 
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
                // Connect to the TPM device. This function actually establishes the connection.
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
                // Run individual tests.
                // 
                AutomaticAuth(tpm);

                //
                // Clean up.
                // 
                tpm.Dispose();
            }
            catch (TpmException e)
            {
                //
                // If a command fails because an unexpected return code is in the response,
                // i.e., TPM returns an error code where success is expected or success
                // where an error code is expected. Or if the response is malformed, then
                // the unmarshaling code will throw a TPM exception.
                // The Error string will contain a description of the return code. Usually the
                // return code will be a known TPM return code. However, if using the TPM through
                // TBS, TBS might encode internal error codes into the response code. For instance
                // a return code of 0x80280400 indicates that a command is blocked by TBS. This
                // error code is also returned if the command is not implemented by the TPM.
                // 
                // You can see the information included in the TPM exception by removing the
                // checks for available TPM commands above and running the sample on a TPM
                // without the required commands.
                // 
                Console.WriteLine("TPM exception occurred: {0}", e.ErrorString);
                Console.WriteLine("Call stack: {0}", e.StackTrace);
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception occurred: {0}", e.Message);
            }

            Console.WriteLine("Press Any Key to continue.");

            Console.ReadLine();
        }

    } // class Program

} // namespace Authorization

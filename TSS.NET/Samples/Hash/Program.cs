/*
 * Copyright (c) 2013  Microsoft Corporation
 */

using System;
using System.Collections.Generic;
using Tpm2Lib;

namespace Hash
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
            Console.WriteLine("Usage: Hash [<device>]");
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

                SimpleHash(tpm);
                HashSequence(tpm);
                HmacUnboundUnseeded(tpm);
                
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
        /// Very simple hash calculation. 
        /// We ask the TPM to calculate the hash of a 3-byte array.
        /// </summary>
        /// <param name="tpm">Reference to the TPM object.</param>
        static void SimpleHash(Tpm2 tpm)
        {
            TkHashcheck validation;
            byte[] hashData = tpm.Hash(new byte[] { 1, 2, 3 },   // Data to hash
                                       TpmAlgId.Sha256,          // Hash algorithm
                                       TpmRh.Owner,              // Hierarchy for ticket (not used here)
                                       out validation);          // Ticket (not used in this example)
            Console.WriteLine("Hashed data (Hash): " + BitConverter.ToString(hashData));
        }

        /// <summary>
        /// Use a hash sequence to concatenate and hash data that is bigger then
        /// the communication buffer to the TPM.
        /// </summary>
        /// <param name="tpm">Reference to the TPM object.</param>
        static void HashSequence(Tpm2 tpm)
        {
            //
            // Create a hash sequence-object based on SHA-1 and with a 10 byte auth-value
            // randomly generated by TSS.Net.
            // This command returns a uint handle value encapsulated in TpmHandle class.
            // 
            TpmHandle hashHandle = tpm.HashSequenceStart(AuthValue.FromRandom(10), TpmAlgId.Sha1);

            //
            // Hash some data using the hash sequence object just created.
            // It is normally the case that the use of TPM internal objects
            // must be "authorized" by proof-of-knowledge of the authorization 
            // value that was set when the object was created. Authorization
            // is communicated using a TPM construct called a "session." 
            // Every handle that requires authorization requires a session
            // that conveys knowledge of the auth-value (or other authorization).
            //
            // The specific style of session used here is a "password authorization
            // session, or PWAP session, where the password is communicated
            // in plain text.
            // 
            // Three styles of session creation are demonstrated.  
            // Style 1 (not preferred).  The method _SetSessions() tells TSS.Net
            // to use the authorization value authVal in a PWAP session 
            // 
            tpm.SequenceUpdate(hashHandle, new byte[] { 0, 1 });

            //
            // Style 2 (not preferred).  The method _SetSessions() returns "this"
            // so the two lines above can be condensed.  tpm._SetSessions(authVal);
            // 
            tpm.SequenceUpdate(hashHandle, new byte[] { 2, 3 });

            //
            // Style 3 - RECOMMENDED
            // In the command sequence below the [authValue] construct is
            // NOT an array-accessor.  Instead it is shorthand to associate 
            // a list of authorization sessions with the command (one session
            // in this case.
            // 
            tpm.SequenceUpdate(hashHandle, new byte[] { 4, 5 });
            tpm.SequenceUpdate(hashHandle, new byte[] { 6, 7 });

            //
            // Add the final data block
            // 
            TkHashcheck validation;
            byte[] hashedData = tpm.SequenceComplete(hashHandle, new byte[] { 4, 5 },
                                                     TpmRh.Owner,
                                                     out validation);

            Console.WriteLine("Hashed data (Sequence): " + BitConverter.ToString(hashedData));
        }

        /// <summary>
        /// This sample shows the use of HMAC sessions to authorize TPM actions.
        /// HMAC sessions may be bound/unbound and seeded/unseeded.  This sample
        /// illustrates an unseeded and unbound session.
        /// </summary>
        /// <param name="tpm">Reference to the TPM object.</param>
        static void HmacUnboundUnseeded(Tpm2 tpm)
        {
            //
            // Create a hash-sequence with a random authorization value
            // 
            TpmHandle hashHandle = tpm.HashSequenceStart(AuthValue.FromRandom(8), TpmAlgId.Sha256);

            //
            // Commands with the Ex modifier are library-provided wrappers
            // around TPM functions to make programming easier.  This version
            // of StartAuthSessionEx calls StartAuthSession configured to 
            // create an unbound and unseeded auth session with the auth-value 
            // provided here.
            // 
            AuthSession s0 = tpm.StartAuthSessionEx(TpmSe.Hmac, TpmAlgId.Sha256);

            //
            // The following calls show the use of the HMAC session in authorization.
            // The session to use is communicated as a parameter in the [] overloaded 
            // function and the auth-value is that set during HMAC session creation.
            // It picks up the appropriate auth value from the handle used in the command
            // (hashHandle in this case).
            // 
            TkHashcheck validate;
            tpm[s0].SequenceUpdate(hashHandle, new byte[] { 0, 2, 1 });
            byte[] hashedData = tpm[s0].SequenceComplete(hashHandle,
                                                         new byte[] { 2, 3, 4 },
                                                         TpmRh.Owner,
                                                         out validate);

            Console.WriteLine("Hashed data (HMAC authorized sequence): " + BitConverter.ToString(hashedData));
            tpm.FlushContext(s0);
        }
    }
}

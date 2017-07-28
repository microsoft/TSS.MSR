/*
 * Copyright (c) 2013  Microsoft Corporation
 */

using System;
using System.Collections.Generic;
using System.Linq;
using Tpm2Lib;

namespace NV
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
            Console.WriteLine("Usage: NV [<device>]");
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

                NVReadWrite(tpm);
                NVCounter(tpm);

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
        /// This sample demonstrates the creation and use of TPM NV-storage
        /// </summary>
        /// <param name="tpm">Reference to TPM object.</param>
        static void NVReadWrite(Tpm2 tpm)
        {
            //
            // AuthValue encapsulates an authorization value: essentially a byte-array.
            // OwnerAuth is the owner authorization value of the TPM-under-test.  We
            // assume that it (and other) auths are set to the default (null) value.
            // If running on a real TPM, which has been provisioned by Windows, this
            // value will be different. An administrator can retrieve the owner
            // authorization value from the registry.
            //
            var ownerAuth = new AuthValue();
            TpmHandle nvHandle = TpmHandle.NV(3001);

            //
            // Clean up any slot that was left over from an earlier run
            // 
            tpm._AllowErrors()
               .NvUndefineSpace(TpmRh.Owner, nvHandle);

            //
            // Scenario 1 - write and read a 32-byte NV-slot
            // 
            AuthValue nvAuth = AuthValue.FromRandom(8);
            tpm.NvDefineSpace(TpmRh.Owner, nvAuth,
                              new NvPublic(nvHandle, TpmAlgId.Sha1,
                                           NvAttr.Authread | NvAttr.Authwrite,
                                           null, 32));

            //
            // Write some data
            // 
            var nvData = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 };
            tpm.NvWrite(nvHandle, nvHandle, nvData, 0);

            //
            // And read it back
            // 
            byte[] nvRead = tpm.NvRead(nvHandle, nvHandle, (ushort)nvData.Length, 0);

            //
            // Is it correct?
            // 
            bool correct = nvData.SequenceEqual(nvRead);
            if (!correct)
            {
                throw new Exception("NV data was incorrect.");
            }

            Console.WriteLine("NV data written and read.");

            //
            // And clean up
            // 
            tpm.NvUndefineSpace(TpmRh.Owner, nvHandle);
        }

        /// <summary>
        /// Demonstrate use of NV counters.
        /// </summary>
        /// <param name="tpm">Reference to the TPM object.</param>
        static void NVCounter(Tpm2 tpm)
        {
            //
            // AuthValue encapsulates an authorization value: essentially a byte-array.
            // OwnerAuth is the owner authorization value of the TPM-under-test.  We
            // assume that it (and other) auths are set to the default (null) value.
            // If running on a real TPM, which has been provisioned by Windows, this
            // value will be different. An administrator can retrieve the owner
            // authorization value from the registry.
            //
            TpmHandle nvHandle = TpmHandle.NV(3001);

            //
            // Clean up any slot that was left over from an earlier run
            // 
            tpm._AllowErrors()
               .NvUndefineSpace(TpmRh.Owner, nvHandle);

            //
            // Scenario 2 - A NV-counter
            // 
            tpm.NvDefineSpace(TpmRh.Owner, AuthValue.FromRandom(8),
                              new NvPublic(nvHandle, TpmAlgId.Sha1,
                                           NvAttr.Counter | NvAttr.Authread | NvAttr.Authwrite,
                                           null, 8));
            //
            // Must write before we can read
            // 
            tpm.NvIncrement(nvHandle, nvHandle);

            //
            // Read the current value
            // 
            byte[] nvRead = tpm.NvRead(nvHandle, nvHandle, 8, 0);
            var initVal = Marshaller.FromTpmRepresentation<ulong>(nvRead);

            //
            // Increment
            // 
            tpm.NvIncrement(nvHandle, nvHandle);

            //
            // Read again and see if the answer is what we expect
            // 
            nvRead = tpm.NvRead(nvHandle, nvHandle, 8, 0);
            var finalVal = Marshaller.FromTpmRepresentation<ulong>(nvRead);
            if (finalVal != initVal + 1)
            {
                throw new Exception("NV-counter fail");
            }

            Console.WriteLine("Incremented counter from {0} to {1}.", initVal, finalVal);

            //
            // Clean up
            // 
            tpm.NvUndefineSpace(TpmRh.Owner, nvHandle);
        }
    }
}

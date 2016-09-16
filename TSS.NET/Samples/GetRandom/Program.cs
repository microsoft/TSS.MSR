/*
 * Copyright (c) 2013  Microsoft Corporation
 */

using System;
using System.Collections.Generic;
using Tpm2Lib;

namespace GetRandom
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
        /// The columns per row to use when displaying the random number.
        /// </summary>
        private const int ColumnsPerRow = 8;

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
        /// Print the random bytes to the console. Format it such that each line is
        /// pre-pended with an index of the first byte, followed by bytes in hexadecimal
        /// format.
        /// </summary>
        /// <param name="bytesToWrite"></param>
        static void WriteBytes(byte[] bytesToWrite)
        {
            for (int i = 0; i < bytesToWrite.Length; i++)
            {
                if (i % ColumnsPerRow == 0)
                {
                    Console.Write("0x{0,4:x4}: ", i);
                }
                Console.Write("0x{0,2:x2} ", bytesToWrite[i]);
                if (i % ColumnsPerRow == ColumnsPerRow - 1)
                {
                    Console.WriteLine();
                }
            }
            Console.WriteLine();
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
        /// Executes the GetRandom functionality. After parsing arguments, the function
        /// connects to the selected TPM device and invokes the GetRandom command on
        /// that connection. If the command was successful, the random byte stream
        /// is displayed.
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

                //
                // Execute the TPM2_GetRandom command. The function takes the requested
                // number of bytes as input and returns the random bytes generated by
                // the TPM.
                // 
                byte[] randomBytes = tpm.GetRandom(bytesRequested);

                //
                // Output the generated random byte string to the console.
                // 
                WriteBytes(randomBytes);

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

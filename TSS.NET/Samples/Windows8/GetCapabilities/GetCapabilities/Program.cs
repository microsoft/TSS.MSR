/*
 * Copyright (c) 2013  Microsoft Corporation
 */

using System;
using System.Collections.Generic;
using System.Text;
using Tpm2Lib;

namespace GetCapabilities
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
            Console.WriteLine("Usage: GetCapabilities [<device>]");
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
        /// Executes the GetCapabilities functionality. After parsing arguments, the 
        /// function connects to the selected TPM device and invokes the GetCapabilities
        /// command on that connection. If the command was successful, the retrieved
        /// capabilities are displayed.
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
                // Query different capabilities
                // 

                ICapabilitiesUnion caps;
                tpm.GetCapability(Cap.Algs, 0, 1000, out caps);
                var algsx = (AlgPropertyArray)caps;

                Console.WriteLine("Supported algorithms:");
                foreach (var alg in algsx.algProperties)
                {
                    Console.WriteLine("  {0}", alg.alg.ToString());
                }

                Console.WriteLine("Supported commands:");
                tpm.GetCapability(Cap.TpmProperties, (uint)Pt.TotalCommands, 1, out caps);
                tpm.GetCapability(Cap.Commands, (uint)TpmCc.First, TpmCc.Last - TpmCc.First + 1, out caps);

                var commands = (CcaArray)caps;
                List<TpmCc> implementedCc = new List<TpmCc>();
                foreach (var attr in commands.commandAttributes)
                {
                    var commandCode = (TpmCc)((uint)attr & 0x0000FFFFU);
                    implementedCc.Add(commandCode);
                    Console.WriteLine("  {0}", commandCode.ToString());
                }
                Console.WriteLine("Commands from spec not implemented:");
                foreach (var cc in Enum.GetValues(typeof(TpmCc)))
                {
                    if (!implementedCc.Contains((TpmCc)cc))
                    {
                        Console.WriteLine("  {0}", cc.ToString());
                    }
                }

                //
                // As an alternative: call GetCapabilities more than once to obtain all values
                //
                byte more;
                var firstCommandCode = (uint)TpmCc.First;
                do
                {
                    more = tpm.GetCapability(Cap.Commands, firstCommandCode, 10, out caps);
                    commands = (CcaArray)caps;
                    //
                    // Commands are sorted; getting the last element as it will be the largest.
                    //
                    uint lastCommandCode = (uint)commands.commandAttributes[commands.commandAttributes.Length - 1] & 0x0000FFFFU;
                    firstCommandCode = lastCommandCode;
                } while (more == 1);

                //
                // Read PCR attributes. Cap.Pcrs returns the list of PCRs which are supported
                // in different PCR banks. The PCR banks are identified by the hash algorithm
                // used to extend values into the PCRs of this bank.
                // 
                tpm.GetCapability(Cap.Pcrs, 0, 255, out caps);
                PcrSelection[] pcrs = ((PcrSelectionArray)caps).pcrSelections;

                Console.WriteLine();
                Console.WriteLine("Available PCR banks:");
                foreach (PcrSelection pcrBank in pcrs)
                {
                    var sb = new StringBuilder();
                    sb.AppendFormat("PCR bank for algorithm {0} has registers at index:", pcrBank.hash);
                    sb.AppendLine();
                    foreach (uint selectedPcr in pcrBank.GetSelectedPcrs())
                    {
                        sb.AppendFormat("{0},", selectedPcr);
                    }
                    Console.WriteLine(sb);
                }

                //
                // Read PCR attributes. Cap.PcrProperties checks for certain properties of each PCR register.
                // 
                tpm.GetCapability(Cap.PcrProperties, 0, 255, out caps);

                Console.WriteLine();
                Console.WriteLine("PCR attributes:");                
                TaggedPcrSelect[] pcrProperties = ((TaggedPcrPropertyArray)caps).pcrProperty;
                foreach (TaggedPcrSelect pcrProperty in pcrProperties)
                {
                    if ((PtPcr)pcrProperty.tag == PtPcr.None)
                    {
                        continue;
                    }

                    uint pcrIndex = 0;
                    var sb = new StringBuilder();
                    sb.AppendFormat("PCR property {0} supported by these registers: ", (PtPcr)pcrProperty.tag);
                    sb.AppendLine();
                    foreach (byte pcrBitmap in pcrProperty.pcrSelect)
                    {
                        for (int i = 0; i < 8; i++)
                        {
                            if ((pcrBitmap & (1 << i)) != 0)
                            {
                                sb.AppendFormat("{0},", pcrIndex);
                            }
                            pcrIndex++;
                        }
                    }
                    Console.WriteLine(sb);
                }

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
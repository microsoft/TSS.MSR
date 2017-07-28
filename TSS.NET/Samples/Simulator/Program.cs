/*
 * Copyright (c) 2013  Microsoft Corporation
 */

using System;
using Tpm2Lib;

namespace Simulator
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
        /// If using a TCP connection, the default DNS name/IP address for the
        /// simulator.
        /// </summary>
        private const string DefaultSimulatorName = "127.0.0.1";
        /// <summary>
        /// If using a TCP connection, the default TCP port of the simulator.
        /// </summary>
        private const int DefaultSimulatorPort = 2321;

        /// <summary>
        /// Executes the hashing functionality. After parsing arguments, the 
        /// function connects to the selected TPM device and invokes the TPM
        /// commands on that connection.
        /// </summary>
        static void Main()
        {
            try
            {
                //
                // Create the device according to the selected connection.
                // 
                Tpm2Device tpmDevice = new TcpTpmDevice(DefaultSimulatorName, DefaultSimulatorPort);
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

                //
                // If we are using the simulator, we have to do a few things the
                // firmware would usually do. These actions have to occur after
                // the connection has been established.
                // 
                tpmDevice.PowerCycle();
                tpm.Startup(Su.Clear);

                ResetDALogic(tpm);
                ResourceManager(tpm);
                PowerAndLocality(tpm);

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
        /// Reset the dictionary-attack logic.
        /// </summary>
        static void ResetDALogic(Tpm2 tpm)
        {
            //
            // set the DA-parms to forgiving.  
            // 
            tpm.DictionaryAttackParameters(TpmHandle.RhLockout, 1000, 10, 1);

            //
            // set the counters to zero
            // 
            tpm.DictionaryAttackLockReset(TpmHandle.RhLockout);

            Console.WriteLine("Reset DA logic.");
        }

        /// <summary>
        /// This sample illustrates the use of the resource manager built into 
        /// Tpm2Lib.  Using the resource manager relieves the programmer of the 
        /// (sometimes burdensome) chore of juggling a small number of TPM slots
        /// </summary>
        /// <param name="tpm">Reference to the TPM object.</param>
        static void ResourceManager(Tpm2 tpm)
        {
            //
            // The Tbs device class has a built-in resource manager. We create an
            // instance of the Tbs device class, but hook it up to the TCP device
            // created above. We also tell the Tbs device class to clean the TPM
            // before we start using it.
            // This sample won't work on top of the default Windows resource manager
            // (TBS).
            // 
            var tbs = new Tbs(tpm._GetUnderlyingDevice(), false);
            var tbsTpm = new Tpm2(tbs.CreateTbsContext());

            //
            // Make more sessions than the TPM has room for
            // 
            const int count = 32;
            var sessions = new AuthSession[count];
            for (int j = 0; j < count; j++)
            {
                sessions[j] = tbsTpm.StartAuthSessionEx(TpmSe.Policy, TpmAlgId.Sha1);
            }

            Console.WriteLine("Created {0} sessions.", count);

            //
            // And now use them. The resource manager will use ContextLoad and 
            // ContextSave to bring them into the TPM
            // 
            for (int j = 0; j < count; j++)
            {
                tbsTpm.PolicyAuthValue(sessions[j].Handle);
            }

            Console.WriteLine("Used {0} sessions.", count);

            //
            // And now clean up
            // 
            for (int j = 0; j < count; j++)
            {
                tbsTpm.FlushContext(sessions[j].Handle);
            }

            Console.WriteLine("Cleaned up.");

            //
            // Dispose of the Tbs device object.
            // 
            tbsTpm.Dispose();
        }

        /// <summary>
        /// NullAuth is the zero-length array auth value (null.)
        /// </summary>
        private static readonly AuthValue _nullAuth = new AuthValue();

        /// <summary>
        /// This sample demonstrates how the caller can control simulated power, locality
        /// and physical-presence against the simulated TPM
        /// </summary>
        /// <param name="tpm">Reference to the TPM object.</param>
        static void PowerAndLocality(Tpm2 tpm)
        {
            //
            // Do a complete simulated clean power-down
            // 
            tpm.Shutdown(Su.Clear);
            tpm._GetUnderlyingDevice().PowerCycle();
            tpm.Startup(Su.Clear);

            Console.WriteLine("Power cycle with TPM2_Startup(CLEAR) completed.");

            //
            // Now do a simulated hibernate
            // 
            tpm.Shutdown(Su.State);
            tpm._GetUnderlyingDevice().PowerCycle();
            tpm.Startup(Su.State);

            Console.WriteLine("Power cycle with TPM2_Startup(STATE) completed.");

            //
            // Execute a command at locality 2
            // 
            tpm._SetLocality(LocalityAttr.TpmLocTwo);
            tpm.PcrReset(TpmHandle.Pcr(21));
            tpm._SetLocality(LocalityAttr.TpmLocZero);

            Console.WriteLine("PCR[21] for locality 2 reset.");

            //
            // Execute a command that needs physical-presence
            // 

            tpm._AssertPhysicalPresence()
               .PpCommands(TpmHandle.RhPlatform, new TpmCc[0], new TpmCc[0]);
            Console.WriteLine("Physical presence commands tested.");
        }
    }
}
/*
 * Copyright (c) 2013  Microsoft Corporation
 */

using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.Linq;
using System.Runtime.InteropServices;
using System.Security.Principal;
using Tpm2Lib;

namespace NVWindows
{
    /// <summary>
    /// Main class to contain the program of this sample.
    /// </summary>
    class Program
    {
        /// <summary>
        /// Executes the hashing functionality. After parsing arguments, the 
        /// function connects to the selected TPM device and invokes the TPM
        /// commands on that connection.
        /// </summary>
        /// <param name="args">Arguments to this program.</param>
        static void Main(string[] args)
        {
            try
            {
                //
                // Create the device according to the selected connection.
                // 
                Tpm2Device tpmDevice = new TbsDevice();

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
                // Run test
                //
                NvReadWriteWithOwnerAuth(tpm);

                //
                // Clean up.
                // 
                tpm.Dispose();
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception occurred: {0}", e.Message);
                Console.WriteLine("{0}", e.StackTrace);
            }

            Console.WriteLine("Press Any Key to continue.");
            Console.ReadLine();
        }

        /// <summary>
        /// This sample demonstrates the creation and use of TPM NV-storage
        /// </summary>
        /// <param name="tpm">Reference to TPM object.</param>
        static void NvReadWriteWithOwnerAuth(Tpm2 tpm)
        {
            if (tpm._GetUnderlyingDevice().GetType() != typeof(TbsDevice))
            {
                return;
            }

            int nvIndex = 3001; // arbitrarely chosen
            TpmHandle nvHandle = TpmHandle.NV(nvIndex);
            //
            // The NV auth value is required to read and write the NV slot after it has been
            // created. Because this test is supposed to be used in different conditions:
            // first as Administrator to create the NV slot, and then as Standard User to read
            // it, the test uses a well defined authorization value.
            //
            // In a real world scenario, tha authorization value should be bigger and random,
            // or include a policy with better policy. For example, the next line could be
            // substitued with:
            // AuthValue nvAuth = AuthValue.FromRandom(32);
            // which requires storage of the authorization value for reads.
            //
            AuthValue nvAuth = new AuthValue(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 });
            var nvData = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 };

            WindowsIdentity identity = WindowsIdentity.GetCurrent();
            WindowsPrincipal principal = new WindowsPrincipal(identity);
            if (principal.IsInRole(WindowsBuiltInRole.Administrator))
            {
                Console.WriteLine("Running as Administrator. Deleting and re-creating NV entry.");

                //
                // AuthValue encapsulates an authorization value: essentially a byte-array.
                // OwnerAuth is the owner authorization value of the TPM-under-test.  We
                // assume that it (and other) auths are set to the default (null) value.
                // If running on a real TPM, which has been provisioned by Windows, this
                // value will be different. An administrator can retrieve the owner
                // authorization value from the registry.
                //
                byte[] ownerAuth;
                if (GetOwnerAuthFromOS(out ownerAuth))
                {
                    tpm.OwnerAuth = ownerAuth;
                }
                else
                {
                    Console.WriteLine("Could not retrieve owner auth from registry. Trying empty auth.");
                }

                bool failed;
                do
                {
                    failed = false;
                    //
                    // Clean up any slot that was left over from an earlier run.
                    // Only clean up the nvIndex if data from a possible previous invocation
                    // should be deleted.
                    //
                    // Another approach could be to invoke NvDefineSpace, check if the call
                    // returns TpmRc.NvDefined, then try a read with the known/stored
                    // NV authorization value. If that succeeds, the likelyhood that this
                    // NV index already contains valid data is high.
                    // 
                    tpm._AllowErrors().NvUndefineSpace(TpmHandle.RhOwner, nvHandle);

                    //
                    // Define the NV slot. The authorization passed in as nvAuth will be
                    // needed for future NvRead and NvWrite access. (Attribute Authread
                    // specifies that authorization is required to read. Attribute Authwrite
                    // specifies that authorization is required to write.)
                    // 
                    try
                    {
                        tpm.NvDefineSpace(TpmHandle.RhOwner, nvAuth,
                                                 new NvPublic(nvHandle, TpmAlgId.Sha1,
                                                              NvAttr.Authread | NvAttr.Authwrite,
                                                              new byte[0], 32));
                    }
                    catch (TpmException e)
                    {
                        if (e.RawResponse == TpmRc.NvDefined)
                        {
                            nvIndex++;
                            nvHandle = TpmHandle.NV(nvIndex);
                            Console.WriteLine("NV index already taken, trying next.");
                            failed = true;
                        }
                        else
                        {
                            Console.WriteLine("Exception {0}\n{1}", e.Message, e.StackTrace);
                            return;
                        }
                    }

                    //
                    // Store successful nvIndex and nvAuth, so next invocation as client
                    // knows which index to read. For instance in registry. Storage of 
                    // nvAuth is only required if attributes of NvDefineSpace include 
                    // NvAttr.Authread.
                    // 
                } while (failed);

                //
                // Now that NvDefineSpace succeeded, write some random data (nvData) to
                // nvIndex. Note that NvDefineSpace defined the NV slot to be 32 bytes,
                // so a NvWrite (nor NvRead) should try to write more than that.
                // If more data has to be written to the NV slot, NvDefineSpace should
                // be adjusted accordingly.
                // 
                Console.WriteLine("Writing NVIndex {0}.", nvIndex);
                tpm[nvAuth].NvWrite(nvHandle, nvHandle, nvData, 0);
                Console.WriteLine("Written: {0}", BitConverter.ToString(nvData));
            }

            //
            // Read the data back.
            // 
            Console.WriteLine("Reading NVIndex {0}.", nvIndex);
            byte[] nvRead = tpm[nvAuth].NvRead(nvHandle, nvHandle, (ushort)nvData.Length, 0);
            Console.WriteLine("Read: {0}", BitConverter.ToString(nvRead));

            //
            // Optional: compare if data read from NV slot is the same as data 
            // written to it.
            // We can only compare if running as admin, because the data has been
            // generated with admin account.
            // 
            if (principal.IsInRole(WindowsBuiltInRole.Administrator))
            {
                bool correct = nvData.SequenceEqual(nvRead);
                if (!correct)
                {
                    throw new Exception("NV data was incorrect.");
                }
            }

            Console.WriteLine("NV access complete.");

            if (principal.IsInRole(WindowsBuiltInRole.Administrator))
            {
                //
                // Optional: clean up.
                // If NvIndex should stick around, skip this code.
                // 
                //tpm.NvUndefineSpace(TpmHandle.RhOwner, nvHandle);
            }
        }

        static bool GetOwnerAuthFromOS(out byte[] ownerAuth)
        {
            ownerAuth = new byte[0];

            // open context
            TbsWrapper.TBS_CONTEXT_PARAMS contextParams;
            UIntPtr tbsContext = UIntPtr.Zero;
            contextParams.Version = TbsWrapper.TBS_CONTEXT_VERSION.TWO;
            contextParams.Flags = TbsWrapper.TBS_CONTEXT_CREATE_FLAGS.IncludeTpm20;
            TbsWrapper.TBS_RESULT result = TbsWrapper.NativeMethods.Tbsi_Context_Create(ref contextParams, ref tbsContext);

            if (result != TbsWrapper.TBS_RESULT.TBS_SUCCESS)
            {
                return false;
            }
            if (tbsContext == UIntPtr.Zero)
            {
                return false;
            }

            // get owner auth size
            uint ownerAuthSize = 0;
            TbsWrapper.TBS_OWNERAUTH_TYPE ownerType = TbsWrapper.TBS_OWNERAUTH_TYPE.TBS_OWNERAUTH_TYPE_STORAGE_20;
            result = TbsWrapper.NativeMethods.Tbsi_Get_OwnerAuth(tbsContext, ownerType, ownerAuth, ref ownerAuthSize);
            if (result != TbsWrapper.TBS_RESULT.TBS_SUCCESS &&
                result != TbsWrapper.TBS_RESULT.TBS_E_INSUFFICIENT_BUFFER)
            {
                ownerType = TbsWrapper.TBS_OWNERAUTH_TYPE.TBS_OWNERAUTH_TYPE_FULL;
                result = TbsWrapper.NativeMethods.Tbsi_Get_OwnerAuth(tbsContext, ownerType, ownerAuth, ref ownerAuthSize);
                if (result != TbsWrapper.TBS_RESULT.TBS_SUCCESS &&
                    result != TbsWrapper.TBS_RESULT.TBS_E_INSUFFICIENT_BUFFER)
                {
                    Console.WriteLine(Globs.GetResourceString("Failed to get ownerAuth."));
                    return false;
                }
            }
            // get owner auth itself
            ownerAuth = new byte[ownerAuthSize];
            result = TbsWrapper.NativeMethods.Tbsi_Get_OwnerAuth(tbsContext, ownerType, ownerAuth, ref ownerAuthSize);
            if (result != TbsWrapper.TBS_RESULT.TBS_SUCCESS)
            {
                Console.WriteLine(Globs.GetResourceString("Failed to get ownerAuth."));
                return false;
            }

            TbsWrapper.NativeMethods.Tbsip_Context_Close(tbsContext);

            return true;
        }
    } // class Program

    internal class TbsWrapper
    {
        public class NativeMethods
        {
            [DllImport("tbs.dll", CharSet = CharSet.Unicode)]
            internal static extern TBS_RESULT
            Tbsi_Context_Create(
                ref TBS_CONTEXT_PARAMS ContextParams,
                ref UIntPtr Context);

            [DllImport("tbs.dll", CharSet = CharSet.Unicode)]
            internal static extern TBS_RESULT
            Tbsip_Context_Close(
                UIntPtr Context);

            [DllImport("tbs.dll", CharSet = CharSet.Unicode)]
            internal static extern TBS_RESULT
                Tbsi_Get_OwnerAuth(
                UIntPtr Context,
                [System.Runtime.InteropServices.MarshalAs(UnmanagedType.U4), In]
                TBS_OWNERAUTH_TYPE OwnerAuthType,
                [System.Runtime.InteropServices.MarshalAs(UnmanagedType.LPArray, SizeParamIndex = 3), In, Out]
                 byte[] OutBuffer,
                ref uint OutBufferSize);
        }

        public enum TBS_RESULT : uint
        {
            TBS_SUCCESS = 0,
            TBS_E_BLOCKED = 0x80280400,
            TBS_E_INTERNAL_ERROR = 0x80284001,
            TBS_E_BAD_PARAMETER = 0x80284002,
            TBS_E_INSUFFICIENT_BUFFER = 0x80284005,
            TBS_E_COMMAND_CANCELED = 0x8028400D,
            TBS_E_OWNERAUTH_NOT_FOUND = 0x80284015
        }

        public enum TBS_OWNERAUTH_TYPE : uint
        {
            TBS_OWNERAUTH_TYPE_FULL = 1,
            TBS_OWNERAUTH_TYPE_ADMIN = 2,
            TBS_OWNERAUTH_TYPE_USER = 3,
            TBS_OWNERAUTH_TYPE_ENDORSEMENT = 4,
            TBS_OWNERAUTH_TYPE_ENDORSEMENT_20 = 12,
            TBS_OWNERAUTH_TYPE_STORAGE_20 = 13
        }

        [StructLayout(LayoutKind.Sequential)]
        public struct TBS_CONTEXT_PARAMS
        {
            public TBS_CONTEXT_VERSION Version;
            public TBS_CONTEXT_CREATE_FLAGS Flags;
        }

        public enum TBS_CONTEXT_VERSION : uint
        {
            ONE = 1,
            TWO = 2
        }

        public enum TBS_CONTEXT_CREATE_FLAGS : uint
        {
            RequestRaw = 0x00000001,
            IncludeTpm12 = 0x00000002,
            IncludeTpm20 = 0x00000004,
        }
    } // class TbsWrapper
}

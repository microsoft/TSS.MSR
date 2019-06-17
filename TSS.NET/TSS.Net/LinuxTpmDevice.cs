/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.Diagnostics;
using System.IO;
#if WINDOWS_UWP
using System.Threading.Tasks;
#else
using System.Threading;
#endif
using System.Runtime.InteropServices;
using Interop = System.Runtime.InteropServices;

namespace Tpm2Lib
{

    // TODO: Make tpm2-abrmd interface architecture agnostic (now 64-bit only)
    internal class AbrmdWrapper
    {
        public class NativeMethods
        {
            [DllImport("libtss2-tcti-tabrmd.so", CallingConvention = CallingConvention.Cdecl)]
            //[return: Interop.MarshalAs(UnmanagedType.LPStruct)]
            public static extern IntPtr Tss2_Tcti_Info();
        }

        [UnmanagedFunctionPointer(CallingConvention.Cdecl)]
        public delegate uint tcti_init_fn(
            IntPtr ctx,
            ref uint size,
            [Interop.MarshalAs(UnmanagedType.LPStr), In]
            string cfg
        );

        [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Ansi/*, Pack = 8*/)]
        public class TctiProvInfo
        {
            public uint version;
            [Interop.MarshalAs(UnmanagedType.LPStr)]
            public String name;
            [Interop.MarshalAs(UnmanagedType.LPStr)]
            public String descr;
            [Interop.MarshalAs(UnmanagedType.LPStr)]
            public String help;
            public tcti_init_fn init;
        };

        public delegate TpmRc transmit_fn(IntPtr ctx,
                    ulong cmd_size,
                    [Interop.MarshalAs(UnmanagedType.LPArray, SizeParamIndex = 1), In]
                    byte[] command);
        public delegate TpmRc receive_fn(IntPtr ctx,
                    ref ulong resp_size,
                    [Interop.MarshalAs(UnmanagedType.LPArray, SizeParamIndex = 1), Out]
                    byte[] response,
                    int timeout);
        public delegate void finalize_fn(IntPtr ctx);

        public delegate TpmRc cancel_fn(IntPtr ctx);
        public delegate TpmRc getPollHandles_fn(IntPtr ctx, IntPtr handles, ref uint num_handles);
        public delegate TpmRc setLocality_fn(IntPtr ctx, byte locality);

        [StructLayout(LayoutKind.Sequential/*, Pack = 8*/)]
        public class TctiContext
        {
            public ulong magic;
            public uint version;
            public transmit_fn transmit;
            public receive_fn receive;
            public finalize_fn finalize;
            public cancel_fn cancel;
            public getPollHandles_fn getPollHandles;
            public setLocality_fn setLocality;
        };

        internal static TctiContext Load(out IntPtr tctiCtxPtr)
        {
            uint ctxSize = 0,
                 res;

            IntPtr tpiPtr = AbrmdWrapper.NativeMethods.Tss2_Tcti_Info();
            if (tpiPtr == IntPtr.Zero)
            {
                tctiCtxPtr = IntPtr.Zero;
                return null;
            }
            //Console.WriteLine("AbrmdWrapper: Got pointer to TctiProvInfo from abrmd!");

            TctiProvInfo tpi = new TctiProvInfo();
            Marshal.PtrToStructure(tpiPtr, tpi);
            //Console.WriteLine("AbrmdWrapper: Unmarshaled TctiProvInfo");

            tcti_init_fn tctiInitFn = tpi.init;

#if false // option with direct loading the initialization method
            // [DllImport("libtss2-tcti-tabrmd.so", CallingConvention = CallingConvention.Cdecl, CharSet = CharSet.Ansi)]
            // public static extern uint Tss2_Tcti_Tabrmd_Init(IntPtr ctx, ref uint size, [Interop.MarshalAs(UnmanagedType.LPStr), In] string cfg);

                Console.WriteLine("Calling Tss2_Tcti_Tabrmd_Init()...");
                tctiInitFn = AbrmdWrapper.NativeMethods.Tss2_Tcti_Tabrmd_Init;
#endif
#if false // option with manual extraction of the initialization function pointer from the TCTI provider data structure
                IntPtr tctiInitFnPtr = Marshal.ReadIntPtr(tpiPtr, 32);
                Console.WriteLine("Reading tcti_init_fn...");
                tcti_init_fn tctiInitFn = Marshal.GetDelegateForFunctionPointer<tcti_init_fn>(tctiInitFnPtr);
                Console.WriteLine("AbrmdWrapper: Unmarshaling tcti_init_fn succeeded");
#endif

            res = tctiInitFn(IntPtr.Zero, ref ctxSize, null);
            //Console.WriteLine($"AbrmdWrapper: Initial call to tcti_init_fn() returned {res:X}; ctxSize = {ctxSize}");

            tctiCtxPtr = Marshal.AllocHGlobal((int)ctxSize);
            res = tctiInitFn(tctiCtxPtr, ref ctxSize, null);
            //Console.WriteLine($"AbrmdWrapper: Successfully initialized TCTI ctx");

            var tctiCtx = new TctiContext();
            Marshal.PtrToStructure(tctiCtxPtr, tctiCtx);
            //Console.WriteLine("AbrmdWrapper: Unmarshaled TCTI_CTX");

            return tctiCtx;
        }
    } // class AbrmdWrapper

    public sealed class LinuxTpmDevice : Tpm2Device
    {
        private const int TpmIORetryCount = 20;
        private const int TpmIORetryBackoffTime = 200;
        private string _tpmDevicePath;
        private FileStream _tpmIO = null;
        private byte[] _responseBuffer = new byte[8192];

        // This contained TPM device is used in case there is a user mode TPM Resourse Manager
        // (TRM) running on Linux (it comes from the tpm2-tools package).
        Tpm2Device  TrmDevice = null;
        IntPtr      TctiCtxPtr = IntPtr.Zero;
        AbrmdWrapper.TctiContext    TctiCtx = null;

        public LinuxTpmDevice(string tpmDevicePath = null)
        {
            _tpmDevicePath = tpmDevicePath ?? "/dev/tpm0";
//            _tpmDevicePath = tpmDevicePath ?? "/dev/tpmrm0";
            try
            {
                Connect();
            }
            catch (Exception)
            {
                //Console.WriteLine("Failed to connect to " + tpmDevicePath);

                try
                {
                    TctiCtx = AbrmdWrapper.Load(out TctiCtxPtr);
                }
                catch (Exception e)
                {
                    Console.WriteLine($"Exception while loading tpm2-abrmd: {e}");
                }

                if (TctiCtx == null)
                {
                    // If the first attempt to connect was to the kernel mode TRM,
                    // then try to connect to the raw TPM device, and vice versa.
                    _tpmDevicePath = _tpmDevicePath.Contains("/dev/tpmrm")
                                   ? _tpmDevicePath.Replace("/dev/tpmrm", "/dev/tpm") : "/dev/tpmrm0";
                    try
                    {
                        Connect();
                    }
                    catch (Exception)
                    {
                        //Console.WriteLine("Failed to connect to " + tpmDevicePath);
                        Debug.Assert(_tpmIO == null);

                        TrmDevice = new TcpTpmDevice("127.0.0.1", 2323, false, true);
                        TrmDevice.Connect();
                    }
                }
            }
            Close();
        }

        // Connect to TPM device
        public override void Connect()
        {
            if (TctiCtx != null && TctiCtxPtr == IntPtr.Zero)
            {
                try
                {
                    TctiCtx = AbrmdWrapper.Load(out TctiCtxPtr);
                }
                catch (Exception e)
                {
                    Console.WriteLine($"Exception while loading tpm2-abrmd: {e}");
                }
            }
            else if (TrmDevice != null)
                TrmDevice.Connect();
            else
                _tpmIO = new FileStream(_tpmDevicePath, FileMode.Open, FileAccess.ReadWrite);
        }

        // Send TPM-command buffer to device
        public override void DispatchCommand(
            CommandModifier mod,
            byte[] cmdBuf, 
            out byte[] respBuf)
        {
            if (TctiCtx != null && TctiCtxPtr != IntPtr.Zero)
            {
                TpmRc rc = TctiCtx.transmit(TctiCtxPtr, (ulong)cmdBuf.Length, cmdBuf);
                if (rc != TpmRc.Success)
                    throw new TssException($"TCTI_CTX::transmit() failed: error {rc}");

                ulong bytesReceived = (ulong)_responseBuffer.Length;
                rc = TctiCtx.receive(TctiCtxPtr, ref bytesReceived, _responseBuffer, 5 * 60 * 1000);
                if (rc != TpmRc.Success)
                    throw new TssException($"TCTI_CTX::receive() failed: error {rc}");
                
                respBuf = new byte[bytesReceived];
                Array.Copy(_responseBuffer, respBuf, (int)bytesReceived);
            }
            else if (TrmDevice != null)
            {
                TrmDevice.DispatchCommand(mod, cmdBuf, out respBuf);
                return;
            }
            else if (_tpmIO != null)
            {
                _tpmIO.Write(cmdBuf, 0, cmdBuf.Length);

                int count = 0;
                int bytesRead = 0;
                do
                {
                    bytesRead = _tpmIO.Read(_responseBuffer, 0, _responseBuffer.Length);
                    if (bytesRead > 0) break;

    #if WINDOWS_UWP
                    Task.Delay(TpmIORetryBackoffTime).Wait();
    #else
                    Thread.Sleep(TpmIORetryBackoffTime);
    #endif
                    Debug.WriteLine($"TPM {_tpmDevicePath} retry {count}.");
                } while (count++ < TpmIORetryCount);

                if (bytesRead <= 0)
                {
                    throw new IOException($"No response from {_tpmDevicePath}");
                }

                respBuf = new byte[bytesRead];
                Array.Copy(_responseBuffer, respBuf, bytesRead);
            }
            else
                throw new InvalidOperationException("TPM context is not initialized.");
        }

        public override void Close()
        {
            if (TctiCtx != null && TctiCtxPtr != IntPtr.Zero)
            {
                Console.WriteLine("Closing TCTI conn");
                TctiCtx.finalize(TctiCtxPtr);
                Console.WriteLine("TCTI conn closed!");
                TctiCtxPtr = IntPtr.Zero;
            }
            else {
                if (_tpmIO != null)
                    _tpmIO.Close();
                if (TrmDevice != null)
                    TrmDevice.Close();
            }
        }

        protected override void Dispose(bool disposing)
        {
            Close();
            if (disposing)
            {
                if (_tpmIO != null)
                {
                    _tpmIO.Dispose();
                    _tpmIO = null;
                }
                if (TrmDevice != null)
                {
                    TrmDevice.Dispose();
                    TrmDevice = null;
                }
            }
        }

        // Power-cycle TPM device
        public override void PowerCycle()
        {
            throw new NotSupportedException();
        }

        /// <summary>
        /// Queries whether the TPM device supports sending/emulation of platform signals,
        /// and if the platform hierarchy is enabled. In particular platform signals
        /// are required to power-cycle the TPM.
        /// </summary>
        public override bool PlatformAvailable()
        {
            return false;
        }

        // Assert physical presence on underlying device
        public override void AssertPhysicalPresence(bool assertPhysicalPresence)
        {
            throw new NotSupportedException();
        }

        // Return whether physical presence can be asserted
        public override bool ImplementsPhysicalPresence()
        {
            return false;
        }

        // Return whether the TPM device is accessed via TBS.
        public override bool UsesTbs()
        {
            return false;
        }

        // Return whether the TPM device implements Resource Management.
        public override bool HasRM()
        {
            return true;
        }

        // Return whether cancel is implemented
        public override bool ImplementsCancel()
        {
            return false;
        }

        // attempt to cancel any outstanding command
        public override void CancelContext()
        {
            throw new NotSupportedException();
        }

        // Return underlying handle (not all devices)
        public override UIntPtr GetHandle(UIntPtr p)
        {
            throw new NotSupportedException();
        }

        public override void SignalHashStart()
        {
            throw new NotSupportedException();
        }

        // hash data
        public override void SignalHashData(byte[] data)
        {
            throw new Exception("Should never be here");
        }

        // Send hash-end signal
        public override void SignalHashEnd()
        {
            throw new NotSupportedException();
        }

        // Send new Endorsement Primary Seed to TPM simulator
        public override void TestFailureMode()
        {
            throw new NotImplementedException("Signal TestFailureMode is supported only by TPM simulator");
        }

        // Send cancel-on signal
        public override void SignalCancelOn()
        {
            CancelContext();
        }

        //  Send cancel-off signal
        public override void SignalCancelOff()
        {
        }

        // Switch NV On
        public override void SignalNvOn()
        {
            throw new NotSupportedException();
        }

        // Switch NV Off
        public override void SignalNvOff()
        {
            throw new NotSupportedException();
        }

        // Switch key caching On
        public override void SignalKeyCacheOn()
        {
            throw new NotSupportedException();
        }

        // Switch key caching Off
        public override void SignalKeyCacheOff()
        {
            throw new NotSupportedException();
        }

        public override byte[] GetLockoutAuth()
        {
            return new byte[0];
        }

        public override byte[] GetOwnerAuth()
        {
            return new byte[0];
        }

        public override byte[] GetEndorsementAuth()
        {
            return new byte[0];
        }
    }
}

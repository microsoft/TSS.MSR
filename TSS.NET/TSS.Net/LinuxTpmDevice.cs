// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Diagnostics.CodeAnalysis;
using System.IO;
using System.Runtime.InteropServices;
using System.Threading;

namespace Tpm2Lib
{
    public sealed class LinuxTpmDevice : Tpm2Device
    {
        private const int TpmIORetryCount = 20;
        private const int TpmIORetryBackoffTime = 200;
        private string _tpmDevicePath;
        private FileStream _tpmIO;
        private byte[] _responseBuffer = new byte[8192];

        public LinuxTpmDevice() : this("/dev/tpm0")
        {
        }

        public LinuxTpmDevice(string tpmDevicePath)
        {
            _tpmDevicePath = tpmDevicePath;
        }

        // Send TPM-command buffer to device
        public override void DispatchCommand(
            CommandModifier mod,
            byte[] cmdBuf, 
            out byte[] respBuf)
        {
            if (_tpmIO == null)
            {
                throw new InvalidOperationException("TPM context not created.");
            }

            _tpmIO.Write(cmdBuf, 0, cmdBuf.Length);

            int count = 0;
            int bytesRead = 0;
            do
            {
                bytesRead = _tpmIO.Read(_responseBuffer, 0, _responseBuffer.Length);
                if (bytesRead > 0) break;

                Thread.Sleep(TpmIORetryBackoffTime);
                Debug.WriteLine($"TPM {_tpmDevicePath} retry {count}.");
            } while (count++ < TpmIORetryCount);

            if (bytesRead <= 0)
            {
                throw new IOException($"No response from {_tpmDevicePath}");
            }

            respBuf = new byte[bytesRead];
            Array.Copy(_responseBuffer, respBuf, bytesRead);
        }

        // Connect to TPM device
        public override void Connect()
        {
            _tpmIO = new FileStream(_tpmDevicePath, FileMode.Open, FileAccess.ReadWrite);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                _tpmIO.Dispose();
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

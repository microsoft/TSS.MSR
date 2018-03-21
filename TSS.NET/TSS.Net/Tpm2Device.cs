/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;

namespace Tpm2Lib
{
    /// <summary>
    /// All tpm devices must derive from Tpm2Device.  TPM devices must forward
    /// TPM commands and other actions (e.g. assertion of physical-presence) to their
    /// associated TPM.  Note that not all TPM devices will be able to support all
    /// of the actions here.  In some cases the caller can query whether an action 
    /// is supported (e.g. can the TPM power state be programmatically cycled
    /// </summary>
    public abstract class Tpm2Device : IDisposable
    {
        // Send TPM-command buffer to device
        public virtual void DispatchCommand(CommandModifier mod,
                                            byte[] cmdBuf, out byte[] respBuf)
        {
            throw new Exception("Tpm2Device.DispatchCommand: Should never be here");
        }

        // Connect to TPM device
        public virtual void Connect()
        {
            throw new Exception("Tpm2Device.Connect: Should never be here");
        }

        // Close the connection to the TPM device
        public virtual void Close()
        {
            throw new Exception("Tpm2Device.Close: Should never be here");
        }

        // Power-cycle TPM device
        public virtual void PowerCycle()
        {
        }

        /// <summary>
        /// Queries whether the TPM device can be power cycled programmatically.
        /// </summary>
        public virtual bool PowerCtlAvailable()
        {
            return false;
        }

        /// <summary>
        /// Queries whether the TPM device allows changing locality programmatically.
        /// </summary>
        public virtual bool LocalityCtlAvailable()
        {
            return false;
        }

        /// <summary>
        /// Queries whether the TPM device allows turning TPM NV on/off programmatically.
        /// </summary>
        public virtual bool NvCtlAvailable()
        {
            return false;
        }

        /// <summary>
        /// Queries whether the TPM device supports sending/emulation of platform signals,
        /// and if the platform hierarchy is enabled. In particular platform signals
        /// are required to power-cycle the TPM.
        /// </summary>
        public virtual bool PlatformAvailable()
        {
            return false;
        }

        // Assert physical presence on underlying device
        public virtual void AssertPhysicalPresence(bool assertPhysicalPresence)
        {
            throw new Exception("AssertPhysicalPresence: Should not be here");
        }

        // Return whether physical presence can be asserted
        public virtual bool ImplementsPhysicalPresence()
        {
            return false;
        }

        // Return whether the TPM device is accessed via TBS.
        public virtual bool UsesTbs()
        {
            return false;
        }

        // Return whether the TPM device implements Resource Management.
        public virtual bool HasRM()
        {
            return _HasRM;
        }

        // ReSharper disable once InconsistentNaming
        public bool _HasRM = false;

        // ReSharper disable once InconsistentNaming
        public bool _NeedsHMAC = true;

        // Return true if the device requires HMAC authorization sessions. A rule of
        // thumb is that HMAC session should be used when communication to TPM occurs
        // via an untrusted channel. Otherwise password session suffices. 
        public bool NeedsHMAC
        {
            get
            {
                return _NeedsHMAC;
            }
            set
            {
                _NeedsHMAC = value;
            }
        }

        // attempt to cancel any outstanding command
        public virtual void CancelContext()
        {
            throw new Exception("Should never be here");
        }

        // Clean up
        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        protected virtual void Dispose(bool disposing)
        {
            if (disposing)
            {
                // nothing...
            }
        }

        // Return underlying handle (not all devices)
        public virtual UIntPtr GetHandle(UIntPtr p)
        {
            return UIntPtr.Zero;
        }

        // Send hash-start signal
        public virtual void SignalHashStart()
        {
            throw new Exception("Should never be here");
        }

        // hash data
        public virtual void SignalHashData(byte[] data)
        {
            throw new Exception("Should never be here");
        }

        // Send hash-end signal
        public virtual void SignalHashEnd()
        {
            throw new Exception("Should never be here");
        }

        // Send new Endorsement Primary Seed to TPM simulator
        public virtual void TestFailureMode()
        {
            throw new Exception("Should never be here");
        }

        // Return whether cancel is implemented
        public virtual bool ImplementsCancel()
        {
            return false;
        }

        // Send cancel-on signal
        public virtual void SignalCancelOn()
        {
            throw new Exception("Should never be here");
        }

        //  Send cancel-off signal
        public virtual void SignalCancelOff()
        {
            throw new Exception("Should never be here");
        }

        // Switch NV On
        public virtual void SignalNvOn()
        {
            throw new Exception("Should never be here");
        }

        // Switch NV Off
        public virtual void SignalNvOff()
        {
            throw new Exception("Should never be here");
        }

        // Switch key caching On
        public virtual void SignalKeyCacheOn()
        {
        }

        // Switch key caching Off
        public virtual void SignalKeyCacheOff()
        {
        }

        public virtual byte[] GetLockoutAuth()
        {
            return new byte[0];
        }

        public virtual byte[] GetOwnerAuth()
        {
            return new byte[0];
        }

        public virtual byte[] GetEndorsementAuth()
        {
            return new byte[0];
        }
    }
}

/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.Diagnostics;
using System.Threading;
using System.Threading.Tasks;

#if !TSS_NO_TCP
using System.Net;
using System.Net.Sockets;
#endif

// TPM Commands.  All commands acknowledge processing by returning a UINT32 == 0.
// RemoteHandshake also returns information about the target TPM, and SendCommand
// returns the TPM response BYTE array.

namespace Tpm2Lib
{
#if !TSS_NO_TCP
    public enum TcpTpmCommands
    {
        SignalPowerOn = 1,
        SignalPowerOff = 2,
        SignalPPOn = 3,
        SignalPPOff = 4,
        SignalHashStart = 5,
        SignalHashData = 6,
        SignalHashEnd = 7,
        SendCommand = 8,
        SignalCancelOn = 9,
        SignalCancelOff = 10,
        SignalNvOn = 11,
        SignalNvOff = 12,
        SignalKeyCacheOn = 13,
        SignalKeyCacheOff = 14,
        RemoteHandshake = 15,
        //SetAlternativeResult = 16,    // Not used since 1.38h
        SessionEnd = 20,
        Stop = 21,

        TestFailureMode = 30
    }

    public enum TpmEndPointInfo : uint
    {
        // Platform hierarchy is enabled, and hardware platform functionality (such
        // as SignalHashStart/Data/End) is available.
        PlatformAvailable = 0x01,

        // The device is TPM Resource Manager (TRM), rather than a raw TPM.
        // This means context management commands are unavailable, and the handle values
        // returned to the client are virtualized.
        UsesTbs = 0x02,

        // The TRM is in raw mode (i.e. no actual resourse virtualization is performed).
        InRawMode = 0x04,

        // Phisical presence signals (SignalPPOn/Off) are supported.
        SupportsPP = 0x08,

        // Valid only with PlatformAvailable set.
        // System and TPM power control signals (SignalPowerOn/Off) are not supported.
        NoPowerCtl = 0x10,

        // Valid only with tpmPlatformAvailable set.
        // TPM locality cannot be changed.
        NoLocalityCtl = 0x20,

        // Valid only with tpmPlatformAvailable set.
        // NV control signals (SignalNvOn/Off) are not supported.
        NoNvCtl = 0x40
    }
#endif //!TSS_NO_TCP


    public interface ICommandCallbacks
    {
        void PreCallback(byte[] command, out byte[] modifedCommand);

        void PostCallback(byte[] command, byte[] response);
    }

    /// <summary>
    /// TpmPassThroughDevice does roughly what it says, however you can install callback
    /// delegates that are invoked before and after commands are send to the underlying
    /// device. It is typically instantiated on top of a TPM device so that test/command
    /// statistics can be collected.
    /// </summary>
    public sealed class TpmPassThroughDevice : Tpm2Device
    {
        private readonly Tpm2Device Device;

        public TpmPassThroughDevice(Tpm2Device underlyingDevice)
        {
            Device = underlyingDevice;
        }

        public void SetCommandCallbacks(ICommandCallbacks callbacks)
        {
            CommandCallbacks = callbacks;
        }

        public Tpm2Device GetUnderlyingDevice()
        {
            return Device;
        }

        public override void DispatchCommand(CommandModifier active, byte[] inBuf, out byte[] outBuf)
        {
            if (CommandCallbacks != null)
            {
                byte[] tempInBuf;
                CommandCallbacks.PreCallback(inBuf, out tempInBuf);
                if (tempInBuf != null)
                {
                    inBuf = tempInBuf;
                }
            }
            Device.DispatchCommand(active, inBuf, out outBuf);
            if (CommandCallbacks != null)
            {
                CommandCallbacks.PostCallback(inBuf, outBuf);
            }
        }

        public override void Connect()
        {
            Device.Connect();
        }

        public override void Close()
        {
            Device.Close();
        }

        public override void PowerCycle()
        {
            PowerCycledDirtyBit = true;
            Device.PowerCycle();
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                Device.Dispose();
            }
        }

        public override bool PlatformAvailable()
        {
            return Device.PlatformAvailable();
        }

        public override bool PowerCtlAvailable()
        {
            return Device.PowerCtlAvailable();
        }

        public override bool LocalityCtlAvailable()
        {
            return Device.LocalityCtlAvailable();
        }

        public override bool NvCtlAvailable()
        {
            return Device.NvCtlAvailable();
        }

        public override bool UsesTbs()
        {
            return Device.UsesTbs();
        }

        public override bool HasRM()
        {
            return Device.HasRM();
        }

        public new bool NeedsHMAC
        {
            get
            {
                return Device.NeedsHMAC;
            }
            set
            {
                Device.NeedsHMAC = value;
            }
        }

        public override bool ImplementsPhysicalPresence()
        {
            return Device.ImplementsPhysicalPresence();
        }

        public override void AssertPhysicalPresence(bool assertPhysicalPresence)
        {
            Device.AssertPhysicalPresence(assertPhysicalPresence);
        }

        public override void SignalHashStart()
        {
            Device.SignalHashStart();
        }

        public override void SignalHashData(byte[] data)
        {
            Device.SignalHashData(data);
        }

        public override void SignalHashEnd()
        {
            Device.SignalHashEnd();
        }

        public override void TestFailureMode()
        {
            Device.TestFailureMode();
        }

        public override UIntPtr GetHandle(UIntPtr h)
        {
            return Device.GetHandle(h);
        }

        public override void CancelContext()
        {
            Device.CancelContext();
        }

        public override bool ImplementsCancel()
        {
            return Device.ImplementsCancel();
        }

        public override void SignalCancelOn()
        {
            Device.SignalCancelOn();
        }

        public override void SignalCancelOff()
        {
            Device.SignalCancelOff();
        }

        public override void SignalNvOn()
        {
            Device.SignalNvOn();
        }

        public override void SignalNvOff()
        {
            Device.SignalNvOff();
        }

        public override void SignalKeyCacheOn()
        {
            Device.SignalKeyCacheOn();
        }

        public override void SignalKeyCacheOff()
        {
            Device.SignalKeyCacheOff();
        }

        ICommandCallbacks CommandCallbacks = null;

        private bool PowerCycledDirtyBit;

        /// <summary>
        /// Returns whether a power-cycle has occurred since the last call to GetPowerCycleDirtyBit().
        /// </summary>
        /// <returns></returns>
        public bool GetPowerCycleDirtyBit()
        {
            bool hasPowerCycled = PowerCycledDirtyBit;
            PowerCycledDirtyBit = false;
            return hasPowerCycled;
        }

        public override byte[] GetLockoutAuth()
        {
            return Device.GetLockoutAuth();
        }

        public override byte[] GetOwnerAuth()
        {
            return Device.GetOwnerAuth();
        }

        public override byte[] GetEndorsementAuth()
        {
            return Device.GetEndorsementAuth();
        }
    } // class TpmPassThroughDevice

#if !TSS_NO_TCP
    /// <summary>
    /// The TcpTpmDevice connects to a TPM on the end of a pair of TCP sockets 
    /// (one for the main commands, and one for platform commands, consecutive ports).  
    /// </summary>
    public sealed class TcpTpmDevice : Tpm2Device
    {
        private const int ClientVersion = 1;

        private NetworkStream   CommandStream = null,
                                PlatformStream = null;
        private TcpClient       CommandClient = null,
                                PlatformClient = null;
        private readonly string ServerName;
        private readonly int CommandServerPort;
        private readonly int PlatformServerPort;
        private int SocketTimeout = -1;
        // Combination of TpmEndPointInfo flags
        private int TpmEndPtInfo;
        private readonly bool StopTpm;
        private volatile bool CancelSignalled;
        private readonly bool LinuxTrm;
        private bool OldTrm;


        /// <summary>
        /// Set the remote host (domain name or IPv4-dotted name) and listening ports.
        /// The tester will attempt to connect to the command port on serverPort and
        /// the platform interface port on serverPort + 1.
        /// </summary>
        /// <param name="serverName"></param>
        /// <param name="serverPort"></param>
        /// <param name="stopTpm"></param>
        public TcpTpmDevice(string serverName, int serverPort,
                            bool stopTpm = false, bool linuxTrm = false)
        {
            ServerName = serverName;
            CommandServerPort = serverPort;
            PlatformServerPort = serverPort + 1;
            StopTpm = stopTpm;
            LinuxTrm = linuxTrm;
            OldTrm = true;  // Start checking with the old version (if necessary at all)
        }

        public override void Connect()
        {
            ConnectWorker(ServerName, CommandServerPort, out CommandStream, out CommandClient);
            if (!LinuxTrm)
                ConnectWorker(ServerName, PlatformServerPort, out PlatformStream, out PlatformClient);
            if (SocketTimeout > 0)
                SetSocketTimeout(SocketTimeout);

            if (LinuxTrm)
            {
                var cmdGetRandom = new byte[]{
                        0x80, 0x01,             // TPM_ST_NO_SESSIONS
                        0, 0, 0, 0x0C,          // length
                        0, 0, 0x01, 0x7B,       // TPM_CC_GetRandom
                        0, 0x08                 // Command parameter - num random bytes to generate
                };

                byte[] resp = null;
                try
                {
                    DispatchCommand(new CommandModifier(), cmdGetRandom, out resp);
                }
                catch (Exception)
                {
                }
                if (resp == null || resp.Length != 20)
                {
                    Close();
                    if (OldTrm)
                    {
                        OldTrm = false;
                        Connect();
                    }
                    else
                        throw new Exception("Unknown user mode TRM protocol version");
                }
                else
                    TpmEndPtInfo = (int)TpmEndPointInfo.UsesTbs;
            }
            else
            {
                // Handshake
                WriteInt(CommandStream, (int)TcpTpmCommands.RemoteHandshake);
                WriteInt(CommandStream, ClientVersion);

                int endPointVersion = ReadInt(CommandStream);
                if (endPointVersion == 0)
                {
                    throw new Exception("Incompatible TPM/proxy (version 0, expected 1 or higher)");
                }
                TpmEndPtInfo = ReadInt(CommandStream);
                GetAck(CommandStream, "Connect");
            }
        }

        public override void Close()
        {
            var cmd = (int)(StopTpm ? TcpTpmCommands.Stop : TcpTpmCommands.SessionEnd);
            if (CommandStream != null)
            {
                try
                {
                    WriteInt(CommandStream, cmd);
                }
                catch (Exception) {}
                CommandStream.Flush();
                CommandStream.Dispose();
                CommandStream = null;
            }
            if (PlatformStream != null)
            {
                try
                {
                    WriteInt(PlatformStream, cmd);
                }
                catch (Exception) { }
                PlatformStream.Flush();
                PlatformStream.Dispose();
                PlatformStream = null;
            }
        }

        private IPAddress GetIPAddressFromHost(string hostName)
        {
            Task<IPHostEntry> hostEntry = Dns.GetHostEntryAsync(hostName);

            hostEntry.Wait();

            if (hostEntry.Result.AddressList.Length > 0)
            {
                return hostEntry.Result.AddressList[0];
            }
            else
            {
                throw new Exception(string.Format("could not locate hostName: {0}", hostName));
            }
        }

        private void ConnectWorker(string hostName, int port, out NetworkStream theStream, out TcpClient theClient)
        {
            IPAddress simulatorAddress;
            theClient = new TcpClient();
            // first try to interpret as a dotted DNS name
            bool parsedOk = IPAddress.TryParse(hostName, out simulatorAddress);
            Task ipConTask = theClient.ConnectAsync(parsedOk ? simulatorAddress : GetIPAddressFromHost(hostName), port);
            bool res = true;
            if (SocketTimeout != -1)
                res = ipConTask.Wait(SocketTimeout * 1000);
            else
                ipConTask.Wait();
            if (!res)
                throw (new TssException("Failed to establish socket connection"));
            theClient.NoDelay = true;
            theStream = theClient.GetStream();
            return;
        }

        public void SetSocketTimeout(int seconds)
        {
            int t = seconds * 1000;
            if (CommandClient != null)
            {
                CommandClient.ReceiveTimeout = t;
                CommandClient.SendTimeout = t;
            }
            if (PlatformClient != null)
            {
                PlatformClient.SendTimeout = t;
                PlatformClient.ReceiveTimeout = t;
            }
            SocketTimeout = seconds;
        }

        public override void PowerCycle()
        {
            PowerOff();
            PowerOn();
        }

        public void PowerOff()
        {
            SendCmdAndGetAck(PlatformStream, TcpTpmCommands.SignalPowerOff);
            SendCmdAndGetAck(PlatformStream, TcpTpmCommands.SignalNvOff);
        }

        public void PowerOn()
        {
            SendCmdAndGetAck(PlatformStream, TcpTpmCommands.SignalPowerOn);
            SendCmdAndGetAck(PlatformStream, TcpTpmCommands.SignalNvOn);
        }

        public override bool PlatformAvailable()
        {
            return (TpmEndPtInfo & (int)Tpm2Lib.TpmEndPointInfo.PlatformAvailable) != 0;
        }

        public override bool PowerCtlAvailable()
        {
            return PlatformAvailable() &&
                   (TpmEndPtInfo & (int)Tpm2Lib.TpmEndPointInfo.NoPowerCtl) == 0;
        }

        public override bool LocalityCtlAvailable()
        {
            return PlatformAvailable() &&
                   (TpmEndPtInfo & (int)Tpm2Lib.TpmEndPointInfo.NoLocalityCtl) == 0;
        }

        public override bool NvCtlAvailable()
        {
            return PlatformAvailable() &&
                   (TpmEndPtInfo & (int)Tpm2Lib.TpmEndPointInfo.NoNvCtl) == 0;
        }

        public override bool HasRM()
        {
            return _HasRM || (TpmEndPtInfo & (int)Tpm2Lib.TpmEndPointInfo.InRawMode) == 0;
        }

        public override bool ImplementsPhysicalPresence()
        {
            return (TpmEndPtInfo & (int)Tpm2Lib.TpmEndPointInfo.SupportsPP) != 0;
        }

        public override void AssertPhysicalPresence(bool assertPhysicalPresence)
        {
            SendCmdAndGetAck(PlatformStream, assertPhysicalPresence ? TcpTpmCommands.SignalPPOn
                                                                    : TcpTpmCommands.SignalPPOff);
        }

        public override bool UsesTbs()
        {
            return (TpmEndPtInfo & (int)Tpm2Lib.TpmEndPointInfo.UsesTbs) != 0;
        }

        public override bool ImplementsCancel()
        {
            return !LinuxTrm;
        }

        public override void SignalCancelOn()
        {
            CancelSignalled = true;
            SendCmdAndGetAck(PlatformStream, TcpTpmCommands.SignalCancelOn);
        }

        public override void SignalCancelOff()
        {
            SendCmdAndGetAck(PlatformStream, TcpTpmCommands.SignalCancelOff);
            CancelSignalled = false;
        }

        public override void SignalNvOn()
        {
            SendCmdAndGetAck(PlatformStream, TcpTpmCommands.SignalNvOn);
        }

        public override void SignalNvOff()
        {
            SendCmdAndGetAck(PlatformStream, TcpTpmCommands.SignalNvOff);
        }

        public override void SignalKeyCacheOn()
        {
            SendCmdAndGetAck(PlatformStream, TcpTpmCommands.SignalKeyCacheOn);
        }

        public override void SignalKeyCacheOff()
        {
            SendCmdAndGetAck(PlatformStream, TcpTpmCommands.SignalKeyCacheOff);
        }

        private void UndoCancelContext()
        {
            // Double check to avoid unnecessary locking
            if (CancelSignalled)
            {
                lock (this)
                {
                    if (CancelSignalled)
                    {
                        SignalCancelOff();
                        CancelSignalled = false;
                    }
                }
            }
        }

        public override void DispatchCommand(CommandModifier active, byte[] inBuf, out byte[] outBuf)
        {
            outBuf = new byte[]{0x80, 0x01,             // TPM_ST_NO_SESSIONS
                                0, 0, 0, 0x0A,          // length
                                0x40, 0x28, 0x00, 0x10  // TSS_DISPATCH_FAILED
                                };
            if (CommandStream == null)
                return;
            if (LinuxTrm)
            {
                if (Globs.NetToHost4U(Globs.CopyData(inBuf, 6, 4)) == (uint)TpmCc.Startup)
                {
                    outBuf[6] = outBuf[7] = 0; outBuf[8] = 0x01; outBuf[9] = 0; // TPM_RC_INITIALIZE
                    return;
                }
            }
            UndoCancelContext();
            var b = new ByteBuf();
            b.Append(Globs.HostToNet((int)TcpTpmCommands.SendCommand));
            b.Append(new[] { active.ActiveLocality });
            if (LinuxTrm && OldTrm)
            {
                b.Append(new byte [] {0, 1});
            }
            b.Append(Globs.HostToNet(inBuf.Length));
            b.Append(inBuf);
            Write(CommandStream, b.GetBuffer());

            outBuf = ReadVarArray(CommandStream);

            GetAck(CommandStream, "DispatchCommand");
        }

        public override void CancelContext()
        {
            lock (this)
            {
                SignalCancelOn();
            }
#if WINDOWS_UWP
            Task.Delay(10).Wait();
#else              
            Thread.Sleep(10);
#endif
            lock (this)
            {
                SignalCancelOff();
            }
        }

        public override void SignalHashStart()
        {
            SendCmdAndGetAck(CommandStream, TcpTpmCommands.SignalHashStart);
        }

        public override void SignalHashData(byte[] data)
        {
            SendVarArrayCmdAndGetAck(CommandStream, TcpTpmCommands.SignalHashData, data);
        }

        public override void SignalHashEnd()
        {
            SendCmdAndGetAck(CommandStream, TcpTpmCommands.SignalHashEnd);
        }

        public override void TestFailureMode()
        {
            SendCmdAndGetAck(PlatformStream, TcpTpmCommands.TestFailureMode);
        }

        protected sealed override void Dispose(bool disposing)
        {
            if (disposing)
                Close();
            base.Dispose(disposing);
        }

        private void GetAck(NetworkStream stream, string operation = "")
        {
            int endTag = ReadInt(stream);
            if (endTag != 0)
            {
                if (endTag == 1)
                {
                    throw new Exception("Operation " + operation + " failed");
                }
                // ReSharper disable once RedundantIfElseBlock
                else
                {
                    throw new Exception("Bad end tag " + endTag + " for operation " + operation);
                }
            }
        }

        /// <summary>
        /// Write exactly x[] bytes.
        /// </summary>
        /// <param name="stream"></param>
        /// <param name="x"></param>
        private void Write(NetworkStream stream, byte[] x)
        {
            stream.Write(x, 0, x.Length);
            NotifyWorker(CommsSort.ByteSent, stream, x);
        }

        /// <summary>
        /// Read exactly numBytes bytes.
        /// </summary>
        /// <param name="stream"></param>
        /// <param name="numBytes"></param>
        /// <returns></returns>
        private byte[] Read(NetworkStream stream, int numBytes)
        {
            var res = Globs.GetZeroBytes(numBytes);
            int numRead = 0;
            while (numRead < numBytes)
            {
                numRead += stream.Read(res, numRead, numBytes - numRead);
            }
            NotifyWorker(CommsSort.ByteReceived, stream, res);
            return res;
        }

        /// <summary>
        /// Simple command ping-pong.  The TCP interface acknowledges simple command
        /// completion with a UINT32=0;
        /// </summary>
        /// <param name="stream"></param>
        /// <param name="cmd"></param>
        private void SendCmdAndGetAck(NetworkStream stream, TcpTpmCommands cmd)
        {
            if (stream == null)
                return;
            WriteInt(stream, (int)cmd);
            GetAck(stream, cmd.ToString());
        }

        private void WriteInt(NetworkStream stream, int x)
        {
            Write(stream, Globs.HostToNet(x));
        }

        private int ReadInt(NetworkStream stream)
        {
            int val = Globs.NetToHost4(Read(stream, 4));
            return val;
        }

        /// <summary>
        /// Read a length-prepended array
        /// </summary>
        /// <returns></returns>
        private byte[] ReadVarArray(NetworkStream stream)
        {
            int bufLen = Globs.NetToHost4(Read(stream, 4));
            byte[] buf = Read(stream, bufLen);
            return buf;
        }

        // ReSharper disable once UnusedMember.Local
        private void WriteVarArray(NetworkStream stream, byte[] buf)
        {
            Write(stream, Globs.HostToNet(buf.Length));
            Write(stream, buf);
        }

        private void SendVarArrayCmdAndGetAck(NetworkStream stream, TcpTpmCommands cmd, byte[] buf)
        {
            var b = new ByteBuf();
            b.Append(Globs.HostToNet((int)cmd));
            b.Append(Globs.HostToNet(buf.Length));
            b.Append(buf);
            Write(stream, b.GetBuffer());

            GetAck(stream, cmd.ToString());
        }

        // ReSharper disable once UnusedMember.Local
        private void Error(string errorMessage)
        {
#if !WINDOWS_UWP
            Console.Error.WriteLine(errorMessage);
#endif
            Debug.WriteLine(errorMessage);
            throw new Exception(errorMessage);
        }

        public enum CommsSort
        {
            Undefined,
            ByteSent,
            ByteReceived,
        }

        public enum Channel
        {
            Undefined,
            Platform,
            Data
        }

        public delegate void NotifyData(CommsSort sort, Channel channel, byte[] inOrOutData);

        private NotifyData Notifier;
        private readonly Object CallbackLock = new Object();

        /// <summary>
        /// Set a communications notification callback, or null to remove the callback.
        /// The registered delegate receives all communications on the TCP channels (both 
        /// on the platform and on the data channel).
        /// Note:  Communications on the platform and data channels can be asynchronous on 
        /// more than one thread.  The callback must disentangle if this might occur
        /// </summary>
        /// <param name="notifier"></param>
        public void SetTransportCallback(NotifyData notifier)
        {
            lock (CallbackLock)
            {
                Notifier = notifier;
            }
        }

        private void NotifyWorker(CommsSort sort, NetworkStream stream, byte[] data)
        {
            lock (CallbackLock)
            {
                Channel c = (stream == PlatformStream) ? Channel.Platform : Channel.Data;
                if (Notifier != null)
                {
                    Notifier(sort, c, data);
                }
            }
        }
    } // class TcpTpmDevice
#endif //!TSS_NO_TCP
} // namespace Tpm2Lib

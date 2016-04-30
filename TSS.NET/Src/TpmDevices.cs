
/*++

Copyright (c) 2010-2015 Microsoft Corporation
Microsoft Confidential

*/
using System;
using System.Diagnostics;
using System.Threading;
using System.Threading.Tasks;
using System.Runtime.InteropServices;
using System.Diagnostics.CodeAnalysis;
using System.ComponentModel;

#if !TSS_NO_TCP
using System.Net;
using System.Net.Sockets;
using System.Net.NetworkInformation;
#endif

// TPM Commands.  All commands acknowledge processing by returning a UINT32 == 0.
// RemoteHandshake also returns information about the target TPM, and SendCommand
// returns the TPM response BYTE array.

namespace Tpm2Lib
{
#if !TSS_NO_TCP
    internal enum TcpTpmCommands
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
        SetAlternativeResult = 16,
        SessionEnd = 20,
        Stop = 21,

        TestFailureMode = 30
    }

    // ReSharper disable once EnumUnderlyingTypeIsInt
    internal enum TpmEndPointInfo : int
    {
        PlatformAvailable = 0x01,
        UsesTbs = 0x02,
        InRawMode = 0x04,
        SupportsPP = 0x08
    }
#endif //!TSS_NO_TCP


    public interface ICommandCallbacks
    {
        void PreCallback(byte[] command, out byte[] modifedCommand);

        void PostCallback(byte[] command, byte[] response);
    }

    /// <summary>
    /// TpmPassThroughDevice does roughly what it says, however you can install callback delegates
    /// that are invoked before and after commands are send to the underlying device.  It is typically
    /// instantiated on top of a TPM device so that test/command statistics can be collected.
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

        public override void SetAlternativeResult(Results r)
        {
            Device.SetAlternativeResult(r);
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

    }

#if !TSS_NO_TCP
    /// <summary>
    /// The TcpTpmDevice connects to a TPM on the end of a pair of TCP sockets 
    /// (one for the main commands, and one for platform commands, consecutive ports).  
    /// </summary>
    public sealed class TcpTpmDevice : Tpm2Device
    {
        private const int ClientVersion = 1;

        private NetworkStream CommandStream, PlatformStream;
        private TcpClient CommandClient, PlatformClient;
        private readonly string ServerName;
        private readonly int CommandServerPort;
        private readonly int PlatformServerPort;
        private int SocketTimeout = -1;
        // Combination of TpmEndPointInfo flags
        private int TpmEndPointInfo;
        private readonly bool StopTpm;
        private volatile bool CancelSignalled;

        /// <summary>
        /// Set the remote host (domain name or IPv4-dotted name) and listening ports.  The tester will attempt to connect
        /// to the command port on serverPort and the platform interface port on serverPort + 1.
        /// </summary>
        /// <param name="serverName"></param>
        /// <param name="serverPort"></param>
        /// <param name="stopTpm"></param>
        public TcpTpmDevice(string serverName, int serverPort, bool stopTpm = false)
        {
            ServerName = serverName;
            CommandServerPort = serverPort;
            PlatformServerPort = serverPort + 1;
            StopTpm = stopTpm;
        }

        public override void Connect()
        {
            ConnectWorker(ServerName, CommandServerPort, out CommandStream, out CommandClient);
            ConnectWorker(ServerName, PlatformServerPort, out PlatformStream, out PlatformClient);
            if (SocketTimeout > 0)
            {
                SetSocketTimeout(SocketTimeout);
            }

            // Handshaking
            WriteInt(CommandStream, (int)TcpTpmCommands.RemoteHandshake);
            WriteInt(CommandStream, ClientVersion);

            int endPointVersion = ReadInt(CommandStream);
            if (endPointVersion == 0)
            {
                throw new Exception("Incompatible TPM/proxy (version 0, expected 1 or higher)");
            }
            TpmEndPointInfo = ReadInt(CommandStream);
            GetAck(CommandStream, "Connect");
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
            // first try to interpret as a dotted DNS name
            bool parsedOk = IPAddress.TryParse(hostName, out simulatorAddress);
            if (parsedOk)
            {
                theClient = new TcpClient();
                Task ipConTask = theClient.ConnectAsync(simulatorAddress, port);
                ipConTask.Wait();
                theClient.NoDelay = true;
                theStream = theClient.GetStream();
                return;
            }
            // else we try the the DNS hostname
            theClient = new TcpClient();
            Task dnsConTask = theClient.ConnectAsync(GetIPAddressFromHost(hostName), port);
            dnsConTask.Wait();
            theClient.NoDelay = true;
            theStream = theClient.GetStream();
        }

        public void SetSocketTimeout(int seconds)
        {
            int t = seconds * 1000;
            CommandClient.ReceiveTimeout = t;
            CommandClient.SendTimeout = t;
            PlatformClient.SendTimeout = t;
            PlatformClient.ReceiveTimeout = t;
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
            return (TpmEndPointInfo & (int)Tpm2Lib.TpmEndPointInfo.PlatformAvailable) != 0;
        }

        public override bool HasRM()
        {
            return _HasRM || (TpmEndPointInfo & (int)Tpm2Lib.TpmEndPointInfo.InRawMode) == 0;
        }

        public override bool ImplementsPhysicalPresence()
        {
            return (TpmEndPointInfo & (int)Tpm2Lib.TpmEndPointInfo.SupportsPP) != 0;
        }

        public override void AssertPhysicalPresence(bool assertPhysicalPresence)
        {
            // ReSharper disable once ConvertIfStatementToConditionalTernaryExpression
            if (assertPhysicalPresence)
            {
                SendCmdAndGetAck(PlatformStream, TcpTpmCommands.SignalPPOn);
            }
            else
            {
                SendCmdAndGetAck(PlatformStream, TcpTpmCommands.SignalPPOff);
            }
        }

        public override bool UsesTbs()
        {
            return (TpmEndPointInfo & (int)Tpm2Lib.TpmEndPointInfo.UsesTbs) != 0;
        }

        public override bool ImplementsCancel()
        {
            return true;
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

        public override void SetAlternativeResult(Results r)
        {
            WriteInt(CommandStream, (int)TcpTpmCommands.SetAlternativeResult);
            WriteInt(CommandStream, (int)r);
            GetAck(CommandStream);
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
            UndoCancelContext();
            var b = new ByteBuf();
            b.Append(Globs.HostToNet((int)TcpTpmCommands.SendCommand));
            b.Append(new[] { active.ActiveLocality });
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
            Thread.Sleep(10);
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
            {
                var cmd = (int)(StopTpm ? TcpTpmCommands.Stop : TcpTpmCommands.SessionEnd);
                if (CommandStream != null)
                {
                    WriteInt(CommandStream, cmd);
                    CommandStream.Flush();
                    CommandStream.Dispose();
                }
                if (PlatformStream != null)
                {
                    WriteInt(PlatformStream, cmd);
                    PlatformStream.Flush();
                    PlatformStream.Dispose();
                }
            }
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
            var res = new byte[numBytes];
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
            Console.Error.WriteLine(errorMessage);
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


    public sealed class TbsDevice : Tpm2Device
    {
        private UIntPtr TbsHandle;
        private UIntPtr OriginalHandle;
        private TbsWrapper.TBS_RESULT AlternativeResult = TbsWrapper.TBS_RESULT.TBS_SUCCESS;

        /// <summary>
        /// Default constructor.
        /// </summary>
        public TbsDevice()
        {
            NeedsHMAC = false;
        }

        public override UIntPtr GetHandle(UIntPtr h)
        {
            if (h != UIntPtr.Zero)
            {
                TbsHandle = h;
            }
            return TbsHandle;
        }

        public override void SetAlternativeResult(Results r)
        {
            switch (r)
            {
                case Results.RESULT_BAD_PARAMETER:
                    AlternativeResult = TbsWrapper.TBS_RESULT.TBS_E_BAD_PARAMETER;
                    break;
                case Results.RESULT_COMMAND_CANCELED:
                    AlternativeResult = TbsWrapper.TBS_RESULT.TBS_E_COMMAND_CANCELED;
                    break;
                case Results.RESULT_INTERNAL_ERROR:
                    AlternativeResult = TbsWrapper.TBS_RESULT.TBS_E_INTERNAL_ERROR;
                    break;
                case Results.RESULT_SUCCESS:
                    AlternativeResult = TbsWrapper.TBS_RESULT.TBS_SUCCESS;
                    break;
                case Results.RESULT_COMMAND_BLOCKED:
                    AlternativeResult = TbsWrapper.TBS_RESULT.TBS_E_BLOCKED;
                    break;
                default:
                    throw new Exception("Bad Value in SetAlternativeResult: " + r.ToString());
            }
        }

        public override void Connect()
        {
            TbsWrapper.TBS_CONTEXT_PARAMS contextParams;

            UIntPtr tbsContext = UIntPtr.Zero;
            contextParams.Version = TbsWrapper.TBS_CONTEXT_VERSION.TWO;
            contextParams.Flags = TbsWrapper.TBS_CONTEXT_CREATE_FLAGS.IncludeTpm20;
            TbsWrapper.TBS_RESULT result = TbsWrapper.NativeMethods.Tbsi_Context_Create(ref contextParams, ref tbsContext);

#if !NETFX_CORE
            Console.WriteLine(Globs.GetResourceString("TbsHandle:") + tbsContext.ToUInt32());
#endif

            if (result != TbsWrapper.TBS_RESULT.TBS_SUCCESS)
            {
                throw new Exception("Can't create TBS context: Error=0x" + Convert.ToString((uint)result, 16));
            }

            TbsHandle = tbsContext;
            OriginalHandle = tbsContext;
        }

        public override void PowerCycle()
        {
            throw new Exception("TbsDevice does not implement PowerCycle()");
        }

        public override void AssertPhysicalPresence(bool assertPhysicalPresence)
        {
            throw new NotImplementedException("Device does not support PP");
        }

        public override bool PlatformAvailable()
        {
            return false;
        }

        public override bool UsesTbs()
        {
            return true;
        }

        public override bool HasRM()
        {
            // TODO: detect raw mode during class initialization
            return true;
        }

        public override void DispatchCommand(CommandModifier active, byte[] inBuf, out byte[] outBuf)
        {
            if (TbsHandle == UIntPtr.Zero)
            {
                throw new Exception("TBS context not created.");
            }

            var resultBuf = new byte[4096];
            uint bytesReturned = (uint)resultBuf.Length;
            TbsWrapper.TBS_RESULT result = TbsWrapper.NativeMethods.Tbsip_Submit_Command(TbsHandle,
                                                                                         (TbsWrapper.TBS_COMMAND_LOCALITY)
                                                                                         active.ActiveLocality,
                                                                                         active.ActivePriority,
                                                                                         inBuf,
                                                                                         (uint)inBuf.Length,
                                                                                         resultBuf,
                                                                                         ref bytesReturned);
            if (result != AlternativeResult && result != TbsWrapper.TBS_RESULT.TBS_SUCCESS)
            {
                string errMsg = new Win32Exception((int)result).Message;
                ProcessError("TBS error " + result.ToString("X") + ": " + errMsg, out outBuf);
                return;
            }
            if (bytesReturned == 0)
            {
                ProcessError(Globs.GetResourceString("SubmitError2"), out outBuf);
                return;
            }

            outBuf = new byte[bytesReturned];
            Array.Copy(resultBuf, outBuf, (int)bytesReturned);
        }

        protected override void Dispose(bool disposing)
        {
            if (OriginalHandle != UIntPtr.Zero)
            {
                TbsWrapper.NativeMethods.Tbsip_Context_Close(OriginalHandle);
                OriginalHandle = UIntPtr.Zero;
            }
        }

        public override bool ImplementsCancel()
        {
            return true;
        }

        public override void SignalCancelOn()
        {
            CancelContext();
        }

        public override void SignalCancelOff()
        {
        }

        public override void CancelContext()
        {
            TbsWrapper.TBS_RESULT result = TbsWrapper.NativeMethods.Tbsip_Cancel_Commands(TbsHandle);
            if (result != TbsWrapper.TBS_RESULT.TBS_SUCCESS)
            {
#if !NETFX_CORE
                Console.Error.WriteLine("TbsStubs.Tbsip_Cancel_Command error 0x{0:x}", result);
#endif
                throw new Exception("Tbsip_Cancel_Command() failed -- 0x" + Convert.ToString((uint)result, 16));
            }
        }

        private void ProcessError(string message, out byte[] byteBuf)
        {
#if !NETFX_CORE
            Console.Error.WriteLine(message);
            Console.Error.WriteLine("This will be processed as a TpmRc.NotUsed error");
#endif
            byteBuf = Marshaller.GetTpmRepresentation(new Object[] {
                TpmSt.NoSessions,
                (uint)10,
                TpmRc.NotUsed
            });
        }
    } // class TbsDevice

    public class TbsPublicStubs
    {
        // Note that the Windows API does not define zero
        [SuppressMessageAttribute("Microsoft.Design", "CA1008:EnumsShouldHaveZeroValue")]
        public enum TBS_COMMAND_PRIORITY : uint
        {
            LOW = 100,
            NORMAL = 200,
            HIGH = 300,
            SYSTEM = 400,
            MAX = 0x80000000
        }

    } // class TbsPublicStubs

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
                Tbsip_Submit_Command(
                UIntPtr Context,
                TBS_COMMAND_LOCALITY Locality,
                TbsPublicStubs.TBS_COMMAND_PRIORITY Priority,
                [System.Runtime.InteropServices.MarshalAs(UnmanagedType.LPArray, SizeParamIndex = 4), In]
                 byte[] InBuffer,
                uint InBufferSize,
                [System.Runtime.InteropServices.MarshalAs(UnmanagedType.LPArray, SizeParamIndex = 6), Out]
                 byte[] OutBuffer,
                ref uint OutBufferSize);

            [DllImport("tbs.dll", CharSet = CharSet.Unicode)]
            internal static extern TBS_RESULT
                Tbsip_Cancel_Commands(
                UIntPtr Context);
        }

        public enum TBS_RESULT : uint
        {
            TBS_SUCCESS = 0,
            TBS_E_BLOCKED = 0x80280400,
            TBS_E_INTERNAL_ERROR = 0x80284001,
            TBS_E_BAD_PARAMETER = 0x80284002,
            TBS_E_COMMAND_CANCELED = 0x8028400D
        }

        [StructLayout(LayoutKind.Sequential)]
        public struct TBS_CONTEXT_PARAMS
        {
            public TBS_CONTEXT_VERSION Version;
            public TBS_CONTEXT_CREATE_FLAGS Flags;
        }

        public enum TBS_COMMAND_LOCALITY : uint
        {
            ZERO = 0,
            ONE = 1,
            TWO = 2,
            THREE = 3,
            FOUR = 4
        }

        public enum TBS_CONTEXT_VERSION : uint
        {
            ONE = 1,
            TWO = 2
        }

        public enum TBS_TPM_VERSION : uint
        {
            Invalid = 0,
            V1_2 = 1,
            V2 = 2
        }

        public enum TBS_CONTEXT_CREATE_FLAGS : uint
        {
            RequestRaw = 0x00000001,
            IncludeTpm12 = 0x00000002,
            IncludeTpm20 = 0x00000004,
        }
    } // class TbsWrapper

    internal class TpmDllWrapper
    {
        public class NativeMethods
        {
            // helper to find the TPM
            [DllImport("kernel32.dll", CharSet = CharSet.Unicode, SetLastError = true)]
            public static extern bool SetDllDirectory(string lpPathName);

#region TpmExports

            [DllImport("tpm.dll", CallingConvention = CallingConvention.Cdecl)]
            public static extern void _TPM_Init();

            [DllImport("tpm.dll", CallingConvention = CallingConvention.Cdecl)]
            public static extern void TPM_Manufacture();

            [DllImport("tpm.dll", CallingConvention = CallingConvention.Cdecl)]
            public static extern void ExecuteCommand(
                uint requestSize,
                [In] byte[] request,
                ref uint responseSize,
                ref IntPtr response);

            [DllImport("tpm.dll", CallingConvention = CallingConvention.Cdecl)]
            public static extern void Signal_Hash_Start();

            [DllImport("tpm.dll", CallingConvention = CallingConvention.Cdecl)]
            public static extern void Signal_Hash_Data(uint size, byte[] buffer);

            [DllImport("tpm.dll", CallingConvention = CallingConvention.Cdecl)]
            public static extern void Signal_Hash_End();

#endregion

#region PlatformExports
            const string platform = "tpm.dll"; // "platform.dll";

            [DllImport(platform, CallingConvention = CallingConvention.Cdecl)]
            public static extern void _plat__Signal_PhysicalPresenceOn();

            [DllImport(platform, CallingConvention = CallingConvention.Cdecl)]
            public static extern void _plat__Signal_PhysicalPresenceOff();

            [DllImport(platform, CallingConvention = CallingConvention.Cdecl)]
            public static extern void _plat__Signal_PowerOn();

            [DllImport(platform, CallingConvention = CallingConvention.Cdecl)]
            public static extern void _plat__Signal_PowerOff();

            [DllImport(platform, CallingConvention = CallingConvention.Cdecl)]
            public static extern void _plat__SetCancel();

            [DllImport(platform, CallingConvention = CallingConvention.Cdecl)]
            public static extern void _plat__ClearCancel();

            [DllImport("tpm.dll", CallingConvention = CallingConvention.Cdecl)]
            public static extern void _plat__NVEnable(IntPtr platParm);

            [DllImport(platform, CallingConvention = CallingConvention.Cdecl)]
            public static extern void _plat__NVDisable();

            [DllImport(platform, CallingConvention = CallingConvention.Cdecl)]
            public static extern void _plat__RsaKeyCacheControl(int state);

            [DllImport(platform, CallingConvention = CallingConvention.Cdecl)]
            public static extern void _plat__LocalitySet(byte locality);

            [DllImport(platform, CallingConvention = CallingConvention.Cdecl)]
            public static extern void _plat__SetNvAvail();

            [DllImport(platform, CallingConvention = CallingConvention.Cdecl)]
            public static extern void _plat__ClearNvAvail();

#endregion
        }
    } // class TpmDllWrapper

    /// <summary>
    /// The InprocTpm loads/runs TPM.dll (and ancillary libraries) in the TPM tester process.
    /// </summary>
    public sealed class InprocTpm : Tpm2Device
    {
        private IntPtr ResponseBuf;
        private uint ResponseBufSize = 4096;

        /// <summary>
        /// Specify the path to TPM.dll.  Note: any TPM in the current directory takes precedence
        /// </summary>
        /// <param name="tpmDllPath"></param>
        public InprocTpm(string tpmDllPath)
        {
            TpmDllWrapper.NativeMethods.SetDllDirectory(tpmDllPath);
            try
            {
                TpmDllWrapper.NativeMethods._plat__NVEnable(IntPtr.Zero);
            }
            catch (Exception)
            {
#if !NETFX_CORE
                Console.Error.WriteLine("Can't load the TPM dll (or a dependency) at " + tpmDllPath);
#endif
                throw;
            }
            NeedsHMAC = false;
            ResponseBuf = Marshal.AllocHGlobal((int)ResponseBufSize);
        }

        public override void Connect()
        {
            TpmDllWrapper.NativeMethods._plat__NVEnable(IntPtr.Zero);
            TpmDllWrapper.NativeMethods.TPM_Manufacture();
            TpmDllWrapper.NativeMethods._plat__NVDisable();
        }

        public override void PowerCycle()
        {
            PowerOff();
            PowerOn();
        }

        private bool PowerIsOn;

        public void PowerOff()
        {
            if (!PowerIsOn)
            {
                return;
            }
            TpmDllWrapper.NativeMethods._plat__Signal_PowerOff();
            PowerIsOn = false;
        }

        public void PowerOn()
        {
            if (PowerIsOn)
            {
                return;
            }
            TpmDllWrapper.NativeMethods._plat__Signal_PowerOn();
            TpmDllWrapper.NativeMethods._TPM_Init();
            TpmDllWrapper.NativeMethods._plat__SetNvAvail();
            PowerIsOn = true;
        }

        public override bool PlatformAvailable()
        {
            return true;
        }

        public override bool HasRM()
        {
            return _HasRM;
        }

        public override bool ImplementsPhysicalPresence()
        {
            return true;
        }

        public override void AssertPhysicalPresence(bool assertPhysicalPresence)
        {
            if (!PowerIsOn)
            {
                return;
            }
            if (assertPhysicalPresence)
            {
                TpmDllWrapper.NativeMethods._plat__Signal_PhysicalPresenceOn();
            }
            else
            {
                TpmDllWrapper.NativeMethods._plat__Signal_PhysicalPresenceOff();

            }
        }

        public override bool ImplementsCancel()
        {
            return true;
        }

        public override void SignalCancelOn()
        {
            if (!PowerIsOn)
            {
                return;
            }
            TpmDllWrapper.NativeMethods._plat__SetCancel();
        }

        public override void SignalCancelOff()
        {
            if (!PowerIsOn)
            {
                return;
            }
            TpmDllWrapper.NativeMethods._plat__ClearCancel();
        }

        public override void SignalNvOn()
        {
            if (!PowerIsOn)
            {
                return;
            }
            TpmDllWrapper.NativeMethods._plat__NVEnable(IntPtr.Zero);
        }

        public override void SignalNvOff()
        {
            if (!PowerIsOn)
            {
                return;
            }
            TpmDllWrapper.NativeMethods._plat__NVDisable();
        }

        public override void SignalKeyCacheOn()
        {
            if (!PowerIsOn)
            {
                return;
            }
            TpmDllWrapper.NativeMethods._plat__RsaKeyCacheControl(1);
        }

        public override void SignalKeyCacheOff()
        {
            if (!PowerIsOn)
            {
                return;
            }
            TpmDllWrapper.NativeMethods._plat__RsaKeyCacheControl(0);
        }

        public override void SignalHashStart()
        {
            if (!PowerIsOn)
            {
                return;
            }
            TpmDllWrapper.NativeMethods.Signal_Hash_Start();
        }

        public override void SignalHashData(byte[] data)
        {
            if (!PowerIsOn)
            {
                return;
            }
            TpmDllWrapper.NativeMethods.Signal_Hash_Data((uint)data.Length, data);
        }

        public override void SignalHashEnd()
        {
            if (!PowerIsOn)
            {
                return;
            }
            TpmDllWrapper.NativeMethods.Signal_Hash_End();
        }

        public override void TestFailureMode()
        {
            throw new NotImplementedException("Signal TestFailureMode is supported only by TPM simulator");
        }

        public override void DispatchCommand(CommandModifier active, byte[] inBuf, out byte[] outBuf)
        {
            if (!PowerIsOn)
            {
                outBuf = new byte[0];
                return;
            }

            TpmDllWrapper.NativeMethods._plat__LocalitySet(active.ActiveLocality);
            TpmDllWrapper.NativeMethods.ExecuteCommand((uint)inBuf.Length,
                                                       inBuf,
                                                       ref ResponseBufSize,
                                                       ref ResponseBuf);
            outBuf = new byte[ResponseBufSize];
            Marshal.Copy(ResponseBuf, outBuf, 0, (int)ResponseBufSize);
        }
    } // class InprocTpm

} // namespace Tpm2Lib

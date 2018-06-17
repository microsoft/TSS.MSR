/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using Tpm2Lib;

namespace TpmProxy
{
    public enum DeviceType
    {
        Tbs,
        Tcp
    }
    internal class NetProxy
    {
        static int Version = 1;

        // this is the type of the 
        DeviceType TheDeviceType;
        string TpmHost;
        int TpmPort;
        // these are the two independent servers
        TcpListener commandListener, platformListener;
        NetworkStream commandServer, platformServer;
        // If the device is TCP then these variables are set
        NetworkStream tpmCommand, tpmPlatform;
        // if the device is TBS then the following variable is set
        TbsDevice tbsDevice;
        // Combination of TpmEndPointInfo flags
        int tpmEndPointInfo;

        byte[] ZeroInt = new byte[4];
        byte[] OneInt = new byte[4]{1, 1, 1, 1};

        internal NetProxy(DeviceType theDeviceType, int listeningPort, string tpmHost, int tpmPort)
        {
            TpmHost = tpmHost;
            TpmPort = tpmPort;
            tpmEndPointInfo = 0;
            TheDeviceType = theDeviceType;
            // make the appropriate devices to connect to the TPM
            if (TheDeviceType == DeviceType.Tbs)
            {
                // todo: Check if TPM is in the raw mode.
                tpmEndPointInfo |= (int)TpmEndPointInfo.UsesTbs;
                tbsDevice = new TbsDevice();
                try
                {
                    tbsDevice.Connect();
                }
                catch (Exception e)
                {
                    Console.WriteLine("Failed to connect to the TBS context.  Error was: " + e.ToString());
                    return;
                }
            }

            // The proxy needs to relay commands on two sockets: one for the platform
            // and one for the rest of the commands.  These need to operate 
            // independently
            commandListener = new TcpListener(IPAddress.Any, listeningPort);
            commandListener.Start();
            platformListener = new TcpListener(IPAddress.Any, listeningPort+1);
            platformListener.Start();

            Console.WriteLine("Proxy is waiting for connections...");
            // Start a second threads to relay TCP commands to the device.  The two threads
            // access disjoint state. 
            Thread t = new Thread(PlatformServerThread);
            t.Start();

            // and start the second thread
            CommandServerThread();

        }

        // This is the platform server.  It runs on a new thread.
        public void PlatformServerThread()
        {
            while (true)
            {
                platformServer = platformListener.AcceptTcpClient().GetStream();
                Console.WriteLine("Platform connection accepted.");
                if (TheDeviceType == DeviceType.Tcp)
                {
                    TcpClient client = new TcpClient(TpmHost, TpmPort + 1);
                    tpmPlatform = client.GetStream();
                }
                try
                {
                    ServePlatform(platformServer, tpmPlatform);
                }
                catch (Exception) {}
                platformServer.Close();
                tpmPlatform.Close();
                Console.WriteLine("    Platform server disconnected");
            }
        }

        // This is the command server.  It runs on the main thread
        public void CommandServerThread()
        {
            while (true)
            {
                commandServer = commandListener.AcceptTcpClient().GetStream();
                if (TheDeviceType == DeviceType.Tcp)
                {
                    TcpClient client = new TcpClient(TpmHost, TpmPort);
                    tpmCommand = client.GetStream();
                }
                Thread.Sleep(300);
                Console.WriteLine("Command server accepted.");
                try
                {
                    ServeCommand(commandServer, tpmCommand);
                }
                catch (Exception) {}
                commandServer.Close();
                tpmCommand.Close();
                Console.WriteLine("    Command server disconnected.");
            }
        }

        /// <summary>
        /// This function serves command event streams.  
        /// </summary>
        /// <param name="s"></param>
        void ServePlatform(NetworkStream inStream, NetworkStream outStream)
        {
            while (true)
            {
                int command = ReadInt(inStream);
                TcpTpmCommands theCommand = (TcpTpmCommands)command;
                //Console.WriteLine("platform " + theCommand.ToString());
                switch (theCommand)
                {
                    case TcpTpmCommands.SignalPowerOn:
                    case TcpTpmCommands.SignalPowerOff:
                    case TcpTpmCommands.SignalPPOn:
                    case TcpTpmCommands.SignalPPOff:
                    case TcpTpmCommands.SignalCancelOn:
                    case TcpTpmCommands.SignalCancelOff:
                    case TcpTpmCommands.SignalNvOn:
                    case TcpTpmCommands.SignalNvOff:
                    case TcpTpmCommands.SignalKeyCacheOn:
                    case TcpTpmCommands.SignalKeyCacheOff:
                    //case TcpTpmCommands.InjectEps:
                        if (TheDeviceType == DeviceType.Tcp)
                        {
                            WriteInt((int)theCommand, outStream);
                            GetAndSendAck(outStream, inStream);
                        }
                        else
                        {
                            NotImplemented(theCommand);
                            Write(ZeroInt, inStream);
                        }
                        break;
                    case TcpTpmCommands.SessionEnd:
                    case TcpTpmCommands.Stop:
                        if (TheDeviceType == DeviceType.Tcp)
                        {
                            // Forward session end request and do not wait for ACK
                            WriteInt((int)theCommand, outStream);
                            //GetAndSendAck(outStream, inStream);
                        }
                        // Send back ACK and exit the communication loop
                        Write(ZeroInt, inStream);
                        return;
                    default:
                        throw new Exception("bad message");
                }
            }
        }
        /// <summary>
        /// This function serves command event streams.  
        /// </summary>
        /// <param name="s"></param>
        void ServeCommand(NetworkStream inStream, NetworkStream outStream)
        {
            while (true)
            {
                int command = ReadInt(inStream);
                TcpTpmCommands theCommand = (TcpTpmCommands)command;
                //Console.WriteLine("command " + theCommand.ToString());
                if (theCommand == TcpTpmCommands.RemoteHandshake)
                {
                    int clientVersion = ReadInt(inStream);
                    if (clientVersion == 0)
                    {
                        throw new Exception("Incompatible client (version 0, expected version 1 or higher)");
                    }
                }
                if (TheDeviceType == DeviceType.Tcp)
                {
                    WriteInt((int)theCommand, outStream);
                    switch (theCommand)
                    {
                        case TcpTpmCommands.SignalHashStart:
                        case TcpTpmCommands.SignalHashEnd:
                            break;

                        case TcpTpmCommands.SendCommand:
                            byte[] loc = Read(1, inStream);
                            byte[] cmd = ReadVarArray(inStream);
                            CommandModifier active = new CommandModifier();
                            Write(loc, outStream);
                            WriteVarArray(cmd, outStream);
                            
                            WriteVarArray(ReadVarArray(outStream), inStream);
                            break;

                        case TcpTpmCommands.SignalHashData:
                            byte[] data = ReadVarArray(inStream);
                            WriteVarArray(data, outStream);
                            break;

                        case TcpTpmCommands.RemoteHandshake:
                            WriteInt(Version, outStream);
                            int endPointVersion = ReadInt(outStream);
                            if (endPointVersion == 0)
                            {
                                throw new Exception("Incompatible end point (version 0, expected version 1 or higher)");
                            }
                            tpmEndPointInfo = ReadInt(outStream);
                            WriteInt(Version, inStream);
                            WriteInt(tpmEndPointInfo, inStream);
                            break;

                        case TcpTpmCommands.SessionEnd:
                        case TcpTpmCommands.Stop:
                            if (TheDeviceType == DeviceType.Tcp)
                            {
                                // Forward session end request and do not wait for ACK
                                WriteInt((int)theCommand, outStream);
                                //GetAndSendAck(outStream, inStream);
                            }
                            // Send back ACK and exit the communication loop
                            Write(ZeroInt, inStream);
                            return;
                        default:
                            throw new NotImplementedException("");
                    }
                    GetAndSendAck(outStream, inStream);
                }
                else // TBS device
                {
                    switch (theCommand)
                    {
                        case TcpTpmCommands.SignalHashStart:
                        case TcpTpmCommands.SignalHashEnd:
                            NotImplemented(theCommand);
                            Write(OneInt, inStream);
                            break;

                        case TcpTpmCommands.SignalHashData:
                            byte[] data = ReadVarArray(inStream);
                            NotImplemented(theCommand);
                            Write(OneInt, inStream);
                            break;

                        case TcpTpmCommands.SendCommand:
                            byte[] loc = Read(1, inStream);
                            byte[] cmd = ReadVarArray(inStream);
                            CommandModifier active = new CommandModifier();
                            byte[] outBuf;

                            tbsDevice.DispatchCommand(active, cmd, out outBuf);

                            WriteVarArray(outBuf, inStream);
                            Write(ZeroInt, inStream);
                            break;

                        case TcpTpmCommands.RemoteHandshake:
                            WriteInt(Version, inStream);
                            WriteInt(tpmEndPointInfo, inStream);
                            Write(ZeroInt, inStream);
                            break;

                        case TcpTpmCommands.SessionEnd:
                        case TcpTpmCommands.Stop:
                            // Send back ACK and exit the communication loop
                            Write(ZeroInt, inStream);
                            return;
                        default:
                            throw new NotImplementedException("");
                    }
                }
            }
        }

        void NotImplemented(TcpTpmCommands command)
        {
            Console.ForegroundColor = ConsoleColor.Red;
            Console.WriteLine("Command not implemented:" + command.ToString());
            Console.ResetColor();
        }
        
        void Write(byte[] x, NetworkStream s)
        {
            s.Write(x, 0, x.Length);
            return;
        }

        byte[] Read(int numBytes, NetworkStream s)
        {
            int numRead = 0;
            byte[] res = new byte[numBytes];

            while (numRead < numBytes)
            {
                numRead += s.Read(res, numRead, numBytes - numRead);
            }
            return res;
        }

        void GetAndSendAck(NetworkStream sOut, NetworkStream sIn)
        {
            int resp = ReadInt(sOut);
            if (resp != 0)
            {
                throw new Exception("TPM should have returned zero. Instead it returned " + resp.ToString());
            }
            Write(ZeroInt, sIn);
        }

        void WriteInt(int x, NetworkStream s)
        {
            s.Write(Globs.HostToNet(x), 0, 4);
        }

        int ReadInt(NetworkStream s)
        {
            int val = Globs.NetToHost4(Read(4, s));

            return val;
        }

        byte[] ReadVarArray(NetworkStream s)
        {
            int bufLen = Globs.NetToHost4(Read(4, s));
            byte[] buf = Read(bufLen, s);
            return buf;
        }

        void WriteVarArray(byte[] x, NetworkStream s)
        {
            Write(Globs.HostToNet(x.Length), s);
            Write(x, s);
        }
    }

    internal class TpmClient
    {
        public Socket CSocket;
        public bool PlatformServer;
        public TpmClient(Socket socket, bool plat)
        {
            CSocket = socket;
            PlatformServer = plat;
        }
    }
}

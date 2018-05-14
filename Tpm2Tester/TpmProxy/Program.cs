/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;

namespace TpmProxy
{
    class Proxy
    {
        static string DeviceName = "tbs";
        static int ListeningPort = 8834;
        static string TcpTpmHost = "localhost";
        static int TcpTpmPort = 2321;
        static DeviceType TheDeviceType;

        static void Main(string[] args)
        {
            bool ok = ParseCommandLine(args);

            if (!ok)
            {
                return;
            }

            Console.WriteLine("TCP Proxy on port " + ListeningPort + " on TPM device " + DeviceName);
            if (DeviceName == "tcp") TheDeviceType = DeviceType.Tcp; else TheDeviceType = DeviceType.Tbs;

            NetProxy proxy = new NetProxy(TheDeviceType, ListeningPort, TcpTpmHost, TcpTpmPort);
        }

        static bool ParseCommandLine(string[] args)
        {
            int argCounter = 0;

            while (argCounter < args.Length)
            {
                string a = args[argCounter++];

                if (a == "-?")
                {
                    PrintHelp();
                    return false;
                }

                if (a == "-device")
                {
                    if (!MoreArgs(argCounter, args))
                    {
                        return false;
                    }

                    DeviceName = args[argCounter++];
                    continue;
                }

                if (a == "-port")
                {
                    if (!MoreArgs(argCounter, args))
                    {
                        return false;
                    }

                    int port;
                    bool success = Int32.TryParse(args[argCounter++], out port);

                    if (!success)
                    {
                        Console.Error.WriteLine("Integer port number expected");
                        return false;
                    }

                    ListeningPort = port;
                    continue;
                }

                if (a == "-address")
                {
                    if (!MoreArgs(argCounter, args))
                    {
                        return false;
                    }

                    int portNum = 0;
                    string[] hostAddr = args[argCounter++].Split(new char[] { ':' });

                    if (hostAddr.Length != 2 || !Int32.TryParse(hostAddr[1], out portNum))
                    {
                        Console.Error.WriteLine("TPM TCP/IP server should be in format HostName:PortNumber");
                        return false;
                    }

                    TcpTpmHost = hostAddr[0];
                    TcpTpmPort = portNum;

                    continue;
                }

                Console.Error.WriteLine("Command line parameter error: " + a);
                PrintHelp();
                return false;
            }
            return true;
        }

        static bool MoreArgs(int argCounter, string[] args)
        {
            if (argCounter < args.Length)
            {
                return true;
            }

            Console.Error.WriteLine("Parameter missing");
            return false;
        }

        static void PrintHelp()
        {
            Console.Error.WriteLine("TpmProxy allows access to TPM from a remote machine over a TCP/IP connection");
            Console.Error.WriteLine("Usage (options can be combined)");
            Console.Error.WriteLine("TpmProxy -device DeviceName -- tbs or tcp, default device is TBS");
            Console.Error.WriteLine("TpmProxy -port PortNumber -- default listening port is 8834");
            Console.Error.WriteLine("TpmProxy -address Host:Port  -- remote host for TCP relay (default localhost:2322)");
            return;
        }

       
    }
}

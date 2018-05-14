/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.IO;
using System.Diagnostics;
using Tpm2Lib;

namespace Tpm2Tester
{
    internal class Dbg
    {
        internal static bool Enabled = false;

        internal bool ThisEnabled = false;
        private string CurIndent = "";

        internal Dbg(bool enabled = false)
        {
            ThisEnabled = enabled;
        }

        internal void Trace(string format, params object[] args)
        {
            if (Enabled && ThisEnabled)
            {
                Debug.WriteLine(CurIndent + format, args);
            }
        }

        internal void Indent()
        {
            if (Enabled && ThisEnabled)
            {
                CurIndent += "    ";
            }
        }

        internal void Unindent()
        {
            if (Enabled && ThisEnabled && CurIndent.Length > 3)
            {
                CurIndent = CurIndent.Substring(0, CurIndent.Length - 4);
            }
        }
    } // class Dbg


#if !TSS_NO_TCP
    // TransportLogger creates log files of TPM commands executed.
    // Currently only works with the TCP TPM device.
    internal class TransportLogger
    {
        string dir = null;
        StreamWriter log;
        bool Sending = true;
        string CurrentTest = null;
        string tempName = null;
        bool logging = false;
        bool PhaseToLog = false;

        internal bool IsLogging()
        {
            return logging;
        }

        internal TransportLogger() { }

        internal TransportLogger(string logDirectory, Tpm2Device device)
        {
            dir = logDirectory;
            if (!Directory.CreateDirectory(logDirectory).Exists)
                return;
            logging = true;
            var d = device as TcpTpmDevice;
            if (d == null)
            {
                throw new Exception("Logging only supports TPM over TCP");
            }
            d.SetTransportCallback(this.NotifyData);
            InitTempLogFile();
        }

        void InitTempLogFile()
        {
            tempName = Path.GetFullPath(dir) + Path.DirectorySeparatorChar + "temp_log.txt";
            log = new StreamWriter(new FileStream(tempName, FileMode.Create));
        }

        internal void NotifyTestStart(string testName)
        {
            if (!logging)
                return;
            lock (notifyLock)
            {
                CurrentTest = testName;
                Sending = true;
                PhaseToLog = true;
            }
        }

        internal void NotifyTestComplete()
        {
            if (!logging)
                return;
            lock (notifyLock)
            {
                log.Flush();
#if !TSS_MIN_API
                log.Close();
#endif
                log.Dispose();

                string fileName = CurrentTest + "_log.txt";
                string logName = Path.GetFullPath(dir) +
                                 Path.DirectorySeparatorChar + fileName;
                if (File.Exists(logName)) File.Delete(logName);
                File.Move(tempName, logName);
                log = null;

                InitTempLogFile();

                Debug.Assert(Sending);
                PhaseToLog = false;
            }
        }

        Object notifyLock = new Object();
        TcpTpmDevice.Channel lastChannel = TcpTpmDevice.Channel.Undefined;
        TcpTpmDevice.CommsSort lastSort = TcpTpmDevice.CommsSort.Undefined;

        internal void NotifyData(TcpTpmDevice.CommsSort sort,
                               TcpTpmDevice.Channel channel, byte[] inOrOutData)
        {
            // Note - a complexity is that there can be two NotifyData per TCP read,
            // because we need to do a Read(len) followed by a Read(data)

            if (!logging || !PhaseToLog)
                return;

            lock (notifyLock)
            {
                if ((sort != lastSort) || (channel != lastChannel))
                {
                    log.WriteLine("");
                    string t = channel.ToString()[0] + " ";
                    t += (sort == TcpTpmDevice.CommsSort.ByteSent) ? "S" : "R";
                    t += " ";
                    log.Write(t);
                }
                lastSort = sort;
                lastChannel = channel;
                log.Write(Globs.HexFromByteArray(inOrOutData));
            }
        }
    } // class TransportLogger
#endif //!TSS_NO_TCP
}

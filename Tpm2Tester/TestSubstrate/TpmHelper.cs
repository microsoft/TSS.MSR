/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.Reflection;
using System.Linq;
using System.Threading;
using System.Diagnostics;
using Tpm2Lib;

namespace Tpm2Tester
{
    public static class TpmHelper
    {
        public static ushort MaxOaepMsgSize(ushort keyBitLen, TpmAlgId hashAlg)
        {
            int keySize = keyBitLen / 8;
            int maxMessageSize = keySize - 2 * TpmHash.DigestSize(hashAlg) - 2;
            return (ushort)(maxMessageSize > keySize || maxMessageSize < 0 ? 0 : maxMessageSize);
        }

        // Returns reference to the byte buffer containing unique value of inPub.
        // Note that in case of ECC keys, the returned buffer references only the
        // first half of the unique value (i.e. x-coordinate of the ECC point).
        public static FieldInfo GetUniqueBuffer(TpmPublic pub, out byte[] buf)
        {
            var keyType = pub.parameters.GetUnionSelector();
            buf = null;
            switch (keyType)
            {
                case TpmAlgId.Rsa:
                    buf = (pub.unique as Tpm2bPublicKeyRsa).buffer;
                    return pub.unique.GetType().GetField("buffer");
                case TpmAlgId.Ecc:
                    buf = (pub.unique as EccPoint).x;
                    return pub.unique.GetType().GetField("x");
                case TpmAlgId.Symcipher:
                case TpmAlgId.Keyedhash:
                    buf = (pub.unique as Tpm2bDigest).buffer;
                    return pub.unique.GetType().GetField("buffer");
            }
            Globs.Throw<NotImplementedException>(
                                "GetUniqueBuffer: Unknown TpmPublic type " + keyType);
            return null;
        }

        public static byte[] GetUniqueBuffer(TpmPublic pub)
        {
            byte[] buf;
            if (pub.parameters.GetUnionSelector() == TpmAlgId.Ecc)
            {
                buf = (pub.unique as EccPoint).GetTpmRepresentation();
            }
            else
                GetUniqueBuffer(pub, out buf);
            return buf;
        }

        public static IAsymSchemeUnion GetScheme(TpmPublic pub)
        {
            return (pub.type == TpmAlgId.Keyedhash
                        ? (IAsymSchemeUnion)(pub.parameters as KeyedhashParms).scheme
                        : pub.type == TpmAlgId.Rsa
                                ? (IAsymSchemeUnion)(pub.parameters as RsaParms).scheme
                                : (IAsymSchemeUnion)(pub.parameters as EccParms).scheme);
        }

        public static TpmAlgId GetSchemeHash(IAsymSchemeUnion scheme)
        {
            if (scheme == null || scheme is NullUnion || scheme is Empty)
                return TpmAlgId.Null;
            var daaScheme = scheme as SchemeEcdaa;
            return daaScheme != null ? daaScheme.hashAlg
                                     : (scheme as SchemeHash).hashAlg;
        }

        public static TpmAlgId GetSchemeHash(ISigSchemeUnion scheme)
        {
            return GetSchemeHash(scheme as IAsymSchemeUnion);
        }

        public static TpmAlgId GetSchemeHash(TpmPublic pub)
        {
            TpmAlgId schemeAlg = GetSchemeHash(GetScheme(pub));
            return schemeAlg == TpmAlgId.Null ? pub.nameAlg : schemeAlg;
        }

        public static TpmHandle[] GetAllLoadedEntities(Tpm2 tpm)
        {
            TpmHandle[] h0 = GetLoadedEntities(tpm, Ht.Transient);
            TpmHandle[] h1 = GetLoadedEntities(tpm, Ht.LoadedSession);

            TpmHandle[] h = new TpmHandle[h0.Length + h1.Length];
            Array.Copy(h0, h, h0.Length);
            Array.Copy(h1, 0, h, h0.Length, h1.Length);
            return h;
        }

        public static TpmHandle[] GetLoadedEntities(Tpm2 tpm, Ht rangeToQuery)
        {
            uint maxHandles = UInt32.MaxValue;
            ICapabilitiesUnion h = null;
            byte moreData = tpm.GetCapability(Cap.Handles, ((uint)rangeToQuery) << 24,
                                              maxHandles, out h);
            if (moreData != 0)
            {
                throw new NotImplementedException(
                                        "GetLoadedEntities: Too much data returned");
            }
            if (h.GetType() != typeof(HandleArray))
            {
                throw new Exception(
                            "GetLoadedEntities: Incorrect capability type requested");
            }
            return (h as HandleArray).handle;
        }

        public static bool FlushEntities(Tpm2 tpm, TpmHandle[] entities)
        {
            bool succeeded = true;
            foreach (TpmHandle h in entities)
            {
                Debug.Assert(h.GetType() != Ht.NvIndex);
                tpm._AllowErrors().FlushContext(h);
                succeeded = succeeded && tpm._LastCommandSucceeded();
            }
            return succeeded;
        }

        // Makes sure that the size of data transferred in a single write operation
        // does not exceed the limit on a command parameter size.
        public static void SafeNvWrite(Tpm2 tpm, ushort maxNvOpSize,
                                   TpmHandle nvHandle, byte[] contents)
        {
            for (ushort offset = 0; offset < contents.Length; offset += maxNvOpSize)
            {
                var chunkSize = contents.Length - offset < maxNvOpSize ?
                                        contents.Length - offset : maxNvOpSize;
                var data = Globs.CopyData(contents, offset, chunkSize);
                tpm.NvWrite(nvHandle, nvHandle, data, offset);
            }
        }

        public static byte[] SafeNvRead(Tpm2 tpm, ushort maxNvOpSize,
                                    TpmHandle nvHandle, ushort size, ushort nvOffset = 0)
        {
            byte[] contents = new byte[size];
            for (ushort offset = 0; offset < size; offset += maxNvOpSize,
                                                   nvOffset += maxNvOpSize)
            {
                var chunkSize = size - offset < maxNvOpSize ? size - offset : maxNvOpSize;
                var chunk = tpm.NvRead(nvHandle, nvHandle, (ushort)chunkSize, nvOffset);
                Array.Copy(chunk, 0, contents, offset, chunkSize);
            }
            return contents;
        }

        public static TpmPublic ReadPublic(Tpm2 tpm, TpmHandle h)
        {
            byte[] name, qualName;
            return tpm.ReadPublic(h, out name, out qualName);
        }

        public static NvPublic NvReadPublic(Tpm2 tpm, TpmHandle h)
        {
            byte[] name;
            return tpm.NvReadPublic(h, out name);
        }

        public static Tpm2bDigest[] SafePcrRead(Tpm2 tpm, PcrSelection sel)
        {
            PcrSelection[] selOut;
            var selIn = new PcrSelection[] { sel.Copy() };
            var pcrValues = new Tpm2bDigest[0];
            do
            {
                Tpm2bDigest[] vals;
                tpm.PcrRead(selIn, out selOut, out vals);
                pcrValues = pcrValues.Concat(vals).ToArray();
                Debug.Assert(selOut.Length == 1);
                // The first part of the while condition is used to by pass not
                // implemented PCRs
            } while (!Globs.IsZeroBuffer(selOut[0].pcrSelect)
                     && selIn[0].Clear(selOut[0]));
            Debug.Assert(selIn[0].GetSelectedPcrs().Length == 0);
            Debug.Assert(sel.GetSelectedPcrs().Length == pcrValues.Length);
            return pcrValues;
        }

        public static Tpm2bDigest[] SafePcrRead(Tpm2 tpm, PcrSelection[] sel)
        {
            if (sel.Length == 0)
            {
                return new Tpm2bDigest[0];
            }

            var pcrValues = SafePcrRead(tpm, sel[0]);
            for (int i = 1; i < sel.Length; ++i)
            {
                pcrValues = pcrValues.Concat(SafePcrRead(tpm, sel[i])).ToArray();
            }
            return pcrValues;
        }

        public static PcrValueCollection SafePcrReadAsColl(Tpm2 tpm, PcrSelection sel)
        {
            return new PcrValueCollection(new PcrSelection[] { sel }, SafePcrRead(tpm, sel));
        }

        public static void PowerCycle(Tpm2 tpm, Su shutdownMode, Su startupMode)
        {
            tpm._AllowErrors()
               .Shutdown(shutdownMode);
            tpm._GetUnderlyingDevice().PowerCycle();
            tpm.Startup(startupMode);
        }

        public static bool AreAnySlotsFull(Tpm2 tpm)
        {
            var tagActiveSession = TpmHelpers.GetEnumerator<Ht>("ActiveSession", "SavedSession");
            return TpmHelper.GetLoadedEntities(tpm, Ht.Transient).Length != 0 ||
                   TpmHelper.GetLoadedEntities(tpm, Ht.LoadedSession).Length != 0 ||
                   TpmHelper.GetLoadedEntities(tpm, Ht.LoadedSession).Length != 0;
        }

        public static TpmPrivate GetPlaintextPrivate(Tpm2 tpm, TpmHandle key, PolicyTree policy)
        {
            AuthSession sess = tpm.StartAuthSessionEx(TpmSe.Policy, policy.HashAlg);
            sess.RunPolicy(tpm, policy);
            TpmPrivate privPlain = null;
            byte[] symSeed;
            tpm[sess]._ExpectResponses(TpmRc.Success, TpmRc.Attributes)
                     .Duplicate(key, TpmRh.Null, null, new SymDefObject(),
                                out privPlain, out symSeed);
            Debug.Assert(!tpm._LastCommandSucceeded() || symSeed.Length == 0);
            tpm.FlushContext(sess);
            return Globs.IsEmpty(privPlain.buffer) ? null : privPlain;
        }

        public static SchemeEcdaa PrepareEcdaaScheme(Tpm2 tpm, TpmHandle signKey,
                                                     ISigSchemeUnion scheme)
        {
            var schemeEcdaa = scheme as SchemeEcdaa;
            if (schemeEcdaa != null)
            {
                byte[] name, qualName;
                var keyPub = tpm.ReadPublic(signKey, out name, out qualName);

                ushort counter = 0;
                EccPoint l, E;
                EccPoint PP = keyPub.unique as EccPoint;

                tpm.Commit(signKey, PP, null, null, out l, out E, out counter);
                schemeEcdaa.count = counter;
            }
            return schemeEcdaa;
        }

        // Returns the ratio of a TPM clock second to one system time second
        public static double MeasureClockRate(Tpm2 tpm)
        {
            const int MaxIter = 20;
            int NumSamplesPerIter = 5;
            int N = NumSamplesPerIter,
                L = 0;
            double[] tpmTimes = null;
            double[] sysTimes = null;
            double[] newTpmTimes = new double[N];
            double[] newSysTimes = new double[N];
            int iter = 0;

            double stdDevSys = 0,
                    meanSys = 0,
                    stdDevTpm = 0,
                    meanTpm = 0;
            int n = 0;
            double sysTime = 0, tpmTime = 0;

            do
            {
                tpmTimes = newTpmTimes;
                sysTimes = newSysTimes;

                TimeInfo tpmStart = null;
                DateTime sysStart = DateTime.MinValue;

                for (int i = L; i <= N; i++)
                {
                    TimeInfo tpmStop = tpm.ReadClock();
                    DateTime sysStop = DateTime.Now;
                    if (tpmStart != null)
                    {
                        tpmTimes[i - 1] = tpmStop.time - tpmStart.time;
                        sysTimes[i - 1] = (sysStop - sysStart).TotalMilliseconds;
                    }
                    tpmStart = tpmStop;
                    sysStart = sysStop;
                    Thread.Sleep(600);
                }

                // Eliminate outliers that may be caused by the current thread having
                // been preempted at a "wrong" point causing the measured rate distortion.
                meanSys = meanTpm = stdDevSys = stdDevTpm = sysTime = tpmTime = 0;
                for (int i = 0; i < N; i++)
                {
                    meanSys += sysTimes[i];
                    meanTpm += tpmTimes[i];
                }
                meanSys /= N;
                meanTpm /= N;

                for (int i = 0; i < N; i++)
                {
                    double d = (sysTimes[i] - meanSys);
                    stdDevSys += d * d;
                    d = (tpmTimes[i] - meanTpm);
                    stdDevTpm += d * d;
                }
                stdDevSys = Math.Sqrt(stdDevSys / N);
                stdDevTpm = Math.Sqrt(stdDevTpm / N);

                bool imprecise = stdDevSys / meanSys > 0.03 || stdDevTpm / meanTpm > 0.03;
                if (imprecise)
                {
                    newTpmTimes = new double[N + NumSamplesPerIter];
                    newSysTimes = new double[N + NumSamplesPerIter];
                }

                n = 0;
                for (int i = 0; i < N; i++)
                {
                    if (Math.Abs(sysTimes[i] - meanSys) < 2 * stdDevSys
                        && Math.Abs(tpmTimes[i] - meanTpm) < 2 * stdDevTpm)
                    {
                        sysTime += sysTimes[i];
                        tpmTime += tpmTimes[i];
                        if (imprecise)
                        {
                            newSysTimes[n] = sysTimes[i];
                            newTpmTimes[n] = tpmTimes[i];
                        }
                        ++n;
                    }
                    //else Console.Write("Dropped[{0}] = {1} ", i, sysTimes[i]);
                }
                if (!imprecise && n > 2)
                    break;
                L = n;
                N = L + NumSamplesPerIter;
            } while (++iter < MaxIter);

            if (iter == MaxIter)
                Globs.Throw("The system is likely overloaded. Cannot do reliable time measurements.");

            //Console.WriteLine("ITER {0}, MEAN {1:F0}->{2:F0}, SD {3:F1}; Good {4}; RATE {5:F2}",
            //                  iter+1, meanSys, sysTime / n, stdDevSys, n, tpmTime / sysTime);
            return tpmTime / sysTime;
        } // MeasureClockRate()

    } // class TpmConfig
}

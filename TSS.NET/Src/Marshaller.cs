/*++

Copyright (c) 2010-2015 Microsoft Corporation
Microsoft Confidential

*/
using System;
using System.Xml.Serialization;
using System.IO;
using System.Runtime.InteropServices;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.Diagnostics;

namespace Tpm2Lib
{
    public enum DataRepresentation
    {
        Tpm,
        LittleEndian
    }

    /// <summary>
    /// Support for marshaling of TPM structures and other types to and from the TPM.
    /// </summary>
    public class Marshaller
    {
        DataRepresentation Repr;

        ByteBuf Buffer;

        // We use length-prepended structures in command marshaling and some other places.
        Stack<SizePlaceholder> SizesToFillIn;

        string[] QualifiedName;
        int QualNamePos;
        int ElementStart;
        int ElementEnd;

        public Marshaller(DataRepresentation mt = DataRepresentation.Tpm)
        {
            Reset(mt);
        }

        public Marshaller(byte[] buf, DataRepresentation mt = DataRepresentation.Tpm)
        {
            Reset(mt);
            Buffer = new ByteBuf(buf);
        }
        
        public void Reset(DataRepresentation mt = DataRepresentation.Tpm)
        {
            Buffer = new ByteBuf();
            SizesToFillIn = new Stack<SizePlaceholder>();
            Repr = mt;
        }
        
        public static byte[] GetTpmRepresentation(params Object[] theObjects)
        {
            var m = new Marshaller();
            foreach(Object o in theObjects)
            {
                m.Put(o, null);
            }
            return m.GetBytes();
        }

        /// <summary>
        /// Convert to a USHORT length-prepended byte array.
        /// </summary>
        /// <param name="x"></param>
        /// <returns></returns>
        public static byte[] ToTpm2B(byte[] x)
        {
            return GetTpmRepresentation((ushort)x.Length, x);
        }

        /// <summary>
        /// Assuming a ushort-prepended array, return the payload (if properly formed).
        /// </summary>
        /// <param name="x"></param>
        /// <returns></returns>
        static public byte[] Tpm2BToBuffer(byte[] x)
        {
            var m = new Marshaller(x);
            var len = m.Get<ushort>();
            if (len != x.Length - 2) throw new Exception("Ill formed TPM2B");
            var ret = new byte[len];
            Array.Copy(x, 2, ret, 0, len);
            return ret;
        }

        public byte[] GetBytes()
        {
            if (SizesToFillIn.Count != 0)
            {
                throw new Exception("Unresolved PushSize()");
            }
            int numBytes = Buffer.GetSize();
            var temp = new byte[numBytes];
            Array.Copy(Buffer.GetBuffer(), temp, numBytes);
            return temp;
        }

        public uint GetGetPos()
        {
            return (uint)Buffer.GetGetPos();
        }

        public byte[] RemoveBytesInMiddle(int pos, int len)
        {
            return Buffer.RemoveBytesInMiddle(pos, len);
        }

        public uint GetValidLength()
        {
            return (uint)Buffer.GetValidLength();
        }

        public void SetGetPos(uint getPos)
        {
            Buffer.SetGetPos((int)getPos);
        }

        public static T FromTpmRepresentation<T>(byte[] b)
        {
            var m = new Marshaller(b);
            Object obj = m.Get<T>();
            return (T)obj;
        }
        
        private Object FromNetValueType(Type tp)
        {
            byte[] data = Buffer.Extract(Globs.SizeOf(tp));
            if (Repr == DataRepresentation.Tpm)
            {
                return Globs.NetToHostValue(tp, data);
            }
            if (Repr == DataRepresentation.LittleEndian)
            {
                return Globs.FromBytes(tp, data);
            }
            // Unsupported type
            Debug.Assert(false);
            return null;
        }

        public void Put(Object o, string name)
        {
            PutInternal(o, name);
        }

        /// <summary>
        /// Gets the location start and length of an embedded element in a TPM structure in TPM-canonical form.
        /// </summary>
        /// <param name="o"></param>
        /// <param name="qualifiedName"></param>
        /// <param name="start"></param>
        /// <param name="finish"></param>
        public static void GetFragmentInfo(Object o, string qualifiedName, out int start, out int finish)
        {
            var m = new Marshaller {QualifiedName = qualifiedName.Split(new[] {'.'}), QualNamePos = 0};
            m.PutInternal(o, "");
            start = m.ElementStart;
            finish = m.ElementEnd;
            m.QualifiedName = null;
        }

        public void PutInternal(Object o, string name)
        {
            bool measuringElement = false;
            if (QualifiedName != null)
            {
                // We are searching for the start and length of a fragment
                if (name == QualifiedName[QualNamePos])
                {
                    ElementStart = (int)GetPutPos();
                    measuringElement = true;
                }
            }

            if (o == null)
            {
            }
            // ReSharper disable once CanBeReplacedWithTryCastAndCheckForNull
            else if (o is TpmStructureBase)
            {
                ((TpmStructureBase)o).ToNet(this);
            }
            else if (o is Enum)
            {
                Type underlyingType = Enum.GetUnderlyingType(o.GetType());
                if (underlyingType == typeof(byte))
                {
                    // ReSharper disable once SuggestUseVarKeywordEvident
                    // ReSharper disable once PossibleInvalidCastException
                    byte x = (byte)o;
                    ToNetValueType(x, name);
                }
                else if (underlyingType == typeof(ushort))
                {
                    // ReSharper disable once SuggestUseVarKeywordEvident
                    // ReSharper disable once PossibleInvalidCastException
                    ushort x = (ushort)o;
                    ToNetValueType(x, name);
                }
                else if (underlyingType == typeof(uint))
                {
                    // ReSharper disable once SuggestUseVarKeywordEvident
                    // ReSharper disable once PossibleInvalidCastException
                    uint x = (uint)o;
                    ToNetValueType(x, name);
                }
                else if (underlyingType == typeof(sbyte))
                {
                    // ReSharper disable once SuggestUseVarKeywordEvident
                    // ReSharper disable once PossibleInvalidCastException
                    byte x = (byte)((sbyte)o);
                    ToNetValueType(x, name);
                }
                else
                {
                    throw new Exception("Unsupported enum");
                }
            }
            else if (o is ValueType)
            {
                ToNetValueType(o, name);
            }
            // ReSharper disable once CanBeReplacedWithTryCastAndCheckForNull
            else if (o is Array)
            {
                var a = (Array)o;
                int count = 0;
                foreach (Object elem in a)
                {
                    Put(elem, name + count);
                    count++;
                }
            }
            else
            {
                throw new NotImplementedException("Unsupported object type");
            }

            if (measuringElement)
            {
                ElementEnd = (int)GetPutPos();
            }
        }

        public void PutUintPrependedArray(byte[] x, string name)
        {
            var l = (uint)x.Length;
            Put(l, name + "_length");
            Put(x, name);
        }

        public Object Get(Type tp, string name)
        {
            if (typeof(TpmStructureBase).GetTypeInfo().IsAssignableFrom(tp.GetTypeInfo()))
            {
                Object o = Activator.CreateInstance(tp);
                ((TpmStructureBase)o).ToHost(this);
                return o;
            }
            if (typeof(Enum).GetTypeInfo().IsAssignableFrom(tp.GetTypeInfo()))
            {
                Type underlyingType = Enum.GetUnderlyingType(tp);
                Object o = FromNetValueType(underlyingType);
                return Enum.ToObject(tp, o);
            }

            if (typeof(ValueType).GetTypeInfo().IsAssignableFrom(tp.GetTypeInfo()))
            {
                Object o = FromNetValueType(tp);
                return o;
            }
            throw new NotImplementedException("NOT IMPLEMENTED");
        }

        public T Get<T>()
        {
            Object tempO = Get(typeof(T), "");
            return (T)tempO;
        }

        public void PutSizeTag(int size, int sizeLength, string name)
        {
            var s = BitConverter.GetBytes(size);
            if (sizeLength != sizeof(uint))
            {
                Array.Resize(ref s, sizeLength);
            }
            PutInternal(Globs.ReverseByteOrder(s), name);
        }

        public int GetSizeTag(int sizeLength, string name)
        {
            byte[] counterData = GetArray<byte>(sizeLength, name);
            if (Repr == DataRepresentation.Tpm)
            {
                counterData = Globs.ReverseByteOrder(counterData);
            }
            Array.Resize(ref counterData, sizeof(int));
            return BitConverter.ToInt32(counterData, 0);
        }

        public T[] GetArray<T>(int length, string name = "")
        {
            return GetArray(typeof(T), length, name) as T[];
        }

        public Object GetArray(Type elementType, int length, string name = "")
        {
            Array a = Array.CreateInstance(elementType, length);
            for (int j = 0; j < length; j++)
            {
                Object val = Get(elementType, name + j);
                a.SetValue(val, j);
            }
            return a;
        }

        public byte[] GetNBytes(int n)
        {
            return Buffer.Extract(n);
        }

        void ToNetValueType(Object o, string name)
        {
            if (Repr == DataRepresentation.Tpm)
            {
                Buffer.Append(Globs.HostToNet(o));
                return;
            }
            if (Repr == DataRepresentation.LittleEndian)
            {
                Buffer.Append(Globs.GetBytes(o));
            }
            throw new Exception("Unsupported marshaling type");
        }

        public void PushLength(int numBytes)
        {
            var sp = new SizePlaceholder(Buffer.GetSize(), numBytes);
            SizesToFillIn.Push(sp);
            switch (numBytes)
            {
                case 1: ToNet((byte)0xFF); return;
                case 2: ToNet((ushort)0xFFFF); return;
                // ReSharper disable once RedundantCast
                case 4: ToNet((uint)0xFFFFFFFF); return;
                default: throw new Exception("Invalid length");
            }
        }

        public void PopAndSetLength()
        {
            SizePlaceholder sp = SizesToFillIn.Pop();
            int len = Buffer.GetSize() - sp.StartPos - sp.Length;
            switch (sp.Length)
            {
                case 1: Buffer.SetBytesInMiddle(Globs.HostToNet((byte)len), sp.StartPos); return;
                case 2: Buffer.SetBytesInMiddle(Globs.HostToNet((ushort)len), sp.StartPos); return;
                case 4: Buffer.SetBytesInMiddle(Globs.HostToNet((uint)len), sp.StartPos); return;
                default: throw new Exception("Invalid length");
            }
        }

        public void PopAndSetLengthToTotalLength()
        {
            SizePlaceholder sp = SizesToFillIn.Pop();
            int len = Buffer.GetSize();
            switch (sp.Length)
            {
                case 1: Buffer.SetBytesInMiddle(Globs.HostToNet((byte)len), sp.StartPos); return;
                case 2: Buffer.SetBytesInMiddle(Globs.HostToNet((ushort)len), sp.StartPos); return;
                case 4: Buffer.SetBytesInMiddle(Globs.HostToNet((uint)len), sp.StartPos); return;
                default: throw new Exception("Invalid length");
            }
        }

        public uint GetPutPos()
        {
            return (uint)Buffer.GetSize();
        }

        void ToNet(Object o)
        {
            Put(o, null);
        }
    } // class Marshaller

    internal struct SizePlaceholder
    {
        internal SizePlaceholder(int startPos, int length)
        {
            StartPos = startPos;
            Length = length;
        }
        internal int StartPos;
        internal int Length;
    }
}
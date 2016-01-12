/*++

Copyright (c) 2010-2015 Microsoft Corporation
Microsoft Confidential

*/
using System;

namespace Tpm2Lib
{
    public class BitFieldElementAttribute : Attribute
    {
        internal int StartBit;
        internal int EndBit;
        public BitFieldElementAttribute(int startBit, int endBit)
        {
            StartBit = startBit;
            EndBit = endBit;
        }
    }

    public class MarshalingAttribute : Attribute
    {
    }

    public class BitFieldAttribute : MarshalingAttribute
    {
        internal Type WireType;
        public BitFieldAttribute(Type wireType)
        {
            WireType = wireType;
        }
    }

    [FlagsAttribute]
    public enum MarshalType
    {
        Normal                  = 0x00,
        FixedLengthArray        = 0x01,
        VariableLengthArray     = 0x04,
        Union                   = 0x08,
        UnionAllowNull          = 0x10,
        CustomMarshal           = 0x20,
        SizedStruct             = 0x40,
        ArrayCount              = 0x80,
      //UnionObject             = 0x100,
        UnionSelector           = 0x200,
      //CountedObject           = 0x400,
        LengthOfStruct          = 0x800,
    }

    public class MarshalAsAttribute : MarshalingAttribute
    {
        internal int Index = 0;
        public MarshalType MarshType;
        internal int ArrayLength;
        internal String AssociatedArrayName;
        internal int SizeLength;
        internal string AssociatedUnionSelector;

        public MarshalAsAttribute(int index, MarshalType tp = MarshalType.Normal)
        {
            Index = index;
            MarshType = tp;
        }

        public MarshalAsAttribute(int index, MarshalType tp, int theArrayLength)
        {
            Index = index;
            if (tp != MarshalType.FixedLengthArray)
            {
                throw new Exception("Marshaling an array?");
            }
            MarshType = tp;
            ArrayLength = theArrayLength;
        }

        public MarshalAsAttribute(int index, MarshalType tp, string associatedVariable, int sizeLength = 0)
        {
            Index = index;
            MarshType = tp;
            SizeLength = sizeLength;
            if (tp == MarshalType.VariableLengthArray)
            {
                AssociatedArrayName = associatedVariable;
                return;
            }
            if (tp == MarshalType.Union)
            {
                AssociatedUnionSelector = associatedVariable;
                return;
            } 
            throw new Exception("Unknown MarshallType?");
        }
    }

    public class RangeAttribute : MarshalingAttribute
    {
        public uint MinVal;
        public uint MaxVal;
        public uint OnlyVal;
        public uint[] Values;
    }

    /// <summary>
    /// Most TPM types are translated into a style that is .Net/Java - like
    /// This attribute encapsulates the original specication type name.
    /// </summary>
    public class SpecTypeNameAttribute : Attribute
    {
        public string Name;
        public SpecTypeNameAttribute(string name)
        {
            Name = name;
        }
    }

    public class TpmCommandAttribute : Attribute
    {
    }
}

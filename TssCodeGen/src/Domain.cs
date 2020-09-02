/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

using System;
using System.Collections.Generic;


namespace CodeGen
{
    /// <summary> Base class for different types of constraint on allowed values </summary>
    public abstract class Constraint
    {
        public enum Type
        {
            Single,
            Min,
            Max
        }

        public abstract bool IsEmpty();

        protected static int _GetHash(TpmConstExpr c)
        {
            return c == null ? 0 : c.GetHashCode();
        }
    }

    /// <summary> Defines a single allowed value </summary>
    public class SingleValue : Constraint
    {
        public TpmConstExpr Value;

        public SingleValue (TpmConstExpr val)
        {
            Value = val;
        }

        public override bool IsEmpty() { return Value == null; }

        public static bool operator == (SingleValue lhs, SingleValue rhs)
        {
            return (object)lhs == null ? (object)rhs == null
                                       : (object)rhs != null && lhs.Value == rhs.Value;
        }

        public static bool operator != (SingleValue lhs, SingleValue rhs)
        {
            return !(lhs == rhs);
        }

        public override bool Equals(Object obj)
        {
            return this == (SingleValue)obj;
        }

        public override int GetHashCode()
        {
            return _GetHash(Value);
        }
    } // class SingleValue

    /// <summary> Defines a range of allowed values </summary>
    public class BoundedRange : Constraint
    {
        public TpmConstExpr MinVal;
        public TpmConstExpr MaxVal;

        public BoundedRange (TpmConstExpr minVal, TpmConstExpr maxVal)
        {
            MinVal = minVal;
            MaxVal = maxVal;
        }

        public override bool IsEmpty() { return MinVal == null && MaxVal == null; }

        public static bool operator == (BoundedRange lhs, BoundedRange rhs)
        {
            return (object)lhs == null ? (object)rhs == null
                                       : (object)rhs != null &&
                                         lhs.MinVal == rhs.MinVal && lhs.MaxVal == rhs.MaxVal;
        }

        public static bool operator != (BoundedRange lhs, BoundedRange rhs)
        {
            return !(lhs == rhs);
        }

        public override bool Equals(Object obj)
        {
            return this == (BoundedRange)obj;
        }

        public override int GetHashCode()
        {
            return _GetHash(MinVal) ^ _GetHash(MaxVal);
        }
    } // class BoundedRange

    /// <summary> Defines a set of all allowed values </summary>
    public class Domain : List<Constraint>
    {
        public TpmConstExpr this[int idx, Constraint.Type ct]
        {
            get {
                if (Count <= idx)
                    return null;

                if (ct == Constraint.Type.Single)
                {
                    var v = this[idx] as SingleValue;
                    return v == null ? null : v.Value;
                }
                
                var r = this[idx] as BoundedRange;
                return r == null ? null : ct == Constraint.Type.Min ? r.MinVal : r.MaxVal;
            }

            set {
                if (ct == Constraint.Type.Single)
                {
                    (this[idx] as SingleValue).Value = value;
                    return;
                }
                var r = this[idx] as BoundedRange;
                if (ct == Constraint.Type.Min)
                    r.MinVal = value;
                else
                    r.MaxVal = value;
            }
        }

        public void Add (TpmConstExpr val)
        {
            Add(new SingleValue(val));
        }

        public void Add (TpmConstExpr minVal, TpmConstExpr maxVal)
        {
            Add(new BoundedRange(minVal, maxVal));
        }

        // Returns the number of added constraint values.
        // Does not process ranges.
        public int AddConstraints (string val)
        {
            if (val == "")
                return 0;
            if (val.Contains(","))
            {
                int n;
                string[] tokens = val.Split(new []{' ', ',', '{', '}'});
                for (n = 0; n < tokens.Length; ++n)
                {
                    if (tokens[n] != "")
                    {
                        Add(tokens[n]);
                    }
                }
                return n;
            }
            Add(val);
            return 1;
        }
    } // class Domain

} // namespace CodeGen

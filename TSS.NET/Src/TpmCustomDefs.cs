/*++

Copyright (c) 2010-2015 Microsoft Corporation
Microsoft Confidential

*/
using System;
using System.Diagnostics;
using System.Linq;
using System.Collections;
using System.Runtime.Serialization;
using System.Collections.Generic;
using System.Text;

namespace Tpm2Lib
{

    /// <summary>
    /// Wrapper structure for hash operations and representations used by the TPM
    /// </summary>
    [DataContract]
    [KnownType(typeof(TpmAlgId))]
    public class TpmHash : TpmStructureBase, ISignatureUnion
    {
        /// <summary>
        /// Gets the hash algorithm (can only be set at creation time)
        /// </summary>
        [MarshalAs(0)]
        public TpmAlgId HashAlg
        {
            get
            {
                return _HashAlg;
            }
            set
            {
                if (!CryptoLib.IsHashAlgorithm(value) && Tpm2._TssBehavior.Strict)
                {
                    Globs.Throw<ArgumentException>("TpmHash.HashAlg: Invalid hash algorithm ID");
                }
                _HashAlg = value;
                _HashData = new byte[CryptoLib.DigestSize(_HashAlg)];
            }
        }

        // ReSharper disable once InconsistentNaming
        [DataMember()]
        private TpmAlgId _HashAlg = TpmAlgId.Null;

        /// <summary>
        /// Get or set the data associated with this TpmHash.  The length is checked when set. 
        /// </summary>
        [MarshalAs(1, MarshalType.FixedLengthArray)]
        public byte[] HashData
        {
            get
            {
                if (_HashData.Length == CryptoLib.DigestSize(_HashAlg))
                {
                    Globs.Throw("TpmHash.HashData: Inconsistent data length");
                }
                return _HashData;
            }
            set
            {
                if (value.Length != Length)
                {
                    Globs.Throw<ArgumentException>("TpmHash.HashData: Incorrect data length");
                }
                _HashData = Globs.CopyData(value);
            }
        }

        // ReSharper disable once InconsistentNaming
        [DataMember()]
        private byte[] _HashData;

        /// <summary>
        /// Get the number of bytes of the hash output 
        /// </summary>
        public int Length { get { return _HashData.Length; } }

        /// <summary>
        /// Create a new TpmHash with no associated hash algorithm (generally this should only be used prior to object
        /// de-serialization)
        /// </summary>
        public TpmHash()
        {
            HashAlg = TpmAlgId.Null;
        }

        /// <summary>
        /// Create an all-zeroes TpmHash with the named hash algorithm
        /// </summary>
        /// <param name="hashAlgId"></param>
        public TpmHash(TpmAlgId hashAlgId)
        {
            HashAlg = hashAlgId;
        }

        public static implicit operator TpmHash(TpmAlgId hashAlgId)
        {
            return new TpmHash(hashAlgId);
        }

        /// <summary>
        /// Create a TpmHash from the provided digest and hash algorithm. The number
        /// of bytes in the digest must match the hash size.
        /// </summary>
        /// <param name="hashAlg">Hash algorithm used to compute digest</param>
        /// <param name="digest">Byte array representing digest</param>
        public TpmHash(TpmAlgId hashAlg, byte[] digest)
        {
            HashAlg = hashAlg;
            if (Length != digest.Length)
            {
                Globs.Throw<ArgumentException>("TpmHash: Digest length does not match the hash algorithm");
            }
            digest.CopyTo(_HashData, 0);
        }

        /// <summary>
        /// Create a TpmHash from the provided digest. Intended to be used by the
        /// conversion from byte[] operator to construct temporary object hash objects
        /// for the purposes of comparison.
        /// </summary>
        /// <param name="digest">Byte array representing digest</param>
        private TpmHash(byte[] digest)
        {
            _HashAlg = TpmAlgId.None;
            _HashData = digest;
        }

        public static implicit operator TpmHash(byte[] digest)
        {
            return new TpmHash(digest);
        }
        
        /// <summary>
        /// Implicit conversion of a hash object to byte-array.
        /// </summary>
        /// <param name="a"></param>
        /// <returns>Digest stored in the hash object</returns>
        public static implicit operator byte[](TpmHash hash)
        {
            return hash == null ? null : hash.HashData;
        }

        /// <summary>
        /// Implicit conversion of a hash object to a TPM algorithm.
        /// </summary>
        /// <param name="a"></param>
        /// <returns>Hash algorithm associated with the hash object</returns>
        public static implicit operator TpmAlgId(TpmHash hash)
        {
            return hash == null ? TpmAlgId.None : hash.HashAlg;
        }

        /// <summary>
        /// Returns true if the two hashes are equal, i.e. if both hash algorithms and
        /// digests are the same. When this operator is used to compare a hash object
        /// with a byte buffer representing digest, the latter is converted to a hash
        /// object with its hash algorithm set to null, which excludes the algorithms
        /// from comparison).
        /// </summary>
        /// <param name="lhs">Left hand side operand</param>
        /// <param name="rhs">Right hand side operand</param>
        /// <returns></returns>
        public static bool operator == (TpmHash lhs, TpmHash rhs)
        {
            return (object)lhs == null ? (object)rhs == null
                                       : (object)rhs != null &&
                        (lhs._HashAlg == TpmAlgId.None || rhs._HashAlg == TpmAlgId.None ||
                         lhs._HashAlg == rhs._HashAlg) &&
                        Globs.ArraysAreEqual(lhs._HashData, rhs._HashData);
        }

        /// <summary>
        /// Returns true if the two hashes are different, i.e. if hash algorithms or
        /// digests are different. When this operator is used to compare a hash object
        /// with a byte buffer representing digest, the latter is converted to a hash
        /// object with its hash algorithm set to null, which excludes the algorithms
        /// from comparison).
        /// </summary>
        /// <param name="lhs">Left hand side operand</param>
        /// <param name="rhs">Right hand side operand</param>
        /// <returns></returns>
        public static bool operator != (TpmHash lhs, TpmHash rhs)
        {
            return !(lhs == rhs);
        }

        public override bool Equals(Object obj)
        {
            return this == (TpmHash)obj;
        }

        public override int GetHashCode()
        {
            // Since the digest is already a randomized sequence of bytes, we can
            // use only the initial four bytes with the same probability of collision
            // as if we used all bytes of digest to compute 4-byte integer.
            return Globs.Mix(HashAlg.GetHashCode(), BitConverter.ToInt32(HashData, 0));
        }

        /// <summary>
        /// Return a TpmHash of specified algorithm set to all zeroes
        /// </summary>
        /// <param name="alg"></param>
        /// <returns></returns>
        public static TpmHash ZeroHash(TpmAlgId alg)
        {
            return new TpmHash(alg);
        }

        /// <summary>
        /// Return a TpmHash of the specified algorithm with value set to 0x01010101...  Note that this
        /// is not a 'real' hash value, but is the initialization value of some resettable PCR
        /// </summary>
        /// <param name="alg"></param>
        /// <returns></returns>
        public static TpmHash AllOnesHash(TpmAlgId alg)
        {
            int len = CryptoLib.DigestSize(alg);
            var bb = new byte[len];
            for (int j = 0; j < bb.Length; j++)
            {
                bb[j] = 0xFF;
            }
            return new TpmHash(alg, bb);
        }

        /// <summary>
        /// Return a new TpmHash set to the hash of the supplied data
        /// </summary>
        /// <param name="hashAlg"></param>
        /// <param name="dataToHash"></param>
        /// <returns></returns>
        public static TpmHash FromData(TpmAlgId hashAlg, byte[] dataToHash)
        {
            if (!CryptoLib.IsHashAlgorithm(hashAlg))
            {
                Globs.Throw<ArgumentException>("TpmHash.FromData: Not a hash algorithm");
            }
            return new TpmHash(hashAlg, CryptoLib.HashData(hashAlg, dataToHash));
        }

        /// <summary>
        /// Return a new TpmHash that is the hash of random data
        /// </summary>
        /// <param name="hashAlg"></param>
        /// <returns></returns>
        public static TpmHash FromRandom(TpmAlgId hashAlg)
        {
            if (!CryptoLib.IsHashAlgorithm(hashAlg))
            {
                Globs.Throw<ArgumentException>("TpmHash.FromRandom: Not a hash algorithm");
            }
            return new TpmHash(hashAlg, CryptoLib.HashData(hashAlg, Globs.GetRandomBytes((int)DigestSize(hashAlg))));
        }

        /// <summary>
        /// Return a TpmHash that is the hash of Encoding.Unicode.GetBytes(password)
        /// </summary>
        /// <param name="hashAlg"></param>
        /// <param name="password"></param>
        /// <returns></returns>
        public static TpmHash FromString(TpmAlgId hashAlg, string password)
        {
            if (!CryptoLib.IsHashAlgorithm(hashAlg))
            {
                Globs.Throw<ArgumentException>("TpmHash.FromString: Not a hash algorithm");
            }
            return new TpmHash(hashAlg, CryptoLib.HashData(hashAlg, Encoding.Unicode.GetBytes(password)));
        }

        /// <summary>
        /// Make a new TpmHash from the hash of the TPM representation of data
        /// </summary>
        /// <param name="hashAlg"></param>
        /// <param name="data"></param>
        /// <returns></returns>
        public static TpmHash FromObject(TpmAlgId hashAlg, Object data)
        {
            var newHash = new TpmHash(hashAlg);
            byte[] temp = Marshaller.GetTpmRepresentation(data);
            newHash.HashData = CryptoLib.HashData(hashAlg, temp);
            return newHash;
        }

        /// <summary>
        /// Replace the hash value with the hash of the concatenation of the current hash value and DataToExtend
        /// </summary>
        /// <param name="dataToExtend"></param>
        /// <returns></returns>
        public TpmHash Event(byte[] dataToExtend)
        {
            HashData = CryptoLib.HashData(HashAlg, HashData, CryptoLib.HashData(HashAlg, dataToExtend));
            return this;
        }

        /// <summary>
        /// Replace the hash value with the hash of the concatenation of the current value and the TPM representation 
        /// of objectToExtend
        /// </summary>
        /// <param name="objectToExtend"></param>
        /// <returns></returns>
        public TpmHash Extend(Object objectToExtend)
        {
            byte[] temp = Marshaller.GetTpmRepresentation(objectToExtend);
            HashData = CryptoLib.HashData(HashAlg, HashData, temp);
            return this;
        }

        TpmAlgId ISignatureUnion.GetUnionSelector()
        {
            return TpmAlgId.Hmac;
        }

        public TpmAlgId GetUnionSelector()
        {
            return TpmAlgId.Hmac;
        }

        /// <summary>
        /// Return the length of the output of the hash function in bytes
        /// </summary>
        /// <param name="hashAlg"></param>
        /// <returns></returns>
        public static ushort DigestSize(TpmAlgId hashAlg)
        {
            return (ushort)CryptoLib.DigestSize(hashAlg);
        }

        /// <summary>
        /// Return the hash function block size in bytes
        /// </summary>
        /// <param name="hashAlg"></param>
        /// <returns></returns>
        public static ushort BlockSize(TpmAlgId hashAlg)
        {
            return (ushort)CryptoLib.BlockSize(hashAlg);
        }

        /// <summary>
        /// Convert to a Tpm2bDigest
        /// </summary>
        /// <param name="h"></param>
        /// <returns></returns>
        public static implicit operator Tpm2bDigest(TpmHash h)
        {
            var d = new Tpm2bDigest(h.HashData);
            return d;
        }

        internal override void ToStringInternal(TpmStructPrinter p)
        {
            p.Print("HashAlg", "TpmAlgId", HashAlg);
            p.Print("HashData", "byte", HashData);
        }
    }

    /// <summary>
    /// AuthValue encapsulates common usage of TPM authorization data.  AuthValues are variable length byte-arrays of 
    /// zero or more bytes.  The TPM removes trailing zeroes when deciding equality.  Proof-of-knowledge of an authorization 
    /// value can be performed by providing the TPM with the value in plaintext, or by using it as an HMAC key.
    /// </summary>
    [DataContract]
    public class AuthValue : TpmStructureBase
    {
        /// <summary>
        /// The auth-value
        /// </summary>
        public byte[] AuthVal { get; set; }

        internal static bool IsNull(AuthValue auth)
        {
            return auth == null || auth.AuthVal.Length == 0;
        }

        /// <summary>
        /// Create an AuthValue from the hash of the string.  See TpmHash.FromString for the transformation used.
        /// </summary>
        /// <param name="hashAlg"></param>
        /// <param name="password"></param>
        /// <returns></returns>
        public static AuthValue FromString(TpmAlgId hashAlg, string password)
        {
            return new AuthValue(TpmHash.FromString(hashAlg, password));
        }

        /// <summary>
        /// Create an zero-length AuthValue 
        /// </summary>
        public AuthValue()
        {
            AuthVal = new byte[0];
        }

        /// <summary>
        /// Create an AuthValue with the specified value.  Note that trailing zeros are not removed.
        /// </summary>
        /// <param name="val"></param>
        public AuthValue(byte[] auth)
        {
            AuthVal = Globs.CopyData(auth);
        }

        /// <summary>
        /// Returns true if the two arguments either are both null references or
        /// contain equal authorization values.
        /// </summary>
        /// <param name="lhs">Left hand side operand</param>
        /// <param name="rhs">Right hand side operand</param>
        /// <returns></returns>
        public static bool operator == (AuthValue lhs, AuthValue rhs)
        {
            return (object)lhs == null ? (object)rhs == null
                                       : (object)rhs != null &&
                                         Globs.ArraysAreEqual(lhs.AuthVal, rhs.AuthVal);
        }

        /// <summary>
        /// Returns true if one of the two arguments is a null references while the
        /// other is not, or if they contain different authorization values.
        /// </summary>
        /// <param name="lhs">Left hand side operand</param>
        /// <param name="rhs">Right hand side operand</param>
        /// <returns></returns>
        public static bool operator != (AuthValue lhs, AuthValue rhs)
        {
            return !(lhs == rhs);
        }

        public override bool Equals(Object obj)
        {
            if (obj == null || GetType() != obj.GetType())
                return false;
            return this == (AuthValue)obj;
        }

        public override int GetHashCode()
        {
            // As auth value is expected to be a randomized sequence of bytes, we can
            // use only the initial four bytes with the same probability of collision
            // as if we computed its 4 byte digest.
            return BitConverter.ToInt32(AuthVal, 0);
        }

        /// <summary>
        /// Creates an auth value comprising the specified number of random bytes.
        /// Since the TPM removes trailing zeros, this routine makes sure that the last
        /// byte is non-zero.
        /// </summary>
        /// <param name="numBytes"></param>
        /// <returns></returns>
        public static AuthValue FromRandom(int numBytes)
        {
            if (numBytes == 0)
            {
                return new AuthValue(new byte[0]);
            }

            byte[] trial = null;
            do {
                trial = Globs.GetRandomBytes(numBytes);
            } while (trial[numBytes - 1] == 0);
            return new AuthValue(trial);
        }

        /// <summary>
        /// Auto-conversion from byte-array
        /// </summary>
        /// <param name="x"></param>
        /// <returns></returns>
        public static implicit operator AuthValue(byte[] x)
        {
            return new AuthValue(x);
        }

        /// <summary>
        /// Auth-conversion to byte-array
        /// </summary>
        /// <param name="a"></param>
        /// <returns></returns>
        public static implicit operator byte[](AuthValue a)
        {
            return a == null ? null : a.AuthVal;
        }
    }

    public partial class RsaParms
    {
        /// <summary>
        /// Default exponent (65537)
        /// </summary>
        public static byte[] DefaultExponent = {1, 0, 1};
    }

    /// <summary>
    /// This class is a crutch for C#'s inability to support implicit conversions
    /// between derived and base classes.
    /// Data structures with members of Types derived from TpmHandle declare those
    /// members as private, expose them via properties of type TpmHandleX, and use
    /// TpmHandleX as the type of the sorresponding constructor arguments.
    /// </summary>
    public class TpmHandleX
    {
        internal TpmHandle Handle;

        public TpmHandleX(TpmHandle h) { Handle = h; }

        public byte[] Name
        {
            get { return Handle.Name; }
            set { Handle.Name = value; }
        }

        public byte[] Auth
        {
            get { return Handle.Auth; }
            set { Handle.Auth = value; }
        }

        public static implicit operator TpmHandleX (TpmHandle from) { return new TpmHandleX(from); }
        public static implicit operator TpmHandle (TpmHandleX from) { return from.Handle; }
    }

#if false
    // Example of handle based TPMI typedef implementation
    [DataContract]
    [SpecTypeName("TPMI_RH_NV_INDEX")]
    public class TpmiRhNvIndex : TpmHandle
    {
        public TpmiRhNvIndex() : base() {}
        public TpmiRhNvIndex(uint h) : base(h) {}
        public TpmiRhNvIndex(TpmHandle h) : base(h, TpmHandle.Bind.ByRef) {}

        public static implicit operator TpmiRhNvIndex (TpmHandleX from)
        {
            return new TpmiRhNvIndex(from.Handle);
        }
        public static implicit operator TpmHandleX (TpmiRhNvIndex from)
        {
            return new TpmHandleX(from.BoundHandle != null &&
                                  from.BoundHandle.handle == from.handle ? from.BoundHandle
                                                                         : (TpmHandle)from);
        }
    }
#endif

    /// <summary>
    /// TpmHandle represents TPM-loaded entities (keys, etc.), well-know objects like the owner and PCR, and NV-entries.
    /// TpmHandle can also contain the name of the referenced entity (for authorization hmacs, etc.)
    /// </summary>
    public partial class TpmHandle
    {
        private byte[] _Name;
        private AuthValue _AuthValue;

        /// <summary>
        /// Get or set the name of the object associated with this handle.  Note that this is only relevant for objects: other
        /// entities have names derived from the handle value.
        /// </summary>
        public byte[] Name
        {
            set { SetName(value); }
            get { return GetName(); }
        }

        /// <summary>
        /// Get the TPM name of the associated entity.
        /// 
        /// If the entity is a transient object, persistent object or NV index, the
        /// name must have been previously set explicitly by the caller (by means of
        /// SetName() or GetName(Tpm2 tpm) methods) or implicitly by the framework
        /// (when an object is created by means of CreatePrimary, CreateLoaded or
        /// Create command).
        /// 
        /// Otherwise the name is a 4-byte TPM representation of the handle value.
        /// </summary>
        public byte[] GetName()
        {
            Ht ht = GetType();
            switch (ht)
            {
                case Ht.Transient:
                case Ht.Persistent:
                case Ht.NvIndex:
                    return _Name;
                case Ht.Pcr:
                case Ht.HmacSession:
                case Ht.PolicySession:
                case Ht.Permanent:
                    return Marshaller.GetTpmRepresentation(handle);
                default:
                    return null;
            }
        }

        /// <summary>
        /// Returns the cached name of an entity referenced by this handle. If the
        /// name is not cached yet, retrieves it from the TPM (for a transient or
        /// persistent object, or NV index) or computes it (for session, PCR or
        /// permanent handles).
        /// </summary>
        public byte[] GetName(Tpm2 tpm)
        {
            Ht ht = GetType();
            if (_Name == null)
            {
                if (ht == Ht.NvIndex)
                {
                    tpm.NvReadPublic(this, out _Name);
                    return _Name;
                }
                if (ht == Ht.Transient || ht == Ht.Persistent)
                {
                    byte[] qName;
                    tpm.ReadPublic(this, out _Name, out qName);
                    return _Name;
                }
            }
            return GetName();
        }

        /// <summary>
        /// Associates the name with the handle. Only needed for transient, persistent
        /// and NV handles.
        /// Normally this association is done automatically either by TPM commands
        /// producing the corresponding handle, or when the handle is passed as a
        /// parameter to a command requiring HMAC authorization (the name is implicitly
        /// requested from the TPM by means of TPM2_ReadPublic or TPM2_NV_ReadPublic
        /// commands).
        /// Thus this method has to be used only either when Tpm2 object is in the strict
        /// mode (i.e. it is prohibited to issue commands not explicitly requested by
        /// the user), or for the sake of performance optimization (if the client code
        /// has the name pre-computed).
        /// </summary>
        /// <param name="name"></param>
        /// <returns>Reference to this object, which can be used for chaining.</returns>
        public TpmHandle SetName(byte[] name)
        {
            _Name = Globs.CopyData(name);
            return this;
        }

        /// <summary>
        /// Associates authorization value with the handle. 
        /// This association is done automatically by TPM commands producing the
        /// corresponding handle or changing object's auth value.
        /// However on many occasions the library does not have access to the auth
        /// value at any moment before it is required for authorizing access to the
        /// handle. Notably, when an externally created key is imported, pre-existing
        /// NV index or persistent object is used, SetAuth() is required to associate
        /// auth value with the handle.
        /// </summary>
        /// <param name="auth"></param>
        /// <returns>Reference to this object, which can be used for chaining.</returns>
        public TpmHandle SetAuth(AuthValue auth)
        {
            Auth = auth;
            return this;
        }

        /// <summary>
        /// Get or set an associated authorization value for this handle.  If this handles is 
        /// subsequently used in a command that requires authorization, this auth-value will be used
        /// (in a PWAP or HMAC session, depending on context)
        /// </summary>
        public byte[] Auth
        {
            get
            {
                return _AuthValue;
            }
            set
            {
                _AuthValue = Globs.CopyData(value);
            }
        }

        // This is the handle that was used to create this object, and which needs
        // to receive auth/name updates whenever they are changed for this object.
        // The term 'bound' in this context has nothing to do with TPM 2.0 bound
        // handles concept.
        internal TpmHandle BoundHandle;

        internal enum Bind
        {
            ByRef
        }

        // Constructs a new TpmHandle object using the numeric handle value from 'src',
        // and binds 'src' so that it receives all auth/name values updates that happen
        // on this object.
        internal TpmHandle(TpmHandle src, Bind bind) : this(src)
        {
            BoundHandle = src;
        }

        /// <summary>
        /// Create a reserved TPM handle.
        /// </summary>
        /// <param name="reservedHandle"></param>
        public TpmHandle(TpmRh reservedHandle)
        {
            handle = (uint)reservedHandle;
        }

        /// <summary>
        /// Create a handle of the given type with the given numerical value.
        /// </summary>
        /// <param name="reservedHandle"></param>
        public TpmHandle (Ht handleType, uint offset)
        {
            new TpmHandle(((uint)handleType << 24) + offset);
        }

        /// <summary>
        /// Returns true if the two arguments either are both null references or
        /// encapsulate the same TPM handle.
        /// </summary>
        /// <param name="lhs">Left hand side operand</param>
        /// <param name="rhs">Right hand side operand</param>
        /// <returns></returns>
        public static bool operator == (TpmHandle lhs, TpmHandle rhs)
        {
            return  (object)lhs == null ? (object)rhs == null
                                        : (object)rhs != null && (lhs.handle == rhs.handle);
        }

        /// <summary>
        /// Returns true if one of the two arguments is a null references while the
        /// other is not, or if they contain different TPM handles.
        /// </summary>
        /// <param name="lhs">Left hand side operand</param>
        /// <param name="rhs">Right hand side operand</param>
        /// <returns></returns>
        public static bool operator != (TpmHandle lhs, TpmHandle rhs)
        {
            return !(lhs == rhs);
        }

        public override bool Equals(Object obj)
        {
            return this == (TpmHandle)obj;
        }

        public override int GetHashCode()
        {
            return (int)handle;
        }

        /// <summary>
        /// Calculate the qualified name of an object presumed loaded under the provided ancestral chain 
        /// in a given hierarchy.
        /// </summary>
        /// <param name="hierarchyHandle"></param>
        /// <param name="children"></param>
        /// <returns></returns>
        public static byte[] GetQualifiedName(TpmHandle hierarchyHandle, TpmPublic[] children)
        {
            byte[] runningName = Marshaller.GetTpmRepresentation(hierarchyHandle);
            foreach (TpmPublic pub in children)
            {
                byte[] thisName = pub.GetName();
                runningName = Globs.Concatenate
                    (
                     Marshaller.GetTpmRepresentation(pub.nameAlg),
                     CryptoLib.HashData(pub.nameAlg, new[] {runningName, thisName})
                    );
            }
            return runningName;
        }

        /// <summary>
        /// return the handle minus the handle-type (top byte) field
        /// </summary>
        /// <returns></returns>
        public uint GetOffset()
        {
            return handle & 0xFFffFF;
        }

        public static ParametrizedHandle operator + (TpmHandle h, object param)
        {
            return new ParametrizedHandle(h) + param;
        }

        /// <summary>
        /// Return a handle for the PCR of specified index
        /// </summary>
        /// <param name="pcrIndex"></param>
        /// <returns></returns>
        public static TpmHandle Pcr(uint pcrIndex)
        {
            return new TpmHandle((TpmRh)((uint)Ht.Pcr << 24) + pcrIndex);
        }

        public static TpmHandle Pcr(int pcrIndex)
        {
            return new TpmHandle((uint)pcrIndex);
        }

        /// <summary>
        /// Create a persistent handle given an index into the persistent handle range
        /// </summary>
        /// <param name="handleIndex"></param>
        /// <returns></returns>
        public static TpmHandle Persistent(uint handleIndex)
        {
            return new TpmHandle((TpmRh)((uint)Ht.Persistent << 24) + handleIndex);
        }

        /// <summary>
        /// Return a handle to the the specified NV index
        /// </summary>
        /// <param name="slotIndex"></param>
        /// <returns></returns>
        public static TpmHandle NV(uint slotIndex)
        {
            return new TpmHandle(((uint)Ht.NvIndex << 24) + slotIndex);
        }

        /// <summary>
        /// Return a handle to the the specified NV index
        /// </summary>
        /// <param name="slotIndex"></param>
        /// <returns></returns>
        public static TpmHandle NV(int slotIndex)
        {
            return NV((uint)slotIndex);
        }

        /// <summary>
        /// Create a HMAC handle given an index into the HMAC handle range
        /// </summary>
        /// <param name="handleIndex"></param>
        /// <returns></returns>
        public static TpmHandle HmacSession(uint handleIndex)
        {
            return new TpmHandle((TpmRh)((uint)Ht.HmacSession << 24) + handleIndex);
        }

        /// <summary>
        /// Get uint value representing the first handle in the range dedicated to
        /// the handles of the given type.
        /// </summary>
        /// <param name="rangeType"></param>
        /// <returns></returns>
        public static uint GetFirst(Ht rangeType)
        {
            return (uint)rangeType >> 24;
        }

        /// <summary>
        /// Get the length (number of handles) in the range dedicated to the handles
        /// of the given type.
        /// </summary>
        /// <param name="rangeType"></param>
        /// <returns></returns>
        public static uint GetRangeLength(Ht rangeType)
        {
            if (rangeType == Ht.NvIndex)
            {
                return 0x00FFFFFF;
            }
            return 1U << 24;
        }

        public static implicit operator TpmHandle(TpmRh reservedHandle)
        {
            return new TpmHandle(reservedHandle);
        }
        
        static public TpmHandle RhOwner { get { return new TpmHandle(TpmRh.Owner); } }

        public static TpmHandle RhNull { get { return new TpmHandle(TpmRh.Null); } }

        public static TpmHandle RhPlatform { get { return new TpmHandle(TpmRh.Platform); } }

        public static TpmHandle RhPlatformNv { get { return new TpmHandle(TpmRh.PlatformNv); } }

        public static TpmHandle RhEndorsement { get { return new TpmHandle(TpmRh.Endorsement); } }

        public static TpmHandle RhLockout { get { return new TpmHandle(TpmRh.Lockout); } }

        public static TpmHandle RhInvalid { get { return new TpmHandle(uint.MaxValue); } }

        internal static bool IsNull(TpmHandle h)
        {
            return h == null || h.handle == (uint)TpmRh.Null || h.handle == (uint)TpmRh.None;
        }

        internal bool IsSession()
        {
            Ht range = GetType();
            return range == Ht.HmacSession || range == Ht.PolicySession || handle == (uint)TpmRh.TpmRsPw;
        }

        new public Ht GetType()
        {
            var rangeVal = (byte)(handle >> 24);
            return (Ht)rangeVal;
        }

        public uint GetIndex()
        {
            uint indexVal = (handle & ~((0xff) << 24));
            return indexVal;
        }
    }

    // PcrSelect contains {sizeOfSelect, byte[] selection}
    public partial class PcrSelect
    {
        public const int DefaultPcrCount = 24;
        private int PcrCount = DefaultPcrCount;

        public PcrSelect(IEnumerable<uint> indices, int pcrCount = DefaultPcrCount)
        {
            PcrCount = pcrCount;
            Init();
            foreach (uint t in indices)
            {
                SelectPcr(t);
            }
        }

        public void SetNumPcrs(int numPcrs = DefaultPcrCount)
        {
            PcrCount = numPcrs;
            pcrSelect = new byte[(PcrCount + 7) / 8];
        }

        public void SelectPcr(uint pcrNumber)
        {
            Init();
            int byteNum = (int)pcrNumber / 8;
            var bitNum = (int)(pcrNumber % 8);
            pcrSelect[byteNum] |= (byte)(1 << bitNum);
        }

        public bool IsPcrSelected(uint pcrNumber)
        {
            Init();
            int byteNum = (int)pcrNumber / 8;
            var bitNum = (int)(pcrNumber % 8);
            return (pcrSelect[byteNum] & (byte)(1 << bitNum)) != 0;
        }

        private void Init()
        {
            if (pcrSelect == null || pcrSelect.Length == 0)
            {
                pcrSelect = new byte[(PcrCount + 7) / 8];
            }
        }

        public int[] GetSelectedPcrs()
        {
            uint count = 0;
            for (uint j = 0; j < pcrSelect.Length; j++)
            {
                if (IsPcrSelected(j))
                {
                    count++;
                }
            }

            var ret = new int[count];
            int c2 = 0;
            for (uint j = 0; j < pcrSelect.Length; j++)
            {
                if (IsPcrSelected(j))
                {
                    ret[c2++] = (int)j;
                }
            }
            return ret;
        }
    }

    // PcrSelection is {AlgId, sizeOfSelect, byte[] selection}
    public partial class PcrSelection
    {
        // Number of PCRs available in the TPM
        public static ushort MaxPcrs = 24;

        private uint PcrCount = 0;

        public PcrSelection(TpmAlgId hashAlg, uint pcrCount = 0)
        {
            Init(hashAlg, pcrCount);
        }

        public PcrSelection(TpmAlgId hashAlg, IEnumerable<uint> indices, uint pcrCount = 0)
        {
            Init(hashAlg, pcrCount);
            foreach (uint t in indices)
            {
                SelectPcr(t);
            }
        }

        private void Init(TpmAlgId hashAlg, uint maxPcrs)
        {
            hash = hashAlg;
            PcrCount = maxPcrs == 0 ? MaxPcrs : maxPcrs;
            pcrSelect = new byte[(PcrCount + 7) / 8];
        }

        // Makes sure that the data structure generated by TPM is fully initialized
        private void FinishInit()
        {
            if (pcrSelect == null || pcrSelect.Length == 0)
            {
                if (PcrCount == 0)
                {
                    PcrCount = MaxPcrs;
                }
                pcrSelect = new byte[(PcrCount + 7) / 8];
            }
            else
            {
                PcrCount = (uint)(pcrSelect.Length * 8);
            }
        }

        public PcrSelection Clone()
        {
            var sel = new PcrSelection(hash);
            pcrSelect.CopyTo(sel.pcrSelect, 0);
            return sel;
        }

        // Unselect in this object PCRs selected in rhs operand.
        // Returns true, if the result is non-empty.
        public bool Clear(PcrSelection rhs)
        {
            FinishInit();
            bool res = false;
            pcrSelect = pcrSelect.Zip(rhs.pcrSelect,
                                      (x, y) => { x &= (byte)~y; res |= x != 0; return x; })
                                 .ToArray();
            return res;
        }

        public static PcrSelection SinglePcr(TpmAlgId bank, uint index)
        {
            return new PcrSelection(bank, new[] {index});
        }

        public static PcrSelection[] SinglePcrArray(TpmAlgId bank, uint index)
        {
            return new[] {new PcrSelection(bank, new[] {index})};
        }

        public static PcrSelection[] SinglePcrArray(TpmAlgId bank, int index)
        {
            return SinglePcrArray(bank, (uint)index);
        }

        public static PcrSelection FullPcrBank(TpmAlgId hashAlg, uint pcrCount = 0)
        {
            uint[] pcrs = new uint[pcrCount == 0 ? MaxPcrs : pcrCount];
            for (int i = 0; i < pcrCount; ++i)
            {
                pcrs[i] = (uint)i;
            }
            return new PcrSelection(hashAlg, pcrs, pcrCount);
        }

        public static PcrSelection[] FullPcrBanks(IEnumerable<TpmAlgId> hashAlgs, uint pcrCount = 0)
        {
            var allBanks = new PcrSelection[hashAlgs.Count()];
            int i = 0;
            foreach (var hashAlg in hashAlgs)
            {
                allBanks[i++] = PcrSelection.FullPcrBank(hashAlg, pcrCount == 0 ? MaxPcrs : pcrCount);
            }
            return allBanks;
        }

        public void SelectPcr(uint pcrNumber)
        {
            FinishInit();
            Debug.Assert(pcrNumber < PcrCount);
            uint byteNum = pcrNumber / 8;
            uint bitNum = pcrNumber % 8;
            byte mask = (byte)(1 << (int)bitNum);
            pcrSelect[byteNum] |= mask;
        }

        public bool IsPcrSelected(uint pcrNumber)
        {
            FinishInit();
            return Globs.IsBitSet(pcrSelect, (int)pcrNumber);
        }

        public int NumPcrsSelected()
        {
            FinishInit();
            int count = 0;
            for (uint i = 0; i < PcrCount; i++)
            {
                if (IsPcrSelected(i))
                {
                    ++count;
                }
            }
            return count;
        }

        public uint[] GetSelectedPcrs()
        {
            FinishInit();
            var selectedPcr = new uint[NumPcrsSelected()];
            for (uint i = 0, count = 0; i < PcrCount; ++i)
            {
                if (IsPcrSelected(i))
                {
                    selectedPcr[count++] = i;
                }
            }
            return selectedPcr;
        }
    } // partial class PcrSelection

    public partial class PcrValue
    {
        // PcrValue contains {uint Index, TpmHash Value}
        public TpmAlgId AlgId
        {
            get
            {
                return value.HashAlg;
            }
        }

        public TpmHash Event(byte[] dataToExtend)
        {
            return value.Event(dataToExtend);
        }

    }

    // todo - 
    /// <summary>
    /// Collection of PcrValues.  Methods to convert to and from commonly used PCR structures.   For example
    /// TPML_PCR_SELECTION
    /// </summary>
    [DataContract]
    public class PcrValueCollection : TpmStructureBase
    {
        public PcrValue[] Values;

        public PcrValueCollection()
        {
            Values = new PcrValue[0];
        }

        public PcrValueCollection(PcrValue oneVal)
        {
            Values = new PcrValue[1];
            Values[0] = new PcrValue(oneVal);
        }

        public PcrValueCollection(PcrValue[] vals)
        {
            Values = Globs.CopyArray<PcrValue>(vals);
        }

        public PcrValueCollection(PcrSelection[] pcrSelection, Tpm2bDigest[] values)
        {
            // Calculate how many indivudal PCRs we have
            int count = pcrSelection.Sum(sel => sel.GetSelectedPcrs().Length);
            Values = new PcrValue[count];
            // Now set the PcrValue[] based on the selection and values
            count = 0;
            foreach (PcrSelection sel in pcrSelection)
            {
                foreach (uint pcrNum in sel.GetSelectedPcrs())
                {
                    Values[count] = new PcrValue(pcrNum, new TpmHash(sel.hash, values[count].buffer));
                    count++;
                }
            }
        }

        // Returns the PcrSelection[] for the current PcrValueCollection
        public PcrSelection[] GetPcrSelectionArray()
        {
            // find all referenced algorithms
            var referencedAlgs = new List<TpmAlgId>();
            foreach (PcrValue v in Values)
            {
                // todo reference or value contains?
                if (!referencedAlgs.Contains(v.value.HashAlg))
                {
                    referencedAlgs.Add(v.value.HashAlg);
                }
            }
            var selection = new PcrSelection[referencedAlgs.Count];
            int count = 0;
            foreach (TpmAlgId algId in referencedAlgs)
            {
                selection[count++] = new PcrSelection(algId, new uint[0]);
            }
            uint bankNum = 0;
            foreach (TpmAlgId algId in referencedAlgs)
            {
                foreach (PcrValue val in Values)
                {
                    if (val.value.HashAlg != algId)
                    {
                        continue;
                    }
                    // Do we already have a PcrValue with the same {alg, pcrNum?}
                    if (selection[bankNum].IsPcrSelected(val.index))
                    {
                        Globs.Throw("PcrValueCollection.GetPcrSelectionArray: PCR is referenced more than once");
                    }
                    // Else select it
                    selection[bankNum].SelectPcr(val.index);
                }
                bankNum++;
            }
            return selection;
        }

        /// <summary>
        /// Returns a TPML_PCR_SELECTION (PcrSelectionArray) suitable for marshalling
        /// </summary>
        /// <returns></returns>
        public PcrSelectionArray GetTpmlPcrSelection()
        {
            var sel = new PcrSelectionArray(GetPcrSelectionArray());
            return sel;
        }

        /// <summary>
        /// Get the hash of the concatenation of the values in the array order defined by the PcrSelection[] 
        /// returned from GetPcrSelectionArray.
        /// </summary>
        /// <param name="hashAlg"></param>
        /// <returns></returns>
        public TpmHash GetSelectionHash(TpmAlgId hashAlg)
        {
            var m = new Marshaller();
            PcrSelection[] selections = GetPcrSelectionArray();
            foreach (PcrSelection sel in selections)
            {
                uint[] pcrIndices = sel.GetSelectedPcrs();
                foreach (uint index in pcrIndices)
                {
                    PcrValue v = GetSpecificValue(sel.hash, index);
                    m.Put(v.value.HashData, "hash");
                }
            }
            var valueHash = new TpmHash(hashAlg, CryptoLib.HashData(hashAlg, m.GetBytes()));
            return valueHash;
        }

        public PcrValue GetSpecificValue(TpmAlgId alg, uint pcrIndex)
        {
            foreach (PcrValue v in Values)
            {
                if (v.index == pcrIndex && v.value.HashAlg == alg)
                {
                    return v;
                }
            }
            Globs.Throw("PcrValueCollection.GetSpecificValue: PCR not found");
            return new PcrValue();
        }
    }

    public partial class Attest
    {
        public static implicit operator Attest(byte[] buf)
        {
            return Marshaller.FromTpmRepresentation<Attest>(buf);
        }
    }

    public partial class SymDef : TpmStructureBase
    {
        ///<param name = "theAlg">TpmAlgId.Xor or TpmAlgId.Null</param>
        ///<param name = "hmacHash">Hash algorithm used with XOR algorithm in HMAC computation</param>
        public SymDef(TpmAlgId theAlg, TpmAlgId hmacHash)
        {
            Algorithm = theAlg;
            KeyBits = 0;
            Mode = hmacHash;
        }

        internal override void ToNet(Marshaller m)
        {
            m.Put(Algorithm, "algorithm");
            switch (Algorithm)
            {
                case TpmAlgId.None:
                case TpmAlgId.Null:
                    return;
                case TpmAlgId.Xor:
                    m.Put(Mode, "hash");
                    break;
                case TpmAlgId.Aes:
                case TpmAlgId.Tdes:
                    m.Put(KeyBits, "keyBits");
                    m.Put(Mode, "mode");
                    break;
                default:
                    Globs.Throw<NotImplementedException>("SymDef.ToNet: Unknown algorithm");
                    m.Put(KeyBits, "keyBits");
                    m.Put(Mode, "mode");
                    break;
            }
        }

        internal override void ToHost(Marshaller m)
        {
            Algorithm = m.Get<TpmAlgId>();
            switch (Algorithm)
            {
                case TpmAlgId.None:
                case TpmAlgId.Null:
                    return;
                case TpmAlgId.Xor:
                    KeyBits = 0;
                    Mode = m.Get<TpmAlgId>();
                    break;
                case TpmAlgId.Aes:
                    KeyBits = m.Get<ushort>();
                    Mode = m.Get<TpmAlgId>();
                    break;
                default:
                    Globs.Throw<NotImplementedException>("SymDef.ToHost: Unknown algorithm");
                    break;
            }
        }
    } // class SymDef

    public partial class SymDefObject : IPublicParmsUnion
    {
        /// <summary>
        /// Return a SymDefObject with TpmAlgId.Null
        /// </summary>
        /// <returns></returns>
        public static SymDefObject NullObject()
        {
            return new SymDefObject(TpmAlgId.Null, 0, TpmAlgId.Null);
        }

        /// <summary>
        /// SymDef binary format is the same as SymDefObject and is used in some TPM method calls
        /// </summary>
        /// <param name="symmDefObject"></param>
        /// <returns></returns>
        public static implicit operator SymDef(SymDefObject src)
        {
            if (src == null)
            {
                return null;
            }
            return Marshaller.FromTpmRepresentation<SymDef>(Marshaller.GetTpmRepresentation(src));
        }

        /// <summary>
        /// SymDef binary format is the same as SymDefObject and is used in some TPM method calls
        /// </summary>
        /// <param name="symmDef"></param>
        /// <returns></returns>
        public static implicit operator SymDefObject(SymDef src)
        {
            if (src == null)
            {
                return null;
            }
            return Marshaller.FromTpmRepresentation<SymDefObject>(Marshaller.GetTpmRepresentation(src));
        }

        public TpmAlgId GetUnionSelector()
        {
            return TpmAlgId.Symcipher;
        }

        internal override void ToNet(Marshaller m)
        {
            if (Algorithm == TpmAlgId.Xor)
            {
                Globs.Throw<NotImplementedException>("SymDefObject.ToNet: XOR is not supported");
            }
            m.Put(Algorithm, "algorithm");
            if (Algorithm == TpmAlgId.None || Algorithm == TpmAlgId.Null)
            {
                return;
            }
            m.Put(KeyBits, "keyBits");
            m.Put(Mode, "mode");
        }

        internal override void ToHost(Marshaller m)
        {
            Algorithm = (TpmAlgId)m.Get(typeof (TpmAlgId), "algorithm");
            if (Algorithm == TpmAlgId.None || Algorithm == TpmAlgId.Null)
            {
                return;
            }
            KeyBits = (ushort)m.Get(typeof (ushort), "keyBits");
            Mode = (TpmAlgId)m.Get(typeof (TpmAlgId), "mode");
        }
    } // class SymDefObject

    public class TpmHashCheck
    {
        // Create a new null TpmHashCheck
        public static TkHashcheck NullHashCheck()
        {
            return new TkHashcheck(TpmRh.Null, new byte[0]);
        }

    }

    public partial class SensitiveCreate
    {
        public static implicit operator byte[](SensitiveCreate src)
        {
            return src.data;
        }
    }

    public partial class EccPoint
    {
        /// <summary>
        /// Returns true if the two ECC points are equal, i.e. if both coordinates
        /// are pairwise equal.
        /// </summary>
        /// <param name="lhs">Left hand side operand</param>
        /// <param name="rhs">Right hand side operand</param>
        public static bool operator == (EccPoint lhs, EccPoint rhs)
        {
            return (object)lhs == null ? (object)rhs == null
                                       : (object)rhs != null &&
                                         Globs.ArraysAreEqual(lhs.x, rhs.x) &&
                                         Globs.ArraysAreEqual(lhs.y, rhs.y);
        }

        /// <summary>
        /// Returns true if the two ECC points are different
        /// </summary>
        /// <param name="lhs">Left hand side operand</param>
        /// <param name="rhs">Right hand side operand</param>
        public static bool operator != (EccPoint lhs, EccPoint rhs)
        {
            return !(lhs == rhs);
        }

        public override bool Equals(Object obj)
        {
            return this == (EccPoint)obj;
        }

        public override int GetHashCode()
        {
            // As values of ECC points are randomized sequences of bytes, it is
            // possible to use only the initial four bytes with the same probability
            // of collision as when a 4 byte digest of the whole value is computed.
            return BitConverter.ToInt32(x, 0);
        }
    } // partial class EccPoint

    public class ParametrizedHandle
    {
        internal TpmHandle Handle;
        internal ArrayList Params;

        public ParametrizedHandle (TpmHandle h)
        {
            Handle = h;
            Params = new ArrayList();
        }

        public static ParametrizedHandle operator + (ParametrizedHandle ph, object param)
        {
            ph.Params.Add(param);
            return ph;
        }
    }

} // namespace Tpm2Lib

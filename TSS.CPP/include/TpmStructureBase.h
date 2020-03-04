/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once

#include "TpmHelpers.h"

_TPMCPP_BEGIN

///<summary>Serialization format enumeration.</summary>
enum class SerializationType {
    None,
    Text,
    Xml,
    JSON
};

enum class TpmTypeId;

///<summary>Base class for all TPM structures.</summary>
class _DLLEXP_ TpmStructureBase {
    public:
        ///<summary>Base class for all TPM structures.</summary>
        TpmStructureBase() {}
        virtual ~TpmStructureBase() {}

        ///<summary>Returns the TPM binary-form representation of this structure.</summary>
        ByteVec ToBuf() const;

        ///<summary>Sets this structure based on the TPM representation in buf.</summary>
        void FromBuf(const ByteVec& buf);

        ///<summary>Creates a new instance of the TPM structure specified in tp.</summary>
        static TpmStructureBase *FromBuf(const ByteVec& bufToBeRead, TpmTypeId tp);

        ///<summary>Returns the string representation of the structure. If !precise then
        /// arrays are truncated for readability (useful for interactive debugging).</summary>
        string ToString(bool precise = true);

        ///<summary>Serialize the object to text, JSON, XML-etc.</summary>
        string Serialize(SerializationType serializationFormat);

        ///<summary>Deserialize from JSON (other formats TBD)</summary>
        bool Deserialize(SerializationType serializationFormat, string inBuf);

        ///<summary>Test for value equality</summary>
        bool operator==(const TpmStructureBase& rhs) const;

        ///<summary>Test for value inequality</summary>
        bool operator!=(TpmStructureBase& rhs) const;

        ///<summary>Get a type-identifier for this structure</summary>
        virtual TpmTypeId GetTypeId() const;

        // TODO: Remove.
        //virtual bool operator==(const TpmStructureBase& r){ _ASSERT(FALSE); return false; }
        //virtual bool operator!=(const TpmStructureBase& r){ _ASSERT(FALSE); return false; }

        // Needed for STL/DLL
        virtual bool operator<(const TpmStructureBase& r) {
            return true;
        }

        // TODO: Remove.
        // template<TpmStructureBase T>
        // auto T(T& _x) -> typename T{ return T(x); }

    protected:
        ///<summary>Add this struct to the outBuf.</summary>
        void MarshallInternal(class OutByteBuf& outBuf) const;
        bool NonDefaultMarshall(OutByteBuf& outBuf) const;

        ///<summary>Hydrate *this from the current position in the buf.</summary>
        void FromBufInternal(class InByteBuf& buf);
        bool FromBufSpecial(class InByteBuf& buf, TpmTypeId tp);

        ///<summary>Get the expected length of the array in fieldNum/field.  Array-lengths
        // are usually explicit, but in some cases (e.g. hashes) the array-len must be
        /// determined from the TPM_ALG_ID</summary>
        UINT32 GetArrayLen(class TpmTypeInfo& fields, class MarshalInfo& field);

        ///<summary>Make a new instance of the specified struct or union type using the default
        /// constructor. If pointerToUnion is not TpmTypeId.Null then also return the pointer
        /// dynacast to the provided union type.</summary>
        static TpmStructureBase* UnionFactory(TpmTypeId id, TpmTypeId dynacastType, void* pointerToUnion);

        ///<summary>This will be overriden in derived classes that are optional in TPM inputs.
        /// If the marshaller sees a null element it will not be marshalled.  Such elements are
        /// length-preceded, and the length will be set to zero.</summary>
        virtual bool NullElement() const {
            return false;
        }

        ///<summary>Provides raw (binary) access to structure members.
        /// If arrayIdex==-1, the address of the element at elementNum is returned. 
        /// If the element is an array:
        ///   a) the current arraySize is also returned,
        ///   b) if newArraySize!=-1, the array is resized accordingly.
        /// If arrayIndex!=-1, then the memory address of the corresponding element is returned.
        /// If the object is a struct or union then a pointer to its TpmStructureBase base class
        /// is also returned (it may not coinside with the element address because of vtbl)
        /// </summary>
        virtual void *ElementInfo(int elementNum,
                                  int arrayIndex,
                                  int& arraySize,
                                  TpmStructureBase*& pStruct,
                                  int newArraySize) {
            _ASSERT(FALSE);
            return NULL;
        }

        friend class Tpm2;
        friend class OutStructSerializer;
        friend class InStructSerializer;
        friend class CryptoServices;
        friend class InByteBuf;
        friend class OutByteBuf;
};

///<summary>Base class for all TPM enums and bitfields.
/// Note that this class was introduced to replace original 'enum class' based
/// imlementation that required pervasive explicit casts to underlying integral types.
///</summary>
template<typename U>
struct TpmEnum {
    using ValueType = U;

    TpmEnum() {}
    TpmEnum(ValueType v) { value = v; }
    operator ValueType() const { return value; }

    template<typename V>
    static ValueType Value(V v) { return (ValueType)v; }
    
    ValueType operator|=(ValueType v) { return value |= v; }
    ValueType operator&=(ValueType v) { return value &= v; }
    ValueType operator^=(ValueType v) { return value ^= v; }
    ValueType operator+=(ValueType v) { return value += v; }
    ValueType operator-=(ValueType v) { return value -= v; }
private:
    ValueType value;
};

_TPMCPP_END

/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once

#include "TpmHelpers.h"
#include "TpmMarshalNew.h"

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
class _DLLEXP_ TpmStructure : public virtual TpmMarshaller
{
    public:
        ///<summary>Base class for all TPM structures.</summary>
        TpmStructure() {}
        virtual ~TpmStructure() {}

        ///<summary>Returns the TPM binary-form representation of this structure.</summary>
        ByteVec ToBuf() const;

        ///<summary>Sets this structure based on the TPM representation in buf.</summary>
        void FromBuf(const ByteVec& buf);

        ///<summary>Creates a new instance of the TPM structure specified in tp.</summary>
        static TpmStructure *FromBuf(const ByteVec& bufToBeRead, TpmTypeId tp);

        ///<summary>Returns the string representation of the structure. If !precise then
        /// arrays are truncated for readability (useful for interactive debugging).</summary>
        string ToString(bool precise = true);

        ///<summary>Serialize the object to text, JSON, XML-etc.</summary>
        string Serialize(SerializationType serializationFormat);

        ///<summary>Deserialize from JSON (other formats TBD)</summary>
        bool Deserialize(SerializationType serializationFormat, string inBuf);

        ///<summary>Test for value equality</summary>
        bool operator==(const TpmStructure& rhs) const;

        ///<summary>Test for value inequality</summary>
        bool operator!=(TpmStructure& rhs) const;

        ///<summary>Get a type-identifier for this structure</summary>
        virtual TpmTypeId GetTypeId() const = 0;

        // Needed for STL/DLL
        // TODO: check if this is correct
        virtual bool operator<(const TpmStructure&)
        {
            return true;
        }

        
        /// <summary> TpmMarshaler method: marshal to the TPM representation </summary>
        virtual void toTpm(TpmBuffer&) const {}

        /// <summary> TpmMarshaler method: marshal from the TPM representation </summary>
        virtual void fromTpm(TpmBuffer&) {}

        ByteVec asTpm2B() const
        {
            auto buf = TpmBuffer();
            buf.writeSizedObj(*this, 2);
            return buf.buffer();
        }

        ByteVec asTpm() const
        {
            auto buf = TpmBuffer(4096);
            toTpm(buf);
            return buf.buffer();
        }

        void writeSizedByteBuf(TpmBuffer& buf) const
        {
            return buf.writeSizedByteBuf(asTpm());
        }

protected:
        ///<summary>Add this struct to the outBuf.</summary>
        void ToBufInternal(class OutByteBuf& outBuf) const;
        bool NonDefaultMarshall(OutByteBuf& outBuf) const;

        ///<summary>Hydrate *this from the current position in the buf.</summary>
        void FromBufInternal(class InByteBuf& buf);
        bool FromBufSpecial(class InByteBuf& buf, TpmTypeId tp);

        ///<summary>Get the expected length of the array in fieldNum/field.  Array-lengths
        // are usually explicit, but in some cases (e.g. hashes) the array-len must be
        /// determined from the TPM_ALG_ID</summary>
        UINT32 GetArrayLen(class TpmStructInfo& containingStruct, class MarshalInfo& field);

        ///<summary>Make a new instance of the specified struct or union type using the default
        /// constructor. If pointerToUnion is not TpmTypeId.Null then also return the pointer
        /// dynacast to the provided union type.</summary>
        static TpmStructure* UnionFactory(TpmTypeId id, TpmTypeId dynacastType, void* pointerToUnion);

        ///<summary>This will be overriden in derived classes that are optional in TPM inputs.
        /// If the marshaller sees a null element it will not be marshalled.  Such elements are
        /// length-preceded, and the length will be set to zero.</summary>
        virtual bool NullElement() const { return false; }

        ///<summary>Provides raw (binary) access to structure members.
        /// If arrayIdex==-1, the address of the element at elementNum is returned. 
        /// If the element is an array:
        ///   a) the current arraySize is also returned,
        ///   b) if newArraySize!=-1, the array is resized accordingly.
        /// If arrayIndex!=-1, then the memory address of the corresponding element is returned.
        /// If the object is a struct or union then a pointer to its TpmStructure base class
        /// is also returned (it may not coinside with the element address because of vtbl)
        /// </summary>
        virtual void* ElementInfo(int elementNum, int arrayIndex, int& arraySize, TpmStructure*& pStruct, int newArraySize) = 0;

        friend class Tpm2;
        friend class OutStructSerializer;
        friend class InStructSerializer;
        friend class Crypto;
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
    
    //ValueType operator&(TpmEnum v) const { return value & v.value; }
    //ValueType operator|(TpmEnum v) const { return value | v.value; }

    ValueType operator&=(ValueType v) const { return value &= v; }
    ValueType operator|=(ValueType v) const { return value |= v; }
    ValueType operator^=(ValueType v) const { return value ^= v; }
    ValueType operator+=(ValueType v) const { return value += v; }
    ValueType operator-=(ValueType v) const { return value -= v; }
private:
    ValueType value;
};

_TPMCPP_END

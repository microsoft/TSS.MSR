/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once

#include "Helpers.h"
#include "TpmMarshal.h"
#include "Serialize.h"

_TPMCPP_BEGIN

/// <summary> Serialization format enumeration. </summary>
enum class SerializationType
{
    Text,
    JSON,
    //Xml
};

/// <summary> Base class for all TPM structures. </summary>
class _DLLEXP_ TpmStructure : public TpmMarshaller, public ISerializable
{
    friend class Tpm2;
    friend class Crypto;

public:
    /// <summary> Base class for all TPM structures. </summary>
    TpmStructure() {}
    virtual ~TpmStructure() {}

    /// <summary> Test for equality </summary>
    bool operator==(const TpmStructure& rhs) const
    {
        if (this == &rhs)
            return true;
        return toBytes() == rhs.toBytes();
    }

    /// <summary> Test for inequality </summary>
    bool operator!=(TpmStructure& rhs) const { return !(*this == rhs); }

    // Needed for STL/DLL
    // TODO: check if this is correct
    virtual bool operator<(const TpmStructure&) { return true; }


    /// <param name="precise"> If false, then middle part of long byte buffers (> 32 bytes) is omitted. </param>
    /// <returns> The string representation of this structure </returns>
    string ToString(bool precise = true)
    {
        PlainTextSerializer buf(precise);
        return buf.Serialize(this);
    }

    /// <summary> Serialize this object using the given text format </summary>
    /// <returns>true in case of success</returns>
    string Serialize(SerializationType serializationFormat);

    /// <summary> Deserialize from JSON (other formats TBD) </summary>
    /// <returns>true in case of success</returns>
    bool Deserialize(SerializationType serializationFormat, string inBuf);


    /** ISerializable method */
    virtual void Serialize(ISerializer& buf) const {}

    /** ISerializable method */
    virtual void Deserialize(ISerializer& buf) {}

    /** ISerializable method */
    virtual const char* TypeName () const { return "TpmStructure"; }


    /// <summary> TpmMarshaller method </summary>
    virtual void toTpm(TpmBuffer&) const {}

    /// <summary> TpmMarshaller method </summary>
    virtual void initFromTpm(TpmBuffer&) {}

    /// <summary> Generates the TPM binary representation of this object </summary>
    /// <seealso cref="toTpm"/>
    /// <returns> The TPM binary representation of this object. </returns>
    ByteVec toBytes() const
    {
        TpmBuffer buf;
        toTpm(buf);
        return buf.trim();
    }

    /// <summary> Inits this object from its TPM binary representation in the given byte buffer </summary>
    /// <seealso cref="initFromTpm"/>
    /// <returns> The TPM binary representation of this object. </returns>
    void initFromBytes(const ByteVec& buffer)
    {
        TpmBuffer buf(buffer);
        initFromTpm(buf);
        _ASSERT(buf.curPos() == buffer.size());
    }

    /// <summary> Marshaling helper </summary>
    /// <returns> 2B size-prefixed TPM binary representation of this object. </returns>
    ByteVec asTpm2B() const
    {
        TpmBuffer buf;
        buf.writeSizedObj(*this);
        return buf.trim();
    }

    [[deprecated("Use toBytes() instead")]]
    ByteVec ToBuf() const { return toBytes(); }

    [[deprecated("Use initFromBytes() instead")]]
    void FromBuf(const ByteVec& buffer) { initFromBytes(buffer); }

protected:
    template<class T>
    static T fromBytes(const ByteVec& buffer)
    {
        TpmBuffer buf(buffer);
        T newObj;
        newObj.initFromTpm(buf);
        _ASSERT(buf.curPos() == buffer.size());
        return newObj;
    }
}; // class TpmStructure


class TPM_HANDLE;


/// <summary> Parameters of the field, to which session based encryption can be applied (i.e.
/// the first non-handle field marshaled in size-prefixed form) </summary>
struct SessEncInfo
{
    /// <summary> Length of the size prefix in bytes. The size prefix contains the number of
    // elements in the sized area filed (normally just bytes). </summary>
    uint16_t sizeLen;
    /// <summary> Length of an element of the sized area in bytes (in most cases 1) </summary>
    uint16_t valLen;
};


class _DLLEXP_ CmdStructure : public TpmStructure
{
public:
    virtual uint16_t numHandles() const { return 0; }

    /// <summary> If session based encryption can be applied to this object (i.e. its first 
    /// non-handle field is marshaled in size-prefixed form), returns non-zero parameters of
    /// the encryptable command/response parameter. Otherwise returns zero initialized struct. </summary>
    virtual SessEncInfo sessEncInfo() const { return {0, 0}; }
};


class _DLLEXP_ ReqStructure : public CmdStructure
{
public:
    virtual vector<TPM_HANDLE> getHandles() const;

    virtual uint16_t numAuthHandles() const { return 0; }

    /** ISerializable method */
    virtual const char* TypeName () const { return "ReqStructure"; }
};


class _DLLEXP_ RespStructure : public CmdStructure
{
public:
    virtual TPM_HANDLE getHandle() const;
    virtual void setHandle(const TPM_HANDLE&) {}

    /** ISerializable method */
    virtual const char* TypeName () const { return "RespStructure"; }
};


/// <summary> Base class for all TPM enums and bitfields.
/// Note that this class was introduced to replace original 'enum class' based
/// imlementation that required pervasive explicit casts to underlying integral types.
/// </summary>
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

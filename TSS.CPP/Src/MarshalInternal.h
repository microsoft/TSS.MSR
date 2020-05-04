/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once
#include "Tpm2.h"

_TPMCPP_BEGIN

extern std::map<TpmTypeId, TpmTypeInfo*>    TypeMap;

template<TpmEntity K>
struct TpmTypeInfoTraits;

template<> struct TpmTypeInfoTraits<TpmEntity::Any> { using InfoType = TpmTypeInfo; };
template<> struct TpmTypeInfoTraits<TpmEntity::Struct> { using InfoType = TpmStructInfo; };
template<> struct TpmTypeInfoTraits<TpmEntity::Union> { using InfoType = TpmUnionInfo; };
template<> struct TpmTypeInfoTraits<TpmEntity::Typedef> { using InfoType = TpmTypedefInfo; };
template<> struct TpmTypeInfoTraits<TpmEntity::Enum> { using InfoType = TpmEnumInfo; };
template<> struct TpmTypeInfoTraits<TpmEntity::Bitfield> { using InfoType = TpmEnumInfo; };

template<TpmEntity EntityKind>
typename TpmTypeInfoTraits<EntityKind>::InfoType& GetTypeInfo(TpmTypeId typeId)
{
    using TypeInfoType = typename TpmTypeInfoTraits<EntityKind>::InfoType;
    TypeInfoType& ti = static_cast<TypeInfoType&>(*TypeMap[typeId]);
    _ASSERT(EntityKind == TpmEntity::Any || ti.Kind == EntityKind);
    return ti;
}

inline
int GetTypeSize(TpmTypeId typeId)
{
    TpmTypedefInfo& ti = static_cast<TpmTypedefInfo&>(*TypeMap[typeId]);
    _ASSERT(ti.Kind == TpmEntity::Typedef || ti.Kind == TpmEntity::Enum || ti.Kind == TpmEntity::Bitfield);
    return ti.Size;
}



#define BYTE_ARRAY_TO_UINT8(b)   (UINT8)((b)[0])

#define BYTE_ARRAY_TO_UINT16(b)  (UINT16)(  ((b)[0] <<  8) \
        + (b)[1])

#define BYTE_ARRAY_TO_UINT32(b)  (UINT32)(  ((b)[0] << 24) \
        + ((b)[1] << 16) \
        + ((b)[2] << 8) \
        + (b)[3])

#define BYTE_ARRAY_TO_UINT64(b)  (UINT64)(  ((UINT64)(b)[0] << 56) \
        + ((UINT64)(b)[1] << 48) \
        + ((UINT64)(b)[2] << 40) \
        + ((UINT64)(b)[3] << 32) \
        + ((UINT64)(b)[4] << 24) \
        + ((UINT64)(b)[5] << 16) \
        + ((UINT64)(b)[6] << 8) \
        + (UINT64)(b)[7])

// Disaggregate a UINT into a byte array
#define UINT8_TO_BYTE_ARRAY(i, b)     ((b)[0] = (BYTE)(i), i)

#define UINT16_TO_BYTE_ARRAY(i, b)    ((b)[0] = (BYTE)((i) >>  8), \
                                       (b)[1] = (BYTE)(i), \
                                       (i))

#define UINT32_TO_BYTE_ARRAY(i, b)    ((b)[0] = (BYTE)((i) >> 24), \
                                       (b)[1] = (BYTE)((i) >> 16), \
                                       (b)[2] = (BYTE)((i) >> 8), \
                                       (b)[3] = (BYTE)(i), \
                                       (i))

#define UINT64_TO_BYTE_ARRAY(i, b)    ((b)[0] = (BYTE)((i) >> 56), \
                                       (b)[1] = (BYTE)((i) >> 48), \
                                       (b)[2] = (BYTE)((i) >> 40), \
                                       (b)[3] = (BYTE)((i) >> 32), \
                                       (b)[4] = (BYTE)((i) >> 24), \
                                       (b)[5] = (BYTE)((i) >> 16), \
                                       (b)[6] = (BYTE)((i) >> 8), \
                                       (b)[7] = (BYTE)(i), \
                                       (i))

///<summary>Returns the value at a memory address cast to a UINT32. Note: no endianness conversion</summary>
inline UINT32 GetValFromBuf(BYTE *pos, UINT32 size)
{
    switch (size) {
        case 1: return *pos;
        case 2: return *(UINT16*)pos;
        case 4: return *(UINT32*)pos;
    }
    throw std::domain_error("GetValFromBuf(): Only sizes 1, 2, 4 supported");
}

inline UINT32 GetValFromBufNetOrder(BYTE *pos, UINT32 size)
{
    switch (size) {
        case 1: return *pos;
        case 2: return BYTE_ARRAY_TO_UINT16(pos);
        case 4: return BYTE_ARRAY_TO_UINT32(pos);
    }
    throw std::domain_error("GetValFromBufNetOrder(): Only sizes 1, 2, 4 supported");
}

inline ByteVec ValueTypeToByteArray(UINT16 x)
{
    size_t len = sizeof(UINT16);
    ByteVec res;
    res.resize(len);
    UINT16_TO_BYTE_ARRAY(x, &res[0]);
    return res;
}

inline ByteVec ValueTypeToByteArray(UINT32 x)
{
    size_t len = sizeof(UINT32);
    ByteVec res;
    res.resize(len);
    UINT32_TO_BYTE_ARRAY(x, &res[0]);
    return res;
}

inline ByteVec ValueTypeToByteArray(UINT64 x)
{
    size_t len = sizeof(UINT64);
    ByteVec res;
    res.resize(len);
    UINT64_TO_BYTE_ARRAY(x, &res[0]);
    return res;
}

inline ByteVec ValueTypeToByteArray(BYTE x)
{
    size_t len = sizeof(BYTE);
    ByteVec res(len);
    res[0] = x;
    return res;
}

inline ByteVec VectorSlice(ByteVec x, size_t start, size_t len)
{
    ByteVec res(len);

    for (size_t j = 0; j < len; j++) {
        res[j] = x[start + j];
    }

    return res;
}

template<typename E>
ByteVec ToNet(E val)
{
    OutByteBuf b;
    b << val;
    return b.GetBuf();
}

///<summary>Return count spaces</summary>
inline string spaces(int count)
{
    return string(count * 4, ' ');
}

///<summary>Copies a UINT of the specified size into a byte-array. Note: *No* endianness conversion</summary>
inline void CopyUint(void* dest, UINT32 val32, int NumBytes)
{
    switch (NumBytes) {
        case 1: {
            UINT8 val8 = (UINT8)val32;
            memcpy(dest, &val8, 1);
            break;
        }
        case 2: {
            UINT16 val16 = (UINT16)val32;
            memcpy(dest, &val16, 2);
            break;
        }
        case 4:
            memcpy(dest, &val32, 4);
            break;
        default:
            _ASSERT(FALSE);
    }
}

union _MARSHALL_BUF {
    BYTE a[8];
    BYTE b1;
    UINT16 b16;
    UINT32 b32;
    UINT64 b64;
};

///<summary>x points to a 1, 2, 4, or 8 byte value type in host order. ToNet converts it to a
/// corresponding net-order byte array</summary>
inline ByteVec ToNet(void *x, int NumBytes)
{
    _MARSHALL_BUF b;
    UINT32 val;
    UINT64 bigVal;

    switch (NumBytes) {
        case 1:
            val = *(BYTE*)x;
            b.b1 = (BYTE)val;
            break;
        case 2:
            val = *(UINT16*)x;
            b.b16 = htons((UINT16)val);
            break;
        case 4:
            val = *(UINT32*)x;
            b.b32 = htonl(val);
            break;
        case 8:
            bigVal = BYTE_ARRAY_TO_UINT64((BYTE*)x);
            b.b64 = bigVal;
            break;
        default:
            _ASSERT(FALSE);
    }

    ByteVec res(NumBytes);
    for (int j = 0; j < NumBytes; j++)
        res[j] = b.a[j];
    return res;
}

inline UINT64 GetValFromByteBuf(BYTE *val, int NumBytes)
{
    switch (NumBytes) {
        case 1:
            return (UINT64) * ((BYTE *)val);

        case 2:
            return (UINT64) * ((UINT16 *)val);

        case 4:
            return (UINT64) * ((UINT32 *)val);

        case 8:
            return (UINT64) * ((UINT64 *)val);

        default:
            _ASSERT(FALSE);
    }

    _ASSERT(FALSE);
    return (UINT64) - 1;
};

inline string AlignToColumns(string s, char separator, int col)
{
    string s2;

    for (size_t j = 0; j < s.size(); j++) {
        s2 += s[j];

        if (s[j] == separator) {
            s2 += "\n";
            s2 += string(col, ' ');
        }
    }

    return s2;
}

///<summary>Return the current column in an ostringstream</summary>
inline int GetColumn(std::ostringstream& s)
{
    int len = (int)s.str().size();
    int column = 0;

    for (int j = len - 1; j >= 0; j--) {
        if (s.str()[j] == '\n') {
            break;
        }

        column++;
    }

    return column;
}

class OutStructSerializer
{
public:
    OutStructSerializer(SerializationType _tp, bool _precise = true)
    {
        SerType = _tp;
        precise = _precise;
        indent = 0;
    };

    string Serialize(class TpmStructure *p);

    string ToString() { return s.str(); }

protected:
    void StartStruct(string structName);
    void EndStruct(string structName);
    void OutTypeAndName(string elementType, string elementName, BOOL isArray);
    void OutByteArray(ByteVec& arr, bool lastInStruct);
    void OutValue(class MarshalInfo& fieldInfo, void *pElem, bool lastInStruct);
    void OutArrayElementSeparator();
    void StartArray(int count);
    void EndArray();
    void Indent();

    SerializationType SerType;
    //TpmStructure *p;
    std::ostringstream s;
    int indent;
    bool precise = true;
};

class InStructSerializer
{
public:
    InStructSerializer(SerializationType _tp, string _s);

    bool DeSerialize(TpmStructure *p);

protected:
    bool StartStruct();
    bool GetElementName(string& name);
    bool GetToken(char terminator, string& tokenName);
    bool NextChar(char needed);
    bool GetInteger(UINT64& _val, int sizeInBytes);
    void DebugStream();

    SerializationType SerType;
    std::istringstream s;
    string debugString;
};

_TPMCPP_END

/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#pragma once
#include "fdefs.h"

_TPMCPP_BEGIN

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
    UINT32 val;

    switch (size) {
        case 1:
            val = *pos;
            break;

        case 2:
            val = *((UINT16 *)pos);
            break;

        case 4:
            val = *((UINT32 *)pos);
            break;

        default:
            _ASSERT(FALSE);
    }

    return val;
}

inline UINT32 GetValFromBufNetOrder(BYTE *pos, UINT32 size)
{
    UINT32 val;

    switch (size) {
        case 1:
            val = *pos;
            break;

        case 2:
            val = (UINT32)BYTE_ARRAY_TO_UINT16(pos);
            break;

        case 4:
            val = BYTE_ARRAY_TO_UINT32(pos);
            break;

        default:
            _ASSERT(FALSE);
    }

    return val;
}

inline vector<BYTE> ValueTypeToByteArray(UINT16 x)
{
    size_t len = sizeof(UINT16);
    vector<BYTE> res;
    res.resize(len);
    UINT16_TO_BYTE_ARRAY(x, &res[0]);
    return res;
}

inline vector<BYTE> ValueTypeToByteArray(UINT32 x)
{
    size_t len = sizeof(UINT32);
    vector<BYTE> res;
    res.resize(len);
    UINT32_TO_BYTE_ARRAY(x, &res[0]);
    return res;
}

inline vector<BYTE> ValueTypeToByteArray(UINT64 x)
{
    size_t len = sizeof(UINT64);
    vector<BYTE> res;
    res.resize(len);
    UINT64_TO_BYTE_ARRAY(x, &res[0]);
    return res;
}

inline vector<BYTE> ValueTypeToByteArray(BYTE x)
{
    size_t len = sizeof(BYTE);
    vector<BYTE> res(len);
    res[0] = x;
    return res;
}

inline std::vector<BYTE> VectorSlice(std::vector<BYTE> x, size_t start, size_t len)
{
    std::vector<BYTE> res(len);

    for (size_t j = 0; j < len; j++) {
        res[j] = x[start + j];
    }

    return res;
}

template<typename E>
std::vector<BYTE> ToNet(E val)
{
    OutByteBuf b;
    b << val;
    return b.GetBuf();
}

///<summary>Return count spaces</summary>
inline std::string spaces(int count)
{
    return std::string(count * 4, ' ');
}

///<summary>Copies a UINT of the specified size into a byte-array. Note: *No* endianness conversion</summary>
inline void CopyUint(BYTE *dest, UINT32 val32, int NumBytes)
{
    UINT16 val16 = (UINT16)val32;
    UINT8 val8 = (UINT8)val32;

    switch (NumBytes) {
        case 1:
            memcpy(dest, (BYTE *)&val8, 1);
            break;

        case 2:
            memcpy(dest, (BYTE *)&val16, 2);
            break;

        case 4:
            memcpy(dest, (BYTE *)&val32, 4);
            break;

        default:
            _ASSERT(FALSE);
    }

    return;
}

///<summary>Copies a UINT of the specified size into a byte-array WITH endianness conversion</summary>
inline void CopyUintNetOrder(BYTE *dest, UINT32 _val32, int NumBytes)
{
    UINT16 _val16 = (UINT16)_val32;
    UINT16 val16 = htons(_val16);
    UINT32 val32 = htonl(_val32);

    switch (NumBytes) {
        case 2:
            memcpy(dest, (BYTE *)&val16, 2);
            break;

        case 4:
            memcpy(dest, (BYTE *)&val32, 4);
            break;

        default:
            _ASSERT(FALSE);
    }

    return;
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
inline vector<BYTE> ToNet(BYTE *x, int NumBytes)
{
    _MARSHALL_BUF b;
    UINT32 val;
    UINT64 bigVal;

    switch (NumBytes) {
        case 1:
            b.b1 = *x;
            val = *x;
            break;

        case 2:
            val = *((UINT16 *)x);
            b.b16 = htons(val);
            break;

        case 4:
            val = *((UINT32 *)x);
            b.b32 = htonl(val);
            break;

        case 8:
            bigVal = BYTE_ARRAY_TO_UINT64(x);
            b.b64 = bigVal;
            break;

        default:
            _ASSERT(FALSE);
    }

    vector<BYTE> res(NumBytes);

    for (int j = 0; j < NumBytes; j++) {
        res[j] = b.a[j];
    }

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

inline std::string AlignToColumns(std::string s, char separator, int col)
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
inline int GetColumn(ostringstream& s)
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

class OutStructSerializer {
    public:
        OutStructSerializer(SerializationType _tp, bool _precise = true) {
            tp = _tp;
            precise = _precise;
            p = NULL;
            indent = 0;
        };

        string Serialize(class TpmStructureBase *p);
        string ToString();

    protected:
        void StartStruct(string structName);
        void EndStruct(string structName);
        void OutTypeAndName(string elementType, string elementName, BOOL isArray);
        void OutByteArray(vector<BYTE>& arr, bool lastInStruct);
        void OutValue(class MarshallInfo& fieldInfo, void *pElem, bool lastInStruct);
        void OutArrayElementSeparator();
        void StartArray(int count);
        void EndArray();
        void Indent();

        SerializationType tp;
        TpmStructureBase *p;
        ostringstream s;
        int indent;
        bool precise = true;

};

class InStructSerializer {
    public:
        InStructSerializer(SerializationType _tp, string _s);
        bool DeSerialize(TpmStructureBase *p);

    protected:
        bool StartStruct();
        bool GetElementName(string& name);
        bool GetToken(char terminator, string& tokenName);
        bool NextChar(char needed);
        bool GetInteger(UINT64& _val, int sizeInBytes);
        void DebugStream();

        SerializationType tp;
        TpmStructureBase *p;
        istringstream s;
        string debugString;
};

_TPMCPP_END
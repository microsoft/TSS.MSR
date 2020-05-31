/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once
#include "Tpm2.h"

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

/// <summary> Returns the value at a memory address cast to a UINT32. Note: no endianness conversion </summary>
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

_TPMCPP_END

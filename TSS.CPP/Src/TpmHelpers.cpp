/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "MarshalInternal.h"

_TPMCPP_BEGIN

using namespace std;

string GetEnumString(UINT32 val, const TpmTypeId& tid)
{
    const TpmTypeInfo* pti = TypeMap[tid];
    string res;

    if (pti->Kind == TpmEntity::Enum)
    {
        auto pei = (TpmEnumInfo*)pti;
        if (pei->ConstNames.count(val) != 0)
        {
            if (pti->Kind == TpmEntity::Enum)
            {
                // Simple enumeration
                res = pei->ConstNames[val];
            }
        }
    }
    else if (pti->Kind == TpmEntity::Bitfield)
    {
        auto pbi = (TpmBitfieldInfo*)pti;
        for (auto it = pbi->ConstNames.begin(); it != pbi->ConstNames.end(); ++it)
        {
            UINT32 bitVal = 1 << it->first;

            if ((val & bitVal) != 0)
            {
                if (res != "")
                    res += " | ";
                res += it->second;
            }
        }
    }
    return res.empty() ? "?" : res;
}

ostream& operator<<(ostream& s, const ByteVec& b)
{
    for (UINT32 j = 0; j < b.size(); j++) {
        s << setw(2) << setfill('0') << hex << (UINT32)b[j];
        if ((j + 1) % 4 == 0)
            s << " ";
    }
    return s;
}

OutByteBuf& OutByteBuf::operator<<(const TpmStructure& x)
{
    ByteVec xx = x.ToBuf();
    buf.insert(buf.end(), xx.begin(), xx.end());
    return *this;
}

InByteBuf& InByteBuf::operator>>(TpmStructure& s)
{
    s.FromBufInternal(*this);
    return *this;
}

InByteBuf& InByteBuf::operator>>(UINT64& val)
{
    BYTE *p = (BYTE *)&val;

    for (UINT32 j = 0; j < 8; j++)*(p + j) =
            buf[pos++];

    val = BYTE_ARRAY_TO_UINT64(p);
    return *this;
}

ByteVec InByteBuf::GetEndianConvertedVec(UINT32 numBytes)
{
    ByteVec v = GetSlice(numBytes);
    BYTE *p = &v[0];

    switch (numBytes) {
        case 1:
            break;

        case 2:
            *((UINT16 *)p) = BYTE_ARRAY_TO_UINT16(p);
            break;

        case 4:
            *((UINT32 *)p) = BYTE_ARRAY_TO_UINT32(p);
            break;

        case 8:
            *((UINT64 *)p) = BYTE_ARRAY_TO_UINT64(p);
            break;

        default:
            _ASSERT(FALSE);
    }

    return v;
}

ByteVec Helpers::ShiftRight(const ByteVec& x, size_t numBits)
{
    size_t  newSize = x.size() - numBits / 8;

    if (numBits % 8 == 0)
        return ByteVec(x.begin(), x.begin() + newSize);

    if (numBits > 7)
        throw domain_error("Can only shift up to 7 bits");

    size_t  numCarryBits = 8 - numBits;
    ByteVec y(newSize);

    for (size_t j = newSize - 1; j >= 0; --j)
    {
        y[j] = (BYTE)(x[j] >> numBits);
        if (j != 0)
            y[j] |= (BYTE)(x[j - 1] << numCarryBits);
    }
    return y;
}

_TPMCPP_END
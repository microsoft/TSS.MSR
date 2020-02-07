/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"
#include "MarshallInternal.h"
#include "Tpm2.h"

_TPMCPP_BEGIN

using namespace std;

string GetEnumString(UINT32 val, StructMarshallInfo& tp)
{
    string res = "";

    // Simple enumeration
    if (tp.EnumNames.size() != 0) {
        if (tp.EnumNames.count(val) != 0) {
            res = tp.EnumNames[val];
        }
    }

    // Bitfield
    if (tp.BitNames.size() != 0) {
        for (UINT32 i = 0; i < tp.BitNames.size(); i++) {
            UINT32 bitVal = 1 << i;

            if ((val & bitVal) != 0) {
                if (res != "") {
                    res += " | ";
                }

                res += tp.BitNames[i];
            }
        }
    }

    if (res == "") {
        res = "?";
    }

    return res;
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


OutByteBuf& OutByteBuf::operator<<(class TpmStructureBase& x)
{
    ByteVec xx = x.ToBuf();
    buf.insert(buf.end(), xx.begin(), xx.end());
    return *this;
}

InByteBuf& InByteBuf::operator>>(TpmStructureBase& s)
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

_TPMCPP_END
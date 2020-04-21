/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once

#include <algorithm>    // required here for gcc C++ 11
#include <iostream>
#include <iomanip>
#include <sstream>
#include <stack>

#include "fdefs.h"

#ifdef __linux__
#   include <unistd.h>
#endif

_TPMCPP_BEGIN

template<typename U> struct TpmEnum;

///<summary>Provides for marshalling TPM types to a byte-buffer</summary>
class OutByteBuf {
    public:
        OutByteBuf() { };

        OutByteBuf& operator<<(BYTE b) {
            buf.push_back(b);
            return *this;
        }

        OutByteBuf& operator<<(UINT16 _val) {
            UINT16 val = htons(_val);
            BYTE *p = (BYTE *)&val;

            for (int j = 0; j < 2; j++) {
                buf.push_back((BYTE) * (p + j));
            }

            return *this;
        }

        OutByteBuf& operator<<(UINT32 _val) {
            UINT32 val = htonl(_val);
            BYTE *p = (BYTE *)&val;

            for (int j = 0; j < 4; j++) {
                buf.push_back((BYTE) * (p + j));
            }

            return *this;
        }

        OutByteBuf& operator<<(const class TpmStructure& x);

        OutByteBuf& operator<<(const ByteVec& xx) {
            buf.insert(buf.end(), xx.begin(), xx.end());
            return *this;
        }

        template<typename U>
        OutByteBuf& operator<<(const TpmEnum<U>& e) {
            return *this << (U)e;
        }

        void AddSlice(ByteVec xx, int start, int len) {
            buf.insert(buf.end(), xx.begin() + start, xx.begin() + start + len);
            return;
        }

        int GetPos() {
            return (int)buf.size();
        }

        ByteVec& GetBuf() {
            return buf;
        }

        BYTE *GetBufPtr(int pos) {
            return &buf[pos];
        }

    protected:
        ByteVec buf;
};

///<summary>Provides for unmarshalling TPM types from a byte-buffer</summary>
class InByteBuf {
    public:
        InByteBuf(const ByteVec& _buf) {
            buf = _buf;
            pos = 0;
        };

        InByteBuf& operator>>(BYTE& b) {
            b = buf[pos++];
            return *this;
        }

        InByteBuf& operator>>(UINT16& val) {
            BYTE *p = (BYTE *)&val;

            for (int j = 0; j < 2; j++)*(p + j) =
                    buf[pos++];

            val = ntohs(val);
            return *this;
        }

        InByteBuf& operator>>(UINT32& val) {
            BYTE *p = (BYTE *)&val;

            for (UINT32 j = 0; j < 4; j++)*(p + j) =
                    buf[pos++];

            val = ntohl(val);
            return *this;
        }

        InByteBuf& operator>>(UINT64& val);
        InByteBuf& operator>>(TpmStructure& s);
        ByteVec GetEndianConvertedVec(UINT32 numBytes);

        ByteVec TheRest() {
            ByteVec theRest(buf.size() - pos);

            for (UINT32 j = pos; j < buf.size(); j++) {
                theRest[j - pos] = buf[j];
            }

            pos = (int)buf.size();
            return theRest;
        }

        ByteVec GetSlice(UINT32 numBytes) {
            ByteVec temp(numBytes);

            for (UINT32 j = 0; j < numBytes; j++) {
                temp[j] = buf[j + pos];
            }

            pos += numBytes;
            return temp;
        }

        UINT32 GetValueType(int numBytes) {
            switch (numBytes) {
                case 1:
                    BYTE x1;
                    *this >> x1;
                    return (UINT32)x1;

                case 2:
                    UINT16 x2;
                    *this >> x2;
                    return (UINT32)x2;

                case 4:
                    UINT16 x4;
                    *this >> x4;
                    return x4;

                default:
                    _ASSERT(FALSE);
            }

            return -1;
        }

        bool eof() {
            return pos == (int)buf.size();
        }

        int GetPos() const { return pos; }

        std::stack<int> sizedStructLen;

    protected:
        ByteVec buf;
        int pos;
};

class Helpers {
    public:
        static ByteVec Concatenate(const ByteVec& t1, 
                                             const ByteVec& t2) {
            ByteVec x(t1.size() + t2.size());
            copy(t1.begin(), t1.end(), x.begin());
            copy(t2.begin(), t2.end(), x.begin() + t1.size());
            return x;
        }

        static ByteVec Concatenate(const vector<ByteVec>& l) {
            ByteVec res;

            for (auto i = l.begin(); i != l.end(); i++) {
                res.resize(res.size() + i->size());
                copy(i->begin(), i->end(), res.end() - i->size());
            }

            return res;
        }

        ///<summary>Returns a new buffer that is UINT16-len prepended</summary>
        static ByteVec ByteVecToLenPrependedByteVec(const ByteVec& x) {
            OutByteBuf b;
            b << (UINT16)x.size() << x;
            return b.GetBuf();
        }
};

///<summary>Returns string representation of a TPM enum or bitfield value</summary>
_DLLEXP_ string GetEnumString(UINT32 val, const enum class TpmTypeId& tid);

///<summary>Get the string representation of an enum or bitfield value.</summary>
template<class E>
static string GetEnumString(const E& enumVal) {
    return GetEnumString((UINT32)enumVal, enumVal.GetTypeId());
}


///<summary> Output a formatted byte-stream</summary>
_DLLEXP_ std::ostream& operator<<(std::ostream& s, const ByteVec& b);

inline void Sleep(int numMillisecs)
{
#ifdef WIN32
    ::Sleep(numMillisecs);
#elif __linux__
    usleep(numMillisecs * 1000);
#endif
}
_TPMCPP_END
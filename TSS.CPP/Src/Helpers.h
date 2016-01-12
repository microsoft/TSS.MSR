/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#pragma once
#include "fdefs.h"

_TPMCPP_BEGIN

///<summary>Convert an eumeration to its underlying integral type</summary>
template<typename E>
auto ToIntegral(E e) -> typename std::underlying_type<E>::type {
    return static_cast<typename std::underlying_type<E>::type>(e);
}

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

        OutByteBuf& operator<<(class TpmStructureBase& x);

        OutByteBuf& operator<<(std::vector<BYTE> xx) {
            buf.insert(buf.end(), xx.begin(), xx.end());
            return *this;
        }

        void AddSlice(std::vector<BYTE> xx, int start, int len) {
            buf.insert(buf.end(), xx.begin() + start, xx.begin() + start + len);
            return;
        }

        int GetPos() {
            return buf.size();
        }

        std::vector<BYTE>& GetBuf() {
            return buf;
        }

        BYTE *GetBufPtr(int pos) {
            return &buf[pos];
        }

    protected:
        vector<BYTE> buf;
};

///<summary>Provides for unmarshalling TPM types from a byte-buffer</summary>
class InByteBuf {
    public:
        InByteBuf(const std::vector<BYTE>& _buf) {
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
        InByteBuf& operator>>(TpmStructureBase& s);
        vector<BYTE> GetEndianConvertedVec(UINT32 numBytes);

        vector<BYTE> TheRest() {
            vector<BYTE> theRest(buf.size() - pos);

            for (UINT32 j = pos; j < buf.size(); j++) {
                theRest[j - pos] = buf[j];
            }

            pos = buf.size();
            return theRest;
        }

        vector<BYTE> GetSlice(UINT32 numBytes) {
            vector<BYTE> temp(numBytes);

            for (UINT32 j = 0; j < numBytes; j++) {
                temp[j] = buf[j + pos];
            }

            pos += numBytes;
            return temp;
        }

        UINT32 GetValueType(int numBytes) {
            switch (numBytes) {
                case 1:
                    byte x1;
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

    protected:
        vector<BYTE> buf;
        int pos;
};

class Helpers {
    public:
        static std::vector<BYTE> Concatenate(const std::vector<BYTE>& t1, 
                                             const std::vector<BYTE>& t2) {
            std::vector<BYTE> x(t1.size() + t2.size());
            copy(t1.begin(), t1.end(), x.begin());
            copy(t2.begin(), t2.end(), x.begin() + t1.size());
            return x;
        }

        static std::vector<BYTE> Concatenate(const std::vector<std::vector<BYTE>>& l) {
            std::vector<BYTE> res;

            for (auto i = l.begin(); i != l.end(); i++) {
                res.resize(res.size() + i->size());
                copy(i->begin(), i->end(), res.end() - i->size());
            }

            return res;
        }

        ///<summary>Returns a new buffer that is UINT16-len prepended</summary>
        static std::vector<BYTE> ByteVecToLenPrependedByteVec(const vector<BYTE>& x) {
            OutByteBuf b;
            b << (UINT16)x.size() << x;
            return b.GetBuf();
        }
};

///<summary>Returns string representation of an enum, in flags form if it is a TPM attribute</summary>
std::string GetEnumString(UINT32 val, class StructMarshallInfo& tp);

///<summary> Output a formatted byte-stream</summary>
inline ostream& operator<<(ostream& s, const vector<byte>& b)
{
    for (UINT32 j = 0; j < b.size(); j++) {
        s << setw(2) << setfill('0') << hex << (UINT32)b[j];

        if ((j + 1) % 4 == 0) {
            s << " ";
        }
    }

    return s;
}

_TPMCPP_END
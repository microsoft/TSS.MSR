/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once
#include <cassert>

_TPMCPP_BEGIN

struct SizedStructInfo
{
    size_t startPos;
    size_t size;
};

class TpmBuffer;

struct TpmMarshaller
{
    /**
     *  Convert this object to its TPM representation and store in the output byte buffer object
     *
     *  @param buf Output byte buffer for the marshaled representation of this object
     */
    virtual void toTpm(TpmBuffer& buf) const = 0;

    /**
     *  Populate this object from the TPM representation in the input byte buffer object
     *
     *  @param buf  An input byte buffer containg marshaled representation of the object
     */
    virtual void fromTpm(TpmBuffer& buf) = 0;
}; // interface TpmMarshaller

typedef ByteVec Buffer;

class TpmBuffer
{
protected:
    ByteVec buf;
    size_t pos = 0;
    bool outOfBounds = false;

private:
    std::vector<SizedStructInfo> sizedStructSizes;

    bool checkLen(size_t len)
    {
        if (this->length() < this->pos + len)
        {
            this->outOfBounds = true;
            this->pos = this->length();
            throw std::runtime_error("");
            //return false;
        }
        return true;
    }

    void writeNum(uint64_t val, size_t len)
    {
        // TODO: Enable assert below
        // assert(len > 1);
        if (!this->checkLen(len))
            return;

        if (len == 8) {
            this->buf[this->pos++] = (val >> 56) & 0xFF;
            this->buf[this->pos++] = (val >> 48) & 0xFF;
            this->buf[this->pos++] = (val >> 40) & 0xFF;
            this->buf[this->pos++] = (val >> 32) & 0xFF;
        }
        if (len >= 4) {
            this->buf[this->pos++] = (val >> 24) & 0xFF;
            this->buf[this->pos++] = (val >> 16) & 0xFF;
        }
        if (len >= 2)
            this->buf[this->pos++] = (val >> 8) & 0xFF;
        this->buf[this->pos++] = val & 0xFF;
    }

    uint64_t readNum(size_t len)
    {
        if (!this->checkLen(len))
            return 0;

        uint64_t res = 0;
        if (len == 8) {
            res += ((uint64_t)this->buf[this->pos++] << 56);
            res += ((uint64_t)this->buf[this->pos++] << 48);
            res += ((uint64_t)this->buf[this->pos++] << 40);
            res += ((uint64_t)this->buf[this->pos++] << 32);
        }
        if (len >= 4) {
            res += (this->buf[this->pos++] << 24);
            res += (this->buf[this->pos++] << 16);
        }
        if (len >= 2)
            res += (this->buf[this->pos++] << 8);
        res += this->buf[this->pos++];
        return res;
    }

public:
    TpmBuffer(size_t length = 4096) : buf(length) {}
    TpmBuffer(const ByteVec& src) : buf(src) {}
    TpmBuffer(const TpmBuffer& src) : buf(src.buf) {} 

    operator Buffer& () { return this->buf; }
    operator const Buffer& () const { return this->buf; }

    Buffer& buffer() { return this->buf; }

    size_t length() const { return this->buf.size(); }

    size_t curPos() { return this->pos; }

    void curPos(size_t newPos)
    {
        this->pos = newPos;
        this->outOfBounds = newPos <= this->length();
    }

    bool isOk() const { return !this->outOfBounds; }

    TpmBuffer& trim()
    {
        this->buf.resize(this->pos);
        return *this;
    }

    size_t getCurStuctRemainingSize()
    {
        SizedStructInfo& ssi = this->sizedStructSizes.back();
        return ssi.size - (this->pos - ssi.startPos);
    }

    /**
     *  Writes the given 8-bit integer to the buffer
     *  @param val  8-bit integer value to marshal
     */
    void writeByte(uint8_t val)
    {
        if (checkLen(1))
            this->buf[this->pos++] = val & 0x00FF;
    }

    /**
     *  Converts the given 16-bit integer to the TPM wire format, and writes it to the buffer.
     *  @param val  16-bit integer value to marshal
     */
    void writeShort(uint16_t val)
    {
        writeNum(val, 2);
    }

    /**
     *  Converts the given 32-bit integer to the TPM wire format, and writes it to the buffer.
     *  @param val  32-bit integer value to marshal
     */
    void writeInt(uint32_t val)
    {
        writeNum(val, 4);
    }

    /**
     *  Converts the given 64-bit integer to the TPM wire format, and writes it to the buffer.
     *  @param val  64-bit integer value to marshal
     */
    void writeInt64(uint64_t val)
    {
        writeNum(val, 8);
    }


    /**
     *  Reads an 8-bit integer from the buffer containg data in the TPM wire format.
     *  @returns Unmarshaled 8-bit integer
     */
    uint8_t readByte()
    {
        if (!this->checkLen(1))
            return 0;
        return this->buf[this->pos++];
    }

    /**
     *  Reads a 16-bit integer from the buffer containg data in the TPM wire format.
     *  @returns Unmarshaled 16-bit integer
     */
    uint16_t readShort()
    {
        return (uint16_t)this->readNum(2);
    }

    /**
     *  Reads a 32-bit integer from the buffer containg data in the TPM wire format.
     *  @returns Unmarshaled 32-bit integer
     */
    uint32_t readInt()
    {
        return (uint32_t)this->readNum(4);
    }

    /**
     *  Reads a 32-bit integer from the buffer containg data in the TPM wire format.
     *  @returns Unmarshaled 32-bit integer
     */
    uint64_t readInt64()
    {
        return this->readNum(8);
    }

    /**
     *  Writes the given byte buffer to the output buffer as a TPM2B structure in the TPM wire format.
     *  @param data  Byte buffer to marshal
     *  @param sizeLen  Length of the byte buffer size prefix in bytes
     */
    void writeSizedByteBuf(const ByteVec& data, size_t sizeLen = 2)
    {
        if (data.empty())
        {
            this->writeNum(0, sizeLen);
        }
        else if (this->checkLen(data.size() + sizeLen))
        {
            this->writeNum((uint32_t)data.size(), sizeLen);
            writeByteBuf(data);
        }
    }

    /**
     *  Reads a byte buffer from its a TPM2B structure representation in the TPM wire format.
     *  @param sizeLen  Length of the byte array size in bytes
     *  @returns Extracted byte buffer
     */
    ByteVec readSizedByteBuf(size_t sizeLen = 2)
    {
        size_t len = (size_t)this->readNum(sizeLen);
        size_t start = this->pos;
        this->pos += len;
        return ByteVec(this->buf.begin() + start, this->buf.begin() + this->pos);
    }

    template<class T>
    void writeSizedObj(const T& obj)
    {
        // Length of the array size is always 2 bytes
        const size_t lenSize = 2;
        if (!this->checkLen(lenSize))
            return;

        // Remember position to marshal the size of the data structure
        size_t sizePos = this->pos;
        // Account for the reserved size area
        this->pos += lenSize;
        // Marshal the object
        obj.toTpm(*this);
        // Calc marshaled object len
        size_t objLen = this->pos - (sizePos + lenSize);
        // Marshal it in the appropriate position
        this->pos = sizePos;
        this->writeShort((uint16_t)objLen);
        this->pos += objLen;
    }

    template<class T>
    void readSizedObj(T& obj)
    {
        // Length of the array size is always 2 bytes
        size_t size = this->readShort();
        if (size == 0)
            return;

        this->sizedStructSizes.push_back({this->pos, size});
        obj.fromTpm(*this);
        this->sizedStructSizes.pop_back();
    }

    // Marshal only data, no size prefix
    void writeByteBuf(const ByteVec& data)
    {
        if (!this->checkLen(data.size()))
            return;
        std::copy(data.cbegin(), data.cend(), this->buf.begin() + this->pos);
        this->pos += data.size();
    }

    ByteVec readByteBuf(size_t size)
    {
        if (!this->checkLen(size))
            return ByteVec();
        auto start = this->buf.begin() + this->pos;
        ByteVec newBuf(start, start + size);
        this->pos += size;
        return newBuf;
    }

    template<class T>
    void writeObjArr(const vector<T>& arr)
    {
        // Length of the array size is always 4 bytes
        this->writeInt((uint32_t)arr.size());
        for (auto elt: arr)
        {
            if (!this->isOk())
                break;
            elt.toTpm(*this);
        }
    }

    template<class T>
    void readObjArr(vector<T>& arr)
    {
        // Length of the array size is always 4 bytes
        size_t len = this->readInt();
        if (len == 0)
            return arr.clear();

        arr.resize(len);
        for (size_t i = 0; i < len; ++i)
        {
            if (!this->isOk())
                break;
            arr[i].fromTpm(*this);
        }
    }

    template<typename T>
    void writeValArr(const vector<T>& arr, size_t valSize)
    {
        // Length of the array size is always 4 bytes
        this->writeInt((uint32_t)arr.size());
        for (auto val: arr)
        {
            if (!this->isOk())
                break;
            this->writeNum(val, valSize);
        }
    }

    template<typename T>
    void readValArr(vector<T>& arr, size_t valSize)
    {
        // Length of the array size is always 4 bytes
        size_t len = this->readInt();
        if (len == 0)
            return arr.clear();

        arr.resize(len);
        for (size_t i = 0; i < len; ++i)
        {
            if (!this->isOk())
                break;
            arr[i] = (T)(uint32_t)this->readNum(valSize);
        }
    }

}; // class TpmBuffer

class _DLLEXP_ _TPMT_SYM_DEF_OBJECT;
class _DLLEXP_ _TPMT_SYM_DEF;

void nonStandardToTpm(const _TPMT_SYM_DEF& sd, TpmBuffer& buf);
void nonStandardToTpm(const _TPMT_SYM_DEF_OBJECT& sdo, TpmBuffer& buf);

void nonStandardFromTpm(_TPMT_SYM_DEF& sd, TpmBuffer& buf);
void nonStandardFromTpm(_TPMT_SYM_DEF_OBJECT& sdo, TpmBuffer& buf);


_TPMCPP_END

/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once

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

    bool isOk() const
    {
        return !this->outOfBounds;
    }

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
     *  Converts an integer value of the given size to the TPM wire format.
     *  @param val Integer value to marshal
     *  @param len Size of the integer value in bytes
     */
    void writeInt(uint32_t val, size_t len)
    {
        _ASSERT(len <= 4);
        if (!this->checkLen(len))
            return;
        if (len == 4) {
            this->buf[this->pos++] = (val >> 24) & 0x000000FF;
            this->buf[this->pos++] = (val >> 16) & 0x000000FF;
        }
        if (len >= 2)
            this->buf[this->pos++] = (val >> 8) & 0x000000FF;
        this->buf[this->pos++] = val & 0x000000FF;
    }

    void writeInt64(uint64_t val)
    {
        if (!this->checkLen(8))
            return;
        this->buf[this->pos++] = (val >> 56) & 0x00000000000000FF;
        this->buf[this->pos++] = (val >> 48) & 0x00000000000000FF;
        this->buf[this->pos++] = (val >> 40) & 0x00000000000000FF;
        this->buf[this->pos++] = (val >> 32) & 0x00000000000000FF;
        this->buf[this->pos++] = (val >> 24) & 0x00000000000000FF;
        this->buf[this->pos++] = (val >> 16) & 0x00000000000000FF;
        this->buf[this->pos++] = (val >> 8) & 0x00000000000000FF;
        this->buf[this->pos++] = val & 0x00000000000000FF;
    }

    /**
     *  Reads an integer value of the given size from the input buffer containg data in the TPM wire format.
     *  @param len  Size of the integer value in bytes
     *  @returns Extracted numerical value
     */
    uint32_t readInt(size_t len)
    {
        _ASSERT(len <= 4);
        if (!this->checkLen(len))
            return 0;

        uint32_t res = 0;
        if (len == 4) {
            res += (this->buf[this->pos++] << 24);
            res += (this->buf[this->pos++] << 16);
        }
        if (len >= 2)
            res += (this->buf[this->pos++] << 8);
        res += this->buf[this->pos++];
        return res;
    }

    uint64_t readInt64()
    {
        if (!this->checkLen(8))
            return 0;

        uint64_t res = 0;
        res += ((uint64_t)this->buf[this->pos++] << 56);
        res += ((uint64_t)this->buf[this->pos++] << 48);
        res += ((uint64_t)this->buf[this->pos++] << 40);
        res += ((uint64_t)this->buf[this->pos++] << 32);
        res += ((uint64_t)this->buf[this->pos++] << 24);
        res += ((uint64_t)this->buf[this->pos++] << 16);
        res += ((uint64_t)this->buf[this->pos++] << 8);
        res += (uint64_t)this->buf[this->pos++];
        return res;
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
            this->writeInt(0, sizeLen);
        }
        else if (this->checkLen(data.size() + sizeLen))
        {
            this->writeInt((uint32_t)data.size(), sizeLen);
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
        size_t len = (size_t)this->readInt(sizeLen);
        size_t start = this->pos;
        this->pos += len;
        return ByteVec(this->buf.begin() + start, this->buf.begin() + this->pos);
    }

    template<class T>
    void writeSizedObj(const T& obj, size_t lenSize)
    {
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
        this->writeInt((uint32_t)objLen, lenSize);
        this->pos += objLen;
    }

    template<class T>
    void readSizedObj(T& obj, size_t lenSize)
    {
        size_t size = this->readInt(lenSize);
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
    void writeObjArr(const vector<T>& arr, size_t lenSize)
    {
        this->writeInt((uint32_t)arr.size(), lenSize);
        for (auto elt: arr)
        {
            if (!this->isOk())
                break;
            elt.toTpm(*this);
        }
    }

    template<class T>
    void readObjArr(vector<T>& arr, size_t lenSize)
    {
        size_t len = this->readInt(lenSize);
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
    void writeValArr(const vector<T>& arr, size_t valSize, size_t lenSize)
    {
        this->writeInt((uint32_t)arr.size(), lenSize);
        for (auto val: arr)
        {
            if (!this->isOk())
                break;
            this->writeInt(val, valSize);
        }
    }

    template<typename T>
    void readValArr(vector<T>& arr, size_t valSize, size_t lenSize)
    {
        size_t len = this->readInt(lenSize);
        if (len == 0)
            return arr.clear();

        arr.resize(len);
        for (size_t i = 0; i < len; ++i)
        {
            if (!this->isOk())
                break;
            arr[i] = (T)this->readInt(valSize);
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

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
    /** Store the TPM binary representation of this object in the given marshaling buffer
     *  @param buf  Marshaling buffer
     */
    virtual void toTpm(TpmBuffer& buf) const = 0;

    /** Populate this object from the TPM representation in the given marshaling buffer
     *  @param buf  Marshaling buffer
     */
    virtual void fromTpm(TpmBuffer& buf) = 0;
}; // interface TpmMarshaller

typedef ByteVec Buffer;

/** Implements marshaling data (integers, TPM enums, data structures and unions, and arrays
 *  thereof) to/from the binary wire representation defined by the TPM 2.0 specificiation.
 *  The contents of the buffer is always in the TPM wire format.
 */
class _DLLEXP_ TpmBuffer
{
protected:
    ByteVec buf;
    size_t pos = 0;
    bool outOfBounds = false;

private:
    std::vector<SizedStructInfo> sizedStructSizes;

    bool checkLen(size_t len)
    {
        if (length() < pos + len)
        {
            outOfBounds = true;
            pos = length();
            throw std::runtime_error("");
            //return false;
        }
        return true;
    }

public:
    TpmBuffer(size_t length = 4096) : buf(length) {}
    TpmBuffer(const ByteVec& src) : buf(src) {}
    TpmBuffer(const TpmBuffer& src) : buf(src.buf) {} 

    /** @return  Reference to the underlying byte buffer */
//    operator Buffer& () { return buf; }
    /** @return  Constant reference to the underlying byte buffer */
//    operator const Buffer& () const { return buf; }

    /** @return  Reference to the underlying byte buffer */
    Buffer& buffer() { return buf; }

    size_t length() const { return buf.size(); }

    size_t curPos() { return pos; }

    void curPos(size_t newPos)
    {
        pos = newPos;
        outOfBounds = length() < newPos;
    }

    bool isOk() const { return !outOfBounds; }

    /** Shrinks the backing byte buffer so that it ends at the current position */
    ByteVec& trim()
    {
        buf.resize(pos);
        return buf;
    }

    size_t remaining() { return buf.size() - pos; }

    size_t getCurStuctRemainingSize()
    {
        SizedStructInfo& ssi = sizedStructSizes.back();
        return ssi.size - (pos - ssi.startPos);
    }

    void writeNum(uint64_t val, size_t len);
    
    uint64_t readNum(size_t len);

    /** Writes the given 8-bit integer to the buffer
     *  @param val  8-bit integer value to marshal
     */
    void writeByte(uint8_t val)
    {
        if (checkLen(1))
            buf[pos++] = val & 0x00FF;
    }

    /** Marshals the given 16-bit integer to this buffer.
     *  @param val  16-bit integer value to marshal
     */
    void writeShort(uint16_t val) { writeNum(val, 2); }

    /** Marshals the given 32-bit integer to this buffer.
     *  @param val  32-bit integer value to marshal
     */
    void writeInt(uint32_t val) { writeNum(val, 4); }

    /** Marshals the given 64-bit integer to this buffer.
     *  @param val  64-bit integer value to marshal
     */
    void writeInt64(uint64_t val) { writeNum(val, 8); }


    /** Unmarshals an 8-bit integer from this buffer.
     *  @return Unmarshaled 8-bit integer
     */
    uint8_t readByte()
    {
        if (!checkLen(1))
            return 0;
        return buf[pos++];
    }

    /** Unmarshals a 16-bit integer from this buffer.
     *  @return Unmarshaled 16-bit integer
     */
    uint16_t readShort() { return (uint16_t)readNum(2); }

    /** Unmarshals a 32-bit integer from this buffer.
     *  @return Unmarshaled 32-bit integer
     */
    uint32_t readInt() { return (uint32_t)readNum(4); }

    /** Unmarshals a 64-bit integer from this buffer.
     *  @return Unmarshaled 64-bit integer
     */
    uint64_t readInt64() { return readNum(8); }

    /** Marshalls the given byte buffer using length-prefixed format.
     *  @param data  Byte buffer to marshal
     *  @param sizeLen  Length of the size prefix in bytes
     */
    void writeSizedByteBuf(const ByteVec& data, size_t sizeLen = 2)
    {
        writeNum((uint32_t)data.size(), sizeLen);
        writeByteBuf(data);
    }

    /** Unmarshals a byte buffer from its size-prefixed representation in the TPM wire format.
     *  @param sizeLen  Length of the size prefix in bytes
     *  @return  Unmarshaled byte buffer
     */
    ByteVec readSizedByteBuf(size_t sizeLen = 2)
    {
        size_t len = (size_t)readNum(sizeLen);
        size_t start = pos;
        pos += len;
        return ByteVec(buf.begin() + start, buf.begin() + pos);
    }

    /** Marshals an object implementing TpmMarshaler interface.
     *  @param obj  Object to marshal
     */
    void writeObj(const TpmMarshaller& obj) { obj.toTpm(*this); }

    /** Unmarshals the contents of an object implementing TpmMarshaler interface.
     *  @param obj  Object to unmarshal
     */
    void readObj(TpmMarshaller& obj) { obj.fromTpm(*this); }

    template<class T>
    void writeSizedObj(const T& obj)
    {
        // Length of the array size is always 2 bytes
        const size_t lenSize = 2;
        if (!checkLen(lenSize))
            return;

        // Remember position to marshal the size of the data structure
        size_t sizePos = pos;
        // Account for the reserved size area
        pos += lenSize;
        // Marshal the object
        obj.toTpm(*this);
        // Calc marshaled object len
        size_t objLen = pos - (sizePos + lenSize);
        // Marshal it in the appropriate position
        pos = sizePos;
        writeShort((uint16_t)objLen);
        pos += objLen;
    }

    template<class T>
    void readSizedObj(T& obj)
    {
        // Length of the array size is always 2 bytes
        size_t size = readShort();
        if (size == 0)
            return;

        sizedStructSizes.push_back({pos, size});
        obj.fromTpm(*this);
        sizedStructSizes.pop_back();
    }

    /** Marshalls the given byte buffer without length prefix.
     *  @param data  Byte buffer to marshal
     */
    void writeByteBuf(const ByteVec& data)
    {
        if (data.empty() || !checkLen(data.size()))
            return;
        std::copy(data.cbegin(), data.cend(), buf.begin() + pos);
        pos += data.size();
    }

    /** Unmarshalls a byte buffer of the given size.
     *  @param data  Unmarshaled byte buffer
     */
    ByteVec readByteBuf(size_t size)
    {
        if (!checkLen(size))
            return ByteVec();
        auto start = buf.begin() + pos;
        ByteVec newBuf(start, start + size);
        pos += size;
        return newBuf;
    }

    template<class T>
    void writeObjArr(const vector<T>& arr)
    {
        // Length of the array size is always 4 bytes
        writeInt((uint32_t)arr.size());
        for (auto elt: arr)
        {
            if (!isOk())
                break;
            elt.toTpm(*this);
        }
    }

    template<class T>
    void readObjArr(vector<T>& arr)
    {
        // Length of the array size is always 4 bytes
        size_t len = readInt();
        if (len == 0)
            return arr.clear();

        arr.resize(len);
        for (size_t i = 0; i < len; ++i)
        {
            if (!isOk())
                break;
            arr[i].fromTpm(*this);
        }
    }

    template<typename T>
    void writeValArr(const vector<T>& arr, size_t valSize)
    {
        // Length of the array size is always 4 bytes
        writeInt((uint32_t)arr.size());
        for (auto val: arr)
        {
            if (!isOk())
                break;
            writeNum(val, valSize);
        }
    }

    template<typename T>
    void readValArr(vector<T>& arr, size_t valSize)
    {
        // Length of the array size is always 4 bytes
        size_t len = readInt();
        if (len == 0)
            return arr.clear();

        arr.resize(len);
        for (size_t i = 0; i < len; ++i)
        {
            if (!isOk())
                break;
            arr[i] = (T)(uint32_t)readNum(valSize);
        }
    }

    void writeNumAtPos(size_t val, size_t pos, size_t len = 4)
    {
        size_t origCurPos = curPos();
        curPos(pos);
        writeNum(val, len);
        curPos(origCurPos);
    }
}; // class TpmBuffer

class _DLLEXP_ _TPMT_SYM_DEF_OBJECT;
class _DLLEXP_ _TPMT_SYM_DEF;

void nonStandardToTpm(const _TPMT_SYM_DEF& sd, TpmBuffer& buf);
void nonStandardToTpm(const _TPMT_SYM_DEF_OBJECT& sdo, TpmBuffer& buf);

void nonStandardFromTpm(_TPMT_SYM_DEF& sd, TpmBuffer& buf);
void nonStandardFromTpm(_TPMT_SYM_DEF_OBJECT& sdo, TpmBuffer& buf);

_TPMCPP_END

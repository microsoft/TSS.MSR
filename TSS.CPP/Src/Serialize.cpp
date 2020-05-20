/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "Serialize.h"

_TPMCPP_BEGIN


void JsonSerializer::Reset()
{
    my_buf.clear();
    my_pos = 0;
    my_indent = 0;
    my_commaExpected = false;
    my_newLine = true;
}

void JsonSerializer::reset()
{
    Reset();
    BeginWriteObj();
}

void JsonSerializer::reset(const string& json)
{
    Reset();
    my_buf = json;
    BeginReadObj();
}

void JsonSerializer::Write(const string& str)
{
    if (my_newLine && !(str.empty() || str == eol))
        Indent();
    my_buf += str;
    my_newLine = str.back() == '\n';
}

void JsonSerializer::WriteLine(const string& str)
{
    Write(str + eol);
}
    
void JsonSerializer::WriteComma(bool newLine)
{
    if (my_commaExpected)
        Write("," + string(newLine ? eol : " "));
}

void JsonSerializer::BeginWriteObj()
{
    WriteComma();
    WriteLine("{");
    TabIn();
    my_commaExpected = false;
}

void JsonSerializer::EndWriteObj()
{
    TabOut();
    WriteLine();
    Write("}");
    my_commaExpected = true;
}

void JsonSerializer::BeginWriteArr()
{
    WriteComma();
    Write("[");
    my_commaExpected = false;
}

void JsonSerializer::EndWriteArr()
{
    Write("]");
    my_commaExpected = true;
}

void JsonSerializer::BeginWriteNamedEntry(bool allowNameless)
{
    if (!my_nextName.empty())
    {
        WriteComma();
        Write(quote + my_nextName + quote + " : ");
    }
    else if (!allowNameless)
        throw std::runtime_error("Value name is missing");
    my_nextName.clear();
    my_commaExpected = false;
}

void JsonSerializer::SkipSpace()
{
    size_t pos = my_buf.find_first_not_of(" \t\r\n", my_pos);
    if (pos == npos)
        throw runtime_error("Invalid JSON: Premature end @pos " + to_string(my_pos));
    my_pos = pos;
}

void JsonSerializer::Find(char token)
{
    size_t pos = my_buf.find(token, my_pos);
    if (pos == npos || my_buf[pos] != token)
        throw runtime_error("Invalid JSON: '" + string(1, token) +  "' missing @pos " + to_string(my_pos));
    // Skip the found token
    my_pos = pos + 1;
}

void JsonSerializer::Next(char token)
{
    SkipSpace();
    if (my_buf[my_pos] != token)
        throw runtime_error("Invalid JSON: '" + string(1, token) +  "' missing @pos " + to_string(my_pos));
    // Skip the found token
    ++my_pos;
}

void JsonSerializer::ReadComma()
{
    if (my_commaExpected)
        Next(',');
}

void JsonSerializer::BeginReadObj()
{
    ReadComma();
    Next('{');
    my_commaExpected = false;
}

void JsonSerializer::EndReadObj()
{
    Next('}');
    my_commaExpected = true;
}

void JsonSerializer::BeginReadArr()
{
    ReadComma();
    Next('[');
    my_commaExpected = false;
}

void JsonSerializer::EndReadArr()
{
    Next(']');
    my_commaExpected = true;
}

void JsonSerializer::BeginReadNamedEntry(bool allowNameless)
{
    SkipSpace();
    if (my_buf[my_pos] == '{')
        return;

    ReadComma();
    Next(quote);
    size_t b = my_pos;
    Find(quote);
    my_lastName = my_buf.substr(b, my_pos - b - 1);
    if (!my_nextName.empty() && my_nextName != my_lastName)
        throw runtime_error("Wrong value name: '" + my_lastName + "' instead of '" + my_nextName + "'");
    my_nextName.clear();
    Next(':');
    SkipSpace();
    my_commaExpected = false;
}

void JsonSerializer::WriteArrSize(size_t size)
{
    string curName = my_nextName;
    my_nextName += "Size";
    writeNum(size);
    my_nextName = curName;
}

size_t JsonSerializer::ReadArrSize()
{
    string curName = my_nextName;
    if (!my_nextName.empty())
        my_nextName += "Size";

    size_t size = (size_t)readNum();
    my_nextName = curName;
    return size;
}

void JsonSerializer::WriteNum(uint64_t val)
{
    WriteComma(false);
    Write(to_string(val));
    my_commaExpected = true;
}

uint64_t JsonSerializer::ReadNum()
{
    ReadComma();
    my_commaExpected = true;
    SkipSpace();
    size_t b = my_pos;
    while (isdnum(my_buf[my_pos]))
        ++my_pos;
    return std::stoull(my_buf.substr(b, my_pos - b));
}

void JsonSerializer::WriteObj(const ISerializable& obj)
{
    BeginWriteObj();
    obj.Serialize(*this);
    EndWriteObj();
}

void JsonSerializer::ReadObj(ISerializable& obj)
{
    BeginReadObj();
    obj.Deserialize(*this);
    EndReadObj();
}

void JsonSerializer::writeNum(uint64_t val)
{
    BeginWriteNamedEntry();
    WriteNum(val);
}

uint64_t JsonSerializer::readNum()
{
    BeginReadNamedEntry();
    return ReadNum();
}

void JsonSerializer::writeEnum(uint32_t val, size_t /*enumID*/)
{
    writeNum(val);
}

uint32_t JsonSerializer::readEnum(size_t /*enumID*/)
{
    return (uint32_t)readNum();
}

void JsonSerializer::writeObj(const ISerializable& obj)
{
    BeginWriteNamedEntry(true);
    WriteObj(obj);
}

void JsonSerializer::readObj(ISerializable& obj)
{
    BeginReadNamedEntry(true);
    ReadObj(obj);
}

void JsonSerializer::writeSizedByteBuf(const ByteVec& buf)
{
    WriteArrSize(buf.size());
    BeginWriteNamedEntry();
    BeginWriteArr();
    for (size_t i = 0; i < buf.size(); ++i)
        WriteNum(buf[i]);
    EndWriteArr();
}

ByteVec JsonSerializer::readSizedByteBuf()
{
    size_t size = ReadArrSize();
    ByteVec buf(size);
    BeginReadNamedEntry();
    BeginReadArr();
    for (size_t i = 0; i < size; ++i)
        buf[i] = (uint8_t)ReadNum();
    EndReadArr();
    return buf;
}

/** Serializes an array of serializable objects */
void JsonSerializer::writeObjArr(const vector_of_bases<ISerializable>& arr)
{
    WriteArrSize(arr.size());
    BeginWriteNamedEntry();
    BeginWriteArr();
    for (auto& obj : arr)
        WriteObj(obj);
    EndWriteArr();
}

/** Deserializes an array of serializable objects */
void JsonSerializer::readObjArr(vector_of_bases<ISerializable>&& arr)
{
    size_t size = ReadArrSize();
    arr.resize(size);
    BeginReadNamedEntry();
    BeginReadArr();
    for (auto& obj : arr)
        ReadObj(obj);
    EndReadArr();
}

/** 
    *  Serializes an array of integers
    *  @param arr  Pointer to the array.
    *  @param size  Number of elements in the array.
    *  @param valSize  Size of an array element in bytes (1, 2, 4, or 8)
    */
void JsonSerializer::writeEnumArr(const void* arr, size_t size, size_t valSize, size_t /*enumID*/)
{
    WriteArrSize(size);
    BeginWriteNamedEntry();
    BeginWriteArr();
    for (auto p = (uint8_t*)arr, end = p + size * valSize; p < end; p += valSize)
    {
        switch (valSize) {
            case 1: WriteNum(*p); break;
            case 2: WriteNum(*(uint16_t*)p); break;
            case 4: WriteNum(*(uint32_t*)p); break;
            default: throw invalid_argument("Only enums of 1-, 2-, and 4-byte size are supported");
        }
    }
    EndWriteArr();
}

/** 
    *  Deserializes an array of integers
    *  @param arr  Pointer to the array. If nullptr, then param `size` is ignored and the method
    *              returns the number of array elememnts in the serialization buffer.
    *  @param size  Number of elements in the array. Ignored if `arr` is nullptr. Otherwise
    *               must be the same as the number of array elememnts in the serialization buffer.
    *  @param valSize  Size of an array element in bytes (1, 2, 4, or 8)
    *  @returns  The size of the array
    */
size_t JsonSerializer::readEnumArr(void* arr, size_t arrSize, size_t valSize, size_t /*enumID*/)
{
    size_t pos = my_pos;
    size_t size = ReadArrSize();
    if (!arr)
    {
        my_pos = pos;
        return size;
    }
    if (size != arrSize)
        throw runtime_error("Serialized array size (" + to_string(size) + 
                            ") is diffrent from the expected size (" + to_string(arrSize) + ")");
    BeginReadNamedEntry();
    BeginReadArr();
    for (auto p = (uint8_t*)arr, end = p + size * valSize; p < end; p += valSize)
    {
        uint64_t val = ReadNum();
        switch (valSize) {
            case 1: *p = (uint8_t)val; break;
            case 2: *(uint16_t*)p = (uint16_t)val; break;
            case 4: *(uint32_t*)p = (uint32_t)val; break;
            default: throw invalid_argument("Only enums of 1-, 2-, and 4-byte size are supported");
        }
    }
    EndReadArr();
    return size;
}


_TPMCPP_END

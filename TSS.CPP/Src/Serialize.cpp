/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "Serialize.h"


_TPMCPP_BEGIN

using namespace std;


static _NORETURN_ void throwMissingAt(const string& what, size_t at)
{
    throw runtime_error("Invalid serial data: '" + what +  "' missing at pos " + to_string(at));
}

static _NORETURN_ void throwWrongName(const string& what, const string& actual, const string& expected)
{
    throw runtime_error("Wrong " + what + " name: '" + actual + "' instead of '" + expected + "'");
}


//
// TextSerializer
// 

void TextSerializer::Reset()
{
    my_buf.clear();
    my_pos = 0;
    my_indent = 0;
    my_commaExpected = false;
    my_newLine = true;
    my_valName = my_valType = my_sizeName = my_sizeType = nstr;
}

void TextSerializer::reset()
{
    Reset();
}

void TextSerializer::reset(const string& textBuf)
{
    Reset();
    my_buf = textBuf;
}

TextSerializer& TextSerializer::with(const char* name, const char* type,
                                     const char* sizeName, const char* sizeType)
{
    my_valName = name;
    my_valType = type;
    my_sizeName = sizeName;
    my_sizeType = sizeType;
    return *this;
}

void TextSerializer::Write(const string& str)
{
    if (str == eol && my_newLine)
        return;
    if (my_newLine && !str.empty())
        Indent();
    my_buf += str;
    my_newLine = str.back() == *eol;
}

void TextSerializer::WriteLine(const string& str)
{
    Write(str + eol);
}
    
void TextSerializer::SkipSpace()
{
    size_t pos = my_buf.find_first_not_of(" \t\r\n", my_pos);
    if (pos == npos)
        throw runtime_error("Invalid JSON: Premature end @pos " + to_string(my_pos));
    my_pos = pos;
}

void TextSerializer::Find(char token)
{
    size_t pos = my_buf.find(token, my_pos);
    if (pos == npos || my_buf[pos] != token)
        throwMissingAt(string(1, token), my_pos);
    // Skip the found token
    my_pos = pos;
}

void TextSerializer::Next(char token)
{
    SkipSpace();
    if (my_buf[my_pos] != token)
        throwMissingAt(string(1, token), my_pos);
    // Skip the found token
    ++my_pos;
}

void TextSerializer::Next(const char* token)
{
    SkipSpace();
    size_t i = 0;
    for (; token[i]; ++i)
    {
        if (my_buf[my_pos + i] != token[i])
            throwMissingAt(token, my_pos);
    }
    // Skip the found token
    my_pos += i;
}

void TextSerializer::WriteComma(bool newLine)
{
    if (my_commaExpected)
        Write("," + string(newLine ? eol : " "));
}

void TextSerializer::ReadComma()
{
    if (my_commaExpected)
        Next(',');
}


void TextSerializer::BeginWriteObj(const char* objType)
{
    WriteComma();
    if (objType)
        Write(string(objType) + " ");
    WriteLine("{");
    TabIn();
    my_commaExpected = false;
}

void TextSerializer::BeginReadObj(const char* objType)
{
    ReadComma();
    if (objType)
        Next(objType);
    Next('{');
    my_commaExpected = false;
}

void TextSerializer::EndWriteObj()
{
    TabOut();
    WriteLine();
    Write("}");
    my_commaExpected = true;
}

void TextSerializer::EndReadObj()
{
    Next('}');
    my_commaExpected = true;
}

void TextSerializer::BeginWriteArr(bool tabIn)
{
    WriteComma();
    if (tabIn)
    {
        WriteLine("[");
        TabIn();
    }
    else
        Write("[");
    my_commaExpected = false;
}

void TextSerializer::BeginReadArr()
{
    ReadComma();
    Next('[');
    my_commaExpected = false;
}

void TextSerializer::EndWriteArr(bool tabOut)
{
    if (tabOut)
    {
        TabOut();
        WriteLine();
    }
    Write("]");
    my_commaExpected = true;
}

void TextSerializer::EndReadArr()
{
    Next(']');
    my_commaExpected = true;
}

void TextSerializer::BeginArrSizeOp()
{
    swap(my_valName, my_sizeName);
    swap(my_valType, my_sizeType);
}

void TextSerializer::EndArrSizeOp()
{
    my_valName = my_sizeName;
    my_valType = my_sizeType;
    my_sizeName = my_sizeType = nstr;
}

void TextSerializer::WriteArrSize(size_t size)
{
    BeginArrSizeOp();
    writeNum(size);
    EndArrSizeOp();
}

size_t TextSerializer::ReadArrSize()
{
    BeginArrSizeOp();
    size_t size = (size_t)readNum();
    EndArrSizeOp();
    return size;
}

void TextSerializer::writeNum(uint64_t val)
{
    BeginWriteNamedEntry();
    WriteNum(val);
}

uint64_t TextSerializer::readNum()
{
    BeginReadNamedEntry();
    return ReadNum();
}

void TextSerializer::writeEnum(uint32_t val, size_t enumID)
{
    BeginWriteNamedEntry();
    WriteEnum(val, enumID);
}

uint32_t TextSerializer::readEnum(size_t enumID)
{
    BeginReadNamedEntry();
    return ReadEnum(enumID);
}

void TextSerializer::writeObj(const ISerializable& obj)
{
    BeginWriteNamedEntry(true);
    WriteObj(obj);
}

void TextSerializer::readObj(ISerializable& obj)
{
    BeginReadNamedEntry();
    ReadObj(obj);
}

void TextSerializer::writeObjArr(const vector_of_bases<ISerializable>& arr)
{
    WriteArrSize(arr.size());
    BeginWriteNamedEntry();
    BeginWriteArr(arr.size() != 0);
    for (auto& obj : arr)
        WriteObj(obj);
    EndWriteArr(arr.size() != 0);
}

void TextSerializer::readObjArr(vector_of_bases<ISerializable>&& arr)
{
    size_t size = ReadArrSize();
    arr.resize(size);
    BeginReadNamedEntry();
    BeginReadArr();
    WriteLine();
    TabIn();
    for (auto& obj : arr)
        ReadObj(obj);
    TabOut();
    EndReadArr();
}

size_t TextSerializer::readEnumArr(void* arr, size_t arrSize, size_t valSize, size_t enumID)
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
        uint32_t val = ReadEnum(enumID);
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


//
// JsonSerializer
// 

void JsonSerializer::BeginWriteNamedEntry(bool objEntry)
{
    if (!is_empty(my_valName))
    {
        WriteComma();
        Write(quote + string(my_valName) + quote + " : ");
    }
    else 
        assert(objEntry);

    my_valName = nstr;
    my_commaExpected = false;
}

void JsonSerializer::BeginReadNamedEntry()
{
    SkipSpace();
    if (my_buf[my_pos] == '{')
        return;

    ReadComma();
    Next(quote);
    size_t b = my_pos;
    Find(quote);
    string name = my_buf.substr(b, my_pos++ - b);
    if (!is_empty(my_valName) && my_valName != name)
        throwWrongName ("value", name, my_valName);
    Next(':');
    SkipSpace();
    my_commaExpected = false;
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

void JsonSerializer::WriteEnum(uint32_t val, size_t /*enumID*/)
{
    WriteNum(val);
}

uint32_t JsonSerializer::ReadEnum(size_t /*enumID*/)
{
    return (uint32_t)ReadNum();
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


//
// PlainTextSerializer
// 

void PlainTextSerializer::WriteHexCopy(uint64_t val)
{
     Write(" (0x" + to_hex(val) + ")");
}

void PlainTextSerializer::NextHexCopy(uint64_t val)
{
    Next("(0x");
    size_t b = my_pos;
    Find(')');
    
    string hex = my_buf.substr(b, my_pos - b);
    uint64_t valHex = from_hex(hex);
    if (val != valHex)
    {
        throw runtime_error("Mismatching dec and hex values " + to_string(val) + 
                            " and 0x" + hex + " at pos " + to_string(b));
    }
    ++my_pos;
}

void PlainTextSerializer::WriteComma(bool newLine)
{
    if (my_useComma)
        TextSerializer::WriteComma(newLine);
    else if (my_commaExpected)
        WriteLine();
}

void PlainTextSerializer::ReadComma()
{
    if (my_useComma)
        TextSerializer::ReadComma();
}

void PlainTextSerializer::BeginWriteNamedEntry(bool objEntry)
{
    if (!is_empty(my_valName))
    {
        assert (!is_empty(my_valType));
        WriteComma();
        Write(string(my_valType) + " " + my_valName + " = ");
        my_valName = nstr;
        if (!objEntry)
            my_valType = nstr;
    }
    else
        assert(objEntry && is_empty(my_valType));
    my_commaExpected = false;
}

void PlainTextSerializer::BeginReadNamedEntry()
{
    SkipSpace();
    if (is_empty(my_valName))
    {
        size_t pos = my_buf.find_first_of("={", my_pos);
        if (pos != npos && my_buf[pos] == '{')
            return;

        ReadComma();
        size_t  e = my_buf.find_last_not_of(" \t", pos - 1) + 1,
                b = my_buf.find_last_of(" \t", e) + 1;
        my_valType = my_buf.c_str() + b; my_buf[e] = 0; //my_buf.substr(b, e - b);
        e = my_buf.find_last_not_of(" \t", b) + 1;
        my_valName = my_buf.c_str() + my_pos; my_buf[e] = 0; //my_buf.substr(my_pos, e - my_pos);
        my_pos = pos + 1;
    }
    else
    {
        assert(!is_empty(my_valType));
        ReadComma();
        Next(my_valType);
        SkipSpace();
        Next(my_valName);
        Next('=');
    }
    SkipSpace();
    my_commaExpected = false;
}

void PlainTextSerializer::WriteNum(uint64_t val)
{
    WriteComma(false);
    Write(to_string(val));
    WriteHexCopy(val);
    my_commaExpected = true;
}

uint64_t PlainTextSerializer::ReadNum()
{
    ReadComma();
    my_commaExpected = true;
    SkipSpace();

    size_t b = my_pos;
    while (isdnum(my_buf[my_pos]))
        ++my_pos;

    uint64_t res = std::stoull(my_buf.substr(b, my_pos - b));
    NextHexCopy(res);
    return res;
}

void PlainTextSerializer::WriteEnum(uint32_t val, size_t enumID)
{
    WriteComma(false);
    Write(EnumToStr(val, enumID));
    WriteHexCopy(val);
    my_commaExpected = true;
}

uint32_t PlainTextSerializer::ReadEnum(size_t enumID)
{
    ReadComma();
    my_commaExpected = true;
    SkipSpace();

    size_t b = my_pos;
    Find('(');

    auto enumName = my_buf.substr(b, my_pos - b - 1);
    uint32_t res = StrToEnum(enumName, enumID);
    NextHexCopy(res);
    return res;
}

void PlainTextSerializer::WriteObj(const ISerializable& obj)
{
    const char* type = my_valType == obj.TypeName() ? nstr : obj.TypeName();
    my_valType = nstr;
    BeginWriteObj(type);

    bool useComma = my_useComma;
    my_useComma = false;
    obj.Serialize(*this);
    EndWriteObj();
    my_useComma = useComma;
}

void PlainTextSerializer::ReadObj(ISerializable& obj)
{
    const char* type = my_valType == obj.TypeName() ? nstr : obj.TypeName();
    BeginReadObj(type);

    bool useComma = my_useComma;
    my_useComma = false;
    obj.Deserialize(*this);
    EndReadObj();
    my_useComma = useComma;
}

size_t PlainTextSerializer::GetCurLineLen() const
{
    if (my_newLine)
        return 0;
    size_t pos = my_buf.rfind(eol);
    return my_buf.length() - (pos == npos ? 0 : pos); 
}

void PlainTextSerializer::WriteByteBufFrag(const ByteVec& buf, size_t pos, size_t len)
{
    assert(pos + len <= buf.size());
    size_t end = pos + (len & ~(WordSize - 1));
    while (pos < end)
    {
        size_t j = pos;
        pos += WordSize;
        while (j < pos)
            Write(to_hex(buf[j++]));
        if (pos < end)
            Write(" ");
    }
    end += len & (WordSize - 1);
    while (pos < end)
        Write(to_hex(buf[pos++]));
    assert(pos == end);
}

void PlainTextSerializer::writeSizedByteBuf(const ByteVec& buf)
{
    WriteArrSize(buf.size());
    BeginWriteNamedEntry();

    bool    sepLine = false,
            indented = false,
            abbreviated = false;
    size_t pos = GetCurLineLen();
    // Byte buffer's hex representation size (including brackets)
    size_t size = buf.size() * 2 + (buf.size() / WordSize - 1) + 2;
    if (my_maxLineLen - pos <= size)
    {
        if (buf.size() <= BytesPerLine || size < my_maxLineLen - my_indent - TabSize)
        {
            WriteLine();        // keep single line representation
            TabIn();
            sepLine = true;
        }
        else if (my_precise)
            indented = true;    // multiline repr (brackets on separate lines)
        else
            abbreviated = true;
    }
    BeginWriteArr(indented);

    if (abbreviated)
    {
        WriteByteBufFrag(buf, 0, BytesPerLine/2);
        Write(" ... ");
        WriteByteBufFrag(buf, buf.size() - BytesPerLine/2, BytesPerLine/2);
    }
    else 
    {
        size_t  row = 0,
                fullRows = buf.size() / BytesPerLine;
        while (row < fullRows)
        {
            WriteByteBufFrag(buf, row++ * BytesPerLine, BytesPerLine);
            if (indented)
                WriteLine();
        }
        WriteByteBufFrag(buf, row * BytesPerLine, buf.size() % BytesPerLine);
    }

    EndWriteArr(indented);
    if (sepLine)
        TabOut();
}

ByteVec PlainTextSerializer::readSizedByteBuf()
{
    size_t size = ReadArrSize();
    ByteVec buf(size);
    BeginReadNamedEntry();
    BeginReadArr();

    size_t pos = my_buf.find_first_of(".]", my_pos);
    if (pos != npos && my_buf[pos] == '.')
    {
        // Byte buffer was serialized in non-precise mode (abbreviated).
        // Discard its contents compleletly.
        assert(my_buf[pos + 1] == '.' && my_buf[pos + 2] == '.');
        Find(']');
        ++my_pos;
        return buf;
    }

    for (size_t i = 0; i < size; ++i)
    {
        if (i % WordSize == 0)
            SkipSpace();

        string hex = my_buf.substr(my_pos, 2);
        my_pos += 2;
        buf[i] = (uint8_t)stoi(hex, nullptr, 16);
    }
    EndReadArr();
    return buf;
}

void PlainTextSerializer::writeEnumArr(const void* arr, size_t size, size_t valSize, size_t enumID)
{
    WriteArrSize(size);
    BeginWriteNamedEntry();
    BeginWriteArr(size > 1);

    bool useComma = my_useComma;
    my_useComma = true;
    for (auto p = (uint8_t*)arr, end = p + size * valSize; p < end; p += valSize)
    {
        if (size > 1)
            WriteLine();
        switch (valSize) {
            case 1: WriteEnum(*p, enumID); break;
            case 2: WriteEnum(*(uint16_t*)p, enumID); break;
            case 4: WriteEnum(*(uint32_t*)p, enumID); break;
            default: throw invalid_argument("Only enums of 1-, 2-, and 4-byte size are supported");
        }
    }
    EndWriteArr(size > 1);
    my_useComma = useComma;
}


_TPMCPP_END

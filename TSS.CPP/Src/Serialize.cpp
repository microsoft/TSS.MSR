/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "Serialize.h"

inline char getHexDigit(uint8_t d)
{
    return d < 10 ? '0' + d : 'A' + (d - 10);
}

template<typename T>
string to_hex(T val)
{
    if (!val)
        return "00";

    string res;
    T mask = 0x0F;
    // This loop would work for Java, too (Java is terrible with signed bit propagation)
    for (int offs = 0; val != 0; val &= ~mask, mask <<= 4, offs += 4)
    {
        res = getHexDigit((uint8_t)(((val & mask) >> offs) & 0x0F)) + res;
    }
    return (res.length() & 1 ? "0" : "") + res;
}



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
}

void JsonSerializer::reset(const string& json)
{
    Reset();
    my_buf = json;
}

void JsonSerializer::Write(const string& str)
{
    if (str == eol && my_newLine)
        return;
    if (my_newLine && !str.empty())
        Indent();
    my_buf += str;
    my_newLine = str.back() == *eol;
}

void JsonSerializer::WriteLine(const string& str)
{
    Write(str + eol);
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
        throwMissingAt(string(1, token), my_pos);
    // Skip the found token
    my_pos = pos;
}

void JsonSerializer::Next(char token)
{
    SkipSpace();
    if (my_buf[my_pos] != token)
        throwMissingAt(string(1, token), my_pos);
    // Skip the found token
    ++my_pos;
}

void JsonSerializer::Next(const char* token)
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

void JsonSerializer::WriteComma(bool newLine)
{
    if (my_commaExpected)
        Write("," + string(newLine ? eol : " "));
}

void JsonSerializer::ReadComma()
{
    if (my_commaExpected)
        Next(',');
}

void JsonSerializer::BeginWriteNamedEntry(bool objEntry)
{
    if (!my_valName.empty())
    {
        WriteComma();
        Write(quote + my_valName + quote + " : ");
    }
    else 
        assert(objEntry);

    my_valName.clear();
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
    if (!my_valName.empty() && my_valName != name)
        throwWrongName ("value", name, my_valName);
    Next(':');
    SkipSpace();
    my_commaExpected = false;
}

void JsonSerializer::BeginWriteObj(const char* objType)
{
    WriteComma();
    if (objType)
        Write(string(objType) + " ");
    WriteLine("{");
    TabIn();
    my_commaExpected = false;
}

void JsonSerializer::BeginReadObj(const char* objType)
{
    ReadComma();
    if (objType)
        Next(objType);
    Next('{');
    my_commaExpected = false;
}

void JsonSerializer::EndWriteObj()
{
    TabOut();
    WriteLine();
    Write("}");
    my_commaExpected = true;
}

void JsonSerializer::EndReadObj()
{
    Next('}');
    my_commaExpected = true;
}

void JsonSerializer::BeginWriteArr(bool tabIn)
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

void JsonSerializer::BeginReadArr()
{
    ReadComma();
    Next('[');
    my_commaExpected = false;
}

void JsonSerializer::EndWriteArr(bool tabOut)
{
    if (tabOut)
    {
        TabOut();
        WriteLine();
    }
    Write("]");
    my_commaExpected = true;
}

void JsonSerializer::EndReadArr()
{
    Next(']');
    my_commaExpected = true;
}

void JsonSerializer::WriteArrSize(size_t size)
{
    string  curName = my_valName,
            curType = my_valType;
    // TODO: replace fixes name and type with real spec names
    my_valName += "Size";
    my_valType = "UINT";
    writeNum(size);
    my_valName = curName;
    my_valType = curType;
}

size_t JsonSerializer::ReadArrSize()
{
    string  curName = my_valName,
            curType = my_valType;
    // TODO: replace fixes name and type with real spec names
    if (!my_valName.empty())
        my_valName += "Size";
    my_valType = "UINT";

    size_t size = (size_t)readNum();
    my_valName = curName;
    my_valType = curType;
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

void JsonSerializer::writeEnum(uint32_t val, size_t enumID)
{
    BeginWriteNamedEntry();
    WriteEnum(val, enumID);
}

uint32_t JsonSerializer::readEnum(size_t enumID)
{
    BeginReadNamedEntry();
    return ReadEnum(enumID);
}

void JsonSerializer::writeObj(const ISerializable& obj)
{
    BeginWriteNamedEntry(true);
    WriteObj(obj);
}

void JsonSerializer::readObj(ISerializable& obj)
{
    BeginReadNamedEntry();
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

void JsonSerializer::writeObjArr(const vector_of_bases<ISerializable>& arr)
{
    WriteArrSize(arr.size());
    BeginWriteNamedEntry();
    BeginWriteArr(arr.size() != 0);
    for (auto& obj : arr)
        WriteObj(obj);
    EndWriteArr(arr.size() != 0);
}

void JsonSerializer::readObjArr(vector_of_bases<ISerializable>&& arr)
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

size_t JsonSerializer::readEnumArr(void* arr, size_t arrSize, size_t valSize, size_t enumID)
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


void TextSerializer::WriteHexCopy(uint64_t val)
{
     Write(" (0x" + to_hex(val) + ")");
}

void TextSerializer::NextHexCopy(uint64_t val)
{
    Next("(0x");
    size_t b = my_pos;
    Find(')');
    
    string hex = my_buf.substr(b, my_pos - b);
    uint64_t valHex = stoull(hex, nullptr, 16);
    if (val != valHex)
    {
        throw runtime_error("Mismatching dec and hex values " + to_string(val) + 
                            " and 0x" + hex + " at pos " + to_string(b));
    }
    ++my_pos;
}

void TextSerializer::WriteComma(bool newLine)
{
    if (my_useComma)
        JsonSerializer::WriteComma(newLine);
    else if (my_commaExpected)
        WriteLine();
}

void TextSerializer::ReadComma()
{
    if (my_useComma)
        JsonSerializer::ReadComma();
}

void TextSerializer::BeginWriteNamedEntry(bool objEntry)
{
    if (!my_valName.empty())
    {
        assert (!my_valType.empty());
        WriteComma();
        Write(my_valType + " " + my_valName + " = ");
        my_valName.clear();
        if (!objEntry)
            my_valType.clear();
    }
    else
        assert(objEntry && my_valType.empty());
    my_commaExpected = false;
}

void TextSerializer::BeginReadNamedEntry()
{
    SkipSpace();
    if (my_valName.empty())
    {
        size_t pos = my_buf.find_first_of("={", my_pos);
        if (pos != npos && my_buf[pos] == '{')
            return;

        ReadComma();
        size_t  e = my_buf.find_last_not_of(" \t", pos - 1),
                b = my_buf.find_last_of(" \t", e);
        my_valType = my_buf.substr(b + 1, e - b);
        e = my_buf.find_last_not_of(" \t", b);
        my_valName = my_buf.substr(my_pos, e - my_pos + 1);
        my_pos = pos + 1;
    }
    else
    {
        assert(!my_valType.empty());
        ReadComma();
        Next(my_valType.c_str());
        SkipSpace();
        Next(my_valName.c_str());
        Next('=');
    }
    SkipSpace();
    my_commaExpected = false;
}

void TextSerializer::WriteNum(uint64_t val)
{
    WriteComma(false);
    Write(to_string(val));
    WriteHexCopy(val);
    my_commaExpected = true;
}

uint64_t TextSerializer::ReadNum()
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

void TextSerializer::WriteEnum(uint32_t val, size_t enumID)
{
    WriteComma(false);
    Write(EnumToStr(val, enumID));
    WriteHexCopy(val);
    my_commaExpected = true;
}

uint32_t TextSerializer::ReadEnum(size_t enumID)
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

void TextSerializer::WriteObj(const ISerializable& obj)
{
    const char* type = my_valType == obj.TypeName() ? nullptr : obj.TypeName();
    my_valType.clear();
    BeginWriteObj(type);

    bool useComma = my_useComma;
    my_useComma = false;
    obj.Serialize(*this);
    EndWriteObj();
    my_useComma = useComma;
}

void TextSerializer::ReadObj(ISerializable& obj)
{
    const char* type = my_valType == obj.TypeName() ? nullptr : obj.TypeName();
    BeginReadObj(type);

    bool useComma = my_useComma;
    my_useComma = false;
    obj.Deserialize(*this);
    EndReadObj();
    my_useComma = useComma;
}

size_t TextSerializer::GetCurLineLen() const
{
    if (my_newLine)
        return 0;
    size_t pos = my_buf.rfind(eol);
    return my_buf.length() - (pos == npos ? 0 : pos); 
}

void TextSerializer::WriteByteBufFrag(const ByteVec& buf, size_t pos, size_t len)
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

void TextSerializer::writeSizedByteBuf(const ByteVec& buf)
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

ByteVec TextSerializer::readSizedByteBuf()
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

void TextSerializer::writeEnumArr(const void* arr, size_t size, size_t valSize, size_t enumID)
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

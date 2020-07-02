/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once
//#include <stdexcept>

_TPMCPP_BEGIN

using namespace std;

struct Serializer;

struct _DLLEXP_ Serializable
{
    virtual ~Serializable() {}

    /** Serializes this structure into the given buffer managed by a serializer */
    virtual void Serialize(Serializer& buf) const = 0;

    /** Deserialize from the given buffer managed by a serializer */
    virtual void Deserialize(Serializer& buf) = 0;

    /** @return  Type of the object/value deserialized by the last read operation */
    virtual const char* TypeName () const = 0;
};

struct _DLLEXP_ Serializer
{
protected:
    /** Serializes an array of serializable objects */
    virtual void writeObjArr(const vector_of_bases<Serializable>& arr) = 0;

    /** Deserializes an array of serializable objects */
    virtual void readObjArr(vector_of_bases<Serializable>&& arr) = 0;

    /** Serializes an array of integers
     *  @param arr  Pointer to the array.
     *  @param size  Number of elements in the array.
     *  @param valSize  Size of an array element in bytes (1, 2, 4, or 8)
     */
    virtual void writeEnumArr(const void* arr, size_t size, size_t valSize, size_t enumID) = 0;

    /** Deserializes an array of integers
     *  @param arr  Pointer to the array. If nullptr, then param `size` is ignored and the method
     *              returns the number of array elememnts in the serialization buffer.
     *  @param size  Number of elements in the array. Ignored if `arr` is nullptr. Otherwise
     *               must be the same as the number of array elememnts in the serialization buffer.
     *  @param valSize  Size of an array element in bytes (1, 2, 4, or 8)
     *  @return  The size of the array
     */
    virtual size_t readEnumArr(void* arr, size_t size, size_t valSize, size_t enumID) = 0;

public:
    virtual ~Serializer() {}

    /** Clears the serialization buffer and initializes new serialization sequence. */
    virtual void reset() = 0;

    /** @return  Current reading position in the serialization buffer. */
    virtual size_t getCurPos () const = 0;

    /** Sets new current reading position in the serialization buffer. */
    virtual void setCurPos (size_t pos) = 0;

    /** Sets qualifiers of the object/value serialized by the next write operation or expected 
     *  qualifiers of the object/value read by the next read operation. Erased by successful
     *  read/write operation.
     *  NOTE: Implementations are allowed to simply store the copies of argument pointers.
     *        Thus the caller must ensure that they remain valid until the end of the next
     *        serialization operation.
     *  @param name  Object/value name
     *  @param type  Object/value type name
     *  @param sizeName  Array size field name
     *  @param sizeType  Array size field type name
     *  @return  A reference to this serializer object
     */
    virtual Serializer& with(const char* name = "", const char* type = "",
                              const char* sizeName = "", const char* sizeType = "") = 0;

    /** @return  Name of the object/value deserialized by the last read operation */
    virtual const char* name() const = 0;

    /** @return  Type of the object/value deserialized by the last read operation */
    virtual const char* type() const = 0;

    virtual void writeObj(const Serializable& obj) = 0;
    virtual void readObj(Serializable& obj) = 0;

    /** Serializes the given enum value
     *  @param  val  Enum value to serialize
     *  @param  enumID  Opaque handle used to covert between numeric and textual representation of the enum value
     */
    virtual void writeEnum(uint32_t val, size_t enumID) = 0;

    /** Deserializes an enum value */
    virtual uint32_t readEnum(size_t enumID) = 0;

    virtual void writeSizedByteBuf(const ByteVec& buf) = 0;

    virtual ByteVec readSizedByteBuf() = 0;

    /** Serializes the given integer value */
    virtual void writeNum(uint64_t val) = 0;

    /** Deserializes an integer value */
    virtual uint64_t readNum() = 0;
    
    //
    // Convenience (typed) helpers
    //

    /** Serializes the given 8-bit integer */
    void writeByte(uint8_t val) { writeNum(val); }

    /**Serializes the given 16-bit integer */
    void writeShort(uint16_t val) { writeNum(val); }

    /** Serializes the given 32-bit integer */
    void writeInt(uint32_t val) { writeNum(val); }

    /** Serializes the given 64-bit integer */
    void writeInt64(uint64_t val) { writeNum(val); }

    /** Deserializes an 8-bit integer from the serialization buffer */
    uint8_t readByte() { return (uint8_t)readNum(); }

    /** Deserializes a 16-bit integer from the serialization buffer */
    uint16_t readShort() { return (uint16_t)readNum(); }

    /** Deserializes a 32-bit integer from the serialization buffer */
    uint32_t readInt() { return (uint32_t)readNum(); }

    /** Deserializes a 64-bit integer from the serialization buffer */
    uint64_t readInt64() { return readNum(); }


    /** Serializes the given enum value */
    template<class E>
    void writeEnum(E val) { writeEnum(val, typeid(E).hash_code()); }

    /** Deserializes an enum value */
    template<class E>
    void readEnum(E& e) { e = (E)readEnum(typeid(E).hash_code()); }

    /** Serializes an array of serializable objects */
    template<class T>
    void writeObjArr(const vector<T>& arr)
    {
        writeObjArr(to_base<Serializable>(arr));
    }

    /** Deserializes an array of serializable objects */
    template<class T>
    void readObjArr(vector<T>& arr)
    {
        readObjArr(to_base<Serializable>(arr));
    }

    /** Serializes an array of enums */
    template<typename E>
    void writeEnumArr(const vector<E>& arr)
    {
        writeEnumArr(arr.data(), arr.size(), sizeof(E), typeid(E).hash_code());
    }

    /** Deserializes an array of enums */
    template<typename E>
    void readEnumArr(vector<E>& arr)
    {
        size_t size = readEnumArr(nullptr, 0, sizeof(E), 0);
        arr.resize(size);
        readEnumArr(nullptr, arr.size(), sizeof(E), typeid(E).hash_code());
    }
}; // interface Serializer


struct _DLLEXP_ BinarySerializer : Serializer
{
    /** Initializes new deserialization sequence.
     *  @throws  exception if this serializer does not use binary representation. */
    virtual void reset(const ByteVec&) = 0;

    /** @return  Buffer with serialized data.
     *  @throws  exception if this serializer does not use binary representation. */
    virtual const ByteVec& getByteBuffer() const = 0;
};


class _DLLEXP_ TextSerializer : public Serializer
{
    void BeginArrSizeOp();
    void EndArrSizeOp();

protected:
    static constexpr size_t TabSize = 4;
    static constexpr auto eol = "\n";
    static constexpr auto nstr = "";
    static constexpr auto npos = string::npos;

    string  my_buf;
    const char  *my_valName, *my_valType,
                *my_sizeName, *my_sizeType;

    size_t  my_pos;
    int     my_indent;
    bool    my_commaExpected,
            my_newLine;

    static _NORETURN_ void throwUnsupported()
    {
        throw runtime_error("This serializer does not use binary representation");
    }

    void Reset();

    void TabIn() { my_indent += TabSize; }
    void TabOut() { my_indent -= TabSize; }
    void Indent() { my_buf += string(my_indent, ' '); }
    
    void Write(const string& str);
    void WriteLine(const string& str = "");

    /** If the current reading position is at a space char (' ', '\t', '\r', '\n'),
     *  moves it to the next non-space char. */
    void SkipSpace();

    /** Moves the current reading position to the the next occurrence of the given token. */
    void Find(char token);

    /** Makes sure that the next non-space (see `SkipSpace`) char is the given token,
     *  and moves the current reading position to the char following it. */
    void Next(char token);

    /** Makes sure that the next non-space (see `SkipSpace`) char begins a substring equal to
     *  the given token, and moves the current reading position to the char following it. */
    void Next(const char* token);

    virtual void WriteComma(bool newLine = true);
    virtual void ReadComma();

    virtual void BeginWriteObj(const char* objType = nullptr);
    virtual void BeginReadObj(const char* objType = nullptr);

    virtual void EndWriteObj();
    virtual void EndReadObj();

    virtual void BeginWriteArr(bool tabIn = false);
    virtual void BeginReadArr();

    virtual void EndWriteArr(bool tabOut = false);
    virtual void EndReadArr();

    virtual void BeginWriteNamedEntry(bool objEntry = false) = 0;
    virtual void BeginReadNamedEntry() = 0;

    virtual void WriteArrSize(size_t size);
    virtual size_t ReadArrSize();

    virtual void WriteNum(uint64_t val) = 0;
    virtual uint64_t ReadNum() = 0;

    virtual void WriteEnum(uint32_t val, size_t enumID) = 0;
    virtual uint32_t ReadEnum(size_t enumID) = 0;

    virtual void WriteObj(const Serializable& obj) = 0;
    virtual void ReadObj(Serializable& obj) = 0;

    static bool isdnum(char c) { return isdigit(c) || c == '-' || c == '+'; }
    static bool is_empty(const char* str) { return !str || str[0] == 0; }

public:
    TextSerializer() { reset(); }
    TextSerializer(const string& serialText) { reset(serialText); }

    //
    // Serializer methods
    //

    virtual void reset();

    /** Initializes new deserialization sequence from the given serialized text.
     *  @throws  exception if this serializer does not use text representation.
     */
    virtual void reset(const string& text);

    /** @return  Reference to the underlying string buffer with the serialized data.
     *  @throws  Exception if this serializer does not use text representation.
     */
    virtual const string& getTextBuffer() const { return my_buf; }

    virtual size_t getCurPos () const { return my_pos; }
    virtual void setCurPos (size_t pos) { my_pos = pos; }

    virtual TextSerializer& with(const char* name = "", const char* type = "",
                                 const char* sizeName = "", const char* sizeType = "");

    virtual const char* name() const { return my_valName; }
    virtual const char* type() const { return my_valType; }

    virtual void writeNum(uint64_t val);
    virtual uint64_t readNum();

    virtual void writeEnum(uint32_t val, size_t /*enumID*/ = 0);
    virtual uint32_t readEnum(size_t /*enumID*/ = 0);

    virtual void writeObj(const Serializable& obj);
    virtual void readObj(Serializable& obj);

    virtual void writeSizedByteBuf(const ByteVec& buf) = 0;
    virtual ByteVec readSizedByteBuf() = 0;

    virtual void writeObjArr(const vector_of_bases<Serializable>& arr);
    virtual void readObjArr(vector_of_bases<Serializable>&& arr);

    virtual void writeEnumArr(const void* arr, size_t size, size_t valSize, size_t enumID) = 0;
    virtual size_t readEnumArr(void* arr, size_t arrSize, size_t valSize, size_t enumID);

    //
    // Helpers & aliases for compatibility with the original TSS.CPP API
    //
    const string& ToString() const { return getTextBuffer(); }
    const string& Serialize(const Serializable* obj) { writeObj(*obj); return ToString(); }
    bool Deserialize(Serializable* obj) { readObj(*obj); return true; }
}; // class TextSerializer


class _DLLEXP_ JsonSerializer : public TextSerializer
{
protected:
    static constexpr auto quote = '\"';

    virtual void BeginWriteNamedEntry(bool objEntry = false);
    virtual void BeginReadNamedEntry();

    virtual void WriteNum(uint64_t val);
    virtual uint64_t ReadNum();

    virtual void WriteEnum(uint32_t val, size_t enumID);
    virtual uint32_t ReadEnum(size_t enumID);

    virtual void WriteObj(const Serializable& obj);
    virtual void ReadObj(Serializable& obj);

public:
    JsonSerializer() {}
    JsonSerializer(const string& json) : TextSerializer(json) {}

    virtual void writeSizedByteBuf(const ByteVec& buf);
    virtual ByteVec readSizedByteBuf();

    virtual void writeEnumArr(const void* arr, size_t size, size_t valSize, size_t /*enumID*/);
}; // class JsonSerializer


class _DLLEXP_ PlainTextSerializer : public TextSerializer
{
    /** Default value for `my_maxLineLen` */
    constexpr static size_t DefaultMaxLineLen = 120;

    /** Size of byte group printed without separating space between them */
    constexpr static size_t WordSize = 4;

    /** Max number of bytes per line for byte buffer hex representation */
    constexpr static size_t BytesPerLine = 32;

protected:
    bool my_useComma = false;
    
    /** Output only beginning and trailing parts of byte buffers not fitting the 
     * `my_maxLineLen` setting requirements */
    bool my_precise = false;

    /** Best effort (not guaranteed) limit of the serialized text line length */
    size_t my_maxLineLen = DefaultMaxLineLen;

    size_t GetCurLineLen() const;

    void WriteHexCopy(uint64_t val);

    /** Makes sure that the next non-space substring is a perenthesized hex representation
     *  of the given value, and moves the current reading position to the char following it */
    void NextHexCopy(uint64_t val);

    void WriteByteBufFrag(const ByteVec& buf, size_t pos, size_t len);

    virtual void WriteComma(bool newLine = true);
    virtual void ReadComma();

    virtual void BeginWriteNamedEntry(bool objEntry = false);
    virtual void BeginReadNamedEntry();

    virtual void WriteNum(uint64_t val);
    virtual uint64_t ReadNum();

    virtual void WriteEnum(uint32_t val, size_t enumID);
    virtual uint32_t ReadEnum(size_t enumID);

    virtual void WriteObj(const Serializable& obj);
    virtual void ReadObj(Serializable& obj);

public:
    PlainTextSerializer(bool precise = true, size_t maxLineLen = DefaultMaxLineLen)
        : my_precise(precise), my_maxLineLen(maxLineLen)
    {}
    PlainTextSerializer(const string& textBuf) : TextSerializer(textBuf) {}

    virtual void writeSizedByteBuf(const ByteVec& buf);
    virtual ByteVec readSizedByteBuf();

    virtual void writeEnumArr(const void* arr, size_t size, size_t valSize, size_t enumID);
}; // class TextSerializer


_TPMCPP_END

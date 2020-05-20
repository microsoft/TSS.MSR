/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once
//#include <stdexcept>

_TPMCPP_BEGIN

using namespace std;

struct ISerializer;

struct _DLLEXP_ ISerializable
{
    virtual ~ISerializable() {}

    /** Serializes this structure into the given buffer managed by a serializer */
    virtual void Serialize(ISerializer& buf) const = 0;

    /** Deserialize from the given buffer managed by a serializer */
    virtual void Deserialize(ISerializer& buf) = 0;

    /** @returns  Type of the object/value deserialized by the last read operation */
    virtual string TypeName () const = 0;
};

struct _DLLEXP_ ISerializer
{
protected:
    /** Serializes an array of serializable objects */
    virtual void writeObjArr(const vector_of_bases<ISerializable>& arr) = 0;

    /** Deserializes an array of serializable objects */
    virtual void readObjArr(vector_of_bases<ISerializable>&& arr) = 0;

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
     *  @returns  The size of the array
     */
    virtual size_t readEnumArr(void* arr, size_t size, size_t valSize, size_t enumID) = 0;

public:
    virtual ~ISerializer() {}

    /** Clears the serialization buffer and initializes new serialization sequence. */
    virtual void reset() = 0;

    /** Initializes new deserialization sequence.
     *  @throws  exception if this serializer does not use text representation. */
    virtual void reset(const string&) = 0;

    /** Initializes new deserialization sequence.
     *  @throws  exception if this serializer does not use binary representation. */
    virtual void reset(const ByteVec&) = 0;

    /** @returns  Buffer with serialized data.
     *  @throws  exception if this serializer does not use text representation. */
    virtual const string& getTextBuffer() const = 0;

    /** @returns  Buffer with serialized data.
     *  @throws  exception if this serializer does not use binary representation. */
    virtual const ByteVec& getByteBuffer() const = 0;

    /** @returns  Current reading position in the serialization buffer. */
    virtual size_t getCurPos () const = 0;

    /** Sets new current reading position in the serialization buffer. */
    virtual void setCurPos (size_t pos) = 0;

    /** Sets qualifiers of the object/value serialized by the next write operation or expected 
     *  qualifiers of the object/value read by the next read operation. Erased by successful
     *  read/write operation.
     *  @param name  Object/value name
     *  @param type  Object/value type name
     *  @returns  A reference to this serializer object
     */
    virtual ISerializer& with(const char* name = nullptr, const char* type = nullptr) = 0;

    /** @returns  Name of the object/value deserialized by the last read operation */
    virtual const string& name () const = 0;

    /** @returns  Type of the object/value deserialized by the last read operation */
    virtual const string& type () const = 0;

    virtual void writeObj(const ISerializable& obj) = 0;
    virtual void readObj(ISerializable& obj) = 0;

    /** Serializes the given enum value
     *  @param  val  Enum value to serialize
     *  @param  enumID  Opaque handle used to covert between numeric and textual representation of the enum value
     */
    virtual void writeEnum(uint32_t val, size_t enumID) = 0;

    /** Deserializes an enum value */
    virtual uint32_t readEnum(size_t enumID) = 0;

    virtual void writeSizedByteBuf(const ByteVec& buf) = 0;

    virtual ByteVec readSizedByteBuf() = 0;

#if 1
    /** Serializes the given integer value */
    virtual void writeNum(uint64_t val) = 0;

    /** Deserializes an integer value */
    virtual uint64_t readNum() = 0;
    
    //
    // Convenience helpers
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
#else
    /** Serializes the given 8-bit integer */
    virtual void writeByte(uint8_t val) = 0;

    /**Serializes the given 16-bit integer */
    virtual void writeShort(uint16_t val) = 0;

    /** Serializes the given 32-bit integer */
    virtual void writeInt(uint32_t val) = 0;

    /** Serializes the given 64-bit integer */
    virtual void writeInt64(uint64_t val) = 0;

    /** Deserializes an 8-bit integer from the serialization buffer */
    virtual uint8_t readByte() = 0;

    /** Deserializes a 16-bit integer from the serialization buffer */
    virtual uint16_t readShort() = 0;

    /** Deserializes a 32-bit integer from the serialization buffer */
    virtual uint32_t readInt() = 0;

    /** Deserializes a 64-bit integer from the serialization buffer */
    virtual uint64_t readInt64() = 0;
#endif // Integers

    //
    // Convenience (typed) helpers
    //

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
        writeObjArr(to_base<ISerializable>(arr));
    }

    /** Deserializes an array of serializable objects */
    template<class T>
    void readObjArr(vector<T>& arr)
    {
        readObjArr(to_base<ISerializable>(arr));
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

    //
    // Compatibility aliases
    //

    string ToString() const { return getTextBuffer(); }
    void Serialize(const ISerializable* obj) { writeObj(*obj); }
    void Deserialize(ISerializable* obj) { readObj(*obj); }
}; // interface ISerializer


class _DLLEXP_ JsonSerializer : public ISerializer
{
    static constexpr auto eol = "\n";
    static constexpr auto quote = '\"';
    static constexpr auto npos = string::npos;

    string my_buf;
    string my_nextName, my_nextType;
    string my_lastName, my_lastType;

    size_t  my_pos = 0;
    int     my_indent = 0;
    bool    my_commaExpected = false,
            my_newLine = true;

    void Reset();

    void TabIn() { my_indent += 4; }
    void TabOut() { my_indent -= 4; }
    void Indent() { my_buf += string(my_indent, ' '); }
    
    void Write(const string& str);
    void WriteLine(const string& str = "");
    void WriteComma(bool newLine = true);

    void SkipSpace();
    void Next(char token);
    void Find(char token);

    void BeginWriteObj();
    void EndWriteObj();
    void BeginWriteArr();
    void EndWriteArr();
    void BeginWriteNamedEntry(bool allowNameless = false);

    void ReadComma();
    void BeginReadObj();
    void EndReadObj();
    void BeginReadArr();
    void EndReadArr();
    void BeginReadNamedEntry(bool allowNameless = false);

    void WriteArrSize(size_t size);
    size_t ReadArrSize();

    void WriteNum(uint64_t val);
    uint64_t ReadNum();

    void WriteObj(const ISerializable& obj);
    void ReadObj(ISerializable& obj);

    static _NORETURN_ void throwUnsupported() { throw exception("This serializer does not use binary representation"); }

    static bool isdnum(char c) { return isdigit(c) || c == '-' || c == '+'; }

public:
    JsonSerializer() {}
    JsonSerializer(const string& json) : my_buf(json) {}

    /** Clears the serialization buffer and initializes new serialization sequence. */
    virtual void reset();

    /** Initializes new deserialization sequence.
     *  @throws  exception if this serializer does not use text representation. */
    virtual void reset(const string& json);

    /** Not supported */
    virtual void reset(const ByteVec&) { throwUnsupported(); }

    /** @returns  Buffer with serialized data.
     *  @throws  exception if this serializer does not use text representation. */
    virtual const string& getTextBuffer() const { return my_buf; }

    /** Not supported */
    virtual const ByteVec& getByteBuffer() const { throwUnsupported(); }

    /** @returns  Current reading position in the serialization buffer. */
    virtual size_t getCurPos () const { return my_pos; }

    /** Sets new current reading position in the serialization buffer. */
    virtual void setCurPos (size_t pos) { my_pos = pos; }

    /**
     *  Sets qualifiers of the object/value serialized by the next write operation or expected 
     *  qualifiers of the object/value read by the next read operation. Erased by successful
     *  read/write operation.
     *  @param name  Object/value name
     *  @param type  Object/value type name
     *  @returns  A reference to this serializer object
     */
    virtual JsonSerializer& with(const char* name = nullptr, const char* type = nullptr)
    {
        my_nextName = name;
        my_nextType = type;
        return *this;
    }

    /** @ returns  Name of the object/value deserialized by the last read operation */
    virtual const string& name () const { return my_lastName; }

    /** @ returns  Type of the object/value deserialized by the last read operation */
    virtual const string& type () const { return my_lastType; }

    virtual void writeNum(uint64_t val);
    virtual uint64_t readNum();

    /** Serializes the given enum value */
    virtual void writeEnum(uint32_t val, size_t /*enumID*/);

    /** Deserializes an enum value */
    virtual uint32_t readEnum(size_t /*enumID*/);

    virtual void writeObj(const ISerializable& obj);
    virtual void readObj(ISerializable& obj);

    virtual void writeSizedByteBuf(const ByteVec& buf);

    virtual ByteVec readSizedByteBuf();

    /** Serializes an array of serializable objects */
    virtual void writeObjArr(const vector_of_bases<ISerializable>& arr);

    /** Deserializes an array of serializable objects */
    virtual void readObjArr(vector_of_bases<ISerializable>&& arr);

    /** 
     *  Serializes an array of integers
     *  @param arr  Pointer to the array.
     *  @param size  Number of elements in the array.
     *  @param valSize  Size of an array element in bytes (1, 2, 4, or 8)
     */
    virtual void writeEnumArr(const void* arr, size_t size, size_t valSize, size_t /*enumID*/);

    /** 
     *  Deserializes an array of integers
     *  @param arr  Pointer to the array. If nullptr, then param `size` is ignored and the method
     *              returns the number of array elememnts in the serialization buffer.
     *  @param size  Number of elements in the array. Ignored if `arr` is nullptr. Otherwise
     *               must be the same as the number of array elememnts in the serialization buffer.
     *  @param valSize  Size of an array element in bytes (1, 2, 4, or 8)
     *  @returns  The size of the array
     */
    virtual size_t readEnumArr(void* arr, size_t arrSize, size_t valSize, size_t /*enumID*/);

}; // class JsonSerializer

_TPMCPP_END

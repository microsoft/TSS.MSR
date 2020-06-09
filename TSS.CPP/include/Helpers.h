/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once

#include <algorithm>    // required here for gcc C++11
#include <stack>

#include "fdefs.h"

#ifdef __linux__
#   include <unistd.h>
#endif

_TPMCPP_BEGIN

namespace Helpers
{
    /// <summary> Generates the given number of random bytes. </summary>
    _DLLEXP_ ByteVec RandomBytes(size_t numBytes);

    /// <summary> Produces a random int in the range [0, upperBound) </summary>
    /// <param name="upperBound"> Non-inclusive upper bound of the range of random ints </param>
    inline int32_t RandomInt(int32_t upperBound = 0x7FFFFFFF)
    {
        return (*(int32_t*)RandomBytes(4).data() & 0x7FFFFFFF) % upperBound;
    }

    inline int RandomInt(int32_t lowerBound, int32_t upperBound)
    {
        return (int32_t)(lowerBound + (*(uint32_t*)RandomBytes(4).data()) % ((uint32_t)upperBound - lowerBound));
    }

    /// <summary> Concatenate two byte buffers </summary>
    _DLLEXP_ ByteVec Concatenate(const ByteVec& buf1, const ByteVec& buf2);

    /// <summary> Concatenate an array of byte buffers </summary>
    _DLLEXP_ ByteVec Concatenate(const vector<ByteVec>& v);

    /// <summary> Returns a copy of the original byte buffer with the trailing zeroes removed </summary>
    _DLLEXP_ ByteVec TrimTrailingZeros(const ByteVec& buf);

    /// <summary> Shift an array right by numBits </summary>
    _DLLEXP_ ByteVec ShiftRight(const ByteVec& buf, size_t numBits);
} // namespace Helpers


_DLLEXP_ string EnumToStr(uint32_t enumVal, size_t enumID);
_DLLEXP_ uint32_t StrToEnum(const string& enumName, size_t enumID);

/// <summary>  Get the string representation of the given enum or bitfield value </summary>
template<class E>
string EnumToStr(E enumMemberVal)
{
    return EnumToStr(enumMemberVal, typeid(E).hash_code());
}

/// <summary>  Get the enum or bitfield value corresponding to the given enumerator name </summary>
template<class E>
uint32_t StrToEnum(const string& enumMemberName)
{
    return StrToEnum(enumMemberName, typeid(E).hash_code());
}

/// <summary> Marshals an integer of the given size to the TPM wire representation </summary>
/// <param name="val"> Integer value to marshal </param>
/// <param name="len"> Size in bytes of the value to marshal </param>
/// <param name="buf"> Buffer to place the marshaled representation to </param>
/// <param name="pos"> Position in the buffer to place the marshaled representation to </param>
inline void Int64ToTpm(uint64_t val, size_t len, ByteVec& buf, size_t& pos)
{
    if (len == 8) {
        buf[pos++] = (val >> 56) & 0xFF;
        buf[pos++] = (val >> 48) & 0xFF;
        buf[pos++] = (val >> 40) & 0xFF;
        buf[pos++] = (val >> 32) & 0xFF;
    }
    if (len >= 4) {
        buf[pos++] = (val >> 24) & 0xFF;
        buf[pos++] = (val >> 16) & 0xFF;
    }
    if (len >= 2)
        buf[pos++] = (val >> 8) & 0xFF;
    buf[pos++] = val & 0xFF;
}

/// <summary> Unmarshals an integer of the given size from the TPM wire representation </summary>
/// <param name="len"> Size in bytes of the value to unmarshal </param>
/// <param name="buf"> Buffer to read the marshaled representation from </param>
/// <param name="pos"> Position in the buffer to read the marshaled representation from </param>
/// <returns> Integer value to marshal </returns>
inline uint64_t Int64FromTpm(size_t len, ByteVec& buf, size_t& pos)
{
    uint64_t res = 0;
    if (len == 8) {
        res += ((uint64_t)buf[pos++] << 56);
        res += ((uint64_t)buf[pos++] << 48);
        res += ((uint64_t)buf[pos++] << 40);
        res += ((uint64_t)buf[pos++] << 32);
    }
    if (len >= 4) {
        res += ((uint32_t)buf[pos++] << 24);
        res += ((uint32_t)buf[pos++] << 16);
    }
    if (len >= 2)
        res += ((uint16_t)buf[pos++] << 8);
    res += (uint8_t)buf[pos++];
    return res;
}

/// <summary> Template warpper for IntToTpm() used to marshal only a single number </summary>
template<typename T>
inline ByteVec IntToTpm(T val)
{
    ByteVec buf(sizeof(T));
    size_t pos = 0;
    Int64ToTpm(val, sizeof(T), buf, pos);
    return buf;
}

/// <summary> Argument type enforcing wrappers for the template IntToTpm() </summary>
inline ByteVec Int64ToTpm(uint64_t val) { return IntToTpm(val); }

/// <summary> Argument type enforcing wrappers for the template IntToTpm() </summary>
inline ByteVec Int32ToTpm(uint32_t val) { return IntToTpm(val); }

/// <summary> Argument type enforcing wrappers for the template IntToTpm() </summary>
inline ByteVec Int16ToTpm(uint16_t val) { return IntToTpm(val); }

/// <summary>  Output a formatted byte-stream </summary>
_DLLEXP_ std::ostream& operator<<(std::ostream& s, const ByteVec& b);


_DLLEXP_ string to_hex(uint64_t val, size_t width = 0);
_DLLEXP_ uint64_t from_hex(const string& hex);


inline void Sleep(int numMillisecs)
{
#ifdef WIN32
    ::Sleep(numMillisecs);
#elif __linux__
    usleep(numMillisecs * 1000);
#endif
}


template<class B, class _Traits>
class vector_of_bases_iterator_interface_base
{
public:
    using iterator_interface = typename _Traits::iterator_interface;
    using iterator = typename _Traits::iterator;
    using pointer = typename _Traits::pointer;
    using reference = typename _Traits::reference;

public:
    virtual ~vector_of_bases_iterator_interface_base() {}

    virtual iterator_interface* clone() const = 0;

    virtual reference operator*() const = 0;
    virtual pointer operator->() const = 0;
    virtual reference operator[] (size_t idx) const = 0;

    virtual void operator++() = 0;
    virtual void operator--() = 0;

    virtual bool operator== (const iterator& rhs) const = 0;
    virtual bool operator!= (const iterator& rhs) const = 0;
};

template<class B>
class vector_of_bases_iterator;

template<class B>
class vector_of_bases_iterator_interface;

template<class B>
class vector_of_bases_iterator_traits
{
public:
    using value_type    = B;
    using pointer       = B*;
    using reference     = B&;
    using iterator      = vector_of_bases_iterator<B>;
    using iterator_interface = vector_of_bases_iterator_interface<B>;
};

template<class B>
class vector_of_bases_iterator_interface :
        public vector_of_bases_iterator_interface_base<B, vector_of_bases_iterator_traits<B>>
{};

template<class B>
class vector_of_bases_const_iterator;

template<class B>
class vector_of_bases_const_iterator_interface;

template<class B>
class vector_of_bases_const_iterator_traits
{
public:
    using value_type    = B;
    using pointer       = const B*;
    using reference     = const B&;
    using iterator      = vector_of_bases_const_iterator<B>;
    using iterator_interface = vector_of_bases_const_iterator_interface<B>;
};

template<class B>
class vector_of_bases_const_iterator_interface :
        public vector_of_bases_iterator_interface_base<B, vector_of_bases_const_iterator_traits<B>>
{};


template<class B, class _IteratorTraits>
class vector_of_bases_iterator_impl
{
    template<class _D, class _B>
    friend class vector_of_bases_for;

public:
    using traits = _IteratorTraits;
    using iterator_interface = typename traits::iterator_interface;
    using iterator = typename traits::iterator;

    // The following types can be either const or non-const
    using pointer = typename traits::pointer;
    using reference = typename traits::reference;

protected:
    shared_ptr<iterator_interface> my_pimpl;

public:
    vector_of_bases_iterator_impl(const iterator_interface& impl) : my_pimpl(impl.clone()) {}
    vector_of_bases_iterator_impl(const shared_ptr<iterator_interface>& impl) : my_pimpl(impl) {}
    virtual ~vector_of_bases_iterator_impl() = default;

    reference operator*() const { return my_pimpl->operator*(); }
    pointer operator->() const { return my_pimpl->operator->(); }
    reference operator[](size_t idx) const { return my_pimpl->operator[](idx); }

    iterator& operator++() { ++*my_pimpl; return static_cast<iterator&>(*this); }
    iterator& operator--() { --*my_pimpl; return static_cast<iterator&>(*this); }

    bool operator== (const iterator& rhs) const { return *my_pimpl == rhs; }
    bool operator!= (const iterator& rhs) const { return *my_pimpl != rhs; }
}; // class vector_of_bases_iterator_impl<>

// Wraps an interface (collection of abstract virtual methods) for iterating over vector of classes
// derived from B into std::iterator compatible interface (collection of non-virtual methods).
template<class B>
class vector_of_bases_iterator:
        public vector_of_bases_iterator_impl<B, vector_of_bases_iterator_traits<B>>
{
    using traits    = vector_of_bases_iterator_traits<B>;
    using _MyImpl   = vector_of_bases_iterator_impl<B, traits>;
    using iterator_interface  = typename traits::iterator_interface;

public:
    vector_of_bases_iterator(const iterator_interface& impl) : _MyImpl(impl) {}
    vector_of_bases_iterator(const shared_ptr<iterator_interface>& impl) : _MyImpl(impl) {}
    vector_of_bases_iterator(iterator_interface* pimpl) : _MyImpl(pimpl) {}
    virtual ~vector_of_bases_iterator() = default;
};

// Wraps an interface (collection of abstract virtual methods) for const-iterating over vector of classes
// derived from B into std::iterator compatible interface (collection of non-virtual methods).
template<class B>
class vector_of_bases_const_iterator:
        public vector_of_bases_iterator_impl<B, vector_of_bases_const_iterator_traits<B>>
{
    using traits        = vector_of_bases_const_iterator_traits<B>;
    using _MyImpl       = vector_of_bases_iterator_impl<B, traits>;
    using _IteratorI    = typename traits::iterator_interface;

public:
    vector_of_bases_const_iterator(const _IteratorI& impl) : _MyImpl(impl) {}
    vector_of_bases_const_iterator(const shared_ptr<_IteratorI>& impl) : _MyImpl(impl) {}
    virtual ~vector_of_bases_const_iterator() = default;
};

// For any class D derived from B, implements a wrapper for std::vector<D> presenting it
// as if it were std::vector<B>.
// Enables interoperability between template based and polymorphic code.
template<class B>
class vector_of_bases
{
public:
    using value_type        = B;
    using pointer           = value_type*;
    using const_pointer     = const value_type*;
    using reference         = value_type&;
    using const_reference   = const value_type&;

    using iterator          = vector_of_bases_iterator<B>;
    using const_iterator    = vector_of_bases_const_iterator<B>;

public:
    virtual ~vector_of_bases() {}

    virtual B& operator[](size_t idx) = 0;
    virtual const B& operator[](size_t idx) const = 0;

    virtual size_t size() const = 0;
    virtual void resize(size_t newSize) = 0;

    virtual iterator begin() = 0;
    virtual iterator end() = 0;
    virtual const_iterator begin() const = 0;
    virtual const_iterator end() const = 0;
};

// Note: this class keeps a plain pointer to the original (wrapped) vector. It is the caller's
// responsibility to ensure appropriate life time management.
template<class D, class B>
class vector_of_bases_for : public vector_of_bases<B>
{
protected:
    using _MyBase = vector_of_bases<B>;
    using _VectorD = vector<D>;

    using _IteratorI        = vector_of_bases_iterator_interface<B>;
    using _ConstIteratorI   = vector_of_bases_const_iterator_interface<B>;
    using _IteratorD        = typename _VectorD::iterator;
    using _ConstIteratorD   = typename _VectorD::const_iterator;

    using typename _MyBase::iterator;
    using typename _MyBase::const_iterator;

    _VectorD        &my_src;
    const _VectorD  *my_csrc;

    template<class _IteratorI, class _IteratorD, class _IteratorB>
    class _IteratorImpl : public _IteratorI
    {
    protected:
        using typename _IteratorI::iterator;
        using typename _IteratorI::pointer;
        using typename _IteratorI::reference;

        _IteratorD  my_it;

        // static_cast results in a faster code, while dynamic_cast checks for operands compatibility at run time.
        // Debug version of STL normally provide a similar check, but it's implementation dependent.
        static const _IteratorD& impl(const iterator& it) { return dynamic_cast<const _IteratorB&>(*it.my_pimpl).my_it; }

    public:
        _IteratorImpl(const _IteratorD& _it) : my_it(_it) {}
        virtual ~_IteratorImpl() = default;

        // In the following member access methods downcast to the base class ptr/ref is performed implicitly.
        virtual reference operator*() const { return *my_it; }
        virtual pointer operator->() const { return my_it.operator->(); }
        virtual reference operator[](size_t idx) const { return my_it[idx]; }

        virtual void operator++() { ++my_it; }
        virtual void operator--() { --my_it; }

        virtual bool operator== (const iterator& rhs) const { return my_it == impl(rhs); }
        virtual bool operator!= (const iterator& rhs) const { return my_it != impl(rhs); }
    }; // class _IteratorImplBase

    class _IteratorB : public _IteratorImpl<_IteratorI, _IteratorD, _IteratorB>
    {
        using _MyImpl = _IteratorImpl<_IteratorI, _IteratorD, _IteratorB>;
        friend _MyImpl;

    public:
        _IteratorB(const _IteratorD& _it) : _MyImpl(_it) {}
        virtual ~_IteratorB() = default;

        virtual _IteratorI* clone() const { return new _IteratorB(_MyImpl::my_it); }
    };

    class _ConstIteratorB : public _IteratorImpl<_ConstIteratorI, _ConstIteratorD, _ConstIteratorB>
    {
        using _MyImpl = _IteratorImpl<_ConstIteratorI, _ConstIteratorD, _ConstIteratorB>;
        friend _MyImpl;

    public:
        _ConstIteratorB(const _ConstIteratorD& _it) : _MyImpl(_it) {}
        virtual ~_ConstIteratorB() = default;

        virtual _ConstIteratorI* clone() const { return new _ConstIteratorB(_MyImpl::my_it); }
    };

public:
    vector_of_bases_for(vector<D>& src) : my_src(src), my_csrc(&src) {}
    vector_of_bases_for(const vector<D>& src) : my_src(*(vector<D>*)nullptr), my_csrc(&src) {}
    virtual ~vector_of_bases_for() = default;

    B& operator[](size_t idx) { return my_src[idx]; }
    const B& operator[](size_t idx) const { return (*my_csrc)[idx]; }

    size_t size() const { return my_csrc->size(); }
    void resize(size_t newSize) { my_src.resize(newSize); }

    virtual iterator begin()  { return iterator(_IteratorB(my_src.begin())); }
    virtual iterator end() { return iterator(_IteratorB(my_src.end())); }

    virtual const_iterator begin() const { return const_iterator(_ConstIteratorB(my_csrc->begin())); }
    virtual const_iterator end() const { return const_iterator(_ConstIteratorB(my_csrc->end())); }
};

template<class B, class D>
vector_of_bases_for<D, B> to_base(vector<D>& v) { return vector_of_bases_for<D, B>(v); }

template<class B, class D>
const vector_of_bases_for<D, B> to_base(const vector<D>& v) { return vector_of_bases_for<D, B>(v); }


_TPMCPP_END

/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once

#include <algorithm>    // required here for gcc C++ 11
#include <iostream>
#include <iomanip>
#include <sstream>
#include <stack>

#include "fdefs.h"

#ifdef __linux__
#   include <unistd.h>
#endif

_TPMCPP_BEGIN

template<typename U> struct TpmEnum;

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

        OutByteBuf& operator<<(const class TpmStructure& x);

        OutByteBuf& operator<<(const ByteVec& xx) {
            buf.insert(buf.end(), xx.begin(), xx.end());
            return *this;
        }

        template<typename U>
        OutByteBuf& operator<<(const TpmEnum<U>& e) {
            return *this << (U)e;
        }

        void AddSlice(const ByteVec& xx, size_t start, size_t len)
        {
            buf.insert(buf.end(), xx.begin() + start, xx.begin() + start + len);
        }

        int GetPos() {
            return (int)buf.size();
        }

        ByteVec& GetBuf() {
            return buf;
        }

        BYTE *GetBufPtr(int pos) {
            return &buf[pos];
        }

    protected:
        ByteVec buf;
};

///<summary>Provides for unmarshalling TPM types from a byte-buffer</summary>
class InByteBuf {
    public:
        InByteBuf(const ByteVec& _buf) {
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
        InByteBuf& operator>>(TpmStructure& s);
        ByteVec GetEndianConvertedVec(UINT32 numBytes);

        ByteVec TheRest() {
            ByteVec theRest(buf.size() - pos);

            for (UINT32 j = pos; j < buf.size(); j++) {
                theRest[j - pos] = buf[j];
            }

            pos = (int)buf.size();
            return theRest;
        }

        ByteVec GetSlice(UINT32 numBytes) {
            ByteVec temp(numBytes);

            for (UINT32 j = 0; j < numBytes; j++) {
                temp[j] = buf[j + pos];
            }

            pos += numBytes;
            return temp;
        }

        UINT32 GetValueType(int numBytes) {
            switch (numBytes) {
                case 1:
                    BYTE x1;
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
            return (UINT32)-1;
        }

        bool eof() {
            return pos == (int)buf.size();
        }

        int GetPos() const { return pos; }

        std::stack<int> sizedStructLen;

    protected:
        ByteVec buf;
        int pos;
};

class Helpers {
    public:
        static ByteVec Concatenate(const ByteVec& t1, const ByteVec& t2)
        {
            ByteVec x(t1.size() + t2.size());
            copy(t1.begin(), t1.end(), x.begin());
            copy(t2.begin(), t2.end(), x.begin() + t1.size());
            return x;
        }

        static ByteVec Concatenate(const vector<ByteVec>& v)
        {
            ByteVec res;
            for (auto i = v.begin(); i != v.end(); i++) {
                res.resize(res.size() + i->size());
                copy(i->begin(), i->end(), res.end() - i->size());
            }
            return res;
        }

        ///<summary>Returns a new buffer that is UINT16-len prepended</summary>
        static ByteVec ToTpm2B(const ByteVec& buf)
        {
            OutByteBuf b;
            b << (UINT16)buf.size() << buf;
            return b.GetBuf();
        }

        ///<summary>Shift an array right by numBits</summary>
        static ByteVec ShiftRight(const ByteVec& buf, size_t numBits);

        static ByteVec TrimTrailingZeros(const ByteVec& buf)
        {
            if (buf.empty() || buf.back() != 0)
                return buf;

            size_t size = buf.size();
            while (size > 0 && buf[size-1] == 0)
                --size;
            return ByteVec(buf.begin(), buf.begin() + size);
        }
}; // class Helpers

// Cannot use in an elaborated specifier, so a forward decl is needed.
enum class TpmTypeId;

///<summary>Returns string representation of a TPM enum or bitfield value</summary>
_DLLEXP_ string GetEnumString(UINT32 val, const TpmTypeId& tid);

///<summary>Get the string representation of an enum or bitfield value.</summary>
template<class E>
static string GetEnumString(const E& enumVal) {
    return GetEnumString((UINT32)enumVal, enumVal.GetTypeId());
}


_DLLEXP_ string EnumToStr(size_t enumHash, uint32_t enumVal);
_DLLEXP_ uint32_t StrToEnum(size_t enumHash, const string& enumName);

///<summary>Get the string representation of the given enum or bitfield value.</summary>
template<class E>
string EnumToStr(uint32_t enumMemberVal) {
    return EnumToStr(typeid(E).hash_code(), enumMemberVal);
}

///<summary>Get the enum or bitfield value corresponding to the given enumerator name.</summary>
template<class E>
uint32_t StrToEnum(const string& enumMemberName) {
    return StrToEnum(typeid(E).hash_code(), enumMemberName);
}


///<summary> Output a formatted byte-stream</summary>
_DLLEXP_ std::ostream& operator<<(std::ostream& s, const ByteVec& b);

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

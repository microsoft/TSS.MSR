/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#pragma once
#include "Helpers.h"

_TPMCPP_BEGIN

///<summary>Serialization format enumeration.</summary>
enum class SerializationType {
    None,
    Text,
    Xml,
    JSON
};

enum class TpmTypeId;

///<summary>Base class for all TPM structures.</summary>
class _DLLEXP_ TpmStructureBase {
    public:
        ///<summary>Base class for all TPM structures.</summary>
        TpmStructureBase();
        virtual ~TpmStructureBase() {};

        ///<summary>Returns the TPM binary-form representation of this structure.</summary>
        std::vector<BYTE> ToBuf() const;

        ///<summary>Sets this structure based on the TPM representation in buf.</summary>
        void FromBuf(const std::vector<BYTE>& buf);

        ///<summary>Creates a new instance of the TPM structure specified in tp.</summary>
        static TpmStructureBase *FromBuf(const std::vector<BYTE>& bufToBeRead, TpmTypeId tp);

        ///<summary>Returns the string representation of the structure. If !precise then
        /// arrays are truncated for readability (useful for interactive debugging).</summary>
        std::string ToString(bool precise = true);

        ///<summary>Serialize the object to text, JSON, XML-etc.</summary>
        std::string Serialize(SerializationType serializationFormat);

        ///<summary>Deserialize from JSON (other formats TBD)</summary>
        bool Deserialize(SerializationType serializationFormat, std::string inBuf);

        ///<summary>Test for value equality</summary>
        bool operator==(const TpmStructureBase& rhs) const;

        ///<summary>Test for value inequality</summary>
        bool operator!=(TpmStructureBase& rhs) const;

        ///<summary>Get a type-identifier for this structure</summary>
        virtual TpmTypeId GetTypeId() const;

        // TODO: Remove.
        //virtual bool operator==(const TpmStructureBase& r){ _ASSERT(FALSE); return false; }
        //virtual bool operator!=(const TpmStructureBase& r){ _ASSERT(FALSE); return false; }

        // Needed for STL/DLL
        virtual bool operator<(const TpmStructureBase& r) {
            return true;
        }

        // TODO: Remove.
        // template<TpmStructureBase T>
        // auto T(T& _x) -> typename T{ return T(x); }

    protected:
        ///<summary>Add this struct to the outBuf.</summary>
        void MarshallInternal(class OutByteBuf& outBuf) const;
        bool NonDefaultMarshall(OutByteBuf& outBuf) const;

        ///<summary>Hydrate *this from the current position in the buf.</summary>
        void FromBufInternal(class InByteBuf& buf);
        bool FromBufSpecial(class InByteBuf& buf, TpmTypeId tp);

        ///<summary>Get the expected length of the array in fieldNum/field.  Array-lengths
        // are usually explicit, but in some cases (e.g. hashes) the array-len must be
        /// determined from the TPM_ALG_ID</summary>
        UINT32 GetArrayLen(class StructMarshallInfo& fields, int fieldNum, class MarshallInfo& field);

        ///<summary>Make a new instance of the specified struct or union type using the default
        /// constructor. If pointerToUnion is not TpmTypeId.Null then also return the pointer
        /// dynacast to the provided union type.</summary>
        static TpmStructureBase *Factory(TpmTypeId id, TpmTypeId dynacastType, void *&pointerToUnion);

        ///<summary>This will be overriden in derived classes that are optional in TPM inputs.
        /// If the marshaller sees a null element it will not be marshalled.  Such elements are
        /// length-preceded, and the length will be set to zero.</summary>
        virtual bool NullElement() const {
            return false;
        }

        ///<summary>ElementInfo provides low-level access to structure members. If arrayIdex==-1,
        /// the address of the element at elementNum is returned.  If the element is an array
        /// then (a) the current arraySize is also returned, and (b), newArraySize!=-1 the 
        /// array size is set to the new array size. If arrayIndex!=-1, then the memory address
        /// of the element is returned (as long as it not an array). If the object is a struct
        /// then a pointer to the associated TpmStructureBase is also returned</summary>
        virtual void *ElementInfo(int elementNum,
                                  int arrayIndex,
                                  int& arraySize,
                                  TpmStructureBase *&pStruct,
                                  int newArraySize) {
            _ASSERT(FALSE);
            return NULL;
        }

        friend class Tpm2;
        friend class OutStructSerializer;
        friend class InStructSerializer;
        friend class CryptoServices;
        friend class InByteBuf;
        friend class OutByteBuf;
};

_TPMCPP_END

#include "Marshall.h"
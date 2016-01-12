/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"
#include "MarshallInternal.h"
#include "Tpm2.h"

_TPMCPP_BEGIN

TpmTypeMap TheTypeMap;

/// All TPM structures and unions have metadata that describes how they should be marshalled.
StructMarshallInfo *TpmTypeMap::GetStructMarshallInfo(TpmTypeId id)
{
    return TypeMap[id];
}

StructMarshallInfo *TpmTypeMap::GetStructMarshallInfo(const string& inf)
{
    return this->TypeInfoNameMap[inf];
}

// Look up union selector given union type
UINT32 TpmTypeMap::GetUnionSelectorFromTypId(TpmTypeId unionId, TpmTypeId selectorId)
{
    StructMarshallInfo *s = GetStructMarshallInfo(unionId);

    for (unsigned int j = 0; j < s->UnionSelector.size(); j++) {
        if (s->UnionType[j] == selectorId) {
            return s->UnionSelector[j];
        }
    }

    return (UINT32) - 1;
}

TpmTypeId TpmTypeMap::GetStructTypeIdFromUnionSelector(TpmTypeId unionId, UINT32 selector)
{
    StructMarshallInfo *s = GetStructMarshallInfo(unionId);

    // TODO: Better data structure
    for (unsigned int j = 0; j < s->UnionSelector.size(); j++) {
        if (s->UnionSelector[j] == selector) {
            return s->UnionType[j];
        }
    }

    return TpmTypeId::None;
}

void TpmTypeMap::Init()
{
    for (auto i : TypeMap) {
        TypeInfoNameMap[i.second->MyTypeInfo] = i.second;
    }

    return;
}

TpmStructureBase::TpmStructureBase()
{
}

TpmTypeId TpmStructureBase::GetTypeId() const
{
    _ASSERT(FALSE);
    return (TpmTypeId)0;
}

///<summary>Serialize the structure to a TPM-formatted byte-array.</summary>
std::vector<BYTE> TpmStructureBase::ToBuf() const
{
    OutByteBuf outBuf;
    MarshallInternal(outBuf);
    return outBuf.GetBuf();
}

// Create a new TPM structure of the type indicated and set the contents based on
// the TPM-formatted byte-stream.
TpmStructureBase *TpmStructureBase::FromBuf(const std::vector<BYTE>& _buf, TpmTypeId tp)
{
    void *pUnion;
    TpmStructureBase *newObj = TpmStructureBase::Factory(tp, TpmTypeId::None, pUnion);

    InByteBuf buf(_buf);
    newObj->FromBufInternal(buf);
    _ASSERT(buf.eof());
    return newObj;
}

// Populate the elements of this object with the TPM-formatted byte-stream.
void TpmStructureBase::FromBuf(const std::vector<BYTE>& _buf)
{
    InByteBuf buf(_buf);
    FromBufInternal(buf);
    _ASSERT(buf.eof());
    return;
}

///<summary>Convert to a string-representation. Optionally (if !precise) truncate long
///byte arrays to improve human-readability during debugging.</summary>
std::string TpmStructureBase::ToString(bool precise)
{
    // Set the selectors and lengths because the text marshallers don't know how to do that.
    ToBuf();

    OutStructSerializer srx(SerializationType::Text, precise);
    string serialized = srx.Serialize(this);

    // TODO: Remove.
    //
    // The following code is for debugging the serializer
    // 
    // OutStructSerializer ser(SerializationType::Text, false);
    // ser.Serialize(this);
    // string serString = ser.ToString();
    // cout << "ORININAL ==============" << endl << s.str();
    // cout << "NEW +++++++++++++" << endl << serString << endl;
    // 
    // OutStructSerializer ser2(SerializationType::JSON, false);
    // ser2.Serialize(this);
    // auto ser2String = ser2.ToString();
    // cout << "JSON +++++++++++++" << endl << ser2String << endl;
    // 
    // 
    // InStructSerializer ser3(SerializationType::JSON, ser2String);
    // 
    // StructMarshallInfo* info = TheTypeMap.GetStructMarshallInfo(this->GetTypeId());
    // ObjectFactory fact = info->Factory;
    // void* pUnion;
    // TpmStructureBase* newObj = TpmStructureBase::Factory(this->GetTypeId(), TpmTypeIds::None, pUnion);
    // bool ok = ser3.DeSerialize(newObj);
    //
    // _ASSERT((ok && (*newObj == *this));

    return serialized;
}

bool TpmStructureBase::operator==(const TpmStructureBase& rhs) const
{
    if (const_cast<TpmStructureBase *>(this)->GetTypeId() != const_cast<TpmStructureBase&>
        (rhs).GetTypeId()) {
        return false;
    }

    std::vector<BYTE> x1 = const_cast<TpmStructureBase *>(this)->ToBuf();
    std::vector<BYTE> x2 = const_cast<TpmStructureBase&>(rhs).ToBuf();
    return (x1 == x2);
}

bool TpmStructureBase::operator!=(TpmStructureBase& rhs) const
{
    bool equal = *this == rhs;
    return !equal;
}

string TpmStructureBase::Serialize(SerializationType serializationFormat)
{
    // The text-serializers can only serialize if the array-len and selector-vals
    // have been set. This is done as a side effect of the TPM-binary serializer.
    this->ToBuf();
    OutStructSerializer ss(serializationFormat);
    ss.Serialize(this);
    return ss.ToString();
}

///<summary>Deserialize from JSON (other formats TBD)</summary>
bool TpmStructureBase::Deserialize(SerializationType serializationFormat, string inBuf)
{
    InStructSerializer ss(serializationFormat, inBuf);
    bool ok = ss.DeSerialize(this);
    return ok;
}

_TPMCPP_END
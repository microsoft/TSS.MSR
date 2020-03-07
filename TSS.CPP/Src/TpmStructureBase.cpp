/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "MarshalInternal.h"

_TPMCPP_BEGIN

TpmTypeId TpmStructureBase::GetTypeId() const
{
    _ASSERT(FALSE);
    return TpmTypeId::None;
}

///<summary>Serialize the structure to a TPM-formatted byte-array.</summary>
ByteVec TpmStructureBase::ToBuf() const
{
    OutByteBuf outBuf;
    ToBufInternal(outBuf);
    return outBuf.GetBuf();
}

// Populate the elements of this object with the TPM-formatted byte-stream.
void TpmStructureBase::FromBuf(const ByteVec& _buf)
{
    InByteBuf buf(_buf);
    FromBufInternal(buf);
    _ASSERT(buf.eof());
}

///<summary>Convert to a string-representation. Optionally (if !precise) truncate long
///byte arrays to improve human-readability during debugging.</summary>
string TpmStructureBase::ToString(bool precise)
{
    // Set the selectors and lengths because the text marshallers don't know how to do that.
    ToBuf();

    OutStructSerializer srx(SerializationType::Text, precise);
    return srx.Serialize(this);
}

bool TpmStructureBase::operator==(const TpmStructureBase& rhs) const
{
    if (const_cast<TpmStructureBase*>(this)->GetTypeId() != const_cast<TpmStructureBase&>
        (rhs).GetTypeId()) {
        return false;
    }

    ByteVec x1 = const_cast<TpmStructureBase*>(this)->ToBuf();
    ByteVec x2 = const_cast<TpmStructureBase&>(rhs).ToBuf();
    return x1 == x2;
}

bool TpmStructureBase::operator!=(TpmStructureBase& rhs) const
{
    return !(*this == rhs);
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
///<returns>true in case of success</returns>
bool TpmStructureBase::Deserialize(SerializationType serializationFormat, string inBuf)
{
    InStructSerializer ss(serializationFormat, inBuf);
    return ss.DeSerialize(this);
}

_TPMCPP_END

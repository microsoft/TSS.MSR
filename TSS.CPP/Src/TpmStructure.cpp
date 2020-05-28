/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "MarshalInternal.h"

_TPMCPP_BEGIN

///<summary>Serialize the structure to a TPM-formatted byte-array.</summary>
ByteVec TpmStructure::ToBuf() const
{
#if NEW_MARSHAL
    TpmBuffer   buf;
    toTpm(buf);
    return buf.trim();
#else
    OutByteBuf outBuf;
    ToBufInternal(outBuf);
    return outBuf.GetBuf();
#endif
}

// Populate the elements of this object with the TPM-formatted byte-stream.
void TpmStructure::FromBuf(const ByteVec& _buf)
{
#if NEW_MARSHAL
    TpmBuffer   buf(_buf);
    fromTpm(buf);
    _ASSERT(buf.curPos() == buf.length());
#else
    InByteBuf buf(_buf);
    FromBufInternal(buf);
    _ASSERT(buf.eof());
#endif
}

///<summary>Convert to a string-representation. Optionally (if !precise) truncate long
///byte arrays to improve human-readability during debugging.</summary>
string TpmStructure::ToString(bool precise)
{
    // Set the selectors and lengths because the text marshallers don't know how to do that.
    ToBuf();
#if NEW_MARSHAL
    PlainTextSerializer buf(precise);
#else
    OutStructSerializer buf(SerializationType::Text, precise);
#endif
    return buf.Serialize(this);
}

bool TpmStructure::operator==(const TpmStructure& rhs) const
{
    if (const_cast<TpmStructure*>(this)->GetTypeId() != const_cast<TpmStructure&>
        (rhs).GetTypeId()) {
        return false;
    }

    ByteVec x1 = const_cast<TpmStructure*>(this)->ToBuf();
    ByteVec x2 = const_cast<TpmStructure&>(rhs).ToBuf();
    return x1 == x2;
}

bool TpmStructure::operator!=(TpmStructure& rhs) const
{
    return !(*this == rhs);
}

string TpmStructure::Serialize(SerializationType format)
{
#if NEW_MARSHAL
    ISerializer& buf = format == SerializationType::JSON ? (ISerializer&)JsonSerializer()
                                                         : (ISerializer&)PlainTextSerializer();
#else
    // The text-serializers can only serialize if the array-len and selector-vals
    // have been set. This is done as a side effect of the TPM-binary serializer.
    this->ToBuf();
    OutStructSerializer buf(format);
#endif
    buf.Serialize(this);
    return buf.ToString();
}

///<summary>Deserialize from JSON (other formats TBD)</summary>
///<returns>true in case of success</returns>
bool TpmStructure::Deserialize(SerializationType format, string inBuf)
{
#if NEW_MARSHAL
    ISerializer& buf = format == SerializationType::JSON ? (ISerializer&)JsonSerializer(inBuf)
                                                         : (ISerializer&)PlainTextSerializer(inBuf);
#else
    InStructSerializer buf(format, inBuf);
#endif
    return buf.Deserialize(this);
}


const vector<TPM_HANDLE> ReqStructure::getHandles() const
{
    return {};
}

TPM_HANDLE RespStructure::getHandle() const
{
    return {};
}

_TPMCPP_END

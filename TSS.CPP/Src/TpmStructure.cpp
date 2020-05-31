/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "MarshalInternal.h"

_TPMCPP_BEGIN

/// <summary> Serialize the structure to a TPM-formatted byte-array. </summary>
ByteVec TpmStructure::ToBuf() const
{
    TpmBuffer buf;
    toTpm(buf);
    return buf.trim();
}

// Populate the elements of this object with the TPM-formatted byte-stream.
void TpmStructure::FromBuf(const ByteVec& tpmBytes)
{
    TpmBuffer buf(tpmBytes);
    fromTpm(buf);
    _ASSERT(buf.curPos() == buf.length());
}

/// <summary> Convert to a string-representation. Optionally (if !precise) truncate long
/// byte arrays to improve human-readability during debugging. </summary>
string TpmStructure::ToString(bool precise)
{
    // Set the selectors and lengths because the text marshallers don't know how to do that.
    ToBuf();
    PlainTextSerializer buf(precise);
    return buf.Serialize(this);
}

bool TpmStructure::operator==(const TpmStructure& rhs) const
{
    if (this == &rhs)
        return true;

    ByteVec x1 = this->ToBuf();
    ByteVec x2 = rhs.ToBuf();
    return x1 == x2;
}

bool TpmStructure::operator!=(TpmStructure& rhs) const
{
    return !(*this == rhs);
}

string TpmStructure::Serialize(SerializationType format)
{
    if (format == SerializationType::JSON)
    {
        JsonSerializer buf;
        return buf.Serialize(this);
    }
    else
    {
        PlainTextSerializer buf;
        return buf.Serialize(this);
    }
}

bool TpmStructure::Deserialize(SerializationType format, string inBuf)
{
    if (format == SerializationType::JSON)
    {
        JsonSerializer buf (inBuf);
        return buf.Deserialize(this);
    }
    else
    {
        PlainTextSerializer buf (inBuf);
        return buf.Deserialize(this);
    }
}


vector<TPM_HANDLE> ReqStructure::getHandles() const
{
    return {};
}

TPM_HANDLE RespStructure::getHandle() const
{
    return {};
}

_TPMCPP_END

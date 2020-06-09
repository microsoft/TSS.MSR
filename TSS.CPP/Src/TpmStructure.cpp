/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"

_TPMCPP_BEGIN

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

/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once

#include "Tpm2.h"

#ifdef __linux__
#   include <unistd.h>
#endif

_TPMCPP_BEGIN

namespace Helpers
{
    /// <summary> Returns a bytebuffer marshaled in the TPM2B format </summary>
    inline ByteVec ToTpm2B(const ByteVec& data)
    {
        TpmBuffer buf(data.size() + 2);
        buf.writeShort((uint16_t)data.size());
        buf.writeByteBuf(data);
        return buf.buffer();
    }

    _DLLEXP_ ByteVec HashPcrs(TPM_ALG_ID hashAlg, const vector<TPM2B_DIGEST>& PcrValues);

} // namespace Helpers

TPM_ALG_ID GetSigningHashAlg(const TPMT_PUBLIC& pub);

_TPMCPP_END

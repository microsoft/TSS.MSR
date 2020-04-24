/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "TpmMarshalNew.h"

_TPMCPP_BEGIN


void nonStandardToTpm(const _TPMT_SYM_DEF& sd, TpmBuffer& buf)
{
    buf.writeInt(sd.algorithm, 2);
    if (sd.algorithm != TPM_ALG_ID::_NULL) {
        buf.writeInt(sd.keyBits, 2);
        buf.writeInt(sd.mode, 2);
    }
}

void nonStandardToTpm(const _TPMT_SYM_DEF_OBJECT& sdo, TpmBuffer& buf)
{
    buf.writeInt(sdo.algorithm, 2);
    if (sdo.algorithm != TPM_ALG_ID::_NULL) {
        buf.writeInt(sdo.keyBits, 2);
        buf.writeInt(sdo.mode, 2);
    }
}

void nonStandardFromTpm(_TPMT_SYM_DEF& sd, TpmBuffer& buf)
{
    sd.algorithm = buf.readInt(2);
    if (sd.algorithm != TPM_ALG_ID::_NULL) {
        sd.keyBits = buf.readInt(2);
        sd.mode = buf.readInt(2);
    }
}

void nonStandardFromTpm(_TPMT_SYM_DEF_OBJECT& sdo, TpmBuffer& buf)
{
    sdo.algorithm = buf.readInt(2);
    if (sdo.algorithm != TPM_ALG_ID::_NULL) {
        sdo.keyBits = buf.readInt(2);
        sdo.mode = buf.readInt(2);
    }
}


_TPMCPP_END
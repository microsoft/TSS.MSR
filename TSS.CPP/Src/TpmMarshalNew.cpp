/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "TpmMarshalNew.h"

_TPMCPP_BEGIN


void nonStandardToTpm(const _TPMT_SYM_DEF& sd, TpmBuffer& buf)
{
    buf.intToTpm(sd.algorithm, 2);
    if (sd.algorithm != TPM_ALG_ID::_NULL) {
        buf.intToTpm(sd.keyBits, 2);
        buf.intToTpm(sd.mode, 2);
    }
}

void nonStandardToTpm(const _TPMT_SYM_DEF_OBJECT& sdo, TpmBuffer& buf)
{
    buf.intToTpm(sdo.algorithm, 2);
    if (sdo.algorithm != TPM_ALG_ID::_NULL) {
        buf.intToTpm(sdo.keyBits, 2);
        buf.intToTpm(sdo.mode, 2);
    }
}

void nonStandardFromTpm(_TPMT_SYM_DEF& sd, TpmBuffer& buf)
{
    sd.algorithm = buf.intFromTpm(2);
    if (sd.algorithm != TPM_ALG_ID::_NULL) {
        sd.keyBits = buf.intFromTpm(2);
        sd.mode = buf.intFromTpm(2);
    }
}

void nonStandardFromTpm(_TPMT_SYM_DEF_OBJECT& sdo, TpmBuffer& buf)
{
    sdo.algorithm = buf.intFromTpm(2);
    if (sdo.algorithm != TPM_ALG_ID::_NULL) {
        sdo.keyBits = buf.intFromTpm(2);
        sdo.mode = buf.intFromTpm(2);
    }
}


_TPMCPP_END
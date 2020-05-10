/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "TpmMarshalNew.h"

_TPMCPP_BEGIN


void nonStandardToTpm(const _TPMT_SYM_DEF& sd, TpmBuffer& buf)
{
    buf.writeShort(sd.algorithm);
    if (sd.algorithm != TPM_ALG_NULL) {
        buf.writeShort(sd.keyBits);
        buf.writeShort(sd.mode);
    }
}

void nonStandardToTpm(const _TPMT_SYM_DEF_OBJECT& sdo, TpmBuffer& buf)
{
    buf.writeShort(sdo.algorithm);
    if (sdo.algorithm != TPM_ALG_NULL) {
        buf.writeShort(sdo.keyBits);
        buf.writeShort(sdo.mode);
    }
}

void nonStandardFromTpm(_TPMT_SYM_DEF& sd, TpmBuffer& buf)
{
    sd.algorithm = buf.readShort();
    if (sd.algorithm != TPM_ALG_NULL) {
        sd.keyBits = buf.readShort();
        sd.mode = buf.readShort();
    }
}

void nonStandardFromTpm(_TPMT_SYM_DEF_OBJECT& sdo, TpmBuffer& buf)
{
    sdo.algorithm = buf.readShort();
    if (sdo.algorithm != TPM_ALG_NULL) {
        sdo.keyBits = buf.readShort();
        sdo.mode = buf.readShort();
    }
}

_TPMCPP_END

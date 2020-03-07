/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "Tpm2.h"

_TPMCPP_BEGIN

// TypeMap holds type info for all kinds of TPM entity
// (structs, unions, typedefs, enums and bitfields)
std::map<TpmTypeId, TpmTypeInfo*>    TypeMap;

_TPMCPP_END

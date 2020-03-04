/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "Tpm2.h"

_TPMCPP_BEGIN

// TypeMap does not include types of ValueType kind
// (i.e. TPM typedefs, enums and bitfields)
std::map<TpmTypeId, TpmTypeInfo*>    TypeMap;

// TypeNameMap contains all TPM types
// std::map<TpmTypeId, string>          TypeNameMap;

// TypeSizeMap includes only types of ValueType kind
// (i.e. TPM typedefs, enums and bitfields)
// std::map<TpmTypeId, int>             TypeSizeMap;

_TPMCPP_END
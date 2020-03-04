/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */
#pragma once

#include <vector>

#include "Tpm2.h"

using namespace TpmCpp;

class TpmConfig
{
public:
    void Init(Tpm2& tpm);

    // All implemented algorithms
    std::vector<TPM_ALG_ID> ImplementedAlgs;

    // Implemented hash algorithms
    std::vector<TPM_ALG_ID> HashAlgs;

    // All commands implemented by the TPM
    std::vector<TPM_CC> ImplementedCommands;
};


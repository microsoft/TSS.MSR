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
    // All implemented algorithms
    static std::vector<TPM_ALG_ID> ImplementedAlgs;

    // Implemented hash algorithms
    static std::vector<TPM_ALG_ID> HashAlgs;

    // All commands implemented by the TPM
    static std::vector<TPM_CC> ImplementedCommands;

    static void Init(Tpm2& tpm);

    static bool Implements(TPM_CC cmd);
    static bool Implements(TPM_ALG_ID alg);
};


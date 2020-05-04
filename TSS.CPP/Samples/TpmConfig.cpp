#include "stdafx.h"
#include "TpmConfig.h"
#include <algorithm>

std::vector<TPM_ALG_ID> TpmConfig::ImplementedAlgs;

// Implemented hash algorithms
std::vector<TPM_ALG_ID> TpmConfig::HashAlgs;

// All commands implemented by the TPM
std::vector<TPM_CC> TpmConfig::ImplementedCommands;


void TpmConfig::Init(Tpm2& tpm)
{
    if (ImplementedCommands.size() > 0)
    {
        _ASSERT(ImplementedAlgs.size() > 0 && HashAlgs.size() > 0);
        return;
    }

    UINT32 startProp = TPM_ALG_ID::FIRST;
    GetCapabilityResponse resp;
    do {
        resp = tpm.GetCapability(TPM_CAP::ALGS, startProp, TPM_ALG_ID::LAST - startProp + 1);

        auto capData = dynamic_cast<TPML_ALG_PROPERTY*>(&*resp.capabilityData);
        auto algProps = capData->algProperties;

        for (const TPMS_ALG_PROPERTY& p: algProps)
        {
            ImplementedAlgs.push_back(p.alg);
            // Note: "equal" vs. "has" is important in the following 'hash' attr check
            if (p.algProperties == TPMA_ALGORITHM::hash && TPM_HASH::DigestSize(p.alg) > 0)
            {
                HashAlgs.push_back(p.alg);
            }
        }
        startProp = (UINT32)algProps.back().alg + 1;
    } while (resp.moreData);

    startProp = TPM_CC::FIRST;
    do {
        const UINT32 MaxVendorCmds = 32;
        resp = tpm.GetCapability(TPM_CAP::COMMANDS, startProp,
                                 TPM_CC::LAST - startProp + MaxVendorCmds + 1);
        auto capData = dynamic_cast<TPML_CCA*>(&*resp.capabilityData);
        auto cmdAttrs = capData->commandAttributes;

        for (auto iter = cmdAttrs.begin(); iter != cmdAttrs.end(); iter++)
        {
            TPM_CC cc = *iter & 0xFFFF;
            //TPMA_CC maskedAttr = *iter & 0xFFFF0000;

            ImplementedCommands.push_back(cc);
        }
        startProp = (cmdAttrs.back() & 0xFFFF) + 1;
    } while (resp.moreData);
} // TpmConfig::Init()

bool TpmConfig::Implements(TPM_CC cmd)
{
    return std::find(ImplementedCommands.begin(), ImplementedCommands.end(), cmd)
            != ImplementedCommands.end();
}

bool TpmConfig::Implements(TPM_ALG_ID alg)
{
    return std::find(ImplementedAlgs.begin(), ImplementedAlgs.end(), alg) != ImplementedAlgs.end();
}

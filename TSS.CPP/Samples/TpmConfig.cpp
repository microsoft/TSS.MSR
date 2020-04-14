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

    UINT32 startProp = (UINT32)TPM_ALG_ID::FIRST;

    GetCapabilityResponse resp;
    do {
        resp = tpm.GetCapability(TPM_CAP::ALGS, startProp,
                                 (UINT32)TPM_ALG_ID::LAST - startProp + 1);

        auto capData = dynamic_cast<TPML_ALG_PROPERTY*>(&*resp.capabilityData);
        auto algProps = capData->algProperties;

        for (const TPMS_ALG_PROPERTY& p: algProps)
        {
            ImplementedAlgs.push_back(p.alg);
            if (p.algProperties & TPMA_ALGORITHM::hash)
                HashAlgs.push_back(p.alg);
        }
        startProp = (UINT32)algProps.back().alg + 1;
    } while (resp.moreData);

    startProp = (UINT32)TPM_CC::FIRST;
    do {
        const UINT32 MaxVendorCmds = 32;
        resp = tpm.GetCapability(TPM_CAP::COMMANDS, startProp,
                                 (UINT32)TPM_CC::LAST - startProp + MaxVendorCmds + 1);
        auto capData = dynamic_cast<TPML_CCA*>(&*resp.capabilityData);
        auto cmdAttrs = capData->commandAttributes;

        for (auto iter = cmdAttrs.begin(); iter != cmdAttrs.end(); iter++)
        {
            UINT32 attrVal = (UINT32)*iter;
            TPM_CC cc = (TPM_CC)(attrVal & 0xFFFF);
            //TPMA_CC maskedAttr = (TPMA_CC)(attrVal & 0xFFFF0000);

            ImplementedCommands.push_back(cc);
        }
        startProp = ((UINT32)cmdAttrs.back() & 0xFFFF) + 1;
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

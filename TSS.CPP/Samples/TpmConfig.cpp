#include "stdafx.h"
#include "TpmConfig.h"



void TpmConfig::Init(Tpm2& tpm)
{
    if (ImplementedCommands.size() > 0)
        return;

    UINT32 startProp = (UINT32)TPM_ALG_ID::FIRST;

    // For the first example we show how to get a batch (8) properties at a time.
    // For simplicity, subsequent samples just get one at a time: avoiding the
    // nested loop.
    do {
        auto getCapResp = tpm.GetCapability(TPM_CAP::ALGS, startProp,
                                            (UINT32)TPM_ALG_ID::LAST - startProp + 1);
        auto props = dynamic_cast<TPML_ALG_PROPERTY*>(&*getCapResp.capabilityData);

        for (const TPMS_ALG_PROPERTY& p: props->algProperties) {
            ImplementedAlgs.push_back(p.alg);
            if (p.algProperties & TPMA_ALGORITHM::hash)
                HashAlgs.push_back(p.alg);
        }

        if (!getCapResp.moreData)
            break;

        startProp = ((UINT32)props->algProperties[props->algProperties.size() - 1].alg) + 1;
    } while (true);
#if 0
    startIdx = 0;

    do {
        GetCapabilityResponse caps = tpm.GetCapability(TPM_CAP::COMMANDS, startIdx, 32);
        auto comms = dynamic_cast<TPML_CCA*>(caps.capabilityData);

        for (auto iter = comms->commandAttributes.begin(); iter != comms->commandAttributes.end(); iter++) {
            TPMA_CC attr = (TPMA_CC)*iter;
            UINT32 attrVal = (UINT32)attr;

            // Decode the packed structure -
            TPM_CC cc = (TPM_CC)(attrVal & 0xFFFF);
            TPMA_CC maskedAttr = (TPMA_CC)(attrVal & 0xFFff0000);

            cout << "Command:" << GetEnumString(cc) << ": ";
            cout << GetEnumString(maskedAttr) << endl;

            commandsImplemented.push_back(cc);

            startIdx = (UINT32)cc;
        }

        cout << endl;

        if (!caps.moreData) {
            break;
        }

        startIdx++;
    } while (true);
#endif
}


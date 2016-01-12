/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/

/*
The definitions in this file are #included in TpmTypes.h

These are the custom-additions to the TPMS_PCR_SELECTION class
*/

#define TPMS_PCR_SELECTION_CUSTOM_CLONE(l,r)

///<summary>Create a TPMS_PCR_SELECTION naming a single-PCR.</summary>
public:
TPMS_PCR_SELECTION(TPM_ALG_ID _alg, UINT32 _pcr)
{
    hash = _alg;
    UINT32 sz = 3;

    if ((_pcr / 8 + 1) > sz) {
        sz = _pcr / 8 + 1;
    }

    pcrSelect.resize(sz);
    pcrSelect[_pcr / 8] = (1 << (_pcr % 8));
}

///<summary>Create a TPMS_PCR_SELECTION for a set of PCR in a single bank.</summary>
public:
TPMS_PCR_SELECTION(TPM_ALG_ID _alg, const std::vector<UINT32>& pcrs)
{
    hash = _alg;
    UINT32 pcrMax = 0;

    for (UINT32 j = 0; j < pcrs.size(); j++)if (pcrs[j] > pcrMax) {
            pcrMax = pcrs[j];
        }

    if (pcrMax < 23) {
        pcrMax = 23;
    }

    pcrSelect.resize(0);
    pcrSelect.resize(pcrMax / 8 + 1);

    for (UINT32 j = 0; j < pcrs.size(); j++) {
        pcrSelect[pcrs[j] / 8] |= (1 << (pcrs[j] % 8));
    }

    return;
}

///<summary>Get a PCR-selection array naming exactly one PCR in one bank.</summary>
public:
static std::vector<TPMS_PCR_SELECTION> GetSelectionArray(TPM_ALG_ID _alg, UINT32 _pcr)
{
    return std::vector<TPMS_PCR_SELECTION> {TPMS_PCR_SELECTION(_alg, _pcr)};
}

///<summary>Is the PCR with index _pcr selected in this TPMS_PCR_SELECTION.</summary>
public:
bool PcrIsSelected(UINT32 _pcr)
{
    return (pcrSelect[_pcr / 8] = (1 << (_pcr % 8)) != 0);
}

///<summary>Return the current PCR-selection as a UINT32 array.</summary>
public:
std::vector<UINT32> ToArray()
{
    std::vector<UINT32> arr;
    int maxIs = pcrSelect.size() * 8;

    for (int j = 0; j < maxIs; j++) {
        if (PcrIsSelected(j)) {
            arr.push_back((UINT32)j);
        }
    }

    return arr;

}

///<summary>Create a TPMS_PCR_SELECTION with no PCR selected.</summary>
public:
static std::vector<TPMS_PCR_SELECTION> NullSelectionArray()
{
    return std::vector<TPMS_PCR_SELECTION>();
}
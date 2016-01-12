/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/

/*
The definitions in this file are #included in TpmTypes.h

These are the custom additions for the TPMT_HA class

*/

#define TPMT_HA_CUSTOM_CLONE(l,r)

///<summary>Create a TPMT_HA from the named-hash of the _data parameter.</summary>
public:
static TPMT_HA FromHashOfData(TPM_ALG_ID _alg, const std::vector<BYTE>& _data);

///<summary>Create a zero-bytes TPMT_HASH with the indicated hash-algorithm.</summary>
public:
TPMT_HA(TPM_ALG_ID alg);

// TODO: Unicode, etc.
///<summary>Create a TPMT_HA from the hash of the supplied-string.</summary>
public:
static TPMT_HA FromHashOfString(TPM_ALG_ID alg, const std::string& str);

///<summary>Perform a TPM-extend operation on the current hash-value.  Note
///the TPM only accepts hash-sized vector inputs: this function has no such limitations.</summary>
public:
TPMT_HA& Extend(const std::vector<BYTE>& x);

///<summary>Perform a TPM-event operation on this PCR-value (an event "extends" the hash of _x).</summary>
public:
TPMT_HA Event(const std::vector<BYTE>& _x);
public:
void Reset();


/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/

/*
The definitions in this file are #included in TpmTypes.h

These are the custom additions for the TPM_HANDLE class

*/

#define TPMT_TK_HASHCHECK_CUSTOM_CLONE(l,r)

///<summary>Create a TPMT_TK_HASHCHECK with a no contained ticket (for when no ticket is needed).</summary>
public:
static TPMT_TK_HASHCHECK NullTicket()
{
    TPMT_TK_HASHCHECK t;
    t.tag = TPM_ST::HASHCHECK;
    t.hierarchy = TPM_HANDLE::FromReservedHandle(TPM_RH::OWNER);
    return t;
}
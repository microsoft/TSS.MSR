/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/

/*
The definitions in this file are #included in TpmTypes.h

These are the custom additions for the TPMT_SYM_DEF class

*/

#define TPMT_SYM_DEF_CUSTOM_CLONE(l,r)

///<summary>Create a NULL SYM_DEF_OBJECT (one with a TPM_ALG_ID::NULL algorithm).</summary>
public:
static TPMT_SYM_DEF NullObject()
{
    return TPMT_SYM_DEF(TPM_ALG_ID::_NULL, 0, TPM_ALG_ID::_NULL);
}


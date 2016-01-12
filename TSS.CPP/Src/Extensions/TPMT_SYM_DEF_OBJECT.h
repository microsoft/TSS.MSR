/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/

/*
The definitions in this file are #included in TpmTypes.h

These are the custom additions for the TPMT_SYM_DEF_OBJECT class

*/
///<summary>Create a TPMT_SYM_DEF_OBJECT with a TPM_ALG_ID::NULL algorithms.</summary>
public:
static TPMT_SYM_DEF_OBJECT NullObject()
{
    TPMT_SYM_DEF_OBJECT s;
    s.algorithm = TPM_ALG_ID::_NULL;
    s.keyBits = 0;
    s.mode = TPM_ALG_ID::_NULL;
    return s;
};

#define TPMT_SYM_DEF_OBJECT_CUSTOM_CLONE(_l,_r)
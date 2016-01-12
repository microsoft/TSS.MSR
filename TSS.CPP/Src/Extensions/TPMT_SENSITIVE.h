/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/

/*
The definitions in this file are #included in TpmTypes.h

These are the custom additions for the TPMT_SENSITIVE class

*/

protected:
bool IsNullElement = false;

///<summary>Create an object suitable when the TPM needs a NULL-object input.</summary>
public:
static TPMT_SENSITIVE NullObject()
{
    TPMT_SENSITIVE s;
    // Make a something to keep the marshaller happy
    s.sensitive = new TPM2B_SYM_KEY();
    s.IsNullElement = true;
    return s;
};

protected:
virtual bool NullElement() const
{
    return IsNullElement;
};

// This might be a better way of dealing with custom elements than the current CustomCloner tag.
#define TPMT_SENSITIVE_CUSTOM_CLONE(_l,_r) _l->IsNullElement = _r.IsNullElement;
/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

///<summary> Customized TPMT_SYM_DEF_OBJECT implementation </summary>
class _DLLEXP_ TPMT_SYM_DEF_OBJECT : public _TPMT_SYM_DEF_OBJECT
{
public:
    TPMT_SYM_DEF_OBJECT() {}
    TPMT_SYM_DEF_OBJECT(TPM_ALG_ID algorithm, UINT16 keyBits, TPM_ALG_ID mode)
        : _TPMT_SYM_DEF_OBJECT( algorithm, keyBits, mode)
    {}
    virtual ~TPMT_SYM_DEF_OBJECT() {}

    ///<summary>Create a NULL SYM_DEF_OBJECT (one with a TPM_ALG_ID::NULL algorithm).</summary>
    static TPMT_SYM_DEF_OBJECT NullObject()
    {
        return TPMT_SYM_DEF_OBJECT(TPM_ALG_ID::_NULL, 0, TPM_ALG_ID::_NULL);
    }
};

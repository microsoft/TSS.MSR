/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

/// <summary>  Customized TPMT_SYM_DEF implementation </summary>
class _DLLEXP_ TPMT_SYM_DEF : public _TPMT_SYM_DEF
{
public:
    TPMT_SYM_DEF() {}
    TPMT_SYM_DEF(TPM_ALG_ID algorithm, UINT16 keyBits, TPM_ALG_ID mode)
        : _TPMT_SYM_DEF( algorithm, keyBits, mode)
    {}
    virtual ~TPMT_SYM_DEF() {}

    [[deprecated("Use default ctor instead")]]
    static TPMT_SYM_DEF NullObject()
    {
        return TPMT_SYM_DEF(TPM_ALG_ID::_NULL, 0, TPM_ALG_ID::_NULL);
    }
};

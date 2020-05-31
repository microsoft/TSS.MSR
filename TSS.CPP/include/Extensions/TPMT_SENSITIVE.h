/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

/// <summary> Customized TPMT_SENSITIVE implementation. </summary>
class _DLLEXP_ TPMT_SENSITIVE : public _TPMT_SENSITIVE
{
public:
    TPMT_SENSITIVE() {}
    TPMT_SENSITIVE(const ByteVec& authValue,
                   const ByteVec& seedValue,
                   const TPMU_SENSITIVE_COMPOSITE& sensitive)
        : _TPMT_SENSITIVE(authValue, seedValue, sensitive)
    {}
    virtual ~TPMT_SENSITIVE() {}

    /// <summary> Create an object suitable when the TPM needs a NULL-object input. </summary>
    [[deprecated("Use default ctor instead")]]
    static TPMT_SENSITIVE NullObject() { return TPMT_SENSITIVE(); };
};

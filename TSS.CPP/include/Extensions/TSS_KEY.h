/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

///<summary> Customized TSS_KEY implementation </summary>
class _DLLEXP_ TSS_KEY : public _TSS_KEY
{
public:
    TSS_KEY() {}
    TSS_KEY(const TPMT_PUBLIC& publicPart, const ByteVec& privatePart)
        : _TSS_KEY(publicPart, privatePart)
    {}
    virtual ~TSS_KEY() {}

    operator const TPMT_PUBLIC& () const { return publicPart; }

    ///<summary>Create a new software key based on the parameters in the publicPart.
    /// Sets the publicPart and privatePart memebers. </summary>
    void CreateKey();

    /// <summary>Sign the dataToSign byte array using the given signing scheme. 
    /// If the keys does not have a scheme of its own (i.e. was configuted with a NULL scheme),
    /// sigScheme must specify the same scheme or be a NULL scheme (TPMS_NULL_SIG_SCHEME). </summary>
    SignResponse Sign(const ByteVec& dataToSign, const TPMU_SIG_SCHEME& sigScheme) const;

    /// <summary>Sign the dataToSign byte array using the given key. </summary>
    SignResponse Sign(const ByteVec& dataToSign) const
    {
        return Sign(dataToSign, TPMS_NULL_SIG_SCHEME());
    }
};

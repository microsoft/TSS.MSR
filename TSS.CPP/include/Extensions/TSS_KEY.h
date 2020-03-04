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

    ///<summary>Create a new software key based on the parameters in the publicPart.  Set the public key value in publicPart
    /// and the private key in privatePart.</summary>
    void CreateKey();

    ///<summary>Sign the data _toSign based on the (default or overriden) scheme (signing keys only).</summary>
    SignResponse Sign(ByteVec& _toSign, const TPMU_SIG_SCHEME& nonDefaultScheme);

    ///<summary>Decrypt _blob (decrypting keys/schemes only).</summary>
    ByteVec Decrypt(ByteVec _blob);
};

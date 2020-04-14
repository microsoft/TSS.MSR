/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

///<summary> Customized TPMT_TK_HASHCHECK implementation </summary>
class _DLLEXP_ TPMT_TK_HASHCHECK : public _TPMT_TK_HASHCHECK
{
public:
    TPMT_TK_HASHCHECK() {}
    TPMT_TK_HASHCHECK(const TPM_HANDLE& hierarchy, const ByteVec& digest)
        : _TPMT_TK_HASHCHECK(hierarchy, digest)
    {}
    virtual ~TPMT_TK_HASHCHECK() {}

    ///<summary>Use default constructor instead</summary>
    [[deprecated("Use default ctor instead")]]
    static TPMT_TK_HASHCHECK NullTicket()
    {
        TPMT_TK_HASHCHECK t;
        t.hierarchy = TPM_HANDLE::FromReservedHandle(TPM_RH::OWNER);
        return t;
    }
};


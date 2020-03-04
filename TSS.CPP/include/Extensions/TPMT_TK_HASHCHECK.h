/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

///<summary> Customized TPMT_TK_HASHCHECK implementation </summary>
class _DLLEXP_ TPMT_TK_HASHCHECK : public _TPMT_TK_HASHCHECK
{
public:
    TPMT_TK_HASHCHECK() {}
    TPMT_TK_HASHCHECK(TPM_ST tag, const TPM_HANDLE& hierarchy, const ByteVec& digest)
        : _TPMT_TK_HASHCHECK(tag, hierarchy, digest)
    {}
    virtual ~TPMT_TK_HASHCHECK() {}

    ///<summary>Create a TPMT_TK_HASHCHECK with a no contained ticket (for when no ticket is needed).</summary>
    static TPMT_TK_HASHCHECK NullTicket()
    {
        TPMT_TK_HASHCHECK t;
        t.tag = TPM_ST::HASHCHECK;
        t.hierarchy = TPM_HANDLE::FromReservedHandle(TPM_RH::OWNER);
        return t;
    }
};


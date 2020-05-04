/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

///<summary> Customized TPM_HANDLE implementation </summary>
class _DLLEXP_ TPM_HANDLE : public _TPM_HANDLE
{
public:
    TPM_HANDLE() {}

    TPM_HANDLE(UINT32 h) : _TPM_HANDLE(h) {}
    virtual ~TPM_HANDLE() {}

    operator UINT32() const { return handle; }

    ///<summary>Create a NULL-TPM_HANDLE.</summary>
    static TPM_HANDLE Null() { return TPM_RH::_NULL; }

    ///<summary>Create a NULL-TPM_HANDLE.</summary>
    [[deprecated("Use default ctor, or TPM_RH_NULL, or TPM_HANDLE::Null() instead")]]
    static TPM_HANDLE NullHandle()
    {
        return Null();
    }

    ///<summary>Create a TPM_HANDLE from in the reserved handle-space
    /// (e.g. one of the admin handles).</summary>
    [[deprecated("Use ctor from UINT32 instead")]]
    static TPM_HANDLE FromReservedHandle(TPM_RH reservedHandle)
    {
        return TPM_HANDLE(reservedHandle);
    }

    ///<summary>Create a handle for a persistent object at the specified offset in the
    /// TPM_HT::PERSISTENT space.</summary>
    static TPM_HANDLE PersistentHandle(UINT32 handleOffset)
    {
        return (TPM_HT::PERSISTENT << 24) + handleOffset;
    }

    ///<summary>Create a TPM_HANDLE for a PCR with given-index.</summary>
    static TPM_HANDLE PcrHandle(int PcrIndex)
    {
        return PcrIndex;
    }

    ///<summary>Create a TPM_HANDLE for an NV-slot.</summary>
    static TPM_HANDLE NVHandle(int NvSlot)
    {
        return (TPM_HT::NV_INDEX << 24) + NvSlot;
    }

    ///<summary>Set the authorization value for this TPM_HANDLE.  The default auth-value is NULL.</summary>
    TPM_HANDLE& SetAuth(const ByteVec& authVal)
    {
        AuthValue = authVal;
        return *this;
    };

    ///<summary>Get the auth-value</summary>
    const ByteVec& GetAuth() const { return AuthValue; };

    ///<summary>Set the name of the associated object (not for handles with architectural names.</summary>
    void SetName(const ByteVec& name);

    ///<summary>Get the current name (calculated or assigned) for this TPM_HANDLE.</summary>
    ByteVec GetName() const;

    ///<summary>Get the top-byte of the TPM_HANDLE.</summary>
    TPM_HT GetHandleType() const
    {
        return TPM_HT(handle >> 24);
    };

protected:
    ByteVec AuthValue;
    mutable ByteVec Name;

}; // class TPM_HANDLE

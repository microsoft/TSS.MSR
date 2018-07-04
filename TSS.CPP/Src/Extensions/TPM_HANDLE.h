/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/

/*
The definitions in this file are #included in TpmTypes.h

These are the custom additions for the TPM_HANDLE class
*/

///<summary>Create a TPM_HANDLE from in the reserved handle-space
/// (e.g. one of the admin handles).</summary>
public:
    TPM_HANDLE (const TPM_RH& reservedHandle)
    {
        handle = (UINT32)reservedHandle;
    };

///<summary>Create a NULL-TPM_HANDLE.</summary>
public:
    static TPM_HANDLE NullHandle()
    {
        return TPM_HANDLE((UINT32)TPM_RH::_NULL);
    };

    ///<summary>Create a TPM_HANDLE from in the reserved handle-space
    /// (e.g. one of the admin handles).</summary>
public:
    static TPM_HANDLE FromReservedHandle(TPM_RH reservedHandle)
    {
        return TPM_HANDLE((UINT32)reservedHandle);
    };

    ///<summary>Create a handle for a persistent object at the specified offset in the
    /// TPM_HT::PERSISTENT space.</summary>
public:
    static TPM_HANDLE PersistentHandle(UINT32 handleOffset)
    {
        return TPM_HANDLE((((UINT32)TPM_HT::PERSISTENT) << 24) + handleOffset);
    };

    ///<summary>Create a TPM_HANDLE for a PCR with given-index.</summary>
public:
    static TPM_HANDLE PcrHandle(int PcrIndex)
    {
        TPM_HANDLE h((UINT32)PcrIndex);
        return h;
    };

    ///<summary>Create a TPM_HANDLE for an NV-slot.</summary>
public:
    static TPM_HANDLE NVHandle(int NvSlot)
    {
        UINT32 handleVal = (UINT32)((UINT32)TPM_HT::NV_INDEX << 24) + (UINT32)NvSlot;
        TPM_HANDLE h((UINT32)handleVal);
        return h;
    };

protected:
    std::vector<BYTE> AuthValue;

protected:
    std::vector<BYTE> Name;

    ///<summary>Set the authorization value for this TPM_HANDLE.  The default auth-value is NULL.</summary>
public:
    TPM_HANDLE& SetAuth(const std::vector<BYTE>& _authVal)
    {
        AuthValue = _authVal;
        return *this;
    };

    ///<summary>Get the auth-value</summary>
public:
    std::vector<BYTE>& GetAuth()
    {
        return AuthValue;
    };

    ///<summary>Set the name of the associated object (not for handles with architectural names.</summary>
public:
    void SetName(const std::vector<BYTE>& _name);

    ///<summary>Get the current name (calculated or assigned) for this TPM_HANDLE.</summary>
public:
    std::vector<BYTE> GetName();

    ///<summary>Get the top-byte of the TPM_HANDLE.</summary>
public:
    TPM_HT GetHandleType()
    {
        return TPM_HT((UINT32)(handle >> 24));
    };

#define TPM_HANDLE_CUSTOM_CLONE(_l,_r) (_l)->AuthValue=(_r).AuthValue;(_l)->Name=(_r).Name;

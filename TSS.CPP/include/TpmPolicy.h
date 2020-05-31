
/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once

_TPMCPP_BEGIN

//
// TpmPolicy.h/cpp contains code supporting TPM policies. All TPM policy commands have an associated
// class derived from PABase (policy-assertion base.) For example PolicyPcr is associated with
// TPM2_PolicyPCR. The class TpmPolicy manages policy trees and has methods for calculating policy
// hashes, executing policies and serialization. Policies are intrnally represented as 
// vector<PABase*>. Policies are executed from the end of the array towards the beginning. The
// POlicyOR class has an array of "branches," each of which is vector<PABase*>.
// 
// Any policy assertion class can have a string "tag." These are used for two purposes. First,
// some PAs need to call back to the hosting program to evaluate a policy (e.g. sign a TPM nonce).
// Second for a branchy-policy (one with PolicyOrs), the evaluator must be told which branch to
// execute: it does this by naming the tag of the leaf (we call a leaf tag a BranchId).
// 
// The library ensures that non-null-string tags are unique in a policy.
// 
// If the policy does not have branches then the library will automatically add the branch-ID
// "leaf" to the last element of the policy, if it is not already tagged.
// 
// Certain policies' executions need to call back to the main OS to get dynamic parameters.
// These callbacks are registered with the PolicyTree.
//

/// <summary> A PolicyNV-callback must provide the following information to TSS.C++. </summary>
class _DLLEXP_ PolicyNVCallbackData
{
public:
    TPM_HANDLE AuthHandle;
    TPM_HANDLE NvIndex;
};

/// <summary> A PolicyNV-callback must have this form </summary>
typedef PolicyNVCallbackData (PolicyNvCallback)(const string& _tag);

/// <summary> A PolicySigned callback should be of this type. </summary>
typedef SignResponse (PolicySignedCallback)(const ByteVec& _nonceTpm, 
                                            UINT32 _expiration,
                                            const ByteVec& _cpHashA,
                                            const ByteVec& _policyRef,
                                            const string& _tag);

/// <summary> PABase is the base class for all TPM policy assertions. Derived classes must provide
/// Execute() and PolicyHash() implementations </summary>
class _DLLEXP_ PABase
{
    friend class PolicyTree;

protected:
    string Tag;

    // When a policy-chain is created with the streaming operators we maintain
    // to-next and to-last links
    PABase *last = NULL;
    PABase *next = NULL;

public:
    PABase() {};
    PABase(const PABase& r) : Tag(r.Tag) {}
    PABase(const string& tag) : Tag(tag) {}
    virtual ~PABase() {};

    virtual PABase* Clone() const = 0;

    //virtual PABase& operator=(const PABase&) = 0;

    string GetTag() const { return Tag; }

    PABase& operator<<(PABase& r)
    {
        next = &r;
        r.last = this;
        return r;
    }

protected:
    static void PolicyUpdate(TPM_HASH& policyDigest,  TPM_CC commandCode,
                             const ByteVec& arg2, const ByteVec& arg3);

    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const = 0;

    virtual void Execute(Tpm2&, class PolicyTree&) = 0;
}; // calss PABase

/// <summary> TpmPolicy provides machinery for manipulating with TPM 2.0 policies </summary>
class _DLLEXP_ PolicyTree
{
    friend class PolicyOr;
    friend class PolicyNV;
    friend class PolicySigned;

    vector<PABase*>         Policy;
    PolicyNvCallback*       theNvCallback = NULL;
    PolicySignedCallback*   theSignCallback = NULL;

protected:
    static bool ChainContainsBranch(const vector<PABase*>& chain, const string& branchId);
    static TPM_HASH GetPolicyDigest(const vector<PABase*>& chain, TPM_ALG_ID hashAlg);
    static string GetBranchId(const vector<PABase*>& chain);
    static vector<string> GetBranchIds(const vector<PABase*>& chain);
    static void GetBranchIdsInternal(const vector<PABase*>& chain,
                                     vector<string>& branchIds, std::map<string, int>& allIds);
    void Execute(Tpm2& tpm, vector<PABase *>& chain, const string& branchId);

public:
    /// <summary> Create an empty policy tree.
    /// SetTree() can be used to add policy assertions. </summary>
    PolicyTree() {}
        
    /// <summary> Create a policy tree with one policy assertion. </summary>
    PolicyTree(const PABase& p0);
        
    /// <summary> Create a policy tree with two policy assertions. </summary>
    PolicyTree(const PABase& p0, const PABase& p1);
        
    /// <summary> Create a policy tree with three assertions. </summary>
    PolicyTree(const PABase& p0, const PABase& p1, const PABase& p2);
        
    /// <summary> Create a policy tree with four assertions. </summary>
    PolicyTree(const PABase& p0, const PABase& p1, const PABase& p2, const PABase& p3);
        
    /// <summary> Create a policy tree with five assertions. </summary>
    PolicyTree(const PABase& p0, const PABase& p1, const PABase& p2, const PABase& p3, const PABase& p4);
        
    /// <summary> Create a policy tree with one or more policy assertions.
    /// Note: a copy is made of the objects provided. </summary>
    PolicyTree(const vector<PABase*>& policyBranch)
    {
        SetTree(policyBranch);
    }
        
    ~PolicyTree();

    /// <summary> Set the policy-tree.  Note: a copy is made of all objects (the caller
    /// need not keep around the objects in _tree) </summary>
    void SetTree(const vector<PABase*>& policyBranch);

    const vector<PABase*>GetTree() const { return Policy; }

    /// <summary> Return the (software-calculated) policy-digest. </summary>
    TPM_HASH GetPolicyDigest(TPM_ALG_ID hashAlg) const;

    /// <summary> Execute the policy with specified branchId. </summary>
    TPM_RC Execute(Tpm2& tpm, AUTH_SESSION& s, string branchId = "leaf");

    /// <summary> The session for the policy (valid during policy-execution). </summary>
    AUTH_SESSION *Session = NULL;

    // Callback installation

    /// <summary> Register a callback function for PolicyNV policy assertions. The callback
    /// is invoked during execution. The caller must provide invocation-specific information
    /// like the NV-handle of the NV-slot referenced by name/tag.
    /// </summary>
    void SetPolicyNvCallback(PolicyNvCallback* callback) { theNvCallback = callback; }

    /// <summary> Register a callback for PolicySigned assertions. The callback will be invoked
    /// to obtain a signature over a TPM-nonce (and other data) when the policy is executed.
    /// </summary>
    void SetPolicySignedCallback(PolicySignedCallback* callback) { theSignCallback = callback; }
}; // class PolicyTree

/// <summary> This command indicates that the authorization will be limited to a specific locality </summary>
class _DLLEXP_ PolicyLocality : public PABase
{
    TPMA_LOCALITY Locality;
public:
    /// <summary>This command indicates that the authorization will be limited to a specific locality </summary>
    PolicyLocality(TPMA_LOCALITY locality, const string& tag = "")
        : PABase(tag), Locality(locality)
    {}
    PolicyLocality(const PolicyLocality& r)
        : PABase(r), Locality(r.Locality)
    {}

    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const;
    virtual void Execute(Tpm2& tpm, PolicyTree& p);

    virtual PABase* Clone() const { return new PolicyLocality(*this); }
};

/// <summary>This command indicates that physical presence will need to be asserted
/// at the time the authorization is performed. </summary>
class _DLLEXP_ PolicyPhysicalPresence : public PABase
{
public:
    PolicyPhysicalPresence(const string& tag = "") : PABase(tag) {}
    PolicyPhysicalPresence(const PolicyPhysicalPresence& r) : PABase(r) {}
        
    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const;
    virtual void Execute(Tpm2& tpm, PolicyTree& p);

    virtual PABase* Clone() const { return new PolicyPhysicalPresence(*this); }
};

/// <summary> This command allows options in authorizations without requiring that the TPM
/// evaluate all of the options. If a policy may be satisfied by different sets of conditions,
/// the TPM need only evaluate one set that satisfies the policy. This command will indicate
/// that one of the required sets of conditions has been satisfied. </summary>
class _DLLEXP_ PolicyOr : public PABase
{
    friend class PolicyTree;

    vector<vector<PABase*>> Branches;

public:
    PolicyOr(const vector<vector<PABase*>>& branches, const string& tag = "")
        : PABase(tag)
    {
        Init(branches);
    }
    PolicyOr(const vector<PABase*>& branch1, const vector<PABase*>& branch2, const string& tag = "")
        : PABase(tag)
    {
        Init({branch1, branch2});
    }
    PolicyOr(const PolicyOr& r) : PABase(r)
    {
        Init(r.Branches);
    }
    virtual ~PolicyOr();

    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const;
    virtual void Execute(Tpm2& tpm, PolicyTree& p);

    virtual PABase* Clone() const { return new PolicyOr(*this); }

private:
    void Init(const vector<vector<PABase*>>& branches);
};

/// <summary>This command is used to cause conditional gating of a policy based on PCR. This
/// allows one group of authorizations to occur when PCRs are in one state and a different
/// set of authorizations when the PCRs are in a different state. If this command is used
/// for a trial policySession, the policyHash will be updated using the values from the command
/// rather than the values from digest of the TPM PCR. </summary>
class _DLLEXP_ PolicyPcr : public PABase
{
    vector<TPM2B_DIGEST> PcrValues;
    vector<TPMS_PCR_SELECTION> Pcrs;

public:
    PolicyPcr(const vector<TPM2B_DIGEST>& pcrValues,
              const vector<TPMS_PCR_SELECTION>& pcrs, const string& tag = "")
        : PABase(tag), PcrValues(pcrValues), Pcrs(pcrs)
    {}
    PolicyPcr(const PolicyPcr& r)
        : PABase(r), PcrValues(r.PcrValues), Pcrs(r.Pcrs)
    {}

    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const;
    virtual void Execute(Tpm2& tpm, PolicyTree& p);

    virtual PABase* Clone() const { return new PolicyPcr(*this); }
};

/// <summary> This command indicates that the authorization will be limited to a
/// specific command code. </summary>
class _DLLEXP_ PolicyCommandCode : public PABase
{
    TPM_CC CommandCode;

public:
    PolicyCommandCode(TPM_CC commandCode, const string& tag = "")
        : PABase(tag), CommandCode(commandCode)
    {}
    PolicyCommandCode(const PolicyCommandCode& r)
        : PABase(r), CommandCode(r.CommandCode)
    {}

    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const;
    virtual void Execute(Tpm2& tpm, PolicyTree& p);

    virtual PABase* Clone() const { return new PolicyCommandCode(*this); }
};

/// <summary> This command is used to allow a policy to be bound to a specific command
/// and command parameters. </summary>
class _DLLEXP_ PolicyCpHash : public PABase
{
    ByteVec CpHash;

public:
    PolicyCpHash(ByteVec _cpHash, const string& tag = "")
        : PABase(tag), CpHash(_cpHash)
    {}
    PolicyCpHash(const PolicyCpHash& r)
        : PABase(r), CpHash(r.CpHash)
    {}

    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const;
    virtual void Execute(Tpm2& tpm, PolicyTree& p);

    virtual PABase* Clone() const { return new PolicyCpHash(*this);}
};

/// <summary>This command is used to cause conditional gating of a policy based on the
/// contents of the TPMS_TIME_INFO structure. </summary>
class _DLLEXP_ PolicyCounterTimer : public PABase
{
    friend class PolicyTree;

    ByteVec OperandB;
    UINT16 Offset;
    TPM_EO Operation;

public:
    PolicyCounterTimer(ByteVec operandB, UINT16 offset, TPM_EO operation, const string& tag = "")
        : PABase(tag), OperandB(operandB), Offset(offset), Operation(operation)
    {}
    PolicyCounterTimer(UINT64 operandB, UINT16 offset, TPM_EO operation, const string& tag = "");
    PolicyCounterTimer(const PolicyCounterTimer& r)
        : PABase(r), OperandB(r.OperandB), Offset(r.Offset), Operation(r.Operation)
    {}

    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const;
    virtual void Execute(Tpm2& tpm, PolicyTree& p);

    virtual PABase* Clone() const { return new PolicyCounterTimer(*this); }
};

/// <summary>This command allows a policy to be bound to a specific set of handles
/// without being bound to the parameters of the command. This is most useful for
/// commands such as TPM2_Duplicate() and for TPM2_PCR_Event() when the referenced
/// PCR requires a policy. </summary>
class _DLLEXP_ PolicyNameHash : public PABase
{
    ByteVec NameHash;

public:
    PolicyNameHash(ByteVec nameHash, const string& tag = "")
        : PABase(tag), NameHash(nameHash)
    {}
    PolicyNameHash(const PolicyNameHash& r)
        : PABase(r), NameHash(r.NameHash)
    {}

    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const;
    virtual void Execute(Tpm2& tpm, PolicyTree& p);

    virtual PABase* Clone() const { return new PolicyNameHash(*this); }
};

/// <summary> PolicyAuthValue indicates that an auth-value HMAC must be provided during policy use </summary>
class _DLLEXP_ PolicyAuthValue : public PABase
{
public:
    /// <summary> PolicyAuthValue indicates that an auth-value HMAC must be provided during policy use </summary>
    PolicyAuthValue(const string& tag = "") : PABase(tag) {}
    PolicyAuthValue(const PolicyAuthValue& r) : PABase(r) {}

    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const;
    virtual void Execute(Tpm2& tpm, PolicyTree& p);

    virtual PABase* Clone() const { return new PolicyAuthValue(*this); }
};

/// <summary>This command allows a policy to be bound to the authorization value of the
/// authorized object (PWAP). </summary>
class _DLLEXP_ PolicyPassword : public PABase
{
public:
    PolicyPassword(const string& tag = "") : PABase(tag) {}
    PolicyPassword(const PolicyPassword& r) : PABase(r) {}

    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const;
    virtual void Execute(Tpm2& tpm, PolicyTree& p);

    virtual PABase* Clone() const { return new PolicyPassword(*this); }
};

/// <summary>This command is used to cause conditional gating of a policy based
/// on the contents of an NV Index. </summary>
class _DLLEXP_ PolicyNV : public PABase
{
    ByteVec OperandB;
    UINT16 Offset;
    TPM_EO Operation;
public:
    PolicyNV(TPM_HANDLE& authHandle, TPM_HANDLE& nvIndex, const ByteVec& nvIndexName,
             const ByteVec& operandB, UINT16 offset, TPM_EO operation, const string& tag = "")
      : PABase(tag), OperandB(operandB), Offset(offset), Operation(operation),
        AuthHandle(authHandle), NvIndex(nvIndex), NvIndexName(nvIndexName)
    {}

    /// <summary>This command is used to cause conditional gating of a policy
    /// based on the contents of an NV Index. </summary>
    PolicyNV(const PolicyNV& r)
      : PABase(r), OperandB(r.OperandB), Offset(r.Offset), Operation(r.Operation),
        AuthHandle(r.AuthHandle),
        NvIndex(r.NvIndex), NvIndexName(r.NvIndexName), CallbackNeeded(r.CallbackNeeded)
    {}

    /// <summary>This command is used to cause conditional gating of a policy
    /// based on the contents of an NV Index. </summary>
    PolicyNV(const ByteVec& operandB, const ByteVec& nvIndexName,
                UINT16 offset, TPM_EO operation, const string& tag = "")
        : PABase(tag), OperandB(operandB), Offset(offset), Operation(operation),
        NvIndexName(nvIndexName), CallbackNeeded(true)
    {}

    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const;
    virtual void Execute(Tpm2& tpm, PolicyTree& p);

    virtual PABase* Clone() const { return new PolicyNV(*this); }

protected:
    // Dynamic parameters
    TPM_HANDLE AuthHandle;
    TPM_HANDLE NvIndex;
    ByteVec NvIndexName;

    bool CallbackNeeded = false;
};

/// <summary>This command includes an asymmetrically signed authorization in a policy. </summary>
class _DLLEXP_ PolicySigned : public PABase
{
    bool IncludeTpmNonce;
    ByteVec CpHashA;
    ByteVec PolicyRef;
    UINT32 Expiration;
    TPMT_PUBLIC PublicKey;
public:
    PolicySigned(bool useNonce, const ByteVec& cpHashA, const ByteVec& policyRef,
                 UINT32 expiration, const TPMT_PUBLIC& pubKey, const string& tag = "")
      : PABase(tag), IncludeTpmNonce(useNonce), CpHashA(cpHashA),
        PolicyRef(policyRef), Expiration(expiration), PublicKey(pubKey)
    {}
    PolicySigned(const PolicySigned& r)
        : PABase(r), IncludeTpmNonce(r.IncludeTpmNonce), CpHashA(r.CpHashA),
        PolicyRef(r.PolicyRef), Expiration(r.Expiration), PublicKey(r.PublicKey),
        FullKey(r.FullKey), CallbackNeeded(r.CallbackNeeded)
    {}

    /// <summary> Normally the policy evaluator will "call back" into the calling porgram
    /// to obtain the signature over the nonce (etc.)  However if the key is in a (software)
    /// TSS_KEY then TSS.C++ can do the signature for your without a callback </summary>
    void SetKey(const TSS_KEY& _key)
    {
        FullKey = _key;
        CallbackNeeded = FALSE;
    }

    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const;
    virtual void Execute(Tpm2& tpm, PolicyTree& p);

    virtual PABase* Clone() const { return new PolicySigned(*this); }

protected:
    // Dynamic params
    TSS_KEY FullKey;
    bool CallbackNeeded = true;
};

/// <summary> PolicyAuthorize transforms a policyHash into a value derived from a public
/// key if the corresponding private key holder has authorized the pre-value with a
/// signature </summary>
class _DLLEXP_ PolicyAuthorize : public PABase
{
    ByteVec ApprovedPolicy;
    ByteVec PolicyRef;
    TPMT_PUBLIC AuthorizingKey;
    TPMT_SIGNATURE Signature;

public:
    PolicyAuthorize(const ByteVec& approvedPolicy, const ByteVec& policyRef,
                    const TPMT_PUBLIC& authorizingKey, const TPMT_SIGNATURE& signature,
                    const string& tag = "")
      : PABase(tag), ApprovedPolicy(approvedPolicy), PolicyRef(policyRef),
        AuthorizingKey(authorizingKey), Signature(signature)
    {}

    /// <summary> PolicyAuthorize transforms a policyHash into a value derived from a public
    /// key if the corresponding private key holder has authorized the pre-value with a
    /// signature </summary>
    PolicyAuthorize(const PolicyAuthorize& r)
        : PABase(r), ApprovedPolicy(r.ApprovedPolicy), PolicyRef(r.PolicyRef),
        AuthorizingKey(r.AuthorizingKey), Signature(r.Signature)
    {}

    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const;
    virtual void Execute(Tpm2& tpm, PolicyTree& p);

    virtual PABase* Clone() const { return new PolicyAuthorize(*this); }
};

/// <summary>This command includes a secret-based authorization to a policy.
/// The caller proves knowledge of the secret value using either a password or 
/// an HMAC-based authorization session. </summary>
class _DLLEXP_ PolicySecret : public PABase
{
    bool IncludeTpmNonce;
    ByteVec CpHashA;
    ByteVec PolicyRef;
    UINT32 Expiration;
    ByteVec AuthObjectName;

public:
    /// <summary> This command includes a secret-based authorization to a policy.
    /// The caller proves knowledge of the secret value using either a password or
    /// an HMAC-based authorization session. </summary>
    PolicySecret(bool useNonce, const ByteVec& cpHashA, const ByteVec& policyRef,
                 UINT32 expiration, const ByteVec& authObjectName, const string& tag = "")
      : PABase(tag), IncludeTpmNonce(useNonce), CpHashA(cpHashA),
        PolicyRef(policyRef), Expiration(expiration), AuthObjectName(authObjectName)
    {}

    /// <summary>This command includes a secret-based authorization to a policy.
    /// The caller proves knowledge of the secret value using either a password
    /// or an HMAC-based authorization session. </summary>
    PolicySecret(const PolicySecret& r)
        : PABase(r), IncludeTpmNonce(r.IncludeTpmNonce), CpHashA(r.CpHashA),
        PolicyRef(r.PolicyRef), Expiration(r.Expiration), AuthObjectName(r.AuthObjectName),
        AuthHandle(r.AuthHandle), CallbackNeeded(r.CallbackNeeded)
    {}

    /// <summary> Normally the policy evaluator will "call back" into the calling program
    /// to obtain the signature over the nonce (etc.)  However if the key is in
    /// a (software) TSS_KEY then TSS.C++ can do the signature for your without
    /// a callback </summary>
    void SetAuthorizingObjectHandle(const TPM_HANDLE& handle)
    {
        AuthHandle = handle;
        CallbackNeeded = false;
    }

    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const
    {
        PolicyUpdate(accumulator, TPM_CC::PolicySecret, AuthObjectName, PolicyRef);
    }

    virtual void Execute(Tpm2& tpm, PolicyTree& p);

    virtual PABase* Clone() const { return new PolicySecret(*this); }

protected:
    TPM_HANDLE AuthHandle;
    bool CallbackNeeded = true;
};

// TODO: Not tested, potentially incomplete.
/// <summary> This command is similar to TPM2_PolicySigned() except that it takes a
/// ticket instead of a signed authorization. The ticket represents a validated
/// authorization that had an expiration time associated with it. </summary>
class _DLLEXP_ PolicyTicket : public PABase
{
    bool IncludeTpmNonce;
    ByteVec CpHashA;
    ByteVec PolicyRef;
    UINT32 Expiration;
    TPMT_PUBLIC PublicKey;

public:
    PolicyTicket(bool useNonce, const ByteVec& cpHashA, const ByteVec& policyRef,
                 UINT32 expiration, const TPMT_PUBLIC& pubKey, const string& tag = "")
      : PABase(tag), IncludeTpmNonce(useNonce), CpHashA(cpHashA),
        PolicyRef(policyRef), Expiration(expiration), PublicKey(pubKey)
    {}

    /// <summary>This command includes an asymmetrically signed authorization in a policy. </summary>
    PolicyTicket(const PolicyTicket& r)
        : PABase(r), IncludeTpmNonce(r.IncludeTpmNonce), CpHashA(r.CpHashA),
        PolicyRef(r.PolicyRef), Expiration(r.Expiration), PublicKey(r.PublicKey),
        FullKey(r.FullKey), CallbackNeeded(r.CallbackNeeded)
    {}

    /// <summary> Normally the policy evaluator will "call back" into the caller to get
    /// the signature over the nonce (etc.)  However if the key is in a (software) TSS_KEY
    /// then the TSS can do the signature for you without a callback </summary>
    void SetKey(const TSS_KEY& _key)
    {
        FullKey = _key;
        CallbackNeeded = FALSE;
    }

    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const;
    virtual void Execute(Tpm2& tpm, PolicyTree& p);

    virtual PABase* Clone() const { return new PolicyTicket(*this); }

protected:
    TSS_KEY FullKey;
    bool CallbackNeeded = true;
};

/// <summary>This command allows qualification of duplication to allow duplication to
/// a selected new parent. </summary>
class _DLLEXP_ PolicyDuplicationSelect : public PABase
{
    ByteVec ObjectName;
    ByteVec NewParentName;
    bool IncludeObjectName;

public:
    PolicyDuplicationSelect(const ByteVec& objName, const ByteVec& newParentName,
                            bool includeObject, const string& tag = "")
        : PABase(tag), ObjectName(objName), NewParentName(newParentName), IncludeObjectName(includeObject)
    {}

    PolicyDuplicationSelect(const PolicyDuplicationSelect& r)
        : PABase(r), ObjectName(r.ObjectName), NewParentName(r.NewParentName), IncludeObjectName(r.IncludeObjectName)
    {}

    virtual void UpdatePolicyDigest(TPM_HASH& accumulator) const;
    virtual void Execute(Tpm2& tpm, PolicyTree& p);

    virtual PABase *Clone() const { return new PolicyDuplicationSelect(*this); }
};

_TPMCPP_END

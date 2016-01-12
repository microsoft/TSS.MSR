
/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#pragma once

_TPMCPP_BEGIN

//
// Policy.h/cpp contains code supporting TPM policies. All TPM policy commands have an associated
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

///<summary>A PolicyNV-callback must provide the following information to TSS.C++.</summary>
class _DLLEXP_ PolicyNVCallbackData {
    public:
        TPM_HANDLE AuthorizationHandle;
        TPM_HANDLE NvIndex;
};

///<summary>A PolicyNV-callback must have this form</summary>
typedef PolicyNVCallbackData (PolicyNvCallback)(const string& _tag);

///<summary>A PolicySigned callback should be of this type.</summary>
typedef SignResponse (PolicySignedCallback)(const ByteVec& _nonceTpm, 
                                            UINT32 _expiration,
                                            const ByteVec& _cpHashA,
                                            const ByteVec& _policyRef,
                                            const string& _tag);

///<summary>PABase is the base class for all TPM policy assertions. Derived classes must provide
/// Execute() and PolicyHash() implementations</summary>
class _DLLEXP_ PABase {
        friend class PolicyTree;
    public:
        PABase() {};

        PABase(const PABase& r) {
            Tag = r.Tag;
        }

        virtual ~PABase() {};

        virtual PABase *Clone() {
            _ASSERT(NULL);
            return NULL;
        }

        virtual PABase operator=(const PABase& _r) {
            _ASSERT(FALSE);
            return PABase();
        }

        string GetTag() const {
            return Tag;
        }

        PABase& operator<<(PABase& r) {
            this->next = &r;
            r.last = this;
            return r;
        }
    protected:
        static void PolicyUpdate(TPMT_HA& policyDigest, 
                                 TPM_CC commandCode,
                                 std::vector<BYTE> arg2,
                                 std::vector<BYTE> arg3);
        std::string Tag;

        virtual void UpdatePolicyDigest(TPMT_HA& accumulator) {
            _ASSERT(FALSE);
        }

        virtual void Execute(class Tpm2& tpm, class PolicyTree& p) {
            _ASSERT(FALSE);
            return;
        }

        // When a policy-chain is created with the streaming operators we maintain
        // to-next and to-last links
        PABase *last = NULL;
        PABase *next = NULL;
};

///<summary>TpmPolicy provides machinery for creating, serializing, executing, etc. policies </summary>
class _DLLEXP_ PolicyTree {
        friend class PolicyOr;
        friend class PolicyNV;
        friend class PolicySigned;
    public:
        ///<summary>Create a policy tree no assertions (use SetTree).</summary>
        PolicyTree() {}
        
        ///<summary>Create a policy tree with one or more policy assertions.</summary>
        PolicyTree(const PABase& _p0);
        
        ///<summary>Create a policy tree with one or more policy assertions.</summary>
        PolicyTree(const PABase& _p0, const PABase& _p1);
        
        ///<summary>Create a policy tree with one or more policy assertions.</summary>
        PolicyTree(const PABase& _p0, const PABase& _p1, const PABase& _p2);
        
        ///<summary>Create a policy tree with one or more policy assertions.</summary>
        PolicyTree(const PABase& _p0, const PABase& _p1, const PABase& _p2, const PABase& _p3);
        
        ///<summary>Create a policy tree with one or more policy assertions.</summary>
        PolicyTree(const PABase& _p0, const PABase& _p1, const PABase& _p2, const PABase& _p3, const PABase& _p4);
        
        ///<summary>Create a policy tree with one or more policy assertions.  Note: a copy is
        ///made of the objects provided</summary>
        PolicyTree(const vector<PABase *>& _policy);
        
        ///<summary>Set the policy-tree.  Note: a copy is made of all objects (the caller
        /// need not keep around the objects in _tree)</summary>
        void SetTree(const vector<PABase *>& _tree);

        const vector<PABase *> GetTree() const {
            return Policy;
        }

        ~PolicyTree();

        ///<summary>Return the (software-calculated) policy-digest.</summary>
        TPMT_HA GetPolicyDigest(TPM_ALG_ID hashAlg);

        ///<summary>Execute the policy with specified branchId.</summary>
        TPM_RC Execute(class Tpm2& tpm, AUTH_SESSION& s, string branchId = "leaf");

        ///<summary>The session for the policy (valid during policy-execution).</summary>
        AUTH_SESSION *Session = NULL;

        // Callback installation

        ///<summary>Register a callback function for PolicyNV policy assertions. The callback
        /// is invoked during execution. The caller must provide invocation-specific information
        /// like the NV-handle of the NV-slot referenced by name/tag.</summary>
        void SetPolicyNvCallback(PolicyNvCallback *_callback) {
            theNvCallback = _callback;
        }

        ///<summary>Register a callback for PolicySigned assertions. The callback will be invoked
        /// to obtain a signature over a TPM-nonce (and other information) when the policy is
        /// executed.</summary>
        void SetPolicySignedCallback(PolicySignedCallback *_callback) {
            theSignCallback = _callback;
        }

    protected:
        static bool ChainContainsBranch(vector<PABase *>& _chain, string branchId);
        static TPMT_HA GetPolicyDigest(vector<PABase *>& _1chain, TPM_ALG_ID hashAlg);
        static std::string GetBranchId(std::vector<PABase *>& chain);
        static vector<string> GetBranchIds(std::vector<PABase *>& chain);
        static void GetBranchIdsInternal(const std::vector<PABase *>& chain, vector<string>& branchIds, map<string, int>& allIds);
        void Execute(class Tpm2& tpm, std::vector<PABase *>& chain, string branchId);
    protected:
        std::vector<PABase *> Policy;
        PolicyNvCallback *theNvCallback = NULL;
        PolicySignedCallback *theSignCallback = NULL;
};

/// <summary> This command indicates that the authorization will be limited to a specific locality</summary>
class _DLLEXP_ PolicyLocality : public PABase {
    public:
        /// <summary>This command indicates that the authorization will be limited to a specific locality</summary>
        PolicyLocality(TPMA_LOCALITY _locality, string _tag = "") {
            Locality = _locality;
            Tag = _tag;
        }
        PolicyLocality(const PolicyLocality& r) : Locality(r.Locality) {
            Tag = r.Tag;
        }
        virtual void UpdatePolicyDigest(TPMT_HA& accumulator);
        virtual void Execute(class Tpm2& tpm, PolicyTree& p);
        virtual PABase *Clone() {
            return new PolicyLocality(this->Locality, this->Tag);
        }
        virtual PolicyLocality operator=(const PolicyLocality& r) {
            return PolicyLocality(r);
        }
    protected:
        TPMA_LOCALITY Locality;
};

/// <summary>This command indicates that physical presence will need to be asserted
/// at the time the authorization is performed.</summary>
class _DLLEXP_ PolicyPhysicalPresence : public PABase {
    public:
        /// <summary> This command indicates that physical presence will need to be asserted
        /// at the time the authorization is performed.</summary>
        PolicyPhysicalPresence(string _tag = "") {
            Tag = _tag;
        }
        PolicyPhysicalPresence(const PolicyPhysicalPresence& r) {
            Tag = r.Tag;
        }
        virtual void UpdatePolicyDigest(TPMT_HA& accumulator);
        virtual void Execute(class Tpm2& tpm, PolicyTree& p);
        virtual PABase *Clone() {
            return new PolicyPhysicalPresence(this->Tag);
        }
        virtual PolicyPhysicalPresence  operator=(const PolicyPhysicalPresence& r) {
            return PolicyPhysicalPresence(r);
        }
    protected:
};

/// <summary> This command allows options in authorizations without requiring that the TPM
/// evaluate all of the options. If a policy may be satisfied by different sets of conditions,
/// the TPM need only evaluate one set that satisfies the policy. This command will indicate
/// that one of the required sets of conditions has been satisfied.</summary>
class _DLLEXP_ PolicyOr : public PABase {
        friend class PolicyTree;
    public:
        /// <summary> This command allows options in authorizations without requiring that the
        /// TPM evaluate all of the options. If a policy may be satisfied by different sets of
        /// conditions, the TPM need only evaluate one set that satisfies the policy. This command
        /// will indicate that one of the required sets of conditions has been satisfied.</summary>
        PolicyOr(std::vector<std::vector<PABase *>> branches, string _tag = "");
        PolicyOr(std::vector<PABase *> branch1, std::vector<PABase *> branch2, string _tag = "");
        PolicyOr(std::vector<PABase *> branch1, std::vector<PABase *> branch2, std::vector<PABase *> branch3, string _tag = "");
        PolicyOr(const PolicyOr& r);
        virtual ~PolicyOr();
        virtual void UpdatePolicyDigest(TPMT_HA& accumulator);
        virtual void Execute(class Tpm2& tpm, PolicyTree& p);
        virtual PABase *Clone();
        virtual PolicyOr  operator=(const PolicyOr& r) {
            return PolicyOr(r);
        }
    protected:
        void Init(std::vector<std::vector<PABase *>> branches);
        std::vector<std::vector<PABase *>> Branches;
};

/// <summary>This command is used to cause conditional gating of a policy based on PCR. This
/// allows one group of authorizations to occur when PCRs are in one state and a different
/// set of authorizations when the PCRs are in a different state. If this command is used
/// for a trial policySession, the policyHash will be updated using the values from the command
/// rather than the values from digest of the TPM PCR.</summary>
class _DLLEXP_ PolicyPcr : public PABase {
        friend class PolicyTree;
    public:
        /// <summary>This command is used to cause conditional gating of a policy based on PCR.
        /// This allows one group of authorizations to occur when PCRs are in one state
        /// and a different set of authorizations when the PCRs are in a different state.
        /// If this command is used for a trial policySession, the policyHash will be
        /// updated using the values from the command rather than the values from
        /// digest of the TPM PCR. </summary>
        PolicyPcr(std::vector<TPM2B_DIGEST>& _pcrValues, vector<TPMS_PCR_SELECTION>& _pcrs,
                  string _tag = "") : PcrValues(_pcrValues), Pcrs(_pcrs) {
            Tag = _tag;
        }
        PolicyPcr(const PolicyPcr& r) : PcrValues(r.PcrValues), Pcrs(r.Pcrs) {
            Tag = r.Tag;
        }
        virtual void UpdatePolicyDigest(TPMT_HA& accumulator);
        virtual void Execute(class Tpm2& tpm, PolicyTree& p);
        virtual PABase *Clone() {
            return new PolicyPcr(this->PcrValues, this->Pcrs, this->Tag);
        }
        virtual PolicyPcr  operator=(const PolicyPcr& r) {
            return PolicyPcr(r);
        }
    protected:
        std::vector<BYTE> GetPcrValueDigest(TPM_ALG_ID hashAlg);
        std::vector<TPM2B_DIGEST> PcrValues;
        vector<TPMS_PCR_SELECTION> Pcrs;
};

/// <summary> This command indicates that the authorization will be limited to a
/// specific command code.</summary>
class _DLLEXP_ PolicyCommandCode : public PABase {
        friend class PolicyTree;
    public:
        /// <summary>This command indicates that the authorization will be limited
        /// to a specific command code.</summary>
        PolicyCommandCode(TPM_CC _commandCode, string _tag = "") : CommandCode(_commandCode) {
            Tag = _tag;
        }
        PolicyCommandCode(const PolicyCommandCode& r) : CommandCode(r.CommandCode) {
            Tag = r.Tag;
        }
        virtual void UpdatePolicyDigest(TPMT_HA& accumulator);
        virtual void Execute(class Tpm2& tpm, PolicyTree& p);
        virtual PABase *Clone() {
            return new PolicyCommandCode(this->CommandCode, this->Tag);
        }
        virtual PolicyCommandCode  operator=(const PolicyCommandCode& r) {
            return PolicyCommandCode(r);
        }
    protected:
        TPM_CC CommandCode;
};

/// <summary> This command is used to allow a policy to be bound to a specific command
/// and command parameters.</summary>
class _DLLEXP_ PolicyCpHash : public PABase {
        friend class PolicyTree;
    public:
        /// <summary>This command is used to allow a policy to be bound to a specific command
        /// and command parameters.</summary>
        PolicyCpHash(vector<BYTE> _cpHash, string _tag = "") : CpHash(_cpHash) {
            Tag = _tag;
        }
        PolicyCpHash(const PolicyCpHash& r) : CpHash(r.CpHash) {
            Tag = r.Tag;
        }
        virtual void UpdatePolicyDigest(TPMT_HA& accumulator);
        virtual void Execute(class Tpm2& tpm, PolicyTree& p);
        virtual PABase *Clone() {
            return new PolicyCpHash(this->CpHash, this->Tag);
        }
        virtual PolicyCpHash  operator=(const PolicyCpHash& r) {
            return PolicyCpHash(r);
        }
    protected:
        std::vector<BYTE> CpHash;;
};

/// <summary>This command is used to cause conditional gating of a policy based on the
/// contents of the TPMS_TIME_INFO structure.</summary>
class _DLLEXP_ PolicyCounterTimer : public PABase {
        friend class PolicyTree;
    public:
        /// <summary>This command is used to cause conditional gating of a policy based on the
        /// contents of the TPMS_TIME_INFO structure.</summary>
        PolicyCounterTimer(vector<BYTE> _operandB, UINT16 _offset, TPM_EO _operation, string _tag = "") :
            OperandB(_operandB), Offset(_offset), Operation(_operation) {
            Tag = _tag;
        }
        /// <summary>This command is used to cause conditional gating of a policy based on the
        /// contents of the TPMS_TIME_INFO structure.</summary>
        PolicyCounterTimer(UINT64 operandB, UINT16 _offset, TPM_EO _operation, string _tag = "");
        PolicyCounterTimer(const PolicyCounterTimer& r) :
            OperandB(r.OperandB), Offset(r.Offset), Operation(r.Operation) {
            Tag = r.Tag;
        }
        virtual void UpdatePolicyDigest(TPMT_HA& accumulator);
        virtual void Execute(class Tpm2& tpm, PolicyTree& p);
        virtual PABase *Clone() {
            return new PolicyCounterTimer(this->OperandB, this->Offset, this->Operation, this->Tag);
        }
        virtual PolicyCounterTimer  operator=(const PolicyCounterTimer& r) {
            return PolicyCounterTimer(r);
        }
    protected:
        vector<BYTE> OperandB;
        UINT16 Offset;
        TPM_EO Operation;
};

/// <summary>This command allows a policy to be bound to a specific set of handles
/// without being bound to the parameters of the command. This is most useful for
/// commands such as TPM2_Duplicate() and for TPM2_PCR_Event() when the referenced
/// PCR requires a policy.</summary>
class _DLLEXP_ PolicyNameHash : public PABase {
        friend class PolicyTree;
    public:
        /// <summary>This command allows a policy to be bound to a specific set of handles
        /// without being bound to the parameters of the command. This is most
        /// useful for commands such as TPM2_Duplicate() and for TPM2_PCR_Event()
        /// when the referenced PCR requires a policy.</summary>
        PolicyNameHash(vector<BYTE> _nameHash, string _tag = "") : NameHash(_nameHash) {
            Tag = _tag;
        }
        PolicyNameHash(const PolicyNameHash& r): NameHash(r.NameHash) {
            Tag = r.Tag;
        }
        virtual void UpdatePolicyDigest(TPMT_HA& accumulator);
        virtual void Execute(class Tpm2& tpm, PolicyTree& p);
        virtual PABase *Clone() {
            return new PolicyNameHash(this->NameHash, this->Tag);
        }
        virtual PolicyNameHash  operator=(const PolicyNameHash& r) {
            return PolicyNameHash(r);
        }
    protected:
        std::vector<BYTE> NameHash;
};

///<summary>PolicyAuthValue indicates that an auth-value HMAC must be provided during policy use</summary>
class _DLLEXP_ PolicyAuthValue : public PABase {
        friend class PolicyTree;
    public:
        ///<summary>PolicyAuthValue indicates that an auth-value HMAC must be provided during policy use</summary>
        PolicyAuthValue(string _tag = "") {
            Tag = _tag;
        }
        PolicyAuthValue(const PolicyAuthValue& r)  {
            Tag = r.Tag;
        }
        virtual void UpdatePolicyDigest(TPMT_HA& accumulator);
        virtual void Execute(class Tpm2& tpm, PolicyTree& p);
        virtual PABase *Clone() {
            return new PolicyAuthValue(this->Tag);
        }
        virtual PolicyAuthValue  operator=(const PolicyAuthValue& r) {
            return PolicyAuthValue(r);
        }
    protected:
};

/// <summary>This command allows a policy to be bound to the authorization value of the
/// authorized object (PWAP).</summary>
class _DLLEXP_ PolicyPassword : public PABase {
        friend class PolicyTree;
    public:
        /// <summary>This command allows a policy to be bound to the authorization
        /// value of the authorized object (PWAP).</summary>
        PolicyPassword(string _tag = "") {
            Tag = _tag;
        }
        PolicyPassword(const PolicyPassword& r) {
            Tag = r.Tag;
        }
        virtual void UpdatePolicyDigest(TPMT_HA& accumulator);
        virtual void Execute(class Tpm2& tpm, PolicyTree& p);
        virtual PABase *Clone() {
            return new PolicyPassword(this->Tag);
        }
        virtual PolicyPassword  operator=(const PolicyPassword& r) {
            return PolicyPassword(r);
        }
    protected:
};

/// <summary>This command is used to cause conditional gating of a policy based
/// on the contents of an NV Index.</summary>
class _DLLEXP_ PolicyNV : public PABase {
        friend class PolicyTree;
    public:
        /// <summary>This command is used to cause conditional gating of a policy
        /// based on the contents of an NV Index.</summary>
        PolicyNV(TPM_HANDLE& _authorizationHandle,
                 TPM_HANDLE& _nvIndex,
                 vector<BYTE>  _nvIndexName,
                 vector<BYTE>  _operandB,
                 UINT16 _offset,
                 TPM_EO _operation, string _tag = "") :
                 OperandB(_operandB), Offset(_offset), Operation(_operation),
                 AuthorizationHandle(_authorizationHandle), 
                 NvIndex(_nvIndex), NvIndexName(_nvIndexName) {
            Tag = _tag;
        }

        /// <summary>This command is used to cause conditional gating of a policy
        /// based on the contents of an NV Index.</summary>
        PolicyNV(const PolicyNV& r) :
            OperandB(r.OperandB), Offset(r.Offset), Operation(r.Operation),
            AuthorizationHandle(r.AuthorizationHandle), 
            NvIndex(r.NvIndex), NvIndexName(r.NvIndexName) {
            Tag = r.Tag;
            CallbackNeeded = r.CallbackNeeded;
        }

        /// <summary>This command is used to cause conditional gating of a policy
        /// based on the contents of an NV Index.</summary>
        PolicyNV(vector<BYTE> _operandB,
                 vector<BYTE> _nvIndexName,
                 UINT16 _offset,
                 TPM_EO _operation, string _tag = "") : OperandB(_operandB), Offset(_offset), 
                 Operation(_operation), NvIndexName(_nvIndexName) {
            Tag = _tag;
            CallbackNeeded = true;
        }

        virtual void UpdatePolicyDigest(TPMT_HA& accumulator);
        virtual void Execute(class Tpm2& tpm, PolicyTree& p);

        virtual PABase *Clone() {
            PolicyNV *t = new PolicyNV(this->AuthorizationHandle, this->NvIndex, this->NvIndexName,
                                       this->OperandB, this->Offset, this->Operation, this->Tag);
            t->CallbackNeeded = this->CallbackNeeded;
            return t;
        }

        virtual PolicyNV  operator=(const PolicyNV& r) {
            return PolicyNV(r);
        }

    protected:
        // Cecurity-critical parameters
        vector<BYTE> OperandB;
        UINT16 Offset;
        TPM_EO Operation;

        // Dynamic parameters
        TPM_HANDLE AuthorizationHandle;
        TPM_HANDLE NvIndex;
        ByteVec NvIndexName;

        bool CallbackNeeded = false;
};

/// <summary>This command includes an asymmetrically signed authorization in a policy.</summary>
class _DLLEXP_ PolicySigned : public PABase {
        friend class PolicyTree;
    public:
        /// <summary>This command includes an asymmetrically signed authorization in a policy.</summary>
        PolicySigned(bool useNonce,
                     const vector<BYTE>& _cpHashA,
                     const vector<BYTE>& _policyRef,
                     UINT32 _expiration,
                     const TPMT_PUBLIC& _pubKey,
                     string _tag = "") {
            IncludeTpmNonce = useNonce;
            CpHashA = _cpHashA;
            PolicyRef = _policyRef;
            Expiration = _expiration;
            PublicKey = _pubKey;
            Tag = _tag;
        }

        /// <summary>This command includes an asymmetrically signed authorization in a policy.</summary>
        PolicySigned(const PolicySigned& r)  {
            IncludeTpmNonce = r.IncludeTpmNonce;
            CpHashA = r.CpHashA;
            PolicyRef = r.PolicyRef;
            Expiration = r.Expiration;
            PublicKey = r.PublicKey;
            Tag = r.Tag;
            FullKey = r.FullKey;
            CallbackNeeded = r.CallbackNeeded;
        }

        ///<summary>Normally the policy evaluator will "call back" into the calling porgram
        /// to obtain the signature over the nonce (etc.)  However if the key is in a (software)
        /// TSS_KEY then TSS.C++ can do the signature for your without a callback</summary>
        void SetKey(const TSS_KEY& _key) {
            FullKey = _key;
            CallbackNeeded = FALSE;
        }

        virtual void UpdatePolicyDigest(TPMT_HA& accumulator);
        virtual void Execute(class Tpm2& tpm, PolicyTree& p);

        virtual PABase *Clone() {
            return new PolicySigned(*this);
        }

        virtual PolicySigned operator=(const PolicySigned& r) {
            return PolicySigned(r);
        }
    protected:
        // Security-critical parameters
        bool IncludeTpmNonce;
        vector<BYTE> CpHashA;
        vector<BYTE> PolicyRef;
        UINT32 Expiration;
        TPMT_PUBLIC PublicKey;

        // Dynamic parms
        TSS_KEY FullKey;
        bool CallbackNeeded = true;
};

///<summary>PolicyAuthorize transforms a policyHash into a value derived from a public
/// key if the corresponding private key holder has authorized the pre-value with a
/// signature</summary>
class _DLLEXP_ PolicyAuthorize : public PABase {
        friend class PolicyTree;
    public:
        ///<summary>PolicyAuthorize transforms a policyHash into a value derived from a public
        /// key if the corresponding private key holder has authorized the pre-value with a
        /// signature</summary>
        PolicyAuthorize(const vector<BYTE>& _approvedPolicy,
                        const vector<BYTE>& _policyRef,
                        const TPMT_PUBLIC& _authorizingKey,
                        const TPMT_SIGNATURE& _signature,
                        string _tag = "") {
            ApprovedPolicy = _approvedPolicy;
            PolicyRef = _policyRef;
            AuthorizingKey = _authorizingKey;
            Signature = _signature;
            Tag = _tag;
        }

        ///<summary>PolicyAuthorize transforms a policyHash into a value derived from a public
        /// key if the corresponding private key holder has authorized the pre-value with a
        /// signature</summary>
        PolicyAuthorize(const PolicyAuthorize& r)  {
            ApprovedPolicy = r.ApprovedPolicy;
            PolicyRef = r.PolicyRef;
            AuthorizingKey = r.AuthorizingKey;
            Signature = r.Signature;
            Tag = r.Tag;
        }

        virtual void UpdatePolicyDigest(TPMT_HA& accumulator);
        virtual void Execute(class Tpm2& tpm, PolicyTree& p);

        virtual PABase *Clone() {
            return new PolicyAuthorize(*this);
        }

        virtual PolicyAuthorize operator=(const PolicyAuthorize& r) {
            return PolicyAuthorize(r);
        }
    protected:
        // Security-critical parameters
        vector<BYTE> ApprovedPolicy;
        vector<BYTE> PolicyRef;
        TPMT_PUBLIC AuthorizingKey;
        TPMT_SIGNATURE Signature;
};

/// <summary>This command includes a secret-based authorization to a policy.
/// The caller proves knowledge of the secret value using either a password or 
/// an HMAC-based authorization session.</summary>
class _DLLEXP_ PolicySecret : public PABase {
        friend class PolicyTree;
    public:
        /// <summary> This command includes a secret-based authorization to a policy.
        /// The caller proves knowledge of the secret value using either a password or
        /// an HMAC-based authorization session.</summary>
        PolicySecret(bool useNonce,
                     const vector<BYTE>& _cpHashA,
                     const vector<BYTE>& _policyRef,
                     UINT32 _expiration,
                     const vector<BYTE>& _authObjectName,
                     string _tag = "") {
            IncludeTpmNonce = useNonce;
            CpHashA = _cpHashA;
            PolicyRef = _policyRef;
            Expiration = _expiration;
            AuthObjectName = _authObjectName;
            Tag = _tag;
        }

        /// <summary>This command includes a secret-based authorization to a policy.
        /// The caller proves knowledge of the secret value using either a password
        /// or an HMAC-based authorization session.</summary>
        PolicySecret(const PolicySecret& r)  {
            IncludeTpmNonce = r.IncludeTpmNonce;
            CpHashA = r.CpHashA;
            PolicyRef = r.PolicyRef;
            Expiration = r.Expiration;
            AuthObjectName = r.AuthObjectName;
            Tag = r.Tag;
            pHandle = r.pHandle;
            CallbackNeeded = r.CallbackNeeded;
        }

        ///<summary>Normally the policy evaluator will "call back" into the calling program
        /// to obtain the signature over the nonce (etc.)  However if the key is in
        /// a (software) TSS_KEY then TSS.C++ can do the signature for your without
        /// a callback</summary>
        void SetAuthorizingObjectHandle(const TPM_HANDLE& _handle) {
            pHandle = &_handle;
            CallbackNeeded = false;
        }

        virtual void UpdatePolicyDigest(TPMT_HA& accumulator);
        virtual void Execute(class Tpm2& tpm, PolicyTree& p);

        virtual PABase *Clone() {
            return new PolicySecret(*this);
        }

        virtual PolicySecret operator=(const PolicySecret& r) {
            return PolicySecret(r);
        }
    protected:
        // Security-critical parameters
        bool IncludeTpmNonce;
        vector<BYTE> CpHashA;
        vector<BYTE> PolicyRef;
        UINT32 Expiration;
        vector<BYTE> AuthObjectName;

        // Dynamic parms
        const TPM_HANDLE *pHandle = NULL;
        bool CallbackNeeded = true;
};

// TODO: Not tested, potentially incomplete.
/// <summary> This command is similar to TPM2_PolicySigned() except that it takes a
/// ticket instead of a signed authorization. The ticket represents a validated
/// authorization that had an expiration time associated with it. </summary>
class _DLLEXP_ PolicyTicket : public PABase {
        friend class PolicyTree;
    public:
        /// <summary>This command is similar to TPM2_PolicySigned() except that it takes a
        /// ticket instead of a signed authorization. The ticket represents a validated
        /// authorization that had an expiration time associated with it.</summary>
        PolicyTicket(bool useNonce,
                     const vector<BYTE>& _cpHashA,
                     const vector<BYTE>& _policyRef,
                     UINT32 _expiration,
                     const TPMT_PUBLIC& _pubKey,
                     string _tag = "") {
            IncludeTpmNonce = useNonce;
            CpHashA = _cpHashA;
            PolicyRef = _policyRef;
            Expiration = _expiration;
            PublicKey = _pubKey;
            Tag = _tag;
        }

        /// <summary>This command includes an asymmetrically signed authorization in a policy.</summary>
        PolicyTicket(const PolicyTicket& r)  {
            IncludeTpmNonce = r.IncludeTpmNonce;
            CpHashA = r.CpHashA;
            PolicyRef = r.PolicyRef;
            Expiration = r.Expiration;
            PublicKey = r.PublicKey;
            Tag = r.Tag;
            FullKey = r.FullKey;
            CallbackNeeded = r.CallbackNeeded;
        }

        ///<summary>Normally the policy evaluator will "call back" into the calling program
        /// to obtain the signature over the nonce (etc.)  However if the key is in
        /// a (software) TSS_KEY then TSS.C++ can do the signature for your without
        /// a callback</summary>
        void SetKey(const TSS_KEY& _key) {
            FullKey = _key;
            CallbackNeeded = FALSE;
        }

        virtual void UpdatePolicyDigest(TPMT_HA& accumulator);
        virtual void Execute(class Tpm2& tpm, PolicyTree& p);

        virtual PABase *Clone() {
            return new PolicyTicket(*this);
        }

        virtual PolicySigned operator=(const PolicySigned& r) {
            return PolicySigned(r);
        }
    protected:
        // Security-critical parameters
        bool IncludeTpmNonce;
        vector<BYTE> CpHashA;
        vector<BYTE> PolicyRef;
        UINT32 Expiration;
        TPMT_PUBLIC PublicKey;

        // Dynamic parms
        TSS_KEY FullKey;
        bool CallbackNeeded = true;
};

/// <summary>This command allows qualification of duplication to allow duplication to
/// a selected new parent.</summary>
class _DLLEXP_ PolicyDuplicationSelect : public PABase {
        friend class PolicyTree;
    public:
        /// <summary>This command allows qualification of duplication to allow duplication
        /// to a selected new parent.</summary>
        PolicyDuplicationSelect(const vector<BYTE>& _objectName,
                                const vector<BYTE>& _newParentName,
                                bool _includeObject,
                                string _tag = "") :
                                ObjectName(_objectName), 
                                NewParentName(_newParentName), 
                                IncludeObject(_includeObject) {
            Tag = _tag;
        }

        PolicyDuplicationSelect(const PolicyDuplicationSelect& r) :
                                ObjectName(r.ObjectName),
                                NewParentName(r.NewParentName),
                                IncludeObject(r.IncludeObject) {
            Tag = r.Tag;
        }

        virtual void UpdatePolicyDigest(TPMT_HA& accumulator);
        virtual void Execute(class Tpm2& tpm, PolicyTree& p);

        virtual PABase *Clone() {
            return new PolicyDuplicationSelect(this->ObjectName, 
                                               this->NewParentName,
                                               this->IncludeObject,
                                               this->Tag);
        }

        virtual PolicyDuplicationSelect  operator=(const PolicyDuplicationSelect& r) {
            return PolicyDuplicationSelect(r);
        }

    protected:
        std::vector<BYTE> ObjectName;
        std::vector<BYTE> NewParentName;
        bool IncludeObject;
};

_TPMCPP_END
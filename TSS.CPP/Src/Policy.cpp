/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"
#include "Tpm2.h"
#include "MarshallInternal.h"

_TPMCPP_BEGIN

void PolicyTree::SetTree(const vector<PABase *>& _policy)
{
    // Check policy sanity. Assert if the policy is not sound.
    std::vector<string> branchIds;
    map<string, int> _allIds;

    GetBranchIdsInternal(_policy, branchIds, _allIds);

    // Make an internal copy of the policy
    Policy.resize(0);

    for (auto i = _policy.begin(); i != _policy.end(); i++) {
        Policy.push_back((*i)->Clone());
    }

    // If it's a simple chain and we don't have a tag add one.
    PABase *lastOne = Policy[Policy.size() - 1];
    auto lastIsPcr = dynamic_cast<PolicyOr *> (lastOne);

    if ((lastIsPcr == NULL) && (lastOne->Tag == "")) {
        lastOne->Tag = "leaf";
    }

    return;
}

PolicyTree::PolicyTree(const vector<PABase *>& _policy)
{
    SetTree(_policy);
    return;
}

PolicyTree::PolicyTree(const PABase& p0)
{
    if (p0.last == NULL) {
        std::vector<PABase *> p { const_cast<PABase *>(&p0) };
        SetTree(p);
        return;
    }

    // Else we have a list, probably formed through Policy1() << Policy2() << Policy3();
    // We need to reverse the order of the list and turn it into the array-form that the
    // rest of the code understands
    PABase *p = const_cast<PABase *>(&p0);
    vector<PABase *> pol;

    // Find the tail
    do {
        p = p->last;
    } while (p->last != NULL);

    // Make a vec, working from the tail
    while (p != NULL) {
        pol.push_back(p->Clone());
        p = p->next;
    }

    SetTree(pol);

    return;
}

PolicyTree::PolicyTree(const PABase& p0, const PABase& p1)
{
    std::vector<PABase *> p { const_cast<PABase *>(&p0), const_cast<PABase *>(&p1) };
    SetTree(p);
    return;
}

PolicyTree::PolicyTree(const PABase& p0, const PABase& p1, const PABase& p2)
{
    std::vector<PABase *> p { const_cast<PABase *>(&p0), const_cast<PABase *>(&p1), const_cast<PABase *>(&p2) };
    SetTree(p);
    return;
}

PolicyTree::PolicyTree(const PABase& p0, const PABase& p1, const PABase& p2, const PABase& p3)
{
    std::vector<PABase *> p { const_cast<PABase *>(&p0), const_cast<PABase *>(&p1), const_cast<PABase *>(&p2), const_cast<PABase *>(&p3) };
    SetTree(p);
    return;
}

PolicyTree::PolicyTree(const PABase& p0, const PABase& p1, const PABase& p2, const PABase& p3, const PABase& p4)
{
    std::vector<PABase *> p { const_cast<PABase *>(&p0), const_cast<PABase *>(&p1), const_cast<PABase *>(&p2), const_cast<PABase *>(&p3), const_cast<PABase *>(&p4) };
    SetTree(p);
    return;
}

PolicyTree::~PolicyTree()
{
    for (auto i = Policy.begin(); i != Policy.end(); i++) {
        delete (*i);
        *i = NULL;
    }

    Policy.clear();
    return;
}

std::string PolicyTree::GetBranchId(std::vector<PABase *>& chain)
{
    return chain.back()->Tag;
}

bool PolicyTree::ChainContainsBranch(vector<PABase *>& _chain, string branchId)
{
    if (branchId == GetBranchId(_chain)) {
        return true;
    }

    // Get the chainId of the last entry (or descend into an OrBranch, if that is last).
    PABase *lastOne = _chain[_chain.size() - 1];

    if (typeid(lastOne) == typeid(PolicyOr *)) {
        PolicyOr *orNode = dynamic_cast<PolicyOr *>(lastOne);

        for (size_t k = 0; k < orNode->Branches.size(); k++) {
            if (ChainContainsBranch(orNode->Branches[k], branchId)) {
                return true;
            }
        }
    }

    return false;
}

vector<string> PolicyTree::GetBranchIds(std::vector<PABase *>& chain)
{
    vector<string> chainIds;
    map<string, int> allIds;
    GetBranchIdsInternal(chain, chainIds, allIds);
    return chainIds;
}

void PolicyTree::GetBranchIdsInternal(const std::vector<PABase *>& chain,
                                      std::vector<string>& branchIds, 
                                      map<string, int>& _allIds)
{
    // Check chain sanity. Non-empty-string tags should be unique. PolicyOr is only allowed
    // at the end of a chain.
    for (size_t j = 0; j < chain.size(); j++) {
        if ((typeid(chain[j]) == typeid(PolicyOr)) && (j != chain.size() - 1)) {
            throw runtime_error("PolicyOR must be the terminal element it a policy-chain");
        }

        string id = chain[j]->Tag;

        if (id != "") {
            if (_allIds.count(id) != 0) {
                throw runtime_error("Illegal repeated tag in policy expression:" + id);
            }

            _allIds[id] = 1;
        }
    }

    // Get the chainId of the last entry (or descend into an OrBranch, if that is last).
    PolicyOr *lastOne = dynamic_cast<PolicyOr *>(chain[chain.size() - 1]);

    if (lastOne != NULL) {
        PolicyOr *orNode = dynamic_cast<PolicyOr *>(lastOne);

        for (size_t k = 0; k < orNode->Branches.size(); k++) {
            GetBranchIdsInternal(orNode->Branches[k], branchIds, _allIds);
        }
    } else {
        branchIds.push_back(chain[chain.size() - 1]->Tag);
    }
}

TPMT_HA PolicyTree::GetPolicyDigest(TPM_ALG_ID hashAlg)
{
    return GetPolicyDigest(Policy, hashAlg);
}

TPMT_HA PolicyTree::GetPolicyDigest(vector<PABase *>& _chain, TPM_ALG_ID hashAlg)
{
    TPMT_HA policyHash(hashAlg);

    // Work backwards...  Recursion will happen iin PolicyOr.
    for (int j = (int) _chain.size() - 1; j >= 0; j--) {
        _chain[j]->UpdatePolicyDigest(policyHash);
    }

    return policyHash;
}

TPM_RC PolicyTree::Execute(class Tpm2& tpm, AUTH_SESSION& s, string branchId)
{
    Session = &s;

    // Check sanity. An exception will be thrown if the ids are not unique.
    vector<string> branchIds = GetBranchIds(Policy);

    // The branch we are searching for must be non-empty
    if (branchId == "") {
        throw runtime_error("Need a non-empty branchId");
    }

    // Check the branchId exists
    if (std::find(branchIds.begin(), branchIds.end(), branchId) == branchIds.end()) {
        throw runtime_error("branchId not found:" + branchId);
    }

    Execute(tpm, Policy, branchId);
    return TPM_RC::SUCCESS;
}

void PolicyTree::Execute(class Tpm2& tpm, std::vector<PABase *>& chain, string branchId)
{
    // At this point we can guarantee that the branchId exists and is unique. Work back from 
    // the bottom recursively
    for (int j = (int)chain.size() - 1; j >= 0; j--) {
        PABase *node = chain[j];
        // Two cases: if the node is an or-node (which will only be at the end of the array
        // if it exists then descend the or-branch that contains the branchId. Else just
        // ecexute the policy assertion.
        auto orNode = dynamic_cast<PolicyOr *>(node);

        if (orNode != NULL) {
            for (size_t k = 0; k < orNode->Branches.size(); k++) {
                if (ChainContainsBranch(orNode->Branches[k], branchId)) {
                    Execute(tpm, orNode->Branches[k], branchId);
                    continue;
                }
            }
        }
        node->Execute(tpm, *this);
    }

    return;
}

void PABase::PolicyUpdate(TPMT_HA& policyDigest, 
                          TPM_CC commandCode, 
                          std::vector<BYTE> arg2,
                          std::vector<BYTE> arg3)
{
    OutByteBuf b;
    b << ToIntegral(commandCode) << arg2;
    policyDigest.Extend(b.GetBuf());
    policyDigest.Extend(arg3);

    return;
}

//
// PolicyLocality
//
void PolicyLocality::UpdatePolicyDigest(TPMT_HA& accumulator)
{
    OutByteBuf t;
    t << ToIntegral(TPM_CC::PolicyLocality) << ToIntegral(Locality);
    accumulator.Extend(t.GetBuf());
}

void PolicyLocality::Execute(class Tpm2& tpm, PolicyTree& p)
{
    tpm.PolicyLocality(*p.Session, Locality);
}

// 
// PolicyPhysicalPresence
// 
void PolicyPhysicalPresence::UpdatePolicyDigest(TPMT_HA& accumulator)
{
    accumulator.Extend(ToNet(ToIntegral(TPM_CC::PolicyPhysicalPresence)));
}

void PolicyPhysicalPresence::Execute(class Tpm2& tpm, PolicyTree& p)
{
    tpm.PolicyPhysicalPresence(*p.Session);
}

// 
// PolicyOR
// 
void PolicyOr::UpdatePolicyDigest(TPMT_HA& accumulator)
{
    TPM_ALG_ID hashAlg = accumulator.hashAlg;
    OutByteBuf t;
    t << ToIntegral(TPM_CC::PolicyOR);

    for (auto i = Branches.begin(); i != Branches.end(); i++) {
        auto branchDigest = PolicyTree::GetPolicyDigest(*i, hashAlg);
        t << branchDigest.digest;
    }

    accumulator.Reset();
    accumulator.Extend(t.GetBuf());
}

void PolicyOr::Execute(class Tpm2& tpm, PolicyTree& p)
{
    // Calculate the or-chain digests
    TPM_ALG_ID hashAlg = p.Session->GetHashAlg();
    vector<TPM2B_DIGEST> hashList(Branches.size());

    for (size_t j = 0; j < Branches.size(); j++) {
        hashList[j].buffer = PolicyTree::GetPolicyDigest(Branches[j], hashAlg).digest;
    }

    tpm.PolicyOR(*p.Session, hashList);
}

PolicyOr::PolicyOr(std::vector<std::vector<PABase *>> branches, string _tag)
{
    Tag = _tag;
    Init(branches);
}

void PolicyOr::Init(std::vector<std::vector<PABase *>> branches)
{
    Branches.resize(branches.size());

    for (size_t j = 0; j < branches.size(); j++) {
        Branches[j].resize(branches[j].size());

        for (size_t k = 0; k < branches[j].size(); k++) {
            Branches[j][k] = branches[j][k]->Clone();
        }
    }
}

PolicyOr::PolicyOr(std::vector<PABase *> branch1, std::vector<PABase *> branch2, string _tag)
{
    Tag = _tag;
    std::vector<std::vector<PABase *>> branches { branch1, branch2 };
    Init(branches);
}

PolicyOr::PolicyOr(const PolicyOr& r)
{
    Tag = r.Tag;
    Branches.resize(r.Branches.size());

    for (size_t j = 0; j < r.Branches.size(); j++) {
        Branches[j].resize(r.Branches[j].size());

        for (size_t k = 0; k < r.Branches[j].size(); k++) {
            Branches[j][k] = r.Branches[j][k]->Clone();
        }
    }
}

PABase *PolicyOr::Clone()
{
    PolicyOr *p = new PolicyOr(*this);
    return p;
}

PolicyOr::~PolicyOr()
{
    for (size_t j = 0; j < Branches.size(); j++) {
        for (size_t k = 0; k < Branches[j].size(); k++) {
            delete Branches[j][k];
            Branches[j][k] = NULL;
        }

        Branches[j].resize(0);
    }

    Branches.resize(0);
}

// 
// PolicyPcr
// 
void PolicyPcr::UpdatePolicyDigest(TPMT_HA& accumulator)
{
    TPM_ALG_ID hashAlg = accumulator.hashAlg;

    // We need the hash of the selected PCR values.
    auto pcrValueHash = GetPcrValueDigest(hashAlg);

    // Next fold in the selection array to form the policy-hash update
    OutByteBuf t;
    t << ToIntegral(TPM_CC::PolicyPCR);
    t << (UINT32) Pcrs.size();

    for (auto i = Pcrs.begin(); i != Pcrs.end(); i++) {
        t << *i;
    }

    t << pcrValueHash;
    accumulator.Extend(t.GetBuf());
}

void PolicyPcr::Execute(class Tpm2& tpm, PolicyTree& p)
{
    tpm.PolicyPCR(*p.Session, GetPcrValueDigest(p.Session->GetHashAlg()), Pcrs);
}

std::vector<BYTE> PolicyPcr::GetPcrValueDigest(TPM_ALG_ID hashAlg)
{
    // Note: we assume that these have been presented in the same order as the selection array
    OutByteBuf pcrVals;

    // Then the concatenated values
    for (auto i = PcrValues.begin(); i != PcrValues.end(); i++) {
        pcrVals << i->buffer;
    }

    auto pcrValueHash = CryptoServices::Hash(hashAlg, pcrVals.GetBuf());
    return pcrValueHash;
}

// 
// PolicyCommandCode
// 
void PolicyCommandCode::UpdatePolicyDigest(TPMT_HA& accumulator)
{
    OutByteBuf t;
    t << ToIntegral(TPM_CC::PolicyCommandCode) << ToIntegral(CommandCode);
    accumulator.Extend(t.GetBuf());
}

void PolicyCommandCode::Execute(class Tpm2& tpm, PolicyTree& p)
{
    tpm.PolicyCommandCode(*p.Session, CommandCode);
}

// 
// PolicyCpHash
// 
void PolicyCpHash::UpdatePolicyDigest(TPMT_HA& accumulator)
{
    OutByteBuf t;
    t << ToIntegral(TPM_CC::PolicyCpHash) << CpHash;
    accumulator.Extend(t.GetBuf());
}

void PolicyCpHash::Execute(class Tpm2& tpm, PolicyTree& p)
{
    tpm.PolicyCpHash(*p.Session, CpHash);
}

// 
// PolicyCounterTimer
// 
PolicyCounterTimer::PolicyCounterTimer(UINT64 _operandB, 
                                       UINT16 _offset,
                                       TPM_EO _operation,
                                       string _tag)
{
    OperandB = ValueTypeToByteArray(_operandB);
    Offset = _offset;
    Operation = (_operation);
    Tag = _tag;
}

void PolicyCounterTimer::UpdatePolicyDigest(TPMT_HA& accumulator)
{
    OutByteBuf argsBuf;
    argsBuf << OperandB << Offset << ToIntegral(Operation);
    std::vector<BYTE> args = CryptoServices::Hash(accumulator.hashAlg, argsBuf.GetBuf());
    OutByteBuf t;
    t << ToIntegral(TPM_CC::PolicyCounterTimer) << args;
    accumulator.Extend(t.GetBuf());
}

void PolicyCounterTimer::Execute(class Tpm2& tpm, PolicyTree& p)
{
    tpm.PolicyCounterTimer(*p.Session, OperandB, Offset, Operation);
}

// 
// PolicyNameHash
// 
void PolicyNameHash::UpdatePolicyDigest(TPMT_HA& accumulator)
{
    OutByteBuf t;
    t << ToIntegral(TPM_CC::PolicyNameHash) << NameHash;
    accumulator.Extend(t.GetBuf());
}

void PolicyNameHash::Execute(class Tpm2& tpm, PolicyTree& p)
{
    tpm.PolicyNameHash(*p.Session, NameHash);
}

// 
// PolicyAuthValue
// 
void PolicyAuthValue::UpdatePolicyDigest(TPMT_HA& accumulator)
{
    OutByteBuf t;
    t << ToIntegral(TPM_CC::PolicyAuthValue);
    accumulator.Extend(t.GetBuf());
}

void PolicyAuthValue::Execute(class Tpm2& tpm, PolicyTree& p)
{
    p.Session->ForceHmac();
    p.Session->SetSessionIncludesAuth();
    tpm.PolicyAuthValue(*p.Session);
}

// 
// PolicyPassword
// 
void PolicyPassword::UpdatePolicyDigest(TPMT_HA& accumulator)
{
    OutByteBuf t;
    t << ToIntegral(TPM_CC::PolicyAuthValue);
    accumulator.Extend(t.GetBuf());
}

void PolicyPassword::Execute(class Tpm2& tpm, PolicyTree& p)
{
    p.Session->IncludePlaintextPassword();
    tpm.PolicyPassword(*p.Session);
}

// 
// PolicyNV
// 
void PolicyNV::UpdatePolicyDigest(TPMT_HA& accumulator)
{
    OutByteBuf argsBuf;
    argsBuf << OperandB << Offset << ToIntegral(Operation);
    std::vector<BYTE> args = CryptoServices::Hash(accumulator.hashAlg, argsBuf.GetBuf());
    OutByteBuf t;
    t << ToIntegral(TPM_CC::PolicyNV) << args << NvIndexName;
    accumulator.Extend(t.GetBuf());
}

void PolicyNV::Execute(class Tpm2& tpm, PolicyTree& p)
{
    if (CallbackNeeded) {
        // Get the extra NV-data
        PolicyNVCallbackData d = (*p.theNvCallback)(Tag);
        AuthorizationHandle = d.AuthorizationHandle;
        NvIndex = d.NvIndex;
    }

    tpm.PolicyNV(AuthorizationHandle, NvIndex, *p.Session, OperandB, Offset, Operation);
}

// 
// PolicySigned
// 
void PolicySigned::UpdatePolicyDigest(TPMT_HA& accumulator)
{
    PolicyUpdate(accumulator, TPM_CC::PolicySigned, PublicKey.GetName(), PolicyRef);
    return;
}

void PolicySigned::Execute(class Tpm2& tpm, PolicyTree& p)
{
    SignResponse sig;
    ByteVec nonceTpm;

    if (IncludeTpmNonce) {
        nonceTpm = p.Session->GetNonceTpm();
    }

    if (CallbackNeeded) {
        // Get the sig from a remote entity
        sig = (*(p.theSignCallback))(nonceTpm, Expiration, CpHashA, PolicyRef, Tag);
    } else { 
        // If we have a TSS_KEY, TSS.C++ can do the sig for us.
        OutByteBuf toSign;
        toSign << nonceTpm << Expiration << CpHashA << PolicyRef;
        TPMS_RSA_PARMS  *parms = dynamic_cast < TPMS_RSA_PARMS *> (PublicKey.parameters);

        if (parms == NULL) {
            throw domain_error("Not supported");
        }

        TPMS_SCHEME_RSASSA *scheme = dynamic_cast<TPMS_SCHEME_RSASSA *>(parms->scheme);

        if (scheme == NULL) {
            throw domain_error("Not supported");
        }

        TPM_ALG_ID hashAlg = scheme->hashAlg;
        auto hashToSign = TPMT_HA::FromHashOfData(hashAlg, toSign.GetBuf());
        sig = FullKey.Sign(hashToSign.digest, TPMS_NULL_SIG_SCHEME());
    }

    TPM_HANDLE pubKeyH = tpm.LoadExternal(TPMT_SENSITIVE::NullObject(), PublicKey,
                                          TPM_HANDLE::FromReservedHandle(TPM_RH::OWNER));

    tpm.PolicySigned(pubKeyH, *(p.Session), nonceTpm, CpHashA,
                     PolicyRef, Expiration, *sig.signature);

    tpm.FlushContext(pubKeyH);

    return;
}

// 
// PolicySigned
// 
void PolicyAuthorize::UpdatePolicyDigest(TPMT_HA& accumulator)
{
    accumulator.Reset();
    PolicyUpdate(accumulator, TPM_CC::PolicyAuthorize, AuthorizingKey.GetName(), PolicyRef);
    return;
}

void PolicyAuthorize::Execute(class Tpm2& tpm, PolicyTree& p)
{
    // This is what the signature should be over
    auto aHash = TPMT_HA::FromHashOfData(p.Session->GetHashAlg(),
                                         Helpers::Concatenate(ApprovedPolicy, PolicyRef));

    // Load the public key to get a sig verification ticket
    TPM_HANDLE verifierHandle = tpm.LoadExternal(TPMT_SENSITIVE::NullObject(), AuthorizingKey,
                                                 TPM_HANDLE::FromReservedHandle(TPM_RH::OWNER));

    // Verify the sig and get the ticket
    VerifySignatureResponse ticket;
    ticket = tpm._AllowErrors().VerifySignature(verifierHandle, aHash.digest, *Signature.signature);

    TPM_RC responseCode = tpm._GetLastError();
    if (responseCode != TPM_RC::SUCCESS) {
        tpm.FlushContext(verifierHandle);
        throw new runtime_error("Policy signature verification failed");
    }

    tpm._AllowErrors().PolicyAuthorize(*p.Session, ApprovedPolicy, PolicyRef, 
                                       AuthorizingKey.GetName(), ticket.validation);

    if (responseCode != TPM_RC::SUCCESS) {
        tpm.FlushContext(verifierHandle);
        throw new runtime_error("PolicyAuthorize failed");
    }

    tpm.FlushContext(verifierHandle);
    return;
}

// 
// PolicySigned
// 
void PolicySecret::UpdatePolicyDigest(TPMT_HA& accumulator)
{
    PolicyUpdate(accumulator, TPM_CC::PolicySecret, AuthObjectName, PolicyRef);
    return;
}

void PolicySecret::Execute(class Tpm2& tpm, PolicyTree& p)
{
    SignResponse sig;
    ByteVec nonceTpm;

    if (IncludeTpmNonce) {
        nonceTpm = p.Session->GetNonceTpm();
    }

    if (CallbackNeeded) {
        // TODO: Get the object handle
        _ASSERT(FALSE);
    }

    tpm.PolicySecret(*pHandle, *(p.Session), nonceTpm, CpHashA, PolicyRef, Expiration);

    return;
}

// 
// PolicySigned
// 
void PolicyDuplicationSelect::UpdatePolicyDigest(TPMT_HA& accumulator)
{
    ByteVec objName;
    BYTE inc = 0;
    auto nameNash = CryptoServices::Hash(accumulator.hashAlg,
                                         Helpers::Concatenate(ObjectName, NewParentName));
    if (IncludeObject) {
        objName = ObjectName;
        inc = 1;
    }

    OutByteBuf buf;
    buf << ToIntegral(TPM_CC::PolicyDuplicationSelect) << objName << NewParentName << inc;
    accumulator.Extend(buf.GetBuf());

    return;
}

void PolicyDuplicationSelect::Execute(class Tpm2& tpm, PolicyTree& p)
{
    BYTE inc = (BYTE)IncludeObject;
    tpm.PolicyDuplicationSelect(*p.Session, ObjectName, NewParentName, inc);

    return;
}

// 
// PolicySigned
// 
void PolicyTicket::UpdatePolicyDigest(TPMT_HA& accumulator)
{
    _ASSERT(FALSE);
    return;
}

void PolicyTicket::Execute(class Tpm2& tpm, PolicyTree& p)
{
    _ASSERT(FALSE);
}

_TPMCPP_END
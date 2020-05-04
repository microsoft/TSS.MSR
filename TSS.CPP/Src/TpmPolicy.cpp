/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "MarshalInternal.h"

_TPMCPP_BEGIN

using namespace std;

void PolicyTree::SetTree(const vector<PABase*>& policyBranch)
{
    // Check policy sanity. Assert if the policy is not sound.
    vector<string> branchIds;
    map<string, int> _allIds;

    GetBranchIdsInternal(policyBranch, branchIds, _allIds);

    // Make an internal copy of the policy
    Policy.resize(0);

    for (auto i = policyBranch.begin(); i != policyBranch.end(); i++)
        Policy.push_back((*i)->Clone());

    // If it's a simple chain and we don't have a tag add one.
    PABase *lastOne = Policy[Policy.size() - 1];
    auto lastIsPcr = dynamic_cast<PolicyOr*>(lastOne);

    if ((lastIsPcr == NULL) && (lastOne->Tag == ""))
        lastOne->Tag = "leaf";
}

PolicyTree::PolicyTree(const PABase& p0)
{
    if (!p0.last)
    {
        vector<PABase*> p { const_cast<PABase*>(&p0) };
        SetTree(p);
        return;
    }

    // Else we have a list, probably formed through Policy1() << Policy2() << Policy3();
    // We need to reverse the order of the list and turn it into the array-form that the
    // rest of the code understands
    PABase *p = const_cast<PABase*>(&p0);
    vector<PABase*>pol;

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
    vector<PABase*>p { const_cast<PABase*>(&p0), const_cast<PABase*>(&p1) };
    SetTree(p);
    return;
}

PolicyTree::PolicyTree(const PABase& p0, const PABase& p1, const PABase& p2)
{
    vector<PABase*>p { const_cast<PABase*>(&p0), const_cast<PABase*>(&p1), const_cast<PABase*>(&p2) };
    SetTree(p);
    return;
}

PolicyTree::PolicyTree(const PABase& p0, const PABase& p1, const PABase& p2, const PABase& p3)
{
    vector<PABase*>p { const_cast<PABase*>(&p0), const_cast<PABase*>(&p1), const_cast<PABase*>(&p2), const_cast<PABase*>(&p3) };
    SetTree(p);
    return;
}

PolicyTree::PolicyTree(const PABase& p0, const PABase& p1, const PABase& p2, const PABase& p3, const PABase& p4)
{
    vector<PABase*>p { const_cast<PABase*>(&p0), const_cast<PABase*>(&p1), const_cast<PABase*>(&p2), const_cast<PABase*>(&p3), const_cast<PABase*>(&p4) };
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

string PolicyTree::GetBranchId(const vector<PABase *>& chain)
{
    return chain.back()->Tag;
}

bool PolicyTree::ChainContainsBranch(const vector<PABase *>& chain, const string& branchId)
{
    if (branchId == GetBranchId(chain))
        return true;

    // Get the chainId of the last entry (or descend into an OrBranch, if that is last).
    PABase *lastOne = chain[chain.size() - 1];

    if (typeid(lastOne) == typeid(PolicyOr*))
    {
        PolicyOr *orNode = dynamic_cast<PolicyOr*>(lastOne);
        for (size_t k = 0; k < orNode->Branches.size(); k++)
            if (ChainContainsBranch(orNode->Branches[k], branchId))
                return true;
    }
    return false;
}

vector<string> PolicyTree::GetBranchIds(const vector<PABase*>& chain)
{
    vector<string> chainIds;
    map<string, int> allIds;
    GetBranchIdsInternal(chain, chainIds, allIds);
    return chainIds;
}

void PolicyTree::GetBranchIdsInternal(const vector<PABase*>& chain,
                                      vector<string>& branchIds, 
                                      map<string, int>& allIds)
{
    // Check chain sanity. Non-empty-string tags should be unique. PolicyOr is only allowed
    // at the end of a chain.
    for (size_t j = 0; j < chain.size(); j++)
    {
        if (typeid(chain[j]) == typeid(PolicyOr) && j != chain.size() - 1)
            throw runtime_error("PolicyOR must be the terminal element it a policy-chain");

        string id = chain[j]->Tag;

        if (id != "") {
            if (allIds.count(id) != 0)
                throw runtime_error("Illegal repeated tag in policy expression:" + id);
            allIds[id] = 1;
        }
    }

    // Get the chainId of the last entry (or descend into an OrBranch, if that is last).
    PolicyOr *lastOne = dynamic_cast<PolicyOr*>(chain[chain.size() - 1]);

    if (lastOne != NULL)
    {
        PolicyOr *orNode = dynamic_cast<PolicyOr*>(lastOne);
        for (size_t k = 0; k < orNode->Branches.size(); k++)
            GetBranchIdsInternal(orNode->Branches[k], branchIds, allIds);
    }
    else
        branchIds.push_back(chain.back()->Tag);
}

TPM_HASH PolicyTree::GetPolicyDigest(TPM_ALG_ID hashAlg) const
{
    return GetPolicyDigest(Policy, hashAlg);
}

TPM_HASH PolicyTree::GetPolicyDigest(const vector<PABase*>& chain, TPM_ALG_ID hashAlg)
{
    TPM_HASH policyHash(hashAlg);

    // Work backwards...  Recursion will happen in PolicyOr.
    for (int j = (int)chain.size() - 1; j >= 0; j--)
        chain[j]->UpdatePolicyDigest(policyHash);
    return policyHash;
}

TPM_RC PolicyTree::Execute(Tpm2& tpm, AUTH_SESSION& s, string branchId)
{
    Session = &s;

    // Check sanity. An exception will be thrown if the ids are not unique.
    vector<string> branchIds = GetBranchIds(Policy);

    // The branch we are searching for must be non-empty
    if (branchId == "") {
        throw runtime_error("Need a non-empty branchId");
    }

    // Check the branchId exists
    if (find(branchIds.begin(), branchIds.end(), branchId) == branchIds.end()) {
        throw runtime_error("branchId not found:" + branchId);
    }

    Execute(tpm, Policy, branchId);
    return TPM_RC::SUCCESS;
}

void PolicyTree::Execute(Tpm2& tpm, vector<PABase*>& chain, const string& branchId)
{
    // At this point we can guarantee that the branchId exists and is unique. Work back from 
    // the bottom recursively
    for (int j = (int)chain.size() - 1; j >= 0; j--) {
        PABase *node = chain[j];
        // Two cases: if the node is an or-node (which will only be at the end of the array
        // if it exists then descend the or-branch that contains the branchId. Else just
        // ecexute the policy assertion.
        auto orNode = dynamic_cast<PolicyOr*>(node);
        if (orNode)
        {
            for (size_t k = 0; k < orNode->Branches.size(); k++)
            {
                if (ChainContainsBranch(orNode->Branches[k], branchId))
                    Execute(tpm, orNode->Branches[k], branchId);
            }
        }
        node->Execute(tpm, *this);
    }
}

void PABase::PolicyUpdate(TPM_HASH& policyDigest, TPM_CC commandCode, 
                          const ByteVec& arg2, const ByteVec& arg3)
{
    OutByteBuf b;
    b << commandCode << arg2;
    policyDigest.Extend(b.GetBuf());
    policyDigest.Extend(arg3);
}

//
// PolicyLocality
//
void PolicyLocality::UpdatePolicyDigest(TPM_HASH& accumulator) const
{
    OutByteBuf t;
    t << TPM_CC::Value(TPM_CC::PolicyLocality) << Locality;
    accumulator.Extend(t.GetBuf());
}

void PolicyLocality::Execute(Tpm2& tpm, PolicyTree& p)
{
    tpm.PolicyLocality(*p.Session, Locality);
}

// 
// PolicyPhysicalPresence
// 
void PolicyPhysicalPresence::UpdatePolicyDigest(TPM_HASH& accumulator) const
{
    accumulator.Extend(ToNet(TPM_CC::Value(TPM_CC::PolicyPhysicalPresence)));
}

void PolicyPhysicalPresence::Execute(Tpm2& tpm, PolicyTree& p)
{
    tpm.PolicyPhysicalPresence(*p.Session);
}

// 
// PolicyOR
// 
void PolicyOr::UpdatePolicyDigest(TPM_HASH& accumulator) const
{
    TPM_ALG_ID hashAlg = accumulator;
    OutByteBuf t;
    t << TPM_CC::Value(TPM_CC::PolicyOR);

    for (auto i = Branches.begin(); i != Branches.end(); i++)
        t << PolicyTree::GetPolicyDigest(*i, hashAlg).digest;

    accumulator.Reset();
    accumulator.Extend(t.GetBuf());
}

void PolicyOr::Execute(Tpm2& tpm, PolicyTree& p)
{
    // Calculate the or-chain digests
    TPM_ALG_ID hashAlg = p.Session->GetHashAlg();
    vector<TPM2B_DIGEST> hashList(Branches.size());

    for (size_t j = 0; j < Branches.size(); j++)
        hashList[j].buffer = PolicyTree::GetPolicyDigest(Branches[j], hashAlg);

    tpm.PolicyOR(*p.Session, hashList);
}

void PolicyOr::Init(const vector<vector<PABase*>>& branches)
{
    Branches.resize(branches.size());
    for (size_t j = 0; j < branches.size(); j++)
    {
        Branches[j].resize(branches[j].size());
        for (size_t k = 0; k < branches[j].size(); k++)
            Branches[j][k] = branches[j][k]->Clone();
    }
}

PolicyOr::~PolicyOr()
{
    for (size_t j = 0; j < Branches.size(); j++)
        for (size_t k = 0; k < Branches[j].size(); k++)
            delete Branches[j][k];
}

// 
// PolicyPcr
// 
void PolicyPcr::UpdatePolicyDigest(TPM_HASH& accumulator) const
{
    TPM_ALG_ID hashAlg = accumulator;

    // We need the hash of the selected PCR values.
    auto pcrValueHash = GetPcrValueDigest(hashAlg);

    // Next fold in the selection array to form the policy-hash update
    OutByteBuf t;
    t << TPM_CC::Value(TPM_CC::PolicyPCR);
    t << (UINT32) Pcrs.size();

    for (auto i = Pcrs.begin(); i != Pcrs.end(); i++) {
        t << *i;
    }

    t << pcrValueHash;
    accumulator.Extend(t.GetBuf());
}

void PolicyPcr::Execute(Tpm2& tpm, PolicyTree& p)
{
    tpm.PolicyPCR(*p.Session, GetPcrValueDigest(p.Session->GetHashAlg()), Pcrs);
}

ByteVec PolicyPcr::GetPcrValueDigest(TPM_ALG_ID hashAlg) const
{
    // Note: we assume that these have been presented in the same order as the selection array
    OutByteBuf pcrVals;

    // Then the concatenated values
    for (auto i = PcrValues.begin(); i != PcrValues.end(); i++)
        pcrVals << i->buffer;

    return Crypto::Hash(hashAlg, pcrVals.GetBuf());
}

// 
// PolicyCommandCode
// 
void PolicyCommandCode::UpdatePolicyDigest(TPM_HASH& accumulator) const
{
    OutByteBuf t;
    t << TPM_CC::Value(TPM_CC::PolicyCommandCode) << CommandCode;
    accumulator.Extend(t.GetBuf());
}

void PolicyCommandCode::Execute(Tpm2& tpm, PolicyTree& p)
{
    tpm.PolicyCommandCode(*p.Session, CommandCode);
}

// 
// PolicyCpHash
// 
void PolicyCpHash::UpdatePolicyDigest(TPM_HASH& accumulator) const
{
    OutByteBuf t;
    t << TPM_CC::Value(TPM_CC::PolicyCpHash) << CpHash;
    accumulator.Extend(t.GetBuf());
}

void PolicyCpHash::Execute(Tpm2& tpm, PolicyTree& p)
{
    tpm.PolicyCpHash(*p.Session, CpHash);
}

// 
// PolicyCounterTimer
// 
PolicyCounterTimer::PolicyCounterTimer(UINT64 operandB, UINT16 offset, TPM_EO operation, const string& tag)
    : PABase(tag), OperandB(ValueTypeToByteArray(operandB)), Offset(offset), Operation(operation)
{}

void PolicyCounterTimer::UpdatePolicyDigest(TPM_HASH& accumulator) const
{
    OutByteBuf argsBuf;
    argsBuf << OperandB << Offset << Operation;
    ByteVec args = Crypto::Hash(accumulator, argsBuf.GetBuf());
    OutByteBuf t;
    t << TPM_CC::Value(TPM_CC::PolicyCounterTimer) << args;
    accumulator.Extend(t.GetBuf());
}

void PolicyCounterTimer::Execute(Tpm2& tpm, PolicyTree& p)
{
    tpm.PolicyCounterTimer(*p.Session, OperandB, Offset, Operation);
}

// 
// PolicyNameHash
// 
void PolicyNameHash::UpdatePolicyDigest(TPM_HASH& accumulator) const
{
    OutByteBuf t;
    t << TPM_CC::Value(TPM_CC::PolicyNameHash) << NameHash;
    accumulator.Extend(t.GetBuf());
}

void PolicyNameHash::Execute(Tpm2& tpm, PolicyTree& p)
{
    tpm.PolicyNameHash(*p.Session, NameHash);
}

// 
// PolicyAuthValue
// 
void PolicyAuthValue::UpdatePolicyDigest(TPM_HASH& accumulator) const
{
    OutByteBuf t;
    t << TPM_CC::Value(TPM_CC::PolicyAuthValue);
    accumulator.Extend(t.GetBuf());
}

void PolicyAuthValue::Execute(Tpm2& tpm, PolicyTree& p)
{
    p.Session->ForceHmac();
    p.Session->SetSessionIncludesAuth();
    tpm.PolicyAuthValue(*p.Session);
}

// 
// PolicyPassword
// 
void PolicyPassword::UpdatePolicyDigest(TPM_HASH& accumulator) const
{
    OutByteBuf t;
    t << TPM_CC::Value(TPM_CC::PolicyAuthValue);
    accumulator.Extend(t.GetBuf());
}

void PolicyPassword::Execute(Tpm2& tpm, PolicyTree& p)
{
    p.Session->IncludePlaintextPassword();
    tpm.PolicyPassword(*p.Session);
}

// 
// PolicyNV
// 
void PolicyNV::UpdatePolicyDigest(TPM_HASH& accumulator) const
{
    OutByteBuf argsBuf;
    argsBuf << OperandB << Offset << Operation;
    ByteVec args = Crypto::Hash(accumulator, argsBuf.GetBuf());
    OutByteBuf t;
    t << TPM_CC::Value(TPM_CC::PolicyNV) << args << NvIndexName;
    accumulator.Extend(t.GetBuf());
}

void PolicyNV::Execute(Tpm2& tpm, PolicyTree& p)
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
void PolicySigned::UpdatePolicyDigest(TPM_HASH& accumulator) const
{
    PolicyUpdate(accumulator, TPM_CC::PolicySigned, PublicKey.GetName(), PolicyRef);
    return;
}

void PolicySigned::Execute(Tpm2& tpm, PolicyTree& p)
{
    SignResponse sig;
    ByteVec nonceTpm;

    if (IncludeTpmNonce)
        nonceTpm = p.Session->GetNonceTpm();

    if (CallbackNeeded) {
        // Get the sig from a remote entity
        sig = (*(p.theSignCallback))(nonceTpm, Expiration, CpHashA, PolicyRef, Tag);
    }
    else { 
        // If we have a TSS_KEY, TSS.C++ can do the sig for us.
        OutByteBuf toSign;
        toSign << nonceTpm << Expiration << CpHashA << PolicyRef;

        TPMS_RSA_PARMS  *parms = dynamic_cast < TPMS_RSA_PARMS*>(&*PublicKey.parameters);
        if (parms == NULL)
            throw domain_error("Not supported");

        TPMS_SCHEME_RSASSA *scheme = dynamic_cast<TPMS_SCHEME_RSASSA*>(&*parms->scheme);
        if (scheme == NULL)
            throw domain_error("Not supported");

        auto hashToSign = TPM_HASH::FromHashOfData(scheme->hashAlg, toSign.GetBuf());
        sig = FullKey.Sign(hashToSign, TPMS_NULL_SIG_SCHEME());
    }

    TPM_HANDLE pubKeyH = tpm.LoadExternal(
#if NEW_MARSHAL
                                          TPMT_SENSITIVE(),
#else
                                          TPMT_SENSITIVE::NullObject(),
#endif
                                          PublicKey,
                                          TPM_RH::OWNER);

    tpm.PolicySigned(pubKeyH, *(p.Session), nonceTpm, CpHashA,
                     PolicyRef, Expiration, *sig.signature);

    tpm.FlushContext(pubKeyH);
}

// 
// PolicyAuthorize
// 
void PolicyAuthorize::UpdatePolicyDigest(TPM_HASH& accumulator) const
{
    accumulator.Reset();
    PolicyUpdate(accumulator, TPM_CC::PolicyAuthorize, AuthorizingKey.GetName(), PolicyRef);
    return;
}

void PolicyAuthorize::Execute(Tpm2& tpm, PolicyTree& p)
{
    // This is what the signature should be over
    auto aHash = TPM_HASH::FromHashOfData(p.Session->GetHashAlg(),
                                         Helpers::Concatenate(ApprovedPolicy, PolicyRef));

    // Load the public key to get a sig verification ticket
    TPM_HANDLE verifierHandle = tpm.LoadExternal(
#if NEW_MARSHAL
                                                 TPMT_SENSITIVE(),
#else
                                                 TPMT_SENSITIVE::NullObject(),
#endif
                                                 AuthorizingKey,
                                                 TPM_RH::OWNER);

    // Verify the sig and get the ticket
    TPMT_TK_VERIFIED ticket = tpm._AllowErrors()
                                 .VerifySignature(verifierHandle, aHash, *Signature.signature);

    TPM_RC responseCode = tpm._GetLastError();
    if (responseCode != TPM_RC::SUCCESS)
    {
        tpm.FlushContext(verifierHandle);
        throw new runtime_error("Policy signature verification failed");
    }

    tpm._AllowErrors().PolicyAuthorize(*p.Session, ApprovedPolicy, PolicyRef, 
                                       AuthorizingKey.GetName(), ticket);
    tpm.FlushContext(verifierHandle);
    if (responseCode != TPM_RC::SUCCESS)
        throw new runtime_error("PolicyAuthorize failed");
}

// 
// PolicySecret
// 
void PolicySecret::Execute(Tpm2& tpm, PolicyTree& p)
{
    SignResponse sig;
    ByteVec nonceTpm;

    if (IncludeTpmNonce)
        nonceTpm = p.Session->GetNonceTpm();

    if (CallbackNeeded) {
        // TODO: Get the object handle
        _ASSERT(FALSE);
    }

    tpm.PolicySecret(*pHandle, *(p.Session), nonceTpm, CpHashA, PolicyRef, Expiration);
}

// 
// PolicyDuplicationSelect
// 
void PolicyDuplicationSelect::UpdatePolicyDigest(TPM_HASH& accumulator) const
{
    ByteVec objName;
    BYTE inc = 0;
    auto nameNash = Crypto::Hash(accumulator, Helpers::Concatenate(ObjectName, NewParentName));
    if (IncludeObject)
    {
        objName = ObjectName;
        inc = 1;
    }

    OutByteBuf buf;
    buf << TPM_CC::Value(TPM_CC::PolicyDuplicationSelect) << objName << NewParentName << inc;
    accumulator.Extend(buf.GetBuf());
}

void PolicyDuplicationSelect::Execute(Tpm2& tpm, PolicyTree& p)
{
    BYTE inc = (BYTE)IncludeObject;
    tpm.PolicyDuplicationSelect(*p.Session, ObjectName, NewParentName, inc);
}

// 
// PolicyTicket
// 
void PolicyTicket::UpdatePolicyDigest(TPM_HASH& accumulator) const
{
    _ASSERT(FALSE);
    return;
}

void PolicyTicket::Execute(Tpm2&, PolicyTree&)
{
    _ASSERT(FALSE);
}

_TPMCPP_END
/*++

Copyright (c) 2010-2015 Microsoft Corporation
Microsoft Confidential

*/

/*
 * This file contains classes that support the dozen-or-so TPM policy commands.  
 * The companion file Policy.cs contains the remainder of the Tpm2Lib support library
 * 
 * TODO: Originally TPM-policies were expressed and maintained as doubly-linked 
 * lists made into trees by arrays of sub-tree-roots in PolicyOR nodes.
 * 
 * This proved to be awkward to express (and to serialize) so we now favor an 
 * array representation.  Right now the library supports both, but we should remove
 * the list-representation. This will mean that legacy tests must be re-written.
 * 
 * Additionally, we should remove the branchId as an attribute of all elements and
 * replace with the specific Branch-ID pseudo-ACE
 * 
 * */

namespace Tpm2Lib
{
    using System;
    using System.IO;
    using System.Diagnostics;
    using System.Collections.Generic;
    using System.Xml.Serialization;
    using System.Xml.Schema;
    using System.Xml;
    using System.Runtime.Serialization;

    /// <summary>
    /// PolicyAce is the abstract base-class for the TPM policy operators
    /// </summary>
    [KnownType(typeof(TpmPolicyOr))]
    [KnownType(typeof(TpmPolicyCommand))]
    [KnownType(typeof(TpmPolicyNV))]
    [KnownType(typeof(TpmPolicyLocality))]
    [KnownType(typeof(TpmPolicyPassword))]
    [KnownType(typeof(TpmPolicyChainId))]
    [KnownType(typeof(TpmPolicyAction))]
    [KnownType(typeof(TpmPolicyPcr))]
    [KnownType(typeof(TpmPolicySigned))]
    [KnownType(typeof(TpmPolicyPhysicalPresence))]
    [KnownType(typeof(TpmPolicyCounterTimer))]
    [KnownType(typeof(TpmPolicyCpHash))]
    [KnownType(typeof(TpmPolicyNameHash))]
    [KnownType(typeof(TpmPolicyAuthValue))]
    [KnownType(typeof(TpmPolicyTicket))]
    [KnownType(typeof(TpmPolicyAuthorize))]
    [KnownType(typeof(TpmPolicySecret))]
    [KnownType(typeof(TpmPolicyDuplicationSelect))]
    [KnownType(typeof(TpmPolicyNvWritten))]
    [DataContract]
    public abstract class PolicyAce
    {
        protected PolicyAce(string branchName)
        {
            BranchIdentifier = branchName;
        }

        public PolicyAce AddNextAce(PolicyAce nextAce)
        {
            if (this is TpmPolicyOr)
            {
                Globs.Throw<ArgumentException>("AddNextAce: Do not call AddNextAce for an OR node: Use AddPolicyBranch instead.");
            }
            if (NextAce != null)
            {
                Globs.Throw<ArgumentException>("AddNextAce: Policy ACE already has a child");
            }
            if (!String.IsNullOrEmpty(BranchIdentifier))
            {
                if (String.IsNullOrEmpty(nextAce.BranchIdentifier))
                {
                    nextAce.BranchIdentifier = BranchIdentifier;
                }
                else if (nextAce.BranchIdentifier != BranchIdentifier)
                {
                    Globs.Throw<ArgumentException>("AddNextAce: Policy ACE with non-empty BranchName can only have a child with the same or no branch name");
                }
                BranchIdentifier = "";
            }
            NextAce = nextAce;
            NextAce.PreviousAce = this;
            return nextAce;
        }

        public PolicyAce And(PolicyAce nextAce)
        {
            return AddNextAce(nextAce);
        }

        internal TpmHash GetNextAcePolicyDigest(TpmAlgId hashAlg)
        {
            if (NextAce == null)
            {
                if (String.IsNullOrEmpty(BranchIdentifier))
                {
                    Globs.Throw("GetNextAcePolicyDigest: Policy tree leaf must have a BranchIdentifier set to allow the policy to be evaluated");
                }
                return TpmHash.ZeroHash(hashAlg);
            }

            TpmHash chainHash = NextAce.GetPolicyDigest(hashAlg);
            return chainHash;
        }

        public override string ToString()
        {
            string ss = String.Format("Type = {0}, NodeID = {1}, BranchId = {2}", GetType(), NodeId, BranchIdentifier);
            return ss;
        }

#if __XML_SCHEMA_EXPORT__
        public static string GetSchema(IEnumerable<Type> typesToExport)
        {
            XsdDataContractExporter exporter = new XsdDataContractExporter();
            Type headType = null;
            foreach (Type t in typesToExport)
            {
                if(headType ==null) headType = t;
                if (!exporter.CanExport(t)) throw new ArgumentException("cannot export type: " + t.ToString());
                exporter.Export(t);
                Console.WriteLine("number of schemas: {0}", exporter.Schemas.Count);
                Console.WriteLine();
            }
            XmlSchemaSet schemas = exporter.Schemas;

            XmlQualifiedName XmlNameValue = exporter.GetRootElementName(headType);
            string ns = XmlNameValue.Namespace;

            StringWriter w = new StringWriter();
            foreach (XmlSchema schema in schemas.Schemas(ns))
            {
                //if(schema.
                schema.Write(w);
            }

            Debug.WriteLine(w.ToString());

            return schemas.ToString();
        }
#endif // __XML_SCHEMA_EXPORT__    

        /// <summary>
        /// Set if the ACE is associated with a PolicyTree.
        /// </summary>
        internal PolicyTree AssociatedPolicy;

        /// <summary>
        /// Previous is closer to the root.
        /// </summary>
        internal PolicyAce PreviousAce;

        /// <summary>
        /// Next is closer to the leaf.
        /// </summary>
        internal PolicyAce NextAce;

        /// <summary>
        /// When a policy is evaluated the caller must name the branch that they wish to attempt to 
        /// satisfy. Set the branch identifier in the leaf ACE.
        /// </summary>
        internal string BranchIdentifier = "";

        /// <summary>
        /// Implements the first step of the policy digest update (see the PolicyUpdate()
        /// method), and also used by PolicyAuthorizeNV.
        /// </summary>
        internal TpmHash PolicyUpdate1(TpmHash currentHash, TpmCc commandCode, byte[] name)
        {
            var m = new Marshaller();
            m.Put(commandCode, "commandCode");
            m.Put(name, "name");

            return currentHash.Extend(m.GetBytes());
        }

        /// <summary>
        /// Return an updated policy digest in accordance with the TPM 2.0 Specification
        /// Section 23.2.3 Policy Digest Update Function
        /// </summary>
        internal TpmHash PolicyUpdate(TpmHash currentHash, TpmCc commandCode, byte[] name, byte[] refData)
        {
            return PolicyUpdate1(currentHash, commandCode, name).Extend(refData);
        }

        // Helper-function for naming policy chains
        public static implicit operator PolicyAce(string chainId)
        {
            return new TpmPolicyChainId(chainId);
        }

        protected PolicyAce ArrayToChain(PolicyAce[] arr)
        {
            for (int j = 0; j < arr.Length - 1; j++)
            {
                arr[j].NextAce = arr[j + 1];
                arr[j + 1].PreviousAce = arr[j];
            }
            return arr[0];
        }

        internal abstract TpmHash GetPolicyDigest(TpmAlgId hashAlg);

        // ReSharper disable once InconsistentNaming
        internal abstract TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy);
        [DataMember(EmitDefaultValue=false)]
        public string NodeId = null;
    }

    /// <summary>
    /// This command allows options in authorizations without requiring that the TPM evaluate all of
    /// the options. If a policy may be satisfied by different sets of conditions, the TPM need only
    /// evaluate one set that satisfies the policy. This command will indicate that one of the 
    /// required sets of conditions has been satisfied.
    /// </summary>
    public class TpmPolicyOr : PolicyAce
    {
        /// <summary>
        /// This command allows options in authorizations without requiring that the TPM evaluate all of
        /// the options. If a policy may be satisfied by different sets of conditions, the TPM need only
        /// evaluate one set that satisfies the policy. This command will indicate that one of the
        /// required sets of conditions has been satisfied.
        /// </summary>
        /// <param name="branchName"></param>
        public TpmPolicyOr(string branchName = "") : base(branchName)
        {
            PolicyBranches = new List<PolicyAce>();
        }

        public TpmPolicyOr() : base("")
        {
            PolicyBranches = new List<PolicyAce>();
        }

        /// <summary>
        /// Add an "OR-branch"
        /// </summary>
        /// <param name="newAce"></param>
        /// <returns></returns>
        public PolicyAce AddPolicyBranch(PolicyAce newAce)
        {
            PolicyBranches.Add(newAce);
            newAce.PreviousAce = this;
            return newAce;
        }

        public void AddPolicyBranches(PolicyAce[] arr)
        {
            PolicyBranches.Clear();
            foreach (PolicyAce a in arr)
            {
                PolicyBranches.Add(a);
            }
        }

        /// <summary>
        /// Calculates and returns the policy-hashes of the attached branches.
        /// </summary>
        /// <param name="hashAlg"></param>
        /// <returns></returns>
        public Tpm2bDigest[] GetPolicyHashArray(TpmAlgId hashAlg)
        {
            int numBranches = PolicyBranches.Count;
            if (numBranches < 2 || numBranches > 8)
            {
                Globs.Throw("GetPolicyHashArray: Must have between 2 and 8 branches in a PolicyOr");
            }

            int i = 0;
            var childHashes = new Tpm2bDigest[numBranches];
            foreach (PolicyAce branch in PolicyBranches)
            {
                TpmHash branchPolicyHash = branch.GetPolicyDigest(hashAlg);
                childHashes[i++] = branchPolicyHash;
            }

            return childHashes;
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            int numBranches = PolicyBranches.Count;
            if (numBranches < 2 || numBranches > 8)
            {
                Globs.Throw("GetPolicyDigest: Must have between 2 and 8 branches in a PolicyOr");
            }

            var m = new Marshaller();
            m.Put(TpmHash.ZeroHash(hashAlg).HashData, "zero");
            m.Put(TpmCc.PolicyOR, "ordinal");
            foreach (PolicyAce branch in PolicyBranches)
            {
                TpmHash branchPolicyHash = branch.GetPolicyDigest(hashAlg);
                m.Put(branchPolicyHash.HashData, "h");
            }
            byte[] polVal = CryptoLib.HashData(hashAlg, m.GetBytes());
            return new TpmHash(hashAlg, polVal);
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            Tpm2bDigest[] branchList = GetPolicyHashArray(policy.PolicyHash.HashAlg);
            tpm.PolicyOR(authSession, branchList);
            return tpm._GetLastResponseCode();
        }

        /// <summary>
        /// Current policy-branches for PolicyOr (up to 8)
        /// </summary>
        internal List<PolicyAce> PolicyBranches;

        /// <summary>
        /// OR-branches as an array.
        /// </summary>
        [XmlArray("OrBranches")]
        [XmlArrayItem("Branch")]
        [DataMember()]
        public PolicyAce[][] Branches
        {
            get
            {
                var arr = new PolicyAce[PolicyBranches.Count][];
                for (int j = 0; j < PolicyBranches.Count; j++)
                {
                    arr[j] = TpmPolicy.GetArrayRepresentation(PolicyBranches[j]);
                }
                return arr;
            }
            set
            {
                PolicyAce[][] aa = value;
                foreach (PolicyAce[] a in aa)
                {
                    AddPolicyBranch(ArrayToChain(a));
                }
            }
        }
    }

    /// <summary>
    /// This command is used to cause conditional gating of a policy based on PCR. 
    /// This allows one group of authorizations to occur when PCRs are in one state 
    /// and a different set of authorizations when the PCRs are in a different state. 
    /// If this command is used for a trial policySession, the policyHash will be 
    /// updated using the values from the command rather than the values from 
    /// digest of the TPM PCR.
    /// </summary>
    [DataContract]
    [KnownType(typeof(PcrValue))]
    public class TpmPolicyPcr : PolicyAce
    {
        /// <summary>
        /// This command is used to cause conditional gating of a policy based on PCR. 
        /// This allows one group of authorizations to occur when PCRs are in one state 
        /// and a different set of authorizations when the PCRs are in a different state. 
        /// If this command is used for a trial policySession, the policyHash will be 
        /// updated using the values from the command rather than the values from 
        /// digest of the TPM PCR.
        /// </summary>
        public TpmPolicyPcr(PcrValueCollection pcrs, string branchName = "") : base(branchName)
        {
            Pcrs = pcrs;
        }

        public TpmPolicyPcr() : base("")
        {
            Pcrs = null;
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            var m = new Marshaller();
            m.Put(TpmCc.PolicyPCR, "ordinal");
            m.Put(Pcrs.GetTpmlPcrSelection(), "selection");
            m.Put(Pcrs.GetSelectionHash(hashAlg).HashData, "pcrs");
            return GetNextAcePolicyDigest(hashAlg).Extend(m.GetBytes());
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession policySession, PolicyTree policy)
        {
            tpm.PolicyPCR(policySession, Pcrs.GetSelectionHash(policy.PolicyHash.HashAlg), Pcrs.GetPcrSelectionArray());
            return tpm._GetLastResponseCode();
        }

        internal PcrValueCollection Pcrs;

        [DataMember()]
        public PcrValue[] PcrValues
        {
            get
            {
                return Pcrs.Values;
            }
            set
            {
                Pcrs = new PcrValueCollection(value);
            }
        }
    }

    /// <summary>
    /// This command is used to cause conditional gating of a policy based on the 
    /// contents of the TPMS_TIME_INFO structure.
    /// </summary>
    [DataContract]
    public class TpmPolicyCounterTimer : PolicyAce
    {
        /// <summary>
        /// This command is used to cause conditional gating of a policy based on the 
        /// contents of the TPMS_TIME_INFO structure.
        /// </summary>
        public TpmPolicyCounterTimer(byte[] operandB, ushort offset, Eo operation, string branchName = "")
            : base(branchName)
        {
            OperandB = Globs.CopyData(operandB);
            Offset = offset;
            Operation = operation;
        }

        public TpmPolicyCounterTimer() : base("")
        {
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            var m = new Marshaller();
            m.Put(OperandB, "operandB");
            m.Put(Offset, "offset");
            m.Put(Operation, "operation");
            byte[] toHash = m.GetBytes();
            byte[] args = CryptoLib.HashData(hashAlg, toHash);

            m = new Marshaller();
            m.Put(TpmCc.PolicyCounterTimer, "cc");
            m.Put(args, "args");

            TpmHash tailHash = GetNextAcePolicyDigest(hashAlg);
            TpmHash hashNow = tailHash.Extend(m.GetBytes());
            return hashNow;
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession policySession, PolicyTree policy)
        {
            tpm.PolicyCounterTimer(policySession, OperandB, Offset, Operation);
            return tpm._GetLastResponseCode();
        }
        
        [DataMember()]
        public byte[] OperandB;
        [DataMember()]
        public ushort Offset;
        [DataMember()]
        public Eo Operation;
    }

    /// <summary>
    /// This command indicates that the authorization will be limited to a specific command code.
    /// </summary>
    public class TpmPolicyCommand : PolicyAce
    {
        /// <summary>
        /// This command indicates that the authorization will be limited to a specific command code.
        /// </summary>
        public TpmPolicyCommand(TpmCc commandCode, string branchName = "") : base(branchName)
        {
            AllowedCommand = commandCode;
        }

        public TpmPolicyCommand() : base("")
        {
            AllowedCommand = 0;
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            var m = new Marshaller();
            m.Put(TpmCc.PolicyCommandCode, "ordinal");
            m.Put(AllowedCommand, "allowedCommand");
            return GetNextAcePolicyDigest(hashAlg).Extend(m.GetBytes());
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            tpm.PolicyCommandCode(authSession, AllowedCommand);
            return tpm._GetLastResponseCode();
        }

        [MarshalAs(0)]
        [DataMember()]
        public TpmCc AllowedCommand;
    }
    
    /// <summary>
    /// This command is used to allow a policy to be bound to a specific command 
    /// and command parameters.
    /// </summary>
    public class TpmPolicyCpHash : PolicyAce
    {
        /// <summary>
        /// This command is used to allow a policy to be bound to a specific command 
        /// and command parameters.
        /// </summary>
        public TpmPolicyCpHash(TpmHash expectedCpHash, string branchName = "") : base(branchName)
        {
            CpHash = expectedCpHash;
        }

        public TpmPolicyCpHash() : base("")
        {
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {

            var m = new Marshaller();
            m.Put(TpmCc.PolicyCpHash, "commandCode");
            m.Put(CpHash.HashData, "hashData");
            return GetNextAcePolicyDigest(hashAlg).Extend(m.GetBytes());
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            tpm.PolicyCpHash(authSession, CpHash);
            return tpm._GetLastResponseCode();
        }

        public TpmHash CpHash;
    }

    /// <summary>
    /// This command allows a policy to be bound to a specific set of handles 
    /// without being bound to the parameters of the command. This is most 
    /// useful for commands such as TPM2_Duplicate() and for TPM2_PCR_Event() 
    /// when the referenced PCR requires a policy.
    /// </summary>
    public class TpmPolicyNameHash : PolicyAce
    {
        /// <summary>
        /// This command allows a policy to be bound to a specific set of handles 
        /// without being bound to the parameters of the command. This is most 
        /// useful for commands such as TPM2_Duplicate() and for TPM2_PCR_Event() 
        /// when the referenced PCR requires a policy.
        /// </summary>
        public TpmPolicyNameHash(byte[] expectedNameHash, string branchName = "") : base(branchName)
        {
            NameHash = Globs.CopyData(expectedNameHash);
        }

        public TpmPolicyNameHash() : base("")
        {
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            var m = new Marshaller();
            m.Put(TpmCc.PolicyNameHash, "commandCod");
            m.Put(NameHash, "hashData");
            return GetNextAcePolicyDigest(hashAlg).Extend(m.GetBytes());
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            tpm.PolicyNameHash(authSession, NameHash);
            return tpm._GetLastResponseCode();
        }

        public byte[] NameHash;
    }

    /// <summary>
    /// This command indicates that the authorization will be limited to a specific locality
    /// </summary>
    [DataContract]
    [KnownType(typeof(LocalityAttr))]
    public class TpmPolicyLocality : PolicyAce
    {
        /// <summary>
        /// This command indicates that the authorization will be limited to a specific locality
        /// </summary>
        public TpmPolicyLocality(LocalityAttr loc, string branchName = "") : base(branchName)
        {
            AllowedLocality = loc;
        }

        public TpmPolicyLocality() : base("")
        {
            AllowedLocality = 0;
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            var m = new Marshaller();
            m.Put(TpmCc.PolicyLocality, "ordinal");
            m.Put(AllowedLocality, "locality");
            return GetNextAcePolicyDigest(hashAlg).Extend(m.GetBytes());
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            tpm.PolicyLocality(authSession, AllowedLocality);
            return tpm._GetLastResponseCode();
        }

        [MarshalAs(0)]
        [DataMember()]
        public LocalityAttr AllowedLocality;
    }

    /// <summary>
    /// This command is used to cause conditional gating of a policy based on the contents of an NV Index.
    /// </summary>
    public class TpmPolicyNV : PolicyAce
    {
        /// <summary>
        /// This command is used to cause conditional gating of a policy based on the contents of an NV Index.
        /// </summary>
        public TpmPolicyNV(
            TpmHandle authorizationHandle,
            byte[] nvAccessAuth,
            TpmHandle nvIndex,
            byte[] indexName,
            byte[] operandB,
            ushort offset,
            Eo operation,
            string branchName = "") : base(branchName)
        {
            AuthorizationHandle = authorizationHandle;
            NvAccessAuth = Globs.CopyData(nvAccessAuth);
            NvIndex = nvIndex;
            OperandB = Globs.CopyData(operandB);
            Offset = offset;
            Operation = operation;
            IndexName = Globs.CopyData(indexName);
        }

        public TpmPolicyNV() : base("")
        {
        }

        public TpmPolicyNV(byte[] nvIndexName, byte[] operandB, ushort offset, Eo operation) : base("")
        {
            IndexName = Globs.CopyData(nvIndexName);
            OperandB = Globs.CopyData(operandB);
            Offset = offset;
            Operation = operation;
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            var m = new Marshaller();
            m.Put(OperandB, "operandB");
            m.Put(Offset, "offset");
            m.Put(Operation, "operation");
            byte[] args = CryptoLib.HashData(hashAlg, m.GetBytes());

            m = new Marshaller();
            m.Put(TpmCc.PolicyNV, "ord");
            m.Put(args, "args");
            m.Put(IndexName, "name");

            return GetNextAcePolicyDigest(hashAlg).Extend(m.GetBytes());
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            TpmRc res;

            if (AuthorizationHandle == null)
            {
                TpmHandle nvHandle, authHandle;
                SessionBase nvAuth;
                AssociatedPolicy.ExecutePolicyNvCallback(this, out authHandle, out nvHandle, out nvAuth);
                tpm[nvAuth].PolicyNV(authHandle, nvHandle, authSession,
                                     OperandB, Offset, Operation);
                res = tpm._GetLastResponseCode();

                if (!(nvAuth is Pwap))
                {
                    tpm.FlushContext(nvAuth);
                }
            }
            else
            {
                tpm[NvAccessAuth].PolicyNV(AuthorizationHandle, NvIndex, authSession,
                    OperandB, Offset, Operation);
                res = tpm._GetLastResponseCode();
            }
            return res;
        }

        internal TpmHandle NvIndex;
        public byte[] OperandB;
        public ushort Offset;
        public Eo Operation;
        public byte[] IndexName;
        internal TpmHandle AuthorizationHandle;
        internal byte[] NvAccessAuth;
    }

    /// <summary>
    /// This command allows a policy to be bound to the authorization value of the authorized object.
    /// </summary>
    public class TpmPolicyAuthValue : PolicyAce
    {
        /// <summary>
        /// This command allows a policy to be bound to the authorization value of the authorized object.
        /// </summary>
        public TpmPolicyAuthValue(string branchName = "") : base(branchName)
        {
        }

        public TpmPolicyAuthValue() : base("")
        {
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            return GetNextAcePolicyDigest(hashAlg).Extend(Marshaller.GetTpmRepresentation(TpmCc.PolicyAuthValue));
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            tpm.PolicyAuthValue(authSession);
            authSession.SessIncludesAuth = true;
            return tpm._GetLastResponseCode();
        }
    }

    /// <summary>
    /// This command allows a policy authorization session to be returned to its initial state. 
    /// </summary>
    public class TpmPolicyRestart : PolicyAce
    {
        /// <summary>
        /// This command allows a policy authorization session to be returned to its initial state.
        /// </summary> 
        public TpmPolicyRestart(string branchName = "") : base(branchName)
        {
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            Globs.Throw("Do not include PolicyRestart in policy trees.");
            return new TpmHash(hashAlg);
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            Globs.Throw("Do not include PolicyRestart in running policies");
            return TpmRc.Policy;
        }
    }

    /// <summary>
    /// This command allows a policy to be bound to the authorization value of the authorized object.
    /// </summary>
    public class TpmPolicyPassword : PolicyAce
    {
        /// <summary>
        /// This command allows a policy to be bound to the authorization value of the authorized object.
        /// </summary>
        public TpmPolicyPassword(string branchName = "") : base(branchName)
        {

        }

        public TpmPolicyPassword() : base("")
        {

        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            return GetNextAcePolicyDigest(hashAlg).Extend(Marshaller.GetTpmRepresentation(TpmCc.PolicyAuthValue));
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            tpm.PolicyPassword(authSession);
            authSession.SessIncludesAuth = true;
            authSession.PlaintextAuth = true;
            return tpm._GetLastResponseCode();
        }
    }

    /// <summary>
    /// This command indicates that physical presence will need to be asserted 
    /// at the time the authorization is performed. 
    /// </summary>
    public class TpmPolicyPhysicalPresence : PolicyAce
    {
        /// <summary>
        /// This command indicates that physical presence will need to be asserted 
        /// at the time the authorization is performed. 
        /// </summary>
        public TpmPolicyPhysicalPresence(string branchName = "") : base(branchName)
        {
        }

        public TpmPolicyPhysicalPresence() : base("")
        {
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            return GetNextAcePolicyDigest(hashAlg).Extend(Marshaller.GetTpmRepresentation(TpmCc.PolicyPhysicalPresence));
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            tpm.PolicyPhysicalPresence(authSession);
            return tpm._GetLastResponseCode();
        }
    }

    /// <summary>
    /// Base class for TpmPolicySigned and TpmPolicySecret.
    /// </summary>
    public abstract class TpmPolicyWithExpiration : PolicyAce
    {
        public bool UseNonceTpm = false;
        public int ExpirationTime = 0;
        public byte[] CpHash;
        public byte[] PolicyRef;
        protected TkAuth PolicyTicket;
        protected byte[] Timeout;

        protected TpmPolicyWithExpiration() : base("")
        {
        }

        protected TpmPolicyWithExpiration(
            bool useNonceTpm,
            int expirationTime,
            byte[] cpHash,
            byte[] policyRef,
            string branchName) : base(branchName)
        {
            UseNonceTpm = useNonceTpm;
            ExpirationTime = expirationTime;
            CpHash = Globs.CopyData(cpHash);
            PolicyRef = Globs.CopyData(policyRef);
        }
 
        public byte[] GetTimeout()
        {
            return Timeout;
        }

        public TkAuth GetPolicyTicket()
        {
            return PolicyTicket;
        }
    }

    /// <summary>
    /// This command includes an asymmetrically signed authorization in a policy.
    /// </summary>
    public class TpmPolicySigned : TpmPolicyWithExpiration
    {
        public TpmPolicySigned()
        {
        }
        public TpmPolicySigned(
            AsymCryptoSystem authorityKey,
            bool useNonceTpm,       
            int expirationTime,
            byte[] cpHash,
            byte[] policyRef,
            string branchName = "") : base(useNonceTpm, expirationTime, cpHash, policyRef, branchName)
        {
            SigningKey = authorityKey;
            SigningKeyPub = SigningKey.GetPublicParms();
            AuthObjectName = authorityKey.GetPublicParms().GetName();
        }

        public TpmPolicySigned(
            byte[] authorityKeyName,
            bool useNonceTpm,
            int expirationTime,
            byte[] cpHash,
            byte[] policyRef,
            string branchName = "") : base(useNonceTpm, expirationTime, cpHash, policyRef, branchName)
        {
            AuthObjectName = Globs.CopyData(authorityKeyName);
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            TpmHash atStart = GetNextAcePolicyDigest(hashAlg);
            return PolicyUpdate(atStart, TpmCc.PolicySigned, AuthObjectName, PolicyRef);
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            byte[] nonceTpm = UseNonceTpm ? Globs.CopyData(authSession.NonceTpm) : new byte[0];

            var dataToSign = new Marshaller();
            dataToSign.Put(nonceTpm, "");
            ISignatureUnion signature;
            // If the library has been given a signing key we can do the challenge here (else we need to call out)
            TpmHandle verificationKey;
            if (SigningKey != null)
            {
                dataToSign.Put(ExpirationTime, "");
                dataToSign.Put(CpHash, "");
                dataToSign.Put(PolicyRef, "");
                // Just ask the key to sign the challenge
                signature = SigningKey.Sign(dataToSign.GetBytes());
                verificationKey = tpm.LoadExternal(null, SigningKeyPub, TpmRh.Owner);
            }
            else
            {
                TpmPublic verifier;
                signature = AssociatedPolicy.ExecuteSignerCallback(this, nonceTpm, out verifier);
                verificationKey = tpm.LoadExternal(null, verifier, TpmRh.Owner);
            }
            TkAuth policyTicket;

            Timeout = tpm.PolicySigned(verificationKey,
                                       authSession,
                                       nonceTpm,
                                       CpHash,
                                       PolicyRef,
                                       ExpirationTime,
                                       signature,
                                       out policyTicket);

            TpmRc responseCode = tpm._GetLastResponseCode();
            // Save the policyTicket in case it is needed later
            PolicyTicket = policyTicket;
            tpm.FlushContext(verificationKey);
            return responseCode;
        }

        internal AsymCryptoSystem SigningKey;
        internal TpmPublic SigningKeyPub = null;

        public byte[] AuthObjectName;

        /*
        // stuff for XML serialization 
        public TpmPublic AuthObject
        {
            get
            {
                if (SigningKeyPub != null)
                {
                    return SigningKeyPub;
                }
                return SigningKey.GetPublicParms();
            }
            set
            {
                SigningKeyPub = value;
            }
        }
         * */
    }

    /// <summary>
    /// This command includes a secret-based authorization to a policy. 
    /// The caller proves knowledge of the secret value using either a 
    /// password or an HMAC-based authorization session.
    /// </summary>
    public class TpmPolicySecret : TpmPolicyWithExpiration
    {
        public TpmPolicySecret()
        {
        }

        public TpmPolicySecret(
            TpmHandle authorityHandle,
            byte[] authorityName,
            AuthValue theAuthVal,
            bool useNonceTpm,
            int expirationTime,
            byte[] cpHash,
            byte[] policyRef,
            string branchName = "") : base(useNonceTpm, expirationTime, cpHash, policyRef, branchName)
        {
            AuthVal = theAuthVal;
            AuthorityHandle = authorityHandle;
            AuthorityName = Globs.CopyData(authorityName);
        }

        public TpmPolicySecret(
            byte[] authObjectName,
            bool useNonceTpm,
            byte[] cpHash,
            byte[] policyRef,
            int expirationTime,
            string branchName = "") : base(useNonceTpm, expirationTime, cpHash, policyRef, branchName)
        {
            AuthorityName = Globs.CopyData(authObjectName);
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            TpmHash atStart = GetNextAcePolicyDigest(hashAlg);
            TpmHash atEnd = PolicyUpdate(atStart, TpmCc.PolicySecret, AuthorityName, PolicyRef);
            return atEnd;
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            TpmRc res;
            byte[] nonceTpm = UseNonceTpm ? Globs.CopyData(authSession.NonceTpm) : new byte[0];

            if (AuthVal == null)
            {
                SessionBase session;
                TpmHandle authorizedEntity;
                bool flushHandleOnCompletion;

                AssociatedPolicy.ExecutePolicySecretCallback(this,
                                                              out session,
                                                              out authorizedEntity,
                                                              out flushHandleOnCompletion);

                Timeout = tpm[session].PolicySecret(authorizedEntity,
                                                    authSession,
                                                    nonceTpm,
                                                    CpHash,
                                                    PolicyRef,
                                                    ExpirationTime,
                                                    out PolicyTicket);
                res = tpm._GetLastResponseCode();
                if (flushHandleOnCompletion)
                {
                    tpm.FlushContext(authorizedEntity);
                }
                if (!(session is Pwap))
                {
                    tpm.FlushContext(session);
                }
            }
            else
            {
                Timeout = tpm[AuthVal].PolicySecret(AuthorityHandle,
                                                    authSession,
                                                    nonceTpm,
                                                    CpHash,
                                                    PolicyRef,
                                                    ExpirationTime,
                                                    out PolicyTicket);
                res = tpm._GetLastResponseCode();
            }
            return res;
        }

        public AuthValue AuthVal;
        public TpmHandle AuthorityHandle;
        public byte[] AuthorityName;
    }

    /// <summary>
    /// This command is similar to TPM2_PolicySigned() except that it takes a 
    /// ticket instead of a signed authorization. The ticket represents a 
    /// validated authorization that had an expiration time associated with it.
    /// </summary>
    public class TpmPolicyTicket : PolicyAce
    {
        /// <summary>
        /// This command is similar to TPM2_PolicySigned() except that it takes a 
        /// ticket instead of a signed authorization. The ticket represents a 
        /// validated authorization that had an expiration time associated with it.
        /// </summary>
        public TpmPolicyTicket(
            TkAuth ticket,
            byte[] expirationTimeFromSignOperation,
            byte[] cpHash,
            byte[] policyRef,
            byte[] objectName,
            string branchName = "") : base(branchName)
        {
            Ticket = ticket;
            ExpirationTime = Globs.CopyData(expirationTimeFromSignOperation);
            CpHash = Globs.CopyData(cpHash);
            PolicyRef = Globs.CopyData(policyRef);
            ObjectName = Globs.CopyData(objectName);
            TicketType = ticket.tag;
        }

        public TpmPolicyTicket() : base("")
        {
        }

        public TpmPolicyTicket(TpmPublic authorizingKey, byte[] policyRef, TpmSt ticketType) : base("")
        {
            AuthorizingKey = authorizingKey;
            PolicyRef = Globs.CopyData(policyRef);
            TicketType = ticketType;
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            TpmCc commandCode = 0;
            if (TicketType == TpmSt.AuthSecret)
                commandCode = TpmCc.PolicySecret;
            else if (TicketType == TpmSt.AuthSigned)
                commandCode = TpmCc.PolicySigned;
            else
            {
                Globs.Throw<ArgumentException>("Ticket type is not recognized");
                return new TpmHash(hashAlg);
            }

            if (ObjectName == null)
            {
                ObjectName = AuthorizingKey.GetName();
            }
            var m = new Marshaller();
            m.Put(commandCode, "ordinal");
            m.Put(ObjectName, "name");

            // ReSharper disable once UnusedVariable
            TpmHash atStart = GetNextAcePolicyDigest(hashAlg);
            TpmHash firstExtend = GetNextAcePolicyDigest(hashAlg).Extend(m.GetBytes());
            TpmHash secondExtend = firstExtend.Extend(PolicyRef);

            return secondExtend;
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            if (ObjectName == null)
            {
                ObjectName = AuthorizingKey.GetName();
            }
            tpm.PolicyTicket(authSession,
                             ExpirationTime,
                             CpHash,
                             PolicyRef,
                             Marshaller.GetTpmRepresentation(ObjectName),
                             Ticket);
            return tpm._GetLastResponseCode();
        }

        public TpmPublic AuthorizingKey;
        public byte[] ExpirationTime;
        public byte[] CpHash;
        public byte[] PolicyRef;
        internal TkAuth Ticket;
        public TpmSt TicketType;
        public void SetTicket(TkAuth ticket)
        {
            Ticket = ticket;
        }
        internal byte[] ObjectName;
    }

    /// <summary>
    /// This command allows policies to change. If a policy were static, 
    /// then it would be difficult to add users to a policy. This command lets a 
    /// policy authority sign a new policy so that it may be used in an existing policy.
    /// </summary>
    public class TpmPolicyAuthorize : PolicyAce
    {
        public delegate void ParamsCallback(Tpm2 tpm, ref TpmHandle policySession, ref byte[] approvedPolicy, ref byte[] policyRef, byte[] keySign, ref TkVerified checkTicket);

        [XmlIgnore]
        public ParamsCallback TheParamsCallback = null;

        /// <summary>
        /// This command allows policies to change. If a policy were static, 
        /// then it would be difficult to add users to a policy. This command lets a 
        /// policy authority sign a new policy so that it may be used in an existing policy.
        /// </summary>
        public TpmPolicyAuthorize(
            byte[] policyToReplace,
            byte[] policyRef,
            TpmPublic signingKey, 
            TpmAlgId signingHash, 
            ISignatureUnion signature,
            string branchName = "") : base(branchName)
        {
            PolicyToReplace = Globs.CopyData(policyToReplace);
            PolicyRef = Globs.CopyData(policyRef);
            SigningKey = signingKey;
            SigningHash = signingHash;
            // todo - this should really be an ISigntatureUnion but the stock serializer
            // can't serialize interfaces
            //Signature = (SignatureRsassa)signature;
            // ReSharper disable once CanBeReplacedWithTryCastAndCheckForNull
            if (signature is SignatureRsapss)
            {
                Sig1 = (SignatureRsapss) signature;
            }
            // ReSharper disable once CanBeReplacedWithTryCastAndCheckForNull
            if (signature is SignatureRsassa)
            {
                Sig2 = (SignatureRsassa) signature;
            }
        }

        public TpmPolicyAuthorize() : base("")
        {
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            // Authorize results in a REPLACEMENT not an extend of the previous policy. 
            var zeroHash = new TpmHash(hashAlg);
            TpmHash atEnd = PolicyUpdate(zeroHash, TpmCc.PolicyAuthorize, SigningKey.GetName(), PolicyRef);
            return atEnd;
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            byte[] dataToSign = Globs.Concatenate(PolicyToReplace, PolicyRef);
            byte[] aHash = CryptoLib.HashData(SigningHash, dataToSign);

            TpmHandle verifierHandle = tpm.LoadExternal(null, SigningKey, TpmRh.Owner);
            if (policy.AllowErrorsInPolicyEval)
            {
                tpm._AllowErrors();
            }

            // todo - fix the serialization so that we can persist the interface
            ISignatureUnion theSig = null;
            if(null!= (Object) Sig1)
            {
                theSig = Sig1;
            }
            if (null != (Object)Sig2)
            {
                theSig = Sig2;
            }

            if (theSig != null)
            {
                Ticket = tpm.VerifySignature(verifierHandle, aHash, theSig);
                TpmRc intermediateError = tpm._GetLastResponseCode();
                if (intermediateError != TpmRc.Success)
                {
                    tpm.FlushContext(verifierHandle);
                    return intermediateError;
                }
            }
            else
            {
                // create a dummy ticket = e.g. for a trial session
                Ticket = new TkVerified(TpmRh.Owner, new byte[0]);
            }
            tpm.FlushContext(verifierHandle);


            byte[] keySign = SigningKey.GetName();
            TpmHandle policySession = authSession;
            if (TheParamsCallback != null)
            {
                TheParamsCallback(tpm, ref policySession, ref PolicyToReplace, ref PolicyRef, keySign, ref Ticket);
            }
            if (policy.AllowErrorsInPolicyEval)
            {
                tpm._AllowErrors();
            }
            tpm.PolicyAuthorize(policySession, PolicyToReplace, PolicyRef, keySign, Ticket);

            return tpm._GetLastResponseCode();
        }
        public byte[] PolicyToReplace;
        public byte[] PolicyRef;
        public TpmPublic SigningKey;
        public TpmAlgId SigningHash;
        private TkVerified Ticket;
        // changed from SIgnatureSsa (see note above)
        //public ISignatureUnion Signature;
        public SignatureRsapss Sig1;
        public SignatureRsassa Sig2;
    }


    // Allows policies to change by indirection. It allows creation of a policy that
    // refers to a policy that exists in a specified NV location. When executed, the
    // policy hash algorithm ID and the policyBuffer are compared to an algorithm ID
    // and data that reside in the specified NV location. If they match, the TPM will
    // reset policySession→policyDigest to a Zero Digest. Then it will update
    // policySession→policyDigest with 
    //   policyDigestnew ≔ HpolicyAlg(policyDigestold || TPM_CC_PolicyAuthorizeNV || nvIndex→Name)
    //
    public class TpmPolicyAuthorizeNV : PolicyAce
    {
        public TpmHandle   AuthHandle;
        public TpmHandle   NvIndex;
        public byte[]      NvIndexName;

        public TpmPolicyAuthorizeNV(TpmHandle authHandle, TpmHandle nvIndex,
                                    byte[] nvIndexName, string branchName = "")
            : base(branchName)
        {
            AuthHandle = authHandle;
            NvIndex = nvIndex;
            NvIndexName = nvIndexName;
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            // Authorize NV results in a REPLACEMENT not an extend of the previous policy. 
            return PolicyUpdate1(TpmHash.ZeroHash(hashAlg), TpmCc.PolicyAuthorizeNV, NvIndexName);
        }

        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            tpm.PolicyAuthorizeNV(AuthHandle, NvIndex, authSession);
            return tpm._GetLastResponseCode();
        }
    } // class TpmPolicyAuthorizeNV


    /// <summary>
    /// This command allows qualification of duplication to allow duplication to a 
    /// selected new parent. If this command is used without a subsequent 
    /// TPM2_PolicyAuthorize() in the policy, then only the new parent is selected. 
    /// If a subsequent TPM2_PolicyAuthorize() is used, then both the new parent 
    /// and the object being duplicated may be specified.
    /// </summary>
    public class TpmPolicyDuplicationSelect : PolicyAce
    {
        /// <summary>
        /// This command allows qualification of duplication to allow duplication to a 
        /// selected new parent. If this command is used without a subsequent 
        /// TPM2_PolicyAuthorize() in the policy, then only the new parent is selected. 
        /// If a subsequent TPM2_PolicyAuthorize() is used, then both the new parent 
        /// and the object being duplicated may be specified.
        /// </summary>
        public TpmPolicyDuplicationSelect(
            byte[] nameOfObjectBeingDuplicated,
            byte[] nameOfNewParent,
            bool includeObjectNameInPolicyHash,
            string branchName = "") : base(branchName)
        {
            NameOfObject = Globs.CopyData(nameOfObjectBeingDuplicated);
            NameOfNewParent = Globs.CopyData(nameOfNewParent);
            IncludeObjectNameInPolicyHash = includeObjectNameInPolicyHash;
        }

        public TpmPolicyDuplicationSelect() : base("")
        {
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            var m = new Marshaller();
            m.Put(TpmCc.PolicyDuplicationSelect, "ordinal");
            if (IncludeObjectNameInPolicyHash)
            {
                m.Put(NameOfObject, "objectName");
            }
            m.Put(NameOfNewParent, "newParent");
            byte includeName = IncludeObjectNameInPolicyHash ? (byte)1 : (byte)0;
            m.Put(includeName, "includeObject");
            TpmHash previous = GetNextAcePolicyDigest(hashAlg);
            return previous.Extend(m.GetBytes());
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            byte includeName = IncludeObjectNameInPolicyHash ? (byte)1 : (byte)0;
            tpm.PolicyDuplicationSelect(authSession, NameOfObject, NameOfNewParent, includeName);
            return tpm._GetLastResponseCode();
        }

        public byte[] NameOfObject;
        public byte[] NameOfNewParent;
        public bool IncludeObjectNameInPolicyHash;
    }

    /// <summary>
    /// PolicyChainId is a dummy policy-ACE that allows the caller to name a chain.  PolicyChainId can 
    /// only be at the leaf of a chain.
    /// </summary>
    public class TpmPolicyChainId : PolicyAce
    {
        public TpmPolicyChainId() : base("")
        {
        }

        public TpmPolicyChainId(string branchName) : base(branchName)
        {
            BranchId = branchName;
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            if (NextAce != null)
            {
                Globs.Throw("PolicyChainId should be a leaf");
                return new TpmHash(hashAlg);
            }
            TpmHash previous = GetNextAcePolicyDigest(hashAlg);
            return previous;
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            return TpmRc.Success;
        }

        // ReSharper disable once InconsistentNaming
        string _branchId;

        [MarshalAs(0)]
        [DataMember()]
        public string BranchId
        {
            get
            {
                return _branchId;
            }
            set
            {
                _branchId = value;
                BranchIdentifier = value;
            }
        }
    }

    /// <summary>
    /// PolicyAction is a dummy-ACE that allows a policy author to embed external data in a policy.  PolicyAction is _not_ 
    /// expected to be directly interpreted by the policy evaluator.  Instead it might be used to trigger
    /// other TPM or non-TPM program actions (like incrementing a monotonic counter).
    /// </summary>
    public class TpmPolicyAction : PolicyAce
    {
        public string Action = "";
        /// <summary>
        /// PolicyAction is a dummy-ACE that allows a policy author to embed external data in a policy.  PolicyAction is _not_ 
        /// expected to be directly interpreted by the policy evaluator.  Instead it might be used to trigger
        /// other TPM or non-TPM program actions (like incrementing a monotonic counter).
        /// </summary>
        public TpmPolicyAction() : base("")
        {
        }

        public TpmPolicyAction(string action) : base("")
        {
            Action = action;
        }

        public TpmPolicyAction(string action, Object context) : base("")
        {
            Action = action;
            Context = context;
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            TpmHash previous = GetNextAcePolicyDigest(hashAlg);
            return previous;
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            policy.ExecutePolicyActionCallback(this);
            return TpmRc.Success;
        }

        public Object Context = null;
    }

    /// <summary>
    /// This command allows a policy to be bound to the TPMA_NV_WRITTEN attributes. 
    /// This is a deferred assertion.  Values are stored in the policy session 
    /// context and checked when the policy is used for authorization.
    /// </summary>
    public class TpmPolicyNvWritten : PolicyAce
    {
        /// <summary>
        /// This command allows a policy to be bound to the TPMA_NV_WRITTEN attributes. 
        /// This is a deferred assertion.  Values are stored in the policy session 
        /// context and checked when the policy is used for authorization.
        /// </summary>
        public TpmPolicyNvWritten(bool isNvIndexRequiredToHaveBeenWritten, string branchName = "") : base(branchName)
        {
            IsNvIndexRequiredToHaveBeenWritten = isNvIndexRequiredToHaveBeenWritten;
        }

        public TpmPolicyNvWritten() : base("")
        {
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            var m = new Marshaller();
            m.Put(TpmCc.PolicyNvWritten, "ordinal");
            byte writtenName = IsNvIndexRequiredToHaveBeenWritten ? (byte)1 : (byte)0;
            m.Put(writtenName, "writtenSet");
            TpmHash previous = GetNextAcePolicyDigest(hashAlg);
            return previous.Extend(m.GetBytes());
        }

        // ReSharper disable once InconsistentNaming
        internal override TpmRc Execute(Tpm2 tpm, AuthSession authSession, PolicyTree policy)
        {
            byte writtenName = IsNvIndexRequiredToHaveBeenWritten ? (byte)1 : (byte)0;
            tpm.PolicyNvWritten(authSession, writtenName);
            return tpm._GetLastResponseCode();
        }

        public bool IsNvIndexRequiredToHaveBeenWritten;
    }
}

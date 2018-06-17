/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */


/*
 * This file contains classes that support the dozen-or-so TPM policy commands.  
 * The companion file Policy.cs contains the remainder of the TSS.Net support library
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
        protected PolicyAce(string branchName, string nodeId = null)
        {
            BranchID = branchName;
            NodeId = nodeId;
        }

        public PolicyAce AddNextAce(PolicyAce nextAce)
        {
            if (this is TpmPolicyOr)
            {
                Globs.Throw<ArgumentException>("AddNextAce: Do not call AddNextAce for " +
                                               "an OR node. Use AddPolicyBranch instead.");
            }
            if (NextAce != null)
            {
                Globs.Throw<ArgumentException>("AddNextAce: Policy ACE already has a child");
            }
            if (!String.IsNullOrEmpty(BranchID))
            {
                if (String.IsNullOrEmpty(nextAce.BranchID))
                {
                    nextAce.BranchID = BranchID;
                }
                else if (nextAce.BranchID != BranchID)
                {
                    Globs.Throw<ArgumentException>(
                                "AddNextAce: Policy ACE with non-empty BranchName " +
                                "can only have a child with the same or no branch name");
                }
                BranchID = "";
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
                if (String.IsNullOrEmpty(BranchID))
                {
                    Globs.Throw("GetNextAcePolicyDigest: Policy tree leaf must have a " +
                                "BranchIdentifier set to allow the policy to be evaluated");
                }
                return TpmHash.ZeroHash(hashAlg);
            }

            return NextAce.GetPolicyDigest(hashAlg);
        }

        public override string ToString()
        {
            string ss = String.Format("Type = {0}, NodeID = {1}, BranchId = {2}",
                                      GetType(), NodeId, BranchID);
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
        /// When a policy is evaluated the caller must name the branch that they wish
        /// to attempt to satisfy. Set the branch identifier in the leaf ACE.
        /// </summary>
        internal string BranchID = "";

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

        internal abstract TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy);

        [DataMember(EmitDefaultValue=false)]
        public string NodeId = null;
    } // abstract class PolicyAce

    /// <summary>
    /// This command allows options in authorizations without requiring that the TPM
    /// evaluate all of the options. If a policy may be satisfied by different sets of
    /// conditions, the TPM need only evaluate one set that satisfies the policy. This
    /// command will indicate that one of the required sets of conditions has been satisfied.
    /// </summary>
    public class TpmPolicyOr : PolicyAce
    {
        public TpmPolicyOr(string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
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
                childHashes[i++] = branch.GetPolicyDigest(hashAlg);
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

            return TpmHash.FromData(hashAlg, m.GetBytes());
        }

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            Tpm2bDigest[] branchList = GetPolicyHashArray(policy.PolicyHash.HashAlg);
            tpm.PolicyOR(sess, branchList);
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
    } // class TpmPolicyOr

    /// <summary>
    /// This command is used to cause conditional gating of a policy based on PCR. 
    /// This allows one group of authorizations to occur when PCRs are in one state 
    /// and a different set of authorizations when the PCRs are in a different state. 
    /// If this command is used for a trial sess, the policyHash will be 
    /// updated using the values from the command rather than the values from 
    /// digest of the TPM PCR.
    /// </summary>
    [DataContract]
    [KnownType(typeof(PcrValue))]
    public class TpmPolicyPcr : PolicyAce
    {
        public TpmPolicyPcr(PcrValueCollection pcrs, string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
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

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            tpm.PolicyPCR(sess, Pcrs.GetSelectionHash(policy.PolicyHash),
                                Pcrs.GetPcrSelectionArray());
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
    } // class TpmPolicyPcr

    /// <summary>
    /// This command is used to cause conditional gating of a policy based on the 
    /// contents of the TPMS_TIME_INFO structure.
    /// </summary>
    [DataContract]
    public class TpmPolicyCounterTimer : PolicyAce
    {
        public TpmPolicyCounterTimer(byte[] operandB, ushort offset, Eo operation,
                                     string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
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

            return GetNextAcePolicyDigest(hashAlg).Extend(m.GetBytes());
        }

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            tpm.PolicyCounterTimer(sess, OperandB, Offset, Operation);
            return tpm._GetLastResponseCode();
        }
        
        [DataMember()]
        public byte[] OperandB;
        [DataMember()]
        public ushort Offset;
        [DataMember()]
        public Eo Operation;
    } // class TpmPolicyCounterTimer

    /// <summary>
    /// This command indicates that the authorization will be limited to a specific
    /// command code.
    /// </summary>
    public class TpmPolicyCommand : PolicyAce
    {
        public TpmPolicyCommand(TpmCc commandCode, string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
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

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            tpm.PolicyCommandCode(sess, AllowedCommand);
            return tpm._GetLastResponseCode();
        }

        [MarshalAs(0)]
        [DataMember()]
        public TpmCc AllowedCommand;
    } // class TpmPolicyCommand
    
    /// <summary>
    /// This command is used to allow a policy to be bound to a specific command 
    /// and command parameters.
    /// </summary>
    public class TpmPolicyCpHash : PolicyAce
    {
        // This command is used to allow a policy to be bound to a specific command 
        // and command parameters.
        public TpmPolicyCpHash(TpmHash expectedCpHash, string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
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

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            tpm.PolicyCpHash(sess, CpHash);
            return tpm._GetLastResponseCode();
        }

        public TpmHash CpHash;
    } // class TpmPolicyCpHash

    /// <summary>
    /// This command allows a policy to be bound to a specific set of handles 
    /// without being bound to the parameters of the command. This is most 
    /// useful for commands such as TPM2_Duplicate() and for TPM2_PCR_Event() 
    /// when the referenced PCR requires a policy.
    /// </summary>
    public class TpmPolicyNameHash : PolicyAce
    {
        public TpmPolicyNameHash(byte[] expectedNameHash, string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
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

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            tpm.PolicyNameHash(sess, NameHash);
            return tpm._GetLastResponseCode();
        }

        public byte[] NameHash;
    } // class TpmPolicyNameHash

    /// <summary>
    /// This command indicates that the authorization will be limited to a specific locality
    /// </summary>
    [DataContract]
    [KnownType(typeof(LocalityAttr))]
    public class TpmPolicyLocality : PolicyAce
    {
        public TpmPolicyLocality(LocalityAttr loc, string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
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

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            tpm.PolicyLocality(sess, AllowedLocality);
            return tpm._GetLastResponseCode();
        }

        [MarshalAs(0)]
        [DataMember()]
        public LocalityAttr AllowedLocality;
    } // class TpmPolicyLocality

    /// <summary>
    /// This command is used to cause conditional gating of a policy based on the contents
    /// of an NV Index.
    /// </summary>
    public class TpmPolicyNV : PolicyAce
    {
        public TpmPolicyNV(TpmHandle authorizationHandle, byte[] nvAccessAuth,
                           TpmHandle nvIndex, byte[] indexName,
                           byte[] operandB, ushort offset, Eo operation,
                           string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
        {
            AuthorizationHandle = authorizationHandle;
            NvAccessAuth = Globs.CopyData(nvAccessAuth);
            NvIndex = nvIndex;
            OperandB = Globs.CopyData(operandB);
            Offset = offset;
            Operation = operation;
            IndexName = Globs.CopyData(indexName);
        }

        public TpmPolicyNV() : base("") {}

        public TpmPolicyNV(byte[] nvIndexName, byte[] operandB, ushort offset, Eo operation)
            : base("")
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

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            TpmRc res;

            if (AuthorizationHandle == null)
            {
                TpmHandle nvHandle, authHandle;
                SessionBase nvAuth;
                AssociatedPolicy.ExecutePolicyNvCallback(this, out authHandle,
                                                         out nvHandle, out nvAuth);
                tpm[nvAuth].PolicyNV(authHandle, nvHandle, sess,
                                     OperandB, Offset, Operation);
                res = tpm._GetLastResponseCode();

                if (!(nvAuth is Pwap))
                {
                    tpm.FlushContext(nvAuth);
                }
            }
            else
            {
                tpm[NvAccessAuth].PolicyNV(AuthorizationHandle, NvIndex, sess,
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
    } // class TpmPolicyNV

    /// <summary>
    /// This command allows a policy to be bound to the authorization value of the 
    /// authorized object.
    /// </summary>
    public class TpmPolicyAuthValue : PolicyAce
    {
        public TpmPolicyAuthValue(string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
        {}

        public TpmPolicyAuthValue() : base("") {}

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            return GetNextAcePolicyDigest(hashAlg)
                  .Extend(Marshaller.GetTpmRepresentation(TpmCc.PolicyAuthValue));
        }

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            tpm.PolicyAuthValue(sess);
            sess.SessIncludesAuth = true;
            return tpm._GetLastResponseCode();
        }
    } // class TpmPolicyAuthValue

    /// <summary>
    /// This command allows a policy authorization session to be returned to its
    /// initial state. 
    /// </summary>
    public class TpmPolicyRestart : PolicyAce
    {
        public TpmPolicyRestart(string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
        {}

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            Globs.Throw("Do not include PolicyRestart in policy trees.");
            return new TpmHash(hashAlg);
        }

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            Globs.Throw("Do not include PolicyRestart in running policies");
            return TpmRc.Policy;
        }
    } // class TpmPolicyRestart

    /// <summary>
    /// This command allows a policy to be bound to the authorization value of the
    /// authorized object.
    /// </summary>
    public class TpmPolicyPassword : PolicyAce
    {
        public TpmPolicyPassword(string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
        {}

        public TpmPolicyPassword() : base("") {}

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            return GetNextAcePolicyDigest(hashAlg)
                  .Extend(Marshaller.GetTpmRepresentation(TpmCc.PolicyAuthValue));
        }

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            tpm.PolicyPassword(sess);
            sess.SessIncludesAuth = true;
            sess.PlaintextAuth = true;
            return tpm._GetLastResponseCode();
        }
    } // class TpmPolicyPassword

    /// <summary>
    /// This command indicates that physical presence will need to be asserted 
    /// at the time the authorization is performed. 
    /// </summary>
    public class TpmPolicyPhysicalPresence : PolicyAce
    {
        public TpmPolicyPhysicalPresence(string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
        {}

        public TpmPolicyPhysicalPresence() : base("") {}

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            return GetNextAcePolicyDigest(hashAlg)
                  .Extend(Marshaller.GetTpmRepresentation(TpmCc.PolicyPhysicalPresence));
        }

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            tpm.PolicyPhysicalPresence(sess);
            return tpm._GetLastResponseCode();
        }
    } // class TpmPolicyPhysicalPresence

    /// <summary>
    /// Base class for TpmPolicySigned and TpmPolicySecret.
    /// </summary>
    public abstract class TpmPolicyWithExpiration : PolicyAce
    {
        public bool UseNonceTpm = false;
        public int ExpirationTime = 0;
        public byte[] CpHash;
        public byte[] PolicyRef;

        /// <summary>
        /// Ticket returned by a call to PolicySigned() or PolicySecret()
        /// </summary>
        public TkAuth Ticket;

        /// <summary>
        /// Timeout value returned by a call to PolicySigned() or PolicySecret()
        /// </summary>
        public byte[] Timeout;

        protected TpmPolicyWithExpiration() : base("")
        {
        }

        protected TpmPolicyWithExpiration(bool useNonceTpm, int expirationTime,
                                          byte[] cpHash, byte[] policyRef,
                                          string branchName, string nodeId = null)
            : base(branchName, nodeId)
        {
            UseNonceTpm = useNonceTpm;
            ExpirationTime = expirationTime;
            CpHash = Globs.CopyData(cpHash);
            PolicyRef = Globs.CopyData(policyRef);
        }
    } // class TpmPolicyWithExpiration

    /// <summary>
    /// This command includes an asymmetrically signed authorization in a policy.
    /// </summary>
    public class TpmPolicySigned : TpmPolicyWithExpiration
    {
        public bool KeepAuth = false;
        public ISignatureUnion AuthSig;
        public AsymCryptoSystem SwSigningKey;
        public TpmPublic SigningKeyPub;

        public byte[] AuthObjectName;

        public TpmPolicySigned() {}

        public TpmPolicySigned(AsymCryptoSystem authorityKey,
                               bool useNonceTpm, int expirationTime,
                               byte[] cpHash, byte[] policyRef = null,
                               string branchName = "", string nodeId = null)
            : base(useNonceTpm, expirationTime, cpHash, policyRef, branchName, nodeId)
        {
            SwSigningKey = authorityKey;
            SigningKeyPub = SwSigningKey.GetPublicParms();
            AuthObjectName = authorityKey.GetPublicParms().GetName();
        }

        public TpmPolicySigned(byte[] authorityKeyName,
                               bool useNonceTpm, int expirationTime,
                               byte[] cpHash, byte[] policyRef = null,
                               string branchName = "", string nodeId = null)
            : base(useNonceTpm, expirationTime, cpHash, policyRef, branchName, nodeId)
        {
            AuthObjectName = Globs.CopyData(authorityKeyName);
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            return PolicyUpdate(GetNextAcePolicyDigest(hashAlg),
                                TpmCc.PolicySigned, AuthObjectName, PolicyRef);
        }

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            byte[] nonceTpm = UseNonceTpm ? Globs.CopyData(sess.NonceTpm) : new byte[0];

            TpmHandle sigKey;

            // If we have both the authorizing signature and the corresponding
            // signing key handle, we are good to go.
            if (AuthSig == null)
            {
                var dataToSign = new Marshaller();
                dataToSign.Put(nonceTpm, "");

                // If we have a signing key we can build the challenge here
                // (else we need to call out)
                if (SwSigningKey != null)
                {
                    dataToSign.Put(ExpirationTime, "");
                    dataToSign.Put(CpHash, "");
                    dataToSign.Put(PolicyRef, "");
                    // Just ask the key to sign the challenge
                    AuthSig = SwSigningKey.Sign(dataToSign.GetBytes());
                    sigKey = tpm.LoadExternal(null, SigningKeyPub, TpmRh.Owner);
                }
                else
                {
                    TpmPublic verifier;
                    AuthSig = AssociatedPolicy.ExecuteSignerCallback(this, nonceTpm,
                                                                       out verifier);
                    sigKey = tpm.LoadExternal(null, verifier, TpmRh.Owner);
                }
            }
            else
            {
                sigKey = tpm.LoadExternal(null, SigningKeyPub, TpmRh.Owner);
            }
            Timeout = tpm.PolicySigned(sigKey, sess, nonceTpm,
                                       CpHash, PolicyRef, ExpirationTime,
                                       AuthSig, out Ticket);

            TpmRc responseCode = tpm._GetLastResponseCode();
            tpm.FlushContext(sigKey);
            if (!KeepAuth)
                AuthSig = null;
            return responseCode;
        }
    } // class TpmPolicySigned

    /// <summary>
    /// This command includes a secret-based authorization to a policy. 
    /// The caller proves knowledge of the secret value using either a 
    /// password or an HMAC-based authorization session.
    /// </summary>
    public class TpmPolicySecret : TpmPolicyWithExpiration
    {
        public TpmHandle AuthEntity;
        public SessionBase AuthSess;

        public TpmPolicySecret() {}

        public TpmPolicySecret(TpmHandle hAuth,
                               bool useNonceTpm, int expirationTime,
                               byte[] cpHash, byte[] policyRef,
                               string branchName = "", string nodeId = null)
            : base(useNonceTpm, expirationTime, cpHash, policyRef, branchName, nodeId)
        {
            if (hAuth.Name == null)
            {
                throw new ArgumentException("TpmPolicySecret() entity name is not set");
            }
            AuthEntity = hAuth;
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            return PolicyUpdate(GetNextAcePolicyDigest(hashAlg),
                                TpmCc.PolicySecret, AuthEntity.Name, PolicyRef);
        }

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            byte[] nonceTpm = UseNonceTpm ? Globs.CopyData(sess.NonceTpm) : new byte[0];

            if (AuthSess != null)
                tpm._SetSessions(AuthSess);

            Timeout = tpm.PolicySecret(AuthEntity, sess, nonceTpm,
                                        CpHash, PolicyRef, ExpirationTime,
                                        out Ticket);
            return tpm._GetLastResponseCode();
        }
    } // class TpmPolicySecret

    /// <summary>
    /// This command is similar to TPM2_PolicySigned() except that it takes a 
    /// ticket instead of a signed authorization. The ticket represents a 
    /// validated authorization that had an expiration time associated with it.
    /// </summary>
    public class TpmPolicyTicket : PolicyAce
    {
        public TpmPolicyTicket(TkAuth ticket,
                               byte[] expirationTimeFromSignOperation,
                               byte[] cpHash, byte[] policyRef,
                               byte[] objectName,
                               string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
        {
            Ticket = ticket;
            ExpirationTime = Globs.CopyData(expirationTimeFromSignOperation);
            CpHash = Globs.CopyData(cpHash);
            PolicyRef = Globs.CopyData(policyRef);
            ObjectName = Globs.CopyData(objectName);
            TicketType = ticket.tag;
        }

        public TpmPolicyTicket() : base("") {}

        public TpmPolicyTicket(TpmPublic authorizingKey, byte[] policyRef, TpmSt ticketType)
            : base("")
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

            return GetNextAcePolicyDigest(hashAlg).Extend(m.GetBytes()).Extend(PolicyRef);
        }

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            if (ObjectName == null)
            {
                ObjectName = AuthorizingKey.GetName();
            }
            tpm.PolicyTicket(sess, ExpirationTime, CpHash, PolicyRef,
                             ObjectName, Ticket);
            return tpm._GetLastResponseCode();
        }

        public TpmPublic AuthorizingKey;
        public byte[] ExpirationTime;
        public byte[] CpHash;
        public byte[] PolicyRef;
        public TkAuth Ticket;
        public TpmSt TicketType;

        internal byte[] ObjectName;
    } // class TpmPolicyTicket

    /// <summary>
    /// This command allows policies to change. If a policy were static, 
    /// then it would be difficult to add users to a policy. This command lets a 
    /// policy authority sign a new policy so that it may be used in an existing policy.
    /// </summary>
    public class TpmPolicyAuthorize : PolicyAce
    {
        public delegate void ParamsCallbackType(Tpm2 tpm, TpmHandle sess,
                                                byte[] approvedPolicy, byte[] policyRef,
                                                byte[] keySign, TkVerified checkTicket);

        [XmlIgnore]
        public ParamsCallbackType ParamsCallback = null;

        public byte[] PolicyToReplace;
        public byte[] PolicyRef;
        public byte[] SigKeyName;
        public TkVerified Ticket;

        public TpmPolicyAuthorize(byte[] policyToReplace, byte[] policyRef,
                                  byte[] sigKeyName, TkVerified tkVerified,
                                  string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
        {
            PolicyToReplace = Globs.CopyData(policyToReplace);
            PolicyRef = Globs.CopyData(policyRef);
            SigKeyName = sigKeyName;
            Ticket = tkVerified;
        }

        public TpmPolicyAuthorize() : base("") {}

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            // Authorize results in a REPLACEMENT not an extend of the previous policy. 
            return PolicyUpdate(TpmHash.ZeroHash(hashAlg),
                                TpmCc.PolicyAuthorize, SigKeyName, PolicyRef);
        }

        internal override
        TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
#if false
            if (Ticket == null)
            {
                // create a dummy ticket = e.g. for a trial session
                Ticket = new TkVerified(TpmRh.Owner, new byte[0]);
            }
#endif

            if (ParamsCallback != null)
            {
                ParamsCallback(tpm, sess, PolicyToReplace, PolicyRef, SigKeyName, Ticket);
            }
            if (policy.AllowErrorsInPolicyEval)
            {
                tpm._AllowErrors();
            }
            tpm.PolicyAuthorize(sess, PolicyToReplace, PolicyRef, SigKeyName, Ticket);

            return tpm._GetLastResponseCode();
        }

        public static
        TkVerified SignApproval(Tpm2 tpm, byte[] approvedPolicy, byte[] policyRef,
                                TpmHandle hSigKey, ISigSchemeUnion scheme = null)
        {
            byte[] name, qname;
            TpmPublic pub = tpm.ReadPublic(hSigKey, out name, out qname);

            byte[] dataToSign = Globs.Concatenate(approvedPolicy, policyRef);
            byte[] aHash = CryptoLib.HashData(pub.nameAlg, dataToSign);

            // Create an authorization certificate for the "approvedPolicy"
            var proof = new TkHashcheck(TpmRh.Null, null);
            var sig = tpm.Sign(hSigKey, aHash, scheme, proof);
            return tpm.VerifySignature(hSigKey, aHash, sig);
        }
    } // class TpmPolicyAuthorize


    /// <summary>
    /// Allows policies to change by indirection. It allows creation of a policy that
    /// refers to a policy that exists in a specified NV location. When executed, the
    /// policy hash algorithm ID and the policyBuffer are compared to an algorithm ID
    /// and data that reside in the specified NV location. If they match, the TPM will
    /// reset sess→policyDigest to a Zero Digest. Then it will update
    /// sess→policyDigest with 
    ///   policyDigestnew ≔ HpolicyAlg(policyDigestold || TPM_CC_PolicyAuthorizeNV || nvIndex→Name)
    ///
    /// </summary>
    public class TpmPolicyAuthorizeNV : PolicyAce
    {
        public TpmHandle   AuthHandle;
        public TpmHandle   NvIndex;
        public byte[]      NvIndexName;

        public TpmPolicyAuthorizeNV(TpmHandle authHandle, TpmHandle nvIndex,
                                    byte[] nvIndexName,
                                    string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
        {
            AuthHandle = authHandle;
            NvIndex = nvIndex;
            NvIndexName = nvIndexName;
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            // Authorize NV results in a REPLACEMENT not an extend of the previous policy. 
            return PolicyUpdate1(TpmHash.ZeroHash(hashAlg),
                                 TpmCc.PolicyAuthorizeNV, NvIndexName);
        }

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            tpm.PolicyAuthorizeNV(AuthHandle, NvIndex, sess);
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
        public TpmPolicyDuplicationSelect(byte[] nameOfObjectBeingDuplicated,
                                          byte[] nameOfNewParent,
                                          bool includeObjectNameInPolicyHash,
                                          string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
        {
            DupObjectName = Globs.CopyData(nameOfObjectBeingDuplicated);
            NewParentName = Globs.CopyData(nameOfNewParent);
            IncludeObjectNameInPolicyHash = includeObjectNameInPolicyHash;
        }

        public TpmPolicyDuplicationSelect() : base("") {}

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            var m = new Marshaller();
            m.Put(TpmCc.PolicyDuplicationSelect, "ordinal");
            if (IncludeObjectNameInPolicyHash)
            {
                m.Put(DupObjectName, "objectName");
            }
            m.Put(NewParentName, "newParent");
            byte includeName = IncludeObjectNameInPolicyHash ? (byte)1 : (byte)0;
            m.Put(includeName, "includeObject");

            return GetNextAcePolicyDigest(hashAlg).Extend(m.GetBytes());
        }

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            byte includeName = IncludeObjectNameInPolicyHash ? (byte)1 : (byte)0;
            tpm.PolicyDuplicationSelect(sess, DupObjectName, NewParentName, includeName);
            return tpm._GetLastResponseCode();
        }

        public byte[] DupObjectName;
        public byte[] NewParentName;
        public bool IncludeObjectNameInPolicyHash;
    } // TpmPolicyDuplicationSelect

    /// <summary>
    /// PolicyChainId is a dummy policy-ACE that allows the caller to name a chain.  PolicyChainId can 
    /// only be at the leaf of a chain.
    /// </summary>
    public class TpmPolicyChainId : PolicyAce
    {
        public TpmPolicyChainId() : base("") {}

        public TpmPolicyChainId(string branchName, string nodeId = null)
            : base(branchName, nodeId)
        {
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            if (NextAce != null)
            {
                Globs.Throw("PolicyChainId should be a leaf");
                return new TpmHash(hashAlg);
            }
            return GetNextAcePolicyDigest(hashAlg);
        }

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            return TpmRc.Success;
        }

        [MarshalAs(0)]
        [DataMember()]
        public string BranchId
        {
            get
            {
                return BranchID;
            }
            set
            {
                BranchID = value;
            }
        }
    } // class TpmPolicyChainId

    /// <summary>
    /// PolicyAction is a dummy-ACE that allows a policy author to embed external data
    /// in a policy.  PolicyAction is _not_ expected to be directly interpreted by the
    /// policy evaluator.  Instead it might be used to trigger other TPM or non-TPM
    /// related program actions (like incrementing a monotonic counter).
    /// </summary>
    public class TpmPolicyAction : PolicyAce
    {
        public string Action = "";

        public TpmPolicyAction() : base("") {}

        public TpmPolicyAction(string action,
                               string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
        {
            Action = action;
        }

        public TpmPolicyAction(string action, Object context,
                               string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
        {
            Action = action;
            Context = context;
        }

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            return GetNextAcePolicyDigest(hashAlg);
        }

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            policy.ExecutePolicyActionCallback(this);
            return TpmRc.Success;
        }

        public Object Context = null;
    } // class TpmPolicyAction

    /// <summary>
    /// This command allows a policy to be bound to the TPMA_NV_WRITTEN attributes. 
    /// This is a deferred assertion.  Values are stored in the policy session 
    /// context and checked when the policy is used for authorization.
    /// </summary>
    public class TpmPolicyNvWritten : PolicyAce
    {
        public TpmPolicyNvWritten(bool isNvIndexRequiredToHaveBeenWritten,
                                  string branchName = "", string nodeId = null)
            : base(branchName, nodeId)
        {
            IsNvIndexRequiredToHaveBeenWritten = isNvIndexRequiredToHaveBeenWritten;
        }

        public TpmPolicyNvWritten() : base("") {}

        internal override TpmHash GetPolicyDigest(TpmAlgId hashAlg)
        {
            var m = new Marshaller();
            m.Put(TpmCc.PolicyNvWritten, "ordinal");
            byte writtenName = IsNvIndexRequiredToHaveBeenWritten ? (byte)1 : (byte)0;
            m.Put(writtenName, "writtenSet");

            return GetNextAcePolicyDigest(hashAlg).Extend(m.GetBytes());
        }

        internal override TpmRc Execute(Tpm2 tpm, AuthSession sess, PolicyTree policy)
        {
            byte writtenName = IsNvIndexRequiredToHaveBeenWritten ? (byte)1 : (byte)0;
            tpm.PolicyNvWritten(sess, writtenName);
            return tpm._GetLastResponseCode();
        }

        public bool IsNvIndexRequiredToHaveBeenWritten;
    } // class TpmPolicyNvWritten
}

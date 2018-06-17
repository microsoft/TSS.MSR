/* 
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

namespace Tpm2Lib
{
    using System;
    using System.Text;
    using System.IO;
    using System.Collections.Generic;
    using System.Runtime.Serialization;
    using System.Runtime.Serialization.Json;
    using System.Xml.Serialization;

    public enum PolicySerializationFormat
    {
        Json,
        Xml
    }
    /// <summary>
    /// A PolicyTree contains machinery for creating, executing and 
    /// persisting TPM policy expression.
    /// </summary>
    public class PolicyTree
    {
        internal TpmHash PolicyHash;
        private PolicyAce PolicyRoot;
        private HashSet<string> BranchIdCollection;

        public TpmAlgId HashAlg
        {
            get { return PolicyHash.HashAlg; }

            set { PolicyHash = new TpmHash(value); }
        }

        TpmHash _Digest = null;

        public TpmHash Digest
        {
            get {
                if (_Digest == null)
                    _Digest = GetPolicyDigest();
                return _Digest; }
        }

        public PolicyTree(TpmAlgId hashAlgorithm)
        {
            HashAlg = hashAlgorithm;
        }

        // [Obsolete]
        public PolicyAce SetPolicyRoot(PolicyAce root)
        {
            PolicyRoot = root;
            _Digest = null;
            return PolicyRoot;
        }

        // [Obsolete]
        public PolicyAce GetPolicyRoot()
        {
            return PolicyRoot;
        }

        public PolicyTree InsertPolicyRoot(PolicyAce newRoot)
        {
            if (PolicyRoot != null)
                newRoot.AddNextAce(PolicyRoot);
            SetPolicyRoot(newRoot);
            return this;
        }

        /// <summary>
        /// A "normalized" policy is one transformed into disjunctive normal form,
        /// in which a collection  of policy "AND chains" is combined with PolicyOR
        /// before submission to the TPM.
        /// Callers must provide an-array-of-arrays of TpmPolicyACEs. The arrays may NOT 
        /// contain PolicyOr (these will be added automatically), but each array MUST be
        /// terminated  with a unique string identifier encoded in a TpmPolicyChainId.
        /// </summary>
        /// <param name="policy"></param>
        public void CreateNormalizedPolicy(PolicyAce[][] policy)
        {
            // To validate that the input does not have any repeated branchIds or ACEs
            var branchIdDict = new Dictionary<string, string>();
            var aces = new HashSet<object>();
            int numBranches = 0;
            bool unnamedBranches = false;

            // The following code validates and transforms the array-of-arrays into a linked
            // list + OR nodes tree. First collect lists of chains in the chains collection.
            var chains = new List<PolicyAce>();
            foreach (PolicyAce[] chain in policy)
            {
                numBranches++;
                PolicyAce leaf = null;
                PolicyAce previousAce = null;
                PolicyAce root = null;
                // Turn the array into a doubly-linked list
                foreach (PolicyAce ace in chain)
                {
                    // Repeats are illegal
                    if (aces.Contains(ace))
                    {
                        Globs.Throw<ArgumentException>("CreateNormalizedPolicy: " + 
                                                       "Repeated ACE in policy");
                    }

                    // Already associated with a session is illegal
                    if (ace.AssociatedPolicy != null)
                    {
                        Globs.Throw<ArgumentException>("CreateNormalizedPolicy: " +
                                            "ACE is already associated with a policy");
                    }

                    ace.AssociatedPolicy = this;
                    aces.Add(ace);

                    // OR is illegal in normal form (these are added automatically 
                    // at the root to union the arrays that are input to this function).
                    if (ace is TpmPolicyOr)
                    {
                        Globs.Throw<ArgumentException>("CreateNormalizedPolicy: " + 
                                            "Normalized form cannot contain TpmPolicyOr");
                    }

                    if (previousAce != null)
                    {
                        previousAce.NextAce = ace;
                    }

                    ace.PreviousAce = previousAce;
                    previousAce = ace;

                    // Is the branchId valid?
                    string branchId = ace.BranchID;
                    if (!String.IsNullOrEmpty(branchId))
                    {
                        if (branchIdDict.ContainsKey(branchId))
                        {
                            Globs.Throw<ArgumentException>("CreateNormalizedPolicy: " + 
                                            "Repeated branch-identifier " + branchId);
                        }
                        branchIdDict.Add(branchId, "");
                    }
                    if (root == null)
                    {
                        root = ace;
                    }
                    leaf = ace;
                }

                // Does the leaf have a branch ID?
                if (leaf != null && String.IsNullOrEmpty(leaf.BranchID))
                {
                    unnamedBranches = true;
                }

                // Else we have a good chain starting at root            
                chains.Add(root);
            }

            if (unnamedBranches && numBranches != 1)
            {
                throw new ArgumentException("Policy-chain leaf does not have a branch identifier");

            }

            // We now have a list of chains in chains.
            int numChains = chains.Count;

            // A single chain (no ORs)
            if (numChains == 1)
            {
                PolicyRoot = chains[0];
                return;
            }

            // Each TPM_or can take up to 8 inputs. We will add OR-aces to the root
            // to capture all chains. The algorithm is that we create an OR and keep
            // adding chains in the input order until full (if it is the last chain)
            // or one-less than full. Then create a new OR, attach it to the last OR
            // and keep filling as before.

            var theRoot = new TpmPolicyOr();
            TpmPolicyOr currentOrAce = theRoot;
            for (int j = 0; j < numChains; j++)
            {
                bool lastChain = (j == numChains - 1);
                if ((currentOrAce.PolicyBranches.Count < 7) || lastChain)
                {
                    currentOrAce.AddPolicyBranch(chains[j]);
                }
                else
                {
                    // We have overflowed the TpmPolicyOr so add a new child-OR
                    // attached to the previous as the final (8th) branch.
                    var nextOr = new TpmPolicyOr();
                    currentOrAce.AddPolicyBranch(nextOr);
                    currentOrAce = nextOr;
                    currentOrAce.AddPolicyBranch(chains[j]);
                }
            }
            // All input chains are connected up to one or more ORs at the root so we are done.
            PolicyRoot = theRoot;
        }

        /// <summary>
        /// Create a simple policy chain (no ORs). 
        /// </summary>
        public void Create(PolicyAce[] singlePolicyChain)
        {
            // ReSharper disable once RedundantExplicitArraySize
            var arr = new PolicyAce[1][] {singlePolicyChain};
            CreateNormalizedPolicy(arr);
        }

        /// <summary>
        /// Sets the current policy tree to a policy branch represented by its leaf ACE.
        /// A policy branch can be constructed by means of the following expressions:
        ///     new TpmAce1().And(new TpmAce2()).And(new TpmAce3());
        /// or
        ///     new TpmAce1().AddNextAce(new TpmAce2()).AddNextAce(new TpmAce3());
        /// </summary>
        public void Set (PolicyAce leaf)
        {
            if (leaf == null)
            {
                PolicyRoot = null;
                return;
            }
            // The construction policyTree.Set()
            // evaluates to ace4.  We have to go back to the root.
            if (String.IsNullOrEmpty(leaf.BranchID))
            {
                leaf.BranchID = "leaf";
            }
            do
            {
                PolicyRoot = leaf;
                leaf = leaf.PreviousAce;
            } while (leaf != null);
        }

        public TpmHash GetPolicyDigest()
        {
            PolicyAce dummyAce = null;

            if(null == PolicyRoot)
            {
                return new TpmHash(PolicyHash.HashAlg);
            }
            // First, check that the tree is OK. An exception is thrown if checks fail.
            CheckPolicy("", ref dummyAce);

            return PolicyRoot.GetPolicyDigest(PolicyHash.HashAlg);
        }

        public void ResetPolicyDigest()
        {
            PolicyHash = new TpmHash(PolicyHash.HashAlg);
        }

        internal bool AllowErrorsInPolicyEval;

        private bool PolicyContainsOrs;

        /// <summary>
        /// Check to see if all branches have an ID and that the IDs are unique.
        /// </summary>
        /// <param name="branchIdToFind"></param>
        /// <param name="matchingAce"></param>
        internal void CheckPolicy(string branchIdToFind, ref PolicyAce matchingAce)
        {
            PolicyContainsOrs = false;
            BranchIdCollection = new HashSet<string>();
            CheckBranchIDs(PolicyRoot, branchIdToFind, ref matchingAce);
        }

        internal void CheckBranchIDs(PolicyAce ace, string branchIdToFind,
                                     ref PolicyAce matchingAce) 
        {
            if (ace == null)
                return;

            ace.AssociatedPolicy = this;

            // ReSharper disable once CanBeReplacedWithTryCastAndCheckForNull
            if (ace is TpmPolicyOr)
            {
                // Go down each branch of the OR
                PolicyContainsOrs = true;
                var orAce = (TpmPolicyOr)ace;
                foreach (PolicyAce nextAce in orAce.PolicyBranches)
                {
                    CheckBranchIDs(nextAce, branchIdToFind, ref matchingAce);
                }
            }
            else
            {
                PolicyAce nextAce = ace.NextAce;
                if (nextAce != null)
                {
                    CheckBranchIDs(nextAce, branchIdToFind, ref matchingAce);
                    return;
                }

                // We are at the leaf. If there are no ORs in this chain then we are
                // done. If there  are ORs then check two things (1) that the leaf
                // has a non-empty BranchIdentifier, and (2) that the branchIdentifiers
                // are unique.
                if (!PolicyContainsOrs)
                {
                    matchingAce = ace;
                    if (String.IsNullOrEmpty(ace.BranchID))
                    {
                        ace.BranchID = "leaf";
                    }
                    return;
                }

                if (String.IsNullOrEmpty(ace.BranchID))
                {
                    Globs.Throw("CheckPolicyIdInternal: Branch leaf does not have a BranchID");
                }

                if (BranchIdCollection.Contains(ace.BranchID))
                {
                    Globs.Throw("CheckPolicyIdInternal: Replicated BranchID " + ace.BranchID);
                }

                BranchIdCollection.Add(ace.BranchID);
                if (ace.BranchID == branchIdToFind)
                {
                    matchingAce = ace;
                }
            }
        }

        public PolicyAce GetAce(string nodeIdentifier)
        {
            return FindNodeId(PolicyRoot, nodeIdentifier);
        }

        private PolicyAce FindNodeId(PolicyAce ace, string nodeId)
        {
            if (ace.NodeId == nodeId)
                return ace;

            if (ace is TpmPolicyOr)
            {
                foreach (PolicyAce branchRoot in ((TpmPolicyOr)ace).PolicyBranches)
                {
                    PolicyAce a = FindNodeId(branchRoot, nodeId);
                    if (a != null)
                        return a;
                }
            }

            return ace.NextAce == null ? null : FindNodeId(ace.NextAce, nodeId);
        }

#region serialization

        /// <summary>
        /// Create a serialization of the current policy object in a stream (e.g. MemoryStream or FileStream)
        /// </summary>
        /// <param name="policyIdentifier"></param>
        /// <param name="format"></param>
        /// <param name="targetStream"></param>
        public void Serialize(string policyIdentifier, PolicySerializationFormat format, Stream targetStream)
        {
            var p = new TpmPolicy(this);
            p.Create(policyIdentifier);
            
            switch(format)
            {
                case PolicySerializationFormat.Xml:
                {
                    var ser = new DataContractSerializer(typeof(TpmPolicy));
                    ser.WriteObject(targetStream, p);
                    break;
                }
                case PolicySerializationFormat.Json:
                {
                    var ser = new DataContractJsonSerializer(typeof(TpmPolicy));
                    ser.WriteObject(targetStream, p);
                    break;
                }
                default:
                    Globs.Throw<ArgumentException>("PolicyTree.Serialize: Unknown format " + format);
                    break;
            }
            targetStream.Flush();
        }

        /// <summary>
        /// Load a policy from a stream (MemoryStream, FileStream) in the specified format
        /// </summary>
        /// <param name="format"></param>
        /// <param name="sourceStream"></param>
        public void Deserialize(PolicySerializationFormat format, Stream sourceStream)
        {
            TpmPolicy pol = null;
            switch (format)
            {
                case PolicySerializationFormat.Xml:
                {
                    var ser = new DataContractSerializer(typeof(TpmPolicy));
                    pol = (TpmPolicy)ser.ReadObject(sourceStream);
                    break;
                }
                case PolicySerializationFormat.Json:
                {
                    var ser = new DataContractJsonSerializer(typeof(TpmPolicy));
                    pol = (TpmPolicy)ser.ReadObject(sourceStream);
                    break;
                }
                default:
                    Globs.Throw<ArgumentException>("PolicyTree.Deserialize: Unknown format " + format);
                    return;
            }
            pol.AssociatedPolicy = this;
            PolicyRoot = pol.PolicyRoot;
        }

        public string SerializeToString(string policyIdentifier, PolicySerializationFormat fmt)
        {
            MemoryStream s = new MemoryStream();
            Serialize(policyIdentifier, fmt, s);
            return Encoding.UTF8.GetString(s.ToArray());
        }

        public void SerializeToFile(string policyIdentifier, PolicySerializationFormat fmt, string fileName)
        {
            string s = SerializeToString(policyIdentifier, fmt);
            File.WriteAllText(fileName, s);
        }

        public void DeserializeFromString(PolicySerializationFormat fmt, string stream)
        {
            MemoryStream s = new MemoryStream(Encoding.UTF8.GetBytes(stream));
            Deserialize(fmt, s);
        }

        public void DeserializeFromFile(PolicySerializationFormat fmt, string fileName)
        {
            using (FileStream s = new FileStream(fileName, FileMode.Open))
            {
                Deserialize(fmt, s);
            }
        }

#endregion

#region callbacks

        //
        // Policies like PolicyCommandCode and PolicyLocality can be executed without
        // any additional support. However some policies need a key-holder to sign
        // or hash a data structure to authenticate a command or satisfy a policy.
        // TSS.Net accommodates this through callbacks in the PolicyTree object.
        //
        // There are currently 3 callbacks for signing, NV-access, and a "dummy"
        // callback to support other actions.The callbacks should be installed
        // before a policy is executed. 
        // 
        // The library supports association of a string called a NodeID with any ACE
        // and this can be used to provide additional context (e.g. to distinguish
        // a request to use a corporate smartcard from a personal one).
        //

#region PolicySingedCallback

        public delegate ISignatureUnion SignDelegate(PolicyTree policy, TpmPolicySigned ace,
                                                     byte[] nonceTpm,
                                                     out TpmPublic sigVerifier);

        private SignDelegate SignerCallback;

        public void SetSignerCallback(SignDelegate signer)
        {
            SignerCallback = signer;
        }

        /// <summary>
        /// This is called from TpmPolicySigned when an external caller must sign the session data.  
        /// </summary>
        /// <returns></returns>
        internal ISignatureUnion ExecuteSignerCallback(TpmPolicySigned ace, byte[] nonceTpm,
                                                       out TpmPublic verificationKey)
        {
            if (SignerCallback == null)
            {
                Globs.Throw("No policy signer callback installed.");
                verificationKey = new TpmPublic();
                return null;
            }

            return SignerCallback(this, ace, nonceTpm, out verificationKey);
        }

        /// <summary>
        /// This is a helper used by signature callbacks to properly marshal data to be
        /// hashed before signing.
        /// </summary>
        public static byte[] PackDataToSign(int expirationTime, byte[] nonceTpm,
                                            byte[] cpHash, byte[] policyRef)
        {
            var dataToSign = new Marshaller();
            dataToSign.Put(nonceTpm, "");
            dataToSign.Put(expirationTime, "");
            dataToSign.Put(cpHash, "");
            dataToSign.Put(policyRef, "");
            return dataToSign.GetBytes();
        }

#endregion // PolicySingedCallback

#region PolicyNvCallback

        public delegate void PolicyNVDelegate(PolicyTree policy, TpmPolicyNV ace,
                                              out SessionBase authorizingSession,
                                              out TpmHandle authorizedEntityHandle,
                                              out TpmHandle nvHandle);

        private PolicyNVDelegate PolicyNVCallback;

        public void SetNvCallback(PolicyNVDelegate policyNvCallback)
        {
            PolicyNVCallback = policyNvCallback;
        }

        /// <summary>
        /// Called from TpmPolicyNV.
        /// </summary>
        /// <returns></returns>
        internal void ExecutePolicyNvCallback(TpmPolicyNV ace, out TpmHandle authHandle, out TpmHandle nvHandle, out SessionBase authSession)
        {
            if (PolicyNVCallback == null)
            {
                Globs.Throw("No policyNV callback installed.");
                authHandle = new TpmHandle();
                nvHandle = new TpmHandle();
                authSession = new AuthSession(new TpmHandle());
                return;
            }
            PolicyNVCallback(this, ace, out authSession, out authHandle, out nvHandle);
        }

#endregion //PolicyNvCallback

#region PolicyActionCallback

        public delegate void PolicyActionDelegate(PolicyTree policy, TpmPolicyAction ace);

        public void SetPolicyActionCallback(PolicyActionDelegate policyActionCallback)
        {
            PolicyActionCallback = policyActionCallback;
        }

        private PolicyActionDelegate PolicyActionCallback;

        internal void ExecutePolicyActionCallback(TpmPolicyAction ace)
        {
            if (PolicyActionCallback == null)
            {
                Globs.Throw("No policyAction callback installed.");
                return;
            }
            PolicyActionCallback(this, ace);
        }

#endregion // PolicyActionCallback

#endregion // callbacks
    } // class PolicyTree

#region TpmPolicyClass

    /// <summary>
    /// The PolicySerializer is a helper-class that creates and consumes a (proposed-standard)
    /// XML-form for policy expressions. In the general case policy-trees are written as a tree
    /// built of arrays-of-arrays of policy-ACEs. If a policy ACE is not a PolicyOr then there
    /// are no sub-arrays. If a policy ACE is a policyOr, then there is a sub-array. Every leaf
    /// element has a string-identifier (unique for this tree). The policy tree has a string-
    /// name and the policy-hash that the tree represents (this is created on serialization
    /// and checked when deserialized).
    /// </summary>
    [DataContract]
    [KnownType(typeof(TpmAlgId))]
    [KnownType(typeof(PolicyAce))]
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
    [KnownType(typeof(TpmHash))]
    [KnownType(typeof(LocalityAttr))]
    [KnownType(typeof(SignatureRsassa))]
    [KnownType(typeof(SignatureRsapss))]
    [KnownType(typeof(SignatureEcdsa))]
    [KnownType(typeof(SignatureEcdaa))]
    public class TpmPolicy
    {
        public TpmPolicy()
        {
        }

        internal TpmPolicy(PolicyTree policy)
        {
            AssociatedPolicy = policy;
            PolicyRoot = policy.GetPolicyRoot();
            PolicyHash = policy.GetPolicyDigest();
        }

        /// <summary>
        /// Create a representation of the associated policy-tree that is suitable for serialization
        /// </summary>
        /// <param name="name"></param>
        internal void Create(string name)
        {
            PolicyName = name;
        }

        private TpmHash PolicyHash;

        internal PolicyAce PolicyRoot;

        internal PolicyTree AssociatedPolicy;

        [MarshalAs(0)]
        [DataMember()]
        public string PolicyName;

        [MarshalAs(1)]
        [DataMember()]
        public TpmAlgId HashAlgorithm
        {
            get
            {
                return PolicyHash.HashAlg;
            }
            set
            {
                PolicyHash = new TpmHash(value);
            }
        }

        [MarshalAs(2)]
        [DataMember()]
        public byte[] PolicyDigest
        {
            get
            {
                return PolicyHash.HashData;
            }
            set
            {
                if (PolicyHash == null)
                {
                    Globs.Throw("TpmPolicy.PolicyDigest: No hash algorithm");
                }
                PolicyHash.HashData = Globs.CopyData(value);
            }
        }

        [MarshalAs(3)]
        [DataMember()]
        public PolicyAce[] Policy
        {
            get
            {
                return GetArrayRepresentation(PolicyRoot);
            }
            set
            {
                PolicyRoot = FromArrayRepresentation(value, AssociatedPolicy);
            }
        }

        // Returns the linked list as an array
        internal static PolicyAce[] GetArrayRepresentation(PolicyAce head)
        {
            int numElems = 0;
            PolicyAce next = head;
            do
            {
                if (next == null)
                {
                    break;
                }
                next = next.NextAce;
                numElems++;
            } while (true);

            var arr = new PolicyAce[numElems];
            int count = 0;
            next = head;
            do
            {
                if (next == null)
                {
                    break;
                }
                arr[count] = next;
                next = next.NextAce;
                count++;
            } while (true);

            return arr;
        }

        internal static PolicyAce FromArrayRepresentation(PolicyAce[] arr, PolicyTree policy)
        {
            PolicyAce root = null;
            PolicyAce current = null;
            PolicyAce previous = null;
            // Makes a doubly linked list from an array
            foreach (PolicyAce a in arr)
            {
                // All ACEs have an associated policy tree
                a.AssociatedPolicy = policy;
                // we need a link to previous
                if (previous != null)
                {
                    a.PreviousAce = previous;
                }
                
                // previous needs a link to us
                if (current != null)
                {
                    current.NextAce = a;
                }

                current = a;
                if (root == null)
                {
                    root = current;
                }
                previous = a;
            }
            return root;
        }
    }

#endregion
}

 
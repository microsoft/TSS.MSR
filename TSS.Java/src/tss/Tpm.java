package tss;

import tss.tpm.*;


// -----------This is an auto-generated file: do not edit

//>>>
/**
* The Tpm class provides Java functions to program a TPM.<p>TPM-defined functions have names like TPM2_PCR_Read(): the TPM2_ prefix is dropped in the Java definition of these functions: e.g. PCR_Read().<p>The Tpm and TpmBase classes also provide a few helper-functions: for example, the command _allowErrors() tells Tpm to not throw an exception if the Next TPM command returns an error.<p>Tpm objects must be "connected" to a physical TPM or simulator using the _setDevice() method. Some devices (like the TPM simulator) need to be configured before they can be used. See the sample code that is part of the TSS.Java distribution for more information.
*/
public class Tpm extends TpmBase
{
    /**
     * TPM2_Startup() is always preceded by _TPM_Init, which is the physical indication that TPM initialization is necessary because of a system-wide reset. TPM2_Startup() is only valid after _TPM_Init. Additional TPM2_Startup() commands are not allowed after it has completed successfully. If a TPM requires TPM2_Startup() and another command is received, or if the TPM receives TPM2_Startup() when it is not required, the TPM shall return TPM_RC_INITIALIZE.
     * 
     * @param startupType TPM_SU_CLEAR or TPM_SU_STATE
     */
    public void Startup(TPM_SU startupType)
    {
        TPM2_Startup_REQUEST inStruct = new TPM2_Startup_REQUEST();
        inStruct.startupType = startupType;
        DispatchCommand(TPM_CC.Startup, new TPM_HANDLE[] {}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command is used to prepare the TPM for a power cycle. The shutdownType parameter indicates how the subsequent TPM2_Startup() will be processed.
     * 
     * @param shutdownType TPM_SU_CLEAR or TPM_SU_STATE
     */
    public void Shutdown(TPM_SU shutdownType)
    {
        TPM2_Shutdown_REQUEST inStruct = new TPM2_Shutdown_REQUEST();
        inStruct.shutdownType = shutdownType;
        DispatchCommand(TPM_CC.Shutdown, new TPM_HANDLE[] {}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command causes the TPM to perform a test of its capabilities. If the fullTest is YES, the TPM will test all functions. If fullTest = NO, the TPM will only test those functions that have not previously been tested.
     * 
     * @param fullTest YES if full test to be performed NO if only test of untested functions required
     */
    public void SelfTest(byte fullTest)
    {
        TPM2_SelfTest_REQUEST inStruct = new TPM2_SelfTest_REQUEST();
        inStruct.fullTest = fullTest;
        DispatchCommand(TPM_CC.SelfTest, new TPM_HANDLE[] {}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command causes the TPM to perform a test of the selected algorithms.
     * 
     * @param toTest list of algorithms that should be tested 
     * @return list of algorithms that need testing
     */
    public TPM_ALG_ID[] IncrementalSelfTest(TPM_ALG_ID[] toTest)
    {
        TPM2_IncrementalSelfTest_REQUEST inStruct = new TPM2_IncrementalSelfTest_REQUEST();
        IncrementalSelfTestResponse outStruct = new IncrementalSelfTestResponse();
        inStruct.toTest = toTest;
        DispatchCommand(TPM_CC.IncrementalSelfTest, new TPM_HANDLE[] {}, 0, 0, inStruct, outStruct);
        return outStruct.toDoList;
    }
    
    /**
     * This command returns manufacturer-specific information regarding the results of a self-test and an indication of the test status.
     * 
     * @return TPM2_GetTestResult_RESPONSE{(ul)(li)(code)outData(/code) - test result data contains manufacturer-specific information(/li)(li)(code)testResult(/code) - -(/li)(/ul)}
     */
    public GetTestResultResponse GetTestResult()
    {
        TPM2_GetTestResult_REQUEST inStruct = new TPM2_GetTestResult_REQUEST();
        GetTestResultResponse outStruct = new GetTestResultResponse();
        DispatchCommand(TPM_CC.GetTestResult, new TPM_HANDLE[] {}, 0, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command is used to start an authorization session using alternative methods of establishing the session key (sessionKey). The session key is then used to derive values used for authorization and for encrypting parameters.
     * 
     * @param tpmKey handle of a loaded decrypt key used to encrypt salt may be TPM_RH_NULL Auth Index: None 
     * @param bind entity providing the authValue may be TPM_RH_NULL Auth Index: None 
     * @param nonceCaller initial nonceCaller, sets nonceTPM size for the session shall be at least 16 octets 
     * @param encryptedSalt value encrypted according to the type of tpmKey If tpmKey is TPM_RH_NULL, this shall be the Empty Buffer. 
     * @param sessionType indicates the type of the session; simple HMAC or policy (including a trial policy) 
     * @param symmetric the algorithm and key size for parameter encryption may select TPM_ALG_NULL 
     * @param authHash hash algorithm to use for the session Shall be a hash algorithm supported by the TPM and not TPM_ALG_NULL 
     * @return TPM2_StartAuthSession_RESPONSE{(ul)(li)(code)handle(/code) - handle for the newly created session(/li)(li)(code)nonceTPM(/code) - the initial nonce from the TPM, used in the computation of the sessionKey(/li)(/ul)}
     */
    public StartAuthSessionResponse StartAuthSession(TPM_HANDLE tpmKey,TPM_HANDLE bind,byte[] nonceCaller,byte[] encryptedSalt,TPM_SE sessionType,TPMT_SYM_DEF symmetric,TPM_ALG_ID authHash)
    {
        TPM2_StartAuthSession_REQUEST inStruct = new TPM2_StartAuthSession_REQUEST();
        StartAuthSessionResponse outStruct = new StartAuthSessionResponse();
        inStruct.tpmKey = tpmKey;
        inStruct.bind = bind;
        inStruct.nonceCaller = nonceCaller;
        inStruct.encryptedSalt = encryptedSalt;
        inStruct.sessionType = sessionType;
        inStruct.symmetric = symmetric;
        inStruct.authHash = authHash;
        DispatchCommand(TPM_CC.StartAuthSession, new TPM_HANDLE[] {tpmKey,bind}, 0, 1, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command allows a policy authorization session to be returned to its initial state. This command is used after the TPM returns TPM_RC_PCR_CHANGED. That response code indicates that a policy will fail because the PCR have changed after TPM2_PolicyPCR() was executed. Restarting the session allows the authorizations to be replayed because the session restarts with the same nonceTPM. If the PCR are valid for the policy, the policy may then succeed.
     * 
     * @param sessionHandle the handle for the policy session
     */
    public void PolicyRestart(TPM_HANDLE sessionHandle)
    {
        TPM2_PolicyRestart_REQUEST inStruct = new TPM2_PolicyRestart_REQUEST();
        inStruct.sessionHandle = sessionHandle;
        DispatchCommand(TPM_CC.PolicyRestart, new TPM_HANDLE[] {sessionHandle}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command is used to create an object that can be loaded into a TPM using TPM2_Load(). If the command completes successfully, the TPM will create the new object and return the objects creation data (creationData), its public area (outPublic), and its encrypted sensitive area (outPrivate). Preservation of the returned data is the responsibility of the caller. The object will need to be loaded (TPM2_Load()) before it may be used. The only difference between the inPublic TPMT_PUBLIC template and the outPublic TPMT_PUBLIC object is in the unique field.
     * 
     * @param parentHandle handle of parent for new object Auth Index: 1 Auth Role: USER 
     * @param inSensitive the sensitive data 
     * @param inPublic the public template 
     * @param outsideInfo data that will be included in the creation data for this object to provide permanent, verifiable linkage between this object and some object owner data 
     * @param creationPCR PCR that will be used in creation data 
     * @return TPM2_Create_RESPONSE{(ul)(li)(code)outPrivate(/code) - the private portion of the object(/li)(li)(code)outPublic(/code) - the public portion of the created object(/li)(li)(code)creationData(/code) - contains a TPMS_CREATION_DATA(/li)(li)(code)creationHash(/code) - digest of creationData using nameAlg of outPublic(/li)(li)(code)creationTicket(/code) - ticket used by TPM2_CertifyCreation() to validate that the creation data was produced by the TPM(/li)(/ul)}
     */
    public CreateResponse Create(TPM_HANDLE parentHandle,TPMS_SENSITIVE_CREATE inSensitive,TPMT_PUBLIC inPublic,byte[] outsideInfo,TPMS_PCR_SELECTION[] creationPCR)
    {
        TPM2_Create_REQUEST inStruct = new TPM2_Create_REQUEST();
        CreateResponse outStruct = new CreateResponse();
        inStruct.parentHandle = parentHandle;
        inStruct.inSensitive = inSensitive;
        inStruct.inPublic = inPublic;
        inStruct.outsideInfo = outsideInfo;
        inStruct.creationPCR = creationPCR;
        DispatchCommand(TPM_CC.Create, new TPM_HANDLE[] {parentHandle}, 1, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command is used to load objects into the TPM. This command is used when both a TPM2B_PUBLIC and TPM2B_PRIVATE are to be loaded. If only a TPM2B_PUBLIC is to be loaded, the TPM2_LoadExternal command is used.
     * 
     * @param parentHandle TPM handle of parent key; shall not be a reserved handle Auth Index: 1 Auth Role: USER 
     * @param inPrivate the private portion of the object 
     * @param inPublic the public portion of the object 
     * @return handle of type TPM_HT_TRANSIENT for the loaded object
     */
    public TPM_HANDLE Load(TPM_HANDLE parentHandle,TPM2B_PRIVATE inPrivate,TPMT_PUBLIC inPublic)
    {
        TPM2_Load_REQUEST inStruct = new TPM2_Load_REQUEST();
        LoadResponse outStruct = new LoadResponse();
        inStruct.parentHandle = parentHandle;
        inStruct.inPrivate = inPrivate;
        inStruct.inPublic = inPublic;
        DispatchCommand(TPM_CC.Load, new TPM_HANDLE[] {parentHandle}, 1, 1, inStruct, outStruct);
        return outStruct.handle;
    }
    
    /**
     * This command is used to load an object that is not a Protected Object into the TPM. The command allows loading of a public area or both a public and sensitive area.
     * 
     * @param inPrivate the sensitive portion of the object (optional) 
     * @param inPublic the public portion of the object 
     * @param hierarchy hierarchy with which the object area is associated 
     * @return handle of type TPM_HT_TRANSIENT for the loaded object
     */
    public TPM_HANDLE LoadExternal(TPMT_SENSITIVE inPrivate,TPMT_PUBLIC inPublic,TPM_HANDLE hierarchy)
    {
        TPM2_LoadExternal_REQUEST inStruct = new TPM2_LoadExternal_REQUEST();
        LoadExternalResponse outStruct = new LoadExternalResponse();
        inStruct.inPrivate = inPrivate;
        inStruct.inPublic = inPublic;
        inStruct.hierarchy = hierarchy;
        DispatchCommand(TPM_CC.LoadExternal, new TPM_HANDLE[] {}, 0, 1, inStruct, outStruct);
        return outStruct.handle;
    }
    
    /**
     * This command allows access to the public area of a loaded object.
     * 
     * @param objectHandle TPM handle of an object Auth Index: None 
     * @return TPM2_ReadPublic_RESPONSE{(ul)(li)(code)outPublic(/code) - structure containing the public area of an object(/li)(li)(code)name(/code) - name of the object(/li)(li)(code)qualifiedName(/code) - the Qualified Name of the object(/li)(/ul)}
     */
    public ReadPublicResponse ReadPublic(TPM_HANDLE objectHandle)
    {
        TPM2_ReadPublic_REQUEST inStruct = new TPM2_ReadPublic_REQUEST();
        ReadPublicResponse outStruct = new ReadPublicResponse();
        inStruct.objectHandle = objectHandle;
        DispatchCommand(TPM_CC.ReadPublic, new TPM_HANDLE[] {objectHandle}, 0, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command enables the association of a credential with an object in a way that ensures that the TPM has validated the parameters of the credentialed object.
     * 
     * @param activateHandle handle of the object associated with certificate in credentialBlob Auth Index: 1 Auth Role: ADMIN 
     * @param keyHandle loaded key used to decrypt the TPMS_SENSITIVE in credentialBlob Auth Index: 2 Auth Role: USER 
     * @param credentialBlob the credential 
     * @param secret keyHandle algorithm-dependent encrypted seed that protects credentialBlob 
     * @return the decrypted certificate information the data should be no larger than the size of the digest of the nameAlg associated with keyHandle
     */
    public byte[] ActivateCredential(TPM_HANDLE activateHandle,TPM_HANDLE keyHandle,TPMS_ID_OBJECT credentialBlob,byte[] secret)
    {
        TPM2_ActivateCredential_REQUEST inStruct = new TPM2_ActivateCredential_REQUEST();
        ActivateCredentialResponse outStruct = new ActivateCredentialResponse();
        inStruct.activateHandle = activateHandle;
        inStruct.keyHandle = keyHandle;
        inStruct.credentialBlob = credentialBlob;
        inStruct.secret = secret;
        DispatchCommand(TPM_CC.ActivateCredential, new TPM_HANDLE[] {activateHandle,keyHandle}, 2, 0, inStruct, outStruct);
        return outStruct.certInfo;
    }
    
    /**
     * This command allows the TPM to perform the actions required of a Certificate Authority (CA) in creating a TPM2B_ID_OBJECT containing an activation credential.
     * 
     * @param handle loaded public area, used to encrypt the sensitive area containing the credential key Auth Index: None 
     * @param credential the credential information 
     * @param objectName Name of the object to which the credential applies 
     * @return TPM2_MakeCredential_RESPONSE{(ul)(li)(code)credentialBlob(/code) - the credential(/li)(li)(code)secret(/code) - handle algorithm-dependent data that wraps the key that encrypts credentialBlob(/li)(/ul)}
     */
    public MakeCredentialResponse MakeCredential(TPM_HANDLE handle,byte[] credential,byte[] objectName)
    {
        TPM2_MakeCredential_REQUEST inStruct = new TPM2_MakeCredential_REQUEST();
        MakeCredentialResponse outStruct = new MakeCredentialResponse();
        inStruct.handle = handle;
        inStruct.credential = credential;
        inStruct.objectName = objectName;
        DispatchCommand(TPM_CC.MakeCredential, new TPM_HANDLE[] {handle}, 0, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command returns the data in a loaded Sealed Data Object.
     * 
     * @param itemHandle handle of a loaded data object Auth Index: 1 Auth Role: USER 
     * @return unsealed data Size of outData is limited to be no more than 128 octets.
     */
    public byte[] Unseal(TPM_HANDLE itemHandle)
    {
        TPM2_Unseal_REQUEST inStruct = new TPM2_Unseal_REQUEST();
        UnsealResponse outStruct = new UnsealResponse();
        inStruct.itemHandle = itemHandle;
        DispatchCommand(TPM_CC.Unseal, new TPM_HANDLE[] {itemHandle}, 1, 0, inStruct, outStruct);
        return outStruct.outData;
    }
    
    /**
     * This command is used to change the authorization secret for a TPM-resident object.
     * 
     * @param objectHandle handle of the object Auth Index: 1 Auth Role: ADMIN 
     * @param parentHandle handle of the parent Auth Index: None 
     * @param newAuth new authorization value 
     * @return private area containing the new authorization value
     */
    public TPM2B_PRIVATE ObjectChangeAuth(TPM_HANDLE objectHandle,TPM_HANDLE parentHandle,byte[] newAuth)
    {
        TPM2_ObjectChangeAuth_REQUEST inStruct = new TPM2_ObjectChangeAuth_REQUEST();
        ObjectChangeAuthResponse outStruct = new ObjectChangeAuthResponse();
        inStruct.objectHandle = objectHandle;
        inStruct.parentHandle = parentHandle;
        inStruct.newAuth = newAuth;
        DispatchCommand(TPM_CC.ObjectChangeAuth, new TPM_HANDLE[] {objectHandle,parentHandle}, 1, 0, inStruct, outStruct);
        return outStruct.outPrivate;
    }
    
    /**
     * This command creates an object and loads it in the TPM. This command allows creation of any type of object (Primary, Ordinary, or Derived) depending on the type of parentHandle. If parentHandle references a Primary Seed, then a Primary Object is created; if parentHandle references a Storage Parent, then an Ordinary Object is created; and if parentHandle references a Derivation Parent, then a Derived Object is generated.
     * 
     * @param parentHandle Handle of a transient storage key, a persistent storage key, TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM+{PP}, or TPM_RH_NULL Auth Index: 1 Auth Role: USER 
     * @param inSensitive the sensitive data, see TPM 2.0 Part 1 Sensitive Values 
     * @param inPublic the public template 
     * @return TPM2_CreateLoaded_RESPONSE{(ul)(li)(code)handle(/code) - handle of type TPM_HT_TRANSIENT for created object(/li)(li)(code)outPrivate(/code) - the sensitive area of the object (optional)(/li)(li)(code)outPublic(/code) - the public portion of the created object(/li)(li)(code)name(/code) - the name of the created object(/li)(/ul)}
     */
    public CreateLoadedResponse CreateLoaded(TPM_HANDLE parentHandle,TPMS_SENSITIVE_CREATE inSensitive,byte[] inPublic)
    {
        TPM2_CreateLoaded_REQUEST inStruct = new TPM2_CreateLoaded_REQUEST();
        CreateLoadedResponse outStruct = new CreateLoadedResponse();
        inStruct.parentHandle = parentHandle;
        inStruct.inSensitive = inSensitive;
        inStruct.inPublic = inPublic;
        DispatchCommand(TPM_CC.CreateLoaded, new TPM_HANDLE[] {parentHandle}, 1, 1, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command duplicates a loaded object so that it may be used in a different hierarchy. The new parent key for the duplicate may be on the same or different TPM or TPM_RH_NULL. Only the public area of newParentHandle is required to be loaded.
     * 
     * @param objectHandle loaded object to duplicate Auth Index: 1 Auth Role: DUP 
     * @param newParentHandle shall reference the public area of an asymmetric key Auth Index: None 
     * @param encryptionKeyIn optional symmetric encryption key The size for this key is set to zero when the TPM is to generate the key. This parameter may be encrypted. 
     * @param symmetricAlg definition for the symmetric algorithm to be used for the inner wrapper may be TPM_ALG_NULL if no inner wrapper is applied 
     * @return TPM2_Duplicate_RESPONSE{(ul)(li)(code)encryptionKeyOut(/code) - If the caller provided an encryption key or if symmetricAlg was TPM_ALG_NULL, then this will be the Empty Buffer; otherwise, it shall contain the TPM-generated, symmetric encryption key for the inner wrapper.(/li)(li)(code)duplicate(/code) - private area that may be encrypted by encryptionKeyIn; and may be doubly encrypted(/li)(li)(code)outSymSeed(/code) - seed protected by the asymmetric algorithms of new parent (NP)(/li)(/ul)}
     */
    public DuplicateResponse Duplicate(TPM_HANDLE objectHandle,TPM_HANDLE newParentHandle,byte[] encryptionKeyIn,TPMT_SYM_DEF_OBJECT symmetricAlg)
    {
        TPM2_Duplicate_REQUEST inStruct = new TPM2_Duplicate_REQUEST();
        DuplicateResponse outStruct = new DuplicateResponse();
        inStruct.objectHandle = objectHandle;
        inStruct.newParentHandle = newParentHandle;
        inStruct.encryptionKeyIn = encryptionKeyIn;
        inStruct.symmetricAlg = symmetricAlg;
        DispatchCommand(TPM_CC.Duplicate, new TPM_HANDLE[] {objectHandle,newParentHandle}, 1, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command allows the TPM to serve in the role as a Duplication Authority. If proper authorization for use of the oldParent is provided, then an HMAC key and a symmetric key are recovered from inSymSeed and used to integrity check and decrypt inDuplicate. A new protection seed value is generated according to the methods appropriate for newParent and the blob is re-encrypted and a new integrity value is computed. The re-encrypted blob is returned in outDuplicate and the symmetric key returned in outSymKey.
     * 
     * @param oldParent parent of object Auth Index: 1 Auth Role: User 
     * @param newParent new parent of the object Auth Index: None 
     * @param inDuplicate an object encrypted using symmetric key derived from inSymSeed 
     * @param name the Name of the object being rewrapped 
     * @param inSymSeed the seed for the symmetric key and HMAC key needs oldParent private key to recover the seed and generate the symmetric key 
     * @return TPM2_Rewrap_RESPONSE{(ul)(li)(code)outDuplicate(/code) - an object encrypted using symmetric key derived from outSymSeed(/li)(li)(code)outSymSeed(/code) - seed for a symmetric key protected by newParent asymmetric key(/li)(/ul)}
     */
    public RewrapResponse Rewrap(TPM_HANDLE oldParent,TPM_HANDLE newParent,TPM2B_PRIVATE inDuplicate,byte[] name,byte[] inSymSeed)
    {
        TPM2_Rewrap_REQUEST inStruct = new TPM2_Rewrap_REQUEST();
        RewrapResponse outStruct = new RewrapResponse();
        inStruct.oldParent = oldParent;
        inStruct.newParent = newParent;
        inStruct.inDuplicate = inDuplicate;
        inStruct.name = name;
        inStruct.inSymSeed = inSymSeed;
        DispatchCommand(TPM_CC.Rewrap, new TPM_HANDLE[] {oldParent,newParent}, 1, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command allows an object to be encrypted using the symmetric encryption values of a Storage Key. After encryption, the object may be loaded and used in the new hierarchy. The imported object (duplicate) may be singly encrypted, multiply encrypted, or unencrypted.
     * 
     * @param parentHandle the handle of the new parent for the object Auth Index: 1 Auth Role: USER 
     * @param encryptionKey the optional symmetric encryption key used as the inner wrapper for duplicate If symmetricAlg is TPM_ALG_NULL, then this parameter shall be the Empty Buffer. 
     * @param objectPublic the public area of the object to be imported This is provided so that the integrity value for duplicate and the object attributes can be checked. NOTE Even if the integrity value of the object is not checked on input, the object Name is required to create the integrity value for the imported object. 
     * @param duplicate the symmetrically encrypted duplicate object that may contain an inner symmetric wrapper 
     * @param inSymSeed the seed for the symmetric key and HMAC key inSymSeed is encrypted/encoded using the algorithms of newParent. 
     * @param symmetricAlg definition for the symmetric algorithm to use for the inner wrapper If this algorithm is TPM_ALG_NULL, no inner wrapper is present and encryptionKey shall be the Empty Buffer. 
     * @return the sensitive area encrypted with the symmetric key of parentHandle
     */
    public TPM2B_PRIVATE Import(TPM_HANDLE parentHandle,byte[] encryptionKey,TPMT_PUBLIC objectPublic,TPM2B_PRIVATE duplicate,byte[] inSymSeed,TPMT_SYM_DEF_OBJECT symmetricAlg)
    {
        TPM2_Import_REQUEST inStruct = new TPM2_Import_REQUEST();
        ImportResponse outStruct = new ImportResponse();
        inStruct.parentHandle = parentHandle;
        inStruct.encryptionKey = encryptionKey;
        inStruct.objectPublic = objectPublic;
        inStruct.duplicate = duplicate;
        inStruct.inSymSeed = inSymSeed;
        inStruct.symmetricAlg = symmetricAlg;
        DispatchCommand(TPM_CC.Import, new TPM_HANDLE[] {parentHandle}, 1, 0, inStruct, outStruct);
        return outStruct.outPrivate;
    }
    
    /**
     * This command performs RSA encryption using the indicated padding scheme according to IETF RFC 8017. If the scheme of keyHandle is TPM_ALG_NULL, then the caller may use inScheme to specify the padding scheme. If scheme of keyHandle is not TPM_ALG_NULL, then inScheme shall either be TPM_ALG_NULL or be the same as scheme (TPM_RC_SCHEME).
     * 
     * @param keyHandle reference to public portion of RSA key to use for encryption Auth Index: None 
     * @param message message to be encrypted NOTE 1 The data type was chosen because it limits the overall size of the input to no greater than the size of the largest RSA public key. This may be larger than allowed for keyHandle. 
     * @param inScheme the padding scheme to use if scheme associated with keyHandle is TPM_ALG_NULL 
     * @param label optional label L to be associated with the message Size of the buffer is zero if no label is present NOTE 2 See description of label above. 
     * @return encrypted output
     */
    public byte[] RSA_Encrypt(TPM_HANDLE keyHandle,byte[] message,TPMU_ASYM_SCHEME inScheme,byte[] label)
    {
        TPM2_RSA_Encrypt_REQUEST inStruct = new TPM2_RSA_Encrypt_REQUEST();
        RSA_EncryptResponse outStruct = new RSA_EncryptResponse();
        inStruct.keyHandle = keyHandle;
        inStruct.message = message;
        inStruct.inScheme = inScheme;
        inStruct.label = label;
        DispatchCommand(TPM_CC.RSA_Encrypt, new TPM_HANDLE[] {keyHandle}, 0, 0, inStruct, outStruct);
        return outStruct.outData;
    }
    
    /**
     * This command performs RSA decryption using the indicated padding scheme according to IETF RFC 8017 ((PKCS#1).
     * 
     * @param keyHandle RSA key to use for decryption Auth Index: 1 Auth Role: USER 
     * @param cipherText cipher text to be decrypted NOTE An encrypted RSA data block is the size of the public modulus. 
     * @param inScheme the padding scheme to use if scheme associated with keyHandle is TPM_ALG_NULL 
     * @param label label whose association with the message is to be verified 
     * @return decrypted output
     */
    public byte[] RSA_Decrypt(TPM_HANDLE keyHandle,byte[] cipherText,TPMU_ASYM_SCHEME inScheme,byte[] label)
    {
        TPM2_RSA_Decrypt_REQUEST inStruct = new TPM2_RSA_Decrypt_REQUEST();
        RSA_DecryptResponse outStruct = new RSA_DecryptResponse();
        inStruct.keyHandle = keyHandle;
        inStruct.cipherText = cipherText;
        inStruct.inScheme = inScheme;
        inStruct.label = label;
        DispatchCommand(TPM_CC.RSA_Decrypt, new TPM_HANDLE[] {keyHandle}, 1, 0, inStruct, outStruct);
        return outStruct.message;
    }
    
    /**
     * This command uses the TPM to generate an ephemeral key pair (de, Qe where Qe [de]G). It uses the private ephemeral key and a loaded public key (QS) to compute the shared secret value (P [hde]QS).
     * 
     * @param keyHandle Handle of a loaded ECC key public area. Auth Index: None 
     * @return TPM2_ECDH_KeyGen_RESPONSE{(ul)(li)(code)zPoint(/code) - results of P h[de]Qs(/li)(li)(code)pubPoint(/code) - generated ephemeral public point (Qe)(/li)(/ul)}
     */
    public ECDH_KeyGenResponse ECDH_KeyGen(TPM_HANDLE keyHandle)
    {
        TPM2_ECDH_KeyGen_REQUEST inStruct = new TPM2_ECDH_KeyGen_REQUEST();
        ECDH_KeyGenResponse outStruct = new ECDH_KeyGenResponse();
        inStruct.keyHandle = keyHandle;
        DispatchCommand(TPM_CC.ECDH_KeyGen, new TPM_HANDLE[] {keyHandle}, 0, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command uses the TPM to recover the Z value from a public point (QB) and a private key (ds). It will perform the multiplication of the provided inPoint (QB) with the private key (ds) and return the coordinates of the resultant point (Z = (xZ , yZ) [hds]QB; where h is the cofactor of the curve).
     * 
     * @param keyHandle handle of a loaded ECC key Auth Index: 1 Auth Role: USER 
     * @param inPoint a public key 
     * @return X and Y coordinates of the product of the multiplication Z = (xZ , yZ) [hdS]QB
     */
    public TPMS_ECC_POINT ECDH_ZGen(TPM_HANDLE keyHandle,TPMS_ECC_POINT inPoint)
    {
        TPM2_ECDH_ZGen_REQUEST inStruct = new TPM2_ECDH_ZGen_REQUEST();
        ECDH_ZGenResponse outStruct = new ECDH_ZGenResponse();
        inStruct.keyHandle = keyHandle;
        inStruct.inPoint = inPoint;
        DispatchCommand(TPM_CC.ECDH_ZGen, new TPM_HANDLE[] {keyHandle}, 1, 0, inStruct, outStruct);
        return outStruct.outPoint;
    }
    
    /**
     * This command returns the parameters of an ECC curve identified by its TCG-assigned curveID.
     * 
     * @param curveID parameter set selector 
     * @return ECC parameters for the selected curve
     */
    public TPMS_ALGORITHM_DETAIL_ECC ECC_Parameters(TPM_ECC_CURVE curveID)
    {
        TPM2_ECC_Parameters_REQUEST inStruct = new TPM2_ECC_Parameters_REQUEST();
        ECC_ParametersResponse outStruct = new ECC_ParametersResponse();
        inStruct.curveID = curveID;
        DispatchCommand(TPM_CC.ECC_Parameters, new TPM_HANDLE[] {}, 0, 0, inStruct, outStruct);
        return outStruct.parameters;
    }
    
    /**
     * This command supports two-phase key exchange protocols. The command is used in combination with TPM2_EC_Ephemeral(). TPM2_EC_Ephemeral() generates an ephemeral key and returns the public point of that ephemeral key along with a numeric value that allows the TPM to regenerate the associated private key.
     * 
     * @param keyA handle of an unrestricted decryption key ECC The private key referenced by this handle is used as dS,A Auth Index: 1 Auth Role: USER 
     * @param inQsB other partys static public key (Qs,B = (Xs,B, Ys,B)) 
     * @param inQeB other party's ephemeral public key (Qe,B = (Xe,B, Ye,B)) 
     * @param inScheme the key exchange scheme 
     * @param counter value returned by TPM2_EC_Ephemeral() 
     * @return TPM2_ZGen_2Phase_RESPONSE{(ul)(li)(code)outZ1(/code) - X and Y coordinates of the computed value (scheme dependent)(/li)(li)(code)outZ2(/code) - X and Y coordinates of the second computed value (scheme dependent)(/li)(/ul)}
     */
    public ZGen_2PhaseResponse ZGen_2Phase(TPM_HANDLE keyA,TPMS_ECC_POINT inQsB,TPMS_ECC_POINT inQeB,TPM_ALG_ID inScheme,int counter)
    {
        TPM2_ZGen_2Phase_REQUEST inStruct = new TPM2_ZGen_2Phase_REQUEST();
        ZGen_2PhaseResponse outStruct = new ZGen_2PhaseResponse();
        inStruct.keyA = keyA;
        inStruct.inQsB = inQsB;
        inStruct.inQeB = inQeB;
        inStruct.inScheme = inScheme;
        inStruct.counter = (short)counter;
        DispatchCommand(TPM_CC.ZGen_2Phase, new TPM_HANDLE[] {keyA}, 1, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * NOTE 1 This command is deprecated, and TPM2_EncryptDecrypt2() is preferred. This should be reflected in platform-specific specifications.
     * 
     * @param keyHandle the symmetric key used for the operation Auth Index: 1 Auth Role: USER 
     * @param decrypt if YES, then the operation is decryption; if NO, the operation is encryption 
     * @param mode symmetric encryption/decryption mode this field shall match the default mode of the key or be TPM_ALG_NULL. 
     * @param ivIn an initial value as required by the algorithm 
     * @param inData the data to be encrypted/decrypted 
     * @return TPM2_EncryptDecrypt_RESPONSE{(ul)(li)(code)outData(/code) - encrypted or decrypted output(/li)(li)(code)ivOut(/code) - chaining value to use for IV in next round(/li)(/ul)}
     */
    public EncryptDecryptResponse EncryptDecrypt(TPM_HANDLE keyHandle,byte decrypt,TPM_ALG_ID mode,byte[] ivIn,byte[] inData)
    {
        TPM2_EncryptDecrypt_REQUEST inStruct = new TPM2_EncryptDecrypt_REQUEST();
        EncryptDecryptResponse outStruct = new EncryptDecryptResponse();
        inStruct.keyHandle = keyHandle;
        inStruct.decrypt = decrypt;
        inStruct.mode = mode;
        inStruct.ivIn = ivIn;
        inStruct.inData = inData;
        DispatchCommand(TPM_CC.EncryptDecrypt, new TPM_HANDLE[] {keyHandle}, 1, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command is identical to TPM2_EncryptDecrypt(), except that the inData parameter is the first parameter. This permits inData to be parameter encrypted.
     * 
     * @param keyHandle the symmetric key used for the operation Auth Index: 1 Auth Role: USER 
     * @param inData the data to be encrypted/decrypted 
     * @param decrypt if YES, then the operation is decryption; if NO, the operation is encryption 
     * @param mode symmetric mode this field shall match the default mode of the key or be TPM_ALG_NULL. 
     * @param ivIn an initial value as required by the algorithm 
     * @return TPM2_EncryptDecrypt2_RESPONSE{(ul)(li)(code)outData(/code) - encrypted or decrypted output(/li)(li)(code)ivOut(/code) - chaining value to use for IV in next round(/li)(/ul)}
     */
    public EncryptDecrypt2Response EncryptDecrypt2(TPM_HANDLE keyHandle,byte[] inData,byte decrypt,TPM_ALG_ID mode,byte[] ivIn)
    {
        TPM2_EncryptDecrypt2_REQUEST inStruct = new TPM2_EncryptDecrypt2_REQUEST();
        EncryptDecrypt2Response outStruct = new EncryptDecrypt2Response();
        inStruct.keyHandle = keyHandle;
        inStruct.inData = inData;
        inStruct.decrypt = decrypt;
        inStruct.mode = mode;
        inStruct.ivIn = ivIn;
        DispatchCommand(TPM_CC.EncryptDecrypt2, new TPM_HANDLE[] {keyHandle}, 1, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command performs a hash operation on a data buffer and returns the results.
     * 
     * @param data data to be hashed 
     * @param hashAlg algorithm for the hash being computed shall not be TPM_ALG_NULL 
     * @param hierarchy hierarchy to use for the ticket (TPM_RH_NULL allowed) 
     * @return TPM2_Hash_RESPONSE{(ul)(li)(code)outHash(/code) - results(/li)(li)(code)validation(/code) - ticket indicating that the sequence of octets used to compute outDigest did not start with TPM_GENERATED_VALUE will be a NULL ticket if the digest may not be signed with a restricted key(/li)(/ul)}
     */
    public HashResponse Hash(byte[] data,TPM_ALG_ID hashAlg,TPM_HANDLE hierarchy)
    {
        TPM2_Hash_REQUEST inStruct = new TPM2_Hash_REQUEST();
        HashResponse outStruct = new HashResponse();
        inStruct.data = data;
        inStruct.hashAlg = hashAlg;
        inStruct.hierarchy = hierarchy;
        DispatchCommand(TPM_CC.Hash, new TPM_HANDLE[] {}, 0, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command performs an HMAC on the supplied data using the indicated hash algorithm.
     * 
     * @param handle handle for the symmetric signing key providing the HMAC key Auth Index: 1 Auth Role: USER 
     * @param buffer HMAC data 
     * @param hashAlg algorithm to use for HMAC 
     * @return the returned HMAC in a sized buffer
     */
    public byte[] HMAC(TPM_HANDLE handle,byte[] buffer,TPM_ALG_ID hashAlg)
    {
        TPM2_HMAC_REQUEST inStruct = new TPM2_HMAC_REQUEST();
        HMACResponse outStruct = new HMACResponse();
        inStruct.handle = handle;
        inStruct.buffer = buffer;
        inStruct.hashAlg = hashAlg;
        DispatchCommand(TPM_CC.HMAC, new TPM_HANDLE[] {handle}, 1, 0, inStruct, outStruct);
        return outStruct.outHMAC;
    }
    
    /**
     * This command performs an HMAC or a block cipher MAC on the supplied data using the indicated algorithm.
     * 
     * @param handle handle for the symmetric signing key providing the MAC key Auth Index: 1 Auth Role: USER 
     * @param buffer MAC data 
     * @param inScheme algorithm to use for MAC 
     * @return the returned MAC in a sized buffer
     */
    public byte[] MAC(TPM_HANDLE handle,byte[] buffer,TPM_ALG_ID inScheme)
    {
        TPM2_MAC_REQUEST inStruct = new TPM2_MAC_REQUEST();
        MACResponse outStruct = new MACResponse();
        inStruct.handle = handle;
        inStruct.buffer = buffer;
        inStruct.inScheme = inScheme;
        DispatchCommand(TPM_CC.MAC, new TPM_HANDLE[] {handle}, 1, 0, inStruct, outStruct);
        return outStruct.outMAC;
    }
    
    /**
     * This command returns the next bytesRequested octets from the random number generator (RNG).
     * 
     * @param bytesRequested number of octets to return 
     * @return the random octets
     */
    public byte[] GetRandom(int bytesRequested)
    {
        TPM2_GetRandom_REQUEST inStruct = new TPM2_GetRandom_REQUEST();
        GetRandomResponse outStruct = new GetRandomResponse();
        inStruct.bytesRequested = (short)bytesRequested;
        DispatchCommand(TPM_CC.GetRandom, new TPM_HANDLE[] {}, 0, 0, inStruct, outStruct);
        return outStruct.randomBytes;
    }
    
    /**
     * This command is used to add "additional information" to the RNG state.
     * 
     * @param inData additional information
     */
    public void StirRandom(byte[] inData)
    {
        TPM2_StirRandom_REQUEST inStruct = new TPM2_StirRandom_REQUEST();
        inStruct.inData = inData;
        DispatchCommand(TPM_CC.StirRandom, new TPM_HANDLE[] {}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command starts an HMAC sequence. The TPM will create and initialize an HMAC sequence structure, assign a handle to the sequence, and set the authValue of the sequence object to the value in auth.
     * 
     * @param handle handle of an HMAC key Auth Index: 1 Auth Role: USER 
     * @param auth authorization value for subsequent use of the sequence 
     * @param hashAlg the hash algorithm to use for the HMAC 
     * @return a handle to reference the sequence
     */
    public TPM_HANDLE HMAC_Start(TPM_HANDLE handle,byte[] auth,TPM_ALG_ID hashAlg)
    {
        TPM2_HMAC_Start_REQUEST inStruct = new TPM2_HMAC_Start_REQUEST();
        HMAC_StartResponse outStruct = new HMAC_StartResponse();
        inStruct.handle = handle;
        inStruct.auth = auth;
        inStruct.hashAlg = hashAlg;
        DispatchCommand(TPM_CC.HMAC_Start, new TPM_HANDLE[] {handle}, 1, 1, inStruct, outStruct);
        return outStruct.handle;
    }
    
    /**
     * This command starts a MAC sequence. The TPM will create and initialize an MAC sequence structure, assign a handle to the sequence, and set the authValue of the sequence object to the value in auth.
     * 
     * @param handle handle of a MAC key Auth Index: 1 Auth Role: USER 
     * @param auth authorization value for subsequent use of the sequence 
     * @param inScheme the algorithm to use for the MAC 
     * @return a handle to reference the sequence
     */
    public TPM_HANDLE MAC_Start(TPM_HANDLE handle,byte[] auth,TPM_ALG_ID inScheme)
    {
        TPM2_MAC_Start_REQUEST inStruct = new TPM2_MAC_Start_REQUEST();
        MAC_StartResponse outStruct = new MAC_StartResponse();
        inStruct.handle = handle;
        inStruct.auth = auth;
        inStruct.inScheme = inScheme;
        DispatchCommand(TPM_CC.MAC_Start, new TPM_HANDLE[] {handle}, 1, 1, inStruct, outStruct);
        return outStruct.handle;
    }
    
    /**
     * This command starts a hash or an Event Sequence. If hashAlg is an implemented hash, then a hash sequence is started. If hashAlg is TPM_ALG_NULL, then an Event Sequence is started. If hashAlg is neither an implemented algorithm nor TPM_ALG_NULL, then the TPM shall return TPM_RC_HASH.
     * 
     * @param auth authorization value for subsequent use of the sequence 
     * @param hashAlg the hash algorithm to use for the hash sequence An Event Sequence starts if this is TPM_ALG_NULL. 
     * @return a handle to reference the sequence
     */
    public TPM_HANDLE HashSequenceStart(byte[] auth,TPM_ALG_ID hashAlg)
    {
        TPM2_HashSequenceStart_REQUEST inStruct = new TPM2_HashSequenceStart_REQUEST();
        HashSequenceStartResponse outStruct = new HashSequenceStartResponse();
        inStruct.auth = auth;
        inStruct.hashAlg = hashAlg;
        DispatchCommand(TPM_CC.HashSequenceStart, new TPM_HANDLE[] {}, 0, 1, inStruct, outStruct);
        return outStruct.handle;
    }
    
    /**
     * This command is used to add data to a hash or HMAC sequence. The amount of data in buffer may be any size up to the limits of the TPM.
     * 
     * @param sequenceHandle handle for the sequence object Auth Index: 1 Auth Role: USER 
     * @param buffer data to be added to hash
     */
    public void SequenceUpdate(TPM_HANDLE sequenceHandle,byte[] buffer)
    {
        TPM2_SequenceUpdate_REQUEST inStruct = new TPM2_SequenceUpdate_REQUEST();
        inStruct.sequenceHandle = sequenceHandle;
        inStruct.buffer = buffer;
        DispatchCommand(TPM_CC.SequenceUpdate, new TPM_HANDLE[] {sequenceHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command adds the last part of data, if any, to a hash/HMAC sequence and returns the result.
     * 
     * @param sequenceHandle authorization for the sequence Auth Index: 1 Auth Role: USER 
     * @param buffer data to be added to the hash/HMAC 
     * @param hierarchy hierarchy of the ticket for a hash 
     * @return TPM2_SequenceComplete_RESPONSE{(ul)(li)(code)result(/code) - the returned HMAC or digest in a sized buffer(/li)(li)(code)validation(/code) - ticket indicating that the sequence of octets used to compute outDigest did not start with TPM_GENERATED_VALUE This is a NULL Ticket when the sequence is HMAC.(/li)(/ul)}
     */
    public SequenceCompleteResponse SequenceComplete(TPM_HANDLE sequenceHandle,byte[] buffer,TPM_HANDLE hierarchy)
    {
        TPM2_SequenceComplete_REQUEST inStruct = new TPM2_SequenceComplete_REQUEST();
        SequenceCompleteResponse outStruct = new SequenceCompleteResponse();
        inStruct.sequenceHandle = sequenceHandle;
        inStruct.buffer = buffer;
        inStruct.hierarchy = hierarchy;
        DispatchCommand(TPM_CC.SequenceComplete, new TPM_HANDLE[] {sequenceHandle}, 1, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command adds the last part of data, if any, to an Event Sequence and returns the result in a digest list. If pcrHandle references a PCR and not TPM_RH_NULL, then the returned digest list is processed in the same manner as the digest list input parameter to TPM2_PCR_Extend() with the pcrHandle in each bank extended with the associated digest value.
     * 
     * @param pcrHandle PCR to be extended with the Event data Auth Index: 1 Auth Role: USER 
     * @param sequenceHandle authorization for the sequence Auth Index: 2 Auth Role: USER 
     * @param buffer data to be added to the Event 
     * @return list of digests computed for the PCR
     */
    public TPMT_HA[] EventSequenceComplete(TPM_HANDLE pcrHandle,TPM_HANDLE sequenceHandle,byte[] buffer)
    {
        TPM2_EventSequenceComplete_REQUEST inStruct = new TPM2_EventSequenceComplete_REQUEST();
        EventSequenceCompleteResponse outStruct = new EventSequenceCompleteResponse();
        inStruct.pcrHandle = pcrHandle;
        inStruct.sequenceHandle = sequenceHandle;
        inStruct.buffer = buffer;
        DispatchCommand(TPM_CC.EventSequenceComplete, new TPM_HANDLE[] {pcrHandle,sequenceHandle}, 2, 0, inStruct, outStruct);
        return outStruct.results;
    }
    
    /**
     * The purpose of this command is to prove that an object with a specific Name is loaded in the TPM. By certifying that the object is loaded, the TPM warrants that a public area with a given Name is self-consistent and associated with a valid sensitive area. If a relying party has a public area that has the same Name as a Name certified with this command, then the values in that public area are correct.
     * 
     * @param objectHandle handle of the object to be certified Auth Index: 1 Auth Role: ADMIN 
     * @param signHandle handle of the key used to sign the attestation structure Auth Index: 2 Auth Role: USER 
     * @param qualifyingData user provided qualifying data 
     * @param inScheme signing scheme to use if the scheme for signHandle is TPM_ALG_NULL 
     * @return TPM2_Certify_RESPONSE{(ul)(li)(code)certifyInfo(/code) - the structure that was signed(/li)(li)(code)signature(/code) - the asymmetric signature over certifyInfo using the key referenced by signHandle(/li)(/ul)}
     */
    public CertifyResponse Certify(TPM_HANDLE objectHandle,TPM_HANDLE signHandle,byte[] qualifyingData,TPMU_SIG_SCHEME inScheme)
    {
        TPM2_Certify_REQUEST inStruct = new TPM2_Certify_REQUEST();
        CertifyResponse outStruct = new CertifyResponse();
        inStruct.objectHandle = objectHandle;
        inStruct.signHandle = signHandle;
        inStruct.qualifyingData = qualifyingData;
        inStruct.inScheme = inScheme;
        DispatchCommand(TPM_CC.Certify, new TPM_HANDLE[] {objectHandle,signHandle}, 2, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command is used to prove the association between an object and its creation data. The TPM will validate that the ticket was produced by the TPM and that the ticket validates the association between a loaded public area and the provided hash of the creation data (creationHash).
     * 
     * @param signHandle handle of the key that will sign the attestation block Auth Index: 1 Auth Role: USER 
     * @param objectHandle the object associated with the creation data Auth Index: None 
     * @param qualifyingData user-provided qualifying data 
     * @param creationHash hash of the creation data produced by TPM2_Create() or TPM2_CreatePrimary() 
     * @param inScheme signing scheme to use if the scheme for signHandle is TPM_ALG_NULL 
     * @param creationTicket ticket produced by TPM2_Create() or TPM2_CreatePrimary() 
     * @return TPM2_CertifyCreation_RESPONSE{(ul)(li)(code)certifyInfo(/code) - the structure that was signed(/li)(li)(code)signature(/code) - the signature over certifyInfo(/li)(/ul)}
     */
    public CertifyCreationResponse CertifyCreation(TPM_HANDLE signHandle,TPM_HANDLE objectHandle,byte[] qualifyingData,byte[] creationHash,TPMU_SIG_SCHEME inScheme,TPMT_TK_CREATION creationTicket)
    {
        TPM2_CertifyCreation_REQUEST inStruct = new TPM2_CertifyCreation_REQUEST();
        CertifyCreationResponse outStruct = new CertifyCreationResponse();
        inStruct.signHandle = signHandle;
        inStruct.objectHandle = objectHandle;
        inStruct.qualifyingData = qualifyingData;
        inStruct.creationHash = creationHash;
        inStruct.inScheme = inScheme;
        inStruct.creationTicket = creationTicket;
        DispatchCommand(TPM_CC.CertifyCreation, new TPM_HANDLE[] {signHandle,objectHandle}, 1, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command is used to quote PCR values.
     * 
     * @param signHandle handle of key that will perform signature Auth Index: 1 Auth Role: USER 
     * @param qualifyingData data supplied by the caller 
     * @param inScheme signing scheme to use if the scheme for signHandle is TPM_ALG_NULL 
     * @param PCRselect PCR set to quote 
     * @return TPM2_Quote_RESPONSE{(ul)(li)(code)quoted(/code) - the quoted information(/li)(li)(code)signature(/code) - the signature over quoted(/li)(/ul)}
     */
    public QuoteResponse Quote(TPM_HANDLE signHandle,byte[] qualifyingData,TPMU_SIG_SCHEME inScheme,TPMS_PCR_SELECTION[] PCRselect)
    {
        TPM2_Quote_REQUEST inStruct = new TPM2_Quote_REQUEST();
        QuoteResponse outStruct = new QuoteResponse();
        inStruct.signHandle = signHandle;
        inStruct.qualifyingData = qualifyingData;
        inStruct.inScheme = inScheme;
        inStruct.PCRselect = PCRselect;
        DispatchCommand(TPM_CC.Quote, new TPM_HANDLE[] {signHandle}, 1, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command returns a digital signature of the audit session digest.
     * 
     * @param privacyAdminHandle handle of the privacy administrator (TPM_RH_ENDORSEMENT) Auth Index: 1 Auth Role: USER 
     * @param signHandle handle of the signing key Auth Index: 2 Auth Role: USER 
     * @param sessionHandle handle of the audit session Auth Index: None 
     * @param qualifyingData user-provided qualifying data may be zero-length 
     * @param inScheme signing scheme to use if the scheme for signHandle is TPM_ALG_NULL 
     * @return TPM2_GetSessionAuditDigest_RESPONSE{(ul)(li)(code)auditInfo(/code) - the audit information that was signed(/li)(li)(code)signature(/code) - the signature over auditInfo(/li)(/ul)}
     */
    public GetSessionAuditDigestResponse GetSessionAuditDigest(TPM_HANDLE privacyAdminHandle,TPM_HANDLE signHandle,TPM_HANDLE sessionHandle,byte[] qualifyingData,TPMU_SIG_SCHEME inScheme)
    {
        TPM2_GetSessionAuditDigest_REQUEST inStruct = new TPM2_GetSessionAuditDigest_REQUEST();
        GetSessionAuditDigestResponse outStruct = new GetSessionAuditDigestResponse();
        inStruct.privacyAdminHandle = privacyAdminHandle;
        inStruct.signHandle = signHandle;
        inStruct.sessionHandle = sessionHandle;
        inStruct.qualifyingData = qualifyingData;
        inStruct.inScheme = inScheme;
        DispatchCommand(TPM_CC.GetSessionAuditDigest, new TPM_HANDLE[] {privacyAdminHandle,signHandle,sessionHandle}, 2, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command returns the current value of the command audit digest, a digest of the commands being audited, and the audit hash algorithm. These values are placed in an attestation structure and signed with the key referenced by signHandle.
     * 
     * @param privacyHandle handle of the privacy administrator (TPM_RH_ENDORSEMENT) Auth Index: 1 Auth Role: USER 
     * @param signHandle the handle of the signing key Auth Index: 2 Auth Role: USER 
     * @param qualifyingData other data to associate with this audit digest 
     * @param inScheme signing scheme to use if the scheme for signHandle is TPM_ALG_NULL 
     * @return TPM2_GetCommandAuditDigest_RESPONSE{(ul)(li)(code)auditInfo(/code) - the auditInfo that was signed(/li)(li)(code)signature(/code) - the signature over auditInfo(/li)(/ul)}
     */
    public GetCommandAuditDigestResponse GetCommandAuditDigest(TPM_HANDLE privacyHandle,TPM_HANDLE signHandle,byte[] qualifyingData,TPMU_SIG_SCHEME inScheme)
    {
        TPM2_GetCommandAuditDigest_REQUEST inStruct = new TPM2_GetCommandAuditDigest_REQUEST();
        GetCommandAuditDigestResponse outStruct = new GetCommandAuditDigestResponse();
        inStruct.privacyHandle = privacyHandle;
        inStruct.signHandle = signHandle;
        inStruct.qualifyingData = qualifyingData;
        inStruct.inScheme = inScheme;
        DispatchCommand(TPM_CC.GetCommandAuditDigest, new TPM_HANDLE[] {privacyHandle,signHandle}, 2, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command returns the current values of Time and Clock.
     * 
     * @param privacyAdminHandle handle of the privacy administrator (TPM_RH_ENDORSEMENT) Auth Index: 1 Auth Role: USER 
     * @param signHandle the keyHandle identifier of a loaded key that can perform digital signatures Auth Index: 2 Auth Role: USER 
     * @param qualifyingData data to tick stamp 
     * @param inScheme signing scheme to use if the scheme for signHandle is TPM_ALG_NULL 
     * @return TPM2_GetTime_RESPONSE{(ul)(li)(code)timeInfo(/code) - standard TPM-generated attestation block(/li)(li)(code)signature(/code) - the signature over timeInfo(/li)(/ul)}
     */
    public GetTimeResponse GetTime(TPM_HANDLE privacyAdminHandle,TPM_HANDLE signHandle,byte[] qualifyingData,TPMU_SIG_SCHEME inScheme)
    {
        TPM2_GetTime_REQUEST inStruct = new TPM2_GetTime_REQUEST();
        GetTimeResponse outStruct = new GetTimeResponse();
        inStruct.privacyAdminHandle = privacyAdminHandle;
        inStruct.signHandle = signHandle;
        inStruct.qualifyingData = qualifyingData;
        inStruct.inScheme = inScheme;
        DispatchCommand(TPM_CC.GetTime, new TPM_HANDLE[] {privacyAdminHandle,signHandle}, 2, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * TPM2_Commit() performs the first part of an ECC anonymous signing operation. The TPM will perform the point multiplications on the provided points and return intermediate signing values. The signHandle parameter shall refer to an ECC key and the signing scheme must be anonymous (TPM_RC_SCHEME).
     * 
     * @param signHandle handle of the key that will be used in the signing operation Auth Index: 1 Auth Role: USER 
     * @param P1 a point (M) on the curve used by signHandle 
     * @param s2 octet array used to derive x-coordinate of a base point 
     * @param y2 y coordinate of the point associated with s2 
     * @return TPM2_Commit_RESPONSE{(ul)(li)(code)K(/code) - ECC point K [ds](x2, y2)(/li)(li)(code)L(/code) - ECC point L [r](x2, y2)(/li)(li)(code)E(/code) - ECC point E [r]P1(/li)(li)(code)counter(/code) - least-significant 16 bits of commitCount(/li)(/ul)}
     */
    public CommitResponse Commit(TPM_HANDLE signHandle,TPMS_ECC_POINT P1,byte[] s2,byte[] y2)
    {
        TPM2_Commit_REQUEST inStruct = new TPM2_Commit_REQUEST();
        CommitResponse outStruct = new CommitResponse();
        inStruct.signHandle = signHandle;
        inStruct.P1 = P1;
        inStruct.s2 = s2;
        inStruct.y2 = y2;
        DispatchCommand(TPM_CC.Commit, new TPM_HANDLE[] {signHandle}, 1, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * TPM2_EC_Ephemeral() creates an ephemeral key for use in a two-phase key exchange protocol.
     * 
     * @param curveID The curve for the computed ephemeral point 
     * @return TPM2_EC_Ephemeral_RESPONSE{(ul)(li)(code)Q(/code) - ephemeral public key Q [r]G(/li)(li)(code)counter(/code) - least-significant 16 bits of commitCount(/li)(/ul)}
     */
    public EC_EphemeralResponse EC_Ephemeral(TPM_ECC_CURVE curveID)
    {
        TPM2_EC_Ephemeral_REQUEST inStruct = new TPM2_EC_Ephemeral_REQUEST();
        EC_EphemeralResponse outStruct = new EC_EphemeralResponse();
        inStruct.curveID = curveID;
        DispatchCommand(TPM_CC.EC_Ephemeral, new TPM_HANDLE[] {}, 0, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command uses loaded keys to validate a signature on a message with the message digest passed to the TPM.
     * 
     * @param keyHandle handle of public key that will be used in the validation Auth Index: None 
     * @param digest digest of the signed message 
     * @param signature signature to be tested 
     * @return This ticket is produced by TPM2_VerifySignature(). This formulation is used for multiple ticket uses. The ticket provides evidence that the TPM has validated that a digest was signed by a key with the Name of keyName. The ticket is computed by
     */
    public TPMT_TK_VERIFIED VerifySignature(TPM_HANDLE keyHandle,byte[] digest,TPMU_SIGNATURE signature)
    {
        TPM2_VerifySignature_REQUEST inStruct = new TPM2_VerifySignature_REQUEST();
        VerifySignatureResponse outStruct = new VerifySignatureResponse();
        inStruct.keyHandle = keyHandle;
        inStruct.digest = digest;
        inStruct.signature = signature;
        DispatchCommand(TPM_CC.VerifySignature, new TPM_HANDLE[] {keyHandle}, 0, 0, inStruct, outStruct);
        return outStruct.validation;
    }
    
    /**
     * This command causes the TPM to sign an externally provided hash with the specified symmetric or asymmetric signing key.
     * 
     * @param keyHandle Handle of key that will perform signing Auth Index: 1 Auth Role: USER 
     * @param digest digest to be signed 
     * @param inScheme signing scheme to use if the scheme for keyHandle is TPM_ALG_NULL 
     * @param validation proof that digest was created by the TPM If keyHandle is not a restricted signing key, then this may be a NULL Ticket with tag = TPM_ST_CHECKHASH. 
     * @return the signature
     */
    public TPMU_SIGNATURE Sign(TPM_HANDLE keyHandle,byte[] digest,TPMU_SIG_SCHEME inScheme,TPMT_TK_HASHCHECK validation)
    {
        TPM2_Sign_REQUEST inStruct = new TPM2_Sign_REQUEST();
        SignResponse outStruct = new SignResponse();
        inStruct.keyHandle = keyHandle;
        inStruct.digest = digest;
        inStruct.inScheme = inScheme;
        inStruct.validation = validation;
        DispatchCommand(TPM_CC.Sign, new TPM_HANDLE[] {keyHandle}, 1, 0, inStruct, outStruct);
        return outStruct.signature;
    }
    
    /**
     * This command may be used by the Privacy Administrator or platform to change the audit status of a command or to set the hash algorithm used for the audit digest, but not both at the same time.
     * 
     * @param auth TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER 
     * @param auditAlg hash algorithm for the audit digest; if TPM_ALG_NULL, then the hash is not changed 
     * @param setList list of commands that will be added to those that will be audited 
     * @param clearList list of commands that will no longer be audited
     */
    public void SetCommandCodeAuditStatus(TPM_HANDLE auth,TPM_ALG_ID auditAlg,TPM_CC[] setList,TPM_CC[] clearList)
    {
        TPM2_SetCommandCodeAuditStatus_REQUEST inStruct = new TPM2_SetCommandCodeAuditStatus_REQUEST();
        inStruct.auth = auth;
        inStruct.auditAlg = auditAlg;
        inStruct.setList = setList;
        inStruct.clearList = clearList;
        DispatchCommand(TPM_CC.SetCommandCodeAuditStatus, new TPM_HANDLE[] {auth}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command is used to cause an update to the indicated PCR. The digests parameter contains one or more tagged digest values identified by an algorithm ID. For each digest, the PCR associated with pcrHandle is Extended into the bank identified by the tag (hashAlg).
     * 
     * @param pcrHandle handle of the PCR Auth Handle: 1 Auth Role: USER 
     * @param digests list of tagged digest values to be extended
     */
    public void PCR_Extend(TPM_HANDLE pcrHandle,TPMT_HA[] digests)
    {
        TPM2_PCR_Extend_REQUEST inStruct = new TPM2_PCR_Extend_REQUEST();
        inStruct.pcrHandle = pcrHandle;
        inStruct.digests = digests;
        DispatchCommand(TPM_CC.PCR_Extend, new TPM_HANDLE[] {pcrHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command is used to cause an update to the indicated PCR.
     * 
     * @param pcrHandle Handle of the PCR Auth Handle: 1 Auth Role: USER 
     * @param eventData Event data in sized buffer 
     * @return -
     */
    public TPMT_HA[] PCR_Event(TPM_HANDLE pcrHandle,byte[] eventData)
    {
        TPM2_PCR_Event_REQUEST inStruct = new TPM2_PCR_Event_REQUEST();
        PCR_EventResponse outStruct = new PCR_EventResponse();
        inStruct.pcrHandle = pcrHandle;
        inStruct.eventData = eventData;
        DispatchCommand(TPM_CC.PCR_Event, new TPM_HANDLE[] {pcrHandle}, 1, 0, inStruct, outStruct);
        return outStruct.digests;
    }
    
    /**
     * This command returns the values of all PCR specified in pcrSelectionIn.
     * 
     * @param pcrSelectionIn The selection of PCR to read 
     * @return TPM2_PCR_Read_RESPONSE{(ul)(li)(code)pcrUpdateCounter(/code) - the current value of the PCR update counter(/li)(li)(code)pcrSelectionOut(/code) - the PCR in the returned list(/li)(li)(code)pcrValues(/code) - the contents of the PCR indicated in pcrSelectOut-) pcrSelection[] as tagged digests(/li)(/ul)}
     */
    public PCR_ReadResponse PCR_Read(TPMS_PCR_SELECTION[] pcrSelectionIn)
    {
        TPM2_PCR_Read_REQUEST inStruct = new TPM2_PCR_Read_REQUEST();
        PCR_ReadResponse outStruct = new PCR_ReadResponse();
        inStruct.pcrSelectionIn = pcrSelectionIn;
        DispatchCommand(TPM_CC.PCR_Read, new TPM_HANDLE[] {}, 0, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command is used to set the desired PCR allocation of PCR and algorithms. This command requires Platform Authorization.
     * 
     * @param authHandle TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER 
     * @param pcrAllocation the requested allocation 
     * @return TPM2_PCR_Allocate_RESPONSE{(ul)(li)(code)allocationSuccess(/code) - YES if the allocation succeeded(/li)(li)(code)maxPCR(/code) - maximum number of PCR that may be in a bank(/li)(li)(code)sizeNeeded(/code) - number of octets required to satisfy the request(/li)(li)(code)sizeAvailable(/code) - Number of octets available. Computed before the allocation.(/li)(/ul)}
     */
    public PCR_AllocateResponse PCR_Allocate(TPM_HANDLE authHandle,TPMS_PCR_SELECTION[] pcrAllocation)
    {
        TPM2_PCR_Allocate_REQUEST inStruct = new TPM2_PCR_Allocate_REQUEST();
        PCR_AllocateResponse outStruct = new PCR_AllocateResponse();
        inStruct.authHandle = authHandle;
        inStruct.pcrAllocation = pcrAllocation;
        DispatchCommand(TPM_CC.PCR_Allocate, new TPM_HANDLE[] {authHandle}, 1, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command is used to associate a policy with a PCR or group of PCR. The policy determines the conditions under which a PCR may be extended or reset.
     * 
     * @param authHandle TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER 
     * @param authPolicy the desired authPolicy 
     * @param hashAlg the hash algorithm of the policy 
     * @param pcrNum the PCR for which the policy is to be set
     */
    public void PCR_SetAuthPolicy(TPM_HANDLE authHandle,byte[] authPolicy,TPM_ALG_ID hashAlg,TPM_HANDLE pcrNum)
    {
        TPM2_PCR_SetAuthPolicy_REQUEST inStruct = new TPM2_PCR_SetAuthPolicy_REQUEST();
        inStruct.authHandle = authHandle;
        inStruct.authPolicy = authPolicy;
        inStruct.hashAlg = hashAlg;
        inStruct.pcrNum = pcrNum;
        DispatchCommand(TPM_CC.PCR_SetAuthPolicy, new TPM_HANDLE[] {authHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command changes the authValue of a PCR or group of PCR.
     * 
     * @param pcrHandle handle for a PCR that may have an authorization value set Auth Index: 1 Auth Role: USER 
     * @param auth the desired authorization value
     */
    public void PCR_SetAuthValue(TPM_HANDLE pcrHandle,byte[] auth)
    {
        TPM2_PCR_SetAuthValue_REQUEST inStruct = new TPM2_PCR_SetAuthValue_REQUEST();
        inStruct.pcrHandle = pcrHandle;
        inStruct.auth = auth;
        DispatchCommand(TPM_CC.PCR_SetAuthValue, new TPM_HANDLE[] {pcrHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * If the attribute of a PCR allows the PCR to be reset and proper authorization is provided, then this command may be used to set the PCR in all banks to zero. The attributes of the PCR may restrict the locality that can perform the reset operation.
     * 
     * @param pcrHandle the PCR to reset Auth Index: 1 Auth Role: USER
     */
    public void PCR_Reset(TPM_HANDLE pcrHandle)
    {
        TPM2_PCR_Reset_REQUEST inStruct = new TPM2_PCR_Reset_REQUEST();
        inStruct.pcrHandle = pcrHandle;
        DispatchCommand(TPM_CC.PCR_Reset, new TPM_HANDLE[] {pcrHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command includes a signed authorization in a policy. The command ties the policy to a signing key by including the Name of the signing key in the policyDigest
     * 
     * @param authObject handle for a key that will validate the signature Auth Index: None 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param nonceTPM the policy nonce for the session This can be the Empty Buffer. 
     * @param cpHashA digest of the command parameters to which this authorization is limited This is not the cpHash for this command but the cpHash for the command to which this policy session will be applied. If it is not limited, the parameter will be the Empty Buffer. 
     * @param policyRef a reference to a policy relating to the authorization may be the Empty Buffer Size is limited to be no larger than the nonce size supported on the TPM. 
     * @param expiration time when authorization will expire, measured in seconds from the time that nonceTPM was generated If expiration is non-negative, a NULL Ticket is returned. See 23.2.5. 
     * @param auth signed authorization (not optional) 
     * @return TPM2_PolicySigned_RESPONSE{(ul)(li)(code)timeout(/code) - implementation-specific time value, used to indicate to the TPM when the ticket expires NOTE If policyTicket is a NULL Ticket, then this shall be the Empty Buffer.(/li)(li)(code)policyTicket(/code) - produced if the command succeeds and expiration in the command was non-zero; this ticket will use the TPMT_ST_AUTH_SIGNED structure tag. See 23.2.5(/li)(/ul)}
     */
    public PolicySignedResponse PolicySigned(TPM_HANDLE authObject,TPM_HANDLE policySession,byte[] nonceTPM,byte[] cpHashA,byte[] policyRef,int expiration,TPMU_SIGNATURE auth)
    {
        TPM2_PolicySigned_REQUEST inStruct = new TPM2_PolicySigned_REQUEST();
        PolicySignedResponse outStruct = new PolicySignedResponse();
        inStruct.authObject = authObject;
        inStruct.policySession = policySession;
        inStruct.nonceTPM = nonceTPM;
        inStruct.cpHashA = cpHashA;
        inStruct.policyRef = policyRef;
        inStruct.expiration = expiration;
        inStruct.auth = auth;
        DispatchCommand(TPM_CC.PolicySigned, new TPM_HANDLE[] {authObject,policySession}, 0, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command includes a secret-based authorization to a policy. The caller proves knowledge of the secret value using an authorization session using the authValue associated with authHandle. A password session, an HMAC session, or a policy session containing TPM2_PolicyAuthValue() or TPM2_PolicyPassword() will satisfy this requirement.
     * 
     * @param authHandle handle for an entity providing the authorization Auth Index: 1 Auth Role: USER 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param nonceTPM the policy nonce for the session This can be the Empty Buffer. 
     * @param cpHashA digest of the command parameters to which this authorization is limited This not the cpHash for this command but the cpHash for the command to which this policy session will be applied. If it is not limited, the parameter will be the Empty Buffer. 
     * @param policyRef a reference to a policy relating to the authorization may be the Empty Buffer Size is limited to be no larger than the nonce size supported on the TPM. 
     * @param expiration time when authorization will expire, measured in seconds from the time that nonceTPM was generated If expiration is non-negative, a NULL Ticket is returned. See 23.2.5. 
     * @return TPM2_PolicySecret_RESPONSE{(ul)(li)(code)timeout(/code) - implementation-specific time value used to indicate to the TPM when the ticket expires(/li)(li)(code)policyTicket(/code) - produced if the command succeeds and expiration in the command was non-zero ( See 23.2.5). This ticket will use the TPMT_ST_AUTH_SECRET structure tag(/li)(/ul)}
     */
    public PolicySecretResponse PolicySecret(TPM_HANDLE authHandle,TPM_HANDLE policySession,byte[] nonceTPM,byte[] cpHashA,byte[] policyRef,int expiration)
    {
        TPM2_PolicySecret_REQUEST inStruct = new TPM2_PolicySecret_REQUEST();
        PolicySecretResponse outStruct = new PolicySecretResponse();
        inStruct.authHandle = authHandle;
        inStruct.policySession = policySession;
        inStruct.nonceTPM = nonceTPM;
        inStruct.cpHashA = cpHashA;
        inStruct.policyRef = policyRef;
        inStruct.expiration = expiration;
        DispatchCommand(TPM_CC.PolicySecret, new TPM_HANDLE[] {authHandle,policySession}, 1, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command is similar to TPM2_PolicySigned() except that it takes a ticket instead of a signed authorization. The ticket represents a validated authorization that had an expiration time associated with it.
     * 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param timeout time when authorization will expire The contents are TPM specific. This shall be the value returned when ticket was produced. 
     * @param cpHashA digest of the command parameters to which this authorization is limited If it is not limited, the parameter will be the Empty Buffer. 
     * @param policyRef reference to a qualifier for the policy may be the Empty Buffer 
     * @param authName name of the object that provided the authorization 
     * @param ticket an authorization ticket returned by the TPM in response to a TPM2_PolicySigned() or TPM2_PolicySecret()
     */
    public void PolicyTicket(TPM_HANDLE policySession,byte[] timeout,byte[] cpHashA,byte[] policyRef,byte[] authName,TPMT_TK_AUTH ticket)
    {
        TPM2_PolicyTicket_REQUEST inStruct = new TPM2_PolicyTicket_REQUEST();
        inStruct.policySession = policySession;
        inStruct.timeout = timeout;
        inStruct.cpHashA = cpHashA;
        inStruct.policyRef = policyRef;
        inStruct.authName = authName;
        inStruct.ticket = ticket;
        DispatchCommand(TPM_CC.PolicyTicket, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command allows options in authorizations without requiring that the TPM evaluate all of the options. If a policy may be satisfied by different sets of conditions, the TPM need only evaluate one set that satisfies the policy. This command will indicate that one of the required sets of conditions has been satisfied.
     * 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param pHashList the list of hashes to check for a match
     */
    public void PolicyOR(TPM_HANDLE policySession,TPM2B_DIGEST[] pHashList)
    {
        TPM2_PolicyOR_REQUEST inStruct = new TPM2_PolicyOR_REQUEST();
        inStruct.policySession = policySession;
        inStruct.pHashList = pHashList;
        DispatchCommand(TPM_CC.PolicyOR, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command is used to cause conditional gating of a policy based on PCR. This command together with TPM2_PolicyOR() allows one group of authorizations to occur when PCR are in one state and a different set of authorizations when the PCR are in a different state.
     * 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param pcrDigest expected digest value of the selected PCR using the hash algorithm of the session; may be zero length 
     * @param pcrs the PCR to include in the check digest
     */
    public void PolicyPCR(TPM_HANDLE policySession,byte[] pcrDigest,TPMS_PCR_SELECTION[] pcrs)
    {
        TPM2_PolicyPCR_REQUEST inStruct = new TPM2_PolicyPCR_REQUEST();
        inStruct.policySession = policySession;
        inStruct.pcrDigest = pcrDigest;
        inStruct.pcrs = pcrs;
        DispatchCommand(TPM_CC.PolicyPCR, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command indicates that the authorization will be limited to a specific locality.
     * 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param locality the allowed localities for the policy
     */
    public void PolicyLocality(TPM_HANDLE policySession,TPMA_LOCALITY locality)
    {
        TPM2_PolicyLocality_REQUEST inStruct = new TPM2_PolicyLocality_REQUEST();
        inStruct.policySession = policySession;
        inStruct.locality = locality;
        DispatchCommand(TPM_CC.PolicyLocality, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command is used to cause conditional gating of a policy based on the contents of an NV Index. It is an immediate assertion. The NV index is validated during the TPM2_PolicyNV() command, not when the session is used for authorization.
     * 
     * @param authHandle handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER 
     * @param nvIndex the NV Index of the area to read Auth Index: None 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param operandB the second operand 
     * @param offset the octet offset in the NV Index for the start of operand A 
     * @param operation the comparison to make
     */
    public void PolicyNV(TPM_HANDLE authHandle,TPM_HANDLE nvIndex,TPM_HANDLE policySession,byte[] operandB,int offset,TPM_EO operation)
    {
        TPM2_PolicyNV_REQUEST inStruct = new TPM2_PolicyNV_REQUEST();
        inStruct.authHandle = authHandle;
        inStruct.nvIndex = nvIndex;
        inStruct.policySession = policySession;
        inStruct.operandB = operandB;
        inStruct.offset = (short)offset;
        inStruct.operation = operation;
        DispatchCommand(TPM_CC.PolicyNV, new TPM_HANDLE[] {authHandle,nvIndex,policySession}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command is used to cause conditional gating of a policy based on the contents of the TPMS_TIME_INFO structure.
     * 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param operandB the second operand 
     * @param offset the octet offset in the TPMS_TIME_INFO structure for the start of operand A 
     * @param operation the comparison to make
     */
    public void PolicyCounterTimer(TPM_HANDLE policySession,byte[] operandB,int offset,TPM_EO operation)
    {
        TPM2_PolicyCounterTimer_REQUEST inStruct = new TPM2_PolicyCounterTimer_REQUEST();
        inStruct.policySession = policySession;
        inStruct.operandB = operandB;
        inStruct.offset = (short)offset;
        inStruct.operation = operation;
        DispatchCommand(TPM_CC.PolicyCounterTimer, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command indicates that the authorization will be limited to a specific command code.
     * 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param code the allowed commandCode
     */
    public void PolicyCommandCode(TPM_HANDLE policySession,TPM_CC code)
    {
        TPM2_PolicyCommandCode_REQUEST inStruct = new TPM2_PolicyCommandCode_REQUEST();
        inStruct.policySession = policySession;
        inStruct.code = code;
        DispatchCommand(TPM_CC.PolicyCommandCode, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command indicates that physical presence will need to be asserted at the time the authorization is performed.
     * 
     * @param policySession handle for the policy session being extended Auth Index: None
     */
    public void PolicyPhysicalPresence(TPM_HANDLE policySession)
    {
        TPM2_PolicyPhysicalPresence_REQUEST inStruct = new TPM2_PolicyPhysicalPresence_REQUEST();
        inStruct.policySession = policySession;
        DispatchCommand(TPM_CC.PolicyPhysicalPresence, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command is used to allow a policy to be bound to a specific command and command parameters.
     * 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param cpHashA the cpHash added to the policy
     */
    public void PolicyCpHash(TPM_HANDLE policySession,byte[] cpHashA)
    {
        TPM2_PolicyCpHash_REQUEST inStruct = new TPM2_PolicyCpHash_REQUEST();
        inStruct.policySession = policySession;
        inStruct.cpHashA = cpHashA;
        DispatchCommand(TPM_CC.PolicyCpHash, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command allows a policy to be bound to a specific set of TPM entities without being bound to the parameters of the command. This is most useful for commands such as TPM2_Duplicate() and for TPM2_PCR_Event() when the referenced PCR requires a policy.
     * 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param nameHash the digest to be added to the policy
     */
    public void PolicyNameHash(TPM_HANDLE policySession,byte[] nameHash)
    {
        TPM2_PolicyNameHash_REQUEST inStruct = new TPM2_PolicyNameHash_REQUEST();
        inStruct.policySession = policySession;
        inStruct.nameHash = nameHash;
        DispatchCommand(TPM_CC.PolicyNameHash, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command allows qualification of duplication to allow duplication to a selected new parent.
     * 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param objectName the Name of the object to be duplicated 
     * @param newParentName the Name of the new parent 
     * @param includeObject if YES, the objectName will be included in the value in policySessionpolicyDigest
     */
    public void PolicyDuplicationSelect(TPM_HANDLE policySession,byte[] objectName,byte[] newParentName,byte includeObject)
    {
        TPM2_PolicyDuplicationSelect_REQUEST inStruct = new TPM2_PolicyDuplicationSelect_REQUEST();
        inStruct.policySession = policySession;
        inStruct.objectName = objectName;
        inStruct.newParentName = newParentName;
        inStruct.includeObject = includeObject;
        DispatchCommand(TPM_CC.PolicyDuplicationSelect, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command allows policies to change. If a policy were static, then it would be difficult to add users to a policy. This command lets a policy authority sign a new policy so that it may be used in an existing policy.
     * 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param approvedPolicy digest of the policy being approved 
     * @param policyRef a policy qualifier 
     * @param keySign Name of a key that can sign a policy addition 
     * @param checkTicket ticket validating that approvedPolicy and policyRef were signed by keySign
     */
    public void PolicyAuthorize(TPM_HANDLE policySession,byte[] approvedPolicy,byte[] policyRef,byte[] keySign,TPMT_TK_VERIFIED checkTicket)
    {
        TPM2_PolicyAuthorize_REQUEST inStruct = new TPM2_PolicyAuthorize_REQUEST();
        inStruct.policySession = policySession;
        inStruct.approvedPolicy = approvedPolicy;
        inStruct.policyRef = policyRef;
        inStruct.keySign = keySign;
        inStruct.checkTicket = checkTicket;
        DispatchCommand(TPM_CC.PolicyAuthorize, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command allows a policy to be bound to the authorization value of the authorized entity.
     * 
     * @param policySession handle for the policy session being extended Auth Index: None
     */
    public void PolicyAuthValue(TPM_HANDLE policySession)
    {
        TPM2_PolicyAuthValue_REQUEST inStruct = new TPM2_PolicyAuthValue_REQUEST();
        inStruct.policySession = policySession;
        DispatchCommand(TPM_CC.PolicyAuthValue, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command allows a policy to be bound to the authorization value of the authorized object.
     * 
     * @param policySession handle for the policy session being extended Auth Index: None
     */
    public void PolicyPassword(TPM_HANDLE policySession)
    {
        TPM2_PolicyPassword_REQUEST inStruct = new TPM2_PolicyPassword_REQUEST();
        inStruct.policySession = policySession;
        DispatchCommand(TPM_CC.PolicyPassword, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command returns the current policyDigest of the session. This command allows the TPM to be used to perform the actions required to pre-compute the authPolicy for an object.
     * 
     * @param policySession handle for the policy session Auth Index: None 
     * @return the current value of the policySessionpolicyDigest
     */
    public byte[] PolicyGetDigest(TPM_HANDLE policySession)
    {
        TPM2_PolicyGetDigest_REQUEST inStruct = new TPM2_PolicyGetDigest_REQUEST();
        PolicyGetDigestResponse outStruct = new PolicyGetDigestResponse();
        inStruct.policySession = policySession;
        DispatchCommand(TPM_CC.PolicyGetDigest, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, outStruct);
        return outStruct.policyDigest;
    }
    
    /**
     * This command allows a policy to be bound to the TPMA_NV_WRITTEN attributes. This is a deferred assertion. Values are stored in the policy session context and checked when the policy is used for authorization.
     * 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param writtenSet YES if NV Index is required to have been written NO if NV Index is required not to have been written
     */
    public void PolicyNvWritten(TPM_HANDLE policySession,byte writtenSet)
    {
        TPM2_PolicyNvWritten_REQUEST inStruct = new TPM2_PolicyNvWritten_REQUEST();
        inStruct.policySession = policySession;
        inStruct.writtenSet = writtenSet;
        DispatchCommand(TPM_CC.PolicyNvWritten, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command allows a policy to be bound to a specific creation template. This is most useful for an object creation command such as TPM2_Create(), TPM2_CreatePrimary(), or TPM2_CreateLoaded().
     * 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param templateHash the digest to be added to the policy
     */
    public void PolicyTemplate(TPM_HANDLE policySession,byte[] templateHash)
    {
        TPM2_PolicyTemplate_REQUEST inStruct = new TPM2_PolicyTemplate_REQUEST();
        inStruct.policySession = policySession;
        inStruct.templateHash = templateHash;
        DispatchCommand(TPM_CC.PolicyTemplate, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command provides a capability that is the equivalent of a revocable policy. With TPM2_PolicyAuthorize(), the authorization ticket never expires, so the authorization may not be withdrawn. With this command, the approved policy is kept in an NV Index location so that the policy may be changed as needed to render the old policy unusable.
     * 
     * @param authHandle handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER 
     * @param nvIndex the NV Index of the area to read Auth Index: None 
     * @param policySession handle for the policy session being extended Auth Index: None
     */
    public void PolicyAuthorizeNV(TPM_HANDLE authHandle,TPM_HANDLE nvIndex,TPM_HANDLE policySession)
    {
        TPM2_PolicyAuthorizeNV_REQUEST inStruct = new TPM2_PolicyAuthorizeNV_REQUEST();
        inStruct.authHandle = authHandle;
        inStruct.nvIndex = nvIndex;
        inStruct.policySession = policySession;
        DispatchCommand(TPM_CC.PolicyAuthorizeNV, new TPM_HANDLE[] {authHandle,nvIndex,policySession}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command is used to create a Primary Object under one of the Primary Seeds or a Temporary Object under TPM_RH_NULL. The command uses a TPM2B_PUBLIC as a template for the object to be created. The size of the unique field shall not be checked for consistency with the other object parameters. The command will create and load a Primary Object. The sensitive area is not returned.
     * 
     * @param primaryHandle TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM+{PP}, or TPM_RH_NULL Auth Index: 1 Auth Role: USER 
     * @param inSensitive the sensitive data, see TPM 2.0 Part 1 Sensitive Values 
     * @param inPublic the public template 
     * @param outsideInfo data that will be included in the creation data for this object to provide permanent, verifiable linkage between this object and some object owner data 
     * @param creationPCR PCR that will be used in creation data 
     * @return TPM2_CreatePrimary_RESPONSE{(ul)(li)(code)handle(/code) - handle of type TPM_HT_TRANSIENT for created Primary Object(/li)(li)(code)outPublic(/code) - the public portion of the created object(/li)(li)(code)creationData(/code) - contains a TPMT_CREATION_DATA(/li)(li)(code)creationHash(/code) - digest of creationData using nameAlg of outPublic(/li)(li)(code)creationTicket(/code) - ticket used by TPM2_CertifyCreation() to validate that the creation data was produced by the TPM(/li)(li)(code)name(/code) - the name of the created object(/li)(/ul)}
     */
    public CreatePrimaryResponse CreatePrimary(TPM_HANDLE primaryHandle,TPMS_SENSITIVE_CREATE inSensitive,TPMT_PUBLIC inPublic,byte[] outsideInfo,TPMS_PCR_SELECTION[] creationPCR)
    {
        TPM2_CreatePrimary_REQUEST inStruct = new TPM2_CreatePrimary_REQUEST();
        CreatePrimaryResponse outStruct = new CreatePrimaryResponse();
        inStruct.primaryHandle = primaryHandle;
        inStruct.inSensitive = inSensitive;
        inStruct.inPublic = inPublic;
        inStruct.outsideInfo = outsideInfo;
        inStruct.creationPCR = creationPCR;
        DispatchCommand(TPM_CC.CreatePrimary, new TPM_HANDLE[] {primaryHandle}, 1, 1, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command enables and disables use of a hierarchy and its associated NV storage. The command allows phEnable, phEnableNV, shEnable, and ehEnable to be changed when the proper authorization is provided.
     * 
     * @param authHandle TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER 
     * @param enable the enable being modified TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM, or TPM_RH_PLATFORM_NV 
     * @param state YES if the enable should be SET, NO if the enable should be CLEAR
     */
    public void HierarchyControl(TPM_HANDLE authHandle,TPM_HANDLE enable,byte state)
    {
        TPM2_HierarchyControl_REQUEST inStruct = new TPM2_HierarchyControl_REQUEST();
        inStruct.authHandle = authHandle;
        inStruct.enable = enable;
        inStruct.state = state;
        DispatchCommand(TPM_CC.HierarchyControl, new TPM_HANDLE[] {authHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command allows setting of the authorization policy for the lockout (lockoutPolicy), the platform hierarchy (platformPolicy), the storage hierarchy (ownerPolicy), and the endorsement hierarchy (endorsementPolicy).
     * 
     * @param authHandle TPM_RH_LOCKOUT, TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER 
     * @param authPolicy an authorization policy digest; may be the Empty Buffer If hashAlg is TPM_ALG_NULL, then this shall be an Empty Buffer. 
     * @param hashAlg the hash algorithm to use for the policy If the authPolicy is an Empty Buffer, then this field shall be TPM_ALG_NULL.
     */
    public void SetPrimaryPolicy(TPM_HANDLE authHandle,byte[] authPolicy,TPM_ALG_ID hashAlg)
    {
        TPM2_SetPrimaryPolicy_REQUEST inStruct = new TPM2_SetPrimaryPolicy_REQUEST();
        inStruct.authHandle = authHandle;
        inStruct.authPolicy = authPolicy;
        inStruct.hashAlg = hashAlg;
        DispatchCommand(TPM_CC.SetPrimaryPolicy, new TPM_HANDLE[] {authHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This replaces the current platform primary seed (PPS) with a value from the RNG and sets platformPolicy to the default initialization value (the Empty Buffer).
     * 
     * @param authHandle TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER
     */
    public void ChangePPS(TPM_HANDLE authHandle)
    {
        TPM2_ChangePPS_REQUEST inStruct = new TPM2_ChangePPS_REQUEST();
        inStruct.authHandle = authHandle;
        DispatchCommand(TPM_CC.ChangePPS, new TPM_HANDLE[] {authHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This replaces the current endorsement primary seed (EPS) with a value from the RNG and sets the Endorsement hierarchy controls to their default initialization values: ehEnable is SET, endorsementAuth and endorsementPolicy are both set to the Empty Buffer. It will flush any resident objects (transient or persistent) in the Endorsement hierarchy and not allow objects in the hierarchy associated with the previous EPS to be loaded.
     * 
     * @param authHandle TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER
     */
    public void ChangeEPS(TPM_HANDLE authHandle)
    {
        TPM2_ChangeEPS_REQUEST inStruct = new TPM2_ChangeEPS_REQUEST();
        inStruct.authHandle = authHandle;
        DispatchCommand(TPM_CC.ChangeEPS, new TPM_HANDLE[] {authHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command removes all TPM context associated with a specific Owner.
     * 
     * @param authHandle TPM_RH_LOCKOUT or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER
     */
    public void Clear(TPM_HANDLE authHandle)
    {
        TPM2_Clear_REQUEST inStruct = new TPM2_Clear_REQUEST();
        inStruct.authHandle = authHandle;
        DispatchCommand(TPM_CC.Clear, new TPM_HANDLE[] {authHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * TPM2_ClearControl() disables and enables the execution of TPM2_Clear().
     * 
     * @param auth TPM_RH_LOCKOUT or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER 
     * @param disable YES if the disableOwnerClear flag is to be SET, NO if the flag is to be CLEAR.
     */
    public void ClearControl(TPM_HANDLE auth,byte disable)
    {
        TPM2_ClearControl_REQUEST inStruct = new TPM2_ClearControl_REQUEST();
        inStruct.auth = auth;
        inStruct.disable = disable;
        DispatchCommand(TPM_CC.ClearControl, new TPM_HANDLE[] {auth}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command allows the authorization secret for a hierarchy or lockout to be changed using the current authorization value as the command authorization.
     * 
     * @param authHandle TPM_RH_LOCKOUT, TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER 
     * @param newAuth new authorization value
     */
    public void HierarchyChangeAuth(TPM_HANDLE authHandle,byte[] newAuth)
    {
        TPM2_HierarchyChangeAuth_REQUEST inStruct = new TPM2_HierarchyChangeAuth_REQUEST();
        inStruct.authHandle = authHandle;
        inStruct.newAuth = newAuth;
        DispatchCommand(TPM_CC.HierarchyChangeAuth, new TPM_HANDLE[] {authHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command cancels the effect of a TPM lockout due to a number of successive authorization failures. If this command is properly authorized, the lockout counter is set to zero.
     * 
     * @param lockHandle TPM_RH_LOCKOUT Auth Index: 1 Auth Role: USER
     */
    public void DictionaryAttackLockReset(TPM_HANDLE lockHandle)
    {
        TPM2_DictionaryAttackLockReset_REQUEST inStruct = new TPM2_DictionaryAttackLockReset_REQUEST();
        inStruct.lockHandle = lockHandle;
        DispatchCommand(TPM_CC.DictionaryAttackLockReset, new TPM_HANDLE[] {lockHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command changes the lockout parameters.
     * 
     * @param lockHandle TPM_RH_LOCKOUT Auth Index: 1 Auth Role: USER 
     * @param newMaxTries count of authorization failures before the lockout is imposed 
     * @param newRecoveryTime time in seconds before the authorization failure count is automatically decremented A value of zero indicates that DA protection is disabled. 
     * @param lockoutRecovery time in seconds after a lockoutAuth failure before use of lockoutAuth is allowed A value of zero indicates that a reboot is required.
     */
    public void DictionaryAttackParameters(TPM_HANDLE lockHandle,int newMaxTries,int newRecoveryTime,int lockoutRecovery)
    {
        TPM2_DictionaryAttackParameters_REQUEST inStruct = new TPM2_DictionaryAttackParameters_REQUEST();
        inStruct.lockHandle = lockHandle;
        inStruct.newMaxTries = newMaxTries;
        inStruct.newRecoveryTime = newRecoveryTime;
        inStruct.lockoutRecovery = lockoutRecovery;
        DispatchCommand(TPM_CC.DictionaryAttackParameters, new TPM_HANDLE[] {lockHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command is used to determine which commands require assertion of Physical Presence (PP) in addition to platformAuth/platformPolicy.
     * 
     * @param auth TPM_RH_PLATFORM+PP Auth Index: 1 Auth Role: USER + Physical Presence 
     * @param setList list of commands to be added to those that will require that Physical Presence be asserted 
     * @param clearList list of commands that will no longer require that Physical Presence be asserted
     */
    public void PP_Commands(TPM_HANDLE auth,TPM_CC[] setList,TPM_CC[] clearList)
    {
        TPM2_PP_Commands_REQUEST inStruct = new TPM2_PP_Commands_REQUEST();
        inStruct.auth = auth;
        inStruct.setList = setList;
        inStruct.clearList = clearList;
        DispatchCommand(TPM_CC.PP_Commands, new TPM_HANDLE[] {auth}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command allows the platform to change the set of algorithms that are used by the TPM. The algorithmSet setting is a vendor-dependent value.
     * 
     * @param authHandle TPM_RH_PLATFORM Auth Index: 1 Auth Role: USER 
     * @param algorithmSet a TPM vendor-dependent value indicating the algorithm set selection
     */
    public void SetAlgorithmSet(TPM_HANDLE authHandle,int algorithmSet)
    {
        TPM2_SetAlgorithmSet_REQUEST inStruct = new TPM2_SetAlgorithmSet_REQUEST();
        inStruct.authHandle = authHandle;
        inStruct.algorithmSet = algorithmSet;
        DispatchCommand(TPM_CC.SetAlgorithmSet, new TPM_HANDLE[] {authHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command uses platformPolicy and a TPM Vendor Authorization Key to authorize a Field Upgrade Manifest.
     * 
     * @param authorization TPM_RH_PLATFORM+{PP} Auth Index:1 Auth Role: ADMIN 
     * @param keyHandle handle of a public area that contains the TPM Vendor Authorization Key that will be used to validate manifestSignature Auth Index: None 
     * @param fuDigest digest of the first block in the field upgrade sequence 
     * @param manifestSignature signature over fuDigest using the key associated with keyHandle (not optional)
     */
    public void FieldUpgradeStart(TPM_HANDLE authorization,TPM_HANDLE keyHandle,byte[] fuDigest,TPMU_SIGNATURE manifestSignature)
    {
        TPM2_FieldUpgradeStart_REQUEST inStruct = new TPM2_FieldUpgradeStart_REQUEST();
        inStruct.authorization = authorization;
        inStruct.keyHandle = keyHandle;
        inStruct.fuDigest = fuDigest;
        inStruct.manifestSignature = manifestSignature;
        DispatchCommand(TPM_CC.FieldUpgradeStart, new TPM_HANDLE[] {authorization,keyHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command will take the actual field upgrade image to be installed on the TPM. The exact format of fuData is vendor-specific. This command is only possible following a successful TPM2_FieldUpgradeStart(). If the TPM has not received a properly authorized TPM2_FieldUpgradeStart(), then the TPM shall return TPM_RC_FIELDUPGRADE.
     * 
     * @param fuData field upgrade image data 
     * @return TPM2_FieldUpgradeData_RESPONSE{(ul)(li)(code)nextDigest(/code) - tagged digest of the next block TPM_ALG_NULL if field update is complete(/li)(li)(code)firstDigest(/code) - tagged digest of the first block of the sequence(/li)(/ul)}
     */
    public FieldUpgradeDataResponse FieldUpgradeData(byte[] fuData)
    {
        TPM2_FieldUpgradeData_REQUEST inStruct = new TPM2_FieldUpgradeData_REQUEST();
        FieldUpgradeDataResponse outStruct = new FieldUpgradeDataResponse();
        inStruct.fuData = fuData;
        DispatchCommand(TPM_CC.FieldUpgradeData, new TPM_HANDLE[] {}, 0, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command is used to read a copy of the current firmware installed in the TPM.
     * 
     * @param sequenceNumber the number of previous calls to this command in this sequence set to 0 on the first call 
     * @return field upgrade image data
     */
    public byte[] FirmwareRead(int sequenceNumber)
    {
        TPM2_FirmwareRead_REQUEST inStruct = new TPM2_FirmwareRead_REQUEST();
        FirmwareReadResponse outStruct = new FirmwareReadResponse();
        inStruct.sequenceNumber = sequenceNumber;
        DispatchCommand(TPM_CC.FirmwareRead, new TPM_HANDLE[] {}, 0, 0, inStruct, outStruct);
        return outStruct.fuData;
    }
    
    /**
     * This command saves a session context, object context, or sequence object context outside the TPM.
     * 
     * @param saveHandle handle of the resource to save Auth Index: None 
     * @return This structure is used in TPM2_ContextLoad() and TPM2_ContextSave(). If the values of the TPMS_CONTEXT structure in TPM2_ContextLoad() are not the same as the values when the context was saved (TPM2_ContextSave()), then the TPM shall not load the context.
     */
    public TPMS_CONTEXT ContextSave(TPM_HANDLE saveHandle)
    {
        TPM2_ContextSave_REQUEST inStruct = new TPM2_ContextSave_REQUEST();
        ContextSaveResponse outStruct = new ContextSaveResponse();
        inStruct.saveHandle = saveHandle;
        DispatchCommand(TPM_CC.ContextSave, new TPM_HANDLE[] {saveHandle}, 0, 0, inStruct, outStruct);
        return outStruct.context;
    }
    
    /**
     * This command is used to reload a context that has been saved by TPM2_ContextSave().
     * 
     * @param context the context blob 
     * @return the handle assigned to the resource after it has been successfully loaded
     */
    public TPM_HANDLE ContextLoad(TPMS_CONTEXT context)
    {
        TPM2_ContextLoad_REQUEST inStruct = new TPM2_ContextLoad_REQUEST();
        ContextLoadResponse outStruct = new ContextLoadResponse();
        inStruct.context = context;
        DispatchCommand(TPM_CC.ContextLoad, new TPM_HANDLE[] {}, 0, 1, inStruct, outStruct);
        return outStruct.handle;
    }
    
    /**
     * This command causes all context associated with a loaded object, sequence object, or session to be removed from TPM memory.
     * 
     * @param flushHandle the handle of the item to flush NOTE This is a use of a handle as a parameter.
     */
    public void FlushContext(TPM_HANDLE flushHandle)
    {
        TPM2_FlushContext_REQUEST inStruct = new TPM2_FlushContext_REQUEST();
        inStruct.flushHandle = flushHandle;
        DispatchCommand(TPM_CC.FlushContext, new TPM_HANDLE[] {}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command allows certain Transient Objects to be made persistent or a persistent object to be evicted.
     * 
     * @param auth TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER 
     * @param objectHandle the handle of a loaded object Auth Index: None 
     * @param persistentHandle if objectHandle is a transient object handle, then this is the persistent handle for the object if objectHandle is a persistent object handle, then it shall be the same value as persistentHandle
     */
    public void EvictControl(TPM_HANDLE auth,TPM_HANDLE objectHandle,TPM_HANDLE persistentHandle)
    {
        TPM2_EvictControl_REQUEST inStruct = new TPM2_EvictControl_REQUEST();
        inStruct.auth = auth;
        inStruct.objectHandle = objectHandle;
        inStruct.persistentHandle = persistentHandle;
        DispatchCommand(TPM_CC.EvictControl, new TPM_HANDLE[] {auth,objectHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command reads the current TPMS_TIME_INFO structure that contains the current setting of Time, Clock, resetCount, and restartCount.
     * 
     * @return This structure is used in the TPM2_GetTime() attestation.
     */
    public TPMS_TIME_INFO ReadClock()
    {
        TPM2_ReadClock_REQUEST inStruct = new TPM2_ReadClock_REQUEST();
        ReadClockResponse outStruct = new ReadClockResponse();
        DispatchCommand(TPM_CC.ReadClock, new TPM_HANDLE[] {}, 0, 0, inStruct, outStruct);
        return outStruct.currentTime;
    }
    
    /**
     * This command is used to advance the value of the TPMs Clock. The command will fail if newTime is less than the current value of Clock or if the new time is greater than FFFF00000000000016. If both of these checks succeed, Clock is set to newTime. If either of these checks fails, the TPM shall return TPM_RC_VALUE and make no change to Clock.
     * 
     * @param auth TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER 
     * @param newTime new Clock setting in milliseconds
     */
    public void ClockSet(TPM_HANDLE auth,long newTime)
    {
        TPM2_ClockSet_REQUEST inStruct = new TPM2_ClockSet_REQUEST();
        inStruct.auth = auth;
        inStruct.newTime = newTime;
        DispatchCommand(TPM_CC.ClockSet, new TPM_HANDLE[] {auth}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command adjusts the rate of advance of Clock and Time to provide a better approximation to real time.
     * 
     * @param auth TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER 
     * @param rateAdjust Adjustment to current Clock update rate
     */
    public void ClockRateAdjust(TPM_HANDLE auth,TPM_CLOCK_ADJUST rateAdjust)
    {
        TPM2_ClockRateAdjust_REQUEST inStruct = new TPM2_ClockRateAdjust_REQUEST();
        inStruct.auth = auth;
        inStruct.rateAdjust = rateAdjust;
        DispatchCommand(TPM_CC.ClockRateAdjust, new TPM_HANDLE[] {auth}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command returns various information regarding the TPM and its current state.
     * 
     * @param capability group selection; determines the format of the response 
     * @param property further definition of information 
     * @param propertyCount number of properties of the indicated type to return 
     * @return TPM2_GetCapability_RESPONSE{(ul)(li)(code)moreData(/code) - flag to indicate if there are more values of this type(/li)(li)(code)capabilityData(/code) - the capability data(/li)(/ul)}
     */
    public GetCapabilityResponse GetCapability(TPM_CAP capability,int property,int propertyCount)
    {
        TPM2_GetCapability_REQUEST inStruct = new TPM2_GetCapability_REQUEST();
        GetCapabilityResponse outStruct = new GetCapabilityResponse();
        inStruct.capability = capability;
        inStruct.property = property;
        inStruct.propertyCount = propertyCount;
        DispatchCommand(TPM_CC.GetCapability, new TPM_HANDLE[] {}, 0, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command is used to check to see if specific combinations of algorithm parameters are supported.
     * 
     * @param parameters algorithm parameters to be validated
     */
    public void TestParms(TPMU_PUBLIC_PARMS parameters)
    {
        TPM2_TestParms_REQUEST inStruct = new TPM2_TestParms_REQUEST();
        inStruct.parameters = parameters;
        DispatchCommand(TPM_CC.TestParms, new TPM_HANDLE[] {}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This command defines the attributes of an NV Index and causes the TPM to reserve space to hold the data associated with the NV Index. If a definition already exists at the NV Index, the TPM will return TPM_RC_NV_DEFINED.
     * 
     * @param authHandle TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER 
     * @param auth the authorization value 
     * @param publicInfo the public parameters of the NV area
     */
    public void NV_DefineSpace(TPM_HANDLE authHandle,byte[] auth,TPMS_NV_PUBLIC publicInfo)
    {
        TPM2_NV_DefineSpace_REQUEST inStruct = new TPM2_NV_DefineSpace_REQUEST();
        inStruct.authHandle = authHandle;
        inStruct.auth = auth;
        inStruct.publicInfo = publicInfo;
        DispatchCommand(TPM_CC.NV_DefineSpace, new TPM_HANDLE[] {authHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command removes an Index from the TPM.
     * 
     * @param authHandle TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER 
     * @param nvIndex the NV Index to remove from NV space Auth Index: None
     */
    public void NV_UndefineSpace(TPM_HANDLE authHandle,TPM_HANDLE nvIndex)
    {
        TPM2_NV_UndefineSpace_REQUEST inStruct = new TPM2_NV_UndefineSpace_REQUEST();
        inStruct.authHandle = authHandle;
        inStruct.nvIndex = nvIndex;
        DispatchCommand(TPM_CC.NV_UndefineSpace, new TPM_HANDLE[] {authHandle,nvIndex}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command allows removal of a platform-created NV Index that has TPMA_NV_POLICY_DELETE SET.
     * 
     * @param nvIndex Index to be deleted Auth Index: 1 Auth Role: ADMIN 
     * @param platform TPM_RH_PLATFORM + {PP} Auth Index: 2 Auth Role: USER
     */
    public void NV_UndefineSpaceSpecial(TPM_HANDLE nvIndex,TPM_HANDLE platform)
    {
        TPM2_NV_UndefineSpaceSpecial_REQUEST inStruct = new TPM2_NV_UndefineSpaceSpecial_REQUEST();
        inStruct.nvIndex = nvIndex;
        inStruct.platform = platform;
        DispatchCommand(TPM_CC.NV_UndefineSpaceSpecial, new TPM_HANDLE[] {nvIndex,platform}, 2, 0, inStruct, null);
        return;
    }
    
    /**
     * This command is used to read the public area and Name of an NV Index. The public area of an Index is not privacy-sensitive and no authorization is required to read this data.
     * 
     * @param nvIndex the NV Index Auth Index: None 
     * @return TPM2_NV_ReadPublic_RESPONSE{(ul)(li)(code)nvPublic(/code) - the public area of the NV Index(/li)(li)(code)nvName(/code) - the Name of the nvIndex(/li)(/ul)}
     */
    public NV_ReadPublicResponse NV_ReadPublic(TPM_HANDLE nvIndex)
    {
        TPM2_NV_ReadPublic_REQUEST inStruct = new TPM2_NV_ReadPublic_REQUEST();
        NV_ReadPublicResponse outStruct = new NV_ReadPublicResponse();
        inStruct.nvIndex = nvIndex;
        DispatchCommand(TPM_CC.NV_ReadPublic, new TPM_HANDLE[] {nvIndex}, 0, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * This command writes a value to an area in NV memory that was previously defined by TPM2_NV_DefineSpace().
     * 
     * @param authHandle handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER 
     * @param nvIndex the NV Index of the area to write Auth Index: None 
     * @param data the data to write 
     * @param offset the octet offset into the NV Area
     */
    public void NV_Write(TPM_HANDLE authHandle,TPM_HANDLE nvIndex,byte[] data,int offset)
    {
        TPM2_NV_Write_REQUEST inStruct = new TPM2_NV_Write_REQUEST();
        inStruct.authHandle = authHandle;
        inStruct.nvIndex = nvIndex;
        inStruct.data = data;
        inStruct.offset = (short)offset;
        DispatchCommand(TPM_CC.NV_Write, new TPM_HANDLE[] {authHandle,nvIndex}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command is used to increment the value in an NV Index that has the TPM_NT_COUNTER attribute. The data value of the NV Index is incremented by one.
     * 
     * @param authHandle handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER 
     * @param nvIndex the NV Index to increment Auth Index: None
     */
    public void NV_Increment(TPM_HANDLE authHandle,TPM_HANDLE nvIndex)
    {
        TPM2_NV_Increment_REQUEST inStruct = new TPM2_NV_Increment_REQUEST();
        inStruct.authHandle = authHandle;
        inStruct.nvIndex = nvIndex;
        DispatchCommand(TPM_CC.NV_Increment, new TPM_HANDLE[] {authHandle,nvIndex}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command extends a value to an area in NV memory that was previously defined by TPM2_NV_DefineSpace.
     * 
     * @param authHandle handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER 
     * @param nvIndex the NV Index to extend Auth Index: None 
     * @param data the data to extend
     */
    public void NV_Extend(TPM_HANDLE authHandle,TPM_HANDLE nvIndex,byte[] data)
    {
        TPM2_NV_Extend_REQUEST inStruct = new TPM2_NV_Extend_REQUEST();
        inStruct.authHandle = authHandle;
        inStruct.nvIndex = nvIndex;
        inStruct.data = data;
        DispatchCommand(TPM_CC.NV_Extend, new TPM_HANDLE[] {authHandle,nvIndex}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command is used to SET bits in an NV Index that was created as a bit field. Any number of bits from 0 to 64 may be SET. The contents of bits are ORed with the current contents of the NV Index.
     * 
     * @param authHandle handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER 
     * @param nvIndex NV Index of the area in which the bit is to be set Auth Index: None 
     * @param bits the data to OR with the current contents
     */
    public void NV_SetBits(TPM_HANDLE authHandle,TPM_HANDLE nvIndex,long bits)
    {
        TPM2_NV_SetBits_REQUEST inStruct = new TPM2_NV_SetBits_REQUEST();
        inStruct.authHandle = authHandle;
        inStruct.nvIndex = nvIndex;
        inStruct.bits = bits;
        DispatchCommand(TPM_CC.NV_SetBits, new TPM_HANDLE[] {authHandle,nvIndex}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * If the TPMA_NV_WRITEDEFINE or TPMA_NV_WRITE_STCLEAR attributes of an NV location are SET, then this command may be used to inhibit further writes of the NV Index.
     * 
     * @param authHandle handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER 
     * @param nvIndex the NV Index of the area to lock Auth Index: None
     */
    public void NV_WriteLock(TPM_HANDLE authHandle,TPM_HANDLE nvIndex)
    {
        TPM2_NV_WriteLock_REQUEST inStruct = new TPM2_NV_WriteLock_REQUEST();
        inStruct.authHandle = authHandle;
        inStruct.nvIndex = nvIndex;
        DispatchCommand(TPM_CC.NV_WriteLock, new TPM_HANDLE[] {authHandle,nvIndex}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * The command will SET TPMA_NV_WRITELOCKED for all indexes that have their TPMA_NV_GLOBALLOCK attribute SET.
     * 
     * @param authHandle TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER
     */
    public void NV_GlobalWriteLock(TPM_HANDLE authHandle)
    {
        TPM2_NV_GlobalWriteLock_REQUEST inStruct = new TPM2_NV_GlobalWriteLock_REQUEST();
        inStruct.authHandle = authHandle;
        DispatchCommand(TPM_CC.NV_GlobalWriteLock, new TPM_HANDLE[] {authHandle}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command reads a value from an area in NV memory previously defined by TPM2_NV_DefineSpace().
     * 
     * @param authHandle the handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER 
     * @param nvIndex the NV Index to be read Auth Index: None 
     * @param size number of octets to read 
     * @param offset octet offset into the NV area This value shall be less than or equal to the size of the nvIndex data. 
     * @return the data read
     */
    public byte[] NV_Read(TPM_HANDLE authHandle,TPM_HANDLE nvIndex,int size,int offset)
    {
        TPM2_NV_Read_REQUEST inStruct = new TPM2_NV_Read_REQUEST();
        NV_ReadResponse outStruct = new NV_ReadResponse();
        inStruct.authHandle = authHandle;
        inStruct.nvIndex = nvIndex;
        inStruct.size = (short)size;
        inStruct.offset = (short)offset;
        DispatchCommand(TPM_CC.NV_Read, new TPM_HANDLE[] {authHandle,nvIndex}, 1, 0, inStruct, outStruct);
        return outStruct.data;
    }
    
    /**
     * If TPMA_NV_READ_STCLEAR is SET in an Index, then this command may be used to prevent further reads of the NV Index until the next TPM2_Startup (TPM_SU_CLEAR).
     * 
     * @param authHandle the handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER 
     * @param nvIndex the NV Index to be locked Auth Index: None
     */
    public void NV_ReadLock(TPM_HANDLE authHandle,TPM_HANDLE nvIndex)
    {
        TPM2_NV_ReadLock_REQUEST inStruct = new TPM2_NV_ReadLock_REQUEST();
        inStruct.authHandle = authHandle;
        inStruct.nvIndex = nvIndex;
        DispatchCommand(TPM_CC.NV_ReadLock, new TPM_HANDLE[] {authHandle,nvIndex}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * This command allows the authorization secret for an NV Index to be changed.
     * 
     * @param nvIndex handle of the entity Auth Index: 1 Auth Role: ADMIN 
     * @param newAuth new authorization value
     */
    public void NV_ChangeAuth(TPM_HANDLE nvIndex,byte[] newAuth)
    {
        TPM2_NV_ChangeAuth_REQUEST inStruct = new TPM2_NV_ChangeAuth_REQUEST();
        inStruct.nvIndex = nvIndex;
        inStruct.newAuth = newAuth;
        DispatchCommand(TPM_CC.NV_ChangeAuth, new TPM_HANDLE[] {nvIndex}, 1, 0, inStruct, null);
        return;
    }
    
    /**
     * The purpose of this command is to certify the contents of an NV Index or portion of an NV Index.
     * 
     * @param signHandle handle of the key used to sign the attestation structure Auth Index: 1 Auth Role: USER 
     * @param authHandle handle indicating the source of the authorization value for the NV Index Auth Index: 2 Auth Role: USER 
     * @param nvIndex Index for the area to be certified Auth Index: None 
     * @param qualifyingData user-provided qualifying data 
     * @param inScheme signing scheme to use if the scheme for signHandle is TPM_ALG_NULL 
     * @param size number of octets to certify 
     * @param offset octet offset into the NV area This value shall be less than or equal to the size of the nvIndex data. 
     * @return TPM2_NV_Certify_RESPONSE{(ul)(li)(code)certifyInfo(/code) - the structure that was signed(/li)(li)(code)signature(/code) - the asymmetric signature over certifyInfo using the key referenced by signHandle(/li)(/ul)}
     */
    public NV_CertifyResponse NV_Certify(TPM_HANDLE signHandle,TPM_HANDLE authHandle,TPM_HANDLE nvIndex,byte[] qualifyingData,TPMU_SIG_SCHEME inScheme,int size,int offset)
    {
        TPM2_NV_Certify_REQUEST inStruct = new TPM2_NV_Certify_REQUEST();
        NV_CertifyResponse outStruct = new NV_CertifyResponse();
        inStruct.signHandle = signHandle;
        inStruct.authHandle = authHandle;
        inStruct.nvIndex = nvIndex;
        inStruct.qualifyingData = qualifyingData;
        inStruct.inScheme = inScheme;
        inStruct.size = (short)size;
        inStruct.offset = (short)offset;
        DispatchCommand(TPM_CC.NV_Certify, new TPM_HANDLE[] {signHandle,authHandle,nvIndex}, 2, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * The purpose of this command is to obtain information about an Attached Component referenced by an AC handle.
     * 
     * @param ac handle indicating the Attached Component Auth Index: None 
     * @param capability starting info type 
     * @param count maximum number of values to return 
     * @return TPM2_AC_GetCapability_RESPONSE{(ul)(li)(code)moreData(/code) - flag to indicate whether there are more values(/li)(li)(code)capabilitiesData(/code) - list of capabilities(/li)(/ul)}
     */
    public AC_GetCapabilityResponse AC_GetCapability(TPM_HANDLE ac,TPM_AT capability,int count)
    {
        TPM2_AC_GetCapability_REQUEST inStruct = new TPM2_AC_GetCapability_REQUEST();
        AC_GetCapabilityResponse outStruct = new AC_GetCapabilityResponse();
        inStruct.ac = ac;
        inStruct.capability = capability;
        inStruct.count = count;
        DispatchCommand(TPM_CC.AC_GetCapability, new TPM_HANDLE[] {ac}, 0, 0, inStruct, outStruct);
        return outStruct;
    }
    
    /**
     * The purpose of this command is to send (copy) a loaded object from the TPM to an Attached Component.
     * 
     * @param sendObject handle of the object being sent to ac Auth Index: 1 Auth Role: DUP 
     * @param authHandle the handle indicating the source of the authorization value Auth Index: 2 Auth Role: USER 
     * @param ac handle indicating the Attached Component to which the object will be sent Auth Index: None 
     * @param acDataIn Optional non sensitive information related to the object 
     * @return May include AC specific data or information about an error.
     */
    public TPMS_AC_OUTPUT AC_Send(TPM_HANDLE sendObject,TPM_HANDLE authHandle,TPM_HANDLE ac,byte[] acDataIn)
    {
        TPM2_AC_Send_REQUEST inStruct = new TPM2_AC_Send_REQUEST();
        AC_SendResponse outStruct = new AC_SendResponse();
        inStruct.sendObject = sendObject;
        inStruct.authHandle = authHandle;
        inStruct.ac = ac;
        inStruct.acDataIn = acDataIn;
        DispatchCommand(TPM_CC.AC_Send, new TPM_HANDLE[] {sendObject,authHandle,ac}, 2, 0, inStruct, outStruct);
        return outStruct.acDataOut;
    }
    
    /**
     * This command allows qualification of the sending (copying) of an Object to an Attached Component (AC). Qualification includes selection of the receiving AC and the method of authentication for the AC, and, in certain circumstances, the Object to be sent may be specified.
     * 
     * @param policySession handle for the policy session being extended Auth Index: None 
     * @param objectName the Name of the Object to be sent 
     * @param authHandleName the Name associated with authHandle used in the TPM2_AC_Send() command 
     * @param acName the Name of the Attached Component to which the Object will be sent 
     * @param includeObject if SET, objectName will be included in the value in policySessionpolicyDigest
     */
    public void Policy_AC_SendSelect(TPM_HANDLE policySession,byte[] objectName,byte[] authHandleName,byte[] acName,byte includeObject)
    {
        TPM2_Policy_AC_SendSelect_REQUEST inStruct = new TPM2_Policy_AC_SendSelect_REQUEST();
        inStruct.policySession = policySession;
        inStruct.objectName = objectName;
        inStruct.authHandleName = authHandleName;
        inStruct.acName = acName;
        inStruct.includeObject = includeObject;
        DispatchCommand(TPM_CC.Policy_AC_SendSelect, new TPM_HANDLE[] {policySession}, 0, 0, inStruct, null);
        return;
    }
    
    /**
     * This is a placeholder to allow testing of the dispatch code.
     * 
     * @param inputData dummy data 
     * @return dummy data
     */
    public byte[] Vendor_TCG_Test(byte[] inputData)
    {
        TPM2_Vendor_TCG_Test_REQUEST inStruct = new TPM2_Vendor_TCG_Test_REQUEST();
        Vendor_TCG_TestResponse outStruct = new Vendor_TCG_TestResponse();
        inStruct.inputData = inputData;
        DispatchCommand(TPM_CC.Vendor_TCG_Test, new TPM_HANDLE[] {}, 0, 0, inStruct, outStruct);
        return outStruct.outputData;
    }
    
}

//<<<


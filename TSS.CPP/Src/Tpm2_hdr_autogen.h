    /// <summary>
    /// TPM2_Startup() is always preceded by _TPM_Init, which is the physical indication that TPM initialization is necessary because of a system-wide reset. TPM2_Startup() is only valid after _TPM_Init. Additional TPM2_Startup() commands are not allowed after it has completed successfully. If a TPM requires TPM2_Startup() and another command is received, or if the TPM receives TPM2_Startup() when it is not required, the TPM shall return TPM_RC_INITIALIZE.
    /// </summary>
    ///<param name = "startupType">TPM_SU_CLEAR or TPM_SU_STATE</param>
    void Startup
    (
        const TPM_SU& startupType
    );
    /// <summary>
    /// This command is used to prepare the TPM for a power cycle. The shutdownType parameter indicates how the subsequent TPM2_Startup() will be processed.
    /// </summary>
    ///<param name = "shutdownType">TPM_SU_CLEAR or TPM_SU_STATE</param>
    void Shutdown
    (
        const TPM_SU& shutdownType
    );
    /// <summary>
    /// This command causes the TPM to perform a test of its capabilities. If the fullTest is YES, the TPM will test all functions. If fullTest = NO, the TPM will only test those functions that have not previously been tested.
    /// </summary>
    ///<param name = "fullTest">YES if full test to be performed NO if only test of untested functions required</param>
    void SelfTest
    (
        const BYTE& fullTest
    );
    /// <summary>
    /// This command causes the TPM to perform a test of the selected algorithms.
    /// </summary>
    ///<param name = "toTest">list of algorithms that should be tested</param>
    ///<param name = "toDoListCount">number of algorithms in the algorithms list; may be 0</param>
    ///<param name = "toDoList">list of algorithms that need testing</param>
    std::vector<TPM_ALG_ID> IncrementalSelfTest
    (
        const std::vector<TPM_ALG_ID>& toTest
    );
    /// <summary>
    /// This command returns manufacturer-specific information regarding the results of a self-test and an indication of the test status.
    /// </summary>
    ///<param name = "outDataSize">size of the buffer</param>
    ///<param name = "outData">test result data contains manufacturer-specific information</param>
    ///<param name = "testResult"></param>
    GetTestResultResponse GetTestResult();
    /// <summary>
    /// This command is used to start an authorization session using alternative methods of establishing the session key (sessionKey). The session key is then used to derive values used for authorization and for encrypting parameters.
    /// </summary>
    ///<param name = "tpmKey">handle of a loaded decrypt key used to encrypt salt may be TPM_RH_NULL Auth Index: None</param>
    ///<param name = "bind">entity providing the authValue may be TPM_RH_NULL Auth Index: None</param>
    ///<param name = "nonceCaller">initial nonceCaller, sets nonce size for the session shall be at least 16 octets</param>
    ///<param name = "encryptedSalt">value encrypted according to the type of tpmKey If tpmKey is TPM_RH_NULL, this shall be the Empty Buffer.</param>
    ///<param name = "sessionType">indicates the type of the session; simple HMAC or policy (including a trial policy)</param>
    ///<param name = "symmetric">the algorithm and key size for parameter encryption may select TPM_ALG_NULL</param>
    ///<param name = "authHash">hash algorithm to use for the session Shall be a hash algorithm supported by the TPM and not TPM_ALG_NULL</param>
    ///<param name = "sessionHandle">handle for the newly created session</param>
    ///<param name = "nonceTPMSize">size in octets of the buffer field; may be 0</param>
    ///<param name = "nonceTPM">the initial nonce from the TPM, used in the computation of the sessionKey</param>
    StartAuthSessionResponse StartAuthSession
    (
        const TPM_HANDLE& tpmKey,
        const TPM_HANDLE& bind,
        const std::vector<BYTE>& nonceCaller,
        const std::vector<BYTE>& encryptedSalt,
        const TPM_SE& sessionType,
        const TPMT_SYM_DEF& symmetric,
        const TPM_ALG_ID& authHash
    );
    /// <summary>
    /// This command allows a policy authorization session to be returned to its initial state. This command is used after the TPM returns TPM_RC_PCR_CHANGED. That response code indicates that a policy will fail because the PCR have changed after TPM2_PolicyPCR() was executed. Restarting the session allows the authorizations to be replayed because the session restarts with the same nonceTPM. If the PCR are valid for the policy, the policy may then succeed.
    /// </summary>
    ///<param name = "sessionHandle">the handle for the policy session</param>
    void PolicyRestart
    (
        const TPM_HANDLE& sessionHandle
    );
    /// <summary>
    /// This command is used to create an object that can be loaded into a TPM using TPM2_Load(). If the command completes successfully, the TPM will create the new object and return the objects creation data (creationData), its public area (outPublic), and its encrypted sensitive area (outPrivate). Preservation of the returned data is the responsibility of the caller. The object will need to be loaded (TPM2_Load()) before it may be used.
    /// </summary>
    ///<param name = "parentHandle">handle of parent for new object Auth Index: 1 Auth Role: USER</param>
    ///<param name = "inSensitive">the sensitive data</param>
    ///<param name = "inPublic">the public template</param>
    ///<param name = "outsideInfo">data that will be included in the creation data for this object to provide permanent, verifiable linkage between this object and some object owner data</param>
    ///<param name = "creationPCR">PCR that will be used in creation data</param>
    ///<param name = "outPrivate">the private portion of the object</param>
    ///<param name = "outPublicSize">size of publicArea NOTE	The = will force the TPM to try to unmarshal a TPMT_PUBLIC and check that the unmarshaled size matches the value of size. If all the required fields of a TPMT_PUBLIC are not present, the TPM will return an error (generally TPM_RC_SIZE) when attempting to unmarshal the TPMT_PUBLIC.</param>
    ///<param name = "outPublic">the public portion of the created object</param>
    ///<param name = "creationDataSize">size of the creation data</param>
    ///<param name = "creationData">contains a TPMS_CREATION_DATA</param>
    ///<param name = "creationHashSize">size in octets of the buffer field; may be 0</param>
    ///<param name = "creationHash">digest of creationData using nameAlg of outPublic</param>
    ///<param name = "creationTicket">ticket used by TPM2_CertifyCreation() to validate that the creation data was produced by the TPM</param>
    CreateResponse Create
    (
        const TPM_HANDLE& parentHandle,
        const TPMS_SENSITIVE_CREATE& inSensitive,
        const TPMT_PUBLIC& inPublic,
        const std::vector<BYTE>& outsideInfo,
        const std::vector<TPMS_PCR_SELECTION>& creationPCR
    );
    /// <summary>
    /// This command is used to load objects into the TPM. This command is used when both a TPM2B_PUBLIC and TPM2B_PRIVATE are to be loaded. If only a TPM2B_PUBLIC is to be loaded, the TPM2_LoadExternal command is used.
    /// </summary>
    ///<param name = "parentHandle">TPM handle of parent key; shall not be a reserved handle Auth Index: 1 Auth Role: USER</param>
    ///<param name = "inPrivate">the private portion of the object</param>
    ///<param name = "inPublic">the public portion of the object</param>
    ///<param name = "objectHandle">handle of type TPM_HT_TRANSIENT for the loaded object</param>
    ///<param name = "nameSize">size of the Name structure</param>
    ///<param name = "name">Name of the loaded object</param>
    TPM_HANDLE Load
    (
        const TPM_HANDLE& parentHandle,
        const TPM2B_PRIVATE& inPrivate,
        const TPMT_PUBLIC& inPublic
    );
    /// <summary>
    /// This command is used to load an object that is not a Protected Object into the TPM. The command allows loading of a public area or both a public and sensitive area.
    /// </summary>
    ///<param name = "inPrivate">the sensitive portion of the object (optional)</param>
    ///<param name = "inPublic">the public portion of the object</param>
    ///<param name = "hierarchy">hierarchy with which the object area is associated</param>
    ///<param name = "objectHandle">handle of type TPM_HT_TRANSIENT for the loaded object</param>
    ///<param name = "nameSize">size of the Name structure</param>
    ///<param name = "name">name of the loaded object</param>
    TPM_HANDLE LoadExternal
    (
        const TPMT_SENSITIVE& inPrivate,
        const TPMT_PUBLIC& inPublic,
        const TPM_HANDLE& hierarchy
    );
    /// <summary>
    /// This command allows access to the public area of a loaded object.
    /// </summary>
    ///<param name = "objectHandle">TPM handle of an object Auth Index: None</param>
    ///<param name = "outPublicSize">size of publicArea NOTE	The = will force the TPM to try to unmarshal a TPMT_PUBLIC and check that the unmarshaled size matches the value of size. If all the required fields of a TPMT_PUBLIC are not present, the TPM will return an error (generally TPM_RC_SIZE) when attempting to unmarshal the TPMT_PUBLIC.</param>
    ///<param name = "outPublic">structure containing the public area of an object</param>
    ///<param name = "nameSize">size of the Name structure</param>
    ///<param name = "name">name of the object</param>
    ///<param name = "qualifiedNameSize">size of the Name structure</param>
    ///<param name = "qualifiedName">the Qualified Name of the object</param>
    ReadPublicResponse ReadPublic
    (
        const TPM_HANDLE& objectHandle
    );
    /// <summary>
    /// This command enables the association of a credential with an object in a way that ensures that the TPM has validated the parameters of the credentialed object.
    /// </summary>
    ///<param name = "activateHandle">handle of the object associated with certificate in credentialBlob Auth Index: 1 Auth Role: ADMIN</param>
    ///<param name = "keyHandle">loaded key used to decrypt the TPMS_SENSITIVE in credentialBlob Auth Index: 2 Auth Role: USER</param>
    ///<param name = "credentialBlob">the credential</param>
    ///<param name = "secret">keyHandle algorithm-dependent encrypted seed that protects credentialBlob</param>
    ///<param name = "certInfoSize">size in octets of the buffer field; may be 0</param>
    ///<param name = "certInfo">the decrypted certificate information the data should be no larger than the size of the digest of the nameAlg associated with keyHandle</param>
    std::vector<BYTE> ActivateCredential
    (
        const TPM_HANDLE& activateHandle,
        const TPM_HANDLE& keyHandle,
        const std::vector<BYTE>& credentialBlob,
        const std::vector<BYTE>& secret
    );
    /// <summary>
    /// This command allows the TPM to perform the actions required of a Certificate Authority (CA) in creating a TPM2B_ID_OBJECT containing an activation credential.
    /// </summary>
    ///<param name = "handle">loaded public area, used to encrypt the sensitive area containing the credential key Auth Index: None</param>
    ///<param name = "credential">the credential information</param>
    ///<param name = "objectName">Name of the object to which the credential applies</param>
    ///<param name = "credentialBlobSize">size of the credential structure</param>
    ///<param name = "credentialBlob">the credential</param>
    ///<param name = "secretSize">size of the secret value</param>
    ///<param name = "secret">handle algorithm-dependent data that wraps the key that encrypts credentialBlob</param>
    MakeCredentialResponse MakeCredential
    (
        const TPM_HANDLE& handle,
        const std::vector<BYTE>& credential,
        const std::vector<BYTE>& objectName
    );
    /// <summary>
    /// This command returns the data in a loaded Sealed Data Object.
    /// </summary>
    ///<param name = "itemHandle">handle of a loaded data object Auth Index: 1 Auth Role: USER</param>
    ///<param name = "outDataSize"></param>
    ///<param name = "outData">unsealed data Size of outData is limited to be no more than 128 octets.</param>
    std::vector<BYTE> Unseal
    (
        const TPM_HANDLE& itemHandle
    );
    /// <summary>
    /// This command is used to change the authorization secret for a TPM-resident object.
    /// </summary>
    ///<param name = "objectHandle">handle of the object Auth Index: 1 Auth Role: ADMIN</param>
    ///<param name = "parentHandle">handle of the parent Auth Index: None</param>
    ///<param name = "newAuth">new authorization value</param>
    ///<param name = "outPrivate">private area containing the new authorization value</param>
    TPM2B_PRIVATE ObjectChangeAuth
    (
        const TPM_HANDLE& objectHandle,
        const TPM_HANDLE& parentHandle,
        const std::vector<BYTE>& newAuth
    );
    /// <summary>
    /// This command duplicates a loaded object so that it may be used in a different hierarchy. The new parent key for the duplicate may be on the same or different TPM or TPM_RH_NULL. Only the public area of newParentHandle is required to be loaded.
    /// </summary>
    ///<param name = "objectHandle">loaded object to duplicate Auth Index: 1 Auth Role: DUP</param>
    ///<param name = "newParentHandle">shall reference the public area of an asymmetric key Auth Index: None</param>
    ///<param name = "encryptionKeyIn">optional symmetric encryption key The size for this key is set to zero when the TPM is to generate the key. This parameter may be encrypted.</param>
    ///<param name = "symmetricAlg">definition for the symmetric algorithm to be used for the inner wrapper may be TPM_ALG_NULL if no inner wrapper is applied</param>
    ///<param name = "encryptionKeyOutSize">size in octets of the buffer field; may be 0</param>
    ///<param name = "encryptionKeyOut">If the caller provided an encryption key or if symmetricAlg was TPM_ALG_NULL, then this will be the Empty Buffer; otherwise, it shall contain the TPM-generated, symmetric encryption key for the inner wrapper.</param>
    ///<param name = "duplicate">private area that may be encrypted by encryptionKeyIn; and may be doubly encrypted</param>
    ///<param name = "outSymSeedSize">size of the secret value</param>
    ///<param name = "outSymSeed">seed protected by the asymmetric algorithms of new parent (NP)</param>
    DuplicateResponse Duplicate
    (
        const TPM_HANDLE& objectHandle,
        const TPM_HANDLE& newParentHandle,
        const std::vector<BYTE>& encryptionKeyIn,
        const TPMT_SYM_DEF_OBJECT& symmetricAlg
    );
    /// <summary>
    /// This command allows the TPM to serve in the role as a Duplication Authority. If proper authorization for use of the oldParent is provided, then an HMAC key and a symmetric key are recovered from inSymSeed and used to integrity check and decrypt inDuplicate. A new protection seed value is generated according to the methods appropriate for newParent and the blob is re-encrypted and a new integrity value is computed. The re-encrypted blob is returned in outDuplicate and the symmetric key returned in outSymKey.
    /// </summary>
    ///<param name = "oldParent">parent of object Auth Index: 1 Auth Role: User</param>
    ///<param name = "newParent">new parent of the object Auth Index: None</param>
    ///<param name = "inDuplicate">an object encrypted using symmetric key derived from inSymSeed</param>
    ///<param name = "name">the Name of the object being rewrapped</param>
    ///<param name = "inSymSeed">seed for symmetric key needs oldParent private key to recover the seed and generate the symmetric key</param>
    ///<param name = "outDuplicate">an object encrypted using symmetric key derived from outSymSeed</param>
    ///<param name = "outSymSeedSize">size of the secret value</param>
    ///<param name = "outSymSeed">seed for a symmetric key protected by newParent asymmetric key</param>
    RewrapResponse Rewrap
    (
        const TPM_HANDLE& oldParent,
        const TPM_HANDLE& newParent,
        const TPM2B_PRIVATE& inDuplicate,
        const std::vector<BYTE>& name,
        const std::vector<BYTE>& inSymSeed
    );
    /// <summary>
    /// This command allows an object to be encrypted using the symmetric encryption values of a Storage Key. After encryption, the object may be loaded and used in the new hierarchy. The imported object (duplicate) may be singly encrypted, multiply encrypted, or unencrypted.
    /// </summary>
    ///<param name = "parentHandle">the handle of the new parent for the object Auth Index: 1 Auth Role: USER</param>
    ///<param name = "encryptionKey">the optional symmetric encryption key used as the inner wrapper for duplicate If symmetricAlg is TPM_ALG_NULL, then this parameter shall be the Empty Buffer.</param>
    ///<param name = "objectPublic">the public area of the object to be imported This is provided so that the integrity value for duplicate and the object attributes can be checked. NOTE	Even if the integrity value of the object is not checked on input, the object Name is required to create the integrity value for the imported object.</param>
    ///<param name = "duplicate">the symmetrically encrypted duplicate object that may contain an inner symmetric wrapper</param>
    ///<param name = "inSymSeed">symmetric key used to encrypt duplicate inSymSeed is encrypted/encoded using the algorithms of newParent.</param>
    ///<param name = "symmetricAlg">definition for the symmetric algorithm to use for the inner wrapper If this algorithm is TPM_ALG_NULL, no inner wrapper is present and encryptionKey shall be the Empty Buffer.</param>
    ///<param name = "outPrivate">the sensitive area encrypted with the symmetric key of parentHandle</param>
    TPM2B_PRIVATE Import
    (
        const TPM_HANDLE& parentHandle,
        const std::vector<BYTE>& encryptionKey,
        const TPMT_PUBLIC& objectPublic,
        const TPM2B_PRIVATE& duplicate,
        const std::vector<BYTE>& inSymSeed,
        const TPMT_SYM_DEF_OBJECT& symmetricAlg
    );
    /// <summary>
    /// This command performs RSA encryption using the indicated padding scheme according to IETF RFC 3447. If the scheme of keyHandle is TPM_ALG_NULL, then the caller may use inScheme to specify the padding scheme. If scheme of keyHandle is not TPM_ALG_NULL, then inScheme shall either be TPM_ALG_NULL or be the same as scheme (TPM_RC_SCHEME).
    /// </summary>
    ///<param name = "keyHandle">reference to public portion of RSA key to use for encryption Auth Index: None</param>
    ///<param name = "message">message to be encrypted NOTE 1	The data type was chosen because it limits the overall size of the input to no greater than the size of the largest RSA public key. This may be larger than allowed for keyHandle.</param>
    ///<param name = "inScheme">the padding scheme to use if scheme associated with keyHandle is TPM_ALG_NULL(One of TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES, TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME)</param>
    ///<param name = "label">optional label L to be associated with the message Size of the buffer is zero if no label is present NOTE 2	See description of label above.</param>
    ///<param name = "outDataSize">size of the buffer The value of zero is only valid for create.</param>
    ///<param name = "outData">encrypted output</param>
    std::vector<BYTE> RSA_Encrypt
    (
        const TPM_HANDLE& keyHandle,
        const std::vector<BYTE>& message,
        const TPMU_ASYM_SCHEME& inScheme,
        const std::vector<BYTE>& label
    );
    /// <summary>
    /// This command performs RSA decryption using the indicated padding scheme according to IETF RFC 3447 ((PKCS#1).
    /// </summary>
    ///<param name = "keyHandle">RSA key to use for decryption Auth Index: 1 Auth Role: USER</param>
    ///<param name = "cipherText">cipher text to be decrypted NOTE	An encrypted RSA data block is the size of the public modulus.</param>
    ///<param name = "inScheme">the padding scheme to use if scheme associated with keyHandle is TPM_ALG_NULL(One of TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES, TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME)</param>
    ///<param name = "label">label whose association with the message is to be verified</param>
    ///<param name = "messageSize">size of the buffer The value of zero is only valid for create.</param>
    ///<param name = "message">decrypted output</param>
    std::vector<BYTE> RSA_Decrypt
    (
        const TPM_HANDLE& keyHandle,
        const std::vector<BYTE>& cipherText,
        const TPMU_ASYM_SCHEME& inScheme,
        const std::vector<BYTE>& label
    );
    /// <summary>
    /// This command uses the TPM to generate an ephemeral key pair (de, Qe where Qe  [de]G). It uses the private ephemeral key and a loaded public key (QS) to compute the shared secret value (P  [hde]QS).
    /// </summary>
    ///<param name = "keyHandle">Handle of a loaded ECC key public area. Auth Index: None</param>
    ///<param name = "zPointSize">size of the remainder of this structure</param>
    ///<param name = "zPoint">results of P  h[de]Qs</param>
    ///<param name = "pubPointSize">size of the remainder of this structure</param>
    ///<param name = "pubPoint">generated ephemeral public point (Qe)</param>
    ECDH_KeyGenResponse ECDH_KeyGen
    (
        const TPM_HANDLE& keyHandle
    );
    /// <summary>
    /// This command uses the TPM to recover the Z value from a public point (QB) and a private key (ds). It will perform the multiplication of the provided inPoint (QB) with the private key (ds) and return the coordinates of the resultant point (Z = (xZ , yZ)  [hds]QB; where h is the cofactor of the curve).
    /// </summary>
    ///<param name = "keyHandle">handle of a loaded ECC key Auth Index: 1 Auth Role: USER</param>
    ///<param name = "inPoint">a public key</param>
    ///<param name = "outPointSize">size of the remainder of this structure</param>
    ///<param name = "outPoint">X and Y coordinates of the product of the multiplication Z = (xZ , yZ)  [hdS]QB</param>
    TPMS_ECC_POINT ECDH_ZGen
    (
        const TPM_HANDLE& keyHandle,
        const TPMS_ECC_POINT& inPoint
    );
    /// <summary>
    /// This command returns the parameters of an ECC curve identified by its TCG-assigned curveID.
    /// </summary>
    ///<param name = "curveID">parameter set selector</param>
    ///<param name = "parameters">ECC parameters for the selected curve</param>
    TPMS_ALGORITHM_DETAIL_ECC ECC_Parameters
    (
        const TPM_ECC_CURVE& curveID
    );
    /// <summary>
    /// This command supports two-phase key exchange protocols. The command is used in combination with TPM2_EC_Ephemeral(). TPM2_EC_Ephemeral() generates an ephemeral key and returns the public point of that ephemeral key along with a numeric value that allows the TPM to regenerate the associated private key.
    /// </summary>
    ///<param name = "keyA">handle of an unrestricted decryption key ECC The private key referenced by this handle is used as dS,A Auth Index: 1 Auth Role: USER</param>
    ///<param name = "inQsB">other partys static public key (Qs,B = (Xs,B, Ys,B))</param>
    ///<param name = "inQeB">other party's ephemeral public key (Qe,B = (Xe,B, Ye,B))</param>
    ///<param name = "inScheme">the key exchange scheme</param>
    ///<param name = "counter">value returned by TPM2_EC_Ephemeral()</param>
    ///<param name = "outZ1Size">size of the remainder of this structure</param>
    ///<param name = "outZ1">X and Y coordinates of the computed value (scheme dependent)</param>
    ///<param name = "outZ2Size">size of the remainder of this structure</param>
    ///<param name = "outZ2">X and Y coordinates of the second computed value (scheme dependent)</param>
    ZGen_2PhaseResponse ZGen_2Phase
    (
        const TPM_HANDLE& keyA,
        const TPMS_ECC_POINT& inQsB,
        const TPMS_ECC_POINT& inQeB,
        const TPM_ALG_ID& inScheme,
        const UINT16& counter
    );
    /// <summary>
    /// This command performs symmetric encryption or decryption.
    /// </summary>
    ///<param name = "keyHandle">the symmetric key used for the operation Auth Index: 1 Auth Role: USER</param>
    ///<param name = "decrypt">if YES, then the operation is decryption; if NO, the operation is encryption</param>
    ///<param name = "mode">symmetric mode For a restricted key, this field shall match the default mode of the key or be TPM_ALG_NULL.</param>
    ///<param name = "ivIn">an initial value as required by the algorithm</param>
    ///<param name = "inData">the data to be encrypted/decrypted</param>
    ///<param name = "outDataSize">size of the buffer</param>
    ///<param name = "outData">encrypted or decrypted output</param>
    ///<param name = "ivOutSize">size of the timeout value This value is fixed for a TPM implementation.</param>
    ///<param name = "ivOut">chaining value to use for IV in next round</param>
    EncryptDecryptResponse EncryptDecrypt
    (
        const TPM_HANDLE& keyHandle,
        const BYTE& decrypt,
        const TPM_ALG_ID& mode,
        const std::vector<BYTE>& ivIn,
        const std::vector<BYTE>& inData
    );
    /// <summary>
    /// This command performs a hash operation on a data buffer and returns the results.
    /// </summary>
    ///<param name = "data">data to be hashed</param>
    ///<param name = "hashAlg">algorithm for the hash being computed  shall not be TPM_ALG_NULL</param>
    ///<param name = "hierarchy">hierarchy to use for the ticket (TPM_RH_NULL allowed)</param>
    ///<param name = "outHashSize">size in octets of the buffer field; may be 0</param>
    ///<param name = "outHash">results</param>
    ///<param name = "validation">ticket indicating that the sequence of octets used to compute outDigest did not start with TPM_GENERATED_VALUE will be a NULL ticket if the digest may not be signed with a restricted key</param>
    HashResponse Hash
    (
        const std::vector<BYTE>& data,
        const TPM_ALG_ID& hashAlg,
        const TPM_HANDLE& hierarchy
    );
    /// <summary>
    /// This command performs an HMAC on the supplied data using the indicated hash algorithm.
    /// </summary>
    ///<param name = "handle">handle for the symmetric signing key providing the HMAC key Auth Index: 1 Auth Role: USER</param>
    ///<param name = "buffer">HMAC data</param>
    ///<param name = "hashAlg">algorithm to use for HMAC</param>
    ///<param name = "outHMACSize">size in octets of the buffer field; may be 0</param>
    ///<param name = "outHMAC">the returned HMAC in a sized buffer</param>
    std::vector<BYTE> HMAC
    (
        const TPM_HANDLE& handle,
        const std::vector<BYTE>& buffer,
        const TPM_ALG_ID& hashAlg
    );
    /// <summary>
    /// This command returns the next bytesRequested octets from the random number generator (RNG).
    /// </summary>
    ///<param name = "bytesRequested">number of octets to return</param>
    ///<param name = "randomBytesSize">size in octets of the buffer field; may be 0</param>
    ///<param name = "randomBytes">the random octets</param>
    std::vector<BYTE> GetRandom
    (
        const UINT16& bytesRequested
    );
    /// <summary>
    /// This command is used to add "additional information" to the RNG state.
    /// </summary>
    ///<param name = "inData">additional information</param>
    void StirRandom
    (
        const std::vector<BYTE>& inData
    );
    /// <summary>
    /// This command starts an HMAC sequence. The TPM will create and initialize an HMAC sequence structure, assign a handle to the sequence, and set the authValue of the sequence object to the value in auth.
    /// </summary>
    ///<param name = "handle">handle of an HMAC key Auth Index: 1 Auth Role: USER</param>
    ///<param name = "auth">authorization value for subsequent use of the sequence</param>
    ///<param name = "hashAlg">the hash algorithm to use for the HMAC</param>
    ///<param name = "sequenceHandle">a handle to reference the sequence</param>
    TPM_HANDLE HMAC_Start
    (
        const TPM_HANDLE& handle,
        const std::vector<BYTE>& auth,
        const TPM_ALG_ID& hashAlg
    );
    /// <summary>
    /// This command starts a hash or an Event Sequence. If hashAlg is an implemented hash, then a hash sequence is started. If hashAlg is TPM_ALG_NULL, then an Event Sequence is started. If hashAlg is neither an implemented algorithm nor TPM_ALG_NULL, then the TPM shall return TPM_RC_HASH.
    /// </summary>
    ///<param name = "auth">authorization value for subsequent use of the sequence</param>
    ///<param name = "hashAlg">the hash algorithm to use for the hash sequence An Event Sequence starts if this is TPM_ALG_NULL.</param>
    ///<param name = "sequenceHandle">a handle to reference the sequence</param>
    TPM_HANDLE HashSequenceStart
    (
        const std::vector<BYTE>& auth,
        const TPM_ALG_ID& hashAlg
    );
    /// <summary>
    /// This command is used to add data to a hash or HMAC sequence. The amount of data in buffer may be any size up to the limits of the TPM.
    /// </summary>
    ///<param name = "sequenceHandle">handle for the sequence object Auth Index: 1 Auth Role: USER</param>
    ///<param name = "buffer">data to be added to hash</param>
    void SequenceUpdate
    (
        const TPM_HANDLE& sequenceHandle,
        const std::vector<BYTE>& buffer
    );
    /// <summary>
    /// This command adds the last part of data, if any, to a hash/HMAC sequence and returns the result.
    /// </summary>
    ///<param name = "sequenceHandle">authorization for the sequence Auth Index: 1 Auth Role: USER</param>
    ///<param name = "buffer">data to be added to the hash/HMAC</param>
    ///<param name = "hierarchy">hierarchy of the ticket for a hash</param>
    ///<param name = "resultSize">size in octets of the buffer field; may be 0</param>
    ///<param name = "result">the returned HMAC or digest in a sized buffer</param>
    ///<param name = "validation">ticket indicating that the sequence of octets used to compute outDigest did not start with TPM_GENERATED_VALUE This is a NULL Ticket when the sequence is HMAC.</param>
    SequenceCompleteResponse SequenceComplete
    (
        const TPM_HANDLE& sequenceHandle,
        const std::vector<BYTE>& buffer,
        const TPM_HANDLE& hierarchy
    );
    /// <summary>
    /// This command adds the last part of data, if any, to an Event Sequence and returns the result in a digest list. If pcrHandle references a PCR and not TPM_RH_NULL, then the returned digest list is processed in the same manner as the digest list input parameter to TPM2_PCR_Extend() with the pcrHandle in each bank extended with the associated digest value.
    /// </summary>
    ///<param name = "pcrHandle">PCR to be extended with the Event data Auth Index: 1 Auth Role: USER</param>
    ///<param name = "sequenceHandle">authorization for the sequence Auth Index: 2 Auth Role: USER</param>
    ///<param name = "buffer">data to be added to the Event</param>
    ///<param name = "resultsCount">number of digests in the list</param>
    ///<param name = "results">list of digests computed for the PCR</param>
    std::vector<TPMT_HA> EventSequenceComplete
    (
        const TPM_HANDLE& pcrHandle,
        const TPM_HANDLE& sequenceHandle,
        const std::vector<BYTE>& buffer
    );
    /// <summary>
    /// The purpose of this command is to prove that an object with a specific Name is loaded in the TPM. By certifying that the object is loaded, the TPM warrants that a public area with a given Name is self-consistent and associated with a valid sensitive area. If a relying party has a public area that has the same Name as a Name certified with this command, then the values in that public area are correct.
    /// </summary>
    ///<param name = "objectHandle">handle of the object to be certified Auth Index: 1 Auth Role: ADMIN</param>
    ///<param name = "signHandle">handle of the key used to sign the attestation structure Auth Index: 2 Auth Role: USER</param>
    ///<param name = "qualifyingData">user provided qualifying data</param>
    ///<param name = "inScheme">signing scheme to use if the scheme for signHandle is TPM_ALG_NULL(One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)</param>
    ///<param name = "certifyInfoSize">size of the attestationData structure</param>
    ///<param name = "certifyInfo">the structure that was signed</param>
    ///<param name = "signatureSigAlg">selector of the algorithm used to construct the signature</param>
    ///<param name = "signature">the asymmetric signature over certifyInfo using the key referenced by signHandle(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
    CertifyResponse Certify
    (
        const TPM_HANDLE& objectHandle,
        const TPM_HANDLE& signHandle,
        const std::vector<BYTE>& qualifyingData,
        const TPMU_SIG_SCHEME& inScheme
    );
    /// <summary>
    /// This command is used to prove the association between an object and its creation data. The TPM will validate that the ticket was produced by the TPM and that the ticket validates the association between a loaded public area and the provided hash of the creation data (creationHash).
    /// </summary>
    ///<param name = "signHandle">handle of the key that will sign the attestation block Auth Index: 1 Auth Role: USER</param>
    ///<param name = "objectHandle">the object associated with the creation data Auth Index: None</param>
    ///<param name = "qualifyingData">user-provided qualifying data</param>
    ///<param name = "creationHash">hash of the creation data produced by TPM2_Create() or TPM2_CreatePrimary()</param>
    ///<param name = "inScheme">signing scheme to use if the scheme for signHandle is TPM_ALG_NULL(One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)</param>
    ///<param name = "creationTicket">ticket produced by TPM2_Create() or TPM2_CreatePrimary()</param>
    ///<param name = "certifyInfoSize">size of the attestationData structure</param>
    ///<param name = "certifyInfo">the structure that was signed</param>
    ///<param name = "signatureSigAlg">selector of the algorithm used to construct the signature</param>
    ///<param name = "signature">the signature over certifyInfo(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
    CertifyCreationResponse CertifyCreation
    (
        const TPM_HANDLE& signHandle,
        const TPM_HANDLE& objectHandle,
        const std::vector<BYTE>& qualifyingData,
        const std::vector<BYTE>& creationHash,
        const TPMU_SIG_SCHEME& inScheme,
        const TPMT_TK_CREATION& creationTicket
    );
    /// <summary>
    /// This command is used to quote PCR values.
    /// </summary>
    ///<param name = "signHandle">handle of key that will perform signature Auth Index: 1 Auth Role: USER</param>
    ///<param name = "qualifyingData">data supplied by the caller</param>
    ///<param name = "inScheme">signing scheme to use if the scheme for signHandle is TPM_ALG_NULL(One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)</param>
    ///<param name = "PCRselect">PCR set to quote</param>
    ///<param name = "quotedSize">size of the attestationData structure</param>
    ///<param name = "quoted">the quoted information</param>
    ///<param name = "signatureSigAlg">selector of the algorithm used to construct the signature</param>
    ///<param name = "signature">the signature over quoted(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
    QuoteResponse Quote
    (
        const TPM_HANDLE& signHandle,
        const std::vector<BYTE>& qualifyingData,
        const TPMU_SIG_SCHEME& inScheme,
        const std::vector<TPMS_PCR_SELECTION>& PCRselect
    );
    /// <summary>
    /// This command returns a digital signature of the audit session digest.
    /// </summary>
    ///<param name = "privacyAdminHandle">handle of the privacy administrator (TPM_RH_ENDORSEMENT) Auth Index: 1 Auth Role: USER</param>
    ///<param name = "signHandle">handle of the signing key Auth Index: 2 Auth Role: USER</param>
    ///<param name = "sessionHandle">handle of the audit session Auth Index: None</param>
    ///<param name = "qualifyingData">user-provided qualifying data  may be zero-length</param>
    ///<param name = "inScheme">signing scheme to use if the scheme for signHandle is TPM_ALG_NULL(One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)</param>
    ///<param name = "auditInfoSize">size of the attestationData structure</param>
    ///<param name = "auditInfo">the audit information that was signed</param>
    ///<param name = "signatureSigAlg">selector of the algorithm used to construct the signature</param>
    ///<param name = "signature">the signature over auditInfo(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
    GetSessionAuditDigestResponse GetSessionAuditDigest
    (
        const TPM_HANDLE& privacyAdminHandle,
        const TPM_HANDLE& signHandle,
        const TPM_HANDLE& sessionHandle,
        const std::vector<BYTE>& qualifyingData,
        const TPMU_SIG_SCHEME& inScheme
    );
    /// <summary>
    /// This command returns the current value of the command audit digest, a digest of the commands being audited, and the audit hash algorithm. These values are placed in an attestation structure and signed with the key referenced by signHandle.
    /// </summary>
    ///<param name = "privacyHandle">handle of the privacy administrator (TPM_RH_ENDORSEMENT) Auth Index: 1 Auth Role: USER</param>
    ///<param name = "signHandle">the handle of the signing key Auth Index: 2 Auth Role: USER</param>
    ///<param name = "qualifyingData">other data to associate with this audit digest</param>
    ///<param name = "inScheme">signing scheme to use if the scheme for signHandle is TPM_ALG_NULL(One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)</param>
    ///<param name = "auditInfoSize">size of the attestationData structure</param>
    ///<param name = "auditInfo">the auditInfo that was signed</param>
    ///<param name = "signatureSigAlg">selector of the algorithm used to construct the signature</param>
    ///<param name = "signature">the signature over auditInfo(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
    GetCommandAuditDigestResponse GetCommandAuditDigest
    (
        const TPM_HANDLE& privacyHandle,
        const TPM_HANDLE& signHandle,
        const std::vector<BYTE>& qualifyingData,
        const TPMU_SIG_SCHEME& inScheme
    );
    /// <summary>
    /// This command returns the current values of Time and Clock.
    /// </summary>
    ///<param name = "privacyAdminHandle">handle of the privacy administrator (TPM_RH_ENDORSEMENT) Auth Index: 1 Auth Role: USER</param>
    ///<param name = "signHandle">the keyHandle identifier of a loaded key that can perform digital signatures Auth Index: 2 Auth Role: USER</param>
    ///<param name = "qualifyingData">data to tick stamp</param>
    ///<param name = "inScheme">signing scheme to use if the scheme for signHandle is TPM_ALG_NULL(One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)</param>
    ///<param name = "timeInfoSize">size of the attestationData structure</param>
    ///<param name = "timeInfo">standard TPM-generated attestation block</param>
    ///<param name = "signatureSigAlg">selector of the algorithm used to construct the signature</param>
    ///<param name = "signature">the signature over timeInfo(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
    GetTimeResponse GetTime
    (
        const TPM_HANDLE& privacyAdminHandle,
        const TPM_HANDLE& signHandle,
        const std::vector<BYTE>& qualifyingData,
        const TPMU_SIG_SCHEME& inScheme
    );
    /// <summary>
    /// TPM2_Commit() performs the first part of an ECC anonymous signing operation. The TPM will perform the point multiplications on the provided points and return intermediate signing values. The signHandle parameter shall refer to an ECC key with the sign attribute (TPM_RC_ATTRIBUTES) and the signing scheme must be anonymous (TPM_RC_SCHEME). Currently, TPM_ALG_ECDAA is the only defined anonymous scheme.
    /// </summary>
    ///<param name = "signHandle">handle of the key that will be used in the signing operation Auth Index: 1 Auth Role: USER</param>
    ///<param name = "P1">a point (M) on the curve used by signHandle</param>
    ///<param name = "s2">octet array used to derive x-coordinate of a base point</param>
    ///<param name = "y2">y coordinate of the point associated with s2</param>
    ///<param name = "KSize">size of the remainder of this structure</param>
    ///<param name = "K">ECC point K  [ds](x2, y2)</param>
    ///<param name = "LSize">size of the remainder of this structure</param>
    ///<param name = "L">ECC point L  [r](x2, y2)</param>
    ///<param name = "ESize">size of the remainder of this structure</param>
    ///<param name = "E">ECC point E  [r]P1</param>
    ///<param name = "counter">least-significant 16 bits of commitCount</param>
    CommitResponse Commit
    (
        const TPM_HANDLE& signHandle,
        const TPMS_ECC_POINT& P1,
        const std::vector<BYTE>& s2,
        const std::vector<BYTE>& y2
    );
    /// <summary>
    /// TPM2_EC_Ephemeral() creates an ephemeral key for use in a two-phase key exchange protocol.
    /// </summary>
    ///<param name = "curveID">The curve for the computed ephemeral point</param>
    ///<param name = "QSize">size of the remainder of this structure</param>
    ///<param name = "Q">ephemeral public key Q  [r]G</param>
    ///<param name = "counter">least-significant 16 bits of commitCount</param>
    EC_EphemeralResponse EC_Ephemeral
    (
        const TPM_ECC_CURVE& curveID
    );
    /// <summary>
    /// This command uses loaded keys to validate a signature on a message with the message digest passed to the TPM.
    /// </summary>
    ///<param name = "keyHandle">handle of public key that will be used in the validation Auth Index: None</param>
    ///<param name = "digest">digest of the signed message</param>
    ///<param name = "signature">signature to be tested(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
    ///<param name = "validation"></param>
    VerifySignatureResponse VerifySignature
    (
        const TPM_HANDLE& keyHandle,
        const std::vector<BYTE>& digest,
        const TPMU_SIGNATURE& signature
    );
    /// <summary>
    /// This command causes the TPM to sign an externally provided hash with the specified symmetric or asymmetric signing key.
    /// </summary>
    ///<param name = "keyHandle">Handle of key that will perform signing Auth Index: 1 Auth Role: USER</param>
    ///<param name = "digest">digest to be signed</param>
    ///<param name = "inScheme">signing scheme to use if the scheme for keyHandle is TPM_ALG_NULL(One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)</param>
    ///<param name = "validation">proof that digest was created by the TPM If keyHandle is not a restricted signing key, then this may be a NULL Ticket with tag = TPM_ST_CHECKHASH.</param>
    ///<param name = "signatureSigAlg">selector of the algorithm used to construct the signature</param>
    ///<param name = "signature">the signature(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
    SignResponse Sign
    (
        const TPM_HANDLE& keyHandle,
        const std::vector<BYTE>& digest,
        const TPMU_SIG_SCHEME& inScheme,
        const TPMT_TK_HASHCHECK& validation
    );
    /// <summary>
    /// This command may be used by the Privacy Administrator or platform to change the audit status of a command or to set the hash algorithm used for the audit digest, but not both at the same time.
    /// </summary>
    ///<param name = "auth">TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
    ///<param name = "auditAlg">hash algorithm for the audit digest; if TPM_ALG_NULL, then the hash is not changed</param>
    ///<param name = "setList">list of commands that will be added to those that will be audited</param>
    ///<param name = "clearList">list of commands that will no longer be audited</param>
    void SetCommandCodeAuditStatus
    (
        const TPM_HANDLE& auth,
        const TPM_ALG_ID& auditAlg,
        const std::vector<TPM_CC>& setList,
        const std::vector<TPM_CC>& clearList
    );
    /// <summary>
    /// This command is used to cause an update to the indicated PCR. The digests parameter contains one or more tagged digest values identified by an algorithm ID. For each digest, the PCR associated with pcrHandle is Extended into the bank identified by the tag (hashAlg).
    /// </summary>
    ///<param name = "pcrHandle">handle of the PCR Auth Handle: 1 Auth Role: USER</param>
    ///<param name = "digests">list of tagged digest values to be extended</param>
    void PCR_Extend
    (
        const TPM_HANDLE& pcrHandle,
        const std::vector<TPMT_HA>& digests
    );
    /// <summary>
    /// This command is used to cause an update to the indicated PCR.
    /// </summary>
    ///<param name = "pcrHandle">Handle of the PCR Auth Handle: 1 Auth Role: USER</param>
    ///<param name = "eventData">Event data in sized buffer</param>
    ///<param name = "digestsCount">number of digests in the list</param>
    ///<param name = "digests"></param>
    std::vector<TPMT_HA> PCR_Event
    (
        const TPM_HANDLE& pcrHandle,
        const std::vector<BYTE>& eventData
    );
    /// <summary>
    /// This command returns the values of all PCR specified in pcrSelectionIn.
    /// </summary>
    ///<param name = "pcrSelectionIn">The selection of PCR to read</param>
    ///<param name = "pcrUpdateCounter">the current value of the PCR update counter</param>
    ///<param name = "pcrSelectionOutCount">number of selection structures A value of zero is allowed.</param>
    ///<param name = "pcrSelectionOut">the PCR in the returned list</param>
    ///<param name = "pcrValuesCount">number of digests in the list, minimum is two for TPM2_PolicyOR().</param>
    ///<param name = "pcrValues">the contents of the PCR indicated in pcrSelect as tagged digests</param>
    PCR_ReadResponse PCR_Read
    (
        const std::vector<TPMS_PCR_SELECTION>& pcrSelectionIn
    );
    /// <summary>
    /// This command is used to set the desired PCR allocation of PCR and algorithms. This command requires Platform Authorization.
    /// </summary>
    ///<param name = "authHandle">TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
    ///<param name = "pcrAllocation">the requested allocation</param>
    ///<param name = "allocationSuccess">YES if the allocation succeeded</param>
    ///<param name = "maxPCR">maximum number of PCR that may be in a bank</param>
    ///<param name = "sizeNeeded">number of octets required to satisfy the request</param>
    ///<param name = "sizeAvailable">Number of octets available. Computed before the allocation.</param>
    PCR_AllocateResponse PCR_Allocate
    (
        const TPM_HANDLE& authHandle,
        const std::vector<TPMS_PCR_SELECTION>& pcrAllocation
    );
    /// <summary>
    /// This command is used to associate a policy with a PCR or group of PCR. The policy determines the conditions under which a PCR may be extended or reset.
    /// </summary>
    ///<param name = "authHandle">TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
    ///<param name = "authPolicy">the desired authPolicy</param>
    ///<param name = "hashAlg">the hash algorithm of the policy</param>
    ///<param name = "pcrNum">the PCR for which the policy is to be set</param>
    void PCR_SetAuthPolicy
    (
        const TPM_HANDLE& authHandle,
        const std::vector<BYTE>& authPolicy,
        const TPM_ALG_ID& hashAlg,
        const TPM_HANDLE& pcrNum
    );
    /// <summary>
    /// This command changes the authValue of a PCR or group of PCR.
    /// </summary>
    ///<param name = "pcrHandle">handle for a PCR that may have an authorization value set Auth Index: 1 Auth Role: USER</param>
    ///<param name = "auth">the desired authorization value</param>
    void PCR_SetAuthValue
    (
        const TPM_HANDLE& pcrHandle,
        const std::vector<BYTE>& auth
    );
    /// <summary>
    /// If the attribute of a PCR allows the PCR to be reset and proper authorization is provided, then this command may be used to set the PCR to zero. The attributes of the PCR may restrict the locality that can perform the reset operation.
    /// </summary>
    ///<param name = "pcrHandle">the PCR to reset Auth Index: 1 Auth Role: USER</param>
    void PCR_Reset
    (
        const TPM_HANDLE& pcrHandle
    );
    /// <summary>
    /// This command includes a signed authorization in a policy. The command ties the policy to a signing key by including the Name of the signing key in the policyDigest
    /// </summary>
    ///<param name = "authObject">handle for a key that will validate the signature Auth Index: None</param>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    ///<param name = "nonceTPM">the policy nonce for the session This can be the Empty Buffer.</param>
    ///<param name = "cpHashA">digest of the command parameters to which this authorization is limited This is not the cpHash for this command but the cpHash for the command to which this policy session will be applied. If it is not limited, the parameter will be the Empty Buffer.</param>
    ///<param name = "policyRef">a reference to a policy relating to the authorization  may be the Empty Buffer Size is limited to be no larger than the nonce size supported on the TPM.</param>
    ///<param name = "expiration">time when authorization will expire, measured in seconds from the time that nonceTPM was generated If expiration is non-negative, a NULL Ticket is returned. See 23.2.5.</param>
    ///<param name = "auth">signed authorization (not optional)(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
    ///<param name = "timeoutSize">size of the timeout value This value is fixed for a TPM implementation.</param>
    ///<param name = "timeout">implementation-specific time value, used to indicate to the TPM when the ticket expires NOTE	If policyTicket is a NULL Ticket, then this shall be the Empty Buffer.</param>
    ///<param name = "policyTicket">produced if the command succeeds and expiration in the command was non-zero; this ticket will use the TPMT_ST_AUTH_SIGNED structure tag. See 23.2.5</param>
    PolicySignedResponse PolicySigned
    (
        const TPM_HANDLE& authObject,
        const TPM_HANDLE& policySession,
        const std::vector<BYTE>& nonceTPM,
        const std::vector<BYTE>& cpHashA,
        const std::vector<BYTE>& policyRef,
        const INT32& expiration,
        const TPMU_SIGNATURE& auth
    );
    /// <summary>
    /// This command includes a secret-based authorization to a policy. The caller proves knowledge of the secret value using an authorization session using the authValue associated with authHandle. A password session, an HMAC session, or a policy session containing TPM2_PolicyAuthValue() or TPM2_PolicyPassword() will satisfy this requirement.
    /// </summary>
    ///<param name = "authHandle">handle for an entity providing the authorization Auth Index: 1 Auth Role: USER</param>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    ///<param name = "nonceTPM">the policy nonce for the session This can be the Empty Buffer.</param>
    ///<param name = "cpHashA">digest of the command parameters to which this authorization is limited This not the cpHash for this command but the cpHash for the command to which this policy session will be applied. If it is not limited, the parameter will be the Empty Buffer.</param>
    ///<param name = "policyRef">a reference to a policy relating to the authorization  may be the Empty Buffer Size is limited to be no larger than the nonce size supported on the TPM.</param>
    ///<param name = "expiration">time when authorization will expire, measured in seconds from the time that nonceTPM was generated If expiration is non-negative, a NULL Ticket is returned. See 23.2.5.</param>
    ///<param name = "timeoutSize">size of the timeout value This value is fixed for a TPM implementation.</param>
    ///<param name = "timeout">implementation-specific time value used to indicate to the TPM when the ticket expires; this ticket will use the TPMT_ST_AUTH_SECRET structure tag</param>
    ///<param name = "policyTicket">produced if the command succeeds and expiration in the command was non-zero. See 23.2.5</param>
    PolicySecretResponse PolicySecret
    (
        const TPM_HANDLE& authHandle,
        const TPM_HANDLE& policySession,
        const std::vector<BYTE>& nonceTPM,
        const std::vector<BYTE>& cpHashA,
        const std::vector<BYTE>& policyRef,
        const INT32& expiration
    );
    /// <summary>
    /// This command is similar to TPM2_PolicySigned() except that it takes a ticket instead of a signed authorization. The ticket represents a validated authorization that had an expiration time associated with it.
    /// </summary>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    ///<param name = "timeout">time when authorization will expire The contents are TPM specific. This shall be the value returned when ticket was produced.</param>
    ///<param name = "cpHashA">digest of the command parameters to which this authorization is limited If it is not limited, the parameter will be the Empty Buffer.</param>
    ///<param name = "policyRef">reference to a qualifier for the policy  may be the Empty Buffer</param>
    ///<param name = "authName">name of the object that provided the authorization</param>
    ///<param name = "ticket">an authorization ticket returned by the TPM in response to a TPM2_PolicySigned() or TPM2_PolicySecret()</param>
    void PolicyTicket
    (
        const TPM_HANDLE& policySession,
        const std::vector<BYTE>& timeout,
        const std::vector<BYTE>& cpHashA,
        const std::vector<BYTE>& policyRef,
        const std::vector<BYTE>& authName,
        const TPMT_TK_AUTH& ticket
    );
    /// <summary>
    /// This command allows options in authorizations without requiring that the TPM evaluate all of the options. If a policy may be satisfied by different sets of conditions, the TPM need only evaluate one set that satisfies the policy. This command will indicate that one of the required sets of conditions has been satisfied.
    /// </summary>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    ///<param name = "pHashList">the list of hashes to check for a match</param>
    void PolicyOR
    (
        const TPM_HANDLE& policySession,
        const std::vector<TPM2B_DIGEST>& pHashList
    );
    /// <summary>
    /// This command is used to cause conditional gating of a policy based on PCR. This command together with TPM2_PolicyOR() allows one group of authorizations to occur when PCR are in one state and a different set of authorizations when the PCR are in a different state. If this command is used for a trial policySession, policySessionpolicyDigest will be updated using the values from the command rather than the values from digest of the TPM PCR.
    /// </summary>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    ///<param name = "pcrDigest">expected digest value of the selected PCR using the hash algorithm of the session; may be zero length</param>
    ///<param name = "pcrs">the PCR to include in the check digest</param>
    void PolicyPCR
    (
        const TPM_HANDLE& policySession,
        const std::vector<BYTE>& pcrDigest,
        const std::vector<TPMS_PCR_SELECTION>& pcrs
    );
    /// <summary>
    /// This command indicates that the authorization will be limited to a specific locality.
    /// </summary>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    ///<param name = "locality">the allowed localities for the policy</param>
    void PolicyLocality
    (
        const TPM_HANDLE& policySession,
        const TPMA_LOCALITY& locality
    );
    /// <summary>
    /// This command is used to cause conditional gating of a policy based on the contents of an NV Index.
    /// </summary>
    ///<param name = "authHandle">handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER</param>
    ///<param name = "nvIndex">the NV Index of the area to read Auth Index: None</param>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    ///<param name = "operandB">the second operand</param>
    ///<param name = "offset">the offset in the NV Index for the start of operand A</param>
    ///<param name = "operation">the comparison to make</param>
    void PolicyNV
    (
        const TPM_HANDLE& authHandle,
        const TPM_HANDLE& nvIndex,
        const TPM_HANDLE& policySession,
        const std::vector<BYTE>& operandB,
        const UINT16& offset,
        const TPM_EO& operation
    );
    /// <summary>
    /// This command is used to cause conditional gating of a policy based on the contents of the TPMS_TIME_INFO structure.
    /// </summary>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    ///<param name = "operandB">the second operand</param>
    ///<param name = "offset">the offset in TPMS_TIME_INFO structure for the start of operand A</param>
    ///<param name = "operation">the comparison to make</param>
    void PolicyCounterTimer
    (
        const TPM_HANDLE& policySession,
        const std::vector<BYTE>& operandB,
        const UINT16& offset,
        const TPM_EO& operation
    );
    /// <summary>
    /// This command indicates that the authorization will be limited to a specific command code.
    /// </summary>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    ///<param name = "code">the allowed commandCode</param>
    void PolicyCommandCode
    (
        const TPM_HANDLE& policySession,
        const TPM_CC& code
    );
    /// <summary>
    /// This command indicates that physical presence will need to be asserted at the time the authorization is performed.
    /// </summary>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    void PolicyPhysicalPresence
    (
        const TPM_HANDLE& policySession
    );
    /// <summary>
    /// This command is used to allow a policy to be bound to a specific command and command parameters.
    /// </summary>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    ///<param name = "cpHashA">the cpHash added to the policy</param>
    void PolicyCpHash
    (
        const TPM_HANDLE& policySession,
        const std::vector<BYTE>& cpHashA
    );
    /// <summary>
    /// This command allows a policy to be bound to a specific set of TPM entities without being bound to the parameters of the command. This is most useful for commands such as TPM2_Duplicate() and for TPM2_PCR_Event() when the referenced PCR requires a policy.
    /// </summary>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    ///<param name = "nameHash">the digest to be added to the policy</param>
    void PolicyNameHash
    (
        const TPM_HANDLE& policySession,
        const std::vector<BYTE>& nameHash
    );
    /// <summary>
    /// This command allows qualification of duplication to allow duplication to a selected new parent.
    /// </summary>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    ///<param name = "objectName">the Name of the object to be duplicated</param>
    ///<param name = "newParentName">the Name of the new parent</param>
    ///<param name = "includeObject">if YES, the objectName will be included in the value in policySessionpolicyDigest</param>
    void PolicyDuplicationSelect
    (
        const TPM_HANDLE& policySession,
        const std::vector<BYTE>& objectName,
        const std::vector<BYTE>& newParentName,
        const BYTE& includeObject
    );
    /// <summary>
    /// This command allows policies to change. If a policy were static, then it would be difficult to add users to a policy. This command lets a policy authority sign a new policy so that it may be used in an existing policy.
    /// </summary>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    ///<param name = "approvedPolicy">digest of the policy being approved</param>
    ///<param name = "policyRef">a policy qualifier</param>
    ///<param name = "keySign">Name of a key that can sign a policy addition</param>
    ///<param name = "checkTicket">ticket validating that approvedPolicy and policyRef were signed by keySign</param>
    void PolicyAuthorize
    (
        const TPM_HANDLE& policySession,
        const std::vector<BYTE>& approvedPolicy,
        const std::vector<BYTE>& policyRef,
        const std::vector<BYTE>& keySign,
        const TPMT_TK_VERIFIED& checkTicket
    );
    /// <summary>
    /// This command allows a policy to be bound to the authorization value of the authorized entity.
    /// </summary>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    void PolicyAuthValue
    (
        const TPM_HANDLE& policySession
    );
    /// <summary>
    /// This command allows a policy to be bound to the authorization value of the authorized object.
    /// </summary>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    void PolicyPassword
    (
        const TPM_HANDLE& policySession
    );
    /// <summary>
    /// This command returns the current policyDigest of the session. This command allows the TPM to be used to perform the actions required to pre-compute the authPolicy for an object.
    /// </summary>
    ///<param name = "policySession">handle for the policy session Auth Index: None</param>
    ///<param name = "policyDigestSize">size in octets of the buffer field; may be 0</param>
    ///<param name = "policyDigest">the current value of the policySessionpolicyDigest</param>
    std::vector<BYTE> PolicyGetDigest
    (
        const TPM_HANDLE& policySession
    );
    /// <summary>
    /// This command allows a policy to be bound to the TPMA_NV_WRITTEN attributes. This is a deferred assertion. Values are stored in the policy session context and checked when the policy is used for authorization.
    /// </summary>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    ///<param name = "writtenSet">YES if NV Index is required to have been written NO if NV Index is required not to have been written</param>
    void PolicyNvWritten
    (
        const TPM_HANDLE& policySession,
        const BYTE& writtenSet
    );
    /// <summary>
    /// This command allows creation of an authorization policy that will only allow creation of a child object with the correct properties.
    /// </summary>
    ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
    ///<param name = "templateHash">the hash of the template to be added to the policy</param>
    void PolicyTemplate
    (
        const TPM_HANDLE& policySession,
        const std::vector<BYTE>& templateHash
    );
    /// <summary>
    /// This command is used to create a Primary Object under one of the Primary Seeds or a Temporary Object under TPM_RH_NULL. The command uses a TPM2B_PUBLIC as a template for the object to be created. The command will create and load a Primary Object. The sensitive area is not returned.
    /// </summary>
    ///<param name = "primaryHandle">TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM+{PP}, or TPM_RH_NULL Auth Index: 1 Auth Role: USER</param>
    ///<param name = "inSensitive">the sensitive data, see TPM 2.0 Part 1 Sensitive Values</param>
    ///<param name = "inPublic">the public template</param>
    ///<param name = "outsideInfo">data that will be included in the creation data for this object to provide permanent, verifiable linkage between this object and some object owner data</param>
    ///<param name = "creationPCR">PCR that will be used in creation data</param>
    ///<param name = "objectHandle">handle of type TPM_HT_TRANSIENT for created Primary Object</param>
    ///<param name = "outPublicSize">size of publicArea NOTE	The = will force the TPM to try to unmarshal a TPMT_PUBLIC and check that the unmarshaled size matches the value of size. If all the required fields of a TPMT_PUBLIC are not present, the TPM will return an error (generally TPM_RC_SIZE) when attempting to unmarshal the TPMT_PUBLIC.</param>
    ///<param name = "outPublic">the public portion of the created object</param>
    ///<param name = "creationDataSize">size of the creation data</param>
    ///<param name = "creationData">contains a TPMT_CREATION_DATA</param>
    ///<param name = "creationHashSize">size in octets of the buffer field; may be 0</param>
    ///<param name = "creationHash">digest of creationData using nameAlg of outPublic</param>
    ///<param name = "creationTicket">ticket used by TPM2_CertifyCreation() to validate that the creation data was produced by the TPM</param>
    ///<param name = "nameSize">size of the Name structure</param>
    ///<param name = "name">the name of the created object</param>
    CreatePrimaryResponse CreatePrimary
    (
        const TPM_HANDLE& primaryHandle,
        const TPMS_SENSITIVE_CREATE& inSensitive,
        const TPMT_PUBLIC& inPublic,
        const std::vector<BYTE>& outsideInfo,
        const std::vector<TPMS_PCR_SELECTION>& creationPCR
    );
    /// <summary>
    /// This command enables and disables use of a hierarchy and its associated NV storage. The command allows phEnable, phEnableNV, shEnable, and ehEnable to be changed when the proper authorization is provided.
    /// </summary>
    ///<param name = "authHandle">TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
    ///<param name = "enable">the enable being modified TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM, or TPM_RH_PLATFORM_NV</param>
    ///<param name = "state">YES if the enable should be SET, NO if the enable should be CLEAR</param>
    void HierarchyControl
    (
        const TPM_HANDLE& authHandle,
        const TPM_HANDLE& enable,
        const BYTE& state
    );
    /// <summary>
    /// This command allows setting of the authorization policy for the lockout (lockoutPolicy), the platform hierarchy (platformPolicy), the storage hierarchy (ownerPolicy), and the endorsement hierarchy (endorsementPolicy).
    /// </summary>
    ///<param name = "authHandle">TPM_RH_LOCKOUT, TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
    ///<param name = "authPolicy">an authorization policy digest; may be the Empty Buffer If hashAlg is TPM_ALG_NULL, then this shall be an Empty Buffer.</param>
    ///<param name = "hashAlg">the hash algorithm to use for the policy If the authPolicy is an Empty Buffer, then this field shall be TPM_ALG_NULL.</param>
    void SetPrimaryPolicy
    (
        const TPM_HANDLE& authHandle,
        const std::vector<BYTE>& authPolicy,
        const TPM_ALG_ID& hashAlg
    );
    /// <summary>
    /// This replaces the current platform primary seed (PPS) with a value from the RNG and sets platformPolicy to the default initialization value (the Empty Buffer).
    /// </summary>
    ///<param name = "authHandle">TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
    void ChangePPS
    (
        const TPM_HANDLE& authHandle
    );
    /// <summary>
    /// This replaces the current endorsement primary seed (EPS) with a value from the RNG and sets the Endorsement hierarchy controls to their default initialization values: ehEnable is SET, endorsementAuth and endorsementPolicy are both set to the Empty Buffer. It will flush any resident objects (transient or persistent) in the Endorsement hierarchy and not allow objects in the hierarchy associated with the previous EPS to be loaded.
    /// </summary>
    ///<param name = "authHandle">TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER</param>
    void ChangeEPS
    (
        const TPM_HANDLE& authHandle
    );
    /// <summary>
    /// This command removes all TPM context associated with a specific Owner.
    /// </summary>
    ///<param name = "authHandle">TPM_RH_LOCKOUT or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER</param>
    void Clear
    (
        const TPM_HANDLE& authHandle
    );
    /// <summary>
    /// TPM2_ClearControl() disables and enables the execution of TPM2_Clear().
    /// </summary>
    ///<param name = "auth">TPM_RH_LOCKOUT or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER</param>
    ///<param name = "disable">YES if the disableOwnerClear flag is to be SET, NO if the flag is to be CLEAR.</param>
    void ClearControl
    (
        const TPM_HANDLE& auth,
        const BYTE& disable
    );
    /// <summary>
    /// This command allows the authorization secret for a hierarchy or lockout to be changed using the current authorization value as the command authorization.
    /// </summary>
    ///<param name = "authHandle">TPM_RH_LOCKOUT, TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
    ///<param name = "newAuth">new authorization value</param>
    void HierarchyChangeAuth
    (
        const TPM_HANDLE& authHandle,
        const std::vector<BYTE>& newAuth
    );
    /// <summary>
    /// This command cancels the effect of a TPM lockout due to a number of successive authorization failures. If this command is properly authorized, the lockout counter is set to zero.
    /// </summary>
    ///<param name = "lockHandle">TPM_RH_LOCKOUT Auth Index: 1 Auth Role: USER</param>
    void DictionaryAttackLockReset
    (
        const TPM_HANDLE& lockHandle
    );
    /// <summary>
    /// This command changes the lockout parameters.
    /// </summary>
    ///<param name = "lockHandle">TPM_RH_LOCKOUT Auth Index: 1 Auth Role: USER</param>
    ///<param name = "newMaxTries">count of authorization failures before the lockout is imposed</param>
    ///<param name = "newRecoveryTime">time in seconds before the authorization failure count is automatically decremented A value of zero indicates that DA protection is disabled.</param>
    ///<param name = "lockoutRecovery">time in seconds after a lockoutAuth failure before use of lockoutAuth is allowed A value of zero indicates that a reboot is required.</param>
    void DictionaryAttackParameters
    (
        const TPM_HANDLE& lockHandle,
        const UINT32& newMaxTries,
        const UINT32& newRecoveryTime,
        const UINT32& lockoutRecovery
    );
    /// <summary>
    /// This command is used to determine which commands require assertion of Physical Presence (PP) in addition to platformAuth/platformPolicy.
    /// </summary>
    ///<param name = "auth">TPM_RH_PLATFORM+PP Auth Index: 1 Auth Role: USER + Physical Presence</param>
    ///<param name = "setList">list of commands to be added to those that will require that Physical Presence be asserted</param>
    ///<param name = "clearList">list of commands that will no longer require that Physical Presence be asserted</param>
    void PP_Commands
    (
        const TPM_HANDLE& auth,
        const std::vector<TPM_CC>& setList,
        const std::vector<TPM_CC>& clearList
    );
    /// <summary>
    /// This command allows the platform to change the set of algorithms that are used by the TPM. The algorithmSet setting is a vendor-dependent value.
    /// </summary>
    ///<param name = "authHandle">TPM_RH_PLATFORM Auth Index: 1 Auth Role: USER</param>
    ///<param name = "algorithmSet">a TPM vendor-dependent value indicating the algorithm set selection</param>
    void SetAlgorithmSet
    (
        const TPM_HANDLE& authHandle,
        const UINT32& algorithmSet
    );
    /// <summary>
    /// This command uses platformPolicy and a TPM Vendor Authorization Key to authorize a Field Upgrade Manifest.
    /// </summary>
    ///<param name = "authorization">TPM_RH_PLATFORM+{PP} Auth Index:1 Auth Role: ADMIN</param>
    ///<param name = "keyHandle">handle of a public area that contains the TPM Vendor Authorization Key that will be used to validate manifestSignature Auth Index: None</param>
    ///<param name = "fuDigest">digest of the first block in the field upgrade sequence</param>
    ///<param name = "manifestSignature">signature over fuDigest using the key associated with keyHandle (not optional)(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
    void FieldUpgradeStart
    (
        const TPM_HANDLE& authorization,
        const TPM_HANDLE& keyHandle,
        const std::vector<BYTE>& fuDigest,
        const TPMU_SIGNATURE& manifestSignature
    );
    /// <summary>
    /// This command will take the actual field upgrade image to be installed on the TPM. The exact format of fuData is vendor-specific. This command is only possible following a successful TPM2_FieldUpgradeStart(). If the TPM has not received a properly authorized TPM2_FieldUpgradeStart(), then the TPM shall return TPM_RC_FIELDUPGRADE.
    /// </summary>
    ///<param name = "fuData">field upgrade image data</param>
    ///<param name = "nextDigest">tagged digest of the next block TPM_ALG_NULL if field update is complete</param>
    ///<param name = "firstDigest">tagged digest of the first block of the sequence</param>
    FieldUpgradeDataResponse FieldUpgradeData
    (
        const std::vector<BYTE>& fuData
    );
    /// <summary>
    /// This command is used to read a copy of the current firmware installed in the TPM.
    /// </summary>
    ///<param name = "sequenceNumber">the number of previous calls to this command in this sequence set to 0 on the first call</param>
    ///<param name = "fuDataSize">size of the buffer</param>
    ///<param name = "fuData">field upgrade image data</param>
    std::vector<BYTE> FirmwareRead
    (
        const UINT32& sequenceNumber
    );
    /// <summary>
    /// This command saves a session context, object context, or sequence object context outside the TPM.
    /// </summary>
    ///<param name = "saveHandle">handle of the resource to save Auth Index: None</param>
    ///<param name = "context"></param>
    TPMS_CONTEXT ContextSave
    (
        const TPM_HANDLE& saveHandle
    );
    /// <summary>
    /// This command is used to reload a context that has been saved by TPM2_ContextSave().
    /// </summary>
    ///<param name = "context">the context blob</param>
    ///<param name = "loadedHandle">the handle assigned to the resource after it has been successfully loaded</param>
    TPM_HANDLE ContextLoad
    (
        const TPMS_CONTEXT& context
    );
    /// <summary>
    /// This command causes all context associated with a loaded object, sequence object, or session to be removed from TPM memory.
    /// </summary>
    ///<param name = "flushHandle">the handle of the item to flush NOTE	This is a use of a handle as a parameter.</param>
    void FlushContext
    (
        const TPM_HANDLE& flushHandle
    );
    /// <summary>
    /// This command allows certain Transient Objects to be made persistent or a persistent object to be evicted.
    /// </summary>
    ///<param name = "auth">TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER</param>
    ///<param name = "objectHandle">the handle of a loaded object Auth Index: None</param>
    ///<param name = "persistentHandle">if objectHandle is a transient object handle, then this is the persistent handle for the object if objectHandle is a persistent object handle, then it shall be the same value as persistentHandle</param>
    void EvictControl
    (
        const TPM_HANDLE& auth,
        const TPM_HANDLE& objectHandle,
        const TPM_HANDLE& persistentHandle
    );
    /// <summary>
    /// This command reads the current TPMS_TIME_INFO structure that contains the current setting of Time, Clock, resetCount, and restartCount.
    /// </summary>
    ///<param name = "currentTime"></param>
    TPMS_TIME_INFO ReadClock();
    /// <summary>
    /// This command is used to advance the value of the TPMs Clock. The command will fail if newTime is less than the current value of Clock or if the new time is greater than FFFF00000000000016. If both of these checks succeed, Clock is set to newTime. If either of these checks fails, the TPM shall return TPM_RC_VALUE and make no change to Clock.
    /// </summary>
    ///<param name = "auth">TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER</param>
    ///<param name = "newTime">new Clock setting in milliseconds</param>
    void ClockSet
    (
        const TPM_HANDLE& auth,
        const UINT64& newTime
    );
    /// <summary>
    /// This command adjusts the rate of advance of Clock and Time to provide a better approximation to real time.
    /// </summary>
    ///<param name = "auth">TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER</param>
    ///<param name = "rateAdjust">Adjustment to current Clock update rate</param>
    void ClockRateAdjust
    (
        const TPM_HANDLE& auth,
        const TPM_CLOCK_ADJUST& rateAdjust
    );
    /// <summary>
    /// This command returns various information regarding the TPM and its current state.
    /// </summary>
    ///<param name = "capability">group selection; determines the format of the response</param>
    ///<param name = "property">further definition of information</param>
    ///<param name = "propertyCount">number of properties of the indicated type to return</param>
    ///<param name = "moreData">flag to indicate if there are more values of this type</param>
    ///<param name = "capabilityDataCapability">the capability</param>
    ///<param name = "capabilityData">the capability data(One of TPML_ALG_PROPERTY, TPML_HANDLE, TPML_CCA, TPML_CC, TPML_CC, TPML_PCR_SELECTION, TPML_TAGGED_TPM_PROPERTY, TPML_TAGGED_PCR_PROPERTY, TPML_ECC_CURVE)</param>
    GetCapabilityResponse GetCapability
    (
        const TPM_CAP& capability,
        const UINT32& property,
        const UINT32& propertyCount
    );
    /// <summary>
    /// This command is used to check to see if specific combinations of algorithm parameters are supported.
    /// </summary>
    ///<param name = "parameters">algorithm parameters to be validated(One of TPMS_KEYEDHASH_PARMS, TPMS_SYMCIPHER_PARMS, TPMS_RSA_PARMS, TPMS_ECC_PARMS, TPMS_ASYM_PARMS)</param>
    void TestParms
    (
        const TPMU_PUBLIC_PARMS& parameters
    );
    /// <summary>
    /// This command defines the attributes of an NV Index and causes the TPM to reserve space to hold the data associated with the NV Index. If a definition already exists at the NV Index, the TPM will return TPM_RC_NV_DEFINED.
    /// </summary>
    ///<param name = "authHandle">TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
    ///<param name = "auth">the authorization value</param>
    ///<param name = "publicInfo">the public parameters of the NV area</param>
    void NV_DefineSpace
    (
        const TPM_HANDLE& authHandle,
        const std::vector<BYTE>& auth,
        const TPMS_NV_PUBLIC& publicInfo
    );
    /// <summary>
    /// This command removes an Index from the TPM.
    /// </summary>
    ///<param name = "authHandle">TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
    ///<param name = "nvIndex">the NV Index to remove from NV space Auth Index: None</param>
    void NV_UndefineSpace
    (
        const TPM_HANDLE& authHandle,
        const TPM_HANDLE& nvIndex
    );
    /// <summary>
    /// This command allows removal of a platform-created NV Index that has TPMA_NV_POLICY_DELETE SET.
    /// </summary>
    ///<param name = "nvIndex">Index to be deleted Auth Index: 1 Auth Role: ADMIN</param>
    ///<param name = "platform">TPM_RH_PLATFORM + {PP} Auth Index: 2 Auth Role: USER</param>
    void NV_UndefineSpaceSpecial
    (
        const TPM_HANDLE& nvIndex,
        const TPM_HANDLE& platform
    );
    /// <summary>
    /// This command is used to read the public area and Name of an NV Index. The public area of an Index is not privacy-sensitive and no authorization is required to read this data.
    /// </summary>
    ///<param name = "nvIndex">the NV Index Auth Index: None</param>
    ///<param name = "nvPublicSize">size of nvPublic</param>
    ///<param name = "nvPublic">the public area of the NV Index</param>
    ///<param name = "nvNameSize">size of the Name structure</param>
    ///<param name = "nvName">the Name of the nvIndex</param>
    NV_ReadPublicResponse NV_ReadPublic
    (
        const TPM_HANDLE& nvIndex
    );
    /// <summary>
    /// This command writes a value to an area in NV memory that was previously defined by TPM2_NV_DefineSpace().
    /// </summary>
    ///<param name = "authHandle">handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER</param>
    ///<param name = "nvIndex">the NV Index of the area to write Auth Index: None</param>
    ///<param name = "data">the data to write</param>
    ///<param name = "offset">the offset into the NV Area</param>
    void NV_Write
    (
        const TPM_HANDLE& authHandle,
        const TPM_HANDLE& nvIndex,
        const std::vector<BYTE>& data,
        const UINT16& offset
    );
    /// <summary>
    /// This command is used to increment the value in an NV Index that has TPMA_NV_COUNTER SET. The data value of the NV Index is incremented by one.
    /// </summary>
    ///<param name = "authHandle">handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER</param>
    ///<param name = "nvIndex">the NV Index to increment Auth Index: None</param>
    void NV_Increment
    (
        const TPM_HANDLE& authHandle,
        const TPM_HANDLE& nvIndex
    );
    /// <summary>
    /// This command extends a value to an area in NV memory that was previously defined by TPM2_NV_DefineSpace.
    /// </summary>
    ///<param name = "authHandle">handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER</param>
    ///<param name = "nvIndex">the NV Index to extend Auth Index: None</param>
    ///<param name = "data">the data to extend</param>
    void NV_Extend
    (
        const TPM_HANDLE& authHandle,
        const TPM_HANDLE& nvIndex,
        const std::vector<BYTE>& data
    );
    /// <summary>
    /// This command is used to SET bits in an NV Index that was created as a bit field. Any number of bits from 0 to 64 may be SET. The contents of data are ORed with the current contents of the NV Index starting at offset.
    /// </summary>
    ///<param name = "authHandle">handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER</param>
    ///<param name = "nvIndex">NV Index of the area in which the bit is to be set Auth Index: None</param>
    ///<param name = "bits">the data to OR with the current contents</param>
    void NV_SetBits
    (
        const TPM_HANDLE& authHandle,
        const TPM_HANDLE& nvIndex,
        const UINT64& bits
    );
    /// <summary>
    /// If the TPMA_NV_WRITEDEFINE or TPMA_NV_WRITE_STCLEAR attributes of an NV location are SET, then this command may be used to inhibit further writes of the NV Index.
    /// </summary>
    ///<param name = "authHandle">handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER</param>
    ///<param name = "nvIndex">the NV Index of the area to lock Auth Index: None</param>
    void NV_WriteLock
    (
        const TPM_HANDLE& authHandle,
        const TPM_HANDLE& nvIndex
    );
    /// <summary>
    /// The command will SET TPMA_NV_WRITELOCKED for all indexes that have their TPMA_NV_GLOBALLOCK attribute SET.
    /// </summary>
    ///<param name = "authHandle">TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
    void NV_GlobalWriteLock
    (
        const TPM_HANDLE& authHandle
    );
    /// <summary>
    /// This command reads a value from an area in NV memory previously defined by TPM2_NV_DefineSpace().
    /// </summary>
    ///<param name = "authHandle">the handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER</param>
    ///<param name = "nvIndex">the NV Index to be read Auth Index: None</param>
    ///<param name = "size">number of octets to read</param>
    ///<param name = "offset">octet offset into the area This value shall be less than or equal to the size of the nvIndex data.</param>
    ///<param name = "dataSize">size of the buffer</param>
    ///<param name = "data">the data read</param>
    std::vector<BYTE> NV_Read
    (
        const TPM_HANDLE& authHandle,
        const TPM_HANDLE& nvIndex,
        const UINT16& size,
        const UINT16& offset
    );
    /// <summary>
    /// If TPMA_NV_READ_STCLEAR is SET in an Index, then this command may be used to prevent further reads of the NV Index until the next TPM2_Startup (TPM_SU_CLEAR).
    /// </summary>
    ///<param name = "authHandle">the handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER</param>
    ///<param name = "nvIndex">the NV Index to be locked Auth Index: None</param>
    void NV_ReadLock
    (
        const TPM_HANDLE& authHandle,
        const TPM_HANDLE& nvIndex
    );
    /// <summary>
    /// This command allows the authorization secret for an NV Index to be changed.
    /// </summary>
    ///<param name = "nvIndex">handle of the entity Auth Index: 1 Auth Role: ADMIN</param>
    ///<param name = "newAuth">new authorization value</param>
    void NV_ChangeAuth
    (
        const TPM_HANDLE& nvIndex,
        const std::vector<BYTE>& newAuth
    );
    /// <summary>
    /// The purpose of this command is to certify the contents of an NV Index or portion of an NV Index.
    /// </summary>
    ///<param name = "signHandle">handle of the key used to sign the attestation structure Auth Index: 1 Auth Role: USER</param>
    ///<param name = "authHandle">handle indicating the source of the authorization value for the NV Index Auth Index: 2 Auth Role: USER</param>
    ///<param name = "nvIndex">Index for the area to be certified Auth Index: None</param>
    ///<param name = "qualifyingData">user-provided qualifying data</param>
    ///<param name = "inScheme">signing scheme to use if the scheme for signHandle is TPM_ALG_NULL(One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)</param>
    ///<param name = "size">number of octets to certify</param>
    ///<param name = "offset">octet offset into the area This value shall be less than or equal to the size of the nvIndex data.</param>
    ///<param name = "certifyInfoSize">size of the attestationData structure</param>
    ///<param name = "certifyInfo">the structure that was signed</param>
    ///<param name = "signatureSigAlg">selector of the algorithm used to construct the signature</param>
    ///<param name = "signature">the asymmetric signature over certifyInfo using the key referenced by signHandle(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
    NV_CertifyResponse NV_Certify
    (
        const TPM_HANDLE& signHandle,
        const TPM_HANDLE& authHandle,
        const TPM_HANDLE& nvIndex,
        const std::vector<BYTE>& qualifyingData,
        const TPMU_SIG_SCHEME& inScheme,
        const UINT16& size,
        const UINT16& offset
    );
    /// <summary>
    /// This is a placeholder to allow testing of the dispatch code.
    /// </summary>
    ///<param name = "inputData">dummy data</param>
    ///<param name = "outputDataSize">size in octets of the buffer field; may be 0</param>
    ///<param name = "outputData">dummy data</param>
    std::vector<BYTE> Vendor_TCG_Test
    (
        const std::vector<BYTE>& inputData
    );
    class _DLLEXP_ AsyncMethods
    {
    protected: Tpm2& theTpm;
    public: AsyncMethods(Tpm2& _tpm):theTpm(_tpm){;};
    public:
        /// <summary>
        /// TPM2_Startup() is always preceded by _TPM_Init, which is the physical indication that TPM initialization is necessary because of a system-wide reset. TPM2_Startup() is only valid after _TPM_Init. Additional TPM2_Startup() commands are not allowed after it has completed successfully. If a TPM requires TPM2_Startup() and another command is received, or if the TPM receives TPM2_Startup() when it is not required, the TPM shall return TPM_RC_INITIALIZE.
        /// </summary>
        ///<param name = "startupType">TPM_SU_CLEAR or TPM_SU_STATE</param>
        void Startup
        (
            const TPM_SU& startupType
        );
        /// <summary>
        /// This command is used to prepare the TPM for a power cycle. The shutdownType parameter indicates how the subsequent TPM2_Startup() will be processed.
        /// </summary>
        ///<param name = "shutdownType">TPM_SU_CLEAR or TPM_SU_STATE</param>
        void Shutdown
        (
            const TPM_SU& shutdownType
        );
        /// <summary>
        /// This command causes the TPM to perform a test of its capabilities. If the fullTest is YES, the TPM will test all functions. If fullTest = NO, the TPM will only test those functions that have not previously been tested.
        /// </summary>
        ///<param name = "fullTest">YES if full test to be performed NO if only test of untested functions required</param>
        void SelfTest
        (
            const BYTE& fullTest
        );
        /// <summary>
        /// This command causes the TPM to perform a test of the selected algorithms.
        /// </summary>
        ///<param name = "toTest">list of algorithms that should be tested</param>
        void IncrementalSelfTest
        (
            const std::vector<TPM_ALG_ID>& toTest
        );
        /// <summary>
        /// This command returns manufacturer-specific information regarding the results of a self-test and an indication of the test status.
        /// </summary>
        void GetTestResult();
        /// <summary>
        /// This command is used to start an authorization session using alternative methods of establishing the session key (sessionKey). The session key is then used to derive values used for authorization and for encrypting parameters.
        /// </summary>
        ///<param name = "tpmKey">handle of a loaded decrypt key used to encrypt salt may be TPM_RH_NULL Auth Index: None</param>
        ///<param name = "bind">entity providing the authValue may be TPM_RH_NULL Auth Index: None</param>
        ///<param name = "nonceCaller">initial nonceCaller, sets nonce size for the session shall be at least 16 octets</param>
        ///<param name = "encryptedSalt">value encrypted according to the type of tpmKey If tpmKey is TPM_RH_NULL, this shall be the Empty Buffer.</param>
        ///<param name = "sessionType">indicates the type of the session; simple HMAC or policy (including a trial policy)</param>
        ///<param name = "symmetric">the algorithm and key size for parameter encryption may select TPM_ALG_NULL</param>
        ///<param name = "authHash">hash algorithm to use for the session Shall be a hash algorithm supported by the TPM and not TPM_ALG_NULL</param>
        void StartAuthSession
        (
            const TPM_HANDLE& tpmKey,
            const TPM_HANDLE& bind,
            const std::vector<BYTE>& nonceCaller,
            const std::vector<BYTE>& encryptedSalt,
            const TPM_SE& sessionType,
            const TPMT_SYM_DEF& symmetric,
            const TPM_ALG_ID& authHash
        );
        /// <summary>
        /// This command allows a policy authorization session to be returned to its initial state. This command is used after the TPM returns TPM_RC_PCR_CHANGED. That response code indicates that a policy will fail because the PCR have changed after TPM2_PolicyPCR() was executed. Restarting the session allows the authorizations to be replayed because the session restarts with the same nonceTPM. If the PCR are valid for the policy, the policy may then succeed.
        /// </summary>
        ///<param name = "sessionHandle">the handle for the policy session</param>
        void PolicyRestart
        (
            const TPM_HANDLE& sessionHandle
        );
        /// <summary>
        /// This command is used to create an object that can be loaded into a TPM using TPM2_Load(). If the command completes successfully, the TPM will create the new object and return the objects creation data (creationData), its public area (outPublic), and its encrypted sensitive area (outPrivate). Preservation of the returned data is the responsibility of the caller. The object will need to be loaded (TPM2_Load()) before it may be used.
        /// </summary>
        ///<param name = "parentHandle">handle of parent for new object Auth Index: 1 Auth Role: USER</param>
        ///<param name = "inSensitive">the sensitive data</param>
        ///<param name = "inPublic">the public template</param>
        ///<param name = "outsideInfo">data that will be included in the creation data for this object to provide permanent, verifiable linkage between this object and some object owner data</param>
        ///<param name = "creationPCR">PCR that will be used in creation data</param>
        void Create
        (
            const TPM_HANDLE& parentHandle,
            const TPMS_SENSITIVE_CREATE& inSensitive,
            const TPMT_PUBLIC& inPublic,
            const std::vector<BYTE>& outsideInfo,
            const std::vector<TPMS_PCR_SELECTION>& creationPCR
        );
        /// <summary>
        /// This command is used to load objects into the TPM. This command is used when both a TPM2B_PUBLIC and TPM2B_PRIVATE are to be loaded. If only a TPM2B_PUBLIC is to be loaded, the TPM2_LoadExternal command is used.
        /// </summary>
        ///<param name = "parentHandle">TPM handle of parent key; shall not be a reserved handle Auth Index: 1 Auth Role: USER</param>
        ///<param name = "inPrivate">the private portion of the object</param>
        ///<param name = "inPublic">the public portion of the object</param>
        void Load
        (
            const TPM_HANDLE& parentHandle,
            const TPM2B_PRIVATE& inPrivate,
            const TPMT_PUBLIC& inPublic
        );
        /// <summary>
        /// This command is used to load an object that is not a Protected Object into the TPM. The command allows loading of a public area or both a public and sensitive area.
        /// </summary>
        ///<param name = "inPrivate">the sensitive portion of the object (optional)</param>
        ///<param name = "inPublic">the public portion of the object</param>
        ///<param name = "hierarchy">hierarchy with which the object area is associated</param>
        void LoadExternal
        (
            const TPMT_SENSITIVE& inPrivate,
            const TPMT_PUBLIC& inPublic,
            const TPM_HANDLE& hierarchy
        );
        /// <summary>
        /// This command allows access to the public area of a loaded object.
        /// </summary>
        ///<param name = "objectHandle">TPM handle of an object Auth Index: None</param>
        void ReadPublic
        (
            const TPM_HANDLE& objectHandle
        );
        /// <summary>
        /// This command enables the association of a credential with an object in a way that ensures that the TPM has validated the parameters of the credentialed object.
        /// </summary>
        ///<param name = "activateHandle">handle of the object associated with certificate in credentialBlob Auth Index: 1 Auth Role: ADMIN</param>
        ///<param name = "keyHandle">loaded key used to decrypt the TPMS_SENSITIVE in credentialBlob Auth Index: 2 Auth Role: USER</param>
        ///<param name = "credentialBlob">the credential</param>
        ///<param name = "secret">keyHandle algorithm-dependent encrypted seed that protects credentialBlob</param>
        void ActivateCredential
        (
            const TPM_HANDLE& activateHandle,
            const TPM_HANDLE& keyHandle,
            const std::vector<BYTE>& credentialBlob,
            const std::vector<BYTE>& secret
        );
        /// <summary>
        /// This command allows the TPM to perform the actions required of a Certificate Authority (CA) in creating a TPM2B_ID_OBJECT containing an activation credential.
        /// </summary>
        ///<param name = "handle">loaded public area, used to encrypt the sensitive area containing the credential key Auth Index: None</param>
        ///<param name = "credential">the credential information</param>
        ///<param name = "objectName">Name of the object to which the credential applies</param>
        void MakeCredential
        (
            const TPM_HANDLE& handle,
            const std::vector<BYTE>& credential,
            const std::vector<BYTE>& objectName
        );
        /// <summary>
        /// This command returns the data in a loaded Sealed Data Object.
        /// </summary>
        ///<param name = "itemHandle">handle of a loaded data object Auth Index: 1 Auth Role: USER</param>
        void Unseal
        (
            const TPM_HANDLE& itemHandle
        );
        /// <summary>
        /// This command is used to change the authorization secret for a TPM-resident object.
        /// </summary>
        ///<param name = "objectHandle">handle of the object Auth Index: 1 Auth Role: ADMIN</param>
        ///<param name = "parentHandle">handle of the parent Auth Index: None</param>
        ///<param name = "newAuth">new authorization value</param>
        void ObjectChangeAuth
        (
            const TPM_HANDLE& objectHandle,
            const TPM_HANDLE& parentHandle,
            const std::vector<BYTE>& newAuth
        );
        /// <summary>
        /// This command duplicates a loaded object so that it may be used in a different hierarchy. The new parent key for the duplicate may be on the same or different TPM or TPM_RH_NULL. Only the public area of newParentHandle is required to be loaded.
        /// </summary>
        ///<param name = "objectHandle">loaded object to duplicate Auth Index: 1 Auth Role: DUP</param>
        ///<param name = "newParentHandle">shall reference the public area of an asymmetric key Auth Index: None</param>
        ///<param name = "encryptionKeyIn">optional symmetric encryption key The size for this key is set to zero when the TPM is to generate the key. This parameter may be encrypted.</param>
        ///<param name = "symmetricAlg">definition for the symmetric algorithm to be used for the inner wrapper may be TPM_ALG_NULL if no inner wrapper is applied</param>
        void Duplicate
        (
            const TPM_HANDLE& objectHandle,
            const TPM_HANDLE& newParentHandle,
            const std::vector<BYTE>& encryptionKeyIn,
            const TPMT_SYM_DEF_OBJECT& symmetricAlg
        );
        /// <summary>
        /// This command allows the TPM to serve in the role as a Duplication Authority. If proper authorization for use of the oldParent is provided, then an HMAC key and a symmetric key are recovered from inSymSeed and used to integrity check and decrypt inDuplicate. A new protection seed value is generated according to the methods appropriate for newParent and the blob is re-encrypted and a new integrity value is computed. The re-encrypted blob is returned in outDuplicate and the symmetric key returned in outSymKey.
        /// </summary>
        ///<param name = "oldParent">parent of object Auth Index: 1 Auth Role: User</param>
        ///<param name = "newParent">new parent of the object Auth Index: None</param>
        ///<param name = "inDuplicate">an object encrypted using symmetric key derived from inSymSeed</param>
        ///<param name = "name">the Name of the object being rewrapped</param>
        ///<param name = "inSymSeed">seed for symmetric key needs oldParent private key to recover the seed and generate the symmetric key</param>
        void Rewrap
        (
            const TPM_HANDLE& oldParent,
            const TPM_HANDLE& newParent,
            const TPM2B_PRIVATE& inDuplicate,
            const std::vector<BYTE>& name,
            const std::vector<BYTE>& inSymSeed
        );
        /// <summary>
        /// This command allows an object to be encrypted using the symmetric encryption values of a Storage Key. After encryption, the object may be loaded and used in the new hierarchy. The imported object (duplicate) may be singly encrypted, multiply encrypted, or unencrypted.
        /// </summary>
        ///<param name = "parentHandle">the handle of the new parent for the object Auth Index: 1 Auth Role: USER</param>
        ///<param name = "encryptionKey">the optional symmetric encryption key used as the inner wrapper for duplicate If symmetricAlg is TPM_ALG_NULL, then this parameter shall be the Empty Buffer.</param>
        ///<param name = "objectPublic">the public area of the object to be imported This is provided so that the integrity value for duplicate and the object attributes can be checked. NOTE	Even if the integrity value of the object is not checked on input, the object Name is required to create the integrity value for the imported object.</param>
        ///<param name = "duplicate">the symmetrically encrypted duplicate object that may contain an inner symmetric wrapper</param>
        ///<param name = "inSymSeed">symmetric key used to encrypt duplicate inSymSeed is encrypted/encoded using the algorithms of newParent.</param>
        ///<param name = "symmetricAlg">definition for the symmetric algorithm to use for the inner wrapper If this algorithm is TPM_ALG_NULL, no inner wrapper is present and encryptionKey shall be the Empty Buffer.</param>
        void Import
        (
            const TPM_HANDLE& parentHandle,
            const std::vector<BYTE>& encryptionKey,
            const TPMT_PUBLIC& objectPublic,
            const TPM2B_PRIVATE& duplicate,
            const std::vector<BYTE>& inSymSeed,
            const TPMT_SYM_DEF_OBJECT& symmetricAlg
        );
        /// <summary>
        /// This command performs RSA encryption using the indicated padding scheme according to IETF RFC 3447. If the scheme of keyHandle is TPM_ALG_NULL, then the caller may use inScheme to specify the padding scheme. If scheme of keyHandle is not TPM_ALG_NULL, then inScheme shall either be TPM_ALG_NULL or be the same as scheme (TPM_RC_SCHEME).
        /// </summary>
        ///<param name = "keyHandle">reference to public portion of RSA key to use for encryption Auth Index: None</param>
        ///<param name = "message">message to be encrypted NOTE 1	The data type was chosen because it limits the overall size of the input to no greater than the size of the largest RSA public key. This may be larger than allowed for keyHandle.</param>
        ///<param name = "inScheme">the padding scheme to use if scheme associated with keyHandle is TPM_ALG_NULL(One of TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES, TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME)</param>
        ///<param name = "label">optional label L to be associated with the message Size of the buffer is zero if no label is present NOTE 2	See description of label above.</param>
        void RSA_Encrypt
        (
            const TPM_HANDLE& keyHandle,
            const std::vector<BYTE>& message,
            const TPMU_ASYM_SCHEME& inScheme,
            const std::vector<BYTE>& label
        );
        /// <summary>
        /// This command performs RSA decryption using the indicated padding scheme according to IETF RFC 3447 ((PKCS#1).
        /// </summary>
        ///<param name = "keyHandle">RSA key to use for decryption Auth Index: 1 Auth Role: USER</param>
        ///<param name = "cipherText">cipher text to be decrypted NOTE	An encrypted RSA data block is the size of the public modulus.</param>
        ///<param name = "inScheme">the padding scheme to use if scheme associated with keyHandle is TPM_ALG_NULL(One of TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES, TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME)</param>
        ///<param name = "label">label whose association with the message is to be verified</param>
        void RSA_Decrypt
        (
            const TPM_HANDLE& keyHandle,
            const std::vector<BYTE>& cipherText,
            const TPMU_ASYM_SCHEME& inScheme,
            const std::vector<BYTE>& label
        );
        /// <summary>
        /// This command uses the TPM to generate an ephemeral key pair (de, Qe where Qe  [de]G). It uses the private ephemeral key and a loaded public key (QS) to compute the shared secret value (P  [hde]QS).
        /// </summary>
        ///<param name = "keyHandle">Handle of a loaded ECC key public area. Auth Index: None</param>
        void ECDH_KeyGen
        (
            const TPM_HANDLE& keyHandle
        );
        /// <summary>
        /// This command uses the TPM to recover the Z value from a public point (QB) and a private key (ds). It will perform the multiplication of the provided inPoint (QB) with the private key (ds) and return the coordinates of the resultant point (Z = (xZ , yZ)  [hds]QB; where h is the cofactor of the curve).
        /// </summary>
        ///<param name = "keyHandle">handle of a loaded ECC key Auth Index: 1 Auth Role: USER</param>
        ///<param name = "inPoint">a public key</param>
        void ECDH_ZGen
        (
            const TPM_HANDLE& keyHandle,
            const TPMS_ECC_POINT& inPoint
        );
        /// <summary>
        /// This command returns the parameters of an ECC curve identified by its TCG-assigned curveID.
        /// </summary>
        ///<param name = "curveID">parameter set selector</param>
        void ECC_Parameters
        (
            const TPM_ECC_CURVE& curveID
        );
        /// <summary>
        /// This command supports two-phase key exchange protocols. The command is used in combination with TPM2_EC_Ephemeral(). TPM2_EC_Ephemeral() generates an ephemeral key and returns the public point of that ephemeral key along with a numeric value that allows the TPM to regenerate the associated private key.
        /// </summary>
        ///<param name = "keyA">handle of an unrestricted decryption key ECC The private key referenced by this handle is used as dS,A Auth Index: 1 Auth Role: USER</param>
        ///<param name = "inQsB">other partys static public key (Qs,B = (Xs,B, Ys,B))</param>
        ///<param name = "inQeB">other party's ephemeral public key (Qe,B = (Xe,B, Ye,B))</param>
        ///<param name = "inScheme">the key exchange scheme</param>
        ///<param name = "counter">value returned by TPM2_EC_Ephemeral()</param>
        void ZGen_2Phase
        (
            const TPM_HANDLE& keyA,
            const TPMS_ECC_POINT& inQsB,
            const TPMS_ECC_POINT& inQeB,
            const TPM_ALG_ID& inScheme,
            const UINT16& counter
        );
        /// <summary>
        /// This command performs symmetric encryption or decryption.
        /// </summary>
        ///<param name = "keyHandle">the symmetric key used for the operation Auth Index: 1 Auth Role: USER</param>
        ///<param name = "decrypt">if YES, then the operation is decryption; if NO, the operation is encryption</param>
        ///<param name = "mode">symmetric mode For a restricted key, this field shall match the default mode of the key or be TPM_ALG_NULL.</param>
        ///<param name = "ivIn">an initial value as required by the algorithm</param>
        ///<param name = "inData">the data to be encrypted/decrypted</param>
        void EncryptDecrypt
        (
            const TPM_HANDLE& keyHandle,
            const BYTE& decrypt,
            const TPM_ALG_ID& mode,
            const std::vector<BYTE>& ivIn,
            const std::vector<BYTE>& inData
        );
        /// <summary>
        /// This command performs a hash operation on a data buffer and returns the results.
        /// </summary>
        ///<param name = "data">data to be hashed</param>
        ///<param name = "hashAlg">algorithm for the hash being computed  shall not be TPM_ALG_NULL</param>
        ///<param name = "hierarchy">hierarchy to use for the ticket (TPM_RH_NULL allowed)</param>
        void Hash
        (
            const std::vector<BYTE>& data,
            const TPM_ALG_ID& hashAlg,
            const TPM_HANDLE& hierarchy
        );
        /// <summary>
        /// This command performs an HMAC on the supplied data using the indicated hash algorithm.
        /// </summary>
        ///<param name = "handle">handle for the symmetric signing key providing the HMAC key Auth Index: 1 Auth Role: USER</param>
        ///<param name = "buffer">HMAC data</param>
        ///<param name = "hashAlg">algorithm to use for HMAC</param>
        void HMAC
        (
            const TPM_HANDLE& handle,
            const std::vector<BYTE>& buffer,
            const TPM_ALG_ID& hashAlg
        );
        /// <summary>
        /// This command returns the next bytesRequested octets from the random number generator (RNG).
        /// </summary>
        ///<param name = "bytesRequested">number of octets to return</param>
        void GetRandom
        (
            const UINT16& bytesRequested
        );
        /// <summary>
        /// This command is used to add "additional information" to the RNG state.
        /// </summary>
        ///<param name = "inData">additional information</param>
        void StirRandom
        (
            const std::vector<BYTE>& inData
        );
        /// <summary>
        /// This command starts an HMAC sequence. The TPM will create and initialize an HMAC sequence structure, assign a handle to the sequence, and set the authValue of the sequence object to the value in auth.
        /// </summary>
        ///<param name = "handle">handle of an HMAC key Auth Index: 1 Auth Role: USER</param>
        ///<param name = "auth">authorization value for subsequent use of the sequence</param>
        ///<param name = "hashAlg">the hash algorithm to use for the HMAC</param>
        void HMAC_Start
        (
            const TPM_HANDLE& handle,
            const std::vector<BYTE>& auth,
            const TPM_ALG_ID& hashAlg
        );
        /// <summary>
        /// This command starts a hash or an Event Sequence. If hashAlg is an implemented hash, then a hash sequence is started. If hashAlg is TPM_ALG_NULL, then an Event Sequence is started. If hashAlg is neither an implemented algorithm nor TPM_ALG_NULL, then the TPM shall return TPM_RC_HASH.
        /// </summary>
        ///<param name = "auth">authorization value for subsequent use of the sequence</param>
        ///<param name = "hashAlg">the hash algorithm to use for the hash sequence An Event Sequence starts if this is TPM_ALG_NULL.</param>
        void HashSequenceStart
        (
            const std::vector<BYTE>& auth,
            const TPM_ALG_ID& hashAlg
        );
        /// <summary>
        /// This command is used to add data to a hash or HMAC sequence. The amount of data in buffer may be any size up to the limits of the TPM.
        /// </summary>
        ///<param name = "sequenceHandle">handle for the sequence object Auth Index: 1 Auth Role: USER</param>
        ///<param name = "buffer">data to be added to hash</param>
        void SequenceUpdate
        (
            const TPM_HANDLE& sequenceHandle,
            const std::vector<BYTE>& buffer
        );
        /// <summary>
        /// This command adds the last part of data, if any, to a hash/HMAC sequence and returns the result.
        /// </summary>
        ///<param name = "sequenceHandle">authorization for the sequence Auth Index: 1 Auth Role: USER</param>
        ///<param name = "buffer">data to be added to the hash/HMAC</param>
        ///<param name = "hierarchy">hierarchy of the ticket for a hash</param>
        void SequenceComplete
        (
            const TPM_HANDLE& sequenceHandle,
            const std::vector<BYTE>& buffer,
            const TPM_HANDLE& hierarchy
        );
        /// <summary>
        /// This command adds the last part of data, if any, to an Event Sequence and returns the result in a digest list. If pcrHandle references a PCR and not TPM_RH_NULL, then the returned digest list is processed in the same manner as the digest list input parameter to TPM2_PCR_Extend() with the pcrHandle in each bank extended with the associated digest value.
        /// </summary>
        ///<param name = "pcrHandle">PCR to be extended with the Event data Auth Index: 1 Auth Role: USER</param>
        ///<param name = "sequenceHandle">authorization for the sequence Auth Index: 2 Auth Role: USER</param>
        ///<param name = "buffer">data to be added to the Event</param>
        void EventSequenceComplete
        (
            const TPM_HANDLE& pcrHandle,
            const TPM_HANDLE& sequenceHandle,
            const std::vector<BYTE>& buffer
        );
        /// <summary>
        /// The purpose of this command is to prove that an object with a specific Name is loaded in the TPM. By certifying that the object is loaded, the TPM warrants that a public area with a given Name is self-consistent and associated with a valid sensitive area. If a relying party has a public area that has the same Name as a Name certified with this command, then the values in that public area are correct.
        /// </summary>
        ///<param name = "objectHandle">handle of the object to be certified Auth Index: 1 Auth Role: ADMIN</param>
        ///<param name = "signHandle">handle of the key used to sign the attestation structure Auth Index: 2 Auth Role: USER</param>
        ///<param name = "qualifyingData">user provided qualifying data</param>
        ///<param name = "inScheme">signing scheme to use if the scheme for signHandle is TPM_ALG_NULL(One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)</param>
        void Certify
        (
            const TPM_HANDLE& objectHandle,
            const TPM_HANDLE& signHandle,
            const std::vector<BYTE>& qualifyingData,
            const TPMU_SIG_SCHEME& inScheme
        );
        /// <summary>
        /// This command is used to prove the association between an object and its creation data. The TPM will validate that the ticket was produced by the TPM and that the ticket validates the association between a loaded public area and the provided hash of the creation data (creationHash).
        /// </summary>
        ///<param name = "signHandle">handle of the key that will sign the attestation block Auth Index: 1 Auth Role: USER</param>
        ///<param name = "objectHandle">the object associated with the creation data Auth Index: None</param>
        ///<param name = "qualifyingData">user-provided qualifying data</param>
        ///<param name = "creationHash">hash of the creation data produced by TPM2_Create() or TPM2_CreatePrimary()</param>
        ///<param name = "inScheme">signing scheme to use if the scheme for signHandle is TPM_ALG_NULL(One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)</param>
        ///<param name = "creationTicket">ticket produced by TPM2_Create() or TPM2_CreatePrimary()</param>
        void CertifyCreation
        (
            const TPM_HANDLE& signHandle,
            const TPM_HANDLE& objectHandle,
            const std::vector<BYTE>& qualifyingData,
            const std::vector<BYTE>& creationHash,
            const TPMU_SIG_SCHEME& inScheme,
            const TPMT_TK_CREATION& creationTicket
        );
        /// <summary>
        /// This command is used to quote PCR values.
        /// </summary>
        ///<param name = "signHandle">handle of key that will perform signature Auth Index: 1 Auth Role: USER</param>
        ///<param name = "qualifyingData">data supplied by the caller</param>
        ///<param name = "inScheme">signing scheme to use if the scheme for signHandle is TPM_ALG_NULL(One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)</param>
        ///<param name = "PCRselect">PCR set to quote</param>
        void Quote
        (
            const TPM_HANDLE& signHandle,
            const std::vector<BYTE>& qualifyingData,
            const TPMU_SIG_SCHEME& inScheme,
            const std::vector<TPMS_PCR_SELECTION>& PCRselect
        );
        /// <summary>
        /// This command returns a digital signature of the audit session digest.
        /// </summary>
        ///<param name = "privacyAdminHandle">handle of the privacy administrator (TPM_RH_ENDORSEMENT) Auth Index: 1 Auth Role: USER</param>
        ///<param name = "signHandle">handle of the signing key Auth Index: 2 Auth Role: USER</param>
        ///<param name = "sessionHandle">handle of the audit session Auth Index: None</param>
        ///<param name = "qualifyingData">user-provided qualifying data  may be zero-length</param>
        ///<param name = "inScheme">signing scheme to use if the scheme for signHandle is TPM_ALG_NULL(One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)</param>
        void GetSessionAuditDigest
        (
            const TPM_HANDLE& privacyAdminHandle,
            const TPM_HANDLE& signHandle,
            const TPM_HANDLE& sessionHandle,
            const std::vector<BYTE>& qualifyingData,
            const TPMU_SIG_SCHEME& inScheme
        );
        /// <summary>
        /// This command returns the current value of the command audit digest, a digest of the commands being audited, and the audit hash algorithm. These values are placed in an attestation structure and signed with the key referenced by signHandle.
        /// </summary>
        ///<param name = "privacyHandle">handle of the privacy administrator (TPM_RH_ENDORSEMENT) Auth Index: 1 Auth Role: USER</param>
        ///<param name = "signHandle">the handle of the signing key Auth Index: 2 Auth Role: USER</param>
        ///<param name = "qualifyingData">other data to associate with this audit digest</param>
        ///<param name = "inScheme">signing scheme to use if the scheme for signHandle is TPM_ALG_NULL(One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)</param>
        void GetCommandAuditDigest
        (
            const TPM_HANDLE& privacyHandle,
            const TPM_HANDLE& signHandle,
            const std::vector<BYTE>& qualifyingData,
            const TPMU_SIG_SCHEME& inScheme
        );
        /// <summary>
        /// This command returns the current values of Time and Clock.
        /// </summary>
        ///<param name = "privacyAdminHandle">handle of the privacy administrator (TPM_RH_ENDORSEMENT) Auth Index: 1 Auth Role: USER</param>
        ///<param name = "signHandle">the keyHandle identifier of a loaded key that can perform digital signatures Auth Index: 2 Auth Role: USER</param>
        ///<param name = "qualifyingData">data to tick stamp</param>
        ///<param name = "inScheme">signing scheme to use if the scheme for signHandle is TPM_ALG_NULL(One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)</param>
        void GetTime
        (
            const TPM_HANDLE& privacyAdminHandle,
            const TPM_HANDLE& signHandle,
            const std::vector<BYTE>& qualifyingData,
            const TPMU_SIG_SCHEME& inScheme
        );
        /// <summary>
        /// TPM2_Commit() performs the first part of an ECC anonymous signing operation. The TPM will perform the point multiplications on the provided points and return intermediate signing values. The signHandle parameter shall refer to an ECC key with the sign attribute (TPM_RC_ATTRIBUTES) and the signing scheme must be anonymous (TPM_RC_SCHEME). Currently, TPM_ALG_ECDAA is the only defined anonymous scheme.
        /// </summary>
        ///<param name = "signHandle">handle of the key that will be used in the signing operation Auth Index: 1 Auth Role: USER</param>
        ///<param name = "P1">a point (M) on the curve used by signHandle</param>
        ///<param name = "s2">octet array used to derive x-coordinate of a base point</param>
        ///<param name = "y2">y coordinate of the point associated with s2</param>
        void Commit
        (
            const TPM_HANDLE& signHandle,
            const TPMS_ECC_POINT& P1,
            const std::vector<BYTE>& s2,
            const std::vector<BYTE>& y2
        );
        /// <summary>
        /// TPM2_EC_Ephemeral() creates an ephemeral key for use in a two-phase key exchange protocol.
        /// </summary>
        ///<param name = "curveID">The curve for the computed ephemeral point</param>
        void EC_Ephemeral
        (
            const TPM_ECC_CURVE& curveID
        );
        /// <summary>
        /// This command uses loaded keys to validate a signature on a message with the message digest passed to the TPM.
        /// </summary>
        ///<param name = "keyHandle">handle of public key that will be used in the validation Auth Index: None</param>
        ///<param name = "digest">digest of the signed message</param>
        ///<param name = "signature">signature to be tested(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
        void VerifySignature
        (
            const TPM_HANDLE& keyHandle,
            const std::vector<BYTE>& digest,
            const TPMU_SIGNATURE& signature
        );
        /// <summary>
        /// This command causes the TPM to sign an externally provided hash with the specified symmetric or asymmetric signing key.
        /// </summary>
        ///<param name = "keyHandle">Handle of key that will perform signing Auth Index: 1 Auth Role: USER</param>
        ///<param name = "digest">digest to be signed</param>
        ///<param name = "inScheme">signing scheme to use if the scheme for keyHandle is TPM_ALG_NULL(One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)</param>
        ///<param name = "validation">proof that digest was created by the TPM If keyHandle is not a restricted signing key, then this may be a NULL Ticket with tag = TPM_ST_CHECKHASH.</param>
        void Sign
        (
            const TPM_HANDLE& keyHandle,
            const std::vector<BYTE>& digest,
            const TPMU_SIG_SCHEME& inScheme,
            const TPMT_TK_HASHCHECK& validation
        );
        /// <summary>
        /// This command may be used by the Privacy Administrator or platform to change the audit status of a command or to set the hash algorithm used for the audit digest, but not both at the same time.
        /// </summary>
        ///<param name = "auth">TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
        ///<param name = "auditAlg">hash algorithm for the audit digest; if TPM_ALG_NULL, then the hash is not changed</param>
        ///<param name = "setList">list of commands that will be added to those that will be audited</param>
        ///<param name = "clearList">list of commands that will no longer be audited</param>
        void SetCommandCodeAuditStatus
        (
            const TPM_HANDLE& auth,
            const TPM_ALG_ID& auditAlg,
            const std::vector<TPM_CC>& setList,
            const std::vector<TPM_CC>& clearList
        );
        /// <summary>
        /// This command is used to cause an update to the indicated PCR. The digests parameter contains one or more tagged digest values identified by an algorithm ID. For each digest, the PCR associated with pcrHandle is Extended into the bank identified by the tag (hashAlg).
        /// </summary>
        ///<param name = "pcrHandle">handle of the PCR Auth Handle: 1 Auth Role: USER</param>
        ///<param name = "digests">list of tagged digest values to be extended</param>
        void PCR_Extend
        (
            const TPM_HANDLE& pcrHandle,
            const std::vector<TPMT_HA>& digests
        );
        /// <summary>
        /// This command is used to cause an update to the indicated PCR.
        /// </summary>
        ///<param name = "pcrHandle">Handle of the PCR Auth Handle: 1 Auth Role: USER</param>
        ///<param name = "eventData">Event data in sized buffer</param>
        void PCR_Event
        (
            const TPM_HANDLE& pcrHandle,
            const std::vector<BYTE>& eventData
        );
        /// <summary>
        /// This command returns the values of all PCR specified in pcrSelectionIn.
        /// </summary>
        ///<param name = "pcrSelectionIn">The selection of PCR to read</param>
        void PCR_Read
        (
            const std::vector<TPMS_PCR_SELECTION>& pcrSelectionIn
        );
        /// <summary>
        /// This command is used to set the desired PCR allocation of PCR and algorithms. This command requires Platform Authorization.
        /// </summary>
        ///<param name = "authHandle">TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
        ///<param name = "pcrAllocation">the requested allocation</param>
        void PCR_Allocate
        (
            const TPM_HANDLE& authHandle,
            const std::vector<TPMS_PCR_SELECTION>& pcrAllocation
        );
        /// <summary>
        /// This command is used to associate a policy with a PCR or group of PCR. The policy determines the conditions under which a PCR may be extended or reset.
        /// </summary>
        ///<param name = "authHandle">TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
        ///<param name = "authPolicy">the desired authPolicy</param>
        ///<param name = "hashAlg">the hash algorithm of the policy</param>
        ///<param name = "pcrNum">the PCR for which the policy is to be set</param>
        void PCR_SetAuthPolicy
        (
            const TPM_HANDLE& authHandle,
            const std::vector<BYTE>& authPolicy,
            const TPM_ALG_ID& hashAlg,
            const TPM_HANDLE& pcrNum
        );
        /// <summary>
        /// This command changes the authValue of a PCR or group of PCR.
        /// </summary>
        ///<param name = "pcrHandle">handle for a PCR that may have an authorization value set Auth Index: 1 Auth Role: USER</param>
        ///<param name = "auth">the desired authorization value</param>
        void PCR_SetAuthValue
        (
            const TPM_HANDLE& pcrHandle,
            const std::vector<BYTE>& auth
        );
        /// <summary>
        /// If the attribute of a PCR allows the PCR to be reset and proper authorization is provided, then this command may be used to set the PCR to zero. The attributes of the PCR may restrict the locality that can perform the reset operation.
        /// </summary>
        ///<param name = "pcrHandle">the PCR to reset Auth Index: 1 Auth Role: USER</param>
        void PCR_Reset
        (
            const TPM_HANDLE& pcrHandle
        );
        /// <summary>
        /// This command includes a signed authorization in a policy. The command ties the policy to a signing key by including the Name of the signing key in the policyDigest
        /// </summary>
        ///<param name = "authObject">handle for a key that will validate the signature Auth Index: None</param>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        ///<param name = "nonceTPM">the policy nonce for the session This can be the Empty Buffer.</param>
        ///<param name = "cpHashA">digest of the command parameters to which this authorization is limited This is not the cpHash for this command but the cpHash for the command to which this policy session will be applied. If it is not limited, the parameter will be the Empty Buffer.</param>
        ///<param name = "policyRef">a reference to a policy relating to the authorization  may be the Empty Buffer Size is limited to be no larger than the nonce size supported on the TPM.</param>
        ///<param name = "expiration">time when authorization will expire, measured in seconds from the time that nonceTPM was generated If expiration is non-negative, a NULL Ticket is returned. See 23.2.5.</param>
        ///<param name = "auth">signed authorization (not optional)(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
        void PolicySigned
        (
            const TPM_HANDLE& authObject,
            const TPM_HANDLE& policySession,
            const std::vector<BYTE>& nonceTPM,
            const std::vector<BYTE>& cpHashA,
            const std::vector<BYTE>& policyRef,
            const INT32& expiration,
            const TPMU_SIGNATURE& auth
        );
        /// <summary>
        /// This command includes a secret-based authorization to a policy. The caller proves knowledge of the secret value using an authorization session using the authValue associated with authHandle. A password session, an HMAC session, or a policy session containing TPM2_PolicyAuthValue() or TPM2_PolicyPassword() will satisfy this requirement.
        /// </summary>
        ///<param name = "authHandle">handle for an entity providing the authorization Auth Index: 1 Auth Role: USER</param>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        ///<param name = "nonceTPM">the policy nonce for the session This can be the Empty Buffer.</param>
        ///<param name = "cpHashA">digest of the command parameters to which this authorization is limited This not the cpHash for this command but the cpHash for the command to which this policy session will be applied. If it is not limited, the parameter will be the Empty Buffer.</param>
        ///<param name = "policyRef">a reference to a policy relating to the authorization  may be the Empty Buffer Size is limited to be no larger than the nonce size supported on the TPM.</param>
        ///<param name = "expiration">time when authorization will expire, measured in seconds from the time that nonceTPM was generated If expiration is non-negative, a NULL Ticket is returned. See 23.2.5.</param>
        void PolicySecret
        (
            const TPM_HANDLE& authHandle,
            const TPM_HANDLE& policySession,
            const std::vector<BYTE>& nonceTPM,
            const std::vector<BYTE>& cpHashA,
            const std::vector<BYTE>& policyRef,
            const INT32& expiration
        );
        /// <summary>
        /// This command is similar to TPM2_PolicySigned() except that it takes a ticket instead of a signed authorization. The ticket represents a validated authorization that had an expiration time associated with it.
        /// </summary>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        ///<param name = "timeout">time when authorization will expire The contents are TPM specific. This shall be the value returned when ticket was produced.</param>
        ///<param name = "cpHashA">digest of the command parameters to which this authorization is limited If it is not limited, the parameter will be the Empty Buffer.</param>
        ///<param name = "policyRef">reference to a qualifier for the policy  may be the Empty Buffer</param>
        ///<param name = "authName">name of the object that provided the authorization</param>
        ///<param name = "ticket">an authorization ticket returned by the TPM in response to a TPM2_PolicySigned() or TPM2_PolicySecret()</param>
        void PolicyTicket
        (
            const TPM_HANDLE& policySession,
            const std::vector<BYTE>& timeout,
            const std::vector<BYTE>& cpHashA,
            const std::vector<BYTE>& policyRef,
            const std::vector<BYTE>& authName,
            const TPMT_TK_AUTH& ticket
        );
        /// <summary>
        /// This command allows options in authorizations without requiring that the TPM evaluate all of the options. If a policy may be satisfied by different sets of conditions, the TPM need only evaluate one set that satisfies the policy. This command will indicate that one of the required sets of conditions has been satisfied.
        /// </summary>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        ///<param name = "pHashList">the list of hashes to check for a match</param>
        void PolicyOR
        (
            const TPM_HANDLE& policySession,
            const std::vector<TPM2B_DIGEST>& pHashList
        );
        /// <summary>
        /// This command is used to cause conditional gating of a policy based on PCR. This command together with TPM2_PolicyOR() allows one group of authorizations to occur when PCR are in one state and a different set of authorizations when the PCR are in a different state. If this command is used for a trial policySession, policySessionpolicyDigest will be updated using the values from the command rather than the values from digest of the TPM PCR.
        /// </summary>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        ///<param name = "pcrDigest">expected digest value of the selected PCR using the hash algorithm of the session; may be zero length</param>
        ///<param name = "pcrs">the PCR to include in the check digest</param>
        void PolicyPCR
        (
            const TPM_HANDLE& policySession,
            const std::vector<BYTE>& pcrDigest,
            const std::vector<TPMS_PCR_SELECTION>& pcrs
        );
        /// <summary>
        /// This command indicates that the authorization will be limited to a specific locality.
        /// </summary>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        ///<param name = "locality">the allowed localities for the policy</param>
        void PolicyLocality
        (
            const TPM_HANDLE& policySession,
            const TPMA_LOCALITY& locality
        );
        /// <summary>
        /// This command is used to cause conditional gating of a policy based on the contents of an NV Index.
        /// </summary>
        ///<param name = "authHandle">handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER</param>
        ///<param name = "nvIndex">the NV Index of the area to read Auth Index: None</param>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        ///<param name = "operandB">the second operand</param>
        ///<param name = "offset">the offset in the NV Index for the start of operand A</param>
        ///<param name = "operation">the comparison to make</param>
        void PolicyNV
        (
            const TPM_HANDLE& authHandle,
            const TPM_HANDLE& nvIndex,
            const TPM_HANDLE& policySession,
            const std::vector<BYTE>& operandB,
            const UINT16& offset,
            const TPM_EO& operation
        );
        /// <summary>
        /// This command is used to cause conditional gating of a policy based on the contents of the TPMS_TIME_INFO structure.
        /// </summary>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        ///<param name = "operandB">the second operand</param>
        ///<param name = "offset">the offset in TPMS_TIME_INFO structure for the start of operand A</param>
        ///<param name = "operation">the comparison to make</param>
        void PolicyCounterTimer
        (
            const TPM_HANDLE& policySession,
            const std::vector<BYTE>& operandB,
            const UINT16& offset,
            const TPM_EO& operation
        );
        /// <summary>
        /// This command indicates that the authorization will be limited to a specific command code.
        /// </summary>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        ///<param name = "code">the allowed commandCode</param>
        void PolicyCommandCode
        (
            const TPM_HANDLE& policySession,
            const TPM_CC& code
        );
        /// <summary>
        /// This command indicates that physical presence will need to be asserted at the time the authorization is performed.
        /// </summary>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        void PolicyPhysicalPresence
        (
            const TPM_HANDLE& policySession
        );
        /// <summary>
        /// This command is used to allow a policy to be bound to a specific command and command parameters.
        /// </summary>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        ///<param name = "cpHashA">the cpHash added to the policy</param>
        void PolicyCpHash
        (
            const TPM_HANDLE& policySession,
            const std::vector<BYTE>& cpHashA
        );
        /// <summary>
        /// This command allows a policy to be bound to a specific set of TPM entities without being bound to the parameters of the command. This is most useful for commands such as TPM2_Duplicate() and for TPM2_PCR_Event() when the referenced PCR requires a policy.
        /// </summary>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        ///<param name = "nameHash">the digest to be added to the policy</param>
        void PolicyNameHash
        (
            const TPM_HANDLE& policySession,
            const std::vector<BYTE>& nameHash
        );
        /// <summary>
        /// This command allows qualification of duplication to allow duplication to a selected new parent.
        /// </summary>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        ///<param name = "objectName">the Name of the object to be duplicated</param>
        ///<param name = "newParentName">the Name of the new parent</param>
        ///<param name = "includeObject">if YES, the objectName will be included in the value in policySessionpolicyDigest</param>
        void PolicyDuplicationSelect
        (
            const TPM_HANDLE& policySession,
            const std::vector<BYTE>& objectName,
            const std::vector<BYTE>& newParentName,
            const BYTE& includeObject
        );
        /// <summary>
        /// This command allows policies to change. If a policy were static, then it would be difficult to add users to a policy. This command lets a policy authority sign a new policy so that it may be used in an existing policy.
        /// </summary>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        ///<param name = "approvedPolicy">digest of the policy being approved</param>
        ///<param name = "policyRef">a policy qualifier</param>
        ///<param name = "keySign">Name of a key that can sign a policy addition</param>
        ///<param name = "checkTicket">ticket validating that approvedPolicy and policyRef were signed by keySign</param>
        void PolicyAuthorize
        (
            const TPM_HANDLE& policySession,
            const std::vector<BYTE>& approvedPolicy,
            const std::vector<BYTE>& policyRef,
            const std::vector<BYTE>& keySign,
            const TPMT_TK_VERIFIED& checkTicket
        );
        /// <summary>
        /// This command allows a policy to be bound to the authorization value of the authorized entity.
        /// </summary>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        void PolicyAuthValue
        (
            const TPM_HANDLE& policySession
        );
        /// <summary>
        /// This command allows a policy to be bound to the authorization value of the authorized object.
        /// </summary>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        void PolicyPassword
        (
            const TPM_HANDLE& policySession
        );
        /// <summary>
        /// This command returns the current policyDigest of the session. This command allows the TPM to be used to perform the actions required to pre-compute the authPolicy for an object.
        /// </summary>
        ///<param name = "policySession">handle for the policy session Auth Index: None</param>
        void PolicyGetDigest
        (
            const TPM_HANDLE& policySession
        );
        /// <summary>
        /// This command allows a policy to be bound to the TPMA_NV_WRITTEN attributes. This is a deferred assertion. Values are stored in the policy session context and checked when the policy is used for authorization.
        /// </summary>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        ///<param name = "writtenSet">YES if NV Index is required to have been written NO if NV Index is required not to have been written</param>
        void PolicyNvWritten
        (
            const TPM_HANDLE& policySession,
            const BYTE& writtenSet
        );
        /// <summary>
        /// This command allows creation of an authorization policy that will only allow creation of a child object with the correct properties.
        /// </summary>
        ///<param name = "policySession">handle for the policy session being extended Auth Index: None</param>
        ///<param name = "templateHash">the hash of the template to be added to the policy</param>
        void PolicyTemplate
        (
            const TPM_HANDLE& policySession,
            const std::vector<BYTE>& templateHash
        );
        /// <summary>
        /// This command is used to create a Primary Object under one of the Primary Seeds or a Temporary Object under TPM_RH_NULL. The command uses a TPM2B_PUBLIC as a template for the object to be created. The command will create and load a Primary Object. The sensitive area is not returned.
        /// </summary>
        ///<param name = "primaryHandle">TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM+{PP}, or TPM_RH_NULL Auth Index: 1 Auth Role: USER</param>
        ///<param name = "inSensitive">the sensitive data, see TPM 2.0 Part 1 Sensitive Values</param>
        ///<param name = "inPublic">the public template</param>
        ///<param name = "outsideInfo">data that will be included in the creation data for this object to provide permanent, verifiable linkage between this object and some object owner data</param>
        ///<param name = "creationPCR">PCR that will be used in creation data</param>
        void CreatePrimary
        (
            const TPM_HANDLE& primaryHandle,
            const TPMS_SENSITIVE_CREATE& inSensitive,
            const TPMT_PUBLIC& inPublic,
            const std::vector<BYTE>& outsideInfo,
            const std::vector<TPMS_PCR_SELECTION>& creationPCR
        );
        /// <summary>
        /// This command enables and disables use of a hierarchy and its associated NV storage. The command allows phEnable, phEnableNV, shEnable, and ehEnable to be changed when the proper authorization is provided.
        /// </summary>
        ///<param name = "authHandle">TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
        ///<param name = "enable">the enable being modified TPM_RH_ENDORSEMENT, TPM_RH_OWNER, TPM_RH_PLATFORM, or TPM_RH_PLATFORM_NV</param>
        ///<param name = "state">YES if the enable should be SET, NO if the enable should be CLEAR</param>
        void HierarchyControl
        (
            const TPM_HANDLE& authHandle,
            const TPM_HANDLE& enable,
            const BYTE& state
        );
        /// <summary>
        /// This command allows setting of the authorization policy for the lockout (lockoutPolicy), the platform hierarchy (platformPolicy), the storage hierarchy (ownerPolicy), and the endorsement hierarchy (endorsementPolicy).
        /// </summary>
        ///<param name = "authHandle">TPM_RH_LOCKOUT, TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
        ///<param name = "authPolicy">an authorization policy digest; may be the Empty Buffer If hashAlg is TPM_ALG_NULL, then this shall be an Empty Buffer.</param>
        ///<param name = "hashAlg">the hash algorithm to use for the policy If the authPolicy is an Empty Buffer, then this field shall be TPM_ALG_NULL.</param>
        void SetPrimaryPolicy
        (
            const TPM_HANDLE& authHandle,
            const std::vector<BYTE>& authPolicy,
            const TPM_ALG_ID& hashAlg
        );
        /// <summary>
        /// This replaces the current platform primary seed (PPS) with a value from the RNG and sets platformPolicy to the default initialization value (the Empty Buffer).
        /// </summary>
        ///<param name = "authHandle">TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
        void ChangePPS
        (
            const TPM_HANDLE& authHandle
        );
        /// <summary>
        /// This replaces the current endorsement primary seed (EPS) with a value from the RNG and sets the Endorsement hierarchy controls to their default initialization values: ehEnable is SET, endorsementAuth and endorsementPolicy are both set to the Empty Buffer. It will flush any resident objects (transient or persistent) in the Endorsement hierarchy and not allow objects in the hierarchy associated with the previous EPS to be loaded.
        /// </summary>
        ///<param name = "authHandle">TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER</param>
        void ChangeEPS
        (
            const TPM_HANDLE& authHandle
        );
        /// <summary>
        /// This command removes all TPM context associated with a specific Owner.
        /// </summary>
        ///<param name = "authHandle">TPM_RH_LOCKOUT or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER</param>
        void Clear
        (
            const TPM_HANDLE& authHandle
        );
        /// <summary>
        /// TPM2_ClearControl() disables and enables the execution of TPM2_Clear().
        /// </summary>
        ///<param name = "auth">TPM_RH_LOCKOUT or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER</param>
        ///<param name = "disable">YES if the disableOwnerClear flag is to be SET, NO if the flag is to be CLEAR.</param>
        void ClearControl
        (
            const TPM_HANDLE& auth,
            const BYTE& disable
        );
        /// <summary>
        /// This command allows the authorization secret for a hierarchy or lockout to be changed using the current authorization value as the command authorization.
        /// </summary>
        ///<param name = "authHandle">TPM_RH_LOCKOUT, TPM_RH_ENDORSEMENT, TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
        ///<param name = "newAuth">new authorization value</param>
        void HierarchyChangeAuth
        (
            const TPM_HANDLE& authHandle,
            const std::vector<BYTE>& newAuth
        );
        /// <summary>
        /// This command cancels the effect of a TPM lockout due to a number of successive authorization failures. If this command is properly authorized, the lockout counter is set to zero.
        /// </summary>
        ///<param name = "lockHandle">TPM_RH_LOCKOUT Auth Index: 1 Auth Role: USER</param>
        void DictionaryAttackLockReset
        (
            const TPM_HANDLE& lockHandle
        );
        /// <summary>
        /// This command changes the lockout parameters.
        /// </summary>
        ///<param name = "lockHandle">TPM_RH_LOCKOUT Auth Index: 1 Auth Role: USER</param>
        ///<param name = "newMaxTries">count of authorization failures before the lockout is imposed</param>
        ///<param name = "newRecoveryTime">time in seconds before the authorization failure count is automatically decremented A value of zero indicates that DA protection is disabled.</param>
        ///<param name = "lockoutRecovery">time in seconds after a lockoutAuth failure before use of lockoutAuth is allowed A value of zero indicates that a reboot is required.</param>
        void DictionaryAttackParameters
        (
            const TPM_HANDLE& lockHandle,
            const UINT32& newMaxTries,
            const UINT32& newRecoveryTime,
            const UINT32& lockoutRecovery
        );
        /// <summary>
        /// This command is used to determine which commands require assertion of Physical Presence (PP) in addition to platformAuth/platformPolicy.
        /// </summary>
        ///<param name = "auth">TPM_RH_PLATFORM+PP Auth Index: 1 Auth Role: USER + Physical Presence</param>
        ///<param name = "setList">list of commands to be added to those that will require that Physical Presence be asserted</param>
        ///<param name = "clearList">list of commands that will no longer require that Physical Presence be asserted</param>
        void PP_Commands
        (
            const TPM_HANDLE& auth,
            const std::vector<TPM_CC>& setList,
            const std::vector<TPM_CC>& clearList
        );
        /// <summary>
        /// This command allows the platform to change the set of algorithms that are used by the TPM. The algorithmSet setting is a vendor-dependent value.
        /// </summary>
        ///<param name = "authHandle">TPM_RH_PLATFORM Auth Index: 1 Auth Role: USER</param>
        ///<param name = "algorithmSet">a TPM vendor-dependent value indicating the algorithm set selection</param>
        void SetAlgorithmSet
        (
            const TPM_HANDLE& authHandle,
            const UINT32& algorithmSet
        );
        /// <summary>
        /// This command uses platformPolicy and a TPM Vendor Authorization Key to authorize a Field Upgrade Manifest.
        /// </summary>
        ///<param name = "authorization">TPM_RH_PLATFORM+{PP} Auth Index:1 Auth Role: ADMIN</param>
        ///<param name = "keyHandle">handle of a public area that contains the TPM Vendor Authorization Key that will be used to validate manifestSignature Auth Index: None</param>
        ///<param name = "fuDigest">digest of the first block in the field upgrade sequence</param>
        ///<param name = "manifestSignature">signature over fuDigest using the key associated with keyHandle (not optional)(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
        void FieldUpgradeStart
        (
            const TPM_HANDLE& authorization,
            const TPM_HANDLE& keyHandle,
            const std::vector<BYTE>& fuDigest,
            const TPMU_SIGNATURE& manifestSignature
        );
        /// <summary>
        /// This command will take the actual field upgrade image to be installed on the TPM. The exact format of fuData is vendor-specific. This command is only possible following a successful TPM2_FieldUpgradeStart(). If the TPM has not received a properly authorized TPM2_FieldUpgradeStart(), then the TPM shall return TPM_RC_FIELDUPGRADE.
        /// </summary>
        ///<param name = "fuData">field upgrade image data</param>
        void FieldUpgradeData
        (
            const std::vector<BYTE>& fuData
        );
        /// <summary>
        /// This command is used to read a copy of the current firmware installed in the TPM.
        /// </summary>
        ///<param name = "sequenceNumber">the number of previous calls to this command in this sequence set to 0 on the first call</param>
        void FirmwareRead
        (
            const UINT32& sequenceNumber
        );
        /// <summary>
        /// This command saves a session context, object context, or sequence object context outside the TPM.
        /// </summary>
        ///<param name = "saveHandle">handle of the resource to save Auth Index: None</param>
        void ContextSave
        (
            const TPM_HANDLE& saveHandle
        );
        /// <summary>
        /// This command is used to reload a context that has been saved by TPM2_ContextSave().
        /// </summary>
        ///<param name = "context">the context blob</param>
        void ContextLoad
        (
            const TPMS_CONTEXT& context
        );
        /// <summary>
        /// This command causes all context associated with a loaded object, sequence object, or session to be removed from TPM memory.
        /// </summary>
        ///<param name = "flushHandle">the handle of the item to flush NOTE	This is a use of a handle as a parameter.</param>
        void FlushContext
        (
            const TPM_HANDLE& flushHandle
        );
        /// <summary>
        /// This command allows certain Transient Objects to be made persistent or a persistent object to be evicted.
        /// </summary>
        ///<param name = "auth">TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER</param>
        ///<param name = "objectHandle">the handle of a loaded object Auth Index: None</param>
        ///<param name = "persistentHandle">if objectHandle is a transient object handle, then this is the persistent handle for the object if objectHandle is a persistent object handle, then it shall be the same value as persistentHandle</param>
        void EvictControl
        (
            const TPM_HANDLE& auth,
            const TPM_HANDLE& objectHandle,
            const TPM_HANDLE& persistentHandle
        );
        /// <summary>
        /// This command reads the current TPMS_TIME_INFO structure that contains the current setting of Time, Clock, resetCount, and restartCount.
        /// </summary>
        void ReadClock();
        /// <summary>
        /// This command is used to advance the value of the TPMs Clock. The command will fail if newTime is less than the current value of Clock or if the new time is greater than FFFF00000000000016. If both of these checks succeed, Clock is set to newTime. If either of these checks fails, the TPM shall return TPM_RC_VALUE and make no change to Clock.
        /// </summary>
        ///<param name = "auth">TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER</param>
        ///<param name = "newTime">new Clock setting in milliseconds</param>
        void ClockSet
        (
            const TPM_HANDLE& auth,
            const UINT64& newTime
        );
        /// <summary>
        /// This command adjusts the rate of advance of Clock and Time to provide a better approximation to real time.
        /// </summary>
        ///<param name = "auth">TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Handle: 1 Auth Role: USER</param>
        ///<param name = "rateAdjust">Adjustment to current Clock update rate</param>
        void ClockRateAdjust
        (
            const TPM_HANDLE& auth,
            const TPM_CLOCK_ADJUST& rateAdjust
        );
        /// <summary>
        /// This command returns various information regarding the TPM and its current state.
        /// </summary>
        ///<param name = "capability">group selection; determines the format of the response</param>
        ///<param name = "property">further definition of information</param>
        ///<param name = "propertyCount">number of properties of the indicated type to return</param>
        void GetCapability
        (
            const TPM_CAP& capability,
            const UINT32& property,
            const UINT32& propertyCount
        );
        /// <summary>
        /// This command is used to check to see if specific combinations of algorithm parameters are supported.
        /// </summary>
        ///<param name = "parameters">algorithm parameters to be validated(One of TPMS_KEYEDHASH_PARMS, TPMS_SYMCIPHER_PARMS, TPMS_RSA_PARMS, TPMS_ECC_PARMS, TPMS_ASYM_PARMS)</param>
        void TestParms
        (
            const TPMU_PUBLIC_PARMS& parameters
        );
        /// <summary>
        /// This command defines the attributes of an NV Index and causes the TPM to reserve space to hold the data associated with the NV Index. If a definition already exists at the NV Index, the TPM will return TPM_RC_NV_DEFINED.
        /// </summary>
        ///<param name = "authHandle">TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
        ///<param name = "auth">the authorization value</param>
        ///<param name = "publicInfo">the public parameters of the NV area</param>
        void NV_DefineSpace
        (
            const TPM_HANDLE& authHandle,
            const std::vector<BYTE>& auth,
            const TPMS_NV_PUBLIC& publicInfo
        );
        /// <summary>
        /// This command removes an Index from the TPM.
        /// </summary>
        ///<param name = "authHandle">TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
        ///<param name = "nvIndex">the NV Index to remove from NV space Auth Index: None</param>
        void NV_UndefineSpace
        (
            const TPM_HANDLE& authHandle,
            const TPM_HANDLE& nvIndex
        );
        /// <summary>
        /// This command allows removal of a platform-created NV Index that has TPMA_NV_POLICY_DELETE SET.
        /// </summary>
        ///<param name = "nvIndex">Index to be deleted Auth Index: 1 Auth Role: ADMIN</param>
        ///<param name = "platform">TPM_RH_PLATFORM + {PP} Auth Index: 2 Auth Role: USER</param>
        void NV_UndefineSpaceSpecial
        (
            const TPM_HANDLE& nvIndex,
            const TPM_HANDLE& platform
        );
        /// <summary>
        /// This command is used to read the public area and Name of an NV Index. The public area of an Index is not privacy-sensitive and no authorization is required to read this data.
        /// </summary>
        ///<param name = "nvIndex">the NV Index Auth Index: None</param>
        void NV_ReadPublic
        (
            const TPM_HANDLE& nvIndex
        );
        /// <summary>
        /// This command writes a value to an area in NV memory that was previously defined by TPM2_NV_DefineSpace().
        /// </summary>
        ///<param name = "authHandle">handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER</param>
        ///<param name = "nvIndex">the NV Index of the area to write Auth Index: None</param>
        ///<param name = "data">the data to write</param>
        ///<param name = "offset">the offset into the NV Area</param>
        void NV_Write
        (
            const TPM_HANDLE& authHandle,
            const TPM_HANDLE& nvIndex,
            const std::vector<BYTE>& data,
            const UINT16& offset
        );
        /// <summary>
        /// This command is used to increment the value in an NV Index that has TPMA_NV_COUNTER SET. The data value of the NV Index is incremented by one.
        /// </summary>
        ///<param name = "authHandle">handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER</param>
        ///<param name = "nvIndex">the NV Index to increment Auth Index: None</param>
        void NV_Increment
        (
            const TPM_HANDLE& authHandle,
            const TPM_HANDLE& nvIndex
        );
        /// <summary>
        /// This command extends a value to an area in NV memory that was previously defined by TPM2_NV_DefineSpace.
        /// </summary>
        ///<param name = "authHandle">handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER</param>
        ///<param name = "nvIndex">the NV Index to extend Auth Index: None</param>
        ///<param name = "data">the data to extend</param>
        void NV_Extend
        (
            const TPM_HANDLE& authHandle,
            const TPM_HANDLE& nvIndex,
            const std::vector<BYTE>& data
        );
        /// <summary>
        /// This command is used to SET bits in an NV Index that was created as a bit field. Any number of bits from 0 to 64 may be SET. The contents of data are ORed with the current contents of the NV Index starting at offset.
        /// </summary>
        ///<param name = "authHandle">handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER</param>
        ///<param name = "nvIndex">NV Index of the area in which the bit is to be set Auth Index: None</param>
        ///<param name = "bits">the data to OR with the current contents</param>
        void NV_SetBits
        (
            const TPM_HANDLE& authHandle,
            const TPM_HANDLE& nvIndex,
            const UINT64& bits
        );
        /// <summary>
        /// If the TPMA_NV_WRITEDEFINE or TPMA_NV_WRITE_STCLEAR attributes of an NV location are SET, then this command may be used to inhibit further writes of the NV Index.
        /// </summary>
        ///<param name = "authHandle">handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER</param>
        ///<param name = "nvIndex">the NV Index of the area to lock Auth Index: None</param>
        void NV_WriteLock
        (
            const TPM_HANDLE& authHandle,
            const TPM_HANDLE& nvIndex
        );
        /// <summary>
        /// The command will SET TPMA_NV_WRITELOCKED for all indexes that have their TPMA_NV_GLOBALLOCK attribute SET.
        /// </summary>
        ///<param name = "authHandle">TPM_RH_OWNER or TPM_RH_PLATFORM+{PP} Auth Index: 1 Auth Role: USER</param>
        void NV_GlobalWriteLock
        (
            const TPM_HANDLE& authHandle
        );
        /// <summary>
        /// This command reads a value from an area in NV memory previously defined by TPM2_NV_DefineSpace().
        /// </summary>
        ///<param name = "authHandle">the handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER</param>
        ///<param name = "nvIndex">the NV Index to be read Auth Index: None</param>
        ///<param name = "size">number of octets to read</param>
        ///<param name = "offset">octet offset into the area This value shall be less than or equal to the size of the nvIndex data.</param>
        void NV_Read
        (
            const TPM_HANDLE& authHandle,
            const TPM_HANDLE& nvIndex,
            const UINT16& size,
            const UINT16& offset
        );
        /// <summary>
        /// If TPMA_NV_READ_STCLEAR is SET in an Index, then this command may be used to prevent further reads of the NV Index until the next TPM2_Startup (TPM_SU_CLEAR).
        /// </summary>
        ///<param name = "authHandle">the handle indicating the source of the authorization value Auth Index: 1 Auth Role: USER</param>
        ///<param name = "nvIndex">the NV Index to be locked Auth Index: None</param>
        void NV_ReadLock
        (
            const TPM_HANDLE& authHandle,
            const TPM_HANDLE& nvIndex
        );
        /// <summary>
        /// This command allows the authorization secret for an NV Index to be changed.
        /// </summary>
        ///<param name = "nvIndex">handle of the entity Auth Index: 1 Auth Role: ADMIN</param>
        ///<param name = "newAuth">new authorization value</param>
        void NV_ChangeAuth
        (
            const TPM_HANDLE& nvIndex,
            const std::vector<BYTE>& newAuth
        );
        /// <summary>
        /// The purpose of this command is to certify the contents of an NV Index or portion of an NV Index.
        /// </summary>
        ///<param name = "signHandle">handle of the key used to sign the attestation structure Auth Index: 1 Auth Role: USER</param>
        ///<param name = "authHandle">handle indicating the source of the authorization value for the NV Index Auth Index: 2 Auth Role: USER</param>
        ///<param name = "nvIndex">Index for the area to be certified Auth Index: None</param>
        ///<param name = "qualifyingData">user-provided qualifying data</param>
        ///<param name = "inScheme">signing scheme to use if the scheme for signHandle is TPM_ALG_NULL(One of TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_SCHEME_HMAC, TPMS_SCHEME_HASH, TPMS_NULL_SIG_SCHEME)</param>
        ///<param name = "size">number of octets to certify</param>
        ///<param name = "offset">octet offset into the area This value shall be less than or equal to the size of the nvIndex data.</param>
        void NV_Certify
        (
            const TPM_HANDLE& signHandle,
            const TPM_HANDLE& authHandle,
            const TPM_HANDLE& nvIndex,
            const std::vector<BYTE>& qualifyingData,
            const TPMU_SIG_SCHEME& inScheme,
            const UINT16& size,
            const UINT16& offset
        );
        /// <summary>
        /// This is a placeholder to allow testing of the dispatch code.
        /// </summary>
        ///<param name = "inputData">dummy data</param>
        void Vendor_TCG_Test
        (
            const std::vector<BYTE>& inputData
        );
        /// <summary>
        /// TPM2_Startup() is always preceded by _TPM_Init, which is the physical indication that TPM initialization is necessary because of a system-wide reset. TPM2_Startup() is only valid after _TPM_Init. Additional TPM2_Startup() commands are not allowed after it has completed successfully. If a TPM requires TPM2_Startup() and another command is received, or if the TPM receives TPM2_Startup() when it is not required, the TPM shall return TPM_RC_INITIALIZE.
        /// </summary>
        void StartupComplete
        (
        );
        /// <summary>
        /// This command is used to prepare the TPM for a power cycle. The shutdownType parameter indicates how the subsequent TPM2_Startup() will be processed.
        /// </summary>
        void ShutdownComplete
        (
        );
        /// <summary>
        /// This command causes the TPM to perform a test of its capabilities. If the fullTest is YES, the TPM will test all functions. If fullTest = NO, the TPM will only test those functions that have not previously been tested.
        /// </summary>
        void SelfTestComplete
        (
        );
        /// <summary>
        /// This command causes the TPM to perform a test of the selected algorithms.
        /// </summary>
        ///<param name = "toDoListCount">number of algorithms in the algorithms list; may be 0</param>
        ///<param name = "toDoList">list of algorithms that need testing</param>
        std::vector<TPM_ALG_ID> IncrementalSelfTestComplete
        (
        );
        /// <summary>
        /// This command returns manufacturer-specific information regarding the results of a self-test and an indication of the test status.
        /// </summary>
        ///<param name = "outDataSize">size of the buffer</param>
        ///<param name = "outData">test result data contains manufacturer-specific information</param>
        ///<param name = "testResult"></param>
        GetTestResultResponse GetTestResultComplete();
        /// <summary>
        /// This command is used to start an authorization session using alternative methods of establishing the session key (sessionKey). The session key is then used to derive values used for authorization and for encrypting parameters.
        /// </summary>
        ///<param name = "sessionHandle">handle for the newly created session</param>
        ///<param name = "nonceTPMSize">size in octets of the buffer field; may be 0</param>
        ///<param name = "nonceTPM">the initial nonce from the TPM, used in the computation of the sessionKey</param>
        StartAuthSessionResponse StartAuthSessionComplete
        (
        );
        /// <summary>
        /// This command allows a policy authorization session to be returned to its initial state. This command is used after the TPM returns TPM_RC_PCR_CHANGED. That response code indicates that a policy will fail because the PCR have changed after TPM2_PolicyPCR() was executed. Restarting the session allows the authorizations to be replayed because the session restarts with the same nonceTPM. If the PCR are valid for the policy, the policy may then succeed.
        /// </summary>
        void PolicyRestartComplete
        (
        );
        /// <summary>
        /// This command is used to create an object that can be loaded into a TPM using TPM2_Load(). If the command completes successfully, the TPM will create the new object and return the objects creation data (creationData), its public area (outPublic), and its encrypted sensitive area (outPrivate). Preservation of the returned data is the responsibility of the caller. The object will need to be loaded (TPM2_Load()) before it may be used.
        /// </summary>
        ///<param name = "outPrivate">the private portion of the object</param>
        ///<param name = "outPublicSize">size of publicArea NOTE	The = will force the TPM to try to unmarshal a TPMT_PUBLIC and check that the unmarshaled size matches the value of size. If all the required fields of a TPMT_PUBLIC are not present, the TPM will return an error (generally TPM_RC_SIZE) when attempting to unmarshal the TPMT_PUBLIC.</param>
        ///<param name = "outPublic">the public portion of the created object</param>
        ///<param name = "creationDataSize">size of the creation data</param>
        ///<param name = "creationData">contains a TPMS_CREATION_DATA</param>
        ///<param name = "creationHashSize">size in octets of the buffer field; may be 0</param>
        ///<param name = "creationHash">digest of creationData using nameAlg of outPublic</param>
        ///<param name = "creationTicket">ticket used by TPM2_CertifyCreation() to validate that the creation data was produced by the TPM</param>
        CreateResponse CreateComplete
        (
        );
        /// <summary>
        /// This command is used to load objects into the TPM. This command is used when both a TPM2B_PUBLIC and TPM2B_PRIVATE are to be loaded. If only a TPM2B_PUBLIC is to be loaded, the TPM2_LoadExternal command is used.
        /// </summary>
        ///<param name = "objectHandle">handle of type TPM_HT_TRANSIENT for the loaded object</param>
        ///<param name = "nameSize">size of the Name structure</param>
        ///<param name = "name">Name of the loaded object</param>
        LoadResponse LoadComplete
        (
        );
        /// <summary>
        /// This command is used to load an object that is not a Protected Object into the TPM. The command allows loading of a public area or both a public and sensitive area.
        /// </summary>
        ///<param name = "objectHandle">handle of type TPM_HT_TRANSIENT for the loaded object</param>
        ///<param name = "nameSize">size of the Name structure</param>
        ///<param name = "name">name of the loaded object</param>
        LoadExternalResponse LoadExternalComplete
        (
        );
        /// <summary>
        /// This command allows access to the public area of a loaded object.
        /// </summary>
        ///<param name = "outPublicSize">size of publicArea NOTE	The = will force the TPM to try to unmarshal a TPMT_PUBLIC and check that the unmarshaled size matches the value of size. If all the required fields of a TPMT_PUBLIC are not present, the TPM will return an error (generally TPM_RC_SIZE) when attempting to unmarshal the TPMT_PUBLIC.</param>
        ///<param name = "outPublic">structure containing the public area of an object</param>
        ///<param name = "nameSize">size of the Name structure</param>
        ///<param name = "name">name of the object</param>
        ///<param name = "qualifiedNameSize">size of the Name structure</param>
        ///<param name = "qualifiedName">the Qualified Name of the object</param>
        ReadPublicResponse ReadPublicComplete
        (
        );
        /// <summary>
        /// This command enables the association of a credential with an object in a way that ensures that the TPM has validated the parameters of the credentialed object.
        /// </summary>
        ///<param name = "certInfoSize">size in octets of the buffer field; may be 0</param>
        ///<param name = "certInfo">the decrypted certificate information the data should be no larger than the size of the digest of the nameAlg associated with keyHandle</param>
        std::vector<BYTE> ActivateCredentialComplete
        (
        );
        /// <summary>
        /// This command allows the TPM to perform the actions required of a Certificate Authority (CA) in creating a TPM2B_ID_OBJECT containing an activation credential.
        /// </summary>
        ///<param name = "credentialBlobSize">size of the credential structure</param>
        ///<param name = "credentialBlob">the credential</param>
        ///<param name = "secretSize">size of the secret value</param>
        ///<param name = "secret">handle algorithm-dependent data that wraps the key that encrypts credentialBlob</param>
        MakeCredentialResponse MakeCredentialComplete
        (
        );
        /// <summary>
        /// This command returns the data in a loaded Sealed Data Object.
        /// </summary>
        ///<param name = "outDataSize"></param>
        ///<param name = "outData">unsealed data Size of outData is limited to be no more than 128 octets.</param>
        std::vector<BYTE> UnsealComplete
        (
        );
        /// <summary>
        /// This command is used to change the authorization secret for a TPM-resident object.
        /// </summary>
        ///<param name = "outPrivate">private area containing the new authorization value</param>
        TPM2B_PRIVATE ObjectChangeAuthComplete
        (
        );
        /// <summary>
        /// This command duplicates a loaded object so that it may be used in a different hierarchy. The new parent key for the duplicate may be on the same or different TPM or TPM_RH_NULL. Only the public area of newParentHandle is required to be loaded.
        /// </summary>
        ///<param name = "encryptionKeyOutSize">size in octets of the buffer field; may be 0</param>
        ///<param name = "encryptionKeyOut">If the caller provided an encryption key or if symmetricAlg was TPM_ALG_NULL, then this will be the Empty Buffer; otherwise, it shall contain the TPM-generated, symmetric encryption key for the inner wrapper.</param>
        ///<param name = "duplicate">private area that may be encrypted by encryptionKeyIn; and may be doubly encrypted</param>
        ///<param name = "outSymSeedSize">size of the secret value</param>
        ///<param name = "outSymSeed">seed protected by the asymmetric algorithms of new parent (NP)</param>
        DuplicateResponse DuplicateComplete
        (
        );
        /// <summary>
        /// This command allows the TPM to serve in the role as a Duplication Authority. If proper authorization for use of the oldParent is provided, then an HMAC key and a symmetric key are recovered from inSymSeed and used to integrity check and decrypt inDuplicate. A new protection seed value is generated according to the methods appropriate for newParent and the blob is re-encrypted and a new integrity value is computed. The re-encrypted blob is returned in outDuplicate and the symmetric key returned in outSymKey.
        /// </summary>
        ///<param name = "outDuplicate">an object encrypted using symmetric key derived from outSymSeed</param>
        ///<param name = "outSymSeedSize">size of the secret value</param>
        ///<param name = "outSymSeed">seed for a symmetric key protected by newParent asymmetric key</param>
        RewrapResponse RewrapComplete
        (
        );
        /// <summary>
        /// This command allows an object to be encrypted using the symmetric encryption values of a Storage Key. After encryption, the object may be loaded and used in the new hierarchy. The imported object (duplicate) may be singly encrypted, multiply encrypted, or unencrypted.
        /// </summary>
        ///<param name = "outPrivate">the sensitive area encrypted with the symmetric key of parentHandle</param>
        TPM2B_PRIVATE ImportComplete
        (
        );
        /// <summary>
        /// This command performs RSA encryption using the indicated padding scheme according to IETF RFC 3447. If the scheme of keyHandle is TPM_ALG_NULL, then the caller may use inScheme to specify the padding scheme. If scheme of keyHandle is not TPM_ALG_NULL, then inScheme shall either be TPM_ALG_NULL or be the same as scheme (TPM_RC_SCHEME).
        /// </summary>
        ///<param name = "outDataSize">size of the buffer The value of zero is only valid for create.</param>
        ///<param name = "outData">encrypted output</param>
        std::vector<BYTE> RSA_EncryptComplete
        (
        );
        /// <summary>
        /// This command performs RSA decryption using the indicated padding scheme according to IETF RFC 3447 ((PKCS#1).
        /// </summary>
        ///<param name = "messageSize">size of the buffer The value of zero is only valid for create.</param>
        ///<param name = "message">decrypted output</param>
        std::vector<BYTE> RSA_DecryptComplete
        (
        );
        /// <summary>
        /// This command uses the TPM to generate an ephemeral key pair (de, Qe where Qe  [de]G). It uses the private ephemeral key and a loaded public key (QS) to compute the shared secret value (P  [hde]QS).
        /// </summary>
        ///<param name = "zPointSize">size of the remainder of this structure</param>
        ///<param name = "zPoint">results of P  h[de]Qs</param>
        ///<param name = "pubPointSize">size of the remainder of this structure</param>
        ///<param name = "pubPoint">generated ephemeral public point (Qe)</param>
        ECDH_KeyGenResponse ECDH_KeyGenComplete
        (
        );
        /// <summary>
        /// This command uses the TPM to recover the Z value from a public point (QB) and a private key (ds). It will perform the multiplication of the provided inPoint (QB) with the private key (ds) and return the coordinates of the resultant point (Z = (xZ , yZ)  [hds]QB; where h is the cofactor of the curve).
        /// </summary>
        ///<param name = "outPointSize">size of the remainder of this structure</param>
        ///<param name = "outPoint">X and Y coordinates of the product of the multiplication Z = (xZ , yZ)  [hdS]QB</param>
        TPMS_ECC_POINT ECDH_ZGenComplete
        (
        );
        /// <summary>
        /// This command returns the parameters of an ECC curve identified by its TCG-assigned curveID.
        /// </summary>
        ///<param name = "parameters">ECC parameters for the selected curve</param>
        TPMS_ALGORITHM_DETAIL_ECC ECC_ParametersComplete
        (
        );
        /// <summary>
        /// This command supports two-phase key exchange protocols. The command is used in combination with TPM2_EC_Ephemeral(). TPM2_EC_Ephemeral() generates an ephemeral key and returns the public point of that ephemeral key along with a numeric value that allows the TPM to regenerate the associated private key.
        /// </summary>
        ///<param name = "outZ1Size">size of the remainder of this structure</param>
        ///<param name = "outZ1">X and Y coordinates of the computed value (scheme dependent)</param>
        ///<param name = "outZ2Size">size of the remainder of this structure</param>
        ///<param name = "outZ2">X and Y coordinates of the second computed value (scheme dependent)</param>
        ZGen_2PhaseResponse ZGen_2PhaseComplete
        (
        );
        /// <summary>
        /// This command performs symmetric encryption or decryption.
        /// </summary>
        ///<param name = "outDataSize">size of the buffer</param>
        ///<param name = "outData">encrypted or decrypted output</param>
        ///<param name = "ivOutSize">size of the timeout value This value is fixed for a TPM implementation.</param>
        ///<param name = "ivOut">chaining value to use for IV in next round</param>
        EncryptDecryptResponse EncryptDecryptComplete
        (
        );
        /// <summary>
        /// This command performs a hash operation on a data buffer and returns the results.
        /// </summary>
        ///<param name = "outHashSize">size in octets of the buffer field; may be 0</param>
        ///<param name = "outHash">results</param>
        ///<param name = "validation">ticket indicating that the sequence of octets used to compute outDigest did not start with TPM_GENERATED_VALUE will be a NULL ticket if the digest may not be signed with a restricted key</param>
        HashResponse HashComplete
        (
        );
        /// <summary>
        /// This command performs an HMAC on the supplied data using the indicated hash algorithm.
        /// </summary>
        ///<param name = "outHMACSize">size in octets of the buffer field; may be 0</param>
        ///<param name = "outHMAC">the returned HMAC in a sized buffer</param>
        std::vector<BYTE> HMACComplete
        (
        );
        /// <summary>
        /// This command returns the next bytesRequested octets from the random number generator (RNG).
        /// </summary>
        ///<param name = "randomBytesSize">size in octets of the buffer field; may be 0</param>
        ///<param name = "randomBytes">the random octets</param>
        std::vector<BYTE> GetRandomComplete
        (
        );
        /// <summary>
        /// This command is used to add "additional information" to the RNG state.
        /// </summary>
        void StirRandomComplete
        (
        );
        /// <summary>
        /// This command starts an HMAC sequence. The TPM will create and initialize an HMAC sequence structure, assign a handle to the sequence, and set the authValue of the sequence object to the value in auth.
        /// </summary>
        ///<param name = "sequenceHandle">a handle to reference the sequence</param>
        TPM_HANDLE HMAC_StartComplete
        (
        );
        /// <summary>
        /// This command starts a hash or an Event Sequence. If hashAlg is an implemented hash, then a hash sequence is started. If hashAlg is TPM_ALG_NULL, then an Event Sequence is started. If hashAlg is neither an implemented algorithm nor TPM_ALG_NULL, then the TPM shall return TPM_RC_HASH.
        /// </summary>
        ///<param name = "sequenceHandle">a handle to reference the sequence</param>
        TPM_HANDLE HashSequenceStartComplete
        (
        );
        /// <summary>
        /// This command is used to add data to a hash or HMAC sequence. The amount of data in buffer may be any size up to the limits of the TPM.
        /// </summary>
        void SequenceUpdateComplete
        (
        );
        /// <summary>
        /// This command adds the last part of data, if any, to a hash/HMAC sequence and returns the result.
        /// </summary>
        ///<param name = "resultSize">size in octets of the buffer field; may be 0</param>
        ///<param name = "result">the returned HMAC or digest in a sized buffer</param>
        ///<param name = "validation">ticket indicating that the sequence of octets used to compute outDigest did not start with TPM_GENERATED_VALUE This is a NULL Ticket when the sequence is HMAC.</param>
        SequenceCompleteResponse SequenceCompleteComplete
        (
        );
        /// <summary>
        /// This command adds the last part of data, if any, to an Event Sequence and returns the result in a digest list. If pcrHandle references a PCR and not TPM_RH_NULL, then the returned digest list is processed in the same manner as the digest list input parameter to TPM2_PCR_Extend() with the pcrHandle in each bank extended with the associated digest value.
        /// </summary>
        ///<param name = "resultsCount">number of digests in the list</param>
        ///<param name = "results">list of digests computed for the PCR</param>
        std::vector<TPMT_HA> EventSequenceCompleteComplete
        (
        );
        /// <summary>
        /// The purpose of this command is to prove that an object with a specific Name is loaded in the TPM. By certifying that the object is loaded, the TPM warrants that a public area with a given Name is self-consistent and associated with a valid sensitive area. If a relying party has a public area that has the same Name as a Name certified with this command, then the values in that public area are correct.
        /// </summary>
        ///<param name = "certifyInfoSize">size of the attestationData structure</param>
        ///<param name = "certifyInfo">the structure that was signed</param>
        ///<param name = "signatureSigAlg">selector of the algorithm used to construct the signature</param>
        ///<param name = "signature">the asymmetric signature over certifyInfo using the key referenced by signHandle(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
        CertifyResponse CertifyComplete
        (
        );
        /// <summary>
        /// This command is used to prove the association between an object and its creation data. The TPM will validate that the ticket was produced by the TPM and that the ticket validates the association between a loaded public area and the provided hash of the creation data (creationHash).
        /// </summary>
        ///<param name = "certifyInfoSize">size of the attestationData structure</param>
        ///<param name = "certifyInfo">the structure that was signed</param>
        ///<param name = "signatureSigAlg">selector of the algorithm used to construct the signature</param>
        ///<param name = "signature">the signature over certifyInfo(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
        CertifyCreationResponse CertifyCreationComplete
        (
        );
        /// <summary>
        /// This command is used to quote PCR values.
        /// </summary>
        ///<param name = "quotedSize">size of the attestationData structure</param>
        ///<param name = "quoted">the quoted information</param>
        ///<param name = "signatureSigAlg">selector of the algorithm used to construct the signature</param>
        ///<param name = "signature">the signature over quoted(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
        QuoteResponse QuoteComplete
        (
        );
        /// <summary>
        /// This command returns a digital signature of the audit session digest.
        /// </summary>
        ///<param name = "auditInfoSize">size of the attestationData structure</param>
        ///<param name = "auditInfo">the audit information that was signed</param>
        ///<param name = "signatureSigAlg">selector of the algorithm used to construct the signature</param>
        ///<param name = "signature">the signature over auditInfo(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
        GetSessionAuditDigestResponse GetSessionAuditDigestComplete
        (
        );
        /// <summary>
        /// This command returns the current value of the command audit digest, a digest of the commands being audited, and the audit hash algorithm. These values are placed in an attestation structure and signed with the key referenced by signHandle.
        /// </summary>
        ///<param name = "auditInfoSize">size of the attestationData structure</param>
        ///<param name = "auditInfo">the auditInfo that was signed</param>
        ///<param name = "signatureSigAlg">selector of the algorithm used to construct the signature</param>
        ///<param name = "signature">the signature over auditInfo(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
        GetCommandAuditDigestResponse GetCommandAuditDigestComplete
        (
        );
        /// <summary>
        /// This command returns the current values of Time and Clock.
        /// </summary>
        ///<param name = "timeInfoSize">size of the attestationData structure</param>
        ///<param name = "timeInfo">standard TPM-generated attestation block</param>
        ///<param name = "signatureSigAlg">selector of the algorithm used to construct the signature</param>
        ///<param name = "signature">the signature over timeInfo(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
        GetTimeResponse GetTimeComplete
        (
        );
        /// <summary>
        /// TPM2_Commit() performs the first part of an ECC anonymous signing operation. The TPM will perform the point multiplications on the provided points and return intermediate signing values. The signHandle parameter shall refer to an ECC key with the sign attribute (TPM_RC_ATTRIBUTES) and the signing scheme must be anonymous (TPM_RC_SCHEME). Currently, TPM_ALG_ECDAA is the only defined anonymous scheme.
        /// </summary>
        ///<param name = "KSize">size of the remainder of this structure</param>
        ///<param name = "K">ECC point K  [ds](x2, y2)</param>
        ///<param name = "LSize">size of the remainder of this structure</param>
        ///<param name = "L">ECC point L  [r](x2, y2)</param>
        ///<param name = "ESize">size of the remainder of this structure</param>
        ///<param name = "E">ECC point E  [r]P1</param>
        ///<param name = "counter">least-significant 16 bits of commitCount</param>
        CommitResponse CommitComplete
        (
        );
        /// <summary>
        /// TPM2_EC_Ephemeral() creates an ephemeral key for use in a two-phase key exchange protocol.
        /// </summary>
        ///<param name = "QSize">size of the remainder of this structure</param>
        ///<param name = "Q">ephemeral public key Q  [r]G</param>
        ///<param name = "counter">least-significant 16 bits of commitCount</param>
        EC_EphemeralResponse EC_EphemeralComplete
        (
        );
        /// <summary>
        /// This command uses loaded keys to validate a signature on a message with the message digest passed to the TPM.
        /// </summary>
        ///<param name = "validation"></param>
        VerifySignatureResponse VerifySignatureComplete
        (
        );
        /// <summary>
        /// This command causes the TPM to sign an externally provided hash with the specified symmetric or asymmetric signing key.
        /// </summary>
        ///<param name = "signatureSigAlg">selector of the algorithm used to construct the signature</param>
        ///<param name = "signature">the signature(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
        SignResponse SignComplete
        (
        );
        /// <summary>
        /// This command may be used by the Privacy Administrator or platform to change the audit status of a command or to set the hash algorithm used for the audit digest, but not both at the same time.
        /// </summary>
        void SetCommandCodeAuditStatusComplete
        (
        );
        /// <summary>
        /// This command is used to cause an update to the indicated PCR. The digests parameter contains one or more tagged digest values identified by an algorithm ID. For each digest, the PCR associated with pcrHandle is Extended into the bank identified by the tag (hashAlg).
        /// </summary>
        void PCR_ExtendComplete
        (
        );
        /// <summary>
        /// This command is used to cause an update to the indicated PCR.
        /// </summary>
        ///<param name = "digestsCount">number of digests in the list</param>
        ///<param name = "digests"></param>
        std::vector<TPMT_HA> PCR_EventComplete
        (
        );
        /// <summary>
        /// This command returns the values of all PCR specified in pcrSelectionIn.
        /// </summary>
        ///<param name = "pcrUpdateCounter">the current value of the PCR update counter</param>
        ///<param name = "pcrSelectionOutCount">number of selection structures A value of zero is allowed.</param>
        ///<param name = "pcrSelectionOut">the PCR in the returned list</param>
        ///<param name = "pcrValuesCount">number of digests in the list, minimum is two for TPM2_PolicyOR().</param>
        ///<param name = "pcrValues">the contents of the PCR indicated in pcrSelect as tagged digests</param>
        PCR_ReadResponse PCR_ReadComplete
        (
        );
        /// <summary>
        /// This command is used to set the desired PCR allocation of PCR and algorithms. This command requires Platform Authorization.
        /// </summary>
        ///<param name = "allocationSuccess">YES if the allocation succeeded</param>
        ///<param name = "maxPCR">maximum number of PCR that may be in a bank</param>
        ///<param name = "sizeNeeded">number of octets required to satisfy the request</param>
        ///<param name = "sizeAvailable">Number of octets available. Computed before the allocation.</param>
        PCR_AllocateResponse PCR_AllocateComplete
        (
        );
        /// <summary>
        /// This command is used to associate a policy with a PCR or group of PCR. The policy determines the conditions under which a PCR may be extended or reset.
        /// </summary>
        void PCR_SetAuthPolicyComplete
        (
        );
        /// <summary>
        /// This command changes the authValue of a PCR or group of PCR.
        /// </summary>
        void PCR_SetAuthValueComplete
        (
        );
        /// <summary>
        /// If the attribute of a PCR allows the PCR to be reset and proper authorization is provided, then this command may be used to set the PCR to zero. The attributes of the PCR may restrict the locality that can perform the reset operation.
        /// </summary>
        void PCR_ResetComplete
        (
        );
        /// <summary>
        /// This command includes a signed authorization in a policy. The command ties the policy to a signing key by including the Name of the signing key in the policyDigest
        /// </summary>
        ///<param name = "timeoutSize">size of the timeout value This value is fixed for a TPM implementation.</param>
        ///<param name = "timeout">implementation-specific time value, used to indicate to the TPM when the ticket expires NOTE	If policyTicket is a NULL Ticket, then this shall be the Empty Buffer.</param>
        ///<param name = "policyTicket">produced if the command succeeds and expiration in the command was non-zero; this ticket will use the TPMT_ST_AUTH_SIGNED structure tag. See 23.2.5</param>
        PolicySignedResponse PolicySignedComplete
        (
        );
        /// <summary>
        /// This command includes a secret-based authorization to a policy. The caller proves knowledge of the secret value using an authorization session using the authValue associated with authHandle. A password session, an HMAC session, or a policy session containing TPM2_PolicyAuthValue() or TPM2_PolicyPassword() will satisfy this requirement.
        /// </summary>
        ///<param name = "timeoutSize">size of the timeout value This value is fixed for a TPM implementation.</param>
        ///<param name = "timeout">implementation-specific time value used to indicate to the TPM when the ticket expires; this ticket will use the TPMT_ST_AUTH_SECRET structure tag</param>
        ///<param name = "policyTicket">produced if the command succeeds and expiration in the command was non-zero. See 23.2.5</param>
        PolicySecretResponse PolicySecretComplete
        (
        );
        /// <summary>
        /// This command is similar to TPM2_PolicySigned() except that it takes a ticket instead of a signed authorization. The ticket represents a validated authorization that had an expiration time associated with it.
        /// </summary>
        void PolicyTicketComplete
        (
        );
        /// <summary>
        /// This command allows options in authorizations without requiring that the TPM evaluate all of the options. If a policy may be satisfied by different sets of conditions, the TPM need only evaluate one set that satisfies the policy. This command will indicate that one of the required sets of conditions has been satisfied.
        /// </summary>
        void PolicyORComplete
        (
        );
        /// <summary>
        /// This command is used to cause conditional gating of a policy based on PCR. This command together with TPM2_PolicyOR() allows one group of authorizations to occur when PCR are in one state and a different set of authorizations when the PCR are in a different state. If this command is used for a trial policySession, policySessionpolicyDigest will be updated using the values from the command rather than the values from digest of the TPM PCR.
        /// </summary>
        void PolicyPCRComplete
        (
        );
        /// <summary>
        /// This command indicates that the authorization will be limited to a specific locality.
        /// </summary>
        void PolicyLocalityComplete
        (
        );
        /// <summary>
        /// This command is used to cause conditional gating of a policy based on the contents of an NV Index.
        /// </summary>
        void PolicyNVComplete
        (
        );
        /// <summary>
        /// This command is used to cause conditional gating of a policy based on the contents of the TPMS_TIME_INFO structure.
        /// </summary>
        void PolicyCounterTimerComplete
        (
        );
        /// <summary>
        /// This command indicates that the authorization will be limited to a specific command code.
        /// </summary>
        void PolicyCommandCodeComplete
        (
        );
        /// <summary>
        /// This command indicates that physical presence will need to be asserted at the time the authorization is performed.
        /// </summary>
        void PolicyPhysicalPresenceComplete
        (
        );
        /// <summary>
        /// This command is used to allow a policy to be bound to a specific command and command parameters.
        /// </summary>
        void PolicyCpHashComplete
        (
        );
        /// <summary>
        /// This command allows a policy to be bound to a specific set of TPM entities without being bound to the parameters of the command. This is most useful for commands such as TPM2_Duplicate() and for TPM2_PCR_Event() when the referenced PCR requires a policy.
        /// </summary>
        void PolicyNameHashComplete
        (
        );
        /// <summary>
        /// This command allows qualification of duplication to allow duplication to a selected new parent.
        /// </summary>
        void PolicyDuplicationSelectComplete
        (
        );
        /// <summary>
        /// This command allows policies to change. If a policy were static, then it would be difficult to add users to a policy. This command lets a policy authority sign a new policy so that it may be used in an existing policy.
        /// </summary>
        void PolicyAuthorizeComplete
        (
        );
        /// <summary>
        /// This command allows a policy to be bound to the authorization value of the authorized entity.
        /// </summary>
        void PolicyAuthValueComplete
        (
        );
        /// <summary>
        /// This command allows a policy to be bound to the authorization value of the authorized object.
        /// </summary>
        void PolicyPasswordComplete
        (
        );
        /// <summary>
        /// This command returns the current policyDigest of the session. This command allows the TPM to be used to perform the actions required to pre-compute the authPolicy for an object.
        /// </summary>
        ///<param name = "policyDigestSize">size in octets of the buffer field; may be 0</param>
        ///<param name = "policyDigest">the current value of the policySessionpolicyDigest</param>
        std::vector<BYTE> PolicyGetDigestComplete
        (
        );
        /// <summary>
        /// This command allows a policy to be bound to the TPMA_NV_WRITTEN attributes. This is a deferred assertion. Values are stored in the policy session context and checked when the policy is used for authorization.
        /// </summary>
        void PolicyNvWrittenComplete
        (
        );
        /// <summary>
        /// This command allows creation of an authorization policy that will only allow creation of a child object with the correct properties.
        /// </summary>
        void PolicyTemplateComplete
        (
        );
        /// <summary>
        /// This command is used to create a Primary Object under one of the Primary Seeds or a Temporary Object under TPM_RH_NULL. The command uses a TPM2B_PUBLIC as a template for the object to be created. The command will create and load a Primary Object. The sensitive area is not returned.
        /// </summary>
        ///<param name = "objectHandle">handle of type TPM_HT_TRANSIENT for created Primary Object</param>
        ///<param name = "outPublicSize">size of publicArea NOTE	The = will force the TPM to try to unmarshal a TPMT_PUBLIC and check that the unmarshaled size matches the value of size. If all the required fields of a TPMT_PUBLIC are not present, the TPM will return an error (generally TPM_RC_SIZE) when attempting to unmarshal the TPMT_PUBLIC.</param>
        ///<param name = "outPublic">the public portion of the created object</param>
        ///<param name = "creationDataSize">size of the creation data</param>
        ///<param name = "creationData">contains a TPMT_CREATION_DATA</param>
        ///<param name = "creationHashSize">size in octets of the buffer field; may be 0</param>
        ///<param name = "creationHash">digest of creationData using nameAlg of outPublic</param>
        ///<param name = "creationTicket">ticket used by TPM2_CertifyCreation() to validate that the creation data was produced by the TPM</param>
        ///<param name = "nameSize">size of the Name structure</param>
        ///<param name = "name">the name of the created object</param>
        CreatePrimaryResponse CreatePrimaryComplete
        (
        );
        /// <summary>
        /// This command enables and disables use of a hierarchy and its associated NV storage. The command allows phEnable, phEnableNV, shEnable, and ehEnable to be changed when the proper authorization is provided.
        /// </summary>
        void HierarchyControlComplete
        (
        );
        /// <summary>
        /// This command allows setting of the authorization policy for the lockout (lockoutPolicy), the platform hierarchy (platformPolicy), the storage hierarchy (ownerPolicy), and the endorsement hierarchy (endorsementPolicy).
        /// </summary>
        void SetPrimaryPolicyComplete
        (
        );
        /// <summary>
        /// This replaces the current platform primary seed (PPS) with a value from the RNG and sets platformPolicy to the default initialization value (the Empty Buffer).
        /// </summary>
        void ChangePPSComplete
        (
        );
        /// <summary>
        /// This replaces the current endorsement primary seed (EPS) with a value from the RNG and sets the Endorsement hierarchy controls to their default initialization values: ehEnable is SET, endorsementAuth and endorsementPolicy are both set to the Empty Buffer. It will flush any resident objects (transient or persistent) in the Endorsement hierarchy and not allow objects in the hierarchy associated with the previous EPS to be loaded.
        /// </summary>
        void ChangeEPSComplete
        (
        );
        /// <summary>
        /// This command removes all TPM context associated with a specific Owner.
        /// </summary>
        void ClearComplete
        (
        );
        /// <summary>
        /// TPM2_ClearControl() disables and enables the execution of TPM2_Clear().
        /// </summary>
        void ClearControlComplete
        (
        );
        /// <summary>
        /// This command allows the authorization secret for a hierarchy or lockout to be changed using the current authorization value as the command authorization.
        /// </summary>
        void HierarchyChangeAuthComplete
        (
        );
        /// <summary>
        /// This command cancels the effect of a TPM lockout due to a number of successive authorization failures. If this command is properly authorized, the lockout counter is set to zero.
        /// </summary>
        void DictionaryAttackLockResetComplete
        (
        );
        /// <summary>
        /// This command changes the lockout parameters.
        /// </summary>
        void DictionaryAttackParametersComplete
        (
        );
        /// <summary>
        /// This command is used to determine which commands require assertion of Physical Presence (PP) in addition to platformAuth/platformPolicy.
        /// </summary>
        void PP_CommandsComplete
        (
        );
        /// <summary>
        /// This command allows the platform to change the set of algorithms that are used by the TPM. The algorithmSet setting is a vendor-dependent value.
        /// </summary>
        void SetAlgorithmSetComplete
        (
        );
        /// <summary>
        /// This command uses platformPolicy and a TPM Vendor Authorization Key to authorize a Field Upgrade Manifest.
        /// </summary>
        void FieldUpgradeStartComplete
        (
        );
        /// <summary>
        /// This command will take the actual field upgrade image to be installed on the TPM. The exact format of fuData is vendor-specific. This command is only possible following a successful TPM2_FieldUpgradeStart(). If the TPM has not received a properly authorized TPM2_FieldUpgradeStart(), then the TPM shall return TPM_RC_FIELDUPGRADE.
        /// </summary>
        ///<param name = "nextDigest">tagged digest of the next block TPM_ALG_NULL if field update is complete</param>
        ///<param name = "firstDigest">tagged digest of the first block of the sequence</param>
        FieldUpgradeDataResponse FieldUpgradeDataComplete
        (
        );
        /// <summary>
        /// This command is used to read a copy of the current firmware installed in the TPM.
        /// </summary>
        ///<param name = "fuDataSize">size of the buffer</param>
        ///<param name = "fuData">field upgrade image data</param>
        std::vector<BYTE> FirmwareReadComplete
        (
        );
        /// <summary>
        /// This command saves a session context, object context, or sequence object context outside the TPM.
        /// </summary>
        ///<param name = "context"></param>
        TPMS_CONTEXT ContextSaveComplete
        (
        );
        /// <summary>
        /// This command is used to reload a context that has been saved by TPM2_ContextSave().
        /// </summary>
        ///<param name = "loadedHandle">the handle assigned to the resource after it has been successfully loaded</param>
        TPM_HANDLE ContextLoadComplete
        (
        );
        /// <summary>
        /// This command causes all context associated with a loaded object, sequence object, or session to be removed from TPM memory.
        /// </summary>
        void FlushContextComplete
        (
        );
        /// <summary>
        /// This command allows certain Transient Objects to be made persistent or a persistent object to be evicted.
        /// </summary>
        void EvictControlComplete
        (
        );
        /// <summary>
        /// This command reads the current TPMS_TIME_INFO structure that contains the current setting of Time, Clock, resetCount, and restartCount.
        /// </summary>
        ///<param name = "currentTime"></param>
        TPMS_TIME_INFO ReadClockComplete();
        /// <summary>
        /// This command is used to advance the value of the TPMs Clock. The command will fail if newTime is less than the current value of Clock or if the new time is greater than FFFF00000000000016. If both of these checks succeed, Clock is set to newTime. If either of these checks fails, the TPM shall return TPM_RC_VALUE and make no change to Clock.
        /// </summary>
        void ClockSetComplete
        (
        );
        /// <summary>
        /// This command adjusts the rate of advance of Clock and Time to provide a better approximation to real time.
        /// </summary>
        void ClockRateAdjustComplete
        (
        );
        /// <summary>
        /// This command returns various information regarding the TPM and its current state.
        /// </summary>
        ///<param name = "moreData">flag to indicate if there are more values of this type</param>
        ///<param name = "capabilityDataCapability">the capability</param>
        ///<param name = "capabilityData">the capability data(One of TPML_ALG_PROPERTY, TPML_HANDLE, TPML_CCA, TPML_CC, TPML_CC, TPML_PCR_SELECTION, TPML_TAGGED_TPM_PROPERTY, TPML_TAGGED_PCR_PROPERTY, TPML_ECC_CURVE)</param>
        GetCapabilityResponse GetCapabilityComplete
        (
        );
        /// <summary>
        /// This command is used to check to see if specific combinations of algorithm parameters are supported.
        /// </summary>
        void TestParmsComplete
        (
        );
        /// <summary>
        /// This command defines the attributes of an NV Index and causes the TPM to reserve space to hold the data associated with the NV Index. If a definition already exists at the NV Index, the TPM will return TPM_RC_NV_DEFINED.
        /// </summary>
        void NV_DefineSpaceComplete
        (
        );
        /// <summary>
        /// This command removes an Index from the TPM.
        /// </summary>
        void NV_UndefineSpaceComplete
        (
        );
        /// <summary>
        /// This command allows removal of a platform-created NV Index that has TPMA_NV_POLICY_DELETE SET.
        /// </summary>
        void NV_UndefineSpaceSpecialComplete
        (
        );
        /// <summary>
        /// This command is used to read the public area and Name of an NV Index. The public area of an Index is not privacy-sensitive and no authorization is required to read this data.
        /// </summary>
        ///<param name = "nvPublicSize">size of nvPublic</param>
        ///<param name = "nvPublic">the public area of the NV Index</param>
        ///<param name = "nvNameSize">size of the Name structure</param>
        ///<param name = "nvName">the Name of the nvIndex</param>
        NV_ReadPublicResponse NV_ReadPublicComplete
        (
        );
        /// <summary>
        /// This command writes a value to an area in NV memory that was previously defined by TPM2_NV_DefineSpace().
        /// </summary>
        void NV_WriteComplete
        (
        );
        /// <summary>
        /// This command is used to increment the value in an NV Index that has TPMA_NV_COUNTER SET. The data value of the NV Index is incremented by one.
        /// </summary>
        void NV_IncrementComplete
        (
        );
        /// <summary>
        /// This command extends a value to an area in NV memory that was previously defined by TPM2_NV_DefineSpace.
        /// </summary>
        void NV_ExtendComplete
        (
        );
        /// <summary>
        /// This command is used to SET bits in an NV Index that was created as a bit field. Any number of bits from 0 to 64 may be SET. The contents of data are ORed with the current contents of the NV Index starting at offset.
        /// </summary>
        void NV_SetBitsComplete
        (
        );
        /// <summary>
        /// If the TPMA_NV_WRITEDEFINE or TPMA_NV_WRITE_STCLEAR attributes of an NV location are SET, then this command may be used to inhibit further writes of the NV Index.
        /// </summary>
        void NV_WriteLockComplete
        (
        );
        /// <summary>
        /// The command will SET TPMA_NV_WRITELOCKED for all indexes that have their TPMA_NV_GLOBALLOCK attribute SET.
        /// </summary>
        void NV_GlobalWriteLockComplete
        (
        );
        /// <summary>
        /// This command reads a value from an area in NV memory previously defined by TPM2_NV_DefineSpace().
        /// </summary>
        ///<param name = "dataSize">size of the buffer</param>
        ///<param name = "data">the data read</param>
        std::vector<BYTE> NV_ReadComplete
        (
        );
        /// <summary>
        /// If TPMA_NV_READ_STCLEAR is SET in an Index, then this command may be used to prevent further reads of the NV Index until the next TPM2_Startup (TPM_SU_CLEAR).
        /// </summary>
        void NV_ReadLockComplete
        (
        );
        /// <summary>
        /// This command allows the authorization secret for an NV Index to be changed.
        /// </summary>
        void NV_ChangeAuthComplete
        (
        );
        /// <summary>
        /// The purpose of this command is to certify the contents of an NV Index or portion of an NV Index.
        /// </summary>
        ///<param name = "certifyInfoSize">size of the attestationData structure</param>
        ///<param name = "certifyInfo">the structure that was signed</param>
        ///<param name = "signatureSigAlg">selector of the algorithm used to construct the signature</param>
        ///<param name = "signature">the asymmetric signature over certifyInfo using the key referenced by signHandle(One of TPMS_SIGNATURE_RSASSA, TPMS_SIGNATURE_RSAPSS, TPMS_SIGNATURE_ECDSA, TPMS_SIGNATURE_ECDAA, TPMS_SIGNATURE_SM2, TPMS_SIGNATURE_ECSCHNORR, TpmHash, TPMS_SCHEME_HASH, TPMS_NULL_SIGNATURE)</param>
        NV_CertifyResponse NV_CertifyComplete
        (
        );
        /// <summary>
        /// This is a placeholder to allow testing of the dispatch code.
        /// </summary>
        ///<param name = "outputDataSize">size in octets of the buffer field; may be 0</param>
        ///<param name = "outputData">dummy data</param>
        std::vector<BYTE> Vendor_TCG_TestComplete
        (
        );
    };

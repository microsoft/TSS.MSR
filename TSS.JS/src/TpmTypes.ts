import { toTpm, fromTpm, toTpm2B, fromTpm2B,
         TpmMarshaller, TpmStructure, getCurStuctRemainingSize,
         createFromTpm, sizedToTpm, sizedFromTpm, arrayToTpm, arrayFromTpm,
         nonStandardFromTpm, nonStandardToTpm } from "./TpmMarshaller.js";


/**
* TPM object handle (and related data)
*/
export class TPM_HANDLE extends TpmStructure
{
    constructor(
        public handle: number = 0
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        return toTpm(this.handle, buf, 4, pos);
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.handle, pos] = fromTpm(buf, 4, pos);
        return pos;
    }
};


/**
* Table 2:124 - Definition of TPMS_AUTH_COMMAND Structure  (StructuresTable)
*/
export class TPMS_AUTH_COMMAND extends TpmStructure
{
    constructor(
        public sessionHandle: TPM_HANDLE = new TPM_HANDLE(0),
        public nonce: Buffer = new Buffer(0),
        public sessionAttributes: number = 0,
        public hmac: Buffer = new Buffer(0)
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = this.sessionHandle.toTpm(buf, pos);
        pos = toTpm2B(this.nonce, buf, pos);
        pos = toTpm(this.sessionAttributes, buf, 1, pos);
        pos = toTpm2B(this.hmac, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        pos = this.sessionHandle.fromTpm(buf, pos);
        [this.nonce, pos] = fromTpm2B(buf, pos);
        [this.sessionAttributes, pos] = fromTpm(buf, 1, pos);
        [this.hmac, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; // class TPMS_AUTH_COMMAND

// Table 2:125 - Definition of TPMS_AUTH_RESPONSE Structure  (StructuresTable)
export class TPMS_AUTH_RESPONSE extends TpmStructure
{
    constructor(
        public nonce: Buffer = new Buffer(0),
        public sessionAttributes: number = 0,
        public hmac: Buffer = new Buffer(0)
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm2B(this.nonce, buf, pos);
        pos = toTpm(this.sessionAttributes, buf, 1, pos);
        pos = toTpm2B(this.hmac, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.nonce, pos] = fromTpm2B(buf, pos);
        [this.sessionAttributes, pos] = fromTpm(buf, 1, pos);
        [this.hmac, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; // class TPMS_AUTH_RESPONSE



/**
 *  Table 2 is the list of algorithms to which the TCG has assigned an algorithm identifier along with its numeric identifier.
 */
export enum TPM_ALG_ID // UINT16
{
    /**
     *  should not occur
     */
    ERROR = 0x0000,
    /**
     *  the RSA algorithm
     */
    FIRST = 0x0001,
    /**
     *  the RSA algorithm
     */
    RSA = 0x0001,
    /**
     *  the SHA1 algorithm
     */
    SHA = 0x0004,
    /**
     *  redefinition for documentation consistency
     */
    SHA1 = 0x0004,
    TDES = 0x0003,
    /**
     *  Hash Message Authentication Code (HMAC) algorithm
     */
    HMAC = 0x0005,
    /**
     *  the AES algorithm with various key sizes
     */
    AES = 0x0006,
    /**
     *  hash-based mask-generation function
     */
    MGF1 = 0x0007,
    /**
     *  an object type that may use XOR for encryption or an HMAC for signing and may also refer to a data object that is neither signing nor encrypting
     */
    KEYEDHASH = 0x0008,
    /**
     *  the XOR encryption algorithm
     */
    XOR = 0x000A,
    /**
     *  the SHA 256 algorithm
     */
    SHA256 = 0x000B,
    /**
     *  the SHA 384 algorithm
     */
    SHA384 = 0x000C,
    /**
     *  the SHA 512 algorithm
     */
    SHA512 = 0x000D,
    /**
     *  Null algorithm
     */
    NULL = 0x0010,
    /**
     *  SM3 hash algorithm
     */
    SM3_256 = 0x0012,
    /**
     *  SM4 symmetric block cipher
     */
    SM4 = 0x0013,
    /**
     *  a signature algorithm defined in section 8.2 (RSASSA-PKCS1-v1_5)
     */
    RSASSA = 0x0014,
    /**
     *  a padding algorithm defined in section 7.2 (RSAES-PKCS1-v1_5)
     */
    RSAES = 0x0015,
    /**
     *  a signature algorithm defined in section 8.1 (RSASSA-PSS)
     */
    RSAPSS = 0x0016,
    /**
     *  a padding algorithm defined in section 7.1 (RSAES_OAEP)
     */
    OAEP = 0x0017,
    /**
     *  signature algorithm using elliptic curve cryptography (ECC)
     */
    ECDSA = 0x0018,
    /**
     *  secret sharing using ECC Based on context, this can be either One-Pass Diffie-Hellman, C(1, 1, ECC CDH) defined in 6.2.2.2 or Full Unified Model C(2, 2, ECC CDH) defined in 6.1.1.2
     */
    ECDH = 0x0019,
    /**
     *  elliptic-curve based, anonymous signing scheme
     */
    ECDAA = 0x001A,
    /**
     *  SM2  depending on context, either an elliptic-curve based, signature algorithm or a key exchange protocol
     *  NOTE	Type listed as signing but, other uses are allowed according to context.
     */
    SM2 = 0x001B,
    /**
     *  elliptic-curve based Schnorr signature
     */
    ECSCHNORR = 0x001C,
    /**
     *  two-phase elliptic-curve key exchange  C(2, 2, ECC MQV) section 6.1.1.4
     */
    ECMQV = 0x001D,
    /**
     *  concatenation key derivation function (approved alternative 1) section 5.8.1
     */
    KDF1_SP800_56A = 0x0020,
    /**
     *  key derivation function KDF2 section 13.2
     */
    KDF2 = 0x0021,
    /**
     *  a key derivation method Section 5.1 KDF in Counter Mode
     */
    KDF1_SP800_108 = 0x0022,
    /**
     *  prime field ECC
     */
    ECC = 0x0023,
    /**
     *  the object type for a symmetric block cipher
     */
    SYMCIPHER = 0x0025,
    /**
     *  Camellia is symmetric block cipher. The Camellia algorithm with various key sizes
     */
    CAMELLIA = 0x0026,
    /**
     *  Counter mode  if implemented, all symmetric block ciphers (S type) implemented shall be capable of using this mode.
     */
    CTR = 0x0040,
    /**
     *  Output Feedback mode  if implemented, all symmetric block ciphers (S type) implemented shall be capable of using this mode.
     */
    OFB = 0x0041,
    /**
     *  Cipher Block Chaining mode  if implemented, all symmetric block ciphers (S type) implemented shall be capable of using this mode.
     */
    CBC = 0x0042,
    /**
     *  Cipher Feedback mode  if implemented, all symmetric block ciphers (S type) implemented shall be capable of using this mode.
     */
    CFB = 0x0043,
    /**
     *  Electronic Codebook mode  if implemented, all symmetric block ciphers (S type) implemented shall be capable of using this mode.
     *  NOTE This mode is not recommended for uses unless the key is frequently rotated such as in video codecs
     */
    ECB = 0x0044,
    LAST = 0x0044,
    /**
     *  Phony alg ID to be used for the first union member with no selector
     */
    ANY = 0x7FFF,
    /**
     *  Phony alg ID to be used for the second union member with no selector
     */
    ANY2 = 0x7FFE
}; // enum TPM_ALG_ID

/**
 *  Table 3 is the list of identifiers for TCG-registered curve ID values for elliptic curve cryptography.
 */
export enum TPM_ECC_CURVE // UINT16
{
    NONE = 0x0000,
    NIST_P192 = 0x0001,
    NIST_P224 = 0x0002,
    NIST_P256 = 0x0003,
    NIST_P384 = 0x0004,
    NIST_P521 = 0x0005,
    /**
     *  curve to support ECDAA
     */
    BN_P256 = 0x0010,
    /**
     *  curve to support ECDAA
     */
    BN_P638 = 0x0011,
    SM2_P256 = 0x0020
}; // enum TPM_ECC_CURVE

/**
 *  Table 12 lists the command codes and their attributes. The only normative column in this table is the column indicating the command code assigned to a specific command (the "Command Code" column). For all other columns, the command and response tables in TPM 2.0 Part 3 are definitive.
 */
export enum TPM_CC  // UINT32
{
    /**
     *  Compile variable. May decrease based on implementation.
     */
    FIRST = 0x0000011F,
    NV_UndefineSpaceSpecial = 0x0000011F,
    EvictControl = 0x00000120,
    HierarchyControl = 0x00000121,
    NV_UndefineSpace = 0x00000122,
    ChangeEPS = 0x00000124,
    ChangePPS = 0x00000125,
    Clear = 0x00000126,
    ClearControl = 0x00000127,
    ClockSet = 0x00000128,
    HierarchyChangeAuth = 0x00000129,
    NV_DefineSpace = 0x0000012A,
    PCR_Allocate = 0x0000012B,
    PCR_SetAuthPolicy = 0x0000012C,
    PP_Commands = 0x0000012D,
    SetPrimaryPolicy = 0x0000012E,
    FieldUpgradeStart = 0x0000012F,
    ClockRateAdjust = 0x00000130,
    CreatePrimary = 0x00000131,
    NV_GlobalWriteLock = 0x00000132,
    GetCommandAuditDigest = 0x00000133,
    NV_Increment = 0x00000134,
    NV_SetBits = 0x00000135,
    NV_Extend = 0x00000136,
    NV_Write = 0x00000137,
    NV_WriteLock = 0x00000138,
    DictionaryAttackLockReset = 0x00000139,
    DictionaryAttackParameters = 0x0000013A,
    NV_ChangeAuth = 0x0000013B,
    /**
     *  PCR
     */
    PCR_Event = 0x0000013C,
    /**
     *  PCR
     */
    PCR_Reset = 0x0000013D,
    SequenceComplete = 0x0000013E,
    SetAlgorithmSet = 0x0000013F,
    SetCommandCodeAuditStatus = 0x00000140,
    FieldUpgradeData = 0x00000141,
    IncrementalSelfTest = 0x00000142,
    SelfTest = 0x00000143,
    Startup = 0x00000144,
    Shutdown = 0x00000145,
    StirRandom = 0x00000146,
    ActivateCredential = 0x00000147,
    Certify = 0x00000148,
    /**
     *  Policy
     */
    PolicyNV = 0x00000149,
    CertifyCreation = 0x0000014A,
    Duplicate = 0x0000014B,
    GetTime = 0x0000014C,
    GetSessionAuditDigest = 0x0000014D,
    NV_Read = 0x0000014E,
    NV_ReadLock = 0x0000014F,
    ObjectChangeAuth = 0x00000150,
    /**
     *  Policy
     */
    PolicySecret = 0x00000151,
    Rewrap = 0x00000152,
    Create = 0x00000153,
    ECDH_ZGen = 0x00000154,
    HMAC = 0x00000155,
    Import = 0x00000156,
    Load = 0x00000157,
    Quote = 0x00000158,
    RSA_Decrypt = 0x00000159,
    HMAC_Start = 0x0000015B,
    SequenceUpdate = 0x0000015C,
    Sign = 0x0000015D,
    Unseal = 0x0000015E,
    /**
     *  Policy
     */
    PolicySigned = 0x00000160,
    /**
     *  Context
     */
    ContextLoad = 0x00000161,
    /**
     *  Context
     */
    ContextSave = 0x00000162,
    ECDH_KeyGen = 0x00000163,
    EncryptDecrypt = 0x00000164,
    /**
     *  Context
     */
    FlushContext = 0x00000165,
    LoadExternal = 0x00000167,
    MakeCredential = 0x00000168,
    /**
     *  NV
     */
    NV_ReadPublic = 0x00000169,
    /**
     *  Policy
     */
    PolicyAuthorize = 0x0000016A,
    /**
     *  Policy
     */
    PolicyAuthValue = 0x0000016B,
    /**
     *  Policy
     */
    PolicyCommandCode = 0x0000016C,
    /**
     *  Policy
     */
    PolicyCounterTimer = 0x0000016D,
    /**
     *  Policy
     */
    PolicyCpHash = 0x0000016E,
    /**
     *  Policy
     */
    PolicyLocality = 0x0000016F,
    /**
     *  Policy
     */
    PolicyNameHash = 0x00000170,
    /**
     *  Policy
     */
    PolicyOR = 0x00000171,
    /**
     *  Policy
     */
    PolicyTicket = 0x00000172,
    ReadPublic = 0x00000173,
    RSA_Encrypt = 0x00000174,
    StartAuthSession = 0x00000176,
    VerifySignature = 0x00000177,
    ECC_Parameters = 0x00000178,
    FirmwareRead = 0x00000179,
    GetCapability = 0x0000017A,
    GetRandom = 0x0000017B,
    GetTestResult = 0x0000017C,
    Hash = 0x0000017D,
    /**
     *  PCR
     */
    PCR_Read = 0x0000017E,
    /**
     *  Policy
     */
    PolicyPCR = 0x0000017F,
    PolicyRestart = 0x00000180,
    ReadClock = 0x00000181,
    PCR_Extend = 0x00000182,
    PCR_SetAuthValue = 0x00000183,
    NV_Certify = 0x00000184,
    EventSequenceComplete = 0x00000185,
    HashSequenceStart = 0x00000186,
    /**
     *  Policy
     */
    PolicyPhysicalPresence = 0x00000187,
    /**
     *  Policy
     */
    PolicyDuplicationSelect = 0x00000188,
    /**
     *  Policy
     */
    PolicyGetDigest = 0x00000189,
    TestParms = 0x0000018A,
    Commit = 0x0000018B,
    /**
     *  Policy
     */
    PolicyPassword = 0x0000018C,
    ZGen_2Phase = 0x0000018D,
    EC_Ephemeral = 0x0000018E,
    /**
     *  Policy
     */
    PolicyNvWritten = 0x0000018F,
    /**
     *  Policy
     */
    PolicyTemplate = 0x00000190,
    CreateLoaded = 0x00000191,
    /**
     *  Policy
     */
    PolicyAuthorizeNV = 0x00000192,
    EncryptDecrypt2 = 0x00000193,
    /**
     *  Compile variable. May increase based on implementation.
     */
    LAST = 0x00000193,
    CC_VEND = 0x20000000,
    /**
     *  Used for testing of command dispatch
     */
    Vendor_TCG_Test = TPM_CC.CC_VEND
}; // enum TPM_CC

/**
 *  In general, response codes defined in TPM 2.0 Part 2 will be unmarshaling errors and will have the F (format) bit SET. Codes that are unique to TPM 2.0 Part 3 will have the F bit CLEAR but the V (version) attribute will be SET to indicate that it is a TPM 2.0 response code. See Response Code Details in TPM 2.0 Part 1.
 */
export enum TPM_RC // UINT32
{
    SUCCESS = 0x000,
    /**
     *  defined for compatibility with TPM 1.2
     */
    BAD_TAG = 0x01E,
    /**
     *  set for all format 0 response codes
     */
    RC_VER1 = 0x100,
    /**
     *  TPM not initialized by TPM2_Startup or already initialized
     */
    INITIALIZE = 0x100 + 0x000,
    /**
     *  commands not being accepted because of a TPM failure
     *  NOTE	This may be returned by TPM2_GetTestResult() as the testResult parameter.
     */
    FAILURE = 0x100 + 0x001,
    /**
     *  improper use of a sequence handle
     */
    SEQUENCE = 0x100 + 0x003,
    /**
     *  not currently used
     */
    PRIVATE = 0x100 + 0x00B,
    /**
     *  not currently used
     */
    HMAC = 0x100 + 0x019,
    /**
     *  the command is disabled
     */
    DISABLED = 0x100 + 0x020,
    /**
     *  command failed because audit sequence required exclusivity
     */
    EXCLUSIVE = 0x100 + 0x021,
    /**
     *  authorization handle is not correct for command
     */
    AUTH_TYPE = 0x100 + 0x024,
    /**
     *  command requires an authorization session for handle and it is not present.
     */
    AUTH_MISSING = 0x100 + 0x025,
    /**
     *  policy failure in math operation or an invalid authPolicy value
     */
    POLICY = 0x100 + 0x026,
    /**
     *  PCR check fail
     */
    PCR = 0x100 + 0x027,
    /**
     *  PCR have changed since checked.
     */
    PCR_CHANGED = 0x100 + 0x028,
    /**
     *  for all commands other than TPM2_FieldUpgradeData(), this code indicates that the TPM is in field upgrade mode; for TPM2_FieldUpgradeData(), this code indicates that the TPM is not in field upgrade mode
     */
    UPGRADE = 0x100 + 0x02D,
    /**
     *  context ID counter is at maximum.
     */
    TOO_MANY_CONTEXTS = 0x100 + 0x02E,
    /**
     *  authValue or authPolicy is not available for selected entity.
     */
    AUTH_UNAVAILABLE = 0x100 + 0x02F,
    /**
     *  a _TPM_Init and Startup(CLEAR) is required before the TPM can resume operation.
     */
    REBOOT = 0x100 + 0x030,
    /**
     *  the protection algorithms (hash and symmetric) are not reasonably balanced. The digest size of the hash must be larger than the key size of the symmetric algorithm.
     */
    UNBALANCED = 0x100 + 0x031,
    /**
     *  command commandSize value is inconsistent with contents of the command buffer; either the size is not the same as the octets loaded by the hardware interface layer or the value is not large enough to hold a command header
     */
    COMMAND_SIZE = 0x100 + 0x042,
    /**
     *  command code not supported
     */
    COMMAND_CODE = 0x100 + 0x043,
    /**
     *  the value of authorizationSize is out of range or the number of octets in the Authorization Area is greater than required
     */
    AUTHSIZE = 0x100 + 0x044,
    /**
     *  use of an authorization session with a context command or another command that cannot have an authorization session.
     */
    AUTH_CONTEXT = 0x100 + 0x045,
    /**
     *  NV offset+size is out of range.
     */
    NV_RANGE = 0x100 + 0x046,
    /**
     *  Requested allocation size is larger than allowed.
     */
    NV_SIZE = 0x100 + 0x047,
    /**
     *  NV access locked.
     */
    NV_LOCKED = 0x100 + 0x048,
    /**
     *  NV access authorization fails in command actions (this failure does not affect lockout.action)
     */
    NV_AUTHORIZATION = 0x100 + 0x049,
    /**
     *  an NV Index is used before being initialized or the state saved by TPM2_Shutdown(STATE) could not be restored
     */
    NV_UNINITIALIZED = 0x100 + 0x04A,
    /**
     *  insufficient space for NV allocation
     */
    NV_SPACE = 0x100 + 0x04B,
    /**
     *  NV Index or persistent object already defined
     */
    NV_DEFINED = 0x100 + 0x04C,
    /**
     *  context in TPM2_ContextLoad() is not valid
     */
    BAD_CONTEXT = 0x100 + 0x050,
    /**
     *  cpHash value already set or not correct for use
     */
    CPHASH = 0x100 + 0x051,
    /**
     *  handle for parent is not a valid parent
     */
    PARENT = 0x100 + 0x052,
    /**
     *  some function needs testing.
     */
    NEEDS_TEST = 0x100 + 0x053,
    /**
     *  returned when an internal function cannot process a request due to an unspecified problem. This code is usually related to invalid parameters that are not properly filtered by the input unmarshaling code.
     */
    NO_RESULT = 0x100 + 0x054,
    /**
     *  the sensitive area did not unmarshal correctly after decryption  this code is used in lieu of the other unmarshaling errors so that an attacker cannot determine where the unmarshaling error occurred
     */
    SENSITIVE = 0x100 + 0x055,
    /**
     *  largest version 1 code that is not a warning
     */
    RC_MAX_FM0 = 0x100 + 0x07F,
    /**
     *  This bit is SET in all format 1 response codes
     *  The codes in this group may have a value added to them to indicate the handle, session, or parameter to which they apply.
     */
    RC_FMT1 = 0x080,
    /**
     *  asymmetric algorithm not supported or not correct
     */
    ASYMMETRIC = 0x080 + 0x001,
    /**
     *  inconsistent attributes
     */
    ATTRIBUTES = 0x080 + 0x002,
    /**
     *  hash algorithm not supported or not appropriate
     */
    HASH = 0x080 + 0x003,
    /**
     *  value is out of range or is not correct for the context
     */
    VALUE = 0x080 + 0x004,
    /**
     *  hierarchy is not enabled or is not correct for the use
     */
    HIERARCHY = 0x080 + 0x005,
    /**
     *  key size is not supported
     */
    KEY_SIZE = 0x080 + 0x007,
    /**
     *  mask generation function not supported
     */
    MGF = 0x080 + 0x008,
    /**
     *  mode of operation not supported
     */
    MODE = 0x080 + 0x009,
    /**
     *  the type of the value is not appropriate for the use
     */
    TYPE = 0x080 + 0x00A,
    /**
     *  the handle is not correct for the use
     */
    HANDLE = 0x080 + 0x00B,
    /**
     *  unsupported key derivation function or function not appropriate for use
     */
    KDF = 0x080 + 0x00C,
    /**
     *  value was out of allowed range.
     */
    RANGE = 0x080 + 0x00D,
    /**
     *  the authorization HMAC check failed and DA counter incremented
     */
    AUTH_FAIL = 0x080 + 0x00E,
    /**
     *  invalid nonce size or nonce value mismatch
     */
    NONCE = 0x080 + 0x00F,
    /**
     *  authorization requires assertion of PP
     */
    PP = 0x080 + 0x010,
    /**
     *  unsupported or incompatible scheme
     */
    SCHEME = 0x080 + 0x012,
    /**
     *  structure is the wrong size
     */
    SIZE = 0x080 + 0x015,
    /**
     *  unsupported symmetric algorithm or key size, or not appropriate for instance
     */
    SYMMETRIC = 0x080 + 0x016,
    /**
     *  incorrect structure tag
     */
    TAG = 0x080 + 0x017,
    /**
     *  union selector is incorrect
     */
    SELECTOR = 0x080 + 0x018,
    /**
     *  the TPM was unable to unmarshal a value because there were not enough octets in the input buffer
     */
    INSUFFICIENT = 0x080 + 0x01A,
    /**
     *  the signature is not valid
     */
    SIGNATURE = 0x080 + 0x01B,
    /**
     *  key fields are not compatible with the selected use
     */
    KEY = 0x080 + 0x01C,
    /**
     *  a policy check failed
     */
    POLICY_FAIL = 0x080 + 0x01D,
    /**
     *  integrity check failed
     */
    INTEGRITY = 0x080 + 0x01F,
    /**
     *  invalid ticket
     */
    TICKET = 0x080 + 0x020,
    /**
     *  authorization failure without DA implications
     */
    BAD_AUTH = 0x080 + 0x022,
    /**
     *  the policy has expired
     */
    EXPIRED = 0x080 + 0x023,
    /**
     *  the commandCode in the policy is not the commandCode of the command or the command code in a policy command references a command that is not implemented
     */
    POLICY_CC = 0x080 + 0x024,
    /**
     *  public and sensitive portions of an object are not cryptographically bound
     */
    BINDING = 0x080 + 0x025,
    /**
     *  curve not supported
     */
    CURVE = 0x080 + 0x026,
    /**
     *  point is not on the required curve.
     */
    ECC_POINT = 0x080 + 0x027,
    /**
     *  set for warning response codes
     */
    RC_WARN = 0x900,
    /**
     *  gap for context ID is too large
     */
    CONTEXT_GAP = 0x900 + 0x001,
    /**
     *  out of memory for object contexts
     */
    OBJECT_MEMORY = 0x900 + 0x002,
    /**
     *  out of memory for session contexts
     */
    SESSION_MEMORY = 0x900 + 0x003,
    /**
     *  out of shared object/session memory or need space for internal operations
     */
    MEMORY = 0x900 + 0x004,
    /**
     *  out of session handles  a session must be flushed before a new session may be created
     */
    SESSION_HANDLES = 0x900 + 0x005,
    /**
     *  out of object handles  the handle space for objects is depleted and a reboot is required
     *  NOTE 1	This cannot occur on the reference implementation.
     *  NOTE 2	There is no reason why an implementation would implement a design that would deplete handle space. Platform specifications are encouraged to forbid it.
     */
    OBJECT_HANDLES = 0x900 + 0x006,
    /**
     *  bad locality
     */
    LOCALITY = 0x900 + 0x007,
    /**
     *  the TPM has suspended operation on the command; forward progress was made and the command may be retried
     *  See TPM 2.0 Part 1, Multi-tasking.
     *  NOTE	This cannot occur on the reference implementation.
     */
    YIELDED = 0x900 + 0x008,
    /**
     *  the command was canceled
     */
    CANCELED = 0x900 + 0x009,
    /**
     *  TPM is performing self-tests
     */
    TESTING = 0x900 + 0x00A,
    /**
     *  the 1st handle in the handle area references a transient object or session that is not loaded
     */
    REFERENCE_H0 = 0x900 + 0x010,
    /**
     *  the 2nd handle in the handle area references a transient object or session that is not loaded
     */
    REFERENCE_H1 = 0x900 + 0x011,
    /**
     *  the 3rd handle in the handle area references a transient object or session that is not loaded
     */
    REFERENCE_H2 = 0x900 + 0x012,
    /**
     *  the 4th handle in the handle area references a transient object or session that is not loaded
     */
    REFERENCE_H3 = 0x900 + 0x013,
    /**
     *  the 5th handle in the handle area references a transient object or session that is not loaded
     */
    REFERENCE_H4 = 0x900 + 0x014,
    /**
     *  the 6th handle in the handle area references a transient object or session that is not loaded
     */
    REFERENCE_H5 = 0x900 + 0x015,
    /**
     *  the 7th handle in the handle area references a transient object or session that is not loaded
     */
    REFERENCE_H6 = 0x900 + 0x016,
    /**
     *  the 1st authorization session handle references a session that is not loaded
     */
    REFERENCE_S0 = 0x900 + 0x018,
    /**
     *  the 2nd authorization session handle references a session that is not loaded
     */
    REFERENCE_S1 = 0x900 + 0x019,
    /**
     *  the 3rd authorization session handle references a session that is not loaded
     */
    REFERENCE_S2 = 0x900 + 0x01A,
    /**
     *  the 4th authorization session handle references a session that is not loaded
     */
    REFERENCE_S3 = 0x900 + 0x01B,
    /**
     *  the 5th session handle references a session that is not loaded
     */
    REFERENCE_S4 = 0x900 + 0x01C,
    /**
     *  the 6th session handle references a session that is not loaded
     */
    REFERENCE_S5 = 0x900 + 0x01D,
    /**
     *  the 7th authorization session handle references a session that is not loaded
     */
    REFERENCE_S6 = 0x900 + 0x01E,
    /**
     *  the TPM is rate-limiting accesses to prevent wearout of NV
     */
    NV_RATE = 0x900 + 0x020,
    /**
     *  authorizations for objects subject to DA protection are not allowed at this time because the TPM is in DA lockout mode
     */
    LOCKOUT = 0x900 + 0x021,
    /**
     *  the TPM was not able to start the command
     */
    RETRY = 0x900 + 0x022,
    /**
     *  the command may require writing of NV and NV is not current accessible
     */
    NV_UNAVAILABLE = 0x900 + 0x023,
    /**
     *  this value is reserved and shall not be returned by the TPM
     */
    NOT_USED = 0x900 + 0x7F
}; // enum TPM_RC

/**
 *  Structure tags are used to disambiguate structures. They are 16-bit values with the most significant bit SET so that they do not overlap TPM_ALG_ID values. A single exception is made for the value associated with TPM_ST_RSP_COMMAND (0x00C4), which has the same value as the TPM_TAG_RSP_COMMAND tag from earlier versions of this specification. This value is used when the TPM is compatible with a previous TPM specification and the TPM cannot determine which family of response code to return because the command tag is not valid.
 */
export enum TPM_ST // UINT16
{
    /**
     *  tag value for a response; used when there is an error in the tag. This is also the value returned from a TPM 1.2 when an error occurs. This value is used in this specification because an error in the command tag may prevent determination of the family. When this tag is used in the response, the response code will be TPM_RC_BAD_TAG (0 1E16), which has the same numeric value as the TPM 1.2 response code for TPM_BADTAG.
     *  NOTE	In a previously published version of this specification, TPM_RC_BAD_TAG was incorrectly assigned a value of 0x030 instead of 30 (0x01e). Some implementations my return the old value instead of the new value.
     */
    RSP_COMMAND = 0x00C4,
    /**
     *  no structure type specified
     */
    _NULL = 0X8000,
    /**
     *  tag value for a command/response for a command defined in this specification; indicating that the command/response has no attached sessions and no authorizationSize/parameterSize value is present
     *  If the responseCode from the TPM is not TPM_RC_SUCCESS, then the response tag shall have this value.
     */
    NO_SESSIONS = 0x8001,
    /**
     *  tag value for a command/response for a command defined in this specification; indicating that the command/response has one or more attached sessions and the authorizationSize/parameterSize field is present
     */
    SESSIONS = 0x8002,
    /**
     *  tag for an attestation structure
     */
    ATTEST_NV = 0x8014,
    /**
     *  tag for an attestation structure
     */
    ATTEST_COMMAND_AUDIT = 0x8015,
    /**
     *  tag for an attestation structure
     */
    ATTEST_SESSION_AUDIT = 0x8016,
    /**
     *  tag for an attestation structure
     */
    ATTEST_CERTIFY = 0x8017,
    /**
     *  tag for an attestation structure
     */
    ATTEST_QUOTE = 0x8018,
    /**
     *  tag for an attestation structure
     */
    ATTEST_TIME = 0x8019,
    /**
     *  tag for an attestation structure
     */
    ATTEST_CREATION = 0x801A,
    /**
     *  tag for a ticket type
     */
    CREATION = 0x8021,
    /**
     *  tag for a ticket type
     */
    VERIFIED = 0x8022,
    /**
     *  tag for a ticket type
     */
    AUTH_SECRET = 0x8023,
    /**
     *  tag for a ticket type
     */
    HASHCHECK = 0x8024,
    /**
     *  tag for a ticket type
     */
    AUTH_SIGNED = 0x8025,
    /**
     *  tag for a structure describing a Field Upgrade Policy
     */
    FU_MANIFEST = 0x8029
}; // enum TPM_ST

/**
 *  These values are used in TPM2_Startup() to indicate the shutdown and startup mode. The defined startup sequences are:
 */
export enum TPM_SU // UINT16
{
    /**
     *  on TPM2_Shutdown(), indicates that the TPM should prepare for loss of power and save state required for an orderly startup (TPM Reset).
     *  on TPM2_Startup(), indicates that the TPM should perform TPM Reset or TPM Restart
     */
    CLEAR = 0x0000,
    /**
     *  on TPM2_Shutdown(), indicates that the TPM should prepare for loss of power and save state required for an orderly startup (TPM Restart or TPM Resume)
     *  on TPM2_Startup(), indicates that the TPM should restore the state saved by TPM2_Shutdown(TPM_SU_STATE)
     */
    STATE = 0x0001
}; // enum TPM_SU

/**
 *  This type is used in TPM2_StartAuthSession() to indicate the type of the session to be created.
 */
export enum TPM_SE  // BYTE
{
    HMAC = 0x00,
    POLICY = 0x01,
    /**
     *  The policy session is being used to compute the policyHash and not for command authorization.
     *  This setting modifies some policy commands and prevents session from being used to authorize a command.
     */
    TRIAL = 0x03
}; // enum TPM_SE

/**
 *  The TPM_CAP values are used in TPM2_GetCapability() to select the type of the value to be returned. The format of the response varies according to the type of the value.
 */
export enum TPM_CAP // UINT32
{
    FIRST = 0x00000000,
    /**
     *  TPML_ALG_PROPERTY
     */
    ALGS = 0x00000000,
    /**
     *  TPML_HANDLE
     */
    HANDLES = 0x00000001,
    /**
     *  TPML_CCA
     */
    COMMANDS = 0x00000002,
    /**
     *  TPML_CC
     */
    PP_COMMANDS = 0x00000003,
    /**
     *  TPML_CC
     */
    AUDIT_COMMANDS = 0x00000004,
    /**
     *  TPML_PCR_SELECTION
     */
    PCRS = 0x00000005,
    /**
     *  TPML_TAGGED_TPM_PROPERTY
     */
    TPM_PROPERTIES = 0x00000006,
    /**
     *  TPML_TAGGED_PCR_PROPERTY
     */
    PCR_PROPERTIES = 0x00000007,
    /**
     *  TPML_ECC_CURVE
     */
    ECC_CURVES = 0x00000008,
    /**
     *  TPML_TAGGED_POLICY
     */
    AUTH_POLICIES = 0x00000009,
    LAST = 0x00000009,
    /**
     *  manufacturer-specific values
     */
    VENDOR_PROPERTY = 0x00000100
}; // enum TPM_CAP

/**
 *  The TPM_PT constants are used in TPM2_GetCapability(capability = TPM_CAP_TPM_PROPERTIES) to indicate the property being selected or returned.
 */
export enum TPM_PT // UINT32
{
    /**
     *  indicates no property type
     */
    NONE = 0x00000000,
    /**
     *  The number of properties in each group.
     *  NOTE The first group with any properties is group 1 (PT_GROUP * 1). Group 0 is reserved.
     */
    PT_GROUP = 0x00000100,
    /**
     *  the group of fixed properties returned as TPMS_TAGGED_PROPERTY
     *  The values in this group are only changed due to a firmware change in the TPM.
     */
    PT_FIXED = TPM_PT.PT_GROUP * 1,
    /**
     *  a 4-octet character string containing the TPM Family value (TPM_SPEC_FAMILY)
     */
    FAMILY_INDICATOR = TPM_PT.PT_FIXED + 0,
    /**
     *  the level of the specification
     *  NOTE 1	For this specification, the level is zero.
     *  NOTE 2	The level is on the title page of the specification.
     */
    LEVEL = TPM_PT.PT_FIXED + 1,
    /**
     *  the specification Revision times 100
     *  EXAMPLE	Revision 01.01 would have a value of 101.
     *  NOTE	The Revision value is on the title page of the specification.
     */
    REVISION = TPM_PT.PT_FIXED + 2,
    /**
     *  the specification day of year using TCG calendar
     *  EXAMPLE	November 15, 2010, has a day of year value of 319 (0000013F16).
     *  NOTE The specification date is on the title page of the specification.
     */
    DAY_OF_YEAR = TPM_PT.PT_FIXED + 3,
    /**
     *  the specification year using the CE
     *  EXAMPLE	The year 2010 has a value of 000007DA16.
     *  NOTE The specification date is on the title page of the specification.
     */
    YEAR = TPM_PT.PT_FIXED + 4,
    /**
     *  the vendor ID unique to each TPM manufacturer
     */
    MANUFACTURER = TPM_PT.PT_FIXED + 5,
    /**
     *  the first four characters of the vendor ID string
     *  NOTE	When the vendor string is fewer than 16 octets, the additional property values do not have to be present. A vendor string of 4 octets can be represented in one 32-bit value and no null terminating character is required.
     */
    VENDOR_STRING_1 = TPM_PT.PT_FIXED + 6,
    /**
     *  the second four characters of the vendor ID string
     */
    VENDOR_STRING_2 = TPM_PT.PT_FIXED + 7,
    /**
     *  the third four characters of the vendor ID string
     */
    VENDOR_STRING_3 = TPM_PT.PT_FIXED + 8,
    /**
     *  the fourth four characters of the vendor ID sting
     */
    VENDOR_STRING_4 = TPM_PT.PT_FIXED + 9,
    /**
     *  vendor-defined value indicating the TPM model
     */
    VENDOR_TPM_TYPE = TPM_PT.PT_FIXED + 10,
    /**
     *  the most-significant 32 bits of a TPM vendor-specific value indicating the version number of the firmware. See 10.12.2 and 10.12.8.
     */
    FIRMWARE_VERSION_1 = TPM_PT.PT_FIXED + 11,
    /**
     *  the least-significant 32 bits of a TPM vendor-specific value indicating the version number of the firmware. See 10.12.2 and 10.12.8.
     */
    FIRMWARE_VERSION_2 = TPM_PT.PT_FIXED + 12,
    /**
     *  the maximum size of a parameter (typically, a TPM2B_MAX_BUFFER)
     */
    INPUT_BUFFER = TPM_PT.PT_FIXED + 13,
    /**
     *  the minimum number of transient objects that can be held in TPM RAM
     *  NOTE	This minimum shall be no less than the minimum value required by the platform-specific specification to which the TPM is built.
     */
    HR_TRANSIENT_MIN = TPM_PT.PT_FIXED + 14,
    /**
     *  the minimum number of persistent objects that can be held in TPM NV memory
     *  NOTE	This minimum shall be no less than the minimum value required by the platform-specific specification to which the TPM is built.
     */
    HR_PERSISTENT_MIN = TPM_PT.PT_FIXED + 15,
    /**
     *  the minimum number of authorization sessions that can be held in TPM RAM
     *  NOTE	This minimum shall be no less than the minimum value required by the platform-specific specification to which the TPM is built.
     */
    HR_LOADED_MIN = TPM_PT.PT_FIXED + 16,
    /**
     *  the number of authorization sessions that may be active at a time
     *  A session is active when it has a context associated with its handle. The context may either be in TPM RAM or be context saved.
     *  NOTE	This value shall be no less than the minimum value required by the platform-specific specification to which the TPM is built.
     */
    ACTIVE_SESSIONS_MAX = TPM_PT.PT_FIXED + 17,
    /**
     *  the number of PCR implemented
     *  NOTE	This number is determined by the defined attributes, not the number of PCR that are populated.
     */
    PCR_COUNT = TPM_PT.PT_FIXED + 18,
    /**
     *  the minimum number of octets in a TPMS_PCR_SELECT.sizeOfSelect
     *  NOTE	This value is not determined by the number of PCR implemented but by the number of PCR required by the platform-specific specification with which the TPM is compliant or by the implementer if not adhering to a platform-specific specification.
     */
    PCR_SELECT_MIN = TPM_PT.PT_FIXED + 19,
    /**
     *  the maximum allowed difference (unsigned) between the contextID values of two saved session contexts
     *  This value shall be 2n-1, where n is at least 16.
     */
    CONTEXT_GAP_MAX = TPM_PT.PT_FIXED + 20,
    /**
     *  the maximum number of NV Indexes that are allowed to have the TPM_NT_COUNTER attribute
     *  NOTE	It is allowed for this value to be larger than the number of NV Indexes that can be defined. This would be indicative of a TPM implementation that did not use different implementation technology for different NV Index types.
     */
    NV_COUNTERS_MAX = TPM_PT.PT_FIXED + 22,
    /**
     *  the maximum size of an NV Index data area
     */
    NV_INDEX_MAX = TPM_PT.PT_FIXED + 23,
    /**
     *  a TPMA_MEMORY indicating the memory management method for the TPM
     */
    MEMORY = TPM_PT.PT_FIXED + 24,
    /**
     *  interval, in milliseconds, between updates to the copy of TPMS_CLOCK_INFO.clock in NV
     */
    CLOCK_UPDATE = TPM_PT.PT_FIXED + 25,
    /**
     *  the algorithm used for the integrity HMAC on saved contexts and for hashing the fuData of TPM2_FirmwareRead()
     */
    CONTEXT_HASH = TPM_PT.PT_FIXED + 26,
    /**
     *  TPM_ALG_ID, the algorithm used for encryption of saved contexts
     */
    CONTEXT_SYM = TPM_PT.PT_FIXED + 27,
    /**
     *  TPM_KEY_BITS, the size of the key used for encryption of saved contexts
     */
    CONTEXT_SYM_SIZE = TPM_PT.PT_FIXED + 28,
    /**
     *  the modulus - 1 of the count for NV update of an orderly counter
     *  The returned value is MAX_ORDERLY_COUNT.
     *  This will have a value of 2N  1 where 1  N  32
     *  NOTE 1	An orderly counter is an NV Index with an TPM_NT of TPM_NV_COUNTER and TPMA_NV_ORDERLY SET.
     *  NOTE 2	When the low-order bits of a counter equal this value, an NV write occurs on the next increment.
     */
    ORDERLY_COUNT = TPM_PT.PT_FIXED + 29,
    /**
     *  the maximum value for commandSize in a command
     */
    MAX_COMMAND_SIZE = TPM_PT.PT_FIXED + 30,
    /**
     *  the maximum value for responseSize in a response
     */
    MAX_RESPONSE_SIZE = TPM_PT.PT_FIXED + 31,
    /**
     *  the maximum size of a digest that can be produced by the TPM
     */
    MAX_DIGEST = TPM_PT.PT_FIXED + 32,
    /**
     *  the maximum size of an object context that will be returned by TPM2_ContextSave
     */
    MAX_OBJECT_CONTEXT = TPM_PT.PT_FIXED + 33,
    /**
     *  the maximum size of a session context that will be returned by TPM2_ContextSave
     */
    MAX_SESSION_CONTEXT = TPM_PT.PT_FIXED + 34,
    /**
     *  platform-specific family (a TPM_PS value)(see Table 25)
     *  NOTE	The platform-specific values for the TPM_PT_PS parameters are in the relevant platform-specific specification. In the reference implementation, all of these values are 0.
     */
    PS_FAMILY_INDICATOR = TPM_PT.PT_FIXED + 35,
    /**
     *  the level of the platform-specific specification
     */
    PS_LEVEL = TPM_PT.PT_FIXED + 36,
    /**
     *  the specification Revision times 100 for the platform-specific specification
     */
    PS_REVISION = TPM_PT.PT_FIXED + 37,
    /**
     *  the platform-specific specification day of year using TCG calendar
     */
    PS_DAY_OF_YEAR = TPM_PT.PT_FIXED + 38,
    /**
     *  the platform-specific specification year using the CE
     */
    PS_YEAR = TPM_PT.PT_FIXED + 39,
    /**
     *  the number of split signing operations supported by the TPM
     */
    SPLIT_MAX = TPM_PT.PT_FIXED + 40,
    /**
     *  total number of commands implemented in the TPM
     */
    TOTAL_COMMANDS = TPM_PT.PT_FIXED + 41,
    /**
     *  number of commands from the TPM library that are implemented
     */
    LIBRARY_COMMANDS = TPM_PT.PT_FIXED + 42,
    /**
     *  number of vendor commands that are implemented
     */
    VENDOR_COMMANDS = TPM_PT.PT_FIXED + 43,
    /**
     *  the maximum data size in one NV write, NV read, or NV certify command
     */
    NV_BUFFER_MAX = TPM_PT.PT_FIXED + 44,
    /**
     *  a TPMA_MODES value, indicating that the TPM is designed for these modes.
     */
    MODES = TPM_PT.PT_FIXED + 45,
    /**
     *  the maximum size of a TPMS_CAPABILITY_DATA structure returned in TPM2_GetCapability().
     */
    MAX_CAP_BUFFER = TPM_PT.PT_FIXED + 46,
    /**
     *  the group of variable properties returned as TPMS_TAGGED_PROPERTY
     *  The properties in this group change because of a Protected Capability other than a firmware update. The values are not necessarily persistent across all power transitions.
     */
    PT_VAR = TPM_PT.PT_GROUP * 2,
    /**
     *  TPMA_PERMANENT
     */
    PERMANENT = TPM_PT.PT_VAR + 0,
    /**
     *  TPMA_STARTUP_CLEAR
     */
    STARTUP_CLEAR = TPM_PT.PT_VAR + 1,
    /**
     *  the number of NV Indexes currently defined
     */
    HR_NV_INDEX = TPM_PT.PT_VAR + 2,
    /**
     *  the number of authorization sessions currently loaded into TPM RAM
     */
    HR_LOADED = TPM_PT.PT_VAR + 3,
    /**
     *  the number of additional authorization sessions, of any type, that could be loaded into TPM RAM
     *  This value is an estimate. If this value is at least 1, then at least one authorization session of any type may be loaded. Any command that changes the RAM memory allocation can make this estimate invalid.
     *  NOTE	A valid implementation may return 1 even if more than one authorization session would fit into RAM.
     */
    HR_LOADED_AVAIL = TPM_PT.PT_VAR + 4,
    /**
     *  the number of active authorization sessions currently being tracked by the TPM
     *  This is the sum of the loaded and saved sessions.
     */
    HR_ACTIVE = TPM_PT.PT_VAR + 5,
    /**
     *  the number of additional authorization sessions, of any type, that could be created
     *  This value is an estimate. If this value is at least 1, then at least one authorization session of any type may be created. Any command that changes the RAM memory allocation can make this estimate invalid.
     *  NOTE	A valid implementation may return 1 even if more than one authorization session could be created.
     */
    HR_ACTIVE_AVAIL = TPM_PT.PT_VAR + 6,
    /**
     *  estimate of the number of additional transient objects that could be loaded into TPM RAM
     *  This value is an estimate. If this value is at least 1, then at least one object of any type may be loaded. Any command that changes the memory allocation can make this estimate invalid.
     *  NOTE	A valid implementation may return 1 even if more than one transient object would fit into RAM.
     */
    HR_TRANSIENT_AVAIL = TPM_PT.PT_VAR + 7,
    /**
     *  the number of persistent objects currently loaded into TPM NV memory
     */
    HR_PERSISTENT = TPM_PT.PT_VAR + 8,
    /**
     *  the number of additional persistent objects that could be loaded into NV memory
     *  This value is an estimate. If this value is at least 1, then at least one object of any type may be made persistent. Any command that changes the NV memory allocation can make this estimate invalid.
     *  NOTE	A valid implementation may return 1 even if more than one persistent object would fit into NV memory.
     */
    HR_PERSISTENT_AVAIL = TPM_PT.PT_VAR + 9,
    /**
     *  the number of defined NV Indexes that have NV the TPM_NT_COUNTER attribute
     */
    NV_COUNTERS = TPM_PT.PT_VAR + 10,
    /**
     *  the number of additional NV Indexes that can be defined with their TPM_NT of TPM_NV_COUNTER and the TPMA_NV_ORDERLY attribute SET
     *  This value is an estimate. If this value is at least 1, then at least one NV Index may be created with a TPM_NT of TPM_NV_COUNTER and the TPMA_NV_ORDERLY attributes. Any command that changes the NV memory allocation can make this estimate invalid.
     *  NOTE	A valid implementation may return 1 even if more than one NV counter could be defined.
     */
    NV_COUNTERS_AVAIL = TPM_PT.PT_VAR + 11,
    /**
     *  code that limits the algorithms that may be used with the TPM
     */
    ALGORITHM_SET = TPM_PT.PT_VAR + 12,
    /**
     *  the number of loaded ECC curves
     */
    LOADED_CURVES = TPM_PT.PT_VAR + 13,
    /**
     *  the current value of the lockout counter (failedTries)
     */
    LOCKOUT_COUNTER = TPM_PT.PT_VAR + 14,
    /**
     *  the number of authorization failures before DA lockout is invoked
     */
    MAX_AUTH_FAIL = TPM_PT.PT_VAR + 15,
    /**
     *  the number of seconds before the value reported by TPM_PT_LOCKOUT_COUNTER is decremented
     */
    LOCKOUT_INTERVAL = TPM_PT.PT_VAR + 16,
    /**
     *  the number of seconds after a lockoutAuth failure before use of lockoutAuth may be attempted again
     */
    LOCKOUT_RECOVERY = TPM_PT.PT_VAR + 17,
    /**
     *  number of milliseconds before the TPM will accept another command that will modify NV
     *  This value is an approximation and may go up or down over time.
     */
    NV_WRITE_RECOVERY = TPM_PT.PT_VAR + 18,
    /**
     *  the high-order 32 bits of the command audit counter
     */
    AUDIT_COUNTER_0 = TPM_PT.PT_VAR + 19,
    /**
     *  the low-order 32 bits of the command audit counter
     */
    AUDIT_COUNTER_1 = TPM_PT.PT_VAR + 20
}; // enum TPM_PT

/**
 *  The TPM_PT_PCR constants are used in TPM2_GetCapability() to indicate the property being selected or returned. The PCR properties can be read when capability == TPM_CAP_PCR_PROPERTIES. If there is no property that corresponds to the value of property, the next higher value is returned, if it exists.
 */
export enum TPM_PT_PCR // UINT32
{
    /**
     *  bottom of the range of TPM_PT_PCR properties
     */
    FIRST = 0x00000000,
    /**
     *  a SET bit in the TPMS_PCR_SELECT indicates that the PCR is saved and restored by TPM_SU_STATE
     */
    SAVE = 0x00000000,
    /**
     *  a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be extended from locality 0
     *  This property is only present if a locality other than 0 is implemented.
     */
    EXTEND_L0 = 0x00000001,
    /**
     *  a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be reset by TPM2_PCR_Reset() from locality 0
     */
    RESET_L0 = 0x00000002,
    /**
     *  a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be extended from locality 1 This property is only present if locality 1 is implemented.
     */
    EXTEND_L1 = 0x00000003,
    /**
     *  a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be reset by TPM2_PCR_Reset() from locality 1
     *  This property is only present if locality 1 is implemented.
     */
    RESET_L1 = 0x00000004,
    /**
     *  a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be extended from locality 2 This property is only present if localities 1 and 2 are implemented.
     */
    EXTEND_L2 = 0x00000005,
    /**
     *  a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be reset by TPM2_PCR_Reset() from locality 2
     *  This property is only present if localities 1 and 2 are implemented.
     */
    RESET_L2 = 0x00000006,
    /**
     *  a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be extended from locality 3
     *  This property is only present if localities 1, 2, and 3 are implemented.
     */
    EXTEND_L3 = 0x00000007,
    /**
     *  a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be reset by TPM2_PCR_Reset() from locality 3
     *  This property is only present if localities 1, 2, and 3 are implemented.
     */
    RESET_L3 = 0x00000008,
    /**
     *  a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be extended from locality 4
     *  This property is only present if localities 1, 2, 3, and 4 are implemented.
     */
    EXTEND_L4 = 0x00000009,
    /**
     *  a SET bit in the TPMS_PCR_SELECT indicates that the PCR may be reset by TPM2_PCR_Reset() from locality 4
     *  This property is only present if localities 1, 2, 3, and 4 are implemented.
     */
    RESET_L4 = 0x0000000A,
    /**
     *  a SET bit in the TPMS_PCR_SELECT indicates that modifications to this PCR (reset or Extend) will not increment the pcrUpdateCounter
     */
    NO_INCREMENT = 0x00000011,
    /**
     *  a SET bit in the TPMS_PCR_SELECT indicates that the PCR is reset by a D-RTM event
     *  These PCR are reset to -1 on TPM2_Startup() and reset to 0 on a _TPM_Hash_End event following a _TPM_Hash_Start event.
     */
    DRTM_RESET = 0x00000012,
    /**
     *  a SET bit in the TPMS_PCR_SELECT indicates that the PCR is controlled by policy
     *  This property is only present if the TPM supports policy control of a PCR.
     */
    POLICY = 0x00000013,
    /**
     *  a SET bit in the TPMS_PCR_SELECT indicates that the PCR is controlled by an authorization value
     *  This property is only present if the TPM supports authorization control of a PCR.
     */
    AUTH = 0x00000014,
    /**
     *  top of the range of TPM_PT_PCR properties of the implementation
     *  If the TPM receives a request for a PCR property with a value larger than this, the TPM will return a zero length list and set the moreData parameter to NO.
     *  NOTE	This is an implementation-specific value. The value shown reflects the reference code implementation.
     */
    LAST = 0x00000014
}; // enum TPM_PT_PCR

/**
 *  The 32-bit handle space is divided into 256 regions of equal size with 224 values in each. Each of these ranges represents a handle type.
 */
export enum TPM_HT // BYTE
{
    /**
     *  PCR  consecutive numbers, starting at 0, that reference the PCR registers
     *  A platform-specific specification will set the minimum number of PCR and an implementation may have more.
     */
    PCR = 0x00,
    /**
     *  NV Index  assigned by the caller
     */
    NV_INDEX = 0x01,
    /**
     *  HMAC Authorization Session  assigned by the TPM when the session is created
     */
    HMAC_SESSION = 0x02,
    /**
     *  Loaded Authorization Session  used only in the context of TPM2_GetCapability
     *  This type references both loaded HMAC and loaded policy authorization sessions.
     */
    LOADED_SESSION = 0x02,
    /**
     *  Policy Authorization Session  assigned by the TPM when the session is created
     */
    POLICY_SESSION = 0x03,
    /**
     *  Saved Authorization Session  used only in the context of TPM2_GetCapability
     *  This type references saved authorization session contexts for which the TPM is maintaining tracking information.
     */
    SAVED_SESSION = 0x03,
    /**
     *  Permanent Values  assigned by this specification in Table 28
     */
    PERMANENT = 0x40,
    /**
     *  Transient Objects  assigned by the TPM when an object is loaded into transient-object memory or when a persistent object is converted to a transient object
     */
    TRANSIENT = 0x80,
    /**
     *  Persistent Objects  assigned by the TPM when a loaded transient object is made persistent
     */
    PERSISTENT = 0x81
}; // enum TPM_HT

/**
 *  Table 28 lists the architecturally defined handles that cannot be changed. The handles include authorization handles, and special handles.
 */
export enum TPM_RH // UINT32
{
    FIRST = 0x40000000,
    /**
     *  not used1
     */
    SRK = 0x40000000,
    /**
     *  handle references the Storage Primary Seed (SPS), the ownerAuth, and the ownerPolicy
     */
    OWNER = 0x40000001,
    /**
     *  not used1
     */
    REVOKE = 0x40000002,
    /**
     *  not used1
     */
    TRANSPORT = 0x40000003,
    /**
     *  not used1
     */
    OPERATOR = 0x40000004,
    /**
     *  not used1
     */
    ADMIN = 0x40000005,
    /**
     *  not used1
     */
    EK = 0x40000006,
    /**
     *  a handle associated with the null hierarchy, an EmptyAuth authValue, and an Empty Policy authPolicy.
     */
    NULL = 0x40000007,
    /**
     *  value reserved to the TPM to indicate a handle location that has not been initialized or assigned
     */
    UNASSIGNED = 0x40000008,
    /**
     *  authorization value used to indicate a password authorization session
     */
    RS_PW = 0x40000009,
    /**
     *  references the authorization associated with the dictionary attack lockout reset
     */
    LOCKOUT = 0x4000000A,
    /**
     *  references the Endorsement Primary Seed (EPS), endorsementAuth, and endorsementPolicy
     */
    ENDORSEMENT = 0x4000000B,
    /**
     *  references the Platform Primary Seed (PPS), platformAuth, and platformPolicy
     */
    PLATFORM = 0x4000000C,
    /**
     *  for phEnableNV
     */
    PLATFORM_NV = 0x4000000D,
    /**
     *  Start of a range of authorization values that are vendor-specific. A TPM may support any of the values in this range as are needed for vendor-specific purposes.
     *  Disabled if ehEnable is CLEAR.
     *  NOTE Any includes none.
     */
    AUTH_00 = 0x40000010,
    /**
     *  End of the range of vendor-specific authorization values.
     */
    AUTH_FF = 0x4000010F,
    /**
     *  the top of the reserved handle area
     *  This is set to allow TPM2_GetCapability() to know where to stop. It may vary as implementations add to the permanent handle area.
     */
    LAST = 0x4000010F
}; // enum TPM_RH

/**
 *  This table lists the values of the TPM_NT field of a TPMA_NV. See Table 204 for usage.
 */
export enum TPM_NT // UINT32
{
    /**
     *  Ordinary  contains data that is opaque to the TPM that can only be modified using TPM2_NV_Write().
     */
    ORDINARY = 0x0,
    /**
     *  Counter  contains an 8-octet value that is to be used as a counter and can only be modified with TPM2_NV_Increment()
     */
    COUNTER = 0x1,
    /**
     *  Bit Field  contains an 8-octet value to be used as a bit field and can only be modified with TPM2_NV_SetBits().
     */
    BITS = 0x2,
    /**
     *  Extend  contains a digest-sized value used like a PCR. The Index can only be modified using TPM2_NV_Extend(). The extend will use the nameAlg of the Index.
     */
    EXTEND = 0x4,
    /**
     *  PIN Fail - contains pinCount that increments on a PIN authorization failure and a pinLimit
     */
    PIN_FAIL = 0x8,
    /**
     *  PIN Pass - contains pinCount that increments on a PIN authorization success and a pinLimit
     */
    PIN_PASS = 0x9
}; // enum TPM_NT

/**
 *  This table contains a collection of values used in various parts of the reference code. The values shown are illustrative.
 */
export enum Implementation // UINT32
{
    /**
     *  temporary define
     */
//    FIELD_UPGRADE_IMPLEMENTED = Logic.NO,
    /**
     *  sets the size granularity for the buffers in a TPM2B structure
     *  TPMxB buffers will be assigned a space that is a multiple of this value. This does not set the size limits for IO. Those are set by the canonical form of the TPMxB
     */
    BUFFER_ALIGNMENT = 4,
    /**
     *  the number of PCR in the TPM
     */
    IMPLEMENTATION_PCR = 24,
    /**
     *  the number of PCR required by the relevant platform specification
     */
    PLATFORM_PCR = 24,
    /**
     *  the D-RTM PCR
     *  NOTE This value is not defined when the TPM does not implement D-RTM
     */
    DRTM_PCR = 17,
    /**
     *  the PCR that will receive the H-CRTM value at TPM2_Startup. This value should not be changed.
     */
    HCRTM_PCR = 0,
    /**
     *  the number of localities supported by the TPM
     *  This is expected to be either 5 for a PC, or 1 for just about everything else.
     */
    NUM_LOCALITIES = 5,
    /**
     *  the maximum number of handles in the handle area
     *  This should be produced by the Part 3 parser but is here for now.
     */
    MAX_HANDLE_NUM = 3,
    /**
     *  the number of simultaneously active sessions that are supported by the TPM implementation
     */
    MAX_ACTIVE_SESSIONS = 64,
    /**
     *  the number of sessions that the TPM may have in memory
     */
    MAX_LOADED_SESSIONS = 3,
    /**
     *  this is the current maximum value
     */
    MAX_SESSION_NUM = 3,
    /**
     *  the number of simultaneously loaded objects that are supported by the TPM; this number does not include the objects that may be placed in NV memory by TPM2_EvictControl().
     */
    MAX_LOADED_OBJECTS = 3,
    /**
     *  the minimum number of evict objects supported by the TPM
     */
    MIN_EVICT_OBJECTS = 2,
    PCR_SELECT_MIN = ((Implementation.PLATFORM_PCR+7)/8),
    PCR_SELECT_MAX = ((Implementation.IMPLEMENTATION_PCR+7)/8),
    /**
     *  number of PCR groups that have individual policies
     */
    NUM_POLICY_PCR_GROUP = 1,
    /**
     *  number of PCR groups that have individual authorization values
     */
    NUM_AUTHVALUE_PCR_GROUP = 1,
    /**
     *  This may be larger than necessary
     */
    MAX_CONTEXT_SIZE = 2048,
    MAX_DIGEST_BUFFER = 1024,
    /**
     *  maximum data size allowed in an NV Index
     */
    MAX_NV_INDEX_SIZE = 2048,
    /**
     *  maximum data size in one NV read or write command
     */
    MAX_NV_BUFFER_SIZE = 1024,
    /**
     *  maximum size of a capability buffer
     */
    MAX_CAP_BUFFER = 1024,
    /**
     *  size of NV memory in octets
     */
    NV_MEMORY_SIZE = 16384,
    NUM_STATIC_PCR = 16,
    /**
     *  number of algorithms that can be in a list
     */
    MAX_ALG_LIST_SIZE = 64,
    /**
     *  nominal value for the pre-scale value of Clock (the number of cycles of the TPM's oscillator for each increment of Clock)
     */
    TIMER_PRESCALE = 100000,
    /**
     *  size of the Primary Seed in octets
     */
    PRIMARY_SEED_SIZE = 32,
    /**
     *  context encryption algorithm
     */
    CONTEXT_ENCRYPT_ALG = TPM_ALG_ID.AES,
    /**
     *  context encryption key size in bits
     */
//    CONTEXT_ENCRYPT_KEY_BITS = ImplementationConstants.MAX_SYM_KEY_BITS,
//    CONTEXT_ENCRYPT_KEY_BYTES = ((Implementation.CONTEXT_ENCRYPT_KEY_BITS+7)/8),
    /**
     *  context integrity hash algorithm
     */
    CONTEXT_INTEGRITY_HASH_ALG = TPM_ALG_ID.SHA256,
    /**
     *  number of byes in the context integrity digest
     */
//    CONTEXT_INTEGRITY_HASH_SIZE = UINT32(SHA256::DIGEST_SIZE),
    /**
     *  size of proof value in octets
     *  This size of the proof should be consistent with the digest size used for context integrity.
     */
//    PROOF_SIZE = Implementation.CONTEXT_INTEGRITY_HASH_SIZE,
    /**
     *  the update interval expressed as a power of 2 seconds
     *  A value of 12 is 4,096 seconds (~68 minutes).
     */
    NV_CLOCK_UPDATE_INTERVAL = 12,
    /**
     *  number of PCR groups that allow policy/auth
     */
    NUM_POLICY_PCR = 1,
    /**
     *  maximum size of a command
     */
    MAX_COMMAND_SIZE = 4096,
    /**
     *  maximum size of a response
     */
    MAX_RESPONSE_SIZE = 4096,
    /**
     *  number between 1 and 32 inclusive
     */
    ORDERLY_BITS = 8,
    /**
     *  maximum count of orderly counter before NV is updated
     *  This must be of the form 2N  1 where 1  N  32.
     */
    MAX_ORDERLY_COUNT = ((1 << Implementation.ORDERLY_BITS) - 1),
    /**
     *  used by TPM2_GetCapability() processing to bound the algorithm search
     */
    ALG_ID_FIRST = TPM_ALG_ID.FIRST,
    /**
     *  used by TPM2_GetCapability() processing to bound the algorithm search
     */
    ALG_ID_LAST = TPM_ALG_ID.LAST,
    /**
     *  the maximum number of octets that may be in a sealed blob; 128 is the minimum allowed value
     */
    MAX_SYM_DATA = 128,
    MAX_RNG_ENTROPY_SIZE = 64,
    RAM_INDEX_SPACE = 512,
    /**
     *  216 + 1
     */
    RSA_DEFAULT_PUBLIC_EXPONENT = 0x00010001,
    /**
     *  indicates if the TPM_PT_PCR_NO_INCREMENT group is implemented
     */
//    ENABLE_PCR_NO_INCREMENT = Logic.YES,
//    CRT_FORMAT_RSA = Logic.YES,
    VENDOR_COMMAND_COUNT = 0,
    /**
     *  MAX_RSA_KEY_BYTES is auto generated from the RSA key size selection in Table 4. If RSA is not implemented, this may need to be manually removed.
     */
//    PRIVATE_VENDOR_SPECIFIC_BYTES = ((ImplementationConstants.MAX_RSA_KEY_BYTES/2) * (3 + Implementation.CRT_FORMAT_RSA * 2)),
    /**
     *  Maximum size of the vendor-specific buffer
     */
    MAX_VENDOR_BUFFER_SIZE = 1024
}; // enum Implementation

/**
 *  The definitions in Table 29 are used to define many of the interface data types.
 */
export enum TPM_HC // UINT32
{
    /**
     *  to mask off the HR
     */
    HR_HANDLE_MASK = 0x00FFFFFF,
    /**
     *  to mask off the variable part
     */
    HR_RANGE_MASK = 0xFF000000,
    HR_SHIFT = 24,
    HR_PCR = (TPM_HT.PCR << TPM_HC.HR_SHIFT),
    HR_HMAC_SESSION = (TPM_HT.HMAC_SESSION << TPM_HC.HR_SHIFT),
    HR_POLICY_SESSION = (TPM_HT.POLICY_SESSION << TPM_HC.HR_SHIFT),
    HR_TRANSIENT = (TPM_HT.TRANSIENT << TPM_HC.HR_SHIFT),
    HR_PERSISTENT = (TPM_HT.PERSISTENT << TPM_HC.HR_SHIFT),
    HR_NV_INDEX = (TPM_HT.NV_INDEX << TPM_HC.HR_SHIFT),
    HR_PERMANENT = (TPM_HT.PERMANENT << TPM_HC.HR_SHIFT),
    /**
     *  first PCR
     */
    PCR_FIRST = (TPM_HC.HR_PCR + 0),
    /**
     *  last PCR
     */
    PCR_LAST = (TPM_HC.PCR_FIRST + Implementation.IMPLEMENTATION_PCR-1),
    /**
     *  first HMAC session
     */
    HMAC_SESSION_FIRST = (TPM_HC.HR_HMAC_SESSION + 0),
    /**
     *  last HMAC session
     */
    HMAC_SESSION_LAST = (TPM_HC.HMAC_SESSION_FIRST+Implementation.MAX_ACTIVE_SESSIONS-1),
    /**
     *  used in GetCapability
     */
    LOADED_SESSION_FIRST = TPM_HC.HMAC_SESSION_FIRST,
    /**
     *  used in GetCapability
     */
    LOADED_SESSION_LAST = TPM_HC.HMAC_SESSION_LAST,
    /**
     *  first policy session
     */
    POLICY_SESSION_FIRST = (TPM_HC.HR_POLICY_SESSION + 0),
    /**
     *  last policy session
     */
    POLICY_SESSION_LAST = (TPM_HC.POLICY_SESSION_FIRST + Implementation.MAX_ACTIVE_SESSIONS-1),
    /**
     *  first transient object
     */
    TRANSIENT_FIRST = (TPM_HC.HR_TRANSIENT + 0),
    /**
     *  used in GetCapability
     */
    ACTIVE_SESSION_FIRST = TPM_HC.POLICY_SESSION_FIRST,
    /**
     *  used in GetCapability
     */
    ACTIVE_SESSION_LAST = TPM_HC.POLICY_SESSION_LAST,
    /**
     *  last transient object
     */
    TRANSIENT_LAST = (TPM_HC.TRANSIENT_FIRST+Implementation.MAX_LOADED_OBJECTS-1),
    /**
     *  first persistent object
     */
    PERSISTENT_FIRST = (TPM_HC.HR_PERSISTENT + 0),
    /**
     *  last persistent object
     */
    PERSISTENT_LAST = (TPM_HC.PERSISTENT_FIRST + 0x00FFFFFF),
    /**
     *  first platform persistent object
     */
    PLATFORM_PERSISTENT = (TPM_HC.PERSISTENT_FIRST + 0x00800000),
    /**
     *  first allowed NV Index
     */
    NV_INDEX_FIRST = (TPM_HC.HR_NV_INDEX + 0),
    /**
     *  last allowed NV Index
     */
    NV_INDEX_LAST = (TPM_HC.NV_INDEX_FIRST + 0x00FFFFFF),
    PERMANENT_FIRST = TPM_RH.FIRST,
    PERMANENT_LAST = TPM_RH.LAST
}; // enum TPM_HC


/**
 *  This structure defines the attributes of an algorithm.
 */
export enum TPMA_ALGORITHM // UINT32
{
    /**
     *  SET (1): an asymmetric algorithm with public and private portions
     *  CLEAR (0): not an asymmetric algorithm
     */
    asymmetric = 0x1,
    /**
     *  SET (1): a symmetric block cipher
     *  CLEAR (0): not a symmetric block cipher
     */
    symmetric = 0x2,
    /**
     *  SET (1): a hash algorithm
     *  CLEAR (0): not a hash algorithm
     */
    hash = 0x4,
    /**
     *  SET (1): an algorithm that may be used as an object type
     *  CLEAR (0): an algorithm that is not used as an object type
     */
    object = 0x8,
    /**
     *  SET (1): a signing algorithm. The setting of asymmetric, symmetric, and hash will indicate the type of signing algorithm.
     *  CLEAR (0): not a signing algorithm
     */
    signing = 0x100,
    /**
     *  SET (1): an encryption/decryption algorithm. The setting of asymmetric, symmetric, and hash will indicate the type of encryption/decryption algorithm.
     *  CLEAR (0): not an encryption/decryption algorithm
     */
    encrypting = 0x200,
    /**
     *  SET (1): a method such as a key derivative function (KDF)
     *  CLEAR (0): not a method
     */
    method = 0x400
}; // enum TPMA_ALGORITHM

/**
 *  This attribute structure indicates an objects use, its authorization types, and its relationship to other objects.
 */
export enum TPMA_OBJECT // UINT32
{
    /**
     *  SET (1): The hierarchy of the object, as indicated by its Qualified Name, may not change.
     *  CLEAR (0): The hierarchy of the object may change as a result of this object or an ancestor key being duplicated for use in another hierarchy.
     */
    fixedTPM = 0x2,
    /**
     *  SET (1): Previously saved contexts of this object may not be loaded after Startup(CLEAR).
     *  CLEAR (0): Saved contexts of this object may be used after a Shutdown(STATE) and subsequent Startup().
     */
    stClear = 0x4,
    /**
     *  SET (1): The parent of the object may not change.
     *  CLEAR (0): The parent of the object may change as the result of a TPM2_Duplicate() of the object.
     */
    fixedParent = 0x10,
    /**
     *  SET (1): Indicates that, when the object was created with TPM2_Create() or TPM2_CreatePrimary(), the TPM generated all of the sensitive data other than the authValue.
     *  CLEAR (0): A portion of the sensitive data, other than the authValue, was provided by the caller.
     */
    sensitiveDataOrigin = 0x20,
    /**
     *  SET (1): Approval of USER role actions with this object may be with an HMAC session or with a password using the authValue of the object or a policy session.
     *  CLEAR (0): Approval of USER role actions with this object may only be done with a policy session.
     */
    userWithAuth = 0x40,
    /**
     *  SET (1): Approval of ADMIN role actions with this object may only be done with a policy session.
     *  CLEAR (0): Approval of ADMIN role actions with this object may be with an HMAC session or with a password using the authValue of the object or a policy session.
     */
    adminWithPolicy = 0x80,
    /**
     *  SET (1): The object is not subject to dictionary attack protections.
     *  CLEAR (0): The object is subject to dictionary attack protections.
     */
    noDA = 0x400,
    /**
     *  SET (1): If the object is duplicated, then symmetricAlg shall not be TPM_ALG_NULL and newParentHandle shall not be TPM_RH_NULL.
     *  CLEAR (0): The object may be duplicated without an inner wrapper on the private portion of the object and the new parent may be TPM_RH_NULL.
     */
    encryptedDuplication = 0x800,
    /**
     *  SET (1): Key usage is restricted to manipulate structures of known format; the parent of this key shall have restricted SET.
     *  CLEAR (0): Key usage is not restricted to use on special formats.
     */
    restricted = 0x10000,
    /**
     *  SET (1): The private portion of the key may be used to decrypt.
     *  CLEAR (0): The private portion of the key may not be used to decrypt.
     */
    decrypt = 0x20000,
    /**
     *  SET (1): For a symmetric cipher object, the private portion of the key may be used to encrypt. For other objects, the private portion of the key may be used to sign.
     *  CLEAR (0): The private portion of the key may not be used to sign or encrypt.
     */
    sign = 0x40000,
    /**
     *  Alias to the Sign value.
     */
    encrypt = 0x40000
}; // enum TPMA_OBJECT

/**
 *  This octet in each session is used to identify the session type, indicate its relationship to any handles in the command, and indicate its use in parameter encryption.
 */
export enum TPMA_SESSION // BYTE
{
    /**
     *  SET (1): In a command, this setting indicates that the session is to remain active after successful completion of the command. In a response, it indicates that the session is still active. If SET in the command, this attribute shall be SET in the response.
     *  CLEAR (0): In a command, this setting indicates that the TPM should close the session and flush any related context when the command completes successfully. In a response, it indicates that the session is closed and the context is no longer active.
     *  This attribute has no meaning for a password authorization and the TPM will allow any setting of the attribute in the command and SET the attribute in the response.
     *  This attribute will only be CLEAR in one response for a logical session. If the attribute is CLEAR, the context associated with the session is no longer in use and the space is available. A session created after another session is ended may have the same handle but logically is not the same session.
     *  This attribute has no effect if the command does not complete successfully.
     */
    continueSession = 0x1,
    /**
     *  SET (1): In a command, this setting indicates that the command should only be executed if the session is exclusive at the start of the command. In a response, it indicates that the session is exclusive. This setting is only allowed if the audit attribute is SET (TPM_RC_ATTRIBUTES).
     *  CLEAR (0): In a command, indicates that the session need not be exclusive at the start of the command. In a response, indicates that the session is not exclusive.
     *  In this revision, if audit is CLEAR, auditExclusive must be CLEAR in the command and will be CLEAR in the response. In a future, revision, this bit may have a different meaning if audit is CLEAR.
     *  See "Exclusive Audit Session" clause in TPM 2.0 Part 1.
     */
    auditExclusive = 0x2,
    /**
     *  SET (1): In a command, this setting indicates that the audit digest of the session should be initialized and the exclusive status of the session SET. This setting is only allowed if the audit attribute is SET (TPM_RC_ATTRIBUTES).
     *  CLEAR (0): In a command, indicates that the audit digest should not be initialized.
     *  This bit is always CLEAR in a response.
     *  In this revision, if audit is CLEAR, auditReset must be clear in the command and will be CLEAR in the response. In a future, revision, this bit may have a different meaning if audit is CLEAR.
     */
    auditReset = 0x4,
    /**
     *  SET (1): In a command, this setting indicates that the first parameter in the command is symmetrically encrypted using the parameter encryption scheme described in TPM 2.0 Part 1. The TPM will decrypt the parameter after performing any HMAC computations and before unmarshaling the parameter. In a response, the attribute is copied from the request but has no effect on the response.
     *  CLEAR (0): Session not used for encryption.
     *  For a password authorization, this attribute will be CLEAR in both the command and response.
     *  This attribute may only be SET in one session per command.
     *  This attribute may be SET in a session that is not associated with a command handle. Such a session is provided for purposes of encrypting a parameter and not for authorization.
     *  This attribute may be SET in combination with any other session attributes.
     *  This attribute may only be SET if the first parameter of the command is a sized buffer (TPM2B_).
     */
    decrypt = 0x20,
    /**
     *  SET (1): In a command, this setting indicates that the TPM should use this session to encrypt the first parameter in the response. In a response, it indicates that the attribute was set in the command and that the TPM used the session to encrypt the first parameter in the response using the parameter encryption scheme described in TPM 2.0 Part 1.
     *  CLEAR (0): Session not used for encryption.
     *  For a password authorization, this attribute will be CLEAR in both the command and response.
     *  This attribute may only be SET in one session per command.
     *  This attribute may be SET in a session that is not associated with a command handle. Such a session is provided for purposes of encrypting a parameter and not for authorization.
     *  This attribute may only be SET if the first parameter of a response is a sized buffer (TPM2B_).
     */
    encrypt = 0x40,
    /**
     *  SET (1): In a command or response, this setting indicates that the session is for audit and that auditExclusive and auditReset have meaning. This session may also be used for authorization, encryption, or decryption. The encrypted and encrypt fields may be SET or CLEAR.
     *  CLEAR (0): Session is not used for audit.
     *  This attribute may only be SET in one session per command or response. If SET in the command, then this attribute will be SET in the response.
     */
    audit = 0x80
}; // enum TPMA_SESSION

/**
 *  In a TPMS_CREATION_DATA structure, this structure is used to indicate the locality of the command that created the object. No more than one of the locality attributes shall be set in the creation data.
 */
export enum TPMA_LOCALITY // BYTE
{
    LOC_ZERO = 0x1,
    LOC_ONE = 0x2,
    LOC_TWO = 0x4,
    LOC_THREE = 0x8,
    LOC_FOUR = 0x10,
    /**
     *  If any of these bits is set, an extended locality is indicated
     */
    Extended_BIT_MASK = 0x000000E0,
    Extended_BIT_OFFSET = 5,
    Extended_BIT_LENGTH = 3,
    Extended_BIT_0 = 0x20,
    Extended_BIT_1 = 0x40,
    Extended_BIT_2 = 0x80
}; // enum TPMA_LOCALITY

/**
 *  The attributes in this structure are persistent and are not changed as a result of _TPM_Init or any TPM2_Startup(). Some of the attributes in this structure may change as the result of specific Protected Capabilities. This structure may be read using TPM2_GetCapability(capability = TPM_CAP_TPM_PROPERTIES, property = TPM_PT_PERMANENT).
 */
export enum TPMA_PERMANENT // UINT32
{
    /**
     *  SET (1): TPM2_HierarchyChangeAuth() with ownerAuth has been executed since the last TPM2_Clear().
     *  CLEAR (0): ownerAuth has not been changed since TPM2_Clear().
     */
    ownerAuthSet = 0x1,
    /**
     *  SET (1): TPM2_HierarchyChangeAuth() with endorsementAuth has been executed since the last TPM2_Clear().
     *  CLEAR (0): endorsementAuth has not been changed since TPM2_Clear().
     */
    endorsementAuthSet = 0x2,
    /**
     *  SET (1): TPM2_HierarchyChangeAuth() with lockoutAuth has been executed since the last TPM2_Clear().
     *  CLEAR (0): lockoutAuth has not been changed since TPM2_Clear().
     */
    lockoutAuthSet = 0x4,
    /**
     *  SET (1): TPM2_Clear() is disabled.
     *  CLEAR (0): TPM2_Clear() is enabled.
     *  NOTE	See TPM2_ClearControl in TPM 2.0 Part 3 for details on changing this attribute.
     */
    disableClear = 0x100,
    /**
     *  SET (1): The TPM is in lockout, when failedTries is equal to maxTries.
     */
    inLockout = 0x200,
    /**
     *  SET (1): The EPS was created by the TPM.
     *  CLEAR (0): The EPS was created outside of the TPM using a manufacturer-specific process.
     */
    tpmGeneratedEPS = 0x400
}; // enum TPMA_PERMANENT

/**
 *  This structure may be read using TPM2_GetCapability(capability = TPM_CAP_TPM_PROPERTIES, property = TPM_PT_STARTUP_CLEAR).
 */
export enum TPMA_STARTUP_CLEAR // UINT32
{
    /**
     *  SET (1): The platform hierarchy is enabled and platformAuth or platformPolicy may be used for authorization.
     *  CLEAR (0): platformAuth and platformPolicy may not be used for authorizations, and objects in the platform hierarchy, including persistent objects, cannot be used.
     *  NOTE	See TPM2_HierarchyControl in TPM 2.0 Part 3 for details on changing this attribute.
     */
    phEnable = 0x1,
    /**
     *  SET (1): The Storage hierarchy is enabled and ownerAuth or ownerPolicy may be used for authorization. NV indices defined using owner authorization are accessible.
     *  CLEAR (0): ownerAuth and ownerPolicy may not be used for authorizations, and objects in the Storage hierarchy, persistent objects, and NV indices defined using owner authorization cannot be used.
     *  NOTE	See TPM2_HierarchyControl in TPM 2.0 Part 3 for details on changing this attribute.
     */
    shEnable = 0x2,
    /**
     *  SET (1): The EPS hierarchy is enabled and Endorsement Authorization may be used to authorize commands.
     *  CLEAR (0): Endorsement Authorization may not be used for authorizations, and objects in the endorsement hierarchy, including persistent objects, cannot be used.
     *  NOTE	See TPM2_HierarchyControl in TPM 2.0 Part 3 for details on changing this attribute.
     */
    ehEnable = 0x4,
    /**
     *  SET (1): NV indices that have TPMA_PLATFORM_CREATE SET may be read or written. The platform can create define and undefine indices.
     *  CLEAR (0): NV indices that have TPMA_PLATFORM_CREATE SET may not be read or written (TPM_RC_HANDLE). The platform cannot define (TPM_RC_HIERARCHY) or undefined (TPM_RC_HANDLE) indices.
     *  NOTE	See TPM2_HierarchyControl in TPM 2.0 Part 3 for details on changing this attribute.
     *  NOTE read refers to these commands: TPM2_NV_Read, TPM2_NV_ReadPublic, TPM_NV_Certify, TPM2_PolicyNV
     *  write refers to these commands: TPM2_NV_Write, TPM2_NV_Increment, TPM2_NV_Extend, TPM2_NV_SetBits
     *  NOTE The TPM must query the index TPMA_PLATFORM_CREATE attribute to determine whether phEnableNV is applicable. Since the TPM will return TPM_RC_HANDLE if the index does not exist, it also returns this error code if the index is disabled. Otherwise, the TPM would leak the existence of an index even when disabled.
     */
    phEnableNV = 0x8,
    /**
     *  SET (1): The TPM received a TPM2_Shutdown() and a matching TPM2_Startup().
     *  CLEAR (0): TPM2_Startup(TPM_SU_CLEAR) was not preceded by a TPM2_Shutdown() of any type.
     *  NOTE A shutdown is orderly if the TPM receives a TPM2_Shutdown() of any type followed by a TPM2_Startup() of any type. However, the TPM will return an error if TPM2_Startup(TPM_SU_STATE) was not preceded by TPM2_Shutdown(TPM_SU_STATE).
     */
    orderly = 0x80000000
}; // enum TPMA_STARTUP_CLEAR

/**
 *  This structure defines the attributes of a command from a context management perspective. The fields of the structure indicate to the TPM Resource Manager (TRM) the number of resources required by a command and how the command affects the TPMs resources.
 */
export enum TPMA_CC // UINT32
{
    /**
     *  indicates the command being selected
     */
    commandIndex_BIT_MASK = 0x0000FFFF,
    commandIndex_BIT_OFFSET = 0,
    commandIndex_BIT_LENGTH = 16,
    commandIndex_BIT_0 = 0x1,
    commandIndex_BIT_1 = 0x2,
    commandIndex_BIT_2 = 0x4,
    commandIndex_BIT_3 = 0x8,
    commandIndex_BIT_4 = 0x10,
    commandIndex_BIT_5 = 0x20,
    commandIndex_BIT_6 = 0x40,
    commandIndex_BIT_7 = 0x80,
    commandIndex_BIT_8 = 0x100,
    commandIndex_BIT_9 = 0x200,
    commandIndex_BIT_10 = 0x400,
    commandIndex_BIT_11 = 0x800,
    commandIndex_BIT_12 = 0x1000,
    commandIndex_BIT_13 = 0x2000,
    commandIndex_BIT_14 = 0x4000,
    commandIndex_BIT_15 = 0x8000,
    /**
     *  SET (1): indicates that the command may write to NV
     *  CLEAR (0): indicates that the command does not write to NV
     */
    nv = 0x400000,
    /**
     *  SET (1): This command could flush any number of loaded contexts.
     *  CLEAR (0): no additional changes other than indicated by the flushed attribute
     */
    extensive = 0x800000,
    /**
     *  SET (1): The context associated with any transient handle in the command will be flushed when this command completes.
     *  CLEAR (0): No context is flushed as a side effect of this command.
     */
    flushed = 0x1000000,
    /**
     *  indicates the number of the handles in the handle area for this command
     */
    cHandles_BIT_MASK = 0x0E000000,
    cHandles_BIT_OFFSET = 25,
    cHandles_BIT_LENGTH = 3,
    cHandles_BIT_0 = 0x2000000,
    cHandles_BIT_1 = 0x4000000,
    cHandles_BIT_2 = 0x8000000,
    /**
     *  SET (1): indicates the presence of the handle area in the response
     */
    rHandle = 0x10000000,
    /**
     *  SET (1): indicates that the command is vendor-specific
     *  CLEAR (0): indicates that the command is defined in a version of this specification
     */
    V = 0x20000000,
    /**
     *  allocated for software; shall be zero
     */
    Res_BIT_MASK = 0xC0000000,
    Res_BIT_OFFSET = 30,
    Res_BIT_LENGTH = 2,
    Res_BIT_0 = 0x40000000,
    Res_BIT_1 = 0x80000000
}; // enum TPMA_CC

/**
 *  This structure of this attribute is used to report that the TPM is designed for these modes. This structure may be read using TPM2_GetCapability(capability = TPM_CAP_TPM_PROPERTIES, property = TPM_PT_MODES).
 */
export enum TPMA_MODES // UINT32
{
    /**
     *  SET (1): indicates that the TPM is designed to comply with all of the FIPS 140-2 requirements at Level 1 or higher.
     */
    FIPS_140_2 = 0x1
}; // TPMA_MODES

/**
 *  A TPM_NV_INDEX is used to reference a defined location in NV memory. The format of the Index is changed from TPM 1.2 in order to include the Index in the reserved handle space. Handles in this range use the digest of the public area of the Index as the Name of the entity in authorization computations
 */
export enum TPM_NV_INDEX // UINT32
{
    /**
     *  The Index of the NV location
     */
    index_BIT_MASK = 0x00FFFFFF,
    index_BIT_OFFSET = 0,
    index_BIT_LENGTH = 24,
    index_BIT_0 = 0x1,
    index_BIT_1 = 0x2,
    index_BIT_2 = 0x4,
    index_BIT_3 = 0x8,
    index_BIT_4 = 0x10,
    index_BIT_5 = 0x20,
    index_BIT_6 = 0x40,
    index_BIT_7 = 0x80,
    index_BIT_8 = 0x100,
    index_BIT_9 = 0x200,
    index_BIT_10 = 0x400,
    index_BIT_11 = 0x800,
    index_BIT_12 = 0x1000,
    index_BIT_13 = 0x2000,
    index_BIT_14 = 0x4000,
    index_BIT_15 = 0x8000,
    index_BIT_16 = 0x10000,
    index_BIT_17 = 0x20000,
    index_BIT_18 = 0x40000,
    index_BIT_19 = 0x80000,
    index_BIT_20 = 0x100000,
    index_BIT_21 = 0x200000,
    index_BIT_22 = 0x400000,
    index_BIT_23 = 0x800000,
    /**
     *  constant value of TPM_HT_NV_INDEX indicating the NV Index range
     */
    RhNv_BIT_MASK = 0xFF000000,
    RhNv_BIT_OFFSET = 24,
    RhNv_BIT_LENGTH = 8,
    RhNv_BIT_0 = 0x1000000,
    RhNv_BIT_1 = 0x2000000,
    RhNv_BIT_2 = 0x4000000,
    RhNv_BIT_3 = 0x8000000,
    RhNv_BIT_4 = 0x10000000,
    RhNv_BIT_5 = 0x20000000,
    RhNv_BIT_6 = 0x40000000,
    RhNv_BIT_7 = 0x80000000
}; // enum TPM_NV_INDEX

/**
 *  This structure allows the TPM to keep track of the data and permissions to manipulate an NV Index.
 */
export enum TPMA_NV // UINT32
{
    /**
     *  SET (1): The Index data can be written if Platform Authorization is provided.
     *  CLEAR (0): Writing of the Index data cannot be authorized with Platform Authorization.
     */
    PPWRITE = 0x1,
    /**
     *  SET (1): The Index data can be written if Owner Authorization is provided.
     *  CLEAR (0): Writing of the Index data cannot be authorized with Owner Authorization.
     */
    OWNERWRITE = 0x2,
    /**
     *  SET (1): Authorizations to change the Index contents that require USER role may be provided with an HMAC session or password.
     *  CLEAR (0): Authorizations to change the Index contents that require USER role may not be provided with an HMAC session or password.
     */
    AUTHWRITE = 0x4,
    /**
     *  SET (1): Authorizations to change the Index contents that require USER role may be provided with a policy session.
     *  CLEAR (0): Authorizations to change the Index contents that require USER role may not be provided with a policy session.
     *  NOTE	TPM2_NV_ChangeAuth() always requires that authorization be provided in a policy session.
     */
    POLICYWRITE = 0x8,
    /**
     *  Ordinary  contains data that is opaque to the TPM that can only be modified using TPM2_NV_Write().
     */
    ORDINARY = 0x0,
    /**
     *  Counter  contains an 8-octet value that is to be used as a counter and can only be modified with TPM2_NV_Increment()
     */
    COUNTER = 0x10,
    /**
     *  Bit Field  contains an 8-octet value to be used as a bit field and can only be modified with TPM2_NV_SetBits().
     */
    BITS = 0x20,
    /**
     *  Extend  contains a digest-sized value used like a PCR. The Index can only be modified using TPM2_NV_Extend(). The extend will use the nameAlg of the Index.
     */
    EXTEND = 0x40,
    /**
     *  PIN Fail - contains pinCount that increments on a PIN authorization failure and a pinLimit
     */
    PIN_FAIL = 0x80,
    /**
     *  PIN Pass - contains pinCount that increments on a PIN authorization success and a pinLimit
     */
    PIN_PASS = 0x90,
    /**
     *  The type of the index. NOTE A TPM is not required to support all TPM_NT values
     */
    TpmNt_BIT_MASK = 0x000000F0,
    TpmNt_BIT_OFFSET = 4,
    TpmNt_BIT_LENGTH = 4,
    TpmNt_BIT_0 = 0x10,
    TpmNt_BIT_1 = 0x20,
    TpmNt_BIT_2 = 0x40,
    TpmNt_BIT_3 = 0x80,
    /**
     *  SET (1): Index may not be deleted unless the authPolicy is satisfied using TPM2_NV_UndefineSpaceSpecial().
     *  CLEAR (0): Index may be deleted with proper platform or owner authorization using TPM2_NV_UndefineSpace(). NOTE An Index with this attribute and a policy that cannot be satisfied (e.g., an Empty Policy) cannot be deleted.
     */
    POLICY_DELETE = 0x400,
    /**
     *  SET (1): Index cannot be written.
     *  CLEAR (0): Index can be written.
     */
    WRITELOCKED = 0x800,
    /**
     *  SET (1): A partial write of the Index data is not allowed. The write size shall match the defined space size.
     *  CLEAR (0): Partial writes are allowed. This setting is required if the .dataSize of the Index is larger than NV_MAX_BUFFER_SIZE for the implementation.
     */
    WRITEALL = 0x1000,
    /**
     *  SET (1): TPM2_NV_WriteLock() may be used to prevent further writes to this location.
     *  CLEAR (0): TPM2_NV_WriteLock() does not block subsequent writes if TPMA_NV_WRITE_STCLEAR is also CLEAR.
     */
    WRITEDEFINE = 0x2000,
    /**
     *  SET (1): TPM2_NV_WriteLock() may be used to prevent further writes to this location until the next TPM Reset or TPM Restart.
     *  CLEAR (0): TPM2_NV_WriteLock() does not block subsequent writes if TPMA_NV_WRITEDEFINE is also CLEAR.
     */
    WRITE_STCLEAR = 0x4000,
    /**
     *  SET (1): If TPM2_NV_GlobalWriteLock() is successful, then further writes to this location are not permitted until the next TPM Reset or TPM Restart.
     *  CLEAR (0): TPM2_NV_GlobalWriteLock() has no effect on the writing of the data at this Index.
     */
    GLOBALLOCK = 0x8000,
    /**
     *  SET (1): The Index data can be read if Platform Authorization is provided.
     *  CLEAR (0): Reading of the Index data cannot be authorized with Platform Authorization.
     */
    PPREAD = 0x10000,
    /**
     *  SET (1): The Index data can be read if Owner Authorization is provided.
     *  CLEAR (0): Reading of the Index data cannot be authorized with Owner Authorization.
     */
    OWNERREAD = 0x20000,
    /**
     *  SET (1): The Index data may be read if the authValue is provided.
     *  CLEAR (0): Reading of the Index data cannot be authorized with the Index authValue.
     */
    AUTHREAD = 0x40000,
    /**
     *  SET (1): The Index data may be read if the authPolicy is satisfied.
     *  CLEAR (0): Reading of the Index data cannot be authorized with the Index authPolicy.
     */
    POLICYREAD = 0x80000,
    /**
     *  SET (1): Authorization failures of the Index do not affect the DA logic and authorization of the Index is not blocked when the TPM is in Lockout mode.
     *  CLEAR (0): Authorization failures of the Index will increment the authorization failure counter and authorizations of this Index are not allowed when the TPM is in Lockout mode.
     */
    NO_DA = 0x2000000,
    /**
     *  SET (1): NV Index state is only required to be saved when the TPM performs an orderly shutdown (TPM2_Shutdown()).
     *  CLEAR (0): NV Index state is required to be persistent after the command to update the Index completes successfully (that is, the NV update is synchronous with the update command). NOTE If TPMA_NV_ORDERLY is SET, TPMA_NV_WRITTEN will be CLEAR by TPM Reset.
     */
    ORDERLY = 0x4000000,
    /**
     *  SET (1): TPMA_NV_WRITTEN for the Index is CLEAR by TPM Reset or TPM Restart.
     *  CLEAR (0): TPMA_NV_WRITTEN is not changed by TPM Restart.
     *  NOTE	This attribute may only be SET if TPM_NT is not TPM_NT_COUNTER.
     */
    CLEAR_STCLEAR = 0x8000000,
    /**
     *  SET (1): Reads of the Index are blocked until the next TPM Reset or TPM Restart.
     *  CLEAR (0): Reads of the Index are allowed if proper authorization is provided.
     */
    READLOCKED = 0x10000000,
    /**
     *  SET (1): Index has been written.
     *  CLEAR (0): Index has not been written.
     */
    WRITTEN = 0x20000000,
    /**
     *  SET (1): This Index may be undefined with Platform Authorization but not with Owner Authorization.
     *  CLEAR (0): This Index may be undefined using Owner Authorization but not with Platform Authorization. The TPM will validate that this attribute is SET when the Index is defined using Platform Authorization and will validate that this attribute is CLEAR when the Index is defined using Owner Authorization.
     */
    PLATFORMCREATE = 0x40000000,
    /**
     *  SET (1): TPM2_NV_ReadLock() may be used to SET TPMA_NV_READLOCKED for this Index.
     *  CLEAR (0): TPM2_NV_ReadLock() has no effect on this Index.
     */
    READ_STCLEAR = 0x80000000
}; // enum TPMA_NV


export interface TpmUnion extends TpmMarshaller
{
    GetUnionSelector(): TPM_ALG_ID | TPM_CAP;
}

/**
 *  This union of all asymmetric schemes is used in each of the asymmetric scheme structures. The actual scheme structure is defined by the interface type used for the selector (TPMI_ALG_ASYM_SCHEME).
 */
export interface TPMU_ASYM_SCHEME extends TpmUnion {}

/**
* Table 110 Definition of TPMU_CAPABILITIES Union (OUT)
*/
export interface TPMU_CAPABILITIES extends TpmUnion {
}

/**
 *  Table 156 Definition of TPMU_KDF_SCHEME Union (IN/OUT, S)
 */
export interface TPMU_KDF_SCHEME extends TpmUnion {}

/**
 *  This is the union of all values allowed in in the unique field of a TPMT_PUBLIC.
 */
export interface TPMU_PUBLIC_ID extends TpmUnion {}

/**
 *  Table 189 defines the possible parameter definition structures that may be contained in the public portion of a key. If the Object can be a parent, the first field must be a TPMT_SYM_DEF_OBJECT. See 11.1.7.
 */
export interface TPMU_PUBLIC_PARMS extends TpmUnion {}

/**
 *  Table 147 Definition of TPMU_SCHEME_KEYEDHASH Union (IN/OUT, S)
 */
export interface TPMU_SCHEME_KEYEDHASH extends TpmUnion {}

/**
 *  The union of all of the signature schemes.
 */
export interface TPMU_SIG_SCHEME extends TpmUnion {}

/**
 *  A TPMU_SIGNATURE_COMPOSITE is a union of the various signatures that are supported by a particular TPM implementation. The union allows substitution of any signature algorithm wherever a signature is required in a structure.
 */
export interface TPMU_SIGNATURE extends TpmUnion {}



// Equivalent of UnionElementFromSelector in TSS.Net
function createUnion<U extends TpmUnion>(unionType: string, selector: TPM_ALG_ID | TPM_CAP): U
{
    let u: TpmUnion = null;

    switch (unionType) {
        case 'TPMU_PUBLIC_PARMS':
            switch(selector) {
                case TPM_ALG_ID.KEYEDHASH: u = new TPMS_KEYEDHASH_PARMS(); break;
                //case TPM_ALG_ID.SYMCIPHER: u = new TPMS_SYMCIPHER_PARMS(); break;
                case TPM_ALG_ID.RSA: u = new TPMS_RSA_PARMS(); break;
                //case TPM_ALG_ID.ECC: u = new TPMS_ECC_PARMS(); break;
                //case TPM_ALG_ID.ANY: u = new TPMS_ASYM_PARMS(); break;
            }
            break;
        case 'TPMU_PUBLIC_ID':
            switch(selector) {
                case TPM_ALG_ID.KEYEDHASH: u = new TPM2B_DIGEST_Keyedhash(); break;
                //case TPM_ALG_ID.SYMCIPHER: u = new TPM2B_DIGEST_Symcipher(); break;
                case TPM_ALG_ID.RSA: u = new TPM2B_PUBLIC_KEY_RSA(); break;
                //case TPM_ALG_ID.ECC: u = new TPMS_ECC_POINT(); break;
                //case TPM_ALG_ID.ANY: u = new TPMS_DERIVE(); break;
            }
            break;
        case 'TPMU_ASYM_SCHEME':
            switch(selector) {
                //case TPM_ALG_ID.ECDH: u = new TPMS_KEY_SCHEME_ECDH(); break;
                //case TPM_ALG_ID.ECMQV: u = new TPMS_KEY_SCHEME_ECMQV(); break;
                case TPM_ALG_ID.RSASSA: u = new TPMS_SIG_SCHEME_RSASSA(); break;
                //case TPM_ALG_ID.RSAPSS: u = new TPMS_SIG_SCHEME_RSAPSS(); break;
                //case TPM_ALG_ID.ECDSA: u = new TPMS_SIG_SCHEME_ECDSA(); break;
                //case TPM_ALG_ID.ECDAA: u = new TPMS_SIG_SCHEME_ECDAA(); break;
                // code generator workaround BUGBUG >> (probChild)case TPM_ALG_ID.SM2: u = new TPMS_SIG_SCHEME_SM2(); break;
                // code generator workaround BUGBUG >> (probChild)case TPM_ALG_ID.ECSCHNORR: u = new TPMS_SIG_SCHEME_ECSCHNORR(); break;
                //case TPM_ALG_ID.RSAES: u = new TPMS_ENC_SCHEME_RSAES(); break;
                case TPM_ALG_ID.OAEP: u = new TPMS_ENC_SCHEME_OAEP(); break;
                //case TPM_ALG_ID.ANY: u = new TPMS_SCHEME_HASH(); break;
                case TPM_ALG_ID.NULL: u = new TPMS_NULL_ASYM_SCHEME(); break;
            }
            break;
        case 'TPMU_SCHEME_KEYEDHASH':
            switch(selector) {
                case TPM_ALG_ID.HMAC: u = new TPMS_SCHEME_HMAC(); break;
                case TPM_ALG_ID.XOR: u = new TPMS_SCHEME_XOR(); break;
                case TPM_ALG_ID.NULL: u = new TPMS_NULL_SCHEME_KEYEDHASH(); break;
            }
            break;
        case 'TPMU_CAPABILITIES':
            switch(selector) {
                case TPM_CAP.TPM_PROPERTIES: u = new TPML_TAGGED_TPM_PROPERTY(); break;
            }
            break;
        default:
            throw(new Error('CreateUnion(' + unionType + ', ' + TPM_ALG_ID[selector] + '): Unrecognized union type'));
    }
    if (u == null)
        throw(new Error('CreateUnion(' + unionType + ', ' + TPM_ALG_ID[selector] + '): Unrecognized selector'));
    return u as U;
} // CreateUnion<U>()


/**
* This structure provides information relating to the creation environment for the object. The creation data includes the parent Name, parent Qualified Name, and the digest of selected PCR. These values represent the environment in which the object was created. Creation data allows a relying party to determine if an object was created when some appropriate protections were present.
*/
export class TPMS_CREATION_DATA extends TpmStructure
{
    /**
    * @param _pcrSelect list indicating the PCR included in pcrDigest 
    * @param _pcrDigest digest of the selected PCR using nameAlg of the object for which this structure is being created pcrDigest.size shall be zero if the pcrSelect list is empty. 
    * @param _locality the locality at which the object was created 
    * @param _parentNameAlg nameAlg of the parent 
    * @param _parentName Name of the parent at time of creation The size will match digest size associated with parentNameAlg unless it is TPM_ALG_NULL, in which case the size will be 4 and parentName will be the hierarchy handle. 
    * @param _parentQualifiedName Qualified Name of the parent at the time of creation Size is the same as parentName. 
    * @param _outsideInfo association with additional information added by the key creator This will be the contents of the outsideInfo parameter in TPM2_Create() or TPM2_CreatePrimary().
    */
    public constructor(
        public pcrSelect: TPMS_PCR_SELECTION[] = null,
        public pcrDigest: Buffer = null,
        public locality: TPMA_LOCALITY = 0,
        public parentNameAlg: TPM_ALG_ID = 0,
        public parentName: Buffer = null,
        public parentQualifiedName: Buffer = null,
        public outsideInfo: Buffer = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = arrayToTpm<TPMS_PCR_SELECTION>(this.pcrSelect, buf, 4, pos);
        pos = toTpm2B(this.pcrDigest, buf, pos);
        pos = toTpm(this.locality, buf, 1, pos);
        pos = toTpm(this.parentNameAlg, buf, 2, pos);
        pos = toTpm2B(this.parentName, buf, pos);
        pos = toTpm2B(this.parentQualifiedName, buf, pos);
        pos = toTpm2B(this.outsideInfo, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.pcrSelect, pos] = arrayFromTpm(TPMS_PCR_SELECTION, buf, 4, pos);
        [this.pcrDigest, pos] = fromTpm2B(buf, pos);
        [this.locality, pos] = fromTpm(buf, 1, pos);
        [this.parentNameAlg, pos] = fromTpm(buf, 2, pos);
        [this.parentName, pos] = fromTpm2B(buf, pos);
        [this.parentQualifiedName, pos] = fromTpm2B(buf, pos);
        [this.outsideInfo, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; // class TPMS_CREATION_DATA

/**
* This structure is used for sizing the TPM2B_ID_OBJECT.
*/
export class TPMS_ID_OBJECT extends TpmStructure
{
    public constructor(
        /**
        * HMAC using the nameAlg of the storage key on the target TPM 
        */
        public integrityHMAC: Buffer = null,
        /**
        * credential protector information returned if name matches the referenced object All of the encIdentity is encrypted, including the size field. NOTE The TPM is not required to check that the size is not larger than the digest of the nameAlg. However, if the size is larger, the ID object may not be usable on a TPM that has no digest larger than produced by nameAlg.
        */
        public encIdentity: Buffer = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm2B(this.integrityHMAC, buf, pos);
        this.encIdentity.copy(buf, pos);
        pos += this.encIdentity.length;
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.integrityHMAC, pos] = fromTpm2B(buf, pos);
        let size = getCurStuctRemainingSize(pos);
        this.encIdentity = new Buffer(size);
        buf.copy(this.encIdentity, 0, pos, pos + size);
        pos += size;
        return pos;
    }
}; // class TPMS_ID_OBJECT

/**
 *  These are the RSA schemes that only need a hash algorithm as a scheme parameter.
 */
export class TPMS_ENC_SCHEME_OAEP implements TPMU_SCHEME_KEYEDHASH, TPMU_SIG_SCHEME, TPMU_KDF_SCHEME, TPMU_ASYM_SCHEME, TPMU_SIGNATURE 
{
    public constructor(
        /**
         *  the hash algorithm used to digest the message
         */
        public hashAlg: TPM_ALG_ID = 0
    ) {}

    /** TpmUnion method */
    GetUnionSelector(): TPM_ALG_ID
    {
        return TPM_ALG_ID.OAEP;
    }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm(this.hashAlg, buf, 2, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.hashAlg, pos] = fromTpm(buf, 2, pos);
        return pos;
    }
}; // class TPMS_ENC_SCHEME_OAEP

/**
 *  This structure describes the parameters that would appear in the public area of a KEYEDHASH object.
 */
export class TPMS_KEYEDHASH_PARMS implements TPMU_PUBLIC_PARMS 
{
    /**
    * @param _scheme Indicates the signing method used for a keyedHash signing object. This field also determines the size of the data field for a data object created with TPM2_Create() or TPM2_CreatePrimary(). (One of TPMS_SCHEME_HMAC, TPMS_SCHEME_XOR, TPMS_NULL_SCHEME_KEYEDHASH)
    */
    public constructor(
        /**
         *  the hash algorithm used to digest the message
         */
        public scheme: TPMU_SCHEME_KEYEDHASH = null
    ) {}

    /** TpmUnion method */
    GetUnionSelector(): TPM_ALG_ID
    {
        return TPM_ALG_ID.KEYEDHASH;
    }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm(this.scheme.GetUnionSelector(), buf, 2, pos);
        pos = this.scheme.toTpm(buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        let schemeAlg: TPM_ALG_ID;
        [schemeAlg, pos] = fromTpm(buf, 2, pos);
        this.scheme = createUnion<TPMU_SCHEME_KEYEDHASH>('TPMU_SCHEME_KEYEDHASH', schemeAlg);
        pos = this.scheme.fromTpm(buf, pos);
        return pos;
    }
}; // class TPMS_KEYEDHASH_PARMS

/**
 *  Custom data structure representing an empty element (i.e. the one with no data to marshal) for selector algorithm TPM_ALG_NULL for the union TpmuAsymScheme
 */
export class TPMS_NULL_ASYM_SCHEME implements TPMU_SCHEME_KEYEDHASH, TPMU_SIG_SCHEME, TPMU_KDF_SCHEME, TPMU_ASYM_SCHEME, TPMU_SIGNATURE // TPMU_SYM_KEY_BITS, TPMU_SYM_MODE, TPMU_SYM_DETAILS, 
{
    public constructor() {}

    /** TpmUnion method */
    GetUnionSelector(): TPM_ALG_ID
    {
        return TPM_ALG_ID.NULL;
    }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        return pos;
    }
}; // class TPMS_NULL_ASYM_SCHEME

/**
 *  Table 145 Definition of Types for HMAC_SIG_SCHEME
 */
export class TPMS_NULL_SCHEME_KEYEDHASH implements TPMU_SCHEME_KEYEDHASH, TPMU_SIG_SCHEME, TPMU_KDF_SCHEME, TPMU_ASYM_SCHEME, TPMU_SIGNATURE // TPMU_SYM_KEY_BITS, TPMU_SYM_MODE, TPMU_SYM_DETAILS, 
{
    public constructor() {}

    /** TpmUnion method */
    GetUnionSelector(): TPM_ALG_ID
    {
        return TPM_ALG_ID.NULL;
    }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        return pos;
    }
}; // class TPMS_NULL_SCHEME_KEYEDHASH

/**
 * Table 87 Definition of TPMS_PCR_SELECTION Structure
 */
export class TPMS_PCR_SELECTION extends TpmStructure
{
    public constructor(
        /**
        * the hash algorithm associated with the selection 
        */
        public hash: TPM_ALG_ID = 0,
        /**
        * the bit map of selected PCR
        */
        public pcrSelect: Buffer = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm(this.hash, buf, 2, pos);
        pos = toTpm2B(this.pcrSelect, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.hash, pos] = fromTpm(buf, 2, pos);
        [this.pcrSelect, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; // class TPMS_PCR_SELECTION

/**
 *  A TPM compatible with this specification and supporting RSA shall support two primes and an exponent of zero. Support for other values is optional. Use of other exponents in duplicated keys is not recommended because the resulting keys would not be interoperable with other TPMs.
 */
export class TPMS_RSA_PARMS implements TPMU_PUBLIC_PARMS 
{
    /**
    */
    public constructor(
        /**
         *  @param _symmetric for a restricted decryption key, shall be set to a supported symmetric algorithm, key size, and mode. if the key is not a restricted decryption key, this field shall be set to TPM_ALG_NULL. 
         */
        public symmetric: TPMT_SYM_DEF_OBJECT = null,
        /**
         *  @param _scheme scheme.scheme shall be: for an unrestricted signing key, either TPM_ALG_RSAPSS TPM_ALG_RSASSA or TPM_ALG_NULL for a restricted signing key, either TPM_ALG_RSAPSS or TPM_ALG_RSASSA for an unrestricted decryption key, TPM_ALG_RSAES, TPM_ALG_OAEP, or TPM_ALG_NULL unless the object also has the sign attribute for a restricted decryption key, TPM_ALG_NULL NOTE When both sign and decrypt are SET, restricted shall be CLEAR and scheme shall be TPM_ALG_NULL. (One of TPMS_KEY_SCHEME_ECDH, TPMS_KEY_SCHEME_ECMQV, TPMS_SIG_SCHEME_RSASSA, TPMS_SIG_SCHEME_RSAPSS, TPMS_SIG_SCHEME_ECDSA, TPMS_SIG_SCHEME_ECDAA, TPMS_SIG_SCHEME_SM2, TPMS_SIG_SCHEME_ECSCHNORR, TPMS_ENC_SCHEME_RSAES, TPMS_ENC_SCHEME_OAEP, TPMS_SCHEME_HASH, TPMS_NULL_ASYM_SCHEME) 
         */
        public scheme: TPMU_ASYM_SCHEME = null,
        /**
         *  @param _keyBits number of bits in the public modulus 
         */
        public keyBits: number = 0,
        /**
         *  @param _exponent the public exponent A prime number greater than 2. When zero, indicates that the exponent is the default of 216 + 1
         */
        public exponent: number = 0
    ) {}

    /** TpmUnion method */
    GetUnionSelector(): TPM_ALG_ID
    {
        return TPM_ALG_ID.RSA;
    }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = this.symmetric.toTpm(buf, pos);
        pos = toTpm(this.scheme.GetUnionSelector(), buf, 2, pos);
        pos = this.scheme.toTpm(buf, pos);
        pos = toTpm(this.keyBits, buf, 2, pos);
        pos = toTpm(this.exponent, buf, 4, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.symmetric, pos] = createFromTpm(TPMT_SYM_DEF_OBJECT, buf, pos);
        let schemeAlg: TPM_ALG_ID;
        [schemeAlg, pos] = fromTpm(buf, 2, pos);
        this.scheme = createUnion<TPMU_ASYM_SCHEME>('TPMU_ASYM_SCHEME', schemeAlg);
        pos = this.scheme.fromTpm(buf, pos);
        [this.keyBits, pos] = fromTpm(buf, 2, pos);
        [this.exponent, pos] = fromTpm(buf, 4, pos);
        return pos;
    }
}; // class TPMS_RSA_PARMS

/**
 *  Table 145 Definition of Types for HMAC_SIG_SCHEME
 */
export class TPMS_SCHEME_HMAC implements TPMU_SCHEME_KEYEDHASH, TPMU_SIG_SCHEME, TPMU_KDF_SCHEME, TPMU_ASYM_SCHEME, TPMU_SIGNATURE 
{
    public constructor(
        /**
         *  the hash algorithm used to digest the message
         */
        public hashAlg: TPM_ALG_ID = 0
    ) {}

    /** TpmUnion method */
    GetUnionSelector(): TPM_ALG_ID
    {
        return TPM_ALG_ID.HMAC;
    }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm(this.hashAlg, buf, 2, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.hashAlg, pos] = fromTpm(buf, 2, pos);
        return pos;
    }
}; // class TPMS_SCHEME_HMAC

/**
 *  This structure is for the XOR encryption scheme.
 */
export class TPMS_SCHEME_XOR implements TPMU_SCHEME_KEYEDHASH
{
    public constructor(
        /**
         *  the hash algorithm used to digest the message
         */
        public hashAlg: TPM_ALG_ID = 0,
        public kdf: TPM_ALG_ID = 0
    ) {}

    /** TpmUnion method */
    GetUnionSelector(): TPM_ALG_ID
    {
        return TPM_ALG_ID.XOR;
    }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm(this.hashAlg, buf, 2, pos);
        pos = toTpm(this.kdf, buf, 2, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.hashAlg, pos] = fromTpm(buf, 2, pos);
        [this.kdf, pos] = fromTpm(buf, 2, pos);
        return pos;
    }
}; // class TPMS_SCHEME_XOR

/**
 * This structure defines the values to be placed in the sensitive area of a created object. This structure is only used within a TPM2B_SENSITIVE_CREATE structure.
 */
export class TPMS_SENSITIVE_CREATE extends TpmStructure
{
    public constructor(
        /**
         * the USER auth secret value 
         */
        public userAuth: Buffer = null,
        /**
         *  data to be sealed, a key, or derivation values
         */
        public data: Buffer = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm2B(this.userAuth, buf, pos);
        pos = toTpm2B(this.data, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.userAuth, pos] = fromTpm2B(buf, pos);
        [this.data, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; // class TPMS_SENSITIVE_CREATE

/**
 *  These are the RSA schemes that only need a hash algorithm as a scheme parameter.
 */
export class TPMS_SIG_SCHEME_RSASSA implements TPMU_SCHEME_KEYEDHASH, TPMU_SIG_SCHEME, TPMU_KDF_SCHEME, TPMU_ASYM_SCHEME, TPMU_SIGNATURE 
{
    public constructor(
        /**
         *  the hash algorithm used to digest the message
         */
        public hashAlg: TPM_ALG_ID = 0
    ) {}

    /** TpmUnion method */
    GetUnionSelector(): TPM_ALG_ID
    {
        return TPM_ALG_ID.RSASSA;
    }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm(this.hashAlg, buf, 2, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.hashAlg, pos] = fromTpm(buf, 2, pos);
        return pos;
    }
}; // class TPMS_SIG_SCHEME_RSASSA

/**
 * This structure is used to report the properties that are UINT32 values. It is returned in response to a TPM2_GetCapability().
 */
export class TPMS_TAGGED_PROPERTY extends TpmStructure
{
    public constructor(
        /**
         * a property identifier 
         */
        public property: TPM_PT = 0,
        /**
         * the value of the property
         */
        public value: number = 0
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm(this.property, buf, 4, pos);
        pos = toTpm(this.value, buf, 4, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.property, pos] = fromTpm(buf, 4, pos);
        [this.value, pos] = fromTpm(buf, 4, pos);
        return pos;
    }
}; // class TPMS_TAGGED_PROPERTY

/**
 * This list is used to report on a list of properties that are TPMS_TAGGED_PROPERTY values. It is returned by a TPM2_GetCapability().
 */
export class TPML_TAGGED_TPM_PROPERTY extends TpmStructure implements TPMU_CAPABILITIES 
{
    public constructor(
        /**
         * an array of tagged properties
         */
        public tpmProperty: TPMS_TAGGED_PROPERTY[] = null
    ) { super(); }

    /** TpmUnion method */
    GetUnionSelector(): TPM_CAP
    {
        return TPM_CAP.TPM_PROPERTIES;
    }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = arrayToTpm<TPMS_TAGGED_PROPERTY>(this.tpmProperty, buf, 4, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.tpmProperty, pos] = arrayFromTpm(TPMS_TAGGED_PROPERTY, buf, 4, pos);
        return pos;
    }
}; // class TPML_TAGGED_TPM_PROPERTY


/**
  * This structure is used for a data buffer that is required to be no larger than the size of the Name of an object.
  */
export class TPM2B_DATA extends TpmStructure
{
    public constructor(
        /**
         * an encrypted private area
         */
        public buffer: Buffer = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm2B(this.buffer, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.buffer, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; // TPM2B_DATA

/**
  * This structure is used for a sized buffer that cannot be larger than the largest digest produced by any hash algorithm implemented on the TPM.
  */
export class TPM2B_DIGEST extends TpmStructure
{
    public constructor(
        /**
         * an encrypted private area
         */
        public buffer: Buffer = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm2B(this.buffer, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.buffer, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; // TPM2B_DIGEST

/**
 * Table 182 Definition of TPM2B_ENCRYPTED_SECRET Structure
 */
export class TPM2B_ENCRYPTED_SECRET extends TpmStructure
{
    public constructor(
        /**
         * secret
         */
        public secret: Buffer = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm2B(this.secret, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.secret, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; // TPM2B_ENCRYPTED_SECRET

/**
 * Auto-derived from TPM2B_DIGEST to provide unique GetUnionSelector() implementation 
 */
export class TPM2B_DIGEST_Keyedhash implements TPMU_PUBLIC_ID
{
    public constructor(
        /**
         * an encrypted private area
         */
        public buffer: Buffer = null
    ) {}

    /** TpmUnion method */
    GetUnionSelector(): TPM_ALG_ID
    {
        return TPM_ALG_ID.KEYEDHASH;
    }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm2B(this.buffer, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.buffer, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; // TPM2B_DIGEST_Keyedhash

/**
* Auto-derived from TPM2B_DIGEST to provide unique GetUnionSelector() implementation
*/
export class TPM2B_DIGEST_Symcipher implements TPMU_PUBLIC_ID
{
    public constructor(
        /**
         * an encrypted private area
         */
        public buffer: Buffer = null
    ) {}

    /** TpmUnion method */
    GetUnionSelector(): TPM_ALG_ID
    {
        return TPM_ALG_ID.KEYEDHASH;
    }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm2B(this.buffer, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.buffer, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; // TPM2B_DIGEST_Symcipher

/**
 * The TPM2B_PRIVATE structure is used as a parameter in multiple commands that create, load, and modify the sensitive area of an object.
 */
export class TPM2B_PRIVATE extends TpmStructure
{
    public constructor(
        /**
        * an encrypted private area
        */
        public buffer: Buffer = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm2B(this.buffer, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.buffer, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; // TPM2B_PRIVATE

/**
 * This sized buffer holds the largest RSA public key supported by the TPM.
 */
export class TPM2B_PUBLIC_KEY_RSA implements TPMU_PUBLIC_ID 
{
    public constructor(
        public buffer: Buffer = null
    ) {}

    /** TpmUnion method */
    GetUnionSelector(): TPM_ALG_ID
    {
        return TPM_ALG_ID.RSA;
    }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm2B(this.buffer, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.buffer, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; // class TPM2B_PUBLIC_KEY_RSA



/**
 *  Table 191 defines the public area structure. The Name of the object is nameAlg concatenated with the digest of this structure using nameAlg.
 */
export class TPMT_PUBLIC extends TpmStructure
{
    constructor(
        // Selector algorithm implicitly associated with this object
        // private TPM_ALG_ID type;
        /**
         *  algorithm used for computing the Name of the object NOTE The "+" indicates that the instance of a TPMT_PUBLIC may have a "+" to indicate that the nameAlg may be TPM_ALG_NULL.
         */
        public nameAlg: TPM_ALG_ID = 0,
        /**
         *  attributes that, along with type, determine the manipulations of this object
         */
        public objectAttributes: TPMA_OBJECT = 0,
        /**
         *  size in octets of the buffer field; may be 0
         */
        // private short authPolicySize;
        /**
         *  optional policy for using this key The policy is computed using the nameAlg of the object. NOTE Shall be the Empty Policy if no authorization policy is present.
         */
        public authPolicy: Buffer = null,
        /**
         *  the algorithm or structure details
         */
        public parameters: TPMU_PUBLIC_PARMS = null,
        /**
         *  the unique identifier of the structure For an asymmetric key, this would be the public key.
         */
        public unique: TPMU_PUBLIC_ID = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm(this.parameters.GetUnionSelector(), buf, 2, pos);
        pos = toTpm(this.nameAlg, buf, 2, pos);
        pos = toTpm(this.objectAttributes, buf, 4, pos);
        pos = toTpm2B(this.authPolicy, buf, pos);
        pos = this.parameters.toTpm(buf, pos);
        pos = this.unique.toTpm(buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        let type: TPM_ALG_ID;
        [type, pos] = fromTpm(buf, 2, pos);
        [this.nameAlg, pos] = fromTpm(buf, 2, pos);
        [this.objectAttributes, pos] = fromTpm(buf, 4, pos);
        [this.authPolicy, pos] = fromTpm2B(buf, pos);
        this.parameters = createUnion<TPMU_PUBLIC_PARMS>('TPMU_PUBLIC_PARMS', type);
        pos = this.parameters.fromTpm(buf, pos);
        this.unique = createUnion<TPMU_PUBLIC_ID>('TPMU_PUBLIC_ID', type);
        pos = this.unique.fromTpm(buf, pos);
        return pos;
    }
}; // class TPMT_PUBLIC

/**
 *  This structure is used when different symmetric block cipher (not XOR) algorithms may be selected. If the Object can be an ordinary parent (not a derivation parent), this must be the first field in the Object's parameter (see 12.2.3.7) field.
 */
export class TPMT_SYM_DEF_OBJECT extends TpmStructure
{
    public constructor(
        /**
         *  symmetric algorithm 
         */
        public algorithm: TPM_ALG_ID = 0,
        /**
         *  key size in bits 
         */
        public keyBits: number = 0,
        /**
         *  encryption mode
         */
        public mode: TPM_ALG_ID = 0
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = nonStandardToTpm(this, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        pos = nonStandardFromTpm(this, buf, pos);
        return pos;
    }
}; // class TPMT_SYM_DEF_OBJECT

/**
 *  The TPMT_SYM_DEF structure is used to select an algorithm to be used for parameter encryption in those cases when different symmetric algorithms may be selected.
 */
export class TPMT_SYM_DEF extends TpmStructure
{
    public constructor(
        /**
         *  symmetric algorithm 
         */
        public algorithm: TPM_ALG_ID,
        /**
         *  key size in bits 
         */
        public keyBits: number,
        /**
         *  encryption mode
         */
        public mode: TPM_ALG_ID
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = nonStandardToTpm(this, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        pos = nonStandardFromTpm(this, buf, pos);
        return pos;
    }
}; // class TPMT_SYM_DEF

/**
* This ticket is produced by TPM2_PolicySigned() and TPM2_PolicySecret() when the authorization has an expiration time. If nonceTPM was provided in the policy command, the ticket is computed by
*/
export class TPMT_TK_AUTH extends TpmStructure
{
    public constructor(
        /**
        * ticket structure tag 
        */
        public tag: TPM_ST = 0,
        /**
        * the hierarchy of the object used to produce the ticket 
        */
        public hierarchy: TPM_HANDLE = null,
        /**
        * This shall be the HMAC produced using a proof value of hierarchy.
        */
        public digest: Buffer = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm(this.tag, buf, 2, pos);
        pos = this.hierarchy.toTpm(buf, pos);
        pos = toTpm2B(this.digest, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.tag, pos] = fromTpm(buf, 2, pos);
        [this.hierarchy, pos] = createFromTpm(TPM_HANDLE, buf, pos);
        [this.digest, pos] = fromTpm2B(buf, pos);
        return pos;
    }
};

/**
* This ticket is produced by TPM2_Create() or TPM2_CreatePrimary(). It is used to bind the creation data to the object to which it applies. The ticket is computed by
*/
export class TPMT_TK_CREATION extends TpmStructure
{
    public constructor(
        /**
        * ticket structure tag 
        */
        public tag: TPM_ST = 0,
        /**
        * the hierarchy containing name 
        */
        public hierarchy: TPM_HANDLE = null,
        /**
        * This shall be the HMAC produced using a proof value of hierarchy.
        */
        public digest: Buffer = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm(this.tag, buf, 2, pos);
        pos = this.hierarchy.toTpm(buf, pos);
        pos = toTpm2B(this.digest, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.tag, pos] = fromTpm(buf, 2, pos);
        [this.hierarchy, pos] = createFromTpm(TPM_HANDLE, buf, pos);
        [this.digest, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; // class TPMT_TK_CREATION

/**
* This ticket is produced by TPM2_SequenceComplete() when the message that was digested did not start with TPM_GENERATED_VALUE. The ticket is computed by
*/
export class TPMT_TK_HASHCHECK extends TpmStructure
{
    /**
    * This ticket is produced by TPM2_SequenceComplete() when the message that was digested did not start with TPM_GENERATED_VALUE. The ticket is computed by
    * 
    * @param _tag ticket structure tag 
    * @param _hierarchy the hierarchy 
    * @param _digest This shall be the HMAC produced using a proof value of hierarchy.
    */
    public constructor(
        public tag: TPM_ST = null,
        public hierarchy: TPM_HANDLE = null,
        public digest: Buffer = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm(this.tag, buf, 2, pos);
        pos = this.hierarchy.toTpm(buf, pos);
        pos = toTpm2B(this.digest, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.tag, pos] = fromTpm(buf, 2, pos);
        [this.hierarchy, pos] = createFromTpm(TPM_HANDLE, buf, pos);
        [this.digest, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; // class TPMT_TK_HASHCHECK


/**
* This command is used to create a Primary Object under one of the Primary Seeds or a Temporary Object under TPM_RH_NULL. The command uses a TPM2B_PUBLIC as a template for the object to be created. The size of the unique field shall not be checked for consistency with the other object parameters. The command will create and load a Primary Object. The sensitive area is not returned.
*/
export class CreatePrimaryResponse extends TpmStructure
{
    /**
    * @param _handle handle of type TPM_HT_TRANSIENT for created Primary Object 
    * @param _outPublic the public portion of the created object 
    * @param _creationData contains a TPMT_CREATION_DATA 
    * @param _creationHash digest of creationData using nameAlg of outPublic 
    * @param _creationTicket ticket used by TPM2_CertifyCreation() to validate that the creation data was produced by the TPM 
    * @param _name the name of the created object
    */
    public constructor(
        public handle: TPM_HANDLE = null,
        public outPublic: TPMT_PUBLIC = null,
        public creationData: TPMS_CREATION_DATA = null,
        public creationHash: Buffer = null,
        public creationTicket: TPMT_TK_CREATION = null,
        public name: Buffer = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = this.handle.toTpm(buf, pos);
        pos = sizedToTpm(this.outPublic, buf, 2, pos);
        pos = this.creationData.toTpm(buf, pos);
        pos = toTpm2B(this.creationHash, buf, pos);
        pos = this.creationTicket.toTpm(buf, pos);
        pos = toTpm2B(this.name, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.handle, pos] = createFromTpm(TPM_HANDLE, buf, pos);
        [this.outPublic, pos] = sizedFromTpm(TPMT_PUBLIC, buf, 2, pos);
        [this.creationData, pos] = sizedFromTpm(TPMS_CREATION_DATA, buf, 2, pos);
        [this.creationHash, pos] = fromTpm2B(buf, pos);
        [this.creationTicket, pos] = createFromTpm(TPMT_TK_CREATION, buf, pos);
        [this.name, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; // class CreatePrimaryResponse

/**
* This command returns various information regarding the TPM and its current state.
*/
export class GetCapabilityResponse extends TpmStructure
{
    public constructor(
        /**
         * flag to indicate if there are more values of this type 
         */
        public moreData: number = null,
        /**
         * the capability data (One of TPML_ALG_PROPERTY, TPML_HANDLE, TPML_CCA, TPML_CC, TPML_CC, TPML_PCR_SELECTION, TPML_TAGGED_TPM_PROPERTY, TPML_TAGGED_PCR_PROPERTY, TPML_ECC_CURVE, TPML_TAGGED_POLICY)
         */
        public capabilityData: TPMU_CAPABILITIES = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm(this.moreData, buf, 1, pos);
        let capabilityDataCapability: TPM_CAP = 0;
        pos = toTpm(this.capabilityData.GetUnionSelector(), buf, 4, pos);
        pos = this.capabilityData.toTpm(buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.moreData, pos] = fromTpm(buf, 1, pos);
        let capabilityDataCapability: TPM_CAP = 0;
        [capabilityDataCapability, pos] = fromTpm(buf, 4, pos);
        this.capabilityData = createUnion<TPMU_CAPABILITIES>('TPMU_CAPABILITIES', capabilityDataCapability);
        pos = this.capabilityData.fromTpm(buf, pos);
        return pos;
    }
};

/**
 *  This command allows access to the public area of a loaded object.
 */
export class ReadPublicResponse extends TpmStructure
{
    public constructor(
        /**
         *  structure containing the public area of an object 
         */
        public outPublic: TPMT_PUBLIC = null,
        /**
         *  name of the object
         */
        public name: Buffer = null,
        /**
         *  the Qualified Name of the object
         */
        public qualifiedName: Buffer = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = sizedToTpm(this.outPublic, buf, 2, pos);
        pos = toTpm2B(this.name, buf, pos);
        pos = toTpm2B(this.qualifiedName, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.outPublic, pos] = sizedFromTpm(TPMT_PUBLIC, buf, 2, pos);
        [this.name, pos] = fromTpm2B(buf, pos);
        [this.qualifiedName, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; // class ReadPublicResponse

/**
* This command adds the last part of data, if any, to a hash/HMAC sequence and returns the result.
*/
export class SequenceCompleteResponse extends TpmStructure
{
    public constructor(
        /**
         * the returned HMAC or digest in a sized buffer 
         */
        public result: Buffer = null,
        /**
         * ticket indicating that the sequence of octets used to compute outDigest did not start with TPM_GENERATED_VALUE This is a NULL Ticket when the sequence is HMAC.
         */
        public validation: TPMT_TK_HASHCHECK = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm2B(this.result, buf, pos);
        pos = this.validation.toTpm(buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.result, pos] = fromTpm2B(buf, pos);
        [this.validation, pos] = createFromTpm(TPMT_TK_HASHCHECK, buf, pos);
        return pos;
    }
}; // class SequenceCompleteResponse

/**
* This command is used to start an authorization session using alternative methods of establishing the session key (sessionKey). The session key is then used to derive values used for authorization and for encrypting parameters.
*/
export class StartAuthSessionResponse extends TpmStructure
{
    public constructor(
        /**
        * handle for the newly created session 
        */
        public handle: TPM_HANDLE = null,
        /**
        * the initial nonce from the TPM, used in the computation of the sessionKey
        */
        public nonceTPM: Buffer = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = this.handle.toTpm(buf, pos);
        pos = toTpm2B(this.nonceTPM, buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.handle, pos] = createFromTpm(TPM_HANDLE, buf, pos);
        [this.nonceTPM, pos] = fromTpm2B(buf, pos);
        return pos;
    }
}; //class StartAuthSessionResponse

/**
* This command includes a secret-based authorization to a policy. The caller proves knowledge of the secret value using an authorization session using the authValue associated with authHandle. A password session, an HMAC session, or a policy session containing TPM2_PolicyAuthValue() or TPM2_PolicyPassword() will satisfy this requirement.
*/
export class PolicySecretResponse extends TpmStructure
{
    /**
    * This command includes a secret-based authorization to a policy. The caller proves knowledge of the secret value using an authorization session using the authValue associated with authHandle. A password session, an HMAC session, or a policy session containing TPM2_PolicyAuthValue() or TPM2_PolicyPassword() will satisfy this requirement.
    * 
    * @param _timeout implementation-specific time value used to indicate to the TPM when the ticket expires 
    * @param _policyTicket produced if the command succeeds and expiration in the command was non-zero ( See 23.2.5). This ticket will use the TPMT_ST_AUTH_SECRET structure tag
    */
    public constructor(
        public timeout: Buffer = null,
        public policyTicket: TPMT_TK_AUTH = null
    ) { super(); }

    /** TpmMarshaller method */
	toTpm(buf: Buffer, pos: number = 0): number
    {
        pos = toTpm2B(this.timeout, buf, pos);
        pos = this.policyTicket.toTpm(buf, pos);
        return pos;
    }

    /** TpmMarshaller method */
    fromTpm(buf: Buffer, pos: number = 0): number
    {
        [this.timeout, pos] = fromTpm2B(buf, pos);
        [this.policyTicket, pos] = createFromTpm(TPMT_TK_AUTH, buf, pos);
        return pos;
    }
}; // class PolicySecretResponse

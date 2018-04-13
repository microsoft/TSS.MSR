package tss.tpm;

import tss.*;
import java.util.*;

// -----------This is an auto-generated file: do not edit

//>>>
/**
* In general, response codes defined in TPM 2.0 Part 2 will be unmarshaling errors and will have the F (format) bit SET. Codes that are unique to TPM 2.0 Part 3 will have the F bit CLEAR but the V (version) attribute will be SET to indicate that it is a TPM 2.0 response code. See Response Code Details in TPM 2.0 Part 1.
*/
public final class TPM_RC extends TpmEnum<TPM_RC>
{
    // Values from enum _N are only intended to be used in case labels of a switch statement using the result of this.asEnum() method as the switch condition.
    // However, their Java names are identical to those of the constants defined in this class further below,
    // so for any other usage just prepend them with the TPM_RC. qualifier.
    public enum _N {
        SUCCESS,
        
        /**
        * defined for compatibility with TPM 1.2
        */
        BAD_TAG,
        
        /**
        * set for all format 0 response codes
        */
        RC_VER1,
        
        /**
        * TPM not initialized by TPM2_Startup or already initialized
        */
        INITIALIZE,
        
        /**
        * commands not being accepted because of a TPM failure NOTE This may be returned by TPM2_GetTestResult() as the testResult parameter.
        */
        FAILURE,
        
        /**
        * improper use of a sequence handle
        */
        SEQUENCE,
        
        /**
        * not currently used
        */
        PRIVATE,
        
        /**
        * not currently used
        */
        HMAC,
        
        /**
        * the command is disabled
        */
        DISABLED,
        
        /**
        * command failed because audit sequence required exclusivity
        */
        EXCLUSIVE,
        
        /**
        * authorization handle is not correct for command
        */
        AUTH_TYPE,
        
        /**
        * command requires an authorization session for handle and it is not present.
        */
        AUTH_MISSING,
        
        /**
        * policy failure in math operation or an invalid authPolicy value
        */
        POLICY,
        
        /**
        * PCR check fail
        */
        PCR,
        
        /**
        * PCR have changed since checked.
        */
        PCR_CHANGED,
        
        /**
        * for all commands other than TPM2_FieldUpgradeData(), this code indicates that the TPM is in field upgrade mode; for TPM2_FieldUpgradeData(), this code indicates that the TPM is not in field upgrade mode
        */
        UPGRADE,
        
        /**
        * context ID counter is at maximum.
        */
        TOO_MANY_CONTEXTS,
        
        /**
        * authValue or authPolicy is not available for selected entity.
        */
        AUTH_UNAVAILABLE,
        
        /**
        * a _TPM_Init and Startup(CLEAR) is required before the TPM can resume operation.
        */
        REBOOT,
        
        /**
        * the protection algorithms (hash and symmetric) are not reasonably balanced. The digest size of the hash must be larger than the key size of the symmetric algorithm.
        */
        UNBALANCED,
        
        /**
        * command commandSize value is inconsistent with contents of the command buffer; either the size is not the same as the octets loaded by the hardware interface layer or the value is not large enough to hold a command header
        */
        COMMAND_SIZE,
        
        /**
        * command code not supported
        */
        COMMAND_CODE,
        
        /**
        * the value of authorizationSize is out of range or the number of octets in the Authorization Area is greater than required
        */
        AUTHSIZE,
        
        /**
        * use of an authorization session with a context command or another command that cannot have an authorization session.
        */
        AUTH_CONTEXT,
        
        /**
        * NV offset+size is out of range.
        */
        NV_RANGE,
        
        /**
        * Requested allocation size is larger than allowed.
        */
        NV_SIZE,
        
        /**
        * NV access locked.
        */
        NV_LOCKED,
        
        /**
        * NV access authorization fails in command actions (this failure does not affect lockout.action)
        */
        NV_AUTHORIZATION,
        
        /**
        * an NV Index is used before being initialized or the state saved by TPM2_Shutdown(STATE) could not be restored
        */
        NV_UNINITIALIZED,
        
        /**
        * insufficient space for NV allocation
        */
        NV_SPACE,
        
        /**
        * NV Index or persistent object already defined
        */
        NV_DEFINED,
        
        /**
        * context in TPM2_ContextLoad() is not valid
        */
        BAD_CONTEXT,
        
        /**
        * cpHash value already set or not correct for use
        */
        CPHASH,
        
        /**
        * handle for parent is not a valid parent
        */
        PARENT,
        
        /**
        * some function needs testing.
        */
        NEEDS_TEST,
        
        /**
        * returned when an internal function cannot process a request due to an unspecified problem. This code is usually related to invalid parameters that are not properly filtered by the input unmarshaling code.
        */
        NO_RESULT,
        
        /**
        * the sensitive area did not unmarshal correctly after decryption this code is used in lieu of the other unmarshaling errors so that an attacker cannot determine where the unmarshaling error occurred
        */
        SENSITIVE,
        
        /**
        * largest version 1 code that is not a warning
        */
        RC_MAX_FM0,
        
        /**
        * This bit is SET in all format 1 response codes The codes in this group may have a value added to them to indicate the handle, session, or parameter to which they apply.
        */
        RC_FMT1,
        
        /**
        * asymmetric algorithm not supported or not correct
        */
        ASYMMETRIC,
        
        /**
        * inconsistent attributes
        */
        ATTRIBUTES,
        
        /**
        * hash algorithm not supported or not appropriate
        */
        HASH,
        
        /**
        * value is out of range or is not correct for the context
        */
        VALUE,
        
        /**
        * hierarchy is not enabled or is not correct for the use
        */
        HIERARCHY,
        
        /**
        * key size is not supported
        */
        KEY_SIZE,
        
        /**
        * mask generation function not supported
        */
        MGF,
        
        /**
        * mode of operation not supported
        */
        MODE,
        
        /**
        * the type of the value is not appropriate for the use
        */
        TYPE,
        
        /**
        * the handle is not correct for the use
        */
        HANDLE,
        
        /**
        * unsupported key derivation function or function not appropriate for use
        */
        KDF,
        
        /**
        * value was out of allowed range.
        */
        RANGE,
        
        /**
        * the authorization HMAC check failed and DA counter incremented
        */
        AUTH_FAIL,
        
        /**
        * invalid nonce size or nonce value mismatch
        */
        NONCE,
        
        /**
        * authorization requires assertion of PP
        */
        PP,
        
        /**
        * unsupported or incompatible scheme
        */
        SCHEME,
        
        /**
        * structure is the wrong size
        */
        SIZE,
        
        /**
        * unsupported symmetric algorithm or key size, or not appropriate for instance
        */
        SYMMETRIC,
        
        /**
        * incorrect structure tag
        */
        TAG,
        
        /**
        * union selector is incorrect
        */
        SELECTOR,
        
        /**
        * the TPM was unable to unmarshal a value because there were not enough octets in the input buffer
        */
        INSUFFICIENT,
        
        /**
        * the signature is not valid
        */
        SIGNATURE,
        
        /**
        * key fields are not compatible with the selected use
        */
        KEY,
        
        /**
        * a policy check failed
        */
        POLICY_FAIL,
        
        /**
        * integrity check failed
        */
        INTEGRITY,
        
        /**
        * invalid ticket
        */
        TICKET,
        
        /**
        * authorization failure without DA implications
        */
        BAD_AUTH,
        
        /**
        * the policy has expired
        */
        EXPIRED,
        
        /**
        * the commandCode in the policy is not the commandCode of the command or the command code in a policy command references a command that is not implemented
        */
        POLICY_CC,
        
        /**
        * public and sensitive portions of an object are not cryptographically bound
        */
        BINDING,
        
        /**
        * curve not supported
        */
        CURVE,
        
        /**
        * point is not on the required curve.
        */
        ECC_POINT,
        
        /**
        * set for warning response codes
        */
        RC_WARN,
        
        /**
        * gap for context ID is too large
        */
        CONTEXT_GAP,
        
        /**
        * out of memory for object contexts
        */
        OBJECT_MEMORY,
        
        /**
        * out of memory for session contexts
        */
        SESSION_MEMORY,
        
        /**
        * out of shared object/session memory or need space for internal operations
        */
        MEMORY,
        
        /**
        * out of session handles a session must be flushed before a new session may be created
        */
        SESSION_HANDLES,
        
        /**
        * out of object handles the handle space for objects is depleted and a reboot is required NOTE 1 This cannot occur on the reference implementation. NOTE 2 There is no reason why an implementation would implement a design that would deplete handle space. Platform specifications are encouraged to forbid it.
        */
        OBJECT_HANDLES,
        
        /**
        * bad locality
        */
        LOCALITY,
        
        /**
        * the TPM has suspended operation on the command; forward progress was made and the command may be retried See TPM 2.0 Part 1, Multi-tasking. NOTE This cannot occur on the reference implementation.
        */
        YIELDED,
        
        /**
        * the command was canceled
        */
        CANCELED,
        
        /**
        * TPM is performing self-tests
        */
        TESTING,
        
        /**
        * the 1st handle in the handle area references a transient object or session that is not loaded
        */
        REFERENCE_H0,
        
        /**
        * the 2nd handle in the handle area references a transient object or session that is not loaded
        */
        REFERENCE_H1,
        
        /**
        * the 3rd handle in the handle area references a transient object or session that is not loaded
        */
        REFERENCE_H2,
        
        /**
        * the 4th handle in the handle area references a transient object or session that is not loaded
        */
        REFERENCE_H3,
        
        /**
        * the 5th handle in the handle area references a transient object or session that is not loaded
        */
        REFERENCE_H4,
        
        /**
        * the 6th handle in the handle area references a transient object or session that is not loaded
        */
        REFERENCE_H5,
        
        /**
        * the 7th handle in the handle area references a transient object or session that is not loaded
        */
        REFERENCE_H6,
        
        /**
        * the 1st authorization session handle references a session that is not loaded
        */
        REFERENCE_S0,
        
        /**
        * the 2nd authorization session handle references a session that is not loaded
        */
        REFERENCE_S1,
        
        /**
        * the 3rd authorization session handle references a session that is not loaded
        */
        REFERENCE_S2,
        
        /**
        * the 4th authorization session handle references a session that is not loaded
        */
        REFERENCE_S3,
        
        /**
        * the 5th session handle references a session that is not loaded
        */
        REFERENCE_S4,
        
        /**
        * the 6th session handle references a session that is not loaded
        */
        REFERENCE_S5,
        
        /**
        * the 7th authorization session handle references a session that is not loaded
        */
        REFERENCE_S6,
        
        /**
        * the TPM is rate-limiting accesses to prevent wearout of NV
        */
        NV_RATE,
        
        /**
        * authorizations for objects subject to DA protection are not allowed at this time because the TPM is in DA lockout mode
        */
        LOCKOUT,
        
        /**
        * the TPM was not able to start the command
        */
        RETRY,
        
        /**
        * the command may require writing of NV and NV is not current accessible
        */
        NV_UNAVAILABLE,
        
        /**
        * this value is reserved and shall not be returned by the TPM
        */
        NOT_USED,
        
        /**
        * add to a handle-related error
        */
        H,
        
        /**
        * add to a parameter-related error
        */
        P,
        
        /**
        * add to a session-related error
        */
        S,
        
        /**
        * add to a parameter-, handle-, or session-related error
        */
        _1,
        
        /**
        * add to a parameter-, handle-, or session-related error
        */
        _2,
        
        /**
        * add to a parameter-, handle-, or session-related error
        */
        _3,
        
        /**
        * add to a parameter-, handle-, or session-related error
        */
        _4,
        
        /**
        * add to a parameter-, handle-, or session-related error
        */
        _5,
        
        /**
        * add to a parameter-, handle-, or session-related error
        */
        _6,
        
        /**
        * add to a parameter-, handle-, or session-related error
        */
        _7,
        
        /**
        * add to a parameter-related error
        */
        _8,
        
        /**
        * add to a parameter-related error
        */
        _9,
        
        /**
        * add to a parameter-related error
        */
        A,
        
        /**
        * add to a parameter-related error
        */
        B,
        
        /**
        * add to a parameter-related error
        */
        C,
        
        /**
        * add to a parameter-related error
        */
        D,
        
        /**
        * add to a parameter-related error
        */
        E,
        
        /**
        * add to a parameter-related error
        */
        F,
        
        /**
        * number mask
        */
        N_MASK,
        
        /**
        * Response buffer returned by the TPM is too short
        */
        TSS_TCP_BAD_HANDSHAKE_RESP,
        
        /**
        * Too old TCP server version
        */
        TSS_TCP_SERVER_TOO_OLD,
        
        /**
        * Bad ack from the TCP end point
        */
        TSS_TCP_BAD_ACK,
        
        /**
        * Wrong length of the response buffer returned by the TPM
        */
        TSS_TCP_BAD_RESP_LEN,
        
        /**
        * TPM2_Startup returned unexpected response code
        */
        TSS_TCP_UNEXPECTED_STARTUP_RESP,
        
        /**
        * Invalid size tag in the TPM response TCP packet
        */
        TSS_TCP_INVALID_SIZE_TAG,
        
        /**
        * Sending data to TPM failed
        */
        TSS_SEND_OP_FAILED,
        
        /**
        * Response buffer returned by the TPM is too short
        */
        TSS_RESP_BUF_TOO_SHORT,
        
        /**
        * Invalid tag in the response buffer returned by the TPM
        */
        TSS_RESP_BUF_INVALID_SESSION_TAG,
        
        /**
        * Windows TBS error TPM_E_COMMAND_BLOCKED
        */
        TBS_COMMAND_BLOCKED,
        
        /**
        * Windows TBS error TPM_E_INVALID_HANDLE
        */
        TBS_INVALID_HANDLE,
        
        /**
        * Windows TBS error TPM_E_DUPLICATE_VHANDLE
        */
        TBS_DUPLICATE_V_HANDLE,
        
        /**
        * Windows TBS error TPM_E_EMBEDDED_COMMAND_BLOCKED
        */
        TBS_EMBEDDED_COMMAND_BLOCKED,
        
        /**
        * Windows TBS error TPM_E_EMBEDDED_COMMAND_UNSUPPORTED
        */
        TBS_EMBEDDED_COMMAND_UNSUPPORTED,
        
        /**
        * Windows TBS returned success but empty response buffer
        */
        TBS_UNKNOWN_ERROR,
        
        /**
        * Windows TBS error TBS_E_INTERNAL_ERROR
        */
        TBS_INTERNAL_ERROR,
        
        /**
        * Windows TBS error TBS_E_BAD_PARAMETER
        */
        TBS_BAD_PARAMETER,
        
        /**
        * Windows TBS error TBS_E_INVALID_OUTPUT_POINTER
        */
        TBS_INVALID_OUTPUT_POINTER,
        
        /**
        * Windows TBS error TBS_E_INVALID_CONTEXT
        */
        TBS_INVALID_CONTEXT,
        
        /**
        * Windows TBS error TBS_E_INSUFFICIENT_BUFFER
        */
        TBS_INSUFFICIENT_BUFFER,
        
        /**
        * Windows TBS error TBS_E_IOERROR
        */
        TBS_IO_ERROR,
        
        /**
        * Windows TBS error TBS_E_INVALID_CONTEXT_PARAM
        */
        TBS_INVALID_CONTEXT_PARAM,
        
        /**
        * Windows TBS error TBS_E_SERVICE_NOT_RUNNING
        */
        TBS_SERVICE_NOT_RUNNING,
        
        /**
        * Windows TBS error TBS_E_TOO_MANY_TBS_CONTEXTS
        */
        TBS_TOO_MANY_CONTEXTS,
        
        /**
        * Windows TBS error TBS_E_TOO_MANY_TBS_RESOURCES
        */
        TBS_TOO_MANY_RESOURCES,
        
        /**
        * Windows TBS error TBS_E_SERVICE_START_PENDING
        */
        TBS_SERVICE_START_PENDING,
        
        /**
        * Windows TBS error TBS_E_PPI_NOT_SUPPORTED
        */
        TBS_PPI_NOT_SUPPORTED,
        
        /**
        * Windows TBS error TBS_E_COMMAND_CANCELED
        */
        TBS_COMMAND_CANCELED,
        
        /**
        * Windows TBS error TBS_E_BUFFER_TOO_LARGE
        */
        TBS_BUFFER_TOO_LARGE,
        
        /**
        * Windows TBS error TBS_E_TPM_NOT_FOUND
        */
        TBS_NOT_FOUND,
        
        /**
        * Windows TBS error TBS_E_SERVICE_DISABLED
        */
        TBS_SERVICE_DISABLED,
        
        /**
        * Windows TBS error TBS_E_ACCESS_DENIED
        */
        TBS_ACCESS_DENIED,
        
        /**
        * Windows TBS error TBS_E_PPI_FUNCTION_UNSUPPORTED
        */
        TBS_PPI_FUNCTION_NOT_SUPPORTED,
        
        /**
        * Windows TBS error TBS_E_OWNERAUTH_NOT_FOUND
        */
        TBS_OWNER_AUTH_NOT_FOUND
        
    }
    
    private static ValueMap<TPM_RC> _ValueMap = new ValueMap<TPM_RC>();
    
    public static final TPM_RC
    
        // These definitions provide mapping of the Java names of constants to their TPM values.
        SUCCESS = new TPM_RC(0x000, _N.SUCCESS),
        BAD_TAG = new TPM_RC(0x01E, _N.BAD_TAG),
        RC_VER1 = new TPM_RC(0x100, _N.RC_VER1),
        INITIALIZE = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x000, _N.INITIALIZE),
        FAILURE = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x001, _N.FAILURE),
        SEQUENCE = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x003, _N.SEQUENCE),
        PRIVATE = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x00B, _N.PRIVATE),
        HMAC = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x019, _N.HMAC),
        DISABLED = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x020, _N.DISABLED),
        EXCLUSIVE = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x021, _N.EXCLUSIVE),
        AUTH_TYPE = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x024, _N.AUTH_TYPE),
        AUTH_MISSING = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x025, _N.AUTH_MISSING),
        POLICY = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x026, _N.POLICY),
        PCR = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x027, _N.PCR),
        PCR_CHANGED = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x028, _N.PCR_CHANGED),
        UPGRADE = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x02D, _N.UPGRADE),
        TOO_MANY_CONTEXTS = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x02E, _N.TOO_MANY_CONTEXTS),
        AUTH_UNAVAILABLE = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x02F, _N.AUTH_UNAVAILABLE),
        REBOOT = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x030, _N.REBOOT),
        UNBALANCED = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x031, _N.UNBALANCED),
        COMMAND_SIZE = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x042, _N.COMMAND_SIZE),
        COMMAND_CODE = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x043, _N.COMMAND_CODE),
        AUTHSIZE = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x044, _N.AUTHSIZE),
        AUTH_CONTEXT = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x045, _N.AUTH_CONTEXT),
        NV_RANGE = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x046, _N.NV_RANGE),
        NV_SIZE = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x047, _N.NV_SIZE),
        NV_LOCKED = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x048, _N.NV_LOCKED),
        NV_AUTHORIZATION = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x049, _N.NV_AUTHORIZATION),
        NV_UNINITIALIZED = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x04A, _N.NV_UNINITIALIZED),
        NV_SPACE = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x04B, _N.NV_SPACE),
        NV_DEFINED = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x04C, _N.NV_DEFINED),
        BAD_CONTEXT = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x050, _N.BAD_CONTEXT),
        CPHASH = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x051, _N.CPHASH),
        PARENT = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x052, _N.PARENT),
        NEEDS_TEST = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x053, _N.NEEDS_TEST),
        NO_RESULT = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x054, _N.NO_RESULT),
        SENSITIVE = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x055, _N.SENSITIVE),
        RC_MAX_FM0 = new TPM_RC(TPM_RC.RC_VER1.toInt() + 0x07F, _N.RC_MAX_FM0),
        RC_FMT1 = new TPM_RC(0x080, _N.RC_FMT1),
        ASYMMETRIC = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x001, _N.ASYMMETRIC),
        ATTRIBUTES = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x002, _N.ATTRIBUTES),
        HASH = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x003, _N.HASH),
        VALUE = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x004, _N.VALUE),
        HIERARCHY = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x005, _N.HIERARCHY),
        KEY_SIZE = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x007, _N.KEY_SIZE),
        MGF = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x008, _N.MGF),
        MODE = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x009, _N.MODE),
        TYPE = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x00A, _N.TYPE),
        HANDLE = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x00B, _N.HANDLE),
        KDF = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x00C, _N.KDF),
        RANGE = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x00D, _N.RANGE),
        AUTH_FAIL = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x00E, _N.AUTH_FAIL),
        NONCE = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x00F, _N.NONCE),
        PP = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x010, _N.PP),
        SCHEME = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x012, _N.SCHEME),
        SIZE = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x015, _N.SIZE),
        SYMMETRIC = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x016, _N.SYMMETRIC),
        TAG = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x017, _N.TAG),
        SELECTOR = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x018, _N.SELECTOR),
        INSUFFICIENT = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x01A, _N.INSUFFICIENT),
        SIGNATURE = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x01B, _N.SIGNATURE),
        KEY = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x01C, _N.KEY),
        POLICY_FAIL = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x01D, _N.POLICY_FAIL),
        INTEGRITY = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x01F, _N.INTEGRITY),
        TICKET = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x020, _N.TICKET),
        BAD_AUTH = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x022, _N.BAD_AUTH),
        EXPIRED = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x023, _N.EXPIRED),
        POLICY_CC = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x024, _N.POLICY_CC),
        BINDING = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x025, _N.BINDING),
        CURVE = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x026, _N.CURVE),
        ECC_POINT = new TPM_RC(TPM_RC.RC_FMT1.toInt() + 0x027, _N.ECC_POINT),
        RC_WARN = new TPM_RC(0x900, _N.RC_WARN),
        CONTEXT_GAP = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x001, _N.CONTEXT_GAP),
        OBJECT_MEMORY = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x002, _N.OBJECT_MEMORY),
        SESSION_MEMORY = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x003, _N.SESSION_MEMORY),
        MEMORY = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x004, _N.MEMORY),
        SESSION_HANDLES = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x005, _N.SESSION_HANDLES),
        OBJECT_HANDLES = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x006, _N.OBJECT_HANDLES),
        LOCALITY = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x007, _N.LOCALITY),
        YIELDED = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x008, _N.YIELDED),
        CANCELED = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x009, _N.CANCELED),
        TESTING = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x00A, _N.TESTING),
        REFERENCE_H0 = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x010, _N.REFERENCE_H0),
        REFERENCE_H1 = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x011, _N.REFERENCE_H1),
        REFERENCE_H2 = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x012, _N.REFERENCE_H2),
        REFERENCE_H3 = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x013, _N.REFERENCE_H3),
        REFERENCE_H4 = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x014, _N.REFERENCE_H4),
        REFERENCE_H5 = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x015, _N.REFERENCE_H5),
        REFERENCE_H6 = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x016, _N.REFERENCE_H6),
        REFERENCE_S0 = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x018, _N.REFERENCE_S0),
        REFERENCE_S1 = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x019, _N.REFERENCE_S1),
        REFERENCE_S2 = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x01A, _N.REFERENCE_S2),
        REFERENCE_S3 = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x01B, _N.REFERENCE_S3),
        REFERENCE_S4 = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x01C, _N.REFERENCE_S4),
        REFERENCE_S5 = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x01D, _N.REFERENCE_S5),
        REFERENCE_S6 = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x01E, _N.REFERENCE_S6),
        NV_RATE = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x020, _N.NV_RATE),
        LOCKOUT = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x021, _N.LOCKOUT),
        RETRY = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x022, _N.RETRY),
        NV_UNAVAILABLE = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x023, _N.NV_UNAVAILABLE),
        NOT_USED = new TPM_RC(TPM_RC.RC_WARN.toInt() + 0x7F, _N.NOT_USED),
        H = new TPM_RC(0x000, _N.H, true),
        P = new TPM_RC(0x040, _N.P),
        S = new TPM_RC(0x800, _N.S),
        _1 = new TPM_RC(0x100, _N._1),
        _2 = new TPM_RC(0x200, _N._2),
        _3 = new TPM_RC(0x300, _N._3),
        _4 = new TPM_RC(0x400, _N._4),
        _5 = new TPM_RC(0x500, _N._5),
        _6 = new TPM_RC(0x600, _N._6),
        _7 = new TPM_RC(0x700, _N._7),
        _8 = new TPM_RC(0x800, _N._8),
        _9 = new TPM_RC(0x900, _N._9),
        A = new TPM_RC(0xA00, _N.A),
        B = new TPM_RC(0xB00, _N.B),
        C = new TPM_RC(0xC00, _N.C),
        D = new TPM_RC(0xD00, _N.D),
        E = new TPM_RC(0xE00, _N.E),
        F = new TPM_RC(0xF00, _N.F),
        N_MASK = new TPM_RC(0xF00, _N.N_MASK),
        TSS_TCP_BAD_HANDSHAKE_RESP = new TPM_RC(0x40280001, _N.TSS_TCP_BAD_HANDSHAKE_RESP),
        TSS_TCP_SERVER_TOO_OLD = new TPM_RC(0x40280002, _N.TSS_TCP_SERVER_TOO_OLD),
        TSS_TCP_BAD_ACK = new TPM_RC(0x40280003, _N.TSS_TCP_BAD_ACK),
        TSS_TCP_BAD_RESP_LEN = new TPM_RC(0x40280004, _N.TSS_TCP_BAD_RESP_LEN),
        TSS_TCP_UNEXPECTED_STARTUP_RESP = new TPM_RC(0x40280005, _N.TSS_TCP_UNEXPECTED_STARTUP_RESP),
        TSS_TCP_INVALID_SIZE_TAG = new TPM_RC(0x40280006, _N.TSS_TCP_INVALID_SIZE_TAG),
        TSS_SEND_OP_FAILED = new TPM_RC(0x40280011, _N.TSS_SEND_OP_FAILED),
        TSS_RESP_BUF_TOO_SHORT = new TPM_RC(0x40280021, _N.TSS_RESP_BUF_TOO_SHORT),
        TSS_RESP_BUF_INVALID_SESSION_TAG = new TPM_RC(0x40280022, _N.TSS_RESP_BUF_INVALID_SESSION_TAG),
        TBS_COMMAND_BLOCKED = new TPM_RC(0x80280400, _N.TBS_COMMAND_BLOCKED),
        TBS_INVALID_HANDLE = new TPM_RC(0x80280401, _N.TBS_INVALID_HANDLE),
        TBS_DUPLICATE_V_HANDLE = new TPM_RC(0x80280402, _N.TBS_DUPLICATE_V_HANDLE),
        TBS_EMBEDDED_COMMAND_BLOCKED = new TPM_RC(0x80280403, _N.TBS_EMBEDDED_COMMAND_BLOCKED),
        TBS_EMBEDDED_COMMAND_UNSUPPORTED = new TPM_RC(0x80280404, _N.TBS_EMBEDDED_COMMAND_UNSUPPORTED),
        TBS_UNKNOWN_ERROR = new TPM_RC(0x80284000, _N.TBS_UNKNOWN_ERROR),
        TBS_INTERNAL_ERROR = new TPM_RC(0x80284001, _N.TBS_INTERNAL_ERROR),
        TBS_BAD_PARAMETER = new TPM_RC(0x80284002, _N.TBS_BAD_PARAMETER),
        TBS_INVALID_OUTPUT_POINTER = new TPM_RC(0x80284003, _N.TBS_INVALID_OUTPUT_POINTER),
        TBS_INVALID_CONTEXT = new TPM_RC(0x80284004, _N.TBS_INVALID_CONTEXT),
        TBS_INSUFFICIENT_BUFFER = new TPM_RC(0x80284005, _N.TBS_INSUFFICIENT_BUFFER),
        TBS_IO_ERROR = new TPM_RC(0x80284006, _N.TBS_IO_ERROR),
        TBS_INVALID_CONTEXT_PARAM = new TPM_RC(0x80284007, _N.TBS_INVALID_CONTEXT_PARAM),
        TBS_SERVICE_NOT_RUNNING = new TPM_RC(0x80284008, _N.TBS_SERVICE_NOT_RUNNING),
        TBS_TOO_MANY_CONTEXTS = new TPM_RC(0x80284009, _N.TBS_TOO_MANY_CONTEXTS),
        TBS_TOO_MANY_RESOURCES = new TPM_RC(0x8028400A, _N.TBS_TOO_MANY_RESOURCES),
        TBS_SERVICE_START_PENDING = new TPM_RC(0x8028400B, _N.TBS_SERVICE_START_PENDING),
        TBS_PPI_NOT_SUPPORTED = new TPM_RC(0x8028400C, _N.TBS_PPI_NOT_SUPPORTED),
        TBS_COMMAND_CANCELED = new TPM_RC(0x8028400D, _N.TBS_COMMAND_CANCELED),
        TBS_BUFFER_TOO_LARGE = new TPM_RC(0x8028400E, _N.TBS_BUFFER_TOO_LARGE),
        TBS_NOT_FOUND = new TPM_RC(0x8028400F, _N.TBS_NOT_FOUND),
        TBS_SERVICE_DISABLED = new TPM_RC(0x80284010, _N.TBS_SERVICE_DISABLED),
        TBS_ACCESS_DENIED = new TPM_RC(0x80284012, _N.TBS_ACCESS_DENIED),
        TBS_PPI_FUNCTION_NOT_SUPPORTED = new TPM_RC(0x80284014, _N.TBS_PPI_FUNCTION_NOT_SUPPORTED),
        TBS_OWNER_AUTH_NOT_FOUND = new TPM_RC(0x80284015, _N.TBS_OWNER_AUTH_NOT_FOUND);
    public TPM_RC (int value) { super(value, _ValueMap); }
    
    public static TPM_RC fromInt (int value) { return TpmEnum.fromInt(value, _ValueMap, TPM_RC.class); }
    
    public static TPM_RC fromTpm (byte[] buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_RC.class); }
    
    public static TPM_RC fromTpm (InByteBuf buf) { return TpmEnum.fromTpm(buf, _ValueMap, TPM_RC.class); }
    
    public TPM_RC._N asEnum() { return (TPM_RC._N)NameAsEnum; }
    
    public static Collection<TPM_RC> values() { return _ValueMap.values(); }
    
    private TPM_RC (int value, _N nameAsEnum) { super(value, nameAsEnum, _ValueMap); }
    
    private TPM_RC (int value, _N nameAsEnum, boolean noConvFromInt) { super(value, nameAsEnum, null); }
    
    @Override
    protected int wireSize() { return 4; }
}

//<<<


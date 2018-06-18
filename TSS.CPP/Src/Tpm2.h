/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#pragma once
#include "TpmTypes.h"
#include "TpmCustomDefs.h"
#include "CryptoServices.h"
#include "TpmDevice.h"
#include "Policy.h"
#include "fdefs.h"

_TPMCPP_BEGIN

///<summary>Function type for user-installable callback</summary>
typedef void(*TpmResponseCallbackHandler)(std::vector<BYTE> tpmCommand,
        std::vector<BYTE> tpmResponse, void *context);

///<summary>Function type for user-installable RNG</summary>
typedef std::vector<BYTE>(*RandomNumberGenerator)(size_t numBytes);

///<summary>Tpm2 provides methods to communicate with an underlying TPM2.0 device. Async-
/// methods are provided via tpm.Async.*, and methods that change how Tpm2 behaves, or 
/// fetches Tpm2 state are prefaced with an underscore, e.g. tpm._GetLastError().</summary>
class _DLLEXP_ Tpm2 {
    public:
        ///<summary>Create a Tpm2 object without an underlying TPM-device.
        /// This can be used for obtaining CpHashes, etc.</summary>
        Tpm2();

        ///<summary>Connect this Tpm2 object to an underlying TpmDevice
        /// (e.g. TpmTcpDevice, or TpmTbsDevice).</summary>
        Tpm2(class TpmDevice& _device);

        ///<summary>Set or change the underlying TPM device.</summary>
        ~Tpm2();

        void _SetDevice(class TpmDevice& _device) {
            device = &_device;
        };

        ///<summary>Obtain the underlying TpmDevice.</summary>
        TpmDevice& _GetDevice() {
            return*device;
        }

        // Sessions: Note the 3-forms for associating sessions with the Tpm2-context.
        ///<summary>Invoke the next command with the session(s) provided. Either omit
        /// explicit sessions or use AUTH_SESSION::PWAP() to use a PWAP session with
        /// the auth-value in the associated handle.</summary>
        Tpm2& _Sessions(AUTH_SESSION& s);

        ///<summary>Invoke the next command with the session(s) provided. Either omit
        /// explicit sessions or use AUTH_SESSION::PWAP() to use a PWAP session with
        /// the auth-value in the associated handle.</summary>
        Tpm2& _Sessions(AUTH_SESSION& s1, AUTH_SESSION& s2);

        ///<summary>Invoke the next command with the session(s) provided. Either omit
        /// explicit sessions or use AUTH_SESSION::PWAP() to use a PWAP session with
        /// the auth-value in the associated handle.</summary>
        Tpm2& _Sessions(AUTH_SESSION& s1, AUTH_SESSION& s2, AUTH_SESSION& s3);

        ///<summary>Invoke the next command with the session(s) provided. Either omit
        /// explicit sessions or use AUTH_SESSION::PWAP() to use a PWAP session with
        /// the auth-value in the associated handle.</summary>
        Tpm2& _Sessions(std::vector<AUTH_SESSION> sessions);

        ///<summary>Invoke the next command with the session(s) provided. Either omit
        /// explicit sessions or use AUTH_SESSION::PWAP() to use a PWAP session with
        /// the auth-value in the associated handle.</summary>
        Tpm2& operator()(AUTH_SESSION& s) {
            return _Sessions(s);
        }

        ///<summary>Invoke the next command with the session(s) provided. Either omit
        /// explicit sessions or use AUTH_SESSION::PWAP() to use a PWAP session with
        /// the auth-value in the associated handle.</summary>
        Tpm2& operator()(AUTH_SESSION& s1, AUTH_SESSION& s2) {
            return _Sessions(s1, s2);
        }

        ///<summary>Invoke the next command with the session(s) provided. Either omit
        /// explicit sessions or use AUTH_SESSION::PWAP() to use a PWAP session with
        /// the auth-value in the associated handle.</summary>
        Tpm2& operator()(AUTH_SESSION& s1, AUTH_SESSION& s2, AUTH_SESSION& s3) {
            return _Sessions(s1, s2, s3);
        }

        ///<summary>Get the string representation of an enum-value or bitfield value.</summary>
        template<class C>
        static string GetEnumString(const C& enumVal) {
            return GetEnumString((UINT32)enumVal, typeid(enumVal).name());
        }

        // Error-handling

        ///<summary>Strips the parameter-error info from the command code to give a
        /// "bare" error code.</summary>
        static TPM_RC ResponseCodeFromTpmError(TPM_RC _decoratedReponseCode);

        ///<summary>The next operation can succeed or fail without an exception being generated.
        /// Check _GetLastError() for status.</summary>
        Tpm2& _AllowErrors() {
            AllowErrors = true;
            return *this;
        }

        ///<summary>The next operation is expected to fail with a specific error: an
        /// exception is thrown if the command succeeds, or an unexpected error is seen.</summary>
        Tpm2& _ExpectError(TPM_RC expectedError) {
            AllowErrors = true;
            ExpectedError = expectedError;
            return *this;
        }

        ///<summary>An exception is thrown if the next operation succeeds.</summary>
        Tpm2& _DemandError() {
            DemandError = true;
            return *this;
        }

        ///<summary>Get the response code for the last command (might be TPM_RC::SUCCESS).</summary>
        TPM_RC _GetLastError() {
            return LastResponseCode;
        }

        ///<summary>Get the response code for the last command in string-form.</summary>
        string _GetLastErrorAsString() {
            return Tpm2::GetEnumString(LastResponseCode);
        }

        ///<summary>Did the last TPM operation succeed?</summary>
        bool _LastOperationSucceeded() {
            return LastResponseCode == TPM_RC::SUCCESS;
        }

        ///<summary>Install a callback to be invoked after the TPM command has been submitted
        /// and the response received. Set to NULL to disable callbacks.</summary>
        void _SetResponseCallback(TpmResponseCallbackHandler handler, void *context) {
            responseCallback = handler;
            responseCallbackContext = context;
        }

        ///<summary>Set this Tpm2 instance to use a new RNG (for session nonces, etc.)</summary>
        void _SetRNG(RandomNumberGenerator _rng) {
            rng = _rng;
        }

        ///<summary>Get random bytes from NON-TPM rng (this is *not* tpm.GetRandom()).
        /// Fetches data from the default or programmer-installed SW-RNG.</summary>
        std::vector<BYTE> _GetRandLocal(UINT32 numBytes) {
            return GetRandomBytes(numBytes);
        }

        ///<summary>The CpHash of the next command is placed in *hashToGet. Note that the
        /// algorithm must be set in hashToGet, and the command will NOT be invoked</summary>
        Tpm2& _GetCpHash(TPMT_HA *hashToGet) {
            CpHash = hashToGet;
            return *this;
        }

        // Audit support

        ///<summary>Sets the hash-alg and starting value to be used in _Audit().</summary>
        void _StartAudit(const TPMT_HA& startVal);

        ///<summary>Stops this Tpm2 instance from maintaining the command audit hash.</summary>
        void _EndAudit();

        ///<summary>Instructs Tpm2 to add the hash of this command to the local log.
        /// The local log will typically be compared to a TPM generated log to ensure
        /// that a command sequence was executed as intended.</summary>
        Tpm2& _Audit() {
            AuditThisCommand = true;
            return *this;
        }

        ///<summary>Get the audit hash (all commands tagged_Audit() since _StartCommandAudit()
        /// was called.</summary>
        TPMT_HA _GetAuditHash();

        ///<summary>The _Admin handles are initialized to the relevant TPM-defined
        /// platform handles.  The programmer (or ports of this library) may also set
        /// the associated auth-value for these handles. Note the association of the
        /// admin-handles to a Tpm2-instance: this allows an application program to
        /// talk to multiple remote/local TPMs with different auth-values</summary>
        class TPM_HANDLE _AdminOwner, _AdminEndorsement, _AdminPlatform, _AdminLockout;

    protected:
        ///<summary>Fetches random bytes.  The default RNG is used unless _SetRng()
        ///has been used to install a custom source.</summary>
        std::vector<BYTE> GetRandomBytes(size_t _numBytes);
        void Init();

        void Dispatch(TPM_CC commandCode, 
                      TpmTypeId responseStruct,
                      class TpmStructureBase *req,
                      class TpmStructureBase *resp);

        bool DispatchOut(TPM_CC _command, TpmStructureBase *_req);
        bool DispatchIn(TPM_CC _command, TpmTypeId responseStruct, TpmStructureBase *_resp);

        void GetAuthSessions(std::vector<BYTE>& bufToFill, 
                             TPM_CC command, 
                             std::vector<BYTE> commandParms,
                             int numAuthHandles,
                             std::vector<TPM_HANDLE *> handles);

        bool ProcessResponseSessions(std::vector<BYTE>& sessionBuf,
                                     TPM_CC command,
                                     TPM_RC response,
                                     std::vector<BYTE> respBufNoHandles,
                                     std::vector<TPM_HANDLE *> inHandles);

        void RollNonces();
        void DoParmEncryption(TpmStructureBase *str, ByteVec& parmBuffer, bool directionIn);
        void DebugPrint(const string& message);
        std::vector<BYTE> CalcHMAC(std::vector<BYTE>& commandParms, TPM_HANDLE h);

        ///<summary>Automatically set the name and AuthVal in the calling programs handles.</summary>
        void UpdateHandleDataCommand(TPM_CC cc, TpmStructureBase *command);
        void UpdateHandleDataResponse(TPM_CC cc, TpmStructureBase *reponse);
        static void GetHandles(TpmStructureBase *request,
                               StructMarshallInfo *marshallInfo,
                               vector<TPM_HANDLE *>& handles);

        // Encrypting session stuff
        void PrepareParmEncryptionSessions();
        ByteVec NonceTpmDec, NonceTpmEnc;
        void CheckParamEncSessCandidate(AUTH_SESSION *candidate, TPMA_SESSION directionFlag);

        static string GetEnumString(UINT32 value, const string& typeName);

        // Per-invocation state
        bool AllowErrors = false, DemandError = false;
        TPM_RC ExpectedError = TPM_RC::SUCCESS;
        std::vector<AUTH_SESSION *> Sessions;
        TPMT_HA *CpHash = NULL;
        bool AuditThisCommand = false;
        void ClearInvocationState() {
            AllowErrors = false;
            DemandError = false;
            ExpectedError = TPM_RC::SUCCESS;
            CpHash = NULL;
            AuditThisCommand = false;
        }

        // State passed from tpmOut to tpmIn for async command processing
        TPM_CC commandBeingProcessed;
        bool phaseTwoExpected = false;
        std::vector<BYTE> respBuf;
        std::vector<BYTE> lastCommandBuf;
        bool sessions = false;
        int numSessions;
        TPM_ST sessionsTag;
        unsigned int authHandleCount;

        // Command input handles. *Note* the handle must survive until the command is
        // complete so that we can apply the new name and authVal (certain commands).
        vector<TPM_HANDLE *> inHandles;

        // The following are calculated from the input parms. If the command
        // succeeds then the name and auth are applied to the handle.
        vector<BYTE> objectInName;
        vector<BYTE> objectInAuth;

        // Other state
        class TpmDevice *device;
        TPM_RC LastResponseCode = TPM_RC::SUCCESS;
        TpmResponseCallbackHandler responseCallback = NULL;
        void *responseCallbackContext = NULL;
        RandomNumberGenerator rng = NULL;
        AUTH_SESSION *EncSession = NULL, *DecSession = NULL;
        TPMT_HA CommandAuditHash;
        TPMT_HA LastCommandAuditCpHash;

    public:
        // Overloaded TPM commands
        /// <summary>This overloaded TPM-command is used to start an unseeded and unbound
        /// HMAC or policy authorization session.</summary>
        AUTH_SESSION StartAuthSession
        (
            TPM_SE sessionType,
            TPM_ALG_ID authHash
        );

        ///<summary>Start a TPM auth-session for a non-bound, non-seeded session.</summary>
        AUTH_SESSION StartAuthSession(TPM_SE sessionType, 
                                      TPM_ALG_ID authHash,
                                      TPMA_SESSION sessionAttributes,
                                      TPMT_SYM_DEF symmAlg);
        ///<summary>Start a TPM auth-session returning an AUTH_SESSION object (all options).</summary>
        AUTH_SESSION StartAuthSession(TPM_HANDLE saltKey,
                                      TPM_HANDLE bindKey,
                                      TPM_SE sessionType,
                                      TPM_ALG_ID authHash,
                                      TPMA_SESSION sessionAttributes,
                                      TPMT_SYM_DEF symDef,
                                      std::vector<BYTE> salt,
                                      std::vector<BYTE> encryptedSalt);

// Auto-generated TPM function prototypes
#include "Tpm2_hdr_autogen.h"

    public:
        AsyncMethods Async;
};

_TPMCPP_END

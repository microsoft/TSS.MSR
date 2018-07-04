/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"
#include "MarshallInternal.h"
#include "Tpm2.h"

_TPMCPP_BEGIN

typedef std::vector<BYTE> ByteVec;
AUTH_SESSION PwapSession = AUTH_SESSION::PWAP();

bool IsFmt1(TPM_RC responseCode)
{
    return ((UINT32)responseCode & 0x80) != 0;
}

TPM_RC Tpm2::ResponseCodeFromTpmError(TPM_RC responseCode)
{
    return (TPM_RC)((UINT32)responseCode & (IsFmt1(responseCode) ? 0xBFU : 0x97FU));
}

Tpm2::Tpm2(class TpmDevice& _device) : device(&_device), Async(*this)
{
    Init();
}

Tpm2::Tpm2() : Async(*this)
{
    Init();
}

void Tpm2::Init()
{
    _AdminPlatform = TPM_HANDLE::FromReservedHandle(TPM_RH::PLATFORM);
    _AdminOwner = TPM_HANDLE::FromReservedHandle(TPM_RH::OWNER);
    _AdminEndorsement = TPM_HANDLE::FromReservedHandle(TPM_RH::ENDORSEMENT);
    _AdminLockout = TPM_HANDLE::FromReservedHandle(TPM_RH::LOCKOUT);
    // TODO: Later ports of this library might want to fetch the OS-defined
    // auth-values for the above handles automatically.
    CommandAuditHash.hashAlg = TPM_ALG_ID::_NULL;
}

Tpm2::~Tpm2()
{
}

string Tpm2::GetEnumString(UINT32 enumVal, const string& typeName)
{
    auto inf = TheTypeMap.GetStructMarshallInfo(typeName);
    string res = "";

    // Simple enumeration
    if (inf->EnumNames.size() != 0) {
        if (inf->EnumNames.count(enumVal) != 0) {
            res = inf->EnumNames[enumVal];
        }
    }

    // Bitfield
    if (inf->BitNames.size() != 0) {
        for (UINT32 i = 0; i < inf->BitNames.size(); i++) {
            UINT32 bitVal = 1 << i;

            if ((enumVal & bitVal) != 0) {
                if (res != "") {
                    res += " | ";
                }

                res += inf->BitNames[i];
            }
        }
    }

    if (res == "") {
        res = "?";
    }

    return res;
}

void Tpm2::RollNonces()
{
    for (size_t j = 0; j < Sessions.size(); j++) {
        if (Sessions[j]->IsPWAP()) {
            continue;
        }

        // Roll the nonceCaller
        ByteVec newNonceCaller = GetRandomBytes(Sessions[j]->NonceCaller.size());
        Sessions[j]->NonceCaller = newNonceCaller;
    }

}

void Tpm2::GetAuthSessions(std::vector<BYTE>& buf,
                           TPM_CC command,
                           std::vector<BYTE> commandBuf,
                           int numAuthHandles,
                           std::vector<TPM_HANDLE *> handles)
{
    int numExplicitSessions = Sessions.size();

    if (numExplicitSessions < numAuthHandles) {
        throw runtime_error("Not enough explicit sessions for number of handles that need authorization");
    }

    OutByteBuf commandWithNames;
    bool haveNonPwap = false;

    for (int j = 0; j < numExplicitSessions; j++)if (!Sessions[j]->IsPWAP()) {
            haveNonPwap = true;
        }

    // We only need the names if we have at least one non-PWAP session
    if (haveNonPwap) {
        commandWithNames << (UINT32)command;

        for (auto i = handles.begin(); i != handles.end(); i++) {
            commandWithNames << (*i)->GetName();
        }

        commandWithNames << commandBuf;
    }

    TPMS_AUTH_COMMAND s;
    OutByteBuf t;
    TPM_HANDLE tempHandle;
    TPM_HANDLE *associatedHandle;

    for (int j = 0; j < numExplicitSessions; j++) {
        AUTH_SESSION& sess = *Sessions[j];

        // PWAPs are easy.
        if (sess.IsPWAP()) {

            tempHandle = TPM_HANDLE::FromReservedHandle(TPM_RH::RS_PW);
            s.sessionHandle = tempHandle;
            s.nonce.resize(0);
            s.hmac = handles[j]->GetAuth();
            s.sessionAttributes = TPMA_SESSION::continueSession;
            t << s;
            continue;
        }

        // Else an HMAC, policy, encrypting session prepare the session and session-hash.
        associatedHandle = NULL;

        if (j < numAuthHandles) {
            sess.SetAuthValue(handles[j]->GetAuth());
            associatedHandle = handles[j];
        }

        s.nonce = sess.NonceCaller;
        s.sessionHandle = sess.handle;
        auto parmHash = CryptoServices::Hash(sess.HashAlg, commandWithNames.GetBuf());
        s.sessionAttributes = Sessions[j]->SessionAttributes;

        if ((sess.SessionType == TPM_SE::HMAC) || sess.ForceHmacOnPolicySession) {
            s.hmac = sess.GetAuthHmac(parmHash, true, NonceTpmDec, NonceTpmEnc, associatedHandle);
        } else if (sess.IncludePlaintextPasswordInPolicySession) {
            s.hmac = handles[j]->GetAuth();
        }

        // And serialize it so that it can be added to the command-buffer
        t << s;
    }

    buf = t.GetBuf();
    return;
}

bool Tpm2::ProcessResponseSessions(std::vector<BYTE>& sessionBufVec,
                                   TPM_CC command,
                                   TPM_RC reponse,
                                   std::vector<BYTE> respBufNoHandles,
                                   std::vector<TPM_HANDLE *> inHandles)
{
    OutByteBuf rpBuf;
    rpBuf << (UINT32)reponse << (UINT32)command << respBufNoHandles;
    InByteBuf sessionBuf(sessionBufVec);
    AUTHResponse authResponse;
    OutByteBuf t;
    TPM_HANDLE tempHandle;
    TPM_HANDLE *associatedHandle;
    ByteVec nullVec;

    for (size_t j = 0; j < Sessions.size(); j++) {
        AUTH_SESSION& sess = *Sessions[j];
        sessionBuf >> authResponse;

        if (sess.IsPWAP()) {
            AUTHResponse r;

            if ((r.nonce != nullVec) || (r.hmac != nullVec)) {
                Sessions.clear();
                throw runtime_error("Bad value in PWAP session response");
            }

            continue;
        }

        associatedHandle = NULL;

        if (j < inHandles.size()) {
            sess.SetAuthValue(inHandles[j]->GetAuth());
            associatedHandle = inHandles[j];
        }

        auto respHash = CryptoServices::Hash(sess.HashAlg, rpBuf.GetBuf());
        // Update our session data based on what the TPM just told us
        sess.NonceTpm = authResponse.nonce;
        sess.SessionAttributes = authResponse.sessionAttributes;

        if (sess.SessionType == TPM_SE::HMAC) {
            auto expectedHmac = sess.GetAuthHmac(respHash, 
                                                 false,
                                                 NonceTpmDec,
                                                 NonceTpmEnc,
                                                 associatedHandle);
            if (expectedHmac != authResponse.hmac) {
                return false;
            }
        }
    }

    if (!sessionBuf.eof()) {
        return false;
    }

    return true;
}

///<summary>Sets the handles array to point to the handle objects in the request</summary>
void Tpm2::GetHandles(TpmStructureBase *request, 
                      StructMarshallInfo *marshallInfo,
                      vector<TPM_HANDLE *>& handles)
{
    if (request == NULL) {
        return;
    }

    for (int j = 0; j < marshallInfo->HandleCount; j++) {
        int x;
        TpmStructureBase *y;
        TPM_HANDLE *h = (TPM_HANDLE *)request->ElementInfo(j, -1, x, y, -1);
        handles.push_back(h);
    }

    return;
}

///<summary> Send a TPM command to the underlying TPM device.  TPM errors are
/// propagated as exceptions by default </summary>
void Tpm2::Dispatch(TPM_CC _command,
                    TpmTypeId respTypeId,
                    class TpmStructureBase *_req,
                    class TpmStructureBase *_resp)
{
    for (;;)
    {
        bool processPhaseTwo = DispatchOut(_command, _req);
        if (!processPhaseTwo ||
            DispatchIn(_command, respTypeId, _resp))
        {
            break;
        }
        Sleep(1000);
    }
}

bool Tpm2::DispatchOut(TPM_CC _command, TpmStructureBase *_req)
{
    if (phaseTwoExpected) {
        throw runtime_error("A TPM command has been dispatched before the previous async-command has been processed.  Call Cancel() if you need to abort");
    }

    OutByteBuf reqBuf;
    StructMarshallInfo *reqInfo = NULL;
    vector<byte> commBuf;
    int handleAreaSize = 0;

    authHandleCount = 0;

    if (_req != NULL) {
        reqInfo = TheTypeMap.GetStructMarshallInfo(_req->GetTypeId());
        handleAreaSize = reqInfo->HandleCount * 4;
        authHandleCount = reqInfo->AuthHandleCount;
    }

    sessions = false;
    numSessions = 0;
    sessionsTag = TPM_ST::NO_SESSIONS;

    vector<BYTE> sessionBuf;

    // AuthValues are always retrieved from the object handle. If no explicit sessions
    // are provided then the auth-value is retrieved for all sessions that require auth
    // and PWAP is used.
    //
    // If explicit sessions are provided then the explicit sessions are used (either a
    // session handle, or a AUTH_SESSION::PWAP(), if PWAP is desired for a handle).
    // If there are not enough explicit sessions, then an error is generated.

    int numExplicitSessions = Sessions.size();

    inHandles.clear();
    GetHandles(_req, reqInfo, inHandles);

    if (numExplicitSessions != 0) {
        if (authHandleCount > Sessions.size()) {
            throw runtime_error("Too few explicit sessions");
        }
    }

    // Do we have sessions of either type?
    if (authHandleCount != 0 || numExplicitSessions != 0) {
        sessions = true;
        sessionsTag = TPM_ST::SESSIONS;
    }

    int commandLen;

    if (_req != NULL) {
        commBuf = _req->ToBuf();
    }

    commandLen = 10 + commBuf.size();

    if (sessions) {
        RollNonces();
        PrepareParmEncryptionSessions();

        DoParmEncryption(_req, commBuf, true);

        // No explicit sessions, but auth needed for one or more handles we fabricate
        // some PWAP sessions
        if (authHandleCount > 0 && numExplicitSessions == 0) {
            Sessions.resize(authHandleCount);

            for (size_t j = 0; j < authHandleCount; j++) {
                Sessions[j] = &PwapSession;
            }

            numExplicitSessions = authHandleCount;
            numSessions = authHandleCount;
        }

        // Then we can process the real or fabricated sessions
        numSessions = Sessions.size();
        vector<BYTE> commBufNoHandles = VectorSlice(commBuf, handleAreaSize,
                                                    commBuf.size() - handleAreaSize);
        GetAuthSessions(sessionBuf, _command, commBufNoHandles, authHandleCount, inHandles);
        commandLen += sessionBuf.size() + 4;
    }

    // Construct the command buffer

    OutByteBuf outCommand;

    // First the standard header
    outCommand << (UINT16)sessionsTag << (UINT32)commandLen << (UINT32)_command;

    // Add the handes (if any).
    outCommand.AddSlice(commBuf, 0, handleAreaSize);

    // Add the sessionLen + sessions (if any).
    if (sessions) {
        outCommand << (UINT32)sessionBuf.size() << sessionBuf;
    }

    // Add the rest of the command (handles already added)
    outCommand.AddSlice(commBuf, handleAreaSize, commBuf.size() - handleAreaSize);

    // Command buffer complete

    if ((CpHash != NULL) || AuditThisCommand  ) {
        // Non-NULL CpHash indicates that the caller wants the CpHash, 
        // but does not want the command invoked.
        OutByteBuf cpBuf;
        cpBuf << ToIntegral(_command);

        for (auto i = inHandles.begin(); i != inHandles.end(); i++) {
            cpBuf << (*i)->GetName();
        }

        cpBuf.AddSlice(commBuf, handleAreaSize, commBuf.size() - handleAreaSize);

        if (CpHash != NULL) {
            CpHash->digest = CryptoServices::Hash(CpHash->hashAlg, cpBuf.GetBuf());
            ClearInvocationState();
            phaseTwoExpected = false;
            CpHash = NULL;
            return false;
        }

        // Else we are auditing this command
        LastCommandAuditCpHash.digest = CryptoServices::Hash(CommandAuditHash.hashAlg, cpBuf.GetBuf());
        cout << "CpHash: " << LastCommandAuditCpHash.digest << endl;
    }

    // Tpms can be used for operations that do not directly involve the TPM: e.g. getting
    // a CpHash, but if we get to thhis point we really need a TPM...

    if (device == NULL) {
        throw runtime_error("No TPM device.  Use _SetDevice() or the constructor that takes a TpmDevice*");
    }

    // And send it to the TPM
    std::vector<BYTE>& commandBuf = outCommand.GetBuf();
    vector<BYTE> tempRespBuf;

    device->DispatchCommand(commandBuf);
    lastCommandBuf = commandBuf;

    UpdateHandleDataCommand(_command, _req);

    respBuf = tempRespBuf;
    phaseTwoExpected = true;
    commandBeingProcessed = _command;

    return true;
}

bool Tpm2::DispatchIn(TPM_CC _command, TpmTypeId responseStruct, TpmStructureBase *_resp)
{
    if (!phaseTwoExpected) {
        phaseTwoExpected = false;
        throw runtime_error("Async command completion with no outstanding command");
    }

    if (commandBeingProcessed != _command) {
        throw runtime_error("Async command completion does not match command being processed");
    }

    phaseTwoExpected = false;

    device->GetResponse(respBuf);
    StructMarshallInfo *respInfo = TheTypeMap.GetStructMarshallInfo(_resp->GetTypeId());

    // Process post-command callback

    if (responseCallback != NULL)
        (*responseCallback)(lastCommandBuf, respBuf, responseCallbackContext);

    // Parse the response buffer
    InByteBuf inStream(respBuf);
    TPM_ST respTag;
    UINT32 respLen;
    TPM_RC respCode;

    // Get the standard response header
    inStream >> (UINT16&)respTag >> respLen >> (UINT32&)respCode;

    // Figure out whether an exception must be generated based on the expected errors.
    // This is rather convoluted logic depending on:
    //          _DemandError()  - exception if command succeeded
    //          _AllowError() - no exception, regardless of success or failure
    //          _ExpectError(TPM_RC) - exception if not "expected error."

    bool throwException = false;
    string errorMessage = "";
    TPM_RC errorCode = ResponseCodeFromTpmError(respCode);
    LastResponseCode = errorCode;

    if (respCode == TPM_RC::RETRY)
        return false;

    if (respCode == TPM_RC::SUCCESS && DemandError) {
        inStream.TheRest();
        errorMessage = "A TPM error was demanded but the function succeeded";
        throwException = true;
        goto outOfHere;
    }

    if ((respCode == TPM_RC::SUCCESS) && (ExpectedError != TPM_RC::SUCCESS)) {
        // We succeeded, but the caller called _ExpectError(someOtherError)
        errorMessage = "_ExpectError(...) was called but command succeeded";
        throwException = true;
        goto outOfHere;

    }

    if (respCode != TPM_RC::SUCCESS) {
        // We demanded an error, so OK that we have one.
        if (DemandError) {
            goto outOfHere;
        }

        // Else we have an error. We generate an exception if either AllowErrors is false.
        if (!AllowErrors) {
            // An error was not expected
            StructMarshallInfo *inf = TypeMap[TpmTypeId::TPM_RC_ID];
            string err = inf->EnumNames[(UINT32)errorCode];
            errorMessage = "TPM Error - TPM_RC::" + err;
            throwException = true;
            goto outOfHere;
        }

        // Final case: we have an error, but this is OK as long as it is the "expected" error.
        if ((errorCode != ExpectedError) && (ExpectedError != TPM_RC::SUCCESS)) {
            StructMarshallInfo *inf = TypeMap[TpmTypeId::TPM_RC_ID];
            string err = inf->EnumNames[(UINT32)errorCode];
            string expected = inf->EnumNames[(UINT32)ExpectedError];
            errorMessage = "TPM Error was not the ExpectError() value.  (Expected=TPM_RC::" + 
                           expected + ", Actual=TPM_RC::" + err + ")";
            throwException = true;
            goto outOfHere;
        }
    }

outOfHere:
    // Keep a copy of this before we clean out invocation state
    bool auditCommand = AuditThisCommand;
    ClearInvocationState();

    if (throwException) {
        Sessions.clear();
        DebugPrint(errorMessage);
        throw system_error((UINT32) errorCode, system_category(), errorMessage);
    }

    // Even if we did not throw an exception there is nothing more to do if we have an error.
    if (errorCode != TPM_RC::SUCCESS) {
        Sessions.clear();
        return true;
    }

    // Else the command succeeded, so we can process the response buffer
    if (sessionsTag != respTag) {
        throw runtime_error("unexpected response tag");
    }

    // Get the handles
    TPM_HANDLE outHandleValues[8];
    int numOutHandles = respInfo->HandleCount;
    OutByteBuf tempParmBuf;

    for (int j = 0; j < numOutHandles; j++) {
        UINT32 tempHandle;
        inStream >> tempHandle;
        outHandleValues[j].handle = tempHandle;
        // Need to put the handles in a vec so that we can unmarshall
        tempParmBuf << outHandleValues[j];
    }

    // If there are no sessions then the rest of the response structure is here.
    // If there are sessions then there is a size parm for the rest of the response
    // struct followed by the rest of the response.
    ByteVec outSessionsArea;
    vector<BYTE> respNoHandles;

    if (sessions) {
        UINT32 restOfInParmSize;
        inStream >> restOfInParmSize;
        respNoHandles = inStream.GetSlice(restOfInParmSize);
        tempParmBuf << respNoHandles;
        outSessionsArea = inStream.TheRest();
    } else {
        respNoHandles = inStream.TheRest();
        tempParmBuf << respNoHandles;
    }

    if (auditCommand) {
        if (CommandAuditHash.hashAlg == TPM_ALG_ID::_NULL) {
            throw runtime_error("No command audit digest set");
        }

        OutByteBuf respBuf;
        respBuf << ToIntegral(TPM_RC::SUCCESS) << ToIntegral(_command) << respNoHandles;
        auto rpHash = TPMT_HA::FromHashOfData(CommandAuditHash.hashAlg, respBuf.GetBuf());
        CommandAuditHash.Extend(Helpers::Concatenate(LastCommandAuditCpHash.digest, rpHash.digest));
    }

    bool sessionsOk = ProcessResponseSessions(outSessionsArea, 
                                              _command,
                                              respCode,
                                              respNoHandles,
                                              inHandles);
    if (!sessionsOk) {
        throw runtime_error("Response session failure");
    }

    // Now we can unmarshall the (possibly previously fragmented) response byte stream.
    DoParmEncryption(_resp, tempParmBuf.GetBuf(), false);
    _resp->FromBuf(tempParmBuf.GetBuf());

    // If there is a returned handle get a pointer to it. It is always the 
    // first element in the structure.
    _ASSERT(numOutHandles < 2);

    UpdateHandleDataResponse(_command, _resp);

    // And finally process the response sessions
    Sessions.clear();
    return true;
}

void Tpm2::UpdateHandleDataCommand(TPM_CC cc, TpmStructureBase *command)
{
    // This function is called with the plaintext input parameters. TSS.C++ tries
    // to keep the handle authValue and name correct, but the values are not applied
    // to the handle unless the operation succeeds. So mostly this function records
    // the names and new auth-values for later processing in UpdateHandleDataResponse.
    
    // TODO: This is a bit of a mess.  the inHandles array points to handles in the
    // corresponding command input struct, rather than to the application handles.
    // The AuthVal and names are copied over, but we can't update the handles in
    // the app-space when a ChangeAuth is seen, but we MUST (at least) update the
    // authValue in the library handle so that the session HMAC is OK when the
    // authValue changes in the command.

    objectInName.clear();

    switch (cc) {
        case TPM_CC::HierarchyChangeAuth: {
            auto c0 = dynamic_cast<TPM2_HierarchyChangeAuth_REQUEST *>(command);
            // Note - Change this so that session will work
            c0->authHandle.SetAuth(c0->newAuth);
            objectInAuth = c0->newAuth;
            return;
        }

        case TPM_CC::LoadExternal: {
            auto c0 = dynamic_cast<TPM2_LoadExternal_REQUEST *>(command);
            objectInName = c0->inPublic.GetName();
            return;
        }

        case TPM_CC::Load: {
            auto c0 = dynamic_cast<TPM2_Load_REQUEST *>(command);
            objectInName = c0->inPublic.GetName();
            return;
        }

        case TPM_CC::NV_ChangeAuth: {
            auto c0 = dynamic_cast<TPM2_NV_ChangeAuth_REQUEST *>(command);
            c0->nvIndex.SetAuth(c0->newAuth);
            // Note - Change this so that session will work
            objectInAuth = c0->newAuth;
            return;
        }

        case TPM_CC::ObjectChangeAuth: {
            auto c0 = dynamic_cast<TPM2_ObjectChangeAuth_REQUEST *>(command);
            c0->objectHandle.SetAuth(c0->newAuth);
            objectInAuth = c0->newAuth;
            return;
        }

        case TPM_CC::PCR_SetAuthValue: {
            auto c0 = dynamic_cast<TPM2_PCR_SetAuthValue_REQUEST *>(command);
            objectInAuth = c0->auth;
            return;
        }

        case TPM_CC::EvictControl: {
            auto c0 = dynamic_cast<TPM2_EvictControl_REQUEST *>(command);
            objectInAuth = c0->objectHandle.GetAuth();

            if (c0->objectHandle.GetHandleType() != TPM_HT::PERSISTENT) {
                objectInName = c0->objectHandle.GetName();
            }

            return;
        }

        case TPM_CC::Clear: {
            auto c0 = dynamic_cast<TPM2_Clear_REQUEST *>(command);
            c0->authHandle.SetAuth(vector<BYTE>());
            return;
        }

        case TPM_CC::HashSequenceStart: {
            auto c0 = dynamic_cast<TPM2_HashSequenceStart_REQUEST *>(command);
            objectInAuth = c0->auth;
            return;
        }

        case TPM_CC::Startup: {
            return;
        }

        case TPM_CC::ContextSave: {
            return;
        }

        case TPM_CC::ContextLoad: {
            return;
        }

        default:
            return;
    }
}

void Tpm2::UpdateHandleDataResponse(TPM_CC cc, TpmStructureBase *response)
{

    // This function is called if a command succeeds. We apply the name and
    // auth-value calculated earlier

    // TODO: Test scenario coverage of this is low.

    switch (cc) {
        case TPM_CC::Load: {
            LoadResponse *r = dynamic_cast<LoadResponse *>(response);

            if (r->name != objectInName) {
                throw runtime_error("TPM-returned object name inconsistent with inPublic-derived name");
            }

            r->objectHandle.SetName(objectInName);
            return;
        }

        case TPM_CC::CreatePrimary: {
            auto r3 = dynamic_cast<CreatePrimaryResponse *>(response);

            if (r3->outPublic.GetName() != r3->name) {
                throw runtime_error("TPM-returned object name inconsistent with outPublic-derived name");
            }

            r3->objectHandle.SetName(r3->outPublic.GetName());
            return;
        }

        case TPM_CC::HierarchyChangeAuth: {
            // Note - Can't update the inHandle because it's a value-copy.
            switch ((TPM_RH) inHandles[0]->handle) {
                case TPM_RH::OWNER:
                    _AdminOwner.SetAuth(objectInAuth);
                    break;

                case TPM_RH::ENDORSEMENT:
                    _AdminEndorsement.SetAuth(objectInAuth);
                    break;

                case TPM_RH::PLATFORM:
                    _AdminPlatform.SetAuth(objectInAuth);
                    break;

                case TPM_RH::LOCKOUT:
                    _AdminLockout.SetAuth(objectInAuth);
                    break;

                default:
                    break;
            }

            inHandles[0]->SetAuth(objectInAuth);
            return;
        }

        case TPM_CC::LoadExternal: {
            auto r0 = dynamic_cast<LoadExternalResponse *>(response);
            r0->name = objectInName;

            if (objectInName != r0->name) {
                throw runtime_error("TPM-returned object name inconsistent with outPublic-derived name");
            }

            return;
        }

        case TPM_CC::NV_ChangeAuth: {
            // TODO: Does not work because we make a value copy of the 
            //       input handle in the command dispatcher.
            // inHandles[0]->SetAuth(objectInAuth);
            return;
        }

        case TPM_CC::ObjectChangeAuth: {
            // TODO: Does not work.
            //inHandles[0]->SetAuth(objectInAuth);
            return;
        }

        case TPM_CC::PCR_SetAuthValue: {
            // TODO: Does not work.
            //inHandles[0]->SetAuth(objectInAuth);
            return;
        }

        case TPM_CC::EvictControl: {
            // TODO: Does not work.
            // auto r0 = dynamic_cast<EvictControlResponse*>(response);
            if (inHandles[1]->GetHandleType() != TPM_HT::PERSISTENT) {
                inHandles[1]->SetAuth(objectInAuth);
                inHandles[1]->SetName(objectInName);
            }

            return;
        }

        case TPM_CC::Clear: {
            std::vector<BYTE> NullVec;
            _AdminLockout.SetAuth(NullVec);
            _AdminOwner.SetAuth(NullVec);
            _AdminEndorsement.SetAuth(NullVec);
            return;
        }

        case TPM_CC::HashSequenceStart: {
            auto r0 = dynamic_cast<HashSequenceStartResponse *>(response);
            r0->sequenceHandle.SetAuth(objectInAuth);
            return;
        }

        case TPM_CC::Startup: {
            return;
        }

        case TPM_CC::ContextSave: {
            return;
        }

        case TPM_CC::ContextLoad: {
            return;
        }

        default:
            return;
    }
}

std::vector<BYTE>  Tpm2::GetRandomBytes(size_t numBytes)
{
    if (rng != NULL) {
        return (*rng)(numBytes);
    }

    return CryptoServices::GetRand(numBytes);
}

Tpm2& Tpm2::_Sessions(AUTH_SESSION& h)
{
    Sessions.clear();
    Sessions.push_back(&h);
    return *this;
}

Tpm2& Tpm2::_Sessions(AUTH_SESSION& h1, AUTH_SESSION& h2)
{
    Sessions.clear();
    Sessions.push_back(&h1);
    Sessions.push_back(&h2);
    return *this;
}

Tpm2& Tpm2::_Sessions(AUTH_SESSION& h1, AUTH_SESSION& h2, AUTH_SESSION& h3)
{
    Sessions.clear();
    Sessions.push_back(&h1);
    Sessions.push_back(&h2);
    Sessions.push_back(&h3);
    return *this;
}

Tpm2& Tpm2::_Sessions(std::vector<AUTH_SESSION> handles)
{
    _ASSERT(FALSE);
    return *this;
}

void Tpm2::PrepareParmEncryptionSessions()
{
    DecSession = NULL;
    EncSession = NULL;
    NonceTpmDec.resize(0);
    NonceTpmEnc.resize(0);

    for (size_t j = 0; j < Sessions.size(); j++) {
        AUTH_SESSION *s = Sessions[j];
        CheckParamEncSessCandidate(s, TPMA_SESSION::decrypt);
        CheckParamEncSessCandidate(s, TPMA_SESSION::encrypt);
    }

    // If the first auth session is followed by parameter decryption and or encryption
    // session(s), the NonceTPM must be included into HMAC of the first auth session.
    // This precludes encryption sessions removal by malware.
    if ((DecSession != NULL) && (DecSession != Sessions[0])) {
        NonceTpmDec = DecSession->NonceTpm;
    }

    if ((EncSession != NULL) && (EncSession != Sessions[0]) && (EncSession != DecSession)) {
        NonceTpmEnc = EncSession->NonceTpm;
    }
}

void Tpm2::CheckParamEncSessCandidate(AUTH_SESSION *candidate, TPMA_SESSION directionFlag)
{
    if (((UINT32) candidate->SessionAttributes & (UINT32) directionFlag) == 0) {
        return;
    }

    bool decrypt = directionFlag == TPMA_SESSION::decrypt;

    if (decrypt) {
        DecSession = candidate;
    } else {
        EncSession = candidate;
    }
}

int GetFirstParmSizeOffset(bool directionIn, StructMarshallInfo *str, int& sizeNumBytes)
{
    // Return the offset to the size parm, and then number of bytes in
    // the size parm if this struct is a parm-encrytion candiate.
    sizeNumBytes = -1;
    int offset = 0;

    if (str->Fields.size() == 0) {
        return -1;
    }

    for (size_t j = 0; j < str->Fields.size(); j++) {
        if (str->Fields[j].ThisElementType == TpmTypeId::TPM_HANDLE_ID) {
            // Skip handles
            offset += 4;
            continue;
        }

        if (!((str->Fields[j].ElementMarshallType != MarshallType::ArrayCount) ||
              (str->Fields[j].ElementMarshallType != MarshallType::LengthOfStruct))) {
            return -1;
        }

        sizeNumBytes = str->Fields[j].ElementSize;
        return offset;
    }

    return -1;
}

void Tpm2::DoParmEncryption(TpmStructureBase *str, ByteVec& parmBuffer, bool directionIn)
{
    if ((EncSession == NULL) && (DecSession == NULL)) {
        return;
    }

    AUTH_SESSION *encSess = NULL;

    if (directionIn) {
        if (DecSession == NULL) {
            return;
        }

        encSess = DecSession;
    } else {
        if (EncSession == NULL) {
            return;
        }

        encSess = EncSession;
    }

    StructMarshallInfo *marshallInfo = TheTypeMap.GetStructMarshallInfo(str->GetTypeId());
    int sizeParmNumBytes;
    int firstParmSizeOffset = GetFirstParmSizeOffset(directionIn, marshallInfo, sizeParmNumBytes);

    if (firstParmSizeOffset == -1) {
        throw runtime_error("Command is not eligible for parm encryption");
    }

    // Get the size
    UINT32 bufLen = GetValFromBufNetOrder(&parmBuffer[firstParmSizeOffset], sizeParmNumBytes);
    UINT32 startOfBuf = firstParmSizeOffset + sizeParmNumBytes;
    
    // Get the slice we want to encrypt
    ByteVec toEncrypt(parmBuffer.begin() + startOfBuf, parmBuffer.begin() + startOfBuf + bufLen);
    
    // Encrypt it
    ByteVec enc = encSess->ParmEncrypt(toEncrypt, directionIn);
    
    // And copy it back into the buffer
    copy(enc.begin(), enc.end(), parmBuffer.begin() + startOfBuf);

    EncSession = NULL;
    DecSession = NULL;
    return;
}

void Tpm2::_StartAudit(const TPMT_HA& startVal)
{
    if (startVal.digest.size() != 0) {
        CommandAuditHash = startVal;
        return;
    }

    CommandAuditHash = TPMT_HA(startVal);
}

void Tpm2::_EndAudit()
{
    CommandAuditHash.hashAlg = TPM_ALG_ID::_NULL;
    return;
}

TPMT_HA Tpm2::_GetAuditHash()
{
    if (CommandAuditHash.hashAlg == TPM_ALG_ID::_NULL) {
        throw runtime_error("No command-audit alg.");
    }

    return CommandAuditHash;
}

void Tpm2::DebugPrint(const string& _message)
{
    WCHAR    str[1024];

    if (_message.length() < 1023) {
        OutputDebugString(L"************** -- TPM Exception: ");
        MultiByteToWideChar(0, 0, _message.c_str(), _message.length() + 1, str, 1024);
        OutputDebugString(str);
        OutputDebugString(L"\n");
    }

}

_TPMCPP_END
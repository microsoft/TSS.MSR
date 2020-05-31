/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "MarshalInternal.h"

_TPMCPP_BEGIN

using namespace std;

typedef ByteVec ByteVec;
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
    _AdminPlatform = TPM_RH::PLATFORM;
    _AdminOwner = TPM_RH::OWNER;
    _AdminEndorsement = TPM_RH::ENDORSEMENT;
    _AdminLockout = TPM_RH::LOCKOUT;
    CommandAuditHash.hashAlg = TPM_ALG_NULL;
}

void Tpm2::RollNonces()
{
    for (size_t j = 0; j < Sessions.size(); j++)
    {
        if (Sessions[j]->IsPWAP())
            continue;

        // Roll the nonceCaller
        Sessions[j]->NonceCaller = GetRandomBytes(Sessions[j]->NonceCaller.size());
    }
}

void Tpm2::_SetRhAuthValue(TPM_HANDLE& h) const
{
    switch (h.handle) {
    case TPM_RH::OWNER:
        h.SetAuth(_AdminOwner.GetAuth());
        break;
    case TPM_RH::ENDORSEMENT:
        h.SetAuth(_AdminEndorsement.GetAuth());
        break;
    case TPM_RH::PLATFORM:
        h.SetAuth(_AdminPlatform.GetAuth());
        break;
    case TPM_RH::LOCKOUT:
        h.SetAuth(_AdminLockout.GetAuth());
        break;
    }
}

void Tpm2::GetAuthSessions(ByteVec& buf, TPM_CC command, const ByteVec& commandBuf,
                           size_t numAuthHandles, const vector<TPM_HANDLE*>& handles)
{
    size_t numExplicitSessions = Sessions.size();
    if (numExplicitSessions < numAuthHandles)
        throw runtime_error("Not enough explicit sessions for number of handles that need authorization");

    OutByteBuf commandWithNames;
    bool haveNonPwap = false;
    for (size_t i = 0; !haveNonPwap && i < numExplicitSessions; i++)
    {
        if (!Sessions[i]->IsPWAP())
            haveNonPwap = true;
    }

    // We only need the names if we have at least one non-PWAP session
    if (haveNonPwap)
    {
        commandWithNames << (UINT32)command;
        for (auto i = handles.begin(); i != handles.end(); i++)
            commandWithNames << (*i)->GetName();
        commandWithNames << commandBuf;
    }

    TPMS_AUTH_COMMAND s;
    OutByteBuf t;
    TPM_HANDLE tempHandle;
    TPM_HANDLE *associatedHandle;

    for (size_t i = 0; i < numExplicitSessions; i++)
    {
        AUTH_SESSION& sess = *Sessions[i];

        if (i < handles.size())
            _SetRhAuthValue(*handles[i]);

        // PWAPs are easy.
        if (sess.IsPWAP())
        {
            tempHandle = TPM_RH::PW;
            s.sessionHandle = tempHandle;
            s.nonce.resize(0);
            s.hmac = handles[i]->GetAuth();
            s.sessionAttributes = TPMA_SESSION::continueSession;
            t << s;
            continue;
        }

        // Else an HMAC, policy, encrypting session prepare the session and session-hash.
        associatedHandle = NULL;

        if (i < numAuthHandles)
        {
            sess.SetAuthValue(handles[i]->GetAuth());
            associatedHandle = handles[i];
        }

        s.nonce = sess.NonceCaller;
        s.sessionHandle = sess;
        auto parmHash = Crypto::Hash(sess.HashAlg, commandWithNames.GetBuf());
        s.sessionAttributes = Sessions[i]->SessionAttributes;

        if (sess.SessionType == TPM_SE::HMAC || sess.ForceHmacOnPolicySession)
        {
            s.hmac = sess.GetAuthHmac(parmHash, true, NonceTpmDec, NonceTpmEnc, associatedHandle);
        }
        else if (sess.IncludePlaintextPasswordInPolicySession)
        {
            s.hmac = handles[i]->GetAuth();
        }

        // And serialize it so that it can be added to the command-buffer
        t << s;
    }

    buf = t.GetBuf();
} // Tpm2::GetAuthSessions()

bool Tpm2::ProcessResponseSessions(ByteVec& sessionBufVec, TPM_CC cmdCode, TPM_RC reponse,
                                   const ByteVec& respBufNoHandles, const vector<TPM_HANDLE*>& inHandles)
{
    OutByteBuf rpBuf;
    rpBuf << (UINT32)reponse << (UINT32)cmdCode << respBufNoHandles;
    InByteBuf sessionBuf(sessionBufVec);
    AUTHResponse authResponse;
    OutByteBuf t;
    TPM_HANDLE tempHandle;
    TPM_HANDLE *associatedHandle;
    ByteVec nullVec;

    for (size_t j = 0; j < Sessions.size(); j++)
    {
        AUTH_SESSION& sess = *Sessions[j];
        sessionBuf >> authResponse;

        if (sess.IsPWAP())
        {
            AUTHResponse r;
            if (r.nonce != nullVec || r.hmac != nullVec)
            {
                Sessions.clear();
                throw runtime_error("Bad value in PWAP session response");
            }
            continue;
        }

        associatedHandle = NULL;

        if (j < inHandles.size())
        {
            sess.SetAuthValue(inHandles[j]->GetAuth());
            associatedHandle = inHandles[j];
        }

        auto respHash = Crypto::Hash(sess.HashAlg, rpBuf.GetBuf());
        // Update our session data based on what the TPM just told us
        sess.NonceTpm = authResponse.nonce;
        sess.SessionAttributes = authResponse.sessionAttributes;

        if (sess.SessionType == TPM_SE::HMAC)
        {
            auto expectedHmac = sess.GetAuthHmac(respHash, false,
                                                 NonceTpmDec, NonceTpmEnc,
                                                 associatedHandle);
            if (expectedHmac != authResponse.hmac)
                return false;
        }
    }
    return sessionBuf.eof();
} // Tpm2::ProcessResponseSessions()

///<summary>Sets the handles array to point to the handle objects in the request</summary>
void Tpm2::GetHandles(const TpmStructure* request, TpmStructInfo* typeInfo,
                      vector<TPM_HANDLE*>& handles)
{
    if (!request)
        return;

    for (int j = 0; j < typeInfo->HandleCount; j++) {
        int x;
        TpmStructure *y;
        TPM_HANDLE *h = (TPM_HANDLE*)const_cast<TpmStructure*>(request)->ElementInfo(j, -1, x, y, -1);
        handles.push_back(h);
    }
}

///<summary> Send a TPM command to the underlying TPM device.  TPM errors are
/// propagated as exceptions by default </summary>
void Tpm2::Dispatch(TPM_CC cmdCode, TpmStructure* req, TpmStructure* resp)
{
    for (;;)
    {
        bool processPhaseTwo = DispatchOut(cmdCode, req);
        if (!processPhaseTwo || DispatchIn(cmdCode, resp))
        {
            break;
        }
        Sleep(1000);
    }
}

bool Tpm2::DispatchOut(TPM_CC cmdCode, TpmStructure* req)
{
    if (phaseTwoExpected)
        throw runtime_error("A TPM command has been dispatched before the previous async-command has been processed.  Call Cancel() if you need to abort");

    OutByteBuf reqBuf;
    TpmStructInfo *reqInfo = NULL;
    size_t handleAreaSize = 0;

    authHandleCount = 0;

    if (req != NULL) {
        reqInfo = &GetTypeInfo<TpmEntity::Struct>(req->GetTypeId());
        handleAreaSize = reqInfo->HandleCount * 4;
        authHandleCount = reqInfo->AuthHandleCount;
    }

    sessions = false;
    numSessions = 0;
    sessionsTag = TPM_ST::NO_SESSIONS;

    // AuthValues are always retrieved from the object handle. If no explicit sessions
    // are provided then the auth-value is retrieved for all sessions that require auth
    // and PWAP is used.
    //
    // If explicit sessions are provided then the explicit sessions are used (either a
    // session handle, or a AUTH_SESSION::PWAP(), if PWAP is desired for a handle).
    // If there are not enough explicit sessions, then an error is generated.

    size_t numExplicitSessions = Sessions.size();

    InHandles.clear();
    GetHandles(req, reqInfo, InHandles);

    //std::for_each(InHandles.begin(), InHandles.end(), [this](TPM_HANDLE* h){SetRhAuthValue(*this, *h);});

    // Do we have sessions of either type?
    if (authHandleCount || numExplicitSessions)
    {
        sessions = true;
        sessionsTag = TPM_ST::SESSIONS;
    }

    ByteVec cmdBuf;
    if (req != NULL)
        cmdBuf = req->ToBuf();

    int commandLen = 10 + (int)cmdBuf.size();

    ByteVec sessBuf;
    if (sessions)
    {
        RollNonces();
        PrepareParmEncryptionSessions();

        DoParmEncryption(req, cmdBuf, true);

        // No explicit sessions, but auth needed for one or more handles we fabricate
        // some PWAP sessions
        if (authHandleCount > numExplicitSessions)
        {
            Sessions.resize(authHandleCount);

            // PwapSession is used only as a placeholder for subsequent processing
            for (size_t j = numExplicitSessions; j < authHandleCount; j++)
                Sessions[j] = &PwapSession;

            numExplicitSessions = authHandleCount;
            numSessions = authHandleCount;
        }

        // Then we can process the real or fabricated sessions
        numSessions = (int)Sessions.size();
        ByteVec commBufNoHandles = VectorSlice(cmdBuf, handleAreaSize,
                                               cmdBuf.size() - handleAreaSize);
        GetAuthSessions(sessBuf, cmdCode, commBufNoHandles, authHandleCount, InHandles);
        commandLen += (int)sessBuf.size() + 4;
    }

    // Construct the command buffer

    OutByteBuf outCommand;

    // First the standard header
    outCommand << (UINT16)sessionsTag << (UINT32)commandLen << (UINT32)cmdCode;

    // Add the handes (if any).
    outCommand.AddSlice(cmdBuf, 0, handleAreaSize);

    // Add the sessionLen + sessions (if any).
    if (sessions)
        outCommand << (UINT32)sessBuf.size() << sessBuf;

    // Add the rest of the command (handles already added)
    outCommand.AddSlice(cmdBuf, handleAreaSize, cmdBuf.size() - handleAreaSize);

    // Command buffer complete

    if ((CpHash != NULL) || AuditThisCommand  ) {
        // Non-NULL CpHash indicates that the caller wants the CpHash, 
        // but does not want the command invoked.
        OutByteBuf cpBuf;
        cpBuf << cmdCode;

        for (auto it = InHandles.begin(); it != InHandles.end(); it++)
            cpBuf << (*it)->GetName();

        cpBuf.AddSlice(cmdBuf, handleAreaSize, (int)cmdBuf.size() - handleAreaSize);

        if (CpHash)
        {
            CpHash->digest = Crypto::Hash(CpHash->hashAlg, cpBuf.GetBuf());
            ClearInvocationState();
            phaseTwoExpected = false;
            CpHash = NULL;
            return false;
        }

        // Else we are auditing this command
        LastCommandAuditCpHash.digest = Crypto::Hash(CommandAuditHash, cpBuf.GetBuf());
        cout << "CpHash: " << LastCommandAuditCpHash.digest << endl;
    }

    // Tpms can be used for operations that do not directly involve the TPM: e.g. getting
    // a CpHash, but if we get to thhis point we really need a TPM...

    if (!device)
        throw runtime_error("No TPM device.  Use _SetDevice() or the constructor that takes a TpmDevice*");

    // And send it to the TPM
    ByteVec& commandBuf = outCommand.GetBuf();
    ByteVec tempRespBuf;

    device->DispatchCommand(commandBuf);
    lastCommandBuf = commandBuf;

    UpdateHandleDataCommand(cmdCode, req);

    respBuf = tempRespBuf;
    phaseTwoExpected = true;
    commandBeingProcessed = cmdCode;

    return true;
} // Tpm2::DispatchOut()

bool Tpm2::DispatchIn(TPM_CC cmdCode, TpmStructure *resp)
{
    if (!phaseTwoExpected) {
        phaseTwoExpected = false;
        throw runtime_error("Async command completion with no outstanding command");
    }

    if (commandBeingProcessed != cmdCode)
        throw runtime_error("Async command completion does not match command being processed");

    phaseTwoExpected = false;
    respBuf = device->GetResponse();

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

    if (respCode == TPM_RC::SUCCESS)
    {
        CompleteUpdateHandleDataCommand(cmdCode);
        if (DemandError) {
            inStream.TheRest();
            errorMessage = "A TPM error was demanded but the function succeeded";
            throwException = true;
            goto outOfHere;
        }

        if (ExpectedError != TPM_RC::SUCCESS) {
            // We succeeded, but the caller called _ExpectError(someOtherError)
            errorMessage = "_ExpectError(...) was called but command succeeded";
            throwException = true;
            goto outOfHere;

        }
    } else {
        // We demanded an error, so OK that we have one.
        if (DemandError)
            goto outOfHere;

        // Else we have an error. We generate an exception if either AllowErrors is false.
        if (!AllowErrors) {
            // An error was not expected
            errorMessage = "TPM Error - TPM_RC::" + GetEnumString(errorCode);
            throwException = true;
            goto outOfHere;
        }

        // Final case: we have an error, but this is OK as long as it is the "expected" error.
        if ((errorCode != ExpectedError) && (ExpectedError != TPM_RC::SUCCESS))
        {
            errorMessage = "TPM returned {" + GetEnumString(errorCode)
                         + "} instead of expected {" + GetEnumString(ExpectedError) + "}";
            throwException = true;
            goto outOfHere;
        }
    }

outOfHere:
    // Keep a copy of this before we clean out invocation state
    bool auditCommand = AuditThisCommand;
    ClearInvocationState();

    if (throwException)
    {
        Sessions.clear();
        DebugPrint(errorMessage);
        throw system_error((UINT32) errorCode, system_category(), errorMessage);
    }

    // Even if we did not throw an exception there is nothing more to do if we have an error.
    if (errorCode != TPM_RC::SUCCESS)
    {
        Sessions.clear();
        return true;
    }

    // Else the command succeeded, so we can process the response buffer
    if (sessionsTag != respTag)
        throw runtime_error("unexpected response tag");

    OutByteBuf tempParmBuf;

    // Get the handles
    if (resp)
    {
        TpmStructInfo& respInfo = GetTypeInfo<TpmEntity::Struct>(resp->GetTypeId());
        _ASSERT(respInfo.HandleCount < 2);
        for (int j = 0; j < respInfo.HandleCount; j++)
        {
            UINT32 hVal;
            inStream >> hVal;
            tempParmBuf << TPM_HANDLE(hVal);
        }
    }

    // If there are no sessions then the rest of the response structure is here.
    // If there are sessions then there is a size parm for the rest of the response
    // struct followed by the rest of the response.
    ByteVec outSessionsArea;
    ByteVec respNoHandles;

    if (sessions) {
        UINT32 restOfInParmSize;
        inStream >> restOfInParmSize;
        respNoHandles = inStream.GetSlice(restOfInParmSize);
        tempParmBuf << respNoHandles;
        outSessionsArea = inStream.TheRest();
    }
    else {
        respNoHandles = inStream.TheRest();
        tempParmBuf << respNoHandles;
    }

    if (auditCommand)
    {
        if (CommandAuditHash.hashAlg == TPM_ALG_NULL)
            throw runtime_error("No command audit digest set");

        OutByteBuf _respBuf;
        _respBuf << TPM_RC::Value(TPM_RC::SUCCESS) << cmdCode << respNoHandles;
        auto rpHash = TPMT_HA::FromHashOfData(CommandAuditHash, _respBuf.GetBuf());
        CommandAuditHash.Extend(Helpers::Concatenate(LastCommandAuditCpHash, rpHash));
    }

    bool sessionsOk = ProcessResponseSessions(outSessionsArea, cmdCode, respCode,
                                              respNoHandles, InHandles);
    if (!sessionsOk)
        throw runtime_error("Response session failure");

    if (resp)
    {
        // Now we can unmarshall the (possibly previously fragmented) response byte stream.
        DoParmEncryption(resp, tempParmBuf.GetBuf(), false);
        resp->FromBuf(tempParmBuf.GetBuf());

        // If there is a returned handle get a pointer to it. It is always the 
        // first element in the structure.
        UpdateHandleDataResponse(cmdCode, resp);
    }

    // And finally process the response sessions
    Sessions.clear();
    return true;
} // Tpm2::DispatchIn()

void Tpm2::UpdateHandleDataCommand(TPM_CC cc, TpmStructure *command)
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
            auto c0 = (TPM2_HierarchyChangeAuth_REQUEST*)command;
            // Note - Change this so that session will work
            c0->authHandle.SetAuth(c0->newAuth);
            objectInAuth = c0->newAuth;
            return;
        }

        case TPM_CC::LoadExternal: {
            auto c0 = (TPM2_LoadExternal_REQUEST*)command;
            objectInName = c0->inPublic.GetName();
            return;
        }

        case TPM_CC::Load: {
            auto c0 = (TPM2_Load_REQUEST*)command;
            objectInName = c0->inPublic.GetName();
            return;
        }

        case TPM_CC::NV_ChangeAuth: {
            auto c0 = (TPM2_NV_ChangeAuth_REQUEST*)command;
            c0->nvIndex.SetAuth(c0->newAuth);
            // Note - Change this so that session will work
            objectInAuth = c0->newAuth;
            return;
        }

        case TPM_CC::ObjectChangeAuth: {
            auto c0 = (TPM2_ObjectChangeAuth_REQUEST*)command;
            c0->objectHandle.SetAuth(c0->newAuth);
            objectInAuth = c0->newAuth;
            return;
        }

        case TPM_CC::PCR_SetAuthValue: {
            auto c0 = (TPM2_PCR_SetAuthValue_REQUEST*)command;
            objectInAuth = c0->auth;
            return;
        }

        case TPM_CC::EvictControl: {
            auto c0 = (TPM2_EvictControl_REQUEST*)command;
            objectInAuth = c0->objectHandle.GetAuth();
            if (c0->objectHandle.GetHandleType() != TPM_HT::PERSISTENT)
                objectInName = c0->objectHandle.GetName();
            return;
        }

        case TPM_CC::Clear: {
            auto c0 = (TPM2_Clear_REQUEST*)command;
            const_cast<TPM2_Clear_REQUEST*>(c0)->authHandle.SetAuth(ByteVec());
            return;
        }

        case TPM_CC::HashSequenceStart: {
            auto c0 = (TPM2_HashSequenceStart_REQUEST*)command;
            objectInAuth = c0->auth;
            return;
        }

        case TPM_CC::Startup:
            return;

        case TPM_CC::ContextSave:
            return;

        case TPM_CC::ContextLoad:
            return;

        default:
            return;
    }
} // Tpm2::UpdateHandleDataCommand()

void Tpm2::CompleteUpdateHandleDataCommand(TPM_CC cc)
{
    switch (cc) {
        case TPM_CC::HierarchyChangeAuth: {
            switch ((TPM_RH) InHandles[0]->handle) {
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
            }

            // TODO: Can't update inHandle[0] because it's a pointer to a value-copy.
            InHandles[0]->SetAuth(objectInAuth);
            return;
        }

        case TPM_CC::NV_ChangeAuth: {
            // TODO: Does not work because we make a value copy of the 
            //       input handle in the command dispatcher.
            InHandles[0]->SetAuth(objectInAuth);
            return;
        }

        case TPM_CC::ObjectChangeAuth: {
            // TODO: Does not work.
            InHandles[0]->SetAuth(objectInAuth);
            return;
        }

        case TPM_CC::PCR_SetAuthValue: {
            // TODO: Does not work.
            InHandles[0]->SetAuth(objectInAuth);
            return;
        }

        case TPM_CC::EvictControl: {
            // TODO: Does not work.
            // auto r0 = dynamic_cast<EvictControlResponse*>(response);
            if (InHandles[1]->GetHandleType() != TPM_HT::PERSISTENT)
            {
                InHandles[1]->SetAuth(objectInAuth);
                InHandles[1]->SetName(objectInName);
            }
            return;
        }

        case TPM_CC::Clear: {
            ByteVec NullVec;
            _AdminLockout.SetAuth(NullVec);
            _AdminOwner.SetAuth(NullVec);
            _AdminEndorsement.SetAuth(NullVec);
            return;
        }

    }
} // Tpm2::CompleteUpdateHandleDataCommand

void Tpm2::UpdateHandleDataResponse(TPM_CC cc, TpmStructure *response)
{
    // This function is called if a command succeeds. We apply the name and
    // auth-value calculated earlier

    // TODO: Test scenario coverage of this is low.

    switch (cc) {
        case TPM_CC::Load: {
            LoadResponse *r = (LoadResponse*)response;
            if (r->name != objectInName)
                throw runtime_error("TPM-returned object name inconsistent with inPublic-derived name");
            r->handle.SetName(objectInName);
            return;
        }

        case TPM_CC::CreatePrimary: {
            auto r = (CreatePrimaryResponse*)response;
            if (r->outPublic.GetName() != r->name)
                throw runtime_error("TPM-returned object name inconsistent with outPublic-derived name");
            r->handle.SetName(r->outPublic.GetName());
            return;
        }

        case TPM_CC::LoadExternal: {
            auto r0 = (LoadExternalResponse*)response;
            if (objectInName != r0->name)
                throw runtime_error("TPM-returned object name inconsistent with inPublic-derived name");
            //r0->name = objectInName;
            return;
        }

        case TPM_CC::HashSequenceStart: {
            auto r0 = (HashSequenceStartResponse*)response;
            r0->handle.SetAuth(objectInAuth);
            return;
        }

        case TPM_CC::Startup:
            return;

        case TPM_CC::ContextSave:
            return;

        case TPM_CC::ContextLoad:
            return;

        default:
            return;
    }
} // Tpm2::UpdateHandleDataResponse()

Tpm2& Tpm2::_Sessions(AUTH_SESSION& h)
{
    Sessions.push_back(&h);
    return *this;
}

Tpm2& Tpm2::_Sessions(AUTH_SESSION& h1, AUTH_SESSION& h2)
{
    Sessions.push_back(&h1);
    Sessions.push_back(&h2);
    return *this;
}

Tpm2& Tpm2::_Sessions(AUTH_SESSION& h1, AUTH_SESSION& h2, AUTH_SESSION& h3)
{
    Sessions.push_back(&h1);
    Sessions.push_back(&h2);
    Sessions.push_back(&h3);
    return *this;
}

Tpm2& Tpm2::_Sessions(const vector<AUTH_SESSION*>& sessions)
{
    Sessions.assign(sessions.begin(), sessions.end());
    return *this;
}

void Tpm2::PrepareParmEncryptionSessions()
{
    DecSession = NULL;
    EncSession = NULL;
    NonceTpmDec.clear();
    NonceTpmEnc.clear();

    for (size_t j = 0; j < Sessions.size(); j++)
    {
        AUTH_SESSION *s = Sessions[j];
        if (s->SessionAttributes & TPMA_SESSION::decrypt)
            DecSession = s;
        if (s->SessionAttributes & TPMA_SESSION::encrypt)
            EncSession = s;
    }

    // If the first auth session is followed by parameter decryption and or encryption
    // session(s), the NonceTPM must be included into HMAC of the first auth session.
    // This precludes encryption sessions removal by malware.
    if (DecSession && DecSession != Sessions[0])
    {
        NonceTpmDec = DecSession->GetNonceTpm();
    }
    if (EncSession && EncSession != Sessions[0] && EncSession != DecSession)
    {
        NonceTpmEnc = EncSession->GetNonceTpm();
    }
}

int GetFirstParmSizeOffset(bool /*directionIn*/, TpmStructInfo& typeInfo, int& sizeNumBytes)
{
    // Return the offset to the size parm, and then number of bytes in
    // the size parm if this struct is a parm-encrytion candiate.
    sizeNumBytes = -1;
    int offset = 0;

    if (!typeInfo.Fields.size())
        return -1;

    for (size_t j = 0; j < typeInfo.Fields.size(); j++)
    {
        MarshalInfo& field = typeInfo.Fields[j];

        if (field.TypeId == TpmTypeId::TPM_HANDLE_ID)
        {
            // Skip handles
            offset += 4;
            continue;
        }

        if (field.MarshalType != WireType::ArrayCount &&
            field.MarshalType != WireType::LengthOfStruct)
        {
            return -1;
        }

        sizeNumBytes = GetTypeInfo<TpmEntity::Typedef>(field.TypeId).Size;
        return offset;
    }
    return -1;
}

void Tpm2::DoParmEncryption(const TpmStructure* req, ByteVec& parmBuffer, bool directionIn)
{
    if (!EncSession && !DecSession)
        return;

    AUTH_SESSION *encSess = NULL;

    if (directionIn) {
        if (!DecSession)
            return;
        encSess = DecSession;
    } else {
        if (!EncSession)
            return;
        encSess = EncSession;
    }

    TpmStructInfo& typeInfo = GetTypeInfo<TpmEntity::Struct>(req->GetTypeId());
    int sizeParmNumBytes;
    int firstParmSizeOffset = GetFirstParmSizeOffset(directionIn, typeInfo, sizeParmNumBytes);

    if (firstParmSizeOffset == -1)
        throw runtime_error("Command is not eligible for parm encryption");

    // Get the size
    UINT32 bufLen = GetValFromBufNetOrder(&parmBuffer[firstParmSizeOffset], sizeParmNumBytes);
    UINT32 startOfBuf = firstParmSizeOffset + sizeParmNumBytes;
    
    // Get the slice we want to encrypt
    ByteVec toEncrypt(parmBuffer.begin() + startOfBuf, parmBuffer.begin() + startOfBuf + bufLen);
    
    // Encrypt it
    ByteVec enc = encSess->ParmEncrypt(toEncrypt, directionIn);
    
    // And copy it back into the buffer
    copy(enc.begin(), enc.end(), parmBuffer.begin() + startOfBuf);

    EncSession = DecSession = NULL;
} // Tpm2::DoParmEncryption()

TPMT_HA Tpm2::_GetAuditHash() const
{
    if (CommandAuditHash.hashAlg == TPM_ALG_NULL)
        throw runtime_error("No command-audit alg.");

    return CommandAuditHash;
}

void Tpm2::DebugPrint(const string& _message)
{
    WCHAR    str[1024];

    if (_message.length() < 1023) {
        OutputDebugString(L"************** -- TPM Exception: ");
        MultiByteToWideChar(0, 0, _message.c_str(), (int)_message.length() + 1, str, 1024);
        OutputDebugString(str);
        OutputDebugString(L"\n");
    }
}

_TPMCPP_END

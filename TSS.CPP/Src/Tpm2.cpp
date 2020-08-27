/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"

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

void Tpm2::ClearInvocationState()
{
    AllowErrors = false;
    DemandError = false;
    ExpectedError = TPM_RC::SUCCESS;
    CpHash = NULL;
    AuditCommand = false;
    PendingCommand = 0;
}

void Tpm2::RollNonces()
{
    for (size_t j = 0; j < Sessions.size(); j++)
    {
        if (Sessions[j]->IsPWAP())
            continue;

        // Roll the nonceCaller
        Sessions[j]->NonceCaller = Helpers::RandomBytes(Sessions[j]->NonceCaller.size());
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

ByteVec Tpm2::GetCpHashData(TPM_CC cmdCode, const ByteVec& cmdParams) const
{
    TpmBuffer buf;
    buf.writeInt(cmdCode);
    for (auto h : InHandles)
        buf.writeByteBuf(h.GetName());
    buf.writeByteBuf(cmdParams);
    return buf.trim();
}

ByteVec Tpm2::ProcessAuthSessions(TpmBuffer& cmdBuf, TPM_CC cmdCode, size_t numAuthHandles,
                                  const ByteVec& cmdParams)
{
    assert(numAuthHandles <= Sessions.size());

    bool needHmac = false;
    for (auto s : Sessions)
    {
        if (!s->IsPWAP())
        {
            needHmac = true;
            break;
        }
    }

    ByteVec cpHashData;
    if (needHmac)
        cpHashData = GetCpHashData(cmdCode, cmdParams);

    TPMS_AUTH_COMMAND s;
    for (size_t i = 0; i < Sessions.size(); i++)
    {
        AUTH_SESSION& sess = *Sessions[i];

        if (i < InHandles.size())
            _SetRhAuthValue(InHandles[i]);

        // PWAPs are easy.
        if (sess.IsPWAP())
        {
            s.sessionHandle = TPM_RH::PW;
            s.nonce.resize(0);
            s.hmac = InHandles[i].GetAuth();
            s.sessionAttributes = TPMA_SESSION::continueSession;
            s.toTpm(cmdBuf);
            continue;
        }

        // This is an HMAC, policy, or encrypting session.
        TPM_HANDLE* associatedHandle = NULL;

        if (i < numAuthHandles)
        {
            sess.SetAuthValue(InHandles[i].GetAuth());
            associatedHandle = &InHandles[i];
        }

        s.nonce = sess.NonceCaller;
        s.sessionHandle = sess;
        s.sessionAttributes = Sessions[i]->SessionAttributes;

        if (sess.SessionType == TPM_SE::HMAC || sess.NeedsHmac)
        {
            auto cpHash = Crypto::Hash(sess.HashAlg, cpHashData);
            s.hmac = sess.GetAuthHmac(cpHash, true, NonceTpmDec, NonceTpmEnc, associatedHandle);
        }
        else if (sess.NeedsPassword)
        {
            s.hmac = InHandles[i].GetAuth();
        }

        s.toTpm(cmdBuf);
    }
    return cpHashData;
} // Tpm2::ProcessAuthSessions()

ByteVec Tpm2::GetRpHash(TPM_ALG_ID hashAlg, TpmBuffer& respBuf, TPM_CC cmdCode,
                        size_t respParamsPos, size_t respParamsSize, bool rpReady)
{
    constexpr size_t rpHeaderSize = 8;
    size_t  rpHashDataPos = respParamsPos - rpHeaderSize;
    if (!rpReady)
    {
        // Create a continuous data area required by rpHash
        size_t  origCurPos = respBuf.curPos();
        respBuf.curPos(rpHashDataPos);
        respBuf.writeInt(TPM_RC::SUCCESS);
        respBuf.writeInt(cmdCode);
        respBuf.curPos(origCurPos);
    }
    return Crypto::Hash(hashAlg, respBuf.buffer(), rpHashDataPos, rpHeaderSize + respParamsSize);
}

bool Tpm2::ProcessRespSessions(TpmBuffer& respBuf, TPM_CC cmdCode,
                               size_t respParamsPos, size_t respParamsSize)
{
    bool rpReady = false;
    respBuf.curPos(respParamsPos + respParamsSize);

    TPMS_AUTH_RESPONSE authResponse;

    for (size_t j = 0; j < Sessions.size(); j++)
    {
        AUTH_SESSION& sess = *Sessions[j];
        authResponse.initFromTpm(respBuf);

        if (sess.IsPWAP())
        {
            TPMS_AUTH_RESPONSE r;
            if (!r.nonce.empty() || !r.hmac.empty())
            {
                Sessions.clear();
                throw runtime_error("Bad value in PWAP session response");
            }
            continue;
        }

        const TPM_HANDLE *associatedHandle = NULL;

        if (j < InHandles.size())
        {
            sess.SetAuthValue(InHandles[j].GetAuth());
            associatedHandle = &InHandles[j];
        }

        // Update our session data based on what the TPM just told us
        sess.NonceTpm = authResponse.nonce;
        sess.SessionAttributes = authResponse.sessionAttributes;

        if (sess.SessionType == TPM_SE::HMAC)
        {
            auto rpHash = GetRpHash(sess.HashAlg, respBuf, cmdCode,
                                    respParamsPos, respParamsSize, rpReady);
            rpReady = true;
            auto expectedHmac = sess.GetAuthHmac(rpHash, false, NonceTpmDec, NonceTpmEnc,
                                                 associatedHandle);
            if (expectedHmac != authResponse.hmac)
                throw runtime_error("Invalid TPM response HMAC");
        }
    }
    if (respBuf.size() - respBuf.curPos() != 0)
        throw runtime_error("Invalid response buffer: Data beyond the authorization area");
    return rpReady;
} // Tpm2::ProcessRespSessions()

/// <summary>  Send a TPM command to the underlying TPM device.  TPM errors are
/// propagated as exceptions by default </summary>
void Tpm2::Dispatch(TPM_CC cmdCode, ReqStructure& req, RespStructure& resp)
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

bool Tpm2::DispatchOut(TPM_CC cmdCode, ReqStructure& req)
{
    if (PendingCommand != 0)
        throw runtime_error("Pending async command must be completed before issuing the next command.");

    if (AuditCommand && CommandAuditHash.hashAlg == TPM_ALG_NULL)
        throw runtime_error("Command audit is not enabled");

    bool hasSessions = req.numAuthHandles() != 0 || Sessions.size();
    SessTag = hasSessions ? TPM_ST::SESSIONS : TPM_ST::NO_SESSIONS;

    TpmBuffer cmdBuf;

    // Standard TPM command header {tag, length, commandCode}
    cmdBuf.writeShort(SessTag);
    cmdBuf.writeInt(0);        // to be filled in later
    cmdBuf.writeInt(cmdCode);
    
    // Handles
    InHandles = req.getHandles();
    for (auto h : InHandles)
        h.toTpm(cmdBuf);
        
    // Marshal command params (without handles) to paramBuf
    TpmBuffer paramBuf;
    req.toTpm(paramBuf);
    paramBuf.trim();

    ByteVec cpHashData;

    //
    // Authorization sessions
    //
    if (hasSessions)
    {
        // We do not know the size of the authorization area yet.
        // Remember the place to marshal it, ...
        size_t authSizePos = cmdBuf.curPos();
        // ... and marshal a placeholder 0 value for now.
        cmdBuf.writeInt(0);

        // If not all required sessions were provided explicitly, the TSS will create the necessary
        // number of password sessions with auth values (if any) from the corresponding TPM_HANDLE objects.
        size_t numExplicitSessions = Sessions.size();
        if (numExplicitSessions < req.numAuthHandles())
        {
            // PwapSession is used as a placeholder for subsequent processing
            Sessions.resize(req.numAuthHandles(), &PwapSession);
        }

        RollNonces();
        PrepareParmEncryptionSessions();

        DoParmEncryption(req, paramBuf, 0, true);

        cpHashData = ProcessAuthSessions(cmdBuf, cmdCode, req.numAuthHandles(), paramBuf.buffer());

        cmdBuf.writeNumAtPos(cmdBuf.curPos() - authSizePos - 4, authSizePos);
    }

    // Write marshaled command params to the command buffer
    cmdBuf.writeByteBuf(paramBuf.buffer());

    // Finally, set the command buffer size
    cmdBuf.writeNumAtPos(cmdBuf.curPos(), 2);

    if (CpHash || AuditCommand)
    {
        if (cpHashData.empty())
            cpHashData = GetCpHashData(cmdCode, paramBuf.buffer());
        if (CpHash)
        {
            CpHash->digest = Crypto::Hash(CpHash->hashAlg, cpHashData);
            ClearInvocationState();
            Sessions.clear();
            CpHash = NULL;
            return false;
        }
        AuditCpHash.digest = Crypto::Hash(CommandAuditHash, cpHashData);
    }

    if (!device)
        throw runtime_error("No TPM device. Use _SetDevice() or the constructor that takes a TpmDevice*");

    LastCommandBuf = cmdBuf.trim();
    device->DispatchCommand(LastCommandBuf);

    UpdateRequestHandles(cmdCode, req);

    PendingCommand = cmdCode;
    return true;
} // Tpm2::DispatchOut()


bool Tpm2::DispatchIn(TPM_CC cmdCode, RespStructure& resp)
{
    if (PendingCommand == 0)
        throw runtime_error("Async command completion with no outstanding command");

    if (PendingCommand != cmdCode)
        throw runtime_error("Async command completion does not match command being processed");

    if (AuditCommand && CommandAuditHash.hashAlg == TPM_ALG_NULL)
        throw runtime_error("Command audit is not enabled");

    PendingCommand = 0;

    ByteVec rawRespBuf = device->GetResponse();
    if (rawRespBuf.size() < 10)
        throw runtime_error("Too short TPM response of " + to_string(rawRespBuf.size()) + " B reveived");

    if (responseCallback != NULL)
        responseCallback(LastCommandBuf, rawRespBuf, responseCallbackContext);

    TpmBuffer respBuf(rawRespBuf);

    // Read the response header
    TPM_ST respTag = respBuf.readShort();
    UINT32 respSize = respBuf.readInt();
    TPM_RC respCode = respBuf.readInt();

    size_t actRespSize = respBuf.size();
    if (respSize != actRespSize)
        throw runtime_error("Inconsistent TPM response buffer: " + to_string(respSize) + 
                            " B reported, " + to_string(actRespSize) + " B received");

    if (respCode == TPM_RC::RETRY)
        return false;

    // Figure out our reaction to the received response. This logic depends on:
    //     _DemandError()  - exception if command succeeded
    //     _AllowError() - no exception, regardless of success or failure
    //     _ExpectError(TPM_RC) - exception if not "expected error."

    LastResponseCode = ResponseCodeFromTpmError(respCode);
    string errMsg = "";
    if (respCode == TPM_RC::SUCCESS)
    {
        CompleteUpdateRequestHandles(cmdCode);
        if (DemandError)
            errMsg = "A TPM error was demanded but the function succeeded";
        else if (ExpectedError != TPM_RC::SUCCESS)
        {
            // We succeeded, while an errors was expected
            errMsg = "Error {" + EnumToStr(ExpectedError) + "} was expected but command " + EnumToStr(cmdCode) + "() succeeded";
        }
    }
    else if (!DemandError && !AllowErrors)
    {
        if (ExpectedError != TPM_RC::SUCCESS)
        {
            if (LastResponseCode != ExpectedError)
            {
                // There was a specifically expected error, but we've got a different one
                errMsg = "TPM returned {" + EnumToStr(LastResponseCode)+ "} instead of expected {" 
                       + EnumToStr(ExpectedError) + "} from" + EnumToStr(cmdCode) + "()";
            }
        }
        else
            errMsg = "TPM Error - TPM_RC::" + EnumToStr(LastResponseCode);
    }

    // Keep a copy of this before we clean out invocation state
    bool auditCommand = AuditCommand;

    ClearInvocationState();

    if (!errMsg.empty())
    {
        Sessions.clear();
        DebugPrint(errMsg);
        throw system_error((UINT32)LastResponseCode, system_category(), errMsg);
    }
    else if (LastResponseCode != TPM_RC::SUCCESS)
    {
        // We've got an expected error. There's nothing more to do here
        Sessions.clear();
        return true;
    }

    // A check for the session tag consistency across the command invocation
    // only makes sense when the command succeeds.
    if (SessTag != respTag)
        throw runtime_error("Wrong response session tag");

    //
    // The command succeeded, so we can process the response buffer
    //

    // Get the handles
    if (resp.numHandles() > 0)
    {
        _ASSERT(resp.numHandles() == 1);
        resp.setHandle(TPM_HANDLE(respBuf.readInt()));
    }

    bool rpReady = false;
    size_t  respParamsPos = 0,
            respParamsSize = 0;

    // If there are no sessions then response parameters take up the remaining part
    // of the response buffer. Otherwise the response parameters area is preceded with
    // its size, and followed by the session area.
    if (SessTag == TPM_ST::SESSIONS)
    {
        respParamsSize = respBuf.readInt();
        respParamsPos = respBuf.curPos();
        rpReady = ProcessRespSessions(respBuf, cmdCode, respParamsPos, respParamsSize);
    }
    else
    {
        respParamsPos = respBuf.curPos();
        respParamsSize = respBuf.size() - respParamsPos;
    }

    if (auditCommand)
    {
        auto rpHash = GetRpHash(CommandAuditHash, respBuf, cmdCode, respParamsPos, respParamsSize, rpReady);
        CommandAuditHash.Extend(Helpers::Concatenate(AuditCpHash, rpHash));
    }

    // Now we can decrypt (if necessary) the first response parameter
    DoParmEncryption(resp, respBuf, respParamsPos, false);

    // ... and unmarshall the whole response parameters area
    respBuf.curPos(respParamsPos);
    resp.initFromTpm(respBuf);
    if (respBuf.curPos() != respParamsPos + respParamsSize)
        throw runtime_error("Bad response parameters area");

    // If there is a returned handle get a pointer to it. It is always the 
    // first element in the structure.
    UpdateRespHandle(cmdCode, resp);

    Sessions.clear();
    return true;
} // Tpm2::DispatchIn()

void Tpm2::UpdateRequestHandles(TPM_CC cc, ReqStructure& req)
{
    // This function is called with the plaintext input parameters. TSS.C++ tries
    // to keep the handle authValue and name correct, but the values are not applied
    // to the handle unless the operation succeeds. So mostly this function records
    // the names and new auth-values for later processing in UpdateRespHandle().
    
    // TODO: This is a bit of a mess.  the inHandles array points to handles in the
    // corresponding command input struct, rather than to the application handles.
    // The AuthVal and names are copied over, but we can't update the handles in
    // the app-space when a ChangeAuth is seen, but we MUST (at least) update the
    // authValue in the library handle so that the session HMAC is OK when the
    // authValue changes in the command.

    objectInName.clear();

    switch (cc) {
        case TPM_CC::HierarchyChangeAuth: {
            auto& r = (TPM2_HierarchyChangeAuth_REQUEST&)req;
            // Note - Change this so that session will work
            r.authHandle.SetAuth(r.newAuth);
            objectInAuth = r.newAuth;
            return;
        }
        case TPM_CC::LoadExternal: {
            auto& r = (TPM2_LoadExternal_REQUEST&)req;
            objectInName = r.inPublic.GetName();
            return;
        }
        case TPM_CC::Load: {
            auto& r = (TPM2_Load_REQUEST&)req;
            objectInName = r.inPublic.GetName();
            return;
        }
        case TPM_CC::NV_ChangeAuth: {
            auto& r = (TPM2_NV_ChangeAuth_REQUEST&)req;
            r.nvIndex.SetAuth(r.newAuth);
            // TODO: Change this so that session will work
            objectInAuth = r.newAuth;
            return;
        }
        case TPM_CC::ObjectChangeAuth: {
            auto& r = (TPM2_ObjectChangeAuth_REQUEST&)req;
            // The command does not change the auth value of the original object
            objectInAuth = r.newAuth;
            return;
        }
        case TPM_CC::PCR_SetAuthValue: {
            auto& r = (TPM2_PCR_SetAuthValue_REQUEST&)req;
            objectInAuth = r.auth;
            return;
        }
        case TPM_CC::EvictControl: {
            auto& r = (TPM2_EvictControl_REQUEST&)req;
            objectInAuth = r.objectHandle.GetAuth();
            if (r.objectHandle.GetHandleType() != TPM_HT::PERSISTENT)
                objectInName = r.objectHandle.GetName();
            return;
        }
        case TPM_CC::Clear: {
            auto& r = (TPM2_Clear_REQUEST&)req;
            const_cast<TPM2_Clear_REQUEST&>(r).authHandle.SetAuth(ByteVec());
            return;
        }
        case TPM_CC::HashSequenceStart: {
            auto& r = (TPM2_HashSequenceStart_REQUEST&)req;
            objectInAuth = r.auth;
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
} // Tpm2::UpdateRequestHandles()

void Tpm2::CompleteUpdateRequestHandles(TPM_CC cc)
{
    switch (cc) {
        case TPM_CC::HierarchyChangeAuth: {
            switch ((TPM_RH) InHandles[0].handle) {
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
            InHandles[0].SetAuth(objectInAuth);
            return;
        }

        case TPM_CC::NV_ChangeAuth: {
            // TODO: Does not work because we make a value copy of the 
            //       input handle in the command dispatcher.
            InHandles[0].SetAuth(objectInAuth);
            return;
        }

        case TPM_CC::ObjectChangeAuth: {
            // TODO: Does not work.
            // TODO: Maintain a map from digests of TpmPrivate to the new auth value
            //       and use it in Load() postprocessing
            return;
        }

        case TPM_CC::PCR_SetAuthValue: {
            // TODO: Does not work.
            InHandles[0].SetAuth(objectInAuth);
            return;
        }

        case TPM_CC::EvictControl: {
            // TODO: Does not work.
            // auto r0 = dynamic_cast<EvictControlResponse*>(response);
            if (InHandles[1].GetHandleType() != TPM_HT::PERSISTENT)
            {
                InHandles[1].SetAuth(objectInAuth);
                InHandles[1].SetName(objectInName);
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
} // Tpm2::CompleteUpdateRequestHandles

void Tpm2::UpdateRespHandle(TPM_CC cc, RespStructure& resp)
{
    // This function is called if a command succeeds. We apply the name and
    // auth-value calculated earlier

    // TODO: Test scenario coverage of this is low.

    switch (cc) {
        case TPM_CC::Load: {
            auto& r = (LoadResponse&)resp;
            assert(r.name == objectInName);
            r.handle.SetName(r.name);
            return;
        }

        case TPM_CC::CreatePrimary: {
            auto& r = (CreatePrimaryResponse&)resp;
            assert(r.name == r.outPublic.GetName());
            r.handle.SetName(r.name);
            return;
        }

        case TPM_CC::LoadExternal: {
            auto& r = (LoadExternalResponse&)resp;
            assert (r.name == objectInName);
            r.handle.SetName(r.name);
            return;
        }

        case TPM_CC::HashSequenceStart: {
            auto& r = (HashSequenceStartResponse&)resp;
            r.handle.SetAuth(objectInAuth);
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
} // Tpm2::UpdateRespHandle()

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
        if (s->IsPWAP())
            continue;
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
        NonceTpmDec = DecSession->NonceTpm;
    }
    if (EncSession && EncSession != Sessions[0] && EncSession != DecSession)
    {
        NonceTpmEnc = EncSession->NonceTpm;
    }
}

void Tpm2::DoParmEncryption(const CmdStructure& cmd, TpmBuffer& paramBuf, size_t startPos, bool request)
{
    AUTH_SESSION *xcryptSess = NULL;
    if (request) {
        if (!DecSession)
            return;
        xcryptSess = DecSession;
    }
    else if (!EncSession)
        return;
    else
        xcryptSess = EncSession;

    auto sei = cmd.sessEncInfo();
    assert(sei.sizeLen != 0 && sei.valLen != 0);

    size_t origCurPos = paramBuf.curPos();
    paramBuf.curPos(startPos);

    // Get the size
    size_t arrSize = paramBuf.readNum(sei.sizeLen),
           arrPos = paramBuf.curPos();
    ByteVec toXcrypt = paramBuf.readByteBuf(arrSize * sei.valLen);

    // Xcrypt it
    ByteVec res = xcryptSess->ParamXcrypt(toXcrypt, request);
    
    // And copy it back into the buffer
    paramBuf.curPos(arrPos);
    paramBuf.writeByteBuf(res);

    paramBuf.curPos(origCurPos);
    EncSession = DecSession = NULL;
} // Tpm2::DoParmEncryption()

TPMT_HA Tpm2::_GetAuditHash() const
{
    if (CommandAuditHash.hashAlg == TPM_ALG_NULL)
        throw runtime_error("Command audit is not enabled");

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

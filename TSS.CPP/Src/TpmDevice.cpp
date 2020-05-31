/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "Tpm2.h"
#include "TpmDevice.h"

_TPMCPP_BEGIN

using namespace std;

using std::to_string;

//
// The Tpm2 class is the main programmer/TPM interface in TSS.C++. However to actually
// communictate with a TPM a "TpmDevice" object is needed. This file contains two classes
// derived from TpmDevice: TpmTcpDevice, that can be used to talk to the TPM simulator
// (or a real or simulated TPM on a remote machine), and TpmTbsDevice that communicates
// through the oeprating sytem TBS (TPM Base Services) interface on Windows.
// 
// It is straightforward to add additional classes for other cases -  e.g. a TPM on
// an embedded system, or a linux platform.
//

TpmDevice::~TpmDevice() {}


TpmTcpDevice::TpmTcpDevice(string _hostName, int _firstPort)
{
    SetTarget(_hostName, _firstPort);
}

void TpmTcpDevice::SetTarget(string _hostName, int _firstPort)
{
    hostName = _hostName;
    commandPort = to_string(_firstPort);
    signalPort = to_string(_firstPort + 1);
    Locality = 0;
}

TpmTcpDevice::~TpmTcpDevice()
{
    closesocket(signalSocket);
    closesocket(commandSocket);
}

/// <summary>Create socket and connect. Note that the host (in dotted IP form)
/// is in this->hostName. </summary>
bool TpmTcpDevice::GetSocket(SOCKET& returnSock, string port)
{
#ifdef WIN32
    struct addrinfo *result = NULL, *ptr = NULL, hints;
    SOCKET sock;
    int res;

    memset(&hints, '\0', sizeof(hints));
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_protocol = IPPROTO_TCP;

    res = getaddrinfo(hostName.c_str(), port.c_str(), &hints, &result);

    if (res != 0)
    {
        printf("error in getaddrinfo: %d\n", res);
        WSACleanup();
        return false;
    }

    for (ptr = result; ptr != NULL; ptr = ptr->ai_next)
    {
        sock = socket(ptr->ai_family, ptr->ai_socktype, ptr->ai_protocol);

        if (sock == INVALID_SOCKET) {
            printf("socket failed with error: %d\n", WSAGetLastError());
            WSACleanup();
            return false;
        }

        res = connect(sock, ptr->ai_addr, (int)ptr->ai_addrlen);

        if (res == SOCKET_ERROR) {
            closesocket(sock);
            sock = INVALID_SOCKET;
            continue;
        }

        break;
    }

    freeaddrinfo(result);

    if (sock == INVALID_SOCKET) {
        printf("Connect failed\n");
        WSACleanup();
        return false;
    }

    returnSock = sock;
#endif

#ifdef __linux__
    struct sockaddr_in sockInfo;
    struct hostent *hPtr;
    int sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    int portNumber = atoi(port.c_str());

    if (sock < 0) {
        close(sock);
        cerr << "Error opening socket.\n";
        return false;
    }

    hPtr = gethostbyname(hostName.c_str());

    if (hPtr == NULL) {
        close(sock);
        cerr << "No such host.\n";
        return false;
    }

    memset((char *)&sockInfo, 0, sizeof(struct sockaddr_in));
    memcpy((char *)&sockInfo.sin_addr.s_addr, (char *)hPtr->h_addr, hPtr->h_length);
    sockInfo.sin_port = htons(portNumber);
    sockInfo.sin_family = AF_INET;

    if (connect(sock, (struct sockaddr *)&sockInfo, sizeof (struct sockaddr_in)) < 0) {
        close(sock);
        cerr << "Error connecting: " << strerror(errno) << "\n";
        return false;
    }

    returnSock = sock;
#endif
    return true;
}

/// <summary> Send exactly toSend bytes to the TPM device </summary>
void Send(SOCKET s, const void* buf, size_t toSend)
{
    size_t numSent = 0;

    while (numSent < toSend)
    {
        int res = send(s, (const char*)buf + numSent, (int)(toSend - numSent), 0);
        if (res == SOCKET_ERROR) {
            printf("send failed: %d\n", WSAGetLastError());
            closesocket(s);
            WSACleanup();
            throw system_error(res, system_category(), "socket send error.  Code = " + to_string(res));
        }
        numSent += res;
    }
}

/// <summary> Send a UINT32 in network byte order </summary>
void SendInt(SOCKET s, UINT32 val)
{
    UINT32 valx = htonl(val);
    Send(s, &valx, 4);
    return;
}

/// <summary> Get exactly toReceive bytes </summary>
void Receive(SOCKET s, void* buf, size_t toReceive)
{
    size_t numGot = 0;
    while (numGot < toReceive)
    {
        int res = recv(s, (char*)buf + numGot, (int)(toReceive - numGot), 0);
        if (res <= 0)
            throw system_error(res, system_category(), "socket recv error.  Code = " + to_string(res));
        numGot += res;
    }
}

/// <summary> Get a UINT32 and translate into host order </summary>
UINT32 ReceiveInt(SOCKET s)
{
    UINT32 _val;
    Receive(s, &_val, 4);
    UINT32 val = ntohl(_val);
    return val;
}

/// <summary> Get an ACK (zero UINT32) from the server </summary>
void GetAck(SOCKET s)
{
    int endTag = ReceiveInt(s);

    if (endTag != 0) {
        if (endTag == 1) {
            throw runtime_error("Operation failed");
        } else {
            throw runtime_error("Bad end tag  for operation ");
        }
    }
}

/// <summary> Send a simulator "signal" emulation request and get an ACK </summary>
void SendCmdAndGetAck(SOCKET s, TcpTpmCommands cmd)
{
    SendInt(s, (int)cmd);
    GetAck(s);
}

bool TpmTcpDevice::Connect(string _hostName, int _firstPort)
{
    hostName = _hostName;
    commandPort = to_string(_firstPort);
    signalPort = to_string(_firstPort + 1);
    Locality = 0;
    return Connect();
}

bool TpmTcpDevice::Connect()
{
    bool ok;

#ifdef WIN32
    // Initialize Winsock
    WSADATA wsaData;
    int res;

    res = WSAStartup(MAKEWORD(2, 2), &wsaData);

    if (res != 0) {
        printf("WSAStartup failed: %d\n", res);
        return false;
    }
#endif

    // Note: the TPM protocol uses two TCP streams: one for commands and one for signals.
    ok = GetSocket(signalSocket, signalPort);
    if (!ok)
        return false;

    ok = GetSocket(commandSocket, commandPort);
    if (!ok)
        return false;

    constexpr int ClientVersion = 1;

    // Make sure the TPM protocol is running
    SendInt(commandSocket, (int)TcpTpmCommands::RemoteHandshake);
    SendInt(commandSocket, ClientVersion);

    int endpointVer = ReceiveInt(commandSocket);
    if (endpointVer != ClientVersion)
        throw runtime_error("Incompatible TPM/proxy");

    ReceiveInt(commandSocket);
    GetAck(commandSocket);
    return true;
}

void TpmTcpDevice::PowerOn()
{
    SendCmdAndGetAck(signalSocket, TcpTpmCommands::SignalPowerOn);
    SendCmdAndGetAck(signalSocket, TcpTpmCommands::SignalNvOn);
}

void TpmTcpDevice::PowerOff()
{
    SendCmdAndGetAck(signalSocket, TcpTpmCommands::SignalPowerOff);
    SendCmdAndGetAck(signalSocket, TcpTpmCommands::SignalNvOff);
}

void TpmTcpDevice::PPOn()
{
    SendCmdAndGetAck(signalSocket, TcpTpmCommands::SignalPPOn);
}

void TpmTcpDevice::PPOff()
{
    SendCmdAndGetAck(signalSocket, TcpTpmCommands::SignalPPOff);
}

void TpmTcpDevice::SetLocality(UINT32 locality)
{
    Locality = locality;
}

UINT32 TpmTcpDevice::GetLocality()
{
    return Locality;
}

ByteVec ReadVarArray(SOCKET s)
{
    int len = ReceiveInt(s);
    ByteVec buf(len);
    Receive(s, &buf[0], len);
    return buf;
}

void TpmTcpDevice::DispatchCommand(const ByteVec& cmdBuf)
{
    TpmBuffer buf;
    BYTE locality = (BYTE)GetLocality();

    // Prepare the command
    buf.writeInt(TcpTpmCommands::SendCommand);
    buf.writeByte(locality);
    buf.writeInt((int)cmdBuf.size());

    ByteVec simCmdHeader = buf.trim();

    // Send to TPM over command stream
    Send(commandSocket, simCmdHeader.data(), (int)simCmdHeader.size());
    Send(commandSocket, cmdBuf.data(), (int)cmdBuf.size());
}

ByteVec TpmTcpDevice::GetResponse()
{
    // Get the response
    ByteVec inBytes = ReadVarArray(commandSocket);

    // And get the terminating ACK
    int ack = ReceiveInt(commandSocket);
    if (ack != 0)
        throw runtime_error("Invalid ACK from the server: " + to_string(ack));
    return inBytes;
}

bool TpmTcpDevice::ResponseIsReady() const
{
    fd_set fds;
    FD_ZERO(&fds);
    FD_SET(commandSocket, &fds);

    timeval tv;
    tv.tv_sec = 0;
    tv.tv_usec = 0;

    // Note that the first argument to select is important on
    // non-WIN32 systems, even though it is ignored by WIN32.
    int numReady = select(((int)commandSocket + 1), &fds, 0, 0, &tv);

    return numReady == 1;
}

#ifdef WIN32
#include <tbs.h>

TpmTbsDevice::TpmTbsDevice()
{
    Locality = 0;
    resSize = 0;
}

TpmTbsDevice::~TpmTbsDevice()
{
}

bool TpmTbsDevice::Connect()
{
    TBS_CONTEXT_PARAMS2 parms;
    parms.includeTpm20 = TRUE;
    parms.version = TBS_CONTEXT_VERSION_TWO;

    TBS_RESULT res = Tbsi_Context_Create((PCTBS_CONTEXT_PARAMS) &parms, &context);

    if (res != TBS_SUCCESS) {
        cerr << "Failed to connect to TBS: " << res << endl;
        return false;
    }

    TPM_DEVICE_INFO info;
    res = Tbsi_GetDeviceInfo(sizeof(info), &info);

    if (res != TBS_SUCCESS)
        cerr << "Failed to GetDeviceInfo" << endl;
    else if (info.tpmVersion != TPM_VERSION_20)
        cerr << "Platform does not contain a TPM2.0" << endl;
    else
        return true;

    Tbsip_Context_Close(context);
    return false;
}

void TpmTbsDevice::DispatchCommand(const ByteVec& outBytes)
{
    // Reset resSize.
    resSize = sizeof(resultBuffer);

    // Submit TPM command to TBS for processing.
    TBS_RESULT res = Tbsip_Submit_Command(context,
                                          TBS_COMMAND_LOCALITY_ZERO,
                                          TBS_COMMAND_PRIORITY_NORMAL,
                                          &outBytes[0],
                                          (UINT32)outBytes.size(),
                                          resultBuffer,
                                          &resSize);
    if (res != TBS_SUCCESS)
        throw runtime_error("TBS SubmitCommand error: 0x" + to_hex(res, 8));

    // TODO: Will need a thread for Async.
}

ByteVec TpmTbsDevice::GetResponse()
{
    if (resSize == 0)
        throw runtime_error("Unexpected TpmTbsDevice::GetResponse()");

    ByteVec inBytes(resultBuffer, resultBuffer + resSize);
    resSize = 0;
    return inBytes;
}

bool TpmTbsDevice::ResponseIsReady() const
{
    if (resSize == 0)
        throw runtime_error("unexpected call or state");
    return true;
}

void TpmTbsDevice::PowerOn()
{
    throw runtime_error("Not supported on this device");
}

void TpmTbsDevice::PowerOff()
{
    throw runtime_error("Not supported on this device");
}

void TpmTbsDevice::PPOn()
{
    throw runtime_error("Not supported on this device");
}

void TpmTbsDevice::PPOff()
{
    throw runtime_error("Not supported on this device");
}

void TpmTbsDevice::SetLocality(UINT32)
{
    throw runtime_error("Not supported on this device");
}

UINT32 TpmTbsDevice::GetLocality()
{
    return 0;
}
#endif

_TPMCPP_END

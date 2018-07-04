/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"
#include "Tpm2.h"
#include "TpmDevice.h"

_TPMCPP_BEGIN

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

TpmDevice::TpmDevice()
{
}

TpmDevice::~TpmDevice()
{
}

TpmTcpDevice::TpmTcpDevice(std::string _hostName, int _firstPort)
{
    SetTarget(_hostName, _firstPort);
}

void TpmTcpDevice::SetTarget(std::string _hostName, int _firstPort)
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
/// is in this->hostName.</summary>
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

    if (res != 0) {
        printf("error in getaddrinfo: %d\n", res);
        WSACleanup();
        return false;
    }

    for (ptr = result; ptr != NULL; ptr = ptr->ai_next) {
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

/// <summary>Send exactly bytesToSend bytes.</summary>
void Send(SOCKET s, BYTE *buf, int bytesToSend)
{
    int res;
    int bytesSent = 0;

    while (bytesSent < bytesToSend) {
        int toSend = bytesToSend - bytesSent;

        res = send(s, (const char *)&buf[bytesSent], toSend, 0);
        if (res == SOCKET_ERROR) {
            printf("send failed: %d\n", WSAGetLastError());
            closesocket(s);
            WSACleanup();
            throw system_error(res, system_category(), "socket send error.  Code = " + to_string(res));
        }

        bytesSent += res;
    }

    return;
}

/// <summary>Send a UINT32 in network byte order.</summary>
void SendInt(SOCKET s, UINT32 val)
{
    UINT32 valx = htonl(val);
    Send(s, (BYTE *) &valx, 4);
    return;
}

/// <summary>Get exactly numBytes bytes.</summary>
void Receive(SOCKET s, BYTE *buf, int numBytes)
{
    int res;
    int numGot = 0;

    while (numGot < numBytes) {
        res = recv(s, (char *) buf + numGot, numBytes, 0);
        if (res <= 0) {
            throw system_error(res, system_category(), "socket recv error.  Code = " + to_string(res));
        }

        numGot += res;
    }

    return;
}

///<summary>Get a UINT32 and translate into host order.</summary>
UINT32 ReceiveInt(SOCKET s)
{
    UINT32 _val;
    Receive(s, (BYTE *)&_val, 4);
    UINT32 val = ntohl(_val);
    return val;
}

///<summary>An ACK in the TPM protocol is a zero UINT32.</summary>
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

///<summary>Send a "simple" command (one with no parms) and get an ACK.</summary>
void SendCmdAndGetAck(SOCKET s, TcpTpmCommands cmd)
{
    SendInt(s, (int)cmd);
    GetAck(s);
}

bool TpmTcpDevice::Connect(std::string _hostName, int _firstPort)
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
    if (!ok) {
        return false;
    }

    ok = GetSocket(commandSocket, commandPort);
    if (!ok) {
        return false;
    }

    int ClientVersion = 1;

    // Make sure the TPM protocol is running
    SendInt(commandSocket, (int)TcpTpmCommands::RemoteHandshake);
    SendInt(commandSocket, ClientVersion);

    int endPointVersion = ReceiveInt(commandSocket);

    if (endPointVersion != ClientVersion) {
        throw runtime_error("Incompatible TPM/proxy");
    }

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

void ReadVarArray(SOCKET s, vector<BYTE>& buf)
{
    int len = ReceiveInt(s);
    buf.resize(len);
    Receive(s, &buf[0], len);
}

void TpmTcpDevice::DispatchCommand(std::vector<BYTE>& outBytes)
{
    OutByteBuf buf;
    BYTE locality = GetLocality();

    // Prepare the command
    buf << (UINT32)TcpTpmCommands::SendCommand << (BYTE)locality << (UINT32)outBytes.size() << outBytes;

    // Send to TPM over command stream
    Send(commandSocket, &buf.GetBuf()[0], buf.GetBuf().size());

    return;
}

void TpmTcpDevice::GetResponse(std::vector<BYTE>& inBytes)
{
    // Get the response
    ReadVarArray(commandSocket, inBytes);

    // And get the terminating ACK
    ReceiveInt(commandSocket);

    return;
}

bool TpmTcpDevice::ResponseIsReady()
{
    fd_set fds;
    FD_ZERO(&fds);
    FD_SET(commandSocket, &fds);

    timeval tv;
    tv.tv_sec = 0;
    tv.tv_usec = 0;

    // Note that the first argument to select is important on
    // non-WIN32 systems, even though it is ignored by WIN32.
    int numReady = select((commandSocket + 1), &fds, 0, 0, &tv);

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
    }

    TPM_DEVICE_INFO info;
    res = Tbsi_GetDeviceInfo(sizeof(info), &info);

    if (res != TBS_SUCCESS) {
        cerr << "Failed to GetDeviceInfo" << endl;
        goto Cleanup;
    }

    if (info.tpmVersion != TPM_VERSION_20) {
        cerr << "Platform does not contain a TPM2.0" << endl;
        goto Cleanup;
    }

    return true;

Cleanup:
    Tbsip_Context_Close(&context);
    return false;
}

void TpmTbsDevice::DispatchCommand(std::vector<BYTE>& outBytes)
{
    // Reset resSize.
    resSize = sizeof(resultBuffer);

    // Submit TPM command to TBS for processing.
    TBS_RESULT res = Tbsip_Submit_Command(context,
                                          TBS_COMMAND_LOCALITY_ZERO,
                                          TBS_COMMAND_PRIORITY_NORMAL,
                                          &outBytes[0],
                                          outBytes.size(),
                                          resultBuffer,
                                          &resSize);
    if (res != TBS_SUCCESS) {
        ostringstream resx;
        resx << "TBS SubmitCommand error: " << hex << res << dec;
        cerr << resx.str() << endl;
        throw runtime_error(resx.str());
    }

    // TODO: Will need a thread for Async.

    return;
}

void TpmTbsDevice::GetResponse(std::vector<BYTE>& inBytes)
{
    if (resSize == 0) {
        throw runtime_error("Unexpected TpmTbsDevice::GetResponse()");
    }

    inBytes.resize(resSize);

    for (size_t j = 0; j < resSize; j++) {
        inBytes[j] = resultBuffer[j];
    }

    resSize = 0;
    return;
}

bool TpmTbsDevice::ResponseIsReady()
{
    if (resSize == 0) {
        throw runtime_error("unexpected call or state");
    }

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

void TpmTbsDevice::SetLocality(UINT32 locality)
{
    throw runtime_error("Not supported on this device");
}

UINT32 TpmTbsDevice::GetLocality()
{
    return 0;
}
#endif

_TPMCPP_END


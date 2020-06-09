/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "Tpm2.h"
#include "TpmDevice.h"

#ifdef WIN32
#   include <tbs.h>
#elif __linux__
#   include <fcntl.h>
#   include <dlfcn.h>
#endif

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

static void ThrowUnsupported(const string& meth)
{
    throw runtime_error("TpmDevice::" + meth + ": Not supported on this device");
}

TpmDevice::~TpmDevice() {}

void TpmDevice::PowerCtl(bool on) { ThrowUnsupported("PowerCtl"); }

void TpmDevice::AssertPhysicalPresence(bool on) { ThrowUnsupported("AssertPhysicalPresence"); }

void TpmDevice::SetLocality(UINT32) { ThrowUnsupported("SetLocality"); }



TpmTcpDevice::TpmTcpDevice(string hostName, int firstPort)
{
    SetTarget(hostName, firstPort);
}

void TpmTcpDevice::SetTarget(string hostName, int port)
{
    HostName = hostName;
    Port = port;
    Locality = 0;
}

TpmTcpDevice::~TpmTcpDevice()
{
    Close();
}

void TpmTcpDevice::Close()
{
    if (commandSocket != INVALID_SOCKET)
    {
        closesocket(commandSocket);
        commandSocket = INVALID_SOCKET;
    }
    if (signalSocket != INVALID_SOCKET)
    {
        closesocket(signalSocket);
        signalSocket = INVALID_SOCKET;
    }
}

/// <summary>Create socket and connect. Note that the host (in dotted IP form)
/// is in this->hostName. </summary>
SOCKET SockConnect(const string& hostName, int port)
{
    SOCKET sock = INVALID_SOCKET;

#ifdef WIN32
    struct addrinfo *result = NULL, *ptr = NULL, hints;
    int res;

    memset(&hints, '\0', sizeof(hints));
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_protocol = IPPROTO_TCP;

    res = getaddrinfo(hostName.c_str(), to_string(port).c_str(), &hints, &result);
    if (res != 0)
    {
        printf("error in getaddrinfo: %d\n", res);
        WSACleanup();
        return INVALID_SOCKET;
    }

    for (ptr = result; ptr; ptr = ptr->ai_next)
    {
        sock = socket(ptr->ai_family, ptr->ai_socktype, ptr->ai_protocol);
        if (sock == INVALID_SOCKET)
        {
            printf("socket failed with error: %d\n", WSAGetLastError());
            WSACleanup();
            return INVALID_SOCKET;
        }

        res = connect(sock, ptr->ai_addr, (int)ptr->ai_addrlen);
        if (res == SOCKET_ERROR)
        {
            closesocket(sock);
            sock = INVALID_SOCKET;
            continue;
        }
        break;
    }

    freeaddrinfo(result);

    if (sock == INVALID_SOCKET)
    {
        printf("Connect failed\n");
        WSACleanup();
        return INVALID_SOCKET;
    }

#elif __linux__

    struct sockaddr_in sockInfo;
    struct hostent *hPtr;
    sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (sock < 0) {
        close(sock);
        cerr << "Error opening socket.\n";
        return INVALID_SOCKET;
    }

    hPtr = gethostbyname(hostName.c_str());
    if (hPtr == NULL) {
        close(sock);
        cerr << "No such host.\n";
        return INVALID_SOCKET;
    }

    memset(&sockInfo, 0, sizeof(struct sockaddr_in));
    memcpy(&sockInfo.sin_addr.s_addr, hPtr->h_addr, hPtr->h_length);
    sockInfo.sin_port = htons(port);
    sockInfo.sin_family = AF_INET;

    if (connect(sock, (struct sockaddr*)&sockInfo, sizeof (struct sockaddr_in)) < 0)
    {
        close(sock);
        cerr << "Error connecting: " << strerror(errno) << "\n";
        return INVALID_SOCKET;
    }
#endif // __linux__

    return sock;
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
void Recv(SOCKET s, void* buf, size_t toReceive)
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
    Recv(s, &_val, 4);
    UINT32 val = ntohl(_val);
    return val;
}

ByteVec RecvVarArray(SOCKET s)
{
    int len = ReceiveInt(s);
    ByteVec buf(len);
    Recv(s, &buf[0], len);
    return buf;
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

bool TpmTcpDevice::Connect(string hostName, int port)
{
    HostName = hostName;
    Port = port;
    Locality = 0;
    return Connect();
}

bool TpmTcpDevice::Connect()
{
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
    signalSocket = SockConnect(HostName, Port + 1);
    if (signalSocket == INVALID_SOCKET)
        return false;

    commandSocket = SockConnect(HostName, Port);
    if (commandSocket == INVALID_SOCKET)
        return false;

    constexpr int ClientVersion = 1;

    // Make sure the TPM protocol is running
    SendInt(commandSocket, (int)TcpTpmCommands::RemoteHandshake);
    SendInt(commandSocket, ClientVersion);

    int endpointVer = ReceiveInt(commandSocket);
    if (endpointVer != ClientVersion)
        throw runtime_error("Incompatible TPM/proxy");

    // Get the endpoint TPM preperties
    TpmInfo = ReceiveInt(commandSocket);

    GetAck(commandSocket);
    return true;
}


void TpmTcpDevice::PowerCtl(bool on)
{
    SendCmdAndGetAck(signalSocket, on ? TcpTpmCommands::SignalPowerOn : TcpTpmCommands::SignalPowerOff);
    SendCmdAndGetAck(signalSocket, on ? TcpTpmCommands::SignalNvOn : TcpTpmCommands::SignalNvOff);
}

void TpmTcpDevice::AssertPhysicalPresence(bool on)
{
    SendCmdAndGetAck(signalSocket, on ? TcpTpmCommands::SignalPPOn : TcpTpmCommands::SignalPPOff);
}

void TpmTcpDevice::SetLocality(UINT32 locality)
{
    Locality = (BYTE)locality;
}

void TpmTcpDevice::DispatchCommand(const ByteVec& cmdBuf)
{
    TpmBuffer buf;
    // Prepare the command
    buf.writeInt(TcpTpmCommands::SendCommand);
    buf.writeByte(Locality);
    buf.writeInt((int)cmdBuf.size());

    ByteVec simCmdHeader = buf.trim();

    // Send to TPM over command stream
    Send(commandSocket, simCmdHeader.data(), (int)simCmdHeader.size());
    Send(commandSocket, cmdBuf.data(), (int)cmdBuf.size());
}

ByteVec TpmTcpDevice::GetResponse()
{
    // Get the response
    ByteVec resp = RecvVarArray(commandSocket);

    // And get the terminating ACK
    int ack = ReceiveInt(commandSocket);
    if (ack != 0)
        throw runtime_error("Invalid ACK from the server: " + to_string(ack));
    return resp;
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


TpmTbsDevice::~TpmTbsDevice()
{
    Close();
}

#ifdef WIN32
void TpmTbsDevice::Close()
{
    if (context)
    {
        Tbsip_Context_Close(context);
        context = nullptr;
    }
}

bool TpmTbsDevice::Connect()
{
    if (context)
        return true;

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
    {
        cerr << "Failed to GetDeviceInfo" << endl;
        return false;
    }
    else if (info.tpmVersion != TPM_VERSION_20)
    {
        cerr << "Platform does not contain a TPM2.0" << endl;
        Tbsip_Context_Close(context);
        context = nullptr;
    }
    return true;
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

    ByteVec resp(resultBuffer, resultBuffer + resSize);
    resSize = 0;
    return resp;
}

bool TpmTbsDevice::ResponseIsReady() const
{
    if (resSize == 0)
        throw runtime_error("unexpected call or state");
    return true;
}

#elif __linux__

#if USE_TCTI
#define LOG_ERR printf

typedef uint32_t TCTI_RC;

#define RC_SUCCESS     0

typedef void* TCTI_HANDLE;

typedef uint32_t (*tcti_init_fn)(TCTI_HANDLE *ctx, size_t *size, const char *cfg);

typedef struct {
    uint32_t version;
    const char *name;
    const char *descr;
    const char *help;
    tcti_init_fn init;
} TCTI_PROV_INFO;

typedef const TCTI_PROV_INFO* (*get_tcti_info_fptr)(void);


typedef struct {
    uint64_t magic;
    uint32_t version;
    TCTI_RC (*transmit) (TCTI_HANDLE h, size_t cmd_size, uint8_t const *command);
    TCTI_RC (*receive) (TCTI_HANDLE h, size_t *resp_size, uint8_t *response, int32_t timeout);
    void (*finalize) (TCTI_HANDLE h);
    TCTI_RC (*cancel) (TCTI_HANDLE h);
    TCTI_RC (*getPollHandles) (TCTI_HANDLE h, void* handles, size_t *num_handles);
    TCTI_RC (*setLocality) (TCTI_HANDLE h, uint8_t locality);
} TCTI_CTX;


TCTI_HANDLE load_abrmd(void** dyLib)
{
    TCTI_HANDLE *tcti_ctx = NULL;
    const TCTI_PROV_INFO *tcti_info;
    const char* abrmd_name = "libtss2-tcti-abrmd.so";
    tcti_init_fn tcti_init = NULL;
    size_t size = 0;
    TCTI_RC rc = 0;

    *dyLib = dlopen (abrmd_name, RTLD_LAZY);
    if (!*dyLib) {
        LOG_ERR("Failed to open %s\n", abrmd_name);
        abrmd_name = "libtss2-tcti-tabrmd.so";
        *dyLib = dlopen (abrmd_name, RTLD_LAZY);
        if (!*dyLib) {
            LOG_ERR("Failed to open %s\n", abrmd_name);
            return NULL;
        }
    }

    get_tcti_info_fptr get_tcti_info = (get_tcti_info_fptr)dlsym(*dyLib, "Tss2_Tcti_Info");
    if (!get_tcti_info) {
        LOG_ERR("No Tss2_Tcti_Info() entry point found in %s\n", abrmd_name);
        goto err;
    }

    tcti_info = get_tcti_info();
#if 0
    uint32_t ver = tcti_info->version;
    printf("TCTI name: %s\n", tcti_info->name);
    printf("TCTI version: %u.%u.%u.%u\n", ver & 0xFF, (ver >> 8) & 0xFF, (ver >> 16) & 0xFF, ver >> 24);
    printf("TCTI descr: %s\n", tcti_info->descr);
    printf("TCTI config help: %s\n", tcti_info->help);
#endif

    tcti_init = tcti_info->init;

    rc = tcti_init(NULL, &size, NULL);
    if (rc != RC_SUCCESS) {
        LOG_ERR("tcti_init(NULL, ...) in %s failed", abrmd_name);
        goto err;
    }
    if (size < sizeof(TCTI_CTX)) {
        LOG_ERR("TCTI context size reported by tcti_init() in %s is too small: %lu < %lu", abrmd_name, size, sizeof(TCTI_CTX));
        goto err;
    }
    printf("Allocated TCTI context of %lu bytes (min expected %lu)", size, sizeof(TCTI_CTX));

    tcti_ctx = (TCTI_HANDLE*)malloc(size);
    if (tcti_ctx == NULL) {
        LOG_ERR("load_abrmd(): malloc failed\n");
        goto err;
    }

    rc = tcti_init(tcti_ctx, &size, NULL);
    if (rc != RC_SUCCESS) {
        LOG_ERR("Tss2_Tcti_Info(ctx, ...) in %s failed", abrmd_name);
        goto err;
    }

    return tcti_ctx;

err:
    dlclose(*dyLib);
    *dyLib = NULL;
    return NULL;
}
#endif // USE_TCTI

void TpmTbsDevice::Close()
{
    if (TpmInfo & TpmSocketConn)
    {
        closesocket(Socket);
    }
    else if (TpmInfo & TpmTbsConn)
    {
        close(DevTpm);
    }
#if USE_TCTI
    else if (TpmInfo & TpmTctiConn)
    {
        ((TCTI_CTX*)Tcti.Ctx)->finalize(Tcti.Ctx);
    }
#endif
    TpmInfo = 0;
}

bool TpmTbsDevice::ConnectToLinuxuserModeTrm()
{
    int oldTrm = access("/usr/lib/x86_64-linux-gnu/libtctisocket.so.0", F_OK) != -1
        || access("/usr/lib/i386-linux-gnu/libtctisocket.so.0", F_OK) != -1;
    //printf ("%sOLD TRM detected\n", oldTrm ? "" : "NO ");
    int newTrm = access("/usr/lib/x86_64-linux-gnu/libtcti-socket.so.0", F_OK) != -1
        || access("/usr/lib/i386-linux-gnu/libtcti-socket.so.0", F_OK) != -1
        || access("/usr/local/lib/libtss2-tcti-tabrmd.so.0", F_OK) != -1;
    //printf ("%sNEW TRM detected\n", newTrm ? "" : "NO ");
    if (!(oldTrm || newTrm))
        return false;

    Socket = SockConnect("127.0.0.1", 2323);
    if (Socket == INVALID_SOCKET)
    {
        cerr << "Failed to connect to the user TRM" << endl;
        return false;
    }

    // No handshake with user mode TRM

    // Neither we need a platform connection
#if 0
    s = SockConnect("127.0.0.1", 2324);
    if (s == INVALID_SOCKET)
    {
        SockClose(Socket);
        return false;
    }
    SendInt(s, Remote_SessionEnd);
    SockClose(s);
#endif

    TpmInfo = TpmSocketConn | TpmUsesTrm | TpmNoPowerCtl | TpmNoLocalityCtl
            | (oldTrm ? TpmLinuxOldUserModeTrm : 0);
    return true;
}


bool TpmTbsDevice::Connect()
{
    if (TpmInfo != 0)
        return true;

    int fd = open("/dev/tpm0", O_RDWR);
    if (fd < 0) {
#if USE_TCTI
        Tcti.Ctx = load_abrmd(&Tcti.DyLib);
        //printf("TCTI Ctx = %p\n", tpm->TpmConnHandle.Tcti.Ctx);
        if (Tcti.Ctx) {
            printf("Successfully initialized abrmd\n");
            TpmInfo = TpmTctiConn | TpmUsesTrm | TpmNoPowerCtl | TpmNoLocalityCtl;
            return true;
        }
#endif
        fd = open("/dev/tpmrm0", O_RDWR);
        if (fd < 0) {
            printf("Unable to open tpm0, abrmd, or tpmrm0: error %d (%s)\n", errno, strerror(errno));
            return ConnectToLinuxuserModeTrm();
        }
        TpmInfo |= TpmUsesTrm;
    }

    DevTpm = fd;
    TpmInfo |= TpmTbsConn | TpmNoPowerCtl | TpmNoLocalityCtl;
    return true;
}

void TpmTbsDevice::DispatchCommand(const ByteVec& cmdBuf)
{
    if (TpmInfo & TpmSocketConn)
    {
        // Send the command to the TPM
        TpmBuffer buf;
        buf.writeInt(TcpTpmCommands::SendCommand);
        buf.writeByte(0);   // locality
        buf.writeInt((int)cmdBuf.size());

        if (TpmInfo & TpmLinuxOldUserModeTrm)
        {
            buf.writeByte(0);   // debugMsgLevel
            buf.writeByte(1);   // commandSent
        }

        ByteVec cmdHeader = buf.trim();
        Send(Socket, cmdHeader.data(), cmdHeader.size());
        Send(Socket, cmdBuf.data(), cmdBuf.size());
    }
    else if (TpmInfo & TpmTbsConn)
    {
        ssize_t bytesWritten = write(DevTpm, cmdBuf.data(), cmdBuf.size());
        if ((size_t)bytesWritten != cmdBuf.size()) {
            fprintf(stderr, "Failed to write TPM command (written %zd out of %zu): %d (%s); fd = %d\n",
                    bytesWritten, cmdBuf.size(), errno, strerror(errno), DevTpm);
            throw runtime_error("Failed to write TPM comamnd to the system TPM");
        }
    }
#if USE_TCTI
    else if (TpmInfo & TpmTctiConn)
    {
        //printf("    > Tcti.Ctx: %p\n", tcti_ctx);
        //printf("    > Sending command of %d bytes\n", cmdBuf.size());
        TCTI_RC rc = ((TCTI_CTX*)Tcti.Ctx)->transmit(Tcti.Ctx, cmdBuf.size(), cmdBuf.data());
        if (rc != 0)
        {
            printf("TCTI_CTX::transmit() failed: 0x%08X\n", rc);
            throw runtime_error("Failed to transmit TPM comamnd to the TRM");
        }
    }
#endif
}

ByteVec TpmTbsDevice::GetResponse()
{
    // Read the TPM response
    if (TpmInfo & TpmSocketConn)
    {
        ByteVec resp = RecvVarArray(Socket);

        // And get the terminating ACK
        int ack = ReceiveInt(Socket);
        if (ack != 0)
            throw runtime_error("Invalid ACK from the server: " + to_string(ack));
        return resp;
    }

    BYTE respBuf[4096];
    ssize_t bytesRead;

    if (TpmInfo & TpmTbsConn)
    {
        bytesRead = read(DevTpm, respBuf, sizeof(respBuf));
        if (bytesRead < 10)
        {
            // 10 is the mandatory response header size
            printf("Failed to read the response: bytes read %zd, error %d (%s)\n",
                   bytesRead, errno, strerror(errno));
            throw runtime_error("Failed to read TPM comamnd from the system TPM");
        }
    }
#if USE_TCTI
    else if (TpmInfo & TpmTctiConn)
    {
        bytesRead = sizeof(respBuf);
        memset(respBuf, 0, 10);
        TCTI_RC rc = ((TCTI_CTX*)Tcti.Ctx)->receive(Tcti.Ctx, (size_t*)&bytesRead, respBuf, 5 * 60 * 1000);
        if (rc != 0)
        {
            printf("TCTI_CTX::receive() failed: 0x%08X\n", rc);
            throw runtime_error("Failed to receive response from the TRM");
        }

        uint32_t respSize = ntohl(*((uint32_t*)(respBuf + 2)));
        //printf("    > Receved response of %d vs. %lu bytes\n", respSize, bytesRead);
        bytesRead = (UINT32)(respSize < bytesRead ? respSize : bytesRead);
    }
#endif
    else
        throw runtime_error("Invalid TPM connection type");

    return ByteVec((BYTE*)respBuf, (BYTE*)respBuf + bytesRead);
}

bool TpmTbsDevice::ResponseIsReady() const
{
    return true;
}

#endif // __linux__

_TPMCPP_END

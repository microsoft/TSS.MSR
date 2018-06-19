/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#pragma once

_TPMCPP_BEGIN

#ifdef __linux__
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <errno.h>
#include <unistd.h>

typedef int SOCKET;

#define closesocket(A) close(A)

#define INVALID_SOCKET -1
#define SOCKET_ERROR   -1

#define WSACleanup()
#define WSAGetLastError() (-1)
#endif

enum class TcpTpmCommands {
    SignalPowerOn = 1,
    SignalPowerOff = 2,
    SignalPPOn = 3,
    SignalPPOff = 4,
    SignalHashStart = 5,
    SignalHashData = 6,
    SignalHashEnd = 7,
    SendCommand = 8,
    SignalCancelOn = 9,
    SignalCancelOff = 10,
    SignalNvOn = 11,
    SignalNvOff = 12,
    SignalKeyCacheOn = 13,
    SignalKeyCacheOff = 14,
    RemoteHandshake = 15,
    SetAlternativeResult = 16,
    SessionEnd = 20,
    Stop = 21,

    TestFailureMode = 30
};

///<summary>TpmDevice is the base abstract class for communicating with TPMs. All virtual
/// fucntions should be overridden when implementing code to talk to a TPM device on
/// a particular interface</summary>
class _DLLEXP_ TpmDevice {
    public:
        TpmDevice();
        ~TpmDevice();

        ///<summary>Send the TPM-formatted byte-stream outBytes to the TPM.  For some devices
        /// this will be a non-blocking operation. For others tis will block until the
        /// TPM responds.</summary>
        virtual void DispatchCommand(std::vector<BYTE>& outBytes) = 0;

        ///<summary>Get the response from the TPM (may block if the device is async and the
        /// response is not ready).</summary>
        virtual void GetResponse(std::vector<BYTE>& inBytes) = 0;

        ///<summary>(after dispatch command) is the response data from the TPM ready?</summary>
        virtual bool ResponseIsReady() = 0;

        ///<summary>Establish connection with the TPM device.</summary>
        virtual bool Connect() = 0;

        ///<summary>Power-on the TPM (typically only implemented for simulator).</summary>
        virtual void PowerOn() = 0;

        ///<summary>Power-op the TPM (typically only implemented for simulator).</summary>
        virtual void PowerOff() = 0;

        ///<summary>Assert physical presence for the commands that follow (typically
        /// only implemented for simulator).</summary>
        virtual void PPOn() = 0;

        ///<summary>End assertion of physical presence for the commands that follow
        /// (typically only implemented for simulator).</summary>
        virtual void PPOff() = 0;

        ///<summary>Send commands that follow at the specified locality (typically
        /// only implemented for the simulator).</summary>
        virtual void SetLocality(UINT32 locality) = 0;

        ///<summary>Get the locality at which commands are being issued.</summary>
        virtual UINT32 GetLocality() = 0;
};

///<summary>TpmTcpDevice connects to a TPM-simulator over a TCP connection TpmTcpConnection
/// can also be used to connect to a remote TPM via a network proxy.</summary>
class _DLLEXP_ TpmTcpDevice : public TpmDevice {
    public:
        ///<summary>TpmTcpDevice connects to a TPM-simulator over a TCP connection
        ///TpmTcpConnection can also be used to connect to a remote TPM via a network
        ///proxy.</summary>
        TpmTcpDevice(std::string _hostName = "127.0.0.1", int _firstPort = 2321);
        ~TpmTcpDevice();

        ///<summary>Set the address of the TPM simulator or proxy</summary>
        void SetTarget(std::string _hostName, int _firstPort);

        ///<summary>Attempt to connect to the TPM simulator or proxy at the previously 
        ///set address.</summary>
        virtual bool Connect();

        ///<summary>Attempt to connect to the TPM simulator or proxy at the named address. 
        ///Dotted or DNS names are accepted.</summary>
        bool Connect(std::string _hostName, int _firstPort);

        virtual void DispatchCommand(std::vector<BYTE>& outBytes);
        virtual void GetResponse(std::vector<BYTE>& inBytes);
        virtual bool ResponseIsReady();

        virtual void PowerOn();
        virtual void PowerOff();
        virtual void PPOn();
        virtual void PPOff();

        virtual void SetLocality(UINT32 locality);
        virtual UINT32 GetLocality();

    protected:
        bool GetSocket(SOCKET& sock, string port);
        string hostName;
        string commandPort, signalPort;
        SOCKET commandSocket;
        SOCKET signalSocket;
        UINT32 Locality = 0;
};

#ifdef WIN32
class _DLLEXP_ TpmTbsDevice : public TpmDevice {
    public:
        TpmTbsDevice();
        ~TpmTbsDevice();

        virtual bool Connect();

        virtual void DispatchCommand(std::vector<BYTE>& outBytes);
        virtual void GetResponse(std::vector<BYTE>& inBytes);
        virtual bool ResponseIsReady();

        virtual void PowerOn();
        virtual void PowerOff();
        virtual void PPOn();
        virtual void PPOff();

        virtual void SetLocality(UINT32 locality);
        virtual UINT32 GetLocality();

    protected:
        void *context;
        UINT32 Locality = 0;
        BYTE resultBuffer[8192];
        UINT32 resSize;
};
#endif

_TPMCPP_END
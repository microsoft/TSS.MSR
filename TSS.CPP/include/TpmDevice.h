/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once

_TPMCPP_BEGIN

#if __linux__
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <errno.h>
#include <unistd.h>

typedef int SOCKET;

#define closesocket(s) close(s)

#define INVALID_SOCKET -1
#define SOCKET_ERROR   -1

#define WSACleanup()
#define WSAGetLastError() (-1)

#define USE_TCTI    1

#endif // __linux__


enum TcpTpmCommands {
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

/// <summary> Partially abstract base class for classes implementing communication 
/// interface with TPM devices of different kinds (e.g. a simulator, 
///TBS interface on Windows or /dev/tpm0 on linux). </summary>
class _DLLEXP_ TpmDevice
{
protected:
    // A set of TSS_TPM_CONN_INFO flags
    UINT32      TpmInfo = 0;

public:
    TpmDevice() {}
    virtual ~TpmDevice();

    /// <summary> Establishes a connection with the TPM device. </summary>
    /// <returns> Whether the connection was established </returns>
    virtual bool Connect() = 0;

    /// <summary> Closes the established connection with the TPM device. </summary>
    virtual void Close() = 0;

    /// <summary> Sends the given TPM command buffer to the TPM. </summary>
    /// <remarks> This may be either blocking or a non-blocking operation. </remarks>
    virtual void DispatchCommand(const ByteVec& outBytes) = 0;

    /// <summary> Gets the response from the TPM (may block if the device is async and the
    /// response is not ready). </summary>
    virtual ByteVec GetResponse() = 0;

    /// <summary> (after dispatch command) is the response data from the TPM ready? </summary>
    virtual bool ResponseIsReady() const = 0;


    /// <summary> Powers on/off the TPM. </summary>
    /// <remarks> Only implemented for TPM simulators and TPM vendors test harness. </remarks> 
    virtual void PowerCtl(bool on);

    /// <summary> Asserts or stop asserting Physical Presence. </summary>
    /// <remarks> Only implemented for TPM simulators and TPM vendors test harness. </remarks> 
    virtual void AssertPhysicalPresence(bool on);

    /// <summary> Sets the locality for subsequent commands. </summary>
    /// <remarks> Only implemented for TPM simulators and TPM vendors test harness. </remarks> 
    virtual void SetLocality(UINT32 locality);

    /// <summary>
    /// Queries whether the TPM device supports sending/emulation of platform signals,
    /// and if the platform hierarchy is enabled. In particular platform signals
    /// are required to power-cycle the TPM.
    /// </summary>
    bool PlatformAvailable() const
    {
        return (TpmInfo & TpmPlatformAvailable) != 0;
    }

    /// <summary> Queries whether the TPM device can be power cycled programmatically. </summary>
    bool PowerCtlAvailable() const
    {
        return PlatformAvailable() && (TpmInfo & TpmNoPowerCtl) == 0;
    }

    /// <summary> Queries whether the TPM device allows changing locality programmatically. </summary>
    bool LocalityCtlAvailable() const
    {
        return PlatformAvailable() && (TpmInfo & TpmNoLocalityCtl) == 0;
    }

    /// <summary> Queries whether physical presence can be asserted </summary>
    bool ImplementsPhysicalPresence() const
    {
        return (TpmInfo & TpmSupportsPP) != 0;
    }

    /// <remarks> Convenience wrapper for PowerCtl(true) </remarks>
    void PowerOn()
    {
        PowerCtl(true);
    }

    /// <remarks> Convenience wrapper for PowerCtl(false) </remarks>
    void PowerOff()
    {
        PowerCtl(false);
    }

    /// <remarks> Convenience wrapper for PowerCtl() </remarks>
    void PowerCycle()
    {
        PowerCtl(false);
        PowerCtl(true);
    }
    
    /// <remarks> Convenience wrapper for AssertPhysicalPresence(true) </remarks>
    void PpOn()
    {
        AssertPhysicalPresence(true);
    }

    /// <remarks> Convenience wrapper for AssertPhysicalPresence(false) </remarks>
    void PpOff()
    {
        AssertPhysicalPresence(false);
    }

protected:
    enum TSS_TPM_CONN_INFO
    {
        // Platform hierarchy is enabled, and hardware platform functionality (such
        // as SignalHashStart/Data/End) is available.
        TpmPlatformAvailable = 0x01,

        // The connection represents a TPM Resource Manager (TRM), rather than TPM device.
        // This means context management commands are unavailable, and the handle values
        // returned to the client are virtualized.
        TpmUsesTrm = 0x02,

        // The TRM is in raw mode (i.e. no actual resourse virtualization is performed).
        TpmInRawMode = 0x04,

        // Phisical presence signals (SignalPPOn/Off) are supported.
        TpmSupportsPP = 0x08,

        // Valid only with PlatformAvailable set.
        // System and TPM power control signals (SignalPowerOn/Off) are not supported.
        // This flag is negative for backward compatibility with older TPM simulator protocol versions.
        TpmNoPowerCtl = 0x10,

        // Valid only with TpmPlatformAvailable set.
        // TPM locality cannot be changed.
        // This flag is negative for backward compatibility with older TPM simulator protocol versions.
        TpmNoLocalityCtl = 0x20,

        //
        // Endpoint type descriptors
        //

        // Connection medium is socket.
        // Mutually exclusive with TpmTbsConn for better error checking
        TpmSocketConn = 0x1000,

        // Connection medium is an OS/platform specific handle representing the TPM device
        // used by the OS/platform. Mutually exclusive with TpmSocketConn.
        TpmTbsConn = 0x2000,

        // Valid with TpmSocketConn only. This is a socket connection to an old
        // version of the Intel's user mode TRM implementation on Linux
        TpmLinuxOldUserModeTrm = 0x4000,

        // Connection via a context representing TCG compliant TCTI connection interface
        TpmTctiConn = 0x8000
    };
}; // class TpmDevice


/// <summary> TpmTcpDevice connects to a TPM-simulator over a TCP connection TpmTcpConnection
/// can also be used to connect to a remote TPM via a network proxy. </summary>
class _DLLEXP_ TpmTcpDevice : public TpmDevice
{
public:
    /// <summary> TpmTcpDevice connects to a TPM-simulator over a TCP protocol.
    /// TpmTcpConnection can also be used to connect to a remote TPM via a network
    /// proxy. </summary>
    TpmTcpDevice(string hostName = "127.0.0.1", int port = 2321);
    ~TpmTcpDevice();

    /// <summary> Set the address of the TPM simulator or proxy </summary>
    void SetTarget(string hostName, int port = 2321);

    virtual bool Connect();
    virtual void Close();

    /// <summary> Attempt to connect to the TPM simulator or proxy at the named address. 
    /// Dotted or DNS names are accepted. </summary>
    bool Connect(string hostName, int port);

    virtual void DispatchCommand(const ByteVec& outBytes);
    virtual ByteVec GetResponse();
    virtual bool ResponseIsReady() const;

    virtual void PowerCtl(bool on);
    virtual void AssertPhysicalPresence(bool on);
    virtual void SetLocality(UINT32 locality);

protected:
    string HostName;
    int Port;
    SOCKET commandSocket = INVALID_SOCKET;
    SOCKET signalSocket = INVALID_SOCKET;
    BYTE   Locality = 0;
};


class _DLLEXP_ TpmTbsDevice : public TpmDevice
{
public:
    TpmTbsDevice() = default;
    ~TpmTbsDevice();

    virtual bool Connect();
    virtual void Close();

    virtual void DispatchCommand(const ByteVec& outBytes);
    virtual ByteVec GetResponse();
    virtual bool ResponseIsReady() const;

protected:
#ifdef WIN32
    void *context = nullptr;
    BYTE resultBuffer[4096];
    UINT32 resSize = 0;
#elif __linux__
    bool ConnectToLinuxuserModeTrm();

    union {
        SOCKET  Socket;
        int     DevTpm;
    };
#if USE_TCTI
    struct {
        void*   Ctx = nullptr;
        void*   DyLib = nullptr;
    } Tcti;
#endif
#endif // __linux__
};

_TPMCPP_END

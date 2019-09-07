import abc
from socket import *
from .TpmTypes import *

class TpmError(Exception):
    def __init__(self, responseCode, tpmCommand, errorMessage):
        Exception.__init__(self, errorMessage)
        self.responseCode = responseCode
        self.tpmCommand = tpmCommand
        #self.errorMessage = errorMessage

if NewPython:
    class TpmDevice(abc.ABC):
        # Returns an error object if connection attempt fails before asyncronous phase commences
        @abc.abstractmethod
        def connect(self):
            pass

        # Sends the command buffe in the TPM wire format to the TPM device,
        # and returns the TPM response buffer via the callback.
        @abc.abstractmethod
        def dispatchCommand(self, commandBuffer):
            pass

        # Closes the connection with the TPM device and releases associated system resources
        @abc.abstractmethod
        def close(self):
           pass

else:
    class TpmDevice(object):
        # Returns an error object if connection attempt fails before asyncronous phase commences
        __metaclass__ = abc.ABCMeta

        @abc.abstractmethod
        def connect(self):
            pass

        # Sends the command buffe in the TPM wire format to the TPM device,
        # and returns the TPM response buffer via the callback.
        @abc.abstractmethod
        def dispatchCommand(self, commandBuffer):
            pass

        # Closes the connection with the TPM device and releases associated system resources
        @abc.abstractmethod
        def close(self):
           pass


class TpmTbsDevice(TpmDevice):

    # override
    def connect(self):
        from ctypes import windll, Structure, byref, c_int, c_void_p
        self.__tbs = windll.LoadLibrary('Tbs')
        self.__tbsCtx = c_void_p()
        self.__c_int = c_int
        self.__byref = byref

        class TbsContextParams(Structure):
            _fields_ = [("version", c_int),
                        ("params", c_int)]

        tbsCtxParams = TbsContextParams(2, 1 << 2)
        res = self.__tbs.Tbsi_Context_Create(byref(tbsCtxParams), byref(self.__tbsCtx))
        if (res != 0):
            raise(Exception('Tbsi_Context_Create() failed: error ' + hex(res)))

    # override
    def dispatchCommand(self, commandBuffer):
        responseBuffer = bytes(4096)
        respLen = self.__c_int(4096)
        res = self.__tbs.Tbsip_Submit_Command(self.__tbsCtx, 0, 0, bytes(commandBuffer), len(commandBuffer), responseBuffer, self.__byref(respLen))
        if (res != 0):
            raise(Exception('Tbsip_Submit_Command() failed: error ' + hex(res)))
        return responseBuffer[:respLen.value]

    # override
    def close(self):
        if (self.__tbs):
            res = self.__tbs.Tbsip_Context_Close(self.__tbsCtx)
            if (res != 0):
                raise(Exception('Tbsi_Context_Close() failed: error ' + hex(res)))

# end of class TpmTbsDevice


class TpmLinuxDevice(TpmDevice):

    # override
    def connect(self):
        try:
            self.__devTpmHandle = open('/dev/tpm0', 'wb+', buffering=0)
            #print('Connected to the raw TPM device')
        except:
            try:
                self.__devTpmHandle = open('/dev/tpmrm0', 'wb+', buffering=0)
                #print('Connected to the kernel TRM')
            except :
                raise(Exception('Failed to connect to the system TPM'))

    # override
    def dispatchCommand(self, commandBuffer):
        self.__devTpmHandle.write(commandBuffer)
        return self.__devTpmHandle.read()

    # override
    def close(self):
        if (self.__devTpmHandle):
            self.__devTpmHandle.close()

# end of class TpmLinuxDevice

class TSS_TPM_INFO(TpmEnum):
    # Flags corresponding to the TpmEndPointInfo values used by the TPM simulator
    TSS_TpmPlatformAvailable = 0x01
    TSS_TpmUsesTbs = 0x02
    TSS_TpmInRawMode = 0x04
    TSS_TpmSupportsPP = 0x08

    # TPM connection type. Flags are mutually exclusive for better error checking
    TSS_SocketConn = 0x1000
    TSS_TbsConn = 0x2000


class TPM_TCP_PROTOCOL(TpmEnum):
    SignalPowerOn = 1
    #SignalPowerOff = 2
    SendCommand = 8
    SignalNvOn = 11
    #SignalNvOff = 12
    HandShake = 15
    SessionEnd = 20
    Stop = 21

class TSS_TPM_INFO(TpmEnum):
    # Flags corresponding to the TpmEndPointInfo values used by the TPM simulator
    TSS_TpmPlatformAvailable = 0x01
    TSS_TpmUsesTbs = 0x02
    TSS_TpmInRawMode = 0x04
    TSS_TpmSupportsPP = 0x08

    # TPM connection type. Flags are mutually exclusive for better error checking
    TSS_SocketConn = 0x1000
    TSS_TbsConn = 0x2000

"""
if not NewPython:
    enumsFixedUp = False
    def fixupTpmDeviceEnums():
        global enumsFixedUp
        if not enumsFixedUp:
            enumsFixedUp = True
            FixupEnumerators(TSS_TPM_INFO)
            FixupEnumerators(TPM_TCP_PROTOCOL)
            FixupEnumerators(TSS_TPM_INFO)
"""

def int32toTpm(val):
    return intToTpm(val, 4)
    #v = int(val)
    #return (v & 0xFF) << 24 | (v & 0xFF00) << 8 | (v & 0xFF0000) >> 8 | (v & 0xFF000000) >> 24
    #return bytesFromList([(v & 0xFF000000) >> 24, (v & 0xFF0000) >> 16, (v & 0xFF00) >> 8, v & 0xFF])

def int16toTpm(val):
    return intToTpm(val, 2)
    #v = int(val)
    #return (v & 0xFF) << 8 | (v & 0xFF00) >> 8
    #return bytesFromList([(v & 0xFF00) >> 8, v & 0xFF])

class TpmTcpDevice(TpmDevice):

    def __init__(self, host = '127.0.0.1', port = 2321, linuxTrm = False):
        self.__host = host
        self.__port = port
        self.__linuxTrm = linuxTrm
        self.__oldTrm = True
        self.__tpmSocket = 0
        self.__platSocket = 0
        self.__tpmInfo = 0

    # override
    def connect(self):
        self.__tpmSocket = socket(AF_INET, SOCK_STREAM)
        self.__platSocket = socket(AF_INET, SOCK_STREAM)

        self.__tpmSocket.connect((self.__host, self.__port))
        if (self.__linuxTrm):
            #raise(Exception('Linux TRM not impl'))
            cmdGetRandom = bytesFromList(
                               [0x80, 0x01,             # TPM_ST_NO_SESSIONS
                                0, 0, 0, 0x0C,          # length
                                0, 0, 0x01, 0x7B,       # TPM_CC_GetRandom
                                0, 0x08                 # Cmd param
            ])
            try:
                resp = self.dispatchCommand(cmdGetRandom)
            except:
                resp = []
            if (len(resp) != 20):
                if (self.__oldTrm):
                    self.__oldTrm = False
                    self.close()
                    self.connect()
                else:
                    raise(Exception('Connection to Linux TRM failed'))
                #raise(Exception('Probe TPM2_GetRandom() failed'))
            #else: connection to Linux TRM established
        else:
            ClientVer = 1
            req = bytesFromList([0, 0, 0, int(TPM_TCP_PROTOCOL.HandShake),
                                 0, 0, 0, int(ClientVer)])
            self.__tpmSocket.send(req)
            resp = bytesFromList([])
            while (len(resp) < 12):
                resp = resp + self.__tpmSocket.recv(32)
            if (len(resp) != 12):
                raise(Exception('Wrong length of the handshake response ' + str(len(resp)) + ' bytes instead of 12'))
            svrVer = intFromTpm(resp, 0, 4)
            if (svrVer != ClientVer):
                raise(Exception('Too old TCP server version:', svrVer))
            self.__tpmInfo = intFromTpm(resp, 4, 4)
            ack = intFromTpm(resp, 8, 4)
            if (ack != 0):
                raise(Exception('Bad ack', ack, 'for the handshake sequence'))
            self.__tpmInfo |= int(TSS_TPM_INFO.TSS_SocketConn);

            self.__platSocket.connect((self.__host, self.__port + 1))

            platReq = bytesFromList([0, 0, 0, int(TPM_TCP_PROTOCOL.SignalPowerOn)])
            self.__platSocket.send(platReq)
            platResp = self.__platSocket.recv(32)
            ack = intFromTpm(platResp, 0, 4)
            if (len(platResp) != 4 or ack != 0):
                raise(Exception('Bad ack ' + str(ack) + ' for the simulator Power-ON command'))

            platReq = bytesFromList([0, 0, 0, int(TPM_TCP_PROTOCOL.SignalNvOn)])
            self.__platSocket.send(platReq)
            platResp = self.__platSocket.recv(32)
            ack = intFromTpm(platResp, 0, 4)
            if (len(platResp) != 4 or ack != 0):
                raise(Exception('Bad ack ' + str(ack) + ' for the simulator NV-ON command'))

            cmdStartup = bytesFromList([
                    0x80, 0x01,             # TPM_ST_NO_SESSIONS
                    0, 0, 0, 0x0C,          # Cmd buf length
                    0, 0, 0x01, 0x44,       # TPM_CC_Startup
                    0, 0                    # Cmd param: TPM_SU_CLEAR
                ])
            self.dispatchCommand(cmdStartup)

    # override
    def dispatchCommand(self, commandBuffer):
        self.__tpmSocket.send(int32toTpm(TPM_TCP_PROTOCOL.SendCommand))
        # locality
        self.__tpmSocket.send(bytesFromList([0]))
        if (self.__linuxTrm and self.__oldTrm):
            # debugMsgLevel, commandSent status bit
            self.__tpmSocket.send(bytesFromList([0, 1]))
        self.__tpmSocket.send(int32toTpm(len(commandBuffer)))
        self.__tpmSocket.send(commandBuffer)
        
        resp = self.__tpmSocket.recv(4096)
        respLen = intFromTpm(resp, 0, 4)
        while (len(resp) < respLen + 8):
            resp = resp + self.__tpmSocket.recv(4096)
        #print('dispatchCommand returned', len(resp), 'bytes; reported in the header', respLen)
        if (respLen != len(resp) - 8):
            raise(Exception('Invalid size tag ' + str(respLen) + ' for the TPM response of '
                                      + str(len(resp) - 8) + ' bytes'))
        ack = intFromTpm(resp, respLen + 4, 4)
        if (ack != 0):
            raise(Exception('Bad ack during regular command dispatch'))
        return resp[4 : respLen + 4]

    # override
    def close(self):
        if (self.__tpmSocket):
            self.__tpmSocket.close()
        if (self.__platSocket):
            self.__platSocket.close()
# end of class TpmTcpDevice

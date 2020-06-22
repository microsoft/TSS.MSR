import abc
import sys

NewPython = sys.version_info[0] > 3 or (sys.version_info[0] == 3 and sys.version_info[1] > 3)

if (NewPython):
    from enum import IntFlag

    class TpmEnum(IntFlag):
        pass

    class TpmMarshaller(abc.ABC):
        'Base interface for all marshalable non-trivial TPM data types'

        @abc.abstractmethod
        def toTpm(self, buf):
            """Convert this object to its TPM representation and store in the output byte buffer object
        
            Parameters
            ----------
            buf: TpmBuffer
                Output byte buffer for the marshaled representation of this object
            """
            pass

        @abc.abstractmethod
        def fromTpm(self, buf):
            """Populate this object from the TPM representation in the input byte buffer object

            Parameters
            ----------
            buf: TpmBuffer
                Input byte buffer with the marshaled representation of the object
            """
            pass
    # interface TpmMarshaller

else:
    class TpmEnum:
        def __init__(self, val, name = None):
            self.__value = val
            self.__name = name if name else str(val)

        def __init_subclass__(cls, **kwargs):
            super().__init_subclass__(**kwargs)

        def __int__(self):
            return self.__value

        def __repr__(self):
            return str(self)

        def __str__(self):
            return str(self.__class__.__name__ + '.' + self.__name)

        def __bool__(self):
            return self.__value != 0

        def __nonzero__(self):
            return self.__value != 0

        def __or__(self, other):
            if not isinstance(other, (self.__class__, int)):
                return NotImplemented
            return self.__class__(self.__value | int(other))

        def __and__(self, other):
            if not isinstance(other, (self.__class__, int)):
                return NotImplemented
            return self.__class__(self.__value & int(other))

        def __xor__(self, other):
            if not isinstance(other, (self.__class__, int)):
                return NotImplemented
            return self.__class__(self.__value ^ int(other))

        def __lshift__(self, other):
            if not isinstance(other, (self.__class__, int)):
                return NotImplemented
            return self.__class__(self.__value << int(other))

        def __rshift__(self, other):
            if not isinstance(other, (self.__class__, int)):
                return NotImplemented
            return self.__class__(self.__value >> int(other))

        def __eq__(self, other):
            if not isinstance(other, (self.__class__, int)):
                return NotImplemented
            return self.__value == int(other)

        def __ne__(self, other):
            return not self.__eq__(other)

        def __lt__(self, other):
            if not isinstance(other, (self.__class__, int)):
                return NotImplemented
            return self.__value < int(other)

        def __le__(self, other):
            if not isinstance(other, (self.__class__, int)):
                return NotImplemented
            return self.__value <= int(other)

        def __gt__(self, other):
            return not self.__le__(other)

        def __lt__(self, other):
            return not self.__lt__(other)

        __ror__ = __or__
        __rand__ = __and__
        __rxor__ = __xor__
        __rlshift__ = __lshift__
        __rrshift__ = __rshift__

    def FixupEnumerators(E):
        for e in dir(E):
            try:
                attr = getattr(E, e)
                val = int(attr)
            except:
                attr = None
            if (attr != None):
                setattr(E, e, E(val, str(e)))

    class TpmMarshaller(object):
        'Base interface for all marshalable non-trivial TPM data types'
        __metaclass__ = abc.ABCMeta

        @abc.abstractmethod
        def toTpm(self, buf):
            """ Convert this object to its TPM representation and store in the output byte buffer object
            Args:
                buf (TpmBuffer): Output byte buffer for the marshaled representation of this object
            """
            pass

        @abc.abstractmethod
        def initFromTpm(self, buf):
            """ Populate this object from the TPM representation in the input byte buffer object
            Args:
                buf (TpmBuffer): Input byte buffer with the marshaled representation of the object
            """
            pass
    # interface TpmMarshaller
# end of Python version specific code


class SizedStructInfo:
    def __init__(self, startPos, size):
        self.startPos = startPos
        self.size = size
# SizedStructInfo


class TpmStructure(TpmMarshaller):
    """ Base class for TPM data structures"""

    def toTpm(self, buf):
        """ TpmMarshaller method """
        pass

    def initFromTpm(self, buf):
        """ TpmMarshaller method """
        pass

    def toBytes(self):
        buf = TpmBuffer()
        self.toTpm(buf)
        return buf.trim()

    def asTpm2B(self):
        buf = TpmBuffer()
        buf.writeSizedObj(self)
        return buf.trim()
# class TpmStructure

class SessEncInfo:
    """
    Parameters of the field, to which session based encryption can be applied (i.e.
    the first non-handle field marshaled in size-prefixed form)
    """
    def __init__(self, sizeLen = 0, valLen = 0):
        """ Constructor
        Args:
            sizeLen: Length of the size prefix in bytes. The size prefix contains the number
                     of elements in the sized filed (normally just bytes).
            valLen: Length of an element of the sized area in bytes (in most cases 1) */
        """
        self.sizeLen = sizeLen
        self.valLen = valLen
# class SessEncInfo

class CmdStructure(TpmStructure):
    """ Base class for custom (not TPM 2.0 spec defined) auto-generated data structures
        representing a TPM command or response parameters and handles, if any.

        They differ from the spec-defined data structures inheriting directly from the
        TpmStructure calss in that their handle fields are not marshaled by their toTpm()
        and initFrom() methods, but rather are acceesed and manipulated via an interface
        defined by this structs and its derivatives ReqStructure and RespStructure.
    """

    def numHandles(self):
        return 0

    def sessEncInfo(self):
        """ Returns non-zero parameters of the encryptable command/response parameter if session
            based encryption can be applied to this object (i.e. its first  non-handle field is
            marshaled in size-prefixed form). Otherwise returns zero initialized struct.
        """
        return SessEncInfo()
# class CmdStructure


class ReqStructure(CmdStructure):
    """ Base class for custom (not TPM 2.0 spec defined) auto-generated data structures
        representing a TPM command parameters and handles, if any.
    """

    def getHandles(self):
        """ Returns an array of this structure handle field values (TPM_HANDLE[]) """
        return None

    def numAuthHandles(self):
        """ Returns an array of handles (TPM_HANDLE[]) contained in this data struct """
        return 0

    def TypeName (self):
        """ ISerializable method """
        return "ReqStructure"
# class ReqStructure


class RespStructure(CmdStructure):
    """ Base class for custom (not TPM 2.0 spec defined) auto-generated data structures
        representing a TPM response parameters and handles, if any.
    """

    def getHandle(self):
        """ Returns this structure's handle field value (TPM_HANDLE) """
        return None

    def setHandle(self, h):
        """ Sets this structure's handle field (TPM_HANDLE) if it is present """
        pass

    def TypeName (self):
        """ ISerializable method """
        return "ReqStructure"




def bytesFromList(l):
    if NewPython:
        return bytes(l)
    else:
        return ''.join([chr(v) for v in l])

def intToTpm(val, valLen):
    v = int(val)
    if NewPython:
        return v.to_bytes(valLen, 'big')
    else:
        l = []
        if valLen == 8:
            l.append((v & 0xFF00000000000000) >> 56)
            l.append((v & 0x00FF000000000000) >> 48)
            l.append((v & 0x0000FF0000000000) >> 40)
            l.append((v & 0x000000FF00000000) >> 32)
            v = v & 0x00000000FFFFFFFF
        if valLen >= 4:
            l.append((v & 0xFF000000) >> 24)
            l.append((v & 0x00FF0000) >> 16)
        if valLen >= 2:
            l.append((v & 0x0000FF00) >> 8)
        l.append(v & 0x000000FF)
        return bytesFromList(l)

def intFromTpm(buf, pos, valLen):
    if NewPython:
        return int.from_bytes(buf[pos : pos + valLen], 'big')
    else:
        if isinstance(buf, bytearray):
            buf = bytes(buf)
        res = 0
        if (valLen == 8):
            res += ord(buf[pos]) << 56
            res += ord(buf[pos + 1]) << 48
            res += ord(buf[pos + 2]) << 40
            res += ord(buf[pos + 3]) << 32
            pos += 4
        if (valLen >= 4):
            res += ord(buf[pos]) << 24
            res += ord(buf[pos + 1]) << 16
            pos += 2
        if (valLen >= 2):
            res += ord(buf[pos]) << 8
            pos += 1
        res += ord(buf[pos])
        return res


class TpmBuffer:
    def __init__(self, lengthOrSrcBuf = 4096):
        if isinstance(lengthOrSrcBuf, TpmBuffer):
            self.__buf = bytearray(lengthOrSrcBuf.buf)
        elif isinstance(lengthOrSrcBuf, bytearray):
            self.__buf = lengthOrSrcBuf
        else:
            self.__buf = bytearray(lengthOrSrcBuf)
        self.init()

    def init(self):
        self.curPos = 0
        self.__length = len(self.buffer)
        self.__sizedStructSizes = []
        self.__outOfBounds = False

    @property
    def buffer(self):
        return self.__buf

    @property
    def size(self):
        """ Returns size of the backing byte buffer.
            Note that during marshaling this size normally exceeds the amount of actually
            stored data until trim() is invoked. 
        """
        return len(self.__buf)

    @property
    def curPos(self):
        return self.__pos

    @curPos.setter
    def curPos(self, newPos):
        self.__pos = newPos
        self.__outOfBounds = self.size < newPos

    def isOk(self):
        return not self.__outOfBounds

    def trim(self):
        """ Shrinks the backing byte buffer so that it ends at the current position
        Returns:
            Reference to the (shrunk) backing byte buffer
        """
        self.__buf = self.__buf[0 : self.curPos]
        return self.__buf

    def getCurStuctRemainingSize(self):
        ssi = self.__sizedStructSizes[len(self.__sizedStructSizes) - 1]
        return ssi.size - (self.curPos - ssi.startPos)

    def __checkLen(self, need, expand = False):
        if (len(self.__buf) < self.curPos + need):
            if (expand):
                self.buffer[self.size : self.curPos + need] = bytearray(self.curPos + need - self.size)
            else:
                self.__outOfBounds = True
                self.curPos = self.size
                return False
        return True

    def writeNum(self, val, len):
        """ Writes the given integer of the given size to this buffer in the TPM wire format
        Args:
            val: Numerical value to marshal
            len: Size of the numerical value in bytes
        """
        self.__checkLen(len, True)
        self.buffer[self.curPos : self.curPos + len] = intToTpm(val, len)
        self.curPos += len

    def readNum(self, valLen):
        """ Returns an integer value of the given size read from this buffer.
        Args:
            len: Size of the value in bytes
        """
        if (not self.__checkLen(valLen)):
            return 0
        res = intFromTpm(self.buffer, self.curPos, valLen)
        self.curPos += valLen
        return res

    def writeNumAtPos(self, val, pos, len = 4):
        curPos = self.curPos
        self.curPos = pos
        self.writeNum(val, len)
        self.curPos = curPos

    def writeByte(self, val):
        """ Writes the given 8-bit integer to this buffer.
        Args:
            val: 8-bit integer value to marshal
        """
        if self.__checkLen(1):
            self.buffer[self.curPos] = val & 0x00FF
            self.curPos += 1

    def writeShort(self, val):
        """ Marshals the given 16-bit integer to this buffer.
        Args:
            val: 16-bit integer value to marshal
        """
        self.writeNum(val, 2)

    def writeInt(self, val):
        """ Marshals the given 32-bit integer to this buffer.
        Args:
            val: 32-bit integer value to marshal
        """
        self.writeNum(val, 4)

    def writeInt64(self, val):
        """ Marshals the given 64-bit integer to this buffer.
        Args:
            val: 64-bit integer value to marshal
        """
        self.writeNum(val, 8)

    def readByte(self):
        """  Returns the byte read from this buffer. """
        if self.__checkLen(1):
            self.curPos += 1
            return self.buffer[self.curPos - 1]

    def readShort(self):
        """ Returns 16-bit integer unmarshaled from this buffer. """
        return self.readNum(2)

    def readInt(self):
        """ Returns 32-bit integer unmarshaled from this buffer. """
        return self.readNum(4)

    def readInt64(self):
        """ Returns 64-bit integer unmarshaled from this buffer. """
        return self.readNum(8)


    def writeByteBuf(self, byteBuf):
        """ Marshalls the given byte buffer with no size prefix.
        Args:
            data:  Byte buffer to marshal
        """
        bufLen = len(byteBuf)
        self.__checkLen(bufLen)
        newPos = self.curPos + bufLen
        self.buffer[self.curPos : newPos] = byteBuf[:]
        self.curPos = newPos

    def readByteBuf(self, size):
        """ Returns a byte buffer of the given size read from this buffer.
        Args:
            size: Number of bytes to read
        """
        if (not self.__checkLen(size)):
            return None
        startPos = self.curPos
        self.curPos += size
        return bytearray(self.buffer[startPos : self.curPos])

    def writeSizedByteBuf(self, data, sizeLen = 2):
        """ Marshals the given byte buffer with a length prefix.
        Args:
            data:  Byte buffer to marshal
            sizeLen:  Length of the size prefix in bytes
        """
        if (data == None or len(data) == 0):
            self.writeNum(0, sizeLen)
            return
        dataLen = len(data)
        self.__checkLen(dataLen + sizeLen, True)
        self.writeNum(dataLen, sizeLen)
        newPos = self.curPos + dataLen
        self.buffer[self.curPos : newPos] = data[:]
        self.curPos = newPos

    def readSizedByteBuf(self, sizeLen = 2):
        """ Returns a byte buffer unmarshaled from its size-prefixed representation.
        Args:
            sizeLen:  Length of the size prefix in bytes
        """
        len = self.readNum(sizeLen)
        begin = self.curPos
        self.curPos += len
        return self.__buf[begin : self.curPos]

    def createObj(self, Type):
        newObj = Type()
        newObj.initFromTpm(self)
        return newObj

    def writeSizedObj(self, obj):
        lenSize = 2  # Length of the object size is always 2 bytes
        if (obj == None):
            self.writeShort(0)  # Length of the object size is always 2 bytes
            return
        self.__checkLen(lenSize, True)

        # Remember position to marshal the size of the data structure
        sizePos = self.curPos
        # Account for the reserved size area
        self.curPos += lenSize
        obj.toTpm(self)
        # Marshal the data structure size
        objSize = self.curPos - (sizePos + lenSize)
        self.buffer[sizePos : sizePos + lenSize] = intToTpm(objSize, lenSize)

    def createSizedObj(self, Type):
        size = self.readShort()  # Length of the object size is always 2 bytes
        if (size == 0):
            return None

        self.__sizedStructSizes.append(SizedStructInfo(self.curPos, size))
        newObj = self.createObj(Type)
        self.__sizedStructSizes.pop()
        return newObj

    def writeObjArr(self, arr):
        if (arr == None):
            self.writeInt(0)
            return

        self.writeInt(len(arr))
        for elt in arr:
            if (not self.isOk()):
                break
            elt.toTpm(self)

    def readObjArr(self, EltType):
        len = self.readInt()
        newArr = []
        for i in range(len):
            if (not self.isOk()):
                break
            newArr.append(self.createObj(EltType))
        return newArr

    def writeValArr(self, arr, eltSize):
        if (arr == None):
            self.writeInt(0)  # Array size is always a 32-bit int
            return

        self.writeInt(len(arr))  # Array size is always a 32-bit int
        for val in arr:
            if (not self.isOk()):
                break
            self.writeNum(val, eltSize)

    def readValArr(self, eltSize):
        len = self.readInt()  # Array size is always a 32-bit int
        if (len == 0):
            return []

        newArr = []
        for i in range(len):
            if (not self.isOk()):
                break
            newArr.append(self.readNum(eltSize))
        return newArr
# class TpmBuffer



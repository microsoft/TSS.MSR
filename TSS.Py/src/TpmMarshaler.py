from .Helpers import *
import abc

if (NewPython):
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
        """ Returns reference to the backing byte buffer """
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
        """ Returns current read/write position in the the backing byte buffer """
        return self.__pos

    @curPos.setter
    def curPos(self, newPos):
        """ Sets current read/write position in the the backing byte buffer """
        self.__pos = newPos
        self.__outOfBounds = self.size < newPos

    def isOk(self):
        """ Returns True unless a previous read/write operation caused
        under/overflow correspondingly
        """
        return not self.__outOfBounds

    def trim(self):
        """ Shrinks the backing byte buffer so that it ends at the current position
        Returns:
            Reference to the shrunk backing byte buffer
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


    def writeByteBuf(self, data):
        """ Marshalls the given byte buffer with no size prefix.
        Args:
            data:  Byte buffer to marshal
        """
        dataSize = len(data) if data else 0
        if not dataSize or not self.__checkLen(dataSize):
            return
        newPos = self.curPos + dataSize
        self.buffer[self.curPos : newPos] = data[:]
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
        dataSize = len(data) if data else 0
        self.writeNum(dataSize, sizeLen)
        self.writeByteBuf(data)

    def readSizedByteBuf(self, sizeLen = 2):
        """ Returns a byte buffer unmarshaled from its size-prefixed representation.
        Args:
            sizeLen:  Length of the size prefix in bytes
        """
        return self.readByteBuf(self.readNum(sizeLen))

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
        # Calc marshaled object len
        objSize = self.curPos - (sizePos + lenSize)
        # Marshal the data structure size
        self.writeNumAtPos(objSize, sizePos, lenSize)
        #self.buffer[sizePos : sizePos + lenSize] = intToTpm(objSize, lenSize)

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



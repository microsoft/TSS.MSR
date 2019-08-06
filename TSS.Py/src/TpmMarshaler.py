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

class SizedStructInfo:
    def __init__(self, startPos, size):
        self.startPos = startPos
        self.size = size
# SizedStructInfo

class TpmStructure(TpmMarshaller):
    """Abstract base class for TPM data structures"""

    def init(self):
        pass

    # TpmMarshaller method
    @abc.abstractmethod
    def toTpm(self, buf):
        pass

    # TpmMarshaller method
    @abc.abstractmethod
    def fromTpm(self, buf):
        pass

    def asTpm2B(self):
        buf = TpmBuffer();
        buf.sizedToTpm(self, 2);
        return buf.slice(0, buf.curPos);

    def asTpm(self):
        buf = TpmBuffer();
        self.toTpm(buf);
        return buf.slice(0, buf.curPos);

    def toTpm2B(self, buf):
        return buf.toTpm2B(self.asTpm());
# class TpmStructure


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
            v = v & 0x00000000FFFFFFFF;
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
    def __init__(self, lengthOrSrcBuf = 0):
        if (isinstance(lengthOrSrcBuf, TpmBuffer)):
            self.__buffer = bytearray(lengthOrSrcBuf.buf)
        elif (isinstance(lengthOrSrcBuf, bytearray)):
            self.__buffer = lengthOrSrcBuf
        else:
            self.__buffer = bytearray(lengthOrSrcBuf)
        self.init()

    def init(self):
        self.curPos = 0
        self.__length = len(self.buffer)
        self.__sizedStructSizes = []
        self.__outOfBounds = False

    @property
    def buffer(self):
        return self.__buffer;

    @property
    def length(self):
       return len(self.buffer);

    @property
    def curPos(self):
        return self.__curPos;
    
    @curPos.setter
    def curPos(self, newPos):
        self.__curPos = newPos;
        self.__outOfBounds = self.length < newPos;

    def isOk(self):
        return not self.__outOfBounds;

    def slice(self, startPos, endPos):
        return self.buffer[startPos : endPos];

    def copy(self, target, targetStart):
        newPos = targetStart + self.length;
        target.buffer[targetStart : newPos] = self.buffer[:];
        target.curPos = newPos;

    def getCurStuctRemainingSize(self):
        ssi = self.__sizedStructSizes[len(self.__sizedStructSizes) - 1];
        return ssi.size - (self.curPos - ssi.startPos);

    def __checkLen(self, len, expand = False):
        if (self.length < self.curPos + len):
            if (expand):
                self.buffer[self.length : self.curPos + len] = bytearray(self.curPos + len - self.length);
            else:
                self.__outOfBounds = True;
                self.curPos = self.length;
                return False;
        return True

    def toTpm(self, val, len):
        """Converts the given numerical value of the given size to the TPM wire format.
        Args:
            val  Numerical value to marshal
            len  Size of the numerical value in bytes
        """
        self.__checkLen(len, True)
        self.buffer[self.curPos : self.curPos + len] = intToTpm(val, len)
        self.curPos += len

        """
        val = int(val)
        if (len == 8):
            self.buffer[self.curPos] = (val >> 56) & 0x00000000000000FF;
            self.buffer[self.curPos + 1] = (val >> 48) & 0x00000000000000FF;
            self.buffer[self.curPos + 2] = (val >> 40) & 0x00000000000000FF;
            self.buffer[self.curPos + 3] = (val >> 32) & 0x00000000000000FF;
            val = val & 0x00000000FFFFFFFF;
            self.curPos += 4;
        if (len >= 4):
            self.buffer[self.curPos] = (val >> 24) & 0x000000FF;
            self.buffer[self.curPos + 1] = (val >> 16) & 0x000000FF;
            self.curPos += 2;
        if (len >= 2):
            self.buffer[self.curPos] = (val >> 8) & 0x000000FF;
            self.curPos += 1;
        self.buffer[self.curPos] = val & 0x000000FF;
        self.curPos += 1;
        """

    def fromTpm(self, valLen):
        """Reads a numerical value of the given size from the input buffer containg data in the TPM wire format.
        Args:
            len  Size of the numerical value in bytes
        Returns:
            Extracted numerical value
        """
        if (not self.__checkLen(valLen)):
            return 0;
        res = intFromTpm(self.buffer, self.curPos, valLen);
        self.curPos += valLen;
        """
        if (len == 8):
            res += self.buffer[self.curPos] << 56;
            res += self.buffer[self.curPos + 1] << 48;
            res += self.buffer[self.curPos + 2] << 40;
            res += self.buffer[self.curPos + 3] << 32;
            self.curPos += 4;
        if (len >= 4):
            res += self.buffer[self.curPos] << 24;
            res += self.buffer[self.curPos + 1] << 16;
            self.curPos += 2;
        if (len >= 2):
            res += self.buffer[self.curPos] << 8;
            self.curPos += 1;
        res += self.buffer[self.curPos];
        self.curPos += 1;
        """
        return res;

    def toTpm2B(self, data, sizeLen = 2):
        """Writes the given byte array to the output buffer as a TPM2B structure in the TPM wire format.
        Args:
            val  Byte array to marshal
            sizeLen  Length of the byte array size in bytes
        """
        if (data == None or len(data) == 0):
            self.toTpm(0, sizeLen);
            return
        dataLen = len(data);
        self.__checkLen(dataLen + sizeLen, True)
        self.toTpm(dataLen, sizeLen);
        newPos = self.curPos + dataLen;
        self.buffer[self.curPos : newPos] = data[:];
        self.curPos = newPos;

    def fromTpm2B(self, sizeLen = 2):
        len = self.fromTpm(sizeLen);
        begin = self.curPos;
        self.curPos += len;
        return self.slice(begin, self.curPos);

    def createFromTpm(self, Type):
        newObj = Type();
        newObj.fromTpm(self);
        return newObj;

    def sizedToTpm(self, obj, lenSize):
        if (obj == None):
            self.toTpm(0, lenSize);
            return;
        self.__checkLen(lenSize, True);

        # Remember position to marshal the size of the data structure
        sizePos = self.curPos;
        # Account for the reserved size area
        self.curPos += lenSize;
        obj.toTpm(self);
        # Marshal the data structure size
        objSize = self.curPos - (sizePos + lenSize);
        self.buffer[sizePos : sizePos + lenSize] = intToTpm(objSize, lenSize);

    def sizedFromTpm(self, Type, lenSize):
        size = self.fromTpm(lenSize);
        if (size == 0):
            return None;

        self.__sizedStructSizes.append(SizedStructInfo(self.curPos, size));
        newObj = self.createFromTpm(Type);
        self.__sizedStructSizes.pop();
        return newObj;

    def bufferToTpm(self, byteBuf):
        bufLen = len(byteBuf);
        self.__checkLen(bufLen);
        newPos = self.curPos + bufLen;
        self.buffer[self.curPos : newPos] = byteBuf[:];
        self.curPos = newPos;
    
    def bufferFromTpm(self, size):
        if (not self.__checkLen(size)):
            return None;
        startPos = self.curPos;
        self.curPos += size;
        return bytearray(self.buffer[startPos : self.curPos]);
    
    def arrayToTpm(self, arr, lenSize):
        if (arr == None):
            self.toTpm(0, lenSize);
            return;

        self.toTpm(len(arr), lenSize);
        for elt in arr:
            if (not self.isOk()):
                break;
            elt.toTpm(self);

    def arrayFromTpm(self, EltType, lenSize):
        len = self.fromTpm(lenSize);
        newArr = [];
        for i in range(len):
            if (not self.isOk()):
                break;
            newArr.append(self.createFromTpm(EltType));
        return newArr;

    def valArrToTpm(self, arr, eltSize, lenSize):
        if (arr == None):
            self.toTpm(0, lenSize);
            return;

        self.toTpm(len(arr), lenSize);
        for val in arr:
            if (not self.isOk()):
                break;
            self.toTpm(val, eltSize);

    def valArrFromTpm(self, eltSize, lenSize):
        len = self.fromTpm(lenSize);
        if (len == 0):
            return [];

        newArr = [];
        for i in range(len):
            if (not self.isOk()):
                break;
            newArr.append(self.fromTpm(eltSize));
        return newArr;
# class TpmBuffer



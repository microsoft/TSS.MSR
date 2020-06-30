from .TpmMarshaler import TpmBuffer, TpmMarshaller, SizedStructInfo
import sys


class TpmStructure(TpmMarshaller):
    """ Base class for data structures auto-generated from the TPM 2.0 spec docs """

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

    def initFromBytes(self, buffer):
        """ Initializes this object from a TPM binary representation in
            the given byte buffer
        """
        self.initFromTpm(TpmBuffer(buffer))

    def asTpm2B(self):
        buf = TpmBuffer()
        buf.writeSizedObj(self)
        return buf.trim()

    def typeName (self):
        """ ISerializable method """
        return "TpmStructure"
# class TpmStructure

class SessEncInfo:
    """ Parameters of the field, to which session based encryption can be applied
        (i.e. the first non-handle field marshaled in size-prefixed form)
    """

    def __init__(self, sizeLen = 0, valLen = 0):
        """ Constructor
        Args:
            sizeLen: Length of the size prefix in bytes. The size prefix contains
                     the number of elements in the sized filed (normally just bytes).
            valLen: Length of an element of the sized area in bytes (in most cases 1)
        """
        self.sizeLen = sizeLen
        self.valLen = valLen
# class SessEncInfo

class CmdStructure(TpmStructure):
    """ Base class for custom (not TPM 2.0 spec defined) auto-generated classes
        representing a TPM command or response parameters and handles, if any.

        These data structures differ from the spec-defined ones derived directly
        from the TpmStructure class in that their handle fields are not marshaled
        by their toTpm() and initFrom() methods, but rather are acceesed and
        manipulated via an interface defined by this structs and its derivatives
        ReqStructure and RespStructure.
    """

    def numHandles(self):
        """ Returns number of TPM handles contained (as fields) in this data structure """
        return 0

    def sessEncInfo(self):
        """ Returns non-zero size info of the encryptable command/response parameter
            if session based encryption can be applied to this object (i.e. its first
            non-handle field is marshaled in size-prefixed form). Otherwise returns
            zero initialized struct.
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
        """ Returns number of authorization TPM handles contained in this data structure """
        return 0

    def typeName (self):
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

    def typeName (self):
        """ ISerializable method """
        return "ReqStructure"
# class RespStructure

from .Helpers import *
import abc

if (NewPython):
    from enum import IntFlag

    class TpmEnum(IntFlag):
        pass

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

# end of Python version specific code

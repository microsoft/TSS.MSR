import sys

NewPython = sys.version_info[0] > 3 or (sys.version_info[0] == 3 and sys.version_info[1] > 3)

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


from .TpmTypes import TPM_ALG_ID
import os
import hashlib
import hmac

class Crypto:
    @staticmethod
    def digestSize(alg):
        if alg == TPM_ALG_ID.SHA1: return 20
        if alg == TPM_ALG_ID.SHA256: return 32
        if alg == TPM_ALG_ID.SHA384: return 48
        if alg == TPM_ALG_ID.SHA512: return 64
        return 0

    @staticmethod
    def tpmAlgToPy(alg):
        if alg == TPM_ALG_ID.SHA1: return hashlib.sha1
        if alg == TPM_ALG_ID.SHA256: return hashlib.sha256
        if alg == TPM_ALG_ID.SHA384: return hashlib.sha384
        if alg == TPM_ALG_ID.SHA512: return hashlib.sha512
        return None

    @staticmethod
    def hash(alg, data):
        hash = Crypto.tpmAlgToPy(alg)(data)
        #hash.update(data)
        return hash.digest()

    @staticmethod
    def hmac(alg, key, data):
        hm = hmac.new(key, data, Crypto.tpmAlgToPy(alg))
        #hm.update(data)
        return hm.digest()

    @staticmethod
    def randomBytes(numBytes):
        return os.urandom(numBytes)

# class Crypto

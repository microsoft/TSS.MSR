from .TpmTypes import TPM_ALG_ID
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
        if alg == TPM_ALG_ID.SHA1: return 'sha1'
        if alg == TPM_ALG_ID.SHA256: return 'sha256'
        if alg == TPM_ALG_ID.SHA384: return 'sha384'
        if alg == TPM_ALG_ID.SHA512: return 'sha512'
        return None

    @staticmethod
    def hash(alg, data):
        hash = hashlib.new(Crypto.tpmAlgToPy(alg), data)
        #hash.update(data)
        return hash.digest()

    @staticmethod
    def hmac(alg, key, data):
        hm = hmac.createHmac(key, data, Crypto.tpmAlgToPy(alg))
        #hm.update(data)
        return hm.digest()
# class Crypto

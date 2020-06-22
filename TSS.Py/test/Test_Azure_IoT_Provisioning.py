""" 
 * Copyright(c) Microsoft Corporation.All rights reserved. 
 * Licensed under the MIT License. 
 * See the LICENSE file in the project root for full license information. 
"""

# This path tweaking is a workaround for VS Code failing to find the 'src' module
import sys
import os.path # os.pardir
cwd = os.getcwd()
sys.path.append(cwd[:cwd.find('TSS.Py') + len('TSS.Py')])
Py3 = sys.version_info > (3,)

from src.Tpm import *
#from src.TpmMarshaler import *
from src.Crypt import Crypto as crypto

TEST_MODE = True

if not NewPython:
    import binascii
    def bytesFromHex(strHex):
        return binascii.unhexlify(strHex)

def cleanSlots(tpm, slotType):
    caps = tpm.GetCapability(TPM_CAP.HANDLES, slotType << 24, 8)
    handles = caps.capabilityData

    if len(handles.handle) == 0:
        print("No dangling", slotType, "handles")
    else:
        for h in handles.handle:
            print("Dangling", slotType, "handle 0x" + hex(h.handle))
            if slotType == TPM_HT.PERSISTENT:
                tpm.allowErrors().EvictControl(TPM_HANDLE.OWNER, h, h)
                if tpm.lastResponseCode not in [TPM_RC.SUCCESS, TPM_RC.HIERARCHY]:
                    raise(tpm.lastError)
            else:
                tpm.FlushContext(h)
# cleanSlots()


Aes128SymDef = TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES, 128, TPM_ALG_ID.CFB)

EK_PersHandle = TPM_HANDLE(0x81010001)
SRK_PersHandle = TPM_HANDLE(0x81000001)
ID_KEY_PersHandle = TPM_HANDLE(0x81000100)

if NewPython:
    EkPolicy = bytes.fromhex('837197674484b3f81a90cc8d46a5d724fd52d76e06520b64f2a1da1b331469aa')
else:
    EkPolicy = bytesFromHex('837197674484b3f81a90cc8d46a5d724fd52d76e06520b64f2a1da1b331469aa')

# Template of the Endorsement Key (EK)
EkTemplate = TPMT_PUBLIC(TPM_ALG_ID.SHA256,
        TPMA_OBJECT.restricted | TPMA_OBJECT.decrypt | TPMA_OBJECT.fixedTPM | TPMA_OBJECT.fixedParent
            | TPMA_OBJECT.adminWithPolicy | TPMA_OBJECT.sensitiveDataOrigin,
        EkPolicy,
        TPMS_RSA_PARMS(Aes128SymDef, TPMS_NULL_ASYM_SCHEME(), 2048, 0),
        TPM2B_PUBLIC_KEY_RSA())

SrkTemplate = TPMT_PUBLIC(TPM_ALG_ID.SHA256,
        TPMA_OBJECT.restricted | TPMA_OBJECT.decrypt | TPMA_OBJECT.fixedTPM | TPMA_OBJECT.fixedParent
            | TPMA_OBJECT.noDA | TPMA_OBJECT.userWithAuth | TPMA_OBJECT.sensitiveDataOrigin,
        None,
        TPMS_RSA_PARMS(Aes128SymDef, TPMS_NULL_ASYM_SCHEME(), 2048, 0),
        TPM2B_PUBLIC_KEY_RSA())


def createPersistentPrimary(tpm, hierarchy, templ, hPers):
    tpm.allowErrors().ReadPublic(hPers)
    #tpm.allowErrors().EvictControl(TPM_HANDLE.OWNER, hPers, hPers)
    if (tpm.lastResponseCode == TPM_RC.SUCCESS):
        print("Persistent key 0x" + hex(hPers.handle) + " already exists")
    else:
        resp = tpm.CreatePrimary(TPM_HANDLE(hierarchy), TPMS_SENSITIVE_CREATE(), templ, None, None)
        print('CreatePrimary returned ' + str(tpm.lastResponseCode))
        if (not resp):
            raise(Exception("CreatePrimary failed for " + templ))
        h = resp.handle
        tpm.EvictControl(TPM_HANDLE.OWNER, h, hPers)
        tpm.FlushContext(h)
        return resp.outPublic


def drsVerifyIdSignature(tpm, data, sig):
    hmacOverData = Crypto.hmac(TPM_ALG_ID.SHA256, idKeySens.data, data)
    return sig == hmacOverData


def SignDeviceToken(idKeyHashAlg):
    # MaxInputBuffer = TpmHelpers.getTpmProperty(tpm, TPM_PT.INPUT_BUFFER)
    caps = tpm.GetCapability(TPM_CAP.TPM_PROPERTIES, TPM_PT.INPUT_BUFFER, 1)
    props = caps.capabilityData     # : TPML_TAGGED_TPM_PROPERTY
    if len(props.tpmProperty) != 1 or props.tpmProperty[0].property != TPM_PT.INPUT_BUFFER:
        raise(Exception('Unexpected result of TPM2_GetCapability(TPM_PT.INPUT_BUFFER)'))
    MaxInputBuffer = props.tpmProperty[0].value

    # First, the code for a short (<= MaxInputBuffer) token signing
    deviceIdToken = crypto.randomBytes(800)
    if len(deviceIdToken) > MaxInputBuffer:
        raise(Exception('Too long token to HMAC'))

    signature = tpm.HMAC(ID_KEY_PersHandle, deviceIdToken, idKeyHashAlg)
    print('HMAC() returned {0}; signature size {1}'.format(str(tpm.lastResponseCode), len(signature)))

    if TEST_MODE:
        # Verify the signature correctness
        sigOK = drsVerifyIdSignature(tpm, deviceIdToken, signature)
        print('Signature over short token: ' + ('OK' if sigOK else 'FAILED'))

        signature[16] ^= 0xCC
        sigOK = drsVerifyIdSignature(tpm, deviceIdToken, signature)
        print('Bad signature over short token: ' + ('OK' if sigOK else 'FAILED'))

    # Now the code for long token (> MaxInputBuffer) signing
    deviceIdToken = crypto.randomBytes(5500)

    hSequence = tpm.HMAC_Start(ID_KEY_PersHandle, None, idKeyHashAlg)
    print('HMAC_Start() returned ' + str(tpm.lastResponseCode))

    curPos = 0
    bytesLeft = len(deviceIdToken)
    while bytesLeft > MaxInputBuffer:
        tpm.SequenceUpdate(hSequence, deviceIdToken[curPos : curPos + MaxInputBuffer])
        print('SequenceUpdate() returned {0} for slice [{1}, {2}]'.format(str(tpm.lastResponseCode), \
                                                                          curPos, curPos + MaxInputBuffer))
        bytesLeft -= MaxInputBuffer
        curPos += MaxInputBuffer

    resp = tpm.SequenceComplete(hSequence, deviceIdToken[-bytesLeft:], TPM_HANDLE(TPM_RH.NULL))
    print('SequenceComplete() returned {0}. Signature size {1}'.format(str(tpm.lastResponseCode), len(signature)))

    if TEST_MODE:
        sigOK = drsVerifyIdSignature(tpm, deviceIdToken, resp.result)
        print('Signature over long token: ' + ('OK' if sigOK else 'FAILED'))
# SignDeviceToken()



class DrsActivationBlob:
    def __init__(self,
        credBlobOrRawActBlob = None, # TPMS_ID_OBJECT
        encSecret = None, #TPM2B_ENCRYPTED_SECRET
        idKeyDupBlob = None, # TPM2B_PRIVATE
        encWrapKey = None, # TPM2B_ENCRYPTED_SECRET
        idKeyPub = None, # TPMT_PUBLIC
        encUriData = None # TPM2B_DATA
        ):
            if credBlobOrRawActBlob and not encSecret:
                assert not idKeyDupBlob and not encWrapKey and not idKeyPub and not encUriData
                self.fromTpm(credBlobOrRawActBlob)
                return
            self.credBlob = credBlobOrRawActBlob
            self.encSecret = encSecret
            self.idKeyDupBlob = idKeyDupBlob
            self.encWrapKey = encWrapKey
            self.idKeyPub = idKeyPub
            self.encUriData = encUriData

    def fromTpm(self, actBlob):
        if isinstance(actBlob, bytes) or isinstance(actBlob, bytearray):
            buf = TpmBuffer(actBlob)
        else:
            assert isinstance(actBlob, TpmBuffer)
            buf = actBlob
        self.credBlob = buf.createSizedObj(TPMS_ID_OBJECT)
        print('credBlob end: {0}'.format(buf.curPos))
        self.encSecret = buf.createObj(TPM2B_ENCRYPTED_SECRET)
        print("encSecret end: {0}; size = {1}".format(buf.curPos, len(self.encSecret.secret)))
        self.idKeyDupBlob = buf.createObj(TPM2B_PRIVATE)
        print("idKeyDupBlob end: {0}; size = {1}".format(buf.curPos, len(self.idKeyDupBlob.buffer)))
        self.encWrapKey = buf.createObj(TPM2B_ENCRYPTED_SECRET)
        print("encWrapKey end: {0}; size = {1}".format(buf.curPos, len(self.encWrapKey.secret)))
        self.idKeyPub = buf.createSizedObj(TPMT_PUBLIC)
        print("idKeyPub end: {0}".format(buf.curPos))
        self.encUriData = buf.createObj(TPM2B_DATA)
        print("encUriData end: {0}".format(buf.curPos))
        if (not buf.isOk()):
            raise(Exception("Failed to unmarshal Activation Blob"))
        assert buf.curPos == buf.size, "Activation Blob sent by DRS has contains gratuitous data"
# class DrsActivationBlob


def doActivation(rawActBlob):
    print('Raw Activation Blob size: {0}'.format(len(rawActBlob)))

    # Unmarshal components of the activation blob received from the DRS
    actBlob = DrsActivationBlob(rawActBlob)

    # Start a policy session to be used with ActivateCredential()
    nonceCaller = crypto.randomBytes(20)
    resp = tpm.StartAuthSession(None, None, nonceCaller, None, TPM_SE.POLICY, NullSymDef, TPM_ALG_ID.SHA256)
    print('StartAuthSession(POLICY_SESS) returned ' + str(tpm.lastResponseCode) + '; sess handle: ' + str(resp.handle))
    policySess = Session(resp.handle, resp.nonceTPM)
    
    # Apply the policy necessary to authorize an EK on Windows
    resp = tpm.PolicySecret(Endorsement, policySess.SessIn.sessionHandle, None, None, None, 0)
    print('PolicySecret() returned ' + str(tpm.lastResponseCode))

    # Use ActivateCredential() to decrypt symmetric key that is used as an inner protector
    # of the duplication blob of the new Device ID key generated by DRS.
    innerWrapKey = tpm.withSessions(None, policySess)   \
                      .ActivateCredential(SRK_PersHandle, EK_PersHandle, actBlob.credBlob, actBlob.encSecret.secret)
    print('ActivateCredential() returned {0}; innerWrapKey size {1}'.format(str(tpm.lastResponseCode), len(innerWrapKey)))

    # Initialize parameters of the symmetric key used by DRS 
    # Note that the client uses the key size chosen by DRS, but other parameters are fixes (an AES key in CFB mode).
    symDef = TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES, len(innerWrapKey) * 8, TPM_ALG_ID.CFB)
            
    # Import the new Device ID key issued by DRS to the device's TPM
    idKeyPriv = tpm.Import(SRK_PersHandle, innerWrapKey, actBlob.idKeyPub, actBlob.idKeyDupBlob, actBlob.encWrapKey.secret, symDef)
    print('Import() returned {0}; idKeyPriv size {1}'.format(str(tpm.lastResponseCode), len(idKeyPriv.buffer)))

    # Load the imported key into the TPM
    hIdKey = tpm.Load(SRK_PersHandle, idKeyPriv, actBlob.idKeyPub)
    print('Load() returned {0}; ID key handle: {1}'.format(str(tpm.lastResponseCode), str(hIdKey.handle)))

    # Remove possibly existing persistent instance of the previous Device ID key
    tpm.allowErrors().EvictControl(Owner, ID_KEY_PersHandle, ID_KEY_PersHandle)

    # Persist the new Device ID key
    tpm.EvictControl(Owner, hIdKey, ID_KEY_PersHandle)
    print('EvictControl({0}, {1}) returned {2}'.format(str(hIdKey.handle), str(ID_KEY_PersHandle.handle), str(tpm.lastResponseCode)))

    # Free the ID Key transient handle
    tpm.FlushContext(hIdKey)
    print('FlushContext(TRANS_ID_KEY) returned ' + str(tpm.lastResponseCode))

    # Free the session object
    tpm.FlushContext(policySess.SessIn.sessionHandle)
    print('FlushContext(POLICY_SESS) returned ' + str(tpm.lastResponseCode))


    #
    # Decrypt the secret URI data sent by the DRS
    #
    """
    symAlg = 'AES-' + str(len(innerWrapKey) * 8) + '-CFB'
    iv = buffer(16)
    iv.fill(0)

    dec: crypto.Decipher = crypto.createDecipheriv(symAlg, innerWrapKey, iv)
    decUriData: Buffer = dec.update(actBlob.encUriData.buffer)
    print('Decipher.update returned ' + len(decUriData) + ' bytes: ' + decUriData)
    decFinal: Buffer = dec.final()
    print('Decipher.final returned ' + len(decFinal) + ' bytes: ' + decFinal)
    """

    #
    # Example of signing a device token using the new Device ID key
    #
    idKeyHashAlg = actBlob.idKeyPub.parameters.scheme.hashAlg   # ((TPMS_SCHEME_HMAC)((TPMS_KEYEDHASH_PARMS)actBlob.idKeyPub.parameters).scheme).hashAlg
    SignDeviceToken(idKeyHashAlg)

# doActivation()


keyBytes = crypto.randomBytes(32)
idKeySens = TPMS_SENSITIVE_CREATE(None, keyBytes)
idKeyTemplate = TPMT_PUBLIC(TPM_ALG_ID.SHA256,
            TPMA_OBJECT.sign | TPMA_OBJECT.userWithAuth | TPMA_OBJECT.noDA,
            None,   # Will be filled by getActivationBlob
            TPMS_KEYEDHASH_PARMS(TPMS_SCHEME_HMAC(TPM_ALG_ID.SHA256)),
            TPM2B_DIGEST())


def drsGetActivationBlob(tpm, ekPubBlob, srkPubBlob):
    ekPub = TpmBuffer(ekPubBlob).createObj(TPM2B_PUBLIC).publicArea
    srkPub = TpmBuffer(srkPubBlob).createObj(TPM2B_PUBLIC).publicArea

    # Start a policy session to be used with ActivateCredential()
    nonceCaller = crypto.randomBytes(20)
    respSas = tpm.StartAuthSession(None, None, nonceCaller, None, TPM_SE.POLICY, NullSymDef, TPM_ALG_ID.SHA256)
    hSess = respSas.handle
    print('DRS >> StartAuthSession(POLICY_SESS) returned ' + str(tpm.lastResponseCode) + '; sess handle: ' + str(hSess.handle))
    sess = Session(hSess, respSas.nonceTPM)

    # Run the policy command necessary for key duplication
    respPcc = tpm.PolicyCommandCode(hSess, TPM_CC.Duplicate)
    print('DRS >> PolicyCommandCode() returned ' + str(tpm.lastResponseCode))

    # Retrieve the policy digest computed by the TPM
    dupPolicyDigest = tpm.PolicyGetDigest(hSess)
    print('DRS >> PolicyGetDigest() returned ' + str(tpm.lastResponseCode))

    idKeyTemplate.authPolicy = dupPolicyDigest

    idKey = tpm.withSession(NullPwSession)  \
               .CreatePrimary(Owner, idKeySens, idKeyTemplate, None, [])

    print('DRS >> CreatePrimary(idKey) returned ' + str(tpm.lastResponseCode))
    
    hSrkPub = tpm.LoadExternal(None, srkPub, Owner)
    print('DRS >> LoadExternal(SRKpub) returned ' + str(tpm.lastResponseCode))

    symWrapperDef = TPMT_SYM_DEF_OBJECT(TPM_ALG_ID.AES, 128, TPM_ALG_ID.CFB)
    respDup = tpm.withSession(sess)   \
                 .Duplicate(idKey.handle, hSrkPub, None, symWrapperDef)
    print('DRS >> Duplicate(...) returned ' + str(tpm.lastResponseCode))
    
    tpm.FlushContext(hSrkPub)

    hEkPub = tpm.LoadExternal(None, ekPub, Endorsement)
    print('DRS >> LoadExternal(EKpub) returned ' + str(tpm.lastResponseCode))

    cred = tpm.MakeCredential(hEkPub, respDup.encryptionKeyOut, srkPub.getName())
    print('DRS >> MakeCredential(...) returned ' + str(tpm.lastResponseCode))

    # Delete the key and session handles
    tpm.FlushContext(hEkPub)
    tpm.FlushContext(idKey.handle)
    tpm.FlushContext(hSess)
    print('DRS >> Cleanup done')

    #
    # Encrypt URI data to be passed to the client device
    #
    symWrapperTemplate = TPMT_PUBLIC(TPM_ALG_ID.SHA256,
                TPMA_OBJECT.decrypt | TPMA_OBJECT.encrypt | TPMA_OBJECT.userWithAuth,
                None,
                TPMS_SYMCIPHER_PARMS(symWrapperDef),
                TPM2B_DIGEST())
    sens = TPMS_SENSITIVE_CREATE(None, respDup.encryptionKeyOut)
    symWrapperKey = tpm.withSession(NullPwSession)  \
                       .CreatePrimary(Owner, sens, symWrapperTemplate, None, [])
    print('DRS >> CreatePrimary(SymWrapperKey) returned ' + str(tpm.lastResponseCode))

    uriData = "http:#my.test.url/TestDeviceID=F4ED90771DAA7C0B3230FF675DF8A61104AE7C8BB0093FD6A".encode('utf8')
    if (Py3):
        iv = bytes(len(respDup.encryptionKeyOut))
    else:
        iv = bytes(b'\0'*len(respDup.encryptionKeyOut))
    respEnc = tpm.withSession(NullPwSession)    \
                 .EncryptDecrypt(symWrapperKey.handle, 0, TPM_ALG_ID.CFB, iv, uriData)
    print('DRS >> EncryptDecrypt() returned ' + str(tpm.lastResponseCode))
    encryptedUri = respEnc.outData

    # Delete the key and session handles
    tpm.FlushContext(symWrapperKey.handle)
    print('DRS >> Final cleanup done')

    #
    # Prepare data to send back to the DRS client
    #
    actBlob = TpmBuffer(4096)
        
    actBlob.writeSizedObj(cred.credentialBlob)
    actBlob.writeSizedByteBuf(cred.secret)
    respDup.duplicate.toTpm(actBlob)
    actBlob.writeSizedByteBuf(respDup.outSymSeed)
    actBlob.writeSizedObj(idKey.outPublic)
    actBlob.writeSizedByteBuf(encryptedUri)
    print('DRS >> Activation blob of {0} bytes generated'.format(actBlob.curPos))
        
    return actBlob.trim()

# drsGetActivationBlob()

tpm = Tpm(True)
tpm.connect()

rb = tpm.GetRandom(10)
print('GetRandom() returned', len(rb), 'bytes:', list(rb))

cleanSlots(tpm, TPM_HT.PERSISTENT)
cleanSlots(tpm, TPM_HT.TRANSIENT)
cleanSlots(tpm, TPM_HT.LOADED_SESSION)

ekPub = createPersistentPrimary(tpm, TPM_RH.ENDORSEMENT, EkTemplate, EK_PersHandle)
srkPub = createPersistentPrimary(tpm, TPM_RH.OWNER, SrkTemplate, SRK_PersHandle)

rawActBlob = drsGetActivationBlob(tpm, ekPub.asTpm2B(), srkPub.asTpm2B())
doActivation(rawActBlob)


# END OF SAMPLE
print('Python sample completed successfully!')


from src.Tpm import *

from src.TpmMarshaler import *


if not NewPython:
    import binascii
    def bytesFromHex(strHex):
        return binascii.unhexlify(strHex)

def cleanSlots(tpm, slotType):
    caps = tpm.GetCapability(TPM_CAP.HANDLES, slotType << 24, 8)
    handles = caps.capabilityData;

    if len(handles.handle) == 0:
        print("No dangling", slotType, "handles")
    else:
        for h in handles.handle:
            print("Dangling", slotType, "handle 0x" + hex(h.handle));
            tpm.FlushContext(h);

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
        print("Persistent key 0x" + hex(hPers.handle) + " already exists");
    else:
        resp = tpm.CreatePrimary(TPM_HANDLE(hierarchy), TPMS_SENSITIVE_CREATE(), templ, None, None)
        if (resp):
            h = resp.handle
            tpm.EvictControl(TPM_HANDLE.OWNER, h, hPers)
            tpm.FlushContext(h)
        else:
            print("CreatePrimary failed for " + templ);



tpm = Tpm(True)
tpm.connect()

rb = tpm.GetRandom(10)
print('GetRandom() returned', len(rb), 'bytes:', list(rb))

cleanSlots(tpm, TPM_HT.TRANSIENT)
cleanSlots(tpm, TPM_HT.LOADED_SESSION)

createPersistentPrimary(tpm, TPM_RH.ENDORSEMENT, EkTemplate, EK_PersHandle)
createPersistentPrimary(tpm, TPM_RH.OWNER, SrkTemplate, SRK_PersHandle)


class DrsActivationBlob:
    def __init__(this,
        credBlob = None, # TPMS_ID_OBJECT
        encSecret = None, #TPM2B_ENCRYPTED_SECRET
        idKeyDupBlob = None, # tss.TPM2B_PRIVATE
        encWrapKey = None, # tss.TPM2B_ENCRYPTED_SECRET
        idKeyPub = None, # TPMT_PUBLIC
        encUriData = None # tss.TPM2B_DATA
        ):
            this.credBlob = credBlob
            this.encSecret = encSecret
            this.idKeyDupBlob = idKeyDupBlob
            this.encWrapKey = encWrapKey
            this.idKeyPub = idKeyPub
            this.encUriData = encUriData

    def fromTpm(this, actBlob):
        buf = TpmBuffer(actBlob)
        this.credBlob = buf.sizedFromTpm(TPMS_ID_OBJECT, 2)
        print('credBlob end: ' + str(buf.curPos))
        this.encSecret = buf.createFromTpm(TPM2B_ENCRYPTED_SECRET);
        print("encSecret end: " + str(buf.curPos) + "; size = " + this.encSecret.secret.length);
        this.idKeyDupBlob = buf.createFromTpm(TPM2B_PRIVATE);
        print("idKeyDupBlob end: " + str(buf.curPos) + "; size = " + this.idKeyDupBlob.buffer.length);
        this.encWrapKey = buf.createFromTpm(TPM2B_ENCRYPTED_SECRET);
        print("encWrapKey end: " + str(buf.curPos) + "; size = " + this.encWrapKey.secret.length);
        this.idKeyPub = buf.sizedFromTpm(TPMT_PUBLIC, 2);
        print("idKeyPub end: " + str(buf.curPos));
        this.encUriData = buf.createFromTpm(TPM2B_DATA);
        print("encUriData end: " + str(buf.curPos));
        if (not buf.isOk()):
            raise(Exception("Failed to unmarshal Activation Blob"))
        if (buf.curPos != buf.length):
            print("WARNING: Activation Blob sent by DRS has contains extra unidentified data");
# class DrsActivationBlob



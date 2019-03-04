/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"
#include "Tpm2.h"
#include "MarshallInternal.h"

_TPMCPP_BEGIN

void TPM_HANDLE::SetName(const std::vector<BYTE>& _name)
{
    auto handleType = GetHandleType();

    if (handleType == TPM_HT::NV_INDEX ||
        handleType == TPM_HT::TRANSIENT || 
        handleType == TPM_HT::PERSISTENT ||
        handleType == TPM_HT::PERSISTENT) {
        Name = _name;
        return;
    } else {
        // For these types the name should be the handle
        auto nameShouldBe = GetName();

        if (_name != nameShouldBe) {
            throw runtime_error("Trying to set the name of an object where the name is the handle, and the name is incorrect");
        }
    }

    return;
}

std::vector<BYTE> TPM_HANDLE::GetName()
{
    auto handleType = (UINT32) GetHandleType();

    switch (handleType) {
        case 0:
        case 2:
        case 3:
        case 0x40:
            Name = ValueTypeToByteArray(handle);
            return Name;

        case 1:
        case 0x80:
        case 0x81:
            if (Name.size() == 0) {
                throw runtime_error("Name is not set for handle");
            }

            return Name;

        default:
            throw runtime_error("Unknown handle type");
    }
}

TPM_ALG_ID TPMT_PUBLIC::GetAlg()
{
    ToBuf();
    return this->type;
}

bool TPMT_PUBLIC::ValidateSignature(vector<BYTE> _dataThatWasSigned, TPMU_SIGNATURE& _sig)
{
    return CryptoServices::ValidateSignature(*this, _dataThatWasSigned, _sig);

}

TPM_ALG_ID GetSigningHashAlg(class TPMT_PUBLIC& pub)
{
    TPMS_RSA_PARMS *rsaParms = dynamic_cast<TPMS_RSA_PARMS *> (pub.parameters);

    if (rsaParms == NULL) {
        throw domain_error("Only RSA signature verificaion is supported");
    }

    TpmStructureBase *pp = rsaParms->scheme;
    auto schemeTypeId = pp->GetTypeId();

    if (schemeTypeId != TpmTypeId::TPMS_SCHEME_RSASSA_ID) {
        throw domain_error("only RSASSA is supported");
    }

    TPMS_SCHEME_RSASSA *scheme = dynamic_cast<TPMS_SCHEME_RSASSA *>(rsaParms->scheme);
    TPM_ALG_ID hashAlg = scheme->hashAlg;
    return hashAlg;
}

bool TPMT_PUBLIC::ValidateQuote(const class PCR_ReadResponse& expectedPcrVals,
                                std::vector<BYTE> Nonce, 
                                class QuoteResponse& quote)
{
    TPM_ALG_ID hashAlg = GetSigningHashAlg(*this);
    TPMS_ATTEST attest = quote.quoted;

    // Validate the quote
    if (attest.extraData != Nonce) {
        return false;
    }

    if (attest.magic != TPM_GENERATED::VALUE) {
        return false;
    }

    TPMS_QUOTE_INFO *quoteInfo = dynamic_cast<TPMS_QUOTE_INFO *> (attest.attested);
    if (quoteInfo == NULL) {
        return false;
    }

    if (quoteInfo->pcrSelect != expectedPcrVals.pcrSelectionOut) {
        return false;
    }

    // Calculate the PCR-value hash
    OutByteBuf pcrBuf;
    for (unsigned int j = 0; j < expectedPcrVals.pcrValues.size(); j++) {
        const TPM2B_DIGEST& pcrVal = expectedPcrVals.pcrValues[j];
        pcrBuf << pcrVal.buffer;
    }

    auto pcrHash = CryptoServices::Hash(hashAlg, pcrBuf.GetBuf());

    // Check that this was as quoted
    if (quoteInfo->pcrDigest != pcrHash) {
        return false;
    }

    // And finally check the signature
    ByteVec signedBlob = quote.quoted.ToBuf();
    auto signedBlobHash = CryptoServices::Hash(hashAlg, signedBlob);
    bool quoteOk = CryptoServices::ValidateSignature(*this, signedBlobHash, *(quote.signature));

    return quoteOk;
}

bool TPMT_PUBLIC::ValidateCertify(class TPMT_PUBLIC& keyThatWasCertified,
                                  ByteVec Nonce,
                                  class CertifyResponse& certResponse)
{
    TPM_ALG_ID hashAlg = GetSigningHashAlg(*this);
    TPMS_ATTEST attest = certResponse.certifyInfo;

    // Validate the quote
    if (attest.extraData != Nonce) {
        return false;
    }

    if (attest.magic != TPM_GENERATED::VALUE) {
        return false;
    }

    TPMS_CERTIFY_INFO *quoteInfo = dynamic_cast<TPMS_CERTIFY_INFO *> (attest.attested);
    if (quoteInfo == NULL) {
        return false;
    }

    if (quoteInfo->name != keyThatWasCertified.GetName()) {
        return false;
    }

    // TODO: Fully qualified name

    // And finally check the signature
    ByteVec signedBlob = certResponse.certifyInfo.ToBuf();
    auto signedBlobHash = CryptoServices::Hash(hashAlg, signedBlob);
    bool quoteOk = CryptoServices::ValidateSignature(*this,
                                                     signedBlobHash, 
                                                     *(certResponse.signature));
    return quoteOk;
}

bool TPMT_PUBLIC::ValidateCertifyCreation(ByteVec Nonce, 
                                          ByteVec creationHash,
                                          class CertifyCreationResponse& certResponse)
{
    TPM_ALG_ID hashAlg = GetSigningHashAlg(*this);
    TPMS_ATTEST attest = certResponse.certifyInfo;

    // Validate the quote
    if (attest.extraData != Nonce) {
        return false;
    }

    if (attest.magic != TPM_GENERATED::VALUE) {
        return false;
    }

    TPMS_CREATION_INFO *quoteInfo = dynamic_cast<TPMS_CREATION_INFO *> (attest.attested);
    if (quoteInfo == NULL) {
        return false;
    }

    if (quoteInfo->creationHash != creationHash) {
        return false;
    }

    // And finally check the signature
    ByteVec signedBlob = certResponse.certifyInfo.ToBuf();
    auto signedBlobHash = CryptoServices::Hash(hashAlg, signedBlob);
    bool quoteOk = CryptoServices::ValidateSignature(*this, 
                                                     signedBlobHash,
                                                     *(certResponse.signature));
    return quoteOk;
}

bool TPMT_PUBLIC::ValidateGetTime(std::vector<BYTE> Nonce, class GetTimeResponse& _timeQuote)
{
    TPM_ALG_ID hashAlg = GetSigningHashAlg(*this);
    TPMS_ATTEST attest = _timeQuote.timeInfo;

    // Validate the quote
    if (attest.extraData != Nonce) {
        return false;
    }

    if (attest.magic != TPM_GENERATED::VALUE) {
        return false;
    }

    // And finally check the signature
    ByteVec signedBlob = _timeQuote.timeInfo.ToBuf();
    auto signedBlobHash = CryptoServices::Hash(hashAlg, signedBlob);
    bool quoteOk = CryptoServices::ValidateSignature(*this, 
                                                     signedBlobHash, 
                                                     *(_timeQuote.signature));
    return quoteOk;
}

bool TPMT_PUBLIC::ValidateCommandAudit(TPMT_HA expectedHash,
                                       std::vector<BYTE> Nonce,
                                       class GetCommandAuditDigestResponse& quote)
{
    TPM_ALG_ID hashAlg = GetSigningHashAlg(*this);
    TPMS_ATTEST attest = quote.auditInfo;

    // Validate the quote
    if (attest.extraData != Nonce) {
        return false;
    }

    if (attest.magic != TPM_GENERATED::VALUE) {
        return false;
    }

    TPMS_COMMAND_AUDIT_INFO *sessionInfo = dynamic_cast<TPMS_COMMAND_AUDIT_INFO *> (attest.attested);
    if (sessionInfo->auditDigest != expectedHash.digest) {
        return false;
    }

    // And finally check the signature
    ByteVec signedBlob = quote.auditInfo.ToBuf();
    auto signedBlobHash = CryptoServices::Hash(hashAlg, signedBlob);
    bool quoteOk = CryptoServices::ValidateSignature(*this, signedBlobHash, *(quote.signature));

    return quoteOk;
}

bool TPMT_PUBLIC::ValidateSessionAudit(TPMT_HA expectedHash,
                                       std::vector<BYTE> Nonce,
                                       class GetSessionAuditDigestResponse& quote)
{
    TPM_ALG_ID hashAlg = GetSigningHashAlg(*this);
    TPMS_ATTEST attest = quote.auditInfo;

    // Validate the quote
    if (attest.extraData != Nonce) {
        return false;
    }

    if (attest.magic != TPM_GENERATED::VALUE) {
        return false;
    }

    TPMS_SESSION_AUDIT_INFO *sessionInfo = dynamic_cast<TPMS_SESSION_AUDIT_INFO *> (attest.attested);
    if (sessionInfo->sessionDigest != expectedHash.digest) {
        return false;
    }

    // And finally check the signature
    ByteVec signedBlob = quote.auditInfo.ToBuf();
    auto signedBlobHash = CryptoServices::Hash(hashAlg, signedBlob);
    bool quoteOk = CryptoServices::ValidateSignature(*this, signedBlobHash, *(quote.signature));

    return quoteOk;
}

bool TPMT_PUBLIC::ValidateCertifyNV(const std::vector<BYTE>& Nonce,
                                    const std::vector<BYTE>& expectedContents,
                                    UINT16 offset, 
                                    class NV_CertifyResponse& quote)
{
    TPM_ALG_ID hashAlg = GetSigningHashAlg(*this);
    TPMS_ATTEST attest = quote.certifyInfo;

    // Validate the quote
    if (attest.extraData != Nonce) {
        return false;
    }

    if (attest.magic != TPM_GENERATED::VALUE) {
        return false;
    }

    TPMS_NV_CERTIFY_INFO *nvInfo = dynamic_cast<TPMS_NV_CERTIFY_INFO *> (attest.attested);
    if (nvInfo->nvContents != expectedContents) {
        return false;
    }

    if (nvInfo->offset != offset) {
        return false;
    }

    // And finally check the signature
    ByteVec signedBlob = quote.certifyInfo.ToBuf();
    auto signedBlobHash = CryptoServices::Hash(hashAlg, signedBlob);
    bool quoteOk = CryptoServices::ValidateSignature(*this, signedBlobHash, *(quote.signature));

    return quoteOk;
}


std::vector<BYTE> TPMT_PUBLIC::Encrypt(std::vector<BYTE> _secret,
                                       std::vector<BYTE> _encodingParms)
{
    return CryptoServices::Encrypt(*this, _secret, _encodingParms);
}

std::vector<BYTE> TPMT_PUBLIC::EncryptSessionSalt(std::vector<BYTE> _secret)
{
    string idString = string("SECRET");
    vector<BYTE> label(idString.length() + 1);

    for (size_t j = 0; j < idString.length(); j++) {
        label[j] = (BYTE)idString[j];
    }

    return CryptoServices::Encrypt(*this, _secret, label);
}

ActivationData TPMT_PUBLIC::CreateActivation(std::vector<BYTE> _secret,
                                             TPM_ALG_ID _nameAlg,
                                             std::vector<BYTE> _nameOfKeyToBeActivated)
{
    TPMS_RSA_PARMS *parms = dynamic_cast<TPMS_RSA_PARMS *> (this->parameters);

    if (parms == NULL) {
        throw domain_error("Only RSA activation supported");
    }

    TPMT_SYM_DEF_OBJECT& symDef = parms->symmetric;

    if ((symDef.algorithm != TPM_ALG_ID::AES) ||
        (symDef.keyBits != 128) ||
        (symDef.mode != TPM_ALG_ID::CFB)) {
        throw domain_error("Unsupported wrapping scheme");
    }

    ByteVec seed = CryptoServices::GetRand(16);
    ActivationData act;

    // Encrypt the seed with the label IDENTITY
    string idString = string("IDENTITY");
    vector<BYTE> label(idString.length() + 1);

    for (size_t j = 0; j < idString.length(); j++) {
        label[j] = (BYTE)idString[j];
    }

    act.Secret = this->Encrypt(seed, label);
    ByteVec nullVec;

    // Now make the activation blob.

    TPM2B_DIGEST secretStruct(_secret);
    ByteVec lengthPrependedSecret = secretStruct.ToBuf();
    // Then make the cred blob. First the encrypted secret.  Make the key then encrypt.
    ByteVec symKey = KDF::KDFa(this->nameAlg, 
                               seed,
                               "STORAGE",
                               _nameOfKeyToBeActivated,
                               nullVec,
                               128);

    ByteVec encIdentity = CryptoServices::CFBXncrypt(true,
                                                     TPM_ALG_ID::AES,
                                                     symKey,
                                                     nullVec,
                                                     lengthPrependedSecret);
    // Next the HMAC protection
    int hmacKeyLen = CryptoServices::HashLength(this->nameAlg);
    ByteVec hmacKey = KDF::KDFa(this->nameAlg,
                                seed,
                                "INTEGRITY",
                                nullVec,
                                nullVec,
                                hmacKeyLen * 8);
    // Next the outer HMAC
    ByteVec outerHmac = CryptoServices::HMAC(this->nameAlg,
                                             hmacKey,
                                             Helpers::Concatenate(encIdentity,
                                                _nameOfKeyToBeActivated));
    // Assemble the activation blob
    //TPM2B_DIGEST outerHmac2bx(outerHmac);
    //auto outerHmac2b = outerHmac2bx.ToBuf();
    //ByteVec activationBlob = Helpers::Concatenate(outerHmac2b, encIdentity);

    act.CredentialBlob = TPMS_ID_OBJECT(outerHmac, encIdentity);

    return act;
}

DuplicationBlob TPMT_PUBLIC::CreateImportableObject(Tpm2& _tpm,
                                                    TPMT_PUBLIC _publicPart,
                                                    TPMT_SENSITIVE _sensitivePart,
                                                    TPMT_SYM_DEF_OBJECT innerWrapper)
{
    if (GetAlg() != TPM_ALG_ID::RSA) {
        throw new domain_error("Only import of keys to RSA storage parents supported");
    }

    DuplicationBlob blob;
    ByteVec encryptedSensitive;
    ByteVec innerWrapperKey;
    ByteVec NullVec;

    if (innerWrapper.algorithm == TPM_ALG_ID::_NULL) {
        encryptedSensitive = Helpers::ByteVecToLenPrependedByteVec(_sensitivePart.ToBuf());
    } else {
        if (innerWrapper.algorithm != TPM_ALG_ID::AES &&
            innerWrapper.keyBits != 128 &&
            innerWrapper.mode != TPM_ALG_ID::CFB) {
            throw new domain_error("innerWrapper KeyDef is not supported for import");
        }

        ByteVec sens = Helpers::ByteVecToLenPrependedByteVec(_sensitivePart.ToBuf());
        ByteVec toHash = Helpers::Concatenate(sens, _publicPart.GetName());

        ByteVec innerIntegrity = Helpers::ByteVecToLenPrependedByteVec(CryptoServices::Hash(nameAlg, toHash));
        ByteVec innerData = Helpers::Concatenate(innerIntegrity, sens);

        innerWrapperKey = _tpm.GetRandom(16);
        encryptedSensitive = CryptoServices::CFBXncrypt(true,
            TPM_ALG_ID::AES,
            innerWrapperKey,
            NullVec,
            innerData);
    }

    TPMS_RSA_PARMS *newParentParms = dynamic_cast<TPMS_RSA_PARMS *>(this->parameters);
    TPMT_SYM_DEF_OBJECT newParentSymDef = newParentParms->symmetric;

    if (newParentSymDef.algorithm != TPM_ALG_ID::AES &&
        newParentSymDef.keyBits != 128 && 
        newParentSymDef.mode != TPM_ALG_ID::CFB) {
        throw new domain_error("new parent symmetric key is not supported for import");
    }

    // Otherwise we know we are AES128
    ByteVec seed = _tpm.GetRandom(16);
    ByteVec parms = CryptoServices::StringToEncodingParms("DUPLICATE");
    ByteVec encryptedSeed = this->Encrypt(seed, parms);

    ByteVec symmKey = KDF::KDFa(this->nameAlg,
                                seed,
                                "STORAGE",
                                _publicPart.GetName(),
                                NullVec,
                                128);

    ByteVec dupSensitive = CryptoServices::CFBXncrypt(true,
                                                      TPM_ALG_ID::AES,
                                                      symmKey,
                                                      NullVec,
                                                      encryptedSensitive);

    int npNameNumBits = CryptoServices::HashLength(nameAlg) * 8;
    ByteVec hmacKey = KDF::KDFa(nameAlg, seed, "INTEGRITY", NullVec, NullVec, npNameNumBits);
    ByteVec outerDataToHmac = Helpers::Concatenate(dupSensitive, _publicPart.GetName());
    ByteVec outerHmacBytes = CryptoServices::HMAC(nameAlg, hmacKey, outerDataToHmac);
    ByteVec outerHmac = Helpers::ByteVecToLenPrependedByteVec(outerHmacBytes);
    ByteVec DuplicationBlob = Helpers::Concatenate(outerHmac, dupSensitive);

    blob.DuplicateObject = DuplicationBlob;
    blob.EncryptionKey = NullVec;
    blob.EncryptedSeed = encryptedSeed;
    blob.InnerWrapperKey = innerWrapperKey;

    return blob;
}

void TSS_KEY::CreateKey()
{
    TPMS_RSA_PARMS *parms = dynamic_cast<TPMS_RSA_PARMS *> (this->publicPart.parameters);

    if (parms == NULL) {
        throw domain_error("Only RSA activation supported");
    }

    int keySize = parms->keyBits;
    UINT32 exponent = parms->exponent;
    ByteVec pub, priv;
    CryptoServices::CreateRsaKey(keySize, exponent, pub, priv);

    TPM2B_PUBLIC_KEY_RSA *pubKey = dynamic_cast<TPM2B_PUBLIC_KEY_RSA *> (this->publicPart.unique);
    pubKey->buffer = pub;

    this->privatePart = priv;

    return;
}

TPMT_HA TPMT_HA::FromHashOfData(TPM_ALG_ID alg, const std::vector<BYTE>& data)
{
    return TPMT_HA(alg, CryptoServices::Hash(alg, data));
}

TPMT_HA::TPMT_HA(TPM_ALG_ID alg)
{
    auto hashLen = CryptoServices::HashLength(alg);
    hashAlg = alg;
    digest.resize(0);
    digest.resize(hashLen);
}

TPMT_HA TPMT_HA::FromHashOfString(TPM_ALG_ID alg, const std::string& str)
{
    // TODO: Unicode
    vector<BYTE> t;
    t.resize(str.length());

    for (size_t j = 0; j < str.size(); j++) {
        t[j] = str[j];
    }

    return TPMT_HA(alg, CryptoServices::Hash(alg, t));
}

TPMT_HA& TPMT_HA::Extend(const std::vector<BYTE>& x)
{
    vector<BYTE> t = Helpers::Concatenate(digest, x);
    digest = CryptoServices::Hash(hashAlg, t);
    return *this;

}

TPMT_HA TPMT_HA::Event(const std::vector<BYTE>& x)
{
    auto s = CryptoServices::Hash(hashAlg, x);
    vector<BYTE> t = Helpers::Concatenate(digest, s);
    digest = CryptoServices::Hash(hashAlg, t);
    return *this;

}

void TPMT_HA::Reset()
{
    std::fill(digest.begin(), digest.end(), 0);

}

std::vector<BYTE> TPMT_PUBLIC::GetName()
{
    std::vector<BYTE> pub = ToBuf();
    std::vector<BYTE> pubHash = CryptoServices::Hash(nameAlg, pub);
    std::vector<BYTE> theHashAlg = ValueTypeToByteArray((UINT16)nameAlg);
    pubHash.insert(pubHash.begin(), theHashAlg.begin(), theHashAlg.end());
    return pubHash;

}

SignResponse TSS_KEY::Sign(std::vector<BYTE>& _toSign, const TPMU_SIG_SCHEME& nonDefaultScheme)
{
    return CryptoServices::Sign(*this, _toSign, nonDefaultScheme);
}

std::vector<BYTE> Decrypt(std::vector<BYTE> _blob)
{
    _ASSERT(FALSE);
    return vector<BYTE>();
}

_TPMCPP_END
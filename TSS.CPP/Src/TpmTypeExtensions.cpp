/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"

_TPMCPP_BEGIN

using namespace std;

#define null  {}

void TPM_HANDLE::SetName(const ByteVec& name)
{
    UINT32 handleType = GetHandleType();

    if (handleType == TPM_HT::NV_INDEX ||
        handleType == TPM_HT::TRANSIENT || 
        handleType == TPM_HT::PERSISTENT ||
        handleType == TPM_HT::PERSISTENT)
    {
        Name = name;
        return;
    }

    // For the rest of the handle types the name is defined by the handle numeric value only
    if (name != GetName())
        throw runtime_error("Trying to set an invalid name of an entity with the name defined by the handle value");
}

ByteVec TPM_HANDLE::GetName() const
{
    switch (GetHandleType())
    {
        case 0:
        case 2:
        case 3:
        case 0x40:
            Name = Int32ToTpm(handle);
            return Name;

        case 1:
        case 0x80:
        case 0x81:
            if (Name.empty())
                throw runtime_error("Name is not set for handle");
            return Name;

        default:
            throw runtime_error("Unknown handle type");
    }
}

bool TPMT_PUBLIC::ValidateSignature(const ByteVec& signedData, const TPMU_SIGNATURE& sig)
{
    return Crypto::ValidateSignature(*this, signedData, sig);
}

bool TPMT_PUBLIC::ValidateQuote(const PCR_ReadResponse& expectedPcrVals,
                                const ByteVec& Nonce, QuoteResponse& quote) const
{
    TPM_ALG_ID hashAlg = GetSigningHashAlg(*this);
    TPMS_ATTEST attest = quote.quoted;

    // Validate the quote
    if (attest.extraData != Nonce)
        return false;

    if (attest.magic != TPM_GENERATED::VALUE)
        return false;

    TPMS_QUOTE_INFO *quoteInfo = dynamic_cast<TPMS_QUOTE_INFO*>(&*attest.attested);
    if (!quoteInfo)
        return false;

    if (quoteInfo->pcrSelect != expectedPcrVals.pcrSelectionOut)
        return false;

    // Check that the expected PCRs digest is as quoted
    if (quoteInfo->pcrDigest != Helpers::HashPcrs(hashAlg, expectedPcrVals.pcrValues))
        return false;

    // And finally check the signature
    ByteVec signedBlob = quote.quoted.toBytes();
    ByteVec signedBlobHash = Crypto::Hash(hashAlg, signedBlob);

    return Crypto::ValidateSignature(*this, signedBlobHash, *quote.signature);
}

bool TPMT_PUBLIC::ValidateCertify(const TPMT_PUBLIC& certifiedKey,
                                  const ByteVec& Nonce, CertifyResponse& certResponse) const
{
    TPM_ALG_ID hashAlg = GetSigningHashAlg(*this);
    TPMS_ATTEST attest = certResponse.certifyInfo;

    // Validate the quote
    if (attest.extraData != Nonce)
        return false;

    if (attest.magic != TPM_GENERATED::VALUE)
        return false;

    TPMS_CERTIFY_INFO *quoteInfo = dynamic_cast<TPMS_CERTIFY_INFO*>(&*attest.attested);
    if (quoteInfo == NULL)
        return false;

    if (quoteInfo->name != certifiedKey.GetName())
        return false;

    // TODO: Fully qualified name

    // And finally check the signature
    ByteVec signedBlob = certResponse.certifyInfo.toBytes();
    auto signedBlobHash = Crypto::Hash(hashAlg, signedBlob);
    return Crypto::ValidateSignature(*this, signedBlobHash, *certResponse.signature);
}

bool TPMT_PUBLIC::ValidateCertifyCreation(const ByteVec& Nonce, const ByteVec& creationHash,
                                          CertifyCreationResponse& certResponse) const
{
    TPM_ALG_ID hashAlg = GetSigningHashAlg(*this);
    TPMS_ATTEST attest = certResponse.certifyInfo;

    // Validate the quote
    if (attest.extraData != Nonce)
        return false;

    if (attest.magic != TPM_GENERATED::VALUE)
        return false;

    TPMS_CREATION_INFO *quoteInfo = dynamic_cast<TPMS_CREATION_INFO*>(&*attest.attested);
    if (!quoteInfo)
        return false;

    if (quoteInfo->creationHash != creationHash)
        return false;

    // And finally check the signature
    ByteVec signedBlob = certResponse.certifyInfo.toBytes();
    auto signedBlobHash = Crypto::Hash(hashAlg, signedBlob);
    return Crypto::ValidateSignature(*this, signedBlobHash, *certResponse.signature);
}

bool TPMT_PUBLIC::ValidateGetTime(const ByteVec& Nonce, GetTimeResponse& timeQuote) const
{
    TPM_ALG_ID hashAlg = GetSigningHashAlg(*this);
    TPMS_ATTEST attest = timeQuote.timeInfo;

    // Validate the quote
    if (attest.extraData != Nonce)
        return false;

    if (attest.magic != TPM_GENERATED::VALUE)
        return false;

    // And finally check the signature
    ByteVec signedBlob = timeQuote.timeInfo.toBytes();
    auto signedBlobHash = Crypto::Hash(hashAlg, signedBlob);
    return Crypto::ValidateSignature(*this, signedBlobHash, *timeQuote.signature);
}

bool TPMT_PUBLIC::ValidateCommandAudit(const TPMT_HA& expectedHash, const ByteVec& Nonce,
                                       GetCommandAuditDigestResponse& quote) const
{
    TPM_ALG_ID hashAlg = GetSigningHashAlg(*this);
    TPMS_ATTEST attest = quote.auditInfo;

    // Validate the quote
    if (attest.extraData != Nonce)
        return false;

    if (attest.magic != TPM_GENERATED::VALUE)
        return false;

    auto sessionInfo = dynamic_cast<TPMS_COMMAND_AUDIT_INFO*>(&*attest.attested);
    if (expectedHash != sessionInfo->auditDigest)
        return false;

    // And finally check the signature
    ByteVec signedBlob = quote.auditInfo.toBytes();
    auto signedBlobHash = Crypto::Hash(hashAlg, signedBlob);
    return Crypto::ValidateSignature(*this, signedBlobHash, *(quote.signature));
}

bool TPMT_PUBLIC::ValidateSessionAudit(const TPMT_HA& expectedHash, const ByteVec& Nonce,
                                       GetSessionAuditDigestResponse& quote) const
{
    TPM_ALG_ID hashAlg = GetSigningHashAlg(*this);
    TPMS_ATTEST attest = quote.auditInfo;

    // Validate the quote
    if (attest.extraData != Nonce)
        return false;

    if (attest.magic != TPM_GENERATED::VALUE)
        return false;

    auto sessionInfo = dynamic_cast<TPMS_SESSION_AUDIT_INFO*>(&*attest.attested);
    if (expectedHash != sessionInfo->sessionDigest)
        return false;

    // And finally check the signature
    ByteVec signedBlob = quote.auditInfo.toBytes();
    auto signedBlobHash = Crypto::Hash(hashAlg, signedBlob);
    return Crypto::ValidateSignature(*this, signedBlobHash, *(quote.signature));
}

bool TPMT_PUBLIC::ValidateCertifyNV(const ByteVec& Nonce, const ByteVec& expectedContents,
                                    UINT16 offset,  NV_CertifyResponse& quote) const
{
    TPM_ALG_ID hashAlg = GetSigningHashAlg(*this);
    TPMS_ATTEST attest = quote.certifyInfo;

    // Validate the quote
    if (attest.extraData != Nonce)
        return false;

    if (attest.magic != TPM_GENERATED::VALUE)
        return false;

    TPMS_NV_CERTIFY_INFO *nvInfo = dynamic_cast<TPMS_NV_CERTIFY_INFO*>(&*attest.attested);
    if (nvInfo->nvContents != expectedContents)
        return false;

    if (nvInfo->offset != offset)
        return false;

    // And finally check the signature
    ByteVec signedBlob = quote.certifyInfo.toBytes();
    auto signedBlobHash = Crypto::Hash(hashAlg, signedBlob);
    return Crypto::ValidateSignature(*this, signedBlobHash, *(quote.signature));
}


ByteVec TPMT_PUBLIC::Encrypt(const ByteVec& secret, const ByteVec& encodingParms) const
{
    return Crypto::Encrypt(*this, secret, encodingParms);
}

ByteVec TPMT_PUBLIC::EncryptSessionSalt(const ByteVec& secret) const
{
    string idString = string("SECRET");
    ByteVec label(idString.length() + 1);

    for (size_t j = 0; j < idString.length(); j++)
        label[j] = (BYTE)idString[j];

    return Crypto::Encrypt(*this, secret, label);
}

ActivationData TPMT_PUBLIC::CreateActivation(const ByteVec& secret, const ByteVec& activatedName) const
{
    TPMS_RSA_PARMS *parms = dynamic_cast<TPMS_RSA_PARMS*>(&*this->parameters);

    if (parms == NULL)
        throw domain_error("Only RSA activation supported");

    TPMT_SYM_DEF_OBJECT& symDef = parms->symmetric;

    if ((symDef.algorithm != TPM_ALG_ID::AES) ||
        (symDef.keyBits != 128) ||
        (symDef.mode != TPM_ALG_ID::CFB)) {
        throw domain_error("Unsupported wrapping scheme");
    }

    ByteVec seed = Crypto::GetRand(16);
    ActivationData act;

    // Encrypt the seed with the label IDENTITY
    string idString = string("IDENTITY");
    ByteVec label(idString.length() + 1);

    for (size_t j = 0; j < idString.length(); j++)
        label[j] = (BYTE)idString[j];

    act.Secret = this->Encrypt(seed, label);
    ByteVec nullVec;

    // Now make the activation blob.

    TPM2B_DIGEST secretStruct(secret);
    ByteVec lengthPrependedSecret = secretStruct.toBytes();
    // Then make the cred blob. First the encrypted secret.  Make the key then encrypt.
    ByteVec symKey = Crypto::KDFa(this->nameAlg, seed, "STORAGE",
                                  activatedName, nullVec, 128);

    ByteVec encIdentity = Crypto::CFBXcrypt(true, TPM_ALG_ID::AES, symKey, nullVec,
                                            lengthPrependedSecret);
    // Next the HMAC protection
    int hmacKeyLen = Crypto::HashLength(this->nameAlg);
    ByteVec hmacKey = Crypto::KDFa(this->nameAlg, seed, "INTEGRITY",
                                   nullVec, nullVec, hmacKeyLen * 8);
    // Next the outer HMAC
    ByteVec outerHmac = Crypto::HMAC(this->nameAlg, hmacKey,
                                     Helpers::Concatenate(encIdentity, activatedName));
    // Assemble the activation blob
    //TPM2B_DIGEST outerHmac2bx(outerHmac);
    //auto outerHmac2b = outerHmac2bx.toBytes();
    //ByteVec activationBlob = Helpers::Concatenate(outerHmac2b, encIdentity);

    act.CredentialBlob = TPMS_ID_OBJECT(outerHmac, encIdentity);
    return act;
}

DuplicationBlob TPMT_PUBLIC::GetDuplicationBlob(Tpm2& _tpm, const TPMT_PUBLIC& pub,
                                                const TPMT_SENSITIVE& sensitive,
                                                const TPMT_SYM_DEF_OBJECT& innerWrapper) const
{
    if (type() != TPM_ALG_ID::RSA)
        throw new domain_error("Only import of keys to RSA storage parents supported");

    DuplicationBlob blob;
    ByteVec encryptedSensitive;
    ByteVec innerWrapperKey;
    ByteVec iv;

    if (innerWrapper.algorithm == TPM_ALG_NULL)
        encryptedSensitive = sensitive.asTpm2B();
    else {
        if (innerWrapper.algorithm != TPM_ALG_ID::AES &&
            innerWrapper.keyBits != 128 &&
            innerWrapper.mode != TPM_ALG_ID::CFB) {
            throw new domain_error("innerWrapper KeyDef is not supported for import");
        }

        ByteVec sens = sensitive.asTpm2B();
        ByteVec toHash = Helpers::Concatenate(sens, pub.GetName());

        ByteVec innerIntegrity = Helpers::ToTpm2B(Crypto::Hash(nameAlg, toHash));
        ByteVec innerData = Helpers::Concatenate(innerIntegrity, sens);

        innerWrapperKey = Helpers::RandomBytes(16);
        encryptedSensitive = Crypto::CFBXcrypt(true, TPM_ALG_ID::AES,
                                               innerWrapperKey, iv, innerData);
    }

    TPMS_RSA_PARMS *newParentParms = dynamic_cast<TPMS_RSA_PARMS*>(&*this->parameters);
    TPMT_SYM_DEF_OBJECT newParentSymDef = newParentParms->symmetric;

    if (newParentSymDef.algorithm != TPM_ALG_ID::AES &&
        newParentSymDef.keyBits != 128 && 
        newParentSymDef.mode != TPM_ALG_ID::CFB)
    {
        throw new domain_error("new parent symmetric key is not supported for import");
    }

    // Otherwise we know we are AES128
    ByteVec seed = Helpers::RandomBytes(Crypto::HashLength(pub.nameAlg));
    ByteVec parms = Crypto::StringToEncodingParms("DUPLICATE");
    ByteVec encryptedSeed = this->Encrypt(seed, parms);

    ByteVec symmKey = Crypto::KDFa(this->nameAlg, seed, "STORAGE",
                                   pub.GetName(), null, 128);
    iv.clear();
    ByteVec dupSensitive = Crypto::CFBXcrypt(true, TPM_ALG_ID::AES, symmKey, iv, encryptedSensitive);

    int npNameNumBits = Crypto::HashLength(nameAlg) * 8;
    ByteVec hmacKey = Crypto::KDFa(nameAlg, seed, "INTEGRITY", null, null, npNameNumBits);
    ByteVec outerDataToHmac = Helpers::Concatenate(dupSensitive, pub.GetName());
    ByteVec outerHmacBytes = Crypto::HMAC(nameAlg, hmacKey, outerDataToHmac);
    ByteVec outerHmac = Helpers::ToTpm2B(outerHmacBytes);
    ByteVec DuplicationBlob = Helpers::Concatenate(outerHmac, dupSensitive);

    blob.DuplicateObject = DuplicationBlob;
    blob.EncryptedSeed = encryptedSeed;
    blob.InnerWrapperKey = innerWrapperKey;
    return blob;
} // TPMT_PUBLIC::GetDuplicationBlob()

DuplicationBlob TPMT_PUBLIC::CreateImportableObject(Tpm2& tpm, const TPMT_PUBLIC& pub, const TPMT_SENSITIVE& sensitive,
                                                const TPMT_SYM_DEF_OBJECT& innerWrapper)
{
    return GetDuplicationBlob(tpm, pub, sensitive, innerWrapper);
}

void TSS_KEY::CreateKey()
{
    TPMS_RSA_PARMS *parms = dynamic_cast<TPMS_RSA_PARMS*>(&*this->publicPart.parameters);

    if (parms == NULL)
        throw domain_error("Only RSA activation supported");

    int keySize = parms->keyBits;
    UINT32 exponent = parms->exponent;
    ByteVec pub, priv;
    Crypto::CreateRsaKey(keySize, exponent, pub, priv);

    TPM2B_PUBLIC_KEY_RSA *pubKey = dynamic_cast<TPM2B_PUBLIC_KEY_RSA*>(&*publicPart.unique);
    pubKey->buffer = pub;
    this->privatePart = priv;
}

TPMT_HA::TPMT_HA(TPM_ALG_ID alg)
{
    auto hashLen = Crypto::HashLength(alg);
    hashAlg = alg;
    digest.resize(0);
    digest.resize(hashLen);
}

TPMT_HA TPMT_HA::FromHashOfData(TPM_ALG_ID alg, const ByteVec& data)
{
    return TPMT_HA(alg, Crypto::Hash(alg, data));
}

TPMT_HA TPMT_HA::FromHashOfString(TPM_ALG_ID alg, const string& str)
{
    // TODO: Unicode
    ByteVec t(str.begin(), str.end());
    return TPMT_HA(alg, Crypto::Hash(alg, t));
}

UINT16 TPMT_HA::DigestSize()
{
    return Crypto::HashLength(hashAlg);
}

UINT16 TPMT_HA::DigestSize(TPM_ALG_ID alg)
{
    return Crypto::HashLength(alg);
}

TPMT_HA& TPMT_HA::Extend(const ByteVec& x)
{
    ByteVec t = Helpers::Concatenate(digest, x);
    digest = Crypto::Hash(hashAlg, t);
    return *this;

}

TPMT_HA TPMT_HA::Event(const ByteVec& x)
{
    auto s = Crypto::Hash(hashAlg, x);
    ByteVec t = Helpers::Concatenate(digest, s);
    digest = Crypto::Hash(hashAlg, t);
    return *this;
}

void TPMT_HA::Reset()
{
    fill(digest.begin(), digest.end(), (BYTE)0);
}

ByteVec TPMT_PUBLIC::GetName() const
{
    ByteVec pubHash = Crypto::Hash(nameAlg, toBytes());
    ByteVec theHashAlg = Int16ToTpm(nameAlg);
    pubHash.insert(pubHash.begin(), theHashAlg.begin(), theHashAlg.end());
    return pubHash;
}

SignResponse TSS_KEY::Sign(const ByteVec& dataToSign, const TPMU_SIG_SCHEME& nonDefaultScheme) const
{
    return Crypto::Sign(*this, dataToSign, nonDefaultScheme);
}

_TPMCPP_END

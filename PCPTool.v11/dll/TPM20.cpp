/*++

THIS CODE AND INFORMATION IS PROVIDED "AS IS" WITHOUT WARRANTY OF
ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
PARTICULAR PURPOSE.

Copyright (c) Microsoft Corporation.  All rights reserved.

Module Name:

    TPM20.cpp

Abstract:

    This file contains TPM 2.0 specific functions.

--*/

#include "stdafx.h"

// Hard-coded policies
const BYTE defaultUserPolicy[] = {0x8f, 0xcd, 0x21, 0x69, 0xab, 0x92, 0x69, 0x4e,
                                  0x0c, 0x63, 0x3f, 0x1a, 0xb7, 0x72, 0x84, 0x2b,
                                  0x82, 0x41, 0xbb, 0xc2, 0x02, 0x88, 0x98, 0x1f,
                                  0xc7, 0xac, 0x1e, 0xdd, 0xc1, 0xfd, 0xdb, 0x0e};
const BYTE adminObjectChangeAuthPolicy[] = {0xe5, 0x29, 0xf5, 0xd6, 0x11, 0x28, 0x72, 0x95,
                                            0x4e, 0x8e, 0xd6, 0x60, 0x51, 0x17, 0xb7, 0x57,
                                            0xe2, 0x37, 0xc6, 0xe1, 0x95, 0x13, 0xa9, 0x49,
                                            0xfe, 0xe1, 0xf2, 0x04, 0xc4, 0x58, 0x02, 0x3a};
const BYTE adminCertifyPolicy[] = {0xaf, 0x2c, 0xa5, 0x69, 0x69, 0x9c, 0x43, 0x6a,
                                   0x21, 0x00, 0x6f, 0x1c, 0xb8, 0xa2, 0x75, 0x6c,
                                   0x98, 0xbc, 0x1c, 0x76, 0x5a, 0x35, 0x59, 0xc5,
                                   0xfe, 0x1c, 0x3f, 0x5e, 0x72, 0x28, 0xa7, 0xe7};
const BYTE adminCertifyPolicyNoPin[] = {0x04, 0x8e, 0x9a, 0x3a, 0xce, 0x08, 0x58, 0x3f,
                                        0x79, 0xf3, 0x44, 0xff, 0x78, 0x5b, 0xbe, 0xa9,
                                        0xf0, 0x7a, 0xc7, 0xfa, 0x33, 0x25, 0xb3, 0xd4,
                                        0x9a, 0x21, 0xdd, 0x51, 0x94, 0xc6, 0x58, 0x50};
const BYTE adminActivateCredentialPolicy[] = {0xc4, 0x13, 0xa8, 0x47, 0xb1, 0x11, 0x12, 0xb1,
                                              0xcb, 0xdd, 0xd4, 0xec, 0xa4, 0xda, 0xaa, 0x15,
                                              0xa1, 0x85, 0x2c, 0x1c, 0x3b, 0xba, 0x57, 0x46,
                                              0x1d, 0x25, 0x76, 0x05, 0xf3, 0xd5, 0xaf, 0x53};

HRESULT
KDFa(
    LPCWSTR pszAlgId,
    _In_reads_(cbKey) PBYTE pbKey,
    UINT32 cbKey,
    __in PSTR label,
    _In_reads_opt_(cbContextU) PBYTE pbContextU,
    UINT32 cbContextU,
    _In_reads_opt_(cbContextV) PBYTE pbContextV,
    UINT32 cbContextV,
    __in UINT32 bits,
    _Out_writes_to_opt_(cbOutput, *pcbResult) PBYTE pbOutput,
    UINT32 cbOutput,
    _Out_ PUINT32 pcbResult
    )
{
    HRESULT hr = S_OK;
    size_t cbLabel = 0;
    UINT32 cbData = 0;
    PBYTE pbData = NULL;
    UINT32 cursor = 0;
    UINT32 iteration = 0;
    PUINT32 pI = NULL;
    PBYTE pbIterationBuffer = NULL;
    UINT32 cbIterationBuffer = 0;

    // Ensure the pointers parameters are set
    if((pbKey == NULL) ||
       (cbKey == 0) ||
       (label == NULL) ||
       (bits == 0) ||
       ((bits % 8) != 0) ||
       (pcbResult == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    *pcbResult = 0;

    // Just a size check?
    *pcbResult = (bits / 8);
    if((pbOutput == NULL) || (cbOutput == 0))
    {
        goto Cleanup;
    }
    else if(cbOutput < *pcbResult)
    {
        hr = HRESULT_FROM_WIN32(ERROR_INSUFFICIENT_BUFFER);
        goto Cleanup;
    }

    // Get the length of the label without terminator
    if(FAILED(hr = StringCchLengthA(label,
                                    MAX_PATH,
                                    &cbLabel)))
    {
        goto Cleanup;
    }

    // Calculate the required data buffer size and get the buffer
    cbData += sizeof(UINT32) +
              (UINT32)cbLabel +
              sizeof(BYTE) +
              cbContextU +
              cbContextV +
              sizeof(bits);
    if(FAILED(hr = AllocateAndZero((PVOID*)&pbData, cbData)))
    {
        goto Cleanup;
    }

    cursor = 0;
    pI = (PUINT32)&pbData[cursor];
    cursor += sizeof(UINT32);
    memcpy(&pbData[cursor], label, cbLabel);
    cursor += (UINT32)cbLabel;
    pbData[cursor] = 0;
    cursor++;
    if(cbContextU != 0)
    {
        memcpy(&pbData[cursor], pbContextU, cbContextU);
        cursor += cbContextU;
    }
    if(cbContextV != 0)
    {
        memcpy(&pbData[cursor], pbContextV, cbContextV);
        cursor += cbContextV;
    }
    ENDIANSWAP_UINT32TOARRAY(bits, pbData, cursor);
    cursor += sizeof(bits);

    // Allocate the iteration buffer
    if(FAILED(hr = TpmAttiShaHash(pszAlgId,
                                  pbKey,
                                  cbKey,
                                  pbData,
                                  cbData,
                                  NULL,
                                  0,
                                  &cbIterationBuffer)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = AllocateAndZero((PVOID*)&pbIterationBuffer, cbIterationBuffer)))
    {
        goto Cleanup;
    }

    // Generation loop
    cursor = 0;
    while(cursor < cbOutput)
    {
        iteration++;
        *pI = ENDIANSWAPUINT32(iteration);
        if(FAILED(hr = TpmAttiShaHash(pszAlgId,
                                      pbKey,
                                      cbKey,
                                      pbData,
                                      cbData,
                                      pbIterationBuffer,
                                      cbIterationBuffer,
                                      &cbIterationBuffer)))
        {
            goto Cleanup;
        }

        // Do we need the full buffer or is this the last iteration?
        if(cbIterationBuffer <= cbOutput - cursor)
        {
            memcpy(&pbOutput[cursor],
                   pbIterationBuffer,
                   cbIterationBuffer);
            cursor += cbIterationBuffer;
        }
        else
        {
            memcpy(&pbOutput[cursor],
                   pbIterationBuffer,
                   cbOutput - cursor);
            cursor += cbOutput - cursor;
        }
    }

Cleanup:
    ZeroAndFree((PVOID*)&pbData, cbData);
    ZeroAndFree((PVOID*)&pbIterationBuffer, cbIterationBuffer);
    return hr;
}

HRESULT
CFB(
    _In_reads_(cbSymKey) PBYTE pbSymKey,
    UINT32 cbSymKey,
    _Inout_updates_(cbIv) PBYTE pbIv,
    UINT32 cbIv,
    _Inout_updates_(cbData) PBYTE pbData,
    UINT32 cbData
    )
{
    HRESULT hr = S_OK;
    BCRYPT_ALG_HANDLE hAlgAES = NULL;
    BCRYPT_KEY_HANDLE hSymKey = NULL;
    DWORD messageBlockLength = 16;
    PBYTE pbIVInternal = NULL;
    UINT16 cbIVInternal = (UINT16)cbIv;
    PBYTE pbDataEncrypted = NULL;
    DWORD cbDataEncrypted = 0;

    // Ensure the pointers parameters are set
    if((pbSymKey == NULL) ||
       (cbSymKey == 0) ||
       (pbIv == NULL) ||
       (cbIv == 0) ||
       (cbIv != messageBlockLength) ||
       (pbData == NULL) ||
       (cbData == 0))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    if(FAILED(hr = AllocateAndZero((PVOID*)&pbIVInternal, cbIVInternal)))
    {
        goto Cleanup;
    }

    if(FAILED(hr = HRESULT_FROM_NT(BCryptOpenAlgorithmProvider(
                                                &hAlgAES,
                                                BCRYPT_AES_ALGORITHM,
                                                MS_PRIMITIVE_PROVIDER,
                                                0))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = HRESULT_FROM_NT(BCryptGenerateSymmetricKey(
                                               hAlgAES,
                                               &hSymKey,
                                               NULL,
                                               0,
                                               pbSymKey,
                                               cbSymKey,
                                               0))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = HRESULT_FROM_NT(BCryptSetProperty(
                                      hSymKey,
                                      BCRYPT_CHAINING_MODE,
                                      (PBYTE)BCRYPT_CHAIN_MODE_CFB,
                                      sizeof(BCRYPT_CHAIN_MODE_CFB),
                                      0))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = HRESULT_FROM_NT(BCryptSetProperty(
                                      hSymKey,
                                      BCRYPT_MESSAGE_BLOCK_LENGTH,
                                      (PBYTE)&messageBlockLength,
                                      sizeof(messageBlockLength),
                                      0))))
    {
        goto Cleanup;
    }

    memcpy(pbIVInternal, pbIv, cbIVInternal);
    if(FAILED(hr = HRESULT_FROM_NT(BCryptEncrypt(
                                  hSymKey,
                                  pbData,
                                  cbData,
                                  NULL,
                                  pbIVInternal,
                                  cbIVInternal,
                                  NULL,
                                  0,
                                  &cbDataEncrypted,
                                  BCRYPT_BLOCK_PADDING))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = AllocateAndZero((PVOID*)&pbDataEncrypted, cbDataEncrypted)))
    {
        goto Cleanup;
    }
    memcpy(pbIVInternal, pbIv, cbIVInternal);
    if(FAILED(hr = HRESULT_FROM_NT(BCryptEncrypt(
                                  hSymKey,
                                  pbData,
                                  cbData,
                                  NULL,
                                  pbIVInternal,
                                  cbIVInternal,
                                  pbDataEncrypted,
                                  cbDataEncrypted,
                                  &cbDataEncrypted,
                                  BCRYPT_BLOCK_PADDING))))
    {
        goto Cleanup;
    }
    memcpy(pbData, pbDataEncrypted, cbData);
    memcpy(pbIv, pbIVInternal, cbIv);

Cleanup:
    ZeroAndFree((PVOID*)&pbIVInternal, cbIVInternal);
    ZeroAndFree((PVOID*)&pbDataEncrypted, cbDataEncrypted);
    return hr;
}


HRESULT
GetNameFromPublic(
    _In_reads_(cbKeyBlob) PBYTE pbKeyBlob,
    UINT32 cbKeyBlob,
    _Out_opt_ LPCWSTR* pNameAlg,
    _Out_writes_to_opt_(cbOutput, *pcbResult) PBYTE pbOutput,
    UINT32 cbOutput,
    _Out_ PUINT32 pcbResult
)
{
    HRESULT hr = S_OK;
    UINT32 cursor = 0;
    UINT16 nameAlg = 0;
    LPCWSTR szHashAlg = NULL;

    if((pbKeyBlob == NULL) ||
       (cbKeyBlob == 0) ||
       (pcbResult == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Make OACR happy
    if(pNameAlg != NULL)
    {
        *pNameAlg = NULL;
    }

    //objectType
    if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &cursor, sizeof(UINT16))))
    {
        goto Cleanup;
    }
    // Get the nameAlg of the object
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &nameAlg)))
    {
        goto Cleanup;
    }

    // Select hash algorithm
    switch(nameAlg)
    {
        case TPM_API_ALG_ID_SHA1: //TPM_ALG_SHA1
            szHashAlg = BCRYPT_SHA1_ALGORITHM;
            *pcbResult = SHA1_DIGEST_SIZE;
            break;
        case TPM_API_ALG_ID_SHA256: //TPM_ALG_SHA256
            szHashAlg = BCRYPT_SHA256_ALGORITHM;
            *pcbResult = SHA256_DIGEST_SIZE;
            break;
        case TPM_API_ALG_ID_SHA384: //TPM_ALG_SHA384
            szHashAlg = BCRYPT_SHA384_ALGORITHM;
            *pcbResult = SHA384_DIGEST_SIZE;
            break;
        default:
            hr = E_INVALIDARG;
            goto Cleanup;
    }

    *pcbResult += sizeof(UINT16);

    // Just a size check?
    if((pbOutput == NULL) || (cbOutput == 0))
    {
        goto Cleanup;
    }
    else if(cbOutput < *pcbResult)
    {
        hr = HRESULT_FROM_WIN32(ERROR_INSUFFICIENT_BUFFER);
        goto Cleanup;
    }

    // Calculate Object Digest
    if(FAILED(hr = TpmAttiShaHash(szHashAlg,
                                  NULL,
                                  0,
                                  pbKeyBlob,
                                  cbKeyBlob,
                                  &pbOutput[sizeof(UINT16)],
                                  cbOutput - sizeof(UINT16),
                                  (PUINT32)pcbResult)))
    {
        goto Cleanup;
    }

    *pcbResult += sizeof(UINT16);
    ENDIANSWAP_UINT16TOARRAY(nameAlg, pbOutput, 0);

    if(pNameAlg != NULL)
    {
        *pNameAlg = szHashAlg;
    }

Cleanup:
    return hr;
}

HRESULT
GetKeyHandleFromPubKeyBlob20(
    _In_reads_(cbKeyBlob) PBYTE pbKeyBlob,
    UINT32 cbKeyBlob,
    BCRYPT_ALG_HANDLE hAlg,
    _Out_ BCRYPT_KEY_HANDLE* phPubKey,
    _Out_opt_ PUINT32 pcbTrailing,
    _Out_opt_ LPCWSTR* pSignHashAlg
)
{
    HRESULT hr = S_OK;
    UINT32 cursor = 0;
    UINT32 cbRsaPubKey = 0;
    PBYTE pbRsaPubKey = NULL;
    BCRYPT_RSAKEY_BLOB* RsaPubKey = NULL;
    BYTE defaultExponent[] = {0x01, 0x00, 0x01};
    UINT16 objectType = 0;
    UINT16 symAlg = 0;
    UINT16 signHash = 0x0010; //TPM_ALG_NULL
    UINT16 keyBits = 0;
    PBYTE pExponent = NULL;
    UINT32 cbExponent = 0;
    PBYTE pbExponent = NULL;
    UINT16 cbPubKey = 0;
    PBYTE pbPubKey = NULL;

    if((hAlg == NULL) ||
       (phPubKey == NULL) ||
       (pbKeyBlob == NULL) ||
       (cbKeyBlob == 0))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Make OACR happy
    *phPubKey = NULL;

    // Unpack key structure
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &objectType)))
    {
        goto Cleanup;
    }
    if(objectType != 0x0001) //TPM_ALG_RSA
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    //nameAlg
    if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &cursor, sizeof(UINT16))))
    {
        goto Cleanup;
    }
    //objectAttributes
    if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &cursor, sizeof(UINT32))))
    {
        goto Cleanup;
    }
    //authPolicy
    if(FAILED(hr = SkipBigEndian2B(pbKeyBlob, cbKeyBlob, &cursor)))
    {
        goto Cleanup;
    }
    //symmetric.algoritm
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &symAlg)))
    {
        goto Cleanup;
    }
    if(symAlg == 0x0006) //TPM_ALG_AES
    {
        //symmetric.keyBits
        if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &cursor, sizeof(UINT16))))
        {
            goto Cleanup;
        }
        //symmetric.mode
        if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &cursor, sizeof(UINT16))))
        {
            goto Cleanup;
        }
    }
    else if(symAlg != 0x0010) //TPM_ALG_NULL
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    //signScheme
    UINT16 scheme = 0;
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &scheme)))
    {
        goto Cleanup;
    }
    if(scheme == 0x0010) //TPM_ALG_NULL
    {
        // No further parameter to read
    }
    else if(scheme == 0x0014) //TPM_ALG_RSASSA
    {
        // HashAlg
        if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &signHash)))
        {
            goto Cleanup;
        }
    }
    else
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    //keyBits
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &keyBits)))
    {
        goto Cleanup;
    }
    //exponent 
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &pExponent, sizeof(UINT32))))
    {
        goto Cleanup;
    }
    if(*((PUINT32)pExponent) != 0)
    {
        for(BYTE n = 0; n < sizeof(UINT32); n++)
        {
            if(pExponent[n] != 0x00)
            {
                pbExponent = &pExponent[n];
                cbExponent = sizeof(UINT32) - n;
                break;
            }
        }
    }
    else
    {
        pbExponent = defaultExponent;
        cbExponent = sizeof(defaultExponent);
    }
    //pubKey
    if(FAILED(hr = ReadBigEndian2B(pbKeyBlob, cbKeyBlob, &cursor, &cbPubKey, &pbPubKey)))
    {
        goto Cleanup;
    }

    // Create a BCRYPT inport key buffer from the data
    cbRsaPubKey = sizeof(BCRYPT_RSAKEY_BLOB) +
                  cbExponent +
                  cbPubKey;
    if(FAILED(hr = AllocateAndZero((PVOID*)&pbRsaPubKey, cbRsaPubKey)))
    {
        goto Cleanup;
    }
    RsaPubKey = (BCRYPT_RSAKEY_BLOB*) pbRsaPubKey;
    RsaPubKey->Magic = BCRYPT_RSAPUBLIC_MAGIC;
    RsaPubKey->BitLength = keyBits;
    RsaPubKey->cbPublicExp = cbExponent;
    RsaPubKey->cbModulus = cbPubKey;
    if(memcpy_s(&pbRsaPubKey[sizeof(BCRYPT_RSAKEY_BLOB)], cbRsaPubKey - sizeof(BCRYPT_RSAKEY_BLOB), pbExponent, cbExponent))
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    if(memcpy_s(&pbRsaPubKey[sizeof(BCRYPT_RSAKEY_BLOB) + RsaPubKey->cbPublicExp], cbRsaPubKey - (sizeof(BCRYPT_RSAKEY_BLOB) + RsaPubKey->cbPublicExp), pbPubKey, cbPubKey))
    {
        hr = E_FAIL;
        goto Cleanup;
    }

    // Load the key
    if(FAILED(hr = HRESULT_FROM_NT(BCryptImportKeyPair(
                                        hAlg,
                                        NULL,
                                        BCRYPT_RSAPUBLIC_BLOB,
                                        phPubKey,
                                        pbRsaPubKey,
                                        cbRsaPubKey,
                                        0))))
    {
        goto Cleanup;
    }

    // Return the index of the trailing data if requested
    if(pcbTrailing != NULL)
    {
        *pcbTrailing = cursor;
    }

    // Return the signature hash algorithm if requested
    if(pSignHashAlg != NULL)
    {
        switch(signHash)
        {
            case TPM_API_ALG_ID_SHA1: //TPM_ALG_SHA1
                *pSignHashAlg = BCRYPT_SHA1_ALGORITHM;
                break;
            case TPM_API_ALG_ID_SHA256: //TPM_ALG_SHA256
                *pSignHashAlg = BCRYPT_SHA256_ALGORITHM;
                break;
            case TPM_API_ALG_ID_SHA384: //TPM_ALG_SHA384
                *pSignHashAlg = BCRYPT_SHA384_ALGORITHM;
                break;
            case 0x0010: //TPM_ALG_NULL
                *pSignHashAlg = NULL;
                break;
            default:
                hr = E_INVALIDARG;
                goto Cleanup;
        }
    }

Cleanup:
    if((FAILED(hr)) && (phPubKey != NULL) && (*phPubKey != NULL))
    {
        BCryptDestroyKey(*phPubKey);
        *phPubKey = NULL;
    }
    ZeroAndFree((PVOID*)&pbRsaPubKey, cbRsaPubKey);
    return hr;
}

HRESULT
PubKeyFromIdBinding20(
    _In_reads_(cbIdBinding) PBYTE pbIdBinding,
    UINT32 cbIdBinding,
    BCRYPT_ALG_HANDLE hRsaAlg,
    _Out_ BCRYPT_KEY_HANDLE* phAikPub
    )
{
    HRESULT hr = S_OK;
    UINT32 cursor = 0;
    PBYTE pbAikPub = NULL;
    UINT32 cbAikPub = 0;

    // Check the parameters
    if((pbIdBinding == NULL) ||
       (cbIdBinding == 0) ||
       (hRsaAlg == NULL) ||
       (phAikPub == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Locate the public AIK in the blob
    if(FAILED(hr = ReadBigEndian2B(pbIdBinding, cbIdBinding, &cursor, (PUINT16)&cbAikPub, &pbAikPub)))
    {
        goto Cleanup;
    }

    // Get a handle to the AIK
    if(FAILED(hr = GetKeyHandleFromPubKeyBlob20(
                            pbAikPub,
                            cbAikPub,
                            hRsaAlg,
                            phAikPub,
                            NULL,
                            NULL)))
    {
        goto Cleanup;
    }

Cleanup:
    return hr;
}

HRESULT
GenerateActivation20(
    BCRYPT_KEY_HANDLE hEkPub,
    _In_reads_(cbIdBinding) PBYTE pbIdBinding,
    UINT32 cbIdBinding,
    _In_reads_opt_(cbNonce) PBYTE pbNonce,
    UINT32 cbNonce,
    _In_reads_(cbSecret) PBYTE pbSecret,
    UINT16 cbSecret,
    _Out_writes_to_opt_(cbOutput, *pcbResult) PBYTE pbOutput,
    UINT32 cbOutput,
    _Out_ PUINT32 pcbResult
    )
{
    HRESULT hr = S_OK;
    UINT32 cursor = 0;
    PBYTE pbAikPub = NULL;
    UINT32 cbAikPub = 0;
    BCRYPT_ALG_HANDLE hRsaAlg = NULL;
    BCRYPT_ALG_HANDLE hAesAlg = NULL;
    BCRYPT_ALG_HANDLE hRngAlg = NULL;
    BCRYPT_KEY_HANDLE hAikPub = NULL;
    BCRYPT_KEY_HANDLE hAesKey = NULL;
    LPCWSTR nameAlg = NULL;
    LPCWSTR signHashAlg = NULL;
    UINT32 cbEncSecret = 0;
    PBYTE pbCreationData = NULL;
    UINT32 cbCreationData = 0;
    PBYTE pbAttest = NULL;
    UINT32 cbAttest = 0;
    PBYTE pbAttestDigest = NULL;
    UINT32 cbAttestDigest = 0;
    UINT16 signatureScheme = 0;
    UINT16 signatureHash = 0;
    PBYTE pbSignature = NULL;
    UINT32 cbSignature = 0;
    PBYTE pbAikName = NULL;
    UINT32 cbAikName = 0;
    PBYTE pbCreationNonce = NULL;
    UINT16 cbCreationNonce = 0;
    PBYTE pbCreationDataDigest = NULL;
    UINT32 cbCreationDataDigest = 0;
    UINT32 cursorAttest = 0;
    PBYTE pbSignedAikName = NULL;
    UINT16 cbSignedAikName = 0;
    BCRYPT_PKCS1_PADDING_INFO pPkcs = {0};
    UINT32 cbCredentialValue = 0;
    BYTE seed[16] = {0};
    BYTE aesKey[16] = {0};
    UINT32 cbAesKey = 0;
    BYTE aesIv[16] = {0};
    BYTE hmacKey[SHA256_DIGEST_SIZE] = {0};
    UINT32 cbHmacKey = 0;

    PSTR szLabel = "IDENTITY";
    BCRYPT_OAEP_PADDING_INFO PaddingInfo = {BCRYPT_SHA256_ALGORITHM,
                                            (PUCHAR)szLabel,
                                            (ULONG)strlen(szLabel) + 1};
    UINT32 cbActivation = 0;

    // Please note: This function requires a windows defined EKNameAlg of SHA256

    // Check the parameters
    if((hEkPub == NULL) ||
       (pbIdBinding == NULL) ||
       (cbIdBinding == 0) ||
       (pbSecret == NULL) ||
       (cbSecret == 0) ||
       (cbSecret > SHA256_DIGEST_SIZE) ||
       (pcbResult == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Make OACR happy
    *pcbResult = 0;

    // Calculate the required output buffer
    if(FAILED(hr = HRESULT_FROM_NT(BCryptGetProperty(
                                        hEkPub,
                                        BCRYPT_BLOCK_LENGTH,
                                        (PBYTE)&cbEncSecret,
                                        sizeof(cbEncSecret),
                                        (PULONG)&cursor,
                                        0))))
    {
        goto Cleanup;
    }

    // Make sure that the AIK is strong enough
    if(cbEncSecret < 256)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    cbCredentialValue = sizeof(UINT16) +                      // TPM2B_ID_OBJECT.size
                        sizeof(UINT16) + SHA256_DIGEST_SIZE + // TPM2B_DIGEST outerHAMC
                        sizeof(UINT16) + cbSecret;            // TPM2B_DIGEST credential value

    cbActivation = cbCredentialValue + sizeof(UINT16) + cbEncSecret;
    if((pbOutput == NULL) || (cbOutput == 0))
    {
        *pcbResult = cbActivation;
        goto Cleanup;
    }
    if(cbOutput > cbActivation)
    {
        hr = HRESULT_FROM_WIN32(ERROR_INSUFFICIENT_BUFFER);
        *pcbResult = cbActivation;
        goto Cleanup;
    }

    // Open providers
    if(FAILED(hr = HRESULT_FROM_NT(BCryptOpenAlgorithmProvider(
                                                &hRngAlg,
                                                BCRYPT_RNG_ALGORITHM,
                                                NULL,
                                                0))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = HRESULT_FROM_NT(BCryptOpenAlgorithmProvider(
                                                &hAesAlg,
                                                BCRYPT_AES_ALGORITHM,
                                                NULL,
                                                0))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = HRESULT_FROM_NT(BCryptOpenAlgorithmProvider(
                                                &hRsaAlg,
                                                BCRYPT_RSA_ALGORITHM,
                                                NULL,
                                                0))))
    {
        goto Cleanup;
    }

    // Locate the public AIK in the blob
    cursor = 0;
    if(FAILED(hr = ReadBigEndian2B(pbIdBinding, cbIdBinding, &cursor, (PUINT16)&cbAikPub, &pbAikPub)))
    {
        goto Cleanup;
    }

    // Locate the creation data in the blob
    if(FAILED(hr = ReadBigEndian2B(pbIdBinding, cbIdBinding, &cursor, (PUINT16)&cbCreationData, &pbCreationData)))
    {
        goto Cleanup;
    }

    // Locate the attest data in the blob
    if(FAILED(hr = ReadBigEndian2B(pbIdBinding, cbIdBinding, &cursor, (PUINT16)&cbAttest, &pbAttest)))
    {
        goto Cleanup;
    }

    // Locate the signature in the blob
    if(FAILED(hr = ReadBigEndian(pbIdBinding, cbIdBinding, &cursor, &signatureScheme)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbIdBinding, cbIdBinding, &cursor, &signatureHash)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian2B(pbIdBinding, cbIdBinding, &cursor, (PUINT16)&cbSignature, &pbSignature)))
    {
        goto Cleanup;
    }

    // Get a handle to the AIK
    if(FAILED(hr = GetKeyHandleFromPubKeyBlob20(
                            pbAikPub,
                            cbAikPub,
                            hRsaAlg,
                            &hAikPub,
                            &cbAikPub,
                            &signHashAlg)))
    {
        goto Cleanup;
    }

    // Calculate the AIK name
    if(FAILED(hr = GetNameFromPublic(
                            pbAikPub,
                            cbAikPub,
                            &nameAlg,
                            NULL,
                            0,
                            &cbAikName)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = AllocateAndZero((PVOID*)&pbAikName, cbAikName)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = GetNameFromPublic(
                            pbAikPub,
                            cbAikPub,
                            &nameAlg,
                            pbAikName,
                            cbAikName,
                            &cbAikName)))
    {
        goto Cleanup;
    }

    // Verify nonce, if requested
    if(pbNonce != NULL)
    {
        UINT32 count = 0;
        UINT32 cursorCreationData = 0;
        PBYTE pbAttestCreationDataDigest = NULL;
        UINT16 cbAttestCreationDataDigest = 0;

        if(FAILED(hr = ReadBigEndian(pbCreationData, cbCreationData, &cursorCreationData, &count)))
        {
            goto Cleanup;
        }
        for(UINT32 n = 0; n < count; n++)
        {
            BYTE sizeOfSelect = 0;
            //TPM_ALG_ID
            if(FAILED(hr = SkipBigEndian(pbCreationData, cbCreationData, &cursorCreationData, sizeof(UINT16))))
            {
                goto Cleanup;
            }
            if(FAILED(hr = ReadBigEndian(pbCreationData, cbCreationData, &cursorCreationData, &sizeOfSelect)))
            {
                goto Cleanup;
            }
            if(FAILED(hr = SkipBigEndian(pbCreationData, cbCreationData, &cursorCreationData, (UINT32)sizeOfSelect)))
            {
                goto Cleanup;
            }
        }
        //TPM2B_DIGEST
        if(FAILED(hr = SkipBigEndian2B(pbCreationData, cbCreationData, &cursorCreationData)))
        {
            goto Cleanup;
        }
        //TPMA_LOCALITY
        if(FAILED(hr = SkipBigEndian(pbCreationData, cbCreationData, &cursorCreationData, sizeof(BYTE))))
        {
            goto Cleanup;
        }
        //TPM_ALG_ID
        if(FAILED(hr = SkipBigEndian(pbCreationData, cbCreationData, &cursorCreationData, sizeof(UINT16))))
        {
            goto Cleanup;
        }
        //TPM2B_NAME
        if(FAILED(hr = SkipBigEndian2B(pbCreationData, cbCreationData, &cursorCreationData)))
        {
            goto Cleanup;
        }
        //TPM2B_NAME
        if(FAILED(hr = SkipBigEndian2B(pbCreationData, cbCreationData, &cursorCreationData)))
        {
            goto Cleanup;
        }
        //TPM2B_DATA
        if(FAILED(hr = ReadBigEndian2B(pbCreationData, cbCreationData, &cursorCreationData, &cbCreationNonce, &pbCreationNonce)))
        {
            goto Cleanup;
        }
        // Compare nonces
        if((cbNonce != (ULONG)cbCreationNonce) ||
           (memcmp(pbNonce, pbCreationNonce, cbNonce) != 0))
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }

        // Create creation data digest
        if(FAILED(hr = TpmAttiShaHash(nameAlg,
                                      NULL,
                                      0,
                                      pbCreationData,
                                      cbCreationData,
                                      NULL,
                                      0,
                                      &cbCreationDataDigest)))
        {
            goto Cleanup;
        }
        if(FAILED(hr = AllocateAndZero((PVOID*)&pbCreationDataDigest, cbCreationDataDigest)))
        {
            goto Cleanup;
        }
        if(FAILED(hr = TpmAttiShaHash(nameAlg,
                                      NULL,
                                      0,
                                      pbCreationData,
                                      cbCreationData,
                                      pbCreationDataDigest,
                                      cbCreationDataDigest,
                                      &cbCreationDataDigest)))
        {
            goto Cleanup;
        }

        // Verify creation data digest in attestation structure
        //TPM_GENERATED
        if(FAILED(hr = SkipBigEndian(pbAttest, cbAttest, &cursorAttest, sizeof(UINT32))))
        {
            goto Cleanup;
        }
        //TPMI_ST_ATTEST
        if(FAILED(hr = SkipBigEndian(pbAttest, cbAttest, &cursorAttest, sizeof(UINT16))))
        {
            goto Cleanup;
        }
        //TPM2B_NAME
        if(FAILED(hr = SkipBigEndian2B(pbAttest, cbAttest, &cursorAttest)))
        {
            goto Cleanup;
        }
        //TPM2B_DATA
        if(FAILED(hr = ReadBigEndian2B(pbAttest, cbAttest, &cursorAttest, &cbCreationNonce, &pbCreationNonce)))
        {
            goto Cleanup;
        }
        // Compare nonces
        if((cbNonce != (ULONG)cbCreationNonce) ||
           (memcmp(pbNonce, pbCreationNonce, cbNonce) != 0))
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }
        //TPMS_CLOCK_INFO
        if(FAILED(hr = SkipBigEndian(pbAttest, cbAttest, &cursorAttest, sizeof(UINT64) + sizeof(UINT32) + sizeof(UINT32) + sizeof(BYTE))))
        {
            goto Cleanup;
        }
        //firmwareVersion
        if(FAILED(hr = SkipBigEndian(pbAttest, cbAttest, &cursorAttest, sizeof(UINT64))))
        {
            goto Cleanup;
        }
        // Object name
        if(FAILED(hr = SkipBigEndian2B(pbAttest, cbAttest, &cursorAttest)))
        {
            goto Cleanup;
        }
        // Verify creation data digest in attest structure
        if(FAILED(hr = ReadBigEndian2B(pbAttest, cbAttest, &cursorAttest, &cbAttestCreationDataDigest, &pbAttestCreationDataDigest)))
        {
            goto Cleanup;
        }
        if((cbCreationDataDigest != (ULONG)cbAttestCreationDataDigest) ||
           (memcmp(pbCreationDataDigest, pbAttestCreationDataDigest, cbCreationDataDigest) != 0))
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }
        cursorAttest = 0;
    }

    // Parse Attestation Blob
    //TPM_GENERATED
    UINT32 generated = 0;
    if(FAILED(hr = ReadBigEndian(pbAttest, cbAttest, &cursorAttest, &generated)))
    {
        goto Cleanup;
    }
    if(generated != 0xff544347) // TPM_GENERATED
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    //TPMI_ST_ATTEST
    UINT16 attestType = 0;
    if(FAILED(hr = ReadBigEndian(pbAttest, cbAttest, &cursorAttest, &attestType)))
    {
        goto Cleanup;
    }
    if(attestType != 0x801A) //TPM_ST_ATTEST_CREATION
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    //TPM2B_NAME
    if(FAILED(hr = SkipBigEndian2B(pbAttest, cbAttest, &cursorAttest)))
    {
        goto Cleanup;
    }
    //TPM2B_DATA
    if(FAILED(hr = SkipBigEndian2B(pbAttest, cbAttest, &cursorAttest)))
    {
        goto Cleanup;
    }
    //TPMS_CLOCK_INFO
    if(FAILED(hr = SkipBigEndian(pbAttest, cbAttest, &cursorAttest, sizeof(UINT64) + sizeof(BYTE) + sizeof(UINT32) + sizeof(UINT32))))
    {
        goto Cleanup;
    }
    //firmwareVersion
    if(FAILED(hr = SkipBigEndian(pbAttest, cbAttest, &cursorAttest, sizeof(UINT64))))
    {
        goto Cleanup;
    }
    //TPM2B_NAME
    if(FAILED(hr = ReadBigEndian2B(pbAttest, cbAttest, &cursorAttest, &cbSignedAikName, &pbSignedAikName)))
    {
        goto Cleanup;
    }
    // Verify attested key name
    if((cbAikName != (ULONG)cbSignedAikName) ||
        (memcmp(pbAikName, pbSignedAikName, cbAikName) != 0))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    // Verify creation data digest in attest structure
    if(FAILED(hr = SkipBigEndian2B(pbAttest, cbAttest, &cursorAttest)))
    {
        goto Cleanup;
    }

    // Create attest data digest
    if(FAILED(hr = TpmAttiShaHash(signHashAlg,
                                  NULL,
                                  0,
                                  pbAttest,
                                  cbAttest,
                                  NULL,
                                  0,
                                  &cbAttestDigest)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = AllocateAndZero((PVOID*)&pbAttestDigest, cbAttestDigest)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = TpmAttiShaHash(signHashAlg,
                                  NULL,
                                  0,
                                  pbAttest,
                                  cbAttest,
                                  pbAttestDigest,
                                  cbAttestDigest,
                                  &cbAttestDigest)))
    {
        goto Cleanup;
    }

    // Verify attestation signature
    pPkcs.pszAlgId = signHashAlg;
    if(FAILED(hr = HRESULT_FROM_NT(BCryptVerifySignature(
                                hAikPub,
                                &pPkcs,
                                pbAttestDigest,
                                cbAttestDigest,
                                pbSignature,
                                cbSignature,
                                BCRYPT_PAD_PKCS1))))
    {
        goto Cleanup;
    }

    // Prepare the credential value
    cursor = 0;
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)(cbCredentialValue - sizeof(UINT16)))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)SHA256_DIGEST_SIZE)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(pbOutput, cbOutput, &cursor, SHA256_DIGEST_SIZE)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)cbSecret)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, pbSecret, cbSecret)))
    {
        goto Cleanup;
    }
    // Note: We are temporarily using the space. The encrypted seed will end up overwriting this
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, pbAikName, cbAikName)))
    {
        goto Cleanup;
    }

    // Create a random seed
    if(FAILED(hr = HRESULT_FROM_NT(BCryptGenRandom(hRngAlg,
                                                   (PUCHAR)seed,
                                                   sizeof(seed),
                                                   0))))
    {
        goto Cleanup;
    }

    // Generate the symmetric key with the KDF
    if(FAILED(hr = KDFa(BCRYPT_SHA256_ALGORITHM,
                        seed,
                        sizeof(seed),
                        "STORAGE",
                        pbAikName,
                        cbAikName,
                        NULL,
                        0,
                        sizeof(aesKey) * 8,
                        aesKey,
                        sizeof(aesKey),
                        &cbAesKey)))
    {
        goto Cleanup;
    }
    // Encrypt the CV value in place
    cursor = sizeof(UINT16) + sizeof(UINT16) + SHA256_DIGEST_SIZE;
    memset(aesIv, 0x00, sizeof(aesIv));
    if(FAILED(hr = CFB(aesKey,
                       sizeof(aesKey),
                       aesIv,
                       sizeof(aesIv),
                       &pbOutput[cursor],
                       cbCredentialValue - cursor)))
    {
        goto Cleanup;
    }
    // Generate the HMAC key
    if(FAILED(hr = KDFa(BCRYPT_SHA256_ALGORITHM,
                        seed,
                        sizeof(seed),
                        "INTEGRITY",
                        NULL,
                        0,
                        NULL,
                        0,
                        sizeof(hmacKey) * 8,
                        hmacKey,
                        sizeof(hmacKey), &cbHmacKey)))
    {
        goto Cleanup;
    }
    // Calculate the outer HMAC
    cursor = sizeof(UINT16) + sizeof(UINT16) + SHA256_DIGEST_SIZE;
    if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA256_ALGORITHM,
                                  hmacKey,
                                  sizeof(hmacKey),
                                  &pbOutput[cursor],
                                  sizeof(UINT16) + cbSecret + sizeof(UINT16) + SHA256_DIGEST_SIZE, // Include the trailing temporary stored AIKname
                                  &pbOutput[sizeof(UINT16) + sizeof(UINT16)],
                                  SHA256_DIGEST_SIZE,
                                  &cursor)))
    {
        goto Cleanup;
    }
    //Encrypt the Seed
    cursor = cbCredentialValue + sizeof(UINT16);
    PaddingInfo.pszAlgId = nameAlg;
    if(FAILED(hr = HRESULT_FROM_NT(BCryptEncrypt(
                                        hEkPub,
                                        (PUCHAR)seed,
                                        sizeof(seed),
                                        &PaddingInfo,
                                        NULL,
                                        0,
                                        &pbOutput[cursor],
                                        cbOutput - cursor,
                                        (PDWORD)&cbEncSecret,
                                        BCRYPT_PAD_OAEP))))
    {
        goto Cleanup;
    }
    cursor = cbCredentialValue;
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)cbEncSecret)))
    {
        goto Cleanup;
    }
    *pcbResult = cbOutput;

Cleanup:
    if(hAikPub != NULL)
    {
        BCryptDestroyKey(hAikPub);
        hAikPub = NULL;
    }
    if(hAesKey != NULL)
    {
        BCryptDestroyKey(hAesKey);
        hAesKey = NULL;
    }
    if(hRsaAlg != NULL)
    {
        BCryptCloseAlgorithmProvider(hAikPub, 0);
        hRsaAlg = NULL;
    }
    if(hAesAlg != NULL)
    {
        BCryptCloseAlgorithmProvider(hAesAlg, 0);
        hAesAlg = NULL;
    }
    if(hRngAlg != NULL)
    {
        BCryptCloseAlgorithmProvider(hRngAlg, 0);
        hRngAlg = NULL;
    }
    ZeroAndFree((PVOID*)&pbAttestDigest, cbAttestDigest);
    ZeroAndFree((PVOID*)&pbCreationDataDigest, cbCreationDataDigest);
    ZeroAndFree((PVOID*)&pbAikName, cbAikName);
    return hr;
}

HRESULT
GenerateQuote20(
    TBS_HCONTEXT hPlatformTbsHandle,
    UINT32 hPlatformKeyHandle,
    _In_reads_opt_(cbKeyAuth) PBYTE pbKeyAuth,
    UINT32 cbKeyAuth,
    UINT32 pcrMask,
    UINT16 pcrAlgId,
    _In_reads_opt_(cbNonce) PBYTE pbNonce,
    UINT32 cbNonce,
    _Out_writes_to_opt_(cbQuote, *pcbResult) PBYTE pbQuote,
    UINT32 cbQuote,
    _Out_ PUINT32 pcbResult
    )
{
    HRESULT hr = S_OK;
    UINT32 cbRequired = 0;
    BYTE cmd[0x200] = {0};
    BYTE rsp[0x200] = {0};
    UINT32 cursorCmd = 0;
    UINT32 cursorRsp = 0;
    UINT32 cursorPcrProfile = 0;
    UINT32 cbRsp = sizeof(rsp);
    BYTE pcrProfile[] = {0x00, 0x00, 0x00, 0x01, // count = 1
                         0x00, 0x04,             // TPM_ALG_SHA
                         0x03,                   // sizeOfSelect
                         0x7f, 0x7f, 0x00};      // Default platform PCRs mask
    UINT32 responseSize = 0;
    UINT32 returnCode = 0;
    PBYTE pbQuoteTpm = NULL;
    UINT16 cbQuoteTpm = 0;
    UINT16 sigAlg = 0;
    UINT16 sigHashAlg = 0;
    PBYTE pbSignature = NULL;
    UINT16 cbSignature = 0;

    // Check the parameters
    if(pcbResult == NULL)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // overwrite hashing algorithm
    cursorPcrProfile = 0x0004;
    if (FAILED(hr = WriteBigEndian(pcrProfile, sizeof(pcrProfile), &cursorPcrProfile, (UINT16)pcrAlgId)))
    {
        goto Cleanup;
    }

    // Fill in PCRmask
    pcrProfile[0x0007] = (BYTE)((pcrMask & 0x000000ff));
    pcrProfile[0x0008] = (BYTE)(((pcrMask & 0x0000ff00) >> 8));
    pcrProfile[0x0009] = (BYTE)(((pcrMask & 0x00ff0000) >> 16));

    // Build Quote2 command buffer
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)0x8002))) //TPM_ST_SESSIONS
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)0x00000000))) //paramSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)0x00000158))) //TPM_CC_Quote
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, hPlatformKeyHandle))) //keyHandle
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)(sizeof(UINT32) + // authHandle
                                                                         sizeof(UINT16) + // nonceNULL
                                                                         sizeof(BYTE) +   // sessionAttributes
                                                                         sizeof(UINT16) + // passwordSize
                                                                         cbKeyAuth))))     // authorizationSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)0x40000009))) //TPM_RS_PW
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)0x0000))) //nonceNULL
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (BYTE)0x00))) //sessionAttributes
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)cbKeyAuth))) //passwordSize
    {
        goto Cleanup;
    }
    if(cbKeyAuth != 0)
    {
        if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, pbKeyAuth, cbKeyAuth))) //password
        {
            goto Cleanup;
        }
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)cbNonce))) //qualifyingDataSize
    {
        goto Cleanup;
    }
    if(cbNonce != 0)
    {
        if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, pbNonce, cbNonce))) //qualifyingData
        {
            goto Cleanup;
        }
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)0x0010))) //TPM_ALG_NULL
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, pcrProfile, sizeof(pcrProfile)))) //PCRselect 
    {
        goto Cleanup;
    }

    // Set the command size
    ENDIANSWAP_UINT32TOARRAY(cursorCmd, cmd, 0x0002); // Location of paramSize

    // Send the command to the TPM
    if(FAILED(hr = Tbsip_Submit_Command(hPlatformTbsHandle,
                                        TBS_COMMAND_LOCALITY_ZERO,
                                        TBS_COMMAND_PRIORITY_NORMAL,
                                        cmd,
                                        cursorCmd,
                                        rsp,
                                        &cbRsp)))
    {
        goto Cleanup;
    }

    // Parse the response
    if(FAILED(hr = SkipBigEndian(rsp, cbRsp, &cursorRsp, sizeof(UINT16)))) // skip tag
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &responseSize))) // responseSize
    {
        goto Cleanup;
    }
    if(responseSize != cbRsp)
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &returnCode))) // ReturnCode
    {
        goto Cleanup;
    }
    if(returnCode != 0)
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(rsp, cbRsp, &cursorRsp, sizeof(UINT32)))) // paramSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &cbQuoteTpm))) // quotedSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &pbQuoteTpm, cbQuoteTpm))) // quoted
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &sigAlg))) // sigAlg
    {
        goto Cleanup;
    }
    if(sigAlg != 0x0014) //TPM_ALG_RSASSA_PKCS1v1_5
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &sigHashAlg))) // hash == TPM_ALG_SHA
    {
        goto Cleanup;
    }
    if(sigHashAlg != TPM_API_ALG_ID_SHA1) //TPM_ALG_SHA
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &cbSignature))) // signatureSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &pbSignature, cbSignature))) // signatureSize
    {
        goto Cleanup;
    }
    // We ignore the trailing session information in the response - It does not hold any information

    // Calculate Quote output buffer
    cbRequired = cbQuoteTpm +     // quote
                 cbSignature;     // Signature
    if((pbQuote == NULL) || (cbQuote == 0))
    {
        *pcbResult = cbRequired;
        goto Cleanup;
    }
    if(cbQuote < cbRequired)
    {
        hr = HRESULT_FROM_WIN32(ERROR_INSUFFICIENT_BUFFER);
        *pcbResult = cbRequired;
        goto Cleanup;
    }

    // Generate Quote output
    *pcbResult = 0;
    if(FAILED(hr = WriteBigEndian(pbQuote, cbQuote, pcbResult, pbQuoteTpm, cbQuoteTpm)))//Quote
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbQuote, cbQuote, pcbResult, pbSignature, cbSignature)))//signature
    {
        goto Cleanup;
    }

    if(*pcbResult > cbQuote)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

Cleanup:
    return hr;
}

HRESULT
GetPlatformPcrs20(
    TBS_HCONTEXT hPlatformTbsHandle,
    UINT16 pcrAlgId,
    _Out_writes_to_opt_(cbOutput, *pcbResult) PBYTE pbOutput,
    UINT32 cbOutput,
    _Out_ PUINT32 pcbResult
    )
{
    HRESULT hr = S_OK;
    UINT32 cbRequired = 0;
    UINT32 cursorOutput = 0;
    BYTE cmd[0x200] = {0};
    BYTE rsp[0x200] = {0};
    UINT32 cursorCmd = 0;
    UINT32 cursorRsp = 0;
    UINT32 cursorPcrProfile = 0;
    UINT32 cbRsp = sizeof(rsp);
    BYTE pcrProfile[] = {0x00, 0x00, 0x00, 0x01, // count = 1
                         0x00, 0x04,             // TPM_ALG_SHA
                         0x03,                   // sizeofSelect
                         0x00, 0x00, 0x00};      // pcrSelect
    UINT32 responseSize = 0;
    UINT32 returnCode = 0;
    UINT32 pcrCount = 0;

    // Check the parameters
    if(pcbResult == NULL)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    *pcbResult = 0;

    // overwrite hashing algorithm
    cursorPcrProfile = 0x0004;
    if (FAILED(hr = WriteBigEndian(pcrProfile, sizeof(pcrProfile), &cursorPcrProfile, (UINT16)pcrAlgId)))
    {
        goto Cleanup;
    }

    // Calculate output buffer
    cbRequired = AVAILABLE_PLATFORM_PCRS * ((pcrAlgId == TPM_API_ALG_ID_SHA256) ? SHA256_DIGEST_SIZE : SHA1_DIGEST_SIZE);
    if((pbOutput == NULL) || (cbOutput == 0))
    {
        *pcbResult = cbRequired;
        goto Cleanup;
    }
    if(cbOutput < cbRequired)
    {
        hr = HRESULT_FROM_WIN32(ERROR_INSUFFICIENT_BUFFER);
        *pcbResult = cbRequired;
        goto Cleanup;
    }

    // Build ReadPCR command buffer
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)0x8001))) //TPM_NO_ST_SESSIONS
    {
        goto Cleanup;
    }
    UINT32 cursorParamSize = cursorCmd;
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)0x00000000))) //paramSize placeholder
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)0x0000017E))) //TPM_CC_PCR_Read
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, pcrProfile, sizeof(pcrProfile)))) //pcrSelectionIn
    {
        goto Cleanup;
    }
    UINT32 cursorPcrMask = cursorCmd - sizeof(UINT32);
    // Set the correct command size
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorParamSize, cursorCmd))) //correct paramSize
    {
        goto Cleanup;
    }

    for(UINT32 n = 0; n < 3; n++)
    {
        // Set the pcrBlock to receive
        UINT32 cursorPcrMaskIteration = cursorPcrMask;
        if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorPcrMaskIteration, (UINT32)((0x00ff0000 >> (n * 8)) | 0x03000000)))) //pcrSelectionIn
        {
            goto Cleanup;
        }

        // Send the command to the TPM
        cbRsp = sizeof(rsp);
        if(FAILED(hr = Tbsip_Submit_Command(hPlatformTbsHandle,
                                            TBS_COMMAND_LOCALITY_ZERO,
                                            TBS_COMMAND_PRIORITY_NORMAL,
                                            cmd,
                                            cursorCmd,
                                            rsp,
                                            &cbRsp)))
        {
            goto Cleanup;
        }

        // Parse the response
        cursorRsp = 0;
        if(FAILED(hr = SkipBigEndian(rsp, cbRsp, &cursorRsp, sizeof(UINT16)))) // skip tag
        {
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &responseSize))) // responseSize
        {
            goto Cleanup;
        }
        if(responseSize != cbRsp)
        {
            hr = E_FAIL;
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &returnCode))) // ReturnCode
        {
            goto Cleanup;
        }
        if(returnCode != 0)
        {
            hr = E_FAIL;
            goto Cleanup;
        }
        if(FAILED(hr = SkipBigEndian(rsp, cbRsp, &cursorRsp, sizeof(UINT32)))) // skip pcrUpdateCounter
        {
            goto Cleanup;
        }
        if(FAILED(hr = SkipBigEndian(rsp, cbRsp, &cursorRsp, sizeof(pcrProfile)))) // pcrSelectionOut
        {
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &pcrCount))) // count
        {
            goto Cleanup;
        }
        for(UINT32 m = 0; m < pcrCount; m++)
        {
            PBYTE pbDigest = NULL;
            UINT16 cbDigest = 0;

            if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &cbDigest))) // digestSize
            {
                goto Cleanup;
            }
            if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &pbDigest, cbDigest))) // digest
            {
                goto Cleanup;
            }
            if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursorOutput, pbDigest, cbDigest)))
            {
                goto Cleanup;
            }
        }
    }
    *pcbResult = cursorOutput;
    if(*pcbResult > cbOutput)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }


Cleanup:
    return hr;
}

/*
Verify 2.0 specific parts of the Quote, like nonce and PCRlist
*/
HRESULT
ValidateQuoteContext20(
    _In_reads_(cbQuote) PBYTE pbQuote,
    UINT32 cbQuote,
    _In_reads_(cbPcrList) PBYTE pbPcrList,
    UINT32 cbPcrList,
    _In_reads_opt_ (cbNonce) PBYTE pbNonce,
    UINT32 cbNonce,
    UINT16 pcrAlgId,
    _Out_ PUINT32 pPcrMask
    )
{
    HRESULT hr = S_OK;
    UINT32 cursorQuote = 0;
    UINT32 magic = 0;
    UINT16 attestType = 0;
    UINT32 count = 0;
    UINT16 digestAlg = 0;
    UINT32 pcrMaskQuote = 0;
    UINT16 cbPcrDigest = 0;
    PBYTE pbPcrDigest = 0;
    BYTE pcrReferenceBuffer[AVAILABLE_PLATFORM_PCRS * MAX_DIGEST_SIZE] = {0};
    UINT32 cursorPcrReferenceBuffer = 0;
    UINT32 cbPcrReferenceDigest = 0;
    BYTE pcrReferenceDigest[MAX_DIGEST_SIZE] = {0};
    UINT32 digestSize = (pcrAlgId == TPM_API_ALG_ID_SHA256) ? SHA256_DIGEST_SIZE : SHA1_DIGEST_SIZE;

    // Check the parameters
    if((pbQuote == NULL) || (cbQuote == 0) ||
       (pbPcrList == NULL) || (cbPcrList == 0) ||
       ((cbPcrList % digestSize) != 0) ||
       (pPcrMask == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Open up the TPMS_ATTEST structure:
    if(FAILED(hr = ReadBigEndian(pbQuote, cbQuote, &cursorQuote, &magic))) // magic
    {
        goto Cleanup;
    }
    if(magic != 0xFF544347) //TPM_GENERATED_VALUE
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbQuote, cbQuote, &cursorQuote, &attestType))) // type
    {
        goto Cleanup;
    }
    if(attestType != 0x8018) //TPM_ST_ATTEST_QUOTE
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian2B(pbQuote, cbQuote, &cursorQuote))) // qualifiedSigner
    {
        goto Cleanup;
    }
    if((pbNonce != NULL) && (cbNonce == SHA1_DIGEST_SIZE))
    {
        // Validate the nonce
        UINT16 cbExtraData = 0;
        PBYTE pbExtraData = 0;
        if(FAILED(hr = ReadBigEndian2B(pbQuote, cbQuote, &cursorQuote, &cbExtraData, &pbExtraData))) // extraData
        {
            goto Cleanup;
        }
        if((cbExtraData != cbNonce) || (memcmp(pbExtraData, pbNonce, cbExtraData) != 0))
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }
    }
    else
    {
        // Ignore the nonce
        if(FAILED(hr = SkipBigEndian2B(pbQuote, cbQuote, &cursorQuote))) // extraData
        {
            goto Cleanup;
        }
    }
    if(FAILED(hr = SkipBigEndian(pbQuote, cbQuote, &cursorQuote, (sizeof(UINT64) +
                                                                  sizeof(BYTE) +
                                                                  sizeof(UINT32) +
                                                                  sizeof(UINT32))))) // TPMS_CLOCK_INFO
    {
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(pbQuote, cbQuote, &cursorQuote, sizeof(UINT64)))) // firmwareVersion
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbQuote, cbQuote, &cursorQuote, &count))) // count
    {
        goto Cleanup;
    }
    if(count != 0x00000001) //Only one set
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    // digestAlg is the algorithm ID of the PCR bank used as input for the Quote operation.
    // The algorithm ID of the hashing operation used to generate the digest of the PCRs is
    // not specified in the quote. It is determined by the key used to generate the quote.
    if(FAILED(hr = ReadBigEndian(pbQuote, cbQuote, &cursorQuote, &digestAlg))) // hashAlg
    {
        goto Cleanup;
    }
    // because the digestAlg is the algorithm ID of the PCR bank used as input into the
    // quote operation, it has to match the algorithm of the PCRs passed in for validation.
    if(digestAlg != pcrAlgId)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbQuote, cbQuote, &cursorQuote, &pcrMaskQuote))) // pcrMask
    {
        goto Cleanup;
    }
    if((pcrMaskQuote & 0xff000000) != 0x03000000)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    pcrMaskQuote = ENDIANSWAPUINT32((pcrMaskQuote << 8) & 0xffffff00);
    if(FAILED(hr = ReadBigEndian2B(pbQuote, cbQuote, &cursorQuote, &cbPcrDigest, &pbPcrDigest))) // PcrDigest
    {
        goto Cleanup;
    }

    // Ensure that we consumed the entire quote
    if(cbQuote != cursorQuote)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Validate the PCRlist against the Quote
    // Read the pcr select indicated PCR values from the list and copy them into the reference buffer
    for(UINT32 n = 0; n < AVAILABLE_PLATFORM_PCRS; n++)
    {
        // Is PCR[n] selected?
        if((pcrMaskQuote & (0x00000001 << n)) != 0)
        {
            // Copy to reference buffer
            if(FAILED(hr = WriteBigEndian(pcrReferenceBuffer, sizeof(pcrReferenceBuffer), 
                                          &cursorPcrReferenceBuffer, &pbPcrList[n * digestSize], digestSize)))
            {
                goto Cleanup;
            }
        }
    }
    // Calculate the pcrDigest. This digest depends on the key used to generate the quote, which is 
    // not given anywhere. Guess that 32 byte digest means SHA256.
    if(FAILED(hr = TpmAttiShaHash(((cbPcrDigest == SHA256_DIGEST_SIZE) ? BCRYPT_SHA256_ALGORITHM : BCRYPT_SHA1_ALGORITHM),
                                  NULL,
                                  0,
                                  pcrReferenceBuffer,
                                  cursorPcrReferenceBuffer,
                                  pcrReferenceDigest,
                                  cbPcrDigest,
                                  &cbPcrReferenceDigest)))
    {
        goto Cleanup;
    }
    // Compare the PCR digests
    if((cbPcrDigest != cbPcrReferenceDigest) ||
       (memcmp(pbPcrDigest, pcrReferenceDigest, cbPcrDigest) != 0))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Return the included PCRMask
    *pPcrMask = pcrMaskQuote;

Cleanup:
    return hr;
}

HRESULT
CertifyKey20(
    TBS_HCONTEXT hPlatformTbsHandle,
    UINT32 hPlatformAikHandle,
    _In_reads_opt_(cbAikUsageAuth) PBYTE pbAikUsageAuth,
    UINT32 cbAikUsageAuth,
    UINT32 hPlatformKeyHandle,
    _In_reads_opt_(cbKeyUsageAuth) PBYTE pbKeyUsageAuth,
    UINT32 cbKeyUsageAuth,
    _In_reads_opt_(cbNonce) PBYTE pbNonce,
    UINT32 cbNonce,
    _Out_writes_to_opt_(cbOutput, *pcbResult) PBYTE pbOutput,
    UINT32 cbOutput,
    _Out_ PUINT32 pcbResult
    )
{
    HRESULT hr = S_OK;
    UINT32 cbRequired = 0;
    BYTE cmd[0x200] = {0};
    BYTE rsp[0x200] = {0};
    UINT32 cursorCmd = 0;
    UINT32 cursorRsp = 0;
    UINT32 cbRsp = sizeof(rsp);
    UINT32 responseSize = 0;
    UINT32 returnCode = 0;
    PBYTE pbCertify = NULL;
    UINT16 cbCertify = 0;
    UINT16 sigAlg = 0;
    UINT16 sigHashAlg = 0;
    PBYTE pbSignature = NULL;
    UINT16 cbSignature = 0;

    // Check the parameters
    if(pcbResult == NULL)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Build Certify command buffer
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)0x8002))) //TPM_ST_SESSIONS
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)0x00000000))) //paramSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)0x00000148))) //TPM_CC_Certify
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, hPlatformKeyHandle))) //keyHandle
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, hPlatformAikHandle))) //aikHandle
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)(2 * sizeof(UINT32) + // authHandle
                                                                         2 * sizeof(UINT16) + // nonceNULL
                                                                         2 * sizeof(BYTE) +   // sessionAttributes
                                                                         2 * sizeof(UINT16) + // passwordSize
                                                                         cbKeyUsageAuth +     // authorizationSize
                                                                         cbAikUsageAuth))))   // authorizationSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)0x40000009))) //TPM_RS_PW
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)0x0000))) //nonceNULL
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (BYTE)0x00))) //sessionAttributes
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)cbKeyUsageAuth))) //passwordSize
    {
        goto Cleanup;
    }
    if(cbKeyUsageAuth != 0)
    {
        if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, pbKeyUsageAuth, cbKeyUsageAuth))) //password
        {
            goto Cleanup;
        }
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)0x40000009))) //TPM_RS_PW
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)0x0000))) //nonceNULL
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (BYTE)0x00))) //sessionAttributes
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)cbAikUsageAuth))) //passwordSize
    {
        goto Cleanup;
    }
    if(cbAikUsageAuth != 0)
    {
        if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, pbAikUsageAuth, cbAikUsageAuth))) //password
        {
            goto Cleanup;
        }
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)cbNonce))) //qualifyingDataSize
    {
        goto Cleanup;
    }
    if(cbNonce != 0)
    {
        if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, pbNonce, cbNonce))) //qualifyingData
        {
            goto Cleanup;
        }
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)0x0010))) //TPM_ALG_NULL
    {
        goto Cleanup;
    }

    // Set the command size
    ENDIANSWAP_UINT32TOARRAY(cursorCmd, cmd, 0x0002); // Location of paramSize

    // Send the command to the TPM
    if(FAILED(hr = Tbsip_Submit_Command(hPlatformTbsHandle,
                                        TBS_COMMAND_LOCALITY_ZERO,
                                        TBS_COMMAND_PRIORITY_NORMAL,
                                        cmd,
                                        cursorCmd,
                                        rsp,
                                        &cbRsp)))
    {
        goto Cleanup;
    }

    // Parse the response
    if(FAILED(hr = SkipBigEndian(rsp, cbRsp, &cursorRsp, sizeof(UINT16)))) // skip tag
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &responseSize))) // responseSize
    {
        goto Cleanup;
    }
    if(responseSize != cbRsp)
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &returnCode))) // ReturnCode
    {
        goto Cleanup;
    }
    if(returnCode != 0)
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(rsp, cbRsp, &cursorRsp, sizeof(UINT32)))) // paramSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &cbCertify))) // certifyInfoSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &pbCertify, cbCertify))) // certifyInfo
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &sigAlg))) // sigAlg
    {
        goto Cleanup;
    }
    if(sigAlg != 0x0014) //TPM_ALG_RSASSA_PKCS1v1_5
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &sigHashAlg))) // hash == TPM_ALG_SHA
    {
        goto Cleanup;
    }
    if(sigHashAlg != TPM_API_ALG_ID_SHA1) //TPM_ALG_SHA
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &cbSignature))) // signatureSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &pbSignature, cbSignature))) // signatureSize
    {
        goto Cleanup;
    }
    // We ignore the trailing session information in the response - It does not hold any information

    // Calculate Quote output buffer
    cbRequired = cbCertify +      // Certify
                 cbSignature;     // Signature
    if((pbOutput == NULL) || (cbOutput == 0))
    {
        *pcbResult = cbRequired;
        goto Cleanup;
    }
    if(cbOutput < cbRequired)
    {
        hr = HRESULT_FROM_WIN32(ERROR_INSUFFICIENT_BUFFER);
        *pcbResult = cbRequired;
        goto Cleanup;
    }

    // Generate Quote output
    *pcbResult = 0;
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, pcbResult, pbCertify, cbCertify))) // Certify
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, pcbResult, pbSignature, cbSignature))) // Signature
    {
        goto Cleanup;
    }
    if(cbOutput < *pcbResult)
    {
        hr = E_FAIL;
        goto Cleanup;
    }

Cleanup:
    return hr;
}

HRESULT
ValidateKeyAttest20(
    _In_reads_(cbKeyAttest) PBYTE pbKeyAttest,
    UINT32 cbKeyAttest,
    _In_reads_opt_(cbNonce) PBYTE pbNonce,
    UINT32 cbNonce,
    _In_reads_(cbKeyAttest) PBYTE pbKeyBlob,
    UINT32 cbKeyBlob,
    UINT32 pcrMask,
    UINT16 pcrAlgId,
    _In_reads_opt_(AVAILABLE_PLATFORM_PCRS * MAX_DIGEST_SIZE) PBYTE pcrTable
    )
{
    HRESULT hr = E_FAIL;
    UINT32 cbRequired = 0;
    UINT32 attestCursor = 0;
    UINT32 keyCursor = 0;
    UINT32 magic = 0;
    UINT16 attestType = 0;
    PBYTE pbAttestNonce = NULL;
    UINT16 cbAttestNonce = 0;
    PBYTE pbKeyName = NULL;
    UINT16 cbKeyName = 0;
    BYTE keyNameReference[SHA256_DIGEST_SIZE + sizeof(UINT16)] = {0};
    UINT32 PolicyDigestCount = 0;
    PBYTE pbPolicyDigest = NULL;
    UINT16 cbPolicyDigest = 0;
    BYTE userPolicyDigestReference[SHA256_DIGEST_SIZE] = {0};
    BYTE policyDigestReference[SHA256_DIGEST_SIZE] = {0};
    PBYTE pbKeyAuthPolicy = NULL;
    UINT16 cbKeyAuthPolicy = 0;
    UINT32 cbPolicyOrDigestBuffer = 0;

    // Check parameters
    PPCP_KEY_BLOB_WIN8 pW8Key = (PPCP_KEY_BLOB_WIN8)pbKeyBlob;
    if((pbKeyAttest == NULL) ||
       (cbKeyAttest == 0) ||
       (pW8Key == NULL) ||
       (cbKeyBlob < sizeof(PCP_KEY_BLOB_WIN8)) ||
       (pW8Key->magic != BCRYPT_PCP_KEY_MAGIC) ||
       (pW8Key->cbHeader < sizeof(PCP_KEY_BLOB_WIN8)) ||
       (pW8Key->pcpType != PCPTYPE_TPM20) ||
       (cbKeyBlob < pW8Key->cbHeader +
                    pW8Key->cbPublic +
                    pW8Key->cbPrivate +
                    pW8Key->cbMigrationPublic +
                    pW8Key->cbMigrationPrivate +
                    pW8Key->cbPolicyDigestList +
                    pW8Key->cbPCRBinding +
                    pW8Key->cbPCRDigest +
                    pW8Key->cbEncryptedSecret +
                    pW8Key->cbTpm12HostageBlob))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Parse and validate the attestation
    if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &magic))) // magic
    {
        goto Cleanup;
    }
    if(magic != 0xff544347) //TPM_GENERATED_VALUE
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &attestType))) // type
    {
        goto Cleanup;
    }
    if(attestType != 0x8017) //TPM_ST_ATTEST_CERTIFY
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian2B(pbKeyAttest, cbKeyAttest, &attestCursor))) // qualifiedSigner
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian2B(pbKeyAttest, cbKeyAttest, &attestCursor, &cbAttestNonce, &pbAttestNonce))) // extraData
    {
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, sizeof(UINT64) +
                                                                          sizeof(BYTE) +
                                                                          sizeof(UINT32) +
                                                                          sizeof(UINT32)))) // TPMS_CLOCK_INFO
    {
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, sizeof(UINT64)))) // firmwareVersion
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian2B(pbKeyAttest, cbKeyAttest, &attestCursor, &cbKeyName, &pbKeyName))) // name
    {
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian2B(pbKeyAttest, cbKeyAttest, &attestCursor))) // qualifiedName
    {
        goto Cleanup;
    }
    // Ensure that there is no trailing data that has been signed
    if(attestCursor != cbKeyAttest)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Get Name from key blob
    if(FAILED(hr = GetNameFromPublic(&pbKeyBlob[pW8Key->cbHeader + sizeof(UINT16)],
                                     pW8Key->cbPublic - sizeof(UINT16),
                                     NULL,
                                     keyNameReference,
                                     sizeof(keyNameReference),
                                     &cbRequired)))
    {
        goto Cleanup;
    }

    // Step 1: Validate key name
    if((sizeof(keyNameReference) != cbKeyName) ||
       (memcmp(pbKeyName, keyNameReference, cbKeyName) != 0))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Step 2: Check the nonce if requested
    if((pbNonce != NULL) && (cbNonce != 0) &&
       ((cbNonce != cbAttestNonce) || ((memcmp(pbNonce, pbAttestNonce, cbNonce)) != 0)))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    /*
    We are only going to recognize keys that have been created by the provider with the well known policy model:

    TPM2_PolicyOR(
        // USER policy
        {
            TPM2_PolicyAuthValue(usageAuth)
            TPM2_PolicyPCR(TPML_PCR_SELECTION) // omitted if pcrs = {00000000}
        },
        // Explicit ADMIN policies - only needed when PCRBound but always present
        {
            TPM2_PolicyCommandCode(TPM_CC_ObjectChangeAuth),
            TPM2_PolicyAuthValue(usageAuth)
        },
        {
            // Legacy Auth Policy for TPC_CC_Certify for Windows 8.1
            TPM2_PolicyCommandCode(TPM_CC_Certify),
            TPM2_PolicyAuthValue(usageAuth)
        },
        {
            TPM2_PolicyCommandCode(TPM_CC_ActivateCredential),
            TPM2_PolicyAuthValue(usageAuth)
        },
        {
            // DUPLICATION policy - only present when exportable
            TPM2_PolicyCommandCode(TPMW2_CC_Duplicate) 
            TPM2_PolicySecret(migrationIdentity)
        },
        {
            // Auth Policy for TPM_CC_Certify for Windows 10
            TPM2_PolicyCommandCode(TPM_CC_Certify)
        }
        // REMINDER: the eighth policy in this TPM2_PolicyOR tree needs to be another TPM2_PolicyOR
        // See the TCG spec details on TPM2_PolicyOR for more information.
    )
    */

    // Read the policy digest list
    keyCursor = pW8Key->cbHeader +
                pW8Key->cbPublic +
                pW8Key->cbPrivate +
                pW8Key->cbMigrationPublic +
                pW8Key->cbMigrationPrivate;
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &PolicyDigestCount)))
    {
        goto Cleanup;
    }

    // Only non-exportable keys may be attested so there have to be exactly 4 or 6 policy digests
    // Keys created in Windows 8.1 will have 4 digests
    // Keys created in Windows 10 will have 6, with a zero-length digest in slot 5
    if((PolicyDigestCount != 0x00000004) && 
        (PolicyDigestCount != 0x00000006))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Calculate the user policy with the PCR data, if provided
    if((pcrTable != NULL) && (pcrMask != 0))
    {
        UINT32 keyBlobPcrMaskCursor = 0;
        UINT32 keyBlobPcrMask = 0;
        UINT32 cbKeyBlobPcrDigest = 0;
        PBYTE pbKeyBlobPcrDigest = NULL;
        BYTE pcrComposite[AVAILABLE_PLATFORM_PCRS * MAX_DIGEST_SIZE] = {0};
        UINT32 compositeCursor = 0;
        BYTE pcrCompositeDigestReference[MAX_DIGEST_SIZE] = {0};
        UINT32 digestSize = (pcrAlgId == TPM_API_ALG_ID_SHA256) ? SHA256_DIGEST_SIZE : SHA1_DIGEST_SIZE;

        // Get pcr data from key blob
        keyCursor = pW8Key->cbHeader +
                    pW8Key->cbPublic +
                    pW8Key->cbPrivate +
                    pW8Key->cbMigrationPublic +
                    pW8Key->cbMigrationPrivate +
                    pW8Key->cbPolicyDigestList;
        keyBlobPcrMaskCursor = keyCursor;
        keyBlobPcrMask = (pbKeyBlob[keyCursor]) |
                         (pbKeyBlob[keyCursor + 1] << 8) |
                         (pbKeyBlob[keyCursor + 2] << 16);
        keyCursor += 3;
        pbKeyBlobPcrDigest = &pbKeyBlob[keyCursor];
        cbKeyBlobPcrDigest = pW8Key->cbPCRDigest;

        // check that PCR algorithm matches
        if ((pW8Key->cbHeader >= sizeof(PCP_20_KEY_BLOB)) &&
            (pcrAlgId != ((PPCP_20_KEY_BLOB)pW8Key)->pcrAlgId))
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }

        // Write all PCRs in the composite that are in the mask
        for(UINT32 n = 0; n < 24; n++)
        {
            if((pcrMask & (0x00000001 << n)) != 0)
            {
                if(FAILED(hr = WriteBigEndian(pcrComposite, sizeof(pcrComposite), &compositeCursor, &pcrTable[n * digestSize], digestSize)))
                {
                    goto Cleanup;
                }
            }
        }

        // Calculate the composite digest
        if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA256_ALGORITHM, // determined by policy not by algorithm of PCR bank
                                      NULL,
                                      0,
                                      pcrComposite,
                                      compositeCursor,
                                      pcrCompositeDigestReference,
                                      SHA256_DIGEST_SIZE,
                                      &cbRequired)))
        {
            goto Cleanup;
        }

        // Check pcr mask and digest with data in the key blob
        if((pcrMask != keyBlobPcrMask) ||
            (cbKeyBlobPcrDigest != SHA256_DIGEST_SIZE) ||
            (memcmp(pbKeyBlobPcrDigest, pcrCompositeDigestReference, SHA256_DIGEST_SIZE) != 0))
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }

        // Calculate the user policy with the PCRs
        BYTE policyDigestBuffer[SHA256_DIGEST_SIZE + // policyHash old 
                                sizeof(UINT32) +     // TPM_CC_PolicyPCR 
                                sizeof(UINT32) +     // TPML_PCR_SELECTION.count
                                sizeof(UINT16) +     // TPML_PCR_SELECTION.TPMS_PCR_SELECTION.hash
                                sizeof(BYTE) +       // TPML_PCR_SELECTION.TPMS_PCR_SELECTION.sizeofSelect
                                3 +                  // pcrSelect
                                SHA256_DIGEST_SIZE] = {0}; // pcrDigest
        UINT32 policyDigestBufferCursor = 0;

        if(FAILED(hr = WriteBigEndian(policyDigestBuffer, sizeof(policyDigestBuffer), &policyDigestBufferCursor, (PBYTE)defaultUserPolicy, sizeof(defaultUserPolicy)))) // Default user policy digest
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(policyDigestBuffer, sizeof(policyDigestBuffer), &policyDigestBufferCursor, (UINT32)0x0000017F))) // TPM_CC_PolicyPCR
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(policyDigestBuffer, sizeof(policyDigestBuffer), &policyDigestBufferCursor, (UINT32)0x00000001))) // TPML_PCR_SELECTION.count
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(policyDigestBuffer, sizeof(policyDigestBuffer), &policyDigestBufferCursor, pcrAlgId))) // TPML_PCR_SELECTION.TPMS_PCR_SELECTION.hash = TPM_ALG_SHA
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(policyDigestBuffer, sizeof(policyDigestBuffer), &policyDigestBufferCursor, (BYTE)0x03))) // TPML_PCR_SELECTION.TPMS_PCR_SELECTION.sizeofSelect
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(policyDigestBuffer, sizeof(policyDigestBuffer), &policyDigestBufferCursor, &pbKeyBlob[keyBlobPcrMaskCursor], 3))) // TPML_PCR_SELECTION.TPMS_PCR_SELECTION.Select
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(policyDigestBuffer, sizeof(policyDigestBuffer), &policyDigestBufferCursor, pbKeyBlobPcrDigest, cbKeyBlobPcrDigest))) // digest
        {
            goto Cleanup;
        }
        if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA256_ALGORITHM, // determined by policy, not by algorithm of PCR bank
                                      NULL,
                                      0,
                                      policyDigestBuffer,
                                      sizeof(policyDigestBuffer),
                                      userPolicyDigestReference,
                                      sizeof(userPolicyDigestReference),
                                      &cbRequired)))
        {
            goto Cleanup;
        }
    }
    else
    {
        // The caller does not want to verify the PCR information
        // Pick the correct user policy reference digest based on the flags that tell us if this key is PCR bound or not
        keyCursor = pW8Key->cbHeader;
        if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, sizeof(UINT16) +  // size
                                                                       sizeof(UINT16) +  // keytype
                                                                       sizeof(UINT16)))) // nameAlg
        {
            goto Cleanup;
        }
        UINT32 keyAttributes = 0;
        if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &keyAttributes)))
        {
            goto Cleanup;
        }

        if(keyAttributes & 0x00000040) //userWithAuth
        {
            // Key not bound to PCRs so we proceed with the default policy
            if(memcpy_s(userPolicyDigestReference,
                        sizeof(userPolicyDigestReference),
                        defaultUserPolicy,
                        sizeof(defaultUserPolicy)))
            {
                hr = E_FAIL;
                goto Cleanup;
            }

        }
        else
        {
            // Key is bound to PCRs, but the user has not asked to validate them
            // We accept the user policy digest value that is stored in the key
            keyCursor = pW8Key->cbHeader +
                        pW8Key->cbPublic +
                        pW8Key->cbPrivate +
                        pW8Key->cbMigrationPublic +
                        pW8Key->cbMigrationPrivate +
                        sizeof(UINT32);
            if(FAILED(hr = ReadBigEndian2B(pbKeyBlob, cbKeyBlob, &keyCursor, &cbPolicyDigest, &pbPolicyDigest)))
            {
                goto Cleanup;
            }
            if(memcpy_s(userPolicyDigestReference, sizeof(userPolicyDigestReference), pbPolicyDigest, cbPolicyDigest))
            {
                goto Cleanup;
            }
        }
    }

    // Step 3: Check the policy digests of each individual branch

    // Read and verify the user policy digest
    keyCursor = pW8Key->cbHeader +
                pW8Key->cbPublic +
                pW8Key->cbPrivate +
                pW8Key->cbMigrationPublic +
                pW8Key->cbMigrationPrivate +
                sizeof(UINT32);

    // Read and compare the user policy
    if(FAILED(hr = ReadBigEndian2B(pbKeyBlob, cbKeyBlob, &keyCursor, &cbPolicyDigest, &pbPolicyDigest)))
    {
        goto Cleanup;
    }
    if((sizeof(userPolicyDigestReference) != cbPolicyDigest) ||
       (memcmp(userPolicyDigestReference, pbPolicyDigest, cbPolicyDigest) != 0))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Read and compare the admin policy for ObjectChangeAuth
    if(FAILED(hr = ReadBigEndian2B(pbKeyBlob, cbKeyBlob, &keyCursor, &cbPolicyDigest, &pbPolicyDigest)))
    {
        goto Cleanup;
    }
    if((sizeof(adminObjectChangeAuthPolicy) != cbPolicyDigest) ||
        (memcmp(adminObjectChangeAuthPolicy, pbPolicyDigest, cbPolicyDigest) != 0))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Read and compare the admin policy for Certify
    if(FAILED(hr = ReadBigEndian2B(pbKeyBlob, cbKeyBlob, &keyCursor, &cbPolicyDigest, &pbPolicyDigest)))
    {
        goto Cleanup;
    }
    if((sizeof(adminCertifyPolicy) != cbPolicyDigest) ||
        (memcmp(adminCertifyPolicy, pbPolicyDigest, cbPolicyDigest) != 0))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Read and compare the admin policy for Certify
    if(FAILED(hr = ReadBigEndian2B(pbKeyBlob, cbKeyBlob, &keyCursor, &cbPolicyDigest, &pbPolicyDigest)))
    {
        goto Cleanup;
    }
    if((sizeof(adminActivateCredentialPolicy) != cbPolicyDigest) ||
        (memcmp(adminActivateCredentialPolicy, pbPolicyDigest, cbPolicyDigest) != 0))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    if (PolicyDigestCount > 4)
    {
        // Windows 10 attestable key policies
        // Read and verify the empty policy for Duplicate
        if(FAILED(hr = ReadBigEndian2B(pbKeyBlob, cbKeyBlob, &keyCursor, &cbPolicyDigest, &pbPolicyDigest)))
        {
            goto Cleanup;
        }

        if(0 != cbPolicyDigest)
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }

        // Read and compare the admin policy for Certify (no PIN)
        if(FAILED(hr = ReadBigEndian2B(pbKeyBlob, cbKeyBlob, &keyCursor, &cbPolicyDigest, &pbPolicyDigest)))
        {
            goto Cleanup;
        }

        if((sizeof(adminCertifyPolicyNoPin) != cbPolicyDigest) ||
            (memcmp(adminCertifyPolicyNoPin, pbPolicyDigest, cbPolicyDigest) != 0))
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }
    }

    // Step 4: Calculate the entire policy digest and verify with the digest in the key
    BYTE policyOrDigestBuffer[SHA256_DIGEST_SIZE +              // policyHash old 
                              sizeof(UINT32) +                  // TPM_CC_PolicyOR 
                              5 * SHA256_DIGEST_SIZE] = {0};    // 4 or 5 policyDigests (duplicate policy is zero-length)
    cbPolicyOrDigestBuffer = sizeof(policyOrDigestBuffer);
    if (PolicyDigestCount == 4)
    {
        // Decrease the size of the data to hash by the size of the missing additional policy digest (6. Certify w/o PIN)
        cbPolicyOrDigestBuffer -= SHA256_DIGEST_SIZE;
    }
    UINT32 policyOrDigestBufferCursor = SHA256_DIGEST_SIZE;
    if(FAILED(hr = WriteBigEndian(policyOrDigestBuffer, cbPolicyOrDigestBuffer, &policyOrDigestBufferCursor, (UINT32)0x00000171))) // TPM_CC_PolicyOR
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(policyOrDigestBuffer, cbPolicyOrDigestBuffer, &policyOrDigestBufferCursor, userPolicyDigestReference, sizeof(userPolicyDigestReference))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(policyOrDigestBuffer, cbPolicyOrDigestBuffer, &policyOrDigestBufferCursor, (PBYTE)adminObjectChangeAuthPolicy, sizeof(adminObjectChangeAuthPolicy))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(policyOrDigestBuffer, cbPolicyOrDigestBuffer, &policyOrDigestBufferCursor, (PBYTE)adminCertifyPolicy, sizeof(adminCertifyPolicy))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(policyOrDigestBuffer, cbPolicyOrDigestBuffer, &policyOrDigestBufferCursor, (PBYTE)adminActivateCredentialPolicy, sizeof(adminActivateCredentialPolicy))))
    {
        goto Cleanup;
    }

    if (PolicyDigestCount > 4)
    {
        // Add in the hash of the Certify without PIN policy
        if(FAILED(hr = WriteBigEndian(policyOrDigestBuffer, cbPolicyOrDigestBuffer, &policyOrDigestBufferCursor, (PBYTE)adminCertifyPolicyNoPin, sizeof(adminCertifyPolicyNoPin))))
        {
            goto Cleanup;
        }
    }

    if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA256_ALGORITHM,
                                  NULL,
                                  0,
                                  policyOrDigestBuffer,
                                  cbPolicyOrDigestBuffer,
                                  policyDigestReference,
                                  sizeof(policyDigestReference),
                                  &cbRequired)))
    {
        goto Cleanup;
    }

    keyCursor = pW8Key->cbHeader +
                sizeof(UINT16) + //keysize
                sizeof(UINT16) + //type
                sizeof(UINT16) + //nameAlg
                sizeof(UINT32);  //TPMA_OBJECT
    if(FAILED(hr = ReadBigEndian2B(pbKeyBlob, cbKeyBlob, &keyCursor, &cbKeyAuthPolicy, &pbKeyAuthPolicy)))
    {
        goto Cleanup;
    }
    if((cbKeyAuthPolicy != sizeof(policyDigestReference)) ||
        (memcmp(pbKeyAuthPolicy, policyDigestReference, cbKeyAuthPolicy)) != 0)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // This key checks out!
    hr = S_OK;

Cleanup:
    return hr;
}

HRESULT
GetKeyProperties20(
    _In_reads_(cbKeyBlob) PBYTE pbKeyBlob,
    UINT32 cbKeyBlob,
    _Out_opt_ PUINT32 pPropertyFlags
    )
{
    HRESULT hr = S_OK;
    PPCP_KEY_BLOB_WIN8 pW8Key = (PPCP_KEY_BLOB_WIN8)pbKeyBlob;
    PBYTE pbPublicKey = NULL;
    UINT32 cbPublicKey = 0;
    UINT32 keyCursor = 0;
    UINT16 keyAlg = 0;
    UINT32 objectAttributes = 0;

    if((pPropertyFlags == NULL) ||
       (pW8Key == NULL) ||
       (cbKeyBlob < sizeof(PCP_KEY_BLOB_WIN8)) ||
       (pW8Key->magic != BCRYPT_PCP_KEY_MAGIC) ||
       (pW8Key->cbHeader < sizeof(PCP_KEY_BLOB_WIN8)) ||
       (pW8Key->pcpType != PCPTYPE_TPM20) ||
       (cbKeyBlob < pW8Key->cbHeader +
                    pW8Key->cbPublic +
                    pW8Key->cbPrivate +
                    pW8Key->cbMigrationPublic +
                    pW8Key->cbMigrationPrivate +
                    pW8Key->cbPolicyDigestList +
                    pW8Key->cbPCRBinding +
                    pW8Key->cbPCRDigest +
                    pW8Key->cbEncryptedSecret +
                    pW8Key->cbTpm12HostageBlob))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    *pPropertyFlags = 0;
    pbPublicKey = &pbKeyBlob[pW8Key->cbHeader + sizeof(UINT16)];
    cbPublicKey = pW8Key->cbPublic - sizeof(UINT16);

    if(FAILED(hr = ReadBigEndian(pbPublicKey, cbPublicKey, &keyCursor, &keyAlg))) // keytype
    {
        goto Cleanup;
    }
    if(keyAlg != 0x0001) // TPM_ALG_RSA
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(pbPublicKey, cbPublicKey, &keyCursor, sizeof(UINT16)))) // nameAlg
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbPublicKey, cbPublicKey, &keyCursor, &objectAttributes))) // TPMA_OBJECT
    {
        goto Cleanup;
    }

    if(((objectAttributes & 0x00000002) != 0) && //fixedTPM
       ((objectAttributes & 0x00000010) !=0))   //fixedParent
    {
        *pPropertyFlags |= PCP_KEY_PROPERTIES_NON_MIGRATABLE;
    }
    if(((pW8Key->flags & PCP_KEY_FLAGS_WIN8_authRequired) != 0) &&
       ((objectAttributes & 0x00000200) == 0)) //NoDA
    {
        *pPropertyFlags |= PCP_KEY_PROPERTIES_PIN_PROTECTED;
    }
    if((objectAttributes & 0x00000040) == 0) //userWithAuth
    {
        *pPropertyFlags |= PCP_KEY_PROPERTIES_PCR_PROTECTED;
    }
    if((objectAttributes & 0x00010000) != 0) //restricted
    {
        if((objectAttributes & 0x00040000) != 0) //sign
        {
            *pPropertyFlags |= PCP_KEY_PROPERTIES_IDENTITY_KEY;
        }
        else if((objectAttributes & 0x00020000) != 0) //decrypt
        {
            *pPropertyFlags |= PCP_KEY_PROPERTIES_STORAGE_KEY;
        }
        else
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }
    }
    else
    {
        if((objectAttributes & 0x00040000) != 0) //sign
        {
            *pPropertyFlags |= PCP_KEY_PROPERTIES_SIGNATURE_KEY;
        }
        if((objectAttributes & 0x00020000) != 0) //decrypt
        {
            *pPropertyFlags |= PCP_KEY_PROPERTIES_ENCRYPTION_KEY;
        }
    }

Cleanup:
    return hr;
}

HRESULT
WrapPlatformKey20(
    _In_reads_(cbKeyPair) PBYTE pbKeyPair,
    UINT32 cbKeyPair,
    BCRYPT_KEY_HANDLE hStorageKey,
    UINT32 keyUsage,
    _In_reads_opt_(cbUsageAuth) PBYTE pbUsageAuth,
    UINT32 cbUsageAuth,
    UINT32 pcrMask,
    UINT16 pcrAlgId,
    _In_reads_opt_(AVAILABLE_PLATFORM_PCRS * MAX_DIGEST_SIZE) PBYTE pcrTable,
    _Out_writes_to_opt_(cbOutput, *pcbResult) PBYTE pbOutput,
    UINT32 cbOutput,
    _Out_ PUINT32 pcbResult
    )
{
    HRESULT hr = S_OK;
    UINT32 cbRequired = 0;
    BCRYPT_RSAKEY_BLOB* pKeyPair = (BCRYPT_RSAKEY_BLOB*)pbKeyPair;
    PPCP_20_KEY_BLOB pOutKey = (PPCP_20_KEY_BLOB)pbOutput;
    UINT32 cursor = 0;
    BYTE defaultExponent[] = {0x01, 0x00, 0x01};
    BOOLEAN tDefaultExponent = FALSE;
    BOOLEAN tWithUsageAuth = FALSE;
    UINT32 cbPublicKey = 0;
    UINT32 cbPolicyDigestList = 0;
    UINT32 cbPCRBinding = 0;
    UINT32 cbPCRDigest = 0;
    UINT32 cbEncryptedSecret = 0;
    UINT32 cbTpm12HostageBlob = 0;
    UINT32 cbPrivateKey = 0;
    UINT32 cbPcrComposite = 0;
    PBYTE pbPrivateKey = NULL;
    UINT32 privateKeyCursor = 0;
    UINT32 objectAttributes = 0;
    PBYTE pcrComposite = NULL;
    BYTE pcrCompositeDigest[SHA256_DIGEST_SIZE] = {0};
    UINT32 orPolicyCursor = SHA256_DIGEST_SIZE;
    BYTE orPolicy[SHA256_DIGEST_SIZE + sizeof(UINT32) + 4 * SHA256_DIGEST_SIZE] = {0};
    BYTE symmetricKey[16] = {0};

    BCRYPT_ALG_HANDLE hRngAlg = NULL;
    BCRYPT_ALG_HANDLE hAesAlg = NULL;
    BCRYPT_KEY_HANDLE hAesKey = NULL;
    BYTE seed[16] = {0};
    BYTE symKey[16] = {0};
    BYTE aesIv[16] = {0};
    BYTE hmacKey[SHA256_DIGEST_SIZE] = {0};
    BYTE keyName[SHA256_DIGEST_SIZE + sizeof(UINT16)] = {0};
    PSTR szLabel = "DUPLICATE";
    BCRYPT_OAEP_PADDING_INFO paddingInfo = {BCRYPT_SHA256_ALGORITHM,
                                            (PUCHAR)szLabel,
                                            (ULONG)strlen(szLabel) + 1};
    BCRYPT_ALG_HANDLE hDerivation = NULL;
    BCRYPT_KEY_HANDLE hDerivationKey = NULL;
    static const CHAR label[] = "ftpmimportkey"; // what is the purpose of the derived key?
    static const CHAR context[] = "Microsoft.Windows"; // who is deriving the key?
    BCryptBuffer parametersSP800108[]= {
        {
            sizeof(label),
            KDF_LABEL,
            (PBYTE)label,
        },
        {
            sizeof(context),
            KDF_CONTEXT,
            (PBYTE)context,
        },
        {
            sizeof(BCRYPT_SHA256_ALGORITHM),
            KDF_HASH_ALGORITHM,
            BCRYPT_SHA256_ALGORITHM,
        }
    };
    BCryptBufferDesc parameters = {
        BCRYPTBUFFER_VERSION,
        ARRAYSIZE(parametersSP800108),
        parametersSP800108
    };
    UINT32 digestSize = (pcrAlgId == TPM_API_ALG_ID_SHA256) ? SHA256_DIGEST_SIZE : SHA1_DIGEST_SIZE;

    // Parameter validation
    if(((pbKeyPair == NULL) || (cbKeyPair < sizeof(BCRYPT_RSAKEY_BLOB))) ||
       (pKeyPair->Magic != BCRYPT_RSAPRIVATE_MAGIC) ||
       (cbKeyPair != sizeof(BCRYPT_RSAKEY_BLOB) +
                     pKeyPair->cbPublicExp +
                     pKeyPair->cbModulus +
                     pKeyPair->cbPrime1 +
                     pKeyPair->cbPrime2) ||
       ((pKeyPair->BitLength != 2048) && (pKeyPair->BitLength != 1024)) ||
       (hStorageKey == NULL) ||
       (keyUsage == 0) ||
       ((pcrMask != 0) && (pcrTable == NULL)) ||
       (pcbResult == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    *pcbResult = 0;

    // Open the required algorithm providers
    if(FAILED(hr = HRESULT_FROM_NT(BCryptOpenAlgorithmProvider(
                                                &hRngAlg,
                                                BCRYPT_RNG_ALGORITHM,
                                                NULL,
                                                0))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = HRESULT_FROM_NT(BCryptOpenAlgorithmProvider(
                                                &hAesAlg,
                                                BCRYPT_AES_ALGORITHM,
                                                NULL,
                                                0))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = HRESULT_FROM_NT(BCryptOpenAlgorithmProvider(
                                                &hDerivation,
                                                BCRYPT_SP800108_CTR_HMAC_ALGORITHM,
                                                NULL,
                                                0))))
    {
        goto Cleanup;
    }

    // Is this key using the default exponent?
    if((pKeyPair->cbPublicExp == 3) &&
       (memcmp(&pbKeyPair[sizeof(BCRYPT_RSAKEY_BLOB)],
               defaultExponent,
               sizeof(defaultExponent)) == 0))
    {
        tDefaultExponent = TRUE;
    }

    // Is this key using a usageAuth?
    if((cbUsageAuth != 0) || (pbUsageAuth != NULL))
    {
        if((cbUsageAuth != SHA1_DIGEST_SIZE) || ((pbUsageAuth == NULL)))
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }
        tWithUsageAuth = TRUE;
    }

    // calculate the objectAttributes
    objectAttributes = 0;
    if(!tWithUsageAuth)
    {
        objectAttributes |= 0x00000400; // noDA
    }
    if(pcrMask == 0)
    {
        objectAttributes |= 0x00000040; // userWithAuth
    }
    switch(keyUsage & 0x0000ffff)
    {
        case NCRYPT_PCP_SIGNATURE_KEY:
            objectAttributes |= 0x00040000; // sign
            break;
        case NCRYPT_PCP_ENCRYPTION_KEY:
            objectAttributes |= 0x00020000; // decrypt
            break;
        case NCRYPT_PCP_GENERIC_KEY:
            objectAttributes |= 0x00060000; // decrypt + sign
            break;
        case NCRYPT_PCP_STORAGE_KEY:
            objectAttributes |= 0x00030000; // decrypt + restricted
            break;
        default:
            hr = E_INVALIDARG;
            goto Cleanup;
            break;
    }

    // Get the size of the storage key encrypted blob
    if(FAILED(hr = HRESULT_FROM_NT(BCryptGetProperty(
                                        hStorageKey,
                                        BCRYPT_BLOCK_LENGTH,
                                        (PBYTE)&cbEncryptedSecret,
                                        sizeof(cbEncryptedSecret),
                                        (PULONG)&cbRequired,
                                        0))))
    {
        goto Cleanup;
    }
    cbEncryptedSecret += sizeof(UINT16);

    // Calculate the public key structure size
    cbPublicKey = sizeof(UINT16) + // 2B size
                  sizeof(UINT16) + // TPMI_ALG_PUBLIC type
                  sizeof(UINT16) + // TPMI_ALG_HASH nameAlg
                  sizeof(UINT32) + // TPMA_OBJECT objectAttributes
                  sizeof(UINT16) + // TPM2B_DIGEST authPolicy.size
                  SHA256_DIGEST_SIZE + 
                  sizeof(UINT16) + // TPMS_RSA_PARMS.TPMT_SYM_DEF_OBJECT.algorithm
                  sizeof(UINT16) + // TPMS_RSA_PARMS.TPMT_RSA_SIG_SCHEME.scheme
                  sizeof(UINT16) + // TPMS_RSA_PARMS.keyBits
                  sizeof(UINT32) + // TPMS_RSA_PARMS.exponent
                  sizeof(UINT16) + // TPMU_PUBLIC_ID.size
                  pKeyPair->cbModulus;
    if((keyUsage & 0x0000ffff) == NCRYPT_PCP_STORAGE_KEY)
    {
        // Only storage keys have a symmetric key
        cbPublicKey += sizeof(UINT16) + // TPMS_RSA_PARMS.TPMT_SYM_DEF_OBJECT.TPMU_SYM_DEF.keyBits
                       sizeof(UINT16); // TPMS_RSA_PARMS.TPMT_SYM_DEF_OBJECT.TPMU_SYM_DEF.mode
    }

    // Calculate the policy digest list size. We can only have 4 policy digests,
    // because hostage keys are never exportable
    cbPolicyDigestList = sizeof(UINT32) + // count
                         4 * (sizeof(UINT16) + SHA256_DIGEST_SIZE);

    // Is this key bound to a PCR policy?
    if(pcrMask != 0)
    {
        // Fixed sizes for PCR information
        cbPCRBinding = 3 * sizeof(BYTE);
        cbPCRDigest = SHA256_DIGEST_SIZE;
    }

    // Calculate the size of the private key blob. It has the same size as the clear data.
    cbTpm12HostageBlob = sizeof(UINT16) +     // hmac.size
                         SHA256_DIGEST_SIZE + // hmac
                         sizeof(UINT16) +     // TPM2B_SENSITIVE.size
                         sizeof(UINT16) +     // TPMT_SENSITIVE.sensitiveType
                         sizeof(UINT16) +     // authValue.Size
                         sizeof(UINT16) +     // symValue.Size
                         sizeof(UINT16) +     // TPMU_SENSITIVE_COMPOSITE.size
                         pKeyPair->cbPrime1;
    if((keyUsage & 0x0000ffff) == NCRYPT_PCP_STORAGE_KEY)
    {
        // Only storage keys have a symmetric key
        cbTpm12HostageBlob += sizeof(symmetricKey); // symKey
    }
    if(tWithUsageAuth)
    {
        cbTpm12HostageBlob += SHA1_DIGEST_SIZE; // authValue
    }

    // Add everything up to see if the provided memory is sufficient
    cbRequired = sizeof(PCP_20_KEY_BLOB) +
                 cbPublicKey +
                 cbPolicyDigestList +
                 cbPCRBinding +
                 cbPCRDigest +
                 cbEncryptedSecret +
                 sizeof(UINT16) + cbTpm12HostageBlob;

    if((pbOutput == NULL) || (cbOutput == 0))
    {
        *pcbResult = cbRequired;
        goto Cleanup;
    }
    if(cbOutput < cbRequired)
    {
        hr = HRESULT_FROM_WIN32(ERROR_INSUFFICIENT_BUFFER);
        *pcbResult = cbRequired;
        goto Cleanup;
    }

    // Fill out the key header structure
    pOutKey->magic = BCRYPT_PCP_KEY_MAGIC;
    pOutKey->cbHeader = sizeof(PCP_20_KEY_BLOB);
    pOutKey->pcpType = PCPTYPE_TPM20;
    if(tWithUsageAuth)
    {
        pOutKey->flags |= PCP_KEY_FLAGS_WIN8_authRequired;
    }
    else
    {
        pOutKey->flags &= ~PCP_KEY_FLAGS_WIN8_authRequired;
    }
    pOutKey->cbPublic = cbPublicKey;
    pOutKey->cbPrivate = 0; // This is an Import key and therefore does not have a private portion that is encrypted by the parent storage key
    pOutKey->cbMigrationPublic = 0; // Key Hostages are never exportable
    pOutKey->cbMigrationPrivate = 0; // Key Hostages are never exportable
    pOutKey->cbPolicyDigestList = cbPolicyDigestList;
    pOutKey->cbPCRBinding = cbPCRBinding;
    pOutKey->cbPCRDigest = cbPCRDigest;
    pOutKey->cbEncryptedSecret = cbEncryptedSecret;
    pOutKey->cbTpm12HostageBlob = sizeof(UINT16) + cbTpm12HostageBlob;

    // We are jumping ahead and are calculating the policy digest list first
    cursor = pOutKey->cbHeader +
             pOutKey->cbPublic +
             pOutKey->cbPrivate +
             pOutKey->cbMigrationPublic +
             pOutKey->cbMigrationPrivate;
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT32)0x00000004)))
    {
        goto Cleanup;
    }

    // Prepare the Or policy
    if(FAILED(hr = WriteBigEndian(orPolicy, sizeof(orPolicy), &orPolicyCursor, (UINT32)0x00000171))) // TPM_CC_PolicyOR 
    {
        goto Cleanup;
    }
    // Add the User policy digest
    if(pcrMask == 0)
    {
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)sizeof(defaultUserPolicy))))
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (PBYTE)defaultUserPolicy, (UINT32)sizeof(defaultUserPolicy))))
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(orPolicy, sizeof(orPolicy), &orPolicyCursor, (PBYTE)defaultUserPolicy, (UINT32)sizeof(defaultUserPolicy))))
        {
            goto Cleanup;
        }
    }
    else
    {
        // Calculate PCR policy
        UINT32 compositeCursor = 0;
        BYTE policyDigestBuffer[SHA256_DIGEST_SIZE + // policyHash old 
                                sizeof(UINT32) +     // TPM_CC_PolicyPCR 
                                sizeof(UINT32) +     // TPML_PCR_SELECTION.count
                                sizeof(UINT16) +     // TPML_PCR_SELECTION.TPMS_PCR_SELECTION.hash
                                sizeof(BYTE) +       // TPML_PCR_SELECTION.TPMS_PCR_SELECTION.sizeofSelect
                                3 +                  // pcrSelect
                                SHA256_DIGEST_SIZE] = {0}; // pcrDigest
        UINT32 policyDigestBufferCursor = 0;
        cbPcrComposite = digestSize * AVAILABLE_PLATFORM_PCRS;
        if(FAILED(hr = AllocateAndZero((PVOID*)&pcrComposite, cbPcrComposite)))
        {
            goto Cleanup;
        }

        pOutKey->pcrAlgId = pcrAlgId;

        // Write all PCRs in the composite that are in the mask
        for(UINT32 n = 0; n < 24; n++)
        {
            if((pcrMask & (0x00000001 << n)) != 0)
            {
                if(FAILED(hr = WriteBigEndian(pcrComposite, cbPcrComposite, &compositeCursor, &pcrTable[n * digestSize], digestSize)))
                {
                    goto Cleanup;
                }
            }
        }

        // Calculate the PCR composite digest using the hashing algorithm of the policy
        if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA256_ALGORITHM,
                                      NULL,
                                      0,
                                      pcrComposite,
                                      compositeCursor,
                                      pcrCompositeDigest,
                                      SHA256_DIGEST_SIZE,
                                      &cbRequired)))
        {
            goto Cleanup;
        }

        // Calculate the user policy with the PCRs
        if(FAILED(hr = WriteBigEndian(policyDigestBuffer, sizeof(policyDigestBuffer), &policyDigestBufferCursor, (PBYTE)defaultUserPolicy, sizeof(defaultUserPolicy)))) // Default user policy digest
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(policyDigestBuffer, sizeof(policyDigestBuffer), &policyDigestBufferCursor, (UINT32)0x0000017F))) // TPM_CC_PolicyPCR
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(policyDigestBuffer, sizeof(policyDigestBuffer), &policyDigestBufferCursor, (UINT32)0x00000001))) // TPML_PCR_SELECTION.count
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(policyDigestBuffer, sizeof(policyDigestBuffer), &policyDigestBufferCursor, pcrAlgId))) // TPML_PCR_SELECTION.TPMS_PCR_SELECTION.hash
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(policyDigestBuffer, sizeof(policyDigestBuffer), &policyDigestBufferCursor, (BYTE)0x03))) // TPML_PCR_SELECTION.TPMS_PCR_SELECTION.sizeofSelect
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(policyDigestBuffer, sizeof(policyDigestBuffer), &policyDigestBufferCursor, (PBYTE)&pcrMask, 3))) // TPML_PCR_SELECTION.TPMS_PCR_SELECTION.Select
        {
            goto Cleanup;
        }
        if (FAILED(hr = WriteBigEndian(policyDigestBuffer, sizeof(policyDigestBuffer), &policyDigestBufferCursor, pcrCompositeDigest, SHA256_DIGEST_SIZE))) // digest
        {
            goto Cleanup;
        }
        if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA256_ALGORITHM,      // determined by policy not by algorithm of PCR bank
                                      NULL,
                                      0,
                                      policyDigestBuffer,
                                      sizeof(policyDigestBuffer),
                                      policyDigestBuffer,
                                      SHA256_DIGEST_SIZE,
                                      &cbRequired)))
        {
            goto Cleanup;
        }

        // Write the policy digest with the PCR policy
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)SHA256_DIGEST_SIZE)))
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (PBYTE)policyDigestBuffer, (UINT32)SHA256_DIGEST_SIZE)))
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(orPolicy, sizeof(orPolicy), &orPolicyCursor, (PBYTE)policyDigestBuffer, (UINT32)SHA256_DIGEST_SIZE)))
        {
            goto Cleanup;
        }
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)sizeof(adminObjectChangeAuthPolicy))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (PBYTE)adminObjectChangeAuthPolicy, (UINT32)sizeof(adminObjectChangeAuthPolicy))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(orPolicy, sizeof(orPolicy), &orPolicyCursor, (PBYTE)adminObjectChangeAuthPolicy, (UINT32)sizeof(adminObjectChangeAuthPolicy))))
    {
        goto Cleanup;
    }

    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)sizeof(adminCertifyPolicy))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (PBYTE)adminCertifyPolicy, (UINT32)sizeof(adminCertifyPolicy))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(orPolicy, sizeof(orPolicy), &orPolicyCursor, (PBYTE)adminCertifyPolicy, (UINT32)sizeof(adminCertifyPolicy))))
    {
        goto Cleanup;
    }

    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)sizeof(adminActivateCredentialPolicy))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (PBYTE)adminActivateCredentialPolicy, (UINT32)sizeof(adminActivateCredentialPolicy))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(orPolicy, sizeof(orPolicy), &orPolicyCursor, (PBYTE)adminActivateCredentialPolicy, (UINT32)sizeof(adminActivateCredentialPolicy))))
    {
        goto Cleanup;
    }

    if(pcrMask != 0)
    {
        // Write the PCR binding now
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (PBYTE)&pcrMask, 3)))
        {
            goto Cleanup;
        }

        // Write the PCR digest now
        if (FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, pcrCompositeDigest, SHA256_DIGEST_SIZE)))
        {
            goto Cleanup;
        }
    }

    // Now we jump back to the beginning and continue with the public key structure
    cursor = pOutKey->cbHeader;
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)cbPublicKey))) // 2B Size
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)0x0001))) // TPM_ALG_RSA
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)TPM_API_ALG_ID_SHA256))) // TPM_ALG_SHA256
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, objectAttributes)))
    {
        goto Cleanup;
    }
    // Calculate and fill in the policy digest
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)SHA256_DIGEST_SIZE)))
    {
        goto Cleanup;
    }
    if((cursor + SHA256_DIGEST_SIZE) > cbOutput)
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA256_ALGORITHM,
                                  NULL,
                                  0,
                                  orPolicy,
                                  sizeof(orPolicy),
                                  &pbOutput[cursor],
                                  SHA256_DIGEST_SIZE,
                                  &cbRequired)))
    {
        goto Cleanup;
    }
    cursor += cbRequired;
    // Only Storage keys need a symmetric algorithm
    if((keyUsage & 0x0000ffff) == NCRYPT_PCP_STORAGE_KEY)
    {
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)0x0006))) // TPM_ALG_AES
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)0x0080))) // keybits
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)0x0043))) // TPM_ALG_CFB
        {
            goto Cleanup;
        }
    }
    else
    {
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)0x0010))) // TPM_ALG_NULL
        {
            goto Cleanup;
        }
    }

    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)0x0010))) // TPM_ALG_NULL
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)pKeyPair->BitLength)))
    {
        goto Cleanup;
    }
    if(tDefaultExponent)
    {
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT32)0x00000000)))
        {
            goto Cleanup;
        }
    }
    else
    {
        // Write leading zeros
        for(UINT32 n = 0; n < (sizeof(UINT32) - pKeyPair->cbPublicExp); n++)
        {
            if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (BYTE)0x00)))
            {
                goto Cleanup;
            }
        }
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, &pbKeyPair[sizeof(BCRYPT_RSAKEY_BLOB)], pKeyPair->cbPublicExp))) // non-default exponent
        {
            goto Cleanup;
        }
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)pKeyPair->cbModulus))) // unique
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, &pbKeyPair[sizeof(BCRYPT_RSAKEY_BLOB) +
                                                                          pKeyPair->cbPublicExp], pKeyPair->cbModulus))) // unique
    {
        goto Cleanup;
    }

    // Write the RSA encrypted seed value
    cursor = pOutKey->cbHeader +
             pOutKey->cbPublic +
             pOutKey->cbPrivate +
             pOutKey->cbMigrationPublic +
             pOutKey->cbMigrationPrivate +
             pOutKey->cbPolicyDigestList +
             pOutKey->cbPCRBinding +
             pOutKey->cbPCRDigest;
    if((cursor + pOutKey->cbEncryptedSecret + sizeof(UINT16)) > cbOutput)
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)(pOutKey->cbEncryptedSecret - sizeof(UINT16)))))
    {
        goto Cleanup;
    }

    // Generate a random seed value to protect the key blob in transit
    if(FAILED(hr = HRESULT_FROM_NT(BCryptGenRandom(
                                     hRngAlg,
                                     (PUCHAR)seed,
                                     sizeof(seed),
                                     0))))
    {
        goto Cleanup;
    }

    // Encrypt that key with the RSA parent
    if(FAILED(hr = HRESULT_FROM_NT(BCryptEncrypt(hStorageKey,
                                                 seed,
                                                 sizeof(seed),
                                                 &paddingInfo,
                                                 NULL,
                                                 0,
                                                 &pbOutput[cursor],
                                                 cbOutput - cursor,
                                                 (PDWORD)&cbRequired,
                                                 BCRYPT_PAD_OAEP))))
    {
        goto Cleanup;
    }
    cursor += cbRequired;

    // Get some memory to put the sensitive structure together
    // We are getting a sizeof(UINT16) + SHA256_DIGEST_SIZE digest more space
    // for the name of the public key, so we can calculate the outer HMAC value
    // directly. Later when we copy the structure we are going to stop short of
    // the name.
    cbPrivateKey = cbTpm12HostageBlob + sizeof(UINT16) + SHA256_DIGEST_SIZE;
    if(FAILED(hr = AllocateAndZero((PVOID*)&pbPrivateKey, cbPrivateKey)))
    {
        goto Cleanup;
    }

    // Fill the private key structure
    if(FAILED(hr = WriteBigEndian(pbPrivateKey, cbPrivateKey, &privateKeyCursor, (UINT16)SHA256_DIGEST_SIZE))) // Outer HMAC size
    {
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(pbPrivateKey, cbPrivateKey, &privateKeyCursor, (UINT32)SHA256_DIGEST_SIZE))) // Skip over outer HMAC, we will fill this in at the end
    {
        goto Cleanup;
    }
    UINT16 cbSensitive = (UINT16)(cbTpm12HostageBlob - (sizeof(UINT16) + SHA256_DIGEST_SIZE));
    if(FAILED(hr = WriteBigEndian(pbPrivateKey, cbPrivateKey, &privateKeyCursor, (UINT16)(cbSensitive - sizeof(UINT16))))) // TPM2B_SENSITIVE.Size
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbPrivateKey, cbPrivateKey, &privateKeyCursor, (UINT16)0x0001))) // TPM_ALG_RSA
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbPrivateKey, cbPrivateKey, &privateKeyCursor, (UINT16)cbUsageAuth)))
    {
        goto Cleanup;
    }
    if(cbUsageAuth != 0)
    {
        if(FAILED(hr = WriteBigEndian(pbPrivateKey, cbPrivateKey, &privateKeyCursor, pbUsageAuth, cbUsageAuth)))
        {
            goto Cleanup;
        }
    }
    if((keyUsage & 0x0000ffff) == NCRYPT_PCP_STORAGE_KEY)
    {
        if(FAILED(hr = WriteBigEndian(pbPrivateKey, cbPrivateKey, &privateKeyCursor, (UINT16)sizeof(symmetricKey))))
        {
            goto Cleanup;
        }

        // Use the value of prime1 or prime2 to derive the SymKey so that keys that use this key as a parent
        // can be moved to any TPM that has this key.
        //
        // Select the longest prime, and when they are the same length select the greater prime
        // this ensures a stable derived key even with the arbitrary ordering of prime1/prime2 
        // in the structure.
        //
        if (pKeyPair->cbPrime1 < pKeyPair->cbPrime2 ||
            0 < memcmp(&pbKeyPair[sizeof(BCRYPT_RSAKEY_BLOB) +
                                  pKeyPair->cbPublicExp +
                                  pKeyPair->cbModulus],
                       &pbKeyPair[sizeof(BCRYPT_RSAKEY_BLOB) +
                                  pKeyPair->cbPublicExp +
                                  pKeyPair->cbModulus +
                                  pKeyPair->cbPrime1],
                       min(pKeyPair->cbPrime1, pKeyPair->cbPrime2)))
        {
            if(FAILED(hr = HRESULT_FROM_NT(BCryptGenerateSymmetricKey(hDerivation,
                                                                      &hDerivationKey,
                                                                      NULL,
                                                                      0,
                                                                      &pbKeyPair[sizeof(BCRYPT_RSAKEY_BLOB) +
                                                                                 pKeyPair->cbPublicExp +
                                                                                 pKeyPair->cbModulus +
                                                                                 pKeyPair->cbPrime1],
                                                                      pKeyPair->cbPrime2,
                                                                      0))))
            {
                goto Cleanup;
            }
        }
        else
        {
            if(FAILED(hr = HRESULT_FROM_NT(BCryptGenerateSymmetricKey(hDerivation,
                                                                      &hDerivationKey,
                                                                      NULL,
                                                                      0,
                                                                      &pbKeyPair[sizeof(BCRYPT_RSAKEY_BLOB) +
                                                                                 pKeyPair->cbPublicExp +
                                                                                 pKeyPair->cbModulus],
                                                                      pKeyPair->cbPrime1,
                                                                      0))))
            {
                goto Cleanup;
            }
        }
        if(FAILED(hr = HRESULT_FROM_NT(BCryptKeyDerivation(hDerivationKey,
                                                           &parameters,
                                                           symmetricKey,
                                                           sizeof(symmetricKey),
                                                           (PDWORD)&cbRequired,
                                                           0))))
        {
            goto Cleanup;
        }

        // Add the symmetric key
        if(FAILED(hr = WriteBigEndian(pbPrivateKey, cbPrivateKey, &privateKeyCursor, symmetricKey, sizeof(symmetricKey))))
        {
            goto Cleanup;
        }
    }
    else
    {
        if(FAILED(hr = WriteBigEndian(pbPrivateKey, cbPrivateKey, &privateKeyCursor, (UINT16)0x0000)))
        {
            goto Cleanup;
        }
    }
    if(FAILED(hr = WriteBigEndian(pbPrivateKey, cbPrivateKey, &privateKeyCursor, (UINT16)pKeyPair->cbPrime1)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbPrivateKey, cbPrivateKey, &privateKeyCursor, &pbKeyPair[sizeof(BCRYPT_RSAKEY_BLOB) +
                                                                                            pKeyPair->cbPublicExp +
                                                                                            pKeyPair->cbModulus], pKeyPair->cbPrime1)))
    {
        goto Cleanup;
    }

    // Add now the name of the public key. The name is only used to calculate the
    // integrity value and is NOT included in the encrypted blob later
    if(FAILED(hr = GetNameFromPublic(&pbOutput[pOutKey->cbHeader + sizeof(UINT16)],
                                     pOutKey->cbPublic - sizeof(UINT16),
                                     NULL,
                                     keyName,
                                     sizeof(keyName),
                                     &cbRequired)))
    {
        goto Cleanup;
    }
    memcpy(&pbPrivateKey[privateKeyCursor], keyName, sizeof(keyName));

    // Calculate the symmetric that will be used to encrypt the sensitive structure
    // symKey = KDFa(npNameAlg, seed, “STORAGE”, name, NULL , bits)
    if(FAILED(hr = KDFa(BCRYPT_SHA256_ALGORITHM,
                        seed,
                        sizeof(seed),
                        "STORAGE",
                        keyName,
                        sizeof(keyName),
                        NULL,
                        0,
                        sizeof(symKey) * 8,
                        symKey,
                        sizeof(symKey),
                        &cbRequired)))
    {
        goto Cleanup;
    }

    // Encrypt the sensitive area in place
    // dupSensitive = CFBnpSymAlg(symKey, 0, sensitive)
    memset(aesIv, 0x00, sizeof(aesIv));
    if(FAILED(hr = CFB(symKey,
                       sizeof(symKey),
                       aesIv,
                       sizeof(aesIv),
                       &pbPrivateKey[sizeof(UINT16) + SHA256_DIGEST_SIZE], // Start right after the HAMC value at the beginning
                       cbSensitive))) // Size + Sensitive
    {
        goto Cleanup;
    }

    // We will calculate now the key for the outer HMAC
    // HMACkey = KDFa(npNameAlg, seed, “INTEGRITY”, NULL, NULL, bits)
    if(FAILED(hr = KDFa(BCRYPT_SHA256_ALGORITHM,
                        seed,
                        sizeof(seed),
                        "INTEGRITY",
                        NULL,
                        0,
                        NULL,
                        0,
                        SHA256_DIGEST_SIZE * 8,
                        hmacKey,
                        sizeof(hmacKey),
                        &cbRequired)))
    {
        goto Cleanup;
    }

    // Calculate the outer HMAC and fill it in at the beginning of the structure
    // outerHMAC = HMACnpNameAlg(HMACkey, dupSensitive || name)
    if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA256_ALGORITHM,
                                  hmacKey,
                                  sizeof(hmacKey),
                                  &pbPrivateKey[sizeof(UINT16) + SHA256_DIGEST_SIZE], // Start right after the HAMC value at the beginning
                                  cbSensitive + sizeof(UINT16) + SHA256_DIGEST_SIZE, // Size + Sensitive +
                                  &pbPrivateKey[sizeof(UINT16)],
                                  SHA256_DIGEST_SIZE,
                                  &cbRequired)))
    {
        goto Cleanup;
    }

    // Append the protected sensitive structure to our key blob.
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)privateKeyCursor))) // 2B size
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, pbPrivateKey, (UINT16)privateKeyCursor)))
    {
        goto Cleanup;
    }

    if(cursor > cbOutput)
    {
        hr = E_FAIL;
        goto Cleanup;
    }

    *pcbResult = cursor;

Cleanup:
    if(hAesKey != NULL)
    {
        BCryptDestroyKey(hAesKey);
        hAesKey = NULL;
    }
    if(hAesAlg != NULL)
    {
        BCryptCloseAlgorithmProvider(hAesAlg, 0);
        hAesAlg = NULL;
    }
    if(hRngAlg != NULL)
    {
        BCryptCloseAlgorithmProvider(hRngAlg, 0);
        hRngAlg = NULL;
    }
    if (hDerivationKey != NULL)
    {
        BCryptDestroyKey(hDerivationKey);
        hDerivationKey = NULL;
    }
    if (hDerivation != NULL)
    {
        (VOID)BCryptCloseAlgorithmProvider(hDerivation, 0);
        hDerivation = NULL;
    }
    ZeroAndFree((PVOID*)&pbPrivateKey, cbPrivateKey);
    ZeroAndFree((PVOID*)&pcrComposite, cbPcrComposite);

    return hr;
}


/*++

THIS CODE AND INFORMATION IS PROVIDED "AS IS" WITHOUT WARRANTY OF
ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
PARTICULAR PURPOSE.

Copyright (c) Microsoft Corporation.  All rights reserved.

Module Name:

    TPM12.cpp

Abstract:

    This file contains TPM 1.2 specific functions.

--*/

#include "stdafx.h"

HRESULT
GetKeyHandleFromPubKeyBlob12(
    _In_reads_(cbKeyBlob) PBYTE pbKeyBlob,
    UINT32 cbKeyBlob,
    BCRYPT_ALG_HANDLE hAlg,
    _Out_ BCRYPT_KEY_HANDLE* phPubKey,
    _Out_opt_ PUINT32 pcbTrailing
    )
{
    HRESULT hr = S_OK;
    UINT32 cursor = 0;
    UINT32 cbRsaPubKey = 0;
    PBYTE pbRsaPubKey = NULL;
    BCRYPT_RSAKEY_BLOB* RsaPubKey = NULL;
    BYTE defaultExponent[] = {0x01, 0x00, 0x01};
    UINT32 algorithmID = 0;
    UINT32 keyLength = 0;
    UINT32 numPrimes = 0;
    UINT32 cbExponent = 0;
    PBYTE pbExponent = NULL;
    UINT32 cbPubKey = 0;
    PBYTE pbPubKey = NULL;

    if((hAlg == NULL) ||
       (phPubKey == NULL) ||
       (pbKeyBlob == NULL) ||
       (cbKeyBlob == 0))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Unpack key structure
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &algorithmID)))
    {
        goto Cleanup;
    }
    if(algorithmID != 0x00000001) //TPM_ALG_RSA
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &cursor, sizeof(UINT16) +  // encScheme
                                                                sizeof(UINT16) +  // sigScheme
                                                                sizeof(UINT32)))) // parmSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &keyLength)))
    {
        goto Cleanup;
    }

    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &numPrimes)))
    {
        goto Cleanup;
    }
    if(numPrimes != 2)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &cbExponent)))
    {
        goto Cleanup;
    }
    if(cbExponent != 0)
    {
        if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &pbExponent, cbExponent)))
        {
            goto Cleanup;
        }
    }
    else
    {
        pbExponent = defaultExponent;
        cbExponent = sizeof(defaultExponent);
    }

    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &cbPubKey)))
    {
        goto Cleanup;
    }
    if(cbPubKey != 0)
    {
        if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &pbPubKey, cbPubKey)))
        {
            goto Cleanup;
        }
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
    RsaPubKey->BitLength = keyLength;
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

    // Return the index of the training data if requested
    if(pcbTrailing != NULL)
    {
        *pcbTrailing = cursor;
    }

Cleanup:
    ZeroAndFree((PVOID*)&pbRsaPubKey, cbRsaPubKey);
    return hr;
}

HRESULT
GetKeyHandleFromKeyBlob12(
    _In_reads_(cbKeyBlob) PBYTE pbKeyBlob,
    UINT32 cbKeyBlob,
    BCRYPT_ALG_HANDLE hAlg,
    _Out_ BCRYPT_KEY_HANDLE* phPubKey,
    _Out_opt_ PUINT32 pcbTrailing
    )
{
    HRESULT hr = S_OK;
    UINT32 cursor = 0;
    UINT32 cbRsaPubKey = 0;
    PBYTE pbRsaPubKey = NULL;
    BCRYPT_RSAKEY_BLOB* RsaPubKey = NULL;
    BYTE defaultExponent[] = {0x01, 0x00, 0x01};
    UINT32 algorithmID = 0;
    UINT32 keyLength = 0;
    UINT32 numPrimes = 0;
    UINT32 cbExponent = 0;
    PBYTE pbExponent = NULL;
    UINT32 pcrInfoSize = 0;
    UINT32 cbPubKey = 0;
    PBYTE pbPubKey = NULL;

    if((hAlg == NULL) ||
       (phPubKey == NULL) ||
       (pbKeyBlob == NULL) ||
       (cbKeyBlob == 0))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Unpack key structure
    if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &cursor, sizeof(UINT16) +  // tag
                                                                sizeof(UINT16) +  // fill
                                                                sizeof(UINT16) +  // keyUsage
                                                                sizeof(UINT32) +  // keyFlags
                                                                sizeof(BYTE))))   // authDataUsage
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &algorithmID)))
    {
        goto Cleanup;
    }
    if(algorithmID != 0x00000001) //TPM_ALG_RSA
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &cursor, sizeof(UINT16) +  // encScheme
                                                                sizeof(UINT16) +  // sigScheme
                                                                sizeof(UINT32)))) // parmSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &keyLength)))
    {
        goto Cleanup;
    }

    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &numPrimes)))
    {
        goto Cleanup;
    }
    if(numPrimes != 2)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &cbExponent)))
    {
        goto Cleanup;
    }
    if(cbExponent != 0)
    {
        if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &pbExponent, cbExponent)))
        {
            goto Cleanup;
        }
    }
    else
    {
        pbExponent = defaultExponent;
        cbExponent = sizeof(defaultExponent);
    }
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &pcrInfoSize)))
    {
        goto Cleanup;
    }
    if(pcrInfoSize != 0)
    {
        if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &cursor, pcrInfoSize)))
        {
            goto Cleanup;
        }
    }
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &cbPubKey)))
    {
        goto Cleanup;
    }
    if(cbPubKey != 0)
    {
        if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &cursor, &pbPubKey, cbPubKey)))
        {
            goto Cleanup;
        }
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
    RsaPubKey->BitLength = keyLength;
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

    // Return the index of the training data if requested
    if(pcbTrailing != NULL)
    {
        *pcbTrailing = cursor;
    }

Cleanup:
    ZeroAndFree((PVOID*)&pbRsaPubKey, cbRsaPubKey);
    return hr;
}

HRESULT
StartOIAPSession(
    _In_ TBS_HCONTEXT hPlatformTbsHandle,
    _Out_ PUINT32 pSessionHandle,
    _Out_writes_(SHA1_DIGEST_SIZE) PBYTE pEvenNonce,
    _Out_writes_(SHA1_DIGEST_SIZE) PBYTE pOddNonce
    )
{
    HRESULT hr = S_OK;
    BYTE cmd[] = {0x00, 0xc1,              //TPM_TAG_RQU_COMMAND
                  0x00, 0x00, 0x00, 0x0a,  //paramSize
                  0x00, 0x00, 0x00, 0x0a}; //TPM_ORD_OIAP
    BYTE rsp[0x200] = {0};
    UINT32 cbRsp = sizeof(rsp);
    UINT32 cursor = 0;
    UINT32 paramSize = 0;
    UINT32 returnCode = 0;
    PBYTE evenNonce = NULL;
    BCRYPT_ALG_HANDLE hRngAlg = NULL;

    if((pSessionHandle == NULL) ||
       (pEvenNonce == NULL) ||
       (pOddNonce == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Make OACR happy
    memset(pOddNonce, 0x00, SHA1_DIGEST_SIZE);

    // Send the hard coded command to the TPM
    if(FAILED(hr = Tbsip_Submit_Command(hPlatformTbsHandle,
                                        TBS_COMMAND_LOCALITY_ZERO,
                                        TBS_COMMAND_PRIORITY_NORMAL,
                                        cmd,
                                        sizeof(cmd),
                                        rsp,
                                        &cbRsp)))
    {
        goto Cleanup;
    }

    // Parse the response
    if(FAILED(hr = SkipBigEndian(rsp, cbRsp, &cursor, sizeof(UINT16)))) // skip tag
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursor, &paramSize))) // paramSize
    {
        goto Cleanup;
    }
    if(paramSize != cbRsp)
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursor, &returnCode))) // ReturnCode
    {
        goto Cleanup;
    }
    if(returnCode != 0)
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursor, pSessionHandle))) // Session Handle
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursor, &evenNonce, SHA1_DIGEST_SIZE))) // evenNonce
    {
        goto Cleanup;
    }

    // Return the nonces
    if(memcpy_s(pEvenNonce, SHA1_DIGEST_SIZE, evenNonce, SHA1_DIGEST_SIZE))
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    if(FAILED(hr = HRESULT_FROM_NT(BCryptOpenAlgorithmProvider(
                                                &hRngAlg,
                                                BCRYPT_RNG_ALGORITHM,
                                                NULL,
                                                0))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = HRESULT_FROM_NT(BCryptGenRandom(
                                         hRngAlg,
                                         pOddNonce,
                                         SHA1_DIGEST_SIZE,
                                         0))))
    {
        goto Cleanup;
    }

Cleanup:
    if(hRngAlg != NULL)
    {
        BCryptCloseAlgorithmProvider(hRngAlg, 0);
        hRngAlg = NULL;
    }
    return hr;
}

HRESULT
PubKeyFromIdBinding12(
    _In_reads_(cbIdBinding) PBYTE pbIdBinding,
    UINT32 cbIdBinding,
    BCRYPT_ALG_HANDLE hRsaAlg,
    _Out_ BCRYPT_KEY_HANDLE* phAikPub
    )
{
    HRESULT hr = S_OK;
    UINT32 cursor = 0;

    // Check the parameters
    if((pbIdBinding == NULL) ||
       (cbIdBinding == 0) ||
       (hRsaAlg == NULL) ||
       (phAikPub == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Take the IdBinding apart
    //Tag
    if(FAILED(hr = SkipBigEndian(pbIdBinding, cbIdBinding, &cursor, sizeof(UINT32))))
    {
        goto Cleanup;
    }
    //TPM_ORD_MakeIdentity
    if(FAILED(hr = SkipBigEndian(pbIdBinding, cbIdBinding, &cursor, sizeof(UINT32))))
    {
        goto Cleanup;
    }
    //LabelPrivCADigest
    if(FAILED(hr = SkipBigEndian(pbIdBinding, cbIdBinding, &cursor, SHA1_DIGEST_SIZE)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = GetKeyHandleFromPubKeyBlob12(
                            &pbIdBinding[cursor],
                            cbIdBinding - cursor,
                            hRsaAlg,
                            phAikPub,
                            NULL)))
    {
        goto Cleanup;
    }

Cleanup:
    return hr;
}

HRESULT
GenerateActivation12(
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
    UINT32 cbRequired = 0;
    PBYTE pbLabelPrivCADigest = NULL;
    UINT32 cbLabelPrivCADigest = SHA1_DIGEST_SIZE;
    PBYTE pbAikPub = NULL;
    UINT32 cbAikPub = 0;
    BCRYPT_ALG_HANDLE hRsaAlg = NULL;
    BCRYPT_KEY_HANDLE hAikPub = NULL;
    PBYTE pbSignature = NULL;
    UINT32 cbSignature = 0;
    PBYTE pbIdBindingDigest = NULL;
    UINT32 cbIdBindingDigest = 0;
    BCRYPT_PKCS1_PADDING_INFO pPkcs = {BCRYPT_SHA1_ALGORITHM};
    PBYTE pbAikDigest = NULL;
    UINT32 cbAikDigest = 0;
    PBYTE pbActivation = NULL;
    UINT32 cbActivation = 0;
    UCHAR abOAEPParam[] = {'T', 'C', 'P', 'A'};
    BCRYPT_OAEP_PADDING_INFO oaep = {BCRYPT_SHA1_ALGORITHM,
                                     abOAEPParam,
                                     sizeof(abOAEPParam)};

    // Check the parameters
    if((hEkPub == NULL) ||
       (pbIdBinding == NULL) ||
       (cbIdBinding == 0) ||
       (pbSecret == NULL) ||
       (cbSecret == 0) ||
       (pcbResult == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Get the Activation blob size
    if(FAILED(hr = HRESULT_FROM_NT(BCryptGetProperty(
                                        hEkPub,
                                        BCRYPT_BLOCK_LENGTH,
                                        (PBYTE)&cbRequired,
                                        sizeof(cbRequired),
                                        (PULONG)&cbRequired,
                                        0))))
    {
        goto Cleanup;
    }

    // Was this just a size request?
    if(cbOutput == 0)
    {
        *pcbResult = cbRequired;
        goto Cleanup;
    }
    else if((cbOutput < cbRequired) ||
            (pbOutput == NULL))
    {
        hr = HRESULT_FROM_WIN32(ERROR_INSUFFICIENT_BUFFER);
        *pcbResult = cbRequired;
        goto Cleanup;
    }

    // Take the IdBinding apart
    //Tag
    if(FAILED(hr = SkipBigEndian(pbIdBinding, cbIdBinding, &cursor, sizeof(UINT32))))
    {
        goto Cleanup;
    }
    //TPM_ORD_MakeIdentity
    if(FAILED(hr = SkipBigEndian(pbIdBinding, cbIdBinding, &cursor, sizeof(UINT32))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbIdBinding, cbIdBinding, &cursor, &pbLabelPrivCADigest, cbLabelPrivCADigest)))
    {
        goto Cleanup;
    }

    // Get Key Handle
    if(FAILED(hr = HRESULT_FROM_NT(BCryptOpenAlgorithmProvider(
                                                &hRsaAlg,
                                                BCRYPT_RSA_ALGORITHM,
                                                NULL,
                                                0))))
    {
        goto Cleanup;
    }
    pbAikPub = &pbIdBinding[cursor];
    if(FAILED(hr = GetKeyHandleFromPubKeyBlob12(
                            pbAikPub,
                            cbIdBinding - cursor,
                            hRsaAlg,
                            &hAikPub,
                            &cbAikPub)))
    {
        goto Cleanup;
    }
    cursor += cbAikPub;

    // Get Signature
    cbSignature = cbIdBinding - cursor;
    if(FAILED(hr = ReadBigEndian(pbIdBinding, cbIdBinding, &cursor, &pbSignature, cbSignature)))
    {
        goto Cleanup;
    }

    // Verify nonce, if requested
    if(pbNonce != NULL)
    {
        if((cbNonce != cbLabelPrivCADigest) ||
           (memcmp(pbNonce, pbLabelPrivCADigest, cbNonce) != 0))
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }
    }

    // Calculate IdBinding Digest
    if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                  NULL,
                                  0,
                                  &pbIdBinding[0],
                                  cbIdBinding - cbSignature,
                                  NULL,
                                  0,
                                  &cbIdBindingDigest)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = AllocateAndZero((PVOID*)&pbIdBindingDigest, cbIdBindingDigest)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                  NULL,
                                  0,
                                  &pbIdBinding[0],
                                  cbIdBinding - cbSignature,
                                  pbIdBindingDigest,
                                  cbIdBindingDigest,
                                  &cbIdBindingDigest)))
    {
        goto Cleanup;
    }

    // Verify IdBinding Signature with AIK
    if(FAILED(hr = HRESULT_FROM_NT(BCryptVerifySignature(hAikPub,
                                &pPkcs,
                                pbIdBindingDigest,
                                cbIdBindingDigest,
                                pbSignature,
                                cbSignature,
                                BCRYPT_PAD_PKCS1))))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Calculate AIKPub Digest
    if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                  NULL,
                                  0,
                                  pbAikPub,
                                  cbAikPub,
                                  NULL,
                                  0,
                                  &cbAikDigest)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = AllocateAndZero((PVOID*)&pbAikDigest, cbAikDigest)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                  NULL,
                                  0,
                                  pbAikPub,
                                  cbAikPub,
                                  pbAikDigest,
                                  cbAikDigest,
                                  &cbAikDigest)))
    {
        goto Cleanup;
    }

    // Create activation token
    cbActivation = sizeof(UINT16) + //TPM_STRUCTURE_TAG tag = TPM_TAG_EK_BLOB
                   sizeof(UINT16) + //TPM_EK_TYPE ekType = TPM_EK_TYPE_ACTIVATE
                   sizeof(UINT32) + //UINT32 blobSize = cbActivation - (2 * sizeof(UINT16) + sizeof(UINT32))
                   sizeof(UINT16) + //TPM_STRUCTURE_TAG tag = TPM_TAG_EK_BLOB_ACTIVATE
                   sizeof(UINT32) + //TPM_ALGORITHM_ID algId = TPM_ALG_XOR
                   sizeof(UINT16) + //TPM_ENC_SCHEME encScheme = TPM_ES_NONE
                   sizeof(UINT16) + //UINT16 size
                   cbSecret +
                   cbAikDigest +
                   sizeof(UINT16) + // UINT16 sizeOfSelect = 3
                   3 + // PcrSelect
                   sizeof(BYTE) + //TPM_LOCALITY_SELECTION localityAtRelease = TPM_LOC_ZERO
                   SHA1_DIGEST_SIZE; //TPM_COMPOSITE_HASH digestAtRelease = 0
    if(FAILED(hr = AllocateAndZero((PVOID*)&pbActivation, cbActivation)))
    {
        goto Cleanup;
    }
    cursor = 0;
    if(FAILED(hr = WriteBigEndian(pbActivation, cbActivation, &cursor, (UINT16)0x000c))) //TPM_TAG_EK_BLOB
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbActivation, cbActivation, &cursor, (UINT16)0x0001))) //TPM_EK_TYPE_ACTIVATE
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbActivation, cbActivation, &cursor, (UINT32)(cbActivation - (2 * sizeof(UINT16) + sizeof(UINT32))))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbActivation, cbActivation, &cursor, (UINT16)0x002b))) //TPM_TAG_EK_BLOB_ACTIVATE
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbActivation, cbActivation, &cursor, (UINT32)0x0000000a))) //TPM_ALG_XOR
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbActivation, cbActivation, &cursor, (UINT16)0x0001))) //TPM_ES_NONE
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbActivation, cbActivation, &cursor, cbSecret)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbActivation, cbActivation, &cursor, pbSecret, cbSecret)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbActivation, cbActivation, &cursor, pbAikDigest, cbAikDigest)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbActivation, cbActivation, &cursor, (UINT16)0x0003))) // UINT16 sizeOfSelect = 3
    {
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(pbActivation, cbActivation, &cursor, (UINT32)3))) // 3 bytes of 0 PcrSelect
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbActivation, cbActivation, &cursor, (BYTE)0x01))) // TPM_LOC_ZERO
    {
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(pbActivation, cbActivation, &cursor, (UINT32)SHA1_DIGEST_SIZE))) // empty digest
    {
        goto Cleanup;
    }
    if(cbActivation != cursor)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Encrypt the token with the EKpub
    if(FAILED(hr = HRESULT_FROM_NT(BCryptEncrypt(
                                        hEkPub,
                                        pbActivation,
                                        cursor,
                                        &oaep,
                                        NULL,
                                        0,
                                        pbOutput,
                                        cbOutput,
                                        (PULONG)pcbResult,
                                        BCRYPT_PAD_OAEP))))
    {
        goto Cleanup;
    }

Cleanup:
    ZeroAndFree((PVOID*)&pbIdBindingDigest, cbIdBindingDigest);
    ZeroAndFree((PVOID*)&pbAikDigest, cbAikDigest);
    ZeroAndFree((PVOID*)&pbActivation, cbActivation);
    return hr;
}

HRESULT
GenerateQuote12(
    TBS_HCONTEXT hPlatformTbsHandle,
    UINT32 hPlatformKeyHandle,
    _In_reads_(cbKeyAuth) PBYTE pbKeyAuth,
    UINT32 cbKeyAuth,
    UINT32 pcrMask,
    _In_reads_(cbNonce) PBYTE pbNonce,
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
    BYTE paramHash[0x200] = {0};
    UINT32 cbRsp = sizeof(rsp);
    UINT32 cursorCmd = 0;
    UINT32 cursorParamHash = 0;
    UINT32 cursorRsp = 0;
    UINT32 authHandle = 0;
    BYTE pcrProfile[] = {0x00, 0x03, 0x7f, 0xf7, 0x00};
    BYTE authBuffer[3 * SHA1_DIGEST_SIZE + sizeof(BYTE)] = {0};
    PBYTE pParamDigest = &authBuffer[0];
    PBYTE pNonceEven = &authBuffer[SHA1_DIGEST_SIZE];
    PBYTE pNonceOdd = &authBuffer[2 * SHA1_DIGEST_SIZE];
    PBYTE pContinueAuthSession = &authBuffer[3 * SHA1_DIGEST_SIZE];
    UINT32 paramSize = 0;
    UINT32 returnCode = 0;
    PBYTE pbPcrData = NULL;
    UINT32 cbPcrData = 26;
    PBYTE pbVersionInfo = NULL;
    UINT32 cbVersionInfo = 0;
    PBYTE pbSignature = NULL;
    UINT32 cbSignature = 0;
    PBYTE pResponseAuth = NULL;
    BYTE responseAuthReference[SHA1_DIGEST_SIZE] = {0};

    // Check the parameters
    if((pcbResult == NULL) ||
       (pbKeyAuth == NULL) ||
       (cbKeyAuth != SHA1_DIGEST_SIZE) ||
       (pbNonce == NULL) ||
       (cbNonce != SHA1_DIGEST_SIZE))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Fill in PCR mask
    pcrProfile[0x0002] = (BYTE)((pcrMask & 0x000000ff));
    pcrProfile[0x0003] = (BYTE)(((pcrMask & 0x0000ff00) >> 8));
    pcrProfile[0x0004] = (BYTE)(((pcrMask & 0x00ff0000) >> 16));

    // Start OIAP session
    if(FAILED(hr = StartOIAPSession(hPlatformTbsHandle, &authHandle, pNonceEven, pNonceOdd)))
    {
        goto Cleanup;
    }

    // Build Quote2 command buffer
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)0x00c2))) //TPM_TAG_RQU_AUTH1_COMMAND
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)0x00000055))) //paramSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)0x0000003e))) //TPM_ORD_Quote2
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, hPlatformKeyHandle))) //keyHandle
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, pbNonce, cbNonce))) //externalData
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, pcrProfile, sizeof(pcrProfile)))) //pcrProfile
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (BYTE) 0x01))) //addVersion
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, authHandle))) //authHandle
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, pNonceOdd, SHA1_DIGEST_SIZE))) //nonceOdd
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, *pContinueAuthSession))) //continueAuthSession
    {
        goto Cleanup;
    }

    // Calculate parameter digest
    if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, (UINT32)0x0000003e))) //TPM_ORD_Quote2
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, pbNonce, cbNonce))) //externalData
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, pcrProfile, sizeof(pcrProfile)))) //pcrProfile
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, (BYTE) 0x01))) //addVersion
    {
        goto Cleanup;
    }

    if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                  NULL,
                                  0,
                                  paramHash,
                                  cursorParamHash,
                                  pParamDigest,
                                  SHA1_DIGEST_SIZE,
                                  (PUINT32)&cbRequired)))
    {
        goto Cleanup;
    }

    // Calculate command authorization
    if(sizeof(cmd) < cursorCmd + SHA1_DIGEST_SIZE)
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                  pbKeyAuth,
                                  cbKeyAuth,
                                  authBuffer,
                                  sizeof(authBuffer),
                                  &cmd[cursorCmd],
                                  SHA1_DIGEST_SIZE,
                                  (PUINT32)&cbRequired)))
    {
        goto Cleanup;
    }
    cursorCmd += SHA1_DIGEST_SIZE;

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
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &paramSize))) // paramSize
    {
        goto Cleanup;
    }
    if(paramSize != cbRsp)
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
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &pbPcrData, cbPcrData))) // pcrData
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &cbVersionInfo))) // versionInfoSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &pbVersionInfo, cbVersionInfo))) // versionInfo
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &cbSignature))) // sigSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &pbSignature, cbSignature))) // sig
    {
        goto Cleanup;
    }

    // Process session information from the response
    if((cursorRsp + 2 * SHA1_DIGEST_SIZE + sizeof(BYTE)) != cbRsp)
    {
        hr = E_FAIL;
        goto Cleanup;
    }

    if(memcpy_s(pNonceEven, sizeof(authBuffer) - (pNonceEven - authBuffer), &rsp[cursorRsp], SHA1_DIGEST_SIZE))
    {
        hr = E_FAIL;
        goto Cleanup;
    }

    cursorRsp += SHA1_DIGEST_SIZE;
    *pContinueAuthSession = rsp[cursorRsp];
    cursorRsp += sizeof(BYTE);
    pResponseAuth = &rsp[cursorRsp];
    cursorRsp += SHA1_DIGEST_SIZE;

    // Calculate parameter digest
    cursorParamHash = 0;
    memset(paramHash, 0x00, sizeof(paramHash));
    if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, returnCode))) //returnCode
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, (UINT32)0x0000003e))) //TPM_ORD_Quote2
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, pbPcrData, cbPcrData))) //pcrData
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, cbVersionInfo))) //versionInfoSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, pbVersionInfo, cbVersionInfo))) //pcrProfile
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, cbSignature))) //sigSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, pbSignature, cbSignature))) //sig
    {
        goto Cleanup;
    }
    if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                  NULL,
                                  0,
                                  paramHash,
                                  cursorParamHash,
                                  pParamDigest,
                                  SHA1_DIGEST_SIZE,
                                  (PUINT32)&cbRequired)))
    {
        goto Cleanup;
    }

    // Calculate response authorization
    if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                  pbKeyAuth,
                                  cbKeyAuth,
                                  authBuffer,
                                  sizeof(authBuffer),
                                  responseAuthReference,
                                  sizeof(responseAuthReference),
                                  (PUINT32)&cbRequired)))
    {
        goto Cleanup;
    }

    // Verify response authorization
    if(memcmp(responseAuthReference, pResponseAuth, sizeof(responseAuthReference)) != 0)
    {
        hr = E_FAIL;
        goto Cleanup;
    }

    // Calculate Quote output buffer
    cbRequired = sizeof(UINT16) +     // TPM_TAG_QUOTE_INFO2
                 sizeof(UINT32) +     // ‘QUT2’
                 SHA1_DIGEST_SIZE +   // externalData
                 cbPcrData +          // infoShort
                 cbVersionInfo +      // versionInfo
                 cbSignature;         // Signature
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
    if(FAILED(hr = WriteBigEndian(pbQuote, cbQuote, pcbResult, (UINT16)0x0036)))//TPM_TAG_QUOTE_INFO2
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbQuote, cbQuote, pcbResult, (UINT32)'QUT2')))//‘QUT2’
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbQuote, cbQuote, pcbResult, pbNonce, cbNonce)))//externalData
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbQuote, cbQuote, pcbResult, pbPcrData, cbPcrData)))//infoShort
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbQuote, cbQuote, pcbResult, pbVersionInfo, cbVersionInfo)))//versionInfo
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
GetPlatformPcrs12(
    TBS_HCONTEXT hPlatformTbsHandle,
    _Out_writes_to_opt_(cbOutput, *pcbResult) PBYTE pbOutput,
    UINT32 cbOutput,
    _Out_ PUINT32 pcbResult
    )
{
    HRESULT hr = S_OK;
    BYTE cmd[] = {0x00, 0xc1,              //TPM_TAG_RQU_COMMAND
                  0x00, 0x00, 0x00, 0x0e,  //paramSize
                  0x00, 0x00, 0x00, 0x15,  //TPM_ORD_PCRRead
                  0x00, 0x00, 0x00, 0x00}; // pcrIndex
    BYTE rsp[0x200] = {0};
    UINT32 cbRsp = sizeof(rsp);
    UINT32 cursor = 0;
    UINT32 cursorOutput = 0;
    UINT32 cbRequired = 0;
    UINT32 returnCode = 0;
    UINT32 paramSize = 0;

    // Check the parameters
    if(pcbResult == NULL)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    *pcbResult = 0;

    // Calculate output buffer
    cbRequired = AVAILABLE_PLATFORM_PCRS * SHA1_DIGEST_SIZE;
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

    for(UINT32 n = 0; n < AVAILABLE_PLATFORM_PCRS; n++)
    {
        PBYTE pbDigest = NULL;
        UINT16 cbDigest = SHA1_DIGEST_SIZE;

        cursor = 0x000a; // Location of pcrIndex in command
        if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursor, n))) // Fill pcrIndex in command
        {
            goto Cleanup;
        }

        // Send the command to the TPM
        cbRsp = sizeof(rsp);
        if(FAILED(hr = Tbsip_Submit_Command(hPlatformTbsHandle,
                                            TBS_COMMAND_LOCALITY_ZERO,
                                            TBS_COMMAND_PRIORITY_NORMAL,
                                            cmd,
                                            sizeof(cmd),
                                            rsp,
                                            &cbRsp)))
        {
            goto Cleanup;
        }

        // Parse the response
        cursor = 0;
        if(FAILED(hr = SkipBigEndian(rsp, cbRsp, &cursor, sizeof(UINT16)))) // skip tag
        {
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursor, &paramSize))) // paramSize
        {
            goto Cleanup;
        }
        if(paramSize != cbRsp)
        {
            hr = E_FAIL;
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursor, &returnCode))) // ReturnCode
        {
            goto Cleanup;
        }
        if(returnCode != 0)
        {
            hr = E_FAIL;
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursor, &pbDigest, cbDigest))) // outDigest
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursorOutput, pbDigest, cbDigest)))
        {
            goto Cleanup;
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

HRESULT
ValidateQuoteContext12(
    _In_reads_(cbQuote) PBYTE pbQuote,
    UINT32 cbQuote,
    _In_reads_(cbPcrList) PBYTE pbPcrList,
    UINT32 cbPcrList,
    _In_reads_opt_ (cbNonce) PBYTE pbNonce,
    UINT32 cbNonce,
    _Out_ PUINT32 pPcrMask
    )
{
    HRESULT hr = S_OK;
    UINT32 cursorQuote = 0;
    UINT16 attestTag = 0;
    UINT32 fixed = 0;
    UINT16 sizeOfSelect = 0;
    PBYTE pPcrMaskQuote = NULL;
    UINT32 pcrMaskQuote = 0;
    UINT16 cbPcrDigest = SHA1_DIGEST_SIZE;
    PBYTE pbPcrDigest = 0;
    UINT16 cbVendorSpecific = 0;
    BYTE pcrReferenceBuffer[sizeof(UINT16) +   // SizeOfSelect
                            sizeof(BYTE) * 3 + // select
                            sizeof(UINT32) +   // valueSize
                            AVAILABLE_PLATFORM_PCRS * SHA1_DIGEST_SIZE] = {0};
    UINT32 cursorPcrReferenceBuffer = 0;
    UINT32 cbPcrReferenceDigest = 0;
    BYTE pcrReferenceDigest[SHA1_DIGEST_SIZE] = {0};

    // Check the parameters
    if((pbQuote == NULL) || (cbQuote == 0) ||
       (pbPcrList == NULL) || (cbPcrList != AVAILABLE_PLATFORM_PCRS * SHA1_DIGEST_SIZE) ||
       (pPcrMask == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Open up the TPM_QUOTE_INFO2 structure:
    if(FAILED(hr = ReadBigEndian(pbQuote, cbQuote, &cursorQuote, &attestTag))) // magic
    {
        goto Cleanup;
    }
    if(attestTag != 0x0036) //TPM_TAG_QUOTE_INFO2
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbQuote, cbQuote, &cursorQuote, &fixed))) // type
    {
        goto Cleanup;
    }
    if(fixed != 'QUT2') //fixed value 'QUT2'
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    if((pbNonce != NULL) && (cbNonce == SHA1_DIGEST_SIZE))
    {
        // Validate the nonce
        PBYTE pbExternalData = 0;
        if(FAILED(hr = ReadBigEndian(pbQuote, cbQuote, &cursorQuote, &pbExternalData, SHA1_DIGEST_SIZE))) // externalData
        {
            goto Cleanup;
        }
        if(memcmp(pbExternalData, pbNonce, SHA1_DIGEST_SIZE) != 0)
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }
    }
    else
    {
        // Ignore the nonce
        if(FAILED(hr = SkipBigEndian(pbQuote, cbQuote, &cursorQuote, SHA1_DIGEST_SIZE))) // externalData
        {
            goto Cleanup;
        }
    }
    if(FAILED(hr = ReadBigEndian(pbQuote, cbQuote, &cursorQuote, &sizeOfSelect))) // sizeOfSelect
    {
        goto Cleanup;
    }
    if(sizeOfSelect != 0x0003)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbQuote, cbQuote, &cursorQuote, &pPcrMaskQuote, sizeOfSelect))) // pcrMask
    {
        goto Cleanup;
    }
    for(UINT32 n = 0; n < sizeOfSelect; n++)
    {
        pcrMaskQuote |= pPcrMaskQuote[n] << (8 * n);
    }
    if(FAILED(hr = SkipBigEndian(pbQuote, cbQuote, &cursorQuote, sizeof(BYTE)))) // skip localityAtRelease
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbQuote, cbQuote, &cursorQuote, &pbPcrDigest, cbPcrDigest))) // digestAtRelease
    {
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(pbQuote, cbQuote, &cursorQuote, sizeof(UINT16) +    // tag
                                                                 sizeof(UINT32) +    // version
                                                                 sizeof(UINT16) +    // specLevel
                                                                 sizeof(BYTE) +      // errataRev
                                                                 sizeof(BYTE) * 4))) // tpmVendorID
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbQuote, cbQuote, &cursorQuote, &cbVendorSpecific))) // vendorSpecificSize
    {
        goto Cleanup;
    }
    if(cbVendorSpecific != 0)
    {
        if(FAILED(hr = SkipBigEndian(pbQuote, cbQuote, &cursorQuote, cbVendorSpecific))) // vendorSpecific
        {
            goto Cleanup;
        }
    }

    // Ensure that we consumed the entire quote
    if(cbQuote != cursorQuote)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Validate the PCRlist against the Quote
    // Read the pcr select indicated PCR values from the list and copy them into the TPM_PCR_COMPOSITE
    if(FAILED(hr = WriteBigEndian(pcrReferenceBuffer, sizeof(pcrReferenceBuffer), &cursorPcrReferenceBuffer, sizeOfSelect)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pcrReferenceBuffer, sizeof(pcrReferenceBuffer), &cursorPcrReferenceBuffer, pPcrMaskQuote, sizeOfSelect)))
    {
        goto Cleanup;
    }
    // We will fill in the Upper limit for now and come back here to fill in the exact size
    UINT32 valueSizeOffset = cursorPcrReferenceBuffer;
    if(FAILED(hr = WriteBigEndian(pcrReferenceBuffer, sizeof(pcrReferenceBuffer), &cursorPcrReferenceBuffer, (UINT32)AVAILABLE_PLATFORM_PCRS * SHA1_DIGEST_SIZE)))
    {
        goto Cleanup;
    }
    UINT32 pcrCompositHdrSize = cursorPcrReferenceBuffer;
    for(UINT32 n = 0; n < AVAILABLE_PLATFORM_PCRS; n++)
    {
        // Is PCR[n] selected?
        if((pcrMaskQuote & (0x00000001 << n)) != 0)
        {
            // Copy to reference buffer
            if(FAILED(hr = WriteBigEndian(pcrReferenceBuffer, sizeof(pcrReferenceBuffer), &cursorPcrReferenceBuffer, &pbPcrList[n * SHA1_DIGEST_SIZE], SHA1_DIGEST_SIZE)))
            {
                goto Cleanup;
            }
        }
    }
    // Set the exact size
    if(FAILED(hr = WriteBigEndian(pcrReferenceBuffer, sizeof(pcrReferenceBuffer), &valueSizeOffset, (UINT32)cursorPcrReferenceBuffer - pcrCompositHdrSize)))
    {
        goto Cleanup;
    }

    // Calculate the pcrDigest
    if(FAILED(hr = TpmAttiShaHash(
                        BCRYPT_SHA1_ALGORITHM,
                        NULL,
                        0,
                        pcrReferenceBuffer,
                        cursorPcrReferenceBuffer,
                        pcrReferenceDigest,
                        sizeof(pcrReferenceDigest),
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
CertifyKey12(
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
    BYTE paramHash[0x200] = {0};
    UINT32 cbRsp = sizeof(rsp);
    UINT32 cursorCmd = 0;
    UINT32 cursorParamHash = 0;
    UINT32 cursorRsp = 0;
    UINT32 authHandle1 = 0;
    BYTE authBuffer1[3 * SHA1_DIGEST_SIZE + sizeof(BYTE)] = {0};
    PBYTE pParamDigest1 = &authBuffer1[0];
    PBYTE pNonceEven1 = &authBuffer1[SHA1_DIGEST_SIZE];
    PBYTE pNonceOdd1 = &authBuffer1[2 * SHA1_DIGEST_SIZE];
    PBYTE pContinueAuthSession1 = &authBuffer1[3 * SHA1_DIGEST_SIZE];
    UINT32 authHandle2 = 0;
    BYTE authBuffer2[3 * SHA1_DIGEST_SIZE + sizeof(BYTE)] = {0};
    PBYTE pParamDigest2 = &authBuffer2[0];
    PBYTE pNonceEven2 = &authBuffer2[SHA1_DIGEST_SIZE];
    PBYTE pNonceOdd2 = &authBuffer2[2 * SHA1_DIGEST_SIZE];
    PBYTE pContinueAuthSession2 = &authBuffer2[3 * SHA1_DIGEST_SIZE];
    PBYTE pResponseAuth = NULL;
    BYTE responseAuthReference[SHA1_DIGEST_SIZE] = {0};

    UINT16 rspTag = 0;
    UINT16 certifyType = 0;
    UINT32 paramSize = 0;
    UINT32 returnCode = 0;
    UINT32 cbCertify = 0;
    PBYTE pbCertify = NULL;
    UINT32 cbSignature = 0;
    PBYTE pbSignature = NULL;

    // Check the parameters
    if((hPlatformTbsHandle == NULL) ||
       (hPlatformAikHandle == NULL) ||
       (hPlatformKeyHandle == NULL) ||
       (pcbResult == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    *pcbResult = 0;

    // Figure out the shenanigans that the TPM puts upon us if we have to call CertifyKey or CertifyKey2:
    // TPM_CertifyKey does not support the case where (a) the certifying key requires a usage
    // authorization to be provided but (b) the key-to-be-certified does not. In such cases,
    // TPM_CertifyKey2 must be used.
    if(!((pbKeyUsageAuth == NULL) && (pbAikUsageAuth != NULL)))
    {
        if((pbKeyUsageAuth != NULL) && (pbAikUsageAuth != NULL))
        {
            // Setup the key OIAP session
            if(FAILED(hr = StartOIAPSession(hPlatformTbsHandle, &authHandle1, pNonceEven1, pNonceOdd1)))
            {
                goto Cleanup;
            }
            // Setup the AIK OIAP session
            if(FAILED(hr = StartOIAPSession(hPlatformTbsHandle, &authHandle2, pNonceEven2, pNonceOdd2)))
            {
                goto Cleanup;
            }

            // Build CertifyKey command buffer
            if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)0x00c3))) //TPM_TAG_RQU_AUTH2_COMMAND since we have two authorizations
            {
                goto Cleanup;
            }
        }
        else if((pbKeyUsageAuth != NULL) && (pbAikUsageAuth == NULL))
        {
            // Setup the key OIAP session
            if(FAILED(hr = StartOIAPSession(hPlatformTbsHandle, &authHandle1, pNonceEven1, pNonceOdd1)))
            {
                goto Cleanup;
            }

            // Build CertifyKey command buffer
            if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)0x00c2))) //TPM_TAG_RQU_AUTH2_COMMAND since we have one authorizations
            {
                goto Cleanup;
            }
        }
        else
        {
            // Build CertifyKey command buffer
            if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)0x00c1))) //TPM_TAG_RQU_COMMAND since we have no authorization
            {
                goto Cleanup;
            }
        }

        if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)0x00000026))) //paramSize
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)0x00000032))) //TPM_ORD_CertifyKey
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, hPlatformAikHandle))) //aikHandle
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, hPlatformKeyHandle))) //keyHandle
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, pbNonce, cbNonce))) //antiReplay
        {
            goto Cleanup;
        }

        if(authHandle1 != 0)
        {
            if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, authHandle1))) //authHandle
            {
                goto Cleanup;
            }
            if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, pNonceOdd1, SHA1_DIGEST_SIZE))) //nonceOdd
            {
                goto Cleanup;
            }
            if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, *pContinueAuthSession1))) //continueAuthSession
            {
                goto Cleanup;
            }

            // Calculate parameter digest
            if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, (UINT32)0x00000032))) //TPM_ORD_CertifyKey
            {
                goto Cleanup;
            }
            if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, pbNonce, cbNonce))) //antiReplay
            {
                goto Cleanup;
            }

            if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                          NULL,
                                          0,
                                          paramHash,
                                          cursorParamHash,
                                          pParamDigest1,
                                          SHA1_DIGEST_SIZE,
                                          (PUINT32)&cbRequired)))
            {
                goto Cleanup;
            }

            // Calculate command authorization 1
            if(sizeof(cmd) < cursorCmd + SHA1_DIGEST_SIZE)
            {
                hr = E_FAIL;
                goto Cleanup;
            }
            if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                          (authHandle2 == 0) ? pbKeyUsageAuth : pbAikUsageAuth,
                                          (authHandle2 == 0) ? cbKeyUsageAuth : cbAikUsageAuth,
                                          authBuffer1,
                                          sizeof(authBuffer1),
                                          &cmd[cursorCmd],
                                          SHA1_DIGEST_SIZE,
                                          (PUINT32)&cbRequired)))
            {
                goto Cleanup;
            }
            cursorCmd += SHA1_DIGEST_SIZE;

            if(authHandle2 != 0)
            {
                // Calculate command authorization 2
                if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, authHandle2))) //authHandle
                {
                    goto Cleanup;
                }
                if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, pNonceOdd2, SHA1_DIGEST_SIZE))) //nonceOdd
                {
                    goto Cleanup;
                }
                if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, *pContinueAuthSession2))) //continueAuthSession
                {
                    goto Cleanup;
                }
                if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                              NULL,
                                              0,
                                              paramHash,
                                              cursorParamHash,
                                              pParamDigest2,
                                              SHA1_DIGEST_SIZE,
                                              (PUINT32)&cbRequired)))
                {
                    goto Cleanup;
                }
                // Calculate and append command authorization 2
                if(sizeof(cmd) < cursorCmd + SHA1_DIGEST_SIZE)
                {
                    hr = E_FAIL;
                    goto Cleanup;
                }
                if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                              pbKeyUsageAuth,
                                              cbKeyUsageAuth,
                                              authBuffer2,
                                              sizeof(authBuffer2),
                                              &cmd[cursorCmd],
                                              SHA1_DIGEST_SIZE,
                                              (PUINT32)&cbRequired)))
                {
                    goto Cleanup;
                }
                cursorCmd += SHA1_DIGEST_SIZE;
            }
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
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &rspTag))) // tag
        {
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &paramSize))) // paramSize
        {
            goto Cleanup;
        }
        if(paramSize != cbRsp)
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

        // We can get a TPM_CERTIFY_INFO or a TPM_CERTIFY_INFO2 structure here.
        UINT32 peekCursor = cursorRsp;
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &peekCursor, &certifyType))) // Read TPM_STRUCT_VER or TPM_STRUCTURE_TAG
        {
            goto Cleanup;
        }
        if(certifyType == 0x0101) //TPM_STRUCT_VER
        {
            // Oh what a mess: We have to parse the stucture to find it's total size
            cbCertify = sizeof(UINT32) + // TPM_STRUCT_VER
                        sizeof(UINT16) + // TPM_KEY_USAGE
                        sizeof(UINT32) + // TPM_KEY_FLAGS
                        sizeof(BYTE) +   // TPM_AUTH_DATA_USAGE
                        sizeof(UINT32) + // TPM_KEY_PARMS.TPM_ALGORITHM_ID
                        sizeof(UINT16) + // TPM_KEY_PARMS.TPM_ENC_SCHEME
                        sizeof(UINT16);  // TPM_KEY_PARMS.TPM_SIG_SCHEME
            peekCursor = cursorRsp + cbCertify;
            if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &peekCursor, &cbRequired))) // TPM_KEY_PARMS.parmSize
            {
                goto Cleanup;
            }
            cbCertify += sizeof(UINT32) +   // TPM_KEY_PARMS.parmSize
                         cbRequired +       // TPM_KEY_PARMS.parms
                         SHA1_DIGEST_SIZE + // pubkeyDigest
                         SHA1_DIGEST_SIZE + // data
                         sizeof(BYTE);      // parentPCRStatus
            peekCursor = cursorRsp + cbCertify;
            if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &peekCursor, &cbRequired))) // PCRInfoSize
            {
                goto Cleanup;
            }
            cbCertify += sizeof(UINT32) +   // PCRInfoSize
                         cbRequired;        // PCRInfo
            if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &pbCertify, cbCertify))) // finally read the entire certify as one block
            {
                goto Cleanup;
            }
        }
        else if(certifyType == 0x0029) //TPM_TAG_CERTIFY_INFO2
        {
            // Oh what a mess part 2: We have to parse the stucture to find it's total size
            cbCertify = sizeof(UINT16) + // TPM_STRUCTURE_TAG
                        sizeof(BYTE) +   // fill
                        sizeof(BYTE) +   // TPM_PAYLOAD_TYPE
                        sizeof(UINT16) + // TPM_KEY_USAGE
                        sizeof(UINT32) + // TPM_KEY_FLAGS
                        sizeof(BYTE) +   // TPM_AUTH_DATA_USAGE
                        sizeof(UINT32) + // TPM_KEY_PARMS.TPM_ALGORITHM_ID
                        sizeof(UINT16) + // TPM_KEY_PARMS.TPM_ENC_SCHEME
                        sizeof(UINT16);  // TPM_KEY_PARMS.TPM_SIG_SCHEME
            peekCursor = cursorRsp + cbCertify;
            if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &peekCursor, &cbRequired))) // TPM_KEY_PARMS.parmSize
            {
                goto Cleanup;
            }
            cbCertify += sizeof(UINT32) +   // TPM_KEY_PARMS.parmSize
                         cbRequired +       // TPM_KEY_PARMS.parms
                         SHA1_DIGEST_SIZE + // pubkeyDigest
                         SHA1_DIGEST_SIZE + // data
                         sizeof(BYTE);      // parentPCRStatus
            peekCursor = cursorRsp + cbCertify;
            if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &peekCursor, &cbRequired))) // PCRInfoSize
            {
                goto Cleanup;
            }
            cbCertify += sizeof(UINT32) +   // PCRInfoSize
                         cbRequired;        // PCRInfo
            peekCursor = cursorRsp + cbCertify;
            if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &peekCursor, &cbRequired))) // migrationAuthoritySize
            {
                goto Cleanup;
            }
            cbCertify += sizeof(UINT32) +   // migrationAuthoritySize
                         cbRequired;        // migrationAuthority
            if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &pbCertify, cbCertify))) // finally read the entire certify as one block
            {
                goto Cleanup;
            }
        }
        else
        {
            hr = E_FAIL;
            goto Cleanup;
        }

        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &cbSignature))) // outDataSize
        {
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &pbSignature, cbSignature))) // outData
        {
            goto Cleanup;
        }

        if(rspTag != 0x00c4)
        {
            // Calculate parameter digest
            cursorParamHash = 0;
            memset(paramHash, 0x00, sizeof(paramHash));
            if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, returnCode))) //returnCode
            {
                goto Cleanup;
            }
            if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, (UINT32)0x00000032))) //TPM_ORD_CertifyKey
            {
                goto Cleanup;
            }
            if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, pbCertify, cbCertify))) //CertifyInfo
            {
                goto Cleanup;
            }
            if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, cbSignature))) //outDataSize
            {
                goto Cleanup;
            }
            if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, pbSignature, cbSignature))) //outData
            {
                goto Cleanup;
            }

            // Process session information from the response
            if(((rspTag == 0x00c5) && ((cursorRsp + 2 * SHA1_DIGEST_SIZE + sizeof(BYTE)) != cbRsp)) ||
               ((rspTag == 0x00c6) && ((cursorRsp + 4 * SHA1_DIGEST_SIZE + 2* sizeof(BYTE)) != cbRsp)))
            {
                hr = E_FAIL;
                goto Cleanup;
            }

            if(memcpy_s(pNonceEven1, sizeof(authBuffer1) - (pNonceEven1 - authBuffer1), &rsp[cursorRsp], SHA1_DIGEST_SIZE))
            {
                hr = E_FAIL;
                goto Cleanup;
            }

            cursorRsp += SHA1_DIGEST_SIZE;
            *pContinueAuthSession1 = rsp[cursorRsp];
            cursorRsp += sizeof(BYTE);
            pResponseAuth = &rsp[cursorRsp];
            cursorRsp += SHA1_DIGEST_SIZE;

            if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                          NULL,
                                          0,
                                          paramHash,
                                          cursorParamHash,
                                          pParamDigest1,
                                          SHA1_DIGEST_SIZE,
                                          (PUINT32)&cbRequired)))
            {
                goto Cleanup;
            }

            // Calculate response authorization
            if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                          (authHandle2 == 0) ? pbKeyUsageAuth : pbAikUsageAuth,
                                          (authHandle2 == 0) ? cbKeyUsageAuth : cbAikUsageAuth,
                                          authBuffer1,
                                          sizeof(authBuffer1),
                                          responseAuthReference,
                                          sizeof(responseAuthReference),
                                          (PUINT32)&cbRequired)))
            {
                goto Cleanup;
            }

            // Verify response authorization
            if(memcmp(responseAuthReference, pResponseAuth, sizeof(responseAuthReference)) != 0)
            {
                hr = E_FAIL;
                goto Cleanup;
            }

            if(rspTag == 0x00c6)
            {
                if(memcpy_s(pNonceEven2, sizeof(authBuffer2) - (pNonceEven2 - authBuffer2), &rsp[cursorRsp], SHA1_DIGEST_SIZE))
                {
                    hr = E_FAIL;
                    goto Cleanup;
                }
                cursorRsp += SHA1_DIGEST_SIZE;
                *pContinueAuthSession2 = rsp[cursorRsp];
                cursorRsp += sizeof(BYTE);
                pResponseAuth = &rsp[cursorRsp];
                cursorRsp += SHA1_DIGEST_SIZE;

                if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                              NULL,
                                              0,
                                              paramHash,
                                              cursorParamHash,
                                              pParamDigest2,
                                              SHA1_DIGEST_SIZE,
                                              (PUINT32)&cbRequired)))
                {
                    goto Cleanup;
                }

                // Calculate response authorization
                if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                              pbKeyUsageAuth,
                                              cbKeyUsageAuth,
                                              authBuffer2,
                                              sizeof(authBuffer2),
                                              responseAuthReference,
                                              sizeof(responseAuthReference),
                                              (PUINT32)&cbRequired)))
                {
                    goto Cleanup;
                }

                // Verify response authorization
                if(memcmp(responseAuthReference, pResponseAuth, sizeof(responseAuthReference)) != 0)
                {
                    hr = E_FAIL;
                    goto Cleanup;
                }
            }
        }
    }
    else
    {
/*
        if((pbKeyUsageAuth != NULL) && (pbAikUsageAuth != NULL))
        {
            // Setup the key OIAP session
            if(FAILED(hr = StartOIAPSession(hPlatformTbsHandle, &authHandle1, pNonceEven1, pNonceOdd1)))
            {
                goto Cleanup;
            }
            // Setup the AIK OIAP session
            if(FAILED(hr = StartOIAPSession(hPlatformTbsHandle, &authHandle2, pNonceEven2, pNonceOdd2)))
            {
                goto Cleanup;
            }

            // Build CertifyKey2 command buffer
            if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)0x00c3))) //TPM_TAG_RQU_AUTH2_COMMAND since we have two authorizations
            {
                goto Cleanup;
            }
        }
        else if((pbKeyUsageAuth == NULL) && (pbAikUsageAuth != NULL))
        {
*/
            // Setup the AIK OIAP session
            if(FAILED(hr = StartOIAPSession(hPlatformTbsHandle, &authHandle1, pNonceEven1, pNonceOdd1)))
            {
                goto Cleanup;
            }

            // Build CertifyKey2 command buffer
            if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)0x00c2))) //TPM_TAG_RQU_AUTH2_COMMAND since we have one authorization
            {
                goto Cleanup;
            }
/*
        }
        else
        {
            // Build CertifyKey2 command buffer
            if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT16)0x00c1))) //TPM_TAG_RQU_COMMAND since we have no authorization
            {
                goto Cleanup;
            }
        }
*/
        if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)0x00000000))) //paramSize place holder
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, (UINT32)0x00000033))) //TPM_ORD_CertifyKey2
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
        if(FAILED(hr = SkipBigEndian(cmd, sizeof(cmd), &cursorCmd, SHA1_DIGEST_SIZE))) //migrationPubDigest = 0
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, pbNonce, cbNonce))) //antiReplay
        {
            goto Cleanup;
        }

        if(authHandle1 != 0)
        {
            // Calculate parameter digest
            if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, (UINT32)0x00000033))) //TPM_ORD_CertifyKey2
            {
                goto Cleanup;
            }
            if(FAILED(hr = SkipBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, SHA1_DIGEST_SIZE))) //migrationPubDigest = 0
            {
                goto Cleanup;
            }
            if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, pbNonce, cbNonce))) //antiReplay
            {
                goto Cleanup;
            }

            if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, authHandle1))) //authHandle
            {
                goto Cleanup;
            }
            if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, pNonceOdd1, SHA1_DIGEST_SIZE))) //nonceOdd
            {
                goto Cleanup;
            }
            if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, *pContinueAuthSession1))) //continueAuthSession
            {
                goto Cleanup;
            }
            if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                      NULL,
                                      0,
                                      paramHash,
                                      cursorParamHash,
                                      pParamDigest1,
                                      SHA1_DIGEST_SIZE,
                                      (PUINT32)&cbRequired)))
            {
                goto Cleanup;
            }

            // Calculate and append command authorization 1
            if(sizeof(cmd) < cursorCmd + SHA1_DIGEST_SIZE)
            {
                hr = E_FAIL;
                goto Cleanup;
            }
            if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                          (authHandle2 == 0) ? pbAikUsageAuth : pbKeyUsageAuth,
                                          (authHandle2 == 0) ? cbAikUsageAuth : cbKeyUsageAuth,
                                          authBuffer1,
                                          sizeof(authBuffer1),
                                          &cmd[cursorCmd],
                                          SHA1_DIGEST_SIZE,
                                          (PUINT32)&cbRequired)))
            {
                goto Cleanup;
            }
            cursorCmd += SHA1_DIGEST_SIZE;

            if(authHandle2 != 0)
            {
                if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, authHandle2))) //authHandle
                {
                    goto Cleanup;
                }
                if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, pNonceOdd2, SHA1_DIGEST_SIZE))) //nonceOdd
                {
                    goto Cleanup;
                }
                if(FAILED(hr = WriteBigEndian(cmd, sizeof(cmd), &cursorCmd, *pContinueAuthSession2))) //continueAuthSession
                {
                    goto Cleanup;
                }
                if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                          NULL,
                                          0,
                                          paramHash,
                                          cursorParamHash,
                                          pParamDigest2,
                                          SHA1_DIGEST_SIZE,
                                          (PUINT32)&cbRequired)))
                {
                    goto Cleanup;
                }
                // Calculate and append command authorization 2
                if(sizeof(cmd) < cursorCmd + SHA1_DIGEST_SIZE)
                {
                    hr = E_FAIL;
                    goto Cleanup;
                }
                if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                              pbAikUsageAuth,
                                              cbAikUsageAuth,
                                              authBuffer2,
                                              sizeof(authBuffer2),
                                              &cmd[cursorCmd],
                                              SHA1_DIGEST_SIZE,
                                              (PUINT32)&cbRequired)))
                {
                    goto Cleanup;
                }
                cursorCmd += SHA1_DIGEST_SIZE;
            }
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
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &rspTag))) // tag
        {
            goto Cleanup;
        }
        if((rspTag != 0x00c4) &&
           (rspTag != 0x00c5) &&
           (rspTag != 0x00c6))
        {
            hr = E_FAIL;
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &paramSize))) // paramSize
        {
            goto Cleanup;
        }
        if(paramSize != cbRsp)
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
        // Oh what a mess: We have to parse the stucture to find it's total size
        cbCertify = sizeof(UINT16) + // TPM_STRUCTURE_TAG
                    sizeof(BYTE) +   // fill
                    sizeof(BYTE) +   // TPM_PAYLOAD_TYPE
                    sizeof(UINT16) + // TPM_KEY_USAGE
                    sizeof(UINT32) + // TPM_KEY_FLAGS
                    sizeof(BYTE) +   // TPM_AUTH_DATA_USAGE
                    sizeof(UINT32) + // TPM_KEY_PARMS.TPM_ALGORITHM_ID
                    sizeof(UINT16) + // TPM_KEY_PARMS.TPM_ENC_SCHEME
                    sizeof(UINT16);  // TPM_KEY_PARMS.TPM_SIG_SCHEME
        UINT32 peekCursor = cursorRsp + cbCertify;
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &peekCursor, &cbRequired))) // TPM_KEY_PARMS.parmSize
        {
            goto Cleanup;
        }
        cbCertify += sizeof(UINT32) +   // TPM_KEY_PARMS.parmSize
                     cbRequired +       // TPM_KEY_PARMS.parms
                     SHA1_DIGEST_SIZE + // pubkeyDigest
                     SHA1_DIGEST_SIZE + // data
                     sizeof(BYTE);      // parentPCRStatus
        peekCursor = cursorRsp + cbCertify;
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &peekCursor, &cbRequired))) // PCRInfoSize
        {
            goto Cleanup;
        }
        cbCertify += sizeof(UINT32) +   // PCRInfoSize
                     cbRequired;        // PCRInfo
        peekCursor = cursorRsp + cbCertify;
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &peekCursor, &cbRequired))) // migrationAuthoritySize
        {
            goto Cleanup;
        }
        cbCertify += sizeof(UINT32) +   // migrationAuthoritySize
                     cbRequired;        // migrationAuthority
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &pbCertify, cbCertify))) // finally read the entire certify as one block
        {
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &cbSignature))) // outDataSize
        {
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(rsp, cbRsp, &cursorRsp, &pbSignature, cbSignature))) // outData
        {
            goto Cleanup;
        }

        if(rspTag != 0x00c4)
        {
            // Calculate parameter digest
            cursorParamHash = 0;
            memset(paramHash, 0x00, sizeof(paramHash));
            if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, returnCode))) //returnCode
            {
                goto Cleanup;
            }
            if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, (UINT32)0x00000033))) //TPM_ORD_CertifyKey2
            {
                goto Cleanup;
            }
            if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, pbCertify, cbCertify))) //CertifyInfo
            {
                goto Cleanup;
            }
            if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, cbSignature))) //outDataSize
            {
                goto Cleanup;
            }
            if(FAILED(hr = WriteBigEndian(paramHash, sizeof(paramHash), &cursorParamHash, pbSignature, cbSignature))) //outData
            {
                goto Cleanup;
            }

            // Process session information from the response
            if(((rspTag == 0x00c5) && ((cursorRsp + 2 * SHA1_DIGEST_SIZE + sizeof(BYTE)) != cbRsp)) ||
               ((rspTag == 0x00c6) && ((cursorRsp + 4 * SHA1_DIGEST_SIZE + 2* sizeof(BYTE)) != cbRsp)))
            {
                hr = E_FAIL;
                goto Cleanup;
            }

            if(memcpy_s(pNonceEven1, sizeof(authBuffer1) - (pNonceEven1 - authBuffer1), &rsp[cursorRsp], SHA1_DIGEST_SIZE))
            {
                hr = E_FAIL;
                goto Cleanup;
            }

            cursorRsp += SHA1_DIGEST_SIZE;
            *pContinueAuthSession1 = rsp[cursorRsp];
            cursorRsp += sizeof(BYTE);
            pResponseAuth = &rsp[cursorRsp];
            cursorRsp += SHA1_DIGEST_SIZE;

            if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                          NULL,
                                          0,
                                          paramHash,
                                          cursorParamHash,
                                          pParamDigest1,
                                          SHA1_DIGEST_SIZE,
                                          (PUINT32)&cbRequired)))
            {
                goto Cleanup;
            }

            // Calculate response authorization
            if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                          authHandle2 ? pbKeyUsageAuth : pbAikUsageAuth,
                                          authHandle2 ? cbKeyUsageAuth : cbAikUsageAuth,
                                          authBuffer1,
                                          sizeof(authBuffer1),
                                          responseAuthReference,
                                          sizeof(responseAuthReference),
                                          (PUINT32)&cbRequired)))
            {
                goto Cleanup;
            }

            // Verify response authorization
            if(memcmp(responseAuthReference, pResponseAuth, sizeof(responseAuthReference)) != 0)
            {
                hr = E_FAIL;
                goto Cleanup;
            }

            if(rspTag == 0x00c6)
            {
                if(memcpy_s(pNonceEven2, sizeof(authBuffer2) - (pNonceEven2 - authBuffer2), &rsp[cursorRsp], SHA1_DIGEST_SIZE))
                {
                    hr = E_FAIL;
                    goto Cleanup;
                }
                cursorRsp += SHA1_DIGEST_SIZE;
                *pContinueAuthSession2 = rsp[cursorRsp];
                cursorRsp += sizeof(BYTE);
                pResponseAuth = &rsp[cursorRsp];
                cursorRsp += SHA1_DIGEST_SIZE;

                if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                              NULL,
                                              0,
                                              paramHash,
                                              cursorParamHash,
                                              pParamDigest2,
                                              SHA1_DIGEST_SIZE,
                                              (PUINT32)&cbRequired)))
                {
                    goto Cleanup;
                }

                // Calculate response authorization
                if(FAILED(hr = TpmAttiShaHash(BCRYPT_SHA1_ALGORITHM,
                                              pbAikUsageAuth,
                                              cbAikUsageAuth,
                                              authBuffer2,
                                              sizeof(authBuffer2),
                                              responseAuthReference,
                                              sizeof(responseAuthReference),
                                              (PUINT32)&cbRequired)))
                {
                    goto Cleanup;
                }

                // Verify response authorization
                if(memcmp(responseAuthReference, pResponseAuth, sizeof(responseAuthReference)) != 0)
                {
                    hr = E_FAIL;
                    goto Cleanup;
                }
            }
        }
    }
    // Calculate certification output buffer
    cbRequired = cbCertify +  // versionInfo
                 cbSignature; // Signature
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

    *pcbResult = cbRequired;
    if(memcpy_s(pbOutput, cbOutput, pbCertify, cbCertify))
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    if(memcpy_s(&pbOutput[cbCertify], cbOutput - cbCertify, pbSignature, cbSignature))
    {
        hr = E_FAIL;
        goto Cleanup;
    }

Cleanup:
    return hr;
}

HRESULT
ValidateKeyAttest12(
    _In_reads_(cbKeyAttest) PBYTE pbKeyAttest,
    UINT32 cbKeyAttest,
    _In_reads_opt_(cbNonce) PBYTE pbNonce,
    UINT32 cbNonce,
    _In_reads_(cbKeyAttest) PBYTE pbKeyBlob,
    UINT32 cbKeyBlob,
    UINT32 pcrMask,
    _In_reads_opt_(AVAILABLE_PLATFORM_PCRS * SHA1_DIGEST_SIZE) PBYTE pcrTable
    )
{
    HRESULT hr = S_OK;
    UINT32 attestCursor = 0;
    UINT32 keyCursor = 0;
    UINT32 required = 0;
    UINT16 structureTag = 0;
    UINT32 cbAttestNonce = 0;
    PBYTE  pbAttestNonce = NULL;
    UINT32 cbPubkeyDigest = 0;
    PBYTE pbPubkeyDigest = NULL;
    UINT32 attestPcrMask = 0;
    UINT32 cbAttestPcrDigest = 0;
    PBYTE pbAttestPcrDigest = NULL;
    UINT32 keyPcrMask = 0;
    UINT32 cbKeyPcrDigest = 0;
    PBYTE pbKeyPcrDigest = NULL;
    UINT32 cbPubkey = 0;
    PBYTE pbPubkey = NULL;
    UINT32 cbStorePubkey = 0;
    PBYTE pbStorePubkey = NULL;
    BYTE pubkeyDigestReference[SHA1_DIGEST_SIZE] = {0};
    BYTE pcrCompositeDigestReference[SHA1_DIGEST_SIZE] = {0};

    UINT32 parmSize = 0;
    UINT32 pcrInfoSize = 0;
    UINT16 pcrSelectSize = 0;
    UINT32 migrationAuthoritySize = 0;
    UINT32 encDataSize = 0;

    // Check parameters
    if((pbKeyAttest == NULL) ||
       (cbKeyAttest == 0) ||
       (pbKeyBlob == NULL) ||
       (cbKeyBlob == 0))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Do we have a TPM_CERTIFY_INFO or TPM_CERTIFY_INFO2 Structure?
    if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &structureTag))) // structureTag
    {
        goto Cleanup;
    }
    if(structureTag == 0x0101) //TPM_CERTIFY_INFO structure
    {
        // Extract from the TPM_CERTIFY_INFO structure the data we have to validate
        UINT32 parmSize = 0;
        UINT32 pcrInfoSize = 0;
        UINT16 pcrSelectSize = 0;
        if(FAILED(hr = SkipBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, sizeof(UINT16) +  // remainder of TPM_STRUCT_VER
                                                                              sizeof(UINT16) +  // TPM_KEY_USAGE
                                                                              sizeof(UINT32) +  // TPM_KEY_FLAGS
                                                                              sizeof(BYTE) +    // TPM_AUTH_DATA_USAGE
                                                                              sizeof(UINT32) +  // TPM_KEY_PARMS.TPM_ALGORITHM_ID
                                                                              sizeof(UINT16) +  // TPM_KEY_PARMS.TPM_ENC_SCHEME
                                                                              sizeof(UINT16)))) // TPM_KEY_PARMS.TPM_SIG_SCHEME
        {
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &parmSize))) // parmSize
        {
            goto Cleanup;
        }
        if(FAILED(hr = SkipBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, parmSize))) // parms
        {
            goto Cleanup;
        }
        cbPubkeyDigest = SHA1_DIGEST_SIZE;
        if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &pbPubkeyDigest, cbPubkeyDigest))) // pubkey name
        {
            goto Cleanup;
        }
        cbAttestNonce = SHA1_DIGEST_SIZE;
        if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &pbAttestNonce, cbAttestNonce))) // data
        {
            goto Cleanup;
        }
        if(FAILED(hr = SkipBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, sizeof(BYTE)))) // parentPCRStatus
        {
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &pcrInfoSize))) // pcrInfoSize
        {
            goto Cleanup;
        }
        if(pcrInfoSize != 0) //TPM_PCR_INFO
        {
            if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &pcrSelectSize))) // pcrSelectSize
            {
                goto Cleanup;
            }
            if(pcrSelectSize > 0x0004)
            {
                hr = E_INVALIDARG;
                goto Cleanup;
            }
            for(DWORD n = 0; n < pcrSelectSize; n++)
            {
                if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &((PBYTE)&attestPcrMask)[n]))) // pcrSelect[n]
                {
                    goto Cleanup;
                }
            }
            cbAttestPcrDigest = SHA1_DIGEST_SIZE;
            if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &pbAttestPcrDigest, cbAttestPcrDigest))) // digestAtRelease
            {
                goto Cleanup;
            }
            if(FAILED(hr = SkipBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, SHA1_DIGEST_SIZE))) // digestAtCreation
            {
                goto Cleanup;
            }
        }
        // Ensure that there is no trailing data that has been signed
        if(attestCursor != cbKeyAttest)
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }
    }
    else if(structureTag == 0x0029) //TPM_CERTIFY_INFO2
    {
        // Extract from the TPM_CERTIFY_INFO structure the data we have to validate
        if(FAILED(hr = SkipBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, sizeof(BYTE) +    // fill
                                                                              sizeof(BYTE) +    // TPM_PAYLOAD_TYPE
                                                                              sizeof(UINT16) +  // TPM_KEY_USAGE
                                                                              sizeof(UINT32) +  // TPM_KEY_FLAGS
                                                                              sizeof(BYTE) +    // TPM_AUTH_DATA_USAGE
                                                                              sizeof(UINT32) +  // TPM_KEY_PARMS.TPM_ALGORITHM_ID
                                                                              sizeof(UINT16) +  // TPM_KEY_PARMS.TPM_ENC_SCHEME
                                                                              sizeof(UINT16)))) // TPM_KEY_PARMS.TPM_SIG_SCHEME
        {
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &parmSize))) // parmSize
        {
            goto Cleanup;
        }
        if(FAILED(hr = SkipBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, parmSize))) // parms
        {
            goto Cleanup;
        }
        cbPubkeyDigest = SHA1_DIGEST_SIZE;
        if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &pbPubkeyDigest, cbPubkeyDigest))) // pubkey name
        {
            goto Cleanup;
        }
        cbAttestNonce = SHA1_DIGEST_SIZE;
        if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &pbAttestNonce, cbAttestNonce))) // data
        {
            goto Cleanup;
        }
        if(FAILED(hr = SkipBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, sizeof(BYTE)))) // parentPCRStatus
        {
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &pcrInfoSize))) // pcrInfoSize
        {
            goto Cleanup;
        }
        if(pcrInfoSize != 0) //TPM_PCR_INFO_SHORT
        {
            if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &pcrSelectSize))) // pcrSelectSize
            {
                goto Cleanup;
            }
            if(pcrSelectSize != 0x0003)
            {
                hr = E_INVALIDARG;
                goto Cleanup;
            }
            if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &((PBYTE)&attestPcrMask)[0]))) // pcrSelect[0]
            {
                goto Cleanup;
            }
            if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &((PBYTE)&attestPcrMask)[1]))) // pcrSelect[1]
            {
                goto Cleanup;
            }
            if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &((PBYTE)&attestPcrMask)[2]))) // pcrSelect[2]
            {
                goto Cleanup;
            }
            if(FAILED(hr = SkipBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, sizeof(BYTE)))) // localityAtRelease
            {
                goto Cleanup;
            }
            cbAttestPcrDigest = SHA1_DIGEST_SIZE;
            if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &pbAttestPcrDigest, cbAttestPcrDigest))) // digestAtRelease
            {
                goto Cleanup;
            }
        }
        if(FAILED(hr = ReadBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, &migrationAuthoritySize))) // migrationAuthoritySize
        {
            goto Cleanup;
        }
        if(migrationAuthoritySize != 0)
        {
            if(FAILED(hr = SkipBigEndian(pbKeyAttest, cbKeyAttest, &attestCursor, migrationAuthoritySize))) // migrationAuthority
            {
                goto Cleanup;
            }
        }

        // Ensure that there is no trailing data that has been signed
        if(attestCursor != cbKeyAttest)
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }
    }
    else
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Extract the pubkey and the pcr binding from the key blob
    PPCP_KEY_BLOB pHeader = (PPCP_KEY_BLOB)&pbKeyBlob[keyCursor];
    if((pHeader == NULL) ||
       ((cbKeyBlob - keyCursor) < sizeof(PCP_KEY_BLOB)) ||
       (pHeader->magic != BCRYPT_PCP_KEY_MAGIC) ||
       (pHeader->cbHeader < sizeof(PCP_KEY_BLOB)) ||
       (pHeader->pcpType != PCPTYPE_TPM12) ||
       ((cbKeyBlob - keyCursor) < pHeader->cbHeader+
                                  pHeader->cbTpmKey))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    keyCursor += pHeader->cbHeader;
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &structureTag))) // structureTag
    {
        goto Cleanup;
    }
    if(structureTag != 0x0028) // TPM_TAG_KEY12 is the only supported key type by the provider
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, sizeof(UINT16) +  // fill
                                                                   sizeof(UINT16) +  // TPM_KEY_USAGE
                                                                   sizeof(UINT32) +  // TPM_KEY_FLAGS
                                                                   sizeof(BYTE) +    // TPM_AUTH_DATA_USAGE
                                                                   sizeof(UINT32) +  // TPM_KEY_PARMS.TPM_ALGORITHM_ID
                                                                   sizeof(UINT16) +  // TPM_KEY_PARMS.TPM_ENC_SCHEME
                                                                   sizeof(UINT16)))) // TPM_KEY_PARMS.TPM_SIG_SCHEME
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &parmSize))) // parmSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, parmSize))) // parms
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &pcrInfoSize))) // pcrInfoSize
    {
        goto Cleanup;
    }
    if(pcrInfoSize != 0) //TPM_PCR_INFO_LONG
    {
        if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, sizeof(UINT16) + // tag
                                                                       sizeof(BYTE) +   // localityAtCreation
                                                                       sizeof(BYTE))))  // localityAtRelease
        {
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &pcrSelectSize))) // creationPCRSelection.pcrSelectSize
        {
            goto Cleanup;
        }
        if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, pcrSelectSize))) // creationPCRSelection
        {
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &pcrSelectSize))) // releasePCRSelection.pcrSelectSize
        {
            goto Cleanup;
        }
        if(pcrSelectSize != 0x0003)
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &((PBYTE)&keyPcrMask)[0]))) // releasePCRSelection.pcrSelect[0]
        {
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &((PBYTE)&keyPcrMask)[1]))) // releasePCRSelection.pcrSelect[1]
        {
            goto Cleanup;
        }
        if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &((PBYTE)&keyPcrMask)[2]))) // releasePCRSelection.pcrSelect[2]
        {
            goto Cleanup;
        }
        if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, SHA1_DIGEST_SIZE))) // digestAtCreation
        {
            goto Cleanup;
        }
        cbKeyPcrDigest = SHA1_DIGEST_SIZE;
        if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &pbKeyPcrDigest, cbKeyPcrDigest))) // digestAtRelease
        {
            goto Cleanup;
        }
    }
    pbStorePubkey = &pbKeyBlob[keyCursor];
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &cbPubkey))) // keyLength
    {
        goto Cleanup;
    }
    cbStorePubkey = cbPubkey + sizeof(cbPubkey);
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &pbPubkey, cbPubkey))) // key
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &encDataSize))) // encDataSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, encDataSize))) // encData
    {
        goto Cleanup;
    }
    // Ensure that we processed the entire key
    if(keyCursor != cbKeyBlob)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Calculate the pubkey digest from the key blob to see if this attestation speaks about the attached key
    if(FAILED(hr = TpmAttiShaHash(
                        BCRYPT_SHA1_ALGORITHM,
                        NULL,
                        0,
                        pbPubkey,
                        cbPubkey,
                        pubkeyDigestReference,
                        sizeof(pubkeyDigestReference),
                        &required)))
    {
        goto Cleanup;
    }

    // Step 1: Check the pubkey digest
    if((cbPubkeyDigest != sizeof(pubkeyDigestReference)) ||
       (memcmp(pbPubkeyDigest, pubkeyDigestReference, cbPubkeyDigest) != 0))
    {
        // IFX TPMs <=V2.21 did calculate the digest over the TPM_STORE_PUBKEY structure.
        // Lets cut these TPMs some slack.

        if(FAILED(hr = TpmAttiShaHash(
                            BCRYPT_SHA1_ALGORITHM,
                            NULL,
                            0,
                            pbStorePubkey,
                            cbStorePubkey,
                            pubkeyDigestReference,
                            sizeof(pubkeyDigestReference),
                            &required)))
        {
            goto Cleanup;
        }

        if((cbPubkeyDigest != sizeof(pubkeyDigestReference)) ||
           (memcmp(pbPubkeyDigest, pubkeyDigestReference, cbPubkeyDigest) != 0))
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }
    }

    // Step 2: Check the nonce if requested
    if((pbNonce != NULL) && (cbNonce != 0) &&
       ((cbNonce != cbAttestNonce) || ((memcmp(pbNonce, pbAttestNonce, cbNonce)) != 0)))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }

    // Step 3: Check the PCR data provided with the key
    if((pcrTable != NULL) && (pcrMask != 0))
    {
        // Calculate the pcrDigest from the pcrTable and pcrMask
        BYTE pcrComposite[sizeof(UINT16) + 3 + sizeof(UINT32) + AVAILABLE_PLATFORM_PCRS * SHA1_DIGEST_SIZE] = {0};
        UINT32 compositeCursor = 0;
        UINT32 cbCount = 0;

        // Make the easy check first
        if((attestPcrMask != pcrMask) ||
           (keyPcrMask != pcrMask))
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }

        // Create the composite
        if(FAILED(hr = WriteBigEndian(pcrComposite, sizeof(pcrComposite), &compositeCursor, (UINT16)0x0003))) //TPM_PCR_SELECTION.sizeofSelect
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pcrComposite, sizeof(pcrComposite), &compositeCursor, (BYTE)(0x000000ff & pcrMask)))) //TPM_PCR_SELECTION.select[0]
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pcrComposite, sizeof(pcrComposite), &compositeCursor, (BYTE)((0x0000ff00 & pcrMask) >> 8)))) //TPM_PCR_SELECTION.select[1]
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pcrComposite, sizeof(pcrComposite), &compositeCursor, (BYTE)((0x00ff0000 & pcrMask) >> 16)))) //TPM_PCR_SELECTION.select[2]
        {
            goto Cleanup;
        }

        // Count the PCRs in the composite
        for(UINT32 n = 0; n < 24; n++)
        {
            if((pcrMask & (0x00000001 << n)) != 0)
            {
                cbCount += SHA1_DIGEST_SIZE;
            }
        }
        if(FAILED(hr = WriteBigEndian(pcrComposite, sizeof(pcrComposite), &compositeCursor, cbCount))) // valueSize
        {
            goto Cleanup;
        }

        // Append all PCRs that are in the mask
        for(UINT32 n = 0; n < 24; n++)
        {
            if((pcrMask & (0x00000001 << n)) != 0)
            {
                if(FAILED(hr = WriteBigEndian(pcrComposite, sizeof(pcrComposite), &compositeCursor, &pcrTable[n * SHA1_DIGEST_SIZE], SHA1_DIGEST_SIZE))) // pcrValue
                {
                    goto Cleanup;
                }
            }
        }

        // Calculate the composite digest
        if(FAILED(hr = TpmAttiShaHash(
                            BCRYPT_SHA1_ALGORITHM,
                            NULL,
                            0,
                            pcrComposite,
                            compositeCursor,
                            pcrCompositeDigestReference,
                            sizeof(pcrCompositeDigestReference),
                            &required)))
        {
            goto Cleanup;
        }

        // Compare the composite digest in key and attestation
        if((cbAttestPcrDigest != SHA1_DIGEST_SIZE) ||
           (cbKeyPcrDigest != SHA1_DIGEST_SIZE) ||
           (memcmp(pcrCompositeDigestReference, pbAttestPcrDigest, SHA1_DIGEST_SIZE) != 0) ||
           (memcmp(pcrCompositeDigestReference, pbKeyPcrDigest, SHA1_DIGEST_SIZE) != 0))
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }
    }

    //Congratulations: This all look fine, we can trust the key

Cleanup:
    return hr;
}

HRESULT
GetKeyProperties12(
    _In_reads_(cbKeyBlob) PBYTE pbKeyBlob,
    UINT32 cbKeyBlob,
    _Out_ PUINT32 pPropertyFlags
    )
{
    HRESULT hr = S_OK;
    PPCP_KEY_BLOB p12Key = (PPCP_KEY_BLOB)pbKeyBlob;
    UINT32 keyCursor = 0;
    UINT16 keyUsage = 0;
    UINT32 keyFlags = 0;
    BYTE authDataUsage = 0;
    UINT32 parmSize = 0;
    UINT32 pcrInfoSize = 0;

    if((pPropertyFlags == NULL) ||
       (p12Key == NULL) ||
       (cbKeyBlob < sizeof(PCP_KEY_BLOB)) ||
       (p12Key->magic != BCRYPT_PCP_KEY_MAGIC) ||
       (p12Key->cbHeader < sizeof(PCP_KEY_BLOB)) ||
       (p12Key->pcpType != PCPTYPE_TPM12) ||
       (cbKeyBlob < p12Key->cbHeader +
                    p12Key->cbTpmKey))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    *pPropertyFlags = 0;

    if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, p12Key->cbHeader +
                                                                   sizeof(UINT16) +  // tag
                                                                   sizeof(UINT16)))) // fill
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &keyUsage))) // TPM_KEY_USAGE
    {
        goto Cleanup;
    }
    switch(keyUsage)
    {
        case 0x0010: //TPM_KEY_SIGNING
            *pPropertyFlags |= PCP_KEY_PROPERTIES_SIGNATURE_KEY;
            break;
        case 0x0011: //TPM_KEY_STORAGE
            *pPropertyFlags |= PCP_KEY_PROPERTIES_STORAGE_KEY;
            break;
        case 0x0012: //TPM_KEY_IDENTITY
            *pPropertyFlags |= PCP_KEY_PROPERTIES_IDENTITY_KEY;
            break;
        case 0x0014: //TPM_KEY_BIND
            *pPropertyFlags |= PCP_KEY_PROPERTIES_ENCRYPTION_KEY;
            break;
        case 0x0015: //TPM_KEY_LEGACY
            *pPropertyFlags |= PCP_KEY_PROPERTIES_GENERIC_KEY;
            break;
        default:
            hr = E_INVALIDARG;
            goto Cleanup;
            break;
    }
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &keyFlags))) // TPM_KEY_FLAGS
    {
        goto Cleanup;
    }
    if((keyFlags & 0x00000002) == 0)
    {
        *pPropertyFlags |= PCP_KEY_PROPERTIES_NON_MIGRATABLE;
    }
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &authDataUsage))) // TPM_AUTH_DATA_USAGE
    {
        goto Cleanup;
    }
    if(authDataUsage != 0x00)
    {
        *pPropertyFlags |= PCP_KEY_PROPERTIES_PIN_PROTECTED;
    }
    if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, sizeof(UINT32) +  // TPM_KEY_PARMS.TPM_ALGORITHM_ID
                                                                   sizeof(UINT16) +  // TPM_KEY_PARMS.TPM_ENC_SCHEME
                                                                   sizeof(UINT16)))) // TPM_KEY_PARMS.TPM_SIG_SCHEME
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &parmSize))) // parmSize
    {
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, parmSize))) // parms
    {
        goto Cleanup;
    }
    if(FAILED(hr = ReadBigEndian(pbKeyBlob, cbKeyBlob, &keyCursor, &pcrInfoSize))) // pcrInfoSize
    {
        goto Cleanup;
    }
    if(pcrInfoSize != 0)
    {
        *pPropertyFlags |= PCP_KEY_PROPERTIES_PCR_PROTECTED;
    }

Cleanup:
    return hr;
}

HRESULT
WrapPlatformKey12(
    _In_reads_(cbKeyPair) PBYTE pbKeyPair,
    UINT32 cbKeyPair,
    BCRYPT_KEY_HANDLE hStorageKey,
    UINT32 keyUsage,
    _In_reads_opt_(cbUsageAuth) PBYTE pbUsageAuth,
    UINT32 cbUsageAuth,
    UINT32 pcrMask,
    _In_reads_opt_(AVAILABLE_PLATFORM_PCRS * SHA1_DIGEST_SIZE) PBYTE pcrTable,
    _Out_writes_to_opt_(cbOutput, *pcbResult) PBYTE pbOutput,
    UINT32 cbOutput,
    _Out_ PUINT32 pcbResult
    )
{
    HRESULT hr = S_OK;
    BCRYPT_ALG_HANDLE hRngAlg = NULL;
    UINT32 cbRequired = 0;
    PPCP_KEY_BLOB pOutKey = (PPCP_KEY_BLOB)pbOutput;
    UINT32 cursor = 0;
    BOOLEAN tDefaultExponent = FALSE;
    BYTE authDataUsage = 0x00; //TPM_AUTH_NEVER
    BYTE defaultExponent[] = {0x01, 0x00, 0x01};
    BCRYPT_RSAKEY_BLOB* pKeyPair = (BCRYPT_RSAKEY_BLOB*)pbKeyPair;
    UINT32 cbEncData = 0;
    UINT16 tpmKeyUsage = 0;
    UINT16 encScheme = 0x0001; //TPM_ES_NONE
    UINT16 sigScheme = 0x0001; //TPM_SS_NONE
    UCHAR abOAEPParam[] = {'T', 'C', 'P', 'A'};
    BCRYPT_OAEP_PADDING_INFO oaep = {BCRYPT_SHA1_ALGORITHM,
                                     abOAEPParam,
                                     sizeof(abOAEPParam)};

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

    // Check and select the tpm keyUsage
    switch(keyUsage & 0x0000ffff)
    {
        case NCRYPT_PCP_SIGNATURE_KEY:
            tpmKeyUsage = 0x0010; // TPM_KEY_SIGNING
            sigScheme = 0x0003;   // TPM_SS_RSASSAPKCS1v15_DER
            break;
        case NCRYPT_PCP_ENCRYPTION_KEY:
            tpmKeyUsage = 0x0014; // TPM_KEY_BIND
            encScheme = 0x0002;   // TPM_ES_RSAESPKCSv15
            break;
        case NCRYPT_PCP_GENERIC_KEY:
            tpmKeyUsage = 0x0015; // TPM_KEY_LEGACY
            encScheme = 0x0002;   // TPM_ES_RSAESPKCSv15
            sigScheme = 0x0003;   // TPM_SS_RSASSAPKCS1v15_DER
            break;
        case NCRYPT_PCP_STORAGE_KEY:
            tpmKeyUsage = 0x0011; // TPM_KEY_STORAGE
            encScheme = 0x0003;   // TPM_ES_RSAESOAEP_SHA1_MGF1
            break;
        default:
            hr = E_INVALIDARG;
            goto Cleanup;
            break;
    }

    // Check usageAuth
    if(pbUsageAuth != NULL)
    {
        if(cbUsageAuth != SHA1_DIGEST_SIZE)
        {
            hr = E_INVALIDARG;
            goto Cleanup;
        }
        authDataUsage = 0x01; //TPM_AUTH_ALWAYS
    }

    // Is this key using the default exponent?
    if((pKeyPair->cbPublicExp == 3) &&
       (memcmp(&pbKeyPair[sizeof(BCRYPT_RSAKEY_BLOB)],
               defaultExponent,
               sizeof(defaultExponent)) == 0))
    {
        tDefaultExponent = TRUE;
    }

    // Get the size of the storage key encrypted blob
    if(FAILED(hr = HRESULT_FROM_NT(BCryptGetProperty(
                                        hStorageKey,
                                        BCRYPT_BLOCK_LENGTH,
                                        (PBYTE)&cbEncData,
                                        sizeof(cbEncData),
                                        (PULONG)&cbRequired,
                                        0))))
    {
        goto Cleanup;
    }

    // Calculate the TPM_KEY12 blob size
    cbRequired = sizeof(PCP_KEY_BLOB) +// BCrypt header
                 sizeof(UINT16) +      // TPM_STRUCTURE_TAG tag
                 sizeof(UINT16) +      // UINT16 fill
                 sizeof(UINT16) +      // TPM_KEY_USAGE keyUsage
                 sizeof(UINT32) +      // TPM_KEY_FLAGS keyFlags
                 sizeof(BYTE) +        // TPM_AUTH_DATA_USAGE authDataUsage
                 sizeof(UINT32) +      // TPM_ALGORITHM_ID algorithmID
                 sizeof(UINT16) +      // TPM_ENC_SCHEME encScheme
                 sizeof(UINT16) +      // TPM_SIG_SCHEME sigScheme
                 sizeof(UINT32) +      // UINT32 parmSize
                 sizeof(UINT32) +      // UINT32 keyLength
                 sizeof(UINT32) +      // UINT32 numPrimes
                 sizeof(UINT32);       // UINT32 exponentSize
    if(!tDefaultExponent)
    {
        cbRequired += pKeyPair->cbPublicExp;
    }
    cbRequired += sizeof(UINT32);      // UINT32 PCRInfoSize
    if(pcrMask != 0)
    {
        cbRequired += sizeof(UINT16) +     // TPM_STRUCTURE_TAG tag
                      sizeof(BYTE) +       // TPM_LOCALITY_SELECTION localityAtCreation;
                      sizeof(BYTE) +       // TPM_LOCALITY_SELECTION localityAtRelease;
                      sizeof(UINT16) + 3 + // TPM_PCR_SELECTION creationPCRSelection;
                      sizeof(UINT16) + 3 + // TPM_PCR_SELECTION releasePCRSelection;
                      SHA1_DIGEST_SIZE +   // TPM_COMPOSITE_HASH digestAtCreation;
                      SHA1_DIGEST_SIZE;    // TPM_COMPOSITE_HASH digestAtRelease;
    }
    cbRequired += sizeof(UINT32) +      // UINT32 keyLength
                  pKeyPair->cbModulus +
                  sizeof(UINT32) +      // UINT32 encDataSize
                  cbEncData;

    // See if we got sufficient buffer to create the key
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

    //Let's put this thing together
    memset(pbOutput, 0x00, cbOutput);
    pOutKey->magic = BCRYPT_PCP_KEY_MAGIC;
    pOutKey->cbHeader = sizeof(PCP_KEY_BLOB);
    pOutKey->pcpType = PCPTYPE_TPM12;
    if(cbUsageAuth != 0)
    {
        pOutKey->flags |= PCP_KEY_FLAGS_authRequired;
    }

    cursor += sizeof(PCP_KEY_BLOB);
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16)0x0028))) // TPM_TAG_KEY12
    {
        goto Cleanup;
    }
    if(FAILED(hr = SkipBigEndian(pbOutput, cbOutput, &cursor, sizeof(UINT16)))) // fill
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, tpmKeyUsage))) // keyUsage
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT32)(0x00000002 | 0x00000004)))) // migratable | isVolatile
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, authDataUsage)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT32)0x00000001))) //TPM_ALG_RSA
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, encScheme)))
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, sigScheme)))
    {
        goto Cleanup;
    }
    if(tDefaultExponent)
    {
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT32)(3 * sizeof(UINT32)))))
        {
            goto Cleanup;
        }
    }
    else
    {
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT32)(3 * sizeof(UINT32) + pKeyPair->cbPublicExp))))
        {
            goto Cleanup;
        }
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT32)pKeyPair->BitLength))) // KeyLength
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT32)0x00000002))) // NumPrimes
    {
        goto Cleanup;
    }
    if(tDefaultExponent)
    {
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT32)0x00000000))) // exponentSize
        {
            goto Cleanup;
        }
    }
    else
    {
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT32)pKeyPair->cbPublicExp))) // exponentSize
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, &pbKeyPair[sizeof(BCRYPT_RSAKEY_BLOB)], pKeyPair->cbPublicExp))) // non-default exponent
        {
            goto Cleanup;
        }
    }
    if(pcrMask == 0)
    {
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT32) 0x00000000))) // pcrInfoSize
        {
            goto Cleanup;
        }
    }
    else
    {
        // Calculate and add the length of the TPM_PCR_INFO_LONG structure
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT32)(sizeof(UINT16) +     // TPM_STRUCTURE_TAG tag
                                                                            sizeof(BYTE) +       // TPM_LOCALITY_SELECTION localityAtCreation;
                                                                            sizeof(BYTE) +       // TPM_LOCALITY_SELECTION localityAtRelease;
                                                                            sizeof(UINT16) + 3 + // TPM_PCR_SELECTION creationPCRSelection;
                                                                            sizeof(UINT16) + 3 + // TPM_PCR_SELECTION releasePCRSelection;
                                                                            SHA1_DIGEST_SIZE +   // TPM_COMPOSITE_HASH digestAtCreation;
                                                                            SHA1_DIGEST_SIZE)    // TPM_COMPOSITE_HASH digestAtRelease;
                                                                            ))) // pcrInfoSize
        {
            goto Cleanup;
        }

        // Assemble a TPM_PCR_INFO_LONG structure
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16) 0x0006))) // TPM_TAG_PCR_INFO_LONG
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (BYTE) 0x01))) // TPM_LOC_ZERO
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (BYTE) 0x01))) // TPM_LOC_ZERO
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16) 0x0003))) // sizeOfSelect
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (PBYTE) &pcrMask, (UINT16) 0x0003))) // pcrSelect
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT16) 0x0003))) // sizeOfSelect
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (PBYTE) &pcrMask, (UINT16) 0x0003))) // pcrSelect
        {
            goto Cleanup;
        }

        // Calculate the pcrDigest from the pcrTable and pcrMask
        BYTE pcrComposite[sizeof(UINT16) + 3 + sizeof(UINT32) + AVAILABLE_PLATFORM_PCRS * SHA1_DIGEST_SIZE] = {0};
        UINT32 compositeCursor = 0;
        UINT32 cbCount = 0;
        BYTE pcrCompositeDigest[SHA1_DIGEST_SIZE] = {0};

        // Create the composite
        if(FAILED(hr = WriteBigEndian(pcrComposite, sizeof(pcrComposite), &compositeCursor, (UINT16)0x0003))) //TPM_PCR_SELECTION.sizeofSelect
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pcrComposite, sizeof(pcrComposite), &compositeCursor, (PBYTE) &pcrMask, (UINT16) 0x0003))) //TPM_PCR_SELECTION.select
        {
            goto Cleanup;
        }

        // Count the PCRs in the composite
        for(UINT32 n = 0; n < 24; n++)
        {
            if((pcrMask & (0x00000001 << n)) != 0)
            {
                cbCount += SHA1_DIGEST_SIZE;
            }
        }
        if(FAILED(hr = WriteBigEndian(pcrComposite, sizeof(pcrComposite), &compositeCursor, cbCount))) // valueSize
        {
            goto Cleanup;
        }

        // Append all PCRs that are in the mask
        for(UINT32 n = 0; n < 24; n++)
        {
            if((pcrMask & (0x00000001 << n)) != 0)
            {
                if(FAILED(hr = WriteBigEndian(pcrComposite, sizeof(pcrComposite), &compositeCursor, &pcrTable[n * SHA1_DIGEST_SIZE], SHA1_DIGEST_SIZE))) // pcrValue
                {
                    goto Cleanup;
                }
            }
        }

        // Calculate the composite digest
        if(FAILED(hr = TpmAttiShaHash(
                            BCRYPT_SHA1_ALGORITHM,
                            NULL,
                            0,
                            pcrComposite,
                            compositeCursor,
                            pcrCompositeDigest,
                            sizeof(pcrCompositeDigest),
                            &cbRequired)))
        {
            goto Cleanup;
        }

        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, pcrCompositeDigest, sizeof(pcrCompositeDigest)))) // digestAtCreation
        {
            goto Cleanup;
        }
        if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, pcrCompositeDigest, sizeof(pcrCompositeDigest)))) // digestAtRelease
        {
            goto Cleanup;
        }
    }

    // Add the public key
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, (UINT32)pKeyPair->cbModulus))) // keyLength
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, &pbKeyPair[sizeof(BCRYPT_RSAKEY_BLOB) +
                                                                          pKeyPair->cbPublicExp], (UINT32)pKeyPair->cbModulus))) // keyLength
    {
        goto Cleanup;
    }


    // Prepare the private key blob data.
    BYTE tpmStoreAsymKey[sizeof(BYTE) +     // TPM_PAYLOAD_TYPE payload
                         SHA1_DIGEST_SIZE + // TPM_SECRET usageAuth
                         SHA1_DIGEST_SIZE + // TPM_SECRET migrationAuth
                         SHA1_DIGEST_SIZE + // TPM_DIGEST pubDataDigest
                         sizeof(UINT32) +   // UINT32 keyLength
                         147] = {0};        // BYTE key
    UINT32 tpmStoreAsymKeyCursor = 0;
    if(FAILED(hr = WriteBigEndian(tpmStoreAsymKey, sizeof(tpmStoreAsymKey), &tpmStoreAsymKeyCursor, (BYTE) 0x01))) // TPM_PT_ASYM
    {
        goto Cleanup;
    }
    if(authDataUsage == 0x01) //TPM_AUTH_ALWAYS
    {
        if(FAILED(hr = WriteBigEndian(tpmStoreAsymKey, sizeof(tpmStoreAsymKey), &tpmStoreAsymKeyCursor, pbUsageAuth, cbUsageAuth)))
        {
            goto Cleanup;
        }
    }
    else
    {
        if(FAILED(hr = SkipBigEndian(tpmStoreAsymKey, sizeof(tpmStoreAsymKey), &tpmStoreAsymKeyCursor, SHA1_DIGEST_SIZE)))
        {
            goto Cleanup;
        }
    }

    // Hostage keys are by definition exportable, but we do not want anybody to be able to do that, or the key could leak.
    // We will generate a random migrationAuth that we are not goign to disclose. This makes the key technically non-exportable.
    if((tpmStoreAsymKeyCursor + SHA1_DIGEST_SIZE) > sizeof(tpmStoreAsymKey))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    if(FAILED(hr = HRESULT_FROM_NT(BCryptOpenAlgorithmProvider(
                                                &hRngAlg,
                                                BCRYPT_RNG_ALGORITHM,
                                                NULL,
                                                0))))
    {
        goto Cleanup;
    }
    if(FAILED(hr = HRESULT_FROM_NT(BCryptGenRandom(
                                         hRngAlg,
                                         &tpmStoreAsymKey[tpmStoreAsymKeyCursor],
                                         SHA1_DIGEST_SIZE,
                                         0))))
    {
        goto Cleanup;
    }
    tpmStoreAsymKeyCursor += SHA1_DIGEST_SIZE;


    // Calculate now the pubDataDigest for the key integrity protection
    if((tpmStoreAsymKeyCursor + SHA1_DIGEST_SIZE) > sizeof(tpmStoreAsymKey))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    if(FAILED(hr = TpmAttiShaHash(
                        BCRYPT_SHA1_ALGORITHM,
                        NULL,
                        0,
                        &pbOutput[sizeof(PCP_KEY_BLOB)],
                        cursor - sizeof(PCP_KEY_BLOB),
                        &tpmStoreAsymKey[tpmStoreAsymKeyCursor],
                        SHA1_DIGEST_SIZE,
                        &cbRequired)))
    {
        goto Cleanup;
    }
    tpmStoreAsymKeyCursor += SHA1_DIGEST_SIZE;

    // Add the first prime. The TPM only needs one.
    if(FAILED(hr = WriteBigEndian(tpmStoreAsymKey, sizeof(tpmStoreAsymKey), &tpmStoreAsymKeyCursor, (UINT32)pKeyPair->cbPrime1))) // keyLength
    {
        goto Cleanup;
    }
    if(FAILED(hr = WriteBigEndian(tpmStoreAsymKey, sizeof(tpmStoreAsymKey), &tpmStoreAsymKeyCursor, &pbKeyPair[sizeof(BCRYPT_RSAKEY_BLOB) +
                                                                                                               pKeyPair->cbPublicExp +
                                                                                                               pKeyPair->cbModulus], (UINT32)pKeyPair->cbPrime1))) // key
    {
        goto Cleanup;
    }

    // We are going to continue writing the key blob now
    if(FAILED(hr = WriteBigEndian(pbOutput, cbOutput, &cursor, cbEncData))) // encDataSize
    {
        goto Cleanup;
    }

    // Now we have to OAEP_RSA encrypt the private portion with the storage pubkey and append that to the key blob
    if((cursor + cbEncData) > cbOutput)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    cbRequired = 0;
    if(FAILED(hr = HRESULT_FROM_NT(BCryptEncrypt(
                                        hStorageKey,
                                        tpmStoreAsymKey,
                                        tpmStoreAsymKeyCursor,
                                        &oaep,
                                        NULL,
                                        0,
                                        &pbOutput[cursor],
                                        cbOutput - cursor,
                                        (PULONG)&cbRequired,
                                        BCRYPT_PAD_OAEP))))
    {
        goto Cleanup;
    }
    cursor += cbRequired;
    pOutKey->cbTpmKey = cursor - sizeof(PCP_KEY_BLOB);
    *pcbResult = cursor;

    // The cat is in the bag! We're done here.

Cleanup:
    if(hRngAlg != NULL)
    {
        BCryptCloseAlgorithmProvider(hRngAlg, 0);
        hRngAlg = NULL;
    }
    return hr;
}

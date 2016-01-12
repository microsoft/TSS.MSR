/*++

THIS CODE AND INFORMATION IS PROVIDED "AS IS" WITHOUT WARRANTY OF
ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
PARTICULAR PURPOSE.

Copyright (c) Microsoft Corporation.  All rights reserved.

Module Name:

    InlineFn.h

Abstract:

    Common inline function definitions.

--*/

#ifdef _MSC_VER
#pragma once
#endif

#ifndef INLINEFN_H
#define INLINEFN_H

#define ENDIANSWAPUINT64(INPARM) \
    (((INPARM & 0x00000000000000FF) << 56) | \
     ((INPARM & 0x000000000000FF00) << 40) | \
     ((INPARM & 0x0000000000FF0000) << 24) | \
     ((INPARM & 0x00000000FF000000) << 8) | \
     ((INPARM & 0x000000FF00000000) >> 8) | \
     ((INPARM & 0x0000FF0000000000) >> 24) | \
     ((INPARM & 0x00FF000000000000) >> 40) | \
     ((INPARM & 0xFF00000000000000) >> 56));

#define ENDIANSWAPUINT32(INPARM) \
    (((INPARM & 0x000000FF) << 24) | \
     ((INPARM & 0x0000FF00) << 8) | \
     ((INPARM & 0x00FF0000) >> 8) | \
     ((INPARM & 0xFF000000) >> 24));

#define ENDIANSWAPUINT16(INPARM) \
    (((INPARM & 0x00FF) << 8) | \
     ((INPARM & 0xFF00) >> 8));

#define ENDIANSWAP_UINT64TOARRAY(INPARM, ARRAY, OFFSET) \
    ARRAY[OFFSET + 0] = (BYTE)(((INPARM) & 0xff00000000000000) >> 56); \
    ARRAY[OFFSET + 1] = (BYTE)(((INPARM) & 0x00ff000000000000) >> 48); \
    ARRAY[OFFSET + 2] = (BYTE)(((INPARM) & 0x0000ff0000000000) >> 40); \
    ARRAY[OFFSET + 3] = (BYTE)(((INPARM) & 0x000000ff00000000) >> 32); \
    ARRAY[OFFSET + 4] = (BYTE)(((INPARM) & 0x00000000ff000000) >> 24); \
    ARRAY[OFFSET + 5] = (BYTE)(((INPARM) & 0x0000000000ff0000) >> 16); \
    ARRAY[OFFSET + 6] = (BYTE)(((INPARM) & 0x000000000000ff00) >> 8); \
    ARRAY[OFFSET + 7] = (BYTE)((INPARM) & 0x00000000000000ff); \

#define ENDIANSWAP_UINT32TOARRAY(INPARM, ARRAY, OFFSET) \
    ARRAY[OFFSET + 0] = (BYTE)(((INPARM) & 0xff000000) >> 24); \
    ARRAY[OFFSET + 1] = (BYTE)(((INPARM) & 0x00ff0000) >> 16); \
    ARRAY[OFFSET + 2] = (BYTE)(((INPARM) & 0x0000ff00) >> 8); \
    ARRAY[OFFSET + 3] = (BYTE)((INPARM) & 0x000000ff); \

#define ENDIANSWAP_UINT16TOARRAY(INPARM, ARRAY, OFFSET) \
    ARRAY[OFFSET + 0] = (BYTE)(((INPARM) & 0x0000ff00) >> 8); \
    ARRAY[OFFSET + 1] = (BYTE)((INPARM) & 0x000000ff); \

#define ENDIANSWAP_UINT64FROMARRAY(OUTPARM, ARRAY, OFFSET) \
    OUTPARM = (((UINT64)ARRAY[OFFSET + 0]) << 56); \
    OUTPARM |= (((UINT64)ARRAY[OFFSET + 1]) << 48); \
    OUTPARM |= (((UINT64)ARRAY[OFFSET + 2]) << 40); \
    OUTPARM |= (((UINT64)ARRAY[OFFSET + 3]) << 32); \
    OUTPARM |= (((UINT64)ARRAY[OFFSET + 4]) << 24); \
    OUTPARM |= (((UINT64)ARRAY[OFFSET + 5]) << 16); \
    OUTPARM |= (((UINT64)ARRAY[OFFSET + 6]) << 8); \
    OUTPARM |= ((UINT64)ARRAY[OFFSET + 7]); \

#define ENDIANSWAP_UINT32FROMARRAY(OUTPARM, ARRAY, OFFSET) \
    OUTPARM = (((UINT32)ARRAY[OFFSET + 0]) << 24); \
    OUTPARM |= (((UINT32)ARRAY[OFFSET + 1]) << 16); \
    OUTPARM |= (((UINT32)ARRAY[OFFSET + 2]) << 8); \
    OUTPARM |= ((UINT32)ARRAY[OFFSET + 3]); \

#define ENDIANSWAP_UINT16FROMARRAY(OUTPARM, ARRAY, OFFSET) \
    OUTPARM = (((UINT16)ARRAY[OFFSET + 0]) << 8); \
    OUTPARM |= ((UINT16)ARRAY[OFFSET + 1]); \


// Inline functions

inline HRESULT
AllocateAndZero(
    _Outptr_result_bytebuffer_(bufSize) PVOID* pptr,
    size_t bufSize
    )
{
    HRESULT hr = S_OK;
    if((pptr == NULL) || (bufSize == 0))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    *pptr = new(std::nothrow) BYTE[bufSize];
    if(*pptr == NULL)
    {
        hr = E_OUTOFMEMORY;
        goto Cleanup;
    }
    ZeroMemory(*pptr, bufSize);
Cleanup:
    return hr;
}

inline void
ZeroAndFree(
    _Deref_pre_bytecap_(bufSize) PVOID* pptr,
    size_t bufSize
    )
{
    if((pptr != NULL) && (*pptr != NULL) && (bufSize != 0))
    {
        if(bufSize != 0)
        {
            ZeroMemory(*pptr, bufSize);
        }
        delete[] *pptr;
        *pptr = NULL;
    }
}

inline HRESULT
SkipBigEndian2B(_In_reads_(cbBuffer) PBYTE pbBuffer,
                UINT32 cbBuffer,
                _Inout_ PUINT32 pCursor)
{
    HRESULT hr = S_OK;
    UINT16 bufSize = 0;

    if((pCursor == NULL) ||
       (*pCursor > cbBuffer) ||
       (sizeof(UINT16) > cbBuffer - *pCursor))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    ENDIANSWAP_UINT16FROMARRAY(bufSize, pbBuffer, *pCursor);
    *pCursor += sizeof(UINT16);
    if(bufSize > cbBuffer - *pCursor)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    *pCursor += bufSize;

Cleanup:
    return hr;
}

inline HRESULT
WriteBigEndian2B(_Out_writes_(cbBuffer) PBYTE pbBuffer,
                 UINT32 cbBuffer,
                 _Inout_ PUINT32 pCursor,
                 UINT16 cbInBuffer,
                 _In_reads_(cbInBuffer) PBYTE pbInBuffer)
{
    HRESULT hr = S_OK;

    if((pCursor == NULL) ||
       (*pCursor > cbBuffer) ||
       (sizeof(cbInBuffer) > cbBuffer - *pCursor) ||
       (pbInBuffer == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    ENDIANSWAP_UINT16TOARRAY(cbInBuffer, pbBuffer, *pCursor);
    *pCursor += sizeof(cbInBuffer);

    if(memcpy_s(&pbBuffer[*pCursor], cbBuffer - *pCursor, pbInBuffer, cbInBuffer))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    *pCursor += cbInBuffer;

Cleanup:
    return hr;
}

inline HRESULT
ReadBigEndian2B(_In_reads_(cbBuffer) PBYTE pbBuffer,
                UINT32 cbBuffer,
                _Inout_ PUINT32 pCursor,
                _Out_ PUINT16 pcbValOut,
                _Outptr_result_bytebuffer_(*pcbValOut) PBYTE* pbValOut)
{
    HRESULT hr = S_OK;

    if((pCursor == NULL) ||
       (*pCursor > cbBuffer) ||
       (sizeof(UINT16) > cbBuffer - *pCursor) ||
       (pbValOut == NULL) ||
       (pcbValOut == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    ENDIANSWAP_UINT16FROMARRAY(*pcbValOut, pbBuffer, *pCursor);
    *pCursor += sizeof(UINT16);

    if(*pcbValOut > cbBuffer - *pCursor)
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    *pbValOut = &pbBuffer[*pCursor];
    *pCursor += *pcbValOut;

Cleanup:
    return hr;
}

inline HRESULT
SkipBigEndian(_In_reads_(cbBuffer) PBYTE pbBuffer,
              UINT32 cbBuffer,
              _Inout_ PUINT32 pCursor,
              UINT32 skipSize)
{
    HRESULT hr = S_OK;

    UNREFERENCED_PARAMETER(pbBuffer);

    if((pCursor == NULL) ||
       (*pCursor > cbBuffer) ||
       (skipSize > cbBuffer - *pCursor))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    *pCursor += skipSize;

Cleanup:
    return hr;
}

inline HRESULT
WriteBigEndian(_Out_writes_(cbBuffer) PBYTE pbBuffer,
               UINT32 cbBuffer,
               _Inout_ PUINT32 pCursor,
               _In_reads_(cbInBuffer) PBYTE pbInBuffer,
               UINT32 cbInBuffer)
{
    HRESULT hr = S_OK;

    if((pCursor == NULL) ||
       (*pCursor > cbBuffer) ||
       (cbInBuffer > cbBuffer - *pCursor) ||
       (pbInBuffer == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    if(memcpy_s(&pbBuffer[*pCursor], cbBuffer - *pCursor, pbInBuffer, cbInBuffer))
    {
        hr = E_FAIL;
        goto Cleanup;
    }
    *pCursor += cbInBuffer;

Cleanup:
    return hr;
}

inline HRESULT
ReadBigEndian(_In_reads_(cbBuffer) PBYTE pbBuffer,
              UINT32 cbBuffer,
              _Inout_ PUINT32 pCursor,
              _Outptr_result_bytebuffer_(cbValOut) PBYTE* pbValOut,
              UINT32 cbValOut)
{
    HRESULT hr = S_OK;

    if((pCursor == NULL) ||
       (*pCursor > cbBuffer) ||
       (cbValOut > cbBuffer - *pCursor) ||
       (pbValOut == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    *pbValOut = &pbBuffer[*pCursor];
    *pCursor += cbValOut;

Cleanup:
    return hr;
}

inline HRESULT
WriteBigEndian(_Out_writes_(cbBuffer) PBYTE pbBuffer,
               UINT32 cbBuffer,
               _Inout_ PUINT32 pCursor,
               UINT32 valIn)
{
    HRESULT hr = S_OK;

    if((pCursor == NULL) ||
       (*pCursor > cbBuffer) ||
       (sizeof(valIn) > cbBuffer - *pCursor))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    ENDIANSWAP_UINT32TOARRAY(valIn, pbBuffer, *pCursor);
    *pCursor += sizeof(valIn);

Cleanup:
    return hr;
}

inline HRESULT
ReadBigEndian(_In_reads_(cbBuffer) PBYTE pbBuffer,
              UINT32 cbBuffer,
              _Inout_ PUINT32 pCursor,
              _Out_ PUINT32 pValOut)
{
    HRESULT hr = S_OK;

    if((pCursor == NULL) ||
       (*pCursor > cbBuffer) ||
       (sizeof(UINT32) > cbBuffer - *pCursor) ||
       (pValOut == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    ENDIANSWAP_UINT32FROMARRAY(*pValOut, pbBuffer, *pCursor);
    *pCursor += sizeof(UINT32);

Cleanup:
    return hr;
}

inline HRESULT
WriteBigEndian(_Out_writes_(cbBuffer) PBYTE pbBuffer,
               UINT32 cbBuffer,
               _Inout_ PUINT32 pCursor,
               UINT16 valIn)
{
    HRESULT hr = S_OK;

    if((pCursor == NULL) ||
       (*pCursor > cbBuffer) ||
       (sizeof(valIn) > cbBuffer - *pCursor))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    ENDIANSWAP_UINT16TOARRAY(valIn, pbBuffer, *pCursor);
    *pCursor += sizeof(valIn);

Cleanup:
    return hr;
}

inline HRESULT
ReadBigEndian(_In_reads_(cbBuffer) PBYTE pbBuffer,
              UINT32 cbBuffer,
              _Inout_ PUINT32 pCursor,
              _Out_ PUINT16 pValOut)
{
    HRESULT hr = S_OK;

    if((pCursor == NULL) ||
       (*pCursor > cbBuffer) ||
       (sizeof(UINT16) > cbBuffer - *pCursor) ||
       (pValOut == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    ENDIANSWAP_UINT16FROMARRAY(*pValOut, pbBuffer, *pCursor);
    *pCursor += sizeof(UINT16);

Cleanup:
    return hr;
}

inline HRESULT
WriteBigEndian(_Out_writes_(cbBuffer) PBYTE pbBuffer,
               UINT32 cbBuffer,
               _Inout_ PUINT32 pCursor,
               BYTE valIn)
{
    HRESULT hr = S_OK;

    if((pCursor == NULL) ||
       (*pCursor > cbBuffer) ||
       (sizeof(valIn) > cbBuffer - *pCursor))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    pbBuffer[*pCursor] = valIn;
    *pCursor += sizeof(valIn);

Cleanup:
    return hr;
}

inline HRESULT
ReadBigEndian(PBYTE pbBuffer,
              UINT32 cbBuffer,
              _Inout_ PUINT32 pCursor,
              _Out_ PBYTE pValOut)
{
    HRESULT hr = S_OK;

    if((pCursor == NULL) ||
       (*pCursor > cbBuffer) ||
       (sizeof(UINT8) > cbBuffer - *pCursor) ||
       (pValOut == NULL))
    {
        hr = E_INVALIDARG;
        goto Cleanup;
    }
    *pValOut = pbBuffer[*pCursor];
    *pCursor += sizeof(UINT8);

Cleanup:
    return hr;
}

#endif //INLINEFN_H

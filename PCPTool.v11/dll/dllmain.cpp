#include "StdAfx.h"

/*++

THIS CODE AND INFORMATION IS PROVIDED "AS IS" WITHOUT WARRANTY OF
ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
PARTICULAR PURPOSE.

Copyright (c) Microsoft Corporation.  All rights reserved.

Module Name:

    DllMain.cpp

Abstract:

    This file contains library attach and detach code.

--*/

BOOL APIENTRY
DllMain(
    _In_ HINSTANCE hModule,
    _In_ DWORD ulReason,
    _In_opt_ LPVOID lpReserved
    )
{
    UNREFERENCED_PARAMETER(hModule);
    UNREFERENCED_PARAMETER(lpReserved);

    switch(ulReason) 
    {
    case DLL_PROCESS_ATTACH:
        break;
    case DLL_PROCESS_DETACH:
        TpmAttiReleaseHashProviders();
        break;
    default:
        break;
    }
    return TRUE;
}


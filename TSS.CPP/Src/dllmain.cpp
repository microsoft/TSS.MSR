/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#include "stdafx.h"
#include "MarshalInternal.h"

void DllCleanup()
{
    for (auto i = TypeMap.begin(); i != TypeMap.end(); i++)
    {
        if (i->second == NULL)
            continue;

        delete i->second;
        i->second = NULL;
    }
    TypeMap.clear();
}

void DllInit()
{
    TpmTypeInfo::TpmTypeInitter();
    return;
}

#ifdef WIN32
BOOL APIENTRY DllMain(HMODULE hModule,
                      DWORD  ul_reason_for_call,
                      LPVOID lpReserved)
{

    switch (ul_reason_for_call) {
        case DLL_PROCESS_ATTACH:
            break;

        case DLL_THREAD_ATTACH:
        case DLL_THREAD_DETACH:
            return true;

        case DLL_PROCESS_DETACH:
            DllCleanup();
            return true;

        default:;
    }

    DllInit();

    return TRUE;
}
#endif


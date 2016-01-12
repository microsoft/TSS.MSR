/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"
#include "Tpm2.h"

void DllCleanup()
{
    for (auto i = TypeMap.begin(); i != TypeMap.end(); i++) {

        if (i->second == NULL) {
            continue;
        }

        i->second->EnumNames.clear();
        i->second->BitNames.clear();
        i->second->Fields.clear();
        i->second->UnionSelector.clear();
        i->second->Name.clear();
        delete i->second;
        i->second = NULL;
    }

    TypeMap.clear();

    return;

}

void DllInit()
{
    StructMarshallInfo::TpmTypeInitter();
    TheTypeMap.Init();
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


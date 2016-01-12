/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"
#include "Tpm2.h"

using namespace TpmCpp;

#include "Samples.h"

// The name "DllInit" is misleading on non-WIN32 platforms but
// the purpose of the routine is the same, initializing TSS.CPP.
extern void DllInit();

#ifdef WIN32
_CrtMemState MemState;

int _tmain(int argc, _TCHAR *argv[])
{
    _CrtMemCheckpoint(&MemState);

    Samples s;
    s.RunAllSamples();

    HMODULE h = LoadLibrary(_T("TSS.CPP.dll"));
    _ASSERT(h != NULL);

    BOOL ok = FreeLibrary(h);
    _ASSERT(ok);
    _CrtMemDumpAllObjectsSince(&MemState);

    return 0;
}
#endif

#ifdef __linux__
int main(int argc, char *argv[])
{
    DllInit();

    try {
        Samples s;
        s.RunAllSamples();
    }
    catch (const runtime_error& exc) {
        cerr << "TpmCppTester: " << exc.what() << "\nExiting...\n";
    }

    return 0;
}
#endif
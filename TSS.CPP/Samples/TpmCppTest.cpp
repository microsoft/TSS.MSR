/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"
#include "Tpm2.h"

#include "Samples.h"

using namespace TpmCpp;

void RunDocSamples();

#ifdef WIN32
_CrtMemState MemState;

int _tmain(int argc, _TCHAR *argv[])
{
#if 0
    TPMA_OBJECT oa1 = TPMA_OBJECT::decrypt,
                oa2 = TPMA_OBJECT::decrypt | TPMA_OBJECT::encrypt;
    std::cout << "oa1 = " << GetEnumString(oa1) << std::endl;
    std::cout << "oa2 = " << GetEnumString(oa2) << std::endl;

#else
    _CrtMemCheckpoint(&MemState);

    RunDocSamples();

    Samples s;
    s.RunAllSamples();

    HMODULE h = LoadLibrary(_T("TSS.CPP.dll"));
    _ASSERT(h != NULL);

    BOOL ok = FreeLibrary(h);
    _ASSERT(ok);
    _CrtMemDumpAllObjectsSince(&MemState);
#endif // 0
    return 0;
}
#endif

#ifdef __linux__
int main(int argc, char *argv[])
{
    try {
        Samples s;
        s.RunAllSamples();
    }
    catch (const std::runtime_error& exc) {
        std::cerr << "TpmCppTester: " << exc.what() << "\nExiting...\n";
    }
    return 0;
}
#endif

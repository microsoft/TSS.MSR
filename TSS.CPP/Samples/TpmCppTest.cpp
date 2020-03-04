/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"
#include "Tpm2.h"

#include "Samples.h"

using namespace TpmCpp;


// The name "DllInit" is misleading on non-WIN32 platforms but
// the purpose of the routine is the same, initializing TSS.CPP.
extern void DllInit();


struct TEST_ENUM : public TpmEnum<UINT16> {
    enum _TEST_ENUM {
    NONE = 0x0000,
    /// <summary>
    /// an object type that contains an RSA key
    /// </summary>
    FIRST = 0x0001,
    /// <summary>
    /// an object type that contains an RSA key
    /// </summary>
    LAST = 0x0002
    };

    TEST_ENUM() {}
    TEST_ENUM(ValueType v) : TpmEnum(v) {}
};

struct TEST_ENUM_2 : public TpmEnum<UINT16> {
    enum _TEST_ENUM {
    NONE = 0x0000,
    /// <summary>
    /// an object type that contains an RSA key
    /// </summary>
    FIRST = 0x0001,
    /// <summary>
    /// an object type that contains an RSA key
    /// </summary>
    LAST = 0x0002
    };

    TEST_ENUM_2() {}
    TEST_ENUM_2(ValueType v) : TpmEnum(v) {}
};



#ifdef WIN32
_CrtMemState MemState;

int _tmain(int argc, _TCHAR *argv[])
{
    TEST_ENUM   te0,
                tex = TEST_ENUM(),
                te = te0,
                te1 = TEST_ENUM::FIRST;
    te0 = TEST_ENUM::LAST;
    UINT32  x1 = te1,
            x = te0 | te1,
            x0 = x & ~TEST_ENUM::FIRST;

    if (x & TEST_ENUM::FIRST)
        printf("FIRST is present in x\n");
    else
        printf("ERROR: FIRST is NOT present in x\n");
    if (x0 & TEST_ENUM::FIRST)
        printf("ERROR: FIRST is STILL present in x0\n");
    else
        printf("FIRST is not present in x0\n");

    te = te1 | TEST_ENUM::LAST;
    te = te & ~te0;
    if (te == TEST_ENUM::FIRST)
        printf("te is FIRST\n");
    else
        printf("ERROR: te is NOT FIRST\n");

    te = te0 | te1;
    te1 = te - te0;
    if (te1 == TEST_ENUM::FIRST)
        printf("te1 is FIRST\n");
    else
        printf("ERROR: te1 is NOT FIRST\n");

    te1 |= TEST_ENUM::LAST;


#if 1
    _CrtMemCheckpoint(&MemState);

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
    DllInit();

    try {
        Samples s;
        s.RunAllSamples();
    }
    catch (const std::runtime_error& exc) {
        cerr << "TpmCppTester: " << exc.what() << "\nExiting...\n";
    }

    return 0;
}
#endif
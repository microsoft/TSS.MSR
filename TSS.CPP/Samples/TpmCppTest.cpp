/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#include "stdafx.h"
#include "Tpm2.h"

#include "Samples.h"

#if __linux__
#define TCHAR   char 
#define _tcscmp strcmp
#define _T(s)   s
#endif

using namespace TpmCpp;

bool UseSimulator = true;

static bool
CmdLine_IsOpt(
    const TCHAR* opt,               // Command line parameter to check
    const TCHAR* optFull,           // Expected full name
    const TCHAR* optShort = nullptr // Expected short (single letter) name
    )
{
    return 0 == _tcscmp(opt, optFull)
        || (   (opt[0] == '/' || opt[0] == '-')
            && (   0 == _tcscmp(opt + 1, optFull)
                || (optShort && opt[1] == optShort[0] && opt[2] == 0)
                || (opt[0] == '-' && opt[1] == '-' && 0 == _tcscmp(opt + 2, optFull))));
}

void CmdLine_Help(std::ostream& ostr)
{
    ostr << "One command line option can be specified." << endl
        << "An option can be in the short form (one letter preceded with '-' or '/')" << endl
        << "or in the full form (preceded with '--' or without any sigil)." << endl
        << "Supported options:" << endl
        << "   -h (help|?) - print this message" << endl
        << "   -s (sim) - use locally running TPM simulator" << endl
        << "   -t (tbs|sys) - use system TPM" << endl;
}

int CmdLine_Parse(int argc, TCHAR *argv[])
{
    if (argc > 2)
    {
        std::cerr << "Too many command line option can be specified." << endl;
        CmdLine_Help(std::cerr);
        return -1;
    }
    if (argc == 1 || CmdLine_IsOpt(argv[1], _T("sim"), _T("s")))
    {
        UseSimulator = true;
        return 0;
    }
    if (CmdLine_IsOpt(argv[1], _T("tbs"), _T("t")) ||
        CmdLine_IsOpt(argv[1], _T("sys")))
    {
        UseSimulator = false;
        return 0;
    }
    if (CmdLine_IsOpt(argv[1], _T("help"), _T("h")) ||
        CmdLine_IsOpt(argv[1], _T("?"), _T("?")))
    {
        CmdLine_Help(std::cout);
        return 1;
    }

    std::cerr << "Unrecognized command line option: '" << argv[1] << "'" << endl;
    CmdLine_Help(std::cerr);
    return -2;
}


#ifdef WIN32
_CrtMemState MemState;

int _tmain(int argc, _TCHAR *argv[])
{
    _CrtMemCheckpoint(&MemState);

#elif __linux__

int main(int argc, char *argv[])
{
#endif

    int res = CmdLine_Parse(argc, argv);
    if (res != 0)
        return res;

    try {
        Samples s;
        s.RunAllSamples();
    }
    catch (const std::runtime_error& exc) {
        std::cerr << "TpmCppTester: " << exc.what() << "\nExiting...\n";
    }

#ifdef WIN32
    HMODULE h = LoadLibrary(_T("TSS.CPP.dll"));
    _ASSERT(h != NULL);

    BOOL ok = FreeLibrary(h);
    _ASSERT(ok);
    _CrtMemDumpAllObjectsSince(&MemState);
#endif

    return 0;
}

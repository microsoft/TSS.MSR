/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#pragma once

//
// TpmCpp library headers for precompilation
//


#include <stdio.h>
#include <stdlib.h>

// STL
#include <exception>
#include <string>
#include <initializer_list>
#include <cstdarg>
#include <typeinfo>
#include <chrono>
#include <system_error>
#include <memory>
#include <iostream>
#include <iomanip>

// OS specific and more STL
#include "fdefs.h"

// Include this line to make compiles faster
#include "Tpm2.h"

#ifdef _DEBUG
inline void DumpBuf(const char *label, size_t size, const BYTE* buf, int LineLen = 32)
{
    if (label)
        printf("%s", label);
    if (buf)
        printf(" %zu bytes", size);
    if (buf) {
        for (size_t i = 0; i < size; i++) {
            if ((i % LineLen) == 0)
                printf("\n    ");
            printf("%02X ", buf[i]);
        }
        printf("\n");
    }
}

inline void DumpBuf(const char *label, const TpmCpp::ByteVec& buf, int LineLen = 32)
{
    DumpBuf(label, buf.size(), buf.data(), LineLen);
}
#endif

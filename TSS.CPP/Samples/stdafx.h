/*
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See the LICENSE file in the project root for full license information.
 */

#pragma once

#include <stdio.h>
#include <stdlib.h>

#if 0
#ifdef WIN32
#define WIN32_LEAN_AND_MEAN       

// REVISIT: Lots of these warnings.
#pragma  warning(once:4251)

#include <crtdbg.h>
#include <windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include <tchar.h>
#endif

#ifdef __linux__
#include <arpa/inet.h>
#include <assert.h>
#include <string.h>

#define OutputDebugString wprintf
#define MultiByteToWideChar(a,b,c,d,e,f) assert(d<=f);mbtowc(e,c,d);
#endif
#endif // 0

// STL stuff
#include <exception>
#include <numeric>
#include <vector>
#include <string>
#include <map>
#include <algorithm>
#include <iostream>
#include <iomanip>
#include <sstream>
#include <initializer_list>
#include <cstdarg>
#include <typeinfo>
#include <chrono>
#include <system_error>

// Include this line to make compiles faster!
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

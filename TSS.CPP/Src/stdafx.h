/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#pragma once

//
// TpmCpp library headers for precompilation
//

// By default we use tracking allocators in the debug build
#ifdef _DEBUG
#define _CRTDBG_MAP_ALLOC
#ifndef DBG_NEW
#define DBG_NEW new ( _NORMAL_BLOCK , __FILE__ , __LINE__ )
#define new DBG_NEW
#endif
#endif  // _DEBUG

#include <stdio.h>
#include <stdlib.h>
#include "targetver.h"

#ifdef WIN32

#define WIN32_LEAN_AND_MEAN

// Windows stuff
#include <crtdbg.h>
#include <windows.h>
#include <tchar.h>
#include <winsock2.h>
#include <ws2tcpip.h>

#endif

#ifdef __linux__
// Non-Windows stuff
#include <arpa/inet.h>
#include <assert.h>
#include <string.h>

#define OutputDebugString wprintf
#define MultiByteToWideChar(a,b,c,d,e,f) assert(d<=f);mbtowc(e,c,d);
#endif

// STL stuff
#include <exception>
#include <string>
#include <initializer_list>
#include <cstdarg>
#include <typeinfo>
#include <chrono>
#include <system_error>

// Include this line to make compiles faster
#include "Tpm2.h"

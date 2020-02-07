/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#pragma once

// fdefs.h - Macro definitions, various forward definitions, and STL-declarations
//           to keep the linker happy.

#define _TPMCPP_BEGIN namespace TpmCpp {
#define _TPMCPP_END }

#ifdef _TPMCPPLIB
#define _DLLEXP_ __declspec(dllexport)
#define _TPMCPP_USING using namespace TpmCpp;
#define _TPMCPP ::TpmCpp::
#define EXPIMP_TEMPLATE
#else
#define _DLLEXP_ __declspec(dllimport)
#define _TPMCPP_USING
#define _TPMCPP
#define EXPIMP_TEMPLATE extern
#endif

#ifdef __linux__
#undef _DLLEXP_
#define _DLLEXP_
#endif

#ifdef WIN32
#define WIN32_LEAN_AND_MEAN       

// Windows stuff
#include <crtdbg.h>
#include <windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include <tchar.h>
#endif

#ifdef __linux__
// Non-Windows stuff
#include <arpa/inet.h>
#include <assert.h>
#include <string.h>

#define OutputDebugString wprintf
#define MultiByteToWideChar(a,b,c,d,e,f) assert(d<=f);mbtowc(e,c,d);
#endif


//using namespace std;
//#if !_HAS_STD_BYTE && (!defined(__cplusplus) || __cplusplus < 201703L)
//typedef unsigned char byte;
//#endif
//#define BYTE    unsigned char

#ifdef WIN32
// REVISIT: Lots of these warnings.
// In STL: 'std::_Compressed_pair<>' needs to have dll-interface to be used by clients of class 'std::_Vector_alloc<>'
#pragma  warning(disable:4251)
#endif

#include <vector>
#include <map>

#ifdef __linux__
typedef unsigned char  BYTE;
typedef unsigned char  UINT8;
typedef unsigned short UINT16;
typedef unsigned int   UINT32;
typedef unsigned long long UINT64;
typedef short   INT16;
typedef int     INT32;
typedef int     SOCKET;
typedef bool    BOOL;
typedef wchar_t WCHAR;

#define FALSE false
#define TRUE  true

#define _ASSERT assert
#endif

_TPMCPP_BEGIN

using std::vector;
using std::string;
using ByteVec = vector<BYTE>;

class TPMS_PCR_SELECTION;
class TPM_HANDLE;
class TPMS_TAGGED_PROPERTY;
class TPMT_HA;
class TPM2B_DIGEST;
class AUTH_SESSION;
class TPMS_ALG_PROPERTY;
class TPMS_PCR_SELECT;
class TPMS_TAGGED_PCR_SELECT;
class PABase;
class TPMT_SENSITIVE;

enum class SerializationType;
enum class TPM_CC : UINT32;
enum class TPM_ALG_ID : UINT16;
enum class TPMA_CC : UINT32;
enum class TPM_ECC_CURVE : UINT16;

_TPMCPP_END

#ifdef _TPMCPPLIB
EXPIMP_TEMPLATE template class _DLLEXP_ std::vector<BYTE>;
EXPIMP_TEMPLATE template class _DLLEXP_ std::vector<UINT32>;
EXPIMP_TEMPLATE template class _DLLEXP_ std::vector<int>;
EXPIMP_TEMPLATE template class _DLLEXP_ std::vector<char>;
EXPIMP_TEMPLATE template class _DLLEXP_ std::basic_string<char>;

_TPMCPP_USING

#endif // _TPMCPPLIB

#include "TpmTypes.h"
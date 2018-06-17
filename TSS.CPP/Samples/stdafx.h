/*++

Copyright (c) 2013, 2014  Microsoft Corporation
Microsoft Confidential

*/
#pragma once

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

//#include <algorithm>
#include <numeric>

#ifdef WIN32
// REVISIT: Lots of these warnings.
#pragma  warning(once:4251)
#endif

// Include this line to make compiles faster!
#include "Tpm2.h"
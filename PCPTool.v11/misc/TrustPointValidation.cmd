@ECHO OFF

REM **Check provider readyness
ECHO ***PCPTool(GetVersion)
PCPTool GetVersion
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Enumarate keys in the registry
ECHO ***PCPTool(EnumerateAIK)
PCPTool EnumerateAIK
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Export public key from provider
ECHO ***PCPTool(GetPubKey)
PCPTool GetPubKey pcptestAIK AikpubForTrustPoint
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Get the platform Counters
ECHO ***PCPTool(GetPlatformCounters)
PCPTool GetPlatformCounters
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Get current platform log
ECHO ***PCPTool(GetLog)
PCPTool GetLog currentLog
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Convert platform log into attestation structure
ECHO ***PCPTool(CreatePlatformAttestationFromLog)
PCPTool CreatePlatformAttestationFromLog currentLog currentAttestation pcptestAIK
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Validate platform attestation with trust point
ECHO ***PCPTool(ValidatePlatformAttestation)
PCPTool ValidatePlatformAttestation currentAttestation AikpubForTrustPoint
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Validate platform attestation on local platform
ECHO ***PCPTool(ValidatePlatformAttestation)
PCPTool ValidatePlatformAttestation currentAttestation ""
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Display platform attestation
ECHO ***PCPTool(DisplayPlatformAttestationFile)
PCPTool DisplayPlatformAttestationFile currentAttestation
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Get platform log from last full boot
ECHO ***PCPTool(GetArchivedLog)
PCPTool GetArchivedLog @ 0 lastBootLog
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Convert platform log into attestation structure
ECHO ***PCPTool(CreatePlatformAttestationFromLog)
PCPTool CreatePlatformAttestationFromLog lastBootLog lastBootAttestation pcptestAIK
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Validate platform attestation with trust point
ECHO ***PCPTool(ValidatePlatformAttestation)
PCPTool ValidatePlatformAttestation lastBootAttestation AikpubForTrustPoint
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Display platform attestation
ECHO ***PCPTool(DisplayPlatformAttestationFile)
PCPTool DisplayPlatformAttestationFile lastBootAttestation
if ERRORLEVEL 1 goto :FAILURE
ECHO.

ECHO ***TEST PASSED***
goto :EOF
:FAILURE
ECHO ***TEST FAILED***
:EOF
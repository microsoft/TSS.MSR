@ECHO OFF

REM **Check provider readyness
ECHO ***PCPTool(GetVersion)
PCPTool GetVersion
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Enumarate keys in the provider
ECHO ***PCPTool(EnumerateKeys)
PCPTool EnumerateKeys
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Export public key from provider
ECHO ***PCPTool(GetPubKey)
PCPTool GetPubKey pcptestAIK AikpubForAttestation
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Get the platform Counters
ECHO ***PCPTool(GetPlatformCounters)
PCPTool GetPlatformCounters
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Get signed platform attestation
ECHO ***PCPTool(GetPlatformAttestation)
PCPTool GetPlatformAttestation pcptestAIK attestationBlob ThisIsANonceProvidedFromTheServer
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Validate signed platform attestation
ECHO ***PCPTool(ValidatePlatformAttestation)
PCPTool ValidatePlatformAttestation attestationBlob AikpubForAttestation ThisIsANonceProvidedFromTheServer
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Validate signed platform attestation on local platform
ECHO ***PCPTool(ValidatePlatformAttestation)
PCPTool ValidatePlatformAttestation attestationBlob "" ThisIsANonceProvidedFromTheServer
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Display signed platform attestation
ECHO ***PCPTool(DisplayPlatformAttestationFile)
PCPTool DisplayPlatformAttestationFile attestationBlob
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Get local platform attestation
ECHO ***PCPTool(GetPlatformAttestation)
PCPTool GetPlatformAttestation "" localAttestationBlob ThisIsANonceProvidedFromTheServer
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Validate local platform attestation
ECHO ***PCPTool(ValidatePlatformAttestation)
PCPTool ValidatePlatformAttestation localAttestationBlob "" ThisIsANonceProvidedFromTheServer
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Display local platform attestation
ECHO ***PCPTool(DisplayPlatformAttestationFile)
PCPTool DisplayPlatformAttestationFile localAttestationBlob
if ERRORLEVEL 1 goto :FAILURE
ECHO.

ECHO ***TEST PASSED***
goto :EOF
:FAILURE
ECHO ***TEST FAILED***
:EOF
@ECHO OFF

REM **Check provider readyness
ECHO ***PCPTool(GetVersion)
PCPTool GetVersion
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Obtain EKCert
ECHO ***PCPTool(GetEK)
PCPTool GetEKCert EKCert
if ERRORLEVEL 1 goto :GetEKDirectly
ECHO.
REM **Obtain EKpub from Cert
ECHO ***PCPTool(ExtractEK)
PCPTool ExtractEK EKCert EKpub
if ERRORLEVEL 1 goto :FAILURE
ECHO.
goto :CreateAIK

:GetEKDirectly
REM **Obtain EKPub from platform
ECHO ***PCPTool(GetEK)
PCPTool GetEK EKpub
if ERRORLEVEL 1 goto :FAILURE
ECHO.

:CreateAIK
REM **Create a new keys on the PCP Provider
ECHO ***PCPTool(CreateAIK)
PCPTool CreateAIK pcptestAIK idBinding NonceFromTheServerForKeyCreation
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(GetPubAIK)
PCPTool GetPubAIK idBinding Aikpub
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(ChallengeAIK)
PCPTool ChallengeAIK idBinding EKpub SecretChallenge activationBlob NonceFromTheServerForKeyCreation
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(ActivateAIK)
PCPTool ActivateAIK pcptestAIK activationBlob
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Enumarate keys in the provider
ECHO ***PCPTool(EnumerateKeys)
PCPTool EnumerateKeys
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Export public key from provider
ECHO ***PCPTool(GetPubKey)
PCPTool GetPubKey pcptestAIK Aikpub
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Register AIK so the system can create platform trust points
ECHO ***PCPTool(RegisterAIK)
PCPTool RegisterAIK pcptestAIK
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Enumarate all AIK that are registered
ECHO ***PCPTool(EnumerateAIK)
PCPTool EnumerateAIK
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Turn integrity services reporting on
ECHO ***bcdedit(integrityservices)
bcdedit -set {globalsettings} integrityservices enable
if ERRORLEVEL 1 goto :FAILURE
ECHO.

ECHO ***TEST PASSED***
goto :EOF
:FAILURE
ECHO ***TEST FAILED***
:EOF
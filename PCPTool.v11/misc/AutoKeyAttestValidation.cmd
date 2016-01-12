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

ECHO ***PCPTool(GetPCRs)
PCPTool GetPCRs goodPcrs
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Create a new keys on the PCP Provider
ECHO ***PCPTool(CreateKey)
PCPTool CreateKey pcptestkey1
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(CreateKey)
PCPTool CreateKey pcptestkey2 MySuperSecretUsagePIN
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(CreateKey)
PCPTool CreateKey pcptestkey3 "" "" 0x0000ffff goodPcrs
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

REM **Retrieve key attestation from key
ECHO ***PCPTool(GetKeyAtestationFromKey)
for %%N in (pcptestkey1 pcptestkey2 pcptestkey3) do (
	PCPTool GetKeyAttestationFromKey %%N %%NAttest
	if ERRORLEVEL 1 goto :FAILURE
)
ECHO.

REM **validate key attestation
ECHO ***PCPTool(ValidateKeyAttestation)
for %%N in (pcptestkey1 pcptestkey2) do (
	PCPTool ValidateKeyAttestation %%NAttest Aikpub
	if ERRORLEVEL 1 goto :FAILURE
)
ECHO.
ECHO ***PCPTool(ValidateKeyAttestation)
PCPTool ValidateKeyAttestation pcptestkey3Attest Aikpub "" 0x0000ffff goodPcrs
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **get key properties
ECHO ***PCPTool(GetKeyProperties)
for %%N in (pcptestkey1 pcptestkey2 pcptestkey3) do (
	PCPTool GetKeyProperties %%NAttest
	if ERRORLEVEL 1 goto :FAILURE
)
ECHO.

REM **Delete keys from provider
ECHO ***PCPTool(DeleteKey)
for %%N in (pcptestkey1 pcptestkey2 pcptestkey3) do (
	PCPTool DeleteKey %%N
	if ERRORLEVEL 1 goto :FAILURE
)
ECHO.

REM **Cleanup
del pcptestkey1 pcptestkey2 pcptestkey3 2>NUL

ECHO ***TEST PASSED***
goto :EOF
:FAILURE
ECHO ***TEST FAILED***
:EOF
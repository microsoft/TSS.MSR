@ECHO OFF

REM **Check provider readyness
ECHO ***PCPTool(GetVersion)
PCPTool GetVersion
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(GetPCRs)
PCPTool GetPCRs goodPcrs
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(GetRandom)
PCPTool GetRandomPcrs "" "" badPcrs
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Create new keys on the PCP Provider
ECHO ***PCPTool(CreateKey)
PCPTool CreateKey pcptestkey1 "" "" 0x0000ffff
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(CreateKey)
PCPTool CreateKey pcptestkey2 "" "" 0x0000ffff goodPcrs
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(CreateKey)
PCPTool CreateKey pcptestkey3 "" "" 0x0000ffff badPcrs
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Enumarate keys in the provider
ECHO ***PCPTool(EnumerateKeys)
PCPTool EnumerateKeys
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Export public keys
ECHO ***PCPTool(GetPubKey)
for %%N in (pcptestkey1 pcptestkey2 pcptestkey3) do (
	PCPTool GetPubKey %%N %%NPub
	if ERRORLEVEL 1 goto :FAILURE
)
ECHO.

REM **Encrypt data with public keys
ECHO ***PCPTool(Encrypt)
for %%N in (pcptestkey1 pcptestkey2 pcptestkey3) do (
	PCPTool Encrypt %%NPub SuperSecretSecret %%NBlob
	if ERRORLEVEL 1 goto :FAILURE
)
ECHO.

REM **Decrypt data with keys
ECHO ***PCPTool(Decrypt)
for %%N in (pcptestkey1 pcptestkey2) do (
	PCPTool Decrypt %%N %%NBlob
	if ERRORLEVEL 1 goto :FAILURE
)
ECHO.

REM **Decrypt data with key with bad pcrs
ECHO ***PCPTool(Decrypt)
PCPTool Decrypt pcptestkey3 pcptestkey3Blob
if ERRORLEVEL 1 goto :DECRYPTFAIL
ECHO Decrypt should have failed because of PCR mismatch! 
ECHO. 
goto :FAILURE
:DECRYPTFAIL
ECHO Failure intended: Decrypt failed because of PCR mismatch!
ECHO. 

REM **Delete keys from provider
ECHO ***PCPTool(DeleteKey)
for %%N in (pcptestkey1 pcptestkey2 pcptestkey3) do (
	PCPTool DeleteKey %%N
	if ERRORLEVEL 1 goto :FAILURE
)
ECHO.

REM **Cleanup
del pcptestkey3 2>NUL

ECHO ***TEST PASSED***
goto :EOF
:FAILURE
ECHO ***TEST FAILED***
:EOF
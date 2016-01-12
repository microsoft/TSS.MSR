@ECHO OFF

REM **Check provider readyness
ECHO ***PCPTool(GetVersion)
PCPTool GetVersion
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(GetRandom)
PCPTool GetRandom 1024 ThisIsASeedForTheRNGInTheTPM
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(GetSRK)
PCPTool GetSRK
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
PCPTool CreateKey pcptestkey3 MySuperSecretUsagePIN TheAdministratorsPIN
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

REM **Decrypt with keys in the PCP Provider
ECHO ***PCPTool(Decrypt)
PCPTool Decrypt pcptestkey1 pcptestkey1Blob
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(Decrypt)
PCPTool Decrypt pcptestkey2 pcptestkey2Blob MySuperSecretUsagePIN
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(Decrypt)
PCPTool Decrypt pcptestkey3 pcptestkey3Blob MySuperSecretUsagePIN
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Change PIN on keys with PIN
ECHO ***PCPTool(ChangeKeyUsageAuth)
for %%N in (pcptestkey2 pcptestkey3) do (
	PCPTool ChangeKeyUsageAuth %%N MySuperSecretUsagePIN MyOtherSuperSecretUsagePIN
	if ERRORLEVEL 1 goto :FAILURE
)
ECHO.

ECHO ***PCPTool(Decrypt)
PCPTool Decrypt pcptestkey2 pcptestkey2Blob MyOtherSuperSecretUsagePIN
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(Decrypt)
PCPTool Decrypt pcptestkey3 pcptestkey3Blob MyOtherSuperSecretUsagePIN
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Export the exportable key
ECHO ***PCPTool(ExportKey)
PCPTool ExportKey pcptestkey3 TheAdministratorsPIN pcptestkey3
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Delete keys from provider
ECHO ***PCPTool(DeleteKey)
for %%N in (pcptestkey1 pcptestkey2 pcptestkey3) do (
	PCPTool DeleteKey %%N
	if ERRORLEVEL 1 goto :FAILURE
)
ECHO.

REM **Import the key on the PCP Provider
ECHO ***PCPTool(ImportKey)
PCPTool ImportKey pcptestkey3 pcptestkey1
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(ImportKey)
PCPTool ImportKey pcptestkey3 pcptestkey2 MySuperSecretUsagePIN
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(ImportKey)
PCPTool ImportKey pcptestkey3 pcptestkey3 MySuperSecretUsagePIN TheAdministratorsPIN
if ERRORLEVEL 1 goto :FAILURE
ECHO.

ECHO ***PCPTool(Decrypt)
PCPTool Decrypt pcptestkey1 pcptestkey3Blob
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(Decrypt)
PCPTool Decrypt pcptestkey2 pcptestkey3Blob MySuperSecretUsagePIN
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(Decrypt)
PCPTool Decrypt pcptestkey2 pcptestkey3Blob MySuperSecretUsagePIN
if ERRORLEVEL 1 goto :FAILURE
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
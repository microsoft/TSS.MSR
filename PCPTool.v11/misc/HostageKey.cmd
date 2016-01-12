@ECHO OFF

REM **Check provider readyness
ECHO ***PCPTool(GetVersion)
PCPTool GetVersion
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Create certificate template
echo [NewRequest] >CertTemplate.inf
echo    Subject = "CN=HostageCert" >>CertTemplate.inf
echo    HashAlgorithm = sha256 >>CertTemplate.inf
echo    KeyAlgorithm = RSA >>CertTemplate.inf
echo    KeyLength = 2048 >>CertTemplate.inf
echo    KeyUsage = "CERT_DIGITAL_SIGNATURE_KEY_USAGE" >>CertTemplate.inf
echo    KeyUsageProperty = "NCRYPT_ALLOW_SIGNING_FLAG" >>CertTemplate.inf
echo    ProviderName = "Microsoft SOFTWARE KEY STORAGE Provider" >>CertTemplate.inf
echo    RequestType = Cert >>CertTemplate.inf
echo    FriendlyName = "DeleteMe!" >>CertTemplate.inf
echo    Exportable = true >>CertTemplate.inf
echo    ExportableEncrypted = false >>CertTemplate.inf
echo [EnhancedKeyUsageExtension] >>CertTemplate.inf
echo    OID=2.5.29.37.0 >>CertTemplate.inf

REM **Create self signed certificate with a new keypair
ECHO ***CertReq(Hostage)
CertReq -new -binary -f CertTemplate.inf Hostage.Cer
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Obtain SRKPub from platform
ECHO ***PCPTool(GetSRK)
PCPTool GetSRK SRKpub
if ERRORLEVEL 1 goto :FAILURE
ECHO.

ECHO ***PCPTool(GetPCRs)
PCPTool GetPCRs Pcrs
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Create hostage key 
ECHO ***PCPTool(WrapKey)
PCPTool WrapKey Hostage.Cer SRKpub Hostage
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(ImportPlatformKey)
PCPTool ImportPlatformKey Hostage Hostage Hostage.Cer
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(GetPubKey)
PCPTool GetPubKey Hostage HostagePub
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(Encrypt)
PCPTool Encrypt HostagePub SuperSecretSecret SecretBlob
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(Decrypt)
PCPTool Decrypt Hostage SecretBlob
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(DeleteKey)
PCPTool DeleteKey Hostage
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Create hostage key with PIN
ECHO ***PCPTool(WrapKey)
PCPTool WrapKey Hostage.Cer SRKpub Hostage MySuperSecretUsagePIN
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(ImportPlatformKey)
PCPTool ImportPlatformKey Hostage Hostage Hostage.Cer
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(Encrypt)
PCPTool Encrypt HostagePub SuperSecretSecret SecretBlob
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(Decrypt)
PCPTool Decrypt Hostage SecretBlob MySuperSecretUsagePIN
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(DeleteKey)
PCPTool DeleteKey Hostage
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Create hostage key with PCRs
ECHO ***PCPTool(WrapKey)
PCPTool WrapKey Hostage.Cer SRKpub Hostage "" 0x0000ffff Pcrs
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(ImportPlatformKey)
PCPTool ImportPlatformKey Hostage Hostage Hostage.Cer
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(Encrypt)
PCPTool Encrypt HostagePub SuperSecretSecret SecretBlob
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(Decrypt)
PCPTool Decrypt Hostage SecretBlob
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(DeleteKey)
PCPTool DeleteKey Hostage
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Create hostage key with PIN and PCRs
ECHO ***PCPTool(WrapKey)
PCPTool WrapKey Hostage.Cer SRKpub Hostage MySuperSecretUsagePIN 0x0000ffff Pcrs
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(ImportPlatformKey)
PCPTool ImportPlatformKey Hostage Hostage Hostage.Cer
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(Encrypt)
PCPTool Encrypt HostagePub SuperSecretSecret SecretBlob
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(Decrypt)
PCPTool Decrypt Hostage SecretBlob MySuperSecretUsagePIN
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(DeleteKey)
PCPTool DeleteKey Hostage
if ERRORLEVEL 1 goto :FAILURE
ECHO.

ECHO ***TEST PASSED***
goto :EOF
:FAILURE
ECHO ***TEST FAILED***
:EOF
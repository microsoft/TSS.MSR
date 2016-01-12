@ECHO OFF

REM **Check provider readyness
ECHO ***PCPTool(GetVersion)
PCPTool GetVersion
if ERRORLEVEL 1 goto :FAILURE
ECHO.

if NOT EXIST CaCertTemp.inf (
 echo [NewRequest] >CaCertTemp.inf
 echo    Subject = "CN=TPM Endorsement CA01" >>CaCertTemp.inf
 echo    HashAlgorithm = sha256 >>CaCertTemp.inf
 echo    KeyAlgorithm = RSA >>CaCertTemp.inf
 echo    KeyLength = 2048 >>CaCertTemp.inf
 echo    KeyUsage = "CERT_DIGITAL_SIGNATURE_KEY_USAGE | CERT_KEY_CERT_SIGN_KEY_USAGE | CERT_CRL_SIGN_KEY_USAGE" >>CaCertTemp.inf
 echo    KeyUsageProperty = "NCRYPT_ALLOW_SIGNING_FLAG" >>CaCertTemp.inf
 echo    ProviderName = "Microsoft Software Key Storage Provider" >>CaCertTemp.inf
 echo    RequestType = Cert >>CaCertTemp.inf
 echo    Exportable = true >>CaCertTemp.inf
 echo    ExportableEncrypted = false >>CaCertTemp.inf
)
if NOT EXIST CaCert.cer (
 certreq -new CaCertTemp.inf CaCert.cer
)

REM **Create Enterprise EK Cert
ECHO ***PCPTool(GetEK)
PCPTool GetEK EKpub
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(IssueEKCert)
PCPTool IssueEKCert EKpub EnterpriseTPM EnterpriseEKCert.cer
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(AddEKCert)
PCPTool AddEKCert EnterpriseEKCert.cer
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Create a new AIK on the PCP Provider
ECHO ***PCPTool(CreateAIK)
PCPTool CreateAIK EnterpriseAIK idBinding NonceFromTheServerForKeyCreation
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(GetPubAIK)
PCPTool GetPubAIK idBinding Aikpub
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(PrivacyCaChallenge)
PCPTool PrivacyCaChallenge idBinding EKpub EnterpriseAIK activationBlob NonceFromTheServerForKeyCreation
if ERRORLEVEL 1 goto :FAILURE
ECHO.
ECHO ***PCPTool(PrivacyCaActivate)
PCPTool PrivacyCaActivate EnterpriseAIK activationBlob AIKCert.cer
if ERRORLEVEL 1 goto :FAILURE
ECHO.

REM **Enumarate keys in the provider
ECHO ***PCPTool(EnumerateKeys)
PCPTool EnumerateKeys
if ERRORLEVEL 1 goto :FAILURE
ECHO.

ECHO ***TEST PASSED***
goto :EOF
:FAILURE
ECHO ***TEST FAILED***
:EOF
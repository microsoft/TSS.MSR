@ECHO OFF

set LOGFILE=%~n0-log.txt
echo . > %LOGFILE%

if "%1"=="continue" goto :PART2

ECHO ***BasicProviderTest.cmd
ECHO ***BasicProviderTest.cmd >> %LOGFILE%
call BasicProviderTest.cmd >> %LOGFILE%
if ERRORLEVEL 1 goto :FAILURE
ECHO. >> %LOGFILE%

ECHO ***AikCreation.cmd
ECHO ***AikCreation.cmd >> %LOGFILE%
call AikCreation.cmd >> %LOGFILE%
if ERRORLEVEL 1 goto :FAILURE
ECHO. >> %LOGFILE%

ECHO *** A REBOOT IS REQUIRED.
ECHO *** AFTER REBOOT run script with: '%~nx0 continue'
ECHO *** PRESS CTRL-C to abort reboot
pause
shutdown /r /t 10
goto :EOF

:PART2

ECHO ***Attestation.cmd
ECHO ***Attestation.cmd >> %LOGFILE%
call Attestation.cmd >> %LOGFILE%
if ERRORLEVEL 1 goto :FAILURE
ECHO. >> %LOGFILE%

ECHO ***KeyAttestationTest.cmd
ECHO ***KeyAttestationTest.cmd >> %LOGFILE%
call KeyAttestationTest.cmd >> %LOGFILE%
if ERRORLEVEL 1 goto :FAILURE
ECHO. >> %LOGFILE%

ECHO ***TrustPointValidation.cmd
ECHO ***TrustPointValidation.cmd >> %LOGFILE%
call TrustPointValidation.cmd >> %LOGFILE%
if ERRORLEVEL 1 goto :FAILURE
ECHO. >> %LOGFILE%

ECHO ***HostageKey.cmd
ECHO ***HostageKey.cmd >> %LOGFILE%
call HostageKey.cmd >> %LOGFILE%
if ERRORLEVEL 1 goto :FAILURE
ECHO. >> %LOGFILE%

ECHO ***PCRBoundKeyTest.cmd
ECHO ***PCRBoundKeyTest.cmd >> %LOGFILE%
call PCRBoundKeyTest.cmd >> %LOGFILE%
if ERRORLEVEL 1 goto :FAILURE
ECHO. >> %LOGFILE%

ECHO ***ALL TESTS PASSED***
ECHO ***ALL TESTS PASSED*** >> %LOGFILE%
goto :EOF
:FAILURE
ECHO ***A TEST FAILED - CHECK %LOGFILE% FOR DETAILS***
ECHO ***A TEST FAILED*** >> %LOGFILE%
:EOF
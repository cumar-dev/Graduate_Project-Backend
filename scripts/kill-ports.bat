@echo off
setlocal
set "PS=%SystemRoot%\System32\WindowsPowerShell\v1.0\powershell.exe"

echo Checking ports 8081, 8082, 9090...

if not exist "%PS%" (
  echo ERROR: PowerShell not found at %PS%
  exit /b 1
)

"%PS%" -NoProfile -ExecutionPolicy Bypass -File "%~dp0kill-ports.ps1"
if errorlevel 1 (
  echo Port cleanup failed.
  exit /b 1
)

echo Port cleanup done.
exit /b 0

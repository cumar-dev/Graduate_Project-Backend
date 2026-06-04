@echo off
REM Loads backend/.env (handles & in MongoDB URI safely via PowerShell)
set "ENV_FILE=%~dp0..\.env"
if not exist "%ENV_FILE%" exit /b 0

powershell -NoProfile -Command ^
  "$f='%ENV_FILE%'; Get-Content $f | ForEach-Object { $t=$_.Trim(); if($t -and -not $t.StartsWith('#')) { $i=$t.IndexOf('='); if($i -gt 0){ $k=$t.Substring(0,$i).Trim(); $v=$t.Substring($i+1).Trim(); [Environment]::SetEnvironmentVariable($k,$v,'Process') } } }"

exit /b 0

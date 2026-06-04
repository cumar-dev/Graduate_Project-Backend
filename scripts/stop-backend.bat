@echo off
REM Stops any process listening on the dev API port (9090)
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":9090" ^| findstr "LISTENING"') do (
  echo Stopping PID %%p on port 9090...
  taskkill /PID %%p /F >nul 2>&1
)
echo Done. You can run mvn spring-boot:run again.

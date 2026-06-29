@echo off
setlocal
cd /d "%~dp0"

if not exist build mkdir build
javac -d build ClassDumpAgent.java
if errorlevel 1 exit /b 1

jar cfm ClassDumpAgent.jar manifest.mf -C build .
if errorlevel 1 exit /b 1

echo Built ClassDumpAgent.jar
exit /b 0
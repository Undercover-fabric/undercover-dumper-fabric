@echo off
setlocal EnableExtensions
cd /d "%~dp0"

echo Replace MOD_DUMPER_DIR with this folder path.
echo.
echo default
echo -javaagent:MOD_DUMPER_DIR\ClassDumpAgent.jar=MOD_DUMPER_DIR\dumped_classes
echo.
echo include
echo -javaagent:MOD_DUMPER_DIR\ClassDumpAgent.jar=MOD_DUMPER_DIR\dumped_classes^|include:com/yourmod,dev/author
echo.
echo mode:all
echo -javaagent:MOD_DUMPER_DIR\ClassDumpAgent.jar=MOD_DUMPER_DIR\dumped_classes^|mode:all
echo.
echo mode:obfuscated
echo -javaagent:MOD_DUMPER_DIR\ClassDumpAgent.jar=MOD_DUMPER_DIR\dumped_classes^|mode:obfuscated
echo.

if /i "%~1"=="local" (
  set "ROOT=%~dp0"
  set "ROOT=%ROOT:~0,-1%"
  echo local
  echo -javaagent:%ROOT%\ClassDumpAgent.jar=%ROOT%\dumped_classes
  echo -javaagent:%ROOT%\ClassDumpAgent.jar=%ROOT%\dumped_classes^|include:com/yourmod,dev/author
  echo -javaagent:%ROOT%\ClassDumpAgent.jar=%ROOT%\dumped_classes^|mode:all
  echo.
)

pause
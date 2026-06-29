@echo off
setlocal
cd /d "%~dp0"

set SRC=%~dp0dumped_classes
set OUT=%~dp0dumped.jar

if not exist "%SRC%" (
    echo Missing folder: dumped_classes
    echo No dumps yet.
    exit /b 1
)

powershell -NoProfile -Command ^
  "$src='%SRC%'; $out='%OUT%';" ^
  "$files=Get-ChildItem $src -Recurse -Filter *.class;" ^
  "if(-not $files){Write-Error 'No .class files'; exit 1};" ^
  "Add-Type -AssemblyName System.IO.Compression.FileSystem;" ^
  "if(Test-Path $out){Remove-Item $out -Force};" ^
  "$zip=[System.IO.Compression.ZipFile]::Open($out,'Create');" ^
  "foreach($f in $files){$rel=$f.FullName.Substring($src.Length+1).Replace('\','/');" ^
  "[System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile($zip,$f.FullName,$rel)|Out-Null};" ^
  "$zip.Dispose();" ^
  "Write-Host ('Packed {0} classes to dumped.jar' -f $files.Count)"

exit /b %ERRORLEVEL%
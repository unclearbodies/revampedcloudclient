@echo off
set "JAVA_HOME=C:\Users\SUVI IRL MINI\AppData\Local\Programs\Eclipse Adoptium\jdk-8.0.492.9-hotspot"
echo Using JAVA_HOME=%JAVA_HOME%
call gradlew build
if %ERRORLEVEL% EQU 0 (
    echo Copying jar to mods folder...
    copy /Y "build\libs\cloudmc-1.0.jar" "C:\Users\SUVI IRL MINI\AppData\Roaming\.minecraft\mods\cloudmc-1.0.jar"
    echo Done!
) else (
    echo Build failed, skipping copy.
)
pause

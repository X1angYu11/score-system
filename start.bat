@echo off
rem ==================================================
rem  Score System - one-click startup script
rem  Note: keep this file ASCII-only (no Chinese),
rem        because cmd reads .bat with the system
rem        codepage and mis-parses UTF-8 Chinese.
rem ==================================================

rem Switch to the directory of this script
cd /d %~dp0

rem Check Java
where java >nul 2>nul
if errorlevel 1 (
    echo [ERROR] "java" not found in PATH. Please check your JDK installation.
    pause
    exit /b 1
)

rem Check the jar
if not exist "target\score-system-0.0.1-SNAPSHOT.jar" (
    echo [ERROR] jar not found: target\score-system-0.0.1-SNAPSHOT.jar
    echo         Please run Maven "package" first.
    pause
    exit /b 1
)

echo ==================================================
echo   Starting Score System ...
echo   URL     : http://localhost:8080/index.html
echo   Account : zxy / 123456
echo   Stop    : close this window (or press Ctrl+C)
echo ==================================================
echo.

java -jar target\score-system-0.0.1-SNAPSHOT.jar

echo.
echo [INFO] Server stopped.
pause

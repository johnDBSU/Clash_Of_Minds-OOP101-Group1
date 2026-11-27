@echo off
setlocal EnableDelayedExpansion
chcp 65001 >nul
cd /d "%~dp0"

set "GAME_DIR=%~dp0"
if "!GAME_DIR:~-1!"=="\" set "GAME_DIR=!GAME_DIR:~0,-1!"

REM Launch in true fullscreen Windows Terminal if available and not already spawned
if /I not "%~1"=="--launched" (
    where wt >nul 2>&1
    if !errorlevel! EQU 0 (
        wt -F --title "CLASH OF MINDS" -d "!GAME_DIR!" cmd /k call run.bat --launched
        exit /b 0
    )
    REM Fallback for legacy cmd.exe: maximize window and send Alt+Enter fullscreen
    powershell -NoProfile -Command "Add-Type -Name W -Namespace N -MemberDefinition '[DllImport(\"kernel32.dll\")]public static extern IntPtr GetConsoleWindow();[DllImport(\"user32.dll\")]public static extern bool ShowWindow(IntPtr h,int c);'; [N.W]::ShowWindow([N.W]::GetConsoleWindow(),3)|Out-Null; $ws=New-Object -ComObject WScript.Shell; $ws.SendKeys('%%{ENTER}')" >nul 2>&1
    timeout /t 1 /nobreak >nul
)

REM Wait briefly for fullscreen resize to settle, enable VT100 ANSI, and read live console size
for /f "tokens=1,2" %%A in ('powershell -NoProfile -Command "Start-Sleep -Milliseconds 450; Write-Output ($Host.UI.RawUI.WindowSize.Width.ToString() + ' ' + $Host.UI.RawUI.WindowSize.Height.ToString())"') do (
    set "TERM_COLS=%%A"
    set "TERM_ROWS=%%B"
)
if not defined TERM_COLS set "TERM_COLS=120"
if not defined TERM_ROWS set "TERM_ROWS=36"

javac -encoding UTF-8 *.java
if errorlevel 1 (
    pause
    exit /b 1
)

java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Dterm.cols=%TERM_COLS% -Dterm.rows=%TERM_ROWS% Main
endlocal

@echo off
@setlocal enableextensions
@cd /d "%~dp0"

echo =======================================================
echo  PetReminder - Open Reports and Web Pages
echo =======================================================
echo.
echo Choose what to open:
echo  1. JaCoCo Test Coverage Report (lib module)
echo  2. ReportGenerator HTML Coverage Report
echo  3. Maven Site (localhost:9000)
echo  4. Open All Reports
echo.
set /p choice="Enter choice (1-4): "

if "%choice%"=="1" goto jacoco
if "%choice%"=="2" goto reportgen
if "%choice%"=="3" goto mavensite
if "%choice%"=="4" goto all

:jacoco
echo Opening JaCoCo report...
start "" "petreminder-app\lib\target\site\jacoco\index.html"
goto end

:reportgen
echo Opening ReportGenerator coverage report...
start "" "petreminder-app\app\target\site\coveragereport\index.html"
goto end

:mavensite
echo Starting Maven Site on http://localhost:9000/
start http://localhost:9000/
call mvn site:run -f petreminder-app\pom.xml
goto end

:all
echo Opening all reports...
start "" "petreminder-app\lib\target\site\jacoco\index.html"
start "" "petreminder-app\app\target\site\coveragereport\index.html"
goto end

:end
echo.
echo Operation Completed!
pause
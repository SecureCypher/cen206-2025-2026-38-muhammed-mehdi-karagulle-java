@echo off
@setlocal enableextensions
@cd /d "%~dp0"

echo Running PetReminder Application...
java -jar petreminder-app\app\target\petreminder-app-2.0.0.jar

echo.
echo Operation Completed!
pause
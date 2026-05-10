@echo off

:: Enable necessary extensions
@setlocal enableextensions

echo Get the current directory
set "currentDir=%CD%"

echo Change the current working directory to the script directory
@cd /d "%~dp0"

echo =======================================================
echo  PetReminder - Multi-Module Build Script
echo  Modules: lib (business logic) + app (GUI)
echo =======================================================

echo.
echo [1/10] Cleaning generated folders...
rd /S /Q "petreminder-app\app\target\site\coverxygen"  2>nul
rd /S /Q "petreminder-app\app\target\site\coveragereport"  2>nul
rd /S /Q "petreminder-app\app\target\site\doxygen"  2>nul
rd /S /Q "petreminder-app\lib\target\site\coverxygen"  2>nul
rd /S /Q "petreminder-app\lib\target\site\coveragereport"  2>nul

echo.
echo [2/10] Clean docs and release folders...
rd /S /Q "docs"
mkdir docs
rd /S /Q "release"
mkdir release

echo.
echo [3/10] Maven clean, test, and package (multi-module)...
call mvn clean test package -f petreminder-app\pom.xml
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Maven build failed! Check the output above.
    pause
    exit /b 1
)

echo.
echo [4/10] Create required output folders...
mkdir "petreminder-app\app\target\site\coverxygen" 2>nul
mkdir "petreminder-app\app\target\site\coveragereport" 2>nul
mkdir "petreminder-app\app\target\site\doxygen" 2>nul

echo.
echo [5/10] Generate Doxygen LaTeX/XML Documentation...
call doxygen Doxyfile

echo.
echo [6/10] Compile LaTeX to PDF...
pushd petreminder-app\app\target\site\doxygen\latex
pdflatex -interaction=batchmode refman.tex
pdflatex -interaction=batchmode refman.tex
popd

echo.
echo [7/10] Copy Doxygen PDF to docs\ folder...
copy "petreminder-app\app\target\site\doxygen\latex\refman.pdf" "docs\petreminder-documentation.pdf"

echo.
echo [8/10] Generate ReportGenerator HTML Report and Badges...
echo NOTE: JaCoCo report is from the lib module (business logic)
call reportgenerator "-reports:petreminder-app\lib\target\site\jacoco\jacoco.xml" "-sourcedirs:petreminder-app\lib\src\main\java" "-targetdir:petreminder-app\app\target\site\coveragereport" -reporttypes:Html
call reportgenerator "-reports:petreminder-app\lib\target\site\jacoco\jacoco.xml" "-sourcedirs:petreminder-app\lib\src\main\java" "-targetdir:petreminder-app\app\target\site\coveragereport" -reporttypes:Badges

echo.
echo [9/10] Run Coverxygen (documentation coverage)...
call python -m coverxygen --xml-dir ./petreminder-app/app/target/site/doxygen/xml --src-dir ./ --format lcov --output ./petreminder-app/app/target/site/coverxygen/lcov.info --prefix %currentDir%/petreminder-app/

echo Run lcov genhtml...
call perl C:\ProgramData\chocolatey\lib\lcov\tools\bin\genhtml --legend --title "Documentation Coverage Report" ./petreminder-app/app/target/site/coverxygen/lcov.info -o petreminder-app/app/target/site/coverxygen

echo.
echo [10/10] Copy badges and package release artifacts...
call copy "petreminder-app\app\target\site\coveragereport\badge_combined.svg"      "assets\badge_combined.svg"
call copy "petreminder-app\app\target\site\coveragereport\badge_branchcoverage.svg" "assets\badge_branchcoverage.svg"
call copy "petreminder-app\app\target\site\coveragereport\badge_linecoverage.svg"   "assets\badge_linecoverage.svg"
call copy "petreminder-app\app\target\site\coveragereport\badge_methodcoverage.svg" "assets\badge_methodcoverage.svg"

call copy "assets\rteu_logo.jpg" "petreminder-app\app\src\site\resources\images\rteu_logo.jpg" 2>nul
call robocopy assets "petreminder-app\app\src\site\resources\assets" /E
call copy README.md "petreminder-app\app\src\site\markdown\readme.md" 2>nul

echo.
echo Generating Maven site...
call mvn site -f petreminder-app\pom.xml

echo.
echo Packaging release artifacts...
tar -czvf release\application-binary.tar.gz -C petreminder-app\app\target petreminder-app-2.0.0.jar
call tar -czvf release\test-jacoco-report.tar.gz       -C petreminder-app\lib\target\site\jacoco .
call tar -czvf release\test-coverage-report.tar.gz     -C petreminder-app\app\target\site\coveragereport .
call tar -czvf release\application-documentation.tar.gz -C petreminder-app\app\target\site\doxygen .
call tar -czvf release\doc-coverage-report.tar.gz      -C petreminder-app\app\target\site\coverxygen .
call tar -czvf release\application-site.tar.gz         -C petreminder-app\app\target\site .

echo.
echo =======================================================
echo  BUILD COMPLETE - Artifacts in .\release\
echo  Run app: java -jar petreminder-app\app\target\petreminder-app-2.0.0.jar
echo  JaCoCo:  petreminder-app\lib\target\site\jacoco\index.html
echo  Coverage: petreminder-app\app\target\site\coveragereport\index.html
echo =======================================================
pause

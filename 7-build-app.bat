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
echo [1/10] Clean docs and release folders...
rd /S /Q "docs" 2>nul
mkdir docs
rd /S /Q "release" 2>nul
mkdir release

echo.
echo [2/10] Maven clean, install, and package (multi-module)...
call mvn clean install -f petreminder-app\pom.xml
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Maven build failed! Check the output above.
    pause
    exit /b 1
)

echo.
echo [3/10] Create required output folders BEFORE Doxygen...
mkdir "petreminder-app\app\target\site\doxygen" 2>nul
mkdir "petreminder-app\app\target\site\coverxygen" 2>nul
mkdir "petreminder-app\app\target\site\coveragereport" 2>nul
mkdir "petreminder-app\app\target\site\assets" 2>nul
mkdir "petreminder-app\app\src\site\resources\images" 2>nul
mkdir "petreminder-app\app\src\site\markdown" 2>nul

echo.
echo [4/10] Generate Doxygen LaTeX/XML Documentation...
call doxygen Doxyfile
if %ERRORLEVEL% neq 0 (
    echo [WARN] Doxygen tamamlandi ama bazi uyarilar var. devam ediliyor...
)

echo.
echo [5/10] Compile LaTeX to PDF...
if exist "petreminder-app\app\target\site\doxygen\latex\refman.tex" (
    pushd petreminder-app\app\target\site\doxygen\latex
    pdflatex -interaction=batchmode refman.tex
    pdflatex -interaction=batchmode refman.tex
    popd
    copy "petreminder-app\app\target\site\doxygen\latex\refman.pdf" "docs\petreminder-documentation.pdf"
    echo [OK] PDF kopyalandi: docs\petreminder-documentation.pdf
) else (
    echo [WARN] LaTeX dosyasi bulunamadi, PDF olusturulamadi.
)

echo.
echo [6/10] Generate ReportGenerator HTML Report and Badges...
echo NOTE: JaCoCo report is from the lib module (business logic)
call reportgenerator "-reports:petreminder-app\lib\target\site\jacoco\jacoco.xml" "-sourcedirs:petreminder-app\lib\src\main\java" "-targetdir:petreminder-app\app\target\site\coveragereport" -reporttypes:Html
call reportgenerator "-reports:petreminder-app\lib\target\site\jacoco\jacoco.xml" "-sourcedirs:petreminder-app\lib\src\main\java" "-targetdir:petreminder-app\app\target\site\coveragereport" -reporttypes:Badges

echo.
echo [7/10] Run Coverxygen (documentation coverage)...
if exist "petreminder-app\app\target\site\doxygen\xml" (
    call python -m coverxygen --xml-dir ./petreminder-app/app/target/site/doxygen/xml --src-dir ./ --format lcov --output ./petreminder-app/app/target/site/coverxygen/lcov.info --prefix %currentDir%/petreminder-app/
    if %ERRORLEVEL% equ 0 (
        echo [OK] Coverxygen tamamlandi.
        echo [7b] Run genhtml...
        call genhtml --legend --title "Documentation Coverage Report" ./petreminder-app/app/target/site/coverxygen/lcov.info -o petreminder-app/app/target/site/coverxygen
    ) else (
        echo [WARN] Coverxygen calismadi, devam ediliyor...
    )
) else (
    echo [WARN] Doxygen XML dizini bulunamadi, coverxygen atlaniyor...
)

echo.
echo [8/10] Copy badges and assets...
if exist "petreminder-app\app\target\site\coveragereport\badge_combined.svg" (
    call copy "petreminder-app\app\target\site\coveragereport\badge_combined.svg"      "assets\badge_combined.svg"
    call copy "petreminder-app\app\target\site\coveragereport\badge_branchcoverage.svg" "assets\badge_branchcoverage.svg"
    call copy "petreminder-app\app\target\site\coveragereport\badge_linecoverage.svg"   "assets\badge_linecoverage.svg"
    call copy "petreminder-app\app\target\site\coveragereport\badge_methodcoverage.svg" "assets\badge_methodcoverage.svg"
    echo [OK] Badge'ler assets\ dizinine kopyalandi.
) else (
    echo [WARN] Badge dosyalari bulunamadi.
)

if exist "assets\rteu_logo.jpg" (
    call copy "assets\rteu_logo.jpg" "petreminder-app\app\src\site\resources\images\rteu_logo.jpg" 2>nul
)
call robocopy assets "petreminder-app\app\src\site\resources\assets" /E /NJH /NJS >nul 2>&1
call copy README.md "petreminder-app\app\src\site\markdown\readme.md" 2>nul

echo.
echo [9/10] Generate Maven site...
call mvn site -f petreminder-app\pom.xml
if %ERRORLEVEL% neq 0 (
    echo [WARN] Maven site olusturmada sorun var, devam ediliyor...
)

echo.
echo [10/10] Package release artifacts...
tar -czvf release\application-binary.tar.gz -C petreminder-app\app\target petreminder-app-2.0.0.jar 2>nul
call tar -czvf release\test-jacoco-report.tar.gz       -C petreminder-app\lib\target\site\jacoco . 2>nul
call tar -czvf release\test-coverage-report.tar.gz     -C petreminder-app\app\target\site\coveragereport . 2>nul

if exist "petreminder-app\app\target\site\doxygen" (
    call tar -czvf release\application-documentation.tar.gz -C petreminder-app\app\target\site\doxygen . 2>nul
)
if exist "petreminder-app\app\target\site\coverxygen\lcov.info" (
    call tar -czvf release\doc-coverage-report.tar.gz      -C petreminder-app\app\target\site\coverxygen . 2>nul
)
call tar -czvf release\application-site.tar.gz         -C petreminder-app\app\target\site . 2>nul

echo.
echo =======================================================
echo  BUILD COMPLETE - Artifacts in .\release\
echo  Run app   : 8-run-app.bat
echo  JaCoCo    : petreminder-app\lib\target\site\jacoco\index.html
echo  Coverage  : petreminder-app\app\target\site\coveragereport\index.html
echo  Doc Cover : petreminder-app\app\target\site\coverxygen\index.html
echo =======================================================
pause

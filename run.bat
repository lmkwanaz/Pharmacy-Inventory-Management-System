@echo off
setlocal
setlocal enabledelayedexpansion

set MYSQL_JAR=mysql-connector-j-8.4.0.jar

if not exist "%MYSQL_JAR%" (
    echo Missing %MYSQL_JAR% in project root.
    echo Download MySQL Connector/J and place the jar in this folder.
    exit /b 1
)

if not exist out mkdir out

set "JAVA_FILES="
for /r src %%f in (*.java) do (
    set "JAVA_FILES=!JAVA_FILES! \"%%f\""
)

javac -cp "%MYSQL_JAR%" -d out !JAVA_FILES!
if errorlevel 1 (
    echo Compilation failed.
    exit /b 1
)

java -cp "out;%MYSQL_JAR%" pims.Main
endlocal

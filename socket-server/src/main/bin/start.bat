@echo off
rem author: py
rem date: 2018-07-14
cd %~dp0
cd ..

rem set title
title ACCEL-PPP Server v1.0.0

set javahome=%JAVA_HOME%
set JAVA_OPTS=-server -Xms512m -Xmx512m -Xmn200m
if "%javahome%"=="" (
	echo error: JAVA_HOME not exist  
	pause
)
echo JAVA_HOME=%javahome%

rem read java version
if not exist "%javahome%/bin/java.exe" (
    echo error: %javahome%/bin/java.exe not exist  
    pause
)
if not exist logs (
   md logs
)
if not exist version (
   md version
)

"%javahome%/bin/java" -version 2>version/java.version
set /p jversion= < version/java.version
echo JAVA_VERSION=%jversion%

set v1=1.8
set v2=%jversion:~14,3%

if not %v1% LEQ %v2% (
	echo error: ACCEL-PPP Server rely on JRE version least is 1.8
	pause
)

echo Welcome to ACCEL-PPP Server
echo %DATE:~0,10% %TIME:~0,8% ACCEL-PPP Server is running...
"%javahome%/bin/java" %JAVA_OPTS% -cp libs/*;./ com.cmhy.boot.WebServer
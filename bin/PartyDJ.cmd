@echo OFF
@setlocal
set ENVIRONMENT_HOME=%~dp0..
set LIBDIR=%ENVIRONMENT_HOME%\lib

@set JDK=%ENVIRONMENT_HOME%\jdk
@set CLASSPATH=%JDK%/jre/lib/rt.jar;%JDK%/lib/tools.jar;%ENVIRONMENT_HOME%/classes;%LIBDIR%/javax.servlet/servlet.jar;%LIBDIR%/com.qotsa.JavaWinampAPI/JavaWinampAPI.jar
%JDK%\jre\bin\java -server -Xms256m -Xmx256m -XX:NewRatio=3 -XX:SurvivorRatio=5 -Djava.library.path=%LIBDIR%/com.qotsa.JavaWinampAPI/wpcom.dll com.partydj.server.PartyDJ %ENVIRONMENT_HOME%\bin\config.properties

if not errorlevel 1 goto :end
@echo Error running program
goto end

:end
@endlocal

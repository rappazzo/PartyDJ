@echo OFF
@setlocal
set ENVIRONMENT_HOME=%~dp0..
set LIBDIR=%ENVIRONMENT_HOME%\lib

@set JDK=%ENVIRONMENT_HOME%\jdk
@set CLASSPATH=%JDK%/jre/lib/rt.jar;%JDK%/lib/tools.jar;%ENVIRONMENT_HOME%/classes;%LIBDIR%/javax.servlet/servlet.jar;%LIBDIR%/com.qotsa.JavaWinampAPI/JavaWinampAPI.jar;%LIBDIR%/com.google.guava_r07/guava-r07.jar;%LIBDIR%/org.apache.lucene\lucene-core-3.0.3.jar
%JDK%\jre\bin\java -server -Xms256m -Xmx256m -XX:NewRatio=3 -XX:SurvivorRatio=5 -Djava.library.path=%LIBDIR%/com.qotsa.JavaWinampAPI com.partydj.server.PartyDJ %ENVIRONMENT_HOME%\bin\config.properties

if not errorlevel 1 goto :end
@echo Error running program
goto end

:end
@endlocal

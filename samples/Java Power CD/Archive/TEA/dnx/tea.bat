@echo off
echo Running TEA...
REM You can edit the classpath as necessary, if you know what you are doing.
REM This assumes that you installed the 1.0 (or later) Java Developers' Kit
REM in C:\Java.

REM For WinNT users, this will be useful if you use the set CLASSPATH line
REM setLocal

set CLASSPATH=C:\Java\lib;C:\Java\classes
REM
REM You can edit the argument to the -T (TEA dir) flag, to reflect your
REM installation.  The default is "." (the current directory).
C:\Java\bin\java dnx.tea.Editor -T C:\Java\dnx %1
REM
REM For WinNT users, this will be useful if you use the set CLASSPATH line
REM endLocal

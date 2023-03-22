@if "%DEBUG%"=="" @echo off
:: Copyright (c) 2021 Matthew Nelson
::
:: Licensed under the Apache License, Version 2.0 (the "License");
:: you may not use this file except in compliance with the License.
:: You may obtain a copy of the License at
::
::         https://www.apache.org/licenses/LICENSE-2.0
::
:: Unless required by applicable law or agreed to in writing, software
:: distributed under the License is distributed on an "AS IS" BASIS,
:: WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
:: See the License for the specific language governing permissions and
:: limitations under the License.

if "%OS%"=="Windows_NT" setlocal EnableDelayedExpansion

set TOOL=%~1

set PROJECT=false
if defined TOOL if not "!TOOL: =!"=="" if exist "tools\%TOOL%\*" set PROJECT=true

if "%PROJECT%"=="false" (
    echo Unknown tool: '%TOOL%'
    exit /b 1
)

set ARGS=%*
set ARGS=!ARGS:*%1=!
if "!ARGS:~0,1!"==" " set ARGS=!ARGS:~1!

call gradlew --quiet ":tools:%TOOL%:build" && call "tools\%TOOL%\build\bin\%TOOL%\releaseExecutable\%TOOL%.kexe" %ARGS%

if "%OS%"=="Windows_NT" endlocal

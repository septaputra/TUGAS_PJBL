@echo off
setlocal enabledelayedexpansion
set line_num=0

for /f "usebackq delims=" %%i in ("app\src\main\java\com\kelasxi\aplikasimonitoringkelas\AdminActivity.kt") do (
    set /a line_num=!line_num!+1
    set "line=%%i"
    if "!line!" equ "                            Text(\"Simpan\")" (
        echo Found at line !line_num!: !line!
        goto :end
    )
)

:end
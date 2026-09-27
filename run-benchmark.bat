@echo off
setlocal
chcp 65001 > nul
cd /d "%~dp0"

echo ===============================================================
echo  FastScreenCapture JMH Benchmark Runner
echo ===============================================================

echo [1/2] Installing FastScreenCapture Core...
call mvn clean install -DskipTests -q
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Core build failed!
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Building JMH Benchmark Uber-JAR...
cd examples\Benchmark
call mvn clean package -DskipTests -q
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Benchmark packaging failed!
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo ===============================================================
echo  Running JMH Benchmarks (Throughput: ops/ms)
echo ===============================================================
java --enable-native-access=ALL-UNNAMED -jar target\benchmarks.jar -wi 2 -i 3 -f 1 -tu ms -bm thrpt
cd ..\..
pause

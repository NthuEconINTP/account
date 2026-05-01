@echo off

echo ==========================
echo Build Spring Boot JAR
echo ==========================
call mvnw.cmd clean package -DskipTests

if errorlevel 1 (
    echo.
    echo Maven build failed.
    pause
    exit /b 1
)

echo.
echo ==========================
echo Build Docker Image
echo ==========================
docker build -t account-app:1.0 .

if errorlevel 1 (
    echo.
    echo Docker build failed.
    pause
    exit /b 1
)

echo.
echo ==========================
echo Stop Compose Services
echo ==========================
docker compose down

echo.
echo ==========================
echo Start Compose Services
echo ==========================
docker compose up -d

if errorlevel 1 (
    echo.
    echo Docker compose up failed.
    pause
    exit /b 1
)

echo.
echo ==========================
echo Current Compose Services
echo ==========================
docker compose ps

echo.
pause
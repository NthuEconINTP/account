@echo off

echo ==========================
echo Build Multi-stage Docker Image
echo ==========================
docker build -f Dockerfile.multistage -t account-app:multi .

if errorlevel 1 (
    echo.
    echo Docker multi-stage build failed.
    pause
    exit /b 1
)

echo.
echo ==========================
echo Stop Compose Services
echo ==========================
docker compose -f docker-compose.yml -f docker-compose.multi.yml down

echo.
echo ==========================
echo Start Compose Services with Multi-stage Image
echo ==========================
docker compose -f docker-compose.yml -f docker-compose.multi.yml up -d

if errorlevel 1 (
    echo.
    echo Docker compose up with multi-stage image failed.
    pause
    exit /b 1
)

echo.
echo ==========================
echo Current Compose Services
echo ==========================
docker compose -f docker-compose.yml -f docker-compose.multi.yml ps

echo.
echo ==========================
echo Account App Images
echo ==========================
docker images account-app

echo.
pause
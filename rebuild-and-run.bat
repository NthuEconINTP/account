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
echo Remove Old Container
echo ==========================
docker stop account-container
docker rm account-container

echo.
echo ==========================
echo Run New Container
echo ==========================
docker run -d -p 8080:8080 --name account-container ^
-e SPRING_PROFILES_ACTIVE=docker ^
-e DB_URL="jdbc:mysql://host.docker.internal:3306/notebook?useSSL=false&serverTimezone=Asia/Taipei&allowPublicKeyRetrieval=true" ^
-e DB_USERNAME=root ^
-e DB_PASSWORD=12111211 ^
account-app:1.0

echo.
echo ==========================
echo Current Containers
echo ==========================
docker ps

echo.
pause
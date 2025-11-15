@echo off
REM ###############################################################################
REM Achat DevOps Pipeline - Quick Start Script (Windows)
REM This script helps you start all DevOps services quickly
REM ###############################################################################

echo ==============================================
echo    Achat DevOps Pipeline - Quick Start
echo ==============================================
echo.

REM Check if Docker is running
docker version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not running!
    echo Please start Docker Desktop first.
    pause
    exit /b 1
)

echo [OK] Docker is running
echo.

REM Stop any existing containers
echo [INFO] Stopping existing containers...
docker-compose down 2>nul

REM Start all services
echo [INFO] Starting all DevOps services...
docker-compose up -d

echo.
echo [INFO] Waiting for services to be ready...
timeout /t 15 /nobreak >nul

REM Check service status
echo.
echo [INFO] Service Status:
docker-compose ps

echo.
echo ==============================================
echo [SUCCESS] All services started!
echo ==============================================
echo.
echo Access URLs:
echo.
echo   Jenkins:     http://localhost:8080
echo   SonarQube:   http://localhost:9000
echo   Nexus:       http://localhost:8081
echo   Prometheus:  http://localhost:9090
echo   Grafana:     http://localhost:3000
echo   MySQL:       localhost:3306
echo   Application: http://localhost:8089/SpringMVC
echo.
echo ==============================================
echo.
echo Default Credentials:
echo.
echo   Jenkins:   admin / (get from container)
echo   SonarQube: admin / admin
echo   Nexus:     admin / (get from container)
echo   Grafana:   admin / admin
echo   MySQL:     root / root
echo.
echo ==============================================
echo.
echo Next Steps:
echo.
echo 1. Get Jenkins initial password:
echo    docker exec achat-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
echo.
echo 2. Get Nexus initial password:
echo    docker exec achat-nexus cat /nexus-data/admin.password
echo.
echo 3. Follow the guide: JENKINS-SETUP-GUIDE.md
echo.
echo ==============================================
echo.

REM Get Jenkins password automatically
echo [INFO] Getting Jenkins initial password...
timeout /t 5 /nobreak >nul
echo.
docker exec achat-jenkins cat /var/jenkins_home/secrets/initialAdminPassword 2>nul
if errorlevel 1 (
    echo [WARN] Jenkins is still starting up. Try again in a minute.
)
echo.

echo ==============================================
echo [SUCCESS] Setup complete! Happy DevOps!
echo ==============================================
echo.
pause


@echo off
echo ========================================
echo   Complete DevOps Pipeline Setup
echo ========================================
echo.

echo Step 1: Starting all services...
docker-compose up -d

echo.
echo Step 2: Waiting for services to initialize (60 seconds)...
timeout /t 60 /nobreak

echo.
echo Step 3: Checking service status...
docker-compose ps

echo.
echo ========================================
echo   Services are starting!
echo ========================================
echo.
echo Jenkins:     http://localhost:8080
echo SonarQube:   http://localhost:9000
echo Nexus:       http://localhost:8081
echo Prometheus:  http://localhost:9090
echo Grafana:     http://localhost:3000
echo Application: http://localhost:8089/SpringMVC/
echo.
echo ========================================
echo   Next Steps:
echo ========================================
echo.
echo 1. Get Jenkins password:
echo    docker exec achat-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
echo.
echo 2. Configure Jenkins pipeline (see COMPLETE-DEVOPS-GUIDE.md)
echo.
echo 3. For Kubernetes setup, read: KUBERNETES-SETUP-GUIDE.md
echo 4. For Terraform setup, read: TERRAFORM-SETUP-GUIDE.md
echo 5. For Monitoring setup, read: MONITORING-SETUP-GUIDE.md
echo.
echo ========================================

pause


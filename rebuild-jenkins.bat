@echo off
echo 🔄 Rebuilding Jenkins with Docker support...
echo.

REM Stop and remove old Jenkins container
docker-compose stop jenkins
docker-compose rm -f jenkins

REM Build new Jenkins image with Docker CLI
docker-compose build jenkins

REM Start Jenkins
docker-compose up -d jenkins

echo.
echo ✅ Jenkins rebuilt successfully!
echo.
echo 📋 Verifying Docker installation in Jenkins...
docker exec -u root achat-jenkins docker --version

echo.
echo 🎉 Done! Jenkins now has Docker support
echo 🌐 Access Jenkins at: http://localhost:8080
pause


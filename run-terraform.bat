@echo off
echo ========================================
echo   Terraform Operations Helper
echo ========================================
echo.

if "%1"=="" (
    echo Usage: run-terraform.bat [action]
    echo.
    echo Actions:
    echo   init      - Initialize Terraform
    echo   validate  - Validate configuration
    echo   plan      - Create execution plan
    echo   apply     - Apply changes
    echo   destroy   - Destroy infrastructure
    echo   output    - Show outputs
    echo   show      - Show current state
    echo.
    pause
    exit /b 1
)

echo Running Terraform action: %1
echo.

docker exec -u root achat-jenkins /bin/bash -c "cd /var/jenkins_home/workspace/terraform && terraform %1"

echo.
echo ========================================
echo   Operation completed!
echo ========================================
pause


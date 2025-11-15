#!/bin/bash

echo "========================================"
echo "  Rebuilding Jenkins with Terraform"
echo "========================================"
echo ""
echo "This will install:"
echo "- Docker CLI"
echo "- Maven"
echo "- Terraform"
echo "- kubectl"
echo "- AWS CLI"
echo ""

echo "Stopping Jenkins..."
docker-compose stop jenkins

echo "Removing old container..."
docker-compose rm -f jenkins

echo "Building new Jenkins image (this may take 5-10 minutes)..."
docker-compose build jenkins

echo "Starting Jenkins..."
docker-compose up -d jenkins

echo ""
echo "Waiting for Jenkins to start (60 seconds)..."
sleep 60

echo ""
echo "========================================"
echo "  Verifying installations..."
echo "========================================"
echo ""

echo "Checking Terraform:"
docker exec -u root achat-jenkins terraform version

echo ""
echo "Checking kubectl:"
docker exec -u root achat-jenkins kubectl version --client

echo ""
echo "Checking AWS CLI:"
docker exec -u root achat-jenkins aws --version

echo ""
echo "Checking Docker:"
docker exec -u root achat-jenkins docker --version

echo ""
echo "========================================"
echo "  Jenkins rebuilt successfully!"
echo "========================================"
echo ""
echo "Jenkins URL: http://localhost:8080"
echo ""
echo "Next steps:"
echo "1. Configure AWS credentials in Jenkins"
echo "2. Read: TERRAFORM-IN-JENKINS.md"
echo "3. Run: ./terraform-helper.sh init"
echo ""

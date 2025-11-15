#!/bin/bash

###############################################################################
# Achat DevOps Pipeline - Quick Start Script
# This script helps you start all DevOps services quickly
###############################################################################

echo "=============================================="
echo "   Achat DevOps Pipeline - Quick Start"
echo "=============================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker is not installed!${NC}"
    echo "Please install Docker first: https://docs.docker.com/get-docker/"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}❌ Docker Compose is not installed!${NC}"
    echo "Please install Docker Compose first"
    exit 1
fi

echo -e "${GREEN}✅ Docker and Docker Compose found${NC}"
echo ""

# Stop any existing containers
echo -e "${YELLOW}🛑 Stopping existing containers...${NC}"
docker-compose down 2>/dev/null

# Start all services
echo -e "${BLUE}🚀 Starting all DevOps services...${NC}"
docker-compose up -d

echo ""
echo -e "${YELLOW}⏳ Waiting for services to be ready...${NC}"
sleep 10

# Check service status
echo ""
echo -e "${BLUE}📊 Service Status:${NC}"
docker-compose ps

echo ""
echo "=============================================="
echo -e "${GREEN}✅ All services started!${NC}"
echo "=============================================="
echo ""
echo "📌 Access URLs:"
echo ""
echo "  🔨 Jenkins:     http://localhost:8080"
echo "  📊 SonarQube:   http://localhost:9000"
echo "  📦 Nexus:       http://localhost:8081"
echo "  📈 Prometheus:  http://localhost:9090"
echo "  📉 Grafana:     http://localhost:3000"
echo "  🗄️  MySQL:       localhost:3306"
echo "  🚀 Application: http://localhost:8089/SpringMVC"
echo ""
echo "=============================================="
echo ""
echo "🔑 Default Credentials:"
echo ""
echo "  Jenkins:   admin / (get from container)"
echo "  SonarQube: admin / admin"
echo "  Nexus:     admin / (get from container)"
echo "  Grafana:   admin / admin"
echo "  MySQL:     root / root"
echo ""
echo "=============================================="
echo ""
echo -e "${YELLOW}📖 Next Steps:${NC}"
echo ""
echo "1. Get Jenkins initial password:"
echo "   docker exec achat-jenkins cat /var/jenkins_home/secrets/initialAdminPassword"
echo ""
echo "2. Get Nexus initial password:"
echo "   docker exec achat-nexus cat /nexus-data/admin.password"
echo ""
echo "3. Follow the guide: JENKINS-SETUP-GUIDE.md"
echo ""
echo "=============================================="
echo ""

# Function to get Jenkins password
get_jenkins_password() {
    echo -e "${BLUE}Getting Jenkins initial password...${NC}"
    sleep 5
    JENKINS_PASSWORD=$(docker exec achat-jenkins cat /var/jenkins_home/secrets/initialAdminPassword 2>/dev/null)
    if [ ! -z "$JENKINS_PASSWORD" ]; then
        echo -e "${GREEN}Jenkins Password: ${JENKINS_PASSWORD}${NC}"
    else
        echo -e "${YELLOW}⏳ Jenkins is still starting up. Try again in a minute.${NC}"
    fi
}

# Ask if user wants to see Jenkins password now
echo -e "${YELLOW}Do you want to get Jenkins password now? (y/n)${NC}"
read -t 10 -n 1 answer
echo ""
if [[ $answer == "y" || $answer == "Y" ]]; then
    get_jenkins_password
fi

echo ""
echo -e "${GREEN}🎉 Setup complete! Happy DevOps! 🚀${NC}"
echo ""


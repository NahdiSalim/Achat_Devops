# 🚀 DevOps Pipeline - Quick Reference Commands

## Table of Contents
1. [Docker Commands](#docker-commands)
2. [Jenkins Commands](#jenkins-commands)
3. [Kubernetes Commands](#kubernetes-commands)
4. [Maven Commands](#maven-commands)
5. [AWS & Terraform Commands](#aws--terraform-commands)
6. [Troubleshooting Commands](#troubleshooting-commands)

---

## Docker Commands

### Start All Services
```bash
# Start all services in background
docker-compose up -d

# Start and view logs
docker-compose up

# Start specific service
docker-compose up -d jenkins
```

### Stop Services
```bash
# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ DELETES DATA)
docker-compose down -v

# Stop specific service
docker-compose stop jenkins
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f jenkins
docker-compose logs -f sonarqube
docker-compose logs -f nexus

# Last 100 lines
docker-compose logs --tail=100 jenkins
```

### Check Status
```bash
# View running containers
docker-compose ps

# View detailed status
docker-compose ps -a
```

### Restart Services
```bash
# Restart all
docker-compose restart

# Restart specific service
docker-compose restart jenkins
```

### Access Container Shell
```bash
# Jenkins
docker exec -it achat-jenkins bash

# SonarQube
docker exec -it achat-sonarqube bash

# MySQL
docker exec -it achat-mysql bash
```

### View Resource Usage
```bash
docker stats
```

### Clean Up
```bash
# Remove unused images
docker image prune -a

# Remove unused volumes
docker volume prune

# Complete cleanup (⚠️ USE CAREFULLY)
docker system prune -a --volumes
```

---

## Jenkins Commands

### Get Initial Password
```bash
# Windows
docker exec achat-jenkins cat /var/jenkins_home/secrets/initialAdminPassword

# Linux/Mac
docker exec achat-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### Restart Jenkins
```bash
docker-compose restart jenkins
```

### View Jenkins Logs
```bash
docker-compose logs -f jenkins
```

### Backup Jenkins Data
```bash
# Create backup directory
mkdir -p backups

# Backup Jenkins home
docker cp achat-jenkins:/var/jenkins_home ./backups/jenkins_home_backup
```

### Fix Docker Permission in Jenkins
```bash
docker exec -u root achat-jenkins chmod 666 /var/run/docker.sock
```

### Install Additional Tools in Jenkins
```bash
# Access Jenkins container as root
docker exec -u root -it achat-jenkins bash

# Inside container:
apt-get update
apt-get install -y kubectl
apt-get install -y terraform
```

---

## Kubernetes Commands

### Cluster Info
```bash
# View cluster info
kubectl cluster-info

# View nodes
kubectl get nodes

# View all resources
kubectl get all
```

### Deploy Application
```bash
# Apply all manifests
kubectl apply -f k8s/

# Apply specific manifest
kubectl apply -f k8s/deployment.yaml

# Delete resources
kubectl delete -f k8s/
```

### View Resources
```bash
# Deployments
kubectl get deployments
kubectl describe deployment achat-app

# Pods
kubectl get pods
kubectl get pods -o wide
kubectl describe pod <pod-name>

# Services
kubectl get services
kubectl get svc

# ConfigMaps and Secrets
kubectl get configmaps
kubectl get secrets
```

### View Logs
```bash
# View pod logs
kubectl logs <pod-name>

# Follow logs
kubectl logs -f <pod-name>

# Previous logs (if pod restarted)
kubectl logs <pod-name> --previous

# Logs from all pods in deployment
kubectl logs -l app=achat
```

### Execute Commands in Pod
```bash
# Access pod shell
kubectl exec -it <pod-name> -- bash

# Run single command
kubectl exec <pod-name> -- ls -la
```

### Port Forwarding
```bash
# Forward local port to pod
kubectl port-forward <pod-name> 8089:8089

# Forward to service
kubectl port-forward service/achat-service 8089:8089
```

### Scaling
```bash
# Scale deployment
kubectl scale deployment achat-app --replicas=3

# Auto-scale
kubectl autoscale deployment achat-app --min=2 --max=10 --cpu-percent=80
```

### Rollout Management
```bash
# View rollout status
kubectl rollout status deployment/achat-app

# View rollout history
kubectl rollout history deployment/achat-app

# Rollback to previous version
kubectl rollout undo deployment/achat-app

# Rollback to specific revision
kubectl rollout undo deployment/achat-app --to-revision=2
```

### Debug
```bash
# View events
kubectl get events

# Sort events by time
kubectl get events --sort-by=.metadata.creationTimestamp

# Debug pod
kubectl debug <pod-name> -it --image=busybox
```

### Create Secret for DockerHub
```bash
kubectl create secret docker-registry regcred \
  --docker-server=https://index.docker.io/v1/ \
  --docker-username=YOUR_USERNAME \
  --docker-password=YOUR_PASSWORD \
  --docker-email=YOUR_EMAIL
```

---

## Maven Commands

### Build Project
```bash
# Clean and compile
./mvnw clean compile

# Package (create JAR)
./mvnw clean package

# Package without tests
./mvnw clean package -DskipTests
```

### Testing
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=FournisseurServiceImplTest

# Run specific test method
./mvnw test -Dtest=FournisseurServiceImplTest#testRetrieveAllFournisseurs

# Generate coverage report
./mvnw clean test jacoco:report
```

### SonarQube Analysis
```bash
# Run SonarQube analysis
./mvnw sonar:sonar \
  -Dsonar.projectKey=achat-project \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=YOUR_SONAR_TOKEN
```

### Deploy to Nexus
```bash
./mvnw deploy
```

### Dependency Management
```bash
# View dependency tree
./mvnw dependency:tree

# Download dependencies
./mvnw dependency:resolve

# Update dependencies
./mvnw versions:display-dependency-updates
```

### Clean Build
```bash
# Complete clean build
./mvnw clean install

# Skip tests
./mvnw clean install -DskipTests

# Offline mode
./mvnw clean install -o
```

---

## AWS & Terraform Commands

### AWS CLI Configuration
```bash
# Configure AWS credentials
aws configure

# Test credentials
aws sts get-caller-identity

# List EC2 instances
aws ec2 describe-instances

# List S3 buckets
aws s3 ls
```

### Terraform Commands
```bash
# Navigate to terraform directory
cd terraform

# Initialize Terraform
terraform init

# Validate configuration
terraform validate

# Plan infrastructure changes
terraform plan

# Apply changes (with confirmation)
terraform apply

# Apply changes (auto-approve)
terraform apply -auto-approve

# Destroy infrastructure
terraform destroy

# Show current state
terraform show

# List resources
terraform state list

# Output values
terraform output
```

### EKS Commands
```bash
# Create EKS cluster
eksctl create cluster \
  --name achat-cluster \
  --region us-east-1 \
  --nodegroup-name achat-nodes \
  --node-type t3.medium \
  --nodes 2

# Update kubeconfig
aws eks update-kubeconfig --region us-east-1 --name achat-cluster

# Delete cluster
eksctl delete cluster --name achat-cluster --region us-east-1

# View cluster info
eksctl get cluster
```

### S3 Commands
```bash
# Create bucket
aws s3 mb s3://achat-terraform-state

# Enable versioning
aws s3api put-bucket-versioning \
  --bucket achat-terraform-state \
  --versioning-configuration Status=Enabled

# List bucket contents
aws s3 ls s3://achat-terraform-state

# Sync directory to S3
aws s3 sync ./backups s3://achat-backups
```

---

## Troubleshooting Commands

### Check Service Health
```bash
# Jenkins
curl http://localhost:8080/login

# SonarQube
curl http://localhost:9000/api/system/health

# Nexus
curl http://localhost:8081/service/rest/v1/status

# Application
curl http://localhost:8089/SpringMVC/actuator/health

# Prometheus
curl http://localhost:9090/-/healthy

# Grafana
curl http://localhost:3000/api/health
```

### View Service Logs
```bash
# All services
docker-compose logs -f

# Specific service with timestamp
docker-compose logs -f --timestamps jenkins
```

### Check Network Connectivity
```bash
# Test network between containers
docker exec achat-jenkins ping achat-sonarqube

# View networks
docker network ls

# Inspect network
docker network inspect achat-network
```

### Resource Usage
```bash
# Docker stats
docker stats --no-stream

# Disk usage
docker system df

# Detailed disk usage
docker system df -v
```

### Clean Up Issues
```bash
# Remove stopped containers
docker container prune

# Remove dangling images
docker image prune

# Remove unused volumes
docker volume prune

# Remove unused networks
docker network prune
```

### Database Issues
```bash
# Access MySQL
docker exec -it achat-mysql mysql -uroot -proot

# Inside MySQL:
SHOW DATABASES;
USE achatdb;
SHOW TABLES;
SELECT * FROM fournisseur;

# Backup database
docker exec achat-mysql mysqldump -uroot -proot achatdb > backup.sql

# Restore database
docker exec -i achat-mysql mysql -uroot -proot achatdb < backup.sql
```

### Jenkins Issues
```bash
# Check if Jenkins can access Docker
docker exec achat-jenkins docker ps

# Fix Docker socket permission
docker exec -u root achat-jenkins chmod 666 /var/run/docker.sock

# View Jenkins system log
docker exec achat-jenkins cat /var/jenkins_home/jenkins.log

# Restart Jenkins safely
docker-compose restart jenkins
```

### SonarQube Issues
```bash
# Check SonarQube logs
docker-compose logs -f sonarqube

# Access SonarQube database
docker exec -it achat-sonarqube-db psql -U sonar

# Restart SonarQube
docker-compose restart sonarqube sonarqube-db
```

### Port Conflicts
```bash
# Check what's using port 8080
# Windows
netstat -ano | findstr :8080

# Linux/Mac
lsof -i :8080

# Kill process on port (Linux/Mac)
kill -9 $(lsof -ti:8080)
```

---

## Common Workflows

### Complete Build & Deploy
```bash
# 1. Start services
docker-compose up -d

# 2. Build application
./mvnw clean package

# 3. Build Docker image
docker build -t salimnahdi/docker-spring-boot:latest .

# 4. Push to DockerHub
docker push salimnahdi/docker-spring-boot:latest

# 5. Deploy to Kubernetes
kubectl apply -f k8s/

# 6. Check deployment
kubectl get pods -w
```

### Update Application
```bash
# 1. Make code changes
# 2. Commit to Git
git add .
git commit -m "Update: description"
git push origin main

# 3. Jenkins will automatically build and deploy
# Or trigger manually in Jenkins UI
```

### View Application Metrics
```bash
# Health check
curl http://localhost:8089/SpringMVC/actuator/health

# Metrics
curl http://localhost:8089/SpringMVC/actuator/metrics

# Prometheus metrics
curl http://localhost:8089/SpringMVC/actuator/prometheus

# Info
curl http://localhost:8089/SpringMVC/actuator/info
```

---

## Quick Commands Summary

```bash
# Start everything
docker-compose up -d && ./mvnw clean package

# Stop everything
docker-compose down

# Rebuild and restart
docker-compose down && docker-compose up -d --build

# View all logs
docker-compose logs -f

# Check all services
docker-compose ps && kubectl get all

# Complete cleanup
docker-compose down -v && docker system prune -a -f
```

---

## 📝 Notes

- Replace `YOUR_USERNAME`, `YOUR_PASSWORD`, `YOUR_TOKEN` with actual values
- Be careful with commands marked with ⚠️
- Always backup data before destructive operations
- Check logs when troubleshooting issues

---

**Last Updated:** 2024
**For More Help:** See JENKINS-SETUP-GUIDE.md


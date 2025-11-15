# 🚀 Getting Started with Achat DevOps Pipeline

Welcome! This guide will help you get your complete DevOps pipeline up and running in **30 minutes**.

---

## 📋 What You're Building

A complete CI/CD pipeline that automates:
- ✅ Code checkout from GitHub
- ✅ Automated testing with JUnit
- ✅ Code quality analysis with SonarQube
- ✅ Code coverage with JaCoCo
- ✅ Artifact management with Nexus
- ✅ Docker image creation and push to DockerHub
- ✅ Kubernetes deployment
- ✅ Infrastructure as Code with Terraform
- ✅ Monitoring with Prometheus & Grafana

---

## 🎯 Quick Start (3 Steps)

### Step 1: Start All Services (2 minutes)

**Windows:**
```bash
start-devops-pipeline.bat
```

**Linux/Mac:**
```bash
chmod +x start-devops-pipeline.sh
./start-devops-pipeline.sh
```

**Or manually:**
```bash
docker-compose up -d
```

Wait 1-2 minutes for all services to start.

### Step 2: Verify Services (1 minute)

Check that all services are running:
```bash
docker-compose ps
```

You should see 7 services running:
- ✅ achat-jenkins
- ✅ achat-sonarqube
- ✅ achat-sonarqube-db
- ✅ achat-nexus
- ✅ achat-mysql
- ✅ achat-prometheus
- ✅ achat-grafana

### Step 3: Access Services

Open these URLs in your browser:

| Service | URL | Default Credentials |
|---------|-----|---------------------|
| **Jenkins** | http://localhost:8080 | admin / (see below) |
| **SonarQube** | http://localhost:9000 | admin / admin |
| **Nexus** | http://localhost:8081 | admin / (see below) |
| **Grafana** | http://localhost:3000 | admin / admin |
| **Prometheus** | http://localhost:9090 | None |

**Get Jenkins Password:**
```bash
docker exec achat-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

**Get Nexus Password:**
```bash
docker exec achat-nexus cat /nexus-data/admin.password
```

---

## 📖 Detailed Setup (Choose Your Path)

### 🎓 Path 1: Learn by Doing (Recommended for Beginners)

Follow this order:

1. **[JENKINS-SETUP-GUIDE.md](JENKINS-SETUP-GUIDE.md)** ⭐ START HERE
   - Complete Jenkins initial setup (15 minutes)
   - Configure all plugins and tools
   - Setup credentials
   - Create your first pipeline

2. **Test Your Pipeline** (5 minutes)
   - Make a small code change
   - Commit and push to GitHub
   - Watch Jenkins build automatically

3. **[PIPELINE-COMMANDS.md](PIPELINE-COMMANDS.md)**
   - Quick reference for all commands
   - Troubleshooting tips

4. **Setup Kubernetes** (Later)
   - Install Minikube or use AWS EKS
   - Deploy to Kubernetes

5. **Setup AWS** (Later)
   - Configure AWS credentials
   - Deploy infrastructure with Terraform

### 🚀 Path 2: Quick Deploy (For Experienced Users)

If you know Jenkins and Docker:

```bash
# 1. Start services
docker-compose up -d

# 2. Configure Jenkins (use JENKINS-SETUP-GUIDE.md sections 1-4)
# - Install plugins
# - Add credentials (DockerHub, SonarQube, Nexus)
# - Create pipeline job

# 3. Build project
./mvnw clean test

# 4. Run pipeline
# Go to Jenkins → Achat-DevOps-Pipeline → Build Now

# 5. Check results
# - SonarQube: http://localhost:9000
# - Nexus: http://localhost:8081
# - DockerHub: https://hub.docker.com
```

---

## 🛠️ What's Been Configured

### ✅ Jenkins Pipeline (Jenkinsfile)

Your pipeline has **15 stages**:

1. **🔍 Checkout Git** - Pull code from GitHub
2. **🔧 Preparation** - Setup build environment
3. **🧹 Clean** - Clean previous builds
4. **🔨 Compile** - Compile source code
5. **🧪 Unit Tests** - Run all tests + coverage
6. **📦 Package** - Create JAR file
7. **📊 SonarQube Analysis** - Code quality check
8. **🔍 Quality Gate** - Verify code quality
9. **📤 Publish to Nexus** - Upload artifact
10. **🐳 Build Docker Image** - Create container image
11. **🔒 Docker Scan** - Security scan
12. **📤 Push Docker Image** - Push to DockerHub
13. **🧹 Cleanup** - Remove local images
14. **☸️ Deploy to Kubernetes** - Deploy application
15. **📈 Configure Monitoring** - Setup metrics

### ✅ Kubernetes Manifests (k8s/)

Ready-to-deploy K8s configs:
- `namespace.yaml` - Dedicated namespace
- `configmap.yaml` - Application configuration
- `secrets.yaml` - Sensitive data
- `mysql-deployment.yaml` - Database
- `deployment.yaml` - Application deployment (2 replicas)
- `service.yaml` - LoadBalancer service
- `ingress.yaml` - Ingress rules
- `hpa.yaml` - Auto-scaling (2-10 pods)

### ✅ Docker Compose (docker-compose.yml)

All services pre-configured:
- Jenkins with Docker access
- SonarQube with PostgreSQL
- Nexus repository
- MySQL database
- Prometheus monitoring
- Grafana dashboards

### ✅ Application Monitoring (Spring Boot Actuator)

Endpoints available at `http://localhost:8089/SpringMVC/actuator/`:
- `/health` - Health check
- `/metrics` - Application metrics
- `/prometheus` - Prometheus metrics
- `/info` - Application info

---

## 🎯 Your Learning Journey

### Week 1: Jenkins & Basic Pipeline
- ✅ Start all services
- ✅ Setup Jenkins
- ✅ Run first pipeline
- ✅ Understand each stage
- **Goal:** Green build in Jenkins ✅

### Week 2: Code Quality & Testing
- ✅ Review SonarQube results
- ✅ Improve code coverage
- ✅ Fix code smells
- ✅ Setup quality gates
- **Goal:** 80% code coverage ✅

### Week 3: Docker & Registry
- ✅ Understand Dockerfile
- ✅ Build custom images
- ✅ Push to DockerHub
- ✅ Manage Nexus artifacts
- **Goal:** Automated Docker builds ✅

### Week 4: Kubernetes
- ✅ Install Minikube
- ✅ Understand K8s manifests
- ✅ Deploy application
- ✅ Scale deployment
- **Goal:** Running in Kubernetes ✅

### Week 5: Monitoring
- ✅ Configure Prometheus
- ✅ Create Grafana dashboards
- ✅ Setup alerts
- ✅ Monitor application
- **Goal:** Full observability ✅

### Week 6: AWS & Production
- ✅ Setup AWS account
- ✅ Configure Terraform
- ✅ Deploy to AWS EKS
- ✅ Production deployment
- **Goal:** Running in cloud ✅

---

## 🔥 Common First-Time Issues

### Issue 1: Port Already in Use

**Error:** "Port 8080 is already in use"

**Solution:**
```bash
# Windows - Check what's using port 8080
netstat -ano | findstr :8080

# Kill the process or change docker-compose.yml ports
```

### Issue 2: Docker Permission Denied

**Error:** "Permission denied while trying to connect to Docker daemon"

**Solution:**
```bash
# Make sure Docker Desktop is running
# Or fix permissions:
docker exec -u root achat-jenkins chmod 666 /var/run/docker.sock
```

### Issue 3: Jenkins Can't Find Docker

**Error:** "docker: command not found"

**Solution:**
- Verify Jenkins has Docker access:
```bash
docker exec achat-jenkins docker ps
```

### Issue 4: SonarQube Won't Start

**Error:** "max virtual memory areas vm.max_map_count [65530] is too low"

**Solution (Linux):**
```bash
sudo sysctl -w vm.max_map_count=262144
```

**Solution (Windows with WSL2):**
```bash
wsl -d docker-desktop
sysctl -w vm.max_map_count=262144
```

### Issue 5: Tests Fail Locally

**Error:** Tests pass in IDE but fail in Maven

**Solution:**
```bash
# Use test application.properties
./mvnw clean test

# Check test resources
ls -la src/test/resources/
```

---

## 📊 Success Metrics

You'll know everything is working when:

### ✅ Jenkins Dashboard
- Pipeline shows all stages in green
- Build time: ~5-10 minutes
- All tests passing
- Artifacts archived

### ✅ SonarQube Project
- Code coverage: >70%
- No blocker issues
- Quality gate: PASSED
- Bugs: 0, Vulnerabilities: 0

### ✅ Nexus Repository
- Artifact uploaded successfully
- Version matches build number
- Download link works

### ✅ DockerHub
- Image tagged with build number
- Image tagged as latest
- Image size: ~200MB

### ✅ Kubernetes Cluster
- Deployment: 2/2 pods running
- Service: LoadBalancer with external IP
- Application accessible
- Health check: UP

---

## 🎓 Learning Resources

### Official Documentation
- **Jenkins:** https://www.jenkins.io/doc/
- **SonarQube:** https://docs.sonarqube.org/
- **Kubernetes:** https://kubernetes.io/docs/
- **Docker:** https://docs.docker.com/
- **Terraform:** https://www.terraform.io/docs/

### Video Tutorials
- Jenkins Pipeline Tutorial
- Kubernetes for Beginners
- Docker Mastery Course
- AWS EKS Workshop

### Practice Projects
1. Add integration tests
2. Implement blue-green deployment
3. Setup staging environment
4. Add security scanning (Trivy)
5. Implement GitOps with ArgoCD

---

## 🤝 Need Help?

### 1. Check the Guides
- **JENKINS-SETUP-GUIDE.md** - Detailed Jenkins setup
- **PIPELINE-COMMANDS.md** - Command reference
- **DEVOPS-GUIDE.md** - DevOps concepts

### 2. Check Logs
```bash
# View all logs
docker-compose logs -f

# Specific service
docker-compose logs -f jenkins
```

### 3. Debug Mode
```bash
# Check service health
docker-compose ps

# Test connectivity
docker exec achat-jenkins ping achat-sonarqube

# View resources
docker stats
```

### 4. Common Commands
```bash
# Restart everything
docker-compose restart

# Rebuild from scratch
docker-compose down -v
docker-compose up -d --build

# Clean Docker
docker system prune -a
```

---

## 🎯 Next Steps

### Right Now (30 minutes)
1. ✅ Run `docker-compose up -d`
2. ✅ Access Jenkins: http://localhost:8080
3. ✅ Follow **JENKINS-SETUP-GUIDE.md** Phases 1-4
4. ✅ Create your first pipeline
5. ✅ Run "Build Now"

### This Week
1. ✅ Understand each pipeline stage
2. ✅ Review SonarQube analysis
3. ✅ Check Nexus artifacts
4. ✅ Verify DockerHub images
5. ✅ Setup Grafana dashboards

### Next Week
1. ✅ Install Kubernetes (Minikube/EKS)
2. ✅ Deploy to Kubernetes
3. ✅ Setup monitoring alerts
4. ✅ Configure Terraform
5. ✅ Plan AWS deployment

---

## 🎉 You're Ready!

Everything is configured and ready to go. Your next step is:

### 👉 **Open JENKINS-SETUP-GUIDE.md and start Phase 1!**

The guide will walk you through:
- ✅ Jenkins initial setup
- ✅ Plugin installation
- ✅ Credential configuration
- ✅ Pipeline creation
- ✅ First successful build

**Expected Time:** 20-30 minutes for complete setup

---

## 📝 Quick Reference

### Services URLs
```
Jenkins:     http://localhost:8080
SonarQube:   http://localhost:9000
Nexus:       http://localhost:8081
Grafana:     http://localhost:3000
Prometheus:  http://localhost:9090
Application: http://localhost:8089/SpringMVC
Swagger:     http://localhost:8089/SpringMVC/swagger-ui/
```

### Get Passwords
```bash
# Jenkins
docker exec achat-jenkins cat /var/jenkins_home/secrets/initialAdminPassword

# Nexus
docker exec achat-nexus cat /nexus-data/admin.password
```

### Essential Commands
```bash
# Start
docker-compose up -d

# Stop
docker-compose down

# Logs
docker-compose logs -f

# Status
docker-compose ps

# Build app
./mvnw clean package

# Run tests
./mvnw test
```

---

## ✨ Tips for Success

1. **Take it step by step** - Don't rush, understand each component
2. **Read the logs** - They tell you exactly what's wrong
3. **Test locally first** - Make sure app works before deploying
4. **Keep Jenkins simple** - Start with basic pipeline, add complexity later
5. **Use the guides** - They're comprehensive and tested
6. **Check credentials** - Most errors are due to wrong credentials
7. **Be patient** - First build takes time, subsequent builds are faster
8. **Backup regularly** - Before major changes, backup Jenkins/data
9. **Document changes** - Keep notes of what you modify
10. **Have fun!** - DevOps is powerful and fun when it works! 🚀

---

**🎉 Happy DevOps! Let's build something amazing!**

**Got questions? Check the troubleshooting section in JENKINS-SETUP-GUIDE.md**

---

**Last Updated:** November 2024  
**Project:** Achat DevOps Pipeline  
**Stack:** Jenkins, SonarQube, Nexus, Docker, Kubernetes, Terraform, AWS  
**Java Version:** 11 (LTS) ✅


# 🚀 Complete Jenkins & DevOps Pipeline Setup Guide

## 📋 Table of Contents
1. [Prerequisites](#prerequisites)
2. [Phase 1: Jenkins Initial Setup](#phase-1-jenkins-initial-setup)
3. [Phase 2: Configure Jenkins Plugins & Tools](#phase-2-configure-jenkins-plugins--tools)
4. [Phase 3: Setup Credentials](#phase-3-setup-credentials)
5. [Phase 4: Create Pipeline Job](#phase-4-create-pipeline-job)
6. [Phase 5: SonarQube Integration](#phase-5-sonarqube-integration)
7. [Phase 6: Nexus Integration](#phase-6-nexus-integration)
8. [Phase 7: Kubernetes Setup](#phase-7-kubernetes-setup)
9. [Phase 8: AWS Deployment](#phase-8-aws-deployment)
10. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Software Requirements
- ✅ Docker & Docker Compose installed
- ✅ Git installed
- ✅ AWS CLI installed (for AWS deployment)
- ✅ kubectl installed (for Kubernetes)
- ✅ Terraform installed

### Check Docker Installation
```bash
docker --version
docker-compose --version
```

---

## Phase 1: Jenkins Initial Setup

### Step 1.1: Start All Services

```bash
# Navigate to project directory
cd Achat_Devops

# Start all services (Jenkins, SonarQube, Nexus, Prometheus, Grafana)
docker-compose up -d

# Check all services are running
docker-compose ps
```

**Expected Output:**
```
NAME                  STATUS      PORTS
achat-jenkins         Up          0.0.0.0:8080->8080/tcp, 50000
achat-sonarqube       Up          0.0.0.0:9000->9000/tcp
achat-nexus           Up          0.0.0.0:8081->8081/tcp
achat-prometheus      Up          0.0.0.0:9090->9090/tcp
achat-grafana         Up          0.0.0.0:3000->3000/tcp
achat-mysql           Up          0.0.0.0:3306->3306/tcp
```

### Step 1.2: Access Jenkins

1. **Open Jenkins in browser:**
   ```
   http://localhost:8080
   ```

2. **Get the initial admin password:**
   ```bash
   docker exec achat-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```
   
   Copy this password (it will look like: `a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6`)

3. **Unlock Jenkins:**
   - Paste the password
   - Click "Continue"

4. **Install Plugins:**
   - Select **"Install suggested plugins"**
   - Wait for installation to complete (2-3 minutes)

5. **Create First Admin User:**
   ```
   Username: admin
   Password: admin123
   Full Name: Jenkins Admin
   Email: admin@achat.local
   ```

6. **Jenkins URL Configuration:**
   - Keep default: `http://localhost:8080/`
   - Click "Save and Finish"
   - Click "Start using Jenkins"

✅ **Jenkins is now ready!**

---

## Phase 2: Configure Jenkins Plugins & Tools

### Step 2.1: Install Required Plugins

1. **Navigate to Plugin Manager:**
   - Dashboard → Manage Jenkins → Plugins → Available plugins

2. **Install these plugins:**
   
   Search and select (use filter box):
   - ☑️ **Docker Pipeline** (for Docker build/push)
   - ☑️ **SonarQube Scanner** (for code analysis)
   - ☑️ **Nexus Artifact Uploader** (for artifact management)
   - ☑️ **Kubernetes** (for K8s deployment)
   - ☑️ **Kubernetes CLI** (for kubectl commands)
   - ☑️ **Pipeline: AWS Steps** (for AWS deployment)
   - ☑️ **Terraform** (for infrastructure)
   - ☑️ **Git** (should be installed already)
   - ☑️ **Pipeline** (should be installed already)
   - ☑️ **Credentials Binding** (should be installed already)

3. **Install without restart:**
   - Check all plugins above
   - Click "Install"
   - ✅ Wait until all are installed
   - Check "Restart Jenkins when no jobs are running"

4. **Wait for Jenkins to restart** (about 1 minute)
   - Refresh browser
   - Login again with your admin credentials

### Step 2.2: Configure JDK

1. **Go to Global Tool Configuration:**
   - Dashboard → Manage Jenkins → Tools

2. **Add JDK:**
   - Scroll to "JDK installations"
   - Click "Add JDK"
   - **Name:** `Java-11`
   - ☑️ Uncheck "Install automatically"
   - **JAVA_HOME:** `/opt/java/openjdk`
   - Click "Save"

   > **Note:** Jenkins container already has Java 11 installed

### Step 2.3: Configure Maven

1. **In the same Tools page, scroll to "Maven installations"**
   - Click "Add Maven"
   - **Name:** `Maven-3.8`
   - ☑️ Check "Install automatically"
   - **Version:** Select `3.8.6` or latest
   - Click "Save"

### Step 2.4: Configure Docker (Already Available)

Since we're running Jenkins in Docker with access to Docker socket, Docker is already available!

To verify:
```bash
# Execute in Jenkins container
docker exec achat-jenkins docker --version
```

✅ **Tools configured!**

---

## Phase 3: Setup Credentials

### Step 3.1: DockerHub Credentials

1. **Navigate to Credentials:**
   - Dashboard → Manage Jenkins → Credentials
   - Click "(global)" under "Stores scoped to Jenkins"
   - Click "Add Credentials"

2. **Add DockerHub credential:**
   ```
   Kind: Username with password
   Scope: Global
   Username: YOUR_DOCKERHUB_USERNAME
   Password: YOUR_DOCKERHUB_PASSWORD (or access token)
   ID: dockerhub
   Description: DockerHub Credentials
   ```
   - Click "Create"

   > **📌 Important:** Use your actual DockerHub username (looks like: `salimnahdi`)

### Step 3.2: SonarQube Token

**First, generate SonarQube token:**

1. **Access SonarQube:**
   ```
   http://localhost:9000
   ```
   - Default credentials: `admin` / `admin`
   - You'll be prompted to change password → set to `admin123`

2. **Generate Token:**
   - Click on "A" (admin avatar) → My Account → Security
   - Token Name: `jenkins-token`
   - Click "Generate"
   - **Copy the token** (example: `squ_1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r`)
   - ⚠️ **Save it now! You can't see it again!**

**Now add to Jenkins:**

1. **Back in Jenkins → Credentials → Add Credentials:**
   ```
   Kind: Secret text
   Scope: Global
   Secret: YOUR_SONARQUBE_TOKEN (paste the token you just copied)
   ID: sonar-token-jenkins
   Description: SonarQube Token
   ```
   - Click "Create"

### Step 3.3: Nexus Credentials

**First, get Nexus password:**

1. **Access Nexus:**
   ```
   http://localhost:8081
   ```

2. **Get initial admin password:**
   ```bash
   docker exec achat-nexus cat /nexus-data/admin.password
   ```
   Copy this password

3. **Sign In:**
   - Click "Sign In" (top right)
   - Username: `admin`
   - Password: (paste the password from step 2)
   - Follow wizard:
     - Set new password: `admin123`
     - Enable anonymous access: ✅ Yes
     - Click "Finish"

**Add to Jenkins:**

1. **Jenkins → Credentials → Add Credentials:**
   ```
   Kind: Username with password
   Scope: Global
   Username: admin
   Password: admin123
   ID: nexus-credentials
   Description: Nexus Repository Credentials
   ```
   - Click "Create"

### Step 3.4: GitHub Credentials (Optional but Recommended)

For private repositories:

1. **Add Credentials:**
   ```
   Kind: Username with password
   Scope: Global
   Username: YOUR_GITHUB_USERNAME
   Password: YOUR_GITHUB_PERSONAL_ACCESS_TOKEN
   ID: github-credentials
   Description: GitHub Credentials
   ```

   > **Generate GitHub token at:** https://github.com/settings/tokens
   > Permissions needed: repo (all)

### Step 3.5: AWS Credentials (For AWS Deployment)

1. **Add AWS Credentials:**
   ```
   Kind: AWS Credentials
   Scope: Global
   Access Key ID: YOUR_AWS_ACCESS_KEY
   Secret Access Key: YOUR_AWS_SECRET_KEY
   ID: aws-credentials
   Description: AWS Credentials for Deployment
   ```

   > Get these from AWS Console → IAM → Users → Security credentials

✅ **All credentials configured!**

---

## Phase 4: Create Pipeline Job

### Step 4.1: Create New Pipeline

1. **Create Pipeline:**
   - Dashboard → New Item
   - **Name:** `Achat-DevOps-Pipeline`
   - Select: **Pipeline**
   - Click "OK"

### Step 4.2: Configure Pipeline

1. **General Settings:**
   - ☑️ Check "GitHub project"
   - **Project url:** `https://github.com/NahdiSalim/Achat_Devops`

2. **Build Triggers:**
   - ☑️ Check "Poll SCM"
   - **Schedule:** `H/5 * * * *` (checks every 5 minutes)
   
   Or for webhook (better):
   - ☑️ Check "GitHub hook trigger for GITScm polling"

3. **Pipeline Configuration:**
   - **Definition:** Pipeline script from SCM
   - **SCM:** Git
   - **Repository URL:** `https://github.com/NahdiSalim/Achat_Devops.git`
   - **Credentials:** (select if private repo)
   - **Branch:** `*/main`
   - **Script Path:** `Jenkinsfile`

4. **Click "Save"**

✅ **Pipeline created!**

---

## Phase 5: SonarQube Integration

### Step 5.1: Configure SonarQube Server in Jenkins

1. **Add SonarQube Server:**
   - Dashboard → Manage Jenkins → System
   - Scroll to "SonarQube servers"
   - Click "Add SonarQube"
   
   Configure:
   ```
   Name: SonarQube-Server
   Server URL: http://achat-sonarqube:9000
   Server authentication token: sonar-token-jenkins (select from dropdown)
   ```
   - Click "Save"

### Step 5.2: Configure SonarQube Scanner

1. **Go to Tools:**
   - Dashboard → Manage Jenkins → Tools
   - Scroll to "SonarQube Scanner installations"
   - Click "Add SonarQube Scanner"
   
   Configure:
   ```
   Name: SonarScanner
   ☑️ Install automatically
   Version: Latest (usually 5.0.x)
   ```
   - Click "Save"

### Step 5.3: Create SonarQube Project

1. **In SonarQube UI (http://localhost:9000):**
   - Login (admin/admin123)
   - Click "Create Project" → "Manually"
   - **Project key:** `achat-project`
   - **Display name:** `Achat DevOps Project`
   - Click "Set Up"
   - Choose "Locally"
   - Use existing token: (the one we created)
   - Choose "Maven"

✅ **SonarQube ready!**

---

## Phase 6: Nexus Integration

### Step 6.1: Create Maven Repository in Nexus

1. **Login to Nexus (http://localhost:8081):**
   - Username: `admin`
   - Password: `admin123`

2. **Create Hosted Repository:**
   - Click ⚙️ (Settings) → Repository → Repositories
   - Click "Create repository"
   - Choose "maven2 (hosted)"
   
   Configure:
   ```
   Name: achat-releases
   Version policy: Release
   Layout policy: Strict
   Deployment policy: Allow redeploy
   ```
   - Click "Create repository"

3. **Create Snapshots Repository:**
   - Create another repository
   - Choose "maven2 (hosted)"
   
   Configure:
   ```
   Name: achat-snapshots
   Version policy: Snapshot
   Layout policy: Strict
   Deployment policy: Allow redeploy
   ```
   - Click "Create repository"

### Step 6.2: Configure Maven Settings

The updated Jenkinsfile will handle this automatically!

✅ **Nexus ready!**

---

## Phase 7: Kubernetes Setup

### Step 7.1: Install Kubernetes (Choose One)

**Option A: Minikube (Local Development)**
```bash
# Windows (using Chocolatey)
choco install minikube

# Or download from: https://minikube.sigs.k8s.io/docs/start/

# Start Minikube
minikube start --driver=docker
```

**Option B: AWS EKS (Production)**
- See Phase 8 for AWS setup

**Option C: Use Docker Desktop Kubernetes**
- Docker Desktop → Settings → Kubernetes → Enable Kubernetes

### Step 7.2: Verify Kubernetes

```bash
kubectl cluster-info
kubectl get nodes
```

### Step 7.3: Create Kubernetes Secret for DockerHub

```bash
kubectl create secret docker-registry regcred \
  --docker-server=https://index.docker.io/v1/ \
  --docker-username=YOUR_DOCKERHUB_USERNAME \
  --docker-password=YOUR_DOCKERHUB_PASSWORD \
  --docker-email=YOUR_EMAIL
```

✅ **Kubernetes ready!**

---

## Phase 8: AWS Deployment

### Step 8.1: AWS Prerequisites

1. **Install AWS CLI:**
   ```bash
   # Windows
   msiexec.exe /i https://awscli.amazonaws.com/AWSCLIV2.msi

   # Verify
   aws --version
   ```

2. **Configure AWS CLI:**
   ```bash
   aws configure
   ```
   Enter:
   - AWS Access Key ID
   - AWS Secret Access Key
   - Default region: `us-east-1`
   - Default output format: `json`

### Step 8.2: Setup Terraform Backend

1. **Create S3 Bucket for Terraform State:**
   ```bash
   aws s3api create-bucket \
     --bucket achat-terraform-state \
     --region us-east-1
   
   aws s3api put-bucket-versioning \
     --bucket achat-terraform-state \
     --versioning-configuration Status=Enabled
   ```

2. **Create DynamoDB Table for State Locking:**
   ```bash
   aws dynamodb create-table \
     --table-name achat-terraform-locks \
     --attribute-definitions AttributeName=LockID,AttributeType=S \
     --key-schema AttributeName=LockID,KeyType=HASH \
     --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
     --region us-east-1
   ```

### Step 8.3: Setup EKS Cluster

```bash
# Install eksctl
# Windows (using Chocolatey)
choco install eksctl

# Create EKS cluster
eksctl create cluster \
  --name achat-cluster \
  --region us-east-1 \
  --nodegroup-name achat-nodes \
  --node-type t3.medium \
  --nodes 2 \
  --nodes-min 1 \
  --nodes-max 3 \
  --managed
```

This will take 15-20 minutes.

### Step 8.4: Configure kubectl for EKS

```bash
aws eks update-kubeconfig --region us-east-1 --name achat-cluster
kubectl get nodes
```

✅ **AWS ready!**

---

## Testing Your Setup

### Test 1: Build Manually

```bash
cd Achat_Devops
./mvnw clean test
```

✅ All tests should pass!

### Test 2: Run Pipeline

1. **Trigger Build:**
   - Go to your pipeline: `Achat-DevOps-Pipeline`
   - Click "Build Now"

2. **Watch Progress:**
   - Click on the build number (e.g., #1)
   - Click "Console Output"
   - Watch the stages execute

### Test 3: Verify SonarQube

After build completes:
- Go to http://localhost:9000
- Check "Projects" → You should see your project with analysis

### Test 4: Verify Nexus

- Go to http://localhost:8081
- Browse → achat-releases
- Should see your artifact

### Test 5: Verify Docker Image

```bash
docker images | grep salimnahdi/docker-spring-boot
```

---

## 🎯 Next Steps After Jenkins is Working

1. **Test the pipeline** - Make a small code change, commit, push
2. **Check SonarQube analysis** - Review code quality metrics
3. **Verify Docker image** - Check DockerHub for your image
4. **Setup monitoring** - Configure Prometheus + Grafana dashboards
5. **Deploy to Kubernetes** - Test K8s deployment locally
6. **Deploy to AWS** - Use Terraform to provision infrastructure

---

## Troubleshooting

### Issue: Jenkins can't access SonarQube

**Solution:**
```bash
# Check network
docker network inspect achat-network

# Restart services
docker-compose restart jenkins sonarqube
```

### Issue: Docker permission denied in Jenkins

**Solution:**
```bash
docker exec -u root achat-jenkins chmod 666 /var/run/docker.sock
```

### Issue: Maven not found

**Solution:** Make sure Maven is configured in Jenkins Tools (Phase 2.3)

### Issue: Cannot push to DockerHub

**Solution:** Verify DockerHub credentials in Jenkins (Phase 3.1)

### Issue: SonarQube analysis fails

**Solution:**
1. Check SonarQube is running: `docker-compose ps`
2. Verify token is correct
3. Check SonarQube server URL in Jenkins

---

## 📞 Support Checklist

Before asking for help, verify:
- ✅ All Docker containers are running
- ✅ All Jenkins plugins are installed
- ✅ All credentials are configured
- ✅ SonarQube token is generated
- ✅ Nexus is accessible
- ✅ Jenkins can access Git repository

---

## 🎉 Success Indicators

You'll know everything is working when:
1. ✅ Jenkins pipeline completes all stages in green
2. ✅ SonarQube shows code analysis results
3. ✅ Nexus contains your JAR artifact
4. ✅ DockerHub has your Docker image
5. ✅ Kubernetes deployment is successful
6. ✅ Application is accessible via LoadBalancer

---

**Ready to start? Begin with Phase 1! 🚀**


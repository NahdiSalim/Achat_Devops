# DevOps Deployment Guide - Achat Application

This guide provides step-by-step instructions for setting up the complete DevOps pipeline for the Achat application.

## 📑 Table of Contents

1. [GitHub Setup](#1-github-setup)
2. [Jenkins Configuration](#2-jenkins-configuration)
3. [SonarQube Setup](#3-sonarqube-setup)
4. [Nexus Repository](#4-nexus-repository)
5. [Docker Configuration](#5-docker-configuration)
6. [Terraform Infrastructure](#6-terraform-infrastructure)
7. [Prometheus Monitoring](#7-prometheus-monitoring)
8. [Grafana Visualization](#8-grafana-visualization)

---

## 1. GitHub Setup

### Create Repository

```bash
# Initialize Git repository
git init

# Add all files
git add .

# Commit changes
git commit -m "Initial commit: Achat DevOps project"

# Add remote repository
git remote add origin https://github.com/yourusername/Achat_Devops.git

# Push to GitHub
git push -u origin main
```

### Create `.gitignore`

```gitignore
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml

# IDE
.idea/
*.iml
.vscode/
.settings/
.project
.classpath

# OS
.DS_Store
Thumbs.db

# Logs
*.log

# Database
*.db
*.h2.db

# Application
application-local.properties
```

### Branch Protection

1. Go to repository **Settings** → **Branches**
2. Add branch protection rule for `main`:
   - ✅ Require pull request reviews
   - ✅ Require status checks to pass
   - ✅ Include administrators

---

## 2. Jenkins Configuration

### Installation

#### Using Docker

```bash
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts-jdk11
```

#### Initial Setup

1. Access Jenkins at `http://localhost:8080`
2. Get initial admin password:
```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```
3. Install suggested plugins
4. Create admin user

### Required Plugins

Install these plugins via **Manage Jenkins** → **Manage Plugins**:

- **Git Plugin** - Source code management
- **Maven Integration Plugin** - Build tool
- **Pipeline Plugin** - Pipeline support
- **SonarQube Scanner** - Code quality
- **Nexus Artifact Uploader** - Artifact repository
- **Docker Pipeline** - Docker integration
- **JaCoCo Plugin** - Code coverage
- **Email Extension Plugin** - Notifications

### Configure Tools

**Manage Jenkins** → **Global Tool Configuration**

#### Maven Configuration
- Name: `Maven-3.8`
- Install automatically: ✅
- Version: `3.8.6`

#### JDK Configuration
- Name: `JDK-18`
- JAVA_HOME: `/usr/lib/jvm/java-18-openjdk`

#### SonarQube Scanner
- Name: `SonarScanner`
- Install automatically: ✅
- Version: Latest

### Create Jenkins Pipeline

1. **New Item** → **Pipeline**
2. Name: `Achat-DevOps-Pipeline`
3. **Pipeline** section:
   - Definition: **Pipeline script from SCM**
   - SCM: **Git**
   - Repository URL: `https://github.com/yourusername/Achat_Devops.git`
   - Branch: `*/main`
   - Script Path: `Jenkinsfile`

### Configure Credentials

**Manage Jenkins** → **Manage Credentials** → **Global**

Add these credentials:

1. **GitHub Credentials**
   - Kind: Username with password
   - ID: `github-credentials`
   - Username: Your GitHub username
   - Password: Personal Access Token

2. **SonarQube Token**
   - Kind: Secret text
   - ID: `sonarqube-token`
   - Secret: Your SonarQube token

3. **Nexus Credentials**
   - Kind: Username with password
   - ID: `nexus-credentials`
   - Username: `admin`
   - Password: Your Nexus password

4. **Docker Hub Credentials**
   - Kind: Username with password
   - ID: `dockerhub-credentials`
   - Username: Your Docker Hub username
   - Password: Your Docker Hub password

---

## 3. SonarQube Setup

### Installation with Docker

```bash
docker run -d \
  --name sonarqube \
  -p 9000:9000 \
  -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true \
  -v sonarqube_data:/opt/sonarqube/data \
  -v sonarqube_extensions:/opt/sonarqube/extensions \
  -v sonarqube_logs:/opt/sonarqube/logs \
  sonarqube:latest
```

### Initial Configuration

1. Access SonarQube at `http://localhost:9000`
2. Default login: `admin/admin`
3. Change password when prompted

### Create Project

1. Click **Create Project**
2. Project key: `achat-devops`
3. Display name: `Achat DevOps`
4. Generate token and save it

### Quality Gates

1. Go to **Quality Gates**
2. Create custom gate or use default
3. Set thresholds:
   - **Coverage**: > 80%
   - **Duplicated Lines**: < 3%
   - **Maintainability Rating**: A
   - **Reliability Rating**: A
   - **Security Rating**: A

### Configure in Jenkins

**Manage Jenkins** → **Configure System** → **SonarQube servers**

- Name: `SonarQube`
- Server URL: `http://sonarqube:9000`
- Authentication token: Select `sonarqube-token`

### Add to Maven `pom.xml`

```xml
<properties>
    <sonar.projectKey>achat-devops</sonar.projectKey>
    <sonar.host.url>http://localhost:9000</sonar.host.url>
    <sonar.login>your-sonarqube-token</sonar.login>
</properties>
```

---

## 4. Nexus Repository

### Installation with Docker

```bash
docker run -d \
  --name nexus \
  -p 8081:8081 \
  -p 8082:8082 \
  -v nexus-data:/nexus-data \
  sonatype/nexus3
```

### Initial Setup

1. Access Nexus at `http://localhost:8081`
2. Get initial admin password:
```bash
docker exec nexus cat /nexus-data/admin.password
```
3. Sign in and change password
4. Configure anonymous access

### Create Repositories

#### Maven Releases Repository
- Type: **maven2 (hosted)**
- Name: `maven-releases`
- Version policy: **Release**
- Layout policy: **Strict**

#### Maven Snapshots Repository
- Type: **maven2 (hosted)**
- Name: `maven-snapshots`
- Version policy: **Snapshot**
- Layout policy: **Strict**

#### Docker Registry
- Type: **docker (hosted)**
- Name: `docker-hosted`
- HTTP Port: `8082`
- Enable Docker V1 API: ✅

### Configure Maven `pom.xml`

```xml
<distributionManagement>
    <repository>
        <id>nexus-releases</id>
        <url>http://localhost:8081/repository/maven-releases/</url>
    </repository>
    <snapshotRepository>
        <id>nexus-snapshots</id>
        <url>http://localhost:8081/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>
```

### Configure Maven `settings.xml`

Create `~/.m2/settings.xml`:

```xml
<settings>
    <servers>
        <server>
            <id>nexus-releases</id>
            <username>admin</username>
            <password>your-password</password>
        </server>
        <server>
            <id>nexus-snapshots</id>
            <username>admin</username>
            <password>your-password</password>
        </server>
    </servers>
</settings>
```

---

## 5. Docker Configuration

### Dockerfile

The `Dockerfile` in the project root builds the application image:

```dockerfile
FROM openjdk:18-jdk-alpine
WORKDIR /app
COPY target/achat-1.0.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java","-jar","app.jar"]
```

### Build Image

```bash
# Build the application first
./mvnw clean package

# Build Docker image
docker build -t achat-app:latest .

# Tag for registry
docker tag achat-app:latest yourusername/achat-app:latest

# Push to Docker Hub
docker push yourusername/achat-app:latest
```

### Docker Compose

The `docker-compose.yml` sets up the entire stack:

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Remove volumes
docker-compose down -v
```

### Services Ports

| Service | Port | URL |
|---------|------|-----|
| Application | 8089 | http://localhost:8089/SpringMVC |
| MySQL | 3306 | localhost:3306 |
| Jenkins | 8080 | http://localhost:8080 |
| SonarQube | 9000 | http://localhost:9000 |
| Nexus | 8081 | http://localhost:8081 |
| Prometheus | 9090 | http://localhost:9090 |
| Grafana | 3000 | http://localhost:3000 |

---

## 6. Terraform Infrastructure

### Prerequisites

```bash
# Install Terraform
# Windows (using Chocolatey)
choco install terraform

# Linux
wget https://releases.hashicorp.com/terraform/1.6.0/terraform_1.6.0_linux_amd64.zip
unzip terraform_1.6.0_linux_amd64.zip
sudo mv terraform /usr/local/bin/
```

### Terraform Files

The project includes Terraform configurations in the `terraform/` directory.

### Initialize Terraform

```bash
cd terraform
terraform init
```

### Plan Infrastructure

```bash
terraform plan
```

### Apply Configuration

```bash
terraform apply
```

### Destroy Infrastructure

```bash
terraform destroy
```

### Terraform Modules

- **Network Module**: VPC, Subnets, Security Groups
- **Compute Module**: EC2 instances for application
- **Database Module**: RDS MySQL instance
- **Load Balancer**: Application Load Balancer
- **Monitoring**: CloudWatch configuration

---

## 7. Prometheus Monitoring

### Configuration

The `prometheus.yml` configuration is included in the project:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'achat-application'
    metrics_path: '/SpringMVC/actuator/prometheus'
    static_configs:
      - targets: ['app:8089']
```

### Add Actuator to Application

Already configured in `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### Configure `application.properties`

```properties
# Actuator endpoints
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.metrics.export.prometheus.enabled=true
```

### Access Prometheus

1. Open `http://localhost:9090`
2. Query metrics:
   - `jvm_memory_used_bytes`
   - `http_server_requests_seconds_count`
   - `hikaricp_connections_active`

---

## 8. Grafana Visualization

### Initial Setup

1. Access Grafana at `http://localhost:3000`
2. Default login: `admin/admin`
3. Change password

### Add Prometheus Data Source

1. **Configuration** → **Data Sources** → **Add data source**
2. Select **Prometheus**
3. URL: `http://prometheus:9090`
4. Click **Save & Test**

### Import Dashboards

#### Spring Boot Dashboard

1. **Create** → **Import**
2. Dashboard ID: `11378` (Spring Boot 2.1 Statistics)
3. Select Prometheus data source
4. Click **Import**

#### JVM Dashboard

1. **Create** → **Import**
2. Dashboard ID: `4701` (JVM Micrometer)
3. Select Prometheus data source
4. Click **Import**

### Create Custom Dashboard

1. **Create** → **Dashboard** → **Add new panel**
2. Add metrics:
   - HTTP Request Rate: `rate(http_server_requests_seconds_count[5m])`
   - HTTP Request Duration: `http_server_requests_seconds_sum / http_server_requests_seconds_count`
   - Active DB Connections: `hikaricp_connections_active`
   - JVM Memory: `jvm_memory_used_bytes`

### Alert Configuration

1. **Alerting** → **Alert rules**
2. Create alerts for:
   - High CPU usage (>80%)
   - Memory usage (>90%)
   - High error rate (>5%)
   - Database connection pool exhaustion

---

## 📊 Complete Pipeline Flow

```
Developer Commits Code
         ↓
    GitHub Webhook
         ↓
    Jenkins Pipeline Triggered
         ↓
    ┌─────────────────────┐
    │ 1. Checkout Code    │
    └─────────────────────┘
         ↓
    ┌─────────────────────┐
    │ 2. Maven Build      │
    └─────────────────────┘
         ↓
    ┌─────────────────────┐
    │ 3. Run Tests        │
    │    - Unit Tests     │
    │    - Integration    │
    │    - Coverage       │
    └─────────────────────┘
         ↓
    ┌─────────────────────┐
    │ 4. SonarQube        │
    │    - Code Quality   │
    │    - Security Scan  │
    └─────────────────────┘
         ↓
    ┌─────────────────────┐
    │ 5. Build Artifact   │
    │    - Create JAR     │
    └─────────────────────┘
         ↓
    ┌─────────────────────┐
    │ 6. Nexus Upload     │
    │    - Store Artifact │
    └─────────────────────┘
         ↓
    ┌─────────────────────┐
    │ 7. Docker Build     │
    │    - Build Image    │
    └─────────────────────┘
         ↓
    ┌─────────────────────┐
    │ 8. Docker Push      │
    │    - Push Registry  │
    └─────────────────────┘
         ↓
    ┌─────────────────────┐
    │ 9. Terraform Deploy │
    │    - Infrastructure │
    └─────────────────────┘
         ↓
    ┌─────────────────────┐
    │ 10. Monitoring      │
    │    - Prometheus     │
    │    - Grafana        │
    └─────────────────────┘
```

---

## 🔍 Troubleshooting

### Jenkins Build Fails

```bash
# Check Jenkins logs
docker logs jenkins

# Check Maven build locally
./mvnw clean install
```

### SonarQube Connection Issues

```bash
# Verify SonarQube is running
docker ps | grep sonarqube

# Check SonarQube logs
docker logs sonarqube
```

### Docker Build Fails

```bash
# Clean Docker cache
docker system prune -a

# Rebuild without cache
docker build --no-cache -t achat-app:latest .
```

### Database Connection Issues

```bash
# Check MySQL is running
docker ps | grep mysql

# Check database logs
docker logs mysql

# Test connection
mysql -h localhost -u root -p achatdb
```

---

## 📚 Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Jenkins Pipeline Documentation](https://www.jenkins.io/doc/book/pipeline/)
- [SonarQube Documentation](https://docs.sonarqube.org/latest/)
- [Nexus Repository Documentation](https://help.sonatype.com/repomanager3)
- [Docker Documentation](https://docs.docker.com/)
- [Terraform Documentation](https://www.terraform.io/docs)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)

---

## ✅ Checklist

- [ ] GitHub repository created
- [ ] Jenkins installed and configured
- [ ] SonarQube server running
- [ ] Nexus repository configured
- [ ] Docker images built
- [ ] Docker Compose tested
- [ ] Terraform infrastructure defined
- [ ] Prometheus collecting metrics
- [ ] Grafana dashboards configured
- [ ] Pipeline successfully executed
- [ ] Application deployed and accessible
- [ ] Monitoring alerts configured

---

Made with ❤️ for DevOps Excellence


# 📊 Prometheus & Grafana Monitoring Setup Guide

Complete guide to set up and use Prometheus and Grafana for monitoring your Achat application.

---

## 📋 Table of Contents
1. [Local Setup](#-local-setup)
2. [Testing Monitoring](#-testing-monitoring)
3. [Understanding Dashboards](#-understanding-dashboards)
4. [AWS Deployment](#-aws-deployment)
5. [Troubleshooting](#-troubleshooting)

---

## 🏠 Local Setup

### Step 1: Start All Services

```bash
# Start the entire stack (MySQL, App, Jenkins, SonarQube, Nexus, Prometheus, Grafana)
docker-compose up -d

# Check if all containers are running
docker-compose ps
```

You should see these containers running:
- `achat-app` (Port 8089)
- `achat-mysql` (Port 3306)
- `achat-prometheus` (Port 9090)
- `achat-grafana` (Port 3000)
- `achat-jenkins` (Port 8080)
- `achat-sonarqube` (Port 9000)
- `achat-nexus` (Port 8081)

### Step 2: Wait for Application to Start

```bash
# Watch application logs
docker logs -f achat-app

# Wait for this message:
# "Started AchatApplication in X seconds"
```

---

## ✅ Testing Monitoring

### Step 1: Verify Prometheus Metrics Endpoint

Open your browser and go to:

**Application Metrics:**
```
http://localhost:8089/SpringMVC/actuator/prometheus
```

You should see Prometheus metrics like:
```
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap",id="PS Eden Space",} 1.234567E7
...
```

**All Actuator Endpoints:**
```
http://localhost:8089/SpringMVC/actuator
```

**Health Check:**
```
http://localhost:8089/SpringMVC/actuator/health
```

### Step 2: Access Prometheus

1. **Open Prometheus:**
   ```
   http://localhost:9090
   ```

2. **Check Targets:**
   - Click **"Status" → "Targets"**
   - Look for `achat-application` target
   - Should show **"UP"** in green

3. **Run a Query:**
   - Go to **"Graph"** tab
   - Enter query: `up{job="achat-application"}`
   - Click **"Execute"**
   - Should return value `1` (meaning up)

4. **Try More Queries:**
   ```promql
   # CPU Usage
   system_cpu_usage{application="achat"} * 100

   # Memory Usage
   jvm_memory_used_bytes{application="achat"}

   # HTTP Request Rate
   rate(http_server_requests_seconds_count{application="achat"}[5m])

   # Database Connections
   hikaricp_connections_active{application="achat"}
   ```

### Step 3: Access Grafana

1. **Open Grafana:**
   ```
   http://localhost:3000
   ```

2. **Login:**
   - Username: `admin`
   - Password: `admin`
   - (You can skip changing the password or set a new one)

3. **Verify Datasource:**
   - Go to **"Configuration" (⚙️) → "Data sources"**
   - You should see **"Prometheus"** already configured
   - Click on it and scroll down
   - Click **"Save & Test"** - should show green checkmark ✅

4. **Open the Dashboard:**
   - Go to **"Dashboards" (☰) → "Browse"**
   - Click on **"Achat Application Monitoring"**

5. **What You'll See:**
   - **Application Status** - Shows if app is UP (1) or DOWN (0)
   - **HTTP Request Rate** - Number of requests per second
   - **CPU Usage** - System CPU utilization
   - **JVM Memory Usage** - Heap and non-heap memory
   - **Database Connection Pool** - Active and idle DB connections
   - **HTTP Response Time** - Average response time for endpoints

---

## 📊 Understanding Dashboards

### Application Status Panel
- **1** = Application is UP ✅
- **0** = Application is DOWN ❌

### HTTP Request Rate
- Shows requests/second for each endpoint
- Helps identify:
  - High traffic periods
  - Most frequently used endpoints
  - Traffic patterns

### CPU Usage
- Shows system CPU utilization %
- **Normal:** 0-50%
- **High:** 50-80%
- **Critical:** 80-100%

### JVM Memory Usage
- **Heap:** Application object memory
- **Non-Heap:** Class metadata, compiled code
- Watch for memory leaks (constantly increasing)

### Database Connection Pool
- **Active:** Connections in use
- **Idle:** Available connections
- If active = max pool size → need more connections

### HTTP Response Time
- Average response time per endpoint
- **Good:** < 100ms
- **Acceptable:** 100-500ms
- **Slow:** > 500ms

---

## 🚀 AWS Deployment

### Prerequisites
- AWS Account (with valid credentials)
- Terraform installed in Jenkins ✅ (already done!)
- AWS credentials configured in Jenkins ✅ (already done!)

### Step 1: Review Terraform Configuration

Your current Terraform setup (`terraform/main.tf`) creates:
- ✅ VPC with public/private subnets
- ✅ Internet Gateway
- ✅ Security Groups
- ✅ Route Tables

**To add EC2 instances and monitoring, we need to extend it.**

### Step 2: Deploy Basic Infrastructure

1. **In Jenkins, run the pipeline:**
   ```
   Build Now
   ```

2. **Check the Terraform stage output** - it should show:
   - AWS credentials verified ✅
   - Terraform initialized ✅
   - Configuration validated ✅
   - Plan created ✅

3. **To actually create the infrastructure**, connect to Jenkins container:
   ```bash
   docker exec -it achat-jenkins bash
   cd /var/jenkins_home/workspace/Achat_pipeline/terraform
   
   # Export AWS credentials (get fresh ones from AWS!)
   export AWS_ACCESS_KEY_ID="your-access-key"
   export AWS_SECRET_ACCESS_KEY="your-secret-key"
   export AWS_SESSION_TOKEN="your-session-token"
   export AWS_DEFAULT_REGION="us-east-1"
   
   # Apply the infrastructure
   terraform apply -auto-approve
   ```

4. **After successful apply**, you'll see:
   - VPC ID
   - Subnet IDs
   - Security Group ID

### Step 3: Deploy Application to AWS

**Option A: Deploy to EC2 Instances**

You'll need to:
1. Create EC2 instances in the VPC
2. Install Docker on EC2
3. Pull and run your Docker image
4. Install Prometheus and Grafana on separate EC2 instances

**Option B: Use AWS ECS/EKS (Recommended for Production)**

1. **ECS (Elastic Container Service):**
   - Simpler, managed container orchestration
   - Good for Docker containers
   - Less operational overhead

2. **EKS (Elastic Kubernetes Service):**
   - Full Kubernetes cluster
   - More complex but more powerful
   - Better for microservices

### Step 4: Monitoring in AWS

**CloudWatch (AWS Native):**
- Automatically available
- Good for basic metrics
- Integrates with AWS services

**Prometheus + Grafana on AWS:**
1. **Deploy Prometheus on EC2:**
   - Create EC2 instance
   - Install Prometheus
   - Configure to scrape your app instances

2. **Deploy Grafana on EC2:**
   - Create EC2 instance
   - Install Grafana
   - Configure Prometheus datasource

3. **Use AWS Managed Services:**
   - **Amazon Managed Service for Prometheus (AMP)**
   - **Amazon Managed Grafana (AMG)**
   - No server management
   - Pay-as-you-go pricing

---

## 🔧 Troubleshooting

### Prometheus Not Scraping Application

**Problem:** Prometheus shows target as "DOWN"

**Solutions:**
1. Check if app is running:
   ```bash
   docker logs achat-app
   curl http://localhost:8089/SpringMVC/actuator/health
   ```

2. Check if metrics endpoint works:
   ```bash
   curl http://localhost:8089/SpringMVC/actuator/prometheus
   ```

3. Check Prometheus logs:
   ```bash
   docker logs achat-prometheus
   ```

4. Verify prometheus.yml configuration:
   ```bash
   cat prometheus.yml
   ```

### Grafana Dashboard Shows "No Data"

**Solutions:**
1. **Check datasource:**
   - Go to Grafana → Configuration → Data sources
   - Click "Test" on Prometheus datasource

2. **Check time range:**
   - Dashboard might be looking at wrong time period
   - Click time picker (top right)
   - Select "Last 15 minutes"

3. **Check if metrics exist in Prometheus:**
   - Go to Prometheus (http://localhost:9090)
   - Run query: `up{job="achat-application"}`
   - If returns nothing → app not sending metrics

4. **Generate some traffic:**
   ```bash
   # Make some requests to generate metrics
   curl http://localhost:8089/SpringMVC/actuator/health
   curl http://localhost:8089/SpringMVC/
   ```

### Application Metrics Not Showing

**Solutions:**
1. **Verify Actuator dependency in pom.xml:**
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

2. **Check application.properties:**
   ```properties
   management.endpoints.web.exposure.include=*
   management.metrics.export.prometheus.enabled=true
   ```

3. **Rebuild application:**
   ```bash
   mvn clean package
   docker-compose down
   docker-compose build app
   docker-compose up -d
   ```

### Grafana Dashboard Not Loading

**Solutions:**
1. **Check provisioning logs:**
   ```bash
   docker logs achat-grafana
   ```

2. **Manually import dashboard:**
   - Go to Grafana → Dashboards → Import
   - Upload `grafana/provisioning/dashboards/achat-dashboard.json`

3. **Restart Grafana:**
   ```bash
   docker-compose restart grafana
   ```

---

## 📚 Useful Commands

```bash
# View all container logs
docker-compose logs -f

# View specific service logs
docker logs -f achat-app
docker logs -f achat-prometheus
docker logs -f achat-grafana

# Restart monitoring stack
docker-compose restart prometheus grafana

# Rebuild everything
docker-compose down
docker-compose build
docker-compose up -d

# Check container health
docker-compose ps
docker inspect achat-app | grep -i health

# Access Prometheus CLI
docker exec -it achat-prometheus sh

# Access Grafana CLI
docker exec -it achat-grafana sh

# Export Grafana dashboard
# Go to dashboard → Settings → JSON Model → Copy
```

---

## 🎯 Next Steps

1. **✅ Test locally** - Verify everything works
2. **📊 Customize dashboards** - Add more panels, adjust queries
3. **⚠️ Set up alerts** - Configure Prometheus alerts
4. **☁️ Deploy to AWS** - Move to production
5. **📈 Monitor production** - Watch your metrics

---

## 🌟 Quick Start Summary

```bash
# 1. Start everything
docker-compose up -d

# 2. Wait for app to start
docker logs -f achat-app

# 3. Open services
# - Application: http://localhost:8089/SpringMVC
# - Prometheus: http://localhost:9090
# - Grafana: http://localhost:3000 (admin/admin)
# - Jenkins: http://localhost:8080

# 4. View dashboard in Grafana
# Navigate to: Dashboards → Achat Application Monitoring

# 5. Generate traffic
curl http://localhost:8089/SpringMVC/actuator/health

# 6. Watch metrics update in Grafana!
```

---

**🎉 You're all set! Your monitoring stack is ready!**

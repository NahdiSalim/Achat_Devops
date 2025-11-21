# 🚀 Jenkins CI/CD Pipeline Setup Guide

## Complete Guide to Deploy Your Application to AWS EKS

This guide will help you set up the Jenkins pipeline that will:
1. Build and test your application
2. Analyze code with SonarQube
3. Publish artifacts to Nexus
4. Build and push Docker image to DockerHub
5. **Deploy to AWS EKS with LoadBalancer**
6. **Access Swagger UI from the cloud!**

---

## 📋 Prerequisites Checklist

Before running the pipeline, ensure you have:

- ✅ Jenkins running (from docker-compose)
- ✅ DockerHub account (username: `salimnahdi`)
- ✅ AWS Account with access credentials
- ✅ GitHub repository access
- ✅ SonarQube and Nexus running (from docker-compose)

---

## 🔐 Step 1: Configure Jenkins Credentials

Access Jenkins at `http://localhost:8080` and add these credentials:

### 1.1 DockerHub Credentials
1. Go to: **Manage Jenkins** → **Manage Credentials** → **Global** → **Add Credentials**
2. **Kind**: Username with password
3. **Username**: `salimnahdi`
4. **Password**: Your DockerHub password
5. **ID**: `dockerhub`
6. **Description**: DockerHub Credentials
7. Click **Create**

### 1.2 SonarQube Token
1. Go to SonarQube: http://localhost:9000
2. Login: `admin` / `admin` (change password if prompted)
3. Click on **My Account** → **Security** → **Generate Token**
4. Token name: `jenkins`
5. **Copy the token** (you won't see it again!)
6. In Jenkins: **Manage Jenkins** → **Credentials** → **Add Credentials**
   - **Kind**: Secret text
   - **Secret**: Paste your SonarQube token
   - **ID**: `sonar-token-jenkins`
   - **Description**: SonarQube Token
   - Click **Create**

### 1.3 Nexus Credentials
1. Go to Nexus: http://localhost:8081
2. First-time login:
   ```bash
   docker exec -it achat-nexus cat /nexus-data/admin.password
   ```
3. Login with `admin` and the password from above
4. Change password (e.g., `admin123`)
5. Create repositories:
   - Go to **Settings** (gear icon) → **Repositories** → **Create repository**
   - Create **maven2 (hosted)** named `achat-releases`
   - Create **maven2 (hosted)** named `achat-snapshots`
6. In Jenkins: **Manage Jenkins** → **Credentials** → **Add Credentials**
   - **Kind**: Username with password
   - **Username**: `admin`
   - **Password**: Your Nexus password
   - **ID**: `nexus-credentials`
   - **Description**: Nexus Credentials
   - Click **Create**

### 1.4 AWS Credentials

**IMPORTANT**: This is for AWS Academy/Learner Lab users:

1. Go to your AWS Academy Lab
2. Click **AWS Details** → **AWS CLI**
3. Click **Show** to see credentials
4. You'll see something like:
   ```
   [default]
   aws_access_key_id=ASIA...
   aws_secret_access_key=...
   aws_session_token=...
   ```

5. Copy these credentials into Jenkins container:
   ```bash
   # Access Jenkins container
   docker exec -it achat-jenkins bash
   
   # Create AWS directory
   mkdir -p /var/jenkins_home/.aws
   
   # Create credentials file
   cat > /var/jenkins_home/.aws/credentials <<EOF
   [default]
   aws_access_key_id=YOUR_ACCESS_KEY_HERE
   aws_secret_access_key=YOUR_SECRET_KEY_HERE
   aws_session_token=YOUR_SESSION_TOKEN_HERE
   EOF
   
   # Create config file
   cat > /var/jenkins_home/.aws/config <<EOF
   [default]
   region=us-east-1
   output=json
   EOF
   
   # Set permissions
   chown -R jenkins:jenkins /var/jenkins_home/.aws
   chmod 600 /var/jenkins_home/.aws/credentials
   chmod 644 /var/jenkins_home/.aws/config
   
   # Test AWS credentials
   export AWS_SHARED_CREDENTIALS_FILE=/var/jenkins_home/.aws/credentials
   export AWS_CONFIG_FILE=/var/jenkins_home/.aws/config
   aws sts get-caller-identity
   
   # Exit container
   exit
   ```

**⚠️ IMPORTANT**: AWS Academy credentials expire after a few hours. You'll need to update them before each pipeline run!

---

## 📦 Step 2: Configure SonarQube

1. Go to http://localhost:9000
2. Login with `admin` / `admin`
3. Go to **Administration** → **Configuration** → **Webhooks**
4. Click **Create**
   - **Name**: Jenkins
   - **URL**: `http://achat-jenkins:8080/sonarqube-webhook/`
   - Click **Create**

5. Go to **Quality Gates** → **Create**
   - **Name**: Achat Quality Gate
   - Set conditions as needed (or use default)

---

## 🔧 Step 3: Configure Jenkins Tools

1. Go to: **Manage Jenkins** → **Global Tool Configuration**

### 3.1 Configure JDK
- Scroll to **JDK**
- Click **Add JDK**
  - **Name**: `JDK-11`
  - Uncheck "Install automatically"
  - **JAVA_HOME**: `/usr/local/openjdk-11`

### 3.2 Configure Maven
- Scroll to **Maven**
- Click **Add Maven**
  - **Name**: `Maven-3.8.6`
  - Check "Install automatically"
  - **Version**: 3.8.6

### 3.3 Configure SonarQube Scanner
- Go to: **Manage Jenkins** → **Configure System**
- Scroll to **SonarQube servers**
- Click **Add SonarQube**
  - **Name**: `SonarQube`
  - **Server URL**: `http://achat-sonarqube:9000`
  - **Server authentication token**: Select `sonar-token-jenkins` from dropdown
  - Click **Save**

---

## 🏗️ Step 4: Create Jenkins Pipeline Job

### 4.1 Create New Pipeline
1. Go to Jenkins dashboard: http://localhost:8080
2. Click **New Item**
3. **Name**: `Achat-DevOps-Pipeline`
4. Select **Pipeline**
5. Click **OK**

### 4.2 Configure Pipeline
1. **General** section:
   - ✅ Check **Discard old builds**
   - **Max # of builds to keep**: `10`

2. **Build Triggers** (optional):
   - ✅ Check **GitHub hook trigger for GITScm polling** (if you want automatic builds)
   - OR ✅ Check **Poll SCM** with schedule: `H/15 * * * *` (every 15 minutes)

3. **Pipeline** section:
   - **Definition**: Pipeline script from SCM
   - **SCM**: Git
   - **Repository URL**: `https://github.com/NahdiSalim/Achat_Devops.git`
   - **Branch Specifier**: `*/main`
   - **Script Path**: `Jenkinsfile`

4. Click **Save**

---

## 🚀 Step 5: Run the Pipeline

### Before Running:
1. **Update AWS Credentials** (if expired):
   ```bash
   docker exec -it achat-jenkins bash
   
   # Update credentials file with new values from AWS Academy
   nano /var/jenkins_home/.aws/credentials
   # Paste new credentials, save and exit (Ctrl+X, Y, Enter)
   
   exit
   ```

2. **Ensure Docker socket is accessible**:
   ```bash
   docker exec -it achat-jenkins bash
   ls -la /var/run/docker.sock
   docker ps
   exit
   ```

### Run the Pipeline:
1. Go to your pipeline: http://localhost:8080/job/Achat-DevOps-Pipeline/
2. Click **Build Now**
3. Watch the pipeline progress in **Console Output**

---

## 📊 Step 6: Monitor Pipeline Execution

The pipeline has 13 stages:

1. **🔍 Checkout Git** - Downloads your code
2. **🔧 Preparation** - Sets up build environment
3. **🧹 MVN Clean** - Cleans and compiles
4. **📦 Artifact Construction** - Builds JAR file
5. **🧪 Unit Tests** - Runs tests with coverage
6. **📊 SonarQube Analysis** - Code quality analysis
7. **📤 Publish to Nexus** - Uploads artifacts
8. **🐳 Build Docker Image** - Creates Docker image
9. **📤 Deploy Docker Image** - Pushes to DockerHub
10. **🧹 Cleanup Docker Images** - Cleans local images
11. **🔐 Test AWS Credentials** - Verifies AWS access
12. **🏗️ Terraform Infrastructure** - Provisions EKS cluster
13. **☸️ Deploy to AWS EKS** - Deploys application

### Expected Timeline:
- **First run**: 15-25 minutes (Terraform creates infrastructure)
- **Subsequent runs**: 8-12 minutes (infrastructure exists)

---

## 🌐 Step 7: Access Your Deployed Application

After successful deployment, the pipeline will output URLs like:

```
════════════════════════════════════════════════════════
🎉  APPLICATION DEPLOYED SUCCESSFULLY!
════════════════════════════════════════════════════════

🌐 APPLICATION URLs:

   📍 Main API:
      http://abc123-1234567890.us-east-1.elb.amazonaws.com/SpringMVC/

   📚 Swagger UI (Test your endpoints here!):
      http://abc123-1234567890.us-east-1.elb.amazonaws.com/SpringMVC/swagger-ui.html

   ❤️  Health Check:
      http://abc123-1234567890.us-east-1.elb.amazonaws.com/SpringMVC/actuator/health

════════════════════════════════════════════════════════
```

### Testing Your Application:

1. **Test Health Check** first:
   ```bash
   curl http://YOUR-LOADBALANCER-URL/SpringMVC/actuator/health
   ```

2. **Access Swagger UI**:
   - Open in browser: `http://YOUR-LOADBALANCER-URL/SpringMVC/swagger-ui.html`
   - You should see all your API endpoints!

3. **Test Endpoints**:
   - In Swagger UI, expand any controller
   - Click "Try it out" on any endpoint
   - Click "Execute"
   - See the response!

---

## 🐛 Troubleshooting

### Issue 1: AWS Credentials Expired
**Symptom**: Stage 11 fails with "ExpiredToken" error

**Solution**:
```bash
# Update credentials from AWS Academy
docker exec -it achat-jenkins bash
nano /var/jenkins_home/.aws/credentials
# Paste new credentials, save and exit
exit
```

### Issue 2: Docker Permission Denied
**Symptom**: Stage 8 fails with "permission denied" on Docker socket

**Solution**:
```bash
# Give Jenkins access to Docker socket
docker exec -u root achat-jenkins chmod 666 /var/run/docker.sock
```

### Issue 3: SonarQube Quality Gate Timeout
**Symptom**: Stage 6 hangs waiting for Quality Gate

**Solution**: This is normal for first run. Wait or skip quality gate check temporarily.

### Issue 4: Nexus Upload Fails
**Symptom**: Stage 7 fails to upload artifact

**Solution**:
- Verify Nexus is running: http://localhost:8081
- Check repositories exist: `achat-releases` and `achat-snapshots`
- Verify credentials in Jenkins

### Issue 5: LoadBalancer URL Not Available
**Symptom**: Final stage shows "LoadBalancer URL not available"

**Solution**:
```bash
# Get the URL manually
kubectl get svc achat-app-service -n achat-app

# Wait a few more minutes, AWS LoadBalancer takes 2-5 minutes to provision
```

### Issue 6: Application Pod Not Starting
**Symptom**: Pods are in CrashLoopBackOff

**Solution**:
```bash
# Check pod logs
kubectl logs -n achat-app -l app=achat-app

# Common issue: MySQL not ready yet
# Wait 2-3 minutes for MySQL pod to be fully ready
kubectl get pods -n achat-app -w
```

---

## 📝 Useful Commands

### Check Pipeline from Terminal:
```bash
# Get LoadBalancer URL
kubectl get svc achat-app-service -n achat-app

# Check pods status
kubectl get pods -n achat-app

# Check application logs
kubectl logs -n achat-app -l app=achat-app

# Check MySQL logs
kubectl logs -n achat-app -l app=mysql

# Describe service (see events)
kubectl describe svc achat-app-service -n achat-app

# Test health endpoint
curl http://YOUR-LOADBALANCER-URL/SpringMVC/actuator/health

# Get all resources
kubectl get all -n achat-app
```

### Restart Pipeline Without Terraform:
If infrastructure is already created, you can temporarily comment out Stage 12 (Terraform) in Jenkinsfile for faster builds.

---

## 🎯 Success Indicators

Your pipeline is successful when you see:

1. ✅ All 13 stages show green checkmarks
2. ✅ Docker image pushed to DockerHub
3. ✅ SonarQube analysis completed
4. ✅ Artifacts in Nexus repository
5. ✅ LoadBalancer URL displayed in console output
6. ✅ **Swagger UI accessible from the cloud!**
7. ✅ Health endpoint returns `{"status":"UP"}`

---

## 🔄 Updating Your Application

To deploy changes:

1. Push code to GitHub repository
2. In Jenkins, click **Build Now**
3. Pipeline will automatically:
   - Build new version
   - Create new Docker image with build number
   - Deploy to EKS
   - **Zero downtime** with rolling update (2 replicas)

---

## 💰 Cost Management (AWS Academy)

**IMPORTANT**: AWS Academy has limited credits!

### To Save Credits:

1. **Stop when done testing**:
   ```bash
   # Delete Kubernetes resources (keep EKS cluster)
   kubectl delete namespace achat-app
   ```

2. **Destroy everything**:
   ```bash
   cd terraform
   terraform destroy -auto-approve
   ```

3. **Stop AWS Lab** in AWS Academy when not in use

### Estimated Costs:
- **EKS Control Plane**: ~$0.10/hour
- **EC2 Worker Nodes**: ~$0.12/hour (t3.medium)
- **LoadBalancer**: ~$0.025/hour
- **Total**: ~$0.25/hour (~$6/day if left running)

---

## 🎉 Next Steps

Once your application is deployed:

1. **Test all endpoints** via Swagger UI
2. **Share the LoadBalancer URL** with your team
3. **Monitor with Prometheus** (add Prometheus scraping)
4. **Set up CI/CD** for automatic deployments
5. **Add custom domain** with Route53 (optional)
6. **Enable HTTPS** with ACM certificate (optional)

---

## 📚 Additional Resources

- **Jenkins Documentation**: https://www.jenkins.io/doc/
- **Kubernetes Documentation**: https://kubernetes.io/docs/
- **AWS EKS Documentation**: https://docs.aws.amazon.com/eks/
- **Terraform AWS Provider**: https://registry.terraform.io/providers/hashicorp/aws

---

## ✅ Checklist Summary

Before running pipeline:

- [ ] Jenkins credentials configured (DockerHub, SonarQube, Nexus)
- [ ] AWS credentials updated in Jenkins container
- [ ] SonarQube webhook configured
- [ ] Nexus repositories created
- [ ] Jenkins tools configured (JDK, Maven, SonarQube)
- [ ] Pipeline job created in Jenkins
- [ ] GitHub repository accessible

Run pipeline:

- [ ] Click "Build Now"
- [ ] Monitor console output
- [ ] Wait for all 13 stages to complete
- [ ] Copy LoadBalancer URL from output
- [ ] Access Swagger UI from cloud
- [ ] Test endpoints

---

**🎉 You're all set! Your DevOps pipeline will deploy your application to AWS EKS with full CI/CD automation!**

**Need help?** Check the troubleshooting section or review the console output for detailed error messages.


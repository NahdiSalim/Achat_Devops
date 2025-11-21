# 🚀 Quick Start - Jenkins Pipeline to AWS EKS

## 5-Minute Setup Guide

### Prerequisites
- Jenkins running at http://localhost:8080
- DockerHub account: `salimnahdi`
- AWS Academy account with credentials

---

## Step 1: Add Credentials to Jenkins (5 minutes)

### 1.1 DockerHub
```
Manage Jenkins → Credentials → Add Credentials
- Kind: Username with password
- ID: dockerhub
- Username: salimnahdi
- Password: YOUR_DOCKERHUB_PASSWORD
```

### 1.2 SonarQube Token
```
1. Go to http://localhost:9000 (login: admin/admin)
2. My Account → Security → Generate Token
3. Copy token
4. In Jenkins: Add Credentials
   - Kind: Secret text
   - ID: sonar-token-jenkins
   - Secret: PASTE_TOKEN
```

### 1.3 Nexus
```
# Get Nexus password
docker exec -it achat-nexus cat /nexus-data/admin.password

# Login to http://localhost:8081 with admin/PASSWORD
# Change password to: admin123

# In Jenkins: Add Credentials
- Kind: Username with password
- ID: nexus-credentials
- Username: admin
- Password: admin123
```

### 1.4 AWS Credentials
```bash
# Copy AWS credentials from AWS Academy
docker exec -it achat-jenkins bash

mkdir -p /var/jenkins_home/.aws

cat > /var/jenkins_home/.aws/credentials <<EOF
[default]
aws_access_key_id=YOUR_KEY
aws_secret_access_key=YOUR_SECRET
aws_session_token=YOUR_TOKEN
EOF

cat > /var/jenkins_home/.aws/config <<EOF
[default]
region=us-east-1
output=json
EOF

chown -R jenkins:jenkins /var/jenkins_home/.aws
chmod 600 /var/jenkins_home/.aws/credentials

# Test
export AWS_SHARED_CREDENTIALS_FILE=/var/jenkins_home/.aws/credentials
aws sts get-caller-identity

exit
```

---

## Step 2: Create Nexus Repositories (2 minutes)

```
1. Go to http://localhost:8081
2. Login: admin/admin123
3. Settings (gear icon) → Repositories → Create repository
4. Create maven2 (hosted):
   - Name: achat-releases
   - Version policy: Release
5. Create maven2 (hosted):
   - Name: achat-snapshots
   - Version policy: Snapshot
```

---

## Step 3: Configure SonarQube Webhook (1 minute)

```
1. Go to http://localhost:9000
2. Administration → Configuration → Webhooks
3. Create:
   - Name: Jenkins
   - URL: http://achat-jenkins:8080/sonarqube-webhook/
```

---

## Step 4: Create Jenkins Pipeline (2 minutes)

```
1. Jenkins Dashboard → New Item
2. Name: Achat-DevOps-Pipeline
3. Type: Pipeline
4. Pipeline section:
   - Definition: Pipeline script from SCM
   - SCM: Git
   - Repository: https://github.com/NahdiSalim/Achat_Devops.git
   - Branch: */main
   - Script Path: Jenkinsfile
5. Save
```

---

## Step 5: Run Pipeline 🚀

```
1. Go to pipeline: http://localhost:8080/job/Achat-DevOps-Pipeline/
2. Click "Build Now"
3. Click on build #1
4. Click "Console Output"
5. Watch the magic happen! ✨
```

**First run takes**: ~20 minutes (builds infrastructure)

**Subsequent runs**: ~8-12 minutes

---

## Step 6: Access Your Application 🌐

After successful deployment, look for output like:

```
════════════════════════════════════════════════════════
🎉  APPLICATION DEPLOYED SUCCESSFULLY!
════════════════════════════════════════════════════════

📚 Swagger UI:
   http://YOUR-URL.elb.amazonaws.com/SpringMVC/swagger-ui.html
```

**Copy the URL and open in browser!**

---

## Quick Commands

### Check Deployment Status:
```bash
kubectl get pods -n achat-app
kubectl get svc achat-app-service -n achat-app
```

### Get Application URL:
```bash
kubectl get svc achat-app-service -n achat-app -o jsonpath='{.status.loadBalancer.ingress[0].hostname}'
```

### Check Application Logs:
```bash
kubectl logs -n achat-app -l app=achat-app --tail=100
```

### Test Health:
```bash
curl http://YOUR-URL/SpringMVC/actuator/health
```

---

## Common Issues & Quick Fixes

### ❌ AWS Credentials Expired
```bash
# Update from AWS Academy
docker exec -it achat-jenkins bash
nano /var/jenkins_home/.aws/credentials
# Paste new credentials, Ctrl+X, Y, Enter
exit
```

### ❌ Docker Permission Denied
```bash
docker exec -u root achat-jenkins chmod 666 /var/run/docker.sock
```

### ❌ Can't Access LoadBalancer URL
```
Wait 3-5 minutes after deployment for AWS LoadBalancer to provision
Then refresh the URL
```

---

## Pipeline Stages

1. ✅ Checkout Git
2. ✅ Preparation
3. ✅ Clean & Compile
4. ✅ Build JAR
5. ✅ Run Tests
6. ✅ SonarQube Analysis
7. ✅ Publish to Nexus
8. ✅ Build Docker Image
9. ✅ Push to DockerHub
10. ✅ Cleanup Images
11. ✅ Test AWS Credentials
12. ✅ Terraform Infrastructure
13. ✅ **Deploy to AWS EKS** 🎉

---

## Success Checklist

After pipeline completes:

- [ ] All 13 stages show ✅ green
- [ ] Docker image on DockerHub: https://hub.docker.com/r/salimnahdi/docker-spring-boot
- [ ] SonarQube analysis: http://localhost:9000
- [ ] Nexus artifacts: http://localhost:8081
- [ ] LoadBalancer URL in console output
- [ ] **Swagger UI accessible from cloud!**
- [ ] Health endpoint returns: `{"status":"UP"}`

---

## URLs Reference

| Service | URL | Login |
|---------|-----|-------|
| **Jenkins** | http://localhost:8080 | admin/admin |
| **SonarQube** | http://localhost:9000 | admin/admin |
| **Nexus** | http://localhost:8081 | admin/admin123 |
| **Grafana** | http://localhost:3000 | admin/admin |
| **Prometheus** | http://localhost:9090 | - |
| **Your App** | http://localhost:8089/SpringMVC/ | - |
| **Swagger (Local)** | http://localhost:8089/SpringMVC/swagger-ui.html | - |
| **Swagger (AWS)** | http://YOUR-ELB-URL/SpringMVC/swagger-ui.html | - |

---

## Cost Saving 💰

**IMPORTANT**: AWS Academy has limited credits!

### After Testing:
```bash
# Delete just the app (keep cluster)
kubectl delete namespace achat-app

# OR destroy everything
cd terraform
terraform destroy -auto-approve
```

**Always stop AWS Academy lab when done!**

---

## 🎉 That's It!

You now have:
- ✅ Fully automated CI/CD pipeline
- ✅ Application deployed on AWS EKS
- ✅ Swagger UI accessible from anywhere
- ✅ Load balanced with 2 replicas
- ✅ MySQL database in Kubernetes
- ✅ Professional DevOps setup

**Share your Swagger UI URL with anyone to demo your API!**

---

## Need More Help?

📖 See full guide: `JENKINS-PIPELINE-SETUP.md`

🐛 Check troubleshooting section for detailed solutions

📊 Monitor your resources in AWS console

---

**Built with ❤️ for DevOps excellence!**


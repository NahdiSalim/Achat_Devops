# ⚡ Quick AWS Deployment - TL;DR

**Goal:** Deploy Achat app with Swagger to AWS EC2 in 10 minutes!

---

## 🚀 Quick Start (Copy-Paste Commands)

### 1. Push Code
```bash
git add terraform/ AWS-DEPLOYMENT-GUIDE.md QUICK-AWS-DEPLOY.md
git commit -m "Add AWS EC2 deployment with Swagger UI"
git push origin main
```

### 2. Get Fresh AWS Credentials

Go to **AWS Academy** → **AWS Details** → Copy credentials

### 3. Update Jenkins

- Jenkins → Manage Jenkins → Credentials
- Update all 3 AWS credentials with new values

### 4. Deploy from Jenkins Container

```bash
# Connect to Jenkins
docker exec -it achat-jenkins bash

# Go to workspace
cd /var/jenkins_home/workspace/Achat_pipeline/terraform

# Set AWS credentials (REPLACE WITH YOUR VALUES!)
export AWS_ACCESS_KEY_ID="ASIA..."
export AWS_SECRET_ACCESS_KEY="..."
export AWS_SESSION_TOKEN="IQoJ..."
export AWS_DEFAULT_REGION="us-east-1"

# Test connection
aws sts get-caller-identity

# Initialize (first time only)
terraform init

# Deploy!
terraform apply -auto-approve
```

### 5. Get Your URLs

After deployment completes (~5 minutes), you'll see:

```
📖 Swagger UI:    http://YOUR_IP/SpringMVC/swagger-ui/
🏠 Landing Page:  http://YOUR_IP/
🏥 Health:        http://YOUR_IP/SpringMVC/actuator/health
📊 Prometheus:    http://YOUR_IP:9090
```

### 6. Wait 2-3 Minutes

Application needs time to start. Then open Swagger UI!

---

## 🎯 Access Swagger

```
http://YOUR_IP_ADDRESS/SpringMVC/swagger-ui/
```

**Try it out:**
- Click any endpoint
- Click "Try it out"
- Click "Execute"
- See response! ✅

---

## 🔍 Check Status

```bash
# SSH into EC2 (if you have key)
ssh -i achat-key.pem ec2-user@YOUR_IP

# Check containers
docker ps

# Check app logs
docker logs -f achat-app

# Wait for: "Started AchatApplication"
```

---

## 🧹 Cleanup (When Done)

```bash
# In Jenkins container / terraform directory
terraform destroy -auto-approve
```

**This deletes everything and stops all costs!**

---

## ⚠️ Important Notes

- ✅ AWS credentials expire every few hours
- ✅ Get fresh ones when they expire
- ✅ Wait 2-3 min after deployment for app to start
- ✅ t2.medium costs ~$35/month (destroy when done!)
- ✅ Make sure your DockerHub image is pushed

---

## 📝 Costs Summary

**While running:** ~$0.05/hour  
**After stopping:** ~$0.10/month (storage only)  
**After destroying:** $0

---

## 🆘 Troubleshooting

**Can't access Swagger?**
```bash
# Check if app is ready
docker logs achat-app | grep "Started"
```

**Credentials expired?**
```bash
# Get new credentials from AWS Academy
# Export them again
export AWS_ACCESS_KEY_ID="new-key"
export AWS_SECRET_ACCESS_KEY="new-secret"
export AWS_SESSION_TOKEN="new-token"
```

**Want to restart app?**
```bash
ssh -i achat-key.pem ec2-user@YOUR_IP
docker restart achat-app
```

---

**That's it! Your Swagger UI is now live on AWS! 🚀**


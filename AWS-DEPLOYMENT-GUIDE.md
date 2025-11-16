# 🚀 AWS Deployment Guide - Achat Application with Swagger

Complete step-by-step guide to deploy your Achat Spring Boot application with Swagger UI to AWS EC2.

---

## 📋 What Will Be Deployed

✅ **EC2 Instance** running Docker  
✅ **MySQL Database** (containerized)  
✅ **Achat Spring Boot Application** (your Docker image)  
✅ **Swagger UI** for API testing  
✅ **Prometheus** for monitoring  
✅ **Nginx** as reverse proxy  
✅ **Elastic IP** for stable public address  

---

## 🎯 Architecture

```
Internet
   ↓
[Elastic IP]
   ↓
[EC2 Instance]
   ├── Nginx (Port 80) → Landing Page + Proxy
   ├── Spring Boot App (Port 8089) → Swagger UI
   ├── MySQL (Port 3306)
   └── Prometheus (Port 9090)
```

---

## ⚙️ Prerequisites

### 1. Fresh AWS Credentials

Your AWS session tokens expire every few hours. Get new ones:

1. Go to **AWS Academy** or **AWS Console**
2. Click **"AWS Details"** or **"Command line or programmatic access"**
3. Copy the credentials:
   ```
   aws_access_key_id=ASIA...
   aws_secret_access_key=...
   aws_session_token=IQoJ...
   ```

### 2. Update Jenkins Credentials

Go to Jenkins and update all 3 credentials with the new values (same as before).

### 3. Push Your Docker Image to DockerHub

Make sure your latest application image is on DockerHub:

```bash
# Build the image
docker build -t salimnadi/achat:latest .

# Login to DockerHub
docker login

# Push the image
docker push salimnadi/achat:latest
```

### 4. (Optional) Create SSH Key in AWS

If you want SSH access to your EC2 instance:

1. Go to **AWS Console** → **EC2** → **Key Pairs**
2. Click **"Create key pair"**
3. Name: `achat-key`
4. Type: **RSA**
5. Format: **.pem**
6. Click **"Create"**
7. **Save the .pem file securely!**

---

## 🚀 Deployment Steps

### Step 1: Commit and Push Terraform Configuration

```bash
# Add all Terraform files
git add terraform/

# Commit
git commit -m "Add EC2 deployment configuration for AWS with Swagger"

# Push to GitHub
git push origin main
```

### Step 2: Initialize Terraform (First Time Only)

1. **Connect to Jenkins container:**
   ```bash
   docker exec -it achat-jenkins bash
   ```

2. **Navigate to workspace:**
   ```bash
   cd /var/jenkins_home/workspace/Achat_pipeline/terraform
   ```

3. **Set AWS credentials:**
   ```bash
   export AWS_ACCESS_KEY_ID="your-new-access-key"
   export AWS_SECRET_ACCESS_KEY="your-new-secret-key"
   export AWS_SESSION_TOKEN="your-new-session-token"
   export AWS_DEFAULT_REGION="us-east-1"
   ```

4. **Test AWS connection:**
   ```bash
   aws sts get-caller-identity
   ```
   Should show your account info ✅

5. **Initialize Terraform:**
   ```bash
   terraform init
   ```

### Step 3: Plan the Infrastructure

```bash
# Create Terraform plan
terraform plan -out=tfplan

# Review the plan - it will show:
# - VPC
# - Subnets (2 public)
# - Internet Gateway
# - Route Tables
# - Security Group (ports 22, 80, 8089, 9090)
# - EC2 Instance (t2.medium)
# - Elastic IP
```

### Step 4: Deploy to AWS

```bash
# Apply the configuration
terraform apply -auto-approve
```

This will take **3-5 minutes** and create all infrastructure.

**You'll see output like:**

```
Apply complete! Resources: 12 added, 0 changed, 0 destroyed.

Outputs:

access_instructions = <<EOT

========================================
🚀 ACHAT APPLICATION DEPLOYED TO AWS! 🚀
========================================

📖 Swagger UI:    http://54.123.45.67/SpringMVC/swagger-ui/
🏠 Landing Page:  http://54.123.45.67/
🏥 Health Check:  http://54.123.45.67/SpringMVC/actuator/health
📊 Prometheus:    http://54.123.45.67:9090

📝 Instance ID:   i-0abcd1234efgh5678
🌐 Public IP:     54.123.45.67

⚠️  Note: Wait 2-3 minutes for application to fully start!

========================================
EOT

ec2_public_ip = "54.123.45.67"
swagger_url = "http://54.123.45.67/SpringMVC/swagger-ui/"
```

**✅ Save this IP address!**

### Step 5: Wait for Application Startup

The EC2 instance needs time to:
1. Install Docker (1 min)
2. Pull MySQL image (1 min)
3. Start MySQL (30 sec)
4. Pull your application image (1-2 min)
5. Start application (30 sec)

**Total: 2-3 minutes**

---

## 🎉 Accessing Your Application

### 1. Landing Page

Open in browser:
```
http://YOUR_IP_ADDRESS/
```

You'll see a beautiful landing page with links to all services!

### 2. Swagger UI (Main Goal!)

Open in browser:
```
http://YOUR_IP_ADDRESS/SpringMVC/swagger-ui/
```

**You'll see:**
- 📖 All your API endpoints
- ✅ Interactive testing interface
- 📝 Request/Response examples
- 🔧 Try it out buttons

**Example Endpoints:**
- `GET /SpringMVC/fournisseur/retrieve-all-fournisseurs`
- `POST /SpringMVC/fournisseur/add-fournisseur`
- `PUT /SpringMVC/fournisseur/update-fournisseur`
- `DELETE /SpringMVC/fournisseur/remove-fournisseur/{fournisseur-id}`

### 3. Test an Endpoint

1. **Click on any endpoint** (e.g., `GET /fournisseur/retrieve-all-fournisseurs`)
2. **Click "Try it out"**
3. **Click "Execute"**
4. **See the response!** ✅

### 4. Health Check

```
http://YOUR_IP_ADDRESS/SpringMVC/actuator/health
```

Should return:
```json
{
  "status": "UP"
}
```

### 5. Prometheus Monitoring

```
http://YOUR_IP_ADDRESS:9090
```

Check your application metrics!

---

## 🔍 Monitoring Deployment

### Check EC2 Instance in AWS Console

1. Go to **AWS Console** → **EC2** → **Instances**
2. Find instance named: `achat-app-instance-dev`
3. **Status:** Should be **"Running"** ✅
4. **Public IP:** Note the IP address

### SSH into EC2 (if you created a key)

```bash
# Make key file private
chmod 400 achat-key.pem

# Connect to EC2
ssh -i achat-key.pem ec2-user@YOUR_IP_ADDRESS
```

### Check Docker Containers

```bash
# List running containers
docker ps

# Should see:
# - achat-mysql
# - achat-app
# - prometheus
```

### Check Application Logs

```bash
# View application logs
docker logs achat-app

# Follow logs in real-time
docker logs -f achat-app

# Look for:
# "Started AchatApplication in X seconds"
```

### Check MySQL

```bash
# Check MySQL logs
docker logs achat-mysql

# Connect to MySQL
docker exec -it achat-mysql mysql -u achat_user -p
# Password: achat_password

# Check database
USE achatdb;
SHOW TABLES;
```

---

## 🔧 Troubleshooting

### Problem: Can't Access Swagger UI

**Solution 1:** Wait longer (application may still be starting)
```bash
ssh -i achat-key.pem ec2-user@YOUR_IP
docker logs -f achat-app
# Wait for "Started AchatApplication"
```

**Solution 2:** Check if containers are running
```bash
ssh -i achat-key.pem ec2-user@YOUR_IP
docker ps -a
# All containers should be "Up"
```

**Solution 3:** Restart application container
```bash
ssh -i achat-key.pem ec2-user@YOUR_IP
docker restart achat-app
```

### Problem: 502 Bad Gateway

**Cause:** Application not ready yet  
**Solution:** Wait 1-2 more minutes, then refresh

### Problem: Connection Timeout

**Cause:** Security group not applied  
**Solution:** Check security group in AWS Console
1. Go to **EC2** → **Security Groups**
2. Find `achat-app-sg-dev`
3. Check **Inbound Rules** - should have ports: 22, 80, 8089, 9090

### Problem: Application Won't Start

**Check logs:**
```bash
ssh -i achat-key.pem ec2-user@YOUR_IP
docker logs achat-app
```

**Common issues:**
- MySQL not ready → Wait longer
- Image pull failed → Check DockerHub image exists
- Memory issues → Upgrade to t2.large

### Problem: AWS Credentials Expired

**Symptoms:**
```
Error: error configuring Terraform AWS Provider: failed to get shared config profile
```

**Solution:**
1. Get fresh AWS credentials (see Prerequisites)
2. Export them again:
   ```bash
   export AWS_ACCESS_KEY_ID="new-key"
   export AWS_SECRET_ACCESS_KEY="new-secret"
   export AWS_SESSION_TOKEN="new-token"
   ```
3. Retry terraform command

---

## 💰 Cost Estimate

**Free Tier Eligible:**
- EC2 t2.medium: **NOT free tier** (~$0.05/hour = ~$35/month)
- VPC, Subnets, IGW: **FREE**
- Elastic IP (while associated): **FREE**
- Data transfer: First 100 GB/month **FREE**

**To minimize costs:**
- Use **t2.micro** instead (free tier eligible)
- Stop instance when not in use:
  ```bash
  # In AWS Console or using AWS CLI
  aws ec2 stop-instances --instance-ids i-YOUR-INSTANCE-ID
  ```
- Destroy infrastructure when done testing:
  ```bash
  terraform destroy -auto-approve
  ```

---

## 🧹 Cleanup

### Option 1: Stop Instance (Keeps Infrastructure)

```bash
# In Jenkins container / terraform directory
aws ec2 stop-instances --instance-ids $(terraform output -raw ec2_instance_id)
```

**Costs:** Only EBS storage (~$0.10/month for 8 GB)

### Option 2: Destroy Everything

```bash
# In Jenkins container / terraform directory
terraform destroy -auto-approve
```

**This will delete:**
- ✅ EC2 Instance
- ✅ Elastic IP
- ✅ Security Group
- ✅ Subnets
- ✅ VPC
- ✅ Everything!

**Costs:** $0

---

## 📊 Testing Your API with Swagger

### Example: Get All Fournisseurs

1. **Open Swagger UI**
2. **Find:** `GET /fournisseur/retrieve-all-fournisseurs`
3. **Click:** "Try it out"
4. **Click:** "Execute"
5. **See:** Response with list of fournisseurs (may be empty initially)

### Example: Add a Fournisseur

1. **Find:** `POST /fournisseur/add-fournisseur`
2. **Click:** "Try it out"
3. **Edit the JSON:**
   ```json
   {
     "code": "F001",
     "libelle": "Test Supplier",
     "categorieFournisseur": "ORDINAIRE"
   }
   ```
4. **Click:** "Execute"
5. **See:** Response with created fournisseur

### Example: Get Specific Fournisseur

1. **Find:** `GET /fournisseur/retrieve-fournisseur/{fournisseur-id}`
2. **Click:** "Try it out"
3. **Enter ID:** (from previous response)
4. **Click:** "Execute"
5. **See:** Response with fournisseur details

---

## 🎯 Next Steps

1. ✅ **Test all endpoints** in Swagger
2. 📊 **Monitor metrics** in Prometheus
3. 🔐 **Secure your API** (add authentication if needed)
4. 🌐 **Add custom domain** (optional - use Route 53)
5. 🔒 **Add HTTPS** (optional - use Let's Encrypt)
6. 📈 **Deploy Grafana** on another EC2 for dashboards

---

## 🆘 Need Help?

**Check logs:**
```bash
# EC2 instance logs
ssh -i achat-key.pem ec2-user@YOUR_IP
sudo tail -f /var/log/achat-deployment.log
sudo tail -f /var/log/cloud-init-output.log

# Docker logs
docker logs achat-app
docker logs achat-mysql
```

**Useful commands:**
```bash
# Restart services
docker restart achat-app
sudo systemctl restart nginx

# Check Docker status
docker ps -a
docker stats

# Check disk space
df -h

# Check memory
free -h
```

---

## ✅ Success Checklist

- [ ] Fresh AWS credentials obtained
- [ ] DockerHub image pushed
- [ ] Terraform applied successfully
- [ ] EC2 instance running
- [ ] Swagger UI accessible
- [ ] Health endpoint returns "UP"
- [ ] API endpoints testable in Swagger
- [ ] Prometheus accessible
- [ ] No errors in logs

---

**🎉 Congratulations! Your application is live on AWS with Swagger for API testing!**

**Share your Swagger URL:** `http://YOUR_IP/SpringMVC/swagger-ui/` 🚀


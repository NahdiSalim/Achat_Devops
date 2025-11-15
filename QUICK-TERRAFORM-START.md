# ⚡ Quick Start: Terraform in Jenkins

## 🎯 What You'll Get

After following this guide:
- ✅ Jenkins with Terraform installed
- ✅ kubectl for Kubernetes management  
- ✅ AWS CLI for cloud operations
- ✅ Scripts to manage infrastructure
- ✅ Option to create AWS EKS (Kubernetes cluster)

---

## 🚀 Step-by-Step Instructions

### Step 1: Rebuild Jenkins (5-10 minutes)

```bash
# Windows
rebuild-jenkins.bat

# This will install Terraform, kubectl, AWS CLI in Jenkins
```

**What happens:**
- ⏹️ Stops current Jenkins
- 🗑️ Removes old container  
- 🔨 Builds new image with Terraform + kubectl + AWS CLI
- ▶️ Starts Jenkins
- ✅ Verifies all tools installed

### Step 2: Verify Installation

After rebuild completes, you should see:

```
Checking Terraform:
Terraform v1.6.6

Checking kubectl:
Client Version: v1.28.x

Checking AWS CLI:
aws-cli/2.x.x

Checking Docker:
Docker version 24.x.x
```

---

## 🔑 Step 3: Configure AWS Credentials

### Option A: Create Free AWS Account

1. Go to https://aws.amazon.com/free/
2. Click **Create a Free Account**
3. Follow signup process (needs credit card for verification)
4. Go to IAM → Users → Create User
5. Attach policy: **AdministratorAccess** (for learning)
6. Create access key
7. Save **Access Key ID** and **Secret Access Key**

### Option B: Add Credentials to Jenkins

1. Open Jenkins: http://localhost:8080
2. **Manage Jenkins** → **Manage Credentials**
3. Click **(global)** → **Add Credentials**
4. Select **AWS Credentials**
5. **ID**: `aws-credentials`
6. Paste your Access Key ID and Secret Access Key
7. Click **OK**

---

## 📋 Step 4: Choose Your Infrastructure

### Option 1: Simple EC2 Instance (FREE TIER)

**Best for beginners**  
**Cost:** $0/month (Free Tier)

Use: `terraform/main.tf`

Creates:
- 1 EC2 t2.micro instance (Free Tier)
- Security group
- SSH access

### Option 2: AWS EKS Kubernetes Cluster (PAID)

**For production-like setup**  
**Cost:** ~$193/month

Use: `terraform-eks.tf` (copy to `terraform/` folder)

Creates:
- Full EKS Kubernetes cluster
- VPC with 2 subnets
- 2 t3.medium worker nodes
- All necessary IAM roles
- Load balancer

⚠️ **Start with Option 1 if learning!**

---

## 🎯 Step 5: Initialize Terraform

```bash
# Option A: Using helper script
run-terraform.bat init

# Option B: Manually
docker exec -u root achat-jenkins /bin/bash -c "cd /var/jenkins_home/workspace/terraform && terraform init"
```

You should see:
```
Terraform has been successfully initialized!
```

---

## 📊 Step 6: Create Terraform Plan

```bash
# See what will be created (without creating it)
run-terraform.bat plan
```

This shows:
- What resources will be created
- Estimated costs
- Configuration validation

**Review this carefully!**

---

## 🚀 Step 7: Apply Infrastructure

⚠️ **WARNING**: This creates real AWS resources

```bash
# Create the infrastructure
run-terraform.bat apply
```

**For FREE TIER (EC2):** Safe to run  
**For EKS:** Will cost ~$193/month

---

## 🧪 Step 8: Test Your Infrastructure

### If you created EC2:

```bash
# Get instance details
docker exec -u root achat-jenkins /bin/bash -c "cd /var/jenkins_home/workspace/terraform && terraform output"

# SSH to instance (if you added SSH key)
ssh -i your-key.pem ec2-user@<PUBLIC_IP>
```

### If you created EKS:

```bash
# Configure kubectl
aws eks update-kubeconfig --region us-east-1 --name achat-eks-cluster

# Check cluster
kubectl get nodes

# Deploy your app
kubectl apply -f k8s/deployment.yaml -n achat-app
```

---

## 🧹 Step 9: Cleanup (Destroy Infrastructure)

⚠️ **IMPORTANT**: Always destroy when done testing!

```bash
# Destroy all AWS resources
run-terraform.bat destroy
```

This prevents ongoing charges!

---

## 💰 Cost Management

### FREE Options:
- ✅ EC2 t2.micro (750 hours/month Free Tier)
- ✅ Terraform planning (no cost)
- ✅ Local Kubernetes (Docker Desktop - FREE)

### PAID Options:
- ⚠️ EKS Cluster: ~$73/month (control plane)
- ⚠️ t3.medium nodes: ~$60/month each
- ⚠️ Load balancers: ~$20/month

### 💡 Recommendation:
1. Start with local Kubernetes (FREE)
2. Learn Terraform with EC2 (FREE TIER)
3. Move to EKS when ready for production

---

## 🎓 Learning Path

### Day 1: Setup (You are here!)
- ✅ Rebuild Jenkins with Terraform
- ✅ Configure AWS credentials
- ✅ Initialize Terraform

### Day 2: First Infrastructure
- Create simple EC2 instance (FREE TIER)
- Learn `terraform plan` and `apply`
- Practice `terraform destroy`

### Day 3: Kubernetes Locally
- Follow `KUBERNETES-SETUP-GUIDE.md`
- Deploy app to local Kubernetes (FREE)
- Learn kubectl commands

### Week 2: Cloud Kubernetes
- Create EKS cluster with Terraform
- Deploy app to EKS
- Configure monitoring

---

## 📚 Full Documentation

- **TERRAFORM-IN-JENKINS.md** - Complete guide with all details
- **KUBERNETES-SETUP-GUIDE.md** - Local Kubernetes setup
- **MONITORING-SETUP-GUIDE.md** - Prometheus & Grafana
- **COMPLETE-DEVOPS-GUIDE.md** - Master reference

---

## 🆘 Troubleshooting

### Terraform not found after rebuild?
```bash
# Verify installation
docker exec -u root achat-jenkins terraform version

# If not found, rebuild again
rebuild-jenkins.bat
```

### AWS credentials error?
```bash
# Test credentials
docker exec -u root achat-jenkins aws sts get-caller-identity

# If fails, re-add in Jenkins credentials
```

### Jenkins not starting?
```bash
# Check logs
docker logs achat-jenkins

# Restart
docker-compose restart jenkins
```

### Terraform init fails?
```bash
# Check you're in the right directory
# Should contain .tf files
cd terraform
ls *.tf

# Try manual init
docker exec -u root achat-jenkins /bin/bash
cd /var/jenkins_home/workspace/terraform
terraform init
```

---

## ✅ Checklist

Before you start:
- [ ] Docker Desktop running
- [ ] Jenkins running (http://localhost:8080)
- [ ] Backup any important work

To rebuild Jenkins with Terraform:
- [ ] Run `rebuild-jenkins.bat`
- [ ] Wait 5-10 minutes
- [ ] Verify Terraform installed
- [ ] Verify kubectl installed
- [ ] Verify AWS CLI installed

To use Terraform:
- [ ] Create AWS account (if using AWS)
- [ ] Configure AWS credentials in Jenkins
- [ ] Choose infrastructure (EC2 or EKS)
- [ ] Run `terraform init`
- [ ] Run `terraform plan`
- [ ] Review plan carefully
- [ ] Run `terraform apply` (creates resources)
- [ ] Test your infrastructure
- [ ] **IMPORTANT**: Run `terraform destroy` when done!

---

## 🎉 You're Ready!

Run this command to start:

```bash
rebuild-jenkins.bat
```

Then read:
- **TERRAFORM-IN-JENKINS.md** for complete details
- **terraform-eks.tf** to see what will be created

**Good luck! 🚀**

---

## Quick Command Reference

```bash
# Rebuild Jenkins with Terraform
rebuild-jenkins.bat

# Initialize Terraform
run-terraform.bat init

# Create plan (preview)
run-terraform.bat plan

# Apply changes (create infrastructure)
run-terraform.bat apply

# Show outputs
run-terraform.bat output

# Destroy infrastructure
run-terraform.bat destroy

# Access Jenkins container
docker exec -it -u root achat-jenkins /bin/bash

# Check Terraform version
docker exec -u root achat-jenkins terraform version
```


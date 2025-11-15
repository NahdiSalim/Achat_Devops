# ☁️ Terraform Infrastructure Setup - Step by Step

## Overview
Terraform will provision cloud infrastructure (AWS/Azure/GCP). This guide uses AWS.

---

## Prerequisites

1. **AWS Account** (or Azure/GCP)
2. **Terraform installed**
3. **AWS CLI installed**
4. **AWS credentials configured**

---

## Step 1: Install Terraform

### Windows:
```powershell
# Using Chocolatey
choco install terraform

# Or download from: https://www.terraform.io/downloads
```

### Verify Installation:
```bash
terraform version
```

You should see: `Terraform v1.x.x`

---

## Step 2: Install AWS CLI

### Windows:
Download from: https://aws.amazon.com/cli/

### Verify:
```bash
aws --version
```

---

## Step 3: Configure AWS Credentials

### Option A: AWS CLI Configuration
```bash
aws configure
```

Enter:
- **AWS Access Key ID**: [Your Access Key]
- **AWS Secret Access Key**: [Your Secret Key]
- **Default region**: us-east-1
- **Default output format**: json

### Option B: Environment Variables
```bash
# Windows PowerShell
$env:AWS_ACCESS_KEY_ID="your-access-key"
$env:AWS_SECRET_ACCESS_KEY="your-secret-key"
$env:AWS_DEFAULT_REGION="us-east-1"
```

### Get AWS Credentials:
1. Log in to AWS Console
2. Go to **IAM** → **Users** → **Your User**
3. **Security credentials** tab
4. Click **Create access key**
5. Save the Access Key ID and Secret Access Key

---

## Step 4: Review Terraform Files

Your Terraform files are in `terraform/` folder:
- `main.tf` - Main infrastructure definition
- `variables.tf` - Input variables
- `outputs.tf` - Output values

### Check what will be created:
```bash
cd terraform
cat main.tf
```

---

## Step 5: Update Terraform Configuration

Let me create a complete AWS infrastructure configuration:

### Your `terraform/main.tf` should define:
- VPC (Virtual Private Cloud)
- EC2 instances
- Security Groups
- Load Balancer
- RDS Database (optional)

---

## Step 6: Initialize Terraform

```bash
cd terraform

# Initialize Terraform (downloads providers)
terraform init
```

You should see: `Terraform has been successfully initialized!`

---

## Step 7: Validate Configuration

```bash
terraform validate
```

You should see: `Success! The configuration is valid.`

---

## Step 8: Plan Infrastructure Changes

```bash
# See what will be created
terraform plan

# Save plan to file
terraform plan -out=tfplan
```

Review the output carefully. It shows:
- **+** Resources to be created
- **~** Resources to be modified  
- **-** Resources to be destroyed

---

## Step 9: Apply Infrastructure (Create Resources)

### ⚠️ WARNING: This will create real AWS resources and incur costs!

```bash
# Apply changes (requires confirmation)
terraform apply

# Or apply saved plan without confirmation
terraform apply tfplan

# Type 'yes' when prompted
```

Wait for resources to be created (5-10 minutes).

---

## Step 10: View Created Resources

```bash
# Show all resources
terraform show

# Show specific outputs
terraform output

# List all resources
terraform state list
```

### In AWS Console:
1. Go to **EC2** → **Instances**
2. Check your new instances
3. Note the public IP addresses

---

## Step 11: Configure Jenkins for Terraform

### Add AWS Credentials to Jenkins:

1. Go to Jenkins → **Manage Jenkins** → **Manage Credentials**
2. Click **Add Credentials**
3. Kind: **AWS Credentials**
4. ID: `aws-credentials`
5. Access Key ID: [Your Access Key]
6. Secret Access Key: [Your Secret Key]
7. Click **OK**

### Enable Terraform Stage in Jenkinsfile:

Set environment variable in Jenkins:
```groovy
environment {
    DEPLOY_TO_AWS = 'true'
}
```

Or set it in Jenkins job configuration.

---

## Step 12: Test Terraform from Jenkins

The Terraform stage in your Jenkinsfile will:
1. Initialize Terraform
2. Validate configuration
3. Create plan
4. (Manual) Apply plan

Run your Jenkins pipeline!

---

## Step 13: Deploy Application to AWS EC2

### SSH to EC2 Instance:
```bash
# Get EC2 public IP from Terraform output
terraform output ec2_public_ip

# SSH to instance (use key pair from Terraform)
ssh -i your-key.pem ec2-user@<PUBLIC_IP>
```

### Install Docker on EC2:
```bash
sudo yum update -y
sudo yum install -y docker
sudo service docker start
sudo usermod -a -G docker ec2-user

# Run your application
docker pull salimnahdi/docker-spring-boot:latest
docker run -d -p 8089:8089 salimnahdi/docker-spring-boot:latest
```

---

## Important Terraform Commands

```bash
# Initialize
terraform init

# Validate
terraform validate

# Plan changes
terraform plan

# Apply changes
terraform apply

# Destroy all resources
terraform destroy

# Show current state
terraform show

# List resources
terraform state list

# Refresh state
terraform refresh

# Format code
terraform fmt

# Show outputs
terraform output
```

---

## Step 14: Clean Up (Destroy Resources)

### ⚠️ This will delete all AWS resources!

```bash
cd terraform

# Destroy everything
terraform destroy

# Type 'yes' to confirm
```

---

## Cost Management

### AWS Free Tier:
- **EC2**: 750 hours/month (t2.micro)
- **RDS**: 750 hours/month (db.t2.micro)
- **S3**: 5GB storage
- **Data Transfer**: 15GB/month

### Monitor Costs:
1. AWS Console → **Billing Dashboard**
2. Set up **Budget Alerts**
3. Review **Cost Explorer**

---

## Terraform Best Practices

✅ **Always run `terraform plan` before `apply`**  
✅ **Use version control for .tf files**  
✅ **Never commit .tfstate files**  
✅ **Use remote state (S3) for team projects**  
✅ **Use variables for reusable code**  
✅ **Tag all resources**  
✅ **Use modules for organization**  
❌ **Don't hardcode credentials**  
❌ **Don't run `terraform destroy` in production**

---

## Troubleshooting

### Authentication errors?
```bash
aws sts get-caller-identity
```

### Provider errors?
```bash
terraform init -upgrade
```

### State locked?
```bash
terraform force-unlock <LOCK_ID>
```

---

## Next Steps

✅ Terraform configured!  
→ Next: [Prometheus & Grafana Setup](./MONITORING-SETUP-GUIDE.md)  
→ Back to: [Kubernetes Setup](./KUBERNETES-SETUP-GUIDE.md)


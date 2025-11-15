# 🚀 Terraform in Jenkins - Complete Setup Guide

## What We're Building

Jenkins with:
- ✅ Terraform installed
- ✅ AWS CLI installed
- ✅ kubectl installed
- ✅ Docker CLI installed
- ✅ Scripts to manage infrastructure

---

## Step 1: Rebuild Jenkins with Terraform

### Windows:

```bash
# Stop current Jenkins
docker-compose stop jenkins
docker-compose rm -f jenkins

# Rebuild Jenkins image with Terraform
docker-compose build jenkins

# Start Jenkins
docker-compose up -d jenkins

# Wait for Jenkins to start (2 minutes)
timeout /t 120

# Verify installations
docker exec -u root achat-jenkins terraform version
docker exec -u root achat-jenkins kubectl version --client
docker exec -u root achat-jenkins aws --version
```

---

## Step 2: Configure AWS Credentials in Jenkins

### Option A: Using Jenkins Credentials

1. Go to Jenkins: http://localhost:8080
2. **Manage Jenkins** → **Manage Credentials**
3. Click **(global)** → **Add Credentials**
4. **Kind**: AWS Credentials
5. **ID**: `aws-credentials`
6. **Access Key ID**: Your AWS Access Key
7. **Secret Access Key**: Your AWS Secret Key
8. Click **OK**

### Option B: Using Environment Variables in Pipeline

In your Jenkinsfile, add:

```groovy
environment {
    AWS_ACCESS_KEY_ID     = credentials('aws-access-key-id')
    AWS_SECRET_ACCESS_KEY = credentials('aws-secret-access-key')
    AWS_DEFAULT_REGION    = 'us-east-1'
}
```

---

## Step 3: Choose Your Terraform Configuration

### Option 1: AWS EKS (Kubernetes Cluster)

Use the file: `terraform-eks.tf`

**What it creates:**
- EKS Kubernetes Cluster
- VPC with 2 public subnets
- Internet Gateway
- 2 t3.medium worker nodes
- All necessary IAM roles

**Cost:** ~$0.10/hour (~$73/month)

### Option 2: Simple EC2 (Basic Infrastructure)

Use your existing: `terraform/main.tf`

**What it creates:**
- EC2 instance
- Security group
- (Add your configurations)

**Cost:** ~$0.01/hour (~$7/month with t2.micro)

---

## Step 4: Initialize Terraform

### From your machine:

```bash
# Navigate to terraform directory
cd terraform

# Initialize Terraform
docker exec -u root achat-jenkins /bin/bash -c "cd /var/jenkins_home/workspace/terraform && terraform init"
```

### Or use the helper script:

```bash
run-terraform.bat init
```

---

## Step 5: Create Terraform Plan

```bash
# Create execution plan
run-terraform.bat plan

# Or manually
docker exec -u root achat-jenkins /bin/bash -c "cd /var/jenkins_home/workspace/terraform && terraform plan"
```

This shows what Terraform will create **without actually creating it**.

---

## Step 6: Apply Terraform (Create Infrastructure)

⚠️ **WARNING**: This creates real AWS resources and costs money!

```bash
# Apply changes
run-terraform.bat apply

# Or manually
docker exec -u root achat-jenkins /bin/bash -c "cd /var/jenkins_home/workspace/terraform && terraform apply -auto-approve"
```

---

## Step 7: Connect to Your EKS Cluster

After Terraform creates the EKS cluster:

```bash
# Get cluster name and region from Terraform output
docker exec -u root achat-jenkins /bin/bash -c "cd /var/jenkins_home/workspace/terraform && terraform output"

# Configure kubectl to use EKS
aws eks update-kubeconfig --region us-east-1 --name achat-eks-cluster

# Verify connection
kubectl get nodes
kubectl get pods --all-namespaces
```

---

## Step 8: Deploy Your Application to EKS

```bash
# Create namespace
kubectl create namespace achat-app

# Deploy your application
kubectl apply -f k8s/deployment.yaml -n achat-app
kubectl apply -f k8s/service.yaml -n achat-app

# Check status
kubectl get all -n achat-app

# Get service URL
kubectl get svc -n achat-app
```

---

## Step 9: Update Jenkinsfile for Terraform

The Terraform stage is already configured but needs to be enabled:

```groovy
stage('🏗️  TERRAFORM INFRASTRUCTURE') {
    when {
        allOf {
            branch 'main'
            environment name: 'DEPLOY_TO_AWS', value: 'true'
        }
    }
    steps {
        echo '======================================'
        echo '       Provisioning Infrastructure    '
        echo '======================================'

        script {
            dir('terraform') {
                withCredentials([aws(credentialsId: 'aws-credentials')]) {
                    sh '''
                        # Initialize Terraform
                        terraform init

                        # Validate configuration
                        terraform validate

                        # Plan infrastructure changes
                        terraform plan -out=tfplan

                        # Apply changes (uncomment when ready)
                        # terraform apply -auto-approve tfplan

                        echo "✅ Terraform plan created"
                    '''
                }
            }
        }
    }
}
```

To enable it, set `DEPLOY_TO_AWS=true` in Jenkins job configuration.

---

## Step 10: Destroy Infrastructure (Cleanup)

⚠️ **WARNING**: This destroys all AWS resources!

```bash
# Destroy everything
run-terraform.bat destroy

# Or manually
docker exec -u root achat-jenkins /bin/bash -c "cd /var/jenkins_home/workspace/terraform && terraform destroy -auto-approve"
```

---

## Terraform Helper Scripts

### `terraform-helper.sh` - Script inside Jenkins

```bash
# Usage inside Jenkins container
./terraform-helper.sh init
./terraform-helper.sh validate
./terraform-helper.sh plan
./terraform-helper.sh apply
./terraform-helper.sh output
./terraform-helper.sh destroy
```

### `run-terraform.bat` - Script from your machine

```bash
# Usage from Windows
run-terraform.bat init
run-terraform.bat plan
run-terraform.bat apply
run-terraform.bat destroy
```

---

## Useful Terraform Commands

```bash
# Inside Jenkins container
docker exec -u root achat-jenkins /bin/bash

# Navigate to terraform directory
cd /var/jenkins_home/workspace/terraform

# Initialize
terraform init

# Validate
terraform validate

# Plan
terraform plan

# Apply
terraform apply

# Show state
terraform show

# List resources
terraform state list

# Get outputs
terraform output

# Destroy
terraform destroy
```

---

## Cost Estimation

### EKS Cluster (terraform-eks.tf):
- EKS Control Plane: $0.10/hour ($73/month)
- 2 x t3.medium nodes: $0.0832/hour each ($120/month total)
- **Total: ~$193/month**

### Simple EC2 (t2.micro - Free Tier):
- EC2 instance: $0.0116/hour ($8.50/month)
- Or **FREE** if within AWS Free Tier
- **Total: ~$8.50/month (or $0 with Free Tier)**

### 💡 Recommendation:
Start with **t2.micro** (Free Tier eligible) before moving to EKS.

---

## Troubleshooting

### Terraform not found?
```bash
# Rebuild Jenkins
docker-compose build jenkins
docker-compose up -d jenkins
```

### AWS credentials error?
```bash
# Test AWS credentials
docker exec -u root achat-jenkins aws sts get-caller-identity

# If fails, add credentials to Jenkins
```

### Terraform state locked?
```bash
# Force unlock (use carefully!)
terraform force-unlock <LOCK_ID>
```

### kubectl can't connect to EKS?
```bash
# Update kubeconfig
aws eks update-kubeconfig --region us-east-1 --name achat-eks-cluster

# Verify
kubectl cluster-info
```

---

## Best Practices

✅ **Always run `terraform plan` before `apply`**  
✅ **Review the plan carefully**  
✅ **Use variables for reusable code**  
✅ **Tag all resources**  
✅ **Use remote state (S3) for team projects**  
✅ **Enable Terraform backend for state locking**  
❌ **Never commit AWS credentials**  
❌ **Don't run `terraform destroy` in production without backup**

---

## Next Steps

1. ✅ Rebuild Jenkins with Terraform
2. ✅ Configure AWS credentials
3. ✅ Initialize Terraform
4. ✅ Create plan
5. ⚠️ Review plan carefully
6. ⚠️ Apply (costs money!)
7. ✅ Connect to EKS cluster
8. ✅ Deploy application
9. ✅ Destroy when done testing

---

## Integration with Kubernetes Guide

After provisioning EKS with Terraform:
1. Configure kubectl to use EKS cluster
2. Follow **KUBERNETES-SETUP-GUIDE.md** from Step 2
3. Deploy your application to EKS
4. All kubectl commands work the same!

---

## Resources

- [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [Terraform EKS Module](https://registry.terraform.io/modules/terraform-aws-modules/eks/aws/latest)
- [AWS EKS Documentation](https://docs.aws.amazon.com/eks/)
- [Terraform Best Practices](https://www.terraform.io/docs/cloud/guides/recommended-practices/index.html)

---

**You now have Terraform + Kubernetes ready in Jenkins! 🚀**


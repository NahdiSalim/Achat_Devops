# 🔧 AWS Academy EKS Deployment - Fixed!

## ❌ Problem Encountered

**Error**: `User is not authorized to perform: iam:CreateRole`

**Root Cause**: AWS Academy accounts (voclabs) have restricted permissions and **cannot create IAM roles**, which are required for standard EKS deployments.

---

## ✅ Solutions Applied

### 1. **Modified `terraform-eks.tf` to Use LabRole**

**Changed From**: Creating custom IAM roles
```hcl
resource "aws_iam_role" "eks_cluster_role" {
  name = "${var.cluster_name}-cluster-role"
  ...
}
```

**Changed To**: Using AWS Academy's pre-existing `LabRole`
```hcl
data "aws_iam_role" "lab_role" {
  name = "LabRole"
}
```

**What This Means**:
- ✅ No IAM role creation needed
- ✅ Uses AWS Academy's provided role that has all necessary permissions
- ✅ Works within AWS Academy limitations

---

### 2. **Updated Jenkinsfile - Added Cleanup Stage**

**Added Stage 12**: `🧹 CLEANUP PREVIOUS TERRAFORM`

This stage:
- Checks if previous Terraform state exists
- Destroys partial infrastructure from failed builds
- Prevents conflicts with new deployments
- Automatically skips if no state exists

**Pipeline Flow Now**:
```
Stage 1-10: Build, Test, Package, Docker ✅
Stage 11:   Test AWS Credentials ✅
Stage 12:   Cleanup Previous Terraform (NEW!) ✅
Stage 13:   Terraform Infrastructure (EKS) ✅
Stage 14:   Deploy to AWS EKS ✅
```

---

## 🚀 What Happens Next

### When You Run the Pipeline:

**Stage 12 (Cleanup)**: 
- Will destroy the partial VPC/Subnets from build #6
- Cleans slate for fresh deployment

**Stage 13 (Terraform Infrastructure)**:
- Creates VPC (10.0.0.0/16)
- Creates 2 Public Subnets in us-east-1a and us-east-1b
- Creates Internet Gateway
- Creates Route Tables
- **Creates EKS Cluster** using LabRole (15-20 minutes) ☕
- **Creates EKS Node Group** with 2 t3.medium instances

**Stage 14 (Deploy to EKS)**:
- Configures kubectl to connect to EKS
- Deploys MySQL database
- Deploys your Spring Boot application
- Creates LoadBalancer service
- Waits for external URL
- Prints Swagger UI URL! 🎉

---

## ⏱️ Expected Timeline

| Stage | Duration | Status |
|-------|----------|--------|
| Stages 1-11 | ~5-7 min | Fast ✅ |
| Stage 12 (Cleanup) | ~1-2 min | Fast ✅ |
| Stage 13 (Terraform) | **15-20 min** | ⏳ Slow (Normal for EKS) |
| Stage 14 (Deploy) | ~5-10 min | Medium ✅ |
| **Total** | **~25-35 min** | Normal for first EKS deployment |

---

## 📋 Files Modified

1. ✅ **terraform-eks.tf**
   - Removed IAM role creation resources
   - Added `data "aws_iam_role" "lab_role"` lookup
   - Updated EKS cluster to use LabRole
   - Updated Node Group to use LabRole

2. ✅ **Jenkinsfile**
   - Added Stage 12: Cleanup Previous Terraform
   - Renumbered Stage 13: Terraform Infrastructure
   - Renumbered Stage 14: Deploy to AWS EKS

---

## 🎯 Next Steps

### 1. Save/Update Pipeline in Jenkins
- Go to your pipeline configuration
- Copy the updated Jenkinsfile content
- Save

### 2. Build Now!
```
Jenkins → Achat-DevOps-Pipeline → Build Now
```

### 3. Monitor Progress
- Stage 12 will clean up build #6's partial infrastructure
- Stage 13 will take 15-20 minutes (grab coffee! ☕)
- Stage 14 will deploy your app

### 4. Access Your Application
After successful deployment, you'll see:
```
════════════════════════════════════════════════════════
🎉  APPLICATION DEPLOYED SUCCESSFULLY!
════════════════════════════════════════════════════════

🌐 APPLICATION URLs:

   📍 Main API:
      http://YOUR-LOADBALANCER-URL/SpringMVC/

   📚 Swagger UI (Test your endpoints here!):
      http://YOUR-LOADBALANCER-URL/SpringMVC/swagger-ui.html

   ❤️  Health Check:
      http://YOUR-LOADBALANCER-URL/SpringMVC/actuator/health
```

---

## 💡 Important Notes

### AWS Academy Limitations:
- ✅ **Can't create IAM roles** → Solved with LabRole
- ⚠️ **Credentials expire every 3-4 hours** → Update in Jenkins UI
- ⚠️ **Resources may be limited** → t3.medium instances are fine
- ✅ **VPC, Subnets, EKS all work!** → No issues with these

### Terraform State:
- Stored in Jenkins workspace: `/var/jenkins_home/workspace/Achat-DevOps-Pipeline/`
- State file: `terraform.tfstate`
- Automatically managed by pipeline

### When Credentials Expire:
1. Get new credentials from AWS Academy
2. Update in Jenkins: Manage Credentials → Update 3 credentials
3. Re-run pipeline (it will use existing infrastructure if still running)

---

## 🎉 Success Criteria

Your pipeline will be **100% successful** when you see:

✅ All 14 stages pass
✅ EKS cluster created
✅ Application deployed
✅ LoadBalancer URL displayed
✅ Swagger UI accessible
✅ Health check returns `{"status":"UP"}`

---

## 🆘 Troubleshooting

### If Stage 12 Fails to Destroy:
- Not a problem! Pipeline continues anyway
- Manually delete resources in AWS Console if needed

### If Stage 13 Takes Too Long (>25 min):
- This is normal for first EKS deployment
- AWS is provisioning the cluster
- Check AWS Console → EKS → Clusters to see progress

### If Stage 14 LoadBalancer Doesn't Get URL:
- Wait 2-3 more minutes
- Run: `kubectl get svc -n achat-app`
- LoadBalancer provisioning can be slow

---

## 📊 Current Status

✅ **Jenkinsfile**: Updated with cleanup stage
✅ **terraform-eks.tf**: Updated to use LabRole
✅ **AWS Credentials**: Working (Stage 11 passed)
✅ **Docker Image**: Built and pushed to DockerHub
✅ **Ready to Deploy**: Yes! Just click Build Now!

---

**Good luck! 🚀**

This should work perfectly now! The LabRole approach is the standard solution for AWS Academy EKS deployments.


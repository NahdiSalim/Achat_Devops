# 🚀 Kubernetes Deployment Guide - Step by Step

## Prerequisites
- Docker Desktop with Kubernetes enabled
- kubectl CLI installed
- Your Jenkins pipeline working

---

## Step 1: Enable Kubernetes in Docker Desktop

### Windows:
1. Open **Docker Desktop**
2. Go to **Settings** → **Kubernetes**
3. Check ✅ **Enable Kubernetes**
4. Click **Apply & Restart**
5. Wait for Kubernetes to start (green indicator)

### Verify Installation:
```bash
kubectl version --client
kubectl cluster-info
kubectl get nodes
```

You should see your local node ready.

---

## Step 2: Create Kubernetes Namespace

```bash
kubectl create namespace achat-app
kubectl get namespaces
```

---

## Step 3: Update Kubernetes Manifests

Your K8s files are already in the `k8s/` folder. Let's verify and update them:

### Check existing files:
```bash
ls k8s/
```

You should see:
- `namespace.yaml`
- `configmap.yaml`
- `secrets.yaml`
- `mysql-deployment.yaml`
- `deployment.yaml`
- `service.yaml`
- `hpa.yaml`
- `ingress.yaml`

---

## Step 4: Create Kubernetes Secrets

Create secrets for sensitive data:

```bash
# Create Docker registry secret (for pulling images from DockerHub)
kubectl create secret docker-registry dockerhub-secret \
  --docker-server=https://index.docker.io/v1/ \
  --docker-username=salimnahdi \
  --docker-password=Salim09531699 \
  --docker-email=your-email@example.com \
  -n achat-app

# Create MySQL secret
kubectl create secret generic mysql-secret \
  --from-literal=mysql-root-password=root \
  --from-literal=mysql-password=achat_password \
  -n achat-app

# Verify secrets
kubectl get secrets -n achat-app
```

---

## Step 5: Deploy MySQL to Kubernetes

```bash
# Apply MySQL deployment
kubectl apply -f k8s/mysql-deployment.yaml -n achat-app

# Check MySQL pod status
kubectl get pods -n achat-app
kubectl logs -f <mysql-pod-name> -n achat-app

# Wait for MySQL to be ready
kubectl wait --for=condition=ready pod -l app=mysql -n achat-app --timeout=300s
```

---

## Step 6: Update Deployment YAML

Make sure your `k8s/deployment.yaml` has the correct image tag placeholder.

The Jenkinsfile will replace `IMAGE_TAG` with the build number.

---

## Step 7: Apply All Kubernetes Resources

```bash
# Apply in order
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/mysql-deployment.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/hpa.yaml

# For ingress (optional - needs ingress controller)
# kubectl apply -f k8s/ingress.yaml
```

---

## Step 8: Verify Deployment

```bash
# Check all resources
kubectl get all -n achat-app

# Check pods
kubectl get pods -n achat-app

# Check services
kubectl get svc -n achat-app

# Check deployments
kubectl get deployments -n achat-app

# View pod logs
kubectl logs -f deployment/achat-app -n achat-app
```

---

## Step 9: Access Your Application

### Option A: Port Forward (Quick Test)
```bash
kubectl port-forward service/achat-service 8089:8089 -n achat-app
```
Then access: http://localhost:8089/SpringMVC/

### Option B: NodePort (Permanent)
Your service is already configured as NodePort. Find the port:
```bash
kubectl get svc achat-service -n achat-app
```
Access via: http://localhost:<NodePort>/SpringMVC/

---

## Step 10: Configure Jenkins for Kubernetes

### Install Kubernetes Plugin in Jenkins:
1. Go to Jenkins → **Manage Jenkins** → **Manage Plugins**
2. Install: **Kubernetes CLI Plugin**
3. Restart Jenkins

### Add Kubernetes Configuration to Jenkins:

1. Go to **Manage Jenkins** → **Manage Credentials**
2. Add kubeconfig file:
   - Kind: **Secret file**
   - ID: `kubeconfig`
   - File: Upload your `~/.kube/config`

### Update Jenkinsfile Kubernetes Stage:

The stage is already configured, but make sure you have the kubeconfig credential.

---

## Step 11: Test Kubernetes Deployment from Jenkins

Run your Jenkins pipeline and it should now deploy to Kubernetes!

---

## Useful Kubernetes Commands

```bash
# View pod logs
kubectl logs -f <pod-name> -n achat-app

# Describe pod (debugging)
kubectl describe pod <pod-name> -n achat-app

# Execute command in pod
kubectl exec -it <pod-name> -n achat-app -- /bin/bash

# Delete resources
kubectl delete -f k8s/deployment.yaml -n achat-app

# Scale deployment
kubectl scale deployment achat-app --replicas=3 -n achat-app

# View events
kubectl get events -n achat-app --sort-by='.lastTimestamp'

# View resource usage
kubectl top pods -n achat-app
kubectl top nodes
```

---

## Troubleshooting

### Pod not starting?
```bash
kubectl describe pod <pod-name> -n achat-app
kubectl logs <pod-name> -n achat-app
```

### Image pull errors?
- Verify DockerHub credentials
- Check image name and tag
- Ensure Docker image exists: `docker pull salimnahdi/docker-spring-boot:latest`

### MySQL connection errors?
- Check MySQL pod is running
- Verify service name in configmap
- Check database name and credentials

---

## Next Steps

✅ Kubernetes deployed!  
→ Next: [Terraform Setup](./TERRAFORM-SETUP-GUIDE.md)  
→ Next: [Prometheus & Grafana](./MONITORING-SETUP-GUIDE.md)


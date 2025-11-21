# 🚀 Docker Setup and Testing Guide for Achat DevOps Application

## ✅ What Was Fixed

1. **Created HomeController** - Now when you access `http://localhost:8089/SpringMVC/`, you'll get API information
2. **Swagger Already Configured** - Your SpringFox Swagger configuration is ready to use
3. **Updated Application Properties** - Added documentation for Docker compatibility
4. **All Endpoints Ready** - You can test all REST endpoints via Swagger UI

---

## 📋 Prerequisites

- Docker Desktop installed and running
- Docker Compose installed
- Port 8089 (app), 3306 (MySQL) available

---

## 🐳 Step-by-Step Docker Deployment

### Step 1: Stop Any Running Containers

```bash
docker-compose down -v
```

### Step 2: Clean Up Old Images (Optional)

```bash
docker system prune -f
docker rmi achat_devops-app 2>/dev/null || true
```

### Step 3: Build and Start Only App and MySQL

For first-time testing, let's start only the essential services (app + MySQL):

```bash
# Start only MySQL and the application
docker-compose up -d mysql app
```

**Wait for containers to be healthy** (usually 30-60 seconds):

```bash
# Check status
docker-compose ps

# Check logs
docker-compose logs -f app
```

You should see logs like:
```
Started AchatApplication in X.XXX seconds
```

### Step 4: Verify the Application is Running

Open your browser and test these URLs:

1. **Home/API Info**: http://localhost:8089/SpringMVC/
   - Should show JSON with application info and available endpoints

2. **Swagger UI**: http://localhost:8089/SpringMVC/swagger-ui.html
   - Interactive API documentation
   - You can test all endpoints here!
   - **Note:** Use `.html` at the end!

3. **API Docs (JSON)**: http://localhost:8089/SpringMVC/v2/api-docs
   - OpenAPI/Swagger JSON specification

4. **Health Check**: http://localhost:8089/SpringMVC/actuator/health
   - Application health status

---

## 🧪 Testing All Endpoints via Swagger UI

### Access Swagger UI
Navigate to: **http://localhost:8089/SpringMVC/swagger-ui.html**

### Available API Groups in Swagger:

1. **Application Info** - Basic app information
2. **Gestion des produits** (Product Management)
3. **Gestion des stocks** (Stock Management)
4. **Gestion des fournisseurs** (Supplier Management)
5. **Gestion des factures** (Invoice Management)
6. **Gestion des opérateurs** (Operator Management)
7. **Gestion des règlements** (Payment Management)
8. **Gestion des catégories** (Category Management)
9. **Gestion des secteurs d'activité** (Business Sector Management)

### How to Test Endpoints in Swagger UI:

1. **Click on any API group** to expand it
2. **Click on an endpoint** (e.g., GET /produit/retrieve-all-produits)
3. **Click "Try it out"** button
4. **Fill in parameters** if required
5. **Click "Execute"** to test the endpoint
6. **See the response** below with status code and JSON data

---

## 📝 Example: Testing Product Endpoints

### 1. Get All Products (GET)
- Endpoint: `GET /SpringMVC/produit/retrieve-all-produits`
- No parameters needed
- Should return an array of products (may be empty initially)

### 2. Add a Product (POST)
- Endpoint: `POST /SpringMVC/produit/add-produit`
- Request Body Example:
```json
{
  "code": "PROD001",
  "libelle": "Product 1",
  "prix": 100.50,
  "dateCreation": "2025-11-21",
  "dateDerniereModification": "2025-11-21"
}
```

### 3. Get Product by ID (GET)
- Endpoint: `GET /SpringMVC/produit/retrieve-produit/{produit-id}`
- Parameter: produit-id = 1

### 4. Update Product (PUT)
- Endpoint: `PUT /SpringMVC/produit/modify-produit`
- Request Body with updated values

### 5. Delete Product (DELETE)
- Endpoint: `DELETE /SpringMVC/produit/remove-produit/{produit-id}`
- Parameter: produit-id = 1

---

## 🔍 Troubleshooting

### Issue: "Cette page ne fonctionne pas" Error

**Solution**: Make sure you're accessing the correct URL with the context path:
- ❌ Wrong: `http://localhost:8089/`
- ✅ Correct: `http://localhost:8089/SpringMVC/`

### Issue: Application Won't Start

```bash
# Check container logs
docker-compose logs app

# Check MySQL logs
docker-compose logs mysql

# Restart containers
docker-compose restart app
```

### Issue: MySQL Connection Failed

```bash
# Verify MySQL is healthy
docker-compose ps mysql

# Wait for MySQL to be fully started (check logs)
docker-compose logs mysql | grep "ready for connections"

# If needed, restart the app after MySQL is ready
docker-compose restart app
```

### Issue: Port Already in Use

```bash
# Check what's using port 8089
netstat -ano | findstr :8089

# Stop the process or change the port in application.properties
```

### Issue: Container Keeps Restarting

```bash
# Check full logs
docker-compose logs --tail=100 app

# Check if the JAR was built correctly
docker-compose exec app ls -la /app/

# Rebuild from scratch
docker-compose down -v
docker-compose build --no-cache app
docker-compose up -d mysql app
```

---

## 🛑 Stopping the Application

```bash
# Stop all containers
docker-compose down

# Stop and remove all data (including database)
docker-compose down -v
```

---

## 🚀 Starting All Services (Full Stack)

Once you've verified the app works, you can start all services:

```bash
docker-compose up -d
```

This will start:
- ✅ MySQL (port 3306)
- ✅ Application (port 8089)
- ✅ Jenkins (port 8080)
- ✅ SonarQube (port 9000)
- ✅ Nexus (port 8081, 8082)
- ✅ Prometheus (port 9090)
- ✅ Grafana (port 3000)

---

## 📊 Testing with Postman (Alternative to Swagger)

You already have a Postman collection: `Achat-API.postman_collection.json`

Import it into Postman to test all endpoints!

---

## 🎯 Quick Reference

| Service | URL | Description |
|---------|-----|-------------|
| **API Home** | http://localhost:8089/SpringMVC/ | API Information |
| **Swagger UI** | http://localhost:8089/SpringMVC/swagger-ui/ | Interactive API Docs |
| **Health Check** | http://localhost:8089/SpringMVC/actuator/health | Application Health |
| **Metrics** | http://localhost:8089/SpringMVC/actuator/prometheus | Prometheus Metrics |
| **MySQL** | localhost:3306 | Database |

---

## ✅ Success Checklist

- [ ] Docker containers are running (`docker-compose ps`)
- [ ] Can access http://localhost:8089/SpringMVC/
- [ ] Can access Swagger UI at http://localhost:8089/SpringMVC/swagger-ui/
- [ ] Can test GET endpoints in Swagger
- [ ] Can test POST endpoints to create data
- [ ] Application logs show no errors

---

## 🎉 You're All Set!

Your Achat DevOps application is now:
- ✅ Running in Docker
- ✅ Connected to MySQL
- ✅ Swagger UI configured and accessible
- ✅ All endpoints ready for testing
- ✅ Health checks working
- ✅ Prometheus metrics enabled

**Happy Testing! 🚀**


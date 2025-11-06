# 🚀 Quick Start Guide - Achat DevOps Project

This guide will help you get the Achat application up and running quickly.

## ⚡ 5-Minute Quick Start

### Prerequisites Check
- ✅ Java 18 installed
- ✅ Maven or Maven Wrapper available
- ✅ MySQL running (or use Docker Compose)

### Option 1: Local Development (Fastest)

```bash
# 1. Clone the repository
git clone https://github.com/yourusername/Achat_Devops.git
cd Achat_Devops

# 2. Configure database (edit src/main/resources/application.properties)
# Or use default settings with MySQL on localhost:3306

# 3. Create database
mysql -u root -p -e "CREATE DATABASE achatdb;"

# 4. Run the application
./mvnw.cmd spring-boot:run  # Windows
./mvnw spring-boot:run      # Linux/Mac

# 5. Access the application
# Main App: http://localhost:8089/SpringMVC
# Swagger UI: http://localhost:8089/SpringMVC/swagger-ui/
```

### Option 2: Docker Compose (Recommended)

```bash
# 1. Clone the repository
git clone https://github.com/yourusername/Achat_Devops.git
cd Achat_Devops

# 2. Build the application
./mvnw.cmd clean package  # Windows
./mvnw clean package      # Linux/Mac

# 3. Start all services with Docker Compose
docker-compose up -d

# 4. Wait for services to be ready (30-60 seconds)

# 5. Access services
```

**Available Services:**

| Service | URL | Credentials |
|---------|-----|-------------|
| Application | http://localhost:8089/SpringMVC | - |
| Swagger API | http://localhost:8089/SpringMVC/swagger-ui/ | - |
| MySQL | localhost:3306 | root/root |
| Jenkins | http://localhost:8080 | See initial setup |
| SonarQube | http://localhost:9000 | admin/admin |
| Nexus | http://localhost:8081 | admin/admin123 |
| Prometheus | http://localhost:9090 | - |
| Grafana | http://localhost:3000 | admin/admin |

## 🧪 Run Tests

```bash
# Run all tests
./mvnw.cmd test

# View coverage report
# Open: target/site/jacoco/index.html
```

## 📚 API Examples

### Get All Suppliers
```bash
curl http://localhost:8089/SpringMVC/fournisseur/retrieve-all-fournisseurs
```

### Create Supplier
```bash
curl -X POST http://localhost:8089/SpringMVC/fournisseur/add-fournisseur \
  -H "Content-Type: application/json" \
  -d '{
    "code": "FRN001",
    "libelle": "Test Supplier",
    "categorieFournisseur": "ORDINAIRE"
  }'
```

### Get Supplier by ID
```bash
curl http://localhost:8089/SpringMVC/fournisseur/retrieve-fournisseur/1
```

### Update Supplier
```bash
curl -X PUT http://localhost:8089/SpringMVC/fournisseur/modify-fournisseur \
  -H "Content-Type: application/json" \
  -d '{
    "idFournisseur": 1,
    "code": "FRN001-UPDATED",
    "libelle": "Updated Supplier",
    "categorieFournisseur": "CONVENTIONNE"
  }'
```

### Delete Supplier
```bash
curl -X DELETE http://localhost:8089/SpringMVC/fournisseur/remove-fournisseur/1
```

## 🔧 Common Issues & Solutions

### Issue: Port 8089 already in use
```bash
# Windows - Find and kill process
netstat -ano | findstr :8089
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8089
kill -9 <PID>
```

### Issue: Maven build fails
```bash
# Clean and rebuild
./mvnw.cmd clean install -U
```

### Issue: Database connection error
```bash
# Check MySQL is running
mysql -u root -p -e "SHOW DATABASES;"

# Verify database exists
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS achatdb;"
```

### Issue: Docker Compose services won't start
```bash
# Stop all containers
docker-compose down

# Remove volumes and restart
docker-compose down -v
docker-compose up -d
```

## 📊 Health Checks

```bash
# Application health
curl http://localhost:8089/SpringMVC/actuator/health

# Prometheus metrics
curl http://localhost:8089/SpringMVC/actuator/prometheus

# Database check
mysql -h localhost -u root -p achatdb -e "SHOW TABLES;"
```

## 🎯 Next Steps

1. ✅ Application running → Explore Swagger UI
2. ✅ Tests passing → Review coverage report
3. ✅ Docker Compose up → Configure Jenkins pipeline
4. ✅ All services ready → Set up monitoring dashboards

## 📖 Full Documentation

- **README.md** - Complete project documentation
- **DEVOPS-GUIDE.md** - Detailed DevOps setup guide
- **TEST-REPORT.md** - Comprehensive test results
- **API Documentation** - Available at Swagger UI

## 💡 Pro Tips

1. Use **Docker Compose** for the complete DevOps stack
2. Access **Swagger UI** for interactive API testing
3. Check **Grafana dashboards** for monitoring
4. Review **JaCoCo reports** for test coverage
5. Use **Jenkins** for automated CI/CD

## 🆘 Need Help?

- Check the logs: `docker-compose logs -f app`
- Review documentation in the project
- Open an issue on GitHub
- Check console output for errors

---

**Ready to deploy?** See `DEVOPS-GUIDE.md` for production deployment instructions.


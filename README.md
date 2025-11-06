# Achat DevOps Project

## 📋 Project Overview

**Achat** is a Spring Boot application designed for purchase management ("Achat" means "Purchase" in French). This project demonstrates a complete DevOps pipeline implementation with automated testing, code quality analysis, containerization, and monitoring.

### Key Features
- **Supplier Management (Fournisseur)** - Complete CRUD operations for suppliers
- **Invoicing System (Facture)** - Invoice management with supplier relationships
- **Product Catalog (Produit)** - Product inventory management
- **Stock Management** - Real-time stock tracking
- **Sector Activities** - Business sector categorization

## 🛠️ Technology Stack

### Backend
- **Java 18**
- **Spring Boot 2.5.3**
- **Spring Data JPA** - Database ORM
- **Hibernate** - JPA Implementation
- **MySQL** - Production Database
- **H2** - In-memory database for testing
- **Lombok** - Code generation
- **Swagger/SpringFox** - API Documentation

### Testing
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework
- **Spring Boot Test** - Integration testing
- **JaCoCo** - Code coverage analysis

### DevOps Tools
- **Maven** - Build automation
- **Git** - Version control
- **Jenkins** - CI/CD automation
- **SonarQube** - Code quality analysis
- **Nexus** - Artifact repository
- **Docker** - Containerization
- **Terraform** - Infrastructure as Code
- **Prometheus** - Monitoring
- **Grafana** - Visualization

## 🚀 Getting Started

### Prerequisites
- Java 18 or higher
- MySQL 5.7+
- Maven 3.6+ (or use Maven wrapper)

### Installation

1. **Clone the repository:**
```bash
git clone https://github.com/yourusername/Achat_Devops.git
cd Achat_Devops
```

2. **Configure the database:**

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/achatdb
spring.datasource.username=root
spring.datasource.password=your_password
```

3. **Create the database:**
```sql
CREATE DATABASE achatdb;
```

4. **Build the project:**
```bash
# Using Maven Wrapper (recommended)
./mvnw.cmd clean install  # Windows
./mvnw clean install      # Linux/Mac

# Or using Maven
mvn clean install
```

5. **Run the application:**
```bash
./mvnw.cmd spring-boot:run  # Windows
./mvnw spring-boot:run      # Linux/Mac
```

The application will start on `http://localhost:8089/SpringMVC`

### API Documentation

Access Swagger UI at: `http://localhost:8089/SpringMVC/swagger-ui/`

## 🧪 Testing

### Run All Tests
```bash
./mvnw.cmd test
```

### Test Coverage Report
After running tests, view the JaCoCo report:
```
target/site/jacoco/index.html
```

### Test Statistics
- **Total Tests:** 49
- **Unit Tests:** 15 (Service Layer)
- **Integration Tests:** 23 (Repository + Integration)
- **Controller Tests:** 11 (REST API)

### Fournisseur (Supplier) Tests

The project includes comprehensive tests for the Fournisseur entity:

#### Service Layer Tests (`FournisseurServiceImplTest`)
- ✅ Retrieve all suppliers
- ✅ Add new supplier
- ✅ Update supplier
- ✅ Delete supplier
- ✅ Retrieve single supplier by ID
- ✅ Assign sector activity to supplier
- ✅ Handle null/invalid data
- ✅ Test both supplier categories (ORDINAIRE, CONVENTIONNE)

#### Repository Layer Tests (`FournisseurRepositoryTest`)
- ✅ Save supplier with cascade operations
- ✅ Find supplier by ID
- ✅ Update supplier details
- ✅ Delete supplier
- ✅ Count operations
- ✅ Exists validation

#### Controller Layer Tests (`FournisseurRestControllerTest`)
- ✅ GET `/fournisseur/retrieve-all-fournisseurs`
- ✅ GET `/fournisseur/retrieve-fournisseur/{id}`
- ✅ POST `/fournisseur/add-fournisseur`
- ✅ PUT `/fournisseur/modify-fournisseur`
- ✅ DELETE `/fournisseur/remove-fournisseur/{id}`
- ✅ PUT `/fournisseur/assignSecteurActiviteToFournisseur/{idSecteur}/{idFournisseur}`

#### Integration Tests (`FournisseurIntegrationTest`)
- ✅ Complete CRUD flow testing
- ✅ Database transaction verification
- ✅ Relationship management testing

## 📊 Project Structure

```
Achat_Devops/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── tn/esprit/rh/achat/
│   │   │       ├── AchatApplication.java
│   │   │       ├── controllers/          # REST Controllers
│   │   │       ├── entities/             # JPA Entities
│   │   │       ├── repositories/         # Spring Data Repositories
│   │   │       ├── services/             # Business Logic
│   │   │       └── util/                 # Utilities (Swagger config)
│   │   └── resources/
│   │       └── application.properties    # Application configuration
│   └── test/
│       ├── java/
│       │   └── tn/esprit/rh/achat/
│       │       ├── controllers/          # Controller tests
│       │       ├── integration/          # Integration tests
│       │       ├── repositories/         # Repository tests
│       │       └── services/             # Service tests
│       └── resources/
│           └── application.properties    # Test configuration
├── pom.xml                               # Maven configuration
├── Dockerfile                            # Docker configuration
├── Jenkinsfile                           # Jenkins pipeline
├── docker-compose.yml                    # Docker Compose setup
└── README.md                             # This file
```

## 🐳 Docker Deployment

### Build Docker Image
```bash
docker build -t achat-app:latest .
```

### Run with Docker Compose
```bash
docker-compose up -d
```

Services:
- **Application:** http://localhost:8089
- **MySQL:** localhost:3306
- **Prometheus:** http://localhost:9090
- **Grafana:** http://localhost:3000

## 🔄 CI/CD Pipeline

The project includes a complete Jenkins pipeline with the following stages:

1. **Checkout** - Pull code from Git
2. **Build** - Compile and package with Maven
3. **Test** - Run all unit and integration tests
4. **Code Quality** - SonarQube analysis
5. **Package** - Create JAR artifact
6. **Publish** - Upload to Nexus repository
7. **Docker Build** - Create Docker image
8. **Docker Push** - Push to Docker registry
9. **Deploy** - Deploy using Terraform

See [DEVOPS-GUIDE.md](DEVOPS-GUIDE.md) for detailed DevOps configuration.

## 📝 API Endpoints

### Fournisseur (Supplier) Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/fournisseur/retrieve-all-fournisseurs` | Get all suppliers |
| GET | `/fournisseur/retrieve-fournisseur/{id}` | Get supplier by ID |
| POST | `/fournisseur/add-fournisseur` | Create new supplier |
| PUT | `/fournisseur/modify-fournisseur` | Update supplier |
| DELETE | `/fournisseur/remove-fournisseur/{id}` | Delete supplier |
| PUT | `/fournisseur/assignSecteurActiviteToFournisseur/{idSecteur}/{idFournisseur}` | Assign sector to supplier |

### Example Request - Add Supplier

```bash
curl -X POST http://localhost:8089/SpringMVC/fournisseur/add-fournisseur \
  -H "Content-Type: application/json" \
  -d '{
    "code": "FRN001",
    "libelle": "Supplier Name",
    "categorieFournisseur": "ORDINAIRE"
  }'
```

## 📈 Monitoring

### Prometheus Metrics
Access at: `http://localhost:9090`

Key metrics:
- Application health
- JVM statistics
- HTTP request metrics
- Database connection pool

### Grafana Dashboards
Access at: `http://localhost:3000`
- Default credentials: `admin/admin`

Pre-configured dashboards for:
- Application overview
- JVM metrics
- Database performance
- API response times

## 🔐 Security

- **Database passwords** should be stored in environment variables
- **JWT authentication** can be added for API security
- **HTTPS** should be enabled in production

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👥 Authors

- **Your Name** - Initial work

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- DevOps community for best practices
- Open source contributors

## 📞 Support

For support, email your-email@example.com or open an issue in the repository.

---

Made with ❤️ for DevOps


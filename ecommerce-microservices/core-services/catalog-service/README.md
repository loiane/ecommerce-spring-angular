# Product Catalog Service

A microservice for managing products and categories in an e-commerce platform, built with Spring Boot 3.5 and PostgreSQL.

## 🌟 Features

- **Product Management**: CRUD operations for products with advanced filtering
- **Category Management**: Hierarchical category structure with parent-child relationships
- **Advanced Search**: JPA Specifications-based filtering with pagination and sorting
- **Data Validation**: Comprehensive input validation and error handling
- **Database Migration**: Flyway for schema versioning and data migrations
- **Health Monitoring**: Spring Boot Actuator for health checks and metrics
- **Containerization**: Docker Compose for local development environment

## 🚀 Quick Start

### Prerequisites

- Java 17+ (tested with Java 24)
- Docker and Docker Compose
- Maven 3.9+

### 1. Clone and Setup

```bash
git clone <repository-url>
cd ecommerce-microservices/core-services/catalog-service
```

### 2. Start Infrastructure

```bash
# Start PostgreSQL and Adminer
docker compose up -d

# Verify containers are running
docker ps
```

### 3. Run the Application

```bash
# Development profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Or using VS Code Spring Boot Dashboard
# Select "ProductServiceApplication" and run with dev profile
```

### 4. Verify Setup

- **Application**: http://localhost:8081
- **Interactive API Docs**: http://localhost:8081/swagger-ui.html
- **Health Check**: http://localhost:8081/actuator/health
- **Database Admin**: http://localhost:8085 (Adminer)
  - Server: `postgres`
  - Username: `product`
  - Password: `product`
  - Database: `product`

## 📊 Database Schema

The service automatically creates the following tables via Flyway migrations:

- **categories**: Category hierarchy with parent-child relationships
- **products**: Product catalog with attributes and status
- **product_categories**: Many-to-many relationship between products and categories

See [docs/DATABASE_SCHEMA.md](docs/DATABASE_SCHEMA.md) for detailed schema information.

## 🔍 API Documentation

### 📋 Interactive API Documentation

This service provides comprehensive **OpenAPI 3.0 documentation** with an interactive Swagger UI interface for easy API exploration and testing.

#### Access the Documentation

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs
- **OpenAPI YAML**: http://localhost:8081/v3/api-docs.yaml

#### Features

- 🚀 **Interactive Testing**: Try out API endpoints directly from the browser
- 📖 **Complete Documentation**: Detailed descriptions for all endpoints, parameters, and responses
- 🔍 **Schema Explorer**: Explore request/response models with examples
- 📊 **Response Examples**: Auto-generated examples with realistic data
- 🎯 **HTTP Status Codes**: Clear documentation of all possible response codes

#### Using the Swagger UI

1. **Navigate** to http://localhost:8081/swagger-ui.html
2. **Explore** the available endpoints organized by tags (Products, Categories)
3. **Try out** endpoints by clicking "Try it out" button
4. **Fill in** required parameters and request bodies
5. **Execute** requests and see real-time responses

### Core Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | List all products (paginated) |
| GET | `/api/products/search` | **NEW**: Advanced product search with filters |
| GET | `/api/products/{id}` | Get product by ID |
| POST | `/api/products` | Create new product |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |
| GET | `/api/categories` | List all categories (paginated) |
| GET | `/api/categories/search` | **NEW**: Advanced category search with filters |
| GET | `/api/categories/{id}` | Get category by ID |
| POST | `/api/categories` | Create new category |
| PUT | `/api/categories/{id}` | Update category |
| DELETE | `/api/categories/{id}` | Delete category |

### 🆕 Advanced Search Features

#### Product Search
```bash
# Search by name
GET /api/products/search?name=laptop

# Multiple filters with pagination
GET /api/products/search?name=laptop&status=ACTIVE&brand=apple&page=0&size=10

# Filter by categories
GET /api/products/search?categoryIds=uuid1,uuid2&sort=price,desc
```

#### Category Search
```bash
# Find root categories
GET /api/categories/search?isRoot=true

# Search by name with pagination
GET /api/categories/search?name=electronics&page=0&size=5&sort=name,asc

# Find subcategories
GET /api/categories/search?parentId=parent-uuid
```

### Available Filters

**Products:**
- `name` - Text search (case-insensitive)
- `status` - Exact match (ACTIVE, DRAFT, DISCONTINUED)
- `brand` - Text search (case-insensitive)
- `sku` - Text search (case-insensitive)
- `categoryIds` - Filter by category IDs (comma-separated)

**Categories:**
- `name` - Text search (case-insensitive)
- `slug` - Text search (case-insensitive)
- `parentId` - Filter by parent category
- `isRoot` - Root categories (true) or subcategories (false)

For complete API documentation, see the **Interactive Swagger UI** at http://localhost:8081/swagger-ui.html or [docs/API.md](docs/API.md).

## 🏗️ Architecture

### Technology Stack

- **Framework**: Spring Boot 3.5.4
- **Java Version**: 17+ (tested with Java 24)
- **Database**: PostgreSQL 16
- **Migration**: Flyway
- **Testing**: JUnit 5, Testcontainers
- **Build**: Maven
- **Containerization**: Docker Compose

### Project Structure

```
src/main/java/com/loiane/product/
├── ProductServiceApplication.java     # Main application class
├── category/                          # Category feature package
│   ├── Category.java                  # JPA Entity
│   ├── CategoryRepository.java        # Data access
│   ├── CategoryService.java           # Business logic
│   ├── CategorySpecification.java     # JPA Specifications for filtering
│   └── api/                           # REST API layer
│       ├── CategoryController.java    # REST endpoints
│       ├── CategoryMapper.java        # DTO mapping
│       └── dto/                       # Request/Response DTOs
├── product/                           # Product feature package
│   ├── Product.java                   # JPA Entity
│   ├── ProductRepository.java         # Data access
│   ├── ProductService.java            # Business logic
│   ├── ProductSpecification.java      # JPA Specifications for filtering
│   └── api/                           # REST API layer
│       ├── ProductController.java     # REST endpoints
│       ├── ProductMapper.java         # DTO mapping
│       └── dto/                       # Request/Response DTOs
└── common/                            # Shared components
    └── api/
        └── GlobalExceptionHandler.java # Error handling
```

### Design Patterns

- **Feature-based packaging**: Organized by business features, not technical layers
- **JPA Specifications**: Type-safe dynamic queries for filtering
- **Repository pattern**: Data access abstraction
- **DTO pattern**: Request/response data transfer objects
- **Mapper pattern**: Entity-DTO conversion utilities

## 🧪 Testing

### Run Tests

```bash
# Unit tests
./mvnw test

# Integration tests with Testcontainers
./mvnw verify

# Test coverage report
./mvnw jacoco:report
```

### Test Categories

- **Unit Tests**: Service and specification logic
- **Integration Tests**: Repository and API endpoints with Testcontainers
- **Contract Tests**: API response validation

## 🔧 Configuration

### Profiles

- **dev**: Development with local PostgreSQL
- **test**: Testing with Testcontainers
- **prod**: Production configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `PRODUCT_DB_HOST` | Database host | `localhost` |
| `PRODUCT_DB_PORT` | Database port | `5432` |
| `PRODUCT_DB_NAME` | Database name | `product` |
| `PRODUCT_DB_USERNAME` | Database username | `product` |
| `PRODUCT_DB_PASSWORD` | Database password | `product` |

### Configuration Files

- `application.yml`: Base configuration
- `application-dev.yml`: Development settings
- `application-test.yml`: Test configuration
- `application-prod.yml`: Production settings

## 📈 Monitoring & Observability

### Actuator Endpoints

- `/actuator/health`: Application health status
- `/actuator/info`: Application information
- `/actuator/metrics`: Application metrics

### Logging

Structured logging with appropriate levels:
- **INFO**: Application lifecycle events
- **DEBUG**: Development debugging (dev profile only)
- **WARN**: Potential issues
- **ERROR**: Application errors

## 🚢 Deployment

### Docker

```bash
# Build application
./mvnw clean package

# Build and run with Docker Compose
docker compose up --build
```

### Production Considerations

- Set `spring.profiles.active=prod`
- Configure external PostgreSQL instance
- Set up monitoring and alerting
- Configure appropriate JVM settings
- Implement security measures (authentication, authorization)
- Set up load balancing if needed

## 📚 Documentation

- [API Documentation](docs/API.md) - Complete REST API reference
- [Filtering and Search Guide](docs/FILTERING_AND_SEARCH.md) - Advanced search capabilities
- [Database Schema](docs/DATABASE_SCHEMA.md) - Database structure and relationships
- [Testing Guide](docs/TESTING.md) - Testing strategies and examples

## 🤝 Contributing

1. Follow the Java code style guidelines in `.github/instructions/java.instructions.md`
2. Write tests for new features
3. Update documentation for API changes
4. Use feature-based packaging for new functionality
5. Follow conventional commit messages

## 📋 TODO / Roadmap

- [x] ~~OpenAPI 3.0 documentation generation~~ ✅ **COMPLETED**
- [ ] Caching with Redis for frequently accessed data
- [ ] Security implementation (JWT, OAuth2)
- [ ] Full-text search with PostgreSQL or Elasticsearch
- [ ] Event-driven architecture with messaging
- [ ] Performance optimization and monitoring
- [ ] API versioning strategy
- [ ] Comprehensive integration tests

## 📄 License

This project is part of the ecommerce-spring-angular microservices architecture.

---

**Built with ❤️ using Spring Boot and modern Java practices**

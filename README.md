# Ecommerce Microservices Platform

A comprehensive ecommerce platform built with Spring Boot microservices architecture, featuring AI-powered recommendations and intelligent chat support.

## 🏗️ Architecture Overview

This project implements a modern microservices architecture with the following key components:

- **Core Business Services**: User management, product catalog, orders, payments, cart, inventory, notifications, and shipping
- **Infrastructure Services**: API Gateway, Service Discovery, Configuration Server, and Circuit Breaker
- **AI Services**: Recommendation engine and intelligent chat support
- **Supporting Services**: Reviews/ratings and file storage

## 🚀 Services

### Infrastructure Services

| Service | Port | Description | Technology Stack |
|---------|------|-------------|------------------|
| **API Gateway** | 8080 | Single entry point, routing, authentication | Spring Cloud Gateway |
| **Service Discovery** | 8761 | Service registration and discovery | Netflix Eureka |
| **Config Server** | 8888 | Centralized configuration management | Spring Cloud Config |
| **Circuit Breaker** | - | Fault tolerance and resilience | Resilience4j |

### Core Business Services

| Service | Port | Description | Database | Technology Stack |
|---------|------|-------------|----------|------------------|
| **User Service** | 8081 | Authentication, user management | PostgreSQL | Spring Boot, Spring Security, JWT |
| **Product Catalog** | 8081 | Product & category management, advanced search & filtering | PostgreSQL | Spring Boot, Spring Data JPA, JPA Specifications |
| **Order Service** | 8083 | Order processing and management | PostgreSQL | Spring Boot, Spring Data JPA |
| **Payment Service** | 8084 | Payment processing, transactions | PostgreSQL | Spring Boot, Spring Security |
| **Shopping Cart** | 8085 | Cart management | Redis | Spring Boot, Redis |
| **Inventory Service** | 8086 | Stock management | MySQL | Spring Boot, Spring Data JPA |
| **Notification Service** | 8087 | Email, SMS, push notifications | MongoDB | Spring Boot, RabbitMQ |
| **Shipping Service** | 8088 | Shipping calculations, tracking | MySQL | Spring Boot |

### AI Services

| Service | Port | Description | Database | Technology Stack |
|---------|------|-------------|----------|------------------|
| **Recommendation Service** | 8091 | Personalized product recommendations | PostgreSQL + Redis | Spring Boot, Apache Mahout, Redis |
| **Chat Service** | 8092 | AI-powered customer support | Redis + PostgreSQL | Spring Boot, OpenAI GPT-4, WebSocket |

### Supporting Services

| Service | Port | Description | Database | Technology Stack |
|---------|------|-------------|----------|------------------|
| **Review Service** | 8093 | Product reviews and ratings | MySQL | Spring Boot, Spring Data JPA |
| **File Service** | 8094 | File upload and management | Object Storage | Spring Boot, AWS S3/MinIO |

## 🛠️ Technology Stack

### Backend
- **Java 21+**
- **Spring Boot 3.x**
- **Spring Cloud 2023.x**
- **Spring Security**
- **Spring Data JPA**

### Databases
- **PostgreSQL** - User, Order, Payment services
- **MySQL** - Product, Inventory, Review services
- **Redis** - Caching, Cart, Chat sessions
- **MongoDB** - Notifications
- **Elasticsearch** - Product search

### AI
- **TBD** - Chat service

### Message Brokers
- **RabbitMQ** - Asynchronous communication

### Infrastructure
- **Docker** - Containerization
- **Kubernetes** - Orchestration
- **Maven** - Build tool

## 📋 Prerequisites

- Java 24 or higher
- Docker and Docker Compose
- Maven 3.8+
- Node.js 18+ (for Angular frontend)

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/loiane/ecommerce-spring-angular.git
cd ecommerce-spring-angular
```

### 2. Start Infrastructure Services

```bash
# Start databases and message brokers
docker-compose up -d postgres mysql redis mongodb elasticsearch rabbitmq

# Start Eureka Service Discovery
cd infrastructure/service-discovery
mvn spring-boot:run

# Start Config Server
cd ../config-server
mvn spring-boot:run

# Start API Gateway
cd ../api-gateway
mvn spring-boot:run
```

### 3. Start Core Services

```bash
# Start User Service
cd core-services/user-service
mvn spring-boot:run

# Start Product Service
cd ../product-service
mvn spring-boot:run

# Start other core services...
```

### 4. Start AI Services

```bash
# Start Chat Service
cd ../chat-service
mvn spring-boot:run
```

## 🐳 Docker Deployment

### Build All Services

```bash
# Build all services
mvn clean package -DskipTests

# Build Docker images
docker-compose build
```

### Run with Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

## ☸️ Kubernetes Deployment

```bash
# Apply Kubernetes configurations
kubectl apply -f kubernetes/

# Check pod status
kubectl get pods

# Access services
kubectl get services
```

## 🔧 Configuration

### Environment Variables

Create `.env` file in the root directory:

```env
# Database Configuration
POSTGRES_USER=ecommerce_user
POSTGRES_PASSWORD=your_password
MYSQL_ROOT_PASSWORD=your_mysql_password
REDIS_PASSWORD=your_redis_password

# AI Service Configuration
OPENAI_API_KEY=your_openai_api_key

# JWT Configuration
JWT_SECRET=your_jwt_secret_key

# Email Configuration
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your_email@gmail.com
SMTP_PASSWORD=your_email_password
```

### Service Configuration

Each service has its own `application.yml` configuration file. Update database connections, service URLs, and other settings as needed.

## 📖 API Documentation

### API Gateway Endpoints

- **Base URL**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

### Service Endpoints

| Service | Endpoint | Documentation |
|---------|----------|---------------|
| User Service | `/api/users/**` | `http://localhost:8081/swagger-ui.html` |
| Product Service | `/api/products/**` | `http://localhost:8082/swagger-ui.html` |
| Order Service | `/api/orders/**` | `http://localhost:8083/swagger-ui.html` |
| Payment Service | `/api/payments/**` | `http://localhost:8084/swagger-ui.html` |
| Cart Service | `/api/cart/**` | `http://localhost:8085/swagger-ui.html` |
| Recommendation Service | `/api/recommendations/**` | `http://localhost:8091/swagger-ui.html` |
| Chat Service | `/api/chat/**` | `http://localhost:8092/swagger-ui.html` |

## 🤖 AI Features

### Recommendation Service

- **Collaborative Filtering**: "Users who bought this also bought..."
- **Content-Based Filtering**: Similar products based on attributes
- **Real-time Recommendations**: On product pages and cart
- **Personalized Suggestions**: Based on user history

**Example API Call:**
```bash
GET /api/recommendations/user/123/products
GET /api/recommendations/product/456/similar
```

### Chat Service

- **24/7 AI Support**: Powered by OpenAI GPT-4
- **Order Assistance**: Track orders, answer questions
- **Product Help**: Product recommendations and information
- **Human Escalation**: Transfer to human agents when needed

**WebSocket Connection:**
```javascript
const socket = new WebSocket('ws://localhost:8092/chat');
```

## 🧪 Testing

### Unit Tests

```bash
# Run tests for all services
mvn test

# Run tests for specific service
cd core-services/user-service
mvn test
```

### Integration Tests

```bash
# Run integration tests
mvn verify -P integration-tests
```

### Load Testing

```bash
# Using Apache Bench
ab -n 1000 -c 10 http://localhost:8080/api/products

# Using JMeter
jmeter -n -t load-tests/ecommerce-load-test.jmx
```

## 📊 Monitoring

### Application Monitoring

- **Spring Boot Actuator**: Health checks and metrics
- **Prometheus**: Metrics collection
- **Grafana**: Dashboards and visualization

### Service Discovery

- **Eureka Dashboard**: `http://localhost:8761`

### Logging

- **Centralized Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **Distributed Tracing**: Spring Cloud Sleuth + Zipkin

## 🔒 Security

### Authentication & Authorization

- **JWT Tokens**: Stateless authentication
- **Spring Security**: Role-based access control
- **OAuth2**: Third-party authentication support

### API Security

- **Rate Limiting**: Implemented at API Gateway
- **CORS Configuration**: Cross-origin resource sharing
- **Input Validation**: Data sanitization and validation

## 🚀 Performance Optimization

### Caching Strategy

- **Redis**: Session data, cart data, recommendations
- **Application Level**: Cacheable service methods
- **Database**: Query optimization and indexing

### Load Balancing

- **API Gateway**: Request distribution
- **Database**: Read replicas for scaling
- **Horizontal Scaling**: Multiple service instances

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow

```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Run tests
        run: mvn test
```

## 📂 Project Structure

```
ecommerce-spring-angular/
├── infrastructure/
│   ├── api-gateway/
│   ├── service-discovery/
│   ├── config-server/
│   └── circuit-breaker/
├── core-services/
│   ├── user-service/
│   ├── product-service/
│   ├── order-service/
│   ├── payment-service/
│   ├── cart-service/
│   ├── inventory-service/
│   ├── notification-service/
│   └── shipping-service/
├── ai-services/
│   ├── recommendation-service/
│   └── chat-service/
├── supporting-services/
│   ├── review-service/
│   └── file-service/
├── shared-libraries/
├── frontend/
│   └── angular-app/
├── docker-compose.yml
├── kubernetes/
├── scripts/
└── README.md
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

- **Documentation**: [Wiki](https://github.com/loiane/ecommerce-spring-angular/wiki)
- **Issues**: [GitHub Issues](https://github.com/loiane/ecommerce-spring-angular/issues)
- **Discussions**: [GitHub Discussions](https://github.com/loiane/ecommerce-spring-angular/discussions)

## 🗺️ Roadmap

- [ ] Implement all core services
- [ ] Integrate chat service with AI
- [ ] Develop Angular frontend
- [ ] Add comprehensive testing
- [ ] Implement monitoring and logging
- [ ] Deploy to cloud platforms

---

**Happy Coding! 🚀**

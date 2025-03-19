# IBE-Backend Project

## Project Overview
This project implements a backend service for an Internet Booking Engine (IBE) system, with infrastructure defined as code for AWS deployment.

## Project Structure

```
IBE-Backend/
├── backend/             # Java Spring Boot application
│   ├── src/             # Source code
│   ├── Dockerfile       # Multi-stage Docker build
│   ├── pom.xml          # Maven dependencies
│   ├── .env             # Environment variables
│   └── mvnw, mvnw.cmd   # Maven wrapper scripts
│
└── iac/                 # Infrastructure as Code (Terraform)
    ├── main.tf          # Main Terraform configuration
    ├── variables.tf     # Input variables
    ├── outputs.tf       # Output variables
    ├── providers.tf     # AWS provider configuration
    ├── modules/         # Reusable Terraform modules
    │   ├── alb/         # Application Load Balancer module
    │   ├── api_gateway/ # API Gateway module
    │   ├── ecs/         # ECS module for container orchestration
    │   └── iam/         # IAM roles and permissions module
    ├── dev.tfvars       # Development environment variables
    └── qa.tfvars        # QA environment variables
```

## Backend Implementation

### Architecture
The backend follows a layered architecture with the following components:

- **Controllers**: Handle incoming HTTP requests
  - `HealthCheckController`: Provides health check endpoints
  - `HotelController`: Manages hotel-related operations
  - `GraphQLController`: Handles GraphQL queries

- **Services**: Implement business logic
  - Clear separation between interfaces and implementations

- **Models**: Define data structures
  - **Entities**: JPA entities for database persistence
    - `Hotel`: Hotel entity
    - `HotelTranslation`: Translations for hotel information
  - **DTOs**: Data Transfer Objects
  - **Response Objects**: Formatted API responses

- **Repositories**: Data access layer

- **Configuration**: Application configuration
  - Environment-specific properties files
  - GraphQL schema configuration

- **Exception Handling**: Custom exception handlers

### Technologies
- Java 21 with Spring Boot
- Maven for dependency management
- JPA/Hibernate for ORM
- GraphQL for flexible API queries
- REST API endpoints
- Docker containerization

## Infrastructure as Code

### AWS Services
The infrastructure is provisioned using Terraform with modular components:

- **ECS (Elastic Container Service)**: For running containerized applications
- **ALB (Application Load Balancer)**: For distributing traffic
- **API Gateway**: For API management and routing
- **IAM**: For identity and access management

### Deployment Environments
- Development environment (`dev.tfvars`)
- QA environment (`qa.tfvars`)

### Infrastructure Features
- Proper tagging strategy for resources
- Environment-specific configurations
- Modular design for reusability
- Secure IAM role configuration

## Build and Deployment

### Building the Application
```bash
# Navigate to backend directory
cd backend

# Build with Maven
./mvnw clean package

# Build Docker image
docker build -t ibe-backend .
```

### Deploying Infrastructure
```bash
# Navigate to iac directory
cd iac

# Initialize Terraform
terraform init

# Plan deployment (development environment)
terraform plan -var-file=dev.tfvars -out=plan

# Apply configuration
terraform apply "plan"
```

## Environment Configuration
The application supports different environments through:
- Environment-specific properties files
- Environment variables via `.env` file
- Terraform variable files for infrastructure 
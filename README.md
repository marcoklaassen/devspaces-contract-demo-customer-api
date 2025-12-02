# Customer API

A RESTful API for managing customers, built with Quarkus and Hibernate Panache.

## Overview

This API provides endpoints to retrieve customer information from a PostgreSQL database. It uses Quarkus framework with Hibernate ORM Panache for database operations.

## Features

- Retrieve all customers
- Get a customer by ID
- Search customers by name
- Create new customers
- RESTful JSON API
- PostgreSQL database integration

## Prerequisites

- **Java 21** or higher
- **Maven 3.8+** (or use the included `mvnw` wrapper)
- **PostgreSQL** database (running locally or accessible)

## API Endpoints

### Get All Customers
Retrieves a list of all customers.

**Request:**
```http
GET /customer
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Marco"
  },
  {
    "id": 2,
    "name": "John"
  }
]
```

### Get Customer by ID
Retrieves a specific customer by their ID.

**Request:**
```http
GET /customer/{id}
```

**Example:**
```http
GET /customer/1
```

**Response:**
```json
{
  "id": 1,
  "name": "Marco"
}
```

### Search Customers by Name
Retrieves all customers matching the provided name.

**Request:**
```http
GET /customer/name?name={name}
```

**Example:**
```http
GET /customer/name?name=Marco
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Marco"
  }
]
```

### Create Customer
Creates a new customer.

**Request:**
```http
POST /customer
Content-Type: application/json
```

**Example:**
```http
POST /customer
Content-Type: application/json

{
  "name": "Jane Doe"
}
```

**Response:**
```json
{
  "id": 3,
  "name": "Jane Doe"
}
```

**Status Code:** `201 Created`

## Running Locally

### 1. Database Setup

Ensure PostgreSQL is running and create a database:

```sql
CREATE DATABASE customerdb;
```

### 2. Configure Database Connection

Create or update `src/main/resources/application.properties` with your database configuration:

```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=your_username
quarkus.datasource.password=your_password
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/customerdb

quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.hibernate-orm.sql-load-script=import.sql
```

### 3. Start the Application

#### Development Mode (Recommended)

Run the application in development mode with hot reload:

```bash
./mvnw quarkus:dev
```

The API will be available at: `http://localhost:8080`

> **Note:** Quarkus Dev UI is available in dev mode at `http://localhost:8080/q/dev/`

#### Production Mode

1. Build the application:
```bash
./mvnw package
```

2. Run the application:
```bash
java -jar target/quarkus-app/quarkus-run.jar
```

Or build an über-jar:
```bash
./mvnw package -Dquarkus.package.jar.type=uber-jar
java -jar target/customer-api-1.0.0-SNAPSHOT-runner.jar
```

## Testing the API

Once the application is running, you can test the endpoints using `curl`:

```bash
# Get all customers
curl http://localhost:8080/customer

# Get customer by ID
curl http://localhost:8080/customer/1

# Search customers by name
curl http://localhost:8080/customer/name?name=Marco

# Create a new customer
curl -X POST http://localhost:8080/customer \
  -H "Content-Type: application/json" \
  -d '{"name": "Jane Doe"}'
```

Or use any HTTP client like Postman, Insomnia, or your browser.

## Project Structure

```
src/
├── main/
│   ├── java/click/klaassen/customer/
│   │   ├── Customer.java          # Customer entity
│   │   ├── CustomerRepository.java # Repository with database operations
│   │   └── CustomerResource.java   # REST endpoints
│   └── resources/
│       ├── application.properties  # Application configuration
│       └── import.sql              # Initial data (optional)
└── test/
    └── java/                       # Test files
```

## Technology Stack

- **Quarkus 3.30.1** - Supersonic Subatomic Java Framework
- **Hibernate ORM Panache** - Simplifies database operations
- **PostgreSQL** - Relational database
- **Jakarta REST** - RESTful web services
- **Jackson** - JSON processing

## Building a Native Executable

You can create a native executable for better performance:

```bash
./mvnw package -Dnative
```

Or using a container (if GraalVM is not installed):

```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

The native executable will be available at: `./target/customer-api-1.0.0-SNAPSHOT-runner`

## Learn More

- [Quarkus Documentation](https://quarkus.io/)
- [Hibernate Panache Guide](https://quarkus.io/guides/hibernate-orm-panache)
- [RESTEasy Guide](https://quarkus.io/guides/resteasy-reactive)

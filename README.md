Samajh gaya — usi stylish format (badges, emojis, sections) me, lekin content sirf humare **Product Management API** assignment ka. Yeh raha:

```markdown
# 📦 Product Management API — Zest India Backend Assignment

<p align="center">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring_Boot-4.1.1-6DB33F?logo=springboot&logoColor=white&style=flat-square" />
  <img alt="Java" src="https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white&style=flat-square" />
  <img alt="PostgreSQL" src="https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white&style=flat-square" />
  <img alt="Spring Security" src="https://img.shields.io/badge/Spring_Security-JWT-6DB33F?logo=springsecurity&logoColor=white&style=flat-square" />
  <img alt="Swagger" src="https://img.shields.io/badge/API_Docs-Swagger-85EA2D?logo=swagger&logoColor=black&style=flat-square" />
  <img alt="Docker" src="https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white&style=flat-square" />
  <img alt="JUnit" src="https://img.shields.io/badge/Tests-JUnit_5_%2B_Mockito-25A162?logo=junit5&logoColor=white&style=flat-square" />
</p>

A RESTful **Product Management API** built with **Spring Boot**, featuring full CRUD operations, JWT authentication with refresh token rotation, role-based authorization, and Docker support — built as part of the **Zest India Java Backend Developer Assignment**.

---

## ✨ Features

- 🔧 **Full CRUD** — Product endpoints with nested Items (One-to-Many relationship)
- 🔐 **JWT Authentication** — access + refresh token, with rotation on every refresh
- 👥 **Role-Based Authorization** — `ADMIN` vs `USER` access control
- 🔢 **API Versioning** — clean `/api/v1/` prefix
- 📄 **Pagination** — on all list endpoints
- ⚠️ **Centralized Exception Handling** — consistent, structured JSON error responses
- ✅ **Input Validation** — Jakarta Bean Validation on all request bodies
- ⚡ **Database Indexing** — on frequently queried foreign keys and unique fields
- 🌐 **CORS Configuration** — ready for frontend integration
- 📘 **Swagger / OpenAPI Docs** — fully interactive API explorer
- 🧪 **Full Test Suite** — Unit, Controller, and Integration tests (JUnit 5, Mockito, H2)
- 🐳 **Dockerized** — one command spins up the app + PostgreSQL

---

## 📦 Tech Stack

| Layer | Technologies |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot 4.1.1 |
| **Database** | PostgreSQL (H2 for tests) |
| **ORM** | Spring Data JPA / Hibernate |
| **Security** | Spring Security + JWT (`jjwt`) |
| **API Docs** | Springdoc OpenAPI (Swagger UI) |
| **Testing** | JUnit 5, Mockito, Spring Boot Test |
| **Build Tool** | Maven |
| **Containerization** | Docker, Docker Compose |

---

## 🏗️ Architecture

```
Controller → Service → Repository → Database
```

Clean layered architecture with DTOs decoupling API contracts from entity models, centralized exception handling via `@RestControllerAdvice`, and stateless JWT authentication via a custom `OncePerRequestFilter`.

---

## 📁 Project Structure

```
com.zestindia.productapi
├── controller     → REST endpoints
├── service        → business logic
├── repository     → Spring Data JPA repositories
├── entity         → JPA entities
├── dto            → request/response objects
├── exception      → custom exceptions + global handler
├── security       → JWT utility, filter, user details service
└── config         → Security, CORS, Swagger configuration
```

---

## 🗄️ Database Structure

**Product** (1) ──── (*) **Item**

| Product | Item |
|---|---|
| id | id |
| product_name | product_id (FK) |
| created_by | quantity |
| created_on | |
| modified_by | |
| modified_on | |

Also includes `users` and `refresh_tokens` tables for authentication.

---

## 🔌 API Endpoints

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/api/v1/auth/login` | Login, returns access + refresh token | Public |
| `POST` | `/api/v1/auth/refresh` | Rotate refresh token, get new access token | Public |
| `GET` | `/api/v1/products` | List products (paginated) | USER, ADMIN |
| `GET` | `/api/v1/products/{id}` | Get product by ID | USER, ADMIN |
| `POST` | `/api/v1/products` | Create product | ADMIN |
| `PUT` | `/api/v1/products/{id}` | Update product | ADMIN |
| `DELETE` | `/api/v1/products/{id}` | Delete product | ADMIN |
| `GET` | `/api/v1/products/{id}/items` | Get items for a product | USER, ADMIN |

---

## 🔐 Authentication

All product endpoints (except Swagger and auth endpoints) require a JWT Bearer token:

```
Authorization: Bearer <access_token>
```

Access tokens expire in 15 minutes; refresh tokens are valid for 7 days and **rotate on every use** — the old refresh token is revoked as soon as a new one is issued.

---

## 🏃 Getting Started (Local Development)

### Prerequisites
- Java 17+
- Maven
- PostgreSQL running locally

### 1. Clone the repository

```bash
git clone https://github.com/kaifmulla3335/zest-backend-assessment.git
cd zest-backend-assessment
```

### 2. Configure the Database

Create a PostgreSQL database:
```sql
CREATE DATABASE product_management_db;
```

Update `src/main/resources/application.properties` with your local PostgreSQL credentials (or export environment variables — see [Environment Variables](#-environment-variables) below).

### 3. Run the Application

```bash
mvn spring-boot:run
```

Runs at: `http://localhost:8080`

> **Note:** User registration is not exposed via API in this version (out of scope for this assignment). A test user can be inserted directly via SQL with a BCrypt-hashed password (see Future Improvements for a planned `/register` endpoint).

---

## 📘 Swagger Documentation

Once running, visit:
```
http://localhost:8080/swagger-ui/index.html
```

---

## 🧪 Running Tests

```bash
mvn test
```

Includes:
- ✅ Unit tests for the service layer (Mockito)
- ✅ Controller tests using `@WebMvcTest` and `MockMvc`
- ✅ Integration tests using `@SpringBootTest` with an in-memory H2 database

---

## 🐳 Docker Setup

### Build and run

```bash
docker compose up --build
```

This starts two containers:
- `product-api-app` — the Spring Boot application (port `8080`)
- `product-api-db` — PostgreSQL database (port `5432`)

### Stop

```bash
docker compose down
```

---

## ⚙️ Environment Variables

| Variable | Description | Default |
|---|---|---|
| `SPRING_DATASOURCE_URL` | Database JDBC URL | `jdbc:postgresql://localhost:5432/product_management_db` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | local dev only |
| `JWT_SECRET` | Secret key for signing JWTs | local dev only, must be changed in production |

---

## 📨 Sample Requests

**Login**
```json
POST /api/v1/auth/login
{
  "username": "admin",
  "password": "Admin@123"
}
```

**Create Product**
```json
POST /api/v1/products
Authorization: Bearer <token>

{
  "productName": "Laptop",
  "createdBy": "admin"
}
```

---

## 🗺️ Future Improvements

- [ ] Expose a `/register` endpoint for user sign-up
- [ ] Replace `ddl-auto=update` with Flyway/Liquibase migrations for production-grade schema versioning
- [ ] Enforce HTTPS at the reverse proxy/load balancer level (e.g., Nginx with Let's Encrypt) in production
- [ ] Add rate limiting on authentication endpoints
- [ ] Async processing was evaluated but determined unnecessary for this CRUD-focused application — all operations are lightweight synchronous DB calls

---

## 📄 License

Built as part of the **Zest India Java Backend Developer Assignment** by [Mohammadkaif Mulla](https://github.com/kaifmulla3335)
```

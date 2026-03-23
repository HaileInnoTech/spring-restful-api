# 🚀 Spring RESTful API

A production-ready RESTful API built with **Spring Boot 4**, featuring JWT authentication, OAuth2 Google login, role-based access control, and full CRUD for a blog-like domain (Posts, Comments, Tags, Users).

---

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Configuration](#configuration)
  - [Run Locally](#run-locally)
  - [Run with Docker](#run-with-docker)
- [API Documentation](#api-documentation)
- [API Endpoints](#api-endpoints)
- [Authentication Flow](#authentication-flow)
- [Environment Variables](#environment-variables)

---

## ✨ Features

- 🔐 **JWT Authentication** — Access token + Refresh token with HttpOnly cookie support
- 🌐 **OAuth2 Google Login** — Social login via Google OAuth2
- 👥 **Role-Based Access Control** — Method-level security with `@EnableMethodSecurity`
- 📝 **Blog Domain** — Full CRUD for Posts, Comments, Tags, Users, Roles
- 🔍 **JPA Specifications** — Advanced filtering/search for Posts and Users
- 📖 **Swagger / OpenAPI 3** — Auto-generated interactive API docs
- 🐳 **Docker Support** — Multi-stage Dockerfile + Docker Compose
- 📊 **Spring Actuator** — Health check and monitoring endpoints
- 📁 **Logback** — Structured file logging with rolling policy
- ⚙️ **Multi-profile** — Separate `dev` and `prod` configuration profiles

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Framework | Spring Boot 4.0.2 |
| Language | Java 17 |
| Security | Spring Security, JWT (HS256), OAuth2 |
| Database | MySQL + Spring Data JPA / Hibernate |
| Documentation | SpringDoc OpenAPI 3 (Swagger UI) |
| Build Tool | Maven |
| Containerization | Docker, Docker Compose |
| Utilities | Lombok, Spring Validation, Spring Actuator |

---

## 📁 Project Structure

```
src/main/java/com/haile/springrestfulapi/
├── config/                   # Security, JWT, OAuth2, OpenAPI config
│   ├── SecurityConfig.java
│   ├── JwtService.java
│   ├── CustomUserDetailsService.java
│   ├── CustomOAuth2UserService.java
│   ├── OAuth2SuccessHandler.java
│   └── OpenAPIDocConfig.java
├── controller/               # REST controllers
│   ├── AuthController.java
│   ├── UserController.java
│   ├── PostController.java
│   ├── CommentController.java
│   ├── TagController.java
│   └── RoleController.java
├── entity/                   # JPA entities + DTOs
│   ├── dto/request/
│   └── dto/response/
├── repository/               # Spring Data JPA repositories
├── service/                  # Business logic
│   └── specification/        # JPA Specifications for dynamic queries
├── helper/                   # ApiResponse wrapper, SecurityUtil, custom exceptions
└── GlobalExceptionHandler.java
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- MySQL 8+
- Docker & Docker Compose *(optional)*

### Configuration

Copy the example config and fill in your values:

```bash
cp src/main/resources/application.yaml.example src/main/resources/application-dev.yaml
```

Edit `application-dev.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/spring_restful_api
    username: your_db_username
    password: your_db_password

  security:
    oauth2:
      client:
        registration:
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID
            client-secret: YOUR_GOOGLE_CLIENT_SECRET

custom:
  jwt:
    access-token:
      base64-secret: YOUR_BASE64_SECRET
      validity-in-seconds: 86400
    refresh-token:
      validity-in-seconds: 8640000
```

> ⚠️ Never commit real credentials. Use environment variables or a secrets manager in production.

### Run Locally

```bash
# Clone the repository
git clone https://github.com/your-username/spring-restful-api.git
cd spring-restful-api

# Build and run
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080`.

### Run with Docker

```bash
# Build and start the container
docker compose up --build

# Run in background
docker compose up -d --build
```

> 💡 Make sure to set environment variables (`GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`) before running Docker.

---

## 📖 API Documentation

Once the application is running, access the interactive Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON spec:

```
http://localhost:8080/v3/api-docs
```

---

## 🔌 API Endpoints

### 🔐 Auth

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/auth/register` | Register new user | No |
| `POST` | `/auth/login` | Login with email/password | No |
| `POST` | `/auth/refresh` | Exchange refresh token | No |
| `POST` | `/auth/refresh-with-cookie` | Refresh via HttpOnly cookie | No |
| `GET` | `/auth/account` | Get current user info | Yes |
| `POST` | `/auth/logout` | Logout and clear cookie | Yes |

### 👤 Users

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `GET` | `/users` | List all users | `ROLE_user` |
| `GET` | `/users/{id}` | Get user by ID | `ROLE_user` |
| `POST` | `/users` | Create user | `ROLE_user` |
| `PUT` | `/users/{id}` | Update user | `ROLE_user` |
| `DELETE` | `/users/{id}` | Delete user | `ROLE_user` |

### 📝 Posts, Comments, Tags, Roles

Full CRUD endpoints available — see Swagger UI for the complete list.

---

## 🔒 Authentication Flow

```
1. POST /auth/login  →  Returns access_token + refresh_token (in HttpOnly cookie)
2. Use access_token in Authorization header:  Bearer <access_token>
3. When access_token expires → POST /auth/refresh-with-cookie  →  New tokens issued
4. POST /auth/logout  →  Refresh token invalidated, cookie cleared
```

**Google OAuth2 Flow:**

```
1. Redirect user to /oauth2/authorization/google
2. Google authenticates and redirects back
3. OAuth2SuccessHandler issues JWT tokens automatically
```

---

## 🌍 Environment Variables

| Variable | Description |
|---|---|
| `GOOGLE_CLIENT_ID` | Google OAuth2 client ID |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret |

JWT secrets and DB credentials should be configured in `application-dev.yaml` or `application-prod.yaml` depending on the active profile.

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

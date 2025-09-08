# Kanban API

A simple Kanban board REST API built with Spring Boot, supporting JWT authentication, PostgreSQL persistence, and real-time task updates via WebSocket/STOMP.

## Quick Introduction

This project provides a backend API for managing Kanban tasks, including authentication, CRUD operations, and real-time updates. It is designed for easy local development and production deployment using Docker.

## Quick Setup Guide

1. **Clone the repository:**
   ```sh
   git clone https://github.com/your-org/kanban-api.git
   cd kanban-api
   ```

2. **Start the application with Docker Compose:**
   ```sh
   sudo docker compose up --build
   ```

   This will start both the PostgreSQL database and the Kanban API server.


3. **Authentication:**
      - Each `/api` endpoint requires a Bearer token for authentication.
      - Obtain a token by sending a POST request to `http://localhost:8080/auth` with the following JSON body:
         ```json
         {
            "username": "postman",
            "password": "randompassword"
         }
         ```
      - The response will contain a JWT token. Use this token in the `Authorization` header for all subsequent API requests:
         ```
         Authorization: Bearer <your_token_here>
         ```

4. **Access the API:**
   - Tasks API base URL: `http://localhost:8080/api/tasks`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

## Used Technologies

- **Spring Boot**: Main framework for building the REST API and application configuration.
- **Spring Data JPA**: For ORM and database access.
- **Spring Security**: Provides authentication and authorization, including JWT-based stateless security.
- **PostgreSQL**: Relational database for persistent storage.
- **Docker & Docker Compose**: Containerization and orchestration for local and production environments.
- **WebSocket (Spring WebSocket, STOMP, SockJS)**: Enables real-time updates for Kanban tasks.
- **JUnit 5 & Mockito**: Unit and integration testing.
- **Testcontainers**: For running integration tests with real PostgreSQL containers.
- **Swagger (springdoc-openapi)**: API documentation and interactive UI.
- **Spring Cache**: Caching for improved performance on frequently accessed endpoints.
- **Micrometer & Prometheus**: Application metrics and monitoring.
- **GitHub Actions**: Continuous Integration pipeline for testing and building    

## Checklist

| Category      | Feature                                                                              |   |
|:-------------:|--------------------------------------------------------------------------------------|:-:|
| Endpoints | GET /api/tasks                                                                       | ✅ |
|  | GET /api/tasks/{id}                                                                  | ✅ |
|  | POST /api/tasks/{id}                                                                 | ✅ |
|  | PUT /api/tasks/{id} (Optimistic locking)                                             | ✅ |
|  | PATCH /api/tasks/{id} (JSON Merge Patch)                                             | ✅ |
|  | DELETE /api/tasks/{id}                                                               | ✅ |
|  | HATEOAS                                                                              | ❌ |
| WebSocket | Spring WebSocket                                                                     | ✅ |
|  | STOMP                                                                                | ✅ |
|  | SockJS fallback                                                                      | ✅ |
| Persistency | PostgreSQL (Docker Compose)                                                          | ✅ |
|  | Spring Data JPA                                                                      | ✅ |
|  | Hibernate                                                                            | ✅ |
|  | Flyway/Liquibase                                                                     | ❌ |
| OpenAPI 3 (springdoc-openapi) | Swagger UI on `/swagger-ui.html`                                                     | ✅ |
| JWT | Simple stateless JWT auth                                                            | ✅ |
| | JWT filter                                                                           | ✅ |
| | BCrypt                                                                               | ✅ |
|  | protected endpoints `/api/**`                                                        | ✅ |
|  | everything else public: `/swagger-ui.*` and `/v3/api-docs/**`                        | ✅ |
| Validation | Bean Validation                                                                      | ✅ |
| Tests | Unit tests with JUnit5 and Mockito                                                   | ✅  |
|  | Integration tests with @SpringBootTest + Testcontainers (PostgreSQL)                 | ✅  |
|  | WebScoket tests with StompClient (confirm after POST broadcast)                      | ✅ |
| Docker | Multistage dockerfile                                                                | ✅ |
|  | Multi-stage dockerfile                                                               | ✅ |
|  | docker-compose.yml file (app + db)                                                   | ✅ |
| Spring Boot Actuator | `/actuator/health`                                                                   | ✅ |
|  | `/actuator/prometheus`                                                               | ✅ |
| Performance | GET /api/tasks?page=0&size=50 ≤ 150 ==result==> 98 ms on first run on local hardware | ✅ |

### Performance overview

- `GET /api/tasks?page=0&size=50`

| Iteration | Time (ms) |
|:---------:|-----------|
|     1     | 98        |
|     2     | 18        |
|     3     | 15        |
|     4     | 15        |
|     5     | 11        |
|     6     | 12        |
|     7     | 12        |
|     8     | 17        |
|     9     | 16        |
|    10     | 9         |

### Extra

| Category      | Feature                                                                   |   |
|:-------------:|---------------------------------------------------------------------------|:-:|
| GraphQL |                                                                           | ❌ |
| Caching | Implemented cache for GET methods                                         | ✅ |
| CI pipeline | GitHub Actions: mvn clean verify → test check ≥ 80 % → build Dockerimage. | ✅ |
| Rate limiting |                                                                           | ❌ |
| Public deploy | Linode VPS; nginx reverse proxy; CloudFlare DNS                           | ✅ |



# Task Manager API

A production-ready REST API for task and project management, built with Spring Boot. Demonstrates clean layered architecture, JWT-based authentication, JPA entity relationships, and comprehensive test coverage.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 4.x |
| Language | Java 17 |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate 7 |
| Security | Spring Security + JWT |
| Testing | JUnit 5 + Mockito + MockMvc |
| Build | Maven |

## Features

- **JWT Authentication** — stateless auth with access tokens, BCrypt password hashing
- **Role-based access** — ADMIN and MEMBER roles enforced at the API level
- **Full CRUD** — Users, Projects, Tasks, Comments with proper HTTP semantics
- **Entity relationships** — @ManyToOne with foreign key constraints, lazy loading
- **Input validation** — @Valid with field-level error messages
- **Global exception handling** — consistent error responses with proper HTTP status codes
- **19 automated tests** — unit, repository, and integration tests

## Architecture

Controller → Service → Repository → PostgreSQL
- **Controller** — HTTP routing, request/response mapping
- **Service** — business logic, transaction management
- **Repository** — Spring Data JPA, custom JPQL queries
- **DTOs** — clean separation between API contract and database schema

## API Endpoints

### Auth (public)

POST /api/auth/register    — create account, returns JWT token
POST /api/auth/login       — login, returns JWT token

### Users (requires JWT token)
GET    /api/users              — get all users
GET    /api/users/{id}         — get user by id
GET    /api/users/search       — get user by email
POST   /api/users              — create user
PUT    /api/users/{id}         — update user
DELETE /api/users/{id}         — delete user

### Projects (requires JWT token)
GET    /api/projects                      — get all projects
GET    /api/projects/{id}                 — get project by id
GET    /api/projects/owner/{ownerId}      — get projects by owner
GET    /api/projects/status/{status}      — get projects by status
POST   /api/projects                      — create project
PUT    /api/projects/{id}                 — update project
PATCH  /api/projects/{id}/status          — update status only
DELETE /api/projects/{id}                 — delete project

### Tasks (requires JWT token)
GET    /api/tasks                                      — get all tasks
GET    /api/tasks/{id}                                 — get task by id
GET    /api/tasks/project/{projectId}                  — tasks by project
GET    /api/tasks/assignee/{assigneeId}                — tasks by assignee
GET    /api/tasks/project/{projectId}/status/{status}  — filter by status
POST   /api/tasks                                      — create task
PUT    /api/tasks/{id}                                 — update task
PATCH  /api/tasks/{id}/status                          — update status only
PATCH  /api/tasks/{id}/assign/{userId}                 — assign task to user
DELETE /api/tasks/{id}                                 — delete task

## Project Structure
src/main/java/com/thinkalike/taskmanager/
├── controller/        — HTTP layer, request/response handling
│   ├── AuthController.java
│   ├── UserController.java
│   ├── ProjectController.java
│   └── TaskController.java
├── service/           — business logic
│   ├── impl/          — service implementations
│   ├── AuthService.java
│   ├── UserService.java
│   ├── ProjectService.java
│   └── TaskService.java
├── repository/        — Spring Data JPA interfaces
│   ├── UserRepository.java
│   ├── ProjectRepository.java
│   ├── TaskRepository.java
│   └── CommentRepository.java
├── model/             — JPA entities
│   ├── User.java      — with Role enum (ADMIN, MEMBER)
│   ├── Project.java   — with ProjectStatus enum
│   ├── Task.java      — with TaskStatus and Priority enums
│   └── Comment.java
├── dto/               — request/response objects
├── security/          — JWT filter, utility, security config
└── exception/         — global exception handler

## Database Schema
users
└── owns many → projects  (owner_id)
└── assigned many → tasks  (assignee_id)
└── created many → tasks   (created_by_id)
└── wrote many → comments  (author_id)
projects
└── contains many → tasks  (project_id)
tasks
└── has many → comments    (task_id)

## Running Locally

**Prerequisites:** Java 17, PostgreSQL

**1. Clone the repository**
```bash
git clone https://github.com/TejasJadhav2799/taskmanager.git
cd taskmanager
```

**2. Create PostgreSQL database**
```sql
CREATE DATABASE taskmanagement;
```

**3. Configure database credentials**

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

**4. Run the application**
```bash
./mvnw spring-boot:run
```

App starts at `http://localhost:8081`

## Running Tests

Tests use H2 in-memory database — no PostgreSQL required.

```bash
./mvnw test
```
UserServiceImplTest       8 tests — unit tests with Mockito
UserRepositoryTest        4 tests — repository query tests with H2
UserControllerTest        6 tests — integration tests with MockMvc
TaskmanagerApplicationTests  1 test  — context loads correctly
─────────────────────────────────────────
Total: 19 tests, 0 failures

## Testing the API

**Step 1 — Register:**
```json
POST /api/auth/register
Content-Type: application/json

{
    "name": "Your Name",
    "email": "you@example.com",
    "password": "password123"
}
```

**Step 2 — Copy the token from the response:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "email": "you@example.com",
    "name": "Your Name",
    "role": "MEMBER"
}
```

**Step 3 — Include token in all subsequent requests:**

Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

## Roadmap

- [x] Phase 1 — Layered architecture, JPA entities, PostgreSQL, full CRUD
- [x] Phase 2 — Spring Security, JWT authentication, BCrypt password hashing
- [x] Phase 3 — JUnit 5 unit tests, repository tests, MockMvc integration tests
- [x] Phase 4 — Docker + Docker Compose
- [x] Phase 5 — Kafka async events (task assignment notifications)
- [x] Phase 6 — Redis caching
- [ ] Phase 7 — Prometheus + Grafana observability
- [ ] Phase 8 — GitHub Actions CI/CD pipeline

## Author

Tejas Jadhav — [GitHub](https://github.com/TejasJadhav2799)
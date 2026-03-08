# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./mvnw clean install

# Run application
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=PjiApplicationTests

# Package without tests
./mvnw clean package -DskipTests
```

The server starts on port **8085**. All API routes are prefixed with `/api/v1`.

## Architecture

Spring Boot 3.5.11 / Java 17 REST API for a clinical decision support system (PJI - Periprosthetic Joint Infection).

### Package structure (`com.vietnam.pji`)

| Package | Purpose |
|---|---|
| `controller/` | REST controllers — mostly stubs currently, implementation pending |
| `model/` | JPA entities |
| `dto/response/` | Response DTOs (`ResponseData<T>` generic wrapper, `ResLoginDTO`) |
| `exception/` | `GlobalExceptionHandler` + custom exceptions (`ResourceNotFoundException`, `InvalidDataException`) |
| `config/` | `DatabaseInitializer` seeds DB on startup (permissions, ADMIN role, default user) |
| `utils/` | `SecurityUtils` — JWT generation/validation, current-user extraction |
| `constant/` | Enums: `GenderEnum`, `UserStatus` |

> **Note:** `DatabaseInitializer` lives in package `com.vietnam.pji.copnfig` (typo — missing `n`) rather than `config`. Repositories (`RoleRepository`, `UserRepository`, `PermissionRepository`) are referenced but not yet created.

### Data model

All entities extend `AbstractEntity` (auto-managed `id`, `createdAt`, `updatedAt`).

- **User** — has one `Role`, stores `refreshToken` for token rotation, `UserStatus` enum
- **Role** — has many `Permission` (ManyToMany via `role_permission` join table)
- **Permission** — defines API access by `apiPath`, `method`, `module`; auto-sets `createdBy`/`updatedBy` via JPA lifecycle hooks using `SecurityUtils.getCurrentUserLogin()`
- **Patient** — does **not** extend `AbstractEntity` (no `id`/timestamps yet; needs fixing)

### Auth & Security

- OAuth2 Resource Server with JWT (HS512, secret in `secure.jwt.base64-secret`)
- Access token: 86400s (1 day), Refresh token: 864000s (10 days)
- `SecurityUtils` handles token generation and extraction of the current authenticated user from `SecurityContextHolder`
- `ResLoginDTO` carries both the JWT and user data; nested `InfoWithinToken` is embedded in JWT claims under key `"user account"`

### Infrastructure dependencies

- **PostgreSQL** — `pji_database` on localhost:5432 (override with `SPRING_DATASOURCE_URL`)
- **Redis** — via `spring-data-redis` + Jedis client (configured but no Redis config class yet)
- **Ollama** — Spring AI integration for local LLM inference (`spring-ai-starter-model-ollama`)
- **Flyway** — migrations in `classpath:/db/migration` and `classpath:/dev/db/migration`; baseline version 0

### Response conventions

- Success responses use `ResponseData<T>(status, message, data)` for GET/POST
- `ResponseData(status, message)` (no data) for PUT/DELETE
- Errors use `ErrorResponse` with `timestamp`, `status`, `path`, `error`, `message` fields
- `GlobalExceptionHandler` maps: validation → 400, `ResourceNotFoundException` → 404, `InvalidDataException` → 409, generic `Exception` → 500

### Filtering

`com.turkraft.springfilter:jpa:3.1.7` is available for dynamic JPA query filtering via request parameters.

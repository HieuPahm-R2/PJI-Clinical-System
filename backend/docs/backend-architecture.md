# Backend Architecture

> PJI Clinical Decision Support System — Spring Boot 3 + PostgreSQL + Redis

## Tech Stack

| Layer | Technology | Purpose |
|-------|------------|---------|
| **Framework** | Spring Boot 3.5 | Application framework |
| **Language** | Java 17 | Core language |
| **Database** | PostgreSQL | Primary data store |
| **Cache** | Redis (Jedis) | Caching, session management |
| **ORM** | Spring Data JPA + Hibernate | Database access |
| **Security** | Spring Security + OAuth2 + JWT | Authentication & authorization |
| **Messaging** | RabbitMQ | Async processing, AI recommendations |
| **Storage** | MinIO | File/image storage |
| **Mapping** | MapStruct | DTO ↔ Entity conversion |
| **Validation** | Jakarta Validation | Request validation |
| **API Docs** | SpringDoc OpenAPI | Swagger documentation |
| **Migrations** | Flyway | Database versioning |

---

## Package Structure

```
src/main/java/com/vietnam/pji/
├── PjiApplication.java              # Main entry point
│
├── config/                          # Configuration classes
│   ├── auth/                        # Security configuration
│   │   ├── WebSecurityConfiguration.java    # Security filter chain
│   │   ├── JwtConfiguration.java            # JWT encoder/decoder
│   │   ├── AuthEntryPointConfig.java        # 401 handler
│   │   ├── AuthorityIntercepter.java        # Permission checking
│   │   ├── CorsConfigure.java               # CORS settings
│   │   ├── InterceptorConfiguration.java    # Interceptor registration
│   │   └── UserDetailsConfig.java           # UserDetailsService
│   │
│   ├── integration/                 # External service configs
│   │   ├── AiServiceClientConfig.java       # AI service HTTP client
│   │   ├── MinioConfiguration.java          # MinIO client
│   │   ├── RedisCacheConfig.java            # Redis cache manager
│   │   └── OpenAPIConfig.java               # Swagger config
│   │
│   └── properties/                  # Custom properties
│       └── MinioProperties.java
│
├── constant/                        # Enums and constants
│   ├── GenderEnum.java
│   ├── EpisodeResult.java
│   ├── RunStatus.java
│   ├── ChatType.java
│   ├── ItemCategory.java
│   ├── SourceType.java
│   └── TriggerType.java
│
├── controller/                      # REST controllers
│   ├── auth/                        # Auth & admin endpoints
│   │   ├── AuthController.java
│   │   ├── UserController.java
│   │   ├── RoleController.java
│   │   └── PermissionController.java
│   │
│   ├── medical/                     # Medical domain endpoints
│   │   ├── PatientController.java
│   │   ├── EpisodeController.java
│   │   ├── ClinicalRecordController.java
│   │   ├── LabResultController.java
│   │   ├── CultureResultController.java
│   │   ├── SensitivityResultController.java
│   │   ├── ImageResultController.java
│   │   ├── MedicalHistoryController.java
│   │   ├── SurgeryController.java
│   │   └── MinioController.java
│   │
│   └── agentic/                     # AI endpoints
│       └── AiChatController.java
│
├── dto/                             # Data Transfer Objects
│   ├── request/                     # Request DTOs
│   │   ├── LoginDTO.java
│   │   ├── PatientRequestDTO.java
│   │   ├── EpisodeRequestDTO.java
│   │   ├── ClinicalRecordRequestDTO.java
│   │   ├── LabResultRequestDTO.java
│   │   ├── CultureResultRequestDTO.java
│   │   ├── SensitivityResultRequestDTO.java
│   │   ├── ImageResultRequestDTO.java
│   │   ├── MedicalHistoryRequestDTO.java
│   │   ├── SurgeryRequestDTO.java
│   │   ├── UserRequestDTO.java
│   │   ├── AiChatRequestDTO.java
│   │   ├── CreateChatSessionRequestDTO.java
│   │   ├── SendChatMessageRequestDTO.java
│   │   └── AiRecommendationGenerateRequestDTO.java
│   │
│   └── response/                    # Response DTOs
│       ├── ResponseData.java        # Standard API response wrapper
│       ├── PaginationResultDTO.java # Paginated list response
│       ├── ResLoginDTO.java
│       ├── ResFileDTO.java
│       ├── AiChatResponseDTO.java
│       ├── AiPredictionResponseDTO.java
│       ├── AiRecommendationGenerateResponseDTO.java
│       └── AiRecommendationRunDetailDTO.java
│
├── exception/                       # Exception handling
│   ├── GlobalExceptionHandler.java  # @RestControllerAdvice
│   ├── ErrorResponse.java           # Error response format
│   ├── ResourceNotFoundException.java
│   ├── InvalidDataException.java
│   ├── BusinessException.java
│   └── ForbiddenException.java
│
├── message/                         # Messaging
│   └── RabbitMQPublisher.java       # RabbitMQ message publisher
│
├── model/                           # JPA Entities
│   ├── AbstractEntity.java          # Base entity (id, audit fields)
│   │
│   ├── auth/                        # Auth domain
│   │   ├── User.java
│   │   ├── Role.java
│   │   └── Permission.java
│   │
│   ├── medical/                     # Medical domain
│   │   ├── Patient.java
│   │   ├── Episode.java
│   │   ├── ClinicalRecord.java
│   │   ├── LabResult.java
│   │   ├── CultureResult.java
│   │   ├── SensitivityResult.java
│   │   ├── ImageResult.java
│   │   ├── MedicalHistory.java
│   │   └── Surgery.java
│   │
│   └── agentic/                     # AI domain
│       ├── AiChatSession.java
│       ├── AiChatMessage.java
│       ├── AiRecommendationRun.java
│       ├── AiRecommendationItem.java
│       ├── AiRagCitation.java
│       └── CaseClinicalSnapshot.java
│
├── repository/                      # Spring Data JPA Repositories
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── PermissionRepository.java
│   ├── PatientRepository.java
│   ├── EpisodeRepository.java
│   ├── ClinicalRecordRepository.java
│   ├── LabResultRepository.java
│   ├── CultureResultRepository.java
│   ├── SensitivityResultRepository.java
│   ├── ImageResultRepository.java
│   ├── MedicalHistoryRepository.java
│   ├── SurgeryRepository.java
│   ├── AiChatSessionRepository.java
│   ├── AiChatMessageRepository.java
│   ├── AiRecommendationRunRepository.java
│   ├── AiRecommendationItemRepository.java
│   ├── AiRagCitationRepository.java
│   └── CaseClinicalSnapshotRepository.java
│
├── services/                        # Service interfaces
│   ├── PatientService.java
│   ├── EpisodeService.java
│   ├── ClinicalRecordService.java
│   ├── LabResultService.java
│   ├── CultureResultService.java
│   ├── ImageResultService.java
│   ├── AiChatService.java
│   ├── AiServiceClient.java
│   ├── EpisodeSnapshotAssemblerService.java
│   │
│   └── impl/                        # Service implementations
│       ├── PatientServiceImpl.java
│       ├── EpisodeServiceImpl.java
│       ├── ClinicalRecordServiceImpl.java
│       ├── LabResultServiceImpl.java
│       ├── CultureResultServiceImpl.java
│       ├── ImageResultServiceImpl.java
│       ├── AiChatServiceImpl.java
│       └── ...
│
└── utils/                           # Utilities
    ├── SecurityUtils.java           # Get current user
    ├── mapper/                      # MapStruct mappers
    │   ├── EntityMapper.java        # Base mapper interface
    │   ├── DefaultConfigMapper.java # MapStruct config
    │   ├── PatientMapper.java
    │   ├── EpisodeMapper.java
    │   ├── ClinicalRecordMapper.java
    │   ├── LabResultMapper.java
    │   ├── CultureResultMapper.java
    │   ├── ImageResultMapper.java
    │   ├── MedicalHistoryMapper.java
    │   ├── SensitivityResultMapper.java
    │   ├── SurgeryMapper.java
    │   └── UserMapper.java
    │
    └── validators/                  # Custom validators
        └── EnumPattern.java         # Enum validation annotation

src/main/resources/
├── application.yaml                 # Main configuration
├── db/migration/                    # Flyway migrations (prod)
└── dev/db/migration/                # Flyway migrations (dev)
```

---

## Layered Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Controller Layer                          │
│  @RestController — handles HTTP, validation, response wrapping  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                         Service Layer                            │
│  @Service — business logic, transactions, orchestration         │
│  Interface + Impl pattern                                        │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                       Repository Layer                           │
│  JpaRepository + JpaSpecificationExecutor — data access         │
│  Spring Filter for dynamic queries                               │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                          Database                                │
│  PostgreSQL — relations, JSONB, enums                           │
└─────────────────────────────────────────────────────────────────┘
```

---

## Request/Response Flow

```
HTTP Request
    │
    ▼
┌─────────────────────┐
│ Security Filter     │ ← JWT validation, authentication
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│ AuthorityIntercepter│ ← Permission checking (method + path)
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│ Controller          │ ← @Valid validation, @RequestBody parsing
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│ Service             │ ← Business logic, mapper conversion
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│ Repository          │ ← JPA queries, Specification filtering
└─────────────────────┘
    │
    ▼
┌─────────────────────┐
│ ResponseData<T>     │ ← Wrapped response: { status, message, data }
└─────────────────────┘
```

---

## Authentication & Authorization

### JWT Flow
```
1. POST /api/v1/auth/login → validates credentials → returns access_token + refresh_token (cookie)
2. Client stores access_token, sends in Authorization: Bearer {token}
3. Security filter validates JWT, extracts user principal
4. AuthorityIntercepter checks user permissions against endpoint (method + path)
5. On 401: Client calls GET /api/v1/auth/refresh → new access_token
```

### Permission Model
```
User → Role → Permissions[]
Permission = { name, apiPath, method, module }

Example:
  - Role: DOCTOR
  - Permissions: [
      { name: "View Patients", apiPath: "/api/v1/patients", method: "GET", module: "PATIENT" },
      { name: "Create Patient", apiPath: "/api/v1/patients", method: "POST", module: "PATIENT" }
    ]
```

---

## Domain Model

### Medical Domain
```
Patient (1) ←──── (N) Episode (admission)
                         │
         ┌───────────────┼───────────────┐
         ▼               ▼               ▼
   ClinicalRecord   LabResult      CultureResult (1) ←── (N) SensitivityResult
         │               │               │
         │               │               │
         ▼               ▼               ▼
   ImageResult    MedicalHistory    Surgery
```

### AI/Agentic Domain
```
Episode (1) ←──── (N) AiRecommendationRun
                         │
         ┌───────────────┼───────────────┐
         ▼               ▼               ▼
AiRecommendationItem  AiRagCitation  CaseClinicalSnapshot

Episode (1) ←──── (N) AiChatSession (1) ←──── (N) AiChatMessage
```

---

## Key Patterns

### Base Entity (Auditing)
```java
@MappedSuperclass
public abstract class AbstractEntity<T> {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    private String createdBy;  // Auto-set via @PrePersist
    private String updatedBy;  // Auto-set via @PreUpdate
}
```

### Response Wrapper
```java
public class ResponseData<T> {
    private int status;      // HTTP status code
    private String message;  // Human-readable message
    private T data;          // Payload (null-excluded if empty)
}
```

### Pagination Response
```java
public class PaginationResultDTO {
    private Meta meta;       // { page, pageSize, pages, total }
    private Object result;   // List of items
}
```

### Dynamic Filtering (Spring Filter)
```java
// Controller
@GetMapping("/patients")
public ResponseData<PaginationResultDTO> getAll(
    @Filter Specification<Patient> spec,
    Pageable pageable
) { ... }

// Query: GET /api/v1/patients?filter=fullName~'John'&page=0&size=10&sort=createdAt,desc
```

---

## External Integrations

### AI Service
- **URL**: `http://localhost:8000` (configurable via `ai.service.base-url`)
- **Purpose**: AI-powered diagnosis recommendations, RAG chat
- **Client**: `AiServiceClient` (HTTP client with RestTemplate)

### RabbitMQ
- **Purpose**: Async AI recommendation generation
- **Pattern**: Publisher → Queue → AI Worker → Store results

### MinIO
- **Purpose**: File/image storage for medical images
- **Access**: `http://localhost:9000`

### Redis
- **Purpose**: Caching, session storage
- **Database**: 0

---

## Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8085 | Server port |
| `api.prefix` | /api/v1 | API path prefix |
| `spring.datasource.url` | jdbc:postgresql://localhost:5433/pji_dev | DB connection |
| `secure.jwt.access-token-validity-in-seconds` | 3600 | Access token TTL |
| `secure.jwt.refresh-token-validity-in-seconds` | 604800 | Refresh token TTL |
| `ai.service.base-url` | http://localhost:8000 | AI service URL |
| `spring.data.redis.host` | localhost | Redis host |
| `spring.rabbitmq.host` | localhost | RabbitMQ host |

---

## API Endpoints Overview

### Auth Module
| Method | Path | Description |
|--------|------|-------------|
| POST | /api/v1/auth/register | Register user |
| POST | /api/v1/auth/login | Login, get tokens |
| GET | /api/v1/auth/refresh | Refresh access token |
| POST | /api/v1/auth/logout | Logout |
| GET | /api/v1/auth/account | Get current user |

### Admin Module
| Method | Path | Description |
|--------|------|-------------|
| CRUD | /api/v1/users | User management |
| CRUD | /api/v1/roles | Role management |
| CRUD | /api/v1/permissions | Permission management |

### Medical Module
| Method | Path | Description |
|--------|------|-------------|
| CRUD | /api/v1/patients | Patient management |
| CRUD | /api/v1/episodes | Episode management |
| GET | /api/v1/patients/{id}/episodes | Episodes by patient |
| CRUD | /api/v1/clinical-records | Clinical records |
| CRUD | /api/v1/lab-results | Lab results |
| CRUD | /api/v1/culture-results | Culture results |
| CRUD | /api/v1/sensitivity-results | Sensitivity results |
| CRUD | /api/v1/image-results | Image results |
| CRUD | /api/v1/medical-history | Medical history |
| CRUD | /api/v1/surgeries | Surgeries |

### AI Module
| Method | Path | Description |
|--------|------|-------------|
| POST | /api/v1/ai-chat/sessions | Create chat session |
| POST | /api/v1/ai-chat/sessions/{id}/messages | Send message |
| GET | /api/v1/ai-chat/sessions/{id}/messages | Get messages |
| POST | /api/v1/episodes/{id}/ai-recommendations/generate | Generate AI recommendations |
| GET | /api/v1/episodes/{id}/ai-recommendations/runs | Get recommendation runs |

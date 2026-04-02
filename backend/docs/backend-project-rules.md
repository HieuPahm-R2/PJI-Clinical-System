# Backend Project Rules

> Conventions, patterns, and workflow for consistent Spring Boot development.

---

## Architecture Principles

1. **Layered architecture**: Controller → Service → Repository
2. **Interface + Implementation**: Services use interface + impl pattern
3. **DTO separation**: Request DTOs for input, Response DTOs for output, never expose entities directly
4. **MapStruct for mapping**: All DTO ↔ Entity conversions via MapStruct
5. **Specification for filtering**: Use Spring Filter / JPA Specifications for dynamic queries
6. **Centralized exception handling**: GlobalExceptionHandler handles all exceptions
7. **Consistent response format**: All APIs return `ResponseData<T>`

---

## Package Naming Conventions

| Package | Purpose |
|---------|---------|
| `config/` | Spring configurations (@Configuration) |
| `constant/` | Enums and static constants |
| `controller/` | REST controllers (@RestController) |
| `dto/request/` | Request DTOs (input validation) |
| `dto/response/` | Response DTOs (output format) |
| `exception/` | Custom exceptions + GlobalExceptionHandler |
| `model/` | JPA entities (@Entity) |
| `repository/` | Spring Data JPA repositories |
| `services/` | Service interfaces |
| `services/impl/` | Service implementations |
| `utils/` | Utility classes, mappers, validators |

---

## Class Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| Entity | `PascalCase` | `Patient.java` |
| Repository | `{Entity}Repository` | `PatientRepository.java` |
| Service Interface | `{Entity}Service` | `PatientService.java` |
| Service Impl | `{Entity}ServiceImpl` | `PatientServiceImpl.java` |
| Controller | `{Entity}Controller` | `PatientController.java` |
| Request DTO | `{Entity}RequestDTO` | `PatientRequestDTO.java` |
| Response DTO | `{Purpose}ResponseDTO` | `PaginationResultDTO.java` |
| Mapper | `{Entity}Mapper` | `PatientMapper.java` |
| Exception | `{Name}Exception` | `ResourceNotFoundException.java` |
| Config | `{Feature}Configuration` | `WebSecurityConfiguration.java` |

---

## Entity Pattern

### Standard Entity Template
```java
package com.vietnam.pji.model.{domain};

import com.vietnam.pji.model.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "{table_name}")
public class {EntityName} extends AbstractEntity<Long> implements Serializable {

    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;

    @Column(name = "json_field", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> jsonField;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusEnum status;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ParentEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<ChildEntity> children;
}
```

### Column Naming Rules
- Use `snake_case` for database columns
- Use `camelCase` for Java fields
- Map with `@Column(name = "snake_case")`

---

## Repository Pattern

### Standard Repository
```java
package com.vietnam.pji.repository;

import com.vietnam.pji.model.{domain}.{Entity};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface {Entity}Repository extends JpaRepository<{Entity}, Long>, JpaSpecificationExecutor<{Entity}> {
    
    // Custom queries
    boolean existsByUniqueField(String uniqueField);
    
    Optional<{Entity}> findByField(String field);
    
    List<{Entity}> findByParentId(Long parentId);
}
```

### Always Extend
- `JpaRepository<Entity, Long>` — basic CRUD
- `JpaSpecificationExecutor<Entity>` — dynamic filtering with Specifications

---

## Service Pattern

### Service Interface
```java
package com.vietnam.pji.services;

import com.vietnam.pji.dto.request.{Entity}RequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.model.{domain}.{Entity};
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface {Entity}Service {
    {Entity} create({Entity}RequestDTO data);
    {Entity} update(Long id, {Entity}RequestDTO data);
    {Entity} getById(Long id);
    void delete(Long id);
    PaginationResultDTO getAll(Specification<{Entity}> spec, Pageable pageable);
}
```

### Service Implementation
```java
package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.{Entity}RequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.exception.InvalidDataException;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.{domain}.{Entity};
import com.vietnam.pji.repository.{Entity}Repository;
import com.vietnam.pji.services.{Entity}Service;
import com.vietnam.pji.utils.mapper.{Entity}Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class {Entity}ServiceImpl implements {Entity}Service {

    private final {Entity}Repository repository;
    private final {Entity}Mapper mapper;

    @Override
    @Transactional
    public {Entity} create({Entity}RequestDTO data) {
        // Validate uniqueness if needed
        if (repository.existsByUniqueField(data.getUniqueField())) {
            throw new InvalidDataException("{Entity} with this field already exists.");
        }
        {Entity} entity = mapper.toEntity(data);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public {Entity} update(Long id, {Entity}RequestDTO data) {
        {Entity} entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("{Entity} not found"));
        mapper.update(data, entity);
        return repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public {Entity} getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("{Entity} not found"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("{Entity} not found");
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationResultDTO getAll(Specification<{Entity}> spec, Pageable pageable) {
        Page<{Entity}> page = repository.findAll(spec, pageable);
        
        PaginationResultDTO.Meta meta = new PaginationResultDTO.Meta();
        meta.setPage(page.getNumber() + 1);
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        PaginationResultDTO result = new PaginationResultDTO();
        result.setMeta(meta);
        result.setResult(page.getContent());
        return result;
    }
}
```

---

## Controller Pattern

### Standard Controller
```java
package com.vietnam.pji.controller.{domain};

import com.turkraft.springfilter.boot.Filter;
import com.vietnam.pji.dto.request.{Entity}RequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.dto.response.ResponseData;
import com.vietnam.pji.model.{domain}.{Entity};
import com.vietnam.pji.services.{Entity}Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}")
@Validated
@Tag(name = "{Entity} Controller")
@RequiredArgsConstructor
public class {Entity}Controller {

    private final {Entity}Service service;

    @PostMapping("/{entities}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<{Entity}> create(@Valid @RequestBody {Entity}RequestDTO request) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "{Entity} created successfully", service.create(request));
    }

    @PutMapping("/{entities}/{id}")
    public ResponseData<{Entity}> update(@PathVariable Long id, @Valid @RequestBody {Entity}RequestDTO request) {
        return new ResponseData<>(HttpStatus.OK.value(), "{Entity} updated successfully", service.update(id, request));
    }

    @GetMapping("/{entities}/{id}")
    public ResponseData<{Entity}> getById(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch {entity} successfully", service.getById(id));
    }

    @DeleteMapping("/{entities}/{id}")
    public ResponseData<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "{Entity} deleted successfully");
    }

    @GetMapping("/{entities}")
    public ResponseData<PaginationResultDTO> getAll(
            @Filter Specification<{Entity}> spec, Pageable pageable) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch {entities} successfully", service.getAll(spec, pageable));
    }
}
```

### Controller Rules
- Always use `@Validated` at class level
- Always use `@Valid` for `@RequestBody`
- Use `@ResponseStatus(HttpStatus.CREATED)` for POST
- Use `${api.prefix}` from configuration
- Return `ResponseData<T>` for all responses

---

## DTO Pattern

### Request DTO
```java
package com.vietnam.pji.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vietnam.pji.constant.StatusEnum;
import com.vietnam.pji.utils.validators.EnumPattern;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter
@Setter
public class {Entity}RequestDTO {

    @NotBlank(message = "fieldName must not be blank")
    private String fieldName;

    @NotNull(message = "dateField must not be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateField;

    @EnumPattern(name = "status", regexp = "ACTIVE|INACTIVE")
    private StatusEnum status;

    @Min(value = 0, message = "numberField must be >= 0")
    @Max(value = 100, message = "numberField must be <= 100")
    private Integer numberField;

    @Email(message = "email must be valid")
    private String email;

    @JsonProperty("parentId")
    private Long parentId;
}
```

### Validation Annotations
| Annotation | Use Case |
|------------|----------|
| `@NotBlank` | Required string fields |
| `@NotNull` | Required non-string fields |
| `@Email` | Email validation |
| `@Min` / `@Max` | Numeric range |
| `@Size` | String length |
| `@Pattern` | Regex pattern |
| `@EnumPattern` | Custom enum validation |
| `@JsonFormat` | Date format for JSON |

---

## Mapper Pattern

### Mapper Interface
```java
package com.vietnam.pji.utils.mapper;

import com.vietnam.pji.dto.request.{Entity}RequestDTO;
import com.vietnam.pji.model.{domain}.{Entity};
import org.mapstruct.*;

@Mapper(config = DefaultConfigMapper.class)
public interface {Entity}Mapper extends EntityMapper<{Entity}RequestDTO, {Entity}> {

    // Custom mappings if needed
    @Override
    @Mapping(target = "parent", ignore = true)  // Handle relationships manually
    {Entity} toEntity({Entity}RequestDTO dto);
}
```

### EntityMapper Base Interface
Provides standard methods:
- `toEntity(DTO)` — DTO → Entity (null-safe)
- `toDto(Entity)` — Entity → DTO
- `update(DTO, Entity)` — Partial update (ignores null fields)
- `toDto(List<Entity>)` — Batch conversion

---

## Exception Handling

### Standard Exceptions
| Exception | HTTP Status | Use Case |
|-----------|-------------|----------|
| `ResourceNotFoundException` | 404 | Entity not found |
| `InvalidDataException` | 409 | Duplicate data, constraint violation |
| `BusinessException` | 400 | Business rule violation |
| `ForbiddenException` | 403 | Unauthorized access |
| `ConstraintViolationException` | 400 | Validation failure |

### Throwing Exceptions
```java
// Not found
throw new ResourceNotFoundException("Patient not found");

// Duplicate
throw new InvalidDataException("Patient with this email already exists");

// Business rule
throw new BusinessException("Cannot delete patient with active episodes");

// Forbidden
throw new ForbiddenException("You don't have permission to access this resource");
```

---

## Adding New Features Workflow

### 1. Create Entity
```java
// model/{domain}/{Entity}.java
@Entity
@Table(name = "{entities}")
public class {Entity} extends AbstractEntity<Long> { ... }
```

### 2. Create Repository
```java
// repository/{Entity}Repository.java
public interface {Entity}Repository extends JpaRepository<{Entity}, Long>, JpaSpecificationExecutor<{Entity}> { }
```

### 3. Create Request DTO
```java
// dto/request/{Entity}RequestDTO.java
@Getter @Setter
public class {Entity}RequestDTO { ... }
```

### 4. Create Mapper
```java
// utils/mapper/{Entity}Mapper.java
@Mapper(config = DefaultConfigMapper.class)
public interface {Entity}Mapper extends EntityMapper<{Entity}RequestDTO, {Entity}> { }
```

### 5. Create Service Interface
```java
// services/{Entity}Service.java
public interface {Entity}Service { ... }
```

### 6. Create Service Implementation
```java
// services/impl/{Entity}ServiceImpl.java
@Service
@RequiredArgsConstructor
public class {Entity}ServiceImpl implements {Entity}Service { ... }
```

### 7. Create Controller
```java
// controller/{domain}/{Entity}Controller.java
@RestController
@RequestMapping("${api.prefix}")
public class {Entity}Controller { ... }
```

### 8. Add Database Migration
```sql
-- resources/db/migration/V{version}__{description}.sql
CREATE TABLE {entities} (
    id BIGSERIAL PRIMARY KEY,
    field_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);
```

---

## Database Migration (Flyway)

### Naming Convention
```
V{version}__{description}.sql

Examples:
V1__create_users_table.sql
V2__create_patients_table.sql
V3__add_phone_to_patients.sql
V4__create_episodes_table.sql
```

### Migration Rules
- Never modify existing migrations
- Always add new migrations for changes
- Use `baseline-version: 0` for existing databases
- Separate dev migrations in `dev/db/migration/`

---

## Security Rules

### Protected Endpoints
- All endpoints except whitelist require JWT
- Whitelist: `/api/v1/auth/login`, `/api/v1/auth/refresh`, `/api/v1/auth/register`, `/swagger-ui/**`

### Permission Checking
- `AuthorityIntercepter` checks user permissions
- Permission = `{ method, apiPath, module }`
- User must have matching permission for endpoint

### NEVER DO
- Hardcode secrets in source code
- Commit credentials to git
- Log passwords or tokens
- Disable security in production
- Skip JWT validation

---

## Code Quality Checklist

### Before Committing
- [ ] No compilation errors
- [ ] All tests pass
- [ ] Request DTOs have validation annotations
- [ ] Service methods have `@Transactional`
- [ ] Read-only methods have `@Transactional(readOnly = true)`
- [ ] Exceptions are handled appropriately
- [ ] No hardcoded values (use constants or config)
- [ ] Swagger annotations for API documentation

### MUST DO
- Use constructor injection (`@RequiredArgsConstructor`)
- Use Lombok annotations (`@Getter`, `@Setter`, `@Builder`)
- Use `Optional` for nullable returns
- Validate input at controller level
- Return `ResponseData<T>` from controllers
- Use MapStruct for DTO ↔ Entity mapping
- Add `@Tag` annotation for Swagger grouping

### MUST NOT
- Expose JPA entities directly in APIs
- Use field injection (`@Autowired` on fields)
- Catch generic `Exception` (catch specific exceptions)
- Return null from service methods (throw exception instead)
- Mix business logic in controllers
- Use raw SQL without parameterization (SQL injection risk)

---

## Testing Guidelines

### Unit Tests
- Test service methods with mocked repositories
- Test mappers for correct conversions
- Test validators for edge cases

### Integration Tests
- Test full request/response cycle
- Test database operations
- Test security (authentication/authorization)

### Test Naming
```java
@Test
void create_ValidData_ReturnsCreatedEntity() { }

@Test
void create_DuplicateEmail_ThrowsInvalidDataException() { }

@Test
void getById_NotFound_ThrowsResourceNotFoundException() { }
```

---

## Logging Guidelines

### Log Levels
| Level | Use Case |
|-------|----------|
| `ERROR` | Unexpected failures requiring attention |
| `WARN` | Unexpected but recoverable situations |
| `INFO` | Normal significant events |
| `DEBUG` | Detailed debugging info (dev only) |

### What to Log
- Request received (INFO)
- Business operations completed (INFO)
- External service calls (DEBUG)
- Exceptions (ERROR with stack trace)

### What NOT to Log
- Passwords, tokens, credentials
- Full request/response bodies (use DEBUG level only)
- Sensitive PII data

# Backend Overview - Plataforma de Torneos E-Sport

## Información General
- **Tecnología**: Spring Boot 3.2.0 + Java 17
- **Arquitectura**: Hexagonal (Clean Architecture) + DDD
- **Base de Datos**: PostgreSQL (prod) / H2 (dev)
- **Puerto**: 8081
- **Documentación API**: http://localhost:8081/swagger-ui.html

## 1. Arquitectura del Backend

### 1.1 Visión Arquitectónica
El backend implementa una **arquitectura hexagonal** (Ports & Adapters) combinada con principios de **Domain-Driven Design**, proporcionando una separación clara entre la lógica de negocio y los detalles de infraestructura.

```
┌─────────────────────────────────────────────────────────────┐
│                 INFRASTRUCTURE LAYER                        │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Controllers   │  │   Config &      │  │ Persistence │ │
│  │   (REST APIs)   │  │   Security      │  │ (JPA/SQL)   │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                  APPLICATION LAYER                          │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Services      │  │      DTOs       │  │ Use Cases   │ │
│  │ (Orchestration) │  │ (Data Transfer) │  │ (Business)  │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    DOMAIN LAYER                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Entities      │  │   Repositories  │  │   Events    │ │
│  │ (Business Logic)│  │  (Interfaces)   │  │ (Domain)    │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Principios Arquitectónicos
- **Inversión de Dependencias**: El dominio no depende de la infraestructura
- **Separación de Responsabilidades**: Cada capa tiene un propósito específico
- **Testabilidad**: Lógica de negocio aislada y testeable
- **Extensibilidad**: Fácil adición de nuevas funcionalidades

## 2. Capa de Dominio (Domain Layer)

### 2.1 Entidades Principales
```java
// Agregado Raíz - Tournament
public class Tournament {
    private UUID id;
    private UUID organizerId;
    private String name;
    private TournamentStatus status;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    
    // Métodos de negocio
    public void publish() {
        if (status != TournamentStatus.DRAFT) {
            throw new IllegalStateException("Solo los torneos en borrador pueden ser publicados");
        }
        this.status = TournamentStatus.PUBLISHED;
    }
    
    public boolean canBeModified() {
        return status == TournamentStatus.DRAFT;
    }
}

// Entidad - User
public class User {
    private UUID id;
    private String email;
    private String fullName;
    private UserRole role;
    
    public boolean canOrganizeTournaments() {
        return role == UserRole.ORGANIZER;
    }
}

// Entidad - Ticket
public class Ticket {
    private UUID id;
    private String accessCode;
    private TicketStatus status;
    
    public void validate() {
        if (status != TicketStatus.ISSUED) {
            throw new IllegalStateException("Ticket already used or cancelled");
        }
        this.status = TicketStatus.USED;
        this.usedAt = LocalDateTime.now();
    }
}
```

### 2.2 Eventos de Dominio
```java
// Eventos para comunicación entre agregados
public record TournamentPublished(UUID tournamentId, UUID organizerId, LocalDateTime publishedAt) 
    implements DomainEvent {}

public record TicketOrderApproved(UUID orderId, UUID tournamentId, UUID userId, int quantity) 
    implements DomainEvent {}

public record TournamentCancelled(UUID tournamentId, String reason) 
    implements DomainEvent {}
```

### 2.3 Repositorios (Interfaces)
```java
// Contratos definidos en el dominio
public interface TournamentRepository {
    Tournament save(Tournament tournament);
    Optional<Tournament> findById(UUID id);
    List<Tournament> findByOrganizerId(UUID organizerId);
    long countByOrganizerIdAndIsPaidAndStatus(UUID organizerId, boolean isPaid, TournamentStatus status);
}

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

## 3. Capa de Aplicación (Application Layer)

### 3.1 Servicios de Aplicación
Los servicios orquestan las operaciones de negocio y coordinan entre diferentes agregados:

```java
@Service
@Transactional
public class TournamentService {
    
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    
    public TournamentResponse create(CreateTournamentRequest request, UUID organizerId) {
        // 1. Validaciones de negocio
        User organizer = userRepository.findById(organizerId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
        if (!organizer.canOrganizeTournaments()) {
            throw new IllegalArgumentException("Solo los usuarios con rol ORGANIZER pueden crear torneos");
        }
        
        // 2. Validar límite de torneos gratuitos
        if (!request.isPaid()) {
            long freeTournaments = tournamentRepository
                .countByOrganizerIdAndIsPaidAndStatus(organizerId, false, TournamentStatus.PUBLISHED);
            if (freeTournaments >= 2) {
                throw new IllegalArgumentException("Un organizador solo puede tener máximo 2 torneos gratuitos activos");
            }
        }
        
        // 3. Crear entidad de dominio
        Tournament tournament = new Tournament(
            organizerId, request.categoryId(), request.gameTypeId(),
            request.name(), request.description(), request.isPaid(),
            request.maxFreeCapacity(), request.startDateTime(), request.endDateTime()
        );
        
        // 4. Persistir
        Tournament savedTournament = tournamentRepository.save(tournament);
        
        // 5. Auditoría
        auditLogService.logTournamentCreated(
            savedTournament.getId(), organizerId, savedTournament.getName()
        );
        
        return mapToResponse(savedTournament);
    }
    
    public TournamentResponse publish(UUID tournamentId, UUID userId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));
            
        // Lógica de dominio
        tournament.publish();
        
        Tournament savedTournament = tournamentRepository.save(tournament);
        
        // Publicar evento de dominio
        domainEventPublisher.publish(new TournamentPublished(tournamentId, userId, LocalDateTime.now()));
        
        return mapToResponse(savedTournament);
    }
}
```

### 3.2 DTOs (Data Transfer Objects)
```java
// Request DTOs
public record CreateTournamentRequest(
    @NotBlank String categoryId,
    @NotBlank String gameTypeId,
    @NotBlank @Size(max = 255) String name,
    @Size(max = 1000) String description,
    boolean isPaid,
    @Min(1) Integer maxFreeCapacity,
    @NotNull @Future LocalDateTime startDateTime,
    @NotNull LocalDateTime endDateTime
) {}

// Response DTOs
public record TournamentResponse(
    UUID id,
    String name,
    String description,
    UUID organizerId,
    UUID categoryId,
    UUID gameTypeId,
    boolean isPaid,
    Integer maxFreeCapacity,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

## 4. Capa de Infraestructura (Infrastructure Layer)

### 4.1 Controladores REST
```java
@RestController
@RequestMapping("/api/tournaments")
@Tag(name = "Tournaments", description = "API para gestión de torneos")
public class TournamentController {
    
    private final TournamentService tournamentService;
    
    @PostMapping
    @Operation(summary = "Crear torneo", description = "Crea un nuevo torneo (solo ORGANIZER)")
    public ResponseEntity<TournamentResponse> create(
            @Valid @RequestBody CreateTournamentRequest request,
            @RequestHeader("X-USER-ID") String userIdHeader) {
        
        UUID organizerId = UUID.fromString(userIdHeader);
        TournamentResponse response = tournamentService.create(request, organizerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    @Operation(summary = "Listar torneos", description = "Obtiene todos los torneos con filtros")
    public ResponseEntity<Page<TournamentResponse>> findAll(
            @RequestParam(required = false) Boolean isPaid,
            @RequestParam(required = false) Tournament.TournamentStatus status,
            Pageable pageable) {
        
        Page<TournamentResponse> response = tournamentService.findByFilters(
            isPaid, status, null, null, null, pageable);
        return ResponseEntity.ok(response);
    }
}
```

### 4.2 Implementación de Repositorios
```java
@Repository
public class TournamentRepositoryImpl implements TournamentRepository {

    private final JpaTournamentRepository jpaRepository;
    private final TournamentMapper mapper;

    @Override
    public Tournament save(Tournament tournament) {
        TournamentEntity entity = mapper.toEntity(tournament);
        TournamentEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Tournament> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}

// JPA Repository
@Repository
public interface JpaTournamentRepository extends JpaRepository<TournamentEntity, UUID> {
    
    @Query("""
        SELECT t FROM TournamentEntity t 
        WHERE (:isPaid IS NULL OR t.isPaid = :isPaid)
        AND (:status IS NULL OR t.status = :status)
        ORDER BY t.createdAt DESC
        """)
    Page<TournamentEntity> findByFilters(@Param("isPaid") Boolean isPaid,
                                       @Param("status") TournamentEntity.TournamentStatus status,
                                       Pageable pageable);
}
```

### 4.3 Entidades JPA
```java
@Entity
@Table(name = "tournaments")
public class TournamentEntity extends BaseEntity {
    
    @Column(name = "organizer_id", nullable = false)
    private UUID organizerId;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus status = TournamentStatus.DRAFT;
    
    @Version
    private Long version; // Optimistic locking
    
    // Getters, setters, lifecycle callbacks
}
```

## 5. Seguridad

### 5.1 Configuración de Spring Security
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tournaments/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/tournaments/**").hasRole("ORGANIZER")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### 5.2 Autenticación JWT
```java
@Service
public class JwtService {
    
    public String generateToken(UUID userId, String email, String role) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(jwtExpiration, ChronoUnit.MILLIS)))
                .signWith(secretKey)
                .compact();
    }
    
    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

## 6. Persistencia y Base de Datos

### 6.1 Configuración de Base de Datos
```yaml
# PostgreSQL (Producción)
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/torneo
    username: postgres
    password: 1234
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  
  flyway:
    enabled: true
    locations: classpath:db/migration
```

### 6.2 Migraciones Flyway
```sql
-- V1__Initial_schema.sql
CREATE TABLE tournaments (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    organizer_id UUID NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- V3__Security_constraints.sql
ALTER TABLE tournaments ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE tournaments ADD CONSTRAINT check_dates 
    CHECK (start_date_time < end_date_time);
```

## 7. Auditoría y Trazabilidad

### 7.1 Sistema de Auditoría
```java
@Service
public class AuditLogService {
    
    public void logEvent(AuditLog.EventType eventType, 
                        AuditLog.EntityType entityType,
                        UUID entityId, 
                        UUID actorUserId, 
                        String metadata) {
        
        AuditLog auditLog = new AuditLog(eventType, entityType, entityId, actorUserId, metadata);
        auditLogRepository.save(auditLog);
        
        // Log estructurado para monitoreo
        auditLogger.info("AUDIT_EVENT: type={}, entity={}, entityId={}, actor={}", 
            eventType, entityType, entityId, actorUserId);
    }
}
```

### 7.2 Logging Estructurado
```java
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String correlationId = UUID.randomUUID().toString();
        String userId = request.getHeader("X-USER-ID");
        
        MDC.put("correlationId", correlationId);
        MDC.put("userId", userId);
        MDC.put("requestUri", request.getRequestURI());
        
        response.setHeader("X-Correlation-ID", correlationId);
        return true;
    }
}
```

## 8. Métricas y Monitoreo

### 8.1 Métricas de Negocio
```java
@Component
public class BusinessMetrics {
    
    private final Counter tournamentsCreated;
    private final Timer orderProcessingTime;
    
    public BusinessMetrics(MeterRegistry meterRegistry) {
        this.tournamentsCreated = Counter.builder("tournaments.created.total")
            .description("Total tournaments created")
            .register(meterRegistry);
    }
    
    public void recordTournamentCreated() {
        tournamentsCreated.increment();
    }
}
```

### 8.2 Health Checks
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up().withDetail("database", "Available").build();
            }
        } catch (Exception e) {
            return Health.down().withException(e).build();
        }
        return Health.down().build();
    }
}
```

## 9. Manejo de Errores

### 9.1 Exception Handler Global
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.badRequest().body(problem);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setProperty("validationErrors", errors);
        return ResponseEntity.badRequest().body(problem);
    }
}
```

## 10. Flujo de Negocio Completo

### 10.1 Creación de Torneo
```
1. Usuario ORGANIZER envía POST /api/tournaments
2. JwtAuthenticationFilter valida token y establece contexto de seguridad
3. TournamentController recibe request y extrae userId del header
4. TournamentService.create() ejecuta:
   - Valida que el usuario sea ORGANIZER
   - Verifica límite de torneos gratuitos (máximo 2)
   - Valida fechas y datos de entrada
   - Crea entidad Tournament con lógica de dominio
   - Persiste en base de datos
   - Registra evento en audit log
5. Responde con TournamentResponse (201 Created)
```

### 10.2 Publicación de Torneo
```
1. Usuario envía POST /api/tournaments/{id}/publish
2. TournamentService.publish() ejecuta:
   - Busca torneo por ID
   - Valida permisos (organizador o subadmin)
   - Ejecuta tournament.publish() (lógica de dominio)
   - Persiste cambio de estado
   - Publica TournamentPublished event
   - Registra en audit log
3. TournamentEventHandler procesa evento:
   - Envía notificaciones
   - Actualiza cache
   - Registra métricas
```

### 10.3 Compra de Tickets
```
1. Usuario envía POST /api/tournaments/{id}/orders
2. TicketOrderService.createOrder() ejecuta:
   - Valida que el torneo esté PUBLISHED
   - Verifica disponibilidad en la etapa de venta
   - Crea TicketOrder en estado PENDING
   - Calcula precio total
   - Persiste orden
3. Proceso de aprobación (manual o automático):
   - Cambia estado a APPROVED
   - Genera tickets individuales con códigos únicos
   - Publica TicketOrderApproved event
   - Envía confirmación al usuario
```

## 11. APIs Principales

### 11.1 Gestión de Torneos
- `POST /api/tournaments` - Crear torneo
- `GET /api/tournaments` - Listar torneos con filtros
- `GET /api/tournaments/{id}` - Obtener torneo por ID
- `PUT /api/tournaments/{id}` - Actualizar torneo
- `POST /api/tournaments/{id}/publish` - Publicar torneo

### 11.2 Gestión de Usuarios
- `POST /api/users` - Crear usuario (registro)
- `GET /api/users/{id}` - Obtener usuario
- `PUT /api/users/{id}` - Actualizar usuario

### 11.3 Autenticación
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/refresh` - Renovar token
- `POST /api/auth/logout` - Cerrar sesión

### 11.4 Tickets y Órdenes
- `POST /api/tournaments/{id}/orders` - Crear orden de tickets
- `GET /api/orders/{id}` - Obtener orden
- `POST /api/tickets/{code}/validate` - Validar ticket

## 12. Configuración y Despliegue

### 12.1 Perfiles de Configuración
- **dev**: H2 en memoria, logs detallados
- **postgres**: PostgreSQL, configuración completa
- **prod**: Optimizado para producción

### 12.2 Comandos de Ejecución
```bash
# Desarrollo con H2
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Desarrollo con PostgreSQL
./mvnw spring-boot:run -Dspring.profiles.active=postgres

# Compilar para producción
./mvnw clean package -DskipTests
java -jar target/torneos-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

---

**Conclusión**: El backend implementa una arquitectura robusta y escalable que separa claramente las responsabilidades, proporciona APIs RESTful completas, maneja la seguridad con JWT, implementa auditoría completa y está preparado para evolucionar hacia una arquitectura de microservicios cuando sea necesario.
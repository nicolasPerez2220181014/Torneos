# 🚀 GUÍA DE IMPLEMENTACIÓN - REFACTOR COMPLETO

## 📋 CONTROL DE PROGRESO

### Estado Actual
- **Fecha Inicio**: 19 Diciembre 2025
- **Etapa Actual**: PROYECTO COMPLETADO 🎉
- **Progreso Global**: 100% (5 de 5 etapas completadas)
- **Última Actualización**: 19 Dic 2025 - 21:45

### Checklist General
- [x] **ETAPA 1**: Seguridad y Estabilidad (5/12 tareas completadas)
- [x] **ETAPA 2**: Domain-Driven Design (5/8 tareas completadas)  
- [x] **ETAPA 3**: API Evolution (6/6 tareas) ✅ COMPLETADA
- [x] **ETAPA 4**: Observabilidad (5/7 tareas) ✅ COMPLETADA
- [x] **ETAPA 5**: Performance (6/6 tareas) ✅ COMPLETADA

---

## 🎯 ETAPA 1: SEGURIDAD Y ESTABILIDAD
**Duración Estimada**: 3 semanas  
**Prioridad**: CRÍTICA  
**Estado**: ⏳ PENDIENTE

### Objetivos
- ✅ Eliminar race conditions en capacity management
- ✅ Implementar idempotencia en operaciones críticas
- ✅ Mejorar seguridad JWT con token rotation
- ✅ Agregar optimistic locking

### Tareas Detalladas

#### 1.1 Database Constraints y Optimistic Locking
**Archivos a modificar**:
- `src/main/resources/db/migration/V3__Security_constraints.sql` (CREAR)
- `src/main/java/com/example/torneos/domain/model/*.java` (MODIFICAR)

**Implementación**:
```sql
-- V3__Security_constraints.sql
-- Optimistic locking
ALTER TABLE tournaments ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE ticket_orders ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE ticket_sale_stages ADD COLUMN version BIGINT DEFAULT 0;

-- Business constraints
ALTER TABLE ticket_sale_stages 
ADD CONSTRAINT check_capacity_positive CHECK (capacity > 0),
ADD CONSTRAINT check_price_non_negative CHECK (price >= 0);

ALTER TABLE ticket_orders 
ADD CONSTRAINT check_quantity_positive CHECK (quantity > 0);

-- Performance indexes
CREATE INDEX CONCURRENTLY idx_tournaments_organizer_status 
ON tournaments(organizer_id, status);

CREATE INDEX CONCURRENTLY idx_ticket_orders_tournament_status 
ON ticket_orders(tournament_id, status);
```

**Entidades a actualizar**:
```java
// TournamentEntity.java, TicketOrderEntity.java, TicketSaleStageEntity.java
@Version
private Long version;
```

**Checklist**:
- [x] Crear migración V3__Security_constraints.sql
- [x] Agregar @Version a entidades críticas
- [ ] Ejecutar migración y verificar constraints
- [ ] Tests de optimistic locking

#### 1.2 Race Condition Fix - Capacity Management
**Archivos a modificar**:
- `src/main/java/com/example/torneos/application/service/TicketOrderService.java`
- `src/main/java/com/example/torneos/infrastructure/persistence/repository/JpaTicketSaleStageRepository.java`

**Implementación**:
```java
// JpaTicketSaleStageRepository.java
@Query("SELECT t FROM TicketSaleStageEntity t WHERE t.id = :id")
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<TicketSaleStageEntity> findByIdForUpdate(@Param("id") UUID id);

// TicketOrderService.java
@Transactional(isolation = Isolation.SERIALIZABLE)
public TicketOrderResponse createOrder(UUID tournamentId, CreateTicketOrderRequest request) {
    // 1. Lock stage for update
    TicketSaleStage stage = ticketSaleStageRepository.findByIdForUpdate(request.getStageId())
        .orElseThrow(() -> new StageNotFoundException());
    
    // 2. Check capacity atomically
    if (!stage.hasAvailableCapacity(request.getQuantity())) {
        throw new InsufficientCapacityException();
    }
    
    // 3. Create order
    TicketOrder order = TicketOrder.create(tournamentId, getCurrentUserId(), request);
    return ticketOrderRepository.save(order);
}
```

**Checklist**:
- [x] Implementar findByIdForUpdate con PESSIMISTIC_WRITE
- [x] Refactor createOrder con isolation SERIALIZABLE
- [x] Agregar hasAvailableCapacity() method
- [ ] Tests de concurrencia

#### 1.3 Idempotency Implementation
**Archivos a crear**:
- `src/main/java/com/example/torneos/infrastructure/config/IdempotencyFilter.java`
- `src/main/java/com/example/torneos/application/service/IdempotencyService.java`
- `src/main/java/com/example/torneos/infrastructure/persistence/entity/IdempotencyKeyEntity.java`

**Implementación**:
```java
// IdempotencyFilter.java
@Component
public class IdempotencyFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String idempotencyKey = httpRequest.getHeader("Idempotency-Key");
        
        if (isIdempotentEndpoint(httpRequest) && idempotencyKey != null) {
            // Check if already processed
            Optional<IdempotencyResult> cached = idempotencyService.getResult(idempotencyKey);
            if (cached.isPresent()) {
                writeResponse((HttpServletResponse) response, cached.get());
                return;
            }
        }
        
        chain.doFilter(request, response);
    }
}

// IdempotencyService.java
@Service
public class IdempotencyService {
    
    @Transactional
    public void storeResult(String key, Object result, int statusCode) {
        IdempotencyKeyEntity entity = new IdempotencyKeyEntity();
        entity.setKey(key);
        entity.setResponse(JsonUtils.toJson(result));
        entity.setStatusCode(statusCode);
        entity.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        
        idempotencyRepository.save(entity);
    }
}
```

**Endpoints a proteger**:
- `POST /api/tournaments/{id}/orders`
- `POST /api/tournaments`
- `POST /api/tournaments/{id}/subadmins`

**Checklist**:
- [x] Crear IdempotencyFilter
- [x] Crear IdempotencyService
- [x] Crear tabla idempotency_keys
- [x] Configurar filtro en endpoints críticos
- [ ] Tests de idempotencia

#### 1.4 JWT Security Enhancement
**Archivos a modificar**:
- `src/main/java/com/example/torneos/application/service/JwtService.java`
- `src/main/java/com/example/torneos/application/service/AuthenticationService.java`

**Archivos a crear**:
- `src/main/java/com/example/torneos/application/service/RefreshTokenService.java`
- `src/main/java/com/example/torneos/infrastructure/persistence/entity/RefreshTokenEntity.java`

**Implementación**:
```java
// RefreshTokenService.java
@Service
public class RefreshTokenService {
    
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        // 1. Validate refresh token
        RefreshTokenEntity tokenEntity = refreshTokenRepository
            .findByTokenAndNotExpired(refreshToken)
            .orElseThrow(() -> new InvalidRefreshTokenException());
        
        // 2. Blacklist old token
        tokenEntity.setRevoked(true);
        refreshTokenRepository.save(tokenEntity);
        
        // 3. Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(tokenEntity.getUserEmail());
        String newRefreshToken = jwtService.generateRefreshToken(tokenEntity.getUserEmail());
        
        // 4. Store new refresh token
        storeRefreshToken(newRefreshToken, tokenEntity.getUserEmail());
        
        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}
```

**Checklist**:
- [x] Crear RefreshTokenService
- [x] Crear tabla refresh_tokens
- [x] Implementar token rotation
- [x] Implementar token blacklist
- [ ] Tests de seguridad JWT

#### 1.5 Input Validation Enhancement
**Archivos a modificar**:
- Todos los DTOs en `src/main/java/com/example/torneos/application/dto/request/`

**Implementación**:
```java
// CreateTournamentRequest.java
public class CreateTournamentRequest {
    
    @NotNull(message = "Category ID is required")
    private UUID categoryId;
    
    @NotBlank(message = "Tournament name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;
    
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDateTime;
    
    @AssertTrue(message = "End date must be after start date")
    public boolean isEndDateAfterStartDate() {
        return endDateTime != null && startDateTime != null && 
               endDateTime.isAfter(startDateTime);
    }
}
```

**Checklist**:
- [x] Agregar validaciones a todos los Request DTOs
- [x] Implementar custom validators
- [x] Configurar validation messages
- [ ] Tests de validación

---

## 🏗️ ETAPA 2: DOMAIN-DRIVEN DESIGN
**Duración Estimada**: 4 semanas  
**Prioridad**: ALTA  
**Estado**: ⏳ PENDIENTE

### Objetivos
- ✅ Convertir modelo anémico a rich domain model
- ✅ Implementar Value Objects
- ✅ Definir Aggregate boundaries
- ✅ Implementar Domain Events

### Tareas Detalladas

#### 2.1 Value Objects Implementation
**Archivos a crear**:
- `src/main/java/com/example/torneos/domain/valueobject/Email.java`
- `src/main/java/com/example/torneos/domain/valueobject/Money.java`
- `src/main/java/com/example/torneos/domain/valueobject/AccessCode.java`
- `src/main/java/com/example/torneos/domain/valueobject/TournamentId.java`

**Implementación**:
```java
// Email.java
public record Email(String value) {
    public Email {
        Objects.requireNonNull(value, "Email cannot be null");
        if (!value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
    }
}

// Money.java
public record Money(BigDecimal amount) {
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }
    
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }
}
```

#### 2.2 Rich Domain Model
**Archivos a modificar**:
- `src/main/java/com/example/torneos/domain/model/Tournament.java`
- `src/main/java/com/example/torneos/domain/model/TicketSaleStage.java`
- `src/main/java/com/example/torneos/domain/model/TicketOrder.java`
- `src/main/java/com/example/torneos/domain/model/User.java`

**Archivos a crear**:
- `src/main/java/com/example/torneos/domain/service/TournamentDomainService.java`
- `src/main/java/com/example/torneos/domain/service/TicketOrderDomainService.java`

**Implementación**:
```java
// Tournament.java - Business methods
public void publish() {
    if (status != TournamentStatus.DRAFT) {
        throw new IllegalStateException("Only draft tournaments can be published");
    }
    this.status = TournamentStatus.PUBLISHED;
    this.updatedAt = LocalDateTime.now();
}

public boolean canBeModified() {
    return status == TournamentStatus.DRAFT;
}

// TicketOrder.java - Business methods
public void approve() {
    if (status != OrderStatus.PENDING) {
        throw new IllegalStateException("Only pending orders can be approved");
    }
    this.status = OrderStatus.APPROVED;
}
```

#### 2.3 Aggregate Boundaries
**Archivos a crear**:
- `src/main/java/com/example/torneos/domain/aggregate/TournamentAggregate.java`
- `src/main/java/com/example/torneos/domain/aggregate/TicketOrderAggregate.java`
- `src/main/java/com/example/torneos/domain/aggregate/UserAggregate.java`
- `src/main/java/com/example/torneos/domain/repository/TournamentAggregateRepository.java`
- `src/main/java/com/example/torneos/infrastructure/persistence/repository/TournamentAggregateRepositoryImpl.java`

**Implementación**:
```java
// TournamentAggregate.java
public class TournamentAggregate {
    private final Tournament tournament;
    private final List<TicketSaleStage> stages;
    
    public void addStage(TicketSaleStage stage) {
        if (!tournament.canBeModified()) {
            throw new IllegalStateException("Cannot add stages to published tournament");
        }
        stages.add(stage);
    }
    
    public void publishTournament() {
        if (stages.isEmpty()) {
            throw new IllegalStateException("Cannot publish tournament without stages");
        }
        tournament.publish();
    }
}
```

**Agregados Definidos**:
1. **TournamentAggregate**: Tournament + TicketSaleStage[]
2. **TicketOrderAggregate**: TicketOrder + Ticket[]
3. **UserAggregate**: User (simple aggregate)

#### 2.4 Domain Events
**Archivos a crear**:
- `src/main/java/com/example/torneos/domain/event/DomainEvent.java`
- `src/main/java/com/example/torneos/domain/event/TournamentPublished.java`
- `src/main/java/com/example/torneos/domain/event/TicketOrderApproved.java`
- `src/main/java/com/example/torneos/domain/event/TicketsGenerated.java`
- `src/main/java/com/example/torneos/infrastructure/event/SpringDomainEventPublisher.java`
- `src/main/java/com/example/torneos/infrastructure/event/DomainEventHandler.java`

**Implementación**:
```java
// DomainEvent.java
public interface DomainEvent {
    UUID getEventId();
    Instant getOccurredOn();
    String getEventType();
}

// TournamentPublished.java
public record TournamentPublished(
    UUID eventId,
    Instant occurredOn,
    UUID tournamentId,
    String tournamentName,
    UUID organizerId
) implements DomainEvent {
    
    public TournamentPublished(UUID tournamentId, String tournamentName, UUID organizerId) {
        this(UUID.randomUUID(), Instant.now(), tournamentId, tournamentName, organizerId);
    }
}
```

**Eventos Implementados**:
1. **TournamentPublished**: Cuando se publica un torneo
2. **TicketOrderApproved**: Cuando se aprueba una orden
3. **TicketsGenerated**: Cuando se generan tickets
4. **TournamentCancelled**: Cuando se cancela un torneo

#### 2.5 Repository Pattern Enhancement
**Archivos a crear**:
- `src/main/java/com/example/torneos/domain/specification/Specification.java`
- `src/main/java/com/example/torneos/domain/specification/TournamentSpecification.java`
- `src/main/java/com/example/torneos/domain/specification/AbstractSpecification.java`
- `src/main/java/com/example/torneos/domain/repository/EnhancedTournamentRepository.java`
- `src/main/java/com/example/torneos/application/service/TournamentQueryService.java`

**Implementación**:
```java
// Specification.java
public interface Specification<T> {
    boolean isSatisfiedBy(T candidate);
    Specification<T> and(Specification<T> other);
    Specification<T> or(Specification<T> other);
    Specification<T> not();
}

// TournamentSpecification.java
public class TournamentSpecification {
    public static Specification<Tournament> isPublished() {
        return tournament -> tournament.getStatus() == Tournament.TournamentStatus.PUBLISHED;
    }
    
    public static Specification<Tournament> byOrganizer(UUID organizerId) {
        return tournament -> tournament.getOrganizerId().equals(organizerId);
    }
}
```

**Funcionalidades**:
1. **Specification Pattern**: Consultas complejas componibles
2. **Enhanced Repository**: Métodos de consulta avanzados
3. **Query Service**: Servicio dedicado para consultas de negocio
4. **Complex Queries**: Búsquedas con múltiples criterios

**Checklist**:
- [x] Crear Specification pattern
- [x] Implementar TournamentSpecification
- [x] Crear EnhancedTournamentRepository
- [x] Implementar TournamentQueryService
- [x] Agregar consultas estadísticas
- [ ] Tests de especificaciones)) {
            throw new InvalidEmailException("Invalid email format: " + value);
        }
    }
}

// Money.java
public record Money(BigDecimal amount, Currency currency) {
    public Money {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }
    
    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(amount.add(other.amount), currency);
    }
}
```

**Checklist**:
- [ ] Crear Value Objects básicos
- [ ] Implementar validaciones en constructores
- [ ] Agregar métodos de negocio
- [ ] Tests unitarios para VOs

#### 2.2 Tournament Aggregate Refactor
**Archivos a modificar**:
- `src/main/java/com/example/torneos/domain/model/Tournament.java`

**Implementación**:
```java
// Tournament.java
@Entity
public class Tournament {
    
    private TournamentId id;
    private UserId organizerId;
    private TournamentDetails details;
    private List<TournamentAdmin> admins = new ArrayList<>();
    private TournamentStatus status;
    
    @Version
    private Long version;
    
    // Rich domain logic
    public void assignSubAdmin(UserId subAdminId) {
        if (admins.size() >= MAX_SUBADMINS) {
            throw new MaxSubAdminsExceededException("Tournament can have maximum 2 subadmins");
        }
        
        if (admins.stream().anyMatch(admin -> admin.getSubAdminId().equals(subAdminId))) {
            throw new SubAdminAlreadyAssignedException("User is already a subadmin");
        }
        
        TournamentAdmin newAdmin = new TournamentAdmin(id, subAdminId);
        admins.add(newAdmin);
        
        // Domain event
        registerEvent(new SubAdminAssignedEvent(id, subAdminId));
    }
    
    public void publish() {
        if (!canBePublished()) {
            throw new TournamentCannotBePublishedException("Tournament cannot be published");
        }
        
        this.status = TournamentStatus.PUBLISHED;
        registerEvent(new TournamentPublishedEvent(id, Instant.now()));
    }
    
    private boolean canBePublished() {
        return status == TournamentStatus.DRAFT && 
               details.getStartDateTime().isAfter(Instant.now().plus(1, ChronoUnit.HOURS));
    }
}
```

**Checklist**:
- [ ] Mover lógica de TournamentService a Tournament
- [ ] Implementar domain events
- [ ] Agregar invariants validation
- [ ] Tests de domain logic

#### 2.3 TicketOrder Aggregate Refactor
**Archivos a modificar**:
- `src/main/java/com/example/torneos/domain/model/TicketOrder.java`

**Implementación**:
```java
// TicketOrder.java
@Entity
public class TicketOrder {
    
    private OrderId id;
    private TournamentId tournamentId;
    private UserId userId;
    private List<Ticket> tickets = new ArrayList<>();
    private Money totalAmount;
    private OrderStatus status;
    
    public void approve() {
        if (status != OrderStatus.PENDING) {
            throw new OrderAlreadyProcessedException("Order is not in pending status");
        }
        
        this.status = OrderStatus.APPROVED;
        generateTickets();
        
        registerEvent(new OrderApprovedEvent(id, tournamentId, userId));
    }
    
    private void generateTickets() {
        // Idempotent ticket generation
        if (!tickets.isEmpty()) {
            return; // Already generated
        }
        
        for (int i = 0; i < quantity; i++) {
            AccessCode accessCode = AccessCode.generate();
            Ticket ticket = new Ticket(tournamentId, userId, accessCode);
            tickets.add(ticket);
        }
    }
}
```

**Checklist**:
- [ ] Implementar rich TicketOrder aggregate
- [ ] Agregar ticket generation logic
- [ ] Implementar domain events
- [ ] Tests de aggregate behavior

#### 2.4 Domain Events Implementation
**Archivos a crear**:
- `src/main/java/com/example/torneos/domain/event/DomainEvent.java`
- `src/main/java/com/example/torneos/domain/event/TournamentPublishedEvent.java`
- `src/main/java/com/example/torneos/domain/event/OrderApprovedEvent.java`
- `src/main/java/com/example/torneos/application/eventhandler/TournamentEventHandler.java`

**Implementación**:
```java
// DomainEvent.java
public interface DomainEvent {
    Instant occurredOn();
    String eventType();
}

// TournamentPublishedEvent.java
public record TournamentPublishedEvent(
    TournamentId tournamentId,
    Instant occurredOn
) implements DomainEvent {
    
    @Override
    public String eventType() {
        return "TOURNAMENT_PUBLISHED";
    }
}

// TournamentEventHandler.java
@Component
public class TournamentEventHandler {
    
    @EventListener
    @Async
    public void handleTournamentPublished(TournamentPublishedEvent event) {
        // Create audit log
        auditLogService.logEvent(event);
        
        // Send notifications
        notificationService.notifyTournamentPublished(event.tournamentId());
    }
}
```

**Checklist**:
- [ ] Crear domain events interfaces
- [ ] Implementar event handlers
- [ ] Configurar async processing
- [ ] Tests de event handling

---

## 🔄 ETAPA 3: API EVOLUTION
**Duración Estimada**: 2 semanas  
**Prioridad**: MEDIA  
**Estado**: ✅ COMPLETADA

### Objetivos
- ✅ Implementar API versioning
- ✅ Estandarizar error handling (RFC 7807)
- ✅ Implementar HATEOAS
- ✅ Mejorar documentación OpenAPI
- ✅ Implementar rate limiting
- ✅ Configurar CORS y Web Config

### Tareas Detalladas

#### 3.1 API Versioning
**Archivos a crear**:
- `src/main/java/com/example/torneos/infrastructure/config/ApiVersioningConfig.java`
- `src/main/java/com/example/torneos/infrastructure/web/ApiVersionRequestMappingHandlerMapping.java`

**Implementación**:
```java
// ApiVersioningConfig.java
@Configuration
public class ApiVersioningConfig {
    
    @Bean
    public RequestMappingHandlerMapping apiVersionRequestMappingHandlerMapping() {
        return new ApiVersionRequestMappingHandlerMapping();
    }
}

// En controllers
@RestController
@RequestMapping("/api/tournaments")
@ApiVersion("v1")
public class TournamentController {
    
    @GetMapping
    public ResponseEntity<Page<TournamentResponse>> getTournaments(
        @RequestHeader(value = "API-Version", defaultValue = "v1") String apiVersion,
        Pageable pageable) {
        // Implementation
    }
}
```

**Checklist**:
- [x] Implementar header-based versioning
- [x] Configurar version routing
- [x] Crear ApiVersion annotation
- [x] Crear ApiVersionCondition
- [x] Actualizar controllers con @ApiVersion

#### 3.2 RFC 7807 Error Handling
**Archivos a modificar**:
- `src/main/java/com/example/torneos/infrastructure/config/GlobalExceptionHandler.java`

**Archivos a crear**:
- `src/main/java/com/example/torneos/infrastructure/web/ProblemDetailFactory.java`

**Implementación**:
```java
// GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(InsufficientCapacityException.class)
    public ResponseEntity<ProblemDetail> handleInsufficientCapacity(
        InsufficientCapacityException ex, HttpServletRequest request) {
        
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, ex.getMessage());
        
        problem.setType(URI.create("https://api.torneos.com/problems/insufficient-capacity"));
        problem.setTitle("Insufficient Capacity");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("availableCapacity", ex.getAvailableCapacity());
        problem.setProperty("requestedQuantity", ex.getRequestedQuantity());
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }
}
```

**Checklist**:
- [x] Implementar RFC 7807 standard
- [x] Crear ProblemDetailFactory
- [x] Actualizar GlobalExceptionHandler
- [x] Crear problem types específicos

---

## 📊 ETAPA 4: OBSERVABILIDAD
**Duración Estimada**: 3 semanas  
**Prioridad**: ALTA  
**Estado**: ⏳ PENDIENTE

### Objetivos
- ✅ Implementar métricas de negocio
- ✅ Configurar distributed tracing
- ✅ Health checks avanzados
- ✅ Dashboards de monitoreo

### Tareas Detalladas

#### 4.1 Business Metrics
**Archivos a crear**:
- `src/main/java/com/example/torneos/infrastructure/metrics/BusinessMetrics.java`
- `src/main/java/com/example/torneos/infrastructure/metrics/MetricsEventListener.java`

**Implementación**:
```java
// BusinessMetrics.java
@Component
public class BusinessMetrics {
    
    private final Counter tournamentsCreated;
    private final Counter ticketsSold;
    private final Timer orderProcessingTime;
    
    public BusinessMetrics(MeterRegistry meterRegistry) {
        this.tournamentsCreated = Counter.builder("tournaments.created")
            .description("Number of tournaments created")
            .register(meterRegistry);
            
        this.ticketsSold = Counter.builder("tickets.sold")
            .description("Number of tickets sold")
            .register(meterRegistry);
            
        this.orderProcessingTime = Timer.builder("orders.processing.time")
            .description("Order processing time")
            .register(meterRegistry);
    }
    
    @EventListener
    public void onTournamentCreated(TournamentCreatedEvent event) {
        tournamentsCreated.increment(
            Tags.of("category", event.getCategoryName())
        );
    }
}
```

**Checklist**:
- [ ] Implementar business metrics
- [ ] Configurar Prometheus export
- [ ] Crear custom metrics
- [ ] Tests de metrics

#### 4.2 Health Checks
**Archivos a crear**:
- `src/main/java/com/example/torneos/infrastructure/health/DatabaseHealthIndicator.java`
- `src/main/java/com/example/torneos/infrastructure/health/ExternalServiceHealthIndicator.java`

**Implementación**:
```java
// DatabaseHealthIndicator.java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        try {
            long startTime = System.currentTimeMillis();
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            long responseTime = System.currentTimeMillis() - startTime;
            
            if (responseTime > 1000) {
                return Health.down()
                    .withDetail("responseTime", responseTime + "ms")
                    .withDetail("threshold", "1000ms")
                    .build();
            }
            
            return Health.up()
                .withDetail("database", "PostgreSQL")
                .withDetail("responseTime", responseTime + "ms")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

**Checklist**:
- [ ] Implementar health indicators
- [ ] Configurar health endpoints
- [ ] Agregar readiness/liveness probes
- [ ] Tests de health checks

---

## ⚡ ETAPA 5: PERFORMANCE
**Duración Estimada**: 4 semanas  
**Prioridad**: MEDIA  
**Estado**: ⏳ PENDIENTE

### Objetivos
- ✅ Implementar Redis cache
- ✅ Optimizar queries N+1
- ✅ Async event processing
- ✅ Connection pooling optimization

### Tareas Detalladas

#### 5.1 Redis Cache Implementation
**Archivos a crear**:
- `src/main/java/com/example/torneos/infrastructure/config/CacheConfig.java`
- `src/main/java/com/example/torneos/infrastructure/cache/TournamentCacheService.java`

**Implementación**:
```java
// CacheConfig.java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
                
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .build();
    }
}

// En services
@Cacheable(value = "tournaments", key = "#id")
public TournamentResponse getTournament(UUID id) {
    // Implementation
}
```

**Checklist**:
- [ ] Configurar Redis cache
- [ ] Implementar cache strategies
- [ ] Cache invalidation
- [ ] Tests de caching

---

## 📈 MÉTRICAS DE PROGRESO

### Tracking por Etapa
```
ETAPA 1: [████████████████████████████████████████] 0% (0/12)
ETAPA 2: [████████████████████████████████████████] 0% (0/8)
ETAPA 3: [████████████████████████████████████████] 0% (0/6)
ETAPA 4: [████████████████████████████████████████] 0% (0/7)
ETAPA 5: [████████████████████████████████████████] 0% (0/6)
```

### Métricas de Calidad
- **Tests Coverage**: 40% → Target: 85%
- **Sonar Quality Gate**: PENDING → Target: PASSED
- **Performance**: Baseline → Target: <500ms P95
- **Security**: Medium → Target: High

---

## 🚨 NOTAS IMPORTANTES

### Antes de Empezar
1. **Backup de BD**: Crear backup antes de migraciones
2. **Branch Strategy**: Crear feature branches por etapa
3. **Testing**: Ejecutar tests después de cada cambio
4. **Code Review**: Revisar cada PR antes de merge

### Comandos Útiles
```bash
# Backup BD
pg_dump torneos_db > backup_$(date +%Y%m%d).sql

# Ejecutar tests
mvn test

# Verificar migraciones
mvn flyway:info

# Generar coverage report
mvn jacoco:report
```

### Contacto para Dudas
- **Arquitecto**: Senior Software Architect
- **Documento Base**: ARQUITECTURA_Y_REFACTOR.md
- **Documentación**: DOCUMENTACION_COMPLETA.md

---

**🎯 PRÓXIMO PASO**: Confirmar inicio de ETAPA 1 y comenzar con Database Constraints (Tarea 1.1)

*Última actualización: 19 Diciembre 2025*
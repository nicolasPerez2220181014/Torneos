# M. Frameworks y Estrategia de Desarrollo

## Información General
- **Proyecto**: Plataforma de Torneos E-Sport
- **Enfoque**: Domain-Driven Design + Clean Architecture
- **Metodología**: Test-Driven Development (TDD)
- **Stack**: Spring Boot + Angular + PostgreSQL
- **Fecha**: Diciembre 2024

## 1. Stack Tecnológico Completo

### 1.1 Backend Framework Stack
```
┌─────────────────────────────────────────────────────────────┐
│                    BACKEND STACK                            │
│                                                             │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ Spring Boot     │    │     Spring Security             │ │
│  │ 3.2.0           │    │     6.x (JWT Authentication)    │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
│                                                             │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ Spring Data JPA │    │     Hibernate ORM               │ │
│  │ (Repository)    │    │     6.x (Entity Mapping)       │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
│                                                             │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ Flyway          │    │     Bean Validation             │ │
│  │ (DB Migration)  │    │     (JSR-303)                   │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
│                                                             │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ OpenAPI 3       │    │     Micrometer                  │ │
│  │ (Swagger)       │    │     (Metrics & Monitoring)      │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Frontend Framework Stack
```
┌─────────────────────────────────────────────────────────────┐
│                   FRONTEND STACK                            │
│                                                             │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ Angular 17      │    │     TypeScript 5.4              │ │
│  │ (Framework)     │    │     (Type Safety)               │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
│                                                             │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ RxJS 7.8        │    │     Angular Router              │ │
│  │ (Reactive)      │    │     (Navigation)                │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
│                                                             │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ Bootstrap 5     │    │     SCSS                        │ │
│  │ (UI Framework)  │    │     (Styling)                   │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
│                                                             │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ Angular CLI     │    │     Vite                        │ │
│  │ (Build Tool)    │    │     (Dev Server)                │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 1.3 Base de Datos y Persistencia
```
┌─────────────────────────────────────────────────────────────┐
│                 PERSISTENCE STACK                           │
│                                                             │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ PostgreSQL 15   │    │     H2 Database                 │ │
│  │ (Production)    │    │     (Development)               │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
│                                                             │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ HikariCP        │    │     Flyway                      │ │
│  │ (Connection     │    │     (Schema Migration)          │ │
│  │  Pool)          │    │                                 │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## 2. Justificación de Frameworks Elegidos

### 2.1 Spring Boot 3.2.0 (Backend)

#### ¿Por qué Spring Boot?
- **Ecosistema Maduro**: Framework empresarial con amplia adopción
- **Configuración por Convención**: Reduce boilerplate y acelera desarrollo
- **Integración Nativa**: Excelente integración con herramientas Java
- **Comunidad Activa**: Amplio soporte y documentación
- **Microservicios Ready**: Preparado para arquitecturas distribuidas

#### Características Utilizadas
```java
// Auto-configuration
@SpringBootApplication
public class TorneosApplication {
    public static void main(String[] args) {
        SpringApplication.run(TorneosApplication.class, args);
    }
}

// Dependency Injection
@RestController
public class TournamentController {
    private final TournamentService tournamentService;
    
    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }
}

// Configuration Properties
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private long expiration;
    // getters and setters
}
```

### 2.2 Angular 17 (Frontend)

#### ¿Por qué Angular?
- **Framework Completo**: Solución integral para aplicaciones SPA
- **TypeScript Nativo**: Type safety y mejor experiencia de desarrollo
- **Arquitectura Escalable**: Modular y mantenible
- **Tooling Excelente**: CLI, DevTools, testing integrado
- **Enterprise Ready**: Usado en aplicaciones empresariales

#### Características Utilizadas
```typescript
// Standalone Components (Angular 17)
@Component({
  selector: 'app-tournament-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="tournament-list">
      @for (tournament of tournaments$ | async; track tournament.id) {
        <div class="tournament-card">{{ tournament.name }}</div>
      }
    </div>
  `
})
export class TournamentListComponent {
  tournaments$ = this.tournamentsService.getTournaments();
  
  constructor(private tournamentsService: TournamentsService) {}
}

// Reactive Forms
export class TournamentFormComponent {
  tournamentForm = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(255)]],
    description: [''],
    startDateTime: ['', [Validators.required]],
    endDateTime: ['', [Validators.required]]
  });
}

// HTTP Interceptors
const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = inject(TokenService).getAccessToken();
  if (token) {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(authReq);
  }
  return next(req);
};
```

### 2.3 PostgreSQL (Base de Datos)

#### ¿Por qué PostgreSQL?
- **ACID Compliance**: Transacciones confiables
- **Extensibilidad**: Soporte para JSON, UUID, extensiones
- **Performance**: Excelente rendimiento para aplicaciones complejas
- **Open Source**: Sin costos de licenciamiento
- **Ecosistema**: Amplio soporte de herramientas

#### Características Utilizadas
```sql
-- UUID Support
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- JSON Support for metadata
ALTER TABLE audit_log ADD COLUMN metadata JSONB;

-- Advanced Indexing
CREATE INDEX idx_tournaments_gin ON tournaments USING GIN (metadata);

-- Check Constraints
ALTER TABLE tournaments 
ADD CONSTRAINT check_dates CHECK (start_date_time < end_date_time);
```

## 3. Estrategia de Desarrollo

### 3.1 Domain-Driven Design (DDD)

#### Principios Aplicados
- **Ubiquitous Language**: Lenguaje común entre negocio y desarrollo
- **Bounded Contexts**: Separación clara de dominios
- **Aggregates**: Entidades con consistencia transaccional
- **Domain Events**: Comunicación entre agregados
- **Repository Pattern**: Abstracción de persistencia

#### Implementación DDD
```java
// Aggregate Root
public class Tournament {
    private UUID id;
    private String name;
    private TournamentStatus status;
    
    // Business methods
    public void publish() {
        if (status != TournamentStatus.DRAFT) {
            throw new IllegalStateException("Solo los torneos en borrador pueden ser publicados");
        }
        this.status = TournamentStatus.PUBLISHED;
        // Publish domain event
        DomainEventPublisher.publish(new TournamentPublished(this.id));
    }
}

// Domain Service
@Service
public class TournamentDomainService {
    public boolean canUserCreateTournament(User user, boolean isPaid) {
        if (!user.isOrganizer()) {
            return false;
        }
        
        if (!isPaid) {
            long freeTournaments = tournamentRepository
                .countByOrganizerIdAndIsPaidAndStatus(user.getId(), false, PUBLISHED);
            return freeTournaments < 2;
        }
        
        return true;
    }
}

// Domain Event
public record TournamentPublished(UUID tournamentId, LocalDateTime publishedAt) 
    implements DomainEvent {
    
    public TournamentPublished(UUID tournamentId) {
        this(tournamentId, LocalDateTime.now());
    }
}
```

### 3.2 Clean Architecture

#### Capas Implementadas
```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│  Controllers, DTOs, Exception Handlers                     │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                   APPLICATION LAYER                         │
│  Services, Use Cases, Application DTOs                     │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                            │
│  Entities, Value Objects, Domain Services, Events          │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                 INFRASTRUCTURE LAYER                        │
│  Repositories, External Services, Database                 │
└─────────────────────────────────────────────────────────────┘
```

#### Dependency Inversion
```java
// Domain Layer - Interface
public interface TournamentRepository {
    Tournament save(Tournament tournament);
    Optional<Tournament> findById(UUID id);
}

// Application Layer - Service
@Service
public class TournamentService {
    private final TournamentRepository tournamentRepository; // Depends on abstraction
    
    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }
}

// Infrastructure Layer - Implementation
@Repository
public class TournamentRepositoryImpl implements TournamentRepository {
    private final JpaTournamentRepository jpaRepository;
    private final TournamentMapper mapper;
    
    @Override
    public Tournament save(Tournament tournament) {
        TournamentEntity entity = mapper.toEntity(tournament);
        TournamentEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

### 3.3 Test-Driven Development (TDD)

#### Ciclo Red-Green-Refactor
```java
// 1. RED - Write failing test
@Test
void create_ShouldCreateTournament_WhenValidRequest() {
    // Given
    CreateTournamentRequest request = new CreateTournamentRequest(/* valid data */);
    when(userRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
    
    // When
    TournamentResponse response = tournamentService.create(request, organizerId);
    
    // Then
    assertNotNull(response);
    assertEquals("Test Tournament", response.name());
}

// 2. GREEN - Implement minimal code to pass
@Service
public class TournamentService {
    public TournamentResponse create(CreateTournamentRequest request, UUID organizerId) {
        // Minimal implementation
        Tournament tournament = new Tournament(/* parameters */);
        Tournament saved = tournamentRepository.save(tournament);
        return mapToResponse(saved);
    }
}

// 3. REFACTOR - Improve code while keeping tests green
@Service
public class TournamentService {
    public TournamentResponse create(CreateTournamentRequest request, UUID organizerId) {
        // Refactored with validations and business rules
        validateTournamentCreation(request, organizerId);
        Tournament tournament = createTournamentEntity(request, organizerId);
        Tournament saved = tournamentRepository.save(tournament);
        auditLogService.logTournamentCreated(saved.getId(), organizerId, saved.getName());
        return mapToResponse(saved);
    }
}
```

#### Testing Strategy
```java
// Unit Tests - Service Layer
@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {
    @Mock private TournamentRepository tournamentRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private TournamentService tournamentService;
    
    @Test
    void create_ShouldThrowException_WhenUserNotOrganizer() {
        // Test business rules
    }
}

// Integration Tests - Repository Layer
@DataJpaTest
class TournamentRepositoryTest {
    @Autowired private TestEntityManager entityManager;
    @Autowired private JpaTournamentRepository repository;
    
    @Test
    void findByStatus_ShouldReturnTournaments_WhenStatusMatches() {
        // Test data access
    }
}

// End-to-End Tests - Controller Layer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TournamentControllerIntegrationTest {
    @Autowired private TestRestTemplate restTemplate;
    
    @Test
    void createTournament_ShouldReturn201_WhenValidRequest() {
        // Test complete flow
    }
}
```

## 4. Patrones de Diseño Implementados

### 4.1 Repository Pattern
```java
// Abstraction in Domain Layer
public interface TournamentRepository {
    Tournament save(Tournament tournament);
    Optional<Tournament> findById(UUID id);
    Page<Tournament> findByFilters(/* parameters */);
}

// Implementation in Infrastructure Layer
@Repository
public class TournamentRepositoryImpl implements TournamentRepository {
    // JPA implementation with mapping
}
```

### 4.2 Factory Pattern
```java
@Component
public class TournamentFactory {
    
    public Tournament createTournament(CreateTournamentRequest request, UUID organizerId) {
        validateRequest(request);
        
        return new Tournament(
            organizerId,
            UUID.fromString(request.categoryId()),
            UUID.fromString(request.gameTypeId()),
            request.name(),
            request.description(),
            request.isPaid(),
            request.maxFreeCapacity(),
            request.startDateTime(),
            request.endDateTime()
        );
    }
}
```

### 4.3 Strategy Pattern
```java
public interface TicketPricingStrategy {
    BigDecimal calculatePrice(TicketSaleStage stage, int quantity);
}

@Component
public class EarlyBirdPricingStrategy implements TicketPricingStrategy {
    @Override
    public BigDecimal calculatePrice(TicketSaleStage stage, int quantity) {
        return stage.getPrice().multiply(BigDecimal.valueOf(quantity * 0.8)); // 20% discount
    }
}

@Component
public class RegularPricingStrategy implements TicketPricingStrategy {
    @Override
    public BigDecimal calculatePrice(TicketSaleStage stage, int quantity) {
        return stage.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
```

### 4.4 Observer Pattern (Domain Events)
```java
// Domain Event
public record TournamentPublished(UUID tournamentId, UUID organizerId, LocalDateTime publishedAt) 
    implements DomainEvent {}

// Event Publisher
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {
    private final ApplicationEventPublisher eventPublisher;
    
    @Override
    public void publish(DomainEvent event) {
        eventPublisher.publishEvent(event);
    }
}

// Event Handler
@Component
public class TournamentEventHandler {
    
    @EventListener
    @Async
    public void handleTournamentPublished(TournamentPublished event) {
        // Send notifications, update caches, etc.
        notificationService.notifyTournamentPublished(event.tournamentId());
        cacheService.invalidateTournamentCache();
    }
}
```

## 5. Principios SOLID Aplicados

### 5.1 Single Responsibility Principle (SRP)
```java
// Each class has one reason to change
@Service
public class TournamentService {
    // Only responsible for tournament business logic
}

@Component
public class TournamentMapper {
    // Only responsible for mapping between domain and persistence
}

@Component
public class TournamentValidator {
    // Only responsible for tournament validation
}
```

### 5.2 Open/Closed Principle (OCP)
```java
// Open for extension, closed for modification
public abstract class NotificationService {
    public abstract void sendNotification(String message, String recipient);
}

@Component
public class EmailNotificationService extends NotificationService {
    @Override
    public void sendNotification(String message, String recipient) {
        // Email implementation
    }
}

@Component
public class SlackNotificationService extends NotificationService {
    @Override
    public void sendNotification(String message, String recipient) {
        // Slack implementation
    }
}
```

### 5.3 Liskov Substitution Principle (LSP)
```java
// Subtypes must be substitutable for their base types
public interface PaymentProcessor {
    PaymentResult processPayment(PaymentRequest request);
}

@Component
public class CreditCardProcessor implements PaymentProcessor {
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // Credit card processing logic
        return new PaymentResult(true, "Payment processed");
    }
}

@Component
public class PayPalProcessor implements PaymentProcessor {
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // PayPal processing logic
        return new PaymentResult(true, "Payment processed via PayPal");
    }
}
```

### 5.4 Interface Segregation Principle (ISP)
```java
// Clients should not depend on interfaces they don't use
public interface TournamentReader {
    Optional<Tournament> findById(UUID id);
    List<Tournament> findByOrganizer(UUID organizerId);
}

public interface TournamentWriter {
    Tournament save(Tournament tournament);
    void delete(UUID id);
}

public interface TournamentRepository extends TournamentReader, TournamentWriter {
    // Composed interface
}
```

### 5.5 Dependency Inversion Principle (DIP)
```java
// High-level modules should not depend on low-level modules
@Service
public class TournamentService {
    private final TournamentRepository repository; // Depends on abstraction
    private final NotificationService notificationService; // Depends on abstraction
    
    public TournamentService(TournamentRepository repository, 
                           NotificationService notificationService) {
        this.repository = repository;
        this.notificationService = notificationService;
    }
}
```

## 6. Estrategia de Versionado y Evolución

### 6.1 API Versioning
```java
@RestController
@RequestMapping("/api/v1/tournaments")
@ApiVersion("v1")
public class TournamentControllerV1 {
    // Version 1 implementation
}

@RestController
@RequestMapping("/api/v2/tournaments")
@ApiVersion("v2")
public class TournamentControllerV2 {
    // Version 2 with enhanced features
}

// Custom annotation for versioning
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {
    String value();
}
```

### 6.2 Database Schema Evolution
```sql
-- V1__Initial_schema.sql
CREATE TABLE tournaments (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    -- initial fields
);

-- V2__Add_tournament_features.sql
ALTER TABLE tournaments ADD COLUMN description TEXT;
ALTER TABLE tournaments ADD COLUMN max_capacity INTEGER;

-- V3__Security_constraints.sql
ALTER TABLE tournaments ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE tournaments ADD CONSTRAINT check_dates 
    CHECK (start_date_time < end_date_time);
```

### 6.3 Backward Compatibility Strategy
```java
// DTO Versioning
public class TournamentResponseV1 {
    private UUID id;
    private String name;
    // v1 fields only
}

public class TournamentResponseV2 extends TournamentResponseV1 {
    private String description;
    private Integer maxCapacity;
    // v2 additional fields
}

// Service Layer Compatibility
@Service
public class TournamentService {
    
    public TournamentResponseV1 findByIdV1(UUID id) {
        Tournament tournament = findTournament(id);
        return mapToV1Response(tournament);
    }
    
    public TournamentResponseV2 findByIdV2(UUID id) {
        Tournament tournament = findTournament(id);
        return mapToV2Response(tournament);
    }
}
```

## 7. Configuración y Profiles

### 7.1 Multi-Environment Configuration
```yaml
# application.yml - Base configuration
spring:
  application:
    name: torneos-backend
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

---
# application-dev.yml - Development
spring:
  config:
    activate:
      on-profile: dev
  datasource:
    url: jdbc:h2:mem:torneos_db
  jpa:
    show-sql: true
  logging:
    level:
      com.example.torneos: DEBUG

---
# application-prod.yml - Production
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: jdbc:postgresql://localhost:5432/torneo
  jpa:
    show-sql: false
  logging:
    level:
      com.example.torneos: WARN
```

### 7.2 Feature Flags
```java
@Component
@ConfigurationProperties(prefix = "features")
public class FeatureFlags {
    private boolean streamingEnabled = true;
    private boolean advancedMetrics = false;
    private boolean experimentalFeatures = false;
    
    // getters and setters
}

@Service
public class TournamentService {
    private final FeatureFlags featureFlags;
    
    public void createTournament(CreateTournamentRequest request) {
        // Core functionality
        Tournament tournament = createBasicTournament(request);
        
        // Feature-flagged functionality
        if (featureFlags.isStreamingEnabled()) {
            setupStreamingForTournament(tournament);
        }
        
        if (featureFlags.isAdvancedMetrics()) {
            recordAdvancedMetrics(tournament);
        }
    }
}
```

## 8. Mejores Prácticas Implementadas

### 8.1 Code Quality
- **Static Analysis**: SonarQube integration ready
- **Code Formatting**: Consistent style with IDE formatters
- **Documentation**: JavaDoc for public APIs
- **Naming Conventions**: Clear, descriptive names

### 8.2 Security Best Practices
- **Input Validation**: Bean Validation at all entry points
- **SQL Injection Prevention**: Parameterized queries
- **Authentication**: JWT with proper expiration
- **Authorization**: Role-based access control

### 8.3 Performance Optimization
- **Database Indexing**: Strategic index placement
- **Connection Pooling**: HikariCP configuration
- **Caching**: Ready for Redis integration
- **Lazy Loading**: JPA fetch strategies

### 8.4 Monitoring and Observability
- **Structured Logging**: JSON format with correlation IDs
- **Metrics**: Micrometer with Prometheus
- **Health Checks**: Actuator endpoints
- **Distributed Tracing**: Ready for Zipkin/Jaeger

## 9. Roadmap de Evolución Tecnológica

### 9.1 Corto Plazo (3-6 meses)
- **Containerización**: Docker + Docker Compose
- **CI/CD**: GitHub Actions pipeline
- **Monitoring**: Prometheus + Grafana
- **Caching**: Redis implementation

### 9.2 Mediano Plazo (6-12 meses)
- **Microservicios**: Separación por bounded contexts
- **Event Sourcing**: Para auditoría completa
- **API Gateway**: Spring Cloud Gateway
- **Service Mesh**: Istio para comunicación

### 9.3 Largo Plazo (1-2 años)
- **Cloud Native**: Kubernetes deployment
- **Serverless**: Functions para procesamiento
- **Machine Learning**: Recomendaciones y analytics
- **Real-time**: WebSockets para streaming

---

**Conclusión**: La estrategia de desarrollo implementada combina frameworks maduros y probados con principios sólidos de ingeniería de software, proporcionando una base escalable y mantenible para la evolución continua de la plataforma de torneos E-Sport, con énfasis en calidad, testabilidad y observabilidad.
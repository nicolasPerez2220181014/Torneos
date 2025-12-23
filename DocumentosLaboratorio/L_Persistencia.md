# L. Persistencia de la Información

## Información General
- **Proyecto**: Plataforma de Torneos E-Sport
- **Motor de BD**: PostgreSQL (Producción) / H2 (Desarrollo)
- **ORM**: Spring Data JPA + Hibernate
- **Migración**: Flyway
- **Fecha**: Diciembre 2024

## 1. Estrategia de Persistencia

### 1.1 Arquitectura de Persistencia
```
┌─────────────────────────────────────────────────────────────┐
│                    DOMAIN LAYER                             │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ Domain Models   │    │     Repository Interfaces       │ │
│  │ (Pure Business  │    │     (Contracts)                 │ │
│  │  Logic)         │    │                                 │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                   │
                                   ▼ Dependency Inversion
┌─────────────────────────────────────────────────────────────┐
│                INFRASTRUCTURE LAYER                         │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ JPA Entities    │    │     Repository Implementations  │ │
│  │ (Persistence    │    │     (JPA + Custom Queries)      │ │
│  │  Model)         │    │                                 │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ Entity Mappers  │    │     JPA Repositories            │ │
│  │ (Domain ↔ JPA)  │    │     (Spring Data)               │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                   │
                                   ▼ JDBC/SQL
┌─────────────────────────────────────────────────────────────┐
│                    DATABASE LAYER                           │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ PostgreSQL      │    │     Connection Pool             │ │
│  │ Database        │    │     (HikariCP)                  │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Principios de Persistencia Aplicados
- **Separation of Concerns**: Separación entre modelo de dominio y persistencia
- **Repository Pattern**: Abstracción del acceso a datos
- **Domain-Driven Design**: Entidades ricas en comportamiento
- **ACID Compliance**: Transacciones confiables
- **Performance Optimization**: Índices y consultas optimizadas

## 2. Configuración de Base de Datos

### 2.1 Configuración PostgreSQL (Producción)
```yaml
# application-postgres.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/torneo
    username: postgres
    password: 1234
    driver-class-name: org.postgresql.Driver
    
    # Connection Pool Configuration (HikariCP)
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      max-lifetime: 1200000
      connection-timeout: 20000
      leak-detection-threshold: 60000
  
  jpa:
    hibernate:
      ddl-auto: validate  # No auto-schema generation in production
    show-sql: false      # Disable SQL logging in production
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        jdbc:
          batch_size: 20
          order_inserts: true
          order_updates: true
        cache:
          use_second_level_cache: true
          region:
            factory_class: org.hibernate.cache.jcache.JCacheRegionFactory
```

### 2.2 Configuración H2 (Desarrollo)
```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:h2:mem:torneos_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  
  h2:
    console:
      enabled: true
      path: /h2-console
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
        format_sql: true
```

### 2.3 Configuración de Flyway
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
    clean-disabled: true  # Prevent accidental data loss
    schemas: public
    table: flyway_schema_history
```

## 3. Modelo de Entidades JPA

### 3.1 Entidad Base
```java
@MappedSuperclass
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;  // Optimistic locking
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and setters
}
```

### 3.2 TournamentEntity (Ejemplo Principal)
```java
@Entity
@Table(name = "tournaments", indexes = {
    @Index(name = "idx_tournaments_organizer_status", columnList = "organizer_id, status"),
    @Index(name = "idx_tournaments_category", columnList = "category_id"),
    @Index(name = "idx_tournaments_game_type", columnList = "game_type_id"),
    @Index(name = "idx_tournaments_dates", columnList = "start_date_time, end_date_time")
})
public class TournamentEntity extends BaseEntity {
    
    @Column(name = "organizer_id", nullable = false)
    private UUID organizerId;
    
    @Column(name = "category_id", nullable = false)
    private UUID categoryId;
    
    @Column(name = "game_type_id", nullable = false)
    private UUID gameTypeId;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_paid", nullable = false)
    private boolean isPaid = false;
    
    @Column(name = "max_free_capacity")
    private Integer maxFreeCapacity;
    
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;
    
    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TournamentStatus status = TournamentStatus.DRAFT;
    
    // Foreign key relationships (lazy loading)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", insertable = false, updatable = false)
    private UserEntity organizer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private CategoryEntity category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_type_id", insertable = false, updatable = false)
    private GameTypeEntity gameType;
    
    // One-to-many relationships
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketSaleStageEntity> ticketSaleStages = new ArrayList<>();
    
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<TicketOrderEntity> ticketOrders = new ArrayList<>();
    
    public enum TournamentStatus {
        DRAFT, PUBLISHED, FINISHED, CANCELLED
    }
}
```

### 3.3 Constraints y Validaciones a Nivel de BD
```sql
-- V3__Security_constraints.sql

-- Business constraints
ALTER TABLE tournaments 
ADD CONSTRAINT check_start_before_end 
CHECK (start_date_time < end_date_time);

ALTER TABLE ticket_sale_stages 
ADD CONSTRAINT check_capacity_positive CHECK (capacity > 0);

ALTER TABLE ticket_sale_stages 
ADD CONSTRAINT check_price_non_negative CHECK (price >= 0);

ALTER TABLE ticket_orders 
ADD CONSTRAINT check_quantity_positive CHECK (quantity > 0);

-- Unique constraints
ALTER TABLE tournament_admins 
ADD CONSTRAINT unique_tournament_admin 
UNIQUE (tournament_id, sub_admin_user_id);

ALTER TABLE users 
ADD CONSTRAINT unique_user_email 
UNIQUE (email);
```

## 4. Patrón Repository

### 4.1 Repository Interface (Domain Layer)
```java
// Domain repository interface
public interface TournamentRepository {
    
    Tournament save(Tournament tournament);
    
    Optional<Tournament> findById(UUID id);
    
    Page<Tournament> findAll(Pageable pageable);
    
    List<Tournament> findByOrganizerId(UUID organizerId);
    
    List<Tournament> findByStatus(Tournament.TournamentStatus status);
    
    Page<Tournament> findByFilters(Boolean isPaid, 
                                 Tournament.TournamentStatus status, 
                                 UUID categoryId, 
                                 UUID gameTypeId, 
                                 UUID organizerId, 
                                 Pageable pageable);
    
    void deleteById(UUID id);
    
    long countByOrganizerIdAndIsPaidAndStatus(UUID organizerId, 
                                            boolean isPaid, 
                                            Tournament.TournamentStatus status);
}
```

### 4.2 JPA Repository (Infrastructure Layer)
```java
@Repository
public interface JpaTournamentRepository extends JpaRepository<TournamentEntity, UUID> {
    
    List<TournamentEntity> findByOrganizerId(UUID organizerId);
    
    List<TournamentEntity> findByStatus(TournamentEntity.TournamentStatus status);
    
    long countByOrganizerIdAndIsPaidAndStatus(UUID organizerId, 
                                            boolean isPaid, 
                                            TournamentEntity.TournamentStatus status);
    
    @Query("""
        SELECT t FROM TournamentEntity t 
        WHERE (:isPaid IS NULL OR t.isPaid = :isPaid)
        AND (:status IS NULL OR t.status = :status)
        AND (:categoryId IS NULL OR t.categoryId = :categoryId)
        AND (:gameTypeId IS NULL OR t.gameTypeId = :gameTypeId)
        AND (:organizerId IS NULL OR t.organizerId = :organizerId)
        ORDER BY t.createdAt DESC
        """)
    Page<TournamentEntity> findByFilters(@Param("isPaid") Boolean isPaid,
                                       @Param("status") TournamentEntity.TournamentStatus status,
                                       @Param("categoryId") UUID categoryId,
                                       @Param("gameTypeId") UUID gameTypeId,
                                       @Param("organizerId") UUID organizerId,
                                       Pageable pageable);
    
    @Query("""
        SELECT t FROM TournamentEntity t 
        JOIN FETCH t.organizer 
        JOIN FETCH t.category 
        JOIN FETCH t.gameType 
        WHERE t.id = :id
        """)
    Optional<TournamentEntity> findByIdWithDetails(@Param("id") UUID id);
    
    @Modifying
    @Query("UPDATE TournamentEntity t SET t.status = :status WHERE t.id = :id")
    int updateStatus(@Param("id") UUID id, @Param("status") TournamentEntity.TournamentStatus status);
}
```

### 4.3 Repository Implementation (Infrastructure Layer)
```java
@Repository
public class TournamentRepositoryImpl implements TournamentRepository {

    private final JpaTournamentRepository jpaRepository;
    private final TournamentMapper mapper;

    public TournamentRepositoryImpl(JpaTournamentRepository jpaRepository, TournamentMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

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

    @Override
    public Page<Tournament> findByFilters(Boolean isPaid, Tournament.TournamentStatus status, 
                                        UUID categoryId, UUID gameTypeId, UUID organizerId, 
                                        Pageable pageable) {
        TournamentEntity.TournamentStatus entityStatus = status != null ? 
            TournamentEntity.TournamentStatus.valueOf(status.name()) : null;
        
        return jpaRepository.findByFilters(isPaid, entityStatus, categoryId, gameTypeId, organizerId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public long countByOrganizerIdAndIsPaidAndStatus(UUID organizerId, boolean isPaid, 
                                                   Tournament.TournamentStatus status) {
        TournamentEntity.TournamentStatus entityStatus = 
            TournamentEntity.TournamentStatus.valueOf(status.name());
        return jpaRepository.countByOrganizerIdAndIsPaidAndStatus(organizerId, isPaid, entityStatus);
    }
}
```

## 5. Mappers (Domain ↔ Persistence)

### 5.1 TournamentMapper
```java
@Component
public class TournamentMapper {
    
    public Tournament toDomain(TournamentEntity entity) {
        if (entity == null) return null;
        
        Tournament tournament = new Tournament(
            entity.getOrganizerId(),
            entity.getCategoryId(),
            entity.getGameTypeId(),
            entity.getName(),
            entity.getDescription(),
            entity.isPaid(),
            entity.getMaxFreeCapacity(),
            entity.getStartDateTime(),
            entity.getEndDateTime()
        );
        
        tournament.setId(entity.getId());
        tournament.setStatus(Tournament.TournamentStatus.valueOf(entity.getStatus().name()));
        tournament.setCreatedAt(entity.getCreatedAt());
        tournament.setUpdatedAt(entity.getUpdatedAt());
        
        return tournament;
    }
    
    public TournamentEntity toEntity(Tournament domain) {
        if (domain == null) return null;
        
        TournamentEntity entity = new TournamentEntity();
        entity.setId(domain.getId());
        entity.setOrganizerId(domain.getOrganizerId());
        entity.setCategoryId(domain.getCategoryId());
        entity.setGameTypeId(domain.getGameTypeId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPaid(domain.isPaid());
        entity.setMaxFreeCapacity(domain.getMaxFreeCapacity());
        entity.setStartDateTime(domain.getStartDateTime());
        entity.setEndDateTime(domain.getEndDateTime());
        entity.setStatus(TournamentEntity.TournamentStatus.valueOf(domain.getStatus().name()));
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        
        return entity;
    }
}
```

## 6. Gestión de Transacciones

### 6.1 Configuración de Transacciones
```java
@Configuration
@EnableTransactionManagement
public class TransactionConfig {
    
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }
}
```

### 6.2 Uso de Transacciones en Servicios
```java
@Service
@Transactional
public class TournamentService {
    
    @Transactional(readOnly = true)
    public TournamentResponse findById(UUID id) {
        Tournament tournament = tournamentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Tournament not found: " + id));
        return mapToResponse(tournament);
    }
    
    @Transactional
    public TournamentResponse create(CreateTournamentRequest request, UUID organizerId) {
        // Multiple operations in single transaction
        validateTournamentCreation(request, organizerId);
        
        Tournament tournament = createTournamentEntity(request, organizerId);
        Tournament savedTournament = tournamentRepository.save(tournament);
        
        // Audit logging (same transaction)
        auditLogService.logTournamentCreated(
            savedTournament.getId(), 
            organizerId, 
            savedTournament.getName()
        );
        
        return mapToResponse(savedTournament);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishTournament(UUID tournamentId, UUID userId) {
        // New transaction for critical operations
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));
            
        tournament.publish();
        tournamentRepository.save(tournament);
        
        // Publish domain event
        domainEventPublisher.publish(new TournamentPublished(tournamentId, userId));
    }
}
```

### 6.3 Manejo de Concurrencia (Optimistic Locking)
```java
@Entity
public class TournamentEntity extends BaseEntity {
    
    @Version
    private Long version;  // Optimistic locking
    
    // Other fields...
}

// Service handling optimistic locking
@Service
public class TournamentService {
    
    @Transactional
    public TournamentResponse update(UUID id, UpdateTournamentRequest request, UUID userId) {
        try {
            Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));
                
            tournament.updateDetails(request.name(), request.description(), request.maxFreeCapacity());
            Tournament savedTournament = tournamentRepository.save(tournament);
            
            return mapToResponse(savedTournament);
            
        } catch (OptimisticLockingFailureException e) {
            throw new IllegalStateException("Tournament was modified by another user. Please refresh and try again.");
        }
    }
}
```

## 7. Integridad de Datos

### 7.1 Constraints de Base de Datos
```sql
-- Foreign Key Constraints
ALTER TABLE tournaments 
ADD CONSTRAINT fk_tournaments_organizer 
FOREIGN KEY (organizer_id) REFERENCES users(id);

ALTER TABLE tournaments 
ADD CONSTRAINT fk_tournaments_category 
FOREIGN KEY (category_id) REFERENCES categories(id);

ALTER TABLE tournaments 
ADD CONSTRAINT fk_tournaments_game_type 
FOREIGN KEY (game_type_id) REFERENCES game_types(id);

-- Check Constraints
ALTER TABLE tournaments 
ADD CONSTRAINT check_tournament_dates 
CHECK (start_date_time < end_date_time);

ALTER TABLE tournaments 
ADD CONSTRAINT check_free_capacity 
CHECK (max_free_capacity IS NULL OR max_free_capacity > 0);

-- Unique Constraints
ALTER TABLE users 
ADD CONSTRAINT unique_email UNIQUE (email);

ALTER TABLE tournament_admins 
ADD CONSTRAINT unique_tournament_subadmin 
UNIQUE (tournament_id, sub_admin_user_id);
```

### 7.2 Validaciones JPA
```java
@Entity
@Table(name = "tournaments")
public class TournamentEntity extends BaseEntity {
    
    @NotNull(message = "Tournament name is required")
    @Size(min = 3, max = 255, message = "Tournament name must be between 3 and 255 characters")
    @Column(nullable = false, length = 255)
    private String name;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;
    
    @NotNull(message = "End date is required")
    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;
    
    @Min(value = 1, message = "Free capacity must be at least 1")
    @Max(value = 10000, message = "Free capacity cannot exceed 10000")
    @Column(name = "max_free_capacity")
    private Integer maxFreeCapacity;
}
```

### 7.3 Validaciones Personalizadas
```java
@ValidTournamentDates
public class CreateTournamentRequest {
    // Fields...
}

@Constraint(validatedBy = TournamentDateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTournamentDates {
    String message() default "End date must be after start date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class TournamentDateValidator implements ConstraintValidator<ValidTournamentDates, CreateTournamentRequest> {
    
    @Override
    public boolean isValid(CreateTournamentRequest request, ConstraintValidatorContext context) {
        if (request.startDateTime() == null || request.endDateTime() == null) {
            return true; // Let @NotNull handle null validation
        }
        
        return request.endDateTime().isAfter(request.startDateTime());
    }
}
```

## 8. Performance y Optimización

### 8.1 Índices de Base de Datos
```sql
-- Performance indexes
CREATE INDEX idx_tournaments_organizer_status 
ON tournaments(organizer_id, status);

CREATE INDEX idx_tournaments_category_status 
ON tournaments(category_id, status);

CREATE INDEX idx_tournaments_dates 
ON tournaments(start_date_time, end_date_time);

CREATE INDEX idx_ticket_orders_tournament_status 
ON ticket_orders(tournament_id, status);

CREATE INDEX idx_tickets_tournament_user 
ON tickets(tournament_id, user_id);

CREATE INDEX idx_audit_log_entity_date 
ON audit_log(entity_type, entity_id, created_at);

-- Partial indexes for active data
CREATE INDEX idx_tournaments_active 
ON tournaments(created_at) 
WHERE status IN ('DRAFT', 'PUBLISHED');
```

### 8.2 Query Optimization
```java
@Repository
public interface JpaTournamentRepository extends JpaRepository<TournamentEntity, UUID> {
    
    // Fetch join to avoid N+1 queries
    @Query("""
        SELECT DISTINCT t FROM TournamentEntity t 
        LEFT JOIN FETCH t.organizer 
        LEFT JOIN FETCH t.category 
        LEFT JOIN FETCH t.gameType 
        WHERE t.status = :status
        """)
    List<TournamentEntity> findByStatusWithDetails(@Param("status") TournamentEntity.TournamentStatus status);
    
    // Projection for lightweight queries
    @Query("""
        SELECT new com.example.torneos.dto.TournamentSummaryDto(
            t.id, t.name, t.status, t.startDateTime, t.endDateTime
        ) FROM TournamentEntity t 
        WHERE t.organizerId = :organizerId
        """)
    List<TournamentSummaryDto> findSummaryByOrganizerId(@Param("organizerId") UUID organizerId);
    
    // Native query for complex operations
    @Query(value = """
        SELECT t.* FROM tournaments t 
        WHERE t.start_date_time BETWEEN :startDate AND :endDate 
        AND EXISTS (
            SELECT 1 FROM ticket_orders o 
            WHERE o.tournament_id = t.id 
            AND o.status = 'APPROVED'
        )
        """, nativeQuery = true)
    List<TournamentEntity> findTournamentsWithApprovedOrders(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
```

### 8.3 Connection Pool Configuration
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20        # Maximum connections
      minimum-idle: 5              # Minimum idle connections
      idle-timeout: 300000         # 5 minutes
      max-lifetime: 1200000        # 20 minutes
      connection-timeout: 20000    # 20 seconds
      leak-detection-threshold: 60000  # 1 minute
      pool-name: TorneosHikariPool
      
      # Performance tuning
      cache-prep-stmts: true
      prep-stmt-cache-size: 250
      prep-stmt-cache-sql-limit: 2048
      use-server-prep-stmts: true
```

## 9. Backup y Recuperación

### 9.1 Estrategia de Backup
```bash
#!/bin/bash
# backup-database.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/opt/backups/database"
DB_NAME="torneo_prod"
DB_USER="postgres"
DB_HOST="localhost"

# Full database backup
pg_dump -h $DB_HOST -U $DB_USER -d $DB_NAME \
  --verbose --clean --no-owner --no-privileges \
  --format=custom \
  --file=$BACKUP_DIR/full_backup_$DATE.dump

# Compress backup
gzip $BACKUP_DIR/full_backup_$DATE.dump

# Schema-only backup
pg_dump -h $DB_HOST -U $DB_USER -d $DB_NAME \
  --schema-only --verbose \
  --file=$BACKUP_DIR/schema_backup_$DATE.sql

# Data-only backup for critical tables
pg_dump -h $DB_HOST -U $DB_USER -d $DB_NAME \
  --data-only --verbose \
  --table=tournaments --table=users --table=audit_log \
  --file=$BACKUP_DIR/critical_data_$DATE.sql

# Upload to cloud storage
aws s3 cp $BACKUP_DIR/full_backup_$DATE.dump.gz \
  s3://torneos-backups/database/$(date +%Y/%m/%d)/

# Cleanup old backups (keep last 30 days)
find $BACKUP_DIR -name "*.dump.gz" -mtime +30 -delete
```

### 9.2 Point-in-Time Recovery
```bash
# Enable WAL archiving in postgresql.conf
wal_level = replica
archive_mode = on
archive_command = 'cp %p /opt/postgres/archive/%f'
max_wal_senders = 3
checkpoint_completion_target = 0.9

# Recovery script
#!/bin/bash
# restore-database.sh

BACKUP_FILE=$1
RECOVERY_TARGET_TIME=$2

# Stop PostgreSQL
systemctl stop postgresql

# Restore base backup
pg_basebackup -h localhost -U postgres -D /var/lib/postgresql/data_recovery

# Create recovery configuration
cat > /var/lib/postgresql/data_recovery/recovery.conf << EOF
restore_command = 'cp /opt/postgres/archive/%f %p'
recovery_target_time = '$RECOVERY_TARGET_TIME'
recovery_target_action = 'promote'
EOF

# Start PostgreSQL with recovery data directory
systemctl start postgresql
```

## 10. Monitoreo de Base de Datos

### 10.1 Métricas de Performance
```java
@Component
public class DatabaseMetrics {
    
    private final DataSource dataSource;
    private final MeterRegistry meterRegistry;
    
    public DatabaseMetrics(DataSource dataSource, MeterRegistry meterRegistry) {
        this.dataSource = dataSource;
        this.meterRegistry = meterRegistry;
        
        // Register custom gauges
        Gauge.builder("database.connections.active")
            .description("Active database connections")
            .register(meterRegistry, this, DatabaseMetrics::getActiveConnections);
            
        Gauge.builder("database.connections.idle")
            .description("Idle database connections")
            .register(meterRegistry, this, DatabaseMetrics::getIdleConnections);
    }
    
    private double getActiveConnections(DatabaseMetrics metrics) {
        if (dataSource instanceof HikariDataSource hikariDS) {
            return hikariDS.getHikariPoolMXBean().getActiveConnections();
        }
        return 0;
    }
    
    private double getIdleConnections(DatabaseMetrics metrics) {
        if (dataSource instanceof HikariDataSource hikariDS) {
            return hikariDS.getHikariPoolMXBean().getIdleConnections();
        }
        return 0;
    }
}
```

### 10.2 Health Checks de Base de Datos
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    private final DataSource dataSource;
    
    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            // Test connection validity
            if (connection.isValid(1)) {
                // Get additional metrics
                DatabaseMetaData metaData = connection.getMetaData();
                
                return Health.up()
                    .withDetail("database", metaData.getDatabaseProductName())
                    .withDetail("version", metaData.getDatabaseProductVersion())
                    .withDetail("url", metaData.getURL())
                    .withDetail("validationQuery", "SELECT 1")
                    .build();
            }
        } catch (SQLException e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .withException(e)
                .build();
        }
        
        return Health.down()
            .withDetail("error", "Connection validation failed")
            .build();
    }
}
```

---

**Conclusión**: La estrategia de persistencia implementada proporciona una base sólida y escalable para la plataforma de torneos E-Sport, con separación clara entre dominio y persistencia, optimizaciones de performance, integridad de datos garantizada y estrategias robustas de backup y recuperación.
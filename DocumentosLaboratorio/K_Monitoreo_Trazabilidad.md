# K. Monitoreo y Trazabilidad

## Información General
- **Proyecto**: Plataforma de Torneos E-Sport
- **Enfoque**: Observabilidad Completa (Logs, Métricas, Trazas)
- **Herramientas**: Spring Boot Actuator, Micrometer, SLF4J, AuditLog
- **Fecha**: Diciembre 2024

## 1. Arquitectura de Observabilidad

### 1.1 Los Tres Pilares de la Observabilidad
```
┌─────────────────────────────────────────────────────────────┐
│                        LOGS                                 │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ Application     │    │     Audit Logs                 │ │
│  │ Logs            │    │     (Business Events)          │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                      METRICS                                │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ Business        │    │     Technical Metrics          │ │
│  │ Metrics         │    │     (Performance, Health)       │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                      TRACES                                 │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ Request         │    │     Correlation IDs             │ │
│  │ Tracing         │    │     (End-to-End Tracking)      │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Flujo de Observabilidad
```
User Request → Frontend → Backend → Database
     │            │         │         │
     ▼            ▼         ▼         ▼
  Browser      Angular   Spring    PostgreSQL
   Logs        Logs      Logs       Logs
     │            │         │         │
     └────────────┼─────────┼─────────┘
                  │         │
                  ▼         ▼
            Correlation ID Chain
                  │
                  ▼
          Centralized Logging
          (ELK Stack / Loki)
```

## 2. Sistema de Logs

### 2.1 Configuración de Logging (Backend)
```yaml
# application.yml
logging:
  level:
    com.example.torneos: INFO
    org.springframework.web: DEBUG
    org.springframework.security: DEBUG
    AUDIT: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{correlationId}] [%X{userId}] %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{correlationId}] [%X{userId}] %logger{36} - %msg%n"
  file:
    name: logs/torneos-backend.log
    max-size: 100MB
    max-history: 30

# Configuración específica para auditoría
  loggers:
    AUDIT:
      level: INFO
      additivity: false
      appenders:
        - name: AUDIT_FILE
          type: RollingFile
          fileName: logs/audit.log
          filePattern: logs/audit-%d{yyyy-MM-dd}.log.gz
```

### 2.2 Structured Logging con MDC
```java
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String USER_ID_HEADER = "X-USER-ID";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Generate or extract correlation ID
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        
        // Extract user ID if present
        String userId = request.getHeader(USER_ID_HEADER);
        
        // Add to MDC for structured logging
        MDC.put("correlationId", correlationId);
        MDC.put("requestMethod", request.getMethod());
        MDC.put("requestUri", request.getRequestURI());
        
        if (userId != null && !userId.isEmpty()) {
            MDC.put("userId", userId);
        }
        
        // Add correlation ID to response
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        // Log request completion
        long duration = System.currentTimeMillis() - (Long) request.getAttribute("startTime");
        
        if (ex != null) {
            log.error("Request completed with error: status={}, duration={}ms, error={}", 
                response.getStatus(), duration, ex.getMessage());
        } else {
            log.info("Request completed: status={}, duration={}ms", 
                response.getStatus(), duration);
        }
        
        // Clean up MDC
        MDC.clear();
    }
}
```

### 2.3 Ejemplo de Log Estructurado
```json
{
  "timestamp": "2024-12-01T10:30:45.123Z",
  "level": "INFO",
  "logger": "com.example.torneos.application.service.TournamentService",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "requestMethod": "POST",
  "requestUri": "/api/tournaments",
  "message": "Tournament created successfully",
  "tournamentId": "789e0123-e45b-67c8-d901-234567890123",
  "tournamentName": "Valorant Championship 2024",
  "organizerId": "123e4567-e89b-12d3-a456-426614174000"
}
```

## 3. Sistema de Auditoría

### 3.1 AuditLog Entity (Base de Datos)
```java
@Entity
@Table(name = "audit_log")
public class AuditLog {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private EntityType entityType;
    
    @Column(name = "entity_id", nullable = false)
    private UUID entityId;
    
    @Column(name = "actor_user_id")
    private UUID actorUserId;
    
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Enums para tipos de eventos y entidades
    public enum EventType {
        // Eventos de Torneos
        TOURNAMENT_CREATED,
        TOURNAMENT_UPDATED,
        TOURNAMENT_PUBLISHED,
        TOURNAMENT_CANCELLED,
        TOURNAMENT_FINISHED,
        
        // Eventos de Usuarios
        USER_CREATED,
        USER_UPDATED,
        USER_LOGIN,
        USER_LOGOUT,
        
        // Eventos de Tickets
        TICKET_ORDER_CREATED,
        TICKET_ORDER_APPROVED,
        TICKET_ORDER_REJECTED,
        TICKET_VALIDATED,
        
        // Eventos de Streaming
        STREAM_ACCESS_GRANTED,
        STREAM_BLOCKED,
        STREAM_UNBLOCKED,
        
        // Eventos de Seguridad
        UNAUTHORIZED_ACCESS_ATTEMPT,
        INVALID_TOKEN_USED,
        SUSPICIOUS_ACTIVITY
    }
    
    public enum EntityType {
        TOURNAMENT,
        USER,
        TICKET,
        ORDER,
        STREAM,
        CATEGORY,
        GAME_TYPE
    }
}
```

### 3.2 AuditService Implementation
```java
@Service
public class AuditService {
    
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private final AuditLogRepository auditLogRepository;
    
    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    
    public void logEvent(AuditLog.EventType eventType, 
                        AuditLog.EntityType entityType,
                        UUID entityId, 
                        UUID actorUserId, 
                        String metadata) {
        
        // Log to database
        AuditLog auditLog = new AuditLog(eventType, entityType, entityId, actorUserId, metadata);
        auditLogRepository.save(auditLog);
        
        // Log to file for real-time monitoring
        auditLogger.info("AUDIT_EVENT: type={}, entity={}, entityId={}, actor={}, metadata={}, timestamp={}", 
            eventType, entityType, entityId, actorUserId, metadata, Instant.now());
    }
    
    // Métodos específicos para eventos de negocio
    public void logTournamentCreated(UUID tournamentId, UUID organizerId, String tournamentName) {
        String metadata = String.format("{\"name\":\"%s\",\"action\":\"created\"}", tournamentName);
        logEvent(AuditLog.EventType.TOURNAMENT_CREATED, AuditLog.EntityType.TOURNAMENT, 
                tournamentId, organizerId, metadata);
    }
    
    public void logTicketOrderApproved(UUID orderId, UUID approvedBy, int ticketCount) {
        String metadata = String.format("{\"ticketCount\":%d,\"action\":\"approved\"}", ticketCount);
        logEvent(AuditLog.EventType.TICKET_ORDER_APPROVED, AuditLog.EntityType.ORDER, 
                orderId, approvedBy, metadata);
    }
    
    public void logSecurityEvent(String eventType, String details, UUID userId, String ipAddress) {
        String metadata = String.format("{\"details\":\"%s\",\"ipAddress\":\"%s\"}", details, ipAddress);
        logEvent(AuditLog.EventType.UNAUTHORIZED_ACCESS_ATTEMPT, AuditLog.EntityType.USER, 
                userId, userId, metadata);
    }
}
```

### 3.3 Uso de Auditoría en Servicios
```java
@Service
@Transactional
public class TournamentService {
    
    private final AuditService auditService;
    
    public TournamentResponse create(CreateTournamentRequest request, UUID organizerId) {
        // Lógica de creación del torneo
        Tournament tournament = createTournamentEntity(request, organizerId);
        Tournament savedTournament = tournamentRepository.save(tournament);
        
        // Auditoría del evento
        auditService.logTournamentCreated(
            savedTournament.getId(), 
            organizerId, 
            savedTournament.getName()
        );
        
        return mapToResponse(savedTournament);
    }
    
    public TournamentResponse publish(UUID tournamentId, UUID userId) {
        Tournament tournament = findTournamentById(tournamentId);
        tournament.publish();
        Tournament savedTournament = tournamentRepository.save(tournament);
        
        // Auditoría del evento
        auditService.logEvent(
            AuditLog.EventType.TOURNAMENT_PUBLISHED,
            AuditLog.EntityType.TOURNAMENT,
            tournamentId,
            userId,
            String.format("{\"name\":\"%s\",\"publishedAt\":\"%s\"}", 
                tournament.getName(), LocalDateTime.now())
        );
        
        return mapToResponse(savedTournament);
    }
}
```

## 4. Métricas de Negocio

### 4.1 BusinessMetrics Component
```java
@Component
public class BusinessMetrics {
    
    private final Counter tournamentsCreated;
    private final Counter tournamentsPublished;
    private final Counter ticketOrdersCreated;
    private final Counter ticketOrdersApproved;
    private final Timer orderProcessingTime;
    private final Gauge activeTournaments;
    
    public BusinessMetrics(MeterRegistry meterRegistry, TournamentRepository tournamentRepository) {
        this.tournamentsCreated = Counter.builder("tournaments.created.total")
            .description("Total tournaments created")
            .tag("type", "business")
            .register(meterRegistry);
            
        this.tournamentsPublished = Counter.builder("tournaments.published.total")
            .description("Total tournaments published")
            .tag("type", "business")
            .register(meterRegistry);
            
        this.ticketOrdersCreated = Counter.builder("ticket.orders.created.total")
            .description("Total ticket orders created")
            .tag("type", "business")
            .register(meterRegistry);
            
        this.ticketOrdersApproved = Counter.builder("ticket.orders.approved.total")
            .description("Total ticket orders approved")
            .tag("type", "business")
            .register(meterRegistry);
            
        this.orderProcessingTime = Timer.builder("order.processing.duration")
            .description("Time to process ticket orders")
            .tag("type", "performance")
            .register(meterRegistry);
            
        this.activeTournaments = Gauge.builder("tournaments.active.count")
            .description("Number of active tournaments")
            .tag("type", "business")
            .register(meterRegistry, this, BusinessMetrics::getActiveTournamentCount);
    }
    
    private double getActiveTournamentCount(BusinessMetrics metrics) {
        return tournamentRepository.countByStatus(Tournament.TournamentStatus.PUBLISHED);
    }
    
    // Métodos para incrementar métricas
    public void incrementTournamentsCreated() {
        tournamentsCreated.increment();
    }
    
    public void recordOrderProcessingTime(Duration duration) {
        orderProcessingTime.record(duration);
    }
}
```

### 4.2 Métricas Técnicas (Actuator)
```yaml
# Configuración de Actuator
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,loggers,auditevents
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
      show-components: always
    metrics:
      enabled: true
    prometheus:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true
      percentiles:
        http.server.requests: 0.5, 0.95, 0.99
```

### 4.3 Custom Health Indicators
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    private final DataSource dataSource;
    
    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up()
                    .withDetail("database", "Available")
                    .withDetail("validationQuery", "SELECT 1")
                    .build();
            }
        } catch (Exception e) {
            return Health.down()
                .withDetail("database", "Unavailable")
                .withException(e)
                .build();
        }
        
        return Health.down()
            .withDetail("database", "Connection validation failed")
            .build();
    }
}

@Component
public class TournamentSystemHealthIndicator implements HealthIndicator {
    
    private final TournamentRepository tournamentRepository;
    
    @Override
    public Health health() {
        try {
            long activeTournaments = tournamentRepository.countByStatus(Tournament.TournamentStatus.PUBLISHED);
            long totalTournaments = tournamentRepository.count();
            
            return Health.up()
                .withDetail("activeTournaments", activeTournaments)
                .withDetail("totalTournaments", totalTournaments)
                .withDetail("systemLoad", activeTournaments > 100 ? "HIGH" : "NORMAL")
                .build();
                
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", "Cannot access tournament data")
                .withException(e)
                .build();
        }
    }
}
```

## 5. Trazabilidad de Requests

### 5.1 Correlation ID Implementation
```java
@Component
public class CorrelationIdFilter implements Filter {
    
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        
        // Add to MDC for logging
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
        
        // Add to response headers
        httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);
        
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }
}
```

### 5.2 Frontend Correlation ID
```typescript
// Angular Interceptor para Correlation ID
const correlationInterceptor: HttpInterceptorFn = (req, next) => {
  let correlationId = sessionStorage.getItem('correlationId');
  
  if (!correlationId) {
    correlationId = generateUUID();
    sessionStorage.setItem('correlationId', correlationId);
  }
  
  const correlationReq = req.clone({
    headers: req.headers.set('X-Correlation-ID', correlationId)
  });
  
  return next(correlationReq).pipe(
    tap(event => {
      if (event instanceof HttpResponse) {
        const responseCorrelationId = event.headers.get('X-Correlation-ID');
        if (responseCorrelationId) {
          console.log(`Request completed with correlation ID: ${responseCorrelationId}`);
        }
      }
    })
  );
};

function generateUUID(): string {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0;
    const v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}
```

## 6. Monitoreo de Performance

### 6.1 Custom Metrics para Performance
```java
@Component
public class PerformanceMetrics {
    
    private final Timer httpRequestTimer;
    private final Counter httpRequestCounter;
    private final Gauge memoryUsage;
    private final Counter errorCounter;
    
    public PerformanceMetrics(MeterRegistry meterRegistry) {
        this.httpRequestTimer = Timer.builder("http.requests.duration")
            .description("HTTP request duration")
            .register(meterRegistry);
            
        this.httpRequestCounter = Counter.builder("http.requests.total")
            .description("Total HTTP requests")
            .register(meterRegistry);
            
        this.memoryUsage = Gauge.builder("jvm.memory.usage.ratio")
            .description("JVM memory usage ratio")
            .register(meterRegistry, this, PerformanceMetrics::getMemoryUsageRatio);
            
        this.errorCounter = Counter.builder("application.errors.total")
            .description("Total application errors")
            .register(meterRegistry);
    }
    
    private double getMemoryUsageRatio(PerformanceMetrics metrics) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        return (double) (totalMemory - freeMemory) / totalMemory;
    }
    
    public void recordHttpRequest(String method, String uri, int status, Duration duration) {
        httpRequestTimer.record(duration);
        httpRequestCounter.increment(
            Tags.of(
                Tag.of("method", method),
                Tag.of("uri", uri),
                Tag.of("status", String.valueOf(status))
            )
        );
    }
    
    public void recordError(String errorType, String component) {
        errorCounter.increment(
            Tags.of(
                Tag.of("type", errorType),
                Tag.of("component", component)
            )
        );
    }
}
```

### 6.2 Performance Monitoring Aspect
```java
@Aspect
@Component
public class PerformanceMonitoringAspect {
    
    private final PerformanceMetrics performanceMetrics;
    
    @Around("@annotation(Timed) || execution(* com.example.torneos.application.service.*.*(..))")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        try {
            Object result = joinPoint.proceed();
            
            // Record successful execution
            sample.stop(Timer.builder("method.execution.time")
                .tag("class", className)
                .tag("method", methodName)
                .tag("status", "success")
                .register(Metrics.globalRegistry));
                
            return result;
            
        } catch (Exception e) {
            // Record failed execution
            sample.stop(Timer.builder("method.execution.time")
                .tag("class", className)
                .tag("method", methodName)
                .tag("status", "error")
                .register(Metrics.globalRegistry));
                
            performanceMetrics.recordError(e.getClass().getSimpleName(), className);
            throw e;
        }
    }
}
```

## 7. Alertas y Notificaciones

### 7.1 Custom Alerting Service
```java
@Service
public class AlertingService {
    
    private static final Logger alertLogger = LoggerFactory.getLogger("ALERTS");
    
    public void sendCriticalAlert(String title, String message, Map<String, Object> context) {
        AlertEvent alert = AlertEvent.builder()
            .level(AlertLevel.CRITICAL)
            .title(title)
            .message(message)
            .context(context)
            .timestamp(Instant.now())
            .build();
            
        // Log alert
        alertLogger.error("CRITICAL_ALERT: title={}, message={}, context={}", 
            title, message, context);
            
        // Send to external systems (Slack, email, etc.)
        sendToExternalSystems(alert);
    }
    
    public void sendWarningAlert(String title, String message) {
        alertLogger.warn("WARNING_ALERT: title={}, message={}", title, message);
    }
    
    private void sendToExternalSystems(AlertEvent alert) {
        // Implementation for Slack, email, PagerDuty, etc.
        // This would be configured based on environment
    }
}
```

### 7.2 Business Rule Monitoring
```java
@Component
public class BusinessRuleMonitor {
    
    private final AlertingService alertingService;
    private final BusinessMetrics businessMetrics;
    
    @EventListener
    public void handleTournamentCreated(TournamentCreatedEvent event) {
        businessMetrics.incrementTournamentsCreated();
        
        // Check for suspicious activity
        if (isHighVolumeCreation(event.getOrganizerId())) {
            alertingService.sendWarningAlert(
                "High Volume Tournament Creation",
                String.format("User %s created multiple tournaments in short time", 
                    event.getOrganizerId())
            );
        }
    }
    
    @EventListener
    public void handleTicketOrderCreated(TicketOrderCreatedEvent event) {
        businessMetrics.incrementTicketOrdersCreated();
        
        // Monitor for potential fraud
        if (isLargeOrder(event.getQuantity()) || isSuspiciousUser(event.getUserId())) {
            alertingService.sendCriticalAlert(
                "Suspicious Ticket Order",
                "Large or suspicious ticket order detected",
                Map.of(
                    "orderId", event.getOrderId(),
                    "userId", event.getUserId(),
                    "quantity", event.getQuantity()
                )
            );
        }
    }
}
```

## 8. Dashboards y Visualización

### 8.1 Métricas Clave para Dashboards
```json
{
  "businessMetrics": {
    "activeTournaments": "tournaments_active_count",
    "tournamentsCreatedToday": "tournaments_created_total{period='today'}",
    "ticketsSoldToday": "ticket_orders_approved_total{period='today'}",
    "averageOrderProcessingTime": "order_processing_duration_mean"
  },
  "technicalMetrics": {
    "responseTime95th": "http_request_duration_seconds{quantile='0.95'}",
    "errorRate": "http_requests_total{status=~'5..'}",
    "memoryUsage": "jvm_memory_usage_ratio",
    "databaseConnections": "hikaricp_connections_active"
  },
  "securityMetrics": {
    "failedLogins": "authentication_failures_total",
    "suspiciousActivity": "security_events_total{type='suspicious'}",
    "unauthorizedAccess": "security_events_total{type='unauthorized'}"
  }
}
```

### 8.2 Configuración de Grafana Dashboard
```json
{
  "dashboard": {
    "title": "Torneos E-Sport Platform",
    "panels": [
      {
        "title": "Active Tournaments",
        "type": "singlestat",
        "targets": [
          {
            "expr": "tournaments_active_count",
            "legendFormat": "Active Tournaments"
          }
        ]
      },
      {
        "title": "Request Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_requests_total[5m])",
            "legendFormat": "{{method}} {{uri}}"
          }
        ]
      },
      {
        "title": "Response Time Percentiles",
        "type": "graph",
        "targets": [
          {
            "expr": "histogram_quantile(0.50, rate(http_request_duration_seconds_bucket[5m]))",
            "legendFormat": "50th percentile"
          },
          {
            "expr": "histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m]))",
            "legendFormat": "95th percentile"
          }
        ]
      },
      {
        "title": "Error Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_requests_total{status=~'5..'}[5m])",
            "legendFormat": "5xx errors"
          }
        ]
      }
    ]
  }
}
```

## 9. Consultas de Auditoría

### 9.1 Queries Comunes de Auditoría
```sql
-- Actividad de un usuario específico
SELECT event_type, entity_type, entity_id, metadata, created_at
FROM audit_log 
WHERE actor_user_id = '123e4567-e89b-12d3-a456-426614174000'
ORDER BY created_at DESC;

-- Eventos de un torneo específico
SELECT event_type, actor_user_id, metadata, created_at
FROM audit_log 
WHERE entity_type = 'TOURNAMENT' 
  AND entity_id = '789e0123-e45b-67c8-d901-234567890123'
ORDER BY created_at DESC;

-- Eventos de seguridad en las últimas 24 horas
SELECT event_type, entity_id, actor_user_id, metadata, created_at
FROM audit_log 
WHERE event_type IN ('UNAUTHORIZED_ACCESS_ATTEMPT', 'INVALID_TOKEN_USED', 'SUSPICIOUS_ACTIVITY')
  AND created_at >= NOW() - INTERVAL '24 hours'
ORDER BY created_at DESC;

-- Resumen de actividad por día
SELECT DATE(created_at) as date, 
       event_type, 
       COUNT(*) as event_count
FROM audit_log 
WHERE created_at >= NOW() - INTERVAL '30 days'
GROUP BY DATE(created_at), event_type
ORDER BY date DESC, event_count DESC;
```

### 9.2 API de Consulta de Auditoría
```java
@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AuditLogResponseDto>> getUserActivity(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AuditLog> auditLogs = auditLogService.findByActorUserId(userId, pageable);
        
        return ResponseEntity.ok(auditLogs.map(this::mapToResponse));
    }
    
    @GetMapping("/security-events")
    public ResponseEntity<Page<AuditLogResponseDto>> getSecurityEvents(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since,
            Pageable pageable) {
        
        if (since == null) {
            since = LocalDateTime.now().minusDays(7); // Default to last 7 days
        }
        
        Page<AuditLog> securityEvents = auditLogService.findSecurityEventsSince(since, pageable);
        return ResponseEntity.ok(securityEvents.map(this::mapToResponse));
    }
}
```

## 10. Retención y Archivado de Logs

### 10.1 Política de Retención
```yaml
# Configuración de retención de logs
logging:
  retention:
    application-logs: 90 days
    audit-logs: 7 years
    security-logs: 2 years
    performance-logs: 30 days
    
  archival:
    enabled: true
    storage: s3://torneos-logs-archive/
    compression: gzip
    schedule: "0 2 * * *"  # Daily at 2 AM
```

### 10.2 Script de Archivado
```bash
#!/bin/bash
# archive-logs.sh

DATE=$(date +%Y%m%d)
ARCHIVE_DIR="/opt/logs/archive"
S3_BUCKET="s3://torneos-logs-archive"

# Archive application logs older than 90 days
find /opt/logs -name "*.log" -mtime +90 -exec gzip {} \;
find /opt/logs -name "*.log.gz" -mtime +90 -exec mv {} $ARCHIVE_DIR/ \;

# Upload to S3
aws s3 sync $ARCHIVE_DIR/ $S3_BUCKET/application-logs/$DATE/

# Archive audit logs older than 1 year to cold storage
psql -d torneo_prod -c "
  COPY (
    SELECT * FROM audit_log 
    WHERE created_at < NOW() - INTERVAL '1 year'
  ) TO '/tmp/audit_archive_$DATE.csv' WITH CSV HEADER;
"

gzip /tmp/audit_archive_$DATE.csv
aws s3 cp /tmp/audit_archive_$DATE.csv.gz $S3_BUCKET/audit-logs/$DATE/

# Clean up local files
rm -f /tmp/audit_archive_$DATE.csv.gz
find $ARCHIVE_DIR -name "*.log.gz" -mtime +7 -delete
```

---

**Conclusión**: El sistema de monitoreo y trazabilidad implementado proporciona observabilidad completa de la plataforma de torneos E-Sport, con logs estructurados, métricas de negocio y técnicas, auditoría completa de eventos críticos, y trazabilidad end-to-end de todas las operaciones del sistema.
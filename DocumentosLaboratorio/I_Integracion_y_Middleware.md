# I. Integración y Middleware

## Información General
- **Proyecto**: Plataforma de Torneos E-Sport
- **Arquitectura**: Frontend Angular + Backend Spring Boot
- **Comunicación**: HTTP REST + JSON
- **Middleware**: Interceptores, Filtros, Handlers
- **Fecha**: Diciembre 2024

## 1. Arquitectura de Integración

### 1.1 Visión General de la Integración
```
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND (Angular)                       │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ Auth Interceptor│    │    Error Interceptor            │ │
│  │ (JWT Injection) │    │    (Error Handling)             │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
│                                   │                         │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              HTTP Client Service                        │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                   │
                                   ▼ HTTP/REST/JSON
┌─────────────────────────────────────────────────────────────┐
│                    BACKEND (Spring Boot)                    │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ CORS Filter     │    │   JWT Authentication Filter    │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ Logging         │    │   Global Exception Handler     │ │
│  │ Interceptor     │    │   (Error Standardization)      │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              REST Controllers                           │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Capas de Middleware
1. **Frontend Interceptors**: Autenticación y manejo de errores
2. **Backend Filters**: CORS, JWT, Logging
3. **Exception Handlers**: Estandarización de errores
4. **Service Layer**: Lógica de negocio y validaciones
5. **Repository Layer**: Acceso a datos

## 2. Integración Frontend ↔ Backend

### 2.1 Configuración de Comunicación HTTP

#### Configuración Base (Angular)
```typescript
// app.config.ts
export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor, errorInterceptor]))
  ]
};
```

#### Servicio Base HTTP
```typescript
@Injectable()
export class HttpBaseService {
  protected baseUrl = 'http://localhost:8081/api';
  
  constructor(protected http: HttpClient) {}
  
  protected get<T>(endpoint: string, options?: any): Observable<T> {
    return this.http.get<T>(`${this.baseUrl}${endpoint}`, options);
  }
  
  protected post<T>(endpoint: string, data: any, options?: any): Observable<T> {
    return this.http.post<T>(`${this.baseUrl}${endpoint}`, data, options);
  }
}
```

### 2.2 Proxy de Desarrollo
```json
// proxy.conf.json
{
  "/api/*": {
    "target": "http://localhost:8081",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug"
  }
}
```

**Beneficios del Proxy**:
- ✅ Evita problemas de CORS en desarrollo
- ✅ Simula entorno de producción
- ✅ Facilita debugging de requests
- ✅ Transparente para el código frontend

## 3. Middleware de Autenticación

### 3.1 Auth Interceptor (Frontend)
```typescript
const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Skip auth endpoints
  if (req.url.includes('/auth/')) {
    return next(req);
  }
  
  const tokenService = inject(TokenService);
  const token = tokenService.getAccessToken();
  
  if (token) {
    let headers = req.headers.set('Authorization', `Bearer ${token}`);
    
    // Extract user ID from token and add X-USER-ID header
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      if (payload.sub) {
        headers = headers.set('X-USER-ID', payload.sub);
      }
    } catch (e) {
      console.error('Error parsing token for user ID:', e);
    }
    
    const authReq = req.clone({ headers });
    return next(authReq);
  }
  
  return next(req);
};
```

**Funcionalidades**:
- ✅ Inyección automática de JWT token
- ✅ Extracción de User ID del token
- ✅ Skip de endpoints de autenticación
- ✅ Manejo de errores de parsing

### 3.2 JWT Authentication Filter (Backend)
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String jwt = authHeader.substring(7);
        
        try {
            UUID userId = jwtService.extractUserId(jwt);
            
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findById(userId).orElse(null);
                
                if (user != null && jwtService.isTokenValid(jwt) && !jwtService.isTokenExpired(jwt)) {
                    List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                    );
                    
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(user, null, authorities);
                    
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token inválido, continuar sin autenticación
        }
        
        filterChain.doFilter(request, response);
    }
}
```

## 4. Manejo de Errores Centralizado

### 4.1 Error Interceptor (Frontend)
```typescript
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Error desconocido';
        
        if (error.error instanceof ErrorEvent) {
          // Client-side error
          errorMessage = `Error: ${error.error.message}`;
        } else {
          // Server-side error - RFC 7807 Problem Details format
          if (error.error && typeof error.error === 'object') {
            const apiError = error.error as ApiError;
            errorMessage = apiError.detail || apiError.title || `Error ${error.status}`;
          } else {
            errorMessage = `Error ${error.status}: ${error.message}`;
          }
        }

        console.error('HTTP Error:', {
          status: error.status,
          message: errorMessage,
          url: error.url,
          error: error.error
        });

        return throwError(() => ({
          status: error.status,
          message: errorMessage,
          originalError: error
        }));
      })
    );
  }
}
```

### 4.2 Global Exception Handler (Backend)
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ProblemDetail problem = problemDetailFactory.createValidationError(
            "Validation failed for one or more fields", request);
        problem.setProperty("validationErrors", errors);
        
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        ProblemDetail problem = problemDetailFactory.createBusinessRuleViolation(
            ex.getMessage(), "BUSINESS_LOGIC", request);
        
        return ResponseEntity.unprocessableEntity().body(problem);
    }
}
```

### 4.3 Formato Estándar de Errores (RFC 7807)
```json
{
  "type": "about:blank",
  "title": "Validation Error",
  "status": 400,
  "detail": "Validation failed for one or more fields",
  "instance": "/api/tournaments",
  "timestamp": "2024-12-01T10:00:00Z",
  "validationErrors": {
    "name": "El nombre es obligatorio",
    "startDateTime": "La fecha debe ser futura"
  }
}
```

## 5. Logging y Trazabilidad

### 5.1 Logging Interceptor (Backend)
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
        // Clean up MDC
        MDC.clear();
    }
}
```

### 5.2 Structured Logging
```java
// Configuración en logback-spring.xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <timestamp/>
                <logLevel/>
                <loggerName/>
                <mdc/>
                <message/>
            </providers>
        </encoder>
    </appender>
</configuration>
```

**Ejemplo de Log Estructurado**:
```json
{
  "timestamp": "2024-12-01T10:00:00.000Z",
  "level": "INFO",
  "logger": "com.example.torneos.application.service.TournamentService",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "message": "Tournament created successfully",
  "tournamentId": "789e0123-e45b-67c8-d901-234567890123"
}
```

## 6. Comunicación entre Servicios

### 6.1 Service Layer Integration
```typescript
// Frontend Service
@Injectable()
export class TournamentsService extends HttpBaseService {
  
  createTournament(tournament: CreateTournamentRequest): Observable<TournamentResponse> {
    return this.post<TournamentResponse>('/tournaments', tournament).pipe(
      tap(response => console.log('Tournament created:', response)),
      catchError(error => {
        console.error('Error creating tournament:', error);
        return throwError(() => error);
      })
    );
  }
  
  getTournaments(filters?: TournamentFilters): Observable<Page<TournamentResponse>> {
    const params = this.buildQueryParams(filters);
    return this.get<Page<TournamentResponse>>('/tournaments', { params });
  }
}
```

### 6.2 Backend Service Integration
```java
@Service
@Transactional
public class TournamentService {
    
    public TournamentResponse create(CreateTournamentRequest request, UUID organizerId) {
        // Validaciones de negocio
        validateTournamentCreation(request, organizerId);
        
        // Crear entidad de dominio
        Tournament tournament = createTournamentEntity(request, organizerId);
        
        // Persistir
        Tournament savedTournament = tournamentRepository.save(tournament);
        
        // Auditoría
        auditLogService.logEvent(
            AuditLog.EventType.TOURNAMENT_CREATED,
            AuditLog.EntityType.TOURNAMENT,
            savedTournament.getId(),
            organizerId,
            "Tournament created: " + savedTournament.getName()
        );
        
        // Mapear a DTO de respuesta
        return mapToResponse(savedTournament);
    }
}
```

## 7. Validación y Transformación de Datos

### 7.1 Validación en Frontend
```typescript
// Reactive Forms con validaciones
export class TournamentFormComponent {
  tournamentForm = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(255)]],
    description: ['', [Validators.maxLength(1000)]],
    startDateTime: ['', [Validators.required, this.futureDateValidator]],
    endDateTime: ['', [Validators.required]],
    categoryId: ['', [Validators.required]],
    gameTypeId: ['', [Validators.required]]
  }, { validators: this.dateRangeValidator });
  
  futureDateValidator(control: AbstractControl): ValidationErrors | null {
    const date = new Date(control.value);
    const now = new Date();
    return date > now ? null : { pastDate: true };
  }
}
```

### 7.2 Validación en Backend
```java
// Bean Validation con anotaciones
public class CreateTournamentRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String name;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDateTime startDateTime;
    
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime endDateTime;
    
    @ValidTournamentDates // Validador personalizado
    public class CreateTournamentRequest { ... }
}
```

### 7.3 Transformación de Datos (Mappers)
```java
@Component
public class TournamentMapper {
    
    public TournamentResponse toResponse(Tournament tournament) {
        return new TournamentResponse(
            tournament.getId(),
            tournament.getName(),
            tournament.getDescription(),
            tournament.getOrganizerId(),
            tournament.getCategoryId(),
            tournament.getGameTypeId(),
            tournament.isPaid(),
            tournament.getMaxFreeCapacity(),
            tournament.getStartDateTime(),
            tournament.getEndDateTime(),
            tournament.getStatus().name(),
            tournament.getCreatedAt(),
            tournament.getUpdatedAt()
        );
    }
}
```

## 8. Manejo de Estados y Errores

### 8.1 Estado de Loading (Frontend)
```typescript
@Injectable()
export class LoadingService {
  private loadingSubject = new BehaviorSubject<boolean>(false);
  public loading$ = this.loadingSubject.asObservable();
  
  show(): void {
    this.loadingSubject.next(true);
  }
  
  hide(): void {
    this.loadingSubject.next(false);
  }
}

// Uso en componentes
export class TournamentsListComponent {
  tournaments$ = this.tournamentsService.getTournaments().pipe(
    tap(() => this.loadingService.show()),
    finalize(() => this.loadingService.hide())
  );
}
```

### 8.2 Manejo de Errores por Tipo
```typescript
// Error handling service
@Injectable()
export class ErrorHandlingService {
  
  handleError(error: any): void {
    switch (error.status) {
      case 400:
        this.showValidationErrors(error.error.validationErrors);
        break;
      case 401:
        this.redirectToLogin();
        break;
      case 403:
        this.showAccessDeniedMessage();
        break;
      case 422:
        this.showBusinessRuleError(error.message);
        break;
      default:
        this.showGenericError();
    }
  }
}
```

## 9. Configuración CORS

### 9.1 Configuración Global (Backend)
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

### 9.2 Configuración por Controlador
```java
@RestController
@RequestMapping("/api/tournaments")
@CrossOrigin(origins = "http://localhost:4200")
public class TournamentController {
    // Endpoints específicos con CORS configurado
}
```

## 10. Rate Limiting y Throttling

### 10.1 Rate Limit Interceptor
```java
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private final Map<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientId = getClientIdentifier(request);
        RateLimit rateLimit = getRateLimitAnnotation(handler);
        
        if (rateLimit != null) {
            if (!isRequestAllowed(clientId, rateLimit)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                return false;
            }
        }
        
        return true;
    }
}
```

### 10.2 Uso de Rate Limiting
```java
@RestController
public class TournamentController {
    
    @PostMapping
    @RateLimit(requests = 10, window = 1, timeUnit = TimeUnit.MINUTES)
    public ResponseEntity<TournamentResponse> create(@RequestBody CreateTournamentRequest request) {
        // Máximo 10 creaciones de torneo por minuto
    }
}
```

## 11. Monitoreo y Métricas

### 11.1 Business Metrics
```java
@Component
public class BusinessMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter tournamentCreatedCounter;
    private final Timer tournamentCreationTimer;
    
    public BusinessMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.tournamentCreatedCounter = Counter.builder("tournaments.created")
                .description("Number of tournaments created")
                .register(meterRegistry);
        this.tournamentCreationTimer = Timer.builder("tournaments.creation.time")
                .description("Time taken to create tournaments")
                .register(meterRegistry);
    }
    
    public void recordTournamentCreated() {
        tournamentCreatedCounter.increment();
    }
}
```

### 11.2 Health Checks
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        try {
            // Check database connectivity
            return Health.up()
                    .withDetail("database", "Available")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "Unavailable")
                    .withException(e)
                    .build();
        }
    }
}
```

## 12. Patrones de Integración Implementados

### 12.1 Request-Response Pattern
- ✅ **Comunicación Síncrona**: HTTP REST para operaciones CRUD
- ✅ **Timeout Handling**: Configuración de timeouts en cliente
- ✅ **Retry Logic**: Reintentos automáticos en caso de fallo
- ✅ **Circuit Breaker**: Preparado para implementación futura

### 12.2 Error Handling Pattern
- ✅ **Centralized Error Handling**: Global exception handler
- ✅ **Standardized Error Format**: RFC 7807 Problem Details
- ✅ **Error Propagation**: Errores propagados correctamente entre capas
- ✅ **User-Friendly Messages**: Mensajes comprensibles para el usuario

### 12.3 Security Integration Pattern
- ✅ **Token-Based Authentication**: JWT tokens stateless
- ✅ **Automatic Token Injection**: Interceptores automáticos
- ✅ **Role-Based Authorization**: Permisos por rol
- ✅ **Security Context Propagation**: Contexto de seguridad entre capas

---

**Conclusión**: La integración entre frontend y backend está implementada con patrones robustos de middleware que proporcionan autenticación automática, manejo centralizado de errores, logging estructurado y comunicación eficiente, asegurando una experiencia de usuario fluida y un sistema mantenible.
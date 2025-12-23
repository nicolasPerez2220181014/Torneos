# G. Seguridad

## Información General
- **Proyecto**: Plataforma de Torneos E-Sport
- **Framework**: Spring Security 6.x
- **Autenticación**: JWT (JSON Web Tokens)
- **Algoritmo**: HMAC SHA-256
- **Fecha**: Diciembre 2024

## 1. Estrategia de Seguridad General

### 1.1 Arquitectura de Seguridad
La plataforma implementa una **estrategia de seguridad multicapa** basada en:

```
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND (Angular)                       │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │   Auth Guard    │    │     HTTP Interceptor            │ │
│  │   (Route        │    │     (JWT Token                  │ │
│  │   Protection)   │    │      Injection)                 │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                   │
                                   ▼ HTTPS/JWT
┌─────────────────────────────────────────────────────────────┐
│                    BACKEND (Spring Boot)                    │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ CORS Filter     │    │   JWT Authentication Filter    │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ Security Config │    │   Method-Level Security         │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                   │
                                   ▼ JPA/SQL
┌─────────────────────────────────────────────────────────────┐
│                    DATABASE LAYER                           │
│  ┌─────────────────┐    ┌─────────────────────────────────┐ │
│  │ SQL Injection   │    │     Data Constraints            │ │
│  │ Prevention      │    │     (Foreign Keys, Checks)      │ │
│  └─────────────────┘    └─────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Principios de Seguridad Aplicados
- **Autenticación Stateless**: JWT tokens sin sesiones del servidor
- **Autorización Basada en Roles**: USER, ORGANIZER, SUBADMIN
- **Principio de Menor Privilegio**: Acceso mínimo necesario
- **Defensa en Profundidad**: Múltiples capas de seguridad
- **Validación de Entrada**: Sanitización en múltiples niveles

## 2. Autenticación JWT

### 2.1 Configuración JWT
```java
@Service
public class JwtService {
    private final SecretKey secretKey;
    private final long jwtExpiration = 3600000;      // 1 hora
    private final long refreshExpiration = 86400000; // 24 horas
    
    // Algoritmo: HMAC SHA-256
    private final SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());
}
```

### 2.2 Estructura del Token JWT
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "role": "ORGANIZER",
    "iat": 1703001600,
    "exp": 1703005200
  },
  "signature": "HMACSHA256(base64UrlEncode(header) + \".\" + base64UrlEncode(payload), secret)"
}
```

### 2.3 Generación de Tokens
```java
public String generateToken(UUID userId, String email, String role) {
    return Jwts.builder()
            .subject(userId.toString())
            .claim(\"email\", email)
            .claim(\"role\", role)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plus(jwtExpiration, ChronoUnit.MILLIS)))
            .signWith(secretKey)
            .compact();
}
```

### 2.4 Validación de Tokens
```java
public boolean isTokenValid(String token) {
    try {
        extractClaims(token);
        return true;
    } catch (JwtException | IllegalArgumentException e) {
        return false;
    }
}

public boolean isTokenExpired(String token) {
    return extractClaims(token).getExpiration().before(new Date());
}
```

## 3. Sistema de Roles y Autorización

### 3.1 Definición de Roles
```java
public enum UserRole {
    USER,       // Usuario regular - puede comprar tickets, ver torneos
    ORGANIZER,  // Organizador - puede crear y gestionar torneos
    SUBADMIN    // Sub-administrador - puede ayudar en gestión de torneos específicos
}
```

### 3.2 Matriz de Permisos

| Recurso/Acción | USER | ORGANIZER | SUBADMIN |
|----------------|------|-----------|----------|
| **Autenticación** |
| Login | ✅ | ✅ | ✅ |
| Logout | ✅ | ✅ | ✅ |
| **Usuarios** |
| Crear cuenta | ✅ | ✅ | ✅ |
| Ver perfil propio | ✅ | ✅ | ✅ |
| Actualizar perfil propio | ✅ | ✅ | ✅ |
| Listar usuarios | ❌ | ✅ | ❌ |
| **Torneos** |
| Ver torneos públicos | ✅ | ✅ | ✅ |
| Crear torneo | ❌ | ✅ | ❌ |
| Actualizar torneo | ❌ | ✅ (propio) | ✅ (asignado) |
| Publicar torneo | ❌ | ✅ (propio) | ❌ |
| Asignar sub-admins | ❌ | ✅ (propio) | ❌ |
| **Tickets** |
| Comprar tickets | ✅ | ✅ | ✅ |
| Ver mis tickets | ✅ | ✅ | ✅ |
| Validar tickets | ❌ | ✅ | ✅ (torneo asignado) |
| **Streaming** |
| Ver stream gratuito | ✅ | ✅ | ✅ |
| Ver stream pagado | ✅ (con ticket) | ✅ | ✅ |
| Configurar stream | ❌ | ✅ (propio) | ❌ |
| Bloquear/desbloquear | ❌ | ✅ (propio) | ❌ |
| **Auditoría** |
| Ver logs | ❌ | ✅ | ✅ (torneo asignado) |

### 3.3 Implementación de Autorización
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz
            // Endpoints públicos
            .requestMatchers(\"/api/auth/**\").permitAll()
            .requestMatchers(\"/api/users\").permitAll() // Registro
            .requestMatchers(HttpMethod.GET, \"/api/tournaments/**\").permitAll()
            
            // Endpoints que requieren roles específicos
            .requestMatchers(HttpMethod.POST, \"/api/tournaments/**\").hasRole(\"ORGANIZER\")
            .requestMatchers(\"/api/audit-logs/**\").hasAnyRole(\"ORGANIZER\", \"SUBADMIN\")
            
            .anyRequest().authenticated()
        );
    }
}
```

## 4. Filtro de Autenticación JWT

### 4.1 JwtAuthenticationFilter
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader(\"Authorization\");
        
        if (authHeader == null || !authHeader.startsWith(\"Bearer \")) {
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
                        new SimpleGrantedAuthority(\"ROLE_\" + user.getRole().name())
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

### 4.2 Flujo de Autenticación
```
1. Cliente envía request con header: Authorization: Bearer <jwt_token>
2. JwtAuthenticationFilter intercepta el request
3. Extrae y valida el JWT token
4. Si es válido, carga el usuario desde la base de datos
5. Crea Authentication object con roles del usuario
6. Establece el contexto de seguridad
7. Continúa con el procesamiento del request
```

## 5. Protección de Endpoints

### 5.1 Endpoints Públicos (Sin Autenticación)
```java
// Autenticación
POST /api/auth/login
POST /api/auth/refresh

// Registro de usuarios
POST /api/users

// Consulta de datos maestros
GET /api/categories
GET /api/game-types

// Consulta pública de torneos
GET /api/tournaments
GET /api/tournaments/{id}

// Documentación
GET /swagger-ui.html
GET /v3/api-docs
GET /actuator/health
```

### 5.2 Endpoints Autenticados (Requieren JWT)
```java
// Gestión de usuarios
GET /api/users
GET /api/users/{id}
PUT /api/users/{id}

// Gestión de perfil
GET /api/users/email/{email}

// Logout
POST /api/auth/logout
```

### 5.3 Endpoints con Autorización por Rol
```java
// Solo ORGANIZER
POST /api/categories
PUT /api/categories/{id}
POST /api/game-types
PUT /api/game-types/{id}
POST /api/tournaments
POST /api/tournaments/{id}/subadmins

// ORGANIZER o SUBADMIN
PUT /api/tournaments/{id}
GET /api/audit-logs
POST /api/tickets/{code}/validate

// Cualquier usuario autenticado
POST /api/tournaments/{id}/orders
GET /api/tournaments/{id}/tickets
```

## 6. Seguridad Frontend (Angular)

### 6.1 Auth Guard
```typescript
@Injectable()
export class AuthGuard implements CanActivate {
  constructor(private tokenService: TokenService, private router: Router) {}
  
  canActivate(): boolean {
    if (this.tokenService.getAccessToken()) {
      return true;
    }
    
    this.router.navigate(['/login']);
    return false;
  }
}
```

### 6.2 HTTP Interceptor
```typescript
const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.includes('/auth/')) {
    return next(req);
  }
  
  const tokenService = inject(TokenService);
  const token = tokenService.getAccessToken();
  
  if (token) {
    let headers = req.headers.set('Authorization', `Bearer ${token}`);
    
    // Extraer user ID del token
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      if (payload.sub) {
        headers = headers.set('X-USER-ID', payload.sub);
      }
    } catch (e) {
      console.error('Error parsing token:', e);
    }
    
    const authReq = req.clone({ headers });
    return next(authReq);
  }
  
  return next(req);
};
```

### 6.3 Almacenamiento Seguro de Tokens
```typescript
@Injectable()
export class TokenService {
  private readonly ACCESS_TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  
  setTokens(accessToken: string, refreshToken: string): void {
    // Usar sessionStorage para mayor seguridad (se limpia al cerrar navegador)
    sessionStorage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
    sessionStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
  }
  
  getAccessToken(): string | null {
    return sessionStorage.getItem(this.ACCESS_TOKEN_KEY);
  }
  
  clearTokens(): void {
    sessionStorage.removeItem(this.ACCESS_TOKEN_KEY);
    sessionStorage.removeItem(this.REFRESH_TOKEN_KEY);
  }
}
```

## 7. Protección Contra Vulnerabilidades

### 7.1 Prevención de SQL Injection
```java
// Uso de JPA/Hibernate con parámetros seguros
@Query(\"SELECT t FROM Tournament t WHERE t.organizerId = :organizerId\")
List<Tournament> findByOrganizerId(@Param(\"organizerId\") UUID organizerId);

// Validación de entrada
@Valid @RequestBody CreateTournamentRequest request
```

### 7.2 Protección CSRF
```java
// CSRF deshabilitado para APIs REST stateless
http.csrf(csrf -> csrf.disable())
```

### 7.3 Configuración CORS
```java
@CrossOrigin(origins = \"http://localhost:4200\")
public class TournamentController {
    // Permite requests solo desde el frontend
}
```

### 7.4 Validación de Entrada
```java
public class CreateTournamentRequest {
    @NotBlank(message = \"El nombre es obligatorio\")
    @Size(max = 255, message = \"El nombre no puede exceder 255 caracteres\")
    private String name;
    
    @NotNull(message = \"La fecha de inicio es obligatoria\")
    @Future(message = \"La fecha de inicio debe ser futura\")
    private LocalDateTime startDateTime;
}
```

### 7.5 Manejo Seguro de Errores
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, 
            \"Invalid request parameters\" // No exponer detalles internos
        );
        return ResponseEntity.badRequest().body(problemDetail);
    }
}
```

## 8. Auditoría y Trazabilidad

### 8.1 Sistema de Auditoría
```java
@Entity
public class AuditLog {
    private UUID id;
    private EventType eventType;        // TOURNAMENT_CREATED, USER_LOGIN, etc.
    private EntityType entityType;      // TOURNAMENT, USER, TICKET, etc.
    private UUID entityId;              // ID de la entidad afectada
    private UUID actorUserId;           // Usuario que ejecutó la acción
    private String metadata;            // Información adicional en JSON
    private LocalDateTime createdAt;
}
```

### 8.2 Eventos Auditados
- **Autenticación**: LOGIN, LOGOUT, TOKEN_REFRESH
- **Torneos**: TOURNAMENT_CREATED, TOURNAMENT_PUBLISHED, TOURNAMENT_UPDATED
- **Tickets**: TICKET_PURCHASED, TICKET_VALIDATED
- **Usuarios**: USER_CREATED, USER_UPDATED, ROLE_CHANGED
- **Streaming**: STREAM_BLOCKED, STREAM_UNBLOCKED, STREAM_ACCESS_GRANTED

### 8.3 Consulta de Logs de Auditoría
```java
// Filtrar por entidad
GET /api/audit-logs/entity/{entityId}

// Filtrar por actor
GET /api/audit-logs/actor/{actorUserId}

// Filtrar por tipo de evento
GET /api/audit-logs/event-type/TOURNAMENT_CREATED
```

## 9. Configuración de Seguridad por Entorno

### 9.1 Desarrollo (application-dev.yml)
```yaml
jwt:
  secret: mySecretKeyForTorneosAppThatIsLongEnoughForHS256Algorithm
  expiration: 3600000      # 1 hora
  refresh-expiration: 86400000  # 24 horas

logging:
  level:
    org.springframework.security: DEBUG
```

### 9.2 Producción (application-prod.yml)
```yaml
jwt:
  secret: ${JWT_SECRET}    # Variable de entorno
  expiration: 1800000      # 30 minutos
  refresh-expiration: 604800000  # 7 días

logging:
  level:
    org.springframework.security: WARN
```

## 10. Mejores Prácticas Implementadas

### 10.1 Gestión de Tokens
- ✅ **Expiración Corta**: Access tokens de 1 hora
- ✅ **Refresh Tokens**: Para renovación automática
- ✅ **Revocación**: Logout invalida refresh tokens
- ✅ **Almacenamiento Seguro**: sessionStorage en frontend

### 10.2 Validación y Sanitización
- ✅ **Bean Validation**: @Valid en todos los endpoints
- ✅ **Constraints de BD**: Foreign keys, checks, unique
- ✅ **Parámetros Seguros**: JPA con @Param
- ✅ **Manejo de Errores**: Sin exposición de detalles internos

### 10.3 Principios de Seguridad
- ✅ **Stateless**: Sin sesiones del servidor
- ✅ **Least Privilege**: Permisos mínimos necesarios
- ✅ **Defense in Depth**: Múltiples capas de protección
- ✅ **Fail Secure**: Fallos seguros por defecto

## 11. Justificación Educativa

### 11.1 Propósito Académico
Esta implementación de seguridad está diseñada para **fines educativos** y demuestra:

- **Conceptos Fundamentales**: Autenticación vs Autorización
- **Tecnologías Modernas**: JWT, Spring Security 6
- **Buenas Prácticas**: Roles, validación, auditoría
- **Arquitectura Segura**: Separación de responsabilidades

### 11.2 Consideraciones para Producción
Para un entorno de producción real, se recomendaría:

- **OAuth 2.0/OpenID Connect**: Para autenticación federada
- **Rate Limiting Avanzado**: Por usuario y endpoint
- **Monitoreo de Seguridad**: Detección de anomalías
- **Cifrado de Base de Datos**: Datos sensibles encriptados
- **Certificados SSL/TLS**: Comunicación segura
- **WAF (Web Application Firewall)**: Protección adicional

---

**Conclusión**: La implementación de seguridad proporciona una base sólida y educativa que demuestra principios fundamentales de seguridad en aplicaciones web modernas, con JWT, autorización basada en roles y auditoría completa.
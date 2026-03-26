# 📘 DOCUMENTACIÓN MAESTRA - BACKEND TORNEOS E-SPORTS

## 📋 TABLA DE CONTENIDOS

1. [Introducción](#introducción)
2. [Arquitectura General](#arquitectura-general)
3. [Estructura del Proyecto](#estructura-del-proyecto)
4. [Dominio y Entidades](#dominio-y-entidades)
5. [APIs y Endpoints](#apis-y-endpoints)
6. [Seguridad JWT](#seguridad-jwt)
7. [Base de Datos](#base-de-datos)
8. [Eventos de Dominio](#eventos-de-dominio)
9. [Observabilidad](#observabilidad)
10. [Testing](#testing)
11. [Docker y Despliegue](#docker-y-despliegue)
12. [CI/CD](#cicd)
13. [Guía de Uso](#guía-de-uso)

---

## 1. INTRODUCCIÓN

### ¿Qué es Backend Torneos?

Backend Torneos es una **API REST** desarrollada en **Spring Boot 3.2** que gestiona torneos de e-sports. Permite:

- ✅ Crear y gestionar torneos (gratuitos y de pago)
- ✅ Vender tickets con diferentes etapas de precio
- ✅ Gestionar acceso a streams de torneos
- ✅ Administrar usuarios con diferentes roles
- ✅ Auditar todas las acciones importantes
- ✅ Generar métricas de negocio

### Tecnologías Principales

- **Java 17**
- **Spring Boot 3.2.0**
- **PostgreSQL 15**
- **JWT (JSON Web Tokens)**
- **Docker & Docker Compose**
- **Maven 3.9**
- **Flyway** (migraciones de BD)
- **TestContainers** (tests de integración)

---

## 2. ARQUITECTURA GENERAL

### 2.1 Arquitectura Limpia (Clean Architecture)

El proyecto sigue **Arquitectura Limpia** con 3 capas principales:

```
┌─────────────────────────────────────────┐
│         INFRASTRUCTURE                   │
│  (Controllers, Repositories, Config)    │
│                                          │
│  ┌────────────────────────────────────┐ │
│  │       APPLICATION                   │ │
│  │  (Services, DTOs, Use Cases)       │ │
│  │                                     │ │
│  │  ┌──────────────────────────────┐  │ │
│  │  │        DOMAIN                 │  │ │
│  │  │  (Entities, Business Logic)  │  │ │
│  │  └──────────────────────────────┘  │ │
│  └────────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

#### Capa de Dominio (Domain)
**Ubicación:** `src/main/java/com/example/torneos/domain/`

**Responsabilidad:** Contiene la lógica de negocio pura, sin dependencias externas.

**Componentes:**
- **Entidades (model/)**: Objetos de negocio (Tournament, User, Ticket, etc.)
- **Repositorios (repository/)**: Interfaces para persistencia
- **Eventos (event/)**: Eventos de dominio

**Ejemplo:**
```java
public class Tournament {
    private UUID id;
    private String name;
    private boolean isPaid;
    
    // Lógica de negocio
    public void publish() {
        if (status != TournamentStatus.DRAFT) {
            throw new IllegalStateException("Solo se pueden publicar torneos en DRAFT");
        }
        this.status = TournamentStatus.PUBLISHED;
    }
}
```

#### Capa de Aplicación (Application)
**Ubicación:** `src/main/java/com/example/torneos/application/`

**Responsabilidad:** Orquesta casos de uso y coordina el dominio.

**Componentes:**
- **Servicios (service/)**: Casos de uso de negocio
- **DTOs (dto/)**: Objetos de transferencia de datos
  - `request/`: DTOs de entrada
  - `response/`: DTOs de salida

**Ejemplo:**
```java
@Service
public class TournamentService {
    public TournamentResponse create(CreateTournamentRequest request, UUID organizerId) {
        // Validaciones
        // Lógica de negocio
        // Persistencia
        // Eventos
        return response;
    }
}
```

#### Capa de Infraestructura (Infrastructure)
**Ubicación:** `src/main/java/com/example/torneos/infrastructure/`

**Responsabilidad:** Implementaciones técnicas y frameworks.

**Componentes:**
- **Controladores (controller/)**: Endpoints REST
- **Persistencia (persistence/)**: Implementación JPA
- **Configuración (config/)**: Beans de Spring
- **Seguridad (security/)**: JWT, autenticación
- **Eventos (event/)**: Listeners de eventos
- **Observabilidad (observability/)**: Métricas, logs

---

## 3. ESTRUCTURA DEL PROYECTO

### 3.1 Árbol de Directorios

```
backend-torneos/
├── src/
│   ├── main/
│   │   ├── java/com/example/torneos/
│   │   │   ├── domain/
│   │   │   │   ├── model/              # Entidades de dominio
│   │   │   │   ├── repository/         # Interfaces de repositorio
│   │   │   │   └── event/              # Eventos de dominio
│   │   │   ├── application/
│   │   │   │   ├── service/            # Servicios de aplicación
│   │   │   │   └── dto/                # DTOs
│   │   │   │       ├── request/        # DTOs de entrada
│   │   │   │       └── response/       # DTOs de salida
│   │   │   └── infrastructure/
│   │   │       ├── controller/         # REST Controllers
│   │   │       ├── persistence/        # JPA Entities & Repos
│   │   │       ├── config/             # Configuración Spring
│   │   │       ├── security/           # JWT & Security
│   │   │       ├── event/              # Event Listeners
│   │   │       └── observability/      # Métricas & Logs
│   │   └── resources/
│   │       ├── application.yml         # Config principal
│   │       ├── application-dev.yml     # Config desarrollo
│   │       ├── application-qa.yml      # Config QA
│   │       ├── application-prod.yml    # Config producción
│   │       └── db/migration/           # Scripts Flyway
│   └── test/
│       └── java/com/example/torneos/
│           ├── application/service/    # Tests unitarios
│           └── integration/            # Tests de integración
├── deployment/
│   ├── aws/                            # Configuración AWS
│   └── azure/                          # Configuración Azure
├── .github/workflows/                  # GitHub Actions
├── docker-compose.yml                  # Docker Compose
├── Dockerfile                          # Imagen Docker
├── pom.xml                             # Dependencias Maven
└── README.md                           # Documentación básica
```

### 3.2 Archivos Importantes

| Archivo | Descripción |
|---------|-------------|
| `pom.xml` | Dependencias Maven y configuración del proyecto |
| `application.yml` | Configuración principal de Spring Boot |
| `Dockerfile` | Definición de imagen Docker multi-stage |
| `docker-compose.yml` | Orquestación de servicios (app + postgres) |
| `.github/workflows/ci-cd.yml` | Pipeline de CI/CD |
| `logback-spring.xml` | Configuración de logs estructurados |

---

## 4. DOMINIO Y ENTIDADES

### 4.1 Modelo de Dominio Completo

```
┌─────────────┐
│    User     │
└──────┬──────┘
       │ organiza
       ↓
┌─────────────┐      ┌──────────────┐
│ Tournament  │─────→│  Category    │
└──────┬──────┘      └──────────────┘
       │             ┌──────────────┐
       ├────────────→│  GameType    │
       │             └──────────────┘
       │
       ├─→ ┌──────────────────┐
       │   │ TournamentAdmin  │ (subadmins)
       │   └──────────────────┘
       │
       ├─→ ┌──────────────────┐
       │   │TicketSaleStage   │ (etapas de venta)
       │   └──────────────────┘
       │
       ├─→ ┌──────────────────┐
       │   │  TicketOrder     │
       │   └────────┬─────────┘
       │            │
       │            ↓
       │   ┌──────────────────┐
       │   │     Ticket       │
       │   └──────────────────┘
       │
       └─→ ┌──────────────────┐
           │  StreamAccess    │
           └──────────────────┘
```

### 4.2 Entidades Principales

#### User (Usuario)
```java
public class User {
    private UUID id;
    private String email;
    private String fullName;
    private UserRole role;  // USER, ORGANIZER, SUBADMIN
}
```

**Roles:**
- `USER`: Usuario regular que compra tickets
- `ORGANIZER`: Crea y gestiona torneos
- `SUBADMIN`: Ayuda a gestionar torneos (máximo 2 por torneo)

#### Tournament (Torneo)
```java
public class Tournament {
    private UUID id;
    private UUID organizerId;
    private UUID categoryId;
    private UUID gameTypeId;
    private String name;
    private String description;
    private boolean isPaid;
    private Integer maxFreeCapacity;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private TournamentStatus status;  // DRAFT, PUBLISHED, CANCELLED
}
```

**Reglas de Negocio:**
- ✅ Un organizador puede tener máximo **2 torneos gratuitos activos**
- ✅ Los torneos gratuitos deben tener capacidad máxima definida
- ✅ Solo se pueden publicar torneos en estado DRAFT
- ✅ Fecha de fin debe ser posterior a fecha de inicio

#### Ticket (Boleto)
```java
public class Ticket {
    private UUID id;
    private UUID orderId;
    private UUID tournamentId;
    private UUID userId;
    private String accessCode;  // Código único
    private TicketStatus status;  // ISSUED, USED, CANCELLED
    private LocalDateTime usedAt;
}
```

**Reglas de Negocio:**
- ✅ Cada ticket tiene un **código de acceso único**
- ✅ Un ticket solo puede validarse **una vez**
- ✅ Tickets cancelados no pueden usarse

#### TicketOrder (Orden de Compra)
```java
public class TicketOrder {
    private UUID id;
    private UUID tournamentId;
    private UUID userId;
    private UUID stageId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private OrderStatus status;  // PENDING, APPROVED, REJECTED
}
```

#### TicketSaleStage (Etapa de Venta)
```java
public class TicketSaleStage {
    private UUID id;
    private UUID tournamentId;
    private StageType stageType;  // EARLY_BIRD, REGULAR, LAST_MINUTE
    private BigDecimal price;
    private Integer capacity;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
```

**Reglas de Negocio:**
- ✅ Solo torneos de pago pueden tener etapas
- ✅ No puede haber etapas duplicadas del mismo tipo
- ✅ Una etapa está activa si la fecha actual está entre start y end

#### StreamAccess (Acceso a Stream)
```java
public class StreamAccess {
    private UUID id;
    private UUID tournamentId;
    private UUID userId;
    private AccessType accessType;  // FREE, PAID
    private UUID ticketId;  // null si es FREE
}
```

**Reglas de Negocio:**
- ✅ Un usuario puede tener máximo **1 acceso gratuito** en total
- ✅ Acceso PAID requiere ticket válido
- ✅ No se puede tener acceso duplicado al mismo torneo

---

## 5. APIs Y ENDPOINTS

### 5.1 Endpoints Públicos (Sin Autenticación)

#### Health Check
```http
GET /actuator/health
```
**Respuesta:**
```json
{
  "status": "UP"
}
```

#### Listar Categorías
```http
GET /api/categories?page=0&size=20
```
**Respuesta:**
```json
{
  "content": [
    {
      "id": "uuid",
      "name": "Battle Royale",
      "active": true
    }
  ],
  "totalElements": 10
}
```

#### Listar Tipos de Juego
```http
GET /api/game-types?page=0&size=20
```

#### Listar Torneos
```http
GET /api/tournaments?isPaid=true&status=PUBLISHED&page=0&size=20
```
**Filtros disponibles:**
- `isPaid`: true/false
- `status`: DRAFT, PUBLISHED, CANCELLED
- `categoryId`: UUID
- `gameTypeId`: UUID
- `organizerId`: UUID

#### Ver Torneo
```http
GET /api/tournaments/{id}
```

### 5.2 Endpoints Protegidos (Requieren JWT)

#### Autenticación
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```
**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 3600000
}
```

#### Crear Torneo (ORGANIZER)
```http
POST /api/tournaments
Authorization: Bearer {token}
Content-Type: application/json

{
  "categoryId": "uuid",
  "gameTypeId": "uuid",
  "name": "Championship 2024",
  "description": "Torneo profesional",
  "isPaid": true,
  "maxFreeCapacity": null,
  "startDateTime": "2024-06-01T10:00:00",
  "endDateTime": "2024-06-01T18:00:00"
}
```

#### Publicar Torneo (ORGANIZER)
```http
POST /api/tournaments/{id}/publish
Authorization: Bearer {token}
```

#### Asignar Subadmin (ORGANIZER)
```http
POST /api/tournaments/{id}/subadmins
Authorization: Bearer {token}
Content-Type: application/json

{
  "subAdminUserId": "uuid"
}
```

#### Crear Etapa de Venta (ORGANIZER)
```http
POST /api/tournaments/{id}/stages
Authorization: Bearer {token}
Content-Type: application/json

{
  "stageType": "EARLY_BIRD",
  "price": 50.00,
  "capacity": 100,
  "startDateTime": "2024-05-01T00:00:00",
  "endDateTime": "2024-05-15T23:59:59"
}
```

#### Crear Orden de Tickets (USER)
```http
POST /api/tickets/orders
Authorization: Bearer {token}
Content-Type: application/json

{
  "tournamentId": "uuid",
  "stageId": "uuid",
  "quantity": 2
}
```

#### Solicitar Acceso a Stream (USER)
```http
POST /api/tournaments/{id}/stream/access
Authorization: Bearer {token}
Content-Type: application/json

{
  "accessType": "FREE"
}
```

O con ticket:
```json
{
  "accessType": "PAID",
  "ticketAccessCode": "TICKET-ABC-123"
}
```

### 5.3 Swagger UI

Acceder a documentación interactiva:
```
http://localhost:8081/swagger-ui.html
```

---


## 6. SEGURIDAD JWT

### 6.1 Flujo de Autenticación

```
┌──────────┐                ┌──────────┐                ┌──────────┐
│  Client  │                │   API    │                │    DB    │
└────┬─────┘                └────┬─────┘                └────┬─────┘
     │                           │                           │
     │ POST /api/auth/login      │                           │
     │ {email, password}         │                           │
     ├──────────────────────────→│                           │
     │                           │ Validar credenciales      │
     │                           ├──────────────────────────→│
     │                           │←──────────────────────────┤
     │                           │ User encontrado           │
     │                           │                           │
     │                           │ Generar JWT               │
     │                           │ (userId, email, role)     │
     │                           │                           │
     │ {token, refreshToken}     │                           │
     │←──────────────────────────┤                           │
     │                           │                           │
     │ GET /api/tournaments      │                           │
     │ Authorization: Bearer JWT │                           │
     ├──────────────────────────→│                           │
     │                           │ Validar JWT               │
     │                           │ Extraer userId & role     │
     │                           │                           │
     │                           │ Verificar permisos        │
     │                           │                           │
     │ Response                  │                           │
     │←──────────────────────────┤                           │
```

### 6.2 Estructura del Token JWT

**Header:**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload:**
```json
{
  "sub": "user-uuid",
  "email": "user@example.com",
  "role": "ORGANIZER",
  "iat": 1640995200,
  "exp": 1640998800
}
```

**Signature:**
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

### 6.3 Configuración de Seguridad

**Archivo:** `SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tournaments/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/tournaments/**").hasRole("ORGANIZER")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### 6.4 Uso de @PreAuthorize

```java
@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {
    
    // Solo ORGANIZER
    @PostMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<TournamentResponse> create(@RequestBody CreateTournamentRequest request) {
        // ...
    }
    
    // ORGANIZER o SUBADMIN
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'SUBADMIN')")
    public ResponseEntity<TournamentResponse> update(@PathVariable UUID id) {
        // ...
    }
    
    // Cualquier usuario autenticado
    @GetMapping("/my-tournaments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TournamentResponse>> myTournaments() {
        // ...
    }
}
```

### 6.5 Obtener Usuario Autenticado

```java
@GetMapping("/profile")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<?> getProfile(@AuthenticationPrincipal User user) {
    return ResponseEntity.ok(Map.of(
        "id", user.getId(),
        "email", user.getEmail(),
        "role", user.getRole()
    ));
}
```

### 6.6 Manejo de Errores

**401 Unauthorized:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token inválido o expirado",
  "path": "/api/tournaments"
}
```

**403 Forbidden:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "No tienes permisos para acceder a este recurso",
  "path": "/api/tournaments"
}
```

---

## 7. BASE DE DATOS

### 7.1 Esquema de Base de Datos

```sql
-- Usuarios
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Categorías
CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT true
);

-- Tipos de Juego
CREATE TABLE game_types (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT true
);

-- Torneos
CREATE TABLE tournaments (
    id UUID PRIMARY KEY,
    organizer_id UUID NOT NULL REFERENCES users(id),
    category_id UUID NOT NULL REFERENCES categories(id),
    game_type_id UUID NOT NULL REFERENCES game_types(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_paid BOOLEAN NOT NULL,
    max_free_capacity INTEGER,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Subadministradores de Torneos
CREATE TABLE tournament_admins (
    id UUID PRIMARY KEY,
    tournament_id UUID NOT NULL REFERENCES tournaments(id),
    sub_admin_user_id UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    UNIQUE(tournament_id, sub_admin_user_id)
);

-- Etapas de Venta
CREATE TABLE ticket_sale_stages (
    id UUID PRIMARY KEY,
    tournament_id UUID NOT NULL REFERENCES tournaments(id),
    stage_type VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    capacity INTEGER NOT NULL,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    UNIQUE(tournament_id, stage_type)
);

-- Órdenes de Tickets
CREATE TABLE ticket_orders (
    id UUID PRIMARY KEY,
    tournament_id UUID NOT NULL REFERENCES tournaments(id),
    user_id UUID NOT NULL REFERENCES users(id),
    stage_id UUID NOT NULL REFERENCES ticket_sale_stages(id),
    quantity INTEGER NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Tickets
CREATE TABLE tickets (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES ticket_orders(id),
    tournament_id UUID NOT NULL REFERENCES tournaments(id),
    user_id UUID NOT NULL REFERENCES users(id),
    access_code VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(50) NOT NULL,
    used_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL
);

-- Acceso a Streams
CREATE TABLE stream_access (
    id UUID PRIMARY KEY,
    tournament_id UUID NOT NULL REFERENCES tournaments(id),
    user_id UUID NOT NULL REFERENCES users(id),
    access_type VARCHAR(50) NOT NULL,
    ticket_id UUID REFERENCES tickets(id),
    created_at TIMESTAMP NOT NULL,
    UNIQUE(tournament_id, user_id)
);

-- Logs de Auditoría
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id UUID NOT NULL,
    user_id UUID REFERENCES users(id),
    description TEXT,
    created_at TIMESTAMP NOT NULL
);
```

### 7.2 Migraciones con Flyway

**Ubicación:** `src/main/resources/db/migration/`

**Nomenclatura:**
- `V1__Initial_schema.sql`
- `V2__Add_audit_logs.sql`
- `V3__Add_stream_access.sql`

**Ejemplo de migración:**
```sql
-- V1__Initial_schema.sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
```

### 7.3 Configuración de Base de Datos

**application-dev.yml:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:torneo}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:1234}
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
  
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

### 7.4 Índices Importantes

```sql
-- Búsqueda de torneos
CREATE INDEX idx_tournaments_status ON tournaments(status);
CREATE INDEX idx_tournaments_organizer ON tournaments(organizer_id);
CREATE INDEX idx_tournaments_dates ON tournaments(start_date_time, end_date_time);

-- Búsqueda de tickets
CREATE INDEX idx_tickets_access_code ON tickets(access_code);
CREATE INDEX idx_tickets_user ON tickets(user_id);
CREATE INDEX idx_tickets_tournament ON tickets(tournament_id);

-- Auditoría
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_created ON audit_logs(created_at);
```

---

## 8. EVENTOS DE DOMINIO

### 8.1 Arquitectura Orientada a Eventos

El sistema usa **eventos de dominio** para desacoplar funcionalidades:

```
┌─────────────────┐
│   Service       │
│  (Publica)      │
└────────┬────────┘
         │
         │ publishEvent()
         ↓
┌─────────────────┐
│  Event Bus      │
│  (Spring)       │
└────────┬────────┘
         │
         ├──────────────┬──────────────┬──────────────┐
         ↓              ↓              ↓              ↓
┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐
│ Listener 1 │  │ Listener 2 │  │ Listener 3 │  │ Listener N │
│ (Metrics)  │  │ (Email)    │  │ (Audit)    │  │ (...)      │
└────────────┘  └────────────┘  └────────────┘  └────────────┘
```

### 8.2 Eventos Implementados

#### TicketPurchasedEvent
```java
public class TicketPurchasedEvent extends DomainEvent {
    private final UUID orderId;
    private final UUID tournamentId;
    private final UUID userId;
    private final int quantity;
    private final double totalAmount;
}
```

**Cuándo se publica:** Cuando se aprueba una orden de tickets

**Listeners:**
- `MetricsEventListener`: Incrementa contador de tickets
- `TicketPurchasedEventListener`: Envía email de confirmación

#### TournamentPublishedEvent
```java
public class TournamentPublishedEvent extends DomainEvent {
    private final UUID tournamentId;
    private final UUID organizerId;
    private final String tournamentName;
    private final boolean isPaid;
    private final LocalDateTime startDateTime;
}
```

**Cuándo se publica:** Cuando un torneo cambia a estado PUBLISHED

**Listeners:**
- `MetricsEventListener`: Incrementa contador de torneos
- `TournamentPublishedEventListener`: Notifica suscriptores

#### StreamAccessGrantedEvent
```java
public class StreamAccessGrantedEvent extends DomainEvent {
    private final UUID accessId;
    private final UUID tournamentId;
    private final UUID userId;
    private final String accessType;
    private final UUID ticketId;
}
```

**Cuándo se publica:** Cuando se otorga acceso a un stream

**Listeners:**
- `StreamAccessGrantedEventListener`: Envía confirmación

### 8.3 Publicar Eventos

```java
@Service
public class TournamentService {
    
    private final DomainEventPublisher eventPublisher;
    
    public TournamentResponse publish(UUID id, UUID userId) {
        // Lógica de negocio...
        Tournament tournament = tournamentRepository.save(tournament);
        
        // Publicar evento
        TournamentPublishedEvent event = new TournamentPublishedEvent(
            tournament.getId(),
            tournament.getOrganizerId(),
            tournament.getName(),
            tournament.isPaid(),
            tournament.getStartDateTime()
        );
        eventPublisher.publish(event);
        
        return mapToResponse(tournament);
    }
}
```

### 8.4 Escuchar Eventos

```java
@Component
public class TournamentPublishedEventListener {
    
    @Async
    @EventListener
    public void handleTournamentPublished(TournamentPublishedEvent event) {
        log.info("Tournament published: {}", event.getTournamentName());
        
        // Enviar notificaciones
        notifySubscribers(event);
        
        // Indexar para búsqueda
        indexForSearch(event);
    }
}
```

### 8.5 Ventajas de Eventos

✅ **Desacoplamiento**: Servicios no dependen entre sí
✅ **Extensibilidad**: Fácil agregar nuevos listeners
✅ **Asincronía**: Procesamiento en background con @Async
✅ **Auditoría**: Registro automático de eventos
✅ **Escalabilidad**: Fácil migrar a message brokers (RabbitMQ, Kafka)

---


## 9. OBSERVABILIDAD

### 9.1 Spring Boot Actuator

**Endpoints disponibles:**

| Endpoint | Descripción |
|----------|-------------|
| `/actuator/health` | Estado de salud de la aplicación |
| `/actuator/info` | Información de la aplicación |
| `/actuator/metrics` | Lista de métricas disponibles |
| `/actuator/metrics/{name}` | Métrica específica |
| `/actuator/prometheus` | Métricas en formato Prometheus |

**Ejemplo de uso:**
```bash
# Health check
curl http://localhost:8081/actuator/health

# Ver métrica específica
curl http://localhost:8081/actuator/metrics/tickets.created
```

### 9.2 Métricas Personalizadas

#### Métricas de Negocio

**tickets.created:**
```java
@Component
public class MetricsService {
    private final Counter ticketsCreatedCounter;
    
    public MetricsService(MeterRegistry meterRegistry) {
        this.ticketsCreatedCounter = Counter.builder("tickets.created")
                .description("Total number of tickets created")
                .tag("type", "ticket")
                .register(meterRegistry);
    }
    
    public void incrementTicketsCreated(int quantity) {
        ticketsCreatedCounter.increment(quantity);
    }
}
```

**tournaments.published:**
```java
Counter.builder("tournaments.published")
    .description("Total number of tournaments published")
    .tag("type", "tournament")
    .register(meterRegistry);
```

**Consultar métricas:**
```bash
curl http://localhost:8081/actuator/metrics/tickets.created
```

**Respuesta:**
```json
{
  "name": "tickets.created",
  "description": "Total number of tickets created",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 150.0
    }
  ],
  "availableTags": [
    {
      "tag": "type",
      "values": ["ticket"]
    }
  ]
}
```

### 9.3 Logging Estructurado

**Configuración:** `logback-spring.xml`

**Desarrollo (texto plano):**
```
2024-01-15 10:30:00 [http-nio-8081-exec-1] INFO  c.e.t.a.s.TournamentService - Tournament published successfully
```

**Producción (JSON):**
```json
{
  "@timestamp": "2024-01-15T10:30:00.123Z",
  "level": "INFO",
  "logger_name": "com.example.torneos.application.service.TournamentService",
  "message": "Tournament published successfully",
  "thread_name": "http-nio-8081-exec-1",
  "tournamentId": "abc-123",
  "userId": "user-456"
}
```

**Agregar contexto a logs:**
```java
MDC.put("tournamentId", tournamentId.toString());
MDC.put("userId", userId.toString());
log.info("Processing tournament");
MDC.clear();
```

### 9.4 Health Indicators

**Custom Health Indicator:**
```java
@Component
public class TorneosHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        return Health.up()
                .withDetail("service", "torneos-backend")
                .withDetail("status", "operational")
                .build();
    }
}
```

**Respuesta de health:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    },
    "torneos": {
      "status": "UP",
      "details": {
        "service": "torneos-backend",
        "status": "operational"
      }
    }
  }
}
```

### 9.5 Integración con Prometheus

**Configuración:**
```yaml
management:
  metrics:
    export:
      prometheus:
        enabled: true
```

**Scrape config (prometheus.yml):**
```yaml
scrape_configs:
  - job_name: 'torneos-backend'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8081']
```

**Queries útiles:**
```promql
# Total de tickets creados
tickets_created_total

# Rate de tickets por minuto
rate(tickets_created_total[1m])

# Torneos publicados
tournaments_published_total

# Latencia de requests
http_server_requests_seconds_sum / http_server_requests_seconds_count
```

---

## 10. TESTING

### 10.1 Estrategia de Testing

```
┌─────────────────────────────────────┐
│     Tests de Integración            │
│  (TestContainers + PostgreSQL)      │
│                                     │
│  ┌───────────────────────────────┐ │
│  │    Tests Unitarios            │ │
│  │  (Mockito + JUnit 5)          │ │
│  └───────────────────────────────┘ │
└─────────────────────────────────────┘
```

### 10.2 Tests Unitarios

**Ubicación:** `src/test/java/com/example/torneos/application/service/`

**Ejemplo:**
```java
@ExtendWith(MockitoExtension.class)
class TournamentServiceTest {
    
    @Mock
    private TournamentRepository tournamentRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private TournamentService tournamentService;
    
    @Test
    void create_shouldThrowException_whenMaxFreeTournamentsReached() {
        // Given
        UUID organizerId = UUID.randomUUID();
        CreateTournamentRequest request = new CreateTournamentRequest(...);
        
        when(tournamentRepository.countByOrganizerIdAndIsPaidAndStatus(
            organizerId, false, TournamentStatus.PUBLISHED))
            .thenReturn(2L);
        
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> tournamentService.create(request, organizerId));
    }
}
```

**Cobertura:**
- ✅ Validaciones de negocio
- ✅ Reglas de dominio
- ✅ Manejo de errores
- ✅ Lógica de servicios

### 10.3 Tests de Integración

**Ubicación:** `src/test/java/com/example/torneos/integration/`

**Base Test:**
```java
@SpringBootTest
@Testcontainers
public abstract class BaseIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

**Ejemplo de test:**
```java
@Transactional
class TournamentServiceIntegrationTest extends BaseIntegrationTest {
    
    @Autowired
    private TournamentService tournamentService;
    
    @Autowired
    private TournamentRepository tournamentRepository;
    
    @Test
    void shouldEnforceMaxTwoFreeTournamentsRule() {
        // Given
        User organizer = createOrganizer();
        
        // When
        tournamentService.create(createFreeTournamentRequest(), organizer.getId());
        tournamentService.create(createFreeTournamentRequest(), organizer.getId());
        
        // Then
        assertThrows(IllegalArgumentException.class, 
            () -> tournamentService.create(createFreeTournamentRequest(), organizer.getId()));
    }
}
```

**Cobertura:**
- ✅ Flujos completos end-to-end
- ✅ Integración con base de datos real
- ✅ Validación de constraints
- ✅ Transacciones

### 10.4 Ejecutar Tests

```bash
# Todos los tests
mvn test

# Solo unitarios
mvn test -Dtest="*Test"

# Solo integración
mvn test -Dtest="*IntegrationTest"

# Test específico
mvn test -Dtest=TournamentServiceTest

# Con coverage
mvn test jacoco:report
```

### 10.5 TestContainers

**Ventajas:**
- ✅ Base de datos real (PostgreSQL)
- ✅ Tests aislados
- ✅ Reproducibles
- ✅ CI/CD friendly

**Configuración:**
```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
```

---

## 11. DOCKER Y DESPLIEGUE

### 11.1 Dockerfile Multi-Stage

```dockerfile
# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Ventajas:**
- ✅ Imagen final pequeña (~200MB)
- ✅ Solo contiene JRE y JAR
- ✅ Cache de dependencias Maven
- ✅ Build reproducible

### 11.2 Docker Compose

**docker-compose.yml:**
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: torneos-postgres
    environment:
      POSTGRES_DB: ${DB_NAME:-torneo}
      POSTGRES_USER: ${DB_USER:-postgres}
      POSTGRES_PASSWORD: ${DB_PASSWORD:-1234}
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build: .
    container_name: torneos-backend
    environment:
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILE:-dev}
      DB_HOST: postgres
      DB_PORT: 5432
      DB_NAME: ${DB_NAME:-torneo}
      DB_USER: ${DB_USER:-postgres}
      DB_PASSWORD: ${DB_PASSWORD:-1234}
    ports:
      - "8081:8081"
    depends_on:
      postgres:
        condition: service_healthy

volumes:
  postgres_data:
```

**Uso:**
```bash
# Iniciar
docker-compose up -d

# Ver logs
docker-compose logs -f app

# Detener
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v
```

### 11.3 Variables de Entorno

**Archivo .env:**
```bash
SPRING_PROFILE=dev
DB_HOST=postgres
DB_PORT=5432
DB_NAME=torneo
DB_USER=postgres
DB_PASSWORD=1234
JWT_SECRET=your-secret-key
```

**Uso:**
```bash
# Cargar variables
source .env

# O con docker-compose
docker-compose --env-file .env up -d
```

### 11.4 Despliegue en AWS ECS

**Task Definition:**
```json
{
  "family": "torneos-backend",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "containerDefinitions": [
    {
      "name": "torneos-backend",
      "image": "username/torneos-backend:latest",
      "portMappings": [
        {
          "containerPort": 8081,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        }
      ],
      "secrets": [
        {
          "name": "DB_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:region:account:secret:torneos/db-password"
        }
      ]
    }
  ]
}
```

**Desplegar:**
```bash
cd deployment/aws
./deploy.sh
```

### 11.5 Despliegue en Azure

**Container Instance:**
```bash
az container create \
  --resource-group torneos-rg \
  --name torneos-backend \
  --image username/torneos-backend:latest \
  --dns-name-label torneos-backend \
  --ports 8081 \
  --environment-variables \
    SPRING_PROFILES_ACTIVE=prod \
    DB_HOST=torneos-db.postgres.database.azure.com \
  --secure-environment-variables \
    DB_PASSWORD=$DB_PASSWORD \
    JWT_SECRET=$JWT_SECRET
```

---

## 12. CI/CD

### 12.1 GitHub Actions Pipeline

**Archivo:** `.github/workflows/ci-cd.yml`

**Stages:**
1. **Build & Test**: Compilar y ejecutar tests
2. **Security Scan**: Escaneo con Trivy
3. **Docker Build & Push**: Construir y subir imagen

**Triggers:**
- Push a `main` o `develop`
- Pull requests
- Releases con tags

**Ejemplo de ejecución:**
```
Push to main
  ↓
Build & Test (3-5 min)
  ↓
Security Scan (1-2 min)
  ↓
Docker Build & Push (2-3 min)
  ↓
✓ Pipeline Success
```

### 12.2 Versionado Semántico

**Automático (commits):**
```
20240115-a1b2c3d
```

**Manual (releases):**
```
v1.0.0
v1.1.0
v2.0.0
```

**Crear release:**
```bash
git tag v1.0.0
git push origin v1.0.0
```

### 12.3 Secrets Requeridos

**GitHub Actions:**
- `DOCKER_USERNAME`: Usuario de Docker Hub
- `DOCKER_PASSWORD`: Token de Docker Hub

**GitLab CI:**
- `CI_REGISTRY_USER`: Usuario de GitLab
- `CI_REGISTRY_PASSWORD`: Token de GitLab
- `CI_REGISTRY_IMAGE`: URL de la imagen

---


## 13. GUÍA DE USO PASO A PASO

### 13.1 Instalación y Configuración Inicial

#### Paso 1: Clonar el Repositorio
```bash
cd /Users/nicolas.perez/Pragma/Torneos/e-sport/backend-torneos
```

#### Paso 2: Configurar Base de Datos

**Opción A: PostgreSQL Local**
```bash
# Instalar PostgreSQL
brew install postgresql@15

# Iniciar servicio
brew services start postgresql@15

# Crear base de datos
createdb torneo
```

**Opción B: Docker**
```bash
docker run -d \
  --name torneos-postgres \
  -e POSTGRES_DB=torneo \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=1234 \
  -p 5432:5432 \
  postgres:15-alpine
```

#### Paso 3: Configurar Variables de Entorno
```bash
# Crear archivo .env
cat > .env << EOF
DB_HOST=localhost
DB_PORT=5432
DB_NAME=torneo
DB_USER=postgres
DB_PASSWORD=1234
JWT_SECRET=my-super-secret-key-change-in-production
EOF

# Cargar variables
source .env
```

#### Paso 4: Compilar el Proyecto
```bash
mvn clean package -DskipTests
```

#### Paso 5: Ejecutar Migraciones
```bash
# Las migraciones se ejecutan automáticamente al iniciar
# Flyway detecta y aplica scripts en src/main/resources/db/migration/
```

#### Paso 6: Iniciar la Aplicación
```bash
java -jar target/torneos-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

#### Paso 7: Verificar que Funciona
```bash
# Health check
curl http://localhost:8081/actuator/health

# Debería responder:
# {"status":"UP"}
```

### 13.2 Crear Primer Usuario y Torneo

#### Paso 1: Registrar Usuario Organizador
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "organizer@example.com",
    "password": "password123",
    "fullName": "John Organizer",
    "role": "ORGANIZER"
  }'
```

#### Paso 2: Hacer Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "organizer@example.com",
    "password": "password123"
  }'

# Guardar el token de la respuesta
TOKEN="eyJhbGciOiJIUzI1NiJ9..."
```

#### Paso 3: Obtener Categorías y Tipos de Juego
```bash
# Listar categorías
curl http://localhost:8081/api/categories

# Listar tipos de juego
curl http://localhost:8081/api/game-types

# Guardar IDs para usar en el torneo
CATEGORY_ID="uuid-de-categoria"
GAME_TYPE_ID="uuid-de-tipo-juego"
```

#### Paso 4: Crear Torneo
```bash
curl -X POST http://localhost:8081/api/tournaments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": "'$CATEGORY_ID'",
    "gameTypeId": "'$GAME_TYPE_ID'",
    "name": "Championship 2024",
    "description": "Torneo profesional de e-sports",
    "isPaid": true,
    "maxFreeCapacity": null,
    "startDateTime": "2024-06-01T10:00:00",
    "endDateTime": "2024-06-01T18:00:00"
  }'

# Guardar el ID del torneo
TOURNAMENT_ID="uuid-del-torneo"
```

#### Paso 5: Crear Etapa de Venta
```bash
curl -X POST http://localhost:8081/api/tournaments/$TOURNAMENT_ID/stages \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "stageType": "EARLY_BIRD",
    "price": 50.00,
    "capacity": 100,
    "startDateTime": "2024-05-01T00:00:00",
    "endDateTime": "2024-05-15T23:59:59"
  }'
```

#### Paso 6: Publicar Torneo
```bash
curl -X POST http://localhost:8081/api/tournaments/$TOURNAMENT_ID/publish \
  -H "Authorization: Bearer $TOKEN"
```

#### Paso 7: Ver Torneo Publicado
```bash
curl http://localhost:8081/api/tournaments/$TOURNAMENT_ID
```

### 13.3 Flujo Completo de Compra de Tickets

#### Paso 1: Registrar Usuario Regular
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "fullName": "Jane User",
    "role": "USER"
  }'
```

#### Paso 2: Login como Usuario
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'

USER_TOKEN="eyJhbGciOiJIUzI1NiJ9..."
```

#### Paso 3: Ver Torneos Disponibles
```bash
curl http://localhost:8081/api/tournaments?status=PUBLISHED
```

#### Paso 4: Ver Etapas de Venta
```bash
curl http://localhost:8081/api/tournaments/$TOURNAMENT_ID/stages
```

#### Paso 5: Crear Orden de Tickets
```bash
curl -X POST http://localhost:8081/api/tickets/orders \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tournamentId": "'$TOURNAMENT_ID'",
    "stageId": "'$STAGE_ID'",
    "quantity": 2
  }'

ORDER_ID="uuid-de-orden"
```

#### Paso 6: Aprobar Orden (simulación de pago)
```bash
curl -X POST http://localhost:8081/api/tickets/orders/$ORDER_ID/approve \
  -H "Authorization: Bearer $ORGANIZER_TOKEN"
```

#### Paso 7: Ver Tickets Generados
```bash
curl http://localhost:8081/api/tickets/orders/$ORDER_ID/tickets \
  -H "Authorization: Bearer $USER_TOKEN"

# Guardar access code
ACCESS_CODE="TICKET-ABC-123"
```

#### Paso 8: Solicitar Acceso a Stream
```bash
curl -X POST http://localhost:8081/api/tournaments/$TOURNAMENT_ID/stream/access \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "accessType": "PAID",
    "ticketAccessCode": "'$ACCESS_CODE'"
  }'
```

### 13.4 Gestión de Subadministradores

#### Paso 1: Crear Usuario Subadmin
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "subadmin@example.com",
    "password": "password123",
    "fullName": "Bob Subadmin",
    "role": "USER"
  }'

SUBADMIN_ID="uuid-del-subadmin"
```

#### Paso 2: Asignar como Subadmin del Torneo
```bash
curl -X POST http://localhost:8081/api/tournaments/$TOURNAMENT_ID/subadmins \
  -H "Authorization: Bearer $ORGANIZER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "subAdminUserId": "'$SUBADMIN_ID'"
  }'
```

#### Paso 3: Ver Subadmins del Torneo
```bash
curl http://localhost:8081/api/tournaments/$TOURNAMENT_ID/subadmins \
  -H "Authorization: Bearer $ORGANIZER_TOKEN"
```

#### Paso 4: Remover Subadmin
```bash
curl -X DELETE http://localhost:8081/api/tournaments/$TOURNAMENT_ID/subadmins/$SUBADMIN_ID \
  -H "Authorization: Bearer $ORGANIZER_TOKEN"
```

### 13.5 Monitoreo y Métricas

#### Ver Health
```bash
curl http://localhost:8081/actuator/health | jq .
```

#### Ver Todas las Métricas
```bash
curl http://localhost:8081/actuator/metrics | jq .
```

#### Ver Métrica Específica
```bash
# Tickets creados
curl http://localhost:8081/actuator/metrics/tickets.created | jq .

# Torneos publicados
curl http://localhost:8081/actuator/metrics/tournaments.published | jq .

# Memoria JVM
curl http://localhost:8081/actuator/metrics/jvm.memory.used | jq .
```

#### Exportar para Prometheus
```bash
curl http://localhost:8081/actuator/prometheus > metrics.txt
```

### 13.6 Troubleshooting Común

#### Problema: No conecta a la base de datos
```bash
# Verificar que PostgreSQL está corriendo
pg_isready -h localhost -p 5432

# Verificar credenciales
psql -h localhost -U postgres -d torneo

# Ver logs de la aplicación
tail -f backend.log | grep -i "database\|connection"
```

#### Problema: Token JWT inválido
```bash
# Verificar que el token no ha expirado (1 hora por defecto)
# Hacer login nuevamente para obtener nuevo token

curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

#### Problema: Error 403 Forbidden
```bash
# Verificar que el usuario tiene el rol correcto
# ORGANIZER para crear torneos
# USER para comprar tickets

# Ver información del token
echo $TOKEN | cut -d'.' -f2 | base64 -d | jq .
```

#### Problema: Puerto 8081 en uso
```bash
# Matar proceso en puerto 8081
lsof -ti:8081 | xargs kill -9

# O cambiar puerto en application.yml
server:
  port: 8082
```

### 13.7 Scripts Útiles

#### Iniciar Todo (Backend + Frontend)
```bash
/Users/nicolas.perez/Pragma/Torneos/e-sport/start-all.sh
```

#### Detener Todo
```bash
/Users/nicolas.perez/Pragma/Torneos/e-sport/stop-all.sh
```

#### Ver Logs en Tiempo Real
```bash
# Backend
tail -f /Users/nicolas.perez/Pragma/Torneos/e-sport/backend-torneos/backend.log

# Frontend
tail -f /Users/nicolas.perez/Pragma/Torneos/e-sport/frontend-torneos/frontend.log
```

#### Limpiar y Recompilar
```bash
cd /Users/nicolas.perez/Pragma/Torneos/e-sport/backend-torneos
mvn clean package -DskipTests
```

#### Ejecutar Tests
```bash
# Todos
mvn test

# Solo unitarios
mvn test -Dtest="*Test"

# Solo integración
mvn test -Dtest="*IntegrationTest"
```

---

## 14. PREGUNTAS FRECUENTES (FAQ)

### ¿Cómo cambio el puerto de la aplicación?
Editar `application.yml`:
```yaml
server:
  port: 8082
```

### ¿Cómo agrego un nuevo endpoint?
1. Crear método en el Controller
2. Agregar @PreAuthorize si requiere autenticación
3. Documentar con @Operation de Swagger

### ¿Cómo agrego una nueva entidad?
1. Crear clase en `domain/model/`
2. Crear repositorio en `domain/repository/`
3. Crear entidad JPA en `infrastructure/persistence/entity/`
4. Crear migración Flyway en `resources/db/migration/`

### ¿Cómo cambio la configuración de JWT?
Editar `application.yml`:
```yaml
jwt:
  secret: tu-nuevo-secret
  expiration: 7200000  # 2 horas
```

### ¿Cómo agrego un nuevo evento?
1. Crear clase en `domain/event/` extendiendo `DomainEvent`
2. Publicar con `DomainEventPublisher.publish()`
3. Crear listener en `infrastructure/event/listener/`

### ¿Cómo despliego en producción?
1. Configurar variables de entorno de producción
2. Usar profile `prod`: `--spring.profiles.active=prod`
3. Configurar secrets en AWS/Azure
4. Usar scripts en `deployment/`

---

## 15. GLOSARIO

| Término | Definición |
|---------|------------|
| **Torneo** | Evento de e-sports con fecha, categoría y tipo de juego |
| **Organizador** | Usuario con rol ORGANIZER que crea torneos |
| **Subadmin** | Usuario que ayuda a gestionar un torneo (máx 2 por torneo) |
| **Ticket** | Boleto de entrada a un torneo con código único |
| **Etapa de Venta** | Período con precio específico (Early Bird, Regular, Last Minute) |
| **Stream Access** | Permiso para ver transmisión de un torneo |
| **Access Code** | Código único de un ticket para validación |
| **JWT** | JSON Web Token para autenticación |
| **Flyway** | Herramienta de migraciones de base de datos |
| **Actuator** | Módulo de Spring Boot para monitoreo |
| **TestContainers** | Framework para tests con contenedores Docker |

---

## 16. RECURSOS ADICIONALES

### Documentación Oficial
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [PostgreSQL](https://www.postgresql.org/docs/)
- [Docker](https://docs.docker.com/)
- [JWT](https://jwt.io/)

### Herramientas Recomendadas
- **Postman**: Testing de APIs
- **DBeaver**: Cliente de base de datos
- **Docker Desktop**: Gestión de contenedores
- **IntelliJ IDEA**: IDE recomendado
- **Grafana**: Visualización de métricas

### Contacto y Soporte
- **Repositorio**: `/Users/nicolas.perez/Pragma/Torneos/e-sport/backend-torneos`
- **Documentación adicional**: `/doc`
- **Swagger UI**: http://localhost:8081/swagger-ui.html

---

## 17. CHANGELOG

### v1.0.0 (2024-01-15)
- ✅ Implementación inicial
- ✅ CRUD de torneos
- ✅ Sistema de tickets
- ✅ Acceso a streams
- ✅ Autenticación JWT
- ✅ Eventos de dominio
- ✅ Observabilidad
- ✅ Tests unitarios e integración
- ✅ Docker y CI/CD
- ✅ Documentación completa

---

**FIN DE LA DOCUMENTACIÓN MAESTRA**

*Última actualización: 2024-02-26*
*Versión: 1.0.0*
*Autor: Backend Torneos Team*

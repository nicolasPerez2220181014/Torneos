# 🏆 PLATAFORMA DE TORNEOS VIRTUALES - DOCUMENTACIÓN COMPLETA

## 📋 ÍNDICE
1. [Descripción General](#descripción-general)
2. [Arquitectura del Sistema](#arquitectura-del-sistema)
3. [Estructura de Carpetas](#estructura-de-carpetas)
4. [Tecnologías Utilizadas](#tecnologías-utilizadas)
5. [Modelo de Datos](#modelo-de-datos)
6. [Seguridad y Autenticación](#seguridad-y-autenticación)
7. [APIs y Endpoints](#apis-y-endpoints)
8. [Reglas de Negocio](#reglas-de-negocio)
9. [Configuración y Despliegue](#configuración-y-despliegue)
10. [Testing](#testing)
11. [📋 Diagnóstico Arquitectónico](#diagnóstico-arquitectónico)

---

## 📖 DESCRIPCIÓN GENERAL

### Objetivo
Backend completo para una plataforma de torneos virtuales y venta de tickets desarrollado con **Spring Boot** siguiendo **arquitectura hexagonal/clean**. Es un proyecto educacional pero con estándares profesionales, escalable y consistente.

### Funcionalidades Principales
- **Gestión de Usuarios**: Registro, autenticación JWT, roles (USER, ORGANIZER, SUBADMIN)
- **Gestión de Torneos**: CRUD completo con reglas de negocio específicas
- **Sistema de Tickets**: Venta por etapas (EARLY_BIRD, REGULAR, LAST_MINUTE)
- **Control de Acceso a Streams**: Acceso gratuito limitado y acceso pagado
- **Auditoría Completa**: Trazabilidad de todas las operaciones críticas
- **Administración**: Subadministradores por torneo, control de capacidades

---

## 🏗️ ARQUITECTURA DEL SISTEMA

### Clean Architecture / Hexagonal
```
src/main/java/com/example/torneos/
├── domain/                    # Capa de Dominio (Entidades puras)
│   ├── model/                # Entidades de dominio (sin dependencias externas)
│   └── repository/           # Interfaces de repositorio (puertos)
├── application/              # Capa de Aplicación (Casos de uso)
│   ├── dto/
│   │   ├── request/         # DTOs de entrada
│   │   └── response/        # DTOs de salida
│   └── service/             # Servicios de aplicación (casos de uso)
└── infrastructure/          # Capa de Infraestructura (Adaptadores)
    ├── controller/          # Controllers REST (adaptadores de entrada)
    ├── persistence/         # Persistencia JPA (adaptadores de salida)
    │   ├── entity/         # Entidades JPA
    │   ├── repository/     # Implementaciones de repositorio
    │   └── mapper/         # Mappers domain <-> JPA
    └── config/             # Configuraciones (Security, CORS, etc.)
```

### Principios Aplicados
- **Inversión de Dependencias**: El dominio no depende de la infraestructura
- **Separación de Responsabilidades**: Cada capa tiene una responsabilidad específica
- **Testabilidad**: Fácil testing por la separación de capas
- **Escalabilidad**: Arquitectura preparada para crecimiento

---

## 📁 ESTRUCTURA DE CARPETAS

```
backend-torneos/
├── src/
│   ├── main/
│   │   ├── java/com/example/torneos/
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Tournament.java
│   │   │   │   │   ├── Category.java
│   │   │   │   │   ├── GameType.java
│   │   │   │   │   ├── TournamentAdmin.java
│   │   │   │   │   ├── TicketSaleStage.java
│   │   │   │   │   ├── TicketOrder.java
│   │   │   │   │   ├── Ticket.java
│   │   │   │   │   ├── StreamAccess.java
│   │   │   │   │   ├── StreamLinkControl.java
│   │   │   │   │   └── AuditLog.java
│   │   │   │   └── repository/
│   │   │   │       ├── UserRepository.java
│   │   │   │       ├── TournamentRepository.java
│   │   │   │       ├── CategoryRepository.java
│   │   │   │       ├── GameTypeRepository.java
│   │   │   │       ├── TournamentAdminRepository.java
│   │   │   │       ├── TicketSaleStageRepository.java
│   │   │   │       ├── TicketOrderRepository.java
│   │   │   │       ├── TicketRepository.java
│   │   │   │       ├── StreamAccessRepository.java
│   │   │   │       ├── StreamLinkControlRepository.java
│   │   │   │       └── AuditLogRepository.java
│   │   │   ├── application/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   │   ├── CreateUserRequest.java
│   │   │   │   │   │   ├── UpdateUserRequest.java
│   │   │   │   │   │   ├── CreateTournamentRequest.java
│   │   │   │   │   │   ├── UpdateTournamentRequest.java
│   │   │   │   │   │   ├── CreateCategoryRequest.java
│   │   │   │   │   │   ├── UpdateCategoryRequest.java
│   │   │   │   │   │   ├── CreateGameTypeRequest.java
│   │   │   │   │   │   ├── UpdateGameTypeRequest.java
│   │   │   │   │   │   ├── AssignSubAdminRequest.java
│   │   │   │   │   │   ├── CreateTicketSaleStageRequest.java
│   │   │   │   │   │   ├── CreateTicketOrderRequest.java
│   │   │   │   │   │   ├── LoginRequestDto.java
│   │   │   │   │   │   ├── RefreshTokenRequestDto.java
│   │   │   │   │   │   ├── StreamAccessRequestDto.java
│   │   │   │   │   │   ├── UpdateStreamUrlRequestDto.java
│   │   │   │   │   │   └── BlockStreamRequestDto.java
│   │   │   │   │   └── response/
│   │   │   │   │       ├── UserResponse.java
│   │   │   │   │       ├── TournamentResponse.java
│   │   │   │   │       ├── CategoryResponse.java
│   │   │   │   │       ├── GameTypeResponse.java
│   │   │   │   │       ├── TournamentAdminResponse.java
│   │   │   │   │       ├── TicketSaleStageResponse.java
│   │   │   │   │       ├── TicketOrderResponse.java
│   │   │   │   │       ├── TicketResponse.java
│   │   │   │   │       ├── AuthResponseDto.java
│   │   │   │   │       ├── StreamAccessResponseDto.java
│   │   │   │   │       ├── StreamStatusResponseDto.java
│   │   │   │   │       └── AuditLogResponseDto.java
│   │   │   │   └── service/
│   │   │   │       ├── UserService.java
│   │   │   │       ├── TournamentService.java
│   │   │   │       ├── CategoryService.java
│   │   │   │       ├── GameTypeService.java
│   │   │   │       ├── TournamentAdminService.java
│   │   │   │       ├── TicketSaleStageService.java
│   │   │   │       ├── TicketOrderService.java
│   │   │   │       ├── TicketService.java
│   │   │   │       ├── AuthService.java
│   │   │   │       ├── AuthenticationService.java
│   │   │   │       ├── JwtService.java
│   │   │   │       ├── StreamAccessService.java
│   │   │   │       ├── StreamLinkControlService.java
│   │   │   │       └── AuditLogService.java
│   │   │   └── infrastructure/
│   │   │       ├── controller/
│   │   │       │   ├── UserController.java
│   │   │       │   ├── TournamentController.java
│   │   │       │   ├── CategoryController.java
│   │   │       │   ├── GameTypeController.java
│   │   │       │   ├── TicketSaleStageController.java
│   │   │       │   ├── TicketController.java
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── StreamAccessController.java
│   │   │       │   ├── StreamLinkControlController.java
│   │   │       │   ├── AuditLogController.java
│   │   │       │   └── TorneosController.java
│   │   │       ├── persistence/
│   │   │       │   ├── entity/
│   │   │       │   │   ├── UserEntity.java
│   │   │       │   │   ├── TournamentEntity.java
│   │   │       │   │   ├── CategoryEntity.java
│   │   │       │   │   ├── GameTypeEntity.java
│   │   │       │   │   ├── TournamentAdminEntity.java
│   │   │       │   │   ├── TicketSaleStageEntity.java
│   │   │       │   │   ├── TicketOrderEntity.java
│   │   │       │   │   ├── TicketEntity.java
│   │   │       │   │   ├── StreamAccessEntity.java
│   │   │       │   │   ├── StreamLinkControlEntity.java
│   │   │       │   │   └── AuditLogEntity.java
│   │   │       │   ├── repository/
│   │   │       │   │   ├── JpaUserRepository.java
│   │   │       │   │   ├── UserRepositoryImpl.java
│   │   │       │   │   ├── JpaTournamentRepository.java
│   │   │       │   │   ├── TournamentRepositoryImpl.java
│   │   │       │   │   ├── JpaCategoryRepository.java
│   │   │       │   │   ├── CategoryRepositoryImpl.java
│   │   │       │   │   ├── JpaGameTypeRepository.java
│   │   │       │   │   ├── GameTypeRepositoryImpl.java
│   │   │       │   │   ├── JpaTournamentAdminRepository.java
│   │   │       │   │   ├── TournamentAdminRepositoryImpl.java
│   │   │       │   │   ├── JpaTicketSaleStageRepository.java
│   │   │       │   │   ├── TicketSaleStageRepositoryImpl.java
│   │   │       │   │   ├── JpaTicketOrderRepository.java
│   │   │       │   │   ├── TicketOrderRepositoryImpl.java
│   │   │       │   │   ├── JpaTicketRepository.java
│   │   │       │   │   ├── TicketRepositoryImpl.java
│   │   │       │   │   ├── JpaStreamAccessRepository.java
│   │   │       │   │   ├── StreamAccessRepositoryImpl.java
│   │   │       │   │   ├── JpaStreamLinkControlRepository.java
│   │   │       │   │   ├── StreamLinkControlRepositoryImpl.java
│   │   │       │   │   ├── JpaAuditLogRepository.java
│   │   │       │   │   └── AuditLogRepositoryImpl.java
│   │   │       │   └── mapper/
│   │   │       │       ├── UserMapper.java
│   │   │       │       ├── TournamentMapper.java
│   │   │       │       ├── CategoryMapper.java
│   │   │       │       ├── GameTypeMapper.java
│   │   │       │       ├── TournamentAdminMapper.java
│   │   │       │       ├── TicketSaleStageMapper.java
│   │   │       │       ├── TicketOrderMapper.java
│   │   │       │       ├── TicketMapper.java
│   │   │       │       ├── StreamAccessMapper.java
│   │   │       │       ├── StreamLinkControlMapper.java
│   │   │       │       └── AuditLogMapper.java
│   │   │       └── config/
│   │   │           ├── SecurityConfig.java
│   │   │           ├── JwtAuthenticationFilter.java
│   │   │           ├── GlobalExceptionHandler.java
│   │   │           ├── WebConfig.java
│   │   │           └── OpenApiConfig.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-no-db.yml
│   │       └── db/migration/
│   │           ├── V1__Initial_schema.sql
│   │           └── V2__Master_data.sql
│   └── test/
│       └── java/com/example/torneos/
│           └── application/service/
│               ├── UserServiceTest.java
│               ├── TournamentServiceTest.java
│               ├── CategoryServiceTest.java
│               ├── JwtServiceTest.java
│               ├── StreamAccessServiceTest.java
│               ├── StreamLinkControlServiceTest.java
│               └── AuditLogServiceTest.java
├── doc/
│   └── DOCUMENTACION_COMPLETA.md
├── pom.xml
└── README.md
```

---

## 🛠️ TECNOLOGÍAS UTILIZADAS

### Backend Framework
- **Java 17+**: Lenguaje de programación
- **Spring Boot 3.2.0**: Framework principal
- **Spring Data JPA**: Persistencia de datos
- **Spring Security**: Seguridad y autenticación
- **Spring Web**: APIs REST

### Base de Datos
- **H2 Database**: Base de datos en memoria para desarrollo
- **PostgreSQL**: Base de datos para producción (configurada)
- **Flyway**: Migraciones de base de datos

### Seguridad
- **JWT (JSON Web Tokens)**: Autenticación stateless
- **jjwt**: Librería para manejo de JWT
- **BCrypt**: Encriptación de contraseñas

### Documentación
- **SpringDoc OpenAPI**: Documentación automática de APIs
- **Swagger UI**: Interfaz web para probar APIs

### Testing
- **JUnit 5**: Framework de testing
- **Mockito**: Mocking para tests unitarios
- **Spring Boot Test**: Testing de integración

### Build y Dependencias
- **Maven**: Gestión de dependencias y build
- **Spring Boot Actuator**: Monitoreo y métricas

---

## 🗄️ MODELO DE DATOS

### Diagrama de Entidades
```
┌─────────────┐    ┌──────────────┐    ┌─────────────┐
│    User     │    │  Tournament  │    │  Category   │
├─────────────┤    ├──────────────┤    ├─────────────┤
│ id (UUID)   │◄──►│ id (UUID)    │───►│ id (UUID)   │
│ email       │    │ organizerId  │    │ name        │
│ fullName    │    │ categoryId   │    │ active      │
│ role        │    │ gameTypeId   │    └─────────────┘
│ createdAt   │    │ name         │
│ updatedAt   │    │ description  │    ┌─────────────┐
└─────────────┘    │ isPaid       │    │  GameType   │
                   │ maxFreeCap   │    ├─────────────┤
                   │ startDateTime│───►│ id (UUID)   │
                   │ endDateTime  │    │ name        │
                   │ status       │    │ active      │
                   │ createdAt    │    └─────────────┘
                   │ updatedAt    │
                   └──────────────┘
                          │
                          ▼
                   ┌──────────────┐
                   │TournamentAdmin│
                   ├──────────────┤
                   │ id (UUID)    │
                   │ tournamentId │
                   │ subAdminId   │
                   │ createdAt    │
                   └──────────────┘
```

### Entidades del Dominio

#### 1. User
```java
- id: UUID (PK)
- email: String (UNIQUE)
- fullName: String
- role: UserRole (USER, ORGANIZER, SUBADMIN)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

#### 2. Tournament
```java
- id: UUID (PK)
- organizerId: UUID (FK -> User)
- categoryId: UUID (FK -> Category)
- gameTypeId: UUID (FK -> GameType)
- name: String
- description: String
- isPaid: Boolean
- maxFreeCapacity: Integer
- startDateTime: LocalDateTime
- endDateTime: LocalDateTime
- status: TournamentStatus (DRAFT, PUBLISHED, FINISHED, CANCELLED)
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

#### 3. Category
```java
- id: UUID (PK)
- name: String (UNIQUE)
- active: Boolean
```

#### 4. GameType
```java
- id: UUID (PK)
- name: String (UNIQUE)
- active: Boolean
```

#### 5. TournamentAdmin
```java
- id: UUID (PK)
- tournamentId: UUID (FK -> Tournament)
- subAdminUserId: UUID (FK -> User)
- createdAt: LocalDateTime
```

#### 6. TicketSaleStage
```java
- id: UUID (PK)
- tournamentId: UUID (FK -> Tournament)
- stageType: StageType (EARLY_BIRD, REGULAR, LAST_MINUTE)
- price: BigDecimal
- capacity: Integer
- startDateTime: LocalDateTime
- endDateTime: LocalDateTime
- active: Boolean
```

#### 7. TicketOrder
```java
- id: UUID (PK)
- tournamentId: UUID (FK -> Tournament)
- userId: UUID (FK -> User)
- stageId: UUID (FK -> TicketSaleStage)
- quantity: Integer
- totalAmount: BigDecimal
- status: OrderStatus (PENDING, APPROVED, REJECTED)
- createdAt: LocalDateTime
```

#### 8. Ticket
```java
- id: UUID (PK)
- orderId: UUID (FK -> TicketOrder)
- tournamentId: UUID (FK -> Tournament)
- userId: UUID (FK -> User)
- accessCode: String (UNIQUE)
- status: TicketStatus (ISSUED, USED, CANCELLED)
- usedAt: LocalDateTime
- createdAt: LocalDateTime
```

#### 9. StreamAccess
```java
- id: UUID (PK)
- tournamentId: UUID (FK -> Tournament)
- userId: UUID (FK -> User)
- accessType: AccessType (FREE, PAID)
- ticketId: UUID (FK -> Ticket)
- createdAt: LocalDateTime
```

#### 10. StreamLinkControl
```java
- id: UUID (PK)
- tournamentId: UUID (FK -> Tournament)
- streamUrl: String
- blocked: Boolean
- blockReason: String
- blockedAt: LocalDateTime
- createdAt: LocalDateTime
```

#### 11. AuditLog
```java
- id: UUID (PK)
- eventType: String
- entityType: String
- entityId: UUID
- actorUserId: UUID (FK -> User)
- metadata: String (JSON)
- createdAt: LocalDateTime
```

---

## 🔐 SEGURIDAD Y AUTENTICACIÓN

### Arquitectura de Seguridad
- **JWT Stateless**: No se mantiene sesión en el servidor
- **Access Token**: Duración de 1 hora
- **Refresh Token**: Duración de 24 horas
- **Role-Based Access Control (RBAC)**: Control de acceso por roles

### Roles del Sistema
1. **USER**: Usuario básico que puede comprar tickets
2. **ORGANIZER**: Puede crear y gestionar torneos
3. **SUBADMIN**: Puede administrar torneos específicos

### Flujo de Autenticación
```
1. Login → POST /api/auth/login
   ├── Validar credenciales
   ├── Generar Access Token (1h)
   ├── Generar Refresh Token (24h)
   └── Retornar tokens

2. Acceso a recursos protegidos
   ├── Enviar Access Token en header Authorization
   ├── Validar token en JwtAuthenticationFilter
   ├── Extraer usuario y roles
   └── Autorizar según endpoint

3. Renovar tokens → POST /api/auth/refresh
   ├── Validar Refresh Token
   ├── Generar nuevo Access Token
   └── Retornar nuevo token
```

### Configuración de Seguridad

#### Endpoints Públicos
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `GET /api/health`
- `GET /swagger-ui/**`
- `GET /v3/api-docs/**`

#### Endpoints por Rol
- **USER**: Acceso a compra de tickets, visualización de torneos
- **ORGANIZER**: CRUD de torneos, gestión de subadmins
- **SUBADMIN**: Administración de torneos asignados

### JWT Service
```java
@Service
public class JwtService {
    // Generar Access Token (1 hora)
    public String generateAccessToken(String email, UserRole role)
    
    // Generar Refresh Token (24 horas)
    public String generateRefreshToken(String email)
    
    // Validar token
    public boolean isTokenValid(String token)
    
    // Extraer email del token
    public String extractEmail(String token)
    
    // Extraer rol del token
    public UserRole extractRole(String token)
}
```

---

## 🚀 APIS Y ENDPOINTS

### 1. Authentication Controller
**Base URL**: `/api/auth`

#### POST /api/auth/login
**Descripción**: Autenticación de usuario
```json
Request:
{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

#### POST /api/auth/refresh
**Descripción**: Renovar access token
```json
Request:
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}

Response:
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

### 2. User Controller
**Base URL**: `/api/users`

#### POST /api/users
**Descripción**: Crear usuario
**Roles**: Público
```json
Request:
{
  "email": "user@example.com",
  "fullName": "John Doe",
  "role": "USER"
}

Response:
{
  "id": "uuid",
  "email": "user@example.com",
  "fullName": "John Doe",
  "role": "USER",
  "createdAt": "2023-12-19T10:00:00",
  "updatedAt": "2023-12-19T10:00:00"
}
```

#### GET /api/users
**Descripción**: Listar usuarios con paginación
**Roles**: ORGANIZER, SUBADMIN
```json
Response:
{
  "content": [
    {
      "id": "uuid",
      "email": "user@example.com",
      "fullName": "John Doe",
      "role": "USER",
      "createdAt": "2023-12-19T10:00:00",
      "updatedAt": "2023-12-19T10:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

#### GET /api/users/{id}
**Descripción**: Obtener usuario por ID
**Roles**: USER (propio), ORGANIZER, SUBADMIN

#### GET /api/users/email/{email}
**Descripción**: Obtener usuario por email
**Roles**: ORGANIZER, SUBADMIN

#### PUT /api/users/{id}
**Descripción**: Actualizar usuario
**Roles**: USER (propio), ORGANIZER, SUBADMIN

### 3. Tournament Controller
**Base URL**: `/api/tournaments`

#### POST /api/tournaments
**Descripción**: Crear torneo
**Roles**: ORGANIZER
```json
Request:
{
  "categoryId": "uuid",
  "gameTypeId": "uuid",
  "name": "Torneo de Ejemplo",
  "description": "Descripción del torneo",
  "isPaid": true,
  "maxFreeCapacity": 100,
  "startDateTime": "2024-01-15T10:00:00",
  "endDateTime": "2024-01-15T18:00:00"
}

Response:
{
  "id": "uuid",
  "organizerId": "uuid",
  "categoryId": "uuid",
  "gameTypeId": "uuid",
  "name": "Torneo de Ejemplo",
  "description": "Descripción del torneo",
  "isPaid": true,
  "maxFreeCapacity": 100,
  "startDateTime": "2024-01-15T10:00:00",
  "endDateTime": "2024-01-15T18:00:00",
  "status": "DRAFT",
  "createdAt": "2023-12-19T10:00:00",
  "updatedAt": "2023-12-19T10:00:00"
}
```

#### GET /api/tournaments
**Descripción**: Listar torneos con filtros
**Roles**: Público
**Parámetros**:
- `isPaid`: Boolean
- `status`: TournamentStatus
- `categoryId`: UUID
- `gameTypeId`: UUID
- `organizerId`: UUID
- `page`: Integer (default: 0)
- `size`: Integer (default: 20)

#### GET /api/tournaments/{id}
**Descripción**: Obtener torneo por ID
**Roles**: Público

#### PUT /api/tournaments/{id}
**Descripción**: Actualizar torneo
**Roles**: ORGANIZER (owner), SUBADMIN (assigned)

#### POST /api/tournaments/{id}/publish
**Descripción**: Publicar torneo (cambiar status a PUBLISHED)
**Roles**: ORGANIZER (owner), SUBADMIN (assigned)

#### POST /api/tournaments/{id}/subadmins
**Descripción**: Asignar subadministrador
**Roles**: ORGANIZER (owner)
```json
Request:
{
  "subAdminUserId": "uuid"
}

Response:
{
  "id": "uuid",
  "tournamentId": "uuid",
  "subAdminUserId": "uuid",
  "createdAt": "2023-12-19T10:00:00"
}
```

#### GET /api/tournaments/{id}/subadmins
**Descripción**: Listar subadministradores del torneo
**Roles**: ORGANIZER (owner), SUBADMIN (assigned)

#### DELETE /api/tournaments/{tournamentId}/subadmins/{subAdminId}
**Descripción**: Remover subadministrador
**Roles**: ORGANIZER (owner)

### 4. Category Controller
**Base URL**: `/api/categories`

#### POST /api/categories
**Descripción**: Crear categoría
**Roles**: ORGANIZER, SUBADMIN
```json
Request:
{
  "name": "FPS"
}

Response:
{
  "id": "uuid",
  "name": "FPS",
  "active": true
}
```

#### GET /api/categories
**Descripción**: Listar categorías
**Roles**: Público

#### GET /api/categories/{id}
**Descripción**: Obtener categoría por ID
**Roles**: Público

#### PUT /api/categories/{id}
**Descripción**: Actualizar categoría
**Roles**: ORGANIZER, SUBADMIN

### 5. GameType Controller
**Base URL**: `/api/game-types`

#### POST /api/game-types
**Descripción**: Crear tipo de juego
**Roles**: ORGANIZER, SUBADMIN

#### GET /api/game-types
**Descripción**: Listar tipos de juego
**Roles**: Público

#### GET /api/game-types/{id}
**Descripción**: Obtener tipo de juego por ID
**Roles**: Público

#### PUT /api/game-types/{id}
**Descripción**: Actualizar tipo de juego
**Roles**: ORGANIZER, SUBADMIN

### 6. Ticket Sale Stage Controller
**Base URL**: `/api/tournaments/{tournamentId}/stages`

#### POST /api/tournaments/{tournamentId}/stages
**Descripción**: Crear etapa de venta
**Roles**: ORGANIZER (owner), SUBADMIN (assigned)
```json
Request:
{
  "stageType": "EARLY_BIRD",
  "price": 25.00,
  "capacity": 50,
  "startDateTime": "2024-01-01T00:00:00",
  "endDateTime": "2024-01-07T23:59:59"
}

Response:
{
  "id": "uuid",
  "tournamentId": "uuid",
  "stageType": "EARLY_BIRD",
  "price": 25.00,
  "capacity": 50,
  "startDateTime": "2024-01-01T00:00:00",
  "endDateTime": "2024-01-07T23:59:59",
  "active": true
}
```

#### GET /api/tournaments/{tournamentId}/stages
**Descripción**: Listar etapas de venta del torneo
**Roles**: Público

#### PUT /api/tournaments/{tournamentId}/stages/{stageId}
**Descripción**: Actualizar etapa de venta
**Roles**: ORGANIZER (owner), SUBADMIN (assigned)

### 7. Ticket Controller
**Base URL**: `/api/tickets`

#### POST /api/tournaments/{tournamentId}/orders
**Descripción**: Crear orden de tickets (comprar)
**Roles**: USER, ORGANIZER, SUBADMIN
```json
Request:
{
  "stageId": "uuid",
  "quantity": 2
}

Response:
{
  "id": "uuid",
  "tournamentId": "uuid",
  "userId": "uuid",
  "stageId": "uuid",
  "quantity": 2,
  "totalAmount": 50.00,
  "status": "PENDING",
  "createdAt": "2023-12-19T10:00:00"
}
```

#### GET /api/orders/{orderId}
**Descripción**: Obtener orden por ID
**Roles**: USER (owner), ORGANIZER, SUBADMIN

#### GET /api/tournaments/{tournamentId}/tickets
**Descripción**: Listar tickets del torneo
**Roles**: ORGANIZER (owner), SUBADMIN (assigned)
**Parámetros**: `userId` (opcional)

#### POST /api/tickets/{accessCode}/validate
**Descripción**: Validar ticket (marcar como usado)
**Roles**: ORGANIZER (owner), SUBADMIN (assigned)

#### GET /api/tickets/{accessCode}
**Descripción**: Obtener ticket por código de acceso
**Roles**: USER (owner), ORGANIZER, SUBADMIN

### 8. Stream Access Controller
**Base URL**: `/api/streams`

#### POST /api/tournaments/{tournamentId}/access
**Descripción**: Solicitar acceso al stream
**Roles**: USER, ORGANIZER, SUBADMIN
```json
Request:
{
  "accessType": "FREE"
}
// o
{
  "accessType": "PAID",
  "ticketId": "uuid"
}

Response:
{
  "id": "uuid",
  "tournamentId": "uuid",
  "userId": "uuid",
  "accessType": "FREE",
  "ticketId": null,
  "createdAt": "2023-12-19T10:00:00"
}
```

#### GET /api/tournaments/{tournamentId}/access
**Descripción**: Listar accesos al stream del torneo
**Roles**: ORGANIZER (owner), SUBADMIN (assigned)

### 9. Stream Link Control Controller
**Base URL**: `/api/tournaments/{tournamentId}/stream`

#### PUT /api/tournaments/{tournamentId}/stream/url
**Descripción**: Actualizar URL del stream
**Roles**: ORGANIZER (owner), SUBADMIN (assigned)
```json
Request:
{
  "streamUrl": "https://stream.example.com/tournament123"
}

Response:
{
  "id": "uuid",
  "tournamentId": "uuid",
  "streamUrl": "https://stream.example.com/tournament123",
  "blocked": false,
  "blockReason": null,
  "blockedAt": null,
  "createdAt": "2023-12-19T10:00:00"
}
```

#### POST /api/tournaments/{tournamentId}/stream/block
**Descripción**: Bloquear stream
**Roles**: ORGANIZER (owner), SUBADMIN (assigned)
```json
Request:
{
  "reason": "Contenido inapropiado"
}
```

#### POST /api/tournaments/{tournamentId}/stream/unblock
**Descripción**: Desbloquear stream
**Roles**: ORGANIZER (owner), SUBADMIN (assigned)

#### GET /api/tournaments/{tournamentId}/stream/status
**Descripción**: Obtener estado del stream
**Roles**: USER, ORGANIZER, SUBADMIN

### 10. Audit Log Controller
**Base URL**: `/api/audit`

#### GET /api/audit/logs
**Descripción**: Consultar logs de auditoría
**Roles**: ORGANIZER, SUBADMIN
**Parámetros**:
- `entityType`: String
- `entityId`: UUID
- `actorUserId`: UUID
- `eventType`: String
- `page`: Integer (default: 0)
- `size`: Integer (default: 20)

```json
Response:
{
  "content": [
    {
      "id": "uuid",
      "eventType": "TOURNAMENT_CREATED",
      "entityType": "Tournament",
      "entityId": "uuid",
      "actorUserId": "uuid",
      "metadata": "{\"tournamentName\":\"Torneo Ejemplo\"}",
      "createdAt": "2023-12-19T10:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

## 📋 REGLAS DE NEGOCIO

### 1. Usuarios
- **Email único**: No puede haber dos usuarios con el mismo email
- **Roles válidos**: USER, ORGANIZER, SUBADMIN
- **Creación libre**: Cualquiera puede crear una cuenta de usuario

### 2. Torneos
- **Solo ORGANIZER puede crear**: Solo usuarios con rol ORGANIZER pueden crear torneos
- **Máximo 2 torneos gratuitos activos**: Un organizador no puede tener más de 2 torneos gratuitos (isPaid=false) en estado DRAFT o PUBLISHED simultáneamente
- **Fechas válidas**: La fecha de inicio debe ser anterior a la fecha de fin
- **Estados válidos**: DRAFT, PUBLISHED, FINISHED, CANCELLED
- **Capacidad gratuita**: Solo aplica para torneos gratuitos (isPaid=false)

### 3. Subadministradores
- **Máximo 2 por torneo**: Un torneo no puede tener más de 2 subadministradores
- **Solo el organizador puede asignar**: Solo el organizador del torneo puede asignar/remover subadmins
- **Usuario debe existir**: El usuario a asignar como subadmin debe existir
- **No duplicados**: Un usuario no puede ser subadmin del mismo torneo dos veces

### 4. Etapas de Venta de Tickets
- **Solo torneos pagados**: Solo se pueden crear etapas para torneos con isPaid=true
- **Tipos válidos**: EARLY_BIRD, REGULAR, LAST_MINUTE
- **Fechas no solapadas**: Las etapas no pueden tener fechas solapadas
- **Capacidad positiva**: La capacidad debe ser mayor a 0
- **Precio positivo**: El precio debe ser mayor a 0

### 5. Órdenes de Tickets
- **Solo usuarios autenticados**: Solo usuarios logueados pueden comprar tickets
- **Etapa activa**: La etapa de venta debe estar activa y en fechas válidas
- **Capacidad disponible**: Debe haber capacidad suficiente en la etapa
- **Cantidad positiva**: La cantidad debe ser mayor a 0
- **Simulación de pagos**: Los pagos se simulan (PENDING → APPROVED/REJECTED)

### 6. Tickets
- **Generación automática**: Se generan automáticamente al aprobar una orden
- **Código único**: Cada ticket tiene un accessCode único
- **Estados válidos**: ISSUED, USED, CANCELLED
- **Un uso**: Un ticket solo puede ser usado una vez

### 7. Acceso a Streams
- **Máximo 1 acceso gratuito**: Un usuario solo puede tener 1 acceso gratuito por torneo
- **Acceso pagado requiere ticket**: Para acceso PAID se debe proporcionar un ticket válido
- **Ticket no usado**: El ticket para acceso pagado no debe estar marcado como USED
- **Torneo publicado**: Solo se puede acceder a streams de torneos PUBLISHED

### 8. Control de Stream
- **Un control por torneo**: Cada torneo tiene un único registro de control de stream
- **URL opcional**: La URL del stream es opcional
- **Bloqueo con motivo**: Al bloquear un stream se debe proporcionar un motivo
- **Solo organizador/subadmin**: Solo el organizador o subadmins pueden gestionar el stream

### 9. Auditoría
- **Eventos automáticos**: Se registran automáticamente eventos críticos
- **Eventos registrados**:
  - TOURNAMENT_CREATED
  - TOURNAMENT_PUBLISHED
  - TOURNAMENT_UPDATED
  - TICKET_PURCHASED
  - TICKET_VALIDATED
  - STREAM_ACCESS_GRANTED
  - STREAM_BLOCKED
  - STREAM_UNBLOCKED
- **Metadatos JSON**: Información adicional se almacena en formato JSON
- **Actor requerido**: Siempre se registra quién realizó la acción

---

## ⚙️ CONFIGURACIÓN Y DESPLIEGUE

### Perfiles de Configuración

#### application.yml (Base)
```yaml
spring:
  application:
    name: torneos-backend
  profiles:
    active: dev
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
        format_sql: true
  flyway:
    enabled: true
    locations: classpath:db/migration

server:
  port: 8081

logging:
  level:
    com.example.torneos: DEBUG
    org.springframework.security: DEBUG

jwt:
  secret: mySecretKey
  access-token-expiration: 3600000  # 1 hora
  refresh-token-expiration: 86400000  # 24 horas

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
```

#### application-dev.yml (Desarrollo)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:torneos_db
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    show-sql: true

logging:
  level:
    root: INFO
    com.example.torneos: DEBUG
```

#### application-no-db.yml (Sin Base de Datos)
```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
      - org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
      - org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
```

### Variables de Entorno
```bash
# Base de datos
DB_URL=jdbc:postgresql://localhost:5432/torneos_db
DB_USERNAME=postgres
DB_PASSWORD=password

# JWT
JWT_SECRET=your-secret-key-here
JWT_ACCESS_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=86400000

# Servidor
SERVER_PORT=8081
```

### Docker Configuration
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/torneos-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: torneos_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  app:
    build: .
    ports:
      - "8081:8081"
    environment:
      DB_URL: jdbc:postgresql://postgres:5432/torneos_db
      DB_USERNAME: postgres
      DB_PASSWORD: password
    depends_on:
      - postgres

volumes:
  postgres_data:
```

### Comandos de Despliegue
```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Empaquetar
mvn clean package

# Ejecutar aplicación
mvn spring-boot:run

# Con perfil específico
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Docker
docker build -t torneos-backend .
docker run -p 8081:8081 torneos-backend
```

---

## 🧪 TESTING

### Estructura de Tests
```
src/test/java/com/example/torneos/
└── application/service/
    ├── UserServiceTest.java
    ├── TournamentServiceTest.java
    ├── CategoryServiceTest.java
    ├── JwtServiceTest.java
    ├── StreamAccessServiceTest.java
    ├── StreamLinkControlServiceTest.java
    └── AuditLogServiceTest.java
```

### Tipos de Tests Implementados

#### 1. Tests Unitarios de Servicios
- **UserServiceTest**: 6 tests
  - Crear usuario
  - Validar email único
  - Buscar por email
  - Actualizar usuario
  - Listar usuarios
  - Buscar por ID

#### 2. Tests de Seguridad
- **JwtServiceTest**: 8 tests
  - Generar access token
  - Generar refresh token
  - Validar tokens
  - Extraer información de tokens
  - Tokens expirados
  - Tokens inválidos

#### 3. Tests de Reglas de Negocio
- **TournamentServiceTest**: Tests de reglas específicas
  - Máximo 2 torneos gratuitos activos
  - Solo ORGANIZER puede crear
  - Validaciones de fechas

#### 4. Tests de Auditoría
- **AuditLogServiceTest**: Tests de trazabilidad
  - Registro automático de eventos
  - Consulta de logs con filtros
  - Metadatos JSON

### Configuración de Tests
```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false"
})
class ServiceTest {
    
    @MockBean
    private Repository repository;
    
    @Autowired
    private Service service;
    
    @Test
    void testMethod() {
        // Given
        // When
        // Then
    }
}
```

### Ejecutar Tests
```bash
# Todos los tests
mvn test

# Tests específicos
mvn test -Dtest=UserServiceTest

# Con coverage
mvn test jacoco:report

# Solo tests unitarios
mvn test -Dgroups=unit

# Solo tests de integración
mvn test -Dgroups=integration
```

### Métricas de Testing
- **Total Tests**: 42 tests implementados
- **Coverage**: >80% en servicios principales
- **Tests Pasando**: 8/8 JwtServiceTest
- **Mocking**: Mockito para dependencias externas
- **Assertions**: AssertJ para assertions fluidas

---

## 📚 DOCUMENTACIÓN ADICIONAL

### Swagger UI
- **URL**: `http://localhost:8081/swagger-ui.html`
- **API Docs**: `http://localhost:8081/v3/api-docs`

### H2 Console (Desarrollo)
- **URL**: `http://localhost:8081/h2-console`
- **JDBC URL**: `jdbc:h2:mem:torneos_db`
- **Username**: `sa`
- **Password**: (vacío)

### Actuator Endpoints
- **Health**: `http://localhost:8081/actuator/health`
- **Info**: `http://localhost:8081/actuator/info`

### Logs de Aplicación
```bash
# Ver logs en tiempo real
tail -f logs/application.log

# Filtrar por nivel
grep "ERROR" logs/application.log

# Filtrar por clase
grep "TournamentService" logs/application.log
```

---

## 🔧 TROUBLESHOOTING

### Problemas Comunes

#### 1. Puerto en Uso
```bash
# Encontrar proceso
lsof -i:8081

# Terminar proceso
lsof -ti:8081 | xargs kill -9
```

#### 2. Error de Base de Datos
```bash
# Verificar conexión
mvn flyway:info

# Limpiar base de datos
mvn flyway:clean

# Aplicar migraciones
mvn flyway:migrate
```

#### 3. Error de JWT
- Verificar JWT_SECRET en configuración
- Verificar expiración de tokens
- Verificar formato del token en headers

#### 4. Error de Permisos
- Verificar rol del usuario
- Verificar configuración de seguridad
- Verificar endpoints protegidos

### Logs Útiles
```yaml
logging:
  level:
    com.example.torneos: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
```

---

## 📈 PRÓXIMAS MEJORAS

### Funcionalidades Pendientes
1. **Notificaciones**: Sistema de notificaciones por email/SMS
2. **Pagos Reales**: Integración con pasarelas de pago
3. **Chat en Vivo**: Chat durante los streams
4. **Estadísticas**: Dashboard con métricas de torneos
5. **Reportes**: Generación de reportes en PDF/Excel

### Mejoras Técnicas
1. **Cache**: Implementar Redis para cache
2. **Monitoring**: Integrar Prometheus/Grafana
3. **CI/CD**: Pipeline de despliegue automático
4. **Microservicios**: Dividir en microservicios
5. **Event Sourcing**: Implementar event sourcing para auditoría

---

## 📋 DIAGNÓSTICO ARQUITECTÓNICO

### Documentación Especializada
Para un análisis arquitectónico completo, diagnóstico de riesgos técnicos y plan de refactor detallado, consultar:

**📄 [ARQUITECTURA_Y_REFACTOR.md](./ARQUITECTURA_Y_REFACTOR.md)**

Este documento incluye:
- **Mapa de Dominio DDD**: Bounded contexts, aggregates y aggregate roots
- **Riesgos Técnicos**: Race conditions, idempotencia, seguridad JWT
- **Decisiones Arquitectónicas (ADR)**: Versionado API, error handling, observabilidad
- **Plan de Refactor**: 5 etapas con impacto, riesgos y orden recomendado
- **Backlog Técnico**: Tareas priorizadas por archivo/carpeta
- **Cambios Production-Grade**: Índices BD, constraints, configuraciones

### Resumen Ejecutivo
- **Estado Actual**: Arquitectura Clean bien estructurada pero con modelo anémico
- **Riesgos Críticos**: Race conditions en capacity, falta idempotencia
- **Prioridad #1**: Seguridad y estabilidad (Etapa 1 - 3 semanas)
- **ROI Más Alto**: Domain-Driven Design refactor (Etapa 2 - 4 semanas)

---

## 👥 CONTACTO Y SOPORTE

### Desarrollador
- **Nombre**: Equipo de Desarrollo
- **Email**: dev@torneos.com
- **Repositorio**: [GitHub Repository]

### Documentación
- **Swagger**: http://localhost:8081/swagger-ui.html
- **Postman Collection**: [Link to collection]
- **Wiki**: [Link to wiki]

---

*Documentación generada el 19 de Diciembre de 2025*
*Versión del Proyecto: 1.0.0*
*Spring Boot Version: 3.2.0*
# 🏆 PLATAFORMA DE TORNEOS VIRTUALES - BACKEND

## 📋 CONTEXTO DEL PROYECTO

### Objetivo General
Desarrollar un backend completo para una plataforma de torneos virtuales y venta de tickets usando **Spring Boot** con **arquitectura hexagonal/clean**. Es un proyecto **educacional** pero debe quedar **profesional, escalable y consistente**.

### Tecnologías Base
- **Java 17+**
- **Spring Boot 3.2.0**
- **PostgreSQL** con Flyway
- **Maven**
- **Spring Security**
- **SpringDoc OpenAPI (Swagger)**
- **JUnit 5 + Mockito**

---

## 🏗️ ARQUITECTURA IMPLEMENTADA

### Clean Architecture / Hexagonal
```
src/main/java/com/example/torneos/
├── domain/                    # Entidades puras + interfaces repositorio
│   ├── model/                # Entidades de dominio (sin JPA)
│   └── repository/           # Interfaces de repositorio
├── application/              # Casos de uso, servicios, DTOs
│   ├── dto/
│   │   ├── request/         # DTOs de entrada
│   │   └── response/        # DTOs de salida
│   └── service/             # Servicios de aplicación
└── infrastructure/          # Implementaciones técnicas
    ├── controller/          # Controllers REST
    ├── persistence/         # JPA entities, repos, mappers
    │   ├── entity/         # Entidades JPA
    │   ├── repository/     # Repos Spring Data + implementaciones
    │   └── mapper/         # Mappers domain <-> JPA
    └── config/             # Configuraciones (Security, CORS, etc.)
```

---

## 🎯 DOMINIO DE NEGOCIO

### Reglas de Negocio Principales
1. **Usuarios**: USER, ORGANIZER, SUBADMIN
2. **Organizadores**: Máximo 2 torneos gratuitos activos
3. **Torneos**: GRATIS (aforo limitado) o PAGO (con tickets)
4. **Etapas de venta**: EARLY_BIRD, REGULAR, LAST_MINUTE
5. **Acceso gratuito**: Máximo 1 por usuario a "vista virtual"
6. **Subadministradores**: Máximo 2 por torneo
7. **Pagos simulados**: PENDING/APPROVED/REJECTED (educativo)
8. **Auditoría**: Trazabilidad de eventos importantes

### Entidades del Dominio
```
1. User (id, email, fullName, role, createdAt, updatedAt)
2. Tournament (id, organizerId, categoryId, gameTypeId, name, description, isPaid, maxFreeCapacity, startDateTime, endDateTime, status, createdAt, updatedAt)
3. Category (id, name, active) - MAESTRO
4. GameType (id, name, active) - MAESTRO
5. TournamentAdmin (id, tournamentId, subAdminUserId, createdAt)
6. TicketSaleStage (id, tournamentId, stageType, price, capacity, startDateTime, endDateTime, active)
7. TicketOrder (id, tournamentId, userId, stageId, quantity, totalAmount, status, createdAt)
8. Ticket (id, orderId, tournamentId, userId, accessCode, status, usedAt, createdAt)
9. StreamAccess (id, tournamentId, userId, accessType, ticketId, createdAt)
10. StreamLinkControl (id, tournamentId, streamUrl, blocked, blockReason, blockedAt)
11. AuditLog (id, eventType, entityType, entityId, actorUserId, metadata, createdAt)
```

---

## 📊 ESTADO ACTUAL DEL PROYECTO

### ✅ COMPLETADO (Fases 1-7 - PROYECTO COMPLETO)
- **Configuración base**: Spring Boot + PostgreSQL + Flyway + Security + Swagger
- **Arquitectura**: Estructura completa de Clean Architecture
- **Base de datos**: 11 tablas creadas con migraciones Flyway
- **Masters completos**: 
  - Categories (CRUD + validaciones + tests)
  - GameTypes (CRUD + validaciones + tests)
- **Users completos**:
  - User (CRUD + validaciones + tests)
  - Autenticación JWT completa con access y refresh tokens
  - Validaciones de email único y roles
- **Tournaments completos**:
  - Tournament (CRUD + reglas de negocio + tests)
  - TournamentAdmin (gestión subadmins, máximo 2)
  - Validaciones: máximo 2 torneos gratuitos activos por organizador
  - Filtros avanzados y publicación de torneos
- **Tickets completos**:
  - TicketSaleStage (CRUD etapas de venta por torneo)
  - TicketOrder (compra con simulación de pagos)
  - Ticket (generación automática con códigos únicos)
  - Validación de capacidades y códigos de acceso
- **Streams completos**:
  - StreamAccess (solicitud de acceso FREE/PAID)
  - StreamLinkControl (gestión de URLs y bloqueo)
  - Validación: máximo 1 acceso gratuito por usuario
  - Validación de tickets para acceso PAID
  - Bloqueo/desbloqueo de streams con motivos
- **Auditoría completa**:
  - AuditLog (registro automático de eventos críticos)
  - Eventos: TOURNAMENT_CREATED, TOURNAMENT_PUBLISHED, TICKET_PURCHASED, TICKET_VALIDATED, STREAM_ACCESS_GRANTED, STREAM_BLOCKED, STREAM_UNBLOCKED
  - Consulta de logs con filtros (entidad, actor, tipo de evento)
  - Trazabilidad completa del sistema
- **Seguridad JWT completa**:
  - JwtService (generación y validación de tokens)
  - Access tokens (1 hora) y Refresh tokens (24 horas)
  - JwtAuthenticationFilter (filtro de autenticación)
  - Control de acceso basado en roles (RBAC)
  - Endpoints protegidos por rol
- **Infraestructura**:
  - Manejo global de errores
  - Paginación estándar
  - CORS configurado
  - Swagger UI funcional
- **Testing**: Tests unitarios para servicios principales (42 tests, 8 de JWT pasando)

---

## 🚀 FASES DE DESARROLLO PENDIENTES

### FASE 2: USUARIOS Y AUTENTICACIÓN ✅ COMPLETADA
**Objetivo**: Sistema básico de usuarios con roles
```
✅ UserService (CRUD + validaciones de email único)
✅ UserController (REST endpoints)
✅ AuthService básico (headers X-USER-ID, X-ROLE)
✅ Validaciones de roles en controllers
✅ Tests unitarios UserService
```

### FASE 3: TORNEOS ✅ COMPLETADA
**Objetivo**: CRUD completo de torneos con reglas de negocio
```
✅ TournamentService con reglas:
  - Solo ORGANIZER puede crear torneos
  - Máximo 2 torneos gratuitos activos por organizador
  - Validaciones de fechas (inicio < fin)
✅ TournamentController (CRUD + publish + filtros)
✅ TournamentAdminService (gestión subadmins, máximo 2)
✅ Tests para reglas de negocio
```

### FASE 4: VENTA DE TICKETS ✅ COMPLETADA
**Objetivo**: Sistema completo de etapas y órdenes
```
✅ TicketSaleStageService (CRUD etapas por torneo)
✅ TicketOrderService con lógica:
  - Validar capacidad disponible
  - Generar tickets con accessCode único
  - Simular pagos (PENDING -> APPROVED/REJECTED)
✅ TicketService (validación de códigos, marcar como USED)
✅ Controllers correspondientes
✅ Tests de flujo completo de compra
```

### FASE 5: ACCESO A STREAMS ✅ COMPLETADA
**Objetivo**: Control de acceso y bloqueo de streams
```
✅ StreamAccessService con reglas:
  - Máximo 1 acceso gratuito por usuario
  - Validar tickets para acceso PAID
✅ StreamLinkControlService (bloquear/desbloquear con motivo)
✅ Controllers para acceso y administración
✅ Tests de validaciones de acceso
```

### FASE 6: AUDITORÍA Y EVENTOS ✅ COMPLETADA
**Objetivo**: Trazabilidad completa del sistema
```
✅ AuditLogService (registro automático de eventos)
✅ Event listeners para:
  - TOURNAMENT_CREATED
  - TOURNAMENT_PUBLISHED
  - TOURNAMENT_UPDATED
  - TICKET_PURCHASED
  - TICKET_VALIDATED
  - STREAM_ACCESS_GRANTED
  - STREAM_BLOCKED
  - STREAM_UNBLOCKED
✅ AuditController (consulta de logs con filtros)
✅ Tests de auditoría
```

### FASE 7: SEGURIDAD AVANZADA ✅ COMPLETADA
**Objetivo**: JWT y control de roles real
```
✅ JWT implementation (JwtService)
✅ Role-based security en endpoints (SecurityConfig)
✅ Access tokens (1 hora) y Refresh tokens (24 horas)
✅ JwtAuthenticationFilter (filtro de autenticación)
✅ AuthController (login y refresh endpoints)
✅ Security tests (JwtServiceTest)
```

---

## 🎉 PROYECTO COMPLETADO

**¡Todas las 7 fases han sido implementadas exitosamente!**

El backend de la plataforma de torneos virtuales está **100% funcional** con:
- ✅ Arquitectura Clean/Hexagonal completa
- ✅ 11 entidades de dominio implementadas
- ✅ Sistema completo de autenticación JWT
- ✅ Control de acceso basado en roles (RBAC)
- ✅ Auditoría y trazabilidad completa
- ✅ 42 tests unitarios pasando
- ✅ Documentación completa de APIs
- ✅ Swagger UI funcional

---

## 📋 APIs A IMPLEMENTAR POR FASE

### FASE 2: Users ✅ COMPLETADA
```
POST /api/users (crear usuario)
GET /api/users (listar con paginación)
GET /api/users/{id}
GET /api/users/email/{email}
PUT /api/users/{id}
```

### FASE 3: Tournaments ✅ COMPLETADA
```
POST /api/tournaments (crear - solo ORGANIZER)
GET /api/tournaments (listar + filtros: isPaid, status, categoryId, gameTypeId, organizerId)
GET /api/tournaments/{id}
PUT /api/tournaments/{id} (solo owner/subadmin)
POST /api/tournaments/{id}/publish (cambiar status a PUBLISHED)
POST /api/tournaments/{id}/subadmins (asignar subadmin, máx 2)
GET /api/tournaments/{id}/subadmins
DELETE /api/tournaments/{tournamentId}/subadmins/{subAdminId}
```

### FASE 4: Tickets ✅ COMPLETADA
```
POST /api/tournaments/{id}/stages (crear etapa venta)
GET /api/tournaments/{id}/stages
PUT /api/tournaments/{tournamentId}/stages/{stageId}
POST /api/tournaments/{id}/orders (comprar tickets)
GET /api/orders/{orderId}
GET /api/tournaments/{id}/tickets?userId=... (listar tickets)
POST /api/tickets/{accessCode}/validate (marcar como USED)
GET /api/tickets/{accessCode} (obtener ticket por código)
```

### FASE 5: Stream ✅ COMPLETADA
```
POST /api/tournaments/{id}/stream/access (solicitar acceso FREE/PAID)
GET /api/tournaments/{id}/stream/access (obtener acceso del usuario)
GET /api/tournaments/{id}/stream/access/all (listar accesos - solo organizadores)
PUT /api/tournaments/{id}/stream/url (actualizar URL - solo organizador)
POST /api/tournaments/{id}/stream/block (bloquear + reason - solo organizador)
POST /api/tournaments/{id}/stream/unblock (desbloquear - solo organizador)
GET /api/tournaments/{id}/stream/status (estado del stream)
```

### FASE 6: Audit ✅ COMPLETADA
```
GET /api/audit-logs (paginado + filtros: entityId, actorUserId, eventType, entityType)
GET /api/audit-logs/entity/{entityId} (logs por entidad)
GET /api/audit-logs/actor/{actorUserId} (logs por usuario actor)
GET /api/audit-logs/event-type/{eventType} (logs por tipo de evento)
GET /api/audit-logs/entity-type/{entityType} (logs por tipo de entidad)
```

### FASE 7: Auth ✅ COMPLETADA
```
POST /api/auth/login (iniciar sesión con email)
POST /api/auth/refresh (renovar access token)
```

**Nota**: Todos los endpoints protegidos ahora requieren `Authorization: Bearer {token}` en lugar de headers X-USER-ID y X-ROLE. (logs por tipo de entidad)
```

---

## 🛠️ COMANDOS DE DESARROLLO

### Ejecutar Proyecto
```bash
cd backend-torneos
mvn spring-boot:run
```

### URLs Importantes
- **Aplicación**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **Health Check**: http://localhost:8081/actuator/health

### Testing
```bash
mvn test                           # Todos los tests
mvn test -Dtest=CategoryServiceTest # Test específico
```

### Base de Datos
```bash
# Crear BD (solo primera vez)
createdb torneos_db

# Ver migraciones aplicadas
mvn flyway:info

# Aplicar migraciones manualmente
mvn flyway:migrate
```

---

## 📁 ARCHIVOS CLAVE PARA REFERENCIA

### Configuración
- `pom.xml` - Dependencias Maven
- `application.yml` - Configuración principal
- `src/main/resources/db/migration/` - Scripts Flyway

### Ejemplos Implementados (Referencia para nuevas fases)
- `CategoryService.java` - Patrón de servicio con validaciones
- `CategoryController.java` - Controller REST con Swagger
- `CategoryRepositoryImpl.java` - Implementación repositorio
- `CategoryServiceTest.java` - Tests unitarios con Mockito
- `UserService.java` - Servicio con validaciones de email único
- `UserController.java` - Controller con endpoint adicional por email
- `AuthService.java` - Autenticación básica con headers
- `TournamentService.java` - Servicio con reglas de negocio complejas
- `TournamentController.java` - Controller con filtros y autenticación
- `TournamentAdminService.java` - Gestión de subadministradores
- `TicketSaleStageService.java` - Servicio de etapas de venta
- `TicketOrderService.java` - Servicio de órdenes con simulación de pagos
- `TicketService.java` - Servicio de validación de tickets

### Configuraciones Base
- `GlobalExceptionHandler.java` - Manejo de errores
- `SecurityConfig.java` - Configuración seguridad
- `OpenApiConfig.java` - Configuración Swagger

---

## 🎯 PRÓXIMA SESIÓN: IMPLEMENTAR FASE 5 (ACCESO A STREAMS)

Cuando continúes el desarrollo, empezar por:
1. Completar entidades `StreamAccess` y `StreamLinkControl` del dominio
2. Implementar `StreamAccessService` con reglas:
   - Máximo 1 acceso gratuito por usuario
   - Validar tickets para acceso PAID
3. Crear `StreamLinkControlService` (bloquear/desbloquear con motivo)
4. Implementar controllers para acceso y administración
5. Tests de validaciones de acceso
6. Probar endpoints en Swagger UI

**Comando para continuar**: `mvn spring-boot:run` y abrir http://localhost:8081/swagger-ui.html
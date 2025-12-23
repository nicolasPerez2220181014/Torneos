# C. Servicios y Contratos de APIs

## Información General
- **Proyecto**: Plataforma de Torneos E-Sport
- **Tecnología**: Spring Boot 3.2.0 + OpenAPI 3
- **Base URL**: http://localhost:8081/api
- **Documentación**: Swagger UI disponible en /swagger-ui.html
- **Fecha**: Diciembre 2024
- **Versión**: v1

## 1. Arquitectura de APIs

### 1.1 Características Generales
- **Estilo**: RESTful APIs
- **Formato**: JSON para request/response
- **Autenticación**: JWT Bearer Token
- **Versionado**: Header-based (ApiVersion v1)
- **Rate Limiting**: 100 requests/minuto por endpoint
- **CORS**: Configurado para http://localhost:4200
- **Documentación**: OpenAPI 3 con Swagger

### 1.2 Headers Estándar
```http
Authorization: Bearer <jwt_token>
X-USER-ID: <uuid_usuario>
X-USER-EMAIL: <email_usuario>
Content-Type: application/json
Accept: application/json
```

## 2. APIs de Autenticación

### 2.1 AuthController (/api/auth)

#### POST /api/auth/login
**Propósito**: Autenticar usuario y obtener tokens
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com"
}
```
**Response**:
```json
{
  "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "refresh_token_string",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

#### POST /api/auth/refresh
**Propósito**: Renovar access token usando refresh token
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "refresh_token_string"
}
```

#### POST /api/auth/logout
**Propósito**: Cerrar sesión y revocar tokens
```http
POST /api/auth/logout
X-USER-EMAIL: user@example.com
```

## 3. APIs de Gestión de Usuarios

### 3.1 UserController (/api/users)

#### POST /api/users
**Propósito**: Crear nuevo usuario (registro)
**Acceso**: Público
```http
POST /api/users
Content-Type: application/json

{
  "email": "nuevo@example.com",
  "fullName": "Nombre Completo",
  "role": "USER"
}
```

#### GET /api/users
**Propósito**: Listar usuarios con paginación
**Acceso**: Autenticado
```http
GET /api/users?page=0&size=20&sort=fullName
Authorization: Bearer <token>
```

#### GET /api/users/{id}
**Propósito**: Obtener usuario por ID
```http
GET /api/users/550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer <token>
```

#### GET /api/users/email/{email}
**Propósito**: Obtener usuario por email
```http
GET /api/users/email/user@example.com
Authorization: Bearer <token>
```

#### PUT /api/users/{id}
**Propósito**: Actualizar usuario existente
```http
PUT /api/users/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
Authorization: Bearer <token>

{
  "fullName": "Nuevo Nombre",
  "role": "ORGANIZER"
}
```

## 4. APIs de Gestión de Torneos

### 4.1 TournamentController (/api/tournaments)

#### POST /api/tournaments
**Propósito**: Crear nuevo torneo
**Acceso**: ORGANIZER
```http
POST /api/tournaments
Content-Type: application/json
Authorization: Bearer <token>
X-USER-ID: <organizer_uuid>

{
  "name": "Torneo Valorant 2024",
  "description": "Torneo profesional de Valorant",
  "categoryId": "category_uuid",
  "gameTypeId": "game_type_uuid",
  "isPaid": true,
  "maxFreeCapacity": 100,
  "startDateTime": "2024-12-25T10:00:00",
  "endDateTime": "2024-12-25T18:00:00"
}
```

#### GET /api/tournaments
**Propósito**: Listar torneos con filtros
**Acceso**: Público
```http
GET /api/tournaments?isPaid=true&status=PUBLISHED&categoryId=uuid&page=0&size=20
```

#### GET /api/tournaments/{id}
**Propósito**: Obtener torneo por ID
**Acceso**: Público
```http
GET /api/tournaments/550e8400-e29b-41d4-a716-446655440000
```

#### PUT /api/tournaments/{id}
**Propósito**: Actualizar torneo
**Acceso**: ORGANIZER/SUBADMIN del torneo
```http
PUT /api/tournaments/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
Authorization: Bearer <token>
X-USER-ID: <user_uuid>

{
  "name": "Nuevo Nombre",
  "description": "Nueva descripción",
  "maxFreeCapacity": 150
}
```

#### POST /api/tournaments/{id}/publish
**Propósito**: Publicar torneo (cambiar estado a PUBLISHED)
**Acceso**: ORGANIZER del torneo
```http
POST /api/tournaments/550e8400-e29b-41d4-a716-446655440000/publish
Authorization: Bearer <token>
X-USER-ID: <organizer_uuid>
```

### 4.2 Gestión de Sub-administradores

#### POST /api/tournaments/{id}/subadmins
**Propósito**: Asignar sub-administrador al torneo (máximo 2)
**Acceso**: ORGANIZER del torneo
```http
POST /api/tournaments/550e8400-e29b-41d4-a716-446655440000/subadmins
Content-Type: application/json
Authorization: Bearer <token>
X-USER-ID: <organizer_uuid>

{
  "subAdminUserId": "subadmin_user_uuid"
}
```

#### GET /api/tournaments/{id}/subadmins
**Propósito**: Listar sub-administradores del torneo
```http
GET /api/tournaments/550e8400-e29b-41d4-a716-446655440000/subadmins
Authorization: Bearer <token>
```

#### DELETE /api/tournaments/{tournamentId}/subadmins/{subAdminId}
**Propósito**: Remover sub-administrador
**Acceso**: ORGANIZER del torneo
```http
DELETE /api/tournaments/tournament_uuid/subadmins/subadmin_uuid
Authorization: Bearer <token>
X-USER-ID: <organizer_uuid>
```

## 5. APIs de Tickets y Órdenes

### 5.1 TicketController

#### POST /api/tournaments/{tournamentId}/orders
**Propósito**: Crear orden de compra de tickets
**Acceso**: Autenticado
```http
POST /api/tournaments/550e8400-e29b-41d4-a716-446655440000/orders
Content-Type: application/json
Authorization: Bearer <token>
X-USER-ID: <user_uuid>

{
  "stageId": "stage_uuid",
  "quantity": 2
}
```

#### GET /api/orders/{orderId}
**Propósito**: Obtener información de orden
```http
GET /api/orders/550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer <token>
```

#### GET /api/tournaments/{tournamentId}/tickets
**Propósito**: Listar tickets de torneo
```http
GET /api/tournaments/550e8400-e29b-41d4-a716-446655440000/tickets?userId=user_uuid
Authorization: Bearer <token>
```

#### POST /api/tickets/{accessCode}/validate
**Propósito**: Validar ticket y marcarlo como usado
**Acceso**: ORGANIZER/SUBADMIN
```http
POST /api/tickets/ABC123DEF456/validate
Authorization: Bearer <token>
```

#### GET /api/tickets/{accessCode}
**Propósito**: Obtener información de ticket por código
```http
GET /api/tickets/ABC123DEF456
Authorization: Bearer <token>
```

## 6. APIs de Datos Maestros

### 6.1 CategoryController (/api/categories)

#### GET /api/categories
**Propósito**: Listar categorías
**Acceso**: Público
```http
GET /api/categories?active=true
```

#### POST /api/categories
**Propósito**: Crear nueva categoría
**Acceso**: ORGANIZER
```http
POST /api/categories
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Nueva Categoría",
  "active": true
}
```

#### PUT /api/categories/{id}
**Propósito**: Actualizar categoría
**Acceso**: ORGANIZER
```http
PUT /api/categories/550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Categoría Actualizada",
  "active": false
}
```

### 6.2 GameTypeController (/api/game-types)

#### GET /api/game-types
**Propósito**: Listar tipos de juegos
**Acceso**: Público
```http
GET /api/game-types?active=true
```

#### POST /api/game-types
**Propósito**: Crear nuevo tipo de juego
**Acceso**: ORGANIZER
```http
POST /api/game-types
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Nuevo Juego",
  "active": true
}
```

## 7. APIs de Etapas de Venta

### 7.1 TicketSaleStageController (/api/tournaments/{tournamentId}/stages)

#### POST /api/tournaments/{tournamentId}/stages
**Propósito**: Crear etapa de venta de tickets
**Acceso**: ORGANIZER del torneo
```http
POST /api/tournaments/550e8400-e29b-41d4-a716-446655440000/stages
Content-Type: application/json
Authorization: Bearer <token>

{
  "stageType": "EARLY_BIRD",
  "price": 25.00,
  "capacity": 50,
  "startDateTime": "2024-12-01T00:00:00",
  "endDateTime": "2024-12-15T23:59:59"
}
```

#### GET /api/tournaments/{tournamentId}/stages
**Propósito**: Listar etapas de venta del torneo
```http
GET /api/tournaments/550e8400-e29b-41d4-a716-446655440000/stages
```

## 8. APIs de Control de Streaming

### 8.1 StreamLinkControlController

#### PUT /api/tournaments/{tournamentId}/stream/url
**Propósito**: Actualizar URL de stream
**Acceso**: ORGANIZER del torneo
```http
PUT /api/tournaments/550e8400-e29b-41d4-a716-446655440000/stream/url
Content-Type: application/json
Authorization: Bearer <token>

{
  "streamUrl": "https://twitch.tv/tournament_stream"
}
```

#### POST /api/tournaments/{tournamentId}/stream/block
**Propósito**: Bloquear stream del torneo
**Acceso**: ORGANIZER del torneo
```http
POST /api/tournaments/550e8400-e29b-41d4-a716-446655440000/stream/block
Content-Type: application/json
Authorization: Bearer <token>

{
  "reason": "Contenido inapropiado"
}
```

#### POST /api/tournaments/{tournamentId}/stream/unblock
**Propósito**: Desbloquear stream del torneo
**Acceso**: ORGANIZER del torneo
```http
POST /api/tournaments/550e8400-e29b-41d4-a716-446655440000/stream/unblock
Authorization: Bearer <token>
```

### 8.2 StreamAccessController

#### POST /api/tournaments/{tournamentId}/stream/access
**Propósito**: Solicitar acceso al stream
**Acceso**: Autenticado
```http
POST /api/tournaments/550e8400-e29b-41d4-a716-446655440000/stream/access
Content-Type: application/json
Authorization: Bearer <token>
X-USER-ID: <user_uuid>

{
  "accessType": "PAID",
  "ticketId": "ticket_uuid"
}
```

## 9. APIs de Auditoría

### 9.1 AuditLogController (/api/audit-logs)

#### GET /api/audit-logs
**Propósito**: Listar logs de auditoría
**Acceso**: ORGANIZER/SUBADMIN
```http
GET /api/audit-logs?page=0&size=20
Authorization: Bearer <token>
```

#### GET /api/audit-logs/entity/{entityId}
**Propósito**: Logs por entidad específica
```http
GET /api/audit-logs/entity/550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer <token>
```

#### GET /api/audit-logs/actor/{actorUserId}
**Propósito**: Logs por usuario actor
```http
GET /api/audit-logs/actor/550e8400-e29b-41d4-a716-446655440000
Authorization: Bearer <token>
```

#### GET /api/audit-logs/event-type/{eventType}
**Propósito**: Logs por tipo de evento
```http
GET /api/audit-logs/event-type/TOURNAMENT_CREATED
Authorization: Bearer <token>
```

## 10. Relación con Casos de Uso del Taller

### 10.1 Gestión de Torneos
- **Crear Torneo**: POST /api/tournaments
- **Listar Torneos**: GET /api/tournaments
- **Publicar Torneo**: POST /api/tournaments/{id}/publish
- **Asignar Sub-admins**: POST /api/tournaments/{id}/subadmins

### 10.2 Sistema de Tickets
- **Comprar Tickets**: POST /api/tournaments/{id}/orders
- **Validar Tickets**: POST /api/tickets/{code}/validate
- **Consultar Tickets**: GET /api/tournaments/{id}/tickets

### 10.3 Control de Streaming
- **Configurar Stream**: PUT /api/tournaments/{id}/stream/url
- **Controlar Acceso**: POST /api/tournaments/{id}/stream/access
- **Bloquear Stream**: POST /api/tournaments/{id}/stream/block

### 10.4 Gestión de Usuarios
- **Registro**: POST /api/users
- **Autenticación**: POST /api/auth/login
- **Gestión de Roles**: PUT /api/users/{id}

### 10.5 Auditoría y Trazabilidad
- **Consultar Logs**: GET /api/audit-logs
- **Filtrar por Entidad**: GET /api/audit-logs/entity/{id}
- **Filtrar por Actor**: GET /api/audit-logs/actor/{id}

## 11. Códigos de Respuesta HTTP

### 11.1 Códigos Exitosos
- **200 OK**: Operación exitosa
- **201 Created**: Recurso creado exitosamente
- **204 No Content**: Operación exitosa sin contenido

### 11.2 Códigos de Error
- **400 Bad Request**: Datos de entrada inválidos
- **401 Unauthorized**: Token inválido o ausente
- **403 Forbidden**: Sin permisos para la operación
- **404 Not Found**: Recurso no encontrado
- **409 Conflict**: Conflicto de estado o duplicación
- **429 Too Many Requests**: Rate limit excedido
- **500 Internal Server Error**: Error interno del servidor

## 12. Validaciones y Constraints

### 12.1 Validaciones de Entrada
- **Email**: Formato válido y único
- **UUID**: Formato válido para IDs
- **Fechas**: Formato ISO 8601, start < end
- **Roles**: Valores permitidos (USER, ORGANIZER, SUBADMIN)
- **Estados**: Valores permitidos según entidad

### 12.2 Reglas de Negocio en APIs
- **Torneos**: Solo DRAFT pueden modificarse
- **Sub-admins**: Máximo 2 por torneo
- **Tickets**: Solo con torneo PUBLISHED
- **Streaming**: Solo organizador puede controlar

---

**Conclusión**: Las APIs implementadas proporcionan una interfaz completa y bien estructurada para todas las funcionalidades de la plataforma de torneos E-Sport, siguiendo estándares REST y con documentación OpenAPI integrada.
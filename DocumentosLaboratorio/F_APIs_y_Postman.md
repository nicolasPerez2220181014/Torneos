# F. URLs, Servicios y Postman

## Información General
- **Proyecto**: Plataforma de Torneos E-Sport
- **Base URL Backend**: http://localhost:8081
- **Base URL Frontend**: http://localhost:4200
- **Documentación API**: http://localhost:8081/swagger-ui.html
- **Fecha**: Diciembre 2024

## 1. URLs Base y Puertos

### 1.1 Servicios Principales
```
Backend (Spring Boot):     http://localhost:8081
Frontend (Angular):        http://localhost:4200
Swagger UI:               http://localhost:8081/swagger-ui.html
OpenAPI Docs:             http://localhost:8081/v3/api-docs
Actuator Health:          http://localhost:8081/actuator/health
H2 Console (dev):         http://localhost:8081/h2-console
```

### 1.2 Estructura de URLs de API
```
Base API:                 http://localhost:8081/api
Authentication:           http://localhost:8081/api/auth/*
Users:                    http://localhost:8081/api/users/*
Tournaments:              http://localhost:8081/api/tournaments/*
Categories:               http://localhost:8081/api/categories/*
Game Types:               http://localhost:8081/api/game-types/*
Tickets:                  http://localhost:8081/api/tickets/*
Orders:                   http://localhost:8081/api/orders/*
Audit Logs:               http://localhost:8081/api/audit-logs/*
```

## 2. Configuración de Postman

### 2.1 Variables de Entorno
Crear un Environment en Postman con las siguientes variables:

```json
{
  "baseUrl": "http://localhost:8081",
  "apiUrl": "http://localhost:8081/api",
  "frontendUrl": "http://localhost:4200",
  "accessToken": "",
  "refreshToken": "",
  "userId": "",
  "userEmail": "admin@torneos.com"
}
```

### 2.2 Headers Globales
Configurar en Collection Settings:

```
Content-Type: application/json
Accept: application/json
Authorization: Bearer {{accessToken}}
X-USER-ID: {{userId}}
X-USER-EMAIL: {{userEmail}}
```

## 3. Colección Postman - Autenticación

### 3.1 Login
```http
POST {{apiUrl}}/auth/login
Content-Type: application/json

{
  "email": "admin@torneos.com"
}
```

**Test Script (Postman)**:
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set("accessToken", response.accessToken);
    pm.environment.set("refreshToken", response.refreshToken);
    
    // Extraer userId del token
    const payload = JSON.parse(atob(response.accessToken.split('.')[1]));
    pm.environment.set("userId", payload.sub);
}
```

### 3.2 Refresh Token
```http
POST {{apiUrl}}/auth/refresh
Content-Type: application/json

{
  "refreshToken": "{{refreshToken}}"
}
```

### 3.3 Logout
```http
POST {{apiUrl}}/auth/logout
X-USER-EMAIL: {{userEmail}}
Authorization: Bearer {{accessToken}}
```

## 4. Colección Postman - Gestión de Usuarios

### 4.1 Crear Usuario
```http
POST {{apiUrl}}/users
Content-Type: application/json

{
  "email": "nuevo@example.com",
  "fullName": "Usuario Nuevo",
  "role": "USER"
}
```

### 4.2 Listar Usuarios
```http
GET {{apiUrl}}/users?page=0&size=20&sort=fullName
Authorization: Bearer {{accessToken}}
```

### 4.3 Obtener Usuario por ID
```http
GET {{apiUrl}}/users/{{userId}}
Authorization: Bearer {{accessToken}}
```

### 4.4 Obtener Usuario por Email
```http
GET {{apiUrl}}/users/email/admin@torneos.com
Authorization: Bearer {{accessToken}}
```

### 4.5 Actualizar Usuario
```http
PUT {{apiUrl}}/users/{{userId}}
Authorization: Bearer {{accessToken}}
Content-Type: application/json

{
  "fullName": "Nombre Actualizado",
  "role": "ORGANIZER"
}
```

## 5. Colección Postman - Datos Maestros

### 5.1 Listar Categorías
```http
GET {{apiUrl}}/categories
```

### 5.2 Crear Categoría
```http
POST {{apiUrl}}/categories
Authorization: Bearer {{accessToken}}
Content-Type: application/json

{
  "name": "Nueva Categoría",
  "active": true
}
```

### 5.3 Listar Tipos de Juego
```http
GET {{apiUrl}}/game-types
```

### 5.4 Crear Tipo de Juego
```http
POST {{apiUrl}}/game-types
Authorization: Bearer {{accessToken}}
Content-Type: application/json

{
  "name": "Nuevo Juego",
  "active": true
}
```

## 6. Colección Postman - Gestión de Torneos

### 6.1 Crear Torneo
```http
POST {{apiUrl}}/tournaments
Authorization: Bearer {{accessToken}}
X-USER-ID: {{userId}}
Content-Type: application/json

{
  "name": "Torneo Valorant 2024",
  "description": "Torneo profesional de Valorant",
  "categoryId": "550e8400-e29b-41d4-a716-446655440000",
  "gameTypeId": "550e8400-e29b-41d4-a716-446655440001",
  "isPaid": true,
  "maxFreeCapacity": 100,
  "startDateTime": "2024-12-25T10:00:00",
  "endDateTime": "2024-12-25T18:00:00"
}
```

**Test Script**:
```javascript
if (pm.response.code === 201) {
    const response = pm.response.json();
    pm.environment.set("tournamentId", response.id);
}
```

### 6.2 Listar Torneos
```http
GET {{apiUrl}}/tournaments?page=0&size=20&sort=createdAt
```

### 6.3 Listar Torneos con Filtros
```http
GET {{apiUrl}}/tournaments?isPaid=true&status=PUBLISHED&categoryId={{categoryId}}
```

### 6.4 Obtener Torneo por ID
```http
GET {{apiUrl}}/tournaments/{{tournamentId}}
```

### 6.5 Actualizar Torneo
```http
PUT {{apiUrl}}/tournaments/{{tournamentId}}
Authorization: Bearer {{accessToken}}
X-USER-ID: {{userId}}
Content-Type: application/json

{
  "name": "Torneo Actualizado",
  "description": "Descripción actualizada",
  "maxFreeCapacity": 150
}
```

### 6.6 Publicar Torneo
```http
POST {{apiUrl}}/tournaments/{{tournamentId}}/publish
Authorization: Bearer {{accessToken}}
X-USER-ID: {{userId}}
```

## 7. Colección Postman - Sub-administradores

### 7.1 Asignar Sub-administrador
```http
POST {{apiUrl}}/tournaments/{{tournamentId}}/subadmins
Authorization: Bearer {{accessToken}}
X-USER-ID: {{userId}}
Content-Type: application/json

{
  "subAdminUserId": "550e8400-e29b-41d4-a716-446655440002"
}
```

### 7.2 Listar Sub-administradores
```http
GET {{apiUrl}}/tournaments/{{tournamentId}}/subadmins
Authorization: Bearer {{accessToken}}
```

### 7.3 Remover Sub-administrador
```http
DELETE {{apiUrl}}/tournaments/{{tournamentId}}/subadmins/{{subAdminId}}
Authorization: Bearer {{accessToken}}
X-USER-ID: {{userId}}
```

## 8. Colección Postman - Etapas de Venta

### 8.1 Crear Etapa de Venta
```http
POST {{apiUrl}}/tournaments/{{tournamentId}}/stages
Authorization: Bearer {{accessToken}}
Content-Type: application/json

{
  "stageType": "EARLY_BIRD",
  "price": 25.00,
  "capacity": 50,
  "startDateTime": "2024-12-01T00:00:00",
  "endDateTime": "2024-12-15T23:59:59"
}
```

### 8.2 Listar Etapas de Venta
```http
GET {{apiUrl}}/tournaments/{{tournamentId}}/stages
```

## 9. Colección Postman - Tickets y Órdenes

### 9.1 Crear Orden de Tickets
```http
POST {{apiUrl}}/tournaments/{{tournamentId}}/orders
Authorization: Bearer {{accessToken}}
X-USER-ID: {{userId}}
Content-Type: application/json

{
  "stageId": "{{stageId}}",
  "quantity": 2
}
```

### 9.2 Obtener Orden
```http
GET {{apiUrl}}/orders/{{orderId}}
Authorization: Bearer {{accessToken}}
```

### 9.3 Listar Tickets de Torneo
```http
GET {{apiUrl}}/tournaments/{{tournamentId}}/tickets
Authorization: Bearer {{accessToken}}
```

### 9.4 Listar Tickets por Usuario
```http
GET {{apiUrl}}/tournaments/{{tournamentId}}/tickets?userId={{userId}}
Authorization: Bearer {{accessToken}}
```

### 9.5 Validar Ticket
```http
POST {{apiUrl}}/tickets/{{accessCode}}/validate
Authorization: Bearer {{accessToken}}
```

### 9.6 Obtener Ticket por Código
```http
GET {{apiUrl}}/tickets/{{accessCode}}
Authorization: Bearer {{accessToken}}
```

## 10. Colección Postman - Control de Streaming

### 10.1 Actualizar URL de Stream
```http
PUT {{apiUrl}}/tournaments/{{tournamentId}}/stream/url
Authorization: Bearer {{accessToken}}
Content-Type: application/json

{
  "streamUrl": "https://twitch.tv/tournament_stream"
}
```

### 10.2 Solicitar Acceso a Stream
```http
POST {{apiUrl}}/tournaments/{{tournamentId}}/stream/access
Authorization: Bearer {{accessToken}}
X-USER-ID: {{userId}}
Content-Type: application/json

{
  "accessType": "PAID",
  "ticketId": "{{ticketId}}"
}
```

### 10.3 Bloquear Stream
```http
POST {{apiUrl}}/tournaments/{{tournamentId}}/stream/block
Authorization: Bearer {{accessToken}}
Content-Type: application/json

{
  "reason": "Contenido inapropiado"
}
```

### 10.4 Desbloquear Stream
```http
POST {{apiUrl}}/tournaments/{{tournamentId}}/stream/unblock
Authorization: Bearer {{accessToken}}
```

## 11. Colección Postman - Auditoría

### 11.1 Listar Logs de Auditoría
```http
GET {{apiUrl}}/audit-logs?page=0&size=20
Authorization: Bearer {{accessToken}}
```

### 11.2 Logs por Entidad
```http
GET {{apiUrl}}/audit-logs/entity/{{tournamentId}}
Authorization: Bearer {{accessToken}}
```

### 11.3 Logs por Actor
```http
GET {{apiUrl}}/audit-logs/actor/{{userId}}
Authorization: Bearer {{accessToken}}
```

### 11.4 Logs por Tipo de Evento
```http
GET {{apiUrl}}/audit-logs/event-type/TOURNAMENT_CREATED
Authorization: Bearer {{accessToken}}
```

## 12. Scripts de Prueba Automatizados

### 12.1 Script Bash - Pruebas Básicas
```bash
#!/bin/bash
BASE_URL="http://localhost:8081"

# Health Check
curl -X GET "$BASE_URL/actuator/health"

# Categorías
curl -X GET "$BASE_URL/api/categories"

# Tipos de juego
curl -X GET "$BASE_URL/api/game-types"

# Crear usuario
curl -X POST "$BASE_URL/api/users" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "fullName": "Usuario Test",
    "role": "USER"
  }'
```

### 12.2 Script con Autenticación
```bash
#!/bin/bash
BASE_URL="http://localhost:8081"

# Login
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@torneos.com"}')

# Extraer token
TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.accessToken')

# Crear torneo con autenticación
curl -X POST "$BASE_URL/api/tournaments" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Torneo Test",
    "description": "Torneo de prueba",
    "categoryId": "category-uuid",
    "gameTypeId": "game-type-uuid",
    "isPaid": false,
    "maxFreeCapacity": 100,
    "startDateTime": "2024-12-25T10:00:00",
    "endDateTime": "2024-12-25T18:00:00"
  }'
```

## 13. Headers Requeridos por Endpoint

### 13.1 Endpoints Públicos
```http
# No requieren autenticación
GET /api/categories
GET /api/game-types
GET /api/tournaments
POST /api/users
POST /api/auth/login
```

### 13.2 Endpoints Autenticados
```http
# Requieren Authorization header
Authorization: Bearer <jwt_token>

# Ejemplos:
GET /api/users
PUT /api/users/{id}
GET /api/audit-logs
```

### 13.3 Endpoints con Usuario Específico
```http
# Requieren X-USER-ID header adicional
Authorization: Bearer <jwt_token>
X-USER-ID: <user_uuid>

# Ejemplos:
POST /api/tournaments
PUT /api/tournaments/{id}
POST /api/tournaments/{id}/orders
```

## 14. Códigos de Respuesta y Manejo de Errores

### 14.1 Respuestas Exitosas
```json
// 200 OK
{
  "id": "uuid",
  "name": "Torneo Test",
  "status": "DRAFT"
}

// 201 Created
{
  "id": "uuid",
  "email": "user@example.com",
  "createdAt": "2024-12-01T10:00:00"
}

// 204 No Content
// (Sin cuerpo de respuesta)
```

### 14.2 Respuestas de Error
```json
// 400 Bad Request
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "instance": "/api/tournaments"
}

// 401 Unauthorized
{
  "type": "about:blank",
  "title": "Unauthorized",
  "status": 401,
  "detail": "JWT token is missing or invalid"
}

// 403 Forbidden
{
  "type": "about:blank",
  "title": "Forbidden",
  "status": 403,
  "detail": "Access denied for this resource"
}
```

## 15. Configuración de CORS

### 15.1 Orígenes Permitidos
```java
@CrossOrigin(origins = "http://localhost:4200")
```

### 15.2 Headers CORS
```http
Access-Control-Allow-Origin: http://localhost:4200
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
Access-Control-Allow-Headers: Authorization, Content-Type, X-USER-ID
```

---

**Conclusión**: La documentación proporciona una guía completa para usar las APIs con Postman, incluyendo configuración de entorno, colecciones organizadas por funcionalidad, scripts de prueba automatizados y manejo de autenticación JWT.
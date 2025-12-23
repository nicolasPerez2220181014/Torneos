# Ejemplos de Requests - API Torneos

## Configuración Base
- **Base URL**: `http://localhost:8081`
- **Swagger UI**: `http://localhost:8081/swagger-ui.html`

## 1. Health Check
```bash
curl -X GET http://localhost:8081/actuator/health
```

## 2. Autenticación JWT

### Iniciar Sesión
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "organizer@example.com"
  }'
```

### Renovar Token
```bash
curl -X POST http://localhost:8081/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
  }'
```

## 3. Categorías

### Crear Categoría
```bash
curl -X POST http://localhost:8081/api/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{
    "name": "Battle Royale"
  }'
```

### Listar Categorías (con paginación)
```bash
curl -X GET "http://localhost:8081/api/categories?page=0&size=10&sort=name"
```

### Obtener Categoría por ID
```bash
curl -X GET http://localhost:8081/api/categories/{id}
```

### Actualizar Categoría
```bash
curl -X PUT http://localhost:8081/api/categories/{id} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{
    "name": "Battle Royale Actualizado",
    "active": true
  }'
```

## 4. Tipos de Juego

### Crear Tipo de Juego
```bash
curl -X POST http://localhost:8081/api/game-types \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{
    "name": "Fortnite"
  }'
```

### Listar Tipos de Juego
```bash
curl -X GET "http://localhost:8081/api/game-types?page=0&size=10&sort=name"
```

### Obtener Tipo de Juego por ID
```bash
curl -X GET http://localhost:8081/api/game-types/{id}
```

### Actualizar Tipo de Juego
```bash
curl -X PUT http://localhost:8081/api/game-types/{id} \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{
    "name": "Fortnite Battle Royale",
    "active": true
  }'
```

## 5. Usuarios

### Crear Usuario
```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "organizer@example.com",
    "fullName": "Juan Organizador",
    "role": "ORGANIZER"
  }'
```

### Listar Usuarios
```bash
curl -X GET "http://localhost:8081/api/users?page=0&size=10&sort=fullName"
```

### Obtener Usuario por ID
```bash
curl -X GET http://localhost:8081/api/users/{id}
```

### Obtener Usuario por Email
```bash
curl -X GET http://localhost:8081/api/users/email/organizer@example.com
```

### Actualizar Usuario
```bash
curl -X PUT http://localhost:8081/api/users/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "email": "organizer.updated@example.com",
    "fullName": "Juan Organizador Actualizado",
    "role": "ORGANIZER"
  }'
```

## 5. Torneos

### Crear Torneo (solo ORGANIZER)
```bash
curl -X POST http://localhost:8081/api/tournaments \
  -H "Content-Type: application/json" \
  -H "X-USER-ID: {organizerId}" \
  -d '{
    "categoryId": "{categoryId}",
    "gameTypeId": "{gameTypeId}",
    "name": "Torneo de Fortnite 2025",
    "description": "Torneo competitivo de Fortnite",
    "isPaid": false,
    "maxFreeCapacity": 100,
    "startDateTime": "2025-01-15T10:00:00",
    "endDateTime": "2025-01-15T18:00:00"
  }'
```

### Listar Torneos con Filtros
```bash
# Todos los torneos
curl -X GET "http://localhost:8081/api/tournaments?page=0&size=10"

# Torneos gratuitos publicados
curl -X GET "http://localhost:8081/api/tournaments?isPaid=false&status=PUBLISHED"

# Torneos por organizador
curl -X GET "http://localhost:8081/api/tournaments?organizerId={organizerId}"

# Torneos por categoría
curl -X GET "http://localhost:8081/api/tournaments?categoryId={categoryId}"
```

### Obtener Torneo por ID
```bash
curl -X GET http://localhost:8081/api/tournaments/{id}
```

### Actualizar Torneo
```bash
curl -X PUT http://localhost:8081/api/tournaments/{id} \
  -H "Content-Type: application/json" \
  -H "X-USER-ID: {organizerId}" \
  -d '{
    "categoryId": "{categoryId}",
    "gameTypeId": "{gameTypeId}",
    "name": "Torneo Actualizado",
    "description": "Descripción actualizada",
    "isPaid": true,
    "maxFreeCapacity": null,
    "startDateTime": "2025-01-20T10:00:00",
    "endDateTime": "2025-01-20T18:00:00"
  }'
```

### Publicar Torneo
```bash
curl -X POST http://localhost:8081/api/tournaments/{id}/publish \
  -H "X-USER-ID: {organizerId}"
```

### Asignar Subadministrador (máximo 2)
```bash
curl -X POST http://localhost:8081/api/tournaments/{id}/subadmins \
  -H "Content-Type: application/json" \
  -H "X-USER-ID: {organizerId}" \
  -d '{
    "subAdminUserId": "{subAdminUserId}"
  }'
```

### Listar Subadministradores
```bash
curl -X GET http://localhost:8081/api/tournaments/{id}/subadmins
```

### Remover Subadministrador
```bash
curl -X DELETE http://localhost:8081/api/tournaments/{tournamentId}/subadmins/{subAdminId} \
  -H "X-USER-ID: {organizerId}"
```

## 6. Etapas de Venta de Tickets

### Crear Etapa de Venta
```bash
curl -X POST http://localhost:8081/api/tournaments/{tournamentId}/stages \
  -H "Content-Type: application/json" \
  -H "X-USER-ID: {organizerId}" \
  -d '{
    "stageType": "EARLY_BIRD",
    "price": 25.00,
    "capacity": 50,
    "startDateTime": "2025-01-01T00:00:00",
    "endDateTime": "2025-01-10T23:59:59"
  }'
```

### Listar Etapas de Venta
```bash
curl -X GET http://localhost:8081/api/tournaments/{tournamentId}/stages
```

### Actualizar Etapa de Venta
```bash
curl -X PUT http://localhost:8081/api/tournaments/{tournamentId}/stages/{stageId} \
  -H "Content-Type: application/json" \
  -H "X-USER-ID: {organizerId}" \
  -d '{
    "stageType": "REGULAR",
    "price": 35.00,
    "capacity": 100,
    "startDateTime": "2025-01-11T00:00:00",
    "endDateTime": "2025-01-20T23:59:59"
  }'
```

## 7. Órdenes y Tickets

### Comprar Tickets
```bash
curl -X POST http://localhost:8081/api/tournaments/{tournamentId}/orders \
  -H "Content-Type: application/json" \
  -H "X-USER-ID: {userId}" \
  -d '{
    "stageId": "{stageId}",
    "quantity": 2
  }'
```

### Obtener Orden
```bash
curl -X GET http://localhost:8081/api/orders/{orderId}
```

### Listar Tickets de Usuario en Torneo
```bash
curl -X GET "http://localhost:8081/api/tournaments/{tournamentId}/tickets?userId={userId}"
```

### Listar Todos los Tickets de Torneo
```bash
curl -X GET http://localhost:8081/api/tournaments/{tournamentId}/tickets
```

### Validar Ticket (marcar como usado)
```bash
curl -X POST http://localhost:8081/api/tickets/{accessCode}/validate
```

### Obtener Ticket por Código
```bash
curl -X GET http://localhost:8081/api/tickets/{accessCode}
```

## 8. Acceso a Streams

### Solicitar Acceso FREE (máximo 1 por usuario)
```bash
curl -X POST http://localhost:8081/api/tournaments/{tournamentId}/stream/access \
  -H "Content-Type: application/json" \
  -H "X-USER-ID: {userId}" \
  -d '{
    "accessType": "FREE"
  }'
```

### Solicitar Acceso PAID (con ticket)
```bash
curl -X POST http://localhost:8081/api/tournaments/{tournamentId}/stream/access \
  -H "Content-Type: application/json" \
  -H "X-USER-ID: {userId}" \
  -d '{
    "accessType": "PAID",
    "ticketAccessCode": "TKT-A1B2C3D4"
  }'
```

### Obtener Acceso del Usuario
```bash
curl -X GET http://localhost:8081/api/tournaments/{tournamentId}/stream/access \
  -H "X-USER-ID: {userId}"
```

### Listar Todos los Accesos (solo organizadores)
```bash
curl -X GET http://localhost:8081/api/tournaments/{tournamentId}/stream/access/all \
  -H "X-ROLE: ORGANIZER"
```

## 9. Control de Streams

### Actualizar URL del Stream
```bash
curl -X PUT http://localhost:8081/api/tournaments/{tournamentId}/stream/url \
  -H "Content-Type: application/json" \
  -H "X-USER-ID: {organizerId}" \
  -d '{
    "streamUrl": "https://stream.example.com/tournament123"
  }'
```

### Bloquear Stream
```bash
curl -X POST http://localhost:8081/api/tournaments/{tournamentId}/stream/block \
  -H "Content-Type: application/json" \
  -H "X-USER-ID: {organizerId}" \
  -d '{
    "blockReason": "Contenido inapropiado detectado"
  }'
```

### Desbloquear Stream
```bash
curl -X POST http://localhost:8081/api/tournaments/{tournamentId}/stream/unblock \
  -H "X-USER-ID: {organizerId}"
```

### Obtener Estado del Stream
```bash
curl -X GET http://localhost:8081/api/tournaments/{tournamentId}/stream/status \
  -H "X-USER-ID: {userId}"
```

## 10. Auditoría

### Listar Todos los Logs de Auditoría
```bash
curl -X GET "http://localhost:8081/api/audit-logs?page=0&size=10&sort=createdAt,desc"
```

### Listar Logs por Entidad
```bash
curl -X GET "http://localhost:8081/api/audit-logs/entity/{entityId}?page=0&size=10"
```

### Listar Logs por Usuario Actor
```bash
curl -X GET "http://localhost:8081/api/audit-logs/actor/{actorUserId}?page=0&size=10"
```

### Listar Logs por Tipo de Evento
```bash
curl -X GET "http://localhost:8081/api/audit-logs/event-type/TOURNAMENT_CREATED?page=0&size=10"
```

### Listar Logs por Tipo de Entidad
```bash
curl -X GET "http://localhost:8081/api/audit-logs/entity-type/TOURNAMENT?page=0&size=10"
```

## Respuestas de Ejemplo

### Respuesta de Login
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjNlNDU2Ny1lODliLTEyZDMtYTQ1Ni00MjY2MTQxNzQwMDAiLCJlbWFpbCI6Im9yZ2FuaXplckBleGFtcGxlLmNvbSIsInJvbGUiOiJPUkdBTklaRVIiLCJpYXQiOjE3MDMwMjQ0MDAsImV4cCI6MTcwMzAyODAwMH0.signature",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjNlNDU2Ny1lODliLTEyZDMtYTQ1Ni00MjY2MTQxNzQwMDAiLCJpYXQiOjE3MDMwMjQ0MDAsImV4cCI6MTcwMzExMDgwMH0.signature",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "email": "organizer@example.com",
  "role": "ORGANIZER"
}
```

### Etapa de Venta Creada
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "tournamentId": "456e7890-e89b-12d3-a456-426614174000",
  "stageType": "EARLY_BIRD",
  "price": 25.00,
  "capacity": 50,
  "startDateTime": "2025-01-01T00:00:00",
  "endDateTime": "2025-01-10T23:59:59",
  "active": true
}
```

### Orden de Compra Creada
```json
{
  "id": "789e0123-e89b-12d3-a456-426614174000",
  "tournamentId": "456e7890-e89b-12d3-a456-426614174000",
  "userId": "012e3456-e89b-12d3-a456-426614174000",
  "stageId": "123e4567-e89b-12d3-a456-426614174000",
  "quantity": 2,
  "totalAmount": 50.00,
  "status": "PENDING",
  "createdAt": "2025-12-19T20:00:00"
}
```

### Ticket Generado
```json
{
  "id": "345e6789-e89b-12d3-a456-426614174000",
  "orderId": "789e0123-e89b-12d3-a456-426614174000",
  "tournamentId": "456e7890-e89b-12d3-a456-426614174000",
  "userId": "012e3456-e89b-12d3-a456-426614174000",
  "accessCode": "TKT-A1B2C3D4",
  "status": "ISSUED",
  "usedAt": null,
  "createdAt": "2025-12-19T20:00:00"
}
```

### Acceso a Stream Creado
```json
{
  "id": "567e8901-e89b-12d3-a456-426614174000",
  "tournamentId": "456e7890-e89b-12d3-a456-426614174000",
  "userId": "012e3456-e89b-12d3-a456-426614174000",
  "accessType": "FREE",
  "ticketId": null,
  "createdAt": "2025-12-19T20:00:00"
}
```

### Estado del Stream
```json
{
  "tournamentId": "456e7890-e89b-12d3-a456-426614174000",
  "streamUrl": "https://stream.example.com/tournament123",
  "blocked": false,
  "blockReason": null,
  "blockedAt": null,
  "hasAccess": true,
  "accessType": "FREE"
}
```

### Stream Bloqueado
```json
{
  "tournamentId": "456e7890-e89b-12d3-a456-426614174000",
  "streamUrl": null,
  "blocked": true,
  "blockReason": "Contenido inapropiado detectado",
  "blockedAt": "2025-12-19T20:30:00",
  "hasAccess": true,
  "accessType": "PAID"
}
```

### Log de Auditoría
```json
{
  "id": "890e1234-e89b-12d3-a456-426614174000",
  "eventType": "TOURNAMENT_CREATED",
  "entityType": "TOURNAMENT",
  "entityId": "456e7890-e89b-12d3-a456-426614174000",
  "actorUserId": "012e3456-e89b-12d3-a456-426614174000",
  "metadata": "Torneo 'Fortnite Championship 2025' creado",
  "createdAt": "2025-12-19T20:00:00"
}
```

### Torneo Creado
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "organizerId": "456e7890-e89b-12d3-a456-426614174000",
  "categoryId": "789e0123-e89b-12d3-a456-426614174000",
  "gameTypeId": "012e3456-e89b-12d3-a456-426614174000",
  "name": "Torneo de Fortnite 2025",
  "description": "Torneo competitivo de Fortnite",
  "isPaid": false,
  "maxFreeCapacity": 100,
  "startDateTime": "2025-01-15T10:00:00",
  "endDateTime": "2025-01-15T18:00:00",
  "status": "DRAFT",
  "createdAt": "2025-12-19T20:00:00",
  "updatedAt": "2025-12-19T20:00:00"
}
```

### Usuario Creado
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "email": "organizer@example.com",
  "fullName": "Juan Organizador",
  "role": "ORGANIZER",
  "createdAt": "2025-12-19T20:00:00",
  "updatedAt": "2025-12-19T20:00:00"
}
```

### Categoría Creada
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Battle Royale",
  "active": true
}
```

### Lista Paginada
```json
{
  "content": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "name": "Battle Royale",
      "active": true
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "ascending": true
    }
  },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### Error de Validación
```json
{
  "code": "VALIDATION_ERROR",
  "message": "Errores de validación",
  "details": {
    "email": "El email debe tener un formato válido",
    "fullName": "El nombre completo es obligatorio"
  },
  "timestamp": "2025-12-19T19:54:31.123456"
}
```

### Error de Regla de Negocio
```json
{
  "code": "BUSINESS_RULE_VIOLATION",
  "message": "Ya existe un usuario con el email: organizer@example.com",
  "details": null,
  "timestamp": "2025-12-19T19:54:31.123456"
}
```

## Autenticación JWT (Reemplaza headers básicos)

Para endpoints que requieren autenticación, usar JWT Bearer token:

```bash
curl -X POST http://localhost:8081/api/tournaments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -d '{ ... }'
```

### Flujo de Autenticación
1. **Login**: `POST /api/auth/login` con email
2. **Usar Access Token**: Incluir en header `Authorization: Bearer {token}`
3. **Renovar**: `POST /api/auth/refresh` cuando expire

### Roles Disponibles
- `USER` - Usuario regular
- `ORGANIZER` - Organizador de torneos  
- `SUBADMIN` - Subadministrador de torneo
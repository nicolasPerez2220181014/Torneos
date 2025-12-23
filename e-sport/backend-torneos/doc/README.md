# Backend Torneos - Spring Boot

> **📋 CONTEXTO COMPLETO**: Ver [PROJECT_CONTEXT.md](PROJECT_CONTEXT.md) para historial completo y fases de desarrollo

Backend para plataforma de torneos virtuales desarrollado con Spring Boot y arquitectura hexagonal.

## 🚀 Inicio Rápido

```bash
# 1. Crear base de datos
createdb torneos_db

# 2. Ejecutar aplicación
mvn spring-boot:run

# 3. Abrir Swagger UI
open http://localhost:8081/swagger-ui.html
```

## 📊 Estado Actual

### ✅ Completado (Fase 1)
- **Masters**: Categories y GameTypes (CRUD completo)
- **Arquitectura**: Clean Architecture implementada
- **Base de datos**: 11 tablas con Flyway
- **APIs**: Swagger + validaciones + paginación
- **Tests**: Unitarios con JUnit 5 + Mockito

### 🎯 Próxima Fase: Usuarios y Autenticación
Ver [PROJECT_CONTEXT.md](PROJECT_CONTEXT.md) para detalles completos.

## 🛠️ Tecnologías

- Java 17+ / Spring Boot 3.2.0
- PostgreSQL + Flyway
- Spring Security + SpringDoc OpenAPI
- JUnit 5 + Mockito
- Maven

## 📋 APIs Disponibles

### Swagger UI
- **URL**: http://localhost:8081/swagger-ui.html
- **API Docs**: http://localhost:8081/api-docs

### Endpoints Actuales
- `GET/POST/PUT /api/categories` - Gestión de categorías
- `GET/POST/PUT /api/game-types` - Gestión de tipos de juego
- `GET /actuator/health` - Health check

### Ejemplos de Uso
Ver [API_EXAMPLES.md](API_EXAMPLES.md) para requests completos.

```bash
# Crear categoría
curl -X POST http://localhost:8081/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Battle Royale"}'

# Listar categorías
curl "http://localhost:8081/api/categories?page=0&size=10&sort=name"
```

## 🏗️ Arquitectura

### Clean Architecture / Hexagonal
```
src/main/java/com/example/torneos/
├── domain/              # Entidades + interfaces repositorio
├── application/         # Servicios + DTOs + validaciones
└── infrastructure/      # Controllers + JPA + configuración
```

### Entidades del Dominio
1. **Category** ✅ - Categorías de torneos
2. **GameType** ✅ - Tipos de juego
3. **User** 🟡 - Usuarios (USER/ORGANIZER/SUBADMIN)
4. **Tournament** 🟡 - Torneos
5. **TicketSaleStage** 🟡 - Etapas de venta
6. **TicketOrder** 🟡 - Órdenes de compra
7. **Ticket** 🟡 - Tickets individuales
8. **StreamAccess** 🟡 - Acceso a streams
9. **StreamLinkControl** 🟡 - Control de enlaces
10. **AuditLog** 🟡 - Auditoría de eventos

## 🔧 Configuración

### Base de Datos
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/torneos_db
    username: postgres
    password: postgres
```

### Puertos
- **Backend**: 8081
- **PostgreSQL**: 5432
- **Frontend Angular**: 4200 (CORS configurado)

## 🧪 Testing

```bash
mvn test                           # Todos los tests
mvn test -Dtest=CategoryServiceTest # Test específico
```

## 📁 Archivos Importantes

- **[PROJECT_CONTEXT.md](PROJECT_CONTEXT.md)** - Contexto completo y fases
- **[API_EXAMPLES.md](API_EXAMPLES.md)** - Ejemplos de requests
- `src/main/resources/db/migration/` - Migraciones Flyway
- `src/test/java/` - Tests unitarios

## 🎯 Desarrollo Futuro

### Fases Planificadas
1. **Fase 2**: Usuarios y autenticación básica
2. **Fase 3**: Torneos con reglas de negocio
3. **Fase 4**: Sistema de tickets y órdenes
4. **Fase 5**: Control de acceso a streams
5. **Fase 6**: Auditoría y eventos
6. **Fase 7**: JWT y seguridad avanzada

### Reglas de Negocio Pendientes
- Organizadores: máximo 2 torneos gratuitos activos
- Usuarios: máximo 1 acceso gratuito a vista virtual
- Torneos: máximo 2 subadministradores
- Pagos simulados para fines educativos

---

**📋 Para continuar desarrollo**: Consultar [PROJECT_CONTEXT.md](PROJECT_CONTEXT.md) con el contexto completo y siguiente fase a implementar.
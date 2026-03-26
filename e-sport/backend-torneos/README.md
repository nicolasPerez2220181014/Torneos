# Backend Torneos E-Sport

API REST para gestión de torneos de e-sports. Desarrollado con Spring Boot 3.2 y Java 17.

## Requisitos

- Java 17
- Maven 3.6+ (o usar el wrapper `./mvnw`)
- PostgreSQL 15 (opcional, se puede usar H2 en desarrollo)

## Cómo correr

```bash
# Con H2 (sin necesidad de BD externa)
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Con PostgreSQL
./mvnw spring-boot:run -Dspring.profiles.active=postgres
```

Si usas PostgreSQL, crea la BD `torneo` y configura las credenciales en `.env` (ver `.env.example`).

## Endpoints útiles

- API: http://localhost:8081
- Swagger: http://localhost:8081/swagger-ui.html
- Health: http://localhost:8081/actuator/health

## Tests

```bash
./mvnw test
```

Los tests de integración usan TestContainers (necesitas Docker corriendo).

## Docker

```bash
docker-compose up -d
```

Levanta PostgreSQL + la app en un solo comando.

## Estructura

El proyecto sigue una arquitectura por capas:

- `domain/` - Entidades y reglas de negocio
- `application/` - Servicios y DTOs
- `infrastructure/` - Controllers, persistencia JPA, configuración de seguridad

## Seguridad

Autenticación con JWT. Roles: USER, ORGANIZER, SUBADMIN.

Los endpoints públicos (GET de torneos, categorías, tipos de juego) no requieren token.
Para crear/modificar torneos se necesita rol ORGANIZER.

## Variables de entorno

```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=torneo
DB_USER=postgres
DB_PASSWORD=1234
```

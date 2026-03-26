# Backend Torneos - Plataforma de Torneos Virtuales

## 🚀 Inicio Rápido

### Requisitos
- Java 17
- Maven 3.9+
- PostgreSQL 15
- Docker (opcional)

### Ejecutar Localmente

```bash
# Compilar
mvn clean package -DskipTests

# Ejecutar
java -jar target/torneos-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### Con Docker

```bash
# Iniciar con docker-compose
docker-compose up -d

# Detener
docker-compose down
```

## 📚 Documentación

### Arquitectura
- **Arquitectura Limpia**: Separación de capas (dominio, aplicación, infraestructura)
- **Eventos de Dominio**: Arquitectura orientada a eventos
- **Seguridad JWT**: Autenticación y autorización basada en tokens
- **Observabilidad**: Métricas, logs estructurados, health checks

### Endpoints Principales
- **API**: http://localhost:8081
- **Swagger**: http://localhost:8081/swagger-ui.html
- **Health**: http://localhost:8081/actuator/health
- **Metrics**: http://localhost:8081/actuator/metrics

### Profiles
- `dev`: Desarrollo con PostgreSQL
- `qa`: QA
- `prod`: Producción

## 🧪 Testing

```bash
# Ejecutar tests
mvn test

# Solo unitarios
mvn test -Dtest="*Test"

# Solo integración
mvn test -Dtest="*IntegrationTest"
```

## 🐳 Docker

```bash
# Build imagen
docker build -t torneos-backend .

# Run
docker run -p 8081:8081 torneos-backend
```

## 🔐 Seguridad

### JWT
- Autenticación basada en tokens
- Roles: USER, ORGANIZER, SUBADMIN
- Endpoints protegidos con @PreAuthorize

### Variables de Entorno
```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=torneo
DB_USER=postgres
DB_PASSWORD=your-password
JWT_SECRET=your-secret-key
```

## 📊 Observabilidad

### Métricas Personalizadas
- `tickets.created`: Total de tickets creados
- `tournaments.published`: Total de torneos publicados

### Logs
- Formato JSON en producción
- Logs estructurados con MDC

## 🚢 Despliegue

### AWS ECS
```bash
cd deployment/aws
./deploy.sh
```

### Azure Container Instances
```bash
cd deployment/azure
./deploy.sh
```

## 📝 Scripts Útiles

```bash
# Iniciar todo (backend + frontend)
/Users/nicolas.perez/Pragma/Torneos/e-sport/start-all.sh

# Detener todo
/Users/nicolas.perez/Pragma/Torneos/e-sport/stop-all.sh

# Ver logs
tail -f backend.log
```

## 🏗️ Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/example/torneos/
│   │       ├── domain/          # Entidades y lógica de negocio
│   │       ├── application/     # Casos de uso y DTOs
│   │       └── infrastructure/  # Controladores, repositorios, config
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-qa.yml
│       └── application-prod.yml
└── test/
    └── java/                    # Tests unitarios e integración
```

## 🔧 Configuración

### Base de Datos
PostgreSQL 15 con Flyway para migraciones

### Cache
Opcional: Redis para cache distribuido

### Monitoreo
- Spring Boot Actuator
- Prometheus metrics
- Health checks

## 📦 CI/CD

### GitHub Actions
Pipeline automático en `.github/workflows/ci-cd.yml`

### GitLab CI
Pipeline en `.gitlab-ci.yml`

## 🤝 Contribuir

1. Fork el proyecto
2. Crear feature branch
3. Commit cambios
4. Push al branch
5. Crear Pull Request

## 📄 Licencia

Proyecto privado - Pragma

## 📞 Soporte

Para más información, consultar la documentación en `/doc`

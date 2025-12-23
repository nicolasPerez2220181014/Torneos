# D. Repositorio de Código Fuente

## Información General
- **Proyecto**: Plataforma de Torneos E-Sport
- **Estructura**: Monorepo con Backend y Frontend separados
- **Tecnologías**: Spring Boot (Backend) + Angular (Frontend)
- **Gestión**: Maven (Backend) + npm (Frontend)
- **Fecha**: Diciembre 2024

## 1. Estructura General del Repositorio

```
Torneos/
├── e-sport/                          # Proyecto principal
│   ├── backend-torneos/              # Backend Spring Boot
│   ├── frontend-torneos/             # Frontend Angular
│   ├── backend-torneos.zip           # Backup del backend
│   ├── start-dev.sh                  # Script de inicio desarrollo
│   └── test-tournament-creation.sh   # Script de pruebas
├── DocumentosLaboratorio/            # Documentación técnica
└── README.md                         # Documentación principal
```

## 2. Estructura del Backend (Spring Boot)

### 2.1 Organización Principal

```
backend-torneos/
├── src/
│   ├── main/
│   │   ├── java/com/example/torneos/     # Código fuente Java
│   │   └── resources/                    # Recursos y configuración
│   └── test/                            # Pruebas unitarias
├── target/                              # Archivos compilados
├── doc/                                 # Documentación del proyecto
├── pom.xml                              # Configuración Maven
├── mvnw                                 # Maven Wrapper
├── *.log                                # Archivos de log
└── test-*.sh                            # Scripts de prueba
```

### 2.2 Arquitectura por Capas (Java)

```
src/main/java/com/example/torneos/
├── TorneosApplication.java              # Clase principal Spring Boot
├── application/                         # Capa de Aplicación
│   ├── dto/                            # Data Transfer Objects
│   │   ├── request/                    # DTOs de entrada
│   │   │   ├── CreateTournamentRequest.java
│   │   │   ├── CreateUserRequest.java
│   │   │   ├── LoginRequestDto.java
│   │   │   └── ...
│   │   └── response/                   # DTOs de salida
│   │       ├── TournamentResponse.java
│   │       ├── UserResponse.java
│   │       ├── AuthResponseDto.java
│   │       └── ...
│   └── service/                        # Servicios de aplicación
│       ├── TournamentService.java
│       ├── UserService.java
│       ├── AuthenticationService.java
│       ├── TicketService.java
│       └── ...
├── domain/                             # Capa de Dominio
│   ├── model/                          # Entidades de dominio
│   │   ├── Tournament.java
│   │   ├── User.java
│   │   ├── Ticket.java
│   │   ├── Category.java
│   │   └── ...
│   ├── repository/                     # Interfaces de repositorio
│   │   ├── TournamentRepository.java
│   │   ├── UserRepository.java
│   │   └── ...
│   └── event/                          # Eventos de dominio
│       ├── DomainEvent.java
│       ├── TournamentPublished.java
│       └── ...
└── infrastructure/                     # Capa de Infraestructura
    ├── controller/                     # Controladores REST
    │   ├── TournamentController.java
    │   ├── UserController.java
    │   ├── AuthController.java
    │   └── ...
    ├── persistence/                    # Implementación de persistencia
    │   ├── entity/                     # Entidades JPA
    │   ├── mapper/                     # Mappers Domain ↔ Entity
    │   └── repository/                 # Implementaciones de repositorio
    ├── config/                         # Configuraciones
    │   ├── SecurityConfig.java
    │   ├── OpenApiConfig.java
    │   ├── AsyncConfig.java
    │   └── ...
    ├── audit/                          # Sistema de auditoría
    ├── cache/                          # Configuración de cache
    ├── metrics/                        # Métricas de negocio
    ├── validation/                     # Validadores personalizados
    └── web/                           # Configuración web
        ├── error/                      # Manejo de errores
        ├── ratelimit/                  # Rate limiting
        └── versioning/                 # Versionado de APIs
```

### 2.3 Recursos y Configuración

```
src/main/resources/
├── db/migration/                       # Scripts Flyway
│   ├── V1__Initial_schema.sql
│   ├── V2__Master_data.sql
│   ├── V3__Security_constraints.sql
│   └── ...
├── application.yml                     # Configuración principal
├── application-dev.yml                 # Perfil desarrollo
├── application-postgres.yml            # Perfil PostgreSQL
├── application-performance.yml         # Perfil performance
└── ValidationMessages.properties       # Mensajes de validación
```

### 2.4 Pruebas Unitarias

```
src/test/java/com/example/torneos/
└── application/service/
    ├── TournamentServiceTest.java
    ├── UserServiceTest.java
    ├── AuditLogServiceTest.java
    ├── JwtServiceTest.java
    └── ...
```

## 3. Estructura del Frontend (Angular)

### 3.1 Organización Principal

```
frontend-torneos/
├── src/                                # Código fuente
├── .angular/                           # Cache de Angular
├── doc/                                # Documentación
├── package.json                        # Dependencias npm
├── angular.json                        # Configuración Angular
├── tsconfig.json                       # Configuración TypeScript
├── tailwind.config.js                  # Configuración Tailwind
├── proxy.conf.json                     # Configuración proxy desarrollo
└── README.md                           # Documentación
```

### 3.2 Arquitectura Frontend (TypeScript)

```
src/
├── app/                                # Aplicación principal
│   ├── core/                          # Módulo central
│   │   ├── components/                # Componentes centrales
│   │   │   └── navbar.component.ts
│   │   ├── guards/                    # Guards de autenticación
│   │   │   └── auth.guard.ts
│   │   ├── interceptors/              # Interceptores HTTP
│   │   │   ├── auth.interceptor.ts
│   │   │   └── error.interceptor.ts
│   │   ├── models/                    # Modelos TypeScript
│   │   │   ├── tournament.models.ts
│   │   │   ├── user.models.ts
│   │   │   ├── auth.models.ts
│   │   │   └── ...
│   │   ├── services/                  # Servicios compartidos
│   │   │   ├── auth.service.ts
│   │   │   ├── token.service.ts
│   │   │   ├── http-base.service.ts
│   │   │   └── ...
│   │   └── utils/                     # Utilidades
│   │       └── tournament.mapper.ts
│   ├── features/                      # Módulos por característica
│   │   ├── auth/                      # Autenticación
│   │   │   └── pages/
│   │   │       └── login.component.ts
│   │   ├── tournaments/               # Gestión de torneos
│   │   │   ├── pages/
│   │   │   │   ├── tournaments-list.component.ts
│   │   │   │   ├── tournament-detail.component.ts
│   │   │   │   └── tournament-form.component.ts
│   │   │   └── services/
│   │   │       └── tournaments.service.ts
│   │   ├── tickets/                   # Gestión de tickets
│   │   │   ├── pages/
│   │   │   │   ├── ticket-purchase.component.ts
│   │   │   │   ├── my-tickets.component.ts
│   │   │   │   └── order-confirmation.component.ts
│   │   │   └── services/
│   │   │       └── tickets.service.ts
│   │   ├── users/                     # Gestión de usuarios
│   │   │   ├── pages/
│   │   │   │   ├── users-list.component.ts
│   │   │   │   └── user-form.component.ts
│   │   │   └── services/
│   │   │       └── users.service.ts
│   │   ├── categories/                # Gestión de categorías
│   │   ├── game-types/                # Gestión de tipos de juego
│   │   ├── streams/                   # Gestión de streaming
│   │   ├── dashboard/                 # Panel de control
│   │   └── ticket-validation/         # Validación de tickets
│   ├── shared/                        # Componentes compartidos
│   │   └── components/
│   │       ├── master-crud.component.ts
│   │       ├── user-selector.component.ts
│   │       └── design-showcase.component.ts
│   ├── app.component.ts               # Componente raíz
│   ├── app.config.ts                  # Configuración de la app
│   └── app.routes.ts                  # Configuración de rutas
├── environments/                       # Configuraciones de entorno
│   ├── environment.ts                 # Desarrollo
│   └── environment.prod.ts            # Producción
├── styles/                            # Estilos globales
│   ├── variables.scss
│   ├── components.scss
│   ├── utilities.scss
│   └── ...
├── assets/                            # Recursos estáticos
├── index.html                         # HTML principal
├── main.ts                            # Punto de entrada
└── styles.scss                        # Estilos principales
```

## 4. Principios de Organización

### 4.1 Backend - Clean Architecture
- **Separación por Capas**: Domain, Application, Infrastructure
- **Inversión de Dependencias**: Domain no depende de Infrastructure
- **Single Responsibility**: Cada clase tiene una responsabilidad específica
- **Testabilidad**: Lógica de dominio aislada y testeable

### 4.2 Frontend - Feature-Based Architecture
- **Módulos por Característica**: Organización funcional
- **Core Module**: Servicios y componentes centrales
- **Shared Module**: Componentes reutilizables
- **Lazy Loading**: Carga diferida de módulos

### 4.3 Convenciones de Nomenclatura

#### Backend (Java)
- **Clases**: PascalCase (TournamentService)
- **Métodos**: camelCase (findById)
- **Constantes**: UPPER_SNAKE_CASE (MAX_CAPACITY)
- **Packages**: lowercase (com.example.torneos.domain)

#### Frontend (TypeScript)
- **Componentes**: kebab-case (tournament-list.component.ts)
- **Servicios**: camelCase (tournaments.service.ts)
- **Interfaces**: PascalCase con 'I' prefix (ITournament)
- **Enums**: PascalCase (TournamentStatus)

## 5. Gestión de Dependencias

### 5.1 Backend (Maven)
```xml
<!-- Principales dependencias -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

### 5.2 Frontend (npm)
```json
{
  "dependencies": {
    "@angular/core": "^17.3.0",
    "@angular/common": "^17.3.0",
    "@angular/router": "^17.3.0",
    "@angular/forms": "^17.3.0",
    "bootstrap": "^5.3.8",
    "rxjs": "~7.8.0"
  }
}
```

## 6. Configuración de Desarrollo

### 6.1 Scripts de Desarrollo
- **start-dev.sh**: Inicia backend y frontend en desarrollo
- **test-tournament-creation.sh**: Pruebas de creación de torneos
- **test-apis.sh**: Pruebas de APIs con curl

### 6.2 Perfiles de Configuración
- **dev**: H2 en memoria para desarrollo rápido
- **postgres**: PostgreSQL para desarrollo completo
- **prod**: Configuración de producción

### 6.3 Proxy de Desarrollo
```json
{
  "/api/*": {
    "target": "http://localhost:8081",
    "secure": true,
    "changeOrigin": true
  }
}
```

## 7. Documentación del Proyecto

### 7.1 Documentación Backend
```
doc/
├── ARQUITECTURA_Y_REFACTOR.md
├── DOCUMENTACION_COMPLETA.md
├── GUIA_IMPLEMENTACION.md
├── PROJECT_CONTEXT.md
├── RESUMEN_EJECUTIVO.md
└── ...
```

### 7.2 Documentación Frontend
```
doc/
├── frontend-evolution.md
└── DESIGN_SYSTEM.md
```

## 8. Mantenibilidad y Escalabilidad

### 8.1 Principios de Mantenibilidad
- **Código Limpio**: Nombres descriptivos, funciones pequeñas
- **Separación de Responsabilidades**: Cada módulo tiene un propósito específico
- **Documentación**: Comentarios útiles y documentación técnica
- **Pruebas**: Cobertura de pruebas unitarias

### 8.2 Preparación para Escalabilidad
- **Modularidad**: Fácil separación en microservicios
- **Configuración Externa**: Parámetros externalizados
- **Logging**: Sistema de logs estructurado
- **Monitoreo**: Actuator endpoints preparados

## 9. Control de Versiones

### 9.1 Estructura de Branches (Recomendada)
```
main/                   # Rama principal (producción)
├── develop/           # Rama de desarrollo
├── feature/           # Ramas de características
├── hotfix/            # Ramas de correcciones urgentes
└── release/           # Ramas de release
```

### 9.2 Convenciones de Commits
- **feat**: Nueva característica
- **fix**: Corrección de bug
- **docs**: Cambios en documentación
- **refactor**: Refactorización de código
- **test**: Adición o modificación de pruebas

## 10. Herramientas de Desarrollo

### 10.1 Backend
- **IDE**: IntelliJ IDEA / Eclipse
- **Build**: Maven 3.x
- **Java**: OpenJDK 17
- **Base de Datos**: PostgreSQL / H2

### 10.2 Frontend
- **IDE**: Visual Studio Code
- **Build**: Angular CLI
- **Node**: Node.js 18+
- **Package Manager**: npm

---

**Conclusión**: La estructura del repositorio sigue principios sólidos de arquitectura de software, con clara separación de responsabilidades, organización modular y preparación para escalabilidad futura. La organización facilita el mantenimiento y la colaboración en equipo.
Plataforma de Torneos de E-Sports

Este proyecto consiste en una plataforma web para la gestión de torneos de videojuegos. Permite organizar competiciones, manejar participantes y administrar resultados, apoyándose en un backend sólido y un frontend moderno.

La solución está compuesta por dos partes principales: un backend desarrollado en Spring Boot y un frontend construido con Angular.

Arquitectura

La estructura general del proyecto es la siguiente:

e-sport/
├── backend-torneos/    → API REST (Spring Boot 3.2 + Java 17)
├── frontend-torneos/   → SPA (Angular 17 + Tailwind CSS)
├── diagramas/          → Diagramas de arquitectura y diseño
└── Documentos/         → Documentación del proyecto

El backend está construido siguiendo una arquitectura hexagonal (puertos y adaptadores), lo que permite separar claramente la lógica de negocio de las dependencias externas.

domain/
Contiene los modelos del negocio, los contratos de los repositorios y los eventos de dominio.
application/
Define los servicios que implementan los casos de uso y los DTOs utilizados para la comunicación.
infrastructure/
Incluye los controladores, la persistencia, la configuración de seguridad y los componentes de observabilidad.

Este enfoque hace que el sistema sea más fácil de mantener, probar y evolucionar con el tiempo.

Tecnologías utilizadas
Capa	Tecnología
Backend	Spring Boot 3.2, Java 17, Spring Security, JWT
Frontend	Angular 17, Tailwind CSS, Bootstrap 5
Base de datos	PostgreSQL 15 (H2 para pruebas)
Migraciones	Flyway
Observabilidad	Spring Actuator, Micrometer, Prometheus, Logstash
Testing	JUnit 5, Mockito, Testcontainers
Contenedores	Docker, Docker Compose
Documentación API	SpringDoc OpenAPI (Swagger UI)
Inicio rápido
Prerrequisitos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

Java 17 o superior
Node.js 18 o superior
Docker y Docker Compose
Ejecución con Docker (recomendado)

Para levantar el entorno completo rápidamente:

cd e-sport/backend-torneos
cp .env.example .env
docker-compose up -d

El backend estará disponible en:
http://localhost:8081

Desarrollo local

Si prefieres trabajar por separado:

Backend:

cd e-sport/backend-torneos
./mvnw spring-boot:run

Frontend:

cd e-sport/frontend-torneos
npm install
npm start

El frontend estará disponible en:
http://localhost:4200

Ejecución completa en desarrollo

También puedes levantar todo el entorno con un solo comando:

cd e-sport
./start-dev.sh

Este script compila e inicia tanto el backend como el frontend. Puedes detener ambos servicios con Ctrl + C.

Ejecución en modo producción local
cd e-sport
./start-all.sh
./stop-all.sh

El primer comando inicia los servicios en segundo plano y el segundo los detiene.

Pruebas de la API

Puedes validar rápidamente el funcionamiento de la API con el siguiente script:

cd e-sport
./test-tournament-creation.sh

Este script verifica la configuración inicial (categorías y tipos de juego) y crea un torneo de prueba.

Variables de entorno
Variable	Descripción	Valor por defecto
SPRING_PROFILE	Perfil activo (dev, qa, prod)	dev
DB_HOST	Host de PostgreSQL	postgres
DB_PORT	Puerto de PostgreSQL	5432
DB_NAME	Nombre de la base de datos	torneo
DB_USER	Usuario de la base de datos	postgres
DB_PASSWORD	Contraseña de la base de datos	1234
Documentación de la API

Una vez el backend esté en ejecución, puedes acceder a la documentación interactiva en:

http://localhost:8081/swagger-ui.html

Desde allí es posible consultar los endpoints disponibles, los modelos de datos y probar las operaciones directamente.

Diagramas

En la carpeta diagramas/ se incluyen los principales diagramas del sistema:

Arquitectura general
Casos de uso
Modelo de dominio
Diagramas de secuencia
Componentes y contexto
Despliegue en Azure

# E. Instalación Local / Ejecución

## Información General
- **Proyecto**: Plataforma de Torneos E-Sport
- **Entorno**: Desarrollo Local
- **SO Soportados**: macOS, Linux, Windows (con WSL)
- **Fecha**: Diciembre 2024

## 1. Requisitos del Sistema

### 1.1 Software Requerido

#### Java Development Kit (JDK)
```bash
# Verificar instalación
java -version
javac -version

# Versión requerida: OpenJDK 17 o superior
# Descargar desde: https://adoptium.net/
```

#### Node.js y npm
```bash
# Verificar instalación
node --version
npm --version

# Versión requerida: Node.js 18+ y npm 9+
# Descargar desde: https://nodejs.org/
```

#### Maven
```bash
# Verificar instalación
mvn --version

# Versión requerida: Maven 3.6+
# El proyecto incluye Maven Wrapper (mvnw)
```

#### PostgreSQL (Opcional - para producción)
```bash
# Verificar instalación
psql --version

# Versión requerida: PostgreSQL 12+
# Para desarrollo se puede usar H2 (incluido)
```

### 1.2 Herramientas Recomendadas
- **IDE Backend**: IntelliJ IDEA, Eclipse, VS Code
- **IDE Frontend**: Visual Studio Code
- **Cliente REST**: Postman, Insomnia
- **Cliente Git**: Git CLI, GitHub Desktop

## 2. Configuración de Base de Datos

### 2.1 Opción 1: H2 (Desarrollo Rápido)
```yaml
# Configuración automática en application-dev.yml
spring:
  profiles:
    active: dev
  datasource:
    url: jdbc:h2:mem:torneos_db
    username: sa
    password: 
```

**Ventajas**:
- ✅ No requiere instalación adicional
- ✅ Base de datos en memoria
- ✅ Consola web en http://localhost:8081/h2-console

### 2.2 Opción 2: PostgreSQL (Desarrollo Completo)
```bash
# 1. Instalar PostgreSQL
# macOS con Homebrew
brew install postgresql
brew services start postgresql

# Ubuntu/Debian
sudo apt update
sudo apt install postgresql postgresql-contrib

# 2. Crear base de datos
sudo -u postgres psql
CREATE DATABASE torneo;
CREATE USER postgres WITH PASSWORD '1234';
GRANT ALL PRIVILEGES ON DATABASE torneo TO postgres;
\q
```

```yaml
# Configuración en application-postgres.yml
spring:
  profiles:
    active: postgres
  datasource:
    url: jdbc:postgresql://localhost:5432/torneo
    username: postgres
    password: 1234
```

## 3. Instalación del Proyecto

### 3.1 Clonar Repositorio
```bash
# Clonar el proyecto
git clone <repository-url>
cd Torneos/e-sport

# Verificar estructura
ls -la
# Debe mostrar: backend-torneos/ frontend-torneos/
```

### 3.2 Configurar Backend
```bash
# Navegar al backend
cd backend-torneos

# Verificar Maven Wrapper
./mvnw --version

# Compilar proyecto (primera vez)
./mvnw clean compile

# Ejecutar pruebas
./mvnw test
```

### 3.3 Configurar Frontend
```bash
# Navegar al frontend
cd ../frontend-torneos

# Instalar dependencias
npm install

# Verificar instalación
npm list --depth=0
```

## 4. Ejecución Manual

### 4.1 Iniciar Backend

#### Con H2 (Desarrollo)
```bash
cd backend-torneos

# Opción 1: Maven directo
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Opción 2: Compilar y ejecutar JAR
./mvnw clean package -DskipTests
java -jar target/torneos-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

#### Con PostgreSQL
```bash
cd backend-torneos

# Asegurar que PostgreSQL esté corriendo
sudo service postgresql start  # Linux
brew services start postgresql # macOS

# Ejecutar con perfil postgres
./mvnw spring-boot:run -Dspring.profiles.active=postgres
```

### 4.2 Verificar Backend
```bash
# Verificar salud del servicio
curl http://localhost:8081/actuator/health

# Respuesta esperada:
# {\"status\":\"UP\"}

# Acceder a Swagger UI
open http://localhost:8081/swagger-ui.html
```

### 4.3 Iniciar Frontend
```bash
cd frontend-torneos

# Desarrollo con proxy
npm start

# O explícitamente
ng serve --proxy-config proxy.conf.json

# Verificar en navegador
open http://localhost:4200
```

## 5. Ejecución Automática (Recomendado)

### 5.1 Script de Desarrollo
```bash
# Desde la carpeta e-sport/
chmod +x start-dev.sh
./start-dev.sh
```

**El script automáticamente**:
1. ✅ Compila el backend
2. ✅ Inicia backend en puerto 8081
3. ✅ Espera que el backend esté listo
4. ✅ Verifica la salud del backend
5. ✅ Inicia frontend en puerto 4200
6. ✅ Configura proxy automáticamente

### 5.2 Salida del Script
```
🚀 INICIANDO ENTORNO DE DESARROLLO
==================================

📦 INICIANDO BACKEND (Puerto 8081)...
⏳ Esperando backend...
🔍 Verificando backend...
✅ Backend iniciado correctamente

🎨 INICIANDO FRONTEND (Puerto 4200)...

🎯 SERVICIOS INICIADOS:
   Backend:  http://localhost:8081
   Frontend: http://localhost:4200
   Swagger:  http://localhost:8081/swagger-ui.html

💡 Presiona Ctrl+C para detener todos los servicios
```

### 5.3 Detener Servicios
```bash
# El script captura Ctrl+C y limpia automáticamente
# O manualmente:
pkill -f "spring-boot:run"
pkill -f "ng serve"
```

## 6. Configuración de Puertos

### 6.1 Puertos por Defecto
- **Backend**: 8081
- **Frontend**: 4200
- **PostgreSQL**: 5432
- **H2 Console**: 8081/h2-console

### 6.2 Cambiar Puertos (si hay conflictos)

#### Backend
```yaml
# En application.yml
server:
  port: 8082  # Cambiar puerto
```

#### Frontend
```bash
# Cambiar puerto del frontend
ng serve --port 4201

# O en angular.json
"serve": {
  "builder": "@angular-devkit/build-angular:dev-server",
  "options": {
    "port": 4201
  }
}
```

## 7. Configuración de Proxy (Frontend → Backend)

### 7.1 Archivo proxy.conf.json
```json
{
  "/api/*": {
    "target": "http://localhost:8081",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug"
  }
}
```

### 7.2 Uso del Proxy
```bash
# El proxy redirige automáticamente:
# http://localhost:4200/api/* → http://localhost:8081/api/*

# Ejemplo:
# Frontend: http://localhost:4200/api/tournaments
# Backend:  http://localhost:8081/api/tournaments
```

## 8. Verificación de la Instalación

### 8.1 Checklist Backend
```bash
# 1. Servicio corriendo
curl http://localhost:8081/actuator/health

# 2. Base de datos conectada
curl http://localhost:8081/api/categories

# 3. Swagger disponible
curl http://localhost:8081/v3/api-docs

# 4. Logs sin errores
tail -f backend-torneos/backend.log
```

### 8.2 Checklist Frontend
```bash
# 1. Aplicación cargando
curl http://localhost:4200

# 2. Proxy funcionando
curl http://localhost:4200/api/categories

# 3. Console del navegador sin errores
# Abrir DevTools → Console
```

### 8.3 Prueba de Integración
```bash
# Crear usuario de prueba
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "fullName": "Usuario Test",
    "role": "USER"
  }'

# Login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com"}'
```

## 9. Solución de Problemas Comunes

### 9.1 Backend no inicia
```bash
# Verificar Java
java -version

# Verificar puerto disponible
lsof -i :8081

# Limpiar y recompilar
./mvnw clean compile

# Verificar logs
tail -f backend.log
```

### 9.2 Frontend no inicia
```bash
# Limpiar cache de npm
npm cache clean --force

# Reinstalar dependencias
rm -rf node_modules package-lock.json
npm install

# Verificar puerto disponible
lsof -i :4200
```

### 9.3 Error de Conexión a Base de Datos
```bash
# Para H2: Verificar perfil activo
grep "active:" src/main/resources/application.yml

# Para PostgreSQL: Verificar servicio
sudo service postgresql status
psql -U postgres -d torneo -c "SELECT 1;"
```

### 9.4 Error de Proxy
```bash
# Verificar configuración
cat frontend-torneos/proxy.conf.json

# Reiniciar frontend con proxy
ng serve --proxy-config proxy.conf.json
```

## 10. Datos de Prueba

### 10.1 Datos Maestros (Automáticos)
El sistema carga automáticamente:
- **Categorías**: Deportes Electrónicos, Battle Royale, MOBA, FPS
- **Juegos**: League of Legends, Fortnite, CS2, Valorant
- **Usuarios**: admin@torneos.com (ORGANIZER)

### 10.2 Crear Datos Adicionales
```bash
# Usar script de pruebas
cd backend-torneos
./test-tournament-creation.sh

# O usar Swagger UI
open http://localhost:8081/swagger-ui.html
```

## 11. Entornos de Desarrollo

### 11.1 Desarrollo Rápido (H2)
```bash
# Perfil: dev
./mvnw spring-boot:run -Dspring.profiles.active=dev
```
- ✅ Base de datos en memoria
- ✅ Inicio rápido
- ✅ No requiere configuración adicional

### 11.2 Desarrollo Completo (PostgreSQL)
```bash
# Perfil: postgres
./mvnw spring-boot:run -Dspring.profiles.active=postgres
```
- ✅ Base de datos persistente
- ✅ Más cercano a producción
- ✅ Mejor para pruebas de integración

### 11.3 Desarrollo con Performance
```bash
# Perfil: performance
./mvnw spring-boot:run -Dspring.profiles.active=performance
```
- ✅ Métricas habilitadas
- ✅ Cache configurado
- ✅ Logging optimizado

---

**Conclusión**: El proceso de instalación y ejecución está completamente automatizado y documentado, permitiendo un inicio rápido del desarrollo con múltiples opciones de configuración según las necesidades del desarrollador.
# 📋 ANÁLISIS Y PLAN DE PRUEBAS - BACKEND TORNEOS

## 🔍 ESTADO ACTUAL DEL PROYECTO

### ✅ Aspectos Positivos Identificados
- **Compilación exitosa**: El proyecto compila sin errores (161 archivos Java)
- **Arquitectura sólida**: Clean Architecture + DDD implementada
- **Migraciones completas**: 5 migraciones Flyway funcionando correctamente
- **Seguridad implementada**: JWT + Spring Security configurado
- **Documentación**: Swagger UI configurado
- **Base de datos**: Soporte para H2 y PostgreSQL

### ❌ Problemas Identificados

#### 1. **Configuración de Perfiles**
- Los perfiles de Spring no se están aplicando correctamente
- Siempre usa el perfil 'dev' por defecto
- No respeta el perfil especificado en línea de comandos

#### 2. **Conflicto de Puertos**
- Puerto 8081 ocupado constantemente
- Configuración de puerto no se aplica correctamente

#### 3. **Conexión a Base de Datos**
- No se conecta a PostgreSQL aunque esté configurado
- Siempre usa H2 en memoria

---

## 🛠️ PLAN DE CORRECCIÓN POR ETAPAS

### **ETAPA 1: Configuración Base** ⏱️ 15 min
1. **Corregir configuración de perfiles**
   - Modificar application.yml principal
   - Crear configuración específica para PostgreSQL
   - Validar conexión a base de datos

2. **Resolver conflictos de puerto**
   - Liberar puerto 8081
   - Configurar puerto alternativo (8082)

### **ETAPA 2: Conexión a PostgreSQL** ⏱️ 10 min
1. **Configurar conexión**
   - Host: localhost
   - Puerto: 5432
   - Base de datos: torneo
   - Usuario: postgres
   - Password: 1234

2. **Ejecutar migraciones**
   - Crear tablas automáticamente con Flyway
   - Insertar datos maestros
   - Validar esquema

### **ETAPA 3: Pruebas de APIs** ⏱️ 45 min

#### 3.1 **APIs Públicas (Sin autenticación)**
```bash
# Health Check
GET http://localhost:8082/api/health

# Categorías
GET http://localhost:8082/api/categories

# Tipos de juego
GET http://localhost:8082/api/game-types

# Torneos (listado público)
GET http://localhost:8082/api/tournaments
```

#### 3.2 **APIs de Autenticación**
```bash
# Registro de usuario
POST http://localhost:8082/api/users
{
  "email": "test@example.com",
  "fullName": "Usuario Test",
  "role": "USER"
}

# Login
POST http://localhost:8082/api/auth/login
{
  "email": "admin@torneos.com",
  "password": "admin123"
}
```

#### 3.3 **APIs Protegidas (Con JWT)**
```bash
# Crear torneo (ORGANIZER)
POST http://localhost:8082/api/tournaments
Authorization: Bearer {jwt_token}

# Gestión de tickets
POST http://localhost:8082/api/tournaments/{id}/orders

# Administración
GET http://localhost:8082/api/audit/logs
```

### **ETAPA 4: Validación Completa** ⏱️ 30 min
1. **Probar todos los endpoints**
2. **Validar reglas de negocio**
3. **Verificar seguridad**
4. **Documentar resultados**

---

## 📊 ENDPOINTS A PROBAR (54 total)

### 🔐 **AuthController** (2 endpoints)
- `POST /api/auth/login` - Login con JWT
- `POST /api/auth/refresh` - Renovar tokens

### 👥 **UserController** (5 endpoints)
- `POST /api/users` - Crear usuario
- `GET /api/users` - Listar usuarios (paginado)
- `GET /api/users/{id}` - Obtener por ID
- `GET /api/users/email/{email}` - Obtener por email
- `PUT /api/users/{id}` - Actualizar usuario

### 🏆 **TournamentController** (8 endpoints)
- `POST /api/tournaments` - Crear torneo
- `GET /api/tournaments` - Listar con filtros
- `GET /api/tournaments/{id}` - Obtener por ID
- `PUT /api/tournaments/{id}` - Actualizar torneo
- `POST /api/tournaments/{id}/publish` - Publicar torneo
- `POST /api/tournaments/{id}/subadmins` - Asignar subadmin
- `GET /api/tournaments/{id}/subadmins` - Listar subadmins
- `DELETE /api/tournaments/{tournamentId}/subadmins/{subAdminId}` - Remover subadmin

### 📂 **CategoryController** (4 endpoints)
- `POST /api/categories` - Crear categoría
- `GET /api/categories` - Listar categorías
- `GET /api/categories/{id}` - Obtener por ID
- `PUT /api/categories/{id}` - Actualizar categoría

### 🎮 **GameTypeController** (4 endpoints)
- `POST /api/game-types` - Crear tipo de juego
- `GET /api/game-types` - Listar tipos
- `GET /api/game-types/{id}` - Obtener por ID
- `PUT /api/game-types/{id}` - Actualizar tipo

### 🎫 **TicketController** (5 endpoints)
- `POST /api/tournaments/{tournamentId}/orders` - Comprar tickets
- `GET /api/orders/{orderId}` - Obtener orden
- `GET /api/tournaments/{tournamentId}/tickets` - Listar tickets
- `POST /api/tickets/{accessCode}/validate` - Validar ticket
- `GET /api/tickets/{accessCode}` - Obtener ticket

### 📊 **TicketSaleStageController** (3 endpoints)
- `POST /api/tournaments/{tournamentId}/stages` - Crear etapa
- `GET /api/tournaments/{tournamentId}/stages` - Listar etapas
- `PUT /api/tournaments/{tournamentId}/stages/{stageId}` - Actualizar etapa

### 📺 **StreamAccessController** (2 endpoints)
- `POST /api/tournaments/{tournamentId}/access` - Solicitar acceso
- `GET /api/tournaments/{tournamentId}/access` - Listar accesos

### 🔗 **StreamLinkControlController** (4 endpoints)
- `PUT /api/tournaments/{tournamentId}/stream/url` - Actualizar URL
- `POST /api/tournaments/{tournamentId}/stream/block` - Bloquear stream
- `POST /api/tournaments/{tournamentId}/stream/unblock` - Desbloquear
- `GET /api/tournaments/{tournamentId}/stream/status` - Estado del stream

### 📋 **AuditLogController** (1 endpoint)
- `GET /api/audit/logs` - Consultar logs (con filtros)

### 🏠 **TorneosController** (2 endpoints)
- `GET /api/health` - Health check básico
- `GET /api/torneos` - Lista de torneos

---

## 🧪 CASOS DE PRUEBA PRIORITARIOS

### **Flujo Principal de Usuario**
1. **Registro** → **Login** → **Ver torneos** → **Comprar ticket** → **Validar acceso**

### **Flujo de Organizador**
1. **Login** → **Crear torneo** → **Configurar etapas** → **Publicar** → **Gestionar**

### **Flujo de Administración**
1. **Login admin** → **Ver logs** → **Gestionar usuarios** → **Supervisar torneos**

---

## 📝 COMANDOS DE EJECUCIÓN

### **Iniciar aplicación con PostgreSQL**
```bash
cd /Users/nicolas.perez/Pragma/e-sport/backend-torneos
mvn spring-boot:run -Dspring.profiles.active=postgres
```

### **Acceso a documentación**
- Swagger UI: http://localhost:8082/swagger-ui.html
- API Docs: http://localhost:8082/api-docs
- Health: http://localhost:8082/actuator/health

---

## 🎯 OBJETIVOS DE LA PRUEBA

1. ✅ **Conectividad**: Todas las APIs responden
2. ✅ **Autenticación**: JWT funciona correctamente
3. ✅ **Autorización**: Roles y permisos aplicados
4. ✅ **Persistencia**: Datos se guardan en PostgreSQL
5. ✅ **Validaciones**: Reglas de negocio funcionan
6. ✅ **Documentación**: Swagger accesible y completo

---

## 📋 CHECKLIST DE VALIDACIÓN

- [ ] Aplicación inicia sin errores
- [ ] Base de datos PostgreSQL conectada
- [ ] Migraciones ejecutadas correctamente
- [ ] Swagger UI accesible
- [ ] APIs públicas funcionan
- [ ] Autenticación JWT operativa
- [ ] APIs protegidas requieren token
- [ ] Roles y permisos aplicados
- [ ] CRUD completo funcional
- [ ] Reglas de negocio validadas
- [ ] Logs de auditoría funcionando
- [ ] Manejo de errores correcto

---

**Tiempo estimado total: 2 horas**
**Estado: En progreso - Etapa 1**
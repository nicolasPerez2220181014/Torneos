# B. Draft de Modelo de Base de Datos

## Información General
- **Proyecto**: Plataforma de Torneos E-Sport
- **Motor de BD**: PostgreSQL (Producción) / H2 (Desarrollo)
- **Herramienta de Migración**: Flyway
- **Fecha**: Diciembre 2024
- **Versión**: 1.0

## 1. Descripción del Modelo Actual

### 1.1 Visión General
El modelo de base de datos implementa un **diseño relacional normalizado** que soporta la gestión completa de torneos E-Sport, incluyendo usuarios, categorías, tipos de juegos, venta de tickets, control de streaming y auditoría.

### 1.2 Características Principales
- **Identificadores**: UUID para todas las entidades principales
- **Auditoría**: Timestamps automáticos (created_at, updated_at)
- **Integridad**: Constraints y foreign keys
- **Performance**: Índices optimizados
- **Extensibilidad**: Estructura preparada para crecimiento

## 2. Entidades Principales

### 2.1 Entidad: Users
**Propósito**: Gestión de usuarios del sistema con diferentes roles.

```sql
CREATE TABLE users (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('USER', 'ORGANIZER', 'SUBADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Atributos**:
- `id`: Identificador único UUID
- `email`: Email único del usuario (login)
- `full_name`: Nombre completo del usuario
- `role`: Rol del usuario (USER, ORGANIZER, SUBADMIN)
- `created_at/updated_at`: Auditoría temporal

**Reglas de Negocio**:
- Email debe ser único en el sistema
- Roles definen permisos específicos
- ORGANIZER puede crear y gestionar torneos
- SUBADMIN puede ayudar en gestión de torneos específicos

### 2.2 Entidad: Categories
**Propósito**: Categorías maestras para clasificar torneos.

```sql
CREATE TABLE categories (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    active BOOLEAN DEFAULT true
);
```

**Datos Maestros**:
- Deportes Electrónicos
- Battle Royale
- MOBA (Multiplayer Online Battle Arena)
- FPS (First Person Shooter)
- Estrategia

### 2.3 Entidad: Game_Types
**Propósito**: Tipos de juegos específicos disponibles.

```sql
CREATE TABLE game_types (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    active BOOLEAN DEFAULT true
);
```

**Datos Maestros**:
- League of Legends
- Fortnite
- Counter-Strike 2
- Valorant
- Dota 2
- Apex Legends

### 2.4 Entidad: Tournaments (Agregado Raíz)
**Propósito**: Entidad central que representa un torneo E-Sport.

```sql
CREATE TABLE tournaments (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    organizer_id UUID NOT NULL REFERENCES users(id),
    category_id UUID NOT NULL REFERENCES categories(id),
    game_type_id UUID NOT NULL REFERENCES game_types(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_paid BOOLEAN DEFAULT false,
    max_free_capacity INTEGER,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' 
        CHECK (status IN ('DRAFT', 'PUBLISHED', 'FINISHED', 'CANCELLED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Estados del Torneo**:
- `DRAFT`: Borrador, en construcción
- `PUBLISHED`: Publicado y disponible
- `FINISHED`: Finalizado
- `CANCELLED`: Cancelado

### 2.5 Entidad: Tournament_Admins
**Propósito**: Relación muchos-a-muchos entre torneos y sub-administradores.

```sql
CREATE TABLE tournament_admins (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    tournament_id UUID NOT NULL REFERENCES tournaments(id),
    sub_admin_user_id UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tournament_id, sub_admin_user_id)
);
```

### 2.6 Entidad: Ticket_Sale_Stages
**Propósito**: Etapas de venta de tickets con precios diferenciados.

```sql
CREATE TABLE ticket_sale_stages (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    tournament_id UUID NOT NULL REFERENCES tournaments(id),
    stage_type VARCHAR(20) NOT NULL 
        CHECK (stage_type IN ('EARLY_BIRD', 'REGULAR', 'LAST_MINUTE')),
    price DECIMAL(10,2) NOT NULL,
    capacity INTEGER NOT NULL,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    active BOOLEAN DEFAULT true
);
```

**Tipos de Etapas**:
- `EARLY_BIRD`: Venta anticipada (precio reducido)
- `REGULAR`: Venta regular
- `LAST_MINUTE`: Última oportunidad

### 2.7 Entidad: Ticket_Orders
**Propósito**: Órdenes de compra de tickets.

```sql
CREATE TABLE ticket_orders (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    tournament_id UUID NOT NULL REFERENCES tournaments(id),
    user_id UUID NOT NULL REFERENCES users(id),
    stage_id UUID REFERENCES ticket_sale_stages(id),
    quantity INTEGER NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' 
        CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2.8 Entidad: Tickets
**Propósito**: Tickets individuales generados por órdenes aprobadas.

```sql
CREATE TABLE tickets (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES ticket_orders(id),
    tournament_id UUID NOT NULL REFERENCES tournaments(id),
    user_id UUID NOT NULL REFERENCES users(id),
    access_code VARCHAR(255) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ISSUED' 
        CHECK (status IN ('ISSUED', 'USED', 'CANCELLED')),
    used_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2.9 Entidad: Stream_Access
**Propósito**: Control de acceso a streams de torneos.

```sql
CREATE TABLE stream_access (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    tournament_id UUID NOT NULL REFERENCES tournaments(id),
    user_id UUID NOT NULL REFERENCES users(id),
    access_type VARCHAR(10) NOT NULL CHECK (access_type IN ('FREE', 'PAID')),
    ticket_id UUID REFERENCES tickets(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2.10 Entidad: Stream_Link_Control
**Propósito**: Gestión y control de enlaces de streaming.

```sql
CREATE TABLE stream_link_control (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    tournament_id UUID NOT NULL REFERENCES tournaments(id),
    stream_url VARCHAR(500),
    blocked BOOLEAN DEFAULT false,
    block_reason TEXT,
    blocked_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2.11 Entidad: Audit_Log
**Propósito**: Registro de auditoría para trazabilidad completa.

```sql
CREATE TABLE audit_log (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    actor_user_id UUID REFERENCES users(id),
    metadata TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 3. Relaciones entre Entidades

### 3.1 Diagrama de Relaciones

```
Users (1) ──────────── (N) Tournaments
  │                         │
  │                         │ (1)
  │                         │
  │ (N)                     │
  │                    Tournament_Admins (N)
  │                         │
  │ (1)                     │
  │                         │
Ticket_Orders (N) ──────────┘
  │ (1)
  │
  │ (N)
Tickets ──────── (1) Stream_Access
  │
  │ (N)
  └── (1) Tournament ──── (1) Stream_Link_Control

Categories (1) ──── (N) Tournaments
Game_Types (1) ──── (N) Tournaments
Tournaments (1) ──── (N) Ticket_Sale_Stages
```

### 3.2 Tipos de Relaciones

#### Relaciones Uno a Muchos (1:N)
- **Users → Tournaments**: Un organizador puede crear múltiples torneos
- **Categories → Tournaments**: Una categoría puede tener múltiples torneos
- **Game_Types → Tournaments**: Un tipo de juego puede tener múltiples torneos
- **Tournaments → Ticket_Sale_Stages**: Un torneo puede tener múltiples etapas
- **Tournaments → Ticket_Orders**: Un torneo puede tener múltiples órdenes
- **Ticket_Orders → Tickets**: Una orden puede generar múltiples tickets

#### Relaciones Muchos a Muchos (N:M)
- **Tournaments ↔ Users (SubAdmins)**: A través de tournament_admins
- **Users ↔ Tournaments (Participants)**: A través de ticket_orders

#### Relaciones Uno a Uno (1:1)
- **Tournament → Stream_Link_Control**: Un torneo tiene un control de stream

## 4. Justificación de Normalización

### 4.1 Primera Forma Normal (1NF)
- ✅ Todos los atributos contienen valores atómicos
- ✅ No hay grupos repetitivos
- ✅ Cada columna tiene un tipo de dato específico

### 4.2 Segunda Forma Normal (2NF)
- ✅ Cumple 1NF
- ✅ Todos los atributos no clave dependen completamente de la clave primaria
- ✅ No hay dependencias parciales

### 4.3 Tercera Forma Normal (3NF)
- ✅ Cumple 2NF
- ✅ No hay dependencias transitivas
- ✅ Separación de entidades maestras (categories, game_types)

### 4.4 Beneficios de la Normalización
1. **Eliminación de Redundancia**: Datos maestros centralizados
2. **Integridad de Datos**: Constraints y foreign keys
3. **Mantenibilidad**: Cambios en un solo lugar
4. **Consistencia**: Reglas de negocio aplicadas a nivel de BD

## 5. Índices para Performance

### 5.1 Índices Implementados

```sql
-- Índices para consultas frecuentes
CREATE INDEX idx_tournaments_organizer ON tournaments(organizer_id);
CREATE INDEX idx_tournaments_category ON tournaments(category_id);
CREATE INDEX idx_tournaments_game_type ON tournaments(game_type_id);
CREATE INDEX idx_tournaments_status ON tournaments(status);

-- Índices para sistema de tickets
CREATE INDEX idx_ticket_orders_tournament ON ticket_orders(tournament_id);
CREATE INDEX idx_ticket_orders_user ON ticket_orders(user_id);
CREATE INDEX idx_tickets_tournament ON tickets(tournament_id);
CREATE INDEX idx_tickets_user ON tickets(user_id);
CREATE INDEX idx_tickets_access_code ON tickets(access_code);

-- Índices para streaming
CREATE INDEX idx_stream_access_tournament ON stream_access(tournament_id);
CREATE INDEX idx_stream_access_user ON stream_access(user_id);

-- Índices para auditoría
CREATE INDEX idx_audit_log_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_log_actor ON audit_log(actor_user_id);
CREATE INDEX idx_audit_log_event_type ON audit_log(event_type);
```

### 5.2 Justificación de Índices
- **Búsquedas por Organizador**: Consultas frecuentes de torneos por usuario
- **Filtros por Categoría/Juego**: Búsquedas de torneos por tipo
- **Estado de Torneos**: Consultas por estado (activos, finalizados)
- **Códigos de Acceso**: Validación rápida de tickets
- **Auditoría**: Consultas de trazabilidad por entidad y actor

## 6. Relación con Reglas de Negocio

### 6.1 Reglas de Torneo
- **Estado DRAFT**: Solo el organizador puede modificar
- **Estado PUBLISHED**: Abierto para inscripciones
- **Fechas**: start_date_time < end_date_time (validado en aplicación)
- **Capacidad**: max_free_capacity controla inscripciones gratuitas

### 6.2 Reglas de Tickets
- **Orden → Tickets**: Una orden aprobada genera tickets individuales
- **Códigos Únicos**: access_code único para validación
- **Estados**: ISSUED → USED (no reversible)
- **Trazabilidad**: Relación completa orden → ticket → usuario

### 6.3 Reglas de Streaming
- **Acceso FREE**: Sin ticket requerido
- **Acceso PAID**: Requiere ticket válido
- **Control de Enlaces**: Posibilidad de bloquear streams
- **Auditoría**: Todos los accesos registrados

## 7. Seguridad y Constraints

### 7.1 Constraints Implementados
- **CHECK Constraints**: Validación de enums a nivel de BD
- **UNIQUE Constraints**: Email único, códigos de acceso únicos
- **FOREIGN KEY Constraints**: Integridad referencial
- **NOT NULL Constraints**: Campos obligatorios

### 7.2 Seguridad de Datos
- **UUID**: Identificadores no secuenciales (seguridad por oscuridad)
- **Roles**: Control de acceso a nivel de aplicación
- **Auditoría**: Registro completo de operaciones críticas
- **Timestamps**: Trazabilidad temporal automática

## 8. Escalabilidad y Evolución

### 8.1 Preparación para Crecimiento
- **Particionamiento**: Posible por tournament_id o fecha
- **Archivado**: Separación de torneos históricos
- **Réplicas**: Estructura preparada para read replicas
- **Sharding**: Posible por región o categoría

### 8.2 Evolución Futura
- **Nuevos Campos**: Estructura extensible
- **Nuevas Entidades**: Relaciones preparadas
- **Migración**: Flyway para cambios controlados
- **Versionado**: Esquema versionado y documentado

---

**Conclusión**: El modelo de base de datos implementa un diseño robusto y normalizado que soporta eficientemente las operaciones de la plataforma de torneos E-Sport, con énfasis en integridad, performance y escalabilidad futura.
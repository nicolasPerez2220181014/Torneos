# 🏗️ AGGREGATE BOUNDARIES - DOMAIN DESIGN

## 📋 DEFINICIÓN DE AGREGADOS

### 1. TournamentAggregate
**Aggregate Root**: Tournament
**Entidades Incluidas**: Tournament, TicketSaleStage[]

**Invariantes**:
- Un torneo debe tener al menos una etapa activa para ser publicado
- Las etapas solo pueden modificarse si el torneo está en estado DRAFT
- Todas las etapas deben pertenecer al mismo torneo

**Operaciones**:
- `addStage()`: Agregar etapa con validaciones
- `publishTournament()`: Publicar con verificación de etapas
- `getActiveStages()`: Obtener etapas activas

### 2. TicketOrderAggregate  
**Aggregate Root**: TicketOrder
**Entidades Incluidas**: TicketOrder, Ticket[]

**Invariantes**:
- Los tickets solo se generan para órdenes aprobadas
- La cantidad de tickets debe coincidir con la cantidad de la orden
- Cada ticket debe tener un código de acceso único

**Operaciones**:
- `approveOrder()`: Aprobar y generar tickets
- `rejectOrder()`: Rechazar y limpiar tickets
- `getTickets()`: Obtener tickets generados

### 3. UserAggregate
**Aggregate Root**: User
**Entidades Incluidas**: User (simple aggregate)

**Invariantes**:
- Email debe ser único y válido
- Solo organizadores pueden crear torneos
- Subadmins pueden gestionar torneos asignados

**Operaciones**:
- `updateProfile()`: Actualizar perfil
- `canManageTournament()`: Verificar permisos

## 🔄 RELACIONES ENTRE AGREGADOS

### Referencias por ID
- TicketOrder → Tournament (tournamentId)
- TicketSaleStage → Tournament (tournamentId)  
- Tournament → User (organizerId)

### Reglas de Consistencia
- **Eventual Consistency**: Entre agregados diferentes
- **Strong Consistency**: Dentro del mismo agregado
- **Transactional Boundaries**: Un agregado por transacción

## 📊 BENEFICIOS

1. **Encapsulación**: Lógica de negocio encapsulada en agregados
2. **Consistencia**: Invariantes garantizadas dentro del agregado
3. **Performance**: Carga optimizada de entidades relacionadas
4. **Escalabilidad**: Transacciones más pequeñas y focalizadas
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DashboardService } from '../services/dashboard.service';
import { AuditLog, AuditFilters } from '../../../core/models/audit.models';
import { PaginatedResponse } from '../../../core/models/api.models';

@Component({
  selector: 'app-audit-logs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container">
      <div class="header">
        <h2>Logs de Auditoría</h2>
        <button class="btn btn-outline" (click)="refreshLogs()">
          Actualizar
        </button>
      </div>

      <!-- Filtros -->
      <div class="filters-section">
        <h3>Filtros</h3>
        <div class="filters-grid">
          <div class="filter-group">
            <label>Acción:</label>
            <select [(ngModel)]="filters.action" (change)="onFilterChange()" class="form-control">
              <option value="">Todas las acciones</option>
              <option value="CREATE">Crear</option>
              <option value="UPDATE">Actualizar</option>
              <option value="DELETE">Eliminar</option>
              <option value="LOGIN">Iniciar Sesión</option>
              <option value="LOGOUT">Cerrar Sesión</option>
              <option value="PURCHASE">Comprar</option>
              <option value="VALIDATE">Validar</option>
            </select>
          </div>

          <div class="filter-group">
            <label>Tipo de Entidad:</label>
            <select [(ngModel)]="filters.entityType" (change)="onFilterChange()" class="form-control">
              <option value="">Todos los tipos</option>
              <option value="Tournament">Torneo</option>
              <option value="User">Usuario</option>
              <option value="Ticket">Ticket</option>
              <option value="Order">Orden</option>
              <option value="Stream">Stream</option>
            </select>
          </div>

          <div class="filter-group">
            <label>Fecha Desde:</label>
            <input 
              type="date" 
              [(ngModel)]="filters.startDate"
              (change)="onFilterChange()"
              class="form-control">
          </div>

          <div class="filter-group">
            <label>Fecha Hasta:</label>
            <input 
              type="date" 
              [(ngModel)]="filters.endDate"
              (change)="onFilterChange()"
              class="form-control">
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div *ngIf="loading" class="loading">
        Cargando logs de auditoría...
      </div>

      <!-- Logs Table -->
      <div *ngIf="!loading" class="logs-section">
        <div class="logs-header">
          <h3>Registros de Actividad</h3>
          <span class="logs-count">{{paginationData?.totalElements || 0}} registros</span>
        </div>

        <div *ngIf="logs.length === 0" class="empty-state">
          No se encontraron registros con los filtros aplicados
        </div>

        <div *ngIf="logs.length > 0" class="logs-table">
          <div class="table-header">
            <div class="col-timestamp">Fecha/Hora</div>
            <div class="col-user">Usuario</div>
            <div class="col-action">Acción</div>
            <div class="col-entity">Entidad</div>
            <div class="col-details">Detalles</div>
          </div>

          <div *ngFor="let log of logs" class="table-row">
            <div class="col-timestamp">
              <div class="timestamp">{{formatDateTime(log.timestamp)}}</div>
            </div>
            
            <div class="col-user">
              <div class="user-info">
                <strong>{{log.user?.firstName}} {{log.user?.lastName}}</strong>
                <small>{{log.user?.email}}</small>
              </div>
            </div>
            
            <div class="col-action">
              <span class="action-badge" [class]="'action-' + log.action.toLowerCase()">
                {{log.action}}
              </span>
            </div>
            
            <div class="col-entity">
              <div class="entity-info">
                <strong>{{log.entityType}}</strong>
                <small>ID: {{log.entityId}}</small>
              </div>
            </div>
            
            <div class="col-details">
              <div class="details" [title]="log.details">
                {{truncateDetails(log.details)}}
              </div>
              <small class="ip-info">{{log.ipAddress}}</small>
            </div>
          </div>
        </div>

        <!-- Paginación -->
        <div *ngIf="paginationData && paginationData.totalElements > 0" class="pagination">
          <button 
            class="btn btn-outline" 
            [disabled]="paginationData.first"
            (click)="goToPage(paginationData.number - 1)">
            Anterior
          </button>
          
          <span class="page-info">
            Página {{paginationData.number + 1}} de {{paginationData.totalPages}}
          </span>
          
          <button 
            class="btn btn-outline" 
            [disabled]="paginationData.last"
            (click)="goToPage(paginationData.number + 1)">
            Siguiente
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .container { max-width: 1400px; margin: 0 auto; padding: 20px; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
    .filters-section { background: white; padding: 20px; border-radius: 8px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
    .filters-section h3 { margin: 0 0 15px 0; }
    .filters-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; }
    .filter-group label { display: block; margin-bottom: 5px; font-weight: bold; color: #666; }
    .form-control { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
    .logs-section { background: white; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
    .logs-header { display: flex; justify-content: space-between; align-items: center; padding: 20px; border-bottom: 1px solid #eee; }
    .logs-header h3 { margin: 0; }
    .logs-count { color: #666; font-size: 14px; }
    .logs-table { overflow-x: auto; }
    .table-header, .table-row { display: grid; grid-template-columns: 150px 200px 100px 120px 1fr; gap: 15px; padding: 15px 20px; }
    .table-header { background: #f9f9f9; font-weight: bold; color: #666; border-bottom: 1px solid #eee; }
    .table-row { border-bottom: 1px solid #f0f0f0; }
    .table-row:hover { background: #f9f9f9; }
    .timestamp { font-size: 12px; color: #666; }
    .user-info { display: flex; flex-direction: column; gap: 2px; }
    .user-info strong { color: #333; }
    .user-info small { color: #666; font-size: 11px; }
    .action-badge { padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: bold; }
    .action-create { background: #e8f5e8; color: #2e7d32; }
    .action-update { background: #e3f2fd; color: #1976d2; }
    .action-delete { background: #ffebee; color: #d32f2f; }
    .action-login { background: #f3e5f5; color: #7b1fa2; }
    .action-logout { background: #f0f0f0; color: #666; }
    .action-purchase { background: #fff3e0; color: #f57c00; }
    .action-validate { background: #e0f2f1; color: #00695c; }
    .entity-info { display: flex; flex-direction: column; gap: 2px; }
    .entity-info strong { color: #333; }
    .entity-info small { color: #666; font-size: 11px; }
    .details { font-size: 12px; color: #333; margin-bottom: 4px; }
    .ip-info { color: #999; font-size: 10px; }
    .pagination { display: flex; justify-content: center; align-items: center; gap: 20px; padding: 20px; }
    .page-info { font-size: 14px; color: #666; }
    .empty-state { text-align: center; padding: 60px; color: #999; }
    .loading { text-align: center; padding: 60px; color: #666; }
    .btn { padding: 8px 16px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: white; }
    .btn-outline { background: white; color: #666; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
  `]
})
export class AuditLogsComponent implements OnInit {
  logs: AuditLog[] = [];
  filters: AuditFilters = {};
  loading = false;
  currentPage = 0;
  pageSize = 20;
  paginationData: any = null;

  constructor(private dashboardService: DashboardService) {}

  ngOnInit() {
    this.loadLogs();
  }

  loadLogs() {
    this.loading = true;
    
    // Mock data for demonstration
    setTimeout(() => {
      this.logs = this.generateMockLogs();
      this.paginationData = {
        number: this.currentPage,
        totalPages: 5,
        totalElements: 97,
        first: this.currentPage === 0,
        last: this.currentPage === 4
      };
      this.loading = false;
    }, 800);
  }

  onFilterChange() {
    this.currentPage = 0;
    this.loadLogs();
  }

  goToPage(page: number) {
    this.currentPage = page;
    this.loadLogs();
  }

  refreshLogs() {
    this.loadLogs();
  }

  formatDateTime(dateString: string): string {
    return new Date(dateString).toLocaleString('es-ES');
  }

  truncateDetails(details: string): string {
    return details.length > 50 ? details.substring(0, 50) + '...' : details;
  }

  private generateMockLogs(): AuditLog[] {
    const actions = ['CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'PURCHASE', 'VALIDATE'];
    const entities = ['Tournament', 'User', 'Ticket', 'Order', 'Stream'];
    const users = [
      { email: 'admin@torneos.com', firstName: 'Admin', lastName: 'Sistema' },
      { email: 'juan@email.com', firstName: 'Juan', lastName: 'Pérez' },
      { email: 'maria@email.com', firstName: 'María', lastName: 'García' }
    ];

    return Array.from({ length: 20 }, (_, i) => ({
      id: i + 1,
      userId: Math.floor(Math.random() * 3) + 1,
      action: actions[Math.floor(Math.random() * actions.length)],
      entityType: entities[Math.floor(Math.random() * entities.length)],
      entityId: Math.floor(Math.random() * 100) + 1,
      details: `Operación realizada sobre entidad con ID ${Math.floor(Math.random() * 100) + 1}`,
      ipAddress: `192.168.1.${Math.floor(Math.random() * 255)}`,
      userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
      timestamp: new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000).toISOString(),
      user: users[Math.floor(Math.random() * users.length)]
    }));
  }
}
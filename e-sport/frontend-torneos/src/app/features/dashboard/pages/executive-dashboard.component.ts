import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { DashboardService } from '../services/dashboard.service';
import { DashboardMetrics, ActivityType } from '../../../core/models/audit.models';

@Component({
  selector: 'app-executive-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container">
      <div class="header">
        <h2>Dashboard Ejecutivo</h2>
        <button class="btn btn-outline" (click)="refreshMetrics()">
          🔄 Actualizar
        </button>
      </div>

      <div *ngIf="loading" class="loading">
        Cargando métricas...
      </div>

      <div *ngIf="metrics && !loading" class="dashboard-content">
        <!-- Key Metrics -->
        <div class="metrics-grid">
          <div class="metric-card primary">
            <div class="metric-icon">🏆</div>
            <div class="metric-content">
              <div class="metric-value">{{metrics.totalTournaments}}</div>
              <div class="metric-label">Total Torneos</div>
              <div class="metric-sublabel">{{metrics.activeTournaments}} activos</div>
            </div>
          </div>

          <div class="metric-card success">
            <div class="metric-icon">👥</div>
            <div class="metric-content">
              <div class="metric-value">{{formatNumber(metrics.totalUsers)}}</div>
              <div class="metric-label">Usuarios Registrados</div>
            </div>
          </div>

          <div class="metric-card warning">
            <div class="metric-icon">🎫</div>
            <div class="metric-content">
              <div class="metric-value">{{formatNumber(metrics.totalTicketsSold)}}</div>
              <div class="metric-label">Tickets Vendidos</div>
            </div>
          </div>

          <div class="metric-card info">
            <div class="metric-icon">💰</div>
            <div class="metric-content">
              <div class="metric-value">\${{formatCurrency(metrics.totalRevenue)}}</div>
              <div class="metric-label">Ingresos Totales</div>
            </div>
          </div>

          <div class="metric-card secondary">
            <div class="metric-icon">📺</div>
            <div class="metric-content">
              <div class="metric-value">{{metrics.activeStreams}}</div>
              <div class="metric-label">Streams Activos</div>
            </div>
          </div>

          <div class="metric-card accent">
            <div class="metric-icon">👁️</div>
            <div class="metric-content">
              <div class="metric-value">{{formatNumber(metrics.totalViews)}}</div>
              <div class="metric-label">Total Visualizaciones</div>
            </div>
          </div>
        </div>

        <!-- Activity Summary -->
        <div class="activity-section">
          <h3>Actividad Reciente</h3>
          <div class="activity-grid">
            <div *ngFor="let activity of metrics.recentActivity" class="activity-card">
              <div class="activity-header">
                <span class="activity-label">{{activity.label}}</span>
                <span class="activity-trend" [class]="getTrendClass(activity.trend)">
                  {{activity.trend > 0 ? '↗' : activity.trend < 0 ? '↘' : '→'}} {{Math.abs(activity.trend)}}%
                </span>
              </div>
              <div class="activity-value">{{formatNumber(activity.count)}}</div>
            </div>
          </div>
        </div>

        <!-- Quick Actions -->
        <div class="actions-section">
          <h3>Acciones Rápidas</h3>
          <div class="actions-grid">
            <button class="action-card" (click)="navigateTo('/tournaments')">
              <div class="action-icon">🏆</div>
              <div class="action-label">Gestionar Torneos</div>
            </button>
            
            <button class="action-card" (click)="navigateTo('/users')">
              <div class="action-icon">👥</div>
              <div class="action-label">Gestionar Usuarios</div>
            </button>
            
            <button class="action-card" (click)="navigateTo('/dashboard/audit')">
              <div class="action-icon">📋</div>
              <div class="action-label">Ver Auditoría</div>
            </button>
            
            <button class="action-card" (click)="navigateTo('/validation')">
              <div class="action-icon">🎫</div>
              <div class="action-label">Validar Tickets</div>
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .container { max-width: 1400px; margin: 0 auto; padding: 20px; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
    .dashboard-content { display: flex; flex-direction: column; gap: 30px; }
    .metrics-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; }
    .metric-card { background: white; border-radius: 12px; padding: 24px; display: flex; align-items: center; gap: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); border-left: 4px solid; }
    .metric-card.primary { border-left-color: #1976d2; }
    .metric-card.success { border-left-color: #2e7d32; }
    .metric-card.warning { border-left-color: #f57c00; }
    .metric-card.info { border-left-color: #0288d1; }
    .metric-card.secondary { border-left-color: #7b1fa2; }
    .metric-card.accent { border-left-color: #d32f2f; }
    .metric-icon { font-size: 32px; }
    .metric-content { flex: 1; }
    .metric-value { font-size: 28px; font-weight: bold; color: #333; margin-bottom: 4px; }
    .metric-label { font-size: 14px; color: #666; font-weight: 500; }
    .metric-sublabel { font-size: 12px; color: #999; margin-top: 4px; }
    .activity-section, .actions-section { background: white; padding: 24px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
    .activity-section h3, .actions-section h3 { margin: 0 0 20px 0; color: #333; }
    .activity-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; }
    .activity-card { background: #f9f9f9; padding: 16px; border-radius: 8px; }
    .activity-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
    .activity-label { font-size: 14px; color: #666; }
    .activity-trend { font-size: 12px; font-weight: bold; }
    .activity-trend.positive { color: #2e7d32; }
    .activity-trend.negative { color: #d32f2f; }
    .activity-trend.neutral { color: #666; }
    .activity-value { font-size: 20px; font-weight: bold; color: #333; }
    .actions-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 15px; }
    .action-card { background: #f5f5f5; border: 2px solid transparent; border-radius: 8px; padding: 20px; cursor: pointer; transition: all 0.3s; display: flex; flex-direction: column; align-items: center; gap: 10px; }
    .action-card:hover { background: #e3f2fd; border-color: #1976d2; }
    .action-icon { font-size: 24px; }
    .action-label { font-size: 14px; font-weight: 500; color: #333; text-align: center; }
    .loading { text-align: center; padding: 60px; color: #666; }
    .btn { padding: 10px 20px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: white; }
    .btn-outline { background: white; color: #666; }
  `]
})
export class ExecutiveDashboardComponent implements OnInit {
  metrics: DashboardMetrics | null = null;
  loading = false;

  constructor(
    private dashboardService: DashboardService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadMetrics();
  }

  loadMetrics() {
    this.loading = true;
    
    // Usar datos reales del backend
    this.dashboardService.getDashboardMetrics().subscribe({
      next: (metrics) => {
        this.metrics = metrics;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading metrics:', error);
        // Fallback a datos mock si falla
        this.metrics = {
          totalTournaments: 0,
          activeTournaments: 0,
          totalUsers: 0,
          totalTicketsSold: 0,
          totalRevenue: 0,
          activeStreams: 0,
          totalViews: 0,
          recentActivity: []
        };
        this.loading = false;
      }
    });
  }

  refreshMetrics() {
    this.loadMetrics();
  }

  navigateTo(route: string) {
    this.router.navigate([route]);
  }

  getTrendClass(trend: number): string {
    if (trend > 0) return 'positive';
    if (trend < 0) return 'negative';
    return 'neutral';
  }

  formatNumber(num: number): string {
    return num.toLocaleString('es-ES');
  }

  formatCurrency(amount: number): string {
    return amount.toLocaleString('es-ES', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }

  Math = Math;
}
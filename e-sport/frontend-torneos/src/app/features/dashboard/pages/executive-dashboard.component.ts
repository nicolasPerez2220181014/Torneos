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
          Actualizar
        </button>
      </div>

      <div *ngIf="loading" class="loading">
        Cargando métricas...
      </div>

      <div *ngIf="metrics && !loading" class="dashboard-content">
        <!-- Key Metrics -->
        <div class="metrics-grid">
          <div class="metric-card primary">
            <div class="metric-icon"></div>
            <div class="metric-content">
              <div class="metric-value">{{metrics.totalTournaments}}</div>
              <div class="metric-label">Total Torneos</div>
              <div class="metric-sublabel">{{metrics.activeTournaments}} activos</div>
            </div>
          </div>

          <div class="metric-card success">
            <div class="metric-icon"></div>
            <div class="metric-content">
              <div class="metric-value">{{formatNumber(metrics.totalUsers)}}</div>
              <div class="metric-label">Usuarios Registrados</div>
            </div>
          </div>

          <div class="metric-card warning">
            <div class="metric-icon"></div>
            <div class="metric-content">
              <div class="metric-value">{{formatNumber(metrics.totalTicketsSold)}}</div>
              <div class="metric-label">Tickets Vendidos</div>
            </div>
          </div>

          <div class="metric-card info">
            <div class="metric-icon"></div>
            <div class="metric-content">
              <div class="metric-value">\${{formatCurrency(metrics.totalRevenue)}}</div>
              <div class="metric-label">Ingresos Totales</div>
            </div>
          </div>

          <div class="metric-card secondary">
            <div class="metric-icon"></div>
            <div class="metric-content">
              <div class="metric-value">{{metrics.activeStreams}}</div>
              <div class="metric-label">Streams Activos</div>
            </div>
          </div>

          <div class="metric-card accent">
            <div class="metric-icon"></div>
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
              <div class="action-icon"></div>
              <div class="action-label">Gestionar Torneos</div>
            </button>
            
            <button class="action-card" (click)="navigateTo('/users')">
              <div class="action-icon"></div>
              <div class="action-label">Gestionar Usuarios</div>
            </button>
            
            <button class="action-card" (click)="navigateTo('/dashboard/audit')">
              <div class="action-icon"></div>
              <div class="action-label">Ver Auditoría</div>
            </button>
            
            <button class="action-card" (click)="navigateTo('/validation')">
              <div class="action-icon"></div>
              <div class="action-label">Validar Tickets</div>
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: var(--sp-2xl);
    }
    .header h2 { font-size: 1.4rem; }

    .dashboard-content {
      display: flex;
      flex-direction: column;
      gap: var(--sp-2xl);
    }

    .metrics-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
      gap: var(--sp-lg);
    }

    .metric-card {
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: var(--radius-lg);
      padding: var(--sp-xl);
      display: flex;
      align-items: center;
      gap: var(--sp-lg);
      border-left: 3px solid;
      transition: all 0.2s var(--ease);
    }
    .metric-card:hover {
      box-shadow: var(--shadow-md);
      border-color: var(--border-hover);
    }
    .metric-card.primary { border-left-color: var(--accent); }
    .metric-card.success { border-left-color: var(--success); }
    .metric-card.warning { border-left-color: var(--warning); }
    .metric-card.info { border-left-color: var(--info); }
    .metric-card.secondary { border-left-color: #a29bfe; }
    .metric-card.accent { border-left-color: var(--danger); }

    .metric-content { flex: 1; }
    .metric-value {
      font-size: 1.6rem;
      font-weight: 700;
      color: var(--text-primary);
      margin-bottom: 2px;
    }
    .metric-label {
      font-size: 0.8rem;
      color: var(--text-secondary);
      font-weight: 500;
    }
    .metric-sublabel {
      font-size: 0.7rem;
      color: var(--text-muted);
      margin-top: 2px;
    }

    .activity-section, .actions-section {
      background: var(--bg-card);
      border: 1px solid var(--border);
      padding: var(--sp-xl);
      border-radius: var(--radius-lg);
    }
    .activity-section h3, .actions-section h3 {
      margin: 0 0 var(--sp-lg) 0;
      font-size: 1.05rem;
    }

    .activity-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
      gap: var(--sp-md);
    }
    .activity-card {
      background: var(--bg-secondary);
      padding: var(--sp-lg);
      border-radius: var(--radius-md);
      border: 1px solid var(--border);
    }
    .activity-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: var(--sp-sm);
    }
    .activity-label { font-size: 0.8rem; color: var(--text-muted); }
    .activity-trend { font-size: 0.75rem; font-weight: 600; }
    .activity-trend.positive { color: var(--success); }
    .activity-trend.negative { color: var(--danger); }
    .activity-trend.neutral { color: var(--text-muted); }
    .activity-value {
      font-size: 1.2rem;
      font-weight: 700;
      color: var(--text-primary);
    }

    .actions-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
      gap: var(--sp-md);
    }
    .action-card {
      background: var(--bg-secondary);
      border: 1px solid var(--border);
      border-radius: var(--radius-md);
      padding: var(--sp-xl) var(--sp-lg);
      cursor: pointer;
      transition: all 0.2s var(--ease);
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: var(--sp-sm);
      text-align: center;
    }
    .action-card:hover {
      border-color: var(--accent);
      background: var(--accent-soft);
    }
    .action-label {
      font-size: 0.8rem;
      font-weight: 500;
      color: var(--text-secondary);
    }
    .action-card:hover .action-label { color: var(--text-primary); }

    @media (max-width: 768px) {
      .metrics-grid { grid-template-columns: 1fr 1fr; }
      .header { flex-direction: column; gap: var(--sp-md); align-items: flex-start; }
    }
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
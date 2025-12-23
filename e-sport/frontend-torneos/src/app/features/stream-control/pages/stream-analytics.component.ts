import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StreamAnalytics, StreamQuality } from '../../../core/models/stream-control.models';

@Component({
  selector: 'app-stream-analytics',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="analytics-dashboard">
      <h4>Analytics del Stream</h4>

      <!-- Key Metrics -->
      <div class="metrics-grid">
        <div class="metric-card">
          <div class="metric-value">{{analytics?.totalViews || 0}}</div>
          <div class="metric-label">Total de Vistas</div>
        </div>
        
        <div class="metric-card">
          <div class="metric-value">{{analytics?.peakViewers || 0}}</div>
          <div class="metric-label">Pico de Espectadores</div>
        </div>
        
        <div class="metric-card">
          <div class="metric-value">{{formatTime(analytics?.averageViewTime || 0)}}</div>
          <div class="metric-label">Tiempo Promedio</div>
        </div>
        
        <div class="metric-card">
          <div class="metric-value">{{analytics?.chatMessages || 0}}</div>
          <div class="metric-label">Mensajes de Chat</div>
        </div>
      </div>

      <!-- Quality Distribution -->
      <div class="quality-section" *ngIf="analytics?.qualityDistribution">
        <h5>Distribución de Calidad</h5>
        <div class="quality-bars">
          <div *ngFor="let quality of analytics?.qualityDistribution || []" class="quality-bar">
            <div class="quality-info">
              <span class="quality-name">{{getQualityLabel(quality.quality)}}</span>
              <span class="quality-percentage">{{quality.percentage}}%</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" [style.width.%]="quality.percentage"></div>
            </div>
            <span class="quality-viewers">{{quality.viewers}} espectadores</span>
          </div>
        </div>
      </div>

      <!-- Viewer Timeline -->
      <div class="timeline-section" *ngIf="analytics?.viewersByHour">
        <h5>Espectadores por Hora</h5>
        <div class="timeline-chart">
          <div *ngFor="let stat of analytics?.viewersByHour || []" class="timeline-bar">
            <div class="bar-container">
              <div 
                class="bar-fill" 
                [style.height.%]="getBarHeight(stat.viewers)">
              </div>
            </div>
            <div class="bar-label">{{stat.hour}}</div>
            <div class="bar-value">{{stat.viewers}}</div>
          </div>
        </div>
      </div>

      <!-- Refresh Button -->
      <div class="analytics-actions">
        <button class="btn btn-outline" (click)="refreshAnalytics()">
          🔄 Actualizar Datos
        </button>
      </div>
    </div>
  `,
  styles: [`
    .analytics-dashboard { margin-top: 20px; }
    .analytics-dashboard h4 { margin: 0 0 20px 0; }
    .analytics-dashboard h5 { margin: 0 0 15px 0; font-size: 16px; }
    .metrics-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 15px; margin-bottom: 30px; }
    .metric-card { background: white; padding: 20px; border-radius: 8px; text-align: center; border: 1px solid #ddd; }
    .metric-value { font-size: 24px; font-weight: bold; color: #1976d2; margin-bottom: 5px; }
    .metric-label { font-size: 14px; color: #666; }
    .quality-section, .timeline-section { background: white; padding: 20px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #ddd; }
    .quality-bars { display: flex; flex-direction: column; gap: 15px; }
    .quality-bar { display: flex; align-items: center; gap: 15px; }
    .quality-info { min-width: 120px; display: flex; justify-content: space-between; }
    .quality-name { font-weight: bold; }
    .quality-percentage { color: #1976d2; }
    .progress-bar { flex: 1; height: 20px; background: #f0f0f0; border-radius: 10px; overflow: hidden; }
    .progress-fill { height: 100%; background: linear-gradient(90deg, #1976d2, #42a5f5); transition: width 0.3s ease; }
    .quality-viewers { min-width: 100px; text-align: right; font-size: 14px; color: #666; }
    .timeline-chart { display: flex; align-items: end; gap: 10px; height: 200px; padding: 20px 0; }
    .timeline-bar { display: flex; flex-direction: column; align-items: center; flex: 1; }
    .bar-container { height: 150px; width: 30px; background: #f0f0f0; border-radius: 4px; display: flex; align-items: end; }
    .bar-fill { width: 100%; background: #1976d2; border-radius: 4px; transition: height 0.3s ease; }
    .bar-label { font-size: 12px; color: #666; margin-top: 5px; }
    .bar-value { font-size: 12px; font-weight: bold; color: #333; }
    .analytics-actions { text-align: center; }
    .btn { padding: 10px 20px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: white; }
    .btn-outline { background: white; color: #666; }
  `]
})
export class StreamAnalyticsComponent implements OnInit {
  @Input() tournamentId!: number;
  
  analytics: StreamAnalytics | null = null;

  ngOnInit() {
    this.loadAnalytics();
  }

  loadAnalytics() {
    // Mock data for demonstration
    this.analytics = {
      tournamentId: this.tournamentId,
      totalViews: 15420,
      peakViewers: 2847,
      averageViewTime: 1680, // seconds
      chatMessages: 8934,
      qualityDistribution: [
        { quality: StreamQuality.ULTRA, percentage: 35, viewers: 995 },
        { quality: StreamQuality.HIGH, percentage: 40, viewers: 1139 },
        { quality: StreamQuality.MEDIUM, percentage: 20, viewers: 570 },
        { quality: StreamQuality.LOW, percentage: 5, viewers: 143 }
      ],
      viewersByHour: [
        { hour: '14:00', viewers: 450 },
        { hour: '15:00', viewers: 890 },
        { hour: '16:00', viewers: 1240 },
        { hour: '17:00', viewers: 2847 },
        { hour: '18:00', viewers: 2156 },
        { hour: '19:00', viewers: 1678 },
        { hour: '20:00', viewers: 1234 }
      ]
    };
  }

  refreshAnalytics() {
    this.loadAnalytics();
  }

  getQualityLabel(quality: StreamQuality): string {
    const labels = {
      [StreamQuality.LOW]: 'Baja (480p)',
      [StreamQuality.MEDIUM]: 'Media (720p)',
      [StreamQuality.HIGH]: 'Alta (1080p)',
      [StreamQuality.ULTRA]: 'Ultra (4K)'
    };
    return labels[quality];
  }

  getBarHeight(viewers: number): number {
    const maxViewers = Math.max(...(this.analytics?.viewersByHour.map(s => s.viewers) || [1]));
    return (viewers / maxViewers) * 100;
  }

  formatTime(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
  }
}
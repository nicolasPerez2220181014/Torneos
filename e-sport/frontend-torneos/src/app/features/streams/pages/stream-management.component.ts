import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StreamsService } from '../services/streams.service';
import { StreamStatus, StreamAccess, StreamUrlUpdate } from '../../../core/models/stream.models';

@Component({
  selector: 'app-stream-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="stream-management">
      <h4>Gestión de Stream</h4>

      <!-- Stream Status -->
      <div class="status-section" *ngIf="streamStatus">
        <div class="status-card">
          <div class="status-header">
            <span class="status-label">Estado:</span>
            <span class="status-badge" [class]="streamStatus.isLive ? 'live' : 'offline'">
              {{streamStatus.isLive ? 'En Vivo' : 'Desconectado'}}
            </span>
          </div>
          
          <div class="status-info">
            <div class="info-item">
              <span>Espectadores:</span>
              <strong>{{streamStatus.viewerCount}}</strong>
            </div>
            <div class="info-item">
              <span>Bloqueado:</span>
              <strong [class]="streamStatus.isBlocked ? 'blocked' : 'active'">
                {{streamStatus.isBlocked ? 'Sí' : 'No'}}
              </strong>
            </div>
          </div>
        </div>
      </div>

      <!-- Stream URL Configuration -->
      <div class="url-section">
        <h5>Configurar URL del Stream</h5>
        <div class="url-form">
          <input 
            type="text" 
            [(ngModel)]="streamUrl"
            placeholder="https://stream.example.com/live/tournament"
            class="form-control">
          <button 
            class="btn btn-primary" 
            (click)="updateUrl()"
            [disabled]="!streamUrl || updating">
            {{updating ? 'Actualizando...' : 'Actualizar'}}
          </button>
        </div>
        <small *ngIf="streamStatus?.streamUrl" class="current-url">
          URL actual: {{streamStatus?.streamUrl}}
        </small>
      </div>

      <!-- Stream Controls -->
      <div class="controls-section">
        <h5>Controles del Stream</h5>
        <div class="control-buttons">
          <button 
            class="btn btn-danger" 
            (click)="blockStream()"
            [disabled]="streamStatus?.isBlocked || blocking">
            {{blocking ? 'Bloqueando...' : 'Bloquear Stream'}}
          </button>
          <button 
            class="btn btn-success" 
            (click)="unblockStream()"
            [disabled]="!streamStatus?.isBlocked || unblocking">
            {{unblocking ? 'Desbloqueando...' : 'Desbloquear Stream'}}
          </button>
        </div>
      </div>

      <!-- Access List -->
      <div class="access-section">
        <h5>Accesos Otorgados ({{accesses.length}})</h5>
        
        <div *ngIf="loadingAccesses" class="loading-small">
          Cargando accesos...
        </div>

        <div *ngIf="!loadingAccesses && accesses.length === 0" class="empty-state">
          No hay accesos otorgados
        </div>

        <div *ngIf="!loadingAccesses && accesses.length > 0" class="access-list">
          <div *ngFor="let access of accesses" class="access-item">
            <div class="access-info">
              <strong>{{access.user?.firstName}} {{access.user?.lastName}}</strong>
              <span class="email">{{access.user?.email}}</span>
            </div>
            <div class="access-meta">
              <span class="access-type" [class]="'type-' + access.accessType.toLowerCase()">
                {{access.accessType}}
              </span>
              <span class="access-status" [class]="access.isActive ? 'active' : 'inactive'">
                {{access.isActive ? 'Activo' : 'Inactivo'}}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .stream-management { margin-top: 20px; }
    .stream-management h4 { margin: 0 0 20px 0; }
    .stream-management h5 { margin: 0 0 15px 0; font-size: 16px; }
    .status-section, .url-section, .controls-section, .access-section { background: #f9f9f9; padding: 20px; border-radius: 8px; margin-bottom: 20px; }
    .status-card { background: white; padding: 15px; border-radius: 4px; }
    .status-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
    .status-badge { padding: 4px 12px; border-radius: 4px; font-size: 12px; font-weight: bold; }
    .status-badge.live { background: #e8f5e8; color: #2e7d32; }
    .status-badge.offline { background: #f0f0f0; color: #666; }
    .status-info { display: flex; gap: 30px; }
    .info-item { display: flex; flex-direction: column; gap: 5px; }
    .info-item span { font-size: 14px; color: #666; }
    .info-item .blocked { color: #d32f2f; }
    .info-item .active { color: #2e7d32; }
    .url-form { display: flex; gap: 10px; margin-bottom: 10px; }
    .url-form .form-control { flex: 1; }
    .current-url { color: #666; font-size: 12px; }
    .control-buttons { display: flex; gap: 10px; }
    .access-list { display: flex; flex-direction: column; gap: 10px; }
    .access-item { display: flex; justify-content: space-between; align-items: center; background: white; padding: 15px; border-radius: 4px; }
    .access-info { display: flex; flex-direction: column; gap: 5px; }
    .access-info strong { color: #333; }
    .access-info .email { color: #666; font-size: 14px; }
    .access-meta { display: flex; gap: 10px; align-items: center; }
    .access-type { padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; }
    .access-type.type-free { background: #e3f2fd; color: #1976d2; }
    .access-type.type-paid { background: #fff3e0; color: #f57c00; }
    .access-type.type-vip { background: #f3e5f5; color: #7b1fa2; }
    .access-status { font-size: 12px; }
    .access-status.active { color: #2e7d32; }
    .access-status.inactive { color: #d32f2f; }
    .empty-state { text-align: center; padding: 20px; color: #999; }
    .loading-small { text-align: center; padding: 20px; color: #666; }
    .form-control { padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
    .btn { padding: 8px 16px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: white; }
    .btn-primary { background: #1976d2; color: white; border-color: #1976d2; }
    .btn-success { background: #2e7d32; color: white; border-color: #2e7d32; }
    .btn-danger { background: #d32f2f; color: white; border-color: #d32f2f; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
  `]
})
export class StreamManagementComponent implements OnInit {
  @Input() tournamentId!: number;

  streamStatus: StreamStatus | null = null;
  accesses: StreamAccess[] = [];
  streamUrl = '';
  updating = false;
  blocking = false;
  unblocking = false;
  loadingAccesses = false;

  constructor(private streamsService: StreamsService) {}

  ngOnInit() {
    if (this.tournamentId) {
      this.loadStreamData();
    }
  }

  loadStreamData() {
    this.loadStreamStatus();
    this.loadAccesses();
  }

  loadStreamStatus() {
    this.streamsService.getStreamStatus(this.tournamentId).subscribe({
      next: (status) => {
        this.streamStatus = status;
        if (status.streamUrl) {
          this.streamUrl = status.streamUrl;
        }
      },
      error: (error) => console.error('Error loading stream status:', error)
    });
  }

  loadAccesses() {
    this.loadingAccesses = true;
    this.streamsService.getAccesses(this.tournamentId).subscribe({
      next: (accesses) => {
        this.accesses = accesses;
        this.loadingAccesses = false;
      },
      error: (error) => {
        console.error('Error loading accesses:', error);
        this.loadingAccesses = false;
      }
    });
  }

  updateUrl() {
    if (!this.streamUrl || this.updating) return;

    this.updating = true;
    const update: StreamUrlUpdate = { streamUrl: this.streamUrl };

    this.streamsService.updateStreamUrl(this.tournamentId, update).subscribe({
      next: () => {
        this.updating = false;
        this.loadStreamStatus();
      },
      error: (error) => {
        this.updating = false;
        console.error('Error updating stream URL:', error);
        alert('Error al actualizar la URL del stream');
      }
    });
  }

  blockStream() {
    if (this.blocking) return;

    this.blocking = true;
    this.streamsService.blockStream(this.tournamentId).subscribe({
      next: () => {
        this.blocking = false;
        this.loadStreamStatus();
      },
      error: (error) => {
        this.blocking = false;
        console.error('Error blocking stream:', error);
        alert('Error al bloquear el stream');
      }
    });
  }

  unblockStream() {
    if (this.unblocking) return;

    this.unblocking = true;
    this.streamsService.unblockStream(this.tournamentId).subscribe({
      next: () => {
        this.unblocking = false;
        this.loadStreamStatus();
      },
      error: (error) => {
        this.unblocking = false;
        console.error('Error unblocking stream:', error);
        alert('Error al desbloquear el stream');
      }
    });
  }
}
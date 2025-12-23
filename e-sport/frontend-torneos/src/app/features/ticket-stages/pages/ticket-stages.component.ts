import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TicketStagesService } from '../services/ticket-stages.service';
import { TicketSaleStage, TicketSaleStageRequest, StageStatus } from '../../../core/models/ticket-stage.models';

@Component({
  selector: 'app-ticket-stages',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="ticket-stages">
      <div class="stages-header">
        <h4>Etapas de Venta</h4>
        <button class="btn btn-primary btn-sm" (click)="toggleForm()" *ngIf="!showForm">
          Agregar Etapa
        </button>
      </div>

      <!-- Formulario -->
      <div *ngIf="showForm" class="stage-form">
        <form (ngSubmit)="onSubmit()" #stageForm="ngForm">
          <div class="form-row">
            <div class="form-group">
              <label>Nombre *</label>
              <input 
                type="text" 
                [(ngModel)]="currentStage.name"
                name="name"
                required
                class="form-control"
                placeholder="Ej: Early Bird">
            </div>
            <div class="form-group">
              <label>Precio *</label>
              <input 
                type="number" 
                [(ngModel)]="currentStage.price"
                name="price"
                required
                min="0"
                step="0.01"
                class="form-control"
                placeholder="0.00">
            </div>
          </div>

          <div class="form-group">
            <label>Descripción</label>
            <textarea 
              [(ngModel)]="currentStage.description"
              name="description"
              rows="2"
              class="form-control"
              placeholder="Descripción de la etapa..."></textarea>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Capacidad *</label>
              <input 
                type="number" 
                [(ngModel)]="currentStage.capacity"
                name="capacity"
                required
                min="1"
                class="form-control"
                placeholder="100">
            </div>
            <div class="form-group">
              <label>Fecha Inicio *</label>
              <input 
                type="datetime-local" 
                [(ngModel)]="currentStage.startDate"
                name="startDate"
                required
                class="form-control">
            </div>
            <div class="form-group">
              <label>Fecha Fin *</label>
              <input 
                type="datetime-local" 
                [(ngModel)]="currentStage.endDate"
                name="endDate"
                required
                class="form-control">
            </div>
          </div>

          <div class="form-actions">
            <button type="button" class="btn btn-outline btn-sm" (click)="cancelForm()">
              Cancelar
            </button>
            <button 
              type="submit" 
              class="btn btn-primary btn-sm"
              [disabled]="!stageForm.valid || saving">
              {{saving ? 'Guardando...' : (editingStage ? 'Actualizar' : 'Crear')}}
            </button>
          </div>
        </form>
      </div>

      <!-- Loading -->
      <div *ngIf="loading" class="loading">
        Cargando etapas...
      </div>

      <!-- Lista de etapas -->
      <div *ngIf="!loading && stages.length === 0 && !showForm" class="empty-state">
        No hay etapas de venta configuradas
      </div>

      <div *ngIf="!loading && stages.length > 0" class="stages-list">
        <div *ngFor="let stage of stages" class="stage-item">
          <div class="stage-header">
            <h5>{{stage.name}}</h5>
            <div class="stage-badges">
              <span class="status" [class]="'status-' + getStageStatus(stage).toLowerCase()">
                {{getStatusLabel(getStageStatus(stage))}}
              </span>
              <span class="price">\${{stage.price}}</span>
            </div>
          </div>

          <p class="stage-description" *ngIf="stage.description">{{stage.description}}</p>

          <div class="stage-info">
            <div class="info-row">
              <span><strong>Capacidad:</strong> {{stage.soldTickets}}/{{stage.capacity}}</span>
              <span><strong>Disponibles:</strong> {{stage.capacity - stage.soldTickets}}</span>
            </div>
            <div class="info-row">
              <span><strong>Inicio:</strong> {{formatDateTime(stage.startDate)}}</span>
              <span><strong>Fin:</strong> {{formatDateTime(stage.endDate)}}</span>
            </div>
          </div>

          <div class="progress-bar">
            <div 
              class="progress-fill" 
              [style.width.%]="(stage.soldTickets / stage.capacity) * 100">
            </div>
          </div>

          <div class="stage-actions">
            <button class="btn btn-outline btn-sm" (click)="editStage(stage)">
              Editar
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .ticket-stages { margin-top: 20px; }
    .stages-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
    .stages-header h4 { margin: 0; }
    .stage-form { background: #f9f9f9; padding: 20px; border-radius: 8px; margin-bottom: 20px; }
    .form-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; }
    .form-group { margin-bottom: 15px; }
    .form-group label { display: block; margin-bottom: 5px; font-weight: bold; font-size: 14px; }
    .form-control { width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px; }
    .form-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
    .stages-list { display: flex; flex-direction: column; gap: 15px; }
    .stage-item { border: 1px solid #ddd; border-radius: 8px; padding: 20px; background: white; }
    .stage-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
    .stage-header h5 { margin: 0; }
    .stage-badges { display: flex; gap: 10px; align-items: center; }
    .status { padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; }
    .status-upcoming { background: #e3f2fd; color: #1976d2; }
    .status-active { background: #e8f5e8; color: #2e7d32; }
    .status-expired { background: #f0f0f0; color: #666; }
    .status-sold_out { background: #ffebee; color: #d32f2f; }
    .price { background: #f5f5f5; padding: 4px 8px; border-radius: 4px; font-weight: bold; }
    .stage-description { color: #666; margin-bottom: 15px; font-size: 14px; }
    .stage-info { margin-bottom: 15px; }
    .info-row { display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 14px; }
    .progress-bar { width: 100%; height: 8px; background: #f0f0f0; border-radius: 4px; margin-bottom: 15px; overflow: hidden; }
    .progress-fill { height: 100%; background: #2e7d32; transition: width 0.3s ease; }
    .stage-actions { display: flex; gap: 10px; }
    .empty-state { text-align: center; padding: 40px; color: #999; }
    .loading { text-align: center; padding: 20px; color: #666; }
    .btn { padding: 6px 12px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: white; }
    .btn-primary { background: #1976d2; color: white; border-color: #1976d2; }
    .btn-outline { background: white; color: #666; }
    .btn-sm { padding: 4px 8px; font-size: 12px; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
  `]
})
export class TicketStagesComponent implements OnInit {
  @Input() tournamentId!: number;

  stages: TicketSaleStage[] = [];
  loading = false;
  saving = false;
  showForm = false;
  editingStage: TicketSaleStage | null = null;

  currentStage: TicketSaleStageRequest = {
    name: '',
    description: '',
    price: 0,
    capacity: 100,
    startDate: '',
    endDate: ''
  };

  constructor(private ticketStagesService: TicketStagesService) {}

  ngOnInit() {
    if (this.tournamentId) {
      this.loadStages();
    }
  }

  loadStages() {
    this.loading = true;
    this.ticketStagesService.getStages(this.tournamentId).subscribe({
      next: (stages) => {
        this.stages = stages;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading stages:', error);
        this.loading = false;
      }
    });
  }

  toggleForm() {
    this.showForm = !this.showForm;
    if (!this.showForm) {
      this.resetForm();
    }
  }

  editStage(stage: TicketSaleStage) {
    this.editingStage = stage;
    this.currentStage = {
      name: stage.name,
      description: stage.description,
      price: stage.price,
      capacity: stage.capacity,
      startDate: this.formatDateForInput(stage.startDate),
      endDate: this.formatDateForInput(stage.endDate)
    };
    this.showForm = true;
  }

  onSubmit() {
    if (this.saving) return;

    this.saving = true;
    const operation = this.editingStage
      ? this.ticketStagesService.updateStage(this.tournamentId, this.editingStage.id, this.currentStage)
      : this.ticketStagesService.createStage(this.tournamentId, this.currentStage);

    operation.subscribe({
      next: () => {
        this.saving = false;
        this.resetForm();
        this.loadStages();
      },
      error: (error) => {
        console.error('Error saving stage:', error);
        this.saving = false;
      }
    });
  }

  cancelForm() {
    this.resetForm();
  }

  resetForm() {
    this.showForm = false;
    this.editingStage = null;
    this.currentStage = {
      name: '',
      description: '',
      price: 0,
      capacity: 100,
      startDate: '',
      endDate: ''
    };
  }

  getStageStatus(stage: TicketSaleStage): StageStatus {
    const now = new Date();
    const start = new Date(stage.startDate);
    const end = new Date(stage.endDate);

    if (stage.soldTickets >= stage.capacity) {
      return StageStatus.SOLD_OUT;
    }
    if (now < start) {
      return StageStatus.UPCOMING;
    }
    if (now > end) {
      return StageStatus.EXPIRED;
    }
    return StageStatus.ACTIVE;
  }

  getStatusLabel(status: StageStatus): string {
    const labels = {
      [StageStatus.UPCOMING]: 'Próximamente',
      [StageStatus.ACTIVE]: 'Activa',
      [StageStatus.EXPIRED]: 'Expirada',
      [StageStatus.SOLD_OUT]: 'Agotada'
    };
    return labels[status];
  }

  formatDateTime(dateString: string): string {
    return new Date(dateString).toLocaleString('es-ES');
  }

  private formatDateForInput(dateString: string): string {
    return new Date(dateString).toISOString().slice(0, 16);
  }
}
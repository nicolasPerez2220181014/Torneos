import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TicketsService } from '../../tickets/services/tickets.service';
import { Ticket, TicketStatus } from '../../../core/models/ticket.models';

@Component({
  selector: 'app-ticket-lookup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container">
      <div class="header">
        <h2>Consultar Ticket</h2>
        <p>Ingresa un código de acceso para ver los detalles del ticket</p>
      </div>

      <div class="lookup-section">
        <div class="input-section">
          <input 
            type="text" 
            [(ngModel)]="accessCode"
            (keyup.enter)="lookupTicket()"
            placeholder="Código de acceso del ticket"
            class="code-input">
          
          <button 
            class="btn btn-primary" 
            (click)="lookupTicket()"
            [disabled]="!accessCode || loading">
            {{loading ? 'Buscando...' : 'Buscar'}}
          </button>
        </div>
      </div>

      <div *ngIf="error" class="error-message">
        {{error}}
      </div>

      <div *ngIf="ticket" class="ticket-details">
        <div class="ticket-card">
          <div class="ticket-header">
            <h3>Detalles del Ticket</h3>
            <span class="status" [class]="'status-' + ticket.status.toLowerCase()">
              {{getStatusLabel(ticket.status)}}
            </span>
          </div>

          <div class="ticket-info">
            <div class="info-grid">
              <div class="info-item">
                <label>Código de Acceso:</label>
                <span class="code">{{ticket.accessCode}}</span>
              </div>
              
              <div class="info-item">
                <label>Precio:</label>
                <span class="price">\${{ticket.price}}</span>
              </div>
              
              <div class="info-item">
                <label>Estado:</label>
                <span class="status-text">{{getStatusLabel(ticket.status)}}</span>
              </div>
              
              <div class="info-item">
                <label>Creado:</label>
                <span>{{formatDateTime(ticket.createdAt)}}</span>
              </div>
              
              <div class="info-item" *ngIf="ticket.validatedAt">
                <label>Validado:</label>
                <span>{{formatDateTime(ticket.validatedAt)}}</span>
              </div>
              
              <div class="info-item">
                <label>¿Validado?:</label>
                <span [class]="ticket.isValidated ? 'validated' : 'not-validated'">
                  {{ticket.isValidated ? 'Sí' : 'No'}}
                </span>
              </div>
            </div>
          </div>

          <div class="ticket-qr">
            <div class="qr-placeholder">
              <div class="qr-code">QR</div>
              <small>{{ticket.accessCode}}</small>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
    .header { text-align: center; margin-bottom: 30px; }
    .header p { color: #666; margin-top: 10px; }
    .lookup-section { background: white; padding: 20px; border-radius: 8px; margin-bottom: 30px; border: 1px solid #ddd; }
    .input-section { display: flex; gap: 10px; }
    .code-input { flex: 1; padding: 12px; border: 2px solid #ddd; border-radius: 4px; font-size: 16px; font-family: monospace; }
    .code-input:focus { outline: none; border-color: #1976d2; }
    .error-message { background: #ffebee; color: #d32f2f; padding: 15px; border-radius: 4px; margin-bottom: 20px; text-align: center; }
    .ticket-details { background: white; border-radius: 8px; border: 1px solid #ddd; overflow: hidden; }
    .ticket-card { padding: 0; }
    .ticket-header { display: flex; justify-content: space-between; align-items: center; padding: 20px; border-bottom: 1px solid #eee; background: #f9f9f9; }
    .ticket-header h3 { margin: 0; }
    .status { padding: 6px 12px; border-radius: 4px; font-size: 12px; font-weight: bold; }
    .status-active { background: #e8f5e8; color: #2e7d32; }
    .status-used { background: #f0f0f0; color: #666; }
    .status-cancelled { background: #ffebee; color: #d32f2f; }
    .status-expired { background: #fff3e0; color: #f57c00; }
    .ticket-info { padding: 20px; }
    .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
    .info-item { display: flex; flex-direction: column; gap: 5px; }
    .info-item label { font-weight: bold; color: #666; font-size: 14px; }
    .info-item .code { font-family: monospace; font-weight: bold; font-size: 16px; }
    .info-item .price { font-weight: bold; color: #2e7d32; font-size: 16px; }
    .validated { color: #2e7d32; font-weight: bold; }
    .not-validated { color: #d32f2f; }
    .ticket-qr { padding: 20px; text-align: center; border-top: 1px solid #eee; background: #f9f9f9; }
    .qr-placeholder { display: inline-block; }
    .qr-code { width: 80px; height: 80px; border: 2px dashed #ddd; display: flex; align-items: center; justify-content: center; margin: 0 auto 10px; border-radius: 8px; background: white; font-weight: bold; color: #666; }
    .qr-placeholder small { font-family: monospace; color: #999; }
    .btn { padding: 10px 20px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: white; }
    .btn-primary { background: #1976d2; color: white; border-color: #1976d2; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
    @media (max-width: 768px) {
      .info-grid { grid-template-columns: 1fr; }
    }
  `]
})
export class TicketLookupComponent {
  accessCode = '';
  ticket: Ticket | null = null;
  loading = false;
  error: string | null = null;

  constructor(private ticketsService: TicketsService) {}

  lookupTicket() {
    if (!this.accessCode.trim() || this.loading) return;

    this.loading = true;
    this.error = null;
    this.ticket = null;

    this.ticketsService.getTicketByCode(this.accessCode.trim()).subscribe({
      next: (ticket) => {
        this.ticket = ticket;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Ticket no encontrado o código inválido';
        this.loading = false;
        console.error('Error looking up ticket:', error);
      }
    });
  }

  getStatusLabel(status: TicketStatus): string {
    const labels = {
      [TicketStatus.ACTIVE]: 'Activo',
      [TicketStatus.USED]: 'Usado',
      [TicketStatus.CANCELLED]: 'Cancelado',
      [TicketStatus.EXPIRED]: 'Expirado'
    };
    return labels[status] || status;
  }

  formatDateTime(dateString: string): string {
    return new Date(dateString).toLocaleString('es-ES');
  }
}
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { TicketsService } from '../../tickets/services/tickets.service';
import { Ticket, TicketStatus } from '../../../core/models/ticket.models';

@Component({
  selector: 'app-ticket-validation',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container">
      <div class="header">
        <h2>Validación de Tickets</h2>
        <div class="tournament-info" *ngIf="tournamentId">
          <span>Torneo ID: {{tournamentId}}</span>
        </div>
      </div>

      <!-- Scanner Manual -->
      <div class="scanner-section">
        <h3>Escanear Código de Acceso</h3>
        
        <div class="input-section">
          <input 
            type="text" 
            [(ngModel)]="accessCode"
            (keyup.enter)="validateTicket()"
            placeholder="Ingresa o escanea el código de acceso"
            class="code-input"
            #codeInput>
          
          <button 
            class="btn btn-primary" 
            (click)="validateTicket()"
            [disabled]="!accessCode || validating">
            {{validating ? 'Validando...' : 'Validar'}}
          </button>
        </div>

        <div class="scanner-help">
          <p>• Escanea el código QR del ticket</p>
          <p>• O ingresa manualmente el código de acceso</p>
          <p>• Presiona Enter para validar rápidamente</p>
        </div>
      </div>

      <!-- Resultado de Validación -->
      <div *ngIf="validationResult" class="validation-result">
        <div class="result-card" [class]="validationResult.isValid ? 'valid' : 'invalid'">
          <div class="result-icon">
            <span *ngIf="validationResult.isValid">✓</span>
            <span *ngIf="!validationResult.isValid">✗</span>
          </div>
          
          <div class="result-content">
            <h4>{{validationResult.message}}</h4>
            
            <div *ngIf="validationResult.ticket" class="ticket-details">
              <div class="detail-row">
                <span>Código:</span>
                <span class="code">{{validationResult.ticket.accessCode}}</span>
              </div>
              <div class="detail-row">
                <span>Precio:</span>
                <span>\${{validationResult.ticket.price}}</span>
              </div>
              <div class="detail-row">
                <span>Estado:</span>
                <span class="status" [class]="'status-' + validationResult.ticket.status.toLowerCase()">
                  {{getStatusLabel(validationResult.ticket.status)}}
                </span>
              </div>
              <div class="detail-row" *ngIf="validationResult.ticket.validatedAt">
                <span>Validado:</span>
                <span>{{formatDateTime(validationResult.ticket.validatedAt)}}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Error -->
      <div *ngIf="error" class="error-message">
        {{error}}
      </div>

      <!-- Historial de Validaciones -->
      <div class="validation-history">
        <h3>Historial de Validaciones</h3>
        
        <div *ngIf="validationHistory.length === 0" class="empty-history">
          No hay validaciones registradas
        </div>

        <div *ngFor="let validation of validationHistory" class="history-item">
          <div class="history-icon" [class]="validation.isValid ? 'valid' : 'invalid'">
            <span *ngIf="validation.isValid">✓</span>
            <span *ngIf="!validation.isValid">✗</span>
          </div>
          
          <div class="history-content">
            <div class="history-code">{{validation.accessCode}}</div>
            <div class="history-message">{{validation.message}}</div>
            <div class="history-time">{{formatTime(validation.timestamp)}}</div>
          </div>
        </div>
      </div>

      <!-- Acciones -->
      <div class="actions">
        <button class="btn btn-outline" (click)="clearHistory()">
          Limpiar Historial
        </button>
        <button class="btn btn-outline" (click)="focusInput()">
          Nuevo Escaneo
        </button>
      </div>
    </div>
  `,
  styles: [`
    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
    .header { text-align: center; margin-bottom: 30px; }
    .tournament-info { background: #e3f2fd; padding: 8px 16px; border-radius: 4px; display: inline-block; margin-top: 10px; }
    .scanner-section { background: white; padding: 20px; border-radius: 8px; margin-bottom: 30px; border: 1px solid #ddd; }
    .scanner-section h3 { margin: 0 0 20px 0; }
    .input-section { display: flex; gap: 10px; margin-bottom: 15px; }
    .code-input { flex: 1; padding: 12px; border: 2px solid #ddd; border-radius: 4px; font-size: 16px; font-family: monospace; }
    .code-input:focus { outline: none; border-color: #1976d2; }
    .scanner-help { font-size: 14px; color: #666; }
    .scanner-help p { margin: 5px 0; }
    .validation-result { margin-bottom: 30px; }
    .result-card { display: flex; align-items: flex-start; gap: 20px; padding: 20px; border-radius: 8px; }
    .result-card.valid { background: #e8f5e8; border: 2px solid #2e7d32; }
    .result-card.invalid { background: #ffebee; border: 2px solid #d32f2f; }
    .result-icon { width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 20px; font-weight: bold; }
    .result-card.valid .result-icon { background: #2e7d32; color: white; }
    .result-card.invalid .result-icon { background: #d32f2f; color: white; }
    .result-content { flex: 1; }
    .result-content h4 { margin: 0 0 15px 0; }
    .ticket-details { display: flex; flex-direction: column; gap: 8px; }
    .detail-row { display: flex; justify-content: space-between; }
    .detail-row .code { font-family: monospace; font-weight: bold; }
    .status { padding: 2px 6px; border-radius: 3px; font-size: 12px; }
    .status-active { background: #e8f5e8; color: #2e7d32; }
    .status-used { background: #f0f0f0; color: #666; }
    .validation-history { background: white; padding: 20px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #ddd; }
    .validation-history h3 { margin: 0 0 20px 0; }
    .empty-history { text-align: center; color: #999; padding: 20px; }
    .history-item { display: flex; align-items: center; gap: 15px; padding: 10px; border-bottom: 1px solid #eee; }
    .history-item:last-child { border-bottom: none; }
    .history-icon { width: 24px; height: 24px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 12px; }
    .history-icon.valid { background: #2e7d32; color: white; }
    .history-icon.invalid { background: #d32f2f; color: white; }
    .history-content { flex: 1; }
    .history-code { font-family: monospace; font-weight: bold; }
    .history-message { font-size: 14px; color: #666; }
    .history-time { font-size: 12px; color: #999; }
    .actions { display: flex; justify-content: center; gap: 15px; }
    .error-message { background: #ffebee; color: #d32f2f; padding: 15px; border-radius: 4px; margin-bottom: 20px; text-align: center; }
    .btn { padding: 10px 20px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: white; }
    .btn-primary { background: #1976d2; color: white; border-color: #1976d2; }
    .btn-outline { background: white; color: #666; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
  `]
})
export class TicketValidationComponent implements OnInit {
  tournamentId: number | null = null;
  accessCode = '';
  validating = false;
  error: string | null = null;
  
  validationResult: {
    isValid: boolean;
    message: string;
    ticket?: Ticket;
  } | null = null;

  validationHistory: {
    accessCode: string;
    isValid: boolean;
    message: string;
    timestamp: Date;
  }[] = [];

  constructor(
    private route: ActivatedRoute,
    private ticketsService: TicketsService
  ) {}

  ngOnInit() {
    const tournamentId = this.route.snapshot.paramMap.get('tournamentId');
    if (tournamentId) {
      this.tournamentId = Number(tournamentId);
    }
    
    // Verificar si hay un código en los query params
    const code = this.route.snapshot.queryParamMap.get('code');
    if (code) {
      this.accessCode = code;
      setTimeout(() => this.validateTicket(), 500);
    }
    
    // Focus input on load
    setTimeout(() => this.focusInput(), 100);
  }

  validateTicket() {
    if (!this.accessCode.trim() || this.validating) return;

    this.validating = true;
    this.error = null;
    this.validationResult = null;

    // Usar fetch para llamar al endpoint simple
    fetch('http://localhost:8081/api/simple/tickets/validate', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ accessCode: this.accessCode.trim() })
    })
    .then(response => response.json())
    .then(data => {
      this.validating = false;
      
      if (data.valid) {
        this.validationResult = {
          isValid: true,
          message: data.message,
          ticket: {
            accessCode: data.ticket.access_code,
            status: data.ticket.status === 'ISSUED' ? 'ACTIVE' : data.ticket.status,
            price: 0, // No tenemos precio en la respuesta
            validatedAt: new Date().toISOString()
          } as any
        };
        
        this.addToHistory(this.accessCode, true, data.message);
      } else {
        this.validationResult = {
          isValid: false,
          message: data.message,
          ticket: data.ticket ? {
            accessCode: data.ticket.access_code,
            status: data.ticket.status,
            price: 0
          } as any : undefined
        };
        
        this.addToHistory(this.accessCode, false, data.message);
      }
      
      this.clearInput();
    })
    .catch(error => {
      this.validating = false;
      this.error = 'Error al validar el ticket';
      console.error('Error:', error);
      this.addToHistory(this.accessCode, false, 'Error de conexión');
      this.clearInput();
    });
  }

  addToHistory(code: string, isValid: boolean, message: string) {
    this.validationHistory.unshift({
      accessCode: code,
      isValid: isValid,
      message: message,
      timestamp: new Date()
    });
    
    // Keep only last 20 validations
    if (this.validationHistory.length > 20) {
      this.validationHistory = this.validationHistory.slice(0, 20);
    }
  }

  clearHistory() {
    this.validationHistory = [];
  }

  clearInput() {
    this.accessCode = '';
    setTimeout(() => this.focusInput(), 100);
  }

  focusInput() {
    const input = document.querySelector('.code-input') as HTMLInputElement;
    if (input) {
      input.focus();
    }
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

  formatTime(date: Date): string {
    return date.toLocaleTimeString('es-ES');
  }
}
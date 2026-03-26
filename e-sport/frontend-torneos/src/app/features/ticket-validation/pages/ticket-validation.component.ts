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
    .container { max-width: 600px; }
    .header { text-align: center; margin-bottom: var(--sp-2xl); }
    .header h2 { font-size: 1.4rem; }
    .tournament-info {
      background: var(--info-soft);
      color: var(--info);
      padding: 6px 14px;
      border-radius: var(--radius-full);
      display: inline-block;
      margin-top: var(--sp-sm);
      font-size: 0.8rem;
    }

    .scanner-section {
      background: var(--bg-card);
      border: 1px solid var(--border);
      padding: var(--sp-xl);
      border-radius: var(--radius-lg);
      margin-bottom: var(--sp-2xl);
    }
    .scanner-section h3 { margin: 0 0 var(--sp-lg) 0; font-size: 1.05rem; }
    .input-section { display: flex; gap: var(--sp-sm); margin-bottom: var(--sp-lg); }
    .code-input {
      flex: 1;
      padding: 12px 14px;
      background: var(--bg-input);
      border: 1px solid var(--border);
      border-radius: var(--radius-sm);
      color: var(--text-primary);
      font-size: 0.95rem;
      font-family: monospace;
      outline: none;
      transition: border-color 0.15s;
    }
    .code-input:focus { border-color: var(--accent); box-shadow: 0 0 0 3px var(--accent-soft); }
    .scanner-help { font-size: 0.8rem; color: var(--text-muted); }
    .scanner-help p { margin: 4px 0; }

    .validation-result { margin-bottom: var(--sp-2xl); }
    .result-card {
      display: flex;
      align-items: flex-start;
      gap: var(--sp-lg);
      padding: var(--sp-xl);
      border-radius: var(--radius-lg);
      border: 1px solid;
    }
    .result-card.valid { background: var(--success-soft); border-color: rgba(0,184,148,0.3); }
    .result-card.invalid { background: var(--danger-soft); border-color: rgba(231,76,60,0.3); }
    .result-icon {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1rem;
      font-weight: 700;
      flex-shrink: 0;
    }
    .result-card.valid .result-icon { background: var(--success); color: #fff; }
    .result-card.invalid .result-icon { background: var(--danger); color: #fff; }
    .result-content { flex: 1; }
    .result-content h4 { margin: 0 0 var(--sp-md) 0; font-size: 0.95rem; color: var(--text-primary); }
    .ticket-details { display: flex; flex-direction: column; gap: var(--sp-sm); }
    .detail-row { display: flex; justify-content: space-between; font-size: 0.8rem; color: var(--text-secondary); }
    .detail-row .code { font-family: monospace; font-weight: 600; color: var(--text-primary); }

    .validation-history {
      background: var(--bg-card);
      border: 1px solid var(--border);
      padding: var(--sp-xl);
      border-radius: var(--radius-lg);
      margin-bottom: var(--sp-xl);
    }
    .validation-history h3 { margin: 0 0 var(--sp-lg) 0; font-size: 1.05rem; }
    .empty-history { text-align: center; color: var(--text-muted); padding: var(--sp-xl); font-size: 0.85rem; }
    .history-item {
      display: flex;
      align-items: center;
      gap: var(--sp-md);
      padding: var(--sp-md) 0;
      border-bottom: 1px solid var(--border);
    }
    .history-item:last-child { border-bottom: none; }
    .history-icon {
      width: 22px;
      height: 22px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 0.65rem;
      font-weight: 700;
      flex-shrink: 0;
    }
    .history-icon.valid { background: var(--success); color: #fff; }
    .history-icon.invalid { background: var(--danger); color: #fff; }
    .history-content { flex: 1; }
    .history-code { font-family: monospace; font-weight: 600; font-size: 0.8rem; color: var(--text-primary); }
    .history-message { font-size: 0.75rem; color: var(--text-muted); }
    .history-time { font-size: 0.7rem; color: var(--text-muted); }

    .actions { display: flex; justify-content: center; gap: var(--sp-md); }
    .error-message {
      background: var(--danger-soft);
      color: var(--danger);
      padding: var(--sp-lg);
      border-radius: var(--radius-md);
      margin-bottom: var(--sp-xl);
      text-align: center;
      font-size: 0.85rem;
      border: 1px solid rgba(231,76,60,0.2);
    }
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
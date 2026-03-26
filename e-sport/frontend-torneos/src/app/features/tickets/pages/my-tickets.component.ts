import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-my-tickets',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container">
      <div class="header">
        <h2>Mis Tickets</h2>
        <button class="btn btn-outline" (click)="goBack()">Volver a Torneos</button>
      </div>

      <div *ngIf="tickets.length === 0" class="no-tickets">
        <p>No tienes tickets comprados aún</p>
        <button class="btn btn-primary" (click)="goToTournaments()">Ver Torneos Disponibles</button>
      </div>

      <div *ngIf="tickets.length > 0" class="tickets-grid">
        <div *ngFor="let ticket of tickets" class="ticket-card">
          <div class="ticket-header">
            <h3>{{ticket.tournamentName}}</h3>
            <span class="status">{{getStatusLabel(ticket.status)}}</span>
          </div>
          
          <div class="ticket-details">
            <div class="detail-item">
              <strong>Código:</strong> {{ticket.accessCode}}
              <button class="btn-copy" (click)="copyCode(ticket.accessCode)"></button>
            </div>
            <div class="detail-item">
              <strong>Precio:</strong> {{ticket.price | currency:'USD':'symbol':'1.2-2'}}
            </div>
            <div class="detail-item">
              <strong>Fecha:</strong> {{formatDate(ticket.purchaseDate)}}
            </div>
          </div>

          <div class="ticket-actions">
            <button class="btn btn-sm btn-primary" (click)="validateTicket(ticket.accessCode)">Validar</button>
            <button class="btn btn-sm btn-outline" (click)="copyCode(ticket.accessCode)">Copiar</button>
          </div>
        </div>
      </div>

      <div class="summary" *ngIf="tickets.length > 0">
        <h4>Resumen</h4>
        <p>Total tickets: {{getTotalTickets()}}</p>
        <p>Total gastado: {{getTotalSpent() | currency:'USD':'symbol':'1.2-2'}}</p>
      </div>
    </div>
  `,
  styles: [`
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--sp-xl); }
    .header h2 { font-size: 1.4rem; }
    .no-tickets { text-align: center; padding: var(--sp-3xl); color: var(--text-muted); }
    .no-tickets p { margin-bottom: var(--sp-lg); }
    .tickets-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(380px, 1fr)); gap: var(--sp-lg); }
    .ticket-card {
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: var(--radius-lg);
      padding: var(--sp-xl);
      transition: all 0.2s var(--ease);
    }
    .ticket-card:hover { border-color: var(--border-hover); box-shadow: var(--shadow-md); }
    .ticket-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--sp-lg); }
    .ticket-header h3 { margin: 0; font-size: 1.05rem; color: var(--text-primary); }
    .status { padding: 3px 10px; border-radius: var(--radius-full); font-size: 0.7rem; font-weight: 600; text-transform: uppercase; background: var(--success-soft); color: var(--success); }
    .ticket-details { margin-bottom: var(--sp-lg); }
    .detail-item { margin-bottom: var(--sp-sm); display: flex; justify-content: space-between; align-items: center; font-size: 0.8rem; color: var(--text-secondary); }
    .detail-item strong { color: var(--text-muted); font-weight: 500; }
    .btn-copy { background: none; border: none; cursor: pointer; color: var(--accent); font-size: 0.8rem; }
    .ticket-actions { display: flex; gap: var(--sp-sm); padding-top: var(--sp-lg); border-top: 1px solid var(--border); }
    .summary {
      background: var(--bg-card);
      border: 1px solid var(--border);
      padding: var(--sp-xl);
      border-radius: var(--radius-lg);
      margin-top: var(--sp-xl);
    }
    .summary h4 { margin: 0 0 var(--sp-md) 0; font-size: 1rem; }
    .summary p { margin: var(--sp-xs) 0; font-size: 0.85rem; color: var(--text-secondary); }
    @media (max-width: 768px) { .tickets-grid { grid-template-columns: 1fr; } }
  `]
})
export class MyTicketsComponent implements OnInit {
  tickets: any[] = [];

  constructor(private router: Router) {}

  ngOnInit() {
    this.loadTickets();
  }

  loadTickets() {
    const storedTickets = localStorage.getItem('purchasedTickets');
    if (storedTickets) {
      this.tickets = JSON.parse(storedTickets);
    }
  }

  getTotalTickets(): number {
    return this.tickets.length;
  }

  getTotalSpent(): number {
    return this.tickets.reduce((sum, ticket) => sum + ticket.total, 0);
  }

  getStatusLabel(status: string): string {
    const labels = {
      'ACTIVE': 'Activo',
      'USED': 'Usado', 
      'EXPIRED': 'Expirado'
    };
    return labels[status as keyof typeof labels] || status;
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('es-ES');
  }

  copyCode(code: string) {
    navigator.clipboard.writeText(code).then(() => {
      alert('Código copiado al portapapeles');
    });
  }

  validateTicket(code: string) {
    this.router.navigate(['/validation'], { queryParams: { code: code } });
  }

  goBack() {
    this.router.navigate(['/tournaments']);
  }

  goToTournaments() {
    this.router.navigate(['/tournaments']);
  }
}
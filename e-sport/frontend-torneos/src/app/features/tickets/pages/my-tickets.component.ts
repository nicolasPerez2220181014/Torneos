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
    .container { max-width: 1200px; margin: 0 auto; padding: 20px; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
    .no-tickets { text-align: center; padding: 40px; }
    .tickets-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(400px, 1fr)); gap: 20px; }
    .ticket-card { border: 1px solid #ddd; border-radius: 8px; padding: 20px; background: white; }
    .ticket-header { display: flex; justify-content: space-between; margin-bottom: 15px; }
    .status { padding: 4px 8px; border-radius: 4px; font-size: 12px; background: #e8f5e8; color: #2e7d32; }
    .ticket-details { margin-bottom: 15px; }
    .detail-item { margin-bottom: 8px; display: flex; justify-content: space-between; align-items: center; }
    .btn-copy { background: none; border: none; cursor: pointer; }
    .ticket-actions { display: flex; gap: 10px; }
    .summary { background: #f5f5f5; padding: 20px; border-radius: 8px; margin-top: 20px; }
    .btn { padding: 8px 16px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: white; }
    .btn-primary { background: #1976d2; color: white; border-color: #1976d2; }
    .btn-outline { background: white; }
    .btn-sm { padding: 4px 8px; font-size: 12px; }
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
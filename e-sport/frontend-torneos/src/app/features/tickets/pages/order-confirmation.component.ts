import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TicketsService } from '../services/tickets.service';
import { TicketOrder, OrderStatus, TicketStatus } from '../../../core/models/ticket.models';

@Component({
  selector: 'app-order-confirmation',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container">
      <div class="header">
        <h2>Confirmación de Compra</h2>
        <button class="btn btn-outline" (click)="goToTournaments()">
          Volver a Torneos
        </button>
      </div>

      <div *ngIf="loading" class="loading">
        Cargando orden...
      </div>

      <div *ngIf="error" class="error">
        {{error}}
      </div>

      <div *ngIf="order && !loading" class="confirmation-container">
        <!-- Estado de la Orden -->
        <div class="order-status">
          <div class="status-icon" [class]="'status-' + order.status.toLowerCase()">
            <span *ngIf="order.status === 'CONFIRMED'">✓</span>
            <span *ngIf="order.status === 'PENDING'"></span>
            <span *ngIf="order.status === 'CANCELLED'">✗</span>
          </div>
          <div class="status-text">
            <h3>{{getStatusMessage()}}</h3>
            <p>{{getStatusDescription()}}</p>
          </div>
        </div>

        <!-- Detalles de la Orden -->
        <div class="order-details">
          <h4>Detalles de la Orden</h4>
          <div class="detail-grid">
            <div class="detail-item">
              <label>Número de Orden:</label>
              <span>#{{order.id}}</span>
            </div>
            <div class="detail-item">
              <label>Fecha de Compra:</label>
              <span>{{formatDateTime(order.createdAt)}}</span>
            </div>
            <div class="detail-item">
              <label>Cantidad de Tickets:</label>
              <span>{{order.quantity}}</span>
            </div>
            <div class="detail-item">
              <label>Total Pagado:</label>
              <span class="amount">\${{order.totalAmount}}</span>
            </div>
          </div>
        </div>

        <!-- Tickets -->
        <div class="tickets-section" *ngIf="order.tickets && order.tickets.length > 0">
          <h4>Tus Tickets</h4>
          <div class="tickets-grid">
            <div *ngFor="let ticket of order.tickets" class="ticket-card">
              <div class="ticket-header">
                <span class="ticket-code">{{ticket.accessCode}}</span>
                <span class="ticket-status" [class]="'status-' + ticket.status.toLowerCase()">
                  {{getTicketStatusLabel(ticket.status)}}
                </span>
              </div>
              
              <div class="ticket-info">
                <div class="info-row">
                  <span>Precio: \${{ticket.price}}</span>
                  <span *ngIf="ticket.isValidated">Validado: {{formatDateTime(ticket.validatedAt!)}}</span>
                </div>
              </div>

              <div class="ticket-qr">
                <div class="qr-placeholder">
                  QR Code
                  <small>{{ticket.accessCode}}</small>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Instrucciones -->
        <div class="instructions">
          <h4>Instrucciones Importantes</h4>
          <ul>
            <li>Guarda estos códigos de acceso en un lugar seguro</li>
            <li>Presenta el código QR o el código de acceso en el evento</li>
            <li>Los tickets son válidos únicamente para la fecha del torneo</li>
            <li>No compartas tus códigos de acceso con terceros</li>
          </ul>
        </div>

        <!-- Acciones -->
        <div class="actions">
          <button class="btn btn-outline" (click)="printTickets()">
            Imprimir Tickets
          </button>
          <button class="btn btn-primary" (click)="goToTournaments()">
            Ver Más Torneos
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .container { max-width: 800px; margin: 0 auto; padding: 20px; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
    .confirmation-container { display: flex; flex-direction: column; gap: 30px; }
    .order-status { display: flex; align-items: center; gap: 20px; background: white; padding: 30px; border-radius: 8px; border: 1px solid #ddd; }
    .status-icon { width: 60px; height: 60px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 24px; font-weight: bold; }
    .status-icon.status-confirmed { background: #e8f5e8; color: #2e7d32; }
    .status-icon.status-pending { background: #fff3e0; color: #f57c00; }
    .status-icon.status-cancelled { background: #ffebee; color: #d32f2f; }
    .status-text h3 { margin: 0 0 10px 0; }
    .status-text p { margin: 0; color: #666; }
    .order-details { background: white; padding: 20px; border-radius: 8px; border: 1px solid #ddd; }
    .order-details h4 { margin: 0 0 20px 0; }
    .detail-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; }
    .detail-item { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #eee; }
    .detail-item label { font-weight: bold; color: #666; }
    .amount { font-weight: bold; color: #2e7d32; font-size: 18px; }
    .tickets-section { background: white; padding: 20px; border-radius: 8px; border: 1px solid #ddd; }
    .tickets-section h4 { margin: 0 0 20px 0; }
    .tickets-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; }
    .ticket-card { border: 1px solid #ddd; border-radius: 8px; padding: 20px; background: #f9f9f9; }
    .ticket-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; }
    .ticket-code { font-family: monospace; font-weight: bold; font-size: 16px; }
    .ticket-status { padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; }
    .ticket-status.status-active { background: #e8f5e8; color: #2e7d32; }
    .ticket-status.status-used { background: #f0f0f0; color: #666; }
    .ticket-info { margin-bottom: 15px; }
    .info-row { display: flex; justify-content: space-between; font-size: 14px; color: #666; }
    .ticket-qr { text-align: center; }
    .qr-placeholder { width: 100px; height: 100px; border: 2px dashed #ddd; display: flex; flex-direction: column; align-items: center; justify-content: center; margin: 0 auto; border-radius: 8px; background: white; }
    .qr-placeholder small { font-size: 10px; color: #999; margin-top: 5px; }
    .instructions { background: #f9f9f9; padding: 20px; border-radius: 8px; }
    .instructions h4 { margin: 0 0 15px 0; }
    .instructions ul { margin: 0; padding-left: 20px; }
    .instructions li { margin-bottom: 8px; color: #666; }
    .actions { display: flex; justify-content: center; gap: 20px; }
    .loading, .error { text-align: center; padding: 40px; }
    .error { color: #d32f2f; }
    .btn { padding: 12px 24px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: white; }
    .btn-primary { background: #1976d2; color: white; border-color: #1976d2; }
    .btn-outline { background: white; color: #666; }
    @media (max-width: 768px) {
      .detail-grid { grid-template-columns: 1fr; }
      .order-status { flex-direction: column; text-align: center; }
      .actions { flex-direction: column; }
    }
  `]
})
export class OrderConfirmationComponent implements OnInit {
  order: TicketOrder | null = null;
  loading = false;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private ticketsService: TicketsService
  ) {}

  ngOnInit() {
    const orderId = Number(this.route.snapshot.paramMap.get('orderId'));
    if (orderId && !isNaN(orderId)) {
      this.loadOrder(orderId);
    }
  }

  loadOrder(orderId: number) {
    this.loading = true;
    this.ticketsService.getOrder(orderId).subscribe({
      next: (order) => {
        this.order = order;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Error al cargar la orden';
        this.loading = false;
        console.error('Error loading order:', error);
      }
    });
  }

  getStatusMessage(): string {
    if (!this.order) return '';
    
    const messages = {
      [OrderStatus.CONFIRMED]: '¡Compra Exitosa!',
      [OrderStatus.PENDING]: 'Procesando Pago',
      [OrderStatus.CANCELLED]: 'Compra Cancelada',
      [OrderStatus.REFUNDED]: 'Compra Reembolsada'
    };
    
    return messages[this.order.status] || 'Estado Desconocido';
  }

  getStatusDescription(): string {
    if (!this.order) return '';
    
    const descriptions = {
      [OrderStatus.CONFIRMED]: 'Tu compra ha sido procesada exitosamente. Puedes usar tus tickets para acceder al evento.',
      [OrderStatus.PENDING]: 'Estamos procesando tu pago. Te notificaremos cuando esté confirmado.',
      [OrderStatus.CANCELLED]: 'Tu compra ha sido cancelada. Si tienes dudas, contacta soporte.',
      [OrderStatus.REFUNDED]: 'Tu compra ha sido reembolsada. El dinero será devuelto a tu método de pago.'
    };
    
    return descriptions[this.order.status] || 'Contacta soporte para más información.';
  }

  getTicketStatusLabel(status: TicketStatus): string {
    const labels = {
      [TicketStatus.ACTIVE]: 'Activo',
      [TicketStatus.USED]: 'Usado',
      [TicketStatus.CANCELLED]: 'Cancelado',
      [TicketStatus.EXPIRED]: 'Expirado'
    };
    
    return labels[status] || status;
  }

  printTickets() {
    window.print();
  }

  goToTournaments() {
    this.router.navigate(['/tournaments']);
  }

  lookupTickets() {
    this.router.navigate(['/validation/lookup']);
  }

  formatDateTime(dateString: string): string {
    return new Date(dateString).toLocaleString('es-ES');
  }
}
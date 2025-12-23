import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TicketsService } from '../services/tickets.service';
import { TicketStagesService } from '../../ticket-stages/services/ticket-stages.service';
import { TournamentsService } from '../../tournaments/services/tournaments.service';
import { Tournament } from '../../../core/models/tournament.models';
import { TicketSaleStage, StageStatus } from '../../../core/models/ticket-stage.models';
import { CartItem, OrderSummary, TicketOrderRequest } from '../../../core/models/ticket.models';

@Component({
  selector: 'app-ticket-purchase',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container">
      <div class="header">
        <h2>Comprar Tickets</h2>
        <button class="btn btn-outline" (click)="goBack()">
          Volver
        </button>
      </div>

      <div *ngIf="loading" class="loading">
        Cargando...
      </div>

      <div *ngIf="error" class="error">
        {{error}}
      </div>

      <div *ngIf="tournament && !loading" class="purchase-container">
        <!-- Información del Torneo -->
        <div class="tournament-info">
          <h3>{{tournament.name}}</h3>
          <p>{{tournament.description}}</p>
          <div class="tournament-details">
            <span><strong>Fecha:</strong> {{formatDate(tournament.startDateTime)}}</span>
            <span><strong>Categoría:</strong> {{tournament.category?.name}}</span>
            <span><strong>Juego:</strong> {{tournament.gameType?.name}}</span>
          </div>
        </div>

        <!-- Etapas Disponibles -->
        <div class="stages-section">
          <h4>Selecciona tus tickets</h4>
          
          <div *ngIf="availableStages.length === 0" class="no-stages">
            No hay etapas de venta disponibles en este momento
          </div>

          <div *ngFor="let stage of availableStages" class="stage-card">
            <div class="stage-header">
              <h5>{{stage.name}}</h5>
              <div class="stage-price">\${{stage.price}}</div>
            </div>
            
            <p class="stage-description" *ngIf="stage.description">{{stage.description}}</p>
            
            <div class="stage-info">
              <span class="availability">
                {{stage.capacity - stage.soldTickets}} disponibles de {{stage.capacity}}
              </span>
              <span class="dates">
                Válido hasta: {{formatDate(stage.endDate)}}
              </span>
            </div>

            <div class="quantity-selector">
              <label>Cantidad:</label>
              <div class="quantity-controls">
                <button 
                  class="btn btn-sm" 
                  (click)="decreaseQuantity(stage.id)"
                  [disabled]="getCartQuantity(stage.id) <= 0">
                  -
                </button>
                <span class="quantity">{{getCartQuantity(stage.id)}}</span>
                <button 
                  class="btn btn-sm" 
                  (click)="increaseQuantity(stage)"
                  [disabled]="getCartQuantity(stage.id) >= getMaxQuantity(stage)">
                  +
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Carrito -->
        <div *ngIf="cart.length > 0" class="cart-section">
          <h4>Tu pedido</h4>
          
          <div class="cart-items">
            <div *ngFor="let item of cart" class="cart-item">
              <div class="item-info">
                <strong>{{item.stageName}}</strong>
                <span class="item-price">\${{item.price}} x {{item.quantity}}</span>
              </div>
              <div class="item-total">\${{item.price * item.quantity}}</div>
              <button class="btn btn-sm btn-danger" (click)="removeFromCart(item.stageId)">
                ×
              </button>
            </div>
          </div>

          <div class="cart-summary">
            <div class="summary-row">
              <span>Total tickets: {{orderSummary.totalQuantity}}</span>
              <strong>Total: \${{orderSummary.totalAmount}}</strong>
            </div>
          </div>

          <div class="cart-actions">
            <button class="btn btn-outline" (click)="clearCart()">
              Limpiar carrito
            </button>
            <button 
              class="btn btn-primary" 
              (click)="proceedToCheckout()"
              [disabled]="purchasing">
              {{purchasing ? 'Procesando...' : 'Comprar tickets'}}
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .container { max-width: 800px; margin: 0 auto; padding: 20px; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
    .tournament-info { background: white; padding: 20px; border-radius: 8px; margin-bottom: 30px; border: 1px solid #ddd; }
    .tournament-info h3 { margin: 0 0 10px 0; color: #333; }
    .tournament-details { display: flex; gap: 20px; margin-top: 15px; font-size: 14px; color: #666; }
    .stages-section { margin-bottom: 30px; }
    .stages-section h4 { margin-bottom: 20px; }
    .stage-card { background: white; border: 1px solid #ddd; border-radius: 8px; padding: 20px; margin-bottom: 15px; }
    .stage-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
    .stage-header h5 { margin: 0; }
    .stage-price { font-size: 18px; font-weight: bold; color: #2e7d32; }
    .stage-description { color: #666; margin-bottom: 15px; }
    .stage-info { display: flex; justify-content: space-between; margin-bottom: 15px; font-size: 14px; }
    .availability { color: #2e7d32; }
    .dates { color: #666; }
    .quantity-selector { display: flex; align-items: center; gap: 15px; }
    .quantity-controls { display: flex; align-items: center; gap: 10px; }
    .quantity { min-width: 30px; text-align: center; font-weight: bold; }
    .cart-section { background: #f9f9f9; padding: 20px; border-radius: 8px; }
    .cart-section h4 { margin: 0 0 20px 0; }
    .cart-items { margin-bottom: 20px; }
    .cart-item { display: flex; justify-content: space-between; align-items: center; padding: 10px; background: white; border-radius: 4px; margin-bottom: 10px; }
    .item-info { display: flex; flex-direction: column; gap: 5px; }
    .item-price { font-size: 14px; color: #666; }
    .item-total { font-weight: bold; }
    .cart-summary { border-top: 1px solid #ddd; padding-top: 15px; margin-bottom: 20px; }
    .summary-row { display: flex; justify-content: space-between; align-items: center; }
    .cart-actions { display: flex; justify-content: space-between; gap: 15px; }
    .no-stages { text-align: center; padding: 40px; color: #999; }
    .loading, .error { text-align: center; padding: 40px; }
    .error { color: #d32f2f; }
    .btn { padding: 8px 16px; border: 1px solid #ddd; border-radius: 4px; cursor: pointer; background: white; }
    .btn-primary { background: #1976d2; color: white; border-color: #1976d2; }
    .btn-danger { background: #d32f2f; color: white; border-color: #d32f2f; }
    .btn-outline { background: white; color: #666; }
    .btn-sm { padding: 4px 8px; font-size: 12px; }
    .btn:disabled { opacity: 0.5; cursor: not-allowed; }
  `]
})
export class TicketPurchaseComponent implements OnInit {
  tournament: Tournament | null = null;
  stages: TicketSaleStage[] = [];
  availableStages: TicketSaleStage[] = [];
  cart: CartItem[] = [];
  loading = false;
  purchasing = false;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private ticketsService: TicketsService,
    private ticketStagesService: TicketStagesService,
    private tournamentsService: TournamentsService
  ) {}

  ngOnInit() {
    const tournamentId = this.route.snapshot.paramMap.get('tournamentId');
    if (tournamentId) {
      this.loadTournamentData(tournamentId);
    }
  }

  loadTournamentData(tournamentId: string) {
    this.loading = true;
    
    // Load tournament info
    this.tournamentsService.getTournament(tournamentId).subscribe({
      next: (tournament) => {
        this.tournament = tournament;
        // Crear etapas simuladas para demostración
        this.createMockStages();
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Error al cargar el torneo';
        this.loading = false;
        console.error('Error loading tournament:', error);
      }
    });
  }

  createMockStages() {
    if (!this.tournament) return;
    
    // Crear etapas simuladas basadas en si el torneo es pago o gratuito
    if (this.tournament.isPaid) {
      this.stages = [
        {
          id: 1,
          name: 'Early Bird',
          description: 'Precio especial por compra anticipada',
          price: 25.00,
          capacity: 50,
          soldTickets: 15,
          startDate: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString(),
          endDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
          isActive: true,
          tournamentId: Number(this.tournament.id),
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        },
        {
          id: 2,
          name: 'Regular',
          description: 'Precio regular',
          price: 35.00,
          capacity: 100,
          soldTickets: 25,
          startDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
          endDate: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString(),
          isActive: true,
          tournamentId: Number(this.tournament.id),
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        }
      ];
    } else {
      this.stages = [
        {
          id: 1,
          name: 'Entrada Gratuita',
          description: 'Acceso gratuito al torneo',
          price: 0,
          capacity: this.tournament.maxFreeCapacity || 100,
          soldTickets: 20,
          startDate: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString(),
          endDate: new Date(this.tournament.startDateTime).toISOString(),
          isActive: true,
          tournamentId: Number(this.tournament.id),
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString()
        }
      ];
    }
    
    this.availableStages = this.stages.filter(stage => this.isStageAvailable(stage));
  }

  isStageAvailable(stage: TicketSaleStage): boolean {
    const now = new Date();
    const start = new Date(stage.startDate);
    const end = new Date(stage.endDate);
    
    return now >= start && now <= end && stage.soldTickets < stage.capacity;
  }

  getCartQuantity(stageId: number): number {
    const item = this.cart.find(item => item.stageId === stageId);
    return item ? item.quantity : 0;
  }

  getMaxQuantity(stage: TicketSaleStage): number {
    return Math.min(10, stage.capacity - stage.soldTickets); // Max 10 per purchase
  }

  increaseQuantity(stage: TicketSaleStage) {
    const currentQuantity = this.getCartQuantity(stage.id);
    const maxQuantity = this.getMaxQuantity(stage);
    
    if (currentQuantity < maxQuantity) {
      const existingItem = this.cart.find(item => item.stageId === stage.id);
      
      if (existingItem) {
        existingItem.quantity++;
      } else {
        this.cart.push({
          stageId: stage.id,
          stageName: stage.name,
          price: stage.price,
          quantity: 1,
          maxQuantity: maxQuantity
        });
      }
    }
  }

  decreaseQuantity(stageId: number) {
    const itemIndex = this.cart.findIndex(item => item.stageId === stageId);
    
    if (itemIndex !== -1) {
      this.cart[itemIndex].quantity--;
      
      if (this.cart[itemIndex].quantity <= 0) {
        this.cart.splice(itemIndex, 1);
      }
    }
  }

  removeFromCart(stageId: number) {
    const itemIndex = this.cart.findIndex(item => item.stageId === stageId);
    if (itemIndex !== -1) {
      this.cart.splice(itemIndex, 1);
    }
  }

  clearCart() {
    this.cart = [];
  }

  get orderSummary(): OrderSummary {
    return {
      items: this.cart,
      totalQuantity: this.cart.reduce((sum, item) => sum + item.quantity, 0),
      totalAmount: this.cart.reduce((sum, item) => sum + (item.price * item.quantity), 0)
    };
  }

  proceedToCheckout() {
    if (!this.tournament || this.cart.length === 0) return;

    this.purchasing = true;
    
    // Enviar compra al backend
    const purchaseData = {
      tournamentId: this.tournament.id,
      quantity: this.orderSummary.totalQuantity,
      totalAmount: this.orderSummary.totalAmount
    };
    
    fetch('http://localhost:8081/api/simple/tickets/purchase', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(purchaseData)
    })
    .then(response => response.json())
    .then(data => {
      if (data.error) {
        throw new Error(data.error);
      }
      
      // Guardar también en localStorage con códigos de acceso e información del torneo
      const purchasedTickets = JSON.parse(localStorage.getItem('purchasedTickets') || '[]');
      
      this.cart.forEach((item, index) => {
        for (let i = 0; i < item.quantity; i++) {
          purchasedTickets.push({
            id: Date.now() + Math.random(),
            tournamentId: this.tournament!.id,
            tournamentName: this.tournament!.name,
            tournamentDate: this.formatDate(this.tournament!.startDateTime),
            category: this.tournament!.category?.name || 'No disponible',
            gameType: this.tournament!.gameType?.name || 'No disponible',
            stageName: item.stageName,
            quantity: 1,
            price: item.price,
            total: item.price,
            purchaseDate: new Date().toISOString(),
            status: 'ACTIVE',
            accessCode: data.accessCodes[purchasedTickets.length % data.accessCodes.length]
          });
        }
      });
      
      localStorage.setItem('purchasedTickets', JSON.stringify(purchasedTickets));
      
      this.purchasing = false;
      const codes = data.accessCodes.join(', ');
      alert(`¡Compra exitosa! Códigos de acceso: ${codes}`);
      this.clearCart();
      this.router.navigate(['/tickets']);
    })
    .catch(error => {
      this.purchasing = false;
      console.error('Error:', error);
      alert('Error al procesar la compra: ' + error.message);
    });
  }

  goBack() {
    this.router.navigate(['/tournaments']);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('es-ES');
  }
}
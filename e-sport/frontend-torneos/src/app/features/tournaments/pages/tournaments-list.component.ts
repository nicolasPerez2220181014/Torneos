import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TournamentsService } from '../services/tournaments.service';
import { CategoriesService } from '../../categories/services/categories.service';
import { GameTypesService } from '../../game-types/services/game-types.service';
import { Tournament, TournamentFilters, TournamentStatus } from '../../../core/models/tournament.models';
import { Category, GameType } from '../../../core/models/masters.models';
import { PaginatedResponse } from '../../../core/models/api.models';

@Component({
  selector: 'app-tournaments-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container">
      <div class="header">
        <h2>Gestión de Torneos</h2>
        <div class="header-actions">
          <button class="btn btn-outline" (click)="showMyTickets()">
            Ver Mis Tickets ({{getMyTicketsCount()}})
          </button>
          <button class="btn btn-primary" (click)="navigateToCreate()">
            Crear Torneo
          </button>
        </div>
      </div>

      <!-- Filtros -->
      <div class="filters">
        <div class="filter-row">
          <input 
            type="text" 
            placeholder="Buscar por nombre..." 
            [(ngModel)]="filters.name"
            (input)="onFilterChange()"
            class="form-control">
          
          <select [(ngModel)]="filters.status" (change)="onFilterChange()" class="form-control">
            <option value="">Todos los estados</option>
            <option *ngFor="let status of statusOptions" [value]="status.value">
              {{status.label}}
            </option>
          </select>

          <select [(ngModel)]="filters.categoryId" (change)="onFilterChange()" class="form-control">
            <option value="">Todas las categorías</option>
            <option *ngFor="let category of categories" [value]="category.id">
              {{category.name}}
            </option>
          </select>

          <select [(ngModel)]="filters.gameTypeId" (change)="onFilterChange()" class="form-control">
            <option value="">Todos los juegos</option>
            <option *ngFor="let gameType of gameTypes" [value]="gameType.id">
              {{gameType.name}}
            </option>
          </select>
        </div>
      </div>

      <!-- Loading -->
      <div *ngIf="loading" class="loading">
        Cargando torneos...
      </div>

      <!-- Error -->
      <div *ngIf="error" class="error">
        {{error}}
      </div>

      <!-- Lista de torneos -->
      <div *ngIf="!loading && !error" class="tournaments-grid">
        <div *ngFor="let tournament of tournaments" class="tournament-card">
          <div class="tournament-header">
            <h3>{{tournament.name}}</h3>
            <span class="status" [class]="'status-' + tournament.status.toLowerCase()">
              {{getStatusLabel(tournament.status)}}
            </span>
          </div>
          
          <p class="description">{{tournament.description}}</p>
          
          <div class="tournament-info">
            <div class="info-item">
              <strong>Categoría:</strong> {{tournament.category?.name || 'N/A'}}
            </div>
            <div class="info-item">
              <strong>Juego:</strong> {{tournament.gameType?.name || 'N/A'}}
            </div>
            <div class="info-item">
              <strong>Participantes:</strong> {{tournament.maxParticipants}}
            </div>
            <div class="info-item">
              <strong>Inicio:</strong> {{formatDate(tournament.startDate)}}
            </div>
          </div>

          <div class="tournament-actions">
            <button class="btn btn-sm btn-outline" (click)="viewTournament(tournament.id)">
              Ver
            </button>
            <button class="btn btn-sm btn-outline" (click)="editTournament(tournament.id)">
              Editar
            </button>
            <button class="btn btn-sm btn-success" (click)="buyTickets(tournament.id)">
              Comprar Tickets
            </button>
            <button class="btn btn-sm btn-primary" (click)="watchStream(tournament.id)">
              Ver Stream
            </button>
            <button 
              *ngIf="tournament.status === 'DRAFT'" 
              class="btn btn-sm btn-success" 
              (click)="publishTournament(tournament)">
              Publicar
            </button>
          </div>
        </div>
      </div>

      <!-- Paginación -->
      <div *ngIf="paginationData && paginationData.totalElements > 0" class="pagination">
        <button 
          class="btn btn-outline" 
          [disabled]="paginationData.first"
          (click)="goToPage(paginationData.number - 1)">
          Anterior
        </button>
        
        <span class="page-info">
          Página {{paginationData.number + 1}} de {{paginationData.totalPages}}
          ({{paginationData.totalElements}} total)
        </span>
        
        <button 
          class="btn btn-outline" 
          [disabled]="paginationData.last"
          (click)="goToPage(paginationData.number + 1)">
          Siguiente
        </button>
      </div>
    </div>
  `,
  styles: [`
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: var(--sp-xl);
    }
    .header h2 { font-size: 1.4rem; }
    .header-actions { display: flex; gap: var(--sp-sm); }

    .filters { margin-bottom: var(--sp-xl); }
    .filter-row {
      display: grid;
      grid-template-columns: 2fr 1fr 1fr 1fr;
      gap: var(--sp-sm);
    }

    .tournaments-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
      gap: var(--sp-lg);
      margin-bottom: var(--sp-xl);
    }

    .tournament-card {
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: var(--radius-lg);
      padding: var(--sp-xl);
      transition: all 0.2s var(--ease);
    }
    .tournament-card:hover {
      border-color: var(--border-hover);
      box-shadow: var(--shadow-md);
    }

    .tournament-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: var(--sp-md);
    }
    .tournament-header h3 {
      margin: 0;
      flex: 1;
      font-size: 1.05rem;
      color: var(--text-primary);
    }

    .description {
      color: var(--text-muted);
      font-size: 0.8rem;
      margin-bottom: var(--sp-lg);
      line-height: 1.5;
    }

    .tournament-info {
      margin-bottom: var(--sp-lg);
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: var(--sp-sm);
    }
    .info-item {
      font-size: 0.8rem;
      color: var(--text-secondary);
    }
    .info-item strong {
      color: var(--text-muted);
      font-weight: 500;
    }

    .tournament-actions {
      display: flex;
      flex-wrap: wrap;
      gap: var(--sp-sm);
      padding-top: var(--sp-lg);
      border-top: 1px solid var(--border);
    }

    @media (max-width: 768px) {
      .filter-row { grid-template-columns: 1fr; }
      .tournaments-grid { grid-template-columns: 1fr; }
      .header { flex-direction: column; gap: var(--sp-md); align-items: flex-start; }
    }
  `]
})
export class TournamentsListComponent implements OnInit {
  tournaments: Tournament[] = [];
  categories: Category[] = [];
  gameTypes: GameType[] = [];
  loading = false;
  error: string | null = null;
  paginationData: any = null;

  filters: TournamentFilters = {};
  currentPage = 0;
  pageSize = 10;

  statusOptions = [
    { value: TournamentStatus.DRAFT, label: 'Borrador' },
    { value: TournamentStatus.PUBLISHED, label: 'Publicado' },
    { value: TournamentStatus.REGISTRATION_OPEN, label: 'Inscripciones Abiertas' },
    { value: TournamentStatus.REGISTRATION_CLOSED, label: 'Inscripciones Cerradas' },
    { value: TournamentStatus.IN_PROGRESS, label: 'En Progreso' },
    { value: TournamentStatus.COMPLETED, label: 'Completado' },
    { value: TournamentStatus.CANCELLED, label: 'Cancelado' }
  ];

  constructor(
    private tournamentsService: TournamentsService,
    private categoriesService: CategoriesService,
    private gameTypesService: GameTypesService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadMasterData();
    this.loadTournaments();
  }

  loadMasterData() {
    this.categoriesService.getCategories().subscribe({
      next: (categories) => this.categories = categories,
      error: (error) => console.error('Error loading categories:', error)
    });

    this.gameTypesService.getGameTypes().subscribe({
      next: (gameTypes) => this.gameTypes = gameTypes,
      error: (error) => console.error('Error loading game types:', error)
    });
  }

  loadTournaments() {
    this.loading = true;
    this.error = null;

    this.tournamentsService.getTournaments(this.currentPage, this.pageSize, this.filters).subscribe({
      next: (response: PaginatedResponse<Tournament>) => {
        this.tournaments = response.content;
        this.paginationData = {
          content: response.content,
          totalElements: response.pageable.totalElements,
          totalPages: response.pageable.totalPages,
          size: response.pageable.pageSize,
          number: response.pageable.pageNumber,
          first: response.pageable.pageNumber === 0,
          last: response.pageable.pageNumber === response.pageable.totalPages - 1,
          numberOfElements: response.content.length,
          empty: response.content.length === 0
        };
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Error al cargar los torneos';
        this.loading = false;
        console.error('Error loading tournaments:', error);
      }
    });
  }

  onFilterChange() {
    this.currentPage = 0;
    this.loadTournaments();
  }

  goToPage(page: number) {
    this.currentPage = page;
    this.loadTournaments();
  }

  navigateToCreate() {
    this.router.navigate(['/tournaments/create']);
  }

  viewTournament(id: number | string) {
    this.router.navigate(['/tournaments', id]);
  }

  editTournament(id: number | string) {
    this.router.navigate(['/tournaments', id, 'edit']);
  }

  buyTickets(id: number | string) {
    this.router.navigate(['/tickets/purchase', id]);
  }

  watchStream(id: number | string) {
    this.router.navigate(['/streams/watch', id]);
  }

  publishTournament(tournament: Tournament) {
    if (confirm(`¿Está seguro de publicar el torneo "${tournament.name}"?`)) {
      this.tournamentsService.publishTournament(tournament.id).subscribe({
        next: () => {
          alert('Torneo publicado exitosamente');
          this.loadTournaments();
        },
        error: (error) => {
          console.error('Error publishing tournament:', error);
          const errorMessage = error.error?.message || error.message || 'Error al publicar el torneo';
          alert(`Error: ${errorMessage}`);
        }
      });
    }
  }

  getStatusLabel(status: TournamentStatus): string {
    const option = this.statusOptions.find(opt => opt.value === status);
    return option ? option.label : status;
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('es-ES');
  }

  getMyTicketsCount(): number {
    const tickets = localStorage.getItem('purchasedTickets');
    return tickets ? JSON.parse(tickets).length : 0;
  }

  showMyTickets() {
    this.router.navigate(['/tickets']);
  }
}